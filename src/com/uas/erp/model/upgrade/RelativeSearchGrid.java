package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的relativesearchgrid
 * 
 * @author yingp
 *
 */
public class RelativeSearchGrid implements Saveable {

	private String rsg_field;
	private String rsg_caption;
	private String rsg_type;
	private Float rsg_width;
	private Integer rsg_detno;
	private String rsg_url;
	private String rsg_rsid;

	public String getRsg_field() {
		return rsg_field;
	}

	public void setRsg_field(String rsg_field) {
		this.rsg_field = rsg_field;
	}

	public String getRsg_caption() {
		return rsg_caption;
	}

	public void setRsg_caption(String rsg_caption) {
		this.rsg_caption = rsg_caption;
	}

	public String getRsg_type() {
		return rsg_type;
	}

	public void setRsg_type(String rsg_type) {
		this.rsg_type = rsg_type;
	}

	public Float getRsg_width() {
		return rsg_width;
	}

	public void setRsg_width(Float rsg_width) {
		this.rsg_width = rsg_width;
	}

	public Integer getRsg_detno() {
		return rsg_detno;
	}

	public void setRsg_detno(Integer rsg_detno) {
		this.rsg_detno = rsg_detno;
	}

	public String getRsg_url() {
		return rsg_url;
	}

	public void setRsg_url(String rsg_url) {
		this.rsg_url = rsg_url;
	}

	@JsonIgnore
	public String getRsg_rsid() {
		return rsg_rsid;
	}

	public void setRsg_rsid(String rsg_rsid) {
		this.rsg_rsid = rsg_rsid;
	}

	@Override
	public String table() {
		return "upgrade$relativesearchgrid";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
