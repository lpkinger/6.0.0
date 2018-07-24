package com.uas.erp.service.crm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.crm.TaskTemplatesService;

@Service
public class TaskTemplatesServiceImpl implements TaskTemplatesService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveTaskTemplates(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		String formSql = SqlUtil.getInsertSqlByMap(store, "TaskTemplates");
		baseDao.execute(formSql);
		for (Map<Object, Object> m : gstore) {
			m.put("ttd_id", baseDao.getSeqId("TaskTemplatesDETAIL_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gstore,
				"TaskTemplatesDetail");
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "tt_id", store.get("tt_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteTaskTemplates(int tt_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, tt_id);
		// 删除purchase
		baseDao.deleteById("TaskTemplates", "tt_id", tt_id);
		baseDao.deleteById("TaskTemplatesdetail", "ttd_ttid", tt_id);
		// 记录操作
		baseDao.logger.delete(caller, "tt_id", tt_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, tt_id);
	}

	@Override
	public void updateTaskTemplates(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"TaskTemplates", "tt_id");
		baseDao.execute(formSql);
		// 修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore,
				"TaskTemplatesDetail", "ttd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("ttd_id") == null || s.get("ttd_id").equals("")
					|| s.get("ttd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("TaskTemplatesDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s,
						"TaskTemplatesDetail", new String[] { "ttd_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "tt_id", store.get("tt_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

}
