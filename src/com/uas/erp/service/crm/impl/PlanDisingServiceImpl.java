package com.uas.erp.service.crm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.crm.PlanDisingService;

@Service
public class PlanDisingServiceImpl implements PlanDisingService {
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private BaseDao baseDao;

	@Override
	public void savePlanDising(String formStore, String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gridstores = BaseUtil
				.parseGridStoreToMaps(param);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gridstores });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"Merchandising", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		List<String> sqls = new ArrayList<String>();
		Object mh_id = store.get("mh_id");
		for (Map<Object, Object> map : gridstores) {
			map.put("mhd_id", baseDao.getSeqId("MerchanDisingDetail_SEQ"));
			sqls.add(SqlUtil.getInsertSqlByMap(map, "MerchandisingDetail"));
		}
		baseDao.execute(sqls);
		baseDao.updateByCondition("Merchandising",
				"mh_total=(select sum(mhd_sumtotal) from MerchandisingDetail where mhd_mhid="
						+ mh_id + ")", "mh_id=" + mh_id);
		baseDao.updateByCondition("Merchandising",
				"mh_qty=(select sum(mhd_sumqty) from MerchandisingDetail where mhd_mhid="
						+ mh_id + ")", "mh_id=" + mh_id);
		try {
			// 记录操作
			baseDao.logger.save(caller, "mh_id", mh_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gridstores });
	}

	@Override
	public void deletePlanDising(int mh_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, mh_id);
		// 删除
		baseDao.deleteById("Merchandising", "mh_id", mh_id);
		// 记录操作
		baseDao.logger.delete(caller, "mh_id", mh_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, mh_id);
	}

	@Override
	public void updatePlanDising(String formStore, String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gridstores = BaseUtil
				.parseGridStoreToMaps(param);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gridstores });
		List<String> sqls = new ArrayList<String>();
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"Merchandising", "mh_id");
		Object mh_id = store.get("mh_id");
		sqls.add(formSql);
		for (Map<Object, Object> map : gridstores) {
			Object mhd_id = map.get("mhd_id");
			if (mhd_id != null && !mhd_id.equals("") && !"0".equals(mhd_id)) {
				sqls.add(SqlUtil.getUpdateSqlByFormStore(map,
						"MerchandisingDetail", "mhd_id"));
			} else {
				map.put("mhd_id", baseDao.getSeqId("MerchandisingDetail_SEQ"));
				sqls.add(SqlUtil.getInsertSqlByMap(map, "MerchandisingDetail"));
			}
			
		}
		baseDao.execute(sqls);
		baseDao.updateByCondition("Merchandising",
				"mh_total=(select sum(mhd_sumtotal) from MerchandisingDetail where mhd_mhid="
						+ mh_id + ")", "mh_id=" + mh_id);
		baseDao.updateByCondition("Merchandising",
				"mh_qty=(select sum(mhd_sumqty) from MerchandisingDetail where mhd_mhid="
						+ mh_id + ")", "mh_id=" + mh_id);
		// 记录操作
		baseDao.logger.update(caller, "mh_id", mh_id);
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gridstores });
	}

	@Override
	public void auditPlanDising(int mh_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Merchandising",
				"mh_statuscode", "mh_id=" + mh_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, mh_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"Merchandising",
				"mh_statuscode='AUDITED',mh_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',mh_auditer='"
						+ SystemSession.getUser().getEm_name()
						+ "',mh_auditdate=sysdate", "mh_id=" + mh_id);
		// 记录操作
		baseDao.logger.audit(caller, "mh_id", mh_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, mh_id);

	}

	@Override
	public void resAuditPlanDising(int mh_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Merchandising",
				"mh_statuscode", "mh_id=" + mh_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行审核前的其它逻辑
		handlerService.beforeResAudit(caller, mh_id);
		baseDao.updateByCondition(
				"Merchandising",
				"mh_statuscode='ENTERING',mh_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',mh_auditer='',mh_auditdate=null", "mh_id=" + mh_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "mh_id", mh_id);
		handlerService.afterResAudit(caller, mh_id);
	}

	@Override
	public void submitPlanDising(int mh_id, String caller) {

		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Merchandising",
				"mh_statuscode", "mh_id=" + mh_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, mh_id);
		//判断是否存在  同一年中,同一个业务员，同一个产品编号,同一个客户这种情况提示请更新后再提交
		int count = baseDao.getCount("select count(*) from MerchandisingDetail a left join Merchandising b on a.mhd_mhid=b.MH_ID "
				+ "where (a.mhd_prodcode,a.mhd_custcode,b.mh_year,b.mh_sellercode,a.mhd_prodbrand) in   "
				+ "(select mhd_prodcode,mhd_custcode,mh_year,mh_sellercode,mhd_prodbrand from MerchandisingDetail "
				+ "left join Merchandising  on mhd_mhid=mh_id where mhd_mhid="+mh_id+"group by mhd_prodcode,mhd_custcode,mh_year,mh_sellercode,mhd_prodbrand"
				+ " having count(*) > 1) and mh_id="+ mh_id);
		if (count > 0) {
			BaseUtil.showError("明细行存在产品编号，物料编号重复的行，请更新后再提交");
		}
		//检测产品编号与品牌不一致的
		String erronenull = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(mhd_detno) from MerchandisingDetail left join Merchandising on mhd_mhid=mh_id "
						+ "where mh_id=? and mhd_prodcode is not null and mhd_prodbrand is not null and mhd_prodbrand!="
						+ "(select pr_brand from product where pr_code=MerchandisingDetail.mhd_prodcode)",
						String.class, mh_id);
		if (erronenull != null){
			BaseUtil.showError("明细行存在产品编号与品牌不一致的行,行号：" + erronenull);
		}
		//检测销售额，毛利润其中一个为空的情况
		String erronenull1 = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(mhd_detno) from MerchandisingDetail left join Merchandising on mhd_mhid=mh_id "
						+ "where mh_id=? and ((mhd_qty1>0 and mhd_total1=0) or (mhd_qty1=0 and mhd_total1>0))",
						String.class, mh_id);
		if (erronenull1 != null){
			BaseUtil.showError("1月份出现销售额填写，毛利润没填写，或者销售额未填写，毛利润填写的情况,请修改后再提交,行号：" + erronenull1);
		}
		String erronenull2 = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(mhd_detno) from MerchandisingDetail left join Merchandising on mhd_mhid=mh_id "
						+ "where mh_id=? and ((mhd_qty2>0 and mhd_total2=0) or (mhd_qty2=0 and mhd_total2>0))",
						String.class, mh_id);
		if (erronenull2 != null){
			BaseUtil.showError("2月份出现销售额填写，毛利润没填写，或者销售额未填写，毛利润填写的情况,请修改后再提交,行号：" + erronenull2);
		}
		String erronenull3 = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(mhd_detno) from MerchandisingDetail left join Merchandising on mhd_mhid=mh_id "
						+ "where mh_id=? and ((mhd_qty3>0 and mhd_total3=0) or (mhd_qty3=0 and mhd_total3>0))",
						String.class, mh_id);
		if (erronenull3 != null){
			BaseUtil.showError("3月份出现销售额填写，毛利润没填写，或者销售额未填写，毛利润填写的情况,请修改后再提交,行号：" + erronenull3);
		}
		String erronenull4 = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(mhd_detno) from MerchandisingDetail left join Merchandising on mhd_mhid=mh_id "
						+ "where mh_id=? and ((mhd_qty4>0 and mhd_total4=0) or (mhd_qty4=0 and mhd_total4>0))",
						String.class, mh_id);
		if (erronenull4 != null){
			BaseUtil.showError("4月份出现销售额填写，毛利润没填写，或者销售额未填写，毛利润填写的情况,请修改后再提交,行号：" + erronenull4);
		}
		String erronenull5 = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(mhd_detno) from MerchandisingDetail left join Merchandising on mhd_mhid=mh_id "
						+ "where mh_id=? and ((mhd_qty5>0 and mhd_total5=0) or (mhd_qty5=0 and mhd_total5>0))",
						String.class, mh_id);
		if (erronenull5 != null){
			BaseUtil.showError("5月份出现销售额填写，毛利润没填写，或者销售额未填写，毛利润填写的情况,请修改后再提交,行号：" + erronenull5);
		}
		String erronenull6 = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(mhd_detno) from MerchandisingDetail left join Merchandising on mhd_mhid=mh_id "
						+ "where mh_id=? and ((mhd_qty6>0 and mhd_total6=0) or (mhd_qty6=0 and mhd_total6>0))",
						String.class, mh_id);
		if (erronenull6 != null){
			BaseUtil.showError("6月份出现销售额填写，毛利润没填写，或者销售额未填写，毛利润填写的情况,请修改后再提交,行号：" + erronenull6);
		}
		String erronenull7 = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(mhd_detno) from MerchandisingDetail left join Merchandising on mhd_mhid=mh_id "
						+ "where mh_id=? and ((mhd_qty7>0 and mhd_total7=0) or (mhd_qty7=0 and mhd_total7>0))",
						String.class, mh_id);
		if (erronenull7 != null){
			BaseUtil.showError("7月份出现销售额填写，毛利润没填写，或者销售额未填写，毛利润填写的情况,请修改后再提交,行号：" + erronenull7);
		}
		String erronenull8 = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(mhd_detno) from MerchandisingDetail left join Merchandising on mhd_mhid=mh_id "
						+ "where mh_id=? and ((mhd_qty8>0 and mhd_total8=0) or (mhd_qty8=0 and mhd_total8>0))",
						String.class, mh_id);
		if (erronenull8 != null){
			BaseUtil.showError("8月份出现销售额填写，毛利润没填写，或者销售额未填写，毛利润填写的情况,请修改后再提交,行号：" + erronenull8);
		}
		String erronenull9 = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(mhd_detno) from MerchandisingDetail left join Merchandising on mhd_mhid=mh_id "
						+ "where mh_id=? and ((mhd_qty9>0 and mhd_total9=0) or (mhd_qty9=0 and mhd_total9>0))",
						String.class, mh_id);
		if (erronenull9 != null){
			BaseUtil.showError("9月份出现销售额填写，毛利润没填写，或者销售额未填写，毛利润填写的情况,请修改后再提交,行号：" + erronenull9);
		}
		String erronenull10 = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(mhd_detno) from MerchandisingDetail left join Merchandising on mhd_mhid=mh_id "
						+ "where mh_id=? and ((mhd_qty10>0 and mhd_total10=0) or (mhd_qty10=0 and mhd_total10>0))",
						String.class, mh_id);
		if (erronenull10 != null){
			BaseUtil.showError("10月份出现销售额填写，毛利润没填写，或者销售额未填写，毛利润填写的情况,请修改后再提交,行号：" + erronenull10);
		}
		String erronenull11 = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(mhd_detno) from MerchandisingDetail left join Merchandising on mhd_mhid=mh_id "
						+ "where mh_id=? and ((mhd_qty11>0 and mhd_total11=0) or (mhd_qty11=0 and mhd_total11>0))",
						String.class, mh_id);
		if (erronenull11 != null){
			BaseUtil.showError("11月份出现销售额填写，毛利润没填写，或者销售额未填写，毛利润填写的情况,请修改后再提交,行号：" + erronenull11);
		}
		String erronenull12 = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(mhd_detno) from MerchandisingDetail left join Merchandising on mhd_mhid=mh_id "
						+ "where mh_id=? and ((mhd_qty12>0 and mhd_total12=0) or (mhd_qty12=0 and mhd_total12>0))",
						String.class, mh_id);
		if (erronenull12 != null){
			BaseUtil.showError("12月份出现销售额填写，毛利润没填写，或者销售额未填写，毛利润填写的情况,请修改后再提交,行号：" + erronenull12);
		}

		// 执行提交操作
		baseDao.updateByCondition(
				"Merchandising",
				"mh_statuscode='COMMITED',mh_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "mh_id="
						+ mh_id);
		// 记录操作
		baseDao.logger.submit(caller, "mh_id", mh_id);
		handlerService.afterSubmit(caller, mh_id);

	}

	@Override
	public void resSubmitPlanDising(int mh_id, String caller) {

		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Merchandising",
				"mh_statuscode", "mh_id=" + mh_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, mh_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"Merchandising",
				"mh_statuscode='ENTERING',mh_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "mh_id="
						+ mh_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "mh_id", mh_id);
		handlerService.afterResSubmit(caller, mh_id);
	}

}
