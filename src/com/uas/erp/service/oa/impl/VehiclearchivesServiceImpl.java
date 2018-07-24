package com.uas.erp.service.oa.impl;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.VehiclearchivesService;

@Service
public class VehiclearchivesServiceImpl implements VehiclearchivesService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;	
	
	@Override
	public void saveVehiclearchives(String formStore, String gridStore,
			String  caller) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[]{store,gstore});
		boolean bool = baseDao.checkByCondition("Vehiclearchives", "va_code='" + store.get("va_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("oa.Vehiclearchives.save_vdcodeHasExist"));
		}
		bool= baseDao.checkByCondition("Vehiclearchives", "va_card='" + store.get("va_card") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("oa.Vehiclearchives.save_vacardHasExist"));
		}
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Vehiclearchives", new String[]{}, new Object[]{});
		baseDao.execute(formSql);		
		try{
			//记录操作
			baseDao.logger.save(caller, "va_id", store.get("va_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store,gstore});

	}

	@Override
	public void updateVehiclearchivesById(String formStore,String gridStore,
			String  caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller,  new Object[]{store,gstore});
		//修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Vehiclearchives", "va_id");
		baseDao.execute(formSql);
		//修改purchaseDetail		
		//记录操作
		baseDao.logger.update(caller, "va_id", store.get("va_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller,  new Object[]{store,gstore});

	}

	@Override
	public void deleteVehiclearchives(int va_id, String  caller) {
		int count1=baseDao.getCount("select count(1) from Vehiclereturn where vr_vecard=(select va_card from Vehiclearchives where va_id='"+va_id+"')");		
		if(count1!=0){
			BaseUtil.showError("此车辆档案已被使用，不能删除！");
		}
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller,  new Object[]{va_id});
		//删除purchase
		baseDao.deleteById("Vehiclearchives", "va_id", va_id);
		//删除purchaseDetail
		/*baseDao.deleteById("Vehiclearchivesdetail", "vd_vaid", va_id);*/
		//记录操作
		baseDao.logger.delete(caller, "va_id", va_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller,  new Object[]{va_id});

	}

	@Override
	public void submitVehiclearchives(int id, String caller) {
		// TODO Auto-generated method stub
		Object status = baseDao.getFieldDataByCondition("Vehiclearchives", "va_statuscode", "va_id=" + id);
		StateAssert.submitOnlyEntering(status);
		handlerService.handler(caller, "commit", "before", new Object[] { id });
		// 执行反提交操作
		baseDao.submit("Vehiclearchives", "va_id=" + id, "va_status", "va_statuscode");		
		// 记录操作
		baseDao.logger.submit(caller, "va_id", id);
		handlerService.handler(caller, "commit", "after", new Object[] { id });
	}

	@Override
	public void resSubmitVehiclearchives(int id, String caller) {
		// TODO Auto-generated method stub
		Object status = baseDao.getFieldDataByCondition("Vehiclearchives", "va_statuscode", "va_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { id });
		// 执行反提交操作
		baseDao.resOperate("Vehiclearchives", "va_id=" + id, "va_status", "va_statuscode");		
		// 记录操作
		baseDao.logger.resSubmit(caller, "va_id", id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { id });
	}

	@Override
	public void auditVehiclearchives(int id, String caller) {
		// TODO Auto-generated method stub
		Object status = baseDao.getFieldDataByCondition("Vehiclearchives", "va_statuscode", "va_id=" + id);
		StateAssert.auditOnlyCommited(status);
		handlerService.handler(caller, "audit", "before", new Object[] { id });
		
		baseDao.audit("Vehiclearchives", "va_id=" + id, "va_status", "va_statuscode","va_auditdate", "va_auditman");		
		// 记录操作
		baseDao.logger.audit(caller, "va_id", id);
		handlerService.handler(caller, "audit", "after", new Object[] { id });
	}

	@Override
	public void resAuditVehiclearchives(int id, String caller) {
		// TODO Auto-generated method stub
		Object status = baseDao.getFieldDataByCondition("Vehiclearchives", "va_statuscode", "va_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.handler(caller, "resAudit", "before", new Object[] { id });
		
		baseDao.resOperate("Vehiclearchives", "va_id=" + id, "va_status", "va_statuscode");		
		// 记录操作
		baseDao.logger.resAudit(caller, "va_id", id);
		handlerService.handler(caller, "resAudit", "after", new Object[] { id });
	}

	//查看车辆档案时，检查车辆是否在使用中。
	@Override
	public String checkVehiclearchives(int id, String caller) {
		String va_isused="F";
		String v_now=DateUtil.parseDateToOracleString(
				Constant.YMD_HMS,"");
		Object va_card = baseDao.getFieldDataByCondition("Vehiclearchives", "va_card", "va_id=" + id);
		List<Object> vr_ids = baseDao.getFieldDatasByCondition("Vehiclereturn", "vr_id", "vr_vecard='" + va_card.toString()+"' and nvl(vr_isback,0)<>1 order by vr_id desc");
		for (Object vr_id : vr_ids) {
			boolean isExist = baseDao.checkIf("Vehicleapply", "va_vrid='" + vr_id.toString()+"' and va_time<"+v_now+" and "+v_now+"<va_endtime");	
			if(isExist){
				va_isused="T";
				break;
			}			
		}	
		return va_isused;
	}
}
