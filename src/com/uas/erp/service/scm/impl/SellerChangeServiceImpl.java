package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.SellerChangeService;

@Service
public class SellerChangeServiceImpl implements SellerChangeService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveSellerChange(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("sellerchange", "Sc_code='" + store.get("Sc_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "sellerchange", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存明细
		for (Map<Object, Object> m : grid) {
			m.put("scd_id", baseDao.getSeqId("SELLERCHANGEDETAIL_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "SELLERCHANGEDETAIL");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.save(caller, "sc_id", store.get("sc_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });	
	}
	
	@Override
	public void updateSellerChange(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的订单资料!
		Object status = baseDao.getFieldDataByCondition("Sellerchange", "sc_statuscode", "sc_id=" + store.get("sc_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 更新sale
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "sellerchange", "sc_id");
		baseDao.execute(formSql);
		// 更新Detail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "sellerchangedetail", "scd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("scd_id") == null || s.get("scd_id").equals("") || s.get("scd_id").equals("0")
					|| Integer.parseInt(s.get("scd_id").toString()) == 0) {// 新添加的数据，id不存在
				s.put("scd_id", baseDao.getSeqId("SELLERCHANGEDETAIL_SEQ"));
				String sql = SqlUtil.getInsertSqlByMap(s, "sellerchangedetail");
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "sc_id", store.get("sc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });		
	}
	
	@Override
	public void deleteSellerChange(int sc_id, String caller) {
		// 只能删除[在录入]的订单资料!
		Object status = baseDao.getFieldDataByCondition("Sellerchange", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, sc_id);
		// 删除sale
		baseDao.deleteById("sellerchange", "sc_id", sc_id);
		// 删除saleDetail
		baseDao.deleteById("sellerchangedetail", "scd_scid", sc_id);
		// 记录操作
		baseDao.logger.delete(caller, "sc_id", sc_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, sc_id);
	}
	
	@Override
	public void submitSellerChange(int sc_id, String caller) {
		// 只能提交状态为[在录入]的合同!
		Object status = baseDao.getFieldDataByCondition("sellerchange", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.submitOnlyEntering(status);
		handlerService.beforeSubmit(caller, sc_id);
		baseDao.submit("sellerchange", "sc_id=" + sc_id, "sc_status", "sc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "sc_id", sc_id);
		handlerService.afterSubmit(caller, sc_id);
	}
	
	@Override
	public void resSubmitSellerChange(int sc_id, String caller) {
		// 只能对状态为[已提交]的合同进行反提交
		Object status = baseDao.getFieldDataByCondition("sellerchange", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, sc_id);
		// 执行反提交操作
		baseDao.resOperate("sellerchange", "sc_id=" + sc_id, "sc_status", "sc_statuscode"); 
		// 记录操作
		baseDao.logger.resSubmit(caller, "sc_id", sc_id);
		handlerService.afterResSubmit(caller, sc_id); 		
	}
	
	@Override
	public void auditSellerChange(int sc_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("sellerchange", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.auditOnlyCommited(status);
		String res = null;
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, sc_id);
		res = baseDao.callProcedure("SP_SELLERCHANGE_SCM",  new Object[] {sc_id});
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
		// 执行审核操作
		baseDao.audit("sellerchange", "sc_id=" + sc_id, "sc_status", "sc_statuscode", "sc_auditdate", "sc_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "sc_id", sc_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, sc_id);		
	}

}
