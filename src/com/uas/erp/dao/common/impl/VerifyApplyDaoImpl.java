package com.uas.erp.dao.common.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.exception.SystemException;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlMap;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.dao.common.MakeCraftDao;
import com.uas.erp.dao.common.VerifyApplyDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Key;
import com.uas.erp.model.MessageLog;

@Repository
public class VerifyApplyDaoImpl extends BaseDao implements VerifyApplyDao {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private TransferRepository transferRepository;
	@Autowired
	private MakeCraftDao makeCraftDao;

	final static String VERIFYAPPLY = "SELECT va_whcode,va_vendcode,va_vendname,va_receivecode,va_receivename"
			+ ",va_date FROM verifyapply WHERE va_id=?";
	final static String VERIFYAPPLYDETAIL = "SELECT vad_pudetno,vad_pucode,vad_prodcode,vad_qty,vad_id,va_code"
			+ ",vad_whcode,pd_price,pd_rate,vad_batchcode FROM verifyapplydetail left join verifyapply on vad_vaid=va_id left join PurchaseDetail on (pd_code"
			+ "=vad_pucode and pd_detno=vad_pudetno) WHERE vad_vaid=?";
	final static String INSERT_PRODINOUT = "INSERT INTO prodinout(pi_id, Fin_Code,pi_inoutno,pi_whcode,pi_recordman,pi_operatorcode,pi_recorddate"
			+ ",pi_date,pi_cardcode,pi_title,pi_invostatus,pi_invostatuscode,pi_class,pi_receivename,pi_receivecode,pi_status,pi_statuscode,pi_updatedate,pi_updateman) "
			+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
	// pd_orderid暂用作记录收料单明细的id
	// pd_ordercode,pd_orderdetno关联的是采购单明细
	// pd_vacode收料单号
	// pd_auditstatus 状态
	// pd_status 过账状态
	// pd_unitpackage,pd_mantissapackage增加插入分装数量，尾数分装数（用户可用，逗号隔开）
	final static String INSERT_PRODIODETAIL = "INSERT INTO prodiodetail(pd_orderdetno,pd_ordercode,pd_prodcode,pd_prodmadedate,pd_inqty"
			+ ",pd_id,pd_inoutno,pd_piclass,pd_pdno,pd_status,pd_auditstatus,pd_piid,pd_orderid,pd_vacode,pd_whcode"
			+ ",pd_orderprice,pd_price,pd_taxrate,pd_taxtotal,pd_total,pd_prodid,pd_batchcode,"
			+ "pd_custcode,pd_custname,pd_salecode,pd_remark2,pd_remark3,pd_unitpackage,pd_mantissapackage) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static String INSERT_BASEPRODINOUT = "INSERT INTO prodinout(pi_id, Fin_Code, pi_inoutno,pi_recordman,pi_operatorcode,pi_recorddate"
			+ ",pi_invostatus,pi_invostatuscode,pi_class,pi_cardcode,pi_title,pi_status,pi_statuscode,pi_updatedate,pi_updateman,pi_currency,pi_rate) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static String VERIFYAPPLYDETAIL_BASE = "SELECT vad_pudetno,vad_pucode,vad_prodcode,vad_pudate,vad_qty"
			+ ",va_id,va_vendcode,va_vendname,va_code,vad_whcode,pd_price,pd_rate,pd_id,pd_puid,vad_detno,vad_batchcode,vad_custcode,vad_custname"
			+ ",vad_salecode,vad_remark2,vad_remark3,vad_unitpackage,vad_mantissapackage FROM verifyapplydetail left join "
			+ "verifyapply on vad_vaid=va_id left join PurchaseDetail on (pd_code=vad_pucode and pd_detno=vad_pudetno) WHERE vad_id=?";
	final static String GETVERIFYAPPLYDETAIL = "SELECT vad_pucode,vad_pudetno,vad_qty,vad_andid,va_class,va_intype,vad_mcid,vad_purcqty FROM VerifyApply,VerifyApplyDetail WHERE va_id=vad_vaid and vad_id=?";
	final static String GETPURCDETAIL = "SELECT pd_qty,pd_id,pd_yqty FROM PurchaseDetail where pd_code=? and pd_detno=?";
	final static String GETACCEPTNOTIFYDETAIL = "SELECT and_inqty,and_id,and_yqty FROM AcceptNotifyDetail where and_id=?";

	static final String TURNVERIFYAPPLYDETAIL = "SELECT vad_vaid,vad_code,vad_class,vad_detno,vad_prodcode,vad_qty,vad_remark,vad_pucode,vad_pudetno"
			+ ",va_vendcode,va_vendname,vad_madedate,va_sendcode,vad_salecode FROM VERIFYAPPLYDETAIL left join VerifyApply on vad_vaid=va_id WHERE vad_id=?";
	static final String TURNVERIFYAPPLYDETAIL_LIST = "SELECT vad_id,vad_vaid,vad_code,vad_class,vad_detno,vad_prodcode,vad_qty,vad_remark,vad_pucode,vad_pudetno,vad_description"
			+ ",va_vendcode,va_vendname,vad_madedate,va_sendcode,vad_batchcode,va_cop,va_code,vad_salecode,va_emcode,va_emname,va_intype,vad_jobcode,vad_mcid,vad_purcqty"
			+ " FROM VERIFYAPPLYDETAIL left join VerifyApply on vad_vaid=va_id WHERE vad_id in (@IDS)";
	static final String INSERQUA_VERIFYAPPLYDETAIL = "INSERT INTO QUA_VERIFYAPPLYDETAIL(ve_id,ve_code,ve_class,ve_method,ve_status,ve_printstatus,ve_checkdate"
			+ ",ve_indate,ve_recorder,ve_statuscode,ve_type,ve_date,vad_vaid,vad_code,vad_class,vad_detno,vad_prodcode,vad_qty,vad_remark,vad_sourcecode"
			+ ",vad_sourcedetno,ve_ordercode,ve_orderdetno,vad_vendcode,vad_vendname,ve_makedate,ve_sendcode)"
			+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String INSERQUA_VERIFYAPPLYDETAILDET = "insert into QUA_VerifyApplyDetailDet(ved_id,ved_veid,ved_detno,ved_okqty,"
			+ "ved_date,ved_testman,ved_checkdate,ved_checkqty,ved_status,ved_statuscode,ved_code) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
	final static String getCurrency = "select pu_currency,pu_rate from purchase where pu_code=?";

	final static String GETMAKE = "SELECT ma_qty,ma_id,ma_haveqty FROM Make where ma_code=?";

	final static String GETMAKECRAFT = "SELECT mc_qty,mc_id,mc_yqty FROM MakeCraft where mc_id=?";

	/**
	 * 收料单整张单入库
	 */
	@Override
	@Transactional
	public int turnStorage(int id) {
		Key key = transferRepository.transfer("VerifyApply!ToPurcIn", id);
		int piid = key.getId();
		// 转入明细
		transferRepository.transferDetail("VerifyApply!ToPurcIn", id, key);
		if (piid != 0) {
			execute("update prodiodetail set pd_prodid=(select pr_id from product where pd_prodcode=pr_code) where pd_piid=" + piid
					+ " and nvl(pd_prodcode,' ')<>' '");
			execute("update prodiodetail set pd_whid=(select wh_id from warehouse where wh_code=pd_whcode) where pd_piid=" + piid
					+ " and nvl(pd_whcode,' ')<>' '");
			baseDao.updateByCondition("ProdInOut",
					"pi_total=(SELECT round(sum(nvl(pd_orderprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0))),2) FROM ProdIODetail WHERE pd_piid="
							+ piid + ")", "pi_id=" + piid);
			baseDao.updateByCondition("ProdInOut", "pi_totalupper=L2U(nvl(pi_total,0))", "pi_id=" + piid);
			execute("Insert into ProdChargeDetail(pd_id,pd_piid,pd_detno,pd_type,pd_amount,pd_currency,pd_rate) "
					+ "select ProdChargeDetail_seq.nextval, " + piid + ", pd_detno, pd_type,pd_amount,pd_currency,pd_rate "
					+ "from ProdChargeDetailAN where PD_ANID=" + id);
			// 修改收料状态
			baseDao.updateByCondition("VerifyApply", "va_turnstatuscode='TURNIN',va_turnstatus='" + BaseUtil.getLocalMessage("TURNIN")
					+ "'", "va_id=" + id);
		}
		return piid;
	}

	/**
	 * 收料单明细批量入库
	 */
	@Transactional
	public JSONObject detailTurnStorage(String caller, List<Map<Object, Object>> maps) {
		int piid = getSeqId("PRODINOUT_SEQ");
		int count = 1;
		double purchaseRate = 0;
		String code = null, currency = null;
		Set<Integer> ids = new HashSet<Integer>();// 根据明细ID，找出对应的哪些收料单主表的ID
		Set<Integer> pus = new HashSet<Integer>();
		Employee employee = SystemSession.getUser();
		for (Map<Object, Object> map : maps) {
			SqlRowList rs = queryForRowSet(VERIFYAPPLYDETAIL_BASE, new Object[] { map.get("vad_id") });
			if (rs.next()) {
				if (code == null) {
					if ("VerifyApply".equals(caller)) {
						code = sGetMaxNumber("ProdInOut!PurcCheckin", 2);
					} else {
						code = sGetMaxNumber("ProdInOut!OutsideCheckIn", 2);
					}
					SqlRowList getcurrency = queryForRowSet(getCurrency, new Object[] { map.get("vad_pucode") });
					if (getcurrency.next()) {
						currency = getcurrency.getString("pu_currency");
						purchaseRate = getcurrency.getDouble("pu_rate");
					}
					execute(INSERT_BASEPRODINOUT,
							new Object[] { piid, code, code, employee.getEm_name(), employee.getEm_code(),
									Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), BaseUtil.getLocalMessage("ENTERING"),
									"ENTERING", "采购验收单", rs.getString("va_vendcode"), rs.getString("va_vendname"),
									BaseUtil.getLocalMessage("UNPOST"), "UNPOST",
									Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), employee.getEm_name(), currency,
									purchaseRate });
				}
				int pdid = getSeqId("PRODIODETAIL_SEQ");
				double tqty = Integer.parseInt(map.get("vad_tqty").toString());
				double price = Double.parseDouble(rs.getObject("pd_price").toString());
				double rate = Double.parseDouble(rs.getObject("pd_rate").toString());
				Object[] prid = getFieldsDataByCondition("Product", new String[] { "pr_id" }, "pr_code='" + rs.getString("vad_prodcode")
						+ "'");
				execute(INSERT_PRODIODETAIL,
						new Object[] { rs.getInt("vad_pudetno"), rs.getString("vad_pucode"), rs.getString("vad_prodcode"),
								rs.getObject("vad_pudate"), tqty, pdid, code, "采购验收单", count++, 0, "ENTERING", piid, map.get("vad_id"),
								rs.getObject("va_code"), rs.getObject("vad_whcode"), price, price / (1 + rate / 100), rate, price * tqty,
								price / (1 + rate / 100) * tqty, prid[0], rs.getObject("vad_batchcode"), rs.getObject("vad_custcode"),
								rs.getObject("vad_custname"), rs.getObject("vad_salecode"), rs.getObject("vad_remark2"),
								rs.getObject("vad_remark3"), rs.getDouble("vad_unitpackage"), rs.getObject("vad_mantissapackage") });
				updateByCondition("VerifyApplyDetail", "vad_yqty=vad_yqty+" + tqty, "vad_id=" + map.get("vad_id"));
				//
				if (!ids.contains(rs.getInt("va_id"))) {
					ids.add(rs.getInt("va_id"));
				}
				if (!pus.contains(rs.getInt("pd_puid"))) {
					pus.add(rs.getInt("pd_puid"));
				}
				// 记录日志
				logMessage(new MessageLog(
						employee.getEm_name(),
						BaseUtil.getLocalMessage("msg.turnStorage"),
						BaseUtil.getLocalMessage("msg.turnSuccess") + "," + BaseUtil.getLocalMessage("msg.detail") + rs.getInt("vad_detno"),
						"VerifyApply|vad_id=" + rs.getInt("va_id")));
			}
		}
		// 修改VerifyApply为部分入库\已入库
		Iterator<Integer> iterator = ids.iterator();
		while (iterator.hasNext()) {
			int id = iterator.next();
			int total = getCountByCondition("VerifyApplyDetail", "vad_vaid=" + id);
			int aud = getCountByCondition("VerifyApplyDetail", "vad_vaid=" + id + " AND vad_yqty=0");
			int turn = getCountByCondition("VerifyApplyDetail", "vad_vaid=" + id + "  AND vad_yqty=vad_qty");
			String status = "PART2IN";
			if (aud == total) {
				status = "";
			} else if (turn == total) {
				status = "TURNIN";
			}
			updateByCondition("VerifyApply", "va_turnstatuscode='" + status + "',va_turnstatus='" + BaseUtil.getLocalMessage(status) + "'",
					"va_id=" + id);
		}
		iterator = pus.iterator();
		while (iterator.hasNext()) {
			int id = iterator.next();
			int total = getCountByCondition("PurchaseDetail", "pd_puid=" + id);
			int aud = getCountByCondition("PurchaseDetail", "pd_puid=" + id + " AND nvl(pd_acceptqty,0)=0");
			int turn = getCountByCondition("PurchaseDetail", "pd_puid=" + id + " AND nvl(pd_acceptqty,0)=nvl(pd_qty,0)");
			String status = "PART2IN";
			if (aud == total) {
				status = "";
			} else if (turn == total) {
				status = "TURNIN";
			}
			updateByCondition("Purchase", "pu_turnstatuscode='" + status + "',pu_turnstatus='" + BaseUtil.getLocalMessage(status) + "'",
					"pu_id=" + id);
		}
		if (code == null)
			return null;
		JSONObject j = new JSONObject();
		j.put("pi_id", piid);
		j.put("pi_inoutno", code);
		return j;
	}

	/**
	 * 收料单删除时 还原采购单数据
	 * 
	 * @param id
	 *            vad_id
	 */
	public void restorePurc(int id) {
		SqlRowList rs = queryForRowSet(GETVERIFYAPPLYDETAIL, id);
		if (rs.next()) {
			Double yqty = rs.getDouble("vad_qty");
			Double vad_purcqty = rs.getDouble("vad_purcqty");
			String vaclass = rs.getGeneralString("va_class");
			Object intype = rs.getObject("va_intype");
			intype = intype == null ? "正常委外" : intype;
			SqlRowList detail = null;
			if ("采购收料单".equals(vaclass.toString())) {
				detail = queryForRowSet(GETPURCDETAIL, rs.getObject("vad_pucode"), rs.getObject("vad_pudetno"));
				if (detail.next()) {
					// 修改原采购明细已转数量
					updateByCondition("PurchaseDetail", "pd_yqty=nvl(pd_yqty,0)-" + yqty, "pd_id=" + detail.getObject("pd_id"));
					/**
					 * 双单位
					 * 采购收料单：删除单据时【采购单位收料数量】更新到采购单的【采购单位已转数】
					 */
					updateByCondition("PurchaseDetail", "pd_ypurcqty=nvl(pd_ypurcqty,0)-" + (vad_purcqty<0?0:vad_purcqty), "pd_id=" + detail.getObject("pd_id"));
					// 修改采购单状态
					updatePurcStatus2(rs.getString("vad_pucode"));
				}
			} else if ("委外收料单".equals(vaclass.toString())) {
				if ("正常委外".equals(intype)) {
					detail = queryForRowSet(GETMAKE, rs.getString(1));
					if (detail.next()) {
						// 修改制造单已转数量
						updateByCondition("Make", "ma_haveqty=nvl(ma_haveqty,0)-" + yqty, "ma_id=" + detail.getInt("ma_id"));
					}
				} else if ("工序委外".equals(intype)) {
					Object mc_id = rs.getGeneralInt("vad_mcid");
					detail = queryForRowSet(GETMAKECRAFT, mc_id);
					if (detail.next()) {
						// 修改工序委外单已转数量
						updateByCondition("MakeCraft", "mc_yqty=nvl(mc_yqty,0)-" + yqty, "mc_id=" + mc_id);
						updateByCondition("MakeCraft", "mc_yqty=0", "nvl(mc_yqty,0)<=0 and mc_id=" + mc_id);
						makeCraftDao.updateStatus(mc_id);
					}
				}
			}
		}
	}

	/**
	 * 收料单删除时 还原收料通知单数据
	 * 
	 * @param id
	 *            vad_id
	 */
	public void restoreAcc(int id) {
		SqlRowList rs = queryForRowSet(GETVERIFYAPPLYDETAIL, id);
		if (rs.next()) {
			int yqty = rs.getInt(3);
			int andid = rs.getInt(4);
			SqlRowList rs1 = queryForRowSet(GETACCEPTNOTIFYDETAIL, andid);
			if (rs1.next()) {
				// 修改原收料通知单明细已转数量
				updateByCondition("AcceptNotifyDetail", "and_yqty=nvl(and_yqty,0)-" + yqty, "and_id=" + andid);
				updateAccStatus(andid);
			}
		}
	}

	/**
	 * @param qty
	 *            本次变更的数量
	 */
	public void restorePurcWithQty(int id, double uqty,double yqty) {
		SqlRowList rs = queryForRowSet(GETVERIFYAPPLYDETAIL, id);
		if (rs.next()) {
			String vaclass = rs.getGeneralString("va_class");
			int andid = rs.getInt("vad_andid");
			Object intype = rs.getObject("va_intype");
			intype = intype == null ? "正常委外" : intype;
			SqlRowList detail = null;
			String str = uqty > 0 ? "-" : "+";
			String str2 = yqty > 0 ? "-" : "+";
			if ("采购收料单".equals(vaclass.toString())) {
				detail = queryForRowSet(GETPURCDETAIL, rs.getObject("vad_pucode"), rs.getObject("vad_pudetno"));
				if (detail.next()) {
					// 修改原采购明细已转数量
					updateByCondition("PurchaseDetail", "pd_yqty=nvl(pd_yqty,0)" + str + Math.abs(uqty),
							"pd_id=" + detail.getObject("pd_id"));
					/**
					 * 双单位
					 */
					updateByCondition("PurchaseDetail", "pd_ypurcqty=nvl(pd_ypurcqty,0)" + str2 + Math.abs(yqty),
							"pd_id=" + detail.getObject("pd_id"));
					// 修改采购单状态
					updatePurcStatus2(rs.getString("vad_pucode"));
				}
			} else if ("委外收料单".equals(vaclass.toString())) {
				if ("正常委外".equals(intype)) {
					detail = queryForRowSet(GETMAKE, rs.getString(1));
					if (detail.next()) {
						// 修改制造单已转数量
						updateByCondition("Make", "ma_haveqty=nvl(ma_haveqty,0)" + str + Math.abs(uqty), "ma_id=" + detail.getInt("ma_id"));
					}
				} else if ("工序委外".equals(intype)) {
					Object mc_id = rs.getGeneralInt("vad_mcid");
					detail = queryForRowSet(GETMAKECRAFT, mc_id);
					if (detail.next()) {
						// 修改工序委外单已转数量
						updateByCondition("MakeCraft", "mc_yqty=nvl(mc_yqty,0)" + str + Math.abs(uqty), "mc_id=" + mc_id);
						updateByCondition("MakeCraft", "mc_yqty=0", "nvl(mc_yqty,0)<=0 and mc_id=" + mc_id);
						makeCraftDao.updateStatus(mc_id);
					}
				}
			}
			if (andid > 0) {
				SqlRowList rs2 = queryForRowSet(GETACCEPTNOTIFYDETAIL, andid);
				if (rs2.next()) {
					// 修改原收料通知单明细已转数量及状态
					updateByCondition("AcceptNotifyDetail", "and_yqty=nvl(and_yqty,0)" + str + Math.abs(uqty), "and_id=" + andid);
				}
			}
		}
	}

	@Override
	@Transactional
	public void deleteVerifyApply(int id) {
		List<Object> ids = getFieldDatasByCondition("VerifyApplyDetail", "vad_id", "vad_vaid=" + id);
		for (Object i : ids) {
			Object andid = getFieldDataByCondition("VerifyApplyDetail", "vad_andid", "vad_id=" + i);
			if (andid != null && Integer.valueOf(andid.toString()) > 0) {
				restoreAcc(Integer.parseInt(i.toString()));
			}
			restorePurc(Integer.parseInt(i.toString()));
			// 删除收料明细
			deleteByCondition("VerifyApplyDetail", "vad_id=" + i);
		}
		// 删除主表
		deleteById("VerifyApply", "va_id", id);
	}

	public void updatePurcStatus(String pucode) {
		int total = getCountByCondition("PurchaseDetail", "pd_code='" + pucode + "'");
		int aud = getCountByCondition("PurchaseDetail", "pd_code='" + pucode + "' AND nvl(pd_acceptqty,0)=0");
		int turn = getCountByCondition("PurchaseDetail", "pd_code='" + pucode + "' AND nvl(pd_acceptqty,0)=nvl(pd_qty,0)");
		String status = "PART2IN";
		if (aud == total) {
			status = "";
		} else if (turn == total) {
			status = "TURNIN";
		}
		updateByCondition("Purchase", "pu_turnstatuscode='" + status + "',pu_turnstatus='" + BaseUtil.getLocalMessage(status) + "'",
				"pu_code='" + pucode + "'");
	}

	/*
	 * 收料单删除后修改主表收料状态
	 */
	public void updatePurcStatus2(String pucode) {
		int total = getCountByCondition("PurchaseDetail", "pd_code='" + pucode + "'");
		int aud = getCountByCondition("PurchaseDetail", "pd_code='" + pucode + "' AND nvl(pd_yqty,0)=0");
		int turn = getCountByCondition("PurchaseDetail", "pd_code='" + pucode + "' AND nvl(pd_yqty,0)=nvl(pd_qty,0)");
		String status = "PART2VA";
		if (aud == total) {
			status = "";
		} else if (turn == total) {
			status = "TURNVA";
		}
		updateByCondition("Purchase", "pu_acceptstatuscode='" + status + "',pu_acceptstatus='" + BaseUtil.getLocalMessage(status) + "'",
				"pu_code='" + pucode + "'");
	}

	/*
	 * 收料单删除后修改主表收料状态
	 */
	public void updateAccStatus(int and_id) {
		SqlRowList rs = queryForRowSet("select and_anid from AcceptNotifydetail where and_id=" + and_id);
		if (rs.next()) {
			int total = getCountByCondition("AcceptNotifydetail", "and_anid='" + rs.getInt("and_anid") + "'");
			int aud = getCountByCondition("AcceptNotifydetail", "and_anid='" + rs.getInt("and_anid") + "' AND nvl(and_yqty,0)=0");
			int turn = getCountByCondition("AcceptNotifydetail", "and_anid='" + rs.getInt("and_anid")
					+ "' AND nvl(and_yqty,0)=nvl(and_inqty,0)");
			String status = "PART2VA";
			if (aud == total) {
				status = "AUDITED";
			} else if (turn == total) {
				status = "TURNVA";
			}
			updateByCondition("AcceptNotify", "an_statuscode='" + status + "',an_status='" + BaseUtil.getLocalMessage(status) + "'",
					"an_id='" + rs.getInt("and_anid") + "'");
		}

	}

	public Map<Integer, String> turnQC(String ids, String qcClass, String qcType, String statusCode) {
		SqlRowList rs = queryForRowSet(TURNVERIFYAPPLYDETAIL_LIST.replace("@IDS", ids));
		SqlMap map = null;
		int pr_id;
		List<SqlMap> sqls = new ArrayList<SqlMap>();
		Map<Integer, String> returnQc = new HashMap<Integer, String>();
		List<String> callbackSqls = new ArrayList<String>();
		Employee employee = SystemSession.getUser();
		Boolean bool = false;
		Object quaprid = 0;
		while (rs.next()) {
			map = new SqlMap("QUA_VerifyApplyDetail");
			Object pv_method = baseDao.getFieldDataByCondition("ProductVendorIQC", "pv_method",
					" pv_vendcode='" + rs.getString("va_vendcode") + "' and pv_prodcode='" + rs.getString("vad_prodcode") + "'");
			if (pv_method == null) {
				baseDao.execute("insert into ProductVendorIQC(pv_id,pv_prodcode,pv_vendcode,pv_method)values(ProductVendorIQC_seq.nextval,'"
						+ rs.getString("vad_prodcode") + "','" + rs.getString("va_vendcode") + "','正常抽检')");
				baseDao.execute("update ProductVendorIQC set pv_prodid=(select pr_id from product where pr_code=pv_prodcode) where pv_prodcode='"
						+ rs.getString("vad_prodcode") + "'");
				baseDao.execute("update ProductVendorIQC set (pv_vendid,pv_vendname)=(select ve_id,ve_name from vendor where ve_code=pv_vendcode) where pv_prodcode='"
						+ rs.getString("vad_prodcode") + "'");
			}
			if (StringUtil.hasText(rs.getObject("vad_prodcode"))) {
				Object[] ob = baseDao.getFieldsDataByCondition("product ", new String[] { "pr_id", "pr_aql", "pr_qualmethod" }, "pr_code='"
						+ rs.getString("vad_prodcode") + "'");
				if (ob != null) {
					if (StringUtil.hasText(ob[2])) {
						quaprid = baseDao.getFieldDataByCondition("QUA_Project", " pr_id", " pr_code='" + ob[2] + "'");
						if (quaprid != null) {
							bool = true;
						}
					}
					pr_id = Integer.valueOf(ob[0].toString());
					Object ve_mod = baseDao.getFieldDataByCondition("ProductVendorIQC", " pv_method", " pv_prodid='" + pr_id
							+ "'and pv_vendcode=+'" + rs.getString("va_vendcode") + "'");
					String ve_method = null;
					if (ve_mod == null) {
						ve_method = "正常抽检";
						baseDao.updateByCondition("ProductVendorIQC", "pv_method='正常抽检'", " pv_prodid='" + pr_id + "'and pv_vendcode=+'"
								+ rs.getString("va_vendcode") + "'");
					} else {
						ve_method = ve_mod.toString();
					}
					map.set("ve_aql", ob[1]);
					map.set("ve_method", ve_method);
					if (quaprid != null) {
						map.set("ve_prid", quaprid);
					}
				}
			}
			int id = getSeqId("QUA_VERIFYAPPLYDETAIL_SEQ");
			String code = sGetMaxNumber("QUA_VerifyApplyDetail", 2);
			map.set("ve_id", id);
			map.set("ve_code", code);
			map.set("ve_class", qcClass);
			map.set("ve_status", BaseUtil.getLocalMessage(statusCode));
			map.set("ve_printstatus", BaseUtil.getLocalMessage("UNPRINT"));
			map.set("ve_checkstatus", BaseUtil.getLocalMessage("UNAPPROVED"));
			map.set("ve_checkstatuscode", "UNAPPROVED");
			map.set("ve_checkdate", "sysdate", true);
			map.set("ve_indate", "sysdate", true);
			map.set("ve_recorder", employee.getEm_name());
			map.set("ve_statuscode", statusCode);
			map.set("ve_type", qcType);
			map.set("ve_date", "sysdate", true);
			map.set("vad_vaid", rs.getInt("vad_vaid"));
			map.set("vad_code", rs.getString("va_code"));
			map.set("vad_class", rs.getString("vad_class"));
			map.set("vad_detno", rs.getInt("vad_detno"));
			map.set("vad_prodcode", rs.getString("vad_prodcode"));
			map.set("vad_qty", rs.getDouble("vad_qty"));
			map.set("vad_remark", rs.getGeneralString("vad_remark"));
			map.setObject("vad_sourcecode", rs.getString("vad_pucode"));
			map.set("vad_sourcedetno", rs.getInt("vad_pudetno"));
			map.setObject("ve_ordercode", rs.getString("vad_pucode"));
			map.set("ve_orderdetno", rs.getInt("vad_pudetno"));
			map.setObject("vad_vendcode", rs.getString("va_vendcode"));
			map.setObject("vad_vendname", rs.getString("va_vendname"));
			map.setDate("ve_makedate", rs.getDate("vad_madedate"));
			map.setObject("ve_sendcode", rs.getGeneralString("va_sendcode"));
			map.setObject("ve_batchcode", rs.getGeneralString("vad_batchcode"));
			map.setObject("ve_cop", rs.getGeneralString("va_cop"));
			map.setObject("vad_salecode", rs.getGeneralString("vad_salecode"));
			map.setObject("ve_buyercode", rs.getString("va_emcode"));
			map.setObject("ve_buyerman", rs.getString("va_emname"));
			map.set("ve_intype", rs.getObject("va_intype"));
			map.set("vad_jobcode", rs.getObject("vad_jobcode"));
			map.set("vad_mcid", rs.getGeneralInt("vad_mcid"));
			map.set("ve_description", rs.getString("vad_description"));
			/**
			 * @author wsy
			 * 双单位
			 */
			map.set("ve_purcqty", rs.getDouble("vad_purcqty"));
			sqls.add(map);
			returnQc.put(id, code);
			// 先判断收料单明细中ve_code是否为空
			boolean exists = checkIf("VerifyApplyDetail", "vad_id=" + rs.getInt("vad_id") + " and nvl(ve_code,' ')<>' '");
			if (exists)
				throw new SystemException("出现重复检验单，请刷新界面");
			// 写回检验单号
			callbackSqls.add("update VerifyApplyDetail set ve_id=" + id + ",ve_code='" + code + "',ve_status='"
					+ BaseUtil.getLocalMessage(statusCode) + "' where vad_id=" + rs.getInt("vad_id"));
			// callbackSqls
			// .add("update QUA_VerifyApplyDetail set ve_aql=(select max(al_code) "
			// +
			// "from QUA_Aql,QUA_AqlDetail where al_id=ad_alid and al_code=ve_aql and ad_quotecode=vad_prodcode and vad_qty>=ad_minqty and vad_qty<=ad_maxqty) "
			// + "where ve_id = " + id);
			callbackSqls
					.add("update QUA_VerifyApplyDetail set (ve_samplingaqty, ve_samplingngjgqty)=(select max(nvl(ad_qty,0)), max(nvl(ad_maxngacceptqty,0)) "
							+ "from QUA_Aql,QUA_AqlDetail where al_id=ad_alid AND al_statuscode='AUDITED' and al_code=ve_aql and vad_qty>=ad_minqty and vad_qty<=ad_maxqty) "
							+ "where ve_id = " + id);
			callbackSqls
					.add("update QUA_VerifyApplyDetail set (ve_samplingaqty, ve_samplingngjgqty)=(select max(nvl(ad_qty,0)), max(nvl(ad_maxngacceptqty,0)) "
							+ "from QUA_Aql,QUA_AqlDetail where al_id=ad_alid AND al_statuscode='AUDITED' and al_code=ve_aql and vad_qty>=ad_minqty and vad_qty<=ad_maxqty) "
							+ "where ve_id = " + id);
			if (bool) {
				callbackSqls
						.add("insert into QUA_ProjectDet(vd_id,vd_veid,vd_class,vd_detno,vd_item,vd_itemname,vd_mrjyyj,vd_jyyq,vd_jyff,vd_unit) "
								+ "select QUAPROJECTDET_SEQ.nextval,"
								+ id
								+ ",'"
								+ qcClass
								+ "',PD_DETNO,pd_itemcode,pd_itemname,ci_bases,ci_checkdevice,ci_checkmethod,ci_unit "
								+ "from QUA_ProjectDetail left join QUA_CheckItem on pd_ciid=ci_id where pd_prid=" + quaprid);
			}
		}
		// 修改数量及状态
		callbackSqls.add("update VerifyApplyDetail set vad_yqty=vad_qty where vad_id in (" + ids + ")");
		batchExecute(sqls, callbackSqls);
		return returnQc;
	}

	public Map<Integer, String> turnFreeQC(String ids, String qcClass, String qcType, String statusCode) {
		SqlRowList rs = queryForRowSet(TURNVERIFYAPPLYDETAIL_LIST.replace("@IDS", ids));
		SqlMap map = null;
		List<SqlMap> sqls = new ArrayList<SqlMap>();
		List<String> callbackSqls = new ArrayList<String>();
		Map<Integer, String> returnQc = new HashMap<Integer, String>();
		Employee employee = SystemSession.getUser();
		while (rs.next()) {
			map = new SqlMap("QUA_VerifyApplyDetail");
			int id = getSeqId("QUA_VERIFYAPPLYDETAIL_SEQ");
			String code = sGetMaxNumber("QUA_VerifyApplyDetail", 2);
			map.set("ve_id", id);
			map.set("ve_code", code);
			map.set("ve_class", qcClass);
			map.set("ve_method", "免检");
			map.set("ve_result", "合格");
			map.set("ve_status", BaseUtil.getLocalMessage(statusCode));
			map.set("ve_printstatus", BaseUtil.getLocalMessage("UNPRINT"));
			map.set("ve_checkdate", "sysdate", true);
			map.set("ve_indate", "sysdate", true);
			map.set("ve_auditdate", "sysdate", true);
			map.set("ve_auditman", employee.getEm_name());
			map.set("ve_recorder", employee.getEm_name());
			map.set("ve_statuscode", statusCode);
			map.set("ve_type", qcType);
			map.set("ve_date", "sysdate", true);
			map.set("vad_vaid", rs.getInt("vad_vaid"));
			map.set("vad_code", rs.getString("va_code"));
			map.set("vad_class", rs.getString("vad_class"));
			map.set("vad_detno", rs.getInt("vad_detno"));
			map.set("vad_prodcode", rs.getString("vad_prodcode"));
			map.set("vad_qty", rs.getDouble("vad_qty"));
			map.set("vad_remark", rs.getGeneralString("vad_remark"));
			map.set("vad_sourcecode", rs.getString("vad_pucode"));
			map.set("vad_sourcedetno", rs.getInt("vad_pudetno"));
			map.set("ve_ordercode", rs.getString("vad_pucode"));
			map.set("ve_orderdetno", rs.getInt("vad_pudetno"));
			map.set("vad_vendcode", rs.getString("va_vendcode"));
			map.set("vad_vendname", rs.getString("va_vendname"));
			map.setDate("ve_makedate", rs.getDate("vad_madedate"));
			map.set("ve_sendcode", rs.getGeneralString("va_sendcode"));
			map.set("ve_batchcode", rs.getGeneralString("vad_batchcode"));
			map.set("ve_cop", rs.getGeneralString("va_cop"));
			map.set("vad_salecode", rs.getGeneralString("vad_salecode"));
			map.set("ve_checkstatus", BaseUtil.getLocalMessage("APPROVE"));// dingyl
			map.set("ve_checkstatuscode", "APPROVE");
			map.set("ve_buyercode", rs.getString("va_emcode"));
			map.set("ve_buyerman", rs.getString("va_emname"));
			map.set("ve_intype", rs.getObject("va_intype"));
			map.set("vad_jobcode", rs.getObject("vad_jobcode"));
			map.set("vad_mcid", rs.getGeneralInt("vad_mcid"));
			/**
			 * @author wsy
			 * 双单位
			 */
			map.set("ve_purcqty", rs.getDouble("vad_purcqty"));
			sqls.add(map);
			returnQc.put(id, code);
			// 先判断收料单明细中ve_code是否为空
			boolean exists = checkIf("VerifyApplyDetail", "vad_id=" + rs.getInt("vad_id") + " and nvl(ve_code,' ')<>' '");
			if (exists)
				throw new SystemException("出现重复检验单，请刷新界面");
			// 写回检验单号
			callbackSqls.add("update VerifyApplyDetail set ve_id=" + id + ",ve_code='" + code + "',ve_status='"
					+ BaseUtil.getLocalMessage(statusCode) + "'  where vad_id=" + rs.getInt("vad_id"));
			SqlMap det = new SqlMap("QUA_VerifyApplyDetailDet");
			det.setSpecial("ved_id", getSeqId("QUA_VERIFYAPPLYDETAILDET_SEQ"));
			det.set("ved_veid", id);
			det.set("ved_detno", 1);
			det.set("ved_okqty", rs.getDouble("vad_qty"));
			det.setSpecial("ved_date", "sysdate");
			det.setSpecial("ved_checkdate", "sysdate");
			det.set("ved_checkqty", rs.getDouble("vad_qty"));
			det.set("ved_status", BaseUtil.getLocalMessage(statusCode));
			det.set("ved_statuscode", statusCode);
			det.set("ved_code", code);
			sqls.add(det);
		}
		callbackSqls.add("update VerifyApplyDetail set vad_yqty=vad_qty,vad_jyqty=vad_qty,ve_okqty=vad_qty,ve_notokqty=0 where vad_id in ("
				+ ids + ")");
		batchExecute(sqls, callbackSqls);
		return returnQc;
	}

	/**
	 * 收料单审核后把相关数据反写回来源单中
	 */
	@Override
	public void updatesourceqty(int vaid) {
		Object vaclass = getFieldDataByCondition("VerifyApply", "va_class", "va_id=" + vaid);
		List<Object[]> objs = getFieldsDatasByCondition("VerifyApplyDetail", new String[] { "vad_pudetno", "vad_pucode", "sum(vad_qty)",
				"vad_prodcode" }, "vad_vaid=" + vaid + " group by vad_pucode, vad_pudetno, vad_prodcode");
		for (Object[] obj : objs) {
			if ("采购收料单".equals(vaclass.toString())) {
				updateByCondition("PurchaseDetail", "pd_reconhand = nvl(pd_reconhand,0)+" + obj[2] + ",pd_totested=NVL(pd_totested,0)+"
						+ obj[2], "pd_code='" + obj[1] + "' and pd_detno = " + obj[0]);
			} else if ("委外收料单".equals(vaclass.toString())) {
				updateByCondition("Make", "ma_reconhand = nvl(ma_reconhand,0)+" + obj[2] + ",ma_totested = NVL(ma_totested,0)+" + obj[2],
						"ma_code='" + obj[1] + "'");
			}
			updateByCondition("Product", "pr_reconhand = nvl(pr_reconhand,0)+" + obj[2] + ",pr_totested =NVL(pr_totested,0)+" + obj[2],
					"pr_code='" + obj[3] + "'");
		}

	}

	/**
	 * 收料单反审核后取消收料仓数量
	 */
	@Override
	public void resauditsourceqty(int vaid) {
		Object vaclass = getFieldDataByCondition("VerifyApply", "va_class", "va_id=" + vaid);
		List<Object[]> objs = getFieldsDatasByCondition("VerifyApplyDetail", new String[] { "vad_pudetno", "vad_pucode", "sum(vad_qty)",
				"vad_prodcode" }, "vad_vaid=" + vaid + " group by vad_pucode, vad_pudetno, vad_prodcode");
		for (Object[] obj : objs) {
			if ("采购收料单".equals(vaclass.toString())) {
				updateByCondition("PurchaseDetail", "pd_reconhand = nvl(pd_reconhand,0)-" + obj[2] + ",pd_totested=NVL(pd_totested,0)-"
						+ obj[2], "pd_code='" + obj[1] + "' and pd_detno = " + obj[0]);
			} else if ("委外收料单".equals(vaclass.toString())) {
				updateByCondition("Make", "ma_reconhand = nvl(ma_reconhand,0)-" + obj[2] + ",ma_totested = NVL(ma_totested,0)-" + obj[2],
						"ma_code='" + obj[1] + "'");
			}
			updateByCondition("Product", "pr_reconhand = nvl(pr_reconhand,0)-" + obj[2] + ",pr_totested =NVL(pr_totested,0)-" + obj[2],
					"pr_code='" + obj[3] + "'");
		}
	}

	private String lpad(int length, String number) {
		while (number.length() < length) {
			number = "0" + number;
		}
		number = number.substring(number.length() - length, number.length());
		return number;
	}

	@Override
	public String barcodeMethod(String pr_code, String ve_id, int num) {
		StringBuffer code = new StringBuffer();
		SqlRowList rs;
		String date = "0";
		if (num > 9) {
			BaseUtil.showError("请重置生成条码流水");
		}
		int c1 = baseDao.getCount("select count(1) from barcodeSet where bs_type='USER' ");
		if (c1 > 0) {
			String res = baseDao.callProcedure("SP_GETBARCODE", new Object[] { pr_code, ve_id });
			if (res != null && !res.trim().equals("")) {
				if (res.startsWith("BARCODE:")) {
					return res.substring(8);
				} else {
					BaseUtil.showError("条码号生成失败");
					return null;
				}
			}
		}
		rs = queryForRowSet("select pr_id ,pr_tracekind,pr_serialtype,pr_exbarcode from product where pr_code=?", pr_code);
		if (rs.next()) {
			Object obs[] = getFieldsDataByCondition("barcodeSet", new String[] { "bs_lenprid", "bs_datestr", "bs_lennum", "bs_maxnum",
					"bs_maxdate", "bs_lenveid" }, "bs_type='BATCH'");
			if (obs == null || ("").equals(obs)) {
				BaseUtil.showError("请先定义条码产生规则");
			}
			code.append(lpad(Integer.valueOf(obs[0].toString()), rs.getString("pr_id")));// PR_ID物料ID的长度
			code.append(lpad(Integer.valueOf(obs[5].toString()), ve_id));
			if (obs[1].equals("YYMMDD")) {
				SimpleDateFormat YMD = new SimpleDateFormat("yyMMdd");
				date = YMD.format(new Date());
			} else if (obs[1].equals("YYMM")) {
				SimpleDateFormat YM = new SimpleDateFormat("yyMM");
				date = YM.format(new Date());
			} else if (obs[1].equals("MMDD")) {
				SimpleDateFormat MD = new SimpleDateFormat("MMdd");
				date = MD.format(new Date());
			}
			code.append(date);// 日期
			updateByCondition("barcodeSet", "bs_maxdate='" + date + "'", "bs_type='BATCH'");
			if (!("").equals(obs[4]) && null != obs[4] && (!date.equals("0"))
					&& (Integer.valueOf(obs[4].toString()) > Integer.valueOf(date))) {// 如果当前日期大于上次日期
				code.append(lpad(Integer.valueOf(obs[2].toString()), "1"));// 流水重新开始
				updateByCondition("barcodeSet", "bs_maxnum=2", "bs_type='BATCH'");// 流水号增加1
			} else {
				code.append(lpad(Integer.valueOf(obs[2].toString()), obs[3].toString()));// 当前流水号
				updateByCondition("barcodeSet", "bs_maxnum=bs_maxnum+1", "bs_type='BATCH'");// 流水号增加1
			}
		}
		int cn = baseDao.getCount("select count(1) from barcode where bar_code='" + code + "' and bar_status<>2");
		if (cn > 0) {
			barcodeMethod(pr_code, ve_id, num++);
		}
		cn = baseDao.getCount("select count(1) from barcodeio where bi_barcode='" + code + "' and bi_status=0");
		if (cn > 0) {
			barcodeMethod(pr_code, ve_id, num++);
		}
		return code.toString();
	}

	@Override
	public String outboxMethod(String pr_id, String kind) {
		StringBuffer code = new StringBuffer();
		SqlRowList rs, rs1;
		rs = queryForRowSet("select pr_tracekind from product where pr_id='" + pr_id + "'");
		if (rs.next()) {
			rs1 = queryForRowSet("select bs_id ,bs_lenprid,bs_lennum,bs_maxnum from barcodeSet where bs_type='PACK'");
			if (rs1.next()) {
				code.append(lpad(Integer.valueOf(rs1.getInt("bs_lenprid")), pr_id));// PR_ID物料ID的长度
				code.append(lpad(rs1.getInt("bs_lennum"), rs1.getString("bs_maxnum")));// 当前流水号
				updateByCondition("barcodeSet", "bs_maxnum=bs_maxnum+1", "bs_type='PACK' and  bs_id=" + rs1.getInt("bs_id"));// 流水号增加1
			} else {
				throw new SystemException("未定义包装箱号产生规则或规则为审核");
			}
		} else {
			throw new SystemException("管控类型错误！");
		}
		return code.toString();
	}

	@Override
	public void restorePurcYqty(Object vadid, double uqty, String vad_pucode, Integer vad_pudetno) {
		Object[] id = getFieldsDataByCondition("purchasedetail left join purchase on pd_puid=pu_id", new String[] { "pd_id", "pd_yqty",
				"pd_qty" }, "pu_code='" + vad_pucode + "' and pd_detno=" + vad_pudetno);
		Object y = getFieldDataByCondition("VerifyApplyDetail", "sum(nvl(vad_qty,0))", "vad_pucode='" + vad_pucode + "' and vad_pudetno="
				+ vad_pudetno + " and vad_id<>" + vadid);
		Object r = getFieldDataByCondition("ProdIODetail left join ProdInOut on pd_piid=pi_id", "sum(nvl(pd_outqty,0))",
				"pd_piclass In ('采购验退单','不良品出库单') and pi_statuscode='POSTED' and pd_ordercode='" + vad_pucode + "' and pd_orderdetno="
						+ vad_pudetno);
		y = y == null ? 0 : y;
		r = r == null ? 0 : r;
		if (id != null) {
			if (NumberUtil.formatDouble(Double.parseDouble(y.toString()) + uqty, 2) > NumberUtil.formatDouble(
					Double.valueOf(id[2].toString()) + Double.parseDouble(r.toString()), 2)) {
				BaseUtil.showError("采购单号为:" + vad_pucode + ",序号为:" + vad_pudetno + "数量超发,原数量为:" + id[2].toString() + ",已转数为:" + id[1]
						+ ".请修改数量!");
			} else {
				updateByCondition("purchasedetail", "pd_yqty=nvl(pd_yqty,0)+" + uqty, "pd_id=" + id[0]);
				updatePurcStatus2(vad_pucode);
			}
		} else {
			BaseUtil.showError("采购单号为:" + vad_pucode + ",序号为:" + vad_pudetno + "不存在,请核对后重新修改!");
		}
	}

}
