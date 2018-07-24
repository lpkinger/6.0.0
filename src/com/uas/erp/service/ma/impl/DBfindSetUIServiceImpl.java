package com.uas.erp.service.ma.impl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DbfindSetDao;
import com.uas.erp.dao.common.DbfindSetUiDao;
import com.uas.erp.model.DBFindSetUI;
import com.uas.erp.model.Employee;
import com.uas.erp.service.ma.DBfindSetUIService;

@Service("DBfindSetUIService")
public class DBfindSetUIServiceImpl implements DBfindSetUIService {
	 
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private DbfindSetDao dbfindSetDao;
	@Autowired
	private DbfindSetUiDao dbfindSetUiDao;
	
	@Override
	public JSONObject getDbFindSetUIByField(String id) {
		int ds_id=Integer.parseInt(id);
		JSONObject obj = new JSONObject();
		DBFindSetUI dbFindSetUI= dbfindSetUiDao.getDbFindSetUIById(ds_id);
		if (dbFindSetUI != null) {
			String str = dbFindSetUI.getDs_uifixedcondition() == null ? "" : dbFindSetUI.getDs_uifixedcondition();
			obj.put("formdata","{\"ds_caption\":\"" + dbFindSetUI.getDs_caption() + "\",\"ds_whichdbfind\":\"" + dbFindSetUI.getDs_whichdbfind()
					+"\",\"ds_likefield\":\""+ dbFindSetUI.getDs_likefield() + "\",\"ds_uifixedcondition\":\"" + str+ "\",\"ds_id\":\"" + dbFindSetUI.getDs_id() 
					+ "\",\"ds_caller\":" + "\""+ (dbFindSetUI.getDs_caller() == null ? "" : dbFindSetUI.getDs_caller()) + "\"" + ",\"ds_whichui\":\""+ dbFindSetUI.getDs_whichui() 
					+"\",\"ds_tables\":\"" + (dbFindSetUI.getDs_tables() == null ? "" : dbFindSetUI.getDs_tables())+"\",\"ds_orderby\":\""
					+(dbFindSetUI.getDs_orderby() == null ? "" : dbFindSetUI.getDs_orderby())+"\",\"ds_dlccaller\":\""+(dbFindSetUI.getDs_dlccaller() == null ? "" : dbFindSetUI.getDs_dlccaller())
					+ "\",\"ds_isfast\":\""+(dbFindSetUI.getDs_isfast() == null ? 0 : dbFindSetUI.getDs_isfast())+"\",\"ds_error\":\""+ (dbFindSetUI.getDs_error() == null ? "" : dbFindSetUI.getDs_error()) + "\"}");
			obj.put("griddata", dbFindSetUI.getDeployData());
			String table = dbFindSetUI.getDs_tables()==null ?"":dbFindSetUI.getDs_tables();
			String tables = "";
			for (int i = 0; i < table.split("#").length; i++) {
				tables += "'" + table.split("#")[i] + "',";
			}
			tables = tables.substring(0, tables.length() - 1).toUpperCase();
			SqlRowList sl = baseDao
					.queryForRowSet("select lower(column_name),comments from User_Col_Comments where upper(table_name) in ("
							+ tables + ")");
			List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
			while (sl.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("value", sl.getString(1));
				map.put("display",sl.getString(1));
				maps.add(map);
			}			
			obj.put("fields", BaseUtil.parseGridStore2Str(maps));
		}
		return obj;
		 
		
	}

