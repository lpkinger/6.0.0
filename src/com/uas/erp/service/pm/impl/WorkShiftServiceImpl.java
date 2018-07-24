package com.uas.erp.service.pm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.WorkShiftService;

@Service
public class WorkShiftServiceImpl implements WorkShiftService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveWorkShift(String formStore, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("WorkShift",
				"ws_code='" + store.get("ws_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "WorkShift",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
					baseDao.logger.save(caller, "ws_id", store.get("ws_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void updateWorkShiftById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的采购单资料!
		Object status = baseDao.getFieldDataByCondition("WorkShift",
				"ws_statuscode", "ws_id=" + store.get("ws_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "WorkShift",
				"ws_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "ws_id", store.get("ws_id"));
		// 更新上次采购价格、供应商
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteWorkShift(int ws_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("WorkShift",
				"ws_statuscode", "ws_id=" + ws_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { ws_id });
		// 删除
		baseDao.deleteById("WorkShift", "ws_id", ws_id);
		// 记录操作
		baseDao.logger.delete(caller, "ws_id", ws_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { ws_id });
	}

	@Override
	public void auditWorkShift(int ws_id, String caller) {

		Object status = baseDao.getFieldDataByCondition("WorkShift",
				"ws_statuscode", "ws_id=" + ws_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { ws_id });
		baseDao.audit("WorkShift", "ws_id=" + ws_id, "ws_status",
				"ws_statuscode", "ws_auditdate", "ws_auditman");
		baseDao.logger.audit(caller, "ws_id", "ws_id");
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { ws_id });
	}

	@Override
	public void resAuditWorkShift(int ws_id, String caller) {

		Object status = baseDao.getFieldDataByCondition("WorkShift",
				"ws_statuscode", "ws_id=" + ws_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("WorkShift", "ws_id=" + ws_id, "ws_status",
				"ws_statuscode", "ws_auditdate", "ws_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "ws_id", ws_id);
	}

	@Override
	public void submitWorkShift(int ws_id, String caller) {

		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("WorkShift",
				"ws_statuscode", "ws_id=" + ws_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { ws_id });
		// 执行提交操作
		baseDao.submit("WorkShift", "ws_id=" + ws_id, "ws_status",
				"ws_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "ws_id", ws_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { ws_id });
	}

	@Override
	public void resSubmitWorkShift(int ws_id, String caller) {

		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("WorkShift",
				"ws_statuscode", "ws_id=" + ws_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { ws_id });
		// 执行反提交操作
		baseDao.resOperate("WorkShift", "ws_id=" + ws_id, "ws_status",
				"ws_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "ws_id", ws_id);
		handlerService.afterResSubmit(caller, new Object[] { ws_id });
	}

}
