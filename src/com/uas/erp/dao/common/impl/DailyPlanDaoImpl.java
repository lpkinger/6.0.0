package com.uas.erp.dao.common.impl;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

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
import com.uas.erp.dao.common.DailyPlanDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;

@Repository
public class DailyPlanDaoImpl extends BaseDao implements DailyPlanDao {

	final static String DailyPlan_PRICE = "select ppd_price,ppd_rate from DailyPlanPriceDetail left join DailyPlanPrice on ppd_ppid=pp_id where ppd_vendcode=? and ppd_prodcode=? and ppd_currency=? and pp_kind=? and to_char(ppd_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID' and ppd_lapqty<=? order by ppd_price ";
	final static String DailyPlan_PRICE_APPSTATUS = "select ppd_price,ppd_rate from DailyPlanPriceDetail left join DailyPlanPrice on ppd_ppid=pp_id where ppd_vendcode=? and ppd_prodcode=? and ppd_currency=? and pp_kind=? and to_char(ppd_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID' and nvl(ppd_appstatus,' ')='合格' and ppd_lapqty<=? order by ppd_price ";
	// static final String TURNACCNOTIFY =
	// "SELECT * FROM DailyPlan WHERE pu_id=?";
	static final String INSERTACCNOTIFY = "INSERT INTO acceptnotify(an_id,an_code,an_sourceid,an_source,an_vendcode,an_vendname,an_receivecode,an_receivename,"
			+ "an_currency,an_rate,an_buyerid,an_buyer,an_paymentcode,an_payment,an_transport,an_status,an_recorder,an_recorderid,an_indate,an_statuscode,an_date"
			+ ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,'ENTERING',sysdate)";
	static final String TURNACCNOTIFYDETAIL = "SELECT * FROM DailyPlandetail WHERE pd_puid=? order by pd_detno";
	static final String INSERTACCNOTIFYDETAIL = "INSERT INTO acceptnotifydetail(and_id,and_anid,and_detno,and_orderid,and_ordercode,and_orderdetno,"
			+ "and_prodcode,and_inqty,and_orderprice,and_taxrate,and_netprice,and_beipin,and_ordertotal,and_taxtotal,and_sellercode,and_seller,and_remark"
			+ ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String TURNANDETAIL = "SELECT * FROM DailyPlanDetail left join DailyPlan on pd_puid=pu_id WHERE pd_id=?";
	static final String DailyPlanDETAIL = "SELECT pd_prodcode,pu_date,pu_code,pd_detno,pu_vendcode,pu_vendname,pu_id FROM "
			+ "DailyPlanDetail left join DailyPlan on pd_puid=pu_id WHERE pd_id=?";
	static final String INSERTVERIFYAPPLY = "INSERT INTO verifyapply(va_id, va_code, va_statuscode, va_status, va_recorder,va_indate,va_date,va_vendcode,va_vendname,va_receivecode,va_receivename,va_class) values (?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String INSERTVERIFYAPPLYBASE = "INSERT INTO verifyapply(va_id, va_code, va_statuscode, va_status, va_recorder,va_indate,va_date,va_class) values (?,?,?,?,?,?,?,?)";
	static final String INSERTVERIFYAPPLYDETAIL = "INSERT INTO verifyapplydetail(vad_id, vad_vaid,vad_code, vad_detno,vad_class,vad_prodcode,"
			+ "vad_qty,vad_pudate,vad_sourcecode,vad_pucode,vad_pudetno,vad_status,vad_vendcode,vad_vendname,ve_status) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String GETVENDER = " select ve_apvendcode,ve_apvendname from vendor where ve_code=?";

	final static String INSERT_PRODIO_VEND = "INSERT INTO ProdInOut(pi_id, pi_inoutno, pi_date, pi_class, pi_invostatus, pi_invostatuscode,pi_recordman, pi_recorddate, pi_cardcode,pi_title"
			+ ", pi_cardid, pi_status, pi_statuscode,pi_updatedate,pi_updateman,pi_printstatus,pi_printstatuscode,pi_currency,pi_payment,pi_rate,pi_receivecode,pi_receivename) VALUES "
			+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static String INSERT_PRODIODETAIL = "INSERT INTO prodiodetail(pd_piid,pd_id,pd_inoutno,pd_piclass,pd_pdno,pd_ordercode,pd_orderdetno,pd_prodcode,pd_prodmadedate,pd_inqty,pd_orderprice"
			+ ",pd_taxrate,pd_taxtotal,pd_status,pd_custprodcode,pd_orderid,pd_auditstatus,pd_accountstatuscode,pd_accountstatus,pd_price,pd_total,pd_sellercode,pd_seller,pd_remark"
			+ ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static String PU_TOTAL = "update DailyPlan set pu_total=(select sum(pd_total) from DailyPlanDetail where DailyPlanDetail.pd_puid = DailyPlan.pu_id) where pu_id=?";
	final static String PU_TAXTOTAL = "update DailyPlan set pu_taxtotal=(select sum(pd_taxtotal) from DailyPlanDetail where DailyPlanDetail.pd_puid = DailyPlan.pu_id) where pu_id=?";

	final static String PRICE_VENDOR = "select round(min(ppd_price*(1-nvl(ppd_rate,0)/(100+nvl(ppd_rate,0)))*cr_rate),8) as price,ppd_vendcode from DailyPlanPriceDetail left join DailyPlanPrice on ppd_ppid=pp_id left join currencys on ppd_currency=cr_name  where  ppd_prodcode=? and pp_kind=? and to_char(ppd_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID' and ppd_lapqty<=? group by ppd_vendcode order by price ";
	final static String PRICE_VENDOR_APPSTATUS = "select round(min(ppd_price*(1-nvl(ppd_rate,0)/(100+nvl(ppd_rate,0)))*cr_rate),8) as price,ppd_vendcode from DailyPlanPriceDetail left join DailyPlanPrice on ppd_ppid=pp_id left join currencys on ppd_currency=cr_name  where  ppd_prodcode=? and pp_kind=? and to_char(ppd_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID' and nvl(ppd_appstatus,' ')='合格' and ppd_lapqty<=? group by ppd_vendcode order by price ";
	final static String PRICE_VENDOR2 = "select * from (SELECT ppd_vendcode,ppd_rate,cr_rate,ppd_currency,ppd_price,ppd_price * cr_rate * (1 - ppd_rate / (100 + ppd_rate)) as price,rank() over (PARTITION BY ppd_prodcode order by (ppd_price * cr_rate * (1 - ppd_rate / (100 + ppd_rate))) asc ,ppd_id desc) mm FROM DailyPlanPriceDetail left join DailyPlanPrice on ppd_ppid=pp_id left join currencys on ppd_currency=cr_name WHERE ppd_prodcode =? and pp_kind=? and ppd_lapqty<=? and to_char(ppd_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID'  )  where mm=1  order by price";
	final static String PRICE_VENDOR_APPSTATUS2 = "select * from (select * from (SELECT ppd_vendcode,ppd_rate,cr_rate,ppd_currency,ppd_price,ppd_price * cr_rate * (1 - ppd_rate / (100 + ppd_rate)) as price,rank() over (PARTITION BY ppd_prodcode order by (ppd_price * cr_rate * (1 - ppd_rate / (100 + ppd_rate))) asc ,ppd_id desc) mm FROM DailyPlanPriceDetail left join DailyPlanPrice on ppd_ppid=pp_id left join currencys on ppd_currency=cr_name WHERE ppd_prodcode =? and pp_kind=? and ppd_lapqty<=? and to_char(ppd_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID' and nvl(ppd_appstatus,' ')='合格' )  where mm=1  order by price";

	/**
	 * 转收料单
	 */
	@Override
	@Transactional
	public String turnAccept(String caller, List<Map<Object, Object>> maps,
			Employee employee, String language) {
		int count = 1;
		String code = null;
		int vaid = 0;
		Set<String> codes = new HashSet<String>();// 根据采购明细ID，找出对应的哪些采购主表的code
		String log = null;
		for (Map<Object, Object> map : maps) {
			SqlRowList rs = queryForRowSet(DailyPlanDETAIL,
					new Object[] { map.get("pd_id") });
			SqlRowList rsDetail;
			String receiveCode = null, receiveName = null;
			if (rs.next()) {
				if (code == null) {
					code = sGetMaxNumber("VerifyApply", 2);
					vaid = getSeqId("VERIFYAPPLY_SEQ");
					Timestamp time = Timestamp.valueOf(DateUtil
							.currentDateString(Constant.YMD_HMS));
					rsDetail = queryForRowSet(GETVENDER, new Object[] { rs
							.getObject("pu_vendcode").toString() });
					if (rsDetail.next()) {
						receiveCode = rsDetail.getObject(1).toString();
						receiveName = rsDetail.getObject(2).toString();
					}
					getJdbcTemplate().update(
							INSERTVERIFYAPPLY,
							new Object[] {
									vaid,
									code,
									"ENTERING",
									BaseUtil.getLocalMessage("ENTERING",
											language), employee.getEm_name(),
									time, time, rs.getObject("pu_vendcode"),
									rs.getObject("pu_vendname"), receiveCode,
									receiveName, "采购收料单" });
				}
				int vadid = getSeqId("VERIFYAPPLYDETAIL_SEQ");
				String pucode = rs.getObject(3).toString();
				getJdbcTemplate().update(
						INSERTVERIFYAPPLYDETAIL,
						new Object[] { vadid, vaid, code, count++, "采购入库申请单",
								rs.getObject(1), map.get("pd_tqty"),
								rs.getObject(2), pucode, pucode,
								rs.getObject(4),
								BaseUtil.getLocalMessage("ENTERING"),
								rs.getObject(5), rs.getObject(6),
								BaseUtil.getLocalMessage("UNAUDIT") });
				// 转成功就修改DailyPlanDetail的[已转数量]
				Object qt = getFieldDataByCondition("DailyPlanDetail",
						"pd_yqty", "pd_id=" + map.get("pd_id"));
				qt = qt == null ? 0 : qt;
				Double yqty = Double.parseDouble(qt.toString())
						+ Double.parseDouble(map.get("pd_tqty").toString());
				updateByCondition("DailyPlanDetail", "pd_yqty=" + yqty, "pd_id="
						+ map.get("pd_id"));
				// 按采购单号分组
				if (!codes.contains(pucode)) {
					codes.add(pucode);
				}
				// 记录日志
				logMessage(new MessageLog(employee.getEm_name(),
						BaseUtil.getLocalMessage("msg.turnVerifyApply",
								language), BaseUtil.getLocalMessage(
								"msg.turnSuccess")
								+ ","
								+ BaseUtil.getLocalMessage("msg.detail",
										language) + rs.getInt("pd_detno"),
						"DailyPlan|pu_id=" + rs.getInt("pu_id")));
			}
		}
		log = "转入成功,收料单号:"
				+ "<a href=\"javascript:openUrl('jsps/scm/DailyPlan/verifyApply.jsp?formCondition=va_idIS"
				+ vaid + "&gridCondition=vad_vaidIS" + vaid
				+ "&whoami=VerifyApply')\">" + code + "</a>";
		execute("update VerifyApplyDetail set vad_unitpackage=vad_qty where vad_vaid="
				+ vaid);
		// 修改DailyPlan为部分转收料\已转收料
		Iterator<String> iterator = codes.iterator();
		while (iterator.hasNext()) {
			String str = iterator.next();
			int total = getCountByCondition("DailyPlanDetail", "pd_code='" + str
					+ "'");
			int aud = getCountByCondition("DailyPlanDetail", "pd_code='" + str
					+ "' AND nvl(pd_yqty,0)=0");
			int turn = getCountByCondition("DailyPlanDetail", "pd_code='" + str
					+ "' AND nvl(pd_yqty,0)=nvl(pd_qty,0)");
			String status = "PART2VA";
			if (aud == total) {
				status = "";
			} else if (turn == total) {
				status = "TURNVA";
			}
			updateByCondition("DailyPlan",
					"pu_acceptstatuscode='" + status + "',pu_acceptstatus='"
							+ BaseUtil.getLocalMessage(status) + "'",
					"pu_code='" + str + "'");
		}
		return log;
	}

	/**
	 * 更新上次采购价格、供应商
	 */
	@Override
	public void updatePreDailyPlan(String pu_code, String pu_date) {
		String sql = "SELECT * FROM DailyPlanDetail WHERE pd_code=" + pu_code
				+ " AND pd_price=0";
		SqlRowList rs1 = queryForRowSet(sql);
		while (rs1.next()) {
			sql = "SELECT DailyPlanDetail.pd_price*DailyPlanDetail.pd_rate,DailyPlanDetail.pd_vendname,DailyPlanDetail.pd_code from DailyPlanDetail"
					+ ",DailyPlan WHERE pu_code<>'"
					+ pu_code
					+ "' and pu_date<='"
					+ pu_date
					+ "' and pd_prodcode='"
					+ rs1.getObject("pd_prodcode")
					+ "' and (pu_statuscode='56' or pu_statuscode='114' or pu_statuscode='115') order by pu_date desc ";
			SqlRowList rs2 = queryForRowSet(sql);
			if (rs2.next()) {
				sql = "UPDATE DailyPlanDetail SET pd_preprice="
						+ rs2.getObject(1) + ",pd_prevendor='"
						+ rs2.getObject(2) + "',pd_precode='"
						+ rs2.getObject(3) + "' where pd_code='" + pu_code
						+ "' and pd_detno=" + rs1.getObject("pd_detno");
				execute(sql);
			}
		}
	}

	/**
	 * 更新采购计划下达数\本次下达数\状态
	 */
	@Override
	public void updateDailyPlanPlan(int pu_id) {
		String sql = "UPDATE DailyPlanPlan SET pp_endqty=(SELECT sum(pd_qty) from DailyPlandetail WHERE pd_ppid=pp_id) WHERE pp_id in (SELECT pd_ppid FROM DailyPlanDetail WHERE pd_puid="
				+ pu_id + ")";
		execute(sql);
		sql = "UPDATE DailyPlanPlan SET pp_thisqty=nvl(pp_planqty,0)-nvl(pp_endqty,0) WHERE pp_id IN (SELECT pd_ppid FROM DailyPlanDetail WHERE pd_puid="
				+ pu_id + ")";
		execute(sql);
		sql = "UPDATE DailyPlanPlan SET pp_status='未下达' WHERE pp_endqty<pp_planqty and pp_id IN (SELECT pd_ppid FROM DailyPlanDetail WHERE pd_puid="
				+ pu_id + ")";
		execute(sql);
	}

	/**
	 * 下达数不能超计划数量检测
	 */
	@Override
	public String checkPlanQty(int pu_id) {
		String sql = "SELECT * FROM  DailyPlanplan WHERE pp_endqty>pp_planqty AND pp_id IN (SELECT pd_ppid FROM DailyPlandetail where pd_puid="
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
		return sb.length() > 0 ? sb.toString().substring(0,
				sb.lastIndexOf(",") - 1) : null;
	}

	/**
	 * 计划ID料号错位检测
	 */
	@Override
	public String checkPPcode(int pu_id) {
		String sql = "SELECT * FROM  DailyPlandetail LEFT JOIN DailyPlanplan ON pp_id=pd_ppid WHERE pd_puid="
				+ pu_id + " AND pd_prodcode<>pp_prodcode AND pd_ppid>0";
		SqlRowList set = queryForRowSet(sql);
		StringBuffer sb = new StringBuffer();
		while (set.next()) {
			sb.append("{pd_detno:" + set.getGeneralString("pd_detno"));
			sb.append(",pp_prodcode:\"" + set.getInt("pp_prodcode"));
			sb.append("\"},");
		}
		return sb.length() > 0 ? sb.toString().substring(0,
				sb.lastIndexOf(",") - 1) : null;
	}

	/**
	 * 采购单转收料通知单
	 */
	@Override
	/*
	 * public int turnAccNotify(int id, String language, Employee employee) {
	 * SqlRowList rs = queryForRowSet(TURNACCNOTIFY, new Object[] { id }); int
	 * anid = 0; if (rs.next()) { anid = getSeqId("ACCEPTNOTIFY_SEQ"); String
	 * code = sGetMaxNumber("AcceptNotify", 2); boolean bool = execute(
	 * INSERTACCNOTIFY, new Object[] { anid, code, rs.getObject("pu_id"),
	 * rs.getObject("pu_code"), rs.getObject("pu_vendcode"),
	 * rs.getObject("pu_vendname"), rs.getObject("pu_receivecode"),
	 * rs.getObject("pu_receivename"), rs.getObject("pu_currency"),
	 * rs.getObject("pu_rate"), rs.getObject("pu_buyerid"),
	 * rs.getObject("pu_buyername"), rs.getObject("pu_paymentscode"),
	 * rs.getObject("pu_payments"), rs.getObject("pu_transport"),
	 * BaseUtil.getLocalMessage("ENTERING"), employee.getEm_name(),
	 * employee.getEm_id() }); if (bool) { rs =
	 * queryForRowSet(TURNACCNOTIFYDETAIL, new Object[] { id }); int count = 1;
	 * while (rs.next()) { int andid = getSeqId("ACCEPTNOTIFYDETAIL_SEQ");
	 * execute(INSERTACCNOTIFYDETAIL, new Object[] { andid, anid, count++,
	 * rs.getObject("pd_id"), rs.getObject("pd_code"), rs.getObject("pd_detno"),
	 * rs.getObject("pd_prodcode"), rs.getObject("pd_qty"),
	 * rs.getObject("pd_price"), rs.getObject("pd_rate"),
	 * rs.getObject("and_netprice"), rs.getObject("pd_beipin"),
	 * rs.getObject("pd_total"), rs.getObject("pd_taxtotal"),
	 * rs.getObject("pd_sellercode"), rs.getObject("pd_seller"),
	 * rs.getObject("pd_remark") }); } } } return anid; }
	 */
	// @Override
	public int newVerifyApply(Employee employee, String language) {
		int vaid = getSeqId("VERIFYAPPLY_SEQ");
		String code = sGetMaxNumber("VerifyApply", 2);
		Timestamp time = Timestamp.valueOf(DateUtil
				.currentDateString(Constant.YMD_HMS));
		boolean bool = execute(
				INSERTVERIFYAPPLYBASE,
				new Object[] { vaid, code, "ENTERING",
						BaseUtil.getLocalMessage("ENTERING"),
						employee.getEm_name(), time, time, "采购收料单" });
		if (bool)
			return vaid;
		return 0;
	}

	public String newVerifyApplyRetCode(Employee employee, String language) {
		int vaid = getSeqId("VERIFYAPPLY_SEQ");
		String code = sGetMaxNumber("VerifyApply", 2);
		Timestamp time = Timestamp.valueOf(DateUtil
				.currentDateString(Constant.YMD_HMS));
		boolean bool = execute(
				INSERTVERIFYAPPLYBASE,
				new Object[] { vaid, code, "ENTERING",
						BaseUtil.getLocalMessage("ENTERING"),
						employee.getEm_name(), time, time, "采购收料单" });
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
		SqlRowList rs = queryForRowSet("SELECT pd_status FROM DailyPlanDetail where pd_puid="
				+ puid);
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
	public void deleteDailyPlan(int id) {
		List<Object[]> objs = getFieldsDatasByCondition("DailyPlanDetail",
				new String[] { "dpd_id" }, "dpd_dpid=" + id);
		for (Object[] obj : objs) {
			deleteByCondition("DailyPlanDetail", "dd_id=" + obj[0]);
		}
	}

	public void updateAppStatus(int ad_id, String language) {
		int idx = Integer.parseInt(getFieldDataByCondition("ApplicationDetail",
				"ad_apid", "ad_id=" + ad_id).toString());
		int total = getCountByCondition("ApplicationDetail", "ad_id=" + ad_id);
		int aud = getCountByCondition("ApplicationDetail", "ad_id=" + ad_id
				+ " AND nvl(ad_yqty,0)=0");
		int turn = getCountByCondition("ApplicationDetail", "ad_id=" + ad_id
				+ " AND nvl(ad_yqty,0)=ad_qty");
		String status = "PART2PU";
		if (aud == total) {
			status = "";
		} else if (turn == total) {
			status = "TURNPURC";
		}
		updateByCondition(
				"Application",
				"ap_turnstatuscode='" + status + "',ap_turnstatus='"
						+ BaseUtil.getLocalMessage(status) + "'",
				"ap_id=" + idx);
	}

	/**
	 * 采购单转采购验收单
	 */
	@Override
	public String detailTurnPurcProdIO(String no, int pdid, int detno,
			Double qty, Employee employee, String language) {
		Object[] objs = getFieldsDataByCondition(
				"DailyPlanDetail left join DailyPlan on pu_id=pd_puid",
				new String[] { "pd_code", "pd_detno", "pd_prodcode", "pd_qty",
						"pd_price", "pd_rate", "pd_custprodcode", "pd_id",
						"pd_sellercode", "pd_seller", "pu_currency",
						"pd_remark" }, "pd_id=" + pdid);
		Object id = getFieldDataByCondition("ProdInOut", "pi_id",
				"pi_inoutno='" + no + "'");
		double price = Double.parseDouble(objs[4].toString());
		double rate = Double.parseDouble(objs[5].toString());
		String currenctDate = DateUtil.currentDateString("yyyy-MM-dd");
		currenctDate = currenctDate.substring(0, currenctDate.length() - 3);
		currenctDate = currenctDate.subSequence(0, 4)
				+ currenctDate.substring(5, 7);
		Object currentRate = getFieldDataByCondition("currencysMonth",
				"cm_crrate", "cm_crname='" + objs[10] + "' and cm_yearmonth='"
						+ currenctDate + "'");
		execute(INSERT_PRODIODETAIL,
				new Object[] {
						id,
						getSeqId("PRODIODETAIL_SEQ"),
						no,
						"采购验收单",
						detno,
						objs[0],
						objs[1],
						objs[2],
						Timestamp.valueOf(DateUtil
								.currentDateString(Constant.YMD_HMS)),
						qty,
						price,
						rate,
						price * qty,
						0,
						objs[6],
						pdid,
						"ENTERING",
						"UNACCOUNT",
						BaseUtil.getLocalMessage("UNACCOUNT"),
						price / (1 + rate / 100)
								* Double.valueOf(currentRate.toString()),
						qty * price / (1 + rate / 100), objs[8], objs[9],
						objs[11] });
		// 修改状态
		updateByCondition("DailyPlanDetail",
				"pd_status='PART2IN',pd_yqty=nvl(pd_yqty,0)+" + qty, "pd_id="
						+ pdid);
		updateByCondition("DailyPlanDetail", "pd_status='TURNIN'", "pd_id="
				+ pdid + " AND pd_qty=nvl(pd_yqty,0) and nvl(pd_yqty,0)>0");
		Integer pu_id = getJdbcTemplate().queryForObject(
				"select nvl(pd_puid,0) from DailyPlanDetail where pd_id=?",
				Integer.class, pdid);
		int tal = getCountByCondition("DailyPlanDetail", "pd_puid=" + pu_id);
		int aud = getCountByCondition("DailyPlanDetail", "pd_puid=" + pu_id
				+ " AND nvl(pd_yqty,0)=0");
		int turn = getCountByCondition("DailyPlanDetail", "pd_puid=" + pu_id
				+ " AND nvl(pd_yqty,0)=nvl(pd_qty,0) and nvl(pd_yqty,0)>0");
		String status = "PART2IN";
		if (aud == tal) {
			status = "";
		} else if (turn == tal) {
			status = "TURNIN";
		}
		updateByCondition("DailyPlan",
				"pu_acceptstatuscode='" + status + "',pu_acceptstatus='"
						+ BaseUtil.getLocalMessage(status) + "'",
				"pu_id=" + pu_id);
		return no;
	}

	public JSONObject newProdIO(String curr, String vecode, String piclass,
			Employee employee, String language, String caller,
			String currentyearmonth) {
		Timestamp time = Timestamp.valueOf(DateUtil
				.currentDateString(Constant.YMD_HMS));
		Object[] objs = getFieldsDataByCondition("Vendor", new String[] {
				"ve_name", "ve_id", "ve_payment", "ve_apvendcode",
				"ve_apvendname" }, "ve_code='" + vecode + "'");
		Object rate = null;
		rate = getFieldDataByCondition("CURRENCYSMONTH", "CM_CRRATE",
				"CM_CRNAME='" + curr + "' and CM_YEARMONTH='"
						+ currentyearmonth + "'");
		rate = rate != null ? rate : getFieldDataByCondition("Currencys",
				"cr_rate", "cr_name='" + curr + "'");
		int id = getSeqId("PRODINOUT_SEQ");
		String no = sGetMaxNumber(caller, 2);
		execute(INSERT_PRODIO_VEND,
				new Object[] { id, no, time, piclass,
						BaseUtil.getLocalMessage("ENTERING"),
						"ENTERING", employee.getEm_name(), time, vecode,
						objs[0], objs[1],
						BaseUtil.getLocalMessage("UNPOST"), "UNPOST",
						time, employee.getEm_name(),
						BaseUtil.getLocalMessage("UNPRINT"),
						"UNPRINT", curr, objs[2], rate, objs[3], objs[4] });
		JSONObject j = new JSONObject();
		j.put("pi_id", id);
		j.put("pi_inoutno", no);
		return j;
	}

	/*
	 * 采购单转收料通知单
	 */
	@Override
	public Object[] turnAcc(String language, String vcode, String curr,
			String apCode, Employee employee, String caller) {
		String code = sGetMaxNumber("AcceptNotify", 2);
		int id = getSeqId("AcceptNotify_SEQ");
		Object rate = getFieldDataByCondition("Currencys", "cr_rate",
				"cr_name='" + curr + "'");
		Object[] objs = getFieldsDataByCondition("Vendor", new String[] {
				"ve_name", "ve_buyerid", "ve_buyername", "ve_paymentcode",
				"ve_payment" }, "ve_code='" + vcode + "'");
		Object apname = getFieldDataByCondition("Vendor", "ve_name",
				"ve_code='" + apCode + "'");
		execute(INSERTACCNOTIFY, new Object[] { id, code, 0, null, vcode,
				objs[0], apCode, apname, curr, rate, objs[1], objs[2], objs[3],
				objs[4], null, BaseUtil.getLocalMessage("ENTERING"),
				employee.getEm_name(), employee.getEm_id() });
		return new Object[] { code, id };
	}

	/**
	 * 采购单转收料通知单明细
	 * 
	 * @author madan 2013-4-11 20:57:33
	 */
	@Override
	public void turnAccdetail(String no, int pdid, int anid, int detno,
			Double qty, Employee employee, String language) {
		SqlRowList rs = queryForRowSet(TURNANDETAIL, new Object[] { pdid });
		if (rs.next()) {
			double price = rs.getDouble("pd_price");
			double rate = rs.getDouble("pd_rate");
			double total = NumberUtil.formatDouble(qty * price, 2);
			double taxtotal = NumberUtil.formatDouble(
					total * rate / (100 + rate), 2);
			execute(INSERTACCNOTIFYDETAIL,
					new Object[] { getSeqId("AcceptNotifydetail_SEQ"), anid,
							detno, pdid, rs.getObject("pu_code"),
							rs.getObject("pd_detno"),
							rs.getObject("pd_prodcode"), qty, price, rate,
							rs.getObject("and_netprice"),
							rs.getObject("pd_beipin"), total, taxtotal,
							rs.getObject("pd_sellercode"),
							rs.getObject("pd_seller"),
							rs.getObject("pd_remark") });
			// 修改状态
			updateByCondition("DailyPlanDetail",
					"pd_status='PART2SN',pd_yqty=nvl(pd_yqty,0)+" + qty,
					"pd_id=" + pdid);
			updateByCondition("DailyPlanDetail", "pd_status='TURNSN'", "pd_id="
					+ pdid
					+ " AND nvl(pd_yqty,0)=nvl(pd_qty,0) and nvl(pd_yqty,0)>0");
			Integer pu_id = getJdbcTemplate().queryForObject(
					"select nvl(pd_puid,0) from DailyPlanDetail where pd_id=?",
					Integer.class, pdid);
			int tal = getCountByCondition("DailyPlanDetail", "pd_puid=" + pu_id);
			int aud = getCountByCondition("DailyPlanDetail", "pd_puid=" + pu_id
					+ " AND nvl(pd_yqty,0)=0");
			int turn = getCountByCondition("DailyPlanDetail", "pd_puid=" + pu_id
					+ " AND nvl(pd_yqty,0)=nvl(pd_qty,0) and nvl(pd_yqty,0)>0");
			String status = "PART2SN";
			if (aud == tal) {
				status = "";
			} else if (turn == tal) {
				status = "TURNSN";
			}
			updateByCondition("DailyPlan",
					"pu_turnstatuscode='" + status + "',pu_turnstatus='"
							+ BaseUtil.getLocalMessage(status) + "'",
					"pu_id=" + pu_id);
		}
	}

	/**
	 * 到物料核价单取采购单价
	 * 
	 * @return {JSONObject} {pd_price: 0.00,pd_rate: 0.00}
	 */
	@Override
	public JSONObject getDailyPlanPrice(String vendcode, String prodcode,
			String currency, String kind, double qty) {
		/* 取setting表查看取价取供应商原则，需不需要认定 */ 
		String sqlstr = null;
		if (isDBSetting("Application!ToPurchase!Deal", "onlyQualifiedPrice")) {
			sqlstr = DailyPlan_PRICE_APPSTATUS;
		} else {
			sqlstr = DailyPlan_PRICE;
		}
		SqlRowList rs = queryForRowSet(sqlstr, vendcode, prodcode, currency,
				kind, qty);
		if (rs.next()) {
			JSONObject obj = new JSONObject();
			obj.put("pd_price",
					rs.getObject("ppd_price") == null ? 0 : rs
							.getObject("ppd_price"));
			obj.put("pd_rate",
					rs.getObject("ppd_rate") == null ? 0 : rs
							.getObject("ppd_rate"));
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
			sqlstr = PRICE_VENDOR_APPSTATUS2;
		} else {
			sqlstr = PRICE_VENDOR2;
		}
		SqlRowList rs = queryForRowSet(sqlstr, prodcode, kind, qty);
		if (rs.next()) {
			JSONObject obj = new JSONObject();
			obj.put("price",
					rs.getObject("ppd_price") == null ? 0 : rs
							.getObject("ppd_price"));
			obj.put("vendcode",
					rs.getObject("ppd_vendcode") == null ? 0 : rs
							.getObject("ppd_vendcode"));
			obj.put("currency",
					rs.getObject("ppd_currency") == null ? "" : rs
							.getObject("ppd_currency"));
			obj.put("taxrate",
					rs.getObject("ppd_rate") == null ? 0 : rs
							.getObject("ppd_rate"));
			obj.put("crrate",
					rs.getObject("cr_rate") == null ? 1 : rs
							.getObject("cr_rate"));
			return obj;
		}
		return null;
	}

	@Override
	public void getPrice(int pu_id) {
		StringBuffer error = new StringBuffer();
		List<Object[]> objects = getFieldsDatasByCondition(
				"DailyPlandetail left join DailyPlan on pu_id=pd_puid",
				new String[] { "pd_prodcode", "pu_vendcode", "pu_currency",
						"pd_qty", "pd_id" }, " pd_puid=" + pu_id);
		JSONObject js = null;
		for (Object[] obj : objects) {
			Object oqty = getFieldDataByCondition("DailyPlanDetail",
					"sum(pd_qty)", " pd_puid=" + pu_id + " and pd_prodcode='"
							+ String.valueOf(obj[0]) + "'");
			js = getDailyPlanPrice(String.valueOf(obj[1]),
					String.valueOf(obj[0]), String.valueOf(obj[2]), "采购",
					Double.parseDouble(oqty.toString()));
			double price = 0;
			double tax = 0;
			double qty = Double.parseDouble(obj[3].toString());
			double p = 0;
			double total = 0;
			double nettotal = 0;
			double netprice = 0;
			if (js != null) {
				price = js.getDouble("pd_price");
				tax = js.getDouble("pd_rate");
			}
			if (price != 0) {
				p = NumberUtil.formatDouble(price, 6);
				total = NumberUtil.formatDouble(qty * p, 2);
				nettotal = NumberUtil.formatDouble(qty * p / (1 + tax / 100), 2);
				netprice = NumberUtil.formatDouble(p / (1 + tax / 100), 6);
			} else {
				error.append("根据 物料编号:[" + obj[0] + "],供应商号:[" + obj[1]
						+ "],币别:[" + obj[2] + "] 在物料核价单未找到对应单价，或单价为空值、0等!<BR/>");
			}
			updateByCondition("DailyPlanDetail", "pd_price=" + p + ",pd_rate="
					+ tax + ",pd_total=" + total + ",pd_taxtotal=" + nettotal
					+ ",pd_netprice=" + netprice, "pd_id=" + obj[4]);
		}
		// 主表金额
		execute(PU_TOTAL, pu_id);
		execute(PU_TAXTOTAL, pu_id);
		Object total = getFieldDataByCondition("DailyPlan", "pu_total", "pu_id="
				+ pu_id);
		if (total != null) {
			execute("update DailyPlan set pu_totalupper='"
					+ MoneyUtil.toChinese(total.toString()) + "' WHERE pu_id="
					+ pu_id);
		}
		if (error.length() > 0) {
			BaseUtil.showErrorOnSuccess(error.toString());
		}
	}

	@Override
	public void getPrice(String pu_code) {
		Object id = getFieldDataByCondition("DailyPlan", "pu_id", "pu_code='"
				+ pu_code + "'");
		if (id != null) {
			getPrice(Integer.parseInt(id.toString()));
		}
	}

	static final String CHECK_YQTY = "SELECT pd_code,pd_detno,pd_qty FROM DailyPlanDetail WHERE pd_id=? and pd_qty<?";

	/**
	 * 采购转入收料之前， 1.判断采购单状态 2.判断thisqty ≤ qty - yqty
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
			pus = getFieldsDataByCondition(
					"DailyPlanDetail left join DailyPlan on pd_puid=pu_id",
					"pu_code,pd_detno", "pd_id=" + id);
			if (pus != null) {
				bool = checkIf("DailyPlan", "pu_code='" + pus[0]
						+ "' and pu_statuscode='AUDITED'");
				if (!bool) {
					BaseUtil.showError("采购单:" + pus[0] + " 未审核通过,无法转收料单!");
				}
				y = getFieldDataByCondition("VerifyApplyDetail",
						"sum(nvl(vad_qty,0))", "vad_pucode='" + pus[0]
								+ "' and vad_pudetno=" + pus[1]);
				y = y == null ? 0 : y;
				r = getFieldDataByCondition("ProdIODetail",
						"sum(nvl(pd_outqty,0))", "pd_ordercode='" + pus[0]
								+ "' and pd_orderdetno=" + pus[1]
								+ " AND pd_piclass='采购验退单'");
				o = getFieldDataByCondition("ProdIODetail",
						"sum(nvl(pd_outqty,0))", "pd_ordercode='" + pus[0]
								+ "' and pd_orderdetno=" + pus[1]
								+ " AND pd_piclass='不良品出库单'");
				r = r == null ? 0 : r;
				o = o == null ? 0 : o;
				rs = queryForRowSet(
						CHECK_YQTY,
						id,
						Double.parseDouble(y.toString())
								- Double.parseDouble(r.toString())
								- Double.parseDouble(o.toString())
								+ Double.parseDouble(d.get("pd_tqty")
										.toString()));
				if (rs.next()) {
					StringBuffer sb = new StringBuffer("本次数量填写超出可转数量,采购单号:")
							.append(rs.getGeneralString("pd_code")).append(",行号:")
							.append(rs.getInt("pd_detno")).append(",采购数:")
							.append(rs.getDouble("pd_qty")).append(",已转收料单数:")
							.append(y).append(",已验退数:").append(r)
							.append(",不良品出库数:").append(o).append(",本次数:")
							.append(d.get("pd_tqty"));
					BaseUtil.showError(sb.toString());
				}
			}
		}
	}

	@Override
	public void getPutype(int pu_id) {
		StringBuffer error = new StringBuffer();
		List<Object[]> objects = getFieldsDatasByCondition(
				"DailyPlandetail left join DailyPlan on pu_id=pd_puid",
				new String[] { "pd_prodcode", "pu_vendcode", "pu_currency",
						"pd_qty", "pd_id", "pd_detno" }, " pd_puid=" + pu_id);
		JSONObject js = null;
		for (Object[] obj : objects) {
			Object oqty = getFieldDataByCondition("DailyPlanDetail",
					"sum(pd_qty)", " pd_puid=" + pu_id + " and pd_prodcode='"
							+ String.valueOf(obj[0]) + "'");
			js = getPriceVendor(String.valueOf(obj[0]), "采购",
					Double.parseDouble(oqty.toString()));
			double price = 0;
			String tax = null;
			if (js != null) {
				price = js.getDouble("price");
				tax = js.get("vendcode").toString();
				if (!tax.equals(obj[1].toString())) {
					price = NumberUtil.formatDouble(price, 6);
					error.append("序号:[" + obj[5] + "]的物料有最低有效供应商:[" + tax
							+ "],最低价格为[" + price + "](折合RMB的价格)或选用非标准订单类型<BR/>");
				} else {
					getPrice(pu_id);
				}
			} else {
				error.append("序号:[" + obj[5] + "]的物料没有定价有效,请走非标准采购订单流程！");
			}

		}
		if (error.length() > 0) {
			BaseUtil.showErrorOnSuccess(error.toString());
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
			ConditionStr = " pd_id in (select pd_id from DailyPlan,DailyPlandetail where pd_puid=pu_id and pu_statuscode='AUDITED' and pd_qty-NVL(pd_acceptqty,0)>0 and NVL(pd_mrpstatuscode,' ')<>'FINISH')  ";
		} else if (pdidstr != null && !pdidstr.equals("")) {
			ConditionStr = " pd_id in (" + pdidstr + ")";
		} else {
			return;
		}
		Sqlstr = "merge into DailyPlandetail USING(select * from (select pd_id,pd_yqty,pd_acceptqty,pd_backqty,pd_rejectqty,NVL(sum(vad_qty),0)vadqty from DailyPlan left join DailyPlandetail on pu_id=pd_puid left join (verifyapplydetail inner join verifyapply on va_id=vad_vaid and va_class='采购收料单' )on pd_code=vad_pucode and pd_detno=vad_pudetno where pu_statuscode='AUDITED' and pd_qty-NVL(pd_acceptqty,0)>0 group by pd_id,pd_yqty,pd_acceptqty,pd_backqty,pd_rejectqty) where NVL(pd_yqty,0)<>vadqty-NVL(pd_backqty,0)+NVL(pd_rejectqty,0))src on (src.pd_id=DailyPlandetail.pd_id) when matched then update set pd_yqty=(case when pd_acceptqty>=vadqty-NVL(pd_backqty,0)+NVL(pd_rejectqty,0) then pd_acceptqty else vadqty-NVL(pd_backqty,0)+NVL(pd_rejectqty,0) end) where "
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
			ConditionStr = " pd_id in (select pd_id from DailyPlan,DailyPlandetail where pd_puid=pu_id and pu_statuscode='AUDITED' and pd_qty-NVL(pd_acceptqty,0)>0 and NVL(pd_mrpstatuscode,' ')<>'FINISH')  ";
			Sqlstr = "update DailyPlandetail set pd_turnqty=0 where "
					+ ConditionStr;
			execute(Sqlstr);
			Sqlstr = "merge into DailyPlandetail USING(select pn_pdid,SUM(pn_qty - NVL(pn_endqty, 0)) as qty FROM DailyPlannotify WHERE  DailyPlanNOTIFY.pn_status <> '已取消'"
					+ " AND DailyPlanNOTIFY.pn_status <> '已发货'  AND   pn_qty>NVL(pn_endqty,0) group by pn_pdid )src on(src.pn_pdid=pd_id)"
					+ " WHEN MATCHED THEN UPDATE SET pd_turnqty=NVL(pd_turnqty,0)+nvl(src.qty,0) where　"
					+ ConditionStr;
			execute(Sqlstr);
			Sqlstr = " merge into DailyPlandetail USING(select pd_id,SUM(and_inqty-NVL(and_yqty,0)) as qty FROM acceptnotify,acceptnotifydetail,DailyPlandetail"
					+ " WHERE  an_id = and_anid  AND an_status <> '已转收料' AND an_status <> '已删除' and and_inqty-NVL(and_yqty,0)>0 and pd_code=and_ordercode and pd_detno=and_orderdetno group by pd_id)src on(src.pd_id=DailyPlandetail.pd_id)"
					+ " WHEN MATCHED THEN UPDATE SET pd_turnqty=NVL(pd_turnqty,0)+nvl(src.qty,0)   where　"
					+ ConditionStr;
			execute(Sqlstr);
		} else if (pdidstr != null && !pdidstr.equals("")) {
			ConditionStr = " pd_id in (" + pdidstr + ")";
			Sqlstr = "update DailyPlandetail set pd_turnqty=0 where "
					+ ConditionStr;
			execute(Sqlstr);
			Sqlstr = "merge into DailyPlandetail USING(select pn_pdid,SUM(pn_qty - NVL(pn_endqty, 0)) as qty FROM DailyPlannotify WHERE pn_pdid in ("
					+ pdidstr
					+ ") and DailyPlanNOTIFY.pn_status <> '已取消'"
					+ " AND DailyPlanNOTIFY.pn_status <> '已发货'  AND    pn_qty>NVL(pn_endqty,0) group by pn_pdid )src on(src.pn_pdid=pd_id)"
					+ " WHEN MATCHED THEN UPDATE SET pd_turnqty=NVL(pd_turnqty,0)+nvl(src.qty,0) where　"
					+ ConditionStr;
			execute(Sqlstr);
			Sqlstr = " merge into DailyPlandetail USING(select pd_id,SUM(and_inqty-NVL(and_yqty,0)) as qty FROM acceptnotify,acceptnotifydetail,DailyPlandetail"
					+ " WHERE pn_pdid in ("
					+ pdidstr
					+ ") and an_id = and_anid  AND an_status <> '已转收料' AND an_status <> '已删除' and and_inqty-NVL(and_yqty,0)>0 and pd_code=and_ordercode and pd_detno=and_orderdetno group by pd_id)src on(src.pd_id=DailyPlandetail.pd_id)"
					+ " WHEN MATCHED THEN UPDATE SET pd_turnqty=NVL(pd_turnqty,0)+nvl(src.qty,0) where　"
					+ ConditionStr;
			execute(Sqlstr);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uas.erp.dao.common.DailyPlanDao#testPostToSqlServer(int)
	 */
	@Override
	public void syncPurcToSqlServer(int pu_id) {
		String hk = "malata_hk";
		SqlRowList rs = queryForRowSet(
				"select * from DailyPlan left join vendor on pu_vendcode=ve_code where pu_id=?",
				pu_id);
		if (rs.next()) {
			SqlRowList rd = queryForRowSet(
					"select * from DailyPlandetail left join product on pd_prodcode=pr_code where pd_puid=? order by pd_detno",
					pu_id);
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
					sqls[i++] = "insert into DailyPlandetail(pd_code,pd_detno,pd_prodcode,pd_price,pd_qty,pd_netprice,pd_total,pd_delivery,pd_remark)values('"
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
							+ rd.getGeneralTimestamp("pd_delivery")
							+ "',20),'"
							+ rd.getGeneralString("pd_apremark") + "')";
				}
				double rate = rs.getGeneralDouble("pu_rate");
				if ("USD".equals(rs.getGeneralString("pu_currency"))) {
					rate = 7.78;
				} else if ("HKD".equals(rs.getGeneralString("pu_currency"))) {
					rate = 1;
				}
				sqls[i++] = "insert into sale(sa_code,sa_relativecode,sa_date,sa_currency,sa_rate,sa_status,sa_recordman,sa_custcode,sa_custname,sa_cop,sa_recorddate,sa_sellercode,sa_seller,sa_payments,sa_toplace,sa_remark,sa_purcstatus) values('"
						+ rs.getGeneralString("pu_code")
						+ "','"
						+ rs.getGeneralString("pu_code")
						+ "',getdate(),'USD',7.78,'已审核','"
						+ SystemSession.getUser().getEm_name()
						+ "','WLDKJ','南靖万利达科技有限公司','香港万利达',getdate(),'wmc8321','王满春','AMS 30天 T/T','福建省南靖靖城萬利達工業園','系统同步产生!"
						+ rs.getGeneralString("pu_remark") + "','已下达计划')";
				sqls[i++] = "insert into DailyPlan(pu_code,pu_relativecode,pu_date,pu_currency,pu_rate,pu_status,pu_recordman,pu_vendcode,pu_vendname,pu_cop,pu_indate,pu_payments,pu_shipaddresscode,pu_buyer,pu_buyername,pu_remark,pu_updatedate,pu_vendoruu) values('"
						+ rs.getGeneralString("pu_code")
						+ "','"
						+ rs.getGeneralString("pu_code")
						+ "',getdate(),'"
						+ rs.getGeneralString("pu_currency")
						+ "',"
						+ rate
						+ ",'已审核','"
						+ SystemSession.getUser().getEm_name()
						+ "','"
						+ rs.getGeneralString("pu_vendcode")
						+ "','"
						+ rs.getGeneralString("pu_vendname")
						+ "','香港万利达',getdate(),'"
						+ rs.getGeneralString("pu_payments")
						+ "','香港北角渣華道321號柯達大廈第二期912室','"
						+ rs.getGeneralString("pu_buyercode")
						+ "','"
						+ rs.getGeneralString("pu_buyername")
						+ "','"
						+ rs.getGeneralString("pu_remark")
						+ "','"
						+ DateUtil.currentDateString(null)
						+ "','"
						+ rs.getGeneralString("pu_vendoruu") + "')";
				getJdbcTemplate().batchUpdate(sqls);
				syncVendor(rs.getCurrentMap());
				syncProduct(rd.getResultList());
				SpObserver.putSp(sob);
				execute("update DailyPlan set pu_sync='已同步' where pu_id=?", pu_id);
			}
		}
	}

	/**
	 * 检查HK帐套是否已经存在关联的单据
	 */
	private String checkExist(String code) {
		Object existCode = getFieldDataByCondition("sale", "sa_code",
				"sa_relativecode='" + code + "'");
		if (existCode != null) {
			return "当前采购单在HK帐套已经有关联的销售合同:" + existCode;
		} else {
			boolean exist = checkIf("DailyPlan", "pu_code='" + code + "'");
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
				"select pd_prodcode from DailyPlandetail where pd_code=? and  not exists (select pr_code from product where pr_code=pd_prodcode)",
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
				sql.set("pr_status",
						BaseUtil.getLocalMessage("AUDITED"));
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
		boolean needSync = checkByCondition("Vendor",
				"ve_code='" + map.get("VE_CODE") + "'");
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
			sql.set("ve_auditstatus",
					BaseUtil.getLocalMessage("AUDITED"));
			sql.set("ve_initdate", "getdate()");
			sql.set("ve_recorder", SystemSession.getUser().getEm_name());
			sql.set("ve_bank", map.get("VE_BANK"));
			sql.set("ve_bankaccount", map.get("VE_BANKACCOUNT"));
			sql.set("ve_currency", map.get("VE_CURRENCY"));
			sql.execute();
			execute("update vendor set ve_buyer=us_code from users where ve_buyername=us_name and ve_code=?",
					map.get("VE_CODE"));
		}
	}

	@Override
	public void resetPurcSyncStatus(int pu_id) {
		String hk = "malata_hk";
		SqlRowList rs = queryForRowSet(
				"select pu_code from DailyPlan where pu_id=?",
				pu_id);
		if (rs.next()) {
			String sob = SpObserver.getSp();
			SpObserver.putSp(hk);
			if (!hk.equals(SpObserver.getSp())) {
				BaseUtil.showError("无法连接到香港帐套,执行失败.");
			}
			boolean exist = checkIf("sale",
					"sa_relativecode='" + rs.getString(1) + "'");
			SpObserver.putSp(sob);
			execute("update DailyPlan set pu_sync=" + (exist ? "'已同步'" : "null") + " where pu_id=?", pu_id);
		}
	}

}
