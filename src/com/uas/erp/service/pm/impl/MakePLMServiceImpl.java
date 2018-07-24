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
import com.uas.erp.service.pm.MakePLMService;

@Service("makePLMService")
public class MakePLMServiceImpl implements MakePLMService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveMakeBase(String formStore,String gridStore , String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Make", "ma_id='" + store.get("ma_id") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}	
		int ma_qty = Integer.parseInt(store.get("ma_qty").toString());
		int ma_madeqty = Integer.parseInt(store.get("ma_madeqty").toString());
		if(ma_madeqty < ma_qty){
			store.put("ma_finishstatus", BaseUtil.getLocalMessage("UNCOMPLET"));
			store.put("ma_finishstatuscode", "UNCOMPLET");
		} else {
			store.put("ma_finishstatuscode", BaseUtil.getLocalMessage("COMPLETED"));
		}
		store.put("ma_checkstatuscode", "UNAPPROVED");
		store.put("ma_checkstatus", BaseUtil.getLocalMessage("UNAPPROVED"));
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller,new Object[]{store,gstore});
		//保存
		Object obj = baseDao.getFieldDataByCondition("Product", "pr_whcode", "pr_code='" + store.get("ma_prodcode") + "'");
		store.put("ma_whcode", obj);
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Make", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
	    //保存MakeMaterial
		for(Map<Object, Object> s:gstore){
			s.put("mm_id", baseDao.getSeqId("MAKEMATERIAL_SEQ"));
			s.put("mm_status", "ENTERING");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gstore, "MakeMaterial");
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "ma_id", store.get("ma_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//执行保存后的其它逻辑
		handlerService.afterSave(caller,new Object[]{store,gstore});
	}
	@Override
	public void deleteMakeBase(int ma_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{ma_id});
		//删除BOM
		baseDao.deleteById("Make", "ma_id", ma_id);	
		//删除MakeMaterial
				baseDao.deleteById("MakeMaterial", "mm_maid", ma_id);
		//记录操作
		baseDao.logger.delete(caller, "ma_id", ma_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{ma_id});
	}
	
	@Override
	public void updateMakeBaseById(String formStore, String gridStore ,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		int ma_qty = Integer.parseInt(store.get("ma_qty").toString());
		int ma_madeqty = Integer.parseInt(store.get("ma_madeqty").toString());
		//完工状态
		if(ma_madeqty < ma_qty){
			store.put("ma_finishstatus", BaseUtil.getLocalMessage("UNCOMPLET"));
			store.put("ma_finishstatuscode", "UNCOMPLET");
		} else {
			store.put("ma_finishstatus", BaseUtil.getLocalMessage("COMPLETED"));
			store.put("ma_finishstatuscode", "COMPLETED");
		}
		//批准状态
		store.put("ma_checkstatus", "UNAPPROVED");
		store.put("ma_checkstatus", BaseUtil.getLocalMessage("UNAPPROVED"));
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store,gstore});
		//修改
		Object obj = baseDao.getFieldDataByCondition("Product", "pr_whcode", "pr_code='" + store.get("ma_prodcode") + "'");
		store.put("ma_whcode", obj);
		store.put("ma_updateman",SystemSession.getUser().getEm_name());
		store.put("ma_updatedate", DateUtil.currentDateString(null));
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Make", "ma_id");
		baseDao.execute(formSql);
		//修改MakeMaterial
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "MakeMaterial", "mm_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("mm_id") == null || s.get("mm_id").equals("") || s.get("mm_id").equals("0") || 
					Integer.parseInt(s.get("mm_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("MAKEMATERIAL_SEQ");
				s.put("mm_status", "ENTERING");
				String sql = SqlUtil.getInsertSqlByMap(s, "MakeMaterial", new String[]{"mm_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "ma_id", store.get("ma_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store,gstore});
	}
	
	
	@Override
	public void auditMakeBase(int ma_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Make", "ma_statuscode", "ma_id=" + ma_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{ma_id});	
		//执行审核操作
		baseDao.audit("Make", "ma_id=" + ma_id, "ma_status", "ma_statuscode", "ma_auditdate", "ma_auditman");
		baseDao.updateByCondition("MakeMaterial", "mm_status='AUDITED'", "mm_maid=" + ma_id);
		//记录操作
		baseDao.logger.audit(caller, "ma_id", ma_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{ma_id});
	}
	@Override
	public void resAuditMakeBase(int ma_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Make", "ma_statuscode", "ma_id=" + ma_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.updateByCondition("Make", "ma_statuscode='ENTERING',ma_status='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "ma_id=" + ma_id);
		baseDao.updateByCondition("MakeMaterial", "mm_status='ENTERING'", "mm_maid=" + ma_id);
		//记录操作
		baseDao.logger.resAudit(caller, "ma_id", ma_id);
	}
	@Override
	public void submitMakeBase(int ma_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Make", "ma_statuscode", "ma_id=" + ma_id);
		StateAssert.submitOnlyEntering(status);
		      //只能选择已审核的客户!
				Object code = baseDao.getFieldDataByCondition("Make", "ma_custcode", "ma_id=" + ma_id);
				status = baseDao.getFieldDataByCondition("Customer", "cu_auditstatuscode", "cu_code='" + code + "'");
				if(status != null && !status.equals("AUDITED")){
					BaseUtil.showError(BaseUtil.getLocalMessage("customer_onlyAudited") + 
						"<a href=\"javascript:openUrl('jsps/scm/sale/customer.jsp?formCondition=cu_codeIS" + code +  "')\">" + code + "</a>&nbsp;");
				}
		        //只能选择已审核的物料!
				Object code1 = baseDao.getFieldDataByCondition("Make", "ma_prodcode", "ma_id=" + ma_id);
				status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_code='" + code1 + "'");
				if(status != null && !status.equals("AUDITED")){
					BaseUtil.showError(BaseUtil.getLocalMessage("product_onlyAudited") + 
						"<a href=\"javascript:openUrl('jsps/scm/product/product.jsp?formCondition=pr_codeIS" + code1 +  "')\">" + code1 + "</a>&nbsp;");
				}
				//只能选择已审核的物料!
				List<Object> codes = baseDao.getFieldDatasByCondition("MakeMaterial", "mm_prodcode", "mm_maid=" + ma_id);
				for(Object c:codes){
					status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_code='" + c + "'");
					if(status != null && !status.equals("AUDITED")){
						BaseUtil.showError(BaseUtil.getLocalMessage("product_onlyAudited") + 
								"<a href=\"javascript:openUrl('jsps/scm/product/product.jsp?formCondition=pr_codeIS" + c +  "')\">" + c + "</a>&nbsp;");
					}
				}
		
		//执行提交前的其它逻辑
	    handlerService.beforeSubmit(caller, new Object[]{ma_id});
		//执行提交操作
		baseDao.updateByCondition("Make", "ma_statuscode='COMMITED',ma_status='" + 
				BaseUtil.getLocalMessage("COMMITED") + "'", "ma_id=" + ma_id);
		//完工状态
		baseDao.updateByCondition("Make", "ma_finishstatuscode='UNCOMPLET',ma_finishstatus='" + 
				BaseUtil.getLocalMessage("UNCOMPLET") + "'", "ma_id=" + ma_id + " AND ma_madeqty<ma_qty");
		baseDao.updateByCondition("Make", "ma_finishstatuscode='COMPLETED',ma_finishstatus='" + 
				BaseUtil.getLocalMessage("COMPLETED") + "'", "ma_id=" + ma_id + " AND ma_madeqty>ma_qty");
		baseDao.updateByCondition("MakeMaterial", "mm_status='COMMITED'", "mm_maid=" + ma_id);
		//更新替代已转数
        baseDao.updateByCondition("MakeMaterialReplace", "mp_repqty=(SELECT sum(nvl(pd_outqty,0)-nvl(pd_inqty,0)) FROM ProdIODetail left join MakeMaterial on mm_id=pd_orderid " + 
        		"WHERE mm_id=mp_mmid AND pd_prodcode=mp_prodcode AND pd_auditstatus<>'DELETED' AND (pd_piclass='生产领料单' or pd_piclass='生产退料单' or pd_piclass='生产补料单'  or pd_piclass='委外补料单' or pd_piclass='委外领料单' or pd_piclass='委外退料单'))", 
        		"mp_mmid in(SELECT mm_id FROM MakeMaterial WHERE mm_maid=" + ma_id + ")");
		//更新替代已领数
        baseDao.updateByCondition("MakeMaterialReplace", "mp_haverepqty=(SELECT sum(nvl(pd_outqty,0)-nvl(pd_inqty,0)) FROM ProdIODetail left join MakeMaterial on mm_id=pd_orderid " + 
        		"WHERE mm_id=mp_mmid AND pd_prodcode=mp_prodcode AND pd_status=99 AND (pd_piclass='生产领料单' or pd_piclass='生产退料单' or pd_piclass='生产补料单'  or pd_piclass='委外补料单' or pd_piclass='委外领料单' or pd_piclass='委外退料单'))", 
        		"mp_mmid in(SELECT mm_id FROM MakeMaterial WHERE mm_maid=" + ma_id + ")");
		//更新替代总已转数
        baseDao.updateByCondition("MakeMaterial", "mm_repqty=(SELECT sum(mp_repqty) FROM MakeMaterialreplace WHERE mp_mmid=mm_id)", 
        		"mm_maid=" + ma_id);
		//更新替代总已领数
        baseDao.updateByCondition("MakeMaterial", "mm_haverepqty=(SELECT sum(mp_haverepqty) FROM MakeMaterialreplace WHERE mp_mmid=mm_id)", 
        		"mm_maid=" + ma_id);
		//更新替代维护数
        baseDao.updateByCondition("MakeMaterial", "mm_canuserepqty=(SELECT sum(mp_canuseqty) FROM MakeMaterialreplace WHERE mp_mmid=mm_id)", 
        		"mm_maid=" + ma_id);
		//记录操作
		baseDao.logger.submit(caller, "ma_id", ma_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{ma_id});
	}
	@Override
	public void resSubmitMakeBase(int ma_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Make", "ma_statuscode", "ma_id=" + ma_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller,new Object[]{ma_id});
		//执行反提交操作
		baseDao.updateByCondition("Make", "ma_statuscode='ENTERING',ma_status='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "ma_id=" + ma_id);
		baseDao.updateByCondition("MakeMaterial", "mm_status='ENTERING'", "mm_maid=" + ma_id);
		//记录操作
		baseDao.logger.resSubmit(caller, "ma_id", ma_id);
		handlerService.afterResSubmit(caller,new Object[]{ma_id});
	}
	@Override
	public void approveMakeBase(int ma_id, String caller) {
		//执行批准前的其它逻辑
		handlerService.handler("Make!PLM", "approve", "before", new Object[]{ma_id,SystemSession.getLang()});
		//执行批准操作
		baseDao.updateByCondition("Make", "ma_checkstatuscode='APPROVE',ma_checkstatus='" + BaseUtil.getLocalMessage("APPROVE") + 
				"',ma_checkdate=" + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + ",ma_checkman='" + 
				SystemSession.getUser().getEm_name() + "'", "ma_id=" + ma_id);
		//记录操作
		baseDao.logger.approve(caller, "ma_id", ma_id);
		//执行批准后的其它逻辑
		handlerService.handler("Make!PLM", "approve", "after", new Object[]{ma_id,SystemSession.getLang()});
	}
	@Override
	public void resApproveMakeBase(int ma_id, String caller) {
		//执行反批准操作
		baseDao.updateByCondition("Make", "ma_checkstatuscode='UNAPPROVED',ma_checkstatus='" + 
				BaseUtil.getLocalMessage("UNAPPROVED") + "'", "ma_id=" + ma_id);
		//记录操作
		baseDao.logger.resApprove(caller, "ma_id", ma_id);
	}
	@Override
	public void endMakeBase(int ma_id, String caller) {
		//只能对状态为[已审核]的订单进行结案操作!
		Object status = baseDao.getFieldDataByCondition("Make", "ma_statuscode", "ma_id=" + ma_id);
		if(!status.equals("AUDITED")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.end_onlyAudited",SystemSession.getLang()));
		}
		//执行结案前的其它逻辑
		handlerService.handler("Make!PLM", "end", "before", new Object[]{ma_id,SystemSession.getLang()});
		//执行结案操作
		baseDao.updateByCondition("Make", "ma_statuscode='FINISH',ma_status='" + 
				BaseUtil.getLocalMessage("FINISH") + "'", "ma_id=" + ma_id);
		baseDao.updateByCondition("MakeMaterial", "mm_status='FINISH'", "mm_maid=" + ma_id);
		//记录操作
		baseDao.logger.getMessageLog(BaseUtil.getLocalMessage("msg.end"), BaseUtil.getLocalMessage("msg.endSuccess"), caller, "ma_id", ma_id);
		//执行结案后的其它逻辑
		handlerService.handler("Make!PLM", "end", "after", new Object[]{ma_id,SystemSession.getLang()});
	}
	@Override
	public void resEndMakeBase(int ma_id, String caller) {
		//只能对状态为[已结案]的订单进行反结案操作!
		Object status = baseDao.getFieldDataByCondition("Make", "ma_statuscode", "ma_id=" + ma_id);
		if(!status.equals("FINISH")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resEnd_onlyEnd",SystemSession.getLang()));
		}
		//执行反批准操作
		baseDao.updateByCondition("Make", "ma_statuscode='ENTERING',ma_status='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "ma_id=" + ma_id);
		baseDao.updateByCondition("MakeMaterial", "mm_status='ENTERING'", "mm_maid=" + ma_id);
		//记录操作
		baseDao.logger.resEnd(caller, "ma_id", ma_id);
	}
	
}
