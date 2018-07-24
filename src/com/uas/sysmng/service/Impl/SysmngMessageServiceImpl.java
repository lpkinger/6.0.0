package com.uas.sysmng.service.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.derby.tools.sysinfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.MessageRole;
import com.uas.erp.model.SubsFormulaDet;
import com.uas.sysmng.service.SysmngMessageService;

@Service
public class SysmngMessageServiceImpl implements SysmngMessageService{
	@Autowired
	private BaseDao baseDao;

	@Override
	public Map<String, Object> getMessageFormData(String id) {
		// TODO Auto-generated method stub
		Map<String,Object> map = new HashMap<String,Object>();
		SqlRowSet rs = baseDao.getJdbcTemplate().queryForRowSet(
"SELECT  MM_ACTION,MM_APPURL, MM_CALLER,MM_CODE,MM_CODEFIELD,MM_DETNO,MM_ID,MM_ISUSED,MM_KEYFIELD,MM_MODULE,MM_NAME,MM_OPERATE,MM_OPERATEDESC,MM_PFFIELD,MM_RECORDDATE,MM_RECORDER,MM_STATUS,MM_STATUSCODE,MM_TABLE,MM_URL FROM MESSAGEMODEL where MM_ID="+id);
		while(rs.next()){
			map.put("MM_ACTION", rs.getString("MM_ACTION"));
			map.put("MM_APPURL", rs.getString("MM_APPURL"));
			map.put("MM_CALLER",rs.getString("MM_CALLER"));
			map.put("MM_CODE", rs.getString("MM_CODE"));
			map.put("MM_CODEFIELD",rs.getString("MM_CODEFIELD"));
			map.put("MM_DETNO", rs.getString("MM_DETNO"));
			map.put("MM_ID", rs.getInt("MM_ID"));
			map.put("MM_ISUSED",rs.getString("MM_ISUSED"));
			map.put("MM_KEYFIELD",rs.getString("MM_KEYFIELD"));
			map.put("MM_MODULE",rs.getString("MM_MODULE"));
			map.put("MM_NAME",rs.getString("MM_NAME"));
			map.put("MM_OPERATE",rs.getString("MM_OPERATE"));
			map.put("MM_OPERATEDESC",rs.getString("MM_OPERATEDESC"));
			map.put("MM_PFFIELD",rs.getString("MM_PFFIELD"));
			map.put("MM_RECORDDATE",rs.getString("MM_RECORDDATE"));
			map.put("MM_RECORDER",rs.getString("MM_RECORDER"));
			map.put("MM_STATUS",rs.getString("MM_STATUS"));
			map.put("MM_STATUSCODE",rs.getString("MM_STATUSCODE"));
			map.put("MM_TABLE",rs.getString("MM_TABLE"));
			map.put("MM_URL",rs.getString("MM_URL"));

		}
		return map;
	
	}

