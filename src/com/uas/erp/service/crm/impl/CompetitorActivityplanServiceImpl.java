package com.uas.erp.service.crm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;


import com.uas.erp.service.crm.CompetitorActivityplanService;

@Service
public class CompetitorActivityplanServiceImpl implements CompetitorActivityplanService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCompetitorActivityplan(String formStore,  String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "CompetitorActivityplan", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save(caller, "cap_id", store.get("cap_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void deleteCompetitorActivityplan(int cap_id,  String caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, cap_id);
		//删除purchase
		baseDao.deleteById("CompetitorActivityplan", "cap_id", cap_id);
		//记录操作
		baseDao.logger.delete(caller, "cap_id", cap_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, cap_id);
	}

	@Override
	public void updateCompetitorActivityplan(String formStore, 	String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		//修改CompetitorActivityplan
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "CompetitorActivityplan", "cap_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "cap_id", store.get("cap_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
	}
}
