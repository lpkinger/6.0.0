package com.uas.b2b.model;

import java.util.Date;
import java.util.List;

import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.KeyEntity;

/**
 * 买家ERP系统的采购询价单明细
 * 
 * @author yingp
 * 
 */
public class InquiryDetail extends KeyEntity  {

	private Long b2b_id_id;
	private short id_detno;
	private String id_prodcode;
	private String id_currency;
	private Float id_rate;
	private Long ve_uu;
	private String ve_contact;
	private Long ve_contactuu;
	private String id_remark;
	private Long id_id;
	private Date id_myfromdate;
	private Date id_mytodate;
	private Date id_fromdate;
	private Date id_todate;
	private Double id_minbuyqty;
	private Double id_minqty;
	private String id_brand;//供应商物料品牌
	private String id_vendorprodcode;//供应商物料编号
	private Long id_leadtime;//供应商交互周期
	private List<InquiryDetailDet> dets;
	private List<RemoteFile> files;
	private String id_attach;
	private Long ve_buyeruu;
	private String sourceApp;
	private String id_vendname;// 供应商名称
	private String id_vendyyzzh;// 供应商营业执照号
	private String in_code; // 存在公共询价但是询价单没有数据的问题，这时需要处理
	// 询价明细状态，用于确认消息是报价还是修改报价
	private String id_status;
	private String in_inquirytype;// 询价类型，公共询价进行特殊处理
	private Integer id_quto;//设置是否自动询价
	public Long getB2b_id_id() {
		return b2b_id_id;
	}

	public void setB2b_id_id(Long b2b_id_id) {
		this.b2b_id_id = b2b_id_id;
	}

	public short getId_detno() {
		return id_detno;
	}

	public void setId_detno(short id_detno) {
		this.id_detno = id_detno;
	}

	public String getId_prodcode() {
		return id_prodcode;
	}

	public void setId_prodcode(String id_prodcode) {
		this.id_prodcode = id_prodcode;
	}

	public String getId_currency() {
		return id_currency;
	}

	public void setId_currency(String id_currency) {
		this.id_currency = id_currency;
	}

	public Float getId_rate() {
		return id_rate;
	}

	public void setId_rate(Float id_rate) {
		this.id_rate = id_rate;
	}

	public Long getVe_uu() {
		return ve_uu;
	}

	public void setVe_uu(Long ve_uu) {
		this.ve_uu = ve_uu;
	}

	public String getId_remark() {
		return id_remark;
	}

	public void setId_remark(String id_remark) {
		this.id_remark = id_remark;
	}

	public Long getId_id() {
		return id_id;
	}

	public void setId_id(Long id_id) {
		this.id_id = id_id;
	}

	public List<InquiryDetailDet> getDets() {
		return dets;
	}

	public void setDets(List<InquiryDetailDet> dets) {
		this.dets = dets;
	}

	public Date getId_myfromdate() {
		return id_myfromdate;
	}

	public void setId_myfromdate(Date id_myfromdate) {
		this.id_myfromdate = id_myfromdate;
	}

	public String getId_attach() {
		return id_attach;
	}

	public void setId_attach(String id_attach) {
		this.id_attach = id_attach;
	}

	public Date getId_mytodate() {
		return id_mytodate;
	}

	public void setId_mytodate(Date id_mytodate) {
		this.id_mytodate = id_mytodate;
	}

	public Date getId_fromdate() {
		return id_fromdate;
	}

	public void setId_fromdate(Date id_fromdate) {
		this.id_fromdate = id_fromdate;
	}

	public Date getId_todate() {
		return id_todate;
	}

	public void setId_todate(Date id_todate) {
		this.id_todate = id_todate;
	}

	public Double getId_minbuyqty() {
		return id_minbuyqty;
	}

	public void setId_minbuyqty(Double id_minbuyqty) {
		this.id_minbuyqty = id_minbuyqty;
	}

	public Double getId_minqty() {
		return id_minqty;
	}

