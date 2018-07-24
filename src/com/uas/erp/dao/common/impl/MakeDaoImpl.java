package com.uas.erp.dao.common.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.MakeDao;
import com.uas.erp.model.MessageLog;

@Repository
public class MakeDaoImpl extends BaseDao implements MakeDao {
	final static String INSERT_PRODIO = "INSERT INTO ProdInOut(pi_id, pi_inoutno, pi_date, pi_class, pi_invostatus, pi_invostatuscode,"
			+ "pi_recordman, pi_recorddate, pi_cardcode, pi_title, pi_whcode, pi_whname, pi_status, pi_statuscode,pi_updatedate,pi_updateman) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static String INSERT_PRODIO_WH = "INSERT INTO ProdInOut(pi_id, pi_inoutno, pi_date, pi_class, pi_invostatus, pi_invostatuscode,"
			+ "pi_recordman, pi_recorddate, pi_whcode, pi_whname, pi_status, pi_statuscode,pi_updatedate,pi_updateman,pi_printstatuscode,pi_printstatus,pi_intype) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static String INSERT_PRODIO_VE = "INSERT INTO ProdInOut(pi_id, pi_inoutno, pi_date, pi_class, pi_invostatus, pi_invostatuscode,"
			+ "pi_recordman, pi_recorddate, pi_status, pi_statuscode,pi_updatedate,pi_updateman,pi_cardcode,pi_title,pi_receivecode,pi_receivename,pi_intype) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static String INSERT_PRODIO_WH_VE = "INSERT INTO ProdInOut(pi_id, pi_inoutno, pi_date, pi_class, pi_invostatus, pi_invostatuscode,"
			+ "pi_recordman, pi_recorddate, pi_whcode, pi_whname, pi_status, pi_statuscode,pi_updatedate,pi_updateman,pi_cardcode,pi_title,pi_printstatuscode,pi_printstatus,pi_receivecode,pi_receivename,pi_intype) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static String INSERT_DETAIL = "INSERT INTO ProdIODetail(pd_id, pd_piid, pd_inoutno, pd_piclass, pd_pdno, pd_status,pd_auditstatus,pd_prodcode, pd_outqty,"
			+ "pd_ordercode, pd_orderdetno, pd_plancode, pd_wccode, pd_orderid, pd_prodid, pd_whcode, pd_whname,pd_pocode,pd_location) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static String INSERT_DETAIL_R = "INSERT INTO ProdIODetail(pd_id, pd_piid, pd_inoutno, pd_piclass, pd_pdno, pd_status,pd_auditstatus,pd_prodcode, pd_inqty,"
			+ "pd_ordercode, pd_orderdetno, pd_plancode, pd_wccode, pd_orderid, pd_prodid,pd_whcode,pd_whname) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static String INSERT_DETAIL_F = "INSERT INTO ProdIODetail(pd_id, pd_piid, pd_inoutno, pd_piclass, pd_pdno, pd_status,pd_auditstatus, pd_inqty,"
			+ "pd_ordercode, pd_wccode, pd_orderid, pd_prodcode, pd_batchcode, pd_price, pd_prodid) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static String INSERT_DETAIL_F2 = "INSERT INTO ProdIODetail(pd_id, pd_piid, pd_inoutno, pd_piclass, pd_pdno, pd_status,pd_auditstatus, pd_inqty,"
			+ "pd_ordercode, pd_wccode, pd_orderid, pd_prodcode, pd_batchcode, pd_price, pd_prodid,pd_plancode) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	static final String TURNMAKE = "SELECT ma_code,ma_salecode,ma_saledetno,ma_saledetailid,ma_requiredate,ma_prodid,ma_prodcode,ma_planenddate,"
			+ "ma_planbegindate,ma_qty,ma_custid,ma_custcode,ma_custname,ma_remark,ma_bomid,ma_date,ma_vendcode,ma_vendname,ma_currency,ma_rate,ma_paymentscode,ma_payments,ma_salecode FROM make WHERE ma_id=?";
	static final String INSERTMAKE = "INSERT INTO make(ma_id,ma_code,ma_source,ma_sourcecode,ma_salecode,ma_saledetno,ma_saledetailid,ma_requiredate,ma_prodid,ma_prodcode,"
			+ "ma_planenddate,ma_planbegindate,ma_qty,ma_custid,ma_custcode,ma_custname,ma_remark,ma_bomid,ma_statuscode,ma_status,ma_date,ma_recorddate,ma_recorderid,"
			+ "ma_recorder,ma_updatedate,ma_updateman,ma_checkstatuscode,ma_checkstatus,ma_tasktype) VALUES (?,?,?,?,?,?,?,?,?,??,?,?,?,?,?,?,?,?,??,?,?,?,?,?,?,?)";
	static final String TURNMAKEMATERIAL = "SELECT mm_code,mm_prodid,mm_prodcode,mm_prodname,mm_prodspec,mm_produnit,mm_oneuseqty,mm_qty,mm_repprodcode,mm_balance,mm_supplytype,mm_havegetqty,"
			+ "mm_canuserepqty,mm_scrapqty,mm_haverepqty,mm_addqty,mm_repqty,mm_whid,mm_whcode,mm_wccode,mm_wcid,mm_maid FROM makematerial WHERE mm_id=?";
	static final String INSERTMAKEMATERIAL = "INSERT INTO makematerial(mm_id,mm_maid,mm_code,mm_detno,mm_prodid,mm_prodcode,mm_prodname,mm_prodspec,mm_produnit,mm_oneuseqty,mm_qty,"
			+ "mm_repprodcode,mm_balance,mm_supplytype,mm_havegetqty,mm_canuserepqty,mm_lostqty,mm_haverepqty,mm_addqty,mm_repqty,mm_whid,mm_whcode,mm_wccode,mm_wcid,mm_status)"
			+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	final static String INSERT_QUAVERIFY = "INSERT INTO QUA_VerifyApplyDetail(ve_id,ve_code,ve_date,ve_type,ve_class,vad_vaid,vad_code,"
			+ "vad_prodcode, vad_qty, vad_vendcode, vad_vendname, vad_sourceid, vad_sourcecode,vad_sourcedetno,ve_statuscode,ve_status,"
			+ "ve_indate,ve_recorder,vad_class, ve_ordercode, vad_wccode,ve_printstatus,ve_license,ve_linename,ve_batchcode) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String INSERQUA_VERIFYAPPLYDETAILDET = "insert into QUA_VerifyApplyDetailDet(ved_id,ved_veid,ved_detno,ved_okqty,"
			+ "ved_date,ved_testman,ved_checkdate,ved_checkqty,ved_status,ved_statuscode,ved_code) VALUES (?,?,?,?,?,?,?,?,?,?,?)";

	static final String INSERTVERIFYAPPLY = "INSERT INTO verifyapply(va_id, va_code, va_statuscode, va_status, va_recorder,va_indate,va_date,"
			+ "va_vendcode,va_vendname,va_receivecode,va_receivename,va_class,va_paymentscode,va_payments,va_currency,va_rate) values (?,?,?,?,?,sysdate,sysdate,?,?,?,?,?,?,?,?,?)";
	static final String INSERTVERIFYAPPLYDETAIL = "INSERT INTO verifyapplydetail(vad_id, vad_vaid,vad_code,vad_detno,vad_class,vad_prodcode,"
			+ "vad_qty,vad_pudate,vad_sourcecode,vad_pucode,vad_status,vad_vendcode,vad_vendname,vad_remark,vad_salecode) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String GETVENDER = " select ve_apvendcode,ve_apvendname from vendor where ve_code=?";

	/**
	 * 修改工单状态
	 */
	public void changeMakeStatus(String code, int mmid) {
		setThisQty(mmid, null, null);
		updateByCondition("MakeMaterial", "mm_status='PARTGET'", "mm_id=" + mmid
				+ " AND (mm_thisqty > 0 or mm_id in (select mp_mmid from makematerialreplace where mp_mmid=mm_id and mp_thisqty>0))");
		updateByCondition("MakeMaterial", "mm_status='TURNGET'", "mm_id=" + mmid
				+ " AND (mm_thisqty = 0 and mm_id not in (select mp_mmid from makematerialreplace where mp_mmid=mm_id and mp_thisqty>0))");
		int ma_id = getFieldValue("MakeMaterial", "mm_maid", "mm_id=" + mmid, Integer.class);
		updateMakeGetStatus(String.valueOf(ma_id));
	}

	/**
	 * 修改工单领料状态，根据所有明细的领料情况更新make表的领料状态 by zhongyl20150422
	 */
	@Override
	public void changeMakeGetStatus(Integer ma_id) {
		setThisQty(null, ma_id, null);
		updateMakeGetStatus(ma_id.toString());
		execute("update make set ma_finishstatuscode='COMPLETED',ma_finishstatus='" + BaseUtil.getLocalMessage("COMPLETED")
				+ "' where ma_id=" + ma_id + " and ma_madeqty>=ma_qty  ");
		execute("update make set ma_finishstatuscode='PARTFI',ma_finishstatus='" + BaseUtil.getLocalMessage("PARTFI") + "' where ma_id="
				+ ma_id + " and ma_madeqty>0 and ma_madeqty<ma_qty  ");
		execute("update make set ma_finishstatuscode='UNCOMPLET',ma_finishstatus='" + BaseUtil.getLocalMessage("UNCOMPLET")
				+ "' where ma_id=" + ma_id + " and ma_madeqty=0  ");
	}

	/**
	 * 修改工单领料状态
	 */
	@Override
	public void updateMakeGetStatus(String ids) {
		execute("update make set ma_turnstatuscode='UNGET',ma_turnstatus='" + BaseUtil.getLocalMessage("UNGET") + "' where ma_id in ("
				+ ids + ") and not exists(select 1 from makematerial where mm_maid=ma_id and NVL(mm_havegetqty,0)+NVL(mm_totaluseqty,0)>0)");
		execute("update make set ma_turnstatuscode='PARTGET',ma_turnstatus='" + BaseUtil.getLocalMessage("PARTGET") + "' where ma_id in ("
				+ ids + ") and nvl(ma_turnstatuscode,' ')<>'PARTGET' and " + "exists (select 1 from makematerial where mm_maid=ma_id "
				+ "and NVL(mm_havegetqty,0)+NVL(mm_totaluseqty,0)+NVL(mm_turnaddqty,0)>0 and " + "NVL(mm_materialstatus,' ')=' ')");
		execute("update make set ma_turnstatuscode='TURNGET',ma_turnstatus='" + BaseUtil.getLocalMessage("TURNGET") + "' where ma_id in ("
				+ ids + ") and nvl(ma_turnstatuscode,' ')<>'TURNGET' and " + "not exists(select 1 from makematerial where mm_maid=ma_id "
				+ "and nvl(MM_QTY,0)-NVL(mm_havegetqty,0)-NVL(mm_totaluseqty,0)"
				+ "-NVL(mm_turnaddqty,0)>0 and NVL(mm_materialstatus,' ')=' ')");
	}

	/**
	 * 制造单转生产领料单 委外单转委外领料单
	 * 
	 * @return 领料单号
	 */
	@Override
	@Transactional
	public void turnOut(String inoutno, int id, int detno, int mmid, int mmdetno, double qty, String whcode, String piclass, String caller) {
		Object[] pis = getFieldsDataByCondition("ProdInOut", "pi_id,pi_whcode,pi_whname", "pi_inoutno='" + inoutno + "' and pi_class='"
				+ piclass + "'");
		Object[] objs = null;
		Object[] pr = null;
		if (whcode == null || whcode.equals("")) {
			whcode = pis[1].toString();
		}
		Object whname = getFieldDataByCondition("WareHouse", "wh_description", "wh_code='" + whcode + "'");
		// 获取制造单，委外单的订单号
		Object pd_pocode = getFieldDataByCondition("make left join makematerial on mm_maid=ma_id", "nvl(ma_salecode,'')",
				"mm_id=" + Math.abs(mmid));
		if (mmid < 0) {// 替代料mp_mmid
			mmid = Math.abs(mmid);
			objs = getFieldsDataByCondition("MakeMaterialReplace", new String[] { "mp_mmcode", "mp_mmdetno", "mp_prodcode" }, "mp_mmid="
					+ mmid + " AND mp_detno=" + mmdetno);
			pr = getFieldsDataByCondition("Product", "pr_id,pr_location", "pr_code='" + objs[2] + "'");
			execute(INSERT_DETAIL, new Object[] { getSeqId("PRODIODETAIL_SEQ"), id, inoutno, piclass, detno, 0, "ENTERING", objs[2], qty,
					objs[0], objs[1], null, null, null, pr[0], whcode, whname, pd_pocode, pr[1] });
			updateByCondition("MakeMaterialReplace", "mp_repqty=nvl(mp_repqty,0)+" + qty, "mp_mmid=" + mmid + " AND mp_detno=" + mmdetno);
			// 修改状态
			changeMakeStatus(objs[0].toString(), mmid);
		} else {
			objs = getFieldsDataByCondition("MakeMaterial", new String[] { "mm_code", "mm_mdcode", "mm_prodcode", "mm_wccode" }, "mm_id="
					+ mmid);
			pr = getFieldsDataByCondition("Product", "pr_id,pr_location", "pr_code='" + objs[2] + "'");
			execute(INSERT_DETAIL, new Object[] { getSeqId("PRODIODETAIL_SEQ"), pis[0], inoutno, piclass, detno, 0, "ENTERING", objs[2],
					qty, objs[0], mmdetno, objs[1], objs[3], mmid, pr[0], whcode, whname, pd_pocode, pr[1] });
			if ("生产领料单".equals(piclass) || "委外领料单".equals(piclass)) {
				updateByCondition("MakeMaterial", "mm_totaluseqty=nvl(mm_totaluseqty,0)+" + qty, "mm_id=" + mmid);
			} else if ("生产补料单".equals(piclass) || "委外补料单".equals(piclass)) {
				updateByCondition("MakeMaterial", "mm_turnaddqty=nvl(mm_turnaddqty,0)+" + qty, "mm_id=" + mmid);
			}
			// 修改状态
			changeMakeStatus(objs[0].toString(), mmid);
		}
		execute("update prodiodetail set (pd_mcid,pd_jobcode)=(select mc_id,mm_mdcode from MakeMaterial,makecraft where pd_orderdetno=mm_detno and pd_ordercode=mm_code and mm_mdcode=mc_code) where pd_piid="
				+ pis[0]);
	}

	/**
	 * 制造单转生产退料单 委外单转委外退料单
	 */
	@Override
	@Transactional
	public void turnIn(String inoutno, int id, int detno, int mmid, double qty, String whcode, String piclass, String caller) {
		Object[] objs = getFieldsDataByCondition("Make left join MakeMaterial on ma_id=mm_maid", new String[] { "mm_id", "mm_code",
				"mm_detno", "mm_mdcode", "mm_prodcode", "mm_wccode" }, "mm_id=" + Math.abs(mmid));
		Object whname = getFieldDataByCondition("WareHouse", "wh_description", "wh_code='" + whcode + "'");
		Object prid = getFieldDataByCondition("Product", "pr_id", "pr_code='" + objs[4] + "'");
		execute(INSERT_DETAIL_R, new Object[] { getSeqId("PRODIODETAIL_SEQ"), id, inoutno, piclass, detno, 0, "ENTERING", objs[4], qty,
				objs[1], objs[2], objs[3], objs[5], objs[0], prid, whcode, whname });
		execute("update prodiodetail set (pd_mcid,pd_jobcode)=(select mc_id,mm_mdcode from MakeMaterial,makecraft where pd_orderdetno=mm_detno and pd_ordercode=mm_code and mm_mdcode=mc_code) where pd_piid="
				+ id);
		execute("update prodiodetail set pd_purcinqty=Round(Pd_Inqty/nvl((Select case when nvl(Pr_Purcrate,0)=0 then 1 else Pr_Purcrate end From Product Where Pd_Prodcode=Pr_Code),1),8) where pd_piid="
				+ id);
	}

	/**
	 * {出入库明细} 已按仓库分组
	 */
	@Transactional
	public void turnOutWh(String no, int detno, String piclass, int mmid, int mmdetno, double qty) {
		Object[] pis = getFieldsDataByCondition("ProdInOut", "pi_id,pi_whcode,pi_whname", "pi_inoutno='" + no + "' AND pi_class='"
				+ piclass + "'");
		Object[] objs = null;
		Object[] pr = null;
		// 获取制造单，委外单的订单号
		Object pd_pocode = getFieldDataByCondition("make left join makematerial on mm_maid=ma_id", "nvl(ma_salecode,'')",
				"mm_id=" + Math.abs(mmid));
		if (mmid < 0) {// 替代料mp_mmid
			mmid = Math.abs(mmid);
			objs = getFieldsDataByCondition("MakeMaterialReplace", new String[] { "mp_mmcode", "mp_mmdetno", "mp_prodcode" }, "mp_mmid="
					+ mmid + " AND mp_detno=" + mmdetno);
			pr = getFieldsDataByCondition("Product", "pr_id,pr_location", "pr_code='" + objs[2] + "'");
			execute(INSERT_DETAIL, new Object[] { getSeqId("PRODIODETAIL_SEQ"), pis[0], no, piclass, detno, 0, "ENTERING", objs[2], qty,
					objs[0], objs[1], null, null, mmid, pr[0], pis[1], pis[2], pd_pocode, pr[1] });
			updateByCondition("MakeMaterialReplace", "mp_repqty=nvl(mp_repqty,0)+" + qty, "mp_mmid=" + mmid + " AND mp_detno=" + mmdetno);
			// 修改状态
			changeMakeStatus(objs[0].toString(), mmid);
		} else {
			objs = getFieldsDataByCondition("MakeMaterial", new String[] { "mm_code", "mm_mdcode", "mm_prodcode", "mm_wccode" }, "mm_id="
					+ mmid);
			pr = getFieldsDataByCondition("Product", "pr_id,pr_location", "pr_code='" + objs[2] + "'");
			execute(INSERT_DETAIL, new Object[] { getSeqId("PRODIODETAIL_SEQ"), pis[0], no, piclass, detno, 0, "ENTERING", objs[2], qty,
					objs[0], mmdetno, objs[1], objs[3], mmid, pr[0], pis[1], pis[2], pd_pocode, pr[1] });
			if ("生产领料单".equals(piclass) || "委外领料单".equals(piclass)) {
				updateByCondition("MakeMaterial", "mm_totaluseqty=nvl(mm_totaluseqty,0)+" + qty, "mm_id=" + mmid);
			} else if ("生产补料单".equals(piclass) || "委外补料单".equals(piclass)) {
				updateByCondition("MakeMaterial", "mm_turnaddqty=nvl(mm_turnaddqty,0)+" + qty, "mm_id=" + mmid);
			}
			// 修改状态
			changeMakeStatus(objs[0].toString(), mmid);
		}
		execute("update prodiodetail set (pd_mcid,pd_jobcode)=(select mc_id,mm_mdcode from MakeMaterial,makecraft where pd_orderdetno=mm_detno and pd_ordercode=mm_code and mm_mdcode=mc_code) where pd_piid="
				+ pis[0]);
	}

	/**
	 * 生产退料单 已按仓库分组
	 */
	@Override
	@Transactional
	public void turnInWh(String no, int detno, int mmid, int mmdetno, double qty, String pi_class) {
		Object[] pis = getFieldsDataByCondition("ProdInOut", "pi_id,pi_whcode,pi_whname", "pi_inoutno='" + no + "' AND pi_class='"
				+ pi_class + "'");
		Object[] objs = null;
		Object prid = null;
		if (mmid < 0) {// 替代料mp_mmid
			mmid = Math.abs(mmid);
			objs = getFieldsDataByCondition("MakeMaterialReplace", new String[] { "mp_mmcode", "mp_mmdetno", "mp_prodcode" }, "mp_mmid="
					+ mmid + " AND mp_detno=" + mmdetno);
			prid = getFieldDataByCondition("Product", "pr_id", "pr_code='" + objs[2] + "'");
			execute(INSERT_DETAIL_R, new Object[] { getSeqId("PRODIODETAIL_SEQ"), pis[0], no, pi_class, detno, 0, "ENTERING", objs[2], qty,
					objs[0], objs[1], null, null, mmid, prid, pis[1], pis[2] });
			updateByCondition("MakeMaterialReplace", "mp_backqty=nvl(mp_backqty,0)+" + qty, "mp_mmid=" + mmid + " AND mp_detno=" + mmdetno);
		} else {
			objs = getFieldsDataByCondition("MakeMaterial", new String[] { "mm_code", "mm_mdcode", "mm_prodcode", "mm_wccode", "mm_maid" },
					"mm_id=" + mmid);
			prid = getFieldDataByCondition("Product", "pr_id", "pr_code='" + objs[2] + "'");
			execute(INSERT_DETAIL_R, new Object[] { getSeqId("PRODIODETAIL_SEQ"), pis[0], no, pi_class, detno, 0, "ENTERING", objs[2], qty,
					objs[0], mmdetno, objs[1], objs[3], mmid, prid, pis[1], pis[2] });
			updateByCondition("MakeMaterial", "mm_backqty=nvl(mm_backqty,0)+" + qty, "mm_id=" + mmid);
			// 修改状态
			setThisQty(mmid, null, null);
			//updateByCondition("MakeMaterial", "mm_status='AUDITED',mm_backqty=nvl(mm_backqty,0) + " + qty, "mm_id=" + mmid);
			updateByCondition("MakeMaterial", "mm_status='PARTGET'", "mm_id=" + mmid + " AND mm_thisqty > 0");
			int ma_id = getFieldValue("MakeMaterial", "mm_maid", "mm_code='" + objs[0] + "'", Integer.class);
			updateMakeGetStatus(String.valueOf(ma_id));
		}
		execute("update prodiodetail set (pd_mcid,pd_jobcode)=(select mc_id,mm_mdcode from MakeMaterial,makecraft where pd_orderdetno=mm_detno and pd_ordercode=mm_code and mm_mdcode=mc_code) where pd_piid="
				+ pis[0]);
		execute("update prodiodetail set pd_purcinqty=Round(Pd_Inqty/nvl((Select case when nvl(Pr_Purcrate,0)=0 then 1 else Pr_Purcrate end From Product Where Pd_Prodcode=Pr_Code),1),8) where pd_piid="
				+ pis[0]);
	}

	public JSONObject newProdIO(String whcode, String piclass, String caller, String piintype) {
		Timestamp time = Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS));
		Object whname = null;
		if (whcode != null) {
			whname = getFieldDataByCondition("WareHouse", "wh_description", "wh_code='" + whcode + "'");
		}
		int id = getSeqId("PRODINOUT_SEQ");
		String no = sGetMaxNumber(caller, 2);
		execute(INSERT_PRODIO_WH, new Object[] { id, no, time, piclass, BaseUtil.getLocalMessage("ENTERING"), "ENTERING",
				SystemSession.getUser().getEm_name(), time, whcode, whname, BaseUtil.getLocalMessage("UNPOST"), "UNPOST", time,
				SystemSession.getUser().getEm_name(), "UNPRINT", BaseUtil.getLocalMessage("UNPRINT"), piintype });
		JSONObject j = new JSONObject();
		j.put("pi_id", id);
		j.put("pi_inoutno", no);
		return j;
	}

	public JSONObject newProdIOWithVendor(String whcode, String ve_code, String ve_apvendcode, String piclass, String caller,
			String piintype) {
		Timestamp time = Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS));
		Object whname = null;
		if (whcode != null && !" ".equals(whcode) && !"".equals(whcode)) {
			whname = getFieldDataByCondition("WareHouse", "wh_description", "wh_code='" + whcode + "'");
		} else {
			whcode = null;
		}
		Object vename = null;
		if (ve_code != null && !" ".equals(ve_code) && !"".equals(ve_code)) {
			vename = getFieldDataByCondition("Vendor", "ve_name", "ve_code='" + ve_code + "'");
		} else {
			ve_code = null;
		}
		Object apvendname = null;
		if (ve_apvendcode != null && !" ".equals(ve_apvendcode) && !"".equals(ve_apvendcode)) {
			apvendname = getFieldDataByCondition("Vendor", "ve_name", "ve_code='" + ve_apvendcode + "'");
		} else {
			ve_apvendcode = null;
		}
		int id = getSeqId("PRODINOUT_SEQ");
		String no = sGetMaxNumber(caller, 2);
		execute(INSERT_PRODIO_WH_VE, new Object[] { id, no, time, piclass, BaseUtil.getLocalMessage("ENTERING"), "ENTERING",
				SystemSession.getUser().getEm_name(), time, whcode, whname, BaseUtil.getLocalMessage("UNPOST"), "UNPOST", time,
				SystemSession.getUser().getEm_name(), ve_code, vename, "UNPRINT", BaseUtil.getLocalMessage("UNPRINT"), ve_apvendcode,
				apvendname, piintype });
		JSONObject j = new JSONObject();
		j.put("pi_id", id);
		j.put("pi_inoutno", no);
		return j;
	}

	public JSONObject newProdIOWithVendor(String ve_code, String ve_apvendcode, String piclass, String caller, String piintype) {
		Timestamp time = Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS));
		Object vename = getFieldDataByCondition("Vendor", "ve_name", "ve_code='" + ve_code + "'");
		Object apvendname = getFieldDataByCondition("Vendor", "ve_name", "ve_code='" + ve_apvendcode + "'");
		int id = getSeqId("PRODINOUT_SEQ");
		String no = sGetMaxNumber(caller, 2);
		execute(INSERT_PRODIO_VE, new Object[] { id, no, time, piclass, BaseUtil.getLocalMessage("ENTERING"), "ENTERING",
				SystemSession.getUser().getEm_name(), time, BaseUtil.getLocalMessage("UNPOST"), "UNPOST", time,
				SystemSession.getUser().getEm_name(), ve_code, vename, ve_apvendcode, apvendname, piintype });
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
	 * @param maid
	 *            制造单ID
	 * @param qty
	 *            本次完工数
	 */
	@Override
	@Transactional
	public void turnMadeWh(String no, int maid, double qty) {
		Object[] objs = getFieldsDataByCondition("Make", new String[] { "ma_code", "ma_prodcode", "ma_prodname", "ma_prodspec",
				"ma_produnit", "ma_batchcode", "ma_price", "ma_wccode" }, "ma_id=" + maid);
		Object id = getFieldDataByCondition("ProdInOut", "pi_id", "pi_inoutno='" + no + "'");
		Object detno = getFieldDataByCondition("ProdIODetail", "max(pd_pdno)", "pd_piid=" + id);
		Object prid = getFieldDataByCondition("Product", "pr_id", "pr_code='" + objs[1] + "'");
		detno = detno == null ? 1 : (Integer.parseInt(detno.toString()) + 1);
		execute(INSERT_DETAIL_F, new Object[] { getSeqId("PRODIODETAIL_SEQ"), id, no, "完工入库单", detno, 0, "ENTERING", qty, objs[0], objs[7],
				maid, objs[1], objs[5], objs[6], prid });
		// 修改状态
		// ma_tomadeqty为当前已转入完工入库单数量，ma_madeqty为已完工数量，(在完工入库单过账后，pd_qty会反馈到ma_madeqty)
		updateByCondition("Make", "ma_tomadeqty=nvl(ma_tomadeqty,0)+" + qty, "ma_id=" + maid);
	}

	/**
	 * 委外验收单 已按仓库分组
	 * 
	 * @param no
	 *            入库单号
	 * @param maid
	 *            制造单ID
	 * @param qty
	 *            本次验收数
	 */
	@Override
	@Transactional
	public void turnMadeOSWh(String no, int maid, double qty) {
		Object[] objs = getFieldsDataByCondition("Make left join Product on ma_prodcode=pr_code", new String[] { "ma_code", "ma_prodcode",
				"pr_detail", "pr_spec", "pr_unit", "ma_batchcode", "ma_price", "ma_wccode", "pr_id" }, "ma_id=" + maid);
		Object id = getFieldDataByCondition("ProdInOut", "pi_id", "pi_inoutno='" + no + "'");
		Object detno = getFieldDataByCondition("ProdIODetail", "max(pd_pdno)", "pd_piid=" + id);
		Object prid = objs[8];
		detno = detno == null ? 1 : (Integer.parseInt(detno.toString()) + 1);
		execute(INSERT_DETAIL_F, new Object[] { getSeqId("PRODIODETAIL_SEQ"), id, no, "委外验收单", detno, 0, "ENTERING", qty, objs[0], objs[7],
				maid, objs[1], objs[5], objs[6], prid });
		// 修改状态
		// ma_tomadeqty为当前已转入委外验收单数量，ma_madeqty为已完工数量，(在委外验收单过账后，pd_qty会反馈到ma_madeqty)
		updateByCondition("Make", "ma_tomadeqty=nvl(ma_tomadeqty,0)+" + qty, "ma_id=" + maid);
	}

	@Override
	@Transactional
	public void turnMadeWhbyflow(String no, int mfid, double qty) {
		Object[] objs = getFieldsDataByCondition("Make,makeflow", new String[] { "ma_code", "ma_prodcode", "ma_prodname", "ma_prodspec",
				"ma_produnit", "ma_batchcode", "ma_price", "ma_wccode", "ma_id", "mf_code" }, "mf_id=" + mfid + " and mf_maid=ma_id");
		Object id = getFieldDataByCondition("ProdInOut", "pi_id", "pi_inoutno='" + no + "'");
		Object detno = getFieldDataByCondition("ProdIODetail", "max(pd_pdno)", "pd_piid=" + id);
		Object prid = getFieldDataByCondition("Product", "pr_id", "pr_code='" + objs[1] + "'");
		detno = detno == null ? 1 : (Integer.parseInt(detno.toString()) + 1);
		execute(INSERT_DETAIL_F2, new Object[] { getSeqId("PRODIODETAIL_SEQ"), id, no, "完工入库单", detno, 0, "ENTERING", qty, objs[0],
				objs[7], objs[8], objs[1], objs[5], objs[6], prid, objs[9] });
		// 修改状态
		// ma_tomadeqty为当前已转入完工入库单数量，ma_madeqty为已完工数量，(在完工入库单过账后，pd_qty会反馈到ma_madeqty)
		updateByCondition("Make", "ma_tomadeqty=nvl(ma_tomadeqty,0)+" + qty, "ma_id=" + objs[8]);
	}

	@Override
	public JSONObject turnMake(int id, double tqty) {
		try {
			SqlRowList rs = queryForRowSet(TURNMAKE, new Object[] { id });
			int maid = 0;
			if (rs.next()) {
				maid = getSeqId("MAKE_SEQ");
				String code = sGetMaxNumber("Make!Base", 2);
				boolean bool = execute(
						INSERTMAKE,
						new Object[] { maid, code, id, rs.getString("ma_code"), rs.getObject("ma_salecode"), rs.getObject("ma_saledetno"),
								rs.getObject("ma_saledetailid"), rs.getObject("ma_requiredate"), rs.getObject("ma_prodid"),
								rs.getObject("ma_prodcode"), rs.getObject("ma_planenddate"), rs.getObject("ma_planbegindate"), tqty,
								rs.getObject("ma_custid"), rs.getObject("ma_custcode"), rs.getObject("ma_custname"),
								rs.getObject("ma_remark"), rs.getObject("ma_bomid"), "ENTERING", BaseUtil.getLocalMessage("ENTERING"),
								Timestamp.valueOf(DateUtil.currentDateString("yyyy-MM-dd HH:mm:ss")),
								Timestamp.valueOf(DateUtil.currentDateString("yyyy-MM-dd HH:mm:ss")), SystemSession.getUser().getEm_id(),
								SystemSession.getUser().getEm_name(), Timestamp.valueOf(DateUtil.currentDateString("yyyy-MM-dd HH:mm:ss")),
								SystemSession.getUser().getEm_name(), "UNAPPROVED", BaseUtil.getLocalMessage("UNAPPROVED"), "MAKE" });
				if (bool) {
					rs = queryForRowSet(TURNMAKEMATERIAL, new Object[] { id });
					int count = 1;
					while (rs.next()) {
						if (tqty > 0) {
							int mmid = getSeqId("MAKEMATERIAL_SEQ");
							execute(INSERTMAKEMATERIAL,
									new Object[] { mmid, id, code, count++, rs.getObject(2), rs.getObject(3), rs.getObject(4),
											rs.getObject(5), rs.getObject(6), rs.getObject(7), tqty, rs.getDouble(9), rs.getObject(10),
											rs.getObject(11), rs.getObject(12), rs.getObject(13), rs.getObject(14), rs.getObject(15),
											rs.getObject(16), rs.getObject(17), rs.getObject(18), rs.getObject(19), rs.getObject(20),
											rs.getObject(21), BaseUtil.getLocalMessage("ENTERING") });
						}
					}
				}
				/*
				 * // 修改make和makematerial状态 updateByCondition("MakeMaterial",
				 * "mm_status='" + BaseUtil.getLocalMessage("PARTMA", language)
				 * + "',mm_yqty=mm_yqty+" + tqty, "mm_maid=" + id);
				 * updateByCondition("MakeMaterial", "mm_status='" +
				 * BaseUtil.getLocalMessage("TURNMA", language) + "'",
				 * "mm_maid=" + id + " AND mm_yqty=mm_qty");
				 */
				JSONObject j = new JSONObject();
				j.put("ma_id", maid);
				j.put("ma_code", code);
				return j;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
			return null;
		}
	}

	/**
	 * 制造单转生产补料单 委外单转委外补料单
	 */
	@Override
	public void turnAdd(String inoutno, int id, int detno, int mmid, int mmdetno, double qty, String whcode, String piclass, String caller) {
		// 获取制造单，委外单的订单号
		Object pd_pocode = getFieldDataByCondition("make left join makematerial on mm_maid=ma_id", "nvl(ma_salecode,'')",
				"mm_id=" + Math.abs(mmid));
		if (mmid < 0) {// 替代料mp_mmid
			mmid = Math.abs(mmid);
			Object[] objs = getFieldsDataByCondition("MakeMaterialReplace left join MakeMaterial on mp_mmid=mm_id", new String[] {
					"mp_mmcode", "mp_mmdetno", "mp_prodcode", "mm_mdcode", "mm_wccode" }, "mp_mmid=" + mmid + " AND mp_detno=" + mmdetno);
			Object whname = getFieldDataByCondition("WareHouse", "wh_description", "wh_code='" + whcode + "'");
			Object[] pr = getFieldsDataByCondition("Product", "pr_id,pr_location", "pr_code='" + objs[2] + "'");
			getJdbcTemplate().update(
					INSERT_DETAIL,
					new Object[] { getSeqId("PRODIODETAIL_SEQ"), id, inoutno, piclass, detno, 0, "ENTERING", objs[2], qty, objs[0],
							objs[1], objs[3], objs[4], mmid, pr[0], whcode, whname, pd_pocode, pr[1] });
		} else {
			Object[] objs = getFieldsDataByCondition("Make left join MakeMaterial on ma_id=mm_maid", new String[] { "mm_id", "mm_code",
					"mm_detno", "mm_mdcode", "mm_prodcode", "mm_wccode" }, "mm_id=" + mmid);
			Object whname = getFieldDataByCondition("WareHouse", "wh_description", "wh_code='" + whcode + "'");
			Object[] pr = getFieldsDataByCondition("Product", new String[] { "pr_id", "pr_location" }, "pr_code='" + objs[4] + "'");
			getJdbcTemplate().update(
					INSERT_DETAIL,
					new Object[] { getSeqId("PRODIODETAIL_SEQ"), id, inoutno, piclass, detno, 0, "ENTERING", objs[4], qty, objs[1],
							objs[2], objs[3], objs[5], objs[0], pr[0], whcode, whname, pd_pocode, pr[1] });
		}
		updateByCondition("MakeMaterial", "mm_turnaddqty=nvl(mm_turnaddqty,0)+" + qty, "mm_id=" + mmid);
		execute("update prodiodetail set (pd_mcid,pd_jobcode)=(select mc_id,mm_mdcode from MakeMaterial,makecraft where pd_orderdetno=mm_detno and pd_ordercode=mm_code and mm_mdcode=mc_code) where pd_piid="
				+ id);
	}

	/**
	 * 制造单转FQC检验单 **锤子科技新增委外单直接转FQC检验单 maz 17-10-27
	 */
	@Override
	@Transactional
	public JSONObject turnQuaCheck(int maid, double qty, Object xlcode, Object batchcode, String statuscode, boolean fqcSeq) {
		Object[] objs = getFieldsDataByCondition("Make left join makekind on ma_kind=mk_name ", new String[] { "ma_code", "ma_prodcode",
				"ma_custcode", "ma_custname", "ma_saledetailid", "ma_salecode", "ma_saledetno", "ma_wccode", "ma_teamcode", "ma_qty",
				"ma_tasktype", "nvl(mk_finishunget,0) AS mk_finishunget" }, "ma_id=" + maid);
		if (objs != null) {
			if (!fqcSeq) {
				int ngqty = 0;
				int toquaqty = 0;
				if ("OS".equals(objs[10])) {
					ngqty = Integer.parseInt(getFieldDataByCondition(
							"qua_verifyapplydetaildet LEFT JOIN qua_verifyapplydetail on ve_id=ved_veid", "NVL(SUM(NVL(ved_ngqty,0)),0)",
							"ve_ordercode='" + objs[0] + "' AND ve_class ='委外检验单' AND NVL(ve_criqty,0)=0").toString());
					toquaqty = Integer.parseInt(getFieldDataByCondition("qua_verifyapplydetail ", "NVL(SUM(NVL(vad_qty,0)),0)",
							"ve_ordercode='" + objs[0] + "' AND ve_class ='委外检验单' ").toString());
				} else {
					ngqty = Integer.parseInt(getFieldDataByCondition(
							"qua_verifyapplydetaildet LEFT JOIN qua_verifyapplydetail on ve_id=ved_veid", "NVL(SUM(NVL(ved_ngqty,0)),0)",
							"ve_ordercode='" + objs[0] + "' AND ve_class ='生产检验单' AND NVL(ve_criqty,0)=0").toString());
					toquaqty = Integer.parseInt(getFieldDataByCondition("qua_verifyapplydetail ", "NVL(SUM(NVL(vad_qty,0)),0)",
							"ve_ordercode='" + objs[0] + "' AND ve_class ='生产检验单' ").toString());
				}
				execute("update make set ma_toquaqty=" + toquaqty + " where ma_id=" + maid + " and ma_toquaqty<>" + toquaqty);
				if (Double.parseDouble(objs[9].toString()) < toquaqty + qty) {
					Object gdrate = getFieldDataByCondition("make left join workcenter on ma_wccode=wc_code", "wc_makegreater", "ma_id="
							+ maid);
					if (gdrate == null || Double.parseDouble(gdrate.toString()) == 0) {// 判断该工作中心是否允许超比例完工，如果不允许则不能超过工单数
						BaseUtil.showError("工单:" + objs[0] + ",本次送检数+已转检验数大于工单数量!");
					}
				}
				// 工单类型未领料可完工则不限制
				if (Integer.valueOf(String.valueOf(objs[11])) == 0) {
					List<Object> mm = getFieldDatasByCondition(
							"MakeMaterial left join make on mm_maid=ma_id left join makekind on mk_name=ma_kind",
							"mm_detno",
							"mm_maid = "
									+ maid
									+ " and ceil((NVL(mm_havegetqty,0)-NVL(mm_scrapqty,0))/mm_oneuseqty)<(case when NVL(ma_madeqty,0)>NVL(ma_toquaqty,0)-"
									+ ngqty
									+ " then ma_madeqty else NVL(ma_toquaqty,0)-"
									+ ngqty
									+ " end)+"
									+ qty
									+ "  AND nvl(mm_materialstatus, ' ')=' ' AND NVL(mk_finishunget,0)=0 AND nvl(mm_oneuseqty,0)>0 and mm_oneuseqty*ma_qty<=mm_qty+0.1 and NVL(mm_havegetqty,0)-NVL(mm_scrapqty,0)<mm_qty ");
					if (mm.size() > 0) {
						BaseUtil.showError("工单:" + objs[0] + ",物料序号:" + BaseUtil.parseList2Str(mm, ",", true) + "的领料数量不足!");
					}
				}
			}
			int id = getSeqId("QUA_VERIFYAPPLYDETAIL_SEQ");
			String code = sGetMaxNumber("VerifyApplyDetail!FQC", 2);
			String type = "生产检验单";
			String vad_class = "完工入库申请单";
			if ("OS".equals(objs[10])) {
				type = "委外检验单";
				vad_class = "委外验收申请单";
			}
			Timestamp time = Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS));
			getJdbcTemplate().update(
					INSERT_QUAVERIFY,
					new Object[] { id, code, time, "FQC", type, maid, objs[0], objs[1], qty, objs[2], objs[3], objs[4], objs[5], objs[6],
							statuscode, BaseUtil.getLocalMessage(statuscode), time, SystemSession.getUser().getEm_name(), vad_class,
							objs[0], objs[7], "未打印", xlcode, objs[8], batchcode });
			// 修改状态
			updateByCondition("Make", "ma_qcstatuscode='PARTQUA',ma_qcstatus='" + BaseUtil.getLocalMessage("PARTQUA")
					+ "',ma_toquaqty=NVL(ma_toquaqty,0)+" + qty, "ma_id=" + maid);
			updateByCondition("Make", "ma_qcstatuscode='TURNQUA',ma_qcstatus='" + BaseUtil.getLocalMessage("TURNQUA") + "'", "ma_id="
					+ maid + " AND ma_toquaqty=ma_qty");
			JSONObject j = new JSONObject();
			j.put("ve_id", id);
			j.put("ve_code", code);
			return j;
		}
		return null;
	}

	/**
	 * 制造单转FQC检验单(免检)
	 */
	@Override
	@Transactional
	public JSONObject turnQuaCheck2(int maid, double qty, Object xlcode, Object batchcode, String statuscode, boolean fqcSeq) {
		Object[] objs = getFieldsDataByCondition("Make left join makekind on ma_kind=mk_name", new String[] { "ma_code", "ma_prodcode",
				"ma_custcode", "ma_custname", "ma_saledetailid", "ma_salecode", "ma_saledetno", "ma_wccode", "ma_teamcode",
				"nvl(mk_finishunget,0) AS mk_finishunget" }, "ma_id=" + maid + " and  ma_qty>nvl(ma_toquaqty,0)");
		if (objs != null) {
			if (!fqcSeq) {
				int ngqty = Integer.parseInt(getFieldDataByCondition(
						"qua_verifyapplydetaildet LEFT JOIN qua_verifyapplydetail on ve_id=ved_veid", "NVL(SUM(NVL(ved_ngqty,0)),0)",
						"ve_ordercode='" + objs[0] + "' AND ve_class ='生产检验单' AND NVL(ve_criqty,0)=0").toString());
				// 工单类型未领料可完工则不限制
				if (Integer.valueOf(String.valueOf(objs[9])) == 0) {
					List<Object> mm = getFieldDatasByCondition(
							"MakeMaterial left join make on ma_id=mm_maid left join makekind on mk_name=ma_kind ",
							"mm_detno",
							" mm_maid = "
									+ maid
									+ " and ceil((NVL(mm_havegetqty,0)-NVL(mm_scrapqty,0))/mm_oneuseqty)<(case when NVL(ma_madeqty,0)>NVL(ma_toquaqty,0)-"
									+ ngqty
									+ " then ma_madeqty else NVL(ma_toquaqty,0)-"
									+ ngqty
									+ " end)+"
									+ qty
									+ "  AND nvl(mm_materialstatus, ' ')=' ' AND NVL(mk_finishunget,0)=0 AND nvl(mm_oneuseqty,0)>0 and mm_oneuseqty*ma_qty<=mm_qty+0.1 and NVL(mm_havegetqty,0)-NVL(mm_scrapqty,0)<mm_qty");
					if (mm.size() > 0) {
						BaseUtil.showError("工单:" + objs[0] + ",物料序号:" + BaseUtil.parseList2Str(mm, ",", true) + "的领料数量不足!");
					}
				}
			}
			int id = getSeqId("QUA_VERIFYAPPLYDETAIL_SEQ");
			String code = sGetMaxNumber("QUA_VerifyApplyDetail", 2);
			Timestamp time = Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS));
			getJdbcTemplate().update(
					INSERT_QUAVERIFY,
					new Object[] { id, code, time, "FQC", "生产检验单", maid, objs[0], objs[1], qty, objs[2], objs[3], objs[4], objs[5],
							objs[6], statuscode, BaseUtil.getLocalMessage(statuscode), time, SystemSession.getUser().getEm_name(),
							"完工入库申请单", objs[0], objs[7], "未打印", xlcode, objs[8], batchcode });
			execute(INSERQUA_VERIFYAPPLYDETAILDET, new Object[] { getSeqId("QUA_VERIFYAPPLYDETAILDET_SEQ"), id, 1, qty, time,
					SystemSession.getUser().getEm_name(), time, qty, BaseUtil.getLocalMessage(statuscode), statuscode, code });
			// 修改状态
			updateByCondition("QUA_VerifyApplyDetail", "ve_result='合格'", "ve_id=" + id);
			updateByCondition("Make", "ma_qcstatuscode='PARTQUA',ma_qcstatus='" + BaseUtil.getLocalMessage("PARTQUA")
					+ "',ma_toquaqty=NVL(ma_toquaqty,0)+" + qty, "ma_id=" + maid);
			updateByCondition("Make", "ma_qcstatuscode='TURNQUA',ma_qcstatus='" + BaseUtil.getLocalMessage("TURNQUA") + "'", "ma_id="
					+ maid + " AND ma_toquaqty=ma_qty");
			JSONObject j = new JSONObject();
			j.put("ve_id", id);
			j.put("ve_code", code);
			return j;
		}
		return null;
	}

