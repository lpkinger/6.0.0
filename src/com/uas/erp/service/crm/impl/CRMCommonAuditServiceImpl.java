package com.uas.erp.service.crm.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;


import com.uas.erp.model.MessageLog;
import com.uas.erp.service.crm.CRMCommonAuditService;
@Service
public class CRMCommonAuditServiceImpl implements CRMCommonAuditService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void audit(String caller, int id, String auditerFieldName,
			String auditdateFieldName) {
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[]{"fo_table", "fo_keyfield", "fo_statusfield", "fo_statuscodefield"}, 
				"fo_caller='" + caller + "'");//先根据caller拿到对应table和主键
		if(objs != null){
			//只能对状态为[已提交]的单据进行审核操作!
			Object status = baseDao.getFieldDataByCondition((String)objs[0], (String)objs[3], (String)objs[1] + "=" +  id);
			StateAssert.auditOnlyCommited(status);
			//执行审核前的其它逻辑
			handlerService.beforeAudit(caller, id);
			//执行审核操作
			baseDao.updateByCondition((String)objs[0], objs[3] + "='AUDITED'," + objs[2] + "='" + 
					BaseUtil.getLocalMessage("AUDITED") + "',"+auditerFieldName+"='"+SystemSession.getUser().getEm_name()+"',"+auditdateFieldName+"=sysdate", objs[1] + "=" + id);
			//记录操作
			try{
				baseDao.logger.audit(caller, objs[1]+"", id);
			} catch (Exception e) {
				e.printStackTrace();
			}	
			//执行审核后的其它逻辑
			handlerService.afterAudit(caller, id);
		} else {
			BaseUtil.showError(BaseUtil.getLocalMessage("audit_tableisnull"));
		}
	}

	@Override
	public void resAudit(String caller, int id, String auditerFieldName,
			String auditdateFieldName) {
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[]{"fo_table", "fo_keyfield", "fo_statusfield", "fo_statuscodefield"}, 
				"fo_caller='" + caller + "'");//先根据caller拿到对应table和主键
		if(objs != null){
			//只能对状态为[已审核]的单据进行反审核操作!
			Object status = baseDao.getFieldDataByCondition((String)objs[0], (String)objs[3], (String)objs[1] + "=" + id);
			if(!status.equals("AUDITED")){
				BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
			}
			//执行反审核操作
			baseDao.updateByCondition((String)objs[0], objs[3] + "='ENTERING'," + objs[2] + "='" + 
					BaseUtil.getLocalMessage("ENTERING") + "',"+auditerFieldName+"='',"+auditdateFieldName+"=null", objs[1] + "=" + id);
			//记录操作
			try{
				baseDao.logger.resAudit(caller, objs[1]+"", id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			BaseUtil.showError(BaseUtil.getLocalMessage("resaudit_tableisnull"));
		}
	}

	@Override
	public void confirmCommon(String caller, int id, String auditerFieldName) {
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[]{"fo_table", "fo_keyfield", "fo_statusfield", "fo_statuscodefield"}, 
				"fo_caller='" + caller + "'");//先根据caller拿到对应table和主键
		if(objs != null){
			//只能对状态为[已审核]的单据进行反审核操作!
			Object status = baseDao.getFieldDataByCondition((String)objs[0], (String)objs[3], (String)objs[1] + "=" + id);
			if(!status.equals("AUDITED")){
				BaseUtil.showError(BaseUtil.getLocalMessage("common.confirm_onlyAudit"));
			}
			//执行反审核操作
			baseDao.updateByCondition((String)objs[0], auditerFieldName+"='已处理'", (String)objs[1]+"=" + id);
			//记录操作
			try{
				baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.confirm"), 
						BaseUtil.getLocalMessage("msg.confirmSuccess"), caller + "|" + objs[1] + "=" + id));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			BaseUtil.showError(BaseUtil.getLocalMessage("resaudit_tableisnull"));
		}		
	}

}
