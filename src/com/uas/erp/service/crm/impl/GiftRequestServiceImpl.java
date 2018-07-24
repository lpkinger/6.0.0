package com.uas.erp.service.crm.impl;

import java.util.ArrayList;
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

import com.uas.erp.service.crm.GiftRequestService;

@Service
public class GiftRequestServiceImpl implements GiftRequestService {
	static final String update = "update giftrequest set gr_isturn='1' where gr_id=?";

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveGiftRequest(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "GiftRequest",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		Object[] gqd_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			gqd_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				gqd_id[i] = baseDao.getSeqId("GiftRequestDETAIL_SEQ");
			}
		} else {
			gqd_id[0] = baseDao.getSeqId("GiftRequestDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore,
				"GiftRequestDetail", "gqd_id", gqd_id);
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "gr_id", store.get("gr_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void updateGiftRequestById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "GiftRequest",
				"gr_id");
		baseDao.execute(formSql);
		// 修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"GiftRequestDetail", "gqd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("gqd_id") == null || s.get("gqd_id").equals("")
					|| s.get("gqd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("GiftRequestDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "GiftRequestDetail",
						new String[] { "gqd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "gr_id", store.get("gr_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteGiftRequest(int gr_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, gr_id);
		// 删除purchase
		baseDao.deleteById("GiftRequest", "gr_id", gr_id);
		// 删除purchaseDetail
		baseDao.deleteById("GiftRequestdetail", "gqd_grid", gr_id);
		// 记录操作
		baseDao.logger.delete(caller, "gr_id", gr_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, gr_id);
	}

	@Override
	public void auditGiftRequest(int gr_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("GiftRequest",
				"gr_statuscode", "gr_id=" + gr_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, gr_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"GiftRequest",
				"gr_statuscode='AUDITED',gr_status='"
						+ BaseUtil.getLocalMessage("AUDITED") + "'GR_AUDITOR='"
						+ SystemSession.getUser().getEm_name()
						+ "',gr_auditdate=sysdate", "gr_id=" + gr_id);
		// 记录操作
		baseDao.logger.audit(caller, "gr_id", gr_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, gr_id);
	}

	@Override
	public void resAuditGiftRequest(int gr_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("GiftRequest",
				"gr_statuscode", "gr_id=" + gr_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, gr_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"GiftRequest",
				"gr_statuscode='ENTERING',gr_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',GR_AUDITOR='',gr_auditdate=null", "gr_id=" + gr_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "gr_id", gr_id);
		handlerService.afterResAudit(caller, gr_id);
	}

	@Override
	public void submitGiftRequest(int gr_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("GiftRequest",
				"gr_statuscode", "gr_id=" + gr_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, gr_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"GiftRequest",
				"gr_statuscode='COMMITED',gr_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "gr_id="
						+ gr_id);
		// 记录操作
		baseDao.logger.submit(caller, "gr_id", gr_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, gr_id);

	}

	@Override
	public void resSubmitGiftRequest(int gr_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("GiftRequest",
				"gr_statuscode", "gr_id=" + gr_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, gr_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"GiftRequest",
				"gr_statuscode='ENTERING',gr_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "gr_id="
						+ gr_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "gr_id", gr_id);
		handlerService.afterResSubmit(caller, gr_id);
	}

	@Override
	public void turnOaPurchase(String formdata, String griddata, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formdata);
		String id = store.get("gr_id").toString();
		String code = store.get("gr_code").toString();
		Object status = baseDao.getFieldDataByCondition("GiftRequest",
				"gr_statuscode", "gr_id=" + id);
		Object status1 = baseDao.getFieldDataByCondition("GiftRequest",
				"gr_isturn", "gr_id=" + id);
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

		int formid = baseDao.getSeqId("giPurchase_SEQ");
		// String purchaseCode = baseDao.sGetMaxNumber("giPurchase", 2);
		String s = store.get("gr_code") + "";
		String formSql = "insert into giPurchase(gp_code,gp_status,gp_statuscode,gp_recordorid,gp_recordor"
				+ ",gp_date,gp_id,gp_isturn)values('"
				+ s
				+ "','"
				+ BaseUtil.getLocalMessage("ENTERING")
				+ "','ENTERING','"
				+ SystemSession.getUser().getEm_id()
				+ "',"
				+ "'"
				+ SystemSession.getUser().getEm_name()
				+ "',to_date('"
				+ store.get("gr_recorddate")
				+ "','YYYY-MM-DD'),'"
				+ formid
				+ "','0')";
		sqls.add(formSql);
		JSONArray grid = JSONArray.fromObject(griddata);
		JSONObject gridjson = new JSONObject();
		String gridSql = null;
		int i, j = 0;
		for (i = 0; i < grid.size(); i++) {
			gridjson = grid.getJSONObject(i);
			j = i + 1;
			String unit = baseDao.getFieldDataByCondition("Gift", "gi_unit",
					"gi_code='" + gridjson.getString("gqd_gicode").toString()
							+ "'")
					+ "";
			gridSql = "insert into giPurchasedetail(gpd_id,gpd_detno,gpd_gpid,gpd_gicode,"
					+ "gpd_giname,gpd_giunit,gpd_neednumber,gpd_code)values('"
					+ baseDao.getSeqId("gipurchasedetail_SEQ")
					+ "','"
					+ j
					+ "','"
					+ formid
					+ "','"
					+ gridjson.getString("gqd_gicode")
					+ "','"
					+ gridjson.getString("gqd_name")
					+ "','"
					+ unit
					+ "','"
					+ gridjson.getInt("gqd_quanlity")
					+ "','"
					+ code
					+ "')";
			sqls.add(gridSql);
		}
		return sqls;
	}
}
