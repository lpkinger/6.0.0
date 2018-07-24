package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.PriceTermService;

@Service
public class PriceTermServiceImpl implements PriceTermService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void savePriceTerm(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("PriceTerm", "prt_code='" + store.get("prt_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PriceTerm", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		baseDao.logger.save(caller, "prt_id", store.get("prt_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}
	
	@Override
	public void deletePriceTerm(int prt_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, prt_id);
		//删除
		baseDao.deleteById("PriceTerm", "prt_id", prt_id);
		//记录操作
		baseDao.logger.delete(caller, "prt_id", prt_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, prt_id);
	}
	
	@Override
	public void updatePriceTermById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PriceTerm", "prt_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "prt_id", store.get("prt_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}
}
