package com.uas.b2b.model;

import com.uas.erp.core.support.KeyEntity;

/**
 * ERP系统的产品
 * 
 * @author yingp
 * 
 */
public class Prod extends KeyEntity{

	private Long pr_id;
	private String pr_code;
	private String pr_detail;
	private String pr_spec;
	private String pr_unit;
	private Float pr_zxbzs;
	private Float pr_zxdhl;
	private Float pr_leadtime;
	private Float pr_ltinstock;//备料提前期，天数
	private String pr_brand;
	private String pr_orispeccode;// 原厂型号
	private String pr_uuid; // 标准料号
	private String pr_status; // 状态： ‘已审核’->有效；‘已禁用’->失效
	private Short pr_issale; // 可销售
	private Short pr_ispurchase; // 可采购
	private Short pr_isshow; // 开放采购物料
	private Short pr_ispubsale; // 开发销售物料
	private Long b2b_id;//平台id

	public Long getPr_id() {
		return pr_id;
	}

	public void setPr_id(Long pr_id) {
		this.pr_id = pr_id;
	}

	public String getPr_code() {
		return pr_code;
	}

	public void setPr_code(String pr_code) {
		this.pr_code = pr_code;
	}

	public String getPr_detail() {
		return pr_detail;
	}

	public void setPr_detail(String pr_detail) {
		this.pr_detail = pr_detail;
	}

	public String getPr_spec() {
		return pr_spec;
	}

	public void setPr_spec(String pr_spec) {
		this.pr_spec = pr_spec;
	}

	public String getPr_unit() {
		return pr_unit;
	}

	public void setPr_unit(String pr_unit) {
		this.pr_unit = pr_unit;
	}

	public Float getPr_zxbzs() {
		return pr_zxbzs;
	}

	public void setPr_zxbzs(Float pr_zxbzs) {
		this.pr_zxbzs = pr_zxbzs;
	}

	public Float getPr_zxdhl() {
		return pr_zxdhl;
	}

	public void setPr_zxdhl(Float pr_zxdhl) {
		this.pr_zxdhl = pr_zxdhl;
	}

	public Float getPr_leadtime() {
		return pr_leadtime;
	}

	public void setPr_leadtime(Float pr_leadtime) {
		this.pr_leadtime = pr_leadtime;
	}

	public Float getPr_ltinstock() {
		return pr_ltinstock;
	}

	public void setPr_ltinstock(Float pr_ltinstock) {
		this.pr_ltinstock = pr_ltinstock;
	}

	@Override
	public Object getKey() {
		return this.pr_id;
	}

	public String getPr_brand() {
		return pr_brand;
	}

	public void setPr_brand(String pr_brand) {
		this.pr_brand = pr_brand;
	}

	public String getPr_orispeccode() {
		return pr_orispeccode;
	}

	public void setPr_orispeccode(String pr_orispeccode) {
		this.pr_orispeccode = pr_orispeccode;
	}

	public String getPr_uuid() {
		return pr_uuid;
	}

	public void setPr_uuid(String pr_uuid) {
		this.pr_uuid = pr_uuid;
	}

	public String getPr_status() {
		return pr_status;
	}

	public void setPr_status(String pr_status) {
		this.pr_status = pr_status;
	}

	public Short getPr_issale() {
		return pr_issale;
	}

	public void setPr_issale(Short pr_issale) {
		this.pr_issale = pr_issale;
	}

	public Short getPr_ispurchase() {
		return pr_ispurchase;
	}

	public void setPr_ispurchase(Short pr_ispurchase) {
		this.pr_ispurchase = pr_ispurchase;
	}

	public Short getPr_isshow() {
		return pr_isshow;
	}

	public void setPr_isshow(Short pr_isshow) {
		this.pr_isshow = pr_isshow;
	}

	public Short getPr_ispubsale() {
		return pr_ispubsale;
	}

	public void setPr_ispubsale(Short pr_ispubsale) {
		this.pr_ispubsale = pr_ispubsale;
	}

	public Long getB2b_id() {
		return b2b_id;
	}

	public void setB2b_id(Long b2b_id) {
		this.b2b_id = b2b_id;
	}

}
