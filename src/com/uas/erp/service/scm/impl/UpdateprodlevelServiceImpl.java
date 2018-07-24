	package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.UpdateprodlevelService;

@Service
public class UpdateprodlevelServiceImpl implements UpdateprodlevelService {
	
	static final String selectcode = "select cp_level from Updateprodlevel where cp_id=?";
	
	static final String getProductCode = "select cd_prodcode from Updateprodleveldetail where cd_cpid=?";
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveUpdateprodlevel(String formStore, String gridStore) {	
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		if(store.get("cp_code").toString().trim().equals("")){
			String code =  baseDao.sGetMaxNumber("Updateprodlevel", 2);
			store.put("cp_code", code);
		}
		formStore = BaseUtil.parseMap2Str(store);
		handlerService.handler("Updateprodlevel", "save", "before", new Object[]{store, grid});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Updateprodlevel", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
	    //保存UpdateprodlevelDetail
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "UpdateprodlevelDetail", "cd_id");
		baseDao.execute(gridSql);
		baseDao.logger.save("Updateprodlevel", "cp_id", store.get("cp_id"));
		//执行保存后的其它逻辑
		handlerService.handler("Updateprodlevel", "save", "after", new Object[]{store, grid});	
	}

	@Override
	public void updateUpdateprodlevelById(String formStore, String gridStore) {	
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("Updateprodlevel", "cp_statuscode", "cp_id=" + store.get("cp_id"));
		StateAssert.updateOnlyEntering(status);	
		handlerService.handler("Updateprodlevel", "save", "before", new Object[]{store, gstore});
		//修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Updateprodlevel", "cp_id");
		baseDao.execute(formSql);
		//修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "UpdateprodlevelDetail", "cd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("cd_id") == null || s.get("cd_id").equals("") || s.get("cd_id").toString().equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("UpdateprodlevelDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "UpdateprodlevelDetail", new String[]{"cd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute("update Updateprodleveldetail set cd_orilevel=(select max(pr_level) from product where pr_code=cd_prodcode) where cd_cpid="+store.get("cp_id"));
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update("Updateprodlevel", "cp_id", store.get("cp_id"));
		//执行修改后的其它逻辑
		handlerService.handler("Updateprodlevel", "save", "after", new Object[]{store, gstore});	
	}

	@Override
	public void deleteUpdateprodlevel(int cp_id) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("Updateprodlevel", "cp_statuscode", "cp_id=" + cp_id);
		if(!status.equals("ENTERING")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		//执行删除前的其它逻辑
		handlerService.handler("Updateprodlevel", "delete", "before", new Object[]{cp_id});
		//删除purchase
		baseDao.deleteById("Updateprodlevel", "cp_id", cp_id);
		//删除purchaseDetail
		baseDao.deleteById("Updateprodleveldetail", "cd_cpid", cp_id);
		//记录操作
		baseDao.logger.delete("Updateprodlevel", "cp_id", cp_id);
		//执行删除后的其它逻辑
		handlerService.handler("Updateprodlevel", "delete", "after", new Object[]{cp_id});	
	}

	@Override
	public void auditUpdateprodlevel(int cp_id) {	
		SqlRowList rs; 
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Updateprodlevel", "cp_statuscode", "cp_id=" + cp_id);
		if(!status.equals("COMMITED")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.audit_onlyCommited"));
		}
		Object productLevel=baseDao.getFieldDataByCondition("Updateprodlevel", "cp_level", "cp_id=" + cp_id);
		if (productLevel==null || productLevel.equals("")){
			BaseUtil.showError("[变更物料等级]不能为空");
		}
		Object newgrade=baseDao.getFieldDataByCondition("productlevel", "NVL(pl_grade,0)", "pl_levcode='" + productLevel+"'");
		if (Integer.parseInt(newgrade.toString())>0){
			rs = baseDao.queryForRowSet("select wm_concat(cd_detno)cd_detno,count(1)c from Updateprodleveldetail left join Updateprodlevel on cp_id=cd_cpid left join product on pr_code=cd_prodcode left join productlevel on pl_levcode=pr_level where cd_cpid="+cp_id+" and NVL(pl_grade,0)<"+newgrade);
			if (rs.next()) {
				if (rs.getInt("c")>0){
					BaseUtil.showError("变更后的等级不能高于原来的等级,序号："+rs.getString("cd_detno"));
				} 
			} 
		}
		//执行审核前的其它逻辑
		handlerService.handler("Updateprodlevel", "audit", "before", new Object[]{cp_id});
		//执行审核操作
		baseDao.audit("Updateprodlevel", "cp_id=" + cp_id, "cp_status", "cp_statuscode", "cp_auditdate", "cp_auditman");
		baseDao.execute("update Updateprodleveldetail set cd_orilevel=(select max(pr_level) from product where pr_code=cd_prodcode) where cd_cpid="+cp_id);
		baseDao.execute("update product set pr_level='"+productLevel+"' where pr_code in (select cd_prodcode from Updateprodleveldetail where cd_cpid="+cp_id+")");
		// 记录操作
		baseDao.logger.audit("Updateprodlevel", "cp_id", cp_id);
		//执行审核后的其它逻辑
		handlerService.handler("Updateprodlevel", "audit", "after", new Object[]{cp_id});		
	}

	@Override
	public void resAuditUpdateprodlevel(int cp_id) {	
		Object status = baseDao.getFieldDataByCondition("Updateprodlevel", "cp_statuscode", "cp_id=" + cp_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("Updateprodlevel", "cp_id=" + cp_id, "cp_status", "cp_statuscode");
		baseDao.execute("update product set pr_level=(select max(cd_orilevel) from Updateprodleveldetail where cd_cpid="+cp_id+" and cd_prodcode=pr_code) where pr_code in (select cd_prodcode from Updateprodleveldetail where cd_cpid="+cp_id+")");
		// 记录操作
		baseDao.logger.resAudit("Updateprodlevel", "cp_id", cp_id);
	}

	@Override
	public void submitUpdateprodlevel(int cp_id) {	
		SqlRowList rs; 
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Updateprodlevel", "cp_statuscode", "cp_id=" + cp_id);
		StateAssert.submitOnlyEntering(status);
		Object productLevel=baseDao.getFieldDataByCondition("Updateprodlevel", "cp_level", "cp_id=" + cp_id);
		if (productLevel==null || productLevel.equals("")){
			BaseUtil.showError("[变更物料等级]不能为空");
		}
		Object newgrade=baseDao.getFieldDataByCondition("productlevel", "NVL(pl_grade,0)", "pl_levcode='" + productLevel+"'");
		if (Integer.parseInt(newgrade.toString())>0){
			rs = baseDao.queryForRowSet("select wm_concat(cd_detno)cd_detno,count(1)c from Updateprodleveldetail left join Updateprodlevel on cp_id=cd_cpid left join product on pr_code=cd_prodcode left join productlevel on pl_levcode=pr_level where cd_cpid="+cp_id+" and NVL(pl_grade,0)<"+newgrade);
			if (rs.next()) {
				if (rs.getInt("c")>0){
					BaseUtil.showError("变更后的等级不能高于原来的等级,序号："+rs.getString("cd_detno"));
				} 
			} 
		}
		//执行提交前的其它逻辑
		handlerService.handler("Updateprodlevel", "commit", "before", new Object[]{cp_id});
		//执行提交操作
		baseDao.submit("Updateprodlevel", "cp_id=" + cp_id, "cp_status", "cp_statuscode");
		// 记录操作
		baseDao.logger.submit("Updateprodlevel", "cp_id", cp_id);
		//执行提交后的其它逻辑
		handlerService.handler("Updateprodlevel", "commit", "after", new Object[]{cp_id});		
	}

	@Override
	public void resSubmitUpdateprodlevel(int cp_id) {	
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Updateprodlevel", "cp_statuscode", "cp_id=" + cp_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler("Updateprodlevel", "resCommit", "before", new Object[]{cp_id});	
		baseDao.resOperate("Updateprodlevel", "cp_id=" + cp_id, "cp_status", "cp_statuscode");
		// 记录操作
		baseDao.logger.resSubmit("Updateprodlevel", "cp_id", cp_id);
		handlerService.handler("Updateprodlevel", "resCommit", "after", new Object[]{cp_id});		
	}
}
