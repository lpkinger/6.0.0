package com.uas.erp.dao.common.impl;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.AppMouldDao;

@Repository
public class AppMouldDaoImpl extends BaseDao implements AppMouldDao {
	static final String SELECTAPPMOULD = "select * from AppMould where app_id=?";

	static final String SELECTAPPMOULDDETAIL = "select * from AppMouldDetail left join ProductSet on ad_pscode=ps_code where ad_appid=? and abs(nvl(ad_isoffer,0))=1";

	static final String INSERTPRICEMOULD = "insert into PriceMould (pd_id,pd_code,pd_appmouldcode,"
			+ "pd_printstatus,pd_status,pd_statuscode," + "pd_printstatuscode,pd_class,pd_date,pd_iscust,"
			+ "pd_custcode,pd_custname,pd_sourcedetailid,pd_sourcetype,pd_inman,pd_indate) values("
			+ "?,?,?,?,?,'ENTERING','UNPRINT','模具报价单',sysdate,?,?,?,?,'开模申请单',?,sysdate)";

	static final String PRODUCTSETDETAIL = "select * from PRODUCTSETDETAIL where psd_psid=?";
	static final String APPMOULDDET = "select * from APPMOULDDET where amd_pscode=? and amd_adid = ?";

	static final String PRICEMOULDDETAIL = "insert into PRICEMOULDDETAIL (pmd_id,pmd_pdid,pmd_pddid,pmd_detno,pmd_code,pmd_prodcode,pmd_pscode,pmd_psname,"
			+ "pmd_samedetno) values(PRICEMOULDDETAIL_SEQ.NEXTVAL,?,?,?,?,?,?,?,?)";

	static final String PRICEMOULDDET = "insert into PRICEMOULDDET (pdd_id,pdd_pdid,pdd_detno,pdd_code,pdd_pscode,pdd_psname,"
			+ "pdd_pstype,pdd_qty,pdd_adid) values(?,?,?,?,?,?,?,?,?)";

	@Override
	@Transactional
	public JSONObject turnPriceMould(Object app_id) {
		int pdid = getSeqId("PRICEMOULD_SEQ");
		String code = sGetMaxNumber("PriceMould", 2);
		JSONObject j = new JSONObject();
		SqlRowList rs = queryForRowSet(SELECTAPPMOULD, new Object[] { app_id });
		if (rs.next()) {
			boolean bool = execute(
					INSERTPRICEMOULD,
					new Object[] { pdid, code, rs.getString("app_code"), BaseUtil.getLocalMessage("UNPRINT"),
							BaseUtil.getLocalMessage("ENTERING"), rs.getString("app_iscust"), rs.getString("app_custcode"),
							rs.getString("app_custname"), rs.getObject("app_id"), SystemSession.getUser().getEm_name() });
			if (bool) {
				execute("update PRICEMOULD set (pd_man,pd_spec,pd_prjcode,pd_prjname)=(select prj_assignto,app_descrip,app_prjcode,app_prjname from AppMould left join project on app_prjcode=prj_code where app_id="
						+ app_id + ") where pd_id=" + pdid);
				j.put("pd_id", pdid);
				j.put("pd_code", code);
			}
		}
		return j;
	}

	static final String PURMOULD = "INSERT into PURMOULD ("
			+ "pm_id,pm_code,pm_status,pm_vendcode,pm_vendname,pm_currency,pm_rate,pm_iscust,pm_recordman,pm_buyercode,pm_buyer,pm_printstatus,"
			+ "pm_shipaddresscode,pm_trandport,pm_indate,pm_date,pm_cop,pm_printstatuscode,pm_statuscode,pm_type) values("
			+ "?,?,?,?,?,?,?,?,?,?,?,?,'深圳市宝安区福永镇新和村新兴工业园6区A1栋','送货上门',sysdate,sysdate,'善领科技','UNPRINT','ENTERING','模具采购单')";

