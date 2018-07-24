package com.uas.erp.dao.common.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.APCheckDao;

@Repository
public class APCheckDaoImpl extends BaseDao implements APCheckDao {
	final static String GETAPBILLDETAIL = "SELECT abd_qty,abd_id,abd_ycheck FROM APBillDetail where abd_id=?";
	final static String GETAPBILLDETAIL2 = "SELECT abd_qty,abd_id,abd_ycheck FROM APBillDetail where abd_pdid=?";
	final static String GETProdioDetail = "SELECT pd_inqty-pd_outqty pd_qty,pd_id,pd_ycheck FROM ProdIODetail where pd_id=?";
	final static String GETAPCHECKDETAIL = "SELECT ad_sourcedetailid,ad_sourcetype,ad_qty,ad_pdid FROM APCheckDetail WHERE ad_id=?";

	public void restoreAPBill(int id) {
		SqlRowList rs = queryForRowSet(GETAPCHECKDETAIL, id);
		if (rs.next()) {
			boolean type = isDBSetting("autoCreateApBill");
			double yqty = rs.getDouble("ad_qty");
			if (type) {
				if ("APBILL".equals(rs.getString("ad_sourcetype"))) {
					SqlRowList rs1 = queryForRowSet(GETAPBILLDETAIL, rs.getInt("ad_sourcedetailid"));
					if (rs1.next()) {
						int abd = rs1.getInt("abd_id");
						updateByCondition("APBillDetail", "abd_ycheck=nvl(abd_ycheck,0)-(" + yqty + ")", "abd_id=" + abd);
					}
				} else if ("PRODINOUT".equals(rs.getString("ad_sourcetype"))) {
					SqlRowList rs2 = queryForRowSet("select pd_piclass from prodiodetail where pd_id=?", rs.getGeneralLong("ad_pdid"));
					if (rs2.next()) {
						if ("不良品入库单".equals(rs2.getGeneralString("pd_piclass")) || "不良品出库单".equals(rs2.getGeneralString("pd_piclass"))) {
							updateByCondition("ProdIODetail", "pd_ycheck=nvl(pd_ycheck,0)-(" + yqty + ")",
									"pd_id=" + rs.getGeneralLong("ad_pdid"));
						}
					}
					SqlRowList rs1 = queryForRowSet(GETAPBILLDETAIL2, rs.getInt("ad_sourcedetailid"));
					if (rs1.next()) {
						int abd = rs1.getInt("abd_id");
						updateByCondition("APBillDetail", "abd_ycheck=nvl(abd_ycheck,0)-(" + yqty + ")", "abd_id=" + abd);
					}
				}
			} else {
				if ("APBILL".equals(rs.getString("ad_sourcetype"))) {
					SqlRowList rs1 = queryForRowSet(GETAPBILLDETAIL, rs.getInt("ad_sourcedetailid"));
					if (rs1.next()) {
						int abd = rs1.getInt("abd_id");
						updateByCondition("APBillDetail", "abd_ycheck=nvl(abd_ycheck,0)-(" + yqty + ")", "abd_id=" + abd);
					}
				}
				if ("PRODINOUT".equals(rs.getString("ad_sourcetype"))) {
					SqlRowList rs1 = queryForRowSet(GETProdioDetail, rs.getInt("ad_sourcedetailid"));
					if (rs1.next()) {
						int abd = rs1.getInt("pd_id");
						updateByCondition("ProdIODetail", "pd_ycheck=nvl(pd_ycheck,0)-(" + yqty + ")", "pd_id=" + abd);
					}
				}
			}
		}
	}

	@Override
	@Transactional
	public void deleteAPCheck(int id) {
		SqlRowList rs = queryForRowSet("select ad_id from APCheckDetail where ad_acid=?", id);
		while (rs.next()) {
			restoreAPBill(rs.getGeneralInt("ad_id"));
			deleteByCondition("APCheckDetail", "ad_id=" + rs.getGeneralInt("ad_id"));
		}
	}

