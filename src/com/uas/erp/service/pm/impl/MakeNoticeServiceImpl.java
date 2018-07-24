package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.MakeNoticeDao;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.pm.MakeNoticeService;

@Service("makeNoticeService")
public class MakeNoticeServiceImpl implements MakeNoticeService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private MakeNoticeDao makeNoticeDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveMakeNotice(String formStore, String gridStore,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid= BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("MakeNotice", "mn_id='" + store.get("mn_id") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store,grid});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "MakeNotice", new String[]{}, new Object[]{});
		baseDao.execute(formSql);		
		//保存MakeNoticeDetail
		for(Map<Object, Object> s:grid){
			s.put("mnd_id", baseDao.getSeqId("MAKENOTICEDETAIL_SEQ"));
			s.put("mnd_statuscode", "ENTERING");
			s.put("mnd_status", BaseUtil.getLocalMessage("ENTERING"));
			s.put("mnd_kind", store.get("mn_kind"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "MakeNoticeDetail");
		baseDao.execute(gridSql);	
		try{
			//记录操作
			baseDao.logger.save(caller, "mn_id", store.get("mn_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}		
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store,grid});
	}
	
	@Override
	public void deleteMakeNotice(int mn_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("MakeNotice", "mn_statuscode", "mn_id=" + mn_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[]{mn_id});
		//删除MakeNotice
		baseDao.deleteById("MakeNotice", "mn_id", mn_id);	
		//删除MakeNoticeDetail
				baseDao.deleteById("MakeNoticeDetail", "mnd_mnid", mn_id);
		//记录操作
		baseDao.logger.delete(caller, "mn_id", mn_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[]{mn_id});
	}
	
	@Override
	public void updateMakeNoticeById(String formStore,String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("MakeNotice", "mn_statuscode", "mn_id=" + store.get("mn_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller,new Object[]{store,gstore});
		//修改MakeNotice
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "MakeNotice", "mn_id");
		baseDao.execute(formSql);
		//修改MakeNoticeDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "MakeNoticeDetail", "mnd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("mnd_id") == null || s.get("mnd_id").equals("") || s.get("mnd_id").equals("0") || 
					Integer.parseInt(s.get("mnd_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("MAKENOTICEDETAIL_SEQ");
				s.put("mnd_id", id);
				s.put("mnd_statuscode", "ENTERING");
				s.put("mnd_status", BaseUtil.getLocalMessage("ENTERING"));
				String sql = SqlUtil.getInsertSqlByMap(s, "MakeNoticeDetail", new String[]{"mnd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "mn_id", store.get("mn_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller,new Object[]{store,gstore});
	}
	
	@Override
	public void auditMakeNotice(int mn_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("MakeNotice", "mn_statuscode", "mn_id=" + mn_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{mn_id});		//执行审核操作
		baseDao.audit("MakeNotice","mn_id=" + mn_id, "mn_status", "mn_statuscode", "mn_auditdate", "mn_auditman");	
		baseDao.audit("MakeNoticeDetail", "mnd_mnid=" + mn_id, "mnd_status", "mnd_statuscode");
		//记录操作
		baseDao.logger.audit(caller, "mn_id", mn_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{mn_id});
	}
	
	@Override
	public void resAuditMakeNotice(int mn_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("MakeNotice", "mn_statuscode", "mn_id=" + mn_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.updateByCondition("MakeNotice", "mn_statuscode='ENTERING',mn_status='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "mn_id=" + mn_id);
		baseDao.updateByCondition("MakeNoticeDetail", "mnd_statuscode='ENTERING',mnd_status='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "mnd_mnid=" + mn_id);
		//记录操作
		baseDao.logger.resAudit(caller, "mn_id", mn_id);
	}
	
	@Override
	public void submitMakeNotice(int mn_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MakeNotice", "mn_statuscode", "mn_id=" + mn_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{mn_id});
		//执行提交操作
		baseDao.updateByCondition("MakeNotice", "mn_statuscode='COMMITED',mn_status='" + 
				BaseUtil.getLocalMessage("COMMITED") + "'", "mn_id=" + mn_id);
		baseDao.updateByCondition("MakeNoticeDetail", "mnd_statuscode='COMMITED',mnd_status='" + 
				BaseUtil.getLocalMessage("COMMITED") + "'", "mnd_mnid=" + mn_id);
		//记录操作
		baseDao.logger.submit(caller, "mn_id", mn_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{mn_id});
	}
	
	@Override
	public void resSubmitMakeNotice(int mn_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MakeNotice", "mn_statuscode", "mn_id=" + mn_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[]{mn_id});
		//执行反提交操作
		baseDao.updateByCondition("MakeNotice", "mn_statuscode='ENTERING',mn_status='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "mn_id=" + mn_id);
		baseDao.updateByCondition("MakeNoticeDetail", "mnd_statuscode='ENTERING',mnd_status='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "mnd_mnid=" + mn_id);
		//记录操作
		baseDao.logger.resSubmit(caller, "mn_id", mn_id);
		handlerService.afterResSubmit(caller, new Object[]{mn_id});
	 }
	
	@Override
	public int turnMake(int mn_id, String caller) {
		int maid=0;
		//判断该新物料申请单是否已经转入过临时物料
		Object code = baseDao.getFieldDataByCondition("MakeNotice", "mn_code", "mn_id=" + mn_id);
		code = baseDao.getFieldDataByCondition("Make", "ma_code", "ma_source='" + code + "'");
		if(code != null && !code.equals("")){
			BaseUtil.showError(BaseUtil.getLocalMessage("pm.make.makenotice.haveturn") + code);
		} else {		
			maid = makeNoticeDao.turnMake(mn_id,caller);
			//记录操作
			baseDao.logger.getMessageLog( BaseUtil.getLocalMessage("msg.turnMake"), BaseUtil.getLocalMessage("msg.turnSuccess"), caller, "mn_id", mn_id);
					
		}
		return maid;
	}

	@Override
	public int turnOutSource(int mn_id, String caller) {
		int maid=0;
		//判断该新物料申请单是否已经转入过临时物料
		Object code = baseDao.getFieldDataByCondition("MakeNotice", "mn_code", "mn_id=" + mn_id);
		code = baseDao.getFieldDataByCondition("Make", "ma_code", "ma_source='" + code + "'");
		if(code != null && !code.equals("")){
			BaseUtil.showError(BaseUtil.getLocalMessage("pm.make.makenotice.haveturn") + code);
		} else {
			maid = makeNoticeDao.turnMake(mn_id,caller);
			//记录操作
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.turnOutSource"), 
					BaseUtil.getLocalMessage("msg.turnSuccess"), "MakeNotice|mn_id=" + mn_id));
		}
		return maid;
	}
}
