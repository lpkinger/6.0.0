package com.uas.erp.dao.common.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlMap;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.AcceptNotifyDao;

@Repository
public class AcceptNotifyDaoImpl extends BaseDao implements AcceptNotifyDao {
	final static String GETPURCDETAIL = "SELECT pd_qty,pd_id,pd_yqty FROM PurchaseDetail where pd_code=? and pd_detno=?";
	final static String GETACCEPTNOTIFYDETAIL = "SELECT and_ordercode,and_orderdetno,and_inqty FROM AcceptNotifyDetail WHERE and_id=?";

	static final String ACCEPTYNOTIFY = "SELECT * FROM AcceptNotify WHERE an_id=?";
	static final String ACCEPTYNOTIFYDETAIL = "SELECT * FROM AcceptNotifyDetail WHERE and_anid=? and and_inqty-NVL(and_yqty,0)>0 order by and_detno";
	static final String PRODCHARGEDETAILAN = "SELECT * FROM ProdChargeDetailAN WHERE pd_anid=?";
	static final String VERIFYAPPLY = "INSERT INTO verifyapply(va_id, va_code, va_statuscode, va_status, va_recorder,"
			+ "va_indate,va_date,va_vendcode,va_paymentscode,va_payments,va_class,va_ancode,va_anid,va_whcode,va_sendcode)"
			+ " values (?,?,'ENTERING',?,?,sysdate,sysdate,?,?,?,'采购收料单',?,?,?,?)";
	static final String PRODINOUT = "INSERT INTO PRODINOUT(pi_id, pi_inoutno, pi_invostatuscode,pi_statuscode,pi_printstatuscode, pi_invostatus,pi_status,"
			+ "pi_printstatus, pi_recordman,pi_recorddate,pi_date,pi_cardcode,pi_title,pi_receivecode,pi_receivename,pi_paymentcode,pi_payment,pi_class,"
			+ "pi_sourcecode,pi_whcode,pi_whname,pi_cgycode,pi_cgy,pi_currency,pi_rate,pi_transport,pi_departmentname,pi_departmentcode,pi_emcode,pi_emname,"
			+ "pi_cop,pi_cardid,pi_invocode,pi_remark,pi_merchandiser) values (?,?,'ENTERING','UNPOST','UNPRINT',?,?,?,?,sysdate,sysdate,?,?,?,?,?,?,'采购验收单',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	/**
	 * 收料通知单删除时 还原采购单数据
	 * 
	 * @param id
	 *            and_id
	 */
	public void restorePurc(int id) {
		SqlRowList rs = queryForRowSet(GETACCEPTNOTIFYDETAIL, id);
		if (rs.next()) {
			double yqty = rs.getGeneralDouble("and_inqty");
			SqlRowList rs1 = queryForRowSet(GETPURCDETAIL, rs.getString("and_ordercode"), rs.getInt("and_orderdetno"));
			if (rs1.next()) {
				int pd = rs1.getInt(2);
				// 修改原采购明细已转数量
				updateByCondition("PurchaseDetail", "pd_yqty=nvl(pd_yqty,0)-" + yqty, "pd_id=" + pd);
				updateByCondition("PurchaseDetail", "pd_status='PART2SN'", "nvl(pd_qty,0)>nvl(pd_yqty,0) and nvl(pd_yqty,0) >0 and pd_id="
						+ pd);
				updateByCondition("PurchaseDetail", "pd_status='TURNSN'", "nvl(pd_qty,0)=nvl(pd_yqty,0) and nvl(pd_yqty,0) >0 and pd_id="
						+ pd);
				updateByCondition("PurchaseDetail", "pd_status='AUDITED'", " nvl(pd_yqty,0)=0 and pd_id=" + pd);
				int total = getCountByCondition("PurchaseDetail", "pd_code='" + rs.getString(1) + "'");
				int aud = getCountByCondition("PurchaseDetail", "pd_code='" + rs.getString(1) + "' AND NVL(pd_yqty,0)=0");
				int turn = getCountByCondition("PurchaseDetail", "pd_code='" + rs.getString(1)
						+ "' AND NVL(pd_yqty,0)=pd_qty AND NVL(pd_yqty,0)>0");
				String status = "PART2SN";
				if (aud == total) {
					status = "";
				} else if (turn == total) {
					status = "TURNSN";
				}
				updateByCondition("Purchase",
						"pu_turnstatuscode='" + status + "',pu_turnstatus='" + BaseUtil.getLocalMessage(status) + "'",
						"pu_code='" + rs.getString(1) + "'");
			}
		}
	}

