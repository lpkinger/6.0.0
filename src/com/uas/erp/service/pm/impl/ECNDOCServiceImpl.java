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
import com.uas.erp.service.pm.ECNDOCService;


@Service("ECNDOCService")
public class ECNDOCServiceImpl implements ECNDOCService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveECNDOC(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ECN", "ecn_code='" + store.get("ecn_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.afterSave("ECN!DOC",  new Object[]{store, gstore});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ECN", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		////保存ECNDetail
		Object[] ecd_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			ecd_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				ecd_id[i] = baseDao.getSeqId("ECNDOC_SEQ");
			}
		} else {
			ecd_id[0] = baseDao.getSeqId("ECNDOC_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "ECNDOC", "ecd_id", ecd_id);
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "ecn_id", store.get("ecn_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave("ECN!DOC",  new Object[]{store, gstore});
	}
	
	@Override
	public void deleteECNDOC(int ecn_id, String caller) {
		//只能删除在录入的ECN
		Object status = baseDao.getFieldDataByCondition("ECN", "ecn_checkstatuscode2", "ecn_id=" + ecn_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel("ECN!DOC",new Object[]{ecn_id});		//删除ECN
		baseDao.deleteById("ECN", "ecn_id", ecn_id);
		//删除purchaseDetail
		baseDao.deleteById("ECNDOC", "ecd_ecnid", ecn_id);
		//记录操作
		baseDao.logger.delete(caller, "ecn_id", ecn_id);
		//执行删除后的其它逻辑
		handlerService.afterDel("ECN!DOC",new Object[]{ecn_id});
	}

	@Override
	public void updateECNDOCById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的ECN!
		Object status = baseDao.getFieldDataByCondition("ECN", "ecn_checkstatuscode2", "ecn_id=" + store.get("ecn_id"));
		StateAssert.updateOnlyEntering(status);
		//更新ECN计划下达数\本次下达数\状态
		//purchaseDao.updatePurchasePlan(Integer.parseInt((String)store.get("pu_id")));
		//执行修改前的其它逻辑
		handlerService.beforeUpdate("ECN!DOC",new Object[]{store,gstore});		//修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ECN", "ecn_id");
		baseDao.execute(formSql);
		//修改ECNDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ECNDOC", "ecd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("ecd_id") == null || s.get("ecd_id").equals("") || s.get("ecd_id").equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("ECNDOCL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "ECNDOC", new String[]{"ecd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "ecn_id", store.get("ecn_id"));
		//更新上次采购价格、供应商
		//purchaseDao.updatePrePurchase((String)store.get("pu_code"), (String)store.get("pu_date"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate("ECN!DOC",new Object[]{store,gstore});
	}
	
	@Override
	public void auditECNDOC(int ecn_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ECN", "ecn_checkstatuscode2", "ecn_id=" + ecn_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit("ECN!DOC",new Object[]{ecn_id});		//执行审核操作
		baseDao.audit("ECN", "ecn_id=" + ecn_id, "ecn_checkstatus2", "ecn_checkstatuscode2", "ecn_auditdate","ecn_auditman");
		//记录操作
		baseDao.logger.audit(caller, "ecn_id", ecn_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit("ECN!DOC",new Object[]{ecn_id});
	}
	@Override
	public void resAuditECNDOC(int ecn_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ECN", "ecn_checkstatuscode2", "ecn_id=" + ecn_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.updateByCondition("ECN", "ecn_checkstatuscode2='ENTERING',ecn_checkstatus2='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "ecn_id=" + ecn_id);
		//记录操作
		baseDao.logger.resAudit(caller, "ecn_id", ecn_id);
	}
	@Override
	public void submitECNDOC(int ecn_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ECN", "ecn_checkstatuscode2", "ecn_id=" + ecn_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit("ECN!DOC",new Object[]{ecn_id});		//执行提交操作
		baseDao.submit("ECN", "ecn_id=" + ecn_id, "ecn_checkstatus2", "ecn_checkstatuscode2");
		//记录操作
		baseDao.logger.submit(caller, "ecn_id", ecn_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit("ECN!DOC",new Object[]{ecn_id});
	}
	@Override
	public void resSubmitECNDOC(int ecn_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ECN", "ecn_checkstatuscode2", "ecn_id=" + ecn_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit("ECN!DOC",new Object[]{ecn_id});		//执行反提交操作
		baseDao.updateByCondition("ECN", "ecn_checkstatuscode2='ENTERING',ecn_checkstatus2='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "ecn_id=" + ecn_id);
		//记录操作
		baseDao.logger.resSubmit(caller, "ecn_id", ecn_id);
		handlerService.afterResSubmit("ECN!DOC",new Object[]{ecn_id});
	}

}
