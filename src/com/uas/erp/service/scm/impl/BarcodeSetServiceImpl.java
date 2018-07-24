package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.BarcodeSetService;

@Service("barcodeSetServiceImpl")
public class BarcodeSetServiceImpl implements BarcodeSetService {
	@Autowired  
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveSerail(String formStore, String gridStore, String caller) {
		// TODO Auto-generated method stub
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		String code = store.get("bs_code").toString();
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BarcodeSet", "bs_code='" + code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] {store, grid});	
        for(Map<Object, Object> s:grid){
        	//物料ID/供应商ID/客户ID/流水号/固定值/日期
        	if(s.get("bsd_type").toString().equals("") || s.get("bsd_type").toString() == null){
        		BaseUtil.showError("类型不允许为空！");
        	}else if(s.get("bsd_type").toString().equals("prid") || s.get("bsd_type").toString().equals("vendid")
        		||s.get("bsd_type").toString().equals("custid")||s.get("bsd_type").toString().equals("selfcode")){
        		if(s.get("bsd_value").toString().equals("")){
        			BaseUtil.showError("当类型为物料ID/供应商ID/客户ID时，值不允许为空！");
        		}if(!s.get("bsd_value").toString().matches("[1-9]")){
        			BaseUtil.showError("当类型为物料ID/供应商ID/客户ID时，值必须是1-9中的任意值！");
        		}
        	}else if(s.get("bsd_type").toString().equals("fixedvalue")){
        		if(s.get("bsd_value").toString().equals("") || s.get("bsd_value").toString() == null){
        			BaseUtil.showError("当类型为固定值时，值不允许为空！");
        		}
        	}        	
        	s.put("bsd_id",baseDao.getSeqId("BARCODESETDETAIL_SEQ"));
        }
		// 保存BarcodeSet
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BarcodeSet", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存BarcodeSetDetail		
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "BarcodeSetDetail", "bsd_id");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "bs_id", store.get("bs_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] {store, grid});			
	}

	@Override
	public void updateSerail(String formStore, String param, String caller) {
		// TODO Auto-generated method stub
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		//只能修改[在录入]的单据资料!
		Object status = baseDao.getFieldDataByCondition("barcodeSet", "bs_statuscode", "bs_id=" + store.get("bs_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store, gstore});
		String code = store.get("bs_code").toString();
		// 当前编号的记录已经存在,不能修改!
		boolean bool = baseDao.checkByCondition("BarcodeSet", "bs_code='" + code + "' and bs_id<>"+store.get("bs_id"));
		if(!bool){
			BaseUtil.showError("规则编号重复！");
		}
		for(Map<Object, Object> s:gstore){
        	//物料ID/供应商ID/客户ID/流水号/固定值/日期
        	if(s.get("bsd_type").toString().equals("") || s.get("bsd_type").toString() == null){
        		BaseUtil.showError("类型不允许为空！");
        	}else if(s.get("bsd_type").toString().equals("prid") || s.get("bsd_type").toString().equals("vendid")
        		||s.get("bsd_type").toString().equals("custid")||s.get("bsd_type").toString().equals("selfcode")){
        		if(s.get("bsd_value").toString().equals("")){
        			BaseUtil.showError("当类型为物料ID/供应商ID/客户ID时，值不允许为空！");
        		}if(!s.get("bsd_value").toString().matches("[1-9]")){
        			BaseUtil.showError("当类型为物料ID/供应商ID/客户ID时，值必须是1-9中的任意值！");
        		}
        	}else if(s.get("bsd_type").toString().equals("fixedvalue")){
        		if(s.get("bsd_value").toString().equals("") || s.get("bsd_value").toString() == null){
        			BaseUtil.showError("当类型为固定值时，值不能为空");
        		}
        	}        	
        }
		//修改BarcodeSet
		store.put("bs_recorder", SystemSession.getUser().getEm_name());
		store.put("bs_date", DateUtil.currentDateString(null));
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BarcodeSet", "bs_id");
		baseDao.execute(formSql);
		//修改BarcodeSetDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "BarcodeSetDetail", "bsd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("bsd_id") == null || s.get("bsd_id").equals("") || s.get("bsd_id").equals("0") ||
					Integer.parseInt(s.get("bsd_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("BARCODESETDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "BarcodeSetDetail", new String[]{"bsd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "ma_id", store.get("ma_id"));
		//执行保存后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store, gstore});	
	}

	@Override
	public void deleteSerail(String caller, int bs_id) {
		// TODO Auto-generated method stub
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("BarcodeSet", "bs_statuscode", "bs_id=" + bs_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, bs_id);
		//删除BarcodeSet
		baseDao.deleteById("BarcodeSet", "bs_id", bs_id);
		//删除BarcodeSetdetail
		baseDao.deleteById("BarcodeSetDetail", "bsd_bsid", bs_id);
		//记录操作
		baseDao.logger.delete(caller, "bs_id", bs_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, bs_id);	
	}

	@Override
	public void auditSerail(String caller, int bs_id) {
		// TODO Auto-generated method stub
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("BarcodeSet", "bs_statuscode", "bs_id=" + bs_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, bs_id);
		//执行审核操作
		baseDao.audit("BarcodeSet", "bs_id=" + bs_id, "bs_status", "bs_statuscode");
		//记录操作
		baseDao.logger.audit(caller, "bs_id", bs_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, bs_id);	
	}

	@Override
	public void bannedSerial(String caller, int bs_id) {
		// TODO Auto-generated method stub
		//执行禁用操作
		baseDao.banned("BarcodeSet", "bs_id=" + bs_id, "bs_status", "bs_statuscode");
		//记录操作
		baseDao.logger.banned(caller, "bs_id", bs_id);
	
	}

	@Override
	public void resBannedSerail(String caller, int bs_id) {
		// TODO Auto-generated method stub
		//执行反禁用操作
		baseDao.resOperate("BarcodeSet", "bs_id=" + bs_id, "bs_status", "bs_statuscode");
		//记录操作
		baseDao.logger.resBanned(caller, "bs_id", bs_id);
	
	}

	@Override
	public void resAuditSerail(String caller, int bs_id) {
		// TODO Auto-generated method stub
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("BarcodeSet", "bs_statuscode", "bs_id=" + bs_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("BarcodeSet", "bs_id=" + bs_id, "bs_status", "bs_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "bs_id", bs_id);	
	}

	@Override
	public void submitSerail(String caller, int bs_id) {
		// TODO Auto-generated method stub
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("BarcodeSet", "bs_statuscode", "bs_id=" + bs_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, bs_id);
		//执行提交操作
		baseDao.submit("BarcodeSet", "bs_id=" + bs_id, "bs_status", "bs_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "bs_id", bs_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, bs_id);
	
	}

	@Override
	public void resSubmitSerail(String caller, int bs_id) {
		// TODO Auto-generated method stub
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("BarcodeSet", "bs_statuscode", "bs_id=" + bs_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, bs_id);
		//执行反提交操作
		baseDao.resOperate("BarcodeSet", "bs_id=" + bs_id, "bs_status", "bs_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "bs_id", bs_id);
		handlerService.afterResSubmit(caller, bs_id);		
	}

}
