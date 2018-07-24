package com.uas.erp.model;

import java.io.Serializable;

public class VendorKind implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int vk_id;
	private String vk_code;
	private String vk_kind;
	private int vk_subof;
	private String vk_leaf;

	public int getvk_id() {
		return vk_id;
	}

	public void setvk_id(int vk_id) {
		this.vk_id = vk_id;
	}

	public String getvk_code() {
		return vk_code;
	}

	public void setvk_code(String vk_code) {
		this.vk_code = vk_code;
	}

	public String getvk_kind() {
		return vk_kind;
	}

	public void setvk_kind(String vk_kind) {
		this.vk_kind = vk_kind;
	}

	public int getvk_subof() {
		return vk_subof;
	}

	public void setvk_subof(int vk_subof) {
		this.vk_subof = vk_subof;
	}

	public String getvk_leaf() {
		return vk_leaf;
	}

	public void setvk_leaf(String vk_leaf) {
		this.vk_leaf = vk_leaf;
	}
}
