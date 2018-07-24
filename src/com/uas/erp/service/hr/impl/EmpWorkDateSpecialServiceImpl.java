package com.uas.erp.service.hr.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.EmpWorkDateSpecialService;

@Service
public class EmpWorkDateSpecialServiceImpl implements EmpWorkDateSpecialService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;


	@Override
	public void saveEmpWorkDateSpecial(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);		
		handlerService.beforeSave(caller,new Object[]{store});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "EmpWorkDateSpecial", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save(caller, "ews_id", store.get("ews_id"));
		handlerService.afterSave(caller,new Object[]{store});
	}

	@Override
	public void updateEmpWorkDateSpecial(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		String[] f = {"ews_beg1","ews_beg2","ews_end1", "ews_end2"}; 
		for(int i=0;i<f.length;i++){
			if(store.get(f[i])==null){
				store.put(f[i], "");
			}
		}
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "EmpWorkDateSpecial", "ews_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "ews_id", store.get("ews_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});		
	}

	@Override
	public void deleteEmpWorkDateSpecial(int ews_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[]{ews_id});
		//删除
		baseDao.deleteById("EmpWorkDateSpecial", "ews_id", ews_id);
		//记录操作
		baseDao.logger.delete(caller, "ews_id", ews_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[]{ews_id});	
	}

	@Override
	public void auditEmpWorkDateSpecial(int ews_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("EmpWorkDateSpecial", "ews_statuscode", "ews_id=" + ews_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ews_id);
		//执行审核操作
		baseDao.audit("EmpWorkDateSpecial", "ews_id=" + ews_id, "ews_status", "ews_statuscode", "ews_auditdate", "ews_auditer");
		//记录操作
		baseDao.logger.audit(caller, "ews_id", ews_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, ews_id);
	}

	@Override
	public void resAuditEmpWorkDateSpecial(int ews_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("EmpWorkDateSpecial", "ews_statuscode", "ews_id=" + ews_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resAudit("EmpWorkDateSpecial", "ews_id=" + ews_id, "ews_status", "ews_statuscode", "ews_auditdate", "ews_auditer");
		//记录操作
		baseDao.logger.resAudit(caller, "ews_id", ews_id);		
	}

	@Override
	public void submitEmpWorkDateSpecial(int ews_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("EmpWorkDateSpecial", "ews_statuscode", "ews_id=" + ews_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ews_id);
		//执行提交操作
		baseDao.submit("EmpWorkDateSpecial", "ews_id=" + ews_id, "ews_status", "ews_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "ews_id", ews_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ews_id);		
	}

	@Override
	public void resSubmitEmpWorkDateSpecial(int ews_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("EmpWorkDateSpecial", "ews_statuscode", "ews_id=" + ews_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeSubmit(caller, ews_id);
		//执行反提交操作
		baseDao.resOperate("EmpWorkDateSpecial", "ews_id=" + ews_id, "ews_status", "ews_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "ews_id", ews_id);
		handlerService.afterResSubmit(caller, ews_id);		
	}
}
