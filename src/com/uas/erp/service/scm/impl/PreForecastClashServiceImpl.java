package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.PreForecastClashService;
import com.uas.erp.service.scm.SaleClashService;

@Service("preForecastClashService")
public class PreForecastClashServiceImpl implements PreForecastClashService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Autowired
	private SaleClashService saleClashService;

	@Override
	public void savePreForecastClash(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("PreForecastClash", "pfc_code='" + store.get("pfc_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, grid });
		// 保存PreForecastClash
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PreForecastClash", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存PreForecastClashDetail
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "PreForecastClashdetail", "pfd_id");
		baseDao.execute(gridSql);
		update(store.get("pfc_id"));
		// 记录操作
		baseDao.logger.save(caller, "pfc_id", store.get("pfc_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, grid });
	}

	@Override
	public void deletePreForecastClash(String caller, int pfc_id) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("PreForecastClash", "pfc_statuscode", "pfc_id=" + pfc_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler("PreForecastClash", "delete", "before", new Object[] { pfc_id });
		// 删除PreForecastClash
		baseDao.deleteById("PreForecastClash", "pfc_id", pfc_id);
		baseDao.deleteById("PreForecastClashDetail", "pfd_pfcid", pfc_id);
		// 记录操作
		baseDao.logger.delete(caller, "pfc_id", pfc_id);
		// 执行删除后的其它逻辑
		handlerService.handler("PreForecastClash", "delete", "after", new Object[] { pfc_id });
	}

	@Override
	public void updatePreForecastClashById(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("PreForecastClash", "pfc_statuscode", "pfc_id=" + store.get("pfc_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "PreForecastClash", "pfc_id"));
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "PreForecastClashDetail", "pfd_id"));
		update(store.get("pfc_id"));
		// 记录操作
		baseDao.logger.update(caller, "pfc_id", store.get("pfc_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
	}

	private void update(Object id) {
		baseDao.execute(
				"update PreForecastClash set pfc_month1=to_char(ADD_MONTHS(sysdate,0),'YYYYMM') where pfc_id=? and nvl(pfc_month1,0)=0", id);
		baseDao.execute(
				"update PreForecastClash set pfc_month2=to_char(ADD_MONTHS(sysdate,1),'YYYYMM') where pfc_id=? and nvl(pfc_month2,0)=0", id);
		baseDao.execute(
				"update PreForecastClash set pfc_month3=to_char(ADD_MONTHS(sysdate,2),'YYYYMM') where pfc_id=? and nvl(pfc_month3,0)=0", id);
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select * from PreForecastClash left join PreForecastClashDetail on pfc_id=pfd_pfcid where pfd_pfcid=? and nvl(pfd_prodcode,' ')<>' ' and nvl(pfc_sellercode, ' ')<>' '",
						id);
		while (rs.next()) {
			Object prodcode = rs.getObject("pfd_prodcode");
			Object sellercode = rs.getObject("pfc_sellercode");
			int month1 = rs.getGeneralInt("pfc_month1");
			int month2 = rs.getGeneralInt("pfc_month2");
			int month3 = rs.getGeneralInt("pfc_month3");
			int pfd_id = rs.getGeneralInt("pfd_id");
			baseDao.execute(
					"update PreForecastClashDetail set PFD_FORECASTQTY1=nvl((select sum(NVL(sd_qty,0)+NVL(sd_clashsaleqty,0)) from saleforecast,saleforecastdetail where sf_id=sd_sfid and sd_prodcode='"
							+ prodcode
							+ "' and sf_statuscode='AUDITED' and to_char(sd_needdate,'YYYYMM')="
							+ month1
							+ " and sd_sellercode='"
							+ sellercode
							+ "'),0)-nvl((select NVL(sum(case when pfc_month1='"
							+ month1
							+ "' then nvl(pfd_cancelqty1,0) else 0 end),0)+NVL(sum(case when pfc_month2='"
							+ month1
							+ "' then nvl(pfd_cancelqty2,0) else 0 end),0)+NVL(sum(case when pfc_month3='"
							+ month1
							+ "' then nvl(pfd_cancelqty3,0) else 0 end),0) from PreForecastClashDetail A,PreForecastClash B where A.pfd_pfcid=B.pfc_id and A.pfd_prodcode='"
							+ prodcode + "' and B.pfc_sellercode='" + sellercode + "' and B.pfc_id<>" + id + "),0) where pfd_id=?", pfd_id);
			baseDao.execute(
					"update PreForecastClashDetail set PFD_FORECASTQTY2=nvl((select sum(NVL(sd_qty,0)+NVL(sd_clashsaleqty,0)) from saleforecast,saleforecastdetail where sf_id=sd_sfid and sd_prodcode='"
							+ prodcode
							+ "' and sf_statuscode='AUDITED' and to_char(sd_needdate,'YYYYMM')="
							+ month2
							+ " and sd_sellercode='"
							+ sellercode
							+ "'),0)-nvl((select NVL(sum(case when pfc_month1='"
							+ month2
							+ "' then nvl(pfd_cancelqty1,0) else 0 end),0)+NVL(sum(case when pfc_month2='"
							+ month2
							+ "' then nvl(pfd_cancelqty2,0) else 0 end),0)+NVL(sum(case when pfc_month3='"
							+ month2
							+ "' then nvl(pfd_cancelqty3,0) else 0 end),0) from PreForecastClashDetail A,PreForecastClash B where A.pfd_pfcid=B.pfc_id and A.pfd_prodcode='"
							+ prodcode + "' and B.pfc_sellercode='" + sellercode + "' and B.pfc_id<>" + id + "),0) where pfd_id=?", pfd_id);
			baseDao.execute(
					"update PreForecastClashDetail set PFD_FORECASTQTY3=nvl((select sum(NVL(sd_qty,0)+NVL(sd_clashsaleqty,0)) from saleforecast,saleforecastdetail where sf_id=sd_sfid and sd_prodcode='"
							+ prodcode
							+ "' and sf_statuscode='AUDITED' and to_char(sd_needdate,'YYYYMM')="
							+ month3
							+ " and sd_sellercode='"
							+ sellercode
							+ "'),0)-nvl((select NVL(sum(case when pfc_month1='"
							+ month3
							+ "' then nvl(pfd_cancelqty1,0) else 0 end),0)+NVL(sum(case when pfc_month2='"
							+ month3
							+ "' then nvl(pfd_cancelqty2,0) else 0 end),0)+NVL(sum(case when pfc_month3='"
							+ month3
							+ "' then nvl(pfd_cancelqty3,0) else 0 end),0) from PreForecastClashDetail A,PreForecastClash B where A.pfd_pfcid=B.pfc_id and A.pfd_prodcode='"
							+ prodcode + "' and B.pfc_sellercode='" + sellercode + "' and B.pfc_id<>" + id + "),0) where pfd_id=?", pfd_id);
		}
	}

	private void createForecastClash(Object id) {
		SqlRowList rs = baseDao.queryForRowSet(
				"select * from PreForecastClash left join PreForecastClashDetail on pfc_id=pfd_pfcid where pfd_pfcid=?", id);
		String clashcode = null;
		int csid = 0;
		double thisqty = 0, clashedqty = 0, cancelqty1 = 0, cancelqty2 = 0, cancelqty3 = 0;
		int detno = 1, month1 = 0, month2 = 0, month3 = 0;
		SqlRowList forecast = null;
		Object prodcode = null;
		while (rs.next()) {
			prodcode = rs.getObject("pfd_prodcode");
			cancelqty1 = rs.getGeneralDouble("pfd_cancelqty1");
			cancelqty2 = rs.getGeneralDouble("pfd_cancelqty2");
			cancelqty3 = rs.getGeneralDouble("pfd_cancelqty3");
			month1 = rs.getGeneralInt("pfc_month1");
			month2 = rs.getGeneralInt("pfc_month2");
			month3 = rs.getGeneralInt("pfc_month3");
			if (clashcode == null) {
				clashcode = baseDao.sGetMaxNumber("SaleClash", 2);
				csid = baseDao.getSeqId("SALECLASH_SEQ");
				baseDao.execute("insert into SaleClash(sc_id,sc_code,sc_date,sc_status,sc_statuscode,sc_recorder,sc_source,sc_sourceid,sc_sourcecode)values("
						+ csid
						+ ",'"
						+ clashcode
						+ "',sysdate,'"
						+ BaseUtil.getLocalMessage("COMMITED")
						+ "','COMMITED','"
						+ SystemSession.getUser().getEm_name() + "','业务员预测调整单'," + id + ",'" + rs.getString("pfc_code") + "')");
			}
			if (cancelqty1 > 0) {
				clashedqty = 0.0;
				forecast = baseDao
						.queryForRowSet(
								"select sf_code,sd_detno,sd_qty from saleforecast,saleforecastdetail where sf_id=sd_sfid and nvl(sd_qty,0)>0 and sf_statuscode='AUDITED' and sd_prodcode=? and to_char(sd_needdate,'yyyymm')= ? order by sd_needdate,sd_id",
								prodcode, month1);
				while (forecast.next()) {
					if (clashedqty < cancelqty1) {
						double remain = forecast.getGeneralDouble("sd_qty");
						thisqty = remain > cancelqty1 - clashedqty ? cancelqty1 - clashedqty : remain;
						baseDao.execute("insert into SaleClashDetail(scd_id,scd_scid,scd_detno,scd_prodcode,scd_clashqty,scd_ordercode,scd_orderdetno,scd_sourcedetid,scd_fromcode,scd_cancelid)values("
								+ "SALECLASHDETAIL_SEQ.NEXTVAL,"
								+ csid
								+ ",'"
								+ detno
								+ "','"
								+ prodcode
								+ "','"
								+ thisqty
								+ "','"
								+ forecast.getString("sf_code")
								+ "','"
								+ forecast.getInt("sd_detno")
								+ "',"
								+ rs.getInt("pfd_id")
								+ ",'"
								+ rs.getString("pfc_code") + "',0)");
						detno = detno + 1;
						clashedqty = clashedqty + thisqty;
					}
				}
			}
			if (cancelqty2 > 0) {
				clashedqty = 0.0;
				forecast = baseDao
						.queryForRowSet(
								"select sf_code,sd_detno,sd_qty from saleforecast,saleforecastdetail where sf_id=sd_sfid and nvl(sd_qty,0)>0 and sf_statuscode='AUDITED' and sd_prodcode=? and to_char(sd_needdate,'yyyymm')= ? order by sd_needdate,sd_id",
								prodcode, month2);
				while (forecast.next()) {
					if (clashedqty < cancelqty2) {
						double remain = forecast.getGeneralDouble("sd_qty");
						thisqty = remain > cancelqty2 - clashedqty ? cancelqty2 - clashedqty : remain;
						baseDao.execute("insert into SaleClashDetail(scd_id,scd_scid,scd_detno,scd_prodcode,scd_clashqty,scd_ordercode,scd_orderdetno,scd_sourcedetid,scd_fromcode,scd_cancelid)values("
								+ "SALECLASHDETAIL_SEQ.NEXTVAL,"
								+ csid
								+ ",'"
								+ detno
								+ "','"
								+ prodcode
								+ "','"
								+ thisqty
								+ "','"
								+ forecast.getString("sf_code")
								+ "','"
								+ forecast.getInt("sd_detno")
								+ "',"
								+ rs.getInt("pfd_id")
								+ ",'"
								+ rs.getString("pfc_code") + "',0)");
						detno = detno + 1;
						clashedqty = clashedqty + thisqty;
					}
				}
			}
			if (cancelqty3 > 0) {
				clashedqty = 0.0;
				forecast = baseDao
						.queryForRowSet(
								"select sf_code,sd_detno,sd_qty from saleforecast,saleforecastdetail where sf_id=sd_sfid and nvl(sd_qty,0)>0 and sf_statuscode='AUDITED' and sd_prodcode=? and to_char(sd_needdate,'yyyymm')= ? order by sd_needdate,sd_id",
								prodcode, month3);
				while (forecast.next()) {
					if (clashedqty < cancelqty3) {
						double remain = forecast.getGeneralDouble("sd_qty");
						thisqty = remain > cancelqty3 - clashedqty ? cancelqty3 - clashedqty : remain;
						baseDao.execute("insert into SaleClashDetail(scd_id,scd_scid,scd_detno,scd_prodcode,scd_clashqty,scd_ordercode,scd_orderdetno,scd_sourcedetid,scd_fromcode,scd_cancelid)values("
								+ "SALECLASHDETAIL_SEQ.NEXTVAL,"
								+ csid
								+ ",'"
								+ detno
								+ "','"
								+ prodcode
								+ "','"
								+ thisqty
								+ "','"
								+ forecast.getString("sf_code")
								+ "','"
								+ forecast.getInt("sd_detno")
								+ "',"
								+ rs.getInt("pfd_id")
								+ ",'"
								+ rs.getString("pfc_code") + "',0)");
						detno = detno + 1;
						clashedqty = clashedqty + thisqty;
					}
				}
			}
		}
		if (clashcode != null && !"".equals(clashcode)) {
			// 审核冲销单
			saleClashService.auditSaleClash(csid, "SaleClash");
		}
	}

	@Override
	@Transactional
	public void auditPreForecastClash(int pfc_id,String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("PreForecastClash", "pfc_statuscode", "pfc_id=" + pfc_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { pfc_id });
		createForecastClash(pfc_id);
		// 执行审核操作
		baseDao.audit("PreForecastClash", "pfc_id=" + pfc_id, "pfc_status", "pfc_statuscode", "pfc_auditdate", "pfc_auditman");
		/**
		 * wusy
		 */
		SqlRowList rs = null;
		rs = baseDao.queryForRowSet("select pfd_qty,pfd_sdid from  PreForecastClashDetail where pfd_pfcid="+pfc_id);
		while (rs.next()) {
			baseDao.updateByCondition("PreSaleForecastDetail ", "sd_qty='"+rs.getObject("pfd_qty")+"'", "sd_id="+rs.getObject("pfd_sdid"));//更新数量到业务员预测
			Object source_id = baseDao.getFieldDataByCondition("PreSaleForecastDetail ", "sd_sourceid", "sd_id="+rs.getObject("pfd_sdid"));//销售预测id
			Object sum = baseDao.getFieldDataByCondition("PreSaleForecastDetail ", "sum(sd_qty)", "sd_sourceid="+source_id);//计算更新对应销售预测数量
			baseDao.updateByCondition("SaleForecastDetail ", "sd_nowsellerqty='"+sum+"',sd_changremark='业务员预测调整'||to_char(sysdate,'YYYYMMDD')", "sd_id="+source_id);
		}
		// 记录操作
		baseDao.logger.audit(caller, "pfc_id", pfc_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { pfc_id });
	}

	@Override
	public void resAuditPreForecastClash(String caller, int pfc_id) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("PreForecastClash", "pfc_statuscode", "pfc_id=" + pfc_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核前的其它逻辑
		handlerService.handler(caller, "resAudit", "before", new Object[] { pfc_id });
		// 执行反审核操作
		baseDao.resAudit("PreForecastClash", "pfc_id=" + pfc_id, "pfc_status", "pfc_statuscode", "pfc_auditdate", "pfc_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "pfc_id", pfc_id);
		// 执行反审核后的其它逻辑
		handlerService.handler(caller, "resAudit", "after", new Object[] { pfc_id });
	}

	@Override
	public void submitPreForecastClash(String caller, int pfc_id) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("PreForecastClash", "pfc_statuscode", "pfc_id=" + pfc_id);
		StateAssert.submitOnlyEntering(status);
		//wusy 华商龙业务预测调整单取消其他提交逻辑
		/*update(pfc_id);
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select pfc_sellercode,pfd_prodcode,pfd_id from PreForecastClash,PreForecastClashDetail where pfc_id=pfd_pfcid and pfc_id=?",
						pfc_id);
		while (rs.next()) {
			int count = baseDao.getCountByCondition("PreForecastClashDetail left join PreForecastClash on pfc_id=pfd_pfcid",
					"pfc_sellercode='" + rs.getObject("pfc_sellercode") + "' and pfd_prodcode='" + rs.getObject("pfd_prodcode")
							+ "' and pfc_statuscode<>'AUDITED' and pfc_id<>" + pfc_id);
			if (count > 1) {
				BaseUtil.showError("业务员号[" + rs.getObject("pfc_sellercode") + "],物料编号[" + rs.getObject("pfd_prodcode")
						+ "]有其它未审核的业务员预测调整单，不允许提交！");
			}
			count = baseDao.getCountByCondition("PreForecastClashDetail", "pfd_prodcode='" + rs.getObject("pfd_prodcode")
					+ "' and pfd_pfcid=" + pfc_id);
			if (count > 1) {
				BaseUtil.showError("物料编号[" + rs.getObject("pfd_prodcode") + "]在当前调整单存在多条明细，不允许提交！");
			}
		}*/
		/*String det = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat('<br>行['||pfd_detno||']年月['||pfc_month1||']取消数量['||nvl(pfd_cancelqty1,0)||']预测数量['||nvl(pfd_forecastqty1,0)||']') from PREFORECASTCLASH,PREFORECASTCLASHDetail where pfc_id=pfd_pfcid and nvl(pfd_cancelqty1,0)> nvl(pfd_forecastqty1,0) and pfd_pfcid=?",
						String.class, pfc_id);
		if (det != null) {
			BaseUtil.showError("该月取消数量不能大于该月预测数量！" + det);
		}*/
		/*det = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat('<br>行['||pfd_detno||']年月['||pfc_month2||']取消数量['||nvl(pfd_cancelqty2,0)||']预测数量['||nvl(pfd_forecastqty2,0)||']') from PREFORECASTCLASH,PREFORECASTCLASHDetail where pfc_id=pfd_pfcid and nvl(pfd_cancelqty2,0)> nvl(pfd_forecastqty2,0) and pfd_pfcid=?",
						String.class, pfc_id);
		if (det != null) {
			BaseUtil.showError("该月取消数量不能大于该月预测数量！" + det);
		}*/
		/*det = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat('<br>行['||pfd_detno||']年月['||pfc_month3||']取消数量['||nvl(pfd_cancelqty3,0)||']预测数量['||nvl(pfd_forecastqty3,0)||']') from PREFORECASTCLASH,PREFORECASTCLASHDetail where pfc_id=pfd_pfcid and nvl(pfd_cancelqty3,0)> nvl(pfd_forecastqty3,0) and pfd_pfcid=?",
						String.class, pfc_id);
		if (det != null) {
			BaseUtil.showError("该月取消数量不能大于该月预测数量！" + det);
		}*/
		/*det = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(pfd_detno) from PREFORECASTCLASHDetail where (nvl(pfd_cancelqty1,0)<0 or nvl(pfd_cancelqty2,0)<0 or nvl(pfd_cancelqty3,0)<0) and pfd_pfcid=?",
						String.class, pfc_id);
		if (det != null) {
			BaseUtil.showError("取消数量必须都是正数！行：" + det);
		}*/
		/*det = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(pfd_detno) from PREFORECASTCLASHDetail where nvl(pfd_cancelqty1,0)+nvl(pfd_cancelqty2,0)+nvl(pfd_cancelqty3,0)<=0 and pfd_pfcid=?",
						String.class, pfc_id);
		if (det != null) {
			BaseUtil.showError("取消数量不能为零！行：" + det);
		}*/
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { pfc_id });
		// 执行提交操作
		baseDao.submit("PreForecastClash", "pfc_id=" + pfc_id, "pfc_status", "pfc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pfc_id", pfc_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { pfc_id });
	}

	@Override
	public void resSubmitPreForecastClash(String caller, int pfc_id) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("PreForecastClash", "pfc_statuscode", "pfc_id=" + pfc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { pfc_id });
		// 执行反提交操作
		baseDao.resOperate("PreForecastClash", "pfc_id=" + pfc_id, "pfc_status", "pfc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pfc_id", pfc_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { pfc_id });
	}
}
