package com.uas.erp.service.pm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.BOMDetailGroupService;



@Service
public class BOMDetailGroupServiceImpl implements BOMDetailGroupService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveBOMDetailGroup(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行保存前的其它逻辑
		handlerService.beforeSave("BOMDetailGroup",new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BOMDetailGroup", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "bdg_id", store.get("bdg_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}		
		//执行保存后的其它逻辑
		handlerService.afterSave("BOMDetailGroup",new Object[]{formStore});
	}
	
	@Override
	public void deleteBOMDetailGroup(int bdg_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel("BOMDetailGroup",new Object[]{bdg_id});
		//删除
		baseDao.deleteById("BOMDetailGroup", "bdg_id", bdg_id);
		//记录操作
		baseDao.logger.delete(caller, "bdg_id", bdg_id);
		//执行删除后的其它逻辑
		handlerService.afterDel("BOMDetailGroup",new Object[]{bdg_id});
	}
	
	@Override
	public void updateBOMDetailGroupById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeSave("WorkBOMDetailGroup", new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BOMDetailGroup", "bdg_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "bdg_id", store.get("bdg_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave("BOMDetailGroup", new Object[]{store});
	}
}
