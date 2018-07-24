package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.Assert;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.scm.ComplaintRecordsService;

@Service
public class ComplaintRecordsServiceImpl implements ComplaintRecordsService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveComplaintRecords(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ComplaintRecords", "cr_code='" + store.get("cr_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store });
		// 保存ComplaintRecords
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ComplaintRecords", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.logger.save(caller, "cr_id", store.get("cr_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void deleteComplaintRecords(int cr_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("ComplaintRecords", "cr_statuscode", "cr_id=" + cr_id);
		Assert.isEquals("common.delete_onlyEntering", "ENTERING", status);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { cr_id});
		baseDao.deleteById("ComplaintRecords", "cr_id", cr_id);
		//记录日志
		baseDao.logger.delete(caller, "cr_id", cr_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { cr_id});
	}

	@Override
	public void updateComplaintRecordsById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("ComplaintRecords", "cr_statuscode", "cr_id=" + store.get("cr_id"));
		Assert.isEquals("common.update_onlyEntering", "ENTERING", status);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ComplaintRecords", "cr_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "cr_id", store.get("cr_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void auditComplaintRecords(int cr_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ComplaintRecords", "cr_statuscode", "cr_id=" + cr_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, cr_id);
		// 执行审核操作
		baseDao.audit("ComplaintRecords", "cr_id=" + cr_id, "cr_status", "cr_statuscode", "cr_auditdate", "cr_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "cr_id", cr_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, cr_id);
	}

	@Override
	public void resAuditComplaintRecords(int cr_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ComplaintRecords", "cr_statuscode", "cr_id=" + cr_id);
		Assert.isEquals("common.resAudit_onlyAudit", "AUDITED", status);
		// 执行反审核操作
		baseDao.resOperate("ComplaintRecords", "cr_id=" + cr_id, "cr_status", "cr_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "cr_id", cr_id);
	}

	@Override
	public void submitComplaintRecords(int cr_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ComplaintRecords", "cr_statuscode", "cr_id=" + cr_id);
		Assert.isEquals("common.submit_onlyEntering", "ENTERING", status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] {cr_id});
		// 执行提交操作
		baseDao.submit("ComplaintRecords", "cr_id=" + cr_id, "cr_status", "cr_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "cr_id", cr_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] {cr_id});
	}

	@Override
	public void resSubmitComplaintRecords(int cr_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ComplaintRecords", "cr_statuscode", "cr_id=" + cr_id);
		Assert.isEquals("common.resSubmit_onlyCommited", "COMMITED", status);
		handlerService.handler(caller, "resCommit", "before", new Object[] {cr_id});
		// 执行反提交操作
		baseDao.resOperate("ComplaintRecords", "cr_id=" + cr_id, "cr_status", "cr_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "cr_id", cr_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] {cr_id});
	}
	@Override
	public void endComplaintRecords(int cr_id, String caller) {// 结案
		Object status = baseDao.getFieldDataByCondition("ComplaintRecords", "cr_statuscode", "cr_id=" + cr_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.end_onlyAudited"));
		}
		// 更新状态
		baseDao.updateByCondition("ComplaintRecords", "cr_statuscode='FINISH',cr_status='" + BaseUtil.getLocalMessage("FINISH") + "'",
				"cr_id=" + cr_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.end"), BaseUtil
				.getLocalMessage("msg.endSuccess"), "ComplaintRecords|cr_id=" + cr_id));
	}

	@Override
	public void resEndComplaintRecords(int cr_id, String caller) {// 反结案
		// 只能对状态为[已结案]的订单进行反结案操作!
		Object status = baseDao.getFieldDataByCondition("ComplaintRecords", "cr_statuscode", "cr_id=" + cr_id);
		if (!status.equals("FINISH")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resEnd_onlyEnd"));
		}
		// 更新状态
		baseDao.updateByCondition("ComplaintRecords", "cr_statuscode='AUDITED',cr_status='" + BaseUtil.getLocalMessage("AUDITED") + "'",
				"cr_id=" + cr_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.resEnd"), BaseUtil
				.getLocalMessage("msg.resEndSuccess"), "ComplaintRecords|cr_id=" + cr_id));
	}
	@Override
	public void updateComplaint(int cr_id, String val1, String val2, String val3, String val4,String val0, String caller) {
		baseDao.updateByCondition("ComplaintRecords", "cr_result='" + val1 + "', cr_dutyman = '" + val2 +"', cr_dutydepartment = '" + val3 + "',cr_content='"+val4+"',cr_improve='"+val0+"'", "cr_id=" + cr_id);
		// 记录操作
		baseDao.logger.others("修改PMC回复日期", "msg.updateSuccess", caller, "cr_id", cr_id);
	}

	@Override
	public String[] printComplaintRecords(int cr_id, String reportName, String condition, String caller) {
		//执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[]{cr_id});
		//执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		//记录操作
		baseDao.logger.print(caller, "cr_id", cr_id);
		//执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[]{cr_id});
		return keys;
	}
}
