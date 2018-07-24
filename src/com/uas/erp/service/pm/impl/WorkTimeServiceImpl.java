package com.uas.erp.service.pm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.WorkTimeService;


@Service
public class WorkTimeServiceImpl implements WorkTimeService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveWorkTime(String formStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("WorkTime", "wt_code='" + store.get("wt_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "WorkTime", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "wt_id", store.get("wt_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}	
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}
	
	@Override
	public void deleteWorkTime(int wt_id, String  caller) {
		//执行删除前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{wt_id});
		//删除
		baseDao.deleteById("WorkTime", "wt_id", wt_id);
		//记录操作
		baseDao.logger.delete(caller, "wt_id", wt_id);
		//执行删除后的其它逻辑
		handlerService.afterSave(caller, new Object[]{wt_id});
	}
	
	@Override
	public void updateWorkTimeById(String formStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "WorkTime", "wt_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "wt_id", store.get("wt_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
	}
}
