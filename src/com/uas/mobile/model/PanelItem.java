package com.uas.mobile.model;

import com.uas.erp.model.ComboStore;
import com.uas.erp.model.DataListDetail;
import com.uas.erp.model.FormDetail;
import com.uas.erp.model.FormItems;

/**
 * 手机端页面主界面
 * */
public class PanelItem {
	private String field;
	private String caption;
	private String format;
	private String type;
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
    public PanelItem(){
    	
    }
    public PanelItem(FormDetail detail, String language){
    	this.field = detail.getFd_field();
    	if (language==null) language="zh_CN";
    	if (language.equals("en_US")) {
    		 this.caption=detail.getFd_captionen();
    	} else if (language.equals("zh_TW")) {
    		this.caption=detail.getFd_captionfan();
    	} else {
    		this.caption = detail.getFd_caption();
    	}
    	String type =detail.getFd_type();
    	if ("S".equals(type)) {
			this.type = "textfield";
		} else if (type.equals("N")) {
			this.type = "numberfield";
		}else if (type.equals("D")) {
			this.type = "datefield";
		} else if (type.equals("DT")) {
			this.type = "datetimefield";
		} else if (type.equals("B")) {
			this.type = "checkbox";
		}else if (type.equals("R")) {
			this.type = "radio";
		} else if (type.equals("C")) {
			this.type = "combo";	
		} else {
			this.type="textfield";
		}
    }
}
