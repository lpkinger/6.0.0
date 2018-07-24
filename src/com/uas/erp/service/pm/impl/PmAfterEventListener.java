package com.uas.erp.service.pm.impl;

import java.util.Date;

import org.jbpm.api.listener.EventListener;
import org.jbpm.api.listener.EventListenerExecution;

import com.uas.erp.core.ContextUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.FormDao;
import com.uas.erp.dao.common.ProcessDao;
import com.uas.erp.model.Form;
import com.uas.erp.model.JNode;

@SuppressWarnings("serial")
public class PmAfterEventListener implements EventListener {
	private static FormDao formDao;
	private static BaseDao baseDao;
	private static ProcessDao processDao;
	static {
		formDao = (FormDao) ContextUtil.getBean("formDao");
		baseDao = (BaseDao) ContextUtil.getBean("baseDao");
		processDao = (ProcessDao) ContextUtil.getBean("processDao");
	}

	@Override
	public void notify(EventListenerExecution execution) throws Exception {
		String processInstanceId = execution.getProcessInstance().getId();
		String nodeName = processDao.getJProcesses(processInstanceId).get(0).getJp_nodeName();
		if (execution.getIsProcessInstance() && execution.isEnded()) {
			String caller = (String) execution.getVariable("caller");
			int keyValue = Integer.parseInt(execution.getVariable("id").toString());
			JNode jnode = processDao.getJNodeBy(processInstanceId, nodeName);
			if (caller.equals("Make!Base")||caller.equals("Make")) {
				/** updateMake **/
				updateMakeBase(caller, keyValue, jnode);
			}
		}
	}

	/**
	 * 制造单维护 审核之后要更新批准人,批准时间,批准状态
	 * */
	private void updateMakeBase(String caller, Object keyValue, JNode jnode) {
		Form form = formDao.getForm(caller, SpObserver.getSp());
		String tablename = form.getFo_table();
		final String Sql = "UPDATE " + tablename
				+ " SET ma_checkstatus='已批准' , ma_checkstatuscode='APPROVE' , ma_checkman='"
				+ jnode.getJn_dealManName() + "',ma_checkdate="
				+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date() + " where ma_id=" + keyValue);
		baseDao.execute(Sql);
	}
}
