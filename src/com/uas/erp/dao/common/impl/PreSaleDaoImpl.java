package com.uas.erp.dao.common.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.PreSaleDao;

@Repository
public class PreSaleDaoImpl extends BaseDao implements PreSaleDao{
	static final String TURNSALE = "SELECT ps_code,ps_date,ps_kind,ps_pocode,ps_sellercode,ps_seller,ps_departmentcode" + 
			",ps_departmentname,ps_custcode,ps_custname,ps_address,ps_transport,ps_paymentscode,ps_payments" + 
			",ps_currency,ps_apcustcode,ps_apcustname,ps_custprodcode,ps_prodcode,ps_qty,ps_price,ps_plandate" +
			" FROM PreSale WHERE ps_id=?";
	static final String INSERSALE = "INSERT INTO sale(sa_id,sa_code,sa_sourceid,sa_source,sa_date,sa_kind,sa_pocode,sa_sellercode,sa_seller,sa_departmentcode" +
			",sa_departmentname,sa_custcode,sa_custname,sa_toplace,sa_transport,sa_paymentscode,sa_payments" +
			",sa_currency,sa_apcustcode,sa_apcustname,sa_shcustcode,sa_shcustname,sa_custid,sa_sellerid,sa_paymentsid,sa_rate" +
			",sa_recorddate,sa_recorder,sa_recorderid,sa_statuscode,sa_status,sa_printstatus,sa_printstatuscode)" +
			" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?,?,'ENTERING',?,?,'UNPRINT')";
	static final String INSERTSALEDETAIL = "INSERT INTO saledetail(sd_id,sd_said,sd_detno,sd_sourceid,sd_source" +
			",sd_prodcode,sd_qty,sd_price,sd_delivery,sd_custprodcode,sd_prodkind,sd_statuscode,sd_status,sd_code)" +
			" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	@Override
	@Transactional
	public int turnSale(int id) {
		try {
			SqlRowList rs = queryForRowSet(TURNSALE, new Object[]{id});
			int said = 0;
			if(rs.next()){
				said = getSeqId("SALE_SEQ");
				String code = rs.getString("ps_code");
				Object[] cust = getFieldsDataByCondition("Customer", new String[]{"cu_id","cu_name"}, "cu_code='" + rs.getObject("ps_custcode") +"'");
				Object[] seller = getFieldsDataByCondition("Employee",  new String[]{"em_id", "em_name"}, "em_code='" + rs.getObject("ps_sellercode") +"'");
				Object payment = getFieldDataByCondition("Payments",  "pa_id", "pa_code='" + rs.getObject("ps_paymentscode") +"'");
				Object rate = getFieldDataByCondition("Currencys",  "cr_rate", "cr_name='" + rs.getObject("ps_currency") +"'");
				boolean bool = execute(INSERSALE, new Object[]{said,code,id,code,rs.getObject("ps_date"),rs.getObject("ps_kind"),
						rs.getObject("ps_pocode"),rs.getObject("ps_sellercode"),seller[1],rs.getObject("ps_departmentcode"),
						rs.getObject("ps_departmentname"),rs.getObject("ps_custcode"),cust[1],rs.getObject("ps_address"),
						rs.getObject("ps_transport"),rs.getObject("ps_paymentscode"),rs.getObject("ps_payments"),rs.getObject("ps_currency"),
						rs.getObject("ps_apcustcode"),rs.getObject("ps_apcustname"),rs.getObject("ps_custcode"),cust[1],cust[0],seller[0],
						payment,rate,SystemSession.getUser().getEm_name(),SystemSession.getUser().getEm_id(),BaseUtil.getLocalMessage("ENTERING"),
						BaseUtil.getLocalMessage("UNPRINT")});
				if(bool){
					int count = 1;
					int sdid = getSeqId("SALEDETAIL_SEQ");
					Object prodkind = getFieldDataByCondition("Product",  "pr_kind", "pr_code='" + rs.getObject("ps_custcode") +"'");
					execute(INSERTSALEDETAIL, new Object[]{sdid,said,count++,id,code,rs.getObject("ps_prodcode"),rs.getObject("ps_qty"),
							rs.getObject("ps_price"),rs.getObject("ps_plandate"),rs.getObject("ps_custprodcode"),prodkind,
							"ENTERING",BaseUtil.getLocalMessage("ENTERING"),code});
				}
			}
			return said;
		} catch (Exception e){
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
			return 0;
		}
	}
}
