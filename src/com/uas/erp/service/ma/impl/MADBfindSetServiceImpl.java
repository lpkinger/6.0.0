package com.uas.erp.service.ma.impl;

//import java.util.List;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.common.DbfindService;
import com.uas.erp.service.ma.MADBfindSetService;

@Service
public class MADBfindSetServiceImpl implements MADBfindSetService{
	@Autowired
	private BaseDao baseDao;
	@Autowired  
	private DbfindService dbfindService;
	@Override
	public void save(String form, String param) {
		Map<Object,Object> store=BaseUtil.parseFormStoreToMap(form);
		int count=baseDao.getCount("select count(1) from dbfindset where ds_caller='"+store.get("ds_caller")+"'");	
		if(count>0){
			BaseUtil.showError("此Caller名已存在");
		}
		List<Map<Object,Object>> gridstores=BaseUtil.parseGridStoreToMaps(param);
		Map<Object,Object> map=null;
		List<String> sqls=new ArrayList<String>();
		StringBuffer sb=new StringBuffer();
		StringBuffer test=new StringBuffer("select ");
	    for(int i=0;i<gridstores.size();i++){
	    	map=gridstores.get(i);
	    	map.remove("dd_id");
	    	map.put("dd_id",baseDao.getSeqId("DBFINDSETDETAIL_SEQ"));
	    	sb.append(map.get("dd_fieldname")).append(",");
	    	sqls.add(SqlUtil.getInsertSqlByMap(map, "DBFINDSETDETAIL"));
	    }
	    try{
			if(sb.length()==0){
				test.append(" count(1) from "+store.get("ds_tablename").toString());
			}else{
				test.append(sb.substring(0,sb.length()-1)).append(" from "+store.get("ds_tablename").toString());
			}
			if(store.get("ds_fixedcondition")!=null&&!store.get("ds_fixedcondition").equals("")){
				test.append(" where 1=2 and "+baseDao.parseEmpCondition(store.get("ds_fixedcondition").toString()));
			}else{
				test.append(" where 1=2 ");
			}
			if(store.get("ds_orderby")!=null&&!store.get("ds_orderby").equals("")){
				test.append(" "+store.get("ds_orderby"));
			}
			baseDao.execute(test.toString());
		} catch (Exception exception){
			BaseUtil.showError("配置有误!请检查【表名】、【排序】、【筛选条件】及明细行【字段名称是否匹配】.");
		}
		String formSql = SqlUtil.getInsertSqlByMap(store, "DBFINDSET");
		baseDao.execute(formSql);
		baseDao.execute(sqls);
	}
	@Override
	public boolean checkCaller(String caller) {
		return baseDao.checkByCondition("dbfindSet", "ds_caller='" + caller + "'");
	}
	@Override
	@CacheEvict(value="dbfind",allEntries=true)
	public void update(String formStore, String param) {
		// TODO Auto-generated method stu
		Map<Object,Object> store=BaseUtil.parseFormStoreToMap(formStore);
		int count=baseDao.getCount("select count(1) from dbfindset where ds_caller='"+store.get("ds_caller")+"' and ds_id <>"+store.get("ds_id"));	
		if(count>0){
			BaseUtil.showError("此Caller名已存在");
		}
		StringBuffer sb=new StringBuffer();
		StringBuffer test=new StringBuffer("select ");
		List<String> sqls=SqlUtil.getUpdateSqlbyGridStore(param, "DBFINDSETDetail", "dd_id");
		List<Map<Object,Object>> maps=BaseUtil.parseGridStoreToMaps(param);
		Map<Object,Object> map=null;
		for(int i=0;i<maps.size();i++){
			map=maps.get(i);
		   Object dd_id=map.get("dd_id");
		   sb.append(map.get("dd_fieldname")).append(",");
		   map.remove("dd_caller");
		   map.put("dd_caller", store.get("ds_caller"));
			if(dd_id == null || dd_id.equals("") || dd_id.equals("0") || 
					Integer.parseInt(dd_id.toString()) == 0){
			sqls.add(SqlUtil.getInsertSqlByMap(map, "DBFINDSETDetail", new String[]{"dd_id"}, new Object[]{baseDao.getSeqId("DBFINDSETDETAIL_SEQ")}));
			}
		}
		try{
				if(sb.length()==0){
					test.append(" count(1) from "+store.get("ds_tablename").toString());
				}else{
					test.append(sb.substring(0,sb.length()-1)).append(" from "+store.get("ds_tablename").toString());
				}
				
				if(store.get("ds_fixedcondition")!=null&&!store.get("ds_fixedcondition").equals("")){
					test.append(" where 1=2 and "+baseDao.parseEmpCondition(store.get("ds_fixedcondition").toString()));
				}else{
					test.append(" where 1=2 ");
				}
				if(store.get("ds_orderby")!=null&&!store.get("ds_orderby").equals("")){
					test.append(" "+store.get("ds_orderby"));
				}
				baseDao.execute(test.toString());
			} catch (Exception exception){
				BaseUtil.showError("配置有误!请检查【表名】、【排序】、【筛选条件】及明细行【字段名称是否匹配】.");
			}
		String formSql=SqlUtil.getUpdateSqlByFormStore(store, "DBFINDSet", "ds_id");
		baseDao.execute(formSql);
		baseDao.execute(sqls);
	}
	@Override
	public void delete(int id) {
		int count =baseDao.getCount("select count(1) from detailgrid where dg_findfunctionname like (select ds_caller||'|%' from dbfindset where ds_id="+id+")");
		 if(count>0){
	        	BaseUtil.showError("此DBFindSet设置已被使用不能删除");
	        }
        baseDao.deleteById("DBFindSet", "ds_id", id);
	}
	
}
