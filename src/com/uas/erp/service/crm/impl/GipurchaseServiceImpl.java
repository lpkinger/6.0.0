package com.uas.erp.service.crm.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.crm.GipurchaseService;

@Service
public class GipurchaseServiceImpl implements GipurchaseService {
	static final String update = "update Gipurchase set gp_isturn='1' where gp_id=?";

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveGipurchase(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Gipurchase",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		Object[] gpd_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			gpd_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				gpd_id[i] = baseDao.getSeqId("GipurchaseDETAIL_SEQ");
			}
		} else {
			gpd_id[0] = baseDao.getSeqId("GipurchaseDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore,
				"GipurchaseDetail", "gpd_id", gpd_id);
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "gp_id", store.get("gp_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void updateGipurchaseById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Gipurchase",
				"gp_id");
		baseDao.execute(formSql);
		// 修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"GipurchaseDetail", "gpd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("gpd_id") == null || s.get("gpd_id").equals("")
					|| s.get("gpd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("GipurchaseDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "GipurchaseDetail",
						new String[] { "gpd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "gp_id", store.get("gp_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteGipurchase(int gp_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, gp_id);
		// 删除purchase
		baseDao.deleteById("Gipurchase", "gp_id", gp_id);
		// 删除purchaseDetail
		baseDao.deleteById("Gipurchasedetail", "gpd_gpid", gp_id);
		// 记录操作
		baseDao.logger.delete(caller, "gp_id", gp_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, gp_id);
	}

	@Override
	public void auditGipurchase(int gp_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Gipurchase",
				"gp_statuscode", "gp_id=" + gp_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, gp_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"Gipurchase",
				"gp_statuscode='AUDITED',gp_status='"
						+ BaseUtil.getLocalMessage("AUDITED") + "'GP_AUDITOR='"
						+ SystemSession.getUser().getEm_name()
						+ "',GP_AUDITORID="
						+ SystemSession.getUser().getEm_id()
						+ ",gp_auditdate=sysdate", "gp_id=" + gp_id);
		// 记录操作
		baseDao.logger.audit(caller, "gp_id", gp_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, gp_id);
	}

	@Override
	public void resAuditGipurchase(int gp_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Gipurchase",
				"gp_statuscode", "gp_id=" + gp_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, gp_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"Gipurchase",
				"gp_statuscode='ENTERING',gp_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',GP_AUDITOR='',GP_AUDITORID=0,gp_auditdate=null",
				"gp_id=" + gp_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "gp_id", gp_id);
		handlerService.afterResAudit(caller, gp_id);
	}

	@Override
	public void submitGipurchase(int gp_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Gipurchase",
				"gp_statuscode", "gp_id=" + gp_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, gp_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"Gipurchase",
				"gp_statuscode='COMMITED',gp_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "gp_id="
						+ gp_id);
		// 记录操作
		baseDao.logger.submit(caller, "gp_id", gp_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, gp_id);
	}

	@Override
	public void resSubmitGipurchase(int gp_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Gipurchase",
				"gp_statuscode", "gp_id=" + gp_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, gp_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"Gipurchase",
				"gp_statuscode='ENTERING',gp_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "gp_id="
						+ gp_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "gp_id", gp_id);
		handlerService.afterResSubmit(caller, gp_id);
	}

	@Override
	public void turnOaacceptance(String formdata, String griddata, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formdata);
		String id = store.get("gp_id").toString();
		String code = store.get("gp_code").toString();
		Object status = baseDao.getFieldDataByCondition("Gipurchase",
				"gp_statuscode", "gp_id=" + id);
		Object status1 = baseDao.getFieldDataByCondition("Gipurchase",
				"gp_isturn", "gp_id=" + id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resAudit_onlyAudit"));
		}
		if ("1".equals(status1)) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resAudit_onlyAudit"));
		}
		List<String> gridSql = getTurnOaPurchaseSql(store, griddata,
				SystemSession.getLang(), code);
		baseDao.execute(gridSql);
		baseDao.execute(update, new Object[] { id });
	}

	public List<String> getTurnOaPurchaseSql(Map<Object, Object> store,
			String griddata, String caller, String code) {
		List<String> sqls = new ArrayList<String>();
		int formid = baseDao.getSeqId("giacceptance_SEQ");
		// String purchaseCode = baseDao.sGetMaxNumber("giPurchase", 2);
		String s = store.get("gp_code") + "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new Date());
		String formSql = "insert into giacceptance(ga_code,ga_status,ga_statuscode,ga_recorderid,ga_recorder"
				+ ",ga_date,ga_id,ga_isturn)values('"
				+ s
				+ "','"
				+ BaseUtil.getLocalMessage("ENTERING")
				+ "','ENTERING','"
				+ SystemSession.getUser().getEm_id()
				+ "',"
				+ "'"
				+ SystemSession.getUser().getEm_name()
				+ "',to_date('"
				+ date
				+ "','YYYY-MM-DD'),'" + formid + "','0')";
		sqls.add(formSql);
		JSONArray grid = JSONArray.fromObject(griddata);
		JSONObject gridjson = new JSONObject();
		String gridSql = null;
		int i, j = 0;
		for (i = 0; i < grid.size(); i++) {
			gridjson = grid.getJSONObject(i);
			j = i + 1;
			gridSql = "insert into giacceptancedetail(gad_id,gad_detno,gad_gaid,gad_gicode,"
					+ "gad_giname,gad_giunit,gad_neednumber,gad_getnumber)values('"
					+ baseDao.getSeqId("gipurchasedetail_SEQ")
					+ "','"
					+ j
					+ "','"
					+ formid
					+ "','"
					+ gridjson.getString("gpd_gicode")
					+ "','"
					+ gridjson.getString("gpd_giname")
					+ "','"
					+ gridjson.getString("gpd_giunit")
					+ "','"
					+ gridjson.getInt("gpd_neednumber")
					+ "','"
					+ gridjson.getInt("gpd_neednumber") + "')";
			sqls.add(gridSql);
		}
		return sqls;
	}
}
