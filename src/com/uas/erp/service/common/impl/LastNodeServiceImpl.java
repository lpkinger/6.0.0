package com.uas.erp.service.common.impl;

import java.util.Date;

import org.jbpm.api.listener.EventListener;
import org.jbpm.api.listener.EventListenerExecution;
import org.jbpm.api.model.OpenProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.JProcessSetDao;
import com.uas.erp.dao.common.ProcessDao;
import com.uas.erp.model.JProcessSet;

/* 流程的 最后一个 节点执行完毕 ,改变 对应表单 的 状态。
 * 利用 最后一个节点的 on-end 事件。
 * 
*/
@SuppressWarnings("serial")
public class LastNodeServiceImpl implements EventListener{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private JProcessSetDao processSetDao;
	@Autowired
	private ProcessDao processDao;
	public void alterStatus(String caller,int formId){
		
		JProcessSet js = processSetDao.getCallerInfo(caller);
				
		final String sql = " UPDATE "+js.getJs_table()+" SET "+js.getJs_formStatusName()+"='已审核'"+" WHERE "+js.getJs_formKeyName()+" ='"+formId+"'";
		baseDao.execute(sql);
	}

	@Override
	public void notify(EventListenerExecution execution) throws Exception {
		//配置些方法  
		System.out.println(execution.getIsProcessInstance());
		int callerId = Integer.parseInt( execution.getVariable("id").toString());
		System.out.println(DateUtil.parseDateToString(new Date(),Constant.YMD_HMS));
		OpenProcessInstance id=execution.getProcessInstance();
		System.out.println(id);
		System.out.println(execution.isEnded());
		System.out.println(processDao);
		System.out.println(baseDao);
		if(execution.getIsProcessInstance()&&execution.isEnded()){ //该执行 必须是 流程实例 而且 已经执行完毕。
			/*String caller = (String) execution.getVariable("caller");		
			int callerId = Integer.parseInt((String) execution.getVariable("id"));
			System.out.println(execution.getProcessInstance().getId());
			System.out.println(execution.getProcessDefinitionId());*/
			/*System.out.println(baseDao.getFieldDatasByCondition("JProcess", field, condition));*/
			/*baseDao.getFieldDataByCondition("Jnode", field, condition)*/
			/*alterStatus(caller, callerId);*/
		}
	}
}
