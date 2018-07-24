package com.uas.erp.service.plm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.service.plm.ProjectCostService;

@Service
public class ProjectCostServiceImpl implements ProjectCostService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private VoucherDao voucherDao;

	@Autowired
	private HandlerService handlerService;

	public void saveProjectCost(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ProjectCost", "pc_code='" + store.get("pc_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ProjectCost", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.logger.save(caller, "pc_id", store.get("pc_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void deleteProjectCost(int pc_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { pc_id });
		// 删除ProjectCost
		baseDao.deleteById("ProjectCost", "pc_id", pc_id);
		// 记录操作
		baseDao.logger.delete(caller, "pc_id", pc_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { pc_id });
	}

	@Override
	public void updateProjectCostById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProjectCost", "pc_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "pc_id", store.get("pc_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void auditProjectCost(int pc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ProjectCost", "pc_statuscode", "pc_id=" + pc_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { pc_id });
		// 执行审核操作
		baseDao.audit("ProjectColor", "pc_id=" + pc_id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.audit(caller, "pc_id", pc_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { pc_id });
	}

	@Override
	public void resAuditProjectCost(int pc_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ProjectCost", "pc_statuscode", "pc_id=" + pc_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("ProjectColor", "pc_id=" + pc_id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "pc_id", pc_id);
	}

	@Override
	public void submitProjectCost(int pc_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ProjectCost", "pc_statuscode", "pc_id=" + pc_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { pc_id });
		// 执行提交操作
		baseDao.submit("ProjectColor", "pc_id=" + pc_id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pc_id", pc_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { pc_id });
	}

	@Override
	public void resSubmitProjectCost(int pc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ProjectCost", "pc_statuscode", "pc_id=" + pc_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		baseDao.resOperate("ProjectColor", "pc_id=" + pc_id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pc_id", pc_id);
	}

	@Override
	public void startAccount() {
		Map<String, Object> map = voucherDao.getJustPeriods("MONTH-O");
		int yearmonth = Integer.parseInt(map.get("PD_DETNO").toString());
		String res = baseDao.callProcedure("SP_COUNTPROJECTCOST",
				new Object[] { yearmonth, String.valueOf(SystemSession.getUser().getEm_name()) });
		if (res.equals("OK")) {
			baseDao.logger.others("项目成本计算", "项目成本计算成功", "AccountProject", "id", yearmonth);
		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public void getSharedCosts(Integer param) {
		Map<String, Object> map = voucherDao.getJustPeriods("MONTH-O");
		int yearmonth = Integer.parseInt(map.get("PD_DETNO").toString());
		String res = baseDao.callProcedure("SP_GETSHAREFEE", new Object[] { yearmonth });
		if (res.equals("OK")) {
			baseDao.logger.others("获取公摊费用", "获取公摊费用成功", "GetSharedCosts", "id", yearmonth);
		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public void sharedCount(Integer param) {
		Map<String, Object> map = voucherDao.getJustPeriods("MONTH-O");
		int yearmonth = Integer.parseInt(map.get("PD_DETNO").toString());
		String res = baseDao.callProcedure("SP_CACSHAREFEE", new Object[] { yearmonth });
		if (res.equals("OK")) {
			baseDao.logger.others("公摊费用计算", "公摊费用计算成功", "SharedCount", "id", yearmonth);
		} else {
			BaseUtil.showError(res);
		}
	}
}
