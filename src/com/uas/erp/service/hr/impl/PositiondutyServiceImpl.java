package com.uas.erp.service.hr.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.PositiondutyService;

@Service
public class PositiondutyServiceImpl implements PositiondutyService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void savePositionduty(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller,new Object[] { store, gstore});
		String formSql = SqlUtil.getInsertSqlByMap(store, "Positionduty");
		baseDao.execute(formSql);
		for (Map<Object, Object> m : gstore) {
			m.put("pdd_id", baseDao.getSeqId("Positiondutydet_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gstore, "Positiondutydet");
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "pd_id", store.get("pd_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller,new Object[] { store, gstore});
	}

	@Override
	public void deletePositionduty(int pd_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { pd_id});
		// 删除purchase
		baseDao.deleteById("Positionduty", "pd_id", pd_id);
		baseDao.deleteById("Positiondutydet", "pdd_pdid", pd_id);
		// 记录操作
		baseDao.logger.delete(caller, "pd_id", pd_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { pd_id});
	}

	@Override
	public void updatePositionduty(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore});
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Positionduty", "pd_id");
		baseDao.execute(formSql);
		// 修改ResearchProjectDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "Positiondutydet", "pdd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("pdd_id") == null || s.get("pdd_id").equals("") || s.get("pdd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("Positiondutydet_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "Positiondutydet", new String[] { "pdd_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "pd_id", store.get("pd_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore});
	}

}
