package com.uas.erp.service.oa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.VehicleTypeService;
@Service
public class VehicleTypeServiceImpl implements VehicleTypeService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveVehicleType(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int count=baseDao.getCount("select count(1) from VehicleType where vt_number='"+store.get("vt_number")+"'");		
		if(count!=0){
			BaseUtil.showError("此类型编号已存在！");
		}
		int count1=baseDao.getCount("select count(1) from VehicleType where vt_type='"+store.get("vt_type")+"'");		
		if(count1!=0){
			BaseUtil.showError("此车辆类型已存在！");
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "VehicleType", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save(caller, "vt_id", store.get("vt_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void updateVehicleType(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
			int count1=baseDao.getCount("select count(1) from VehicleType where vt_number='"+store.get("vt_number")+"'");		
			if(count1!=0){
				Object vt_id = baseDao.getFieldDataByCondition("VehicleType", "vt_id", "vt_number='" + store.get("vt_number")+"'");
				if(!vt_id.toString().equals(store.get("vt_id"))){
					BaseUtil.showError("此类型编号已存在！");
				}	
			}
			int count2=baseDao.getCount("select count(1) from VehicleType where vt_type='"+store.get("vt_type")+"'");		
			if(count2!=0){
				Object vt_id = baseDao.getFieldDataByCondition("VehicleType", "vt_id", "vt_type='" + store.get("vt_type")+"'");
				if(!vt_id.toString().equals(store.get("vt_id"))){
					BaseUtil.showError("此车辆类型已存在！");
				}
			}	
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		//修改VehicleType
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "VehicleType", "vt_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "vt_id", store.get("vt_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
		
	}

	@Override
	public void deleteVehicleType(int vt_id, String caller) {
		int count1=baseDao.getCount("select count(1) from Vehiclearchives where va_type=(select vt_type from VEHICLETYPE where vt_id='"+vt_id+"')");		
		if(count1!=0){
			BaseUtil.showError("此车辆类型已被使用，不能删除！");
		}
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{vt_id});
		//删除purchase
		baseDao.deleteById("VehicleType", "vt_id", vt_id);
		//记录操作
		baseDao.logger.delete(caller, "vt_id", vt_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{vt_id});
	}
}
