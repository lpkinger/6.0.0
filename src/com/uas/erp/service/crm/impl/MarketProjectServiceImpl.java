package com.uas.erp.service.crm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.crm.MarketProjectService;

@Service
public class MarketProjectServiceImpl implements MarketProjectService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveMarketProject(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 计算剩余预算金额
		for (Map<Object, Object> map : gstore) {
			map.put("ppd_surplus", map.get("ppd_amount"));
		}
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		String formSql = SqlUtil.getInsertSqlByMap(store, "MProjectPlan");
		baseDao.execute(formSql);
		for (Map<Object, Object> m : gstore) {
			m.put("ppd_id", baseDao.getSeqId("ResearchProjectDETAIL_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gstore,
				"ResearchProjectDetail");
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "prjplan_id", store.get("prjplan_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteMarketProject(int prjplan_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, prjplan_id);
		// 删除purchase
		baseDao.deleteById("MProjectPlan", "prjplan_id", prjplan_id);
		baseDao.deleteById("ResearchProjectdetail", "ppd_ppid", prjplan_id);
		// 记录操作
		baseDao.logger.delete(caller, "prjplan_id", prjplan_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, prjplan_id);
	}

	@Override
	public void updateMarketProject(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "MProjectPlan",
				"prjplan_id");
		baseDao.execute(formSql);
		// 计算剩余预算金额
		for (Map<Object, Object> map : gstore) {
			map.put("ppd_surplus",
					Double.parseDouble(String.valueOf(map.get("ppd_amount")))
							- Double.parseDouble(String.valueOf(map
									.get("ppd_used"))));
		}
		// 修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore,
				"ResearchProjectDetail", "ppd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("ppd_id") == null || s.get("ppd_id").equals("")
					|| s.get("ppd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("ResearchProjectDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s,
						"ResearchProjectDetail", new String[] { "ppd_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "prjplan_id", store.get("prjplan_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

}