	/*
	 * @Override
	 * 
	 * @Transactional public void deleteMake(int id) { List<Object[]> objs =
	 * getFieldsDatasByCondition("Make", new String[] { "ma_id", "ma_qty" },
	 * "ma_id=" + id); deleteByCondition("Make", "ma_id=" + obj[0]);
	 */
	/*
	 * for (Object[] obj : objs) { if (Integer.parseInt(obj[1].toString()) > 0)
	 * { // 还原销售明细及销售单 restoreSale(Integer.parseInt(obj[0].toString())); }
	 * 
	 * } }
	 */

	/*	*//**
	 * 制造单删除时，修改销售单状态、数量等
	 */
	/*
	 * public void restoreSale(int maid) { Object[] objs =
	 * getFieldsDataByCondition("Make", new String[] { "ma_saledetailid",
	 * "ma_qty", "ma_salecode" }, "ma_id=" + maid); if (objs != null && objs[0]
	 * != null && objs[2] != null) { updateByCondition("SaleDetail",
	 * "sd_tomakeqty=sd_tomakeqty-" + Integer.parseInt(objs[1].toString()),
	 * "sd_id=" + objs[0]); } }
	 */

	/**
	 * 制造单修改时，修改销售单状态、数量等
	 */
	@Override
	public void restoreSaleWithQty(int maid, double uqty) {
		Object[] objs = getFieldsDataByCondition("Make", new String[] { "ma_saledetailid", "ma_salecode" }, "ma_id=" + maid);
		if (objs != null && objs[0] != null && objs[1] != null) {
			updateByCondition("SaleDetail", "sd_tomakeqty=sd_tomakeqty-" + uqty, "sd_id=" + objs[0]);
		}
	}

