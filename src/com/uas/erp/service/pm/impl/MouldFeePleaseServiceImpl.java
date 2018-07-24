package com.uas.erp.service.pm.impl;

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
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.MouldFeePleaseDao;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.pm.MouldFeePleaseService;

@Service("mouldFeePleaseService")
public class MouldFeePleaseServiceImpl implements MouldFeePleaseService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private MouldFeePleaseDao mouldFeePleaseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveMouldFeePlease(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("MOULDFEEPLEASE", "mp_code='" + store.get("mp_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存MouldFeePlease
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "MOULDFEEPLEASE", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存MouldFeePleaseDetail
		for (Map<Object, Object> s : grid) {
			s.put("mfd_id", baseDao.getSeqId("MOULDFEEPLEASEDETAIL_SEQ"));
			s.put("mfd_code", store.get("mp_code"));
			if (StringUtil.hasText(s.get("mfd_purccode")) && s.get("mfd_pddetno") != null && !"0".equals(s.get("mfd_pddetno"))) {
				mouldFeePleaseDao.restoreYamount(Double.parseDouble(s.get("mfd_amount").toString()), s.get("mfd_purccode").toString(),
						Integer.valueOf(s.get("mfd_pddetno").toString()), s.get("mfd_purcdetno"));
			}
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "MOULDFEEPLEASEDETAIL");

		baseDao.execute(gridSql);
		baseDao.execute("update MOULDFEEPLEASEDETAIL set mfd_code=(select mp_code from MOULDFEEPLEASE where mfd_mpid=mp_id) where mfd_mpid="
				+ store.get("mp_id") + " and not exists (select 1 from MOULDFEEPLEASE where mfd_code=mp_code)");
		baseDao.updateByCondition("MOULDFEEPLEASE",
				"mp_total=(SELECT round(nvl(sum(nvl(mfd_amount,0)),0),2) FROM MOULDFEEPLEASEDETAIL WHERE mfd_mpid=mp_id)",
				"mp_id=" + store.get("mp_id"));
		baseDao.logger.save(caller, "mp_id", store.get("mp_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void deleteMouldFeePlease(int mp_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("MOULDFEEPLEASE", "mp_statuscode", "mp_id=" + mp_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { mp_id });
		// 删除
		mouldFeePleaseDao.deleteMouldFeePlease(mp_id);
		// 记录操作
		baseDao.logger.delete(caller, "mp_id", mp_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { mp_id });
	}

	@Override
	public void updateMouldFeePleaseById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("MOULDFEEPLEASE", "mp_statuscode", "mp_id=" + store.get("mp_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改MouldFeePlease
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "MOULDFEEPLEASE", "mp_id"));
		// 修改MouldFeePleaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "MOULDFEEPLEASEDetail", "mfd_id");
		for (Map<Object, Object> s : gstore) {
			Object mfdid = s.get("mfd_id");
			Object pdid = s.get("mfd_pdid");
			if (mfdid == null || mfdid.equals("") || mfdid.equals("0") || Integer.parseInt(mfdid.toString()) == 0) {// 新添加的数据，id不存在
				if (s.get("mfd_purccode") != null && !"".equals(s.get("mfd_purccode")) && s.get("mfd_pddetno") != null
						&& !"".equals(s.get("mfd_pddetno")) && !"0".equals(s.get("mfd_pddetno"))) {
					if (pdid == null || pdid.equals("") || pdid.equals("0")) {
						pdid = baseDao.getFieldDataByCondition("purmoulddet left join purmoul on pm_id=pd_pmid", "pd_id",
								"pm_code='" + s.get("mfd_purccode") + "' and pd_detno=" + s.get("mfd_pddetno"));
						s.put("mfd_pdid", pdid);
					}
					mouldFeePleaseDao.restoreYamount(Double.parseDouble(s.get("mfd_amount").toString()), s.get("mfd_purccode").toString(),
							Integer.valueOf(s.get("mfd_pddetno").toString()), s.get("mfd_purcdetno"));
				}
				baseDao.execute(SqlUtil.getInsertSql(s, "MOULDFEEPLEASEDetail", "mfd_id"));
			} else {
				mouldFeePleaseDao.restoreWithAmount(Integer.parseInt(mfdid.toString()), Double.parseDouble(s.get("mfd_amount").toString()),
						s.get("mfd_purccode"), s.get("mfd_pddetno"), s.get("mfd_purcdetno"));
			}
		}
		baseDao.execute(gridSql);
		baseDao.execute("update MOULDFEEPLEASEDETAIL set mfd_code=(select mp_code from MOULDFEEPLEASE where mfd_mpid=mp_id) where mfd_mpid="
				+ store.get("mp_id") + " and not exists (select 1 from MOULDFEEPLEASE where mfd_code=mp_code)");
		baseDao.updateByCondition("MOULDFEEPLEASE",
				"mp_total=(SELECT round(nvl(sum(nvl(mfd_amount,0)),0),2) FROM MOULDFEEPLEASEDETAIL WHERE mfd_mpid=mp_id)",
				"mp_id=" + store.get("mp_id"));
		// 记录操作
		baseDao.logger.update(caller, "mp_id", store.get("mp_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void auditMouldFeePlease(int mp_id, String caller) {
		baseDao.execute("update MOULDFEEPLEASEDETAIL set mfd_code=(select mp_code from MOULDFEEPLEASE where mfd_mpid=mp_id) where mfd_mpid="
				+ mp_id + " and not exists (select 1 from MOULDFEEPLEASE where mfd_code=mp_code)");
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("MOULDFEEPLEASE", "mp_statuscode", "mp_id=" + mp_id);
		StateAssert.auditOnlyCommited(status);
		check(mp_id);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { mp_id });
		// 执行审核操作
		baseDao.audit("MOULDFEEPLEASE", "mp_id=" + mp_id, "mp_status", "mp_statuscode", "mp_auditdate", "mp_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "mp_id", mp_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { mp_id });
	}

	@Override
	public void resAuditMouldFeePlease(int mp_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("MOULDFEEPLEASE", "mp_statuscode", "mp_id=" + mp_id);
		StateAssert.resAuditOnlyAudit(status);
		int count = baseDao.getCount("select count(*) from AccountRegister where ar_sourcetype='模具付款申请' and ar_sourceid=" + mp_id);
		if (count > 0) {
			BaseUtil.showError("已经转入银行登记，不允许反审核!");
		}
		count = baseDao.getCount("select count(*) from BillAP where bap_sourcetype='模具付款申请' and bap_sourceid=" + mp_id);
		if (count > 0) {
			BaseUtil.showError("已经转入应付票据，不允许反审核!");
		}
		count = baseDao.getCount("select count(*) from BillARChange where brc_sourcetype='模具付款申请' and brc_sourceid=" + mp_id);
		if (count > 0) {
			BaseUtil.showError("已经转入应收票据异动，不允许反审核!");
		}
		// 执行反审核操作
		baseDao.resAudit("MOULDFEEPLEASE", "mp_id=" + mp_id, "mp_status", "mp_statuscode", "mp_auditdate", "mp_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "mp_id", mp_id);
	}

	private void check(int mp_id) {
		int count = baseDao
				.getCount("select count(*) from MOULDFEEPLEASEDETAIL where mfd_mpid="
						+ mp_id
						+ " and nvl(mfd_purccode,' ')<>' ' and nvl(mfd_pddetno,0)=0 group by mfd_purccode,mfd_purcdetno,mfd_pscode,mfd_paymentscode");
		if (count > 1) {
			BaseUtil.showError("采购单+序号+模具编号+付款方式，只能录入1条明细！");
		}
		Object y = 0;
		String log = null;
		StringBuffer sb = new StringBuffer();
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select mfd_amount,mfd_purccode, mfd_purcdetno, round(nvl(pmd_qty,0)*nvl(pmd_price,0),2) pmd_total from (select round(sum(nvl(mfd_amount,0)),2) mfd_amount, mfd_purccode, mfd_purcdetno from MOULDFEEPLEASEDETAIL where mfd_mpid=? and nvl(mfd_purccode,' ')<>' ' and nvl(mfd_purcdetno,0)<>0 group by mfd_purccode, mfd_purcdetno) left join PurMouldDetail on mfd_purccode=pmd_code and mfd_purcdetno=pmd_detno",
						mp_id);
		while (rs.next()) {
			y = baseDao.getFieldDataByCondition(
					"MOULDFEEPLEASEDETAIL",
					"sum(nvl(mfd_amount,0))",
					"mfd_mpid<>" + mp_id + " and mfd_purccode='" + rs.getGeneralString("mfd_purccode") + "' AND mfd_purcdetno="
							+ rs.getGeneralInt("mfd_purcdetno"));
			y = y == null ? 0 : y;
			if (rs.getGeneralDouble("mfd_amount") + Double.parseDouble(y.toString()) > rs.getGeneralDouble("pmd_total")) {
				log = "申请金额累计数大于模具款！采购单号[" + rs.getGeneralString("mfd_purccode") + "]，行号[" + rs.getGeneralInt("mfd_purcdetno") + "],已申请金额["
						+ (rs.getGeneralDouble("mfd_amount") + Double.parseDouble(y.toString())) + "]，模具款["
						+ rs.getGeneralDouble("pmd_total") + "]";
				if (log != null) {
					sb.append(log).append("<hr>");
				}
			}
		}
		rs = baseDao
				.queryForRowSet(
						"select mfd_amount,mfd_purccode, mfd_pddetno, pd_amount, pd_paydesc from (select round(sum(nvl(mfd_amount,0)),2) mfd_amount, mfd_purccode, mfd_pddetno, mfd_pdid from MOULDFEEPLEASEDETAIL where mfd_mpid=? and nvl(mfd_purccode,' ')<>' ' and nvl(mfd_pddetno,0)<>0 group by mfd_purccode, mfd_pddetno, mfd_pdid) left join PurMouldDet on mfd_pdid=pd_id",
						mp_id);
		while (rs.next()) {
			y = baseDao.getFieldDataByCondition("MOULDFEEPLEASEDETAIL", "sum(nvl(mfd_amount,0))", "mfd_mpid<>" + mp_id
					+ " and mfd_purccode='" + rs.getGeneralString("mfd_purccode") + "' AND mfd_pddetno=" + rs.getGeneralInt("mfd_pddetno"));
			y = y == null ? 0 : y;
			if (rs.getGeneralDouble("mfd_amount") + Double.parseDouble(y.toString()) > rs.getGeneralDouble("pd_amount")) {
				log = "申请金额累计数大于分期款！采购单号[" + rs.getGeneralString("mfd_purccode") + "]，行号[" + rs.getGeneralInt("mfd_pddetno") + "],已申请金额["
						+ (rs.getGeneralDouble("mfd_amount") + Double.parseDouble(y.toString())) + "]，分期款["
						+ rs.getGeneralDouble("pd_amount") + "]";
				if (log != null) {
					sb.append(log).append("<hr>");
				}
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(mfd_detno) from MOULDFEEPLEASEDETAIL,MOULDFEEPLEASE where mfd_mpid=mp_id and mp_id=? and nvl(mp_appcode,' ')<>' ' and nvl(mfd_pscode,' ')<>' ' and nvl(mfd_purcdetno,0)=0"
								+ "and not exists (select 1 from AppMould,AppMouldDetail where app_id=ad_appid and mp_appcode=app_code and mfd_pscode=ad_pscode)",
						String.class, mp_id);
		if (dets != null) {
			BaseUtil.showError("明细行模具资料在开模申请单中不存在，不允许进行当前操作!行号：" + dets);
		}
	}

	@Override
	public void submitMouldFeePlease(int mp_id, String caller) {
		baseDao.execute("update MOULDFEEPLEASEDETAIL set mfd_code=(select mp_code from MOULDFEEPLEASE where mfd_mpid=mp_id) where mfd_mpid="
				+ mp_id + " and not exists (select 1 from MOULDFEEPLEASE where mfd_code=mp_code)");
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MOULDFEEPLEASE", "mp_statuscode", "mp_id=" + mp_id);
		StateAssert.submitOnlyEntering(status);
		baseDao.execute("delete from MOULDFEEPLEASEDETAIL where mfd_mpid=" + mp_id + " and nvl(mfd_amount,0)=0");
		check(mp_id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { mp_id });
		baseDao.updateByCondition("MOULDFEEPLEASE",
				"mp_total=(SELECT round(nvl(sum(nvl(mfd_amount,0)),0),2) FROM MOULDFEEPLEASEDETAIL WHERE mfd_mpid=mp_id)", "mp_id=" + mp_id);
		// 执行提交操作
		baseDao.submit("MOULDFEEPLEASE", "mp_id=" + mp_id, "mp_status", "mp_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "mp_id", mp_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { mp_id });
	}

	@Override
	public void resSubmitMouldFeePlease(int mp_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MOULDFEEPLEASE", "mp_statuscode", "mp_id=" + mp_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { mp_id });
		// 执行反提交操作
		baseDao.resOperate("MOULDFEEPLEASE", "mp_id=" + mp_id, "mp_status", "mp_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "mp_id", mp_id);
		handlerService.afterResSubmit(caller, new Object[] { mp_id });
	}

	@Override
	public int turnAccountRegister(int id, Object thisamount, String catecode, String thisdate, String caller) {
		JSONObject j = null;
		StringBuffer sb = new StringBuffer();
		if (catecode != null && !"".equals(catecode)) {
			String error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code not in (select ca_code from Category where (nvl(ca_iscash,0)=-1 OR nvl(ca_isbank,0)=-1) and nvl(ca_isleaf,0)<>0 and nvl(ca_statuscode,' ')='AUDITED')",
							String.class, catecode);
			if (error != null) {
				BaseUtil.showError("填写科目编号不存在，或者状态不等于已审核，或者不是末级科目，或者不是银行现金科目，不允许转银行登记！");
			}
			baseDao.execute("update MOULDFEEPLEASE set mp_bankcode=? where mp_id=?", catecode, id);
			baseDao.execute(
					"update MOULDFEEPLEASE set mp_bankname=(select ca_description from category where mp_bankcode=ca_code) where mp_id=?",
					id);
			baseDao.execute("update MOULDFEEPLEASE set mp_thispaydate=to_date('" + thisdate + "','yyyy-mm-dd') where mp_id=?", id);
		}
		if (Double.parseDouble(thisamount.toString()) == 0) {
			BaseUtil.showError("已经全部转银行登记!");
		}
		j = mouldFeePleaseDao.turnAccountRegister(id, thisamount);
		int ar_id = 0;
		if (j != null) {
			ar_id = j.getInt("ar_id");
			baseDao.execute("update accountregister set ar_date=to_date('" + thisdate + "','yyyy-mm-dd') where ar_id=?", ar_id);
			baseDao.updateByCondition(
					"AccountRegister",
					"ar_araprate=1,ar_aramount=ar_payment,ar_accountrate=(select nvl(cm_crrate,0) from currencysmonth where cm_crname=ar_accountcurrency and cm_yearmonth=to_char(ar_date,'yyyymm'))",
					"ar_id=" + ar_id);
			baseDao.updateByCondition("AccountRegister", "ar_cateid=(select ca_id from category where ca_code=ar_accountcode)", "ar_id="
					+ ar_id + " and nvl(ar_accountcode,' ')<>' '");
			baseDao.updateByCondition("MOULDFEEPLEASE",
					"mp_paystatuscode='PAYMENTED',mp_paystatus='" + BaseUtil.getLocalMessage("PAYMENTED") + "'", "mp_id =" + id
							+ " and nvl(mp_payamount,0)=nvl(mp_total,0)");
			baseDao.updateByCondition("MOULDFEEPLEASE",
					"mp_paystatuscode='PARTPAYMENT',mp_paystatus='" + BaseUtil.getLocalMessage("PARTPAYMENT") + "'", "mp_id =" + id
							+ " and nvl(mp_payamount,0)<nvl(mp_total,0) and nvl(mp_payamount,0)>0");
			baseDao.updateByCondition("MOULDFEEPLEASE",
					"mp_paystatuscode='UNPAYMENT',mp_paystatus='" + BaseUtil.getLocalMessage("UNPAYMENT") + "'", "mp_id =" + id
							+ " and nvl(mp_payamount,0)=0");
			sb.append("转入成功,银行登记单号:" + "<a href=\"javascript:openUrl('jsps/fa/gs/accountRegister.jsp?formCondition=ar_idIS"
					+ j.getInt("ar_id") + "&gridCondition=ard_aridIS" + ar_id + "&whoami=AccountRegister!Bank')\">"
					+ j.getString("ar_code") + "</a>&nbsp;");
		}
		return ar_id;
	}

	@Override
	public int turnBillAP(int id, Object thisamount, String catecode, String thisdate, String caller) {
		JSONObject j = null;
		StringBuffer sb = new StringBuffer();
		if (catecode != null && !"".equals(catecode)) {
			String error = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
					String.class, catecode);
			if (error != null) {
				BaseUtil.showError("填写科目编号不存在，或者状态不等于已审核，或者不是末级科目，不允许转应付票据！");
			}
			error = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(ca_code) from Category where ca_code=? and (nvl(ca_iscash,0)=-1 OR nvl(ca_isbank,0)=-1)",
					String.class, catecode);
			if (error != null) {
				BaseUtil.showError("填写科目是银行现金科目，不允许转应付票据！");
			}
			baseDao.execute("update MOULDFEEPLEASE set mp_bankcode=? where mp_id=?", catecode, id);
			baseDao.execute(
					"update MOULDFEEPLEASE set mp_bankname=(select ca_description from category where mp_bankcode=ca_code) where mp_id=?",
					id);
			baseDao.execute("update MOULDFEEPLEASE set mp_thispaydate=to_date('" + thisdate + "','yyyy-mm-dd') where mp_id=?", id);
		}
		if (Double.parseDouble(thisamount.toString()) == 0) {
			BaseUtil.showError("已经全部转出!");
		}
		j = mouldFeePleaseDao.turnBillAP(id, thisamount);
		int bap_id = 0;
		if (j != null) {
			bap_id = j.getInt("bap_id");
			baseDao.execute("update Billap set bap_date=to_date('" + thisdate + "','yyyy-mm-dd') where bap_id=?", bap_id);
			baseDao.updateByCondition("MOULDFEEPLEASE",
					"mp_paystatuscode='PAYMENTED',mp_paystatus='" + BaseUtil.getLocalMessage("PAYMENTED") + "'", "mp_id =" + id
							+ " and nvl(mp_payamount,0)=nvl(mp_total,0)");
			baseDao.updateByCondition("MOULDFEEPLEASE",
					"mp_paystatuscode='PARTPAYMENT',mp_paystatus='" + BaseUtil.getLocalMessage("PARTPAYMENT") + "'", "mp_id =" + id
							+ " and nvl(mp_payamount,0)<nvl(mp_total,0) and nvl(mp_payamount,0)>0");
			baseDao.updateByCondition("MOULDFEEPLEASE",
					"mp_paystatuscode='UNPAYMENT',mp_paystatus='" + BaseUtil.getLocalMessage("UNPAYMENT") + "'", "mp_id =" + id
							+ " and nvl(mp_payamount,0)=0");
			sb.append("转入成功,应付票据单号:" + "<a href=\"javascript:openUrl('jsps/fa/gs/billAP.jsp?formCondition=bap_idIS" + bap_id
					+ "&whoami=BillAP')\">" + j.getString("bap_code") + "</a>&nbsp;");
		}
		return bap_id;
	}

	@Override
	public int turnBillARChange(int id, Object thisamount, String catecode, String thisdate, String caller) {
		JSONObject j = null;
		StringBuffer sb = new StringBuffer();
		if (catecode != null && !"".equals(catecode)) {
			String error = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
					String.class, catecode);
			if (error != null) {
				BaseUtil.showError("填写科目编号不存在，或者状态不等于已审核，或者不是末级科目，不允许转应收票据异动！");
			}
			error = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(ca_code) from Category where ca_code=? and (nvl(ca_iscash,0)=-1 OR nvl(ca_isbank,0)=-1)",
					String.class, catecode);
			if (error != null) {
				BaseUtil.showError("填写科目是银行现金科目，不允许转应收票据异动！");
			}
			baseDao.execute("update MOULDFEEPLEASE set mp_bankcode=? where mp_id=?", catecode, id);
			baseDao.execute(
					"update MOULDFEEPLEASE set mp_bankname=(select ca_description from category where mp_bankcode=ca_code) where mp_id=?",
					id);
			baseDao.execute("update MOULDFEEPLEASE set mp_thispaydate=to_date('" + thisdate + "','yyyy-mm-dd') where mp_id=?", id);
		}
		if (Double.parseDouble(thisamount.toString()) == 0) {
			BaseUtil.showError("已经全部转出!");
		}
		j = mouldFeePleaseDao.turnBillARChange(id, thisamount);
		int brc_id = 0;
		if (j != null) {
			brc_id = j.getInt("brc_id");
			baseDao.execute("update Billarchange set brc_date=to_date('" + thisdate + "','yyyy-mm-dd') where brc_id=?", brc_id);
			baseDao.updateByCondition("MOULDFEEPLEASE",
					"mp_paystatuscode='PAYMENTED',mp_paystatus='" + BaseUtil.getLocalMessage("PAYMENTED") + "'", "mp_id =" + id
							+ " and nvl(mp_payamount,0)=nvl(mp_total,0)");
			baseDao.updateByCondition("MOULDFEEPLEASE",
					"mp_paystatuscode='PARTPAYMENT',mp_paystatus='" + BaseUtil.getLocalMessage("PARTPAYMENT") + "'", "mp_id =" + id
							+ " and nvl(mp_payamount,0)<nvl(mp_total,0) and nvl(mp_payamount,0)>0");
			baseDao.updateByCondition("MOULDFEEPLEASE",
					"mp_paystatuscode='UNPAYMENT',mp_paystatus='" + BaseUtil.getLocalMessage("UNPAYMENT") + "'", "mp_id =" + id
							+ " and nvl(mp_payamount,0)=0");
			sb.append("转入成功,应收票据异动单号:" + "<a href=\"javascript:openUrl('jsps/fa/gs/billARChange.jsp?formCondition=brc_idIS" + brc_id
					+ "&gridCondition=brd_brcidIS" + brc_id + "')\">" + j.getString("brc_code") + "</a>&nbsp;");
		}
		return brc_id;
	}

	@Override
	public String[] printMouldFeePlease(int id, String caller, String reportName, String condition) {
		// // 执行打印前的其它逻辑
		handlerService.beforePrint(caller, new Object[] { id });
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 修改打印状态
		baseDao.updateByCondition("MOULDFEEPLEASE", "mp_printstatuscode='PRINTED',mp_printstatus='" + BaseUtil.getLocalMessage("PRINTED")
				+ "'", "mp_id=" + id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.print"), BaseUtil
				.getLocalMessage("msg.printSuccess"), caller + "|mp_id=" + id));

		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, new Object[] { id });
		return keys;
	}

	@Override
	public void endMouldFeePlease(int mp_id, String caller) {
		// 只能对状态为[已审核]的订单进行结案操作!
		Object status = baseDao.getFieldDataByCondition("MOULDFEEPLEASE", "mp_statuscode", "mp_id=" + mp_id);
		StateAssert.end_onlyAudited(status);
		handlerService.handler(caller, "end", "before", new Object[] { mp_id });
		// 执行反提交操作
		baseDao.execute("update MOULDFEEPLEASE set mp_status='已结案', mp_statuscode='FINISH' where mp_id=" + mp_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "mp_id", mp_id);
		handlerService.handler(caller, "end", "after", new Object[] { mp_id });
	}

	@Override
	public void resEndMouldFeePlease(int mp_id, String caller) {
		// 只能对状态为[已结案]的订单进行反结案操作!
		Object status = baseDao.getFieldDataByCondition("MOULDFEEPLEASE", "mp_statuscode", "mp_id=" + mp_id);
		StateAssert.resEnd_onlyAudited(status);
		handlerService.handler(caller, "resEnd", "before", new Object[] { mp_id });
		// 执行反提交操作
		baseDao.execute("update MOULDFEEPLEASE set mp_status='已审核', mp_statuscode='AUDITED' where mp_id=" + mp_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "mp_id", mp_id);
		handlerService.handler(caller, "resEnd", "after", new Object[] { mp_id });
	}
}