	@Override
	public void restoreAPBillWithQty(int adid, double uqty) {
		Object abdid = null;
		Object qty = 0;
		Object aq = 0;
		// 判断数量是否超出
		Object[] abd = getFieldsDataByCondition("APCheckDetail", new String[] { "ad_sourcedetailid", "ad_sourcetype" }, "ad_id=" + adid);
		abdid = abd[0];
		if (abd != null && Integer.parseInt(abdid.toString()) > 0) {
			if ("APBILL".equals(abd[1])) {
				qty = getFieldDataByCondition("APCheckDetail", "sum(ad_qty)", "ad_sourcedetailid=" + abdid
						+ "and nvl(ad_sourcetype,' ')='APBILL' AND ad_id <>" + adid);
				qty = qty == null ? 0 : qty;
				aq = getFieldDataByCondition("APBillDetail", "abd_qty", "abd_id=" + abdid);
				if (Math.abs(Double.parseDouble(aq.toString())) <= Math.abs(Double.parseDouble(qty.toString()) + uqty)) {
					BaseUtil.showError("新数量超出原发票数量,超出数量:"
							+ (Double.parseDouble(qty.toString()) + (uqty) - Double.parseDouble(aq.toString())));
				} else {
					updateByCondition("APBillDetail", "abd_ycheck=" + (Double.parseDouble(qty.toString()) + (uqty)), "abd_id=" + abdid);
				}
			} else if ("PRODINOUT".equals(abd[1])) {
				qty = getFieldDataByCondition("APCheckDetail", "sum(ad_qty)", "ad_sourcedetailid=" + abdid
						+ "and nvl(ad_sourcetype,' ')='PRODINOUT' AND ad_id <>" + adid);
				qty = qty == null ? 0 : qty;
				aq = getFieldDataByCondition("ProdIODetail", "pd_inqty-pd_outqty", "pd_id=" + abdid);
				if (Math.abs(Double.parseDouble(aq.toString())) <= Math.abs(Double.parseDouble(qty.toString()) + uqty)) {
					BaseUtil.showError("新数量超出原来源单数量,超出数量:"
							+ (Double.parseDouble(qty.toString()) + (uqty) - Double.parseDouble(aq.toString())));
				} else {
					updateByCondition("ProdIODetail", "pd_ycheck=" + (Double.parseDouble(qty.toString()) + (uqty)), "pd_id=" + abdid);
				}
			}
		}
	}

	/**
	 * 修改对账单开票状态
	 */
	@Override
	public void updateBillStatus(Integer ac_id) {
		if (isDBSetting("needCheck")) {
			execute("update apcheck set ac_billstatuscode='UNINVOICE',ac_billstatus='" + BaseUtil.getLocalMessage("UNINVOICE")
					+ "' where ac_id=" + ac_id
					+ " and not exists(select 1 from apcheckdetail where ad_acid=ac_id and abs(NVL(ad_yqty,0))>0 )");
			execute("update apcheck set ac_billstatuscode='PARTAR',ac_billstatus='"
					+ BaseUtil.getLocalMessage("PARTAR")
					+ "' where ac_id="
					+ ac_id
					+ " and nvl(ac_billstatuscode,' ')<>'PARTAR' and exists (select 1 from apcheckdetail where ad_acid=ac_id and abs(NVL(ad_yqty,0))>0 )");
			execute("update apcheck set ac_billstatuscode='TURNAR',ac_billstatus='"
					+ BaseUtil.getLocalMessage("TURNAR")
					+ "' where ac_id="
					+ ac_id
					+ " and nvl(ac_billstatuscode,' ')<>'TURNAR' and not exists(select 1 from apcheckdetail where ad_acid=ac_id and abs(nvl(ad_qty,0))-abs(NVL(ad_yqty,0))>0 )");
		} else {
			execute("update apcheck set ac_billstatuscode='UNINVOICE',ac_billstatus='"
					+ BaseUtil.getLocalMessage("UNINVOICE")
					+ "' where ac_id="
					+ ac_id
					+ " and not exists(select 1 from apcheckdetail where ad_acid=ac_id and abs(NVL(ad_yqty,0))>0 and nvl(ad_abclass,' ')<>'其它应付单')");
			execute("update apcheck set ac_billstatuscode='PARTAR',ac_billstatus='"
					+ BaseUtil.getLocalMessage("PARTAR")
					+ "' where ac_id="
					+ ac_id
					+ " and nvl(ac_billstatuscode,' ')<>'PARTAR' and exists (select 1 from apcheckdetail where ad_acid=ac_id and abs(NVL(ad_yqty,0))>0 and nvl(ad_abclass,' ')<>'其它应付单')");
			execute("update apcheck set ac_billstatuscode='TURNAR',ac_billstatus='"
					+ BaseUtil.getLocalMessage("TURNAR")
					+ "' where ac_id="
					+ ac_id
					+ " and nvl(ac_billstatuscode,' ')<>'TURNAR' and not exists(select 1 from apcheckdetail where ad_acid=ac_id and abs(nvl(ad_qty,0))-abs(NVL(ad_yqty,0))>0 and nvl(ad_abclass,' ')<>'其它应付单')");
		}
	}
}
