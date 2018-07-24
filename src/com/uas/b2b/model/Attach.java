package com.uas.b2b.model;

import java.io.Serializable;

/**
 * 附件信息
 * 
 * @author suntg
 * 
 */
public class Attach implements Serializable {

	public static final String DOWN_FILE_ACTION = "/common/downloadbyId.action?id=";

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * ID
	 */
	private Long fp_id;
	/**
	 * 附件名称
	 */
	private String fp_name;
	/**
	 * 附件Url
	 */
	private String fp_url;
	/**
	 * 附件大小
	 */
	private Long fp_size;

	public Long getFp_id() {
		return fp_id;
	}

	public void setFp_id(Long fp_id) {
		this.fp_id = fp_id;
	}

	public String getFp_name() {
		return fp_name;
	}

	public void setFp_name(String fp_name) {
		this.fp_name = fp_name;
	}

	public String getFp_url() {
		return fp_url;
	}

	public void setFp_url(String fp_url) {
		this.fp_url = fp_url;
	}

	public Long getFp_size() {
		return fp_size;
	}

	public void setFp_size(Long fp_size) {
		this.fp_size = fp_size;
	}

}
