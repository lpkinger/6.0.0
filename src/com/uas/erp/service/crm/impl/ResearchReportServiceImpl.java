package com.uas.erp.service.crm.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.crm.ResearchReportService;
@Service
public class ResearchReportServiceImpl implements ResearchReportService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveResearchReport(String formStore, String gridStore,
			String language, Employee employee,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] {
				formStore, language });
		// 保存MarketTaskReport
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"MarketTaskReport", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存Contact
		for (Map<Object, Object> s : grid) {
			s.put("mrd_id", baseDao.getSeqId("MarketTaskReportDetail_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"MarketTaskReportDetail");
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
					.getLocalMessage("msg.save", language), BaseUtil
					.getLocalMessage("msg.saveSuccess", language),
					"MarketTaskReport|mr_id=" + store.get("mr_id")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] {
				formStore, language });
		
	}

	@Override
	public void deleteResearchReport(int mr_id, String language,
			Employee employee,String caller) {
		// 只能删除在录入的!
				Object status = baseDao.getFieldDataByCondition("MarketTaskReport",
						"mr_statuscode", "mr_id=" + mr_id);
				if (!status.equals("ENTERING")) {
					BaseUtil.showError(BaseUtil.getLocalMessage(
							"common.delete_onlyEntering", language));
				}
				// 执行删除前的其它逻辑
				handlerService.handler(caller, "delete", "before",
						new Object[] { mr_id, language, employee });
				// 删除MarketTaskReport
				baseDao.deleteById("MarketTaskReport", "mr_id", mr_id);
				// 删除Contact
				baseDao.deleteById("MarketTaskReportdetail", "mrd_mrid", mr_id);
				// 记录操作
				baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
						.getLocalMessage("msg.delete", language), BaseUtil
						.getLocalMessage("msg.deleteSuccess", language),
						"MarketTaskReport|mr_id=" + mr_id));
				// 执行删除后的其它逻辑
				handlerService.handler(caller, "delete", "after",
						new Object[] { mr_id, language, employee });
		
	}

	@Override
	public void updateResearchReportById(String formStore, String gridStore,
			String language, Employee employee,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("MarketTaskReport",
				"mr_statuscode", "mr_id=" + store.get("mr_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.update_onlyEntering", language));
		}
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] {
				store, language });
		// 修改MarketTaskReport
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"MarketTaskReport", "mr_id");
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"MarketTaskReportdetail", "mrd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("mrd_id") == null || s.get("mrd_id").equals("")
					|| s.get("mrd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("MarketTaskReportdetail_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s,
						"MarketTaskReportdetail", new String[] { "mrd_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
				.getLocalMessage("msg.update", language), BaseUtil
				.getLocalMessage("msg.updateSuccess", language),
				"MarketTaskReport|mr_id=" + store.get("mr_id")));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] {
				store, language });
		
	}

	@Override
	public void submitResearchReport(int mr_id, String language,
			Employee employee,String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
				Object status = baseDao.getFieldDataByCondition("MarketTaskReport",
						"mr_statuscode", "mr_id=" + mr_id);
				if (!status.equals("ENTERING")) {
					BaseUtil.showError(BaseUtil.getLocalMessage(
							"common.submit_onlyEntering", language));
				}
				// 执行提交前的其它逻辑
				handlerService.handler(caller, "commit", "before",
						new Object[] { mr_id, language, employee });
				// 执行提交操作
				baseDao.updateByCondition(
						"MarketTaskReport",
						"mr_statuscode='COMMITED',mr_status='"
								+ BaseUtil.getLocalMessage("COMMITED", language) + "'",
						"mr_id=" + mr_id);
				// 记录操作
				baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
						.getLocalMessage("msg.submit", language), BaseUtil
						.getLocalMessage("msg.submitSuccess", language),
						"MarketTaskReport|mr_id=" + mr_id));
				// 执行提交后的其它逻辑
				handlerService.handler(caller, "commit", "after",
						new Object[] { mr_id, language, employee });
		
	}

	@Override
	public void resSubmitResearchReport(int mr_id, String language,
			Employee employee,String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
				Object status = baseDao.getFieldDataByCondition("MarketTaskReport",
						"mr_statuscode", "mr_id=" + mr_id);
				if (!status.equals("COMMITED")) {
					BaseUtil.showError(BaseUtil.getLocalMessage(
							"common.resSubmit_onlyCommited", language));
				}
				handlerService.handler(caller, "resCommit", "before",
						new Object[] { mr_id, language, employee });
				// 执行反提交操作
				baseDao.updateByCondition(
						"MarketTaskReport",
						"mr_statuscode='ENTERING',mr_status='"
								+ BaseUtil.getLocalMessage("ENTERING", language) + "'",
						"mr_id=" + mr_id);
				// 记录操作
				baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
						.getLocalMessage("msg.resSubmit", language), BaseUtil
						.getLocalMessage("msg.resSubmitSuccess", language),
						"MarketTaskReport|mr_id=" + mr_id));
				handlerService.handler(caller, "resCommit", "after",
						new Object[] { mr_id, language, employee });
	}

	@Override
	public void auditResearchReport(int mr_id, String language,
			Employee employee,String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
				Object status = baseDao.getFieldDataByCondition("MarketTaskReport",
						"mr_statuscode", "mr_id=" + mr_id);
				if (!status.equals("COMMITED")) {
					BaseUtil.showError(BaseUtil.getLocalMessage(
							"common.audit_onlyCommited", language));
				}
				// 执行审核前的其它逻辑
				handlerService.handler(caller, "audit", "before",
						new Object[] { mr_id, language });
				// 执行审核操作
				baseDao.updateByCondition(
						"MarketTaskReport",
						"mr_statuscode='AUDITED',mr_status='"
								+ BaseUtil.getLocalMessage("AUDITED", language) + "'",
						"mr_id=" + mr_id);
				//任务已完成
				Object taskId=baseDao.getFieldDataByCondition("projecttask left join MarketTaskReport on taskcode=mr_taskcode", "id", "mr_id="+mr_id);
				baseDao.execute("update resourceassignment set ra_taskpercentdone=100,ra_status='已完成',ra_statuscode='FINISHED',ra_enddate="+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date())+" where ra_taskid="
						+ taskId);
				baseDao.execute("update ProjectTask set handstatus='已完成',handstatuscode='FINISHED',percentdone=100 where id="+taskId);
				// 记录操作
				baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
						.getLocalMessage("msg.audit", language), BaseUtil
						.getLocalMessage("msg.auditSuccess", language),
						"MarketTaskReport|mr_id=" + mr_id));
				// 执行审核后的其它逻辑
				handlerService.handler(caller, "audit", "after", new Object[] {
						mr_id, language });
	}

	@Override
	public void resAuditResearchReport(int mr_id, String language,
			Employee employee,String caller) {
		// 执行反审核前的其它逻辑
				handlerService.handler(caller, "resAudit", "before",
						new Object[] { mr_id, language, employee });
				// 只能对状态为[已审核]的订单进行反审核操作!
				Object status = baseDao.getFieldDataByCondition("MarketTaskReport",
						"mr_statuscode", "mr_id=" + mr_id);
				if (!status.equals("AUDITED")) {
					BaseUtil.showError(BaseUtil.getLocalMessage(
							"common.resAudit_onlyAudit", language));
				}
				// 执行反审核操作
				baseDao.updateByCondition(
						"MarketTaskReport",
						"mr_statuscode='ENTERING',mr_status='"
								+ BaseUtil.getLocalMessage("ENTERING", language) + "'",
						"mr_id=" + mr_id);
				Object taskId=baseDao.getFieldDataByCondition("projecttask left join MarketTaskReport on taskcode=mr_taskcode", "id", "mr_id="+mr_id);
				baseDao.execute("update resourceassignment set ra_taskpercentdone=0,ra_status='进行中',ra_statuscode='START',ra_enddate="+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date())+" where ra_taskid="
						+ taskId);
				baseDao.execute("update ProjectTask set handstatus='已启动',handstatuscode='DOING',percentdone=0 where id="+taskId);
				// 记录操作
				baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
						.getLocalMessage("msg.resAudit", language), BaseUtil
						.getLocalMessage("msg.resAuditSuccess", language),
						"MarketTaskReport|mr_id=" + mr_id));
				// 执行反审核后的其它逻辑
				handlerService.handler(caller, "resAudit", "after",
						new Object[] { mr_id, language, employee });
	}

	@Override
	@Transactional
	public String turnFeepleaseCLFBX(int mr_id, String language,
			Employee employee, String caller) {
		Object[]  data=baseDao.getFieldsDataByCondition("MarketTaskReport left join employee on mr_recorder=em_name left join projecttask on mr_taskcode=taskcode", 
				new String[]{"em_name","em_depart","mr_code","mr_tosob","mr_reportcode","id"}, "mr_id="+mr_id);//取原表单的录入人作为出差费用申请的申请人
		if(data[5]!=null && !data[5].equals("")){
			Object sob = baseDao.getFieldDataByCondition("master", "ma_user", "ma_function='"+data[3]+"'");
			if(sob == null){
				BaseUtil.showError("没有该帐套!请核对后重试!");
			}
			Object localSobName = baseDao.getFieldDataByCondition("master", "ma_function", "ma_user='"+SpObserver.getSp()+"'");
			if(localSobName==null){
				localSobName="";
			}
			Object[] feedata=baseDao.getFieldsDataByCondition(sob+"."+"FeePlease", new String[]{"fp_code","fp_id"}, "fp_sourcekind='市场调研立项' and fp_sourcecode='"+data[2]+"("+localSobName+")"+"'");
			if(feedata!=null){//如果feeplease中存在记录，则报错
				BaseUtil.showError("转入失败,此拜访记录已存在于"+data[3]+"的差旅费报销,单号为:" + feedata[0]);
			}
			int id=baseDao.getSeqId(sob+"."+"FeePlease_seq");
			String code=baseDao.callProcedure(sob + ".Sp_GetMaxNumber", new Object[] { "FeePlease!CLFBX", 2 });
			String insertSql="insert into "+sob +"."+"FeePlease(fp_code,fp_pleaseman,fp_department,fp_status,fp_recordman,fp_kind,fp_recorddate,fp_sourcecode,fp_sourcekind,fp_id,fp_statuscode,fp_v14)" +
					" values(?,?,?,?,?,?,sysdate,?,?,?,?,?)";
			baseDao.execute(insertSql, new Object[]{code,data[0],data[1],"在录入",employee.getEm_name(),"差旅费报销单",data[2]+"("+localSobName+")","市场调研立项",id,"ENTERING",data[4]+"#"+data[5]});
			String insertDetSql="insert into "+sob +"."+"FeePleasedetail (fpd_detno,fpd_d1,fpd_n7,fpd_n8,fpd_d3,fpd_id,fpd_fpid) " +
					"select mrd_detno,mrd_costname,mrd_used,mrd_used,mrd_remark,"+sob +"."+"FeePleasedetail_seq.nextval,"+id+" from MarketTaskReportDetail where mrd_mrid="+mr_id;
			baseDao.execute(insertDetSql);
			baseDao.execute("update "+sob +"."+"FeePlease set fp_startdate='',fp_enddate='' where fp_id="+id);
			baseDao.updateByCondition("MarketTaskReport", "mr_isturnfeeplease='是'", "mr_id="+mr_id);
			String log = "转入成功,"+data[3]+"的差旅费报销单号:" + code;
			return log;
		}else{
			Object[] feedata=baseDao.getFieldsDataByCondition("FeePlease", new String[]{"fp_code","fp_id"}, "fp_sourcekind='市场调研立项' and fp_sourcecode='"+data[2]+"'");
			if(feedata!=null){//如果feeplease中存在记录，则报错
				BaseUtil.showError("转入失败,此拜访记录已存在差旅费报销,单号为:" + "<a href=\"javascript:openUrl('jsps/oa/fee/feePlease.jsp?whoami=FeePlease!CLFBX&formCondition=fp_idIS"
						+ feedata[1] + "&gridCondition=fpd_fpidIS" + feedata[1] + "')\">" + feedata[0] + "</a>");
			}
			int id=baseDao.getSeqId("FeePlease_seq");
			String code=baseDao.sGetMaxNumber("FeePlease!CLFBX", 2);
			String insertSql="insert into FeePlease(fp_code,fp_pleaseman,fp_department,fp_status,fp_recordman,fp_kind,fp_recorddate,fp_sourcecode,fp_sourcekind,fp_id,fp_statuscode,fp_v14)" +
					" values(?,?,?,?,?,?,sysdate,?,?,?,?,?)";
			baseDao.execute(insertSql, new Object[]{code,data[0],data[1],"在录入",employee.getEm_name(),"差旅费报销单",data[2],"市场调研立项",id,"ENTERING",data[4]+"#"+data[5]});
			String insertDetSql="insert into FeePleasedetail (fpd_detno,fpd_d1,fpd_n7,fpd_n8,fpd_d3,fpd_id,fpd_fpid) " +
					"select mrd_detno,mrd_costname,mrd_used,mrd_used,mrd_remark,FeePleasedetail_seq.nextval,"+id+" from MarketTaskReportDetail where vrd_vrid="+mr_id;
			baseDao.execute(insertDetSql);
			baseDao.execute("update FeePlease set fp_startdate='',fp_enddate='' where fp_id="+id);
			baseDao.updateByCondition("MarketTaskReport", "mr_isturnfeeplease='是'", "mr_id="+mr_id);
			String log = "转入成功,差旅费报销单号:" + "<a href=\"javascript:openUrl('jsps/oa/fee/feePlease.jsp?whoami=FeePlease!CLFBX&formCondition=fp_idIS"
					+ id + "&gridCondition=fpd_fpidIS" + id + "')\">" + code + "</a>";
			return log;
		}
	}

}
