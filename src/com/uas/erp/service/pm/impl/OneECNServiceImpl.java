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
import com.uas.erp.service.pm.OneECNService;


@Service("oneECNService")
public class OneECNServiceImpl implements OneECNService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveOneECN(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ECN", "ecn_code='" + store.get("ecn_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ECN", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//保存ECNDetail
		Object[] ed_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			ed_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				ed_id[i] = baseDao.getSeqId("ECNDETAIL_SEQ");
			}
		} else {
			ed_id[0] = baseDao.getSeqId("ECNDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "ECNDetail", "ed_id", ed_id);
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "ecn_id", store.get("ecn_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller,  new Object[]{store,gstore});
	}
	
	@Override
	public void deleteOneECN(int ecn_id, String caller) {
		//只能删除在录入的ECN
		Object status = baseDao.getFieldDataByCondition("ECN", "ecn_checkstatuscode", "ecn_id=" + ecn_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{ecn_id});
		//删除ECN
		baseDao.deleteById("ECN", "ecn_id", ecn_id);
		//删除
		baseDao.deleteById("ECNdetail", "ed_ecnid", ecn_id);
		//记录操作
		baseDao.logger.delete(caller, "ecn_id", ecn_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{ecn_id});
	}

	@Override
	public void updateOneECNById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的ECN!
		Object status = baseDao.getFieldDataByCondition("ECN", "ecn_checkstatuscode", "ecn_id=" + store.get("ecn_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,new Object[]{store,gstore});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ECN", "ecn_id");
		baseDao.execute(formSql);
		//修改ECNDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ECNDetail", "ed_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("ed_id") == null || s.get("ed_id").equals("") || s.get("ed_id").equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("ECNDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "ECNDetail", new String[]{"ed_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "ecn_id", store.get("ecn_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[]{store,gstore});
	}
	
	@Override
	public void auditOneECN(int ecn_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ECN", "ecn_checkstatuscode", "ecn_id=" + ecn_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{ecn_id});
		//执行审核操作
		baseDao.audit("ECN", "ecn_id=" + ecn_id, "ecn_checkstatus", "ecn_checkstatuscode", "ecn_auditdate", "ecn_auditman");
		//记录操作
		baseDao.logger.audit(caller, "ecn_id", ecn_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{ecn_id});
	}
	
	@Override
	public void resAuditOneECN(int ecn_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ECN", "ecn_checkstatuscode", "ecn_id=" + ecn_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("ECN", "ecn_id=" + ecn_id, "ecn_checkstatus", "ecn_checkstatuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "ecn_id", ecn_id);
	}
	
	@Override
	public void submitOneECN(int ecn_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ECN", "ecn_checkstatuscode", "ecn_id=" + ecn_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{ecn_id});
		//执行提交操作
		baseDao.submit("ECN", "ecn_id=" + ecn_id, "ecn_checkstatus", "ecn_checkstatuscode");
		//记录操作
		baseDao.logger.submit(caller, "ecn_id", ecn_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{ecn_id});
	}
	
	@Override
	public void resSubmitOneECN(int ecn_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ECN", "ecn_checkstatuscode", "ecn_id=" + ecn_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[]{ecn_id});
		//执行反提交操作
		baseDao.resOperate("ECN", "ecn_id=" + ecn_id, "ecn_checkstatus", "ecn_checkstatuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "ecn_id", ecn_id);
		handlerService.afterResSubmit(caller, new Object[]{ecn_id});
	}
}
