package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.MouldSaleDao;
import com.uas.erp.service.pm.MouldSaleService;

@Service("mouldSaleService")
public class MouldSaleServiceImpl implements MouldSaleService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private MouldSaleDao mouldSaleDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveMouldSale(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("MOD_Sale", "msa_code='" + store.get("msa_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存MouldSale
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "MOD_Sale", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存MouldSaleDetail
		for (Map<Object, Object> s : grid) {
			s.put("msd_id", baseDao.getSeqId("MOD_SALEDETAIL_SEQ"));
			s.put("msd_code", store.get("msa_code"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "MOD_SALEDETAIL");
		baseDao.execute(gridSql);
		getTotal(store.get("msa_id"));
		baseDao.logger.save(caller, "msa_id", store.get("msa_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void deleteMouldSale(int msa_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("MOD_Sale", "msa_statuscode", "msa_id=" + msa_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { msa_id });
		baseDao.execute("update appmould set APP_TURNSALE=null,APP_TURNSALECODE=null where app_id in (select msa_sourceid from mod_sale where msa_id="
				+ msa_id + " and msa_sourcetype='开模申请单')");
		baseDao.execute("update MOD_ALTER set al_turnsale=null,al_turnsalecode=null where al_id in (select msa_sourceid from mod_sale where msa_id="
				+ msa_id + " and msa_sourcetype='模具修改申请单')");
		// 删除MouldSale
		baseDao.deleteById("MOD_Sale", "msa_id", msa_id);
		// 删除MouldSaleDetail
		baseDao.deleteById("MOD_Saledetail", "msd_msaid", msa_id);
		// 记录操作
		baseDao.logger.delete(caller, "msa_id", msa_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { msa_id });
	}

	@Override
	public void updateMouldSaleById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("MOD_Sale", "msa_statuscode", "msa_id=" + store.get("msa_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改MouldSale
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "MOD_Sale", "msa_id");
		baseDao.execute(formSql);
		// 修改MouldSaleDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "MOD_SaleDetail", "msd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("msd_id") == null || s.get("msd_id").equals("") || s.get("msd_id").equals("0")
					|| Integer.parseInt(s.get("msd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("MOD_SALEDETAIL_SEQ");
				s.put("msd_code", store.get("msa_code"));
				String sql = SqlUtil.getInsertSqlByMap(s, "MOD_SaleDetail", new String[] { "msd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		getTotal(store.get("msa_id"));
		// 记录操作
		baseDao.logger.update(caller, "msa_id", store.get("msa_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void printMouldSale(int msa_id, String caller) {
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, new Object[] { msa_id });
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "msa_id", msa_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, new Object[] { msa_id });
	}

	@Override
	public void auditMouldSale(int msa_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("MOD_Sale", "msa_statuscode", "msa_id=" + msa_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { msa_id });
		// 执行审核操作
		baseDao.audit("MOD_Sale", "msa_id=" + msa_id, "msa_status", "msa_statuscode", "msa_auditdate", "msa_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "msa_id", msa_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { msa_id });
	}

	@Override
	public void resAuditMouldSale(int msa_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("MOD_Sale", "msa_statuscode", "msa_id=" + msa_id);
		StateAssert.resAuditOnlyAudit(status);
		int count = baseDao.getCountByCondition("MOD_SaleDetail", "msd_msaid=" + msa_id + " and nvl(msd_turnstatuscode,' ')<>' '");
		if (count > 0) {
			BaseUtil.showError("已转模具出货单不允许反审核!");
		}
		// 执行反审核操作
		baseDao.resOperate("MOD_Sale", "msa_id=" + msa_id, "msa_status", "msa_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "msa_id", msa_id);
	}

	@Override
	public void submitMouldSale(int msa_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MOD_Sale", "msa_statuscode", "msa_id=" + msa_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { msa_id });
		getTotal(msa_id);
		// 执行提交操作
		baseDao.submit("MOD_Sale", "msa_id=" + msa_id, "msa_status", "msa_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "msa_id", msa_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { msa_id });
	}

	@Override
	public void resSubmitMouldSale(int msa_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MOD_Sale", "msa_statuscode", "msa_id=" + msa_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { msa_id });
		// 执行反提交操作
		baseDao.resOperate("MOD_Sale", "msa_id=" + msa_id, "msa_status", "msa_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "msa_id", msa_id);
		handlerService.afterResSubmit(caller, new Object[] { msa_id });
	}

	@Override
	public String turnDeliveryOrder(int msa_id, String caller) {
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(msd_detno) from MOD_SaleDetail where msd_msaid=? and nvl(msd_closestatuscode,' ')='FINISH'",
				String.class, msa_id);
		if (dets != null) {
			BaseUtil.showError("存在已结案的明细行，不允许转单操作!行号" + dets);
		}
		JSONObject j = null;
		StringBuffer sb = new StringBuffer();
		int md_id = 0;
		// 判断该模具销售单是否转过模具出货单
		Object code = baseDao.getFieldDataByCondition("MOD_Sale", "msa_code", "msa_id=" + msa_id);
		code = baseDao.getFieldDataByCondition("MOD_DeliveryOrder", "md_code", "md_sourcecode='" + code + "'");
		if (code != null && !code.equals("")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("pm.mould.mouldsale.haveturn")
					+ "<a href=\"javascript:openUrl('jsps/pm/mould/deliveryOrder.jsp?formCondition=md_codeIS" + code
					+ "&gridCondition=mdd_codeIS" + code + "')\">" + code + "</a>&nbsp;");
		} else {
			j = mouldSaleDao.turnDeliveryOrder(msa_id);
			if (j != null) {
				md_id = j.getInt("md_id");
				sb.append("转入成功,模具出货单号:" + "<a href=\"javascript:openUrl('jsps/pm/mould/deliveryOrder.jsp?formCondition=md_idIS" + md_id
						+ "&gridCondition=mdd_mdidIS" + md_id + "')\">" + j.getString("md_code") + "</a>&nbsp;");
				baseDao.updateByCondition("MOD_Sale", "msa_turnstatuscode='TURNOUT',msa_turnstatus='" + BaseUtil.getLocalMessage("TURNOUT")
						+ "'", "msa_id=" + msa_id);
				// 记录操作
				baseDao.logger.turn("转模具出货单", caller, "msa_id", msa_id);
			}
		}
		return sb.toString();
	}

	/**
	 * 计算total
	 */
	private void getTotal(Object msa_id) {
		baseDao.execute("update MOD_SALEDETAIL set msd_code=(select msa_code from MOD_SALE where msd_msaid=msa_id) where msd_msaid="
				+ msa_id + " and not exists (select 1 from MOD_SALE where msd_code=msa_code)");
		baseDao.updateByCondition("MOD_SaleDETAIL", "msd_amount=round(nvl(msd_price,0)*nvl(msd_qty,0),2)", "msd_msaid=" + msa_id);
		baseDao.updateByCondition("MOD_Sale",
				"msa_amount=(SELECT round(sum(nvl(msd_price,0)*nvl(msd_qty,0)),2) FROM MOD_SaleDETAIL WHERE msd_msaid=" + msa_id + ")",
				"msa_id=" + msa_id);
	}

	@Override
	public void updateChargeStatus(int msa_id, String status, String remark, String caller) {
		baseDao.execute("update mod_sale set msa_chargeremark='" + remark + "',msa_chargestatus='" + status + "' where msa_id=" + msa_id);
		baseDao.logger.others("更新收款状态", "msg.updateSuccess", caller, "msa_id", msa_id);
	}
}
