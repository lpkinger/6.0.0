package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.PurMouldDao;
import com.uas.erp.service.pm.PurMouldService;

@Service("purcMouldService")
public class PurMouldServiceImpl implements PurMouldService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private PurMouldDao purMouldDao;

	@Override
	public void deletePurcMould(int pm_id, String caller) {
		// 只能删除在录入的单据!
		Object[] status = baseDao.getFieldsDataByCondition("PURMOULD", new String[] { "pm_statuscode", "pm_source" }, "pm_id=" + pm_id);
		StateAssert.delOnlyEntering(status[0]);
		SqlRowList rs = baseDao
				.queryForRowSet("select pmd_sourceid from PurMouldDetail where pmd_pmid=? and pmd_sourcetype='开模申请单'", pm_id);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { pm_id });
		// 删除PurcMould
		baseDao.deleteById("PurMould", "pm_id", pm_id);
		// 删除PurMouldDetail
		baseDao.deleteById("PurMouldDetail", "pmd_pmid", pm_id);
		// 删除PURMOULDDET
		baseDao.deleteById("PURMOULDDET", "pd_pmid", pm_id);
		// 删除之后还原开模申请单状态
		while (rs.next()) {
			baseDao.updateByCondition("APPMouldDetail", "ad_statuscode=null,ad_status=null", "ad_id=" + rs.getInt("pmd_sourceid"));
		}
		// 删除之后还原模具核价单状态
		int i = baseDao.getCountByCondition("PurMould", "pm_source='" + status[1] + "'");
		if (i == 0) {
			baseDao.updateByCondition("PriceMould", "pd_statuscode='AUDITED',pd_status='" + BaseUtil.getLocalMessage("AUDITED") + "'",
					"pd_code='" + status[1] + "'");
		}
		// 记录操作;
		baseDao.logger.delete(caller, "pm_id", pm_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { pm_id });
	}

	@Override
	public int turnYSReport(int pm_id, String caller) {
		String dets = baseDao.queryForObject(
				"select WM_CONCAT(pmd_detno) from PurMouldDetail where pmd_pmid=? and nvl(pmd_closestatuscode,' ')='FINISH'", String.class,
				pm_id);
		if (dets != null) {
			BaseUtil.showError("存在已结案的明细行，不允许转单操作!行号" + dets);
		}
		//@zjh start 2018050325
		if(baseDao.isDBSetting("Purc!Mould", "MouldProdIdentified")){
			SqlRowList queryForRowSet = baseDao.queryForRowSet("select count(1) cn,wm_concat(psd_prodcode) as psd_prodcodes from PURMOULD left join PURMOULDdetail on pm_id =pmd_pmid "
					+"left join ProductSet on pmd_pscode=ps_code "
					+"left join ProductSetdetail on ps_id = psd_psid "
					+"left join Product on psd_prodcode=pr_code "
					+"where pm_id="+pm_id+" and nvl(pr_material,' ')='未认可'");
			if(queryForRowSet.next()&&queryForRowSet.getInt("cn")>0){
				BaseUtil.showError("模具资料明细物料存在未认定物料"+queryForRowSet.getString("psd_prodcodes")+"，不允许转验收报告");
			}
		}
		if(baseDao.isDBSetting("Purc!Mould", "MouldProdAudit")){
			SqlRowList queryForRowSet = baseDao.queryForRowSet("select count(1) cn,wm_concat(psd_prodcode) as psd_prodcodes from PURMOULD left join PURMOULDdetail on pm_id =pmd_pmid "
					+"left join ProductSet on pmd_pscode=ps_code "
					+"left join ProductSetdetail on ps_id = psd_psid "
					+"left join Product on psd_prodcode=pr_code left join ProductApproval on pr_code=pa_prodcode "
					+"where pm_id="+pm_id+" and nvl(pa_status,' ')<>'已审核'");
			if(queryForRowSet.next()&&queryForRowSet.getInt("cn")>0){
				BaseUtil.showError("模具资料明细物料"+queryForRowSet.getString("psd_prodcodes")+"不存在已审核物料认定单，不允许转验收报告");
			}
		}
		//@zjh end  2018050325
		int moid = 0;
		// 判断该模具采购单是否已经转入过模具验收报告
		Object code = baseDao.getFieldDataByCondition("PURMOULD", "pm_code", "pm_id=" + pm_id);
		code = baseDao.getFieldDataByCondition("MOD_YSREPORT", "mo_code", "mo_source='" + code + "'");
		if (code != null && !code.equals("")) {
			BaseUtil.showError("该模具采购单已经转入过模具验收报告[" + code + "]！");
		} else {
			moid = purMouldDao.turnYSReport(pm_id);
		}
		// 修改报价单状态
		baseDao.updateByCondition("PURMOULD", "pm_turnstatuscode='TURNYS',pm_turnstatus='" + BaseUtil.getLocalMessage("TURNYS") + "'",
				"pm_id=" + pm_id);
		// 记录操作
		baseDao.logger.turn("转模具验收报告", "Purc!Mould", "pm_id", pm_id);
		return moid;
	}

	@Override
	public void savePurcMould(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("PURMOULD", "pm_code='" + store.get("pm_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存PurcMould
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PurMould", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存PurMouldDetail

		for (Map<Object, Object> s : grid) {
			s.put("pmd_id", baseDao.getSeqId("PurMouldDetail_SEQ"));
			s.put("pmd_code", store.get("pm_code"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "PurMouldDetail");
		baseDao.execute(gridSql);
		getTotal(store.get("pm_id"));
		// 记录操作
		baseDao.logger.save(caller, "pm_id", store.get("pm_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });

	}

	void getTotal(Object pm_id) {
		baseDao.execute("update PurMouldDetail set pmd_total=ROUND(nvl(pmd_price,0)*nvl(pmd_qty,0),2) where pmd_pmid=" + pm_id);
		baseDao.execute("update PURMOULD set pm_taxtotal=(select sum(pmd_total) from PurMouldDetail where PurMouldDetail.pmd_pmid = PURMOULD.pm_id) where pm_id="
				+ pm_id);
		baseDao.execute("update PurMouldDetail set PMD_CODE=(select pm_code from PURMOULD where pmd_pmid=pm_id) where pmd_pmid=" + pm_id
				+ " and not exists (select 1 from PURMOULD where pm_code=PMD_CODE)");
		baseDao.execute("update PURMOULD set (pm_prjcode,pm_prjname)=(select ps_prjcode,ps_prjname from productset,PurMouldDetail where pmd_pmid=pm_id and pmd_pscode=ps_code and nvl(pmd_pscode,' ')<>' ' and nvl(ps_prjcode,' ')<>' ' and rownum=1)"
				+ " where pm_id=" + pm_id + " and nvl(pm_prjcode,' ')=' '");
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select pd_id,in_purcrate,pm_taxtotal from PURMOULD left join PURMOULDDET on pm_id=pd_pmid left join Installment on pd_paydesc=in_name where pd_pmid=? and nvl(pd_paydesc,' ')<>' ' and nvl(in_purcrate,0)<>0",
						pm_id);
		while (rs.next()) {
			baseDao.execute("update PURMOULDDET set pd_amount=round((" + rs.getGeneralDouble("pm_taxtotal") + "*"
					+ rs.getGeneralDouble("in_purcrate") + "/100),2) where pd_id=" + rs.getInt("pd_id"));
		}
	}

	@Override
	public void updatePurcMouldById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("PURMOULD", "pm_statuscode", "pm_id=" + store.get("pm_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 修改PurcMould
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PurMould", "pm_id");
		baseDao.execute(formSql);
		// 修改PurMouldDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "PurMouldDetail", "pmd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("pmd_id") == null || s.get("pmd_id").equals("") || s.get("pmd_id").equals("0")
					|| Integer.parseInt(s.get("pmd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("PurMouldDetail_SEQ");
				s.put("pmd_code", store.get("pm_code"));
				String sql = SqlUtil.getInsertSqlByMap(s, "PurMouldDetail", new String[] { "pmd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		getTotal(store.get("pm_id"));
		// 记录操作
		baseDao.logger.update(caller, "pm_id", store.get("pm_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void printPurcMould(int pm_id, String caller) {
		// 只能打印审核后的单据!
		Object status = baseDao.getFieldDataByCondition("PURMOULD", "pm_statuscode", "pm_id=" + pm_id);
		if (!status.equals("AUDITED") && !status.equals("PARTRECEIVED") && !status.equals("RECEIVED") && !status.equals("NULLIFIED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.print_onlyAudit"));
		}
		getTotal(pm_id);
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, new Object[] { pm_id });
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "pm_id", pm_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, new Object[] { pm_id });
	}

	@Override
	public void auditPurcMould(int pm_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("PURMOULD", "pm_statuscode", "pm_id=" + pm_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { pm_id });
		baseDao.execute("update PurMouldDet set pd_isfinal=(select in_isfinal from installment where in_name=pd_paydesc) where pd_pmid="
				+ pm_id + " and nvl(pd_paydesc,' ')<>' ' and exists (select 1 from installment where in_name=pd_paydesc)");
		int count = baseDao.getCount("select count(*) from PURMOULDDET where pd_pmid=" + pm_id);
		if (count > 0) {
			count = baseDao.getCount("select count(*) from PURMOULDDET where pd_pmid=" + pm_id + " and nvl(pd_isfinal,0)<>0");
			if (count > 1) {
				BaseUtil.showError("分期付款明细只能存在一个尾款！");
			}
			double s1 = baseDao.getSummaryByField("PurMouldDetail", "pmd_total", "pmd_pmid=" + pm_id);
			double s2 = baseDao.getSummaryByField("PURMOULDDET", "pd_amount", "pd_pmid=" + pm_id);
			if (s1 != s2) {
				BaseUtil.showError("分期付款明细总金额[" + s2 + "]不等于采购金额[" + s1 + "]，不能审核！");
			}
		} else {
			baseDao.execute("insert into PURMOULDDet(pd_id,pd_pmid,pd_detno,pd_paydesc,pd_isfinal,pd_amount,pd_yamount) "
					+ "select PURMOULDDET_SEQ.NEXTVAL, PM_ID, 1, '尾款', 1, pm_taxtotal, 0 from PURMOULD where pm_id=" + pm_id);
		}
		// 执行审核操作
		baseDao.audit("PURMOULD", "pm_id=" + pm_id, "pm_status", "pm_statuscode", "pm_auditdate", "pm_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "pm_id", pm_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { pm_id });
	}

	@Override
	public void resAuditPurcMould(int pm_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("PURMOULD", new String[] { "pm_statuscode", "pm_code" }, "pm_id=" + pm_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		String dets = baseDao
				.queryForObject(
						"select wm_concat(DISTINCT mp_code) from  MOULDFEEPLEASE LEFT JOIN MOULDFEEPLEASEDETAIL ON MP_ID=mfd_mpid WHERE mfd_code=?",
						String.class, status[1]);
		if (dets != null) {
			BaseUtil.showError("该模具采购单已转付款申请[" + dets + "]，不允许反审核！");
		}
		int count = baseDao.getCountByCondition("PURMOULD", "pm_id=" + pm_id + " and pm_paystatuscode='PAYMENTED'");
		if (count > 0) {
			BaseUtil.showError("该模具采购单[已付款]，不允许反审核！");
		}
		// 判断该模具采购单是否已经转入过模具验收报告
		Object code = baseDao.getFieldDataByCondition("PURMOULD", "pm_code", "pm_id=" + pm_id);
		code = baseDao.getFieldDataByCondition("MOD_YSREPORT", "mo_code", "mo_source='" + code + "'");
		if (code != null && !code.equals("")) {
			BaseUtil.showError("该模具采购单已经转模具验收报告[" + code + "]，不允许反审核！");
		}
		// 执行反审核操作
		baseDao.resAudit("PURMOULD", "pm_id=" + pm_id, "pm_status", "pm_statuscode", "pm_auditdate", "pm_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "pm_id", pm_id);
	}

	@Override
	public void submitPurcMould(int pm_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("PURMOULD", "pm_statuscode", "pm_id=" + pm_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { pm_id });
		getTotal(pm_id);
		baseDao.execute("update PurMouldDet set pd_isfinal=(select in_isfinal from installment where in_name=pd_paydesc) where pd_pmid="
				+ pm_id + " and nvl(pd_paydesc,' ')<>' ' and exists (select 1 from installment where in_name=pd_paydesc)");
		int count = baseDao.getCount("select count(*) from PURMOULDDET where pd_pmid=" + pm_id);
		if (count > 0) {
			count = baseDao.getCount("select count(*) from PURMOULDDET where pd_pmid=" + pm_id + " and nvl(pd_isfinal,0)<>0");
			if (count > 1) {
				BaseUtil.showError("分期付款明细只能存在一个尾款！");
			}
			double s1 = baseDao.getSummaryByField("PurMouldDetail", "pmd_total", "pmd_pmid=" + pm_id);
			double s2 = baseDao.getSummaryByField("PURMOULDDET", "pd_amount", "pd_pmid=" + pm_id);
			if (s1 != s2) {
				BaseUtil.showError("分期付款明细总金额[" + s2 + "]不等于采购金额[" + s1 + "]，不能提交！");
			}
		} else {
			baseDao.execute("insert into PURMOULDDet(pd_id,pd_pmid,pd_detno,pd_paydesc,pd_isfinal,pd_amount,pd_yamount) "
					+ "select PURMOULDDET_SEQ.NEXTVAL, PM_ID, 1, '尾款', 1, pm_taxtotal, 0 from PURMOULD where pm_id=" + pm_id);
		}
		
		// 执行提交操作
		baseDao.submit("PURMOULD", "pm_id=" + pm_id, "pm_status", "pm_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pm_id", pm_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { pm_id });
	}

	@Override
	public void resSubmitPurcMould(int pm_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("PURMOULD", "pm_statuscode", "pm_id=" + pm_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { pm_id });
		// 执行反提交操作
		baseDao.resOperate("PURMOULD", "pm_id=" + pm_id, "pm_status", "pm_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pm_id", pm_id);
		handlerService.afterResSubmit(caller, new Object[] { pm_id });
	}

	@Override
	public void updatepaystatus(int pm_id, String paystatus, String payremark) {
		baseDao.execute("update PURMOULD set pm_payremark='" + payremark + "',pm_paystatus='" + paystatus + "' where pm_id=" + pm_id);
		baseDao.logger.others("更新付款状态", "msg.updateSuccess", "Purc!Mould", "pm_id", pm_id);
	}

	@Override
	public int turnFeePlease(int pm_id, String caller) {
		int mpid = 0;
		// 判断该模具修改申请单已转入模具付款申请单
		Object code = baseDao.getFieldDataByCondition("MOULDFEEPLEASE", "mp_code", "mp_sourceid=" + pm_id + " and mp_sourcetype='模具采购单'");
		if (code != null && !code.equals("")) {
			BaseUtil.showError("该模具采购单已转入模具付款申请单,申请单号" + code);
		} else {
			SqlRowList rs = baseDao.queryForRowSet("SELECT sum(nvl(pmd_total,0)) from PurMouldDetail where pmd_pmid=?", pm_id);
			Double aldamount = 0.0;
			if (rs.next()) {
				aldamount = rs.getDouble(1);
				if (aldamount == 0) {
					BaseUtil.showError("该模具采购单没有金额不能转!");
				}
			}
			mpid = purMouldDao.turnFeePlease(pm_id, aldamount);
			baseDao.updateByCondition("PURMOULD", "PM_PLEASESTATUS='已转付款申请'", "pm_id=" + pm_id);
			baseDao.logger.turn("转付款申请", "Purc!Mould", "pm_id", pm_id);
		}
		return mpid;
	}

	@Override
	public void savePurcMould(String formStore, String gridStore, String gridStore2, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> grid2 = BaseUtil.parseGridStoreToMaps(gridStore2);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("PURMOULD", "pm_code='" + store.get("pm_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存PurcMould
		baseDao.execute(SqlUtil.getInsertSqlByFormStore(store, "PurMould", new String[] {}, new Object[] {}));
		// 保存PurMouldDetail
		baseDao.execute(SqlUtil.getInsertSqlbyList(grid, "PurMouldDetail", "pmd_id"));
		// 保存PURMOULDDET
		baseDao.execute(SqlUtil.getInsertSqlbyList(grid2, "PurMouldDet", "pd_id"));
		getTotal(store.get("pm_id"));
		baseDao.execute("update PurMouldDet set pd_isfinal=(select in_isfinal from installment where in_name=pd_paydesc) where pd_pmid="
				+ store.get("pm_id") + " and nvl(pd_paydesc,' ')<>' ' and exists (select 1 from installment where in_name=pd_paydesc)");
		// 记录操作
		baseDao.logger.save(caller, "pm_id", store.get("pm_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void updatePurcMouldById(String formStore, String gridStore, String gridStore2, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> gstore2 = BaseUtil.parseGridStoreToMaps(gridStore2);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("PURMOULD", "pm_statuscode", "pm_id=" + store.get("pm_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 修改PurcMould
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "PurMould", "pm_id"));
		// 修改PurMouldDetail
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "PurMouldDetail", "pmd_id"));
		// 修改PURMOULDDET
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore2, "PURMOULDDET", "pd_id"));
		getTotal(store.get("pm_id"));
		baseDao.execute("update PurMouldDet set pd_isfinal=(select in_isfinal from installment where in_name=pd_paydesc) where pd_pmid="
				+ store.get("pm_id") + " and nvl(pd_paydesc,' ')<>' ' and exists (select 1 from installment where in_name=pd_paydesc)");
		// 记录操作
		baseDao.logger.update(caller, "pm_id", store.get("pm_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}
}
