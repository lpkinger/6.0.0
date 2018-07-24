package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.AgentPriceService;

@Service("agentPriceService")
public class AgentPriceServiceImpl implements AgentPriceService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveAgentPrice(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("AgentPrice", "ap_code='" + store.get("ap_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store, grid});
		//保存SalePrice
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "AgentPrice", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//保存SalePriceDetail
		for(int i=0;i<grid.size();i++){
			Map<Object, Object> map = grid.get(i);
			map.put("apd_id", baseDao.getSeqId("AGENTPRICEDETAIL_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "AgentPriceDetail");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.save(caller, "ap_id", store.get("ap_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store, grid});
	}
	@Override
	public void deleteAgentPrice(int ap_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("AgentPrice", "ap_statuscode", "ap_id=" + ap_id);
		if(!status.equals("ENTERING")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{ap_id});
		//删除SalePrice
		baseDao.deleteById("AgentPrice", "ap_id", ap_id);
		//删除SalePriceDetail
		baseDao.deleteById("AgentPricedetail", "apd_spid", ap_id);
		//记录操作
		baseDao.logger.delete(caller, "ap_id", ap_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{ap_id});
	}
	
	@Override
	public void updateAgentPriceById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("AgentPrice", "ap_statuscode", "ap_id=" + store.get("ap_id"));
		if(!status.equals("ENTERING")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		//执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store, gstore});
		//修改SalePrice
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "AgentPrice", "ap_id");
		baseDao.execute(formSql);
		//修改SalePriceDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "AgentPriceDetail", "apd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("apd_id") == null || s.get("apd_id").equals("") || s.get("apd_id").equals("0") ||
					Integer.parseInt(s.get("apd_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("AGENTPRICEDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "AgentPriceDetail", new String[]{"apd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "ap_id", store.get("ap_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store, gstore});
	}
	@Override
	public void printAgentPrice(int ap_id, String caller) {
		//执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[]{ap_id});
		//执行打印操作
		baseDao.logger.print(caller, "ap_id", ap_id);
		//执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[]{ap_id});
	}
	@Override
	public void auditAgentPrice(int ap_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("AgentPrice", "ap_statuscode", "ap_id=" + ap_id);
		if(!status.equals("COMMITED")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.audit_onlyCommited"));
		}
		//执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[]{ap_id});
		String enuu = baseDao.getDBSetting("sys.enuu");
		//执行审核操作
		baseDao.audit("AgentPrice", "ap_id=" + ap_id, "ap_status", "ap_statuscode", "ap_auditdate", "ap_auditman");
		baseDao.updateByCondition("AgentPrice", "ap_sendstatus='待上传',ap_enid='"+enuu+"'" , "ap_id=" + ap_id);
		//记录操作
		baseDao.logger.audit(caller, "ap_id", ap_id);
		//执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[]{ap_id});
	}
	@Override
	public void resAuditAgentPrice(int ap_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("AgentPrice", "ap_statuscode", "ap_id=" + ap_id);
		if(!status.equals("AUDITED")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
		}
		handlerService.handler(caller, "resAudit", "before", new Object[]{ap_id});
		//执行反审核操作
		baseDao.resOperate("AgentPrice", "ap_id=" + ap_id, "ap_status", "ap_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "ap_id", ap_id);
		handlerService.handler(caller, "resAudit", "after", new Object[]{ap_id});
	}
	@Override
	public void submitAgentPrice(int ap_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("AgentPrice", "ap_statuscode", "ap_id=" + ap_id);
		if(!status.equals("ENTERING")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.submit_onlyEntering"));
		}
		//执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[]{ap_id});
		//执行提交操作
		baseDao.submit("AgentPrice", "ap_id=" + ap_id, "ap_status", "ap_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "ap_id", ap_id);
		//执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[]{ap_id});
	}
	@Override
	public void resSubmitAgentPrice(int ap_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("AgentPrice", "ap_statuscode", "ap_id=" + ap_id);
		if(!status.equals("COMMITED")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resSubmit_onlyCommited"));
		}
		handlerService.handler(caller, "resCommit", "before", new Object[]{ap_id});
		//执行反提交操作
		baseDao.resOperate("AgentPrice", "ap_id=" + ap_id, "ap_status", "ap_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "ap_id", ap_id);
		handlerService.handler(caller, "resCommit", "after", new Object[]{ap_id});
	}
}
