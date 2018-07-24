package com.uas.erp.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DbfindSetDao;
import com.uas.erp.dao.common.DbfindSetUiDao;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.dao.common.FormDao;
import com.uas.erp.dao.common.PowerDao;
import com.uas.erp.model.DBFindSet;
import com.uas.erp.model.DBFindSetUI;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Form;
import com.uas.erp.model.FormDetail;
import com.uas.erp.model.Interceptors;
import com.uas.erp.service.common.DbfindService;
import com.uas.erp.service.common.DocumentSetService;
import com.uas.erp.service.common.ProcessService;
import com.uas.erp.service.ma.InterceptorService;

/**
 * 读取配置，调用不同业务逻辑
 */
@Component
public class HandlerService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private InterceptorService interceptorService;
	@Autowired
	private PowerDao powerDao;
	@Autowired
	private ProcessService processService;
	@Autowired
	private FormDao formDao;
	@Autowired
	private DbfindSetUiDao dbfindSetUiDao;
	@Autowired
	private DetailGridDao detailGridDao;
	@Autowired
	private DbfindSetDao dbfindSetDao;
	@Autowired
	private DbfindService dbfindService;
	@Autowired
	private DocumentSetService documentSetService;
	
	
	/**
	 * 调用保存前配置逻辑
	 * 
	 * @param caller
	 * @param args
	 */
	public void beforeSave(String caller, Object... args) {
		handler(caller, "save", "before", args);
	}

	/**
	 * 调用保存后配置逻辑
	 * 
	 * @param caller
	 * @param args
	 */
	public void afterSave(String caller, Object... args) {
		handler(caller, "save", "after", args);
	}

	/**
	 * 调用修改前配置逻辑
	 * 
	 * @param caller
	 * @param args
	 */
	public void beforeUpdate(String caller, Object... args) {
		handler(caller, "update", "before", args);
	}

	/**
	 * 调用修改后配置逻辑
	 * 
	 * @param caller
	 * @param args
	 */
	public void afterUpdate(String caller, Object... args) {
		handler(caller, "update", "after", args);
	}

	/**
	 * 调用删除前配置逻辑
	 * 
	 * @param caller
	 * @param keyValue
	 */
	public void beforeDel(String caller, Object keyValue) {
		Object[] args = new Object[] { keyValue };
		if (keyValue instanceof Object[])
			args = (Object[]) keyValue;
		handler(caller, "delete", "before", args);
	}

	/**
	 * 调用删除后配置逻辑
	 * 
	 * @param caller
	 * @param keyValue
	 */
	public void afterDel(String caller, Object keyValue) {
		Object[] args = new Object[] { keyValue };
		if (keyValue instanceof Object[])
			args = (Object[]) keyValue;
		handler(caller, "delete", "after", args);
	}

	/**
	 * 调用提交前配置逻辑
	 * 
	 * @param caller
	 * @param keyValue
	 */
	public void beforeSubmit(String caller, Object keyValue) {
		Object[] args = new Object[] { keyValue };
		if (keyValue instanceof Object[])
			args = (Object[]) keyValue;
		handler(caller, "commit", "before", args);
	}

	/**
	 * 调用提交后配置逻辑
	 * 
	 * @param caller
	 * @param args
	 */
	public void afterSubmit(String caller, Object keyValue) {
		Object[] args = new Object[] { keyValue };
		if (keyValue instanceof Object[])
			args = (Object[]) keyValue;
		handler(caller, "commit", "after", args);
	}

	/**
	 * 调用反提交前配置逻辑
	 * 
	 * @param caller
	 * @param keyValue
	 */
	public void beforeResSubmit(String caller, Object keyValue) {
		Object[] args = new Object[] { keyValue };
		if (keyValue instanceof Object[])
			args = (Object[]) keyValue;
		handler(caller, "resCommit", "before", args);
	}

	/**
	 * 调用反提交后配置逻辑
	 * 
	 * @param caller
	 * @param args
	 */
	public void afterResSubmit(String caller, Object keyValue) {
		Object[] args = new Object[] { keyValue };
		if (keyValue instanceof Object[])
			args = (Object[]) keyValue;
		handler(caller, "resCommit", "after", args);
	}

	/**
	 * 调用审核前配置逻辑
	 * 
	 * @param caller
	 * @param args
	 */
	public void beforeAudit(String caller, Object keyValue) {
		Object[] args = new Object[] { keyValue };
		if (keyValue instanceof Object[])
			args = (Object[]) keyValue;
		handler(caller, "audit", "before", args);
	}

	/**
	 * 调用审核后配置逻辑
	 * 
	 * @param caller
	 * @param args
	 */
	public void afterAudit(String caller, Object keyValue) {
		Object[] args = new Object[] { keyValue };
		if (keyValue instanceof Object[])
			args = (Object[]) keyValue;
		handler(caller, "audit", "after", args);
		triggerTask(caller, keyValue);
	}

	/**
	 * 调用反审核前配置逻辑
	 * 
	 * @param caller
	 * @param args
	 */
	public void beforeResAudit(String caller, Object keyValue) {
		Object[] args = new Object[] { keyValue };
		if (keyValue instanceof Object[])
			args = (Object[]) keyValue;
		handler(caller, "resAudit", "before", args);
	}

	/**
	 * 调用反审核后配置逻辑
	 * 
	 * @param caller
	 * @param args
	 */
	public void afterResAudit(String caller, Object keyValue) {
		Object[] args = new Object[] { keyValue };
		if (keyValue instanceof Object[])
			args = (Object[]) keyValue;
		handler(caller, "resAudit", "after", args);
	}

	/**
	 * 调用打印前配置逻辑
	 * 
	 * @param caller
	 * @param args
	 */
	public void beforePrint(String caller, Object keyValue) {
		Object[] args = new Object[] { keyValue };
		if (keyValue instanceof Object[])
			args = (Object[]) keyValue;
		handler(caller, "print", "before", args);
	}

	/**
	 * 调用打印后配置逻辑
	 * 
	 * @param caller
	 * @param args
	 */
	public void afterPrint(String caller, Object keyValue) {
		Object[] args = new Object[] { keyValue };
		if (keyValue instanceof Object[])
			args = (Object[]) keyValue;
		handler(caller, "print", "after", args);
	}

	/**
	 * 调用过账前配置逻辑
	 * 
	 * @param caller
	 * @param args
	 */
	public void beforePost(String caller, Object keyValue) {
		Object[] args = new Object[] { keyValue };
		if (keyValue instanceof Object[])
			args = (Object[]) keyValue;
		handler(caller, "post", "before", args);
	}

	/**
	 * 调用过账后配置逻辑
	 * 
	 * @param caller
	 * @param args
	 */
	public void afterPost(String caller, Object keyValue) {
		Object[] args = new Object[] { keyValue };
		if (keyValue instanceof Object[])
			args = (Object[]) keyValue;
		handler(caller, "post", "after", args);
	}

	/**
	 * 调用反过账前配置逻辑
	 * 
	 * @param caller
	 * @param args
	 */
	public void beforeResPost(String caller, Object keyValue) {
		Object[] args = new Object[] { keyValue };
		if (keyValue instanceof Object[])
			args = (Object[]) keyValue;
		handler(caller, "resPost", "before", args);
	}

	/**
	 * 调用反过账后配置逻辑
	 * 
	 * @param caller
	 * @param args
	 */
	public void afterResPost(String caller, Object keyValue) {
		Object[] args = new Object[] { keyValue };
		if (keyValue instanceof Object[])
			args = (Object[]) keyValue;
		handler(caller, "resPost", "after", args);
	}

	/**
	 * @param caller
	 *            表名或表的标志
	 * @param type
	 *            save、delete、commit、audit、write、unwrite
	 * @param turn
	 *            after、before
	 * @param args
	 *            参数
	 */
	public void handler(String caller, String type, String turn, Object[] args) {
		// 部分界面未传caller回来的情况
		if (StringUtils.isEmpty(caller))
			return;
		//系统参数配置中启用了文档归档
		String isDocUse = baseDao.getDBSetting("sys", "documentManage");
		if(isDocUse != null && !"".equals(isDocUse) && 1 == Integer.parseInt(isDocUse)){
			if("audit".equals(type) && "after".equals(turn)){
				if(args.length>0){
					if(args[0] instanceof Integer){
						int id = (int) args[0];
						documentSetService.documentManage(id, caller);
					}
				}
			}
			if("resAudit".equals(type) && "before".equals(turn)){
				if(args.length>0){
					if(args[0] instanceof Integer){
						int id = (int) args[0];
						documentSetService.beforeResAudit(caller, id);
					}
				}
			}
		}

		Employee employee = SystemSession.getUser();
		// before handler
		if ((type.equals("save") || type.equals("update")) && turn.equals("before")) {
			// 保存之前，校验数据有效性
			dataValidation(caller, args);
		} else if ((type.equals("delete") || type.equals("resCommit") || type.equals("resAudit") || type.equals("approve") || type
				.equals("resApprove")) && turn.equals("after")) {
			String flowcaller = processService.getFlowCaller(caller);
			// 清除流程设置人
			if (flowcaller != null) {
				baseDao.deleteByCondition("jnodeperson",
						"jp_caller='" + flowcaller + "' and jp_keyvalue=" + Integer.parseInt(args[0].toString()));
				try {
					// 删除该单据已实例化的流程
					processService.deletePInstance(Integer.parseInt(args[0].toString()), flowcaller, type);
				} catch (Exception e) {

				}
			}
		} else if (type.equals("commit") && turn.equals("before")) {
			// 提交之前，校验数据有效性
			dataValidation(caller, args);
			necessaryFieldsValidation(caller, args[0]);
			FieldsConditionValidation(caller, args[0]);
		} else if (type.equals("commit") && turn.equals("after")) {
			// 提交之后
			SqlRowList list = baseDao.queryForRowSet("SELECT fo_title,fo_flowcaller FROM form WHERE fo_caller=? AND fo_isautoflow=-1",
					caller);
			if (list.next()) {
				// 取对应的审批流caller
				Object flowcaller = list.getObject(2);
				if (flowcaller != null) {
					// 实例化审批流
					launchProcess(flowcaller.toString(), (Integer) args[0], list.getString(1) + "流程");
				} else {
					BaseUtil.showErrorOnSuccess("实例化审批流失败，请先到form配置里选择流程caller！");
				}
			}
		}else if (type.equals("post") && turn.equals("before")) {
			// 过账之前，校验数据有效性
			dataValidation(caller, args);
		} else if (!"admin".equals(employee.getEm_type()) && type.equals("resCommit") && turn.equals("before")) {
			// 限制反提交
			SqlRowList list = baseDao.queryForRowSet("SELECT fo_title,fo_flowcaller FROM form WHERE fo_caller=? AND fo_isautoflow=-1",
					caller);
			if (list.next()) {
				// 取对应的审批流caller
				Object flowcaller = list.getObject(2);
				if (flowcaller != null) {
					// 总是取最新的流程版本中JD_RESSUBMIT字段
					boolean boolRes = baseDao.checkIf("jprocessdeploy", "JD_CALLER='" + flowcaller + "' AND JD_RESSUBMIT='是'");
					if (boolRes) {
						boolean bool = baseDao.checkIf("Jprocess", "jp_keyvalue=" + args[0] + " and jp_status='待审批' and jp_caller='"
								+ flowcaller + "' and jp_launcherid='" + employee.getEm_code() + "'");
						if (bool)
							BaseUtil.showError("该单据存在待审批的流程,不允许反提交!");
						boolean boolA = baseDao.checkIf("Jprocand", "jp_keyvalue=" + args[0] + " and jp_flag=1 and jp_caller='"
								+ flowcaller + "' and jp_launcherid='" + employee.getEm_code() + "'");
						if (boolA)
							BaseUtil.showError("该单据存在待接管的流程,不允许反提交!");
					}
				}
			}
		}
		List<Interceptors> interceptors = interceptorService.getInterceptorsByCallerAndType(caller, type, turn);
		if (!CollectionUtil.isEmpty(interceptors)) {
			for (Interceptors interceptor : interceptors) {
				Object object = ContextUtil.getBean(interceptor.getClass_().substring(interceptor.getClass_().lastIndexOf(".") + 1));
				if (object == null) {
					invoke(interceptor.getClass_(), interceptor.getMethod(), type, args);
				} else {
					Object[] parsedArgs = args;
					if (interceptor.isCommon_config()) {
						parsedArgs = Arrays.copyOf(args, args.length + 1);
						parsedArgs[parsedArgs.length - 1] = interceptor.getId();
					}
					try {
						Method method = object.getClass().getMethod(interceptor.getMethod(), ClassUtil.getObjectsClasses(parsedArgs));
						method.invoke(object, parsedArgs);
					} catch (Exception e) {
						if (e.getCause() != null) {
							String exName = e.getCause().getClass().getSimpleName();
							if (exName.equals("RuntimeException") || exName.equals("SystemException"))
								BaseUtil.showError(e.getCause().getMessage());
						}
						e.printStackTrace();
						// BaseUtil.showError(e.getMessage());
					}
				}
			}

		}
		if ("delete".equals(type) && "before".equals(turn)) {// 删除之前，记录被删除数据
			baseDao.recycleAll(caller, Integer.parseInt(args[0].toString()), employee);
		}
		if ("deletedetail".equals(type) && "before".equals(turn)) {
			baseDao.recycle(caller, Integer.parseInt(args[0].toString()), employee);
		}
		try {
			// 判断该操作是否设置了消息模板
			if ("after".equals(turn)) {
				Object mmid = baseDao.getFieldDataByCondition("MESSAGEMODEL left join MESSAGEROLE on mm_id=mr_mmid", "distinct mm_id",
						"MR_ISUSED=-1 AND MM_ISUSED=-1 and mm_caller='" + caller + "' and MM_OPERATE='" + type + "'");
				// 调用生成消息的存储过程
				if (mmid != null) {
					Object keyValue = null;
					if (("save".equals(type) || "update".equals(type)) && args[0] instanceof HashMap) {
						Object keyfield = baseDao.getFieldDataByCondition("Form", "fo_keyfield", "fo_caller='" + caller + "'");
						if (keyfield != null && keyfield != "") {
							Map<String, Object> map = (Map<String, Object>) args[0];
							keyValue = map.get(keyfield);
						}
					} else
						keyValue = args[0];
					baseDao.callProcedure(
							"SP_CREATEINFO",
							new Object[] { mmid, employee.getEm_code(), keyValue,
									DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) });
				}
			}
		} catch (Exception e) {
			System.out.println("Got a Exception：" + e.getMessage());
			e.printStackTrace();
		}
	}

	public void launchProcess(String caller, int formId, String processName) {
		Employee employee = SystemSession.getUser();
		/***
		 * 重复提交
		 */
		boolean bool = false;
		bool = baseDao.checkIf("Jprocess", "jp_keyvalue=" + formId + " and jp_status='待审批' and jp_caller='" + caller + "'");
		if (bool) {
			BaseUtil.showErrorOnSuccess("当前单据状态可能是已提交存在待审批的流程 不允许重复发起!");
		} else {
			bool = baseDao.checkIf("Jprocand", "jp_keyvalue=" + formId + " and jp_flag=1 and jp_caller='" + caller + "'");
			if (bool) {
				BaseUtil.showErrorOnSuccess("当前单据状态可能是已提交存在待审批的流程 不允许重复发起!");
			}
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("caller", caller);
		result.put("id", formId);
		result.put("jpName", processName);
		result.put("code", employee.getEm_code());
		result.put("name", employee.getEm_name());
		processService.startProcess(result, employee);
	}

	/**
	 * 一般逻辑
	 * 
	 * @param methodName
	 *            方法名字，与ld_code一致
	 * @param type
	 *            方法类型
	 * @param args
	 *            传入参数
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void invoke(String className, String methodName, String type, Object[] args) {
		try {
			Class cls = Class.forName(className);
			Class[] argsClass = new Class[args.length];
			for (int i = 0, j = args.length; i < j; i++) {
				argsClass[i] = args[i].getClass();
			}
			Method method = cls.getMethod(methodName, argsClass);
			method.invoke(cls.newInstance(), args);
		} catch (ClassNotFoundException e) {
			BaseUtil.showError(className + "." + methodName + "不存在");
		} catch (Exception e) {
			BaseUtil.showError(e.getMessage());
		}
	}

	@SuppressWarnings({ "rawtypes" })
	public String processinvoke(String className, String methodName, String bean, Object[] args) {
		Class[] argsClass = new Class[args.length];
		for (int i = 0, j = args.length; i < j; i++) {
			if (args[i].getClass() != Integer.class) {
				argsClass[i] = args[i].getClass();
			} else
				argsClass[i] = int.class;
		}
		Object object = ContextUtil.getBean(bean);
		try {
			if (object == null) {
				object = ContextUtil.getBean(className);
			}
			Method method = object.getClass().getMethod(methodName, argsClass);
			method.invoke(object, args);
		} catch (NoSuchMethodException e) {
			// 考虑到程序部分没有caller配置
			if (args.length > 1) {
				return processinvoke(className, methodName, bean, new Object[] { args[0] });
			} else
				return "未找到相应的审批逻辑!";
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getCause() != null) {
				String exName = e.getCause().getClass().getSimpleName();
				if (exName.equals("RuntimeException") || exName.equals("SystemException"))
					return e.getCause().getMessage();
			}
			if (e.getCause() != null && e.getCause().getMessage() != null)
				return e.getCause().getMessage();
			return e.getMessage();
		}
		return null;
	}

	/**
	 * 利用放大镜配置，校验数据有效性
	 */
	@SuppressWarnings("unchecked")
	public void dataValidation(String caller, Object[] args) {
		if (args.length > 0 && args[0] instanceof HashMap) {
			Map<String, Object> map = (Map<String, Object>) args[0];
			Form form = formDao.getForm(caller, SpObserver.getSp());
			if (form != null && form.getFormDetails() != null) {
				for (FormDetail detail : form.getFormDetails()) {
					if (detail.isNeedCheck()) {
						if ("C".equals(detail.getFd_type()) || "EC".equals(detail.getFd_type())) {
							validateByCombo(caller, detail, map.get(detail.getFd_field()));
						} else {
							validateByDBFindSetUI(caller, detail, map.get(detail.getFd_field()));
						}
					}
					if (detail.getFd_minvalue() != null) {
						validateByMinvalue(caller, detail, map.get(detail.getFd_field()));
					}
				}
			}
		}
		if (args.length > 1 && args[1] instanceof ArrayList) {
			ArrayList<Map<String, Object>> maps = (ArrayList<Map<String, Object>>) args[1];
			List<List<Map<String, Object>>> subList = CollectionUtil.split(maps, Constant.ORACLE_MAX_TABLE_SIZE);
			List<DetailGrid> detailGrids = detailGridDao.getDetailGridsByCaller(caller, SpObserver.getSp());
			if (detailGrids != null && detailGrids.size() > 0) {
				for (DetailGrid detail : detailGrids) {
					for (List<Map<String, Object>> list : subList) {// 防止超过数据库table
																	// type的999条限制
						if (detail.isNeedCheck()) {
							if ("combo".equals(detail.getDg_type()) || "editcombo".equals(detail.getDg_type())) {
								validateByCombo(detail, CollectionUtil.pluckSqlString(list, detail.getDg_field()));
							} else {
								validateByDBFind(detail, CollectionUtil.pluckSqlString(list, detail.getDg_field()));
							}
						}
						if (detail.getDg_minvalue() != null) {
							validateByMinvalue(detail, CollectionUtil.pluckSqlString(list, detail.getDg_field()));
						}
					}
				}
			}
		}
		if (args.length == 1 && args[0] instanceof Integer) {
			Form form = formDao.getForm(caller, SpObserver.getSp());
			if (form != null && form.getFormDetails().size() > 0) {
				String condition = form.getFo_keyfield() + "=" + args[0];
				Map<String, Object> map = baseDao.getFormData(form, condition);
				if (StringUtils.hasText(form.getFo_detailtable())) {
					List<DetailGrid> detailGrids = detailGridDao.getDetailGridsByCaller(caller, SpObserver.getSp());
					if (detailGrids != null & detailGrids.size() > 0) {
						String detailCond = form.getFo_detailmainkeyfield() + "=" + args[0];
						List<Map<String, Object>> grid = baseDao.getDetailGridData(detailGrids, detailCond, SystemSession.getUser(), 1,
								1000);
						dataValidation(caller, new Object[] { map, grid });
					} else {
						dataValidation(caller, new Object[] { map });
					}
				} else
					dataValidation(caller, new Object[] { map });
			}

		}
	}

	/**
	 * 利用formdetail填写的最小值校验数据
	 * 
	 * @param caller
	 * @param detail
	 * @param object
	 */
	private void validateByMinvalue(String caller, FormDetail detail, Object object) {
		if (StringUtil.hasText(object)) {
			if (Double.parseDouble(object.toString()) < Double.parseDouble(detail.getFd_minvalue()))
				BaseUtil.showError(detail.getFd_caption() + " 数据(" + object + ")小于" + detail.getFd_minvalue());
		}
	}

	/**
	 * 利用griddetail填写的最小值校验数据
	 * 
	 * @param caller
	 * @param detail
	 * @param object
	 */
	private void validateByMinvalue(DetailGrid detail, String objects) {
		boolean bool = detail.getDg_minvalue().matches(Constant.REG_NUM);
		String errs = null;
		if (bool) {
			errs = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(col) from (select column_value as col from table(STR_TABLE_TYPE(" + objects
							+ "))) where col < ? and rownum<20 ", String.class, Double.parseDouble(detail.getDg_minvalue()));
		} else {
			errs = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(col) from (select column_value as col from table(STR_TABLE_TYPE(" + objects
							+ "))) where col < ? and rownum<20 ", String.class, detail.getDg_minvalue());
		}
		if (errs != null)
			BaseUtil.showError(detail.getDg_caption() + " 数据(" + errs + ")小于" + detail.getDg_minvalue());
	}

	/**
	 * 利用combo校验数据
	 * 
	 * @param caller
	 * @param detail
	 * @param object
	 */
	private void validateByCombo(String caller, FormDetail detail, Object object) {
		if (StringUtil.hasText(object)) {
			int count = baseDao.getCountByCondition("DatalistCombo",
					"dlc_caller='" + caller + "' and dlc_fieldname='" + detail.getFd_field() + "' and dlc_display='" + object + "'");
			if (count == 0)
				BaseUtil.showError(detail.getFd_caption() + " 数据(" + object + ")不存在");
		}
	}

	/**
	 * 利用DBFindSetUI校验数据
	 * 
	 * @param caller
	 * @param detail
	 * @param object
	 */
	private void validateByDBFindSetUI(String caller, FormDetail detail, Object object) {
		if (StringUtil.hasText(object)) {
			object = String.valueOf(object).replaceAll("'", "''");
			DBFindSetUI dbFindSetUI = null;
			try {
				dbFindSetUI = dbfindSetUiDao.getDbFindSetUIByField(caller, detail.getFd_field(), SpObserver.getSp());
			} catch (Exception e) {
				// 对于没有配合dbfind的，或配置错误的不考虑
				return;
			}
			if (dbFindSetUI != null) {
				String condition = dbFindSetUI.getDs_likefield()
						+ ("M".equals(detail.getFd_dbfind()) ? " IN(" + SqlUtil.splitToSqlString(object.toString(), "#") + ")" : "='"
								+ object + "'");
				if (StringUtil.hasText(dbFindSetUI.getDs_uifixedcondition())) {
					condition = "(" + condition + ") and (" + baseDao.parseEmpCondition(dbFindSetUI.getDs_uifixedcondition()) + ")";
				}
				int count = baseDao.getCount(dbFindSetUI.getSql(condition));
				if (count == 0)
					BaseUtil.showError(detail.getFd_caption() + " (" + object + ") "
							+ ((dbFindSetUI.getDs_error() == null) ? " 在系统中不存在或未审批" : dbFindSetUI.getDs_error()));
			}
		}
	}

	private void validateByDBFind(DetailGrid detail, String objects) {
		String dbCaller = detail.getDg_findfunctionname().split("\\|")[0];
		String likeField = detail.getDg_findfunctionname().split("\\|")[1];
		DBFindSet dbFindSet = dbfindSetDao.getDbfind(dbCaller, SpObserver.getSp());
		if (dbFindSet != null) {
			String errs = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(col) from (select column_value as col from table(STR_TABLE_TYPE("
							+ objects
							+ "))) where not exists (select 1 from "
							+ dbFindSet.getDs_tablename()
							+ " where "
							+ likeField
							+ "=col"
							+ (StringUtil.hasText(dbFindSet.getDs_fixedcondition()) ? " and "
									+ baseDao.parseEmpCondition(dbFindSet.getDs_fixedcondition()) : "") + ")", String.class);
			if (errs != null)
				BaseUtil.showError(detail.getDg_caption() + " 数据(" + errs + ")错误,"
						+ ((dbFindSet.getDs_error() == null) ? " 单据不存在或者未审批" : dbFindSet.getDs_error()));
		}
	}

	private void validateByCombo(DetailGrid detail, String objects) {
		String errs = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(col) from (select column_value as col from table(STR_TABLE_TYPE(" + objects
						+ "))) where not exists (select 1 from datalistcombo where dlc_caller=? and dlc_fieldname=? and dlc_display=col)",
				String.class, detail.getDg_caller(), detail.getDg_field());
		if (errs != null)
			BaseUtil.showError(detail.getDg_caption() + " 数据(" + errs + ")不存在");
	}

	private void triggerTask(String caller, Object keyValue) {
		if (keyValue instanceof Object[]) {
			Object[] info = (Object[]) keyValue;
			for (Object o : info) {
				if (o instanceof Integer) {
					keyValue = o;
					break;
				}
			}
		}
		baseDao.updateByCondition("ProjectTask", "status='已审核',statuscode='AUDITED',handstatuscode='DOING',handstatus='已启动'",
				"sourcecaller='" + caller + "' and sourceid=" + keyValue + " and nvl(handstatuscode,' ')='UNACTIVE'");
	}

	/**
	 * 必填项验证函数校验 necessaryFields:wo_mankind='主管及以下':wo_remark
	 */
	public void necessaryFieldsValidation(String caller, Object id) {
		if (id != null) {
			Form form = formDao.getForm(caller, SpObserver.getSp());
			if (form != null && form.getFormDetails() != null) {
				for (FormDetail detail : form.getFormDetails()) {
					if (detail.getFd_func() != null && detail.getFd_func() != "") {
						String[] fields = detail.getFd_func().split(";");
						for (String f : fields) {
							if (f.startsWith("necessaryFields:")) {
								String condition = form.getFo_keyfield() + "=" + id;
								String[] arr = f.split(":");
								if (arr.length == 3 && arr[1].length() > 0 && arr[2].length() > 0) {
									condition = condition + " and " + arr[1];
									boolean b = baseDao.checkIf(form.getFo_table(), condition);
									if (b) {
										condition = condition + " and " + arr[2].replaceAll(",", " is not null and ") + " is not null";
										int count = baseDao.getCountByCondition(form.getFo_table(), condition);
										if (count == 0) {
											String err = "";
											for (FormDetail formDetail : form.getFormDetails()) {
												for (String field : arr[2].split(",")) {
													if (formDetail.getFd_field().equalsIgnoreCase(field))
														err += formDetail.getFd_caption() + "、";
												}
											}
											BaseUtil.showError("必填检测：当前条件下，" + err.substring(0, err.length() - 1) + "必填!");
										}
									}
								}

							}
						}
					}
				}
			}
		}
	}

	/**
	 * 提交时验证附加条件 condition:wo_mankind='主管及以下':length(wo_remark)>10:人员类型为'主管及以下'时,备注长度需大于10
	 */
	public void FieldsConditionValidation(String caller, Object id) {
		if (id != null) {
			Form form = formDao.getForm(caller, SpObserver.getSp());
			if (form != null && form.getFormDetails() != null) {
				for (FormDetail detail : form.getFormDetails()) {
					if (detail.getFd_func() != null && detail.getFd_func() != "") {
						String[] fields = detail.getFd_func().split(";");
						for (String f : fields) {
							if (f.startsWith("condition:")) {
								String condition = form.getFo_keyfield() + "=" + id;
								f = f.replaceAll("@ID", id.toString());
								String[] arr = f.split(":");
								if (arr.length == 4 && arr[1].length() > 0 && arr[2].length() > 0 && arr[3].length() > 0) {
									condition = condition + " and " + arr[1];
									boolean b = baseDao.checkIf(form.getFo_table(), condition);
									if (b) {
										condition = condition + " and " + arr[2];
										int count = baseDao.getCountByCondition(form.getFo_table(), condition);
										if (count == 0) {
											BaseUtil.showError(arr[3]);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
