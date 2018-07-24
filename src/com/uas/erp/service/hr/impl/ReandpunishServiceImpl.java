package com.uas.erp.service.hr.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.ReandpunishService;

@Service
public class ReandpunishServiceImpl implements ReandpunishService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveReandpunish(String formStore, String caller) {	
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);		
		handlerService.beforeSave(caller,new Object[]{store});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Reandpunish", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "rp_id", store.get("rp_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller,new Object[]{store});
	}

	@Override
	public void updateReandpunishById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Reandpunish", "rp_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "rp_id", store.get("rp_id"));;
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
	}

	@Override
	public void deleteReandpunish(int rp_id, String  caller) {
		handlerService.beforeDel(caller, new Object[]{rp_id});
		//删除
		baseDao.deleteById("Reandpunish", "rp_id", rp_id);
		//记录操作
		baseDao.logger.delete(caller, "rp_id", rp_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{rp_id});
	}

	@Override
	public void auditReandpunish(int rp_id, String  caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("Reandpunish", new String[]{"rp_statuscode", "rp_gettercode"}, "rp_id=" + rp_id);
		StateAssert.auditOnlyCommited(status[0]);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller,new Object[]{rp_id});
		//执行审核操作
		Object emid = baseDao.getFieldDataByCondition("employee", "em_id", "em_code='" + status[1] + "'");
		baseDao.audit("Reandpunish", "rp_id=" + rp_id, "rp_status", "rp_statuscode", "rp_auditdate", "rp_auditman");
		int maxdetno = baseDao.getCount("select count(*) from archivereandpunish where AR_ARID=" + emid);
		baseDao.execute("insert into archivereandpunish (AR_ID,AR_DETNO,AR_ARID,AR_TITLE,AR_CLASS,AR_CONTENT,AR_TIME,AR_RECORDTIME,ar_sourceid) "
				+ "select ARCHIVEREANDPUNISH_SEQ.NEXTVAL," + (maxdetno+1) +"," + emid + ",rp_title,rp_class,rp_content,rp_date,sysdate,rp_id"
				+ " from Reandpunish where rp_id=" + rp_id);
		//记录操作
		baseDao.logger.audit(caller, "rp_id", rp_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller,new Object[]{rp_id});
	}

	@Override
	public void resAuditReandpunish(int rp_id, String  caller) {
		Object status = baseDao.getFieldDataByCondition("Reandpunish", "rp_statuscode", "rp_id=" + rp_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resAudit("Reandpunish", "rp_id=" + rp_id, "rp_status", "rp_statuscode", "rp_auditdate", "rp_auditman");
		baseDao.execute("delete from archivereandpunish where ar_sourceid=" + rp_id);
		//记录操作
		baseDao.logger.resAudit(caller, "rp_id", rp_id);
	}

	@Override
	public void submitReandpunish(int rp_id, String  caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Reandpunish", "rp_statuscode", "rp_id=" + rp_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{rp_id});
		//执行提交操作
		baseDao.submit("Reandpunish", "rp_id=" + rp_id, "rp_status", "rp_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "rp_id", rp_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{rp_id});
	}

	@Override
	public void resSubmitReandpunish(int rp_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Reandpunish", "rp_statuscode", "rp_id=" + rp_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, rp_id);
		//执行反提交操作
		baseDao.resOperate("Reandpunish", "rp_id=" + rp_id, "rp_status", "rp_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "ip_id", rp_id);
		handlerService.afterResSubmit(caller, rp_id);
	}
}
