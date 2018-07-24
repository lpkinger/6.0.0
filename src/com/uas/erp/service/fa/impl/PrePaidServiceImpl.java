package com.uas.erp.service.fa.impl;

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

import com.uas.erp.service.fa.PrePaidService;

@Service("prePaidService")
public class PrePaidServiceImpl implements PrePaidService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void deletePrePaid(int pp_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("PrePaid",
				"pp_statuscode", "pp_id=" + pp_id);
		if (!status.equals("ENTERING") && !status.equals("UNPOST")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, pp_id);
		// 删除PrePaid
		baseDao.deleteById("PrePaid", "pp_id", pp_id);
		// 删除PrePaidDetail
		baseDao.deleteById("PrePaiddetail", "pd_ppid", pp_id);
		// 记录操作
		baseDao.logger.delete(caller, "pp_id", pp_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, pp_id);
	}

	@Override
	public void updatePrePaidById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("PrePaid",
				"pp_statuscode", "pp_id=" + store.get("pp_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.handler("PrePaid", "save", "before",
				new Object[] { store });
		// 修改PrePaid
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PrePaid",
				"pp_id");
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore,
				"PrePaidDetail", "pd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("pd_id") == null || s.get("pd_id").equals("")
					|| s.get("pd_id").equals("0")
					|| Integer.parseInt(s.get("pd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("PrePaidDETAIL_SEQ");
				s.put("pd_class", store.get("pp_class"));
				s.put("pd_code", store.get("pp_code"));
				String sql = SqlUtil.getInsertSqlByMap(s, "PrePaidDetail",
						new String[] { "pd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "pp_id", store.get("pp_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void auditPrePaid(int pp_id, String caller) {
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, pp_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"PrePaid",
				"pp_statuscode='AUDITED',pp_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',PP_AUDITMAN='"
						+ SystemSession.getUser().getEm_name()
						+ "',PP_AUDITdate=sysdate", "pp_id=" + pp_id);
		baseDao.logger.audit(caller, "pp_id", pp_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, pp_id);
	}

	@Override
	public void resAuditPrePaid(int pp_id, String caller) {
		// 只能反审核已审核的资料!
		Object status = baseDao.getFieldDataByCondition("PrePaid",
				"pp_statuscode", "pp_id=" + pp_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, pp_id);
		baseDao.updateByCondition(
				"PrePaid",
				"pp_statuscode='ENTERING',pp_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',PP_AUDITMAN='',PP_AUDITdate=null", "pp_id="
						+ pp_id);
		handlerService.afterResAudit(caller, pp_id);
	}

	@Override
	public void savePrePaid(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("PrePaid",
				"pp_code='" + store.get("pp_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 保存PrePaid
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PrePaid",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存PrePaidDetail
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		for (Map<Object, Object> m : grid) {
			m.put("pd_id", baseDao.getSeqId("PrePaidDETAIL_SEQ"));
			m.put("pd_class", store.get("pp_class"));
			m.put("pd_code", store.get("pp_code"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"PrePaidDetail");
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "pp_id", store.get("pp_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void postPrePaid(int pp_id, String caller) {
		// 执行过账前的其它逻辑
		handlerService.beforePost(caller, pp_id);
		// 执行过账操作
		String res = baseDao.callProcedure(
				"SP_COMMITPREPAIDINOUT",
				new Object[] { pp_id,
						String.valueOf(SystemSession.getUser().getEm_id()) });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
		baseDao.updateByCondition(
				"PrePaid",
				"pp_statuscode='POSTED',pp_status='"
						+ BaseUtil.getLocalMessage("POSTED") + "'", "pp_id="
						+ pp_id);
		baseDao.logger.post(caller, "pp_id", pp_id);
		// 执行审核后的其它逻辑
		handlerService.afterPost(caller, pp_id);
	}

	@Override
	public void resPostPrePaid(int pp_id, String caller) {
		// 只能反过账已过账的资料!
		Object status = baseDao.getFieldDataByCondition("PrePaid",
				"pp_statuscode", "pp_id=" + pp_id);
		StateAssert.resPostOnlyPosted(status);
		handlerService.beforeResPost(caller, pp_id);
		String res = baseDao.callProcedure(
				"SP_UNCOMMITPREPAIDINOUT",
				new Object[] { pp_id,
						String.valueOf(SystemSession.getUser().getEm_id()) });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
		baseDao.updateByCondition(
				"PrePaid",
				"pp_statuscode='AUDITED',pp_status='"
						+ BaseUtil.getLocalMessage("AUDITED") + "'", "pp_id="
						+ pp_id);
		baseDao.logger.resPost(caller, "pp_id", pp_id);
		handlerService.afterPost(caller, pp_id);
	}
}
