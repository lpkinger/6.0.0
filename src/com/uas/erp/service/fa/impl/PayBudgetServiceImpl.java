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
import com.uas.erp.service.fa.PayBudgetService;

@Service
public class PayBudgetServiceImpl implements PayBudgetService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void savePayBudget(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore); // 从表grid数据
		String pb_code = store.get("pb_code").toString();
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("PayBudget", "pb_code='" + pb_code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存主表
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PayBudget", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存从表
		for (Map<Object, Object> map : grid) {
			map.put("pbd_id", baseDao.getSeqId("PayBudgetDetail_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "PayBudgetDetail");
		String check = baseDao.executeWithCheck(
				gridSql,
				null,
				"select wm_concat(pbd_vendorcode) from  PayBudgetDetail left join PAYBUDGET on pbd_pbid=pb_id where pbd_pbid="
						+ store.get("pb_id") + "  group  by  pb_sellercode,pbd_vendorcode,pbd_currency  having  count(pbd_vendorcode) > 1");
		if (check != null && check.length() > 0) {
			BaseUtil.showError("明细行供应商编号重复");
		}
		baseDao.logger.save(caller, "pb_id", store.get("pb_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deletePayBudget(int pb_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("PayBudget", "pb_statuscode", "pb_id=" + pb_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { pb_id });
		// delete PayBudget
		baseDao.deleteById("PayBudget", "pb_id", pb_id);
		// delete PayBudgetDetail
		baseDao.deleteById("PayBudgetDetail", "pbd_pbid", pb_id);

		// 记录操作
		baseDao.logger.delete(caller, "pb_id", pb_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, pb_id);
	}

	@Override
	public void updatePayBudgetById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("PayBudget", "pb_statuscode", "pb_id=" + store.get("pb_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改PayBudget
		/*
		 * String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PayBudget",
		 * "pb_id"); baseDao.execute(formSql);
		 */
		// UPDATE PayBudgetDETAIL
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "PayBudgetDetail", "pbd_id");
		for (Map<Object, Object> s : grid) {
			if (s.get("pbd_id") == null || s.get("pbd_id").equals("") || s.get("pbd_id").equals("0")
					|| Integer.parseInt(s.get("pbd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("PayBudgetDetail_SEQ");
				s.put("pbd_id", id);
				gridSql.add(SqlUtil.getInsertSqlByMap(s, "PayBudgetDetail"));
			}
		}
		String check = baseDao.executeWithCheck(
				gridSql,
				null,
				"select wm_concat(pbd_vendorcode) from  PayBudgetDetail left join PAYBUDGET on pbd_pbid=pb_id where pbd_pbid="
						+ store.get("pb_id") + "  group  by  pb_sellercode,pbd_vendorcode,pbd_currency  having  count(pbd_vendorcode) > 1");
		if (check != null && check.length() > 0) {
			BaseUtil.showError("明细行供应商编号重复");
		}
		// 记录操作
		baseDao.logger.update(caller, "pb_id", store.get("pb_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void auditPayBudget(int pb_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("PayBudget", "pb_statuscode", "pb_id=" + pb_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, pb_id);
		// 执行审核操作
		baseDao.updateByCondition(caller, "pb_statuscode='AUDITED',pb_status='" + BaseUtil.getLocalMessage("AUDITED") + "'", "pb_id="
				+ pb_id);
		// 记录操作
		baseDao.logger.audit(caller, "pb_id", pb_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, pb_id);
	}

	@Override
	public void resAuditPayBudget(int pb_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("PayBudget", "pb_statuscode", "pb_id=" + pb_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, pb_id);
		// 执行反审核操作
		baseDao.updateByCondition(caller, "pb_statuscode='ENTERING',pb_status='" + BaseUtil.getLocalMessage("ENTERING") + "'", "pb_id="
				+ pb_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "pb_id", pb_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, pb_id);
	}

	@Override
	public void submitPayBudget(int pb_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("PayBudget", "pb_statuscode", "pb_id=" + pb_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.submit_onlyEntering"));
		}
		// 付款预算总额
		baseDao.execute("update PAYBUDGET set PB_PAYBUDGET=nvl((select sum(nvl(pbd_paybudget,0)) from PAYBUDGETDetail where PBD_PBID=pb_id),0) where pb_id="
				+ pb_id);
		// 当期应付款总额
		baseDao.execute("update PAYBUDGET set PB_THISMONTHPAY=nvl((select sum(nvl(pbd_thismonthpay,0)) from PAYBUDGETDetail where PBD_PBID=pb_id),0) where pb_id="
				+ pb_id);
		// 超额预算
		baseDao.execute("update PAYBUDGETDetail set pbd_overbudget =nvl(pbd_paybudget,0)-nvl(pbd_thismonthpay,0) where pbd_pbid=" + pb_id);
		// 预算占应付款比
		baseDao.execute("update PAYBUDGET set PB_PROPORTION=round(nvl(PB_PAYBUDGET,0)/nvl(PB_THISMONTHPAY,0),6)*100 where pb_id=" + pb_id
				+ " and nvl(PB_THISMONTHPAY,0)>0");
		baseDao.execute("update PAYBUDGET set PB_PROPORTION=100 where pb_id=" + pb_id + " and nvl(PB_PROPORTION,0)>100");
		// 预算达成率
		baseDao.execute("update PAYBUDGET set PB_REACHRATE=nvl((select round(sum(nvl(pbd_actualpay,0))/sum(nvl(pbd_paybudget,0)),6)*100 from PAYBUDGETDetail where PBD_PBID=pb_id and nvl(pbd_paybudget,0) >0),0) where pb_id="
				+ pb_id);
		baseDao.execute("update PAYBUDGET set PB_REACHRATE=100 where pb_id=" + pb_id + " and nvl(PB_REACHRATE,0)>100");
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, pb_id);
		// 执行提交操作
		baseDao.updateByCondition(caller, "pb_statuscode='COMMITED',pb_status='" + BaseUtil.getLocalMessage("COMMITED") + "'", "pb_id="
				+ pb_id);
		// 记录操作
		baseDao.logger.submit(caller, "pb_id", pb_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, pb_id);
	}

	@Override
	public void resSubmitPayBudget(int pb_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("PayBudget", "pb_statuscode", "pb_id=" + pb_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resSubmit_onlyCommited"));
		}
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, pb_id);
		// 执行反提交操作
		baseDao.updateByCondition("PayBudget", "pb_statuscode='ENTERING',pb_status='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"pb_id=" + pb_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "pb_id", pb_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, pb_id);
	}

	@Override
	public void CalFKBudget(int yearmonth) {
		String res = baseDao.callProcedure("SP_CALFKBUDGET", new Object[] { yearmonth });
		Employee employee = SystemSession.getUser();
		if (res.equals("OK")) {
			baseDao.logMessage(new MessageLog(employee.getEm_name(), "刷新收款预算单", "刷新收款预算单", "刷新收款预算成功"));
		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public String[] printPayBudget(int pb_id, String reportName, String condition) {
		return null;
	}
}
