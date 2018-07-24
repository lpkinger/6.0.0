package com.uas.erp.service.hr.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.SpeAttendanceService;
@Service
public class SpeAttendanceServiceImpl implements SpeAttendanceService {

	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;	
	@Override
	public void saveSpeAttendance(String formStore, String caller) {
        Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
        handlerService.beforeSave(caller, new Object[]{store});
		String code = (String) store.get("sa_code");
		int count = baseDao.getCount("select sa_code from SpeAttendance where sa_code='"+code+"'");
		if(count > 0){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_sameCode"));
			return;
		}
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "SpeAttendance", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//updateEmployeeHoliday(language,employee);
		try{
			//记录操作
			baseDao.logger.save(caller, "sa_id", store.get("sa_id"));			
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void updateSpeAttendance(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "SpeAttendance", "sa_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "sa_id", store.get("sa_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
	}

	@Override
	public void deleteSpeAttendance(int id, String caller) {
		handlerService.beforeDel(caller,new Object[]{id});
		//删除
		baseDao.deleteById("SpeAttendance", "sa_id", id);
		//记录操作
		baseDao.logger.delete(caller, "sa_id", id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[]{id});
	}

	@Override
	public void auditSpeAttendance(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("SpeAttendance", "sa_statuscode", "sa_id=" + id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{id});
		//执行审核操作
		baseDao.audit("SpeAttendance", "sa_id=" + id, "sa_status", "sa_statuscode", "sa_auditdate", "sa_auditman");
		//记录操作
		baseDao.logger.audit(caller, "sa_id", id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{id});
	}

	@Override
	public void resAuditSpeAttendance(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("SpeAttendance", "sa_statuscode", "sa_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("SpeAttendance", "sa_id=" + id, "sa_status", "sa_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "sa_id", id);
	}

	@Override
	public void submitSpeAttendance(int id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("SpeAttendance", "sa_statuscode", "sa_id=" + id);
		StateAssert.submitOnlyEntering(status);		
		//判断是否启用延期限制提交
		commitNeedCheck(caller,id);
		//限制如果类型是忘记打卡，则本月只能提交两次
		limitSignCard(id);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller,new Object[]{id});
		//执行提交操作
		baseDao.submit("SpeAttendance", "sa_id=" + id, "sa_status", "sa_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "sa_id", id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller,new Object[]{id});
	}

	@Override
	public void resSubmitSpeAttendance(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("SpeAttendance", "sa_statuscode", "sa_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[]{id});
		//执行反提交操作
		baseDao.resOperate("SpeAttendance", "sa_id=" + id, "sa_status", "sa_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "sa_id", id);
		handlerService.afterResSubmit(caller, new Object[]{id});
	}

	@Override
	public void confirmSpeAttendance(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("SpeAttendance", "sa_statuscode", "sa_id=" + id);
		if(!status.equals("AUDITED")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.confirm_onlyAudit"));
		}
		//执行反审核操作
		baseDao.updateByCondition("SpeAttendance", "sa_auditstatus='已处理'", "sa_id=" + id);
		//记录操作
		baseDao.logger.getMessageLog(BaseUtil.getLocalMessage("msg.confirm"), BaseUtil.getLocalMessage("msg.confirmSuccess"), caller, "sa_id", id);
	}

	
	// 提交前判断是否超过系统设置的可延期天数
	private void commitNeedCheck(String caller, Object id) {
		if (baseDao.isDBSetting("SpeAttendance", "commitNeedCheck")) {
			String days = baseDao.getDBSetting("SpeAttendance","SetDelayDays");
			if(days!=null && Integer.parseInt(days)>0){
				Object time = baseDao.getFieldDataByCondition("SpeAttendance", "to_char(SA_APPDATE,'yyyy-mm-dd')", "sa_id="+id);
				boolean bool=baseDao.checkIf("dual", "DAY_COUNT(to_date('"+time.toString()+"','yyyy-mm-dd'),sysdate)>"+days);
			if(bool) BaseUtil.showError("系统设置的延期提交天数为"+days+"天，已超过不允许提交！");
			}
	}
	}
	
	//判断员工忘记打卡次数
	private void limitSignCard(int id){
		Object[] codeAndReason = baseDao.getFieldsDataByCondition("speattendance", new String[]{"sa_appmancode","sa_reason","sa_appdate"}, "sa_id="+id);
				if(codeAndReason!=null && codeAndReason[0]!=null && codeAndReason[1]!=null && codeAndReason[1].equals("忘记打卡")&& codeAndReason[2]!=null){
					if (baseDao.isDBSetting("SpeAttendance", "commitNeedCheckSignCard")){
						Object count = baseDao.getFieldDataByCondition("speattendance", "count(1)", "sa_appmancode='"+codeAndReason[0].toString()+"' and sa_statuscode in ('AUDITED','COMMITED') and sa_reason='忘记打卡' and trunc(sa_appdate,'mm')=trunc(to_date('"+codeAndReason[2]+"','yyyy-mm-dd hh24:mi:ss'),'mm')");
						String num = baseDao.getDBSetting("SpeAttendance","SetForgetSignCardNum");
						if(num!=null&&Integer.parseInt(num)>0){
							if(Integer.parseInt(count.toString())+1>Integer.parseInt(num)){
								BaseUtil.showError("本月忘记打卡类型特殊考勤单据只能提交"+Integer.parseInt(num)+"次");
							}	
						}
					}
				}

	}

	@Override
	public void endSpeAttendance(int  id, String caller) {
		// 执行禁用前的其它逻辑
		handlerService.handler(caller, "end", "before", new Object[] {  id });
		// 执行禁用操作
		baseDao.updateByCondition("SpeAttendance", "sa_statuscode='FINISH',sa_status='" + BaseUtil.getLocalMessage("FINISH") + "'", 
				"sa_id="+  id);
		// 记录操作
		baseDao.logger.others("msg.end", "msg.endSuccess", caller, "sa_id",  id);
		// 执行禁用后的其它逻辑
		handlerService.handler(caller, "end", "after", new Object[] { id });
	}

	@Override
	public void resEndSpeAttendance(int id, String caller) {
		// 只能对状态为[已结案]的订单进行反结案操作!
		Object status = baseDao.getFieldDataByCondition("SpeAttendance", "sa_statuscode", "sa_id=" + id);
		if (!status.equals("FINISH")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resEnd_onlyEnd"));
		}
		// 反结案
		baseDao.updateByCondition("SpeAttendance", "sa_statuscode='AUDITED',sa_status='" + BaseUtil.getLocalMessage("AUDITED") + "'", 
				"sa_id="+  id);
		// 记录操作
		baseDao.logger.others("msg.resEnd", "msg.resEndSuccess", caller, "sa_id", id);
	}
}
