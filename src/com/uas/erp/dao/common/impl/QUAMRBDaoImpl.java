package com.uas.erp.dao.common.impl;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.QUAMRBDao;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class QUAMRBDaoImpl extends BaseDao implements QUAMRBDao {
	final static String INSERT_BASEPRODINOUT = "INSERT INTO prodinout(pi_id, Fin_Code, pi_inoutno,pi_recordman,pi_operatorcode,pi_recorddate"
			+ ",pi_invostatus,pi_invostatuscode,pi_class,pi_statuscode,pi_status,pi_cardcode,pi_title,pi_receivecode,pi_receivename,pi_payment"
			+ ",pi_currency,pi_rate,pi_type,pi_sourcecode,pi_cgy,PI_COP,pi_sendcode) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static String QUA_PURC_GROUP = "select mr_code from qua_mrbdet"
			+ " left join qua_mrb on md_mrid=mr_id where md_id in(??) group by mr_code";
	final static String QUA_MAKE_GROUP = "select mr_code from qua_mrbdet left join qua_mrb on md_mrid=mr_id where md_id in(??) group by mr_code";
	final static String QUA_PURC_ARG = "select mr_pudetno,mr_pucode,mr_prodcode,mr_datein,mr_inqty,mr_veid,mr_vendcode,mr_vendname,mr_code,mr_batchcode"
			+ ",pd_id,pd_price,pd_rate,md_id,nvl(pu_receivecode,mr_vendcode) pu_receivecode,nvl(pu_receivename,mr_vendname) pu_receivename,pu_payments,"
			+ "pu_rate,pu_code,pu_currency,mr_id,md_detno,mr_whman,mr_cop,mr_shcode from qua_mrbdet left join"
			+ " qua_mrb on md_mrid=mr_id left join purchasedetail on mr_pucode=pd_code and mr_pudetno=pd_detno left join purchase"
			+ " on pd_puid=pu_id where mr_code=? and md_id in(??)";
	final static String QUA_MAKE_ARG = "select mr_pucode,mr_prodcode,mr_datein,mr_inqty,mr_veid,mr_vendcode,mr_vendname,mr_code,mr_batchcode"
			+ ",ma_id,ma_price,ma_taxrate,md_id,ma_payments,ma_rate,mr_id,md_detno,mr_whman,mr_cop,mr_shcode,ma_code from qua_mrbdet left join"
			+ " qua_mrb on md_mrid=mr_id left join make on mr_pucode=ma_code where mr_code = ? and md_id in(??)";

	@Override
	public void deleteMRB(int id) {
		restoreprodio(id);
		deleteByCondition("QUA_MRB", "mr_id=" + id);
	}

	/**
	 * MRB单删除时，修改不良品入库单明细已转数量
	 */
	private void restoreprodio(int mrid) {
		Object[] objs = getFieldsDataByCondition("QUA_MRB", new String[] { "mr_veid", "mr_inqty" }, "mr_id=" + mrid);
		if (objs != null && objs[0] != null) {
			updateByCondition("ProdIODetail", "pd_yqty=nvl(pd_yqty,0)-" + objs[1], "pd_id=" + objs[0]);
		}
	}

	/**
	 * 送检数量之和大于收料数量时不允许审核
	 */
	@Override
	public String checkqtyCheck(int id) {
		SqlRowList rs = queryForRowSet(
				"select mr_code from QUA_MRB where mr_inqty < (select sum(md_okqty+md_ngqty) from QUA_MRBDet where md_mrid=?) and mr_id=?",
				id, id);
		StringBuffer sb = new StringBuffer();
		if (rs.next()) {
			sb.append("单据：");
			sb.append(rs.getString("mr_code"));
			sb.append("的送检数之和大于收料总数，不允许审核！");
		}
		return sb.toString();
	}

	@Override
	public List<JSONObject> detailTurnDefectOut(String caller, String piclass, List<Map<Object, Object>> maps, boolean isok) {
		Timestamp time = Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS));
		// 按采购单供应商+币别分组
		String ids = BaseUtil.parseArray2Str(CollectionUtil.pluck(maps, "md_id"), ",");
		SqlRowList rs = queryForRowSet(QUA_PURC_GROUP.replace("??", ids));
		String mrCode = null;
		int piid = 0;
		SqlRowList rsx = null;
		String code = null;
		int pdid = 0;
		double qty = 0;
		List<Map<Object, Object>> list = null;
		Map<Object, Object> de = null;
		List<JSONObject> codes = new ArrayList<JSONObject>();
		while (rs.next()) {
			mrCode = rs.getString("mr_code");
			rsx = queryForRowSet(QUA_PURC_ARG.replace("??", ids), mrCode);
			code = null;
			int count = 1;
			while (rsx.next()) {
				if (!StringUtil.hasText(rsx.getObject("pu_code"))) {
					BaseUtil.showError("采购单[" + rsx.getObject("mr_pucode") + "]不存在！");
				}
				if (code == null) {
					JSONObject j = new JSONObject();
					code = sGetMaxNumber(caller, 2);
					piid = getSeqId("PRODINOUT_SEQ");
					getJdbcTemplate().update(INSERT_BASEPRODINOUT, piid, code, code, SystemSession.getUser().getEm_name(),
							SystemSession.getUser().getEm_code(), time, BaseUtil.getLocalMessage("ENTERING"), "ENTERING", piclass,
							"UNPOST", BaseUtil.getLocalMessage("UNPOST"), rsx.getObject("mr_vendcode"), rsx.getObject("mr_vendname"),
							rsx.getObject("pu_receivecode"), rsx.getObject("pu_receivename"), rsx.getObject("pu_payments"),
							rsx.getObject("pu_currency"), rsx.getGeneralDouble("pu_rate"), "特采", rsx.getObject("mr_code"),
							rsx.getObject("mr_whman"), rsx.getObject("mr_cop"), rsx.getObject("mr_shcode"));
					getJdbcTemplate()
							.update("update prodinout set pi_paymentcode=(select pa_code from payments where pa_class='付款方式' and pa_name=pi_payment) where pi_id=?",
									piid);
					getJdbcTemplate().update(
							"update prodinout set pi_cgycode=(select max(em_code) from employee where em_name=pi_cgy) where pi_id=?", piid);
					getJdbcTemplate()
							.update("update prodinout set (pi_departmentcode,pi_departmentname)=(select max(dp_code),max(dp_name) from employee left join Department on dp_name=em_depart where em_code=pi_recordman) where pi_id=?",
									piid);
					j.put("pi_id", piid);
					j.put("pi_inoutno", code);
					codes.add(j);
				}
				list = CollectionUtil.filter(maps, CollectionUtil.INCLUDE, "md_id", rsx.getInt("md_id"));
				if (list.size() == 1) {
					de = list.get(0);
					pdid = getSeqId("PRODIODETAIL_SEQ");
					qty = Double.parseDouble(de.get("qty").toString());
					Map<String, Object> diffence = new HashMap<String, Object>();
					diffence = new HashMap<String, Object>();
					diffence.put("pd_piid", piid);
					diffence.put("pd_inoutno", "'" + code + "'");
					diffence.put("pd_pdno", count++);
					diffence.put("pd_piclass", "'不良品出库单'");
					diffence.put("pd_auditstatus", "'ENTERING'");
					diffence.put("pd_status", 0);
					diffence.put("pd_inqty", 0);
					diffence.put("pd_qcid", 0);
					diffence.put("pd_yqty", 0);
					diffence.put("pd_id", pdid);
					diffence.put("pd_outqty", qty);
					diffence.put("pd_taxrate", rsx.getObject("pd_rate"));//修改税率和采购单一致  maz  5-4
					diffence.put("pd_mrid", rsx.getObject("md_id"));
					copyRecord("ProdIODetail", "ProdIODetail", "pd_id=" + rsx.getObject("mr_veid"), diffence);
					updateByCondition("QUA_MRBDet", "md_statuscode='TURNIN', md_status='" + BaseUtil.getLocalMessage("TURNIN") + "',"
							+ (isok ? "md_isok=1" : "md_isng=1"), "md_id=" + de.get("md_id"));
					// 记录日志
					logger.turnDetail("特采操作", caller, "mr_id", de.get("mr_id"), rsx.getInt("md_detno"));
				}
			}
		}
		return codes;
	}

	@Override
	public List<JSONObject> detailTurnDefectOut2(String caller, String piclass, List<Map<Object, Object>> maps, boolean isok) {
		Timestamp time = Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS));
		// 按委外单供应商+币别分组
		String ids = BaseUtil.parseArray2Str(CollectionUtil.pluck(maps, "md_id"), ",");
		SqlRowList rs = queryForRowSet(QUA_MAKE_GROUP.replace("??", ids));
		String vCode = null;
		String mrCode = null;
		int piid = 0;
		SqlRowList rsx = null;
		String code = null;
		int pdid = 0;
		double qty = 0;
		List<Map<Object, Object>> list = null;
		Map<Object, Object> de = null;
		List<JSONObject> codes = new ArrayList<JSONObject>();
		while (rs.next()) {
			mrCode = rs.getString("mr_code");
			rsx = queryForRowSet(QUA_MAKE_ARG.replace("??", ids), mrCode);
			code = null;
			int count = 1;
			while (rsx.next()) {
				if (!StringUtil.hasText(rsx.getObject("mr_vendcode"))) {
					BaseUtil.showError("供应商未填写！");
				} else {
					vCode = rsx.getString("mr_vendcode");
				}
				if (!StringUtil.hasText(rsx.getObject("ma_code"))) {
					BaseUtil.showError("委外加工单[" + rsx.getObject("mr_pucode") + "]不存在！");
				}
				Object[] obj = getFieldsDataByCondition("Vendor", new String[] { "ve_name", "ve_apvendcode", "ve_apvendname" }, "ve_code='"
						+ vCode + "'");
				if (obj == null) {
					BaseUtil.showError("供应商编号[" + vCode + "]不存在！");
				}
				if (code == null) {
					JSONObject j = new JSONObject();
					code = sGetMaxNumber(caller, 2);
					piid = getSeqId("PRODINOUT_SEQ");
					getJdbcTemplate().update(INSERT_BASEPRODINOUT, piid, code, code, SystemSession.getUser().getEm_name(),
							SystemSession.getUser().getEm_code(), time, BaseUtil.getLocalMessage("ENTERING"), "ENTERING", piclass,
							"UNPOST", BaseUtil.getLocalMessage("UNPOST"), vCode, obj[0], obj[1], obj[2], rsx.getObject("ma_payments"),
							rsx.getObject("ma_currency"), rsx.getGeneralDouble("ma_rate"), "OS", rsx.getObject("mr_code"),
							rsx.getObject("mr_whman"), rsx.getObject("mr_cop"), rsx.getObject("mr_shcode"));
					getJdbcTemplate()
							.update("update prodinout set pi_paymentcode=(select pa_code from payments where pa_class='付款方式' and pa_name=pi_payment) where pi_id=?",
									piid);
					getJdbcTemplate().update(
							"update prodinout set pi_cgycode=(select max(em_code) from employee where em_name=pi_cgy) where pi_id=?", piid);
					getJdbcTemplate()
							.update("update prodinout set (pi_departmentcode,pi_departmentname)=(select max(dp_code),max(dp_name) from employee left join Department on dp_name=em_depart where em_code=pi_recordman) where pi_id=?",
									piid);
					j.put("pi_id", piid);
					j.put("pi_inoutno", code);
					codes.add(j);
				}
				list = CollectionUtil.filter(maps, CollectionUtil.INCLUDE, "md_id", rsx.getInt("md_id"));
				if (list.size() == 1) {
					de = list.get(0);
					pdid = getSeqId("PRODIODETAIL_SEQ");
					qty = Double.parseDouble(de.get("qty").toString());
					Map<String, Object> diffence = new HashMap<String, Object>();
					diffence = new HashMap<String, Object>();
					diffence.put("pd_piid", piid);
					diffence.put("pd_inoutno", "'" + code + "'");
					diffence.put("pd_pdno", count++);
					diffence.put("pd_piclass", "'不良品出库单'");
					diffence.put("pd_auditstatus", "'ENTERING'");
					diffence.put("pd_status", 0);
					diffence.put("pd_inqty", 0);
					diffence.put("pd_qcid", 0);
					diffence.put("pd_yqty", 0);
					diffence.put("pd_id", pdid);
					diffence.put("pd_outqty", qty);
					diffence.put("pd_mrid", rsx.getObject("md_id"));
					copyRecord("ProdIODetail", "ProdIODetail", "pd_id=" + rsx.getObject("mr_veid"), diffence);
					updateByCondition("QUA_MRBDet", "md_statuscode='TURNIN', md_status='" + BaseUtil.getLocalMessage("TURNIN") + "',"
							+ (isok ? "md_isok=1" : "md_isng=1"), "md_id=" + de.get("md_id"));
					// 记录日志
					logger.turnDetail("特采操作", caller, "mr_id", de.get("mr_id"), rsx.getInt("md_detno"));
				}
			}
		}
		return codes;
	}

	public JSONObject turnProdioPurc(JSONObject j) {
		Object piid = j.get("pi_id");
		getJdbcTemplate().update("update prodiodetail set pd_mrok=1 where pd_piid=?", piid);// 用来判断当前不良品出库单是合格转入的
		// 转采购验收单
		Map<String, Object> diff = new HashMap<String, Object>();
		int pi_id = getSeqId("PRODINOUT_SEQ");
		String pi_inoutno = sGetMaxNumber("ProdInOut!PurcCheckin", 2);
		diff.put("pi_id", pi_id);
		diff.put("pi_inoutno", "'" + pi_inoutno + "'");
		diff.put("pi_class", "'采购验收单'");
		diff.put("pi_recorddate", "sysdate");
		diff.put("pi_recordman", "'" + SystemSession.getUser().getEm_name() + "'");
		diff.put("pi_updatedate", "sysdate");
		diff.put("pi_updateman", "'" + SystemSession.getUser().getEm_name() + "'");
		diff.put("pi_operatorcode", "'" + SystemSession.getUser().getEm_code() + "'");
		diff.put("pi_invostatuscode", "'ENTERING'");
		diff.put("pi_invostatus", "'" + BaseUtil.getLocalMessage("ENTERING") + "'");
		diff.put("pi_statuscode", "'UNPOST'");
		diff.put("pi_type", "'特采'");
		diff.put("pi_status", "'" + BaseUtil.getLocalMessage("UNPOST") + "'");
		diff.put("pi_printstatuscode", "'UNPRINT'");
		diff.put("pi_printstatus", "'" + BaseUtil.getLocalMessage("UNPRINT") + "'");
		// 转入主表
		copyRecord("ProdInOut", "ProdInOut", "pi_id=" + piid, diff);
		// 转入从表
		SqlRowList rowlist = queryForRowSet("SELECT pd_id,pd_outqty FROM ProdIODetail WHERE pd_piid=? order by pd_pdno", piid);
		diff = new HashMap<String, Object>();
		diff.put("pd_piid", pi_id);
		diff.put("pd_inoutno", "'" + pi_inoutno + "'");
		diff.put("pd_piclass", "'采购验收单'");
		diff.put("pd_auditstatus", "'ENTERING'");
		diff.put("pd_status", 0);
		diff.put("pd_outqty", 0);
		diff.put("pd_yqty", 0);
		diff.put("pd_batchcode", "null");
		diff.put("pd_batchid", 0);
		diff.put("pd_vacode", "null");
		while (rowlist.next()) {
			diff.put("pd_id", getSeqId("PRODIODETAIL_SEQ"));
			diff.put("pd_inqty", rowlist.getDouble("pd_outqty"));
			diff.put("pd_ioid", rowlist.getDouble("pd_id"));
			copyRecord("ProdIODetail", "ProdIODetail", "pd_id=" + rowlist.getInt("pd_id"), diff);
		}
		getJdbcTemplate()
				.update("update ProdIODetail set pd_orderprice=(select pd_price from purchasedetail where pd_code=pd_ordercode and pd_detno=pd_orderdetno) where pd_piid=?",
						pi_id);
		getJdbcTemplate().update("update ProdIODetail set pd_ordertotal=round(pd_orderprice*pd_inqty,2) where pd_piid=?", pi_id);
		getJdbcTemplate()
				.update("update ProdIODetail set (pd_whcode,pd_whname)=(select pr_whcode,pr_whname from product where pd_prodcode=pr_code) where pd_piid=?",
						pi_id);
		getJdbcTemplate()
				.update("update Prodinout set (pi_whcode,pi_whname)=(select pr_whcode,pr_whname from product left join Prodiodetail on pd_prodcode=pr_code where pd_piid=pi_id and pd_pdno=1) where pi_id=?",
						pi_id);
		JSONObject o = new JSONObject();
		o.put("pi_id", pi_id);
		o.put("pi_inoutno", pi_inoutno);
		return o;
	}

	public JSONObject turnProdioMake(JSONObject j) {
		Object piid = j.get("pi_id");
		getJdbcTemplate().update("update prodiodetail set pd_mrok=1 where pd_piid=?", piid);// 用来判断当前不良品出库单是合格转入的
		// 转委外验收单
		Map<String, Object> diff = new HashMap<String, Object>();
		int pi_id = getSeqId("PRODINOUT_SEQ");
		String pi_inoutno = sGetMaxNumber("ProdInOut!OutsideCheckIn", 2);
		diff.put("pi_id", pi_id);
		diff.put("pi_inoutno", "'" + pi_inoutno + "'");
		diff.put("pi_class", "'委外验收单'");
		diff.put("pi_recorddate", "sysdate");
		diff.put("pi_recordman", "'" + SystemSession.getUser().getEm_name() + "'");
		diff.put("pi_updatedate", "sysdate");
		diff.put("pi_updateman", "'" + SystemSession.getUser().getEm_name() + "'");
		diff.put("pi_operatorcode", "'" + SystemSession.getUser().getEm_code() + "'");
		diff.put("pi_invostatuscode", "'ENTERING'");
		diff.put("pi_invostatus", "'" + BaseUtil.getLocalMessage("ENTERING") + "'");
		diff.put("pi_statuscode", "'UNPOST'");
		diff.put("pi_type", "'特采'");
		diff.put("pi_status", "'" + BaseUtil.getLocalMessage("UNPOST") + "'");
		diff.put("pi_printstatuscode", "'UNPRINT'");
		diff.put("pi_printstatus", "'" + BaseUtil.getLocalMessage("UNPRINT") + "'");
		// 转入主表
		copyRecord("ProdInOut", "ProdInOut", "pi_id=" + piid, diff);
		// 转入从表
		SqlRowList rowlist = queryForRowSet("SELECT pd_id,pd_outqty FROM ProdIODetail WHERE pd_piid=? order by pd_pdno", piid);
		diff = new HashMap<String, Object>();
		diff.put("pd_piid", pi_id);
		diff.put("pd_inoutno", "'" + pi_inoutno + "'");
		diff.put("pd_piclass", "'委外验收单'");
		diff.put("pd_auditstatus", "'ENTERING'");
		diff.put("pd_status", 0);
		diff.put("pd_outqty", 0);
		diff.put("pd_yqty", 0);
		diff.put("pd_batchcode", "null");
		diff.put("pd_batchid", 0);
		while (rowlist.next()) {
			diff.put("pd_id", getSeqId("PRODIODETAIL_SEQ"));
			diff.put("pd_inqty", rowlist.getDouble("pd_outqty"));
			diff.put("pd_ioid", rowlist.getDouble("pd_id"));
			copyRecord("ProdIODetail", "ProdIODetail", "pd_id=" + rowlist.getInt("pd_id"), diff);
		}
		getJdbcTemplate()
				.update("update ProdIODetail set pd_orderprice=(select pd_price from purchasedetail where pd_code=pd_ordercode and pd_detno=pd_orderdetno) where pd_piid=?",
						pi_id);
		getJdbcTemplate().update("update ProdIODetail set pd_ordertotal=round(pd_orderprice*pd_inqty,2) where pd_piid=?", pi_id);
		getJdbcTemplate()
				.update("update ProdIODetail set (pd_whcode,pd_whname)=(select pr_whcode,pr_whname from product where pd_prodcode=pr_code) where pd_piid=?",
						pi_id);
		getJdbcTemplate()
				.update("update Prodinout set (pi_whcode,pi_whname)=(select pr_whcode,pr_whname from product left join Prodiodetail on pd_prodcode=pr_code where pd_piid=pi_id and pd_pdno=1) where pi_id=?",
						pi_id);
		JSONObject o = new JSONObject();
		o.put("pi_id", pi_id);
		o.put("pi_inoutno", pi_inoutno);
		return o;
	}
}
