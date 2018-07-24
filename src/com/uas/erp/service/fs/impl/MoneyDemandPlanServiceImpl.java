package com.uas.erp.service.fs.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.fs.MoneyDemandPlanService;

@Service
public class MoneyDemandPlanServiceImpl implements MoneyDemandPlanService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	@Transactional
	public void saveMoneyDemandPlan(String formStore, String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 执行保存操作
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "MoneyDemandPlan"));
		baseDao.execute(SqlUtil.getInsertSqlbyList(grid, "MoneyDemandPlanDetail", "mpd_id"));
		updateDate(store.get("mp_id"));
		// 记录操作
		baseDao.logger.save(caller, "mp_id", store.get("mp_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });

	}

	@Override
	@Transactional
	public void updateMoneyDemandPlan(String formStore, String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "MoneyDemandPlan", "mp_id"));
		// 修改CraftDetail
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "MoneyDemandPlanDetail", "mpd_id"));
		updateDate(store.get("mp_id"));
		// 记录操作
		baseDao.logger.update(caller, "mp_id", store.get("mp_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void deleteMoneyDemandPlan(int mp_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { mp_id });
		// 删除MoneyDemandPlan
		baseDao.deleteById("MoneyDemandPlan", "mp_id", mp_id);
		// 删除MoneyDemandPlanDETAIL
		baseDao.deleteById("MoneyDemandPlanDETAIL", "mpd_mpid", mp_id);
		// 记录操作
		baseDao.logger.delete(caller, "mp_id", mp_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { mp_id });
	}

	@Override
	public void submitMoneyDemandPlan(int mp_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		SqlRowList rs = baseDao.queryForRowSet("select mp_statuscode from MoneyDemandPlan where mp_id=?", mp_id);
		if (rs.next()) {
			updateDate(mp_id);
			StateAssert.submitOnlyEntering(rs.getObject("mp_statuscode"));
			// 执行提交前的其它逻辑
			handlerService.beforeSubmit(caller, new Object[] { mp_id });
			// 执行提交操作
			baseDao.submit("MoneyDemandPlan", "mp_id=" + mp_id, "mp_status", "mp_statuscode");
			// 记录操作
			baseDao.logger.submit(caller, "mp_id", mp_id);
			// 执行提交后的其它逻辑
			handlerService.afterSubmit(caller, new Object[] { mp_id });
		}
	}

	@Override
	public void resSubmitMoneyDemandPlan(int mp_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MoneyDemandPlan", "mp_statuscode", "mp_id=" + mp_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, mp_id);
		// 执行反提交操作
		baseDao.resOperate("MoneyDemandPlan", "mp_id=" + mp_id, "mp_status", "mp_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "mp_id", mp_id);
		// 执行提交后的其它逻辑
		handlerService.afterResSubmit(caller, mp_id);
	}

	@Override
	@Transactional
	public void auditMoneyDemandPlan(int mp_id, String caller) {
		SqlRowList rs = baseDao.queryForRowSet("select mp_statuscode from MoneyDemandPlan where mp_id=?", mp_id);
		if (rs.next()) {
			StateAssert.auditOnlyCommited(rs.getObject("mp_statuscode"));
			// 执行审核前的其它逻辑
			handlerService.beforeAudit(caller, new Object[] { mp_id });
			// 执行审核操作
			baseDao.audit("MoneyDemandPlan", "mp_id=" + mp_id, "mp_status", "mp_statuscode", "mp_auditdate", "mp_auditman");
			// 记录操作
			baseDao.logger.audit(caller, "mp_id", mp_id);
			// 执行审核后的其它逻辑
			handlerService.afterAudit(caller, new Object[] { mp_id });
		}
	}

	@Override
	public void resAuditMoneyDemandPlan(int mp_id, String caller) {
		// 只能对状态为[已审核]的采购单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("MoneyDemandPlan", "mp_statuscode", "mp_id=" + mp_id);
		StateAssert.resAuditOnlyAudit(status);
		baseDao.resAuditCheck("MoneyDemandPlan", mp_id);
		handlerService.beforeResAudit(caller, new Object[] { mp_id });
		// 执行反审核操作
		baseDao.resAudit("MoneyDemandPlan", "mp_id=" + mp_id, "mp_status", "mp_statuscode", "mp_auditman", "mp_auditdate");
		// 记录操作
		baseDao.logger.resAudit(caller, "mp_id", mp_id);
		handlerService.afterResAudit(caller, new Object[] { mp_id });
	}

	private void updateDate(Object mp_id) {
		baseDao.execute("update moneydemandplan set mp_begindate=(select min(mpd_date) from moneydemandplandetail where mpd_mpid=mp_id) where mp_id="
				+ mp_id);
		baseDao.execute("update moneydemandplan set mp_enddate=(select max(mpd_date) from moneydemandplandetail where mpd_mpid=mp_id) where mp_id="
				+ mp_id);
	}

}
