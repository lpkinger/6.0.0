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
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.MouldProdHourService;

@Service
public class MouldProdHourServiceImpl implements MouldProdHourService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void resAuditMouldProdHour(int mph_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		handlerService.beforeResAudit(caller,new Object[] { mph_id});
		Object status = baseDao.getFieldDataByCondition("MouldProdHour", "mph_statuscode", "mph_id=" + mph_id);
		StateAssert.resAuditOnlyAudit(status);
		//反审核的关联业务数据判断
		baseDao.resAuditCheck("MouldProdHour", mph_id);
		// 执行反审核操作
		baseDao.resAudit("MouldProdHour", "mph_id=" + mph_id, "mph_status", "mph_statuscode", "MPH_AUDITER", "mph_auditdate");
		// 记录操作
		baseDao.logger.resAudit(caller, "mph_id", mph_id);
		handlerService.afterResAudit(caller,new Object[] { mph_id});
	}

	@Override
	public void auditMouldProdHour(int mph_id, String caller) {

		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("MouldProdHour", "mph_statuscode", "mph_id=" + mph_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { mph_id});
		// 执行审核操作
		baseDao.audit("MouldProdHour", "mph_id=" + mph_id, "mph_status", "mph_statuscode","mph_auditdate","MPH_AUDITER");
		// 记录操作
		baseDao.logger.audit(caller, "mph_id", mph_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller,new Object[] { mph_id});
	

	}

	@Override
	public void resSubmitMouldProdHour(int mph_id, String caller) {

		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MouldProdHour", "mph_statuscode", "mph_id=" + mph_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { mph_id });
		// 执行反提交操作
		baseDao.resOperate("MouldProdHour", "mph_id=" + mph_id, "mph_status", "mph_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "mph_id", mph_id);
		handlerService.afterResSubmit(caller,new Object[] { mph_id});

	}

	@Override
	public void submitMouldProdHour(int mph_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MouldProdHour", "mph_statuscode", "mph_id=" + mph_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { mph_id});
		// 执行提交操作
		baseDao.submit("MouldProdHour", "mph_id=" + mph_id, "mph_status", "mph_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "mph_id", mph_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { mph_id});
	
	}

	@Override
	public void printMouldProdHour(int id, String caller) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateMouldProdHourById(String formStore, String gridStore, String caller) {


		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的单据资料!
		Object status = baseDao.getFieldDataByCondition("MouldProdHour", "mph_statuscode", "mph_id=" + store.get("mph_id"));
		StateAssert.updateOnlyEntering(status);
		List<String> gridSql = SqlUtil.getInsertOrUpdateSql(gstore, "MouldProdHourdetail", "mphd_id");
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] {store,gstore});
		// 修改Detail 
		baseDao.execute(gridSql);
		// 保存BOM
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "MouldProdHour", "mph_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "mph_id", store.get("mph_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore});
	

	}

	@Override
	public void deleteMouldProdHour(int mph_id, String caller) {

		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("MouldProdHour", "mph_statuscode", "mph_id=" + mph_id);
		StateAssert.delOnlyEntering(status);
		// 是否已产生业务数据
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[] { mph_id});
		// 删除
		baseDao.deleteById("MouldProdHour", "mph_id", mph_id);
		// 删除Detail
		baseDao.deleteById("MouldProdHourdetail", "mphd_mphid", mph_id);
		// 记录操作
		baseDao.logger.delete(caller, "mph_id", mph_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[] { mph_id});
	

	}

	@Override
	public void saveMouldProdHour(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("MouldProdHour", "mph_code='" + store.get("mph_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		List<String> gridSql = SqlUtil.getInsertOrUpdateSql(grid, "MouldProdHourDETAIL", "mphd_id");
		baseDao.execute(gridSql);
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "MouldProdHour", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "mph_id", store.get("mph_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });

	}

}
