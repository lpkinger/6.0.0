package com.uas.erp.service.cost.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.cost.ProdIOCateSetService;

@Service("prodIOCateSetService")
public class ProdIOCateSetServiceImpl implements ProdIOCateSetService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProdIOCateSet(String formStore, String language, Employee employee) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ProdIOCateSet", "pc_code='" + store.get("pc_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist", language));
		}
		// 执行保存前的其它逻辑
		handlerService.handler("ProdIOCateSet", "save", "before", new Object[] { formStore, language });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ProdIOCateSet", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.save", language), BaseUtil
					.getLocalMessage("msg.saveSuccess", language), "ProdIOCateSet|pc_id=" + store.get("pc_id")));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 执行保存后的其它逻辑
		handlerService.handler("ProdIOCateSet", "save", "after", new Object[] { formStore, language, employee });
	}

	@Override
	public void deleteProdIOCateSet(int pc_id, String language, Employee employee) {
		// 执行删除前的其它逻辑
		handlerService.handler("ProdIOCateSet", "delete", "before", new Object[] { pc_id, language, employee });
		boolean hasUsed = baseDao
				.checkIf(
						"ProdIOCateSet",
						"pc_id="
								+ pc_id
								+ " and (pc_class,pc_type,pc_departmentcode) in (select pi_class,pi_type,pi_departmentcode from prodinout where pi_class in ('其它入库单','其它出库单'))");
		if (hasUsed) {
			BaseUtil.showError("该类型有关联其它出入库单据，不允许删除!");
		}
		// 删除ProdIOCateSet
		baseDao.deleteById("ProdIOCateSet", "pc_id", pc_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.delete", language), BaseUtil
				.getLocalMessage("msg.deleteSuccess", language), "ProdIOCateSet|pc_id=" + pc_id));
		// 执行删除后的其它逻辑
		handlerService.handler("ProdIOCateSet", "delete", "after", new Object[] { pc_id, language, employee });
	}

	@Override
	public void updateProdIOCateSetById(String formStore, String language, Employee employee) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.handler("ProdIOCateSet", "save", "before", new Object[] { formStore, language, employee });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProdIOCateSet", "pc_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.update", language), BaseUtil
				.getLocalMessage("msg.updateSuccess", language), "ProdIOCateSet|pc_id=" + store.get("pc_id")));
		// 执行修改后的其它逻辑
		handlerService.handler("ProdIOCateSet", "save", "after", new Object[] { formStore, language, employee });
	}

	@Override
	public void auditProdIOCateSet(int pc_id, String language, Employee employee) {
		// 只能对状态为[已提交]的单据进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ProdIOCateSet", "pc_statuscode", "pc_id=" + pc_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.audit_onlyCommited", language));
		}
		// 执行审核前的其它逻辑
		handlerService.handler("ProdIOCateSet", "audit", "before", new Object[] { pc_id, language, employee });
		// 执行审核操作
		baseDao.updateByCondition("ProdIOCateSet", "pc_statuscode='AUDITED',pc_status='" + BaseUtil.getLocalMessage("AUDITED", language)
				+ "'", "pc_id=" + pc_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.audit", language), BaseUtil.getLocalMessage(
				"msg.auditSuccess", language), "ProdIOCateSet|pc_id=" + pc_id));
		// 执行审核后的其它逻辑
		handlerService.handler("ProdIOCateSet", "audit", "after", new Object[] { pc_id, language, employee });
	}

	@Override
	public void resAuditProdIOCateSet(int pc_id, String language, Employee employee) {
		// 只能对状态为[已审核]的单据进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ProdIOCateSet", "pc_statuscode", "pc_id=" + pc_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit", language));
		}
		//
		boolean hasUsed = baseDao
				.checkIf(
						"ProdIOCateSet",
						"pc_id="
								+ pc_id
								+ " and (pc_class,pc_type,pc_departmentcode) in (select pi_class,pi_type,pi_departmentcode from prodinout where pi_class in ('其它入库单','其它出库单'))");
		// 执行反审核操作
		baseDao.updateByCondition("ProdIOCateSet", "pc_statuscode='ENTERING',pc_status='" + BaseUtil.getLocalMessage("ENTERING", language)
				+ "'", "pc_id=" + pc_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.resAudit", language), BaseUtil
				.getLocalMessage("msg.resAuditSuccess", language), "ProdIOCateSet|pc_id=" + pc_id));
		if (hasUsed) {
			BaseUtil.appendError("该类型有关联其它出入库单据！");
		}
	}

	@Override
	public void submitProdIOCateSet(int pc_id, String language, Employee employee) {
		// 只能对状态为[在录入]的单据进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ProdIOCateSet", "pc_statuscode", "pc_id=" + pc_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.submit_onlyEntering", language));
		}
		// 执行提交前的其它逻辑
		handlerService.handler("ProdIOCateSet", "commit", "before", new Object[] { pc_id, language, employee });
		// 执行提交操作
		baseDao.updateByCondition("ProdIOCateSet", "pc_statuscode='COMMITED',pc_status='" + BaseUtil.getLocalMessage("COMMITED", language)
				+ "'", "pc_id=" + pc_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.submit", language), BaseUtil
				.getLocalMessage("msg.submitSuccess", language), "ProdIOCateSet|pc_id=" + pc_id));
		// 执行提交后的其它逻辑
		handlerService.handler("ProdIOCateSet", "commit", "after", new Object[] { pc_id, language, employee });
	}

	@Override
	public void resSubmitProdIOCateSet(int pc_id, String language, Employee employee) {
		// 只能对状态为[已提交]的单据进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ProdIOCateSet", "pc_statuscode", "pc_id=" + pc_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resSubmit_onlyCommited", language));
		}
		// 执行反提交操作
		baseDao.updateByCondition("ProdIOCateSet", "pc_statuscode='ENTERING',pc_status='" + BaseUtil.getLocalMessage("ENTERING", language)
				+ "'", "pc_id=" + pc_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.resSubmit", language), BaseUtil
				.getLocalMessage("msg.resSubmitSuccess", language), "ProdIOCateSet|pc_id=" + pc_id));
	}

	@Override
	public void bannedProdIOCateSet(int pc_id, String language, Employee employee) {
		// 执行禁用操作
		baseDao.updateByCondition("ProdIOCateSet", "pc_statuscode='DISABLE', pc_status='" + BaseUtil.getLocalMessage("DISABLE", language)
				+ "'", "pc_id=" + pc_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.banned", language), BaseUtil
				.getLocalMessage("msg.bannedSuccess", language), "pc_id=" + pc_id));

	}

	@Override
	public void resBannedProdIOCateSet(int pc_id, String language, Employee employee) {
		// 执行反禁用操作
		baseDao.updateByCondition("ProdIOCateSet", "pc_statuscode='ENTERING', pc_status='" + BaseUtil.getLocalMessage("ENTERING", language)
				+ "'", "pc_id=" + pc_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.unbanned", language), BaseUtil
				.getLocalMessage("msg.unbannedSuccess", language), "pc_id=" + pc_id));
	}
}