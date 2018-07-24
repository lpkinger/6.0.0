package com.uas.erp.service.crm.impl;

import java.util.ArrayList;
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
import com.uas.erp.dao.SqlRowList;

import com.uas.erp.service.crm.MarketTaskReportService;

@Service
public class MarketTaskReportServiceImpl implements MarketTaskReportService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void auditMarketTaskReport(int mr_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MarketTaskReport",
				"mr_statuscode", "mr_id=" + mr_id);
		StateAssert.auditOnlyCommited(status);
		// 执行提交前的其它逻辑
		handlerService.beforeAudit(caller, mr_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"MarketTaskReport",
				"mr_statuscode='AUDITED',mr_status='"
						+ BaseUtil.getLocalMessage("AUDITED") + "'mr_auditer='"
						+ SystemSession.getUser().getEm_name()
						+ "',mr_auditdate=sysdate", "mr_id=" + mr_id);
		baseDao.updateByCondition("MarketTaskReportDetail",
				"mrd_status='已转费用报销'", "mrd_mrid=" + mr_id);
		// 实际提交数加一
		String updateSql = "update MProjectTask set finishqty=finishqty+1 where taskcode in "
				+ "(select mr_taskcode from markettaskreport where mr_id="
				+ mr_id + " )";
		baseDao.execute(updateSql);
		SqlRowList rs = baseDao
				.queryForRowSet("SELECT * from MarketTaskReportDetail WHERE mrd_mrid="
						+ mr_id);
		int fc_id = baseDao.getSeqId("FEECLAIM_SEQ");
		String insertGridSql = "INSERT INTO FeeClaimDetail (fcd_class,fcd_sqamount,fcd_detno,fcd_fcid,fcd_id) VALUES(?,?,?,?,?)";
		String updateGridSql = "UPDATE ResearchProjectDetail SET ppd_used =ppd_used+ ?, ppd_surplus=ppd_amount-ppd_used- ? "
				+ "WHERE ppd_id= ?";
		int detno = 1;
		double used = 0;// 总费用
		// 添加费用报销明细，同时将费用反应到调研项目中
		while (rs.next()) {
			used += Double.parseDouble(rs.getObject("mrd_used") + "");
			baseDao.execute(
					insertGridSql,
					new Object[] { rs.getObject("mrd_costname"),
							rs.getObject("mrd_used"), detno, fc_id,
							baseDao.getSeqId("FEECLAIMDETAIL_SEQ") });
			baseDao.execute(
					updateGridSql,
					new Object[] { rs.getObject("mrd_used"),
							rs.getObject("mrd_used"), rs.getObject("mrd_ppdid") });
			detno++;
		}
		// 添加费用主表
		String insertSql = "INSERT INTO FeeClaim (fc_id,fc_code,fc_recordman,fc_type,fc_status,fc_statuscode,fc_helpman,fc_recorddate,fc_claimamount,fin_code) "
				+ "VALUES(?,?,?,?,?,?,?,"
				+ DateUtil.parseDateToOracleString(null, new Date()) + ",?,?)";
		baseDao.execute(insertSql,
				new Object[] { fc_id, baseDao.sGetMaxNumber("FeeClaim", 2),
						SystemSession.getUser().getEm_name(), "项目调研费用", "在录入",
						"ENTERING", SystemSession.getUser().getEm_name(), used,
						mr_id });
		// 记录操作
		baseDao.logger.audit(caller, "mr_id", mr_id);
		// 执行提交后的其它逻辑
		handlerService.afterAudit(caller, mr_id);
	}

	@Override
	public void resAuditMarketTaskReport(int mr_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MarketTaskReport",
				"mr_statuscode", "mr_id=" + mr_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, mr_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"MarketTaskReport",
				"mr_statuscode='ENTERING',mr_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',mr_auditer='',mr_auditdate=null", "mr_id=" + mr_id);
		baseDao.updateByCondition("MarketTaskReportDetail",
				"mrd_status='未转费用报销'", "mrd_mrid=" + mr_id);
		// 实际提交数减一
		String updateSql = "update MProjectTask set finishqty=finishqty-1 where taskcode in "
				+ "(select mr_taskcode from markettaskreport where mr_id="
				+ mr_id + " )";
		baseDao.execute(updateSql);
		// 删除费用报销单
		Object fc_id = baseDao.getFieldDataByCondition("FeeClaim", "fc_id",
				"fin_code='" + mr_id + "'");
		baseDao.deleteByCondition("FeeClaim", "fin_code='" + mr_id + "'");
		baseDao.deleteById("FeeClaimDetail", "fcd_fcid",
				Integer.parseInt(fc_id + ""));
		// 还原项目剩余费用及已使用费用
		String updateGridSql = "UPDATE ResearchProjectDetail SET ppd_used =ppd_used- ?, ppd_surplus=ppd_amount-ppd_used+ ? "
				+ "WHERE ppd_id= ?";
		SqlRowList rs = baseDao
				.queryForRowSet("SELECT * from MarketTaskReportDetail WHERE mrd_mrid="
						+ mr_id);
		while (rs.next()) {
			baseDao.execute(
					updateGridSql,
					new Object[] { rs.getObject("mrd_used"),
							rs.getObject("mrd_used"), rs.getObject("mrd_ppdid") });
		}
		// 记录操作
		baseDao.logger.resAudit(caller, "mr_id", mr_id);
		handlerService.afterResAudit(caller, mr_id);
	}

	@Override
	public void saveMarketTaskReport(String formStore, String caller,
			String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 合并相同的项目类型以及检查数据的正确性
		List<Map<Object, Object>> gStore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		gStore = check(Integer.parseInt(String.valueOf(store.get("mr_prjid"))),
				gStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gStore });
		store.put("mr_reportcode", caller);
		// 保存MarketTaskReport
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"MarketTaskReport", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存MarketTaskReportDetail
		Object[] mrd_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			mrd_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				mrd_id[i] = baseDao.getSeqId("MarketTaskReportDETAIL_SEQ");
			}
		} else {
			mrd_id[0] = baseDao.getSeqId("MarketTaskReportDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore,
				"MarketTaskReportDetail", "mrd_id", mrd_id);
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "mr_id", store.get("mr_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gStore });
	}

	@Override
	public void updateMarketTaskReport(String formStore, String caller,
			String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("MarketTaskReport",
				"mr_statuscode", "mr_id=" + store.get("mr_id"));
		StateAssert.updateOnlyEntering(status);
		// 合并相同的项目类型以及检查数据的正确性
		List<Map<Object, Object>> gStore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		gStore = check(Integer.parseInt(String.valueOf(store.get("mr_prjid"))),
				gStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gStore });
		// 修改MarketTaskReport
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"MarketTaskReport", "mr_id");
		baseDao.execute(formSql);
		// 修改MarketTaskReportDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore,
				"MarketTaskReportDetail", "mrd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("mrd_id") == null || s.get("mrd_id").equals("")
					|| s.get("mrd_id").equals("0")
					|| Integer.parseInt(s.get("mrd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("MarketTaskReportDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s,
						"MarketTaskReportDetail", new String[] { "mrd_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "mr_id", store.get("mr_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gStore });
	}

	@Override
	public void deleteMarketTaskReport(int mr_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("MarketTaskReport",
				"mr_statuscode", "mr_id=" + mr_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, mr_id);
		// 删除MarketTaskReport
		baseDao.deleteById("MarketTaskReport", "mr_id", mr_id);
		// 删除MarketTaskReportDetail
		baseDao.deleteById("MarketTaskReportdetail", "mrd_mrid", mr_id);
		// 记录操作
		baseDao.logger.delete(caller, "mr_id", mr_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, mr_id);
	}

	@Override
	public void submitMarketTaskReport(int mr_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MarketTaskReport",
				"mr_statuscode", "mr_id=" + mr_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, mr_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"MarketTaskReport",
				"mr_statuscode='COMMITED',mr_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "mr_id="
						+ mr_id);
		// 记录操作
		baseDao.logger.submit(caller, "mr_id", mr_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, mr_id);
	}

	@Override
	public void resSubmitMarketTaskReport(int mr_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MarketTaskReport",
				"mr_statuscode", "mr_id=" + mr_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, mr_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"MarketTaskReport",
				"mr_statuscode='ENTERING',mr_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "mr_id="
						+ mr_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "mr_id", mr_id);
		handlerService.afterResSubmit(caller, mr_id);
	}

	/**
	 * 合并相同的费用，检查数据的准确性，费用类型是否正确，费用是否超出预算
	 * 
	 */
	private List<Map<Object, Object>> check(int mr_prjid,
			List<Map<Object, Object>> gStore) {
		Map<String, Map<Object, Object>> countMap = new HashMap<String, Map<Object, Object>>();// 用于合并相同的费用
		for (Map<Object, Object> map : gStore) {
			if (countMap.get(map.get("mrd_costname") + "") != null) {
				double mrd_used = Double.parseDouble(map.get("mrd_used") + "");
				Map<Object, Object> m = countMap.get(map.get("mrd_costname")
						+ "");
				double countUsed = mrd_used
						+ Double.parseDouble(m.get("mrd_used") + "");
				m.put("mrd_used", countUsed);
				countMap.put(map.get("mrd_costname") + "", m);
			} else {
				countMap.put(map.get("mrd_costname") + "", map);
			}
		}
		gStore = new ArrayList<Map<Object, Object>>();
		for (String key : countMap.keySet()) {
			gStore.add(countMap.get(key));
		}
		// 检查数据的准确性
		for (Map<Object, Object> map : gStore) {
			Object ppd_surplus = baseDao.getFieldDataByCondition(
					"ResearchProjectDetail", "ppd_surplus", "ppd_costname='"
							+ map.get("mrd_costname") + "' and ppd_ppid="
							+ mr_prjid);
			if (ppd_surplus == null
					|| Double.parseDouble(ppd_surplus + "") < Double
							.parseDouble(map.get("mrd_used") + "")) {
				BaseUtil.showError("费用类型无效或者费用超出剩余预算！");
			}
		}
		return gStore;
	}
}
