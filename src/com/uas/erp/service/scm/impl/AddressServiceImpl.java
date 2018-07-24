package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.AddressService;

@Service
public class AddressServiceImpl implements AddressService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveAddress(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Address", "ad_code='" + store.get("ad_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store});
		//保存
		Object detno = baseDao.getFieldDataByCondition("Address", "max(ad_detno)", "1=1");
		store.put("ad_detno", Integer.parseInt(detno.toString()) + 1);
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Address", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "ad_id", store.get("ad_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}
	@Override
	public void deleteAddress(int ad_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{ad_id});
		//删除
		baseDao.deleteById("Address", "ad_id", ad_id);
		// 记录操作
		baseDao.logger.delete(caller, "ad_id", ad_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{ad_id});
	}
	@Override
	public void updateAddressById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Address", "ad_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "ad_id", store.get("ad_id"));
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}
}
