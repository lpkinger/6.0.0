package com.uas.b2b.model;

import java.util.Date;
import java.util.List;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.StringUtil;

/**
 * 卖家ERP系统的客户样品认定单
 * 
 * @author suntg
 * 
 */
public class SaleSampleApproval{

	private Long pa_b2bid;
	private String pa_code;
	private String pa_pscode;
	private String pa_sscode;
	private Long pa_custuu;
	private String pa_custprodcode;
	private Double pa_sampleqty;
	private Double pa_height;
	private String pa_material;
	private String pa_materialquality;
	private String pa_address;
	private String pa_addressmark;
	private String pa_recordor;
	private Date pa_inDate;
	private String pa_remark;
	private String pa_attach;
	private Date pa_prdtime;
	private Double pa_prdypsl;
	private String pa_prdresult;
	private String pa_prdadvice;
	private String pa_prdremark;
	private String pa_prdattach;
	private Date pa_padtime;
	private Double pa_padypsl;
	private String pa_padresult;
	private String pa_padadvice;
	private String pa_padremark;
	private String pa_padattach;
	private Date pa_ppdtime;
	private Double pa_ppdypsl;
	private String pa_ppdresult;
	private String pa_ppdadvice;
	private String pa_ppdremark;
	private String pa_ppdattach;
	private String pa_finalresult;
	private String pa_finalresultremark;
	private String pa_custproddetail;
	private String pa_custprodspec;
	private String pa_custprodunit;
	private String pa_indate;
	private String pa_yxdj;//优选等级
	private List<RemoteFile> files;
	private List<RemoteFile> prdfiles;
	private List<RemoteFile> padfiles;
	private List<RemoteFile> ppdfiles;
	