	/**
	 * 计算本次可领料数 替代料可领料数
	 * 
	 * @param maidlist
	 *            {String} Make表ID 用,隔开
	 */
	public void setThisQty(Integer mm_id, Integer ma_id, String maidlist) {
		String wherestr1 = "";
		String wherestr2 = "";
		String sqlstr = "";
		if (mm_id != null && mm_id > 0) {
			wherestr1 = " where mm_id=" + mm_id;
			wherestr2 = " where mp_mmid=" + mm_id;
		} else if (ma_id != null && ma_id > 0) {
			wherestr1 = " where mm_maid=" + ma_id;
			wherestr2 = " where mp_maid=" + ma_id;
		} else if (maidlist != null && !maidlist.equals("")) {
			wherestr1 = " where mm_maid in (" + maidlist + ")";
			wherestr2 = " where mp_maid in (" + maidlist + ")";
		} else {
			return;
		}
		// 更新主料已转数量
		sqlstr = "update MakeMaterial set mm_totaluseqty=(select NVL(sum(nvl(pd_outqty,0)),0) from prodiodetail,prodinout "
				+ "where pd_piid=pi_id and pd_status=0  and "
				+ "pd_ordercode=mm_code and pd_orderdetno=mm_detno and pd_piclass in ('生产领料单', '委外领料单'))";
		// select ds_name FROM documentsetup WHERE " + "nvl(ds_name, ' ')<>' '
		// and nvl(ds_ismakemminout,0)<>0))
		sqlstr += wherestr1;
		execute(sqlstr);
		sqlstr = "update makematerial set mm_totaluseqty=0 " + wherestr1 + " and mm_totaluseqty<0";
		execute(sqlstr);
		// 更新替代维护数
		sqlstr = "update MakeMaterial set mm_canuserepqty=(select NVL(sum(nvl(mp_canuseqty,0)),0) from MakeMaterialreplace where "
				+ "mp_mmid=mm_id) ";
		sqlstr += wherestr1;
		execute(sqlstr);
		// 更新替代已转数量
		sqlstr = "update makematerialreplace set mp_repqty=(select NVL(sum(nvl(pd_outqty,0)),0) from prodiodetail,prodinout "
				+ "where pd_piid=pi_id and pd_status=0  and pd_prodcode=mp_prodcode and pd_ordercode=mp_mmcode and "
				+ "pd_orderdetno=mp_mmdetno  and pd_piclass in ('生产领料单', '委外领料单'))";
		sqlstr += wherestr2;
		execute(sqlstr);
		sqlstr = "update makematerialreplace set mp_repqty=0 " + wherestr2 + " and mp_repqty<0";
		execute(sqlstr);
		// 更新替代已领料数量
		sqlstr = "update MakeMaterialreplace set mp_haverepqty=(select NVL(sum(nvl(pd_outqty,0)-NVL(pd_inqty,0)),0) from prodiodetail,prodinout "
				+ "where pd_piid=pi_id and pd_status=99 and mp_prodcode=pd_prodcode and pd_ordercode=mp_mmcode and "
				+ "pd_orderdetno=mp_mmdetno and pd_piclass in ('生产领料单','生产退料单','生产补料单', '委外领料单','委外补料单','委外退料单'))";
		sqlstr += wherestr2;
		execute(sqlstr);
		// 更新替代总已转数
		sqlstr = "update MakeMaterial set mm_repqty=NVL((select sum(NVL(mp_repqty,0)) from MakeMaterialreplace where mp_mmid=mm_id),0)  "
				+ wherestr1;
		execute(sqlstr);
		// 更新替代总已领数
		sqlstr = "update MakeMaterial set mm_haverepqty=NVL((select sum(NVL(mp_haverepqty,0)) from MakeMaterialreplace where mp_mmid=mm_id),0)  "
				+ wherestr1;
		execute(sqlstr);

		// 计算主料应领数 本次数量=需求数-替代维护数-(已领数+制程不良退料数-补料数)-已转数量
		sqlstr = "update MakeMaterial set mm_thisqty=round((mm_qty-nvl(mm_canuserepqty,0)-(nvl(mm_havegetqty,0)-nvl(mm_haverepqty,0)+"
				+ "(nvl(mm_returnmqty,0)-nvl(mm_repreturnmqty,0))-(nvl(mm_addqty,0)-NVL(mm_repaddqty,0)))-nvl(mm_totaluseqty,0)+NVL(mm_repqty,0))-NVL(mm_stepinqty,0),7) ";
		sqlstr += wherestr1;
		execute(sqlstr);
		sqlstr = "update MakeMaterial set mm_thisqty=0 " + wherestr1 + " and mm_thisqty<0";
		execute(sqlstr);

		// 计算替代料应领数 本次数量=维护可用数-已转领料数
		sqlstr = "update MakeMaterialreplace set mp_thisqty=nvl(mp_canuseqty,0)-(nvl(mp_haverepqty,0)-NVL(mp_addqty,0)+NVL(mp_returnmqty,0))-nvl(mp_repqty,0) "
				+ wherestr2;
		execute(sqlstr);
		sqlstr = "update MakeMaterialreplace set mp_thisqty=0 " + wherestr2 + " and mp_thisqty<0";
		execute(sqlstr);
	}