	@Override
	public int saveDbFindSetUI(String formStore, String gridStore) {
		Map<Object, Object> formmap = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gridmaps = BaseUtil.parseGridStoreToMaps(gridStore);
		String dbfindtoui = "";
		String dbcaption = "";
		String dbwidth = "";
		String type = "";
		String sql = "";
		StringBuffer test=new StringBuffer("select ");
		StringBuffer sb=new StringBuffer();
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
		try{
			if(!sb.toString().toLowerCase().contains(formmap.get("ds_likefield").toString().toLowerCase())){
				sb.append(formmap.get("ds_likefield").toString()).append(",");
			}
			test.append(sb.substring(0,sb.length()-1)).append(" from "+formmap.get("ds_whichdbfind").toString());
			if(formmap.get("ds_uifixedcondition")!=null&&!formmap.get("ds_uifixedcondition").equals("")){
				String condition=parseCondition(formmap.get("ds_uifixedcondition").toString());
				test.append(" where 1=2 and "+condition);
			}else{
				test.append(" where 1=2 ");
			}
			if(formmap.get("ds_orderby")!=null&&!formmap.get("ds_orderby").equals("")){
				test.append(" "+formmap.get("ds_orderby"));
			}
			
			baseDao.execute(test.toString());
		} catch (Exception exception){
			BaseUtil.showError("配置有误!请检查【取值字段】、【查找表名】、【关联字段】、【条件】及【排序】.");
		}
		Object id = formmap.get("ds_id");
		Object field = formmap.get("ds_whichui");
		Object caller = formmap.get("ds_caller");
		if (caller==null||"".equals(caller)) {
			caller = " ";
		}
		if (id != null && !id.equals("")) {
			// 说明存在配置 则更新
			formmap.remove("ds_caller");
			sql = SqlUtil.getUpdateSqlByFormStore(formmap, "dbfindsetui", "ds_id");
			sql = sql.split("WHERE")[0] + ",ds_findtoui='" + dbfindtoui + "',ds_dbcaption='" + dbcaption + "',ds_dbwidth='" + dbwidth
					+"',ds_type='"+type+ "' WHERE " + sql.split("WHERE")[1];
		} else {
			
			int count = baseDao.getCountByCondition("dbfindsetui", "ds_whichui='"+field+"' and nvl(ds_caller,' ')='"+caller+"'");
			if (count>0) {
				BaseUtil.showError("配置重复！当前caller和字段已存在！");
			}
			// 说明是插入
			id = baseDao.getSeqId("DBFINDSETUI_SEQ");
			formmap.remove("ds_id");
			formmap.put("ds_id", id);
			formmap.put("ds_caller", formmap.get("ds_caller"));
			formmap.put("ds_findtoui", dbfindtoui);
			formmap.put("ds_dbcaption", dbcaption);
			formmap.put("ds_dbwidth", dbwidth);
			formmap.put("ds_type", type);
			sql = SqlUtil.getInsertSqlByMap(formmap, "dbfindsetui");
		}
		
		baseDao.execute(sql);
		baseDao.execute("update dbfindsetui set ds_whichdbfind=upper(ds_whichdbfind),ds_likefield=upper(ds_likefield),ds_tables=upper(ds_tables) where ds_id="+id);
		id = baseDao.getFieldValue("dbfindsetui", "ds_id", "ds_whichui='"+field+"' and nvl(ds_caller,' ')='"+caller+"'", Integer.class);
		return Integer.parseInt(id.toString());
		// TODO Auto-generated method stub
		
	}
	public String parseCondition(String condition){
		Employee employee=SystemSession.getUser();
		if(condition.contains("session:em_uu")){
			condition=condition.replace("session:em_uu", employee.getEm_uu().toString());
		}
		if(condition.contains("session:em_id")){
			condition=condition.replace("session:em_id", employee.getEm_id().toString());
		}
		if(condition.contains("session:em_code")){
			condition=condition.replace("session:em_code", "'"+employee.getEm_code().toString()+"'");
		}
		if(condition.contains("session:em_name")){
			condition=condition.replace("session:em_name", "'"+employee.getEm_name().toString()+"'");
		}
		if(condition.contains("session:em_defaulthsid")){
			condition=condition.replace("session:em_defaulthsid",  employee.getEm_defaulthsid().toString() );
		}
		if(condition.contains("session:em_position")){
			condition=condition.replace("session:em_position", "'"+employee.getEm_position().toString()+"'");
		}
		if(condition.contains("session:em_defaultorid")){
			condition=condition.replace("session:em_defaultorid",  employee.getEm_defaultorid().toString() );
		}
		if(condition.contains("session:em_defaultorname")){
			condition=condition.replace("session:em_defaultorname", "'"+employee.getEm_defaultorname().toString()+"'");
		}
		if(condition.contains("session:em_depart")){
			condition=condition.replace("session:em_depart", "'"+employee.getEm_depart().toString()+"'");
		}
		if(condition.contains("session:em_departmentcode")){
			condition=condition.replace("session:em_departmentcode","'"+employee.getEm_departmentcode().toString()+"'" );
		}
		return condition;
	}

	@Override
	public int deleteDbFindSetUI(String id) {
		int intID=Integer.parseInt(id);
		DBFindSetUI dbFindSetUI=dbfindSetUiDao.getDbFindSetUIById(intID);
		if(dbFindSetUI!=null){
			String sqString ="delete from dbfindsetui where  ds_id='"+id+"'";
			
			baseDao.execute(sqString);
			return 0;
		}else {
			return 1;
		}
		
	};

}
