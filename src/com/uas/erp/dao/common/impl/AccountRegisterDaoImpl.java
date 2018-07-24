package com.uas.erp.dao.common.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.MoneyUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.AccountRegisterDao;
import com.uas.erp.model.AccountRegisterDetailAss;
import com.uas.erp.model.Employee;

@Repository
public class AccountRegisterDaoImpl extends BaseDao implements AccountRegisterDao {
	static final String TURNACCOUNTREFISTER = "SELECT * FROM AccountRegister WHERE ar_id=?";
	static final String INSERTPAYBALANCE = "INSERT INTO PayBalance(pb_id,pb_code,pb_source,pb_sourceid,pb_vendcode,pb_date"
			+ ",pb_currency,pb_rate,pb_bank,pb_recorddate,pb_vendid,pb_vendname,pb_recorder,pb_vmstatus"
			+ ",pb_vmstatuscode,pb_printstatus,pb_printstatuscode,pb_recorderid,pb_auditstatus,pb_auditstatuscode,pb_status"
			+ ",pb_statuscode,pb_kind) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String INSERTRECBALANCE = "INSERT INTO RecBalance(rb_id,rb_code,rb_source,rb_sourcecode,rb_sourceid,rb_custcode,rb_date"
			+ ",rb_currency,rb_rate,rb_recorddate,rb_custid,rb_custname,rb_emname,rb_strikestatus"
			+ ",rb_strikestatuscode,rb_printstatus,rb_printstatuscode,rb_emid,rb_auditstatus,rb_auditstatuscode,rb_status"
			+ ",rb_statuscode,rb_kind,rb_sellerid,rb_seller,RB_SELLERCODE,rb_banknoname) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	/**
	 * get YearMonth
	 * 
	 * @param periods
	 *            "Month-A"
	 */
	@Override
	public int getPeriodsFromDate(String periods, String date) {
		StringBuffer sb = new StringBuffer("SELECT pd_detno FROM PeriodsDetail WHERE ");
		sb.append("pd_code='");
		sb.append(periods.toUpperCase());
		sb.append("' AND pd_enddate >= ");
		sb.append(DateUtil.parseDateToOracleString(Constant.YMD, date));
		sb.append(" ORDER BY pd_detno");
		SqlRowList list = queryForRowSet(sb.toString());
		if (list.next()) {
			return list.getInt("pd_detno");
		} else {
			String[] d = date.split("-");
			String year = d[0];
			String month = d[1];
			String ym = year + month;
			String start = DateUtil.getMinMonthDate(date);
			String end = DateUtil.getMaxMonthDate(date);
			execute("INSERT INTO PeriodsDetail(pd_code,pd_detno,pd_startdate,pd_enddate,pd_status,pd_year) VALUES (?,?,?,?,0,?)", periods,
					ym, DateUtil.parse(start, null), DateUtil.parse(end, null), year);
			return Integer.parseInt(ym);
		}
	}

	public Map<String, Object> getPeriodsDate(String periods, Integer date) {
		StringBuffer sb = new StringBuffer("SELECT pd_detno,pd_startdate,pd_enddate FROM PeriodsDetail WHERE ");
		sb.append("pd_code='");
		sb.append(periods.toUpperCase());
		sb.append("' AND pd_detno=");
		sb.append(date);
		SqlRowList list = queryForRowSet(sb.toString());
		if (list.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("pd_detno", list.getObject(1));
			map.put("pd_startdate", DateUtil.parseDateToOracleString(Constant.YMD, Timestamp.valueOf(list.getObject(2).toString())));
			map.put("pd_enddate", DateUtil.parseDateToOracleString(Constant.YMD, Timestamp.valueOf(list.getObject(3).toString())));
			return map;
		}
		return null;
	}

	/**
	 * @param period
	 *            PeriodsDetail.pd_code
	 */
	public Map<String, Object> getJustPeriods(String period) {
		String sql = "SELECT * FROM PeriodsDetail WHERE pd_code=? and pd_status<10 ORDER BY pd_detno";
		SqlRowList list = queryForRowSet(sql, period.toUpperCase());
		Map<String, Object> data = null;
		if (list.next()) {
			data = list.getCurrentMap();
		} else {
			data = new HashMap<String, Object>();
			data.put("PD_DETNO", 0);
			data.put("PD_STARTDATE", new Date());
			data.put("PD_ENDDATE", new Date());
		}
		return data;
	}

	public void checkDebit(int vId, double debit, double credit) {
		String sql = "SELECT round(sum(nvl(ard_credit-ard_debit,0)),2) from AccountRegisterDetail,Category where ard_catecode=ca_code and ard_void=?";
		SqlRowList list = queryForRowSet(sql, vId);
		double amount = 0;
		double leaveAmount = 0;
		double thisAmount = 0;
		if (list.next()) {
			amount = list.getDouble(1);
			if (debit == amount) {
				sql = "UPDATE AccountRegisterDetail set ard_creditcashflow=nvl(ard_credit,0),ard_debitcashflow=nvl(ard_debit,0) FROM Category where ard_catecode=ca_code and isnull(ca_cashflow,0)=0 and ard_void=?";
				execute(sql, vId);
			} else {
				sql = "SELECT ard_credit,ard_id from AccountRegisterDetail,Category where ard_catecode=ca_code and nvl(ca_cashflow,0)=0 and ard_void=?";
				list = queryForRowSet(sql, vId);
				if (list.hasNext()) {
					leaveAmount = debit;
					thisAmount = 0;
					while (list.next()) {
						thisAmount = list.getDouble(1);
						if (thisAmount < leaveAmount) {
							leaveAmount = NumberUtil.formatDouble(leaveAmount - thisAmount, 2);
							sql = "UPDATE AccountRegisterDetail set ard_creditcashflow=? where ard_id=?";
							execute(sql, thisAmount, list.getInt(2));
						} else {
							thisAmount = leaveAmount;
							sql = "UPDATE AccountRegisterDetail set ard_creditcashflow=? where ard_id=?";
							execute(sql, thisAmount, list.getInt(2));
							break;
						}
					}
				}
			}
		}
	}

	@Override
	public int getPddetno(String periods) {
		String sql = "select pd_detno from PeriodsDetail WHERE pd_status=0 ORDER BY pd_detno";
		SqlRowList sqlRowList = queryForRowSet(sql);
		if (sqlRowList.next()) {
			return sqlRowList.getInt("pd_detno");
		} else {
			return 0;
		}
	}

	/**
	 * valid AccountRegister 判断合法性
	 * 
	 * @param vId
	 *            AccountRegister.ar_id
	 */
	@Override
	public void validVoucher(int vId) {
		String sql = "SELECT vo_explanation,vo_source,vo_relativecode FROM AccountRegister WHERE ar_id=?";
		SqlRowList list = queryForRowSet(sql, vId);
		if (list.next()) {
			String explanation = null;
			String rel = list.getString("vo_relativecode");
			StringBuffer err = new StringBuffer("");
			double sDebit = 0;
			double sCredit = 0;
			double sdDebit = 0;
			double sdCredit = 0;
			double debit = 0;
			double dDebit = 0;
			double credit = 0;
			double dCredit = 0;
			double rate = 0;
			String currency = null;
			String cateCode;
			int vdid = 0;
			int count = 0;
			SqlRowList cList = null;
			StringBuffer uSql = null;
			String ass = null;
			int cFlow = 0;
			sql = "SELECT * FROM AccountRegisterDetail WHERE ard_arid=?";
			list = queryForRowSet(sql, vId);
			if (list.hasNext()) {
				while (list.next()) {
					vdid = list.getInt("ard_id");
					currency = list.getString("ard_currency");
					debit = list.getDouble("ard_debit");
					dDebit = list.getDouble("ard_doubledebit");
					credit = list.getDouble("ard_credit");
					dCredit = list.getDouble("ard_doublecredit");
					rate = list.getDouble("ard_rate");
					sDebit += debit;
					sCredit += credit;
					sdDebit += dDebit;
					sdCredit += dCredit;
					cateCode = list.getString("ard_catecode");
					// 摘要
					if (explanation == null || explanation.length() < 1) {
						explanation = list.getString("ard_explanation");
					}
					uSql = new StringBuffer();
					uSql.append("UPDATE AccountRegisterDetail SET ");
					if (debit == 0 && credit == 0 && dDebit == 0 && dCredit == 0) {
						err.append("借、贷未填。科目:<" + cateCode + ">;");
					}
					// 不可能既有借方，又有贷方
					if (debit * credit >= 0.01 || dDebit * dCredit >= 0.1) {
						err.append("不可以既有借方，又有贷方。科目:<" + cateCode + ">;");
					}
					// 科目
					cList = queryForRowSet("SELECT * FROM Category WHERE ca_code=?", cateCode);
					if (cList.next()) {
						// 有无子科目
						count = getCountByCondition("Category", "ca_subof = " + cList.getInt("ca_id"));
						if (count > 0) {
							err.append("有子科目,科目:<" + cateCode + ">;");
						}
						// 判断科目是否有辅助核算项目,如果有辅助核算而没填写，则凭证无效
						ass = cList.getString("ca_asstype");
						if (ass != null && !"".equals(ass)) {
							count = getCountByCondition("AccountRegisterDetailAss", "ars_ardid=" + vdid + " AND ars_asscode is not null");
							if (count < ass.toString().split("#").length) {
								err.append("辅助核算未填写完善。科目:<" + cateCode + ">;");
							}
						}
						// 本币金额 ?= 外币金额 * 汇率
						if (cList.getInt("ca_currencytype") != 0 && explanation != "汇兑损益") {
							if ((currency != null || dDebit + dCredit > 0) && explanation != "调汇差") {
								if (dDebit * rate - debit > 0.01 || dCredit * rate - credit > 0.01 || "".equals(currency)) {
									err.append("外币填写不正确。科目:<" + cateCode + ">;");
								}
							}
						}
						// 银行现金类科目是否在登记中
						if ((cList.getInt("ca_isbank") != 0 || cList.getInt("ca_iscash") != 0) && explanation != "汇兑损益") {
							count = getCountByCondition("AccountRegister", "ar_code='" + rel + "'");
							if (count == 0 && rel == "合并制作") {
								err.append("银行现金科目要在登记中。科目:<" + cateCode + ">;");
							}
						}
					} else {
						err.append("科目不存在。科目:<" + cateCode + ">;");
					}
					uSql.append(",ard_debit=");
					uSql.append(NumberUtil.formatDouble(debit, 2));
					uSql.append(",ard_credit=");
					uSql.append(NumberUtil.formatDouble(credit, 2));
					uSql.append(" WHERE ard_id=" + vdid);
					execute(uSql.toString());
				}
				if (cFlow != 0) {
					// 借方对应科目
					checkDebit(vId, debit, credit);
					// 贷方对应科目
					checkCredit(vId, debit, credit);
				}
			} else {
				err.append("无明细行;");
			}
			if (Math.abs(sDebit - sCredit) >= 0.005 || Math.abs(sdDebit - sdCredit) >= 0.005) {
				err.append("不平衡;");
			}
			String errStr = err.toString();
			errStr = errStr.length() > 100 ? errStr.substring(0, 100) + "..." : errStr;
			sql = "UPDATE AccountRegister SET ar_errstring=?,ar_total=?,ar_totalupper=?,ar_explanation=? WHERE ar_id=?";
			execute(sql, errStr, sDebit, MoneyUtil.toChinese(sDebit), explanation, vId);
		}
	}

	public void checkCredit(int vId, double debit, double credit) {
		String sql = "SELECT round(sum(nvl(ard_debit-ard_credit,0)),2) from AccountRegisterDetail,Category where ard_catecode=ca_code and vd_void=?";
		SqlRowList list = queryForRowSet(sql, vId);
		double amount = 0;
		double leaveAmount = 0;
		double thisAmount = 0;
		list = queryForRowSet(sql, vId);
		if (list.next()) {
			amount = list.getDouble(1);
			if (credit == amount) {
				sql = "UPDATE AccountRegisterDetail set vd_debitcashflow=nvl(vd_debit,0),vd_creditcashflow=nvl(vd_credit,0) FROM Category where vd_catecode=ca_code and isnull(ca_cashflow,0)=0 and vd_void=?";
				execute(sql, vId);
			} else {
				sql = "SELECT vd_debit,vd_id from VoucherDetail,Category where vd_catecode=ca_code and nvl(ca_cashflow,0)=0 and vd_void=?";
				list = queryForRowSet(sql, vId);
				if (list.hasNext()) {
					leaveAmount = credit;
					thisAmount = 0;
					while (list.next()) {
						thisAmount = list.getDouble(1);
						if (thisAmount < leaveAmount) {
							leaveAmount = NumberUtil.formatDouble(leaveAmount - thisAmount, 2);
							sql = "UPDATE VoucherDetail set vd_debitcashflow=? where vd_id=?";
							execute(sql, thisAmount, list.getInt(2));
						} else {
							thisAmount = leaveAmount;
							sql = "UPDATE VoucherDetail set vd_debitcashflow=? where vd_id=?";
							execute(sql, thisAmount, list.getInt(2));
							break;
						}
					}
				}
			}
		}
	}

	@Override
	public int turnPayBalance(int id) {
		SqlRowList rs = queryForRowSet(TURNACCOUNTREFISTER, new Object[] { id });
		int pbid = 0;
		if (rs.next()) {
			Employee employee = SystemSession.getUser();
			pbid = getSeqId("PAYBALANCE_SEQ");
			String code = sGetMaxNumber("PayBalance", 2);
			Timestamp time = Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS));
			String vendcode = rs.getString("ar_vendcode");
			if (vendcode != null && vendcode != "") {
				Object vendid = getFieldDataByCondition("Vendor", "ve_id", "ve_code='" + vendcode + "'");
				execute(INSERTPAYBALANCE,
						new Object[] { pbid, code, rs.getObject("ar_code"), id, vendcode, time, rs.getObject("ar_currency"),
								rs.getObject("ar_rate"), rs.getObject("ar_accountname"), time, vendid, rs.getObject("ar_vendname"),
								employee.getEm_name(), BaseUtil.getLocalMessage("UNSTRIKE"), "UNSTRIKE",
								BaseUtil.getLocalMessage("UNPRINT"), "UNPRINT", employee.getEm_id(), BaseUtil.getLocalMessage("ENTERING"),
								"ENTERING", BaseUtil.getLocalMessage("UNPOST"), "UNPOST", "付款单" });
			} else {
				BaseUtil.showError("供应商不能为空!");
			}
		}
		return pbid;
	}

	@Override
	public int turnRecBalance(int id) {
		SqlRowList rs = queryForRowSet(TURNACCOUNTREFISTER, new Object[] { id });
		int rb_id = 0;
		if (rs.next()) {
			Employee employee = SystemSession.getUser();
			rb_id = getSeqId("RECBALANCE_SEQ");
			String code = sGetMaxNumber("RecBalance", 2);
			Timestamp time = Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS));
			String custcode = rs.getString("ar_custcode");
			if (custcode != null && custcode != "") {
				Object[] cust = getFieldsDataByCondition("customer", new String[] { "cu_id", "cu_sellerid", "cu_sellername", "cu_name",
						"cu_sellercode" }, "cu_code='" + custcode + "'");
				execute(INSERTRECBALANCE,
						new Object[] { rb_id, code, "Bank", rs.getObject("ar_code"), id, custcode, time, rs.getObject("ar_currency"),
								rs.getObject("ar_rate"), time, cust[0], rs.getObject("ar_custname"), employee.getEm_name(),
								BaseUtil.getLocalMessage("UNSTRIKE"), "UNSTRIKE", BaseUtil.getLocalMessage("UNPRINT"), "UNPRINT",
								employee.getEm_id(), BaseUtil.getLocalMessage("ENTERING"), "ENTERING", BaseUtil.getLocalMessage("UNPOST"),
								"UNPOST", "收款单", cust[1], cust[2], cust[3], rs.getObject("ar_accountname") });
			} else {
				BaseUtil.showError("客户不能为空!");
			}
		}
		return rb_id;
	}

	@Override
	public int turnRecBalanceIMRE(int id, String custcode, String thisamount) {
		SqlRowList rs = queryForRowSet(TURNACCOUNTREFISTER, new Object[] { id });
		int rb_id = 0;
		if (rs.next()) {
			if (rs.getGeneralDouble("ar_deposit") - rs.getGeneralDouble("ar_recamount") < Double.parseDouble(thisamount)) {
				BaseUtil.showError("超剩余金额转冲应收款！收入金额[" + rs.getGeneralDouble("ar_deposit") + "]已认款金额[" + rs.getGeneralDouble("ar_recamount")
						+ "]");
			}
			rb_id = getSeqId("RECBALANCE_SEQ");
			String code = sGetMaxNumber("RecBalance!IMRE", 2);
			Employee employee = SystemSession.getUser();
			Timestamp time = Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS));
			Object[] cust = getFieldsDataByCondition("customer", new String[] { "cu_id", "cu_sellerid", "cu_sellername", "cu_name",
					"cu_sellercode" }, "cu_code='" + custcode + "'");
			execute(INSERTRECBALANCE,
					new Object[] { rb_id, code, "Bank", rs.getObject("ar_code"), id, custcode, time, rs.getObject("ar_accountcurrency"),
							rs.getObject("ar_accountrate"), time, cust[0], cust[3], employee.getEm_name(),
							BaseUtil.getLocalMessage("UNSTRIKE"), "UNSTRIKE", BaseUtil.getLocalMessage("UNPRINT"), "UNPRINT",
							employee.getEm_id(), BaseUtil.getLocalMessage("ENTERING"), "ENTERING", BaseUtil.getLocalMessage("UNPOST"),
							"UNPOST", "冲应收款", cust[1], cust[2], cust[4], rs.getObject("ar_accountname") });
			SqlRowList rs1 = queryForRowSet(
					"SELECT ca_name,ca_class,ard_catecode FROM (SELECT ca_name,ca_class,ard_catecode FROM accountregisterdetail left join category on ard_catecode=ca_code WHERE ARD_arID=? AND NVL(ard_catecode,' ')<>' ' ORDER BY ARD_DETNO) WHERE ROWNUM<2",
					id);
			if (rs1.next()) {
				execute("update recbalance set rb_catecode=?,rb_catename=?,RB_CATETYPE=?,RB_CATECURRENCY=? where rb_id=?",
						rs1.getObject("ard_catecode"), rs1.getObject("ca_name"), rs1.getObject("ca_class"),
						rs.getObject("ar_accountcurrency"), rb_id);
			}

			execute("update recbalance set rb_amount=?,rb_cmamount=?,rb_cmcurrency=rb_currency,rb_cmrate=1 where rb_id=?", thisamount,
					thisamount, rb_id);
			execute("update recbalance set RB_SOURCECODE=RB_SOURCE WHERE RB_SOURCE IS NOT NULL AND RB_SOURCECODE IS NULL and nvl(RB_SOURCEID,0)<>0 and rb_kind ='冲应收款' and RB_SOURCEID=?",
					id);
			execute("update recbalance set RB_SOURCE='Bank' WHERE RB_SOURCECODE=RB_SOURCE and rb_kind ='冲应收款' and RB_SOURCEID=?", id);
			execute("UPDATE ACCOUNTREGISTER SET ar_recamount=nvl(ar_recamount,0)+" + thisamount + " WHERE AR_ID=" + id);
		}
		return rb_id;
	}

	public double getTurnAR(Object fp_id) {
		Object fp = getFieldDataByCondition("FeePlease", "fp_v13", "fp_id=" + fp_id + " and fp_kind='总务申请单'");
		SqlRowList rs = queryForRowSet(
				"select ar_accountcurrency,to_char(ar_date,'yyyymm') ar_date,ar_accountrate,ar_payment,ar_deposit from AccountRegister where ar_sourcetype='总务申请单' and ar_sourceid=?",
				fp_id);
		double amount = 0.0;
		if (fp != null) {
			while (rs.next()) {
				if (!rs.getGeneralString("ar_accountcurrency").equals(fp)) {
					Double fprate = getFieldValue("CurrencysMonth", "cm_crrate",
							"cm_crname='" + fp + "' and cm_yearmonth=" + rs.getObject("ar_date"), Double.class);
					if (fprate != null && fprate != 0) {
						amount = amount + (rs.getGeneralDouble("ar_payment") - rs.getGeneralDouble("ar_deposit"))
								* rs.getGeneralDouble("ar_accountrate") / fprate;
					}
				} else {
					amount = amount + (rs.getGeneralDouble("ar_payment") - rs.getGeneralDouble("ar_deposit"));
				}
			}
		}
		return amount;
	}

	@Override
	public List<AccountRegisterDetailAss> getAssByAccountRegisterId(int ar_id) {
		return query(
				"select * from AccountRegisterDetailAss where exists (select 1 from AccountRegisterDetail where ars_ardid=ard_id and ard_arid=?) order by ars_detno",
				AccountRegisterDetailAss.class, ar_id);
	}

}
