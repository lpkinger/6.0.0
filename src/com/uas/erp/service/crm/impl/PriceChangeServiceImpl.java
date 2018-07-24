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
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;

import com.uas.erp.service.crm.PriceChangeService;

@Service
public class PriceChangeServiceImpl implements PriceChangeService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void savePriceChange(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("PriceChange", "pc_code='"
				+ store.get("pc_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 保存PriceChange
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PriceChange",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存PriceChangeDetail
		Object[] pcd_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			pcd_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				pcd_id[i] = baseDao.getSeqId("PriceChangeDETAIL_SEQ");
			}
		} else {
			pcd_id[0] = baseDao.getSeqId("PriceChangeDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore,
				"PriceChangeDetail", "pcd_id", pcd_id);
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "pc_id", store.get("pc_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void updatePriceChangeById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("PriceChange",
				"pc_statuscode", "pc_id=" + store.get("pc_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改PriceChange
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PriceChange",
				"pc_id");
		baseDao.execute(formSql);
		// 修改PriceChangeDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"PriceChangeDetail", "pcd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("pcd_id") == null || s.get("pcd_id").equals("")
					|| s.get("pcd_id").equals("0")
					|| Integer.parseInt(s.get("pcd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("PriceChangeDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "PriceChangeDetail",
						new String[] { "pcd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "pc_id", store.get("pc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void deletePriceChange(int pc_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("PriceChange",
				"pc_statuscode", "pc_id=" + pc_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, pc_id);
		// 删除PriceChange
		baseDao.deleteById("PriceChange", "pc_id", pc_id);
		// 删除PriceChangeDetail
		baseDao.deleteById("PriceChangedetail", "pcd_pcid", pc_id);
		// 记录操作
		baseDao.logger.delete(caller, "pc_id", pc_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, pc_id);
	}

	@Override
	public void auditPriceChange(int pc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("PriceChange",
				"pc_statuscode", "pc_id=" + pc_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, pc_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"PriceChange",
				"pc_statuscode='AUDITED',pc_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',pc_auditname='"
						+ SystemSession.getUser().getEm_name()
						+ "',pc_auditdate="
						+ DateUtil.parseDateToOracleString(Constant.YMD_HMS,
								new Date()), "pc_id=" + pc_id);
		// 信息自动反馈到价格单
		turnSalePrice(pc_id);
		// 记录操作
		baseDao.logger.audit(caller, "pc_id", pc_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, pc_id);
	}

	@Override
	public void resAuditPriceChange(int pc_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("PriceChange",
				"pc_statuscode", "pc_id=" + pc_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, pc_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"PriceChange",
				"pc_statuscode='ENTERING',pc_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "pc_id="
						+ pc_id);
		resTurnSalePrice(pc_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "pc_id", pc_id);
		handlerService.afterResAudit(caller, pc_id);
	}

	@Override
	public void submitPriceChange(int pc_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("PriceChange",
				"pc_statuscode", "pc_id=" + pc_id);
		StateAssert.submitOnlyEntering(status);
		// 只能选择已审核的物料!
		List<Object> codes = baseDao.getFieldDatasByCondition(
				"PriceChangeDetail", "pcd_prodcode", "pcd_pcid=" + pc_id);
		for (Object c : codes) {
			status = baseDao.getFieldDataByCondition("Product",
					"pr_statuscode", "pr_code='" + c + "'");
			if (!status.equals("AUDITED")) {
				BaseUtil.showError(BaseUtil
						.getLocalMessage("product_onlyAudited")
						+ "<a href=\"javascript:openUrl('jsps/scm/product/product.jsp?formCondition=pr_codeIS"
						+ c + "')\">" + c + "</a>&nbsp;");
			}
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, pc_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"PriceChange",
				"pc_statuscode='COMMITED',pc_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "pc_id="
						+ pc_id);
		// 记录操作
		baseDao.logger.submit(caller, "pc_id", pc_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, pc_id);
	}

	@Override
	public void resSubmitPriceChange(int pc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("PriceChange",
				"pc_statuscode", "pc_id=" + pc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, pc_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"PriceChange",
				"pc_statuscode='ENTERING',pc_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "pc_id="
						+ pc_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "pc_id", pc_id);
		handlerService.afterResSubmit(caller, pc_id);
	}

	public void turnSalePrice(int pc_id) {
		String turnPrice = "SELECT pcd_newcurrency,pcd_newprice,pcd_newdlprice,pcd_newbomcost,"
				+ "pcd_newfxprice,pcd_newlsprice,pcd_newtaxrate,pcd_newtotalcost,pcd_newprofitrate,pcd_newremark,pcd_spdid "
				+ "FROM pricechangedetail WHERE pcd_pcid=?";
		String updatePrice = "UPDATE salepricedetail SET spd_currency = ?,spd_price = ?,"
				+ "spd_dlprice = ?,spd_bomcost = ?,spd_fxprice = ?,spd_lsprice = ?,spd_taxrate = ?,"
				+ "spd_totalcost = ?,spd_profitrate = ?,spd_remark =? WHERE spd_id= ?";
		SqlRowList rs = baseDao.queryForRowSet(turnPrice,
				new Object[] { pc_id });
		while (rs.next()) {
			baseDao.execute(
					updatePrice,
					new Object[] { rs.getObject(1), rs.getObject(2),
							rs.getObject(3), rs.getObject(4), rs.getObject(5),
							rs.getObject(6), rs.getObject(7), rs.getObject(8),
							rs.getObject(9), rs.getObject(10), rs.getObject(11) });
		}
	}

	public void resTurnSalePrice(int pc_id) {
		String resTurnPrice = "SELECT pcd_currency,pcd_price,pcd_dlprice,pcd_bomcost,"
				+ "pcd_fxprice,pcd_lsprice,pcd_taxrate,pcd_totalcost,pcd_profitrate,pcd_remark,pcd_spdid "
				+ "FROM pricechangedetail WHERE pcd_pcid=?";
		String updatePrice = "UPDATE salepricedetail SET spd_currency = ?,spd_price = ?,"
				+ "spd_dlprice = ?,spd_bomcost = ?,spd_fxprice = ?,spd_lsprice = ?,spd_taxrate = ?,"
				+ "spd_totalcost = ?,spd_profitrate = ?,spd_remark =? WHERE spd_id= ?";
		SqlRowList rs = baseDao.queryForRowSet(resTurnPrice,
				new Object[] { pc_id });
		while (rs.next()) {
			baseDao.execute(
					updatePrice,
					new Object[] { rs.getObject(1), rs.getObject(2),
							rs.getObject(3), rs.getObject(4), rs.getObject(5),
							rs.getObject(6), rs.getObject(7), rs.getObject(8),
							rs.getObject(9), rs.getObject(10), rs.getObject(11) });
		}
	}

}
