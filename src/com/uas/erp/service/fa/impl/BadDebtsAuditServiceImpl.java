package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.BadDebtsAuditDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.BadDebtsAuditService;

@Service("BadDebtsAuditService")
public class BadDebtsAuditServiceImpl implements BadDebtsAuditService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private BadDebtsAuditDao badDebtsAuditDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveBadDebtsAudit(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore); // 从表grid数据
		String bda_code = store.get("bda_code").toString();
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BadDebtsAudit", "bda_code='" + bda_code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存主表
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BadDebtsAudit", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存明细表
		for (Map<Object, Object> map : grid) {
			map.put("bdad_id", baseDao.getSeqId("BadDebtsAuditDetail_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "BadDebtsAuditDetail");
		// 明细行发票编号重复限制保存
		String check = baseDao.executeWithCheck(gridSql, null,
				"select wm_concat(bdad_ordercode) from  BadDebtsAuditDetail where bdad_bdaid=" + store.get("bda_id")
						+ "  group  by  bdad_ordercode  having  count(bdad_ordercode) > 1");
		if (check != null && check.length() > 0) {
			BaseUtil.showError("明细行发票编号重复");
		}
		// update 主表 bda_arrearageamount
		baseDao.execute("update BadDebtsAudit set bda_arrearageamount=(select sum(bdad_nowbalance) from BadDebtsAuditdetail where bdad_bdaid="
				+ store.get("bda_id") + ") where bda_id=" + store.get("bda_id") + "");
		baseDao.logger.save(caller, "bda_id", store.get("bda_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteBadDebtsAudit(int bda_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("BadDebtsAudit", "bda_statuscode", "bda_id=" + bda_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { bda_id });
		// delete BadDebtsAudit
		baseDao.deleteById("BadDebtsAudit", "bda_id", bda_id);
		// delete BadDebtsAuditDetail
		baseDao.deleteById("BadDebtsAuditDetail", "bdad_id", bda_id);

		// 记录操作
		baseDao.logger.delete(caller, "bda_id", bda_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, bda_id);
	}

	@Override
	public void updateBadDebtsAuditById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		/*
		 * Object status = baseDao.getFieldDataByCondition("BadDebtsAudit",
		 * "bda_statuscode", "bda_id=" + store.get("bda_id")); if
		 * (!status.equals("ENTERING")) { BaseUtil.showError(BaseUtil
		 * .getLocalMessage("common.update_onlyEntering")); }
		 */
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改主表
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BadDebtsAudit", "bda_id");
		baseDao.execute(formSql);
		// 修改明细表
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "BadDebtsAuditDetail", "bdad_id");
		for (Map<Object, Object> s : grid) {
			if (s.get("bdad_id") == null || s.get("bdad_id").equals("") || s.get("bdad_id").equals("0")
					|| Integer.parseInt(s.get("bdad_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("BadDebtsAuditDetail_SEQ");
				s.put("bdad_id", id);
				gridSql.add(SqlUtil.getInsertSqlByMap(s, "BadDebtsAuditDetail"));
			}
		}
		// 明细行发票编号重复 限制更新
		String check = baseDao.executeWithCheck(gridSql, null,
				"select wm_concat(bdad_ordercode) from  BadDebtsAuditDetail where bdad_bdaid=" + store.get("bda_id")
						+ "  group  by  bdad_ordercode  having  count(bdad_ordercode) > 1");
		if (check != null && check.length() > 0) {
			BaseUtil.showError("明细行发票编号重复");
		}
		// update 主表 bda_arrearageamount
		baseDao.execute("update BadDebtsAudit set bda_arrearageamount=(select sum(bdad_nowbalance) from BadDebtsAuditdetail where bdad_bdaid="
				+ store.get("bda_id") + ") where bda_id=" + store.get("bda_id") + "");
		// 记录操作
		baseDao.logger.update(caller, "bda_id", store.get("bda_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void auditBadDebtsAudit(int bda_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("BadDebtsAudit", "bda_statuscode", "bda_id=" + bda_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, bda_id);
		// 执行审核操作
		baseDao.updateByCondition(caller, "bda_statuscode='AUDITED',bda_status='" + BaseUtil.getLocalMessage("AUDITED") + "'", "bda_id="
				+ bda_id);
		// 记录操作
		baseDao.logger.audit(caller, "bda_id", bda_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, bda_id);
	}

	@Override
	public void resAuditBadDebtsAudit(int bda_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("BadDebtsAudit", "bda_statuscode", "bda_id=" + bda_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, bda_id);
		// 执行反审核操作
		baseDao.updateByCondition(caller, "bda_statuscode='ENTERING',bda_status='" + BaseUtil.getLocalMessage("ENTERING") + "'", "bda_id="
				+ bda_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "bda_id", bda_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, bda_id);
	}

	@Override
	public void submitBadDebtsAudit(int bda_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("BadDebtsAudit", "bda_statuscode", "bda_id=" + bda_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.submit_onlyEntering"));
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, bda_id);
		// 执行提交操作
		baseDao.updateByCondition(caller, "bda_statuscode='COMMITED',bda_status='" + BaseUtil.getLocalMessage("COMMITED") + "'", "bda_id="
				+ bda_id);
		// 记录操作
		baseDao.logger.submit(caller, "bda_id", bda_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, bda_id);
	}

	@Override
	public void resSubmitBadDebtsAudit(int bda_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("BadDebtsAudit", "bda_statuscode", "bda_id=" + bda_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resSubmit_onlyCommited"));
		}
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, bda_id);
		// 执行反提交操作
		baseDao.updateByCondition("BadDebtsAudit", "bda_statuscode='ENTERING',bda_status='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"bda_id=" + bda_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "bda_id", bda_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, bda_id);
	}

	@Override
	public int turnRecBalanceIMRE(int bda_id, String caller) {
		int rbid = 0;
		Employee employee = SystemSession.getUser();
		// 转冲应收款单
		rbid = badDebtsAuditDao.turnRecBalanceIMRE(bda_id, caller);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), "转冲应收款单", BaseUtil.getLocalMessage("msg.turnSuccess"),
				"BadDebtsAudit|bda_id=" + bda_id));
		return rbid;
	}

	@Override
	public String[] printBadDebtsAudit(int bda_id, String reportName, String condition) {
		// 执行打印前的其它逻辑
		handlerService.handler("BadDebtsAudit", "print", "before", new Object[] { bda_id });
		// 执行打印操作
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		Employee employee = SystemSession.getUser();
		// 修改打印状态
		baseDao.updateByCondition("BadDebtsAudit", "bda_printstatuscode='PRINTED',bda_printstatus='" + BaseUtil.getLocalMessage("PRINTED")
				+ "'", "bda_id=" + bda_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.print"), BaseUtil
				.getLocalMessage("msg.printSuccess"), "BadDebtsAudit|bda_id=" + bda_id));
		// 执行打印后的其它逻辑
		handlerService.handler("BadDebtsAudit", "print", "after", new Object[] { bda_id });
		return keys;
	}
}
