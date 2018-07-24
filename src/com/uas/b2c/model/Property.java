package com.uas.b2c.model;

import java.io.Serializable;

/**
 * 商品关联类别的类别属性值<br>
 * 所有的数据都用字符串来存
 * 
 * @author ChenHao
 * @date 2015年12月3日17:31:49
 *
 */
public class Property implements Serializable {

	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 1L;

	private String stringValue;

	private Integer num;

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

}
