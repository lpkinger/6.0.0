package com.uas.erp.model;

import java.io.Serializable;
import java.util.Map;

import com.uas.erp.core.StringUtil;
public class FeedBackFlow implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int ff_id;
	private String ff_code;
	private int ff_detno;
	private String ff_step;
	private int ff_autoend;
	private String ff_caption;
	private String ff_manidfield;
	private String ff_manfield;
	private String ff_mansql;
	private String ff_resultfield;
	private String ff_plandatefield;
	private String ff_dealdatefield;
	private String ff_remarkfield;
	private String ff_otherfields;
	public int getFf_id() {
		return ff_id;
	}
	public void setFf_id(int ff_id) {
		this.ff_id = ff_id;
	}
	public String getFf_code() {
		return ff_code;
	}
	public void setFf_code(String ff_code) {
		this.ff_code = ff_code;
	}
	public int getFf_detno() {
		return ff_detno;
	}
	public void setFf_detno(int ff_detno) {
		this.ff_detno = ff_detno;
	}
	public String getFf_step() {
		return ff_step;
	}
	public void setFf_step(String ff_step) {
		this.ff_step = ff_step;
	}
	public int getFf_autoend() {
		return ff_autoend;
	}
	public void setFf_autoend(int ff_autoend) {
		this.ff_autoend = ff_autoend;
	}
	public String getFf_caption() {
		return ff_caption;
	}
	public void setFf_caption(String ff_caption) {
		this.ff_caption = ff_caption;
	}
	public String getFf_manidfield() {
		return ff_manidfield;
	}
	public void setFf_manidfield(String ff_manidfield) {
		this.ff_manidfield = ff_manidfield;
	}
	public String getFf_manfield() {
		return ff_manfield;
	}
	public void setFf_manfield(String ff_manfield) {
		this.ff_manfield = ff_manfield;
	}
	public String getFf_mansql() {
		return ff_mansql;
	}
	public void setFf_mansql(String ff_mansql) {
		this.ff_mansql = ff_mansql;
	}
	public String getFf_resultfield() {
		return ff_resultfield;
	}
	public void setFf_resultfield(String ff_resultfield) {
		this.ff_resultfield = ff_resultfield;
	}
	public String getFf_plandatefield() {
		return ff_plandatefield;
	}
	public void setFf_plandatefield(String ff_plandatefield) {
		this.ff_plandatefield = ff_plandatefield;
	}
	public String getFf_dealdatefield() {
		return ff_dealdatefield;
	}
	public void setFf_dealdatefield(String ff_dealdatefield) {
		this.ff_dealdatefield = ff_dealdatefield;
	}	
	public String getFf_remarkfield() {
		return ff_remarkfield;
	}
	public void setFf_remarkfield(String ff_remarkfield) {
		this.ff_remarkfield = ff_remarkfield;
	}
	public String getFf_otherfields() {
		return ff_otherfields;
	}
	public void setFf_otherfields(String ff_otherfields) {
		this.ff_otherfields = ff_otherfields;
	}
	public String getManSql(Object id){
		String sql=this.getFf_mansql();
		if(sql!=null)
		sql=sql.replaceAll("@KEYVALUE", ""+id);
		return sql;
	}
	public String getRemark(Map<Object,Object> data){
		String remark=" 处理结果:";
		if(this.ff_resultfield!=null) {
			try {
				Integer.parseInt(String.valueOf(data.get(this.ff_resultfield)));
				remark+=" 已处理";
			} catch (NumberFormatException e) {
				remark+=data.get(this.ff_resultfield);
			}
		}
		if(this.ff_remarkfield!=null) remark+=" 处理描述:"+(StringUtil.hasText(data.get(this.ff_remarkfield))?data.get(this.ff_remarkfield):"无");
		return remark;
	}

}
