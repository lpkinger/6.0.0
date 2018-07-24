package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.fa.BadDebitRateService;

@Service
public class BadDebitRateServiceImpl implements BadDebitRateService {
	@Autowired
	BaseDao baseDao;

	@Override
	public void updateBadDebitRateById(String formStore, String gridStore,
			String caller) {
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		List<String> insertSql = new ArrayList<String>();
		List<Map<Object, Object>> updateMap = new ArrayList<Map<Object, Object>>();
		for (Map<Object, Object> s : gstore) {
			if (s.get("bdr_id") == null || s.get("bdr_id").equals("")
					|| s.get("bdr_id").equals("0")
					|| Integer.parseInt(s.get("bdr_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("BadDebitRate_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "BadDebitRate",
						new String[] { "bdr_id" }, new Object[] { id });
				insertSql.add(sql);
			} else {
				updateMap.add(s);
			}
		}
		List<String> updateSql = SqlUtil.getUpdateSqlbyGridStore(updateMap,
				"BadDebitRate", "bdr_id");
		for (String s : insertSql) {
			updateSql.add(s);
		}
		baseDao.execute(updateSql);
		// 记录操作
		baseDao.logger.update(caller, "bdr_id", gstore.get(0).get("bdr_id"));
	}

	@Override
	public void confirmBadDebtProvision(String data, String caller) {
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(data);
		if (map.containsKey("pd_detno")) {
			Object o = map.get("pd_detno");
			String res = baseDao.callProcedure("SP_ARBADDEBTS", new Object[] {
					o, SystemSession.getUser().getEm_name() });
			if (res != null && !res.trim().equals("OK")) {
				BaseUtil.showError(res);
			}
		}

	}
}
