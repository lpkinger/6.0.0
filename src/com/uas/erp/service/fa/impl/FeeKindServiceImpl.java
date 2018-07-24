package com.uas.erp.service.fa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.fa.FeeKindService;

@Service("FeeKindService")
public class FeeKindServiceImpl implements FeeKindService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveFeeKind(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String fk_code = store.get("fk_code").toString();
		// 当前编号的记录已经存在,不能新增!
		boolean bool1 = baseDao.checkByCondition("FeeKind", "fk_code='" + fk_code + "' ");
		if (!bool1) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		bool1 = baseDao.checkByCondition("FeeKind",
				"fk_controlway='" + store.get("fk_controlway") + "' and fk_name='" + store.get("fk_name") + "'");
		if (!bool1) {
			BaseUtil.showError("费用名称+额度管控方式已经存在！");
		}
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByMap(store, "FeeKind");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "fk_id", store.get("fk_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void updateFeeKind(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object[] status = baseDao.getFieldsDataByCondition("FeeKind", new String[] { "fk_statuscode" }, "fk_id=" + store.get("fk_id"));
		StateAssert.updateOnlyEntering(status[0]);
		boolean bool1 = baseDao.checkByCondition(
				"FeeKind",
				"fk_controlway='" + store.get("fk_controlway") + "' and fk_name='" + store.get("fk_name") + "' and fk_id<>"
						+ store.get("fk_id"));
		if (!bool1) {
			BaseUtil.showError("费用名称+额度管控方式已经存在！");
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 执行修改操作
		String sql = SqlUtil.getUpdateSqlByFormStore(store, "FeeKind", "fk_id");
		baseDao.execute(sql);
		// 记录操作
		baseDao.logger.update(caller, "fk_id", store.get("fk_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void deleteFeeKind(int fk_id, String caller) {
		// 只能删除[在录入]的客户资料
		Object status = baseDao.getFieldDataByCondition("FeeKind", "fk_statuscode", "fk_id=" + fk_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { fk_id });
		// 执行删除操作
		baseDao.deleteById("FeeKind", "fk_id", fk_id);
		// 记录操作
		baseDao.logger.delete(caller, "fk_id", fk_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, fk_id);
	}

	@Override
	public void submitFeeKind(int fk_id, String caller) {
		// 只能提交[在录入]的资料
		Object[] status = baseDao.getFieldsDataByCondition("FeeKind", new String[] { "fk_statuscode" }, "fk_id=" + fk_id);
		StateAssert.submitOnlyEntering(status[0]);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, fk_id);
		// 执行提交操作
		baseDao.updateByCondition("FeeKind", "fk_statuscode='COMMITED', fk_status='" + BaseUtil.getLocalMessage("COMMITED") + "'", "fk_id="
				+ fk_id);
		// 记录操作
		baseDao.logger.submit("FeeKind", "fk_id", fk_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, fk_id);
	}

	@Override
	public void resSubmitFeeKind(int fk_id, String caller) {
		// 只能对状态为[已提交]的进行反提交操作
		Object status = baseDao.getFieldDataByCondition("FeeKind", "fk_statuscode", "fk_id=" + fk_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, fk_id);
		baseDao.updateByCondition("FeeKind", "fk_statuscode='ENTERING', fk_status='" + BaseUtil.getLocalMessage("ENTERING") + "'", "fk_id="
				+ fk_id);
		// 记录操作
		baseDao.logger.resSubmit("FeeKind", "fk_id", fk_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, fk_id);
	}

	@Override
	public void auditFeeKind(int fk_id, String caller) {
		// 只能审核[已提交]
		Object[] status = baseDao.getFieldsDataByCondition("FeeKind", new String[] { "fk_statuscode" }, "fk_id=" + fk_id);
		if (!status[0].equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, fk_id);
		baseDao.updateByCondition("FeeKind", "fk_statuscode='AUDITED', fk_status='" + BaseUtil.getLocalMessage("AUDITED") + "'", "fk_id="
				+ fk_id);
		// 记录操作
		baseDao.logger.audit("FeeKind", "fk_id", fk_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, fk_id);
	}

	@Override
	public void resAuditFeeKind(int fk_id, String caller) {
		// 只能反审核[已审核]
		Object[] status = baseDao.getFieldsDataByCondition("FeeKind", new String[] { "fk_statuscode" }, "fk_id=" + fk_id);
		if (!status[0].equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, fk_id);
		// 执行反审核操作
		baseDao.updateByCondition("FeeKind", "fk_statuscode='ENTERING', fk_status='" + BaseUtil.getLocalMessage("ENTERING") + "'", "fk_id="
				+ fk_id);
		// 记录操作
		baseDao.logger.resAudit("FeeKind", "fk_id", fk_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, fk_id);
	}

}
