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
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.ReceiveBudgetService;

@Service
public class ReceiveBudgetServiceImpl implements ReceiveBudgetService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveReceiveBudget(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore); // 从表grid数据
		String rb_code = store.get("rb_code").toString();
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ReceiveBudget", "rb_code='" + rb_code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存主表
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ReceiveBudget", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存从表
		for (Map<Object, Object> map : grid) {
			map.put("rbd_id", baseDao.getSeqId("ReceiveBudgetDetail_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "ReceiveBudgetDetail");
		// 明细行客户编号重复限制保存
		String check = baseDao.executeWithCheck(gridSql, null,
				"select wm_concat(rbd_custcode) from  ReceiveBudgetDetail left join ReceiveBudget on rbd_rbid=rb_id where rbd_rbid="
						+ store.get("rb_id") + "  group  by  rb_sellercode,rbd_custcode,rbd_currency   having  count(rbd_custcode) > 1");
		if (check != null && check.length() > 0) {
			BaseUtil.showError("明细行客户编号重复");
		}
		// 记录日志
		baseDao.logger.save(caller, "rb_id", store.get("rb_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteReceiveBudget(int rb_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("ReceiveBudget", "rb_statuscode", "rb_id=" + rb_id);
		if (status != null && !status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { rb_id });
		// 删除主表
		baseDao.deleteById("ReceiveBudget", "rb_id", rb_id);
		// 删除明细表
		baseDao.deleteById("ReceiveBudgetDetail", "rbd_rbid", rb_id);
		// 记录操作
		baseDao.logger.delete(caller, "rb_id", rb_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, rb_id);
	}

	@Override
	public void updateReceiveBudgetById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("ReceiveBudget", "rb_statuscode", "rb_id=" + store.get("rb_id"));
		if (status != null && !status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改主表
		/*
		 * String formSql = SqlUtil.getUpdateSqlByFormStore(store,
		 * "ReceiveBudget", "rb_id"); baseDao.execute(formSql);
		 */
		// 修改明细表
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ReceiveBudgetDetail", "rbd_id");
		for (Map<Object, Object> s : grid) {
			if (s.get("rbd_id") == null || s.get("rbd_id").equals("") || s.get("rbd_id").equals("0")
					|| Integer.parseInt(s.get("rbd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("ReceiveBudgetDetail_SEQ");
				s.put("rbd_id", id);
				gridSql.add(SqlUtil.getInsertSqlByMap(s, "ReceiveBudgetDetail"));
			}
		}
		// 明细行客户编号重复 限制更新
		String check = baseDao.executeWithCheck(gridSql, null,
				"select wm_concat(rbd_custcode) from  ReceiveBudgetDetail  left join ReceiveBudget on rbd_rbid=rb_id where rbd_rbid="
						+ store.get("rb_id") + "  group  by  rb_sellercode,rbd_custcode,rbd_currency  having  count(rbd_custcode) > 1");
		if (check != null && check.length() > 0) {
			BaseUtil.showError("明细行客户编号重复");
		}
		// 记录操作
		baseDao.logger.update(caller, "rb_id", store.get("rb_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void auditReceiveBudget(int rb_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ReceiveBudget", "rb_statuscode", "rb_id=" + rb_id);
		if (status != null && !status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, rb_id);
		// 执行审核操作
		baseDao.updateByCondition(caller, "rb_statuscode='AUDITED',rb_status='" + BaseUtil.getLocalMessage("AUDITED") + "'", "rb_id="
				+ rb_id);
		// 记录操作
		baseDao.logger.audit(caller, "rb_id", rb_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, rb_id);
	}

	@Override
	public void resAuditReceiveBudget(int rb_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ReceiveBudget", "rb_statuscode", "rb_id=" + rb_id);
		if (status != null && !status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, rb_id);
		// 执行反审核操作
		baseDao.updateByCondition(caller, "rb_statuscode='ENTERING',rb_status='" + BaseUtil.getLocalMessage("ENTERING") + "'", "rb_id="
				+ rb_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "rb_id", rb_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, rb_id);
	}

	@Override
	public void submitReceiveBudget(int rb_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ReceiveBudget", "rb_statuscode", "rb_id=" + rb_id);
		if (status != null && !status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.submit_onlyEntering"));
		}
		// 收款预算总额
		baseDao.execute("update ReceiveBudget set rb_receivebudget=nvl((select sum(nvl(rbd_receivebudget,0)) from ReceiveBudgetDetail where rbd_rbid=rb_id),0) where rb_id="
				+ rb_id);
		// 当期应回款总额
		baseDao.execute("update ReceiveBudget set rb_thismonthback=nvl((select sum(nvl(rbd_thismonthback,0)) from ReceiveBudgetDetail where rbd_rbid=rb_id),0) where rb_id="
				+ rb_id);
		// 预算占应回款比
		baseDao.execute("update ReceiveBudget set rb_proportion=round(nvl(rb_receivebudget,0)/nvl(rb_thismonthback,1),6)*100 where rb_id="
				+ rb_id + " and nvl(rb_thismonthback,0)<>0");
		baseDao.execute("update ReceiveBudget set rb_proportion=100 where rb_id=" + rb_id + " and nvl(rb_proportion,0)>100");
		// 预算达成率
		baseDao.execute("update ReceiveBudget set rb_budgetrate=nvl((select round(sum(nvl(rbd_inbudgetreceived,0))/sum(nvl(rbd_receivebudget,0))*0.5+sum(nvl(rbd_actualback,0))/sum(nvl(rbd_thismonthback,0))*0.5,6)*100 from ReceiveBudgetDetail where rbd_rbid=rb_id and nvl(rbd_thismonthback,0) >0 and nvl(rbd_receivebudget,0)>0),0) where rb_id="
				+ rb_id);
		baseDao.execute("update ReceiveBudget set rb_budgetrate=100 where rb_id=" + rb_id + " and nvl(rb_budgetrate,0)>100");
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, rb_id);
		// 执行提交操作
		baseDao.updateByCondition(caller, "rb_statuscode='COMMITED',rb_status='" + BaseUtil.getLocalMessage("COMMITED") + "'", "rb_id="
				+ rb_id);
		// 记录操作
		baseDao.logger.submit(caller, "rb_id", rb_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, rb_id);
	}

	@Override
	public void resSubmitReceiveBudget(int rb_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ReceiveBudget", "rb_statuscode", "rb_id=" + rb_id);
		if (status != null && !status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resSubmit_onlyCommited"));
		}
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, rb_id);
		// 执行反提交操作
		baseDao.updateByCondition("ReceiveBudget", "rb_statuscode='ENTERING',rb_status='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"rb_id=" + rb_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "rb_id", rb_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, rb_id);
	}

	@Override
	public void CalBudget(int yearmonth) {
		String res = baseDao.callProcedure("SP_CALBUDGET", new Object[] { yearmonth });
		Employee employee = SystemSession.getUser();
		if (res.equals("OK")) {
			baseDao.logMessage(new MessageLog(employee.getEm_name(), "刷新收款预算单", "刷新收款预算单", "刷新收款预算成功"));
		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public String[] printReceiveBudget(int rb_id, String reportName, String condition) {
		return null;
	}
}
