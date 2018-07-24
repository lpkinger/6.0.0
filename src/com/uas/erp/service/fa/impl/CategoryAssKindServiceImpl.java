package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.fa.CategoryAssKindService;

@Service("categoryAssKindService")
public class CategoryAssKindServiceImpl implements CategoryAssKindService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCategoryAssKind(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 保存Dispatch
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Category",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存DispatchDetail
		Object[] cak_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			cak_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				cak_id[i] = baseDao.getSeqId("CATEGORYASSKIND_SEQ");
			}
		} else {
			cak_id[0] = baseDao.getSeqId("CATEGORYASSKIND_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore,
				"CategoryAssKind", "cak_id", cak_id);
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "ca_id", store.get("ca_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteCategoryAssKind(int ca_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, ca_id);
		// 删除Dispatch
		baseDao.deleteById("Category", "ca_id", ca_id);
		// 删除DispatchDetail
		baseDao.deleteById("CategoryAssKind", "cak_cateid", ca_id);
		// 记录操作
		baseDao.logger.delete(caller, "ca_id", ca_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ca_id);
	}

	@Override
	public void updateCategoryAssKindById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);

		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改Dispatch
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Category",
				"ca_id");
		baseDao.execute(formSql);
		// 修改DispatchDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"CategoryAssKind", "cak_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("cak_id") == null || s.get("cak_id").equals("")
					|| s.get("cak_id").equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("CATEGORYASSKIND_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "CategoryAssKind",
						new String[] { "cak_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "ca_id", store.get("ca_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

}
