package com.uas.erp.service.pm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.LineService;

@Service
public class LineServiceImpl implements LineService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveLine(String formStore, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Line",
				"li_code='" + store.get("li_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Line",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
					baseDao.logger.save(caller, "li_id", store.get("li_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void updateLineById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的采购单资料!
		Object status = baseDao.getFieldDataByCondition("Line",
				"li_statuscode", "li_id=" + store.get("li_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Line",
				"li_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "li_id", store.get("li_id"));
		// 更新上次采购价格、供应商
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteLine(int li_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Line",
				"li_statuscode", "li_id=" + li_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { li_id });
		// 删除
		baseDao.deleteById("Line", "li_id", li_id);
		// 记录操作
		baseDao.logger.delete(caller, "li_id", li_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { li_id });
	}

	@Override
	public void auditLine(int li_id, String caller) {

		Object status = baseDao.getFieldDataByCondition("Line",
				"li_statuscode", "li_id=" + li_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { li_id });
		baseDao.audit("Line", "li_id=" + li_id, "li_status",
				"li_statuscode", "li_auditdate", "li_auditman");
		//记录操作
		baseDao.logger.audit(caller, "li_id", li_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { li_id });
	}

	@Override
	public void resAuditLine(int li_id, String caller) {

		Object status = baseDao.getFieldDataByCondition("Line",
				"li_statuscode", "li_id=" + li_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("Line", "li_id=" + li_id, "li_status",
				"li_statuscode", "li_auditdate", "li_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "li_id", li_id);
	}

	@Override
	public void submitLine(int li_id, String caller) {

		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Line",
				"li_statuscode", "li_id=" + li_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { li_id });
		// 执行提交操作
		baseDao.submit("Line", "li_id=" + li_id, "li_status",
				"li_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "li_id", li_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { li_id });
	}

	@Override
	public void resSubmitLine(int li_id, String caller) {

		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Line",
				"li_statuscode", "li_id=" + li_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { li_id });
		// 执行反提交操作
		baseDao.resOperate("Line", "li_id=" + li_id, "li_status",
				"li_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "li_id", li_id);
		handlerService.afterResSubmit(caller, new Object[] { li_id });
	}

}
