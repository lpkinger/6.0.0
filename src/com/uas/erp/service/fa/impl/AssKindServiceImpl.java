package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.fa.AssKindService;

@Service("assKindService")
public class AssKindServiceImpl implements AssKindService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveAssKind(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("AssKind",
				"ak_code='" + store.get("ak_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "AssKind",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "ak_id", store.get("ak_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteAssKind(int ak_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, ak_id);
		// 删除
		baseDao.deleteById("AssKind", "ak_id", ak_id);
		// 记录操作
		baseDao.logger.delete(caller, "ak_id", ak_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ak_id);
	}

	@Override
	public void updateAssKindById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "AssKind",
				"ak_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "ak_id", store.get("ak_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public List<Map<String, Object>> getAssKind() {
		SqlRowList rs = baseDao.queryForRowSet("select ak_code,ak_name,ak_table,ak_dbfind,ak_asscode,ak_assname,ak_addkind,ak_id from asskind order by ak_id");
		return rs.getResultList();
	}
}
