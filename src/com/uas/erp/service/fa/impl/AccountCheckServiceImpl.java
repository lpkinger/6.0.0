package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.service.fa.AccountCheckService;

@Service("accountCheckService")
public class AccountCheckServiceImpl implements AccountCheckService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private TransferRepository transferRepository;

	@Override
	public void saveAccountCheck(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		String code = store.get("acc_code").toString();
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("AccountCheck", "acc_code='" + code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { formStore, gridStore });
		// 保存AccountCheck
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "AccountCheck"));
		baseDao.execute(SqlUtil.getInsertSqlbyList(grid, "AccountCheckDetail", "acd_id"));
		baseDao.execute("update AccountCheckDetail set acd_checkstatus='未对账' where acd_acid=" + store.get("acc_id"));
		baseDao.logger.save("AccountCheck", "acc_id", store.get("acc_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { formStore, gridStore });
	}

	@Override
	public void deleteAccountCheck(int acc_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("AccountCheck", "acc_statuscode", "acc_id=" + acc_id);
		StateAssert.delOnlyEntering(status);
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(acd_detno) from AccountCheckDetail where acd_acid=? and nvl(acd_checkstatus,'未对账')='已对账'", String.class,
				acc_id);
		if (dets != null) {
			BaseUtil.showError("存在已对账的明细行，不允许删除！行" + dets);
		}
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { acc_id });
		// 删除AccountCheck
		baseDao.deleteById("AccountCheck", "acc_id", acc_id);
		// 删除AccountCheckDetail
		baseDao.deleteById("AccountCheckdetail", "acd_acid", acc_id);
		// 记录操作
		baseDao.logger.delete("AccountCheck", "acc_id", acc_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { acc_id });
	}

	@Override
	public void updateAccountCheck(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("AccountCheck", "acc_statuscode", "acc_id=" + store.get("acc_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, store, gstore);
		// 修改MakeCraft
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "AccountCheck", "acc_id"));
		// 修改MakeCraftDetail
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "AccountCheckdetail", "acd_id"));
		baseDao.execute("update AccountCheckDetail set acd_checkstatus='未对账' where acd_acid=" + store.get("acc_id"));
		// 记录操作
		baseDao.logger.update(caller, "mc_id", store.get("mc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, store, gstore);
	}

	@Override
	public void auditAccountCheck(int acc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("AccountCheck", "acc_statuscode", "acc_id=" + acc_id);
		StateAssert.auditOnlyCommited(status);
		check(acc_id);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { acc_id });
		// 执行审核操作
		baseDao.updateByCondition("AccountCheck", "acc_statuscode='AUDITED',acc_status='" + BaseUtil.getLocalMessage("AUDITED") + "'",
				"acc_id=" + acc_id);
		// 记录操作
		baseDao.logger.audit("AccountCheck", "acc_id", acc_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { acc_id });
	}

	@Override
	public void resAuditAccountCheck(int acc_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("AccountCheck", "acc_statuscode", "acc_id=" + acc_id);
		StateAssert.resAuditOnlyAudit(status);
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(acd_detno) from AccountCheckDetail where acd_acid=? and nvl(acd_checkstatus,'未对账')='已对账'", String.class,
				acc_id);
		if (dets != null) {
			BaseUtil.showError("存在已对账的明细行，不允许反审核！行" + dets);
		}
		// 执行反审核操作
		baseDao.updateByCondition("AccountCheck", "acc_statuscode='ENTERING',acc_status='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"acc_id=" + acc_id);
		// 记录操作
		baseDao.logger.resAudit("AccountCheck", "acc_id", acc_id);
	}

	void check(int acc_id) {
		baseDao.execute("update AccountCheckDetail set acd_checkstatus='未对账' where acd_acid=" + acc_id);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(acd_detno) from AccountCheck left join AccountCheckDetail on acc_id=acd_acid where acc_id=? and to_char(acd_date,'yyyymm')<>acc_yearmonth",
						String.class, acc_id);
		if (dets != null) {
			BaseUtil.showError("明细行日期不在当前票据资金期间！行" + dets);
		}
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(acd_detno) from AccountCheckDetail where acd_acid=? and nvl(acd_debit,0)<>0 and nvl(acd_credit,0)<>0",
				String.class, acc_id);
		if (dets != null) {
			BaseUtil.showError("明细行借方、贷方不能同时有值！行" + dets);
		}
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(acd_detno) from AccountCheckDetail where acd_acid=? and nvl(acd_debit,0)=0 and nvl(acd_credit,0)=0",
				String.class, acc_id);
		if (dets != null) {
			BaseUtil.showError("明细行借方、贷方不能同时无值！行" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(acd_detno) from AccountCheckDetail where acd_acid=? and not exists (select 1 from category where acd_catecode=ca_code)",
						String.class, acc_id);
		if (dets != null) {
			BaseUtil.showError("明细行账户编号不存在！行" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(acd_detno) from AccountCheckDetail where acd_acid=? and not exists (select 1 from category where acd_catecode=ca_code and ca_statuscode='AUDITED' AND NVL(ca_iscashbank,0)<>0)",
						String.class, acc_id);
		if (dets != null) {
			BaseUtil.showError("明细行账户编号必须是已审核且是否现金银行为是的科目！行" + dets);
		}
	}

	@Override
	public void submitAccountCheck(int acc_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("AccountCheck", "acc_statuscode", "acc_id=" + acc_id);
		StateAssert.submitOnlyEntering(status);
		check(acc_id);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { acc_id });
		// 执行提交操作
		baseDao.updateByCondition("AccountCheck", "acc_statuscode='COMMITED',acc_status='" + BaseUtil.getLocalMessage("COMMITED") + "'",
				"acc_id=" + acc_id);
		// 记录操作
		baseDao.logger.submit("AccountCheck", "acc_id", acc_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { acc_id });
	}

	@Override
	public void resSubmitAccountCheck(int acc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("AccountCheck", "acc_statuscode", "acc_id=" + acc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { acc_id });
		// 执行反提交操作
		baseDao.updateByCondition("AccountCheck", "acc_statuscode='ENTERING',acc_status='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"acc_id=" + acc_id);
		// 记录操作
		baseDao.logger.resSubmit("AccountCheck", "acc_id", acc_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { acc_id });
	}

	@Override
	public List<Map<String, Object>> getAccountCheck(String caller, int yearmonth, String status, String catecode) {
		String sql = "SELECT acd_id,acd_date,acd_explanation,acd_debit,acd_credit,acd_catecode,ca_name,ca_description,nvl(acd_checkstatus,'未对账') acd_checkstatus "
				+ "from ACCOUNTCHECK left join ACCOUNTCHECKdetail on acc_id=acd_acid left join category on acd_catecode=ca_code "
				+ "where acc_statuscode='AUDITED' and to_char(acd_date,'yyyymm')=" + yearmonth;
		if (!"全部".equals(status) && !"$ALL".equals(status)) {
			sql = sql + " and nvl(acd_checkstatus,'未对账')='" + status + "'";
		}
		if (StringUtil.hasText(catecode)) {
			sql = sql + " and acd_catecode='" + catecode + "'";
		}
		sql = sql + " order by acd_date";
		SqlRowList list = baseDao.queryForRowSet(sql);
		return list.getResultList();
	}

	@Override
	public List<Map<String, Object>> getAccountRegister(String caller, int yearmonth, String status, String catecode) {
		String sql = "SELECT ar_id,ar_code,ar_date,ar_memo,ar_deposit,ar_payment,ar_accountcode,ca_name,ca_description,nvl(ar_checkstatus,'未对账') ar_checkstatus "
				+ "from AccountRegister left join category on ar_accountcode=ca_code "
				+ "where ar_statuscode='POSTED' and to_char(ar_date,'yyyymm')=" + yearmonth;
		if (!"全部".equals(status) && !"$ALL".equals(status)) {
			sql = sql + " and nvl(ar_checkstatus,'未对账')='" + status + "'";
		}
		if (StringUtil.hasText(catecode)) {
			sql = sql + " and ar_accountcode='" + catecode + "'";
		}
		sql = sql + " order by ar_date";
		SqlRowList list = baseDao.queryForRowSet(sql);
		return list.getResultList();
	}

	@Override
	public void autoCheck(int yearmonth, String caller) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select acd_id,round(nvl(acd_debit,0)-nvl(acd_credit,0),2) amount,acd_catecode from ACCOUNTCHECK left join ACCOUNTCHECKdetail on acc_id=acd_acid where acc_statuscode='AUDITED' and to_char(acd_date,'yyyymm')=? and nvl(acd_checkstatus,'未对账')='未对账' order by acd_date",
						yearmonth);
		while (rs.next()) {
			double amount = rs.getGeneralDouble("amount");
			int acdid = rs.getGeneralInt("acd_id");
			String catecode = rs.getGeneralString("acd_catecode");
			SqlRowList ar = baseDao
					.queryForRowSet(
							"select ar_id from accountregister where ar_statuscode='POSTED' and to_char(ar_date,'yyyymm')=? and nvl(ar_checkstatus,'未对账')='未对账' and ar_accountcode=? and round(nvl(ar_deposit,0)-nvl(ar_payment,0),2)=? order by ar_date",
							yearmonth, catecode, amount);
			if (ar.next()) {
				int arid = ar.getGeneralInt("ar_id");
				baseDao.execute("update ACCOUNTCHECKdetail set acd_checkstatus='已对账' where acd_id=" + acdid);
				baseDao.execute("update accountregister set ar_checkstatus='已对账' where ar_id=" + arid);
			}
		}
	}

	@Override
	public void confirmCheck(String data1, String data2) {
		List<Map<Object, Object>> store1 = BaseUtil.parseGridStoreToMaps(data1);// 银行对账单
		List<Map<Object, Object>> store2 = BaseUtil.parseGridStoreToMaps(data2);// 银行登记
		if (store1.size() == 0) {
			BaseUtil.showError("未勾选银行对账单！");
		}
		if (store2.size() == 0) {
			BaseUtil.showError("未勾选银行登记单！");
		}
		String acids = CollectionUtil.pluckSqlString(store1, "acd_id");
		String arids = CollectionUtil.pluckSqlString(store2, "ar_id");
		StringBuffer sb = new StringBuffer();
		SqlRowList rs = baseDao
				.queryForRowSet("select sum(round(nvl(acd_debit,0)-nvl(acd_credit,0),2)) amount,acd_catecode from ACCOUNTCHECK left join ACCOUNTCHECKdetail on acc_id=acd_acid where acd_id in ("
						+ acids + ") and nvl(acd_checkstatus,'未对账')='未对账' group by acd_catecode");
		while (rs.next()) {
			double acamount = rs.getGeneralDouble("amount");
			String catecode = rs.getGeneralString("acd_catecode");
			SqlRowList ar = baseDao
					.queryForRowSet("select sum(round(nvl(ar_deposit,0)-nvl(ar_payment,0),2)) aramount from accountregister where ar_id in ("
							+ arids + ") and ar_accountcode='" + catecode + "' and nvl(ar_checkstatus,'未对账')='未对账'");
			if (ar.hasNext()) {
				while (ar.next()) {
					if (acamount != ar.getGeneralDouble("aramount")) {
						sb.append("账户编号[" + catecode + "]所勾选金额对账金额不一致").append("<hr>");
					} else {
						baseDao.execute("update ACCOUNTCHECKdetail set acd_checkstatus='已对账' where acd_id in (" + acids
								+ ") and acd_catecode='" + catecode + "'");
						baseDao.execute("update accountregister set ar_checkstatus='已对账' where ar_id in (" + arids
								+ ") and ar_accountcode='" + catecode + "'");
					}
				}
			} else {
				sb.append("账户编号[" + catecode + "]没有勾选对应的银行登记！").append("<hr>");
			}
		}
		if (sb.length() > 0) {
			BaseUtil.appendError(sb.toString());
		}
	}

	@Override
	public void cancelCheck(String data1, String data2) {
		List<Map<Object, Object>> store1 = BaseUtil.parseGridStoreToMaps(data1);// 银行对账单
		List<Map<Object, Object>> store2 = BaseUtil.parseGridStoreToMaps(data2);// 银行登记
		if (store1.size() == 0) {
			BaseUtil.showError("未勾选银行对账单！");
		}
		if (store2.size() == 0) {
			BaseUtil.showError("未勾选银行登记单！");
		}
		String acids = CollectionUtil.pluckSqlString(store1, "acd_id");
		String arids = CollectionUtil.pluckSqlString(store2, "ar_id");
		StringBuffer sb = new StringBuffer();
		SqlRowList rs = baseDao
				.queryForRowSet("select sum(round(nvl(acd_debit,0)-nvl(acd_credit,0),2)) amount,acd_catecode from ACCOUNTCHECK left join ACCOUNTCHECKdetail on acc_id=acd_acid where acd_id in ("
						+ acids + ") and nvl(acd_checkstatus,'未对账')='已对账' group by acd_catecode");
		while (rs.next()) {
			double acamount = rs.getGeneralDouble("amount");
			String catecode = rs.getGeneralString("acd_catecode");
			SqlRowList ar = baseDao
					.queryForRowSet("select sum(round(nvl(ar_deposit,0)-nvl(ar_payment,0),2)) aramount from accountregister where ar_id in ("
							+ arids + ") and ar_accountcode='" + catecode + "' and nvl(ar_checkstatus,'未对账')='已对账'");
			if (ar.hasNext()) {
				while (ar.next()) {
					if (acamount != ar.getGeneralDouble("aramount")) {
						sb.append("账户编号[" + catecode + "]所勾选金额取消对账金额不一致").append("<hr>");
					} else {
						baseDao.execute("update ACCOUNTCHECKdetail set acd_checkstatus='未对账' where acd_id in (" + acids
								+ ") and acd_catecode='" + catecode + "'");
						baseDao.execute("update accountregister set ar_checkstatus='未对账' where ar_id in (" + arids
								+ ") and ar_accountcode='" + catecode + "'");
					}
				}
			} else {
				sb.append("账户编号[" + catecode + "]没有勾选对应的银行登记！").append("<hr>");
			}
		}
		if (sb.length() > 0) {
			BaseUtil.appendError(sb.toString());
		}
	}
}
