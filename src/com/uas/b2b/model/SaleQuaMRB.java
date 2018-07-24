package com.uas.b2b.model;

import java.util.Date;
import java.util.List;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.StringUtil;

/**
 * 买方MRB
 * @author suntg
 *
 */
public class SaleQuaMRB {
	
	private Long mr_b2bid;//b2b id
	private String mr_code;//编号
	private Date mr_date;//日期
	private Long mr_coustomeruu;//客户uu 
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
	private List<SaleQuaMRBCheckItem> checkItems;//检验明细行
	private List<SaleQuaMRBProjectItem> projectItems;//项目明细行
	
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
	public Long getMr_coustomeruu() {
		return mr_coustomeruu;
	}
	public void setMr_coustomeruu(Long mr_coustomeruu) {
		this.mr_coustomeruu = mr_coustomeruu;
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
	public List<SaleQuaMRBCheckItem> getCheckItems() {
		return checkItems;
	}
	public void setCheckItems(List<SaleQuaMRBCheckItem> checkItems) {
		this.checkItems = checkItems;
	}
	public List<SaleQuaMRBProjectItem> getProjectItems() {
		return projectItems;
	}
	public void setProjectItems(List<SaleQuaMRBProjectItem> projectItems) {
		this.projectItems = projectItems;
	}
	public String getMr_vecode() {
		return mr_vecode;
	}
	public void setMr_vecode(String mr_vecode) {
		this.mr_vecode = mr_vecode;
	}
	public Long getMr_b2bid() {
		return mr_b2bid;
	}
	public void setMr_b2bid(Long mr_b2bid) {
		this.mr_b2bid = mr_b2bid;
	}
	
	public String toSqlString(int primaryKey){
		return "insert into qua_mrbdown (mr_id, mr_b2bid, mr_code, mr_date, mr_pucode, mr_pudetno, mr_vecode"
				+ ", mr_datein, mr_inqty, mr_checkqty, mr_ngqty, mr_okqty, mr_result, mr_remark, mr_shcode)"
				+ " values ("
				+ primaryKey
				+ ", "
				+ this.mr_b2bid
				+ ", '"
				+ mr_code
				+ "', "
				+ DateUtil.parseDateToOracleString(null, this.mr_date)
				+ ", '"
				+ mr_pucode
				+ "', "
				+ mr_pudetno
				+ ", '"
				+ StringUtil.nvl(this.mr_vecode, "")
				+ "', "
				+ DateUtil.parseDateToOracleString(null, this.mr_datein)
				+ ", "
				+ mr_inqty
				+ ", "
				+ mr_checkqty
				+ ", "
				+ mr_ngqty
				+ ", "
				+ mr_okqty
				+ ", '"
				+ StringUtil.nvl(this.mr_result, "")
				+ "', '"
				+ StringUtil.nvl(this.mr_remark, "")
				+ "', '"
				+ StringUtil.nvl(this.mr_shcode, "")
				+ "'"
				+ ")";
	}
	
	

}
