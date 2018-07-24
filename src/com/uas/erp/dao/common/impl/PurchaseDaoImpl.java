package com.uas.erp.dao.common.impl;
 
import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.MoneyUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlMap;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ApplicationDao;
import com.uas.erp.dao.common.PurchaseDao;
import com.uas.erp.model.Employee;

@Repository
public class PurchaseDaoImpl extends BaseDao implements PurchaseDao {
	@Autowired
	private ApplicationDao applicationDao;
	/**
	 * @author wsy
	 * 双单位
	 */
	final static String PURCHASE_PRICE = "select ppd_price,ppd_rate,ppd_id,ppd_rebatesprice from PurchasePriceDetail left join PurchasePrice on ppd_ppid=pp_id left join product on ppd_prodcode=pr_code where ppd_vendcode=? and ppd_prodcode=? and ppd_currency=? and pp_kind like '%@kind' and to_char(ppd_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID' and ppd_lapqty<=round(?/case when nvl(pr_purcrate,0)=0 then 1 else pr_purcrate end,2) order by ppd_price ";
	final static String PURCHASE_PRICE_APPSTATUS = "select ppd_price,ppd_rate,ppd_id,ppd_rebatesprice from PurchasePriceDetail left join PurchasePrice on ppd_ppid=pp_id left join product on ppd_prodcode=pr_code where ppd_vendcode=? and ppd_prodcode=? and ppd_currency=? and pp_kind like '%@kind' and to_char(ppd_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID' and (nvl(ppd_appstatus,' ')='合格' or pr_material<>'已认可') and ppd_lapqty<=round(?/case when nvl(pr_purcrate,0)=0 then 1 else pr_purcrate end,2) order by ppd_price ";
	static final String INSERTVERIFYAPPLYBASE = "INSERT INTO verifyapply(va_id, va_code, va_statuscode, va_status, va_recorder,va_indate,va_date,va_class) values (?,?,?,?,?,?,?,?)";

	final static String INSERT_PRODIO_VEND = "INSERT INTO ProdInOut(pi_id, pi_inoutno, pi_date, pi_class, pi_invostatus, pi_invostatuscode,pi_recordman, pi_recorddate, pi_cardcode,pi_title"
			+ ", pi_cardid, pi_status, pi_statuscode,pi_updatedate,pi_updateman,pi_printstatus,pi_printstatuscode,pi_currency,pi_payment,pi_rate,pi_receivecode,pi_receivename) VALUES "
			+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static String PU_TOTAL = "update Purchase set pu_total=(select sum(pd_total) from PurchaseDetail where PurchaseDetail.pd_puid = Purchase.pu_id) where pu_id=?";
	final static String PU_TAXTOTAL = "update Purchase set pu_taxtotal=(select sum(pd_taxtotal) from PurchaseDetail where PurchaseDetail.pd_puid = Purchase.pu_id) where pu_id=?";
	final static String PU_PURCVEND = "update PURCHASEDETAIL set (pd_purcvendcode,pd_purcvendname,pd_purccurrency,pd_purctaxrate,pd_purcprice)=(SELECT ppd_purcvendcode,ppd_purcvendname,ppd_purccurrency,ppd_purctaxrate,ppd_purcprice from purchasepricedetail where ppd_id=pd_ppdid) where pd_puid=? and nvl(pd_ppdid,0)<>0";

	final static String PRICE_VENDOR = "select round(min(ppd_price*(1-nvl(ppd_rate,0)/(100+nvl(ppd_rate,0)))*cr_rate),8) as price,ppd_vendcode from PurchasePriceDetail left join PurchasePrice on ppd_ppid=pp_id left join currencys on ppd_currency=cr_name  where  ppd_prodcode=? and pp_kind=? and to_char(ppd_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID' and ppd_lapqty<=? group by ppd_vendcode order by price ";
	final static String PRICE_VENDOR_APPSTATUS = "select round(min(ppd_price*(1-nvl(ppd_rate,0)/(100+nvl(ppd_rate,0)))*cr_rate),8) as price,ppd_vendcode from PurchasePriceDetail left join PurchasePrice on ppd_ppid=pp_id left join currencys on ppd_currency=cr_name left join product on ppd_prodcode=pr_code  where  ppd_prodcode=? and pp_kind=? and to_char(ppd_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID' and (nvl(ppd_appstatus,' ')='合格' or pr_material<>'已认可') and ppd_lapqty<=? group by ppd_vendcode order by price ";
	final static String PRICE_VENDOR2 = "select * from (SELECT ppd_vendcode,ppd_rate,cr_rate,ppd_currency,ppd_price,ppd_price * cr_rate * (1 - ppd_rate / (100 + ppd_rate)) as price,rank() over (PARTITION BY ppd_prodcode order by (ppd_price * cr_rate * (1 - ppd_rate / (100 + ppd_rate))) asc ,ppd_id desc) mm,ppd_id FROM PurchasePriceDetail left join PurchasePrice on ppd_ppid=pp_id left join currencys on ppd_currency=cr_name WHERE ppd_prodcode =? and pp_kind like '%@kind' and ppd_lapqty<=? and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID'  )  where mm=1  order by price";
	final static String PRICE_VENDOR_APPSTATUS2 = "select * from (select * from (SELECT ppd_vendcode,ppd_rate,cr_rate,ppd_currency,ppd_price,ppd_price * cr_rate * (1 - ppd_rate / (100 + ppd_rate)) as price,rank() over (PARTITION BY ppd_prodcode order by (ppd_price * cr_rate * (1 - ppd_rate / (100 + ppd_rate))) asc ,ppd_id desc) mm,ppd_id FROM PurchasePriceDetail left join PurchasePrice on ppd_ppid=pp_id left join currencys on ppd_currency=cr_name left join product on ppd_prodcode=pr_code WHERE ppd_prodcode =? and pp_kind like '%@kind' and ppd_lapqty<=? and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID' and (nvl(ppd_appstatus,' ')='合格' or pr_material<>'已认可')) )  where mm=1  order by price";
	
	final static String PRICE_VENDOR_CHECK="select * from (SELECT ppd_vendcode,ppd_rate,cr_rate,ppd_currency,ppd_price,ppd_price * cr_rate * (1 - ppd_rate / (100 + ppd_rate)) as price,rank() over (PARTITION BY ppd_prodcode order by (ppd_price * cr_rate * (1 - ppd_rate / (100 + ppd_rate))) asc ,case when ppd_vendcode=? then 0 else 1 end asc ,ppd_id desc) mm,ppd_id FROM PurchasePriceDetail left join PurchasePrice on ppd_ppid=pp_id left join currencys on ppd_currency=cr_name WHERE ppd_prodcode =? and pp_kind like '%@kind' and ppd_lapqty<=? and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID'  )  where mm=1  order by price";
	final static String PRICE_VENDOR_CHECK_APPSTATUS = "select * from (select * from (SELECT ppd_vendcode,ppd_rate,cr_rate,ppd_currency,ppd_price,ppd_price * cr_rate * (1 - ppd_rate / (100 + ppd_rate)) as price,rank() over (PARTITION BY ppd_prodcode order by (ppd_price * cr_rate * (1 - ppd_rate / (100 + ppd_rate))) asc ,case when ppd_vendcode=? then 0 else 1 end asc ,ppd_id desc) mm,ppd_id FROM PurchasePriceDetail left join PurchasePrice on ppd_ppid=pp_id left join currencys on ppd_currency=cr_name left join product on ppd_prodcode=pr_code WHERE ppd_prodcode =? and pp_kind like '%@kind' and ppd_lapqty<=? and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID' and (nvl(ppd_appstatus,' ')='合格' or pr_material<>'已认可')) )  where mm=1  order by price";
	
	/**
	 * 修改采购单收料状态
	 */
	@Override
	public void udpatestatus(int pdid) {
		Object puid = getFieldDataByCondition("PurchaseDetail", "pd_puid", "pd_id=" + pdid);
		int total = getCountByCondition("PurchaseDetail", "pd_puid=" + puid);
		int aud = getCountByCondition("PurchaseDetail", "pd_puid=" + puid + " AND nvl(pd_yqty,0)=0");
		int turn = getCountByCondition("PurchaseDetail", "pd_puid=" + puid + " AND nvl(pd_yqty,0)=nvl(pd_qty,0)");
		String status = "PART2VA";
		if (aud == total) {
			status = "";
		} else if (turn == total) {
			status = "TURNVA";
		}
		updateByCondition("Purchase", "pu_acceptstatuscode='" + status + "',pu_acceptstatus='" + BaseUtil.getLocalMessage(status) + "'",
				"pu_id=" + puid);
	}

