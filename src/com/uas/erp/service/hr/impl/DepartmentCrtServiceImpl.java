package com.uas.erp.service.hr.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.hr.DepartmentCrtService;

@Service
public class DepartmentCrtServiceImpl implements DepartmentCrtService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveDepartmentCrt(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存DepartmentCrt
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "DepartmentCrt",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存Contact
		for (Map<Object, Object> s : grid) {
			s.put("dcd_id", baseDao.getSeqId("DepartmentCrtdetail_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"DepartmentCrtdetail");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.save(caller, "dc_id", store.get("dc_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });

	}

	@Override
	public void deleteDepartmentCrt(int dc_id, String caller) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("DepartmentCrt",
				"dc_statuscode", "dc_id=" + dc_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { dc_id });
		// 删除DepartmentCrt
		baseDao.deleteById("DepartmentCrt", "dc_id", dc_id);
		// 删除Contact
		baseDao.deleteById("DepartmentCrtdetail", "dcd_dcid", dc_id);
		// 记录操作
		baseDao.logger.delete(caller, "dc_id", dc_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, dc_id);
	}

	@Override
	public void updateDepartmentCrt(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("DepartmentCrt",
				"dc_statuscode", "dc_id=" + store.get("dc_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.update_onlyEntering"));
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改DepartmentCrt
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "DepartmentCrt",
				"dc_id");
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"DepartmentCrtdetail", "dcd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("dcd_id") == null || s.get("dcd_id").equals("")
					|| s.get("dcd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("DepartmentCrtdetail_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "DepartmentCrtdetail",
						new String[] { "dcd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "dc_id", store.get("dc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });

	}

	@Override
	public void submitDepartmentCrt(int dc_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("DepartmentCrt",
				"dc_statuscode", "dc_id=" + dc_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.submit_onlyEntering"));
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, dc_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"DepartmentCrt",
				"dc_statuscode='COMMITED',dc_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'",
				"dc_id=" + dc_id);
		// 记录操作
		baseDao.logger.submit(caller, "dc_id", dc_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, dc_id);

	}

	@Override
	public void resSubmitDepartmentCrt(int dc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("DepartmentCrt",
				"dc_statuscode", "dc_id=" + dc_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.resSubmit_onlyCommited"));
		}
		handlerService.beforeResSubmit(caller, dc_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"DepartmentCrt",
				"dc_statuscode='ENTERING',dc_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'",
				"dc_id=" + dc_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "dc_id", dc_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, dc_id);

	}

	@Override
	public void auditDepartmentCrt(int dc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("DepartmentCrt",
				"dc_statuscode", "dc_id=" + dc_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, dc_id);
		// 执行审核操作
		Employee employee = SystemSession.getUser();
		baseDao.updateByCondition(
				"DepartmentCrt",
				"dc_statuscode='AUDITED',dc_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',dc_auditman='" + employee.getEm_name()
						+ "',dc_auditdate=sysdate", "dc_id=" + dc_id);
		// 记录操作
		baseDao.logger.audit(caller, "dc_id", dc_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, dc_id);

	}

	@Override
	public void resAuditDepartmentCrt(int dc_id, String caller) {
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, dc_id);
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("DepartmentCrt",
				"dc_statuscode", "dc_id=" + dc_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.resAudit_onlyAudit"));
		}
		// 执行反审核操作
		baseDao.updateByCondition(
				"DepartmentCrt",
				"dc_statuscode='ENTERING',dc_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',dc_auditman='',dc_auditdate=null", "dc_id=" + dc_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "dc_id", dc_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, dc_id);

	}

}
