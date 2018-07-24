package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.ProdChargeKindService;

@Service("prodChargeKindService")
public class ProdChargeKindServiceImpl implements ProdChargeKindService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProdChargeKind(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ProdChargeKind", "pck_code='" + store.get("pck_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存ProdChargeKind
		baseDao.execute(SqlUtil.getInsertSqlByFormStore(store, "ProdChargeKind", new String[] {}, new Object[] {}));
		baseDao.logger.save(caller, "pck_id", store.get("pck_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteProdChargeKind(int pck_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("ProdChargeKind", "pck_statuscode", "pck_id=" + pck_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, pck_id);
		baseDao.delCheck("ProdChargeKind", pck_id);
		// 删除ProdChargeKind
		baseDao.deleteById("ProdChargeKind", "pck_id", pck_id);
		// 记录操作
		baseDao.logger.delete(caller, "pck_id", pck_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, pck_id);
	}

	@Override
	public void updateProdChargeKindById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("ProdChargeKind", "pck_statuscode", "pck_id=" + store.get("pck_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 修改ProdChargeKind
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "ProdChargeKind", "pck_id"));
		// 记录操作
		baseDao.logger.update(caller, "pck_id", store.get("pck_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void auditProdChargeKind(int pck_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ProdChargeKind", "pck_statuscode", "pck_id=" + pck_id);
		StateAssert.auditOnlyCommited(status);
		check(pck_id);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, pck_id);
		// 执行审核操作
		baseDao.audit("ProdChargeKind", "pck_id=" + pck_id, "pck_status", "pck_statuscode");
		// 记录操作
		baseDao.logger.audit(caller, "pck_id", pck_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, pck_id);
	}

	private void check(int pck_id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('费用类型['||pck_name||']出入库类型['||pck_purpose||']') from ProdChargeKind a where pck_id="
								+ pck_id
								+ " and exists (select 1 from ProdChargeKind b where a.pck_name=b.pck_name and a.pck_purpose=b.pck_purpose and b.pck_id<>"
								+ pck_id + ")", String.class);
		if (dets != null) {
			BaseUtil.showError(dets + "不能重复！");
		}
	}

	@Override
	public void resAuditProdChargeKind(int pck_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ProdChargeKind", "pck_statuscode", "pck_id=" + pck_id);
		StateAssert.resAuditOnlyAudit(status);
		baseDao.resAuditCheck("ProdChargeKind", pck_id);
		// 执行反审核操作
		baseDao.resOperate("ProdChargeKind", "pck_id=" + pck_id, "pck_status", "pck_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "pck_id", pck_id);
	}

	@Override
	public void submitProdChargeKind(int pck_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ProdChargeKind", "pck_statuscode", "pck_id=" + pck_id);
		StateAssert.submitOnlyEntering(status);
		check(pck_id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, pck_id);
		// 执行提交操作
		baseDao.submit("ProdChargeKind", "pck_id=" + pck_id, "pck_status", "pck_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pck_id", pck_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, pck_id);
	}

	@Override
	public void resSubmitProdChargeKind(int pck_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ProdChargeKind", "pck_statuscode", "pck_id=" + pck_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeSubmit(caller, pck_id);
		// 执行反提交操作
		baseDao.resOperate("ProdChargeKind", "pck_id=" + pck_id, "pck_status", "pck_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pck_id", pck_id);
		handlerService.afterResSubmit(caller, pck_id);
	}
}
