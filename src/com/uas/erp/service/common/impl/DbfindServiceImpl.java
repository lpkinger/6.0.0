package com.uas.erp.service.common.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.JacksonUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DataListComboDao;
import com.uas.erp.dao.common.DbfindSetDao;
import com.uas.erp.dao.common.DbfindSetGridDao;
import com.uas.erp.dao.common.DbfindSetUiDao;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.model.DBFindSet;
import com.uas.erp.model.DBFindSetDetail;
import com.uas.erp.model.DBFindSetGrid;
import com.uas.erp.model.DBFindSetUI;
import com.uas.erp.model.DataListCombo;
import com.uas.erp.model.Dbfind;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.Employee;
import com.uas.erp.model.GridColumns;
import com.uas.erp.model.GridFields;
import com.uas.erp.model.GridPanel;
import com.uas.erp.service.common.DbfindService;

@Service("dbfindService")
public class DbfindServiceImpl implements DbfindService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private DbfindSetDao dbfindSetDao;
	@Autowired
	private DbfindSetUiDao dbfindSetUiDao;
	@Autowired
	private DbfindSetGridDao dbfindSetGridDao;
	@Autowired
	private DetailGridDao detailGridDao;
	@Autowired
	private DataListComboDao dataListComboDao;
	//@Autowired 
	//private Jpoint jpoint;
	@Override
	public GridPanel getDbfindGridByCaller(String caller, String condition, String orderby, int page, int pageSize, String language,
			boolean isCloud) {
		String master = SpObserver.getSp();
		DBFindSet dbFindSet = null;
		if (isCloud) {
			SpObserver.putSp(Constant.UAS_CLOUD);
			dbFindSet = dbfindSetDao.getDbfind(caller, Constant.UAS_CLOUD);
			SpObserver.putSp(master);
		} else
			dbFindSet = dbfindSetDao.getDbfind(caller, master);
		GridPanel gridPanel = new GridPanel();
		List<GridFields> fields = new ArrayList<GridFields>();// grid
		// store的字段fields
		List<GridColumns> columns = new ArrayList<GridColumns>();// grid的列信息columns
		StringBuffer callers = new StringBuffer("'" + caller + "',");
		if (dbFindSet.getDs_dlccaller() != null && !"".equals(dbFindSet.getDs_dlccaller())) {
			String[] callerArr = dbFindSet.getDs_dlccaller().split(",");
			for (int i = 0; i < callerArr.length; i++) {
				callers.append("'").append(callerArr[i].toString()).append("',");
			}
		}
		List<DataListCombo> combos = dataListComboDao.getComboxsByCallers(callers.substring(0, callers.length() - 1).toString());
		for (DBFindSetDetail detail : dbFindSet.getDbFindSetDetails()) {
			// 从数据库表dbfindSetDetail的数据，通过自定义的构造器，转化为extjs识别的fields格式，详情可见GridFields的构造函数
			fields.add(new GridFields(detail));
			columns.add(new GridColumns(detail, combos));
		}
		gridPanel.setGridColumns(columns);
		gridPanel.setGridFields(fields);
		String f = dbFindSet.getDs_fixedcondition();
		if (StringUtil.hasText(f)) {
			condition = StringUtil.hasText(condition) ? "(" + condition + ") and (" + baseDao.parseEmpCondition(f) + ")" : f;
		}
		if(dbFindSet.getDs_nolimit()==null || dbFindSet.getDs_nolimit()!=-1){
			condition = appendCondition(dbFindSet.getDs_tablename(), condition);
		}
		gridPanel.setDataString(baseDao.getDataStringByDbfindSet(dbFindSet, condition, orderby, page, pageSize));
		gridPanel.setAllowreset(dbFindSet.getDs_allowreset() != 0);
		gridPanel.setAutoHeight(dbFindSet.getDs_autoheight() != 0);
		return gridPanel;
	}

	@Override
	public int getCountByCaller(String caller, String condition, boolean isCloud , boolean autoDbfind) {
		String master = SpObserver.getSp();
		DBFindSet dbFindSet = null;
		if (isCloud) {
			SpObserver.putSp(Constant.UAS_CLOUD);
			dbFindSet = dbfindSetDao.getDbfind(caller, Constant.UAS_CLOUD);
			SpObserver.putSp(master);
		} else
			dbFindSet = dbfindSetDao.getDbfind(caller, master);
		if(!autoDbfind && dbFindSet.getDs_isfast() !=null && dbFindSet.getDs_isfast() != 0){
			return -1;
		}
		String f = dbFindSet.getDs_fixedcondition();
		if (f != null && !f.trim().equals("")) {
			condition = !StringUtil.hasText(condition) ? f : "(" + condition + ") and (" + baseDao.parseEmpCondition(f) + ")";
		}
		if(dbFindSet.getDs_nolimit()==null || dbFindSet.getDs_nolimit()!=-1){
			condition = appendCondition(dbFindSet.getDs_tablename(), condition);
		}
		return baseDao.getCount(dbFindSet.getSql(condition));
	}

	public GridPanel getDbfindGridByField(String caller, String field, String condition, int page, int pageSize, boolean isCloud) {
		DBFindSetUI dbFindSetUI = null;
		String master = SpObserver.getSp();
		if (isCloud) {
			SpObserver.putSp(Constant.UAS_CLOUD);
			dbFindSetUI = dbfindSetUiDao.getDbFindSetUIByField(caller, field, Constant.UAS_CLOUD);
			SpObserver.putSp(master);
		} else
			dbFindSetUI = dbfindSetUiDao.getDbFindSetUIByField(caller, field, master);

		GridPanel gridPanel = new GridPanel();
		List<GridFields> fields = new ArrayList<GridFields>();// grid
		List<GridColumns> columns = new ArrayList<GridColumns>();// grid的列信息columns
		List<Dbfind> dbfinds = new ArrayList<Dbfind>();
		String[] names = dbFindSetUI.getDs_findtoui().split("#");
		String[] captions = dbFindSetUI.getDs_dbcaption().split("#");
		String[] widths = dbFindSetUI.getDs_dbwidth().split("#");
		String callers = dbFindSetUI.getDs_dlccaller();
		String type = dbFindSetUI.getDs_type();
		if (type != null) {
			List<DataListCombo> combos = new ArrayList<DataListCombo>();
			if (callers != null) {
				String[] callerArr = dbFindSetUI.getDs_dlccaller().split(",");
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < callerArr.length; i++) {
					sb.append("'").append(callerArr[i].toString()).append("',");
				}
				callers = sb.substring(0, sb.length() - 1).toString();
				combos = dataListComboDao.getComboxsByCallers(callers);
			}
			String[] ff = null, typeArr = null;
			for (int i = 0, len = names.length; i < len; i++) {
				ff = names[i].split(",");
				typeArr = type.split("#");
				fields.add(new GridFields(ff[0], typeArr[i]));
				columns.add(new GridColumns(ff[0], captions[i], Integer.parseInt(widths[i]), typeArr[i], combos));
				dbfinds.add(new Dbfind(ff.length > 1 ? ff[1] : null, ff[0]));
			}
		} else {
			String[] ff = null;
			for (int i = 0, len = names.length; i < len; i++) {
				ff = names[i].split(",");
				fields.add(new GridFields(ff[0]));
				columns.add(new GridColumns(ff[0], captions[i], Integer.parseInt(widths[i])));
				dbfinds.add(new Dbfind(ff.length > 1 ? ff[1] : null, ff[0]));
			}
		} // store的字段fields
		gridPanel.setGridColumns(columns);
		gridPanel.setGridFields(fields);
		gridPanel.setDbfinds(dbfinds);
		condition = condition.replace(field, dbFindSetUI.getDs_likefield());
		String def = dbFindSetUI.getDs_uifixedcondition();
		if (StringUtil.hasText(def)) {
			def = baseDao.parseEmpCondition(def);
			condition = StringUtil.hasText(condition) ? condition + " and (" + def + ")" : def;
		}
		if(dbFindSetUI.getDs_nolimit()==null || dbFindSetUI.getDs_nolimit()!=-1){
			condition = appendCondition(dbFindSetUI.getDs_whichdbfind(), condition);
		}		
		gridPanel.setDataString(baseDao.getDataStringByDbfindSetUi(dbFindSetUI, condition, page, pageSize));
		gridPanel.setAllowreset(dbFindSetUI.getDs_allowreset() != 0);
		gridPanel.setAutoHeight(dbFindSetUI.getDs_autoheight() != 0);
		return gridPanel;
	}

	@Override
	public int getCountByField(String caller, String field, String condition, boolean isCloud , boolean autoDbfind) {
		DBFindSetUI dbFindSetUI = null;
		String master = SpObserver.getSp();
		if (isCloud) {
			SpObserver.putSp(Constant.UAS_CLOUD);
			dbFindSetUI = dbfindSetUiDao.getDbFindSetUIByField(caller, field, Constant.UAS_CLOUD);
			SpObserver.putSp(master);
		} else
			dbFindSetUI = dbfindSetUiDao.getDbFindSetUIByField(caller, field, SpObserver.getSp());
		if(!autoDbfind && dbFindSetUI.getDs_isfast() !=null && dbFindSetUI.getDs_isfast() != 0){
			return -1;
		}
		condition = condition.replace(field, dbFindSetUI.getDs_likefield());
		String def = dbFindSetUI.getDs_uifixedcondition();
		if (StringUtil.hasText(def)) {
			condition = StringUtil.hasText(condition) ? condition + " and (" + baseDao.parseEmpCondition(def) + ")" : def;
		}//主要涉及到一些申请放大镜 不受权限管控
		if(dbFindSetUI.getDs_nolimit()==null || dbFindSetUI.getDs_nolimit()!=-1){
			condition = appendCondition(dbFindSetUI.getDs_whichdbfind(), condition);
		}		
		return baseDao.getCount(dbFindSetUI.getSql(condition));
	}

	@Override
	public JSONObject getDbFindSetUIByField(String caller, String field, String condition, boolean isCloud) {
		JSONObject obj = new JSONObject();
		DBFindSetUI dbFindSetUI = dbfindSetUiDao.getDbFindSetUIByField(caller, field, SystemSession.getUser().getEm_master());
		if (dbFindSetUI != null) {
			String str = dbFindSetUI.getDs_uifixedcondition() == null ? "" : dbFindSetUI.getDs_uifixedcondition();
			obj.put("formdata",
					"{\"ds_caption\":\"" + dbFindSetUI.getDs_caption() + "\",\"ds_whichdbfind\":\"" + dbFindSetUI.getDs_whichdbfind()
					+ "\",\"ds_likefield\":\"" + dbFindSetUI.getDs_likefield() + "\",\"ds_uifixedcondition\":\"" + str
					+ "\",\"ds_id\":\"" + dbFindSetUI.getDs_id() + "\",\"ds_caller\":" + "\""
					+ (dbFindSetUI.getDs_caller() == null ? "" : dbFindSetUI.getDs_caller()) + "\"" + ",\"ds_whichui\":\""
					+ dbFindSetUI.getDs_whichui() + "\",\"ds_tables\":\""
					+ (dbFindSetUI.getDs_tables() == null ? "" : dbFindSetUI.getDs_tables()) + "\",\"ds_orderby\":\""
					+ (dbFindSetUI.getDs_orderby() == null ? "" : dbFindSetUI.getDs_orderby()) + "\",\"ds_dlccaller\":\""
					+ (dbFindSetUI.getDs_dlccaller() == null ? "" : dbFindSetUI.getDs_dlccaller()) + "\",\"ds_error\":\""
					+ (dbFindSetUI.getDs_error() == null ? "" : dbFindSetUI.getDs_error()) + "\"}");
			obj.put("griddata", dbFindSetUI.getDeployData());
			String table = dbFindSetUI.getDs_tables() == null ? "" : dbFindSetUI.getDs_tables();
			String tables = "";
			for (int i = 0; i < table.split("#").length; i++) {
				tables += "'" + table.split("#")[i] + "',";
			}
			tables = tables.substring(0, tables.length() - 1).toUpperCase();
			SqlRowList sl = baseDao.queryForRowSet("select lower(column_name),comments from User_Col_Comments where upper(table_name) in ("
					+ tables + ")");
			List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
			while (sl.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("value", sl.getString(1));
				map.put("display", sl.getString(1)+" "+(sl.getString(2)==null?"无":sl.getString(2)));
				maps.add(map);
			}
			obj.put("fields", BaseUtil.parseGridStore2Str(maps));
		}
		return obj;
	}

	@Override
	public String getDbFindFields(String table) {
		String str = "";
		// 这里将table统一转成大写
		for (int i = 0; i < table.split("#").length; i++) {
			str += "'" + table.split("#")[i] + "',";
		}
		str = str.substring(0, str.length() - 1).toUpperCase();
		SqlRowList sl = baseDao
				.queryForRowSet("select lower(a.column_name),comments from  User_Tab_Columns a left join  User_Col_Comments b on A.Table_Name=B.Table_Name and A.Column_Name=B.column_name where a.table_name in ("
						+ str + ")");
		List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
		while (sl.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("value", sl.getString(1));
			map.put("display", sl.getString(1)+" "+(sl.getString(2)==null?"无":sl.getString(2)));
			maps.add(map);
		}
		return BaseUtil.parseGridStore2Str(maps);
	}

	@Override
	public void deleteDbFindField(String field, int id) {
		DBFindSetUI dBFindSetUI = dbfindSetUiDao.getDbFindSetUIById(id);
		String updateStr = null;
		int index = 0;
		if (dBFindSetUI != null) {
			String ds_findtoui = dBFindSetUI.getDs_findtoui();
			for (int i = 0; i < ds_findtoui.split("#").length && index == 0; i++) {
				if (ds_findtoui.split("#")[i].contains(field)) {
					index = i;
				}
			}
			if (index > 0) {
				updateStr = "ds_findtoui='" + ds_findtoui.replace("#" + ds_findtoui.split("#")[index], "") + "',ds_dbcaption='"
						+ dBFindSetUI.getDs_dbcaption().replace("#" + dBFindSetUI.getDs_dbcaption().split("#")[index], "")
						+ "',ds_dbwidth='" + dBFindSetUI.getDs_dbwidth().replace("#" + dBFindSetUI.getDs_dbwidth().split("#")[index], "")
						+ "'";
			} else if (index == 0 && ds_findtoui.split("#").length > 1) {
				updateStr = "ds_findtoui='" + ds_findtoui.replace(ds_findtoui.split("#")[index] + "#", "") + "',ds_dbcaption='"
						+ dBFindSetUI.getDs_dbcaption().replace(dBFindSetUI.getDs_dbcaption().split("#")[index] + "#", "")
						+ "',ds_dbwidth='" + dBFindSetUI.getDs_dbwidth().replace(dBFindSetUI.getDs_dbwidth().split("#")[index] + "#", "")
						+ "'";
			} else {
				// 不存在 则删除该DBFind
				baseDao.deleteById("dbfindSetUI", "ds_id", id);
			}
		}
		baseDao.updateByCondition("dbfindSetUI", updateStr, "ds_id='" + id + "'");
	}

	@Override
	@CacheEvict(value = "dbfindsetui", allEntries = true)
	public int saveDbFindSetUI(String caller, String formStore, String gridStore) {
		Map<Object, Object> formmap = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gridmaps = BaseUtil.parseGridStoreToMaps(gridStore);
		String dbfindtoui = "";
		String dbcaption = "";
		String dbwidth = "";
		String type = "";
		String sql = "";
		StringBuffer test = new StringBuffer("select ");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < gridmaps.size(); i++) {
			Map<Object, Object> map = gridmaps.get(i);
			dbfindtoui += map.get("ds_findtoui_f").toString() + "," + map.get("ds_findtoui_i").toString();
			dbcaption += map.get("ds_dbcaption");
			dbwidth += map.get("ds_dbwidth");
			type += map.get("ds_type");
			if (i < gridmaps.size() - 1) {
				dbfindtoui += "#";
				dbcaption += "#";
				dbwidth += "#";
				type += "#";
			}
			sb.append(map.get("ds_findtoui_f").toString()).append(",");
		}
		try {
			if (!sb.toString().toLowerCase().contains(formmap.get("ds_likefield").toString().toLowerCase())) {
				sb.append(formmap.get("ds_likefield").toString()).append(",");
			}
			test.append(sb.substring(0, sb.length() - 1)).append(" from " + formmap.get("ds_whichdbfind").toString());
			if (formmap.get("ds_uifixedcondition") != null && !formmap.get("ds_uifixedcondition").equals("")) {
				String condition = baseDao.parseEmpCondition(formmap.get("ds_uifixedcondition").toString());
				test.append(" where 1=2 and " + condition);
			} else {
				test.append(" where 1=2 ");
			}
			if (formmap.get("ds_orderby") != null && !formmap.get("ds_orderby").equals("")) {
				test.append(" " + formmap.get("ds_orderby"));
			}
			baseDao.execute(test.toString());
		} catch (Exception exception) {
			BaseUtil.showError("配置有误!请检查【取值字段】、【查找表名】、【关联字段】、【条件】及【排序】.");
		}
		Object id = formmap.get("ds_id");
		if (id != null && !id.equals("")) {
			// 说明存在配置 则更新
			formmap.remove("ds_caller");
			sql = SqlUtil.getUpdateSqlByFormStore(formmap, "dbfindsetui", "ds_id");
			sql = sql.split("WHERE")[0] + ",ds_findtoui='" + dbfindtoui + "',ds_dbcaption='" + dbcaption + "',ds_dbwidth='" + dbwidth
					+ "',ds_type='" + type + "' WHERE " + sql.split("WHERE")[1];
		} else {
			// 说明是插入
			id = baseDao.getSeqId("DBFINDSETUI_SEQ");
			formmap.remove("ds_id");
			formmap.put("ds_id", id);
			formmap.put("ds_caller", caller);
			formmap.put("ds_findtoui", dbfindtoui);
			formmap.put("ds_dbcaption", dbcaption);
			formmap.put("ds_dbwidth", dbwidth);
			formmap.put("ds_type", type);
			sql = SqlUtil.getInsertSqlByMap(formmap, "dbfindsetui");
		}
		baseDao.execute(sql);
		baseDao.execute("update dbfindsetui set ds_whichdbfind=upper(ds_whichdbfind),ds_likefield=upper(ds_likefield),ds_tables=upper(ds_tables) where ds_id="
				+ id);
		return Integer.parseInt(id.toString());
	}

	@Override
	public void deleteDbfindSetUI(int id) {
		dbfindSetUiDao.deleteDbFindSetUIById(id);
	}

	@Override
	public List<DBFindSetGrid> getDbFindSetGridByCaller(String caller, String field) {
		List<DBFindSetGrid> details = dbfindSetGridDao.getDbFindSetGridsByCaller(caller);
		return details;
	}

	@Override
	public JSONObject getDbFindSetGridFieldsByCallerAndFields(Employee employee, String caller, String field) {
		JSONObject obj = new JSONObject();
		DBFindSet dbFindSet = null;
		if (field != null && !field.equals("")) {
			String DbCaller = null;
			String LinkKey = null;
			List<DetailGrid> details = detailGridDao.getDetailGridsByCaller(caller, SpObserver.getSp());
			for (int i = 0; i < details.size(); i++) {
				if (details.get(i).getDg_field().equals(field)) {
					String functionname = details.get(i).getDg_findfunctionname();
					if (StringUtil.hasText(functionname)) {
						DbCaller = functionname.split("[|]")[0];
						LinkKey = functionname.split("[|]")[1];
						dbFindSet = dbfindSetDao.getDbfind(DbCaller, SpObserver.getSp());
					}
					break;
				}
			}
			if (dbFindSet != null) {
				List<DBFindSetDetail> SetDetails = dbFindSet.getDbFindSetDetails();
				List<DBFindSetGrid> lists = new ArrayList<DBFindSetGrid>();
				obj.put("details", SetDetails);
				List<DBFindSetGrid> dbfindsetgrids = dbfindSetGridDao.getDbFindSetGridsByCaller(caller);
				// 只取匹配的字段显示 否则不显示
				for (int j = 0; j < dbfindsetgrids.size(); j++) {
					String ds_dbfindfield = dbfindsetgrids.get(j).getDs_dbfindfield();
					for (DBFindSetDetail dbdetail : SetDetails) {
						for (int k = 0; k < (ds_dbfindfield == null ? "" : ds_dbfindfield).split(";").length; k++) {
							if (ds_dbfindfield.split(";")[k].equals(dbdetail.getDd_fieldname())
									|| dbdetail.getDd_fieldname().contains(" " + ds_dbfindfield.split(";")[k])) {
								lists.add(dbfindsetgrids.get(j));
								break;
							}
						}
					}
				}
				obj.put("dbfindsetgrid", lists);
				obj.put("dbcaller", DbCaller);
				obj.put("dbtablename", dbFindSet.getDs_tablename());
				obj.put("linkkey", LinkKey);

			}
		} else {
			dbFindSet = dbfindSetDao.getDbfind(caller, SpObserver.getSp());
			if (dbFindSet != null) {
				List<DBFindSetDetail> SetDetails = dbFindSet.getDbFindSetDetails();
				List<DBFindSetGrid> lists = new ArrayList<DBFindSetGrid>();
				obj.put("details", SetDetails);
				List<DBFindSetGrid> dbfindsetgrids = dbfindSetGridDao.getDbFindSetGridsByCaller(caller);
				// 只取匹配的字段显示 否则不显示
				for (int j = 0; j < dbfindsetgrids.size(); j++) {
					String ds_dbfindfield = dbfindsetgrids.get(j).getDs_dbfindfield();
					for (DBFindSetDetail dbdetail : SetDetails) {
						// 不能用contains
						for (int k = 0; k < (ds_dbfindfield == null ? "" : ds_dbfindfield).split(";").length; k++) {
							if (ds_dbfindfield.split(";")[k].equals(dbdetail.getDd_fieldname())) {
								lists.add(dbfindsetgrids.get(j));
								break;
							}
						}
					}
				}
				obj.put("dbfindsetgrid", lists);
			}

		}
		return obj;
	}

	@Override
	@CacheEvict(value = { "dbfind", "gridpanel" }, allEntries = true)
	public void saveDbFindSetGrid(String caller, String field, String table, String dgfield, String gridStore) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(gridStore);
		List<String> sqls = new ArrayList<String>();
		// 页面的caller
		String BaseCaller = null;
		try {
			String test = "select " + field + " from " + table;
			baseDao.execute(test);
		} catch (Exception exception) {
			BaseUtil.showError("配置有误!请检查【关联字段名】与DBFindSetCaller是否匹配");
		}
		int detno = 1;
		if (maps.size() > 0)
			BaseCaller = maps.get(0).get("ds_caller").toString();
		// 获得最大序号
		SqlRowList sl = baseDao.queryForRowSet("select max(ds_detno) from  DBFindSetGrid where ds_caller=?", BaseCaller);
		if (sl.next()) {
			if (sl.getObject(1) != null) {
				detno = sl.getInt(1);
			}
		}
		for (int i = 0; i < maps.size(); i++) {
			if (maps.get(i).get("ds_id") != null && !maps.get(i).get("ds_id").equals("null") && !maps.get(i).get("ds_id").equals("")) {
				// 更新操作
				Map<Object, Object> map = maps.get(i);
				map.remove("button");
				sqls.add(SqlUtil.getUpdateSqlByFormStore(map, "DBFindSetGrid", "ds_id"));
			} else {
				int id = baseDao.getSeqId("DBFINDSETGRID_SEQ");
				detno++;
				Map<Object, Object> map = maps.get(i);
				map.remove("ds_id");
				map.remove("button");
				map.put("ds_id", id);
				map.put("ds_detno", detno);
				sqls.add(SqlUtil.getInsertSqlByMap(map, "DBFindSetGrid"));
			}
		}
		baseDao.execute(sqls);

		baseDao.updateByCondition("DetailGrid", "dg_findfunctionname='" + caller + "|" + field + "'", "dg_caller='" + BaseCaller
				+ "' AND dg_field='" + dgfield + "'");
	}

	@Override
	public void deleteDBFindSetGrid(int id) {
		dbfindSetGridDao.deleteDbFindSetGridById(id);
	}

	private String appendCondition(String tableName, String condition) {
		// 设置的约束关系
		Employee employee = SystemSession.getUser();
		String limitcondition = baseDao.getLimitCondition(tableName, employee.getEm_id());
		if ((condition == null || "".equals(condition)) && !"".equals(limitcondition))
			condition = limitcondition;
		else
			condition += !"".equals(limitcondition) ? (" AND " + limitcondition) : "";
			return condition;
	}

	@Override
	public String getDlccallerByTable(String table, String fields) {
		String ds_dlccaller = "";
		SqlRowList sl = baseDao
				.queryForRowSet("SELECT wm_concat(distinct dlc_caller) ds_dlccaller FROM (SELECT ROW_NUMBER() OVER(PARTITION BY DLC_FIELDNAME ORDER BY dlc_id desc) rn, "
						+ "datalistcombo.* FROM datalistcombo  where dlc_caller in(select fo_caller from form where upper(fo_table) in ("
						+ table + ")) " + "and dlc_fieldname in (" + fields + ") and DLC_DISPLAY is not null) WHERE rn = 1");
		if (sl.next()) {
			if (sl.getString("ds_dlccaller") != null) {
				ds_dlccaller = sl.getString("ds_dlccaller");
			}
		}
		return ds_dlccaller;
	}

	/**
	 * 根据条件联想搜索数据
	 * 
	 * @param table 搜索的表名
	 * @param field 所要搜索的字段（如："x,x,x"）
	 * @param condition  文本框输入的搜索内容
	 * @param configSearchCondition  放大镜配置的条件
	 * @param name  配置放大镜的字段名称
	 * @param caller  所在界面的caller
	 * @param type  放大镜所在的位置（form或者grid）
	 * @param searchTpl  是否需要加载tpl
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<String> getSearchData(String table,String field,String condition,String configSearchCondition,String name,String caller,String type,String searchTpl) {
		String searchfield="";//查询的字段
		String sql="";
		String newcondition=condition;//输入的模糊查询条件
		String searchTable=table;//查询的表
		List<String> result=new ArrayList<String>();
		List<Map<Object, Object>> maps=new ArrayList<Map<Object, Object>>();
		//加载tpl模板数据
		if("true".equals(searchTpl)){//判断是否需要加载tpl模板
			if("form".equals(type)){
				Object tpl="";
				String tablename="";				
				if(baseDao.checkIf("dbfindsetui", "(DS_caller='"+caller+"' or ds_caller is null) and DS_WHICHUI='"+name+"'")){
					DBFindSetUI dbFindSetUI= dbfindSetUiDao.getDbFindSetUIByField(caller, name, SpObserver.getSp());
					if(dbFindSetUI!=null){
						if(dbFindSetUI.getDs_enablelikes()!=null && dbFindSetUI.getDs_enablelikes()==-1){
							tablename= getLikeTable( dbFindSetUI.getDs_whichdbfind().trim().toUpperCase());			
							String searchCondition=dbFindSetUI.getDs_uifixedcondition();
							if(StringUtil.hasText(searchCondition)){
								searchCondition = baseDao.parseEmpCondition(searchCondition);
							}
							configSearchCondition=StringUtil.hasText(searchCondition)?configSearchCondition+ " and "+searchCondition:configSearchCondition;
							String rleaseField=dbFindSetUI.getDs_likefield();
							tpl=baseDao.getFieldDataByCondition("DBFIND$PROP", "tpl_conf", "table_name='"+tablename+"'");
							searchTable= dbFindSetUI.getDs_whichdbfind();					
							result.add((tpl==null?"":tpl).toString());
							result.add(searchTable);
							result.add(searchCondition);
							result.add(rleaseField);
							if(!"".equals(tpl) && tpl!=null){
								String gettpl="";
								List<Map<Object, Object>> datatpl=JacksonUtil.fromJsonArray(tpl.toString());
								for(Map<Object, Object> data2:datatpl){
									gettpl=gettpl+data2.get("field")+",";
								}
								field=gettpl.substring(0,gettpl.length()-1);
							}
						}else return result;
					}
				}else{
					return result;
				}
			}else if("grid".equals(type)){
				Object tpl="";
				String tablename="";
				if(baseDao.checkIf("dbfindset", "DS_caller='"+caller+"'")){
					DBFindSet dbFindSet=dbfindSetDao.getDbfind(caller,SpObserver.getSp());
					if(dbFindSet!=null && dbFindSet.getDs_enablelikes()!=null && dbFindSet.getDs_enablelikes()==-1){
						searchTable=dbFindSet.getDs_tablename().trim().toUpperCase();
						tablename= getLikeTable(searchTable);			
						tpl=baseDao.getFieldDataByCondition("DBFIND$PROP", "tpl_conf", "table_name='"+tablename+"'");
						String searchCondition=dbFindSet.getDs_fixedcondition()!=null?dbFindSet.getDs_fixedcondition():"";
						configSearchCondition="".equals(searchCondition)?configSearchCondition:configSearchCondition+" and "+searchCondition;
						result.add((tpl==null?"":tpl).toString());
						result.add(searchTable);
						result.add(searchCondition);
						result.add("");
						if(!"".equals(tpl) && tpl!=null){
							String gettpl="";
							List<Map<Object, Object>> datatpl=JacksonUtil.fromJsonArray(tpl.toString());
							for(Map<Object, Object> data2:datatpl){
								gettpl=gettpl+data2.get("field")+",";
							}
							field=gettpl.substring(0,gettpl.length()-1);
						}
					}else{
						return result;
					}
				}else{
					return result;
				}
			}
		}
		//获取搜索数据
		if(searchTable ==null || "".equals(searchTable)){
			BaseUtil.showError("联想查询的表不能为空!");
		}else{
			if(field.contains(",")){
				String[] fields=field.split(",");
				for(int i=0;i<fields.length-1;i++){
					if(i==0){
						searchfield="concat("+fields[0]+",'#'||"+fields[1]+")";//多个字段查询时输入相连字段开头和结尾字符也会查出结果，所以用'#'隔开不同字段查出的结果
					}else{
						searchfield="concat("+searchfield+",'#'||"+fields[i+1]+")";
					}
				}
				if(condition.contains("'")){
					newcondition=condition.replaceAll("'", "''");
				}
				if(condition.contains("%")){
					newcondition=condition.replaceAll("%", "/%");
					sql="select "+field+" from "+searchTable+" where upper("+searchfield+") like '%"+newcondition.toUpperCase()+"%' ESCAPE '/' and RowNum<50 and ("+configSearchCondition+")";
				}else{
					sql="select "+field+" from "+searchTable+" where upper("+searchfield+") like '%"+newcondition.toUpperCase()+"%' and RowNum<50 and ("+configSearchCondition+")";
				}
				SqlRowList result1=baseDao.queryForRowSet(sql);
				while(result1.next()){
					HashMap<Object,Object> map=new HashMap<Object,Object>();
					for(int j=0;j<fields.length;j++){
						map.put(fields[j], result1.getGeneralString(fields[j])==""?"-":result1.getGeneralString(fields[j]));
					}
					maps.add(map);
				}
				String res=JacksonUtil.toJsonArray(maps);
				result.add(res);
			}else if(!("".equals(field)) && field!=null){
				if(condition.contains("'")){
					newcondition=condition.replaceAll("'", "''");
				}
				if(condition.contains("%")){
					newcondition=condition.replaceAll("%", "/%");
					sql="select "+field+" from "+searchTable+" where upper("+field+") like '%"+newcondition.toUpperCase()+"%' ESCAPE '/' and RowNum<50 and ("+configSearchCondition+")";
				}else{
					sql="select "+field+" from "+searchTable+" where upper("+field+") like '%"+newcondition.toUpperCase()+"%' and RowNum<50 and ("+configSearchCondition+")";
				}
				System.out.println("----");
				System.out.println(sql);
				SqlRowList result1=baseDao.queryForRowSet(sql);
				while(result1.next()){
					HashMap<Object,Object> map=new HashMap<Object,Object>();
					map.put(field, result1.getGeneralString(field)==""?"-":result1.getGeneralString(field));
					maps.add(map);
				}
				String res=JacksonUtil.toJsonArray(maps);
				result.add(res);
			}else{
				result.add(" ");
			}
		}
		return result;
	}
	private String getLikeTable(String searchTable){
		String relationType=null;
		if(searchTable.contains(" LEFT ")){
			relationType="LEFT";
		}else if(searchTable.contains(" RIGHT ")){
			relationType="RIGHT";					
		}
		if(relationType!=null) return  searchTable.substring(0, searchTable.indexOf(relationType)).trim();
		else return searchTable;
	}
	
	public List<Map<String, Object>> getComboBoxTriggerData(String id,String text){
		String sqlString; 		
		if("".equals(text)||text==null){
			sqlString ="select * from  jpoint where Userid_='"+id+"'";
		}else {
			String newtext=text.replaceAll("'", "''");
			 sqlString ="select * from  jpoint where Userid_='"+id+"' and Text_ like '%"+newtext+"%'";
		}
		return baseDao.getJdbcTemplate().queryForList(sqlString);
		
	}

	@Override
	public Boolean saveToCommonWords(String value) {
		// TODO Auto-generated method stub
		Employee employee=SystemSession.getUser();
		int em_id=employee.getEm_id();
		String valueString=value.replaceAll("\'", "''");
		String sql="insert into jpoint(id_,text_,userid_) values (jpoint_seq.nextval,'"+valueString+"','"+em_id+"')";
		Boolean	flag=baseDao.checkByCondition("jpoint", "text_='"+valueString+"' and userid_ ='"+em_id+"'");
		if(!flag){
			BaseUtil.showError("您已经保存过该常用语！请不要重复保存！");
		}
		try {
			baseDao.execute(sql);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Boolean deleteCommonWords(String id) {
		// TODO Auto-generated method stub
		try {
			baseDao.deleteByCondition("jpoint", "Id_='"+id+"'");
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}
}
