package com.uas.sysmng.service.Impl;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.SysNavigation;
import com.uas.sysmng.service.SysmngUpgradeService;

@Service
public class SysmngUpgradeServiceImpl implements SysmngUpgradeService{
	@Autowired
	private BaseDao baseDao;

	
	public List<JSONTree> getJSONTreeByParentId(int parentId, String condition) {
		StringBuffer sb = new StringBuffer();
		List<JSONTree> tree = new ArrayList<JSONTree>();
		sb.append("select * from sysnavigation where ");
		sb.append("sn_parentid=?");
		if (condition != null && !"".equals(condition)) {
			sb.append(" AND ");
			sb.append(condition);
		}
		sb.append(" order by sn_detno");
		try {
			List<SysNavigation> sns = baseDao.getJdbcTemplate().query(sb.toString(), new BeanPropertyRowMapper<SysNavigation>(SysNavigation.class),
					parentId);
			
			for(SysNavigation navigation:sns){
			tree.add(new JSONTree(navigation, true));
		}
			
		} catch (EmptyResultDataAccessException exception) {
			//return new ArrayList<SysNavigation>();
		}
		
		return tree;
		
	}
	
	public void updateVersionLog(String id,String numid,String version,String remark,String name) {
		
			String updatNumSstring="UPDATE SYSNAVIGATION SET SN_NUM='"+numid+"' where SN_ID="+id+"";
			
			baseDao.execute(updatNumSstring);
			
			String sqlString="update sysnavigation set sn_deleteable = 'F' where SN_ID ='"+id+"'";
			
			baseDao.execute(sqlString);
			
			if(version!=null && version!=""&& remark!="" && remark!=null){
				
				String updatVersionSstring="UPDATE SYSNAVIGATION SET SN_SVNVERSION='"+version+"' where SN_ID="+id;
				
				baseDao.execute(updatVersionSstring);
				
				String logSqlString="INSERT INTO SYSNAVIGATIONUPGRADE_LOG(LOG_MAN,LOG_DATE,LOG_VERSION,LOG_REMARK,LOG_NUMID)VALUES('"+name+"',sysdate,'"+version+"','"+remark+"','"+numid+"')";
				baseDao.execute(logSqlString);

			}
	}

	@Override
	public List<Map<String, Object>> searchLog(String id) {
		// TODO Auto-generated method stub
		 //to_char(log_date,'yyyy-MM-dd hh:mm:ss') 
		String sql="SELECT log_man,log_version,log_remark,Log_numid,log_date FROM SYSNAVIGATIONUPGRADE_LOG where LOG_NUMID='"+id+"' order by log_date desc ";
		
		try {
			return  baseDao.getJdbcTemplate().queryForList(sql);
		} catch (Exception e) {
			// TODO: handle exception
			
		}
		return null;
	}
	
	public Map<String,Object> getUpgradeSql(String condition) {
		String sql = "select * from UPGRADE$SQL where 1=1";
		if (condition==null||condition.equals("")) {
			return null;
		}
		sql +=" AND "+condition;
		Map<String,Object> map = baseDao.getJdbcTemplate().queryForMap(sql);
		Object value = map.get("DATE_");
		value = value == null || value.equals("null") ? "" : value;
		if (value != null) {
			String classname = value.getClass().getSimpleName();
			if (classname.toUpperCase().equals("TIMESTAMP")) {
				Timestamp time = (Timestamp) value;
				try {
					value = DateUtil.parseDateToString(new Date(time.getTime()), Constant.YMD_HMS);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		map.remove("DATE_".toUpperCase());
		map.put("DATE_", value);
		return map;	
	}

	@Override
	public Map<String, Object> saveUpgradeSql(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int num = baseDao.getSeqId("UPGRADE$SQL_SEQ");
		store.put("NUM_", num);
		// 保存UPGRADE$SQL
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "UPGRADE$SQL", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		Map<String,Object> data = getUpgradeSql("NUM_="+num);
		if (data==null) {
			BaseUtil.showError("保存失败");
		}
		return data;
	}

	@Override
	public int getUpgradeSqlCount(String condition) {
		if("".equals(condition)){
			condition="1=1";
		}
		return baseDao.getCount("select count(1) from UPGRADE$SQL"+" where "+condition);
	}

	@Override
	public List<Map<String, Object>> getUpgradeSqlData(String condition,int page, int pageSize) {
		if("".equals(condition)){
			condition="1=1";
		}
		int start = ((page - 1) * pageSize + 1);
		int end = page * pageSize;
		String sql = "select *from (select a.*,rownum rn from (select * from (select * from UPGRADE$SQL  where "+condition+" order by num_ desc) where rownum<="+end+") a) where rn >="+start;
		return baseDao.getJdbcTemplate().queryForList(sql);
	}
	
	public Map<String,Object> checkSqls(int id,String sqls){
		Map<String,Object> responseMap = new HashMap<String,Object>();
		List<Map<String,Object>> checkResults = new ArrayList<Map<String,Object>>();
		Map<String,Object> map = null;
		String checkSql = null;
		if(sqls!=null&&!"".equals(sqls)){
			String[] sqlData = sqls.split(";");
			StringBuffer sb = new StringBuffer();
			
			for (String sql : sqlData) {
				sb.append("begin ");
				sb.append("execute immediate '").append(sql.replace("'", "''")).append("';rollback;end;");
				checkSql = sql;
				try{
					baseDao.execute(sb.toString());
				}catch(Exception e){
					if (e.getCause() instanceof java.sql.SQLException) {
						String message = e.getCause().getMessage();
						if(message.contains("ORA-009")){
							map = new HashMap<String,Object>();
							map.put("grammarError", true);
							map.put("errorSql", checkSql);
							map.put("errorInfo", message.substring(message.indexOf("ORA-009"),message.indexOf("\n")));
							checkResults.add(map);
						}
					}
				}finally{
					sb.setLength(0);
				}
			}
			responseMap.put("errorSqls",checkResults);
			if(checkResults.size()==0){
				responseMap.put("result", true);
				//改变状态为已审核通过
				baseDao.updateByCondition("UPGRADE$SQL", "STATUS_ = 1", "NUM_="+id);
			}else if(checkResults.size()>0){
				//改变状态为未审核通过
				baseDao.updateByCondition("UPGRADE$SQL", "STATUS_ = 0", "NUM_="+id);
				responseMap.put("result", false);
			}
		}
		return responseMap;
	}

	@Override
	public Map<String, Object> updateUpgradeSql(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		
		try {
			// 修改UPGRADE$SQL
			String formSql = SqlUtil.getUpdateSqlByFormStore(store, "UPGRADE$SQL", "NUM_");
			baseDao.execute(formSql);
			//检查SQL语句
			int id = Integer.parseInt((String) store.get("NUM_"));
			String sqls = (String) store.get("SQL_");
			checkSqls(id,sqls);
		} catch (Exception e) {
			BaseUtil.showError("更新失败");
		}
		Map<String,Object> data = getUpgradeSql("NUM_="+store.get("NUM_"));
		return data;		
	}

	@Override
	public void deleteUpgradeSqlByID(int id) {
		try {
			baseDao.deleteByCondition("UPGRADE$SQL", "NUM_="+id);
		} catch (Exception e) {
			BaseUtil.showError("删除失败");
		}		
	}

}