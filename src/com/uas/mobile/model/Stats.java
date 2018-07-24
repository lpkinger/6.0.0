package com.uas.mobile.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.uas.erp.core.BaseUtil;

public class Stats implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int st_id;
	private String st_title;
	private String st_sql;
	private String st_beforeprocess;
	private String st_processargs;
	private String st_keyfield;
	private String st_valuefield;
	private int st_detno;
	private String st_group;
	private int st_type;
	private List<Map<String,Object>> datas;
	private final static Map<Object,Object> argmap=new HashMap<Object, Object>();
	static{
		argmap.put("YEAR","TO_CHAR(SYSDATE,'YYYY')");
		argmap.put("QUARTER","TO_CHAR(SYSDATE,'Q')");
		argmap.put("MONTH","TO_CHAR(SYSDATE,'MM')");
		argmap.put("WEEK", "TO_CHAR(SYSDATE,'IW')");
		argmap.put("DAY", "TO_CHAR(SYSDATE,'YYYY-MM-DD')");
		
		
	}
	public int getSt_id() {
		return st_id;
	}
	public void setSt_id(int st_id) {
		this.st_id = st_id;
	}
	public String getSt_title() {
		return st_title;
	}
	public void setSt_title(String st_title) {
		this.st_title = st_title;
	}
	public String getSt_sql() {
		return st_sql;
	}
	public void setSt_sql(String st_sql) {
		this.st_sql = st_sql;
	}
	public String getSt_beforeprocess() {
		return st_beforeprocess;
	}
	public void setSt_beforeprocess(String st_beforeprocess) {
		this.st_beforeprocess = st_beforeprocess;
	}
	public String getSt_processargs() {
		return st_processargs;
	}
	public void setSt_processargs(String st_processargs) {
		this.st_processargs = st_processargs;
	}
	public String getSt_keyfield() {
		return st_keyfield;
	}
	public void setSt_keyfield(String st_keyfield) {
		this.st_keyfield = st_keyfield;
	}
	public String getSt_valuefield() {
		return st_valuefield;
	}
	public void setSt_valuefield(String st_valuefield) {
		this.st_valuefield = st_valuefield;
	}
	public int getSt_detno() {
		return st_detno;
	}
	public void setSt_detno(int st_detno) {
		this.st_detno = st_detno;
	}
	public String getSt_group() {
		return st_group;
	}
	public void setSt_group(String st_group) {
		this.st_group = st_group;
	}
	public List<Map<String, Object>> getDatas() {
		return datas;
	}
	public void setDatas(List<Map<String, Object>> datas) {
		this.datas = datas;
	}	
	public int getSt_type() {
		return st_type;
	}
	public void setSt_type(int st_type) {
		this.st_type = st_type;
	}
	public  String getQuerySql(String config){
		Map<Object,Object> map=config!=null?BaseUtil.parseFormStoreToMap(config):argmap; 
    	for(Object key:map.keySet()){
    		if(map.get(key)!=null){
    		   this.st_sql=this.st_sql.replaceAll("@"+key+"@",String.valueOf(map.get(key)));
    		}
    	}
    	return this.st_sql;
    }
}
