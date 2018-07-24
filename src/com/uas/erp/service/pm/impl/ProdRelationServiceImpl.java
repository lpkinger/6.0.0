package com.uas.erp.service.pm.impl;

import java.util.ArrayList;
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
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.ProdRelationService;

@Service
public class ProdRelationServiceImpl implements ProdRelationService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProdRelation(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		Map<Object, List<Map<Object, Object>>> gmstore=BaseUtil.groupMap(gstore, "prr_repcode");
		if(gstore.size()!=gmstore.size()){
			BaseUtil.showError("替代料不能重复录入多行");
		}
		//主件编号：同一主件编号只能存在一张“在录入或已提交”的单据，如在多张限制保存并提示；		
		SqlRowList rs =  baseDao.queryForRowSet("select count(0) cn from ProdRelation where prr_soncode= '"+store.get("prr_soncode")+"' and  prr_usestatuscode in('ENTERING','COMMITED') and prr_thisid<>'"+store.get("prr_thisid")+"'");
		if(rs.next()){
			if(Integer.valueOf(rs.getString("cn")) > 0){
			  BaseUtil.showError("主件编号："+store.get("prr_soncode")+",只能存在一张“在录入或已提交”的单据");
			}
		}
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store,gstore});	
//		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ProdRelation", new String[]{}, new Object[]{});
//		baseDao.execute(formSql);
		//修改ProdRelation
		List<String> gridSql = new ArrayList<String>();
//		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ProdRelation", "prr_id");
		for(Map<Object, Object> s:gstore){
			if(store.get("prr_soncode").equals(s.get("prr_repcode"))){
				BaseUtil.showError("替代料编号与主料编号相同");
			}
			if(s.get("prr_id") == null || s.get("prr_id").equals("") || s.get("prr_id").equals("0") ||
					Integer.parseInt(s.get("prr_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("PRODRELATION_SEQ");
				s.put("prr_usestatuscode", "ENTERING");
				s.put("prr_usestatus", BaseUtil.getLocalMessage("ENTERING"));
				s.put("prr_thisid", store.get("prr_thisid"));
				s.put("prr_thiscode", store.get("prr_thiscode"));
				s.put("prr_soncode", store.get("prr_soncode"));
				s.put("prr_sonname", store.get("prr_sonname"));
				s.put("prr_sonspec", store.get("prr_sonspec"));
				s.put("prr_sonvendcode", store.get("prr_sonvendcode"));
				s.put("prr_sonvendname", store.get("prr_sonvendname"));
				s.put("prr_inman", store.get("prr_inman"));
				String sql = SqlUtil.getInsertSqlByMap(s, "ProdRelation", new String[]{"prr_id"}, new Object[]{id});
				gridSql.add(sql);
			}	
		 }
		baseDao.execute(gridSql);
		baseDao.execute("update ProdRelation set prr_builddate=sysdate,prr_inman='"+SystemSession.getUser().getEm_name()+"' where prr_thisid=" + store.get("prr_thisid") +" and prr_builddate is null and prr_inman is null");
		//记录操作
		baseDao.logger.save(caller, "prr_thisid", store.get("prr_thisid"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store,gstore});		
	}
	@Override
	public void updateProdRelation(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);	
		Map<Object, List<Map<Object, Object>>> gmstore=BaseUtil.groupMap(gstore, "prr_repcode");
		if(gstore.size()!=gmstore.size()){
			BaseUtil.showError("替代料号不能重复录入多行");
		} 
		//主件编号：同一主件编号只能存在一张“在录入或已提交”的单据，如在多张限制保存并提示；		
		SqlRowList rs =  baseDao.queryForRowSet("select prr_soncode from ProdRelation where prr_soncode= '"+store.get("prr_soncode")+" ' and  prr_usestatuscode in('ENTERING','COMMITED') and prr_thisid<>'"+store.get("prr_thisid")+"'");
		if(rs.next()){
			BaseUtil.showError("主件编号："+store.get("prr_soncode")+",只能存在一张“在录入或已提交”的单据");
		}
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,new Object[]{store,gstore});
		//更新主表
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "ProdRelation", "prr_thisid"));
		//修改ProdRelation
		//修改成对应的
		List<String> gridSql = new ArrayList<String> ();
