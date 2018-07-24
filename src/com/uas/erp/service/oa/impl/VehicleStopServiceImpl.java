package com.uas.erp.service.oa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.VehicleStopService;
@Service
public class VehicleStopServiceImpl implements VehicleStopService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveVehicleStop(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "VehicleStop", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save(caller, "vs_id", store.get("vs_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void updateVehicleStop(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//修改VehicleStop
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "VehicleStop", "vs_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.delete(caller, "vs_id", store.get("vs_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
		
	}

	@Override
	public void deleteVehicleStop(int vs_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{vs_id});
		//删除purchase
		baseDao.deleteById("VehicleStop", "vs_id", vs_id);
		//记录操作
		baseDao.logger.delete(caller, "vs_id", vs_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{vs_id});
	}
}
