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

import com.uas.erp.service.fa.DepositKindService;

@Service("depositKindService")
public class DepositKindServiceImpl implements DepositKindService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveDepositKind(String caller, String gridStore) {
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { gstore });
		List<String> insertSql = new ArrayList<String>();
		List<Map<Object, Object>> updateMap = new ArrayList<Map<Object, Object>>();
		for (Map<Object, Object> s : gstore) {
			if (s.get("dk_id") == null || s.get("dk_id").equals("")
					|| s.get("dk_id").equals("0")
					|| Integer.parseInt(s.get("dk_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("DEPOSITKIND_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "DepositKind",
						new String[] { "dk_id" }, new Object[] { id });
				insertSql.add(sql);
			} else {
				updateMap.add(s);
			}
		}
		List<String> updateSql = SqlUtil.getUpdateSqlbyGridStore(updateMap,
				"DepositKind", "dk_id");
		for (String s : insertSql) {
			updateSql.add(s);
		}
		baseDao.execute(updateSql);
		// 记录操作
		baseDao.logger.update(caller, "dk_id", gstore.get(0).get("dk_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { gstore });
	}
}