	@Override
	public List<MessageRole> getMessageGridData( String id) {
		try {
			return baseDao.getJdbcTemplate().query("select * from MessageRole where mr_mmid=?",
					new BeanPropertyRowMapper<MessageRole>(MessageRole.class), id);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Boolean deleteData(String id) {
		
		try {
			
			String sqlString="delete from MESSAGEMODEL where MM_ID='"+id+"'";
			String sqlString2="delete from MESSAGEROLE where MR_MMID='"+id+"'";
			baseDao.execute(sqlString);
			baseDao.execute(sqlString2);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

	@Override
	public Boolean toolbarDelete(String id) {
		
		try {			
			
			String sqlString2="delete from MESSAGEROLE where MR_ID='"+id+"'";
			baseDao.execute(sqlString2);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

	@Override
	public Boolean updateData(String formData, String gridData1,String gridData2) {
		Map<Object, Object> fData = BaseUtil.parseFormStoreToMap(formData);
		List<Map<Object,Object>> gData1 = BaseUtil.parseGridStoreToMaps(gridData1);
		List<Map<Object,Object>> gData2 = BaseUtil.parseGridStoreToMaps(gridData2);
		
		List<Map<Object,Object>> gDataCopy1 = new ArrayList<Map<Object,Object>>();
		List<Map<Object,Object>> gDataCopy2 = new ArrayList<Map<Object,Object>>();
		int count=baseDao.getCountByCondition("messagemodel", "mm_caller='"+fData.get("MM_CALLER")+"' and MM_ID <>'"+fData.get("MM_ID")+"' and MM_OPERATE='"+fData.get("MM_OPERATE")+"'");
		if(count>0){
			BaseUtil.showError("此CALLER对应的操作已经存在消息模板，不允许重复新增！");
		}
		int count2=baseDao.getCountByCondition("messagemodel", "mm_caller='"+fData.get("MM_CALLER")+"' and MM_DETNO <>'"+fData.get("MM_DETNO")+"'");
		if(count2>0){
			BaseUtil.showError("同CALLER单据序号不同，不允许更新!");
		}		
		Map<Object,Object> f = new HashMap<Object,Object>();		
		f = FlexJsonUtil.fromJson(formData);
		String formSql = SqlUtil.getUpdateSqlByFormStore(f, "messagemodel","MM_ID");			
		baseDao.execute(formSql);
		
		for(Map<Object, Object> map:gData2){
			Set<Object> set = map.keySet();
			Map<Object, Object> newMap = new HashMap<Object,Object>();
			for(Object obj:set){
				newMap.put(obj, map.get(obj));
			}
			gDataCopy2.add(newMap);
		}		
		for(Map<Object, Object> s:gData1){
			s.put("mr_mmid", fData.get("MM_ID"));
			if (s.get("mr_id")=="0"||"0".equals(s.get("mr_id").toString())) {				
				s.put("mr_id",baseDao.getSeqId("MESSAGEROLE_SEQ"));
			}	
		}
		//深度拷贝gData1数据
		for(Map<Object, Object> map:gData1){
			Set<Object> set = map.keySet();
			Map<Object, Object> newMap = new HashMap<Object,Object>();
			for(Object obj:set){
				newMap.put(obj, map.get(obj));
			}
			gDataCopy1.add(newMap);
		}
		for(Map<Object, Object> s:gData1){							
				if(!"".equals(s.get("mr_messagestr"))){
					s.put("mr_messagestr", "");					
				}
				if(!"".equals(s.get("mr_messagedemo"))){				
					s.put("mr_messagedemo", "");
				}
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gData1, "MESSAGEROLE");
		baseDao.execute(gridSql);
		
		//对clob数据特殊处理逻辑
				for(Map<Object, Object> s:gDataCopy1){
					if(!"".equals(s.get("mr_messagestr"))){					
						baseDao.saveClob("MESSAGEROLE", "mr_messagestr", s.get("mr_messagestr").toString(),"mr_id='"+s.get("mr_id")+"'");						
					}
					if(!"".equals(s.get("mr_messagedemo"))){
						baseDao.saveClob("MESSAGEROLE", "mr_messagedemo", s.get("mr_messagedemo").toString(),"mr_id='"+s.get("mr_id")+"'");						
					}							
				}		
		for(Map<Object, Object> s:gData2){
			if(!"".equals(s.get("mr_messagestr"))){
				s.put("mr_messagestr", "");					
			}
			if(!"".equals(s.get("mr_messagedemo"))){				
				s.put("mr_messagedemo", "");
			}
					
		}
		List<String> gridSql2 = SqlUtil.getInsertOrUpdateSql(gData2, "MESSAGEROLE","mr_id");
		baseDao.execute(gridSql2);	
		//对clob数据特殊处理逻辑
				for(Map<Object, Object> s:gDataCopy2){											
					baseDao.saveClob("MESSAGEROLE", "mr_messagestr", s.get("mr_messagestr").toString(),"mr_id='"+s.get("mr_id")+"'");										
					baseDao.saveClob("MESSAGEROLE", "mr_messagedemo", s.get("mr_messagedemo").toString(),"mr_id='"+s.get("mr_id")+"'");								
				}	
		return true;
	}

	@Override
	public Map<String, Object> saveData(String formData, String gridData) {
		Map<Object, Object> fData = BaseUtil.parseFormStoreToMap(formData);
		List<Map<Object,Object>> gData = BaseUtil.parseGridStoreToMaps(gridData);
		Map<String, Object> rmap=new HashMap<String, Object>();
		
		int count=baseDao.getCountByCondition("messagemodel", "mm_caller='"+fData.get("MM_CALLER")+"' and MM_OPERATE='"+fData.get("MM_OPERATE")+"'");
		if(count>0){
			BaseUtil.showError("此CALLER对应的操作已经存在消息模板，不允许重复新增！");
		}
		int count2=baseDao.getCountByCondition("messagemodel", "mm_caller='"+fData.get("MM_CALLER")+"' and MM_DETNO <>'"+fData.get("MM_DETNO")+"'");
		if(count2>0){
			BaseUtil.showError("同CALLER单据序号不同，不允许更新!");
		}
		
		if (fData.get("MM_ID")==null||"".equals(fData.get("MM_ID"))) {			
			fData.put("MM_ID",baseDao.getSeqId("MESSAGEMODEL_SEQ"));
		}	
		if (fData.get("MM_CODE")==null||"".equals(fData.get("MM_CODE"))) {			
			fData.put("MM_CODE",baseDao.sGetMaxNumber("MESSAGEMODEL", 2));
		}	
		String formSql = SqlUtil.getInsertSqlByFormStore(fData, "MESSAGEMODEL", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		
		List<Map<Object,Object>> gDataCopy1 = new ArrayList<Map<Object,Object>>();	
		for(Map<Object, Object> s:gData){
			s.put("mr_mmid", fData.get("MM_ID"));			
			int i=baseDao.getSeqId("MESSAGEROLE_SEQ");
			s.put("mr_id",i);	
		}
		for(Map<Object, Object> map:gData){
			Set<Object> set = map.keySet();
			Map<Object, Object> newMap = new HashMap<Object,Object>();
			for(Object obj:set){
				newMap.put(obj, map.get(obj));
			}
			gDataCopy1.add(newMap);
		}
		for(Map<Object, Object> s:gData){			
			if(!"".equals(s.get("mr_messagestr"))){
				s.put("mr_messagestr", "");					
			}
			if(!"".equals(s.get("mr_messagedemo"))){				
				s.put("mr_messagedemo", "");
			}
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gData, "MESSAGEROLE");	
		baseDao.execute(gridSql);
		//针对clob数据的特殊逻辑处理
		for(Map<Object, Object> s:gDataCopy1){
			
			baseDao.saveClob("MESSAGEROLE", "mr_messagestr", s.get("mr_messagestr").toString(),"mr_id='"+s.get("mr_id")+"'");
			baseDao.saveClob("MESSAGEROLE", "mr_messagedemo", s.get("mr_messagedemo").toString(),"mr_id='"+s.get("mr_id")+"'");						
		}
		rmap.put("formid", fData.get("MM_ID"));
		//rmap.put("gridid", gData.get(""));
		return rmap;
	}

	
}