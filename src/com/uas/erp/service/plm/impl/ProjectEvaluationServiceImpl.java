package com.uas.erp.service.plm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.dao.common.FormDao;
import com.uas.erp.model.Key;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.common.SingleFormItemsService;
import com.uas.erp.service.plm.ProjectEvaluationService;

@Service("projectEvaluationService")
public class ProjectEvaluationServiceImpl implements ProjectEvaluationService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private FormDao formDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private SingleFormItemsService singleFormItemsService;
	@Autowired
	private TransferRepository transferRepository;
	
	@Override
	public void saveProjectEvaluation(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		if(caller.equals("ProjectScheme")){
			int count=baseDao.getCount("select count(1) from ProjectEvaluation where pe_tempcode='"+store.get("pe_tempcode")+"'");		
			if(count!=0){
				BaseUtil.showError("此方案编号已存在！");
			}
		}else{
			int count=baseDao.getCount("select count(1) from ProjectEvaluation where pe_code='"+store.get("pe_code")+"'");		
			if(count!=0){
				BaseUtil.showError("此编号已存在！");
			}
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller,  new Object[] { store});	
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ProjectEvaluation", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "pe_id", store.get("pe_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller,  new Object[] { store});
	}

	@Override
	public void deleteProjectEvaluation(int pe_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { pe_id});
		baseDao.deleteById("ProjectEvaluation", "pe_id", pe_id);
		// 记录操作
		baseDao.logger.delete(caller, "pe_id", pe_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { pe_id});
	}

	@Override
	public void updateProjectEvaluation(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,  new Object[] { store});
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProjectEvaluation", "pe_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "pe_id", store.get("pe_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller,  new Object[] { store});
	}

	@Override
	public void submitProjectEvaluation(int pe_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ProjectEvaluation",
						"pe_statuscode", "pe_id=" + pe_id);
		StateAssert.submitOnlyEntering(status);
		handlerService.beforeSubmit(caller, new Object[] {pe_id});
		// 执行提交操作
		baseDao.submit("ProjectEvaluation", "pe_id=" + pe_id, "pe_status", "pe_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pe_id", pe_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] {pe_id});
	}

	@Override
	public void resSubmitProjectEvaluation(int id, String caller) {
		handlerService.handler(caller, "resCommit", "before", new Object[] { id });
		// 执行反提交操作
		baseDao.resOperate("ProjectEvaluation", "pe_id=" + id, "pe_status", "pe_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pe_id", id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, new Object[] {id});
	}

	@Override
	public void auditProjectEvaluation(int pe_id, String caller) {// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ProjectEvaluation","pe_statuscode", "pe_id=" + pe_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] {pe_id });
		// 执行审核操作
		baseDao.audit("ProjectEvaluation", "pe_id=" + pe_id, "pe_status", "pe_statuscode", "pe_auditdate", "pe_auditer");
		// 记录操作
		baseDao.logger.audit(caller, "pe_id", pe_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] {pe_id });}

	@Override
	public void resAuditProjectEvaluation(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("ProjectEvaluation","pe_statuscode", "pe_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("ProjectEvaluation", "pe_id=" +  id, "pe_status", "pe_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "pe_id", id);
	}
	@Override
	public int turnProject(int pe_id, String caller) {
		Key key = transferRepository.transfer("ProjectEvaluation", pe_id);
		int id = key.getId();
		// 更新原表字段
		baseDao.execute("update ProjectEvaluation set pe_turn=-1 where pe_id=" + pe_id);
		return id;
	}

	@Override
	public void turn(int pe_id, String type,String caller) {
		String str="plm.project.projectEvaluation.turnStandard";
		if("CUSTOM".equals(type)){//转定制
			baseDao.updateByCondition("ProjectEvaluation","pe_type='CUSTOM'", "pe_id="+pe_id);
			str="plm.project.projectEvaluation.turnCustom";
		}else if("STANDARD".equals(type)){
			baseDao.updateByCondition("ProjectEvaluation","pe_type='STANDARD'", "pe_id="+pe_id);
		}
		// 记录操作
		try {
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage(str), BaseUtil
					.getLocalMessage("msg.turnSuccess"), caller + "|" + "pe_id=" + pe_id));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
