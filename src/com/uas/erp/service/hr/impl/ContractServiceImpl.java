package com.uas.erp.service.hr.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.ContractService;

@Service
public class ContractServiceImpl implements ContractService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveContract(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);		
		handlerService.beforeSave(caller,new Object[]{store});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Contract", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save(caller, "co_id", store.get("co_id"));
		handlerService.afterSave(caller,new Object[]{store});

	}

	@Override
	public void updateContractById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Contract", "co_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "co_id", store.get("co_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
	}

	@Override
	public void deleteContract(int co_id, String  caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[]{co_id});
		//删除
		baseDao.deleteById("Contract", "co_id", co_id);
		//记录操作
		baseDao.logger.delete(caller, "co_id", co_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[]{co_id});
	}

	@Override
	public void auditContract(int co_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Contract", "co_statuscode", "co_id=" + co_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, co_id);
		//执行审核操作
		baseDao.audit("Contract", "co_id=" + co_id, "co_status", "co_statuscode");
		baseDao.execute("update employee set (em_startdate,em_cancellingdate)=(select co_begintime,co_endtime from contract where CO_CONTRACTORCODE=em_code and co_id="+co_id+")"
				+ "where em_code in (select CO_CONTRACTORCODE from contract where co_id="+co_id + " and nvl(CO_CONTRACTORCODE,' ')<>' ')");
		//记录操作
		baseDao.logger.audit(caller, "co_id", co_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, co_id);
	}

	@Override
	public void resAuditContract(int co_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Contract", "co_statuscode", "co_id=" + co_id);
		StateAssert.resAuditOnlyAudit(status);
		baseDao.resAuditCheck("Contract", co_id);
		//执行反审核操作
		baseDao.resOperate("Contract", "co_id=" + co_id, "co_status", "co_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "co_id", co_id);
	}

	@Override
	public void submitContract(int co_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Contract", "co_statuscode", "co_id=" + co_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, co_id);
		//执行提交操作
		baseDao.submit("Contract", "co_id=" + co_id, "co_status", "co_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "co_id", co_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, co_id);
	}

	@Override
	public void resSubmitContract(int co_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Contract", "co_statuscode", "co_id=" + co_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeSubmit(caller, co_id);
		//执行反提交操作
		baseDao.resOperate("Contract", "co_id=" + co_id, "co_status", "co_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "co_id", co_id);
		handlerService.afterResSubmit(caller, co_id);
	}
}
