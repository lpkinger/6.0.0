package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.ProductAssessService;

@Service
public class ProductAssessServiceImpl implements ProductAssessService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProductAssess(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String code = store.get("pa_code") + "";
		// 编号要求M开头
		if (!code.startsWith("M")) {
			store.put("pa_code", "M" + code);
		}
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ProductAssess", "pa_code='" + store.get("pa_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ProductAssess", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "pa_id", store.get("pa_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteProductAssess(int pa_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, pa_id);
		// 删除purchase
		baseDao.deleteById("ProductAssess", "pa_id", pa_id);
		// 记录操作
		baseDao.logger.delete(caller, "pa_id", pa_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, pa_id);
	}

	@Override
	public void updateProductAssess(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		String code = store.get("pa_code") + "";
		// 编号要求M开头
		if (!code.startsWith("M")) {
			store.put("pa_code", "M" + code);
		}
		// 修改ProductAssess
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProductAssess", "pa_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "pa_id", store.get("pa_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void submitProductAssess(int pa_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ProductAssess", "pa_statuscode", "pa_id=" + pa_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, pa_id);
		// 执行提交操作
		baseDao.submit("ProductAssess", "pa_id=" + pa_id, "pa_status", "pa_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pa_id", pa_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, pa_id);
	}

	@Override
	public void resSubmitProductAssess(int pa_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ProductAssess", "pa_statuscode", "pa_id=" + pa_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		baseDao.resOperate("ProductAssess", "pa_id=" + pa_id, "pa_status", "pa_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pa_id", pa_id);
	}

	@Override
	public void auditProductAssess(int pa_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ProductAssess", "pa_statuscode", "pa_id=" + pa_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, pa_id);
		// 执行审核操作
		baseDao.audit("ProductAssess", "pa_id=" + pa_id, "pa_status", "pa_statuscode", "pa_auditdate", "pa_auditor");
		// 记录操作
		baseDao.logger.audit(caller, "pa_id", pa_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, pa_id);
	}

	@Override
	public void resAuditProductAssess(int pa_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ProductAssess", "pa_statuscode", "pa_id=" + pa_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反提交操作
		baseDao.resOperate("ProductAssess", "pa_id=" + pa_id, "pa_status", "pa_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "pa_id", pa_id);
	}

	@Override
	public void turnProductApplication(int pa_id, String caller) {

	}
}
