package com.uas.erp.dao.common.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.SendNotifyDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;

@Repository
public class SendNotifyDaoImpl extends BaseDao implements SendNotifyDao {
	static final String TURNPRODIO = "SELECT sn_code,sn_warehousecode,sn_time,sn_currency,sn_rate,sn_custcode,sn_custname,sn_sellername"
			+ ",sn_payment,sn_cop,sn_departmentcode,sn_departmentname,sn_custid,sn_arcustcode,sn_arcustname,sn_sellercode FROM sendnotify WHERE sn_id=?";
	static final String INSERPRODIO = "INSERT INTO prodinout(pi_id,pi_inoutno,pi_class,pi_relativeplace,pi_whcode,pi_date,pi_currency,pi_rate,pi_cardcode"
			+ ",pi_title,pi_belongs,pi_payment,pi_cop,pi_departmentcode,pi_departmentname,pi_cardid,pi_code,pi_recorddate,pi_invostatus,pi_operatorcode,pi_recordman"
			+ ",pi_invostatuscode,pi_statuscode,pi_status,pi_updatedate,pi_updateman,pi_arcode,pi_arname,pi_whname,pi_sellercode,pi_sellername,pi_belongs,pi_printstatuscode,pi_printstatus) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String INSERTBASEPRODIO = "INSERT INTO prodinout(pi_id,pi_inoutno,pi_class,pi_invostatus,pi_invostatuscode"
			+ ",pi_operatorcode,pi_recordman,pi_recorddate,pi_statuscode,pi_status,pi_updatedate,pi_updateman) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String INSERTPRODIOWITHCUST = "INSERT INTO prodinout(pi_id,pi_inoutno,pi_class,pi_invostatus,pi_invostatuscode,pi_operatorcode,pi_recordman,pi_recorddate"
			+ ",pi_cardid, pi_cardcode,pi_title,pi_sellercode,pi_belongs,pi_payment,pi_status,pi_statuscode,pi_updatedate,pi_updateman,pi_currency,pi_rate,pi_cusaddresssid"
			+ ", pi_paymentcode, pi_sellername,pi_printstatuscode,pi_printstatus,pi_address) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String TURNPRODINDETAIL = "SELECT snd_code,snd_pdno,snd_description,snd_batchcode,snd_prodcode,snd_outqty,snd_assqty,snd_sendprice"
			+ ",snd_taxrate,snd_discount,snd_sdid,snd_custprodcode,snd_pocode,snd_netprice,snd_readyqty,snd_id"
			+ ",snd_yqty,snd_ordercode,snd_orderdetno,snd_warehouse,snd_warehousecode,snd_remark FROM sendnotifydetail WHERE snd_snid=?";
	static final String TURNBASEPRODINDETAIL = "SELECT snd_code,snd_pdno,snd_description,snd_batchcode,snd_prodcode,snd_outqty,snd_assqty,snd_sendprice"
			+ ",snd_taxrate,snd_discount,snd_sdid,snd_custprodcode,snd_pocode,snd_netprice,snd_readyqty,snd_id,snd_bonded"
			+ ",sn_id,sn_code,snd_ordercode,snd_orderdetno,snd_remark,snd_batchid,sn_custcode,sn_arcustcode,sn_currency,"
			+ "sn_paymentscode FROM sendnotifydetail left join sendnotify on snd_snid=sn_id WHERE snd_id=?";
	static final String INSERPRODINDETAIL = "INSERT INTO prodiodetail(pd_id,pd_inoutno,pd_piclass,pd_pdno,pd_ordercode,pd_orderdetno,pd_description,pd_batchcode,pd_prodcode"
			+ ",pd_outqty,pd_notoutqty,pd_sendprice,pd_taxrate,pd_discount,pd_sdid,pd_custprodcode,pd_pocode,pd_taxprice,pd_beipinoutqty,pd_orderid,pd_piid,pd_status,pd_auditstatus"
			+ ",pd_snid,pd_netprice,pd_taxtotal,pd_nettotal,pd_whcode,pd_whname,pd_prodid,pd_remark,pd_batchid,pd_bonded) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String GETPIBYSOURCE = "select pi_code from prodinout where pi_relativeplace=(select sn_code from sendnotify left join sendnotifydetail on sn_id=snd_snid where snd_id=?)";
	static final String INSERT_PRODIOSPECIAL = "INSERT INTO prodinout(pi_id,pi_inoutno,pi_class,pi_invostatus,pi_invostatuscode,"
			+ "pi_operatorcode,pi_recordman,pi_recorddate,pi_cardid, pi_cardcode,pi_title,pi_sellercode,pi_sellername,pi_belongs,pi_payment,pi_paymentcode,"
			+ "pi_status,pi_statuscode,pi_updatedate,pi_updateman,pi_currency,pi_rate,pi_whcode,pi_whname,pi_transport,pi_address,"
			+ "pi_departmentcode,pi_departmentname,pi_arname,pi_arcode,pi_receivecode,pi_receivename,pi_sourcecode,"
			+ "pi_cusaddresssid,pi_remark,pi_custcode2,pi_custname2,pi_invoiceremark,pi_packingremark,pi_remark2) VALUES "
			+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String INSERT_PRODIOSPECIALys = "INSERT INTO prodinout(pi_id,pi_inoutno,pi_class,pi_invostatus,pi_invostatuscode,"// 5
			+ "pi_operatorcode,pi_recordman,pi_recorddate,pi_cardid, pi_cardcode,pi_title,pi_sellercode,pi_sellername,pi_belongs,pi_payment,pi_paymentcode,"// 16
			+ "pi_status,pi_statuscode,pi_updatedate,pi_updateman,pi_currency,pi_rate,pi_whcode,pi_whname,pi_transport,pi_address,"// 26
			+ "pi_departmentcode,pi_departmentname,pi_arname,pi_arcode,pi_receivecode,pi_receivename,pi_sourcecode,"
			+ // 33
			"pi_cusaddresssid,pi_remark,pi_custcode2,pi_custname2,pi_invoiceremark,pi_packingremark,pi_remark2,pi_tocode,pi_type,pi_merchandiser,pi_cop,pi_ntbamount,pi_tduedate,"
			+ "pi_printstatuscode,pi_printstatus,pi_emergency) VALUES "// 49
			+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String INSERT_PRODIO = "INSERT INTO prodinout(pi_id,pi_inoutno,pi_class,pi_invostatus,pi_invostatuscode,"
			+ "pi_operatorcode,pi_recordman,pi_recorddate,pi_cardid, pi_cardcode,pi_title,pi_sellercode,pi_sellername,pi_belongs,pi_payment,pi_paymentcode,"
			+ "pi_status,pi_statuscode,pi_updatedate,pi_updateman,pi_currency,pi_rate,pi_whcode,pi_whname,pi_transport,pi_address,"
			+ "pi_departmentcode,pi_departmentname,pi_arname,pi_arcode,pi_receivecode,pi_receivename,pi_sourcecode,"
			+ "pi_cusaddresssid,pi_remark,pi_cop,pi_ntbamount,pi_tduedate,pi_printstatuscode,pi_printstatus) VALUES "
			+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	/**
	 * 整张转出货单
	 */
	@Override
	@Transactional
	public int turnProdIN(int id) {
		try {
			Employee employee = SystemSession.getUser();
			SqlRowList rs = queryForRowSet(TURNPRODIO, new Object[] { id });
			int piid = 0;
			if (rs.next()) {
				piid = getSeqId("PRODINOUT_SEQ");
				String code = sGetMaxNumber("ProdInOut!Sale", 2);
				boolean bool = execute(
						INSERPRODIO,
						new Object[] { piid, code, "出货单", rs.getObject(1), rs.getObject(2), rs.getObject(3), rs.getObject(4),
								rs.getObject(5), rs.getObject(6), rs.getObject(7), rs.getObject(8), rs.getObject(9), rs.getObject(10),
								rs.getObject(11), rs.getObject(12), rs.getObject(13), rs.getObject(1),
								Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), BaseUtil.getLocalMessage("ENTERING"),
								employee.getEm_code(), employee.getEm_name(), "ENTERING", "UNPOST", BaseUtil.getLocalMessage("UNPOST"),
								Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), employee.getEm_name(),
								rs.getObject("sn_arcustcode"), rs.getObject("sn_arcustname"),
								getFieldDataByCondition("warehouse", "wh_description", "wh_code='" + rs.getObject(2) + "'"),
								rs.getObject("sn_sellercode"), rs.getObject("sn_sellername"), rs.getObject("sn_sellername"), "UNPRINT",
								BaseUtil.getLocalMessage("UNPRINT"), });
				if (bool) {
					execute("update prodinout set pi_rate=nvl((select cm_crrate from currencysmonth where cm_yearmonth=to_char(pi_date,'yyyymm') and cm_crname=pi_currency),1) where pi_id=? and nvl(pi_currency,' ')<>' '",
							piid);
					rs = queryForRowSet(TURNPRODINDETAIL, new Object[] { id });
					int count = 1;
					while (rs.next()) {
						int qty = rs.getInt(6) - rs.getInt(20);// 实际应转入值
						if (qty > 0) {
							int pdid = getSeqId("PRODIODETAIL_SEQ");
							String whcode = rs.getString("snd_warehousecode");
							String whName = rs.getString("snd_warehouse");
							Object[] prid = getFieldsDataByCondition("Product", new String[] { "pr_id" },
									"pr_code='" + rs.getString("snd_prodcode") + "'");
							execute(INSERPRODINDETAIL,
									new Object[] { pdid, code, "出货单", count++, rs.getObject("snd_ordercode"),
											rs.getObject("snd_orderdetno"), rs.getObject("snd_description"), rs.getObject("snd_batchcode"),
											rs.getObject("snd_prodcode"), qty, rs.getObject("snd_assqty"), rs.getDouble("snd_sendprice"),
											rs.getDouble("snd_taxrate"), rs.getObject("snd_discount"), rs.getObject("snd_sdid"),
											rs.getObject("snd_custprodcode"), rs.getObject("snd_pocode"), rs.getObject("snd_netprice"),
											rs.getObject("snd_readyqty"), rs.getObject("snd_id"), piid, 0, "ENTERING", id,
											rs.getDouble("snd_sendprice") / (1 + rs.getDouble("snd_taxrate") / 100),
											qty * rs.getDouble("snd_sendprice"),
											qty * rs.getDouble("snd_sendprice") / (1 + rs.getDouble("snd_taxrate") / 100), whcode, whName,
											prid[0], rs.getObject("snd_remark"), rs.getObject("snd_batchid"), rs.getObject("snd_bonded") });
						}
					}
				}
			}
			return piid;
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
			return 0;
		}
	}

