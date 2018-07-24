package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.scm.BigLineApplyService;

@Service
public class BigLineApplyServiceImpl implements BigLineApplyService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveBigLineApply(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存BigLineApply
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BigLineApply",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "ba_id", store.get("ba_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });

	}

	@Override
	public void deleteBigLineApply(int ba_id, String caller) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("BigLineApply",
				"ba_statuscode", "ba_id=" + ba_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { ba_id });
		// 删除BigLineApply
		baseDao.deleteById("BigLineApply", "ba_id", ba_id);
		// 记录操作
		baseDao.logger.delete(caller, "ba_id", ba_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ba_id);

	}

	@Override
	public void updateBigLineApplyById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("BigLineApply",
				"ba_statuscode", "ba_id=" + store.get("ba_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.update_onlyEntering"));
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改BigLineApply
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BigLineApply",
				"ba_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "ba_id", store.get("ba_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });

	}

	@Override
	public void submitBigLineApply(int ba_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("BigLineApply",
				"ba_statuscode", "ba_id=" + ba_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.submit_onlyEntering"));
		}
		Object[] datass = baseDao.getFieldsDataByCondition(
				"BigLineApply", new String[] { "ba_emcode","ba_brand" },
				"ba_id=" + ba_id);
		Object count1 = baseDao
				.getFieldDataByCondition(
						"LineApply",
						"count(1)",
						"la_emcode='"+datass[0]+"' and la_brand='"+datass[1]+"'");
		if (Integer.parseInt(count1 + "") >=1) {
			BaseUtil.showError("您目前已经在PM产品线中申请了该品牌，不能提交！");
		}
		Object[] data = baseDao
				.getFieldsDataByCondition(
						"BigLineApply",
						new String[] { "ba_id", "ba_code", "ba_brand" },
						"ba_statuscode<>'ENTERING' AND ba_brand=(select ba_brand from biglineapply where ba_id="
								+ ba_id + " )");
		if (data != null) {
			BaseUtil.showError("当前品牌["
					+ data[2]
					+ "]已有人申请，不能提交！单号为："
					+ "<a href=\"javascript:openUrl('jsps/scm/sale/bigLineApply.jsp?formCondition=ba_idIS"
					+ data[0] + "')\">" + data[1] + "</a>");
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ba_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"BigLineApply",
				"ba_statuscode='COMMITED',ba_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'",
				"ba_id=" + ba_id);
		// 记录操作
		baseDao.logger.submit(caller, "ba_id", ba_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ba_id);

	}

	@Override
	public void resSubmitBigLineApply(int ba_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("BigLineApply",
				"ba_statuscode", "ba_id=" + ba_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.resSubmit_onlyCommited"));
		}
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, ba_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"BigLineApply",
				"ba_statuscode='ENTERING',ba_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'",
				"ba_id=" + ba_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ba_id", ba_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, ba_id);

	}

	@Override
	public void auditBigLineApply(int ba_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("BigLineApply",
				"ba_statuscode", "ba_id=" + ba_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.audit_onlyCommited"));
		}
		Employee employee = SystemSession.getUser();
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ba_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"BigLineApply",
				"ba_statuscode='AUDITED',ba_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',ba_auditer='" + employee.getEm_name()
						+ "',ba_auditdate=sysdate", "ba_id=" + ba_id);
		// 记录操作
		baseDao.logger.audit(caller, "ba_id", ba_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ba_id);

	}

	@Override
	public void resAuditBigLineApply(int ba_id, String caller) {
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, ba_id);
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("BigLineApply",
				"ba_statuscode", "ba_id=" + ba_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.resAudit_onlyAudit"));
		}
		// 执行反审核操作
		baseDao.updateByCondition(
				"BigLineApply",
				"ba_statuscode='ENTERING',ba_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',ba_auditer='',ba_auditdate=null", "ba_id=" + ba_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ba_id", ba_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, ba_id);

	}

}
