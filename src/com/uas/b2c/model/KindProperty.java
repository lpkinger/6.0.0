package com.uas.b2c.model;

import java.io.Serializable;

import com.uas.erp.core.bind.Constant;

/**
 * 商品每个类目定义的属性
 * 
 * @author yingp
 * 
 */
public class KindProperty implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 序号
	 */
	private Integer num;

	/**
	 * 属性名称
	 */
	private String label;

	/**
	 * 属性名称英文
	 */
	private String labelEnglish;

	/**
	 * 属性输入类型
	 */
	private String type;

	/**
	 * 属性数据最大字节数
	 */
	private Integer dataLength;

	/**
	 * 帮助信息
	 */
	private String helpinfo;

	/**
	 * 占位符
	 */
	private String placeholder;

	/**
	 * 前置
	 */
	private String prefix;

	/**
	 * 后置
	 */
	private String subfix;

	/**
	 * 选项
	 */
	private String options;

	/**
	 * 必须填写?(1是/0否)
	 */
	private Short required;

	/**
	 * 是否主要的属性（主要的属性会在类目展示列表中显示）(1是/0否) 每个类目下最多只能有7个主要属性
	 */
	private Short primary;

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabelEnglish() {
		return labelEnglish;
	}

	public void setLabelEnglish(String labelEnglish) {
		this.labelEnglish = labelEnglish;
	}

	public Integer getDataLength() {
		return dataLength;
	}

	public void setDataLength(Integer dataLength) {
		this.dataLength = dataLength;
	}

	public String getHelpinfo() {
		return helpinfo;
	}

	public void setHelpinfo(String helpinfo) {
		this.helpinfo = helpinfo;
	}

	public String getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSubfix() {
		return subfix;
	}

	public void setSubfix(String subfix) {
		this.subfix = subfix;
	}

	public void setRequired(Short required) {
		this.required = required;
	}

	public boolean isRequired() {
		return this.required == Constant.YES;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public boolean isPrimary() {
		return this.primary == Constant.YES;
	}

	public void setPrimary(Short primary) {
		this.primary = primary;
	}

}