	@Override
	@Transactional
	/**
	 * 收料通知单明细删除后，还原采购单明细数据
	 * @author madan 2013-4-11 20:57:33
	 */
	public void deleteAcceptNotify(int id) {
		List<Object> ids = getFieldDatasByCondition("AcceptNotifyDetail", "and_id", "and_anid=" + id);
		for (Object i : ids) {
			restorePurc(Integer.parseInt(i.toString()));
			// 删除收料明细
			deleteByCondition("AcceptNotifyDetail", "and_id=" + i);
		}
	}

	/**
	 * 收料通知单修改时，修改采购单状态、数量等
	 */
	@Override
	public void restorePurcWithQty(int andid, double uqty) {
		Object pdid = null;
		Object qty = 0;
		Object aq = 0;
		uqty = Math.abs(uqty);
		// 判断数量是否超出采购数量
		pdid = getFieldDataByCondition("AcceptNotifyDetail", "and_orderid", "and_id=" + andid);
		if (pdid != null && Integer.parseInt(pdid.toString()) > 0) {
			qty = getFieldDataByCondition("AcceptNotifyDetail", "sum(and_inqty)", "and_orderid=" + pdid + " AND and_id <>" + andid);
			qty = qty == null ? 0 : qty;
			aq = getFieldDataByCondition("PurchaseDetail", "pd_qty", "pd_id=" + pdid);
			if (Double.parseDouble(aq.toString()) < Double.parseDouble(qty.toString()) + uqty) {
				BaseUtil.showError("新数量超出原采购数量,超出数量:" + (Double.parseDouble(qty.toString()) + uqty - Double.parseDouble(aq.toString())));
			} else {
				updateByCondition("PurchaseDetail", "pd_yqty=" + (Double.parseDouble(qty.toString()) + uqty), "pd_id=" + pdid);
				updateByCondition("PurchaseDetail", "pd_status='PART2SN'", "nvl(pd_qty,0)>nvl(pd_yqty,0) and nvl(pd_yqty,0) >0 and pd_id="
						+ pdid);
				updateByCondition("PurchaseDetail", "pd_status='TURNSN'", "nvl(pd_qty,0)=nvl(pd_yqty,0) and nvl(pd_yqty,0) >0 and pd_id="
						+ pdid);
				updateByCondition("PurchaseDetail", "pd_status='AUDITED'", " nvl(pd_yqty,0)=0 and pd_id=" + pdid);
				Integer puid = getJdbcTemplate().queryForObject("select nvl(pd_puid,0) from PurchaseDetail where pd_id=?", Integer.class,
						pdid);
				if (puid != null && puid > 0) {
					int count = getCountByCondition("PurchaseDetail", "pd_puid=" + puid);
					int yCount = getCountByCondition("PurchaseDetail", "pd_puid=" + puid + " and NVL(pd_yqty,0)=pd_qty");
					int xCount = getCountByCondition("PurchaseDetail", "pd_puid=" + puid + " and NVL(pd_yqty,0)=0");
					String status = "PART2SN";
					if (yCount == count) {
						status = "TURNSN";
					}
					if (xCount == count) {
						status = "";
					}
					updateByCondition("Purchase", "pu_turnstatuscode='" + status + "',pu_turnstatus='" + BaseUtil.getLocalMessage(status)
							+ "'", "pu_id=" + puid);
				}
			}
		}
	}

