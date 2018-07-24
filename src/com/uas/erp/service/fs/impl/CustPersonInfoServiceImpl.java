package com.uas.erp.service.fs.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.fs.CustPersonInfoService;


@Service
public class CustPersonInfoServiceImpl implements CustPersonInfoService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCustPersonInfo(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		boolean bool = baseDao.checkIf("CustPersonInfo", "cp_papertype='"+store.get("cp_papertype")+"' and cp_papercode='"+store.get("cp_papercode")+"'");
		if (bool) {
			BaseUtil.showError("该客户个人信息已存在!");
		}
		
		handlerService.beforeSave(caller, new Object[]{store});
		
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "CustPersonInfo", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		
		//记录日志
		baseDao.logger.save(caller, "cp_id", store.get("cp_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void updateCustPersonInfo(String formStore,String caller) {		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		boolean bool = baseDao.checkIf("CustPersonInfo", "cp_papertype='"+store.get("cp_papertype")+"' and cp_papercode='"+store.get("cp_papercode")+"' and cp_id <>"+store.get("cp_id"));
		if (bool) {
			BaseUtil.showError("该客户个人信息已存在!");
		}
		
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "CustPersonInfo", "cp_id");
		baseDao.execute(formSql);
		
		//记录操作
		baseDao.logger.update(caller, "cp_id", store.get("cp_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
	}

	@Override
	public void deleteCustPersonInfo(int cp_id, String caller) {		
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{cp_id});
		//删除主表内容
		baseDao.deleteById("CustPersonInfo", "cp_id", cp_id);
		
		//记录日志
		baseDao.logger.delete(caller, "cp_id", cp_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{cp_id});
	}

	@Override
	public void submitCustPersonInfo(int cp_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("CustPersonInfo", "cp_statuscode", "cp_id=" + cp_id);
		StateAssert.submitOnlyEntering(status);
		
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { cp_id });
		
		// 执行提交操作
		baseDao.submit("CustPersonInfo", "cp_id=" + cp_id, "cp_status", "cp_statuscode");
		
		// 记录操作
		baseDao.logger.submit(caller, "cp_id", cp_id);
		
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { cp_id });
	}
	
	@Override
	public void resSubmitCustPersonInfo(int cp_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("CustPersonInfo", "cp_statuscode", "cp_id=" + cp_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { cp_id });
		
		// 执行反提交操作
		baseDao.resOperate("CustPersonInfo", "cp_id=" + cp_id, "cp_status", "cp_statuscode");
		
		// 记录操作
		baseDao.logger.resSubmit(caller, "cp_id", cp_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { cp_id });
	}
	
	@Override
	public void auditCustPersonInfo(int cp_id, String caller) {
		//只能对已提交进行审核操作
		Object status = baseDao.getFieldDataByCondition("CustPersonInfo", "cp_statuscode", "cp_id=" + cp_id);
		StateAssert.auditOnlyCommited(status);
		
		//执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[]{cp_id});
		baseDao.audit("CustPersonInfo", "cp_id=" + cp_id, "cp_status", "cp_statuscode","cp_auditdate","cp_auditman");
		
		//记录操作
		baseDao.logger.audit(caller, "cp_id", cp_id);
		//执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[]{ cp_id });
	}
	
	@Override
	public void resAuditCustPersonInfo(int cp_id, String caller) {
		// 只能对状态为[已审核]的采购单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("CustPersonInfo", "cp_statuscode", "cp_id=" + cp_id);
		StateAssert.resAuditOnlyAudit(status);
		
		baseDao.resAuditCheck("CustPersonInfo",cp_id);
		handlerService.beforeResAudit(caller, new Object[]{cp_id});
		
		// 执行反审核操作
		baseDao.resAudit("CustPersonInfo", "cp_id=" + cp_id, "cp_status", "cp_statuscode", "cp_auditman","cp_auditdate");
		
		// 记录操作
		baseDao.logger.resAudit(caller, "cp_id",cp_id);
		handlerService.afterResAudit(caller, new Object[]{cp_id});
	}
	
}
