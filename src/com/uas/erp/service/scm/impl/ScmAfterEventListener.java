package com.uas.erp.service.scm.impl;

import java.util.Date;
import java.util.List;

import org.jbpm.api.listener.EventListener;
import org.jbpm.api.listener.EventListenerExecution;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.ContextUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.EmployeeDao;
//import com.uas.erp.dao.common.FormDao;
import com.uas.erp.dao.common.ProcessDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.JNode;
import com.uas.erp.service.scm.PreProductService;

@SuppressWarnings("serial")
public class ScmAfterEventListener implements EventListener {
	// private static FormDao formDao;
	private static BaseDao baseDao;
	private static ProcessDao processDao;
	private static String baselanguage = "zh_CN";
	static {
		// formDao = (FormDao) BaseUtil.getBean("formDao");
		baseDao = (BaseDao) ContextUtil.getBean("baseDao");
		processDao = (ProcessDao) ContextUtil.getBean("processDao");
	}

	@Override
	public void notify(EventListenerExecution execution) throws Exception {
		String processInstanceId = execution.getProcessInstance().getId();
		String nodeName = processDao.getJProcesses(processInstanceId).get(0).getJp_nodeName();
		if (execution.getIsProcessInstance()) {
			String caller = (String) execution.getVariable("caller");
			int keyValue = Integer.parseInt(execution.getVariable("id").toString());
			JNode jnode = processDao.getJNodeBy(processInstanceId, nodeName);
			if (caller.equals("PreProduct")) {
				/** turnFormal 转正式物料 **/
				turnFormal(keyValue, jnode);
			} else if (caller.equals("PurchasePrice")) {
				/** 改价格为有效 */
				validPurchasePrice(keyValue, jnode);
			}
		}
	}

	/**
	 * 转正式物料
	 * */
	private void turnFormal(int keyValue, JNode jnode) {
		PreProductService preservice = (PreProductService) ContextUtil.getBean("preProductService");
		preservice.turnFormal(keyValue);
	}

	/**
	 * 改价格为有效
	 * */
	private void validPurchasePrice(int keyvalue, JNode jnode) {
		EmployeeDao employeeDao = (EmployeeDao) ContextUtil.getBean("employeeDao");
		Employee employee = employeeDao.getEmployeeByEmCode(jnode.getJn_dealManId());
		baseDao.updateByCondition(
				"PurchasePrice",
				"pp_statuscode='AUDITED',pp_status='" + BaseUtil.getLocalMessage("AUDITED", baselanguage) + "',pp_auditman='"
						+ employee.getEm_name() + "',pp_auditdate=" + DateUtil.parseDateToOracleString(null, new Date()), "pp_id="
						+ keyvalue);
		baseDao.updateByCondition("PurchasePriceDetail",
				"ppd_statuscode='VALID',ppd_status='" + BaseUtil.getLocalMessage("VALID", baselanguage) + "'", "ppd_ppid=" + keyvalue);
		StringBuffer sb = new StringBuffer();
		List<Object[]> list = baseDao.getFieldsDatasByCondition("purchasePrice left join purchasePriceDetail on pp_id=ppd_ppid",
				new String[] { "ppd_vendcode", "ppd_prodcode", "ppd_currency", "pp_kind" }, "ppd_ppid=" + keyvalue
						+ " and ppd_statuscode = 'VALID'");// 供应商、料号、币别、定价类型
		if (!list.isEmpty()) {
			for (Object[] objs : list) {
				List<Object[]> spds = baseDao.getFieldsDatasByCondition("purchasePrice left join purchasePriceDetail on pp_id=ppd_ppid",
						new String[] { "ppd_id", "pp_code", "pp_id", "ppd_detno" }, "ppd_vendcode='" + objs[0]
								+ "' AND ppd_statuscode='VALID'" + " AND ppd_prodcode='" + objs[1] + "' AND ppd_currency='" + objs[2] + "'"
								+ " and ppd_ppid <> " + keyvalue + " AND pp_kind='" + objs[3] + "'");
				for (Object[] spd : spds) {
					baseDao.updateByCondition("purchasePriceDetail",
							"ppd_statuscode='UNVALID',ppd_unvaliddate=sysdate,ppd_status='" + BaseUtil.getLocalMessage("UNVALID", baselanguage) + "'", "ppd_id="
									+ spd[0]);
					sb.append("价格库原编号为<a href=\"javascript:openUrl('jsps/scm/purchase/purchasePrice.jsp?formCondition=pp_idIS" + spd[2]
							+ "&gridCondition=ppd_ppidIS" + spd[2] + "&whoami=purchasePrice')\">" + spd[1] + "</a>&nbsp;第" + spd[3]
							+ "行数据已自动失效!<hr>");
				}
			}
		}
	}
}
