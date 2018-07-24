package com.uas.erp.service.hr.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.QuestionsetService;

@Service
public class QuestionsetServiceImpl implements QuestionsetService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveQuestionset(String formStore, String caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[]{store});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Questionset", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "qs_id", store.get("qs_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void updateQuestionsetById(String formStore, String caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Questionset", "qs_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "qs_id", store.get("qs_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[]{store});
	}

	@Override
	public void deleteQuestionset(int qs_id, String caller) {
		
		handlerService.beforeDel(caller, new Object[]{qs_id});
		//删除
		baseDao.deleteById("Questionset", "qs_id", qs_id);
		//记录操作
		baseDao.logger.delete(caller, "qs_id", qs_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{qs_id});
	}
}
