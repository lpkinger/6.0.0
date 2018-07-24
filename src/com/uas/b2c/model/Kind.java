package com.uas.b2c.model;

import java.io.Serializable;

import com.uas.erp.core.bind.Constant;

/**
 * @Description: 商品类目
 * @author yingp
 * @date 2014年8月14日 上午12:32:10
 * @version V1.0
 */
public class Kind implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;

	/**
	 * 父级类目ID，0 为最顶层节点
	 */
	private Long parentId;

	/**
	 * 是否最下级类目
	 */
	private Short isLeaf;

	/**
	 * 在父节点下的序号
	 */
	private Short number;

	/**
	 * 类目名称
	 */
	private String name;

	/**
	 * 英文名称
	 */
	private String name_en;

	/**
	 * 类目下属器件数量
	 */
	private Long count;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Short getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(Short isLeaf) {
		this.isLeaf = isLeaf;
	}

	public boolean isLeaf() {
		return getIsLeaf() == Constant.YES;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Short getNumber() {
		return number;
	}

	public void setNumber(Short number) {
		this.number = number;
	}

	public String getName_en() {
		return name_en;
	}

	public void setName_en(String name_en) {
		this.name_en = name_en;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public void addCount() {
		this.count = this.count + 1;
	}

}
