package com.uas.erp.model;

import java.io.Serializable;
import java.util.List;

import com.uas.erp.dao.Saveable;

/**
 * @author yingp
 **/
public class DBFindSet implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int ds_id;
	private String ds_caller;
	private String ds_tablename;
	private String ds_searchcaller;
	private String ds_caption;
	private String ds_groupby;
	private String ds_orderby;
	private String ds_fixedcondition;
	private int ds_allownew;
	private int ds_allowedit;
	private int ds_allowfilt;
	private int ds_findall;
	private int ds_gstcodefield;
	private int ds_gstnextcodefield;
	private int ds_gstdetno;
	private int ds_outcondition;
	private int ds_distinct;
	private int ds_multichoose;
	private int ds_pagesize;
	private String ds_popedommodule;
	private String ds_recordfield;
	private String ds_copfield;
	private int ds_copdatabase;
	private String ds_caption_en;
	private String ds_caption_fan;
	private String ds_enid;
	private String ds_error;
	private int ds_allowreset;
	private String dataString;// 字符串格式的数据
	private Integer ds_autoheight = 0;
	private String ds_dlccaller;// 关联下拉项
    private Integer ds_nolimit = 0;//不受数据权限管控
    private Integer ds_enablelikes;//是否启用联想搜索
    private Integer ds_isfast = 0;//大数据，放大镜不取count
	public String getDs_enid() {
		return ds_enid;
	}

	public void setDs_enid(String dsEnid) {
		ds_enid = dsEnid;
	}

	public int getDs_id() {
		return ds_id;
	}

	public void setDs_id(int ds_id) {
		this.ds_id = ds_id;
	}

	public String getDs_caller() {
		return ds_caller;
	}

	public void setDs_caller(String ds_caller) {
		this.ds_caller = ds_caller;
	}

	public String getDs_tablename() {
		return ds_tablename;
	}

	public void setDs_tablename(String ds_tablename) {
		this.ds_tablename = ds_tablename;
	}

	public String getDs_searchcaller() {
		return ds_searchcaller;
	}

	public void setDs_searchcaller(String ds_searchcaller) {
		this.ds_searchcaller = ds_searchcaller;
	}

	public String getDs_caption() {
		return ds_caption;
	}

	public void setDs_caption(String ds_caption) {
		this.ds_caption = ds_caption;
	}

	public String getDs_groupby() {
		return ds_groupby;
	}

	public void setDs_groupby(String ds_groupby) {
		this.ds_groupby = ds_groupby;
	}

	public String getDs_orderby() {
		return ds_orderby;
	}

	public void setDs_orderby(String ds_orderby) {
		this.ds_orderby = ds_orderby;
	}

	public Integer getDs_autoheight() {
		return ds_autoheight == null ? 0 : ds_autoheight;
	}

	public void setDs_autoheight(Integer ds_autoheight) {
		this.ds_autoheight = ds_autoheight == null ? 0 : ds_autoheight;
	}

	public String getDs_fixedcondition() {
		return ds_fixedcondition;
	}

	public void setDs_fixedcondition(String ds_fixedcondition) {
		this.ds_fixedcondition = ds_fixedcondition;
	}

	public int getDs_allownew() {
		return ds_allownew;
	}

	public void setDs_allownew(int ds_allownew) {
		this.ds_allownew = ds_allownew;
	}

	public int getDs_allowedit() {
		return ds_allowedit;
	}

	public void setDs_allowedit(int ds_allowedit) {
		this.ds_allowedit = ds_allowedit;
	}

	public int getDs_allowfilt() {
		return ds_allowfilt;
	}

	public void setDs_allowfilt(int ds_allowfilt) {
		this.ds_allowfilt = ds_allowfilt;
	}

	public int getDs_allowreset() {
		return ds_allowreset;
	}

	public void setDs_allowreset(int ds_allowreset) {
		this.ds_allowreset = ds_allowreset;
	}

	public int getDs_findall() {
		return ds_findall;
	}

	public void setDs_findall(int ds_findall) {
		this.ds_findall = ds_findall;
	}

	public int getDs_gstcodefield() {
		return ds_gstcodefield;
	}

	public void setDs_gstcodefield(int ds_gstcodefield) {
		this.ds_gstcodefield = ds_gstcodefield;
	}

	public int getDs_gstnextcodefield() {
		return ds_gstnextcodefield;
	}

	public void setDs_gstnextcodefield(int ds_gstnextcodefield) {
		this.ds_gstnextcodefield = ds_gstnextcodefield;
	}

	public int getDs_gstdetno() {
		return ds_gstdetno;
	}

	public void setDs_gstdetno(int ds_gstdetno) {
		this.ds_gstdetno = ds_gstdetno;
	}

	public int getDs_outcondition() {
		return ds_outcondition;
	}

	public void setDs_outcondition(int ds_outcondition) {
		this.ds_outcondition = ds_outcondition;
	}

	public int getDs_distinct() {
		return ds_distinct;
	}

	public void setDs_distinct(int ds_distinct) {
		this.ds_distinct = ds_distinct;
	}

	public int getDs_multichoose() {
		return ds_multichoose;
	}

	public void setDs_multichoose(int ds_multichoose) {
		this.ds_multichoose = ds_multichoose;
	}

	public int getDs_pagesize() {
		return ds_pagesize;
	}

	public void setDs_pagesize(int ds_pagesize) {
		this.ds_pagesize = ds_pagesize;
	}

	public String getDs_popedommodule() {
		return ds_popedommodule;
	}

	public void setDs_popedommodule(String ds_popedommodule) {
		this.ds_popedommodule = ds_popedommodule;
	}

	public String getDs_recordfield() {
		return ds_recordfield;
	}

	public void setDs_recordfield(String ds_recordfield) {
		this.ds_recordfield = ds_recordfield;
	}

	public String getDs_copfield() {
		return ds_copfield;
	}

	public void setDs_copfield(String ds_copfield) {
		this.ds_copfield = ds_copfield;
	}

	public int getDs_copdatabase() {
		return ds_copdatabase;
	}

	public void setDs_copdatabase(int ds_copdatabase) {
		this.ds_copdatabase = ds_copdatabase;
	}

	public String getDs_caption_en() {
		return ds_caption_en;
	}

	public void setDs_caption_en(String ds_caption_en) {
		this.ds_caption_en = ds_caption_en;
	}

	public String getDs_caption_fan() {
		return ds_caption_fan;
	}

	public void setDs_caption_fan(String ds_caption_fan) {
		this.ds_caption_fan = ds_caption_fan;
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

	public String getDataString() {
		return dataString;
	}

	public void setDataString(String dataString) {
		this.dataString = dataString;
	}

	@Override
	public String table() {
		return "DbfindSet";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "ds_id" };
	}

	private List<DBFindSetDetail> dbFindSetDetails;

	public List<DBFindSetDetail> getDbFindSetDetails() {
		return dbFindSetDetails;
	}

	public void setDbFindSetDetails(List<DBFindSetDetail> dbFindSetDetails) {
		this.dbFindSetDetails = dbFindSetDetails;
	}

	/**
	 * 从dbfindset表拿到object的字段和对应的表名拼装成sql语句 并可以实现分页功能
	 * 
	 * @param condition
	 *            附加的条件
	 */
	public String getSql(String condition, String orderby, int page, int pageSize) {
		StringBuffer longFieldsSb = new StringBuffer("");
		for (DBFindSetDetail detail : this.dbFindSetDetails) {
			longFieldsSb.append(detail.getDd_fieldname());
			longFieldsSb.append(",");
		}
		String longFieldsStr = longFieldsSb.substring(0, longFieldsSb.length() - 1);
		condition = "".equals(condition) ? "" : " WHERE " + condition;
		int start = ((page - 1) * pageSize + 1);
		int end = page * pageSize;
		StringBuffer sb = new StringBuffer("select * from (select TT.*, ROWNUM rn from (select ");
		if (this.ds_distinct == 1) {
			sb.append("distinct ");
		}
		sb.append(longFieldsStr);
		sb.append(" from ");
		sb.append(this.ds_tablename);
		sb.append(" ");
		sb.append(condition);
		sb.append(" ");
		if (orderby != null && orderby.length() > 0 && !"null".equals(orderby))
			sb.append(orderby);
		else if (this.ds_orderby != null && this.ds_orderby.indexOf("order") >= 0)
			sb.append(this.ds_orderby);
		sb.append(" )TT where ROWNUM <= ");
		sb.append(end);
		sb.append(") where rn >= ");
		sb.append(start);
		return sb.toString();
	}

	/**
	 * 从dbfindSet表拿到object的字段和对应的表名拼装成sql语句
	 * 
	 * @param condition
	 *            附加的条件
	 */
	public String getSql(String condition) {
		String sub = this.ds_tablename + (condition.equals("") ? "" : " where " + condition);
		String str = "SELECT count(1) FROM " + sub;
		if (this.ds_distinct == 1) {
			StringBuffer longFieldsSb = new StringBuffer("");
			for (DBFindSetDetail detail : this.dbFindSetDetails) {
				longFieldsSb.append(detail.getDd_fieldname());
				longFieldsSb.append(",");
			}
			String longFieldsStr = longFieldsSb.substring(0, longFieldsSb.length() - 1);
			str = "SELECT count(1) FROM (select distinct " + longFieldsStr + " from " + sub + ")";
		}
		return str;
	}

	public String getDs_error() {
		return ds_error;
	}

	public void setDs_error(String ds_error) {
		this.ds_error = ds_error;
	}

	public String getDs_dlccaller() {
		return ds_dlccaller;
	}

	public void setDs_dlccaller(String ds_dlccaller) {
		this.ds_dlccaller = ds_dlccaller;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ds_caller == null) ? 0 : ds_caller.hashCode());
		result = prime * result + ((ds_caption == null) ? 0 : ds_caption.hashCode());
		result = prime * result + ds_id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DBFindSet other = (DBFindSet) obj;
		if (ds_caller == null) {
			if (other.ds_caller != null)
				return false;
		} else if (!ds_caller.equals(other.ds_caller))
			return false;
		if (ds_caption == null) {
			if (other.ds_caption != null)
				return false;
		} else if (!ds_caption.equals(other.ds_caption))
			return false;
		if (ds_id != other.ds_id)
			return false;
		return true;
	}

	public Integer getDs_isfast() {
		return ds_isfast;
	}

	public void setDs_isfast(Integer ds_isfast) {
		this.ds_isfast = ds_isfast;
	}
}
