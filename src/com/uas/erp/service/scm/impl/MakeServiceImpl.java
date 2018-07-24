package com.uas.erp.service.scm.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.Des;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.MakeService;

@Service("makeService")
public class MakeServiceImpl implements MakeService{
	
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void saveMake(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Make", "ma_code='" + store.get("ma_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		handlerService.beforeSave(caller, new Object[]{store, gstore});
		//保存Make
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Make", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//保存MakeMaterial
		for(Map<Object, Object> map:gstore){
			map.put("mm_id", baseDao.getSeqId("MAKEMATERIAL_SEQ"));
			map.put("mm_status", "ENTERING");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gstore, "MakeMaterial");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "ma_id", store.get("ma_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store, gstore});
	}
	
	@Override
	public void deleteMake(int ma_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("Make", "ma_statuscode", "ma_id=" + ma_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, ma_id);
		if(baseDao.isDBSetting("usingMakeCraft")){
			int count=baseDao.getCount("select count(1) from makecraft where mc_makecode=(select ma_code from make where ma_id="+ma_id+")");
			if(count>0){
				BaseUtil.showError("存在作业单不能删除");
			}
		}
		//删除Make
		baseDao.deleteById("Make", "ma_id", ma_id);
		//删除MakeMaterial
		baseDao.deleteById("MakeMaterial", "mm_maid", ma_id);
		//记录操作
		baseDao.logger.delete(caller, "ma_id", ma_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, ma_id);
	}
	
	@Override
	public void updateMakeById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的单据资料!
		Object status = baseDao.getFieldDataByCondition("Make", "ma_statuscode", "ma_id=" + store.get("ma_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store, gstore});
		//修改Make
		store.put("ma_updateman", SystemSession.getUser().getEm_name());
		store.put("ma_updatedate", DateUtil.currentDateString(null));
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Make", "ma_id");
		baseDao.execute(formSql);
		//修改MakeMaterial
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "MakeMaterial", "mm_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("mm_id") == null || s.get("mm_id").equals("") || s.get("mm_id").equals("0") ||
					Integer.parseInt(s.get("mm_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("MAKEMATERIAL_SEQ");
				s.put("mm_status", "ENTERING");
				String sql = SqlUtil.getInsertSqlByMap(s, "MakeMaterial", new String[]{"mm_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "ma_id", store.get("ma_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store, gstore});
	}
	
	@Override
	public String[] printMake(int ma_id, String caller, String reportName, String condition) {
		//执行打印前的其它逻辑
		handlerService.beforePrint(caller, ma_id);
		//执行打印操作
		String key = "12345678";
		String[] keys = new String[4];
		Des de = new Des();
		try {
			String name = URLEncoder.encode(reportName, "utf-8").toLowerCase();
	        keys[0] = de.toHexString(de.encrypt(name, key)).toUpperCase(); 
	        
	        String skey = URLEncoder.encode(key, "utf-8").toLowerCase(); 
	        keys[1] = de.toHexString(de.encrypt(skey, key)).toUpperCase();
	        
	        String cond = java.net.URLEncoder.encode(condition, "utf-8").toLowerCase(); 
			keys[2] = de.toHexString(de.encrypt(cond, key)).toUpperCase();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
	        String lyTime = sdf.format(new java.util.Date());  
	        String time = java.net.URLEncoder.encode(lyTime, "utf-8").toLowerCase(); 
	        keys[3] = de.toHexString(de.encrypt(time, key)).toUpperCase();
	        
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		baseDao.print("Make", "ma_id=" + ma_id, "MA_PRINTSTATUS", "MA_PRINTSTATUSCODE");
		//记录操作
		baseDao.logger.print(caller, "ma_id", ma_id);
		//执行打印后的其它逻辑
		handlerService.afterPrint(caller, ma_id);
	    return keys;
	}
	
	@Override
	public void auditMake(int ma_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Make", "ma_statuscode", "ma_id=" + ma_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ma_id);
		//执行审核操作
		baseDao.audit("Make", "ma_id=" + ma_id, "ma_status", "ma_statuscode", "ma_auditdate", "ma_auditman");
		baseDao.updateByCondition("MakeMaterial", "mm_status='AUDITED", "mm_maid=" + ma_id);
		//记录操作
		baseDao.logger.audit(caller, "ma_id", ma_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, ma_id);
	}
	
	@Override
	public void resAuditMake(int ma_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Make", "ma_statuscode", "ma_id=" + ma_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("Make", "ma_id=" + ma_id, "ma_status", "ma_statuscode");
		baseDao.updateByCondition("MakeMaterial", "mm_status='ENTERING", "mm_maid=" + ma_id);
		//记录操作
		baseDao.logger.resAudit(caller, "ma_id", ma_id);
	}
	
	@Override
	public void submitMake(int ma_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Make", "ma_statuscode", "ma_id=" + ma_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ma_id);
		//执行提交操作
		baseDao.submit("Make", "ma_id=" + ma_id, "ma_status", "ma_statuscode");
		baseDao.updateByCondition("MakeMaterial", "mm_status='COMMITED", "mm_maid=" + ma_id);
		//记录操作
		baseDao.logger.submit(caller, "ma_id", ma_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ma_id);
	}
	
	@Override
	public void resSubmitMake(int ma_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Make", "ma_statuscode", "ma_id=" + ma_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, ma_id);
		//执行反提交操作
		baseDao.resOperate("Make", "ma_id=" + ma_id, "ma_status", "ma_statuscode");
		baseDao.updateByCondition("MakeMaterial", "mm_status='ENTERING", "mm_maid=" + ma_id);
		//记录操作
		baseDao.logger.resSubmit(caller, "ma_id", ma_id);
		handlerService.afterResSubmit(caller, ma_id);
	}

	@Override
	public void bannedMake(int ma_id, String caller) {
		//执行禁用操作
		baseDao.banned("Make", "ma_id=" + ma_id, "ma_status", "ma_statuscode");
		baseDao.updateByCondition("MakeMaterial", "mm_status='DISABLE", "mm_maid=" + ma_id);
		//记录操作
		baseDao.logger.banned(caller, "ma_id", ma_id);
	}

	@Override
	public void resBannedMake(int ma_id, String caller) {
		//执行反禁用操作
		baseDao.resOperate("Make", "ma_id=" + ma_id, "ma_status", "ma_statuscode");
		baseDao.updateByCondition("MakeMaterial", "mm_status='ENTERING", "mm_maid=" + ma_id);
		//记录操作
		baseDao.logger.resBanned(caller, "ma_id", ma_id);
	}
}
