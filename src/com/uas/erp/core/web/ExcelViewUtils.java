package com.uas.erp.core.web;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.View;

import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.ContextUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.interceptor.InterceptorUtil;
import com.uas.erp.core.web.DocumentConfig.MixedKey;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.BenchDao;
import com.uas.erp.dao.common.DataListComboDao;
import com.uas.erp.dao.common.DataListDao;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.dao.common.HrJobDao;
import com.uas.erp.dao.common.PowerDao;
import com.uas.erp.model.Bench.BenchScene;
import com.uas.erp.model.Bench.BenchSceneGrid;
import com.uas.erp.model.DataList;
import com.uas.erp.model.DataListCombo;
import com.uas.erp.model.DataListDetail;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.Employee;
import com.uas.erp.model.LimitFields;
import com.uas.erp.model.Master;
import com.uas.erp.service.bench.BenchService;
import com.uas.erp.service.common.DataListService;

public class ExcelViewUtils {

	@Autowired
	private BaseDao baseDao;
	
	public static View getViewByDataList(String caller, String condition, String fileName, String fields, boolean self, Integer lg,
			Employee employee,boolean _jobemployee) {
		DataListDao dataListDao = (DataListDao) ContextUtil.getBean("dataListDao");
		BaseDao baseDao = (BaseDao) ContextUtil.getBean("baseDao");
		PowerDao powerDao = (PowerDao) ContextUtil.getBean("powerDao");
		boolean bool = baseDao.checkIf("DataListDetailEmps", "dde_caller='" + caller + "' and dde_emid=" + employee.getEm_id());
		DataList dataList = bool ? dataListDao.getDataListByEm(caller, employee) : dataListDao.getDataList(caller, employee.getEm_master());
		DocumentConfig config = getConfig(caller, fields, employee, dataList, false);
		String con = null;
		if(_jobemployee){
			con = powerDao.getRecorderCondition(condition, dataList.getDl_entryfield(), employee, true);
		}else if(self){
			con = parseSelfCondition(dataList, employee);
		}else{
			con = dataList.getDl_condition();
		}
		//String con = self ? parseSelfCondition(dataList, employee) : dataList.getDl_condition();
		condition = (con == null || "".equals(con)) ? condition : ("(" + con + ")" + ((condition == null || "".equals(condition)) ? ""
				: " AND (" + condition + ")"));
		
		DataListService dataListService = (DataListService) ContextUtil.getBean("dataListService");
		condition = dataListService.appendCondition(dataList, condition, employee);
		
		String executable = dataList.getSearchSql(condition, dataList.getDl_orderby(), 1, Constant.EXCEL_MAX_SIZE);
		if(_jobemployee){
			executable = dataListDao.getSqlWithJobEmployee(employee) + executable;
		}
		
		// 按导出数据量判断采用哪张方式
		boolean isBigExcel = false;
		if (lg == null) {
			String sql = dataList.getSearchSql(condition);
			if(_jobemployee){
				sql = dataListDao.getSqlWithJobEmployee(employee) + sql;
			}
			int size = baseDao.getCount(sql);
			isBigExcel = size > Constant.EXCEL_LG_SIZE;
		} else {
			isBigExcel = lg == Constant.YES;
		}
		if (isBigExcel)
			return new DefaultBigExcelView(config, executable, fileName);
		return new DefaultExcelView(config, executable, fileName);
	}

	private static String parseSelfCondition(DataList dataList, Employee employee ) {
		String condition = dataList.getDl_condition();
		String f = dataList.getDl_entryfield();
		if (StringUtils.hasText(f)) {
			Object emVal = employee.getEm_id(); // recorderfield默认与em_id对应
			if (f.endsWith("@C")) { // recorderfield与em_code对应
				f = f.substring(0, f.lastIndexOf("@C"));
				emVal = employee.getEm_code();
			} else if (f.endsWith("@N")) { // recorderfield与em_name对应
				f = f.substring(0, f.lastIndexOf("@N"));
				emVal = employee.getEm_name();
			}
			if (StringUtils.hasText(condition)) {
				condition += " AND ";
			} else
				condition = "";
			condition += f + "='" + emVal + "'";
		}
		return condition;
	}
	
