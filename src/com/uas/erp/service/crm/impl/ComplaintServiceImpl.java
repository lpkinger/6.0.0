package com.uas.erp.service.crm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;


import com.uas.erp.service.crm.ComplaintService;
import com.uas.opensys.service.CurBaseService;

@Service
public class ComplaintServiceImpl implements ComplaintService{
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	
	@Autowired
	private CurBaseService curBaseService;
	@Override
	public void saveComplaint(String formStore, 
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Complaint", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		if("CurComplaint".equals(caller)){
			curBaseService.updateCurInfo("Complaint","co_cucode","co_cuname",store.get("co_enid"),"co_id="+store.get("co_id"));
		}
		//记录操作
		baseDao.logger.save(caller, "co_id", store.get("co_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void updateComplaint(String formStore, 
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Complaint", "co_id");
		baseDao.execute(formSql);
		if("CurComplaint".equals(caller)){
			curBaseService.updateCurInfo("Complaint","co_cucode","co_cuname",store.get("co_enid"),"co_id="+store.get("co_id"));
		}
		//记录操作
		baseDao.logger.update(caller, "co_id", store.get("co_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
	}

	@Override
	public void deleteComplaint(int co_id,  String caller) {
		handlerService.beforeDel(caller, co_id);
		//删除purchase
		baseDao.deleteById("Complaint", "co_id", co_id);
		//记录操作
		baseDao.logger.delete(caller, "co_id", co_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, co_id);
	}

	@Override
	public void auditComplaint(int co_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Complaint","co_statuscode", "co_id=" + co_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] {co_id });
		// 执行审核操作
		baseDao.audit("Complaint", "co_id=" + co_id, "co_status", "co_statuscode");
		// 记录操作
		baseDao.logger.audit(caller, "co_id", co_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] {co_id });
	}

	@Override
	public void resAuditComplaint(int co_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Complaint","co_statuscode", "co_id=" + co_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, new Object[] {co_id});
		// 执行反审核操作
		baseDao.resOperate("Complaint", "co_id=" + co_id, "co_status", "co_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "co_id", co_id);
		handlerService.afterResAudit(caller, new Object[] {co_id });
	}

	@Override
	public void submitComplaint(int co_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Complaint","co_statuscode", "co_id=" + co_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑	
		handlerService.beforeSubmit(caller, new Object[] {co_id});
		// 执行提交操作
		baseDao.submit("Complaint", "co_id=" + co_id, "co_status", "co_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "co_id", co_id);;
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] {co_id});
	}

	@Override
	public void resSubmitComplaint(int co_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		handlerService.beforeResSubmit(caller, new Object[] {co_id});
		Object status = baseDao.getFieldDataByCondition("Complaint","co_statuscode", "co_id=" + co_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		baseDao.resOperate("Complaint", "co_id=" + co_id, "co_status", "co_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "co_id", co_id);
		handlerService.afterResSubmit(caller, new Object[] {co_id});
	}
}
