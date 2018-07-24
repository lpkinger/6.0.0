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
import com.uas.erp.service.hr.KpiEmpService;
@Service
public class KpiEmpServiceImpl implements KpiEmpService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveKpiEmp(String formStore, String gridStore,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] {store,grid});
		// //保存Contact
		baseDao.execute("delete from KpiEmp where ke_emid="+store.get("ke_emid"));
		for (Map<Object, Object> s : grid) {
			s.put("ke_id", baseDao.getSeqId("KpiEmp_SEQ"));
			s.put("ke_emid", store.get("ke_emid"));
			s.put("ke_emname", store.get("ke_emname"));
			s.put("ke_krtitle", s.get("kr_title"));
			s.remove("kr_title");
			s.put("ke_krid", s.get("kr_id"));
			s.remove("kr_id");
			s.remove("isUsed");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"KpiEmp");
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "ke_emid", store.get("ke_emid"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store,grid});
	}
	

	@Override
	public String show(String gridStore, String condition) {
				List<Map<Object, Object>> grid = BaseUtil
						.parseGridStoreToMaps(gridStore);
				List<Object> objects = baseDao.getFieldDatasByCondition("KpiEmp", "ke_krid", condition);
				for(Map<Object, Object> m:grid){
					boolean isUsed=false;
					for(Object o:objects){
						if(o.toString().equals(m.get("kr_id").toString())){
							isUsed=true;
							break;
						}
					}
					m.put("isUsed", isUsed);
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

}
