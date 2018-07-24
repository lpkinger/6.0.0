package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.CountryService;

@Service
public class CountryServiceImpl implements CountryService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveCountry(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Countrys", "co_code='" + store.get("co_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.handler("Country", "save", "before", new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Countrys", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save("Country", "co_id", store.get("co_id"));
		//执行保存后的其它逻辑
		handlerService.handler("Country", "save", "after", new Object[]{store});
	}

	@Override
	public void updateCountryById(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.handler("Country", "save", "before", new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Countrys", "co_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update("Country", "co_id", store.get("co_id"));
		//执行修改后的其它逻辑
		handlerService.handler("Country", "save", "after", new Object[]{store});
	}

	@Override
	public void deleteCountry(int co_id) {
		//执行删除前的其它逻辑
		handlerService.handler("Country", "delete", "before", new Object[]{co_id});
		//删除Countrys
		baseDao.deleteById("Countrys", "co_id", co_id);		
		//记录操作
		baseDao.logger.delete("Country", "co_id", co_id);
		//执行删除后的其它逻辑
		handlerService.handler("Country", "delete", "after", new Object[]{co_id});
	}
}