	/**
	 * 修改采购单入库状态
	 */
	@Override
	public void udpateturnstatus(int pdid) {
		Object puid = getFieldDataByCondition("PurchaseDetail", "pd_puid", "pd_id=" + pdid);
		int total = getCountByCondition("PurchaseDetail", "pd_puid=" + puid);
		int aud = getCountByCondition("PurchaseDetail", "pd_puid=" + puid + " AND nvl(pd_acceptqty,0)=0");
		int turn = getCountByCondition("PurchaseDetail", "pd_puid=" + puid + " AND nvl(pd_acceptqty,0)=nvl(pd_qty,0)");
		String status = "PART2IN";
		if (aud == total) {
			status = "";
		} else if (turn == total) {
			status = "TURNIN";
		}
		updateByCondition("Purchase", "pu_turnstatuscode='" + status + "',pu_turnstatus='" + BaseUtil.getLocalMessage(status) + "'",
				"pu_id=" + puid);
	}

	/**
	 * 更新上次采购价格、供应商
	 */
	@Override
	public void updatePrePurchase(String pu_code, String pu_date) {
		String sql = "SELECT * FROM PurchaseDetail WHERE pd_code=" + pu_code + " AND pd_price=0";
		SqlRowList rs1 = queryForRowSet(sql);
		while (rs1.next()) {
			sql = "SELECT PurchaseDetail.pd_price*PurchaseDetail.pd_rate,PurchaseDetail.pd_vendname,PurchaseDetail.pd_code from PurchaseDetail"
					+ ",Purchase WHERE pu_code<>'"
					+ pu_code
					+ "' and pu_date<='"
					+ pu_date
					+ "' and pd_prodcode='"
					+ rs1.getObject("pd_prodcode")
					+ "' and (pu_statuscode='56' or pu_statuscode='114' or pu_statuscode='115') order by pu_date desc ";
			SqlRowList rs2 = queryForRowSet(sql);
			if (rs2.next()) {
				sql = "UPDATE purchaseDetail SET pd_preprice=" + rs2.getObject(1) + ",pd_prevendor='" + rs2.getObject(2) + "',pd_precode='"
						+ rs2.getObject(3) + "' where pd_code='" + pu_code + "' and pd_detno=" + rs1.getObject("pd_detno");
				execute(sql);
			}
		}
	}

	/**
	 * 更新采购计划下达数\本次下达数\状态
	 */
	@Override
	public void updatePurchasePlan(int pu_id) {
		String sql = "UPDATE PurchasePlan SET pp_endqty=(SELECT sum(pd_qty) from purchasedetail WHERE pd_ppid=pp_id) WHERE pp_id in (SELECT pd_ppid FROM PurchaseDetail WHERE pd_puid="
				+ pu_id + ")";
		execute(sql);
		sql = "UPDATE PurchasePlan SET pp_thisqty=nvl(pp_planqty,0)-nvl(pp_endqty,0) WHERE pp_id IN (SELECT pd_ppid FROM PurchaseDetail WHERE pd_puid="
				+ pu_id + ")";
		execute(sql);
		sql = "UPDATE PurchasePlan SET pp_status='未下达' WHERE pp_endqty<pp_planqty and pp_id IN (SELECT pd_ppid FROM PurchaseDetail WHERE pd_puid="
				+ pu_id + ")";
		execute(sql);
	}

	/**
	 * 下达数不能超计划数量检测
	 */
	@Override
	public String checkPlanQty(int pu_id) {
		String sql = "SELECT * FROM  purchaseplan WHERE pp_endqty>pp_planqty AND pp_id IN (SELECT pd_ppid FROM purchasedetail where pd_puid="
				+ pu_id + ")";
		SqlRowList set = queryForRowSet(sql);
		StringBuffer sb = new StringBuffer();
		while (set.next()) {
			sb.append("{pp_prodcode:\"" + set.getGeneralString("pp_prodcode"));
			sb.append("\",pp_endqty:" + set.getInt("pp_endqty"));
			sb.append(",pp_planqty:" + set.getInt("pp_planqty"));
			sb.append(",pp_id:" + set.getInt("pp_id") + "}");
			sb.append(",");
		}
		return sb.length() > 0 ? sb.toString().substring(0, sb.lastIndexOf(",") - 1) : null;
	}

	/**
	 * 计划ID料号错位检测
	 */
	@Override
	public String checkPPcode(int pu_id) {
		String sql = "SELECT * FROM  purchasedetail LEFT JOIN purchaseplan ON pp_id=pd_ppid WHERE pd_puid=" + pu_id
				+ " AND pd_prodcode<>pp_prodcode AND pd_ppid>0";
		SqlRowList set = queryForRowSet(sql);
		StringBuffer sb = new StringBuffer();
		while (set.next()) {
			sb.append("{pd_detno:" + set.getGeneralString("pd_detno"));
			sb.append(",pp_prodcode:\"" + set.getInt("pp_prodcode"));
			sb.append("\"},");
		}
		return sb.length() > 0 ? sb.toString().substring(0, sb.lastIndexOf(",") - 1) : null;
	}

