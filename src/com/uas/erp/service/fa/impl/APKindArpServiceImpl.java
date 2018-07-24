package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.fa.APKindArpService;

@Service("apKindArpService")
public class APKindArpServiceImpl implements APKindArpService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveAPKind(String caller, String gridStore) {
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// List<String> gridSql = new ArrayList<String>();
		// List<Map<Object, Object>> maps =
		// BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行修改前的其它逻辑;*/
		handlerService.beforeSave(caller, new Object[] { gstore });
		// List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore,
		// "AssetsLocation","al_id",null);

		// List<String> updateSql =new ArrayList<String>();
		List<String> insertSql = new ArrayList<String>();
		List<Map<Object, Object>> updateMap = new ArrayList<Map<Object, Object>>();

		for (Map<Object, Object> s : gstore) {
			if (s.get("ak_id") == null || s.get("ak_id").equals("")
					|| s.get("ak_id").equals("0")
					|| Integer.parseInt(s.get("ak_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("APKind_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "APKind",
						new String[] { "ak_id" }, new Object[] { id });
				insertSql.add(sql);
			} else {

				updateMap.add(s);
				// String sql = SqlUtil.getUpdateSqlbyGridStore(maps,
				// "AssetsLocation", "al_id");

			}
		}
		List<String> updateSql = SqlUtil.getUpdateSqlbyGridStore(updateMap,
				"APKind", "ak_id");

		for (String s : insertSql) {

			updateSql.add(s);
		}

		baseDao.execute(updateSql);
		// 记录操作
		baseDao.logger.update(caller, "ak_id", gstore.get(0).get("ak_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { gstore });

	}

}
