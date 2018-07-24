package com.uas.erp.service.common.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.WordToHtml;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.bind.Operation;
import com.uas.erp.core.bind.Status;
import com.uas.erp.core.encry.HmacUtils;
import com.uas.erp.core.support.ICallable;
import com.uas.erp.core.support.MergeTask;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DataListComboDao;
import com.uas.erp.dao.common.DataListDao;
import com.uas.erp.dao.common.FormAttachDao;
import com.uas.erp.dao.common.FormDao;
import com.uas.erp.dao.common.HrJobDao;
import com.uas.erp.dao.common.PaymentsDao;
import com.uas.erp.dao.common.PowerDao;
import com.uas.erp.dao.common.PurchaseDao;
import com.uas.erp.model.DataList;
import com.uas.erp.model.DataListCombo;
import com.uas.erp.model.Employee;
import com.uas.erp.model.EmpsJobs;
import com.uas.erp.model.Form;
import com.uas.erp.model.FormDetail;
import com.uas.erp.model.FormItems;
import com.uas.erp.model.FormPanel;
import com.uas.erp.model.GridColumns;
import com.uas.erp.model.GridFields;
import com.uas.erp.model.GridPanel;
import com.uas.erp.model.LimitFields;
import com.uas.erp.model.Master;
import com.uas.erp.model.MessageLog;
import com.uas.erp.model.PaymentsForDate;
import com.uas.erp.model.PersonalPower;
import com.uas.erp.model.PositionPower;
import com.uas.erp.model.RelativeSearch;
import com.uas.erp.model.RelativeSearchLimit;
import com.uas.erp.model.SysSpecialPower;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.common.SingleFormItemsService;

