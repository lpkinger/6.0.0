package com.uas.erp.service.crm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;


import com.uas.erp.service.crm.ProjBudgetChangeService;
@Service
public class ProjBudgetChangeServiceImpl implements ProjBudgetChangeService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveProjBudgetChange(String formStore, 
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ProjBudgetChange", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save(caller, "pbc_id", store.get("pbc_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void deleteProjBudgetChange(int pbc_id, 
			String caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, pbc_id);
		//删除purchase
		baseDao.deleteById("ProjBudgetChange", "pbc_id", pbc_id);
		//记录操作
		baseDao.logger.delete(caller, "pbc_id", pbc_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, pbc_id);
	}

	@Override
	public void updateProjBudgetChange(String formStore, 
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		//修改ProjBudgetChange
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProjBudgetChange", "pbc_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "pbc_id", store.get("pbc_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
	}

	@Override
	public void auditProjBudgetChange(int pbc_id,  String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ProjBudgetChange", "pbc_statuscode", "pbc_id=" + pbc_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, pbc_id);
		//执行审核操作
		baseDao.updateByCondition("ProjBudgetChange", "pbc_statuscode='AUDITED',pbc_status='" + 
				BaseUtil.getLocalMessage("AUDITED") + "',pbc_auditer='"+SystemSession.getUser().getEm_name()+"',pbc_auditdate=sysdate", "pbc_id=" + pbc_id);
		change(pbc_id);
		
		//记录操作
		baseDao.logger.audit(caller, "pbc_id", pbc_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, pbc_id);
	}

	@Override
	public void resAuditProjBudgetChange(int pbc_id, 
			String caller) {
		Object status = baseDao.getFieldDataByCondition("ProjBudgetChange", "pbc_statuscode", "pbc_id=" + pbc_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, pbc_id);
		//执行反审核操作
		baseDao.updateByCondition("ProjBudgetChange", "pbc_statuscode='ENTERING',pbc_status='" + 
				BaseUtil.getLocalMessage("ENTERING") + "',pbc_auditer='',pbc_auditdate=null", "pbc_id=" + pbc_id);
		resChange(pbc_id);
		//记录操作
		baseDao.logger.resAudit(caller, "pbc_id", pbc_id);
		handlerService.afterResAudit(caller, pbc_id);
	}

	@Override
	public void submitProjBudgetChange(int pbc_id, 
			String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ProjBudgetChange", "pbc_statuscode", "pbc_id=" + pbc_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, pbc_id);
		//执行提交操作
		baseDao.updateByCondition("ProjBudgetChange", "pbc_statuscode='COMMITED',pbc_status='" + 
				BaseUtil.getLocalMessage("COMMITED") + "'", "pbc_id=" + pbc_id);
		//记录操作
		baseDao.logger.submit(caller, "pbc_id", pbc_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, pbc_id);
	}

	@Override
	public void resSubmitProjBudgetChange(int pbc_id, 
			String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ProjBudgetChange", "pbc_statuscode", "pbc_id=" + pbc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, pbc_id);
		//执行反提交操作
		baseDao.updateByCondition("ProjBudgetChange", "pbc_statuscode='ENTERING',pbc_status='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "pbc_id=" + pbc_id);
		//记录操作
		baseDao.logger.resSubmit(caller, "pbc_id", pbc_id);
		handlerService.afterResSubmit(caller, pbc_id);
	}
	public void change(int pbc_id){
		String querysql="SELECT pbc_newadcost,pbc_newmeetcost,pbc_newprcost,pbc_newemployeecost,pbc_newfixcost,pbc_newtotalcost,pbc_pbcode" +
				" FROM ProjBudgetChange WHERE pbc_id=?";
		String updatesql="UPDATE PROJBUDGETI SET pb_adcost = ? ,pb_meetcost = ? ,pb_prcost = ? ,pb_employeecost = ? ,pb_fixcost = ? ,pb_totalcost = ? WHERE " +
				"pb_code=?";
		SqlRowList rs=baseDao.queryForRowSet(querysql, new Object[]{pbc_id});
		while(rs.next()){
			baseDao.execute(updatesql, new Object[]{rs.getObject(1),rs.getObject(2),rs.getObject(3),rs.getObject(4),rs.getObject(5),
					rs.getObject(6),rs.getObject(7)});
		}
	}
	public void resChange(int pbc_id){
		String querysql="SELECT pbc_adcost,pbc_meetcost,pbc_prcost,pbc_employeecost,pbc_fixcost,pbc_totalcost,pbc_pbcode" +
				" FROM ProjBudgetChange WHERE pbc_id=?";
		String updatesql="UPDATE PROJBUDGETI SET pb_adcost = ? ,pb_meetcost = ? ,pb_prcost = ? ,pb_employeecost = ? ,pb_fixcost = ? ,pb_totalcost = ? WHERE " +
				"pb_code=?";
		SqlRowList rs=baseDao.queryForRowSet(querysql, new Object[]{pbc_id});
		while(rs.next()){
			baseDao.execute(updatesql, new Object[]{rs.getObject(1),rs.getObject(2),rs.getObject(3),rs.getObject(4),rs.getObject(5),
					rs.getObject(6),rs.getObject(7)});
		}
	}
}
