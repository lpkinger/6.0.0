package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的initdetail
 * 
 * @author yingp
 *
 */
public class InitDetail implements Saveable{

	private String id_caller;// caller
	private String id_table;// 表名
	private String id_field;// 字段
	private String id_logic;// 校验公式
	private String id_default;// 转正式默认值
	private String id_caption;// 描述
	private Short id_visible;// 是否显示(1-可见)
	private Short id_need;// 是否必填(1-必填)
	private Short id_detno;// 排序
	private Double id_width;// 列宽
	private String id_type;// 字段类型(number,varchar2(100)...)
	private String id_rule;// 要求
	private int id_fieldtype;// 字段类型：主表-0，从表-1，其它-2
	
	private String plan_id;

	public String getId_caller() {
		return id_caller;
	}

	public void setId_caller(String id_caller) {
		this.id_caller = id_caller;
	}

	public String getId_table() {
		return id_table;
	}

	public void setId_table(String id_table) {
		this.id_table = id_table;
	}

	public String getId_field() {
		return id_field;
	}

	public void setId_field(String id_field) {
		this.id_field = id_field;
	}

	public String getId_logic() {
		return id_logic;
	}

	public void setId_logic(String id_logic) {
		this.id_logic = id_logic;
	}

	public String getId_default() {
		return id_default;
	}

	public void setId_default(String id_default) {
		this.id_default = id_default;
	}

	public String getId_caption() {
		return id_caption;
	}

	public void setId_caption(String id_caption) {
		this.id_caption = id_caption;
	}

	public Short getId_visible() {
		return id_visible;
	}

	public void setId_visible(Short id_visible) {
		this.id_visible = id_visible;
	}

	public Short getId_need() {
		return id_need;
	}

	public void setId_need(Short id_need) {
		this.id_need = id_need;
	}

	public Short getId_detno() {
		return id_detno;
	}

	public void setId_detno(Short id_detno) {
		this.id_detno = id_detno;
	}

	public Double getId_width() {
		return id_width;
	}

	public void setId_width(Double id_width) {
		this.id_width = id_width;
	}

	public String getId_type() {
		return id_type;
	}

	public void setId_type(String id_type) {
		this.id_type = id_type;
	}

	public String getId_rule() {
		return id_rule;
	}

	public void setId_rule(String id_rule) {
		this.id_rule = id_rule;
	}

	public int getId_fieldtype() {
		return id_fieldtype;
	}

	public void setId_fieldtype(int id_fieldtype) {
		this.id_fieldtype = id_fieldtype;
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
		return "upgrade$initdetail";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
