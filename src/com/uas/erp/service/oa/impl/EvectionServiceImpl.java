package com.uas.erp.service.oa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.EvectionService;

@Service
public class EvectionServiceImpl implements EvectionService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveEvection(String formStore, String caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);	
		handlerService.beforeSave(caller, new Object[]{store});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Evection", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "ec_id", store.get("ec_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller, new Object[]{store});

	}

	@Override
	public void updateEvectionById(String formStore, String caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Evection", "ec_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "ec_id", store.get("ec_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[]{store});

	}

	@Override
	public void deleteEvection(int ec_id, String  caller) {
		
		handlerService.beforeDel(caller,  new Object[]{ec_id});
		//删除
		baseDao.deleteById("Evection", "ec_id", ec_id);
		//记录操作
		baseDao.logger.delete(caller, "ec_id", ec_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller,  new Object[]{ec_id});

	}

	@Override
	public void auditEvection(int ec_id, String  caller) {
		
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Evection", "ec_statuscode", "ec_id=" + ec_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{ec_id});
		//执行审核操作
		baseDao.audit("Evection", "ec_id=" + ec_id, "ec_status", "ec_statuscode", "ec_auditdate", "ec_auditor");;
		//记录操作
		baseDao.logger.audit(caller, "ec_id", ec_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{ec_id});

	}

	@Override
	public void resAuditEvection(int ec_id, String  caller) {
		
		Object status = baseDao.getFieldDataByCondition("Evection", "ec_statuscode", "ec_id=" + ec_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("Evection", "ec_id=" + ec_id, "ec_status", "ec_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "ec_id", ec_id);
		
	}

	@Override
	public void submitEvection(int ec_id, String  caller) {
		
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Evection", "ec_statuscode", "ec_id=" + ec_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{ec_id});
		//执行提交操作
		baseDao.submit("Evection", "ec_id=" + ec_id, "ec_status", "ec_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "ec_id", ec_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{ec_id});
	}

	@Override
	public void resSubmitEvection(int ec_id, String  caller) {
		
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Evection", "ec_statuscode", "ec_id=" + ec_id);
		StateAssert.resSubmitOnlyCommited(status);
		//执行反提交操作
		baseDao.resOperate("Evection", "ec_id=" + ec_id, "ec_status", "ec_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "ec_id", ec_id);

	}

}
