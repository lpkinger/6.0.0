package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.CheckItemService;

@Service
public class CheckItemServiceImpl implements CheckItemService{
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCheckItem(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("QUA_CheckItem", "ci_code='" + store.get("ci_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store});
		store.put("ci_checkstatuscode", "ENTERING");
		//保存QUA_CheckItem
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "QUA_CheckItem", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save(caller, "ci_id", store.get("ci_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void updateCheckItemById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);	
		//执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store});
		//修改QUA_CheckItem
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "QUA_CheckItem", "ci_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "ci_id", store.get("ci_id"));
		//执行修改前的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});		
	}

	@Override
	public void deleteCheckItem(int ci_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{ci_id});
		//删除QUA_CheckItem
		baseDao.deleteById("QUA_CheckItem", "ci_id", ci_id);		
		//记录操作
		baseDao.logger.delete(caller, "ci_id", ci_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{ci_id});		
	}

	@Override
	public void printCheckItem(int ci_id, String caller) {
		//执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[]{ci_id});
		//执行打印操作
		//TODO
		//记录操作
		baseDao.logger.print(caller, "ci_id", ci_id);
		//执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[]{ci_id});	
	}
}
