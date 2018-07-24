package com.uas.erp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.bind.Constant;

/**
 * 工作台入口
 * */
public class Bench implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 工作台信息
	 * */
	private Integer bc_id;
	private String bc_code;
	private String bc_title;
	private Integer bc_detno;
	private Integer bc_used;
	private String bc_urlcond;
	private String bc_icon;
	private String bc_desc;
	private List <BenchBusiness>  benchBusinesses; //业务
	private List <BenchBusiness>  hideBusinesses; //隐藏业务
	private Map<String, List<BenchButton>>  benchButtons; //工作台按钮
	public Integer getBc_id() {
		return bc_id;
	}
	public void setBc_id(Integer bc_id) {
		this.bc_id = bc_id;
	}
	public String getBc_code() {
		return bc_code;
	}
	public void setBc_code(String bc_code) {
		this.bc_code = bc_code;
	}
	public String getBc_title() {
		return bc_title;
	}
	public void setBc_title(String bc_title) {
		this.bc_title = bc_title;
	}
	
	public Integer getBc_detno() {
		return bc_detno;
	}
	public void setBc_detno(Integer bc_detno) {
		this.bc_detno = bc_detno;
	}
	public Integer getBc_used() {
		return bc_used;
	}
	public void setBc_used(Integer bc_used) {
		this.bc_used = bc_used;
	}
	public String getBc_urlcond() {
		return bc_urlcond;
	}
	public void setBc_urlcond(String bc_urlcond) {
		this.bc_urlcond = bc_urlcond;
	}
	public String getBc_icon() {
		return bc_icon;
	}
	public void setBc_icon(String bc_icon) {
		this.bc_icon = bc_icon;
	}
	public String getBc_desc() {
		return bc_desc;
	}
	public void setBc_desc(String bc_desc) {
		this.bc_desc = bc_desc;
	}
	public List<BenchBusiness> getBenchBusinesses() {
		return benchBusinesses;
	}
	public void setBenchBusinesses(List<BenchBusiness> benchBusinesses) {
		this.benchBusinesses = benchBusinesses;
	}
	public List<BenchBusiness> getHideBusinesses() {
		return hideBusinesses;
	}
	public void setHideBusinesses(List<BenchBusiness> hideBusinesses) {
		this.hideBusinesses = hideBusinesses;
	}

	public Map<String, List<BenchButton>> getBenchButtons() {
		return benchButtons;
	}
	public void setBenchButtons(Map<String, List<BenchButton>> benchButtons) {
		this.benchButtons = benchButtons;
	}



	public static class BenchButton implements Serializable{
		/**
		 * 工作台按钮
		 */
		private static final long serialVersionUID = 1L;
		private Integer bb_id;
		private String bb_code;
		private String bb_bccode;
		private Integer bb_detno;
		private String bb_url;
		private String bb_text;
		private String bb_group;
		private String bb_busingroup;
		private String bb_caller;
		private String bb_listurl;
		
		private List<BenchButton> menuButtons;
		
		public Integer getBb_id() {
			return bb_id;
		}
		public void setBb_id(Integer bb_id) {
			this.bb_id = bb_id;
		}
		public String getBb_code() {
			return bb_code;
		}
		public void setBb_code(String bb_code) {
			this.bb_code = bb_code;
		}
		public String getBb_bccode() {
			return bb_bccode;
		}
		public void setBb_bccode(String bb_bccode) {
			this.bb_bccode = bb_bccode;
		}
		public Integer getBb_detno() {
			return bb_detno;
		}
		public void setBb_detno(Integer bb_detno) {
			this.bb_detno = bb_detno;
		}
		public String getBb_url() {
			return bb_url;
		}
		public void setBb_url(String bb_url) {
			this.bb_url = bb_url;
		}
		public String getBb_text() {
			return bb_text;
		}
		public void setBb_text(String bb_text) {
			this.bb_text = bb_text;
		}
		public String getBb_group() {
			return bb_group;
		}
		public void setBb_group(String bb_group) {
			this.bb_group = bb_group;
		}
		public String getBb_busingroup() {
			return bb_busingroup;
		}
		public void setBb_busingroup(String bb_busingroup) {
			this.bb_busingroup = bb_busingroup;
		}
		public String getBb_caller() {
			return bb_caller;
		}
		public void setBb_caller(String bb_caller) {
			this.bb_caller = bb_caller;
		}
		public String getBb_listurl() {
			return bb_listurl;
		}
		public void setBb_listurl(String bb_listurl) {
			this.bb_listurl = bb_listurl;
		}
		public List<BenchButton> getMenuButtons() {
			return menuButtons;
		}
		public void setMenuButtons(List<BenchButton> menuButtons) {
			this.menuButtons = menuButtons;
		}
		
	}
	
	public static class BenchBusiness implements Serializable{
		/**
		 * 工作台业务
		 */
		private static final long serialVersionUID = 1L;
		private String bb_code;
		private String bb_name;
		private String active;
		private Integer count;
		private List <BenchScene>  benchScenes; //场景
		
		public String getBb_code() {
			return bb_code;
		}
		public void setBb_code(String bb_code) {
			this.bb_code = bb_code;
		}
		
		public String getBb_name() {
			return bb_name;
		}
		
		public void setBb_name(String bb_name) {
			this.bb_name = bb_name;
		}
		public String getActive() {
			return active;
		}
		public void setActive(String active) {
			this.active = active;
		}
		public Integer getCount() {
			return count;
		}
		public void setCount(Integer count) {
			this.count = count;
		}
		public List<BenchScene> getBenchScenes() {
			return benchScenes;
		}
		public void setBenchScenes(List<BenchScene> benchScenes) {
			this.benchScenes = benchScenes;
		}
		
	}

	public static class SceneButton implements Serializable{
		/**
		 * 工作台场景按钮
		 */
		private static final long serialVersionUID = 1L;
		private Integer sb_id;
		private String sb_alias;
		private Integer sb_detno;
		private String sb_condition;
		private String sb_url;
		private String sb_bscode;
		private String sb_requesttype;
		private String sb_relativecaller;
		private String sb_title;
		private String sb_spaction;
		private String sb_description;
		public Integer getSb_id() {
			return sb_id;
		}
		public void setSb_id(Integer sb_id) {
			this.sb_id = sb_id;
		}
		public String getSb_alias() {
			return sb_alias;
		}
		public void setSb_alias(String sb_alias) {
			this.sb_alias = sb_alias;
		}
		public Integer getSb_detno() {
			return sb_detno;
		}
		public void setSb_detno(Integer sb_detno) {
			this.sb_detno = sb_detno;
		}
		public String getSb_condition() {
			return sb_condition;
		}
		public void setSb_condition(String sb_condition) {
			this.sb_condition = sb_condition;
		}
		public String getSb_url() {
			return sb_url;
		}
		public void setSb_url(String sb_url) {
			this.sb_url = sb_url;
		}
		public String getSb_bscode() {
			return sb_bscode;
		}
		public void setSb_bscode(String sb_bscode) {
			this.sb_bscode = sb_bscode;
		}
		public String getSb_requesttype() {
			return sb_requesttype;
		}
		public void setSb_requesttype(String sb_requesttype) {
			this.sb_requesttype = sb_requesttype;
		}
		public String getSb_relativecaller() {
			return sb_relativecaller;
		}
		public void setSb_relativecaller(String sb_relativecaller) {
			this.sb_relativecaller = sb_relativecaller;
		}
		public String getSb_title() {
			return sb_title;
		}
		public void setSb_title(String sb_title) {
			this.sb_title = sb_title;
		}
		public String getSb_spaction() {
			return sb_spaction;
		}
		public void setSb_spaction(String sb_spaction) {
			this.sb_spaction = sb_spaction;
		}
		public String getSb_description() {
			return sb_description;
		}
		public void setSb_description(String sb_description) {
			this.sb_description = sb_description;
		}
		
	}
	
	
	public static class BenchScene implements Serializable{
		/**
		 * 场景信息
		 */
		private static final long serialVersionUID = 1L;
		private Integer bs_id;
		private String bs_code;
		private String bs_title;
		private String bs_table;
		private Integer bs_detno;
		private String bs_fixcond;
		private Integer bs_iscount;
		private String bs_condition;
		private String bs_bccode;
		private String bs_groupby;
		private String bs_orderby;
		private String bs_keyfield;
		private String bs_selffield;
		private String bs_batchset;
		private String bs_bbcode;
		private String bs_bbname;
		private List<BenchSceneGrid> benchSceneGrids;
		private List <SceneButton>  sceneButtons; //场景按钮
		private Integer count = 0;
		private String bs_caller;
		private Integer bs_enable;
		private Integer bs_islist = -1;
		
		public Integer getBs_id() {
			return bs_id;
		}
		public void setBs_id(Integer bs_id) {
			this.bs_id = bs_id;
		}
		public String getBs_code() {
			return bs_code;
		}
		public void setBs_code(String bs_code) {
			this.bs_code = bs_code;
		}
		public String getBs_title() {
			return bs_title;
		}
		public void setBs_title(String bs_title) {
			this.bs_title = bs_title;
		}
		public String getBs_table() {
			return bs_table;
		}
		public void setBs_table(String bs_table) {
			this.bs_table = bs_table;
		}
		public Integer getBs_detno() {
			return bs_detno;
		}
		public void setBs_detno(Integer bs_detno) {
			this.bs_detno = bs_detno;
		}
		public String getBs_fixcond() {
			return bs_fixcond;
		}
		public void setBs_fixcond(String bs_fixcond) {
			this.bs_fixcond = bs_fixcond;
		}
		public Integer getBs_iscount() {
			return bs_iscount;
		}
		public void setBs_iscount(Integer bs_iscount) {
			this.bs_iscount = bs_iscount;
		}
		public String getBs_condition() {
			return bs_condition;
		}
		public void setBs_condition(String bs_condition) {
			this.bs_condition = bs_condition;
		}
		public String getBs_bccode() {
			return bs_bccode;
		}
		public void setBs_bccode(String bs_bccode) {
			this.bs_bccode = bs_bccode;
		}
		public String getBs_groupby() {
			return bs_groupby;
		}
		public void setBs_groupby(String bs_groupby) {
			this.bs_groupby = bs_groupby;
		}
		public String getBs_orderby() {
			return bs_orderby;
		}
		public void setBs_orderby(String bs_orderby) {
			this.bs_orderby = bs_orderby;
		}
		public String getBs_keyfield() {
			return bs_keyfield;
		}
		public void setBs_keyfield(String bs_keyfield) {
			this.bs_keyfield = bs_keyfield;
		}
		public String getBs_selffield() {
			return bs_selffield;
		}
		public void setBs_selffield(String bs_selffield) {
			this.bs_selffield = bs_selffield;
		}
		public String getBs_batchset() {
			return bs_batchset;
		}
		public void setBs_batchset(String bs_batchset) {
			this.bs_batchset = bs_batchset;
		}
		public String getBs_bbcode() {
			return bs_bbcode;
		}
		public void setBs_bbcode(String bs_bbcode) {
			this.bs_bbcode = bs_bbcode;
		}
		public String getBs_bbname() {
			return bs_bbname;
		}
		public void setBs_bbname(String bs_bbname) {
			this.bs_bbname = bs_bbname;
		}
		public Integer getBs_enable() {
			return bs_enable;
		}
		public void setBs_enable(Integer bs_enable) {
			this.bs_enable = bs_enable;
		}
		public List<BenchSceneGrid> getBenchSceneGrids() {
			return benchSceneGrids;
		}
		public void setBenchSceneGrids(List<BenchSceneGrid> benchSceneGrids) {
			this.benchSceneGrids = benchSceneGrids;
		}     
		public List<SceneButton> getSceneButtons() {
			return sceneButtons;
		}
		public void setSceneButtons(List<SceneButton> sceneButtons) {
			this.sceneButtons = sceneButtons;
		}
		public Integer getCount() {
			return count;
		}
		public void setCount(Integer count) {
			this.count = count;
		}
		public String getBs_caller() {
			return bs_caller;
		}
		public void setBs_caller(String bs_caller) {
			this.bs_caller = bs_caller;
		}
		public Integer getBs_islist() {
			return bs_islist;
		}
		public void setBs_islist(Integer bs_islist) {
			this.bs_islist = bs_islist;
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
			if (master.getMa_type() == 0) {
				String tabName = this.bs_table;
				this.bs_table = getFullTableName(this.bs_table, BaseUtil.getXmlSetting("dataSob"));
				String sql = getSql(condition);
				this.bs_table = tabName;
				return sql;
			}
			return getGroupSql(condition, employee);
		}
		
		
		/**
		 * 高级查询时，DataList数据Count，支持别名字段的筛选
		 * 
		 * @param condition
		 *            附加的条件
		 */
		public String getSearchSql(String condition) {
			if (StringUtils.hasText(this.bs_groupby)) {
				return getSql(condition);
			}
			StringBuffer fieldStr = new StringBuffer();
			if (benchSceneGrids!=null) {
				for (BenchSceneGrid detail : benchSceneGrids) {
					if (detail.getSg_field().contains(" ")) {// 有别名的字段
						fieldStr.append(",").append(detail.getSg_field());
					}
				}
			}
			String sql = "SELECT * FROM " + this.bs_table;
			sql = "select count(1) from (SELECT tab.*" + fieldStr.toString() + " from (" + sql;
			if (StringUtils.hasText(this.bs_orderby)) {
				sql += " " + this.bs_orderby + ") tab)";
			} else
				sql += ") tab)";
			if (StringUtils.hasText(condition))
				sql += " WHERE " + condition;
			return sql;
		}
		
		/**
		 * BenchScene取数据SQL，支持别名字段的筛选(集团版)
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
			if (master.getMa_type() == 0) {
				String tabName = this.bs_table;
				this.bs_table = getFullTableName(this.bs_table, BaseUtil.getXmlSetting("dataSob"));
				String sql = getSearchSql(condition, orderby, page, pageSize);
				this.bs_table = tabName;
				return sql;
			}
			return getGroupSql(condition, orderby, employee, page, pageSize);
		}

		/**
		 * BenchScene取数据SQL，支持别名字段的筛选
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
			for (BenchSceneGrid detail : this.benchSceneGrids) {
				if (fieldStr.length() > 0)
					fieldStr.append(",");
				if (detail.getSg_field().contains(" ")) {
					aliasStr.append(",").append(detail.getSg_field());
					String[] strs = detail.getSg_field().split(" ");
					fieldStr.append(strs[strs.length - 1]);
				} else
					fieldStr.append(detail.getSg_field());
			}
			condition = "".equals(condition) ? "" : " WHERE " + condition;
			
			if (this.bs_groupby != null && this.bs_groupby.length() > 0) {
				orderby=orderby!=null?orderby:"";
				orderby = this.bs_groupby+" "+ orderby;
			}
			else if ((orderby == null || orderby.equals("")) && this.bs_keyfield != null && this.bs_keyfield.indexOf("@") < 0) {
				orderby = "order by " + this.bs_keyfield + " desc";
			}
			else if (orderby != null && !orderby.equals("") && this.bs_keyfield != null && this.bs_keyfield.indexOf("@") < 0 && !orderby.equals(this.getBs_orderby()) ) {
				orderby += ", " + this.bs_keyfield + " desc";
			}	
			orderby = orderby == null ? " " : orderby;
			
			StringBuffer aliasSql = new StringBuffer("select tab.*");
			aliasSql.append(aliasStr).append(" from (select * from ").append(this.bs_table).append(") tab");
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
			
			if (this.bs_groupby != null && this.bs_groupby.length() > 0){
				orderby=orderby!=null?orderby:"";
				orderby = this.bs_groupby+" "+ orderby;
			}
			else if ((orderby == null || orderby.equals("")) && this.bs_keyfield != null && this.bs_keyfield.indexOf("@") < 0) {
				orderby = "order by " + this.bs_keyfield + " desc";
			}
			else if (orderby != null && !orderby.equals("") && this.bs_keyfield != null && this.bs_keyfield.indexOf("@") < 0 && !orderby.equals(this.getBs_orderby()) ) {
				orderby += ", " + this.bs_keyfield + " desc";
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
				.append(getFullTableName(this.bs_table, s)).append(condition);
			}
			if (sb.length() > 0) {
				return new StringBuffer("select * from (select TT.*, ROWNUM rn from (select * from (").append(sb.toString()).append(")")
						.append(masterCondition).append(" ").append(orderby).append(")TT where ROWNUM <= ").append(end)
						.append(") where rn >= ").append(start).toString();
			}
			return null;
		}
		
		private String getFieldsSql() {
			StringBuffer sb = new StringBuffer();
			for (BenchSceneGrid detail : this.benchSceneGrids) {
				sb.append(detail.getSg_field());
				sb.append(",");
			}
			return sb.substring(0, sb.length() - 1);
		}

		/**
		 * BenchScene数据Count
		 * 
		 * @param condition
		 *            附加的条件
		 */
		public String getSql(String condition) {
			String str = "SELECT count(1) c FROM " + this.bs_table;
			condition = condition.equals("") ? "" : " WHERE " + condition;
			str = condition.equals("") ? str : str + " " + condition;
			if (this.bs_groupby != null && this.bs_groupby.length() > 0) {
				str = "select count(1) from (" + str + " " + this.bs_groupby + ")";
			} else if (this.bs_orderby != null && this.bs_orderby.startsWith("group by")) {
				str = "select count(1) from (" + str + " " + this.bs_orderby + ")";
			}
			return str;
		}

		/**
		 * BenchScene数据Count(集团版)
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
			if (master.getMa_type() == 0) {
				String tabName = this.bs_table;
				this.bs_table = getFullTableName(this.bs_table, BaseUtil.getXmlSetting("dataSob"));
				String sql = getSql(condition);
				this.bs_table = tabName;
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
			String con = this.bs_condition;
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
				sb.append("select count(1) c,'").append(s).append("' CURRENTMASTER from ").append(getFullTableName(this.bs_table, s))
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
			for (BenchSceneGrid detail : this.benchSceneGrids) {
				if(StringUtils.hasText(detail.getSg_summarytype())){
					if(Constant.SUMMARY_AVERAGE.equalsIgnoreCase(detail.getSg_summarytype())){
						sumFields.append("avg("+detail.getSg_field()+")").append(",");
					}else if(Constant.SUMMARY_MIN.equalsIgnoreCase(detail.getSg_summarytype())){
						sumFields.append("min("+detail.getSg_field()+")").append(",");
					}else if(Constant.SUMMARY_MAX.equalsIgnoreCase(detail.getSg_summarytype())){
						sumFields.append("max("+detail.getSg_field()+")").append(",");
					}else{
						sumFields.append("sum("+detail.getSg_field()+")").append(",");
					}
				}

			}
			if(sumFields.length()>1){
				StringBuffer sb = new StringBuffer("SELECT ");
				sb.append(sumFields.substring(0, sumFields.length()-1));
				condition = StringUtils.hasText(condition) ?  " WHERE " + condition:"" ;
				sb.append(" FROM " +this.bs_table);
				sb.append(" ");
				sb.append(condition);
				sb.append(StringUtils.hasText(this.bs_groupby)?this.bs_groupby:"");
				return sb.toString();
			}else return null;
		}
	}	
   
	public static class BenchSceneGrid implements Serializable{
		/**
		 *场景明细GRID 
		 */
		private static final long serialVersionUID = 1L;
		private Integer  sg_id;
		private String  sg_text;
		private String  sg_field;
		private Integer  sg_detno;
		private Integer  sg_width;
		private String  sg_type;
		private Integer  sg_isdesktop;
		private String  sg_bscode;//考虑将来作为升级的唯一标识
		private String sg_render;
		private String sg_summarytype;
		private Integer sg_editable;
		private String sg_text_en;
		private String sg_text_fan;
		private String sg_table;
		public Integer getSg_id() {
			return sg_id;
		}
		public void setSg_id(Integer sg_id) {
			this.sg_id = sg_id;
		}
		public String getSg_text() {
			return sg_text;
		}
		public void setSg_text(String sg_text) {
			this.sg_text = sg_text;
		}
		public String getSg_field() {
			return sg_field;
		}
		public void setSg_field(String sg_field) {
			this.sg_field = sg_field;
		}
		public Integer getSg_detno() {
			return sg_detno;
		}
		public void setSg_detno(Integer sg_detno) {
			this.sg_detno = sg_detno;
		}
		public Integer getSg_width() {
			return sg_width;
		}
		public void setSg_width(Integer sg_width) {
			this.sg_width = sg_width;
		}
		public String getSg_type() {
			return sg_type;
		}
		public void setSg_type(String sg_type) {
			this.sg_type = sg_type;
		}
		public Integer getSg_isdesktop() {
			return sg_isdesktop;
		}
		public void setSg_isdesktop(Integer sg_isdesktop) {
			this.sg_isdesktop = sg_isdesktop;
		}
		public String getSg_bscode() {
			return sg_bscode;
		}
		public void setSg_bscode(String sg_bscode) {
			this.sg_bscode = sg_bscode;
		}
		public String getSg_render() {
			return sg_render;
		}
		public void setSg_render(String sg_render) {
			this.sg_render = sg_render;
		}
		public String getSg_summarytype() {
			return sg_summarytype;
		}
		public void setSg_summarytype(String sg_summarytype) {
			this.sg_summarytype = sg_summarytype;
		}
		public Integer getSg_editable() {
			return sg_editable;
		}
		public void setSg_editable(Integer sg_editable) {
			this.sg_editable = sg_editable;
		}
		public String getSg_text_en() {
			return sg_text_en;
		}
		public void setSg_text_en(String sg_text_en) {
			this.sg_text_en = sg_text_en;
		}
		public String getSg_text_fan() {
			return sg_text_fan;
		}
		public void setSg_text_fan(String sg_text_fan) {
			this.sg_text_fan = sg_text_fan;
		}
		public String getSg_table() {
			return sg_table;
		}
		public void setSg_table(String sg_table) {
			this.sg_table = sg_table;
		}
		
	}
}
