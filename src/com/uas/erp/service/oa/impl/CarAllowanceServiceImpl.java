package com.uas.erp.service.oa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.oa.CarAllowanceService;

@Service
public class CarAllowanceServiceImpl implements CarAllowanceService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void auditCarAllowance(int id, String  caller) {
		handlerService.beforeAudit(caller,new Object[]{id});
		//执行审核操作
		baseDao.audit("CarAllowance", "ca_id=" + id, "ca_status", "ca_statuscode", "ca_auditdate", "ca_auditman");
		//记录操作
		baseDao.logger.audit(caller, "ca_id", id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller,new Object[]{id});
	}

	@Override
	public void saveCarAllowance(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "CarAllowance", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save(caller, "ca_id", store.get("ca_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void updateCarAllowance(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,new Object[]{store});
		//修改AskLeave
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "CarAllowance", "ca_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "ca_id", store.get("ca_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[]{store});
	}

	@Override
	public void deleteCarAllowance(int id, String  caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{id});
		//删除
		baseDao.deleteById("CarAllowance", "ca_id", id);
		//记录操作
		baseDao.logger.delete(caller, "ca_id", id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{id});

	}

	@Override
	public void resAuditCarAllowance(int id, String  caller) {
		baseDao.resOperate("CarAllowance", "ca_id=" + id, "ca_status", "ca_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "ca_id", id);
	}

	@Override
	public void submitCarAllowance(int id, String  caller) {
		handlerService.beforeSubmit(caller, new Object[]{id});
		//执行提交操作
		baseDao.submit("CarAllowance", "ca_id=" + id, "ca_status", "ca_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "ca_id", id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{id});
	}

	@Override
	public void resSubmitCarAllowance(int id, String  caller) {
		handlerService.beforeResSubmit(caller, new Object[] { id});
		// 执行反提交操作
		baseDao.resOperate("CarAllowance", "ca_id=" + id, "ca_status", "ca_statuscode");	
		// 记录操作
		baseDao.logger.resSubmit(caller, "ca_id", id);
		handlerService.afterResSubmit(caller, new Object[] { id});
	}

	@Override
	public void confirmCarAllowance(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("CarAllowance",
				"ca_statuscode", "ca_id=" + id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.confirm_onlyAudit"));
		}
		Employee employee = SystemSession.getUser();
		// 执行反审核操作
		baseDao.updateByCondition("CarAllowance", "ca_auditstatus='已处理'", "ca_id="
				+ id);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
				.getLocalMessage("msg.confirm"), BaseUtil
				.getLocalMessage("msg.confirmSuccess"),
				"CarAllowance|ca_id=" + id));
	}

}
