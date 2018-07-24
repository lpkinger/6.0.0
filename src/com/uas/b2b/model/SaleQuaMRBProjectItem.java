package com.uas.b2b.model;

import com.uas.erp.core.StringUtil;

/**
 * 买方MRB项目明细
 * @author suntg
 *
 */
public class SaleQuaMRBProjectItem {
	
	private Short mrd_detno;//明细行号
	private String mrd_ciname;//项目名称
	private String mrd_content;//异常情况
	private String mrd_result;//处理结果
	private Double mrd_ngqty;//不合格数
	
	public Short getMrd_detno() {
		return mrd_detno;
	}
	public void setMrd_detno(Short mrd_detno) {
		this.mrd_detno = mrd_detno;
	}
	public String getMrd_ciname() {
		return mrd_ciname;
	}
	public void setMrd_ciname(String mrd_ciname) {
		this.mrd_ciname = mrd_ciname;
	}
	public String getMrd_content() {
		return mrd_content;
	}
	public void setMrd_content(String mrd_content) {
		this.mrd_content = mrd_content;
	}
	public String getMrd_result() {
		return mrd_result;
	}
	public void setMrd_result(String mrd_result) {
		this.mrd_result = mrd_result;
	}
	public Double getMrd_ngqty() {
		return mrd_ngqty;
	}
	public void setMrd_ngqty(Double mrd_ngqty) {
		this.mrd_ngqty = mrd_ngqty;
	}
	
	public String toSqlString(int primaryKey){
		return "insert into qua_mrbdowndetail (mrd_id, mrd_mrid, mrd_detno, mrd_ciname, mrd_content, mrd_result, mrd_ngqty) "
				+ " values (qua_mrbdowndetail_seq.nextval, "
				+ primaryKey
				+ ", "
				+ mrd_detno
				+ ", '"
				+ StringUtil.nvl(this.mrd_ciname, "")
				+ "', '"
				+ StringUtil.nvl(this.mrd_content, "")
				+ "', '"
				+ StringUtil.nvl(this.mrd_result, "")
				+ "', "
				+ mrd_ngqty
				+ ") ";
	}
}
