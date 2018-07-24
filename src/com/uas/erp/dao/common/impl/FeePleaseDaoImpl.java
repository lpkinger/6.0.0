package com.uas.erp.dao.common.impl;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.FeePleaseDao;
import com.uas.erp.model.Employee;

@Repository
public class FeePleaseDaoImpl extends BaseDao implements FeePleaseDao {
	static final String TURNFEEPLEASE = "SELECT * FROM FeePlease WHERE fp_id=?";
	static final String TURNCZCFSQDETAIL = "SELECT * FROM FeePleaseDetail WHERE fpd_fpid=?";
	static final String INSERTFYBX = "INSERT INTO FeePlease(fp_id,fp_code,fp_sourcecode,fp_sourcekind,fp_pleaseman,fp_department,fp_pleaseamount,fp_item,"
			+ "fp_systerm,fp_status,fp_statuscode,fp_recordman,fp_kind,fp_recorddate) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,'费用报销单',sysdate)";
	static final String INSERTYHFKSQ = "INSERT INTO FeePlease(fp_id,fp_code,fp_sourcecode,fp_sourcekind,fp_pleaseman,fp_department,fp_pleaseamount,fp_item,"
			+ "fp_systerm,fp_status,fp_statuscode,fp_recordman,fp_kind,fp_recorddate) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,'银行付款申请单',sysdate)";
	static final String INSERTYWZDBX = "INSERT INTO FeePlease(fp_id,fp_code,fp_sourcecode,fp_sourcekind,fp_pleaseman,fp_department,fp_pleaseamount,fp_item,"
			+ "fp_systerm,fp_status,fp_statuscode,fp_recordman,fp_kind,fp_recorddate,fp_v1,fp_v2,fp_v3,fp_v4,fp_v5,fp_v8,fp_v9)"
			+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,'业务招待费报销单',sysdate,?,?,?,?,?,?,?)";

	@Override
	public JSONObject turnCLFBX(int id, String caller, Object sob) {
		String prefix = (sob == null || "".equals(sob)) ? "" : (sob + "."); 
		String sobName = "";
		String localsob = SpObserver.getSp();
		if (sob != null) {
			Object obj = getFieldDataByCondition("master", "ma_function", "ma_user='"+localsob+"'");
			sobName = obj == null ? "" : ("(" + obj.toString() + ")");
		}
		int fpid = getSeqId(prefix + "FEEPLEASE_SEQ");
		String fp_code = sGetMaxNumber(prefix + "FeePlease!FYBX", 2);// 统一用FeePlease!FYBX取编号
		//转单时的报销人编号 fp_pleasemancode
		Object em_code = getFieldDataByCondition(prefix + "employee", "nvl(em_code,'') ", "em_name=(select fp_recordman from FeePlease where fp_id=" + id+")");
		em_code = em_code ==null ? "" : em_code;
		String insertSql = "insert into "+prefix+"FeePlease (fp_sourcecode,fp_sourcekind,fp_id,fp_code,fp_kind,fp_recorddate,fp_billdate,"
				+ "fp_recordman,fp_statuscode,fp_status,fp_printstatuscode,fp_printstatus,"
				+ "fp_department,fp_startdate,fp_enddate,fp_remark,fp_pleaseman,fp_prjcode,fp_prjname,fp_pleasemancode) select fp_code||'"+sobName+"','出差申请单'," + fpid + ",'" + fp_code + "','差旅费报销单',sysdate,sysdate,'"
				+ SystemSession.getUser().getEm_name() + "','ENTERING','" + BaseUtil.getLocalMessage("ENTERING") + "','UNPRINT','"
				+ BaseUtil.getLocalMessage("UNPRINT") + "',fp_department,fp_startdate,fp_enddate,fp_v3,fp_recordman,fp_prjcode,fp_prjname,'"+em_code+"' from FeePlease where fp_id=" + id;
		execute(insertSql);
		JSONObject j = new JSONObject();
		j.put("fp_id", fpid);
		j.put("fp_code", fp_code);
		return j;
	}

	@Override
	public int turnFYBX(int id, String caller) {
		try {
			SqlRowList rs = queryForRowSet(TURNFEEPLEASE, new Object[] { id });
			int fpid = 0;
			if (rs.next()) {
				fpid = getSeqId("FEEPLEASE_SEQ");
				String code = sGetMaxNumber(caller, 2);
				execute(INSERTFYBX,
						new Object[] { fpid, code, rs.getString("fp_code"), rs.getObject("fp_kind"), rs.getObject("fp_pleaseman"),
								rs.getObject("fp_department"), rs.getObject("fp_pleaseamount"), rs.getObject("fp_item"),
								rs.getObject("fp_systerm"), BaseUtil.getLocalMessage("ENTERING"), "ENTERING",
								SystemSession.getUser().getEm_name() });
			}
			return fpid;
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
			return 0;
		}
	}

	@Override
	public int turnYHFKSQ(int id, String caller) {
		try {
			SqlRowList rs = queryForRowSet(TURNFEEPLEASE, new Object[] { id });
			int fpid = 0;
			if (rs.next()) {
				fpid = getSeqId("FEEPLEASE_SEQ");
				String code = sGetMaxNumber(caller, 2);
				execute(INSERTYHFKSQ,
						new Object[] { fpid, code, rs.getString("fp_code"), rs.getObject("fp_kind"), rs.getObject("fp_pleaseman"),
								rs.getObject("fp_department"), rs.getObject("fp_pleaseamount"), rs.getObject("fp_item"),
								rs.getObject("fp_systerm"), BaseUtil.getLocalMessage("ENTERING"), "ENTERING",
								SystemSession.getUser().getEm_name() });
			}
			return fpid;
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
			return 0;
		}
	}

	@Override
	public int turnYWZDBX(int id, String caller) {
		try {
			SqlRowList rs = queryForRowSet(TURNFEEPLEASE, new Object[] { id });
			int fpid = 0;
			if (rs.next()) {
				Employee employee = SystemSession.getUser();
				fpid = getSeqId("FEEPLEASE_SEQ");
				String code = sGetMaxNumber(caller, 2);
				execute(INSERTYWZDBX,
						new Object[] { fpid, code, rs.getString("fp_code"), rs.getObject("fp_kind"), rs.getObject("fp_pleaseman"),
								rs.getObject("fp_department"), rs.getObject("fp_pleaseamount"), rs.getObject("fp_item"),
								rs.getObject("fp_systerm"), BaseUtil.getLocalMessage("ENTERING"), "ENTERING", employee.getEm_name(),
								rs.getObject("fp_v1"), rs.getObject("fp_v2"), rs.getObject("fp_v3"), rs.getObject("fp_v4"),
								rs.getObject("fp_v5"), rs.getObject("fp_v8"), rs.getObject("fp_v9") });
			}
			return fpid;
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
			return 0;
		}
	}

	@Override
	public int jksqturnFYBX(int id, String caller) {
		try {
			SqlRowList rs = queryForRowSet(TURNFEEPLEASE, new Object[] { id });
			int fpid = getSeqId("FEEPLEASE_SEQ");
			String code = sGetMaxNumber(caller, 2);
			if (rs.next()) {
				Map<String, Object> diffence = new HashMap<String, Object>();
				diffence.put("fp_sourcecode", "'" + rs.getObject("fp_code") + "'");
				diffence.put("fp_id", fpid);
				diffence.put("fp_code", "'" + code + "'");
				diffence.put("fp_kind", "'费用报销单'");
				diffence.put("fp_recorddate", "sysdate");
				diffence.put("fp_recordman", "'" + SystemSession.getUser().getEm_name() + "'");
				diffence.put("fp_printstatuscode", "'UNPRINT'");
				diffence.put("fp_printstatus", "'" + BaseUtil.getLocalMessage("UNPRINT") + "'");
				diffence.put("fp_statuscode", "'ENTERING'");
				diffence.put("fp_status", "'" + BaseUtil.getLocalMessage("ENTERING") + "'");
				diffence.put("fp_v2", null);
				// 转入主表
				copyRecord("FeePlease", "FeePlease", "fp_id=" + id, diffence);
				// 转入从表
				rs = queryForRowSet("SELECT fpd_id FROM FeePleaseDetail WHERE fpd_fpid=? order by fpd_detno", id);
				diffence = new HashMap<String, Object>();
				diffence.put("fpd_fpid", fpid);
				diffence.put("fpd_code", "'" + code + "'");
				diffence.put("fpd_class", "'费用报销单'");
				while (rs.next()) {
					diffence.put("fpd_id", getSeqId("FEEPLEASEDETAIL_SEQ"));
					copyRecord("FeePleaseDetail", "FeePleaseDetail", "fpd_id=" + rs.getInt("fpd_id"), diffence);
				}
			}
			return fpid;
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
			return 0;
		}
	}

	static final String INSERTBILLAP = "INSERT INTO BillAP(bap_id,bap_code,bap_date,bap_currency,bap_rate"
			+ ",bap_balance,bap_vendcode,bap_vendname,bap_remark,bap_status,bap_statuscode,bap_operator,bap_recorder"
			+ ",bap_indate,bap_getstatus,bap_sendstatus,bap_doublebalance,bap_topaybalance,bap_cmcurrency,bap_source,"
			+ "bap_billkind,bap_sourceid,bap_settleamount,bap_leftamount,bap_cmrate,bap_othercatecode,bap_nowstatus,bap_sourcetype)"
			+ " values(?,?,sysdate,?,?,?,?,?,?,?,?,?,?,sysdate,?,?,?,?,?,?,?,?,?,?,1,?,?,?)";

	@Override
	public JSONObject turnBillAP(int id, double thisamount, String caller) {
		SqlRowList rs = queryForRowSet("select * from FeePlease where fp_id=?", id);
		if (rs.next()) {
			String paymentcode = rs.getGeneralString("fp_v11");
			String currency = rs.getGeneralString("fp_v13");
			Object[] cate = getFieldsDataByCondition(
					"Category left join currencysmonth on ca_currency=cm_crname and cm_yearmonth=to_char(sysdate,'yyyymm')", new String[] {
							"ca_id", "ca_currency", "nvl(cm_crrate,0)" }, "ca_code='" + paymentcode + "'");
			Double fprate = 0.0;
			if (cate != null && cate[1] != null && !currency.equals(cate[1])) {
				fprate = getFieldValue("Currencysmonth", "nvl(cm_crrate,0)", "cm_crname='" + currency
						+ "' and cm_yearmonth=to_char(sysdate, 'yyyymm')", Double.class);
				// 月度汇率为空则提示
				if (fprate == null) {
					BaseUtil.showError("月度汇率未设置，请先设置!");
				}
			}
			Employee employee = SystemSession.getUser();
			int bapid = getSeqId("BILLAP_SEQ");
			String bapcode = sGetMaxNumber("BillAP", 2);
			boolean bool = execute(
					INSERTBILLAP,
					new Object[] { bapid, bapcode, currency, fprate, rs.getObject("fp_pleaseamount"), rs.getObject("fp_cucode"),
							rs.getObject("fp_cuname"), rs.getObject("fp_v3"), BaseUtil.getLocalMessage("ENTERING"), "ENTERING",
							employee.getEm_name(), employee.getEm_name(), "未领取", "未寄出", thisamount, thisamount, currency,
							rs.getObject("fp_code"), "其他付款", id, 0, thisamount, paymentcode, "未付款", "总务申请单" });
			if (bool) {
				execute("update FeePlease set fp_n1=nvl(fp_n1,0)+" + thisamount + " where fp_id=" + id);
				execute("update FeePlease set fp_v7=case when fp_pleaseamount=fp_n1 then '已支付' else '部分支付' end where fp_id=" + id);
				JSONObject j = new JSONObject();
				j.put("bap_id", bapid);
				j.put("bap_code", bapcode);
				return j;
			}
		}
		return null;
	}

	static final String INSERTBILLARCHANGE = "INSERT INTO BILLARCHANGE(brc_id,brc_code,brc_date,brc_kind"
			+ ",brc_status,brc_amount,brc_vendcode,brc_vendname,brc_currency,brc_rate,brc_cmcurrency"
			+ ",brc_cmrate,brc_cmamount,brc_recorder,brc_indate,brc_statuscode,brc_source,brc_sourceid"
			+ ",brc_explain,brc_sourcetype,brc_catecode,brc_catename) "
			+ "values (?,?,sysdate,'其他收款',?,?,?,?,?,?,?,?,?,?,sysdate,'ENTERING',?,?,?,?,?,?)";

	@Override
	public JSONObject turnBillARChange(int id, double thisamount, String caller) {
		SqlRowList rs = queryForRowSet("select * from FeePlease where fp_id=?", id);
		if (rs.next()) {
			String paymentcode = rs.getGeneralString("fp_v11");
			String currency = rs.getGeneralString("fp_v13");
			Object[] cate = getFieldsDataByCondition(
					"Category left join currencysmonth on ca_currency=cm_crname and cm_yearmonth=to_char(sysdate,'yyyymm')", new String[] {
							"ca_id", "ca_currency", "nvl(cm_crrate,0)", "ca_name" }, "ca_code='" + paymentcode + "'");
			Double fprate = 0.0;
			if (cate != null && cate[1] != null && !currency.equals(cate[1])) {
				fprate = getFieldValue("Currencysmonth", "nvl(cm_crrate,0)", "cm_crname='" + currency
						+ "' and cm_yearmonth=to_char(sysdate, 'yyyymm')", Double.class);
				// 月度汇率为空则提示
				if (fprate == null) {
					BaseUtil.showError("月度汇率未设置，请先设置!");
				}
			}
			Employee employee = SystemSession.getUser();
			int brcid = getSeqId("BILLARCHANGE_SEQ");
			String brccode = sGetMaxNumber("BillARChange", 2);
			boolean bool = execute(
					INSERTBILLARCHANGE,
					new Object[] { brcid, brccode, BaseUtil.getLocalMessage("ENTERING"), thisamount, rs.getObject("fp_cucode"),
							rs.getObject("fp_cuname"), currency, fprate, currency, 1, thisamount, employee.getEm_name(),
							rs.getObject("fp_code"), id, rs.getObject("fp_v3"), "总务申请单", paymentcode, cate[3] });
			if (bool) {
				execute("update FeePlease set fp_n1=nvl(fp_n1,0)+" + thisamount + " where fp_id=" + id);
				execute("update FeePlease set fp_v7=case when fp_pleaseamount=fp_n1 then '已支付' else '部分支付' end where fp_id=" + id);
				JSONObject j = new JSONObject();
				j.put("brc_id", brcid);
				j.put("brc_code", brccode);
				return j;
			}
		}
		return null;
	}
}