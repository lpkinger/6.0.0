package com.uas.erp.service.hr.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.RecruitplanService;

@Service
public class RecruitplanServiceImpl implements RecruitplanService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveRecruitplan(String formStore, String gridStore,
			String  caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[]{store,gstore});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Recruitplan", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
	////保存RecruitplanDetail
		Object[] rd_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			rd_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				rd_id[i] = baseDao.getSeqId("RecruitplanDETAIL_SEQ");
			}
		} else {
			rd_id[0] = baseDao.getSeqId("RecruitplanDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "RecruitplanDetail", "rd_id", rd_id);
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "rp_id", store.get("rp_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store,gstore});

	}

	@Override
	public void updateRecruitplanById(String formStore, String gridStore,
			String  caller) {		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[]{store,gstore});
		//修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Recruitplan", "rp_id");
		baseDao.execute(formSql);
		//修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "RecruitplanDetail", "rd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("rd_id") == null || s.get("rd_id").equals("") || s.get("rd_id").toString().equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("RecruitplanDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "RecruitplanDetail", new String[]{"rd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "rp_id", store.get("rp_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store,gstore});
	}

	@Override
	public void deleteRecruitplan(int rp_id, String  caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{rp_id});
		//如果是转过来的招聘计划，恢复原用人申请的re_isturn
		Object code = baseDao.getFieldDataByCondition("recruitment left join recruitplan on re_code=rp_sourcecode","re_code","rp_id="+rp_id);
		if(code!=null){
			baseDao.execute("update recruitment set re_isturn=0 where re_code='"+code+"'");
		}
		//删除purchase
		baseDao.deleteById("Recruitplan", "rp_id", rp_id);
		//删除purchaseDetail
		baseDao.deleteById("Recruitplandetail", "rd_rpid", rp_id);
		//记录操作
		baseDao.logger.delete(caller, "rp_id", rp_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{rp_id});
	}
	
	@Override
	public void auditRecruitplan(int rp_id, String  caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Recruitplan", "rp_statuscode", "rp_id=" + rp_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, rp_id);
		//执行审核操作
		baseDao.audit("Recruitplan", "rp_id=" + rp_id, "rp_status", "rp_statuscode", "rp_auditdate", "rp_auditor");
		//记录操作
		baseDao.logger.audit(caller, "rp_id", rp_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, rp_id);
	}

	@Override
	public void resAuditRecruitplan(int rp_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Recruitplan", "rp_statuscode", "rp_id=" + rp_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resAudit("Recruitplan", "rp_id=" + rp_id, "rp_status", "rp_statuscode", "rp_auditdate", "rp_auditor");
		//记录操作
		baseDao.logger.resAudit(caller, "rp_id", rp_id);
	}

	@Override
	public void submitRecruitplan(int rp_id, String  caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Recruitplan", "rp_statuscode", "rp_id=" + rp_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, rp_id);
		//执行提交操作
		baseDao.submit("Recruitplan", "rp_id=" + rp_id, "rp_status", "rp_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "rp_id", rp_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, rp_id);
	}

	@Override
	public void resSubmitRecruitplan(int rp_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Recruitplan", "rp_statuscode", "rp_id=" + rp_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, rp_id);
		//执行反提交操作
		baseDao.resOperate("Recruitplan", "rp_id=" + rp_id, "rp_status", "rp_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "rp_id", rp_id);
		handlerService.afterResSubmit(caller, rp_id);
	}
}
