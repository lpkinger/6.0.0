package com.uas.erp.service.oa.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.OADocumentPowerService;
@Service("oaDocumentPowerService")
public class OADocumentPowerServiceImpl implements OADocumentPowerService {

	@Autowired
	private BaseDao baseDao;
	@Override
	public void save(String save, String table, String otherField, Object[] otherValues, String  caller) {
		List<String> sqls = new ArrayList<String>();
		if(save.contains("},")){//明细行有多行数据哦
			String[] datas = save.split("},");
			int count = 0;
			for(String data:datas){
				data = data.replace("{", "").replace("}", "");
				String[] strs = data.split(",");
//				StringBuffer sb1 = new StringBuffer("INSERT into " + table + " (");
//				StringBuffer sb2 = new StringBuffer(" ");
				Map<Object, Object> map = new HashMap<Object, Object>();
				for(String str:strs){
					Object key = str.split(":")[0].replace("\"", "");
					Object value = str.split(":")[1].replace("\"", "");
					map.put(key, value);
				}
//				if(map.get(otherField) != null){//字段重复了哦
//					map.put(otherField, otherValues[count++]);//优先选用传递过来的value
//				}
				if(! otherField.equals("")// && map.get(otherField) == null
						){
					map.put(otherField, otherValues[count++]);
				}
				sqls.add("INSERT into " + table + " (dcp_id,dcp_powername,dcp_parentid,dcp_isleaf) VALUES ('" + 
				        map.get("dcp_id") + "','" + 
						map.get("dc_displayname") + "','" +
						map.get("dc_parentid") + "','" + 
						map.get("dc_isfile") + "')");
			}
		} else {
			save = save.substring(save.indexOf("{")+1, save.lastIndexOf("}"));
			String[] strs = save.split(",");
//			StringBuffer sb1 = new StringBuffer("INSERT into " + table + " (");
//			StringBuffer sb2 = new StringBuffer(" ");
			Map<Object, Object> map = new HashMap<Object, Object>();
			for(String str:strs){
				Object key = str.split(":")[0].replace("\"", "");
				Object value = str.split(":")[1].replace("\"", "");
				map.put(key, value);
			}
//			if(map.get(otherField) != null){//字段重复了哦
//				map.put(otherField, otherValues[count++]);//优先选用传递过来的value
//			}
			if(! otherField.equals("")// && map.get(otherField) == null
					){
				map.put(otherField, otherValues[0]);
			}
			sqls.add("INSERT into " + table + " (dcp_id,dcp_powername,dcp_parentid,dcp_isleaf) VALUES ('" + 
			        map.get("dc_id") + "','" + 
					map.get("dc_displayname") + "','" +
					map.get("dc_parentid") + "','" + 
					map.get("dc_isfile") + "')");
		}
		baseDao.execute(sqls);
		try{
			for(Object o:otherValues){
				//记录操作
				baseDao.logger.save(caller, "dcp_id", o);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(String update, String table, String keyField, String  caller) {
		List<String> sqls = new ArrayList<String>();
		if(update != null){
			if(update.contains("},")){//明细行有多行数据哦
				String[] datas = update.split("},");
				for(String data:datas){
					data = data.replace("{", "").replace("}", "");
					String[] strs = data.split(",");
					Object keyValue = "";
					Map<Object, Object> map = new HashMap<Object, Object>();
					for(String str:strs){
						Object key = str.split(":")[0].replace("\"", "");
						Object value = str.split(":")[1].replace("\"", "");
						map.put(key, value);
						if(key.equals(keyField)){
							//value不为空，即为已存在数据
							if(value != null && !value.equals("")){
								keyValue = value;
							} else {//否则，为新添加的数据
								break;
							}
						}
					}
					sqls.add("UPDATE " + table + " SET dcp_powername = '" + map.get("dc_displayname") + "'," 
							+ "dcp_parentid = '" + map.get("dc_parentid") + "',"
							+ "dcp_isleaf = '" + map.get("dc_isfile") + "'"
							+ " WHERE " + keyField + "='" + keyValue + "'");
				}
		
			} else {
				if(update.contains("{") && update.contains("}")){
					update = update.substring(update.indexOf("{")+1, update.lastIndexOf("}"));
					String[] strs = update.split(",");
					Object keyValue = "";
					Map<Object, Object> map = new HashMap<Object, Object>();
					for(String str:strs){
						Object key = str.split(":")[0].replace("\"", "");
						Object value = str.split(":")[1].replace("\"", "");
						map.put(key, value);
						if(key.equals(keyField)){
							keyValue = value;
						}
					}
					sqls.add("UPDATE " + table + " SET dcp_powername = '" + map.get("dc_displayname") + "'," 
							+ "dcp_parentid = '" + map.get("dc_parentid") + "',"
							+ "dcp_isleaf = '" + map.get("dc_isfile") + "'"
							+ " WHERE " + keyField + "='" + keyValue + "'");
				}
			}
		}
		baseDao.execute(sqls);
		//记录操作
		try {
			List<Map<Object, Object>> store = BaseUtil
					.parseGridStoreToMaps(update);
			for (Map<Object, Object> map : store) {
				// 记录操作
				baseDao.logger.update(caller, "dcp_id",map.get("dc_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void delete(int id, String caller) {
		// 删除
		deleteChilds(id);
		// 记录操作
		try {
			baseDao.logger.delete(caller, "dcp_id", id);;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteChilds(int id) {
		baseDao.deleteByCondition("DocumentPower",
				"dcp_id=" + id);
		// 判断是否有子元素
		boolean bool = baseDao.checkByCondition("DocumentPower",
				"dcp_parentid=" + id);
		if (!bool) {
			List<Object> objs = baseDao.getFieldDatasByCondition(
					"DocumentPower", "dcp_id", "dcp_parentid=" + id);
			for (Object obj : objs) {
				deleteChilds(Integer.parseInt("" + obj));
			}
		}
	}
}
