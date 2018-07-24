package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.fa.AssetsReasonService;

@Service("assetsReasonService")
public class AssetsReasonServiceImpl implements AssetsReasonService {
	@Autowired
	private BaseDao baseDao;

	@Override
	public void saveAssetsReason(String caller, String gridStore) {
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		List<String> insertSql = new ArrayList<String>();
		List<Map<Object, Object>> updateMap = new ArrayList<Map<Object, Object>>();
		for (Map<Object, Object> s : gstore) {
			if (s.get("ar_id") == null || s.get("ar_id").equals("")
					|| s.get("ar_id").equals("0")
					|| Integer.parseInt(s.get("ar_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("AssetsReason_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "AssetsReason",
						new String[] { "ar_id" }, new Object[] { id });
				insertSql.add(sql);
			} else {
				updateMap.add(s);
			}
		}
		List<String> updateSql = SqlUtil.getUpdateSqlbyGridStore(updateMap,
				"AssetsReason", "ar_id");
		for (String s : insertSql) {
			updateSql.add(s);
		}
		baseDao.execute(updateSql);
		// 记录操作
		baseDao.logger.update(caller, "ar_id", gstore.get(0).get("ar_id"));
	}
}
