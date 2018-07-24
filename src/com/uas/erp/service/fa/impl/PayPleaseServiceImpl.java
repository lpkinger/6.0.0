package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.PayPleaseDao;
import com.uas.erp.service.fa.AccountRegisterBankService;
import com.uas.erp.service.fa.PayPleaseService;

@Service
public class PayPleaseServiceImpl implements PayPleaseService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private PayPleaseDao payPleaseDao;
	@Autowired
	private AccountRegisterBankService accountRegisterBankService;

	@Override
	public void savePayPlease(String caller, String formStore, String gridStore1, String gridStore2) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore1 = BaseUtil.parseGridStoreToMaps(gridStore1);
		List<Map<Object, Object>> gstore2 = BaseUtil.parseGridStoreToMaps(gridStore2);
		handlerService.beforeSave(caller, new Object[] { store, gstore1, gstore2 });
		boolean bool = baseDao.checkIf("PayPlease", "pp_code='" + store.get("pp_code") + "'");
		if (bool) {
			BaseUtil.showError("单据编号已存在，请修改编号！");
		}
		// 保存PayPlease
		String formSql = SqlUtil.getInsertSqlByMap(store, "PayPlease");
		baseDao.execute(formSql);
		// 保存PayPleaseDetail
		baseDao.execute(SqlUtil.getInsertSqlbyList(gstore1, "PayPleaseDetail", "ppd_id"));
		// 保存PayPleaseDetailDet
		baseDao.execute(SqlUtil.getInsertSqlbyList(gstore2, "PayPleaseDetailDet", "ppdd_id"));
		update(caller, store.get("pp_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore1, gstore2 });
		// 记录操作
		baseDao.logger.save(caller, "pp_id", store.get("pp_id"));
	}

	@Override
	public void updatePayPleaseById(String caller, String formStore, String gridStore1, String gridStore2) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore1 = BaseUtil.parseGridStoreToMaps(gridStore1);
		List<Map<Object, Object>> gstore2 = BaseUtil.parseGridStoreToMaps(gridStore2);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore1, gstore2 });
		boolean bool = baseDao.checkIf("PayPlease", "pp_code='" + store.get("pp_code") + "' and pp_id<>" + store.get("pp_id"));
		if (bool) {
			BaseUtil.showError("单据编号已存在，请修改编号！");
		}
		// 修改PayPlease
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "PayPlease", "pp_id"));
		// 修改PayPleaseDetail
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore1, "PayPleaseDetail", "ppd_id"));
		// 修改PayPleaseDetailDet
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore2, "PayPleaseDetailDet", "ppdd_id"));
		update(caller, store.get("pp_id"));
		Object code = baseDao.getFieldDataByCondition("PayPlease", "pp_code", "pp_id='" + store.get("pp_id") + "'");
		String res = baseDao.callProcedure("SP_APLOCKAMOUNT", new Object[] { code });
		if (!res.trim().equals("OK") && res != null) {
			BaseUtil.showError(res);
		}
		// 执行保存后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore1, gstore2 });
		// 记录操作
		baseDao.logger.update(caller, "pp_id", store.get("pp_id"));
	}

	private void update(String caller, Object pp_id) {
		baseDao.execute("update payplease set PP_PRINTSTATUS='未打印' where pp_id=" + pp_id + " and PP_PRINTSTATUSCODE='UNPRINT'");
		baseDao.execute("update paypleasedetaildet set ppdd_ppdid=(select ppd_id from paypleasedetail where ppdd_ppid=ppd_ppid) where nvl(ppdd_ppdid,0)=0 and ppdd_ppid="
				+ pp_id);
		baseDao.execute("update PayPleaseDetail set ppd_applyamount=round((select sum(ppdd_thisapplyamount) from paypleasedetaildet where ppdd_ppdid=ppd_id),2) where ppd_ppid="
				+ pp_id + " and exists (select 1 from paypleasedetaildet where ppdd_ppdid=ppd_id)");
		baseDao.execute("update PayPleaseDetail set ppd_auditamount=ppd_applyamount where ppd_ppid=" + pp_id);
		baseDao.execute("update PayPlease set pp_total=round((select sum(ppd_applyamount) from paypleasedetail where ppd_ppid=pp_id),2) where pp_id="
				+ pp_id);
		baseDao.execute("update paypleasedetail set ppd_vendid=(select ve_id from vendor where ve_code=ppd_vendcode) where nvl(ppd_vendid,0)=0 and ppd_ppid="
				+ pp_id);
		// 将明细行的供应商名更新到主记录
		baseDao.execute("update PayPlease set pp_vendor=(select wmsys.wm_concat(ppd_vendname) from paypleasedetail where ppd_ppid=pp_id) where pp_id="
				+ pp_id);
	}

	@Override
	public void deletePayPlease(String caller, int pp_id) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, pp_id);
		Object accode = baseDao.getFieldDataByCondition("PayPlease", "pp_sourcecode", " pp_sourcetype='应付对账单' and pp_id=" + pp_id);
		Object bicode = baseDao.getFieldDataByCondition("PayPlease", "pp_sourcecode", " pp_sourcetype='应付开票记录' and pp_id=" + pp_id);
		baseDao.execute("update APBill set ab_lockamount=nvl(ab_lockamount,0)-nvl((select sum(nvl(ppdd_thisapplyamount,0)) from PayPleaseDetailDet where ab_code=ppdd_billcode and ppdd_ppdid in (select ppd_id from paypleasedetail where ppd_ppid = "
				+ pp_id
				+ ")),0) where ab_code in (select distinct ppdd_billcode from PayPleaseDetailDet where ppdd_ppdid in (select ppd_id from paypleasedetail where ppd_ppid = "
				+ pp_id + ") and nvl(ppdd_billcode,' ')<>' ')");
		// 删除PayPleaseDetailDet
		baseDao.deleteByCondition("PayPleaseDetailDet", "ppdd_id in (select ppdd_id from paypleasedetaildet where"
				+ " ppdd_ppdid in(select ppd_id from paypleasedetail where ppd_ppid = '" + pp_id + "'))");
		// 删除PayPlease
		baseDao.deleteById("PayPlease", "pp_id", pp_id);
		// 删除PayPleaseDetail
		baseDao.deleteById("PayPleaseDetail", "ppd_ppid", pp_id);
		if (accode != null) {
			baseDao.execute("update APCheck set AC_TURNSTATUS=null where ac_code='" + accode + "'");
		}
		if (bicode != null) {
			baseDao.execute("update Billoutap set BI_TURNSTATUS=null where bi_code='" + bicode + "'");
		}
		// 记录操作
		baseDao.logger.delete(caller, "pp_id", pp_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, pp_id);
	}

	@Override
	public String[] printPayPlease(int pp_id, String reportName, String condition, String caller) {
		update(caller, pp_id);
		Object status = baseDao.getFieldDataByCondition("PayPlease", "pp_statuscode", "pp_id=" + pp_id);
		// 判断已审核才允许打印
		if (baseDao.isDBSetting(caller, "printNeedAudit")) {
			StateAssert.printOnlyAudited(status);
		}
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, pp_id);
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 修改打印状态
		baseDao.print("PayPlease", "pp_id=" + pp_id, "pp_printstatus", "pp_printstatuscode");
		baseDao.logger.print(caller, "pp_id", pp_id);
		handlerService.afterPrint(caller, pp_id);
		return keys;
	}

	@Override
	public void auditPayPlease(int pp_id, String caller) {
		baseDao.execute("update paypleasedetail set ppd_vendid=(select ve_id from vendor where ve_code=ppd_vendcode) where nvl(ppd_vendid,0)=0 and ppd_ppid="
				+ pp_id);
		if ("PayPlease".equals(caller) || "PayPlease!YF".equals(caller)) {
			checkDet(pp_id, caller);
		}
		// 执行过账前的其它逻辑
		handlerService.beforeAudit(caller, pp_id);
		// 执行审核操作
		baseDao.audit("PayPlease", "pp_id=" + pp_id, "pp_status", "pp_statuscode", "pp_auditdate", "pp_auditer");
		// 记录操作
		baseDao.logger.audit(caller, "pp_id", pp_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, pp_id);
	}

	@Override
	public void resAuditPayPlease(int pp_id, String caller) {
		int count = baseDao
				.getCount("select count(*) from PayPleaseDetail where (ppd_statuscode in ('TURNPP','TURNPB','PARTBR','TURNBR','TURNBA') or nvl(ppd_account,0) > 0) and ppd_ppid="
						+ pp_id);
		if (count > 0) {
			BaseUtil.showError("已经转入其它单据，不允许反审核!");
		}
		handlerService.beforeResAudit(caller, pp_id);
		// 执行反审核操作
		baseDao.resAudit("PayPlease", "pp_id=" + pp_id, "pp_status", "pp_statuscode", "pp_auditdate", "pp_auditer");
		// 记录操作
		baseDao.logger.resAudit(caller, "pp_id", pp_id);
		handlerService.afterResAudit(caller, pp_id);
		// 重新计算明细行发票的锁定金额
		SqlRowList billcode = baseDao.queryForRowSet(
				"select ppdd_billcode from paypleasedetaildet where ppdd_ppid=? and nvl(ppdd_billcode,' ')<>' '", pp_id);
		while (billcode.next()) {
			baseDao.procedure("SP_APLOCKAMOUNT2", new Object[] { billcode.getObject("pbd_ordercode") });
		}
	}

	private void checkDet(Integer pp_id, String caller) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pp_code) from payplease,paypleasedetail where pp_id=ppd_ppid and pp_id=? and nvl(ppd_paymethod,' ')<>' ' and (ppd_paymethodcode,ppd_paymethod) not in (select pa_code,pa_name from Payments where pa_class='付款方式')",
						String.class, pp_id);
		if (dets != null) {
			BaseUtil.showError("明细中付款方式编号+付款方式名称在付款方式资料中不存在！");
		}
		if ("PayPlease".equals(caller)) {
			int count = baseDao.getCount("select count(*) from PayPleaseDetailDet,PayPleaseDetail where ppd_id=ppdd_ppdid and ppd_ppid="
					+ pp_id + " and nvl(ppdd_billcode,' ')<>' ' and nvl(ppd_applyamount,0)<>0");
			if (count <= 0) {
				BaseUtil.showError("从表二没有填写发票，不允许进行当前操作！");
			}
			String errBills = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ppdd_detno) from PayPleaseDetailDet where ppdd_ppdid in (select ppd_id from PayPleaseDetail where ppd_ppid=?) and nvl(ppdd_catecode,' ')=' ' and nvl(ppdd_billcode,' ') not in (select ab_code from apbill)",
							String.class, pp_id);
			if (errBills != null) {
				BaseUtil.showError("发票号不存在, 明细:" + errBills);
			}
			String error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"Select Wmsys.Wm_Concat(ppdd_billcode) From (select ppdd_billcode from PayPleaseDetailDet where ppdd_ppdid in (select ppd_id from paypleasedetail where ppd_ppid=?) and nvl(ppdd_billcode,' ')<>' ' group by ppdd_billcode having count(ppdd_billcode)>1)",
							String.class, pp_id);
			if (error != null) {
				BaseUtil.showError("发票重复:" + error);
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ppdd_billcode) from PayPleaseDetailDet left join PayPleaseDetail on ppdd_ppdid=ppd_id left join PayPlease on ppd_ppid=pp_id left join APBill on ppdd_billcode=ab_code where pp_id=? and nvl(ppdd_billcode,' ')<>' ' and to_char(ab_date,'yyyymmdd') > to_char(pp_date,'yyyymmdd')",
							String.class, pp_id);
			if (error != null) {
				BaseUtil.showError("发票日期大于单据日期，发票号：" + error);
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(PPDD_DETNO) from PayPleaseDetailDet left join APBill on ppdd_billcode=ab_code where ppdd_ppid=? and nvl(ppdd_billcode,' ')<>' ' and nvl(ppdd_currency,' ')<>nvl(ab_currency,' ')",
							String.class, pp_id);
			if (error != null) {
				BaseUtil.showError("币别与明细发票币别不一致！行号：" + error);
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(PPDD_DETNO) from PayPleaseDetailDet left join PayPleaseDetail on ppdd_ppdid=ppd_id left join PayPlease on ppd_ppid=pp_id where pp_id=? and nvl(PPDD_BILLCODE,' ')<>' ' and abs(nvl(ppdd_thisapplyamount,0))>abs(nvl(ppdd_billamount,0))",
							String.class, pp_id);
			if (error != null) {
				BaseUtil.showError("本次申请金额不能超过发票金额！行号：" + error);
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(PPDD_DETNO) from PayPleaseDetailDet left join apbill on ppdd_billcode=ab_code where ppdd_ppid=? and nvl(PPDD_BILLCODE,' ')<>' ' and abs(nvl(ppdd_thisapplyamount,0))>abs(nvl(ab_apamount,0)-nvl(ab_payamount,0))",
							String.class, pp_id);
			if (error != null) {
				BaseUtil.showError("本次申请金额不能超过发票金额-已付金额！行号：" + error);
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(PPDD_DETNO) from PayPleaseDetailDet left join PayPleaseDetail on ppdd_ppdid=ppd_id left join PayPlease on ppd_ppid=pp_id where pp_id=? and nvl(PPDD_BILLCODE,' ')<>' ' and abs(nvl(ppdd_billamount,0))<=abs(nvl(ppdd_account,0))",
							String.class, pp_id);
			if (error != null) {
				BaseUtil.showError("发票已付款！行号：" + error);
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ppdd_billcode) from PayPleaseDetailDet left join PayPleaseDetail on ppdd_ppdid=ppd_id left join PayPlease on ppd_ppid=pp_id left join APBill on ppdd_billcode=ab_code where pp_id=? and nvl(ppdd_billcode,' ')<>' ' and nvl(ppd_vendcode,' ') <> nvl(ab_vendcode,' ')",
							String.class, pp_id);
			if (error != null) {
				BaseUtil.showError("从表二发票供应商与从表一供应商不一致，发票号：" + error);
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ppdd_detno) from PayPleaseDetailDet where ppdd_ppdid in (select ppd_id from PayPleaseDetail where ppd_ppid=?) and nvl(ppdd_billcode,' ')=' ' and nvl(ppdd_catecode,' ') not in (select ca_code from category)",
							String.class, pp_id);
			if (error != null) {
				BaseUtil.showError("科目号不存在, 明细:" + error);
			}
		} else if ("PayPlease!YF".equals(caller)) {
			String error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ppdd_pucode) from PayPleaseDetailDet left join PURCHASEWITHOA_VIEW on ppdd_pucode=pu_code and pu_type=ppdd_type where ppdd_ppid=? and nvl(ppdd_pucode,' ')<>' ' and nvl(pu_statuscode,' ')<>'AUDITED'",
							String.class, pp_id);
			if (error != null) {
				BaseUtil.showError("明细采购单" + error + "状态不等于已审核！");
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ppdd_makecode) from PayPleaseDetailDet left join make on ppdd_makecode=ma_code and ma_tasktype='OS' where ppdd_ppid=? and nvl(ppdd_makecode,' ')<>' ' and (nvl(ma_statuscode,' ')<>'AUDITED' or nvl(ma_checkstatuscode,' ')<>'APPROVE')",
							String.class, pp_id);
			if (error != null) {
				BaseUtil.showError("明细委外单" + error + "状态不等于已审核或者已批准！");
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(PPDD_DETNO) from PayPleaseDetailDet left join PURCHASEWITHOA_VIEW on ppdd_pucode=pu_code and pu_type=ppdd_type where ppdd_ppid=? and nvl(ppdd_pucode,' ')<>' ' and nvl(ppdd_currency,' ')<>nvl(pu_currency,' ')",
							String.class, pp_id);
			if (error != null) {
				BaseUtil.showError("币别与明细采购单币别不一致！行号：" + error);
			}
			// 采购明细ID不为空的时候，判断本次付款金额不能超采购金额-预付金额
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(PPDD_DETNO) from PayPleaseDetailDet,purchasedetail where ppdd_pdid=pd_id and ppdd_ppid=? and nvl(ppdd_pdid,0)<>0 and abs(ROUND(nvl(ppdd_thisapplyamount,0),2))>abs(ROUND(nvl(pd_total,0)-nvl(pd_preamount,0),2))",
							String.class, pp_id);
			if (error != null) {
				BaseUtil.showError("本次付款金额不能超采购金额-预付金额！行号：" + error);
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat('采购单号:'||ppdd_pucode||'采购金额:'||nvl(ppdd_billamount,0)||'已提交申请金额:'||nvl(otheramount,0)||'本次申请:'||nvl(ppdd_thisapplyamount,0)) from (select ppdd_pucode,ppdd_thisapplyamount ppdd_thisapplyamount,ppdd_billamount ppdd_billamount,nvl((select sum(ppdd_thisapplyamount) from PayPleaseDetailDet left join PayPleaseDetail on ppdd_ppdid=ppd_id left join PayPlease on ppd_ppid=pp_id where ppdd_pucode = A.ppdd_pucode and pp_statuscode not in ('ENTERING','FINISH') AND PP_ID <>B.pp_id),0)-nvl((select sum(c.ppd_nowbalance) from PAYPLEASEDETAILDET LEFT JOIN PrePayDetail c on PPDD_PUCODE = c.ppd_ordercode and c.ppd_ordertype=ppdd_type left join PrePay d on d.pp_id=c.ppd_ppid where d.pp_type='预付退款单' and d.pp_statuscode='POSTED' and ppdd_ppid=b.pp_id),0) otheramount from PayPleaseDetailDet A left join PayPleaseDetail on ppdd_ppdid=ppd_id left join PayPlease B on ppd_ppid=pp_id where PP_ID =? and nvl(ppdd_pucode,' ')<>' ') where nvl(ppdd_thisapplyamount,0)+nvl(otheramount,0)>nvl(ppdd_billamount,0)",
							String.class, pp_id);
			if (error != null) {
				BaseUtil.showError("申请总额超过采购金额!" + error);
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(PPDD_DETNO) from PayPleaseDetailDet left join PayPleaseDetail on ppdd_ppdid=ppd_id left join PayPlease on ppd_ppid=pp_id where pp_id=? and nvl(ppdd_pucode,' ')<>' ' and abs(ROUND(nvl(ppdd_thisapplyamount,0),2))>abs(ROUND(nvl(ppdd_billamount,0)-nvl(ppdd_account,0),2))",
							String.class, pp_id);
			if (error != null) {
				BaseUtil.showError("本次申请金额不能超过采购金额-已付金额！行号：" + error);
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(PPDD_DETNO) from PayPleaseDetailDet left join make on ppdd_makecode=ma_code where ppdd_ppid=? and nvl(ppdd_makecode,' ')<>' ' and nvl(ppdd_currency,' ')<>nvl(ma_currency,' ')",
							String.class, pp_id);
			if (error != null) {
				BaseUtil.showError("币别与明细采购单币别不一致！行号：" + error);
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(PPDD_DETNO) from PayPleaseDetailDet left join PayPleaseDetail on ppdd_ppdid=ppd_id left join PayPlease on ppd_ppid=pp_id where pp_id=? and nvl(ppdd_makecode,' ')<>' ' and abs(ROUND(nvl(ppdd_thisapplyamount,0),2))>abs(ROUND(nvl(ppdd_billamount,0),2))",
							String.class, pp_id);
			if (error != null) {
				BaseUtil.showError("本次申请金额不能超过委外金额！行号：" + error);
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(PPDD_DETNO) from PayPleaseDetailDet left join PayPleaseDetail on ppdd_ppdid=ppd_id left join PayPlease on ppd_ppid=pp_id left join make on ppdd_makecode=ma_code where pp_id=? and nvl(ppdd_makecode,' ')<>' ' and round(nvl(ppdd_thisapplyamount,0)+nvl(ma_prepayamount,0),2)>round(nvl(ma_total,0),2)",
							String.class, pp_id);
			if (error != null) {
				BaseUtil.showError("本次申请金额不能超过委外金额-已付金额！行号：" + error);
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(PPDD_DETNO) from PayPleaseDetailDet left join PayPleaseDetail on ppdd_ppdid=ppd_id left join PayPlease on ppd_ppid=pp_id where pp_id=? and nvl(ppdd_pucode,' ')<>' ' and abs(ROUND(nvl(ppdd_billamount,0),2))<=abs(ROUND(nvl(ppdd_account,0),2))",
							String.class, pp_id);
			if (error != null) {
				BaseUtil.showError("采购单已付款！行号：" + error);
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(PPDD_DETNO) from PayPleaseDetailDet left join PayPleaseDetail on ppdd_ppdid=ppd_id left join PayPlease on ppd_ppid=pp_id where pp_id=? and nvl(ppdd_makecode,' ')<>' ' and abs(ROUND(nvl(ppdd_billamount,0),0))<=abs(ROUND(nvl(ppdd_account,0),0))",
							String.class, pp_id);
			if (error != null) {
				BaseUtil.showError("委外单已付款！行号：" + error);
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ppdd_pucode) from (select PPDD_TYPE, ppdd_pucode from paypleasedetaildet,paypleasedetail where ppdd_ppdid=ppd_id and ppd_ppid=? group by PPDD_TYPE,ppdd_pucode,nvl(ppdd_pddetno,0) having count(*)>1)",
							String.class, pp_id);
			if (error != null) {
				BaseUtil.showError("采购单重复：" + error);
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ppdd_pucode) from (select PPDD_TYPE, ppdd_pucode from PayPleaseDetailDet left join PayPleaseDetail on ppdd_ppdid=ppd_id left join PayPlease on ppd_ppid=pp_id left join PURCHASEWITHOA_VIEW on ppdd_pucode=pu_code and PPDD_TYPE=pu_type where pp_id=? and nvl(ppdd_pucode,' ')<>' ' and to_char(pu_date,'yyyymmdd') > to_char(pp_date,'yyyymmdd'))",
							String.class, pp_id);
			if (error != null) {
				BaseUtil.showError("采购单日期大于单据日期：" + error);
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ppdd_pucode) from (select PPDD_TYPE, ppdd_pucode from PayPleaseDetailDet left join PayPleaseDetail on ppdd_ppdid=ppd_id left join PayPlease on ppd_ppid=pp_id left join PURCHASEWITHOA_VIEW on ppdd_pucode=pu_code and PPDD_TYPE=pu_type where pp_id=? and nvl(ppdd_pucode,' ')<>' ' and nvl(ppd_vendcode,' ') <> nvl(pu_receivecode,' '))",
							String.class, pp_id);
			if (error != null) {
				BaseUtil.showError("采购单应付供应商与从表一供应商不一致：" + error);
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ppdd_makecode) from PayPleaseDetailDet left join PayPleaseDetail on ppdd_ppdid=ppd_id left join PayPlease on ppd_ppid=pp_id left join make on ppdd_makecode=ma_code where pp_id=? and nvl(ppdd_makecode,' ')<>' ' and to_char(ma_date,'yyyymmdd') > to_char(pp_date,'yyyymmdd')",
							String.class, pp_id);
			if (error != null) {
				BaseUtil.showError("委外单日期大于单据日期，委外单号：" + error);
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ppdd_makecode) from PayPleaseDetailDet left join PayPleaseDetail on ppdd_ppdid=ppd_id left join PayPlease on ppd_ppid=pp_id left join make on ppdd_makecode=ma_code left join vendor on ma_vendcode=ve_code where pp_id=? and nvl(ppdd_makecode,' ')<>' ' and nvl(ppd_vendcode,' ') <> nvl(ma_apvendcode,ve_apvendcode)",
							String.class, pp_id);
			if (error != null) {
				BaseUtil.showError("从表二委外单应付供应商与从表一供应商不一致，委外单号：" + error);
			}
			// @add wuyx 商城预付款申请单提交限制：明细含商城类型采购单时必须全部都为商城类型采购单
			Integer b2cppdd_detno = baseDao
					.getJdbcTemplate()
					.queryForObject(
							" select max(ppdd_detno) from PayPleaseDetailDet where  ppdd_ppid = ? and  exists ( "
									+ " select * from PayPleaseDetailDet  left join  purchase on ppdd_pucode = pu_code "
									+ " where upper(nvl(pu_ordertype,' ')) = 'B2C' and ppdd_ppid = ? and upper(nvl(pu_ordertype,' ')) = upper(nvl(pu_ordertype,' ')))",
							Integer.class, pp_id, pp_id);
			if (b2cppdd_detno != null && b2cppdd_detno > 1) {
				BaseUtil.showError("明细含商城类型采购单时不能多单一起付款！");
			}

		}
		String error = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(PPDD_DETNO) from PayPleaseDetailDet left join PayPleaseDetail on ppdd_ppdid=ppd_id left join PayPlease on ppd_ppid=pp_id where pp_id=? and nvl(PPDD_CURRENCY,' ')<>nvl(PPD_CURRENCY,' ')",
						String.class, pp_id);
		if (error != null) {
			BaseUtil.showError("从表二币别与从表一币别不一致！行号：" + error);
		}

	}

	@Override
	public void submitPayPlease(int pp_id, String caller) {
		update(caller, pp_id);
		checkDet(pp_id, caller);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, pp_id);
		if (baseDao.isDBSetting(caller, "APARCheckAccount")) {
			baseDao.procedure("SP_PAYPLEASEBEGIN", new Object[] { pp_id });
		}
		// 执行提交操作
		baseDao.updateByCondition("PayPlease", "pp_statuscode='COMMITED',pp_status='" + BaseUtil.getLocalMessage("COMMITED") + "'",
				"pp_id=" + pp_id);
		if ("PayPlease".equals(caller)) {
			baseDao.execute("UPDATE paypleasedetail SET ppd_amount=(SELECT NVL(va_amount,0) FROM vendap WHERE va_vendcode=ppd_vendcode AND va_currency=ppd_currency) WHERE ppd_ppid="
					+ pp_id);
		}
		// 记录操作
		baseDao.logger.submit(caller, "pp_id", pp_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, pp_id);
	}

	@Override
	public void resSubmitPayPlease(int pp_id, String caller) {
		// 执行反提交操作
		handlerService.beforeResSubmit(caller, pp_id);
		baseDao.updateByCondition("PayPlease", "pp_statuscode='ENTERING',pp_status='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"pp_id=" + pp_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "pp_id", pp_id);
		handlerService.afterResSubmit(caller, pp_id);
		// 重新计算明细行发票的锁定金额
		SqlRowList billcode = baseDao.queryForRowSet(
				"select ppdd_billcode from paypleasedetaildet where ppdd_ppid=? and nvl(ppdd_billcode,' ')<>' '", pp_id);
		while (billcode.next()) {
			baseDao.procedure("SP_APLOCKAMOUNT2", new Object[] { billcode.getObject("pbd_ordercode") });
		}
	}

	@Override
	public void catchAP(String caller, String ppd_id, String ppd_ppid, String startdate, String enddate, String bicode) {
		boolean isAudit = baseDao.checkIf("PayPlease", "pp_id=" + ppd_ppid + " AND pp_statuscode not in ('ENTERING', 'COMMITED')");
		if (isAudit) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		String res = "";
		if (!StringUtil.hasText(bicode)) {
			res = baseDao.callProcedure("CT_CATCHABTOPPBB", new Object[] { ppd_id, startdate, enddate });
		} else {
			int count = baseDao.getCountByCondition("BillOutAPDetail", "ard_code='" + bicode + "'");
			if (count > 0) {
				res = baseDao.callProcedure("CT_CATCHABTOPPBB_BP", new Object[] { ppd_id, startdate, enddate, bicode });
			} else {
				BaseUtil.showError("票据[" + bicode + "]没有发票明细！");
			}
		}
		if ("ok".equals(res)) {
			baseDao.logger.others("获取发票(" + ppd_id + ")", "获取成功", caller, "pp_id", ppd_ppid);
		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public void cleanAP(String caller, String ppd_id, String ppd_ppid) {
		Object status = baseDao.getFieldDataByCondition("PayPlease", "pp_statuscode", "pp_id=" + ppd_ppid);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		// 重新计算明细行发票的锁定金额
		SqlRowList rs = baseDao.queryForRowSet(
				"select ppdd_billcode from PayPleaseDetailDet where ppdd_ppid=? and nvl(ppdd_billcode,' ')<>' '", ppd_ppid);
		baseDao.deleteByCondition("PayPleaseDetailDet", "ppdd_ppid=" + ppd_ppid);
		while (rs.next()) {
			baseDao.procedure("SP_APLOCKAMOUNT2", new Object[] { rs.getObject("ppdd_billcode") });
		}
		baseDao.logger.others("清除发票(" + ppd_id + ")", "清除成功", caller, "pp_id", ppd_ppid);
	}

	/**
	 * 转预付款单
	 */
	@Override
	public JSONObject turnPrePay(String caller, String formStore) {
		// 主表中的数据 界面上显示的主表的所有数据
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object pp_id = store.get("pp_id");
		String pp_code = (String) store.get("pp_code");
		String pp_type = (String) store.get("pp_type");
		String pp_paymentcode = (String) store.get("pp_paymentcode");
		String pp_payment = (String) store.get("pp_payment");
		Object pp_thispaydate = store.get("pp_thispaydate");
		Object pp_thispayamount = store.get("pp_thispayamount");
		if (pp_paymentcode != null && !"".equals(pp_paymentcode)) {
			String error = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
					String.class, pp_paymentcode);
			if (error != null) {
				BaseUtil.showError("填写科目编号不存在，或者状态不等于已审核，或者不是末级科目，不允许转付款！");
			}
			error = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(CA_ISCASHBANK,0)<>0", String.class,
					pp_paymentcode);
			if (error != null) {
				BaseUtil.showError("付款科目有误！");
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code IN (SELECT COLUMN_VALUE FROM TABLE(parseString(getconfig('CheckAccount!GS','bapCatecode'), chr(10))))",
							String.class, pp_paymentcode);
			if (error != null) {
				BaseUtil.showError("付款科目有误！");
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code IN (SELECT COLUMN_VALUE FROM TABLE(parseString(getconfig('CheckAccount!GS','barCatecode'), chr(10))))",
							String.class, pp_paymentcode);
			if (error != null) {
				BaseUtil.showError("付款科目有误！");
			}
			baseDao.execute("update payplease set pp_paymentcode=?,pp_payment=? where pp_code=?", pp_paymentcode, pp_payment, pp_code);
			if (pp_thispaydate != null) {
				baseDao.execute("update payplease set pp_thispaydate=to_date('" + pp_thispaydate + "','yyyy-mm-dd') where pp_code=?",
						pp_code);
			} else {
				baseDao.execute("update payplease set pp_thispaydate=sysdate where pp_code=?", pp_code);
			}
		}
		if (Double.parseDouble(pp_thispayamount.toString()) == 0) {
			BaseUtil.showError("已经全部转出!");
		}
		if ("PayPlease!YF".equals(caller) && StringUtil.hasText(pp_code)) {
			String error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ppdd_pucode) from PayPleaseDetailDet left join PURCHASEWITHOA_VIEW on ppdd_pucode=pu_code and pu_type=ppdd_type where ppdd_ppid=? and nvl(ppdd_pucode,' ')<>' ' and upper(nvl(pu_ordertype,' ')) = 'B2C'",
							String.class, pp_id);
			if (error != null) {
				BaseUtil.showError("含商城类型的采购只接受现金交易，请转银行登记。");
			}
		}
		JSONObject j = null;
		SqlRowList rs = baseDao.queryForRowSet("select ppd_id from paypleasedetail where ppd_ppid=?", new Object[] { pp_id });
		if (rs.next()) {
			j = payPleaseDao.turnPrePay(rs.getGeneralInt("ppd_id"), pp_code, pp_thispayamount);
			if (j != null) {
				baseDao.logger.turn("转" + pp_type + ":" + pp_thispayamount, caller, "pp_id", pp_id);
			}
		}
		if ("PayPlease!YF".equals(caller) && StringUtil.hasText(pp_code)) {
			payPleaseDao.updateDetailAmountYF(pp_code);
		}
		return j;
	}

	/**
	 * 转银行登记
	 */
	@Override
	public JSONObject turnBankRegister(String caller, String formStore) {
		// 主表中的数据 界面上显示的主表的所有数据
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object pp_id = store.get("pp_id");
		String pp_code = (String) store.get("pp_code");
		String pp_type = (String) store.get("pp_type");
		String pp_paymentcode = (String) store.get("pp_paymentcode");
		String pp_payment = (String) store.get("pp_payment");
		String pp_thispaydate = (String) store.get("pp_thispaydate");
		String pp_refno = (String) store.get("pp_refno");
		Object pp_thispayamount = store.get("pp_thispayamount");
		if (pp_paymentcode != null && !"".equals(pp_paymentcode)) {
			String error = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
					String.class, pp_paymentcode);
			if (error != null) {
				BaseUtil.showError("填写科目编号不存在，或者状态不等于已审核，或者不是末级科目，不允许转银行登记！");
			}
			error = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(CA_ISCASHBANK,0)=0", String.class,
					pp_paymentcode);
			if (error != null) {
				BaseUtil.showError("付款科目有误，请填写银行现金科目！");
			}
			baseDao.execute("update payplease set pp_paymentcode=?,pp_payment=?,pp_thispaydate=to_date('" + pp_thispaydate
					+ "','yyyy-mm-dd') where pp_code=?", pp_paymentcode, pp_payment, pp_code);
		}
		if (Double.parseDouble(pp_thispayamount.toString()) == 0) {
			BaseUtil.showError("已经全部转银行登记!");
		}
		// @add20180105 wuyx 商城类型采购单限制
		if ("PayPlease!YF".equals(caller) && StringUtil.hasText(pp_code)) {/*
																			 * String
																			 * error
																			 * =
																			 * baseDao
																			 * .
																			 * getJdbcTemplate
																			 * (
																			 * )
																			 * .
																			 * queryForObject
																			 * (
																			 * "select wmsys.wm_concat(ppdd_pucode) from PayPleaseDetailDet left join purchase on ppdd_pucode=pu_code and pu_type=ppdd_type left join on  where ppdd_ppid=? and nvl(ppdd_pucode,' ')<>' ' and upper(nvl(pu_ordertype,' ')) = 'B2C'"
																			 * ,
																			 * String
																			 * .
																			 * class
																			 * ,
																			 * pp_id
																			 * )
																			 * ;
																			 * if
																			 * (
																			 * error
																			 * !=
																			 * null
																			 * )
																			 * {
																			 * BaseUtil
																			 * .
																			 * showError
																			 * (
																			 * "含商城类型的采购只接受现金交易，请转银行登记。"
																			 * )
																			 * ;
																			 * }
																			 * error
																			 * =
																			 * baseDao
																			 * .
																			 * getJdbcTemplate
																			 * (
																			 * )
																			 * .
																			 * queryForObject
																			 * (
																			 * "select wmsys.wm_concat(ppdd_pucode) from PayPleaseDetailDet left join purchase on ppdd_pucode=pu_code and pu_type=ppdd_type where ppdd_type = '采购单' and ppdd_ppid=? and nvl(ppdd_pucode,' ')<>' ' and upper(nvl(pu_ordertype,' ')) = 'B2C' and pu_total <> ? "
																			 * ,
																			 * String
																			 * .
																			 * class
																			 * ,
																			 * pp_id
																			 * ,
																			 * Double
																			 * .
																			 * parseDouble
																			 * (
																			 * pp_thispayamount
																			 * .
																			 * toString
																			 * (
																			 * )
																			 * )
																			 * )
																			 * ;
																			 * if
																			 * (
																			 * error
																			 * !=
																			 * null
																			 * )
																			 * {
																			 * BaseUtil
																			 * .
																			 * showError
																			 * (
																			 * "含商城类型的采购必须一次性付清账款。"
																			 * )
																			 * ;
																			 * }
																			 */
		}
		JSONObject j = null;
		SqlRowList rs = baseDao.queryForRowSet("select ppd_id from paypleasedetail where ppd_ppid=?", new Object[] { pp_id });
		if (rs.next()) {
			int ppd_id = rs.getGeneralInt("ppd_id");
			j = payPleaseDao.turnBankRegister(ppd_id, pp_code, pp_type, pp_thispayamount, pp_thispaydate, pp_refno);
			if (j != null) {
				accountRegisterBankService.updateErrorString(j.getInt("ar_id"));
				baseDao.logger.turn("转银行登记" + pp_thispayamount, caller, "pp_id", pp_id);
			}
		}
		if ("PayPlease".equals(caller) && StringUtil.hasText(pp_code)) {
			payPleaseDao.updateDetailAmount(pp_code);
		}
		return j;
	}

	@Override
	public JSONObject turnBillAP(String caller, String formStore) {
		// 主表中的数据 界面上显示的主表的所有数据
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object pp_id = store.get("pp_id");
		String pp_code = (String) store.get("pp_code");
		String pp_type = (String) store.get("pp_type");
		String pp_paymentcode = (String) store.get("pp_paymentcode");
		String pp_payment = (String) store.get("pp_payment");
		String pp_thispaydate = (String) store.get("pp_thispaydate");
		String pp_refno = (String) store.get("pp_refno");
		Object pp_thispayamount = store.get("pp_thispayamount");
		// ppdd_pucode
		if (pp_paymentcode != null && !"".equals(pp_paymentcode)) {
			String error = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
					String.class, pp_paymentcode);
			if (error != null) {
				BaseUtil.showError("填写科目编号不存在，或者状态不等于已审核，或者不是末级科目，不允许转应付票据！");
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code NOT IN (SELECT COLUMN_VALUE FROM TABLE(parseString(getconfig('CheckAccount!GS','bapCatecode'), chr(10))))",
							String.class, pp_paymentcode);
			if (error != null) {
				BaseUtil.showError("付款科目有误，请填写应付票据科目！");
			}
			baseDao.execute("update payplease set pp_paymentcode=?,pp_payment=?,pp_thispaydate=to_date('" + pp_thispaydate
					+ "','yyyy-mm-dd') where pp_code=?", pp_paymentcode, pp_payment, pp_code);
		}
		if (Double.parseDouble(pp_thispayamount.toString()) == 0) {
			BaseUtil.showError("已经全部转出!");
		}
		// @add 20180105 wuyx
		if ("PayPlease".equals(caller)) {/*
										 * String error =
										 * baseDao.getJdbcTemplate()
										 * .queryForObject(
										 * "select wmsys.wm_concat(ppdd_pucode) from PayPleaseDetailDet left join PURCHASEWITHOA_VIEW on ppdd_pucode=pu_code and pu_type=ppdd_type where ppdd_ppid=? and nvl(ppdd_pucode,' ')<>' ' and upper(nvl(pu_ordertype,' ')) = 'B2C'"
										 * , String.class, pp_id); if (error !=
										 * null) { BaseUtil.showError(
										 * "含商城类型的采购只能进行现金交易，不可转应付票据，请转银行登记。");
										 * }
										 */
		}
		// 所选择的付款申请单从表中的信息 每一条数据只有ppd_id ppd_vendcode 这两个数据
		JSONObject j = null;
		SqlRowList rs = baseDao.queryForRowSet("select ppd_id from paypleasedetail where ppd_ppid=?", new Object[] { pp_id });
		if (rs.next()) {
			int ppd_id = rs.getGeneralInt("ppd_id");
			j = payPleaseDao.turnBillAP(ppd_id, pp_code, pp_type, pp_thispayamount, pp_thispaydate, pp_refno);
			if (j != null) {
				baseDao.logger.turn("转应付票据" + pp_thispayamount, caller, "pp_id", pp_id);
			}
		}
		return j;
	}

	@Override
	public JSONObject turnBillARChange(String caller, String formStore) {
		// 主表中的数据 界面上显示的主表的所有数据
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object pp_id = store.get("pp_id");
		String pp_code = (String) store.get("pp_code");
		String pp_type = (String) store.get("pp_type");
		String pp_paymentcode = (String) store.get("pp_paymentcode");
		String pp_payment = (String) store.get("pp_payment");
		String pp_thispaydate = (String) store.get("pp_thispaydate");
		String pp_refno = (String) store.get("pp_refno");
		Object pp_thispayamount = store.get("pp_thispayamount");
		if (pp_paymentcode != null && !"".equals(pp_paymentcode)) {
			String error = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
					String.class, pp_paymentcode);
			if (error != null) {
				BaseUtil.showError("填写科目编号不存在，或者状态不等于已审核，或者不是末级科目，不允许转应收票据异动！");
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code NOT IN (SELECT COLUMN_VALUE FROM TABLE(parseString(getconfig('CheckAccount!GS','barCatecode'), chr(10))))",
							String.class, pp_paymentcode);
			if (error != null) {
				BaseUtil.showError("付款科目有误，请填写应收票据科目！");
			}
			baseDao.execute("update payplease set pp_paymentcode=?,pp_payment=?,pp_thispaydate=to_date('" + pp_thispaydate
					+ "','yyyy-mm-dd') where pp_code=?", pp_paymentcode, pp_payment, pp_code);
		}
		if (Double.parseDouble(pp_thispayamount.toString()) == 0) {
			BaseUtil.showError("已经全部转出!");
		}
		// @add 20180105 wuyx
		if ("PayPlease!YF".equals(caller)) {
			// String error = baseDao.getJdbcTemplate()
			// .queryForObject(
			// "select wmsys.wm_concat(ppdd_pucode) from PayPleaseDetailDet left join PURCHASEWITHOA_VIEW on ppdd_pucode=pu_code and pu_type=ppdd_type where ppdd_ppid=? and nvl(ppdd_pucode,' ')<>' ' and upper(nvl(pu_ordertype,' '))='B2C'",
			// String.class, pp_id);
			// if (error != null) {
			// BaseUtil.showError("含商城类型的采购只能进行现金交易，不可转应收票据异动，请转银行登记。");
			// }
		}
		// 所选择的付款申请单从表中的信息 每一条数据只有ppd_id ppd_vendcode 这两个数据
		JSONObject j = null;
		SqlRowList rs = baseDao.queryForRowSet("select ppd_id from paypleasedetail where ppd_ppid=?", new Object[] { pp_id });
		if (rs.next()) {
			int ppd_id = rs.getGeneralInt("ppd_id");
			j = payPleaseDao.turnBillARChange(ppd_id, pp_code, pp_type, pp_thispayamount, pp_thispaydate, pp_refno);
			if (j != null) {
				baseDao.logger.turn("转应收票据背书转让" + pp_thispayamount, caller, "pp_id", pp_id);
			}
		}
		return j;
	}

	@Override
	public void endPayPlease(int pp_id, String caller) {
		// 执行结案操作
		baseDao.execute("update PayPlease set pp_status='已结案', pp_statuscode='FINISH' where pp_id=" + pp_id);
		// 记录操作
		baseDao.logger.end(caller, "pp_id", pp_id);
	}

	@Override
	public void resEndPayPlease(int pp_id, String caller) {
		// 执行反结案操作
		baseDao.execute("update PayPlease set pp_status='已审核', pp_statuscode='AUDITED' where pp_id=" + pp_id);
		// 记录操作
		baseDao.logger.resEnd(caller, "pp_id", pp_id);
	}

	@Override
	public void reLockAmount(int id, String abcode, Double amount) {
		SqlRowList rs = baseDao.queryForRowSet("select * from APBill where ab_code=?", abcode);
		if (rs.next()) {
			baseDao.execute("update apbill set ab_lockamount=nvl(ab_lockamount,0)-(" + amount + ") where ab_code='" + abcode + "'");
		}
	}

	@Override
	public JSONObject turnPayBalanceCYF(String caller, String formStore) {
		// 主表中的数据 界面上显示的主表的所有数据
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object pp_id = store.get("pp_id");
		String pp_code = (String) store.get("pp_code");
		String pp_type = (String) store.get("pp_type");
		String pp_paymentcode = (String) store.get("pp_paymentcode");
		String pp_payment = (String) store.get("pp_payment");
		String pp_thispaydate = (String) store.get("pp_thispaydate");
		String pp_refno = (String) store.get("pp_refno");
		Object pp_thispayamount = store.get("pp_thispayamount");
		if (pp_paymentcode != null && !"".equals(pp_paymentcode)) {
			String error = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
					String.class, pp_paymentcode);
			if (error != null) {
				BaseUtil.showError("填写科目编号不存在，或者状态不等于已审核，或者不是末级科目，不允许转冲应付款！");
			}
			error = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(ca_code) from Category where ca_code=? and (nvl(ca_iscash,0)=-1 OR nvl(ca_isbank,0)=-1)",
					String.class, pp_paymentcode);
			if (error != null) {
				BaseUtil.showError("银行现金科目不允许转冲应付款！");
			}
			baseDao.execute("update payplease set pp_paymentcode=?,pp_payment=?,pp_thispaydate=to_date('" + pp_thispaydate
					+ "','yyyy-mm-dd') where pp_code=?", pp_paymentcode, pp_payment, pp_code);
		}
		if (Double.parseDouble(pp_thispayamount.toString()) == 0) {
			BaseUtil.showError("已经全部转出!");
		}
		// 所选择的付款申请单从表中的信息 每一条数据只有ppd_id ppd_vendcode 这两个数据
		JSONObject j = null;
		SqlRowList rs = baseDao.queryForRowSet("select ppd_id from paypleasedetail where ppd_ppid=?", new Object[] { pp_id });
		if (rs.next()) {
			int ppd_id = rs.getGeneralInt("ppd_id");
			j = payPleaseDao.turnPayBalanceCYF(ppd_id, pp_code, pp_type, pp_thispayamount, pp_thispaydate, pp_refno);
			if (j != null) {
				baseDao.logger.turn("转冲应付款" + pp_thispayamount, caller, "pp_id", pp_id);
			}
		}
		return j;
	}

}