	@Override
	public JSONObject newPurMould(Object vendcode, Object iscust, Object pmcode, String caller) {
		String code = null;
		JSONObject j = new JSONObject();
		int pmid = 0;
		if (pmcode != null && !"".equals(pmcode)) {
			pmid = Integer.parseInt(getFieldDataByCondition("PurMould", "pm_id", "pm_code='" + pmcode + "'").toString());
			code = pmcode.toString();
			j.put("pm_id", pmid);
			j.put("pm_code", code);
			if (vendcode != null && !"".equals(vendcode)) {
				Object[] vend = getFieldsDataByCondition("Vendor left join employee on ve_buyerid=em_id", new String[] { "ve_name",
						"ve_currency", "ve_rate", "em_code", "ve_buyername" }, "ve_code='" + vendcode + "'");
				execute("update PURMOULD set pm_vendcode='" + vendcode + "',pm_vendname='" + vend[0] + "',pm_currency='" + vend[1]
						+ "',pm_rate=" + vend[2] + ",pm_buyercode='" + vend[3] + "',pm_buyer='" + vend[4] + "' where pm_id=" + pmid
						+ " and nvl(pm_vendcode,' ')=' '");
			}
			return j;
		} else {
			code = sGetMaxNumber("Purc!Mould", 2);
			pmid = getSeqId("PURMOULD_SEQ");
			boolean bool = false;
			if (vendcode != null && !"".equals(vendcode)) {
				Object[] vend = getFieldsDataByCondition("Vendor left join employee on ve_buyerid=em_id", new String[] { "ve_name",
						"ve_currency", "ve_rate", "em_code", "ve_buyername" }, "ve_code='" + vendcode + "'");
				bool = execute(PURMOULD, new Object[] { pmid, code, BaseUtil.getLocalMessage("ENTERING"), vendcode, vend[0], vend[1],
						vend[2], iscust, SystemSession.getUser(), vend[3], vend[4], BaseUtil.getLocalMessage("UNPRINT") });
			} else {
				bool = execute(PURMOULD, new Object[] { pmid, code, BaseUtil.getLocalMessage("ENTERING"), null, null, null, null, iscust,
						SystemSession.getUser(), null, null, BaseUtil.getLocalMessage("UNPRINT") });
			}
			if (bool) {
				j.put("pm_id", pmid);
				j.put("pm_code", code);
				return j;
			}
		}
		return null;
	}

	static final String APPMOUIDDETAIL = "SELECT ad_code,ad_pscode FROM APPMOULDdetail WHERE ad_id=?";
	static final String PURMOULDDETAIL = "INSERT INTO PURMOULDDETAIL(pmd_id,pmd_pmid,pmd_code"
			+ ",pmd_detno,pmd_pscode,pmd_sourceid,pmd_source,pmd_sourcetype) VALUES (?,?,?,?,?,?,?,?)";

