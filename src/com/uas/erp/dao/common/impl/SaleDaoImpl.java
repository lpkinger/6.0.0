package com.uas.erp.dao.common.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.docx4j.wml.Style.BasedOn;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.deser.Deserializers.Base;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.ContextUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.SaleDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.pm.MakeBaseService;

@Repository
public class SaleDaoImpl extends BaseDao implements SaleDao {
	final static String SALE_PRICE = "select spd_price,spd_taxrate from (select spd_price,spd_taxrate from SalePriceDetail left join SalePrice on spd_spid=sp_id"
			+ " where spd_arcustcode=? and spd_prodcode=? and spd_currency=? and sp_kind=? and to_char(sp_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and"
			+ " to_char(sp_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND spd_statuscode='VALID' ORDER BY SalePrice.sp_indate DESC,SalePriceDetail.spd_price) where rownum<2";
	final static String SALE_PRICE_CCP = "select spd_price,spd_taxrate from (select spd_price,spd_taxrate,spd_remark,spd_ratio from SalePriceDetail left join SalePrice on spd_spid=sp_id"
			+ " where spd_arcustcode=? and spd_prodcode=? and spd_currency=? and to_char(sp_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and nvl(spd_lapqty,0)<=? and"
			+ " to_char(sp_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND spd_statuscode='VALID' and sp_statuscode='AUDITED' ORDER BY SalePrice.sp_indate DESC,SalePriceDetail.spd_price) where rownum<2";
	final static String SALE_PRICE_CCPR = "select spd_price,spd_taxrate from (select spd_price,spd_taxrate,spd_remark,spd_ratio from SalePriceDetail left join SalePrice on spd_spid=sp_id"
			+ " where spd_arcustcode=? and spd_prodcode=? and spd_currency=? AND spd_taxrate=? and to_char(sp_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and nvl(spd_lapqty,0)<=? and"
			+ " to_char(sp_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND spd_statuscode='VALID' and sp_statuscode='AUDITED' ORDER BY SalePrice.sp_indate DESC,SalePriceDetail.spd_price) where rownum<2";
	final static String SALE_PRICE_SCP = "select spd_price,spd_taxrate from (select spd_price,spd_taxrate,spd_remark,spd_ratio from SalePriceDetail left join SalePrice on spd_spid=sp_id"
			+ " where sp_kind=? and spd_prodcode=? and spd_currency=? and to_char(sp_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and nvl(spd_lapqty,0)<=? and"
			+ " to_char(sp_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND spd_statuscode='VALID' and sp_statuscode='AUDITED' ORDER BY SalePrice.sp_indate DESC,SalePriceDetail.spd_price) where rownum<2";
	final static String SALE_PRICE_KCP = "select spd_price,spd_taxrate from (select spd_price,spd_taxrate,spd_remark,spd_ratio from SalePriceDetail left join SalePrice on spd_spid=sp_id"
			+ " where spd_prodcode=? and spd_currency=? and to_char(sp_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and spd_pricetype=? and nvl(spd_lapqty,0)<=? and"
			+ " to_char(sp_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND spd_statuscode='VALID' and sp_statuscode='AUDITED' ORDER BY SalePrice.sp_indate DESC,SalePriceDetail.spd_price) where rownum<2";
	final static String SALE_PRICE_PC = "select spd_price,spd_taxrate from (select spd_price,spd_taxrate,spd_remark,spd_ratio from SalePriceDetail left join SalePrice on spd_spid=sp_id"
			+ " where spd_prodcode=? and spd_currency=? and to_char(sp_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and nvl(spd_lapqty,0)<=? and"
			+ " to_char(sp_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND spd_statuscode='VALID' and sp_statuscode='AUDITED' ORDER BY SalePrice.sp_indate DESC,SalePriceDetail.spd_price) where rownum<2";
	final static String SALE_PRICE_PCR = "select spd_price,spd_taxrate from (select spd_price,spd_taxrate,spd_remark,spd_ratio from SalePriceDetail left join SalePrice on spd_spid=sp_id"
			+ " where spd_prodcode=? and spd_currency=? and spd_taxrate=? and to_char(sp_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and nvl(spd_lapqty,0)<=? and"
			+ " to_char(sp_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND spd_statuscode='VALID' and sp_statuscode='AUDITED' ORDER BY SalePrice.sp_indate DESC,SalePriceDetail.spd_price) where rownum<2";
	final static String SALE_PRICE_S = "select spd_price,spd_taxrate from (select spd_price,spd_taxrate from SalePriceDetail left join SalePrice on spd_spid=sp_id"
			+ " where (spd_arcustcode,spd_prodcode,spd_currency,sp_kind)=(select sa_custcode,sd_prodcode,sa_currency,sa_kind from "
			+ " sale left join saledetail on sd_said=sa_id where sa_code=? and sd_detno=?) and sp_fromdate<=sysdate and"
			+ " sp_todate>=sysdate AND spd_statuscode='VALID' AND sp_statuscode='AUDITED' ORDER BY SalePrice.sp_indate DESC,SalePriceDetail.spd_price) where rownum<2";
	static final String GET_SALE = "select * from sale";
	static final String GET_SALEDETAIL = "SELECT sa_code,sa_custid,sa_custcode,sa_custname,sa_pocode,sa_seller,sd_id,sd_detno,sd_prodcode,"
			+ "sd_price,sa_id,sd_taxrate,sa_shcustcode,sa_shcustname,sa_apcustcode,sa_apcustname,sa_currency,sa_rate,sa_remark,sd_remark,"
			+ "sd_custprodcode,sa_cusaddresssid,sa_paymentscode,sd_prodcustcode FROM SaleDetail LEFT JOIN Sale ON sd_said=sa_id WHERE sd_id=?";
	static final String insert_QuaVerify = "insert into QUA_VerifyApplyDetail(ve_id,ve_code,vad_code,"
			+ "vad_detno,vad_prodcode,vad_vendcode,vad_vendname,vad_qty,ve_indate,ve_status,ve_statuscode,"
			+ "ve_type,ve_class,ve_recorder,ve_sourcetype)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,'销售订单')";
	// pd_orderid订单明细的id
	static final String INSERT_IODETAIL = "INSERT INTO prodiodetail(pd_orderid,pd_orderdetno,pd_ordercode,pd_prodcode,"
			+ "pd_sendprice,pd_outqty,pd_beipinoutqty,pd_pocode,pd_id,pd_inoutno,pd_piclass,pd_pdno,pd_status,pd_auditstatus,pd_piid,"
			+ "pd_netprice,pd_taxrate,pd_taxtotal,pd_nettotal,pd_prodid,pd_sdid,pd_snid,pd_remark,pd_custprodcode,pd_barcode) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	static final String TURNSENDNOTIFY = "SELECT sa_code,sa_custid,sa_custcode,sa_sellerid,sa_seller,sa_currency,sa_rate,sa_cop"
			+ ",sa_paymentsid,sa_toplace,sa_pocode,sa_plandelivery,sa_custname,sa_payments,sa_kind,sa_transport,sa_salemethod"
			+ ",sa_shcustcode,sa_shcustname,sa_apcustcode,sa_apcustname,sa_paymentscode FROM sale WHERE sa_id=?";
	static final String INSERTSENDNOTIFY = "INSERT INTO sendnotify(sn_id,sn_code,sn_custid,sn_custcode,sn_sellerid,sn_sellername,sn_currency,sn_rate,sn_cop,sn_payments,sn_toplace"
			+ ",sn_pocode,sn_deliverytime,sn_custname,sn_payment,sn_date,sn_recorder,sn_indate,sn_status,sn_kind,sn_statuscode,sn_recordername,sn_saletype,sn_transport,sn_salemethod"
			+ ",sn_shcustcode,sn_shcustname,sn_arcustcode,ar_cusrname,sn_paymentscode,sn_departmentcode,sn_departmentname)VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String INSERTBASESENDNOTIFY = "INSERT INTO sendnotify(sn_id,sn_code,sn_status,sn_statuscode"
			+ ",sn_recorder,sn_indate,sn_recordername) VALUES (?,?,?,?,?,?,?)";
	static final String INSERTSENDNOTIFYWITHCUST = "INSERT INTO sendnotify(sn_id,sn_code,sn_status,sn_statuscode,sn_recorder,sn_recordername,sn_indate"
			+ ",sn_custid,sn_custcode,sn_custname,sn_sellername,sn_currency,sn_payments,sn_payment,sn_paymentscode,sn_shcustcode,sn_shcustname,sn_rate,"
			+ "sn_saletype,sn_kind,sn_toplace,sn_arcustcode,sn_arcustname,sn_salemethod,sn_transport,sn_sellercode,sn_departmentcode,sn_departmentname,sn_cusaddresssid,sn_pocode,sn_remark,sn_cop) VALUES ("
			+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String TURNSALEDETAIL = "SELECT sd_id,sd_said,sd_code,sd_detno,sd_prodcode,sd_qty,sd_price,sd_custprodcode,sd_discount,sd_batchcode,sd_taxrate,sd_netprice"
			+ ",sd_assqty,sd_readyqty,sd_description,sd_yqty,sd_bonded,sa_pocode,sa_id,sd_remark FROM saledetail left join sale on sd_said=sa_id WHERE sd_said=?";
	static final String TURNBASESALEDETAIL = "SELECT sd_id,sd_said,sd_code,sd_detno,sd_prodcode,sd_qty,sd_price,sd_custprodcode,sd_discount,sd_batchcode,sd_taxrate,sd_netprice"
			+ ",sd_assqty,sd_readyqty,sd_description,sd_yqty,sa_pocode,sa_id,sd_remark,sa_custcode,sa_apcustcode,sa_paymentscode,sa_code,sa_currency FROM saledetail left join sale on sd_said=sa_id WHERE sd_id=?";
	static final String INSERTSENDNOTIFYDETAIL = "INSERT INTO SendNotifyDetail(snd_id,snd_snid,snd_pdno,snd_sdid,snd_ordercode,snd_orderdetno,snd_prodcode,snd_outqty,snd_sendprice"
			+ ",snd_custprodcode,snd_discount,snd_batchcode,snd_taxrate,snd_netprice,snd_assqty,snd_readyqty,snd_description,snd_statuscode"
			+ ",snd_sourcecode,snd_code,snd_pocode,snd_total,snd_taxtotal,snd_remark,snd_bonded) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String GETSNBYSOURCE = "select sn_code from sendnotify left join SaleDetail on sn_id=snd_snid where snd_sourcecode=(select sa_code from sale left join saledetail on sa_id=sd_said where sd_id=?)";

	static final String TURNSALE = "select sd_id,sa_code,sd_detno,case when sd_detno<10 then '0'||sd_detno else to_char(sd_detno) end  sddetno,sd_delivery-1 delivery,sa_custcode,sa_custname,sd_prodcode,sd_bonded,sd_delivery,pr_gdtqq,pr_plzl,pr_id,pr_whcode,bo_id,"
			+ "CASE WHEN nvl(pr_manutype,' ')='MAKE' THEN 'MAKE' WHEN nvl(pr_manutype,' ')='OSMAKE' THEN 'OS' END pr_manutype,pr_leadtime,sa_pocode,bo_wccode,sa_cop,sd_factory "
			+ "from saledetail left join sale on sd_said=sa_id left join product on sd_prodcode=pr_code left join bom on bo_mothercode=sd_prodcode where sd_id=?";
	static final String INSERTMAKE = "INSERT INTO Make(ma_id,ma_code,ma_date,ma_status,ma_statuscode,ma_recorddate,ma_recorderid,ma_recorder,ma_tasktype,ma_saledetailid,"
			+ "ma_salecode,ma_saledetno,ma_pocode,ma_requiredate,ma_custcode,ma_custname,ma_prodid,ma_prodcode,ma_bonded,ma_sourcekind,"
			+ "ma_whcode,ma_printstatus,ma_printstatuscode,ma_bomid,ma_madeqty,ma_wccode,ma_source,ma_qty,ma_turnstatus,ma_turnstatuscode,"
		     + "  ma_cop,ma_kind,ma_checkstatus,ma_checkstatuscode,ma_finishstatus,ma_finishstatuscode,ma_planbegindate,ma_planenddate,ma_factory)"
			+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String GETSALEPUPRICE = "select spd_price,spd_ratio from salepricedetail where spd_pricetype=? and spd_prodcode=? and spd_currency=? and nvl(spd_lapqty,0)<=? and nvl(spd_statuscode,' ')='VALID'  order by spd_lapqty desc,spd_id desc";

	@Override
	public boolean deleteSaleforecastdetail(String sa_relativecode, int sa_id) {
		boolean bool = true;
		String sql = "SELECT Saledetail.sd_detno FROM Saledetail LEFT JOIN (SELECT sd_code,sd_prodcode FROM saleforecastdetail WHERE sd_code='"
				+ sa_relativecode
				+ "') tab ON tab.sd_prodcode=Saledetail.sd_prodcode  WHERE Saledetail.sd_said="
				+ sa_id
				+ " AND tab.sd_prodcode is null";
		SqlRowList set = queryForRowSet(sql);
		while (set.next()) {
			bool = false;
			sql = "DELETE FROM Saledetail WHERE sd_said=" + sa_id + " AND sd_detno=" + set.getInt(1);
			execute(sql);
		}
		return bool;
	}

	@Override
	public String checkProdInOut(String sa_code) {
		String sql = "SELECT pd_inoutno FROM ProdIODetail,ProdInout WHERE pd_piid=pi_id AND "
				+ "pi_status<>'DELETED' AND pd_piclass=pi_class AND pd_piclass='发货单' AND pd_ordercode='" + sa_code + "'";
		SqlRowList set = queryForRowSet(sql);
		if (set.next()) {
			return set.getString("pd_inoutno");
		}
		return null;
	}

	@Override
	public String checkQty(int sa_id) {
		String sql = "SELECT saledetail.sd_detno FROM saledetail left join saleforecastdetail on "
				+ " saledetail.sd_forecastcode=saleforecastdetail.sd_code and saledetail.sd_forecastdetno=saleforecastdetail.sd_detno "
				+ " WHERE  saledetail.sd_said=" + sa_id
				+ " and saledetail.sd_forecastcode<>' ' AND NVL(saleforecastdetail.sd_qty,0)<saledetail.sd_qty";
		SqlRowList set = queryForRowSet(sql);
		if (set.next()) {
			return set.getInt("sd_detno") + "";
		}
		return null;
	}

	@Override
	@Transactional
	public int turnSendNotify(int id) {
		try {
			SqlRowList rs = queryForRowSet(TURNSENDNOTIFY, new Object[] { id });
			Employee employee = SystemSession.getUser();
			int snid = 0;
			if (rs.next()) {
				snid = getSeqId("SENDNOTIFY_SEQ");
				String code = sGetMaxNumber("SendNotify", 2);
				String sourcecode = rs.getString(1);
				boolean bool = execute(
						INSERTSENDNOTIFY,
						new Object[] { snid, code, rs.getObject(2), rs.getObject(3), rs.getObject(4), rs.getObject(5), rs.getObject(6),
								rs.getObject(7), rs.getObject(8), rs.getObject(9), rs.getObject("sa_toplace"), rs.getObject("sa_pocode"),
								rs.getObject(12), rs.getObject(13), rs.getObject(14),
								Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), employee.getEm_id(),
								Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), BaseUtil.getLocalMessage("ENTERING"),
								"出货通知单", "ENTERING", employee.getEm_name(), rs.getObject("sa_kind"), rs.getObject("sa_transport"),
								rs.getObject("sa_salemethod"), rs.getObject("sa_shcustcode"), rs.getObject("sa_shcustname"),
								rs.getObject("sa_arcustcode"), rs.getObject("sa_arcustname"), rs.getObject("sa_paymentscode"),
								rs.getObject("sa_departmentcode"), rs.getObject("sa_departmentname") });
				if (bool) {
					rs = queryForRowSet(TURNSALEDETAIL, new Object[] { id });
					int count = 1;
					while (rs.next()) {
						int qty = rs.getInt(6) - rs.getInt(19);// 实际应转入值
						if (qty > 0) {
							int sndid = getSeqId("SENDNOTIFYDETAIL_SEQ");
							Double rate = rs.getDouble("sd_taxrate");
							Double price = rs.getGeneralDouble("sd_price", 6);
							Double netprice = NumberUtil.formatDouble(price / (1 + rate / 100), 6);
							Double total = NumberUtil.formatDouble(qty * price, 2);
							Double nettotal = NumberUtil.formatDouble(qty * netprice, 2);
							execute(INSERTSENDNOTIFYDETAIL,
									new Object[] { sndid, snid, count++, rs.getObject("sd_id"), rs.getObject("sd_code"),
											rs.getObject("sd_detno"), rs.getObject("sd_prodcode"), qty, price,
											rs.getObject("sd_custprodcode"), rs.getObject("sd_discount"), rs.getObject("sd_batchcode"),
											rate, netprice, rs.getObject("sd_assqty"), rs.getObject("sd_readyqty"),
											rs.getObject("sd_description"), "ENTERING", sourcecode, code, rs.getObject("sa_pocode"), total,
											nettotal, rs.getObject("sd_remark"), rs.getObject("sd_bonded") });
						}
					}
				}
			}
			return snid;
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
			return 0;
		}
	}

	@Override
	public int toAppointedSendNotify(String sn_code, int sd_id, double qty) {
		Object snid = getFieldDataByCondition("SendNotify", "sn_id", "sn_code='" + sn_code + "'");
		SqlRowList rs = queryForRowSet(TURNBASESALEDETAIL, new Object[] { sd_id });
		if (rs.next()) {
			if (sn_code != null) {
				StringBuffer sb = new StringBuffer();
				Object[] sn = getFieldsDataByCondition("SendNotify", new String[] { "sn_custcode", "sn_arcustcode", "sn_currency",
						"sn_paymentscode" }, "sn_code='" + sn_code + "'");
				if (sn != null) {
					if (!sn[0].equals(rs.getString("sa_custcode"))) {
						sb.append("客户资料不符!订单号[" + rs.getString("sa_code") + "],客户号[" + rs.getString("sa_custcode") + "],通知单客户号[" + sn[0]
								+ "]<br/>");
					}
					if (!sn[1].equals(rs.getString("sa_apcustcode"))) {
						sb.append("应收客户资料不符!订单号[" + rs.getString("sa_code") + "],应收客户号[" + rs.getString("sa_apcustcode") + "],通知单应收客户号["
								+ sn[1] + "]<br/>");
					}
					if (!sn[2].equals(rs.getString("sa_currency"))) {
						sb.append("币别不符!订单号[" + rs.getString("sa_code") + "],币别[" + rs.getString("sa_currency") + "],通知单币别[" + sn[2]
								+ "]<br/>");
					}
					if (!sn[3].equals(rs.getString("sa_paymentscode"))) {
						sb.append("收款方式不符!订单号[" + rs.getString("sa_code") + "],收款方式号[" + rs.getString("sa_paymentscode") + "],通知单收款方式号["
								+ sn[3] + "]<br/>");
					}
				}
				if (sb.length() > 0) {
					BaseUtil.showError(sb.toString());
				}
			}
			int sndid = getSeqId("SENDNOTIFYDETAIL_SEQ");
			Object count = getFieldDataByCondition("SendNotifyDetail", "max(snd_pdno)", "snd_code='" + sn_code + "'");
			count = count == null ? 0 : count;
			int detno = Integer.parseInt(count.toString());
			Double rate = rs.getDouble("sd_taxrate");
			Double price = rs.getGeneralDouble("sd_price", 6);

			Double total = NumberUtil.formatDouble(qty * price, 2);
			Double netprice = NumberUtil.formatDouble(price / (1 + rate / 100), 6);
			Double nettotal = NumberUtil.formatDouble(qty * netprice, 2);
			execute(INSERTSENDNOTIFYDETAIL,
					new Object[] { sndid, snid, ++detno, rs.getObject("sd_id"), rs.getObject("sd_code"), rs.getObject("sd_detno"),
							rs.getObject("sd_prodcode"), qty, price, rs.getObject("sd_custprodcode"), rs.getObject("sd_discount"),
							rs.getObject("sd_batchcode"), rate, netprice, rs.getObject("sd_assqty"), 0, rs.getObject("sd_description"),
							"ENTERING", rs.getObject("sd_code"), sn_code, rs.getObject("sa_pocode"), total, nettotal,
							rs.getObject("sd_remark"), rs.getObject("sd_bonded") });
			return sndid;
		} else
			return 0;
	}

	@Override
	public JSONObject newSendNotifyWithCustomer(int custid, String custcode, String custname, String shcustcode, String shcustName,
			String currency, Double rate, String kind, String address, String salemethod, String shipment, String apcustCode,
			String apcustName, String departcode, String departname, int cuaddressid, Object sa_id) {
		int snid = getSeqId("SENDNOTIFY_SEQ");
		String code = sGetMaxNumber("SendNotify", 2);
		Employee employee = SystemSession.getUser();
		Object[] sn_pocde = getFieldsDataByCondition("sale", new String[] { "sa_pocode", "sa_apcustcode", "sa_apcustname", "sa_salemethod",
				"sa_transport", "sa_kind", "sa_seller", "sa_sellercode", "sa_paymentscode", "sa_payments", "sa_shcustcode",
				"sa_shcustname", "sa_paymentsid", "sa_remark", "sa_cop" }, "sa_id=" + sa_id);
		boolean bool = execute(INSERTSENDNOTIFYWITHCUST, new Object[] { snid, code, BaseUtil.getLocalMessage("ENTERING"), "ENTERING",
				employee.getEm_id(), employee.getEm_name(), Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), custid,
				custcode, custname, sn_pocde[6], currency, sn_pocde[12], sn_pocde[9], sn_pocde[8], sn_pocde[10], sn_pocde[11], rate, kind,
				sn_pocde[5], address, sn_pocde[1], sn_pocde[2], sn_pocde[3], sn_pocde[4], sn_pocde[7], departcode, departname, cuaddressid,
				sn_pocde[0], sn_pocde[13], sn_pocde[14] });
		if (bool) {
			JSONObject j = new JSONObject();
			j.put("sn_id", snid);
			j.put("sn_code", code);
			return j;
		}
		return null;
	}

	@Override
	public JSONObject newSendNotifyWithSale(int sa_id) {
		int snid = getSeqId("SENDNOTIFY_SEQ");
		String code = sGetMaxNumber("SendNotify", 2);
		Employee employee = SystemSession.getUser();
		SqlRowList rs = queryForRowSet("select * from sale left join customer on cu_code=sa_custcode where sa_id=?", sa_id);
		if (rs.next()) {
			boolean bool = execute(
					INSERTSENDNOTIFYWITHCUST,
					new Object[] { snid, code, BaseUtil.getLocalMessage("ENTERING"), "ENTERING", employee.getEm_id(),
							employee.getEm_name(), Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)),
							rs.getGeneralInt("cu_id"), rs.getString("sa_custcode"), rs.getString("cu_name"), rs.getString("sa_seller"),
							rs.getString("sa_currency"), rs.getGeneralInt("sa_paymentsid"), rs.getString("sa_payments"),
							rs.getString("sa_paymentscode"), rs.getString("sa_shcustcode"), rs.getString("sa_shcustname"),
							rs.getDouble("sa_rate"), rs.getString("sa_kind"), rs.getString("sa_kind"), rs.getString("sa_toplace"),
							rs.getString("sa_apcustcode"), rs.getString("sa_apcustname"), rs.getString("sa_salemethod"),
							rs.getString("sa_transport"), rs.getString("sa_sellercode"), rs.getString("sa_departmentcode"),
							rs.getString("sa_departmentname"), rs.getGeneralInt("sa_cusaddresssid"), rs.getGeneralString("sa_pocode"),
							rs.getString("sa_remark"), rs.getString("sa_cop") });
			if (bool) {
				JSONObject j = new JSONObject();
				j.put("sn_id", snid);
				j.put("sn_code", code);
				return j;
			}
		}
		return null;
	}

	@Override
	public String newSendNotify() {
		int snid = getSeqId("SENDNOTIFY_SEQ");
		String code = sGetMaxNumber("SendNotify", 2);
		Employee employee = SystemSession.getUser();
		boolean bool = execute(
				INSERTBASESENDNOTIFY,
				new Object[] { snid, code, BaseUtil.getLocalMessage("ENTERING"), "ENTERING", employee.getEm_id(),
						Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), employee.getEm_name() });
		if (bool) {
			return code;
		}
		return null;
	}

	@Override
	public String getSNCodeBySourceCode(int id) {
		SqlRowList rs = queryForRowSet(GETSNBYSOURCE, new Object[] { id });
		if (rs.next()) {
			return rs.getString(1);
		}
		return null;
	}

	/**
	 * 修改销售单状态
	 */
	@Override
	public void updateturnstatus(int sdid) {
		Object said = getFieldDataByCondition("SaleDetail", "sd_said", "sd_id=" + sdid);
		int total = getCountByCondition("SaleDetail", "sd_said=" + said);
		int aud = getCountByCondition("SaleDetail", "sd_said=" + said + " AND nvl(sd_yqty,0)=0");
		int turn = getCountByCondition("SaleDetail", "sd_said=" + said + " AND nvl(sd_yqty,0)=nvl(sd_qty,0) and nvl(sd_yqty,0)>=0");
		String status = "PART2SN";
		if (aud == total) {
			status = "";
		} else if (turn == total) {
			status = "TURNSN";
		}
		updateByCondition("Sale", "sa_turnstatuscode='" + status + "',sa_turnstatus='" + BaseUtil.getLocalMessage(status) + "'", "sa_id="
				+ said);
	}

	@Override
	public void deleteSale(int id) {
		List<Object[]> objs = getFieldsDatasByCondition("SaleDetail", new String[] { "sd_id", "sd_qty", "sd_sourceid" }, "sd_said=" + id);
		for (Object[] obj : objs) {
			if (Integer.parseInt(obj[1].toString()) > 0 && obj[2] != null) {
				// 还原报价明细及报价单
				restoreQuotation(Integer.parseInt(obj[0].toString()));
			}
			deleteByCondition("SaleDetail", "sd_id=" + obj[0]);
		}
	}

	/**
	 * 销售单删除时，修改报价单状态、数量等
	 */
	public void restoreQuotation(int sdid) {
		Object[] objs = getFieldsDataByCondition("SaleDetail", new String[] { "sd_sourceid", "sd_qty", "sd_source" }, "sd_id=" + sdid
				+ " and nvl(sd_sourceid,0)>0");
		if (objs != null) {
			updateByCondition("QuotationDetail", "qd_yqty=qd_yqty-" + Integer.parseInt(objs[1].toString()), "qd_id=" + objs[0]);
			updateQuoStatus(Integer.parseInt(objs[0].toString()));
		}
	}

	public void updateQuoStatus(int qdid) {
		Object quid = getFieldDataByCondition("QuotationDetail", "qd_quid", "qd_id=" + qdid);
		int total = getCountByCondition("QuotationDetail", "qd_id=" + qdid);
		int aud = getCountByCondition("QuotationDetail", "qd_id=" + qdid + " AND qd_yqty=0");
		int turn = getCountByCondition("QuotationDetail", "qd_id=" + qdid + " AND qd_yqty=qd_qty");
		String status = "PART2SA";
		if (aud == total) {
			status = "AUDITED";
		} else if (turn == total) {
			status = "TURNSA";
		}
		updateByCondition("Quotation", "qu_statuscode='" + status + "',qu_status='" + BaseUtil.getLocalMessage(status) + "'", "qu_id="
				+ quid);
	}

	/**
	 * 订单批量转制造单
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public StringBuffer turnMake(int sdid, Double qty) {
		StringBuffer res = new StringBuffer();
		SqlRowList rs = queryForRowSet(TURNSALE, new Object[] { sdid });
		Employee employee = SystemSession.getUser();
		if (rs.next()) {
			int id = getSeqId("MAKE_SEQ");
			String rcode = sGetMaxNumber("MAKE!Base", 2);
			String	code=rcode+"-01";
			int pr_gdtqq=rs.getGeneralInt("pr_gdtqq");
			int  plzl_day=0;
			int  leadtime=rs.getGeneralInt("pr_leadtime");
			if(rs.getGeneralInt("pr_plzl")!=0){
				plzl_day=(int) Math.ceil(qty/rs.getGeneralInt("pr_plzl"));
			}
			//计划完工日期=订单需求日期-1-物料的固定提前期
			Date enddate = DateUtil.overDate(rs.getDate("sd_delivery"),-1-pr_gdtqq);
			if (enddate.compareTo(new Date()) < 0) {
				enddate = new Date();
			}
			//计划开始日期=计划完工日期-提前期-（工单数量/提前期批量）取整
			Date begindate =DateUtil.overDate(enddate,0-(leadtime+plzl_day));
			if (begindate.compareTo(new Date()) < 0) {
				begindate = new Date();
			}
			Timestamp time = Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS));
			Object makekind=null;
			String caller="",type="";
			if("MAKE".equals(rs.getObject("pr_manutype"))){
				caller="Make!Base";
				type="制造单";
				makekind=getFieldDataByCondition("MAKEKIND","max(MK_NAME)", "mk_isuse='-1' and mk_makind='MAKE' and mk_ifmrpkind='-1'");
			}else if("OS".equals(rs.getObject("pr_manutype"))){
				caller="Make";
				type="委外加工单";
				makekind=getFieldDataByCondition("MAKEKIND","max(MK_NAME)", "mk_isuse='-1' and mk_makind='OSMAKE' and mk_ifmrpkind='-1'");
			};
			if(makekind==null){
				makekind="标准工单";
			}
			boolean bool = execute(
					INSERTMAKE,
					new Object[] { id, code, time, BaseUtil.getLocalMessage("AUDITED"), "AUDITED",time, employee.getEm_id(),
							employee.getEm_name(), rs.getObject("pr_manutype"),sdid,rs.getObject("sa_code"),rs.getObject("sd_detno"),
							rs.getObject("sa_pocode"), rs.getObject("delivery"),rs.getObject("sa_custcode"),
							rs.getObject("sa_custname"),rs.getObject("pr_id"),rs.getObject("sd_prodcode"),rs.getObject("sd_bonded"),"Sale",
							rs.getObject("pr_whcode"),BaseUtil.getLocalMessage("UNPRINT"), "UNPRINT",rs.getObject("bo_id"),0 ,rs.getObject("bo_wccode"),
							"订单下推",qty,BaseUtil.getLocalMessage("UNGET"),"UNGET",rs.getObject("sa_cop"),makekind,BaseUtil.getLocalMessage("UNAPPROVED"),
							"UNAPPROVED",BaseUtil.getLocalMessage("UNCOMPLET"),"UNCOMPLET",
							Timestamp.valueOf(DateUtil.parseDateToString(begindate, Constant.YMD_HMS)),
							Timestamp.valueOf(DateUtil.parseDateToString(enddate, Constant.YMD_HMS)),rs.getObject("sd_factory")});
			if (bool) {
				MakeBaseService makeBaseService=(MakeBaseService) ContextUtil.getBean("makeBaseService");
				makeBaseService.setMakeMaterial(code, caller);
				Object onhand=getFieldDataByCondition("productonhand", "po_onhand", "po_prodcode='"+rs.getObject("sd_prodcode")+"'");
				res.append(type + "号:"
						+ "<a href=\"javascript:openUrl('jsps/pm/make/makeBase.jsp?whoami="+caller+"&formCondition=ma_idIS" + id
						+ "&gridCondition=mm_maidIS" + id +"')\">"+code + "</a>&nbsp;产品库存数:"+(onhand==null?0:onhand.toString())+"&nbsp;<hr>");
				
				SqlRowList sonBoms = queryForRowSet("select mm_bomid,mm_qty from makematerial left join product on mm_prodcode=pr_code where mm_maid=? "
						+ "and nvl(pr_manutype,' ') in('MAKE','OSMAKE') and nvl(pr_dhzc,' ')='MPS' and NVL(MM_MATERIALSTATUS,' ')<>'JUMP' AND NVL(MM_BOMID,0)>0", id);				
				int c=2;
				while(sonBoms.next()){
					StringBuffer sb =new StringBuffer();
					Map<String, Object> sonMap=sonBomturnMake(id,sonBoms.getGeneralInt("mm_bomid"),sonBoms.getGeneralDouble("mm_qty"),1,rcode,sb,c);
					c=Integer.parseInt(sonMap.get("count").toString());
					res.append(sonMap.get("res").toString());
				}
			}
		}
		return res;
	}
	/**
	 * 
	 * @param maid 母件制造单id
	 * @param bomid Bomid
	 * @param sdid  订单明细id
	 * @param n     层级
	 * @param c     生成数量
	 */
	private Map<String, Object> sonBomturnMake(int maid,int bomid,Double qty,int n,String rcode,StringBuffer sb,int c){
		Map<String, Object> map = new HashMap<String, Object>();
		if(n<15){
			SqlRowList rs = queryForRowSet("select CASE WHEN nvl(pr_manutype,' ')='MAKE' THEN 'MAKE' WHEN nvl(pr_manutype,' ')='OSMAKE' THEN 'OS' END pr_manutype,"
					+ "pr_id,pr_code,pr_whcode,pr_gdtqq,pr_plzl,pr_leadtime,bo_wccode "
					+ "from bom left join product on pr_code=bo_mothercode where bo_id=?", new Object[] { bomid });
			if (rs.next()) {
				int pr_gdtqq_topbom=0;
				int	plzl_day_topbom=0;
				int	leadtime_topbom=0;
				SqlRowList topbom = queryForRowSet("SELECT pr_gdtqq,pr_plzl,nvl(pr_leadtime,0) pr_leadtime,mm_qty FROM MAKEMATERIAL left join product on pr_code =mm_prodcode"
						+ " WHERE MM_MAID="+maid+" AND MM_BOMID=(SELECT MM_TOPBOMID FROM MAKEMATERIAL LEFT JOIN MAKE ON MM_MaID=ma_id WHERE MM_MAID="+maid+
						" AND MM_BOMID="+bomid+" AND MM_TOPBOMID<>MA_BOMID) and mm_materialstatus='JUMP'");
				if(topbom.next()){
					pr_gdtqq_topbom=topbom.getGeneralInt("pr_gdtqq");
					if(topbom.getGeneralInt("pr_plzl")!=0){
						plzl_day_topbom=(int) Math.ceil(topbom.getGeneralInt("mm_qty")/topbom.getGeneralInt("pr_plzl"));
					}
					leadtime_topbom=topbom.getGeneralInt("pr_leadtime");
				}
				int id = getSeqId("MAKE_SEQ");
				String code ="";
				if (c<10){
					code=rcode+"-0"+c;
				} else{
					code=rcode+"-"+c;
				}
				Timestamp time = Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS));
				Employee employee = SystemSession.getUser();
				Object[] makeInfo= getFieldsDataByCondition("make","ma_saledetailid,ma_salecode,ma_saledetno,ma_pocode,ma_planbegindate,"
						+ "ma_custcode,ma_custname,ma_bonded,ma_cop,ma_factory","ma_id="+maid);
				int  leadtime=rs.getGeneralInt("pr_leadtime");
				int  pr_gdtqq=rs.getGeneralInt("pr_gdtqq");
				int  plzl_day=0;
				if(rs.getGeneralInt("pr_plzl")!=0){
					plzl_day=(int) Math.ceil(qty/rs.getGeneralInt("pr_plzl"));
				}
				Date requiredate=DateUtil.overDate(DateUtil.parseStringToDate(makeInfo[4].toString(), Constant.YMD_HMS), -1-pr_gdtqq_topbom-plzl_day_topbom-leadtime_topbom);
				Date enddate = DateUtil.overDate(requiredate,-pr_gdtqq);
				if (enddate.compareTo(new Date()) < 0) {
					enddate = new Date();
				}
				
				Date begindate =DateUtil.overDate(enddate,0-(leadtime+plzl_day));
				if (begindate.compareTo(new Date()) < 0) {
					begindate = new Date();
				}
				Object makekind=null;
				String caller="",type="";
				if("MAKE".equals(rs.getObject("pr_manutype"))){
					caller="Make!Base";
					type="制造单";
					makekind=getFieldDataByCondition("MAKEKIND","max(MK_NAME)", "mk_isuse='-1' and mk_makind='MAKE' and mk_ifmrpkind='-1'");
				}else if("OS".equals(rs.getObject("pr_manutype"))){
					caller="Make";
					type="委外加工单";
					makekind=getFieldDataByCondition("MAKEKIND","max(MK_NAME)", "mk_isuse='-1' and mk_makind='OSMAKE' and mk_ifmrpkind='-1'");
				};
				if(makekind==null){
					makekind="标准工单";
				}
				boolean bool = execute(
						INSERTMAKE,
						new Object[] { id, code, time, BaseUtil.getLocalMessage("AUDITED"), "AUDITED",time, employee.getEm_id(),
								employee.getEm_name(), rs.getObject("pr_manutype"),makeInfo[0],makeInfo[1],makeInfo[2],
								makeInfo[3],Timestamp.valueOf(DateUtil.parseDateToString(requiredate, Constant.YMD_HMS)),makeInfo[5],makeInfo[6],rs.getObject("pr_id"),rs.getObject("pr_code"),makeInfo[7],"Sale",
								rs.getObject("pr_whcode"),BaseUtil.getLocalMessage("UNPRINT"), "UNPRINT",bomid,0 ,rs.getObject("bo_wccode"),
								"订单下推",qty,BaseUtil.getLocalMessage("UNGET"),"UNGET",makeInfo[8],makekind,BaseUtil.getLocalMessage("UNAPPROVED"),
								"UNAPPROVED",BaseUtil.getLocalMessage("UNCOMPLET"),"UNCOMPLET",
								Timestamp.valueOf(DateUtil.parseDateToString(begindate, Constant.YMD_HMS)),
								Timestamp.valueOf(DateUtil.parseDateToString(enddate, Constant.YMD_HMS)),makeInfo[9]});
				if (bool) {
					c++;
					map.put("res", sb);
					map.put("count", c);
					MakeBaseService makeBaseService=(MakeBaseService) ContextUtil.getBean("makeBaseService");
					makeBaseService.setMakeMaterial(code, caller);
					Object onhand=getFieldDataByCondition("productonhand", "po_onhand", "po_prodcode='"+rs.getObject("sd_prodcode")+"'");
					sb.append(type + "号:"
							+ "<a href=\"javascript:openUrl('jsps/pm/make/makeBase.jsp?whoami="+caller+"&formCondition=ma_idIS" + id
							+ "&gridCondition=mm_maidIS" + id +"')\">"+code + "</a>&nbsp;产品库存数:"+(onhand==null?0:onhand.toString())+"&nbsp;<hr>");
					SqlRowList sonBoms = queryForRowSet("select mm_bomid,mm_qty from makematerial left join product on mm_prodcode=pr_code where mm_maid=? "
							+ "and nvl(pr_manutype,' ') in('MAKE','OSMAKE') and nvl(pr_dhzc,' ')='MPS' and NVL(MM_MATERIALSTATUS,' ')<>'JUMP' AND NVL(MM_BOMID,0)>0", id);
					while(sonBoms.next()){
						Map<String, Object> sonMap=sonBomturnMake(id,sonBoms.getGeneralInt("mm_bomid"),sonBoms.getGeneralDouble("mm_qty"),n+1,rcode,sb,c);
						c=Integer.parseInt(sonMap.get("count").toString())+1;
						map.put("count", c);
						map.put("res", sb);
					}
				}
			}
		}
		return map;
	}
	/**
	 * 到价格表取销售单价(包括销售类型)
	 * 
	 * @return {JSONObject} {sd_price: 0.00,sd_taxrate: 0.00}
	 */
	@Override
	public JSONObject getSalePrice(String custcode, String prodcode, String currency, String kind) {
		SqlRowList rs = queryForRowSet(SALE_PRICE, custcode, prodcode, currency, kind);
		if (rs.next()) {
			JSONObject obj = new JSONObject();
			obj.put("sd_price", rs.getObject("spd_price"));
			obj.put("sd_taxrate", rs.getObject("spd_taxrate"));
			return obj;
		}
		return null;
	}

	/**
	 * 到价格表取销售单价 CCP:客户+币别+料号 KCP:客户类型+币别+料号 PC :料号+币别
	 * 
	 * @return {JSONObject} {sd_price: 0.00,sd_taxrate: 0.00}
	 */
	@Override
	public JSONObject getSalePrice_N(String custcode, String sakind, String prodcode, String currency, String cukind, Object pricekind,
			Double sumqty, Double taxrate) {
		SqlRowList rs = null;
		/**
		 * 反馈编号：2017030117
		 * 西博泰科UAS:销售类型-取价原则，增加一种原则PCR:“料号+币别+税率”;
		 */
		if (pricekind != null) {
			if ("CCP".equals(pricekind)) {
				rs = queryForRowSet(SALE_PRICE_CCP, custcode, prodcode, currency, sumqty);
			} else if ("CCPR".equals(pricekind)) {
				rs = queryForRowSet(SALE_PRICE_CCPR, custcode, prodcode, currency, taxrate, sumqty);
			} else if ("KCP".equals(pricekind)) {
				rs = queryForRowSet(SALE_PRICE_KCP, prodcode, currency, cukind, sumqty);
			} else if ("PC".equals(pricekind)) {
				rs = queryForRowSet(SALE_PRICE_PC, prodcode, currency, sumqty);
			} else if ("SCP".equals(pricekind)) {
				rs = queryForRowSet(SALE_PRICE_SCP, sakind, prodcode, currency, sumqty);
			} else if ("PCR".equals(pricekind)) {
				rs = queryForRowSet(SALE_PRICE_PCR,prodcode, currency, taxrate,sumqty);
			}
		}
		if (rs.next()) {
			JSONObject obj = new JSONObject();
			obj.put("sd_price", rs.getObject("spd_price") == null ? 0 : rs.getObject("spd_price"));
			obj.put("sd_taxrate", rs.getObject("spd_taxrate") == null ? 0 : rs.getObject("spd_taxrate"));
			return obj;
		}
		return null;
	}

	public JSONObject getSalePrice(String sa_code, int sd_detno) {
		SqlRowList rs = queryForRowSet(SALE_PRICE_S, sa_code, sd_detno);
		if (rs.next()) {
			JSONObject obj = new JSONObject();
			obj.put("sd_price", rs.getObject("spd_price"));
			obj.put("sd_taxrate", rs.getObject("spd_taxrate"));
			return obj;
		}
		return null;
	}

	public void getPrice(String sa_code) {
		Object sa_id = getFieldDataByCondition("Sale", "sa_id", "sa_code='" + sa_code + "'");
		if (sa_id != null) {
			getPrice(Integer.parseInt(sa_id.toString()));
		}
	}

	@Override
	public String getPrice(int sa_id) {
		StringBuffer error = new StringBuffer();
		Object sakind = getFieldDataByCondition("Sale", "sa_kind", "sa_id=" + sa_id);
		Object pricekind = getFieldDataByCondition("SaleKind", "sk_pricekind", "sk_name='" + sakind + "'");
		Object allowzero = getFieldDataByCondition("SaleKind", "nvl(sk_allowzero,0)", "sk_name='" + sakind + "'");
		List<Object[]> objects = getFieldsDatasByCondition(
				"saledetail left join sale on sa_id=sd_said left join Customer on sa_custcode=cu_code", new String[] { "sd_prodcode",
						"sa_custcode", "sa_currency", "sd_qty", "sd_id", "cu_pricetype", "nvl(sd_price,0)", "nvl(sd_taxrate,0)","nvl(sd_purcprice,0)" }, " sd_said="
						+ sa_id);
		JSONObject js = null;
		if(isDBSetting("Sale","getPriceBySale")){
			for (Object[] obj : objects) {
				Double a = 0.0;
				Double b = 0.0;
				if(Double.parseDouble(obj[8].toString()) == 0.0){
					Object[] last = getFieldsDataByCondition("(select * from sale left join saledetail on sa_id=sd_said where sa_custcode='"+obj[1]+"' and sa_currency='"+obj[2]+"' and sd_prodcode='"+obj[0]+"' and sa_statuscode='AUDITED' order by sa_date desc)", new String[]{"nvl(sd_price,0)","nvl(sd_taxrate,0)"}, "rownum<2");
					if(last != null){
						a = last[0] == null ? 0.0 : Double.parseDouble(last[0].toString());
						b = last[1] == null ? 0.0 : Double.parseDouble(last[1].toString());
						execute("update saledetail set sd_purcprice="+a+" where sd_id="+obj[4]);
						if(Double.parseDouble(obj[6].toString()) == 0.0){
							execute("update saledetail set sd_price="+a+" where sd_id="+obj[4]);
						}
						if(Integer.parseInt(obj[7].toString())== 0){
							execute("update saledetail set sd_taxrate="+b+" where sd_id="+obj[4]);
						}
					}
				}
			}
		}else{
			for (Object[] obj : objects) {
				Object oqty = getFieldDataByCondition("SaleDetail", "sum(sd_qty)",
						" sd_said=" + sa_id + " and sd_prodcode='" + String.valueOf(obj[0]) + "'");
				/**
				 * 西博泰科UAS:销售类型-取价原则，增加一种原则PCR:“料号+币别+税率”;
				 */
				if (pricekind == null
						|| (!"CCPR".equals(pricekind) && !"CCP".equals(pricekind) && !"KCP".equals(pricekind) && !"PC".equals(pricekind) && !"SCP"
								.equals(pricekind)&&!"PCR".equals(pricekind))) {
					return null;
				} else
					js = getSalePrice_N(String.valueOf(obj[1]), String.valueOf(sakind), String.valueOf(obj[0]), String.valueOf(obj[2]),
							String.valueOf(obj[5]), pricekind, Double.parseDouble(oqty.toString()), Double.parseDouble(obj[7].toString()));
				double price = 0;
				double tax = 0;
				double p = 0;
				if (js != null) {
					price = js.getDouble("sd_price");
					tax = js.getDouble("sd_taxrate");
				}
				Boolean forceGetPrice=isDBSetting("Sale", "forceGetPrice");//强制按照销售价格表获取的价格更新
				if (price != 0) {
					p = price;
					if (Integer.parseInt(String.valueOf(allowzero)) == 0) {
						if(forceGetPrice){
							updateByCondition("saleDetail", "sd_price=" + p, "sd_id=" + obj[4]);
						}else{
							updateByCondition("saleDetail", "sd_price=" + p, "sd_id=" + obj[4] + "  and nvl(sd_price,0)=0");
						}
					} else {
						if(forceGetPrice){
							updateByCondition("saleDetail", "sd_price=" + p, "sd_id=" + obj[4]+"  and nvl(sd_isspecial,0)=0 ");
						}else{
							updateByCondition("saleDetail", "sd_price=" + p, "sd_id=" + obj[4] + " and nvl(sd_isspecial,0)=0 and nvl(sd_price,0)=0");
					
						}
					}
					updateByCondition("saleDetail", "sd_purcprice=" + p + ",sd_taxrate=" + tax + ",sd_costprice=round(sd_price/"
							+ (tax / 100 + 1) + ",8)" + ",sd_taxtotal=round(sd_price*sd_qty/" + (tax / 100 + 1)
							+ ",2),sd_total=round(sd_price*sd_qty,2)", "sd_id=" + obj[4]);
				} else {
					error.append("根据 物料编号:[" + obj[0] + "],客户编号:[" + obj[1] + "],币别:[" + obj[2] + "],分段数量：[" + oqty
							+ "] 在销售单价表未找到对应单价，或单价为空值、0等!<BR/>");
				}
			}
		}
		if (error.length() > 0) {
			return error.toString();
		}
		return null;
	}

	@Override
	public void updateSaleTotal(String sa_code) {
		// 修改主单据的总金额
		execute("update saledetail set sd_total=trunc(nvl(sd_price,0)*nvl(sd_qty,0), 2) where sd_code=?", sa_code);
		execute("update saledetail set sd_costprice=trunc(nvl(sd_price,0)/(1+nvl(sd_taxrate,0)/100),6),"
				+ "sd_taxtotal=trunc(nvl(sd_costprice,0)*nvl(sd_qty,0), 2) where sd_code=?", sa_code);
		execute("update sale set sa_total=trunc((select sum(sd_total) from saledetail where sd_said=sa_id),2),sa_totalupper="
				+ "L2U(sa_total) where sa_code=?", sa_code);
	}

	static final String CHECK_YQTY = "SELECT sd_code,sd_detno,sd_qty FROM SaleDetail WHERE sd_id=? and sd_qty<?";

	/**
	 * 销售订单转入出货通知单之前，判断thisqty ≤ qty - yqty
	 */
	@Override
	public void checkAdYqty(List<Map<Object, Object>> datas) {
		int id = 0;
		Object y = 0;
		Object r = 0;
		SqlRowList rs = null;
		boolean bool = false;
		Object[] sas = null;
		for (Map<Object, Object> d : datas) {
			id = Integer.parseInt(d.get("sd_id").toString());
			sas = getFieldsDataByCondition("SaleDetail left join Sale on sd_said=sa_id", "sa_code,sd_detno", "sd_id=" + id);
			if (sas != null) {
				bool = checkIf("Sale", "sa_code='" + sas[0] + "' and sa_statuscode='AUDITED'");
				if (!bool) {
					BaseUtil.showError("销售订单:" + sas[0] + " 未审核通过,无法转通知单!");
				}
				y = getFieldDataByCondition("SendNotifyDetail left join SendNotify on snd_snid=sn_id", "sum(nvl(snd_outqty,0))",
						"snd_sdid=" + id + " and snd_statuscode<>'FINISH'");
				r = getFieldDataByCondition("ProdIODetail left join ProdInOut on pd_piid=pi_id", "sum(pd_inqty)",
						"pd_piclass='销售退货单' and pi_statuscode='POSTED' and pd_ordercode='" + sas[0] + "' and pd_orderdetno=" + sas[1]);
				y = y == null ? 0 : y;
				r = r == null ? 0 : r;
				rs = queryForRowSet(
						CHECK_YQTY,
						id,
						NumberUtil.formatDouble(
								Double.parseDouble(y.toString()) + Double.parseDouble(d.get("sd_tqty").toString())
										- Double.parseDouble(r.toString()), 6));
				if (rs.next()) {
					StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],销售单号:").append(rs.getString("sd_code")).append(",行号:")
							.append(rs.getInt("sd_detno")).append(",订单数:").append(rs.getDouble("sd_qty")).append(",通知单数:").append(y)
							.append(",销售退货数:").append(r).append(",本次数:").append(d.get("sd_tqty"));
					BaseUtil.showError(sb.toString());
				}
			}
		}
	}

	@Override
	public String turnProdInOutCustomer(String caller, List<Map<Object, Object>> maps) {
		Employee employee = SystemSession.getUser();
		int veid = getSeqId("QUA_VERIFYAPPLYDETAIL_SEQ");
		String code = null;
		for (Map<Object, Object> map : maps) {
			SqlRowList rs = queryForRowSet(GET_SALEDETAIL, new Object[] { map.get("sd_id") });
			if (rs.next()) {
				float qty = Float.parseFloat(map.get("sd_thisoqcqty").toString());
				code = sGetMaxNumber("VerifyApplyDetailOQC", 2);
				execute(insert_QuaVerify,
						new Object[] { veid, code, rs.getString("sa_code"), rs.getInt("sd_detno"), rs.getString("sd_prodcode"),
								rs.getString("sa_custcode"), rs.getString("sa_custname"), qty,
								Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), BaseUtil.getLocalMessage("ENTERING"),
								"ENTERING", "OQC", "客户验货单", employee.getEm_name() });
				updateByCondition("SaleDetail", "sd_oqcyqty=nvl(sd_oqcyqty,0)+" + qty, "sd_id=" + map.get("sd_id"));
				// 记录日志
				logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.VerifyApplyDetailOQC"),
						BaseUtil.getLocalMessage("msg.turnSuccess") + BaseUtil.getLocalMessage("msg.detail") + rs.getInt("sd_detno")
								+ BaseUtil.getLocalMessage("msg.qty.out") + "+" + qty, "Sale|sa_id=" + rs.getInt("sa_id")));
			}
		}
		return "转入成功,客户验货单号:<a href=\"javascript:openUrl('jsps/scm/qc/verifyApplyDetailOQC.jsp?formCondition=ve_idIS" + veid
				+ "&whoami=VerifyApplyDetailOQC')\">" + code + "</a>&nbsp;";
	}

	@Override
	public JSONObject getSalePuPrice(String cukind, String prodcode, String currency, double sumqty) {
		SqlRowList rs = queryForRowSet(GETSALEPUPRICE, cukind, prodcode, currency, sumqty);
		if (rs.next()) {
			JSONObject obj = new JSONObject();
			obj.put("sd_purcprice", rs.getObject("spd_price") == null ? 0 : rs.getObject("spd_price"));
			obj.put("sd_ratio", rs.getObject("spd_ratio") == null ? 0 : rs.getObject("spd_ratio"));
			return obj;
		}
		return null;
	}

	@Override
	public void udpatestatus(int sdid) {
		Object said = getFieldDataByCondition("SaleDetail", "sd_said", "sd_id=" + sdid);
		int total = getCountByCondition("SaleDetail", "sd_said=" + said);
		int aud = getCountByCondition("SaleDetail", "sd_said=" + said + " AND nvl(sd_sendqty,0)=0");
		int turn = getCountByCondition("SaleDetail", "sd_said=" + said + " AND nvl(sd_sendqty,0)=nvl(sd_qty,0)");
		String status = "PARTOUT";
		if (aud == total) {
			status = "";
		} else if (turn == total) {
			status = "TURNOUT";
		}
		updateByCondition("Sale", "sa_sendstatuscode='" + status + "',sa_sendstatus='" + BaseUtil.getLocalMessage(status) + "'", "sa_id="
				+ said);
	}

	static final String CHECKYQTY = "SELECT sd_code,sd_detno,sd_qty FROM SaleDetail LEFT JOIN Sale on sa_id=sd_said WHERE sd_id=? and sd_qty<?";

	@Override
	public void checkYqty(List<Map<Object, Object>> datas, String piclass) {
		int id = 0;
		Object y = 0;
		Object r = 0;
		SqlRowList rs = null;
		Object[] sas = null;
		for (Map<Object, Object> d : datas) {
			id = Integer.parseInt(d.get("sd_id").toString());
			sas = getFieldsDataByCondition("SaleDetail left join Sale on sd_said=sa_id", "sa_code,sd_detno", "sd_id=" + id);
			if (sas != null) {
				y = getFieldDataByCondition("ProdIODetail", "sum(nvl(pd_outqty,0))", "pd_piclass='" + piclass + "' and pd_sdid=" + id);
				r = getFieldDataByCondition("ProdIODetail left join ProdInOut on pd_piid=pi_id", "sum(pd_inqty)",
						"pd_piclass='销售退货单' and pi_statuscode='POSTED' and pd_ordercode='" + sas[0] + "' and pd_orderdetno=" + sas[1]);
				y = y == null ? 0 : y;
				r = r == null ? 0 : r;
				rs = queryForRowSet(CHECKYQTY, id, Double.parseDouble(y.toString()) + Double.parseDouble(d.get("sd_tqty").toString())
						- Double.parseDouble(r.toString()));
				if (rs.next()) {
					StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],订单号:").append(rs.getString("sd_code")).append(",行号:")
							.append(rs.getInt("sd_detno")).append(",订单数:").append(rs.getDouble("sd_qty")).append(",已转" + piclass + "数:")
							.append(y).append(",销售退货单数:").append(r).append(",本次数:").append(d.get("sd_tqty"));
					BaseUtil.showError(sb.toString());
				}
			}
		}
	}
}
