package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hsqldb.lib.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.fa.CustomerCreditReportService;

@Service("CustomerCreditReportService")
public class CustomerCreditReportServiceImpl implements CustomerCreditReportService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCustomerCreditReport(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		baseDao.logger.save(caller, "ccr_id", store.get("ccr_id"));
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByMap(store, "CustomerCreditReport");
		baseDao.execute(formSql);
		// 更新状态字段，上传报告时间
		String ccr_attach = store.get("ccr_attach").toString();
		String ccr_recorddate = store.get("ccr_recorddate").toString();
		if (!StringUtil.isEmpty(ccr_attach)) {
			baseDao.execute("update CustomerCreditReport set ccr_creditreportstatus='" + BaseUtil.getLocalMessage("UPLOADED")
					+ "', ccr_creditreportstatuscode='UPLOADED' WHERE ccr_custcode='" + store.get("ccr_custcode") + "'");

			baseDao.execute("update Customer set cu_creditreportstatus='" + BaseUtil.getLocalMessage("UPLOADED")
					+ "', cu_creditreportstatuscode='UPLOADED' WHERE cu_code='" + store.get("ccr_custcode") + "'");

			baseDao.execute("update CreditReportService set crs_reportpostdate=" + DateUtil.parseDateToOracleString(null, ccr_recorddate)
					+ " where crs_code='" + store.get("ccr_crscode") + "'");

			// 发寻呼
			SqlRowList rs = baseDao
					.queryForRowSet("select em_id,em_name from CreditReportService left join CustomerCreditReport on crs_custcode=ccr_custcode left join employee on em_name=crs_applicants where ccr_custcode='"
							+ store.get("ccr_custcode") + "'");
			while (rs.next()) {
				if (rs.getInt("em_id") > 0) {
					List<String> sqls = new ArrayList<String>();
					int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
					int prd_id = baseDao.getSeqId("PAGINGRELEASEDETAIL_SEQ");
					sqls.add("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context,pr_from)values('" + pr_id + "','"
							+ SystemSession.getUser().getEm_name() + "'," + DateUtil.parseDateToOracleString(Constant.YMD, new Date())
							+ ",'" + SystemSession.getUser().getEm_id() + "','信用申请编号：" + store.get("ccr_crscode") + "的客户:"
							+ store.get("ccr_custname") + " 的信用报告已经上传" + "','crm')");
					sqls.add("insert into pagingreleasedetail(prd_id,prd_prid,PRD_RECIPIENTID,PRD_RECIPIENT) values('" + prd_id + "','"
							+ pr_id + "','" + rs.getString("em_id") + "','" + rs.getString("em_name") + "')");
					baseDao.execute(sqls);
					//保存到历史消息表
					int IH_ID=baseDao.getSeqId("ICQHISTORY_SEQ");
					baseDao.execute("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
							+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
							+ " where pr_id="+pr_id);
					baseDao.execute("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
							+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+pr_id+"and ("+IH_ID+",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");					
				}
			}

		}
		// 记录操作
		baseDao.logger.save(caller, "ccr_id", store.get("ccr_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void updateCustomerCreditReport(String formStore, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object[] status = baseDao.getFieldsDataByCondition("CustomerCreditReport", new String[] { "ccr_statuscode" },
				"ccr_id=" + store.get("ccr_id"));
		StateAssert.updateOnlyEntering(status[0]);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 执行修改操作
		String sql = SqlUtil.getUpdateSqlByFormStore(store, "CustomerCreditReport", "ccr_id");
		baseDao.execute(sql);
		String ccr_attach = store.get("ccr_attach").toString();
		String ccr_recorddate = store.get("ccr_recorddate").toString();
		if (!StringUtil.isEmpty(ccr_attach)) {
			baseDao.execute("update CustomerCreditReport set ccr_creditreportstatus='" + BaseUtil.getLocalMessage("UPLOADED")
					+ "', ccr_creditreportstatuscode='UPLOADED' WHERE ccr_custcode='" + store.get("ccr_custcode") + "'");

			baseDao.execute("update Customer set cu_creditreportstatus='" + BaseUtil.getLocalMessage("UPLOADED")
					+ "', cu_creditreportstatuscode='UPLOADED' WHERE cu_code='" + store.get("ccr_custcode") + "'");

			baseDao.execute("update CreditReportService set crs_reportpostdate=" + DateUtil.parseDateToOracleString(null, ccr_recorddate)
					+ " where crs_code='" + store.get("ccr_crscode") + "'");

		}
		// 记录操作
		baseDao.logger.update(caller, "dgl_id", store.get("dgl_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void deleteCustomerCreditReport(int ccr_id, String caller) {
		// 执行提交前的其它逻辑
		handlerService.beforeDel(caller, ccr_id);
		// 执行删除操作
		baseDao.deleteById("CustomerCreditReport", "ccr_id", ccr_id);
		// 记录操作
		baseDao.logger.delete(caller, "ccr_id", ccr_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ccr_id);
	}

	@Override
	public void auditCustomerCreditReport(int ccr_id, String caller) {
		// 只能审核[已提交]的客户
		Object[] status = baseDao.getFieldsDataByCondition("CustomerCreditReport", new String[] { "ccr_statuscode" }, "ccr_id=" + ccr_id);
		if (!status[0].equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.customer.audit_uncommit"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ccr_id);
		baseDao.updateByCondition("CustomerCreditReport", "ccr_statuscode='AUDITED', ccr_status='" + BaseUtil.getLocalMessage("AUDITED")
				+ "'", "ccr_id=" + ccr_id);
		// 记录操作
		baseDao.logger.audit(caller, "ccr_id", ccr_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ccr_id);
	}

	@Override
	public void resAuditCustomerCreditReport(int ccr_id, String caller) {
		// 只能反审核[已审核]的客户
		Object status = baseDao.getFieldDataByCondition("CustomerCreditReport", "ccr_statuscode", "ccr_id=" + ccr_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.customer.resaudit_onlyAudited"));
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, ccr_id);
		// 执行反审核操作
		baseDao.updateByCondition("CustomerCreditReport", "ccr_statuscode='ENTERING', ccr_status='" + BaseUtil.getLocalMessage("ENTERING")
				+ "'", "ccr_id=" + ccr_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ccr_id", ccr_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, ccr_id);
	}

	@Override
	public void submitCustomerCreditReport(int ccr_id, String caller) {
		// 只能提交[在录入]的资料
		Object[] status = baseDao.getFieldsDataByCondition("CustomerCreditReport", new String[] { "ccr_statuscode", "ccr_custcode" },
				"ccr_id=" + ccr_id);
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.customer.submit_onlyEntering"));
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ccr_id);
		// 执行提交操作
		baseDao.updateByCondition("CustomerCreditReport", "ccr_statuscode='COMMITED', ccr_status='" + BaseUtil.getLocalMessage("COMMITED")
				+ "'", "ccr_id=" + ccr_id);
		// 记录操作
		baseDao.logger.submit(caller, "ccr_id", ccr_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ccr_id);
	}

	@Override
	public void resSubmitCustomerCreditReport(int ccr_id, String caller) {
		// 只能对状态为[已提交]的合同进行反提交操作
		Object status = baseDao.getFieldDataByCondition("CustomerCreditReport", "ccr_statuscode", "ccr_id=" + ccr_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.customer.ressubmit_onlyCommited"));
		}
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, ccr_id);
		// 执行反提交操作
		baseDao.updateByCondition("CustomerCreditReport", "ccr_statuscode='ENTERING', ccr_status='" + BaseUtil.getLocalMessage("ENTERING")
				+ "'", "ccr_id=" + ccr_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ccr_id", ccr_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, ccr_id);
	}

}
