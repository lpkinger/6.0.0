package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.fa.CheckBookService;

@Service("checkBookService")
public class CheckBookServiceImpl implements CheckBookService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCheckBook(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		String code = store.get("cb_checkcode").toString();
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("CheckBook", "cb_checkcode='"
				+ code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存CheckBook
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "CheckBook",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存CheckBookDetail
		for (Map<Object, Object> m : grid) {
			m.put("cbd_id", baseDao.getSeqId("CheckBookDETAIL_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"CheckBookDetail");
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "cb_id", store.get("cb_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void deleteCheckBook(int cb_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, cb_id);
		// 删除CheckBook
		baseDao.deleteById("CheckBook", "cb_id", cb_id);
		// 删除CheckBookDetail
		baseDao.deleteById("CheckBookdetail", "cbd_cbid", cb_id);
		// 记录操作
		baseDao.logger.delete(caller, "cb_id", cb_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, cb_id);
	}

	@Override
	public void updateCheckBookById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改CheckBook
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "CheckBook",
				"cb_id");
		baseDao.execute(formSql);
		// 修改CheckBookDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"CheckBookDetail", "cbd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("cbd_id") == null || s.get("cbd_id").equals("")
					|| s.get("cbd_id").equals("0")
					|| Integer.parseInt(s.get("cbd_id").toString()) == 0) {// 新添加的数据，id不存在
				s.put("cbd_id", baseDao.getSeqId("CheckBookDETAIL_SEQ"));
				String sql = SqlUtil.getInsertSqlByMap(s, "CheckBookDetail");
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "cb_id", store.get("cb_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}
}
