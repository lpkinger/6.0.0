package com.uas.b2b.model;

import java.util.Date;
import java.util.List;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.service.common.AttachUploadedAble;

/**
 * 卖家ERP系统的客户送货提醒单
 * 
 * @author yingp
 * 
 */
public class SaleSampleSend implements AttachUploadedAble{

	private Long ss_id;//(卖方ERP)单据ID
	private Long b2b_ss_id;//送样单b2b Id
	private Long b2b_ps_id;//客户送样申请b2b Id
	private String ss_code;
	private Double ss_sendnum;
	private Double ss_ratio;
	private Double ss_height;
	private String ss_material;
	private String ss_materialquality;
	private String ss_address;
	private String ss_addressmark;
	private Date ss_indate;
	private Double ss_puprice;
	private Double ss_spare;
	private String ss_brand;
	private Double ss_minqty;
	private Double ss_minbuyqty;
	private Double ss_delivery;
	private String ss_attach;
	private String ss_recorder;
	private String ss_pscode;
	private Short ss_pddetno;
	private Long ss_useruu;//买方联系人
	private String ss_vendspec;//生产厂型号（供应商的物料规格）
	private List<RemoteFile> files;
	public List<RemoteFile> getFiles() {
		return files;
	}
	public void setFiles(List<RemoteFile> files) {
		this.files = files;
	}
	public Long getB2b_ss_id() {
		return b2b_ss_id;
	}
	public void setB2b_ss_id(Long b2b_ss_id) {
		this.b2b_ss_id = b2b_ss_id;
	}
	public Long getB2b_ps_id() {
		return b2b_ps_id;
	}
	public void setB2b_ps_id(Long b2b_ps_id) {
		this.b2b_ps_id = b2b_ps_id;
	}
	public String getSs_code() {
		return ss_code;
	}
	public void setSs_code(String ss_code) {
		this.ss_code = ss_code;
	}
	public Double getSs_sendnum() {
		return ss_sendnum;
	}
	public void setSs_sendnum(Double ss_sendnum) {
		this.ss_sendnum = ss_sendnum;
	}
	public Double getSs_ratio() {
		return ss_ratio;
	}
	public void setSs_ratio(Double ss_ratio) {
		this.ss_ratio = ss_ratio;
	}
	public Double getSs_height() {
		return ss_height;
	}
	public void setSs_height(Double ss_height) {
		this.ss_height = ss_height;
	}
	public String getSs_material() {
		return ss_material;
	}
	public void setSs_material(String ss_material) {
		this.ss_material = ss_material;
	}
	public String getSs_materialquality() {
		return ss_materialquality;
	}
	public void setSs_materialquality(String ss_materialquality) {
		this.ss_materialquality = ss_materialquality;
	}
	public String getSs_address() {
		return ss_address;
	}
	public void setSs_address(String ss_address) {
		this.ss_address = ss_address;
	}
	public String getSs_addressmark() {
		return ss_addressmark;
	}
	public void setSs_addressmark(String ss_addressmark) {
		this.ss_addressmark = ss_addressmark;
	}
	public Date getSs_indate() {
		return ss_indate;
	}
	public void setSs_indate(Date ss_indate) {
		this.ss_indate = ss_indate;
	}
	public Double getSs_puprice() {
		return ss_puprice;
	}
	public void setSs_puprice(Double ss_puprice) {
		this.ss_puprice = ss_puprice;
	}
	public Double getSs_spare() {
		return ss_spare;
	}
	public void setSs_spare(Double ss_spare) {
		this.ss_spare = ss_spare;
	}
	public String getSs_brand() {
		return ss_brand;
	}
	public void setSs_brand(String ss_brand) {
		this.ss_brand = ss_brand;
	}
	public Double getSs_minqty() {
		return ss_minqty;
	}
	public void setSs_minqty(Double ss_minqty) {
		this.ss_minqty = ss_minqty;
	}
	public Double getSs_minbuyqty() {
		return ss_minbuyqty;
	}
	public void setSs_minbuyqty(Double ss_minbuyqty) {
		this.ss_minbuyqty = ss_minbuyqty;
	}
	public Double getSs_delivery() {
		return ss_delivery;
	}
	public void setSs_delivery(Double ss_delivery) {
		this.ss_delivery = ss_delivery;
	}
	public String getSs_attach() {
		return ss_attach;
	}
	public void setSs_attach(String ss_attach) {
		this.ss_attach = ss_attach;
	}
	public String getSs_recorder() {
		return ss_recorder;
	}
	public void setSs_recorder(String ss_recorder) {
		this.ss_recorder = ss_recorder;
	}
	public String getSs_pscode() {
		return ss_pscode;
	}
	public void setSs_pscode(String ss_pscode) {
		this.ss_pscode = ss_pscode;
	}
	public Short getSs_pddetno() {
		return ss_pddetno;
	}
	public void setSs_pddetno(Short ss_pddetno) {
		this.ss_pddetno = ss_pddetno;
	}
	public Long getSs_id() {
		return ss_id;
	}
	public void setSs_id(Long ss_id) {
		this.ss_id = ss_id;
	}
	public Long getSs_useruu() {
		return ss_useruu;
	}
	public void setSs_useruu(Long ss_useruu) {
		this.ss_useruu = ss_useruu;
	}
	public String getSs_vendspec() {
		return ss_vendspec;
	}
	public void setSs_vendspec(String ss_vendspec) {
		this.ss_vendspec = ss_vendspec;
	}
	