	public String getPa_custproddetail() {
		return pa_custproddetail;
	}
	public void setPa_custproddetail(String pa_custproddetail) {
		this.pa_custproddetail = pa_custproddetail;
	}
	public String getPa_custprodspec() {
		return pa_custprodspec;
	}
	public void setPa_custprodspec(String pa_custprodspec) {
		this.pa_custprodspec = pa_custprodspec;
	}
	public String getPa_indate() {
		return pa_indate;
	}
	public void setPa_indate(String pa_indate) {
		this.pa_indate = pa_indate;
	}
	public List<RemoteFile> getFiles() {
		return files;
	}
	public void setFiles(List<RemoteFile> files) {
		this.files = files;
	}
	public List<RemoteFile> getPrdfiles() {
		return prdfiles;
	}
	public void setPrdfiles(List<RemoteFile> prdfiles) {
		this.prdfiles = prdfiles;
	}
	public List<RemoteFile> getPadfiles() {
		return padfiles;
	}
	public void setPadfiles(List<RemoteFile> padfiles) {
		this.padfiles = padfiles;
	}
	public List<RemoteFile> getPpdfiles() {
		return ppdfiles;
	}
	public void setPpdfiles(List<RemoteFile> ppdfiles) {
		this.ppdfiles = ppdfiles;
	}
	public String getPa_code() {
		return pa_code;
	}
	public void setPa_code(String pa_code) {
		this.pa_code = pa_code;
	}
	public String getPa_pscode() {
		return pa_pscode;
	}
	public void setPa_pscode(String pa_pscode) {
		this.pa_pscode = pa_pscode;
	}
	public String getPa_sscode() {
		return pa_sscode;
	}
	public void setPa_sscode(String pa_sscode) {
		this.pa_sscode = pa_sscode;
	}
	public Double getPa_sampleqty() {
		return pa_sampleqty;
	}
	public void setPa_sampleqty(Double pa_sampleqty) {
		this.pa_sampleqty = pa_sampleqty;
	}
	public Double getPa_height() {
		return pa_height;
	}
	public void setPa_height(Double pa_height) {
		this.pa_height = pa_height;
	}
	public String getPa_material() {
		return pa_material;
	}
	public void setPa_material(String pa_material) {
		this.pa_material = pa_material;
	}
	public String getPa_materialquality() {
		return pa_materialquality;
	}
	public void setPa_materialquality(String pa_materialquality) {
		this.pa_materialquality = pa_materialquality;
	}
	public String getPa_address() {
		return pa_address;
	}
	public void setPa_address(String pa_address) {
		this.pa_address = pa_address;
	}
	public String getPa_addressmark() {
		return pa_addressmark;
	}
	public void setPa_addressmark(String pa_addressmark) {
		this.pa_addressmark = pa_addressmark;
	}
	public String getPa_recordor() {
		return pa_recordor;
	}
	public void setPa_recordor(String pa_recordor) {
		this.pa_recordor = pa_recordor;
	}
	public Date getPa_inDate() {
		return pa_inDate;
	}
	public void setPa_inDate(Date pa_inDate) {
		this.pa_inDate = pa_inDate;
	}
	public String getPa_remark() {
		return pa_remark;
	}
	public void setPa_remark(String pa_remark) {
		this.pa_remark = pa_remark;
	}
	public String getPa_attach() {
		return pa_attach;
	}
	public void setPa_attach(String pa_attach) {
		this.pa_attach = pa_attach;
	}
	public Date getPa_prdtime() {
		return pa_prdtime;
	}
	public void setPa_prdtime(Date pa_prdtime) {
		this.pa_prdtime = pa_prdtime;
	}
	public Double getPa_prdypsl() {
		return pa_prdypsl;
	}
	public void setPa_prdypsl(Double pa_prdypsl) {
		this.pa_prdypsl = pa_prdypsl;
	}
	public String getPa_prdresult() {
		return pa_prdresult;
	}
	public void setPa_prdresult(String pa_prdresult) {
		this.pa_prdresult = pa_prdresult;
	}
	public String getPa_prdadvice() {
		return pa_prdadvice;
	}
	public void setPa_prdadvice(String pa_prdadvice) {
		this.pa_prdadvice = pa_prdadvice;
	}
	public String getPa_prdremark() {
		return pa_prdremark;
	}
	public void setPa_prdremark(String pa_prdremark) {
		this.pa_prdremark = pa_prdremark;
	}
	public String getPa_prdattach() {
		return pa_prdattach;
	}
	public void setPa_prdattach(String pa_prdattach) {
		this.pa_prdattach = pa_prdattach;
	}
	public Date getPa_padtime() {
		return pa_padtime;
	}
	public void setPa_padtime(Date pa_padtime) {
		this.pa_padtime = pa_padtime;
	}
	public Double getPa_padypsl() {
		return pa_padypsl;
	}
	public void setPa_padypsl(Double pa_padypsl) {
		this.pa_padypsl = pa_padypsl;
	}
	public String getPa_padresult() {
		return pa_padresult;
	}
	public void setPa_padresult(String pa_padresult) {
		this.pa_padresult = pa_padresult;
	}
	public String getPa_padadvice() {
		return pa_padadvice;
	}
	public void setPa_padadvice(String pa_padadvice) {
		this.pa_padadvice = pa_padadvice;
	}
	public String getPa_padremark() {
		return pa_padremark;
	}
	public void setPa_padremark(String pa_padremark) {
		this.pa_padremark = pa_padremark;
	}
	public String getPa_padattach() {
		return pa_padattach;
	}
	public void setPa_padattach(String pa_padattach) {
		this.pa_padattach = pa_padattach;
	}
	public Date getPa_ppdtime() {
		return pa_ppdtime;
	}
	public void setPa_ppdtime(Date pa_ppdtime) {
		this.pa_ppdtime = pa_ppdtime;
	}
	public Double getPa_ppdypsl() {
		return pa_ppdypsl;
	}
	public void setPa_ppdypsl(Double pa_ppdypsl) {
		this.pa_ppdypsl = pa_ppdypsl;
	}
	public String getPa_ppdresult() {
		return pa_ppdresult;
	}
	public void setPa_ppdresult(String pa_ppdresult) {
		this.pa_ppdresult = pa_ppdresult;
	}
	public String getPa_ppdadvice() {
		return pa_ppdadvice;
	}
	public void setPa_ppdadvice(String pa_ppdadvice) {
		this.pa_ppdadvice = pa_ppdadvice;
	}
	public String getPa_ppdremark() {
		return pa_ppdremark;
	}
	public void setPa_ppdremark(String pa_ppdremark) {
		this.pa_ppdremark = pa_ppdremark;
	}
	public String getPa_ppdattach() {
		return pa_ppdattach;
	}
	public void setPa_ppdattach(String pa_ppdattach) {
		this.pa_ppdattach = pa_ppdattach;
	}
	public String getPa_finalresult() {
		return pa_finalresult;
	}
	public void setPa_finalresult(String pa_finalresult) {
		this.pa_finalresult = pa_finalresult;
	}
	public String getPa_finalresultremark() {
		return pa_finalresultremark;
	}
	public void setPa_finalresultremark(String pa_finalresultremark) {
		this.pa_finalresultremark = pa_finalresultremark;
	}
	public Long getPa_b2bid() {
		return pa_b2bid;
	}
	public void setPa_b2bid(Long pa_b2bid) {
		this.pa_b2bid = pa_b2bid;
	}
	public Long getPa_custuu() {
		return pa_custuu;
	}
	public void setPa_custuu(Long pa_custuu) {
		this.pa_custuu = pa_custuu;
	}
	public String getPa_custprodcode() {
		return pa_custprodcode;
	}
	public void setPa_custprodcode(String pa_custprodcode) {
		this.pa_custprodcode = pa_custprodcode;
	}
	public String getPa_custprodunit() {
		return pa_custprodunit;
	}
	public void setPa_custprodunit(String pa_custprodunit) {
		this.pa_custprodunit = pa_custprodunit;
	}
	
