package com.uas.erp.service.ma.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DataDictionaryDao;
import com.uas.erp.model.DataDictionary;
import com.uas.erp.model.DataDictionaryDetail;
import com.uas.erp.model.DataRelation;
import com.uas.erp.model.Page;
import com.uas.erp.service.ma.MADataDictionaryService;

import freemarker.template.utility.Execute;

@Service
public class MADataDictionaryServiceImpl implements MADataDictionaryService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private DataDictionaryDao dataDictionaryDao;
	private static String modify="MODIFY";
	private static String add="ADD";
	private static String drop="DROP";
	private static String rel_col_comments="select A.Column_Name,A.Comments,A.Table_Name,B.Data_Type from Datarelation  LEFT JOIN User_Col_Comments a ON (Datarelation.Table_Name_X=a.Table_Name  and (col_x_1=a.Column_Name or col_x_2=a.Column_Name)) or "
			+ "(Datarelation.Table_Name_y=a.Table_Name  and (col_y_1=a.Column_Name or col_y_2=a.Column_Name)) left join user_tab_cols b on a.table_name=b.table_name and a.column_name=b.column_name where table_name_x=?";
	@Override
	public boolean checkTable(String table) {
		return baseDao.checkByCondition("DataDictionary", "dd_tablename='" + table.toUpperCase() + "'");
	}

	@Override
	public List<DataDictionaryDetail> getDataDictionary(String table) {
		return baseDao.getDataDictionaryDetails(table);
	}

	public List<DataDictionary> getDataDictionaries(String tables) {
		String[] tabs = tables.split(",");
		List<DataDictionary> dictionaries = new ArrayList<DataDictionary>();
		for (String tab : tabs) {
			dictionaries.add(dataDictionaryDao.getDataDictionary(tab));
		}
		return dictionaries;
	}

	@Override
	public Page<DataDictionary> getPageDataDictionary(final String query, final int page, final int start, final int limit) {
		final String condition = "table_name like '%" + query.toUpperCase() + "%' or comments like '%" + query + "%'";
		Page<DataDictionary> p = new Page<DataDictionary>() {

			@Override
			public int getTotalCount() {
				return baseDao.getCountByCondition("User_Tab_Comments", condition);
			}

			@Override
			public List<DataDictionary> getTarget() {
				return baseDao.getJdbcTemplate().query(
						"SELECT * FROM ( SELECT A.*, ROWNUM RN FROM (SELECT table_name,comments FROM User_Tab_Comments where " + condition
						+ ") A WHERE ROWNUM <= ? ) WHERE RN >= ? order by table_name",
						new BeanPropertyRowMapper<DataDictionary>(DataDictionary.class), page * limit - 1, start);
			}
		};
		return p;
	}

	@Override
	public void alter(String col_update, String col_create, String col_remove, String ind_update, String ind_create, String ind_remove, String formStore,String gridStore) {
		// TODO Auto-generated method stub
		List<String>sqls=new ArrayList<String>();
		String sql=null;
		String tablename=null;
		Object obj_id=null;
		Object obj_name=null;
		Object obj_comments=null;
		if(formStore!=null){
			Map<Object,Object> store=BaseUtil.parseFormStoreToMap(formStore);
			obj_id=store.get("object_id");
			obj_name=store.get("object_name");
			obj_comments=store.get("comments");
			if(obj_id!=null && !obj_id.equals("")){
				Object[]tabinfo=baseDao.getFieldsDataByCondition("USER_OBJECTS LEFT JOIN User_Tab_Comments ON OBJECT_NAME=User_Tab_Comments.Table_Name", new String[]{"OBJECT_NAME","COMMENTS"},"OBJECT_ID="+obj_id+" AND OBJECT_TYPE='TABLE'");
				tablename=String.valueOf(tabinfo[0]);
				if(!tabinfo[0].equals(obj_name)){
					if(existViews(tablename))BaseUtil.showError(tablename+" 存在相应的视图不能修改!");
					sqls.add("ALTER TABLE "+tablename+" RENAME TO "+obj_name);
					tablename=String.valueOf(obj_name);
				}
				if(!obj_comments.equals(tabinfo[1])){
					sqls.add("COMMENT ON TABLE "+tablename+" IS '"+obj_comments+"'");
				}
				if(sqls.size()>0){
					baseDao.execute(sqls);
					log(tablename, modify, sqls.toString());
				}
				if(col_update!=null){
					List<Map<Object, Object>> maps=BaseUtil.parseGridStoreToMaps(col_update);
					for(Map<Object,Object> map:maps){
						sqls.clear();
						if("VARCHAR2".equals(map.get("data_type"))){
							sql="alter table "+tablename+" modify "+map.get("column_name")+" VARCHAR2("+map.get("data_length")+")";
						}else 
							sql="alter table "+tablename+" modify "+map.get("column_name")+" "+map.get("data_type");
						if(!CHECKNULLABLECHANGE(String.valueOf(map.get("nullable")),String.valueOf(map.get("column_name")),tablename)){							
							sql+="Y".equals(map.get("nullable"))?" NULL ":" NOT NULL";
						} 
						sql+=getDefaultData(map.get("data_default"));
						sqls.add(sql);
						if(map.get("comments")!=null) sqls.add(getColComments(String.valueOf(map.get("column_name")),tablename,String.valueOf(map.get("comments"))));	 
						if(sqls.size()>0){
							baseDao.execute(sqls);
							log(tablename, modify, sqls.toString());
						}
					}					
				}
				if(col_create!=null){
				List<Map<Object, Object>> maps=BaseUtil.parseGridStoreToMaps(col_create);
				for(Map<Object,Object> map:maps){
					sqls.clear();
					try {
						if("VARCHAR2".equals(map.get("data_type"))){
							sqls.add("alter table "+tablename+" add "+map.get("column_name")+" VARCHAR2("+map.get("data_length")+")");
						}else 
							sqls.add("alter table "+tablename+" add "+map.get("column_name")+" "+map.get("data_type"));
						if(!CHECKNULLABLECHANGE(String.valueOf(map.get("nullable")),String.valueOf(map.get("column_name")),tablename)){							
							sql+="Y".equals(map.get("nullable"))?" NULL ":" NOT NULL";
						} 
						sql+=getDefaultData(map.get("data_default"));
						if(map.get("comments")!=null) sqls.add(getColComments(String.valueOf(map.get("column_name")),tablename,String.valueOf(map.get("comments"))));	 	 
						if(sqls.size()>0){
							baseDao.execute(sqls);
							log(tablename, modify, sqls.toString());
						}
					} catch (Exception e) {
						// TODO: handle exception
						BaseUtil.showError("该字段已存在！");
					}					
				}
				executesql(col_create,tablename);
}
				if(col_remove!=null){
					List<Map<Object, Object>> maps=BaseUtil.parseGridStoreToMaps(col_remove);
					for(Map<Object,Object> map:maps){
						sqls.clear();
						sqls.add("alter table "+tablename+" drop column "+map.get("column_name"));											
						if(sqls.size()>0){
							baseDao.execute(sqls);
							log(tablename, drop, sqls.toString());
						}
					}
				}
				if(ind_create!=null){
					createIndex(ind_create,tablename);
				}
				if(ind_remove!=null){
					List<Map<Object,Object>> maps=BaseUtil.parseGridStoreToMaps(ind_remove);
					for(Map<Object,Object> map:maps){
						sqls.clear();
						sql=" DROP INDEX "+map.get("index_name");           	
						baseDao.execute(sql);
						log(tablename, drop, sql);
					}
				}	
				if(gridStore!=null&& gridStore.length() > 2){
					List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
					List<String> gridsqls = new ArrayList<String>();
					for (Map<Object, Object> map : grid) {
						int count=baseDao.getCount("select count(*) from tab_col_property where tablename_='"+obj_name+"' and colname_='"+map.get("column_name")+"'");
						boolean b=(Boolean) map.get("allowbatchupdate_");
						if(b){
							map.put("allowbatchupdate_", "1");
						}else{
							map.put("allowbatchupdate_", "0");
						}
						if(count>0){
							String gridsql="update tab_col_property set allowbatchupdate_='"+map.get("allowbatchupdate_")+"' where tablename_='"+obj_name+"'and colname_='"+map.get("column_name")+"'";
							gridsqls.add(gridsql);
						}else{
							String gridsql="insert into tab_col_property(tablename_ ,colname_,allowbatchupdate_) values( '"+obj_name+"','"+map.get("column_name")+"','"+map.get("allowbatchupdate_")+"')";
							gridsqls.add(gridsql);
						}
					}
					baseDao.execute(gridsqls);
				}
			}else {
				//创建表
				StringBuffer sb=new StringBuffer();
				sb.append("CREATE TABLE "+obj_name+"(");
				if(col_create==null) BaseUtil.showError("建表至少包含一个非虚拟列!");
				List<Map<Object, Object>> maps=BaseUtil.parseGridStoreToMaps(col_create);
				for(int i=0;i<maps.size();i++){
					Map<Object,Object> map=maps.get(i);
					if("VARCHAR2".equals(map.get("data_type"))){
						sb.append(map.get("column_name")+" VARCHAR2("+map.get("data_length")+")");
					}else 
						sb.append(map.get("column_name")+" "+map.get("data_type"));
					if(map.get("nullable")!=null){
						sb.append("Y".equals(map.get("nullable"))?"":" NOT NULL");
					}
					sb.append(getDefaultData(map.get("data_default")));
					if(i<maps.size()-1) sb.append(",");
					if(map.get("comments")!=null) sqls.add(getColComments(String.valueOf(map.get("column_name")),String.valueOf(obj_name),String.valueOf(map.get("comments"))));	 	 

				}
				sb.append(")");
				baseDao.execute(sb.toString());
				baseDao.execute("COMMENT ON TABLE "+obj_name+" IS '"+obj_comments+"'");
				if(sqls.size()>0){
					baseDao.execute(sqls);
					sb.append(sqls.toString());
				}
				createIndex(ind_create,String.valueOf(obj_name));
			}
		}
	}
	public  void  executesql(String col_create,String tablename){
		List<String>sqls=new ArrayList<String>();
		List<Map<Object, Object>> maps=BaseUtil.parseGridStoreToMaps(col_create);
		List<Object> masters = baseDao.getFieldDatasByCondition(getDefaultSob()+".master", "ma_name", "ma_name is not null");					
		String sob = SpObserver.getSp();	
		String sql=null;		
		for(Map<Object,Object> map:maps){
			sqls.clear();						
			for(Object o : masters){
				if(!(sob.equals(o))){
					sqls.clear();			
					if("VARCHAR2".equals(map.get("data_type"))){
						sqls.add("alter table "+o+"."+tablename+" add "+map.get("column_name")+" VARCHAR2("+map.get("data_length")+")");
					}else 
						sqls.add("alter table "+o+"."+tablename+" add "+map.get("column_name")+" "+map.get("data_type"));
					try {
						if(sqls.size()>0){
							baseDao.execute(sqls);
							log(tablename, modify, sqls.toString());
						}
					} catch (Exception e) {
						// TODO: handle exception
						continue;
					}
					sqls.clear();
					sql=null;
					if(!CHECKNULLABLECHANGE(String.valueOf(map.get("nullable")),String.valueOf(map.get("column_name")),tablename)){							
						sql+="Y".equals(map.get("nullable"))?" NULL ":" NOT NULL";
					} 
					sql+=getDefaultData(map.get("data_default"));
					if(map.get("comments")!=null) sqls.add(getColComments(String.valueOf(map.get("column_name")),o+"."+tablename,String.valueOf(map.get("comments"))));	 	 																				
					try {
						if(sqls.size()>0){
							baseDao.execute(sqls);
							log(tablename, modify, sqls.toString());
						}
					} catch (Exception e) {
						// TODO: handle exception
						continue;
					}
					
				}
			}
		}
	
	}
	public String getDefaultSob() {
		return BaseUtil.getXmlSetting("defaultSob");
	}
	private String getColComments(String field,String tablename,String comments){
		comments=comments.replaceAll("'", "");
		return "COMMENT ON COLUMN "+tablename+"."+field+" IS '"+comments+"'";
	}
	private boolean existViews(String tablename){
		return baseDao.checkIf("User_Dependencies", "type='VIEW' AND Referenced_Type='TABLE' AND  Referenced_Name='"+tablename+"'");
	}
	private void log(String table,String type,String sql){
		sql=sql.replaceAll("'", "''");
		baseDao.execute(" insert into DB$log(TABLE_NAME,ALTER_TYPE,ALTER_REMARK,ALTER_MAN) values('"+table+"','"+type+"','"+sql+"','"+SystemSession.getUser().getEm_name()+"')");	
	}
	private String getDefaultData(Object data_default){
		if(data_default!=null){
			if (String.valueOf(data_default).toLowerCase().equals("null")) return " default null";
			if (String.valueOf(data_default).toLowerCase().equals("sysdate")) return " default sysdate";
			if(String.valueOf(data_default).startsWith("'")) return  " default "+data_default;
			else if(String.valueOf(data_default).trim().toUpperCase().startsWith("TO_DATE")) return " default "+data_default;
			else return " default '"+data_default+"'";
		}else  return "";
	}
	private boolean CHECKNULLABLECHANGE(String nullable,String fieldname,String tablename){
		return baseDao.checkIf("User_Tab_Columns", "nullable='"+nullable+"' and table_name='"+tablename+"' and column_name='"+fieldname+"'");
	}
	private void createIndex(String ind_create,String tablename){
		List<Map<Object,Object>> maps=BaseUtil.parseGridStoreToMaps(ind_create);  
		String sql=null;
		for(Map<Object,Object> map:maps){
			sql=" CREATE ";
			if("UNIQUE".equals(map.get("uniqueness"))){
				sql+=" UNIQUE ";
			}
			sql+=" INDEX "+map.get("index_name")+" ON "+tablename+"(";
			JSONArray arr= JSONArray.fromObject(map.get("ind_columns"));
			for(int i=0;i<arr.size();i++){
				JSONObject json = arr.getJSONObject(i);  
				sql+="\""+json.get("COLUMN_NAME")+"\"";
				if(i<arr.size()-1) sql+=",";
			}
			sql+=")";
			baseDao.execute(sql);
			log(tablename,add, sql);
		}
	}

	@Override
	public List<DataRelation> getDataRelations(String tablename) {
		// TODO Auto-generated method stub
		try{
			return baseDao.getJdbcTemplate().query(
					"select * from DataRelation where table_name_x=? ", 
					new BeanPropertyRowMapper<DataRelation>(DataRelation.class),tablename);
		} catch (EmptyResultDataAccessException e){
			e.printStackTrace();
			return null;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public List<JSONObject> getRelation_Col_Comments(String tablename) {
		// TODO Auto-generated method stub
		SqlRowList sl= baseDao.queryForRowSet(rel_col_comments,new Object[]{tablename});
		List<JSONObject> lists=new ArrayList<JSONObject>();
		while(sl.next()){
          lists.add(sl.getJSONObject());
		}
		return lists;
	}

	@Override
	public List<JSONObject> getRelation_Tab_Comments(String tablename) {
		// TODO Auto-generated method stub
		List<JSONObject> lists=new ArrayList<JSONObject>();
		SqlRowList sl=baseDao.queryForRowSet("select table_name,comments from Datarelation left join User_Tab_Comments on table_name_y=table_name where table_name_x=?", new Object[]{tablename});
		while(sl.next()){
			lists.add(sl.getJSONObject());
		}
		return lists;
	}

	@Override
	public void refresh(String tablename) {
		baseDao.callProcedure("SYS_DBREFRESH", new Object[] {tablename});
	}
}
