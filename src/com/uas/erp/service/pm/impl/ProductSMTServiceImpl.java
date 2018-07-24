package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.ProductSMTService;

@Service("productSMTService")
public class ProductSMTServiceImpl implements ProductSMTService{
	@Autowired
	private BaseDao baseDao;	
	
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveProductSMT(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		baseDao.asserts.nonExistCode("ProductSMT", "ps_code", store.get("ps_code"));
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, store, grid);
		// 保存ProductSMT
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "ProductSMT"));
		baseDao.execute(SqlUtil.getInsertSqlbyList(grid, "ProductSMTLocation", "psl_id"));
		// 记录操作
		baseDao.logger.save(caller, "ps_code", store.get("ps_code"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void deleteProductSMT(int ps_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("ProductSMT", "ps_statuscode", "ps_id=" + ps_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { ps_id });
		// 删除ProductSMT
		baseDao.deleteById("ProductSMT", "ps_id", ps_id);
		// 删除ProductSMTLocation
		baseDao.deleteById("PRODUCTSMTLOCATION", "psl_psid", ps_id);
		// 记录操作
		baseDao.logger.delete(caller, "ps_id", ps_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { ps_id });
	}

	@Override
	public void updateProductSMTById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("ProductSMT", "ps_statuscode", "ps_id=" + store.get("ps_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, store, gstore);
		// 修改ProductSMT
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "ProductSMT", "ps_id"));
		// 修改ProductSMTLocation
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "ProductSMTLocation", "psl_id"));
		// 记录操作
		baseDao.logger.update(caller, "ps_id", store.get("ps_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, store, gstore);
	}
	@Override
	public void printProductSMT(int ps_id,String caller) {
		//执行打印前的其它逻辑
		handlerService.beforePrint(caller, new Object[]{ps_id});
		//执行打印操作
		//记录操作
		baseDao.logger.print(caller, "ps_id", ps_id);
		//执行打印后的其它逻辑
		handlerService.afterPrint(caller, new Object[]{ps_id});
	}
	
	@Override
	public void auditProductSMT(int ps_id,String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ProductSMT", "ps_statuscode", "ps_id=" + ps_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{ps_id});
		//执行审核操作
		baseDao.audit("ProductSMT", "ps_id=" + ps_id, "ps_status", "ps_statuscode", "ps_auditdate", "ps_auditman");
		//记录操作
		baseDao.logger.audit(caller,  "ps_id", ps_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{ps_id});
	}
	
	@Override
	public void resAuditProductSMT(int ps_id,String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ProductSMT", "ps_statuscode", "ps_id=" + ps_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resAudit("ProductSMT", "ps_id=" + ps_id, "ps_status", "ps_statuscode", "ps_auditdate", "ps_auditman");
		//记录操作
		baseDao.logger.resAudit(caller, "ps_id" ,ps_id);
	}
	
	@Override
	public void submitProductSMT(int ps_id,String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ProductSMT", "ps_statuscode", "ps_id=" + ps_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{ps_id});
		//执行提交操作
		baseDao.submit("ProductSMT", "ps_id=" + ps_id, "ps_status", "ps_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "ps_id", ps_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{ps_id});
	}
	
	@Override
	public void resSubmitProductSMT(int ps_id,String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ProductSMT", "ps_statuscode", "ps_id=" + ps_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[]{ps_id});
		//执行反提交操作
		baseDao.resOperate("ProductSMT", "ps_id=" + ps_id, "ps_status", "ps_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "ps_id", ps_id);
		handlerService.afterResSubmit(caller, new Object[]{ps_id});
	}
}

