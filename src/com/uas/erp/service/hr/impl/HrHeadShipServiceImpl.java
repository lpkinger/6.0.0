package com.uas.erp.service.hr.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.HrHeadShipService;

@Service
public class HrHeadShipServiceImpl implements HrHeadShipService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveHrHeadShip(String formStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("HrHeadShip", "hs_code='" + store.get("hs_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "HrHeadShip", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "hs_id", store.get("hs_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}
	@Override
	public void deleteHrHeadShip(int hs_id, String  caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{hs_id});
		//删除
		baseDao.deleteById("HrHeadShip", "hs_id", hs_id);
		//记录操作
		baseDao.logger.delete(caller, "hs_id", hs_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{hs_id});
	}
	@Override
	public void updateHrHeadShipById(String formStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "HrHeadShip", "hs_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "hs_id", store.get("hs_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}
}
