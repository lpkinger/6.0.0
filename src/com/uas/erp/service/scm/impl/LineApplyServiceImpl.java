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
import com.uas.erp.service.scm.LineApplyService;

@Service
public class LineApplyServiceImpl implements LineApplyService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveLineApply(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存LineApply
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "LineApply", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存Contact
		for (Map<Object, Object> s : grid) {
			s.put("lad_id", baseDao.getSeqId("LineApplydetail_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "LineApplydetail");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.save(caller, "la_id", store.get("la_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });

	}

	@Override
	public void deleteLineApply(int la_id, String caller) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("LineApply", "la_statuscode", "la_id=" + la_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { la_id });
		// 删除LineApply
		baseDao.deleteById("LineApply", "la_id", la_id);
		// 删除Contact
		baseDao.deleteById("LineApplydetail", "lad_laid", la_id);
		// 记录操作
		baseDao.logger.delete(caller, "la_id", la_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, la_id);
	}

	@Override
	public void updateLineApplyById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("LineApply", "la_statuscode", "la_id=" + store.get("la_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改LineApply
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "LineApply", "la_id");
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "LineApplydetail", "lad_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("lad_id") == null || s.get("lad_id").equals("") || s.get("lad_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("LineApplydetail_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "LineApplydetail", new String[] { "lad_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "la_id", store.get("la_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });

	}

	@Override
	public void submitLineApply(int la_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("LineApply", "la_statuscode", "la_id=" + la_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.submit_onlyEntering"));
		}
		boolean flag = baseDao.isDBSetting(caller, "commitLimit");
		if(flag) {
			Object count = baseDao.getFieldDataByCondition("LineApply", "count(1)",
					"la_statuscode<>'ENTERING' AND LA_EMCODE=(SELECT LA_EMCODE FROM LINEAPPLY WHERE LA_ID=" + la_id + ")");
			if (Integer.parseInt(count + "") >= 8) {
				BaseUtil.showError("您目前已经有8条已提交、已审核的产品线申请表，不能提交！");
			}
		}
		Object[] datass = baseDao.getFieldsDataByCondition("LineApply", new String[] { "la_emcode", "la_brand" }, "la_id=" + la_id);
		Object count1 = baseDao.getFieldDataByCondition("BigLineApply", "count(1)", "ba_emcode='" + datass[0] + "' and ba_brand='"
				+ datass[1] + "'");
		if (Integer.parseInt(count1 + "") >= 1) {
			BaseUtil.showError("您目前已经在SPM产品线中申请了该品牌，不能提交！");
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, la_id);
		// 执行提交操作
		baseDao.updateByCondition("LineApply", "la_statuscode='COMMITED',la_status='" + BaseUtil.getLocalMessage("COMMITED") + "'",
				"la_id=" + la_id);
		// 记录操作
		baseDao.logger.submit(caller, "la_id", la_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, la_id);

	}

	@Override
	public void resSubmitLineApply(int la_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("LineApply", "la_statuscode", "la_id=" + la_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resSubmit_onlyCommited"));
		}
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, la_id);
		// 执行反提交操作
		baseDao.updateByCondition("LineApply", "la_statuscode='ENTERING',la_status='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"la_id=" + la_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "la_id", la_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, la_id);

	}

	@Override
	public void auditLineApply(int la_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("LineApply", "la_statuscode", "la_id=" + la_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.audit_onlyCommited"));
		}
		Employee employee = SystemSession.getUser();
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, la_id);
		// 执行审核操作
		baseDao.updateByCondition("LineApply", "la_statuscode='AUDITED',la_status='" + BaseUtil.getLocalMessage("AUDITED")
				+ "',la_auditer='" + employee.getEm_name() + "',la_auditdate=sysdate", "la_id=" + la_id);
		// 记录操作
		baseDao.logger.audit(caller, "la_id", la_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, la_id);

	}

	@Override
	public void resAuditLineApply(int la_id, String caller) {
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, la_id);
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("LineApply", "la_statuscode", "la_id=" + la_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
		}
		// 执行反审核操作
		baseDao.updateByCondition("LineApply", "la_statuscode='ENTERING',la_status='" + BaseUtil.getLocalMessage("ENTERING")
				+ "',la_auditer='',la_auditdate=null", "la_id=" + la_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "la_id", la_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, la_id);
	}

}
