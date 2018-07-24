package com.uas.erp.service.hr.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hsqldb.lib.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.WageItemService;


@Service
public class WageItemServiceImpl implements WageItemService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private CacheManager cacheManager;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveWageItem(String formStore, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[] { store });
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "WageItem",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "wi_id", store.get("wi_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void updateWageItemById(String formStore, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "WageItem",
				"wi_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "wi_id", store.get("wi_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void deleteWageItem(int wi_id, String caller) {

		handlerService.beforeDel(caller, wi_id);
		// 删除
		baseDao.deleteById("WageItem", "wi_id", wi_id);
		// 记录操作
		baseDao.logger.delete(caller, "wi_id", wi_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, wi_id);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void auditWageItem(int wi_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("WageItem",
				"wi_statuscode", "wi_id=" + wi_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, wi_id);
		//判断当前字段是否被使用
		Object wi_fieldname = baseDao.getFieldDataByCondition("WageItem", "wi_fieldname", "wi_id="+wi_id);
		boolean bool = baseDao.checkByCondition("WageItem", "wi_fieldname='" + wi_fieldname + "' and  wi_statuscode='AUDITED' ");
		if (!bool) {
			BaseUtil.showError("当前字段已被使用");
		}
		
		//新增到基础项数据的明细行配置
		Object wi_name = baseDao.getFieldDataByCondition("WageItem", "wi_name", "wi_id="+wi_id);
		int newSEQUENCE = baseDao.queryForObject("select max(DG_SEQUENCE) from DETAILGRID where dg_caller='WageBase'", Integer.class)+1;
		String insertSql = "Insert into detailgrid (DG_ID,DG_CALLER,DG_SEQUENCE,DG_CAPTION,DG_WIDTH,DG_VISIBLE,DG_EDITABLE,DG_DBBUTTON,DG_FIELD,DG_GRIDENABLED,DG_FINDFUNCTIONNAME,DG_CAPTIONFAN,DG_CAPTIONEN,DG_TABLE,DG_SUMMARYTYPE,DG_ALLOWBLANK,DG_TYPE,DG_LOGICTYPE,DG_RENDERER,DG_LOCKED,DG_CHECK,DG_MINVALUE,DG_MAXLENGTH,DG_MOBILEUSED,DG_MODIFY,DG_ISFIXED,DG_APPWIDTH,DG_CONCATFIELD) values (detailgrid_seq.nextval,'WageBase',"+newSEQUENCE+",'"+wi_name+"',80,-1,-1,0,'"+wi_fieldname+"',null,null,null,null,'WAGEBASEDETAIL',null,'T','numbercolumn',null,null,0,0,null,null,0,'F',0,0,null)";			
		baseDao.execute(insertSql);
	
		//新增到查询界面配置
		List<Integer> ids = baseDao.queryForList("select st_id from searchtemplate where ST_CALLER='WageReport'", Integer.class);
		for (Integer id : ids) {
			//获得新序号
			int newdetno=baseDao.queryForObject("select max(STG_DETNO) from searchtemplategrid where stg_stid="+id, Integer.class)+1;
			String insertSql2="Insert into SEARCHTEMPLATEGRID (STG_STID,STG_DETNO,STG_FIELD,STG_OPERATOR,STG_VALUE,STG_LOCK,STG_GROUP,STG_SUM,STG_DBFIND,STG_DOUBLE,STG_QUERY,STG_USE,STG_TYPE,STG_TABLE,STG_WIDTH,STG_TEXT,STG_FORMAT,STG_MODE,STG_LINK,STG_TOKENTAB1,STG_TOKENCOL1,STG_TOKENTAB2,STG_TOKENCOL2,STG_FORMULA,STG_ID,STG_APPCONDITION,STG_APPUSE) values "
					+ "("+id+","+newdetno+",'"+wi_fieldname+"',null,null,0,0,0,0,0,0,1,'NUMBER','WAGEREPORT',100,'"+wi_name+"',null,null,null,null,null,null,null,null,0,0,1)";
			baseDao.execute(insertSql2);
		}
		
		// 执行审核操作
		baseDao.updateByCondition(
				"WageItem",
				"wi_statuscode='AUDITED',wi_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',wi_auditer='"
						+ SystemSession.getUser().getEm_name()
						+ "',wi_auditdate=sysdate", "wi_id=" + wi_id);
		// 记录操作
		baseDao.logger.audit(caller, "wi_id", wi_id);
		//清除缓存
		removeCache();
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, wi_id);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void resAuditWageItem(int wi_id, String caller) {

		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("WageItem",
				"wi_statuscode", "wi_id=" + wi_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, wi_id);
	
		//删除到基础项数据的明细行配置
		Object wi_fieldname = baseDao.getFieldDataByCondition("WageItem", "wi_fieldname", "wi_id="+wi_id);
		baseDao.deleteByCondition("detailgrid", "DG_FIELD='"+wi_fieldname+"'  and DG_CALLER= 'WageBase'");		
		//删除查询界面的配置
		List<Integer> ids = baseDao.queryForList("select st_id from searchtemplate where ST_CALLER='WageReport'", Integer.class);
		for (Integer id : ids) {
			baseDao.deleteByCondition("SEARCHTEMPLATEGRID", "STG_STID="+id+" and  STG_FIELD='"+wi_fieldname+"'  and stg_table= 'WAGEREPORT'");					
		}
		
		// 执行反审核操作
		baseDao.updateByCondition(
				"WageItem",
				"wi_statuscode='ENTERING',wi_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',wi_auditer='',wi_auditdate=null", "wi_id=" + wi_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "wi_id", wi_id);
		//清除缓存
		removeCache();		
		handlerService.afterResAudit(caller, wi_id);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void submitWageItem(int wi_id, String caller) {

		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("WageItem",
				"wi_statuscode", "wi_id=" + wi_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, wi_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"WageItem",
				"wi_statuscode='COMMITED',wi_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "wi_id="
						+ wi_id);
		// 记录操作
		baseDao.logger.submit(caller, "wi_id", wi_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, wi_id);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void resSubmitWageItem(int wi_id, String caller) {

		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("WageItem",
				"wi_statuscode", "wi_id=" + wi_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, wi_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"WageItem",
				"wi_statuscode='ENTERING',wi_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "wi_id="
						+ wi_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "wi_id", wi_id);
		handlerService.afterResSubmit(caller, wi_id);
	}

	@Override
	public List<Map<String, Object>> getWageItems() {
		List<Map<String, Object>> list = baseDao.queryForList("select wi_name,WI_FIELDNAME from wageitem where WI_STATUSCODE='AUDITED'");
		return list;
	}

	/**
	 * 清除系统缓存
	 */
	public void removeCache() {
		// 约定cache名称格式统一是masterName.cacheRealName
		Collection<String> cacheNames = null;
		cacheNames = cacheManager.getCacheNames();
		Iterator<String> iterator = cacheNames.iterator();
		while (iterator.hasNext())
			cacheManager.getCache(iterator.next()).clear();
	}	
	
	
}
