package com.uas.erp.dao.common.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ARBillDao;
import com.uas.erp.model.Employee;

@Repository
public class ARBillDaoImpl extends BaseDao implements ARBillDao {
	final static String insertbillout = " insert into BillOut(bi_id,bi_code,bi_custcode,bi_custname,bi_sellercode,"
			+ "bi_seller,BI_PAYMENTSCODE,BI_PAYMENTSMETHOD,bi_recorder,bi_indate,bi_status,bi_currency,bi_rate,"
			+ "bi_statuscode,bi_departmentcode,bi_department,bi_cop,bi_sendkind,bi_date) values (?,?,?,?,?,?"
			+ ",?,?,?,sysdate,?,?,?,'ENTERING',?,?,?,?,sysdate)";
	final static String insertbilloutdetail = " insert into BillOutDetail(ard_id,ard_code,ard_biid,ard_detno,ard_orderid,"
			+ "ard_ordercode,ard_orderdetno,ard_orderamount,ard_prodcode,ard_qty,ard_nowqty,ard_price,ard_taxrate,ard_status,ard_nowprice,"
			+ "ard_statuscode,ard_costprice)values(BILLOUTDETAIL_SEQ.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'ENTERING',?)";

	/**
	 * 应收发票转销BillOut主表
	 * 
	 * @author madan 2014-3-14 09:47:43
	 */
	@Override
	public Object[] turnBillOut(String language, String vcode, String curr, Employee employee, Object bidate, String abid, Object Sendtype) {
		String code = sGetMaxNumber("BillOut", 2);
		int id = getSeqId("BILLOUT_SEQ");
		Object[] cust = getFieldsDataByCondition("Customer", new String[] { "cu_name", "cu_sellercode", "cu_sellername", "cu_paymentscode",
				"cu_payments" }, "cu_code='" + vcode + "'");
		if (cust == null) {
			BaseUtil.showError("客户[" + vcode + "]不存在！");
		}
		Object rate = getFieldDataByCondition("Currencys", "cr_rate", "cr_name='" + curr + "'");

		Object[] os = getFieldsDataByCondition("arbill", new String[] { "ab_sellercode", "ab_seller", "ab_sellerid", "ab_departmentcode",
				"ab_departmentname", "ab_cop", "ab_paymentcode", "ab_payments" }, "ab_id='" + abid + "'");
		if (os[6] == null) {
			execute(insertbillout,
					new Object[] { id, code, vcode, cust[0], os[0], os[1], cust[3], cust[4], employee.getEm_name(),
							BaseUtil.getLocalMessage("ENTERING", language), curr, rate, os[3], os[4], os[5], Sendtype });
		} else {
			execute(insertbillout,
					new Object[] { id, code, vcode, cust[0], os[0], os[1], os[6], os[7], employee.getEm_name(),
							BaseUtil.getLocalMessage("ENTERING", language), curr, rate, os[3], os[4], os[5], Sendtype });
		}
		if (bidate != null && !"".equals(bidate)) {
			execute("update billout set bi_date=to_date('" + bidate + "','yyyy-mm-dd') where bi_id=" + id);
		}
		return new Object[] { code, id };
	}

	/**
	 * 应收发票转销BillOutDetail
	 * 
	 * @author madan 2014-3-14 10:15:26
	 */
	@Override
	public void turnBillOutDetail(String no, int abdid, int biid, int detno, Double qty, Double price) {
		Object[] objs = getFieldsDataByCondition("ARBillDetail", new String[] { "abd_code", "abd_detno", "abd_aramount", "abd_payamount",
				"abd_qty", "abd_thisvoprice", "abd_prodcode", "abd_taxrate", "abd_ordercode", "abd_costprice" }, "abd_id=" + abdid);
		execute(insertbilloutdetail, new Object[] { no, biid, detno, abdid, objs[0], objs[1], objs[2], objs[6], objs[4], qty, objs[5],
				objs[7], BaseUtil.getLocalMessage("ENTERING"), price, objs[9] });
		execute("update BillOutDetail set ard_nowbalance=round(nvl(ard_nowqty,0)*nvl(ard_nowprice,0),2) where ard_biid=" + biid);
		execute("update BillOutDetail set ard_taxamount=round(ard_nowbalance*nvl(ard_taxrate,0)/(100+nvl(ard_taxrate,0)),4) where ard_biid="
				+ biid);
		// 修改状态
		updateByCondition("ARBillDetail", "abd_yqty=nvl(abd_yqty,0)+(" + qty + ")", "abd_id=" + abdid);
	}

