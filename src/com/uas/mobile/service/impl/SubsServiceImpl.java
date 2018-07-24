package com.uas.mobile.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.SubsFormulaDet;
import com.uas.mobile.service.SubsService;

@Service
public class SubsServiceImpl implements SubsService{
	@Autowired
	private BaseDao baseDao;
	
	@Override
	public List<Map<String, Object>> getRealTimeSubs(int emid) {
		List<Map<String, Object>> subs =  baseDao.queryForList("SELECT SUBSNUM.id_ id,SUBSNUM.title_ title FROM SUBSNUM_MANS left join SUBSNUM on num_id=id_ WHERE kind_='realtime' and emp_id="+emid);
		return subs;
	}

	@Override
	public Map<Object, Object> mobileRealTimeCharts(int numId, int emid) {
		String v_returnStr = baseDao.callProcedure("SP_CREATE_REALTIME_DATA", new Object[] { numId, emid });
		Map<Object,Object> store = BaseUtil.parseFormStoreToMap(v_returnStr);
		return store;
	}

	@Override
	public Map<String, Object> getSubsConditionsConfig(int numId, int emid) {
		Map<String,Object> params = new HashMap<String,Object>();
		List<Map<String,Object>> configs = baseDao.queryForList("select * from SUBSNUM_CONDITIONS where num_id="+numId +"  order by detno_");
		List<Map<String,Object>> res = new ArrayList<Map<String,Object>>();
		String type = null;
		List<String> options = null;
		if(configs!=null&&configs.size()>0){
			for(Map<String,Object> map:configs){
				type = map.get("type_").toString();
				options = new ArrayList<String>();
				if("combo".equals(type) || "select".equals(type)){
					if(map.get("value_")!=null&&!"".equals(map.get("value_").toString())){
						String[] strs = map.get("value_").toString().split(";");
						for(String str:strs){
							options.add(str);
						}
					}
					map.put("VALUE_", options);
				}else if("sql".equals(type) || "editcombo".equals(type)){
					//List<String> sqlData = baseDao.query("select em_name from employee where em_code = 'U0818'", String.class);
					try {
						String sql = map.get("value_").toString();
						if(sql.toUpperCase().contains("@CONDITION")){
							sql = sql.substring(0,sql.toUpperCase().indexOf("@CONDITION"));
						}
						SqlRowList rs = baseDao.queryForRowSet(sql);
						while(rs.next()){
							options.add(rs.getString(1));
						}
						//List<String> sqlData = baseDao.query(map.get("value_").toString(), String.class);
						//options.addAll(sqlData);
						map.put("VALUE_", options);
					} catch (Exception e) {
						map.put("VALUE_", "");
					}
				}
				res.add(map);
			}
		}
		params.put("configs", configs);
		
		Object obj = baseDao.getFieldDataByCondition("SUBSNUM_PARAMS_INSTANCE", "data_", "numid_=" + numId + " and emid_=" + emid);
		params.put("data", obj);
		return params;
	}

	@Override
	public Map<String, Object> updateSubsConditionsInstance(int numId,
			int emid, String data) {
		Map<String,Object> params = new HashMap<String,Object>();
		boolean hasInstance = baseDao.checkIf("SUBSNUM_PARAMS_INSTANCE", "numid_=" + numId + " and emid_=" + emid);
		if(!hasInstance){
			baseDao.execute("insert into SUBSNUM_PARAMS_INSTANCE(id_,numid_,data_,emid_,updatetime_) values(SUBSNUM_PARAMS_INSTANCE_SEQ.NEXTVAL,"+numId+",'"+data.replaceAll("'", "''")+"',"+emid+",sysdate)");
		}else{
			baseDao.execute("update SUBSNUM_PARAMS_INSTANCE set data_='"+data.replaceAll("'", "''")+"',updatetime_=sysdate where numid_=" + numId + " and emid_=" + emid);
		}
		params.put("success", true);
		return params;
	}
	
	public List<Object> getRelConfig(int numId){
		return baseDao.getFieldDatasByCondition("SUBSNUM_RELATIONCONFIG", "sr_relfield", "num_id=" + numId + " group by sr_relfield");
	}
	
	public List<Map<String, Object>> getComboData(String fieldName, String value, int numId){
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		String sql = "select sr_field, sr_sql from subsnum_relationconfig where sr_relfield='"+fieldName+"' and num_id="+numId;
		List<Map<String, Object>> sqlList = baseDao.queryForList(sql);
		for(Map<String, Object> map : sqlList){
			String condition = String.valueOf(map.get("SR_SQL"));
			map.put("SR_SQL", condition.toUpperCase().replace("@"+fieldName.toUpperCase(), "'"+value+"'"));
		}
		String conditionSql = "select field_,value_,type_ from subsnum_conditions where num_id="+numId+" and type_ in('sql','editcombo')";
		List<Map<String, Object>> conditionList = baseDao.queryForList(conditionSql);
		for(Map<String, Object> conditionMap : conditionList){
			String conditionField = String.valueOf(conditionMap.get("FIELD_"));
			String fieldType = String.valueOf(conditionMap.get("TYPE_"));
			for(Map<String, Object> relationMap : sqlList){
				Map<String, Object> modelMap = new HashMap<String, Object>();
				String relationField = String.valueOf(relationMap.get("SR_FIELD"));
				if(conditionField.equalsIgnoreCase(relationField)){
					String conditionQuerySql = String.valueOf(conditionMap.get("VALUE_"));
					conditionMap.put("VALUE_", conditionQuerySql.toUpperCase().replace("@CONDITION", String.valueOf(relationMap.get("SR_SQL"))));
					SqlRowList rs = baseDao.queryForRowSet(String.valueOf(conditionMap.get("VALUE_")));
					List<String> options = new ArrayList<String>();
					while(rs.next()){
						options.add(rs.getString(1));
					}
					modelMap.put("field", conditionField);
					modelMap.put("value", options);
					modelMap.put("type", fieldType);
					resultList.add(modelMap);
				}
			}
		}
		return resultList;
	}
	
	public Map<String, Object> getGridLinkedDate(String data, String formulaNum, String field){
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject json = JSON.parseObject(data);
		JSONObject rowData = json.getJSONObject("rowData");
		JSONObject formData = json.getJSONObject("formData");
		
		Object[] fields = baseDao.getFieldsDataByCondition("subsformula", new String[]{"TITLE_", "SQL_"}, "code_='"+formulaNum+"'");
		String sql = String.valueOf(fields[1]).toUpperCase();
		Set<String> set = new HashSet<String>();
		set.addAll(rowData.keySet());
		//set.addAll(formData.keySet());
		Iterator<String> it = set.iterator();
		while(it.hasNext()){
			String key = it.next();
			sql = sql.replace("@"+key.toUpperCase(),"'"+rowData.getString(key)+"'");
		}
		set.clear();
		set.addAll(formData.keySet());
		it = set.iterator();
		while(it.hasNext()){
			String key = it.next();
			sql = sql.replace("@"+key.toUpperCase(),"'"+formData.getString(key)+"'");
		}
		map.put("data", baseDao.queryForList(sql));
		map.put("gridTitle", fields[0]);
		map.put("config", baseDao.query("SELECT SUBSformula_DET.* FROM SUBSformula_DET left join subsformula on id_= formula_id_ WHERE code_='"+formulaNum+"' order by detno_",SubsFormulaDet.class));
		return map;
	}
	
}
