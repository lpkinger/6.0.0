package com.uas.erp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.ContextUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.Saveable;

/**
 * 
 *
 */
public class DataList implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int dl_id;
	private String dl_caller;
	private String dl_tablename;
	private String dl_title;
	private String dl_relative;// 关联列表
	private String dl_lockpage;
	private String dl_pfcaption;
	private String dl_fixedcondition;
	private String dl_condition;
	private String dl_search;
	private String dl_recordfield;
	private String dl_groupby;
	private Float dl_total;
	private String dl_orderby;
	private String dl_distinct;
	private String dl_keyfield;
	private String dl_statusfield;
	private Integer dl_pagesize;
	private String dl_enid;
	private String dl_pffield;
	private Integer dl_fixedcols;
	private String dl_entryfield;// 录入人字段
	private String dataString;// 字符串格式的数据
	private String master;
	private boolean personality = false;

	public String getDl_enid() {
		return dl_enid;
	}

	public void setDl_enid(String dl_enid) {
		this.dl_enid = dl_enid;
	}

	public int getDl_id() {
		return dl_id;
	}

	public void setDl_id(int dl_id) {
		this.dl_id = dl_id;
	}

	public Integer getDl_pagesize() {
		return dl_pagesize;
	}

	public void setDl_pagesize(Integer dl_pagesize) {
		this.dl_pagesize = dl_pagesize;
	}

	public String getDl_keyfield() {
		return dl_keyfield;
	}

	public void setDl_keyfield(String dl_keyfield) {
		this.dl_keyfield = dl_keyfield;
	}

	public String getDl_caller() {
		return dl_caller;
	}

	public void setDl_caller(String dl_caller) {
		this.dl_caller = dl_caller;
	}

	public String getDl_pffield() {
		return dl_pffield;
	}

	public void setDl_pffield(String dlPffield) {
		dl_pffield = dlPffield;
	}

	public String getDl_condition() {
		return dl_condition;
	}

	public void setDl_condition(String dl_condition) {
		this.dl_condition = dl_condition;
	}

	public String getDl_tablename() {
		return dl_tablename;
	}

	public void setDl_tablename(String dl_tablename) {
		this.dl_tablename = dl_tablename;
	}

	public String getDl_entryfield() {
		return dl_entryfield;
	}

	public void setDl_entryfield(String dl_entryfield) {
		this.dl_entryfield = dl_entryfield;
	}

	public String getDl_statusfield() {
		return dl_statusfield;
	}

	public void setDl_statusfield(String dl_statusfield) {
		this.dl_statusfield = dl_statusfield;
	}

	public String getDl_title() {
		return dl_title;
	}

	public void setDl_title(String dl_title) {
		this.dl_title = dl_title;
	}

	public String getDl_relative() {
		return dl_relative;
	}

	public void setDl_relative(String dl_relative) {
		this.dl_relative = dl_relative;
	}

	public String getDl_lockpage() {
		return dl_lockpage;
	}

	public void setDl_lockpage(String dl_lockpage) {
		this.dl_lockpage = dl_lockpage;
	}

	public String getDl_pfcaption() {
		return dl_pfcaption;
	}

	public void setDl_pfcaption(String dl_pfcaption) {
		this.dl_pfcaption = dl_pfcaption;
	}

	public String getDl_fixedcondition() {
		return dl_fixedcondition;
	}

	public void setDl_fixedcondition(String dl_fixedcondition) {
		this.dl_fixedcondition = dl_fixedcondition;
	}

	public String getDl_search() {
		return dl_search;
	}

	public void setDl_search(String dl_search) {
		this.dl_search = dl_search;
	}

	public String getDl_recordfield() {
		return dl_recordfield;
	}

	public void setDl_recordfield(String dl_recordfield) {
		this.dl_recordfield = dl_recordfield;
	}

	public String getDataString() {
		return dataString;
	}

	public void setDataString(String dataString) {
		this.dataString = dataString;
	}

	public String getDl_groupby() {
		return dl_groupby;
	}

	public void setDl_groupby(String dl_groupby) {
		this.dl_groupby = dl_groupby;
	}

	public Float getDl_total() {
		return dl_total;
	}

	public void setDl_total(Float dl_total) {
		this.dl_total = dl_total;
	}

	public String getDl_orderby() {
		return dl_orderby;
	}

	public void setDl_orderby(String dl_orderby) {
		this.dl_orderby = dl_orderby;
	}

	public String getDl_distinct() {
		return dl_distinct;
	}

	public void setDl_distinct(String dl_distinct) {
		this.dl_distinct = dl_distinct;
	}

	public Integer getDl_fixedcols() {
		return dl_fixedcols;
	}

	public void setDl_fixedcols(Integer dl_fixedcols) {
		this.dl_fixedcols = dl_fixedcols;
	}

	public boolean isPersonality() {
		return personality;
	}

	public void setPersonality(boolean personality) {
		this.personality = personality;
	}

	@Override
	public String table() {
		return "datalist";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "dl_id" };
	}

	private List<DataListDetail> dataListDetails;

	public List<DataListDetail> getDataListDetails() {
		return dataListDetails;
	}

	public void setDataListDetails(List<DataListDetail> dataListDetails) {
		this.dataListDetails = dataListDetails;
	}

	/**
	 * DataList取数据SQL
	 * 
	 * @param condition
	 *            附加的条件
	 * @see getFasterSql
	 */
	@Deprecated
	public String getSql(String condition, String orderby, int page, int pageSize) {
		StringBuffer sb = new StringBuffer("SELECT ");
		if (master != null) {// 帐套信息
			sb.append("'").append(master).append("'").append(" CURRENTMASTER,");
		}
		for (DataListDetail detail : this.dataListDetails) {
			sb.append(detail.getDld_field());
			sb.append(",");
		}
		String groupby = this.dl_groupby;
		String str = sb.substring(0, sb.length() - 1);
		condition = "".equals(condition) ? "" : " WHERE " + condition;
		if (orderby == null || orderby.equals("")) {
			orderby = "order by " + this.dl_keyfield + " desc";
		} else if (orderby.startsWith("group by")) {
			groupby = orderby;
			orderby = "order by " + this.dl_keyfield + " desc";
		}
		sb = new StringBuffer("select * from(");
		sb.append(str);
		sb.append(",row_number()over(");
		sb.append(orderby);
		sb.append(") rn FROM ");
		sb.append(this.dl_tablename);
		sb.append(" ");
		sb.append(condition);
		if (groupby != null && !groupby.equals("") && groupby.contains("group")) {
			sb.append(" ").append(groupby);
		}
		sb.append(")where rn between ");
		sb.append(((page - 1) * pageSize + 1));
		sb.append(" and ");
		sb.append(page * pageSize);
		return sb.toString();
	}

	/**
	 * DataList取数据SQL(集团版)
	 * 
	 * @param condition
	 * @param orderby
	 * @param page
	 * @param pageSize
	 * @return
	 * @see getFasterSql
	 */
	@Deprecated
	public String getSql(String condition, String orderby, Employee employee, int page, int pageSize) {
		Master master = employee.getCurrentMaster();
		if (master == null || master.getMa_type() == 3 || master.getMa_soncode() == null) {
			return getSql(condition, orderby, page, pageSize);
		}
		// 集团中心,取资料中心数据
		if (master.getMa_type() == 0
				&& ("Product".equals(this.dl_caller) || "Vendor".equals(this.dl_caller) || "Customer".equals(this.dl_caller) || "Customer!Base"
						.equals(this.dl_caller))) {
			String tabName = this.dl_tablename;
			String dataSob = BaseUtil.getXmlSetting("dataSob");
			this.dl_tablename = getFullTableName(this.dl_tablename, dataSob);
			this.master = dataSob;
			String sql = getSql(condition, orderby, page, pageSize);
			this.dl_tablename = tabName;
			return sql;
		} else {
			// 集团数据
			return getGroupSql(condition, orderby, employee, page, pageSize);
		}
	}

	private String getFieldsSql() {
		StringBuffer sb = new StringBuffer();
		for (DataListDetail detail : this.dataListDetails) {
			sb.append(detail.getDld_field());
			sb.append(",");
		}
		return sb.substring(0, sb.length() - 1);
	}

	/**
	 * DataList取数据SQL，快速取数
	 * 
	 * @param condition
	 * @param orderby
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public String getFasterSql(String condition, String orderby, int page, int pageSize) {
		StringBuffer longFieldsSb = new StringBuffer("");
		if (master != null) {// 帐套信息
			longFieldsSb.append("'").append(master).append("'").append(" CURRENTMASTER,");
		}
		for (DataListDetail detail : this.dataListDetails) {
			longFieldsSb.append(detail.getDld_field());
			longFieldsSb.append(",");
		}
		String longFieldsStr = longFieldsSb.substring(0, longFieldsSb.length() - 1);
		condition = "".equals(condition) ? "" : " WHERE " + condition;
		if (this.dl_groupby != null && this.dl_groupby.length() > 0)
			orderby = this.dl_groupby;
		if ((orderby == null || orderby.equals("")) && this.dl_keyfield != null && this.dl_keyfield.indexOf("@") < 0) {
			orderby = "order by " + this.dl_keyfield + " desc";
		}
		orderby = orderby == null ? " " : orderby;
		int start = ((page - 1) * pageSize + 1);
		int end = page * pageSize;
		StringBuffer sb = new StringBuffer("select * from (select TT.*, ROWNUM rn from (select ");
		sb.append(longFieldsStr);
		sb.append(" from ");
		sb.append(this.dl_tablename);
		sb.append(" ");
		sb.append(condition);
		sb.append(" ");
		sb.append(orderby);
		sb.append(" )TT where ROWNUM <= ");
		sb.append(end);
		sb.append(") where rn >= ");
		sb.append(start);
		return sb.toString();
	}

	/**
	 * DataList取数据SQL，快速取数(集团版)
	 * 
	 * @param condition
	 * @param orderby
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public String getFasterSql(String condition, String orderby, Employee employee, int page, int pageSize) {
		Master master = employee.getCurrentMaster();
		if (master == null || master.getMa_type() == 3 || master.getMa_soncode() == null) {
			return getFasterSql(condition, orderby, page, pageSize);
		}
		// 集团中心,取资料中心数据
		if (master.getMa_type() == 0
				&& ("Product".equals(this.dl_caller) || "Vendor".equals(this.dl_caller) || "Customer".equals(this.dl_caller) || "Customer!Base"
						.equals(this.dl_caller))) {
			String tabName = this.dl_tablename;
			this.dl_tablename = getFullTableName(this.dl_tablename, BaseUtil.getXmlSetting("dataSob"));
			String sql = getFasterSql(condition, orderby, page, pageSize);
			this.dl_tablename = tabName;
			return sql;
		}
		return getGroupSql(condition, orderby, employee, page, pageSize);
	}

	/**
	 * DataList取数据SQL，支持别名字段的筛选(集团版)
	 * 
	 * @param condition
	 * @param orderby
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public String getSearchSql(String condition, String orderby, Employee employee, int page, int pageSize) {
		Master master = employee.getCurrentMaster();
		if (master == null || master.getMa_type() == 3 || master.getMa_soncode() == null) {
			return getSearchSql(condition, orderby, page, pageSize);
		}
		// 集团中心,取资料中心数据
		if (master.getMa_type() == 0
				&& ("Product".equals(this.dl_caller) || "Vendor".equals(this.dl_caller) || "Customer".equals(this.dl_caller) || "Customer!Base"
						.equals(this.dl_caller))) {
			String tabName = this.dl_tablename;
			this.dl_tablename = getFullTableName(this.dl_tablename, BaseUtil.getXmlSetting("dataSob"));
			String sql = getSearchSql(condition, orderby, page, pageSize);
			this.dl_tablename = tabName;
			return sql;
		}
		return getGroupSql(condition, orderby, employee, page, pageSize);
	}

	/**
	 * DataList取数据SQL，支持别名字段的筛选
	 * 
	 * @param condition
	 * @param orderby
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public String getSearchSql(String condition, String orderby, int page, int pageSize) {
		StringBuffer aliasStr = new StringBuffer();
		StringBuffer fieldStr = new StringBuffer();
		if (master != null) {// 帐套信息
			fieldStr.append("'").append(master).append("'").append(" CURRENTMASTER");
		}
		BaseDao baseDao = (BaseDao) ContextUtil.getBean("baseDao");
		boolean bool=baseDao.isDBSetting("newDatalistSql");
		condition = "".equals(condition) ? "" : " WHERE " + condition;
		if (this.dl_groupby != null && this.dl_groupby.length() > 0) {
			orderby=orderby!=null?orderby:"";
			orderby = this.dl_groupby+" "+ orderby;
		}
		else if ((orderby == null || orderby.equals("")) && this.dl_keyfield != null && this.dl_keyfield.indexOf("@") < 0) {
			orderby = "order by " + this.dl_keyfield + " desc";
		}
		else if (orderby != null && !orderby.equals("") && this.dl_keyfield != null && this.dl_keyfield.indexOf("@") < 0 && !orderby.equals(this.getDl_orderby()) ) {
			orderby += ", " + this.dl_keyfield + " desc";
		}	
		orderby = orderby == null ? " " : orderby;
		if(!bool){//原方式
			for (DataListDetail detail : this.dataListDetails) {
				if (fieldStr.length() > 0)
					fieldStr.append(",");
				if (detail.getDld_field().contains(" ")) {
					aliasStr.append(",").append(detail.getDld_field());
					String[] strs = detail.getDld_field().split(" ");
					fieldStr.append(strs[strs.length - 1]);
				} else
					fieldStr.append(detail.getDld_field());
			}			
			StringBuffer aliasSql = new StringBuffer("select tab.*");
			aliasSql.append(aliasStr).append(" from (select * from ").append(this.dl_tablename).append(") tab");
			int start = ((page - 1) * pageSize + 1);
			int end = page * pageSize;
			StringBuffer sb = new StringBuffer("select * from (select TT.*, ROWNUM rn from (select ");
			sb.append(fieldStr);
			sb.append(" from ");
			sb.append("(").append(aliasSql).append(")");
			sb.append(" ");
			sb.append(condition);
			sb.append(" ");
			sb.append(orderby);
			sb.append(" )TT where ROWNUM <= ");
			sb.append(end);
			sb.append(") where rn >= ");
			sb.append(start);
			return sb.toString();
		}else{//新方式 
			String patternstr="";
			for (DataListDetail detail : this.dataListDetails) {
				if (fieldStr.length() > 0)
					fieldStr.append(",");
				String fieldString=detail.getDld_field().trim();
				if (fieldString.contains(" ")) {
					String aliasfield=fieldString.substring( fieldString.lastIndexOf(" ")).trim();
					String repString=fieldString.substring(0, fieldString.lastIndexOf(" ")).trim();
					patternstr="(\\s|\\(|\\)|>|=|<|\\+|\\-|\\*|\\/)("+aliasfield+")(\\s|\\(|\\)|>|=|<|\\+|\\-|\\*|\\/)";
					Pattern pattern=Pattern.compile(patternstr,Pattern.CASE_INSENSITIVE);
					while(condition.matches(".*(\\s|\\(|\\)|>|=|<|\\+|\\-|\\*|\\/)((?i)"+aliasfield+")(\\s|\\(|\\)|>|=|<|\\+|\\-|\\*|\\/)[^a-z].*")){
						Matcher matcher=pattern.matcher(condition);
						if(matcher.find())
						{   
						    String str2=condition.substring(0,matcher.start()+1);
						    String str3=condition.substring(matcher.end()-1,condition.length());
						    condition=str2+" ("+repString+") "+str3;
						}
					}		
				}
				fieldStr.append(detail.getDld_field());
			}
			int start = ((page - 1) * pageSize + 1);
			int end = page * pageSize;
			StringBuffer sb = new StringBuffer("select * from (select TT.*, ROWNUM rn from (select ");
			sb.append(fieldStr);
			sb.append(" from ");
			sb.append(this.dl_tablename);
			sb.append(" ");
			sb.append(condition);
			sb.append(" ");
			sb.append(orderby);
			sb.append(" )TT where ROWNUM <= ");
			sb.append(end);
			sb.append(") where rn >= ");
			sb.append(start);
			return sb.toString();
		}		
		
	}

	/**
	 * 高级查询时，DataList数据Count，支持别名字段的筛选
	 * 
	 * @param condition
	 *            附加的条件
	 */
	public String getSearchSql(String condition) {
		String con="";
		if (StringUtils.hasText(this.dl_groupby)) {
			return getSql(condition);
		}
		BaseDao baseDao = (BaseDao) ContextUtil.getBean("baseDao");
		boolean bool=baseDao.isDBSetting("newDatalistSql");
		StringBuffer fieldStr = new StringBuffer();
		if(!bool){
			for (DataListDetail detail : dataListDetails) {
				if (detail.getDld_field().contains(" ")) {// 有别名的字段
					fieldStr.append(",").append(detail.getDld_field());
				}
			}
			String sql = "SELECT * FROM " + this.dl_tablename;
			if (StringUtils.hasText(this.dl_condition))
				sql += " WHERE " + this.dl_condition;
			sql = "select count(1) from (SELECT tab.*" + fieldStr.toString() + " from (" + sql;
			if (StringUtils.hasText(this.dl_orderby)) {
				sql += " " + this.dl_orderby + ") tab)";
			} else
				sql += ") tab)";
			if (StringUtils.hasText(condition))
				sql += " WHERE " + condition;
			return sql;
		}else{
			if (StringUtils.hasText(this.dl_condition))
				con=" where "+this.dl_condition;
			if (StringUtils.hasText(condition)){
				if(StringUtils.hasText(con)){
					con+=" and "+condition;
				}else{
					con+=" WHERE " + condition;
				}
			}
			String patternstr="";
			for (DataListDetail detail : dataListDetails) {
				fieldStr.append(",").append(detail.getDld_field());
				String fieldString=detail.getDld_field().trim();
				if (fieldString.contains(" ")) {
					String aliasfield=fieldString.substring( fieldString.lastIndexOf(" ")).trim();
					String repString=fieldString.substring(0, fieldString.lastIndexOf(" ")).trim();
					patternstr="(\\s|\\(|\\)|>|=|<|\\+|\\-|\\*|\\/)("+aliasfield+")(\\s|\\(|\\)|>|=|<|\\+|\\-|\\*|\\/)";
					Pattern pattern=Pattern.compile(patternstr,Pattern.CASE_INSENSITIVE);
					while(con.matches(".*(\\s|\\(|\\)|>|=|<|\\+|\\-|\\*|\\/)((?i)"+aliasfield+")(\\s|\\(|\\)|>|=|<|\\+|\\-|\\*|\\/)[^a-z].*")){
						Matcher matcher=pattern.matcher(con);
						if(matcher.find())
						{   
						    String str2=con.substring(0,matcher.start()+1);
						    String str3=con.substring(matcher.end()-1,con.length());
						    con=str2+" ("+repString+") "+str3;
						}
					}
				} 
			}
			StringBuffer sql=new StringBuffer("select count(1) from (select ");
			sql.append(fieldStr.toString().substring(1));
			sql.append(" from ");
			sql.append(this.dl_tablename);
			sql.append(" "); 
			sql.append(con); 
			sql.append(" )"); 
			return sql.toString();
		}		
	}

	/**
	 * group模式会新增CURRENTMASTER字段， 如果针对该字段筛选， condition将需要拆分
	 * 
	 * @param condition
	 * @return
	 */
	private String[] splitCondition(String condition) {
		if (condition == null || condition.trim().length() == 0) {
			return new String[] { "", "" };
		}
		if (!condition.contains("CURRENTMASTER")) {
			return new String[] { " where " + condition, "" };
		}
		if (!condition.toUpperCase().contains(" AND ")) {
			return new String[] { "", " where " + condition };
		}
		String[] strs = condition.toUpperCase().split(" AND ");
		List<String> ns = new ArrayList<String>();
		int i = 0;
		int j = 0;
		int len = condition.length();
		String masCond = "";
		for (String s : strs) {
			i = j;
			j += s.length();
			if (i > 0 && j < len) {
				i += 5;
				j += 5;
			}
			if (s.trim().length() > 0) {
				if (!s.contains("CURRENTMASTER")) {
					ns.add(condition.substring(i, j));
				} else {
					masCond = condition.substring(i, j);
				}
			}
		}
		return new String[] { " where " + BaseUtil.parseList2Str(ns, " and ", false), " where " + masCond };
	}

	private String getGroupSql(String condition, String orderby, Employee employee, int page, int pageSize) {
		Master master = employee.getCurrentMaster();
		String[] sonCodes = master.getMa_soncode().split(",");
		String masters = employee.getEm_masters();
		masters = masters == null ? employee.getEm_master() : masters;
		List<String> usedCodes = BaseUtil.parseStr2List(masters, ",", false);
		boolean admin = "admin".equals(employee.getEm_type());
		StringBuffer sb = new StringBuffer();
		String[] fixedCondition = splitCondition(condition);
		condition = fixedCondition[0];
		String masterCondition = fixedCondition[1];
		
		if (this.dl_groupby != null && this.dl_groupby.length() > 0){
			orderby=orderby!=null?orderby:"";
			orderby = this.dl_groupby+" "+ orderby;
		}
		else if ((orderby == null || orderby.equals("")) && this.dl_keyfield != null && this.dl_keyfield.indexOf("@") < 0) {
			orderby = "order by " + this.dl_keyfield + " desc";
		}
		else if (orderby != null && !orderby.equals("") && this.dl_keyfield != null && this.dl_keyfield.indexOf("@") < 0 && !orderby.equals(this.getDl_orderby()) ) {
			orderby += ", " + this.dl_keyfield + " desc";
		}	
		orderby = orderby == null ? " " : orderby;
				
		int start = ((page - 1) * pageSize + 1);
		int end = page * pageSize;
		for (String s : sonCodes) {
			if (!admin && !usedCodes.contains(s)) {
				continue;
			}
			if (sb.length() > 0)
				sb.append(" UNION ALL ");
			sb.append("select '").append(s).append("' CURRENTMASTER,").append(getFieldsSql()).append(" from ")
			.append(getFullTableName(this.dl_tablename, s)).append(condition);
		}
		if (sb.length() > 0) {
			return new StringBuffer("select * from (select TT.*, ROWNUM rn from (select * from (").append(sb.toString()).append(")")
					.append(masterCondition).append(" ").append(orderby).append(")TT where ROWNUM <= ").append(end)
					.append(") where rn >= ").append(start).toString();
		}
		return null;
	}

	/**
	 * DataList数据Count
	 * 
	 * @param condition
	 *            附加的条件
	 */
	public String getSql(String condition) {
		String str = "SELECT count(1) c FROM " + this.dl_tablename;
		String con = this.dl_condition;
		condition = (con == null || "".equals(con)) ? condition : ("(" + con + ")")
				+ ((condition == null || "".equals(condition)) ? "" : " AND (" + condition + ")");
		condition = condition.equals("") ? "" : " WHERE " + condition;
		str = condition.equals("") ? str : str + " " + condition;
		if (this.dl_groupby != null && this.dl_groupby.length() > 0) {
			str = "select count(1) from (" + str + " " + this.dl_groupby + ")";
		} else if (this.dl_orderby != null && this.dl_orderby.startsWith("group by")) {
			str = "select count(1) from (" + str + " " + this.dl_orderby + ")";
		}
		return str;
	}

	/**
	 * DataList数据Count(集团版)
	 * 
	 * @param condition
	 * @param master
	 *            帐套
	 * @return
	 */
	public String getSql(String condition, Employee employee) {
		Master master = employee.getCurrentMaster();
		if (master == null || master.getMa_type() == 3 || master.getMa_soncode() == null) {
			return getSql(condition);
		}
		// 集团中心,取资料中心数据
		if (master.getMa_type() == 0
				&& ("Product".equals(this.dl_caller) || "Vendor".equals(this.dl_caller) || "Customer".equals(this.dl_caller) || "Customer!Base"
						.equals(this.dl_caller))) {
			String tabName = this.dl_tablename;
			this.dl_tablename = getFullTableName(this.dl_tablename, BaseUtil.getXmlSetting("dataSob"));
			String sql = getSql(condition);
			this.dl_tablename = tabName;
			return sql;
		}
		return getGroupSql(condition, employee);
	}

	/**
	 * DataList数据Count(集团版)
	 * 
	 * @param condition
	 * @param master
	 *            帐套
	 * @return
	 */
	public String getSearchSql(String condition, Employee employee) {
		Master master = employee.getCurrentMaster();
		if (master == null || master.getMa_type() == 3 || master.getMa_soncode() == null) {
			return getSearchSql(condition);
		}
		// 集团中心,取资料中心数据
		if (master.getMa_type() == 0
				&& ("Product".equals(this.dl_caller) || "Vendor".equals(this.dl_caller) || "Customer".equals(this.dl_caller) || "Customer!Base"
						.equals(this.dl_caller))) {
			String tabName = this.dl_tablename;
			this.dl_tablename = getFullTableName(this.dl_tablename, BaseUtil.getXmlSetting("dataSob"));
			String sql = getSql(condition);
			this.dl_tablename = tabName;
			return sql;
		}
		return getGroupSql(condition, employee);
	}

	private String getGroupSql(String condition, Employee employee) {
		Master master = employee.getCurrentMaster();
		String[] sonCodes = master.getMa_soncode().split(",");
		String masters = employee.getEm_masters();
		masters = masters == null ? employee.getEm_master() : masters;
		List<String> usedCodes = BaseUtil.parseStr2List(masters, ",", false);
		boolean admin = "admin".equals(employee.getEm_type());
		String con = this.dl_condition;
		condition = (con == null || "".equals(con)) ? condition : ("(" + con + ")")
				+ ((condition == null || "".equals(condition)) ? "" : " AND (" + condition + ")");
		String[] fixedCondition = splitCondition(condition);
		condition = fixedCondition[0];
		String masterCondition = fixedCondition[1];
		StringBuffer sb = new StringBuffer();
		for (String s : sonCodes) {
			if (!admin && !usedCodes.contains(s)) {
				continue;
			}
			if (sb.length() > 0)
				sb.append(" UNION ALL ");
			else
				sb.append("select sum(c) from (");
			sb.append("select count(1) c,'").append(s).append("' CURRENTMASTER from ").append(getFullTableName(this.dl_tablename, s))
			.append(condition);
		}
		sb.append(")").append(masterCondition);
		return sb.toString();
	}

	/**
	 * 加帐套名称前缀
	 * 
	 * @param masterCode
	 * @return
	 */
	private String getFullTableName(String tabName, String masterCode) {
		String[] strs = tabName.split("left join ");
		StringBuffer sb = new StringBuffer();
		for (int i = 0, len = strs.length; i < len; i++) {
			sb.append(masterCode).append(".").append(strs[i]);
			if (i != len - 1)
				sb.append("left join ");
		}
		return sb.toString();
	}
	/**
	 * 获取列表合计数据
	 * */
	public String getSummarySql(String condition){
		StringBuffer sumFields = new StringBuffer("");
		for (DataListDetail detail : this.dataListDetails) {
			if(StringUtils.hasText(detail.getDld_summarytype())){
				if(Constant.SUMMARY_AVERAGE.equalsIgnoreCase(detail.getDld_summarytype())){
					sumFields.append("avg("+detail.getDld_field()+")").append(",");
				}else if(Constant.SUMMARY_MIN.equalsIgnoreCase(detail.getDld_summarytype())){
					sumFields.append("min("+detail.getDld_field()+")").append(",");
				}else if(Constant.SUMMARY_MAX.equalsIgnoreCase(detail.getDld_summarytype())){
					sumFields.append("max("+detail.getDld_field()+")").append(",");
				}else{
					sumFields.append("sum("+detail.getDld_field()+")").append(",");
				}
			}

		}
		StringBuffer fieldStr = new StringBuffer();		
		BaseDao baseDao = (BaseDao) ContextUtil.getBean("baseDao");
		if(sumFields.length()>1){
			StringBuffer sb = new StringBuffer("SELECT ");
			if(!baseDao.isDBSetting("newDatalistSql")){	
				for (DataListDetail detail : dataListDetails) {
					if (detail.getDld_field().contains(" ")) {// 有别名的字段
						fieldStr.append(",").append(detail.getDld_field());
					}
				}
				String sql = "SELECT tab.*" + fieldStr.toString() + " from (SELECT * FROM "+this.dl_tablename+ " ) tab";
				sb.append(sumFields.substring(0, sumFields.length()-1));
				String con = this.dl_condition;
				condition = (con == null || "".equals(con)) ? condition : ("(" + con + ")")
						+ ((condition == null || "".equals(condition)) ? "" : " AND (" + condition + ")");
				condition = StringUtils.hasText(condition) ?  " WHERE " + condition:" " ;
				sb.append(" FROM (" +sql+") ");
				sb.append(" ");
				sb.append(condition);
				sb.append(StringUtils.hasText(this.dl_groupby)?this.dl_groupby:"");
			}else{
				sb.append(sumFields.substring(0, sumFields.length()-1));
				String con = this.dl_condition;
				condition = (con == null || "".equals(con)) ? condition : ("(" + con + ")")
						+ ((condition == null || "".equals(condition)) ? "" : " AND (" + condition + ")");
				condition = StringUtils.hasText(condition) ?  " WHERE " + condition:" " ;
				for (DataListDetail detail : this.dataListDetails) {
					String fieldString=detail.getDld_field().trim();
					if (fieldString.contains(" ")) {
						String aliasfield=fieldString.substring( fieldString.lastIndexOf(" ")).trim();
						String repString=fieldString.substring(0, fieldString.lastIndexOf(" ")).trim();
						String patternstr="(\\s|\\(|\\)|>|=|<|\\+|\\-|\\*|\\/)("+aliasfield+")(\\s|\\(|\\)|>|=|<|\\+|\\-|\\*|\\/)";
						Pattern pattern=Pattern.compile(patternstr,Pattern.CASE_INSENSITIVE);
						while(condition.matches(".*(\\s|\\(|\\)|>|=|<|\\+|\\-|\\*|\\/)((?i)"+aliasfield+")(\\s|\\(|\\)|>|=|<|\\+|\\-|\\*|\\/)[^a-z].*")){
							Matcher matcher=pattern.matcher(condition);
							if(matcher.find())
							{   
							    String str2=condition.substring(0,matcher.start()+1);
							    String str3=condition.substring(matcher.end()-1,condition.length());
							    condition=str2+" ("+repString+") "+str3;
							}
						}		
					}
				}				
				sb.append(" FROM "+this.dl_tablename);
				sb.append(" ");
				sb.append(condition);
				sb.append(StringUtils.hasText(this.dl_groupby)?this.dl_groupby:"");
			}
			return sb.toString();
		}else return null;
	}
}