	/**
	 * @param caller
	 * @param fields
	 * @param employee
	 * @param dataList
	 * @param alias
	 *            字段别名
	 * @return
	 */
	private static DocumentConfig getConfig(String caller, String fields, Employee employee, DataList dataList, boolean alias) {
		DocumentConfig config = new DocumentConfig();
		DataListComboDao dataListComboDao = (DataListComboDao) ContextUtil.getBean("dataListComboDao");
		List<DataListCombo> combos = dataListComboDao.getComboxsByCaller(caller, SpObserver.getSp());
		List<DataListCombo> aliaCombos = new ArrayList<DataListCombo>();
		List<DataListDetail> details = dataList.getDataListDetails();
		Set<String> limits = null;
		if (!"admin".equals(employee.getEm_type())) {
			HrJobDao hrjobDao = (HrJobDao) ContextUtil.getBean("hrjobDao");
			List<LimitFields> limitFields = hrjobDao.getLimitFieldsByType(caller, dataList.getDl_relative(), 2,
					employee.getEm_defaulthsid(), employee.getEm_master());
			if (!CollectionUtil.isEmpty(limitFields)) {
				limits = new HashSet<String>();
				for (LimitFields field : limitFields) {
					limits.add(field.getLf_field());
				}
			}
		}
		String[] ff = null;
		Master master = employee.getCurrentMaster();
		if (master != null && master.getMa_type() != 3 && master.getMa_soncode() != null) {
			config.getHeaders().put("CURRENTMASTER", "账套");
			config.getWidths().put("CURRENTMASTER", 80);
			config.getTypes().put("CURRENTMASTER", "");
			config.getLocks().put("CURRENTMASTER", true);
		}
		String[] customFields = null;
		if (fields != null && !"".equals(fields)) {
			customFields = fields.split(",");
		}
		int index = 0;
		for (DataListDetail detail : details) {
			if ((detail.getDld_width() != 0 || detail.getDld_flex() != 0)) {
				ff = detail.getDld_field().split(" ");
				String field = ff[ff.length - 1];// 别名
				if ((customFields != null && !StringUtil.isInArray(customFields, field)) || (limits != null && limits.contains(field))) {
					continue;
				}
				if (alias) {
					field = String.valueOf((char) (48 + ++index));
					for (DataListCombo combo : combos) {
						if (combo.getDlc_fieldname().equals(detail.getDld_field())) {
							aliaCombos.add(new DataListCombo(combo, field));
						}
					}
				}
				config.getHeaders().put(field, detail.getDld_caption());
				config.getWidths().put(field, detail.getDld_width());
				config.getTypes().put(field, getType(detail.getDld_fieldtype()));
				config.getLocks().put(field, false);
			}
		}
		parseCombo(config, alias ? aliaCombos : combos);
		return config;
	}

	private static String getType(String type) {
		String format = "";
		if ("N".equals(type)) {
			format = "0";
		} else if ("F".equals(type)) {
			format = "0.00";
		} else if ("D".equals(type)) {
			format = "yyyy-m-d";
		} else if ("DT".equals(type)) {
			format = "yyyy-m-d hh:MM:ss";
		} else if (type.matches("^F\\d{1}$")) {
			int length = Integer.parseInt(type.replace("F", ""));
			format = "0.";
			for (int i = 0; i < length; i++) {
				format += "0";
			}
		} else if ("C".equals(type)) {
			format = Constant.TYPE_COMBO;
		}else if("yncolumn".equals(type) || "ynnvcolumn".equals(type)){
			format = Constant.TYPE_YN;
		}
		return format;
	}
	
	public static View getViewBySceneGrid(String caller, String condition, String fileName, String fields, Boolean self,Integer lg, Employee employee, Boolean noControl) {
		BenchDao benchDao = (BenchDao) ContextUtil.getBean("benchDao");
		BaseDao baseDao = (BaseDao) ContextUtil.getBean("baseDao");
		BenchService benchService = (BenchService) ContextUtil.getBean("benchService");
		BenchScene benchScene =benchDao.getBenchScene(caller,employee.getEm_master());
		DocumentConfig config = getConfig(caller, fields, employee, benchScene, false);
		Boolean jobemployee = false;
		if (!noControl) {
			jobemployee = benchService.isJobEmployee(benchScene.getBs_caller(), employee);
			condition = benchService.appendPowerCondition(benchScene, employee, condition, self, jobemployee, true);
		}
		condition = benchService.getCondition(benchScene, employee, condition);
		String executable = benchScene.getSearchSql(condition, benchScene.getBs_orderby(), 1, Constant.EXCEL_MAX_SIZE);
		if(jobemployee){
			executable = benchDao.getSqlWithJobEmployee(employee) + executable;
		}
		// 按导出数据量判断采用哪张方式
		boolean isBigExcel = false;
		if (lg == null) {
			String countSql = benchScene.getSearchSql(condition);
			if(jobemployee){
				countSql = benchDao.getSqlWithJobEmployee(employee) + countSql;
			}
			int size = baseDao.getCount(countSql);
			isBigExcel = size > Constant.EXCEL_LG_SIZE;
		} else {
			isBigExcel = lg == Constant.YES;
		}
		if (isBigExcel)
			return new DefaultBigExcelView(config, executable, fileName);
		return new DefaultExcelView(config, executable, fileName);
	}
	
