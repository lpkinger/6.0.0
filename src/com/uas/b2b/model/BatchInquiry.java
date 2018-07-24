package com.uas.b2b.model;

import java.util.Date;
import java.util.List;

import com.uas.erp.core.support.KeyEntity;

/**
 * 公共询价单
 * 
 * @author hejq
 * @time 创建时间：2017年9月15日
 */
public class BatchInquiry extends KeyEntity {

	/**
	 * erp id
	 */
	private Long bi_id;
	
	/**
	 * 询价单号
	 */
	private String bi_code;
	
	/**
	 * 时间
	 */
	private Date bi_date;
	
	/**
	 * 询价类型
	 */
	private String bi_kind;
	
	/**
	 * 环保需求
	 */
	private String bi_environment;
	
	/**
	 * 使用范围
	 */
	private String bi_purpose;
	
	/**
	 * 价格类型
	 */
	private String bi_pricekind;
	
	/**
	 * 录入人
	 */
	private String bi_recorder;
	
	/**
	 * 录入人uu
	 */
	private Long bi_recorduu;
	
	/**
	 * 录入时间
	 */
	private String bi_recorddate;
	
	/**
	 * 报价截止日期
	 */
	private Date bi_enddate;
	
	/**
	 * 备注
	 */
	private String bi_remark;
	
	/**
	 * 价格种类
	 */
	private String bi_pricetype;

	/**
	 * 询价物料
	 */
	private List<BatchInProduct> inProducts;
	
	/**
	 * 附件
	 */
	private String in_attach;
	
	/**
	 *  所有的附件信息
	 */
	private List<Attach> attaches;
	
	public Long getBi_id() {
		return bi_id;
	}

	public void setBi_id(Long bi_id) {
		this.bi_id = bi_id;
	}

	public String getBi_code() {
		return bi_code;
	}

	public void setBi_code(String bi_code) {
		this.bi_code = bi_code;
	}

	public Date getBi_date() {
		return bi_date;
	}

	public void setBi_date(Date bi_date) {
		this.bi_date = bi_date;
	}

	public String getBi_kind() {
		return bi_kind;
	}

	public void setBi_kind(String bi_kind) {
		this.bi_kind = bi_kind;
	}

	public String getBi_environment() {
		return bi_environment;
	}

	public void setBi_environment(String bi_environment) {
		this.bi_environment = bi_environment;
	}

	public String getBi_purpose() {
		return bi_purpose;
	}

	public void setBi_purpose(String bi_purpose) {
		this.bi_purpose = bi_purpose;
	}

	public String getBi_pricekind() {
		return bi_pricekind;
	}

	public void setBi_pricekind(String bi_pricekind) {
		this.bi_pricekind = bi_pricekind;
	}

	public String getBi_recorder() {
		return bi_recorder;
	}

	public void setBi_recorder(String bi_recorder) {
		this.bi_recorder = bi_recorder;
	}

	public Long getBi_recorduu() {
		return bi_recorduu;
	}

	public void setBi_recorduu(Long bi_recorduu) {
		this.bi_recorduu = bi_recorduu;
	}

	public String getBi_recorddate() {
		return bi_recorddate;
	}

	public void setBi_recorddate(String bi_recorddate) {
		this.bi_recorddate = bi_recorddate;
	}

	public Date getBi_enddate() {
		return bi_enddate;
	}

	public void setBi_enddate(Date bi_enddate) {
		this.bi_enddate = bi_enddate;
	}

	public String getBi_remark() {
		return bi_remark;
	}

	public void setBi_remark(String bi_remark) {
		this.bi_remark = bi_remark;
	}

	public String getBi_pricetype() {
		return bi_pricetype;
	}

	public void setBi_pricetype(String bi_pricetype) {
		this.bi_pricetype = bi_pricetype;
	}

	public List<BatchInProduct> getInProducts() {
		return inProducts;
	}

	public void setInProducts(List<BatchInProduct> inProducts) {
		this.inProducts = inProducts;
	}

	public String getIn_attach() {
		return in_attach;
	}

	public void setIn_attach(String in_attach) {
		this.in_attach = in_attach;
	}

	public List<Attach> getAttaches() {
		return attaches;
	}

	public void setAttaches(List<Attach> attaches) {
		this.attaches = attaches;
	}

	@Override
	public Object getKey() {
		return this.bi_id;
	}
	
}
