package com.uas.erp.dao.common.impl;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.MouldSaleDao;

@Repository
public class MouldSaleDaoImpl extends BaseDao implements MouldSaleDao {
	static final String SELECTMODSALE = "select * from mod_sale where msa_id=?";

	static final String INSERTMODDELIVERYORDER = "insert into MOD_DeliveryOrder (md_code,md_class,md_date,"
			+ "md_auditstatus,md_custcode,md_custname,md_sellercode,md_sellername,md_currency,md_rate,md_add,"
			+ "md_transport,md_recorder,md_indate,md_depart,md_remark,md_pocode,md_custprodcode,md_amount,"
			+ "md_printstatus,md_status,md_statuscode,md_auditstatuscode,md_printstatuscode,md_id,md_sourcecode,"
			+ "md_sourceid,md_total,md_taxrate) values(?,'模具出货单',sysdate,?,?,?,?,?,?,?,?,?,?,sysdate,?,?,?,?,?,"
			+ "?,?,'UNPOST','ENTERING','UNPRINT',?,?,?,?,?)";

	static final String MODSALEDETAIL = "select * from MOD_SALEDETAIL where msd_msaid=?";

	static final String MODDELIVERYORDERDETAIL = "insert into MOD_DELIVERYORDERDETAIL (mdd_id,mdd_mdid,mdd_code,mdd_detno,"
			+ "mdd_msdid,mdd_pscode,mdd_qty,mdd_price,mdd_amount,mdd_taxrate,mdd_remark) values(MOD_DELIVERYORDERDETAIL_SEQ.NEXTVAL,"
			+ "?,?,?,?,?,?,?,?,?,?)";

	@Override
	@Transactional
	public JSONObject turnDeliveryOrder(int id) {
		SqlRowList rs = queryForRowSet(SELECTMODSALE, new Object[] { id });
		if (rs.next()) {
			int mdid = getSeqId("MOD_DELIVERYORDER_SEQ");
			String code = sGetMaxNumber("DeliveryOrder", 2);
			boolean bool = execute(
					INSERTMODDELIVERYORDER,
					new Object[] { code, BaseUtil.getLocalMessage("ENTERING"), rs.getString("msa_custcode"),
							rs.getObject("msa_custname"), rs.getObject("msa_sellercode"), rs.getObject("msa_sellername"),
							rs.getObject("msa_currency"), rs.getObject("msa_rate"), rs.getObject("msa_add"), rs.getObject("msa_transport"),
							SystemSession.getUser().getEm_name(), rs.getObject("msa_depart"), rs.getObject("msa_remark"), rs.getObject("msa_pocode"),
							rs.getObject("msa_custprodcode"), rs.getObject("msa_amount"), BaseUtil.getLocalMessage("UNPRINT"),
							BaseUtil.getLocalMessage("UNPOST"), mdid, rs.getObject("msa_code"), rs.getObject("msa_id"),
							rs.getObject("msa_total"), rs.getObject("msa_taxrate")});
			if (bool) {
				SqlRowList rd = queryForRowSet(MODSALEDETAIL, new Object[] { id });
				int count = 1;
				while (rd.next()) {
					execute(MODDELIVERYORDERDETAIL, new Object[] { mdid, code, count++, rd.getObject("msd_id"), rd.getObject("msd_pscode"),
							rd.getObject("msd_qty"), rd.getObject("msd_price"), rd.getObject("msd_amount"), rd.getObject("msd_taxrate"),
							rd.getObject("msd_remark") });
				}
			}
			JSONObject j = new JSONObject();
			j.put("md_id", mdid);
			j.put("md_code", code);
			return j;
		}
		return null;
	}
}
