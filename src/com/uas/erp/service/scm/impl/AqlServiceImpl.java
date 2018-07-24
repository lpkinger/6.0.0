package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.AqlService;

@Service
public class AqlServiceImpl implements AqlService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveAql(String caller,String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("QUA_Aql", "al_code='" + store.get("al_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store, grid});
		//保存QUA_Aql
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "QUA_Aql", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//保存QUA_AqlMaterial
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "QUA_AqlDetail", "ad_id");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "al_id", store.get("al_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store, grid});
	}

	@Override
	public void updateAqlById(String caller,String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的单据资料!
		Object status = baseDao.getFieldDataByCondition("QUA_Aql", "al_statuscode", "al_id=" + store.get("al_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store, gstore});
		//修改QUA_Aql
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "QUA_Aql", "al_id");
		baseDao.execute(formSql);
		//修改QUA_AqlMaterial
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "QUA_AqlDetail", "ad_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("ad_id") == null || s.get("ad_id").equals("") || s.get("ad_id").equals("0") ||
					Integer.parseInt(s.get("ad_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("QUA_AQLDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "QUA_AqlDetail", new String[]{"ad_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "al_id", store.get("al_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store, gstore});
	}

	@Override
	public void deleteAql(String caller,int al_id) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("QUA_Aql", "al_statuscode", "al_id=" + al_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, al_id);
		//删除QUA_Aql
		baseDao.deleteById("QUA_Aql", "al_id", al_id);
		//删除QUA_AqlMaterial
		baseDao.deleteById("QUA_AqlDetail", "ad_alid", al_id);
		//记录操作
		baseDao.logger.delete(caller, "al_id", al_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, al_id);
	}

	@Override
	public void printAql(String caller,int al_id) {
	
		
	}

	@Override
	public void auditAql(String caller, int al_id) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("QUA_AQL", "al_statuscode", "al_id=" + al_id);
		StateAssert.auditOnlyCommited(status);
		handlerService.beforeAudit(caller, al_id);
		// 执行审核操作
		baseDao.audit("QUA_AQL", "al_id="+ al_id, "al_status", "al_statuscode", "AL_AUDITDATE", "AL_AUDITMAN");
		// 记录操作
		baseDao.logger.audit(caller, "al_id", al_id);
		handlerService.afterAudit(caller, al_id);
	}
	
	@Override
	public void resAuditAql(String caller, int al_id) {
		Object status = baseDao.getFieldDataByCondition("QUA_AQL", "al_statuscode", "al_id=" + al_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, al_id);
		// 执行审核操作
		baseDao.resAudit("QUA_AQL", "al_id="+ al_id, "al_status", "al_statuscode", "AL_AUDITDATE", "AL_AUDITMAN");
		// 记录操作
		baseDao.logger.audit(caller, "al_id", al_id);
		handlerService.afterResAudit(caller, al_id);
	}
	
	@Override
	public void submitAql(String caller, int al_id) {
		Object status = baseDao.getFieldDataByCondition("QUA_AQL", "al_statuscode", "al_id=" + al_id);
		StateAssert.submitOnlyEntering(status);
		handlerService.beforeSubmit(caller, al_id);
		// 执行审核操作
		baseDao.submit("QUA_AQL", "al_id="+ al_id, "al_status", "al_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "al_id", al_id);
		handlerService.afterSubmit(caller, al_id);
	}
	
	@Override
	public void resSubmitAql(String caller, int al_id) {
		Object status = baseDao.getFieldDataByCondition("QUA_AQL", "al_statuscode", "al_id=" + al_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, al_id);
		// 执行审核操作
		baseDao.resOperate("QUA_AQL", "al_id="+ al_id, "al_status", "al_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "al_id", al_id);
		handlerService.afterResSubmit(caller, al_id);
	}
}
