package com.uas.erp.dao.common.impl;

import java.util.List;
import java.util.Map;

import org.hsqldb.lib.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.APBillDao;
import com.uas.erp.dao.common.APCheckDao;
import com.uas.erp.model.Employee;

@Repository
public class APBillDaoImpl extends BaseDao implements APBillDao {

	@Autowired
	private APCheckDao APCheckDao;

	final static String insertbillout = " insert into BillOutAP(bi_id,bi_code,bi_vendcode,bi_vendname,"
			+ "bi_recorder,bi_indate,bi_status,bi_currency,bi_rate," + "bi_statuscode,bi_refno,bi_date)values(?,?,?,?,?"
			+ ",sysdate,?,?,?,'ENTERING',?,sysdate)";
	final static String insertbilloutdetail = " insert into BillOutAPDetail(ard_id,ard_code,ard_biid,ard_detno,ard_orderid,"
			+ "ard_ordercode,ard_orderdetno,ard_orderamount,ard_prodcode,ard_qty,ard_nowqty,ard_price,ard_taxrate,ard_status,"
			+ "ard_nowprice,ard_statuscode,ard_costprice)values(BILLOUTAPDETAIL_SEQ.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'ENTERING',?)";

	/**
	 * 应收发票转销BillOutAP主表
	 * 
	 * @author madan 2014-3-14 09:47:43
	 */
	@Override
	public Object[] turnBillOutAP(String language, String vcode, String curr, Employee employee, Object bidate, Object refno) {
		String code = sGetMaxNumber("BillOutAP", 2);
		int id = getSeqId("BILLOUTAP_SEQ");
		Object vend = getFieldDataByCondition("Vendor", "ve_name", "ve_code='" + vcode + "'");
		Object rate = getFieldDataByCondition("Currencys", "cr_rate", "cr_name='" + curr + "'");
		execute(insertbillout, new Object[] { id, code, vcode, vend, employee.getEm_name(), BaseUtil.getLocalMessage("ENTERING", language),
				curr, rate, refno });
		if (bidate != null && !"".equals(bidate)) {
			execute("update billoutap set bi_date=to_date('" + bidate + "','yyyy-mm-dd') where bi_id=" + id);
		}
		return new Object[] { code, id };
	}

	/**
	 * 应收发票转销BillOutAPDetail
	 * 
	 * @author madan 2014-3-14 10:15:26
	 */
	@Override
	public void turnBillOutAPDetail(String no, int abdid, int biid, int detno, Double qty, Double price) {
		Object[] objs = getFieldsDataByCondition("APBillDetail", new String[] { "abd_code", "abd_detno", "abd_apamount", "abd_payamount",
				"abd_qty", "abd_thisvoprice", "abd_prodcode", "abd_taxrate", "abd_ordercode", "abd_costprice" }, "abd_id=" + abdid);
		execute(insertbilloutdetail, new Object[] { no, biid, detno, abdid, objs[0], objs[1], objs[2], objs[6], objs[4], qty, objs[5],
				objs[7], BaseUtil.getLocalMessage("ENTERING"), price, objs[9] });
		execute("update BillOutAPDetail set ard_nowbalance=round(nvl(ard_nowqty,0)*round(nvl(ard_nowprice,0),8),2) where ard_biid=" + biid);
		execute("update BillOutAPDetail set ard_taxamount=round(ard_nowbalance*nvl(ard_taxrate,0)/(100+nvl(ard_taxrate,0)),2) where ard_biid="
				+ biid);
		// 修改状态
		updateByCondition("APBillDetail", "abd_yqty=nvl(abd_yqty,0)+(" + qty + ")", "abd_id=" + abdid);
	}

	static final String CHECK_YQTY = "SELECT abd_code,abd_detno,abd_qty FROM APBillDetail WHERE abd_id=? and abs(abd_qty)<abs(?)";

