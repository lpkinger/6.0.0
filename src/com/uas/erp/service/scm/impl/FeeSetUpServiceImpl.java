package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.FeeSetUpService;

@Service
public class FeeSetUpServiceImpl implements FeeSetUpService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveFeeSetUp(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("FeeSetUp", "fs_code='" + store.get("fs_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave("FeeSetUp", new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "FeeSetUp", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		baseDao.logger.save("FeeSetUp", "fs_id", store.get("fs_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave("FeeSetUp", new Object[]{store});
	}
	@Override
	public void deleteFeeSetUp(int fs_id) {
		//执行删除前的其它逻辑
		handlerService.beforeDel("FeeSetUp", fs_id);
		//删除
		baseDao.deleteById("FeeSetUp", "fs_id", fs_id);
		//记录操作
		baseDao.logger.delete("FeeSetUp", "fs_id", fs_id);
		//执行删除后的其它逻辑
		handlerService.afterDel("FeeSetUp", fs_id);
	}
	@Override
	public void updateFeeSetUpById(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeSave("FeeSetUp", new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "FeeSetUp", "fs_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update("FeeSetUp", "fs_id", store.get("fs_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave("FeeSetUp", new Object[]{store});
	}
}
