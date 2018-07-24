package com.uas.erp.service.plm.impl;

import com.uas.erp.model.Employee;
import com.uas.erp.service.plm.PrjManChangeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

@Service
public class PrjManChangeServiceImpl implements PrjManChangeService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void savePrjManChange(String formStore, String gridStore,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		boolean bool = baseDao.checkByCondition("PrjManChange", "mc_code='" + store.get("mc_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] {
				formStore });

		for (Map<Object, Object> s : grid) {
			s.put("mcd_id", baseDao.getSeqId("PrjManChangedet_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"PrjManChangedet");
		String check = baseDao.executeWithCheck(gridSql, null, "select wm_concat(mcd_emcode) from  prjmanchangedet where mcd_mcid=" + store.get("mc_id") + "  group  by  mcd_emcode  having  count(mcd_emcode) > 1");
		if (check != null) {
			BaseUtil.showError("明细行员工编号重复");
		}
		// 保存PrjManChange
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PrjManChange",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "mc_id", store.get("mc_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] {
				formStore});

	}

	@Override
	public void deletePrjManChange(int mc_id,String caller) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("PrjManChange",
				"mc_statuscode", "mc_id=" + mc_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before",
				new Object[] { mc_id });
		// 删除PrjManChange
		baseDao.deleteById("PrjManChange", "mc_id", mc_id);
		// 删除Contact
		baseDao.deleteById("PrjManChangedet", "mcd_mcid", mc_id);
		// 记录操作
		baseDao.logger.delete(caller, "mc_id", mc_id);
		// 执行删除后的其它逻辑
		handlerService.handler("PrjManChange", "delete", "after", new Object[] {
				mc_id });

	}

	@Override
	public void updatePrjManChangeById(String formStore, String gridStore,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("PrjManChange",
				"mc_statuscode", "mc_id=" + store.get("mc_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.update_onlyEntering"));
		}
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] {
				store});
		List<String> gridSql = SqlUtil.getInsertOrUpdateSql(gstore, "PrjManChangedet", "mcd_id");
		String check = baseDao.executeWithCheck(gridSql, null, "select wm_concat(mcd_emcode) from  prjmanchangedet where mcd_mcid=" + store.get("mc_id") + "  group  by  mcd_emcode  having  count(mcd_emcode) > 1");
		if (check != null) {
			BaseUtil.showError("明细行员工编号重复");
		}
		// 保存PrjManChange
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PrjManChange",
				"mc_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "mc_id", store.get("mc_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] {
				store});

	}

	@Override
	public void submitPrjManChange(int mc_id,String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("PrjManChange",
				"mc_statuscode", "mc_id=" + mc_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.submit_onlyEntering"));
		}
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before",
				new Object[] { mc_id });
		// 执行提交操作
		baseDao.updateByCondition(
				"PrjManChange",
				"mc_statuscode='COMMITED',mc_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'",
				"mc_id=" + mc_id);
		// 记录操作
		baseDao.logger.submit(caller, "mc_id", mc_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] {
				mc_id});

	}

	@Override
	public void resSubmitPrjManChange(int mc_id,String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("PrjManChange",
				"mc_statuscode", "mc_id=" + mc_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.resSubmit_onlyCommited"));
		}
		handlerService.handler(caller, "resCommit", "before",
				new Object[] { mc_id });
		// 执行反提交操作
		baseDao.updateByCondition(
				"PrjManChange",
				"mc_statuscode='ENTERING',mc_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'",
				"mc_id=" + mc_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "mc_id", mc_id);
		handlerService.handler(caller, "resCommit", "after",
				new Object[] { mc_id, });

	}

	@Override
	@Transactional
	public void auditPrjManChange(int mc_id,String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("PrjManChange",
				"mc_statuscode", "mc_id=" + mc_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] {
				mc_id});
		if("PrjManChange".equals(caller)){
			List<String> sqls = new ArrayList<String>();
			List<Object[]> datas = baseDao
					.getFieldsDatasByCondition(
							"PrjManChangedet left join prjManChange on mcd_mcid=mc_id left join Project on mc_prjcode=prj_code and nvl(prj_class,' ')='市场推广立项'",
							new String[] { "mcd_emcode", "mcd_emname",
									"mcd_operation", "mc_prjcode", "prj_id" },
							"mcd_mcid=" + mc_id);
			if (datas != null) {
				int maxdetno = baseDao
						.getCount("select max(prd_detno) from projectdet where prd_prjid="
								+ datas.get(0)[4]);
				for (Object[] os : datas) {
					if ("增加".equals("" + os[2])) {
						if (baseDao
								.getCount("select count(*) from projectdet where prd_emcode='"+os[0]+"' and prd_prjid="
										+ os[4]) == 0) {// 如果不存在才添加
							StringBuffer sb = new StringBuffer(
									"insert into projectdet(prd_id,prd_prjid,prd_detno,prd_emcode,prd_emname)values(projectdet_seq.nextval,");
							sb.append(os[4]).append(",").append(++maxdetno)
									.append(",'");
							sb.append(os[0]).append("','").append(os[1])
									.append("')");
							sqls.add(sb.toString());
						}
					} else if ("删除".equals("" + os[2])) {
						int count=baseDao.getCount("select count(*) from VisitRecord left join ProductInfo on vr_id=pi_vrid where pi_vendor='"
					+os[3]+"' and vr_recorder='"+os[1]+"' and vr_class='OfficeClerk'");
						if(count!=0){
							BaseUtil.showError(os[1]+"已提交拜访报告,不能删除!");
						}
						int count1=baseDao.getCount("select count(1) FROM ExpandPlan  LEFT JOIN ExpandPlandet ON ep_id=epd_epid where ep_type='市场推广立项' and ep_prcode='"+os[3]+"' and epd_emcode='"+os[0]+"'");
						if(count1!=0){
							BaseUtil.showError(os[1]+"已分配了任务,不能删除!");
						}
						sqls.add("delete projectdet where prd_emcode='"+os[0]+"' and prd_prjid="+os[4]);
					}
				}
			}
			baseDao.execute(sqls);
		}
		if("PrjManChange!DY".equals(caller)){
			List<String> sqls = new ArrayList<String>();
			List<Object[]> datas = baseDao
					.getFieldsDatasByCondition(
							"PrjManChangedet left join prjManChange on mcd_mcid=mc_id left join Project on mc_prjcode=prj_code and nvl(prj_class,' ')='市场调研立项'",
							new String[] { "mcd_emcode", "mcd_emname",
									"mcd_operation", "mc_prjcode", "prj_id" },
							"mcd_mcid=" + mc_id);
			if (datas != null) {
				int maxdetno = baseDao
						.getCount("select max(prd_detno) from projectdet where prd_prjid="
								+ datas.get(0)[4]);
				for (Object[] os : datas) {
					if ("增加".equals("" + os[2])) {
						if (baseDao
								.getCount("select count(*) from projectdet where prd_emcode='"+os[0]+"' and prd_prjid="
										+ os[4]) == 0) {// 如果不存在才添加
							StringBuffer sb = new StringBuffer(
									"insert into projectdet(prd_id,prd_prjid,prd_detno,prd_emcode,prd_emname)values(projectdet_seq.nextval,");
							sb.append(os[4]).append(",").append(++maxdetno)
									.append(",'");
							sb.append(os[0]).append("','").append(os[1])
									.append("')");
							sqls.add(sb.toString());
						}
					} else if ("删除".equals("" + os[2])) {
						/*int count=baseDao.getCount("select count(*) from VisitRecord left join ProductInfo on vr_id=pi_vrid where pi_vendor='"
					+os[3]+"' and vr_recorder='"+os[1]+"' and vr_class='OfficeClerk'");
						if(count!=0){
							BaseUtil.showError(os[1]+"已提交拜访报告,不能删除!");
						}
						*/
						int count1=baseDao.getCount("select count(1) FROM ExpandPlan  LEFT JOIN ExpandPlandet ON ep_id=epd_epid where ep_type='市场调研立项' and ep_prcode='"+os[3]+"' and epd_emcode='"+os[0]+"'");
						if(count1!=0){
							BaseUtil.showError(os[1]+"已分配了任务,不能删除!");
						}
						sqls.add("delete projectdet where prd_emcode='"+os[0]+"' and prd_prjid="+os[4]);
					}
				}
			}
			baseDao.execute(sqls);
		}
		// 执行审核操作
		Employee employee = SystemSession.getUser();
		baseDao.updateByCondition(
				"PrjManChange",
				"mc_statuscode='AUDITED',mc_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',mc_auditer='" + employee.getEm_name()
						+ "',mc_auditdate=sysdate", "mc_id=" + mc_id);

		// 记录操作
		baseDao.logger.audit(caller, "mc_id", mc_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] {
				mc_id});

	}

	@Override
	public void resAuditPrjManChange(int mc_id,String caller) {
		// 执行反审核前的其它逻辑
		handlerService.handler(caller, "resAudit", "before",
				new Object[] { mc_id});
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("PrjManChange",
				"mc_statuscode", "mc_id=" + mc_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.resAudit_onlyAudit"));
		}
		// 执行反审核操作
		baseDao.updateByCondition(
				"PrjManChange",
				"mc_statuscode='ENTERING',mc_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',mc_auditer=null,mc_auditdate=null", "mc_id="
						+ mc_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "mc_id", mc_id);
		// 执行反审核后的其它逻辑
		handlerService.handler(caller, "resAudit", "after",
				new Object[] { mc_id });

	}

}