	static final String CHECK_YQTY = "SELECT abd_code,abd_detno,abd_qty FROM ARBillDetail WHERE abd_id=? and abs(abd_qty)<abs(?)";

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
			ars = getFieldsDataByCondition("ARBillDetail left join ARBill on abd_abid=ab_id", "ab_code,abd_detno,abd_abid", "abd_id=" + id);
			if (ars != null) {
				bool = checkIf("ARBill", "ab_id=" + ars[2] + " and ab_statuscode='POSTED'");
				if (!bool) {
					BaseUtil.showError("应收发票:" + ars[0] + " 未过账,无法开票!");
				}
				y = getFieldDataByCondition("BillOutDetail", "sum(nvl(ard_nowqty,0))", "ard_orderid=" + id);
				y = y == null ? 0 : y;
				rs = queryForRowSet(CHECK_YQTY, id,
						Double.parseDouble(y.toString()) + Double.parseDouble(d.get("abd_thisvoqty").toString()));
				if (rs.next()) {
					StringBuffer sb = new StringBuffer("本次数量填写超出可转数量,发票单号:").append(rs.getString("abd_code")).append(",行号:")
							.append(rs.getInt("abd_detno")).append(",发票数:").append(rs.getDouble("abd_qty")).append(",已转发票记录数:").append(y)
							.append(",本次数:").append(d.get("abd_thisvoqty"));
					BaseUtil.showError(sb.toString());
				}
			}
		}
	}

	static final String CHECK_YQTY1 = "SELECT abd_code,abd_detno,abd_qty FROM ARBillDetail WHERE abd_id=? and abs(abd_qty)<abs(?)";
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
			if ("ARBILL".equals(sourcetype)) {
				ars = getFieldsDataByCondition("ARBillDetail left join ARBill on abd_abid=ab_id", "ab_code,abd_detno,abd_abid", "abd_id="
						+ id);
				if (ars != null) {
					bool = checkIf("ARBill", "ab_id=" + ars[2] + " and ab_statuscode='POSTED'");
					if (!bool) {
						BaseUtil.showError("应收发票:" + ars[0] + " 未过账,无法对账!");
					}
					y = getFieldDataByCondition("ARCheckDetail", "nvl(sum(nvl(ad_qty,0)),0)", "ad_sourcedetailid=" + id);
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
					y = getFieldDataByCondition("ARCheckDetail", "nvl(sum(nvl(ad_qty,0)),0)", "ad_sourcedetailid=" + id);
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
	public Object[] turnARCheck(String vcode, String curr, Object soureid, Object sourcetype) {
		String code = sGetMaxNumber("ARCheck", 2);
		int id = getSeqId("ARCHECK_SEQ");
		if ("ARBILL".equals(sourcetype)) {
			execute("INSERT INTO ARCheck(ac_id, ac_code, ac_date, ac_ardate, ac_currency, ac_rate, ac_custcode, ac_custname,"
					+ "ac_sellercode, ac_sellername, ac_paymentcode, ac_paymentname, ac_recorder, ac_status, ac_statuscode)" + " select "
					+ id + ",'" + code + "',sysdate,ab_paydate,ab_currency,ab_rate,ab_custcode,cu_name,'',ab_seller,'',ab_payments,'"
					+ SystemSession.getUser().getEm_name() + "', '" + BaseUtil.getLocalMessage("ENTERING") + "','ENTERING'"
					+ " from arbill left join Customer on ab_custcode=cu_code where ab_id=" + soureid);
		} else if ("PRODINOUT".equals(sourcetype)) {
			execute("INSERT INTO ARCheck(ac_id, ac_code, ac_date, ac_ardate, ac_currency, ac_rate, ac_custcode, ac_custname,"
					+ "ac_sellercode, ac_sellername, ac_paymentcode, ac_paymentname, ac_recorder, ac_status, ac_statuscode)"
					+ " select "
					+ id
					+ ",'"
					+ code
					+ "',sysdate,pi_paydate,pi_currency,pi_rate,pi_arcode,pi_arname,pi_sellercode,pi_sellername,pi_paymentcode,pi_payment,'"
					+ SystemSession.getUser().getEm_name() + "', '" + BaseUtil.getLocalMessage("ENTERING") + "','ENTERING'"
					+ " from ProdInOut where pi_id=" + soureid);
		}
		execute("update ARCheck set ac_sellercode=(select max(em_code) from employee where em_name=ac_sellername) where nvl(ac_sellercode,' ')=' ' and ac_id="
				+ id);
		execute("update ARCheck set ac_paymentcode=(select pa_code from payments where pa_name=ac_paymentname and pa_class='收款方式' and pa_auditstatuscode='AUDITED') where nvl(ac_paymentcode,' ')=' ' and ac_id="
				+ id);
		return new Object[] { code, id };
	}

	@Override
	public void turnARCheckDetail(String no, Object sourcedetailid, int id, int detno, Double qty, Double price, Object sourcetype) {
		if ("ARBILL".equals(sourcetype)) {
			execute("INSERT INTO ARCheckDetail(ad_id, ad_acid, ad_detno, ad_sourcecode, ad_sourcedetno, ad_sourcedetailid, ad_sourcetype, ad_inoutno,"
					+ "ad_pdid, ad_qty, ad_price, ad_taxrate, ad_pocode, ad_custprodcode, ad_custprodspec, ad_abclass)"
					+ " select ARCHECKDETAIL_SEQ.NEXTVAL,"
					+ id
					+ ","
					+ detno
					+ ",abd_code,abd_detno,abd_id,'ARBILL',abd_pdinoutno,abd_pdid,"
					+ qty
					+ ",abd_price,abd_taxrate, pd_pocode, pd_custprodcode, pd_custprodspec, ab_class "
					+ " from arbill left join arbilldetail on ab_id=abd_abid left join ProdIODetail on ABD_PDID=pd_id where abd_id="
					+ sourcedetailid);
			// 修改状态
			updateByCondition("ARBillDetail", "abd_ycheck=nvl(abd_ycheck,0)+(" + qty + ")", "abd_id=" + sourcedetailid);
		} else if ("PRODINOUT".equals(sourcetype)) {
			execute("INSERT INTO ARCheckDetail(ad_id, ad_acid, ad_detno, ad_sourcecode, ad_sourcedetno, ad_sourcedetailid, ad_sourcetype, ad_pocode,"
					+ "ad_custprodspec,ad_custprodcode, ad_qty, ad_price, ad_taxrate)"
					+ " select ARCHECKDETAIL_SEQ.NEXTVAL,"
					+ id
					+ ","
					+ detno
					+ ",pd_inoutno,pd_pdno,pd_id,'PRODINOUT',pd_pocode,pd_custprodspec,pd_custprodcode,"
					+ qty
					+ ","
					+ price
					+ ",pd_taxrate" + " from ProdIODetail where pd_id=" + sourcedetailid);
			execute("update ARCheckDetail set ad_inoutno=ad_sourcecode,ad_pdid=ad_sourcedetailid where ad_acid=" + id);
			// 修改状态
			updateByCondition("ProdIODetail", "pd_ycheck=nvl(pd_ycheck,0)+(" + qty + ")", "pd_id=" + sourcedetailid);
		}
		execute("update ARCheckDetail set ad_amount=round(nvl(ad_qty*ad_price,0),2) where ad_acid=" + id);
	}

	@Override
	public void updateSourceYqty(int abdid, int sourcedetailid, Object sourcekind) {
		SqlRowList rs = queryForRowSet("select abd_adid from arbilldetail where abd_id=? and nvl(abd_adid,0)<>0", abdid);
		if (rs.next()) {
			execute("update archeckdetail set ad_yqty=nvl((Select Sum(nvl(Abd_Qty,0)) From Arbilldetail Where Abd_Adid=ad_id),0) where ad_id="
					+ rs.getObject("abd_adid"));
			Long ac_id = getFieldValue("archeckdetail", "ad_acid", "ad_id=" + rs.getObject("abd_adid"), Long.class);
			execute("update archeck set ac_billstatuscode='UNINVOICE',ac_billstatus='" + BaseUtil.getLocalMessage("UNINVOICE")
					+ "' where ac_id=" + ac_id
					+ " and not exists(select 1 from archeckdetail where ad_acid=ac_id and abs(NVL(ad_yqty,0))>0)");
			execute("update archeck set ac_billstatuscode='PARTAR',ac_billstatus='"
					+ BaseUtil.getLocalMessage("PARTAR")
					+ "' where ac_id="
					+ ac_id
					+ " and nvl(ac_billstatuscode,' ')<>'PARTAR' and exists (select 1 from archeckdetail where ad_acid=ac_id and abs(NVL(ad_yqty,0))>0)");
			execute("update archeck set ac_billstatuscode='TURNAR',ac_billstatus='"
					+ BaseUtil.getLocalMessage("TURNAR")
					+ "' where ac_id="
					+ ac_id
					+ " and nvl(ac_billstatuscode,' ')<>'TURNAR' and not exists(select 1 from archeckdetail where ad_acid=ac_id and abs(nvl(ad_qty,0))-abs(NVL(ad_yqty,0))>0)");
		} else {
			if ("PRODIODETAIL".equals(sourcekind)) {
				execute("update prodiodetail set pd_showinvoqty=nvl((Select Sum(nvl(Abd_Qty,0)) From Arbilldetail Where Abd_Sourcedetailid=pd_id and abd_sourcekind='PRODIODETAIL'),0) where pd_id="
						+ sourcedetailid);
				execute("update prodiodetail set pd_showinvototal=nvl((Select Sum(round(nvl(Abd_Qty,0)*nvl(abd_thisvoprice,0),2)) From Arbilldetail Where Abd_Sourcedetailid=pd_id and abd_sourcekind='PRODIODETAIL'),0) where pd_id="
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
			} else if ("GOODSSEND".equals(sourcekind)) {
				execute("update GOODSSENDDetail set gsd_showinvoqty=nvl((Select Sum(nvl(Abd_Qty,0)) From ARbilldetail Where Abd_Sourcedetailid=gsd_id and abd_sourcekind='GOODSSEND'),0) where gsd_id="
						+ sourcedetailid);
				execute("update GOODSSENDDetail set gsd_showinvototal=nvl((Select Sum(round(nvl(Abd_Qty,0)*nvl(abd_thisvoprice,0),2)) From ARbilldetail Where Abd_Sourcedetailid=gsd_id and abd_sourcekind='GOODSSEND'),0) where GSd_id="
						+ sourcedetailid);
				updateByCondition("GoodsSendDetail", "gsd_statuscode='AUDITED'", "gsd_showinvoqty=0 and gsd_id=" + sourcedetailid);
				updateByCondition("GoodsSendDetail", "gsd_statuscode='TURNAR'",
						"abs(NVL(gsd_showinvoqty,0))>0 and abs(NVL(gsd_showinvoqty,0))=abs(NVL(gsd_qty,0)) and gsd_id=" + sourcedetailid);
				updateByCondition("GoodsSendDetail", "gsd_statuscode='PARTAR'",
						"abs(NVL(gsd_showinvoqty,0))>0 and abs(NVL(gsd_showinvoqty,0))<abs(NVL(gsd_qty,0)) and gsd_id=" + sourcedetailid);
				// 修改暂估单开票状态
				int gs_id = getFieldValue("GoodsSendDetail", "gsd_gsid", "gsd_id=" + sourcedetailid, Integer.class);
				updateGoodsSendStatus(gs_id);
			}
		}
	}

	@Override
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

	@Override
	public void updateGoodsSendStatus(Integer gs_id) {
		execute("update GoodsSend set gs_invostatuscode='UNINVOICE',gs_invostatus='" + BaseUtil.getLocalMessage("UNINVOICE")
				+ "' where gs_id=" + gs_id
				+ " and not exists(select 1 from GoodsSenddetail where gsd_gsid=gs_id and abs(NVL(gsd_Showinvoqty,0))>0)");
		execute("update GoodsSend set gs_invostatuscode='PARTAR',gs_invostatus='"
				+ BaseUtil.getLocalMessage("PARTAR")
				+ "' where gs_id="
				+ gs_id
				+ " and nvl(gs_invostatuscode,' ')<>'PARTAR' and exists (select 1 from GoodsSenddetail where gsd_gsid=gs_id and abs(NVL(gsd_Showinvoqty,0))>0)");
		execute("update GoodsSend set gs_invostatuscode='TURNAR',gs_invostatus='"
				+ BaseUtil.getLocalMessage("TURNAR")
				+ "' where gs_id="
				+ gs_id
				+ " and nvl(gs_invostatuscode,' ')<>'TURNAR' and not exists(select 1 from GoodsSenddetail where gsd_gsid=gs_id and abs(nvl(gsd_qty,0))-abs(NVL(gsd_Showinvoqty,0))>0)");
	}
}
