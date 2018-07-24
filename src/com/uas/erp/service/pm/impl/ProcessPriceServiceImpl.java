package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.ProcessPriceService;

@Service
public class ProcessPriceServiceImpl implements ProcessPriceService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void saveProcessPrice(String formStore, String gridStore, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ProcessPrice", "pp_code='" + store.get("pp_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ProcessPrice", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "pp_id", store.get("pp_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	

	}

	@Override
	public void updateProcessPriceById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的单据资料!
		Object status = baseDao.getFieldDataByCondition("ProcessPrice", "pp_statuscode", "pp_id=" + store.get("pp_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] {store,gstore});
		// 保存ProcessPrice
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProcessPrice", "pp_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "pp_id", store.get("pp_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore});

	}

	@Override
	public void deleteProcessPrice(int pp_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("ProcessPrice", "pp_statuscode", "pp_id=" + pp_id);
		StateAssert.delOnlyEntering(status);
		// 是否已产生业务数据
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[] { pp_id});
		// 删除
		baseDao.deleteById("ProcessPrice", "pp_id", pp_id);
		// 记录操作
		baseDao.logger.delete(caller, "pp_id", pp_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[] { pp_id});

	}

	@Override
	public void printProcessPrice(int pp_id, String caller) {
		// TODO Auto-generated method stub

	}

	@Override
	public void auditProcessPrice(int pp_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ProcessPrice", "pp_statuscode", "pp_id=" + pp_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { pp_id});
		// 执行审核操作
		baseDao.audit("ProcessPrice", "pp_id=" + pp_id, "pp_status", "pp_statuscode","pp_auditdate","pp_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "pp_id", pp_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller,new Object[] { pp_id});

	}

	@Override
	public void resAuditProcessPrice(int pp_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		handlerService.beforeResAudit(caller,new Object[] { pp_id});
		Object status = baseDao.getFieldDataByCondition("ProcessPrice", "pp_statuscode", "pp_id=" + pp_id);
		StateAssert.resAuditOnlyAudit(status);
		//反审核的关联业务数据判断
		baseDao.resAuditCheck("ProcessPrice", pp_id);
		// 执行反审核操作
		baseDao.resAudit("ProcessPrice", "pp_id=" + pp_id, "pp_status", "pp_statuscode", "pp_auditman", "pp_auditdate");
		// 记录操作
		baseDao.logger.resAudit(caller, "pp_id", pp_id);
		handlerService.afterResAudit(caller,new Object[] { pp_id});

	}

	@Override
	public void submitProcessPrice(int pp_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ProcessPrice", "pp_statuscode", "pp_id=" + pp_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { pp_id});
		// 执行提交操作
		baseDao.submit("ProcessPrice", "pp_id=" + pp_id, "pp_status", "pp_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pp_id", pp_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { pp_id});

	}

	@Override
	public void resSubmitProcessPrice(int pp_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ProcessPrice", "pp_statuscode", "pp_id=" + pp_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { pp_id });
		// 执行反提交操作
		baseDao.resOperate("ProcessPrice", "pp_id=" + pp_id, "pp_status", "pp_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pp_id", pp_id);
		handlerService.afterResSubmit(caller,new Object[] { pp_id});

	}

}
