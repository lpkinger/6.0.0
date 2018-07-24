package com.uas.erp.service.hr.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.ExamQuestService;

@Service
public class ExamQuestServiceImpl implements ExamQuestService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveExamQuest(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存ExamQuest
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ExamQuest",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "eq_id", store.get("eq_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteExamQuest(int eq_id, String caller) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("ExamQuest",
				"eq_statuscode", "eq_id=" + eq_id);
		StateAssert.delOnlyEntering(status);
		baseDao.delCheck("ExamQuest", eq_id);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { eq_id });
		// 删除ExamQuest
		baseDao.deleteById("ExamQuest", "eq_id", eq_id);
		// 记录操作
		baseDao.logger.delete(caller, "eq_id", eq_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { eq_id });

	}

	@Override
	public void updateExamQuestById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("ExamQuest",
				"eq_statuscode", "eq_id=" + store.get("eq_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改ExamQuest
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ExamQuest",
				"eq_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "eq_id", store.get("eq_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });

	}

	@Override
	public void submitExamQuest(int eq_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ExamQuest",
				"eq_statuscode", "eq_id=" + eq_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { eq_id });
		// 执行提交操作
		baseDao.submit("ExamQuest", "eq_id=" + eq_id, "eq_status",
				"eq_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "eq_id", eq_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { eq_id });

	}

	@Override
	public void resSubmitExamQuest(int eq_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ExamQuest",
				"eq_statuscode", "eq_id=" + eq_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { eq_id });
		// 执行反提交操作
		baseDao.resOperate("ExamQuest", "eq_id=" + eq_id, "eq_status",
				"eq_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "eq_id", eq_id);
		handlerService.afterResSubmit(caller, new Object[] { eq_id });

	}

	@Override
	public void auditExamQuest(int eq_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ExamQuest",
				"eq_statuscode", "eq_id=" + eq_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { eq_id });
		// 执行审核操作
		baseDao.audit("ExamQuest", "eq_id=" + eq_id, "eq_status",
				"eq_statuscode", "eq_auditdate", "eq_auditer");
		// 记录操作
		baseDao.logger.audit(caller, "eq_id", eq_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { eq_id });

	}

	@Override
	public void resAuditExamQuest(int eq_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ExamQuest",
				"eq_statuscode", "eq_id=" + eq_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.updateByCondition(
				"ExamQuest",
				"eq_statuscode='ENTERING',eq_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',eq_auditer='',eq_auditdate=null", "eq_id=" + eq_id);
		// 记录操作
		baseDao.logger.audit(caller, "eq_id", eq_id);

	}

}
