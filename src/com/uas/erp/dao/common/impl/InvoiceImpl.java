package com.uas.erp.dao.common.impl;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.InvoiceDao;
import com.uas.erp.model.Employee;

@Repository
public class InvoiceImpl extends BaseDao implements InvoiceDao {
	final static String INSERT_PACKINGDETAIL = "insert into PackingDetail(pd_id, pd_piid, pd_code, pd_detno, pd_ordercode, pd_orderdetno,"
			+ "pd_prodcode, pd_qty, pd_price, pd_total, pd_pocode, pd_custprodcode, pd_outboxlength, pd_outboxwidth, pd_outboxheight, "
			+ "pd_outerboxgw, pd_outerboxnw, pd_discount, pd_cartonno, pd_cartons, pd_remark, pd_custprodspec_user) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	final static String INSERT_INVOICEDETAIL = "insert into InvoiceDetail(id_id, id_inid, id_code, id_detno, id_ordercode, id_orderdetno,"
			+ "id_prodcode, id_qty, id_price, id_total, id_pocode, id_custprodcode, id_outboxlength, id_outboxwidth, id_outboxheight, "
			+ "id_outerboxgw, id_outerboxnw, id_discount, id_cartonno, id_cartons, id_remark, ID_SENDQTY,id_custprodspec_user) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,0,?)";

	/**
	 * 出货单，销售退货单明细：分别合并生成装箱单，发票明细
	 */
	@Override
	public void detailTurnPaInDetail(String no, String piids, Object inid, Object piid) {
		// 按物料號+po號合并明細转发票
		// 分组条件+最小包装量
		String sql = "SELECT pr_spec, pd_pocode, pd_sendprice, max(pd_ordercode), max(pd_orderdetno), max(pd_prodcode), sum(nvl(pd_outqty,0)+nvl(pd_inqty,0)),"
				+ "sum((nvl(pd_outqty,0)+nvl(pd_inqty,0))*nvl(pd_sendprice,0))/sum(nvl(pd_outqty,0)+nvl(pd_inqty,0)), max(pd_custprodcode) ,max(nvl(pd_outboxlength,0)), max(nvl(pd_outboxwidth,0)),"
				+ "max(nvl(pd_outboxheight,0)), max(nvl(pd_outerboxgw,0)), max(nvl(pd_outerboxnw,0)), max(nvl(pd_discount,0)), max(pd_cartonno),sum(pd_cartons),"
				+ "max(pd_remark),max(pd_custprodspec) FROM (SELECT * FROM ProdIODetail,Product where pd_prodcode=pr_code and pd_piid in ("
				+ piids
				+ ") order by pd_piid,pd_pdno) group by pr_spec,pd_pocode,pd_custprodcode,pd_custprodspec,pd_sendprice,pd_prodcode order by pr_spec,pd_prodcode";
		SqlRowList rs = queryForRowSet(sql);
		int detno = 1;
		int count = 1;
		while (rs.next()) {
			Double qty = rs.getDouble(7);
			Double price = NumberUtil.formatDouble(rs.getDouble(8), 8);
			Double total = NumberUtil.formatDouble(rs.getDouble(7) * rs.getDouble(8), 2);
			execute(INSERT_PACKINGDETAIL,
					new Object[] { getSeqId("PACKINGDETAIL_SEQ"), piid, no, detno++, rs.getObject(4), rs.getObject(5), rs.getObject(6),
							qty, price, total, rs.getObject(2), rs.getObject(9), rs.getObject(10), rs.getObject(11), rs.getObject(12),
							rs.getObject(13), rs.getObject(14), rs.getObject(15), rs.getObject(16), rs.getObject(17), rs.getObject(18),
							rs.getObject(19) });
			execute(INSERT_INVOICEDETAIL,
					new Object[] { getSeqId("INVOICEDETAIL_SEQ"), inid, no, count++, rs.getObject(4), rs.getObject(5), rs.getObject(6),
							qty, price, total, rs.getObject(2), rs.getObject(9), rs.getObject(10), rs.getObject(11), rs.getObject(12),
							rs.getObject(13), rs.getObject(14), rs.getObject(15), rs.getObject(16), rs.getObject(17), rs.getObject(18),
							rs.getObject(19) });
		}
		// 更新发票单长宽高、产地
		execute("update InvoiceDetail set id_outboxlength=(select pr_length from Product where id_prodcode=pr_code) where id_code='" + no
				+ "' and nvl(id_outboxlength,0)=0");
		execute("update InvoiceDetail set id_outboxwidth=(select pr_width from Product where id_prodcode=pr_code) where id_code='" + no
				+ "' and nvl(id_outboxwidth,0)=0");
		execute("update InvoiceDetail set id_outboxheight=(select pr_height from Product where id_prodcode=pr_code) where id_code='" + no
				+ "' and nvl(id_outboxheight,0)=0");
		execute("update InvoiceDetail set id_outerboxgw=(select pr_outerboxgw from Product where id_prodcode=pr_code) where id_code='" + no
				+ "' and nvl(id_outerboxgw,0)=0");
		execute("update InvoiceDetail set id_outerboxnw=(select pr_outerboxnw from Product where id_prodcode=pr_code) where id_code='" + no
				+ "' and nvl(id_outerboxnw,0)=0");
		execute("update InvoiceDetail set id_madein=(select pr_madein from Product where id_prodcode=pr_code) where id_code='" + no
				+ "' and nvl(id_madein,' ')=' '");
		execute("update InvoiceDetail set ID_SIZE_USER=(select pr_size from Product where id_prodcode=pr_code) where id_code='" + no
				+ "' and nvl(ID_SIZE_USER,' ')=' '");
		// 更新装箱单长宽高、产地
		execute("update PackingDetail set pd_outboxlength=(select pr_length from Product where pd_prodcode=pr_code) where pd_code='" + no
				+ "' and nvl(pd_outboxlength,0)=0");
		execute("update PackingDetail set pd_outboxwidth=(select pr_width from Product where pd_prodcode=pr_code) where pd_code='" + no
				+ "' and nvl(pd_outboxwidth,0)=0");
		execute("update PackingDetail set pd_outboxheight=(select pr_height from Product where pd_prodcode=pr_code) where pd_code='" + no
				+ "' and nvl(pd_outboxheight,0)=0");
		execute("update PackingDetail set pd_outerboxgw=(select pr_outerboxgw from Product where pd_prodcode=pr_code) where pd_code='" + no
				+ "' and nvl(pd_outerboxgw,0)=0");
		execute("update PackingDetail set pd_outerboxnw=(select pr_outerboxnw from Product where pd_prodcode=pr_code) where pd_code='" + no
				+ "' and nvl(pd_outerboxnw,0)=0");
		execute("update PackingDetail set pd_madein=(select pr_madein from Product where pd_prodcode=pr_code) where pd_code='" + no
				+ "' and nvl(pd_madein,' ')=' '");
		execute("update PackingDetail set PD_SIZE_USER=(select pr_size from Product where pd_prodcode=pr_code) where pd_code='" + no
				+ "' and nvl(PD_SIZE_USER,' ')=' '");
	}
	
	private JSONObject getCode(String caller){
		JSONObject obj = null;
		Long number = null;
		String code = "";
		if (isDBSetting(caller, "autoCode")) {
			Long ret = getFieldValue("Invoice", "nvl(max(in_number),0)", "nvl(in_number,0)<>0", Long.class);
			number = getJdbcTemplate()
					.queryForObject(
							"select nvl(MIN(t.num),1) FROM (select in_number+1 num from Invoice) t WHERE t.num not in(select nvl(in_number,0) from Invoice)",
							Long.class);
			SqlRowList rs = queryForRowSet("select mn_leadcode, mn_maxreturn FROM maxnumbers where mn_tablename='" + caller + "'");
			if (rs.next()) {
				String leadcode = rs.getGeneralString("mn_leadcode");
				int length = rs.getGeneralInt("mn_maxreturn");
				if (StringUtil.hasText(leadcode)) {
					length -= leadcode.length();
					if (String.valueOf(number).length()>length) {
						BaseUtil.showError("编号长度不够！");
					}
					if (String.valueOf(number).length()>8) {
						code = leadcode  + String.format("%0"+length+"d", number);
					}else {
						length = length >8?8:length;
						code = leadcode  + String.format("%0"+length+"d", number);
					}
				}else{
					if (String.valueOf(number).length()>length) {
						BaseUtil.showError("编号长度不够！");
					}
					if (String.valueOf(number).length()>10) {
						code = String.format("%0"+length+"d", number);
					}else{
						length = length >10?10:length;
						code = String.format("%0"+length+"d", number);
					}
				}
			}
			if (checkIf("Invoice", "in_code = '"+code+"'")) {
				updateByCondition("Invoice", "in_number = "+number, "in_code = '"+code+"'");
				obj = getCode(caller);
			}
			ret = ret > number? ret:number;
			if (ret!=null) {
				updateByCondition("maxnumbers", "mn_number=" + ret, "mn_tablename='" + caller + "'");
			}
		} else {
			code = sGetMaxNumber(caller, 2);
		}
		if (obj==null) {
			obj = new JSONObject();
			obj.put("number", number);
			obj.put("code", code);
		}
		return obj;
	}

	public JSONObject newPaIn(Object pi_id, String inoutno, String caller) {
		int piid = getSeqId("PACKING_SEQ");
		int inid = getSeqId("INVOICE_SEQ");
		Employee employee = SystemSession.getUser();
		String code = null;
		String number = null;
		if("ProdInOut!AppropriationOut".equals(caller)){
			code = inoutno;
		}else {
			JSONObject obj = getCode("Invoice");
			code = obj.getString("code");
			if (obj.has("number")) {
				number = obj.getString("number");
			}
		}
		
		execute("INSERT INTO PACKING(pi_id, pi_code, pi_date, pi_currency, pi_rate, pi_custcode, pi_custname,"
				+ "pi_sellercode, pi_address, pi_receivecode, pi_receivename, pi_needhkdtotal, pi_remark2, pi_cop,"
				+ "pi_printrate, pi_recordman, pi_indate, pi_payment, pi_paymentcode, pi_source, pi_status, pi_statuscode, "
				+ "pi_printstatus, pi_printstatuscode, pi_custcode2, pi_custname2, pi_invoiceremark, pi_packingremark,"
				+ "pi_departmentcode,pi_departmentname,pi_relativecode)" + " select " + piid + ",'" + code
				+ "',pi_date,pi_currency,pi_rate,pi_cardcode,pi_title,pi_sellercode,pi_address,"
				+ "pi_receivecode,pi_receivename,pi_needhkdtotal,pi_remark2,pi_cop,pi_printrate,'" + employee.getEm_name() + "', sysdate,"
				+ "pi_payment,pi_paymentcode,pi_class,'" + BaseUtil.getLocalMessage("ENTERING") + "','ENTERING','"
				+ BaseUtil.getLocalMessage("UNPRINT") + "','UNPRINT',pi_custcode2,pi_custname2,pi_invoiceremark,pi_packingremark,"
				+ "pi_departmentcode,pi_departmentname,'" + inoutno + "' from prodinout where pi_id=" + pi_id);
		execute("INSERT INTO invoice(in_id, in_code, in_date, in_currency, in_rate, in_custcode, in_custname,"
				+ "in_sellercode, in_address, in_receivecode, in_receivename, in_needhkdtotal, in_remark2, in_cop,"
				+ "in_printrate, in_recordman, in_indate, in_payment, in_paymentcode, in_source, in_status, in_statuscode, "
				+ "in_printstatus, in_printstatuscode, in_custcode2, in_custname2, in_invoiceremark, in_packingremark, "
				+ "in_relativecode, in_whcode,in_departmentcode,in_departmentname,in_number)" + " select " + inid + ",'" + code
				+ "',pi_date,pi_currency,pi_rate,pi_cardcode,pi_title,pi_sellercode,pi_address,"
				+ "pi_receivecode,pi_receivename,pi_needhkdtotal,pi_remark2,pi_cop,pi_printrate,'" + employee.getEm_name() + "', sysdate,"
				+ "pi_payment,pi_paymentcode,pi_class,'" + BaseUtil.getLocalMessage("ENTERING") + "','ENTERING','"
				+ BaseUtil.getLocalMessage("UNPRINT") + "','UNPRINT',pi_custcode2,pi_custname2,pi_invoiceremark,pi_packingremark,'"
				+ inoutno + "',pi_whcode,pi_departmentcode,pi_departmentname,"+number+" from prodinout where pi_id=" + pi_id);
		execute("update packing set pi_emergency=(select pi_emergency from prodinout where prodinout.pi_id=" + pi_id + ") where pi_id="
				+ piid);
		execute("update invoice set in_emergency=(select pi_emergency from prodinout where pi_id=" + pi_id + ") where in_id=" + inid);
		JSONObject j = new JSONObject();
		j.put("pi_id", piid);
		j.put("in_id", inid);
		j.put("code", code);
		return j;
	}

	public JSONObject newPaInwithno(Object pi_id, String inoutno, String no, String caller) {
		int piid = getSeqId("PACKING_SEQ");
		int inid = getSeqId("INVOICE_SEQ");
		Employee employee = SystemSession.getUser();
		execute("INSERT INTO PACKING(pi_id, pi_code, pi_date, pi_currency, pi_rate, pi_custcode, pi_custname,"
				+ "pi_sellercode, pi_address, pi_receivecode, pi_receivename, pi_needhkdtotal, pi_remark2, pi_cop,"
				+ "pi_printrate, pi_recordman, pi_indate, pi_payment, pi_paymentcode, pi_source, pi_status, pi_statuscode, "
				+ "pi_printstatus, pi_printstatuscode, pi_custcode2, pi_custname2, pi_invoiceremark, pi_packingremark,pi_departmentcode,pi_departmentname,pi_relativecode)"
				+ " select " + piid + ",'" + no + "',pi_date,pi_currency,pi_rate,pi_cardcode,pi_title,pi_sellercode,pi_address,"
				+ "pi_receivecode,pi_receivename,pi_needhkdtotal,pi_remark2,pi_cop,pi_printrate,'" + employee.getEm_name() + "', sysdate,"
				+ "pi_payment,pi_paymentcode,pi_class,'" + BaseUtil.getLocalMessage("ENTERING") + "','ENTERING','"
				+ BaseUtil.getLocalMessage("UNPRINT")
				+ "','UNPRINT',pi_custcode2,pi_custname2,pi_invoiceremark,pi_packingremark,pi_departmentcode,pi_departmentname,'" + inoutno
				+ "' from prodinout where pi_id=" + pi_id);
		execute("INSERT INTO invoice(in_id, in_code, in_date, in_currency, in_rate, in_custcode, in_custname,"
				+ "in_sellercode, in_address, in_receivecode, in_receivename, in_needhkdtotal, in_remark2, in_cop,"
				+ "in_printrate, in_recordman, in_indate, in_payment, in_paymentcode, in_source, in_status, in_statuscode, "
				+ "in_printstatus, in_printstatuscode, in_custcode2, in_custname2, in_invoiceremark, in_packingremark, in_relativecode, in_whcode,in_departmentcode,in_departmentname)"
				+ " select " + inid + ",'" + no + "',pi_date,pi_currency,pi_rate,pi_cardcode,pi_title,pi_sellercode,pi_address,"
				+ "pi_receivecode,pi_receivename,pi_needhkdtotal,pi_remark2,pi_cop,pi_printrate,'" + employee.getEm_name() + "', sysdate,"
				+ "pi_payment,pi_paymentcode,pi_class,'" + BaseUtil.getLocalMessage("ENTERING") + "','ENTERING','"
				+ BaseUtil.getLocalMessage("UNPRINT") + "','UNPRINT',pi_custcode2,pi_custname2,pi_invoiceremark,pi_packingremark,'"
				+ inoutno + "',pi_whcode,pi_departmentcode,pi_departmentname from prodinout where pi_id=" + pi_id);
		execute("update packing set pi_emergency=(select pi_emergency from prodinout where prodinout.pi_id=" + pi_id + ") where pi_id="
				+ piid);
		execute("update invoice set in_emergency=(select pi_emergency from prodinout where pi_id=" + pi_id + ") where in_id=" + inid);
		execute("update packing set pi_rate=(select cr_rate from currencys where pi_currency=cr_name) where nvl(pi_rate,0)=0 and pi_id="
				+ piid);
		execute("update invoice set in_rate=(select cr_rate from currencys where in_currency=cr_name) where nvl(in_rate,0)=0 and in_id="
				+ inid);
		JSONObject j = new JSONObject();
		j.put("pi_id", piid);
		j.put("in_id", inid);
		return j;
	}
}
