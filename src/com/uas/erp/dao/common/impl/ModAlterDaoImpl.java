package com.uas.erp.dao.common.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ModAlterDao;

@Repository
public class ModAlterDaoImpl extends BaseDao implements ModAlterDao {
	static final String SELECTMODALTER = "select * from MOD_ALTER where al_id=?";

	static final String INSERTMOULDFEEPLEASE = "insert into MOULDFEEPLEASE ("
			+ "mp_id,mp_code,mp_recorddate,mp_recordman,mp_vendcode,mp_vendname,mp_pleaseman,"
			+ "mp_status,mp_printstatus,mp_total,mp_changecode,mp_remark,mp_statuscode,"
			+ "mp_printstatuscode,mp_paymentscode,mp_payments, mp_source, mp_sourcetype, mp_sourceid) values("
			+ "?,?,sysdate,?,?,?,?,?,?,?,?,?,'ENTERING','UNPRINT',?,?,?,?,?)";
	static final String INSERTMOULDSALE = "INSERT INTO MOD_Sale(msa_id,msa_code,msa_date,msa_status,"
			+ "msa_recorder,msa_indate,msa_remark,msa_chargestatus,msa_statuscode,"
			+ "msa_sourcecode,msa_sourceid,msa_sourcetype,msa_class,msa_taxrate) VALUES (?,?,sysdate,?,?,sysdate,"
			+ "?,?,'ENTERING',?,?,'模具修改申请单','模具销售单',?)";
	static final String SELECTMODALTERDETAIL = "select * from MOD_ALTERDETAIL left join MOD_ALTER on al_id=ald_alid where ald_alid=?";
	static final String INSERTMOULDSALEDETAIL = "INSERT INTO MOD_SaleDetail(msd_id,msd_msaid,msd_code,msd_detno,msd_adid,"
			+ "msd_pscode,msd_qty,msd_remark) VALUES (MOD_SALEDETAIL_SEQ.NEXTVAL,?,?,?,?,?,?,?)";
	static final String PRODUCTSETDETAIL = "select psd_code,psd_prodcode,psd_detno from PRODUCTSETDETAIL where psd_psid=?";
	static final String INSERTPRICEMOULD = "insert into PriceMould (pd_id,pd_code,pd_sourcecode,"
			+ "pd_man,pd_spec,pd_pscode,pd_psname,pd_auditman,pd_printstatus,pd_status,pd_statuscode,"
			+ "pd_printstatuscode,pd_class,pd_date,pd_auditdate,pd_iscust,pd_sourcetype,pd_qty) values(" + "?,?,?,?,?,?,?,?,"
			+ "?,?,'ENTERING','UNPRINT','模具报价单',sysdate,sysdate,?,'模具修改申请单',?)";
	static final String PRICEMOULDDETAIL = "insert into PRICEMOULDDETAIL (" + "pmd_id,pmd_pdid,pmd_detno,pmd_code,pmd_prodcode,pmd_pscode,"
			+ "pmd_samedetno) values(PRICEMOULDDETAIL_SEQ.NEXTVAL,?,?,?,?,?,?)";

	@Override
	@Transactional
	public int turnFeePlease(int id, double aldamount) {
		try {
			int mpid = 0;
			SqlRowList rs = queryForRowSet(SELECTMODALTER, new Object[] { id });
			if (rs.next()) {
				mpid = getSeqId("MOULDFEEPLEASE_SEQ");
				String code = sGetMaxNumber("FeePlease!Mould", 2);
				String sourcecode = rs.getString("al_code");
				Object[] vend = getFieldsDataByCondition("Vendor", new String[] { "ve_code", "ve_name", "ve_paymentcode", "ve_payment" },
						"ve_code='" + rs.getObject("al_vendcode") + "'");
				boolean bool = execute(
						INSERTMOULDFEEPLEASE,
						new Object[] { mpid, code, SystemSession.getUser().getEm_name(), vend[0], vend[1],
								SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("ENTERING"),
								BaseUtil.getLocalMessage("UNPRINT"), aldamount, sourcecode, "改模申请单:" + sourcecode, vend[2], vend[3],
								sourcecode, "改模申请单", id });
				if (bool) {
					execute("insert into MOULDFEEPLEASEDETAIL(mfd_id,mfd_mpid,mfd_code,mfd_detno,mfd_pscode,mfd_payments,mfd_amount) "
							+ " select MOULDFEEPLEASEDETAIL_SEQ.NEXTVAL," + mpid + ",'" + code + "',ald_detno,ald_pscode,'改修款',ald_amount "
							+ " FROM MOD_ALTERDETAIL WHERE ald_alid=" + id);
				}
				return mpid;
			}
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
			return 0;
		}
		return 0;
	}