	/**
	 * 根据物料属性默认未填写的属性 供应类型、默认仓库
	 * 
	 * @param maidstr
	 *            {String} Make表ID 用,隔开
	 * @param mmidstr
	 *            {String} MakeMaterial表ID 用,隔开
	 */
	public void saveDefault(String maidstr, String mmidstr) {
		String wherestr = null;
		if (maidstr != null && !"".equals(maidstr)) {
			wherestr = " mm_maid in (" + maidstr + ")";
		} else if (mmidstr != null && !"".equals(mmidstr)) {
			wherestr = " mm_id in (" + mmidstr + ")";
		} else {
			return;
		}
		String sqlstr = "update makematerial set mm_supplytype=(select max (pr_supplytype) from product where pr_code=mm_prodcode) where "
				+ wherestr + " and NVL(mm_supplytype,' ')=' ' ";
		execute(sqlstr);
		sqlstr = "update makematerial set mm_whcode=(select max (pr_whcode) from product where pr_code=mm_prodcode) where " + wherestr
				+ "  and NVL(mm_whcode,' ')=' '  ";
		execute(sqlstr);
		if (maidstr != null && !"".equals(maidstr)) {
			sqlstr = "merge into make using(select sa_code,sa_custcode,sa_custname from sale )s on (s.sa_code=ma_salecode )"
					+ " when matched then update set ma_custcode=s.sa_custcode,ma_custname=s.sa_custname Where ma_id in(" + maidstr + ")";
			execute(sqlstr);
			sqlstr = "merge into make using(select sf_code,sf_custcode,sf_custname from saleforecast )s on (s.sf_code=ma_salecode )"
					+ " when matched then update set ma_custcode=s.sf_custcode,ma_custname=s.sf_custname Where ma_id in(" + maidstr + ")";
			execute(sqlstr);
		}
		// 更新标准损耗数
		if (isDBSetting("makeLostOnQTY")) {// 启用损耗率分段数方案
			sqlstr = " update makematerial set mm_lostqty=(select NVL(max(pl_rate),0) from ("
					+ " select mm_id,mm_prodcode ,pl_code,pl_rate,pl_lapqty, rank() over (partition by mm_id order by pl_code desc,pl_lapqty desc)mm "
					+ "from makematerial t1 left join make on mm_maid=ma_id left join( sELECT * from ProductLoss  )t2 on  mm_prodcode like pl_code||'%' "
					+ "and pl_lapqty<=mm_oneuseqty*ma_qty where " + wherestr + " )t3 where makematerial.mm_id=t3.mm_Id and mm=1) where "
					+ wherestr + "  ";
			execute(sqlstr);
		} else {
			if(isDBSetting("Make", "osLossNotUsePrLoss")){
				sqlstr = " update makematerial set mm_lostqty=(select NVL(max(pr_exportlossrate),0) from product where pr_code=mm_prodcode)  where "
						+ wherestr + " ";
			}else{
				sqlstr = " update makematerial set mm_lostqty=(select NVL(max(pr_lossrate),0) from product where pr_code=mm_prodcode)  where "
						+ wherestr + " ";
			}
			execute(sqlstr);
			
		}
	}

