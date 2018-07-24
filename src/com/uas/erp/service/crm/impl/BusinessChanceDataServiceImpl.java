package com.uas.erp.service.crm.impl;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.crm.BusinessChanceDataService;

@Service
public class BusinessChanceDataServiceImpl implements BusinessChanceDataService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public Map<String, Object> getAgency(String caller){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		 Employee employee = SystemSession.getUser();
			Integer em_defaultorid=employee.getEm_defaultorid();
		Object agency=baseDao.getFieldDataByCondition("hrorg", "agentname", "or_id="+em_defaultorid +" and exists (select 1 from  job where jo_code='"+employee.getEm_defaulthscode()+"' and nvl(ISAGENT,0)=-1)");
		modelMap.put("agentname", agency);
		return modelMap;
	}
	@Override
	public void saveBusinessChanceData(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BusinessChanceData",
				"bcd_code='" + store.get("bcd_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });		
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"BusinessChanceData", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "bcd_id", store.get("bcd_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteBusinessChanceData(int bcd_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { bcd_id });
		// 删除
		baseDao.deleteById("BusinessChanceData", "bcd_id", bcd_id);
		// 记录操作
		baseDao.logger.delete(caller, "bcd_id", bcd_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, bcd_id);
	}

	@Override
	public void updateBusinessChanceData(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		
		//修改备注
		Map<Object,Object> recvMap = new HashMap<Object,Object>();
		recvMap = FlexJsonUtil.fromJson(formStore);
		Object[] descAndDetno = baseDao.getFieldsDataByCondition("businesschancestage", new String[]{"bs_point","bs_pointdetno"}, "bs_code='"+recvMap.get("bcd_bscode")+"'");
		if(descAndDetno!=null){
			if(descAndDetno[0]!=null&&descAndDetno[1]!=null){
				String remark = "";
				String[] descs = descAndDetno[0].toString().split("#");
				String[] detnos = descAndDetno[1].toString().split("#");
				
				for(int i=0;i<descs.length;i++){
					if(recvMap.get("bcd_column"+detnos[i])!=null&&!"".equals(recvMap.get("bcd_column"+detnos[i]))){
						remark += ";" + descs[i] + ":" + recvMap.get("bcd_column"+detnos[i]); 	
					}
				}
				remark = remark.substring(1);
				
				recvMap.remove("bcd_remark");
				recvMap.put("bcd_remark", remark);
			}
		}
		
		// 修改BusinessChanceData
		String formSql = SqlUtil.getUpdateSqlByFormStore(recvMap,
				"BusinessChanceData", "bcd_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "bcd_id", store.get("bcd_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void auditBusinessChanceData(int bcd_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("BusinessChanceData",
				"bcd_statuscode", "bcd_id=" + bcd_id);
		Object[] datas = baseDao.getFieldsDataByCondition("BusinessChanceData",
				"bcd_bcid,bcd_bscode,bcd_bsname", "bcd_id=" + bcd_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, bcd_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"BusinessChanceData",
				"bcd_statuscode='AUDITED',bcd_status='"
						+ BaseUtil.getLocalMessage("AUDITED") + "'", "bcd_id="
						+ bcd_id);
		baseDao.updateByCondition("BusinessChanceData", "bcd_bscode='"
				+ datas[1] + "',bcd_bsname='" + datas[2] + "'", "bcd_id="
				+ bcd_id);
		//新华商智特有的逻辑，商机动态审核后直接更新商机主表的当前阶段和最后更新日期。
		if(baseDao.isDBSetting(caller, "UPDATACURRENTPROCESS")){ 
			baseDao.updateByCondition("BusinessChance", "bc_currentprocess='"+datas[2] + "'", "bc_id=" + datas[0]);
			baseDao.updateByCondition("BusinessChance", "bc_lastdate=sysdate", "bc_id=" + datas[0]);	
		}else {
			Object bs_detno=baseDao.getFieldDataByCondition("businesschancestage", "bs_detno", "bs_name=(select BCD_BSNAME from businesschancedata where bcd_id='"+bcd_id+"')");
			Object olddetno=baseDao.getFieldDataByCondition("businesschancestage", "bs_detno", "bs_name=(select bc_currentprocess from businesschance where BC_ID=(select bcd_BCID from  businesschancedata where bcd_id='"+bcd_id+"'))");
			if(bs_detno!=null && !bs_detno.equals("") && olddetno!=null && !olddetno.equals("")){
				if(Integer.parseInt(bs_detno.toString())>Integer.parseInt(olddetno.toString())){	
					baseDao.updateByCondition("BusinessChance", "bc_currentprocess='"+datas[2] + "'", "bc_id=" + datas[0]);
					baseDao.updateByCondition("BusinessChance", "bc_lastdate=sysdate", "bc_id=" + datas[0]);				
				}
			}
		}
		
		baseDao.updateByCondition("BusinessChance", "bc_statuscode='UNVALID', "
				+ "bc_status='已失效'", "bc_id=(select bcd_bcid from BusinessChanceData where bcd_type='失效' and bcd_id="+bcd_id+")");	
		// 记录操作
		baseDao.logger.audit(caller, "bcd_id", bcd_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, bcd_id);
	}
	
	@Override
	public void resAuditBusinessChanceData(int bcd_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("BusinessChanceData",
				"bcd_statuscode", "bcd_id=" + bcd_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, bcd_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"BusinessChanceData",
				"bcd_statuscode='ENTERING',bcd_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "bcd_id="
						+ bcd_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "bcd_id", bcd_id);
		handlerService.afterResAudit(caller, bcd_id);
	}

	@Override
	public void submitBusinessChanceData(int bcd_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("BusinessChanceData",
				"bcd_statuscode", "bcd_id=" + bcd_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, bcd_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"BusinessChanceData",
				"bcd_statuscode='COMMITED',bcd_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "bcd_id="
						+ bcd_id);
		// 记录操作
		baseDao.logger.submit(caller, "bcd_id", bcd_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, bcd_id);
	}

	@Override
	public void resSubmitBusinessChanceData(int bcd_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("BusinessChanceData",
				"bcd_statuscode", "bcd_id=" + bcd_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, bcd_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"BusinessChanceData",
				"bcd_statuscode='ENTERING',bcd_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "bcd_id="
						+ bcd_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "bcd_id", bcd_id);
		handlerService.afterResSubmit(caller, bcd_id);
	}
}
