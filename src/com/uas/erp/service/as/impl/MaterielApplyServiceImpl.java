package com.uas.erp.service.as.impl;

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
import com.uas.erp.service.as.MaterielApplyService;

@Service
public class MaterielApplyServiceImpl implements MaterielApplyService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveMaterielApply(String formStore,String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[]{store});		
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "AS_MAKEAPPLY", new String[]{}, new Object[]{});
		baseDao.execute(formSql);	
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		List<String> sqls = new ArrayList<String>();
		for(Map<Object, Object> s:gstore){
			if(s.get("amad_id") == null || s.get("amad_id").equals("") || s.get("amad_id").equals("0")){//新添加的数据，id不存在
				int amad_id = baseDao.getSeqId("AS_MAKEAPPLYDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "AS_MAKEAPPLYDETAIL", new String[]{"amad_id"}, new Object[]{amad_id});
				sqls.add(sql);
			}
		}
		baseDao.execute(sqls);
		baseDao.logger.save(caller, "ama_id", store.get("ama_id"));
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void updateMaterielApplyById(String formStore,String param,String caller) {		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[]{store});
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "AS_MAKEAPPLY", "ama_id");
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(param, "AS_MAKEAPPLYDETAIL", "amad_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("amad_id") == null || s.get("amad_id").equals("") || s.get("amad_id").equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("AS_MAKEAPPLYDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "AS_MAKEAPPLYDETAIL", new String[]{"amad_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "ama_id", store.get("ama_id"));
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void deleteMaterielApply(int ama_id, String caller) {		
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{ama_id});
		//删除主表内容
		baseDao.deleteById("AS_MAKEAPPLY", "ama_id", ama_id);
		baseDao.deleteById("As_makeapplydetail", "amad_amaid", ama_id );
		baseDao.logger.delete(caller, "ama_id", ama_id);
		handlerService.handler(caller, "delete", "after", new Object[]{ama_id});
	}

	@Override
	public void auditMaterielApply(int ama_id, String caller) {
		//只能对已提交进行审核操作
	//	Object status = baseDao.getFieldDataByCondition("MaterielApply", "mpf_statuscode", "ama_id=" + ama_id);
		Object ob=baseDao.getFieldDataByCondition("AS_MAKEAPPLY", "ama_statuscode",  "ama_id=" + ama_id);
		StateAssert.auditOnlyCommited(ob);
		//执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[]{ama_id});
		//执行审核操作,待写
		baseDao.audit("AS_MAKEAPPLY", "ama_id=" + ama_id, "ama_status", "ama_statuscode", "ama_auditdate", "ama_auditor");
		//记录操作
		baseDao.logger.audit(caller, "ama_id", ama_id);
		handlerService.handler(caller, "audit", "after", new Object[]{ama_id});
	}
	@Override
	public void submitMaterielApply(int ama_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("AS_MAKEAPPLY", "ama_statuscode", "ama_id=" + ama_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { ama_id });
		baseDao.updateByCondition("AS_MAKEAPPLYDETAIL", "amad_nqty=amad_applyquantity", "amad_amaid="+ama_id);
		// 执行提交操作
		baseDao.submit("AS_MAKEAPPLY", "ama_id=" + ama_id, "ama_status", "ama_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "ama_id", ama_id);
		handlerService.handler(caller, "commit", "after", new Object[] { ama_id });
	}
	@Override
	public void resSubmitMaterielApply(int ama_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("AS_MAKEAPPLY", "ama_statuscode", "ama_id=" + ama_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { ama_id });
		// 执行反提交操作
		baseDao.resOperate("AS_MAKEAPPLY", "ama_id=" + ama_id, "ama_status", "ama_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "ama_id", ama_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { ama_id });
	}
	@Override
	public void resAuditMaterielApply(int ama_id, String caller) {
		// 只能对状态为[已审核]的表单进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("AS_MAKEAPPLY", new String[]{"ama_statuscode","ama_code"}, "ama_id=" + ama_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		Object outcode = baseDao.getFieldDataByCondition("AS_MAKEOUTDETAIL", "max(amod_amoid)", "amod_amacode='"+status[1]+"'");
		if(outcode!=null){
			Object code = baseDao.getFieldDataByCondition("AS_MAKEOUT", "amo_code", "amo_id='"+outcode+"'");
			BaseUtil.showError("该申请单有关联的出库单，不允许反审核，请先删除关联的出库单，出库单号："+ "<a href=\"javascript:openUrl('jsps/as/port/materielout.jsp?formCondition=amo_idIS" + outcode
							+ "&gridCondition=amod_amoidIS" + outcode + "&whoami=MaterielOut')\">" +code + "</a>");
		}
		// 执行反审核操作
		baseDao.resAudit("AS_MAKEAPPLY", "ama_id=" + ama_id, "ama_status", "ama_statuscode", "ama_auditdate", "ama_auditor");
		baseDao.resOperate("AS_MAKEAPPLY", "ama_id=" + ama_id, "ama_status", "ama_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "ama_id", ama_id);
	}
}
