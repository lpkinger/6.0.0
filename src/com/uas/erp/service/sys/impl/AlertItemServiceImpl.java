package com.uas.erp.service.sys.impl;

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
import com.uas.erp.model.Employee;
import com.uas.erp.service.sys.AlertItemService;

@Service
public class AlertItemServiceImpl implements AlertItemService{

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void save(String caller, String formStore, String params1, String params2) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object ai_title = store.get("ai_title");
		if(ai_title==null||"".equals(ai_title)){
			BaseUtil.showError("预警项目名称不允许为空");
		}
		List<Map<Object, Object>> alertArgs = BaseUtil.parseGridStoreToMaps(params1);
		List<Map<Object, Object>> alertOutput = BaseUtil.parseGridStoreToMaps(params2);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ALERT_ITEM", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		//保存alertArgs项目参数
		for(Map<Object, Object> m:alertArgs){
			m.put("aa_id", baseDao.getSeqId("ALERT_ARGS_SEQ"));
		}
		List<String> alertArgsSql = SqlUtil.getInsertSqlbyGridStore(alertArgs, "ALERT_ARGS");
		baseDao.execute(alertArgsSql);
		//保存alertOutput输出结果定义
		for(Map<Object, Object> m:alertOutput){
			m.put("ao_id", baseDao.getSeqId("ALERT_ARGS_SEQ"));
		}
		List<String> alertOutputSql = SqlUtil.getInsertSqlbyGridStore(alertOutput, "ALERT_OUTPUT");
		baseDao.execute(alertOutputSql);
		// 记录操作
		baseDao.logger.save(caller, "ai_id", store.get("ai_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void update(String caller,String formStore,String params1,String params2) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object ai_title = store.get("ai_title");
		if(ai_title==null||"".equals(ai_title)){
			BaseUtil.showError("预警项目名称不允许为空");
		}
		List<Map<Object, Object>> alertArgs = BaseUtil.parseGridStoreToMaps(params1);
		List<Map<Object, Object>> alertOutput = BaseUtil.parseGridStoreToMaps(params2);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ALERT_ITEM", "ai_id");
		baseDao.execute(formSql);
		//修改alertArgs项目参数
		List<String> alertArgsSql = SqlUtil.getUpdateSqlbyGridStore(alertArgs, "ALERT_ARGS", "aa_id");
		for(Map<Object, Object> map : alertArgs){
			Object aaid = map.get("aa_id");
			if(aaid == null || aaid.equals("") || aaid.equals("0") || Integer.parseInt(aaid.toString()) == 0){
				baseDao.execute(SqlUtil.getInsertSql(map, "ALERT_ARGS", "aa_id"));
			}
		}
		baseDao.execute(alertArgsSql);
		//修改alertArgs项目参数
		List<String> alertOutputSql = SqlUtil.getUpdateSqlbyGridStore(alertOutput, "ALERT_OUTPUT", "ao_id");
		for(Map<Object, Object> map : alertOutput){
			Object aoid = map.get("ao_id");
			if(aoid == null || aoid.equals("") || aoid.equals("0") || Integer.parseInt(aoid.toString()) == 0){
				baseDao.execute(SqlUtil.getInsertSql(map, "ALERT_OUTPUT", "ao_id"));
			}
		}
		baseDao.execute(alertOutputSql);
		// 记录操作
		baseDao.logger.update(caller, "ai_id", store.get("ai_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void submit(int id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ALERT_ITEM", "ai_statuscode", "ai_id=" + id);
		StateAssert.submitOnlyEntering(status);
		//判断执行语句以及参数设置是否规范
		checkSql(id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller,new Object[] { id });
		// 执行提交操作
		baseDao.submit("ALERT_ITEM", "ai_id=" + id, "ai_status", "ai_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "ai_id", id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller,new Object[] { id });
	}

	/**
	 * 判断执行语句以及参数设置是否规范
	 * @param id
	 */
	private void checkSql(int id){
		Object ai_alertsql = baseDao.getFieldDataByCondition("ALERT_ITEM", "ai_alertsql", "ai_id=" + id);
		if(ai_alertsql==null||"".equals(ai_alertsql.toString().trim())){
			BaseUtil.showError("预警项目的执行语句不能为空");
		}
		String alertsql = ai_alertsql.toString().replaceAll(";", " ");
		if(!alertsql.toUpperCase().startsWith("SELECT")){			
			BaseUtil.showError("执行语句不规范,请您设置规范的查询语句");
		}else{
			//检查sql是否正确
			if(baseDao.checkSQL(alertsql)){
				//检查项目参数设置是否规范
				checkArgs(id,alertsql);
				//检查项目参数类型是否规范
				checkArgstype(id,alertsql);
				//检查输出结果定义是否规范
				checkOutput(id,alertsql);
			}else{
				BaseUtil.showError("执行语句不规范,请您设置规范的查询语句");
			}
		}
	}
	/**
	 * 检查项目参数设置是否规范
	 * @param id 项目id
	 * @param alertsql 执行语句
	 */
	private void checkArgs(int id,String alertsql){
		List<Object> argsList = baseDao.getFieldDatasByCondition("ALERT_ARGS", "aa_field", "aa_aiid=" + id);
		int len = argsList.size();
		//没有设置参数的,直接返回
		if(len == 0){
			return;
		}
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		for(int i = 0 ; i < len ; i++){
			sql.append(argsList.get(i));
			if(i<len-1){
				sql.append(",");
			}
		}
		sql.append(" from (");
		sql.append(alertsql);
		sql.append(")");
		if(!baseDao.checkSQL(sql.toString())){
			BaseUtil.showError("项目参数不规范,请您设置执行语句中存在的参数");
		}
		//TODO:检查项目参数类型是否匹配。  select count(*) from (alertsql) where aa_field = aa_type'
		
	}
	/**
	 * 检查项目参数类型是否规范
	 * @param id 项目id
	 * @param alertsql 执行语句
	 */
	private void checkArgstype(int id, String alertsql){
		List<Object[]> argsList = baseDao.getFieldsDatasByCondition("ALERT_ARGS", new String[]{"aa_field","aa_type","aa_detno","aa_dbfind"}, "aa_aiid=" + id);
		int len = argsList.size();
		//没有设置参数的,直接返回
		if(len == 0){
			return;
		}
		for(int i = 0 ; i < len ; i++){
			StringBuffer sql = new StringBuffer();
			sql.append("select count(*) from (");
			sql.append(alertsql);
			sql.append(") where ");
			Object[] args = argsList.get(i);
			if("N".equals(args[1])){
				sql.append(args[0]);
				sql.append("=1");
			}else if("D".equals(args[1])){
				sql.append(args[0]);
				sql.append("=sysdate");
			}else if("YN".equals(args[1])){
				sql.append(args[0]);
				sql.append("='-1'");
			}else if ("SQL".equals(args[1])){
				continue;
			}else{
				sql.append(args[0]);
				sql.append("='usoft'");
			}
			if(!baseDao.checkSQL(sql.toString())){
				BaseUtil.showError("行号: "+args[2]+" 的项目参数类型不规范,请您设置对应的参数类型");
			}
		}
	}
	/**
	 * 检查输出结果定义是否规范
	 * @param id 项目id
	 * @param alertsql 执行语句
	 */
	private void checkOutput(int id, String alertsql){
		List<Object> outputList = baseDao.getFieldDatasByCondition("ALERT_OUTPUT", "ao_resultname", "ao_aiid=" + id);
		int len = outputList.size();
		//没有设置参数的,直接返回
		if(len == 0){
			return;
		}
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		for(int i = 0 ; i < len ; i++){
			sql.append(outputList.get(i));
			if(i<len-1){
				sql.append(",");
			}
		}
		sql.append(" from (");
		sql.append(alertsql);
		sql.append(")");
		if(!baseDao.checkSQL(sql.toString())){
			BaseUtil.showError("输出结果定义不规范,请您设置执行语句中存在的参数");
		}
	}
	@Override
	public void resSubmit(int id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ALERT_ITEM", "ai_statuscode", "ai_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行提交前的其它逻辑
		handlerService.beforeResSubmit(caller,new Object[] { id });
		//执行反提交操作
		baseDao.resOperate("ALERT_ITEM", "ai_id=" + id, "ai_status", "ai_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "ai_id", id);
		// 执行提交后的其它逻辑
		handlerService.afterResSubmit(caller,new Object[] { id });
	}

	@Override
	public void audit(int id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ALERT_ITEM", "ai_statuscode", "ai_id=" + id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { id });
		//执行审核操作
		baseDao.audit("ALERT_ITEM", "ai_id=" + id, "ai_status", "ai_statuscode", "ai_auditdate", "ai_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "ai_id", id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { id });
	}

	@Override
	public void resAudit(int id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ALERT_ITEM", "ai_statuscode", "ai_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		//已经存在项目项次的限制反审核
		Object ai_code = baseDao.getFieldDataByCondition("ALERT_ITEM", "ai_code", "ai_id=" + id);
		if(!baseDao.checkByCondition("ALERT_ITEM_INSTANCE", "aii_itemcode='"+ai_code+"'")){
			BaseUtil.showError("该预警项目存在预警项次，限制反审核");
		}
		//执行反审核前的其他逻辑
		handlerService.beforeResAudit(caller, new Object[] { id });
		// 执行反审核操作
		baseDao.resAudit("ALERT_ITEM", "ai_id=" + id, "ai_status", "ai_statuscode", "ai_auditdate", "ai_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "ai_id", id);
		//执行反审核后的其他逻辑
		handlerService.afterResAudit(caller, new Object[] { id });
	}

	@Override
	public void delete(int id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ALERT_ITEM", "ai_statuscode", "ai_id=" + id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[] { id});
		//执行删除操作
		baseDao.deleteById("ALERT_ARGS", "aa_aiid", id);
		baseDao.deleteById("ALERT_OUTPUT", "ao_aiid", id);
		baseDao.deleteById("ALERT_ITEM", "ai_id", id);
		// 记录操作
		baseDao.logger.delete(caller, "ai_id", id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[] { id});
	}
	
	@Override
	public void banned(int id, String caller) {
		//执行启用操作前的其他逻辑
		handlerService.handler(caller, "banned", "before", new Object[] { id });
		baseDao.updateByCondition("ALERT_ITEM", "ai_enable = 0", "ai_id=" + id);
		//记录操作
		baseDao.logger.others("禁用", "禁用成功", caller, "ai_id", id);
		//执行启用操作后的其他逻辑
		handlerService.handler(caller, "banned", "after", new Object[] { id });
	}

	@Override
	public void resBanned(int id, String caller) {
		//执行启用操作前的其他逻辑
		handlerService.handler(caller, "resBanned", "before", new Object[] { id });
		baseDao.updateByCondition("ALERT_ITEM", "ai_enable = -1", "ai_id=" + id);
		//记录操作
		baseDao.logger.others("反禁用", "反禁用成功", caller, "ai_id", id);
		//执行启用操作后的其他逻辑
		handlerService.handler(caller, "resBanned", "after", new Object[] { id });
	}

}
