package com.uas.erp.model;

import java.io.Serializable;
/**
 * 数据更新方案明细表
 */
public class UpdateSchemeDetail implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int scheme_Id_;//关联UpdateScheme主表id 
	private String field_;// 字段名
	private String caption_;//字段描述
	private String type_;//字段类型	
	private Integer width_;//宽度
	private Integer checked_;//是否选中
	public int getScheme_Id_() {
		return scheme_Id_;
	}
	public void setScheme_Id_(int scheme_Id_) {
		this.scheme_Id_ = scheme_Id_;
	}
	public String getField_() {
		return field_;
	}
	public void setField_(String field_) {
		this.field_ = field_;
	}
	public String getCaption_() {
		return caption_;
	}
	public void setCaption_(String caption_) {
		this.caption_ = caption_;
	}
	public String getType_() {
		return type_;
	}
	public void setType_(String type_) {
		this.type_ = type_;
	}
	public Integer getWidth_() {
		return width_;
	}
	public void setWidth_(Integer width_) {
		this.width_ = width_;
	}
	public Integer getChecked_() {
		return checked_;
	}
	public void setChecked_(Integer checked_) {
		this.checked_ = checked_;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}	
}