	@Override
	public int newVerifyApply() {
		Employee employee = SystemSession.getUser();
		int vaid = getSeqId("VERIFYAPPLY_SEQ");
		String code = sGetMaxNumber("VerifyApply", 2);
		Timestamp time = Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS));
		boolean bool = execute(INSERTVERIFYAPPLYBASE,
				new Object[] { vaid, code, "ENTERING", BaseUtil.getLocalMessage("ENTERING"), employee.getEm_name(), time, time, "采购收料单" });
		if (bool)
			return vaid;
		return 0;
	}

	public String newVerifyApplyRetCode(Employee employee) {
		int vaid = getSeqId("VERIFYAPPLY_SEQ");
		String code = sGetMaxNumber("VerifyApply", 2);
		Timestamp time = Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS));
		boolean bool = execute(INSERTVERIFYAPPLYBASE,
				new Object[] { vaid, code, "ENTERING", BaseUtil.getLocalMessage("ENTERING"), employee.getEm_name(), time, time, "采购收料单" });
		if (bool)
			return code;
		return null;
	}

	/**
	 * 判断当前采购单所有明细的状态 判断采购明细是否都是{status}状态
	 * 
	 * @return false 部分状态不是{status} true 所有明细的状态都是{status}
	 */
	public boolean checkPdStatus(int puid, String status) {
		SqlRowList rs = queryForRowSet("SELECT pd_status FROM PurchaseDetail where pd_puid=" + puid);
		boolean bool = true;
		while (rs.next()) {
			String s = rs.getGeneralString(1);
			if (s == null || !s.equals(status)) {
				bool = false;
				break;
			}
		}
		return bool;
	}

	@Override
	@Transactional
	public void deletePurchase(int id) {
		List<Object[]> objs = getFieldsDatasByCondition("PurchaseDetail", new String[] { "pd_id", "pd_qty" }, "pd_puid=" + id);
		for (Object[] obj : objs) {
			if (Float.parseFloat(obj[1].toString()) > 0) {
				// 还原请购明细及请购单
				restoreApplication(Integer.parseInt(obj[0].toString()));
			}
			deleteByCondition("PurchaseDetail", "pd_id=" + obj[0]);
		}
	}

	/**
	 * 采购单删除时，修改请购单状态、数量等
	 */
	public void restoreApplication(int pdid) {
		Object[] objs = getFieldsDataByCondition("PurchaseDetail", new String[] { "pd_sourcedetail", "pd_qty", "pd_source",
				"NVL(pd_mmid,0)", "NVL(pd_mtid,0)" }, "pd_id=" + pdid);
		if (objs != null && objs[2] != null) {
			updateByCondition("ApplicationDetail", "ad_yqty=nvl(ad_yqty,0)-" + Double.parseDouble(objs[1].toString()), "ad_id=" + objs[0]);
			updateByCondition("MRPReplace", "mr_purcqty=nvl(mr_purcqty,0)-" + Float.parseFloat(objs[1].toString()), "mr_id=" + objs[3]);
			//updateAppStatus(Integer.parseInt(objs[0].toString()));
			applicationDao.checkAdQty(Integer.parseInt(objs[0].toString()));
			// applicationreplace
			updateByCondition("ApplicationReplace", "ar_purcqty=nvl(ar_purcqty,0)-" + Float.parseFloat(objs[1].toString()), "ar_id="
					+ objs[4]);
		}
	}

	/**
	 * 采购单修改时，修改请购单状态、数量等
	 */
	@Override
	public void restoreApplicationWithQty(int pdid, Double uqty) {
		Object ad_id = null;
		Object qty = 0;
		Object aq = 0;
		int mr_id = 0;
		uqty = Math.abs(uqty);
		// 判断数量是否超出请购数
		ad_id = getFieldDataByCondition("PurchaseDetail", "pd_sourcedetail", "pd_id=" + pdid);
		SqlRowList rs = queryForRowSet("select NVL(pd_sourcedetail,0) as pd_sourcedetail ,NVL(pd_mmid,0) pd_mmid,NVL(pd_mtid,0) pd_mtid from PurchaseDetail where pd_id="
				+ pdid);
		if (rs.next()) {
			ad_id = rs.getInt("pd_sourcedetail");
			if (ad_id != null && Integer.parseInt(ad_id.toString()) > 0) {
				mr_id = rs.getInt("pd_mmid");
				//pd_cancelqty 取消数量 ：变更单不归还请购单已转数的数量和
				qty = getFieldDataByCondition("PurchaseDetail", "sum(pd_qty+nvl(pd_cancelqty,0))", "pd_sourcedetail=" + ad_id + " AND pd_id <>" + pdid);
				qty = qty == null ? 0 : qty;
				aq = getFieldDataByCondition("ApplicationDetail", "ad_qty", "ad_id=" + ad_id);
				if (Double.parseDouble(aq.toString()) < NumberUtil.add(Double.parseDouble(qty.toString()),uqty) && !isDBSetting("Purchase","AllowOut")) {
					BaseUtil.showError("新数量超出原请购数,超出数量:" + NumberUtil.sub( NumberUtil.add(Double.parseDouble(qty.toString()),uqty) , Double.parseDouble(aq.toString()) ));
				} else {
					updateByCondition("PurchaseDetail", "pd_qty=" + uqty, "pd_id=" + pdid);
					updateByCondition("ApplicationDetail", "ad_yqty=" + NumberUtil.add(Double.parseDouble(qty.toString()) , uqty), "ad_id=" + ad_id);
					if (mr_id > 0) {// 更新替代料下达数量
						qty = getFieldDataByCondition("PurchaseDetail", "sum(pd_qty)", "pd_mmid=" + mr_id + " AND pd_id <>" + pdid);
						qty = qty == null ? 0 : qty;
						updateByCondition("MRPReplace", "mr_purcqty=" + NumberUtil.add(Double.parseDouble(qty.toString()) , uqty), "mr_id=" + mr_id);
						// applicationreplace
						updateByCondition("ApplicationReplace", "ar_purcqty=" + NumberUtil.add(Double.parseDouble(qty.toString()) , uqty),
								"ar_id=" + rs.getInt("pd_mtid"));
					}
					//updateAppStatus(Integer.parseInt(String.valueOf(ad_id)));
					applicationDao.checkAdQty(Integer.parseInt(String.valueOf(ad_id)));
				}
			}
		}
	}

	public void updateAppStatus(int ad_id) {
		Object idx = getFieldDataByCondition("ApplicationDetail", "ad_apid", "ad_id=" + ad_id);
		if (idx != null) {
			int total = getCountByCondition("ApplicationDetail", "ad_id=" + ad_id);
			int aud = getCountByCondition("ApplicationDetail", "ad_id=" + ad_id + " AND nvl(ad_yqty,0)=0");
			int turn = getCountByCondition("ApplicationDetail", "ad_id=" + ad_id + " AND nvl(ad_yqty,0)>=ad_qty");
			String status = "PART2PU";
			if (aud == total) {
				status = "";
			} else if (turn == total) {
				status = "TURNPURC";
			}
			updateByCondition("Application", "ap_turnstatuscode='" + status + "',ap_turnstatus='" + BaseUtil.getLocalMessage(status) + "'",
					"ap_id=" + idx);
		}
	}

	public JSONObject newProdIO(String curr, String vecode, String piclass, String caller, String currentyearmonth) {
		Employee employee = SystemSession.getUser();
		Timestamp time = Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS));
		Object[] objs = getFieldsDataByCondition("Vendor", new String[] { "ve_name", "ve_id", "ve_payment", "ve_apvendcode",
				"ve_apvendname" }, "ve_code='" + vecode + "'");
		Object rate = null;
		rate = getFieldDataByCondition("CURRENCYSMONTH", "CM_CRRATE", "CM_CRNAME='" + curr + "' and CM_YEARMONTH='" + currentyearmonth
				+ "'");
		rate = rate != null ? rate : getFieldDataByCondition("Currencys", "cr_rate", "cr_name='" + curr + "'");
		int id = getSeqId("PRODINOUT_SEQ");
		String no = sGetMaxNumber(caller, 2);
		execute(INSERT_PRODIO_VEND,
				new Object[] { id, no, time, piclass, BaseUtil.getLocalMessage("ENTERING"), "ENTERING", employee.getEm_name(), time,
						vecode, objs[0], objs[1], BaseUtil.getLocalMessage("UNPOST"), "UNPOST", time, employee.getEm_name(),
						BaseUtil.getLocalMessage("UNPRINT"), "UNPRINT", curr, objs[2], rate, objs[3], objs[4] });
		JSONObject j = new JSONObject();
		j.put("pi_id", id);
		j.put("pi_inoutno", no);
		return j;
	}

	/**
	 * 到物料核价单取采购单价
	 * pu_date 取价时间: oracle的时间格式字符串，to_date()或者sysdate
	 * @return {JSONObject} {pd_price: 0.00,pd_rate: 0.00}
	 */
	@Override
	public JSONObject getPurchasePrice(String vendcode, String prodcode, String currency, String kind, double qty, String pu_date) {
		/* 取setting表查看取价取供应商原则，需不需要认定 */
		String sqlstr = null;
		if (isDBSetting("Application!ToPurchase!Deal", "onlyQualifiedPrice")) {
			sqlstr = PURCHASE_PRICE_APPSTATUS.replace("@kind", kind);
		} else {
			sqlstr = PURCHASE_PRICE.replace("@kind", kind);
		}
		sqlstr = sqlstr.replaceAll("sysdate", pu_date);
		SqlRowList rs = queryForRowSet(sqlstr, vendcode, prodcode, currency, qty);
		if (rs.next()) {
			JSONObject obj = new JSONObject();
			obj.put("pd_price", rs.getGeneralDouble("ppd_price"));
			obj.put("pd_rate", rs.getGeneralDouble("ppd_rate"));
			obj.put("pd_ppdid", rs.getGeneralDouble("ppd_id"));
			obj.put("pd_rebatesprice", rs.getGeneralDouble("ppd_rebatesprice"));
			return obj;
		}
		return null;
	}
	/**
	 *  maz 到物料核价单取模材采购单价
	 * 
	 * @return {JSONObject} {pd_price: 0.00,pd_rate: 0.00}
	 */
	@Override
	public JSONObject getMCPurchasePrice(String vendcode, String prodcode, String currency, Integer pd_id, double qty) {
		/* 取setting表查看取价取供应商原则，需不需要认定 */
		Object material = getFieldDataByCondition("PurchaseDetail", "pd_material","pd_id="+pd_id);
		String sqlstr = "select ppd_price,ppd_rate,ppd_id,ppd_rebatesprice from PurchasePriceDetail left join PurchasePrice on ppd_ppid=pp_id where ppd_vendcode=? and ppd_material=? and ppd_currency=? and pp_kind='模材' and to_char(ppd_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID' and ppd_lapqty<=? order by ppd_price ";
		SqlRowList rs = queryForRowSet(sqlstr, vendcode, material, currency, qty);
		if (rs.next()) {
			JSONObject obj = new JSONObject();
			obj.put("pd_price", rs.getGeneralDouble("ppd_price"));
			obj.put("pd_rate", rs.getGeneralDouble("ppd_rate"));
			obj.put("pd_ppdid", rs.getGeneralDouble("ppd_id"));
			obj.put("pd_rebatesprice", rs.getGeneralDouble("ppd_rebatesprice"));
			return obj;
		}
		return null;
	}
	/**
	 * 到物料核价单取委外最低单价
	 * 
	 * @return {JSONObject} {pd_price: 0.00,pd_rate: 0.00}
	 */
	@Override
	public JSONObject getMakePrice(String prodcode, String kind, double qty) {
		/* 取setting表查看取价取供应商原则，需不需要认定 */
		String sqlstr = null;
		if (isDBSetting("Application!ToPurchase!Deal", "onlyQualifiedPrice")) {
			sqlstr = PRICE_VENDOR_APPSTATUS2.replace("@kind", kind);
		} else {
			sqlstr = PRICE_VENDOR2.replace("@kind", kind);
		}
		SqlRowList rs = queryForRowSet(sqlstr, prodcode, qty);
		JSONObject obj = new JSONObject();
		if (rs.next()) {
			obj.put("price", rs.getGeneralDouble("ppd_price"));
			obj.put("vendcode", rs.getGeneralString("ppd_vendcode"));
			obj.put("currency", rs.getGeneralString("ppd_currency"));
			obj.put("taxrate", rs.getGeneralDouble("ppd_rate"));
			obj.put("crrate", rs.getObject("cr_rate") == null ? 1 : rs.getObject("cr_rate"));
			return obj;
		}
		return null;
	}

	/**
	 * 到物料核价单取采购单价以及供应商
	 * 
	 * @return {JSONObject} {pd_price: 0.00,pd_rate: 0.00}
	 */
	@Override
	public JSONObject getPriceVendor(String prodcode, String kind, double qty) {
		/* 取setting表查看取价取供应商原则，需不需要认定 */
		String sqlstr = null;
		if (isDBSetting("Application!ToPurchase!Deal", "onlyQualifiedPrice")) {
			sqlstr = PRICE_VENDOR_APPSTATUS2.replace("@kind", kind);
		} else {
			sqlstr = PRICE_VENDOR2.replace("@kind", kind);
		}
		SqlRowList rs = queryForRowSet(sqlstr, prodcode, qty);
		JSONObject obj = new JSONObject();
		if (rs.next()) {
			obj.put("price", rs.getGeneralDouble("ppd_price"));
			obj.put("vendcode", rs.getGeneralString("ppd_vendcode"));
			obj.put("currency", rs.getGeneralString("ppd_currency"));
			obj.put("taxrate", rs.getGeneralDouble("ppd_rate"));
			obj.put("crrate", rs.getObject("cr_rate") == null ? 1 : rs.getObject("cr_rate"));
			obj.put("ppd_id", rs.getObject("ppd_id"));
		} else {
			obj.put("price", 0);
			obj.put("vendcode", "");
			obj.put("currency", "");
			obj.put("taxrate", 0);
			obj.put("crrate", 1);
			obj.put("ppd_id", 0);
		}
		return obj;
	}

	@Override
	public void getPrice(int pu_id) {
		StringBuffer error = new StringBuffer();
		List<Object[]> objects = getFieldsDatasByCondition("purchasedetail left join Purchase on pu_id=pd_puid", new String[] {
				"pd_prodcode", "pu_vendcode", "pu_currency", "pd_qty", "pd_id", "pu_ordertype","pu_kind" }, " pd_puid=" + pu_id);
		if (objects.size() > 0 && !"B2C".equals(objects.get(0)[5])) {
			JSONObject js = null;
			for (Object[] obj : objects) {
				Object oqty = getFieldDataByCondition("PurchaseDetail", "sum(pd_qty)",
						" pd_puid=" + pu_id + " and pd_prodcode='" + String.valueOf(obj[0]) + "'");
				if("模材".equals(obj[6]) ){
					js = getMCPurchasePrice(String.valueOf(obj[1]), String.valueOf(obj[0]), String.valueOf(obj[2]), Integer.parseInt(obj[4].toString()),
							Double.parseDouble(oqty.toString()));
				} else {
					Object pu_date = getFieldDataByCondition("Purchase", "to_char(pu_date,'yyyy-mm-dd')", "pu_id="+pu_id);
					js = getPurchasePrice(String.valueOf(obj[1]), String.valueOf(obj[0]), String.valueOf(obj[2]), "采购",
							Double.parseDouble(oqty.toString()),DateUtil.parseDateToOracleString(Constant.YMD, (String)pu_date));
				}
				double price = 0;
				double tax = 0;
				double qty = Double.parseDouble(obj[3].toString());
				double p = 0;
				double total = 0;
				double nettotal = 0;
				double netprice = 0;
				double ppdid = 0;
				double rebatesprice = 0;
				if (js != null) {
					price = js.getDouble("pd_price");
					tax = js.getDouble("pd_rate");
					ppdid = js.getDouble("pd_ppdid");
					rebatesprice = js.getDouble("pd_rebatesprice");
				}
				if (price != 0) {
					p = NumberUtil.formatDouble(price, 6);
					total = NumberUtil.formatDouble(qty * p, 2);
					nettotal = NumberUtil.formatDouble(qty * p / (1 + tax / 100), 2);
					netprice = NumberUtil.formatDouble(p / (1 + tax / 100), 6);
				} else {
					error.append("根据 物料编号:[" + obj[0] + "],供应商号:[" + obj[1] + "],币别:[" + obj[2] + "] 在物料核价单未找到对应单价，或单价为空值、0等!<BR/>");
				}
				//增加pd_qty-NVL(pd_beipin,0)>0条件， 是因为请购单转采购单后拆分的备品行的单价要为0，即新的备品行不需要更新单价
				updateByCondition("purchaseDetail", "pd_price=" + p + ",pd_rate=" + tax + ",pd_total=" + total + ",pd_taxtotal=" + nettotal
						+ ",pd_netprice=" + netprice + ",pd_ppdid=" + ppdid +",pd_rebatesprice=" + rebatesprice + "", "pd_qty-NVL(pd_beipin,0)>0 and pd_id=" + obj[4]);
			}
			// 主表金额
			execute(PU_TOTAL, pu_id);
			execute(PU_TAXTOTAL, pu_id);
			execute(PU_PURCVEND, pu_id);
			Object total = getFieldDataByCondition("Purchase", "pu_total", "pu_id=" + pu_id);
			if (total != null) {
				execute("update Purchase set pu_totalupper='" + MoneyUtil.toChinese(total.toString()) + "' WHERE pu_id=" + pu_id);
			}
			if (error.length() > 0) {
				BaseUtil.appendError(error.toString());
			}
		}
		if(checkIf("PURCHASEDETAIL left join purchasepricedetail on ppd_id=pd_ppdid", "pd_puid="+pu_id+" and nvl(pd_ppdid,0)>0 and (nvl(ppd_accuqty,0)>0 or nvl(ppd_nextaccuqty,0)>0)")){
			getPriceByAccuqty(pu_id);
		}
	}

	@Override
	public void getPriceByAccuqty(int pu_id){
		//将采购单明细中获取到核价单的累计下单量大于0的采购单价更新为0
		execute("update purchasedetail set pd_price=0 where pd_puid="+pu_id+" and nvl(pd_ppdid,0)>0 and exists (select 1 from purchasepricedetail where"
				+ " ppd_id=pd_ppdid and (nvl(ppd_accuqty,0)>0 or nvl(ppd_nextaccuqty,0)>0))");
		while (checkIf("purchasedetail left join purchasepricedetail on ppd_id=pd_ppdid","pd_puid="+pu_id+" and nvl(pd_ppdid,0)>0 and (nvl(ppd_accuqty,0)>0 or nvl(ppd_nextaccuqty,0)>0 ) and nvl(pd_price,0)=0")) {
			SqlRowList rs = queryForRowSet("select A.*,case when nvl(pd_purcqty,0)=0 then pd_qty else pd_purcqty end pdqty from "
					+ "(Select pd_id,pd_detno,pu_vendcode,pu_currency,pd_prodcode,pd_rate,pd_qty,to_char(ppd_fromdate,'yyyy-mm-dd') fromdate, to_char(ppd_todate,'yyyy-mm-dd') todate "
					+ ",pd_purcqty from purchasedetail left join purchase on pd_puid=pu_id left join purchasepricedetail on ppd_id=pd_ppdid  "
					+ "Where pd_puid='"+pu_id+"' and nvl(pd_ppdid,0)>0 and ( nvl(ppd_accuqty,0)>0 or nvl(ppd_nextaccuqty,0)>0)and nvl(pd_price,0)=0 Order by pd_detno) A where rownum<2")  ;
			if(rs.next()){ 
        		double qty= rs.getGeneralDouble("pdqty");
				SqlRowList rs1=queryForRowSet("select  ppd_id,ppd_price,nvl(ppd_accuqty,0) ppd_accuqty,nvl(ppd_nextaccuqty,0) ppd_nextaccuqty,(nvl(ppd_nextaccuqty,0)-nvl(sum(qty),0)) thisqty "
						+ "from purchasepricedetail left join  purchaseprice on ppd_ppid=pp_id left join "
						+ "(select case when pd_mrpstatuscode='FINISH' then (case when nvl(pd_purcacceptqty,0)=0 then pd_acceptqty else pd_purcacceptqty end) else (case when nvl(pd_purcqty,0)=0 then pd_qty else pd_purcqty end) end qty,"
						+ "pu_date,pd_price,pu_vendcode,pu_currency,pd_prodcode,pd_rate,pd_ppdid	from purchase left join purchasedetail on pd_puid=pu_id left join product on pr_code=pd_prodcode "
						+ "where nvl(pd_ppdid,0)>0  and pu_vendcode='" + rs.getObject("pu_vendcode") + "' and pd_prodcode='" + rs.getObject("pd_prodcode") + "' and pu_currency='" + rs.getObject("pu_currency")+"' "
								+ "and pd_rate="+rs.getObject("pd_rate")+" ) on ppd_id=pd_ppdid and nvl(pd_price,0)=nvl(ppd_price,0) and trunc(pu_date)>=trunc(ppd_fromdate) and trunc(pu_date)<=trunc(ppd_todate) "
										+ " where pp_kind='采购' and ppd_statuscode='VALID' and ppd_vendcode='" + rs.getObject("pu_vendcode") + "' and ppd_prodcode='" + rs.getObject("pd_prodcode") + 
										"' and ppd_rate="+rs.getObject("pd_rate")+" and ppd_currency='" + rs.getObject("pu_currency")+"' and to_char(ppd_fromdate,'yyyy-mm-dd')='"+rs.getGeneralString("fromdate")
										+"' and to_char(ppd_todate,'yyyy-mm-dd')='"+rs.getGeneralString("todate")+"' group by ppd_id,ppd_price,nvl(ppd_accuqty,0),nvl(ppd_nextaccuqty,0) "
												+ "having  (nvl(ppd_nextaccuqty,0)=0 and nvl(ppd_accuqty,0)<>0) or (nvl(ppd_nextaccuqty,0)-nvl(sum(qty),0)>0) order by nvl(ppd_accuqty,0)");
				 
				execute("update purchasedetail set pd_detno=(select nvl(max(pd_detno),0)+1 from purchasedetail where pd_puid="+pu_id+") where nvl(pd_detno,0)<0  and pd_id="+rs.getInt("pd_id"));
				if(rs1.next()){			
					double accuqty=rs1.getGeneralDouble("thisqty");
					if(rs1.getGeneralDouble("ppd_accuqty")>0 && rs1.getGeneralDouble("ppd_nextaccuqty")==0 ){
						execute("update purchasedetail set pd_price="+rs1.getGeneralDouble("ppd_price")+",pd_ppdid="+rs1.getGeneralInt("ppd_id")+" where pd_puid="+pu_id +" and nvl(pd_ppdid,0)>0 and nvl(pd_price,0)=0 and "
								+ "exists (select 1 from purchasepricedetail where pd_ppdid=ppd_id and nvl(ppd_accuqty,0)>0)");
						execute("update purchasedetail set pd_detno=(select nvl(max(pd_detno),0)+1 from purchasedetail where pd_puid="+pu_id+") where pd_puid="+pu_id +" and nvl(pd_detno,0)<0");
					}else if(qty-accuqty > 0 ){
						Map<String, Object> diffence = new HashMap<String, Object>();
						diffence.put("pd_detno",-1);
						diffence.put("pd_price", 0);
						diffence.put("pd_qty", (qty-accuqty) / qty * rs.getGeneralDouble("pd_qty") ); 
						diffence.put("pd_purcqty", qty-accuqty);
						diffence.put("pd_id", getSeqId("PURCHASEDETAIL_SEQ"));
						copyRecord("purchasedetail", "purchasedetail", "pd_id=" + rs.getInt("pd_id"), diffence);
						execute("update purchasedetail set pd_purcqty= "+accuqty+",pd_price="+ rs1.getGeneralDouble("ppd_price")+",pd_ppdid="+rs1.getGeneralInt("ppd_id")+","
								+ "pd_qty= (case when nvl(pd_purcqty,0)<>0 then "+accuqty+"/pd_purcqty*pd_qty else "+accuqty+" end) where pd_id="+rs.getInt("pd_id"));
						qty=qty-accuqty;
					}else{
						 execute("update purchasedetail set pd_price="+ rs1.getGeneralDouble("ppd_price")+",pd_ppdid="+rs1.getGeneralInt("ppd_id")+" where pd_id="+rs.getInt("pd_id"));
						 qty=0;	
					}
				} 
			}
		}
	}
	@Override
	public void getPrice(String pu_code) {
		Object id = getFieldDataByCondition("Purchase", "pu_id", "pu_code='" + pu_code + "'");
		if (id != null) {
			getPrice(Integer.parseInt(id.toString()));
		}
	}

	static final String CHECK_YQTY = "SELECT pd_code,pd_detno,pd_qty,nvl(pd_frozenqty,0) pd_frozenqty FROM PurchaseDetail WHERE pd_id=? and pd_qty-nvl(pd_frozenqty,0)<?";

	/**
	 * 采购转入收料之前， 1.判断采购单状态 2.判断thisqty ≤ qty - yqty-nvl(pd_frozenqty,0)
	 */
	@Override
	public void checkPdYqty(List<Map<Object, Object>> datas) {
		int id = 0;
		Object y = 0;// 已转收料单数量
		Object r = 0;// 验退数量
		Object o = 0;// 不良品出库数量
		SqlRowList rs = null;
		boolean bool = false;
		Object[] pus = null;
		for (Map<Object, Object> d : datas) {
			id = Integer.parseInt(d.get("pd_id").toString());
			pus = getFieldsDataByCondition("PurchaseDetail left join Purchase on pd_puid=pu_id", "pu_code,pd_detno", "pd_id=" + id);
			if (pus != null) {
				bool = checkIf("Purchase", "pu_code='" + pus[0] + "' and pu_statuscode='AUDITED'");
				if (!bool) {
					BaseUtil.showError("采购单:" + pus[0] + " 未审核通过,无法转收料单!");
				}
				y = getFieldDataByCondition("VerifyApplyDetail", "sum(nvl(vad_qty,0))", "vad_pucode='" + pus[0] + "' and vad_pudetno="
						+ pus[1]);
				y = y == null ? 0 : y;
				r = getFieldDataByCondition("ProdIODetail", "sum(nvl(pd_outqty,0))", "pd_ordercode='" + pus[0] + "' and pd_orderdetno="
						+ pus[1] + " AND pd_piclass='采购验退单'");
				o = getFieldDataByCondition("ProdIODetail", "sum(nvl(pd_outqty,0))", "pd_ordercode='" + pus[0] + "' and pd_orderdetno="
						+ pus[1] + " AND pd_piclass='不良品出库单'");
				r = r == null ? 0 : r;
				o = o == null ? 0 : o;
				rs = queryForRowSet(
						CHECK_YQTY,
						id,
						new BigDecimal(y.toString()).subtract(new BigDecimal(r.toString())).subtract(new BigDecimal(o.toString())).add(new BigDecimal(d.get("pd_tqty").toString())));
				if (rs.next()) {
					StringBuffer sb = new StringBuffer("本次数量填写超出可转数量,采购单号:").append(rs.getGeneralString("pd_code")).append(",行号:")
							.append(rs.getInt("pd_detno")).append(",采购数:").append(rs.getDouble("pd_qty")).append(",已冻结数量：")
							.append(rs.getDouble("pd_frozenqty")).append(",已转收料单数:").append(y).append(",已验退数:").append(r)
							.append(",不良品出库数:").append(o).append(",本次数:").append(d.get("pd_tqty"));
					BaseUtil.showError(sb.toString());
				}
			}
		}
	}
	
	/**
	 * 判断采购单供应商是否是最低价供应商
	 */
	@Override
	public JSONObject getPriceVendor_check(String prodcode, String kind, double qty ,String vendcode) {
		/* 取setting表查看取价取供应商原则，需不需要认定 */
		String sqlstr = null;
		if (isDBSetting("Application!ToPurchase!Deal", "onlyQualifiedPrice")) {
			sqlstr = PRICE_VENDOR_CHECK_APPSTATUS.replace("@kind", kind);
		} else {
			sqlstr = PRICE_VENDOR_CHECK.replace("@kind", kind);
		}
		SqlRowList rs = queryForRowSet(sqlstr, vendcode, prodcode, qty);
		JSONObject obj = new JSONObject();
		if (rs.next()) {
			obj.put("price", rs.getGeneralDouble("ppd_price"));
			obj.put("vendcode", rs.getGeneralString("ppd_vendcode"));
			obj.put("currency", rs.getGeneralString("ppd_currency"));
			obj.put("taxrate", rs.getGeneralDouble("ppd_rate"));
			obj.put("crrate", rs.getObject("cr_rate") == null ? 1 : rs.getObject("cr_rate"));
			obj.put("ppd_id", rs.getObject("ppd_id"));
		} else {
			obj.put("price", 0);
			obj.put("vendcode", "");
			obj.put("currency", "");
			obj.put("taxrate", 0);
			obj.put("crrate", 1);
			obj.put("ppd_id", 0);
		}
		return obj;
	}

	@Override
	public void getPutype(int pu_id) {
		StringBuffer error = new StringBuffer();
		Boolean isvendorate = isDBSetting("vendorRate"); // 是否启用供应商比例分配
		List<Object[]> objects = getFieldsDatasByCondition("purchasedetail left join Purchase on pu_id=pd_puid", new String[] {
				"pd_prodcode", "pu_vendcode", "pu_currency", "pd_qty", "pd_id", "pd_detno", "pu_ordertype" }, " pd_puid=" + pu_id);
		if (objects.size() > 0 && !"B2C".equals(objects.get(0)[6])) {
			JSONObject js = null;
			for (Object[] obj : objects) {
				if (isvendorate) {
					Object setrate = getFieldDataByCondition("productvendorrate", "pv_id", "pv_prodcode='" + obj[0] + "' and pv_vendcode='"
							+ obj[1] + "'");
					if (setrate != null) {
						continue;
					}
				}
				Object oqty = getFieldDataByCondition("PurchaseDetail", "sum(pd_qty)",
						" pd_puid=" + pu_id + " and pd_prodcode='" + String.valueOf(obj[0]) + "'");
				js = getPriceVendor_check(String.valueOf(obj[0]), "采购", Double.parseDouble(oqty.toString()),obj[1].toString());
				double price = 0;
				String tax = null;
				if (js != null) {
					price = js.getDouble("price");
					tax = js.get("vendcode").toString();
					if (!tax.equals(obj[1].toString())) {
						price = NumberUtil.formatDouble(price, 6);
						error.append("序号:[" + obj[5] + "]的物料有最低有效供应商:[" + tax + "],最低价格为[" + price + "](折合RMB的价格)或选用非标准订单类型<BR/>");
					}
					/*
					 * else { getPrice(pu_id); }
					 */
				} else {
					error.append("序号:[" + obj[5] + "]的物料没有定价有效,请走非标准采购订单流程！");
				}

			}
			if (error.length() > 0) {
				BaseUtil.showError(error.toString());
			}
		}
	}

	/**
	 * 更新采购单明细已收料数量pd_yqty（包括直接验收数量） by zhongyl
	 */
	@Override
	public void updatePurcYQTY(int ifall, String pdidstr) {
		String Sqlstr = "";
		String ConditionStr = "";
		if (ifall == -1 || ifall == 1) {
			ConditionStr = " pd_id in (select pd_id from purchase,purchasedetail where pd_puid=pu_id and pu_statuscode='AUDITED' and pd_qty-NVL(pd_acceptqty,0)>0 and NVL(pd_mrpstatuscode,' ')<>'FINISH')  ";
		} else if (pdidstr != null && !pdidstr.equals("")) {
			ConditionStr = " pd_id in (" + pdidstr + ")";
		} else {
			return;
		}
		Sqlstr = "merge into purchasedetail USING(select * from (select pd_id,pd_yqty,pd_acceptqty,pd_backqty,pd_rejectqty,NVL(sum(vad_qty),0)vadqty from purchase left join purchasedetail on pu_id=pd_puid left join (verifyapplydetail inner join verifyapply on va_id=vad_vaid and va_class='采购收料单' )on pd_code=vad_pucode and pd_detno=vad_pudetno where pu_statuscode='AUDITED' and pd_qty-NVL(pd_acceptqty,0)>0 group by pd_id,pd_yqty,pd_acceptqty,pd_backqty,pd_rejectqty) where NVL(pd_yqty,0)<>vadqty-NVL(pd_backqty,0)+NVL(pd_rejectqty,0))src on (src.pd_id=purchasedetail.pd_id) when matched then update set pd_yqty=(case when pd_acceptqty>=vadqty-NVL(pd_backqty,0)+NVL(pd_rejectqty,0) then pd_acceptqty else vadqty-NVL(pd_backqty,0)+NVL(pd_rejectqty,0) end) where "
				+ ConditionStr;
		execute(Sqlstr);
	}

	/**
	 * 更新采购单明细当前已通知数 (已发通知未收料部分) by zhongyl
	 */
	@Override
	public void updatePurcYNotifyQTY(int ifall, String pdidstr) {
		String Sqlstr = "";
		String ConditionStr = "";
		if (ifall == -1 || ifall == 1) {
			ConditionStr = " pd_id in (select pd_id from purchase,purchasedetail where pd_puid=pu_id and pu_statuscode='AUDITED' and pd_qty-NVL(pd_acceptqty,0)>0 and NVL(pd_mrpstatuscode,' ')<>'FINISH')  ";
			Sqlstr = "update purchasedetail set pd_turnqty=0 where " + ConditionStr;
			execute(Sqlstr);
			Sqlstr = "merge into purchasedetail USING(select pn_pdid,SUM(pn_qty - NVL(pn_endqty, 0)) as qty FROM purchasenotify WHERE  PURCHASENOTIFY.pn_status <> '已取消'"
					+ " AND PURCHASENOTIFY.pn_status <> '已发货'  AND   pn_qty>NVL(pn_endqty,0) group by pn_pdid )src on(src.pn_pdid=pd_id)"
					+ " WHEN MATCHED THEN UPDATE SET pd_turnqty=NVL(pd_turnqty,0)+nvl(src.qty,0) where　" + ConditionStr;
			execute(Sqlstr);
			Sqlstr = " merge into purchasedetail USING(select pd_id,SUM(and_inqty-NVL(and_yqty,0)) as qty FROM acceptnotify,acceptnotifydetail,purchasedetail"
					+ " WHERE  an_id = and_anid  AND an_status <> '已转收料' AND an_status <> '已删除' and and_inqty-NVL(and_yqty,0)>0 and pd_code=and_ordercode and pd_detno=and_orderdetno group by pd_id)src on(src.pd_id=purchasedetail.pd_id)"
					+ " WHEN MATCHED THEN UPDATE SET pd_turnqty=NVL(pd_turnqty,0)+nvl(src.qty,0)   where　" + ConditionStr;
			execute(Sqlstr);
		} else if (pdidstr != null && !pdidstr.equals("")) {
			ConditionStr = " pd_id in (" + pdidstr + ")";
			Sqlstr = "update purchasedetail set pd_turnqty=0 where " + ConditionStr;
			execute(Sqlstr);
			Sqlstr = "merge into purchasedetail USING(select pn_pdid,SUM(pn_qty - NVL(pn_endqty, 0)) as qty FROM purchasenotify WHERE pn_pdid in ("
					+ pdidstr
					+ ") and PURCHASENOTIFY.pn_status <> '已取消'"
					+ " AND PURCHASENOTIFY.pn_status <> '已发货'  AND    pn_qty>NVL(pn_endqty,0) group by pn_pdid )src on(src.pn_pdid=pd_id)"
					+ " WHEN MATCHED THEN UPDATE SET pd_turnqty=NVL(pd_turnqty,0)+nvl(src.qty,0) where　" + ConditionStr;
			execute(Sqlstr);
			Sqlstr = " merge into purchasedetail USING(select pd_id,SUM(and_inqty-NVL(and_yqty,0)) as qty FROM acceptnotify,acceptnotifydetail,purchasedetail"
					+ " WHERE pd_id in ("
					+ pdidstr
					+ ") and an_id = and_anid  AND an_status <> '已转收料' AND an_status <> '已删除' and and_inqty-NVL(and_yqty,0)>0 and pd_code=and_ordercode and pd_detno=and_orderdetno group by pd_id)src on(src.pd_id=purchasedetail.pd_id)"
					+ " WHEN MATCHED THEN UPDATE SET pd_turnqty=NVL(pd_turnqty,0)+nvl(src.qty,0) where　" + ConditionStr;
			execute(Sqlstr);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uas.erp.dao.common.PurchaseDao#testPostToSqlServer(int)
	 */
	@Override
	public void syncPurcToSqlServer(int pu_id) {
		Employee employee = SystemSession.getUser();
		String hk = "malata_hk";
		SqlRowList rs = queryForRowSet("select * from purchase left join vendor on pu_vendcode=ve_code where pu_id=?", pu_id);
		if (rs.next()) {
			SqlRowList rd = queryForRowSet(
					"select * from purchasedetail left join product on pd_prodcode=pr_code where pd_puid=? order by pd_detno", pu_id);
			if (rd.hasNext()) {
				String sob = SpObserver.getSp();
				SpObserver.putSp(hk);
				if (!hk.equals(SpObserver.getSp())) {
					BaseUtil.showError("无法连接到香港帐套,同步失败.");
				}
				String error = checkExist(rs.getGeneralString("pu_code"));
				if (error != null) {
					SpObserver.putSp(sob);
					BaseUtil.showError(error);
				}
				String[] sqls = new String[2 * rd.getResultList().size() + 2];
				int i = 0;
				double bsqty = 0.0;
				double beipin = 0.0;
				while (rd.next()) {
					if (Math.abs(rd.getGeneralInt("pd_bonded")) == 1) {
						bsqty = rd.getGeneralDouble("pd_qty");
						beipin = 0;
					} else {
						beipin = rd.getGeneralDouble("pd_qty");
						bsqty = 0;
					}
					sqls[i++] = "insert into purchasedetail(pd_code,pd_detno,pd_prodcode,pd_price,pd_qty,pd_netprice,pd_total,pd_delivery,pd_remark)values('"
							+ rd.getGeneralString("pd_code")
							+ "',"
							+ rd.getInt("pd_detno")
							+ ",'"
							+ rd.getGeneralString("pd_prodcode")
							+ "',"
							+ rd.getGeneralDouble("pd_price")
							+ ","
							+ rd.getGeneralDouble("pd_qty")
							+ ","
							+ rd.getGeneralDouble("pd_netprice")
							+ ","
							+ rd.getGeneralDouble("pd_total")
							+ ",dateadd(day,-3,CONVERT(datetime,'"
							+ rd.getGeneralTimestamp("pd_delivery")
							+ "',20)),'"
							+ rd.getGeneralString("pd_remark") + "')";
					sqls[i++] = "insert into saledetail(sd_code,sd_detno,sd_prodcode,sd_price,sd_qty,sd_bsqty,sd_beipin,sd_taxrate,sd_delivery,sd_remark)values('"
							+ rd.getGeneralString("pd_code")
							+ "',"
							+ rd.getInt("pd_detno")
							+ ",'"
							+ rd.getGeneralString("pd_prodcode")
							+ "',0,"
							+ rd.getGeneralDouble("pd_qty")
							+ ","
							+ bsqty
							+ ","
							+ beipin
							+ ",0,CONVERT(datetime,'"
							+ rd.getGeneralTimestamp("pd_delivery") + "',20),'" + rd.getGeneralString("pd_apremark") + "')";
				}
				double rate = rs.getGeneralDouble("pu_rate");
				if ("USD".equals(rs.getGeneralString("pu_currency"))) {
					rate = 7.78;
				} else if ("HKD".equals(rs.getGeneralString("pu_currency"))) {
					rate = 1;
				}
				String cop = "深圳移动".equals(rs.getString("pu_cop")) ? "深圳移动" : "香港万利达";
				sqls[i++] = "insert into sale(sa_code,sa_relativecode,sa_date,sa_currency,sa_rate,sa_status,sa_recordman,sa_custcode,sa_custname,sa_cop,sa_recorddate,sa_sellercode,sa_seller,sa_payments,sa_toplace,sa_remark,sa_purcstatus) values('"
						+ rs.getGeneralString("pu_code")
						+ "','"
						+ rs.getGeneralString("pu_code")
						+ "',getdate(),'USD',7.78,'已审核','"
						+ employee.getEm_name()
						+ "','WLDKJ','南靖万利达科技有限公司','"
						+ cop
						+ "',getdate(),'wmc8321','王满春','AMS 30天 T/T','福建省南靖靖城萬利達工業園','系统同步产生!"
						+ rs.getGeneralString("pu_remark")
						+ "','已下达计划')";
				sqls[i++] = "insert into purchase(pu_code,pu_relativecode,pu_date,pu_currency,pu_rate,pu_status,pu_recordman,pu_vendcode,pu_vendname,pu_cop,pu_indate,pu_payments,pu_shipaddresscode,pu_buyer,pu_buyername,pu_remark,pu_updatedate,pu_vendoruu) values('"
						+ rs.getGeneralString("pu_code")
						+ "','"
						+ rs.getGeneralString("pu_code")
						+ "',getdate(),'"
						+ rs.getGeneralString("pu_currency")
						+ "',"
						+ rate
						+ ",'已审核','"
						+ employee.getEm_name()
						+ "','"
						+ rs.getGeneralString("pu_vendcode")
						+ "','"
						+ rs.getGeneralString("pu_vendname")
						+ "','"
						+ cop
						+ "',getdate(),'"
						+ rs.getGeneralString("pu_payments")
						+ "','香港北角渣華道321號柯達大廈第二期912室','"
						+ rs.getGeneralString("pu_buyercode")
						+ "','"
						+ rs.getGeneralString("pu_buyername")
						+ "','"
						+ rs.getGeneralString("pu_remark")
						+ "','"
						+ DateUtil.currentDateString(null) + "','" + rs.getGeneralString("pu_vendoruu") + "')";
				getJdbcTemplate().batchUpdate(sqls);
				syncVendor(rs.getCurrentMap());
				syncProduct(rd.getResultList());
				SpObserver.putSp(sob);
				execute("update purchase set pu_sync='已同步' where pu_id=?", pu_id);
			}
		}
	}

	/**
	 * 检查HK帐套是否已经存在关联的单据
	 */
	private String checkExist(String code) {
		Object existCode = getFieldDataByCondition("sale", "sa_code", "sa_relativecode='" + code + "'");
		if (existCode != null) {
			return "当前采购单在HK帐套已经有关联的销售合同:" + existCode;
		} else {
			boolean exist = checkIf("purchase", "pu_code='" + code + "'");
			if (exist)
				return "当前采购单在HK帐套已经有关联的采购单";
		}
		return null;
	}

	/**
	 * 同步物料
	 */
	private void syncProduct(List<Map<String, Object>> maps) {
		SqlRowList rs = queryForRowSet(
				"select pd_prodcode from purchasedetail where pd_code=? and not exists (select 1 from product where pr_code=pd_prodcode)",
				maps.get(0).get("PD_CODE"));
		Map<String, Object> map = null;
		while (rs.next()) {
			map = CollectionUtil.findRecord(maps, "PR_CODE", rs.getGeneralString(1));
			if (map != null) {
				SqlMap sql = new SqlMap("product");
				sql.set("pr_code", map.get("PR_CODE"));
				sql.set("pr_detail", map.get("PR_DETAIL"));
				sql.set("pr_spec", map.get("PR_SPEC"));
				sql.set("pr_bgmc", map.get("PR_DETAIL"));
				sql.set("pr_bgspec", map.get("PR_SPEC"));
				sql.set("pr_serial", map.get("PR_SERIAL"));
				sql.set("pr_kind", map.get("PR_KIND"));
				sql.set("pr_unit", map.get("PR_UNIT"));
				String cop = String.valueOf(map.get("PR_COP"));
				if ("深圳移动".equals(cop)) {
					sql.set("pr_remark2", "万利达移动");
				} else {
					sql.set("pr_remark2", "万利达科技");
				}
				sql.set("pr_status", BaseUtil.getLocalMessage("AUDITED"));
				sql.set("pr_remark", "采购订单同步建立");
				sql.set("pr_docdate", "getdate()");
				sql.set("pr_buyercode", map.get("PR_BUYERCODE"));
				sql.set("pr_buyername", map.get("PR_BUYERNAME"));
				sql.execute();
			}
		}
	}

	/**
	 * 同步供应商
	 */
	private void syncVendor(Map<String, Object> map) {
		Employee employee = SystemSession.getUser();
		boolean needSync = checkByCondition("Vendor", "ve_code='" + map.get("VE_CODE") + "'");
		if (needSync) {
			SqlMap sql = new SqlMap("vendor");
			sql.set("ve_code", map.get("VE_CODE"));
			sql.set("ve_name", map.get("VE_NAME"));
			sql.set("ve_shortname", map.get("VE_SHORTNAME"));
			sql.set("ve_engname", map.get("VE_ENGNAME"));
			sql.set("ve_contact", map.get("VE_CONTACT"));
			sql.set("ve_tel", map.get("VE_TEL"));
			sql.set("ve_fax", map.get("VE_FAX"));
			sql.set("ve_mobile", map.get("VE_MOBILE"));
			sql.set("ve_email", map.get("VE_EMAIL"));
			sql.set("ve_add1", map.get("VE_ADD1"));
			sql.set("ve_payment", map.get("VE_PAYMENT"));
			sql.set("ve_shipment", map.get("VE_SHIPMENT"));
			sql.set("ve_buyername", map.get("VE_BUYERNAME"));
			sql.set("ve_usercode", map.get("VE_USERCODE"));
			sql.set("ve_password", map.get("VE_PASSWORD"));
			sql.set("ve_auditstatus", BaseUtil.getLocalMessage("AUDITED"));
			sql.set("ve_initdate", "getdate()");
			sql.set("ve_recorder", employee.getEm_name());
			sql.set("ve_bank", map.get("VE_BANK"));
			sql.set("ve_bankaccount", map.get("VE_BANKACCOUNT"));
			sql.set("ve_currency", map.get("VE_CURRENCY"));
			sql.execute();
			execute("update vendor set ve_buyer=us_code from users where ve_buyername=us_name and ve_code=?", map.get("VE_CODE"));
		}
	}

	@Override
	public void resetPurcSyncStatus(int pu_id) {
		String hk = "malata_hk";
		SqlRowList rs = queryForRowSet("select pu_code from purchase where pu_id=?", pu_id);
		if (rs.next()) {
			String sob = SpObserver.getSp();
			SpObserver.putSp(hk);
			if (!hk.equals(SpObserver.getSp())) {
				BaseUtil.showError("无法连接到香港帐套,执行失败.");
			}
			boolean exist = checkIf("sale", "sa_relativecode='" + rs.getString(1) + "'");
			SpObserver.putSp(sob);
			execute("update purchase set pu_sync=" + (exist ? "'已同步'" : "null") + " where pu_id=?", pu_id);
		}
	}

	/**
	 * 采购单转验收单之前， 1.判断采购单状态 2.判断thisqty ≤ qty - yqty
	 */
	@Override
	public void checkqty(List<Map<Object, Object>> datas) {
		int id = 0;
		Object y = 0;// 已转收料单数量
		Object r = 0;// 验退数量
		Object o = 0;// 不良品出库数量
		SqlRowList rs = null;
		boolean bool = false;
		Object[] pus = null;
		for (Map<Object, Object> d : datas) {
			id = Integer.parseInt(d.get("pd_id").toString());
			pus = getFieldsDataByCondition("PurchaseDetail left join Purchase on pd_puid=pu_id", "pu_code,pd_detno", "pd_id=" + id);
			if (pus != null) {
				bool = checkIf("Purchase", "pu_code='" + pus[0] + "' and pu_statuscode='AUDITED'");
				if (!bool) {
					BaseUtil.showError("采购单:" + pus[0] + " 未审核通过,无法转收料单!");
				}
				y = getFieldDataByCondition("ProdIODetail", "sum(nvl(pd_inqty,0))", "pd_piclass='采购验收单' and pd_ordercode='" + pus[0]
						+ "' and pd_orderdetno=" + pus[1]);
				y = y == null ? 0 : y;
				r = getFieldDataByCondition("ProdIODetail", "sum(nvl(pd_outqty,0))", "pd_ordercode='" + pus[0] + "' and pd_orderdetno="
						+ pus[1] + " AND pd_piclass='采购验退单' and pd_status>0");
				o = getFieldDataByCondition("ProdIODetail", "sum(nvl(pd_outqty,0))", "pd_ordercode='" + pus[0] + "' and pd_orderdetno="
						+ pus[1] + " AND pd_piclass='不良品出库单' and pd_status>0");
				r = r == null ? 0 : r;
				o = o == null ? 0 : o;
				rs = queryForRowSet(
						CHECK_YQTY,
						id,
						Double.parseDouble(y.toString()) - Double.parseDouble(r.toString()) - Double.parseDouble(o.toString())
								+ Double.parseDouble(d.get("pd_tqty").toString()));
				if (rs.next()) {
					StringBuffer sb = new StringBuffer("本次数量填写超出可转数量,采购单号:").append(rs.getGeneralString("pd_code")).append(",行号:")
							.append(rs.getInt("pd_detno")).append(",采购数:").append(rs.getDouble("pd_qty")).append(",已转验收数:").append(y)
							.append(",已验退数:").append(r).append(",不良品出库数:").append(o).append(",已冻结数量：").append(rs.getDouble("pd_frozenqty"))
							.append(",本次数:").append(d.get("pd_tqty"));
					BaseUtil.showError(sb.toString());
				}
			}
		}
	}

	@Override
	public void getLastPrice(int pu_id) {
		SqlRowList rs = queryForRowSet(
				"SELECT * FROM PurchaseDetail LEFT JOIN Purchase on pu_id=pd_puid WHERE pu_id=? and nvl(pd_price,0)=0 and nvl(pu_getprice,0)=0",
				pu_id);
		while (rs.next()) {
			SqlRowList pd = queryForRowSet(
					"SELECT pd_price,pd_bgprice,pd_rate,pd_netprice FROM (select nvl(pd_price,0) pd_price,nvl(pd_bgprice,0) pd_bgprice,nvl(pd_rate,0) pd_rate,nvl(pd_netprice,0) pd_netprice "
							+ "from PurchaseDetail LEFT JOIN Purchase on pu_id=pd_puid where pd_prodcode=? and pu_currency=? and pu_vendcode=? and pu_statuscode='AUDITED' and pu_auditdate is not null order by pu_auditdate desc) WHERE rownum<2",
					rs.getString("pd_prodcode"), rs.getString("pu_currency"), rs.getString("pu_vendcode"));
			if (pd.next()) {
				updateByCondition("PurchaseDetail",
						"pd_price=" + pd.getGeneralDouble("pd_price") + ",pd_bgprice=" + pd.getGeneralDouble("pd_bgprice") + ", pd_rate="
								+ pd.getGeneralDouble("pd_rate") + ", pd_netprice=" + pd.getGeneralDouble("pd_netprice"),
						"pd_id=" + rs.getGeneralInt("pd_id"));
			}
		}
	}

	@Override
	public void restoreYqty(double tqty, Integer adid) {
		Object[] id = getFieldsDataByCondition("applicationdetail", new String[] { "ad_id", "ad_yqty", "ad_qty" }, "ad_id=" + adid);
		Object y = getFieldDataByCondition("purchaseDETAIL", "sum(nvl(pd_qty,0)+nvl(pd_cancelqty,0))", "pd_sourcedetail=" + adid);
		y = y == null ? 0 : y;
		if (id != null) {
			if (NumberUtil.formatDouble(Double.parseDouble(y.toString()) + tqty, 2) > NumberUtil.formatDouble(
					Double.valueOf(id[2].toString()), 2)) {
				BaseUtil.showError("数量超出来源请购数量,超出数量:" + (Double.parseDouble(y.toString()) + tqty - Double.parseDouble(id[2].toString())));
			} else {
				updateByCondition("applicationdetail", "ad_yqty=nvl(ad_yqty,0)+" + tqty, "ad_id=" + adid);
				//updateAppStatus(adid);
				applicationDao.checkAdQty(adid);
			}
		}
	}
}
