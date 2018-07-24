package com.uas.erp.service.scm.impl;

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
import com.uas.erp.service.scm.EngineerContrastService;

@Service
public class EngineerContrastServiceImpl implements EngineerContrastService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveEngineerContrast(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存EngineerContrast
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"EngineerContrast", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存Contact
		for (Map<Object, Object> s : grid) {
			s.put("ecd_id", baseDao.getSeqId("EngineerContrastdetail_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"EngineerContrastdetail");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.save(caller, "ec_id", store.get("ec_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteEngineerContrast(int ec_id, String caller) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("EngineerContrast",
				"ec_statuscode", "ec_id=" + ec_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { ec_id });
		// 删除EngineerContrast
		baseDao.deleteById("EngineerContrast", "ec_id", ec_id);
		// 删除Contact
		baseDao.deleteById("EngineerContrastdetail", "ecd_ecid", ec_id);
		// 记录操作
		baseDao.logger.delete(caller, "ec_id", ec_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ec_id);
	}

	@Override
	public void updateEngineerContrastById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("EngineerContrast",
				"ec_statuscode", "ec_id=" + store.get("ec_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.update_onlyEntering"));
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改EngineerContrast
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"EngineerContrast", "ec_id");
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"EngineerContrastdetail", "ecd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("ecd_id") == null || s.get("ecd_id").equals("")
					|| s.get("ecd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("EngineerContrastdetail_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s,
						"EngineerContrastdetail", new String[] { "ecd_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "ec_id", store.get("ec_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });

	}

	@Override
	public void submitEngineerContrast(int ec_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("EngineerContrast",
				"ec_statuscode", "ec_id=" + ec_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.submit_onlyEntering"));
		}
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('行号：'||ecd_detno||'，物料编号：'||ecd_materialcode) from EngineerContrastdetail a left join EngineerContrast on ecd_ecid=ec_id "
						+ "where (ecd_materialcode,ec_dept) in "
						+ "(select ecd_materialcode,ec_dept from (EngineerContrastdetail b left join EngineerContrast on ecd_ecid=ec_id) " +
						" where b.ecd_id<>a.ecd_id)  and ec_id=?",
						String.class, ec_id);
		if (dets != null) {
			BaseUtil.showError("明细行物料已存在!" + dets);
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ec_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"EngineerContrast",
				"ec_statuscode='COMMITED',ec_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'",
				"ec_id=" + ec_id);
		// 记录操作
		baseDao.logger.submit(caller, "ec_id", ec_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ec_id);

	}

	@Override
	public void resSubmitEngineerContrast(int ec_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("EngineerContrast",
				"ec_statuscode", "ec_id=" + ec_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.resSubmit_onlyCommited"));
		}
		handlerService.beforeResSubmit(caller, ec_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"EngineerContrast",
				"ec_statuscode='ENTERING',ec_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'",
				"ec_id=" + ec_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ec_id", ec_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, ec_id);

	}

	@Override
	public void auditEngineerContrast(int ec_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("EngineerContrast",
				"ec_statuscode", "ec_id=" + ec_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ec_id);
		// 执行审核操作
		Employee employee = SystemSession.getUser();
		baseDao.updateByCondition(
				"EngineerContrast",
				"ec_statuscode='AUDITED',ec_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',ec_auditer='" + employee.getEm_name()
						+ "',ec_auditdate=sysdate", "ec_id=" + ec_id);
		// 记录操作
		baseDao.logger.audit(caller, "ec_id", ec_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ec_id);

	}

	@Override
	public void resAuditEngineerContrast(int ec_id, String caller) {
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, ec_id);
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("EngineerContrast",
				"ec_statuscode", "ec_id=" + ec_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.resAudit_onlyAudit"));
		}
		// 执行反审核操作
		baseDao.updateByCondition(
				"EngineerContrast",
				"ec_statuscode='ENTERING',ec_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',ec_auditer='',ec_auditdate=null", "ec_id=" + ec_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ec_id", ec_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, ec_id);

	}

}
