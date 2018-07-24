package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.fa.AssKindDetailService;

@Service("assKindDetailService")
public class AssKindDetailServiceImpl implements AssKindDetailService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveAssKindDetail(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 保存Dispatch
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "AssKind",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存DispatchDetail
		Object[] akd_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			akd_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				akd_id[i] = baseDao.getSeqId("ASSKINDDETAYL_SEQ");
			}
		} else {
			akd_id[0] = baseDao.getSeqId("ASSKINDDETAYL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore,
				"AssKindDetail", "akd_id", akd_id);
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "ak_id", store.get("ak_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteAssKindDetail(int ak_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, ak_id);
		// 删除Dispatch
		baseDao.deleteById("AssKind", "ak_id", ak_id);
		// 删除DispatchDetail
		baseDao.deleteById("AssKindDetail", "akd_akid", ak_id);
		// 记录操作
		baseDao.logger.delete(caller, "ak_id", ak_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ak_id);
	}

	@Override
	public void updateAssKindDetailById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);

		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改Dispatch
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "AssKind",
				"ak_id");
		baseDao.execute(formSql);
		// 修改
		for (Map<Object, Object> gs : gstore) {
			if (gs.containsKey("akd_status")) {
				if (gs.get("akd_status") == null
						|| gs.get("akd_status").equals("")) {
					gs.put("akd_status", "CANUSE");
				}
			}
		}
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore,
				"AssKindDetail", "akd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("akd_id") == null || s.get("akd_id").equals("")
					|| s.get("akd_id").equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("ASSKINDDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "AssKindDetail",
						new String[] { "akd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "ak_id", store.get("ak_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(distinct akd_assname) from (select akd_akid,akd_assname from asskinddetail where akd_akid=? group by akd_akid,akd_assname having count(*)>1)",
						String.class, store.get("ak_id"));
		if (dets != null) {
			BaseUtil.appendError("核算名称["+ dets+"]重复");
		}
	}

}
