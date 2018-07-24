package com.uas.erp.service.oa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.VehicleuseService;

@Service
public class VehicleuseServiceImpl implements VehicleuseService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveVehicleuse(String formStore, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		
		handlerService.beforeSave(caller, new Object[]{store});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Vehicleuse", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "vu_id", store.get("vu_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void updateVehicleuseById(String formStore, String caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Vehicleuse", "vu_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "vu_id", store.get("vu_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});

	}

	@Override
	public void deleteVehicleuse(int vu_id, String caller) {
		
		handlerService.beforeDel(caller, new Object[]{vu_id});
		//删除
		baseDao.deleteById("Vehicleuse", "vu_id", vu_id);
		//记录操作
		baseDao.logger.delete(caller, "vu_id", vu_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{vu_id});

	}

}
