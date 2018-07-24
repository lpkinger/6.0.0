package com.uas.erp.service.pm.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.pm.TurnBomPleaseService;


@Service("turnBomPleaseService")
public class TurnBomPleaseServiceImpl implements TurnBomPleaseService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveTurnBomPlease(String caller,String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("TurnBomPlease", "tp_code='" + store.get("tp_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store,gstore});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "TurnBomPlease", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "tp_id", store.get("tp_id"));
		} catch (Exception e) {
			e.printStackTrace();
		} 
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store,gstore});
	}
	
	@Override
	public void deleteTurnBomPlease(String caller,int tp_id) {
		//只能删除在录入的采购单!
		Object status = baseDao.getFieldDataByCondition("TurnBomPlease", "tp_statuscode", "tp_id=" + tp_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{tp_id});
		//删除
		baseDao.deleteById("TurnBomPlease", "tp_id", tp_id);
		//删除Detail
		//记录操作
		baseDao.logger.delete(caller, "tp_id", tp_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{tp_id});
	}
	
	@Override
	public void updateTurnBomPleaseById(String caller,String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的采购单资料!
		Object status = baseDao.getFieldDataByCondition("TurnBomPlease", "tp_statuscode", "tp_id=" + store.get("tp_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,  new Object[]{store,gstore});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "TurnBomPlease", "tp_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "tp_id", store.get("tp_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller,  new Object[]{store,gstore});
	}
	
	@Override
	public void auditTurnBomPlease(int tp_id,String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("TurnBomPlease", "tp_statuscode", "tp_id=" + tp_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{tp_id});
		baseDao.updateByCondition("TurnBomPlease",
				"tp_statuscode='AUDITED',tp_status='" + BaseUtil.getLocalMessage("AUDITED")
						+ "',tp_auditdate=" + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date())
						+ ",tp_auditman='" + SystemSession.getUser().getEm_name() + "',tp_auditmancode='"+SystemSession.getUser().getEm_code()+"',TP_auditmanid='"+SystemSession.getUser().getEm_id()+"'", "tp_id=" + tp_id);
		
		
//		//执行审核操作
//		baseDao.updateByCondition("TurnBomPlease", "tp_statuscode='AUDITED',tp_status='" + 
//				BaseUtil.getLocalMessage("AUDITED") + "'", "tp_id=" + tp_id);
		
		//记录操作
		baseDao.logger.audit(caller, "tp_id", tp_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{tp_id});
	}
	
	@Override
	public void resAuditTurnBomPlease(String caller,int tp_id) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("TurnBomPlease", "tp_statuscode", "tp_id=" + tp_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("TurnBomPlease", "tp_id=" + tp_id, "tp_status", "tp_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "tp_id", tp_id);
	}
	
	@Override
	public void submitTurnBomPlease(String caller, int tp_id) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("TurnBomPlease", "tp_statuscode", "tp_id=" + tp_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{tp_id});
		//执行提交操作
		baseDao.submit("TurnBomPlease", "tp_id=" + tp_id, "tp_status", "tp_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "tp_id", tp_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{tp_id});
	}
	
	@Override
	public void resSubmitTurnBomPlease(String caller,int tp_id) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("TurnBomPlease", "tp_statuscode", "tp_id=" + tp_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[]{tp_id});
		//执行反提交操作
		baseDao.resOperate("TurnBomPlease", "tp_id=" + tp_id, "tp_status", "tp_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "tp_id", tp_id);
		handlerService.afterResSubmit(caller, new Object[]{tp_id});
	}

	@Override
	public void turnStandard(String caller, int id) {
		Object status = baseDao.getFieldDataByCondition("TurnBomPlease", "tp_statuscode", "tp_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		Employee employee = SystemSession.getUser();
		//
		Object prjcode=baseDao.getFieldDataByCondition("TurnBomPlease", "tp_elementname", "tp_id="+id);
		if(prjcode!=null){
			baseDao.updateByCondition("BOM", "bo_style='标准'", "nvl(bo_relativecode,' ')='" + prjcode + "'");
			baseDao.updateByCondition("BOM", "bo_level='标准'", "nvl(bo_relativecode,' ')='" 
					+ prjcode + "' and bo_level not in (select bl_code from bomlevel where bl_code='外购件BOM' OR bl_ifpurchase<>0)");
		}else BaseUtil.showError("项目编号为空不能转标准BOM!");
		baseDao.updateByCondition("TurnBomPlease", "tp_statuscode='TURNED',tp_status='" + 
				BaseUtil.getLocalMessage("TURNED") + "'", "tp_id=" + id);
		//记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(),"转标准BOM", 
				"转标准BOM成功", caller + "|tp_id=" + id));
	}
	 	 	
}
