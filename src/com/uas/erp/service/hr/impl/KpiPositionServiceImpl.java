package com.uas.erp.service.hr.impl;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.KpiPositionService;
@Service
public class KpiPositionServiceImpl implements KpiPositionService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public String show(String gridStore, String caller,
			String condition) {
		//List<Map<String,Object>> maps=new ArrayList<Map<String,Object>>();
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		List<Object> objects = baseDao.getFieldDatasByCondition("KpiPosition", "kp_krid", condition);
		for(Map<Object, Object> m:grid){
			boolean isUsed=false;
			for(Object o:objects){
				if(o.toString().equals(m.get("kr_id").toString())){
					isUsed=true;
					break;
				}
			}
			m.put("isUsed", isUsed);
			/*if(objects.contains(m.get("kr_id"))){
				m.put("isUsed", true);
			}else{
				m.put("isUsed", false);
			}*/
		}
		//parseGridStore2Str
		JSONArray arr = new JSONArray();
		JSONObject obj = null;
		for (Map<Object, Object> map : grid) {
			if (map != null) {
				obj = new JSONObject();
				for (Object key : map.keySet()) {
					obj.put(key, map.get(key));
				}
				arr.add(obj);
			}
		}
		return arr.toString();
	}
	@Override
	public void saveKpiPosition(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] {store,grid});
		// //保存Contact
		baseDao.execute("delete from KpiPosition where kp_positionid="+store.get("kp_positionid"));
		for (Map<Object, Object> s : grid) {
			s.put("kp_id", baseDao.getSeqId("KpiPosition_SEQ"));
			s.put("kp_positionid", store.get("kp_positionid"));
			s.put("kp_position", store.get("kp_position"));
			s.put("kp_krtitle", s.get("kr_title"));
			s.remove("kr_title");
			s.put("kp_krid", s.get("kr_id"));
			s.remove("kr_id");
			s.remove("isUsed");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"KpiPosition");
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "kp_positionid", store.get("kp_positionid"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store,grid});
	}

}
