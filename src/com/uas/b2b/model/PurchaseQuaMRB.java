package com.uas.b2b.model;

import java.util.Date;
import java.util.List;

/**
 * 买方MRB
 * @author suntg
 *
 */
public class PurchaseQuaMRB {
	
	private Long mr_id;//id
	private String mr_code;//编号
	private Date mr_date;//日期
	private Long mr_venduu;//供应商uu 
	private String mr_pucode;//采购单编号
	private Short mr_pudetno;//采购单明细行号
	private String mr_vecode;//来源不良品入库单号
	private Date mr_datein;//送检日期
	private Double mr_inqty;//来料数量
	private Double mr_checkqty;//检验数量
	private Double mr_ngqty;//不合格数
	private Double mr_okqty;//合格数
	private String mr_result;//检验结果
	private String mr_remark;//备注
	private String mr_shcode;//收货单号
	private String mr_attach;// 附件
	private List<PurchaseQuaMRBCheckItem> checkItems;//检验明细行
	private List<PurchaseQuaMRBProjectItem> projectItems;//项目明细行
	private List<Attach> attaches;// 附件信息列表
	
	public String getMr_code() {
		return mr_code;
	}
	public void setMr_code(String mr_code) {
		this.mr_code = mr_code;
	}
	public Date getMr_date() {
		return mr_date;
	}
	public void setMr_date(Date mr_date) {
		this.mr_date = mr_date;
	}
	public Long getMr_venduu() {
		return mr_venduu;
	}
	public void setMr_venduu(Long mr_venduu) {
		this.mr_venduu = mr_venduu;
	}
	public String getMr_pucode() {
		return mr_pucode;
	}
	public void setMr_pucode(String mr_pucode) {
		this.mr_pucode = mr_pucode;
	}
	public Short getMr_pudetno() {
		return mr_pudetno;
	}
	public void setMr_pudetno(Short mr_pudetno) {
		this.mr_pudetno = mr_pudetno;
	}
	public Date getMr_datein() {
		return mr_datein;
	}
	public void setMr_datein(Date mr_datein) {
		this.mr_datein = mr_datein;
	}
	public Double getMr_inqty() {
		return mr_inqty;
	}
	public void setMr_inqty(Double mr_inqty) {
		this.mr_inqty = mr_inqty;
	}
	public Double getMr_checkqty() {
		return mr_checkqty;
	}
	public void setMr_checkqty(Double mr_checkqty) {
		this.mr_checkqty = mr_checkqty;
	}
	public Double getMr_ngqty() {
		return mr_ngqty;
	}
	public void setMr_ngqty(Double mr_ngqty) {
		this.mr_ngqty = mr_ngqty;
	}
	public Double getMr_okqty() {
		return mr_okqty;
	}
	public void setMr_okqty(Double mr_okqty) {
		this.mr_okqty = mr_okqty;
	}
	public String getMr_result() {
		return mr_result;
	}
	public void setMr_result(String mr_result) {
		this.mr_result = mr_result;
	}
	public String getMr_remark() {
		return mr_remark;
	}
	public void setMr_remark(String mr_remark) {
		this.mr_remark = mr_remark;
	}
	public String getMr_shcode() {
		return mr_shcode;
	}
	public void setMr_shcode(String mr_shcode) {
		this.mr_shcode = mr_shcode;
	}
	public List<PurchaseQuaMRBCheckItem> getCheckItems() {
		return checkItems;
	}
	public void setCheckItems(List<PurchaseQuaMRBCheckItem> checkItems) {
		this.checkItems = checkItems;
	}
	public List<PurchaseQuaMRBProjectItem> getProjectItems() {
		return projectItems;
	}
	public void setProjectItems(List<PurchaseQuaMRBProjectItem> projectItems) {
		this.projectItems = projectItems;
	}
	public Long getMr_id() {
		return mr_id;
	}
	public void setMr_id(Long mr_id) {
		this.mr_id = mr_id;
	}
	public String getMr_vecode() {
		return mr_vecode;
	}
	public void setMr_vecode(String mr_vecode) {
		this.mr_vecode = mr_vecode;
	}

	public String getMr_attach() {
		return mr_attach;
	}

	public void setMr_attach(String mr_attach) {
		this.mr_attach = mr_attach;
	}
	public List<Attach> getAttaches() {
		return attaches;
	}

	public void setAttaches(List<Attach> attaches) {
		this.attaches = attaches;
	}
	
}
