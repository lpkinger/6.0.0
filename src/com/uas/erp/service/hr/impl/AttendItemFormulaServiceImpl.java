package com.uas.erp.service.hr.impl;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.AttendItemFormulaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AttendItemFormulaServiceImpl implements AttendItemFormulaService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveAttendItemFormula(String formStore, String caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);	
		handlerService.beforeSave(caller,new Object[]{store});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "AttendItemFormula", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "aif_id", store.get("aif_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller,new Object[]{store});
	}

	@Override
	public void updateAttendItemFormulaById(String formStore, String caller) {	
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "AttendItemFormula", "aif_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "aif_id", store.get("aif_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
	}

	@Override
	public void deleteAttendItemFormula(int aif_id, String  caller) {

		handlerService.beforeDel(caller,new Object[]{aif_id});
		//删除
		baseDao.deleteById("AttendItemFormula", "aif_id", aif_id);
		//记录操作
		baseDao.logger.delete(caller, "aif_id", aif_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[]{aif_id});
		
	}

}
