package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.ProdPackService;

@Service
public class ProdPackServiceImpl implements ProdPackService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void saveProdPack(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行保存前的其它逻辑
		handlerService.beforeSave("ProdPack", new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ProdPack", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		baseDao.logger.save("ProdPack", "pp_id", store.get("pp_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave("ProdPack", new Object[]{store});
	}
	
	@Override
	public void deleteProdPack(int pp_id) {
		//执行删除前的其它逻辑
		handlerService.beforeDel("ProdPack", pp_id);
		//删除
		baseDao.deleteById("ProdPack", "pp_id", pp_id);
		//记录操作
		baseDao.logger.delete("ProdPack", "pp_id", pp_id);
		//执行删除后的其它逻辑
		handlerService.afterDel("ProdPack", pp_id);
	}
	
	@Override
	public void updateProdPackById(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeSave("ProdPack", new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProdPack", "pp_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update("ProdPack", "pp_id", store.get("pp_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave("ProdPack", new Object[]{store});
	}
}
