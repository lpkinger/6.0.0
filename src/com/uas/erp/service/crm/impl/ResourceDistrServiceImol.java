package com.uas.erp.service.crm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.crm.ResourceDistrService;

@Service
public class ResourceDistrServiceImol implements ResourceDistrService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveResourceDistr(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		for (Map<Object, Object> m : gstore) {
			m.put("rd_id", baseDao.getSeqId("ResourceDistr_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gstore,
				"ResourceDistr");
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "pr_id", store.get("pr_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteResourceDistr(int pr_id, String caller) {
		handlerService.beforeDel(caller, pr_id);
		// 删除purchase
		baseDao.deleteById("ResourceDistr", "rd_prid", pr_id);
		// 记录操作
		baseDao.logger.delete(caller, "pr_id", pr_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, pr_id);
	}

	@Override
	public void updateResourceDistr(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改MProjectPlanDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"ResourceDistr", "rd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("rd_id") == null || s.get("rd_id").equals("")
					|| s.get("rd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("ResourceDistr_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "ResourceDistr",
						new String[] { "rd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "pr_id", store.get("pr_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

}
