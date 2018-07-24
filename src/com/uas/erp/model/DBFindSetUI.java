package com.uas.erp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.Saveable;

public class DBFindSetUI implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ds_whichui;
	private String ds_whichdbfind;
	private String ds_likefield;
	private String ds_findtoui;
	private String ds_caption;
	private String ds_uifixedcondition;
	private String ds_dbcaption;
	private String ds_dbwidth;
	private String ds_enid;
	private String ds_caller;
	private String ds_orderby;
	private int ds_allowreset;
	private String ds_tables;
	private String dataString;
	private Integer ds_autoheight;
	private String ds_error;
	private String ds_type;
	private String ds_dlccaller;
	private Integer ds_enablelikes;
    private Integer ds_nolimit=0;//不受数据权限管控
    private Integer ds_isfast = 0;//大数据，放大镜不取count
	public String getDs_type() {
		return ds_type;
	}

	public void setDs_type(String ds_type) {
		this.ds_type = ds_type;
	}

	public String getDs_dlccaller() {
		return ds_dlccaller;
	}

	public void setDs_dlccaller(String ds_dlccaller) {
		this.ds_dlccaller = ds_dlccaller;
	}

	public String getDs_enid() {
		return ds_enid;
	}

	public void setDs_enid(String dsEnid) {
		ds_enid = dsEnid;
	}

	private int ds_id;

	public String getDs_whichui() {
		return ds_whichui;
	}

	public void setDs_whichui(String ds_whichui) {
		this.ds_whichui = ds_whichui;
	}

	public int getDs_allowreset() {
		return ds_allowreset;
	}

	public void setDs_allowreset(int ds_allowreset) {
		this.ds_allowreset = ds_allowreset;
	}

	public Integer getDs_autoheight() {
		return ds_autoheight == null ? 0 : ds_autoheight;
	}

	public void setDs_autoheight(Integer ds_autoheight) {
		this.ds_autoheight = ds_autoheight == null ? 0 : ds_autoheight;
	}

	public String getDs_dbcaption() {
		return ds_dbcaption;
	}

	public void setDs_dbcaption(String ds_dbcaption) {
		this.ds_dbcaption = ds_dbcaption;
	}

	public String getDs_dbwidth() {
		return ds_dbwidth;
	}

	public void setDs_dbwidth(String ds_dbwidth) {
		this.ds_dbwidth = ds_dbwidth;
	}

	public String getDs_whichdbfind() {
		return ds_whichdbfind;
	}

	public void setDs_whichdbfind(String ds_whichdbfind) {
		this.ds_whichdbfind = ds_whichdbfind;
	}

	public String getDs_caller() {
		return ds_caller;
	}

	public void setDs_caller(String ds_caller) {
		this.ds_caller = ds_caller;
	}

	public String getDs_likefield() {
		return ds_likefield;
	}

	public void setDs_likefield(String ds_likefield) {
		this.ds_likefield = ds_likefield;
	}

	public String getDs_orderby() {
		return ds_orderby;
	}

	public void setDs_orderby(String ds_orderby) {
		this.ds_orderby = ds_orderby;
	}

	public String getDs_findtoui() {
		return ds_findtoui;
	}

	public void setDs_findtoui(String ds_findtoui) {
		this.ds_findtoui = ds_findtoui;
	}

	public String getDs_caption() {
		return ds_caption;
	}

	public void setDs_caption(String ds_caption) {
		this.ds_caption = ds_caption;
	}

	public String getDs_uifixedcondition() {
		return ds_uifixedcondition;
	}

	public void setDs_uifixedcondition(String ds_uifixedcondition) {
		this.ds_uifixedcondition = ds_uifixedcondition;
	}

	public String getDataString() {
		return dataString;
	}

	public void setDataString(String dataString) {
		this.dataString = dataString;
	}

	public int getDs_id() {
		return ds_id;
	}

	public void setDs_id(int ds_id) {
		this.ds_id = ds_id;
	}

	public String getDs_tables() {
		return ds_tables;
	}

	public void setDs_tables(String ds_tables) {
		this.ds_tables = ds_tables;
	}
    
	public Integer getDs_nolimit() {
		return ds_nolimit;
	}

	public void setDs_nolimit(Integer ds_nolimit) {
		this.ds_nolimit = ds_nolimit;
	}
   
	public Integer getDs_enablelikes() {
		return ds_enablelikes;
	}

	public void setDs_enablelikes(Integer ds_enablelikes) {
		this.ds_enablelikes = ds_enablelikes;
	}

	@Override
	public String table() {
		return "DbfindSetUI";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "ds_id" };
	}

	public String getSql(String condition) {
		condition = condition.equals("") ? "" : " WHERE " + condition;
		return "SELECT count(1) FROM " + this.ds_whichdbfind + " " + condition;
	}

	public String getSql(String condition, int page, int pageSize) {
		StringBuffer sb = new StringBuffer("SELECT ");
		StringBuffer sb1 = new StringBuffer("SELECT ");
		String[] names = this.ds_findtoui.split("#");
		String field = null;
		String groupBy = (StringUtils.hasText(this.ds_orderby) && this.ds_orderby.startsWith("group by")) ? this.ds_orderby : null;
		for (String name : names) {
			field = name.split(",")[0];
			if (groupBy == null) {
				if (field.contains("(")) {// sum(..),nvl(..)等带函数名的字段
					field = field.substring(field.indexOf("(") + 1, field.indexOf(")"));
					sb.append(" ");
				} else if (field.contains(".")) {
					field = name.split(",")[0].replace(".", "_");
					sb.append(" ");
				}
				sb.append(field);
				sb1.append(field);
			} else {
				sb.append(field);
				if (field.contains(" ")) {
					String[] strs = field.split(" ");
					field = strs[strs.length - 1];
				} else if (field.contains("(")) {// sum(..),nvl(..)等带函数名的字段
					field = field.substring(field.indexOf("(") + 1, field.indexOf(")"));
				} else if (field.contains(".")) {
					field = name.split(",")[0].replace(".", "_");
				}
				sb1.append(field);
			}
			sb.append(",");
			sb1.append(",");
		}
		String str = sb.substring(0, sb.length() - 1);
		String str1 = sb1.substring(0, sb1.length() - 1);
		condition = condition.equals("") ? "" : " WHERE " + condition;
		String orderBy = this.ds_orderby;
		if (!StringUtils.hasText(orderBy) || groupBy != null) {
			orderBy = "order by " + this.ds_likefield + " desc";
		}
		return str1 + " FROM (" + str + ",row_number()over(" + orderBy + ") rn FROM " + this.ds_whichdbfind + " " + condition
				+ (groupBy != null ? groupBy : "") + ")" + "where rn between " + ((page - 1) * pageSize + 1) + " and " + page * pageSize;
		
	}

	public String getDeployData() {
		String FindTout = this.getDs_findtoui();
		String DbCaption = this.getDs_dbcaption();
		String DbWidth = this.getDs_dbwidth();
		String Type = this.getDs_type();// == null ? "" : this.getDs_type();
		List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < FindTout.split("#").length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ds_dbcaption", DbCaption.split("#")[i]==null?"":DbCaption.split("#")[i]);
			map.put("ds_dbwidth", DbWidth.split("#")[i]==null?0:DbWidth.split("#")[i]);
			map.put("ds_findtoui_f", FindTout.split("#")[i].split(",")[0]);
			if (FindTout.split("#")[i].split(",").length > 1) {
				map.put("ds_findtoui_i", FindTout.split("#")[i].split(",")[1]);
			}
			map.put("ds_type", Type ==null ? "S" : Type.split("#")[i]);
			maps.add(map);
		}
		return BaseUtil.parseGridStore2Str(maps);
	}

	public String getDs_error() {
		return ds_error;
	}

	public void setDs_error(String ds_error) {
		this.ds_error = ds_error;
	}
	
	public Integer getDs_isfast() {
		return ds_isfast;
	}

	public void setDs_isfast(Integer ds_isfast) {
		this.ds_isfast = ds_isfast;
	}
}
