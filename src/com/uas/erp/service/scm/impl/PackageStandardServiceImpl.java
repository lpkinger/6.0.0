package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.PackageStandardService;

@Service
public class PackageStandardServiceImpl implements PackageStandardService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	@Override
	public void savePackageStandard(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("PackageStandard", "ps_code='" + store.get("ps_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PackageStandard", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		baseDao.logger.save(caller, "ps_id", store.get("ps_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}
	
	@Override
	public void deletePackageStandard(int ps_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, ps_id);
		baseDao.delCheck("PackageStandard", ps_id);
		//删除
		baseDao.deleteById("PackageStandard", "ps_id", ps_id);
		//记录操作
		baseDao.logger.delete(caller, "ps_id", ps_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, ps_id);
	}
	
	@Override
	public void updatePackageStandardById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PackageStandard", "ps_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "ps_id", store.get("ps_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}
}