	/**
	 * 1.判断通知单状态 2.判断thisqty ≤ qty +CQTY - yqty
	 */
	@Override
	public void checkyqty(List<Map<Object, Object>> datas) {
		int id = 0;
		Object y = 0;
		SqlRowList rs = null;
		boolean bool = false;
		Object[] ars = null;
		for (Map<Object, Object> d : datas) {
			id = Integer.parseInt(d.get("abd_id").toString());
			ars = getFieldsDataByCondition("APBillDetail left join APBill on abd_abid=ab_id", "ab_code,abd_detno,abd_abid", "abd_id=" + id);
			if (ars != null) {
				bool = checkIf("APBill", "ab_id=" + ars[2] + " and ab_statuscode='POSTED'");
				if (!bool) {
					BaseUtil.showError("应付发票:" + ars[0] + " 未过账,无法开票!");
				}
				y = getFieldDataByCondition("BillOutAPDetail", "sum(nvl(ard_nowqty,0))", "ard_orderid=" + id);
				y = y == null ? 0 : y;
				rs = queryForRowSet(CHECK_YQTY, id, NumberUtil.add(y.toString(), d.get("abd_thisvoqty").toString()));
				if (rs.next()) {
					StringBuffer sb = new StringBuffer("本次数量填写超出可转数量,发票单号:").append(rs.getString("abd_code")).append(",行号:")
							.append(rs.getInt("abd_detno")).append(",发票数:").append(rs.getDouble("abd_qty")).append(",已转发票记录数:").append(y)
							.append(",本次数:").append(d.get("abd_thisvoqty"));
					BaseUtil.showError(sb.toString());
				}
			}
		}
	}

	static final String CHECK_YQTY1 = "SELECT abd_code,abd_detno,abd_qty FROM APBillDetail WHERE abd_id=? and abs(abd_qty)<abs(?)";
	static final String CHECK_YQTY2 = "SELECT pi_inoutno,pd_pdno,pd_inqty+pd_outqty pd_qty,pd_piclass FROM ProdIODetail left join ProdInOut on pd_piid=pi_id WHERE pd_id=? and abs(nvl(pd_inqty,0)+nvl(pd_outqty,0))<abs(?)";

	/**
	 * 1.判断通知单状态 2.判断thisqty ≤ qty +CQTY - yqty
	 */
	@Override
	public void checkqty(List<Map<Object, Object>> datas) {
		int id = 0;
		Object y = 0;
		Object sourcetype = null;
		SqlRowList rs = null;
		boolean bool = false;
		Object[] ars = null;
		Double tqty = 0.0;
		Double qty = 0.0;
		for (Map<Object, Object> d : datas) {
			id = Integer.parseInt(d.get("pd_id").toString());
			sourcetype = d.get("pi_type").toString();
			tqty = Double.parseDouble(d.get("pd_thisvoqty").toString());
			qty = Double.parseDouble(d.get("pd_showqty").toString());
			if (qty < 0 && tqty > 0) {
				BaseUtil.showError("开票数量为负数的，本次对账数不能为正数！");
			}
			if (qty > 0 && tqty < 0) {
				BaseUtil.showError("开票数量为正数的，本次对账数不能为负数！");
			}
			if (tqty == 0) {
				BaseUtil.showError("本次对账数不能为0！");
			}
			if ("APBILL".equals(sourcetype)) {
				ars = getFieldsDataByCondition("APBillDetail left join APBill on abd_abid=ab_id", "ab_code,abd_detno,abd_abid", "abd_id="
						+ id);
				if (ars != null) {
					bool = checkIf("APBill", "ab_id=" + ars[2] + " and ab_statuscode='POSTED'");
					if (!bool) {
						BaseUtil.showError("应付发票:" + ars[0] + " 未过账,无法对账!");
					}
					y = getFieldDataByCondition("APCheckDetail", "nvl(sum(nvl(ad_qty,0)),0)", "ad_sourcedetailid=" + id);
					y = y == null ? 0 : y;
					rs = queryForRowSet(CHECK_YQTY1, id, Double.parseDouble(y.toString()) + tqty);
					if (rs.next()) {
						StringBuffer sb = new StringBuffer("本次数量填写超出可转数量,发票单号:").append(rs.getString("abd_code")).append(",行号:")
								.append(rs.getInt("abd_detno")).append(",发票数:").append(rs.getDouble("abd_qty")).append(",已转对账数:").append(y)
								.append(",本次数:").append(tqty);
						BaseUtil.showError(sb.toString());
					}
				}
			} else if ("PRODINOUT".equals(sourcetype)) {
				ars = getFieldsDataByCondition("ProdIODetail", "pd_inoutno,pd_pdno,pd_piid,pd_piclass", "pd_id=" + id);
				if (ars != null) {
					bool = checkIf("ProdInOut", "pi_id=" + ars[2] + " and pi_statuscode='POSTED'");
					if (!bool) {
						BaseUtil.showError(ars[3] + ":" + ars[0] + " 未过账,无法对账!");
					}
					y = getFieldDataByCondition("APCheckDetail", "nvl(sum(nvl(ad_qty,0)),0)", "ad_sourcedetailid=" + id);
					y = y == null ? 0 : y;
					rs = queryForRowSet(CHECK_YQTY2, id, Double.parseDouble(y.toString()) + tqty);
					if (rs.next()) {
						StringBuffer sb = new StringBuffer("本次数量填写超出可转数量,").append(rs.getString("pd_piclass")).append("号:")
								.append(rs.getString("pd_inoutno")).append(",行号:").append(rs.getInt("pd_pdno")).append(",数量:")
								.append(rs.getDouble("pd_qty")).append(",已转对账数:").append(y).append(",本次数:").append(tqty);
						BaseUtil.showError(sb.toString());
					}
				}
			}
		}
	}

