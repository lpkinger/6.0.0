package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.MadeinSetupService;

@Service
public class MadeinSetupServiceImpl implements MadeinSetupService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveMadeinSetup(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("MadeinSetup", " ms_code='" + store.get("ms_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave("MadeinSetup", new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "MadeinSetup", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		baseDao.logger.save("MadeinSetup", "ms_id", store.get("ms_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave("MadeinSetup", new Object[]{store});
	}
	@Override
	public void deleteMadeinSetup(int ms_id) {
		//执行删除前的其它逻辑
		handlerService.beforeDel("MadeinSetup", ms_id);
		//删除
		baseDao.deleteById("MadeinSetup", "ms_id", ms_id);
		//记录操作
		baseDao.logger.delete("MadeinSetup", "ms_id", ms_id);
		//执行删除后的其它逻辑
		handlerService.afterDel("MadeinSetup", ms_id);
	}
	@Override
	public void updateMadeinSetupById(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeSave("MadeinSetup", new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "MadeinSetup", "ms_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update("MadeinSetup", "ms_id", store.get("ms_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave("MadeinSetup", new Object[]{store});
	}
}
