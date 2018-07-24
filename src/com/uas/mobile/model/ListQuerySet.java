package com.uas.mobile.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.JacksonUtil;
import com.uas.erp.core.StringUtil;

public class ListQuerySet {
	private String ls_caller;
	private String ls_field;
	private String ls_type;
	private String ls_datasource;
	private String ls_caption;
	private int ls_fixedvalue;
	private Object dataStr;
	public String getLs_caller() {
		return ls_caller;
	}
	public void setLs_caller(String ls_caller) {
		this.ls_caller = ls_caller;
	}
	public String getLs_field() {
		return ls_field;
	}
	public void setLs_field(String ls_field) {
		this.ls_field = ls_field;
	}
	public String getLs_type() {
		return ls_type;
	}
	public void setLs_type(String ls_type) {
		this.ls_type = ls_type;
	}
	public String getLs_datasource() {
		return ls_datasource;
	}
	public void setLs_datasource(String ls_datasource) {
		this.ls_datasource = ls_datasource;
	}
	public String getLs_caption() {
		return ls_caption;
	}
	public void setLs_caption(String ls_caption) {
		this.ls_caption = ls_caption;
	}
	public int getLs_fixedvalue() {
		return ls_fixedvalue;
	}
	public void setLs_fixedvalue(int ls_fixedvalue) {
		this.ls_fixedvalue = ls_fixedvalue;
	}
	
	
	public Object getDataStr() {
		return dataStr;
	}
	public void setDataStr(Object dataStr) {
		this.dataStr = dataStr;
	}
	public ListQuerySet(){

	} 
	protected boolean CheckQuery(){
		if(this.ls_fixedvalue==0){
			
		}
		return false;
	}
	public String getQuerySql(){
		String Str=null;	
		String[]args=null;
		if(this.ls_datasource!=null) 
			args=this.ls_datasource.split("\\|");
		switch (args.length) {
		case 3:
            Str=this.ls_type.equals("EM")?"SELECT "+args[1] +" FROM " +args[0]+" WHERE " +args[2]:"SELECT "+args[1] +" AS "+this.ls_field+" FROM " +args[0]+" WHERE " +args[2];
			break;
		case 2:
			Str=this.ls_type.equals("EM")? "SELECT "+args[1] +" FROM " +args[0] :"SELECT "+args[1] +" AS "+this.ls_field+" FROM " +args[0];
			break;
		default:
			break;
		}
		return  Str;
	}
	public JSONObject FormatEmData(List<Map<String,Object>> maps){
		Map<Object, List<Map<String, Object>>> set = new HashMap<Object, List<Map<String, Object>>>();
	    JSONObject obj=new JSONObject();
		List<Map<String, Object>> list = null;
		for (Map<String, Object> map : maps) {
			Object key = map.get("EM_DEFAULTORNAME");
			if (StringUtil.hasText(key) && set.containsKey(key)) {
				list = set.get(key);
			} else {
				list = new ArrayList<Map<String, Object>>();
			}
			map.put(this.ls_field.toUpperCase(),map.get("EM_NAME") );
			list.add(map);
			obj.put(key, list);
		}
		return obj;
	}

	public JSONArray FormatFixedData(String data){
		StringBuffer sb=new StringBuffer();
	    sb.append("[");
	    if(data!=null){
	    	String []str=data.split(",");
	    	for(String s:str){
	    		sb.append("{"+"\""+this.ls_field.toUpperCase()+"\":\""+s+"\"},");
	    	}
	    }else return new JSONArray();
        data=sb.toString();
		return JSONArray.fromObject(data.substring(0, data.lastIndexOf(","))+"]");
	}
}
