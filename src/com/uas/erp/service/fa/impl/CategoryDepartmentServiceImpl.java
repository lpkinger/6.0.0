package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.fa.CategoryDepartmentService;

@Service("categoryDepartmentService")
public class CategoryDepartmentServiceImpl implements CategoryDepartmentService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCategoryDepartment(String formStore, String gridStore,
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
		Object[] cd_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			cd_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				cd_id[i] = baseDao.getSeqId("CATEGORYDEPARTMENT_SEQ");
			}
		} else {
			cd_id[0] = baseDao.getSeqId("CATEGORYDEPARTMENT_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore,
				"CategoryDepartment", "cd_id", cd_id);
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "ca_id", store.get("ca_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteCategoryDepartment(int ca_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, ca_id);
		// 删除Dispatch
		baseDao.deleteById("Category", "ca_id", ca_id);
		// 删除DispatchDetail
		baseDao.deleteById("CategoryDepartment", "cd_cateid", ca_id);
		// 记录操作
		baseDao.logger.delete(caller, "ca_id", ca_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ca_id);
	}

	@Override
	public void updateCategoryDepartmentById(String formStore,
			String gridStore, String caller) {
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
				"CategoryAssKind", "cd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("cd_id") == null || s.get("cd_id").equals("")
					|| s.get("cd_id").equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("CATEGORYDEPARTMENT_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "CategoryAssKind",
						new String[] { "cd_id" }, new Object[] { id });
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
