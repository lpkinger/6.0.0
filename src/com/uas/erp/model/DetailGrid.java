package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.Saveable;

public class DetailGrid implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int dg_id;
	private String dg_caller;
	private int dg_sequence;
	private String dg_caption;
	private Integer dg_width;
	private Float dg_visible;
	private Float dg_editable;
	private Float dg_dbbutton;
	private String dg_field;
	private String dg_type;
	private String dg_gridenabled;
	private String dg_findfunctionname;
	private String dg_captionfan;
	private String dg_captionen;
	private String dg_table;
	private String dg_summarytype;
	private String dg_allowblank;
	private String dg_logictype;
	private String dg_renderer;
	private Integer dg_locked;
	private String dg_minvalue;
	private String dg_modify;// 非在录入状态可更新
	/**
	 * 需要校验
	 */
	// 明细限制不能超过输入长度
	private Integer dg_maxlength;
	private Float dg_check;

	public String getDg_allowblank() {
		return dg_allowblank;
	}

	public void setDg_allowblank(String dg_allowblank) {
		this.dg_allowblank = dg_allowblank;
	}

	public String getDg_minvalue() {
		return dg_minvalue;
	}

	public void setDg_minvalue(String dg_minvalue) {
		this.dg_minvalue = dg_minvalue;
	}

	public int getDg_id() {
		return dg_id;
	}

	public void setDg_id(int dg_id) {
		this.dg_id = dg_id;
	}

	public String getDg_table() {
		return dg_table;
	}

	public void setDg_table(String dg_table) {
		this.dg_table = dg_table;
	}

	public String getDg_caller() {
		return dg_caller;
	}

	public void setDg_caller(String dg_caller) {
		this.dg_caller = dg_caller;
	}

	public int getDg_sequence() {
		return dg_sequence;
	}

	public void setDg_sequence(int dg_sequence) {
		this.dg_sequence = dg_sequence;
	}

	public String getDg_caption() {
		return dg_caption;
	}

	public void setDg_caption(String dg_caption) {
		this.dg_caption = dg_caption;
	}

	public Integer getDg_width() {
		return dg_width;
	}

	public void setDg_width(Integer dg_width) {
		this.dg_width = dg_width;
	}

	public Float getDg_visible() {
		return dg_visible;
	}

	public void setDg_visible(Float dg_visible) {
		this.dg_visible = dg_visible;
	}

	public Float getDg_editable() {
		return dg_editable;
	}

	public void setDg_editable(Float dg_editable) {
		this.dg_editable = dg_editable;
	}

	public Float getDg_dbbutton() {
		return dg_dbbutton;
	}

	public void setDg_dbbutton(Float dg_dbbutton) {
		this.dg_dbbutton = dg_dbbutton;
	}

	public String getDg_field() {
		return dg_field;
	}

	public void setDg_field(String dg_field) {
		this.dg_field = dg_field;
	}

	public String getDg_type() {
		return dg_type;
	}

	public void setDg_type(String dg_type) {
		this.dg_type = dg_type;
	}

	public String getDg_gridenabled() {
		return dg_gridenabled;
	}

	public void setDg_gridenabled(String dg_gridenabled) {
		this.dg_gridenabled = dg_gridenabled;
	}

	public String getDg_findfunctionname() {
		return dg_findfunctionname;
	}

	public void setDg_findfunctionname(String dg_findfunctionname) {
		this.dg_findfunctionname = dg_findfunctionname;
	}

	public String getDg_captionfan() {
		return dg_captionfan;
	}

	public void setDg_captionfan(String dg_captionfan) {
		this.dg_captionfan = dg_captionfan;
	}

	public String getDg_captionen() {
		return dg_captionen;
	}

	public void setDg_captionen(String dg_captionen) {
		this.dg_captionen = dg_captionen;
	}

	public String getDg_logictype() {
		return dg_logictype;
	}

	public void setDg_logictype(String dg_logictype) {
		this.dg_logictype = dg_logictype;
	}

	public String getDg_summarytype() {
		return dg_summarytype;
	}

	public void setDg_summarytype(String dg_summarytype) {
		this.dg_summarytype = dg_summarytype;
	}

	public String getDg_renderer() {
		return dg_renderer;
	}

	public void setDg_renderer(String dg_renderer) {
		this.dg_renderer = dg_renderer;
	}

	public Integer getDg_locked() {
		return dg_locked;
	}

	public void setDg_locked(Integer dg_locked) {
		this.dg_locked = dg_locked;
	}

	public Float getDg_check() {
		return dg_check;
	}

	public void setDg_check(Float dg_check) {
		this.dg_check = dg_check;
	}

	public Integer getDg_maxlength() {
		return dg_maxlength;
	}

	public void setDg_maxlength(Integer dg_maxlength) {
		this.dg_maxlength = dg_maxlength;
	}

	@Override
	public String table() {
		return "detailgrid";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "dg_id" };
	}

	/**
	 * 是否需要校验
	 * 
	 * @return
	 */
	public boolean isNeedCheck() {
		return Constant.YES == this.dg_check && StringUtil.hasText(this.dg_findfunctionname);
	}

	public DetailGrid() {
		super();
	}

	public DetailGrid(int dg_sequence, String dg_caption, int dg_width, Float dg_dbbutton, String dg_field, String dg_logictype,
			String dg_type, String dg_table, Integer dg_locked, Float dg_editable, String dg_findfunctionname) {
		this.dg_sequence = dg_sequence;
		this.dg_caption = dg_caption;
		this.dg_width = dg_width;
		this.dg_dbbutton = dg_dbbutton;
		this.dg_field = dg_field;
		this.dg_logictype = dg_logictype;
		this.dg_type = dg_type;
		this.dg_table = dg_table;
		this.dg_locked = dg_locked;
		this.dg_editable = dg_editable;
		this.dg_findfunctionname = dg_findfunctionname;
		/*
		 * private int dg_id; private String dg_caller; private Float dg_visible; private Float dg_editable; private String dg_gridenabled; private String dg_captionfan; private String dg_captionen; private String dg_summarytype; private String dg_allowblank; private String dg_logictype; private String dg_renderer; private String dg_minvalue;
		 */
	}

	public String getDg_modify() {
		return dg_modify;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dg_caller == null) ? 0 : dg_caller.hashCode());
		result = prime * result + ((dg_field == null) ? 0 : dg_field.hashCode());
		result = prime * result + dg_id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DetailGrid other = (DetailGrid) obj;
		if (dg_caller == null) {
			if (other.dg_caller != null)
				return false;
		} else if (!dg_caller.equals(other.dg_caller))
			return false;
		if (dg_field == null) {
			if (other.dg_field != null)
				return false;
		} else if (!dg_field.equals(other.dg_field))
			return false;
		if (dg_id != other.dg_id)
			return false;
		return true;
	}

	public void setDg_modify(String dg_modify) {
		this.dg_modify = dg_modify;
	}

}
