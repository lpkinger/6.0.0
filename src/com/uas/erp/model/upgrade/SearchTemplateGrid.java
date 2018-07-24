package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的searchtemplategrid
 * 
 * @author yingp
 *
 */
public class SearchTemplateGrid implements Saveable{

	private String stg_field;
	private String stg_text;
	private String stg_operator;// 运算符
	private String stg_value;
	private Integer stg_use = 1;// 是否显示
	private Integer stg_query = 0;// 用于查询
	private Integer stg_lock = 0;// 是否固定列
	private Integer stg_group = 0;// 是否分组
	private Integer stg_sum = 0;// 合计
	private Short stg_detno;
	private Integer stg_dbfind = 0;// Dbfind字段
	private Integer stg_double = 0;// 多字段
	private String stg_type;// 字段类型
	private String stg_table;// 表名
	private Double stg_width;// 列宽
	private String stg_format;// 格式转换
	private String stg_mode;// 多选模式
	// 链接
	private String stg_link;
	private String stg_tokentab1;
	private String stg_tokencol1;
	private String stg_tokentab2;
	private String stg_tokencol2;

	private String stg_formula;// 自定义公式

	private String stg_stid;

	public String getStg_field() {
		return stg_field;
	}

	public void setStg_field(String stg_field) {
		this.stg_field = stg_field;
	}

	public String getStg_text() {
		return stg_text;
	}

	public void setStg_text(String stg_text) {
		this.stg_text = stg_text;
	}

	public String getStg_operator() {
		return stg_operator;
	}

	public void setStg_operator(String stg_operator) {
		this.stg_operator = stg_operator;
	}

	public String getStg_value() {
		return stg_value;
	}

	public void setStg_value(String stg_value) {
		this.stg_value = stg_value;
	}

	public Integer getStg_use() {
		return stg_use;
	}

	public void setStg_use(Integer stg_use) {
		this.stg_use = stg_use;
	}

	public Integer getStg_query() {
		return stg_query;
	}

	public void setStg_query(Integer stg_query) {
		this.stg_query = stg_query;
	}

	public Integer getStg_lock() {
		return stg_lock;
	}

	public void setStg_lock(Integer stg_lock) {
		this.stg_lock = stg_lock;
	}

	public Integer getStg_group() {
		return stg_group;
	}

	public void setStg_group(Integer stg_group) {
		this.stg_group = stg_group;
	}

	public Integer getStg_sum() {
		return stg_sum;
	}

	public void setStg_sum(Integer stg_sum) {
		this.stg_sum = stg_sum;
	}

	public Short getStg_detno() {
		return stg_detno;
	}

	public void setStg_detno(Short stg_detno) {
		this.stg_detno = stg_detno;
	}

	public Integer getStg_dbfind() {
		return stg_dbfind;
	}

	public void setStg_dbfind(Integer stg_dbfind) {
		this.stg_dbfind = stg_dbfind;
	}

	public Integer getStg_double() {
		return stg_double;
	}

	public void setStg_double(Integer stg_double) {
		this.stg_double = stg_double;
	}

	public String getStg_type() {
		return stg_type;
	}

	public void setStg_type(String stg_type) {
		this.stg_type = stg_type;
	}

	public String getStg_table() {
		return stg_table;
	}

	public void setStg_table(String stg_table) {
		this.stg_table = stg_table;
	}

	public Double getStg_width() {
		return stg_width;
	}

	public void setStg_width(Double stg_width) {
		this.stg_width = stg_width;
	}

	public String getStg_format() {
		return stg_format;
	}

	public void setStg_format(String stg_format) {
		this.stg_format = stg_format;
	}

	public String getStg_mode() {
		return stg_mode;
	}

	public void setStg_mode(String stg_mode) {
		this.stg_mode = stg_mode;
	}

	public String getStg_link() {
		return stg_link;
	}

	public void setStg_link(String stg_link) {
		this.stg_link = stg_link;
	}

	public String getStg_tokentab1() {
		return stg_tokentab1;
	}

	public void setStg_tokentab1(String stg_tokentab1) {
		this.stg_tokentab1 = stg_tokentab1;
	}

	public String getStg_tokencol1() {
		return stg_tokencol1;
	}

	public void setStg_tokencol1(String stg_tokencol1) {
		this.stg_tokencol1 = stg_tokencol1;
	}

	public String getStg_tokentab2() {
		return stg_tokentab2;
	}

	public void setStg_tokentab2(String stg_tokentab2) {
		this.stg_tokentab2 = stg_tokentab2;
	}

	public String getStg_tokencol2() {
		return stg_tokencol2;
	}

	public void setStg_tokencol2(String stg_tokencol2) {
		this.stg_tokencol2 = stg_tokencol2;
	}

	public String getStg_formula() {
		return stg_formula;
	}

	public void setStg_formula(String stg_formula) {
		this.stg_formula = stg_formula;
	}

	@JsonIgnore
	public String getStg_stid() {
		return stg_stid;
	}

	public void setStg_stid(String stg_stid) {
		this.stg_stid = stg_stid;
	}

	@Override
	public String table() {
		return "upgrade$searchtemplategrid";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
