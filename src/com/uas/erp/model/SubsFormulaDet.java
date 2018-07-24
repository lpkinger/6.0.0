package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.dao.Saveable;

public class SubsFormulaDet implements Saveable, Serializable {

	private static final long serialVersionUID = 1L;
	private String description_; // 列描述
	private String field_; // 列
	private String format_; // 格式化方式
	private String type_; // 字段類型
	private int formula_id_; // 关联订阅项目id
	private int det_id_;
	private int sum_; // 是否需要合计
	private int width_; // 列宽
	private int detno_; // 序号

	public String getDescription_() {
		return description_;
	}

	public void setDescription_(String description_) {
		this.description_ = description_;
	}

	public String getFormat_() {
		return format_;
	}

	public void setFormat_(String format_) {
		this.format_ = format_;
	}

	public int getFormula_id_() {
		return formula_id_;
	}

	public void setFormula_id_(int formula_id_) {
		this.formula_id_ = formula_id_;
	}

	public int getDet_id_() {
		return det_id_;
	}

	public void setDet_id_(int det_id_) {
		this.det_id_ = det_id_;
	}

	public int getSum_() {
		return sum_;
	}

	public void setSum_(int sum_) {
		this.sum_ = sum_;
	}

	public int getWidth_() {
		return width_;
	}

	public void setWidth_(int width_) {
		this.width_ = width_;
	}

	public String getField_() {
		return field_;
	}

	public void setField_(String field_) {
		this.field_ = field_;
	}

	public int getDetno_() {
		return detno_;
	}

	public void setDetno_(int detno_) {
		this.detno_ = detno_;
	}

	public String getType_() {
		return type_;
	}

	public void setType_(String type_) {
		this.type_ = type_;
	}

	@Override
	public String table() {
		return "subsformula_det";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "id_" };
	}

}