	@Override
	public List<Map<String, Object>> turnPriceMould(int id) {
		try {
			SqlRowList rs = queryForRowSet(SELECTMODALTERDETAIL, new Object[] { id });
			int pdid = 0;
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			while (rs.next()) {
				pdid = getSeqId("PRICEMOULD_SEQ");
				String code = sGetMaxNumber("PriceMould", 2);
				Object[] objs = getFieldsDataByCondition("ProductSet", new String[] { "ps_name", "ps_id", "ps_description" }, "ps_code='"
						+ rs.getObject("ald_pscode") + "'");
				boolean bool = execute(
						INSERTPRICEMOULD,
						new Object[] { pdid, code, rs.getString("al_code"), rs.getObject("al_pleaseman"), objs[2],
								rs.getObject("ald_pscode"), objs[0], SystemSession.getUser().getEm_name(),
								BaseUtil.getLocalMessage("UNPRINT"), BaseUtil.getLocalMessage("ENTERING"), rs.getString("al_iscust"),
								rs.getObject("ald_qty") });
				if (bool) {
					SqlRowList rd = queryForRowSet(PRODUCTSETDETAIL, new Object[] { objs[1] });
					int count = 1;
					Map<String, Object> map = new HashMap<String, Object>();
					while (rd.next()) {
						execute(PRICEMOULDDETAIL,
								new Object[] { pdid, count++, code, rd.getObject("psd_prodcode"), rd.getObject("psd_code"),
										rd.getObject("psd_detno") });
					}
					map.put("id", pdid);
					map.put("code", code);
					list.add(map);
				}
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
			return null;
		}
	}

	@Override
	public JSONObject turnMouldSale(int id) {
		SqlRowList rs = queryForRowSet(SELECTMODALTER, id);
		if (rs.next()) {
			int msaid = getSeqId("MOD_SALE_SEQ");
			String code = sGetMaxNumber("MouldSale", 2);
			boolean bool = execute(
					INSERTMOULDSALE,
					new Object[] { msaid, code, BaseUtil.getLocalMessage("ENTERING"), SystemSession.getUser().getEm_name(),
							rs.getObject("al_remark"), "未收款", rs.getObject("al_code"), id, rs.getObject("al_taxrate") });

			if (bool) {
				if (rs.getObject("al_custcode") != null) {
					Object[] cust = getFieldsDataByCondition("Customer", new String[] { "cu_currency", "cu_rate", "cu_shipment", "cu_cop",
							"cu_sellercode", "cu_sellername" }, "cu_code='" + rs.getString("al_custcode") + "'");
					execute("update MOD_Sale set msa_custcode='" + rs.getString("al_custcode") + "', msa_custname='"
							+ rs.getObject("al_custname") + "', msa_sellercode='" + cust[4] + "', msa_sellername='" + cust[5] + "',"
							+ " msa_currency='" + cust[0] + "', msa_rate=" + cust[1] + ", msa_transport='" + cust[2] + "', msa_depart='"
							+ cust[3] + "' where msa_id=" + msaid);
				}
				SqlRowList rd = queryForRowSet(SELECTMODALTERDETAIL, new Object[] { id });
				int count = 1;
				while (rd.next()) {
					execute(INSERTMOULDSALEDETAIL, new Object[] { msaid, code, count++, rd.getObject("ald_id"), rd.getObject("ald_pscode"),
							rd.getObject("ald_qty"), rd.getObject("ald_remark") });
				}
			}
			JSONObject j = new JSONObject();
			j.put("msa_id", msaid);
			j.put("msa_code", code);
			return j;
		}
		return null;
	}
}
