package com.uas.erp.service.fa.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;








import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.BillOutService;
import com.uas.sso.common.encrypt.MD5;

@Service("billOutService")
public class BillOutServiceImpl implements BillOutService {
	
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveBillOut(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		String code = store.get("bi_code").toString();
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BillOut", "bi_code='" + code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存BillOut
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BillOut", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存BillOutDetail
		for (Map<Object, Object> m : grid) {
			m.put("ard_id", baseDao.getSeqId("BillOutDETAIL_SEQ"));
			m.put("ard_status", BaseUtil.getLocalMessage("ENTERING"));
			m.put("ard_statuscode", "ENTERING");
			m.put("ard_code", code);
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "BillOutDetail");
		baseDao.execute(gridSql);
		getTotal(store.get("bi_id"));
		// 记录操作
		baseDao.logger.save(caller, "bi_id", store.get("bi_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	private void getTotal(Object bi_id) {
		baseDao.execute("update BillOutDetail set ard_code=(select bi_code from BillOut where ard_biid=bi_id) where ard_biid=" + bi_id
				+ " and not exists (select 1 from BillOut where ard_code=bi_code)");
		baseDao.execute("update BillOutDetail set ard_nowbalance=round(nvl(ard_nowqty,0)*nvl(ard_nowprice,0),2) where ard_biid=" + bi_id);
		baseDao.execute("update BillOutDetail set ard_taxamount=round(ard_nowbalance*nvl(ard_taxrate,0)/(100+nvl(ard_taxrate,0)),2) where ard_biid="
				+ bi_id);
		baseDao.execute("update BillOut set bi_amount=round(nvl((select sum(round(ard_nowbalance,2)) from BillOutDetail where ard_biid=bi_id),0),2) where bi_id="
				+ bi_id);
		baseDao.execute("update BillOut set bi_taxamount=round(nvl((select sum(round(ard_taxamount,2)) from BillOutDetail where ard_biid=bi_id),2),2) + nvl(bi_taxdiffer,0) where bi_id="
				+ bi_id);
	}

	@Override
	public void deleteBillOut(int bi_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("BillOut", "bi_statuscode", "bi_id=" + bi_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, bi_id);
		String adidstr = "";
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select abd_id from BillOutDetail left join arbilldetail on abd_code=ard_ordercode and abd_detno=ard_orderdetno where ard_biid=?",
						bi_id);
		while (rs.next()) {
			adidstr += "," + rs.getString("abd_id");
		}
		// 删除BillOut
		baseDao.deleteById("BillOut", "bi_id", bi_id);
		// 删除BillOutDetail
		baseDao.deleteById("BillOutdetail", "ard_biid", bi_id);
		if (!adidstr.equals("")) {
			adidstr = adidstr.substring(1);
			baseDao.execute("update arbilldetail set abd_yqty=nvl((select sum(ard_nowqty) from billoutdetail where abd_code=ard_ordercode and abd_detno=ard_orderdetno and nvl(ard_adid,0)=0 group by ard_ordercode,ard_orderdetno ),0) where abd_id in ("
					+ adidstr + ")");
		}
		// 记录操作
		baseDao.logger.delete(caller, "bi_id", bi_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, bi_id);
	}

	@Override
	public void updateBillOutById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		String code = store.get("bi_code").toString();
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("BillOut", "bi_statuscode", "bi_id=" + store.get("bi_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改BillOut
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BillOut", "bi_id");
		baseDao.execute(formSql);
		// 修改BillOutDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "BillOutDetail", "ard_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("ard_id") == null || s.get("ard_id").equals("") || s.get("ard_id").equals("0")
					|| Integer.parseInt(s.get("ard_id").toString()) == 0) {// 新添加的数据，id不存在
				s.put("ard_status", BaseUtil.getLocalMessage("ENTERING"));
				s.put("ard_statuscode", "ENTERING");
				s.put("ard_code", code);
				s.put("ard_id", baseDao.getSeqId("BillOutDETAIL_SEQ"));
				String sql = SqlUtil.getInsertSqlByMap(s, "BillOutDetail");
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		getTotal(store.get("bi_id"));
		// 记录操作
		baseDao.logger.update(caller, "bi_id", store.get("bi_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public String[] printBillOut(int bi_id, String reportName, String condition, String caller) {
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, bi_id);
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "bi_id", bi_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, bi_id);
		return keys;
	}

	@Override
	public void auditBillOut(int bi_id, String caller) {
		getTotal(bi_id);
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("BillOut", "bi_statuscode", "bi_id=" + bi_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler("BillOut", "audit", "before", new Object[] { bi_id });
		// 执行审核操作
		baseDao.updateByCondition("BillOut", "bi_statuscode='AUDITED',bi_status='" + BaseUtil.getLocalMessage("AUDITED") + "',bi_auditer='"
				+ SystemSession.getUser().getEm_name() + "',bi_auditdate=sysdate", "bi_id=" + bi_id);
		baseDao.updateByCondition("BillOutDetail", "ard_statuscode='AUDITED',ard_status='" + BaseUtil.getLocalMessage("AUDITED") + "'",
				"ard_biid=" + bi_id);
		// 记录操作
		baseDao.logger.audit(caller, "bi_id", bi_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, bi_id);
	}

	@Override
	public void resAuditBillOut(int bi_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("BillOut", "bi_statuscode", "bi_id=" + bi_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, bi_id);
		// 执行反审核操作
		baseDao.updateByCondition("BillOut", "bi_statuscode='ENTERING',bi_status='" + BaseUtil.getLocalMessage("ENTERING")
				+ "',bi_auditer='',bi_auditdate=null", "bi_id=" + bi_id);
		baseDao.updateByCondition("BillOutDetail", "ard_statuscode='ENTERING',ard_status='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"ard_biid=" + bi_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "bi_id", bi_id);
		handlerService.afterResAudit(caller, bi_id);
	}

	@Override
	public void submitBillOut(int bi_id, String caller) {
		getTotal(bi_id);
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("BillOut", "bi_statuscode", "bi_id=" + bi_id);
		StateAssert.submitOnlyEntering(status);
		check(bi_id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, bi_id);
		// 执行提交操作
		baseDao.updateByCondition("BillOut", "bi_statuscode='COMMITED',bi_status='" + BaseUtil.getLocalMessage("COMMITED") + "'", "bi_id="
				+ bi_id);
		baseDao.updateByCondition("BillOutDetail", "ard_statuscode='COMMITED',ard_status='" + BaseUtil.getLocalMessage("COMMITED") + "'",
				"ard_biid=" + bi_id);
		// 记录操作
		baseDao.logger.submit(caller, "bi_id", bi_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, bi_id);
	}

	@Override
	public void resSubmitBillOut(int bi_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("BillOut", "bi_statuscode", "bi_id=" + bi_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, bi_id);
		// 执行反提交操作
		baseDao.updateByCondition("BillOut", "bi_statuscode='ENTERING',bi_status='" + BaseUtil.getLocalMessage("ENTERING") + "'", "bi_id="
				+ bi_id);
		baseDao.updateByCondition("BillOutDetail", "ard_statuscode='ENTERING',ard_status='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"ard_biid=" + bi_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "bi_id", bi_id);
		handlerService.afterResSubmit(caller, bi_id);
	}

	void check(int bi_id) {
		if (!baseDao.isDBSetting("BillOut", "allowUpdatePrice")) {
			baseDao.execute("update BILLOUTDETAIL set ARD_PRICE=round(nvl(ard_price,0),8),ARD_NOWPRICE=round(nvl(ard_nowprice,0),8) where ARD_BIID="
					+ bi_id);
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(ard_detno) from BillOutDetail where ard_biid=? and round(nvl(ard_price,0),8)<>round(nvl(ard_nowprice,0),8)",
							String.class, bi_id);
			if (dets != null) {
				BaseUtil.showError("开票单价与发票单价不一致，不允许进行当前操作！行：" + dets);
			}
		} else {
			String accountCate = baseDao.getDBSetting("BillOut", "accountCate");
			if (accountCate == null || accountCate.length() == 0) {
				BaseUtil.showError("调账科目设置未设置！");
			}
		}
	}

	void beforeAccount(int bi_id, Object bi_code) {
		// 自动产生已过账的其它应收单
		if (baseDao.isDBSetting("BillOut", "allowUpdatePrice")) {
			String accountCate = baseDao.getDBSetting("BillOut", "accountCate");
			int count = baseDao.getCount("select count(1) from BillOutDetail where ard_biid=" + bi_id
					+ " and nvl(ard_price,0)<>nvl(ard_nowprice,0)");
			if (count > 0) {
				int bill = baseDao.getCountByCondition("ARBILL", "AB_SOURCEID=" + bi_id
						+ " and AB_SOURCETYPE='应收开票记录' and ab_class='其它应收单'");
				if (bill == 0) {
					String ab_code = baseDao.sGetMaxNumber("ARBill!OTRS", 2);
					int ab_id = baseDao.getSeqId("ARBILL_SEQ");
					baseDao.execute("insert into ARBILL (ab_id,ab_code,ab_date,ab_custid,ab_custcode,ab_custname,ab_currency,ab_rate,ab_sellerid,ab_sellercode,ab_seller,ab_paymentcode,ab_payments,ab_printstatus,ab_status,ab_auditstatus,ab_paystatus,ab_recorder,ab_recorderid,"
							+ "ab_indate,ab_class,ab_vouchercode,ab_sendtype,AB_SOURCETYPE,AB_SOURCEID,ab_auditstatuscode,ab_paystatuscode,ab_statuscode,ab_printstatuscode,ab_aramount,ab_payamount,ab_cop,ab_departmentcode,ab_departmentname,ab_refno,AB_SOURCECODE) "
							+ "select "
							+ ab_id
							+ ", '"
							+ ab_code
							+ "',bi_date,cu_id,bi_custcode,bi_custname,bi_currency,bi_rate,em_id,bi_sellercode,bi_seller,bi_paymentscode,bi_paymentsmethod,'"
							+ BaseUtil.getLocalMessage("UNPRINT")
							+ "','"
							+ BaseUtil.getLocalMessage("UNPOST")
							+ "','"
							+ BaseUtil.getLocalMessage("ENTERING")
							+ "','"
							+ BaseUtil.getLocalMessage("UNCOLLECT")
							+ "','"
							+ SystemSession.getUser().getEm_name()
							+ "',"
							+ SystemSession.getUser().getEm_id()
							+ ", sysdate, '其它应收单','UNNEED',bi_sendkind,'应收开票记录',bi_id,'ENTERING','UNCOLLECT','UNPOST','UNPRINT',0,0,bi_cop,bi_departmentcode,bi_department,bi_refno,bi_code "
							+ "from billout left join customer on bi_custcode=cu_code left join employee on bi_sellercode=em_code where bi_id="
							+ bi_id);
					baseDao.execute("insert into arbilldetail (abd_id,abd_abid,abd_code,abd_detno,abd_catecode,abd_qty,abd_price,abd_taxrate,abd_aramount,abd_status,abd_sourcekind,abd_sourcetype,abd_sourcedetailid) "
							+ "select arbilldetail_seq.nextval,"
							+ ab_id
							+ ", '"
							+ ab_code
							+ "',rownum,'"
							+ accountCate
							+ "',1,round(ard_nowbalance-ard_orderamount,2),ard_taxrate,round(ard_nowbalance-ard_orderamount,2),'0','BILLOUT','应收开票记录',ard_id from billoutdetail where ard_biid="
							+ bi_id + " and nvl(ard_price,0)<>nvl(ard_nowprice,0)");
					baseDao.execute("update arbilldetail set abd_aramount=ROUND(abd_price*abd_qty,2) WHERE abd_abid=" + ab_id);
					baseDao.execute("update arbilldetail set abd_noaramount=ROUND(abd_price*abd_qty/(1+abd_taxrate/100),2) WHERE abd_abid="
							+ ab_id);
					baseDao.execute("update arbill set ab_taxamount=(select sum(round(((abd_price*abd_qty*abd_taxrate/100)/(1+abd_taxrate/100)),2)) from arbilldetail where abd_abid=ab_id)+ab_differ where ab_id="
							+ ab_id);
					baseDao.execute("update arbilldetail set abd_taxamount=NVL(abd_aramount,0)-NVL(abd_noaramount,0) WHERE abd_abid="
							+ ab_id);
					// 更新ARBill主表的金额
					baseDao.execute("update arbill set ab_aramount=round((select sum(abd_aramount) from arbilldetail where abd_abid=ab_id),2) where ab_id="
							+ ab_id);
					// 存储过程
					String res1 = baseDao.callProcedure("Sp_CommiteARBill", new Object[] { ab_code, 1 });
					if (res1 != null && !res1.trim().equals("")) {
						BaseUtil.showError(res1);
					}
					baseDao.updateByCondition("ARBill", "ab_statuscode='POSTED',ab_status='" + BaseUtil.getLocalMessage("POSTED") + "'",
							"ab_id=" + ab_id);
					baseDao.updateByCondition("ARBillDetail", "abd_status=99,abd_statuscode='POSTED'", "abd_abid=" + ab_id);
				}
			}
		}
	}

	@Override
	public void accountedBillOut(int bi_id, String caller) {
		// 只能对状态为[未记账]的订单进行操作!
		Object[] status = baseDao.getFieldsDataByCondition("BillOut", new String[] { "bi_statuscode", "bi_code" }, "bi_id=" + bi_id);
		if (status[0].equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.account_onlyCommited"));
		}
		baseDao.execute("update BillOut set bi_rate=(select cm_crrate from CurrencysMonth where cm_crname=bi_currency "
				+ " and CM_YEARMONTH=to_char(bi_date,'yyyymm')) where bi_id=" + bi_id + " and nvl(bi_currency,' ')<>' '");
		check(bi_id);
		beforeAccount(bi_id, status[1]);
		// 执行记账前的其它逻辑
		handlerService.handler("BillOut", "account", "before", new Object[] { bi_id });
		// 执行记账操作
		// 存储过程
		String res = baseDao.callProcedure("SP_COMMITEBILLOUTAR", new Object[] { status[1] });
		if (res != null && !res.trim().equals("") && !"OK".equals(res.toUpperCase())) {
			BaseUtil.showError("单据[" + status[1] + "]" + res);
		}
		getTotal(bi_id);
		baseDao.updateByCondition("BillOut", "bi_statuscode='POSTED',bi_status='已过账',bi_postman='" + SystemSession.getUser().getEm_name()
				+ "',bi_postdate=sysdate", "bi_id=" + bi_id);
		baseDao.updateByCondition("BillOutDetail", "ard_statuscode='POSTED',ard_status='" + BaseUtil.getLocalMessage("POSTED") + "'",
				"ard_biid=" + bi_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.account"), BaseUtil
				.getLocalMessage("msg.accountSuccess"), "BillOut|bi_id=" + bi_id));
		// 执行记账后的其它逻辑
		handlerService.handler("BillOut", "account", "after", new Object[] { bi_id });
		baseDao.execute("update ARBillDETAIL set abd_invoamount=nvl((select round(sum(nvl(ard_nowbalance,0)),2) from BILLOUTDETAIL where ard_ordercode=abd_code and ard_orderdetno=abd_detno),0) where (abd_code,abd_detno) in (select distinct ard_ordercode,ard_orderdetno from BillOutDetail where ard_biid="
				+ bi_id + " and ard_statuscode='POSTED')");
		baseDao.execute("update ARBill set ab_invoamount=nvl((select round(sum(nvl(abd_invoamount,0)),2) from ARBillDetail where abd_abid=ab_id),0) where ab_code in (select distinct ard_ordercode from BillOutDetail where ard_biid="
				+ bi_id + ")");
	}

	@Override
	public void resAccountedBillOut(int bi_id, String caller) {
		// 只能对状态为[已记账]的订单进行反记账操作!
		Object[] status = baseDao.getFieldsDataByCondition("BillOut", new String[] { "bi_statuscode", "bi_code", "bi_token","bi_refno" }, "bi_id=" + bi_id);
		if (!status[0].equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAccount_onlyAccount"));
		}
		if (StringUtil.hasText(status[2])&&StringUtil.hasText(status[3])) {
			BaseUtil.showError("已开具发票，不允许反过账！");
		}else if (StringUtil.hasText(status[2])) {
			BaseUtil.showError("申请开具发票中，请先取消申请！");
		}
		
		handlerService.handler("BillOut", "resAccount", "before", new Object[] { bi_id });
		// 执行反记账操作
		String res = null;
		Object[] ab = baseDao.getFieldsDataByCondition("arbill",
				new String[] { "ab_code", "ab_statuscode", "ab_id", "nvl(ab_payamount,0)" }, "ab_sourcetype='应收开票记录' and ab_sourceid="
						+ bi_id);
		if (ab != null) {
			if (Double.parseDouble(ab[3].toString()) != 0) {
				BaseUtil.showError("关联的其它应收单[" + ab[0] + "]已收款，请先取消收款!");
			}
			if ("POSTED".equals(ab[1])) {
				res = baseDao.callProcedure("Sp_UnCommiteARBill", new Object[] { ab[0], 1 });
				if (res != null && !res.trim().equals("")) {
					BaseUtil.showError(res);
				}
			}
			baseDao.execute("delete from arbilldetail where abd_abid=" + ab[2]);
			baseDao.execute("delete from arbill where ab_id=" + ab[2]);
		}
		res = baseDao.callProcedure("SP_UNCOMMITEBILLOUTAR", new Object[] { status[1] });
		if (res != null && !res.trim().equals("") && !"OK".equals(res.toUpperCase())) {
			BaseUtil.showError(res);
		}
		baseDao.updateByCondition("BillOut", "bi_statuscode='ENTERING',bi_status='" + BaseUtil.getLocalMessage("ENTERING")
				+ "',bi_postdate=null,bi_postman=null", "bi_id=" + bi_id);
		baseDao.updateByCondition("BillOutDetail", "ard_statuscode='ENTERING',ard_status='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"ard_biid=" + bi_id);
		baseDao.execute("update ARBillDETAIL set abd_invoamount=nvl((select round(sum(nvl(ard_nowbalance,0)),2) from BILLOUTDETAIL where ard_ordercode=abd_code and ard_orderdetno=abd_detno),0) where (abd_code,abd_detno) in (select distinct ard_ordercode,ard_orderdetno from BillOutDetail where ard_biid="
				+ bi_id + " and ard_statuscode='POSTED')");
		baseDao.execute("update ARBill set ab_invoamount=nvl((select round(sum(nvl(abd_invoamount,0)),2) from ARBillDetail where abd_abid=ab_id),0) where ab_code in (select distinct ard_ordercode from BillOutDetail where ard_biid="
				+ bi_id + ")");
		// baseDao.execute("UPDATE ARBILL SET AB_BILLDATE=(SELECT BI_POSTDATE FROM (SELECT DISTINCT BI_POSTDATE,ARD_ORDERCODE FROM (SELECT BI_POSTDATE,ARD_ORDERCODE,RANK() OVER (PARTITION BY ARD_ORDERCODE ORDER BY BI_POSTDATE DESC) I "
		// +
		// "FROM BILLOUT,BILLOUTDETAIL WHERE ARD_BIID=BI_ID AND BI_STATUSCODE='POSTED' AND ARD_ORDERCODE IN (SELECT ARD_ORDERCODE FROM BILLOUTDETAIL WHERE ARD_BIID="
		// + bi_id
		// +
		// ")) WHERE I=1) WHERE ARD_ORDERCODE=AB_CODE) WHERE AB_CODE IN (SELECT DISTINCT ARD_ORDERCODE FROM BILLOUTDETAIL WHERE ARD_BIID="
		// + bi_id + ") AND AB_STATUSCODE='POSTED'");
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.resAccount"), BaseUtil
				.getLocalMessage("msg.resAccountSuccess"), "BillOut|bi_id=" + bi_id));
		// 执行记账后的其它逻辑
		handlerService.handler("BillOut", "resAccount", "after", new Object[] { bi_id });
	}

	@Override
	public String[] printVoucherCodeBillOut(int bi_id, String caller, String reportName, String condition) {
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, bi_id);
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "bi_id", bi_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, bi_id);
		return keys;
	}

