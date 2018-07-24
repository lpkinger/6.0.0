package com.uas.erp.service.crm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;


import com.uas.erp.service.crm.ResearchProjectService;

@Service
public class ResearchProjectServiceImpl implements ResearchProjectService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveResearchProject(String formStore, String gridStore,  String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 计算剩余预算金额
		for (Map<Object, Object> map : gstore) {
			map.put("ppd_surplus", map.get("ppd_amount"));
			map.put("ppd_id", baseDao.getSeqId("ResearchProjectDETAIL_SEQ"));
		}
		handlerService.beforeSave(caller, new Object[]{store,gstore});
		String formSql = SqlUtil.getInsertSqlByMap(store, "ResearchProject");
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gstore, "ResearchProjectDetail");
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "pp_id", store.get("pp_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store,gstore});
	}

	@Override
	public void deleteResearchProject(int pp_id,  String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, pp_id);
		// 删除purchase
		baseDao.deleteById("ResearchProject", "pp_id", pp_id);
		baseDao.deleteById("ResearchProjectdetail", "ppd_ppid", pp_id);
		// 记录操作
		baseDao.logger.delete(caller, "pp_id", pp_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, pp_id);
	}

	@Override
	public void updateResearchProject(String formStore, String gridStore,  String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[]{store,gstore});
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ResearchProject", "pp_id");
		baseDao.execute(formSql);
		// 计算剩余预算金额
		for (Map<Object, Object> map : gstore) {
			map.put("ppd_surplus",
					Double.parseDouble(String.valueOf(map.get("ppd_amount")))
							- Double.parseDouble(String.valueOf(map.get("ppd_used"))));
		}
		// 修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "ResearchProjectDetail", "ppd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("ppd_id") == null || s.get("ppd_id").equals("") || s.get("ppd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("ResearchProjectDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "ResearchProjectDetail", new String[] { "ppd_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "pp_id", store.get("pp_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store,gstore});
	}

	@Override
	public void auditResearchProject(int pp_id,  String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ResearchProject", "pp_statuscode", "pp_id=" + pp_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, pp_id);
		// 执行审核操作
		baseDao.updateByCondition("ResearchProject",
				"pp_statuscode='AUDITED',pp_status='" + BaseUtil.getLocalMessage("AUDITED") + "',pp_auditer='"+SystemSession.getUser().getEm_name()+"',pp_auditdate=sysdate", "pp_id="
						+ pp_id);
		// 记录操作
		baseDao.logger.audit(caller, "pp_id", pp_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, pp_id);
	}

	@Override
	public void resAuditResearchProject(int pp_id,  String caller) {
		Object status = baseDao.getFieldDataByCondition("ResearchProject", "pp_statuscode", "pp_id=" + pp_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, pp_id);
		// 执行反审核操作
		baseDao.updateByCondition("ResearchProject",
				"pp_statuscode='ENTERING',pp_status='" + BaseUtil.getLocalMessage("ENTERING") + "',pp_auditer='',pp_auditdate=null", "pp_id="
						+ pp_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "pp_id", pp_id);
		handlerService.afterResAudit(caller, pp_id);
	}

	@Override
	public void submitResearchProject(int pp_id,  String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ResearchProject", "pp_statuscode", "pp_id=" + pp_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, pp_id);
		// 执行提交操作
		baseDao.updateByCondition("ResearchProject",
				"pp_statuscode='COMMITED',pp_status='" + BaseUtil.getLocalMessage("COMMITED") + "'", "pp_id="
						+ pp_id);
		// 记录操作
		baseDao.logger.submit(caller, "pp_id", pp_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, pp_id);
	}

	@Override
	public void resSubmitResearchProject(int pp_id,  String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ResearchProject", "pp_statuscode", "pp_id=" + pp_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, pp_id);
		// 执行反提交操作
		baseDao.updateByCondition("ResearchProject",
				"pp_statuscode='ENTERING',pp_status='" + BaseUtil.getLocalMessage("ENTERING") + "'", "pp_id="
						+ pp_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "pp_id", pp_id);
		handlerService.afterResSubmit(caller, pp_id);
	}

}
