package com.uas.erp.dao.common.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.dao.common.QUAVerifyApplyDetailDao;
import com.uas.erp.model.Key;

@Repository
public class QUAVerifyApplyDetailDaoImpl extends BaseDao implements QUAVerifyApplyDetailDao {

	@Autowired
	private TransferRepository transferRepository;

	final static String INSERT_PRODIO_WH = "INSERT INTO ProdInOut(pi_id, pi_inoutno, pi_date, pi_class, pi_invostatus, pi_invostatuscode,"
			+ "pi_recordman, pi_recorddate, pi_whcode, pi_whname, pi_statuscode, pi_status) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
	final static String INSERT_PRODIO = "INSERT INTO ProdInOut(pi_id, pi_inoutno, pi_date, pi_class, pi_invostatus, pi_invostatuscode,"
			+ "pi_recordman, pi_recorddate, pi_statuscode, pi_status) VALUES (?,?,?,?,?,?,?,?,?,?)";
	final static String INSERT_DETAIL_F = "INSERT INTO ProdIODetail(pd_id,pd_piid,pd_inoutno,pd_piclass,pd_pdno,pd_status,pd_auditstatus,"
			+ "pd_inqty,pd_ordercode,pd_wccode,pd_prodcode,pd_batchcode,pd_prodid, pd_qcid) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static String INSERT_DETAIL_N = "INSERT INTO ProdIODetail(pd_id,pd_piid,pd_inoutno,pd_piclass,pd_pdno,pd_status,pd_auditstatus,"
			+ "pd_inqty,pd_ordercode,pd_wccode,pd_prodcode,pd_batchcode,pd_prodid, pd_qcid, pd_whcode) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static String INSERT_BASEPRODINOUT = "INSERT INTO prodinout(pi_id, Fin_Code, pi_inoutno,pi_recordman,pi_operatorcode,pi_recorddate"
			+ ",pi_invostatus,pi_invostatuscode,pi_class,pi_statuscode,pi_status,pi_cardcode,pi_title,pi_receivecode,pi_receivename,pi_payment,pi_paymentcode"
			+ ",pi_currency,pi_rate,pi_type,pi_sendcode,pi_sourcecode,pi_cgy,pi_cgycode,pi_cop,pi_emcode,pi_emname,pi_intype) "
			+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static String QUA_PURC_GROUP = "select max(ve_ordercode) ve_ordercode,pu_vendcode,nvl(pu_receivecode,pu_vendcode) pu_receivecode,pu_currency,pu_paymentscode from qua_verifyapplydetaildet"
			+ " left join qua_verifyapplydetail on ved_veid=ve_id left join purchasedetail on ve_ordercode=pd_code and ve_orderdetno=pd_detno left join"
			+ " purchase on pd_puid=pu_id where ved_id in(??) group by pu_vendcode,nvl(pu_receivecode,pu_vendcode),pu_currency,pu_paymentscode";
	final static String QUA_PURC_ARG = "select ve_orderdetno,ve_ordercode,vad_prodcode,ve_makedate,vad_qty,vad_vaid,vad_vendcode,vad_vendname,vad_code"
			+ ",pd_id,pd_price,pd_rate,ved_id,pu_vendname,pu_receivename,pu_payments,pu_rate,ve_id,ved_detno,ve_sendcode,ve_code,va_whman,va_whmancode "
			+ ",ve_cop,pd_custcode,pd_custname,pd_salecode,pd_remark2,pd_remark3,ve_buyercode,ve_buyerman,ve_purcqty,pd_rebatesprice,ve_description from qua_verifyapplydetaildet left join"
			+ " qua_verifyapplydetail on ved_veid=ve_id left join purchasedetail on ve_ordercode=pd_code and ve_orderdetno=pd_detno left join purchase"
			+ " on pd_puid=pu_id  left join VerifyApply on vad_vaid=va_id where nvl(pu_vendcode,' ')=? and nvl(nvl(pu_receivecode,pu_vendcode),' ') = ? and nvl(pu_currency,' ') = ? and nvl(pu_paymentscode,' ')=? and ved_id in(??)";
	final static String INSERT_PRODIODETAIL = "INSERT INTO prodiodetail(pd_orderdetno,pd_ordercode,pd_prodcode,pd_prodmadedate,pd_inqty,pd_purcinqty"
			+ ",pd_id,pd_inoutno,pd_piclass,pd_pdno,pd_status,pd_auditstatus,pd_piid,pd_orderid,pd_vacode,pd_whcode,pd_orderprice"
			+ ",pd_taxrate,pd_ordertotal,pd_whname,pd_qcid,pd_prodid,pd_whid,pd_textbox,pd_batchcode,pd_custcode,pd_custname,pd_salecode,pd_remark2,pd_remark3,pd_unitpackage,pd_mantissapackage,pd_mcid,pd_jobcode,pd_rebatesprice,pd_description"
			+ ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static String QUA_SEND_GROUP = "select max(ve_ordercode) ve_ordercode,max(pu_vendcode) pu_vendcode,max(nvl(pu_receivecode,pu_vendcode)) pu_receivecode,max(pu_currency) pu_currency,max(pu_paymentscode) pu_paymentscode,ve_sendcode from qua_verifyapplydetaildet"
			+ " left join qua_verifyapplydetail on ved_veid=ve_id left join purchasedetail on ve_ordercode=pd_code and ve_orderdetno=pd_detno left join"
			+ " purchase on pd_puid=pu_id where ved_id in(??) group by ve_sendcode";
	final static String QUA_SEND_ARG = "select ve_orderdetno,ve_ordercode,vad_prodcode,ve_makedate,vad_qty,vad_vaid,vad_vendcode,vad_vendname,vad_code"
			+ ",pd_id,pd_price,pd_rate,ved_id,pu_vendname,pu_receivename,pu_payments,pu_rate,ve_id,ved_detno,ve_sendcode,ve_code,va_whman,va_whmancode "
			+ ",ve_cop,pd_custcode,pd_custname,pd_salecode,pd_remark2,pd_remark3,ve_buyercode,ve_buyerman,ve_purcqty,pd_rebatesprice,ve_description from qua_verifyapplydetaildet left join"
			+ " qua_verifyapplydetail on ved_veid=ve_id left join purchasedetail on ve_ordercode=pd_code and ve_orderdetno=pd_detno left join purchase"
			+ " on pd_puid=pu_id  left join VerifyApply on vad_vaid=va_id where nvl(ve_sendcode,' ')=?  and ved_id in(??)";

	final static String QUA_TYPE_GROUP = "select nvl(va_intype,'正常委外') va_intype from qua_verifyapplydetaildet left join qua_verifyapplydetail on ved_veid=ve_id left join verifyapply on vad_vaid=va_id where ved_id in(??) and ve_class='委外检验单' group by nvl(va_intype,'正常委外')";
	final static String QUA_MAKE_GROUP = "select ma_vendcode,ma_currency,apvendcode from "
			+ "(select ma_currency,ma_vendcode,nvl(nvl(ma_apvendcode,ve_apvendcode),ma_vendcode) apvendcode from qua_verifyapplydetaildet "
			+ "left join qua_verifyapplydetail on ved_veid=ve_id left join verifyapply on vad_vaid=va_id left join make on ve_ordercode=ma_code "
			+ "left join vendor V on ma_vendcode=V.ve_code where ved_id in(??) and nvl(va_intype,'正常委外')='正常委外') T group by ma_vendcode,ma_currency,apvendcode";
	final static String QUA_OSSEND_GROUP = "select ve_sendcode,max(ma_vendcode)ma_vendcode,max(ma_currency)ma_currency,max(apvendcode)apvendcode from "
			+ "(select ve_sendcode,ma_currency,ma_vendcode,nvl(nvl(ma_apvendcode,ve_apvendcode),ma_vendcode) apvendcode from qua_verifyapplydetaildet "
			+ "left join qua_verifyapplydetail on ved_veid=ve_id left join verifyapply on vad_vaid=va_id left join make on ve_ordercode=ma_code "
			+ "left join vendor V on ma_vendcode=V.ve_code where ved_id in(??) and nvl(va_intype,'正常委外')='正常委外') T group by ve_sendcode";
	final static String QUA_MAKECRAFT_GROUP = "select mc_vendcode,mc_currency,apvendcode from "
			+ "(select mc_currency,mc_vendcode,nvl(ve_apvendcode,mc_vendcode) apvendcode from qua_verifyapplydetaildet "
			+ "left join qua_verifyapplydetail on ved_veid=ve_id left join verifyapply on vad_vaid=va_id left join makecraft on VAD_MCID=MC_ID "
			+ "left join vendor V on mc_vendcode=V.ve_code where ved_id in(??) and va_intype='工序委外') T group by mc_vendcode,mc_currency,apvendcode";
	final static String QUA_SENDOS_GROUP = "select ve_sendcode,max(mc_vendcode)mc_vendcode,max(mc_currency)mc_currency,max(apvendcode)apvendcode from "
			+ "(select ve_sendcode,mc_currency,mc_vendcode,nvl(ve_apvendcode,mc_vendcode) apvendcode from qua_verifyapplydetaildet "
			+ "left join qua_verifyapplydetail on ved_veid=ve_id left join verifyapply on vad_vaid=va_id left join makecraft on VAD_MCID=MC_ID "
			+ "left join vendor V on mc_vendcode=V.ve_code where ved_id in(??) and va_intype='工序委外') T group by ve_sendcode";
	final static String QUA_MAKE_ARG = "select T.ve_code,T.ve_ordercode,vad_prodcode,T.ve_makedate,vad_qty,vad_vaid,vad_vendcode,vad_vendname,vad_code ,ma_id,ma_price,"
			+ " nvl(ma_taxrate,0) ma_taxrate,ved_id,ma_vendname,ma_payments,ma_paymentscode,ma_rate,T.ve_id,ved_detno,T.ve_sendcode,va_whman ,va_whmancode,T.ve_cop,apvendcode,ve_name,T.ve_buyercode,T.ve_buyerman,T.ve_purcqty"
			+ " from (select q.ve_code,q.ve_ordercode,vad_prodcode,q.ve_makedate,vad_qty,vad_vaid,vad_vendcode,vad_vendname,vad_code ,ma_id,ma_price,ma_taxrate,"
			+ " ved_id,ma_vendname,ma_payments,ma_paymentscode,ma_rate,q.ve_id,ved_detno,q.ve_sendcode,va_whman ,va_whmancode,q.ve_cop ,"
			+ " nvl(nvl(ma_apvendcode,V.ve_apvendcode),ma_vendcode) apvendcode,q.ve_buyercode,q.ve_buyerman,q.ve_purcqty "
			+ " from qua_verifyapplydetaildet left join qua_verifyapplydetail q on ved_veid=q.ve_id left join verifyapply on "
			+ " vad_vaid=va_id left join make on ve_ordercode=ma_code left join vendor V on ma_vendcode=V.ve_code where ma_vendcode=? and ma_currency = ?"
			+ " and ved_id in(??) ) T left join vendor E on E.ve_code=apvendcode WHERE apvendcode =?";
	final static String QUA_OSSEND_ARG = "select T.ve_code,T.ve_ordercode,vad_prodcode,T.ve_makedate,vad_qty,vad_vaid,vad_vendcode,vad_vendname,vad_code ,ma_id,ma_price,"
			+ " nvl(ma_taxrate,0) ma_taxrate,ved_id,ma_vendname,ma_payments,ma_paymentscode,ma_rate,T.ve_id,ved_detno,T.ve_sendcode,va_whman ,va_whmancode,T.ve_cop,apvendcode,ve_name,T.ve_buyercode,T.ve_buyerman,T.ve_purcqty"
			+ " from (select q.ve_code,q.ve_ordercode,vad_prodcode,q.ve_makedate,vad_qty,vad_vaid,vad_vendcode,vad_vendname,vad_code ,ma_id,ma_price,ma_taxrate,"
			+ " ved_id,ma_vendname,ma_payments,ma_paymentscode,ma_rate,q.ve_id,ved_detno,q.ve_sendcode,va_whman ,va_whmancode,q.ve_cop ,"
			+ " nvl(nvl(ma_apvendcode,V.ve_apvendcode),ma_vendcode) apvendcode,q.ve_buyercode,q.ve_buyerman,q.ve_purcqty "
			+ " from qua_verifyapplydetaildet left join qua_verifyapplydetail q on ved_veid=q.ve_id left join verifyapply on "
			+ " vad_vaid=va_id left join make on ve_ordercode=ma_code left join vendor V on ma_vendcode=V.ve_code where nvl(ve_sendcode,' ')=?"
			+ " and ved_id in(??) ) T left join vendor E on E.ve_code=apvendcode";
	final static String QUA_MAKECRAFT_ARG = "select T.ve_code,T.ve_ordercode,vad_prodcode,T.ve_makedate,vad_qty,vad_vaid,vad_vendcode,vad_vendname,vad_code,mc_id,mc_price,"
			+ " nvl(mc_taxrate,0) mc_taxrate,ved_id,mc_vendname,mc_payments,mc_paymentscode,mc_rate,T.ve_id,ved_detno,T.ve_sendcode,va_whman ,va_whmancode,T.ve_cop,apvendcode,ve_name,T.ve_buyercode,T.ve_buyerman"
			+ " from (select q.ve_code,q.ve_ordercode,vad_prodcode,q.ve_makedate,vad_qty,vad_vaid,vad_vendcode,vad_vendname,vad_code ,mc_id,mc_price,mc_taxrate,"
			+ " ved_id,mc_vendname,mc_payments,mc_paymentscode,mc_rate,q.ve_id,ved_detno,q.ve_sendcode,va_whman ,va_whmancode,q.ve_cop ,"
			+ " nvl(V.ve_apvendcode,mc_vendcode) apvendcode,q.ve_buyercode,q.ve_buyerman "
			+ " from qua_verifyapplydetaildet left join qua_verifyapplydetail q on ved_veid=q.ve_id left join verifyapply on "
			+ " vad_vaid=va_id left join makecraft on vad_mcid=mc_id left join vendor V on mc_vendcode=V.ve_code where mc_vendcode=? and mc_currency = ?"
			+ " and ved_id in(??) ) T left join vendor E on E.ve_code=apvendcode WHERE apvendcode =?";
	final static String QUA_SENDOS_ARG = "select T.ve_code,T.ve_ordercode,vad_prodcode,T.ve_makedate,vad_qty,vad_vaid,vad_vendcode,vad_vendname,vad_code,mc_id,mc_price,"
			+ " nvl(mc_taxrate,0) mc_taxrate,ved_id,mc_vendname,mc_payments,mc_paymentscode,mc_rate,T.ve_id,ved_detno,T.ve_sendcode,va_whman ,va_whmancode,T.ve_cop,apvendcode,ve_name,T.ve_buyercode,T.ve_buyerman"
			+ " from (select q.ve_code,q.ve_ordercode,vad_prodcode,q.ve_makedate,vad_qty,vad_vaid,vad_vendcode,vad_vendname,vad_code ,mc_id,mc_price,mc_taxrate,"
			+ " ved_id,mc_vendname,mc_payments,mc_paymentscode,mc_rate,q.ve_id,ved_detno,q.ve_sendcode,va_whman ,va_whmancode,q.ve_cop ,"
			+ " nvl(V.ve_apvendcode,mc_vendcode) apvendcode,q.ve_buyercode,q.ve_buyerman "
			+ " from qua_verifyapplydetaildet left join qua_verifyapplydetail q on ved_veid=q.ve_id left join verifyapply on "
			+ " vad_vaid=va_id left join makecraft on vad_mcid=mc_id left join vendor V on mc_vendcode=V.ve_code where nvl(ve_sendcode,' ')=?"
			+ " and ved_id in(??) ) T left join vendor E on E.ve_code=apvendcode";
	final static String QUA_MAKE = "select ve_ordercode,vad_prodcode,ve_makedate,vad_qty,vad_vaid,vad_vendcode,vad_vendname,vad_code"
			+ ",ma_id,ma_price,ma_taxrate,ved_id,ma_vendname,ma_payments,ma_rate,ve_id,ved_detno from qua_verifyapplydetaildet left join"
			+ " qua_verifyapplydetail on ved_veid=ve_id left join make on ve_ordercode=ma_code where ved_id in(??)";

	public JSONObject newProdIO(String caller, String whcode, String piclass) {
		Timestamp time = Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS));
		Object whname = getFieldDataByCondition("WareHouse", "wh_description", "wh_code='" + whcode + "'");
		if (whname == null) {
			BaseUtil.showError("仓库[" + whcode + "]不存在!");
		}
		int id = getSeqId("PRODINOUT_SEQ");
		String no = sGetMaxNumber(caller, 2);
		execute(INSERT_PRODIO_WH, new Object[] { id, no, time, piclass, BaseUtil.getLocalMessage("ENTERING"), "ENTERING",
				SystemSession.getUser().getEm_name(), time, whcode, whname, "UNPOST", BaseUtil.getLocalMessage("UNPOST") });
		JSONObject j = new JSONObject();
		j.put("pi_id", id);
		j.put("pi_inoutno", no);
		return j;
	}

