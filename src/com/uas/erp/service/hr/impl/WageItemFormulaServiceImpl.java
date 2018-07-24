package com.uas.erp.service.hr.impl;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.WageItemFormulaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WageItemFormulaServiceImpl implements WageItemFormulaService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveWageItemFormula(String formStore, String caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		
		handlerService.beforeSave(caller,  new Object[]{store});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "WageItemFormula", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "wif_id", store.get("wif_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller,  new Object[]{store});
	}

	@Override
	public void updateWageItemFormulaById(String formStore, String caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "WageItemFormula", "wif_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "wif_id", store.get("wif_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[]{store});
	}

	@Override
	public void deleteWageItemFormula(int wif_id, String  caller) {

		handlerService.beforeDel(caller, new Object[]{wif_id});	
		//删除
		baseDao.deleteById("WageItemFormula", "wif_id", wif_id);
		//记录操作
		baseDao.logger.delete(caller, "wif_id", wif_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{wif_id});		
	}

}
