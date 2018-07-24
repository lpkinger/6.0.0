package com.uas.erp.service.plm.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.plm.ProjectTGService;

@Service
public class ProjectTGServiceImpl implements ProjectTGService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void saveProjectTG(String formStore, String gridStore,  String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		boolean bool = baseDao.checkByCondition("Project", "prj_code='" + store.get("prj_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}

		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { formStore });
		if ("Project!TG".equals(caller)) {
			// 处理明细
			boolean hasOrganiger = false;
			boolean hasAssignto = false;

			for (Map<Object, Object> s : grid) {
				s.put("prd_id", baseDao.getSeqId("projectdet_SEQ"));

				if (store.get("prj_organiger").equals(s.get("prd_emname"))) {
					hasOrganiger = true;
				}
				if (store.get("prj_assignto").equals(s.get("prd_emname"))) {
					hasAssignto = true;
				}
			}
			// 如果明细中没有添加责任人和发起人，则添加进去
			if (!hasOrganiger) {
				Map<Object, Object> s = new HashMap<Object, Object>();
				s.put("prd_id", baseDao.getSeqId("projectdet_SEQ"));
				s.put("prd_prjid", store.get("prj_id"));
				s.put("prd_detno", grid.size() + 1);
				s.put("prd_emcode", baseDao.getFieldDataByCondition("Employee", "em_code", "em_name='" + store.get("prj_organiger") + "'"));
				s.put("prd_emname", store.get("prj_organiger"));
				grid.add(s);
			}
			if (!hasAssignto && !store.get("prj_organiger").equals(store.get("prj_assignto"))) {// 如果责任人和发起人只有一个，则添加一次就好
				Map<Object, Object> s = new HashMap<Object, Object>();
				s.put("prd_id", baseDao.getSeqId("projectdet_SEQ"));
				s.put("prd_prjid", store.get("prj_id"));
				s.put("prd_detno", grid.size() + 1);
				s.put("prd_emcode", baseDao.getFieldDataByCondition("Employee", "em_code", "em_name='" + store.get("prj_assignto") + "'"));
				s.put("prd_emname", store.get("prj_assignto"));
				grid.add(s);
			}
		}

		for (Map<Object, Object> map : grid) {
			map.put("prd_id", baseDao.getSeqId("projectdet_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "projectdet");
		// baseDao.execute(gridSql);
		String check = baseDao.executeWithCheck(gridSql, null,
				"select wm_concat(prd_emcode) from  projectdet where prd_prjid=" + store.get("prj_id")
						+ "  group  by  prd_emcode  having  count(prd_emcode) > 1");
		if (check != null) {
			BaseUtil.showError("明细行参与人编号重复");
		}
		
		// 保存Project
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Project", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		
		try {
			// 记录操作
			baseDao.logger.save(caller, "prj_id", store.get("prj_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { formStore});
	}

	@Override
	public void updateProjectTG(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("Project", "prj_statuscode", "prj_id=" + store.get("prj_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });


		// 修改Detail
		List<String> gridSql = SqlUtil.getInsertOrUpdateSql(gstore, "projectdet", "prd_id");
		String check = baseDao.executeWithCheck(gridSql, null,
				"select wm_concat(prd_emcode) from  projectdet where prd_prjid=" + store.get("prj_id")
						+ "  group  by  prd_emcode  having  count(prd_emcode) > 1");
		if (check != null) {
			BaseUtil.showError("明细行参与人编号重复");
		}
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "project", "prj_id");
		baseDao.execute(formSql);

		// 记录操作
		baseDao.logger.update(caller, "prj_id", store.get("prj_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
	}

	@Override
	public String copy(int prj_id) {
		int id = baseDao.getSeqId("Project_seq");
		String code = baseDao.sGetMaxNumber("Project!TG", 2);
		Employee employee = SystemSession.getUser();
		String sql = "insert into Project(prj_name,prj_others,prj_producttype,prj_organiger,prj_organigerdep,prj_assignto,prj_organigerdate,prj_start,prj_type,"
				+ "prj_code,prj_person,prj_recordate,prj_status,prj_id,prj_statuscode) select prj_name,prj_others,prj_producttype,prj_organiger,prj_organigerdep,prj_assignto,prj_organigerdate,prj_start,prj_type,"
				+ "'" + code + "','" + employee.getEm_name() + "'," + DateUtil.parseDateToOracleString(null, new Date())// BaseUtil.parseDateToOracleString(null,
																														// new
																														// Date())
				+ ",'在录入'," + id + ",'ENTERING' from Project where prj_id=" + prj_id;
		String detailSql = "insert into projectdet(prd_detno,prd_emcode,prd_emname,prd_id,prd_prjid) select prd_detno,prd_emcode,prd_emname,projectdet_seq.nextval,"
				+ id + " from projectdet where prd_prjid=" + prj_id;
		baseDao.execute(sql);
		baseDao.execute(detailSql);
		return "复制表单成功,新的单号为:" + "<a href=\"javascript:openUrl('jsps/plm/project/projectTG.jsp?formCondition=prj_idIS" + id
				+ "&gridCondition=prd_prjidIS" + id + "&whoami=Project!TG')\">" + code + "</a>&nbsp;<hr>";
	}

	@Override
	public void auditProjectTG(int prj_id,String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Project",
				"prj_statuscode", "prj_id=" + prj_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.audit_onlyCommited"));
		}
		Employee employee = SystemSession.getUser();
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] {
				prj_id });
		if ("Project!DY".equals(caller)) {
			
			// 有审核人和审核日期
			baseDao.updateByCondition(
					"Project",
					"prj_statuscode='AUDITED',prj_status='"
							+ BaseUtil.getLocalMessage("AUDITED")
							+ "',prj_text1='" + employee.getEm_name()
							+ "',prj_date1=sysdate", "prj_id=" + prj_id);
		}else {
			// 执行审核操作
			baseDao.updateByCondition(
					"Project",
					"prj_statuscode='AUDITED',prj_status='"
							+ BaseUtil.getLocalMessage("AUDITED")
							+ "',prj_text1='" + employee.getEm_name()
							+ "',prj_date1=sysdate", "prj_id=" + prj_id);
		}
		// 记录操作
		baseDao.logger.audit(caller, "prj_id", prj_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { prj_id });

	}

	@Override
	public void resAuditProjectTG(int prj_id,String caller) {
		// 执行反审核前的其它逻辑
		handlerService.handler(caller, "resAudit", "before", new Object[] { prj_id});
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Project", "prj_statuscode", "prj_id=" + prj_id);
		Object prj_code = baseDao.getFieldDataByCondition("Project",
				"prj_code", "prj_id=" + prj_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
		}
		if ("Project!DY".equals(caller)) {
			// 有审核人和审核日期
			int count1= baseDao.getCount("select count(*) from PrjManChange left join project on prj_code=mc_prjcode where mc_prjcode='"+prj_code+"'");
			if(count1!=0){
				BaseUtil.showError("项目编号:"+prj_code+" 已有对应的人员异动单,不能反审核!");
			}
			int count2=baseDao.getCount("select count(*) from ExpandPlan left join project on prj_code=ep_prcode where ep_prcode='"+prj_code+"'");
			if(count2!=0){
				BaseUtil.showError("项目编号:"+prj_code+" 已有对应的任务计划,不能反审核!");
			}
			int count3=baseDao.getCount("select count(*) from VisitRecord  left join ProductInfo on vr_id=pi_vrid where pi_vendor='"+prj_code+"'");
			if(count3!=0){
				BaseUtil.showError("项目编号:"+prj_code+" 已有对应的客户拜访报告,不能反审核!");
			}
			baseDao.updateByCondition("Project", "prj_statuscode='ENTERING',prj_status='" + BaseUtil.getLocalMessage("ENTERING")
					+ "',prj_text1='',prj_date1=null", "prj_id=" + prj_id);
		}else if ("Project!TG".equals(caller)) {
			// 有审核人和审核日期
			int count1= baseDao.getCount("select count(*) from PrjManChange left join project on prj_code=mc_prjcode where mc_prjcode='"+prj_code+"'");
			if(count1!=0){
				BaseUtil.showError("项目编号:"+prj_code+" 已有对应的人员异动单,不能反审核!");
			}
			int count2=baseDao.getCount("select count(*) from ExpandPlan left join project on prj_code=ep_prcode where ep_prcode='"+prj_code+"'");
			if(count2!=0){
				BaseUtil.showError("项目编号:"+prj_code+" 已有对应的任务计划,不能反审核!");
			}
			int count3=baseDao.getCount("select count(*) from VisitRecord  left join ProductInfo on vr_id=pi_vrid where pi_vendor='"+prj_code+"'");
			if(count3!=0){
				BaseUtil.showError("项目编号:"+prj_code+" 已有对应的客户拜访报告,不能反审核!");
			}
			baseDao.updateByCondition(
					"Project",
					"prj_statuscode='ENTERING',prj_status='"
							+ BaseUtil.getLocalMessage("ENTERING")
							+ "'", "prj_id=" + prj_id);
		} else {
			// 执行反审核操作
			baseDao.updateByCondition("Project", "prj_statuscode='ENTERING',prj_status='" + BaseUtil.getLocalMessage("ENTERING")
					+ "'", "prj_id=" + prj_id);
		}
		// 记录操作
		baseDao.logger.resAudit(caller, "prj_id", prj_id);
		// 执行反审核后的其它逻辑
		handlerService.handler(caller, "resAudit", "after", new Object[] { prj_id });

	}

	@Override
	public void deleteProjectTG(int prj_id,String caller) {
		Object[] status = baseDao
				.getFieldsDataByCondition("project", new String[] { "prj_statuscode","prj_code"}, "prj_id=" + prj_id);
		StateAssert.delOnlyEntering(status[0]);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { prj_id });
		int count1= baseDao.getCount("select count(*) from PrjManChange left join project on prj_code=mc_prjcode where mc_prjcode='"+status[1]+"'");
		if(count1!=0){
			BaseUtil.showError("项目编号:"+status[1]+" 已有对应的人员异动单,不能删除!");
		}
		int count2=baseDao.getCount("select count(*) from ExpandPlan left join project on prj_code=ep_prcode where ep_prcode='"+status[1]+"'");
		if(count2!=0){
			BaseUtil.showError("项目编号:"+status[1]+" 已有对应的任务计划,不能删除!");
		}
		int count3=baseDao.getCount("select count(*) from VisitRecord  left join ProductInfo on vr_id=pi_vrid where pi_vendor='"+status[1]+"'");
		if(count3!=0){
			BaseUtil.showError("项目编号:"+status[1]+" 已有对应的客户拜访报告,不能删除!");
		}
		// 删除
		baseDao.deleteById("project", "prj_id", prj_id);
		// 删除Detail
		baseDao.deleteById("projectdet", "prd_prjid", prj_id);
		
		// 记录操作
		baseDao.logger.delete(caller, "prj_id", prj_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller,  new Object[] { prj_id });
	}

}
