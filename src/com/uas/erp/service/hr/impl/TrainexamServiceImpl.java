package com.uas.erp.service.hr.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.TrainexamService;

@Service
public class TrainexamServiceImpl implements TrainexamService {

	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void saveTrainexam(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[]{store});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Trainexam", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "te_id", store.get("te_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void updateTrainexamById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Trainexam", "te_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "te_id", store.get("te_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
	}

	@Override
	public void deleteTrainexam(int te_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[]{te_id});
		//删除
		baseDao.deleteById("Trainexam", "te_id", te_id);
		//记录操作
		baseDao.logger.delete(caller, "te_id", te_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[]{te_id});
	}
}
