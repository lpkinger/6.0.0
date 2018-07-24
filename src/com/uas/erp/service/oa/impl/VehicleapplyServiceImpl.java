package com.uas.erp.service.oa.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.oa.VehicleapplyService;

@Service
public class VehicleapplyServiceImpl implements VehicleapplyService {

	static final String insert = " insert into Vehicleuse(vu_code,vu_vacode,vu_depart,vu_recordorid,vu_recodor," +
			"vu_date,vu_vecode,vu_vecard,vu_vetype,vu_driver,vu_address,vu_reason,vu_time,vu_allhours," +
			"vu_id)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	static final String update = " update Vehicleapply set va_isturn='1' where va_id=?";

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveVehicleapply(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int count=baseDao.getCount("select count(1) from Vehicleapply where va_code='"+store.get("va_code")+"'");		
		if(count!=0){
			BaseUtil.showError("此申请单号已存在！");
		}
		handlerService.beforeSave(caller, new Object[] { store});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Vehicleapply", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "va_id", store.get("va_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller, new Object[] { store});

	}

	@Override
	public void updateVehicleapplyById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int count1=baseDao.getCount("select count(1) from Vehicleapply where va_id<> "+store.get("va_id")+" and va_code='"+store.get("va_code")+"'");
		if(count1!=0){
			BaseUtil.showError("此申请单号已存在！");
		}
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store});
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Vehicleapply", "va_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "va_id", store.get("va_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store});

	}

	@Override
	public void deleteVehicleapply(int va_id, String  caller) {

		handlerService.beforeDel(caller,new Object[] { va_id});
		// 删除
		baseDao.deleteById("Vehicleapply", "va_id", va_id);
		// 记录操作
		baseDao.logger.delete(caller, "va_id", va_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[] { va_id});

	}

	@Override
	public void auditVehicleapply(int va_id, String  caller) {

		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Vehicleapply", "va_statuscode", "va_id=" + va_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { va_id});
		Object[] objs= baseDao.getFieldsDataByCondition("Vehicleapply",new String[]{"va_kind","va_depart","va_time","va_endtime","va_vecard","va_driver"}, "va_id=" + va_id);
		if("直接派车".equals(objs[0])){
			checkVehicleTime(objs[4].toString(),objs[2].toString(),objs[3].toString());
			Employee employee = SystemSession.getUser();
			int id = baseDao.getSeqId("Vehiclereturn_SEQ");
			String code = baseDao.sGetMaxNumber("Vehiclereturn", 2);
			String sqlstr="insert into Vehiclereturn (vr_id,vr_code,vr_vecard,vr_driver,vr_recorder,vr_recorddate,vr_starttime,vr_endtime,vr_status,vr_statuscode,vr_depart)values("+id+",'"+code+"','"+objs[4]+"','"+objs[5]+"','"+employee.getEm_name()+"',sysdate,to_date('"+objs[2].toString()+"','yyyy-MM-dd HH24:MI:SS'),to_date('"+objs[3].toString()+"','yyyy-MM-dd HH24:MI:SS'),'"+BaseUtil.getLocalMessage("AUDITED")+"','AUDITED','"+objs[1]+"')";
			baseDao.execute(sqlstr);
			String updateStr="update Vehicleapply set va_vrid="+id+",va_vrcode='"+code+"',va_turnstatus='已派车' where va_id="+va_id+"";
			baseDao.execute(updateStr);
			baseDao.execute("update Vehiclereturn set vr_stdistance=(select va_km from Vehiclearchives where va_card='"+objs[4]+"') where vr_id="+id);
		}
		// 执行审核操作
		baseDao.audit("Vehicleapply", "va_id=" + va_id, "va_status", "va_statuscode", "va_auditdate", "va_auditor");
		// 记录操作
		baseDao.logger.audit(caller, "va_id", va_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { va_id});

	}

	@Override
	public void resAuditVehicleapply(int va_id, String caller) {

		Object status = baseDao.getFieldDataByCondition("Vehicleapply", "va_statuscode", "va_id=" + va_id);
	    StateAssert.resAuditOnlyAudit(status);
	    Object va_turnstatus = baseDao.getFieldDataByCondition("Vehicleapply", "va_turnstatus", "va_id=" + va_id);
	    if(va_turnstatus!=null && va_turnstatus.toString().equals("已派车")){
	    	BaseUtil.showError("已转派车单,无法反审核");
	    }
		// 执行反审核操作
		baseDao.resOperate("Vehicleapply", "va_id=" + va_id, "va_status", "va_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "va_id", va_id);

	}

	@Override
	public void submitVehicleapply(int va_id, String  caller) {

		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Vehicleapply", "va_statuscode", "va_id=" + va_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller,new Object[] { va_id});
		/*Object[] objs= baseDao.getFieldsDataByCondition("Vehicleapply",new String[]{"va_kind","va_depart","va_time","va_endtime","va_vecard","va_driver"}, "va_id=" + va_id);
		if("直接派车".equals(objs[0])){//直接派车单提交时检测时间是否冲突
			checkVehicleTime(objs[4].toString(),objs[2].toString(),objs[3].toString());}	*/	
		// 执行提交操作
		baseDao.submit("Vehicleapply", "va_id=" + va_id, "va_status", "va_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "va_id", va_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller,new Object[] { va_id});

	}

	@Override
	public void resSubmitVehicleapply(int va_id, String caller) {
		handlerService.beforeResSubmit(caller, new Object[] { va_id});
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Vehicleapply", "va_statuscode", "va_id=" + va_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		baseDao.resOperate("Vehicleapply", "va_id=" + va_id, "va_status", "va_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "va_id", va_id);
		handlerService.afterResSubmit(caller, new Object[] { va_id});

	}

	@Override
	public void turnVehicle(JSONObject formStore, String  caller) {

		String code = baseDao.sGetMaxNumber("Vehicleuse", 2);
		int newId = baseDao.getSeqId("Vehicleuse_SEQ");
		boolean bool = baseDao.execute(
				insert,
				new Object[] { code, formStore.getString("va_code"), formStore.getString("va_depart"),
						SystemSession.getUser().getEm_id(), SystemSession.getUser().getEm_name(), formStore.getString("va_vecode"),
						formStore.getString("va_vetype"),
						formStore.getString("va_vecard"), formStore.getString("va_driver"),
						formStore.getString("va_address"), formStore.getString("va_reason"),
						formStore.getString("va_time"), formStore.getString("va_allhours"),
						Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), newId });
		if (bool) {
			baseDao.execute(update, new Object[] { formStore.getInt("va_id") });
		}
		baseDao.logger.getMessageLog(BaseUtil.getLocalMessage("oa.turnVehicle"), BaseUtil.getLocalMessage("oa.turnVehicleSuccess"), caller, "va_id", formStore.getInt("va_id"));

	}

	@Override
	public void backUpdateVehicle(String formStore, String caller) {
		// TODO Auto-generated method stub
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store});
		store.put("va_isback", 1);
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Vehicleapply", "va_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.getMessageLog("填写回填信息", "回填成功!", caller, "va_id", store.get("va_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store});

	}

	@Override
	public String turnReturnVehicle(String caller, String data) {
		// TODO Auto-generated method stub
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Object vecard = maps.get(0).get("va_vecard");
		Object driverman = maps.get(0).get("va_driver");
		Object vrcode=maps.get(0).get("va_vrcode");
		StringBuffer sb = new StringBuffer();
		if(vrcode != null && !"".equals(vrcode)){
			Object[] objs=baseDao.getFieldsDataByCondition("Vehiclereturn", new String[]{"vr_id","vr_isback"}, " vr_code='"+vrcode+"'");
			if("1".equals(objs[1])){
				return "指定的派车单已返回!";	
			}else{
				for (int i = 0; i < maps.size(); i++) {
					sb.append(maps.get(i).get("va_id") + ",");				
				}
				String range = sb.toString().substring(0, sb.toString().length() - 1);
				String upsqlstr="update Vehicleapply set (va_vrid,va_vrcode)=(select vr_id,vr_code from Vehiclereturn where vr_code='"+vrcode+"'),va_turnstatus='已派车' where va_id in ("+range+")";
				baseDao.execute(upsqlstr);
				return "派车成功,派车单号:<a href=\"javascript:openUrl('jsps/oa/vehicle/vehiclereturn.jsp?formCondition=vr_idIS" + objs[0]
						+ "&gridCondition=va_vridIS"+objs[0]+"&whoami=Vehiclereturn')\">" + vrcode + "</a>&nbsp;";
			}			
		}else{
			if(vecard != null && !"".equals(vecard)){//(vecard != null && !"".equals(vecard))&&(driverman != null && !"".equals(driverman))	
				boolean Driver = baseDao.isDBSetting("Vehicleapply", "needDriver");
				if (Driver) {
					if (driverman == null || "".equals(driverman)) {
						BaseUtil.showError("请选定司机后派车!");
					}
				}
				int id = baseDao.getSeqId("Vehiclereturn_SEQ");
				String code = baseDao.sGetMaxNumber("Vehiclereturn", 2);
				Employee employee = SystemSession.getUser();
				String starttime="";
				String endtime="";				
				for (int i = 0; i < maps.size(); i++) {
					sb.append(maps.get(i).get("va_id") + ",");
					if(starttime.equals("")){
						starttime=maps.get(i).get("va_time").toString();
						endtime=maps.get(i).get("va_endtime").toString();
					}else{
						if(DateUtil.compare(starttime, maps.get(i).get("va_time").toString())==1){
							starttime=maps.get(i).get("va_time").toString();
						}
						if(DateUtil.compare(endtime, maps.get(i).get("va_endtime").toString())==0){
							endtime=maps.get(i).get("va_endtime").toString();
						}
					}
				}
				checkVehicleTime(vecard.toString(),starttime,endtime);
				String range = sb.toString().substring(0, sb.toString().length() - 1);			
				String sqlstr="insert into Vehiclereturn (vr_id,vr_code,vr_vecard,vr_driver,vr_recorder,vr_recorddate,vr_starttime,vr_endtime,vr_status,vr_statuscode)values("+id+",'"+code+"','"+vecard+"','"+driverman+"','"+employee.getEm_name()+"',sysdate,to_date('"+starttime+"','yyyy-MM-dd HH24:MI:SS'),to_date('"+endtime+"','yyyy-MM-dd HH24:MI:SS'),'"+BaseUtil.getLocalMessage("AUDITED")+"','AUDITED')";
				baseDao.execute(sqlstr);
				baseDao.execute("update Vehiclereturn set vr_stdistance=(select va_km from Vehiclearchives where va_card='"+vecard+"') where vr_id="+id);
				String upsqlstr="update Vehicleapply set va_vrid="+id+",va_vrcode='"+code+"',va_turnstatus='已派车' where va_id in ("+range+")";
				baseDao.execute(upsqlstr);
				//消息模板配置
				Object mmid=baseDao.getFieldDataByCondition("MESSAGEMODEL left join MESSAGEROLE on mm_id=mr_mmid", "distinct mm_id", "MR_ISUSED=-1 AND MM_ISUSED=-1 and mm_caller='Vehiclereturn'");
					//调用生成消息的存储过程
				if (mmid != null) {
					baseDao.callProcedure("SP_CREATEINFO",new Object[] { mmid,"sys", id,DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) });
					}		
			    return "派车成功,派车单号:<a href=\"javascript:openUrl('jsps/oa/vehicle/vehiclereturn.jsp?formCondition=vr_idIS" + id
						+ "&gridCondition=va_vridIS"+id+"&whoami=Vehiclereturn')\">" + code + "</a>&nbsp;";
			}else{
				return "请指定车辆后派车！";			
			}	
		}
		
	}
	public void checkVehicleTime(String va_card,String va_time,String va_endtime){
		va_time=DateUtil.parseDateToOracleString(Constant.YMD_HMS,va_time);
		va_endtime=DateUtil.parseDateToOracleString(Constant.YMD_HMS,va_endtime);
		boolean isExist = baseDao.checkIf("Vehiclereturn", "vr_vecard='" +va_card+"' and nvl(vr_isback,0)=0 and ((vr_starttime<="+va_time+" and "+va_time+"<=vr_endtime) or (vr_starttime<="+va_endtime+" and "+va_endtime+"<=vr_endtime))");	
		if(isExist){
			BaseUtil.showError("派车时间冲突，请重新选择车辆");
		}		
	
	}
	
	//派车申请单打印
	@Override
	public String[] printVehicleapply(int va_id, String caller,
			String reportName, String condition) {
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, va_id);
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "va_id", va_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, va_id);
		return keys;
	}
	
	//派车单打印
	@Override
	public String[] printVehiclereturn(int va_id, String caller,
			String reportName, String condition) {
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, va_id);
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "vr_id", va_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, va_id);
		return keys;
	}

	@Override
	public void refreshSendTime(String caller, String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		if (store != null) {
			Object time = (store.get("vr_realsendtime") !=null && !"".equals(store.get("vr_realsendtime"))) ? "to_date('" + store.get("vr_realsendtime") + "','yyyy-mm-dd hh24:mi:ss')" : "sysdate" ;
			baseDao.updateByCondition("Vehiclereturn", "vr_realsendtime=" + time, "vr_id=" + store.get("vr_id"));
		}
	}
}