	/**
	 * 默认备损数=总需求数-单位用量*工单数 ，单位用量小于0的备损数0
	 * 
	 * @param maidstr
	 *            工单ID用逗号隔开
	 * @param mmidstr
	 *            用料表ID用逗号隔开
	 **/
	public void setBalance(String maidstr, String mmidstr) {
		String wherestr = null;
		if (maidstr != null && !"".equals(maidstr)) {
			wherestr = " mm_maid in (" + maidstr + ")";
		} else if (mmidstr != null && !"".equals(mmidstr)) {
			wherestr = " mm_id in (" + mmidstr + ")";
		} else {
			return;
		}
		String sqlstr = "update makematerial set mm_balance=mm_qty-mm_oneuseqty*(select ma_qty from make where ma_id=mm_maid) where "
				+ wherestr;
		execute(sqlstr);
		sqlstr = "update makematerial set mm_balance=0 where " + wherestr + " and (mm_balance<0 or mm_oneuseqty<0) ";
		execute(sqlstr);
	}

	/**
	 * 更新用料表的替代料编号标识
	 * 
	 * @param maidstr
	 *            工单ID用逗号隔开
	 * @param mmidstr
	 *            用料表ID用逗号隔开
	 **/
	public void setMaterialRepcode(String maidstr, String mmidstr) {
		String wherestr = null;
		int mmid = 0;
		if (maidstr != null && !"".equals(maidstr)) {
			wherestr = " mm_maid in (" + maidstr + ")";
		} else if (mmidstr != null && !"".equals(mmidstr)) {
			wherestr = " mm_id in (" + mmidstr + ")";
		} else {
			return;
		}
		try {
			SqlRowList rs = queryForRowSet("select mm_id from makematerial where " + wherestr);
			while (rs.next()) {
				mmid = rs.getInt("mm_id");
				String repCode = null;
				SqlRowList rs2 = queryForRowSet("select mp_canuseqty,mp_prodcode from  makematerialreplace where mp_mmid=?", mmid);
				while (rs2.next()) {
					if (repCode == null) {
						repCode = rs2.getString("mp_prodcode");
					} else {
						repCode = repCode + "," + rs2.getString("mp_prodcode");
					}
				}
				if (repCode != null && repCode.length() > 100) {
					repCode = repCode.substring(0, 99);
				}
				updateByCondition("MakeMaterial", "mm_repprodcode='" + repCode + "'", "mm_id=" + mmid);
			}
		} catch (Exception e) {
			if (mmid > 0) {
				updateByCondition("MakeMaterial", "mm_repprodcode='" + e.getMessage() + "'", "mm_id=" + mmid);
			}
		}

	}

