package com.uas.erp.dao.common.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.PurMouldDao;

@Repository
public class PurMouldDaoImpl extends BaseDao implements PurMouldDao {
	static final String PurMould = "SELECT * FROM PurMould WHERE pm_id=?";
	static final String PurMouldDetail = "SELECT * FROM PurMouldDetail WHERE pmd_pmid=?";
	static final String YSReport = "INSERT INTO MOD_YSREPORT(mo_id, mo_code, mo_wdate, mo_status, mo_statuscode, mo_source, mo_gys,mo_wp,mo_prjcode,mo_prjname)"
			+ " values (?,?,sysdate,?,'ENTERING',?,?,?,?,?)";
	static final String YSReportDetail = "INSERT INTO MOD_YSBGDETAIL (yd_id, yd_moid, yd_code,yd_detno,yd_mjcode, yd_mjhtcod,"
			+ "yd_mjhtdetno, yd_vendcode,yd_vendname,yd_mjhtid) values (?,?,?,?,?,?,?,?,?,?)";

	static final String INSERTMOULDFEEPLEASE = "insert into MOULDFEEPLEASE ("
			+ "mp_id,mp_code,mp_recorddate,mp_recordman,mp_vendcode,mp_vendname,mp_pleaseman,"
			+ "mp_status,mp_printstatus,mp_total,mp_source,mp_remark,mp_statuscode,"
			+ "mp_printstatuscode,mp_paymentscode,mp_payments, mp_sourceid, mp_sourcetype) values("
			+ "?,?,sysdate,?,?,?,?,?,?,?,?,?,'ENTERING','UNPRINT',?,?,?,?)";

	@Override
	public int turnYSReport(int id) {
		try {
			SqlRowList rs = queryForRowSet(PurMould, id);
			int mo_id = 0;
			if (rs.next()) {
				mo_id = getSeqId("MOD_YSREPORT_SEQ");
				String code = sGetMaxNumber("YSReport!Mould", 2);
				Object vendcode = rs.getObject("pm_vendcode");
				Object vendname = rs.getObject("pm_vendname");
				boolean bool = execute(YSReport, new Object[] { mo_id, code, BaseUtil.getLocalMessage("ENTERING"), rs.getObject("pm_code"),
						vendname, SystemSession.getUser().getEm_name(), rs.getObject("pm_prjcode"), rs.getObject("pm_prjname") });
				if (bool) {
					rs = queryForRowSet(PurMouldDetail, id);
					int count = 1;
					while (rs.next()) {
						execute(YSReportDetail,
								new Object[] { getSeqId("MOD_YSBGDETAIL_SEQ"), mo_id, code, count++, rs.getObject("pmd_pscode"),
										rs.getObject("pmd_code"), rs.getObject("pmd_detno"), vendcode, vendname, rs.getObject("pmd_id") });
					}
					execute("update MOD_YSREPORT set (mo_prjcode,mo_prjname)=(select ps_prjcode,ps_prjname from MOD_YSBGDETAIL left join ProductSet on yd_mjcode=ps_code "
							+ "where yd_moid=mo_id and yd_detno=1) where mo_id=" + mo_id);
				}
			}
			return mo_id;
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
			return 0;
		}
	}

	@Override
	public int turnFeePlease(int id, double aldamount) {
		int mpid = 0;
		SqlRowList rs = queryForRowSet("select * from PURMOULD where pm_id=?", new Object[] { id });
		if (rs.next()) {
			mpid = getSeqId("MOULDFEEPLEASE_SEQ");
			String code = sGetMaxNumber("FeePlease!Mould", 2);
			String sourcecode = rs.getString("pm_code");
			Object[] vend = getFieldsDataByCondition("Vendor", new String[] { "ve_code", "ve_name", "ve_paymentcode", "ve_payment" },
					"ve_code='" + rs.getObject("pm_vendcode") + "'");
			execute(INSERTMOULDFEEPLEASE, new Object[] { mpid, code, SystemSession.getUser().getEm_name(), vend[0], vend[1],
					SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("ENTERING"), BaseUtil.getLocalMessage("UNPRINT"),
					aldamount, sourcecode, "模具采购单:" + sourcecode, vend[2], vend[3], id, "模具采购单" });
		}
		return mpid;
	}

	/**
	 * 修改模具采购单状态
	 */
	@Override
	public void udpatestatus(int pdid) {
		Object pmid = getFieldDataByCondition("PurMouldDet", "pd_pmid", "pd_id=" + pdid);
		int total = getCountByCondition("PurMouldDet", "pd_pmid=" + pmid);
		int aud = getCountByCondition("PurMouldDet", "pd_pmid=" + pmid + " AND nvl(pd_yamount,0)=0");
		int turn = getCountByCondition("PurMouldDet", "pd_pmid=" + pmid + " AND nvl(pd_yamount,0)=nvl(pd_amount,0)");
		String status = "部分转付款申请";
		if (aud == total) {
			status = "";
		} else if (turn == total) {
			status = "已转付款申请";
		}
		updateByCondition("PurMould", "PM_PLEASESTATUS='" + status + "'", "pm_id=" + pmid);
	}

	static final String CHECK_YQTY = "SELECT pd_amount FROM PurMouldDet WHERE pd_id=? and nvl(pd_amount,0)<?";

	@Override
	public void checkPdYamount(List<Map<Object, Object>> datas) {
		int id = 0;
		Object y = 0;// 已转金额
		SqlRowList rs = null;
		Object[] pus = null;
		for (Map<Object, Object> d : datas) {
			id = Integer.parseInt(d.get("pd_id").toString());
			pus = getFieldsDataByCondition("PurMouldDet left join PurMould on pd_pmid=pm_id", "pm_code,pd_detno", "pd_id=" + id);
			if (pus != null) {
				y = getFieldDataByCondition("MOULDFEEPLEASEDETAIL", "sum(nvl(mfd_amount,0))", "mfd_purccode='" + pus[0]
						+ "' and mfd_pddetno=" + pus[1]);
				y = y == null ? 0 : y;
				rs = queryForRowSet(CHECK_YQTY, id,
						Double.parseDouble(y.toString()) + Double.parseDouble(d.get("pd_thisamount").toString()));
				if (rs.next()) {
					StringBuffer sb = new StringBuffer("本次金额填写超出可转金额,采购单号:").append(pus[0]).append(",行号:").append(pus[1]).append(",金额:")
							.append(rs.getDouble("pd_amount")).append(",已转金额:").append(y).append(",本次金额:").append(d.get("pd_thisamount"));
					BaseUtil.showError(sb.toString());
				}
			}
		}
	}
}
