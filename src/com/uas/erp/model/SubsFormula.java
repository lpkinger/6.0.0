package com.uas.erp.model;

import java.io.Serializable;
import java.util.List;

import com.uas.erp.dao.Saveable;

public class SubsFormula implements Saveable, Serializable {

	private static final long serialVersionUID = 1L;
	private String before_;// 执行前
	private String code_;
	private int id_;
	private String keydisplay_; // 参考描述
	private String keyfield_;// 参照字段
	private String sql_;
	private String style_;// 展现方式(柱状、饼状、列表...)
	private String title_;
	private String valuedisplay_;// 值描述
	private String valuefield_;// 值字段
	private String unit_;
	private Integer checked_;
	private String status_;
	private String statusCode_;
	private String desc_;
	private Integer isApplied_;
	private String intro_;
	private String img_;
	private List<SubsFormulaDet> dets;

	public String getBefore_() {
		return before_;
	}

	public void setBefore_(String before_) {
		this.before_ = before_;
	}

	public String getCode_() {
		return code_;
	}

	public void setCode_(String code_) {
		this.code_ = code_;
	}

	public int getId_() {
		return id_;
	}

	public void setId_(int id_) {
		this.id_ = id_;
	}

	public String getKeydisplay_() {
		return keydisplay_;
	}

	public void setKeydisplay_(String keydisplay_) {
		this.keydisplay_ = keydisplay_;
	}

	public String getKeyfield_() {
		return keyfield_;
	}

	public void setKeyfield_(String keyfield_) {
		this.keyfield_ = keyfield_;
	}

	public String getSql_() {
		return sql_;
	}

	public void setSql_(String sql_) {
		this.sql_ = sql_;
	}

	public String getStyle_() {
		return style_;
	}

	public void setStyle_(String style_) {
		this.style_ = style_;
	}

	public String getTitle_() {
		return title_;
	}

	public void setTitle_(String title_) {
		this.title_ = title_;
	}

	public String getValuedisplay_() {
		return valuedisplay_;
	}

	public void setValuedisplay_(String valuedisplay_) {
		this.valuedisplay_ = valuedisplay_;
	}

	public String getValuefield_() {
		return valuefield_;
	}

	public void setValuefield_(String valuefield_) {
		this.valuefield_ = valuefield_;
	}

	public String getUnit_() {
		return unit_;
	}

	public void setUnit_(String unit_) {
		this.unit_ = unit_;
	}

	public Integer getChecked_() {
		return checked_;
	}

	public void setChecked_(Integer checked_) {
		this.checked_ = checked_;
	}

	public String getStatus_() {
		return status_;
	}

	public void setStatus_(String status) {
		this.status_ = status;
	}

	public String getStatusCode_() {
		return statusCode_;
	}

	public void setStatusCode_(String statusCode_) {
		this.statusCode_ = statusCode_;
	}

	public String getDesc_() {
		return desc_;
	}

	public void setDesc_(String desc_) {
		this.desc_ = desc_;
	}

	public Integer getIsApplied_() {
		return isApplied_;
	}

	public void setIsApplied_(Integer isApplied_) {
		this.isApplied_ = isApplied_;
	}

	public String getIntro_() {
		return intro_;
	}

	public void setIntro_(String intro_) {
		this.intro_ = intro_;
	}

	public String getImg_() {
		return img_;
	}

	public void setImg_(String img_) {
		this.img_ = img_;
	}

	public List<SubsFormulaDet> getDets() {
		return dets;
	}

	public void setDets(List<SubsFormulaDet> dets) {
		this.dets = dets;
	}

	@Override
	public String table() {
		return "SubsFormula";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "id_" };
	}

}
