package com.uas.erp.service.oa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.FeeClaimService;

@Service("feeClaimService")
public class FeeClaimServiceImpl implements FeeClaimService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveFeeClaim(String formStore, String gridStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("FeeClaim", "fc_code='" + store.get("fc_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store,gstore});
		//保存FeeClaim
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "FeeClaim", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		////保存FeeClaimDetail
		Object[] fcd_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			fcd_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				fcd_id[i] = baseDao.getSeqId("FEECLAIMDETAIL_SEQ");
			}
		} else {
			fcd_id[0] = baseDao.getSeqId("FEECLAIMDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "FeeClaimDetail", "fcd_id", fcd_id);
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "fc_id", store.get("fc_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store,gstore});
	}
	@Override
	public void deleteFeeClaim(int fc_id, String  caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("FeeClaim", "fc_statuscode", "fc_id=" + fc_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.afterDel(caller, new Object[]{fc_id});
		//删除FeeClaim
		baseDao.deleteById("FeeClaim", "fc_id", fc_id);
		//删除FeeClaimDetail
		baseDao.deleteById("FeeClaimdetail", "fcd_fcid", fc_id);
		//记录操作
		baseDao.logger.delete(caller, "fc_id", fc_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{fc_id});
	}
	
	@Override
	public void updateFeeClaimById(String formStore, String gridStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("FeeClaim", "fc_statuscode", "fc_id=" + store.get("fc_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,new Object[]{store,gstore});
		//修改FeeClaim
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "FeeClaim", "fc_id");
		baseDao.execute(formSql);
		//修改FeeClaimDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "FeeClaimDetail", "fcd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("fcd_id") == null || s.get("fcd_id").equals("") || s.get("fcd_id").equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("FEECLAIMDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "FeeClaimDetail", new String[]{"fcd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "fc_id", store.get("fc_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[]{store,gstore});
	}
	@Override
	public void auditFeeClaim(int fc_id, String  caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("FeeClaim", "fc_statuscode", "fc_id=" + fc_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{fc_id});
		//执行审核操作
		baseDao.audit("FeeClaim", "fc_id=" + fc_id, "fc_status", "fc_statuscode", "fc_auditdate", "fc_auditman");
		//记录操作
		baseDao.logger.audit(caller, "fc_id", fc_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{fc_id});
	}
	@Override
	public void resAuditFeeClaim(int fc_id, String  caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("FeeClaim", "fc_statuscode", "fc_id=" + fc_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("FeeClaim", "fc_id=" + fc_id, "fc_status", "fc_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "fc_id", fc_id);
	}
	@Override
	public void submitFeeClaim(int fc_id, String  caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("FeeClaim", "fc_statuscode", "fc_id=" + fc_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{fc_id});
		//执行提交操作
		baseDao.submit("FeeClaim", "fc_id=" + fc_id, "fc_status", "fc_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "fc_id", fc_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{fc_id});
	}
	@Override
	public void resSubmitFeeClaim(int fc_id, String  caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("FeeClaim", "fc_statuscode", "fc_id=" + fc_id);
		StateAssert.resSubmitOnlyCommited(status);
		//执行反提交操作
		baseDao.resOperate("FeeClaim", "fc_id=" + fc_id, "fc_status", "fc_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "fc_id", fc_id);
	}
}