	@Override
	public void toAppointedPurMould(int pm_id, String pm_code, Object ad_id, int detno, String type) {
		SqlRowList rs = queryForRowSet(APPMOUIDDETAIL, new Object[] { ad_id });
		if (rs.next()) {
			Object count = getFieldDataByCondition("PurMouldDetail", "max(pmd_detno)", "pmd_code='" + pm_code + "'");
			count = count == null ? 0 : count;
			try {
				execute(PURMOULDDETAIL, new Object[] { getSeqId("PURMOULDDETAIL_SEQ"), pm_id, pm_code, detno, rs.getObject("ad_pscode"),
						ad_id, rs.getObject("ad_code"), type });
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	static final String APPMOULD = "SELECT * FROM APPMOULD WHERE app_id=?";
	static final String INSERTMOULDSALE = "INSERT INTO MOD_Sale(msa_id,msa_code,msa_date,msa_status,msa_custcode,"
			+ "msa_custname,msa_sellercode,msa_sellername,msa_currency,msa_rate,msa_transport,msa_depart,"
			+ "msa_recorder,msa_indate,msa_remark,msa_chargestatus,msa_statuscode,msa_chargestatuscode,"
			+ "msa_sourcecode,msa_sourceid,msa_sourcetype,msa_class,msa_taxrate) VALUES (?,?,sysdate,?,?,?,?,?,?,?,?,?,?,sysdate,"
			+ "?,?,'ENTERING','UNCOLLECT',?,?,'开模申请单','模具销售单',?)";
	static final String INSERTMOULDSALEDETAIL = "INSERT INTO MOD_SaleDetail(msd_id,msd_msaid,msd_code,msd_detno,msd_adid,"
			+ "msd_pscode,msd_qty,msd_remark) VALUES (MOD_SALEDETAIL_SEQ.NEXTVAL,?,?,?,?,?,?,?)";

	@Override
	public JSONObject turnMouldSale(int id) {
		SqlRowList rs = queryForRowSet(APPMOULD, new Object[] { id });
		if (rs.next()) {
			int msaid = getSeqId("MOD_SALE_SEQ");
			String code = sGetMaxNumber("MouldSale", 2);
			Object[] cust = getFieldsDataByCondition("Customer", new String[] { "cu_currency", "cu_rate", "cu_shipment", "cu_cop" },
					"cu_code='" + rs.getString("app_custcode") + "'");
			boolean bool = execute(
					INSERTMOULDSALE,
					new Object[] { msaid, code, BaseUtil.getLocalMessage("ENTERING"), rs.getString("app_custcode"),
							rs.getObject("app_custname"), rs.getObject("app_sellercode"), rs.getObject("app_seller"), cust[0], cust[1],
							cust[2], rs.getObject("app_depart"), SystemSession.getUser().getEm_name(), rs.getObject("app_remark"),
							BaseUtil.getLocalMessage("UNCOLLECT"), rs.getObject("app_code"), id, rs.getObject("app_taxrate") });

			if (bool) {
				SqlRowList rd = queryForRowSet(SELECTAPPMOULDDETAIL, new Object[] { id });
				int count = 1;
				while (rd.next()) {
					execute(INSERTMOULDSALEDETAIL, new Object[] { msaid, code, count++, rd.getObject("ad_id"), rd.getObject("ad_pscode"),
							rd.getObject("ad_qty"), rd.getObject("ad_remark") });
				}
			}
			JSONObject j = new JSONObject();
			j.put("msa_id", msaid);
			j.put("msa_code", code);
			return j;
		}
		return null;
	}

	/**
	 * 修改转报价状态
	 */
	@Override
	public void checkAdQty(int adid) {
		Object appid = getFieldDataByCondition("AppMouldDetail", "ad_appid", "ad_id=" + adid);
		int total = getCountByCondition("AppMouldDetail", "ad_appid=" + appid);
		int audit = getCountByCondition("AppMouldDetail", "ad_appid=" + appid + " AND nvl(ad_statuscode,' ')<>'TURNPM'");
		int turn = getCountByCondition("AppMouldDetail", "ad_appid=" + appid + " AND nvl(ad_statuscode,' ')='TURNPM'");
		String status = "PARTPM";
		if (audit == total) {
			status = null;
		} else if (turn == total) {
			status = "TURNPM";
		}
		updateByCondition("AppMould", "app_turnpricecode='" + status + "',app_turnprice='" + BaseUtil.getLocalMessage(status) + "'",
				"app_id=" + appid);
	}

	@Override
	public void toAppointedPriceMould(int pd_id, Object pd_code, int ad_id, int detno) {
		SqlRowList rs = queryForRowSet("select * from AppMouldDetail left join ProductSet on ad_pscode=ps_code where ad_id=?",
				new Object[] { ad_id });
		int pdd_id = 0;
		if (rs.next()) {
			pdd_id = getSeqId("PRICEMOULDDET_SEQ");
			execute(PRICEMOULDDET,
					new Object[] { pdd_id, pd_id, detno, pd_code, rs.getObject("ad_pscode"), rs.getObject("ps_name"),
							rs.getObject("ps_type"), rs.getObject("ad_qty"), rs.getObject("ad_id") });
			SqlRowList rd = queryForRowSet(APPMOULDDET, new Object[] { rs.getObject("ps_code"),ad_id });
			Object no = null;
			while (rd.next()) {
				no = getFieldDataByCondition("PRICEMOULDDETAIL", "max(nvl(pmd_detno,0))", "pmd_pdid=" + pd_id);
				no = no == null ? 0 : no;
				int count = Integer.parseInt(no.toString()) + 1;
				execute(PRICEMOULDDETAIL,
						new Object[] { pd_id, pdd_id, count, pd_code, rd.getObject("amd_prodcode"), rd.getObject("amd_pscode"),
								rd.getObject("amd_psname"), rd.getObject("amd_detno") });
			}
			execute("update AppMouldDetail set ad_statuscode='TURNPM', ad_status='" + BaseUtil.getLocalMessage("TURNPM") + "' where ad_id="
					+ ad_id);
			// 记录操作
			logger.turnDetail("转模具报价单", "AppMould", "app_id", rs.getObject("ad_appid"), "行号：" + rs.getObject("ad_detno"));
		}
	}
}
