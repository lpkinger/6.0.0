package com.uas.erp.service.pm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.SourceService;

@Service
public class SourceServiceImpl implements SourceService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveSource(String formStore, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Source",
				"sc_code='" + store.get("sc_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Source",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
					baseDao.logger.save(caller, "sc_id", store.get("sc_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void updateSourceById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的采购单资料!
		Object status = baseDao.getFieldDataByCondition("Source",
				"sc_statuscode", "sc_id=" + store.get("sc_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Source",
				"sc_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "sc_id", store.get("sc_id"));
		// 更新上次采购价格、供应商
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteSource(int sc_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Source",
				"sc_statuscode", "sc_id=" + sc_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { sc_id });
		// 删除
		baseDao.deleteById("Source", "sc_id", sc_id);
		// 记录操作
		baseDao.logger.delete(caller, "sc_id", sc_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { sc_id });
	}

	@Override
	public void auditSource(int sc_id, String caller) {

		Object status = baseDao.getFieldDataByCondition("Source",
				"sc_statuscode", "sc_id=" + sc_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { sc_id });
		baseDao.audit("Source", "sc_id=" + sc_id, "sc_status",
				"sc_statuscode", "sc_auditdate", "sc_auditman");
		baseDao.logger.audit(caller, "sc_id", "sc_id");
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { sc_id });
	}

	@Override
	public void resAuditSource(int sc_id, String caller) {

		Object status = baseDao.getFieldDataByCondition("Source",
				"sc_statuscode", "sc_id=" + sc_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("Source", "sc_id=" + sc_id, "sc_status",
				"sc_statuscode", "sc_auditdate", "sc_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "sc_id", sc_id);
	}

	@Override
	public void submitSource(int sc_id, String caller) {

		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Source",
				"sc_statuscode", "sc_id=" + sc_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { sc_id });
		// 执行提交操作
		baseDao.submit("Source", "sc_id=" + sc_id, "sc_status",
				"sc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "sc_id", sc_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { sc_id });
	}

	@Override
	public void resSubmitSource(int sc_id, String caller) {

		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Source",
				"sc_statuscode", "sc_id=" + sc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { sc_id });
		// 执行反提交操作
		baseDao.resOperate("Source", "sc_id=" + sc_id, "sc_status",
				"sc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "sc_id", sc_id);
		handlerService.afterResSubmit(caller, new Object[] { sc_id });
	}

}
