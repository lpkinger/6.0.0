package com.uas.erp.service.plm.impl;

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
import com.uas.erp.service.plm.MilePostFollowService;

@Service
public class MilePostFollowServiceImpl implements MilePostFollowService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveMilePostFollow(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[]{store});		
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "MilePostFollow", new String[]{}, new Object[]{});
		baseDao.execute(formSql);	
		baseDao.logger.save(caller, "mpf_id", store.get("mpf_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void updateMilePostFollowById(String formStore,String caller) {		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[]{store});
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "MilePostFollow", "mpf_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "mpf_id", store.get("mpf_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void deleteMilePostFollow(int mpf_id, String caller) {		
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{mpf_id});
		//删除主表内容
		baseDao.deleteById("MilePostFollow", "mpf_id", mpf_id);
		baseDao.logger.delete(caller, "mpf_id", mpf_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{mpf_id});
	}

	@Override
	public void auditMilePostFollow(int mpf_id, String caller) {
		//只能对已提交进行审核操作
	//	Object status = baseDao.getFieldDataByCondition("MilePostFollow", "mpf_statuscode", "mpf_id=" + mpf_id);
		Object[] ob=baseDao.getFieldsDataByCondition("MilePostFollow left join project on mpf_prjcode=prj_code", new String[]{"mpf_statuscode","prj_id","mpf_phase"},  "mpf_id=" + mpf_id);
		StateAssert.auditOnlyCommited(ob[0]);
		//执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[]{mpf_id});
		//执行审核操作,待写
		baseDao.audit("MilePostFollow", "mpf_id=" + mpf_id, "mpf_status", "mpf_statuscode", "mpf_auditdate", "mpf_auditor");
		// 更新里程碑实际结束时间，更新里程碑状态为已完成		 
		baseDao.updateByCondition("ProjectPhase", "pp_status='已完成',pp_realenddate=sysdate", "pp_phase='"+ob[2]+"' and pp_prjid="+ob[1]);
		Object detno=baseDao.getFieldDataByCondition("ProjectPhase","min(pp_detno)", "pp_detno>(select pp_detno from ProjectPhase where pp_prjid="+ob[1]+" "
				+ "and pp_phase='"+ob[2]+"')and  pp_prjid="+ob[1]);
		if(detno!=null){
			baseDao.updateByCondition("ProjectPhase","pp_status='进行中',pp_realstartdate=sysdate", "pp_detno="+detno+" and pp_prjid="+ob[1]);
			baseDao.execute("update project set prj_phase =(select pp_phase from ProjectPhase where pp_detno="+detno+" and pp_prjid="+ob[1]+") where  prj_id="+ob[1]);
		}
		//记录操作
		baseDao.logger.audit(caller, "mpf_id", mpf_id);
		//执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[]{mpf_id});
	}
	@Override
	public void submitMilePostFollow(int mpf_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MilePostFollow", "mpf_statuscode", "mpf_id=" + mpf_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { mpf_id });
		// 执行提交操作
		baseDao.submit("MilePostFollow", "mpf_id=" + mpf_id, "mpf_status", "mpf_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "mpf_id", mpf_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { mpf_id });
	}
	@Override
	public void resSubmitMilePostFollow(int mpf_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MilePostFollow", "mpf_statuscode", "mpf_id=" + mpf_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { mpf_id });
		// 执行反提交操作
		baseDao.resOperate("MilePostFollow", "mpf_id=" + mpf_id, "mpf_status", "mpf_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "mpf_id", mpf_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { mpf_id });
	}
	@Override
	public void resAuditMilePostFollow(int mpf_id, String caller) {
		// 只能对状态为[已审核]的表单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("MilePostFollow", "mpf_statuscode", "mpf_id=" + mpf_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("MilePostFollow", "mpf_id=" + mpf_id, "mpf_status", "mpf_statuscode", "mpf_auditdate", "mpf_auditor");
		baseDao.resOperate("MilePostFollow", "mpf_id=" + mpf_id, "mpf_status", "mpf_statuscode");
		
		Object[] ob=baseDao.getFieldsDataByCondition("MilePostFollow left join project on mpf_prjcode=prj_code", new String[]{"mpf_statuscode","prj_id","mpf_phase"},  "mpf_id=" + mpf_id);
		List<String> sqls = new ArrayList<String>();
		//更新项目阶段为进行中
		String phase = "update projectphase set pp_status='进行中',pp_realenddate=null where pp_id=(select pp_id from projectphase left join project on prj_id=pp_prjid where (prj_code,pp_phase)=(select mpf_prjcode,mpf_phase from milepostfollow where mpf_id="+mpf_id+"))";
		sqls.add(phase);
		//更新下一阶段的状态为空
		Object detno=baseDao.getFieldDataByCondition("ProjectPhase","min(pp_detno)", "pp_detno>(select pp_detno from ProjectPhase where pp_prjid="+ob[1]+" "
				+ "and pp_phase='"+ob[2]+"')and  pp_prjid="+ob[1]);
		if(detno!=null){ 
			//检查下一阶段是否已完成，如果已完成，不允许反审核
			boolean bool = baseDao.checkIf("projectphase", "pp_detno>(select pp_detno from ProjectPhase where pp_prjid="+ob[1]+" "
					+ "and pp_phase='"+ob[2]+"')and  pp_prjid="+ob[1] + " and pp_status='已完成'");
			if(bool){
				BaseUtil.showError("当前阶段下一阶段已完成，不允许反审核");
			}
			
			String otherPhase = "update projectphase set pp_status=null,pp_realstartdate=null where pp_detno=" + detno + " and pp_prjid=" + ob[1];

			sqls.add(otherPhase);
		}
		
		baseDao.execute(sqls);
		// 记录操作
		baseDao.logger.resAudit(caller, "mpf_id", mpf_id);
	}
}