	public String toSaleSqlString(int primaryKey) {
		return "insert into CustSendSample (ss_id, b2b_ss_id, ss_b2b_ps_id, ss_code, ss_sendnum, ss_ratio, ss_height, "
				+ "ss_material, ss_materialquality, ss_address, ss_addressmark, ss_indate, ss_puprice, "
				+ "ss_spare, ss_brand, ss_vendspec, ss_minqty, ss_minbuyqty, ss_delivery, ss_recorder, "
				+ "ss_statuscode, ss_status, ss_sendstatus) "
				+ "values ("
				+ primaryKey
				+ ", "
				+ b2b_ss_id
				+ ", "
				+ b2b_ps_id
				+ ", '"
				+ ss_code
				+ "', "
				+ ss_sendnum
				+ ", "
				+ ss_ratio
				+ ", "
				+ ss_height
				+ ", '"
				+ StringUtil.nvl(ss_material, "")
				+ "', '"
				+ StringUtil.nvl(ss_materialquality, "")
				+ "', '"
				+ StringUtil.nvl(ss_address, "")
				+ "', '"
				+ StringUtil.nvl(ss_addressmark, "")
				+ "', "
				+ DateUtil.parseDateToOracleString(null, ss_indate)
				+ ", "
				+ ss_puprice
				+ ", "
				+ ss_spare
				+ ", '"
				+ StringUtil.nvl(ss_brand, "")
				+ "', '"
				+ StringUtil.nvl(ss_vendspec, "")
				+ "', "
				+ ss_minqty
				+ ", "
				+ ss_minbuyqty
				+ ", "
				+ ss_delivery
				+ ", '"
				+ StringUtil.nvl(ss_recorder, "")
				+ "', 'AUDITED', '已审核', '已上传'"
				+ ")";
	}
	
	public String toPurcSqlString(int primaryKey, String code) {
		return "insert into SendSample (ss_id, ss_code, b2b_ss_id, ss_pscode, ss_pddetno, ss_sendcode, ss_sendnum, ss_ratio, ss_height, "
				+ "ss_material, ss_materialquality, ss_address, ss_addressmark, ss_indate, ss_puprice, "
				+ "ss_spare, ss_brand, ss_vendspec, ss_minqty, ss_minbuyqty, ss_delivery, ss_recorder, ss_statuscode, ss_status) "
				+ "values ("
				+ primaryKey
				+ ", '"
				+ code
				+ "', "
				+ b2b_ss_id
				+ ", '"
				+ ss_pscode
				+ "', "
				+ ss_pddetno
				+ ", '"
				+ ss_code
				+ "', "
				+ ss_sendnum
				+ ", "
				+ ss_ratio
				+ ", "
				+ ss_height
				+ ", '"
				+ StringUtil.nvl(ss_material, "")
				+ "', '"
				+ StringUtil.nvl(ss_materialquality, "")
				+ "', '"
				+ StringUtil.nvl(ss_address, "")
				+ "', '"
				+ StringUtil.nvl(ss_addressmark, "")
				+ "', "
				+ DateUtil.parseDateToOracleString(null, ss_indate)
				+ ", "
				+ ss_puprice
				+ ", "
				+ ss_spare
				+ ", '"
				+ StringUtil.nvl(ss_brand, "")
				+ "', '"
				+ StringUtil.nvl(ss_vendspec, "")
				+ "', "
				+ ss_minqty
				+ ", "
				+ ss_minbuyqty
				+ ", "
				+ ss_delivery
				+ ", '"
				+ StringUtil.nvl(ss_recorder, "")
				+ "', '"
				+ "AUDITED"
				+ "', '"
				+ "已审核"
				+ "'"
				+ ")";
	}
	@Override
	public String getAttachs() {
		return this.ss_attach;
	}
	@Override
	public Object getReffrencValue() {
		return this.ss_code;
	}
	
	
	



}
