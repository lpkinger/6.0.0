package com.uas.erp.dao.common.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ARCheckDao;
import com.uas.erp.dao.common.BillOutDao;

@Repository
public class BillOutDaoImpl extends BaseDao implements BillOutDao {

	@Autowired
	private ARCheckDao arCheckDao;

	final static String GETARBILLDETAIL = "SELECT abd_qty,abd_id,abd_yqty FROM ARBillDetail where abd_code=? and abd_detno=?";
	final static String GETBILLOUTDETAIL = "SELECT ard_ordercode,ard_orderdetno,ard_nowqty,ard_adid FROM BillOutDetail WHERE ard_id=?";

	/**
	 * 应收开票记录删除明细行时 还原应收发票已转数
	 * 
	 * @param id
	 *            and_id
	 */
	@Override
	public void restoreARBill(int id) {
		SqlRowList rs = queryForRowSet(GETBILLOUTDETAIL, id);
		if (rs.next()) {
			double yqty = rs.getGeneralDouble("ard_nowqty");
			int adid = rs.getGeneralInt("ard_adid");
			if (adid != 0) {
				updateByCondition("ARCheckDetail", "ad_yqty=nvl(ad_yqty,0)-(" + yqty + ")", "ad_id=" + adid);
				int ac_id = getFieldValue("ARCheckDetail", "ad_acid", "ad_id=" + adid, Integer.class);
				arCheckDao.updateBillStatus(ac_id);
			} else {
				SqlRowList rs1 = queryForRowSet(GETARBILLDETAIL, rs.getString("ard_ordercode"), rs.getInt("ard_orderdetno"));
				if (rs1.next()) {
					int abd = rs1.getInt(2);
					updateByCondition("ARBillDetail", "abd_yqty=nvl(abd_yqty,0)-(" + yqty + ")", "abd_id=" + abd);
				}
			}
		}
	}

	@Override
	@Transactional
	/**
	 * 应收开票记录删除后，还原应收发票已转数
	 * @author madan 2013-4-11 20:57:33
	 */
	public void deleteBillOut(int id) {
		SqlRowList rs = queryForRowSet("SELECT ard_id FROM BillOutDetail WHERE ard_biid=?", id);
		while (rs.next()) {
			restoreARBill(rs.getInt("ard_id"));
		}
	}

	/**
	 * 应收开票记录修改时，修改应收发票状态、数量等
	 */
	@Override
	public void restoreARBillWithQty(int ardid, double uqty) {
		Object abdid = null;
		Object qty = 0;
		Object aq = 0;
		abdid = getFieldDataByCondition("BillOutDetail", "ard_orderid", "ard_id=" + ardid);
		if (abdid != null && Integer.parseInt(abdid.toString()) > 0) {
			qty = getFieldDataByCondition("BillOutDetail", "sum(ard_nowqty)", "ard_orderid=" + abdid + " AND ard_id <>" + ardid);
			qty = qty == null ? 0 : qty;
			aq = getFieldDataByCondition("ARBillDetail", "abd_qty", "abd_id=" + abdid);
			if (Math.abs(Double.parseDouble(aq.toString())) <= Math.abs(Double.parseDouble(qty.toString()) + uqty)) {
				BaseUtil.showError("新数量超出原发票数量,超出数量:" + (Double.parseDouble(qty.toString()) + (uqty) - Double.parseDouble(aq.toString())));
			} else {
				updateByCondition("ARBillDetail", "abd_yqty=" + (Double.parseDouble(qty.toString()) + (uqty)), "abd_id=" + abdid);
			}
		} else {
			Object[] objs = getFieldsDataByCondition("BillOutDetail", new String[] { "ard_adid", "ard_nowqty" }, "ard_id=" + ardid
					+ " and nvl(ard_adid,0)<>0");
			if (objs != null && objs[0] != null) {
				updateByCondition("ARCheckDetail", "ad_yqty=nvl(ad_yqty,0)-(" + objs[1] + ")", "ad_id=" + objs[0]);
			}
		}
	}
}
