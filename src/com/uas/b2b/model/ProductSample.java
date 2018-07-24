package com.uas.b2b.model;

import java.util.Date;
import java.util.List;

import com.uas.erp.core.support.KeyEntity;
import com.uas.erp.service.common.AttachUploadedAble;

/**
 * 买家ERP系统的采购询价单
 * 
 * @author yingp
 * 
 */
public class ProductSample extends KeyEntity implements AttachUploadedAble{
	
	private Long ps_id;
	private String ps_code;
	private String ps_recordor;
	private Date ps_indate;
	private Long ps_appmanuu;
	private String ps_prodcode;
	private String ps_isfree;//是否收费
	private Date ps_delivery;
	private String ps_envrequire;
	private String ps_scope;
	private String ps_remark;
	private String ps_attach;//附件
	private List<ProductSampleDetail> details;
	private List<Attach> attaches;// 附件信息列表
	
	public Long getPs_id() {
		return ps_id;
	}

	public void setPs_id(Long ps_id) {
		this.ps_id = ps_id;
	}

	public String getPs_code() {
		return ps_code;
	}

	public void setPs_code(String ps_code) {
		this.ps_code = ps_code;
	}

	public String getPs_recordor() {
		return ps_recordor;
	}

	public void setPs_recordor(String ps_recordor) {
		this.ps_recordor = ps_recordor;
	}

	public Date getPs_indate() {
		return ps_indate;
	}

	public void setPs_indate(Date ps_indate) {
		this.ps_indate = ps_indate;
	}

	public Long getPs_appmanuu() {
		return ps_appmanuu;
	}

	public void setPs_appmanuu(Long ps_appmanuu) {
		this.ps_appmanuu = ps_appmanuu;
	}

	public String getPs_prodcode() {
		return ps_prodcode;
	}

	public void setPs_prodcode(String ps_prodcode) {
		this.ps_prodcode = ps_prodcode;
	}

	public String getPs_isfree() {
		return ps_isfree;
	}

	public void setPs_isfree(String ps_isfree) {
		this.ps_isfree = ps_isfree;
	}

	public Date getPs_delivery() {
		return ps_delivery;
	}

	public void setPs_delivery(Date ps_delivery) {
		this.ps_delivery = ps_delivery;
	}

	public String getPs_envrequire() {
		return ps_envrequire;
	}

	public void setPs_envrequire(String ps_envrequire) {
		this.ps_envrequire = ps_envrequire;
	}

	public String getPs_scope() {
		return ps_scope;
	}

	public void setPs_scope(String ps_scope) {
		this.ps_scope = ps_scope;
	}

	public String getPs_remark() {
		return ps_remark;
	}

	public void setPs_remark(String ps_remark) {
		this.ps_remark = ps_remark;
	}

	public String getPs_attach() {
		return ps_attach;
	}

	public void setPs_attach(String ps_attach) {
		this.ps_attach = ps_attach;
	}

	public List<ProductSampleDetail> getDetails() {
		return details;
	}

	public void setDetails(List<ProductSampleDetail> details) {
		this.details = details;
	}

	@Override
	public Object getKey() {
		return this.ps_id;
	}

	@Override
	public String getAttachs() {
		return this.ps_attach;
	}

	@Override
	public Object getReffrencValue() {
		return this.ps_code;
	}

	public List<Attach> getAttaches() {
		return attaches;
	}

	public void setAttaches(List<Attach> attaches) {
		this.attaches = attaches;
	}

}
