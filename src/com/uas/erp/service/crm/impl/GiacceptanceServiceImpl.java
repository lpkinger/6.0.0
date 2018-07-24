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

import com.uas.erp.service.crm.GiacceptanceService;

@Service
public class GiacceptanceServiceImpl implements GiacceptanceService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveGiacceptance(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Giacceptance",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		Object[] gad_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			gad_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				gad_id[i] = baseDao.getSeqId("GiacceptanceDETAIL_SEQ");
			}
		} else {
			gad_id[0] = baseDao.getSeqId("GiacceptanceDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore,
				"GiacceptanceDetail", "gad_id", gad_id);
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "ga_id", store.get("ga_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void updateGiacceptanceById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Giacceptance",
				"ga_id");
		baseDao.execute(formSql);
		// 修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"GiacceptanceDetail", "gad_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("gad_id") == null || s.get("gad_id").equals("")
					|| s.get("gad_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("GiacceptanceDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "GiacceptanceDetail",
						new String[] { "gad_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "ga_id", store.get("ga_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteGiacceptance(int ga_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, ga_id);
		// 删除purchase
		baseDao.deleteById("Giacceptance", "ga_id", ga_id);
		// 删除purchaseDetail
		baseDao.deleteById("Giacceptancedetail", "gad_gaid", ga_id);
		// 记录操作
		baseDao.logger.delete(caller, "ga_id", ga_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ga_id);
	}

	@Override
	public void auditGiacceptance(int ga_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Giacceptance",
				"ga_statuscode", "ga_id=" + ga_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ga_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"Giacceptance",
				"ga_statuscode='AUDITED',ga_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',ga_auditer='"
						+ SystemSession.getUser().getEm_name()
						+ "',ga_auditdate=sysdate", "ga_id=" + ga_id);
		// 记录操作
		baseDao.logger.audit(caller, "ga_id", ga_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ga_id);
	}

	@Override
	public void resAuditGiacceptance(int ga_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Giacceptance",
				"ga_statuscode", "ga_id=" + ga_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, ga_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"Giacceptance",
				"ga_statuscode='ENTERING',ga_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',ga_auditer='',ga_auditdate=null", "ga_id=" + ga_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ga_id", ga_id);
		handlerService.afterResAudit(caller, ga_id);
	}

	@Override
	public void submitGiacceptance(int ga_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Giacceptance",
				"ga_statuscode", "ga_id=" + ga_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ga_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"Giacceptance",
				"ga_statuscode='COMMITED',ga_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "ga_id="
						+ ga_id);
		// 记录操作
		baseDao.logger.submit(caller, "ga_id", ga_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ga_id);
	}

	@Override
	public void resSubmitGiacceptance(int ga_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Giacceptance",
				"ga_statuscode", "ga_id=" + ga_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, ga_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"Giacceptance",
				"ga_statuscode='ENTERING',ga_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "ga_id="
						+ ga_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ga_id", ga_id);
		handlerService.afterResSubmit(caller, ga_id);
	}

	@Override
	public void turnOainstorage(String formdata, String griddata, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formdata);
		String id = store.get("ga_id").toString();
		String code = store.get("ga_code").toString();
		Object status = baseDao.getFieldDataByCondition("Giacceptance",
				"ga_isinstore", "ga_id=" + id);
		Object status1 = baseDao.getFieldDataByCondition("Giacceptance",
				"ga_statuscode", "ga_id=" + id);
		if (!status1.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resAudit_onlyAudit"));
		}
		if ("1".equals(status)) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resAudit_onlyAudit"));
		}
		List<String> gridSql = getTurnOaPurchaseSql(store, griddata,
				SystemSession.getLang(), code);
		baseDao.execute(gridSql);
		baseDao.execute(
				"update Giacceptance set ga_isinstore='1',ga_isturn='1' where ga_id=?",
				new Object[] { id });

	}

	public List<String> getTurnOaPurchaseSql(Map<Object, Object> store,
			String griddata, String caller, String code) {
		List<String> sqls = new ArrayList<String>();
		JSONArray grid = JSONArray.fromObject(griddata);
		JSONObject gridjson = new JSONObject();
		String gridSql = null;
		String price = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new Date());
		for (int i = 0; i < grid.size(); i++) {
			gridjson = grid.getJSONObject(i);
			price = "".equals(gridjson.getString("gad_price")) ? "0" : gridjson
					.getString("gad_price");
			gridSql = "update gift set gi_totalnum=nvl2(gi_totalnum,"
					+ gridjson.getString("gad_getnumber") + "+gi_totalnum,"
					+ gridjson.getString("gad_getnumber") + "),"
					+ "gi_lastinnum=" + gridjson.getString("gad_getnumber")
					+ ",gi_lastindate=" + "to_date('" + date
					+ "','YYYY-MM-DD')," + "gi_lastprice=" + price
					+ " where gi_code=" + "'"
					+ gridjson.getString("gad_gicode") + "'";
			sqls.add(gridSql);
		}
		return sqls;
	}
}