	/**
	 * 判断是否超采购数收料
	 * 
	 * @author yingp 2013-4-17 9:08:59
	 */
	public void checkQty(int id) {
		SqlRowList rs = queryForRowSet(
				"SELECT and_ordercode,and_orderdetno,sum(and_inqty) and_inqty FROM AcceptNotifyDetail where and_anid=? group by and_ordercode,and_orderdetno",
				id);
		String code = null;
		int detno = 0;
		Object count = null;
		boolean bool = false;
		StringBuffer sb = new StringBuffer();
		while (rs.next()) {
			code = rs.getString("and_ordercode");
			detno = rs.getInt("and_orderdetno");
			count = getFieldDataByCondition("AcceptNotifyDetail left join AcceptNotify on an_id=and_anid", "sum(and_inqty)",
					"and_ordercode='" + code + "' and and_orderdetno=" + detno + " and an_statuscode in('AUDITED','TURNIN') and and_anid<>"
							+ id);
			if (count != null) {
				bool = checkByCondition("PurchaseDetail", "pd_code='" + code + "' and pd_detno=" + detno
						+ " and nvl(pd_qty,0)+nvl(pd_backqty,0)<" + (Double.parseDouble(count.toString()) + rs.getDouble("and_inqty")));
				if (!bool) {
					sb.append("<br>");
					sb.append("不能超采购数量收料,采购单号[");
					sb.append(code);
					sb.append("],明细行号[");
					sb.append(detno);
					sb.append("]");
				}
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}
	}

	@Override
	@Transactional
	public int turnVerifyApply(int id) {
		try {
			SqlRowList rs = queryForRowSet(ACCEPTYNOTIFY, id);
			int vaid = 0;
			SqlMap map = null;
			if (rs.next()) {
				vaid = getSeqId("VERIFYAPPLY_SEQ");
				String code = sGetMaxNumber("VerifyApply", 2);
				String sourcecode = rs.getString("an_code");
				boolean bool = execute(
						VERIFYAPPLY,
						new Object[] { vaid, code, BaseUtil.getLocalMessage("ENTERING"), SystemSession.getUser().getEm_name(),
								rs.getObject("an_vendcode"), rs.getObject("an_paymentcode"), rs.getString("an_payment"), sourcecode, id,
								rs.getObject("an_whcode"), rs.getObject("an_sendcode") });
				if (bool) {
					updateByCondition(
							"verifyapply",
							"(va_vendname,va_receivecode,va_receivename)=(select ve_name,ve_apvendcode,ve_apvendname from Vendor where ve_code=va_vendcode)",
							"va_id=" + vaid + " and nvl(va_vendcode,' ')<>' '");
					rs = queryForRowSet(ACCEPTYNOTIFYDETAIL, id);
					int count = 1;
					while (rs.next()) {
						double qty = rs.getDouble("and_inqty") - rs.getDouble("and_yqty");// 实际应转入值
						if (qty > 0) {
							map = new SqlMap("VerifyApplyDetail");
							map.set("vad_id", getSeqId("VERIFYAPPLYDETAIL_SEQ"));
							map.set("vad_vaid", vaid);
							map.set("vad_code", code);
							map.set("vad_detno", count++);
							map.set("vad_pucode", rs.getObject("and_ordercode"));
							map.set("vad_pudetno", rs.getObject("and_orderdetno"));
							map.set("vad_prodcode", rs.getString("and_prodcode"));
							map.set("vad_qty", qty);
							map.set("vad_class", "采购收料单");
							map.set("vad_sourcecode", sourcecode);
							map.set("vad_andid", rs.getInt("and_id"));
							map.set("vad_unitpackage", qty);
							map.execute();
							execute("update ACCEPTNOTIFYDETAIL set and_yqty=nvl(and_yqty,0)+" + qty + " where and_id="
									+ rs.getInt("and_id"));
							execute("update PURCHASEDETAIL set pd_yqty=nvl(pd_yqty,0)+" + qty + " where pd_code='"
									+ rs.getObject("and_ordercode") + "' and pd_detno =" + rs.getObject("and_orderdetno"));
						}
					}
				}
			}
			return vaid;
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
			return 0;
		}
	}

	@Override
	@Transactional
	public int turnProdio(int id) {
		try {
			SqlRowList rs = queryForRowSet(ACCEPTYNOTIFY, id);
			int piid = 0;
			SqlMap map = null;
			while (rs.next()) {
				piid = getSeqId("PRODINOUT_SEQ");
				String code = sGetMaxNumber("ProdInOut!PurcCheckin", 2);
				String sourcecode = rs.getString("an_code");
				Object[] vend = getFieldsDataByCondition("Vendor", new String[] { "ve_name", "ve_id" },
						"ve_code='" + rs.getObject("an_vendcode") + "'");
				boolean bool = execute(
						PRODINOUT,
						new Object[] { piid, code, BaseUtil.getLocalMessage("ENTERING"), BaseUtil.getLocalMessage("UNPOST"),
								BaseUtil.getLocalMessage("UNPRINT"), SystemSession.getUser().getEm_name(), rs.getObject("an_vendcode"),
								vend[0], rs.getObject("an_receivecode"), rs.getObject("an_receivename"), rs.getObject("an_paymentcode"),
								rs.getString("an_payment"), sourcecode, rs.getObject("an_whcode"), rs.getObject("an_whname"),
								rs.getObject("an_cgycode"), rs.getObject("an_cgy"), rs.getObject("an_currency"), rs.getObject("an_rate"),
								rs.getObject("an_transport"), rs.getObject("an_departmentname"), rs.getObject("an_departmentcode"),
								rs.getObject("an_emcode"), rs.getObject("an_emname"), rs.getObject("an_cop"), vend[1],
								rs.getObject("an_billcode"), rs.getObject("an_remark"), rs.getObject("an_cggdy") });
				if (bool) {
					rs = queryForRowSet(ACCEPTYNOTIFYDETAIL, id);
					int count = 1;
					while (rs.next()) {
						double qty = rs.getDouble("and_inqty") - rs.getDouble("and_yqty");// 实际应转入值
						Object purc = getFieldDataByCondition("PurchaseDetail", "pd_id", "pd_code='" + rs.getObject("and_ordercode")
								+ "' and pd_detno=" + rs.getObject("and_orderdetno"));
						Object prid = getFieldDataByCondition("Product", "pr_id", "pr_code='" + rs.getObject("and_prodcode") + "'");
						Object whid = getFieldDataByCondition("Warehouse", "wh_id", "wh_code='" + rs.getObject("and_whcode") + "'");
						Object baid = getFieldDataByCondition("Batch", "ba_id", "ba_code='" + rs.getObject("and_batchcode") + "'");
						if (qty > 0) {
							map = new SqlMap("ProdIODetail");
							map.set("pd_id", getSeqId("PRODIODETAIL_SEQ"));
							map.set("pd_piid", piid);
							map.set("pd_inoutno", code);
							map.set("pd_piclass", "采购验收单");
							map.set("pd_pdno", count++);
							map.set("pd_ordercode", rs.getObject("and_ordercode"));
							map.set("pd_orderdetno", rs.getObject("and_orderdetno"));
							map.set("pd_prodid", prid);
							map.set("pd_prodcode", rs.getString("and_prodcode"));
							map.set("pd_inqty", qty);
							map.set("pd_batchid", baid);
							map.set("pd_batchcode", rs.getString("and_batchcode"));
							map.set("pd_orderprice", rs.getObject("and_orderprice"));
							map.set("pd_price", rs.getObject("and_price"));
							map.set("pd_taxrate", rs.getObject("and_taxrate"));
							map.set("pd_sellercode", rs.getObject("and_sellercode"));
							map.set("pd_seller", rs.getObject("and_seller"));
							map.set("pd_whid", whid);
							map.set("pd_whcode", rs.getObject("and_whcode"));
							map.set("pd_whname", rs.getObject("and_whname"));
							map.set("pd_location", rs.getObject("and_location"));
							map.set("pd_total", rs.getObject("and_total"));
							map.set("pd_ordertotal", rs.getObject("and_ordertotal"));
							map.set("pd_customprice", rs.getObject("and_customprice"));
							map.set("pd_plancode", rs.getObject("and_plancode"));
							map.set("pd_barcode", rs.getObject("and_barcode"));
							map.set("pd_taxtotal", rs.getObject("and_taxtotal"));
							map.set("pd_description", rs.getObject("and_description"));
							map.set("pd_mmid", rs.getObject("and_mmid"));
							map.set("pd_beipininqty", rs.getObject("and_beipininqty"));
							map.set("pd_status", 0);
							map.set("pd_anid", rs.getInt("and_id"));
							map.set("pd_orderid", purc);
							map.execute();
							execute("update ACCEPTNOTIFYDETAIL set and_yqty=nvl(and_yqty,0)+" + qty + " where and_id="
									+ rs.getInt("and_id"));
						}
					}
					rs = queryForRowSet(PRODCHARGEDETAILAN, id);
					int detno = 1;
					while (rs.next()) {
						map = new SqlMap("ProdChargeDetail");
						map.set("pd_id", getSeqId("PRODCHARGEDETAIL_SEQ"));
						map.set("pd_piid", piid);
						map.set("pd_detno", detno++);
						map.set("pd_type", rs.getObject("pd_type"));
						map.set("pd_amount", rs.getObject("pd_amount"));
						map.set("pd_currency", rs.getString("pd_currency"));
						map.set("pd_rate", rs.getString("pd_rate"));
						map.execute();
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

	/**
	 * 更新采购单明细的已通知数
	 * 
	 * @author ZhongYL
	 */
	public void UpdatePurcTurnQTY(int pd_id, double addqty) {
		// 更新采购单已收料数
		updateByCondition("PurchaseDetail", "pd_turnqty=NVL(pd_turnqty,0)+(" + addqty + ")", "pd_id=" + pd_id);
	}

	/**
	 * 恢复采购单明细的已通知数量 收料单保存、更新、删除、删除明细行 等操作之前调用
	 * 
	 * @param vad_id
	 * @param vaclass
	 * @param cmdtype
	 *            操作类型：ADD/UPDATE/DELETE
	 * @author ZhongYL
	 */
	public void RestorePurcTurnQTY(int and_id, String cmdtype, double newqty, String newpucode, int newpudetno, String language) {
		int oldpdid = 0;
		SqlRowList rs;
		double oldandinqty = 0;
		if (!cmdtype.equals("DELETE") && !cmdtype.equals("ADD") && !cmdtype.equals("UPDATE")) {
			return;
		}
		if (!cmdtype.equals("ADD") && and_id <= 0) {
			return;
		}
		if (cmdtype.equals("ADD") && newqty > 0) {// 新增明明细行
			Object newpdid = getFieldDataByCondition("purchasedetail", "pd_id", "pd_code='" + newpucode + "' and pd_detno=" + newpudetno);
			if (newpdid != null && Integer.parseInt(newpdid.toString()) > 0) {
				UpdatePurcTurnQTY(Integer.parseInt(newpdid.toString()), newqty);// 更新采购已通知数量
			}
		} else if (and_id > 0) {// 修改或删除已有明细
			rs = queryForRowSet(
					"SELECT and_ordercode,and_orderdetno,and_inqty,pd_code,pd_detno,pd_id FROM acceptnotifydetail left join purchasedetail on and_ordercode=pd_code and and_orderdetno=pd_detno WHERE and_id=?",
					and_id);
			if (rs.next()) {
				oldpdid = rs.getInt("pd_id");
				oldandinqty = rs.getDouble("and_inqty");
				if (cmdtype.equals("DELETE") && oldpdid > 0) {// 删除记录
					// 更新采购已通知数量
					UpdatePurcTurnQTY(oldpdid, 0 - oldandinqty);
				} else if (cmdtype.equals("UDPATE")) {// 修改记录
					Object newpdid = getFieldDataByCondition("purchasedetail", "pd_id", "pd_code='" + newpucode + "' and pd_detno="
							+ newpudetno);
					// 判断是否变更了单号或序号
					if (oldpdid > 0) {// 原PDID有值
						if (newpdid == null) {
							UpdatePurcTurnQTY(oldpdid, 0 - oldandinqty);// 扣减原来单号已通知数量
						} else if (Integer.parseInt(newpdid.toString()) == oldpdid) {
							UpdatePurcTurnQTY(oldpdid, newqty - oldandinqty);// 只变更数量
						} else {// PDID已变
							UpdatePurcTurnQTY(oldpdid, 0 - oldandinqty);// 扣减原来单号已通知数量
							UpdatePurcTurnQTY(Integer.parseInt(newpdid.toString()), newqty);// 增加新单号已通知数量
						}
					} else if (newpdid != null && Integer.parseInt(newpdid.toString()) > 0) {// 新PDID有值
						UpdatePurcTurnQTY(Integer.parseInt(newpdid.toString()), newqty);// 更新采购已通知数量
					}
				}
			} else {
				return;// 记录已经不存在
			}
		}
	}

}