	@Override
	public Object[] turnAPCheck(String vcode, String curr, Object soureid, Object sourcetype) {
		String code = sGetMaxNumber("APCheck", 2);
		int id = getSeqId("APCHECK_SEQ");
		if ("APBILL".equals(sourcetype)) {
			execute("INSERT INTO APCheck(ac_id, ac_code, ac_date, ac_apdate, ac_currency, ac_rate, ac_vendcode, ac_vendname,"
					+ "ac_buyercode, ac_buyername, ac_paymentcode, ac_paymentname, ac_recorder, ac_status, ac_statuscode)" + " select "
					+ id + ",'" + code
					+ "',sysdate,ab_paydate,ab_currency,ab_rate,ab_vendcode,ve_name,'',ab_buyer, ab_paymentcode, ab_payments,'"
					+ SystemSession.getUser().getEm_name() + "', '" + BaseUtil.getLocalMessage("ENTERING") + "','ENTERING'"
					+ " from apbill left join vendor on ab_vendcode=ve_code where ab_id=" + soureid);
		} else if ("PRODINOUT".equals(sourcetype)) {
			execute("INSERT INTO APCheck(ac_id, ac_code, ac_date, ac_apdate, ac_currency, ac_rate, ac_vendcode, ac_vendname,"
					+ "ac_buyercode, ac_buyername, ac_paymentcode, ac_paymentname, ac_recorder, ac_status, ac_statuscode)"
					+ " select "
					+ id
					+ ",'"
					+ code
					+ "',sysdate,pi_paydate,pi_currency,pi_rate,pi_receivecode,pi_receivename,pi_sellercode,pi_sellername,pi_paymentcode,pi_payment,'"
					+ SystemSession.getUser().getEm_name() + "', '" + BaseUtil.getLocalMessage("ENTERING") + "','ENTERING'"
					+ " from ProdInOut where pi_id=" + soureid);
		}
		execute("update APCheck set ac_buyername=(select ve_buyername from vendor where ve_code=ac_vendcode) where nvl(ac_buyername,' ')=' ' and ac_id="
				+ id);
		execute("update APCheck set ac_buyercode=(select max(em_code) from employee where em_name=ac_buyercode) where nvl(ac_buyercode,' ')=' ' and ac_id="
				+ id);
		execute("update APCheck set ac_paymentcode=(select pa_code from payments where pa_name=ac_paymentname and pa_class='付款方式' and pa_auditstatuscode='AUDITED') where nvl(ac_paymentcode,' ')=' ' and ac_id="
				+ id);
		return new Object[] { code, id };
	}

