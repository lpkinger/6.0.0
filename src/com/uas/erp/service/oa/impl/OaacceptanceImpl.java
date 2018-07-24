package com.uas.erp.service.oa.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.ContextUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.OaPurchaseDao;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.oa.OaacceptanceService;

@Service("OaacceptanceService")
public class OaacceptanceImpl implements OaacceptanceService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private OaPurchaseDao oapurchaseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveOaacceptance(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Oaacceptance", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		Object[] od_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			od_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				od_id[i] = baseDao.getSeqId("OaacceptanceDETAIL_SEQ");
			}
		} else {
			od_id[0] = baseDao.getSeqId("OaacceptanceDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "OaacceptanceDetail", "od_id", od_id);
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "op_id", store.get("op_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });

	}

	@Override
	@Transactional
	public void updateOaacceptanceById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Oaacceptance", "op_id");
		baseDao.execute(formSql);
		// 修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "OaacceptanceDetail", "od_id");
		// 明细不能添加
		/*
		 * for (Map<Object, Object> s : gstore) { if (s.get("od_id") == null || s.get("od_id").equals("") || s.get("od_id").toString().equals("0")) {// 新添加的数据，id不存在 int id = baseDao.getSeqId("OaacceptanceDETAIL_SEQ"); String sql = SqlUtil.getInsertSqlByMap(s, "OaacceptanceDetail", new String[] { "od_id" }, new Object[] { id }); //新添加的明细反应到采购单上 baseDao.updateByCondition("OApurchasedetail", "od_yqty=od_yqty+" + Integer.parseInt(s.get("od_qty")+""), "od_id=" + s.get("od_sourcedetail"));
		 * gridSql.add(sql); } }
		 */
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "op_id", store.get("op_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });

	}

	@Override
	public void deleteOaacceptance(int op_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { op_id });
		// 删除Oaacceptancedetail
		oapurchaseDao.deleteById("Oaacceptancedetail", "od_opid", op_id);
		// 删除Oaacceptance
		baseDao.deleteById("Oaacceptance", "op_id", op_id);
		// 记录操作
		baseDao.logger.delete(caller, "op_id", op_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { op_id });

	}

	@Override
	public void auditOaacceptance(int op_id, String caller) {

		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Oaacceptance", "op_statuscode", "op_id=" + op_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { op_id });
		// 执行审核操作
		baseDao.audit("Oaacceptance", "op_id=" + op_id, "op_status", "op_statuscode");
		// 记录操作
		baseDao.logger.audit(caller, "op_id", op_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { op_id });

	}

	@Override
	public void resAuditOaacceptance(int op_id, String caller) {

		Object status = baseDao.getFieldDataByCondition("Oaacceptance", "op_statuscode", "op_id=" + op_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("Oaacceptance", "op_id=" + op_id, "op_status", "op_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "op_id", op_id);

	}

	@Override
	public void submitOaacceptance(int op_id, String caller) {

		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Oaacceptance", "op_statuscode", "op_id=" + op_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { op_id });
		// 执行提交操作
		// 修改明细金额
		if ("Oaacceptance".equals(caller)) {
			baseDao.execute("update Oaacceptancedetail set od_total=od_qty*od_price where od_opid=" + op_id);
		}
		if ("Oaacceptance!YT".equals(caller)) {
			baseDao.execute("update Oaacceptancedetail set od_total=od_outqty*od_price where od_opid=" + op_id);
		}
		baseDao.submit("Oaacceptance", "op_id=" + op_id, "op_status", "op_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "op_id", op_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { op_id });

	}

	@Override
	public void resSubmitOaacceptance(int op_id, String caller) {

		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Oaacceptance", "op_statuscode", "op_id=" + op_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		baseDao.resOperate("Oaacceptance", "op_id=" + op_id, "op_status", "op_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "op_id", op_id);

	}

	@Override
	@Transactional
	public void turnOainstorage(String formdata, String griddata, String caller) {
		List<String> sqls = new ArrayList<String>();
		JSONObject formjson = JSONObject.fromObject(formdata);
		Object status = baseDao.getFieldDataByCondition("Oaacceptance", "op_inoutstatuscode", "op_id=" + formjson.getInt("op_id"));
		if (status.equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.post_onlyUnPost"));
		}
		String formSql = "update Oaacceptance set op_isinstore='1',op_inoutstatus='已过账',op_inoutstatuscode='POSTED' where op_id='"
				+ formjson.getInt("op_id") + "'";
		sqls.add(formSql);
		JSONArray grid = JSONArray.fromObject(griddata);
		JSONObject gridjson = new JSONObject();
		String gridSql = null;
		String proCode = null;
		int i;
		for (i = 0; i < grid.size(); i++) {
			gridjson = grid.getJSONObject(i);
			proCode = gridjson.getString("od_procode");
			gridSql = "select * from oainstorage where os_procode='" + proCode + "'";
			if (baseDao.getCount(gridSql) != 0) {
				gridSql = "update oainstorage set os_totalnum=os_totalnum+" + gridjson.getInt("od_qty") + ",os_lastinnum="
						+ gridjson.getInt("od_qty") + ",os_lastprice=" + gridjson.getInt("od_price") + ",os_lastindate=to_date('"
						+ formjson.getString("op_date") + "','YYYY-MM-DD') where os_procode='" + proCode + "'";
			} else {
				gridSql = " insert into oainstorage(os_id,os_procode,os_name,os_unit,os_totalnum,os_lastprice,"
						+ "os_lastinnum,os_lastindate)values('" + baseDao.getSeqId("oainstorage_SEQ") + "','"
						+ gridjson.getString("od_procode") + "','" + gridjson.getString("od_proname") + "','"
						+ gridjson.getString("od_prounit") + "','" + gridjson.getInt("od_qty") + "','" + gridjson.getInt("od_price")
						+ "','" + gridjson.getString("od_qty") + "',to_date('" + formjson.getString("op_date") + "','YYYY-MM-DD'))";
			}
			String updateSql = "update oapurchasedetail set od_ysqty=nvl(od_ysqty,0)+" + gridjson.getString("od_qty") + " where  od_detno="
					+ gridjson.getString("od_opdetno") + " and od_oaid in (select op_id from Oapurchase where op_code ='"
					+ gridjson.getString("od_opcode") + "')";// 更新采购单明细的已验收数量
			sqls.add(updateSql);
			baseDao.execute(gridSql);
			// sqls.add(gridSql);
		}
		baseDao.execute(sqls);
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.post"), BaseUtil
				.getLocalMessage("msg.postSuccess"), "Oaacceptance|op_id=" + formjson.getInt("op_id")));
	}

	@Override
	@Transactional
	public void returnOainstorage(String formdata, String griddata, String caller) {
		List<String> sqls = new ArrayList<String>();
		JSONObject formjson = JSONObject.fromObject(formdata);
		Object status = baseDao.getFieldDataByCondition("Oaacceptance", "op_inoutstatuscode", "op_id=" + formjson.getInt("op_id"));
		if (!status.equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.post_onlyPost"));
		}
		String formSql = "update Oaacceptance set op_isinstore='0',op_inoutstatus='未过账',op_inoutstatuscode='UNPOST' where op_id='"
				+ formjson.getInt("op_id") + "'";
		sqls.add(formSql);
		JSONArray grid = JSONArray.fromObject(griddata);
		JSONObject gridjson = new JSONObject();
		String gridSql = null;
		String proCode = null;
		int i;
		for (i = 0; i < grid.size(); i++) {
			gridjson = grid.getJSONObject(i);
			proCode = gridjson.getString("od_procode");
			gridSql = "select * from oainstorage where os_procode='" + proCode + "'";
			if (baseDao.getCount(gridSql) != 0) {
				Object count = null;
				count = baseDao.getFieldDataByCondition("oainstorage", "os_totalnum", "os_procode='" + proCode + "'");
				if (Integer.parseInt(count.toString()) - gridjson.getInt("od_qty") < 0) {
					BaseUtil.showError("序号" + gridjson.getInt("od_detno") + "的库存数量不足，不能反过账");
				}
				gridSql = "update oainstorage set os_totalnum=os_totalnum-" + gridjson.getInt("od_qty") + ",os_lastinnum="
						+ gridjson.getInt("od_qty") + ",os_lastprice=" + gridjson.getInt("od_price") + ",os_lastindate=to_date('"
						+ formjson.getString("op_date") + "','YYYY-MM-DD') where os_procode='" + proCode + "'";
			} else {
				BaseUtil.showError("序号" + gridjson.getInt("od_detno") + "的物料在库存资料中不存在，不能反过账");
			}
			String updateSql = "update oapurchasedetail set od_ysqty=nvl(od_ysqty,0)-" + gridjson.getString("od_qty") + " where  od_detno="
					+ gridjson.getString("od_opdetno") + " and od_oaid in (select op_id from Oapurchase where op_code ='"
					+ gridjson.getString("od_opcode") + "')";// 更新采购单明细的已验收数量
			sqls.add(updateSql);
			sqls.add(gridSql);
		}
		baseDao.execute(sqls);
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.unpost"), BaseUtil
				.getLocalMessage("msg.unpostSuccess"), "Oaacceptance|op_id=" + formjson.getInt("op_id")));

	}

	@Override
	@Transactional
	public void ytPost(String formdata, String griddata, String caller) {
		List<String> sqls = new ArrayList<String>();
		JSONObject formjson = JSONObject.fromObject(formdata);
		Object status = baseDao.getFieldDataByCondition("Oaacceptance", "op_inoutstatuscode", "op_id=" + formjson.getInt("op_id"));
		if (status.equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.post_onlyUnPost"));
		}
		String formSql = "update Oaacceptance set op_isinstore='1',op_inoutstatus='已过账',op_inoutstatuscode='POSTED' where op_id='"
				+ formjson.getInt("op_id") + "'";
		sqls.add(formSql);
		JSONArray grid = JSONArray.fromObject(griddata);
		JSONObject gridjson = new JSONObject();
		String gridSql = null;
		String proCode = null;
		int i;
		for (i = 0; i < grid.size(); i++) {
			gridjson = grid.getJSONObject(i);
			proCode = gridjson.getString("od_procode");
			gridSql = "select * from oainstorage where os_procode='" + proCode + "'";
			if (baseDao.getCount(gridSql) != 0) {
				Object count = null;
				count = baseDao.getFieldDataByCondition("oainstorage", "os_totalnum", "os_procode='" + proCode + "'");
				if (Integer.parseInt(count.toString()) - gridjson.getInt("od_outqty") < 0) {
					BaseUtil.showError("序号" + gridjson.getInt("od_detno") + "的库存数量不足，不能过账");
				}
				gridSql = "update oainstorage set os_totalnum=os_totalnum-" + gridjson.getInt("od_outqty") + " where os_procode='"
						+ proCode + "'";
			} else {
				BaseUtil.showError("序号" + gridjson.getInt("od_detno") + "的物料在库存资料中不存在，不能过账");
			}
			String updateSql = "update oapurchasedetail set od_ysqty=nvl(od_ysqty,0)-" + gridjson.getString("od_outqty")
					+ " where  od_detno=" + gridjson.getString("od_opdetno")
					+ " and od_oaid in (select op_id from Oapurchase where op_code ='" + gridjson.getString("od_opcode") + "')";// 更新采购单明细的已验收数量
			sqls.add(updateSql);
			sqls.add(gridSql);
		}
		baseDao.execute(sqls);
		baseDao.logger.post(caller, "op_id", formjson.getInt("op_id"));

	}

	@Override
	@Transactional
	public void ytResPost(String formdata, String griddata, String caller) {
		List<String> sqls = new ArrayList<String>();
		JSONObject formjson = JSONObject.fromObject(formdata);
		Object status = baseDao.getFieldDataByCondition("Oaacceptance", "op_inoutstatuscode", "op_id=" + formjson.getInt("op_id"));
		if (!status.equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.post_onlyUnPost"));
		}
		String formSql = "update Oaacceptance set op_isinstore='0',op_inoutstatus='未过账',op_inoutstatuscode='UNPOST' where op_id='"
				+ formjson.getInt("op_id") + "'";
		sqls.add(formSql);
		JSONArray grid = JSONArray.fromObject(griddata);
		JSONObject gridjson = new JSONObject();
		String gridSql = null;
		String proCode = null;
		int i;
		for (i = 0; i < grid.size(); i++) {
			gridjson = grid.getJSONObject(i);
			proCode = gridjson.getString("od_procode");
			gridSql = "select * from oainstorage where os_procode='" + proCode + "'";
			if (baseDao.getCount(gridSql) != 0) {
				gridSql = "update oainstorage set os_totalnum=os_totalnum+" + gridjson.getInt("od_outqty") + " where os_procode='"
						+ proCode + "'";
			} else {
				BaseUtil.showError("序号" + gridjson.getInt("od_detno") + "的物料在库存资料中不存在，不能反过账");
			}
			String updateSql = "update oapurchasedetail set od_ysqty=nvl(od_ysqty,0)+" + gridjson.getString("od_outqty")
					+ " where  od_detno=" + gridjson.getString("od_opdetno")
					+ " and od_oaid in (select op_id from Oapurchase where op_code ='" + gridjson.getString("od_opcode") + "')";// 更新采购单明细的已验收数量
			sqls.add(updateSql);
			sqls.add(gridSql);
		}
		baseDao.execute(sqls);
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.unpost"), BaseUtil
				.getLocalMessage("msg.unpostSuccess"), "Oaacceptance|op_id=" + formjson.getInt("op_id")));
	}

	@Override
	public String[] printOaacceptance(int op_id, String caller, String reportName, String condition) {
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 修改打印状态
		/*
		 * baseDao.updateByCondition("Oapurchase", "pu_printstatuscode='PRINTED',pu_printstatus='" + BaseUtil.getLocalMessage("PRINTED") + "'", "op_id=" + op_id); // 记录操作 baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.print"), BaseUtil.getLocalMessage("msg.printSuccess"), "Oapurchase|pu_id=" + op_id)); // 执行打印后的其它逻辑 handlerService.handler("Oapurchase", "print", "after", new Object[] { op_id });
		 */
		return keys;
	}

	@Override
	public void postOaacceptance(int op_id, String caller) {
		// 只能对状态为[未记账]的订单进行操作!
		Object[] status = baseDao.getFieldsDataByCondition("Oaacceptance", new String[] { "op_inoutstatuscode", "op_code" }, "op_id="
				+ op_id);
		if (status[0].equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.post_onlyUnPost"));
		}
		// 过账前的其它逻辑
		handlerService.beforePost(caller, new Object[] { op_id });
		// 执行记账操作
		// 存储过程
		String type = null;
		if ("Oaacceptance".equals(caller)) {
			type = "用品验收单";
		} else if ("Oaacceptance!YT".equals(caller)) {
			type = "用品验退单";
		}
		String res = baseDao.callProcedure("SP_COMMITOAINSTORAGE",
				new Object[] { type, op_id, String.valueOf(SystemSession.getUser().getEm_id()) });
		if (res != null && !res.trim().equals("") && !"OK".equals(res.toUpperCase())) {
			BaseUtil.showError(res);
		}
		// 记录操作
		baseDao.logger.post(caller, "op_id", op_id);
		// 执行过账后的其它逻辑
		handlerService.afterPost(caller, new Object[] { op_id });
	}

	@Override
	public void resPostOaacceptance(int op_id, String caller) {
		// 只能对状态为[已记账]的订单进行反记账操作!
		Object[] status = baseDao.getFieldsDataByCondition("Oaacceptance", new String[] { "op_inoutstatuscode", "op_code" }, "op_id="
				+ op_id);
		StateAssert.resPostOnlyPosted(status[0]);
		String type = null;
		if ("Oaacceptance".equals(caller)) {
			type = "用品验收单";
		} else if ("Oaacceptance!YT".equals(caller)) {
			type = "用品验退单";
		}
		// 执行反记账前的其它逻辑
		handlerService.beforeResPost(caller, new Object[] { op_id });
		// 执行反记账操作
		Object obj = baseDao.getFieldDataByCondition("APBill", "ab_code", "ab_sourceid=" + op_id + " and ab_class='用品发票'");
		String res = null;
		// 存储过程
		if (obj != null) {
			res = baseDao.callProcedure("Sp_UnCommiteAPBill", new Object[] { obj, 1 });
			if (res == null || res.trim().equals("")) {
				res = baseDao.callProcedure("SP_UNCOMMITOAINSTORAGE", new Object[] { type, op_id });
				if (res != null && !res.trim().equals("")) {
					if ("OK".equals(res.toUpperCase())) {
						// 记录操作
						baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "反记帐", "," + type + "反记帐,编号:" + status[1],
								caller + "|op_id=" + op_id));
						baseDao.execute("delete from APBill where ab_code=?", obj);
						baseDao.execute("delete from APBillDetail where abd_code=?", obj);
					} else {
						BaseUtil.showError(res);
					}
				}
			} else {
				BaseUtil.showError(res);
			}
		} else {
			res = baseDao.callProcedure("SP_UNCOMMITOAINSTORAGE", new Object[] { type, op_id });
			if (res != null && !res.trim().equals("")) {
				if ("OK".equals(res.toUpperCase())) {
					// 记录操作
					baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "反记帐", "," + type + "反记帐,编号:" + status[1],
							caller + "|op_id=" + op_id));
				} else {
					BaseUtil.showError(res);
				}
			}
		}
		// 反过账后的其它逻辑
		handlerService.afterResPost(caller, new Object[] { op_id });
		// 记录操作
		baseDao.logger.getMessageLog(BaseUtil.getLocalMessage("msg.resAccount"), BaseUtil.getLocalMessage("msg.resAccountSuccess"), caller,
				"op_id", op_id);
	}

	public void oaacceptance_deletedetail(Integer id, String caller) {
		baseDao = baseDao == null ? (BaseDao) ContextUtil.getBean("baseDao") : baseDao;
		Object[] objs = baseDao.getFieldsDataByCondition("OAACCEPTANCEDETAIL", "od_sourcedetail,od_qty,od_opcode", "od_id=" + id);
		if (objs != null && objs[2] != null) {
			baseDao.updateByCondition("OApurchasedetail", "od_yqty=od_yqty-" + Integer.parseInt(objs[1].toString()), "od_id=" + objs[0]);
		}
	}
}