	/**
	 * @param caller
	 * @param fields
	 * @param employee
	 * @param dataList
	 * @param alias
	 *            字段别名
	 * @return
	 */
	private static DocumentConfig getConfig(String caller, String fields, Employee employee, BenchScene benchScene, boolean alias) {
		DocumentConfig config = new DocumentConfig();
		DataListComboDao dataListComboDao = (DataListComboDao) ContextUtil.getBean("dataListComboDao");
		List<DataListCombo> combos = dataListComboDao.getComboxsByCaller(caller, SpObserver.getSp());
		List<DataListCombo> aliaCombos = new ArrayList<DataListCombo>();
		List<BenchSceneGrid> details = benchScene.getBenchSceneGrids();
		Set<String> limits = null;
		/*if (!"admin".equals(employee.getEm_type())) {
			HrJobDao hrjobDao = (HrJobDao) ContextUtil.getBean("hrjobDao");
			List<LimitFields> limitFields = hrjobDao.getLimitFieldsByType(caller, benchScene.getBs_relative(), 2,
					employee.getEm_defaulthsid(), employee.getEm_master());
			if (!CollectionUtil.isEmpty(limitFields)) {
				limits = new HashSet<String>();
				for (LimitFields field : limitFields) {
					limits.add(field.getLf_field());
				}
			}
		}*/
		String[] ff = null;
		Master master = employee.getCurrentMaster();
		if (master != null && master.getMa_type() != 3 && master.getMa_soncode() != null) {
			config.getHeaders().put("CURRENTMASTER", "账套");
			config.getWidths().put("CURRENTMASTER", 80);
			config.getTypes().put("CURRENTMASTER", "");
			config.getLocks().put("CURRENTMASTER", true);
		}
		String[] customFields = null;
		if (fields != null && !"".equals(fields)) {
			customFields = fields.split(",");
		}
		int index = 0;
		for (BenchSceneGrid detail : details) {
			if (detail.getSg_width() != 0) {
				ff = detail.getSg_field().split(" ");
				String field = ff[ff.length - 1];// 别名
				if ((customFields != null && !StringUtil.isInArray(customFields, field)) || (limits != null && limits.contains(field))) {
					continue;
				}
				if (alias) {
					field = String.valueOf((char) (48 + ++index));
					for (DataListCombo combo : combos) {
						if (combo.getDlc_fieldname().equals(detail.getSg_field())) {
							aliaCombos.add(new DataListCombo(combo, field));
						}
					}
				}
				config.getHeaders().put(field, detail.getSg_text());
				config.getWidths().put(field, detail.getSg_width());
				config.getTypes().put(field, getType(detail.getSg_field()));
				config.getLocks().put(field, false);
			}
		}
		parseCombo(config, alias ? aliaCombos : combos);
		return config;
	}

