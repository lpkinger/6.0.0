package com.uas.erp.service.hr.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.ContractsetService;

@Service
public class ContractsetServiceImpl implements ContractsetService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveContractset(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);		
		handlerService.beforeSave(caller, new Object[]{store});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Contractset", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "cs_id", store.get("cs_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void updateContractsetById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Contractset", "cs_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "cs_id", store.get("cs_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});

	}

	@Override
	public void deleteContractset(int cs_id, String  caller) {
		//执行删除前的其它逻辑
		    handlerService.beforeDel(caller, new Object[]{cs_id});
			//删除
			baseDao.deleteById("Contractset", "cs_id", cs_id);
			//记录操作
			baseDao.logger.delete(caller, "cs_id", cs_id);
			//执行删除后的其它逻辑
			handlerService.afterDel(caller, new Object[]{cs_id});
	}

}
