package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.SaleForeCastChangeDao;
import com.uas.erp.service.scm.SaleForeCastChangeService;

@Service("saleForeCastChangeService")
public class SaleForeCastChangeServiceImpl implements SaleForeCastChangeService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private SaleForeCastChangeDao SaleForeCastChangeDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveSaleForeCastChange(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("SaleForeCastChange", "sc_code='" + store.get("sc_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, grid });
		// 保存SaleForeCastChange
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "SaleForeCastChange", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存SaleForeCastChangeDetail
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "SaleForeCastChangeDetail", "scd_id");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "sc_id", store.get("sc_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, grid });
	}

	@Override
	public void deleteSaleForeCastChange(int sc_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("SaleForeCastChange", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { sc_id });
		// 删除SaleForeCastChange
		baseDao.deleteById("SaleForeCastChange", "sc_id", sc_id);
		// 删除SaleForeCastChangeDetail
		baseDao.deleteById("SaleForeCastChangedetail", "scd_mainid", sc_id);
		// 记录操作
		baseDao.logger.delete(caller, "sc_id", sc_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { sc_id });
	}

	@Override
	public void updateSaleForeCastChangeById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("SaleForeCastChange", "sc_statuscode", "sc_id=" + store.get("sc_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		// 修改SaleForeCastChange
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "SaleForeCastChange", "sc_id");
		baseDao.execute(formSql);
		// 修改SaleForeCastChangeDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "SaleForeCastChangeDetail", "scd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("scd_id") == null || s.get("scd_id").equals("") || s.get("scd_id").equals("0")
					|| Integer.parseInt(s.get("scd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("SaleForeCastChangeDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "SaleForeCastChangeDetail", new String[] { "scd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		baseDao.execute("update saleforecastchange set sc_sfid=(select sf_id from saleforecast where sc_sfcode=sf_code) where nvl(sc_sfid,0)=0 and sc_id="
				+ store.get("sc_id"));
		// 记录操作
		baseDao.logger.update(caller, "sc_id", store.get("sc_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
	}

	@Override
	public void printSaleForeCastChange(int sc_id, String caller) {
		// 执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[] { sc_id });
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "sc_id", sc_id);
		// 执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[] { sc_id });
	}

	@Override
	public void auditSaleForeCastChange(int sc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("SaleForeCastChange", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.auditOnlyCommited(status);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(scd_detno) from SaleForeCastChangeDetail where scd_mainid=? and trunc(scd_olddelivery) <> trunc(scd_newdelivery) and trunc(scd_newdelivery)<trunc(sysdate)",
						String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("新交货日期小于系统当前日期，不允许提交！行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(scd_detno) from SaleForeCastChangeDetail where scd_mainid=? and trunc(scd_oldenddate) <> trunc(scd_newenddate) and trunc(scd_newenddate)<trunc(sysdate)",
						String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("新截止日期小于系统当前日期，不允许提交！行号：" + dets);
		}
		// 判断旧预测数是否跟当前预测数一致，如果原数量已经发生变化，则不能变更。
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(scd_detno) from SaleForeCastChangeDetail left join SaleForeCastChange on scd_mainid=sc_id left join saleforecast on sc_sfcode=sf_code left join saleforecastdetail on sd_sfid=sf_id and sd_detno=scd_pddetno where sc_id=? and scd_oldqty<>sd_qty",
						String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("序号:" + dets + "的原单预测单数发生了变化，不能执行数量变更");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('订单号:'||sa_code||',行号:'||sd_detno||',预测单号:'||sd_forecastcode||',预测单行号:'||sd_forecastdetno) from (select sa_code,sd_detno,sd_forecastcode,sd_forecastdetno from Sale left join SaleDetail on sa_id=sd_said where nvl(sa_statuscode,' ')<>'AUDITED' and nvl(sa_statuscode,' ')<>'FINISH' and (sd_forecastcode,sd_forecastdetno) in (select sc_sfcode,scd_pddetno from SaleForeCastChange left join SaleForeCastChangeDetail on scd_mainid=sc_id where sc_id=?))",
						String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("预测单号所在的订单未审核，不允许提交!" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('预测单号:'||sf_code||',行号:'||sd_detno) from (select sf_code,sd_detno from SaleForecast left join SaleForecastDetail on sf_id=sd_sfid where nvl(sd_statuscode,' ')<>'AUDITED' and (sf_code,sd_detno) in (select sc_sfcode,scd_pddetno from SaleForeCastChange left join SaleForeCastChangeDetail on scd_mainid=sc_id where sc_id=?))",
						String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("预测单号+预测行号状态不等于已审核，不允许提交!" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('冲销单号:'||sc_code||',行号:'||scd_detno||',预测单号:'||scd_ordercode||',预测单行号:'||scd_orderdetno) from (select sc_code,scd_detno,scd_ordercode,scd_orderdetno from SaleClash left join SaleClashDetail on sc_id=scd_scid where nvl(sc_statuscode,' ')<>'AUDITED' and (scd_ordercode,scd_orderdetno) in (select sc_sfcode,scd_pddetno from SaleForeCastChange left join SaleForeCastChangeDetail on scd_mainid=sc_id where sc_id=?))",
						String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("预测单号所在的冲销单未审核，不允许提交!" + dets);
		}
		if (!baseDao.isDBSetting(caller, "MatchQty")) {
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select WM_CONCAT(scd_detno) from SaleForeCastChange left join SaleForeCastChangeDetail on sc_id=scd_mainid where nvl(scd_newqty,0)>nvl(scd_oldqty,0) and sc_id=?",
							String.class, sc_id);
			if (dets != null) {
				BaseUtil.showError("新预测数大于原预测数，不允许提交!行号：" + dets);
			}
		
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(scd_detno) from SaleForeCastChange left join SaleForeCastChangeDetail on sc_id=scd_mainid where (sc_sfcode,scd_pddetno) in (select sf_code,sd_detno from SaleForecast left join SaleForecastDetail on sf_id=sd_sfid where nvl(scd_newqty,0)<nvl(sd_yqty,0)) and sc_id=?",
						String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("新预测数量小于预测订单中转出数量，不允许提交!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(sc_code) from SaleForeCastChange left join SaleForecastchangedetail on sc_id=scd_mainid where nvl(sc_statuscode,' ')='COMMITED' and sc_id<>?"
								+ " and (sc_sfcode,scd_pddetno) in (select sc_sfcode,scd_pddetno from  SaleForeCastChange left join SaleForecastchangedetail on sc_id=scd_mainid where sc_id=?)",
						String.class, sc_id, sc_id);
		if (dets != null) {
			BaseUtil.showError("有已提交的销售预测变更单，不允许提交!变更单号：" + dets);
		}
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { sc_id });
		// 信息自动反馈到预测单
		int sfid = SaleForeCastChangeDao.turnSaleForecast(sc_id);
		if (sfid > 0) {
			// 执行审核操作
			baseDao.audit("SaleForeCastChange", "sc_id=" + sc_id, "sc_status", "sc_statuscode", "sc_auditdate", "sc_auditman");
			// 记录操作
			baseDao.logger.audit(caller, "sc_id", sc_id);
			// 执行审核后的其它逻辑
			handlerService.handler(caller, "audit", "after", new Object[] { sc_id });
			BaseUtil.appendError("AFTERSUCCESS信息已自动反馈到销售预测单&nbsp;&nbsp;"
					+ "<a href=\"javascript:openUrl('jsps/scm/sale/saleForecast.jsp?formCondition=sf_idIS" + sfid
					+ "&gridCondition=sd_sfidIS" + sfid + "')\">点击查看</a>&nbsp;");
		}
	}

	@Override
	public void resAuditSaleForeCastChange(int sc_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("SaleForeCastChange", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("SaleForeCastChange", "sc_id=" + sc_id, "sc_status", "sc_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "sc_id", sc_id);
	}

	@Override
	public void submitSaleForeCastChange(int sc_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("SaleForeCastChange", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { sc_id });
		// 执行提交操作
		baseDao.execute("update saleforecastchange set sc_sfid=(select sf_id from saleforecast where sc_sfcode=sf_code) where nvl(sc_sfid,0)=0 and sc_id="
				+ sc_id);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(scd_detno) from SaleForeCastChangeDetail where scd_mainid=? and trunc(scd_olddelivery) <> trunc(scd_newdelivery) and trunc(scd_newdelivery)<trunc(sysdate)",
						String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("新交货日期小于系统当前日期，不允许提交！行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(scd_detno) from SaleForeCastChangeDetail where scd_mainid=? and trunc(scd_oldenddate) <> trunc(scd_newenddate) and trunc(scd_newenddate)<trunc(sysdate)",
						String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("新截止日期小于系统当前日期，不允许提交！行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('订单号:'||sa_code||',行号:'||sd_detno||',预测单号:'||sd_forecastcode||',预测单行号:'||sd_forecastdetno) from (select sa_code,sd_detno,sd_forecastcode,sd_forecastdetno from Sale left join SaleDetail on sa_id=sd_said where nvl(sa_statuscode,' ')<>'AUDITED' and nvl(sa_statuscode,' ')<>'FINISH' and (sd_forecastcode,sd_forecastdetno) in (select sc_sfcode,scd_pddetno from SaleForeCastChange left join SaleForeCastChangeDetail on scd_mainid=sc_id where sc_id=?))",
						String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("预测单号所在的订单未审核，不允许提交!" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('预测单号:'||sf_code||',行号:'||sd_detno) from (select sf_code,sd_detno from SaleForecast left join SaleForecastDetail on sf_id=sd_sfid where nvl(sd_statuscode,' ')<>'AUDITED' and (sf_code,sd_detno) in (select sc_sfcode,scd_pddetno from SaleForeCastChange left join SaleForeCastChangeDetail on scd_mainid=sc_id where sc_id=?))",
						String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("预测单号+预测行号状态不等于已审核，不允许提交!" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('冲销单号:'||sc_code||',行号:'||scd_detno||',预测单号:'||scd_ordercode||',预测单行号:'||scd_orderdetno) from (select sc_code,scd_detno,scd_ordercode,scd_orderdetno from SaleClash left join SaleClashDetail on sc_id=scd_scid where nvl(sc_statuscode,' ')<>'AUDITED' and (scd_ordercode,scd_orderdetno) in (select sc_sfcode,scd_pddetno from SaleForeCastChange left join SaleForeCastChangeDetail on scd_mainid=sc_id where sc_id=?))",
						String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("预测单号所在的冲销单未审核，不允许提交!" + dets);
		}
		
		if (!baseDao.isDBSetting(caller, "MatchQty")) {
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select WM_CONCAT(scd_detno) from SaleForeCastChange left join SaleForeCastChangeDetail on sc_id=scd_mainid where nvl(scd_newqty,0)>nvl(scd_oldqty,0) and sc_id=?",
							String.class, sc_id);
			if (dets != null) {
				BaseUtil.showError("新预测数大于原预测数，不允许提交!行号：" + dets);
			}
		
		}
	
		// 判断旧预测数是否跟当前预测数一致，如果原数量已经发生变化，则不能变更。
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(scd_detno) from SaleForeCastChangeDetail left join SaleForeCastChange on scd_mainid=sc_id left join saleforecast on sc_sfcode=sf_code left join saleforecastdetail on sd_sfid=sf_id and sd_detno=scd_pddetno where sc_id=? and scd_oldqty<>sd_qty",
						String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("序号:" + dets + "的原单预测单数发生了变化，不能执行数量变更");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(scd_detno) from SaleForeCastChange left join SaleForeCastChangeDetail on sc_id=scd_mainid where (sc_sfcode,scd_pddetno) in (select sf_code,sd_detno from SaleForecast left join SaleForecastDetail on sf_id=sd_sfid where nvl(scd_newqty,0) <nvl(sd_yqty,0)) and sc_id=?",
						String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("新预测数量小于预测订单中转出数量，不允许提交!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(sc_code) from SaleForeCastChange left join SaleForecastchangedetail on sc_id=scd_mainid where nvl(sc_statuscode,' ')='COMMITED' and sc_id<>?"
								+ " and (sc_sfcode,scd_pddetno) in (select sc_sfcode,scd_pddetno from  SaleForeCastChange left join SaleForecastchangedetail on sc_id=scd_mainid where sc_id=?)",
						String.class, sc_id, sc_id);
		if (dets != null) {
			BaseUtil.showError("有已提交的销售预测变更单，不允许提交!变更单号：" + dets);
		}
		baseDao.submit("SaleForeCastChange", "sc_id=" + sc_id, "sc_status", "sc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "sc_id", sc_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { sc_id });
	}

	@Override
	public void resSubmitSaleForeCastChange(int sc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("SaleForeCastChange", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { sc_id });
		// 执行反提交操作
		baseDao.resOperate("SaleForeCastChange", "sc_id=" + sc_id, "sc_status", "sc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "sc_id", sc_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { sc_id });
	}
}