	public static View getViewByDetailGrid(String caller, String condition, String fileName, String fields, Integer lg, Employee employee) {
		DetailGridDao detailGridDao = (DetailGridDao) ContextUtil.getBean("detailGridDao");
		BaseDao baseDao = (BaseDao) ContextUtil.getBean("baseDao");
		List<DetailGrid> detailGrids = detailGridDao.getDetailGridsByCaller(caller, employee.getEm_master());
		DocumentConfig config = getConfig(caller, fields, employee, detailGrids, false);
		Object[] objs = baseDao.getFieldsDataByCondition("Form", "fo_detailtable,fo_detailcondition,fo_detailgridorderby", "fo_caller='"
				+ caller + "'");
		String table = detailGrids.get(0).getDg_table();
		if (objs != null) {// 优先用Form的配置
			if (objs[0] != null)
				table = objs[0].toString();
			if (objs[1] != null) {
				if ("".equals(condition)) {
					condition = objs[1].toString();
				} else {
					int index = condition.toLowerCase().indexOf("order by");
					if (index > -1) {
						condition = condition.substring(0, index) + " AND " + objs[1] + " " + condition.substring(index);
					} else {
						condition += " AND " + objs[1];
					}
				}
			}
			if (condition.toLowerCase().indexOf("order by") == -1 && objs[2] != null
					&& objs[2].toString().toLowerCase().indexOf("order by") > -1) {
				condition += " " + objs[2];
			}
		}
		String executable = SqlUtil.getQuerySqlByDetailGrid(detailGrids, table, condition, employee, 1, Constant.EXCEL_MAX_SIZE);
		// 按导出数据量判断采用哪张方式
		boolean isBigExcel = false;
		if (lg == null) {
			int size = baseDao.getCountByCondition(table, condition);
			isBigExcel = size > Constant.EXCEL_LG_SIZE;
		} else {
			isBigExcel = lg == Constant.YES;
		}
		if (isBigExcel)
			return new DefaultBigExcelView(config, executable, fileName);
		return new DefaultExcelView(config, executable, fileName);
	}

	/**
	 * @param caller
	 * @param fields
	 * @param employee
	 * @param detailGrids
	 * @param alias
	 *            字段别名
	 * @return
	 */
	private static DocumentConfig getConfig(String caller, String fields, Employee employee, List<DetailGrid> detailGrids, boolean alias) {
		DocumentConfig config = new DocumentConfig();
		DataListComboDao dataListComboDao = (DataListComboDao) ContextUtil.getBean("dataListComboDao");
		List<DataListCombo> combos = dataListComboDao.getComboxsByCaller(caller, SpObserver.getSp());
		List<DataListCombo> aliaCombos = new ArrayList<DataListCombo>();
		Master master = employee.getCurrentMaster();
		if (master != null && master.getMa_type() != 3 && master.getMa_soncode() != null) {
			config.getHeaders().put("CURRENTMASTER", "账套");
			config.getWidths().put("CURRENTMASTER", 80);
			config.getTypes().put("CURRENTMASTER", "");
			config.getLocks().put("CURRENTMASTER", true);
		}
		Set<String> limits = null;
		if (!"admin".equals(employee.getEm_type())) {
			HrJobDao hrjobDao = (HrJobDao) ContextUtil.getBean("hrjobDao");
			List<LimitFields> limitFields = hrjobDao.getLimitFieldsByType(caller, null, 0, employee.getEm_defaulthsid(),
					employee.getEm_master());
			if (!CollectionUtil.isEmpty(limitFields)) {
				limits = new HashSet<String>();
				for (LimitFields field : limitFields) {
					limits.add(field.getLf_field());
				}
			}
		}
		String[] ff = null;
		String[] customFields = null;
		if (fields != null && !"".equals(fields)) {
			customFields = fields.split(",");
		}
		int index = 0;
		for (DetailGrid grid : detailGrids) {
			if (grid.getDg_width() != 0) {
				ff = grid.getDg_field().split(" ");
				String field = ff[ff.length - 1];// 别名
				if ((customFields != null && !StringUtil.isInArray(customFields, field)) || (limits != null && limits.contains(field))) {
					continue;
				}
				if (alias) {
					field = String.valueOf((char) (48 + ++index));
					for (DataListCombo combo : combos) {
						if (combo.getDlc_fieldname().equals(grid.getDg_field())) {
							aliaCombos.add(new DataListCombo(combo, field));
						}
					}
				}
				//明细表下载，必填字段
				if(grid.getDg_logictype()!=null&&grid.getDg_logictype().equals("necessaryField")){					
					config.getNecessary().put(field, true);
				}
				config.getHeaders().put(field, grid.getDg_caption());
				config.getWidths().put(field, grid.getDg_width());
				config.getTypes().put(field, getType(grid));
				config.getLocks().put(field, grid.getDg_locked() == 1);
				config.getSummary().put(field, "sum".equals(grid.getDg_summarytype()));
			}
		}
		parseCombo(config, alias ? aliaCombos : combos);
		return config;
	}

	private static void parseCombo(DocumentConfig config, List<DataListCombo> combos) {
		for (DataListCombo combo : combos) {
			config.getCombos().put(new MixedKey(new Object[] { combo.getDlc_fieldname(), combo.getDlc_display() }),
					StringUtil.nvl(combo.getDlc_value(), ""));
		}
	}

