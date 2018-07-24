package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.LienSetUpService;

@Service
public class LienSetUpServiceImpl implements LienSetUpService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void saveLienSetUp(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("LienSetUp", "ls_code='" + store.get("ls_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave("LienSetUp", new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "LienSetUp", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save("LienSetUp", "ls_id", store.get("ls_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave("LienSetUp", new Object[]{store});
	}
	
	@Override
	public void deleteLienSetUp(int ls_id) {
		//执行删除前的其它逻辑
		handlerService.beforeDel("LienSetUp", ls_id);
		//删除
		baseDao.deleteById("LienSetUp", "ls_id", ls_id);
		//记录操作
		baseDao.logger.delete("LienSetUp", "ls_id", ls_id);
		//执行删除后的其它逻辑
		handlerService.afterDel("LienSetUp", ls_id);
	}
	
	@Override
	public void updateLienSetUpById(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeSave("LienSetUp", new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "LienSetUp", "ls_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update("LienSetUp", "ls_id", store.get("ls_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave("LienSetUp", new Object[]{store});
	}
}
