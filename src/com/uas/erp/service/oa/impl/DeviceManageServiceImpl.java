package com.uas.erp.service.oa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.oa.DeviceManageService;

@Service
public class DeviceManageServiceImpl implements DeviceManageService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void saveDevice(String formStore, String caller,String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Device",
				"de_code='" + store.get("de_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Device",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getInsertSqlbyList(gstore,
				"DeviceAttribute", "da_id");
		baseDao.execute(gridSql);
		int count = baseDao.getCount("select count(1) cn from deviceattribute where da_deid = "+store.get("de_id"));
		if(count == 0){
			SqlRowList rs = baseDao.queryForRowSet("select de_kind from device where de_id = ?",store.get("de_id"));
			if(rs.next()){
				baseDao.execute("insert into deviceattribute(da_id,da_deid,da_detno,da_attname,remark)select DEVICEATTRIBUTE_SEQ.NEXTVAL,"+store.get("de_id")+", dka_detno,dka_attribute,dka_remark "
				+" from DeviceKindAttribute left join devicekind on dka_dkid = dk_id where dk_name=?",rs.getString("de_kind"));
			}
		}
		// 记录操作
		baseDao.logger.save(caller, "de_id", store.get("de_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void updateDeviceById(String formStore, String caller,String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的采购单资料!
		Object status = baseDao.getFieldDataByCondition("Device",
				"de_statuscode", "de_id=" + store.get("de_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Device",
				"de_id");
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getInsertOrUpdateSqlbyGridStore(gstore, "DeviceAttribute", "da_id");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "de_id", store.get("de_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteDevice(int de_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Device",
				"de_statuscode", "de_id=" + de_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { de_id });
		// 删除
		baseDao.deleteById("Device", "de_id", de_id);
		baseDao.deleteById("DeviceAttribute", "da_deid", de_id);
		// 记录操作
		baseDao.logger.delete(caller, "de_id", de_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { de_id });
	}

	@Override
	public void auditDevice(int de_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Device",
				"de_statuscode", "de_id=" + de_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { de_id });
		baseDao.audit("Device", "de_id=" + de_id, "de_status",
				"de_statuscode", "de_auditdate", "de_auditman");
		baseDao.logger.audit(caller, "de_id", de_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { de_id });
	}

	@Override
	public void resAuditDevice(int de_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Device",
				"de_statuscode", "de_id=" + de_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("Device", "de_id=" + de_id, "de_status",
				"de_statuscode", "de_auditdate", "de_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "de_id", de_id);
	}

	@Override
	public void submitDevice(int de_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Device",
				"de_statuscode", "de_id=" + de_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { de_id });
		// 执行提交操作
		baseDao.submit("Device", "de_id=" + de_id, "de_status",
				"de_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "de_id", de_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { de_id });
	}

	@Override
	public void resSubmitDevice(int de_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Device",
				"de_statuscode", "de_id=" + de_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { de_id });
		// 执行反提交操作
		baseDao.resOperate("Device", "de_id=" + de_id, "de_status",
				"de_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "de_id", de_id);
		handlerService.afterResSubmit(caller, new Object[] { de_id });
	}

	@Override
	public void vastMaintenanceDevice(String data,String caller) {
			List<Map<Object, Object>> list = BaseUtil.parseGridStoreToMaps(data);
			SqlRowList rs = null;
			Object id = null;
			Object de_dotype = null;
			String ids = CollectionUtil.pluckSqlString(list, "de_id");
			String error = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(de_code) from Device where de_id in(" + ids + ") and (nvl(de_status,' ') <>'已审核' OR nvl(de_statuscode,' ') <>'AUDITED') ",
					String.class);
			if (error != null) {
				BaseUtil.showError("只能保养已审核的设备,设备[" + error + "]未审核");
			}
			error = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(de_code) from Device where de_id in(" + ids + ") and nvl(de_runstatus,' ') = 'SCRAPPED' ",
					String.class);
			if (error != null) {
				BaseUtil.showError("设备[" + error + "]已报废");
			}
			for (Map<Object, Object> m : list) {
				id = m.get("de_id");
				if (m.containsKey("de_dotype") && !StringUtil.hasText(m.get("de_dotype"))) {
					BaseUtil.showError("保养类型没有填写!");
				}
				de_dotype = m.get("de_dotype");
				if (caller.equals("DeviceBatch!Maintain!Deal")) {
					rs = baseDao.queryForRowSet("select de_code,de_name,de_maintenancecycle from device where de_id = ?",id);
					if(rs.next()){
						//先插入到Barcodechange表,记录
						if(("保养").equals(m.get("de_dotype"))){
							baseDao.execute("insert into Devicechange (dc_id,dc_indate,dc_class,dc_emcode,dc_emname,dc_devcode,dc_devname,dc_kind,dc_inman,dc_deid,dc_actionresult,dc_actiondate) "
									+ "values(Devicechange_seq.nextval,sysdate,'批量保养','"+SystemSession.getUser().getEm_code()+"','"+SystemSession.getUser().getEm_name()+"','"+rs.getString("de_code")+"','"
									+rs.getString("de_name")+"','"+de_dotype+"','"+SystemSession.getUser().getEm_name()+"',"+id+",'批量保养',sysdate)");
							//更新设备信息
							baseDao.execute("update Device set de_maintenancedate=sysdate,de_maintenancecode='"+SystemSession.getUser().getEm_code()+"',de_maintenanceman='"+SystemSession.getUser().getEm_name()+"',de_nextmaintenancedate = sysdate+(case when "+rs.getInt("de_maintenancecycle")+" >0 then "+rs.getInt("de_maintenancecycle")+" else 0 end) where de_id = ?",id);
						}else if(("校准").equals(m.get("de_dotype"))){
							baseDao.execute("insert into Devicechange (dc_id,dc_indate,dc_class,dc_emcode,dc_emname,dc_devcode,dc_devname,dc_kind,dc_inman,dc_deid,dc_actionresult,dc_actiondate) "
									+ "values(Devicechange_seq.nextval,sysdate,'批量校准','"+SystemSession.getUser().getEm_code()+"','"+SystemSession.getUser().getEm_name()+"','"+rs.getString("de_code")+"','"
									+rs.getString("de_name")+"','"+de_dotype+"','"+SystemSession.getUser().getEm_name()+"',"+id+",'批量校准',sysdate)");
							//更新设备信息
							baseDao.execute("update Device set de_calibrationdate=sysdate,de_calibrationcode='"+SystemSession.getUser().getEm_code()+"',de_calibrationname='"+SystemSession.getUser().getEm_name()+"' where de_id = ?",id);
						}
					}
				}
			}
	}
}
