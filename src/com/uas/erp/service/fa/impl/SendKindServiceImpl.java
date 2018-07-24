package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.fa.SendKindService;

@Service("sendKindService")
public class SendKindServiceImpl implements SendKindService {
	@Autowired
	BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void updateSendKindById(String formStore, String gridStore,
			String caller) {
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行修改前的其它逻辑;*/
		/*
		 * handlerService.handler("ParaSetup", "save", "before", new
		 * Object[]{gstore.get(0), language});
		 */
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"SendKind", "sk_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("sk_id") == null || s.get("sk_id").equals("")
					|| s.get("sk_id").equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("SendKind_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "SendKind",
						new String[] { "sk_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "sk_id", gstore.get(0).get("sk_id"));
		// 执行修改后的其它逻辑
		/*
		 * handlerService.handler("ParaSetup", "save", "after", new
		 * Object[]{gstore.get(0), language});
		 */

	}

}