	/**
	 * 更新 工单最大领料套数 只考虑正常用料的领料数
	 * 
	 * @param maidstr
	 *            工单ID用逗号隔开
	 **/
	@Override
	public void setMaxCanMadeqty(String maidstr) {
		if (maidstr == null || "".equals(maidstr)) {
			return;
		}
		String sqlstr = "update make set ma_canmadeqty=nvl((select min(case when NVL(mm_havegetqty,0)-NVL(mm_scrapqty,0)>=mm_qty then ma_qty else ceil((  nvl(mm_havegetqty, 0)-nvl(mm_scrapqty,0))*1.0/mm_oneuseqty )end) from makematerial where mm_maid=ma_id and nvl(mm_materialstatus,' ')=' ' and mm_oneuseqty>0 and mm_oneuseqty*ma_qty<=mm_qty+0.1 ),0)  where ma_id in ("
				+ maidstr + ") ";
		execute(sqlstr);
		sqlstr = "update make set ma_canmadeqty=0 where ma_id in (" + maidstr + ") and NVL(ma_canmadeqty,0)<=0";
		execute("update make set ma_canmadeqty=ma_qty where ma_id in (" + maidstr
				+ ") and ma_id not in (select ma_id from make left join makematerial on ma_id=mm_maid where ma_id in (" + maidstr
				+ ") and nvl(mm_materialstatus,' ')=' ' and mm_oneuseqty>0 and mm_oneuseqty*ma_qty<=mm_qty+0.1)");
		execute(sqlstr);
	}

	/**
	 * 更新工单用料在线结存数量
	 * 
	 * @param WhereStr
	 *            筛选条件 不带where
	 */
	@Override
	public void setMaxCanMadeqtyByCondition(String WhereStr) {
		if (WhereStr == null || "".equals(WhereStr)) {
			return;
		}
		String sqlstr = "update make set ma_canmadeqty=nvl((select min(floor((NVL(mm_havegetqty,0)-NVL(mm_scrapqty,0))/mm_oneuseqty)) from makematerial where mm_maid=mm_id and nvl(mm_materialstatus,' ')=' ' and mm_oneuseqty>0 and mm_oneuseqty*ma_qty<=mm_qty+0.1),0)  where "
				+ WhereStr;
		execute(sqlstr);
		sqlstr = "update make set ma_canmadeqty=0 where NVL(ma_canmadeqty,0)<=0 and " + WhereStr;
		execute(sqlstr);
	}

	/**
	 * 更新工单用料在线结存数量
	 * 
	 * @param maidstr
	 *            工单ID用逗号隔开
	 * @param mmidstr
	 *            用料表ID用逗号隔开
	 **/
	public void setMMOnlineQTY(String maidstr, String mmidstr) {
		String wherestr = null;
		if (maidstr != null && !"".equals(maidstr)) {
			wherestr = " mm_maid in (" + maidstr + ")";
		} else if (mmidstr != null && !"".equals(mmidstr)) {
			wherestr = " mm_id in (" + mmidstr + ")";
		} else {
			return;
		}
		String sqlstr ="";
		if(isDBSetting("usingMakeCraft")){
			sqlstr ="update makematerial set mm_onlineqty=nvl(mm_havegetqty,0)+nvl(mm_stepinqty,0)-nvl(mm_clashqty,0) -NVL(mm_scrapqty,0)  where "+ wherestr;
		}else{
			sqlstr = "update makematerial set mm_onlineqty=NVL(mm_havegetqty,0)-NVL(mm_scrapqty,0)-NVL(mm_oneuseqty,0)*(select nvl(ma_madeqty,0) from make where ma_id=mm_maid)  where "
					+ wherestr + " ";
		}
		execute(sqlstr);
		sqlstr = "update makematerial set mm_onlineqty=0  where " + wherestr + " and mm_onlineqty<=0 ";
		execute(sqlstr);
	}

