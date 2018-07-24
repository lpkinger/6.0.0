package com.uas.erp.dao.common.impl;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.PriceMouldDao;

@Repository
public class PriceMouldDaoImpl extends BaseDao implements PriceMouldDao {
	static final String SELECTPRICEMOULD = "select * from pricemould where pd_id=?";
	static final String SELECTPRICEMOULDDET = "select * from pricemoulddet where pdd_pdid=?";
	static final String SELECTPRICEMOULDDETAIL = "select * from pricemoulddetail where pmd_pddid=?";

	static final String INSERTINQUIRYMOULD = "insert into InquiryMould ("
			+ "in_id,in_code,in_vendcode,in_vendname,in_recorddate,in_date,in_statuscode,in_sourceid,in_source,in_sourcetype,"
			+ "in_status,in_recorder,in_recorderid,in_currency,in_rate,in_sendstatus) values("
			+ "?,?,?,?,sysdate,sysdate,'ENTERING',?,?,?,?,?,?,?,?,'待上传')";
	static final String INSERTINQUIRYMOULDDET = "insert into InquiryMouldDet (idd_id,idd_inid,idd_code,idd_detno,idd_pscode,idd_psname,idd_pstype,"
			+ "idd_price,idd_pddid,idd_remark) values(?,?,?,?,?,?,?,0,?,?)";
	static final String INSERTINQUIRYMOULDDETAIL = "insert into InquiryMouldDetail (ind_id,ind_inid,ind_code,ind_detno,ind_prodcode,"
			+ "ind_price,ind_pmdid,ind_iddid,ind_remark) values(INQUIRYMOULDDETAIL_SEQ.NEXTVAL,?,?,?,?,0,?,?,?)";
	static final String INSERTPURMOULD = "insert into PURMOULD ("
			+ "pm_id,pm_code,pm_type,pm_date,pm_statuscode,pm_printstatuscode,pm_indate,pm_recordman,pm_status,pm_printstatus,"
			+ "pm_vendcode,pm_vendname,pm_paymentcode,pm_payment,pm_trandport,pm_priceterm,pm_buyercode,"
			+ "pm_buyer,pm_source,pm_sourceid,pm_currency,pm_rate,pm_iscust,pm_custcode,pm_custname,pm_paystatus,pm_shipaddresscode) values("
			+ "?,?,'模具采购单',sysdate,'ENTERING','UNPRINT',sysdate,?,?,?," + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'未付款',?)";
	static final String INSERTPURMOULDDETAIL = "insert into PURMOULDDetail (pmd_id,pmd_pmid,pmd_code,pmd_detno,pmd_qty,"
			+ "pmd_price,pmd_total,pmd_pscode,pmd_returnqty,pmd_rate) values(" + "PURMOULDDETAIL_SEQ.NEXTVAL,?,?,?,?,?,0,?,?,?)";

	@Override
	@Transactional
	public JSONObject turnInquiry(int id, String vendcode, String caller) {
		SqlRowList rs = queryForRowSet(SELECTPRICEMOULD, new Object[] { id });
		int inid = 0;
		if (rs.next()) {
			inid = getSeqId("INQUIRYMOULD_SEQ");
			String code = sGetMaxNumber("Inquiry!Mould", 2);
			String sourcecode = rs.getString("pd_code");
			Object[] vend = getFieldsDataByCondition("Vendor", new String[] { "ve_name", "ve_currency", "ve_rate" }, "ve_code='" + vendcode
					+ "'");
			if (vend != null) {
				boolean bool = execute(INSERTINQUIRYMOULD,
						new Object[] { inid, code, vendcode, vend[0], id, sourcecode, "模具报价单", BaseUtil.getLocalMessage("ENTERING"),
								SystemSession.getUser().getEm_name(), SystemSession.getUser().getEm_id(), vend[1], vend[2] });
				if (bool) {
					execute("update inquiryMould set in_rate=nvl((select cm_crrate from currencysmonth where cm_yearmonth=to_char(in_date,'yyyymm') and cm_crname=in_currency),1) where in_id="
							+ inid);
					rs = queryForRowSet(SELECTPRICEMOULDDET, new Object[] { id });
					int count = 1;
					while (rs.next()) {
						int iddid = getSeqId("INQUIRYMOULDDET_SEQ");
						execute(INSERTINQUIRYMOULDDET,
								new Object[] { iddid, inid, code, count++, rs.getObject("pdd_pscode"), rs.getObject("pdd_psname"),
										rs.getObject("pdd_pstype"), rs.getObject("pdd_id"), rs.getObject("pdd_remark") });
						SqlRowList rd = queryForRowSet(SELECTPRICEMOULDDETAIL, new Object[] { rs.getObject("pdd_id") });
						Object no = null;
						while (rd.next()) {
							no = getFieldDataByCondition("INQUIRYMOULDDETAIL", "max(nvl(ind_detno,0))", "ind_inid=" + inid);
							no = no == null ? 0 : no;
							int detno = Integer.parseInt(no.toString()) + 1;
							execute(INSERTINQUIRYMOULDDETAIL,
									new Object[] { inid, code, detno, rd.getObject("pmd_prodcode"), rd.getObject("pmd_id"), iddid,
											rd.getObject("pmd_remark") });
						}
					}
					logger.turn("转模具询价单" + code, "PriceMould", "pd_id", id);
					JSONObject j = new JSONObject();
					j.put("in_id", inid);
					j.put("in_code", code);
					return j;
				}
			}
		}
		return null;
	}

	@Transactional
	public int turnPurMould(int id, String pricecolumn, String returncolumn, String caller) {
		try {
			SqlRowList rs = queryForRowSet(SELECTPRICEMOULD, new Object[] { id });
			int pmid = 0;
			if (rs.next()) {
				pmid = getSeqId("PURMOULD_SEQ");
				String code = sGetMaxNumber("PurMould", 2);
				String sourcecode = rs.getString("pd_code");
				Object[] vend = getFieldsDataByCondition("Vendor", new String[] { "ve_code", "ve_name", "ve_paymentcode", "ve_payment",
						"ve_add1", "ve_buyercode", "ve_buyername", "ve_shipment", "ve_priceterm" },
						"ve_code='" + rs.getString("pd_vendcode") + "'");
				boolean bool = execute(
						INSERTPURMOULD,
						new Object[] { pmid, code, SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("ENTERING"),
								BaseUtil.getLocalMessage("UNPRINT"), vend[0], vend[1], rs.getObject("pd_paymentscode"),
								rs.getObject("pd_payments"), vend[7], vend[8], vend[5], vend[6], sourcecode, id,
								rs.getObject("pd_currency"), rs.getObject("pd_rate"), rs.getObject("pd_iscust"),
								rs.getObject("pd_custcode"), rs.getObject("pd_custname"), vend[4] });
				if (bool) {
					execute("update PURMOULD set (pm_prjcode,pm_prjname)=(select app_prjcode,app_prjname from PriceMould,AppMould where pd_code=pm_source and pd_appmouldcode=app_code and nvl(app_prjcode,' ')<>' ')"
							+ " where pm_id=" + pmid + " and nvl(pm_prjcode,' ')=' '");
					SqlRowList rd = queryForRowSet("SELECT * FROM PriceMouldDet where pdd_pdid=" + id);
					int count = 1;
					while (rd.next()) {
						execute(INSERTPURMOULDDETAIL,
								new Object[] { pmid, code, count++, rd.getObject("pdd_qty"), rd.getObject(pricecolumn),
										rd.getObject("pdd_pscode"), rs.getObject(returncolumn), rs.getObject("pd_taxrate") });
					}
					execute("update PURMOULDDetail set pmd_total = round(pmd_qty*pmd_price,2) where pmd_pmid= " + pmid);
					execute("insert into PURMOULDDet(pd_id,pd_pmid,pd_detno,pd_paydesc,pd_isfinal,pd_amount,pd_yamount) "
							+ "select PURMOULDDET_SEQ.NEXTVAL, PM_ID, 1, '尾款', 1, pm_taxtotal, 0 from PURMOULD where pm_id=" + pmid);
				}
				return pmid;
			}
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
			return 0;
		}
		return 0;
	}
}
