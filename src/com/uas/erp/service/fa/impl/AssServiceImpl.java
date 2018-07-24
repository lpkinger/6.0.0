package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.fa.AssService;

@Service("assService")
public class AssServiceImpl implements AssService {
	@Autowired
	private BaseDao baseDao;

	@Override
	public void saveAss(String caller, String param) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		List<String> insertSql = new ArrayList<String>();
		List<Map<Object, Object>> updateMap = new ArrayList<Map<Object, Object>>();
		for (Map<Object, Object> s : gstore) {
			if (s.get("mta_id") == null || s.get("mta_id").equals("")
					|| s.get("mta_id").equals("0")
					|| Integer.parseInt(s.get("mta_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("MainTableAss_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "MainTableAss",
						new String[] { "mta_id" }, new Object[] { id });
				insertSql.add(sql);
			} else {
				updateMap.add(s);
			}
		}
		List<String> updateSql = SqlUtil.getUpdateSqlbyGridStore(updateMap,
				"MainTableAss", "mta_id");
		for (String s : insertSql) {
			updateSql.add(s);
		}
		baseDao.execute(updateSql);
		// 记录操作
		baseDao.logger.update(caller, "mta_id", gstore.get(0).get("mta_id"));
	}
}
