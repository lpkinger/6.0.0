package com.uas.b2b.model;

import java.util.Date;
import java.util.List;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.StringUtil;

/**
 * 卖家ERP系统的客户客户打样申请单
 * 
 * @author suntg
 * 
 */
public class SaleSampleDown {

	private Long b2b_ps_id;
	private String ps_code;
	private Date ps_indate;
	private String ps_recordor;
	private Long ps_custuu;
	private String ps_contact;
	private Long ps_contactuu;
	private String ps_custprodcode;
	private String ps_custpesc;
	private String ps_custproddetail;
	private String ps_custunit;
	private Date ps_delivery;
	private String ps_envrequire;
	private String ps_scope;
	private String ps_attach;
	private String ps_isfree;//是否收费
	private Double ps_price;
	private String ps_currency;
	private Double ps_rate;
	private Double ps_qty;
	private Double ps_total;
	private String ps_remark;
	private List<RemoteFile> files;
	public Long getB2b_ps_id() {
		return b2b_ps_id;
	}
	public void setB2b_ps_id(Long b2b_ps_id) {
		this.b2b_ps_id = b2b_ps_id;
	}
	public String getPs_code() {
		return ps_code;
	}
	public void setPs_code(String ps_code) {
		this.ps_code = ps_code;
	}
	public Date getPs_indate() {
		return ps_indate;
	}
	public void setPs_indate(Date ps_indate) {
		this.ps_indate = ps_indate;
	}
	public String getPs_recordor() {
		return ps_recordor;
	}
	public void setPs_recordor(String ps_recordor) {
		this.ps_recordor = ps_recordor;
	}
	public Long getPs_custuu() {
		return ps_custuu;
	}
	public void setPs_custuu(Long ps_custuu) {
		this.ps_custuu = ps_custuu;
	}
	public String getPs_contact() {
		return ps_contact;
	}
	public void setPs_contact(String ps_contact) {
		this.ps_contact = ps_contact;
	}
	public Long getPs_contactuu() {
		return ps_contactuu;
	}
	public void setPs_contactuu(Long ps_contactuu) {
		this.ps_contactuu = ps_contactuu;
	}
	public String getPs_custprodcode() {
		return ps_custprodcode;
	}
	public void setPs_custprodcode(String ps_custprodcode) {
		this.ps_custprodcode = ps_custprodcode;
	}
	public String getPs_custpesc() {
		return ps_custpesc;
	}
	public void setPs_custpesc(String ps_custpesc) {
		this.ps_custpesc = ps_custpesc;
	}
	public String getPs_custproddetail() {
		return ps_custproddetail;
	}
	public void setPs_custproddetail(String ps_custproddetail) {
		this.ps_custproddetail = ps_custproddetail;
	}
	public String getPs_custunit() {
		return ps_custunit;
	}
	public void setPs_custunit(String ps_custunit) {
		this.ps_custunit = ps_custunit;
	}
	public Date getPs_delivery() {
		return ps_delivery;
	}
	public void setPs_delivery(Date ps_delivery) {
		this.ps_delivery = ps_delivery;
	}
	public String getPs_envrequire() {
		return ps_envrequire;
	}
	public void setPs_envrequire(String ps_envrequire) {
		this.ps_envrequire = ps_envrequire;
	}
	public String getPs_scope() {
		return ps_scope;
	}
	public void setPs_scope(String ps_scope) {
		this.ps_scope = ps_scope;
	}
	public String getPs_attach() {
		return ps_attach;
	}
	public void setPs_attach(String ps_attach) {
		this.ps_attach = ps_attach;
	}
	public String getPs_isfree() {
		return ps_isfree;
	}
	public void setPs_isfree(String ps_isfree) {
		this.ps_isfree = ps_isfree;
	}
	public Double getPs_price() {
		return ps_price;
	}
	public void setPs_price(Double ps_price) {
		this.ps_price = ps_price;
	}
	public String getPs_currency() {
		return ps_currency;
	}
	public void setPs_currency(String ps_currency) {
		this.ps_currency = ps_currency;
	}
	public Double getPs_rate() {
		return ps_rate;
	}
	public void setPs_rate(Double ps_rate) {
		this.ps_rate = ps_rate;
	}
	public Double getPs_qty() {
		return ps_qty;
	}
	public void setPs_qty(Double ps_qty) {
		this.ps_qty = ps_qty;
	}
	public Double getPs_total() {
		return ps_total;
	}
	public void setPs_total(Double ps_total) {
		this.ps_total = ps_total;
	}
	public String getPs_remark() {
		return ps_remark;
	}
	public void setPs_remark(String ps_remark) {
		this.ps_remark = ps_remark;
	}
	public List<RemoteFile> getFiles() {
		return files;
	}
	public void setFiles(List<RemoteFile> files) {
		this.files = files;
	}
	
	public String toSqlString(int primaryKey){
		return "insert into productsampledown (ps_id, b2b_ps_id, ps_code, ps_indate, ps_recordor, "
				+ "ps_custuu, ps_contact, ps_contactuu, ps_custprodcode, ps_custproddetail, ps_custspec, "
				+ "ps_custunit, ps_delivery, ps_envrequire, ps_scope, ps_isfree, ps_price, "
				+ "ps_currency, ps_rate, ps_qty, ps_total, ps_remark) "
				+ "values ("
				+ primaryKey 
				+ ", "
				+ b2b_ps_id 
				+ ", '"
				+ ps_code 
				+ "', "
				+ DateUtil.parseDateToOracleString(null, ps_indate)
				+ ", '"
				+ StringUtil.nvl(ps_recordor, "")
				+ "', "
				+ ps_custuu
				+ ", '"
				+ StringUtil.nvl(ps_contact, "")
				+ "', "
				+ ps_contactuu
				+ ", '"
				+ StringUtil.nvl(ps_custprodcode, "")
				+ "', '"
				+ StringUtil.nvl(ps_custproddetail, "")
				+ "', '"
				+ StringUtil.nvl(ps_custpesc, "")
				+ "', '"
				+ StringUtil.nvl(ps_custunit, "")
				+ "', "
				+ DateUtil.parseDateToOracleString(null, ps_delivery)
				+ ", '"
				+ StringUtil.nvl(ps_envrequire, "")
				+ "', '"
				+ StringUtil.nvl(ps_scope, "")
				+ "', '"
				+ StringUtil.nvl(ps_isfree, "")
				+ "', "
				+ ps_price
				+ ", '"
				+ StringUtil.nvl(ps_currency, "")
				+ "', "
				+ ps_rate
				+ ", "
				+ NumberUtil.nvl(ps_qty, 0)
				+ ", "
				+ ps_total
				+ ", '"
				+ StringUtil.nvl(ps_remark, "")
				+ "'"
				+ ")";
	}
	
	
}
