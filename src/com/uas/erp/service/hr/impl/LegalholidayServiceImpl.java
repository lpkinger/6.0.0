package com.uas.erp.service.hr.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.LegalholidayService;

@Service
public class LegalholidayServiceImpl implements LegalholidayService {

	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void saveLegalholiday(String formStore, String caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Legalholiday", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "lh_id", store.get("lh_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});

	}

	@Override
	public void updateLegalholidayById(String formStore, String caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Legalholiday", "lh_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "lh_id", store.get("lh_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});

	}

	@Override
	public void deleteLegalholiday(int lh_id, String  caller) {		
		handlerService.afterDel(caller,new Object[]{lh_id});
		//删除
		baseDao.deleteById("Legalholiday", "lh_id", lh_id);
		//记录操作
		baseDao.logger.delete(caller, "lh_id", lh_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[]{lh_id});

	}

}
