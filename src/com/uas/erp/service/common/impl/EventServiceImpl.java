package com.uas.erp.service.common.impl;

import org.jbpm.api.listener.EventListener;
import org.jbpm.api.listener.EventListenerExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.ContextUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.ProcessDao;
import com.uas.erp.dao.common.impl.ProcessDaoImpl;

@Service("EventServiceImpl")
public class EventServiceImpl implements EventListener{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    @Autowired
    private BaseDao baseDao;
	@Override
	public void notify(EventListenerExecution execution) throws Exception {
		ProcessDao processDao=(ProcessDao)ContextUtil.getBean("processDao");
		System.out.println(baseDao);
		String id=execution.getProcessInstance().getId();
		if(processDao==null){
			processDao=new ProcessDaoImpl();
		}
		System.out.println(processDao.getJProcesses(id).get(0).getJp_nodeName());
	}

}
