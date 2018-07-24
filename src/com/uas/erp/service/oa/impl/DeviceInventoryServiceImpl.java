package com.uas.erp.service.oa.impl;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.oa.DeviceInventoryService;
@Service
public class DeviceInventoryServiceImpl implements DeviceInventoryService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void saveDeviceInventory(String formStore, String caller, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("DeviceBatch",
				"db_code='" + store.get("db_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "DeviceBatch",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getInsertSqlbyList(gstore,
				"DeviceChange", "dc_id");
		baseDao.execute(gridSql);
		if(("DeviceBatch!Maintain").equals(caller)){
			baseDao.execute("update DeviceChange set dc_deid = (select de_id from device where de_code = dc_devcode) where dc_dbid=?",store.get("db_id"));
			baseDao.execute("update devicechange set (dc_code,dc_class,dc_kind,dc_actiondate,dc_actionresult,dc_actionremark,dc_centercode, "
				+" dc_centername,dc_emcode,dc_emname,dc_status,dc_statuscode,dc_inman,dc_indate,dc_auditdate,dc_auditman,dc_dbid) "
				+" =(select db_code,db_class,db_kind,db_actiondate,db_actionresult,db_actionremark,db_centercode,db_centername,db_emcode,db_emname,db_status,db_statuscode,db_inman,db_indate,db_auditdate,db_auditman,? from  devicebatch "
				+" where db_id = dc_dbid) where dc_dbid = ?",store.get("db_id"),store.get("db_id"));
		}
		// 记录操作
		baseDao.logger.save(caller, "db_id", store.get("db_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });

	}

	@Override
	public void updateDeviceInventoryById(String formStore, String caller, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的采购单资料!
		Object status = baseDao.getFieldDataByCondition("DeviceBatch",
				"db_statuscode", "db_id=" + store.get("db_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "DeviceBatch",
				"db_id");
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getInsertOrUpdateSqlbyGridStore(gstore, "DeviceChange", "dc_id");
		baseDao.execute(gridSql);
		if(("DeviceBatch!Maintain").equals(caller)){
			baseDao.execute("update DeviceChange set dc_deid = (select de_id from device where de_code = dc_devcode) where dc_dbid=?",store.get("db_id"));
			baseDao.execute("update devicechange set (dc_code,dc_class,dc_kind,dc_actiondate,dc_actionresult,dc_actionremark,dc_centercode, "
				+" dc_centername,dc_emcode,dc_emname,dc_status,dc_statuscode,dc_inman,dc_indate,dc_auditdate,dc_auditman,dc_dbid) "
				+" =(select db_code,db_class,db_kind,db_actiondate,db_actionresult,db_actionremark,db_centercode,db_centername,db_emcode,db_emname,db_status,db_statuscode,db_inman,db_indate,db_auditdate,db_auditman,? from  devicebatch "
				+" where db_id = dc_dbid) where dc_dbid = ?",store.get("db_id"),store.get("db_id"));
		}
		// 记录操作
		baseDao.logger.update(caller, "db_id", store.get("db_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteDeviceInventory(int db_id, String caller) {
		// TODO Auto-generated method stub
		Object status = baseDao.getFieldDataByCondition("DeviceBatch",
				"db_statuscode", "db_id=" + db_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { db_id });
		// 删除
		baseDao.deleteById("DeviceBatch", "db_id", db_id);
		baseDao.deleteById("DeviceChange", "dc_dbid", db_id);
		// 记录操作
		baseDao.logger.delete(caller, "db_id", db_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { db_id });
	}

	@Override
	public void auditDeviceInventory(int db_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("DeviceBatch",
				"db_statuscode", "db_id=" + db_id);
		StateAssert.auditOnlyCommited(status);
		if(("DeviceBatch!Stock").equals(caller)){
			Object[] objects = baseDao.getFieldsDataByCondition("DeviceBatch", "db_centercode,db_devtype,db_linecode", "db_id=" + db_id);
			if(objects==null){
				BaseUtil.showError("线别 、设备类型、部门至少填一项！");
			}
			int count = baseDao.getCount("select count(1) cn from Devicechange where dc_dbid = "+db_id);
			if(count == 0){
				BaseUtil.showError("不存在明细行,不允许审核");
			}
			SqlRowList rs = baseDao.queryForRowSet("select wm_concat(dc_devcode) dc_devcode from devicechange where dc_dbid =? and nvl(dc_actionresult,' ') = '盘盈' "
					+ "and nvl(dc_emcode,' ') = ' '",db_id);
			if(rs.next() && rs.getString("dc_devcode") != null){
				BaseUtil.showError("设备:"+rs.getString("dc_devcode")+"盘点结果为盘盈,盘点人员必填");
			}
			//判断明细行数据字段  盘亏  盘盈  为空
			rs = baseDao.queryForRowSet("select dc_id,dc_actionresult,dc_devcode from devicechange where dc_dbid=?", db_id);
			while(rs.next()){
				String dc_actionresult = rs.getString("dc_actionresult");
				String dc_devcode = rs.getString("dc_devcode");
				if(dc_actionresult==null || "".equals(dc_actionresult)){
					baseDao.execute("update devicechange set dc_actionresult='盘亏' where dc_dbid=? and dc_id=?",db_id,rs.getInt("dc_id"));
				}
				if(("盘亏".equals(dc_actionresult) || (dc_actionresult==null || "".equals(dc_actionresult)))&&dc_devcode!=null){
					baseDao.execute("update device set de_runstatus='LOSSED' where de_code=?",dc_devcode);
				}
				if("盘盈".equals(dc_actionresult)){
					SqlRowList forRowSet = baseDao.queryForRowSet("select db_centercode,db_centername,db_devtype,db_linecode,db_workshop from deviceBatch where db_id=?",db_id);
					while(forRowSet.next()){
						baseDao.execute("update device set de_currentcentercode=?,de_currentcentername= ?,de_currentlinecode=?,de_workshop=?  where de_code=?",forRowSet.getString("db_centercode"),forRowSet.getString("db_centername"),forRowSet.getString("db_linecode"),forRowSet.getString("db_workshop"),dc_devcode);
					}
				}
			}
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { db_id });
		baseDao.audit("DeviceBatch", "db_id=" + db_id, "db_status",
				"db_statuscode", "db_auditdate", "db_auditman");
		 if(("DeviceBatch!Stock").equals(caller)){
			 baseDao.execute("update devicechange set dc_deid  = (select de_id from device where de_code = dc_devcode),dc_kind = '周期盘点',dc_code = (select db_code from devicebatch where db_id = ?) where dc_dbid = ?",db_id,db_id);
			 baseDao.execute("update Device set (de_inventorydate,de_inventorycode,de_inventoryname) = (select sysdate ,'"+SystemSession.getUser().getEm_code()+"','"+SystemSession.getUser().getEm_name()+"' from devicechange where dc_dbid = ? "
						+" and dc_devcode = de_code) where exists (select 1 from devicechange where de_code = dc_devcode and dc_dbid = ?)",db_id,db_id);
		 }
		 
		baseDao.logger.audit(caller, "db_id", db_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { db_id });

	}

	@Override
	public void resAuditDeviceInventory(int db_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("DeviceBatch",
				"db_statuscode", "db_id=" + db_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("DeviceBatch", "db_id=" + db_id, "db_status",
				"db_statuscode", "db_auditdate", "db_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "db_id", db_id);
		
	}

	@Override
	public void submitDeviceInventory(int db_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("DeviceBatch",
				"db_statuscode", "db_id=" + db_id);
		StateAssert.submitOnlyEntering(status);
		Object[] objects = baseDao.getFieldsDataByCondition("DeviceBatch", "db_centercode,db_devtype,db_linecode", "db_id=" + db_id);
		if(objects==null){
			BaseUtil.showError("线别 、设备类型、部门至少填一项！");
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { db_id });
		// 执行提交操作
		baseDao.submit("DeviceBatch", "db_id=" + db_id, "db_status",
				"db_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "db_id", db_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { db_id });

	}

	@Override
	public void resSubmitDeviceInventory(int db_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("DeviceBatch",
				"db_statuscode", "db_id=" + db_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { db_id });
		// 执行反提交操作
		baseDao.resOperate("DeviceBatch", "db_id=" + db_id, "db_status",
				"db_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "db_id", db_id);
		handlerService.afterResSubmit(caller, new Object[] { db_id });

	}

	@Override
	public void lossDeviceInventory(int db_id, String caller) {
		//更新明细行状态为空的为盘亏。
		SqlRowList field = baseDao.queryForRowSet("select dc_id,dc_status,dc_actionresult,dc_detno from DeviceChange where nvl(dc_actionresult,' ') = ' ' and dc_dbid="+db_id);
		if(field.next()){
			baseDao.execute("update devicechange set dc_actionresult='盘亏' where dc_dbid=? and nvl(dc_actionresult,' ') = ' '",db_id);
			baseDao.logger.others("确认盘亏", "确认盘亏成功", caller,"db_id", db_id);
		}else{
			BaseUtil.showError("没有盘点结果为空的数据");
		}
	}

	@Override
	public void getDeviceAttribute(int db_id, String caller) {
//		SqlRowList rs = baseDao.queryForRowSet("select * from devicechange where dc_dbid = ?",db_id);
		Object dc_detno = baseDao.getFieldDataByCondition("devicechange", "max(dc_detno) dc_detno", "dc_dbid ="+db_id);
		int size;
		if(dc_detno == null){
			size = 0;
		}else{
			size = Integer.parseInt(String.valueOf(dc_detno));
		}
		String condition = " 1=1 ";
		SqlRowList rs = baseDao.queryForRowSet("select db_centercode,db_linecode,db_devtype,db_workshop from devicebatch where db_id=?",db_id);
		if(rs.next()){
			String centercode = (rs.getString("db_centercode")!=null && !("").equals(rs.getString("db_centercode")))?"and de_currentcentercode = '"+rs.getString("db_centercode")+"'":" ";
			String linecode = (rs.getString("db_linecode")!=null && !("").equals(rs.getString("db_linecode")))?"and de_currentlinecode = '"+rs.getString("db_linecode")+"'":" ";
			String devtype = (rs.getString("db_devtype")!=null && !("").equals(rs.getString("db_devtype")))?"and de_type = '"+rs.getString("db_devtype")+"'":" ";
			String workshop = (rs.getString("db_workshop")!=null && !("").equals(rs.getString("db_workshop")))?"and de_workshop = '"+rs.getString("db_workshop")+"'":" ";
			condition = condition+centercode+linecode+devtype+workshop;
		}
		rs = baseDao.queryForRowSet("select de_code,de_name,de_inventorycode,de_inventoryname,de_currentcentercode,de_currentcentername,de_currentlinecode,de_type,de_inventorydate "
				+" from device  where "+condition+" and DE_RUNSTATUS not in ('LOSSED','SCRAPPED') and not exists (select 1 from devicechange where dc_dbid= "+db_id
				+" and dc_devcode = de_code)");
		while(rs.next()){
			size++;
			String sql = "insert into devicechange(dc_id,dc_centercode,dc_centername,dc_linecode,dc_devcode,dc_devname,dc_emcode,dc_emname,dc_actiondate,dc_actionresult,dc_dbid,dc_detno) values(DEVICECHANGE_SEQ.nextval,'"+rs.getString("de_currentcentercode")+"','"+rs.getString("de_currentcentername")+"','"+rs.getString("de_currentlinecode")
			+"','"+rs.getString("de_code")+"','"+rs.getString("de_name")+"','"+SystemSession.getUser().getEm_code()+"','"+SystemSession.getUser().getEm_name()+"',sysdate,'',"+db_id+","+size+")";
			baseDao.execute(sql);
		}
		if(rs.size() == 0){
			BaseUtil.showError("没有符合条件的数据");
		}
	}

}
