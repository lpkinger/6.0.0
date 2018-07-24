package com.uas.erp.service.oa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.VehiclereturnService;
@Service
public class VehiclereturnServiceImpl implements VehiclereturnService{
	   @Autowired
	   private BaseDao baseDao;
	   @Autowired
	   private HandlerService handlerService;
	@Override
	public void saveVehiclereturn(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller,new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Vehiclereturn", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		baseDao.logger.save(caller, "vr_id", store.get("vr_id"));
		String costtotal="update Vehiclereturn set vr_costtotal=vr_roadcost+vr_oilcost where vr_id="+store.get("vr_id");		
		//修改申请单的标识
		baseDao.updateByCondition("Vehicleapply", "va_isback=1", "va_code='"+store.get("vr_vacode")+"'");
		baseDao.execute(costtotal);
		handlerService.afterSave(caller,new Object[]{store});
	}
	
	@Override
	public void confirmVehiclereturn(int id, String caller) {
		// TODO Auto-generated method stub
		Object status = baseDao.getFieldDataByCondition("Vehiclereturn", "vr_statuscode", "vr_id=" + id);
		if(!status.equals("AUDITED")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.confirm_onlyAudit"));
		}
		Object isback = baseDao.getFieldDataByCondition("Vehiclereturn", "vr_isback", "vr_id=" + id);
		if("1".equals(isback.toString())){
			BaseUtil.showError("车辆已返回，无需再确认！");
		}
		else{
			Object[] objs=baseDao.getFieldsDataByCondition("Vehiclereturn", new String[]{"vr_vecard","vr_enddistance"}, " vr_id="+id+"");
			if(objs[1]!=null)
			{baseDao.execute("update Vehiclearchives set va_km="+Double.parseDouble(objs[1].toString())+" where va_card='"+objs[0]+"'");}
		}
		//执行反审核操作		
		baseDao.updateByCondition("Vehiclereturn", "vr_isback=1", "vr_id=" + id);
		baseDao.updateByCondition("Vehicleapply", "va_isback=1", "va_vrid=" + id);
		
		//记录操作
		baseDao.logger.getMessageLog(BaseUtil.getLocalMessage("msg.confirm"), BaseUtil.getLocalMessage("msg.confirmSuccess"), caller, "vr_id", id);
	}

	@Override
	public void resConfirmVehiclereturn(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Vehiclereturn", "vr_statuscode", "vr_id=" + id);
		if(!status.equals("AUDITED")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resConfirm_onlyAudit"));
		}
		Object isback = baseDao.getFieldDataByCondition("Vehiclereturn", "vr_isback", "vr_id=" + id);
		if(!"1".equals(isback.toString())){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resConfirm_onlyConfirm"));
		}
		else{
			Object[] objs=baseDao.getFieldsDataByCondition("Vehiclereturn", new String[]{"vr_vecard","vr_enddistance"}, " vr_id="+id+"");
			if(objs[1]!=null)
			{baseDao.execute("update Vehiclearchives set va_km=nvl(va_km,0)-"+Double.parseDouble(objs[1].toString())+" where va_card='"+objs[0]+"'");}
		}
		//执行反审核操作		
		baseDao.updateByCondition("Vehiclereturn", "vr_isback=0", "vr_id=" + id);
		baseDao.updateByCondition("Vehicleapply", "va_isback=0", "va_vrid=" + id);		
		//记录操作
		baseDao.logger.getMessageLog(BaseUtil.getLocalMessage("msg.resConfirm"),BaseUtil.getLocalMessage("msg.resConfirmSuccess"), caller, "vr_id", id);			
	}

}
