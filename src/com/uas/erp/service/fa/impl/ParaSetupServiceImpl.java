package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.fa.ParaSetupService;

@Service
public class ParaSetupServiceImpl implements ParaSetupService {

	@Autowired
	BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void updateParaSetupById(String formStore, String gridStore,
			String caller) {

		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] { gstore });
		// 执行修改前的其它逻辑;*/
		/*
		 * handlerService.handler("ParaSetup", "save", "before", new
		 * Object[]{gstore.get(0), language});
		 */
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"ParaSetup", "ps_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("ps_id") == null || s.get("ps_id").equals("")
					|| s.get("ps_id").equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("ParaSetup_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "ParaSetup",
						new String[] { "ps_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "ps_id", gstore.get(0).get("ps_id"));
		handlerService.afterUpdate(caller, new Object[] { gstore });
		// 执行修改后的其它逻辑
		/*
		 * handlerService.handler("ParaSetup", "save", "after", new
		 * Object[]{gstore.get(0), language});
		 */
	}

}