@Service("singleFormItemsService")
public class SingleFormItemsServiceImpl implements SingleFormItemsService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private FormDao formDao;
	@Autowired
	private DataListComboDao dataListComboDao;
	@Autowired
	private HrJobDao hrJobDao;
	@Autowired
	private PaymentsDao paymentsDao;
	@Autowired
	private DataListDao dataListDao;
	@Autowired
	private FormAttachDao formAttachDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private PurchaseDao purchaseDao;
	@Autowired
	private PowerDao powerDao;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private EnterpriseService enterpriseService;

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
						/*if (formDetail.getFd_type().equals("MT")) {// 对于合并型的字段
							if (formDetail.getFd_logictype() != null && !formDetail.getFd_logictype().equals("")) {
								items.add(new FormItems(count, str, formDetail, cos));
							}
						} else {*/
							items.add(new FormItems(count, str, formDetail, cos));
						/*}*/
					}
				}
				count++;
			}
		}
		if (count == 1) {// 说明没有分组
			for (FormDetail formDetail : formDetails) {
				if (formDetail.getFd_type() != null) {
					/*if (formDetail.getFd_type().equals("MT")) {
						if (formDetail.getFd_logictype() != null && !formDetail.getFd_logictype().equals("")) {
							items.add(new FormItems(count, null, formDetail, combos));
						}
					} else {*/
						items.add(new FormItems(count, null, formDetail, combos));
					/*}*/
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
		formPanel.setFo_isPrevNext(form.getFo_isPrevNext());//上一条下一条
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
	public String getFormDataByCaller(String caller, String condition) {
		Form form = formDao.getForm(caller, SpObserver.getSp());
		return baseDao.getDataStringByForm(form, condition);
	}

	/**
	 * 新方法 支持jboss
	 */
	@Override
	public Map<String, Object> getFormData(String caller, String condition, boolean isCloud) {
		Form form = null;
		String master = SpObserver.getSp();
		if (isCloud) {
			SpObserver.putSp(Constant.UAS_CLOUD);
			form = formDao.getForm(caller, Constant.UAS_CLOUD);
			SpObserver.putSp(master);
		} else
			form = formDao.getForm(caller, master);
		return baseDao.getFormData(form, condition);
	}

	/**
	 * 按jprocessset表的流程caller及界面url来确定界面的caller
	 * 
	 * @param caller
	 * @param url
	 * @return
	 */
	@Override
	public String getPageCallerByFlow(String caller, String url) {
		String whoami = baseDao.getJdbcTemplate().queryForObject(
				"select max(js_pagecaller) from jprocessset where js_caller=? and js_formurl=?", String.class, caller, url);
		if (whoami != null)
			return whoami;
		return caller;
	}

	@Override
	public JSONObject getReadOnlyForm(String caller, String condition, String url, String language, Employee employee) {
		String whoami = getPageCallerByFlow(caller, url);
		Form form = formDao.getForm(whoami, employee.getEm_master());
		Map<String, Object> store = baseDao.getFormData(form, condition);
		if (store == null) {
			BaseUtil.showError("单据不存在！");
		}
		List<DataListCombo> combos = dataListComboDao.getComboxsByCaller(whoami, employee.getEm_master());
		List<FormDetail> formDetails = form.getFormDetails();
		List<LimitFields> limits = new ArrayList<LimitFields>();
		JSONObject rsForm = new JSONObject();
		// 权限控制字段
		if (!"admin".equals(employee.getEm_type())) {
			limits = hrJobDao.getLimitFieldsByType(whoami, null, 1, employee.getEm_defaulthsid(), employee.getEm_master());
		}
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
		JSONObject data = new JSONObject();
		JSONObject gp = null;
		Object value = null;
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
				gp = new JSONObject();
				int i = 0;
				for (FormDetail formDetail : list) {
					value = store.get(formDetail.getFd_field());
					if (value != null && !"".equals(value) && formDetail.getFd_columnwidth() > 0 && !formDetail.getFd_type().equals("H")
							&& !isLimited(limits, formDetail) && !formDetail.getFd_field().equals(form.getFo_codefield())) {
						if ("MT".equals(formDetail.getFd_type())) {// 对于合并型的字段
							if (formDetail.getFd_logictype() != null && !formDetail.getFd_logictype().equals("")) {
								gp.put(formDetail.getFd_caption(), value + "(" + store.get(formDetail.getFd_logictype()) + ")");
							}
						} else {
							value = getDisplayValue(combos, formDetail, value);
							if (!StringUtil.hasText(formDetail.getFd_caption()) && i > 0) {
								FormDetail last = list.get(i - 1);
								if (StringUtil.hasText(last.getFd_caption())) {
									value = StringUtil.nvl(store.get(last.getFd_field()), "") + "(" + value + ")";
									gp.put(last.getFd_caption(), value);
								}
							} else
								gp.put(formDetail.getFd_caption(), value);
						}
					}
					i++;
				}
				if (!gp.isEmpty()) {
					data.put(str, gp);
					count++;
				}
			}
		}
		if (count == 1) {// 说明没有分组
			data = new JSONObject();
			int i = 0;
			for (FormDetail formDetail : formDetails) {
				value = store.get(formDetail.getFd_field());
				if (value != null && !"".equals(value) && formDetail.getFd_columnwidth() > 0 && !"H".equals(formDetail.getFd_type())
						&& !isLimited(limits, formDetail) && !formDetail.getFd_field().equals(form.getFo_codefield())) {
					if ("MT".equals(formDetail.getFd_type())) {
						if (formDetail.getFd_logictype() != null && !formDetail.getFd_logictype().equals("")) {
							data.put(formDetail.getFd_caption(), value + "(" + store.get(formDetail.getFd_logictype()) + ")");
						}
					} else {
						value = getDisplayValue(combos, formDetail, value);
						if (!StringUtil.hasText(formDetail.getFd_caption()) && i > 0) {
							FormDetail last = formDetails.get(i - 1);
							if (StringUtil.hasText(last.getFd_caption())) {
								value = StringUtil.nvl(store.get(last.getFd_field()), "") + "(" + value + ")";
								data.put(last.getFd_caption(), value);
							}
						} else
							data.put(formDetail.getFd_caption(), value);
					}
				}
				i++;
			}
			rsForm.put("data", data);
		} else {
			rsForm.put("data", data);
			rsForm.put("group", true);
		}
		return rsForm;
	}

	/**
	 * 权限限制的字段
	 * 
	 * @param limits
	 * @param formDetail
	 * @return
	 */
	private boolean isLimited(List<LimitFields> limits, FormDetail formDetail) {
		for (LimitFields limit : limits) {
			if (limit.getLf_field().equals(formDetail.getFd_field()))
				return true;
		}
		return false;
	}

	/**
	 * 配置成下拉框的，取显示值
	 * 
	 * @param combos
	 * @param formDetail
	 * @param value
	 * @return
	 */
	private Object getDisplayValue(List<DataListCombo> combos, FormDetail formDetail, Object value) {
		if ("C".equals(formDetail.getFd_type())) {
			for (DataListCombo combo : combos) {
				if (combo.getDlc_value() != null && combo.getDlc_fieldname().equals(formDetail.getFd_field())
						&& combo.getDlc_value().equals(value)) {
					value = combo.getDlc_display();
					break;
				}
			}
		}
		return value;
	}

	@Override
	public int getId(String seq) {
		return baseDao.getSeqId(seq);
	}

	@Override
	public Object getFieldData(String caller, String field, String condition) {
		return baseDao.getFieldDataByCondition(caller, field, condition);
	}

	@Override
	public JSONObject getFieldsData(String caller, String fields, String condition) {
		JSONObject obj = new JSONObject();
		String[] fis = fields.split(",");
		Object[] data = baseDao.getFieldsDataByCondition(caller, fields, condition);
		if (data != null) {
			for (int i = 0; i < fis.length; i++) {
				if (data[i] != null && !"null".equals(data[i]))
					obj.put(fis[i], data[i]);
			}
		}
		return obj;
	}

	@Override
	public boolean checkFieldValue(String caller, String condition) {
		int count = baseDao.getCountByCondition(caller, condition);
		return count == 0 ? true : false;
	}

	@Override
	public String getCodeString(String caller, String table, int type) {
		if (table == null || table.equals("")) {
			table = (String) baseDao.getFieldDataByCondition("form", "fo_table", "fo_caller='" + caller + "'");
		}
		return baseDao.sGetMaxNumber(table != null ? table.split(" ")[0] : caller, type);
	}

	@Override
	public void vastDelete(String language, Employee employee, String caller, String data) {
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield" }, "fo_caller='" + caller + "'");
		String tab = String.valueOf(objs[0]);
		String keyF = String.valueOf(objs[1]);
		if (tab != null && keyF != null) {// 必须配置了表名和主键字段
			List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
			for (Map<Object, Object> m : maps) {
				baseDao.deleteByCondition(tab, keyF + "=" + m.get(keyF));
			}
		} else {
			BaseUtil.showError("配置不详，无法执行删除操作.");
		}
	}

	@Override
	public void vastSubmit(String language, Employee employee, String caller, String data) {
		deal(language, employee, caller, data, Operation.COMMIT);
	}

	@Override
	public void vastAudit(String language, Employee employee, String caller, String data) {
		deal(language, employee, caller, data, Operation.AUDIT);
	}

	@Override
	public void vastSend(String language, Employee employee, String caller, String data) {

	}

	@Override
	public void vastFreeze(String language, Employee employee, String caller, String data) {
		deal(language, employee, caller, data, Operation.FREEZE);
	}

	@Override
	public void vastResStart(String language, Employee employee, String caller, String data) {
		deal(language, employee, caller, data, Operation.RESSTART);
	}

	@Override
	public void vastResFinish(String language, Employee employee, String caller, String data) {
		deal(language, employee, caller, data, Operation.AUDIT);
	}

	@Override
	public void vastClose(String language, Employee employee, String caller, String data) {
		deal(language, employee, caller, data, Operation.CLOSE);
	}

	@Override
	public void vastCloseDetail(String language, Employee employee, String caller, String data) {
		dealdetail(language, employee, caller, data, Operation.CLOSE);
	}

	@Override
	public String vastPost(String language, Employee employee, String caller, String to, String data) {
		String res = null;
		if ("true".equals(BaseUtil.getXmlSetting("group"))) {
			String dataCenter = BaseUtil.getXmlSetting("dataSob");
			if (!SpObserver.getSp().equals(dataCenter)) {
				boolean isBase = baseDao.checkIf("basedataset", "bds_caller='" + caller.replace("!Post", "")
						+ "' and nvl(bds_editable,0)=1 and bds_caller<>'PositionPower'");
				if (isBase) {
					return "不允许在营运中心同步该资料!";
				}
			}
		}
		// 人员资料同步的特殊限制修改，考虑到有的人员资料没填em_masters字段
		if ("true".equals(BaseUtil.getXmlSetting("group")) && "Employeemanager!Post".equals(caller)) {
			String em_id = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(em_id) from employee where em_type='admin' and em_id in(" + data + ")", String.class);
			if (em_id != null) {
				String masters = baseDao.getJdbcTemplate().queryForObject(
						"select wmsys.wm_concat(ma_user) from " + BaseUtil.getXmlSetting("defaultSob") + ".Master where ma_type=3",
						String.class);
				res = baseDao.callProcedure("SYS_POST", new Object[] { caller, SpObserver.getSp(), masters, em_id, employee.getEm_name()+"("+employee.getEm_code()+")",
						employee.getEm_id() });
			}
			if (res == null || res.length() == 0) {
				SqlRowList rs = baseDao
						.queryForRowSet("select wmsys.wm_concat(em_id),em_masters from employee where nvl(em_type,' ')<>'admin' and em_id in("
								+ data + ") group by em_masters");
				while (rs.next()) {
					if (rs.getString(2) != null && rs.getString(2) != "" && rs.getString(2).length() != 0)
						res = baseDao.callProcedure("SYS_POST", new Object[] { caller, SpObserver.getSp(), rs.getString(2),
								rs.getString(1), employee.getEm_name()+"("+employee.getEm_code()+")", employee.getEm_id() });
					else
						res = baseDao
								.callProcedure(
										"SYS_POST",
										new Object[] { caller, SpObserver.getSp(), to, rs.getString(1), employee.getEm_name()+"("+employee.getEm_code()+")",
												employee.getEm_id() });
					if (res != null && res.length() > 0) {
						break;
					}
				}
			}
		} else {
			try {
				res = baseDao.callProcedure("SYS_POST",
						new Object[] { caller, SpObserver.getSp(), to, data, employee.getEm_name()+"("+employee.getEm_code()+")", employee.getEm_id() });
				handlerService.handler(caller, "sync", "after", new Object[]{employee.getEm_id()});			
			} catch (Exception e) {	
				e.printStackTrace();
			}
			}
		//增加判断 为人员资料抛转时才执行以下逻辑
		if("Employeemanager!Post".equals(caller)){
			String masterN = SpObserver.getSp();
			String[] master = to.split(",");
			List<Employee> employees = employeeService.getEmployeesByCondition("em_id in (" + data + ") and em_b2benable=-1");
			
			if (!CollectionUtil.isEmpty(employees)) {
				for (int i = 0; i < master.length; i++) {
					SpObserver.putSp(master[i]);
					final Master masterTo = enterpriseService.getMasterByName(master[i]);
					try {
						MergeTask<String, Employee> task = new MergeTask<String, Employee>(new ICallable<String, Employee>() {

							@Override
							public String call(Employee employee) throws Exception {
								String err = employeeService.postToAccountCenter(employee, masterTo);
								return err == null ? null : "【" + employee.getEm_name() + "】：" + err;
							}

						});
						for (Employee employeess : employees) {
							task.join(employeess);
						}
						task.execute();
					} catch (EmptyResultDataAccessException e) {

					}
				}
			}
			SpObserver.putSp(masterN);
		}
		return res;
	}

	/**
	 * 根据{condition}查看单据的操作日志
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<MessageLog> getMessageLogs(String caller, Object id) {
		try {
			return baseDao.getJdbcTemplate().query(
					"select * from MessageLog where ml_search like '" + caller + "|%=" + id + "' order by ml_id",
					new BeanPropertyRowMapper(MessageLog.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 根据{condition}查看我的操作日志
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<MessageLog> getMyMessageLogs(String caller, int id, Employee employee) {
		try {
			return baseDao.getJdbcTemplate().query(
					"select * from MessageLog where ml_search like '" + caller + "|%=" + id + "' AND " + "ml_man='" + employee.getEm_name()
							+ "' order by ml_id", new BeanPropertyRowMapper(MessageLog.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 根据收款方式中的月增加和日增加来得到计划收款日
	 */
	@Override
	public String getPayDate(String paymentmethodid, String startdateString) {

		if (paymentmethodid == null || startdateString == null || paymentmethodid.equals("") || startdateString.equals("")) {
			return "";
		} else {
			PaymentsForDate paymentsForDate = paymentsDao.findPaymentsById(paymentmethodid);

			if (paymentsForDate != null) {
				String[] startpart = startdateString.split("-");
				Calendar calendar = new GregorianCalendar(Integer.parseInt(startpart[0]), Integer.parseInt(startpart[1]) - 1,
						Integer.parseInt(startpart[2]));
				// calendar.set(Integer.parseInt(startpart[0]),0,0);
				calendar.add(Calendar.MONTH, paymentsForDate.getPa_monthadd());
				calendar.add(Calendar.DAY_OF_MONTH, paymentsForDate.getPa_dayadd());
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				String dateString = df.format(calendar.getTime());
				return dateString;
			} else {
				return "";
			}

		}
	}

	@Override
	public String getFieldDatas(String caller, String field, String condition) {
		List<Object> objs = baseDao.getFieldDatasByCondition(caller, field, condition);
		return BaseUtil.parseList2Str(objs, "#", false);
	}

	/**
	 * 整批作废
	 */
	@Override
	public void vastCancel(String language, Employee employee, String caller, String data) {
		deal(language, employee, caller, data, Operation.NULLIFY);
	}

	@Override
	public List<?> getFieldsDatas(String caller, String fields, String condition) {
		StringBuffer sb = new StringBuffer("SELECT ");
		sb.append(fields);
		sb.append(" FROM ");
		sb.append(caller);
		sb.append(" WHERE ");
		sb.append(condition);
		SqlRowList list = baseDao.queryForRowSet(sb.toString());
		List<Object> data = new ArrayList<Object>();
		while (list.next()) {
			data.add(list.getJSONObject());
		}
		return data;
	}

	@Override
	public void vastResAudit(String language, Employee employee, String caller, String data) {
		deal(language, employee, caller, data, Operation.RESAUDIT);
	}

	@Override
	public void vastFreezeDetail(String language, Employee employee, String caller, String data) {
		dealdetail(language, employee, caller, data, Operation.FREEZE);
	}

	/**
	 * 批量修改状态(只限主表) 需要配置正确，且将主表ID传回
	 */
	private void deal(String language, Employee employee, String caller, String data, Operation operation) {
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield", "fo_statusfield",
				"fo_statuscodefield", "fo_flowcaller" }, "fo_caller='" + caller + "'");
		Status status = operation.getResultStatus();
		String tab = String.valueOf(objs[0]);
		String keyF = String.valueOf(objs[1]);
		String sF = String.valueOf(objs[2]);
		String scF = String.valueOf(objs[3]);
		String pageCaller = objs[4] == null ? null : String.valueOf(objs[4]);
		if (tab != null && keyF != null && scF != null) {// 必须配置了状态码字段、表名和主键字段
			List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
			StringBuffer sb = new StringBuffer();
			if (sF != null) {// 如果存在状态字段
				sb.append(sF);
				sb.append("='");
				sb.append(status.display());
				sb.append("',");
			}
			sb.append(scF);
			sb.append("='");
			sb.append(status.code());
			sb.append("'");
			if (objs[0].toString().contains("left")) {
				tab = tab.split("left")[0].toString();
			}
			pageCaller = pageCaller == null ? tab : pageCaller;
			List<String> sqls = new ArrayList<String>();
			for (Map<Object, Object> m : maps) {
				sqls.add("update " + tab + " set " + sb.toString() + " where " + keyF + "=" + m.get(keyF));
				sqls.add(baseDao.logger.getMessageLog(operation, pageCaller, keyF, m.get(keyF)).getSql());
			}
			baseDao.execute(sqls);
		} else {
			BaseUtil.showError("配置不详，无法执行操作.");
		}
	}

	/**
	 * 批量修改状态(包括主表和从表) 需要配置正确，且将明细ID传回
	 */
	private void dealdetail(String language, Employee employee, String caller, String data, Operation operation) {
		SqlRowList rs = baseDao.queryForRowSet("SELECT * FROM Form WHERE fo_caller=?", caller);
		Object obj = null;
		if (rs.next()) {
			Status status = operation.getResultStatus();
			String gKey = rs.getString("fo_detailkeyfield");
			if (gKey == null) {
				obj = baseDao.getFieldDataByCondition("DetailGrid", "dg_field", "dg_caller='" + caller + "' and dg_logictype='keyField'");
				if (obj == null) {
					BaseUtil.showError("配置有误,未找到主键!");
				} else {
					gKey = String.valueOf(obj);
				}
			}
			String gTab = rs.getString("fo_detailtable");
			if (gTab == null) {
				obj = baseDao.getFieldDataByCondition("DetailGrid", "dg_table", "dg_caller='" + caller
						+ "' and dg_table is not null and dg_logictype='keyField'");
				if (obj == null) {
					BaseUtil.showError("配置有误,未找到表名!");
				} else {
					gTab = String.valueOf(obj);
				}
			}
			gTab = gTab.split(" ")[0];
			String fTab = rs.getString("fo_table");
			if (fTab == null) {
				return;
			}
			fTab = fTab.split(" ")[0];
			if (gTab.equals(fTab)) {// 如果Form和Grid都显示的是主表数据，此时无法得知从表名字，从而无法修改明细状态
				deal(language, employee, caller, data, operation);
				return;
			}
			String pageCaller = rs.getString("fo_flowcaller");
			pageCaller = pageCaller == null ? gTab : pageCaller;
			String mKey = rs.getString("fo_detailmainkeyfield");
			if (mKey == null) {
				obj = baseDao.getFieldDataByCondition("DetailGrid", "dg_field", "dg_caller='" + caller + "' and dg_logictype='mainField'");
				if (obj != null) {
					mKey = String.valueOf(obj);
				}
			}
			String kKey = rs.getString("fo_keyfield");
			String detnoF = rs.getString("fo_detaildetnofield");
			String sf = rs.getString("fo_detailstatus");
			String sfc = rs.getString("fo_detailstatuscode");
			if (sf == null) {
				obj = baseDao
						.getFieldDataByCondition("DetailGrid", "dg_field", "dg_caller='" + caller + "' and dg_logictype='statusField'");
				if (obj != null) {
					sf = String.valueOf(obj);
				}
			}
			if (sfc == null) {
				obj = baseDao.getFieldDataByCondition("DetailGrid", "dg_field", "dg_caller='" + caller
						+ "' and dg_logictype='statuscodeField'");
				if (obj != null) {
					sfc = String.valueOf(obj);
				}
			}
			if (sf == null && sfc == null) {
				BaseUtil.showError("配置有误,未找到状态描述字段!");
			}
			StringBuffer update = new StringBuffer();
			if (sf != null) {
				update.append(sf);
				update.append("='");
				update.append(status.display());
				update.append("'");
			}
			if (sfc != null) {
				if (sf != null) {
					update.append(",");
				}
				update.append(sfc);
				update.append("='");
				update.append(status.code());
				update.append("'");
			}
			StringBuffer updateA = new StringBuffer();
			if ("Purchase!Close!Deal".equals(caller) || "Purchase".equals(caller)) {// 采购单明细结案
				updateA.append(",pd_enddate=sysdate");
			} else if ("Sale!Close!Deal".equals(caller) || "Sale".equals(caller)) {// 销售单,明细结案
				updateA.append(",sd_enddate=sysdate");
			} else if ("SaleForecast!Close!Deal".equals(caller)) {// 销售预测结案
				updateA.append(",sd_finishdate=sysdate");
			} else if ("Purchase!FinishReStart!Deal".equals(caller)) {// 采购明细反结案
				updateA.append(",pd_enddate=null");
			} else if ("Sale!ReStart".equals(caller)) {// 销售单，明细反结案
				updateA.append(",sd_enddate=null");
			} else if ("SaleForecast!ReStart!Deal".equals(caller)) {// 销售预测 反结案
				updateA.append(",sd_finishdate=null");
			} else if ("Application!CloseReStart!Deal".equals(caller)){//请购单反结案
				if(baseDao.isDBSetting("Application!Close!Deal", "updateQty")){
					updateA.append(",ad_qty=nvl(ad_oldqty,0)");
				}
			}
			List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
			Set<Integer> keys = new HashSet<Integer>();
			Object key = null;
			List<String> sqls = new ArrayList<String>();
			for (Map<Object, Object> m : maps) {
				sqls.add("update " + gTab + " set " + update.toString() + updateA.toString() + " where " + gKey + "=" + m.get(gKey));
				if (StringUtils.hasText(mKey)) {
					if (StringUtils.hasText(detnoF)) {
						Object[] args = baseDao.getFieldsDataByCondition(gTab, mKey + "," + detnoF, gKey + "=" + m.get(gKey));
						if (args != null) {
							if (args[0] != null && !keys.contains(args[0])) {
								keys.add(Integer.parseInt(String.valueOf(args[0])));
								sqls.add(baseDao.logger.getMessageLog(operation.getTitle(language),
										operation.getResult(language) + ",明细行:" + args[1], pageCaller, kKey, args[0]).getSql());
								continue;
							}
						}
					} else {
						key = baseDao.getFieldDataByCondition(gTab, mKey, gKey + "=" + m.get(gKey));
						if (key != null && !keys.contains(key)) {
							keys.add(Integer.parseInt(key.toString()));
						}
					}
				}
				sqls.add(baseDao.logger.getMessageLog(operation, pageCaller, gKey, m.get(gKey)).getSql());
			}
			baseDao.execute(sqls);
			if (mKey != null) {
				if (kKey == null) {
					return;
				}
				String fs = rs.getString("fo_statusfield");
				String fsc = rs.getString("fo_statuscodefield");
				if (fs == null && fsc == null) {
					return;
				}
				StringBuffer sUpdate = new StringBuffer();
				if (fs != null) {
					sUpdate.append(fs);
					sUpdate.append("='");
					sUpdate.append(status.display());
					sUpdate.append("'");
				}
				if (fsc != null) {
					if (fs != null) {
						sUpdate.append(",");
					}
					sUpdate.append(fsc);
					sUpdate.append("='");
					sUpdate.append(status.code());
					sUpdate.append("'");
				}
				StringBuffer sUpdateA = new StringBuffer();
				if ("Purchase!Close!Deal".equals(caller) || "Purchase".equals(caller)) {// 采购单主表结案
					sUpdateA.append(",pu_enddate=sysdate");
				} else if ("Sale!Close!Deal".equals(caller) || "Sale".equals(caller)) {// 销售单批量，主表结案
					sUpdateA.append(",sa_enddate=sysdate");
				} else if ("SaleForecast!Close!Deal".equals(caller)) {// 销售预测结案
					sUpdateA.append(",sf_enddate=sysdate");
				} else if ("Purchase!FinishReStart!Deal".equals(caller)) {// 采购反结案
					sUpdateA.append(",pu_enddate=null");
				} else if ("Sale!ReStart".equals(caller)) {// 销售单反结案
					sUpdateA.append(",sa_enddate=null");
				} else if ("SaleForecast!ReStart!Deal".equals(caller)) {// 销售预测反结案
					sUpdateA.append(",sf_enddate=null");
				}
				int fCount = 0, count = 0;
				for (Integer id : keys) {
					count = baseDao.getCountByCondition(gTab, mKey + "=" + id);
					fCount = baseDao.getCountByCondition(gTab, mKey + "=" + id + " AND " + update.toString().replace(",", " AND "));
					if (status.equals("ENTERING") || status.equals("AUDITED")) {
						if (fCount > 0) {
							baseDao.updateByCondition(fTab, sUpdate.toString() + sUpdateA.toString(), kKey + "=" + id);
						}
					} else {
						if (fCount == count) {
							baseDao.updateByCondition(fTab, sUpdate.toString() + sUpdateA.toString(), kKey + "=" + id);
						}
					}
				}
			}
		}
	}

	@Override
	public void vastCancelDetail(String language, Employee employee, String caller, String data) {
		dealdetail(language, employee, caller, data, Operation.NULLIFY);
	}

	@Override
	public void vastResStartDetail(String language, Employee employee, String caller, String data) {
		dealdetail(language, employee, caller, data, Operation.RESSTART);
	}

	@Override
	public void vastResCloseDetail(String language, Employee employee, String caller, String data) {
		dealdetail(language, employee, caller, data, Operation.RESCLOSE);
	}

	@Override
	public void updateByCondition(String table, String update, String condition, Employee employee, String type, String caller) {
		baseDao.updateByCondition(table, update, condition);
		if (!"".equals(type) || type != null) {
			baseDao.logMessage(new MessageLog(employee.getEm_name(), type + "操作", type + "成功", caller + "|" + condition.replaceAll("'", "''")));
		}
	}

	/**
	 * 查询之前，到documentHandler执行与caller对应的配置的方法
	 */
	@Override
	public String beforeQuery(String caller, String condition, Employee employee) {
		handlerService.handler(caller, "query", "before", new Object[] { condition });
		try {
			return dataListDao.getRelativesettings(caller, "batchdeal", employee.getEm_id());
		} catch (Exception e) {
		}
		return null;

	}

	/**
	 * 单据关联查询权限表
	 * 
	 * @param caller
	 * @return
	 */
	private List<RelativeSearchLimit> getRelativeSearchLimit(String caller) {
		Employee employee = SystemSession.getUser();
		List<RelativeSearchLimit> limits = null;
		// 单据关联查询是否需要进行权限管控
		boolean limit = baseDao.isDBSetting("relativeSearchLimit") && !employee.isAdmin();

		if (limit) {
			String dataSource = SpObserver.getSp();
			limits = new ArrayList<RelativeSearchLimit>();
			limits.addAll(formDao.getRelativeSearchLimitsByEmpl(caller, employee.getEm_id(), dataSource));
			limits.addAll(formDao.getRelativeSearchLimitsByJob(caller, employee.getEm_defaulthsid(), dataSource));
			
		
			
		}
		
		return limits;
	}

	/**
	 * 权限允许的关联查询方案，基于caller一致的情况下
	 * 
	 * @param searchs
	 * @param limits
	 * @return
	 */
	private List<RelativeSearch> getAuthedRelativeSearch(String caller) {
		List<RelativeSearch> searchs = formDao.getRelativeSearchs(caller, SpObserver.getSp());
		// 权限分配
		List<RelativeSearchLimit> limits = getRelativeSearchLimit(caller);
		if (limits == null) {			
			return searchs;
		}
		List<RelativeSearch> authed = new ArrayList<RelativeSearch>(searchs);
		List<RelativeSearch> removeauthed = new ArrayList<RelativeSearch>();
		for (RelativeSearch search : searchs) {
			for (RelativeSearchLimit limit : limits) {
				if (limit.getRsl_title().equals(search.getRs_title())) {
					removeauthed.add(search);
				}
			}
		}
		for(RelativeSearch r:removeauthed){
			authed.remove(r);
		}
		return authed;
	}

	@Override
	public List<Map<String, Object>> getRelativeSearchs(String caller) {
		Employee employee = SystemSession.getUser();
		List<RelativeSearch> searchs = getAuthedRelativeSearch(caller);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> obj = null;
		for (RelativeSearch s : searchs) {
			FormPanel formPanel = new FormPanel();
			List<FormItems> items = new ArrayList<FormItems>();
			List<RelativeSearch.Form> forms = s.getForms();
			for (RelativeSearch.Form f : forms) {
				items.add(new FormItems(f));
			}
			formPanel.setItems(items);
			formPanel.setTablename(s.getRs_table());
			formPanel.setTitle(s.getRs_title());
			formPanel.setCondition(s.getRs_condition());
			formPanel.setCaller(s.getRs_caller());
			formPanel.setFo_id(s.getRs_id());
			List<RelativeSearch.Grid> grids = s.getGrids();
			GridPanel gridPanel = new GridPanel();
			List<GridFields> fields = new ArrayList<GridFields>();
			List<GridColumns> columns = new ArrayList<GridColumns>();
			for (RelativeSearch.Grid g : grids) {
				fields.add(new GridFields(g));
				columns.add(new GridColumns(g));
			}
			List<LimitFields> limits = new ArrayList<LimitFields>();
			if (!"admin".equals(employee.getEm_type())) // 关联查询权限外字段
				limits = hrJobDao.getLimitFieldsByType(caller + "|" + s.getRs_title(), null, 3, employee.getEm_defaulthsid(),
						employee.getEm_master());
			gridPanel.setGridColumns(columns);
			gridPanel.setGridFields(fields);
			gridPanel.setLimits(limits);
			obj = new HashMap<String, Object>();
			obj.put("form", formPanel);
			obj.put("grid", gridPanel);
			list.add(obj);
		}
		return list;
	}

	@Override
	public JSONArray getSearchData(Integer id, String tabName, String condition, String fields, int start, int end) {
		RelativeSearch search = formDao.getRelativeSearch(id, SpObserver.getSp());
		if (search != null) {
			StringBuffer sqlFields = new StringBuffer();
			for (RelativeSearch.Grid grid : search.getGrids()) {
				if (sqlFields.length() > 0)
					sqlFields.append(",");
				sqlFields.append(grid.getRsg_field());
			}
			String sql = RelativeSearch.getDataSql(tabName, baseDao.parseEmpCondition(condition), fields, sqlFields.toString(),
					search.getRs_orderby(), search.getRs_groupby(), start, end);
			SqlRowList rs = baseDao.queryForRowSet(sql);
			String[] fs = BaseUtil.parseStr2Array(fields, ",");
			JSONArray array = new JSONArray();
			while (rs.next()) {
				JSONObject obj = new JSONObject();
				for (String f : fs) {
					obj.put(f, SqlRowList.parseValue(rs.getObject(f)));
				}
				array.add(obj);
			}
			return array;
		}
		return null;
	}

	@Override
	public int getSearchDataCount(String tabName, String condition) {
		return baseDao.getCount(RelativeSearch.getCountSql(tabName, baseDao.parseEmpCondition(condition)));
	}

	@Override
	public Form getForm(String caller) {
		return formDao.getForm(caller, SpObserver.getSp());
	}

	@Override
	public void vastCloseSaleDetail(String language, Employee employee, String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Object id, closeReason;
		String adidstr = "";
		Object sa_id = "";
		for (Map<Object, Object> map : maps) {
			adidstr += "," + map.get("sd_id").toString();
		}
		if (!adidstr.equals("")) {
			adidstr = adidstr.substring(1);
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(distinct '销售单号：'||snd_ordercode||'序号：'||snd_orderdetno) from SendNotifyDetail where nvl(snd_yqty,0)<nvl(snd_outqty,0) and nvl(snd_statuscode,' ')<>'FINISH' and (snd_ordercode, snd_orderdetno) in (select sd_code, sd_detno from SaleDetail where sd_id in ("
									+ adidstr + "))", String.class);
			if (dets != null) {
				BaseUtil.showError("存在出货通知明细行状态不等于已结案且未全部转，不允许进行结案操作！" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(distinct '销售单号：'||pd_ordercode||'序号：'||pd_orderdetno) from ProdIODetail where nvl(pd_status,0)=0 and pd_piclass in ('出货单','销售退货单') and (pd_ordercode, pd_orderdetno) in (select sd_code, sd_detno from SaleDetail where sd_id in ("
									+ adidstr + "))", String.class);
			if (dets != null) {
				BaseUtil.showError("存在出货单或者销售退货单未过账，不允许进行结案操作！" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(distinct '销售单号：'||snd_ordercode||'序号：'||snd_orderdetno) from SendNotifyChangeDetail left join SendNotifyChange on scd_scid=sc_id left join SendNotifydetail on scd_sndid=snd_id where sc_statuscode<>'AUDITED' and (snd_ordercode,snd_orderdetno) in (select sd_code,sd_detno from SaleDetail where sd_id in ("
									+ adidstr + "))", String.class);
			if (dets != null) {
				BaseUtil.showError("存在出货通知变更单或出货通知单未全部转，不允许进行结案操作！" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(distinct '销售单号：'||scd_sacode||'序号：'||scd_sddetno) from SaleChangeDetail left join SaleChange on scd_scid=sc_id where sc_statuscode<>'AUDITED' and (scd_sacode,scd_sddetno) in (select sd_code,sd_detno from SaleDetail where sd_id in ("
									+ adidstr + "))", String.class);
			if (dets != null) {
				BaseUtil.showError("存在销售变更单未审核，不允许进行结案操作！" + dets);
			}
		}
		for (Map<Object, Object> m : maps) {
			id = m.get("sd_id");
			sa_id = m.get("sd_said");
			handlerService.handler("Sale", "enddetail", "before", new Object[] { id });
		}
		for (Map<Object, Object> m : maps) {
			id = m.get("sd_id");
			closeReason = m.get("sd_barcode");
			if (m.containsKey("sd_barcode") && (closeReason == null || "".equals(closeReason.toString()))) {
				BaseUtil.showError("结案原因没有填写,结案失败!");
			}
			if (closeReason != null && !"".equals(closeReason.toString())) {
				baseDao.updateByCondition("saledetail", "sd_barcode='" + closeReason + "'", "sd_id=" + id);
			}
		}
		dealdetail(language, employee, caller, data, Operation.FINISH);
		for (Map<Object, Object> m : maps) {
			id = m.get("sd_id");
			handlerService.handler("Sale", "enddetail", "after", new Object[] { id });
		}
	}

	@Override
	public void resetQty(String tab) {
		baseDao.procedure("SP_RESETYQTY", new Object[] { tab });
	}

	@Override
	public void vastClosePurchaseDetail(String language, Employee employee, String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Object id, closeReason = null;
		String adidstr = "";
		for (Map<Object, Object> map : maps) {
			adidstr += "," + map.get("pd_id").toString();
		}
		if (!adidstr.equals("")) {
			adidstr = adidstr.substring(1);
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat('采购单号：'||pd_code||'序号：'||pd_detno) from PurchaseDetail where nvl(pd_yqty,0)>nvl(pd_acceptqty,0)+nvl(pd_ngacceptqty,0) and pd_id in ("
									+ adidstr + ")", String.class);
			if (dets != null) {
				BaseUtil.showError("存在已收料未入库的情况，不允许进行结案操作！" + dets);
			}

			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(distinct '采购单号：'||pd_code||'序号：'||pd_detno) from purchasedetail where pd_id in ("
									+ adidstr
									+ ") and exists (select 1 from ProdIODetail left join ProdInOut on pd_piid=pi_id where pd_ordercode=pd_code and pd_orderdetno=pd_detno and pi_statuscode<>'POSTED' and pd_piclass='采购验退单')",
							String.class);
			if (dets != null) {
				BaseUtil.showError("存在采购验退单未过账的情况，不允许进行结案操作！" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(distinct '采购单号：'||pd_code||'序号：'||pd_detno) from purchasedetail where pd_id in ("
									+ adidstr
									+ ") and exists (select 1 from VerifyApplyChangeDetail left join VerifyApplyChange on vcd_vcid=vc_id where vcd_pucode=pd_code and vcd_pudetno=pd_detno and vc_statuscode<>'AUDITED')",
							String.class);
			if (dets != null) {
				BaseUtil.showError("存在收料变更单未审核，不允许进行结案操作！" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(distinct '采购单号：'||pd_code||'序号：'||pd_detno) from purchasedetail where pd_id in ("
									+ adidstr
									+ ") and exists (select 1 from PurchaseChangeDetail left join PurchaseChange on pcd_pcid=pc_id where pc_purccode=pd_code and pcd_pddetno=pd_detno and pc_statuscode not in ('AUDITED','CONFIRMED'))",
							String.class);
			if (dets != null) {
				BaseUtil.showError("存在采购变更单未审核，不允许进行结案操作！" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(distinct '采购单号：'||pd_code||'序号：'||pd_detno) from purchasedetail where pd_id in ("
									+ adidstr
									+ ") and exists (select 1 from purchasenotify where pn_ordercode=pd_code and pn_orderdetno=pd_detno and pn_qty-NVL(pn_endqty,0)>0 and pn_statuscode<>'CANCELED')",
							String.class);
			if (dets != null) {
				BaseUtil.showError("存在未交货送货提醒，不允许进行结案操作！" + dets);
			}
		}
		for (Map<Object, Object> m : maps) {
			id = m.get("pd_id");
			closeReason = m.get("pd_textbox");
			// pd_textbox 结案原因
			if (m.containsKey("pd_textbox") && (closeReason == null || "".equals(closeReason.toString()))) {
				BaseUtil.showError("结案原因没有填写,结案失败!");
			}
			if (closeReason != null && !"".equals(closeReason.toString())) {
				baseDao.updateByCondition("purchasedetail", "pd_textbox='" + closeReason + "'", "pd_id=" + id);
			}
			baseDao.updateByCondition("purchasedetail", "pd_endstatus='待上传'", "pd_id=" + id);
		}
		dealdetail(language, employee, caller, data, Operation.FINISH);
	}

	@Override
	public String refreshSync(Employee employee, String caller, String to, String data) {
		return baseDao.callProcedure("SYS_POST",
				new Object[] { caller, SpObserver.getSp(), to, data, employee.getEm_name()+"("+employee.getEm_code()+")", employee.getEm_id() });
	}

	@Override
	public void vastFreezePurchaseDetail(String language, Employee employee, String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Object id, closeReason = null;
		String adidstr = "";
		for (Map<Object, Object> map : maps) {
			adidstr += "," + map.get("pd_id").toString();
		}
		if (maps.get(0).get("pd_tqty") != null) {// 冻结数量
			List<String> sqls = new ArrayList<String>();
			Set<Integer> puids = new HashSet<Integer>();
			purchaseDao.updatePurcYNotifyQTY(0, adidstr.substring(1));
			for (Map<Object, Object> map : maps) {
				SqlRowList rs = baseDao
						.queryForRowSet(
								"select pd_puid,pd_code,pd_detno,nvl(pd_qty,0) pd_qty,nvl(pd_yqty,0) pd_yqty,nvl(pd_turnqty,0) pd_turnqty,nvl(pd_frozenqty,0) pd_frozenqty,nvl(pd_qty,0)-nvl(pd_yqty,0)-nvl(pd_turnqty,0)-nvl(pd_frozenqty,0) qty from PURCHASEDETAIL  where  pd_id=?",
								map.get("pd_id"));
				if (rs.next()) {
					if (rs.getDouble("qty") < Double.parseDouble(map.get("pd_tqty").toString())) {
						BaseUtil.showError("采购单号：" + rs.getObject("pd_code") + " 采购序号：" + rs.getObject("pd_detno")
								+ " 本次冻结数量大于可冻结数量!<br>可冻结数量：" + rs.getObject("qty") + ",订单数量：" + rs.getObject("pd_qty") + ",已收料数量:"
								+ rs.getObject("pd_yqty") + ",已投送货提醒数:" + rs.getObject("pd_turnqty") + ",已冻结数量:"
								+ rs.getObject("pd_frozenqty"));
					}
					closeReason = map.get("pd_textbox");
					// pd_textbox 冻结原因
					if (map.containsKey("pd_textbox") && (closeReason == null || "".equals(closeReason.toString()))) {
						BaseUtil.showError("冻结原因没有填写,冻结失败!");
					}
					puids.add(rs.getInt("pd_puid"));
					// 更新冻结数量
					sqls.add("update PURCHASEDETAIL set pd_frozenqty=nvl(pd_frozenqty,0)+" + map.get("pd_tqty") + " where pd_id="
							+ map.get("pd_id"));// dyl
					if (closeReason != null && !"".equals(closeReason.toString())) {// 更新结案原因
						sqls.add("update purchasedetail set pd_textbox='" + closeReason + "' where pd_id=" + map.get("pd_id"));
					}
					// 插入日志
					sqls.add("insert into messagelog (ml_date,ml_man,ml_content,ml_result,ml_search) values (sysdate,'"
							+ employee.getEm_name() + "','冻结操作','冻结成功！行" + rs.getObject("pd_detno") + ",数量：" + map.get("pd_tqty")
							+ "','Purchase|pu_id=" + rs.getObject("pd_puid") + "')");
				}
			}
			baseDao.execute(sqls);
			// 更新明细状态
			baseDao.updateByCondition("PURCHASEDETAIL", "pd_mrpstatuscode='FREEZE',pd_mrpstatus='已冻结'",
					"nvl(pd_qty,0)=nvl(pd_yqty,0)+nvl(pd_turnqty,0)+nvl(pd_frozenqty,0) and pd_id in(" + adidstr.substring(1) + ")");
			int fCount = 0, count = 0;
			for (Integer puid : puids) {// 更新采购单主表状态
				count = baseDao.getCountByCondition("purchasedetail", "pd_puid=" + puid);
				fCount = baseDao.getCountByCondition("purchasedetail", "pd_puid=" + puid + " AND pd_mrpstatuscode='FREEZE'");
				if (fCount == count) {
					baseDao.updateByCondition("purchase", "pu_status='已冻结',pu_statuscode='FREEZE'", "pu_id=" + puid);
				}
			}
		} else {// 整行冻结
			if (!adidstr.equals("")) {
				adidstr = adidstr.substring(1);
				String dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat('采购单号：'||pd_code||'序号：'||pd_detno) from PurchaseDetail where nvl(pd_yqty,0)>nvl(pd_acceptqty,0)+nvl(pd_ngacceptqty,0) and pd_id in ("
										+ adidstr + ")", String.class);
				if (dets != null) {
					BaseUtil.showError("存在已收料未入库的情况，不允许进行冻结操作！" + dets);
				}
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(distinct '采购单号：'||pd_code||'序号：'||pd_detno) from purchasedetail where pd_id in ("
										+ adidstr
										+ ") and exists (select 1 from VerifyApplyChangeDetail left join VerifyApplyChange on vcd_vcid=vc_id where vcd_pucode=pd_code and vcd_pudetno=pd_detno and vc_statuscode<>'AUDITED')",
								String.class);
				if (dets != null) {
					BaseUtil.showError("存在收料变更单未审核，不允许进行冻结操作！" + dets);
				}
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(distinct '采购单号：'||pd_code||'序号：'||pd_detno) from purchasedetail where pd_id in ("
										+ adidstr
										+ ") and exists (select 1 from PurchaseChangeDetail left join PurchaseChange on pcd_pcid=pc_id where pc_purccode=pd_code and pcd_pddetno=pd_detno and pc_statuscode not in ('AUDITED','CONFIRMED'))",
								String.class);
				if (dets != null) {
					BaseUtil.showError("存在采购变更单未审核，不允许进行冻结操作！" + dets);
				}
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(distinct '采购单号：'||pd_code||'序号：'||pd_detno) from purchasedetail where pd_id in ("
										+ adidstr
										+ ") and exists (select 1 from purchasenotify where pn_ordercode=pd_code and pn_orderdetno=pd_detno and pn_qty-NVL(pn_endqty,0)>0 and pn_statuscode<>'CANCELED')",
								String.class);
				if (dets != null) {
					BaseUtil.showError("存在未交货送货提醒，不允许进行冻结操作！" + dets);
				}
			}
			for (Map<Object, Object> m : maps) {
				id = m.get("pd_id");
				closeReason = m.get("pd_textbox");
				// pd_textbox 冻结原因
				if (m.containsKey("pd_textbox") && (closeReason == null || "".equals(closeReason.toString()))) {
					BaseUtil.showError("冻结原因没有填写,冻结失败!");
				}
				if (closeReason != null && !"".equals(closeReason.toString())) {
					baseDao.updateByCondition("purchasedetail",
							"pd_frozenqty=nvl(pd_qty,0)-nvl(pd_yqty,0)-nvl(pd_turnqty,0)-nvl(pd_frozenqty,0),pd_textbox='" + closeReason
									+ "'", "pd_id=" + id);
				}
			}
			dealdetail(language, employee, caller, data, Operation.FREEZE);
		}
	}

	@Override
	public void vastFreezeSaleDetail(String language, Employee employee, String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Object id, closeReason = null;
		String adidstr = "";
		for (Map<Object, Object> map : maps) {
			adidstr += "," + map.get("sd_id").toString();
		}
		if (!adidstr.equals("")) {
			adidstr = adidstr.substring(1);
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(distinct '销售单号：'||sd_code||'序号：'||sd_detno) from SaleDetail where sd_id in ("
									+ adidstr
									+ ") and exists (select 1 from SendNotifyDetail where snd_ordercode=sd_code and snd_orderdetno=sd_detno and nvl(snd_yqty,0)<nvl(snd_outqty,0) and nvl(snd_statuscode,' ')<>'FINISH')",
							String.class);
			if (dets != null) {
				BaseUtil.showError("存在出货通知明细行状态不等于已结案且未全部转，不允许进行冻结操作！" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(distinct '销售单号：'||sd_code||'序号：'||sd_detno) from SaleDetail where sd_id in ("
									+ adidstr
									+ ") and exists (select 1 from ProdIODetail where pd_ordercode=sd_code and pd_orderdetno=sd_detno and nvl(pd_status,0)=0 and pd_piclass in ('出货单','销售退货单'))",
							String.class);
			if (dets != null) {
				BaseUtil.showError("存在出货单或者销售退货单未过账，不允许进行冻结操作！" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(distinct '销售单号：'||sd_code||'序号：'||sd_detno) from SaleDetail where sd_id in ("
									+ adidstr
									+ ") and exists (select 1 from SendNotifyChangeDetail left join SendNotifyChange on scd_scid=sc_id left join SendNotifydetail on scd_sndid=snd_id where snd_ordercode=sd_code and snd_orderdetno=sd_detno and sc_statuscode<>'AUDITED')",
							String.class);
			if (dets != null) {
				BaseUtil.showError("存在通知变更单未审核，不允许进行冻结操作！" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(distinct '销售单号：'||sd_code||'序号：'||sd_detno) from SaleDetail where sd_id in ("
									+ adidstr
									+ ") and exists (select 1 from SaleChangeDetail left join SaleChange on scd_scid=sc_id where scd_sacode=sd_code and scd_sddetno=sd_detno and sc_statuscode<>'AUDITED')",
							String.class);
			if (dets != null) {
				BaseUtil.showError("存在销售变更单未审核，不允许进行冻结操作！" + dets);
			}
		}
		for (Map<Object, Object> m : maps) {
			id = m.get("sd_id");
			closeReason = m.get("sd_barcode");
			// pd_textbox 冻结原因
			if (m.containsKey("sd_barcode") && (closeReason == null || "".equals(closeReason.toString()))) {
				BaseUtil.showError("冻结原因没有填写,冻结失败!");
			}
			if (closeReason != null && !"".equals(closeReason.toString())) {
				baseDao.updateByCondition("SaleDetail", "sd_barcode='" + closeReason + "'", "sd_id=" + id);
			}
		}
		dealdetail(language, employee, caller, data, Operation.FREEZE);
	}

	@Override
	public void vastCloseSaleForecastDetail(String language, Employee employee, String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Object id, closeReason = null;
		String ids = CollectionUtil.pluckSqlString(maps, "sd_id");
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(distinct '预测单号：'||sd_code||'序号：'||sd_detno) from saleForecastdetail where sd_id in ("
								+ ids
								+ ") and exists (select 1 from SaleDetail left join Sale on sd_said=sa_id where SaleDetail.sd_forecastcode=saleForecastdetail.sd_code and SaleDetail.sd_forecastdetno=saleForecastdetail.sd_detno and sa_statuscode<>'AUDITED' and nvl(SaleDetail.sd_forecastcode,' ')<>' ')",
						String.class);
		if (dets != null) {
			BaseUtil.showError("存在销售订单未审核，不允许进行结案操作！" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(distinct '预测单号：'||sd_code||'序号：'||sd_detno) from saleForecastdetail where sd_id in ("
								+ ids
								+ ") and exists (select 1 from SaleClashDetail left join SaleClash on scd_scid=sc_id where scd_ordercode=sd_code and scd_orderdetno=sd_detno and sc_statuscode<>'AUDITED')",
						String.class);
		if (dets != null) {
			BaseUtil.showError("存在预测冲销单未审核，不允许进行结案操作！" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(distinct '预测单号：'||sd_code||'序号：'||sd_detno) from SaleForecastDetail a where sd_id in ("
								+ ids
								+ ") and exists (select 1 from saleForecastdetail b left join SaleForecast on b.sd_sfid=sf_id where b.sd_forecastcode=a.sd_code and b.sd_forecastdetno=a.sd_detno and nvl(b.sd_forecastcode,' ')<>' ' and sf_statuscode<>'AUDITED')",
						String.class);
		if (dets != null) {
			BaseUtil.showError("存在销售预测单未审核，不允许进行结案操作！" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(distinct '预测单号：'||sd_code||'序号：'||sd_detno) from saleForecastdetail where sd_id in ("
								+ ids
								+ ") and exists (select 1 from SaleForeCastChangeDetail left join SaleForeCastChange on scd_mainid=sc_id where sc_sfcode=sd_code and scd_pddetno=sd_detno and sc_statuscode<>'AUDITED')",
						String.class);
		if (dets != null) {
			BaseUtil.showError("存在销售预测单变更单未审核，不允许进行结案操作！" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(distinct '预测单号：'||pd_plancode||'序号：'||pd_forecastdetno||'类型：'||pd_piclass) from ProdIODetail where nvl(pd_plancode,' ')<>' ' and pd_status=0 and exists (select 1 from saleForecastdetail where pd_plancode=sd_code and pd_forecastdetno=sd_detno and sd_id in ("
								+ ids + "))", String.class);
		if (dets != null) {
			BaseUtil.showError("存在出入库单未过账，不允许进行结案操作！" + dets);
		}
		for (Map<Object, Object> m : maps) {
			id = m.get("sd_id");
			handlerService.handler("SaleForecast", "enddetail", "before", new Object[] { id });
		}
		for (Map<Object, Object> m : maps) {
			id = m.get("sd_id");
			closeReason = m.get("sd_barcode");
			// pd_textbox 冻结原因
			if (m.containsKey("sd_barcode") && (closeReason == null || "".equals(closeReason.toString()))) {
				BaseUtil.showError("结案原因没有填写,结案失败!");
			}
			if (closeReason != null && !"".equals(closeReason.toString())) {
				baseDao.updateByCondition("SaleForecastDetail", "sd_barcode='" + closeReason + "'", "sd_id=" + id);
			}
		}
		dealdetail(language, employee, caller, data, Operation.FINISH);
		for (Map<Object, Object> m : maps) {
			id = m.get("sd_id");
			handlerService.handler("SaleForecast", "enddetail", "after", new Object[] { id });
		}
	}

	@Override
	public void vastCloseSendNotifyDetail(String language, Employee employee, String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Object id, closeReason = null;
		String ids = CollectionUtil.pluckSqlString(maps, "snd_id");
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(distinct '通知单号：'||sn_code||'序号：'||snd_pdno) from SendNotifyDetail left join SendNotify on snd_snid=sn_id where snd_statuscode<>'AUDITED' and snd_id in ("
								+ ids + ")", String.class);
		if (dets != null) {
			BaseUtil.showError("存在通知单明细行状态不等于审核，不允许进行结案操作！" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(distinct '通知单号：'||snd_code||'序号：'||snd_pdno) from ProdIODetail left join SendNotifyDetail on pd_orderid=snd_id where nvl(pd_status,0)=0 and nvl(pd_snid,0)>0 and pd_piclass in ('出货单','其它出库单','拨出单','换货出库单') and pd_orderid in ("
								+ ids + ")", String.class);
		if (dets != null) {
			BaseUtil.showError("存在出货单、其它出库单、拨出单、换货出库单未过账，不允许进行结案操作！" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(distinct '通知单号：'||sc_sncode||'序号：'||scd_snddetno) from SendNotifyChangeDetail left join SendNotifyChange on scd_scid=sc_id left join SendNotifydetail on scd_sndid=snd_id where sc_statuscode<>'AUDITED' and scd_sndid in ("
								+ ids + ")", String.class);
		if (dets != null) {
			BaseUtil.showError("存在通知变更单未审核，不允许进行结案操作！" + dets);
		}
		for (Map<Object, Object> m : maps) {
			id = m.get("snd_id");
			closeReason = m.get("snd_barcode");
			// pd_textbox 冻结原因
			if (m.containsKey("snd_barcode") && (closeReason == null || "".equals(closeReason.toString()))) {
				BaseUtil.showError("结案原因没有填写,结案失败!");
			}
			if (closeReason != null && !"".equals(closeReason.toString())) {
				baseDao.updateByCondition("SendNotifyDetail", "snd_barcode='" + closeReason + "'", "snd_id=" + id);
			}
		}
		dealdetail(language, employee, caller, data, Operation.FINISH);
	}

	@Override
	public String getFormHelpDoc(String caller) {
		// TODO Auto-generated method stub
		Form form = formDao.getForm(caller, SpObserver.getSp());
		if (form != null && StringUtils.hasText(form.getFo_helpdoc())) {
			int attachId = Integer.parseInt(form.getFo_helpdoc().split(";")[0]);
			JSONObject obj = formAttachDao.getFilePath(attachId);
			String path = obj.getString("fp_path");
			String fileName = obj.getString("fp_name");
			return WordToHtml.getWord(path, caller, fileName);
		}
		return "";
	}

	/**
	 * 这个方法和
	 * {@link SingleFormItemsServiceImpl#updateByCondition(String, String, String, Employee, String, String)}
	 * 基本一致 主要是在记录日志的,把修改的附件名记录下来,比原来的更详细点
	 */
	@Override
	public void updateAttachField(String table, String update, String condition, Employee employee, String type, String caller) {
		if (type != null || !"".equals(type)) {
			String oldFieldValue = baseDao.getFieldDataByCondition(table, update.split("=")[0], condition) + "";
			String newFieldValue = update.split("=")[1].replaceAll("'", "");
			List<String> changeAttach = null;
			if ("添加附件".equals(type)) {
				changeAttach = new ArrayList<String>(Arrays.asList(newFieldValue.split(";")));
				changeAttach.removeAll(Arrays.asList(oldFieldValue.split(";")));
			} else if ("删除附件".equals(type)) {
				//判断是否有权限删除附件
				checkDeleteAttachPower(condition.split("=")[1].replaceAll("'", "") , employee , caller);
				// 限制单据状态为已审核，不允许删除
				if (!"admin".equals(employee.getEm_type()) && !checkDelete_Attach(caller, condition.split("=")[1], employee))
					BaseUtil.showError("您没有删除该附件的权限!");
				changeAttach = new ArrayList<String>(Arrays.asList(oldFieldValue.split(";")));
				changeAttach.removeAll(Arrays.asList(newFieldValue.split(";")));
			}
			if (changeAttach != null) {
				for (String attachID : changeAttach) {
					baseDao.logMessage(new MessageLog(employee.getEm_name(), type + "操作,附件名为:"
							+ baseDao.getFieldDataByCondition("filepath", "fp_name", "fp_id=" + attachID), type + "成功", caller + "|"
							+ condition.replaceAll("'","")));				
				}
			}
		}
		baseDao.updateByCondition(table, update, condition);
	}

	private boolean checkDelete_Attach(String caller, String id, Employee employee) {
		SysSpecialPower power = powerDao.getSysSPower(caller, "@/ERP/common/attach/change.action", employee.getEm_master());
		if (power == null)
			return true;
		Form form = formDao.getForm(caller, employee.getEm_master());
		if (form != null && form.getFo_statuscodefield() != null) {
			String status = baseDao.getFieldValue(form.getFo_table(), form.getFo_statuscodefield(), form.getFo_keyfield() + "=" + id,
					String.class);
			if (Status.AUDITED.code().equals(status)) {
				boolean bool = powerDao.getSpecialPowerByActionId(String.valueOf(power.getSsp_id()), employee.getEm_defaulthsid(),
						employee.getEm_master());
				if (!bool && employee.getEmpsJobs() != null) {
					// 按员工岗位关系取查找权限
					for (EmpsJobs empsJob : employee.getEmpsJobs()) {
						bool = powerDao.getSpecialPowerByActionId(String.valueOf(power.getSsp_id()), empsJob.getJob_id(),
								employee.getEm_master());
					}
				}
				return bool;
			}
		}
		return true;
	}

	@Override
	public String getDemoUrl(String caller, String master, String webpath) {
		Form form = formDao.getForm(caller, SpObserver.getSp());
		DataList datalist = dataListDao.getDataList(caller, SpObserver.getSp());
		String condition = "1=1";
		if (datalist != null) {
			condition = datalist.getDl_condition() != null ? datalist.getDl_condition() : condition;
		} else
			BaseUtil.showError("与模板系统配置不一致，无法获取！");
		Integer keyValue = baseDao.getFieldValue(form.getFo_table(), form.getFo_keyfield(), condition, Integer.class);
		if (keyValue != null) {
			StringBuffer buf = new StringBuffer();
			buf.append(webpath);
			String lockpage = datalist.getDl_lockpage().trim();
			buf.append(lockpage);
			if (lockpage.indexOf("?") > 0) {
				buf.append("&");
			} else
				buf.append("?");
			buf.append("formCondition=" + form.getFo_keyfield() + "IS" + keyValue);
			buf.append("&gridCondition=" + form.getFo_detailmainkeyfield() + "IS" + keyValue);
			buf.append("&master=" + master);
			buf.append("&_nobutton=1");
			buf.append("&_timestamp=").append(System.currentTimeMillis());
			String message = buf.toString();
			buf.append("&_signature=").append(HmacUtils.encode(message));
			return buf.toString();
		} else
			BaseUtil.showError("暂未找到相应的模板数据！");
		return "";

	}

	@Override
	public String getDemoWebSite(String caller) {
		// TODO Auto-generated method stub
		Map<String, String> map = getDemoSite();
		StringBuffer buf = new StringBuffer();
		buf.append(map.get("url")).append("demo/skipDemo.action?caller=").append(caller).append("&master=").append(map.get("master"));
		buf.append("&_timestamp=").append(System.currentTimeMillis());
		String message = buf.toString();
		buf.append("&_signature=").append(HmacUtils.encode(message));
		return buf.toString();
	}

	private Map<String, String> getDemoSite() {
		/**
		 * 先暂时默认UAS演示,后期再考虑多个不同版本演示的问题
		 * */
		Map<String, String> map = new HashMap<String, String>();
		map.put("url", "http://218.17.158.219:8090/ERP/");
		map.put("master", "UAS_DEMO");
		return map;
	}

	@Override
	public JSONObject getSearchSummary(Integer id, String condition) {
		RelativeSearch search = formDao.getRelativeSearch(id, SpObserver.getSp());
		if (search != null) {
			StringBuffer colFields = new StringBuffer();
			StringBuffer sqlFields = new StringBuffer();
			for (RelativeSearch.Grid grid : search.getGrids()) {
				if (StringUtils.hasText(grid.getRsg_sumtype())) {
					if (sqlFields.length() > 0) {
						sqlFields.append(",");
						colFields.append(",");
					}
					String sqlField = grid.getRsg_field();
					String colField = grid.getRsg_field();
					if (colField.contains(" ")) {
						String[] strs = colField.split(" ");
						colField = strs[strs.length - 1];
						sqlField = sqlField.substring(0, sqlField.lastIndexOf(" "));
					}
					// 防保留字符，别名注意使用双引号
					sqlFields.append(grid.getRsg_sumtype()).append("(").append(sqlField).append(") \"").append(colField).append("\"");
					colFields.append("\"").append(colField).append("\"");
				}
			}
			if (colFields.length() > 0) {
				String sql = RelativeSearch.getSummarySql(search.getRs_table(), baseDao.parseEmpCondition(condition), colFields.toString(),
						sqlFields.toString(), search.getRs_groupby());
				SqlRowList rs = baseDao.queryForRowSet(sql);
				String[] fs = BaseUtil.parseStr2Array(colFields.toString().replace("\"", ""), ",");
				JSONObject obj = new JSONObject();
				if (rs.next()) {
					for (String f : fs) {
						obj.put(f, SqlRowList.parseValue(rs.getObject(f)));
					}
				}
				return obj;
			}
		}
		return null;
	}

	@Override
	public List<RelativeSearchLimit> getRelativeSearchLimitsByEmpl(String caller, Integer em_id) {
		// 单据关联查询是否需要进行权限管控
		boolean limit = baseDao.isDBSetting("relativeSearchLimit");
		// 勾选了该参数才允许编辑权限
		return limit ? formDao.getRelativeSearchLimitsByEmpl(caller, em_id, SpObserver.getSp()) : null;
	}

	@Override
	public List<RelativeSearchLimit> getRelativeSearchLimitsByJob(String caller, Integer jo_id) {
		// 单据关联查询是否需要进行权限管控
		boolean limit = baseDao.isDBSetting("relativeSearchLimit");
		// 勾选了该参数才允许编辑权限
		return limit ? formDao.getRelativeSearchLimitsByJob(caller, jo_id, SpObserver.getSp()) : null;
	}

	@Override
	public List<RelativeSearch> getRelativeSearchForPower(String caller) {
		// 单据关联查询是否需要进行权限管控
		boolean limit = baseDao.isDBSetting("relativeSearchLimit");
		return limit ? formDao.getRelativeSearchs(caller, SpObserver.getSp()) : null;
	}

	@Override
	@Transactional
	@CacheEvict(value = "relativesearch", allEntries = true)
	public void saveRelativeSearchLimit(String limits, int id, Boolean _self) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(limits);
		if (!CollectionUtil.isEmpty(store)) {
			String field = _self ? "rsl_emid" : "rsl_joid";
			List<String> sqls = new ArrayList<String>();
			String caller = store.get(0).get("rsl_caller").toString();
			for (Map<Object, Object> m : store) {
				Boolean bool = (Boolean) m.get("checked");
				if (bool) {
					m.remove("checked");
					m.put(field, id);
					sqls.add(SqlUtil.getInsertSqlByMap(m, "RelativeSearchLimit"));
				}
			}
			baseDao.execute("delete from RelativeSearchLimit where " + field + "=? and rsl_caller=?", id, caller);
			if (sqls.size() > 0) {
				baseDao.execute(sqls);
			}
		}
	}

	@Override
	public void vastResFreezePurchaseDetail(String language, Employee employee, String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String adidstr = "";
		for (Map<Object, Object> map : maps) {
			adidstr += "," + map.get("pd_id").toString();
		}
		List<String> sqls = new ArrayList<String>();
		Set<Integer> puids = new HashSet<Integer>();
		purchaseDao.updatePurcYNotifyQTY(0, adidstr.substring(1));
		for (Map<Object, Object> map : maps) {
			SqlRowList rs = baseDao.queryForRowSet(
					"select pd_puid,pd_code,pd_detno,nvl(pd_frozenqty,0) pd_frozenqty from PURCHASEDETAIL  where  pd_id=?",
					map.get("pd_id"));
			if (rs.next()) {
				if (rs.getDouble("pd_frozenqty") < Double.parseDouble(map.get("qty").toString())) {
					BaseUtil.showError("采购单号：" + rs.getObject("pd_code") + " 采购序号：" + rs.getObject("pd_detno")
							+ " 本次取消冻结数量必须小于等于已冻结数量!<br>可取消冻结数量：" + rs.getObject("pd_turnqty"));
				}
				puids.add(rs.getInt("pd_puid"));
				// 更新冻结数量
				sqls.add("update PURCHASEDETAIL set pd_frozenqty=nvl(pd_frozenqty,0)-" + map.get("qty") + " where pd_id="
						+ map.get("pd_id"));
				// 插入日志
				sqls.add("insert into messagelog (ml_date,ml_man,ml_content,ml_result,ml_search) values (sysdate,'" + employee.getEm_name()
						+ "','取消冻结操作','取消冻结成功！行" + rs.getObject("pd_detno") + ",数量：" + map.get("qty") + "','Purchase|pu_id="
						+ rs.getObject("pd_puid") + "')");
			}
		}
		baseDao.execute(sqls);
		// 更新明细状态
		baseDao.updateByCondition("PURCHASEDETAIL", "pd_mrpstatuscode='AUDITED',pd_mrpstatus='已审核'",
				"nvl(pd_qty,0)<>nvl(pd_yqty,0)+nvl(pd_turnqty,0)+nvl(pd_frozenqty,0) and pd_id in(" + adidstr.substring(1) + ")");
		int fCount = 0, count = 0;
		for (Integer puid : puids) {// 更新采购单主表状态
			count = baseDao.getCountByCondition("purchasedetail", "pd_puid=" + puid);
			fCount = baseDao.getCountByCondition("purchasedetail", "pd_puid=" + puid + " AND pd_mrpstatuscode='FREEZE'");
			if (fCount != count) {
				baseDao.updateByCondition("purchase", "pu_status='已审核',pu_statuscode='AUDITED'", "pu_id=" + puid);
			}
		}
	}

	@Override
	public List<Map<String,Object>> getMessageInfo(String caller, String id) {
		String sqlString="select * from icqhistorydetail left join icqhistory on ihd_ihid=ih_id where IH_CALLER='"+caller+"' and IH_KEYVALUE='"+id+"' ORDER by ih_date asc";		
		return baseDao.queryForList(sqlString);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String,Object> getLogicMessageLogs(String caller,String context,int page,int limit) {
		int start =(page-1)*limit;
		int end=page*limit;
		Map<String,Object> logMap=new HashMap<String,Object>();
		List<Map<String,Object>> logs=null;
		String condition=StringUtil.hasText(context)?"ml_search ='"+caller+"|Config Caller' and ml_result like '%"+context.replace("'", "\"")+"%'":"ml_search ='"+caller+"|Config Caller'";
		int count = baseDao.getCount("select count(*) from MessageLog where "+condition);
		try {
			logs=baseDao.getJdbcTemplate().query(
					"select * from (select Tab.*,rownum rn  from (select * from MessageLog where "+condition+" order by ml_date desc) Tab where rownum <="+end+") where rn>"+start+"",
					new BeanPropertyRowMapper(MessageLog.class));
		} catch (EmptyResultDataAccessException e) {
		}
		logMap.put("num",count);
		logMap.put("logs",logs);
		return logMap;
	}

	@Override
	public void vastBanned(String language, Employee employee, String caller,String data) {
		deal(language, employee, caller, data, Operation.BANNED);
	}

	@Override
	public void vastResBanned(String language, Employee employee,String caller, String data) {
		deal(language, employee, caller, data, Operation.RESBANNED);
	}

	@Override
	public void afterQuery(String caller, String condition, Employee employee) {
		handlerService.handler(caller, "query", "after", new Object[] { caller,condition });
	}

	@Override
	public void vastCloseApplicationDetail(String language, Employee employee,
			String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Object id, closeReason = null;
		boolean bool = baseDao.isDBSetting("Application!Close!Deal", "updateQty");
		for (Map<Object, Object> m : maps) {
			id = m.get("ad_id");
			closeReason = m.get("ad_reason");
			if (m.containsKey("ad_reason") && (closeReason == null || "".equals(closeReason.toString()))) {
				BaseUtil.showError("结案原因没有填写,结案失败!");
			}
			if (closeReason != null && !"".equals(closeReason.toString())) {
				baseDao.updateByCondition("applicationdetail", "ad_reason='" + closeReason + "'", "ad_id=" + id);
			}
			if(bool){
				baseDao.updateByCondition("applicationdetail", "ad_oldqty=ad_qty", "ad_id=" + id);
				baseDao.updateByCondition("applicationdetail", "ad_qty=ad_yqty", "ad_id=" + id);
			}
		}
		dealdetail(language, employee, caller, data, Operation.FINISH);
	}
	
	/**
	 * 判断是否有删除附件的权限（修改单据的权限）
	 */
	private void checkDeleteAttachPower(String id , Employee employee , String caller){
		if (!"admin".equals(employee.getEm_type())) {
			boolean bool = checkJobPower(caller, PositionPower.SAVE, employee);
			if (!bool) {
				bool = powerDao.getSelfPowerByType(caller, PersonalPower.SAVE, employee);
				if (!bool) {
					BaseUtil.showError("ERR_POWER_023:您没有<修改>该单据的权限!");
				} else {
					if (id != null&&!id.equals("")) {
						bool = powerDao.getOtherSelfPowerByType(caller, Integer.parseInt(id), PositionPower.SAVE_OTHER, employee);
						if (!bool) {
							BaseUtil.showError("ERR_POWER_024:您没有<修改他人>单据的权限!");
						} 
					}
				}
			} else {
				if (id != null&&!id.equals("")) {
					bool = powerDao.getOtherPowerByType(caller, Integer.parseInt(id), PositionPower.SAVE_OTHER, employee);
					if (!bool) {
						BaseUtil.showError("ERR_POWER_024:您没有<修改他人>单据的权限!");
					}
				}
			}
		}
	}
	private boolean checkJobPower(String caller, String powerType, Employee employee) {
		String sob = employee.getEm_master();
		// 默认岗位设置
		boolean bool = powerDao.getPowerByType(caller, powerType, sob, employee.getEm_defaulthsid());
		if (!bool && employee.getEmpsJobs() != null) {
			// 按员工岗位关系取查找权限
			for (EmpsJobs empsJob : employee.getEmpsJobs()) {
				bool = powerDao.getPowerByType(caller, powerType, sob, empsJob.getJob_id());
				if (bool)
					break;
			}
		}
		return bool;
	}
	
	@Override
	public String specialPost(String language, Employee employee, String caller, String to, String data) {
		String res = null;
		if ("true".equals(BaseUtil.getXmlSetting("group"))) {
			String dataCenter = BaseUtil.getXmlSetting("dataSob");
			if (!SpObserver.getSp().equals(dataCenter)) {
				boolean isBase = baseDao.checkIf("basedataset", "bds_caller='" + caller.replace("!Post", "")
						+ "' and nvl(bds_editable,0)=1 and bds_caller<>'PositionPower'");
				if (isBase) {
					return "不允许在营运中心同步该资料!";
				}
			}
		}
		res = baseDao.callProcedure("SPECIAL_POST",
				new Object[] { caller, SpObserver.getSp(), to, data, employee.getEm_name(), employee.getEm_id() });
		return res;
	}

	@Override
	public void BusinessTripOpen(String emcodes) {
		if(emcodes.length()>0) {
			baseDao.callProcedure("BUSINESSTRIPOPEN", emcodes);
		}
	}

}
