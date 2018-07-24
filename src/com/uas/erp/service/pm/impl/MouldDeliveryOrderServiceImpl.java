package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.MouldDeliveryOrderService;

@Service("mouldDeliveryOrder")
public class MouldDeliveryOrderServiceImpl implements MouldDeliveryOrderService{
	@Autowired
	private BaseDao baseDao;	
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveMouldDeliveryOrder(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("MOD_DELIVERYORDER", "md_code='" + store.get("md_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller,new Object[]{store, grid});
		//保存MouldDeliveryOrder
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "MOD_DELIVERYORDER", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//保存MouldDeliveryOrderDetail
		for(Map<Object, Object> s:grid){
			s.put("mdd_id", baseDao.getSeqId("MOD_DELIVERYORDERDETAIL_SEQ"));
			s.put("mdd_code", store.get("md_code"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "MOD_DELIVERYORDERDETAIL");
		baseDao.execute(gridSql);
		getTotal(store.get("md_id"));
		baseDao.logger.save(caller, "md_id", store.get("md_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller,new Object[]{store, grid});
	}
	
	@Override
	public void deleteMouldDeliveryOrder(int md_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("MOD_DELIVERYORDER", "md_auditstatuscode", "md_id=" + md_id);
		StateAssert.delOnlyEntering(status);
		status = baseDao.getFieldDataByCondition("MOD_DELIVERYORDER", "md_statuscode", "md_id=" + md_id);
		if(!status.equals("UNPOST")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller,  new Object[]{md_id});
		baseDao.execute("update mod_sale set msa_turnstatus=null,msa_turnstatuscode=null where msa_id in (select md_sourceid from mod_deliveryorder where md_id="+md_id+")");
		//删除MouldDeliveryOrder
		baseDao.deleteById("MOD_DELIVERYORDER", "md_id", md_id);
		//删除MouldDeliveryOrderDetail
		baseDao.deleteById("MOD_DELIVERYORDERdetail", "mdd_mdid", md_id);
		//记录操作
		baseDao.logger.delete(caller, "md_id", md_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller,  new Object[]{md_id});
	}
	
	@Override
	public void updateMouldDeliveryOrderById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("MOD_DELIVERYORDER", "md_auditstatuscode", "md_id=" + store.get("md_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store, gstore});
		//修改MouldDeliveryOrder
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "MOD_DELIVERYORDER", "md_id");
		baseDao.execute(formSql);
		//修改MouldDeliveryOrderDetail		
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "MOD_DELIVERYORDERDETAIL", "mdd_id");
		for(Map<Object, Object> s:gstore){
			if (s.get("mdd_id") == null || s.get("mdd_id").equals("") || s.get("mdd_id").equals("0")
					|| Integer.parseInt(s.get("mdd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("MOD_DELIVERYORDERDETAIL_SEQ");
				s.put("mdd_code", store.get("md_code"));
				String sql = SqlUtil.getInsertSqlByMap(s, "MOD_DELIVERYORDERDETAIL", new String[] { "mdd_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		getTotal(store.get("md_id"));
		//记录操作
		baseDao.logger.update(caller, "md_id", store.get("md_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store, gstore});
	}
	
	@Override
	public void printMouldDeliveryOrder(int md_id, String caller) {
		//执行打印前的其它逻辑
		handlerService.beforePrint(caller,  new Object[]{md_id});
		//执行打印操作
		baseDao.updateByCondition("MOD_DELIVERYORDER", "md_printstatuscode='PRINTED',md_printstatus='" + 
				BaseUtil.getLocalMessage("PRINTED") + "'", "md_id=" + md_id);
		//记录操作
		baseDao.logger.print(caller, "md_id", md_id);
		//执行打印后的其它逻辑
		handlerService.afterPrint(caller,  new Object[]{md_id});
	}
	
	@Override
	public void auditMouldDeliveryOrder(int md_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("MOD_DELIVERYORDER", "md_auditstatuscode", "md_id=" + md_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller,new Object[]{md_id});
		//执行审核操作
		baseDao.audit("MOD_DELIVERYORDER", "md_id=" + md_id, "md_auditstatus", "md_auditstatuscode", "md_auditdate", "md_auditman");
		//记录操作
		baseDao.logger.audit(caller, "md_id", md_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller,new Object[]{md_id});
	}
	
	@Override
	public void resAuditMouldDeliveryOrder(int md_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("MOD_DELIVERYORDER", "md_auditstatuscode", "md_id=" + md_id);
		StateAssert.resAuditOnlyAudit(status);
		status = baseDao.getFieldDataByCondition("MOD_DELIVERYORDER", "md_statuscode", "md_id=" + md_id);
		if(!status.equals("UNPOST")){
			BaseUtil.showError("只能反审核[未过账]的单据!");
		}
		//执行反审核操作
		baseDao.resOperate("MOD_DELIVERYORDER", "md_id=" + md_id, "md_auditstatus", "md_auditstatuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "md_id", md_id);
	}
	
	@Override
	public void submitMouldDeliveryOrder(int md_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MOD_DELIVERYORDER", "md_auditstatuscode", "md_id=" + md_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{md_id});
		getTotal(md_id);
		//执行提交操作
		baseDao.submit("MOD_DELIVERYORDER", "md_id=" + md_id, "md_auditstatus", "md_auditstatuscode");
		//记录操作
		baseDao.logger.submit(caller, "md_id", md_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{md_id});
	}
	
	@Override
	public void resSubmitMouldDeliveryOrder(int md_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MOD_DELIVERYORDER", "md_auditstatuscode", "md_id=" + md_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[]{md_id});
		//执行反提交操作
		baseDao.resOperate("MOD_DELIVERYORDER", "md_id=" + md_id, "md_auditstatus", "md_auditstatuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "md_id", md_id);
		handlerService.afterResSubmit(caller, new Object[]{md_id});
	}

	@Override
	public void postMouldDeliveryOrder(int md_id, String caller) {
		// 只能对状态为[未过账]的单据进行过账操作!
		Object status = baseDao.getFieldDataByCondition("MOD_DELIVERYORDER", "md_statuscode", "md_id=" + md_id);
		if (status.equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.post_onlyUnPost"));
		}
		// 过账前的其它逻辑
		handlerService.beforePost(caller,new Object[] { md_id });
		getTotal(md_id);
		baseDao.updateByCondition("MOD_DELIVERYORDER", "md_statuscode='POSTED',md_status='" + 
				BaseUtil.getLocalMessage("POSTED") + "',md_postman='"+SystemSession.getUser().getEm_name()+
				"', md_postdate=sysdate", "md_id=" + md_id);
		// 记录操作
		baseDao.logger.post(caller, "md_id", md_id);
		// 执行过账后的其它逻辑
		handlerService.afterPost(caller,new Object[] { md_id });
	}

	@Override
	public void resPostMouldDeliveryOrder(int md_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("MOD_DELIVERYORDER", "md_statuscode", "md_id=" + md_id);
		if (!status.equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resPost_onlyPost"));
		}
		// 反过账前的其它逻辑
		handlerService.beforeResPost(caller, new Object[] { md_id });
		baseDao.updateByCondition("MOD_DELIVERYORDER", "md_statuscode='UNPOST',md_status='" + 
				BaseUtil.getLocalMessage("UNPOST") + "'", "md_id=" + md_id);
		// 记录操作
		baseDao.logger.resPost(caller, "md_id", md_id);
		// 执行反过账后的其它逻辑
		handlerService.afterResPost(caller, new Object[] { md_id });
	}
	/**
	 * 计算total
	 */
	private void getTotal(Object md_id) {
		baseDao.updateByCondition(
				"MOD_DELIVERYORDERDETAIL",
					"mdd_amount=round(nvl(mdd_price,0)*nvl(mdd_qty,0),2)", "mdd_mdid=" + md_id);
		baseDao.updateByCondition("MOD_DELIVERYORDER",
				"md_amount=(SELECT round(sum(nvl(mdd_price,0)*nvl(mdd_qty,0)),2) FROM MOD_DELIVERYORDERDETAIL WHERE mdd_mdid="
						+ md_id + ")", "md_id=" + md_id);
	}
}

