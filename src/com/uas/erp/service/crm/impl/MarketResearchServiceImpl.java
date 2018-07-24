package com.uas.erp.service.crm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.crm.MarketResearchService;

@Service
public class MarketResearchServiceImpl implements MarketResearchService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveMarketResearch(String formStore, String language,
			Employee employee) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.handler("MarketResearch", "save", "before",
				new Object[] { formStore, language });
		// 保存MarketResearch
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"MarketResearch", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
					.getLocalMessage("msg.save", language), BaseUtil
					.getLocalMessage("msg.saveSuccess", language),
					"MarketResearch|mr_id=" + store.get("mr_id")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.handler("MarketResearch", "save", "after", new Object[] {
				formStore, language });

	}

	@Override
	public void deleteMarketResearch(int mr_id, String language,
			Employee employee) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("MarketResearch",
				"mr_statuscode", "mr_id=" + mr_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.delete_onlyEntering", language));
		}
		// 执行删除前的其它逻辑
		handlerService.handler("MarketResearch", "delete", "before",
				new Object[] { mr_id, language, employee });
		// 删除MarketResearch
		baseDao.deleteById("MarketResearch", "mr_id", mr_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
				.getLocalMessage("msg.delete", language), BaseUtil
				.getLocalMessage("msg.deleteSuccess", language),
				"MarketResearch|mr_id=" + mr_id));
		// 执行删除后的其它逻辑
		handlerService.handler("MarketResearch", "delete", "after",
				new Object[] { mr_id, language, employee });

	}

	@Override
	public void updateMarketResearchById(String formStore, String language,
			Employee employee) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("MarketResearch",
				"mr_statuscode", "mr_id=" + store.get("mr_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.update_onlyEntering", language));
		}
		// 执行修改前的其它逻辑
		handlerService.handler("MarketResearch", "save", "before",
				new Object[] { store, language });
		// 修改MarketResearch
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"MarketResearch", "mr_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
				.getLocalMessage("msg.update", language), BaseUtil
				.getLocalMessage("msg.updateSuccess", language),
				"MarketResearch|mr_id=" + store.get("mr_id")));
		// 执行修改后的其它逻辑
		handlerService.handler("MarketResearch", "save", "after", new Object[] {
				store, language });

	}

	@Override
	public void submitMarketResearch(int mr_id, String language,
			Employee employee) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MarketResearch",
				"mr_statuscode", "mr_id=" + mr_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.submit_onlyEntering", language));
		}
		// 执行提交前的其它逻辑
		handlerService.handler("MarketResearch", "commit", "before",
				new Object[] { mr_id, language, employee });
		// 执行提交操作
		baseDao.updateByCondition(
				"MarketResearch",
				"mr_statuscode='COMMITED',mr_status='"
						+ BaseUtil.getLocalMessage("COMMITED", language) + "'",
				"mr_id=" + mr_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
				.getLocalMessage("msg.submit", language), BaseUtil
				.getLocalMessage("msg.submitSuccess", language),
				"MarketResearch|mr_id=" + mr_id));
		// 执行提交后的其它逻辑
		handlerService.handler("MarketResearch", "commit", "after",
				new Object[] { mr_id, language, employee });

	}

	@Override
	public void resSubmitMarketResearch(int mr_id, String language,
			Employee employee) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MarketResearch",
				"mr_statuscode", "mr_id=" + mr_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.resSubmit_onlyCommited", language));
		}
		handlerService.handler("MarketResearch", "resCommit", "before",
				new Object[] { mr_id, language, employee });
		// 执行反提交操作
		baseDao.updateByCondition(
				"MarketResearch",
				"mr_statuscode='ENTERING',mr_status='"
						+ BaseUtil.getLocalMessage("ENTERING", language) + "'",
				"mr_id=" + mr_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
				.getLocalMessage("msg.resSubmit", language), BaseUtil
				.getLocalMessage("msg.resSubmitSuccess", language),
				"MarketResearch|mr_id=" + mr_id));
		handlerService.handler("MarketResearch", "resCommit", "after",
				new Object[] { mr_id, language, employee });

	}

	@Override
	public void auditMarketResearch(int mr_id, String language,
			Employee employee) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("MarketResearch",
				"mr_statuscode", "mr_id=" + mr_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.audit_onlyCommited", language));
		}
		// 执行审核前的其它逻辑
		handlerService.handler("MarketResearch", "audit", "before",
				new Object[] { mr_id, language });
		// 执行审核操作
		baseDao.updateByCondition(
				"MarketResearch",
				"mr_statuscode='AUDITED',mr_status='"
						+ BaseUtil.getLocalMessage("AUDITED", language) + "'",
				"mr_id=" + mr_id);
		//发送寻呼
		Object[] data =baseDao.getFieldsDataByCondition("MarketResearch left join employee on mr_emname=em_name", new String[]{"em_id","em_name","mr_code"}, "mr_id="+mr_id);
		int pr_id = baseDao.getSeqId("pagingrelease_seq");
		StringBuffer sb = new StringBuffer();
		sb.append("提&nbsp;&nbsp;醒&nbsp;&nbsp;[");
		sb.append(DateUtil.parseDateToString(null, "MM-dd HH:mm"));//parseDateToString(null, "MM-dd HH:mm")
		sb.append("]</br>");
		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<a style=\"font-size:14px; color:blue;\" href=\"javascript:openUrl(''");		
		sb.append("jsps/crm/marketmgr/marketresearch/marketResearch.jsp?formCondition=mr_idIS");
		sb.append(mr_id);
		sb.append("'')\">");
		sb.append("市场调研(");
		sb.append(data[2]);
		sb.append(")</a>");
		baseDao.execute("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context,pr_from)values('" + pr_id
				+ "','" + employee.getEm_name() + "',sysdate,'" + employee.getEm_id() + "','" + sb.toString() + "','crm')");
		baseDao.execute("insert into pagingreleasedetail(prd_id,prd_prid,prd_recipientid,prd_recipient) values(pagingreleasedetail_seq.nextval"
				+ "," + pr_id + "," + data[0] + ",'" + data[1] + "')");
		//保存到历史消息表
		int IH_ID=baseDao.getSeqId("ICQHISTORY_SEQ");
		baseDao.execute("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
						+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
						+ " where pr_id="+pr_id);
		baseDao.execute("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
						+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+pr_id+"and ("+IH_ID+",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");

		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
				.getLocalMessage("msg.audit", language), BaseUtil
				.getLocalMessage("msg.auditSuccess", language),
				"MarketResearch|mr_id=" + mr_id));
		// 执行审核后的其它逻辑
		handlerService.handler("MarketResearch", "audit", "after",
				new Object[] { mr_id, language });

	}

	@Override
	public void resAuditMarketResearch(int mr_id, String language,
			Employee employee) {
		// 执行反审核前的其它逻辑
		handlerService.handler("MarketResearch", "resAudit", "before",
				new Object[] { mr_id, language, employee });
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("MarketResearch",
				"mr_statuscode", "mr_id=" + mr_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.resAudit_onlyAudit", language));
		}
		// 执行反审核操作
		baseDao.updateByCondition(
				"MarketResearch",
				"mr_statuscode='ENTERING',mr_status='"
						+ BaseUtil.getLocalMessage("ENTERING", language) + "'",
				"mr_id=" + mr_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
				.getLocalMessage("msg.resAudit", language), BaseUtil
				.getLocalMessage("msg.resAuditSuccess", language),
				"MarketResearch|mr_id=" + mr_id));
		// 执行反审核后的其它逻辑
		handlerService.handler("MarketResearch", "resAudit", "after",
				new Object[] { mr_id, language, employee });

	}

}
