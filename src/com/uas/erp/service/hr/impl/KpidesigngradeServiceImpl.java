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
import com.uas.erp.service.hr.KpidesigngradeService;;
@Service
public class KpidesigngradeServiceImpl implements KpidesigngradeService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveKpidesigngrade(String formStore, String gridStore,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] {store});
		store.put("kg_score",store.get("kg_score_from")+"~"+store.get("kg_score_to"));
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"Kpidesigngrade", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "kg_id", store.get("kg_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store});
	}
	@Override
	public void updateKpidesigngrade(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		
		//执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store});
		store.put("kg_score",store.get("kg_score_from")+"~"+store.get("kg_score_to"));
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Kpidesigngrade", "kg_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "kg_id", store.get("kg_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}
	@Override
	public void deleteKpidesigngrade(int kg_id, String caller) {
			//执行删除前的其它逻辑
			handlerService.handler(caller, "delete", "before", new Object[]{kg_id});
			//删除
			baseDao.deleteById("Kpidesigngrade", "kg_id", kg_id);
			//记录操作
			baseDao.logger.delete(caller, "kg_id", kg_id);
			//执行删除后的其它逻辑
			handlerService.handler(caller, "delete", "after", new Object[]{kg_id});	
	}
}
