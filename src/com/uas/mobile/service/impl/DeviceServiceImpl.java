package com.uas.mobile.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.mobile.model.Panel;
import com.uas.mobile.service.DeviceService;
import com.uas.mobile.service.PanelService;

@Service
public class DeviceServiceImpl implements DeviceService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	PanelService panelService;
	@Autowired
	private HandlerService handlerService;

	@Override
	public Map<String, Object> getgetDeviceInfo(String decode) {
		SqlRowList rs = baseDao.queryForRowSet("select * from Device where de_code = ?", decode);
		if (rs.next()) {
			Map<String, Object> map = new HashMap<>();
			int[] arr = new int[0];
			String formCondition = "de_id = " + rs.getInt("de_id");
			String gridCondition = "da_deid = " + rs.getInt("de_id");
			Panel panel = panelService.getPanelByCaller("Device", formCondition, gridCondition,
					SystemSession.getUser().getEm_code());
			Map<String, Object> map1 = panelService.getGridPanel("DeviceResume",
					"dc_devcode = '" + decode + "' and nvl(dc_kind,' ' )<> ' '");
			map.put("formItem", panel.getPanelItems());
			map.put("formData", panel.getFormdata());
			map.put("grid1Item", panel.getColumns());
			map.put("grid1Data", panel.getListdata());
			map.put("grid2Item", map1.get("gridItem"));
			map.put("grid2Data", map1.get("gridData") == null ? arr : map1.get("gridData"));
			return map;
		} else {
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED, "未查询到设备" + decode + "的信息");
		}

	}

	@Override
	public Map<String, Object> getCheckQty(String caller, Integer id) {
		int db_unactionqty = baseDao.getCount(
				"select count(1) cn from DeviceChange where  dc_dbid =  " + id + "and nvl(dc_actionresult ,' ') = ' '");
		int db_actionqty = baseDao.getCount(
				"select count(1) cn from DeviceChange where dc_dbid = " + id + " and nvl(dc_actionresult ,' ') <> ' '");
		SqlRowList rs = baseDao.queryForRowSet("select * from devicebatch where db_id = ?", id);
		if (rs.next()) {
			Map<String, Object> map = rs.getCurrentMap();
			map.put("DB_UNACTIONQTY", db_unactionqty);
			map.put("DB_ACTIONQTY", db_actionqty);
			return map;
		}
		return null;
	}

	@Override
	public Map<String, Object> deviceStock(String caller, Integer id, String decode) {
		SqlRowList rs = baseDao.queryForRowSet("select db_statuscode from DeviceBatch where db_id = ?", id);
		String oldStatus = "";
		String oldStatuscode = "";
		if (rs.next()) {
			if (!("COMMITED").equals(rs.getString("db_statuscode"))) {
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED, "判定盘点只能是已提交状态");
			}
			SqlRowList rsDev = baseDao.queryForRowSet("select * from Device where de_code = ?", decode);
			if (rsDev.next()) {
				oldStatus = rsDev.getString("de_status");
				oldStatuscode = rsDev.getString("de_statuscode");
				rs = baseDao.queryForRowSet(
						"select dc_actionresult from devicechange where dc_devcode = ? and dc_dbid = ?", decode, id);
				if (rs.next()) {
					if (rs.getString("dc_actionresult") != null && !("").equals(rs.getString("dc_actionresult"))) {
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED, "设备" + decode + "已盘点");
					} else {
						baseDao.execute(
								"update DeviceChange set dc_actionresult='在库',dc_actiondate = sysdate where dc_devcode = ? and dc_dbid = ?",
								decode, id);
					}
				} else {
					String sql = "insert into DeviceChange (dc_id,dc_devcode,dc_devname,dc_dbid,dc_emcode,dc_emname,dc_class,dc_inman,dc_indate,dc_status,dc_statuscode,dc_centercode,dc_centername,dc_linecode,dc_kind,dc_actiondate,dc_actionresult,dc_oldstatus,dc_oldstatuscode)"
							+ " select DeviceChange_SEQ.nextval,'" + decode + "','" + rsDev.getString("de_name") + "',"
							+ id + ",'" + SystemSession.getUser().getEm_code() + "','"
							+ SystemSession.getUser().getEm_name()
							+ "','周期盘点',db_inman,db_indate,db_status,db_statuscode,db_centercode,db_centername,db_linecode,db_kind,sysdate,'盘盈','"
							+ oldStatus + "','" + oldStatuscode + "' from DeviceBatch" + " where db_id = " + id;
					baseDao.execute(sql);
				}
				int cn = baseDao.getCount(
						"select count(1) cn from DeviceChange where nvl(dc_actionresult,' ') = ' ' and dc_dbid = "
								+ id);
				Map<String, Object> map = rsDev.getCurrentMap();
				map.put("DB_UNACTIONQTY", cn);
				return map;
			} else {
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED, "设备编号" + decode + "不存在");
			}
		} else {
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED, "请检查单号是否存在");
		}
	}

	@Override
	public Map<String, Object> saveAndSubmitDeviceStock(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String code = baseDao.sGetMaxNumber("DeviceBatch", 2);
		int id = 0;
		if (store.get("db_linecode") == null && "".equals(store.get("db_linecode"))
				&& store.get("db_centercode") == null && "".equals(store.get("db_centercode"))
				&& store.get("db_devtype") == null && "".equals(store.get("db_devtype"))) {
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED, "线别、部门编号、设备类型不能都为空");
		}
		if (store.get("db_code") == null || "".equals(store.get("db_code"))) {
			store.put("db_code", code);
		}
		if (store.get("db_id") == null || "".equals(store.get("db_id"))) {
			id = baseDao.getSeqId("DEVICEBATCH_SEQ");
			store.put("db_id", id);
		}
		if (store.get("db_statuscode") == null || "".equals(store.get("db_statuscode"))) {
			store.put("db_statuscode", "ENTERING");
		}
		if (store.get("db_status") == null || "".equals(store.get("db_status"))) {
			store.put("db_status", "在录入");
		}
		if (store.get("db_inman") == null || "".equals(store.get("db_inman"))) {
			store.put("db_inman", SystemSession.getUser().getEm_name());
		}
		if (store.get("db_indate") == null || "".equals(store.get("db_indate"))) {
			Date dt = new Date();
			SimpleDateFormat matter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			store.put("db_indate", matter1.format(dt));
		}
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("DeviceChange", "dc_code='" + store.get("dc_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}

		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, "save", "before", new Object[] { store });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "DeviceBatch", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "db_id", store.get("db_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, "save", "after", new Object[] { store });

		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("DeviceBatch", "db_statuscode", "db_id=" + store.get("db_id"));
		if (status == null) {
			BaseUtil.showError("保存失败!");
		}
		StateAssert.submitOnlyEntering(status);
		if (store.get("db_id") != null && !("").equals(store.get("db_id"))) {
			id = Integer.valueOf(store.get("db_id").toString());
		}
		modelMap.put("db_id", id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, id);
		// 执行提交操作
		baseDao.submit("DeviceBatch", "db_id=" + id, "db_status", "db_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "db_id", id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, id);

		return modelMap;
	}

	@Override
	public Map<String, Object> saveAndSubmitDeviceChange(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String code = baseDao.sGetMaxNumber("DeviceChange", 2);
		int id = 0;

		if (store.get("dc_code") == null || "".equals(store.get("dc_code"))) {
			store.put("dc_code", code);
		}
		if (store.get("dc_id") == null || "".equals(store.get("dc_id"))) {
			id = baseDao.getSeqId("DEVICECHANGE_SEQ");
			store.put("dc_id", id);
		}
		if (store.get("dc_statuscode") == null || "".equals(store.get("dc_statuscode"))) {
			store.put("dc_statuscode", "ENTERING");
		}
		if (store.get("dc_status") == null || "".equals(store.get("dc_status"))) {
			store.put("dc_status", "在录入");
		}
		if (store.get("dc_inman") == null || "".equals(store.get("dc_inman"))) {
			store.put("dc_inman", SystemSession.getUser().getEm_name());
		}
		if (store.get("dc_indate") == null || "".equals(store.get("dc_indate"))) {
			Date dt = new Date();
			SimpleDateFormat matter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			store.put("dc_indate", matter1.format(dt));
		}
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("DeviceChange", "dc_code='" + store.get("dc_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "DeviceChange", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		if (store.get("dc_id") != null && !("").equals(store.get("dc_id"))) {
			id = Integer.valueOf(store.get("dc_id").toString());
		}
		baseDao.execute(
				"update DeviceChange set dc_deid = (select de_id from device where de_code = dc_devcode) where dc_id =?",
				id);
		if (("DeviceChange!Scrap").equals(caller)) {
			baseDao.execute("update devicechange set dc_kind = '报废' where dc_id =?", store.get("dc_id"));
		}
		if (("DeviceChange!Inspect").equals(caller)) {
			baseDao.execute(
					"update DeviceChange set dc_oldstatus = (select de_runstatus from device where de_code = dc_devcode) where dc_id = ?",
					id);
			baseDao.execute(
					"update Device set (de_runstatus,de_faultperformance,de_faultreason,de_sendcode,de_sendname,de_senddate)= "
							+ " (select 'BREAKING',dc_reason,dc_actionremark,dc_emcode,dc_emname,sysdate from Devicechange where  dc_id = ?) where exists(select 1 from devicechange where de_code = dc_devcode and dc_id = ?)",
					id, id);
		}
		// 记录操作
		baseDao.logger.save(caller, "dc_id", store.get("dc_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });

		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("DeviceChange", "dc_statuscode", "dc_id=" + store.get("dc_id"));
		if (status == null) {
			BaseUtil.showError("保存失败!");
		}
		StateAssert.submitOnlyEntering(status);
		modelMap.put("dc_id", id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, id);
		// 执行提交操作
		baseDao.submit("DeviceChange", "dc_id=" + id, "dc_status", "dc_statuscode");
		/*
		 * if(("DeviceChange!Inspect").equals(caller)){ baseDao.
		 * execute("update DeviceChange set dc_oldstatus = (select de_runstatus from device where de_code = dc_devcode) where dc_id = ?"
		 * ,id); baseDao.
		 * execute("update Device set de_runstatus = 'BREAKING' where de_code =(select dc_devcode from devicechange where dc_id = ?)"
		 * ,id); }
		 */
		// 记录操作
		baseDao.logger.submit(caller, "dc_id", id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, id);
		Object configs_ = baseDao.getFieldDataByCondition("configs", "data",
				"code='submitAutoAudit' and caller='" + caller + "'");
		if (StringUtil.hasText(configs_) && "1".equals(configs_)) {
			status = baseDao.getFieldDataByCondition("DeviceChange", "dc_statuscode", "dc_id=" + id);
			StateAssert.auditOnlyCommited(status);
			// 执行审核前的其它逻辑
			handlerService.beforeAudit(caller, new Object[] { id });
			baseDao.audit("DeviceChange", "dc_id=" + id, "dc_status", "dc_statuscode", "dc_auditdate", "dc_auditman");
			if (("DeviceChange!Inspect").equals(caller)) {
				SqlRowList rs = baseDao.queryForRowSet("select dc_actionresult from DeviceChange where dc_id=?", id);
				if (rs.next()) {
					if (("已维修").equals(rs.getString("dc_actionresult"))) {
						baseDao.execute(
								"update device set (de_runstatus,de_repairdate,de_repaircode,de_repairname) = (select dc_oldstatus,dc_repairdate, "
										+ " dc_repaircode,dc_repairname from devicechange where dc_id =?) where exists(select 1 from devicechange where de_code = dc_devcode and dc_id = ?)",
								id, id);
					}
				}
			}
			if (("DeviceChange!Scrap").equals(caller)) {
				SqlRowList rs = baseDao
						.queryForRowSet("select dc_devcode,dc_actiondate from deviceChange where dc_id = ?", id);
				if (rs.next()) {
					baseDao.execute(
							"update device set (de_runstatus,de_scrapdate)=(select 'SCRAPPED',dc_actiondate from devicechange where dc_id = ?) where exists(select 1 from devicechange where de_code = dc_devcode and dc_id = ?)",
							id, id);
					baseDao.execute("update devicechange set dc_kind = '报废' where dc_id =?", id);
				}
			}
			if (("DeviceChange!Maintain").equals(caller)) {
				// 更新设备主档的使用信息的保养信息
				SqlRowList rs = baseDao.queryForRowSet(
						"select de_maintenancecycle,dc_kind from devicechange left join device on de_code = dc_devcode where dc_id = ?",
						id);
				if (rs.next()) {
					// rs.getString("de_maintenancecycle")
					if (("校准").equals(rs.getString("dc_kind"))) {
						baseDao.execute(
								"update Device set (de_calibrationdate,de_calibrationcode,de_calibrationname) = (select dc_actiondate ,dc_emcode,dc_emname from devicechange where  dc_id = ?) where exists(select 1 from devicechange where de_code = dc_devcode and dc_id = ?)",
								id, id);
					} else if (("保养").equals(rs.getString("dc_kind"))) {
						baseDao.execute(
								"update Device set (de_maintenancedate,de_maintenancecode,de_maintenanceman,de_nextmaintenancedate) = (select dc_actiondate,dc_emcode ,dc_emname,dc_actiondate+(case when "
										+ rs.getInt("de_maintenancecycle") + " >0 then "
										+ rs.getInt("de_maintenancecycle")
										+ " else 0 end) from devicechange where dc_id = ?) where exists(select 1 from devicechange where de_code = dc_devcode and dc_id = ?)",
								id, id);
					}
				}
			}
			baseDao.logger.audit(caller, "dc_id", id);
			// 执行审核后的其它逻辑
			handlerService.afterAudit(caller, new Object[] { id });

		}
		return modelMap;

	}

	// 设备管理更新提交方法
	@Override
	public Map<String, Object> updateAndSubmitDeviceChange(String caller, String formStore) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的单据!
		Object status = baseDao.getFieldDataByCondition("DeviceChange", "dc_statuscode", "dc_id=" + store.get("dc_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "DeviceChange", "dc_id");
		baseDao.execute(formSql);
		int id = 0;
		if (store.get("dc_id") != null && !("").equals(store.get("dc_id"))) {
			id = Integer.valueOf(store.get("dc_id").toString());
		}
		baseDao.execute(
				"update DeviceChange set dc_deid = (select de_id from device where de_code = dc_devcode) where dc_id=?",
				id);
		if (("DeviceChange!Scrap").equals(caller)) {
			baseDao.execute("update devicechange set dc_kind = '报废' where dc_id =?", id);
		}
		if (("DeviceChange!Inspect").equals(caller)) {
			baseDao.execute(
					"update DeviceChange set dc_oldstatus = (select de_runstatus from device where de_code = dc_devcode) where dc_id = ?",
					id);
			baseDao.execute(
					"update Device set (de_runstatus,de_faultperformance,de_faultreason,de_sendcode,de_sendname,de_senddate)= "
							+ " (select 'BREAKING',dc_reason,dc_actionremark,dc_emcode,dc_emname,sysdate from Devicechange where  dc_id = ?) where exists(select 1 from devicechange where de_code = dc_devcode and dc_id = ?)",
					id, id);
		}
		// 记录操作
		baseDao.logger.update(caller, "dc_id", id);
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });

		// 只能对状态为[在录入]的订单进行提交操作!
		status = baseDao.getFieldDataByCondition("DeviceChange", "dc_statuscode", "dc_id=" + store.get("dc_id"));
		if (status == null) {
			BaseUtil.showError("保存失败!");
		}
		StateAssert.submitOnlyEntering(status);
		modelMap.put("dc_id", id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, id);
		// 执行提交操作
		baseDao.submit("DeviceChange", "dc_id=" + id, "dc_status", "dc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "dc_id", id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, id);
		return modelMap;
	}

	@Override
	public void confirmDeal(String caller, Integer id) {
		if (("DeviceChange!Use").equals(caller)) { // 设备使用转移单
			// 更新设备主档的使用信息的使用方信息
			SqlRowList rs = baseDao.queryForRowSet(
					"select de_runstatus,de_code,dc_kind from device left join devicechange on dc_devcode = de_code where dc_id = ?",
					id);
			if (rs.next()) {
				if (("领用").equals(rs.getString("dc_kind"))) {
					if (!("UNUSED").equals(rs.getString("de_runstatus"))) {
						BaseUtil.showError("设备不处于闲置状态,无法领用");
					}
				} /*
					 * else if(("转移").equals(rs.getString("dc_kind"))){
					 * if(("UNUSED").equals(rs.getString("de_runstatus"))){
					 * BaseUtil.showError("设备处于闲置状态,无法转移"); } }
					 */
				baseDao.execute(
						"update Device set (de_runstatus,de_currentcentercode,de_currentcentername,de_currentlinecode,de_managecode,de_manageman,de_gotdate,de_workshop) = (select 'USING',dc_centercode,dc_centername,dc_linecode,dc_emcode,dc_emname,dc_actiondate,dc_workshop from devicechange  where dc_id = ?) where exists(select 1 from devicechange where de_code = dc_devcode and dc_id = ?)",
						id, id);
				baseDao.logger.others("设备转移处理确认", "处理确认成功", caller, "dc_id", id);
			}
		} else if (("DeviceChange!Inspect").equals(caller)) {
			SqlRowList rs = baseDao.queryForRowSet("select dc_actionresult from DeviceChange where dc_id=?", id);
			if (rs.next()) {
				if (("已维修").equals(rs.getString("dc_actionresult"))) {
					baseDao.execute(
							"update device set (de_runstatus,de_repairdate,de_repaircode,de_repairname) = (select dc_oldstatus,dc_repairdate, "
									+ " dc_repaircode,dc_repairname from devicechange where  dc_id =?) where exists(select 1 from devicechange where de_code = dc_devcode and dc_id = ?)",
							id, id);
					baseDao.logger.others("设备故障送检确认", "处理确认成功", caller, "dc_id", id);
				}
			}
		}
	}

	@Override
	public Map<String, Object> getDevInfomation(String decode) {
		SqlRowList rs = baseDao.queryForRowSet("select * from device where de_code = ?", decode);
		if (rs.next()) {
			return rs.getCurrentMap();
		} else {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "设备编号" + decode + "不存在");
		}
	}

	// List<Map<String, Object>> map =
	// pdaOutMaterialService.getProdOut(inoutNo,whcode);
	@Override
	public List<Map<String, Object>> getDevModelInfo(String centercode, String linecode, String workshop,
			String devmodel) {
		SqlRowList rs = baseDao.queryForRowSet(
				"(select DM_CODE,DE_NAME,DE_SPEC,EXISTQTY,NEEDQTY,CASE WHEN (NEEDQTY-EXISTQTY)>=0 THEN (NEEDQTY-EXISTQTY) ELSE 0 END LACKQTY from "
						+ "(select dm_code DM_CODE,dmd_dename DE_NAME,dmd_despec DE_SPEC, sum(case when de_currentlinecode is null then 0  else 1 end) EXISTQTY,max(dmd_qty) NEEDQTY from DeviceModelDetail left join DeviceModel on dmd_dmid = dm_id left join device "
						+ "on dmd_dename = de_name and dmd_despec = de_spec and de_currentcentercode "
						+ "='"+centercode+"' and de_currentlinecode ='"+linecode+"' and de_workshop = '"+workshop+"' "
						+ "and de_runstatus='USING'  where dm_code='"+devmodel+"' and nvl(dmd_despec,' ')<> ' ' group by dm_code,dmd_dename,dmd_despec)) "
						+ "union ( "
						+ "select DM_CODE,DE_NAME,de_spec,EXISTQTY,NEEDQTY,CASE WHEN (NEEDQTY-EXISTQTY)>=0 THEN (NEEDQTY-EXISTQTY) ELSE 0 END LACKQTY from "
						+ "(select dm_code DM_CODE,dmd_dename DE_NAME,'' de_spec,sum(case when de_currentlinecode is null then 0  else 1 end)-nvl((select costqty from (select de_name name,sum(case when EXISTQTY<=NEEDQTY then EXISTQTY else NEEDQTY end) costqty from ("
						+ "select dm_code DM_CODE,dmd_dename DE_NAME,dmd_despec DE_SPEC, sum(case when de_currentlinecode is null then 0  else 1 end) EXISTQTY,max(dmd_qty) NEEDQTY,CASE "
						+ "WHEN (max(dmd_qty)-sum(case when de_currentlinecode is null then 0  else 1 end))>=0 THEN (max(dmd_qty)-sum(case when de_currentlinecode is null then 0  else 1 end)) ELSE 0 END LACKQTY from DeviceModelDetail left join DeviceModel on dmd_dmid = dm_id left  join  device "
						+ "on dmd_dename = de_name and dmd_despec = de_spec and de_currentcentercode "
						+ "='"+centercode+"' and de_currentlinecode ='"+linecode+"' and de_workshop = '"+workshop+"' "
						+ "and de_runstatus='USING'  where dm_code='"+devmodel+"' and nvl(dmd_despec,' ')<> ' ' group by dm_code,dmd_dename,dmd_despec) group by de_name) where name=dmd_dename),0)   EXISTQTY,max(dmd_qty) NEEDQTY from DeviceModelDetail left join DeviceModel on dmd_dmid = dm_id left join device "
						+ "on dmd_dename = de_name and de_currentcentercode ='"+centercode+"' "
						+ "and de_currentlinecode ='"+linecode+"' and de_workshop = '"+workshop+"' and de_runstatus='USING' where dm_code = '"+devmodel+"'  "
						+ "and nvl(dmd_despec,' ')= ' '  group by dm_code,dmd_dename))");
		if (rs.next()) {
			return rs.getResultList();
		}else{
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "没有符合条件的数据");
		}
	}

	@Override
	public void deviceInspectRes(String caller, int id) {
		String table = "";
		String keyfield = "";
		String status = "";
		String statuscode = "";
		if (caller != null && !("").equals(caller) && ("DeviceChange!Inspect").equals(caller)) {
			table = "DeviceChange";
			keyfield = "dc_id";
			status = "dc_status";
			statuscode = "dc_statuscode";
			// 只能对状态为[已提交]的订单进行反提交操作!
			Object thisstatus = baseDao.getFieldDataByCondition(table, statuscode, "" + keyfield + "=" + id);
			if (thisstatus == null) {
				BaseUtil.showError("该单已不存在");
			}
			StateAssert.resSubmitOnlyCommited(thisstatus);
			handlerService.handler(caller, "resCommit", "before", new Object[] { id });
			// 执行反提交操作
			baseDao.resOperate(table, "" + keyfield + "=" + id, status, statuscode);
			baseDao.execute(
					"update device set (de_runstatus,de_faultperformance,de_faultreason,de_sendcode,de_sendname,de_senddate) = "
							+ " (select dc_oldstatus,'','','','','' from devicechange where  dc_id = ?) where exists(select 1 from devicechange where de_code = dc_devcode and dc_id = ?)",
					id, id);
			// 记录操作
			baseDao.logger.resSubmit(caller, keyfield, id);
			handlerService.handler(caller, "resCommit", "after", new Object[] { id });
		}
	}

	@Override
	public Integer turnScrap(int dc_id, String caller) {
		SqlRowList rs = baseDao.queryForRowSet(
				"select dc_actionresult,dc_devcode,de_name,de_id,de_runstatus,dc_code,dc_id from devicechange left join device on dc_devcode = de_code where dc_id = ?",
				dc_id);
		if (rs.next()) {
			String decode = rs.getString("dc_devcode");
			if ("SCRAPPED".equals(rs.getString("de_runstatus"))) {
				throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "设备" + decode + "已经是报废状态");
			}
			if ("无法维修".equals(rs.getString("dc_actionresult"))) {
				SqlRowList rs1 = baseDao.queryForRowSet(
						"select dc_code,dc_id from devicechange where dc_devcode = ? and dc_class = '报废申请'",
						rs.getString("dc_devcode"));
				if (rs1.next()) {
					throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS,
							"已经存在此设备的报废申请单:" + rs1.getString("dc_code"));
				}
				String code = baseDao.sGetMaxNumber("DeviceChange!Scrap", 2);
				int id = baseDao.getSeqId("DEVICECHANGE_SEQ");
				String sql = "insert into Devicechange(dc_id,dc_code,dc_class,dc_centercode,dc_centername,dc_emcode,dc_emname,dc_devcode,dc_devname,dc_actiondate,dc_actionremark,dc_deid,dc_inman,dc_indate,dc_status,dc_statuscode,dc_kind)"
						+ "select " + id + ",'" + code + "','报废申请',dc_centercode,dc_centername,'"
						+ SystemSession.getUser().getEm_code() + "','" + SystemSession.getUser().getEm_name() + "','"
						+ decode + "','" + rs.getString("de_name") + "',sysdate,'无法维修,送修单" + rs.getString("dc_code")
						+ "转入'," + rs.getInt("de_id") + ",'" + SystemSession.getUser().getEm_name()
						+ "',sysdate,'在录入','ENTERING','报废' from devicechange where dc_id= " + rs.getInt("dc_id");
				baseDao.execute(sql);
				return id;
			} else {
				throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "故障处理状态必须为无法维修");
			}
		} else {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "设备或单据不存在");
		}
	}

	@Override
	public Map<String, Object> lossDevice(String caller, int id) {
		Object status = baseDao.getFieldDataByCondition("DeviceBatch", "db_statuscode", "db_id=" + id);
		if (!"COMMITED".equals(status)) {
			BaseUtil.showError("非提交状态不允许盘亏");
		}
		SqlRowList dev = baseDao.queryForRowSet(
				"select dc_id,dc_status,dc_actionresult,dc_detno from DeviceChange where nvl(dc_actionresult,' ') = ' ' and dc_dbid="
						+ id);
		if (dev.next()) {
			baseDao.execute(
					"update devicechange set dc_actionresult='盘亏' where dc_dbid=? and nvl(dc_actionresult,' ') = ' '",
					id);
			baseDao.logger.others("确认盘亏", "确认盘亏成功", caller, "db_id", id);
		} else {
			BaseUtil.showError("没有盘点结果为空的数据");
		}
		return null;
	}

	@Override
	public Map<String, Object> getDeviceAttribute(String caller, int id) {

		Object status = baseDao.getFieldDataByCondition("DeviceBatch", "db_statuscode", "db_id=" + id);
		if (!"COMMITED".equals(status)) {
			BaseUtil.showError("非提交状态不允许获取明细数据");
		}
		Object dc_detno = baseDao.getFieldDataByCondition("devicechange", "max(dc_detno) dc_detno", "dc_dbid =" + id);
		int size;
		if (dc_detno == null) {
			size = 0;
		} else {
			size = Integer.parseInt(String.valueOf(dc_detno));
		}
		String condition = " 1=1 ";
		SqlRowList rs = baseDao.queryForRowSet(
				"select db_centercode,db_linecode,db_devtype,db_workshop from devicebatch where db_id=?", id);
		if (rs.next()) {
			String centercode = (rs.getString("db_centercode") != null && !("").equals(rs.getString("db_centercode")))
					? "and de_currentcentercode = '" + rs.getString("db_centercode") + "'" : " ";
			String linecode = (rs.getString("db_linecode") != null && !("").equals(rs.getString("db_linecode")))
					? "and de_currentlinecode = '" + rs.getString("db_linecode") + "'" : " ";
			String devtype = (rs.getString("db_devtype") != null && !("").equals(rs.getString("db_devtype")))
					? "and de_type = '" + rs.getString("db_devtype") + "'" : " ";
			String workshop = (rs.getString("db_workshop") != null && !("").equals(rs.getString("db_workshop")))
					? "and de_workshop = '" + rs.getString("db_workshop") + "'" : " ";
			condition = condition + centercode + linecode + devtype + workshop;
		}
		rs = baseDao.queryForRowSet(
				"select de_code,de_name,de_inventorycode,de_inventoryname,de_currentcentercode,de_currentcentername,de_currentlinecode,de_type,de_inventorydate "
						+ " from device  where " + condition
						+ " and DE_RUNSTATUS not in ('LOSSED','SCRAPPED') and not exists (select 1 from devicechange where dc_dbid= "
						+ id + " and dc_devcode = de_code)");
		while (rs.next()) {
			size++;
			String sql = "insert into devicechange(dc_id,dc_centercode,dc_centername,dc_linecode,dc_devcode,dc_devname,dc_emcode,dc_emname,dc_actiondate,dc_actionresult,dc_dbid,dc_detno) values(DEVICECHANGE_SEQ.nextval,'"
					+ rs.getString("de_currentcentercode") + "','" + rs.getString("de_currentcentername") + "','"
					+ rs.getString("de_currentlinecode") + "','" + rs.getString("de_code") + "','"
					+ rs.getString("de_name") + "','" + SystemSession.getUser().getEm_code() + "','"
					+ SystemSession.getUser().getEm_name() + "',sysdate,''," + id + "," + size + ")";
			baseDao.execute(sql);
		}
		if (rs.size() == 0) {
			BaseUtil.showError("没有符合条件的数据");
		}
		SqlRowList returnDev = baseDao.queryForRowSet("select * from devicechange where dc_dbid = ?", id);
		if (returnDev.next()) {
			return returnDev.getCurrentMap();
		} else {
			BaseUtil.showError("没有符合条件的数据");
		}
		return null;

	}

}
