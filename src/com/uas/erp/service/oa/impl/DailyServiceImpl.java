package com.uas.erp.service.oa.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DailyDao;
import com.uas.erp.service.oa.DailyService;

@Service("DailyService")
public class DailyServiceImpl implements DailyService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private DailyDao DailyDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveDaily(String formStore, String gridStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Daily", "da_code='" + store.get("da_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("oa.Daily.Daily.save_dacodeHasExist"));
		}
		//store.put("pu_printstatuscode", "UNPRINT");
		//store.put("pu_printstatus", BaseUtil.getLocalMessage("UNPRINT"));
		// 缺省应付供应商
		/*if (store.get("pu_receivecode") == null || store.get("pu_receivecode").toString().trim().equals("")) {
			store.put("pu_receivecode", store.get("pu_vendcode"));
			store.put("pu_receivename", store.get("pu_vendname"));
		}*/
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存Daily
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Daily", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存DailyDetail
		Object[] dd_id = new Object[grid.size()];
		//bool = store.get("pu_getprice").toString().equals("-1");// 是否自动获取单价
		StringBuffer error = new StringBuffer();
		for (int i = 0; i < grid.size(); i++) {
			Map<Object, Object> map = grid.get(i);
			dd_id[i] = baseDao.getSeqId("DailyDETAIL_SEQ");
			map.put("dd_id", dd_id[i]);
			//map.put("dd_status", "ENTERING");
			map.put("dd_code", store.get("da_code"));
			
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "DailyDetail");
		baseDao.execute(gridSql);
		// 修改主单据的总金额
		
		try {
			// 记录操作
			baseDao.logger.save(caller, "da_id", store.get("da_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
		if (error.length() > 0) {
			BaseUtil.showErrorOnSuccess(error.toString());
		}
	}

	@Override
	public void deleteDaily(int da_id, String  caller) {
		// 只能删除在录入的采购单!
		Object status = baseDao.getFieldDataByCondition("Daily", "da_statuscode", "da_id=" + da_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller,  new Object[] { da_id});
		// 删除DailyDetail
		DailyDao.deleteDaily(da_id);
		// 删除Daily
		baseDao.deleteById("Daily", "da_id", da_id);
		// 还原请购单
		// 记录操作
		baseDao.logger.delete(caller, "da_id", da_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller,  new Object[] { da_id});
	}

	@Override
	public void updateDailyById(String formStore, String gridStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的采购单资料!
		Object status = baseDao.getFieldDataByCondition("Daily", "da_statuscode", "da_id=" + store.get("da_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,new Object[] { store, gstore });
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Daily", "da_id");
		baseDao.execute(formSql);
		// 修改DailyDetail
		StringBuffer error = new StringBuffer();
		
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "DailyDetail", "dd_id");
		for (Map<Object, Object> s : gstore) {
			Object pdid = s.get("dd_id");
			if (pdid == null || pdid.equals("") || pdid.equals("0") || Integer.parseInt(pdid.toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("DailyDETAIL_SEQ");
				s.put("dd_id", id);
				s.put("dd_code", store.get("da_code"));
				s.put("dd_status", "ENTERING");
				String sql = SqlUtil.getInsertSqlByMap(s, "DailyDetail", new String[] { "dd_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		
		baseDao.updateByCondition("Dailydetail", "dd_code='" + store.get("da_code") + "'",
				"dd_daid=" + store.get("da_id"));
		// 记录操作
		baseDao.logger.update(caller, "da_id", store.get("da_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[] { store, gstore });
		if (error.length() > 0) {
			BaseUtil.showErrorOnSuccess(error.toString());
		}
	}

	@Override
	public String[] printDaily(int da_id, String  caller, String reportName, String condition) {
		// 只能打印审核后的采购单!
		/*
		 * Object status = baseDao.getFieldDataByCondition("Daily",
		 * "da_statuscode", "da_id=" + da_id); if(!status.equals("AUDITED") &&
		 * !status.equals("PARTRECEIVED") && !status.equals("RECEIVED") &&
		 * !status.equals("NULLIFIED")){
		 * BaseUtil.showError(BaseUtil.getLocalMessage
		 * ("scm.Daily.Daily.print_onlyAudit")); }
		 */
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, new Object[] { da_id });
		// 执行打印操作
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 修改打印状态
		baseDao.print("Daily", "da_id=" + da_id, "pu_printstatus", "pu_printstatuscode");
		// 记录操作
		baseDao.logger.print(caller, "da_id", da_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, new Object[] { da_id });
		return keys;
	}

	@Override
	public void auditDaily(int da_id, String  caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Daily", "da_statuscode", "da_id=" + da_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { da_id});
		// 执行审核操作
		baseDao.audit("Daily", "da_id=" + da_id, "pu_status", "da_statuscode", "da_auditdate", "da_auditor");
		baseDao.updateByCondition("DailyDetail", "dd_status='AUDITED'", "dd_daid=" + da_id);
		// 记录操作
		baseDao.logger.audit(caller, "da_id", da_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { da_id});
	}

	@Override
	public void resAuditDaily(int da_id, String  caller) {
		// 只能对状态为[已审核]的采购单进行反审核操作!
		Object[] objs = baseDao.getFieldsDataByCondition("Daily", new String[] { "da_statuscode"}, "da_id=" + da_id);
		StateAssert.resAuditOnlyAudit(objs[0]);	
		// 执行反审核操作
		baseDao.resOperate("Daily", "da_id="+ da_id, "da_status", "da_statuscode");
		baseDao.updateByCondition("DailyDetail", "dd_status='ENTERING'", "dd_daid=" + da_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "da_id", da_id);
	}

	@Override
	public void submitDaily(int da_id, String  caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Daily", "da_statuscode", "da_id=" + da_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交操作
		baseDao.submit("Daily", "da_id="+ da_id, "da_status", "da_statuscode");
		baseDao.updateByCondition("DailyDetail", "dd_status='COMMITED'", "dd_daid=" + da_id);
		// 记录操作
		baseDao.logger.submit(caller, "da_id", da_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { da_id});
	}

	@Override
	public void resSubmitDaily(int da_id, String  caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Daily", "da_statuscode", "da_id=" + da_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { da_id});
		// 执行反提交操作
		baseDao.resOperate("Daily", "da_id="+ da_id, "da_status", "da_statuscode");
		baseDao.updateByCondition("DailyDetail", "dd_status='ENTERING'", "dd_daid=" + da_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "da_id", da_id);
		handlerService.afterResSubmit(caller, new Object[] { da_id});
	}

	@Override
	public void endDaily(int da_id, String  caller) {
		// 只能对状态为[已审核]的订单进行结案操作!
		Object status = baseDao.getFieldDataByCondition("Daily", "da_statuscode", "da_id=" + da_id);
		StateAssert.end_onlyAudited(status);
		// 结案
		baseDao.updateByCondition("Daily",
				"da_statuscode='FINISH',pu_sendstatus='待上传',pu_status='" + BaseUtil.getLocalMessage("FINISH")
						+ "'", "da_id=" + da_id);
		baseDao.updateByCondition("DailyDetail", "dd_status='FINISH'", "dd_daid=" + da_id);
		// 记录操作
		baseDao.logger.getMessageLog(BaseUtil.getLocalMessage("msg.end"), BaseUtil.getLocalMessage("msg.endSuccess"), caller, "da_id", da_id);
	}

	@Override
	public void resEndDaily(int da_id, String  caller) {
		// 只能对状态为[已结案]的订单进行反结案操作!
		Object status = baseDao.getFieldDataByCondition("Daily", "da_statuscode", "da_id=" + da_id);
		StateAssert.resEnd_onlyAudited(status);
		// 反结案
		baseDao.updateByCondition(
				"Daily",
				"da_statuscode='AUDITED',pu_sendstatus='待上传',pu_status='"
						+ BaseUtil.getLocalMessage("AUDITED") + "'", "da_id=" + da_id);
		baseDao.updateByCondition("DailyDetail", "dd_status='AUDITED'", "dd_daid=" + da_id);
		// 记录操作
		baseDao.logger.getMessageLog(BaseUtil.getLocalMessage("msg.resEnd"), BaseUtil.getLocalMessage("msg.resEndSuccess"), caller, "da_id", da_id);
	}

	public void getPrice(int da_id) {
		 
	}

	public void getStandardPrice(int da_id) {
	}
	
	@Override
	public void vastDeleteDaily(int[] id, String  caller) {
		SqlRowList rs = baseDao
				.queryForRowSet("SELECT dd_code,pd_detno,pd_yqty,pd_acceptqty,dd_daid FROM DailyDetail WHERE dd_daid in("
						+ BaseUtil.parseArray2Str(NumberUtil.toIntegerArray(id), ",") + ")");
		StringBuffer sb = new StringBuffer();
		while (rs.next()) {
			if (rs.getDouble("pd_yqty") > 0 || rs.getDouble("pd_acceptqty") > 0) {
				sb.append("采购单号[");
				sb.append(rs.getObject("dd_code"));
				sb.append("],序号[");
				sb.append(rs.getInt("pd_detno"));
				sb.append("]中已转出或者已验收，不允许删除！");
			} else {
				baseDao.deleteByCondition("Daily", "da_id=" + rs.getInt("dd_daid"));
				baseDao.deleteByCondition("DailyDetail", "dd_daid=" + rs.getInt("dd_daid"));
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showErrorOnSuccess(sb.toString());
		}
	}

	@Override
	public JSONObject copyDaily(int id, String caller) {
		Map<String, Object> dif = new HashMap<String, Object>();
		// Copy Purcahse
		int nId = baseDao.getSeqId("Daily_SEQ");
		dif.put("da_id", nId);
		dif.put("pu_date", "sysdate");
		dif.put("pu_indate", "sysdate");
		String code = baseDao.sGetMaxNumber("Purcahse", 2);
		dif.put("da_code", "'" + code + "'");
		dif.put("pu_recordid", SystemSession.getUser().getEm_id());
		dif.put("pu_recordman", "'" + SystemSession.getUser().getEm_name() + "'");
		dif.put("pu_status", "'" + BaseUtil.getLocalMessage("ENTERING") + "'");
		dif.put("da_statuscode", "'ENTERING'");
		dif.put("pu_auditman", "null");
		dif.put("pu_auditdate", "null");
		dif.put("pu_turnstatus", "null");
		dif.put("pu_turnstatuscode", "null");
		dif.put("pu_acceptstatus", "null");
		dif.put("pu_acceptstatuscode", "null");
		dif.put("pu_printstatus", "null");
		baseDao.copyRecord("Purcahse", "Purcahse", "da_id=" + id, dif);
		// Copy PurcahseDetail
		dif = new HashMap<String, Object>();
		dif.put("dd_id", "Dailydetail_seq.nextval");
		dif.put("dd_daid", nId);
		dif.put("pd_yqty", 0);
		dif.put("pd_acceptqty", 0);
		dif.put("pd_ngacceptqty", 0);
		dif.put("pd_source", "null");
		dif.put("pd_sourcecode", "null");
		dif.put("pd_sourcedetail", 0);
		dif.put("pd_mrpstatus", "null");
		baseDao.copyRecord("PurcahseDetail", "PurcahseDetail", "dd_daid=" + id, dif);
		JSONObject obj = new JSONObject();
		obj.put("id", nId);
		obj.put("code", code);
		return obj;
	}

	@Override
	public void getMakeVendorPrice(int ma_id, String  caller) {
		String vendcode = "", currency = "";
		double taxrate = 0;
		SqlRowList rs = baseDao.queryForRowSet("select * from Make where  ma_id=" + ma_id + " and ma_tasktype='OS' ");
		if (rs.next()) {
			if (rs.getString("ma_statuscode").equals("FINISH")) {
				BaseUtil.showError("已经结案工单不能更新委外商");
			}
			if (rs.getObject("ma_madeqty") != null && rs.getDouble("ma_madeqty") > 0) {
				BaseUtil.showError("已有验收数量的委外单不能更新委外商信息");
			}
			// 到物料核价单取单价
			JSONObject obj = null;
			obj = DailyDao.getPriceVendor(rs.getString("ma_prodcode"), "委外", rs.getDouble("ma_qty"));
			if (obj != null) {
				double price = obj.getDouble("price");
				vendcode = obj.getString("vendcode");
				currency = obj.getString("currency");
				taxrate = obj.getDouble("taxrate");
				baseDao.updateByCondition("Make", "ma_vendcode='" + vendcode + "', ma_currency='" + currency
						+ "',ma_taxrate= " + taxrate + ", ma_price=round("
						+ price + ",8), ma_total=round(" + price + "*ma_qty,2) ", "ma_id =" + ma_id);

				baseDao.execute("update make set (ma_paymentscode,ma_payments,ma_vendname,ma_rate)=(select ve_paymentcode,ve_payment,ve_name, cm_crrate from vendor left join currencysmonth on cm_crname=ve_currency where ve_code=ma_vendcode and cm_yearmonth=to_char(ma_date,'yyyymm')) where ma_id="
						+ ma_id);
			}
			// 记录操作
			baseDao.logger.getMessageLog("委外信息变更", BaseUtil.getLocalMessage("msg.saveSuccess"), caller, "ma_id", ma_id);
		}
	}

	@Override
	public void syncDaily(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String ids = BaseUtil.parseArray2Str(CollectionUtil.pluck(maps, "da_id"), ",");
		SqlRowList rs = baseDao.queryForRowSet("select da_id from Daily where da_id in (" +
				ids + ") and da_statuscode='AUDITED' and pu_receivecode='02.01.028' and nvl(pu_sync,' ')=' '");
		while (rs.next()) {
			DailyDao.syncPurcToSqlServer(rs.getInt(1));
		}
	}

	@Override
	public void updateVendorBackInfo(String data, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(data);
		Object[] datas = baseDao.getFieldsDataByCondition("Dailydetail", new String[] { "pd_detno", "pd_qty",
				"dd_daid" }, "dd_id=" + store.get("dd_id"));
		boolean bool = Double.parseDouble(datas[1].toString()) < Double
				.parseDouble(store.get("pd_qtyreply").toString());
		if (bool)
			BaseUtil.showError("回复数量不能大于采购数!");
		baseDao.execute("update Dailydetail set pd_qtyreply=" + store.get("pd_qtyreply") + ",pd_isok='"
				+ store.get("pd_isok") + "',pd_deliveryreply='" + store.get("pd_deliveryreply") + "',pd_replydetail='"+store.get("pd_replydetail")+"' where dd_id="
				+ store.get("dd_id"));
		baseDao.logger.others("更新供应商回复信息", "更新成功,序号:" + datas[0], caller, "da_id", datas[2]);
	}

	@Override
	public void resetSyncStatus(String caller, Integer id) {
		DailyDao.resetPurcSyncStatus(id);
	}

}
