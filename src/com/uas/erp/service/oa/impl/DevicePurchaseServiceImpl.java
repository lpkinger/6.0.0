package com.uas.erp.service.oa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.DevicePurchaseService;

@Service
public class DevicePurchaseServiceImpl implements DevicePurchaseService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveDevicePurchase(String formStore,String param, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("DevicePurchase",
				"dp_code='" + store.get("dp_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "DevicePurchase",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "DevicePurchaseDetail", "dpd_id");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.save(caller, "dp_id", store.get("dp_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void updateDevicePurchase(String formStore,String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param);
		// 只能修改[在录入]的采购单资料!
		Object status = baseDao.getFieldDataByCondition("DevicePurchase",
				"dp_statuscode", "dp_id=" + store.get("dp_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "DevicePurchase",
				"dp_id");
		baseDao.execute(formSql);
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(grid, "DevicePurchaseDetail", "dpd_id"));
		// 记录操作
		baseDao.logger.update(caller, "dp_id", store.get("dp_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteDevicePurchase(int dp_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("DevicePurchase",
				"dp_statuscode", "dp_id=" + dp_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { dp_id });
		// 删除
		baseDao.deleteById("DevicePurchase", "dp_id", dp_id);
		baseDao.deleteById("DevicePurchaseDetail", "dpd_dpid", dp_id);
		// 记录操作
		baseDao.logger.delete(caller, "dp_id", dp_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { dp_id });
	}

	@Override
	public void auditDevicePurchase(int dp_id, String caller) {

		Object status = baseDao.getFieldDataByCondition("DevicePurchase",
				"dp_statuscode", "dp_id=" + dp_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { dp_id });
		baseDao.audit("DevicePurchase", "dp_id=" + dp_id, "dp_status",
				"dp_statuscode", "dp_auditdate", "dp_auditman");
		baseDao.logger.audit(caller, "dp_id", dp_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { dp_id });
	}

	@Override
	public void resAuditDevicePurchase(int dp_id, String caller) {

		Object status = baseDao.getFieldDataByCondition("DevicePurchase",
				"dp_statuscode", "dp_id=" + dp_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("DevicePurchase", "dp_id=" + dp_id, "dp_status",
				"dp_statuscode", "dp_auditdate", "dp_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "dp_id", dp_id);
	}

	@Override
	public void submitDevicePurchase(int dp_id, String caller) {

		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("DevicePurchase",
				"dp_statuscode", "dp_id=" + dp_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { dp_id });
		// 执行提交操作
		baseDao.submit("DevicePurchase", "dp_id=" + dp_id, "dp_status",
				"dp_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "dp_id", dp_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { dp_id });
	}

	@Override
	public void resSubmitDevicePurchase(int dp_id, String caller) {

		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("DevicePurchase",
				"dp_statuscode", "dp_id=" + dp_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { dp_id });
		// 执行反提交操作
		baseDao.resOperate("DevicePurchase", "dp_id=" + dp_id, "dp_status",
				"dp_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "dp_id", dp_id);
		handlerService.afterResSubmit(caller, new Object[] { dp_id });
	}

}
