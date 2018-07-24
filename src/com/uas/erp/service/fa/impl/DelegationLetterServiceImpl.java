package com.uas.erp.service.fa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.fa.DelegationLetterService;

@Service("DelegationLetterService")
public class DelegationLetterServiceImpl implements DelegationLetterService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveDelegationLetter(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String dgl_code = store.get("dgl_code").toString();
		String dgl_sellercode = store.get("dgl_sellercode").toString();
		String dgl_receivecustcode = store.get("dgl_receivecustcode").toString();
		// 该业务员尚存在未收款或者部分收款的收款委托书
		boolean bool = baseDao
				.checkByCondition("DelegationLetter", "dgl_sellercode='" + dgl_sellercode + "' and dgl_receivecustcode='"
						+ dgl_receivecustcode
						+ "' and dgl_receivestatuscode in ('UNCOLLECT','PARTCOLLECT') and nvl(dgl_statuscode,' ')<>'FINISH' ");
		if (!bool) {
			BaseUtil.showError("该业务员尚存在未收款或者部分收款的收款委托书");
		}
		// 当前编号的记录已经存在,不能新增!
		boolean bool1 = baseDao.checkByCondition("DelegationLetter", "dgl_code='" + dgl_code + "' ");
		if (!bool1) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByMap(store, "DelegationLetter");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "dgl_id", store.get("dgl_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void updateDelegationLetter(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object[] status = baseDao.getFieldsDataByCondition("DelegationLetter", new String[] { "dgl_statuscode", "dgl_sellercode",
				"dgl_receivecustcode" }, "dgl_id=" + store.get("dgl_id"));
		StateAssert.updateOnlyEntering(status[0]);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 执行修改操作
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "DelegationLetter", "dgl_id"));
		// 记录操作
		baseDao.logger.update(caller, "dgl_id", store.get("dgl_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void deleteDelegationLetter(int dgl_id, String caller) {
		// 只能删除[在录入]的客户资料
		Object status = baseDao.getFieldDataByCondition("DelegationLetter", "dgl_statuscode", "dgl_id=" + dgl_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { dgl_id });
		// 执行删除操作
		baseDao.deleteById("DelegationLetter", "dgl_id", dgl_id);
		// 记录操作
		baseDao.logger.delete(caller, "dgl_id", dgl_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, dgl_id);
	}

	@Override
	public void submitDelegationLetter(int dgl_id, String caller) {
		// 只能提交[在录入]的资料
		Object[] status = baseDao.getFieldsDataByCondition("DelegationLetter", new String[] { "dgl_statuscode", "dgl_sellercode",
				"dgl_receivecustcode" }, "dgl_id=" + dgl_id);
		StateAssert.submitOnlyEntering(status[0]);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, dgl_id);
		// 该业务员尚存在未收款或者部分收款的收款委托书
		int count = baseDao.getCount("select count(*) from DelegationLetter where dgl_sellercode='" + status[1]
				+ "' and dgl_receivecustcode='" + status[2]
				+ "' and dgl_receivestatuscode in ('UNCOLLECT','PARTCOLLECT') and nvl(dgl_statuscode,' ')<>'FINISH' ");
		if (count > 1) {
			BaseUtil.showError("该业务员尚存在该客户的未收款或者部分收款的收款委托书");
		}
		// 执行提交操作
		baseDao.updateByCondition("DelegationLetter", "dgl_statuscode='COMMITED', dgl_status='" + BaseUtil.getLocalMessage("COMMITED")
				+ "'", "dgl_id=" + dgl_id);
		// 记录操作
		baseDao.logger.submit("DelegationLetter", "dgl_id", dgl_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, dgl_id);
	}

	@Override
	public void resSubmitDelegationLetter(int dgl_id, String caller) {
		// 只能对状态为[已提交]的进行反提交操作
		Object status = baseDao.getFieldDataByCondition("DelegationLetter", "dgl_statuscode", "dgl_id=" + dgl_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, dgl_id);
		baseDao.updateByCondition("DelegationLetter", "dgl_statuscode='ENTERING', dgl_status='" + BaseUtil.getLocalMessage("ENTERING")
				+ "'", "dgl_id=" + dgl_id);
		// 记录操作
		baseDao.logger.resSubmit("DelegationLetter", "dgl_id", dgl_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, dgl_id);
	}

	@Override
	public void auditDelegationLetter(int dgl_id, String caller) {
		// 只能审核[已提交]
		Object status = baseDao.getFieldDataByCondition("DelegationLetter", "dgl_statuscode", "dgl_id=" + dgl_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, dgl_id);
		baseDao.audit("DelegationLetter", "dgl_id=" + dgl_id, "dgl_status", "dgl_statuscode","dgl_auditdate","dgl_auditman");
		// 记录操作
		baseDao.logger.audit("DelegationLetter", "dgl_id", dgl_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, dgl_id);
	}

	@Override
	public void resAuditDelegationLetter(int dgl_id, String caller) {
		// 只能反审核[已审核]
		Object[] status = baseDao.getFieldsDataByCondition("DelegationLetter", new String[] { "dgl_statuscode", "dgl_receivestatuscode" },
				"dgl_id=" + dgl_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		if (!status[1].equals("UNCOLLECT")) {
			BaseUtil.showError("该收款委托书已收到账款，请勿反审核");
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, dgl_id);
		// 执行反审核操作
		baseDao.resAudit("DelegationLetter", "dgl_id=" + dgl_id, "dgl_status", "dgl_statuscode", "dgl_auditman", "dgl_auditdate");
		// 记录操作
		baseDao.logger.resAudit("DelegationLetter", "dgl_id", dgl_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, dgl_id);
	}

	@Override
	public String[] printReceiptDelegationLetter(int dgl_id, String caller, String reportName, String condition) {
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, dgl_id);
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "dgl_id", dgl_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, dgl_id);
		return keys;
	}

	@Override
	public void endDelegationLetter(int dgl_id,String endreason, String caller) {
		// 只能反审核[已审核]
		Object[] status = baseDao.getFieldsDataByCondition("DelegationLetter", new String[] { "dgl_statuscode", "dgl_receivestatuscode" },
				"dgl_id=" + dgl_id);
		if (!status[0].equals("AUDITED")) {
			BaseUtil.showError("只有已审核的收款委托书才能结案");
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, dgl_id);
		// 执行反审核操作
		baseDao.updateByCondition("DelegationLetter", "dgl_endreason='"+endreason+"',dgl_statuscode='FINISH', dgl_status='" + BaseUtil.getLocalMessage("FINISH") + "'",
				"dgl_id=" + dgl_id);
		// 记录操作
		baseDao.logger.others("msg.end", "msg.endSuccess", caller, "dgl_id", dgl_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, dgl_id);
	}

	@Override
	public void resEndDelegationLetter(int dgl_id, String caller) {
		// 只能反审核[已审核]
		Object[] status = baseDao.getFieldsDataByCondition("DelegationLetter", new String[] { "dgl_statuscode", "dgl_receivestatuscode" },
				"dgl_id=" + dgl_id);
		if (!status[0].equals("FINISH")) {
			BaseUtil.showError("只有已结案的收款委托书才能反结案");
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, dgl_id);
		// 执行反审核操作
		baseDao.updateByCondition("DelegationLetter", "dgl_endreason='',dgl_statuscode='AUDITED', dgl_status='" + BaseUtil.getLocalMessage("AUDITED") + "'",
				"dgl_id=" + dgl_id);
		// 记录操作
		baseDao.logger.others("msg.resEnd", "msg.resEndSuccess", caller, "dgl_id", dgl_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, dgl_id);
	}

}
