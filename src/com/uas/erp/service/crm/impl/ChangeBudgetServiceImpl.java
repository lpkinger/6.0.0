package com.uas.erp.service.crm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;

import com.uas.erp.service.crm.ChangeBudgetService;

@Service
public class ChangeBudgetServiceImpl implements ChangeBudgetService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveChangeBudget(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ChangeBudget", "cb_code='"
				+ store.get("cb_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存ChangeBudget
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ChangeBudget",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存ChangeBudgetDetail
		Object[] cbd_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			cbd_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				cbd_id[i] = baseDao.getSeqId("ChangeBudgetDETAIL_SEQ");
			}
		} else {
			cbd_id[0] = baseDao.getSeqId("ChangeBudgetDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore,
				"ChangeBudgetDetail", "cbd_id", cbd_id);
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "cb_id", store.get("cb_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void updateChangeBudgetById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("ChangeBudget",
				"cb_statuscode", "cb_id=" + store.get("cb_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改ChangeBudget
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ChangeBudget",
				"cb_id");
		baseDao.execute(formSql);
		// 修改ChangeBudgetDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"ChangeBudgetDetail", "cbd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("cbd_id") == null || s.get("cbd_id").equals("")
					|| s.get("cbd_id").equals("0")
					|| Integer.parseInt(s.get("cbd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("ChangeBudgetDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "ChangeBudgetDetail",
						new String[] { "cbd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "cb_id", store.get("cb_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteChangeBudget(int cb_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("ChangeBudget",
				"cb_statuscode", "cb_id=" + cb_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, cb_id);
		// 删除ChangeBudget
		baseDao.deleteById("ChangeBudget", "cb_id", cb_id);
		// 删除ChangeBudgetDetail
		baseDao.deleteById("ChangeBudgetdetail", "cbd_cbid", cb_id);
		// 记录操作
		baseDao.logger.delete(caller, "cb_id", cb_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, cb_id);
	}

	@Override
	public void auditChangeBudget(int cb_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ChangeBudget",
				"cb_statuscode", "cb_id=" + cb_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, cb_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"ChangeBudget",
				"cb_statuscode='AUDITED',cb_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',cb_auditname='"
						+ SystemSession.getUser().getEm_name()
						+ "',cb_auditdate=sysdate", "cb_id=" + cb_id);
		// 修改预算及剩余费用
		changeAmount(cb_id);
		// 记录操作
		baseDao.logger.audit(caller, "cb_id", cb_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, cb_id);
	}

	@Override
	public void resAuditChangeBudget(int cb_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ChangeBudget",
				"cb_statuscode", "cb_id=" + cb_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, cb_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"ChangeBudget",
				"cb_statuscode='COMMITED',cb_status='"
						+ BaseUtil.getLocalMessage("COMMITED")
						+ "',cb_auditname='',cb_auditdate=''", "cb_id=" + cb_id);
		// 修改预算及剩余费用
		resChangeAmount(cb_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "cb_id", cb_id);
		handlerService.afterResAudit(caller, cb_id);
	}

	@Override
	public void submitChangeBudget(int cb_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ChangeBudget",
				"cb_statuscode", "cb_id=" + cb_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, cb_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"ChangeBudget",
				"cb_statuscode='COMMITED',cb_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "cb_id="
						+ cb_id);
		// 记录操作
		baseDao.logger.submit(caller, "cb_id", cb_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, cb_id);
	}

	@Override
	public void resSubmitChangeBudget(int cb_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ChangeBudget",
				"cb_statuscode", "cb_id=" + cb_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, cb_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"ChangeBudget",
				"cb_statuscode='ENTERING',cb_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "cb_id="
						+ cb_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "cb_id", cb_id);
		handlerService.afterResSubmit(caller, cb_id);
	}

	public void changeAmount(int cb_id) {
		String querySql = "SELECT cbd_newamount ,cbd_ppdid FROM ChangeBudgetDetail WHERE cbd_cbid= ?";
		String updateSql = "UPDATE ResearchProjectDetail SET ppd_amount= ?,ppd_surplus=?-ppd_used WHERE ppd_id= ?";
		SqlRowList rs = baseDao
				.queryForRowSet(querySql, new Object[] { cb_id });
		while (rs.next()) {
			baseDao.execute(
					updateSql,
					new Object[] { rs.getObject(1), rs.getObject(1),
							rs.getObject(2) });
		}
	}

	public void resChangeAmount(int cb_id) {
		String querySql = "SELECT cbd_amount ,cbd_ppdid FROM ChangeBudgetDetail WHERE cbd_cbid= ?";
		String updateSql = "UPDATE ResearchProjectDetail SET ppd_amount= ?,ppd_surplus=?-ppd_used WHERE ppd_id= ?";
		SqlRowList rs = baseDao
				.queryForRowSet(querySql, new Object[] { cb_id });
		while (rs.next()) {
			baseDao.execute(
					updateSql,
					new Object[] { rs.getObject(1), rs.getObject(1),
							rs.getObject(2) });
		}
	}
}
