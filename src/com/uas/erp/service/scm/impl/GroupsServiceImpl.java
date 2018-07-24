package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.GroupsService;

@Service
public class GroupsServiceImpl implements GroupsService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void saveGroups(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Groups", "gr_code='" + store.get("gr_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave("Groups", new Object[]{store});
		//保存Groups
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Groups", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save("Groups", "gr_id", store.get("gr_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave("Groups", new Object[]{store});
	}
	
	@Override
	public void deleteGroups(int gr_id) {
		//执行删除前的其它逻辑
		handlerService.beforeDel("Groups", gr_id);
		//删除
		baseDao.deleteById("Groups", "gr_id", gr_id);
		//记录操作
		baseDao.logger.delete("Groups", "gr_id", gr_id);
		//执行删除后的其它逻辑
		handlerService.afterDel("Groups", gr_id);
	}
	
	@Override
	public void updateGroupsById(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeSave("Groups", new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Groups", "gr_id");
		baseDao.execute(formSql);
		baseDao.logger.update("Groups", "gr_id", store.get("gr_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave("Groups", new Object[]{store});
	}	
}