	@Override
	public void turnAPCheckDetail(String no, Object sourcedetailid, int id, int detno, Double qty, Double price, Object sourcetype) {
		if ("APBILL".equals(sourcetype)) {
			execute("INSERT INTO APCheckDetail(ad_id, ad_acid, ad_detno, ad_sourcecode, ad_sourcedetno, ad_sourcedetailid, ad_sourcetype, ad_inoutno,"
					+ "ad_pdid, ad_qty, ad_price, ad_taxrate, ad_b2bqty, ad_abclass)"
					+ " select APCHECKDETAIL_SEQ.NEXTVAL,"
					+ id
					+ ","
					+ detno
					+ ",abd_code,abd_detno,abd_id,'APBILL',abd_pdinoutno,abd_pdid,"
					+ qty
					+ ",abd_price,abd_taxrate,"
					+ qty
					+ ",ab_class from APBill,APBillDetail where ab_id=abd_abid and abd_id=" + sourcedetailid);
			// 修改状态
			updateByCondition("APBillDetail", "abd_ycheck=nvl(abd_ycheck,0)+(" + qty + ")", "abd_id=" + sourcedetailid);
		} else if ("PRODINOUT".equals(sourcetype)) {
			execute("INSERT INTO APCheckDetail(ad_id, ad_acid, ad_detno, ad_sourcecode, ad_sourcedetno, ad_sourcedetailid, ad_sourcetype,"
					+ " ad_qty, ad_price, ad_taxrate, ad_b2bqty)" + " select APCHECKDETAIL_SEQ.NEXTVAL," + id + "," + detno
					+ ",pd_inoutno,pd_pdno,pd_id,'PRODINOUT'," + qty + "," + price + ",pd_taxrate," + qty
					+ " from ProdIODetail where pd_id=" + sourcedetailid);
			execute("update APCheckDetail set ad_inoutno=ad_sourcecode,ad_pdid=ad_sourcedetailid where ad_acid=" + id);
			// 修改状态
			updateByCondition("ProdIODetail", "pd_ycheck=nvl(pd_ycheck,0)+(" + qty + ")", "pd_id=" + sourcedetailid);
		}
		execute("update APCheckDetail set ad_amount=round(nvl(ad_qty*ad_price,0),2) where ad_acid=" + id);
	}

	public void apbill_return_deletedetail(int abdid, int sourcedetailid, Object sourcekind, int adid) {
		if (adid != 0) {
			execute("update APCheckDetail set ad_yqty=nvl((Select Sum(nvl(Abd_Qty,0)) From Apbilldetail Where Abd_Adid=" + adid
					+ " and abd_id<>" + abdid + "),0) where ad_id=" + adid);
			int ac_id = getFieldValue("APCheckDetail", "ad_acid", "ad_id=" + adid, Integer.class);
			APCheckDao.updateBillStatus(ac_id);
		} else {
			if (sourcekind != null && !StringUtil.isEmpty(sourcekind.toString())) {
				if ("PRODIODETAIL".equals(sourcekind)) {
					execute("update prodiodetail set pd_showinvoqty=nvl((Select Sum(nvl(Abd_Qty,0)) From Apbilldetail Where abd_pdid=pd_id and abd_sourcekind='PRODIODETAIL' and abd_id<>"
							+ abdid + "),0) where pd_id=" + sourcedetailid);
					execute("update prodiodetail set pd_showinvototal=nvl((Select Sum(round(nvl(Abd_Qty,0)*nvl(abd_thisvoprice,0),2)) From Apbilldetail Where Abd_Sourcedetailid=pd_id and abd_sourcekind='PRODIODETAIL' and abd_id<>"
							+ abdid + "),0) where pd_id=" + sourcedetailid);
					updateByCondition("prodiodetail", "pd_auditstatus='AUDITED'", "pd_showinvoqty=0 and pd_id=" + sourcedetailid);
					updateByCondition("prodiodetail", "pd_auditstatus='TURNAR'",
							"abs(NVL(pd_showinvoqty,0))>0 and abs(NVL(pd_showinvoqty,0))=abs(NVL(pd_inqty,0)+NVL(pd_outqty,0)) and pd_id="
									+ sourcedetailid);
					updateByCondition("prodiodetail", "pd_auditstatus='PARTAR'",
							"abs(NVL(pd_showinvoqty,0))>0 and abs(NVL(pd_showinvoqty,0))<abs(NVL(pd_inqty,0)+NVL(pd_outqty,0)) and pd_id="
									+ sourcedetailid);
					// 修改出入库单开票状态
					Long pi_id = getFieldValue("prodiodetail", "pd_piid", "pd_id=" + sourcedetailid, Long.class);
					updateProdIOBillStatus(pi_id);
				} else if ("ESTIMATE".equals(sourcekind)) {
					execute("update estimatedetail set Esd_Showinvoqty=nvl((Select Sum(nvl(Abd_Qty,0)) From Apbilldetail Where Abd_Sourcedetailid=Esd_Id and abd_sourcekind='ESTIMATE' and abd_id<>"
							+ abdid + "),0) where esd_id=" + sourcedetailid);
					execute("update estimatedetail set esd_showinvototal=nvl((Select Sum(round(nvl(Abd_Qty,0)*nvl(abd_thisvoprice,0),2)) From Apbilldetail Where Abd_Sourcedetailid=Esd_Id and abd_sourcekind='ESTIMATE' and abd_id<>"
							+ abdid + "),0) where esd_id=" + sourcedetailid);
					updateByCondition("estimatedetail", "esd_statuscode='AUDITED'", "esd_showinvoqty=0 and esd_id=" + sourcedetailid);
					updateByCondition("estimatedetail", "esd_statuscode='TURNAR'",
							"abs(NVL(Esd_Showinvoqty,0))>0 and abs(NVL(Esd_Showinvoqty,0))=abs(NVL(Esd_qty,0)) and esd_id="
									+ sourcedetailid);
					updateByCondition("estimatedetail", "esd_statuscode='PARTAR'",
							"abs(NVL(Esd_Showinvoqty,0))>0 and abs(NVL(Esd_Showinvoqty,0))<abs(NVL(Esd_qty,0)) and esd_id="
									+ sourcedetailid);
					// 修改暂估单开票状态
					int es_id = getFieldValue("estimatedetail", "esd_esid", "esd_id=" + sourcedetailid, Integer.class);
					updateEstimateBillStatus(es_id);
				}
			}
		}
	}

