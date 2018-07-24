package com.uas.erp.service.oa.impl;

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
import com.uas.erp.service.oa.TiChengService;

@Service
public class TiChengServiceImpl implements TiChengService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveTiCheng(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存TiCheng
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "TiCheng",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		//保存Contact
		for (Map<Object, Object> s : grid) {
			s.put("tcd_id", baseDao.getSeqId("TiChengdet_SEQ"));
		}
		//保存从表
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"TiChengdet");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.save(caller, "tc_id", store.get("tc_id"));	
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });

	}

	@Override
	public void deleteTiCheng(int tc_id, String caller) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("TiCheng",
				"tc_statuscode", "tc_id=" + tc_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { tc_id });
		// 删除TiCheng
		baseDao.deleteById("TiCheng", "tc_id", tc_id);
		// 删除Contact
		baseDao.deleteById("TiChengdet", "tcd_tcid", tc_id);
		// 记录操作
		baseDao.logger.delete(caller, "tc_id", tc_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, tc_id);

	}

	@Override
	public void updateTiChengById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("TiCheng",
				"tc_statuscode", "tc_id=" + store.get("tc_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.update_onlyEntering"));
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改TiCheng
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "TiCheng",
				"tc_id");
		baseDao.execute(formSql);
		// 修改从表
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"TiChengdet", "tcd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("tcd_id") == null || s.get("tcd_id").equals("")
					|| s.get("tcd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("TiChengdet_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "TiChengdet",
						new String[] { "tcd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "tc_id", store.get("tc_id"));
	    // 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });

	}

	@Override
	public void submitTiCheng(int tc_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("TiCheng",
				"tc_statuscode", "tc_id=" + tc_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.submit_onlyEntering"));
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, tc_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"TiCheng",
				"tc_statuscode='COMMITED',tc_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'",
				"tc_id=" + tc_id);
		// 记录操作
		baseDao.logger.submit(caller, "tc_id", tc_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, tc_id);

	}

	@Override
	public void resSubmitTiCheng(int tc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("TiCheng",
				"tc_statuscode", "tc_id=" + tc_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.resSubmit_onlyCommited"));
		}
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, tc_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"TiCheng",
				"tc_statuscode='ENTERING',tc_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'",
				"tc_id=" + tc_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "tc_id", tc_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, tc_id);

	}

	@Override
	public void auditTiCheng(int tc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("TiCheng",
				"tc_statuscode", "tc_id=" + tc_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, tc_id);
		Employee employee = SystemSession.getUser();
		// 执行审核操作
		baseDao.updateByCondition(
				"TiCheng",
				"tc_statuscode='AUDITED',tc_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',tc_auditer='" + employee.getEm_name()
						+ "',tc_auditdate=sysdate", "tc_id=" + tc_id);
		
		// 记录操作
		baseDao.logger.audit(caller, "tc_id", tc_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, tc_id);

	}

	@Override
	public void resAuditTiCheng(int tc_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("TiCheng",
				"tc_statuscode", "tc_id=" + tc_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.resAudit_onlyAudit"));
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, tc_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"TiCheng",
				"tc_statuscode='ENTERING',tc_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',tc_auditer=null,tc_auditdate=null", "tc_id="
						+ tc_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "tc_id", tc_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, tc_id);

	}

}
