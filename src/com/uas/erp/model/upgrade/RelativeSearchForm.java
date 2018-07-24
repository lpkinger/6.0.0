package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的relativesearchform
 * 
 * @author yingp
 *
 */
public class RelativeSearchForm implements Saveable{

	private String rsf_field;
	private String rsf_caption;
	private String rsf_type;
	private Float rsf_width;
	private String rsf_logic;
	private Integer rsf_detno;
	
	private String rsf_rsid;

	public String getRsf_field() {
		return rsf_field;
	}

	public void setRsf_field(String rsf_field) {
		this.rsf_field = rsf_field;
	}

	public String getRsf_caption() {
		return rsf_caption;
	}

	public void setRsf_caption(String rsf_caption) {
		this.rsf_caption = rsf_caption;
	}

	public String getRsf_type() {
		return rsf_type;
	}

	public void setRsf_type(String rsf_type) {
		this.rsf_type = rsf_type;
	}

	public Float getRsf_width() {
		return rsf_width;
	}

	public void setRsf_width(Float rsf_width) {
		this.rsf_width = rsf_width;
	}

	public String getRsf_logic() {
		return rsf_logic;
	}

	public void setRsf_logic(String rsf_logic) {
		this.rsf_logic = rsf_logic;
	}

	public Integer getRsf_detno() {
		return rsf_detno;
	}

	public void setRsf_detno(Integer rsf_detno) {
		this.rsf_detno = rsf_detno;
	}

	@JsonIgnore
	public String getRsf_rsid() {
		return rsf_rsid;
	}

	public void setRsf_rsid(String rsf_rsid) {
		this.rsf_rsid = rsf_rsid;
	}

	@Override
	public String table() {
		return "upgrade$relativesearchform";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
