package com.uas.erp.service.pm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.StencilService;

@Service("stencilService")
public class StencilServiceImpl implements StencilService{

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveStencil(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Stencil",
				"st_code='" + store.get("st_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Stencil",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "st_id", store.get("st_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteStencil(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Stencil",
				"st_statuscode", "st_id=" + id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { id });
		// 删除
		baseDao.deleteById("Stencil", "st_id", id);
		// 记录操作
		baseDao.logger.delete(caller, "st_id", id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { id });
	}

	@Override
	public void updateStencilById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的采购单资料!
		Object status = baseDao.getFieldDataByCondition("Stencil",
				"st_statuscode", "st_id=" + store.get("st_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Stencil",
				"st_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "st_id", store.get("st_id"));
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void submitStencil(int id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Stencil",
				"st_statuscode", "st_id=" + id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { id });
		// 执行提交操作
		baseDao.submit("Stencil", "st_id=" + id, "st_status",
				"st_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "st_id", id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { id });
	}

	@Override
	public void resSubmitStencil(int id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Stencil",
				"st_statuscode", "st_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { id });
		// 执行反提交操作
		baseDao.resOperate("Stencil", "st_id=" + id, "st_status",
				"st_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "st_id", id);
		handlerService.afterResSubmit(caller, new Object[] { id });
	}

	@Override
	public void auditStencil(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Stencil",
				"st_statuscode", "st_id=" + id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { id });
		baseDao.audit("Stencil", "st_id=" + id, "st_status",
				"st_statuscode", "st_auditdate", "st_auditman");
		baseDao.logger.audit(caller, "st_id", "st_id");
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { id });
	}

	@Override
	public void resAuditStencil(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Stencil",
				"st_statuscode", "st_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("Stencil", "st_id=" + id, "st_status",
				"st_statuscode", "st_auditdate", "st_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "st_id", id);
	}

}
