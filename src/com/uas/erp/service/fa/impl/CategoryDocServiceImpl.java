package com.uas.erp.service.fa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.fa.CategoryDocService;

@Service("categoryDocService")
public class CategoryDocServiceImpl implements CategoryDocService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCategoryDoc(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);

		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"CategoryDoc", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "cd_id", store.get("cd_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteCategoryDoc(int cd_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, cd_id);
		// 删除
		baseDao.deleteById("CategoryDoc", "cd_id", cd_id);
		// 记录操作
		baseDao.logger.delete(caller, "cd_id", cd_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, cd_id);
	}

	@Override
	public void updateCategoryDocById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"CategoryDoc", "cd_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "cd_id", store.get("cd_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}
}
