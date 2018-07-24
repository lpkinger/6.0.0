package com.uas.erp.service.oa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.oa.DeviceModelService;

@Service
public class DeviceModelServiceImpl implements DeviceModelService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	@Transactional
	public void saveDeviceModel(String formStore,String param, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("DeviceModel",
				"dm_code='" + store.get("dm_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "DeviceModel",
				new String[] {}, new Object[] {});
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "DeviceModelDetail", "dmd_id");
		String errors = baseDao
				.executeWithCheck(gridSql, null,
						"select wm_concat(dmd_dename) from devicemodeldetail where dmd_dmid = "+store.get("dm_id")+"and not exists(select 1 from device where de_name = dmd_dename)");
		if(errors != null && errors != "null"){
			BaseUtil.showError("设备名称: " + errors+"不存在");
		}
		SqlRowList rs = baseDao.queryForRowSet("select wm_concat(dmd_dename) dmd_dename from (select distinct dmd_dename||':'||dmd_despec dmd_dename from devicemodeldetail where dmd_dmid = "+store.get("dm_id") +"group by dmd_dename||':'||dmd_despec having count(1)>1)");
		if(rs.next() && rs.getString("dmd_dename") != null){
			BaseUtil.showError("设备名称规格: " + rs.getString("dmd_dename")+"  重复");
		}
		baseDao.execute(formSql);
		/*baseDao.execute(gridSql);*/
		// 记录操作
		baseDao.logger.save(caller, "dm_id", store.get("dm_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	@Transactional
	public void updateDeviceModel(String formStore,String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param);
		// 只能修改[在录入]的采购单资料!
		Object status = baseDao.getFieldDataByCondition("DeviceModel",
				"dm_statuscode", "dm_id=" + store.get("dm_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "DeviceModel",
				"dm_id");
		baseDao.execute(formSql);
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(grid, "DeviceModelDetail", "dmd_id"));
		checkDevice(store.get("dm_id"));
		// 记录操作
		baseDao.logger.update(caller, "dm_id", store.get("dm_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteDeviceModel(int dm_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("DeviceModel",
				"dm_statuscode", "dm_id=" + dm_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { dm_id });
		// 删除
		baseDao.deleteById("DeviceModel", "dm_id", dm_id);
		baseDao.deleteById("DeviceModelDetail", "dmd_dmid", dm_id);
		// 记录操作
		baseDao.logger.delete(caller, "dm_id", dm_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { dm_id });
	}

	@Override
	public void auditDeviceModel(int dm_id, String caller) {

		Object status = baseDao.getFieldDataByCondition("DeviceModel",
				"dm_statuscode", "dm_id=" + dm_id);
		StateAssert.auditOnlyCommited(status);
		checkDevice(dm_id);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { dm_id });
		baseDao.audit("DeviceModel", "dm_id=" + dm_id, "dm_status",
				"dm_statuscode", "dm_auditdate", "dm_auditman");
		baseDao.logger.audit(caller, "dm_id", dm_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { dm_id });
	}

	@Override
	public void resAuditDeviceModel(int dm_id, String caller) {

		Object status = baseDao.getFieldDataByCondition("DeviceModel",
				"dm_statuscode", "dm_id=" + dm_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("DeviceModel", "dm_id=" + dm_id, "dm_status",
				"dm_statuscode", "dm_auditdate", "dm_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "dm_id", dm_id);
	}

	@Override
	public void submitDeviceModel(int dm_id, String caller) {

		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("DeviceModel",
				"dm_statuscode", "dm_id=" + dm_id);
		StateAssert.submitOnlyEntering(status);
		checkDevice(dm_id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { dm_id });
		// 执行提交操作
		baseDao.submit("DeviceModel", "dm_id=" + dm_id, "dm_status",
				"dm_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "dm_id", dm_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { dm_id });
	}

	@Override
	public void resSubmitDeviceModel(int dm_id, String caller) {

		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("DeviceModel",
				"dm_statuscode", "dm_id=" + dm_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { dm_id });
		// 执行反提交操作
		baseDao.resOperate("DeviceModel", "dm_id=" + dm_id, "dm_status",
				"dm_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "dm_id", dm_id);
		handlerService.afterResSubmit(caller, new Object[] { dm_id });
	}
	
	private void checkDevice(Object dm_id){
		SqlRowList rs  = baseDao.queryForRowSet("select wm_concat(dmd_dename) dmd_dename from devicemodeldetail where dmd_dmid = ? and not exists(select 1 from device where de_name = dmd_dename)",dm_id);
		if(rs.next() && rs.getString("dmd_dename") != null){
			BaseUtil.showError("设备名称: "+rs.getString("dmd_dename")+"不存在");
		}
		rs = baseDao.queryForRowSet("select wm_concat(dmd_dename) dmd_dename from (select distinct dmd_dename||':'||dmd_despec dmd_dename from devicemodeldetail where dmd_dmid = ? group by dmd_dename||':'||dmd_despec having count(1)>1)",dm_id);
		if(rs.next() && rs.getString("dmd_dename") != null){
			BaseUtil.showError("设备名称规格: "+rs.getString("dmd_dename")+"  重复");
		}
	}

}
