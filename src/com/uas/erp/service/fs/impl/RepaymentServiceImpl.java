package com.uas.erp.service.fs.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Master;
import com.uas.erp.service.fs.RepaymentService;

@Service
public class RepaymentServiceImpl implements RepaymentService {
	@Autowired
	private BaseDao baseDao;

	static final String ACCOUNTREGISTERBILL = "INSERT INTO ACCOUNTREGISTERBILL(arb_id,arb_arid,arb_sourceid,arb_sourcetype,arb_sourcecode,arb_amount,arb_currency,arb_aacode)"
			+ " values (ACCOUNTREGISTERBILL_SEQ.NEXTVAL,?,?,?,?,?,?,?)";
	static final String ACCOUNTREGISTER = "INSERT INTO ACCOUNTREGISTER(ar_id,ar_code,ar_date,ar_type,ar_recorddate,ar_statuscode,ar_payment,ar_deposit,"
			+ "ar_sourceid,ar_source,ar_sourcetype,ar_recordman,ar_memo,ar_status,ar_fscucode,ar_fscuname,ar_truster,ar_aacode) "
			+ "values (?,?,sysdate,'保理收款',sysdate,'ENTERING',0,?,?,?,?,?,?,?,?,?,?,?)";
	static final String REIMBURSEMENTLOG = "INSERT INTO REIMBURSEMENTLOG(RL_ID,RL_CODE,RL_KIND,RL_BACKCODE,RL_BACKDATE,RL_CURRENCY,RL_AMOUNT,RL_TRUSTER,"
			+ "RL_APPLYDATE,RL_ARCODE,rl_aacode,RL_ARID) values (REIMBURSEMENTLOG_SEQ.nextval,?,?,?,sysdate,?,?,?,?,?,?,?)";
	static final String REIMBURSEMENTPLAN = "insert into reimbursementplan(rp_id,rp_code,rp_aaid,rp_aacode,rp_backdate,rp_currency,rp_principal,rp_interest,rp_iscloseoff,rp_iscarryout,rp_truster,rp_applydate) "
			+ " values (REIMBURSEMENTPLAN_SEQ.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?)";

