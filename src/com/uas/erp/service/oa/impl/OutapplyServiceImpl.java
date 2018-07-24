package com.uas.erp.service.oa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.OutapplyService;

@Service
public class OutapplyServiceImpl implements OutapplyService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveOutapply(String formStore, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		
		handlerService.beforeSave(caller, new Object[]{store});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Outapply", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "oa_id", store.get("oa_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void updateOutapplyById(String formStore, String caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Outapply", "oa_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "oa_id", store.get("oa_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});

	}

	@Override
	public void deleteOutapply(int oa_id, String  caller) {
		
		handlerService.beforeDel(caller, new Object[]{oa_id});
		//删除
		baseDao.deleteById("Outapply", "oa_id", oa_id);
		//记录操作
		baseDao.logger.delete(caller, "oa_id", oa_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{oa_id});

	}

	@Override
	public void auditOutapply(int oa_id, String  caller) {
		
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Outapply", "oa_statuscode", "oa_id=" + oa_id);
	    StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{oa_id});
		//执行审核操作
		baseDao.audit("Outapply", "oa_id=" + oa_id, "oa_status", "oa_statuscode");
		//记录操作
		baseDao.logger.audit(caller, "oa_id", oa_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{oa_id});

	}

	@Override
	public void resAuditOutapply(int oa_id, String  caller) {
		
		Object status = baseDao.getFieldDataByCondition("Outapply", "oa_statuscode", "oa_id=" + oa_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("Outapply", "oa_id=" + oa_id, "oa_status", "oa_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "oa_id", oa_id);

	}

	@Override
	public void submitOutapply(int oa_id, String  caller) {
		
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Outapply", "oa_statuscode", "oa_id=" + oa_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{oa_id});
		//执行提交操作
		baseDao.submit("Outapply", "oa_id=" + oa_id, "oa_status", "oa_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "oa_id", oa_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{oa_id});

	}

	@Override
	public void resSubmitOutapply(int oa_id, String  caller) {
		
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Outapply", "oa_statuscode", "oa_id=" + oa_id);
		StateAssert.resSubmitOnlyCommited(status);
		//执行反提交操作
		baseDao.resOperate("Outapply", "oa_id=" + oa_id, "oa_status", "oa_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "oa_id", oa_id);

	}

}
