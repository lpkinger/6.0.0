package com.uas.erp.dao.common.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.BorrowApplyDao;

@Repository
public class BorrowApplyDaoImpl extends BaseDao implements BorrowApplyDao{
	final static String GETBORROWAPPLYDETAIL = "SELECT bad_qty,bad_id,bad_yqty,bad_baid FROM BorrowApplyDetail where bad_id=?";
	final static String GETPRODIODETAIL = "SELECT pd_orderid,pd_outqty FROM ProdIODetail WHERE pd_id=?";
	static final String PRODCHARGEDETAILAN = "SELECT * FROM ProdChargeDetailAN WHERE pd_anid=?";
	
	/**
	 * 借货出货单删除时
	 * 还原借货申请单数据
	 * @param id pd_id
	 */
	public void restoreBorrowApply(int id) {
		SqlRowList rs = queryForRowSet(GETPRODIODETAIL, id);
		if(rs.next()){
			double yqty = rs.getDouble("pd_outqty");
			SqlRowList rs1 = queryForRowSet(GETBORROWAPPLYDETAIL, rs.getInt("pd_orderid"));
			if(rs1.next()){
				int pd = rs1.getInt(2);
				int baid= rs1.getInt("bad_baid");
				//修改借货申请明细已转数量
				updateByCondition("BorrowApplyDetail", "bad_yqty=nvl(bad_yqty,0)-" + yqty, "bad_id=" + pd);
				updateByCondition("BorrowApplyDetail", "bad_statuscode='PARTOUT',bad_status='部分出货'", "nvl(bad_qty,0)>nvl(bad_yqty,0) and nvl(bad_yqty,0) >0 and bad_id=" + pd);
				updateByCondition("BorrowApplyDetail", "bad_statuscode='TURNPRODIO',bad_status='已转出货单'", "nvl(bad_qty,0)=nvl(bad_yqty,0) and nvl(bad_yqty,0) >0 and bad_id=" + pd);
				updateByCondition("BorrowApplyDetail", "bad_statuscode=null,bad_status=null", " nvl(bad_yqty,0)=0 and bad_id=" + pd);
				int total = getCountByCondition("BorrowApplyDetail", "bad_baid=" + baid);
				int aud = getCountByCondition("BorrowApplyDetail", "bad_baid=" + baid + " AND nvl(bad_yqty,0)=0");
				int turn = getCountByCondition("BorrowApplyDetail", "bad_baid=" + baid + " AND NVL(bad_yqty,0)=bad_qty AND NVL(bad_yqty,0)>0");
				String status = "PARTOUT";
				if (aud == total) {
					status = "";
				} else if (turn == total) {
					status = "TURNPRODIO";
				}
				updateByCondition("BorrowApply", "ba_turnstatuscode='" + status + "',ba_turnstatus='"
						+ BaseUtil.getLocalMessage(status) + "'", "ba_id=" + baid);
			}
		}
	}
	@Override
	@Transactional
	/**
	 * 借货出货单明细删除后，还原借货申请单明细数据
	 * @author madan 2014-8-21 10:11:23
	 */
	public void deleteBorrowApply(int id) {
		List<Object> ids = getFieldDatasByCondition("ProdIODetail", "pd_id", "pd_piid=" + id);
		for(Object i:ids){
			restoreBorrowApply(Integer.parseInt(i.toString()));
			//删除收料明细
			deleteByCondition("ProdIODetail", "pd_id=" + i);
		}
	}
	/**
	 * 借货出货单修改时，修改借货申请状态、数量等
	 */
	@Override
	public void restoreBorrowApplyWithQty(int pdid, double uqty) {
		Object badid = null;
		Object qty = 0;
		Object aq = 0;
		uqty = Math.abs(uqty);
		//判断数量是否超出采购数量
		badid = getFieldDataByCondition("ProdIODetail", "pd_orderid", "pd_id=" + pdid);
		if(badid != null && Integer.parseInt(badid.toString()) > 0) {
			qty = getFieldDataByCondition("ProdIODetail", "sum(pd_outqty)", "pd_orderid=" + badid + 
						" AND pd_id <>" + pdid + " and pd_piclass='借货出货单'");
			qty = qty == null ? 0 : qty;
			aq = getFieldDataByCondition("BorrowApplyDetail", "bad_qty", "bad_id=" + badid);
			if(Double.parseDouble(aq.toString()) < Double.parseDouble(qty.toString()) + uqty) {
				BaseUtil.showError("新数量超出原申请数量,超出数量:" +
						(Double.parseDouble(qty.toString()) + uqty - Double.parseDouble(aq.toString())));
			} else {
				updateByCondition("BorrowApplyDetail", "bad_yqty=" + 
						(Double.parseDouble(qty.toString()) + uqty), "bad_id=" + badid);
				updateByCondition("BorrowApplyDetail", "bad_statuscode='PARTOUT', bad_statuscode='部分出货'", "nvl(bad_qty,0)>nvl(bad_yqty,0) and nvl(bad_yqty,0) >0 and bad_id=" + badid);
				updateByCondition("BorrowApplyDetail", "bad_statuscode='TURNPRODIO', bad_statuscode='已转出货单'", "nvl(bad_qty,0)=nvl(bad_yqty,0) and nvl(bad_yqty,0) >0 and bad_id=" + badid);
				updateByCondition("BorrowApplyDetail", "bad_statuscode=null, bad_statuscode=null", " nvl(bad_yqty,0)=0 and bad_id=" + badid);
				Integer baid = getJdbcTemplate().queryForObject(
						"select nvl(bad_baid,0) from BorrowApplyDetail where bad_id=?", Integer.class, badid);
				if (baid != null && baid > 0) {
					int count = getCountByCondition("BorrowApplyDetail", "bad_baid=" + baid);
					int yCount = getCountByCondition("BorrowApplyDetail", "bad_baid=" + baid
							+ " and bad_yqty=bad_qty");
					int xCount = getCountByCondition("BorrowApplyDetail", "bad_baid=" + baid
							+ " and bad_yqty=0");
					String status = "PARTOUT";
					if (yCount == count) {
						status = "TURNPRODIO";
					}
					if (xCount == count) {
						status = "";
					}
					updateByCondition("BorrowApply", "ba_turnstatuscode='" + status
							+ "',ba_turnstatus='" + BaseUtil.getLocalMessage(status) + "'",
							"ba_id=" + baid);
				}
			}
		}
	}
	static final String CHECK_YQTY = "SELECT ba_code,bad_detno,bad_qty FROM BorrowApplyDetail LEFT JOIN BorrowApply on ba_id=bad_baid WHERE bad_id=? and bad_qty<?";
	@Override
	public void checkAdYqty(List<Map<Object, Object>> datas) {
		int id = 0;
		Object y = 0;
		SqlRowList rs = null;
		Object[] sns = null;
		for (Map<Object, Object> d : datas) {
			id = Integer.parseInt(d.get("bad_id").toString());
			sns = getFieldsDataByCondition("BorrowApplyDetail left join BorrowApply on bad_baid=ba_id",
					"ba_code,bad_detno", "bad_id=" + id);
			if (sns != null) {
				y = getFieldDataByCondition("ProdIODetail", "sum(nvl(pd_outqty,0))", "pd_orderid=" + id
						+ " and pd_piclass='借货出货单'");
				y = y == null ? 0 : y;
				rs = queryForRowSet(
						CHECK_YQTY,
						id,
						Double.parseDouble(y.toString()) + Double.parseDouble(d.get("bad_tqty").toString()));
				if (rs.next()) {
					StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],申请单号:").append(rs.getString("ba_code"))
							.append(",行号:").append(rs.getInt("bad_detno")).append(",申请数量:")
							.append(rs.getDouble("bad_qty")).append(",已转数:").append(y)
							.append(",本次数:").append(d.get("bad_tqty"));
					BaseUtil.showError(sb.toString());
				}
			}
		}
	}
	
	@Override
	public void checkBADQty(int badid, Object bad_baid) {
		Object baid;
		if (bad_baid != null) {
			baid = bad_baid;
		} else {
			baid = getFieldDataByCondition("BorrowApplyDetail", "bad_baid", "bad_id=" + badid);
		}
		String status = null;
		int count = getCountByCondition("BorrowApplyDetail", "bad_baid=" + baid);
		int yCount = getCountByCondition("BorrowApplyDetail", "bad_baid=" + baid
				+ " and bad_yqty=bad_qty and nvl(bad_yqty,0)>0");
		int xCount = getCountByCondition("BorrowApplyDetail", "bad_baid=" + baid + " and nvl(bad_yqty,0)=0");
		status = "PARTOUT";
		if (yCount == count) {
			status = "TURNPRODIO";
		}
		if (xCount == count) {
			status = "";
		}
		updateByCondition("BorrowApply",
				"ba_turnstatuscode='" + status + "',ba_turnstatus='" + BaseUtil.getLocalMessage(status) + "'",
				"ba_id=" + baid);
	}
}