package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.ProdReplaceMotherService;



@Service("prodReplaceMotherService")
public class ProdReplaceMotherServiceImpl implements ProdReplaceMotherService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveProdReplaceMother(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
	/*	//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Product", "pr_code='" + store.get("pr_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}*/
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller,new Object[]{store,gstore});
		/*//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Product", new String[]{}, new Object[]{});
		baseDao.execute(formSql);*/
		////保存Detail
		Object[] pre_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			pre_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				pre_id[i] = baseDao.getSeqId("PRODREPLACE_SEQ");
			}
		} else {
			pre_id[0] = baseDao.getSeqId("PRODREPLACE_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "ProdReplace", "pre_id", pre_id);
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "pr_id", store.get("pr_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller,new Object[]{store,gstore});
	}
	
	@Override
	public void deleteProdReplaceMother(int pr_id, String caller) {
		//只能删除在录入的采购单!
		Object status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_id=" + pr_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller,  new Object[]{pr_id});
		/*//删除
		baseDao.deleteById("Product", "pr_id", pr_id);*/
		//删除明细
		baseDao.deleteById("PRODREPLACE", "pre_itemid", pr_id);
		//记录操作
		baseDao.logger.delete(caller, "pr_id", pr_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{pr_id});
	}
	
	@Override
	public void updateProdReplaceMotherById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_id=" + store.get("pr_id"));
		StateAssert.updateOnlyEntering(status);
		//更新采购计划下达数\本次下达数\状态
		//purchaseDao.updatePurchasePlan(Integer.parseInt((String)store.get("pu_id")));
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store,gstore});
		/*//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(formStore, "Product", "pr_id");
		baseDao.execute(formSql);*/
		//修改Detail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ProdReplace", "pre_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("pre_id") == null || s.get("pre_id").equals("") || s.get("pre_id").equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("PRODREPLACE_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "ProdReplace", new String[]{"pre_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "pr_id", store.get("pr_id"));
		//更新上次采购价格、供应商
		//purchaseDao.updatePrePurchase((String)store.get("pu_code"), (String)store.get("pu_date"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store,gstore});
	}
	
	@Override
	public void auditProdReplaceMother(int pr_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_id=" + pr_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller,new Object[]{pr_id});
		//执行审核操作
		baseDao.audit("Product", "pr_id=" + pr_id, "pr_status", "pr_statuscode", "pr_auditdate", "pr_auditman");
		//记录操作
		baseDao.logger.audit(caller, "pr_id", pr_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller,new Object[]{pr_id});
	}
	@Override
	public void resAuditProdReplaceMother(int pr_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_id=" + pr_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("Product", "pr_id=" + pr_id, "pr_status", "pr_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "pr_id", pr_id);
	}
	@Override
	public void submitProdReplaceMother(int pr_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_id=" + pr_id);
		StateAssert.submitOnlyEntering(status);			
		//只能选择已审核的物料!
		List<Object> codes = baseDao.getFieldDatasByCondition("ProdReplace", "pre_prodcode", "pre_itemid=" + pr_id);
		for(Object c:codes){
			status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_code='" + c + "'");
			if(!status.equals("AUDITED")){
				BaseUtil.showError(BaseUtil.getLocalMessage("product_onlyAudited") + 
						"<a href=\"javascript:openUrl('jsps/scm/product/product.jsp?formCondition=pr_codeIS" + c +  "')\">" + c + "</a>&nbsp;");
			}
		}	
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller,new Object[]{pr_id});
		//执行提交操作
		baseDao.submit("Product", "pr_id=" + pr_id, "pr_status", "pr_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "pr_id", pr_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller,new Object[]{pr_id});
	}
	
	@Override
	public void resSubmitProdReplaceMother(int pr_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_id=" + pr_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller,  new Object[]{pr_id});
		//执行反提交操作
		baseDao.resOperate("Product", "pr_id=" + pr_id, "pr_status", "pr_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "pr_id", pr_id);
		handlerService.afterResSubmit(caller,  new Object[]{pr_id});
	}
}
