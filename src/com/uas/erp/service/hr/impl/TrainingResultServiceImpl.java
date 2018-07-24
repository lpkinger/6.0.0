package com.uas.erp.service.hr.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.TrainingResultService;

@Service
public class TrainingResultServiceImpl implements TrainingResultService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveTrainingResult(String formStore, String gridStore, String caller) {	
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int count=baseDao.getCount("select count(1) from TrainingCourseResult where tr_emcode='"+store.get("tr_emcode")+"' and tr_tccode='"+store.get("tr_tccode")+"' and tr_tpid="+store.get("tr_tpid")+"");	
		if(count>0){
			BaseUtil.showError("此课程的培训反馈单已存在！");
		}
		handlerService.beforeSave(caller,  new Object[]{store});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "TrainingCourseResult",  new String[]{}, new Object[]{});
		baseDao.execute(formSql);		
		try{
			//记录操作
			baseDao.logger.save(caller, "tr_id", store.get("tr_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller,  new Object[]{store});
	}

	@Override
	public void updateTrainingResultById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int count=baseDao.getCount("select count(1) from TrainingCourseResult where tr_id<> "+store.get("tr_id")+" and tr_emcode='"+store.get("tr_emcode")+"' and tr_tccode='"+store.get("tr_tccode")+"' and tr_tpid="+store.get("tr_tpid")+"");
		if(count!=0){
			BaseUtil.showError("此课程的培训反馈单已存在！");
		}
		handlerService.beforeUpdate(caller, new Object[]{store});
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "TrainingCourseResult", "tr_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "tr_id", store.get("tr_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
	}

	@Override
	public void deleteTrainingResult(int id, String caller) {		
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{id});
		//删除purchase
		baseDao.deleteById("TrainingCourseResult", "tr_id", id);
		//记录操作
		baseDao.logger.delete(caller, "tr_id", id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{id});
	}

	@Override
	public void auditTrainingResult(int id, String caller) {		
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("TrainingCourseResult", "tr_statuscode", "tr_id=" + id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{id});
		//执行审核操作
		baseDao.audit("TrainingCourseResult", "tr_id=" + id, "tr_status", "tr_statuscode");
		baseDao.execute("insert into TRAININGCOURSEASSESS(TA_ID,TA_TPID,TA_EMCODE,TA_TCCODE,TA_GRADE,TA_SCORE,TA_SUGGESTION,TA_STATUS,TA_STATUSCODE,TA_DATE) select TRAININGCOURSEASSESS_SEQ.nextval,"
				+ "tr_tpid,tr_emcode,tr_tccode,tr_tagrade,tr_tascore,tr_suggestion,'已审核','AUDITED',sysdate from TrainingCourseResult where tr_id="+id+"");//插入到课程评估表
		Object[] ob=baseDao.getFieldsDataByCondition("trainingCourseResult left join trainingCourseInstance on tr_tpid=ti_tpid and tr_tccode=ti_tccode",new String[]{"ti_id","ti_detno","tr_tpid"}, "TR_ID="+id);
		baseDao.execute("update TrainingCourseInstance set ti_enddate=sysdate,ti_status='已完成' where ti_id="+ob[0]);//更新个人培训计划从表-课程实例
		Object detno = baseDao.getFieldDataByCondition("trainingCourseInstance", "min(ti_detno)", "ti_tpid=" +ob[2]+" and ti_detno>"+ob[1]);
		if(detno!=null){
			baseDao.execute("update TrainingCourseInstance set ti_startdate=sysdate,ti_status='进行中' where ti_tpid="+ob[2]+" and ti_detno="+detno);
		}
		baseDao.execute("update TrainingPlan set tp_tcname=(select ti_tcname from TrainingCourseInstance where ti_tpid="+ob[2]+" and ti_detno="+detno+") where tp_id="+ob[2]+"");
		baseDao.execute("update TrainingCourse set (tc_grade,tc_score)=(select round(avg(nvl(ta_grade,0)),2),round(avg(nvl(ta_score,0)),2) from TrainingCourseAssess where ta_status='已审核' and ta_tccode=tc_code and ta_tccode=(select tr_tccode from TrainingCourseResult where tr_id="+id+"))");
		//记录操作
		baseDao.logger.audit(caller, "tr_id", id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{id});

	}

	/*@Override
	public void resAuditTrainingResult(int tp_id, String caller) {
		
		Object status = baseDao.getFieldDataByCondition("TrainingPlan", "tp_statuscode", "tp_id=" + tp_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resAudit("TrainingPlan", "tp_id=" + tp_id, "tp_status", "tp_statuscode","tp_auditdate", "tp_auditer");
		//记录操作
		baseDao.logger.resAudit(caller, "tp_id", tp_id);

	}
*/
	@Override
	public void submitTrainingResult(int id, String caller) {
		
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("TrainingCourseResult", "tr_statuscode", "tr_id=" + id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{id});
		//执行提交操作
		baseDao.submit("TrainingCourseResult", "tr_id=" + id, "tr_status", "tr_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "tr_id", id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{id});
	}

	@Override
	public void resSubmitTrainingResult(int  id, String caller) {
		
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("TrainingCourseResult", "tr_statuscode", "tr_id=" +  id);
		StateAssert.resSubmitOnlyCommited(status);
		//执行反提交操作
		baseDao.resOperate("TrainingCourseResult", "tr_id=" +  id, "tr_status", "tr_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "tr_id",  id);
		handlerService.afterResSubmit(caller, new Object[] { id});

	}
}
