package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.ProductBrandService;

@Service
public class ProductBrandServiceImpl implements ProductBrandService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProductBrand(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[]{store});		
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ProductBrand", new String[]{}, new Object[]{});
		baseDao.execute(formSql);	
		baseDao.logger.save(caller, "pb_id", store.get("pb_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void updateProductBrandById(String formStore,String caller) {		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[]{store});
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProductBrand", "pb_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "pb_id", store.get("pb_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void deleteProductBrand(int pb_id, String caller) {		
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{pb_id});
		//删除purchase
		baseDao.deleteById("ProductBrand", "pb_id", pb_id);
		baseDao.logger.delete(caller, "pb_id", pb_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{pb_id});
	}

	@Override
	public void auditProductBrand(int pb_id, String caller) {		
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ProductBrand", "pb_statuscode", "pb_id=" + pb_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[]{pb_id});
		//执行审核操作
		baseDao.audit("ProductBrand", "pb_id=" + pb_id, "pb_status", "pb_statuscode", "pb_auditdate", "pb_auditor");
		//记录操作
		baseDao.logger.audit(caller, "pb_id", pb_id);
		//执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[]{pb_id});
	}

	@Override
	public void resAuditProductBrand(int pb_id, String caller) {		
		Object status = baseDao.getFieldDataByCondition("ProductBrand", "pb_statuscode", "pb_id=" + pb_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resAudit("ProductBrand", "pb_id=" + pb_id, "pb_status", "pb_statuscode", "pb_auditdate", "pb_auditor");
		baseDao.resOperate("ProductBrand", "pb_id=" + pb_id, "pb_status", "pb_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "pb_id", pb_id);
	}

	@Override
	public void submitProductBrand(int pb_id, String caller) {		
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ProductBrand", "pb_statuscode", "pb_id=" + pb_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[]{pb_id});
		//执行提交操作
		baseDao.submit("ProductBrand", "pb_id=" + pb_id, "pb_status", "pb_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "pb_id", pb_id);
		//执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[]{pb_id});
	}

	@Override
	public void resSubmitProductBrand(int pb_id, String caller) {		
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ProductBrand", "pb_statuscode", "pb_id=" + pb_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[]{pb_id});
		//执行反提交操作
		baseDao.resOperate("ProductBrand", "pb_id=" + pb_id, "pb_status", "pb_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "pb_id", pb_id);
		handlerService.handler(caller, "resCommit", "after", new Object[]{pb_id});
	}

	@Override
	public void bannedProductBrand(int id, String caller) {
		// 执行禁用前的其它逻辑
		handlerService.handler(caller, "banned", "before", new Object[] {id});
		// 禁用(修改物料状态为已禁用)
		baseDao.banned("ProductBrand", "pb_id=" + id, "pb_status", "pb_statuscode");
		// 记录操作
		baseDao.logger.banned(caller, "pb_id", id);
		// 执行禁用后的其它逻辑
		handlerService.handler(caller, "banned", "after", new Object[] { id});
	}

	@Override
	public void resBannedProductBrand(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("ProductBrand", "pb_statuscode", "pb_id=" + id);
		if (!"DISABLE".equals(status)) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resBanned_onlyBanned"));
		}
		// 执行反禁用前的其它逻辑
		handlerService.handler(caller, "resBanned", "before", new Object[] { id});
		// 反禁用(修改物料状态为在录入)
		baseDao.resOperate("ProductBrand", "pb_id=" + id, "pb_status", "pb_statuscode");
		//记录操作
		baseDao.logger.resBanned(caller, "pb_id", id);
		// 执行反禁用后的其它逻辑
		handlerService.handler(caller, "resBanned", "after", new Object[] { id});
	}
}
