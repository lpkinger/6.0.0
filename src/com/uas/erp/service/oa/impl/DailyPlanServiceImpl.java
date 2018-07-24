package com.uas.erp.service.oa.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DailyPlanDao;
import com.uas.erp.service.oa.DailyPlanService;

@Service("DailyPlanService")
public class DailyPlanServiceImpl implements DailyPlanService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private DailyPlanDao DailyPlanDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveDailyPlan(String formStore, String gridStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("DailyPlan", "dp_code='" + store.get("dp_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("oa.DailyPlan.DailyPlan.save_dacodeHasExist"));
		}
		// 执行保存前的其它逻辑
		//store.put("pu_printstatuscode", "UNPRINT");
		//store.put("pu_printstatus", BaseUtil.getLocalMessage("UNPRINT"));
		// 缺省应付供应商
		/*if (store.get("pu_receivecode") == null || store.get("pu_receivecode").toString().trim().equals("")) {
			store.put("pu_receivecode", store.get("pu_vendcode"));
			store.put("pu_receivename", store.get("pu_vendname"));
		}*/
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存DailyPlan
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "DailyPlan", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存DailyPlanDetail
		Object[] dpd_id = new Object[grid.size()];
		//bool = store.get("pu_getprice").toString().equals("-1");// 是否自动获取单价
		StringBuffer error = new StringBuffer();
		for (int i = 0; i < grid.size(); i++) {
			Map<Object, Object> map = grid.get(i);
			dpd_id[i] = baseDao.getSeqId("DailyPlanDETAIL_SEQ");
			map.put("dpd_id", dpd_id[i]);
			//map.put("dd_status", "ENTERING");
			map.put("dpd_code", store.get("dp_code"));
			
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "DailyPlanDetail");
		baseDao.execute(gridSql);
		// 修改主单据的总金额
		
		try {
			// 记录操作
			baseDao.logger.save(caller, "dp_id", store.get("dp_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
		if (error.length() > 0) {
			BaseUtil.showErrorOnSuccess(error.toString());
		}
	}

	@Override
	public void deleteDailyPlan(int dp_id, String  caller) {
		// 只能删除在录入的采购单!
		Object status = baseDao.getFieldDataByCondition("DailyPlan", "dp_statuscode", "dp_id=" + dp_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.DailyPlan.DailyPlan.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { dp_id});
		// 删除DailyPlanDetail
		DailyPlanDao.deleteDailyPlan(dp_id);
		// 删除DailyPlan
		baseDao.deleteById("DailyPlan", "dp_id", dp_id);
		// 还原请购单
		// 记录操作
		baseDao.logger.delete(caller, "dp_id", dp_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { dp_id});
	}

	@Override
	public void updateDailyPlanById(String formStore, String gridStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的采购单资料!
		Object status = baseDao.getFieldDataByCondition("DailyPlan", "dp_statuscode", "dp_id=" + store.get("dp_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.DailyPlan.DailyPlan.update_onlyEntering"));
		}
		// 执行修改前的其它逻辑
		handlerService. beforeUpdate(caller, new Object[] { store, gstore });
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "DailyPlan", "dp_id");
		baseDao.execute(formSql);
		// 修改DailyPlanDetail
		StringBuffer error = new StringBuffer();
		
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "DailyPlanDetail", "dpd_id");
		for (Map<Object, Object> s : gstore) {
			Object pdid = s.get("dpd_id");
			if (pdid == null || pdid.equals("") || pdid.equals("0") || Integer.parseInt(pdid.toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("DailyPlanDETAIL_SEQ");
				s.put("dpd_id", id);
				s.put("dpd_code", store.get("dp_code"));
				s.put("dd_status", "ENTERING");
				String sql = SqlUtil.getInsertSqlByMap(s, "DailyPlanDetail", new String[] { "dpd_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		
		baseDao.updateByCondition("DailyPlandetail", "dpd_code='" + store.get("dp_code") + "'",
				"dd_daid=" + store.get("dp_id"));
		// 记录操作
		baseDao.logger.update(caller, "dp_id", store.get("dp_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
		if (error.length() > 0) {
			BaseUtil.showErrorOnSuccess(error.toString());
		}
	}

	@Override
	public String[] printDailyPlan(int dp_id, String  caller, String reportName, String condition) {
		// 只能打印审核后的采购单!
		/*
		 * Object status = baseDao.getFieldDataByCondition("DailyPlan",
		 * "dp_statuscode", "dp_id=" + dp_id); if(!status.equals("AUDITED") &&
		 * !status.equals("PARTRECEIVED") && !status.equals("RECEIVED") &&
		 * !status.equals("NULLIFIED")){
		 * BaseUtil.showError(BaseUtil.getLocalMessage
		 * ("scm.DailyPlan.DailyPlan.print_onlyAudit")); }
		 */
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, new Object[] { dp_id });
		// 执行打印操作
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 修改打印状态
		baseDao.print("DailyPlan", "dp_id=" + dp_id, "pu_printstatus", "pu_printstatuscode");
		// 记录操作
		baseDao.logger.print(caller, "dp_id", dp_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, new Object[] { dp_id });
		return keys;
	}

	@Override
	public void auditDailyPlan(int dp_id, String  caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("DailyPlan", "dp_statuscode", "dp_id=" + dp_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.DailyPlan.DailyPlan.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller,new Object[] { dp_id});
		// 执行审核操作
		baseDao.audit("DailyPlan", "dp_id=" + dp_id, "pu_status", "dp_statuscode", "dp_audittime", "dp_auditor");
		baseDao.updateByCondition("DailyPlanDetail", "dd_status='AUDITED'", "dd_daid=" + dp_id);
		// 记录操作
		baseDao.logger.audit(caller, "dp_id", dp_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller,new Object[] { dp_id});
	}

	@Override
	public void resAuditDailyPlan(int dp_id, String  caller) {
		// 只能对状态为[已审核]的采购单进行反审核操作!
		Object[] objs = baseDao.getFieldsDataByCondition("DailyPlan", new String[] { "dp_statuscode"}, "dp_id=" + dp_id);
		if (!"AUDITED".equals(objs[0])) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.DailyPlan.DailyPlan.resAudit_onlyAudit"));
		}		
		// 执行反审核操作
		baseDao.resOperate("DailyPlan", "dp_id=" + dp_id, "dp_status", "dp_statuscode");
		baseDao.updateByCondition("DailyPlanDetail", "dd_status='ENTERING'", "dd_daid=" + dp_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "dp_id", dp_id);
	}

	@Override
	public void submitDailyPlan(int dp_id, String  caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("DailyPlan", "dp_statuscode", "dp_id=" + dp_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交操作
		baseDao.submit("DailyPlan", "dp_id=" + dp_id, "dp_status", "dp_statuscode");
		baseDao.updateByCondition("DailyPlanDetail", "dd_status='COMMITED'", "dd_daid=" + dp_id);
		// 记录操作
		baseDao.logger.submit(caller, "dp_id", dp_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { dp_id});
	}

	@Override
	public void resSubmitDailyPlan(int dp_id, String  caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("DailyPlan", "dp_statuscode", "dp_id=" + dp_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { dp_id});
		// 执行反提交操作
		baseDao.resOperate("DailyPlan", "dp_id=" + dp_id, "dp_status", "dp_statuscode");
		baseDao.updateByCondition("DailyPlanDetail", "dd_status='ENTERING'", "dd_daid=" + dp_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "dp_id", dp_id);
		handlerService.afterResSubmit(caller, new Object[] { dp_id});
	}

	@Override
	public void endDailyPlan(int dp_id, String  caller) {
		// 只能对状态为[已审核]的订单进行结案操作!
		Object status = baseDao.getFieldDataByCondition("DailyPlan", "dp_statuscode", "dp_id=" + dp_id);
		StateAssert.end_onlyAudited(status);
		// 结案
		baseDao.updateByCondition("DailyPlan",
				"dp_statuscode='FINISH',pu_sendstatus='待上传',pu_status='" + BaseUtil.getLocalMessage("FINISH")
						+ "'", "dp_id=" + dp_id);
		baseDao.updateByCondition("DailyPlanDetail", "dd_status='FINISH'", "dd_daid=" + dp_id);
		// 记录操作
		baseDao.logger.getMessageLog(BaseUtil.getLocalMessage("msg.end"), BaseUtil.getLocalMessage("msg.endSuccess"), caller, "dp_id", dp_id);
	}

	@Override
	public void resEndDailyPlan(int dp_id, String  caller) {
		// 只能对状态为[已结案]的订单进行反结案操作!
		Object status = baseDao.getFieldDataByCondition("DailyPlan", "dp_statuscode", "dp_id=" + dp_id);
		StateAssert.resEnd_onlyAudited(status);
		// 反结案
		baseDao.updateByCondition(
				"DailyPlan",
				"dp_statuscode='AUDITED',pu_sendstatus='待上传',pu_status='"
						+ BaseUtil.getLocalMessage("AUDITED") + "'", "dp_id=" + dp_id);
		baseDao.updateByCondition("DailyPlanDetail", "dd_status='AUDITED'", "dd_daid=" + dp_id);
		// 记录操作
		baseDao.logger.resEnd(caller, "dp_id", dp_id);
	}

	public void getPrice(int dp_id) {
		 
	}

	public void getStandardPrice(int dp_id) {
	}
	
	@Override
	public void vastDeleteDailyPlan(int[] id, String  caller) {
		SqlRowList rs = baseDao
				.queryForRowSet("SELECT dpd_code,pd_detno,pd_yqty,pd_acceptqty,dd_daid FROM DailyPlanDetail WHERE dd_daid in("
						+ BaseUtil.parseArray2Str(NumberUtil.toIntegerArray(id), ",") + ")");
		StringBuffer sb = new StringBuffer();
		while (rs.next()) {
			if (rs.getDouble("pd_yqty") > 0 || rs.getDouble("pd_acceptqty") > 0) {
				sb.append("采购单号[");
				sb.append(rs.getObject("dpd_code"));
				sb.append("],序号[");
				sb.append(rs.getInt("pd_detno"));
				sb.append("]中已转出或者已验收，不允许删除！");
			} else {
				baseDao.deleteByCondition("DailyPlan", "dp_id=" + rs.getInt("dd_daid"));
				baseDao.deleteByCondition("DailyPlanDetail", "dd_daid=" + rs.getInt("dd_daid"));
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showErrorOnSuccess(sb.toString());
		}
	}

	@Override
	public JSONObject copyDailyPlan(int id, String caller) {
		Map<String, Object> dif = new HashMap<String, Object>();
		// Copy Purcahse
		int nId = baseDao.getSeqId("DailyPlan_SEQ");
		dif.put("dp_id", nId);
		dif.put("pu_date", "sysdate");
		dif.put("pu_indate", "sysdate");
		String code = baseDao.sGetMaxNumber("Purcahse", 2);
		dif.put("dp_code", "'" + code + "'");
		dif.put("pu_recordid", SystemSession.getUser().getEm_id());
		dif.put("pu_recordman", "'" + SystemSession.getUser().getEm_name() + "'");
		dif.put("pu_status", "'" + BaseUtil.getLocalMessage("ENTERING") + "'");
		dif.put("dp_statuscode", "'ENTERING'");
		dif.put("pu_auditman", "null");
		dif.put("pu_auditdate", "null");
		dif.put("pu_turnstatus", "null");
		dif.put("pu_turnstatuscode", "null");
		dif.put("pu_acceptstatus", "null");
		dif.put("pu_acceptstatuscode", "null");
		dif.put("pu_printstatus", "null");
		baseDao.copyRecord("Purcahse", "Purcahse", "dp_id=" + id, dif);
		// Copy PurcahseDetail
		dif = new HashMap<String, Object>();
		dif.put("dpd_id", "DailyPlandetail_seq.nextval");
		dif.put("dd_daid", nId);
		dif.put("pd_yqty", 0);
		dif.put("pd_acceptqty", 0);
		dif.put("pd_ngacceptqty", 0);
		dif.put("pd_source", "null");
		dif.put("pd_sourcecode", "null");
		dif.put("pd_sourcedetail", 0);
		dif.put("pd_mrpstatus", "null");
		baseDao.copyRecord("PurcahseDetail", "PurcahseDetail", "dd_daid=" + id, dif);
		JSONObject obj = new JSONObject();
		obj.put("id", nId);
		obj.put("code", code);
		return obj;
	}

	
	@Override
	public void syncDailyPlan(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String ids = BaseUtil.parseArray2Str(CollectionUtil.pluck(maps, "dp_id"), ",");
		SqlRowList rs = baseDao.queryForRowSet("select dp_id from DailyPlan where dp_id in (" +
				ids + ") and dp_statuscode='AUDITED' and pu_receivecode='02.01.028' and nvl(pu_sync,' ')=' '");
		while (rs.next()) {
			DailyPlanDao.syncPurcToSqlServer(rs.getInt(1));
		}
	}

	@Override
	public void updateVendorBackInfo(String data, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(data);
		Object[] datas = baseDao.getFieldsDataByCondition("DailyPlandetail", new String[] { "pd_detno", "pd_qty",
				"dd_daid" }, "dpd_id=" + store.get("dpd_id"));
		boolean bool = Double.parseDouble(datas[1].toString()) < Double
				.parseDouble(store.get("pd_qtyreply").toString());
		if (bool)
			BaseUtil.showError("回复数量不能大于采购数!");
		baseDao.execute("update DailyPlandetail set pd_qtyreply=" + store.get("pd_qtyreply") + ",pd_isok='"
				+ store.get("pd_isok") + "',pd_deliveryreply='" + store.get("pd_deliveryreply") + "',pd_replydetail='"+store.get("pd_replydetail")+"' where dpd_id="
				+ store.get("dpd_id"));
		baseDao.logger.getMessageLog("更新供应商回复信息", "更新成功,序号:" + datas[0], caller, "dp_id", datas[2]);
	}

	@Override
	public void resetSyncStatus(String caller, Integer id) {
		DailyPlanDao.resetPurcSyncStatus(id);
	}

}
