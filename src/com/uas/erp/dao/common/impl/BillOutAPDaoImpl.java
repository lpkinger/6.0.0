package com.uas.erp.dao.common.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.APCheckDao;
import com.uas.erp.dao.common.BillOutAPDao;

@Repository
public class BillOutAPDaoImpl extends BaseDao implements BillOutAPDao {

	@Autowired
	private APCheckDao apCheckDao;

	final static String GETAPBILLDETAIL = "SELECT abd_qty,abd_id,abd_yqty FROM APBillDetail where abd_code=? and abd_detno=?";
	final static String GETBILLOUTAPDETAIL = "SELECT ard_ordercode,ard_orderdetno,ard_nowqty,ard_adid FROM BillOutAPDetail WHERE ard_id=?";

	/**
	 * @param id
	 *            and_id
	 */
	public void restoreAPBill(int id) {
		SqlRowList rs = queryForRowSet(GETBILLOUTAPDETAIL, id);
		if (rs.next()) {
			double yqty = rs.getDouble("ard_nowqty");
			int adid = rs.getGeneralInt("ard_adid");
			if (adid != 0) {
				updateByCondition("APCheckDetail", "ad_yqty=nvl(ad_yqty,0)-(" + yqty + ")", "ad_id=" + adid);
				int ac_id = getFieldValue("APCheckDetail", "ad_acid", "ad_id=" + adid, Integer.class);
				apCheckDao.updateBillStatus(ac_id);
			} else {
				SqlRowList rs1 = queryForRowSet(GETAPBILLDETAIL, rs.getString("ard_ordercode"), rs.getInt("ard_orderdetno"));
				if (rs1.next()) {
					int abd = rs1.getInt(2);
					// 修改原采购明细已转数量
					updateByCondition("APBillDetail", "abd_yqty=nvl(abd_yqty,0)-(" + yqty + ")", "abd_id=" + abd);
				}
			}
		}
	}

	@Override
	@Transactional
	/**
	 * @author madan 2013-4-11 20:57:33
	 */
	public void deleteBillOutAP(int id) {
		SqlRowList rs = queryForRowSet("SELECT ard_id FROM BillOutAPDetail WHERE ard_biid=?", id);
		while (rs.next()) {
			restoreAPBill(rs.getInt("ard_id"));
		}
	}

	/**
	 * @author madan 2014-3-14 12:51:16
	 */
	@Override
	public void restoreAPBillWithQty(int ardid, double uqty) {
		Object abdid = null;
		Object qty = 0;
		Object aq = 0;
		Object[] objs = getFieldsDataByCondition("BillOutAPDetail", new String[] { "ard_adid", "ard_nowqty" }, "ard_id=" + ardid
				+ " and nvl(ard_adid,0)<>0");
		if (objs != null && objs[0] != null) {
			updateByCondition("APCheckDetail", "ad_yqty=nvl(ad_yqty,0)-(" + objs[1] + ")", "ad_id=" + objs[0]);
		} else {
			abdid = getFieldDataByCondition("BillOutAPDetail", "ard_orderid", "ard_id=" + ardid);
			if (abdid != null && Integer.parseInt(abdid.toString()) > 0) {
				qty = getFieldDataByCondition("BillOutAPDetail", "sum(ard_nowqty)", "ard_orderid=" + abdid + " AND ard_id <>" + ardid);
				qty = qty == null ? 0 : qty;
				aq = getFieldDataByCondition("APBillDetail", "abd_qty", "abd_id=" + abdid);
				if (Math.abs(Double.parseDouble(aq.toString())) <= Math.abs(Double.parseDouble(qty.toString()) + (uqty))) {
					BaseUtil.showError("新数量超出原发票数量,超出数量:"
							+ (Double.parseDouble(qty.toString()) + (uqty) - Double.parseDouble(aq.toString())));
				} else {
					updateByCondition("APBillDetail", "abd_yqty=" + (Double.parseDouble(qty.toString()) + (uqty)), "abd_id=" + abdid);
				}
			}
		}
	}
}
