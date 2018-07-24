package com.uas.erp.service.hr.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.KpiAllocationService;
@Service
public class KpiAllocationImpl implements KpiAllocationService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveKpiAllocation(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller,new Object[] {store,grid});
		// 保存KpiAllocation
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"KpiAllocation", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存Contact
		for (Map<Object, Object> s : grid) {
			s.put("kad_id", baseDao.getSeqId("KpiAllocationdet_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"KpiAllocationdet");
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "ka_id", store.get("ka_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller,new Object[] {store,grid});
	}

	@Override
	public void deleteKpiAllocation(int ka_id, String caller) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("KpiAllocation",
				"ka_statuscode", "ka_id=" + ka_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[] { ka_id});
		// 删除KpiAllocation
		baseDao.deleteById("KpiAllocation", "ka_id", ka_id);
		// 删除Contact
		baseDao.deleteById("KpiAllocationdet", "kad_kaid", ka_id);
		// 记录操作
		baseDao.logger.delete(caller, "ka_id", ka_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[] { ka_id});
	}

	@Override
	public void updateKpiAllocationById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("KpiAllocation",
				"ka_statuscode", "ka_id=" + store.get("ka_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] {store,gstore});
		// 修改KBIAssess
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"KpiAllocation", "ka_id");
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"KpiAllocationdet", "kad_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("kad_id") == null || s.get("kad_id").equals("")
					|| s.get("kad_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("KpiAllocationdet_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "KpiAllocationdet",
						new String[] { "kad_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.delete(caller, "ka_id", store.get("ka_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] {store,gstore});
	}

	@Override
	public void submitKpiAllocation(int ka_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("KpiAllocation",
				"ka_statuscode", "ka_id=" + ka_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { ka_id});
		// 执行提交操作
		baseDao.submit("KpiAllocation", "ka_id=" + ka_id, "ka_status", "ka_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "ka_id", ka_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { ka_id});
	}

	@Override
	public void resSubmitKpiAllocation(int ka_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("KpiAllocation",
				"ka_statuscode", "ka_id=" + ka_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller,new Object[] { ka_id});
		// 执行反提交操作
		baseDao.resOperate("KpiAllocation", "ka_id=" + ka_id, "ka_status", "ka_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "ka_id", ka_id);
		handlerService.afterResSubmit(caller,new Object[] { ka_id});
	}

	@Override
	public void auditKpiAllocation(int ka_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("KpiAllocation",
				"ka_statuscode", "ka_id=" + ka_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] {ka_id});
		// 执行审核操作
		baseDao.audit("KpiAllocation", "ka_id=" + ka_id, "ka_status", "ka_statuscode");
		// 记录操作
		baseDao.logger.audit(caller, "ka_id", ka_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] {ka_id});
	}

	@Override
	public void resAuditKpiAllocation(int ka_id, String caller) {
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller,new Object[] {ka_id});
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("KpiAllocation",
				"ka_statuscode", "ka_id=" + ka_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("KpiAllocation", "ka_id=" + ka_id, "ka_status", "ka_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "ka_id", ka_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller,new Object[] {ka_id});
	}

}
