package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.BrandVendorService;

@Service
public class BrandVendorServiceImpl implements BrandVendorService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveBrandVendor(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[]{store});		
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BrandVendor", new String[]{}, new Object[]{});
		baseDao.execute(formSql);	
		baseDao.logger.save(caller, "bv_id", store.get("bv_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void updateBrandVendor(String formStore,String caller) {		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[]{store});
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BrandVendor", "bv_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "bv_id", store.get("bv_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void deleteBrandVendor(int bv_id, String caller) {		
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{bv_id});
		//删除purchase
		baseDao.deleteById("BrandVendor", "bv_id", bv_id);
		baseDao.logger.delete(caller, "bv_id", bv_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{bv_id});
	}

	@Override
	public void auditBrandVendor(int bv_id, String caller) {		
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("BrandVendor", "bv_statuscode", "bv_id=" + bv_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[]{bv_id});
		//执行审核操作
		baseDao.audit("BrandVendor", "bv_id=" + bv_id, "bv_status", "bv_statuscode");
		//记录操作
		baseDao.logger.audit(caller, "bv_id", bv_id);
		//执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[]{bv_id});
	}

	@Override
	public void resAuditBrandVendor(int bv_id, String caller) {		
		Object status = baseDao.getFieldDataByCondition("BrandVendor", "bv_statuscode", "bv_id=" + bv_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("BrandVendor", "bv_id=" + bv_id, "bv_status", "bv_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "bv_id", bv_id);
	}

	@Override
	public void submitBrandVendor(int bv_id, String caller) {		
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("BrandVendor", "bv_statuscode", "bv_id=" + bv_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[]{bv_id});
		//执行提交操作
		baseDao.submit("BrandVendor", "bv_id=" + bv_id, "bv_status", "bv_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "bv_id", bv_id);
		//执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[]{bv_id});
	}

	@Override
	public void resSubmitBrandVendor(int bv_id, String caller) {		
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("BrandVendor", "bv_statuscode", "bv_id=" + bv_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[]{bv_id});
		//执行反提交操作
		baseDao.resOperate("BrandVendor", "bv_id=" + bv_id, "bv_status", "bv_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "bv_id", bv_id);
		handlerService.handler(caller, "resCommit", "after", new Object[]{bv_id});
	}

	@Override
	public void bannedBrandVendor(int id, String caller) {
		// 执行禁用前的其它逻辑
		handlerService.handler(caller, "banned", "before", new Object[] {id});
		// 禁用(修改物料状态为已禁用)
		baseDao.banned("BrandVendor", "bv_id=" + id, "bv_status", "bv_statuscode");
		// 记录操作
		baseDao.logger.banned(caller, "bv_id", id);
		// 执行禁用后的其它逻辑
		handlerService.handler(caller, "banned", "after", new Object[] { id});
	}

	@Override
	public void resBannedBrandVendor(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("BrandVendor", "bv_statuscode", "bv_id=" + id);
		if (!"DISABLE".equals(status)) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resBanned_onlyBanned"));
		}
		// 执行反禁用前的其它逻辑
		handlerService.handler(caller, "resBanned", "before", new Object[] { id});
		// 反禁用
		baseDao.resOperate("BrandVendor", "bv_id=" + id, "bv_status", "bv_statuscode");
		//记录操作
		baseDao.logger.resBanned(caller, "bv_id", id);
		// 执行反禁用后的其它逻辑
		handlerService.handler(caller, "resBanned", "after", new Object[] { id});
	}
}
