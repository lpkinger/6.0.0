package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.ECRDOCService;


@Service("ECRDOCService")
public class ECRDOCServiceImpl implements ECRDOCService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveECRDOC(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ECR", "ecr_code='" + store.get("ecr_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave("ECR!DOC",new Object[]{store, gstore});		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ECR", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		////保存ECRDetail
		Object[] ecd_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			ecd_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				ecd_id[i] = baseDao.getSeqId("ECRDETAIL_SEQ");
			}
		} else {
			ecd_id[0] = baseDao.getSeqId("ECRDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "ECRDOC", "ecd_id", ecd_id);
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "ecr_id", store.get("ecr_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave("ECR!DOC",new Object[]{store, gstore});
	}
	@Override
	public void deleteECRDOC(int ecr_id, String caller) {
		//只能删除在录入的ECR
		Object status = baseDao.getFieldDataByCondition("ECR", "ecr_checkstatuscode", "ecr_id=" + ecr_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel("ECR!DOC", new Object[]{ecr_id});		//删除ECR
		baseDao.deleteById("ECR", "ecr_id", ecr_id);
		//删除purchaseDetail
		baseDao.deleteById("ECRDoc", "ecd_ecrid", ecr_id);
		//记录操作
		baseDao.logger.delete(caller, "ecr_id", ecr_id);
		//执行删除后的其它逻辑
		handlerService.afterDel("ECR!DOC", new Object[]{ecr_id});
	}

	@Override
	public void updateECRDOCById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的ECR!
		Object status = baseDao.getFieldDataByCondition("ECR", "ecr_checkstatuscode", "ecr_id=" + store.get("ecr_id"));
		StateAssert.updateOnlyEntering(status);
		//更新ECR计划下达数\本次下达数\状态
		//purchaseDao.updatePurchasePlan(Integer.parseInt((String)store.get("pu_id")));
		//执行修改前的其它逻辑
		handlerService.beforeSave("ECR!DOC",  new Object[]{store, gstore});		//修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ECR", "ecr_id");
		baseDao.execute(formSql);
		//修改ECRDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ECRDOC", "ecd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("ecd_id") == null || s.get("ecd_id").equals("") || s.get("ecd_id").equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("ECRDOC_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "ECRDOC", new String[]{"ecd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "ecr_id", store.get("ecr_id"));
		//更新上次采购价格、供应商
		//purchaseDao.updatePrePurchase((String)store.get("pu_code"), (String)store.get("pu_date"));
		//执行修改后的其它逻辑
		handlerService.afterSave("ECR!DOC",  new Object[]{store, gstore});
	}
	
	@Override
	public void auditECRDOC(int ecr_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ECR", "ecr_checkstatuscode", "ecr_id=" + ecr_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit("ECR!DOC",new Object[]{ecr_id});		//执行审核操作
		baseDao.audit("ECR", "ecr_id=" + ecr_id, "ecr_checkstatus", "ecr_checkstatuscode", "ecr_auditdate", "ecr_auditman");
		//记录操作
		baseDao.logger.audit(caller, "ecr_id", ecr_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit("ECR!DOC",new Object[]{ecr_id});
	}
	
	@Override
	public void resAuditECRDOC(int ecr_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ECR", "ECR_checkstatuscode", "ecr_id=" + ecr_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.updateByCondition("ECR", "ecr_checkstatuscode='ENTERING',ecr_checkstatus='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "ecr_id=" + ecr_id);
		//记录操作
		baseDao.logger.resSubmit(caller, "ecr_id", ecr_id);
	}
	
	@Override
	public void submitECRDOC(int ecr_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ECR", "ecr_checkstatuscode", "ecr_id=" + ecr_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit("ECR!DOC",new Object[]{ecr_id});		//执行提交操作
		baseDao.submit("ECR", "ecr_id=" + ecr_id, "ecr_checkstatus", "ecr_checkstatuscode");
		//记录操作
		baseDao.logger.submit(caller, "ecr_id", ecr_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit("ECR!DOC",new Object[]{ecr_id});
	}
	
	@Override
	public void resSubmitECRDOC(int ecr_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ECR", "ecr_checkstatuscode", "ecr_id=" + ecr_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit("ECR!DOC", new Object[]{ecr_id});		//执行反提交操作
		baseDao.updateByCondition("ECR", "ecr_checkstatuscode='ENTERING',ecr_checkstatus='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "ecr_id=" + ecr_id);
		//记录操作
		baseDao.logger.resSubmit(caller, "ecr_id", ecr_id);
		handlerService.afterResSubmit("ECR!DOC", new Object[]{ecr_id});
	}
}
