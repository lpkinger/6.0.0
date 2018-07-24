package com.uas.erp.service.ma.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.FormDao;
import com.uas.erp.model.RelativeSearch;
import com.uas.erp.service.ma.RelativeSearchService;

@Service
public class RelativeSearchServiceImpl implements RelativeSearchService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Autowired
	private FormDao formDao;

	@Override
	public void saveRelativeSearch(String formStore, String[] gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[] { store });
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "RelativeSearch", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		if (gridStore != null) {
			List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore[0]);
			if (grid == null)
				return;
			Object[] id = new Object[grid.size()];
			for (int i = 0; i < grid.size(); i++) {
				id[i] = baseDao.getSeqId("RelativeSearchForm_SEQ");
				grid.get(i).put("rsf_id", id[i]);
			}
			List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "RelativeSearchForm");
			baseDao.execute(gridSql);
			// 保存VoucherDetail
			grid = BaseUtil.parseGridStoreToMaps(gridStore[1]);
			id = new Object[grid.size()];
			for (int i = 0; i < grid.size(); i++) {
				id[i] = baseDao.getSeqId("RelativeSearchGrid_SEQ");
				grid.get(i).put("rsg_id", id[i]);
			}
			gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "RelativeSearchGrid");
			baseDao.execute(gridSql);
		}
		try {
			// 记录操作
			baseDao.logger.save(caller, "rs_id", store.get("rs_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	@CacheEvict(value = "relativesearch", allEntries = true)
	public void updateRelativeSearchById(String formStore, String[] gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "RelativeSearch", "rs_id");
		baseDao.execute(formSql);
		if (gridStore != null) {
			List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore[0]);
			if (grid == null)
				return;
			List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(grid, "RelativeSearchForm", "rsf_id");
			for (Map<Object, Object> s : grid) {
				if (s.get("rsf_id") == null || s.get("rsf_id").equals("") || s.get("rsf_id").toString().equals("0")) {// 新添加的数据，id不存在
					int id = baseDao.getSeqId("RelativeSearchForm_SEQ");
					String sql = SqlUtil.getInsertSqlByMap(s, "RelativeSearchForm", new String[] { "rsf_id" }, new Object[] { id });
					gridSql.add(sql);
				}
			}
			baseDao.execute(gridSql);
			grid = BaseUtil.parseGridStoreToMaps(gridStore[1]);
			gridSql = SqlUtil.getUpdateSqlbyGridStore(grid, "RelativeSearchGrid", "rsg_id");
			for (Map<Object, Object> s : grid) {
				if (s.get("rsg_id").toString().equals("0") || s.get("rsg_id") == null || s.get("rsg_id").equals("")) {
					int id = baseDao.getSeqId("RelativeSearchGrid_SEQ");
					String sql = SqlUtil.getInsertSqlByMap(s, "RelativeSearchGrid", new String[] { "rsg_id" }, new Object[] { id });
					gridSql.add(sql);
				}
			}
			baseDao.execute(gridSql);
		}
		baseDao.logger.update(caller, "rs_id", store.get("rs_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });

	}

	@Override
	@CacheEvict(value = "relativesearch", allEntries = true)
	public void deleteRelativeSearch(int rs_id, String caller) {
		handlerService.beforeDel(caller, new Object[] { rs_id });

		baseDao.deleteById("RelativeSearch", "rs_id", rs_id);

		baseDao.deleteById("RelativeSearchForm", "rsf_rsid", rs_id);

		baseDao.deleteById("RelativeSearchGrid", "rsg_rsid", rs_id);

		baseDao.logger.delete(caller, "rs_id", rs_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { rs_id });

	}

	@Override
	public RelativeSearch getRelativeSearch(int id) {
		return formDao.getRelativeSearch(id, SpObserver.getSp());
	}

	@Override
	@CacheEvict(value = "relativesearch", allEntries = true)
	@Transactional
	public void saveRelativeSearch(final RelativeSearch relativeSearch) {
		RelativeSearch oldOne = baseDao.queryBean("select * from relativeSearch where rs_caller=? and rs_title=?", RelativeSearch.class,
				relativeSearch.getRs_caller(), relativeSearch.getRs_title());
		if (null != oldOne) {
			// 覆盖原配置
			baseDao.deleteById("RelativeSearch", "rs_id", oldOne.getRs_id());
			baseDao.deleteById("RelativeSearchForm", "rsf_rsid", oldOne.getRs_id());
			baseDao.deleteById("RelativeSearchGrid", "rsg_rsid", oldOne.getRs_id());
		}
		// 重新取ID
		int newId = baseDao.getSeqId("RelativeSearch_SEQ");
		relativeSearch.setRs_id(newId);
		baseDao.save(relativeSearch, "RelativeSearch");
		if (null != relativeSearch.getForms()) {
			for (RelativeSearch.Form form : relativeSearch.getForms()) {
				form.setRsf_rsid(newId);
				form.setRsf_id(baseDao.getSeqId("RelativeSearchForm_SEQ"));
			}
			baseDao.save(relativeSearch.getForms(), "RelativeSearchForm");
		}
		if (null != relativeSearch.getGrids()) {
			for (RelativeSearch.Grid grid : relativeSearch.getGrids()) {
				grid.setRsg_rsid(newId);
				grid.setRsg_id(baseDao.getSeqId("RelativeSearchGrid_SEQ"));
			}
			baseDao.save(relativeSearch.getGrids(), "RelativeSearchGrid");
		}

	}
}
