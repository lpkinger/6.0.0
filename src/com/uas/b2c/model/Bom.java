package com.uas.b2c.model;

import java.util.Date;
import java.util.Set;

public class Bom {
	/**
	 * 主表ID
	 */
	private Long mid;

	/**
	 * 母件编号
	 */
	private String motherCode;

	/**
	 * BOM ID
	 */
	private Long bomId;


	/**
	 * 名称
	 */
	private String title;

	/**
	 * 单位
	 */
	private String unit;

	/**
	 * 规格
	 */
	private String spec;

	/**
	 * 版本
	 */
	private String version;

	/**
	 * UAS预估总价
	 */
	private Double uasEstimatePrice;

	/**
	 * 币种
	 */
	private String currency;

	/**
	 * 平台参考价
	 */
	private Double refPrice;

	/**
	 * 生效日期
	 */
	private Date effectiveDate;

	/**
	 * 失效日期
	 */
	private Date deadline;

	/**
	 * bom分类
	 */
	private String bomClassify;

	/**
	 * BOM等级
	 */
	private String bomLevel;

	/**
	 * 编制人UU
	 */
	private Long orgUU;

	/**
	 * 录入人UU
	 */
	private Long inputUU;

	/**
	 * 审核人UU
	 */
	private String checkUU;

	/**
	 * 状态
	 */
	private Integer bomStatus;

	/**
	 * 所属企业UU
	 */
	private Long enuu;

	/**
	 * 录入日期
	 */
	private Date inputDate;

	/**
	 * bom明细
	 */
	private Set<BomDetail> bomDetails;

	public Long getMid() {
		return mid;
	}

	public void setMid(Long mid) {
		this.mid = mid;
	}


	public String getMotherCode() {
		return motherCode;
	}

	public void setMotherCode(String motherCode) {
		this.motherCode = motherCode;
	}

	public Long getBomId() {
		return bomId;
	}

	public void setBomId(Long bomId) {
		this.bomId = bomId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Double getUasEstimatePrice() {
		return uasEstimatePrice;
	}

	public void setUasEstimatePrice(Double uasEstimatePrice) {
		this.uasEstimatePrice = uasEstimatePrice;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Double getRefPrice() {
		return refPrice;
	}

	public void setRefPrice(Double refPrice) {
		this.refPrice = refPrice;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public String getBomClassify() {
		return bomClassify;
	}

	public void setBomClassify(String bomClassify) {
		this.bomClassify = bomClassify;
	}

	public String getBomLevel() {
		return bomLevel;
	}

	public void setBomLevel(String bomLevel) {
		this.bomLevel = bomLevel;
	}

	public Long getOrgUU() {
		return orgUU;
	}

	public void setOrgUU(Long orgUU) {
		this.orgUU = orgUU;
	}

	public Long getInputUU() {
		return inputUU;
	}

	public void setInputUU(Long inputUU) {
		this.inputUU = inputUU;
	}

	public String getCheckUU() {
		return checkUU;
	}

	public void setCheckUU(String checkUU) {
		this.checkUU = checkUU;
	}

	public Integer getBomStatus() {
		return bomStatus;
	}

	public void setBomStatus(Integer bomStatus) {
		this.bomStatus = bomStatus;
	}

	public Long getEnuu() {
		return enuu;
	}

	public void setEnuu(Long enuu) {
		this.enuu = enuu;
	}

	public Date getInputDate() {
		return inputDate;
	}

	public void setInputDate(Date inputDate) {
		this.inputDate = inputDate;
	}

	public Set<BomDetail> getBomDetails() {
		return bomDetails;
	}

	public void setBomDetails(Set<BomDetail> bomDetails) {
		this.bomDetails = bomDetails;
	}

	@Override
	public String toString() {
		return "Bom [mid=" + mid + ", motherCode=" + motherCode + ", bomId=" + bomId + ", title=" + title + ", unit="
				+ unit + ", spec=" + spec + ", version=" + version + ", uasEstimatePrice=" + uasEstimatePrice
				+ ", currency=" + currency + ", refPrice=" + refPrice + ", effectiveDate=" + effectiveDate
				+ ", deadline=" + deadline + ", bomClassify=" + bomClassify + ", bomLevel=" + bomLevel + ", orgUU="
				+ orgUU + ", inputUU=" + inputUU + ", checkUU=" + checkUU + ", bomStatus=" + bomStatus + ", enuu="
				+ enuu + ", inputDate=" + inputDate + ", bomDetails=" + bomDetails + "]";
	}

	
}
