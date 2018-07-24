package com.uas.erp.service.oa.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.CustomMessageService;

@Service
public class CustomMessageServiceImpl implements CustomMessageService{
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	
	@Override
	@Transactional
	public void save(String formStore,String gridStore){
		List<String> mmSql = SqlUtil.getUpdateSqlbyGridStore(formStore, "messagemodel", "mm_id");
		List<String> mrSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "messagerole", "mr_id");
		
		baseDao.execute(mmSql);
		baseDao.execute(mrSql);
	}
	
	@Override
	public Map<String,Object> getModule(String module,String caller){
		
		Map<String,Object> modelMap = new HashMap<String,Object>();
		Map<String,Object> map;
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		if(caller==null){
			SqlRowSet rs = baseDao.getJdbcTemplate().queryForRowSet("select distinct mm_name,mm_caller from messagemodel where mm_module='"+module+"'");
			while(rs.next()){
				map = new HashMap<String,Object>();
				map.put("mm_name", rs.getString("mm_name"));
				map.put("mm_caller", rs.getString("mm_caller"));
				list.add(map);
			}					
			modelMap.put("modules",list);
		}else{
			SqlRowSet rs = baseDao.getJdbcTemplate().queryForRowSet("select * from messagemodel left join messagerole on mm_id=mr_mmid where mm_caller='"+caller+"' order by mm_id,mm_detno");
			String mmId = "";
			List<Map<String,Object>> roleList = new ArrayList<Map<String,Object>>();
			Map<String,Object> roleMap;
			while(rs.next()){
				if(mmId.equals(rs.getString("mm_id"))){
					roleMap = getRoleMap(rs);
					roleList.add(roleMap);
				}else{
					mmId = rs.getString("mm_id");
					map = new HashMap<String,Object>();		
					roleList = new ArrayList<Map<String,Object>>();
					map.put("mm_id", mmId);
					map.put("mm_isused",rs.getInt("mm_isused"));
					map.put("mm_operate", rs.getString("mm_operate"));
					map.put("mm_operatedesc",rs.getString("mm_operatedesc"));
					map.put("mm_name", rs.getString("mm_name"));
					roleMap = getRoleMap(rs);
					roleList.add(roleMap);
					map.put("roles", roleList);  
					list.add(map);
				}				
			}
			modelMap.put("setting",list);
		}
		return modelMap;
	}
	
	private Map<String,Object> getRoleMap(SqlRowSet rs){
		Map<String,Object> roleMap = new HashMap<String,Object>();
		roleMap.put("mr_id", rs.getInt("mr_id"));
		roleMap.put("mr_isused",rs.getInt("mr_isused"));
		roleMap.put("mr_desc", rs.getString("mr_desc"));
		roleMap.put("mr_ispopwin", rs.getInt("mr_ispopwin"));
		roleMap.put("mr_level", rs.getString("mr_level"));
		
		//clobObject
		Clob clobObject = (Clob)rs.getObject("mr_messagedemo");
		roleMap.put("mr_messagedemo",clobObject==null?"":clobToString(clobObject));			
		roleMap.put("mr_mans", rs.getString("mr_mans"));
		roleMap.put("mr_manids", rs.getString("mr_manids"));
		return roleMap;
	}
	
	private String clobToString(Clob data)
	{
	    final StringBuilder sb = new StringBuilder();
	    try
	    {
	        final Reader reader = data.getCharacterStream();
	        final BufferedReader br = new BufferedReader(reader);

	        int b;
	        while(-1 != (b = br.read())){
	            sb.append((char)b);
	        }
	        br.close();
	    }catch (SQLException e){
	        return e.toString();
	    }catch (IOException e){
	        return e.toString();
	    }
	    return sb.toString();
	}
}
