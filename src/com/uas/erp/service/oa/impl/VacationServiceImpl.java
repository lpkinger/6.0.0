package com.uas.erp.service.oa.impl;


import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.oa.VacationService;

@Service
public class VacationServiceImpl implements VacationService {
	static final String selectEmployee="select em_id,to_char(em_indate，'mm') as nowmonth,to_char(em_indate，'dd') as nowday from employee where em_indate is not null";
	static final String strsqla="update employee set em_days=0 where sysdate-em_indate<365 and em_indate is not null";
	static final String strsqlb="update employee set em_days=5*8 where sysdate-em_indate>365 and sysdate-em_indate<3650 and em_indate is not null";
	static final String strsqlc="update employee set em_days=10*8 where sysdate-em_indate>3650 and sysdate-em_indate<7300 and em_indate is not null";
	static final String strsqld="update employee set em_days=15*8 where sysdate-em_indate>7300 and em_indate is not null";
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;	

	@Override
	@Transactional
	public void saveVacation(String formStore, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[]{store});
		String code = (String) store.get("va_code");
		int count = baseDao.getCount("select count(*) from Vacation where va_code='"+code+"'");
		if(count > 0){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_sameCode"));
			return;
		}
		checkTime(store);
		String vaemcode = store.get("va_emcode")==null?store.get("VA_EMCODE").toString():store.get("va_emcode").toString();
		int num = checkDuplicateTime(vaemcode,store.get("va_startime").toString(),store.get("va_endtime").toString());
		if(num>0){
			BaseUtil.showError("请假时间段有重复！");
		}
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Vacation", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "va_id", store.get("va_id"));			
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller, new Object[]{store});

	}

	/*
	 * 判断请假时间段是否有重复
	 */
	@Override
	public int checkDuplicateTime(String emcode,String start,String end){
		return baseDao.getCountByCondition("vacation", "va_emcode='"+emcode+"' and "
				+"(  "
				+"( to_date('"+start+"','yyyy-mm-dd hh24:mi:ss')<=va_startime and (va_startime<to_date('"+end+"','yyyy-mm-dd hh24:mi:ss') and to_date('"+end+"','yyyy-mm-dd hh24:mi:ss')<=va_endtime)) or"
				+"( to_date('"+start+"','yyyy-mm-dd hh24:mi:ss')<=va_startime and  to_date('"+end+"','yyyy-mm-dd hh24:mi:ss')>=va_endtime) or"
				+"( to_date('"+start+"','yyyy-mm-dd hh24:mi:ss')>=va_startime and to_date('"+end+"','yyyy-mm-dd hh24:mi:ss')<va_endtime  )"
				+")" );
	}
	
	@Override
	public void updateVacationById(String formStore, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		//修改
		checkTime(store);
		int num = baseDao.getCountByCondition("vacation", "va_emcode='"+store.get("va_emcode")+"' and va_id<>"+store.get("va_id")+" and "
				+"(  "
				+"( to_date('"+store.get("va_startime").toString()+"','yyyy-mm-dd hh24:mi:ss')<=va_startime and (va_startime<to_date('"+store.get("va_endtime").toString()+"','yyyy-mm-dd hh24:mi:ss') and to_date('"+store.get("va_endtime").toString()+"','yyyy-mm-dd hh24:mi:ss')<=va_endtime)) or"
				+"( to_date('"+store.get("va_startime").toString()+"','yyyy-mm-dd hh24:mi:ss')<=va_startime and  to_date('"+store.get("va_endtime").toString()+"','yyyy-mm-dd hh24:mi:ss')>=va_endtime) or"
				+"( to_date('"+store.get("va_startime").toString()+"','yyyy-mm-dd hh24:mi:ss')>=va_startime and to_date('"+store.get("va_startime").toString()+"','yyyy-mm-dd hh24:mi:ss')<va_endtime  )"
				+")" );
		if(num>0){
			BaseUtil.showError("请假时间段有重复！");
		}
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Vacation", "va_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "va_id", store.get("va_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});

	}

	@Override
	public void deleteVacation(int va_id, String  caller) {

		handlerService.beforeDel(caller, new Object[]{va_id});
		//删除
		baseDao.deleteById("Vacation", "va_id", va_id);
		//记录操作
		baseDao.logger.delete(caller, "va_id", va_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{va_id});

	}

	@Override
	public void auditVacation(int va_id, String  caller) {

		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Vacation", "va_statuscode", "va_id=" + va_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{va_id});
		//执行审核操作
		baseDao.audit("Vacation", "va_id=" + va_id, "va_status", "va_statuscode", "va_auditdate", "va_auditman");
		//记录操作
		baseDao.logger.audit(caller, "va_id", va_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{va_id});

	}

	@Override
	public void resAuditVacation(int va_id, String  caller) {
		handlerService.handler("Vacation", "resAudit", "before", new Object[]{va_id});
		Object status = baseDao.getFieldDataByCondition("Vacation", "va_statuscode", "va_id=" + va_id);
	    StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("Vacation", "va_id=" + va_id, "va_status", "va_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "va_id", va_id);
		handlerService.afterResAudit(caller, new Object[]{va_id});
	}

	@Override
	public void submitVacation(int va_id, String  caller) {

		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Vacation", "va_statuscode", "va_id=" + va_id);
		StateAssert.submitOnlyEntering(status);
		Object vatype = baseDao.getFieldDataByCondition("Vacation", "va_vacationtype", "va_id=" + va_id);
		if(vatype.equals("年假")){
			/*String res="";
			res=checkHoliday(va_id,language,employee);
			if(!"".equals(res)){
				BaseUtil.showError(res);
			}*/
		}
		Object[] obj = baseDao.getFieldsDataByCondition("Vacation", new String[]{"va_startime","va_endtime","va_emcode"},"va_id=" + va_id);
		int num = baseDao.getCountByCondition("vacation", "va_emcode='"+obj[2]+"' and va_id<>"+va_id+" and "
				+"(  "
				+"( to_date('"+obj[0].toString()+"','yyyy-mm-dd hh24:mi:ss')<=va_startime and (va_startime<to_date('"+obj[1].toString()+"','yyyy-mm-dd hh24:mi:ss') and to_date('"+obj[1].toString()+"','yyyy-mm-dd hh24:mi:ss')<=va_endtime)) or"
				+"( to_date('"+obj[0].toString()+"','yyyy-mm-dd hh24:mi:ss')<=va_startime and  to_date('"+obj[1].toString()+"','yyyy-mm-dd hh24:mi:ss')>=va_endtime) or"
				+"( to_date('"+obj[0].toString()+"','yyyy-mm-dd hh24:mi:ss')>=va_startime and to_date('"+obj[0].toString()+"','yyyy-mm-dd hh24:mi:ss')<va_endtime  )"
				+")" );
		if(num>0){
			BaseUtil.showError("请假时间段有重复！");
		}
		//判断是否启用延期限制提交
		commitNeedCheck(caller,va_id);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{va_id});
		//执行提交操作
		baseDao.submit("Vacation", "va_id=" + va_id, "va_status", "va_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "va_id", va_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{va_id});
	}

	@Override
	public void resSubmitVacation(int va_id, String  caller) {

		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Vacation", "va_statuscode", "va_id=" + va_id);
		StateAssert.resSubmitOnlyCommited(status);
		Object vatype = baseDao.getFieldDataByCondition("Vacation", "va_vacationtype", "va_id=" + va_id);
		if(vatype.equals("年假")){/*
			Object valldays= baseDao.getFieldDataByCondition("Vacation", "va_alldays", "va_id=" + va_id);
			Object vaemcode= baseDao.getFieldDataByCondition("Vacation", "va_emcode", "va_id=" + va_id);
			Object emhavedays= baseDao.getFieldDataByCondition("Employee", "em_havedays", "em_code='" + vaemcode.toString()+"'");
			Object emdays= baseDao.getFieldDataByCondition("Employee", "em_days","em_code='" + vaemcode.toString()+"'");
			double hodays=Double.parseDouble(valldays.toString());
			double daysem=0;
			if(Integer.parseInt(emdays.toString())*8<Double.parseDouble(emhavedays.toString())+hodays){
				daysem=Integer.parseInt(emdays.toString())*8;
			}else{
				daysem=Double.parseDouble(emhavedays.toString())+hodays;
			}			
			baseDao.execute("update employee set em_havedays='"+daysem+"' where em_code='"+vaemcode.toString()+"'");
		 */}
		if(vatype.equals("病假")&&baseDao.isDBSetting("Ask4Leave", "vacation_sick_check")){
			baseDao.callProcedure("sp_uncommit_year_holiday",new Object[]{va_id});
		}
		handlerService.beforeResSubmit(caller,new Object[]{va_id});
		//执行反提交操作
		baseDao.resOperate("Vacation", "va_id=" + va_id, "va_status", "va_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "va_id", va_id);
		handlerService.afterResSubmit(caller,new Object[]{va_id});

	}

	@Override
	public void updateEmployeeHoliday(String  caller) {
		//更新所有员工的年假期间
		String qjdate=null;
		String qjdateday=null;
		String strsql=null;	
		try {			
			SqlRowList rs = baseDao.queryForRowSet(selectEmployee);						
			while(rs.next()){
				//当请假期间是2月份时，比较时间为到28日
				if(rs.getString("nowmonth").equals("02")&&rs.getString("nowday").equals("29")){
					qjdateday="28";
				}else{
					qjdateday=rs.getString("nowday");
				}							
				qjdate=DateUtil.getYear(DateUtil.getCurrentDate())+"-"+rs.getString("nowmonth")+"-"+qjdateday;						
				strsql="update employee set em_holidaydate=to_date('"+qjdate+"','yyyy-mm-dd') where em_id="+rs.getInt("em_id");
				baseDao.execute(strsql);
			}


		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("数据异常,转单失败");
		} 

	}
	/*检查年假天数*/
	@Override
	public String checkHoliday(int va_id, String  caller) {
		Object valldays= baseDao.getFieldDataByCondition("Vacation", "va_alldays", "va_id=" + va_id);
		Object vaemcode= baseDao.getFieldDataByCondition("Vacation", "va_emcode", "va_id=" + va_id);
		String res="";
		double hodays=Double.parseDouble(valldays.toString());
		try{
			SqlRowList rs = baseDao.queryForRowSet("select em_havedays,em_holidaydate,em_days from employee where em_code='"+vaemcode+"'");	
			while(rs.next()){
				int a=DateUtil.compare(rs.getObject("em_holidaydate").toString(),DateUtil.getCurrentDate());
				if(a==1){						
					if(Double.parseDouble(rs.getObject("em_havedays").toString())-hodays>=0){						
						baseDao.execute("update employee set em_havedays=em_havedays-"+hodays+" where em_code='"+vaemcode.toString()+"'");
					}else{						
						/*	BaseUtil.showError("年假日期不足，不能申请,剩余年假只有"+Double.parseDouble(rs.getObject("em_havedays").toString())+"天!");*/
						res="年假日期不足，不能申请,剩余年假只有"+Double.parseDouble(rs.getObject("em_havedays").toString())+"小时!";
					}	

				}else if(a==-1){					
					updateEmployeeHavedays(" em_code='"+vaemcode.toString()+"'");
					baseDao.execute("update employee set em_holidaydate=em_holidaydate+365,em_havedays=em_days-"+hodays+" where em_code='"+vaemcode.toString()+"'");					
				}
				//if(DateUtil.getCurrentDate())
			}
		}catch(Exception e){

		}
		return res;
	}
	/*计算员工年假天数*/
	@Override
	public void updateEmployeeHavedays(String condition){
		if(!condition.equals("")){
			String strsqlaa="update employee set em_days=0 where sysdate-em_indate<365 and em_indate is not null and "+condition+"";
			String strsqlbb="update employee set em_days=5*8 where sysdate-em_indate>365 and sysdate-em_indate<3650 and em_indate is not null and "+condition+"";
			String strsqlcc="update employee set em_days=10*8 where sysdate-em_indate>3650 and sysdate-em_indate<7300 and em_indate is not null and "+condition+"";
			String strsqldd="update employee set em_days=15*8 where sysdate-em_indate>7300 and em_indate is not null and "+condition+"";
			baseDao.execute(strsqlaa);
			baseDao.execute(strsqlbb);
			baseDao.execute(strsqlcc);
			baseDao.execute(strsqldd);
		}else{
			baseDao.execute(strsqla);
			baseDao.execute(strsqlb);
			baseDao.execute(strsqlc);
			baseDao.execute(strsqld);
		}
	}

	@Override
	public void auditAsk4Leave(int va_id,String  caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		//Object status = baseDao.getFieldDataByCondition("Vacation", "va_statuscode", "va_id=" + va_id);
		Object[] obj = baseDao.getFieldsDataByCondition("Vacation", new String[]{"va_statuscode","va_emcode"}, "va_id=" + va_id);
		StateAssert.auditOnlyCommited(obj[0]);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{va_id});
		//执行审核操作
		baseDao.audit("Vacation", "va_id=" + va_id, "va_status", "va_statuscode", "va_auditdate", "va_auditman");
		//记录操作
		baseDao.logger.audit(caller, "va_id", va_id);
		baseDao.callProcedure("COUNT_YEAR_REST", new Object[]{obj[1].toString()});
		//插入到流程转移
		//baseDao.getFieldDataByCondition(tableName, field, condition)
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{va_id});
	}

	@Override
	public void resAuditAsk4Leave(int va_id, String  caller) {
		Object status = baseDao.getFieldDataByCondition("Vacation", "va_statuscode", "va_id=" + va_id);
		Object[] obj = baseDao.getFieldsDataByCondition("Vacation", new String[]{"va_statuscode","va_emcode"}, "va_id=" + va_id);
		StateAssert.resAuditOnlyAudit(obj[0]);
		//执行反审核操作
		baseDao.resOperate("Vacation", "va_id=" + va_id, "va_status", "va_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "va_id", va_id);
		baseDao.callProcedure("COUNT_YEAR_REST", new Object[]{obj[1].toString()});
	}

	@Override
	public void confirmAsk4Leave(int id, String  caller) {
		Object status = baseDao.getFieldDataByCondition("Vacation", "va_statuscode", "va_id=" + id);
		StateAssert.confirmOnlyAudited(status);
		//执行反审核操作
		baseDao.updateByCondition("Vacation", "va_auditstatus='已处理'", "va_id=" + id);
		//记录操作
		baseDao.logger.getMessageLog(BaseUtil.getLocalMessage("msg.confirm"), BaseUtil.getLocalMessage("msg.confirmSuccess"), caller, "va_id", id);
	}
	
	// 提交前判断是否超过系统设置的可延期天数
	private void commitNeedCheck(String caller, Object id) {
		if (baseDao.isDBSetting("Ask4Leave", "commitNeedCheck")) {
			String days = baseDao.getDBSetting("Ask4Leave","SetDelayDays");
			if(days!=null && Integer.parseInt(days)>0){
				Object time = baseDao.getFieldDataByCondition("Vacation", "to_char(va_startime,'yyyy-mm-dd')", "va_id="+id);
				boolean bool=baseDao.checkIf("dual", "DAY_COUNT(to_date('"+time.toString()+"','yyyy-mm-dd'),sysdate)-1>"+days);
				if(bool) BaseUtil.showError("系统设置的延期提交天数为"+days+"天，已超过不允许提交！");}
		}
	}

	@Override
	public  Map<String, Object>  sickCheck(int va_id, String caller) {
		Map<String, Object> map = new HashMap<String, Object>();
		Object[] o = baseDao.getFieldsDataByCondition(
				"Vacation left join employee on va_emcode=em_code",
				new String[] { "va_code", "va_vacationtype", "va_startime",
						"va_endtime", "va_alldays", "va_date", "va_emcode",
						"em_indate" }, "va_id = '" + va_id + "'");
		if (o[1] != null) {
			if (o[1].toString().equals("病假")) {
				if (o[7] == null || o[7].equals("null")
						|| o[7].toString().equals("")) {
					// 没有入职时间 不能请年假
					BaseUtil.showError("员工没有入职时间  不能请病假");
				}
				List<String> list = baseDao.callProcedureWithOut(
						"SP_CANCOMMITVACATION", new Object[] { va_id },
						new Integer[] { 1 }, new Integer[] { 2, 3 });
				 Pattern pattern = Pattern.compile("[0-9]*"); 
				 Matcher isNum = pattern.matcher(list.get(0).toString());
				 if( !isNum.matches() ){
					 BaseUtil.showError(list.get(0).toString());
				 }else{
					 map.put("result", list.get(0).toString());
					 map.put("sickdays", list.get(1).toString());
				 } 
			}
		}	
		return map;
	}

	@Override
	public void cleanEmpdays(int id, String caller) {
		baseDao.execute("update employee set em_number1=0 where em_code=(select va_emcode from vacation where va_id="+id+")");
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),"提交病假清除年假操作", "操作成功", caller + "|va_id=" +id ));
	}
	
	@Override
	public void checkTime(Map<Object, Object> formStore){
		Object type=formStore.get("va_vacationtype");
		if("哺乳假".equals(type)){
			//后期完善
		}else{
			Timestamp starttime = Timestamp.valueOf(formStore.get("va_startime").toString());
			Timestamp endtime = Timestamp.valueOf(formStore.get("va_endtime").toString());
			if(starttime.after(endtime)){
				BaseUtil.showError("时间输入有误，请检查后重新输入");
			}
		}
	}

	@Override
	public void resEndVacation(int id, String caller) {
		//只能对状态为[已结案]的订单进行反结案操作!
		Object status = baseDao.getFieldDataByCondition("Vacation", "va_statuscode", "va_id=" + id);
		if(!status.equals("FINISH")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resEnd_onlyEnd",SystemSession.getLang()));
		}
		//执行反结案操作
		baseDao.updateByCondition("Vacation", "va_statuscode='AUDITED',va_status='" + 
				BaseUtil.getLocalMessage("AUDITED") + "'", "va_id=" + id);
		//记录操作
		baseDao.logger.resEnd(caller, "va_id", id);
	}

	@Override
	public void endVacation(int id, String caller) {
		//只能对状态为[已审核]的订单进行结案操作!
		Object status = baseDao.getFieldDataByCondition("Vacation", "va_statuscode", "va_id=" + id);
		if(!status.equals("AUDITED")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.end_onlyAudited",SystemSession.getLang()));
		}
		//执行结案前的其它逻辑
		handlerService.handler("Vacation", "end", "before", new Object[]{id,SystemSession.getLang()});
		//执行结案操作
		baseDao.updateByCondition("Vacation", "va_statuscode='FINISH',va_status='" + 
				BaseUtil.getLocalMessage("FINISH") + "'", "va_id=" + id);
		//记录操作
		baseDao.logger.end(caller, "va_id", id);
		//执行结案后的其它逻辑
		handlerService.handler("Vacation", "end", "after", new Object[]{id,SystemSession.getLang()});
	}
	
}
