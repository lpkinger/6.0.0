package com.uas.erp.service.oa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.BasicDataService;

@Service
public class BasicDataServiceImpl implements BasicDataService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveBasicData(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller,new Object[]{store});
		//执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BasicData", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save(caller, "bd_id", store.get("bd_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller,new Object[]{store});
	}

	@Override
	public void updateBasicData(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,new Object[]{store});
		//修改BasicData
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BasicData", "bd_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "bd_id", store.get("bd_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[]{store});
		
	}

	@Override
	public void deleteBasicData(int bd_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[]{bd_id});
		//删除purchase
		baseDao.deleteById("BasicData", "bd_id", bd_id);
		//记录操作
		baseDao.logger.delete(caller, "bd_id", bd_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[]{bd_id});
	}
}