	public JSONObject newProdIO2(String caller, String piclass) {
		Timestamp time = Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS));
		int id = getSeqId("PRODINOUT_SEQ");
		String no = sGetMaxNumber(caller, 2);
		execute(INSERT_PRODIO, new Object[] { id, no, time, piclass, BaseUtil.getLocalMessage("ENTERING"), "ENTERING",
				SystemSession.getUser().getEm_name(), time, "UNPOST", BaseUtil.getLocalMessage("UNPOST") });
		JSONObject j = new JSONObject();
		j.put("pi_id", id);
		j.put("pi_inoutno", no);
		return j;
	}

	/**
	 * 完工入库单 已按仓库分组
	 * 
	 * @param no
	 *            入库单号
	 * @param veid
	 *            生产检验单ID
	 * @param qty
	 *            本次完工数
	 */
	@Override
	@Transactional
	public void turnMadeWh(String no, int veid, double qty) {
		Object[] objs = getFieldsDataByCondition("QUA_VerifyApplyDetail left join Product on ved_prodcode=pr_code", new String[] {
				"ve_code", "vad_code", "vad_wccode", "vad_vaid", "vad_prodcode", "ve_batchcode", "pr_id" }, "ve_id=" + veid);
		Object id = getFieldDataByCondition("ProdInOut", "pi_id", "pi_inoutno='" + no + "'");
		Object detno = getFieldDataByCondition("ProdIODetail", "max(pd_pdno)", "pd_piid=" + id);
		detno = detno == null ? 1 : (Integer.parseInt(detno.toString()) + 1);
		execute(INSERT_DETAIL_F, new Object[] { getSeqId("PRODIODETAIL_SEQ"), id, no, "完工入库单", detno, 0, "ENTERING", qty, objs[0], objs[2],
				veid, objs[4], objs[5], objs[6] });
		// 修改状态
		// ve_yqty为当前已转入完工入库单数量，ve_madeqty为已完工数量，(在完工入库单过账后，pd_qty会反馈到ve_madeqty)
		updateByCondition("QUA_VerifyApplyDetail", "ve_statuscode='PARTFI',ve_status='" + BaseUtil.getLocalMessage("PARTFI")
				+ "',ve_yqty=ve_yqty+" + qty, "ve_id=" + veid);
		updateByCondition("QUA_VerifyApplyDetail", "ve_statuscode='COMPLETED',ve_status='" + BaseUtil.getLocalMessage("COMPLETED") + "'",
				"ve_id=" + veid + " AND ve_yqty=vad_qty");
	}

	/**
	 * 检验单批量转入库单
	 */
	@Override
	@Transactional
	public List<JSONObject> detailTurnStorage(String caller, String piclass, List<Map<Object, Object>> maps, boolean isok) {
		Timestamp time = Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS));
		// 按采购单供应商+币别分组
		String ids = BaseUtil.parseArray2Str(CollectionUtil.pluck(maps, "ved_id"), ",");
		SqlRowList rs = queryForRowSet(QUA_PURC_GROUP.replace("??", ids));
		// maz 2018030244 新增按照送货单号进行分组
		if(isDBSetting("VerifyApplyDetail!Deal","turnByDeliver")){
			rs =  queryForRowSet(QUA_SEND_GROUP.replace("??", ids));
		}
		String vCode = null;
		String vName = null;
		String rCode = null;
		String rName = null;
		String curr = null;
		String paycode = null;
		String pay = null;
		String sendcode = null;
		double rate = 0;
		int piid = 0;
		SqlRowList rsx = null;
		String code = null;
		int pdid = 0;
		double price = 0;
		double qty = 0;
		double veqty = 0;
		double purcqty = 0;
		List<Map<Object, Object>> list = null;
		Map<Object, Object> de = null;
		List<JSONObject> codes = new ArrayList<JSONObject>();
		while (rs.next()) {
			if (rs.getString("ve_ordercode") != null) {// 分组取max(ve_ordercode)判断检验单中采购单号是否为空
				vCode = rs.getString("pu_vendcode");
				rCode = rs.getString("pu_receivecode");
				curr = rs.getString("pu_currency");
				paycode = rs.getString("pu_paymentscode");
				sendcode = rs.getString("ve_sendcode")==null ? " ":rs.getString("ve_sendcode");
				if (rCode == null) {
					BaseUtil.showError("检验单对应采购单应付供应商编号不能为空！");
				}
				if (curr == null) {
					BaseUtil.showError("检验单对应采购单中币别不能为空！");
				}
				if (vCode == null) {
					BaseUtil.showError("检验单对应采购单中供应商编号不能为空！");
				}
				if (paycode == null) {
					BaseUtil.showError("检验单对应采购单中付款方式编号不能为空！");
				}
			} else {
				BaseUtil.showError("检验单没有对应的采购单/委外单，【分组方式】请选用【按收料单分组】！");
			}
			if(isDBSetting("VerifyApplyDetail!Deal","turnByDeliver")){
				rsx =  queryForRowSet(QUA_SEND_ARG.replace("??", ids),sendcode);
			}else{
				rsx = queryForRowSet(QUA_PURC_ARG.replace("??", ids), vCode, rCode, curr, paycode);
			}
			code = null;
			int count = 1;
			while (rsx.next()) {
				if (code == null) {
					vName = rsx.getString("pu_vendname");
					rName = rsx.getString("pu_receivename");
					pay = rsx.getString("pu_payments");
					rate = rsx.getGeneralDouble("pu_rate");
					veqty = rsx.getGeneralDouble("vad_qty");
					purcqty = rsx.getGeneralDouble("ve_purcqty") == 0 ? veqty : rsx.getGeneralDouble("ve_purcqty");
					code = sGetMaxNumber(caller, 2);
					piid = getSeqId("PRODINOUT_SEQ");
					getJdbcTemplate().update(INSERT_BASEPRODINOUT, piid, code, code, SystemSession.getUser().getEm_name(),
							SystemSession.getUser().getEm_code(), time, BaseUtil.getLocalMessage("ENTERING"), "ENTERING", piclass,
							"UNPOST", BaseUtil.getLocalMessage("UNPOST"), vCode, vName, rCode, rName, pay, paycode, curr, rate,
							(isok ? null : "PURC"), rsx.getObject("ve_sendcode"), rsx.getObject("ve_code"), rsx.getObject("va_whman"),
							rsx.getObject("va_whmancode"), rsx.getObject("ve_cop"), rsx.getObject("ve_buyercode"),
							rsx.getObject("ve_buyerman"), null);
					JSONObject j = new JSONObject();
					j.put("pi_id", piid);
					j.put("pi_inoutno", code);
					codes.add(j);
				}
				list = CollectionUtil.filter(maps, CollectionUtil.INCLUDE, "ved_id", rsx.getInt("ved_id"));
				if (list.size() == 1) {
					de = list.get(0);
					pdid = getSeqId("PRODIODETAIL_SEQ");
					price = Double.parseDouble(rsx.getObject("pd_price").toString());
					qty = Double.parseDouble(de.get("qty").toString());
					String whcode = de.get("wh").toString();
					Object[] whname = getFieldsDataByCondition("Warehouse", new String[] { "wh_description", "wh_id" }, "wh_code= '"
							+ whcode + "'");
					if (whname == null) {
						BaseUtil.showError("仓库[" + whcode + "]不存在!");
					}
					Object[] vad_id = getFieldsDataByCondition(
							"VerifyApplyDetail left join VerifyApply on vad_vaid=va_id",
							new String[] { "vad_id", "vad_batchcode", "vad_unitpackage", "vad_mantissapackage" },
							"(va_code,vad_detno) in (select vad_code,vad_detno from qua_VerifyApplyDetail where ve_id" + "="
									+ de.get("ve_id") + ")");
					rate = rsx.getGeneralDouble(12);
					Object prid = getFieldDataByCondition("Product", "pr_id", "pr_code='" + rsx.getObject("vad_prodcode") + "'");
					if (vad_id == null) {
						BaseUtil.showError("检验单对应的收料单号+行号不存在！");
					}
					getJdbcTemplate().update(
							INSERT_PRODIODETAIL,
							new Object[] { rsx.getInt("ve_orderdetno"), rsx.getString("ve_ordercode"), rsx.getString("vad_prodcode"),
									rsx.getObject("ve_makedate"), qty, NumberUtil.formatDouble(qty * purcqty / veqty, 8), pdid, code,
									piclass, count++, 0, "ENTERING", piid, vad_id[0], rsx.getObject("vad_code"), whcode, price, rate,
									price * qty, whname[0], Integer.parseInt(de.get("ved_id").toString()), prid, whname[1],
									rsx.getObject("ve_sendcode"), vad_id[1], rsx.getObject("pd_custcode"), rsx.getObject("pd_custname"),
									rsx.getObject("pd_salecode"), rsx.getObject("pd_remark2"), rsx.getObject("pd_remark3"), vad_id[2],
									vad_id[3], null, null,rsx.getGeneralDouble("pd_rebatesprice"),rsx.getString("ve_description") });
					updateByCondition("QUA_VerifyApplyDetailDet",
							"ved_statuscode='TURNIN', ved_status='" + BaseUtil.getLocalMessage("TURNIN") + "',"
									+ (isok ? "ved_isok=1" : "ved_isng=1"), "ved_id=" + de.get("ved_id"));
					// 修改状态
					// 记录日志
					logger.turnDetail("msg.turnStorage", caller, "ve_id", de.get("ve_id"), rsx.getInt("ved_detno"));
				}
			}
		}
		return codes;
	}

	/**
	 * QC检验单审核后把相关数据反写回收料单中
	 */
	@Override
	public void updateverifyqty(int veid) {
		Object[] objs = getFieldsDataByCondition("QUA_VerifyApplyDetail", new String[] { "vad_detno", "vad_code" }, "ve_id=" + veid);
		Object[] qty = getFieldsDataByCondition("QUA_VerifyApplyDetailDet", new String[] { "sum(ved_checkqty)", "sum(ved_okqty)",
				"sum(ved_ngqty)" }, "ved_veid=" + veid);
		updateByCondition("VerifyApplyDetail", "vad_jyqty=" + qty[0] + ",ve_okqty=" + qty[1] + ",ve_notokqty=" + qty[2], "vad_code='"
				+ objs[1] + "' and vad_detno =" + objs[0]);
	}

	@Override
	public void deleteQC(int id, String caller) {
		Object vclass = getFieldDataByCondition("QUA_VerifyApplyDetail", "ve_class", "ve_id=" + id);
		// 如果把委外检验单归入IQC后，推荐这里写 "FQC".equals(ve_type) 锤子科技委外单批量入检FQC 17-11-09
		if ("生产检验单".equals(vclass) || ("委外检验单".equals(vclass) && "VerifyApplyDetail!FQC".equals(caller))) {
			deleteFQC(id);
		} else {
			deleteIQC(id);
		}
	}

	/**
	 * QC检验单删除时，修改收料明细状态、数量等
	 */
	private void restoreVerify(int veid) {
		Object[] objs = getFieldsDataByCondition("QUA_VerifyApplyDetail", new String[] { "vad_detno", "vad_qty", "vad_code" }, "ve_id="
				+ veid);
		if (objs != null && objs[0] != null && objs[2] != null) {
			updateByCondition(
					"VerifyApplyDetail",
					"vad_statuscode='AUDITED',vad_status='已审核',ve_status=null,ve_id=null,vad_jyqty=0,vad_yqty=0,ve_code=null,ve_okqty=0,ve_notokqty=0",
					"vad_detno=" + objs[0] + "and vad_code='" + objs[2] + "'");
		}
	}

	/**
	 * IQC送检数量之和大于收料数量时不允许审核
	 */
	@Override
	public String checkqtyCheck(int id) {
		SqlRowList rs = queryForRowSet(
				"select ve_code from QUA_VerifyApplyDetail where vad_qty < (select sum(ved_okqty+ved_ngqty) from QUA_VerifyApplyDetailDet where ved_veid=?) and ve_id=?",
				id, id);
		StringBuffer sb = new StringBuffer();
		if (rs.next()) {
			sb.append("单据：");
			sb.append(rs.getString("ve_code"));
			sb.append("的送检数之和大于收料总数，不允许审核！");
		}
		return sb.toString();
	}

	/**
	 * 委外检验单批量转入库单
	 */
	@Override
	public List<JSONObject> detailTurnStorageOs(String caller, String piclass, List<Map<Object, Object>> maps, boolean isok) {
		Timestamp time = Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS));
		// 按采购单供应商+币别+应付供应商分组
		String ids = BaseUtil.parseArray2Str(CollectionUtil.pluck(maps, "ved_id"), ",");
		SqlRowList type = queryForRowSet(QUA_TYPE_GROUP.replace("??", ids));
		SqlRowList rs = null;
		String vCode = null;
		String aCode = null;
		String vName = null;
		String curr = null;
		String pay = null;
		String sendcode = null;
		double rate = 0;
		int piid = 0;
		SqlRowList rsx = null;
		String code = null;
		int pdid = 0;
		double price = 0;
		double qty = 0;
		double veqty = 0;
		double purcqty = 0;
		List<Map<Object, Object>> list = null;
		Map<Object, Object> de = null;
		List<JSONObject> codes = new ArrayList<JSONObject>();
		while (type.next()) {
			String intype = type.getString("va_intype");
			if ("正常委外".equals(intype)) {
				if(isDBSetting("VerifyApplyDetail!Deal","turnByDeliver")){
					rs = queryForRowSet(QUA_OSSEND_GROUP.replace("??", ids));
				}else{
					rs = queryForRowSet(QUA_MAKE_GROUP.replace("??", ids));
				}
				while (rs.next()) {
					vCode = rs.getString("ma_vendcode");
					curr = rs.getString("ma_currency");
					aCode = rs.getString("apvendcode");
					sendcode = rs.getString("ve_sendcode")==null ? " ":rs.getString("ve_sendcode");
					if (vCode != null && curr != null) {
						if(isDBSetting("VerifyApplyDetail!Deal","turnByDeliver")){
							rsx = queryForRowSet(QUA_OSSEND_ARG.replace("??", ids), sendcode);
						}else{
							rsx = queryForRowSet(QUA_MAKE_ARG.replace("??", ids), vCode, curr, aCode);
						}
						code = null;
						int count = 1;
						while (rsx.next()) {
							veqty = rsx.getGeneralDouble("vad_qty");
							purcqty = rsx.getGeneralDouble("ve_purcqty") == 0 ? veqty : rsx.getGeneralDouble("ve_purcqty");
							if (code == null) {
								vName = rsx.getString("ma_vendname");
								pay = rsx.getString("ma_payments");
								rate = rsx.getDouble("ma_rate");
								code = sGetMaxNumber(caller, 2);
								piid = getSeqId("PRODINOUT_SEQ");
								getJdbcTemplate().update(INSERT_BASEPRODINOUT, piid, code, code, SystemSession.getUser().getEm_name(),
										SystemSession.getUser().getEm_code(), time, BaseUtil.getLocalMessage("ENTERING"), "ENTERING",
										piclass, "UNPOST", BaseUtil.getLocalMessage("UNPOST"), vCode, vName, rsx.getString("apvendcode"),
										rsx.getString("ve_name"), pay, rsx.getString("ma_paymentscode"), curr, rate, (isok ? null : "OS"),
										rsx.getObject("ve_sendcode"), rsx.getObject("ve_code"), rsx.getObject("va_whman"),
										rsx.getObject("va_whmancode"), rsx.getObject("ve_cop"), rsx.getObject("ve_buyercode"),
										rsx.getObject("ve_buyerman"), intype);
								JSONObject j = new JSONObject();
								j.put("pi_id", piid);
								j.put("pi_inoutno", code);
								j.put("intype", intype);
								codes.add(j);
							}
							list = CollectionUtil.filter(maps, CollectionUtil.INCLUDE, "ved_id", rsx.getInt("ved_id"));
							if (list.size() == 1) {
								de = list.get(0);
								pdid = getSeqId("PRODIODETAIL_SEQ");
								price = rsx.getGeneralDouble("ma_price");
								qty = Double.parseDouble(de.get("qty").toString());
								String whcode = de.get("wh").toString();
								Object[] whname = getFieldsDataByCondition("Warehouse", new String[] { "wh_description", "wh_id" },
										"wh_code= '" + whcode + "'");
								if (whname == null) {
									BaseUtil.showError("仓库[" + whcode + "]不存在!");
								}
								Object[] vad_id = getFieldsDataByCondition("VerifyApplyDetail left join VerifyApply on vad_vaid=va_id",
										new String[] { "vad_id", "vad_batchcode", "vad_unitpackage", "vad_mantissapackage" },
										"(va_code,vad_detno) in (select vad_code,vad_detno from qua_VerifyApplyDetail where ve_id" + "="
												+ de.get("ve_id") + ")");
								rate = Double.parseDouble(rsx.getObject("ma_taxrate").toString());
								Object prid = getFieldDataByCondition("Product", "pr_id", "pr_code='" + rsx.getObject("vad_prodcode") + "'");
								getJdbcTemplate().update(
										INSERT_PRODIODETAIL,
										new Object[] { null, rsx.getString("ve_ordercode"), rsx.getString("vad_prodcode"),
												rsx.getObject("ve_makedate"), qty, NumberUtil.formatDouble(qty * purcqty / veqty, 8), pdid,
												code, piclass, count++, 0, "ENTERING", piid, vad_id[0], rsx.getObject("vad_code"), whcode,
												price, rate, price * qty, whname[0], Integer.parseInt(de.get("ved_id").toString()), prid,
												whname[1], rsx.getObject("ve_sendcode"), vad_id[1], "", "", "", "", "", vad_id[2],
												vad_id[3], null, null,0,null });
								updateByCondition("QUA_VerifyApplyDetailDet",
										"ved_statuscode='TURNIN', ved_status='" + BaseUtil.getLocalMessage("TURNIN") + "',"
												+ (isok ? "ved_isok=1" : "ved_isng=1"), "ved_id=" + rsx.getInt("ved_id"));
								// 记录日志
								logger.turnDetail("转入库", "VerifyApplyDetail", "ve_id", de.get("ve_id"), rsx.getInt("ved_detno"));
								///23
							}
						}
					}
				}
			} else if ("工序委外".equals(intype)) {
				if(isDBSetting("VerifyApplyDetail!Deal","turnByDeliver")){
					rs = queryForRowSet(QUA_SENDOS_GROUP.replace("??", ids));
				}else{
					rs = queryForRowSet(QUA_MAKECRAFT_GROUP.replace("??", ids));
				}
				while (rs.next()) {
					vCode = rs.getString("mc_vendcode");
					curr = rs.getString("mc_currency");
					aCode = rs.getString("apvendcode");
					sendcode = rs.getString("ve_sendcode")==null ? " ":rs.getString("ve_sendcode");
					if (vCode != null && curr != null) {
						if(isDBSetting("VerifyApplyDetail!Deal","turnByDeliver")){
							rsx = queryForRowSet(QUA_SENDOS_ARG.replace("??", ids),sendcode);
						}else{
							rsx = queryForRowSet(QUA_MAKECRAFT_ARG.replace("??", ids), vCode, curr, aCode);
						}
						code = null;
						int count = 1;
						while (rsx.next()) {
							if (code == null) {
								vName = rsx.getString("mc_vendname");
								pay = rsx.getString("mc_payments");
								rate = rsx.getDouble("mc_rate");
								code = sGetMaxNumber(caller, 2);
								piid = getSeqId("PRODINOUT_SEQ");
								getJdbcTemplate().update(INSERT_BASEPRODINOUT, piid, code, code, SystemSession.getUser().getEm_name(),
										SystemSession.getUser().getEm_code(), time, BaseUtil.getLocalMessage("ENTERING"), "ENTERING",
										piclass, "UNPOST", BaseUtil.getLocalMessage("UNPOST"), vCode, vName, rsx.getString("apvendcode"),
										rsx.getString("ve_name"), pay, rsx.getString("mc_paymentscode"), curr, rate, (isok ? null : "OS"),
										rsx.getObject("ve_sendcode"), rsx.getObject("ve_code"), rsx.getObject("va_whman"),
										rsx.getObject("va_whmancode"), rsx.getObject("ve_cop"), rsx.getObject("ve_buyercode"),
										rsx.getObject("ve_buyerman"), intype);
								JSONObject j = new JSONObject();
								j.put("pi_id", piid);
								j.put("pi_inoutno", code);
								j.put("intype", intype);
								codes.add(j);
							}
							list = CollectionUtil.filter(maps, CollectionUtil.INCLUDE, "ved_id", rsx.getInt("ved_id"));
							if (list.size() == 1) {
								de = list.get(0);
								pdid = getSeqId("PRODIODETAIL_SEQ");
								price = rsx.getGeneralDouble("mc_price");
								qty = Double.parseDouble(de.get("qty").toString());
								String whcode = de.get("wh").toString();
								Object[] whname = getFieldsDataByCondition("Warehouse", new String[] { "wh_description", "wh_id" },
										"wh_code= '" + whcode + "'");
								if (whname == null) {
									BaseUtil.showError("仓库[" + whcode + "]不存在!");
								}
								Object[] vad_id = getFieldsDataByCondition("VerifyApplyDetail left join VerifyApply on vad_vaid=va_id",
										new String[] { "vad_id", "vad_batchcode", "vad_unitpackage", "vad_mantissapackage", "vad_mcid",
												"vad_jobcode" },
										"(va_code,vad_detno) in (select vad_code,vad_detno from qua_VerifyApplyDetail where ve_id" + "="
												+ de.get("ve_id") + ")");
								rate = rsx.getGeneralDouble("mc_taxrate");
								Object prid = getFieldDataByCondition("Product", "pr_id", "pr_code='" + rsx.getObject("vad_prodcode") + "'");
								getJdbcTemplate().update(
										INSERT_PRODIODETAIL,
										new Object[] { null, rsx.getString("ve_ordercode"), rsx.getString("vad_prodcode"),
												rsx.getObject("ve_makedate"), qty, qty, pdid, code, piclass, count++, 0, "ENTERING", piid,
												vad_id[0], rsx.getObject("vad_code"), whcode, price, rate, price * qty, whname[0],
												Integer.parseInt(de.get("ved_id").toString()), prid, whname[1],
												rsx.getObject("ve_sendcode"), vad_id[1], "", "", "", "", "", vad_id[2], vad_id[3],
												vad_id[4], vad_id[5],0,null });
								updateByCondition("QUA_VerifyApplyDetailDet",
										"ved_statuscode='TURNIN', ved_status='" + BaseUtil.getLocalMessage("TURNIN") + "',"
												+ (isok ? "ved_isok=1" : "ved_isng=1"), "ved_id=" + rsx.getInt("ved_id"));
								// 记录日志
								logger.turnDetail("转入库", "VerifyApplyDetail", "ve_id", de.get("ve_id"), rsx.getInt("ved_detno"));
							}
						}
					}
				}
			}
		}
		return codes;
	}
	/**
	 *  FQC转委外验收单   锤子科技
	 */
	@Override
	public List<JSONObject> OSdetailTurnStorageOs(String caller, String piclass, List<Map<Object, Object>> maps, boolean isok) {
		Timestamp time = Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS));
		// 按采购单供应商+币别+应付供应商分组
		String ids = BaseUtil.parseArray2Str(CollectionUtil.pluck(maps, "ved_id"), ",");
		SqlRowList type = queryForRowSet(QUA_TYPE_GROUP.replace("??", ids));
		SqlRowList rs = null;
		String vCode = null;
		String aCode = null;
		String vName = null;
		String curr = null;
		String pay = null;
		double rate = 0;
		int piid = 0;
		SqlRowList rsx = null;
		String code = null;
		int pdid = 0;
		double price = 0;
		double qty = 0;
		double veqty = 0;
		double purcqty = 0;
		List<Map<Object, Object>> list = null;
		Map<Object, Object> de = null;
		List<JSONObject> codes = new ArrayList<JSONObject>();
		while (type.next()) {
			String intype = type.getString("va_intype");
			if ("正常委外".equals(intype)) {
				rs = queryForRowSet(QUA_MAKE_GROUP.replace("??", ids));
				while (rs.next()) {
					vCode = rs.getString("ma_vendcode");
					curr = rs.getString("ma_currency");
					aCode = rs.getString("apvendcode");
					if (vCode != null && curr != null) {
						rsx = queryForRowSet(QUA_MAKE_ARG.replace("??", ids), vCode, curr, aCode);
						code = null;
						int count = 1;
						while (rsx.next()) {
							veqty = rsx.getGeneralDouble("vad_qty");
							purcqty = rsx.getGeneralDouble("ve_purcqty") == 0 ? veqty : rsx.getGeneralDouble("ve_purcqty");
							if (code == null) {
								vName = rsx.getString("ma_vendname");
								pay = rsx.getString("ma_payments");
								rate = rsx.getDouble("ma_rate");
								code = sGetMaxNumber(caller, 2);
								piid = getSeqId("PRODINOUT_SEQ");
								getJdbcTemplate().update(INSERT_BASEPRODINOUT, piid, code, code, SystemSession.getUser().getEm_name(),
										SystemSession.getUser().getEm_code(), time, BaseUtil.getLocalMessage("ENTERING"), "ENTERING",
										piclass, "UNPOST", BaseUtil.getLocalMessage("UNPOST"), vCode, vName, rsx.getString("apvendcode"),
										rsx.getString("ve_name"), pay, rsx.getString("ma_paymentscode"), curr, rate, (isok ? null : "OS"),
										rsx.getObject("ve_sendcode"), rsx.getObject("ve_code"), rsx.getObject("va_whman"),
										rsx.getObject("va_whmancode"), rsx.getObject("ve_cop"), rsx.getObject("ve_buyercode"),
										rsx.getObject("ve_buyerman"), intype);
								JSONObject j = new JSONObject();
								j.put("pi_id", piid);
								j.put("pi_inoutno", code);
								j.put("intype", intype);
								codes.add(j);
							}
							list = CollectionUtil.filter(maps, CollectionUtil.INCLUDE, "ved_id", rsx.getInt("ved_id"));
							if (list.size() == 1) {
								de = list.get(0);
								pdid = getSeqId("PRODIODETAIL_SEQ");
								price = rsx.getGeneralDouble("ma_price");
								qty = Double.parseDouble(de.get("qty").toString());
								String whcode = de.get("wh").toString();
								Object[] whname = getFieldsDataByCondition("Warehouse", new String[] { "wh_description", "wh_id" },
										"wh_code= '" + whcode + "'");
								if (whname == null) {
									BaseUtil.showError("仓库[" + whcode + "]不存在!");
								}
								rate = Double.parseDouble(rsx.getObject("ma_taxrate").toString());
								Object prid = getFieldDataByCondition("Product", "pr_id", "pr_code='" + rsx.getObject("vad_prodcode") + "'");
								getJdbcTemplate().update(
										INSERT_PRODIODETAIL,
										new Object[] { null, rsx.getString("ve_ordercode"), rsx.getString("vad_prodcode"),
												rsx.getObject("ve_makedate"), qty, NumberUtil.formatDouble(qty * purcqty / veqty, 8), pdid,
												code, piclass, count++, 0, "ENTERING", piid, 0, rsx.getObject("vad_code"), whcode,
												price, rate, price * qty, whname[0], Integer.parseInt(de.get("ved_id").toString()), prid,
												whname[1], rsx.getObject("ve_sendcode"), 0, "", "", "", "", "", 0,
												0, null, null,0,null });
								updateByCondition("QUA_VerifyApplyDetailDet",
										"ved_statuscode='TURNIN', ved_status='" + BaseUtil.getLocalMessage("TURNIN") + "',"
												+ (isok ? "ved_isok=1" : "ved_isng=1")+ ",ved_turnqty=" + qty, "ved_id=" + rsx.getInt("ved_id"));
								// 记录日志
								logger.turnDetail("msg.turnStorage", caller, "ve_id", de.get("ve_id"), rsx.getInt("ved_detno"));
							}
						}
					}
				}
			}
		}
		return codes;
	}
	/**
	 * 生产检验单批量转完工入库单
	 * 
	 * @param caller
	 * @param piclass
	 * @param maps
	 * @param employee
	 * @param language
	 * @return
	 */
	public List<JSONObject> turnFinish(String caller, String piclass, List<Map<Object, Object>> maps, boolean isok, JSONObject j) {
		List<JSONObject> codes = new ArrayList<JSONObject>();
		int detno = 1;
		for (Map<Object, Object> map : maps) {
			if (!isok)
				updateByCondition("prodinout", "pi_type='MAKE'", "pi_id=" + j.get("pi_id"));
			Object[] objs = getFieldsDataByCondition("QUA_VerifyApplyDetail left join QUA_VerifyApplyDetailDet on ve_id=ved_veid"
					+ " left join Product on vad_prodcode=pr_code", new String[] { "ve_ordercode", "vad_wccode", "vad_prodcode",
					"ve_batchcode", "pr_id" }, "ved_id=" + map.get("ved_id"));
			String pr_whcode = map.get("pr_whcode") == null ? map.get("wh").toString():map.get("pr_whcode").toString();
			execute(INSERT_DETAIL_N, new Object[] { getSeqId("PRODIODETAIL_SEQ"), j.get("pi_id"), j.get("pi_inoutno"), "完工入库单", detno++, 0,
					"ENTERING", map.get("qty"), objs[0], objs[1], objs[2], objs[3], objs[4], map.get("ved_id"),
					pr_whcode });
			updateByCondition("QUA_VerifyApplyDetailDet", "ved_statuscode='TURNIN', ved_status='" + BaseUtil.getLocalMessage("TURNIN")
					+ "'," + (isok ? "ved_isok=1" : "ved_isng=1") + ",ved_turnqty=" + map.get("_okqty"), "ved_id=" + map.get("ved_id"));
			// execute("update prodiodetail set (pd_custprodcode,pd_bgxh)=(select ma_companytype,ma_custprodcode from make where ma_code=pd_ordercode) where pd_inoutno='"+j.get("pi_inoutno")+"'"
			// + " and pd_piclass ='完工入库单'");
			// 记录日志

			// ma_tomadeqty为当前已转入完工入库单数量，ma_madeqty为已完工数量，(在完工入库单过账后，pd_qty会反馈到ma_madeqty)
			//反馈编号 2018050036
			updateByCondition("Make", "ma_tomadeqty=nvl(ma_tomadeqty,0)+" + map.get("qty"), "ma_code='" +  objs[0]+"'");
			logger.turn("msg.turnStorage", caller, "ve_id", map.get("ve_id"));
			codes.add(j);
		}
		execute("update prodiodetail set pd_whname=(select WH_DESCRIPTION from warehouse where pd_whcode=wh_code) where pd_piid="
				+ j.get("pi_id"));
		return codes;
	}

	@Override
	public void checkstatus(List<Map<Object, Object>> datas) {
		String ids = BaseUtil.parseArray2Str(CollectionUtil.pluck(datas, "ved_id"), ",");
		// 有些帐套根据明细已转数来判断是否能再转入库单
		// ved_okqty=nvl(ved_turnqty,0) or nvl(ved_turnqty,0)=0 or
		// nvl(ved_turnqty,0)<0 存在老数据没有更新到turnqty和初期转出来的负数据
		String errorDet = getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ve_code||'行'||ved_detno) from QUA_VerifyApplyDetailDet left join QUA_VerifyApplyDetail on ved_veid=ve_id where ved_id in ("
								+ ids
								+ ") and ved_statuscode<>'AUDITED' and (ved_okqty=nvl(ved_turnqty,0) or nvl(ved_turnqty,0)=0 or nvl(ved_turnqty,0)<0)",
						String.class);
		// 存在分批入库的情况..
		if (errorDet != null) {
			BaseUtil.showError("有检验单未审核通过,无法入库!" + errorDet);
		}
	}

	@Override
	public void updatesourceqty(int veid) {
		Object vaclass = getFieldDataByCondition("QUA_VerifyApplyDetail", "ve_class", "ve_id=" + veid);
		Object[] objs = getFieldsDataByCondition("QUA_VerifyApplyDetail", new String[] { "ve_orderdetno", "ve_ordercode", "vad_prodcode" },
				"ve_id=" + veid);
		Object[] qty = getFieldsDataByCondition("QUA_VerifyApplyDetailDet", new String[] { "sum(ved_checkqty)" }, "ved_veid=" + veid);
		if ("采购检验单".equals(vaclass.toString())) {
			updateByCondition("PurchaseDetail", "pd_totested=NVL(pd_totested,0)+" + qty[0], "pd_code='" + objs[1] + "' and pd_detno ="
					+ objs[0]);
		} else if ("委外检验单".equals(vaclass.toString())) {
			updateByCondition("Make", "ma_totested = NVL(ma_totested,0)+" + qty[0], "ma_code='" + objs[1] + "'");
		}
		updateByCondition("Product", "pr_totested =NVL(pr_totested,0)+" + qty[0], "pr_code='" + objs[2] + "'");
	}

	@Override
	public void resauditsourceqty(int veid) {
		Object vaclass = getFieldDataByCondition("QUA_VerifyApplyDetail", "ve_class", "ve_id=" + veid);
		Object[] objs = getFieldsDataByCondition("QUA_VerifyApplyDetail", new String[] { "ve_orderdetno", "ve_ordercode", "vad_prodcode" },
				"ve_id=" + veid);
		Object[] qty = getFieldsDataByCondition("QUA_VerifyApplyDetailDet", new String[] { "sum(ved_checkqty)" }, "ved_veid=" + veid
				+ " and ved_statuscode='TURNIN'");
		if ("采购检验单".equals(vaclass.toString())) {
			updateByCondition("PurchaseDetail", "pd_totested=NVL(pd_totested,0)-" + qty[0], "pd_code='" + objs[1] + "' and pd_detno ="
					+ objs[0]);
		} else if ("委外检验单".equals(vaclass.toString())) {
			updateByCondition("Make", "ma_totested = NVL(ma_totested,0)-" + qty[0], "ma_code='" + objs[1] + "'");
		}
		updateByCondition("Product", "pr_totested = NVL(pr_totested,0)-" + qty[0], "pr_code='" + objs[2] + "'");
	}

	/**
	 * 采购检验单、委外检验单删除时还原收料单数量
	 * 
	 * @param id
	 * @param employee
	 * @param language
	 */
	@Transactional
	private void deleteIQC(int id) {
		// 还原收料明细
		restoreVerify(id);
		deleteByCondition("QUA_VerifyApplyDetail", "ve_id=" + id);
	}

	/**
	 * 生产检验单删除时还原制造单数量
	 * 
	 * @param id
	 * @param employee
	 * @param language
	 */
	@Transactional
	private void deleteFQC(int id) {
		restoreVerify(id);
		SqlRowList rs = queryForRowSet("SELECT vad_vaid,vad_qty,ve_class from QUA_VerifyApplyDetail WHERE ve_id=?", id);
		if (rs.next()) {
			Object ma_id = rs.getObject(1);
			deleteByCondition("QUA_VerifyApplyDetail", "ve_id=" + id);
			// 锤子科技 委外单直接入捡FQC 删除时反写数量 17-11-09 maz
			if ("委外检验单".equals(rs.getObject("ve_class"))) {
				execute("update make set ma_toquaqty=(SELECT NVL(SUM(NVL(vad_qty,0)),0) FROM qua_verifyapplydetail WHERE ve_ordercode=ma_code AND ve_class ='委外检验单') where ma_id="
						+ ma_id);
			} else {
				execute("update make set ma_toquaqty=(SELECT NVL(SUM(NVL(vad_qty,0)),0) FROM qua_verifyapplydetail WHERE ve_ordercode=ma_code AND ve_class ='生产检验单') where ma_id="
						+ ma_id);
			}
			// 修改状态
			updateByCondition("Make", "ma_qcstatuscode='PARTQUA',ma_qcstatus='" + BaseUtil.getLocalMessage("PARTQUA") + "'", "ma_id="
					+ ma_id + " AND ma_toquaqty<ma_qty");
			updateByCondition("Make", "ma_qcstatuscode=null,ma_qcstatus=null", "ma_id=" + ma_id + " AND ma_toquaqty=0");
		}
	}

	@Override
	public int turnProdAbnormal(int id) {
		Key key = transferRepository.transfer("IQC!turnProdAbnormal", id);
		int pa_id = key.getId();
		Object[] qty = getFieldsDataByCondition("QUA_VerifyApplyDetailDet", new String[] { "sum(ved_checkqty)", "sum(ved_okqty)",
				"sum(ved_ngqty)" }, "ved_veid=" + id);
		updateByCondition("ProdAbnormal", "pa_checkqty=" + qty[0] + ",pa_ngqty=" + qty[2], "pa_id=" + pa_id);
		execute("update ProdAbnormal set pa_ngrate=nvl(pa_ngqty,0)/nvl(pa_checkqty,1) where pa_id=" + pa_id + " and nvl(pa_checkqty,0)<>0");
		return pa_id;
	}

	@Override
	public int turnT8DReport(int id) {
		Key key = transferRepository.transfer("IQC!turnT8DReport", id);
		return key.getId();
	}

	@Override
	public List<JSONObject> detailTurnStorageByVacode(String caller, String piclass, List<Map<Object, Object>> maps, boolean isok) {
		Timestamp time = Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS));
		// 按收料单号分组
		Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(maps, new Object[] { "vad_code" });
		// maz 2018030244 新增按照送货单号进行分组
		if(isDBSetting("VerifyApplyDetail!Deal","turnByDeliver")){
			groups = BaseUtil.groupsMap(maps, new Object[] { "ve_sendcode" });
		}
		List<Map<Object, Object>> items;
		int piid = 0;
		String code = null;
		int pdid = 0;
		double price = 0;
		double qty = 0;
		double veqty = 0;
		double purcqty = 0;
		List<JSONObject> codes = new ArrayList<JSONObject>();
		int count = 1;
		for (Object s : groups.keySet()) {
			code = null;
			items = groups.get(s);
			// 转入出入库主记录
			Integer ve_id = getFieldValue("Qua_verifyapplyDetailDet", "ved_veid", "ved_id=" + items.get(0).get("ved_id"), Integer.class);
			SqlRowList rs = queryForRowSet(
					"select verifyapply.va_vendcode va_vendcode, verifyapply.va_vendname va_vendname,verifyapply.va_receivecode va_receivecode,verifyapply.va_receivename va_receivename,verifyapply.va_paymentscode va_paymentscode,verifyapply.va_payments va_payments,verifyapply.va_currency va_currency,"
							+ "verifyapply.va_rate va_rate,Qua_verifyapplyDetail.ve_sendcode ve_sendcode,Qua_verifyapplyDetail.ve_code ve_code,verifyapply.va_whman va_whman,verifyapply.va_whmancode va_whmancode,Qua_verifyapplyDetail.ve_cop ve_cop, Qua_verifyapplyDetail.vad_code vad_code,"
							+ "Qua_verifyapplyDetail.vad_detno vad_detno,Qua_verifyapplyDetail.ve_buyercode ve_buyercode,Qua_verifyapplyDetail.ve_buyerman ve_buyerman from Qua_verifyapplyDetail left join verifyapply on Qua_verifyapplyDetail.vad_vaid=verifyapply.va_id where ve_id=?",
					ve_id);
			if (rs.next()) {
				if (code == null) {
					code = sGetMaxNumber(caller, 2);
					piid = getSeqId("PRODINOUT_SEQ");
					getJdbcTemplate().update(INSERT_BASEPRODINOUT, piid, code, code, SystemSession.getUser().getEm_name(),
							SystemSession.getUser().getEm_code(), time, BaseUtil.getLocalMessage("ENTERING"), "ENTERING", piclass,
							"UNPOST", BaseUtil.getLocalMessage("UNPOST"), rs.getObject("va_vendcode"), rs.getObject("va_vendname"),
							rs.getObject("va_receivecode"), rs.getObject("va_receivename"), rs.getObject("va_payments"),
							rs.getObject("va_paymentscode"), rs.getObject("va_currency"), rs.getObject("va_rate"), (isok ? null : "PURC"),
							rs.getObject("ve_sendcode"), rs.getObject("ve_code"), rs.getObject("va_whman"), rs.getObject("va_whmancode"),
							rs.getObject("ve_cop"), rs.getObject("ve_buyercode"), rs.getObject("ve_buyerman"), null);
					execute("update prodinout set pi_transport=(select ve_shipment from vendor where pi_cardcode=ve_code) where pi_id="
							+ piid);
					if (items.get(0).get("vad_code") != null && !"".equals(items.get(0).get("vad_code"))) {
						execute("update prodinout set pi_remark=(select va_remark from VerifyApply where va_code='"
								+ items.get(0).get("vad_code") + "') where pi_id=" + piid);
					}
					JSONObject j = new JSONObject();
					j.put("pi_id", piid);
					j.put("pi_inoutno", code);
					codes.add(j);
				}
				for (Map<Object, Object> map : items) {
					int ved_id = Integer.parseInt(map.get("ved_id").toString());
					qty = Double.parseDouble(map.get("qty").toString());
					String whcode = map.get("wh").toString();
					Object[] whname = getFieldsDataByCondition("Warehouse", new String[] { "wh_description", "wh_id" }, "wh_code= '"
							+ whcode + "'");
					if (whname == null) {
						BaseUtil.showError("仓库[" + whcode + "]不存在!");
					}
					SqlRowList ve = queryForRowSet(
							"select * from Qua_VerifyApplyDetail left join Qua_VerifyApplyDetailDet on ved_veid=ve_id where ved_id=?",
							ved_id);
					if (ve.next()) {
						SqlRowList rsx = queryForRowSet(
								"select * from VerifyApplyDetail left join VerifyApply on vad_vaid=va_id left join PurchaseDetail on vad_pucode=pd_code and vad_pudetno=pd_detno left join Product on vad_prodcode=pr_code where va_code=? and vad_detno=?",
								ve.getObject("vad_code"), ve.getGeneralInt("vad_detno"));
						if (rsx.next()) {
							pdid = getSeqId("PRODIODETAIL_SEQ");
							price = rsx.getObject("pd_price") == null ? 0 : Double.parseDouble(rsx.getObject("pd_price").toString());
							veqty = rsx.getGeneralDouble("vad_qty");
							purcqty = rsx.getGeneralDouble("ve_purcqty") == 0 ? veqty : rsx.getGeneralDouble("ve_purcqty");
							getJdbcTemplate()
									.update(INSERT_PRODIODETAIL,
											new Object[] { rsx.getInt("vad_pudetno") == -1 ? null : rsx.getInt("vad_pudetno"),
													rsx.getString("vad_pucode"), rsx.getString("vad_prodcode"),
													ve.getObject("ve_makedate"), qty, NumberUtil.formatDouble(qty * purcqty / veqty, 8),
													pdid, code, piclass, count++, 0, "ENTERING", piid, rsx.getGeneralInt("vad_id"),
													rsx.getObject("va_code"), whcode, price, rsx.getGeneralDouble("pd_rate"), price * qty,
													whname[0], ved_id, rsx.getGeneralInt("pr_id"), whname[1], ve.getObject("ve_sendcode"),
													rsx.getObject("vad_batchcode"), rsx.getObject("vad_custcode"),
													rsx.getObject("vad_custname"), rsx.getObject("vad_salecode"),
													rsx.getObject("vad_remark2"), rsx.getObject("vad_remark3"),
													rsx.getDouble("vad_unitpackage"), rsx.getObject("vad_mantissapackage"),
													rsx.getObject("vad_mcid"), rsx.getObject("vad_jobcode"),rsx.getGeneralDouble("pd_rebatesprice"),null });
							updateByCondition("QUA_VerifyApplyDetailDet",
									"ved_statuscode='TURNIN', ved_status='" + BaseUtil.getLocalMessage("TURNIN") + "',"
											+ (isok ? "ved_isok=1" : "ved_isng=1"), "ved_id=" + ved_id);
							// 记录日志
							logger.turnDetail("转入库", "VerifyApplyDetail", "ve_id", ve.getGeneralInt("ve_id"), ve.getGeneralInt("ved_detno"));
						}
					}
				}
			}
		}
		return codes;
	}

	@Override
	public List<JSONObject> detailTurnStorageOsByVacode(String caller, String piclass, List<Map<Object, Object>> maps, boolean isok) {
		Timestamp time = Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS));
		// 按收料单号分组
		Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(maps, new Object[] { "vad_code" });
		// maz 2018030244 新增按照送货单号进行分组
		if(isDBSetting("VerifyApplyDetail!Deal","turnByDeliver")){
			groups = BaseUtil.groupsMap(maps, new Object[] { "ve_sendcode" });
		}
		List<Map<Object, Object>> items;
		int piid = 0;
		String code = null;
		int pdid = 0;
		double price = 0;
		double qty = 0;
		double veqty = 0;
		double purcqty = 0;
		List<JSONObject> codes = new ArrayList<JSONObject>();
		int count = 1;
		/**
		 * 同一收料单中的委外加工单的应付供应商或币别若不一致，则限制转单，提示选择其他分组方式
		 */
		for (Object s : groups.keySet()) {
			items = groups.get(s);
			String ids = CollectionUtil.pluckSqlString(items, "ved_id");
			SqlRowList rs = queryForRowSet("select apvendcode from (select nvl(nvl(ma_apvendcode,ve_apvendcode),ma_vendcode) apvendcode ,ved_id,ma_code"
					+ " from qua_verifyapplydetail left join qua_verifyapplydetaildet on ved_veid=ve_id left join verifyapply "
					+ " on verifyapply.va_code=qua_verifyapplydetail.vad_code left join verifyapplydetail on "
					+ " verifyapplydetail.vad_vaid=verifyapply.va_id and verifyapplydetail.vad_detno=qua_verifyapplydetail.vad_detno"
					+ " left join make on ma_code=vad_pucode left join vendor on vendor.ve_code=ma_vendcode  "
					+ " where qua_verifyapplydetaildet.ved_id in("
					+ ids
					+ ") and nvl(va_intype,'正常委外')='正常委外')t group by apvendcode having count(apvendcode)<" + items.size());
			if (rs.next()) {
				BaseUtil.showError("同一收料单号存在应付供应商不一致的委外加工单，请选择其他分组方式");
			}
			rs = queryForRowSet("select apvendcode from (select mc_vendcode apvendcode,ved_id,mc_code"
					+ " from qua_verifyapplydetail left join qua_verifyapplydetaildet on ved_veid=ve_id left join verifyapply "
					+ " on verifyapply.va_code=qua_verifyapplydetail.vad_code left join verifyapplydetail on "
					+ " verifyapplydetail.vad_vaid=verifyapply.va_id and verifyapplydetail.vad_detno=qua_verifyapplydetail.vad_detno"
					+ " left join makecraft on mc_code=vad_pucode where qua_verifyapplydetaildet.ved_id in(" + ids
					+ ") and nvl(va_intype,'工序委外')='工序委外')t group by apvendcode having count(apvendcode)<" + items.size());
			if (rs.next()) {
				BaseUtil.showError("同一收料单号存在委外商不一致的工序委外单，请选择其他分组方式");
			}
			SqlRowList rs2 = queryForRowSet("select ma_currency from (select ma_currency,ved_id,ma_code"
					+ " from qua_verifyapplydetail left join qua_verifyapplydetaildet on ved_veid=ve_id left join verifyapply"
					+ " on verifyapply.va_code=qua_verifyapplydetail.vad_code left join verifyapplydetail on"
					+ " verifyapplydetail.vad_vaid=verifyapply.va_id and verifyapplydetail.vad_detno=qua_verifyapplydetail.vad_detno"
					+ " left join make on ma_code=vad_pucode where qua_verifyapplydetaildet.ved_id in(" + ids
					+ ") and nvl(va_intype,'正常委外')='正常委外')t group by ma_currency having count(ma_currency)<" + items.size());
			if (rs2.next()) {
				BaseUtil.showError("同一收料单号存在币别不一致的委外加工单，请选择其他分组方式");
			}
			rs2 = queryForRowSet("select mc_currency from (select mc_currency,ved_id,mc_code"
					+ " from qua_verifyapplydetail left join qua_verifyapplydetaildet on ved_veid=ve_id left join verifyapply"
					+ " on verifyapply.va_code=qua_verifyapplydetail.vad_code left join verifyapplydetail on"
					+ " verifyapplydetail.vad_vaid=verifyapply.va_id and verifyapplydetail.vad_detno=qua_verifyapplydetail.vad_detno"
					+ " left join makecraft on mc_code=vad_pucode where qua_verifyapplydetaildet.ved_id in(" + ids
					+ ") and nvl(va_intype,'正常委外')='正常委外')t group by mc_currency having count(mc_currency)<" + items.size());
			if (rs2.next()) {
				BaseUtil.showError("同一收料单号存在币别不一致的工序委外单，请选择其他分组方式");
			}
		}
		for (Object s : groups.keySet()) {
			code = null;
			items = groups.get(s);
			String intype = null;
			// 转入出入库主记录
			Integer ve_id = getFieldValue("Qua_verifyapplyDetailDet", "ved_veid", "ved_id=" + items.get(0).get("ved_id"), Integer.class);
			SqlRowList rs = queryForRowSet(
					"select verifyapply.va_vendcode va_vendcode, verifyapply.va_vendname va_vendname,verifyapply.va_receivecode va_receivecode,verifyapply.va_receivename va_receivename,verifyapply.va_paymentscode va_paymentscode,verifyapply.va_payments va_payments,verifyapply.va_currency va_currency,verifyapply.va_rate va_rate,"
							+ "Qua_verifyapplyDetail.ve_sendcode ve_sendcode,Qua_verifyapplyDetail.ve_code ve_code,verifyapply.va_whman va_whman,verifyapply.va_whmancode va_whmancode,Qua_verifyapplyDetail.ve_cop ve_cop, Qua_verifyapplyDetail.vad_code vad_code, Qua_verifyapplyDetail.vad_detno vad_detno,"
							+ "Qua_verifyapplyDetail.ve_buyercode ve_buyercode,Qua_verifyapplyDetail.ve_buyerman ve_buyerman,nvl(verifyapply.va_intype,'正常委外') va_intype,Qua_verifyapplyDetail.vad_mcid vad_mcid,nvl(Qua_verifyapplyDetail.ve_purcqty,0) ve_purcqty,nvl(Qua_verifyapplyDetail.vad_qty,0) vad_qty "
							+ "from Qua_verifyapplyDetail left join verifyapply on Qua_verifyapplyDetail.vad_vaid=verifyapply.va_id where ve_id=?",
					ve_id);
			if (rs.next()) {
				if (code == null) {
					code = sGetMaxNumber(caller, 2);
					piid = getSeqId("PRODINOUT_SEQ");
					intype = rs.getGeneralString("va_intype");
					veqty = rs.getGeneralDouble("vad_qty");
					purcqty = rs.getGeneralDouble("ve_purcqty") == 0 ? veqty : rs.getGeneralDouble("ve_purcqty");
					getJdbcTemplate().update(INSERT_BASEPRODINOUT, piid, code, code, SystemSession.getUser().getEm_name(),
							SystemSession.getUser().getEm_code(), time, BaseUtil.getLocalMessage("ENTERING"), "ENTERING", piclass,
							"UNPOST", BaseUtil.getLocalMessage("UNPOST"), rs.getObject("va_vendcode"), rs.getObject("va_vendname"),
							rs.getObject("va_receivecode"), rs.getObject("va_receivename"), rs.getObject("va_payments"),
							rs.getObject("va_paymentscode"), rs.getObject("va_currency"), rs.getObject("va_rate"), (isok ? null : "OS"),
							rs.getObject("ve_sendcode"), rs.getObject("ve_code"), rs.getObject("va_whman"), rs.getObject("va_whmancode"),
							rs.getObject("ve_cop"), rs.getObject("ve_buyercode"), rs.getObject("ve_buyerman"), intype);
					execute("update prodinout set pi_transport=(select ve_shipment from vendor where pi_cardcode=ve_code) where pi_id="
							+ piid);
					JSONObject j = new JSONObject();
					j.put("pi_id", piid);
					j.put("pi_inoutno", code);
					j.put("intype", intype);
					codes.add(j);
				}
				for (Map<Object, Object> map : items) {
					int ved_id = Integer.parseInt(map.get("ved_id").toString());
					qty = Double.parseDouble(map.get("qty").toString());
					String whcode = map.get("wh").toString();
					Object[] whname = getFieldsDataByCondition("Warehouse", new String[] { "wh_description", "wh_id" }, "wh_code= '"
							+ whcode + "'");
					if (whname == null) {
						BaseUtil.showError("仓库[" + whcode + "]不存在!");
					}
					SqlRowList ve = queryForRowSet(
							"select * from Qua_VerifyApplyDetail left join Qua_VerifyApplyDetailDet on ved_veid=ve_id where ved_id=?",
							ved_id);
					if (ve.next()) {
						SqlRowList rsx = queryForRowSet(
								"select * from VerifyApplyDetail left join VerifyApply on vad_vaid=va_id left join Make on vad_pucode=ma_code left join Product on vad_prodcode=pr_code where va_code=? and vad_detno=? and ma_tasktype='OS'",
								ve.getObject("vad_code"), ve.getGeneralInt("vad_detno"));
						if (rsx.next()) {
							pdid = getSeqId("PRODIODETAIL_SEQ");
							price = Double.parseDouble(rsx.getObject("ma_price").toString());
							getJdbcTemplate().update(
									INSERT_PRODIODETAIL,
									new Object[] { rsx.getInt("vad_pudetno"), rsx.getString("vad_pucode"), rsx.getString("vad_prodcode"),
											ve.getObject("ve_makedate"), qty, NumberUtil.formatDouble(qty * purcqty / veqty, 8), pdid,
											code, piclass, count++, 0, "ENTERING", piid, rsx.getGeneralInt("vad_id"),
											rsx.getObject("va_code"), whcode, price, rsx.getGeneralDouble("ma_taxrate"), price * qty,
											whname[0], ved_id, rsx.getGeneralInt("pr_id"), whname[1], ve.getObject("ve_sendcode"),
											rsx.getObject("vad_batchcode"), rsx.getObject("vad_custcode"), rsx.getObject("vad_custname"),
											rsx.getObject("vad_salecode"), rsx.getObject("vad_remark2"), rsx.getObject("vad_remark3"),
											rsx.getGeneralDouble("vad_unitpackage"), rsx.getGeneralDouble("vad_mantissapackage"),
											rsx.getObject("vad_mcid"), rsx.getObject("vad_jobcode"),0,null });
							updateByCondition("QUA_VerifyApplyDetailDet",
									"ved_statuscode='TURNIN', ved_status='" + BaseUtil.getLocalMessage("TURNIN") + "',"
											+ (isok ? "ved_isok=1" : "ved_isng=1"), "ved_id=" + ved_id);
							// 记录日志
							logger.turnDetail("转入库", "VerifyApplyDetail", "ve_id", ve.getGeneralInt("ve_id"), ve.getGeneralInt("ved_detno"));
						}
					}
				}
			}
		}
		return codes;
	}
}
