package com.uas.erp.service.oa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.AttendapplyService;

@Service
public class AttendapplyServiceImpl implements AttendapplyService {

	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void saveAttendapply(String formStore, String caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);		
		handlerService.beforeSave(caller,new Object[]{store});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Attendapply", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "aa_id", store.get("aa_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller,new Object[]{store});

	}

	@Override
	public void updateAttendapplyById(String formStore, String caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Attendapply", "aa_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "aa_id", store.get("aa_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[]{store});

	}

	@Override
	public void deleteAttendapply(int aa_id, String caller) {
		
		handlerService.beforeDel(caller,new Object[]{aa_id});
		//删除
		baseDao.deleteById("Attendapply", "aa_id", aa_id);
		//记录操作
		baseDao.logger.delete(caller, "aa_id", aa_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[]{aa_id});

	}

	@Override
	public void auditAttendapply(int aa_id, String caller) {
		
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Attendapply", "aa_statuscode", "aa_id=" + aa_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{aa_id});
		//执行审核操作
		baseDao.audit("Attendapply", "aa_id=" + aa_id, "aa_status", "aa_statuscode", "aa_auditdate", "aa_auditor");
		//记录操作
		baseDao.logger.audit(caller, "aa_id", aa_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{aa_id});

	}

	@Override
	public void resAuditAttendapply(int aa_id, String caller) {
		
		Object status = baseDao.getFieldDataByCondition("Attendapply", "aa_statuscode", "aa_id=" + aa_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("Attendapply", "aa_id=" + aa_id, "aa_status", "aa_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "aa_id", aa_id);

	}

	@Override
	public void submitAttendapply(int aa_id, String caller) {
		
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Attendapply", "aa_statuscode", "aa_id=" + aa_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{aa_id});
		//执行提交操作
		baseDao.submit("Attendapply", "aa_id=" + aa_id, "aa_status", "aa_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "aa_id", aa_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{aa_id});
	}

	@Override
	public void resSubmitAttendapply(int aa_id, String caller) {
		
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Attendapply", "aa_statuscode", "aa_id=" + aa_id);
		StateAssert.resSubmitOnlyCommited(status);
		//执行反提交操作
		baseDao.resOperate("Attendapply", "aa_id=" + aa_id, "aa_status", "aa_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "aa_id", aa_id);

	}

}
