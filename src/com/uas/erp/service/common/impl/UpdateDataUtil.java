package com.uas.erp.service.common.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import com.uas.erp.model.Employee;
import com.uas.erp.model.UpdateSchemeData;
import com.uas.erp.model.UpdateSchemeDetail;
import com.uas.erp.model.InitToFormal;

public class UpdateDataUtil {

	public static final String DATE_FIELD = "date";
	public static final String NUMBER_FIELD = "number";

	public final static String REG_YMD = "\\d{4}-\\d{1,2}-\\d{1,2}";
	public final static String REG_YMD_HIS = "\\d{4}-\\d{1,2}-\\d{1,2} \\d{2}:\\d{2}:\\d{2}";
	public final static String REG_YMD_HIS_T = "\\d{4}-\\d{1,2}-\\d{1,2} \\d{2}:\\d{2}:\\d{2}.\\d{1}";

	public final static String REG_YMD_SPRIT = "\\d{4}/\\d{1,2}/\\d{1,2}";
	public final static String REG_MDY_SPRIT = "\\d{1,2}/\\d{1,2}/\\d{2,4}";

	public final static String ymdFormat = "yyyy-mm-dd";
	public final static String ymdhisFormat = "yyyy-mm-dd hh24:mi:ss";
	public final static String ymdspritFormat = "yyyy/mm/dd";
	public final static String mdyspritFormat = "mm/dd/yyyy";

	private List<UpdateSchemeDetail> indexfields;
	private List<UpdateSchemeDetail> updatedetails;
	private List<UpdateSchemeData> datas;
	private List<InitToFormal> formals;
	private String table;
	private String condition;
	private JSONObject currentData;
	private StringBuffer sql_v;

	public UpdateDataUtil() {
	}
	public List<InitToFormal> getFormals() {
		return formals;
	}

	public UpdateDataUtil(List<UpdateSchemeDetail> indexfields,List<UpdateSchemeDetail> updatedetails, List<UpdateSchemeData> datas, Employee employee,String table,String condition) {
		this();
		this.indexfields=indexfields;
		this.updatedetails = updatedetails;
		this.datas = datas;
		this.table=table;
		this.condition=condition;
		this.formals = new ArrayList<InitToFormal>();
		updateData();
	}

	public synchronized void updateData() {
		String field = null;
		Object val = null;
		String value = null;
		int udid = 0;	
		String sql="UPDATE "+this.table+" SET ";
		for (UpdateSchemeData d : this.datas) {
			sql_v = new StringBuffer();
			currentData = JSONObject.fromObject(d.getUd_data());
			udid = d.getUd_id();			
			for (UpdateSchemeDetail t : this.updatedetails) {
				field = t.getField_();
				val = currentData.get(field);
				value = val == null ? null : val.toString();
				sql_v.append(field);
				sql_v.append("=");				
				parseDefault(field,value,t.getType_(),false);
			}
			sql_v=sql_v.deleteCharAt(sql_v.length()-1);  
			sql_v.append(" where ");
			for (UpdateSchemeDetail i : this.indexfields) {
				field = i.getField_();
				val = currentData.get(field);
				value = val == null ? null : val.toString();
				sql_v.append(field);
				sql_v.append("=");				
				parseDefault(field,value,i.getType_(),true);
			}
			if(this.condition!="null" && this.condition.trim().length()!=0) sql_v.append(this.condition+" and ");		
			formals.add(new InitToFormal(udid, sql+sql_v.substring(0, sql_v.length() - 5)));
		}
	}
	private void parseDefault(String field, String value,String type,boolean isIndex) {
			if (value != null && value.trim().length() > 0) {
				if (type.startsWith("DATE")) {
					if (value.matches(REG_YMD)) {
						value = "to_date('" + value + "','" + ymdFormat + "')";
					} else if (value.matches(REG_YMD_HIS)) {
						value = "to_date('" + value + "','" + ymdhisFormat + "')";
					} else if (value.matches(REG_YMD_HIS_T)) {
						value = "to_date('" + value.substring(0, value.lastIndexOf(".")) + "','" + ymdhisFormat
								+ "')";
					} else if (value.matches(REG_YMD_SPRIT)) {
						value = "to_date('" + value + "','" + ymdspritFormat + "')";
					} else if (value.matches(REG_MDY_SPRIT)) {
						value = "to_date('" + value + "','" + mdyspritFormat + "')";
					} else {
						value = "";
					}
					sql_v.append(value);
				} else if (type.startsWith("NUMBER")) {
					sql_v.append("'");
					sql_v.append(value.replaceAll(",", ""));
					sql_v.append("'");
				} else {
					sql_v.append("'");
					sql_v.append(value.replaceAll("'", "''"));// 考虑oracle sql单引号
					sql_v.append("'");
				}
			} else {
				sql_v.append("null");
			}
			if(isIndex)sql_v.append(" and ");
			else sql_v.append(",");
	}

}