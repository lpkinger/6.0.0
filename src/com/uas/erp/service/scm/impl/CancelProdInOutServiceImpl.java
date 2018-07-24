package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;





import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.CancelProdInOutDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DataListComboDao;
import com.uas.erp.dao.common.DbfindSetGridDao;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.dao.common.FormDao;
import com.uas.erp.dao.common.HrJobDao;
import com.uas.erp.model.DBFindSetGrid;
import com.uas.erp.model.DataListCombo;
import com.uas.erp.model.Dbfind;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Form;
import com.uas.erp.model.FormDetail;
import com.uas.erp.model.FormItems;
import com.uas.erp.model.FormPanel;
import com.uas.erp.model.GridColumns;
import com.uas.erp.model.GridFields;
import com.uas.erp.model.GridPanel;
import com.uas.erp.model.LimitFields;
import com.uas.erp.model.Master;
import com.uas.erp.service.scm.CancelProdInOutService;



@Service("cancelProdInOutService")
public class CancelProdInOutServiceImpl implements CancelProdInOutService{
	@Autowired
	//private BaseDao baseDao;
	private CancelProdInOutDao cancelProdInOutDao;
	@Autowired
	private FormDao formDao;
	@Autowired
	private DataListComboDao dataListComboDao;
	@Autowired
	private HrJobDao hrJobDao;
	@Autowired
	private DetailGridDao detailGridDao;
	@Autowired
	private DbfindSetGridDao dbfindSetGridDao;
	
	@Override
	public FormPanel getFormItemsByCaller(String caller, String condition, String language, Employee employee, boolean isCloud) {
		FormPanel formPanel = new FormPanel();
		String master = employee != null ? employee.getEm_master() : SpObserver.getSp();
		Form form = null;
		List<DataListCombo> combos = null;
		if (isCloud) {
			SpObserver.putSp(Constant.UAS_CLOUD);
			form = formDao.getForm(caller, Constant.UAS_CLOUD);
			combos = dataListComboDao.getComboxsByCaller(caller, Constant.UAS_CLOUD);
			SpObserver.putSp(master);
		} else {
			form = formDao.getForm(caller, master);
			combos = dataListComboDao.getComboxsByCaller(caller, master);
		}
		List<DataListCombo> cos = new ArrayList<DataListCombo>();
		// 针对通用变更单界面 保证下拉框统一 采用原始下拉框的配置
		if (caller != null && caller.endsWith("$Change")) {
			cos.addAll(combos);
			cos.addAll(dataListComboDao.getComboxsByCaller(caller.substring(0, caller.indexOf("$Change")), master));
		} else
			cos = combos;
		List<FormDetail> formDetails = form.getFormDetails();
		List<FormItems> items = new ArrayList<FormItems>();
		List<LimitFields> limits = new ArrayList<LimitFields>();
		// 权限控制字段
		if (!"admin".equals(employee.getEm_type())) {
			limits = hrJobDao.getLimitFieldsByType(caller, null, 1, employee.getEm_defaulthsid(), master);
		}
		formPanel.setLimitFields(limits);
		Map<String, List<FormDetail>> map = new HashMap<String, List<FormDetail>>();// form分组
		for (FormDetail formDetail : formDetails) {
			if (formDetail.getFd_group() != null && !formDetail.getFd_group().trim().equals("")) {
				if (!map.containsKey(formDetail.getFd_group())) {
					List<FormDetail> list = new ArrayList<FormDetail>();
					list.add(formDetail);
					map.put(formDetail.getFd_group(), list);
				} else {
					List<FormDetail> list = map.get(formDetail.getFd_group());
					list.add(formDetail);
					map.put(formDetail.getFd_group(), list);
				}
			}
		}
		int count = 1;
		if (map.size() > 1) {// 分组必须大于1
			// 分组先排序
			Iterator<String> iterator = map.keySet().iterator();
			Map<String, Integer> groups = new HashMap<String, Integer>();
			while (iterator.hasNext()) {
				String group = iterator.next();
				List<FormDetail> list = map.get(group);
				int min = 999999999;
				for (FormDetail formDetail : list) {
					min = Math.min(min, formDetail.getFd_detno());// 分组顺序采取最用当前组的最小detno
				}
				groups.put(group, min);
			}
			List<String> glist = BaseUtil.mapSort(groups, 0);
			for (String str : glist) {
				List<FormDetail> list = map.get(str);
				items.add(new FormItems(count, str));
				for (FormDetail formDetail : list) {
					if (formDetail.getFd_type() != null) {
						if (formDetail.getFd_type().equals("MT")) {// 对于合并型的字段
							if (formDetail.getFd_logictype() != null && !formDetail.getFd_logictype().equals("")) {
								items.add(new FormItems(count, str, formDetail, cos));
							}
						} else {
							items.add(new FormItems(count, str, formDetail, cos));
						}
					}
				}
				count++;
			}
		}
		if (count == 1) {// 说明没有分组
			for (FormDetail formDetail : formDetails) {
				if (formDetail.getFd_type() != null) {
					if (formDetail.getFd_type().equals("MT")) {
						if (formDetail.getFd_logictype() != null && !formDetail.getFd_logictype().equals("")) {
							items.add(new FormItems(count, null, formDetail, combos));
						}
					} else {
						items.add(new FormItems(count, null, formDetail, combos));
					}
				}
			}
		}
		formPanel.setItems(items);
		if (condition.equals("")) {
			// 单表添加界面的condition是空的
			formPanel.setButtons(form.getFo_button4add());
		} else {
			formPanel.setButtons(form.getFo_button4rw());
		}
		formPanel.setFo_id(form.getFo_id());
		formPanel.setFo_keyField(form.getFo_keyfield());// 主表keyfield
		formPanel.setFo_detailMainKeyField(form.getFo_detailmainkeyfield());// 从表对用主表keyfield的field
		formPanel.setTablename(form.getFo_table());
		formPanel.setFo_mainpercent(form.getFo_mainpercent());
		formPanel.setFo_detailpercent(form.getFo_detailpercent());
		formPanel.setCodeField(form.getFo_codefield());
		formPanel.setTitle(form.getFo_title());
		formPanel.setFo_detailGridOrderBy(form.getFo_detailgridorderby());
		if (form.getFo_statusfield() != null)
			formPanel.setStatusField(form.getFo_statusfield());
		if (form.getFo_statuscodefield() != null)
			formPanel.setStatuscodeField(form.getFo_statuscodefield());
		if (form.getFo_dealurl() != null) {
			formPanel.setDealUrl(form.getFo_dealurl());
		}
		if (form.getFo_detailkeyfield() != null) {
			formPanel.setFo_detailkeyfield(form.getFo_detailkeyfield());
		}
		return formPanel;
	}
	
