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
import com.uas.erp.service.hr.KpiRuleService;;
@Service
public class KpiRuleServiceImpl implements KpiRuleService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveKpiRule(String formStore, String gridStore,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] {store});
		store.put("kr_score",store.get("kr_score_from")+"~"+store.get("kr_score_to"));
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"KpiRule", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "kr_id", store.get("kr_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store});
	}
	@Override
	public void updateKpiRule(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		
		//执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store});
		store.put("kr_score",store.get("kr_score_from")+"~"+store.get("kr_score_to"));
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "KpiRule", "kr_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "kr_id", store.get("kr_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}
	@Override
	public void deleteKpiRule(int kr_id, String caller) {
			//删除前，关联表数据检查
			baseDao.delCheck("KpiRule", kr_id);
			//执行删除前的其它逻辑
			handlerService.handler(caller, "delete", "before", new Object[]{kr_id});
			//删除
			baseDao.deleteById("KpiRule", "kr_id", kr_id);
			//记录操作
			baseDao.logger.delete(caller, "kr_id", kr_id);
			//执行删除后的其它逻辑
			handlerService.handler(caller, "delete", "after", new Object[]{kr_id});	
	}
	@Override
	public void testSQL(String sql, String caller) {		
		baseDao.execute(sql);
	}
}
