package com.uas.erp.service.crm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.crm.MarketAnalyseService;

@Service
public class MarketAnalyseServiceImpl implements MarketAnalyseService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveMarketAnalyse(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"MarketAnalyse", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "ma_id", store.get("ma_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteMarketAnalyse(int ma_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, ma_id);
		// 删除purchase
		baseDao.deleteById("MarketAnalyse", "ma_id", ma_id);
		// 记录操作
		baseDao.logger.delete(caller, "ma_id", ma_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ma_id);
	}

	@Override
	public void updateMarketAnalyse(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 修改MarketAnalyse
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"MarketAnalyse", "ma_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "ma_id", store.get("ma_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void auditMarketAnalyse(int ma_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("MarketAnalyse",
				"ma_statuscode", "ma_id=" + ma_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ma_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"MarketAnalyse",
				"ma_statuscode='AUDITED',ma_status='"
						+ BaseUtil.getLocalMessage("AUDITED") + "'ma_auditer='"
						+ SystemSession.getUser().getEm_name()
						+ "',ma_auditdate=sysdate", "ma_id=" + ma_id);
		// 记录操作
		baseDao.logger.audit(caller, "ma_id", ma_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ma_id);
	}

	@Override
	public void resAuditMarketAnalyse(int ma_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("MarketAnalyse",
				"ma_statuscode", "ma_id=" + ma_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, ma_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"MarketAnalyse",
				"ma_statuscode='ENTERING',ma_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',ma_auditer='',ma_auditdate", "ma_id=" + ma_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ma_id", ma_id);
		handlerService.afterResAudit(caller, ma_id);
	}

	@Override
	public void submitMarketAnalyse(int ma_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MarketAnalyse",
				"ma_statuscode", "ma_id=" + ma_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ma_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"MarketAnalyse",
				"ma_statuscode='COMMITED',ma_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "ma_id="
						+ ma_id);
		// 记录操作
		baseDao.logger.submit(caller, "ma_id", ma_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ma_id);
	}

	@Override
	public void resSubmitMarketAnalyse(int ma_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MarketAnalyse",
				"ma_statuscode", "ma_id=" + ma_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, ma_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"MarketAnalyse",
				"ma_statuscode='ENTERING',ma_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "ma_id="
						+ ma_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ma_id", ma_id);
		handlerService.afterResSubmit(caller, ma_id);
	}

}