	/**
	 * 修改出入库单开票状态
	 */
	public void updateProdIOBillStatus(Long pi_id) {
		execute("update prodinout set pi_billstatuscode='UNINVOICE',pi_billstatus='" + BaseUtil.getLocalMessage("UNINVOICE")
				+ "' where pi_id=" + pi_id
				+ " and not exists(select 1 from prodiodetail where pd_piid=pi_id and abs(NVL(pd_showinvoqty,0))>0)");
		execute("update prodinout set pi_billstatuscode='PARTAR',pi_billstatus='"
				+ BaseUtil.getLocalMessage("PARTAR")
				+ "' where pi_id="
				+ pi_id
				+ " and nvl(pi_billstatuscode,' ')<>'PARTAR' and exists (select 1 from prodiodetail where pd_piid=pi_id and abs(NVL(pd_showinvoqty,0))>0)");
		execute("update prodinout set pi_billstatuscode='TURNAR',pi_billstatus='"
				+ BaseUtil.getLocalMessage("TURNAR")
				+ "' where pi_id="
				+ pi_id
				+ " and nvl(pi_billstatuscode,' ')<>'TURNAR' and not exists(select 1 from prodiodetail where pd_piid=pi_id and abs(nvl(pd_inqty,0)+nvl(pd_outqty,0))-abs(NVL(pd_showinvoqty,0))>0)");
	}

	/**
	 * 修改暂估单开票状态
	 */
	public void updateEstimateBillStatus(Integer es_id) {
		execute("update Estimate set es_invostatuscode='UNINVOICE',es_invostatus='" + BaseUtil.getLocalMessage("UNINVOICE")
				+ "' where es_id=" + es_id
				+ " and not exists(select 1 from estimatedetail where esd_esid=es_id and abs(NVL(Esd_Showinvoqty,0))>0)");
		execute("update Estimate set es_invostatuscode='PARTAR',es_invostatus='"
				+ BaseUtil.getLocalMessage("PARTAR")
				+ "' where es_id="
				+ es_id
				+ " and nvl(es_invostatuscode,' ')<>'PARTAR' and exists (select 1 from estimatedetail where esd_esid=es_id and abs(NVL(Esd_Showinvoqty,0))>0)");
		execute("update Estimate set es_invostatuscode='TURNAR',es_invostatus='"
				+ BaseUtil.getLocalMessage("TURNAR")
				+ "' where es_id="
				+ es_id
				+ " and nvl(es_invostatuscode,' ')<>'TURNAR' and not exists(select 1 from estimatedetail where esd_esid=es_id and abs(nvl(esd_qty,0))-abs(NVL(Esd_Showinvoqty,0))>0)");
	}