	@Override
	public void updateTaxcode(String caller, int bi_id, String bi_refno, String bi_remark) {
		baseDao.updateByCondition("BillOut", "bi_refno='" + bi_refno + "',bi_remark='" + bi_remark + "',bi_token=null", "bi_id=" + bi_id);
		baseDao.logger.others("更新税票信息", "更新成功", caller, "bi_id", bi_id);
	}
	
	/**
     * 生成签名
     * @return
     */
    private String sign(String api, String httpMethod, List<String> paras, String taxpayerKey) {
        StringBuilder content = new StringBuilder();
        content.append(httpMethod);
        //获取请求的url
        content.append(api);
        
        //参数按字典排序
        Collections.sort(paras,new Comparator<String>(){
            public int compare(String str, String str1) {
                return str.compareTo(str1);
            }
        });
        
        for (String para : paras) {
        	content.append(para);
		}
        
        content.append(taxpayerKey);
        
        String sign = null;
        try {
            sign = MD5.toMD5(URLEncoder.encode(content.toString(),"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sign;
    }
	
	/**
	 * 应收发票开具
	 */
	@Override
	public Map<String, Object> openInvoice(String caller, int bi_id) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		Object [] billOut = baseDao.getFieldsDataByCondition("BillOut", new String [] {"bi_code","bi_custcode","bi_remark","bi_token","bi_refno"}, "bi_id = "+bi_id);
	        
        if (billOut==null) {
			BaseUtil.showError("此开票记录不存在，不允许开具发票！");
		}else if(StringUtil.hasText(billOut[3])&&StringUtil.hasText(billOut[4])){
			BaseUtil.showError("已开发票，不允许重复开具发票！");
		}else if(StringUtil.hasText(billOut[3])){
			result.put("token", billOut[3]);
			return result;
		}
		
		
        String mediumType = baseDao.getDBSetting(caller, "mediumType")==null?"2":baseDao.getDBSetting(caller, "mediumType");//1: 电子发票; 2: 纸质发票
        String category = baseDao.getDBSetting(caller, "category")==null?"1":baseDao.getDBSetting(caller, "category");//发票分类  1: 增值税专用发票; 4: 增值税普通发票（纸质）；10: 增值税普通发票（电子）
        String splitStrategy = baseDao.getDBSetting(caller, "splitStrategy")==null?"1":baseDao.getDBSetting(caller, "splitStrategy");//拆分策略: 1:数量(按照数量拆分); 2单价(按照单价拆分)
       
        //获取购方开票信息
        Object [] customer = baseDao.getFieldsDataByCondition("Customer", 
        		new String [] {"cu_name","cu_bank","cu_bankaccount","cu_add1","cu_mobile","cu_tel","cu_taxid"}, "cu_code = '"+billOut[1]+"'");
        if (customer==null) {
        	BaseUtil.showError("客户资料不存在，不允许开具发票！");
		}else if (!StringUtil.hasText(customer[0])) {
			BaseUtil.showError("客户名称不存在！");
		}
       
        Object [] enterprise = baseDao.getFieldsDataByCondition("Enterprise", new String [] {"en_taxcode","en_taxpayerKey","en_taxwebsite"}, "1=1");
        if (enterprise == null) {
        	BaseUtil.showError("企业信息不存在！");
		}
		
		if (!StringUtil.hasText(enterprise[0])) {
			BaseUtil.showError("纳税人识别号不存在，请先去企业信息进行维护！");
		}
		String taxpayerId = String.valueOf(enterprise[0]);
		
		if (!StringUtil.hasText(enterprise[1])) {
			BaseUtil.showError("taxpayerKey不存在，请先去企业信息进行维护！");
		}
		if (!StringUtil.hasText(enterprise[2])) {
			BaseUtil.showError("开具发票网址不存在，请先去企业信息进行维护！");
		}
		
		String taxpayerKey = String.valueOf(enterprise[1]);
		
		String Interface = "imanager/manager/importInvoiceInfoFromBPMDataOut.ysy";
		String website = String.valueOf(enterprise[2]);
		if (!website.endsWith("/")) {
			website += "/";
		}
		String action = website.substring(website.indexOf("://")+3)+Interface;
        //签名部分
        List<String> paras = new ArrayList<>();
        paras.add("sysOrderNo=" + billOut[0]);
        paras.add("taxpayerId=" + taxpayerId);
        String timestamp = String.valueOf(System.currentTimeMillis());
        paras.add("timestamp=" + timestamp);
        
        JSONObject paraMap = new JSONObject(21,true);
        paraMap.put("sysOrderNo", billOut[0]);//申请单编号
        paraMap.put("mediumType", mediumType);//发票媒介类型
        paraMap.put("category", category);//发票分类
        paraMap.put("taxpayerId", taxpayerId);
        paraMap.put("buyerName", customer[0]);
       
        if ("1".equals(category)) {
            if (!StringUtil.hasText(customer[6])) {
            	BaseUtil.showError("客户纳税识别号为空！");
            }
            if (!StringUtil.hasText(customer[3])) {
            	BaseUtil.showError("客户地址为空！");
            }
            if (!StringUtil.hasText(customer[4])&&!StringUtil.hasText(customer[5])) {
            	BaseUtil.showError("客户联系电话为空！");
            }
            if (!StringUtil.hasText(customer[1])) {
            	BaseUtil.showError("客户方开户银行为空！");
            }
            
            if (!StringUtil.hasText(customer[2])) {
            	BaseUtil.showError("客户方银行账号为空！");
            }
        } 
        
        Object buyerTaxpayerId = customer[6]==null?"":customer[6];
        Object buyerAddr = customer[3]==null?"":customer[3];
        Object buyerMobile = StringUtil.hasText(customer[4])?customer[4]:customer[5];
        buyerMobile = buyerMobile == null?"":buyerMobile;
        Object buyerBankName = customer[1]==null?"":customer[1];
        Object buyerAccountNo = customer[2]==null?"":customer[2];
        paraMap.put("buyerTaxpayerId", buyerTaxpayerId);
        paraMap.put("buyerAddr", buyerAddr);
        paraMap.put("buyerMobile", buyerMobile);
        paraMap.put("buyerBankName", buyerBankName);
        paraMap.put("buyerAccountNo", buyerAccountNo);

        Employee employee = SystemSession.getUser();
        paraMap.put("drawer", employee.getEm_name());//收款人
        paraMap.put("payee", "");//收款人
        paraMap.put("checker", "");//复核人
        paraMap.put("remark", billOut[2]);

        paraMap.put("urlAddress", "");//开票成功回调地址  用于回调开票成功信息
        paraMap.put("settlementNum", "");//结算单号
        paraMap.put("contractNum", "");//合同编号
        paraMap.put("businessInfo", "");//业务信息
        paraMap.put("splitStrategy", splitStrategy);
        Integer precisionNumber = baseDao.queryForObject("select max(pr_precision) from BILLOUTDETAIL_INVOICE_VIEW where ard_biid = ?", Integer.class, bi_id);
        paraMap.put("precisionNumber", ""+precisionNumber);
        
		List<JSONObject> wares = new ArrayList<>();
		SqlRowList rs = baseDao.queryForRowSet("select pr_detail,ard_nowqty,ard_nowprice,pr_taxcode,ard_taxrate,pr_unit,substrb(pr_spec,1,24) pr_spec from BILLOUTDETAIL_INVOICE_VIEW where ard_biid = ?", bi_id);
		while (rs.next()) {
			if (!StringUtil.hasText(rs.getString("pr_detail"))) {
				BaseUtil.showError("明细行号：" + rs.getGeneralInt("ard_detno") + ",物料名称不存在！");
			}
			if (!StringUtil.hasText(rs.getString("pr_taxcode"))) {
				BaseUtil.showError("明细行号：" + rs.getGeneralInt("ard_detno") + ",物料商品编号不存在！");
			}

			JSONObject ware = new JSONObject(7, true);
			ware.put("wareName", rs.getGeneralString("pr_detail"));// 商品名称
			ware.put("count", Math.abs(rs.getGeneralDouble("ard_nowqty")));// 数量 整数
			ware.put("unitPrice", NumberUtil.formatDouble(rs.getGeneralDouble("ard_nowprice"), 2));// 单价 ， 小数点后2位，以元为单位精确到分，含税价
			String wareNo = rs.getString("pr_taxcode");
			int length = wareNo.length();
			for (int i = length; i < 19; i++) {
				wareNo += "0";
			}
			ware.put("wareNo", wareNo);// 商品编码// 应与税局最新发行的商品和服务税收分类与编码相一致
			ware.put("taxRate", rs.getGeneralString("ard_taxrate"));// 0表示免税，两位小数，0.17
			ware.put("unit", rs.getGeneralString("pr_unit"));// 单位
			ware.put("standard", rs.getGeneralString("pr_spec"));// 规格型号

			wares.add(ware);
		}

		paraMap.put("invitem", wares);
		
		Response response = null;
		// 调用票加加开票接口
		try {
			String sign = sign(action, "POST", paras, taxpayerKey);
			String url = website + Interface + "?timestamp=" + timestamp + "&sign=" + sign;
			response = HttpUtil.doPost(url, paraMap.toString(), false, null);
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("链接票加加开票接口失败!");
		}
		
		if (response.getStatusCode() != HttpStatus.OK.value()) {
			BaseUtil.showError("连接票加加失败," + response.getStatusCode());
		} else {
			String data = response.getResponseText();
			if (data == null) {
				BaseUtil.showError("从票加加开票接口获取数据失败!");
			}
			JSONObject resultJo = JSON.parseObject(data);
			String code = resultJo.getString("code");// 结果码 0000代表成功，其它为失败
			String resMsg = resultJo.getString("resMsg");
			String invoiceImportTokenList = resultJo.getString("invoiceImportTokenList");// 请求流水号
			if ("0000".equals(code)) {
				try {
					baseDao.updateByCondition("BillOut", "bi_token = '" + invoiceImportTokenList + "'", "bi_id = '" + bi_id + "'");
					
					result.put("token", invoiceImportTokenList);
					result.put("resMsg", resMsg); 
					baseDao.logger.others("发票开具申请", "申请成功", caller, "bi_id", bi_id);
				} catch (Exception e) {
					BaseUtil.showError("上传发票成功，但过程中数据库更新开票单失败，需要人工紧急处理，重新上传此开票单据");
				}
			} else {
				baseDao.logger.others("发票开具申请", "申请失败，"+resMsg, caller, "bi_id", bi_id);
				BaseUtil.showError("开具发票失败，错误：" + resMsg);
			}
		}
		return result;
    }
	
	@Override
	public String cancelInvoiceApply(String caller, int bi_id) {
		Object [] billOut = baseDao.getFieldsDataByCondition("BillOut", new String [] {"bi_token","bi_refno"}, "bi_id = "+bi_id);
	        
        if (billOut==null) {
			BaseUtil.showError("此开票记录不存在！");
		} else if(StringUtil.hasText(billOut[0])&&StringUtil.hasText(billOut[1])){
			BaseUtil.showError("已开具发票，不能取消申请！");
		} else if(!StringUtil.hasText(billOut[0])){
			BaseUtil.showError("未进行申请，无需取消！");
		}
        
        Object [] enterprise = baseDao.getFieldsDataByCondition("Enterprise", new String [] {"en_taxcode","en_taxpayerKey","en_taxwebsite"}, "1=1");
        if (enterprise == null) {
        	BaseUtil.showError("企业信息不存在！");
		}
		
		if (!StringUtil.hasText(enterprise[0])) {
			BaseUtil.showError("纳税人识别号不存在，请先去企业信息进行维护！");
		}
		String taxpayerId = String.valueOf(enterprise[0]);
		
		if (!StringUtil.hasText(enterprise[1])) {
			BaseUtil.showError("taxpayerKey不存在，请先去企业信息进行维护！");
		}
		if (!StringUtil.hasText(enterprise[2])) {
			BaseUtil.showError("开具发票网址不存在，请先去企业信息进行维护！");
		}
		
		String taxpayerKey = String.valueOf(enterprise[1]);
		
		String Interface = "imanager/manager/deleteInvoiceCollectionFromBpm.ysy";
		
		String website = String.valueOf(enterprise[2]);
		if (!website.endsWith("/")) {
			website += "/";
		}
		
		String action = website.substring(website.indexOf("://")+3) + Interface;
		
        //签名部分
        List<String> paras = new ArrayList<>();
        paras.add("taxpayerId=" + taxpayerId);
        String timestamp = String.valueOf(System.currentTimeMillis());
        paras.add("timestamp=" + timestamp);

        String token = String.valueOf(billOut[0]);
        List<Map<Object, Object>> list = BaseUtil.parseGridStoreToMaps(token);
        List<Object> sysOrderNoList = new ArrayList<Object>();
        for (Map<Object, Object> map : list) {
        	sysOrderNoList.add(map.get("sysOrderNo"));
		}
        JSONObject paraMap = new JSONObject(2,true);
        paraMap.put("sysOrderNoList", sysOrderNoList);//申请单编号
        paraMap.put("taxpayerId", taxpayerId);
		
		Response response = null;
		// 调用票加加开票接口
		try {
			String sign = sign(action, "POST", paras, taxpayerKey);
			String url = website + Interface + "?timestamp=" + timestamp + "&sign=" + sign;
			response = HttpUtil.doPost(url, paraMap.toString(), false, null);
		} catch (Exception e) {
			BaseUtil.showError("链接票加加开票接口失败!");
			e.printStackTrace();
		}
		
		if (response.getStatusCode() != HttpStatus.OK.value()) {
			BaseUtil.showError("连接票加加失败," + response.getStatusCode());
		} else {
			String data = response.getResponseText();
			if (data == null) {
				BaseUtil.showError("从票加加开票接口获取数据失败!");
			}
			JSONObject resultJo = JSON.parseObject(data);
			String code = resultJo.getString("code");// 结果码 0000代表成功，其它为失败
			String resMsg = resultJo.getString("resMsg");
			if ("0000".equals(code)) {
				try {
					baseDao.updateByCondition("BillOut", "bi_token = ''", "bi_id = '" + bi_id + "'");
					baseDao.logger.others("取消发票开具申请", "取消成功", caller, "bi_id", bi_id);
					return resMsg; 
				} catch (Exception e) {
					BaseUtil.showError("取消发票开具申请，但过程中数据库更新开票单失败");
				}
			} else {
				baseDao.logger.others("取消发票开具申请", "取消失败，"+resMsg, caller, "bi_id", bi_id);
				BaseUtil.showError("开具发票失败，错误：" + resMsg);
			}
		}
		return null;
	}

	@Override
	public String queryInvoiceInfo(String caller, int bi_id) {
		Object [] billOut = baseDao.getFieldsDataByCondition("BillOut", new String [] {"bi_token","bi_refno"}, "bi_id = "+bi_id);
        
        if (billOut==null) {
			BaseUtil.showError("此开票记录不存在！");
		} else if(StringUtil.hasText(billOut[0])&&StringUtil.hasText(billOut[1])){
			return null;
		} else if(!StringUtil.hasText(billOut[0])){
			return null;
		}
        
        Object [] enterprise = baseDao.getFieldsDataByCondition("Enterprise", new String [] {"en_taxcode","en_taxpayerKey","en_taxwebsite"}, "1=1");
        if (enterprise == null) {
        	BaseUtil.showError("企业信息不存在！");
		}
		
		if (!StringUtil.hasText(enterprise[0])) {
			BaseUtil.showError("纳税人识别号不存在，请先去企业信息进行维护！");
		}
		String taxpayerId = String.valueOf(enterprise[0]);
		
		if (!StringUtil.hasText(enterprise[1])) {
			BaseUtil.showError("taxpayerKey不存在，请先去企业信息进行维护！");
		}
		if (!StringUtil.hasText(enterprise[2])) {
			BaseUtil.showError("开具发票网址不存在，请先去企业信息进行维护！");
		}
		
		String taxpayerKey = String.valueOf(enterprise[1]);
		
		String Interface = "imanager/manager/queryInvoiceInfoCodeNumber.ysy";
		String website = String.valueOf(enterprise[2]);
		if (!website.endsWith("/")) {
			website += "/";
		}
		String action = website.substring(website.indexOf("://")+3) + Interface;
        
        //签名部分
        List<String> paras = new ArrayList<>();
        paras.add("taxpayerId=" + taxpayerId);
        String timestamp = String.valueOf(System.currentTimeMillis());
        paras.add("timestamp=" + timestamp);

        String token = String.valueOf(billOut[0]);
        List<Map<Object, Object>> list = BaseUtil.parseGridStoreToMaps(token);
        List<String> invoiceNums = new ArrayList<String>();
        for (Map<Object, Object> map : list) {
	    	JSONObject paraMap = new JSONObject(2,true);
	    	paras.add("sysOrderNo=" + map.get("sysOrderNo"));
	        paraMap.put("sysOrderNo", map.get("sysOrderNo"));//申请单编号
	        paraMap.put("taxpayerId", taxpayerId);
     		Response response = null;
     		// 调用票加加开票接口
     		try {
     			String sign = sign(action, "POST", paras, taxpayerKey);
     			String url = website + Interface + "?timestamp=" + timestamp + "&sign=" + sign;
     			response = HttpUtil.doPost(url, paraMap.toString(), false, null);
     		} catch (Exception e) {
     			BaseUtil.showError("链接票加加开票接口失败!");
     			e.printStackTrace();
     		}
     		
     		if (response.getStatusCode() != HttpStatus.OK.value()) {
     			BaseUtil.showError("连接票加加失败," + response.getStatusCode());
     		} else {
     			String data = response.getResponseText();
     			if (data == null) {
     				BaseUtil.showError("从票加加开票接口获取数据失败!");
     			}
     			JSONObject resultJo = JSON.parseObject(data);
     			String code = resultJo.getString("code");// 结果码 0000代表成功，其它为失败
     			String resMsg = resultJo.getString("resMsg");
     			JSONArray invoiceCodeNumberList = resultJo.getJSONArray("invoiceCodeNumberList");// 开票结果列表
     			if ("0000".equals(code)) {
     				for (int i = 0; i < invoiceCodeNumberList.size(); i++) {
     					JSONObject obj = invoiceCodeNumberList.getJSONObject(i);
     					String invoiceNum = obj.getString("invoiceNum");
     					if (StringUtil.hasText(invoiceNum)) {
     						invoiceNums.add(invoiceNum);
						}
					}
     			} else {
     				baseDao.logger.others("获取开票结果", "获取失败，"+resMsg, caller, "bi_id", bi_id);
     				BaseUtil.showError("获取开票结果失败，错误：" + resMsg);
     			}
     		}
		}
        if (invoiceNums.size()>0) {
        	 try {
        		Collections.sort(invoiceNums);
             	String invoiceNum = invoiceNums.get(0);
             	if (invoiceNums.size()>1) {
                 	String minNum = invoiceNums.get(0);
                 	String maxNum = invoiceNums.get(invoiceNums.size()-1);
                 	char[] minNumArray = minNum.toCharArray();
                 	char[] maxNumArray = maxNum.toCharArray();
                 	String publicString = "";
                 	for (int i = 0; i < maxNumArray.length; i++) {
						if (minNumArray[i]!=maxNumArray[i]) {
							break;
						}
						publicString += minNumArray[i];
					}
                 	invoiceNum += "-" + maxNum.substring(publicString.length());
     			}
             
     			baseDao.updateByCondition("BillOut", "bi_refno = '" + invoiceNum + "'", "bi_id = '" + bi_id + "'");
     			baseDao.logger.others("获取开票结果", "获取成功", caller, "bi_id", bi_id);
     			return invoiceNum; 
     		} catch (Exception e) {
     			BaseUtil.showError("获取开票结果成功，但过程中数据库更新开票单失败");
     		}
		}
        
		return null;
	}

	@Override
	public String getTaxWebSite() {
		try {
			return baseDao.queryForObject("select En_Taxwebsite from enterprise", String.class);
		} catch (Exception e) {
			return null;
		}
	}
}
