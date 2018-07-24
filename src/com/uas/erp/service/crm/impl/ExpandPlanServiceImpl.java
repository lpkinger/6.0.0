package com.uas.erp.service.crm.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.crm.ExpandPlanService;

@Service
public class ExpandPlanServiceImpl implements ExpandPlanService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveExpandPlan(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);

		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ExpandPlan", "ep_code='"
				+ store.get("ep_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before",
				new Object[] { formStore });

		// 保存主表
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ExpandPlan",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);

		// 保存明细表
		for (Map<Object, Object> s : grid) {
			s.put("epd_id", baseDao.getSeqId("ExpandPlandet_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"ExpandPlandet");
		String check = baseDao
				.executeWithCheck(
						gridSql,
						null,
						"select wm_concat(epd_emcode) from  ExpandPlandet where epd_epid="
								+ store.get("ep_id")
								+ "  group  by  epd_emcode  having  count(epd_emcode) > 1");
		if (check != null) {
			BaseUtil.showError("明细行业务员编号重复");
		}
		// 计算实际投入人数
		baseDao.execute("update ExpandPlan set ep_actmans=(select count(1) from ExpandPlandet where epd_epid="
				+ store.get("ep_id") + ") where ep_id=" + store.get("ep_id"));
		try {
			// 记录操作
			baseDao.logger.save(caller, "ep_id", store.get("ep_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after",
				new Object[] { formStore });

	}

	@Override
	public void deleteExpandPlan(int ep_id, String caller) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("ExpandPlan",
				"ep_statuscode", "ep_id=" + ep_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before",
				new Object[] { ep_id });
		// 删除主表
		baseDao.deleteById("ExpandPlan", "ep_id", ep_id);
		// 删除明细表
		baseDao.deleteById("ExpandPlandet", "epd_epid", ep_id);
		
		//更新任务为已结案
		String taskClass = "";
		if ("ExpandPlan".equals(caller)) {
			taskClass = "agendatask";
		}
		if ("ExpandPlan!DY".equals(caller)) {
			taskClass = "researchtask";
		}
		baseDao.updateByCondition("resourceassignment", "ra_status='已结案',ra_statuscode='ENDED',ra_taskpercentdone=100", "ra_taskid in (select id from projecttask where nvl(prjplanid,0)="+ep_id+" and nvl(class,' ')='"+taskClass+"')");
		baseDao.updateByCondition("ProjectTask", "status='已结案',statuscode='FINISHED'","nvl(prjplanid,0)="+ep_id+" and nvl(class,' ')='"+taskClass+"'");
		
		// 记录操作
		baseDao.logger.delete(caller, "ep_id", ep_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after",
				new Object[] { ep_id });

	}

	@Override
	public void updateExpandPlanById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("ExpandPlan",
				"ep_statuscode", "ep_id=" + store.get("ep_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.update_onlyEntering"));
		}
		// 执行修改前的其它逻辑
		handlerService
				.handler(caller, "save", "before", new Object[] { store });
		// 修改主表
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ExpandPlan",
				"ep_id");
		baseDao.execute(formSql);
		// 修改从表
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"ExpandPlandet", "epd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("epd_id") == null || s.get("epd_id").equals("")
					|| s.get("epd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("ExpandPlandet_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "ExpandPlandet",
						new String[] { "epd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		// 明细行业务员编号重复限制更新
		String check = baseDao
				.executeWithCheck(
						gridSql,
						null,
						"select wm_concat(epd_emcode) from  ExpandPlandet where epd_epid="
								+ store.get("ep_id")
								+ "  group  by  epd_emcode  having  count(epd_emcode) > 1");
		if (check != null) {
			BaseUtil.showError("明细行业务员编号重复");
		}
		// 计算实际投入人数
		baseDao.execute("update ExpandPlan set ep_actmans=(select count(1) from ExpandPlandet where epd_epid="
				+ store.get("ep_id") + ") where ep_id=" + store.get("ep_id"));
		// 记录操作
		baseDao.logger.update(caller, "ep_id", store.get("ep_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });

	}

	@SuppressWarnings("deprecation")
	@Override
	public void submitExpandPlan(int ep_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ExpandPlan",
				"ep_statuscode", "ep_id=" + ep_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.submit_onlyEntering"));
		}
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before",
				new Object[] { ep_id });
		// 检查每个人分配的任务是否足够
		// 同时检查开始时间和结束时间是同一个月
		// 限制开始时间不能大于结束时间
		List<Object[]> data = baseDao.getFieldsDatasByCondition(
				"ExpandPlandet", new String[] { "epd_emcode", "epd_emname",
						"epd_starttime", "epd_endtime", "nvl(epd_times,0)",
						"epd_detno" }, "epd_epid=" + ep_id);
		for (Object[] os : data) {
			Date star = DateUtil.parseStringToDate(os[2].toString(),
					"yyyy-MM-dd HH:mm:ss");
			Date end = DateUtil.parseStringToDate(os[3].toString(),
					"yyyy-MM-dd HH:mm:ss");
			if (star.getTime() > end.getTime()) {
				BaseUtil.showError("第" + os[5] + "行,开始时间大于结束时间!");
			}
			if (star.getMonth() != end.getMonth()) {
				BaseUtil.showError("第" + os[5] + "行,开始时间和结束时间不在同一个月!");
			}
			if ("ExpandPlan".equals(caller)) {
				int times = baseDao
						.getCount("select count(*) from projecttask where class='agendatask' "
								+ "and (startdate between to_date('"
								+ os[2]
								+ "','yyyy-mm-dd hh24:mi:ss') and "
								+ "to_date('"
								+ os[3].toString().split(" ")[0]
								+ " 23:59:59','yyyy-mm-dd hh24:mi:ss') ) and resourcecode='"
								+ os[0] + "' and Prjplanid=" + ep_id);
				if (Double.parseDouble(os[4].toString()) != times) {
					BaseUtil.showError(os[1] + "调研任务数与任务日程安排不一致，请确认!");
				}
			}
			if ("ExpandPlan!DY".equals(caller)) {// 市场调研
				int times = baseDao
						.getCount("select count(*) from projecttask where class='researchtask' "
								+ "and (startdate between to_date('"
								+ os[2]
								+ "','yyyy-mm-dd hh24:mi:ss') and "
								+ "to_date('"
								+ os[3].toString().split(" ")[0]
								+ " 23:59:59','yyyy-mm-dd hh24:mi:ss') ) and resourcecode='"
								+ os[0] + "' and Prjplanid=" + ep_id);
				if (Double.parseDouble(os[4].toString()) != times) {
					BaseUtil.showError(os[1] + "调研任务数与任务日程安排不一致，请确认!");
				}
			}
		}
		// 执行提交操作
		baseDao.updateByCondition(
				"ExpandPlan",
				"ep_statuscode='COMMITED',ep_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "ep_id="
						+ ep_id);
		// 计算实际投入人数
		baseDao.execute("update ExpandPlan set ep_actmans=(select count(1) from ExpandPlandet where epd_epid="
				+ ep_id + ") where ep_id=" + ep_id);
		// 记录操作
		baseDao.logger.submit(caller, "ep_id", ep_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after",
				new Object[] { ep_id });

	}

	@Override
	public void resSubmitExpandPlan(int ep_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ExpandPlan",
				"ep_statuscode", "ep_id=" + ep_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resSubmit_onlyCommited"));
		}
		handlerService.handler(caller, "resCommit", "before",
				new Object[] { ep_id });
		// 执行反提交操作
		baseDao.updateByCondition(
				"ExpandPlan",
				"ep_statuscode='ENTERING',ep_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "ep_id="
						+ ep_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ep_id", ep_id);
		handlerService.handler(caller, "resCommit", "after",
				new Object[] { ep_id });

	}

	@Override
	public void auditExpandPlan(int ep_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ExpandPlan",
				"ep_statuscode", "ep_id=" + ep_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before",
				new Object[] { ep_id });
		// 执行审核操作
		Employee employee = SystemSession.getUser();
		baseDao.updateByCondition(
				"ExpandPlan",
				"ep_statuscode='AUDITED',ep_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',ep_auditer='" + employee.getEm_name()
						+ "',ep_auditdate=sysdate", "ep_id=" + ep_id);
		/** 触发相应的任务书 */
		if ("ExpandPlan".equals(caller)) {
			baseDao.updateByCondition("ProjectTask",
					"statuscode='AUDITED',status='已审核'", "nvl(prjplanid,0)="
							+ ep_id + " and class='agendatask'");
		}
		if ("ExpandPlan!DY".equals(caller)) {
			baseDao.updateByCondition("ProjectTask",
					"statuscode='AUDITED',status='已审核'", "nvl(prjplanid,0)="
							+ ep_id + " and class='researchtask'");

		}
		// 记录操作
		baseDao.logger.audit(caller, "ep_id", ep_id);
		// 执行审核后的其它逻辑
		handlerService
				.handler(caller, "audit", "after", new Object[] { ep_id });

	}

	@Override
	public void resAuditExpandPlan(int ep_id, String caller) {
		// 执行反审核前的其它逻辑
		handlerService.handler(caller, "resAudit", "before",
				new Object[] { ep_id });
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ExpandPlan",
				"ep_statuscode", "ep_id=" + ep_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resAudit_onlyAudit"));
		}
		// 执行反审核操作
		baseDao.updateByCondition(
				"ExpandPlan",
				"ep_statuscode='ENTERING',ep_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',ep_auditer=null,ep_auditdate=null", "ep_id="
						+ ep_id);
		/** 触发相应的任务书 */
		if ("ExpandPlan".equals(caller)) {
			baseDao.updateByCondition("ProjectTask",
					"statuscode='ENTERING',status='在录入'", "nvl(prjplanid,0)="
							+ ep_id + " and class='agendatask'");
		}
		if ("ExpandPlan!DY".equals(caller)) {
			baseDao.updateByCondition("ProjectTask",
					"statuscode='ENTERING',status='在录入'", "nvl(prjplanid,0)="
							+ ep_id + " and class='researchtask'");
		}
		// 记录操作
		baseDao.logger.resAudit(caller, "ep_id", ep_id);
		// 执行反审核后的其它逻辑
		handlerService.handler(caller, "resAudit", "after",
				new Object[] { ep_id });

	}
}
