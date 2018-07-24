package com.uas.erp.service.plm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.plm.TestTemplateService;

@Service
public class TestTemplateServiceImpl implements TestTemplateService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveTestTemplate(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler("TestTemplate", "save", "before", new Object[] { store });
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "TestTemplate", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.logger.save("MeetingRoom", "tt_id", store.get("tt_id"));
		handlerService.handler("TestTemplate", "save", "after", new Object[] { store });

	}

	@Override
	public void updateTestTemplate(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.handler("TestTemplate", "save", "before", new Object[] { store });
		// 修改MeetingRoom
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "TestTemplate", "mr_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update("MeetingRoom", "tt_id", store.get("tt_id"));
		// 执行修改后的其它逻辑
		handlerService.handler("TestTemplate", "save", "after", new Object[] { store });

	}

	@Override
	public void deleteTestTemplate(int tt_id) {
		// 执行删除前的其它逻辑
		handlerService.handler("TestTemplate", "delete", "before", new Object[] { tt_id });
		// 删除purchase
		baseDao.deleteById("TestTemplate", "tt_id", tt_id);
		// 记录操作
		baseDao.logger.delete("MeetingRoom", "tt_id", tt_id);
		// 执行删除后的其它逻辑
		handlerService.handler("MeetingRoom", "delete", "after", new Object[] { tt_id });
	}

}
