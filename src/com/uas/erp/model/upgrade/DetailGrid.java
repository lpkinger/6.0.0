package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的detailgrid
 * 
 * @author yingp
 *
 */
public class DetailGrid implements Saveable{

	private String dg_caller;
	private Float dg_sequence;
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
	// 明细限制不能超过输入长度
	private Integer dg_maxlength;
	/**
	 * 需要校验
	 */
	private Short dg_check;

	private String plan_id;

	public String getDg_caller() {
		return dg_caller;
	}

	public void setDg_caller(String dg_caller) {
		this.dg_caller = dg_caller;
	}

	public Float getDg_sequence() {
		return dg_sequence;
	}

	public void setDg_sequence(Float dg_sequence) {
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

	public String getDg_table() {
		return dg_table;
	}

	public void setDg_table(String dg_table) {
		this.dg_table = dg_table;
	}

	public String getDg_summarytype() {
		return dg_summarytype;
	}

	public void setDg_summarytype(String dg_summarytype) {
		this.dg_summarytype = dg_summarytype;
	}

	public String getDg_allowblank() {
		return dg_allowblank;
	}

	public void setDg_allowblank(String dg_allowblank) {
		this.dg_allowblank = dg_allowblank;
	}

	public String getDg_logictype() {
		return dg_logictype;
	}

	public void setDg_logictype(String dg_logictype) {
		this.dg_logictype = dg_logictype;
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

	public String getDg_minvalue() {
		return dg_minvalue;
	}

	public void setDg_minvalue(String dg_minvalue) {
		this.dg_minvalue = dg_minvalue;
	}

	public Integer getDg_maxlength() {
		return dg_maxlength;
	}

	public void setDg_maxlength(Integer dg_maxlength) {
		this.dg_maxlength = dg_maxlength;
	}

	public Short getDg_check() {
		return dg_check;
	}

	public void setDg_check(Short dg_check) {
		this.dg_check = dg_check;
	}

	@JsonIgnore
	public String getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}

	@Override
	public String table() {
		return "upgrade$detailgrid";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