//		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ProdRelation", "prr_id");
		for(Map<Object, Object> s:gstore){
			if(store.get("prr_soncode").equals(s.get("prr_repcode"))){
				BaseUtil.showError("序号:"+store.get("prr_detno")+"替代料编号与主料编号相同");
			}
			if(s.get("prr_id") == null || s.get("prr_id").equals("") || s.get("prr_id").equals("0") ||
					Integer.parseInt(s.get("prr_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("PRODRELATION_SEQ");
				s.put("prr_usestatuscode", "ENTERING");
				s.put("prr_usestatus", BaseUtil.getLocalMessage("ENTERING"));
				s.put("prr_thisid", store.get("prr_thisid"));
				s.put("prr_thiscode", store.get("prr_thiscode"));
				s.put("prr_soncode", store.get("prr_soncode"));
				s.put("prr_sonname", store.get("prr_sonname"));
				s.put("prr_sonspec", store.get("prr_sonspec"));
				s.put("prr_sonvendcode", store.get("prr_sonvendcode"));
				s.put("prr_sonvendname", store.get("prr_sonvendname"));
				s.put("prr_inman", SystemSession.getUser().getEm_name());
				String sql = SqlUtil.getInsertSqlByMap(s, "ProdRelation", new String[]{"prr_id"}, new Object[]{id});
				gridSql.add(sql);
			}else{
				//获取from字段数据。
				s.put("prr_soncode", store.get("prr_soncode"));
				s.put("prr_sonname", store.get("prr_sonname"));
				s.put("prr_sonspec", store.get("prr_sonspec"));
				s.put("prr_sonvendcode", store.get("prr_sonvendcode"));
				s.put("prr_sonvendname", store.get("prr_sonvendname"));
				s.put("prr_inman", SystemSession.getUser().getEm_name());
				String sql = SqlUtil.getUpdateSqlByFormStore(s, "ProdRelation", "prr_id", "prr_");
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		baseDao.execute("update ProdRelation set prr_builddate=sysdate,prr_inman='"+SystemSession.getUser().getEm_name()+"' where prr_thisid=" + store.get("prr_thisid") +" and prr_builddate is null and prr_inman is null");
		//记录操作
		baseDao.logger.update(caller, "prr_thisid", store.get("prr_thisid"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[]{store,gstore});
	}

	@Override
	public void submitProdRelation(int id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ProdRelation", "prr_usestatuscode", "prr_thisid=" + id);
		StateAssert.submitOnlyEntering(status);  
		//限制主料+替代料存在多行记录
		SqlRowList rs =  baseDao.queryForRowSet("select count(1) n,wm_concat('单号:'||prr_thiscode||'替代料号:'||prr_repcode) str from(select distinct prr_thiscode,prr_repcode  from ProdRelation where prr_thisid="+ id +" and (prr_soncode,prr_repcode) in (select prr_soncode,prr_repcode from prodrelation b where b.prr_id<>prodrelation.prr_id and NVL(prr_usestatuscode,' ')<>'DISABLE'))");
		if(rs.next()){
			if (rs.getInt("n")>0){
				BaseUtil.showError(""+rs.getString("str")+",录入重复");
			} 
		} 
		//执行提交前的其它逻辑
		handlerService.beforeSubmit("ProdRelation", new Object[]{id});
		baseDao.execute("update ProdRelation set prr_usestatuscode='COMMITED',prr_usestatus='"+
				BaseUtil.getLocalMessage("COMMITED")+"' where prr_thisid = " +
				id + " and prr_usestatuscode='ENTERING'");
		//记录操作
		baseDao.logger.submit(caller, "prr_thisid", id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit("ProdRelation", new Object[]{id});
	}

	@Override
	public void resSubmitProdRelation(int id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ProdRelation", "prr_usestatuscode", "prr_thisid=" + id);
		StateAssert.resSubmitOnlyCommited(status);  
		handlerService.beforeResSubmit("ProdRelation", new Object[]{id});
		baseDao.resOperate("ProdRelation", "prr_thisid = "+id, "prr_usestatus", "prr_usestatuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "prr_thisid", id);
		handlerService.afterResSubmit("ProdRelation", new Object[]{id});
	}

	@Override
	public void auditProdRelation(int id, String caller) { 
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ProdRelation", "prr_usestatuscode", "prr_thisid=" + id);
		StateAssert.auditOnlyCommited(status);
		handlerService.beforeAudit(caller, new Object[]{id});
		baseDao.audit("ProdRelation ", "prr_thisid = "+	id , "prr_usestatus", "prr_usestatuscode", "prr_auditdate", "prr_auditman");
		//记录操作
		baseDao.logger.audit(caller, "prr_thisid", id);
		handlerService.afterAudit(caller, new Object[]{id});
	}

	@Override
	public void resAuditProdRelation(int id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ProdRelation", "prr_usestatuscode", "prr_thisid=" + id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller,new Object[]{id});
		baseDao.resOperate("ProdRelation", "prr_thisid = "+	id , "prr_usestatus", "prr_usestatuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "prr_thisid", id);
		handlerService.afterResAudit(caller, new Object[]{id});
	}

	@Override
	public void deleteProdRelation(int id, String caller) {  
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("ProdRelation", "prr_usestatuscode", "prr_thisid=" + id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[]{id});
		baseDao.execute("delete from ProdRelation where prr_thisid = "+
				id + " and prr_usestatuscode='ENTERING'"); 
		//记录操作
		baseDao.logger.delete(caller, "prr_thisid", id);
		handlerService.afterDel(caller, new Object[]{id});
	}
	public String bannedProdRelation(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> map : maps) {		
			int prrid = Integer.parseInt(map.get("prr_id").toString());
			handlerService.handler("ProdRelation", "banned", "before", new Object[]{prrid});
			int prr_thisid = Integer.parseInt(map.get("prr_thisid").toString());
			baseDao.execute("update ProdRelation set prr_usestatuscode='DISABLE',prr_usestatus='"+
					BaseUtil.getLocalMessage("DISABLE")+"',prr_disabledate=sysdate where prr_id = "+
					prrid + " and prr_usestatuscode='AUDITED'");
			// 记录日志
			baseDao.logger.others(BaseUtil.getLocalMessage("msg.banned"),"禁用替代料："+map.get("prr_repcode")+","+ BaseUtil.getLocalMessage("msg.bannedSuccess"), "ProdRelation", "prr_thisid", prr_thisid);
			//禁用之后的业务处理
			handlerService.handler("ProdRelation", "banned", "after", new Object[]{prrid});
		}		
		return "禁用成功";
	} 
}