	private static String getType(DetailGrid detailGrid) {
		String type = detailGrid.getDg_type();
		String format = "";
		if ("numbercolumn".equals(type)) {
			format = "0";
		} else if ("floatcolumn".equals(type)) {
			format = "0.00";
		} else if ("datecolumn".equals(type)) {
			format = "yyyy-m-d";
		} else if ("datetimecolumn".equals(type)) {
			format = "yyyy-m-d hh:MM:ss";
		} else if (type.matches("^floatcolumn\\d{1}$")) {
			format = "0.";
			int length = Integer.parseInt(type.replace("floatcolumn", ""));
			for (int i = 0; i < length; i++) {
				format += "0";
			}
		} else if ("yncolumn".equals(type) || "ynnvcolumn".equals(type)) {
			format = Constant.TYPE_YN;
		} else if ("combo".equals(type))
			format = Constant.TYPE_COMBO;
		return format;
	}

	public static DocumentConfig getConfig(List<Map<String, Object>> columns) {
		DocumentConfig config = new DocumentConfig();
		Object cm = null;
		for (Map<String, Object> m : columns) {
			cm = m.get("dataIndex");
			if (cm != null) {
				//明细表下载，必填字段
				if(m.get("logic")!=null&&m.get("logic").toString().equals("necessaryField")){
					config.getNecessary().put(cm.toString(), true);
				}
				config.getHeaders().put(cm.toString(), m.get("text").toString());
				config.getWidths().put(cm.toString(), Integer.parseInt(String.valueOf(m.get("width"))));
				config.getLocks().put(cm.toString(), "true".equals(String.valueOf(m.get("locked"))));
				config.getSummary().put(cm.toString(), "true".equals(String.valueOf(m.get("summary"))));
				if ("numbercolumn".equals(String.valueOf(m.get("xtype")))) {
					String format = String.valueOf(m.get("format"));
					if (format != null && !format.equals("null")) {
						if (format.indexOf("0.") > -1) {
							config.getTypes().put(cm.toString(), format.substring(format.indexOf("0.")));
						} else {
							config.getTypes().put(cm.toString(), "0");
						}
					} else
						config.getTypes().put(cm.toString(), "NUMBER");
				} else if ("yncolumn".equals(String.valueOf(m.get("xtype"))) || "ynnvcolumn".equals(String.valueOf(m.get("xtype")))) {
					config.getTypes().put(cm.toString(), Constant.TYPE_YN);
				} else {
					config.getTypes().put(cm.toString(), "");
				}
			}
		}
		return config;
	}

	public static DocumentConfig getConfig(List<Map<String, Object>> columns, String docTitle) {
		DocumentConfig config = getConfig(columns);
		config.setTitle(docTitle);
		return config;
	}

	public static View getView(String caller, String type, String condition, String fileName, String fields, boolean self, Integer lg,
			Employee employee, boolean _jobemployee, HttpServletRequest request) {
		if ("datalist".equals(type)) {
			return getViewByDataList(caller, condition, fileName, fields, self, lg, employee,_jobemployee);
		} else if ("detailgrid".equals(type)) {
			return getViewByDetailGrid(caller, condition, fileName, fields, lg, employee);
		}else if("scenegrid".equals(type)){
			boolean noControl = InterceptorUtil.noControl(request, employee);
			return getViewBySceneGrid(caller, condition, fileName, fields, self, lg, employee, noControl);
		}
		return null;
	}

	public static View getView(DocumentConfig config, List<Map<String, Object>> datas, String fileName, Employee employee) {
		if (datas.size() > Constant.EXCEL_LG_SIZE)
			return new DefaultBigExcelDataView(config, datas, fileName);
		return new DefaultExcelDataView(config, datas, fileName);
	}

	public static View getView(List<Map<String, Object>> columns, List<Map<String, Object>> datas, String fileName, Employee employee) {
		return getView(getConfig(columns), datas, fileName, employee);
	}

	/**
	 * @param columns
	 * @param datas
	 * @param fileName
	 *            文件名
	 * @param docTitle
	 *            文件里面的大标题
	 * @param employee
	 * @return
	 */
	public static View getView(List<Map<String, Object>> columns, List<Map<String, Object>> datas, String fileName, String docTitle,
			Employee employee) {
		return getView(getConfig(columns, docTitle), datas, fileName, employee);
	}

}
