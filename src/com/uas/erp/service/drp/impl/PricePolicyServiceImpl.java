package com.uas.erp.service.drp.impl;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.drp.PricePolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PricePolicyServiceImpl implements PricePolicyService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void savePricePolicy(String formStore, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("PricePolicy", "pp_code='"
				+ store.get("pp_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		handlerService.beforeSave(caller, new Object[] { store });

		String formSql = SqlUtil.getInsertSqlByFormStore(store, "pricepolicy",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "pp_id", store.get("pp_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });

	}

	@Override
	public void updatePricePolicyById(String formStore, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "pricepolicy",
				"pp_id");
		baseDao.execute(formSql);

		// 记录操作
		baseDao.logger.update(caller, "pp_id", store.get("pp_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void deletePricePolicy(int pp_id, String caller) {

		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, pp_id);
		// 删除purchase
		baseDao.deleteById("pricepolicy", "pp_id", pp_id);
		// 记录操作
		baseDao.logger.delete(caller, "pp_id", pp_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, pp_id);

	}

	@Override
	public void auditPricePolicy(int pp_id, String caller) {

		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("pricepolicy",
				"pp_statuscode", "pp_id=" + pp_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, pp_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"pricepolicy",
				"pp_statuscode='AUDITED',pp_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',pp_auditer='"
						+ SystemSession.getUser().getEm_name()
						+ "',pp_auditdate=sysdate", "pp_id=" + pp_id);
		// 记录操作
		baseDao.logger.audit(caller, "pp_id", pp_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, pp_id);

	}

	@Override
	public void resAuditPricePolicy(int pp_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("pricepolicy",
				"pp_statuscode", "pp_id=" + pp_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, pp_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"pricepolicy",
				"pp_statuscode='ENTERING',pp_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',pp_auditer='',pp_auditdate=null", "pp_id=" + pp_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "pp_id", pp_id);
		handlerService.afterResAudit(caller, pp_id);
	}

	@Override
	public void submitPricePolicy(int pp_id, String caller) {

		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("pricepolicy",
				"pp_statuscode", "pp_id=" + pp_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, pp_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"pricepolicy",
				"pp_statuscode='COMMITED',pp_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "pp_id="
						+ pp_id);
		// 记录操作
		baseDao.logger.submit(caller, "pp_id", pp_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, pp_id);

	}

	@Override
	public void resSubmitPricePolicy(int pp_id, String caller) {

		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("pricepolicy",
				"pp_statuscode", "pp_id=" + pp_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, pp_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"pricepolicy",
				"pp_statuscode='ENTERING',pp_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "pp_id="
						+ pp_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "pp_id", pp_id);
		handlerService.afterResSubmit(caller, pp_id);

	}

}
