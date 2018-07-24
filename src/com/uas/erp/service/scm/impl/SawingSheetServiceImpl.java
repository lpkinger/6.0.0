package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.SawingSheetDao;
import com.uas.erp.service.scm.ProdInOutService;
import com.uas.erp.service.scm.SawingSheetService;

@Service("sawingSheetService")
public class SawingSheetServiceImpl implements SawingSheetService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Autowired
	private SawingSheetDao sawingSheetDao;

	@Autowired
	private ProdInOutService prodInOutService;

	@Override
	public void saveSawingSheet(String formStore, String gridStore, String gridStore2, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> grid2 = BaseUtil.parseGridStoreToMaps(gridStore2);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("SawingSheet", "ss_code='" + store.get("ss_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存SawingSheet
		baseDao.execute(SqlUtil.getInsertSqlByFormStore(store, "SawingSheet", new String[] {}, new Object[] {}));
		// 保存SawingSheetBefore
		baseDao.execute(SqlUtil.getInsertSqlbyList(grid, "SawingSheetBefore", "ssb_id"));
		// 保存SawingSheetAfter
		baseDao.execute(SqlUtil.getInsertSqlbyList(grid2, "SawingSheetAfter", "ssa_id"));
		// 记录操作
		baseDao.logger.save(caller, "ss_id", store.get("ss_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteSawingSheet(String caller, int ss_id) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("SawingSheet", "ss_statuscode", "ss_id=" + ss_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { ss_id });
		// 删除SawingSheet
		baseDao.deleteById("SawingSheet", "ss_id", ss_id);
		baseDao.deleteById("SawingSheetBefore", "ssb_ssid", ss_id);
		baseDao.deleteById("SawingSheetAfter", "ssa_ssid", ss_id);
		// 记录操作
		baseDao.logger.delete(caller, "ss_id", ss_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { ss_id });
	}

	@Override
	public void updateSawingSheetById(String formStore, String gridStore, String gridStore2, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> gstore2 = BaseUtil.parseGridStoreToMaps(gridStore2);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("SawingSheet", "ss_statuscode", "ss_id=" + store.get("ss_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 修改SawingSheet
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "SawingSheet", "ss_id"));
		// 修改SawingSheetBefore
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "SawingSheetBefore", "ssb_id"));
		// 修改SawingSheetAfter
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore2, "SawingSheetAfter", "ssa_id"));
		// 记录操作
		baseDao.logger.update(caller, "ss_id", store.get("ss_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void auditSawingSheet(String caller, int ss_id) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("SawingSheet", "ss_statuscode", "ss_id=" + ss_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { ss_id });
		// 执行审核操作
		baseDao.audit("SawingSheet", "ss_id=" + ss_id, "ss_status", "ss_statuscode", "ss_auditdate", "ss_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "ss_id", ss_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { ss_id });
	}

	@Override
	public void resAuditSawingSheet(String caller, int ss_id) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("SawingSheet", "ss_statuscode", "ss_id=" + ss_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核前的其它逻辑
		handlerService.handler(caller, "resAudit", "before", new Object[] { ss_id });
		// 执行反审核操作
		baseDao.resAudit("SawingSheet", "ss_id=" + ss_id, "ss_status", "ss_statuscode", "ss_auditdate", "ss_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "ss_id", ss_id);
		// 执行反审核后的其它逻辑
		handlerService.handler(caller, "resAudit", "after", new Object[] { ss_id });
	}

	@Override
	public void submitSawingSheet(String caller, int ss_id) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("SawingSheet", "ss_statuscode", "ss_id=" + ss_id);
		StateAssert.submitOnlyEntering(status);
		beforeCheck(ss_id);
		// 开料前明细的总采购单位数量【每一行的转换率*出库数量】A
		double A = baseDao.getSummaryByField("SawingSheetBefore left join Product on ssb_prodcode=pr_code",
				"round(nvl(ssb_outqty,0) * nvl(pr_purcrate,0),2)", "ssb_ssid=" + ss_id);
		// 开料后明细的总采购单位数量【每一行的转换率*入库数量】B
		double B = baseDao.getSummaryByField("SawingSheetAfter left join Product on ssa_prodcode=pr_code",
				"round(nvl(ssa_inqty,0) * nvl(pr_purcrate,0),2)", "ssa_ssid=" + ss_id);
		// 开料前的采购单位的平均单价C【C=sum(开料前批单价*出库数)/sum(出库数)/开料前转换率】
		double outqty = baseDao.getSummaryByField("SawingSheetBefore", "nvl(ssb_outqty,0)", "ssb_ssid=" + ss_id);
		double outamount = baseDao.getSummaryByField("SawingSheetBefore", "round(nvl(ssb_price,0)*nvl(ssb_outqty,0),2)", "ssb_ssid="
				+ ss_id);
		double C = 0.0;
		Object purcrate = baseDao.getFieldDataByCondition("SawingSheetBefore left join Product on ssb_prodcode=pr_code",
				"max(nvl(pr_purcrate,0))", "ssb_ssid=" + ss_id);
		if (purcrate != null && Double.parseDouble(purcrate.toString()) != 0) {
			C = NumberUtil.formatDouble(outamount / outqty / Double.parseDouble(purcrate.toString()), 8);
		}
		SqlRowList rs = baseDao.queryForRowSet(
				"select ssa_id, pr_purcrate from SawingSheetAfter left join Product on ssa_prodcode=pr_code where ssa_ssid=?", ss_id);
		while (rs.next()) {
			// 开料后明细中每一行的成本单价D=转换率*C*A/B
			if (B != 0) {
				double D = rs.getGeneralDouble("pr_purcrate") * C * A / B;
				baseDao.execute("update SawingSheetAfter set ssa_price=round(" + D + ",8) where ssa_id=" + rs.getInt("ssa_id"));
			}
		}
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { ss_id });
		// 执行提交操作
		baseDao.submit("SawingSheet", "ss_id=" + ss_id, "ss_status", "ss_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "ss_id", ss_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { ss_id });
	}

	private void beforeCheck(int ss_id) {
		int count = baseDao.getCount("select count(*) from SawingSheetBefore where ssb_ssid=" + ss_id);
		if (count == 0) {
			BaseUtil.showError("开料前明细为空!");
		}
		count = baseDao.getCount("select count(*) from SawingSheetAfter where ssa_ssid=" + ss_id);
		if (count == 0) {
			BaseUtil.showError("开料后明细为空!");
		}
		count = baseDao.getCount("select count(distinct ssb_prodcode) from SawingSheetBefore where ssb_ssid=" + ss_id);
		if (count > 1) {
			BaseUtil.showError("开料前明细只能是同一个产品!");
		}
		// 判断明细行物料转换率是否为0
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ssb_detno) from SawingSheetBefore left join Product on ssb_prodcode=pr_code where ssb_ssid=? and nvl(pr_purcrate,0)=0",
						String.class, ss_id);
		if (dets != null) {
			BaseUtil.showError("开料前明细物料转换率不能为0！行:" + dets);
		}
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(ssb_detno) from SawingSheetBefore where ssb_ssid=? and nvl(ssb_price,0)=0", String.class, ss_id);
		if (dets != null) {
			BaseUtil.showError("开料前明细单价不能为0！行:" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ssa_detno) from SawingSheetAfter left join Product on ssa_prodcode=pr_code where ssa_ssid=? and nvl(pr_purcrate,0)=0",
						String.class, ss_id);
		if (dets != null) {
			BaseUtil.showError("开料后明细物料转换率不能为0！行:" + dets);
		}
		double bqty = baseDao.getSummaryByField("SawingSheetBefore left join Product on ssb_prodcode=pr_code ",
				"round(nvl(ssb_outqty,0)*nvl(pr_purcrate,0),2)", "ssb_ssid=" + ss_id);
		double aqty = baseDao.getSummaryByField("SawingSheetAfter left join Product on ssa_prodcode=pr_code ",
				"round(nvl(ssa_inqty,0)*nvl(pr_purcrate,0),2)", "ssa_ssid=" + ss_id);
		if (aqty > bqty) {
			BaseUtil.showError("开料后明细数量*转换率不能大于开料前明细数量*转换率！");
		}
		// 判断开料前明细批号是否为空
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(ssb_detno) from SawingSheetBefore where ssb_ssid=? and nvl(ssb_batchcode,' ')=' '", String.class,
				ss_id);
		if (dets != null) {
			BaseUtil.showError("开料前明细未填写批号,行:" + dets);
		}
		// 如果开料后明细行批号没填自动产生批号
		baseDao.execute("update SawingSheetAfter set ssa_batchcode='" + baseDao.getBatchcode("ProdInOut!OtherIn") + "' where ssa_ssid="
				+ ss_id + " and nvl(ssa_batchcode,' ')=' '");
		// 开料后明细行批号，同一物料同仓库不能同时入两次相同的批号
		SqlRowList rs = baseDao
				.queryForRowSet("select  count(1)n, wm_concat(ssa_detno) detno from (select ssa_batchcode,ssa_whcode,ssa_prodcode,min(ssa_detno) ssa_detno,count(1)c from SawingSheetAfter where ssa_ssid="
						+ ss_id + " and ssa_batchcode<>' ' group by ssa_batchcode,ssa_whcode,ssa_prodcode ) where c> 1");
		if (rs.next()) {
			if (rs.getInt("n") > 0) {
				BaseUtil.showError("同一物料同仓库不能同时入两次相同的批号！行号：" + rs.getString("detno"));
			}
		}
		rs = baseDao
				.queryForRowSet("select count(1)n, wm_concat(ssa_detno) detno from SawingSheetAfter where ssa_ssid="
						+ ss_id
						+ " and ssa_batchcode is not null and exists (select 1 from prodiodetail where pd_batchcode=ssa_batchcode and pd_prodcode=ssa_prodcode and pd_whcode=ssa_whcode and nvl(pd_inqty,0)<>0 and nvl(pd_status,0)=0)");
		if (rs.next()) {
			if (rs.getInt("n") > 0) {
				BaseUtil.showError("批号已存在入库单，不能重复入库！行：" + rs.getString("detno"));
			}
		}
		rs = baseDao
				.queryForRowSet("select count(1)n, wm_concat(ssa_detno) detno from SawingSheetAfter where ssa_ssid="
						+ ss_id
						+ " and ssa_batchcode is not null and exists (select 1 from batch where ba_code=ssa_batchcode and ba_prodcode=ssa_prodcode and ba_whcode=ssa_whcode and (nvl(ba_remain,0)<>0 or nvl(ba_inqty,0)<>0))");
		if (rs.next()) {
			if (rs.getInt("n") > 0) {
				BaseUtil.showError("批号已存在，不能重复入库！行：" + rs.getString("detno"));
			}
		}
		// 物料当前等级库存不可用
		rs = baseDao.queryForRowSet("select wm_concat(ssb_prodcode)  prodcode from (select distinct ssb_prodcode from SawingSheetBefore "
				+ "left join product on ssb_prodcode=pr_code left join productlevel on pr_level=pl_levcode where ssb_ssid=? and pl_id>0 "
				+ "and pl_isuseable=0 ) where rownum<=20", ss_id);
		if (rs.next()) {
			if (rs.getString("prodcode") != null) {
				BaseUtil.showError(BaseUtil.getLocalMessage("开料前明细行物料当前等级库存不可用,物料编号：" + rs.getString("prodcode")));
			}
		}
		rs = baseDao.queryForRowSet("select wm_concat(ssa_prodcode)  prodcode from (select distinct ssa_prodcode from SawingSheetAfter "
				+ "left join product on ssa_prodcode=pr_code left join productlevel on pr_level=pl_levcode where ssa_ssid=? and pl_id>0 "
				+ "and pl_isuseable=0 ) where rownum<=20", ss_id);
		if (rs.next()) {
			if (rs.getString("prodcode") != null) {
				BaseUtil.showError(BaseUtil.getLocalMessage("开料后明细行物料当前等级库存不可用,物料编号：" + rs.getString("prodcode")));
			}
		}
	}

	@Override
	public void resSubmitSawingSheet(String caller, int ss_id) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("SawingSheet", "ss_statuscode", "ss_id=" + ss_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { ss_id });
		// 执行反提交操作
		baseDao.resOperate("SawingSheet", "ss_id=" + ss_id, "ss_status", "ss_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "ss_id", ss_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { ss_id });
	}

	@Override
	public void postSawingSheet(String caller, int ss_id) {
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "post", "before", new Object[] { ss_id });
		beforeCheck(ss_id);
		// 执行过账操作
		String det = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(pi_class||'号：'||pi_inoutno) from prodinout where pi_fromcode=(select ss_code from sawingsheet where ss_id=?) and pi_type in ('开料入库','开料出库')",
						String.class, ss_id);
		if (det != null) {
			BaseUtil.showError("已经转入其它出入库单，请先删除单据！<br>" + det);
		}
		baseDao.execute("update SawingSheet set ss_statuscode='POSTED', ss_status='" + BaseUtil.getLocalMessage("POSTED")
				+ "' where ss_id=" + ss_id);
		Object outcode = null;
		Object incode = null;
		// 开料前明细行转其它出库单
		JSONObject out = sawingSheetDao.turnProdInOut(ss_id, "其它出库单");
		if (out != null) {
			sawingSheetDao.turnProdIODetail(ss_id, "其它出库单", out.get("pi_id"), out.get("pi_inoutno"));
			outcode = out.get("pi_inoutno");
		}
		// 开料后明细行转其它入库单
		JSONObject in = sawingSheetDao.turnProdInOut(ss_id, "其它入库单");
		if (in != null) {
			sawingSheetDao.turnProdIODetail(ss_id, "其它入库单", in.get("pi_id"), in.get("pi_inoutno"));
			incode = in.get("pi_inoutno");
		}
		if (outcode != null) {
			prodInOutService.postProdInOut(Integer.parseInt(out.get("pi_id").toString()), "ProdInOut!OtherOut");
		}
		if (incode != null) {
			prodInOutService.postProdInOut(Integer.parseInt(in.get("pi_id").toString()), "ProdInOut!OtherIn");
		}
		// 记录操作
		baseDao.logger.audit(caller, "ss_id", ss_id);
		// 执行过账后的其它逻辑
		handlerService.handler(caller, "post", "after", new Object[] { ss_id });
	}

	@Override
	public void resPostSawingSheet(String caller, int ss_id) {
		// 只能对状态为[已过账]的订单进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("SawingSheet", new String[] { "ss_statuscode", "ss_code" }, "ss_id=" + ss_id);
		StateAssert.resPostOnlyPosted(status[0]);
		String det = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(pi_class||'号：'||pi_inoutno) from prodinout where pi_fromcode=? and pi_type in ('开料入库','开料出库')",
				String.class, status[1]);
		if (det != null) {
			BaseUtil.showError("已经转入其它出入库单，请先删除单据！<br>" + det);
		}
		// 执行反审核前的其它逻辑
		handlerService.handler(caller, "resPost", "before", new Object[] { ss_id });
		// 执行反审核操作
		baseDao.execute("update SawingSheet set ss_statuscode='AUDITED', ss_status='" + BaseUtil.getLocalMessage("AUDITED")
				+ "' where ss_id=" + ss_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ss_id", ss_id);
		// 执行反审核后的其它逻辑
		handlerService.handler(caller, "resPost", "after", new Object[] { ss_id });
	}

}
