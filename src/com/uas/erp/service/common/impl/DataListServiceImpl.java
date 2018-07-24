package com.uas.erp.service.common.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.JSONUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.DataListComboDao;
import com.uas.erp.dao.common.DataListDao;
import com.uas.erp.dao.common.HrJobDao;
import com.uas.erp.dao.common.PowerDao;
import com.uas.erp.model.DataList;
import com.uas.erp.model.DataListCombo;
import com.uas.erp.model.DataListDetail;
import com.uas.erp.model.Employee;
import com.uas.erp.model.GridColumns;
import com.uas.erp.model.GridFields;
import com.uas.erp.model.GridPanel;
import com.uas.erp.model.LimitFields;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.DataListService;

@Service("dataListService")
public class DataListServiceImpl implements DataListService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private DataListDao dataListDao;
	@Autowired
	private DataListComboDao dataListComboDao;
	@Autowired
	private HrJobDao hrJobDao;
	@Autowired
	private PowerDao powerDao;

	@Override
	public GridPanel getDataListGridByCaller(String caller, String condition, int page, int pageSize, String orderby, Boolean _self,
			Integer _f, boolean fromHeader, boolean isCloud, boolean _jobemployee) {
		Employee employee = SystemSession.getUser();
		String language = SystemSession.getLang();
		boolean bool = baseDao.checkIf("DataListDetailEmps", "dde_caller='" + caller + "' and dde_emid=" + employee.getEm_id());
		DataList dataList = null;
		System.out.println(bool);
		if (isCloud) {
			SpObserver.putSp(Constant.UAS_CLOUD);
			dataList = dataListDao.getDataList(caller, Constant.UAS_CLOUD);
			SpObserver.putSp(employee.getEm_master());

		} else
			dataList = bool ? dataListDao.getDataListByEm(caller, employee) : dataListDao.getDataList(caller, employee.getEm_master());
		List<DataListDetail> details = dataList.getDataListDetails();
		List<DataListCombo> combos = dataListComboDao.getComboxsByCaller(caller, employee.getEm_master());
		GridPanel gridPanel = new GridPanel();
		GridColumns column = null;
		List<GridFields> fields = new ArrayList<GridFields>();// grid
		List<GridColumns> columns = new ArrayList<GridColumns>();// grid的列信息columns
		List<GridColumns> basecolumns = new ArrayList<GridColumns>();// grid的所有可用列信息columns
		Object[] datalist_config = baseDao.getFieldsDataByCondition("datalistconfig left join datalistconfig$emp on id_=id$emp",
				"name_,filterjson_,id_", "caller_='" + caller + "' and emcode$emp='" + employee.getEm_code() + "'");
		if (datalist_config == null) {
			datalist_config = baseDao.getFieldsDataByCondition("datalistconfig", "name_,filterjson_,id_", "caller_='" + caller
					+ "' and isNorm_=-1 and isdefault_=-1");
		}

		Master master = employee.getCurrentMaster();
		// 多帐套，加帐套名称
		if (master != null && master.getMa_type() != 3 && master.getMa_soncode() != null) {
			fields.add(new GridFields("CURRENTMASTER"));
			columns.add(new GridColumns("CURRENTMASTER", "帐套", 80));
		}
		if ("Periods!Now".equals(caller) || "Periods".equals(caller)) {
			baseDao.execute("update periods set pe_nowmonth=(select pd_detno from (select pd_code,min(pd_detno) pd_detno from PeriodsDetail where pd_status=0 group by pd_code) where pd_code=pe_code)");
		}
		if (bool) {
			for (DataListDetail detail : details) {
				fields.add(new GridFields(detail));
				if (datalist_config != null && datalist_config[1] != null) {
					String name_ = datalist_config[0].toString();
					List<Map<Object, Object>> filterlist = JSONUtil.toMapList(datalist_config[1].toString());
					int id_ = datalist_config[2] == null ? 0 : Integer.parseInt(datalist_config[2].toString());
					column = new GridColumns(detail, combos, language, name_, filterlist, id_);
				} else {
					column = new GridColumns(detail, combos, language);
				}
				if (detail.getDde_width() != null) {
					columns.add(column);
				}
				basecolumns.add(column);
			}
		} else {
			for (DataListDetail detail : details) {
				fields.add(new GridFields(detail));
				if (datalist_config != null && datalist_config[1] != null) {
					String name_ = datalist_config[0].toString();
					List<Map<Object, Object>> filterlist = JSONUtil.toMapList(datalist_config[1].toString());
					int id_ = datalist_config[2] == null ? 0 : Integer.parseInt(datalist_config[2].toString());
					column = new GridColumns(detail, combos, language, name_, filterlist, id_);
				} else {
					column = new GridColumns(detail, combos, language);
				}
				basecolumns.add(column);
			}
		}
		if (datalist_config != null && !fromHeader) {
			List<Map<Object, Object>> filterlist = JSONUtil.toMapList(datalist_config[1].toString());
			if (condition != null && condition.trim().length() > 0) {
				condition += " AND ";
			}
			String defaultfiltercondition = appendFilterCondition(filterlist);
			condition += defaultfiltercondition;
			gridPanel.setDefaultFilterCondition(defaultfiltercondition);

		}
		columns = columns.size() < 2 ? basecolumns : columns;
		gridPanel.setDataString(BaseUtil.parseGridStore2Str(getDataListData(caller, dataList, condition, employee, page, pageSize, orderby,
				_self, false, _f, _jobemployee)));
		gridPanel.setSummarydata(getSummaryData(dataList, condition, employee, _self, _jobemployee));
		gridPanel.setGridColumns(columns);
		gridPanel.setBaseColumns(basecolumns);
		gridPanel.setGridFields(fields);
		gridPanel.setKeyField(dataList.getDl_keyfield());
		gridPanel.setUrl(dataList.getDl_lockpage());
		gridPanel.setRelative(dataList.getDl_relative());
		gridPanel.setPfField(dataList.getDl_pffield());
		gridPanel.setVastbutton(dataList.getDl_fixedcondition());
		if (!"admin".equals(employee.getEm_type())) {
			List<LimitFields> listDefault = hrJobDao.getLimitFieldsByType(caller, dataList.getDl_relative(), 2,
					employee.getEm_defaulthsid(), employee.getEm_master());
			List<Object> jo_ids = baseDao.getFieldDatasByCondition("EMPSJOBS", "JOB_ID", "EMP_ID=" + employee.getEm_id());
			List<LimitFields> empLimits = null;
			if (listDefault.size() > 0) {
				for (Object jo_id : jo_ids) {
					empLimits = hrJobDao.getLimitFieldsByType(caller, dataList.getDl_relative(), 2, Integer.parseInt(jo_id.toString()),
							employee.getEm_master());
					if (empLimits.size() == 0) {
						listDefault.removeAll(listDefault);
						break;
					} else {
						removeDifferentLimitFields(listDefault, empLimits);
						if (listDefault.size() == 0)
							break;
					}
				}
			}
			gridPanel.setLimits(listDefault);
		}
		return gridPanel;

	}

	private void removeDifferentLimitFields(List<LimitFields> limitFields, List<LimitFields> empLimits) {
		List<LimitFields> list = new ArrayList<LimitFields>();
		String field = "";
		String empLimitField = "";
		boolean same;
		if (limitFields.size() > 0) {
			for (LimitFields limitField : limitFields) {
				same = false;
				field = limitField.getLf_field();
				if (field != null) {
					for (LimitFields empLimit : empLimits) {
						empLimitField = empLimit.getLf_field();
						if (field.equals(empLimitField)) {
							same = true;
							break;
						}
					}
					if (!same) {
						list.add(limitField);
					}
				}
			}
			limitFields.removeAll(list);
		}
	}

	private List<Map<String, Object>> getDataListData(String caller, DataList dataList, String condition, Employee employee, int page,
			int pageSize, String orderBy, Boolean _self, boolean _alia, Integer _f, boolean _jobemployee) {
		if ("1=2".equals(condition))
			return new ArrayList<Map<String, Object>>();
		/** 添加其他约束条件 */
		condition = appendCondition(dataList, condition, employee);
		/** 添加权限条件，看自己/看所有 */
		condition = appendPowerCondition(dataList, condition, employee, _self, _jobemployee);
		return dataListDao.getDataListData(dataList, condition, employee, page, pageSize, _f, _alia, orderBy, _jobemployee);
	}

	private List<Map<String, Object>> getSummaryData(DataList datalist, String condition, Employee employee, Boolean _self,
			boolean _jobemployee) {
		/** 添加其他约束条件 */
		condition = appendCondition(datalist, condition, employee);
		/** 添加权限条件，看自己/看所有 */
		condition = appendPowerCondition(datalist, condition, employee, _self, _jobemployee);
		return dataListDao.getSummaryData(datalist, condition, _jobemployee);
	}

	@Override
	public Map<String, Object> getDataListData(String caller, String condition, int page, int pageSize, String orderBy, Boolean _self,
			boolean _alia, Integer _f, boolean isCloud, boolean _jobemployee) {
		Employee employee = SystemSession.getUser();
		Map<String, Object> map = new HashMap<String, Object>();
		boolean bool = baseDao.checkIf("DataListDetailEmps", "dde_caller='" + caller + "' and dde_emid=" + employee.getEm_id());
		DataList dataList = null;
		if (isCloud) {
			SpObserver.putSp(Constant.UAS_CLOUD);
			dataList = dataListDao.getDataList(caller, Constant.UAS_CLOUD);
			SpObserver.putSp(employee.getEm_master());

		} else
			dataList = bool ? dataListDao.getDataListByEm(caller, employee) : dataListDao.getDataList(caller, employee.getEm_master());
		map.put("data", getDataListData(caller, dataList, condition, employee, page, pageSize, orderBy, _self, _alia, _f, _jobemployee));
		map.put("summarydata", getSummaryData(dataList, condition, employee, _self, _jobemployee));
		return map;
	}

	@Override
	public int getCountByCaller(String caller, String condition, Boolean _self, boolean fromHeader, boolean isCloud, boolean _jobemployee) {
		DataList dataList = null;
		Employee employee = SystemSession.getUser();
		if (isCloud) {
			SpObserver.putSp(Constant.UAS_CLOUD);
			dataList = dataListDao.getDataList(caller, Constant.UAS_CLOUD);
			SpObserver.putSp(employee.getEm_master());

		} else
			dataList = dataListDao.getDataList(caller, employee.getEm_master());
		/** 添加其他约束条件 */
		condition = appendCondition(dataList, condition, employee);
		/** 添加权限条件，看自己/看所有 */
		condition = appendPowerCondition(dataList, condition, employee, _self, _jobemployee);
		Object[] datalist_config = baseDao.getFieldsDataByCondition("datalistconfig left join datalistconfig$emp on id_=ID$EMP",
				"name_,filterjson_,id_", "caller_='" + caller + "' and EMCODE$EMP='" + employee.getEm_code() + "'");
		if (datalist_config == null) {
			datalist_config = baseDao.getFieldsDataByCondition("datalistconfig", "name_,filterjson_,id_", "caller_='" + caller
					+ "' and isNorm_=-1 and isdefault_=-1");
		}
		if (datalist_config != null && !fromHeader) {
			List<Map<Object, Object>> filterlist = JSONUtil.toMapList(datalist_config[1].toString());
			if (condition != null && condition.trim().length() > 0) {
				condition += " AND ";
			}
			condition += appendFilterCondition(filterlist);
		}
		/**
		 * dataList.getSql换为dataList.getSearchSql
		 * 
		 * @date 2016-6-20 16:39:14
		 */

		String sql = dataList.getSearchSql(condition, employee);
		if (_jobemployee) {
			sql = dataListDao.getSqlWithJobEmployee(employee) + sql;
		}

		return baseDao.getCount(sql);
	}

	@Override
	public void vastDelete(String language, Employee employee, String caller, int[] id) {
		// 整批删除
		Object[] objs = baseDao.getFieldsDataByCondition("datalist", new String[] { "dl_tablename", "dl_keyfield" }, "dl_caller='" + caller
				+ "'");
		if (objs[0] != null && objs[1] != null) {
			for (int key : id) {
				baseDao.deleteById(objs[0].toString().split(" ")[0], (String) objs[1], key);
			}
		}
		// 记录日志
	}

	@Override
	public void vastSubmit(String language, Employee employee, String caller, int[] id) {
		// 整批提交
		Object[] objs = baseDao.getFieldsDataByCondition("datalist", new String[] { "dl_tablename", "dl_keyfield", "dl_statusfield",
				"dl_statuscodefield" }, "dl_caller='" + caller + "'");
		for (int key : id) {
			baseDao.updateByCondition((String) objs[0],
					objs[3] + "='COMMITED'," + objs[2] + "='" + BaseUtil.getLocalMessage("COMMITED", language) + "'", objs[1] + "=" + key);
		}
		// 记录日志
	}

	@Override
	public void vastAudit(String language, Employee employee, String caller, int[] id) {
		// 整批审核
		Object[] objs = baseDao.getFieldsDataByCondition("datalist", new String[] { "dl_tablename", "dl_keyfield", "dl_statusfield",
				"dl_statuscodefield" }, "dl_caller='" + caller + "'");
		for (int key : id) {
			baseDao.updateByCondition((String) objs[0],
					objs[3] + "='AUDITED'," + objs[2] + "='" + BaseUtil.getLocalMessage("AUDITED", language) + "'", objs[1] + "=" + key);
		}
		// 记录日志
	}

	@Override
	public void vastSend(String language, Employee employee, String caller, int[] id) {
		// 整批发出

		// 记录日志
	}

	@Override
	public void vastFreeze(String language, Employee employee, String caller, int[] id) {
		// 整批冻结
		Object[] objs = baseDao.getFieldsDataByCondition("datalist", new String[] { "dl_tablename", "dl_keyfield", "dl_statusfield",
				"dl_statuscodefield" }, "dl_caller='" + caller + "'");
		for (int key : id) {
			baseDao.updateByCondition((String) objs[0],
					objs[3] + "='FREEZE'," + objs[2] + "='" + BaseUtil.getLocalMessage("FREEZE", language) + "'", objs[1] + "=" + key);
		}
		// 记录日志
	}

	@Override
	public void vastResStart(String language, Employee employee, String caller, int[] id) {
		// 整批重启
		Object[] objs = baseDao.getFieldsDataByCondition("datalist", new String[] { "dl_tablename", "dl_keyfield", "dl_statusfield",
				"dl_statuscodefield" }, "dl_caller='" + caller + "'");
		for (int key : id) {
			baseDao.updateByCondition(String.valueOf(objs[0]).split(" ")[0],
					objs[3] + "='ENTERING'," + objs[2] + "='" + BaseUtil.getLocalMessage("ENTERING", language) + "'", objs[1] + "=" + key);
		}
		// 记录日志
	}

	@Override
	public void vastClose(String language, Employee employee, String caller, int[] id) {
		// 整批结案
		Object[] objs = baseDao.getFieldsDataByCondition("datalist", new String[] { "dl_tablename", "dl_keyfield", "dl_statusfield",
				"dl_statuscodefield" }, "dl_caller='" + caller + "'");
		for (int key : id) {
			baseDao.updateByCondition((String) objs[0],
					objs[3] + "='FINISH'," + objs[2] + "='" + BaseUtil.getLocalMessage("FINISH", language) + "'", objs[1] + "=" + key);
		}
		// 记录日志
	}

	@Override
	public void vastPost(String language, Employee employee, String caller, int[] id) {
		// 整批抛转

		// 记录日志
	}

	@Override
	public void vastCancel(String language, Employee employee, String caller, String data) {
		// 整批作废
		Object[] objs = baseDao.getFieldsDataByCondition("datalist", new String[] { "dl_tablename", "dl_keyfield", "dl_statusfield",
				"dl_statuscodefield" }, "dl_caller='" + caller + "'");
		String tab = String.valueOf(objs[0]);
		String keyF = String.valueOf(objs[1]);
		String sF = String.valueOf(objs[2]);
		String scF = String.valueOf(objs[3]);
		if (tab != null && keyF != null && scF != null) {// 必须配置了状态码字段、表名和主键字段
			List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
			StringBuffer sb = new StringBuffer();
			if (sF != null) {// 如果存在状态字段
				sb.append(sF);
				sb.append("='NULLIFIED',");
			}
			sb.append(scF);
			sb.append("='");
			sb.append(BaseUtil.getLocalMessage("NULLIFIED", language));
			sb.append("'");
			for (Map<Object, Object> m : maps) {
				baseDao.updateByCondition((String) objs[0], sb.toString(), keyF + "=" + m.get(keyF));
			}
		} else {
			BaseUtil.showError("配置不详，无法执行作废操作.");
		}
		// 记录日志
	}

	@Override
	public void vastResPost(String language, Employee employee, String caller, int[] id) {
		// 整批作废
		Object[] objs = baseDao.getFieldsDataByCondition("datalist", new String[] { "dl_tablename", "dl_keyfield", "dl_statusfield",
				"dl_statuscodefield" }, "dl_caller='" + caller + "'");
		for (int key : id) {
			baseDao.updateByCondition((String) objs[0],
					objs[3] + "='POSTED'," + objs[2] + "='" + BaseUtil.getLocalMessage("POSTED", language) + "'", objs[1] + "=" + key);
		}
		// 记录日志
	}

	/**
	 * 批量保存
	 */
	@Override
	public void vastSave(String language, Employee employee, String caller, String data) {
		Object[] objs = baseDao.getFieldsDataByCondition("DataList", new String[] { "dl_tablename", "dl_keyfield" }, "dl_caller='" + caller
				+ "'");
		if (objs != null) {
			String table = objs[0].toString();
			table = table.split(" ")[0];
			List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
			List<String> sqls = SqlUtil.getUpdateSqlbyGridStore(maps, table, objs[1].toString());
			baseDao.execute(sqls);
		} else {
			BaseUtil.showError("配置不正确");
		}
	}

	@Override
	public void AgreeToPrice(String language, Employee employee, String caller, int[] id) {
		StringBuffer sb = new StringBuffer();
		for (int key : id) {
			// 先判断该供应商营业执照号是否在供应商中存在，如果没有判断是否勾选自动生成供应商资料参数，没有勾选则生成新的供应商引入并提示
			boolean isExist = baseDao.checkIf("vendor", "ve_auditstatus='已审核' and ve_uu=(select id_venduu from inquirydetail where id_id="
					+ key + ")");
			if (!isExist) {
				if (baseDao.isDBSetting("Inquiry", "VendorOrBefore")) {
					int ve_id = baseDao.getSeqId("Vendor_SEQ");
					String ve_code = baseDao.sGetMaxNumber("Vendor", 2);
					baseDao.execute("insert into Vendor(ve_id,ve_code,ve_name,ve_uu,ve_auditstatus,ve_auditstatuscode,ve_webserver,ve_remark) select "
							+ ve_id
							+ ", '"
							+ ve_code
							+ "',id_vendname,id_venduu,'已审核','AUDITED',id_vendyyzzh,'来源于B2B' from inquirydetail where id_id = " + key + "");
					sb.append("温馨提示：您刚刚采纳了非系统内的供应商的报价，系统已为您自动生成已审核的供应商，单号：<a href=\"javascript:openUrl('jsps/scm/purchase/vendor.jsp?formCondition=ve_idIS"
							+ ve_id + "')\">" + ve_code + "</a>&nbsp;<hr>");
					baseDao.execute("update InquiryDetail set id_vendcode='" + ve_code + "' where id_id=" + key + "");
				} else {
					int ve_id = baseDao.getSeqId("PreVendor_SEQ");
					String ve_code = baseDao.sGetMaxNumber("PreVendor", 2);
					baseDao.execute("insert into PreVendor(ve_id,ve_code,ve_name,ve_uu,ve_auditstatus,ve_auditstatuscode,ve_webserver,ve_currency,ve_remark) select "
							+ ve_id
							+ ", '"
							+ ve_code
							+ "',id_vendname,id_venduu,'在录入','ENTERING',id_vendyyzzh,'RMB','来源于B2B' from inquirydetail where id_id = "
							+ key + "");
					sb.append("温馨提示：您刚刚采纳了非系统内的供应商的报价，系统已为您自动生成在录入的供应商引进单，单号："
							+ "<a href=\"javascript:openUrl('jsps/scm/purchase/preVendor.jsp?formCondition=ve_idIS" + ve_id
							+ "&gridCondition=NULLIS" + ve_id + "')\">" + ve_code + "</a>&nbsp;");
				}
			}
			baseDao.updateByCondition("InquiryDetail", "id_isagreed=-1", "id_id=" + key);
		}
		BaseUtil.showErrorOnSuccess(sb.toString());
	}

	@Override
	public void NotAgreeToPrice(String language, Employee employee, String caller, int[] id) {
		for (int key : id) {
			baseDao.updateByCondition("InquiryDetail", "id_isagreed=0", "id_id=" + key);
		}
	}

	@Override
	public void saveTemplate(String caller, String desc, String fields, Employee employee) {
		baseDao.execute("insert into datatemplate(dt_caller,dt_desc,dt_fields,dt_date,dt_man) values(?,?,?,sysdate,?)", caller, desc,
				fields, employee.getEm_name());
	}

	@Override
	public void AgreeAllToPrice(String language, Employee employee, String caller, int id) {
		baseDao.updateByCondition("InquiryDetail", "id_isagreed=-1", "id_inid=" + id + " and nvl(id_price,0)>0");
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@CacheEvict(value = "datalistEm", allEntries = true)
	public void saveEmpsDataListDetails(String caller, String data, Employee employee) {
		int dlid = baseDao.getFieldValue("DataList", "dl_id", "dl_caller='" + caller + "'", Integer.class);
		baseDao.deleteByCondition("DATALISTDETAILEMPS", "dde_dlid=" + dlid + " and dde_emid=" + employee.getEm_id());
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		int detno = 1;
		for (Map<Object, Object> map : maps) {
			map.put("dde_detno", detno);
			map.put("dde_dlid", dlid);
			map.put("dde_emid", employee.getEm_id());
			map.put("dde_caller", caller);
			detno++;
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(maps, "DATALISTDETAILEMPS"));

	}

	@Override
	@CacheEvict(value = "datalistEm", allEntries = true)
	public void resetEmpsDataListDetails(String caller) {
		baseDao.deleteByCondition("datalistdetailemps", "dde_caller='" + caller + "' and dde_emid=" + SystemSession.getUser().getEm_id());
	}

	@Override
	public String appendCondition(DataList dataList, String condition, Employee employee) {
		// 人事资料关联控制
		try {
			String conditionstr = dataListDao.getRelativesettings(dataList.getDl_caller(), "datalist", employee.getEm_id());
			if ((condition == null || "".equals(condition)) && conditionstr != null)
				condition = conditionstr;
			else
				condition += conditionstr != null ? " AND " + conditionstr : "";
		} catch (Exception e) {
		}
		// 设置的约束关系
		String limitcondition = baseDao.getLimitCondition(dataList.getDl_tablename(), employee.getEm_id());
		if ((condition == null || "".equals(condition)) && !"".equals(limitcondition))
			condition = limitcondition;
		else
			condition += !"".equals(limitcondition) ? (" AND " + limitcondition) : "";
		return condition;
	}

	/**
	 * 批量确认设置每个模块的当前期间
	 */
	@Override
	public void vastConfirmPeriods(String caller, String data) {
		Object[] objs = baseDao.getFieldsDataByCondition("DataList", new String[] { "dl_tablename", "dl_keyfield" }, "dl_caller='" + caller
				+ "'");
		if (objs != null) {
			List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
			if (maps.size() > 0) {
				for (Map<Object, Object> map : maps) {
					if (map.containsKey("pe_id")) {
						int peid = Integer.parseInt(map.get("pe_id").toString());
						if (peid > 0) {
							Object[] name = baseDao.getFieldsDataByCondition("Periods", new String[] { "pe_detail", "pe_code" }, "pe_id="
									+ peid);
							Object now = map.get("pe_nowmonth");
							if (Integer.parseInt(now.toString()) > DateUtil.getYearmonth()) {
								BaseUtil.showError("期间不能大于系统当前日期所在期间！");
							}
							if (name != null && name[1] != null) {
								baseDao.execute("update PeriodsDetail set pd_status=99 where pd_code='" + name[1] + "' and pd_detno < "
										+ now);
								baseDao.execute("update PeriodsDetail set pd_status=0 where pd_code='" + name[1] + "' and pd_detno >= "
										+ now);
								baseDao.execute("update periods set pe_nowmonth=(select pd_detno from (select pd_code,min(pd_detno) pd_detno from PeriodsDetail where pd_status=0 group by pd_code) where pd_code=pe_code)");
								baseDao.logger.others("设置[" + name[0] + "]期间为[" + now + "]", "确认设置成功", caller, "pe_id", peid);
							}
						}
					}
				}
			} else {
				BaseUtil.showError("没有修改期间设置！");
			}
		} else {
			BaseUtil.showError("配置不正确");
		}
	}

	/**
	 * 获取所有下拉框
	 */
	@Override
	public Map<String, Object> getComboDatalist(String condition, int page, int start, int limit, String sort) {
		Map<String, Object> map = new HashMap<String, Object>();
		String orderby = " order by ";
		if ("".equals(condition) || condition == null) {
			condition = "1=1";
		}
		if ("".equals(sort) || sort == null) {
			orderby += "id desc";
		} else {
			List<Map<Object, Object>> sortlist = BaseUtil.parseGridStoreToMaps(sort);
			for (Map<Object, Object> s : sortlist) {
				orderby += "upper(" + s.get("property") + ") " + s.get("direction") + ",";
			}
			orderby = orderby.substring(0, orderby.length() - 1);
		}

		int end = page * limit;

		String sql = "select * from (select a.*,rownum rn from (select id,DLC_CALLER caller,DLC_FIELDNAME fieldname,title,caption,using,"
				+ "usetype from (select max(dlc_id) id ,DLC_FIELDNAME,DLC_CALLER from datalistcombo where DLC_CALLER is not null and "
				+ "DLC_FIELDNAME is not null group by DLC_FIELDNAME,DLC_CALLER) left join (select dg_caller caller,dg_field field,"
				+ "fo_title title,dg_caption caption,case dg_type when 'combo' then -1 else 0 end  using,'GRID' usetype from detailgrid "
				+ "left join form on dg_caller = fo_caller union select fo_caller caller,fd_field,fo_title title,fd_caption caption,"
				+ "case fd_type when 'C' then -1 when 'EC' then -1 else 0 end  using,'FORM' usetype  from  formdetail inner join form on "
				+ "fd_foid=fo_id) on DLC_CALLER=caller and DLC_FIELDNAME = field where " + condition + orderby + ") a) where rn<=" + end
				+ " and rn >" + start;

		map.put("count",
				baseDao.getCount("select count(1) from (select id,DLC_CALLER,DLC_FIELDNAME,title,caption,using,usetype from "
						+ "(select max(dlc_id) id ,DLC_FIELDNAME,DLC_CALLER from datalistcombo where DLC_CALLER is not null and DLC_FIELDNAME "
						+ "is not null group by DLC_FIELDNAME,DLC_CALLER) left join (select dg_caller caller,dg_field field,fo_title title,"
						+ "dg_caption caption,case dg_type when 'combo' then -1 else 0 end  using,'GRID' usetype from detailgrid  left join form "
						+ "on dg_caller = fo_caller union select fo_caller caller,fd_field,fo_title title,fd_caption caption,case fd_type "
						+ "when 'C' then -1 when 'EC' then -1 else 0 end  using,'FORM' usetype  from  formdetail inner join form on fd_foid=fo_id) "
						+ "on DLC_CALLER=caller and DLC_FIELDNAME = field) where " + condition));
		map.put("data", baseDao.getJdbcTemplate().queryForList(sql));

		return map;
	}

	@Override
	public void vastConfirmFirstPeriods(String caller, String data) {
		Object[] objs = baseDao.getFieldsDataByCondition("DataList", new String[] { "dl_tablename", "dl_keyfield" }, "dl_caller='" + caller
				+ "'");
		if (objs != null) {
			List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
			if (maps.size() > 0) {
				int count = 0;
				StringBuffer sb = new StringBuffer();
				for (Map<Object, Object> map : maps) {
					if (map.containsKey("pe_id")) {
						int peid = Integer.parseInt(map.get("pe_id").toString());
						if (peid > 0) {
							Object[] name = baseDao.getFieldsDataByCondition("Periods", new String[] { "pe_detail", "pe_code" }, "pe_id="
									+ peid);
							if (name[1] != null && StringUtil.hasText(map.get("pe_firstday"))) {
								String code = name[1].toString();
								int first = Integer.parseInt(map.get("pe_firstday").toString());
								if ("MONTH-A".equals(code)) {
									count = baseDao.getCount("select count(1) from CATEMONTH where cm_yearmonth<" + first);
									if (count > 0) {
										sb.append("总账会计系统已经存在初始化期间[" + first + "]之前的数据，不能修改！！");
									}
								} else if ("MONTH-B".equals(code)) {
									count = baseDao.getCount("select count(1) from ALMONTH where am_yearmonth<" + first);
									if (count > 0) {
										sb.append("票据资金系统已经存在初始化期间[" + first + "]之前的数据，不能修改！！");
									}
								} else if ("MONTH-C".equals(code)) {
									count = baseDao.getCount("select count(1) from CUSTMONTH where cm_yearmonth<" + first);
									if (count > 0) {
										sb.append("应收账款系统已经存在初始化期间[" + first + "]之前的数据，不能修改！！");
									}
								} else if ("MONTH-F".equals(code)) {
									count = baseDao.getCount("select count(1) from AssetsDepreciation where to_char(de_date,'yyyymm')<"
											+ first);
									if (count > 0) {
										sb.append("固定资产系统已经存在初始化期间[" + first + "]之前的数据，不能修改！！");
									}
								} else if ("MONTH-O".equals(code)) {
									count = baseDao.getCount("select count(1) from PROJECTCOST where pc_yearmonth<" + first);
									if (count > 0) {
										sb.append("项目成本系统已经存在初始化期间[" + first + "]之前的数据，不能修改！！");
									}
								} else if ("MONTH-P".equals(code)) {
									count = baseDao.getCount("select count(1) from PRODUCTWHMONTH where pwm_yearmonth<" + first);
									if (count > 0) {
										sb.append("库存管理系统已经存在初始化期间[" + first + "]之前的数据，不能修改！！");
									}
								} else if ("MONTH-T".equals(code)) {
									count = baseDao.getCount("select count(1) from COSTDETAIL where cd_yearmonth<" + first);
									if (count > 0) {
										sb.append("成本核算系统已经存在初始化期间[" + first + "]之前的数据，不能修改！！");
									}
								} else if ("MONTH-V".equals(code)) {
									count = baseDao.getCount("select count(1) from VENDMONTH where vm_yearmonth<" + first);
									if (count > 0) {
										sb.append("应付账款系统已经存在初始化期间[" + first + "]之前的数据，不能修改！！");
									}
								}
								if (sb.length() == 0) {
									baseDao.execute("update Periods set pe_firstday=" + first + ",pe_volead='"
											+ StringUtil.nvl(map.get("pe_volead"), "") + "' where pe_id=" + peid);
									baseDao.logger.others("设置[" + name[0] + "]初始化期间为[" + first + "]", "设置初始化期间成功", caller, "pe_id", peid);
								}
							}
							if (sb.length() > 0) {
								BaseUtil.showError(sb.toString());
							}
						}
					}
				}
			} else {
				BaseUtil.showError("没有修改期间设置！");
			}
		} else {
			BaseUtil.showError("配置不正确");
		}
	}

	public String appendPowerCondition(DataList dataList, String condition, Employee employee, Boolean _self, boolean jobEmployee) {
		if (jobEmployee || (_self != null && _self)) {
			String f = dataList.getDl_entryfield();

			if (f != null && f.trim().length() > 0) {
				condition = powerDao.getRecorderCondition(condition, f, employee, jobEmployee);
			} else {
				BaseUtil.showErrorOnSuccess("无法限制列表权限!原因: 未配置录入人字段.");
			}
		}
		return condition;
	};

	@Override
	public GridPanel getColumns(String caller, boolean isCloud) {
		Employee employee = SystemSession.getUser();
		String language = SystemSession.getLang();
		boolean bool = baseDao.checkIf("DataListDetailEmps", "dde_caller='" + caller + "' and dde_emid=" + employee.getEm_id());
		DataList dataList = null;
		if (isCloud) {
			SpObserver.putSp(Constant.UAS_CLOUD);
			dataList = dataListDao.getDataList(caller, Constant.UAS_CLOUD);
			SpObserver.putSp(employee.getEm_master());

		} else
			dataList = bool ? dataListDao.getDataListByEm(caller, employee) : dataListDao.getDataList(caller, employee.getEm_master());
		List<DataListDetail> details = dataList.getDataListDetails();
		List<DataListCombo> combos = dataListComboDao.getComboxsByCaller(caller, employee.getEm_master());
		GridPanel gridPanel = new GridPanel();
		GridColumns column = null;
		List<GridFields> fields = new ArrayList<GridFields>();// grid
		List<GridColumns> columns = new ArrayList<GridColumns>();// grid的列信息columns
		List<GridColumns> basecolumns = new ArrayList<GridColumns>();// grid的所有可用列信息columns
		Master master = employee.getCurrentMaster();
		// 多帐套，加帐套名称
		if (master != null && master.getMa_type() != 3 && master.getMa_soncode() != null) {
			fields.add(new GridFields("CURRENTMASTER"));
			columns.add(new GridColumns("CURRENTMASTER", "帐套", 80));
		}
		if (bool) {
			for (DataListDetail detail : details) {
				fields.add(new GridFields(detail));
				column = new GridColumns(detail, combos, language);
				if (detail.getDde_width() != null) {
					columns.add(column);
				}
				basecolumns.add(column);
			}
		} else {
			for (DataListDetail detail : details) {
				fields.add(new GridFields(detail));
				column = new GridColumns(detail, combos, language);
				basecolumns.add(column);
			}
		}
		columns = columns.size() < 2 ? basecolumns : columns;
		gridPanel.setGridColumns(columns);
		return gridPanel;
	}

	@Override
	public List<Map<String, Object>> getDataListFilterName(String caller) {
		Employee employee = SystemSession.getUser();
		String em_code = employee.getEm_code();
		String sql = "SELECT  isdefault_,id_,ID$EMP,isnorm_,name_  FROM datalistconfig left join (SELECT * FROM datalistconfig$emp WHERE EMCODE$EMP='"
				+ em_code
				+ "' and caller$emp='"
				+ caller
				+ "' ) on ID$EMP=id_  WHERE CALLER_='"
				+ caller
				+ "' and (emcode_='"
				+ em_code
				+ "' or ISNORM_=-1 ) order by id$emp asc";
		List<Map<String, Object>> list = baseDao.queryForList(sql);
		boolean hasDefault = false;
		for (Map<String, Object> map : list) {
			if (map.get("ID$EMP") != null) {
				hasDefault = true;
				map.put("ISDEFAULT_", -1);
			} else {
				if (hasDefault)
					map.put("ISDEFAULT_", 0);
			}
		}
		return list;
	}

	@Override
	public List<Map<String, Object>> getTreeNodeData(String id) {
		String sqlString = "select sql_,sqlname_,filterjson_ from datalistconfig where  id_=" + id;
		return baseDao.queryForList(sqlString);
	}

	@Override
	public Boolean deleteTreeNode(String id) {
		Employee employee = SystemSession.getUser();
		if ("admin".equals(employee.getEm_type())) {
			baseDao.execute("delete from datalistconfig$emp where ID$EMP=" + id);
			baseDao.execute("delete from datalistconfig where id_=" + id);
		} else {
			if (baseDao.checkIf("datalistconfig", "ISNORM_='-1' and id_=" + id)) {
				BaseUtil.showError("您(非管理员)没有权限删除标准方案！");
			} else {
				baseDao.execute("delete from datalistconfig$emp where ID$EMP=" + id);
				baseDao.execute("delete from datalistconfig where id_=" + id);
			}

		}
		return true;
	}

	@Override
	public void saveQuery(int id, String data, boolean isDefault, String caller) {
		List<Map<Object, Object>> list = JSONUtil.toMapList(data);
		// Object isNorm = baseDao.getFieldDataByCondition("datalistconfig",
		// " nvl(isNorm_,0) ", "id_="+id);
		Object[] olddata = baseDao
				.getFieldsDataByCondition("datalistconfig", new String[] { "filterJson_", "nvl(isNorm_,0)" }, "id_=" + id);
		if (!olddata[0].equals(data) && Integer.parseInt(olddata[1].toString()) == -1
				&& !"admin".equals(SystemSession.getUser().getEm_type())) {
			BaseUtil.showError("没有权限修改标准方案!");
		}
		List<Object> column = checkData(list);
		checkRepeat(column);
		baseDao.updateByCondition("datalistconfig", "filterJson_='" + data + "'", "id_=" + id);
		if (isDefault && Integer.parseInt(olddata[1].toString()) == -1) {
			baseDao.execute("update datalistconfig set isdefault_=0 where caller_=? and isNorm_=-1", caller);
			baseDao.execute("update datalistconfig set isdefault_=-1 where caller_=? and isNorm_=-1 and id_=?", caller, id);
		} else if (Integer.parseInt(olddata[1].toString()) == -1) {
			baseDao.execute("update datalistconfig set isdefault_=0 where caller_=? and isNorm_=-1 and id_=?", caller, id);
		}
		if (isDefault) {
			baseDao.execute("delete from datalistconfig$emp where CALLER$EMP=? and EMCODE$EMP=?", caller, SystemSession.getUser()
					.getEm_code());
			baseDao.execute(
					"insert into datalistconfig$emp (ID,ID$EMP,EMCODE$EMP,CALLER$EMP) values(DATALISTCONFIG$EMP_SEQ.nextval,?,?,?)", id,
					SystemSession.getUser().getEm_code(), caller);
		} else {
			baseDao.execute("delete from datalistconfig$emp where ID$EMP=? and CALLER$EMP=? and EMCODE$EMP=?", id, caller, SystemSession
					.getUser().getEm_code());
		}
	}

	@Override
	public void saveAnotherQuery(String queryName, boolean isDefault, boolean isNormal, String data, String caller) {
		List<Map<Object, Object>> list = JSONUtil.toMapList(data);
		String type = SystemSession.getUser().getEm_type();
		if (isNormal && !"admin".equals(type)) {
			BaseUtil.showError("没有权限保存为标准方案!");
		}
		if (isNormal) {
			boolean ifname = baseDao.checkIf("datalistconfig", "caller_='" + caller + "' and name_='" + queryName + "'");
			if (ifname) {
				BaseUtil.showError("查询方案名称重复!");
			}
		} else {
			boolean ifname = baseDao.checkIf("datalistconfig", "ISNORM_=0 and caller_='" + caller + "' and name_='" + queryName
					+ "' and emcode_='" + SystemSession.getUser().getEm_code() + "'");
			if (ifname) {
				BaseUtil.showError("查询方案名称重复!");
			}
		}
		List<Object> column = checkData(list);
		checkRepeat(column);
		int default_ = isDefault ? -1 : 0;
		int id_ = baseDao.getSeqId("DATALISTCONFIG_SEQ");
		if (isNormal && isDefault) {
			baseDao.execute("delete from datalistconfig$emp where CALLER$EMP=? and EMCODE$EMP=?", caller, SystemSession.getUser()
					.getEm_code());
			baseDao.execute("update datalistconfig set isdefault_=0 where caller_=? and isNorm_=-1", caller);
		}
		baseDao.execute("insert into datalistconfig (id_,name_,caller_,emcode_,isNorm_,filterJson_,ISDEFAULT_) values(?,?,?,?,?,?,?)", id_,
				queryName, caller, SystemSession.getUser().getEm_code(), isNormal ? -1 : 0, data, (isNormal && isDefault) ? -1 : 0);
		if (isDefault) {
			baseDao.execute("delete from datalistconfig$emp where CALLER$EMP=? and EMCODE$EMP=?", caller, SystemSession.getUser()
					.getEm_code());
			baseDao.execute(
					"insert into datalistconfig$emp (ID,ID$EMP,EMCODE$EMP,CALLER$EMP) values(DATALISTCONFIG$EMP_SEQ.nextval,?,?,?)", id_,
					SystemSession.getUser().getEm_code(), caller);
		}
	}

	private static void match(String regex, String str, String column_value) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		if (!matcher.matches()) {
			BaseUtil.showError(column_value + "的值不合法!");
		}
	}

	private static void checkRepeat(List<Object> column) {
		Set<Object> set = new HashSet<Object>();
		for (Object i : column)
			set.add(i);
		if (!(set.size() == column.size())) {
			BaseUtil.showError("存在重复字段!");
		}
	}

	private static List<Object> checkData(List<Map<Object, Object>> list) {
		String reg = "";
		List<Object> column = new ArrayList<Object>();
		for (Map<Object, Object> map : list) {
			String column_value = map.get("column_value").toString();
			Object originalxtype = map.get("originalxtype");
			String value = map.get("value").toString();
			column.add(column_value);
			if ("numberfield".equals(originalxtype)) {
				if (value.indexOf("~") == -1) {
					reg = "^(>=|>|<=|<|=|!=)?(-)?[0-9]+([.]?[0-9]+)?$";
					match(reg, value, column_value);
				} else {
					reg = "^(-)?[0-9]+([.]?[0-9]+)?~(-)?[0-9]+([.]?[0-9]+)?$";
					match(reg, value, column_value);
				}

			} else if ("datefield".equals(originalxtype)) {
				if (value.indexOf("~") == -1) {
					reg = "^(>=|<=|=)?[0-9]{4}-((0[1-9])|(1[0-2])){1}-((0[1-9])|((1|2)[0-9])|((3)[0-1])){1}$";
					match(reg, value, column_value);
				} else {
					reg = "^[0-9]{4}-((0[1-9])|(1[0-2])){1}-((0[1-9])|((1|2)[0-9])|((3)[0-1])){1}~[0-9]{4}-((0[1-9])|(1[0-2])){1}-((0[1-9])|((1|2)[0-9])|((3)[0-1])){1}$";
					match(reg, value, column_value);
				}
			}
		}
		return column;
	}

	@Override
	public boolean setDefault(String id, String caller) {
		baseDao.execute("delete from datalistconfig$emp where CALLER$EMP=? and EMCODE$EMP=?", caller, SystemSession.getUser().getEm_code());
		baseDao.execute("insert into datalistconfig$emp (ID,ID$EMP,EMCODE$EMP,CALLER$EMP) values(DATALISTCONFIG$EMP_SEQ.nextval,?,?,?)",
				id, SystemSession.getUser().getEm_code(), caller);
		return true;
	}

	private String appendFilterCondition(List<Map<Object, Object>> filterlist) {
		String condition = "";
		for (Map<Object, Object> map : filterlist) {
			String originalxtype = map.get("originalxtype").toString();
			String type = map.get("type").toString();
			String value = map.get("value").toString();
			String column_value = map.get("column_value").toString();
			if (condition != null && condition.trim().length() > 0) {
				condition += " AND ";
			}
			if ("textfield".equals(originalxtype)) {
				if ("direct".equals(type)) {
					condition += column_value + "='" + value + "'";
				} else if ("nodirect".equals(type)) {
					condition += "nvl(" + column_value + ",' ')<>'" + value + "'";
				} else if ("vague".equals(type)) {
					condition += "instr(" + column_value + ",'" + value + "')>0";
				} else if ("novague".equals(type)) {
					condition += "(instr(" + column_value + ",'" + value + "')=0 or " + column_value + " is null)";
				} else if ("head".equals(type)) {
					condition += "instr(" + column_value + ",'" + value + "')=1";
				} else if ("end".equals(type)) {
					condition += "instr(" + column_value + ",'" + value + "',-1,1)=LENGTH(" + column_value + ")-length('" + value
							+ "')+1 and LENGTH(" + column_value + ")>=length('" + value + "')";
				} else if ("null".equals(type)) {
					condition += column_value + " is null";
				}
			} else if ("numberfield".equals(originalxtype)) {
				if ("~".equals(type) || value.indexOf("~") > -1) {
					String[] arr = value.split("~");
					condition += column_value + " between " + arr[0] + " and " + arr[1] + " ";
				} else if ("!=".equals(type)) {
					condition += "(" + column_value + type + value + " or " + column_value + " is null) ";
				} else {
					condition += column_value + type + value;
				}
			} else if ("datefield".equals(originalxtype)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				try {
					if ("~".equals(type) || value.indexOf("~") > -1) {
						String[] arr = value.split("~");
						condition += "to_char(" + column_value + ",'yyyy-MM-dd') between '" + sdf.format(sdf.parse(arr[0])) + "' and '"
								+ sdf.format(sdf.parse(arr[1])) + "'";
					} else if (">=".equals(type) || value.indexOf(">=") == 0) {
						condition += "to_char(" + column_value + ",'yyyy-MM-dd')>='" + sdf.format(sdf.parse(value)) + "' ";
					} else if ("<=".equals(type) || value.indexOf("<=") == 0) {
						condition += "to_char(" + column_value + ",'yyyy-MM-dd')<='" + sdf.format(sdf.parse(value)) + "' ";
					} else {
						condition += "to_char(" + column_value + ",'yyyy-MM-dd')='" + sdf.format(sdf.parse(value)) + "' ";
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if ("combo".equals(originalxtype)) {
				if ("-所有-".equals(value)) {
					condition += " 1=1 ";
				} else if ("-无-".equals(value)) {
					condition += "nvl(to_char(" + column_value + "),' ')=' '";
				} else {
					condition += "instr(" + column_value + ",'" + value + "')=1";
				}
			}
		}
		return condition;
	}

	@Override
	public boolean hasFilterCondition(String caller) {
		Employee employee = SystemSession.getUser();
		String em_code = employee.getEm_code();
		boolean flag = baseDao.checkIf("datalistconfig left join (SELECT * FROM datalistconfig$emp WHERE EMCODE$EMP='" + em_code
				+ "' and caller$emp='" + caller + "' ) on ID$EMP=id_ ", "CALLER_='" + caller + "' and (emcode_='" + em_code
				+ "' or ISNORM_=-1 )");
		return flag;
	}

}
