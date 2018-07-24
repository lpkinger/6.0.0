package com.uas.mobile.model;

import java.util.List;
import java.util.Map;

public class ListConditions {
	private String type;
	private String caption;
	private String field;
	private Object data;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
  
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public ListConditions(){
    	
    }
    public ListConditions(ListQuerySet set){
    	this.caption=set.getLs_caption();
    	this.field=set.getLs_field().toUpperCase();
        if(set.getLs_type().equals("CD")) 
         this.type="condatefield";
        else if(set.getLs_type().equals("EM"))
         this.type="employeefield";
        else this.type="textfield";
        this.data=set.getDataStr();
    }
}
