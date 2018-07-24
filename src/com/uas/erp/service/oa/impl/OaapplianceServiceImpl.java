package com.uas.erp.service.oa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.OaapplianceService;

@Service
public class OaapplianceServiceImpl implements OaapplianceService {
	
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void saveOaappliance(String formStore, String caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int count=baseDao.getCountByCondition("Oaappliance", "oa_procode='"+store.get("oa_procode")+"'");
		if(count>=1){
			BaseUtil.showError("该编号已存在，请更换后重试！");
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		boolean bool = baseDao.checkByCondition("Oaappliance", "oa_procode='" + store.get("oa_procode") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Oaappliance", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save(caller, "oa_id", store.get("oa_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});

	}

	@Override
	public void updateOaappliance(String formStore, String caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		//修改Oaappliance
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Oaappliance", "oa_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "oa_id", store.get("oa_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});

	}

	@Override
	public void deleteOaappliance(int oa_id, String caller) {
		
		handlerService.beforeDel(caller, new Object[]{oa_id});
		//删除purchase
		baseDao.deleteById("Oaappliance", "oa_id", oa_id);
		//记录操作
		baseDao.logger.delete(caller, "oa_id", oa_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{oa_id});

	}

}