	/**
	 * 修改发票来源已转数
	 */
	public void updateSourceYqty(int abdid, int sourcedetailid, Object sourcekind) {
		SqlRowList rs = queryForRowSet("select abd_adid from apbilldetail where abd_id=? and nvl(abd_adid,0)<>0", abdid);
		if (rs.next()) {
			execute("update apcheckdetail set ad_yqty=nvl((Select Sum(nvl(Abd_Qty,0)) From APbilldetail Where Abd_Adid=ad_id),0) where ad_id="
					+ rs.getObject("abd_adid"));
			Long ac_id = getFieldValue("apcheckdetail", "ad_acid", "ad_id=" + rs.getObject("abd_adid"), Long.class);
			execute("update apcheck set ac_billstatuscode='UNINVOICE',ac_billstatus='" + BaseUtil.getLocalMessage("UNINVOICE")
					+ "' where ac_id=" + ac_id
					+ " and not exists(select 1 from apcheckdetail where ad_acid=ac_id and abs(NVL(ad_yqty,0))>0)");
			execute("update apcheck set ac_billstatuscode='PARTAR',ac_billstatus='"
					+ BaseUtil.getLocalMessage("PARTAR")
					+ "' where ac_id="
					+ ac_id
					+ " and nvl(ac_billstatuscode,' ')<>'PARTAR' and exists (select 1 from apcheckdetail where ad_acid=ac_id and abs(NVL(ad_yqty,0))>0)");
			execute("update apcheck set ac_billstatuscode='TURNAR',ac_billstatus='"
					+ BaseUtil.getLocalMessage("TURNAR")
					+ "' where ac_id="
					+ ac_id
					+ " and nvl(ac_billstatuscode,' ')<>'TURNAR' and not exists(select 1 from apcheckdetail where ad_acid=ac_id and abs(nvl(ad_qty,0))-abs(NVL(ad_yqty,0))>0)");
		} else {
			if ("PRODIODETAIL".equals(sourcekind)) {
				execute("update prodiodetail set pd_showinvoqty=nvl((Select Sum(nvl(Abd_Qty,0)) From Apbilldetail Where Abd_Sourcedetailid=pd_id and abd_sourcekind='PRODIODETAIL'),0) where pd_id="
						+ sourcedetailid);
				execute("update prodiodetail set pd_showinvototal=nvl((Select Sum(round(nvl(Abd_Qty,0)*nvl(abd_thisvoprice,0),2)) From Apbilldetail Where Abd_Sourcedetailid=pd_id and abd_sourcekind='PRODIODETAIL'),0) where pd_id="
						+ sourcedetailid);
				updateByCondition("prodiodetail", "pd_auditstatus='AUDITED'", "pd_showinvoqty=0 and pd_id=" + sourcedetailid);
				updateByCondition("prodiodetail", "pd_auditstatus='TURNAR'",
						"abs(NVL(pd_showinvoqty,0))>0 and abs(NVL(pd_showinvoqty,0))=abs(NVL(pd_inqty,0)+NVL(pd_outqty,0)) and pd_id="
								+ sourcedetailid);
				updateByCondition("prodiodetail", "pd_auditstatus='PARTAR'",
						"abs(NVL(pd_showinvoqty,0))>0 and abs(NVL(pd_showinvoqty,0))<abs(NVL(pd_inqty,0)+NVL(pd_outqty,0)) and pd_id="
								+ sourcedetailid);
				// 修改出入库单开票状态
				Long pi_id = getFieldValue("prodiodetail", "pd_piid", "pd_id=" + sourcedetailid, Long.class);
				updateProdIOBillStatus(pi_id);
			} else if ("ESTIMATE".equals(sourcekind)) {
				execute("update estimatedetail set esd_showinvoqty=nvl((Select Sum(nvl(Abd_Qty,0)) From Apbilldetail Where Abd_Sourcedetailid=esd_id and abd_sourcekind='ESTIMATE'),0) where esd_id="
						+ sourcedetailid);
				execute("update estimatedetail set esd_showinvototal=nvl((Select Sum(round(nvl(Abd_Qty,0)*nvl(abd_thisvoprice,0),2)) From Apbilldetail Where Abd_Sourcedetailid=esd_id and abd_sourcekind='ESTIMATE'),0) where esd_id="
						+ sourcedetailid);
				updateByCondition("estimatedetail", "esd_statuscode='AUDITED'", "esd_showinvoqty=0 and esd_id=" + sourcedetailid);
				updateByCondition("estimatedetail", "esd_statuscode='TURNAR'",
						"abs(NVL(Esd_Showinvoqty,0))>0 and abs(NVL(Esd_Showinvoqty,0))=abs(NVL(Esd_qty,0)) and esd_id=" + sourcedetailid);
				updateByCondition("estimatedetail", "esd_statuscode='PARTAR'",
						"abs(NVL(Esd_Showinvoqty,0))>0 and abs(NVL(Esd_Showinvoqty,0))<abs(NVL(Esd_qty,0)) and esd_id=" + sourcedetailid);
				// 修改暂估单开票状态
				int es_id = getFieldValue("estimatedetail", "esd_esid", "esd_id=" + sourcedetailid, Integer.class);
				updateEstimateBillStatus(es_id);
			}
		}
	}
}