	@Override
	public Map<String, Object> getFormData(String caller, String condition, boolean isCloud) {
		Form form = null;
		String master = SpObserver.getSp();
		if (isCloud) {
			SpObserver.putSp(Constant.UAS_CLOUD);
			form = formDao.getForm(caller, Constant.UAS_CLOUD);
			SpObserver.putSp(master);
		} else{
			form = formDao.getForm(caller, master);
		}
		return cancelProdInOutDao.getFormData(form, condition);
	}
	
	
	@Override
	public GridPanel getGridPanelByCaller(String caller, String condition, Integer start, Integer end, Integer _m, boolean isCloud,String _copyConf , Employee employee) {
		String _master = employee != null ? employee.getEm_master() : SpObserver.getSp();
		GridPanel gridPanel = new GridPanel();
		List<DetailGrid> detailGrids = null;
		List<DataListCombo> combos = null;
		List<DBFindSetGrid> dbFindSetGrids = null;
		if (isCloud) {
			SpObserver.putSp(Constant.UAS_CLOUD);
			detailGrids = detailGridDao.getDetailGridsByCaller(caller, Constant.UAS_CLOUD);
			combos = dataListComboDao.getComboxsByCaller(caller, Constant.UAS_CLOUD);
			dbFindSetGrids = dbfindSetGridDao.getDbFindSetGridsByCaller(caller);
			SpObserver.putSp(_master);
		} else {
			detailGrids = detailGridDao.getDetailGridsByCaller(caller, _master);
			combos = dataListComboDao.getComboxsByCaller(caller, _master);
			dbFindSetGrids = dbfindSetGridDao.getDbFindSetGridsByCaller(caller);
		}
		if (detailGrids != null && detailGrids.size() > 0) {
			List<GridFields> fields = new ArrayList<GridFields>();// grid //
																	// store的字段fields
			List<GridColumns> columns = new ArrayList<GridColumns>();// grid的列信息columns
			Master master = employee.getCurrentMaster();
			// 多帐套，加帐套名称
			if (master != null && master.getMa_type() != 3 && master.getMa_soncode() != null) {
				if (_m != null && 0 == _m) {
					master = null;
				} else {
					fields.add(new GridFields("CURRENTMASTER"));
					columns.add(new GridColumns("CURRENTMASTER", "帐套", 80, true));
				}
			}
			List<LimitFields> limits = new ArrayList<LimitFields>();
			if (!"admin".equals(employee.getEm_type())) {
				limits = hrJobDao.getLimitFieldsByType(caller, null, 0, employee.getEm_defaulthsid(), employee.getEm_master());
			}
			gridPanel.setLimits(limits);// 权限控制字段
			//int i = 0;
			for (DetailGrid grid : detailGrids) {
				// 从数据库表detailgrid的数据，通过自定义的构造器，转化为extjs识别的fields格式，详情可见GridFields的构造函数
				fields.add(new GridFields(grid));
				columns.add(new GridColumns(grid, combos));
			}
			gridPanel.setGridColumns(columns);
			gridPanel.setGridFields(fields);

			List<Dbfind> dbfinds = new ArrayList<Dbfind>();
			for (DBFindSetGrid dbFindSetGrid : dbFindSetGrids) {
				dbfinds.add(new Dbfind(dbFindSetGrid));
			}
			gridPanel.setDbfinds(dbfinds);
			if (!condition.equals("")) {
				gridPanel.setDataString(getDataStringByDetailGrid(detailGrids, condition, start, end));
			}else if(_copyConf!=null &&!"".equals(_copyConf)){//复制界面获取来源单据数据并进行替换
				Map<Object, Object> copyConf = BaseUtil.parseFormStoreToMap(_copyConf);
				Object[] obs=cancelProdInOutDao.getFieldsDataByCondition("form", new String[]{"fo_detailkeyfield","fo_detailmainkeyfield"}, "fo_caller='"+caller+"'");
				Object detailmainkeyfield=cancelProdInOutDao.getFieldDataByCondition("detailgrid","dg_field", "dg_caller='"+caller+"' and dg_logictype='mainField'");
				if(detailmainkeyfield==null || "".equals(detailmainkeyfield)){
					detailmainkeyfield = obs[1];
				}
				List<Map<String, Object>> maps = cancelProdInOutDao.getDetailGridData(detailGrids, detailmainkeyfield+"="+copyConf.get("keyValue"), employee, start, end);
				if(obs!=null){
					for (Map<String, Object> map : maps) {
						map.put(obs[0].toString(), "");//清空明细主键
						map.put(obs[1].toString(), "");//清空明细外键
					}
				}
				SqlRowList rs = cancelProdInOutDao.queryForRowSet("select cc_field,cc_copyvalue from　COPYCONFIGS where cc_findkind='detail' and cc_caller=?", caller);
				while (rs.next()) {
					for (Map<String, Object> map : maps) {
						if("null".equals(rs.getString("cc_copyvalue"))){
							map.put(rs.getString("cc_field"),"");
						}else{
							map.put(rs.getString("cc_field"), rs.getString("cc_copyvalue"));
						}
					}
				}
				gridPanel.setDataString(BaseUtil.parseGridStore2Str(maps));
			}
		}
		return gridPanel;
	}

	public String getDataStringByDetailGrid(List<DetailGrid> detailGrids, String condition, Integer start, Integer end) {
		Employee employee = SystemSession.getUser();
		List<Map<String, Object>> maps = cancelProdInOutDao.getDetailGridData(detailGrids, condition, employee, start, end);
		return BaseUtil.parseGridStore2Str(maps);
	}
}