	public void setId_minqty(Double id_minqty) {
		this.id_minqty = id_minqty;
	}

	public String getVe_contact() {
		return ve_contact;
	}

	public void setVe_contact(String ve_contact) {
		this.ve_contact = ve_contact;
	}

	public Long getVe_contactuu() {
		return ve_contactuu;
	}

	public void setVe_contactuu(Long ve_contactuu) {
		this.ve_contactuu = ve_contactuu;
	}

	public Long getVe_buyeruu() {
		return ve_buyeruu;
	}

	public void setVe_buyeruu(Long ve_buyeruu) {
		this.ve_buyeruu = ve_buyeruu;
	}

	public String getId_brand() {
		return id_brand;
	}

	public void setId_brand(String id_brand) {
		this.id_brand = id_brand;
	}

	public String getId_vendorprodcode() {
		return id_vendorprodcode;
	}

	public void setId_vendorprodcode(String id_vendorprodcode) {
		this.id_vendorprodcode = id_vendorprodcode;
	}

	public Long getId_leadtime() {
		return id_leadtime;
	}

	public void setId_leadtime(Long id_leadtime) {
		this.id_leadtime = id_leadtime;
	}

	public String getSourceApp() {
		return sourceApp;
	}

	public void setSourceApp(String sourceApp) {
		this.sourceApp = sourceApp;
	}

	public List<RemoteFile> getFiles() {
		return files;
	}

	public void setFiles(List<RemoteFile> files) {
		this.files = files;
	}

	public String getId_status() {
		return id_status;
	}

	public void setId_status(String id_status) {
		this.id_status = id_status;
	}

	public String getId_vendname() {
		return id_vendname;
	}

	public void setId_vendname(String id_vendname) {
		this.id_vendname = id_vendname;
	}

	public String getId_vendyyzzh() {
		return id_vendyyzzh;
	}

	public void setId_vendyyzzh(String id_vendyyzzh) {
		this.id_vendyyzzh = id_vendyyzzh;
	}

	public String getIn_code() {
		return in_code;
	}

	public void setIn_code(String in_code) {
		this.in_code = in_code;
	}

	public String getIn_inquirytype() {
		return in_inquirytype;
	}

	public void setIn_inquirytype(String in_inquirytype) {
		this.in_inquirytype = in_inquirytype;
	}

	public Integer getId_quto() {
		return id_quto;
	}

	public void setId_quto(Integer id_quto) {
		this.id_quto = id_quto;
	}

	/**
	 * 主动报价SQL封装
	 * 
	 * @param primaryKey
	 * @param foreignKey
	 * @return
	 */
	public String toSqlString(int primaryKey, int foreignKey) {
		return "insert into InquiryDetail(id_id,id_inid,id_detno,id_prodcode,id_currency,id_rate,id_minbuyqty,id_minqty,id_remark,id_venduu, ID_VENDCONTACT, ID_VENDCONTACTUU, ID_BRAND, ID_LEADTIME, ID_VENDORPRODCODE) values ("
				+ primaryKey
				+ ","
				+ foreignKey
				+ ","
				+ this.id_detno
				+ ",'"
				+ StringUtil.nvl(this.id_prodcode, "")
				+ "','"
				+ this.id_currency
				+ "',"
				+ this.id_rate
				+ ","
				+ this.id_minbuyqty
				+ ","
				+ this.id_minqty
				+ ",'"
				+ StringUtil.nvl(this.id_remark, "")
				+ "',"
				+ this.ve_uu 
				+ ",'"
				+ StringUtil.nvl(this.ve_contact, "")
				+ "',"
				+ this.ve_contactuu
				+ ",'"
				+ StringUtil.nvl(this.id_brand, "")
				+ "', "
				+ this.id_leadtime
				+ ",'"
				+ StringUtil.nvl(this.id_vendorprodcode, "")
				+ "'"
				+ ")";
	}

	@Override
	public Object getKey() {
		return this.id_id;
	}

}
