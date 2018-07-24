package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.model.Key;
import com.uas.erp.service.scm.SaleProjectService;

@Service
public class SaleProjectServiceImpl implements SaleProjectService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private TransferRepository transferRepository;
	
	@Override
	public void saveSaleProject(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("SaleProject", "sp_code='" + store.get("sp_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "SaleProject", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "sp_id", store.get("sp_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}
	@Override
	public void deleteSaleProject(int sp_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("SaleProject", "sp_statuscode", "sp_id=" + sp_id);
		if(!status.equals("ENTERING")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{sp_id});
		//删除PreSale
		baseDao.deleteById("SaleProject", "sp_id", sp_id);		
		//记录操作
		baseDao.logger.delete(caller, "sp_id", sp_id);		
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{sp_id});
	}
	
	@Override
	public void updateSaleProjectById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("SaleProject", "sp_statuscode", "sp_id=" + store.get("sp_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "SaleProject", "sp_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "sp_id", store.get("sp_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}
	@Override
	public void auditSaleProject(int sp_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("SaleProject", "sp_statuscode", "sp_id=" + sp_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before",  new Object[]{sp_id});
		//执行审核操作
		baseDao.audit("SaleProject", "sp_id=" + sp_id, "sp_status", "sp_statuscode", "sp_auditdate", "sp_auditman");
		//记录操作
		baseDao.logger.audit(caller, "sp_id", sp_id);	
		//执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[]{sp_id});
	}
	@Override
	public void resAuditSaleProject(int sp_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("SaleProject", "sp_statuscode", "sp_id=" + sp_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.handler(caller, "resAudit", "before",  new Object[]{sp_id});
		//执行反审核操作
		baseDao.resOperate("SaleProject", "sp_id=" + sp_id, "sp_status", "sp_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "sp_id", sp_id);	
		handlerService.handler(caller, "resAudit", "after",  new Object[]{sp_id});
	}
	@Override
	public void submitSaleProject(int sp_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("SaleProject", "sp_statuscode", "sp_id=" + sp_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[]{sp_id});
		//执行提交操作
		baseDao.submit("SaleProject", "sp_id=" + sp_id, "sp_status", "sp_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "sp_id", sp_id);
		//执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[]{sp_id});
	}
	@Override
	public void resSubmitSaleProject(int sp_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("SaleProject", "sp_statuscode", "sp_id=" + sp_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[]{sp_id});
		//执行反提交操作
		baseDao.resOperate("SaleProject", "sp_id=" + sp_id, "sp_status", "sp_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "sp_id", sp_id);	
		handlerService.handler(caller, "resCommit", "after", new Object[]{sp_id});
	}
	@Override
	public int turnProject(int sp_id, String caller) {
		Key key = transferRepository.transfer("SaleProject!ToProject", sp_id);
		int id = key.getId();
		// 更新原表字段
		baseDao.execute("update SaleProject set sp_turnstatus='已转立项' where sp_id=" + sp_id);
		return id;
	}	
}
