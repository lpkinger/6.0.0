package com.uas.erp.service.hr.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.TrainassessService;

@Service
public class TrainassessServiceImpl implements TrainassessService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveTrainassess(String formStore, String caller) {	
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller,new Object[]{store});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "TrainingCourseassess", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "ta_id", store.get("ta_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller,new Object[]{store});
	}

	@Override
	public void updateTrainassessById(String formStore, String caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "TrainingCourseassess", "ta_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "ta_id", store.get("ta_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[]{store});
	}

	@Override
	public void deleteTrainassess(int ta_id, String caller) {
		handlerService.beforeDel(caller, new Object[]{ta_id});
		//删除
		baseDao.deleteById("TrainingCourseassess", "ta_id", ta_id);
		//记录操作
		baseDao.logger.delete(caller, "ta_id", ta_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{ta_id});
	}

	@Override
	public void auditTrainassess(int id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("TrainingCourseAssess", "ta_statuscode", "ta_id=" + id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{id});
		//执行审核操作
		baseDao.audit("TrainingCourseAssess", "ta_id=" + id, "ta_status", "ta_statuscode");
		baseDao.execute("update TrainingCourse set (tc_grade,tc_score)=(select round(avg(nvl(ta_grade,0)),2),round(avg(nvl(ta_score,0)),2) from TrainingCourseAssess where ta_status='已审核' and ta_tccode=tc_code and ta_tccode=(select ta_tccode from TrainingCourseAssess where ta_id="+id+"))");
		//记录操作
		baseDao.logger.audit(caller, "ta_id", id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{id});
		
	}

	@Override
	public void submitTrainassess(int id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("TrainingCourseAssess", "ta_statuscode", "ta_id=" + id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{id});
		//执行提交操作
		baseDao.submit("TrainingCourseAssess", "ta_id=" + id, "ta_status", "ta_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "ta_id", id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{id});
	}

	@Override
	public void resSubmitTrainassess(int id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("TrainingCourseAssess", "ta_statuscode", "ta_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		//执行反提交操作
		baseDao.resOperate("TrainingCourseAssess", "ta_id=" + id, "ta_status", "ta_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "ta_id", id);
		handlerService.afterResSubmit(caller, new Object[] { id});
	}
}