	@Override
	@Transactional
	public void ConfirmRepayment(String aacode, String aakind, Double thisamount, Double backcustamount, String backdate) {
		SqlRowList rs = baseDao.queryForRowSet("select * from FS_ACCOUNTWITHOVER_VIEW where aa_code=? and aa_kind=?", aacode, aakind);
		if (rs.next()) {
			double thispayamount = thisamount;
			double lx = rs.getGeneralDouble("aa_interestpay"); // 未转利息单的利息
			double yflx = rs.getGeneralDouble("aa_interest"); // 应付利息（包含利息单未转银行登记部分）
			double overlx = rs.getGeneralDouble("aa_overinterest");// 逾期利息
			String cucode = rs.getGeneralString("aa_custcode"); // 保理客户编号
			String cuname = rs.getGeneralString("aa_custname"); // 保理客户名称
			String truster = rs.getGeneralString("aa_truster"); // 经办人
			String mfcucode = rs.getGeneralString("aa_mfcustcode"); // 保理买方客户编号
			String cacode = rs.getGeneralString("aa_cacode"); // 额度申请单编号
			double dueamount = rs.getGeneralDouble("aa_dueamount")*(-1);
			// 逾期利息
			if (thispayamount > 0 && overlx > 0) {
				if (thispayamount < overlx) {
					BaseUtil.showError("不允许部分归还逾期利息！");
				}
				int ar_id = baseDao.getSeqId("ACCOUNTREGISTER_SEQ");
				String ar_code = baseDao.sGetMaxNumber("AccountRegister", 2);
				int oin_id = baseDao.getSeqId("FSOVERDUEINTEREST_SEQ");
				String oin_code = baseDao.sGetMaxNumber("FsOverdueInterest", 2);
				// 产生逾期利息单
				baseDao.execute("insert into FSOVERDUEINTEREST(oin_id,oin_code,oin_aacode,oin_currency,oin_interest,oin_overdays,oin_truster,oin_applydate,oin_date)"
						+ "select "
						+ oin_id
						+ ",'"
						+ oin_code
						+ "',OD_AACODE,OD_CURRENCY,round("
						+ overlx
						+ ",2),OD_ODDAYS,OD_TRUSTER,OD_APPLYDATE,sysdate from Fsoverdue where OD_AACODE='" + aacode + "'");
				baseDao.execute("UPDATE FSOVERDUE SET OD_ODINTEREST=0 WHERE OD_AACODE='" + aacode + "'");
				// 产生银行登记
				baseDao.execute(ACCOUNTREGISTERBILL, new Object[] { ar_id, oin_id, "逾期利息单", oin_code, overlx, rs.getObject("oa_currency"),
						aacode });
				baseDao.execute(ACCOUNTREGISTER, new Object[] { ar_id, ar_code, overlx, oin_id, oin_code, "逾期利息单",
						SystemSession.getUser().getEm_name(), "出账单[" + aacode + "]逾期利息还款", BaseUtil.getLocalMessage("ENTERING"), cucode,
						cuname, truster, aacode });
				baseDao.execute(REIMBURSEMENTLOG,
						new Object[] { baseDao.sGetMaxNumber("ReimbursementLog", 2), "逾期利息单", oin_code, rs.getObject("oa_currency"),
								overlx, truster, rs.getObject("aa_applydate"), ar_code, aacode, ar_id });
				thispayamount = thispayamount - overlx;
			}
			if (thispayamount > 0) {
				// 利息单
				if (yflx > 0) {
					int ar_id = baseDao.getSeqId("ACCOUNTREGISTER_SEQ");
					String ar_code = baseDao.sGetMaxNumber("AccountRegister", 2);
					SqlRowList in = baseDao
							.queryForRowSet(
									"select nvl(in_interest,0)-nvl(in_yamount,0) amount,in_id,in_code,in_currency,in_aacode,in_interest from FSINTEREST where IN_AACODE=? and nvl(IN_INTEREST,0)>nvl(in_yamount,0) order by in_date ",
									aacode);
					while (in.next()) {
						if (thispayamount > 0) {
							double amount = in.getGeneralDouble("amount");
							int inid = in.getInt("in_id");
							String incode = in.getGeneralString("in_code");
							if (thispayamount <= amount) {
								baseDao.execute(ACCOUNTREGISTERBILL,
										new Object[] { ar_id, inid, "利息单", incode, thispayamount, in.getObject("in_currency"), aacode });
								baseDao.execute("update FSINTEREST set IN_YAMOUNT=round(nvl(IN_YAMOUNT,0) + " + thispayamount
										+ ",2) where in_id=" + inid);
								baseDao.execute("update FSINTEREST set IN_ISCLOSEOFF='是' where nvl(IN_YAMOUNT,0)=nvl(IN_INTEREST,0) and in_id="
										+ inid);
								baseDao.execute("UPDATE ACCOUNTAPPLY SET AA_INTEREST=round(NVL(AA_INTEREST,0)-" + thispayamount
										+ ",2) WHERE AA_code='" + aacode + "'");
								thispayamount = thispayamount - amount;
								continue;
							} else {
								baseDao.execute(ACCOUNTREGISTERBILL,
										new Object[] { ar_id, inid, "利息单", incode, amount, in.getObject("in_currency"), aacode });
								baseDao.execute("UPDATE ACCOUNTAPPLY SET AA_INTEREST=round(NVL(AA_INTEREST,0)-" + amount
										+ ",2) WHERE AA_code='" + aacode + "'");
								baseDao.execute("update FSINTEREST set IN_YAMOUNT=nvl(IN_INTEREST,0),IN_ISCLOSEOFF='是' where in_id=" + inid);
							}
							thispayamount = thispayamount - amount;
						}
					}
					if (thispayamount > 0 && lx > 0) {
						if (thispayamount < lx) {
							BaseUtil.showError("未产生利息单，不允许提前归还利息！");
						}
						// 产生利息单
						int in_id = baseDao.getSeqId("FSINTEREST_SEQ");
						String in_code = baseDao.sGetMaxNumber("FsInterest", 2);
						baseDao.execute("insert into fsinterest(in_id,in_code,in_aacode,in_currency,in_interest,in_iscloseoff,in_truster,in_applydate,in_yamount,in_remark,in_date)"
								+ "select "
								+ in_id
								+ ",'"
								+ in_code
								+ "',aa_code,aa_currency,round("
								+ lx
								+ ",2),'是', aa_truster,aa_applydate,"
								+ lx
								+ ",aa_remark,sysdate from accountapply where aa_code='"
								+ aacode + "'");
						baseDao.execute("UPDATE accountapply SET aa_interestpay=0 WHERE aa_code='" + aacode + "'");
						baseDao.execute(ACCOUNTREGISTERBILL, new Object[] { ar_id, in_id, "利息单", in_code, lx, rs.getObject("aa_currency"),
								aacode });
						baseDao.execute("UPDATE accountapply SET aa_interest=round(NVL(aa_interest,0)-" + lx + ",2) WHERE AA_code='"
								+ aacode + "'");
						// 更新以后月份的还款计划为不执行
						baseDao.execute("update reimbursementplan set rp_iscarryout='不执行' where rp_aacode='" + aacode
								+ "' and rp_iscarryout='未执行' and trunc(rp_backdate)>=trunc(sysdate)");
						thispayamount = thispayamount - lx;
					}
					SqlRowList arb = baseDao.queryForRowSet("select * from ACCOUNTREGISTERBILL where arb_arid=" + ar_id);
					if (arb.hasNext()) {
						// 所有利息单产生一张银行登记
						boolean bool = baseDao.execute(ACCOUNTREGISTER, new Object[] { ar_id, ar_code, 0, 0, null, "利息单",
								SystemSession.getUser().getEm_name(), "出账单[" + aacode + "]利息还款", BaseUtil.getLocalMessage("ENTERING"),
								cucode, cuname, truster, aacode });
						if (bool) {
							baseDao.execute("update ACCOUNTREGISTER set ar_deposit=(select sum(arb_amount) from accountregisterbill where arb_arid=ar_id) where ar_id="
									+ ar_id);
						}
						while (arb.next()) {
							baseDao.execute(
									REIMBURSEMENTLOG,
									new Object[] { baseDao.sGetMaxNumber("ReimbursementLog", 2), arb.getObject("arb_sourcetype"),
											arb.getObject("arb_sourcecode"), arb.getObject("arb_currency"), arb.getObject("arb_amount"),
											truster, rs.getObject("aa_applydate"), ar_code, aacode, ar_id });
						}
					}
				}
				if (thispayamount > 0 && "逾期单".equals(aakind)) {
					// 产生逾期还款单
					int ra_id = baseDao.getSeqId("REIMBURSEMENTAPPLY_SEQ");
					String ra_code = baseDao.sGetMaxNumber("ReimbursementApply", 2);
					int ar_id = baseDao.getSeqId("ACCOUNTREGISTER_SEQ");
					String ar_code = baseDao.sGetMaxNumber("AccountRegister", 2);
					baseDao.execute("insert into REIMBURSEMENTAPPLY(RA_ID,RA_CODE,ra_odcode,RA_CURRENCY,RA_BACKPRINCIPAL,RA_ISCLOSEOFF,RA_TRUSTER,RA_APPLYDATE,RA_RECORDER,RA_INDATE,RA_KIND,RA_STATUSCODE,ra_status,RA_REMARK,ra_custcode,ra_custname) "
							+ "select "
							+ ra_id
							+ ",'"
							+ ra_code
							+ "',od_code,od_currency,round("
							+ thispayamount
							+ ",2),'是',od_truster,od_applydate,'"
							+ SystemSession.getUser().getEm_name()
							+ "',sysdate,'OVERDUE','AUDITED','"
							+ BaseUtil.getLocalMessage("AUDITED")
							+ "',od_remark,'"
							+ cucode
							+ "', '"
							+ cuname + "' from fsoverdue where OD_AACODE='" + aacode + "'");
					// 逾期还款单转银行登记
					baseDao.execute(ACCOUNTREGISTERBILL,
							new Object[] { ar_id, ra_id, "逾期还款单", ra_code, thispayamount, rs.getObject("aa_currency"), aacode });
					baseDao.execute(ACCOUNTREGISTER, new Object[] { ar_id, ar_code, thispayamount, ra_id, ra_code, "逾期还款单",
							SystemSession.getUser().getEm_name(), "出账单[" + aacode + "]逾期本金还款", BaseUtil.getLocalMessage("ENTERING"),
							cucode, cuname, truster, aacode });
					baseDao.execute(REIMBURSEMENTLOG,
							new Object[] { baseDao.sGetMaxNumber("ReimbursementLog", 2), "逾期还款单", ra_code, rs.getObject("aa_currency"),
									thispayamount, truster, rs.getObject("aa_applydate"), ar_code, aacode, ar_id });
					// 更新逾期单已还金额
					baseDao.execute("update fsoverdue SET OD_BACKAMOUNT=nvl(OD_BACKAMOUNT,0) + round(" + thispayamount
							+ ",2) WHERE OD_AACODE='" + aacode + "'");
					baseDao.execute("update fsoverdue SET od_iscloseoff='是' WHERE OD_AACODE='" + aacode
							+ "' and nvl(OD_BACKAMOUNT,0)=nvl(OD_ODAMOUNT,0)");
				}
				if (thispayamount > 0 && "出账单".equals(aakind)) {
					// 利息计划还完之后还有金额，产生一张还款申请单
					if (thispayamount > 0) {
						int ra_id = baseDao.getSeqId("REIMBURSEMENTAPPLY_SEQ");
						String ra_code = baseDao.sGetMaxNumber("ReimbursementApply", 2);
						int ar_id = baseDao.getSeqId("ACCOUNTREGISTER_SEQ");
						String ar_code = baseDao.sGetMaxNumber("AccountRegister", 2);
						baseDao.execute("insert into REIMBURSEMENTAPPLY(RA_ID,RA_CODE,RA_AACODE,RA_CURRENCY,RA_BACKPRINCIPAL,RA_ISCLOSEOFF,RA_TRUSTER,RA_APPLYDATE,RA_RECORDER,RA_INDATE,RA_KIND,RA_STATUSCODE,ra_status,RA_REMARK,ra_custcode,ra_custname) "
								+ "select "
								+ ra_id
								+ ",'"
								+ ra_code
								+ "',aa_code,aa_currency,round("
								+ thispayamount
								+ ",2),'是',aa_truster,aa_applydate,'"
								+ SystemSession.getUser().getEm_name()
								+ "',sysdate,'NORMAL','AUDITED','"
								+ BaseUtil.getLocalMessage("AUDITED")
								+ "',AA_REMARK,aa_custcode,aa_custname from AccountApply where aa_code='" + aacode + "'");
						baseDao.execute(ACCOUNTREGISTERBILL,
								new Object[] { ar_id, ra_id, "还款单", ra_code, thispayamount, rs.getObject("aa_currency"), aacode });
						baseDao.execute(ACCOUNTREGISTER, new Object[] { ar_id, ar_code, thispayamount, ra_id, ra_code, "还款单",
								SystemSession.getUser().getEm_name(), "出账单[" + aacode + "]本金还款", BaseUtil.getLocalMessage("ENTERING"),
								cucode, cuname, truster, aacode });
						baseDao.execute(REIMBURSEMENTLOG,
								new Object[] { baseDao.sGetMaxNumber("ReimbursementLog", 2), "还款单", ra_code, rs.getObject("aa_currency"),
										thispayamount, truster, rs.getObject("aa_applydate"), ar_code, aacode, ar_id });
						// 更新出账单剩余金额
						baseDao.execute("update AccountApply SET aa_leftamount=round(nvl(aa_leftamount,0)-" + thispayamount
								+ ",2) WHERE aa_CODE='" + aacode + "'");
						// 根据未还本金更新还款计划
						try {
							SqlRowList aa = baseDao
									.queryForRowSet(
											"select aa_leftamount, round(nvl(aa_leftamount,0)*nvl(aa_interestrate,0)/100/365,2) aa_everyday from AccountApply where aa_code=? ",
											aacode);
							if (aa.next()) {
								if (aa.getGeneralDouble("aa_leftamount") > 0) {
									// 插入新的还款计划
									SqlRowList rp = baseDao
											.queryForRowSet(
													"select rp_code,rp_aaid,rp_aacode,rp_backdate,rp_currency,rp_principal,rp_interest,rp_iscloseoff,rp_iscarryout,rp_truster,rp_applydate,to_char(rp_backdate,'yyyy-mm-dd') rp_date from reimbursementplan where rp_aacode=? and rp_iscarryout='不执行' order by rp_backdate ",
													aacode);
									String date = DateUtil.currentDateString(null);
									while (rp.next()) {
										double amount = 0;
										if (rp.getGeneralDouble("rp_principal") > 0) {
											amount = aa.getGeneralDouble("aa_leftamount");
										}
										int days = DateUtil.countDates(date, rp.getString("rp_date"));
										baseDao.execute(
												REIMBURSEMENTPLAN,
												new Object[] { "_" + rp.getString("rp_code"), rp.getObject("rp_aaid"),
														rp.getObject("rp_aacode"), rp.getObject("rp_backdate"),
														rp.getObject("rp_currency"), amount, days * aa.getGeneralDouble("aa_everyday"),
														rp.getObject("rp_iscloseoff"), rp.getObject("rp_iscarryout"),
														rp.getObject("rp_truster"), rp.getObject("rp_applydate") });
										date = rp.getGeneralString("rp_date");
									}
								} else {
									Object[] cust = baseDao.getFieldsDataByCondition("CustomerQuota inner join FSMFCUSTINFO on cq_id = MF_CQID", new String[] {
											"mf_sourcecode", "mf_custname", "cq_finid" }, "cq_code = '" + cacode + "' and mf_custcode = '" + mfcucode + "'");
									if (cust != null) {
										if (null!=cust[2]) {
											Object uu = baseDao.getFieldDataByCondition("CustomerInfor", "cu_enuu", "cu_code = '"+cucode+"' and nvl(cu_b2benable,0)<>0");
											if (null!=uu) {
												Master master = SystemSession.getUser().getCurrentMaster();
												Map<String, String> params = new HashMap<String, String>();
												Map<String, Object> sellerQuota = new HashMap<String, Object>();
												sellerQuota.put("faid", cust[2]);
												sellerQuota.put("cq_custname", cust[1]);
												sellerQuota.put("cq_dueamount", dueamount);
												sellerQuota.put("cq_uu", uu);
												params.put("sellerQuota", FlexJsonUtil.toJson(sellerQuota));
								
												Response response = HttpUtil.sendPostRequest(master.getMa_finwebsite() + "/sellerquota/updateQuota?access_id=" + master.getMa_uu(), params,
														true, master.getMa_accesssecret());
												if (response.getStatusCode() != HttpStatus.OK.value()) {
													throw new Exception("连接平台失败," + response.getStatusCode());
												}
											}
										}else {
											SqlRowList rs1 = baseDao.queryForRowSet(
													"select cu_webserver,cu_whichsystem,cu_secret FROM CustomerInfor where cu_code = ? and nvl(cu_issys,0)<>0", cucode);
											if (rs1.next()) {
												String web = rs1.getGeneralString("cu_webserver");
												String whichsys = rs1.getGeneralString("cu_whichsystem");
												String secret = rs1.getGeneralString("cu_secret");
												if (!StringUtil.hasText(web) || !StringUtil.hasText(whichsys)) {
													BaseUtil.showError("客户资料的网址或账套不明，无法正常取数！");
												}

												if (!StringUtil.hasText(secret)) {
													BaseUtil.showError("密钥为空，不能审批系统客户额度申请！");
												}
												Map<String, String> params = new HashMap<String, String>();
												params.put("cqcode", cacode);
												params.put("custcode", String.valueOf(cust[0]));
												params.put("custname", String.valueOf(cust[1]));
												params.put("amount", String.valueOf(dueamount));
												Response response = HttpUtil.sendPostRequest(web + "/openapi/factoring/AccountApply.action?master=" + whichsys, params,
														true, secret);
												if (response.getStatusCode() != HttpStatus.OK.value()) {
													throw new Exception("连接客户账套失败," + response.getStatusCode());
												}
											}
										}
									}
									// 更新以后月份的还款计划为不执行
									baseDao.execute("update reimbursementplan set rp_iscarryout='不执行' where rp_aacode='" + aacode
											+ "' and rp_iscarryout='未执行' and trunc(rp_backdate)>=trunc(sysdate)");
									baseDao.execute("update AccountApply set aa_iscloseoff='是' where aa_code='" + aacode + "'");
								}
							}
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
							BaseUtil.showError("错误：" + e.getMessage());
						}
					}
				}
			}
		}
	}
}
