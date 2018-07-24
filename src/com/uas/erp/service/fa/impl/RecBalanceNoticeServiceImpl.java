package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.Key;
import com.uas.erp.service.fa.AccountRegisterBankService;
import com.uas.erp.service.fa.RecBalanceNoticeService;

@Service("recBalanceNoticeService")
public class RecBalanceNoticeServiceImpl implements RecBalanceNoticeService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private VoucherDao voucherDao;
	@Autowired
	private TransferRepository transferRepository;
	@Autowired
	private AccountRegisterBankService accountRegisterBankService;

	@Override
	@Transactional
	public void saveRecBalanceNotice(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		String code = store.get("rb_code").toString();
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("RecBalanceNotice", "rb_code='" + code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		checkDate(store.get("rb_date").toString());
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存RecBalanceNotice
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "RecBalanceNotice"));
		baseDao.execute(SqlUtil.getInsertSqlbyList(grid, "RecBalanceNoticeDetail", "rbd_id"));
		getTotal(store.get("rb_id"), caller);
		// 记录操作
		baseDao.logger.save(caller, "rb_id", store.get("rb_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	/**
	 * 单据日期是否超期
	 */
	private void checkDate(String date) {
		int yearmonth = voucherDao.getPeriodsFromDate("Month-C", date);
		int nowym = voucherDao.getNowPddetno("Month-C");// 当前期间
		if (yearmonth < nowym) {
			BaseUtil.showError("期间" + yearmonth + "已经结转,当前账期在:" + nowym + "<br>请修改日期，或反结转应收账.");
		}
	}

	private void getTotal(Object id, String caller) {
		if ("RecBalanceNotice!YS".equals(caller)) {
			int count = baseDao.getCount("select count(1) from RecBalanceNoticeDetail where rbd_rbid=" + id);
			if (count > 0) {
				baseDao.execute("update RecBalanceNotice set rb_cmamount=round(nvl((select sum(nvl(rbd_amount,0)) from RecBalanceNoticeDetail where rb_id=rbd_rbid),0),2) where rb_id="
						+ id);
			}
		} else if ("RecBalanceNotice!PR".equals(caller)) {
			int count = baseDao.getCount("select count(1) from RecBalanceNoticeDetail where rbd_rbid=" + id
					+ " and nvl(rbd_sacode,' ')<>' '");
			if (count > 0) {
				baseDao.execute("update RecBalanceNotice set rb_cmamount=round(nvl((select sum(nvl(rbd_amount,0)) from RecBalanceNoticeDetail where rb_id=rbd_rbid),0),2) where rb_id="
						+ id);
			}
		}
		baseDao.execute("update RecBalanceNotice set rb_amount=rb_cmamount where rb_id=" + id + " and nvl(rb_amount,0)=0");
		baseDao.execute("update RecBalanceNotice set rb_actamount=rb_amount where rb_id=" + id + " and nvl(rb_actamount,0)=0");
		baseDao.execute("update RecBalanceNotice set rb_cmcurrency=rb_currency where rb_id=" + id + " and nvl(rb_cmcurrency,' ')=' '");
	}

	@Override
	public void updateRecBalanceNoticeById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("RecBalanceNotice", "rb_statuscode", "rb_id=" + store.get("rb_id"));
		StateAssert.updateOnlyEntering(status);
		checkDate(store.get("rb_date").toString());
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改RecBalanceNotice
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "RecBalanceNotice", "rb_id"));
		// 修改RecBalanceNoticeDetail
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "RecBalanceNoticeDetail", "rbd_id"));
		getTotal(store.get("rb_id"), caller);
		// 记录操作
		baseDao.logger.update(caller, "rb_id", store.get("rb_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteRecBalanceNotice(int rb_id, String caller) {
		// 只能删除在录入的采购单!
		Object status = baseDao.getFieldDataByCondition("RecBalanceNotice", "rb_statuscode", "rb_id=" + rb_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, rb_id);
		// 删除RecBalanceNotice
		baseDao.deleteById("RecBalanceNotice", "rb_id", rb_id);
		// 删除RecBalanceNoticeDetail
		baseDao.deleteById("RecBalanceNoticedetail", "rbd_rbid", rb_id);
		// 记录操作
		baseDao.logger.delete(caller, "rb_id", rb_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, rb_id);
	}

	@Override
	public void auditRecBalanceNotice(int rb_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("RecBalanceNotice", "rb_statuscode", "rb_id=" + rb_id);
		StateAssert.auditOnlyCommited(status);
		String dets = null;
		if ("RecBalanceNotice!PR".equals(caller)) {
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(rbd_detno) from RecBalanceNoticeDetail left join RecBalanceNotice on rbd_rbid=rb_id where rb_id=? and nvl(rbd_sacode,' ')<>' ' and nvl(rb_cmcurrency,' ')<>nvl(rbd_currency,' ')",
							String.class, rb_id);
			if (dets != null) {
				BaseUtil.showError("明细行订单币别与当前单主表冲账币别不一致，不允许进行当前操作！行号：" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(rbd_detno) from RecBalanceNoticeDetail left join Sale on rbd_sacode=sa_code where rbd_rbid=? and nvl(rbd_sacode,' ')<>' ' and abs(nvl(rbd_amount,0))>abs(nvl(sa_total,0)-nvl(sa_prepayamount,0))",
							String.class, rb_id);
			if (dets != null) {
				BaseUtil.showError("明细行本次回款金额大于订单金额-已预收金额，不允许进行当前操作！行号：" + dets);
			}
		} else if ("RecBalanceNotice!YS".equals(caller)) {
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(rbd_detno) from RecBalanceNoticeDetail left join RecBalanceNotice on rbd_rbid=rb_id where rb_id=? and nvl(rb_cmcurrency,' ')<>nvl(rbd_currency,' ')",
							String.class, rb_id);
			if (dets != null) {
				BaseUtil.showError("明细行币别与当前单主表冲账币别不一致，不允许进行当前操作！行号：" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(rbd_detno) from RecBalanceNoticeDetail left join ARBill on rbd_abcode=ab_code where rbd_rbid=? and nvl(rbd_abcode,' ')<>' ' and abs(nvl(rbd_amount,0))>abs(nvl(ab_aramount,0)-nvl(ab_payamount,0))",
							String.class, rb_id);
			if (dets != null) {
				BaseUtil.showError("明细行本次回款金额大于发票金额-已收金额，不允许进行当前操作！行号：" + dets);
			}
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, rb_id);
		// 执行审核操作
		baseDao.audit("RecBalanceNotice", "rb_id=" + rb_id, "rb_status", "rb_statuscode", "rb_auditdate", "rb_auditer");
		// 记录操作
		baseDao.logger.audit(caller, "rb_id", rb_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, rb_id);
	}

	@Override
	public void resAuditRecBalanceNotice(int rb_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("RecBalanceNotice", "rb_statuscode", "rb_id=" + rb_id);
		StateAssert.resAuditOnlyAudit(status);
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(ar_code) from AccountRegister where ar_sourcetype='回款通知单' and ar_sourceid=?", String.class, rb_id);
		if (dets != null) {
			BaseUtil.showError("已转入银行登记，不允许反审核操作！银行登记:" + dets);
		}
		handlerService.beforeResAudit(caller, rb_id);
		// 执行反审核操作
		baseDao.resAudit("RecBalanceNotice", "rb_id=" + rb_id, "rb_status", "rb_statuscode", "rb_auditdate", "rb_auditer");
		// 记录操作
		baseDao.logger.resAudit(caller, "rb_id", rb_id);
		handlerService.afterResAudit(caller, rb_id);
	}

	@Override
	public void submitRecBalanceNotice(int rb_id, String caller) {
		getTotal(rb_id, caller);
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("RecBalanceNotice", "rb_statuscode", "rb_id=" + rb_id);
		StateAssert.submitOnlyEntering(status);
		String dets = null;
		if ("RecBalanceNotice!PR".equals(caller)) {
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(rbd_detno) from RecBalanceNoticeDetail left join RecBalanceNotice on rbd_rbid=rb_id where rb_id=? and nvl(rbd_sacode,' ')<>' ' and nvl(rb_cmcurrency,' ')<>nvl(rbd_currency,' ')",
							String.class, rb_id);
			if (dets != null) {
				BaseUtil.showError("明细行订单币别与当前单主表冲账币别不一致，不允许进行当前操作！行号：" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(rbd_detno) from RecBalanceNoticeDetail left join Sale on rbd_sacode=sa_code where rbd_rbid=? and nvl(rbd_sacode,' ')<>' ' and abs(nvl(rbd_amount,0))>abs(nvl(sa_total,0)-nvl(sa_prepayamount,0))",
							String.class, rb_id);
			if (dets != null) {
				BaseUtil.showError("明细行本次回款金额大于订单金额-已预收金额，不允许进行当前操作！行号：" + dets);
			}
		} else if ("RecBalanceNotice!YS".equals(caller)) {
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(rbd_detno) from RecBalanceNoticeDetail left join RecBalanceNotice on rbd_rbid=rb_id where rb_id=? and nvl(rb_cmcurrency,' ')<>nvl(rbd_currency,' ')",
							String.class, rb_id);
			if (dets != null) {
				BaseUtil.showError("明细行币别与当前单主表冲账币别不一致，不允许进行当前操作！行号：" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(rbd_detno) from RecBalanceNoticeDetail left join ARBill on rbd_abcode=ab_code where rbd_rbid=? and nvl(rbd_abcode,' ')<>' ' and abs(nvl(rbd_amount,0))>abs(nvl(ab_aramount,0)-nvl(ab_payamount,0))",
							String.class, rb_id);
			if (dets != null) {
				BaseUtil.showError("明细行本次回款金额大于发票金额-已收金额，不允许进行当前操作！行号：" + dets);
			}
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, rb_id);
		// 执行提交操作
		baseDao.submit("RecBalanceNotice", "rb_id=" + rb_id, "rb_status", "rb_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "rb_id", rb_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, rb_id);
	}

	@Override
	public void resSubmitRecBalanceNotice(int rb_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("RecBalanceNotice", "rb_statuscode", "rb_id=" + rb_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, rb_id);
		// 执行反提交操作
		baseDao.resOperate("RecBalanceNotice", "rb_id=" + rb_id, "rb_status", "rb_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "rb_id", rb_id);
		handlerService.afterResSubmit(caller, rb_id);
	}

	@Override
	public String[] printRecBalanceNotice(int rb_id, String reportName, String condition, String caller) {
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, rb_id);
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 记录操作
		baseDao.logger.print(caller, "rb_id", rb_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, rb_id);
		return keys;
	}

	@Override
	public String turnAccountRegister(int rb_id, String catecode, String caller) {
		int arid = 0;
		String log = null;
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(rbd_detno) from RECBALANCENOTICEDetail,Sale where rbd_sacode=sa_code and nvl(rbd_sacode,' ')<>' ' and rbd_rbid=? and sa_statuscode<>'AUDITED'",
						String.class, rb_id);
		if (dets != null) {
			BaseUtil.showError("销售订单未审核！行：" + dets);
		}
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(ar_code) from AccountRegister where ar_sourcetype='回款通知单' and ar_sourceid=?", String.class, rb_id);
		if (dets != null) {
			BaseUtil.showError("已转入银行登记，不允许重复转！银行登记:" + dets);
		}
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(bar_code) from BillAR where bar_sourcetype='回款通知单' and bar_sourceid=?", String.class, rb_id);
		if (dets != null) {
			BaseUtil.showError("已转入应收票据，不允许转银行登记！应收票据:" + dets);
		}
		if (StringUtil.hasText(catecode)) {
			String error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code not in (select ca_code from Category where (nvl(ca_iscash,0)=-1 OR nvl(ca_isbank,0)=-1) and nvl(ca_isleaf,0)<>0 and nvl(ca_statuscode,' ')='AUDITED')",
							String.class, catecode);
			if (error != null) {
				BaseUtil.showError("填写科目编号不存在，或者状态不等于已审核，或者不是末级科目，或者不是银行现金科目，不允许转银行登记！");
			}
			baseDao.execute("update RecBalanceNotice set rb_catecode=? where rb_id=?", catecode, rb_id);
			baseDao.execute(
					"update RecBalanceNotice set (rb_catename,rb_cateid)=(select ca_description,ca_id from category where ca_code=rb_catecode) where rb_id=?",
					rb_id);
		}
		Key key = transferRepository.transfer(caller, rb_id);
		arid = key.getId();
		if (arid > 0) {
			// 转入明细
			transferRepository.transferDetail(caller, rb_id, key);
			baseDao.execute("update accountregister set ar_accountcurrency=(select ca_currency from category where ar_accountcode=ca_code) where ar_id="
					+ arid);
			baseDao.execute("update accountregister set ar_memo=(select rb_remark from RecBalanceNotice where rb_id=" + rb_id
					+ ") where ar_id=" + arid);
			baseDao.execute("update accountregister set ar_accountrate=(select nvl(cm_crrate,0) from currencysmonth where ar_accountcurrency=cm_crname and to_char(ar_date,'yyyymm')=cm_yearmonth) where ar_id="
					+ arid);
			baseDao.execute("update AccountRegister set ar_araprate=round(nvl(ar_aramount,0)/(nvl(ar_payment,0)+nvl(ar_deposit,0)),15) where (nvl(ar_payment,0)+nvl(ar_deposit,0))<>0 and ar_id="
					+ arid);
			accountRegisterBankService.updateErrorString(arid);
			baseDao.execute("update RecBalanceNotice set rb_yamount=rb_actamount where rb_id=" + rb_id);
			handlerService.handler(caller, "turnAccount", "after", new Object[] { arid });
			log = "转入成功<hr> 银行登记:" + "<a href=\"javascript:openUrl('jsps/fa/gs/accountRegister.jsp?formCondition=ar_idIS" + arid
					+ "&gridCondition=ard_aridIS" + arid + "&whoami=AccountRegister!Bank')\">" + key.getCode() + "</a>&nbsp;";
			baseDao.logger.others("转银行登记", "转入成功", caller, "rb_id", rb_id);
		}
		return log;
	}

	@Override
	public String turnBillAR(int rb_id, String catecode, String caller) {
		int barid = 0;
		String log = null;
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(bar_code) from BillAR where bar_sourcetype='回款通知单' and bar_sourceid=?", String.class, rb_id);
		if (dets != null) {
			BaseUtil.showError("已转入应收票据，不允许重复转！应收票据:" + dets);
		}
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(ar_code) from AccountRegister where ar_sourcetype='回款通知单' and ar_sourceid=?", String.class, rb_id);
		if (dets != null) {
			BaseUtil.showError("已转入银行登记，不允许转应收票据！银行登记:" + dets);
		}
		if (StringUtil.hasText(catecode)) {
			String error = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
					String.class, catecode);
			if (error != null) {
				BaseUtil.showError("填写科目编号不存在，或者状态不等于已审核，或者不是末级科目，不允许转应收票据！");
			}
			error = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(ca_code) from Category where ca_code=? and (nvl(ca_iscash,0)=-1 OR nvl(ca_isbank,0)=-1)",
					String.class, catecode);
			if (error != null) {
				BaseUtil.showError("填写科目是银行现金科目，不允许转应收票据！");
			}
			baseDao.execute("update RecBalanceNotice set rb_catecode=? where rb_id=?", catecode, rb_id);
			baseDao.execute(
					"update RecBalanceNotice set (rb_catename,rb_cateid)=(select ca_description,ca_id from category where ca_code=rb_catecode) where rb_id=?",
					rb_id);
		}
		Key key = transferRepository.transfer(caller + "!BillAR", rb_id);
		barid = key.getId();
		if (barid > 0) {
			baseDao.execute("update BillAR set bar_rate=(select nvl(cm_crrate,0) from currencysmonth where bar_currency=cm_crname and to_char(bar_date,'yyyymm')=cm_yearmonth) where bar_id="
					+ barid);
			baseDao.execute("update BillAR set bar_cmrate=round(nvl(bar_topaybalance,0)/nvl(bar_doublebalance,0),15) where nvl(bar_topaybalance,0)<>0 and bar_id="
					+ barid);
			baseDao.execute("update RecBalanceNotice set rb_yamount=rb_actamount where rb_id=" + rb_id);
			handlerService.handler(caller, "turnBillAR", "after", new Object[] { barid });
			log = "转入成功<hr> 应收票据:" + "<a href=\"javascript:openUrl('jsps/fa/gs/billAR.jsp?formCondition=bar_idIS" + barid + "')\">"
					+ key.getCode() + "</a>&nbsp;";
			baseDao.logger.others("转应收票据", "转入成功", caller, "rb_id", rb_id);
		}
		return log;
	}

	@Override
	public void catchAB(String caller, String formStore, String startdate, String enddate, String bicode) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int rb_id = Integer.parseInt(store.get("rb_id").toString());
		Object status = baseDao.getFieldDataByCondition("RecBalanceNotice", "rb_statuscode", "rb_id=" + store.get("rb_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		startdate = startdate == null ? "1970-01-01" : startdate;
		enddate = enddate == null ? DateUtil.format(DateUtil.overDate(null, 100), Constant.YMD) : enddate;
		String res = "";
		if (bicode == null || "".equals(bicode.trim())) {
			res = baseDao.callProcedure("Ct_CatchAbToRbNotice", new Object[] { rb_id, startdate, enddate });
		} else {
			for (String code : bicode.toString().trim().split("#")) {
				int count = baseDao.getCountByCondition("BillOutDetail", "ard_code='" + code + "'");
				if (count > 0) {
					String rs = baseDao.callProcedure("CT_CATCHABTORBNOTICE_BR", new Object[] { rb_id, startdate, enddate, code });
					if (!rs.trim().equals("ok")) {
						BaseUtil.showError(rs);
					}
				} else {
					BaseUtil.showError("票据[" + code + "]没有发票明细！");
				}
			}
			res = "ok";
		}
		getTotal(rb_id, caller);
		if (res.trim().equals("ok")) {
			baseDao.logger.others("抓取发票明细", "抓取成功", caller, "rb_id", rb_id);
		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public void cleanAB(String caller, String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int rb_id = Integer.parseInt(store.get("rb_id").toString());
		Object status = baseDao.getFieldDataByCondition("RecBalanceNotice", "rb_statuscode", "rb_id=" + rb_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		baseDao.execute("update RecBalanceNotice set rb_cmamount =0 where rb_id=" + rb_id);
		baseDao.deleteByCondition("RecBalanceNoticeDetail", "rbd_rbid=" + rb_id);
		baseDao.logger.others("清除发票明细", "清除成功", caller, "rb_id", rb_id);
	}
}