	/**
	 * 委外加工单批量转委外收料单
	 */
	@Override
	@Transactional
	public String turnAccept(String caller, List<Map<Object, Object>> maps) {
		int count = 1;
		String code = null;
		int vaid = 0;
		Set<String> codes = new HashSet<String>();// 根据委外加工单ID，找出对应的哪些采购主表的code
		String log = null;
		for (Map<Object, Object> map : maps) {
			SqlRowList rs = queryForRowSet(TURNMAKE, new Object[] { map.get("ma_id") });
			SqlRowList rsDetail;
			Object receiveCode = null, receiveName = null;
			if (rs.next()) {
				if (code == null) {
					code = sGetMaxNumber("VerifyApply", 2);
					vaid = getSeqId("VERIFYAPPLY_SEQ");
					rsDetail = queryForRowSet(GETVENDER, new Object[] { rs.getObject("ma_vendcode").toString() });
					if (rsDetail.next()) {
						receiveCode = rsDetail.getObject(1);
						receiveName = rsDetail.getObject(2);
					}
					execute(INSERTVERIFYAPPLY, new Object[] { vaid, code, "ENTERING", BaseUtil.getLocalMessage("ENTERING"),
							SystemSession.getUser().getEm_name(), rs.getObject("ma_vendcode"), rs.getObject("ma_vendname"), receiveCode,
							receiveName, "委外收料单", rs.getObject("ma_paymentscode"), rs.getObject("ma_payments"),
							rs.getObject("ma_currency"), rs.getGeneralDouble("ma_rate") });
				}
				int vadid = getSeqId("VERIFYAPPLYDETAIL_SEQ");
				String macode = rs.getObject("ma_code").toString();
				execute(INSERTVERIFYAPPLYDETAIL,
						new Object[] { vadid, vaid, code, count++, "委外入库申请单", rs.getObject("ma_prodcode"), map.get("ma_thisqty"),
								rs.getObject("ma_date"), macode, macode, BaseUtil.getLocalMessage("ENTERING"), rs.getObject("ma_vendcode"),
								rs.getObject("ma_vendname"), rs.getObject("ma_remark"), rs.getObject("ma_salecode") });
				int argCount = getCountByCondition("user_tab_columns",
						"table_name='MAKE' and column_name in ('MA_APVENDCODE','MA_APVENDNAME')");
				if (argCount == 2) {
					execute("update verifyapply set (va_receivecode,va_receivename)=(select MA_APVENDCODE,MA_APVENDNAME from make,verifyapplydetail where vad_vaid=va_id and vad_pucode=ma_code and vad_detno=1 and nvl(ma_apvendcode,' ')<>' ') where va_id="
							+ vaid);
					execute("update verifyapply set (va_receivecode,va_receivename)=(select VE_APVENDCODE,VE_APVENDNAME from VENDOR where VE_CODE=VA_VENDCODE) where VA_id="
							+ vaid + " AND NVL(va_receivecode,' ')=' '");
				}
				/**
				 * @author wsy 双单位
				 */
				execute("update VerifyApplydetail set vad_purcqty=round(vad_qty/(select ma_qty from make where ma_id=" + map.get("ma_id")
						+ ")*(select case when nvl(ma_purcqty,0)=0 then ma_qty else ma_purcqty end from  make where ma_id="
						+ map.get("ma_id") + "),6) where vad_vaid=?", vaid);
				// 转成功就修改make的[已转数量]
				Object qt = getFieldDataByCondition("Make", "ma_haveqty", "ma_id=" + map.get("ma_id"));
				qt = qt == null ? 0 : qt;
				int yqty = Integer.parseInt(qt.toString()) + Integer.parseInt(map.get("ma_thisqty").toString());
				updateByCondition("Make", "ma_haveqty=" + yqty, "ma_id=" + map.get("ma_id"));
				// 按采购单号分组
				if (!codes.contains(macode)) {
					codes.add(macode);
				}
				// 记录日志
				logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.turnVerifyApply"),
						BaseUtil.getLocalMessage("msg.turnSuccess"), "Make|ma_id=" + rs.getInt("ma_id")));
			}
		}
		log = "转入成功,收料单号:" + "<a href=\"javascript:openUrl('jsps/scm/purchase/verifyApply.jsp?formCondition=va_idIS" + vaid
				+ "&gridCondition=vad_vaidIS" + vaid + "&whoami=VerifyApply!OS')\">" + code + "</a>";
		return log;
	}

	/**
	 * 计算本次需调拨数
	 * 
	 * @param maid
	 *            {Integer} ma_id
	 * @param setqty
	 *            {Integer} 套料数，默认0
	 * @param wipwhcode
	 *            {String} 线边仓的仓库编号
	 * 
	 */
	@Override
	@Transactional
	public void setLSThisqty(String caller, String maid, Integer setqty, String wipwhcode) {
		int rn = 0;
		if (maid == null || ("").equals(maid)) {
			BaseUtil.showError("所选工单存在未批准或未审核的,不能发料!");
		}
		rn = getCount("select count(1) from (select ma_tasktype from make where ma_id in (" + maid
				+ ") and (nvl(ma_checkstatuscode,' ')<>'APPROVE' or ma_statuscode<>'AUDITED')) ");
		if (rn > 0) {
			BaseUtil.showError("所选工单存在未批准或未审核的,不能发料!");
		}
		rn = getCount("select count(1) from (select ma_tasktype from make where ma_id in (" + maid + ") group by ma_tasktype)");
		if (rn > 1) {
			BaseUtil.showError("所选工单必须是同一工单类型!");
		}
		String sqlstr = "";
		// 更新替代料的默认发料仓库
		sqlstr = "update MakeMaterialreplace set mp_whcode=(select max(pr_whcode) from Product where pr_code=mp_prodcode) "
				+ "where mp_maid in (" + maid + ") and NVL(mp_whcode,' ')=' '";
		execute(sqlstr);
		boolean bool = isDBSetting(caller, "autoUpdateRepQty");
		if (bool) {
			SqlRowList rs = queryForRowSet(" select * from ( select  mm_code,mm_detno,mm_id,mm_prodcode,mm_whcode,iifrep,mm_qty,ba_date,remain,"
					+ " (select sum(pd_outqty) from prodiodetail where pd_status=0 and pd_prodcode=mm_prodcode and pd_whcode=mm_whcode) unpostqty,"
					+ " rank() over(partition by mm_code,mm_id order by case when remain>mm_qty then ba_date else sysdate+300 end,remain) rm "
					+ " from ( "
					+ " select mm_code,mm_id,mm_prodcode,mm_whcode,iifrep,min(ba_date)ba_date,sum(ba_remain)remain,max(mm_qty)mm_qty,max(mm_detno)mm_detno from( "
					+ " select mm_code,mm_detno,mm_id,0 iifrep,mm_prodcode,mm_whcode,mm_qty  "
					+ " from makematerial where mm_maid in ("
					+ maid
					+ ") and mm_ifrep<>0 and mm_canuseqty=0 "
					+ " union(select mp_mmcode mm_code,mp_mmdetno mm_detno,mp_mmid mm_id,-1 ifrep,mp_prodcode mm_prodcode,mp_whcode mm_whcode, mm_qty "
					+ " from makematerial left join makematerialreplace on mp_maid=mm_maid and mp_mmdetno=mm_detno where mp_maid in ("
					+ maid
					+ ")) "
					+ " ) left join batch on ba_prodcode=mm_prodcode and ba_whcode=mm_whcode and ba_remain>0 "
					+ " group by  mm_code,mm_id,mm_prodcode,mm_whcode,iifrep)A) where RM=1 "
					+ " and remain-NVL(unpostqty,0)>mm_qty and iifrep<>0 and mm_detno>0 "
					+ " and not exists(select 1 from prodiodetail where pd_ordercode=mm_code and pd_orderdetno=mm_detno) ");
			while (rs.next()) {
				int mm_id = rs.getGeneralInt("mm_id");
				execute("update makematerialreplace set mp_canuseqty=? where mp_mmid=? and mp_prodcode=?", rs.getObject("mm_qty"), mm_id,
						rs.getGeneralString("mm_prodcode"));
				execute("update makematerial set mm_canuseqty=? where mm_id=?", rs.getObject("mm_qty"), mm_id);
			}
		}
		// 更新物料剩余可领料数
		setThisQty(0, null, maid);
		Object[] maids = maid.split(",");
		Object[] ma = null;
		List<String> sqls = new ArrayList<String>();
		String error = "";
		for (int i = 0; i < maids.length; i++) {
			ma = this.getFieldsDataByCondition("make", "ma_qty,ma_code", "ma_id=" + maids[i]);
			if (Integer.parseInt(ma[0].toString()) <= 0) {
				error += "工单[" + ma[1] + "]数量不能为0<br/>";
				continue;
			}
			if (setqty > 0) {
				sqls.add("update makematerial set mm_thisqty=ceil(mm_qty*" + setqty + "/" + ma[0].toString() + ") where mm_maid="
						+ maids[i] + " and mm_oneuseqty>0 and mm_thisqty>ceil(mm_qty*" + setqty + "/" + ma[0].toString() + ")  ");
				sqls.add("merge into makematerialreplace using(select mm_id,mm_oneuseqty,mm_qty from makematerial where mm_maid="
						+ maids[i]
						+ " and mm_oneuseqty>0 ) src on (mm_id=mp_mmid) when matched then update set mp_thisqty=case when mp_thisqty>ceil(mm_qty*"
						+ setqty + "/" + ma[0].toString() + ")  then ceil(mm_qty*" + setqty + "/" + ma[0].toString()
						+ ")  else mp_thisqty end where mp_maid=" + maids[i]);
			}
		}
		if (!"".equals(error)) {
			BaseUtil.showError(error);
		}
		execute(sqls);
		// 后台存储过程运算需调拨数
		boolean v = this.checkIf("warehouse", "wh_code='" + wipwhcode + "' and wh_statuscode='AUDITED' and wh_ifwip<>0");
		if (!v) {
			BaseUtil.showError("线边仓:" + wipwhcode + "不是有效的线边仓库");
		}
		String str = callProcedure("MM_SETLSTHSIQTY", new String[] { maid.toString(), wipwhcode });
		if (str != null && !str.trim().equals("")) {
			// 提示错误信息
			BaseUtil.showError("计算需调拨数失败");
		}
	}

	static final String Scrap = "INSERT INTO MakeScrap (ms_id,ms_code,ms_recordman,ms_status,ms_statuscode,ms_class,ms_indate,ms_printstatuscode,ms_printstatus,ms_wccode,ms_date) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
	static final String ScrapDetail = "INSERT INTO MakeScrapDetail(md_id,md_msid,md_mmid,md_detno,md_mmcode,md_mmdetno,md_prodcode,md_qty) VALUES(?,?,?,?,?,?,?,?)";

	/**
	 * 转报废
	 */
	public JSONObject turnScrap(String caller, String cls, List<Map<Object, Object>> store) {
		int id = getSeqId("MAKESCRAP_SEQ");
		String code = sGetMaxNumber(caller, 2);
		Object wccode = getFieldDataByCondition("Make", "NVL(ma_wccode,' ')", "ma_code='" + store.get(0).get("mm_code").toString() + "'");
		getJdbcTemplate().update(Scrap, id, code, SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("ENTERING"), "ENTERING",
				cls, Timestamp.valueOf(DateUtil.currentDateString("yyyy-MM-dd HH:mm:ss")), "UNPRINT", BaseUtil.getLocalMessage("UNPRINT"),
				wccode.toString(), Timestamp.valueOf(DateUtil.currentDateString("yyyy-MM-dd HH:mm:ss")));
		int index = 1;
		StringBuffer sb1 = new StringBuffer();
		for (Map<Object, Object> p : store) {
			if (Double.parseDouble(p.get("mm_thisqty").toString()) == 0) {
				sb1.append("行号[" + p.get("mm_detno").toString() + "]本次数量不能为0").append("<hr>");
			}
		}
		if (sb1.length() > 0) {
			BaseUtil.showError(sb1.toString());
		}
		for (Map<Object, Object> m : store) {
			if (Integer.valueOf(m.get("mm_id").toString()) < 0) {
				Object[] objs = getFieldsDataByCondition("MakeMaterialReplace", new String[] { "mp_mmdetno", "mp_prodcode" }, "mp_mmid="
						+ Math.abs(Integer.valueOf(m.get("mm_id").toString())) + " AND mp_detno=" + m.get("mm_detno"));
				getJdbcTemplate().update(ScrapDetail, getSeqId("MAKESCRAPDETAIL_SEQ"), id, m.get("mm_id"), index++, m.get("mm_code"),
						objs[0], objs[1], m.get("mm_thisqty"));
			} else {
				SqlRowList rs = queryForRowSet("SELECT mm_prodcode FROM MakeMaterial WHERE mm_id=?", m.get("mm_id"));
				if (rs.next()) {
					getJdbcTemplate().update(ScrapDetail, getSeqId("MAKESCRAPDETAIL_SEQ"), id, m.get("mm_id"), index++, m.get("mm_code"),
							m.get("mm_detno"), rs.getString("mm_prodcode"), m.get("mm_thisqty"));
				}
			}
		}
		JSONObject js = new JSONObject();
		js.put("ms_id", id);
		js.put("ms_code", code);
		return js;
	}

	@Override
	public void setAddQty(String ma_id) {//
		// 更新已转补料数量，根据用料序号匹配，
		execute("update MakeMaterial set mm_turnaddqty=(select sum(nvl(pd_outqty,0)) from prodiodetail,prodinout "
				+ "where pd_piid=pi_id  and pi_statuscode<>'DELETE' and pd_status=0 and "
				+ "pd_ordercode=mm_code and pd_orderdetno=mm_detno  and pd_piclass in ('生产补料单', '委外补料单')) where mm_maid in (" + ma_id + ")");
		execute("update MakeMaterial set mm_thisqty=NVL(mm_scrapqty,0)+NVL(mm_returnmqty,0)+nvl((select nvl(mc_scrapqty,0)+nvl(mc_ngoutqty,0)-nvl(mc_jumponmake,0) from makecraft where MC_CODE=MM_MDCODE),0)*nvl(mm_oneuseqty,0)-(case when mm_qty=floor(mm_qty) then floor(NVL(mm_balance ,0)) else nvl(mm_balance,0) end)-NVL(mm_addqty,0)-nvl(mm_turnaddqty,0) where mm_maid in("
				+ ma_id + ")");
		// 替代料默认等于主料
		execute("update MakeMaterialreplace set mp_thisqty=(select mm_thisqty from makematerial where mp_mmid=mm_id) where mp_maid in ("
				+ ma_id + ")");
	}

	@Override
	public void setBackQty(String maidstr, Integer mm_id) {
		if (mm_id != null && mm_id > 0) {
			getJdbcTemplate()
					.execute(
							"update makematerial set mm_backqty=(select sum(pd_inqty) from prodiodetail where pd_piclass in ('生产退料单','委外退料单') and pd_ordercode=mm_code and pd_orderdetno=mm_detno and pd_status=0) WHERE mm_maid in ("
									+ maidstr + ") and mm_id=" + mm_id);
			getJdbcTemplate()
					.execute(
							"update MakeMaterialreplace set mp_backqty=(select sum(pd_inqty) from prodiodetail where pd_piclass in ('生产退料单','委外退料单') and pd_ordercode=mp_mmcode and pd_orderdetno=mp_mmdetno and pd_prodcode=mp_prodcode and pd_status=0) WHERE mp_maid in ("
									+ maidstr + ") and mp_mmid=" + mm_id);
			getJdbcTemplate()
					.execute(
							"update makematerial set mm_turnscrapqty=(select sum(md_qty) from makescrapdetail,makescrap where ms_id=md_msid and ms_statuscode<>'AUDITED' and md_mmcode=mm_code and md_mmdetno=mm_detno ) WHERE mm_maid in ("
									+ maidstr + ") and mm_id=" + mm_id);
			getJdbcTemplate()
					.execute(
							"update MakeMaterialreplace set mp_turnscrapqty=(select sum(md_qty) from makescrapdetail,makescrap where ms_id=md_msid and ms_statuscode<>'AUDITED' and md_mmcode=mp_mmcode and md_mmdetno=mp_mmdetno ) WHERE mp_maid in ("
									+ maidstr + ") and mp_mmid=" + mm_id);
		} else {
			getJdbcTemplate()
					.execute(
							"update makematerial set mm_backqty=(select sum(pd_inqty) from prodiodetail where pd_piclass in ('生产退料单','委外退料单') and pd_ordercode=mm_code and pd_orderdetno=mm_detno and pd_status=0) WHERE mm_maid in ("
									+ maidstr + ")");
			getJdbcTemplate()
					.execute(
							"update MakeMaterialreplace set mp_backqty=(select sum(pd_inqty) from prodiodetail where pd_piclass in ('生产退料单','委外退料单') and pd_ordercode=mp_mmcode and pd_orderdetno=mp_mmdetno and pd_prodcode=mp_prodcode and pd_status=0) WHERE mp_maid in ("
									+ maidstr + ")");
			getJdbcTemplate()
					.execute(
							"update makematerial set mm_turnscrapqty=(select sum(md_qty) from makescrapdetail,makescrap where ms_id=md_msid and ms_statuscode<>'AUDITED' and md_mmcode=mm_code and md_mmdetno=mm_detno ) WHERE mm_maid in ("
									+ maidstr + ")");
			getJdbcTemplate()
					.execute(
							"update MakeMaterialreplace set mp_turnscrapqty=(select sum(md_qty) from makescrapdetail,makescrap where ms_id=md_msid and ms_statuscode<>'AUDITED' and md_mmcode=mp_mmcode and md_mmdetno=mp_mmdetno ) WHERE mp_maid in ("
									+ maidstr + ")");
		}
	}

	@Override
	public void refreshTurnQty(Integer ma_id, Integer mm_id) {
		if (mm_id != null && mm_id > 0) {
			// 已转领料数
			execute("update MakeMaterial set mm_totaluseqty=(select sum(nvl(pd_outqty,0)) from prodiodetail,prodinout "
					+ "where pd_piid=pi_id and pd_status=0 and pd_ordercode=mm_code and pd_orderdetno=mm_detno and pd_piclass in ('生产领料单', '委外领料单')) WHERE mm_maid="
					+ ma_id + " and mm_id=" + mm_id);
			// 转补料数
			execute("update MakeMaterial set mm_turnaddqty=(select sum(nvl(pd_outqty,0)) from prodiodetail,prodinout "
					+ "where pd_piid=pi_id and pi_statuscode<>'DELETE' and pd_status=0 and pd_ordercode=mm_code and pd_orderdetno=mm_detno and pd_piclass in ('生产补料单', '委外补料单')) where mm_maid ="
					+ ma_id + " and mm_id=" + mm_id);
			// 更新替代已转数
			execute("update makematerialreplace set mp_repqty=(select NVL(sum(nvl(pd_outqty,0)),0) from prodiodetail,prodinout "
					+ "where pd_piid=pi_id and pd_status=0  and pd_prodcode=mp_prodcode and pd_ordercode=mp_mmcode and "
					+ "pd_orderdetno=mp_mmdetno  and pd_piclass in ('生产领料单', '委外领料单')) where mp_maid=" + ma_id + " and mp_mmid=" + mm_id);
			execute("update makematerialreplace set mp_repqty=0  where mp_maid=" + ma_id +" AND MP_REPQTY<0");
			// 更新替代总已转数
			execute("update MakeMaterial set mm_repqty=NVL((select sum(NVL(mp_repqty,0)) from MakeMaterialreplace where mp_mmid=mm_id),0) where mm_maid="
					+ ma_id + " and mm_id=" + mm_id);
			// 更新替代总已领数
			execute("update MakeMaterial set mm_haverepqty=NVL((select sum(NVL(mp_haverepqty,0)) from MakeMaterialreplace where mp_mmid=mm_id),0) where mm_maid="
					+ ma_id + " and mm_id=" + mm_id);
		} else {
			// 已转领料数
			execute("update MakeMaterial set mm_totaluseqty=(select sum(nvl(pd_outqty,0)) from prodiodetail,prodinout "
					+ "where pd_piid=pi_id and pd_status=0 and pd_ordercode=mm_code and pd_orderdetno=mm_detno and pd_piclass in ('生产领料单', '委外领料单')) WHERE mm_maid="
					+ ma_id);
			// 转补料数
			execute("update MakeMaterial set mm_turnaddqty=(select sum(nvl(pd_outqty,0)) from prodiodetail,prodinout "
					+ "where pd_piid=pi_id and pi_statuscode<>'DELETE' and pd_status=0 and pd_ordercode=mm_code and pd_orderdetno=mm_detno and pd_piclass in ('生产补料单', '委外补料单')) where mm_maid ="
					+ ma_id);
			// 更新替代已转数
			execute("update makematerialreplace set mp_repqty=(select NVL(sum(nvl(pd_outqty,0)),0) from prodiodetail,prodinout "
					+ "where pd_piid=pi_id and pd_status=0  and pd_prodcode=mp_prodcode and pd_ordercode=mp_mmcode and "
					+ "pd_orderdetno=mp_mmdetno  and pd_piclass in ('生产领料单', '委外领料单')) where mp_maid=" + ma_id);
			execute("update makematerialreplace set mp_repqty=0  where mp_maid=" + ma_id+" AND MP_REPQTY<0");
			// 更新替代总已转数
			execute("update MakeMaterial set mm_repqty=NVL((select sum(NVL(mp_repqty,0)) from MakeMaterialreplace where mp_mmid=mm_id),0) where mm_maid="
					+ ma_id);
			// 更新替代总已领数
			execute("update MakeMaterial set mm_haverepqty=NVL((select sum(NVL(mp_haverepqty,0)) from MakeMaterialreplace where mp_mmid=mm_id),0) where mm_maid="
					+ ma_id);
		}

	}

	@Override
	public Map<String, String> statistics(String condition) {
		Map<String, String> map = new HashMap<String, String>();
		String sql = "select avg(okrate) rate from (select  mc_code,exp(sum(ln(mcd_okqty/mcd_inqty))) okrate  from makecraft inner join makecraftdetail on mc_id=mcd_mcid left join craft on cr_code=mc_craftcode left join craftdetail on cd_crid=cr_id "
				+ "where " + condition + " and cd_iftest<>0 and mcd_inqty>0  group by mc_code)";
		SqlRowList rs1 = queryForRowSet(sql);
		if (rs1.next()) {
			String rate = rs1.getString("rate");
			map.put("rate", rate);
		} else {
			map.put("rate", "");
		}
		String sql1 = "select mcd_stepname,avg(mcd_okqty/mcd_inqty) okrate  from makecraft inner join makecraftdetail on mc_id=mcd_mcid left join craft on cr_code=mc_craftcode left join craftdetail on cd_crid=cr_id "
				+ "where " + condition + " and cd_iftest<>0 and mcd_inqty>0  group by mcd_stepname";
		SqlRowList rs2 = queryForRowSet(sql1);
		if (rs2.next()) {
			int i = 0;
			String labels[] = new String[rs2.size()];
			String value1[] = new String[rs2.size()];// 直通率
			String value2[] = new String[rs2.size()];// 不良率
			labels[i] = rs2.getString(1);
			value1[i] = rs2.getString(2);
			value2[i] = (1.0 - Double.parseDouble(value1[i])) + "";
			while (rs2.next()) {
				i++;
				labels[i] = rs2.getString(1);
				value1[i] = rs2.getString(2);
				value2[i] = (1.0 - Double.parseDouble(value1[i])) + "";
			}
			map.put("labels", Arrays.toString(labels));
			map.put("value1", Arrays.toString(value1));
			map.put("value2", Arrays.toString(value2));
		} else {
			map.put("labels", "");
			map.put("data2", "");
		}
		return map;
	}

	/**
	 * @author XiaoST 2016年8月25日 下午7:18:45 更新工单的完工状态，在完工入库，委外验收，验退过账，反过账时调用
	 */
	@Override
	public void updateMakeFinishStatus(Integer ma_id) {
		execute("update make set ma_finishstatuscode='COMPLETED',ma_finishstatus='" + BaseUtil.getLocalMessage("COMPLETED")
				+ "' where ma_id=" + ma_id + " and ma_madeqty>=ma_qty  ");
		execute("update make set ma_finishstatuscode='PARTFI',ma_finishstatus='" + BaseUtil.getLocalMessage("PARTFI") + "' where ma_id="
				+ ma_id + " and ma_madeqty>0 and ma_madeqty<ma_qty  ");
		execute("update make set ma_finishstatuscode='UNCOMPLET',ma_finishstatus='" + BaseUtil.getLocalMessage("UNCOMPLET")
				+ "' where ma_id=" + ma_id + " and ma_madeqty=0  ");
	}

	@Override
	public void deleteMake(int id) {
		// TODO Auto-generated method stub

	}

}
