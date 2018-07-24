package com.uas.b2b.model;

/**
 * ERP系统的模具询价单明细
 * 
 * @author hejq
 * @time 创建时间：2016年12月7日
 */
public class InquiryMouldDetail {

	/**
	 * id
	 */
	private Long ind_id;

	/**
	 * 模具询价主表id
	 */
	private Long ind_inid;

	/**
	 * 单据编号
	 */
	private String ind_code;

	/**
	 * 序号
	 */
	private Long ind_detno;

	/**
	 * 物料编号
	 */
	private String ind_prodcode;

	/**
	 * 产品价格
	 */
	private Double ind_price;

	/**
	 * 备注
	 */
	private String ind_remark;

	/**
	 * 模具编号
	 */
	private String ind_pscode;

	/**
	 * 模具名称
	 */
	private String ind_psname;

	/**
	 * 询价模具明细id
	 */
	private Long ind_iddid;

	/**
	 * 报价物料明细id
	 */
	private Long ind_pmdid;

	public Long getInd_id() {
		return ind_id;
	}

	public void setInd_id(Long ind_id) {
		this.ind_id = ind_id;
	}

	public Long getInd_inid() {
		return ind_inid;
	}

	public void setInd_inid(Long ind_imid) {
		this.ind_inid = ind_imid;
	}

	public String getInd_code() {
		return ind_code;
	}

	public void setInd_code(String ind_code) {
		this.ind_code = ind_code;
	}

	public Long getInd_detno() {
		return ind_detno;
	}

	public void setInd_detno(Long ind_detno) {
		this.ind_detno = ind_detno;
	}

	public String getInd_prodcode() {
		return ind_prodcode;
	}

	public void setInd_prodcode(String ind_procode) {
		this.ind_prodcode = ind_procode;
	}

	public Double getInd_price() {
		return ind_price;
	}

	public void setInd_price(Double ind_price) {
		this.ind_price = ind_price;
	}

	public String getInd_remark() {
		return ind_remark;
	}

	public void setInd_remark(String ind_remark) {
		this.ind_remark = ind_remark;
	}

	public String getInd_pscode() {
		return ind_pscode;
	}

	public void setInd_pscode(String ind_pscode) {
		this.ind_pscode = ind_pscode;
	}

	public String getInd_psname() {
		return ind_psname;
	}

	public void setInd_psname(String ind_psname) {
		this.ind_psname = ind_psname;
	}

	public Long getInd_iddid() {
		return ind_iddid;
	}

	public void setInd_iddid(Long ind_iddid) {
		this.ind_iddid = ind_iddid;
	}

	public Long getInd_pmdid() {
		return ind_pmdid;
	}

	public void setInd_pmdid(Long ind_pmdid) {
		this.ind_pmdid = ind_pmdid;
	}

}
