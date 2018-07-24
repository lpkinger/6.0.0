package com.uas.erp.dao.common.impl;

import org.jbpm.api.listener.EventListener;
import org.jbpm.api.listener.EventListenerExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.JProcessSetDao;
import com.uas.erp.model.JProcessSet;

/* 流程的 最后一个 节点执行完毕 ,改变 对应表单 的 状态。
 * 利用 最后一个节点的 on-end 事件。
 * 
*/
@SuppressWarnings("serial")
@Service("lastNodeService")
public class LastNodeServiceImpl implements EventListener{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private JProcessSetDao processSetDao;
	public void alterStatus(String caller,int callerId){
		
		JProcessSet js = processSetDao.getCallerInfo(caller);
				
		final String sql = " UPDATE "+caller+" SET "+js.getJs_formStatusName()+"='已审核'"+" WHERE "+js.getJs_formKeyName()+" ='"+callerId+"'";
		baseDao.execute(sql);
	}

	@Override
	public void notify(EventListenerExecution execution) throws Exception {
		if(execution.getIsProcessInstance()&&execution.isEnded()){ //该执行 必须是 流程实例 而且 已经执行完毕。
			String caller = (String) execution.getVariable("caller");
			int callerId = Integer.parseInt((String) execution.getVariable("id"));
			alterStatus(caller, callerId);
		}
	}
}
