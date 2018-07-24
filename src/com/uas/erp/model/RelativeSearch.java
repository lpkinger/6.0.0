package com.uas.erp.model;

import java.io.Serializable;
import java.util.List;

import org.springframework.util.StringUtils;

import com.uas.erp.dao.Saveable;

/**
 * Form维护界面 -- 关联查询
 * 
 * @author yingp
 * 
 */
public class RelativeSearch implements Serializable, Saveable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int rs_id;
	private String rs_caller;
	private String rs_table;
	private String rs_condition;
	private String rs_title;
	private String rs_orderby;
	private String rs_groupby;
	private Integer rs_detno;
	private List<Form> forms;
	private List<Grid> grids;

	public int getRs_id() {
		return rs_id;
	}

	public void setRs_id(int rs_id) {
		this.rs_id = rs_id;
	}

	public String getRs_caller() {
		return rs_caller;
	}

	public void setRs_caller(String rs_caller) {
		this.rs_caller = rs_caller;
	}

	public Integer getRs_detno() {
		return rs_detno;
	}

	public void setRs_detno(Integer rs_detno) {
		this.rs_detno = rs_detno;
	}

	public String getRs_table() {
		return rs_table;
	}

	public void setRs_table(String rs_table) {
		this.rs_table = rs_table;
	}

	public String getRs_title() {
		return rs_title;
	}

	public void setRs_title(String rs_title) {
		this.rs_title = rs_title;
	}

	public String getRs_condition() {
		return rs_condition;
	}

	public void setRs_condition(String rs_condition) {
		this.rs_condition = rs_condition;
	}

	public String getRs_orderby() {
		return rs_orderby;
	}

	public void setRs_orderby(String rs_orderby) {
		this.rs_orderby = rs_orderby;
	}

	public String getRs_groupby() {
		return rs_groupby;
	}

	public void setRs_groupby(String rs_groupby) {
		this.rs_groupby = rs_groupby;
	}

	public List<Form> getForms() {
		return forms;
	}

	public void setForms(List<Form> forms) {
		this.forms = forms;
	}

	public List<Grid> getGrids() {
		return grids;
	}

	public void setGrids(List<Grid> grids) {
		this.grids = grids;
	}

	@Override
	public String table() {
		return "RelativeSearch";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "rs_id" };
	}

	public static String getCountSql(String tabName, String condition) {
		StringBuffer sb = new StringBuffer("SELECT count(1) FROM ").append(tabName);
		if (!"".equals(condition)) {
			sb.append(" WHERE ").append(condition);
		}
		return sb.toString();
	}

	public static String getDataSql(String tabName, String condition, String colFields, String sqlFields, String orderby, String groupby,
			int start, int end) {
		StringBuffer sb = new StringBuffer("SELECT ").append(colFields);
		if (!"".equals(condition)) {
			condition = " WHERE " + condition;
		}
		if (orderby == null || !orderby.toLowerCase().contains("order by")) {
			orderby = "order by rownum";
		}
		if (groupby != null && !groupby.equals("") && groupby.toLowerCase().contains("group")) {
			sb.append(" row_number()over(").append(orderby).append(") rn from ").append(tabName).append(" ").append(condition).append(" ")
					.append(groupby);
		} else {
			sb.append(" FROM (").append("SELECT ").append(sqlFields).append(",row_number()over(").append(orderby).append(") rn FROM ")
					.append(tabName).append(" ").append(condition).append(")where rn between ").append(start).append(" and ").append(end);
		}
		return sb.toString();
	}

	/**
	 * 求合计
	 * 
	 * @param tabName
	 * @param condition
	 * @param colFields
	 * @param sqlFields
	 * @param groupby
	 * @return
	 */
	public static String getSummarySql(String tabName, String condition, String colFields, String sqlFields, String groupby) {
		StringBuffer sql = new StringBuffer("SELECT ").append(colFields).append(" FROM (SELECT ").append(sqlFields).append(" FROM ")
				.append(tabName);
		if (StringUtils.hasText(condition)) {
			sql.append(" WHERE ").append(condition);
		}
		if (StringUtils.hasText(groupby) && groupby.toLowerCase().contains("group")) {
			sql.append(" ").append(groupby);
		}
		return sql.append(")").toString();
	}

	public static class Form implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int rsf_id;
		private int rsf_rsid;
		private String rsf_field;
		private String rsf_caption;
		private String rsf_type;
		private float rsf_width;
		private String rsf_logic;
		private Integer rsf_detno;

		public int getRsf_id() {
			return rsf_id;
		}

		public void setRsf_id(int rsf_id) {
			this.rsf_id = rsf_id;
		}

		public int getRsf_rsid() {
			return rsf_rsid;
		}

		public void setRsf_rsid(int rsf_rsid) {
			this.rsf_rsid = rsf_rsid;
		}

		public String getRsf_field() {
			return rsf_field;
		}

		public void setRsf_field(String rsf_field) {
			this.rsf_field = rsf_field;
		}

		public Integer getRsf_detno() {
			return rsf_detno;
		}

		public void setRsf_detno(Integer rsf_detno) {
			this.rsf_detno = rsf_detno;
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

		public float getRsf_width() {
			return rsf_width;
		}

		public void setRsf_width(float rsf_width) {
			this.rsf_width = rsf_width;
		}

		public String getRsf_logic() {
			return rsf_logic;
		}

		public void setRsf_logic(String rsf_logic) {
			this.rsf_logic = rsf_logic;
		}
	}

	public static class Grid implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int rsg_id;
		private int rsg_rsid;
		private String rsg_field;
		private String rsg_caption;
		private String rsg_type;
		private float rsg_width;
		private Integer rsg_detno;
		private String rsg_url;
		// summaryType
		private String rsg_sumtype;
		private String rsg_combovalue;

		public String getRsg_combovalue() {
			return rsg_combovalue;
		}

		public void setRsg_combovalue(String rsg_combovalue) {
			this.rsg_combovalue = rsg_combovalue;
		}

		public int getRsg_id() {
			return rsg_id;
		}

		public void setRsg_id(int rsg_id) {
			this.rsg_id = rsg_id;
		}

		public int getRsg_rsid() {
			return rsg_rsid;
		}

		public void setRsg_rsid(int rsg_rsid) {
			this.rsg_rsid = rsg_rsid;
		}

		public String getRsg_field() {
			return rsg_field;
		}

		public void setRsg_field(String rsg_field) {
			this.rsg_field = rsg_field;
		}

		public Integer getRsg_detno() {
			return rsg_detno;
		}

		public void setRsg_detno(Integer rsg_detno) {
			this.rsg_detno = rsg_detno;
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

		public float getRsg_width() {
			return rsg_width;
		}

		public void setRsg_width(float rsg_width) {
			this.rsg_width = rsg_width;
		}

		public String getRsg_url() {
			return rsg_url;
		}

		public void setRsg_url(String rsg_url) {
			this.rsg_url = rsg_url;
		}

		public String getRsg_sumtype() {
			return rsg_sumtype;
		}

		public void setRsg_sumtype(String rsg_sumtype) {
			this.rsg_sumtype = rsg_sumtype;
		}
	}
}