	public String getPa_yxdj() {
		return pa_yxdj;
	}
	public void setPa_yxdj(String pa_yxdj) {
		this.pa_yxdj = pa_yxdj;
	}
	public String toSqlString(int primaryKey) {
		return "insert into productapprovaldown (pa_id, pa_b2bid, pa_code, pa_pscode, pa_sscode, pa_custuu, pa_custprodcode, "
				+ "pa_custproddetail, pa_custprodspec, pa_custprodunit, pa_sampleqty, pa_height, pa_material, pa_materialquality, "
				+ "pa_address, pa_addressmark, pa_recordor, pa_indate, pa_remark, pa_prdtime, pa_prdypsl, pa_prdresult, "
				+ "pa_prdadvice, pa_prdremark, pa_padtime, pa_padypsl, pa_padresult, pa_padadvice, pa_padremark, "
				+ "pa_ppdtime, pa_ppdypsl, pa_ppdresult, pa_ppdadvice, pa_ppdremark, pa_finalresult, "
				+ "pa_finalresultremark,pa_yxdj) values ("
				+ primaryKey
				+ ", "
				+ pa_b2bid
				+ ", '"
				+ pa_code
				+ "', '"
				+ StringUtil.nvl(pa_pscode, "")
				+ "', '"
				+ StringUtil.nvl(pa_sscode, "")
				+ "', "
				+ pa_custuu
				+ ", '"
				+ StringUtil.nvl(pa_custprodcode, "")
				+ "', '"
				+ StringUtil.nvl(pa_custproddetail, "")
				+ "', '"
				+ StringUtil.nvl(pa_custprodspec, "")
				+ "', '"
				+ StringUtil.nvl(pa_custprodunit, "")
				+ "', "
				+ pa_sampleqty
				+ ", "
				+ pa_height
				+ ", '"
				+ StringUtil.nvl(pa_material, "")
				+ "', '"
				+ StringUtil.nvl(pa_materialquality, "")
				+ "', '"
				+ StringUtil.nvl(pa_address, "")
				+ "', '"
				+ StringUtil.nvl(pa_addressmark, "")
				+ "', '"
				+ StringUtil.nvl(pa_recordor, "")
				+ "', "
				+ DateUtil.parseDateToOracleString(null, pa_indate)
				+ ", '"
				+ StringUtil.nvl(pa_remark, "")
				+ "', "
				+ DateUtil.parseDateToOracleString(null, pa_prdtime)
				+ ", "
				+ pa_prdypsl
				+ ", '"
				+ StringUtil.nvl(pa_prdresult, "")
				+ "', '"
				+ StringUtil.nvl(pa_prdadvice, "")
				+ "', '"
				+ StringUtil.nvl(pa_prdremark, "")
				+ "', "
				+ DateUtil.parseDateToOracleString(null, pa_padtime)
				+ ", "
				+ pa_padypsl
				+ ", '"
				+ StringUtil.nvl(pa_padresult, "")
				+ "', '"
				+ StringUtil.nvl(pa_padadvice, "")
				+ "', '"
				+ StringUtil.nvl(pa_padremark, "")
				+ "', "
				+ DateUtil.parseDateToOracleString(null, pa_ppdtime)
				+ ", "
				+ pa_ppdypsl
				+ ", '"
				+ StringUtil.nvl(pa_ppdresult, "")
				+ "', '"
				+ StringUtil.nvl(pa_ppdadvice, "")
				+ "', '"
				+ StringUtil.nvl(pa_ppdremark, "")
				+ "', '"
				+ StringUtil.nvl(pa_finalresult, "")
				+ "', '"
				+ StringUtil.nvl(pa_finalresultremark, "")
				+ "', '"
				+ StringUtil.nvl(pa_yxdj, "")
				+ "'"
				+ ")";
	}



}
