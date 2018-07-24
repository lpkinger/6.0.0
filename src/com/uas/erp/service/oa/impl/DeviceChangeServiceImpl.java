package com.uas.erp.service.oa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.oa.DeviceChangeService;

@Service
public class DeviceChangeServiceImpl implements DeviceChangeService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveDeviceChange(String formStore, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("DeviceChange",
				"dc_code='" + store.get("dc_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "DeviceChange",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.execute("update DeviceChange set dc_deid = (select de_id from device where de_code = dc_devcode) where dc_id=?",store.get("dc_id"));
		if(("DeviceChange!Scrap").equals(caller)){
			baseDao.execute("update devicechange set dc_kind = '报废' where dc_id =?",store.get("dc_id"));
		}
		// 记录操作
		baseDao.logger.save(caller, "dc_id", store.get("dc_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void updateDeviceChange(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的采购单资料!
		Object status = baseDao.getFieldDataByCondition("DeviceChange",
				"dc_statuscode", "dc_id=" + store.get("dc_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "DeviceChange",
				"dc_id");
		baseDao.execute(formSql);
		baseDao.execute("update DeviceChange set dc_deid = (select de_id from device where de_code = dc_devcode) where dc_id=?",store.get("dc_id"));
		if(("DeviceChange!Scrap").equals(caller)){
			baseDao.execute("update devicechange set dc_kind = '报废' where dc_id =?",store.get("dc_id"));
		}
		// 记录操作
		baseDao.logger.update(caller, "dc_id", store.get("dc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteDeviceChange(int dc_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("DeviceChange",
				"dc_statuscode", "dc_id=" + dc_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { dc_id });
		// 删除
		baseDao.deleteById("DeviceChange", "dc_id", dc_id);
		// 记录操作
		baseDao.logger.delete(caller, "dc_id", dc_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { dc_id });
	}

	@Override
	public void auditDeviceChange(int dc_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("DeviceChange",
				"dc_statuscode", "dc_id=" + dc_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { dc_id });
		if(("DeviceChange!Inspect").equals(caller)){			
			SqlRowList rs= baseDao.queryForRowSet("select dc_actionresult from DeviceChange where dc_id=?",dc_id);
			if(rs.next()){
				if(("已维修").equals(rs.getString("dc_actionresult"))){
					baseDao.execute("update device set (de_runstatus,de_repairdate,de_repaircode,de_repairname) = (select dc_oldstatus,dc_repairdate, "
							+" dc_repaircode,dc_repairname from devicechange where dc_id =?)"
							+" where exists(select 1 from devicechange where de_code = dc_devcode and dc_id = ?)",dc_id,dc_id);
				}
			}
		}
		if(("DeviceChange!Scrap").equals(caller)){
			SqlRowList rs = baseDao.queryForRowSet("select dc_devcode,dc_actiondate from deviceChange where dc_id = ?",dc_id);
			if(rs.next()){				
				baseDao.execute("update device set (de_runstatus,de_scrapdate)=(select 'SCRAPPED',dc_actiondate from devicechange where dc_id = ? ) where exists(select 1 from devicechange where de_code = dc_devcode and dc_id = ?)",dc_id,dc_id );
				baseDao.execute("update devicechange set dc_kind = '报废' where dc_id =?",dc_id);
			}
		}
		if(("DeviceChange!Maintain").equals(caller)){
			//更新设备主档的使用信息的保养信息
			SqlRowList rs = baseDao.queryForRowSet("select de_maintenancecycle,dc_kind from devicechange left join device on de_code = dc_devcode where dc_id = ?",dc_id);
			if(rs.next()){
				//   rs.getString("de_maintenancecycle")
				if(("校准").equals(rs.getString("dc_kind"))){
					baseDao.execute("update Device set (de_calibrationdate,de_calibrationcode,de_calibrationname) = (select dc_actiondate ,dc_emcode,dc_emname from devicechange where dc_id = ? ) where exists(select 1 from devicechange where de_code = dc_devcode and dc_id = ?)",dc_id,dc_id);
				}else if(("保养").equals(rs.getString("dc_kind"))){					
					baseDao.execute("update Device set (de_maintenancedate,de_maintenancecode,de_maintenanceman,de_nextmaintenancedate) = (select dc_actiondate,dc_emcode ,dc_emname,dc_actiondate+(case when "+rs.getInt("de_maintenancecycle")+" >0 then "+rs.getInt("de_maintenancecycle")+" else 0 end) from devicechange where dc_id = ?) where exists(select 1 from devicechange where de_code = dc_devcode and dc_id = ?)",dc_id,dc_id);
				}
			}
		}
		if(("DeviceChange!Use").equals(caller)){ //设备使用转移单
			//更新设备主档的使用信息的使用方信息
			SqlRowList rs = baseDao.queryForRowSet("select de_runstatus,de_code,dc_kind from device left join devicechange on dc_devcode = de_code where dc_id = ?",dc_id);
			if(rs.next()){
				if(("领用").equals(rs.getString("dc_kind"))){
					if(!("UNUSED").equals(rs.getString("de_runstatus"))){
						BaseUtil.showError("设备不处于闲置状态,无法领用");
					}
				}
				baseDao.execute("update Device set (de_runstatus,de_currentcentercode,de_currentcentername,de_currentlinecode,de_managecode,de_manageman,de_gotdate,de_workshop) = (select 'USING',dc_centercode,dc_centername,dc_linecode,dc_emcode,dc_emname,dc_actiondate,dc_workshop from devicechange where dc_id = ?) where exists(select 1 from devicechange where de_code = dc_devcode and dc_id = ?)",dc_id,dc_id);
			}
		}
		baseDao.audit("DeviceChange", "dc_id=" + dc_id, "dc_status",
				"dc_statuscode", "dc_auditdate", "dc_auditman");
		baseDao.logger.audit(caller, "dc_id", dc_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { dc_id });
	}

	@Override
	public void resAuditDeviceChange(int dc_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("DeviceChange",
				"dc_statuscode", "dc_id=" + dc_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("DeviceChange", "dc_id=" + dc_id, "dc_status",
				"dc_statuscode", "dc_auditdate", "dc_auditman");
		if(("DeviceChange!Inspect").equals(caller)){			
			baseDao.execute("update device set de_runstatus = (select dc_oldstatus from devicechange where dc_id = ?) where exists(select 1 from devicechange where de_code = dc_devcode and dc_id = 3873)",dc_id,dc_id);
		}
		if(("DeviceChange!Use").equals(caller)){
			BaseUtil.showError("设备使用申请不允许反审核");
		}
		// 记录操作
		baseDao.logger.resAudit(caller, "dc_id", dc_id);
	}

	@Override
	public void submitDeviceChange(int dc_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("DeviceChange",
				"dc_statuscode", "dc_id=" + dc_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { dc_id });
		// 执行提交操作
		baseDao.submit("DeviceChange", "dc_id=" + dc_id, "dc_status",
				"dc_statuscode");
		if(("DeviceChange!Inspect").equals(caller)){			
			baseDao.execute("update DeviceChange set dc_oldstatus = (select de_runstatus from device where de_code = dc_devcode) where dc_id = ?",dc_id);
			baseDao.execute("update Device set (de_runstatus,de_faultperformance,de_faultreason,de_sendcode,de_sendname,de_senddate)= "
					+ " (select 'BREAKING',dc_reason,dc_actionremark,dc_emcode,dc_emname,sysdate from Devicechange where dc_id = ?) where exists(select 1 from devicechange where de_code = dc_devcode and dc_id = ?)",dc_id,dc_id);
		}
		// 记录操作
		baseDao.logger.submit(caller, "dc_id", dc_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { dc_id });
	}

	@Override
	public void resSubmitDeviceChange(int dc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("DeviceChange",
				"dc_statuscode", "dc_id=" + dc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { dc_id });
		// 执行反提交操作
		baseDao.resOperate("DeviceChange", "dc_id=" + dc_id, "dc_status",
				"dc_statuscode");
		if(("DeviceChange!Inspect").equals(caller)){			
			baseDao.execute("update device set (de_runstatus,de_faultperformance,de_faultreason,de_sendcode,de_sendname,de_senddate) = "
					+" (select dc_oldstatus,'','','','','' from devicechange where  dc_id = ?) where exists(select 1 from devicechange where de_code = dc_devcode and dc_id = ?)",dc_id,dc_id);
		}
		// 记录操作
		baseDao.logger.resSubmit(caller, "dc_id", dc_id);
		handlerService.afterResSubmit(caller, new Object[] { dc_id });
	}

	@Override
	public void confirmDeal(int dc_id, String caller) {
		
		 if(("DeviceChange!Inspect").equals(caller)){
			SqlRowList rs= baseDao.queryForRowSet("select dc_actionresult from DeviceChange where dc_id=?",dc_id);
			if(rs.next()){
				if(("已维修").equals(rs.getString("dc_actionresult"))){
					baseDao.execute("update device set (de_runstatus,de_repairdate,de_repaircode,de_repairname) = (select dc_oldstatus,dc_repairdate, "
							+" dc_repaircode,dc_repairname from devicechange where dc_id =?) where exists(select 1 from devicechange where de_code = dc_devcode and dc_id = ?)",dc_id,dc_id);
					baseDao.logger.others("设备故障送检确认", "处理确认成功", caller, "dc_id", dc_id);
				}
			}
		}else if("DeviceBatch!Use".equals(caller)){
			String decodeUsed = null;
			String decodeUnUsed = null;
			SqlRowList rs = baseDao.queryForRowSet("select wm_concat(dc_devcode) dc_devcode from devicechange where dc_dbid = ? and not exists(select 1 from device where de_code = dc_devcode)",dc_id);
			if(rs.next() && rs.getString("dc_devcode")!=null){
				BaseUtil.showError("设备"+rs.getString("dc_devcode")+"不存在");
			}
			 rs = baseDao.queryForRowSet("select wm_concat(dc_devcode) dc_devcode from devicechange left join device on de_code = dc_devcode where dc_dbid = ? and nvl(de_runstatus,' ') <> 'UNUSED'",dc_id);
			if(rs.next() && rs.getString("dc_devcode")!=null){
				decodeUsed = rs.getString("dc_devcode");
			} 
			 rs = baseDao.queryForRowSet("select wm_concat(dc_devcode) dc_devcode from devicechange left join device on de_code = dc_devcode where dc_dbid = ? and nvl(de_runstatus,' ') = 'UNUSED'",dc_id);
				if(rs.next() && rs.getString("dc_devcode")!=null){
					decodeUnUsed = rs.getString("dc_devcode");
			} 
			rs = baseDao.queryForRowSet("select db_kind,db_code from deviceBatch where db_id = ?",dc_id);
			if(rs.next()){
				if(("领用").equals(rs.getString("db_kind"))){
					if(decodeUsed != null && !"".equals(decodeUsed)){
						BaseUtil.showError("设备"+decodeUsed+"不处于闲置状态,无法领用");
					}
				}/*else if(("转移").equals(rs.getString("db_kind"))){
					if(decodeUnUsed != null && !"".equals(decodeUnUsed)){
						BaseUtil.showError("设备"+decodeUnUsed+"处于闲置状态,无法转移");
					}
				}*/
			}
			baseDao.execute("update Device set (de_runstatus,de_currentcentercode,de_currentcentername,de_currentlinecode,de_managecode,de_manageman,de_gotdate,de_workshop) = (select 'USING',db_centercode,db_centername,db_linecode,db_emcode,db_emname,db_actiondate,db_workshop from deviceBatch where db_id = ?) where exists(select 1 from devicechange where de_code = dc_devcode and dc_dbid = ?)",dc_id,dc_id);
			baseDao.execute("insert into Devicechange (dc_id,dc_indate,dc_class,dc_emcode,dc_emname,dc_devcode,dc_devname,dc_kind,dc_inman,dc_deid,dc_actionresult,dc_actiondate) "
					+ "select Devicechange_seq.nextval,sysdate,'批量"+rs.getString("db_kind")+"','"+SystemSession.getUser().getEm_code()+"','"+SystemSession.getUser().getEm_name()+"',de_code,de_name,'"+rs.getString("db_kind")+"','"+SystemSession.getUser().getEm_name()+"',de_id,'"+rs.getString("db_code")+"批量"+rs.getString("db_kind")+"成功',sysdate from device left join devicechange on dc_devcode = de_code where  dc_dbid=?",dc_id);
			baseDao.logger.others("设备转移处理确认", "处理确认成功", caller, "db_id", dc_id);
		}
		
	}

	@Override
	public String turnScrap(int dc_id, String caller) {
		SqlRowList rs = baseDao.queryForRowSet("select dc_actionresult,dc_devcode,de_name,de_id,de_runstatus,dc_code,dc_id from devicechange left join device on dc_devcode = de_code where dc_id = ?",dc_id);
		if(rs.next()){
			String decode = rs.getString("dc_devcode");	
			if("SCRAPPED".equals(rs.getString("de_runstatus"))){
				BaseUtil.showError("设备"+decode+"已经是报废状态");
			}
			if("无法维修".equals(rs.getString("dc_actionresult"))){
				SqlRowList rs1 = baseDao.queryForRowSet("select dc_code,dc_id from devicechange where dc_devcode = ? and dc_class = '报废申请'",rs.getString("dc_devcode"));
				if(rs1.next()){
					BaseUtil.showError("已经存在此设备的报废申请单:"+"<a href=\"javascript:openUrl('jsps/oa/device/deviceChange.jsp?whoami=DeviceChange!Scrap&formCondition=dc_idIS" + rs1.getInt("dc_id")
							+ "')\">" + rs1.getString("dc_code") + "</a>&nbsp;");
				}
				String code = baseDao.sGetMaxNumber("DeviceChange!Scrap", 2);
				Integer id = baseDao.getSeqId("DEVICECHANGE_SEQ");
				String sql = "insert into Devicechange(dc_id,dc_code,dc_class,dc_centercode,dc_centername,dc_emcode,dc_emname,dc_devcode,dc_devname,dc_actiondate,dc_actionremark,dc_deid,dc_inman,dc_indate,dc_status,dc_statuscode,dc_kind)"
						+ "select "+id+",'"+code+"','报废申请',dc_centercode,dc_centername,'"+SystemSession.getUser().getEm_code()+"','"+SystemSession.getUser().getEm_name()+"','"+decode+"','"+rs.getString("de_name")+
						"',sysdate,'无法维修,送修单"+rs.getString("dc_code")+"转入',"+rs.getInt("de_id")+",'"+SystemSession.getUser().getEm_name()+"',sysdate,'在录入','ENTERING','报废' from devicechange where dc_id= "+rs.getInt("dc_id");
				baseDao.execute(sql);
				StringBuffer sb = new StringBuffer();
				sb.append("转入成功,报废申请号:"+"<a href=\"javascript:openUrl('jsps/oa/device/deviceChange.jsp?whoami=DeviceChange!Scrap&formCondition=dc_idIS" + id
						+ "')\">" + code + "</a>&nbsp;");
				return sb.toString();
			}else{
				BaseUtil.showError("故障处理状态必须为无法维修");
			}
		}else{
			BaseUtil.showError("设备或单据不存在");
		}
		return null;
	}

}