	@Override
	public String newProdInOut() {
		int piid = getSeqId("PRODINOUT_SEQ");
		String code = sGetMaxNumber("ProdInOut!Sale", 2);
		Employee employee = SystemSession.getUser();
		boolean bool = execute(
				INSERTBASEPRODIO,
				new Object[] { piid, code, "出货单", BaseUtil.getLocalMessage("ENTERING"), "ENTERING", employee.getEm_code(),
						employee.getEm_name(), Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), "UNPOST",
						BaseUtil.getLocalMessage("UNPOST"), Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)),
						employee.getEm_name() });
		if (bool) {
			execute("update prodinout set pi_rate=nvl((select cm_crrate from currencysmonth where cm_yearmonth=to_char(pi_date,'yyyymm') and cm_crname=pi_currency),1) where pi_id=? and nvl(pi_currency,' ')<>' '",
					piid);
			return code;
		}
		return null;
	}

	@Override
	public String getPICodeBySourceCode(int id) {
		SqlRowList rs = queryForRowSet(GETPIBYSOURCE, new Object[] { id });
		if (rs.next()) {
			return rs.getString(1);
		}
		return null;
	}

	/**
	 * 批量转出货单
	 */
	@Override
	public void toAppointedProdInOut(int pi_id, String pi_code, int snd_id, double qty, int detno, String whcode, String whName, String type) {
		SqlRowList rs = queryForRowSet(TURNBASEPRODINDETAIL, new Object[] { snd_id });
		if (rs.next()) {
			if (pi_code != null) {
				StringBuffer sb = new StringBuffer();
				Object[] pi = getFieldsDataByCondition("ProdInOut", new String[] { "pi_cardcode", "pi_arcode", "pi_currency",
						"pi_paymentcode" }, "pi_id=" + pi_id);
				if (pi != null) {
					if (!pi[0].equals(rs.getString("sn_custcode"))) {
						sb.append("客户资料不符!通知单号[" + rs.getString("sn_code") + "],客户号[" + rs.getString("sn_custcode") + "],出货单客户号[" + pi[0]
								+ "]<br/>");
					}
					if (!pi[1].equals(rs.getString("sn_arcustcode"))) {
						sb.append("应收客户资料不符!通知单号[" + rs.getString("sn_code") + "],应收客户号[" + rs.getString("sn_arcustcode") + "],出货单应收客户号["
								+ pi[1] + "]<br/>");
					}
					if (!pi[2].equals(rs.getString("sn_currency"))) {
						sb.append("币别不符!通知单号[" + rs.getString("sn_code") + "],币别[" + rs.getString("sn_currency") + "],出货单币别[" + pi[2]
								+ "]<br/>");
					}
					if (!pi[3].equals(rs.getString("sn_paymentscode"))) {
						sb.append("收款方式不符!订单号[" + rs.getString("sn_code") + "],收款方式号[" + rs.getString("sn_paymentscode") + "],出货单收款方式号["
								+ pi[3] + "]<br/>");
					}
				}
				if (sb.length() > 0) {
					BaseUtil.showError(sb.toString());
				}
			}
			int pdid = getSeqId("PRODIODETAIL_SEQ");
			Object count = getFieldDataByCondition("ProdIODetail", "max(pd_pdno)", "pd_inoutno='" + pi_code + "' and pd_piid=" + pi_id);
			count = count == null ? 0 : count;
			Object[] prid = getFieldsDataByCondition("Product", new String[] { "pr_id" }, "pr_code='" + rs.getString("snd_prodcode") + "'");
			try {
				execute(INSERPRODINDETAIL,
						new Object[] { pdid, pi_code, type, detno, rs.getObject("snd_ordercode"), rs.getObject("snd_orderdetno"),
								rs.getObject("snd_description"), rs.getObject("snd_batchcode"), rs.getObject("snd_prodcode"), qty,
								rs.getObject("snd_assqty"), rs.getDouble("snd_sendprice"), rs.getDouble("snd_taxrate"),
								rs.getObject("snd_discount"), rs.getObject("snd_sdid"), rs.getObject("snd_custprodcode"),
								rs.getObject("snd_pocode"), rs.getObject("snd_netprice"), rs.getObject("snd_readyqty"),
								rs.getObject("snd_id"), pi_id, 0, "ENTERING", rs.getObject("sn_id"),
								rs.getDouble("snd_sendprice") / (1 + rs.getDouble("snd_taxrate") / 100),
								qty * rs.getDouble("snd_sendprice"),
								qty * rs.getDouble("snd_sendprice") / (1 + rs.getDouble("snd_taxrate") / 100), whcode, whName, prid[0],
								rs.getObject("snd_remark"), rs.getObject("snd_batchid"), rs.getObject("snd_bonded") });
				execute("update prodinout set pi_rate=nvl((select cm_crrate from currencysmonth where cm_yearmonth=to_char(pi_date,'yyyymm') and cm_crname=pi_currency),1) where pi_id=? and nvl(pi_currency,' ')<>' '",
						pi_id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 出货通知单按钮单张转出货单
	 */
	public JSONObject newProdInOutBySendNotify(int sn_id, String whcode, String whName, String pi_class) {
		SqlRowList rs = queryForRowSet("SELECT * FROM SendNotify WHERE sn_id=?", sn_id);
		Employee employee = SystemSession.getUser();
		int custAddressid = 0;
		if (rs.next()) {
			if (rs.getObject("sn_cusaddresssid") != null) {
				custAddressid = rs.getInt("sn_cusaddresssid");
			}
			String type = rs.getString("sn_type");
			// String kind = rs.getString("sn_outtype");
			String caller = null;
			if ("配货通知单".equals(type)) {
				type = "配货单";
			} else {
				type = pi_class;
			}
			if ("出货单".equals(pi_class)) {
				caller = "ProdInOut!Sale";
			} else if ("其它出库单".equals(pi_class)) {
				caller = "ProdInOut!OtherOut";
			} else if ("换货出库单".equals(pi_class)) {
				caller = "ProdInOut!ExchangeOut";
			} else if ("拨出单".equals(pi_class)) {
				caller = "ProdInOut!AppropriationOut";
			}
			int piid = getSeqId("PRODINOUT_SEQ");
			String code = sGetMaxNumber(caller, 2);
			// 宇声特殊转
			boolean IsSpecial = checkIf("SETTING", "se_what='SendNotifySpecTurn'");
			boolean bool = false;
			if (IsSpecial) {
				bool = execute(
						INSERT_PRODIOSPECIALys,
						new Object[] { piid, code, type, BaseUtil.getLocalMessage("ENTERING"), "ENTERING", employee.getEm_code(),
								employee.getEm_name(), Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)),
								rs.getObject("sn_custid"), rs.getString("sn_custcode"), rs.getString("sn_custname"),
								rs.getString("sn_sellercode"), rs.getString("sn_sellername"), rs.getString("sn_sellername"),
								rs.getString("sn_payment"), rs.getString("sn_paymentscode"), BaseUtil.getLocalMessage("UNPOST"), "UNPOST",
								Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), employee.getEm_name(),
								rs.getString("sn_currency"), rs.getDouble("sn_rate"), whcode, whName, rs.getString("sn_transport"),
								rs.getString("sn_toplace"), rs.getString("sn_departmentcode"), rs.getString("sn_departmentname"),
								rs.getString("sn_arcustname"), rs.getString("sn_arcustcode"), rs.getString("sn_receivecode"),
								rs.getString("sn_receivename"), rs.getString("sn_code"), custAddressid, rs.getString("sn_remark"),
								rs.getString("sn_custcode2"), rs.getString("sn_custname2"), rs.getString("sn_invoiceremark"),
								rs.getString("sn_packingremark"), rs.getString("sn_remark2"), rs.getString("sn_tocode"),
								rs.getString("sn_type"), rs.getString("sn_merchandiser"), rs.getObject("sn_cop"),
								rs.getObject("sn_ntbamount"), rs.getObject("sn_tduedate"), "UNPRINT", BaseUtil.getLocalMessage("UNPRINT"),
								rs.getString("sn_emergency") });
			} else {
				bool = execute(
						INSERT_PRODIO,
						new Object[] { piid, code, type, BaseUtil.getLocalMessage("ENTERING"), "ENTERING", employee.getEm_code(),
								employee.getEm_name(), Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)),
								rs.getObject("sn_custid"), rs.getString("sn_custcode"), rs.getString("sn_custname"),
								rs.getString("sn_sellercode"), rs.getString("sn_sellername"), rs.getString("sn_sellername"),
								rs.getString("sn_payment"), rs.getString("sn_paymentscode"), BaseUtil.getLocalMessage("UNPOST"), "UNPOST",
								Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), employee.getEm_name(),
								rs.getString("sn_currency"), rs.getDouble("sn_rate"), whcode, whName, rs.getString("sn_transport"),
								rs.getString("sn_toplace"), rs.getString("sn_departmentcode"), rs.getString("sn_departmentname"),
								rs.getString("sn_arcustname"), rs.getString("sn_arcustcode"), rs.getString("sn_receivecode"),
								rs.getString("sn_receivename"), rs.getString("sn_code"), custAddressid, rs.getString("sn_remark"),
								rs.getObject("sn_cop"), rs.getObject("sn_ntbamount"), rs.getObject("sn_tduedate"), "UNPRINT",
								BaseUtil.getLocalMessage("UNPRINT") });
			}
			if (bool) {
				getJdbcTemplate()
						.update("update prodinout set pi_rate=nvl((select cm_crrate from currencysmonth where cm_yearmonth=to_char(pi_date,'yyyymm') and cm_crname=pi_currency),1) where pi_id=? and nvl(pi_currency,' ')<>' '",
								piid);
				if ("出货单".equals(pi_class)) {
					// 更新 出货单联系人 和电话
					getJdbcTemplate()
							.update("update prodinout set (pi_purposename,pi_expresscode,pi_fax)=(select ca_person,ca_phone,ca_fax from CustomerAddress left join customer on ca_cuid=cu_id  where cu_code=? and ca_address=?)  where pi_id=?",
									rs.getObject("sn_custcode"), rs.getObject("sn_toplace"), piid);
				}
				JSONObject j = new JSONObject();
				j.put("pi_id", piid);
				j.put("pi_inoutno", code);
				return j;
			}
		}
		return null;
	}

	@Override
	public JSONObject newProdInOutWithCustomer(int custid, String custcode, String custname, String currency, Double rate, int cusaddressid) {
		Employee employee = SystemSession.getUser();
		int piid = getSeqId("PRODINOUT_SEQ");
		String code = sGetMaxNumber("ProdInOut!Sale", 2);
		Object[] objs = getFieldsDataByCondition("Customer", new String[] { "cu_sellerid", "cu_sellername", "cu_currency", "cu_rate",
				"cu_payments", "cu_paymentscode", "cu_sellercode", "cu_add1" }, "cu_id=" + custid);
		boolean bool = execute(
				INSERTPRODIOWITHCUST,
				new Object[] { piid, code, "出货单", BaseUtil.getLocalMessage("ENTERING"), "ENTERING", employee.getEm_code(),
						employee.getEm_name(), Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), custid, custcode, custname,
						objs[6], objs[1], objs[4], BaseUtil.getLocalMessage("UNPOST"), "UNPOST",
						Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), employee.getEm_name(), currency, rate,
						cusaddressid, objs[5], objs[1], "UNPRINT", BaseUtil.getLocalMessage("UNPRINT"), objs[7] });
		if (bool) {
			execute("update prodinout set (pi_arcode,pi_arname,pi_receivecode,pi_receivename)=(select cu_arcode,cu_arname,cu_shcustcode,cu_shcustname from customer where cu_id=?) where pi_id=?",
					custid, piid);
			execute("update prodinout set pi_rate=nvl((select cm_crrate from currencysmonth where cm_yearmonth=to_char(pi_date,'yyyymm') and cm_crname=pi_currency),1) where pi_id=? and nvl(pi_currency,' ')<>' '",
					piid);
			execute("update prodinout set (pi_departmentcode,pi_departmentname)=(select dp_code,em_depart from employee left join department on em_depart=dp_name where em_code=pi_sellercode) where pi_id=?",
					piid);
			JSONObject j = new JSONObject();
			j.put("pi_id", piid);
			j.put("pi_inoutno", code);
			return j;
		}
		return null;
	}

	@Override
	public void getCustomer(int[] id) {

	}

	@Override
	public void checkSNDQty(int sndid, Object snd_snid) {
		Object snid;
		if (snd_snid != null) {
			snid = snd_snid;
		} else {
			snid = getFieldDataByCondition("SendNotifyDetail", "snd_snid", "snd_id=" + sndid);
		}
		String status = null;
		int count = getCountByCondition("SendNotifyDetail", "snd_snid=" + snid);
		int yCount = getCountByCondition("SendNotifyDetail", "snd_snid=" + snid + " and nvl(snd_yqty,0)=nvl(snd_outqty,0) and nvl(snd_yqty,0)>=0");
		int xCount = getCountByCondition("SendNotifyDetail", "snd_snid=" + snid + " and nvl(snd_yqty,0)=0");
		status = "PARTOUT";
		if (xCount == count) {
			status = "";
		}
		if (yCount == count) {
			status = "TURNOUT";
		}
		updateByCondition("SendNotify", "SN_SENDSTATUSCODE='" + status + "',SN_SENDSTATUS='" + BaseUtil.getLocalMessage(status) + "'",
				"sn_id=" + snid);
	}

	@Override
	@Transactional
	public void deleteSendNotify(int id) {
		SqlRowList rs = queryForRowSet("select snd_id,snd_outqty from SendNotifyDetail where snd_snid=?", id);
		while (rs.next()) {
			restoreSale(rs.getGeneralInt("snd_id"));
			// 还原客户送货明细数量
			restoreNotice(rs.getGeneralInt("snd_id"));
			deleteByCondition("SendNotifyDetail", "snd_id=" + rs.getGeneralInt("snd_id"));
		}
		deleteById("SendNotify", "sn_id", id);
	}

	/**
	 * 发货通知单删除时，修改销售单单状态、数量等
	 */
	public void restoreSale(int sndid) {
		SqlRowList rs = queryForRowSet(
				"select snd_id,snd_outqty,snd_readyqty,snd_ordercode,snd_orderdetno,snd_snid from SendNotifyDetail where snd_id=?", sndid);
		if (rs.next()) {
			if (rs.getObject("snd_ordercode") != null && rs.getGeneralInt("snd_orderdetno") != 0) {
				Object sdid = getFieldDataByCondition("SaleDetail", "sd_id", "sd_code='" + rs.getObject("snd_ordercode")
						+ "' and sd_detno=" + rs.getGeneralInt("snd_orderdetno"));
				updateByCondition("SaleDetail", "sd_yqty=nvl(sd_yqty,0)-" + rs.getGeneralDouble("snd_outqty"), "sd_id=" + sdid
						+ " AND nvl(sd_yqty,0)>0");
				updateByCondition("SaleDetail", "sd_yqty=0", "sd_id=" + sdid + " AND nvl(sd_yqty,0)<0");
				updateSaleStatus(Integer.parseInt(sdid.toString()));
				execute("update sendnotify set sn_total=(select sum(snd_total) from sendnotifydetail where sendnotifydetail.snd_snid = sendnotify.sn_id) where sn_id="
						+ rs.getGeneralInt("snd_snid"));
			}
		}
	}

	/**
	 * 出货通知单删除时，修改送货提醒已转数
	 * 
	 * @param sndId
	 */
	public void restoreNotice(int sndId) {
		SqlRowList rs = queryForRowSet(
				"select snd_noticeid,snd_outqty from sendnotifydetail left join SaleNotifyDown on snd_noticeid=sn_id where snd_id=? and sn_id is not null",
				sndId);
		if (rs.next()) {
			updateByCondition("SaleNotifyDown", "sn_yqty=greatest((sn_yqty-" + rs.getGeneralDouble("snd_outqty") + "),0)",
					"sn_id=" + rs.getObject("snd_noticeid"));
		}
	}

	public void updateSaleStatus(int sdid) {
		Object said = getFieldDataByCondition("SaleDetail", "sd_said", "sd_id=" + sdid);
		if (said != null) {
			int id = Integer.parseInt(said.toString());
			checkSendNotifyStatus(id);
			checkSendStatus(id);
		}
	}

	/**
	 * 修改订单的通知单状态
	 * 
	 * @param said
	 * @param language
	 */
	private void checkSendNotifyStatus(int said) {
		int total = getCountByCondition("SaleDetail", "sd_said=" + said);
		int aud = getCountByCondition("SaleDetail", "sd_said=" + said + " AND nvl(sd_yqty,0)=0");
		int turn = getCountByCondition("SaleDetail", "sd_said=" + said + " AND sd_yqty=sd_qty and nvl(sd_yqty,0)>0");
		String status = "PART2SN";
		if (aud == total) {
			status = null;
		} else if (turn == total) {
			status = "TURNSN";
		}
		String str = null;
		if (status != null) {
			str = BaseUtil.getLocalMessage(status);
		}
		execute("UPDATE sale SET sa_turnstatuscode=?,sa_turnstatus=? WHERE sa_id=?", status, str, said);
	}

	/**
	 * 修改订单的发货状态
	 * 
	 * @param said
	 * @param language
	 */
	private void checkSendStatus(int said) {
		int total = getCountByCondition("SaleDetail", "sd_said=" + said);
		int aud = getCountByCondition("SaleDetail", "sd_said=" + said + " AND nvl(sd_sendqty,0)=0");
		int turn = getCountByCondition("SaleDetail", "sd_said=" + said + " AND sd_sendqty=sd_qty and nvl(sd_sendqty,0)>=0");
		String status = "PARTOUT";
		if (aud == total) {
			status = null;
		} else if (turn == total) {
			status = "TURNOUT";
		}
		String str = null;
		if (status != null) {
			str = BaseUtil.getLocalMessage(status);
		}
		execute("UPDATE sale SET sa_sendstatuscode=?,sa_sendstatus=? WHERE sa_id=?", status, str, said);
	}

	/**
	 * 发货通知单修改时，修改销售单状态、数量等
	 */
	@Override
	public void restoreSaleWithQty(int sndid, Double uqty, Object ordercode, Object orderdetno) {
		Object qty = 0;
		Object aq = 0;
		Object r = 0;
		Object endqty = 0;
		Object newqty = 0;
		Object newaq = 0;
		Object newr = 0;
		Object newendqty = 0;
		uqty = Math.abs(uqty);
		execute("update sendnotifydetail set snd_sdid=(select sd_id from saledetail where sd_code =snd_ordercode and sd_detno=snd_orderdetno) where nvl(snd_ordercode,' ')<>' ' and snd_id="
				+ sndid);
		// 判断数量是否超出销售数量
		Object[] snd = getFieldsDataByCondition("SendNotifyDetail", "snd_sdid,snd_ordercode,snd_orderdetno", "snd_id=" + sndid);
		// 可能存在有订单转过来的通知单 手动去修改订单和序号 导致无法还原数量
		if (orderdetno == null || " ".equals(orderdetno))
			orderdetno = 0;
		Object sdid = getFieldDataByCondition("SaleDetail", "sd_id", "sd_code='" + ordercode + "' and sd_detno='" + orderdetno + "'");
		if (snd != null && Integer.parseInt(snd[0].toString()) > 0) {
			qty = getFieldDataByCondition("SendNotifyDetail LEFT JOIN SendNotify ON SN_ID=SND_SNID", "sum(snd_outqty)", "snd_sdid="
					+ snd[0] + " AND snd_id <>" + sndid + " and snd_statuscode<>'FINISH'");
			r = getFieldDataByCondition("ProdIODetail left join ProdInOut on pd_piid=pi_id", "sum(pd_inqty)",
					"pd_piclass='销售退货单' and pi_statuscode='POSTED' and pd_ordercode='" + snd[1] + "' and pd_orderdetno=" + snd[2]);
			endqty = getFieldDataByCondition("SendNotifyDetail LEFT JOIN SendNotify ON SN_ID=SND_SNID", "sum(nvl(snd_yqty,0))",
					"snd_sdid=" + snd[0] + " AND snd_id <>" + sndid + " and snd_statuscode='FINISH'");
			qty = qty == null ? 0 : qty;
			r = r == null ? 0 : r;
			endqty = endqty == null? 0 : endqty;
			aq = getFieldDataByCondition("SaleDetail", "sd_qty", "sd_id=" + snd[0]);
			if (sdid != null && !"0".equals(sdid)) {
				if (!snd[0].equals(sdid)) {
					newqty = getFieldDataByCondition("SendNotifyDetail LEFT JOIN SendNotify ON SN_ID=SND_SNID", "sum(snd_outqty)",
							"snd_sdid=" + sdid + " AND snd_id <>" + sndid + " and snd_statuscode<>'FINISH'");
					newr = getFieldDataByCondition("ProdIODetail left join ProdInOut on pd_piid=pi_id", "sum(pd_inqty)",
							"pd_piclass='销售退货单' and pi_statuscode='POSTED' and pd_ordercode='" + ordercode + "' and pd_orderdetno="
									+ orderdetno);
					newendqty = getFieldDataByCondition("SendNotifyDetail LEFT JOIN SendNotify ON SN_ID=SND_SNID", "sum(snd_outqty)",
							"snd_sdid=" + sdid + " AND snd_id <>" + sndid + " and snd_statuscode='FINISH'");
					newqty = newqty == null ? 0 : newqty;
					newr = newr == null ? 0 : newr;
					newendqty = newendqty == null ? 0 : newendqty;
					newaq = getFieldDataByCondition("SaleDetail", "sd_qty", "sd_id=" + sdid);
					if (Double.parseDouble(newaq.toString()) < NumberUtil.formatDouble(Double.parseDouble(newqty.toString()) + uqty
							- Double.parseDouble(newr.toString())+Double.parseDouble(newendqty.toString()), 6)) {
						BaseUtil.showError("订单"
								+ ordercode
								+ "序号["
								+ orderdetno
								+ "]的新数量超出原销售数量,超出数量:"
								+ (Double.parseDouble(newqty.toString()) + uqty - Double.parseDouble(newr.toString())+Double.parseDouble(newendqty.toString()) - Double
										.parseDouble(newaq.toString())));
					} else {
						updateByCondition("SendNotifyDetail", "snd_outqty=" + uqty + ",snd_ordercode='" + ordercode + "',snd_orderdetno="
								+ orderdetno, "snd_id=" + sndid);
						updateByCondition(
								"SaleDetail",
								"sd_yqty="
										+ NumberUtil.formatDouble(
												(Double.parseDouble(qty.toString()) + uqty - Double.parseDouble(r.toString())+Double.parseDouble(endqty.toString())), 4),
								"sd_id=" + snd[0]);
						updateSaleStatus(Integer.parseInt(String.valueOf(snd[0])));
						updateByCondition(
								"SaleDetail",
								"sd_yqty="
										+ NumberUtil.formatDouble(
												(Double.parseDouble(newqty.toString()) + uqty - Double.parseDouble(newr.toString())+Double.parseDouble(newendqty.toString())), 4),
								"sd_id=" + sdid);
						updateSaleStatus(Integer.parseInt(String.valueOf(sdid)));
					}
				} else {
					if (Double.parseDouble(aq.toString()) < NumberUtil.formatDouble(
							Double.parseDouble(qty.toString()) + uqty - Double.parseDouble(r.toString()), 6)) {
						BaseUtil.showError("订单"
								+ snd[1]
								+ "序号["
								+ snd[2]
								+ "]的新数量超出原销售数量,超出数量:"
								+ (Double.parseDouble(qty.toString()) + uqty - Double.parseDouble(r.toString()) - Double.parseDouble(aq
										.toString())+Double.parseDouble(endqty.toString())));
					} else {
						updateByCondition("SendNotifyDetail", "snd_outqty=" + uqty, "snd_id=" + sndid);
						updateByCondition(
								"SaleDetail",
								"sd_yqty="
										+ NumberUtil.formatDouble(
												(Double.parseDouble(qty.toString()) + uqty - Double.parseDouble(r.toString())+Double.parseDouble(endqty.toString())), 4),
								"sd_id=" + snd[0]);
						updateSaleStatus(Integer.parseInt(String.valueOf(snd[0])));
					}
				}
			} else {
				BaseUtil.showError("订单" + ordercode + "序号[" + orderdetno + "]不存在！");
			}
		}
	}

	public void restoreNoticeWithQty(int sndId, Double thisQty, Object orderCode, Object orderDetno) {
		SqlRowList rs = queryForRowSet(
				"select snd_noticeid,snd_ordercode,snd_orderdetno,snd_outqty,sn_qty,sn_yqty from sendnotifydetail left join SaleNotifyDown on snd_noticeid=sn_id where snd_id=? and sn_id is not null",
				sndId);
		if (rs.next()) {
			if (!rs.getString("snd_ordercode").equals(orderCode) && !rs.getObject("snd_orderdetno").equals(orderDetno)) {
				BaseUtil.showError("由客户送货提醒生成的出货通知单不允许修改订单号、订单序号!");
			}
			Double yqty = getFieldValue("SendNotifyDetail LEFT JOIN SendNotify ON SN_ID=SND_SNID", "nvl(sum(snd_outqty),0)",
					"snd_noticeid=" + rs.getObject("snd_noticeid") + " AND snd_id <>" + sndId + " and snd_statuscode<>'FINISH'",
					Double.class);
			if (rs.getDouble("sn_qty") < yqty + thisQty) {
				BaseUtil.showError("超出送货提醒数量发货！<br>送货提醒需求数：" + rs.getDouble("sn_qty") + " < 已转数：" + yqty + " + 当前填写数量：" + thisQty);
			} else {
				updateByCondition("SaleNotifyDown", "sn_yqty=" + (yqty + thisQty), "sn_id=" + rs.getObject("snd_noticeid"));
			}
		}
	}

	static final String CHECK_YQTY = "SELECT snd_code,snd_pdno,snd_outqty FROM SendNotifyDetail LEFT JOIN SendNotify on sn_id=snd_snid WHERE snd_id=? and snd_outqty<? and snd_statuscode<>'FINISH'";

	/**
	 * 出货通知单转入出货单之前， 1.判断通知单状态 2.判断thisqty ≤ qty - yqty
	 */
	@Override
	public void checkAdYqty(List<Map<Object, Object>> datas, String piclass) {
		int id = 0;
		Object y = 0;
		Object r = 0;
		SqlRowList rs = null;
		Object[] sns = null;
		String inclass = null;
		if ("出货单".equals(piclass)) {
			inclass = "销售退货单";
		} else if ("其它出库单".equals(piclass)) {
			inclass = "其它入库单";
		} else if ("换货出库单".equals(piclass)) {
			inclass = "换货入库单";
		}
		for (Map<Object, Object> d : datas) {
			id = Integer.parseInt(d.get("snd_id").toString());
			sns = getFieldsDataByCondition("SendNotifyDetail left join SendNotify on snd_snid=sn_id",
					"sn_code,snd_pdno,snd_ordercode,snd_orderdetno", "snd_id=" + id + " and snd_statuscode<>'FINISH'");
			if (sns != null) {
				if (sns[2] == null || sns[2].toString().trim().length() == 0) {
					y = getFieldDataByCondition("ProdIODetail", "sum(nvl(pd_outqty,0))", "pd_orderid=" + id + " and pd_piclass='" + piclass
							+ "' and nvl(pd_ordercode,' ')=' ' and nvl(pd_orderdetno,0)=0");
					r = getFieldDataByCondition("ProdIODetail left join ProdInOut on pd_piid=pi_id", "sum(pd_inqty)", "pd_piclass='"
							+ inclass + "' and pi_statuscode='POSTED' and pd_orderid=" + id);
				} else {
					y = getFieldDataByCondition("ProdIODetail", "sum(nvl(pd_outqty,0))", "pd_orderid=" + id + " and pd_piclass='" + piclass
							+ "' and pd_ordercode='" + sns[2] + "' and pd_orderdetno=" + sns[3]);
					r = getFieldDataByCondition("ProdIODetail left join ProdInOut on pd_piid=pi_id", "sum(pd_inqty)", "pd_piclass ='"
							+ inclass + "' and pi_statuscode='POSTED' and pd_ordercode='" + sns[2] + "' and pd_orderdetno=" + sns[3]);
				}
				y = y == null ? 0 : y;
				r = r == null ? 0 : r;
				rs = queryForRowSet(CHECK_YQTY, id, Double.parseDouble(y.toString()) + Double.parseDouble(d.get("snd_tqty").toString())
						- Double.parseDouble(r.toString()));
				if (rs.next()) {
					StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],出货通知单号:").append(rs.getString("snd_code")).append(",行号:")
							.append(rs.getInt("snd_pdno")).append(",通知单数:").append(rs.getDouble("snd_outqty"))
							.append(",已转" + piclass + "数:").append(y).append("," + inclass + "数:").append(r).append(",本次数:")
							.append(d.get("snd_tqty"));
					BaseUtil.showError(sb.toString());
				}
			}
		}
	}

	@Override
	public void calYqty(int sn_id) {

	}

	@Override
	public void restoreSaleYqty(double snd_outqty, String snd_ordercode, Integer detno) {
		Object[] id = getFieldsDataByCondition("saledetail left join sale on sd_said=sa_id", new String[] { "sd_id", "sd_yqty", "sd_qty" },
				"sa_code='" + snd_ordercode + "' and sd_detno=" + detno);
		Object y = getFieldDataByCondition("SendNotifyDetail", "sum(nvl(snd_outqty,0))", "snd_ordercode='" + snd_ordercode
				+ "' and snd_orderdetno=" + detno);
		Object r = getFieldDataByCondition("ProdIODetail left join ProdInOut on pd_piid=pi_id", "sum(nvl(pd_inqty,0))",
				"pd_piclass='销售退货单' and pi_statuscode='POSTED' and pd_ordercode='" + snd_ordercode + "' and pd_orderdetno=" + detno);
		y = y == null ? 0 : y;
		r = r == null ? 0 : r;
		if (id != null) {
			if (NumberUtil.formatDouble(Double.parseDouble(y.toString()) + snd_outqty, 2) > NumberUtil.formatDouble(
					Double.valueOf(id[2].toString()) + Double.parseDouble(r.toString()), 2)) {
				BaseUtil.showError("销售单号为:"
						+ snd_ordercode
						+ ",订单序号为:"
						+ detno
						+ "数量超发,超出数量:"
						+ (Double.parseDouble(y.toString()) + snd_outqty - Double.parseDouble(id[2].toString()) - Double.parseDouble(r
								.toString())));
			} else {
				updateByCondition("saledetail", "sd_yqty=nvl(sd_yqty,0)+" + snd_outqty, "sd_id=" + id[0]);
				updateSaleStatus(Integer.parseInt(id[0].toString()));
			}
		} else {
			BaseUtil.showError("销售单号为:" + snd_ordercode + ",订单序号为:" + detno + "不存在,请核对后重新修改!");
		}
	}

	static final String GET_SENDNOTIFYDETAIL = "SELECT * FROM SendNotifyDetail LEFT JOIN SendNotify ON snd_snid=sn_id WHERE snd_id=?";
	static final String insert_QuaVerify = "insert into QUA_VerifyApplyDetail(ve_id,ve_code,vad_code,"
			+ "vad_detno,ve_sendcode,ve_senddetno,vad_prodcode,vad_vendcode,vad_vendname,vad_qty,ve_indate,ve_status,ve_statuscode,"
			+ "ve_type,ve_class,ve_recorder,ve_sourcetype)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'出货通知单')";

	@Override
	public String turnProdInOutCustomer(String caller, List<Map<Object, Object>> maps) {
		Employee employee = SystemSession.getUser();
		int veid = getSeqId("QUA_VERIFYAPPLYDETAIL_SEQ");
		String code = null;
		for (Map<Object, Object> map : maps) {
			SqlRowList rs = queryForRowSet(GET_SENDNOTIFYDETAIL, new Object[] { map.get("snd_id") });
			if (rs.next()) {
				float qty = Float.parseFloat(map.get("snd_thisoqcqty").toString());
				code = sGetMaxNumber("VerifyApplyDetailOQC", 2);
				execute(insert_QuaVerify,
						new Object[] { veid, code, rs.getString("snd_ordercode"), rs.getInt("snd_orderdetno"), rs.getString("sn_code"),
								rs.getInt("snd_pdno"), rs.getString("snd_prodcode"), rs.getString("sn_custcode"),
								rs.getString("sn_custname"), qty, Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)),
								BaseUtil.getLocalMessage("ENTERING"), "ENTERING", "OQC", "客户验货单", employee.getEm_name() });
				updateByCondition("SendNotifyDetail", "snd_oqcyqty=nvl(snd_oqcyqty,0)+" + qty, "snd_id=" + map.get("snd_id"));
				// 记录日志
				logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.VerifyApplyDetailOQC"),
						BaseUtil.getLocalMessage("msg.turnSuccess") + BaseUtil.getLocalMessage("msg.detail") + rs.getInt("snd_pdno")
								+ BaseUtil.getLocalMessage("msg.qty.out") + "+" + qty, "SendNotify|sa_id=" + rs.getInt("sn_id")));
			}
		}
		return "转入成功,客户验货单号:<a href=\"javascript:openUrl('jsps/scm/qc/verifyApplyDetailOQC.jsp?formCondition=ve_idIS" + veid
				+ "&whoami=VerifyApplyDetailOQC')\">" + code + "</a>&nbsp;";
	}
}
