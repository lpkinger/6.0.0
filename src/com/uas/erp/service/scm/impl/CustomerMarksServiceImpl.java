package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.CustomerMarksService;

@Service
public class CustomerMarksServiceImpl implements CustomerMarksService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveCustomerMarks(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("CustomerMarks", "cm_markcode='" + store.get("cm_markcode") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave("CustomerMarks", new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "CustomerMarks", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		baseDao.logger.save("CustomerMarks", "cm_id", store.get("cm_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave("CustomerMarks", new Object[]{store});
	}
	@Override
	public void deleteCustomerMarks(int cm_id) {
		//执行删除前的其它逻辑
		handlerService.beforeDel("CustomerMarks", cm_id);
		//删除
		baseDao.deleteById("CustomerMarks", "cm_id", cm_id);
		//记录操作
		baseDao.logger.delete("CustomerMarks", "cm_id", cm_id);
		//执行删除后的其它逻辑
		handlerService.afterDel("CustomerMarks", cm_id);
	}
	@Override
	public void updateCustomerMarksById(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeSave("CustomerMarks", new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "CustomerMarks", "cm_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update("CustomerMarks", "cm_id", store.get("cm_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave("CustomerMarks", new Object[]{store});
	}
}
