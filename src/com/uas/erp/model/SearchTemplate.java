package com.uas.erp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.alibaba.fastjson.annotation.JSONField;
import com.uas.erp.dao.Saveable;

/**
 * 新查询界面 模板定义
 * 
 * @author yingp
 * 
 */
public class SearchTemplate implements Serializable, Saveable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String table() {
		return "SearchTemplate";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "st_id" };
	}

	private int st_id;
	private String st_caller;
	private String st_title;
	private String st_condition;
	private Integer st_detno;
	private String st_man;
	private Date st_date;
	private String st_usedtable;// 使用到的表，用逗号隔开
	private String st_tablesql;// 使用的表，按关联条件封装的sql
	private String st_sorts;// 排序
	private String st_limits;// 权限约束
	private List<Grid> items;
	private List<Property> properties;
	private List<DataRelation> relations;
	// 查询前钩子
	// java:beanName.methodName(:ARG_1,CONST_ARG_2,:ARG_3)、procedure:procedureName(:ARG_1,CONST_ARG_2,:ARG_3)
	private String pre_hook;
	private int st_appuse;

	public int getSt_id() {
		return st_id;
	}

	public void setSt_id(int st_id) {
		this.st_id = st_id;
	}

	public String getSt_caller() {
		return st_caller;
	}

	public void setSt_caller(String st_caller) {
		this.st_caller = st_caller;
	}

	public String getSt_title() {
		return st_title;
	}

	public void setSt_title(String st_title) {
		this.st_title = st_title;
	}

	public String getSt_condition() {
		return st_condition;
	}

	public void setSt_condition(String st_condition) {
		this.st_condition = st_condition;
	}

	public Integer getSt_detno() {
		return st_detno;
	}

	public void setSt_detno(Integer st_detno) {
		this.st_detno = st_detno;
	}

	public int getSt_appuse() {
		return st_appuse;
	}

	public void setSt_appuse(int st_appuse) {
		this.st_appuse = st_appuse;
	}

	public String getSt_man() {
		return st_man;
	}

	public void setSt_man(String st_man) {
		this.st_man = st_man;
	}

	public Date getSt_date() {
		return st_date;
	}

	public void setSt_date(Date st_date) {
		this.st_date = st_date;
	}

	public String getSt_usedtable() {
		return st_usedtable;
	}

	public void setSt_usedtable(String st_usedtable) {
		this.st_usedtable = st_usedtable;
	}

	public String getSt_tablesql() {
		return st_tablesql;
	}

	public void setSt_tablesql(String st_tablesql) {
		this.st_tablesql = st_tablesql;
	}

	public String getSt_limits() {
		return st_limits;
	}

	public void setSt_limits(String st_limits) {
		this.st_limits = st_limits;
	}

	public List<Grid> getItems() {
		return items;
	}

	public void setItems(List<Grid> items) {
		this.items = items;
	}

	public List<DataRelation> getRelations() {
		return relations;
	}

	public void setRelations(List<DataRelation> relations) {
		this.relations = relations;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public String getPre_hook() {
		return pre_hook;
	}

	public void setPre_hook(String pre_hook) {
		this.pre_hook = pre_hook;
	}

	private List<Property> getPropertiesByField(String field) {
		if (this.properties != null) {
			List<Property> props = new ArrayList<SearchTemplate.Property>();
			for (Property prop : properties) {
				if (prop.getStg_field().equals(field) && !prop.getDisplay().equals(prop.getValue())) {
					props.add(prop);
				}
			}
			return props;
		}
		return null;
	}

	public String getSt_sorts() {
		return st_sorts;
	}

	public void setSt_sorts(String st_sorts) {
		this.st_sorts = st_sorts;
	}

	/**
	 * 封装查询sql
	 * 
	 * <dl>
	 * <dt>字段别名规则</dt>
	 * <dd>COL_{stg_detno}</dd>
	 * </dl>
	 * 
	 * @param condition
	 * @param sorts
	 * @param start
	 * @param end
	 * @return
	 */
	public String getSql(String condition, String sorts, int start, int end) {
		StringBuffer sb = new StringBuffer("SELECT * FROM (SELECT A.*, ROWNUM RN FROM (SELECT ");
		Set<String> cols = new HashSet<String>();
		List<Property> props = null;
		for (Grid grid : items) {
			String key = grid.getStg_table() + "." + grid.getStg_field();
			if (grid.getStg_formula() != null)
				key = grid.getStg_formula();
			if (!cols.contains(key)) {
				if (!cols.isEmpty()) {
					sb.append(",");
				}
				if ("date".equals(grid.getStg_type().toLowerCase())) {
					sb.append("to_char(");
				}
				if (grid.getStg_field().startsWith("YM_VIEW_PARAM")) {
					sb.append(grid.getStg_table()).append(".");
					sb.append(grid.getStg_field().substring(grid.getStg_field().indexOf("$") + 1));
				} else {
					if (grid.getStg_mode() != null) {
						props = getPropertiesByField(grid.getStg_field());
						if (props != null && props.size() > 0) {
							sb.append("case ");
							for (Property prop : props) {
								sb.append("when ").append(key).append("='").append(prop.getValue()).append("' then '")
										.append(prop.getDisplay()).append("' ");
							}
							sb.append("else to_char(").append(key).append(") end");
						} else {
							sb.append(key);
						}
					} else {
						sb.append(key);
					}
				}
				if ("date".equals(grid.getStg_type().toLowerCase())) {
					sb.append(",'yyyy-mm-dd hh24:mi:ss')");
				}
				sb.append(" ").append(grid.getStg_alias());
				cols.add(key);
			}
			if (grid.getStg_link() != null) {
				if (grid.getStg_tokencol1() != null) {
					key = grid.getStg_tokentab1() + "." + grid.getStg_tokencol1();
					if (!cols.contains(grid.getStg_tokencol1())) {
						// 这里由于链接使用时必须用原字段名，所以，即使key重复也无所谓
						sb.append(",").append(key).append(" ").append(grid.getStg_tokencol1());
						cols.add(grid.getStg_tokencol1());
					}
				}
				if (grid.getStg_tokencol2() != null) {
					key = grid.getStg_tokentab2() + "." + grid.getStg_tokencol2();
					if (!cols.contains(grid.getStg_tokencol2())) {
						sb.append(",").append(key).append(" ").append(grid.getStg_tokencol2());
						cols.add(grid.getStg_tokencol2());
					}
				}
			}
		}
		sb.append(" FROM ").append(this.st_tablesql);
		if (condition != null && condition.length() > 0)
			sb.append(" where ").append(condition);
		if (sorts != null && sorts.length() > 0)
			sb.append(" order by ").append(sorts);
		sb.append(") A WHERE ROWNUM <= ");
		sb.append(end);
		sb.append(") WHERE RN >= ");
		sb.append(start);
		return sb.toString();
	}

	public static class Grid implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String stg_field;
		private String stg_text;
		private String stg_operator;// 运算符
		private String stg_value;
		private Integer stg_use = 1;// 是否显示
		private Integer stg_query = 0;// 用于查询
		private Integer stg_lock = 0;// 是否固定列
		private Integer stg_group = 0;// 是否分组
		private Integer stg_sum = 0;// 合计
		private int stg_stid;
		private int stg_detno;
		private Integer stg_dbfind = 0;// Dbfind字段
		private Integer stg_double = 0;// 多字段
		private String stg_type;// 字段类型
		private String stg_table;// 表名
		private Double stg_width;// 列宽
		private String stg_format;// 格式转换
		private String stg_mode;// 多选模式
		private Integer stg_appcondition;
		private Integer stg_appuse;
		// 链接
		private String stg_link;
		private String stg_tokentab1;
		private String stg_tokencol1;
		private String stg_tokentab2;
		private String stg_tokencol2;

		private String stg_formula;// 自定义公式
		private Integer stg_id;

		private List<DataDictionaryDetail.Link> links;

		public String getStg_field() {
			return stg_field;
		}

		public void setStg_field(String stg_field) {
			this.stg_field = stg_field;
		}

		public String getStg_alias() {
			return "COL_" + stg_detno;
		}

		public String getStg_text() {
			return stg_text;
		}

		public void setStg_text(String stg_text) {
			this.stg_text = stg_text;
		}

		public Integer getStg_use() {
			return stg_use;
		}

		public void setStg_appuse(Integer stg_appuse) {
			this.stg_appuse = stg_appuse;
		}

		public Integer getStg_appuse() {
			return stg_appuse;
		}

		public Integer getStg_appcondition() {
			return stg_appcondition;
		}

		public void setStg_appcondition(Integer stg_appcondition) {
			this.stg_appcondition = stg_appcondition;
		}

		public String getStg_operator() {
			return stg_operator;
		}

		public void setStg_operator(String stg_operator) {
			this.stg_operator = stg_operator;
		}

		public String getStg_value() {
			return stg_value;
		}

		public void setStg_value(String stg_value) {
			this.stg_value = stg_value;
		}

		public Integer getStg_query() {
			return stg_query;
		}

		public void setStg_query(Integer stg_query) {
			this.stg_query = stg_query;
		}

		public Integer getStg_lock() {
			return stg_lock;
		}

		public void setStg_lock(Integer stg_lock) {
			this.stg_lock = stg_lock;
		}

		public Integer getStg_group() {
			return stg_group;
		}

		public void setStg_group(Integer stg_group) {
			this.stg_group = stg_group;
		}

		public int getStg_stid() {
			return stg_stid;
		}

		public void setStg_stid(int stg_stid) {
			this.stg_stid = stg_stid;
		}

		public int getStg_detno() {
			return stg_detno;
		}

		public void setStg_detno(int stg_detno) {
			this.stg_detno = stg_detno;
		}

		public Integer getStg_sum() {
			return stg_sum;
		}

		public void setStg_sum(Integer stg_sum) {
			this.stg_sum = stg_sum;
		}

		public Integer getStg_dbfind() {
			return stg_dbfind;
		}

		public void setStg_dbfind(Integer stg_dbfind) {
			this.stg_dbfind = stg_dbfind;
		}

		public Integer getStg_double() {
			return stg_double;
		}

		public void setStg_double(Integer stg_double) {
			this.stg_double = stg_double;
		}

		public String getStg_type() {
			return stg_type;
		}

		public void setStg_type(String stg_type) {
			this.stg_type = stg_type;
		}

		public String getStg_table() {
			return stg_table;
		}

		public void setStg_table(String stg_table) {
			this.stg_table = stg_table;
		}

		public Double getStg_width() {
			return stg_width;
		}

		public void setStg_width(Double stg_width) {
			this.stg_width = stg_width;
		}

		public String getStg_format() {
			return stg_format;
		}

		public void setStg_format(String stg_format) {
			this.stg_format = stg_format;
		}

		public String getStg_mode() {
			return stg_mode;
		}

		public void setStg_mode(String stg_mode) {
			this.stg_mode = stg_mode;
		}

		public String getStg_link() {
			return stg_link;
		}

		public void setStg_link(String stg_link) {
			this.stg_link = stg_link;
		}

		public String getStg_tokentab1() {
			return stg_tokentab1;
		}

		public void setStg_tokentab1(String stg_tokentab1) {
			this.stg_tokentab1 = stg_tokentab1;
		}

		public String getStg_tokencol1() {
			return stg_tokencol1;
		}

		public void setStg_tokencol1(String stg_tokencol1) {
			this.stg_tokencol1 = stg_tokencol1;
		}

		public String getStg_tokentab2() {
			return stg_tokentab2;
		}

		public void setStg_tokentab2(String stg_tokentab2) {
			this.stg_tokentab2 = stg_tokentab2;
		}

		public String getStg_tokencol2() {
			return stg_tokencol2;
		}

		public void setStg_tokencol2(String stg_tokencol2) {
			this.stg_tokencol2 = stg_tokencol2;
		}

		public String getStg_formula() {
			return stg_formula;
		}

		public void setStg_formula(String stg_formula) {
			this.stg_formula = stg_formula;
		}

		public List<DataDictionaryDetail.Link> getLinks() {
			return links;
		}

		public void setLinks(List<DataDictionaryDetail.Link> links) {
			this.links = links;
		}

		@JsonIgnore
		@JSONField(serialize = false)
		public Integer getStg_id() {
			return stg_id;
		}

		public void setStg_id(Integer stg_id) {
			this.stg_id = stg_id;
		}
	}

	public static class Property implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String stg_field;

		private int num;

		private String display;

		private String value;

		private Integer st_id;

		public String getStg_field() {
			return stg_field;
		}

		public void setStg_field(String stg_field) {
			this.stg_field = stg_field;
		}

		public int getNum() {
			return num;
		}

		public void setNum(int num) {
			this.num = num;
		}

		public String getDisplay() {
			return display;
		}

		public void setDisplay(String display) {
			this.display = display;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@JSONField(serialize = false)
		@JsonIgnore
		public Integer getSt_id() {
			return st_id;
		}

		public void setSt_id(Integer st_id) {
			this.st_id = st_id;
		}

	}

}
