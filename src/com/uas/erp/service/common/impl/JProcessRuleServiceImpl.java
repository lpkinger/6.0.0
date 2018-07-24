package com.uas.erp.service.common.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.common.JProcessRuleService;

@Service
public class JProcessRuleServiceImpl implements JProcessRuleService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveJProcessRule(String caller,String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		checkCallerAndRuleName(store);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { formStore });
		// 执行保存操作
		String sql = SqlUtil.getInsertSqlByMap(store, "jprocessrule");
		baseDao.execute(sql);
		// 记录操作
		baseDao.logger.save(caller, "ru_id", store.get("ru_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
		
	}
	
	public void checkCallerAndRuleName(Map<Object, Object> store){
		String name = store.get("ru_name").toString().replace("'", "''");
		String caller= store.get("ru_caller").toString();
		Object ruid = store.get("ru_id");
		String idCon = "";
		if(ruid!=null){
			idCon = " and nvl(ru_id,0)<>" + ruid;
		}
		boolean bol = baseDao.checkIf("jprocessrule", "ru_name='" + name + "' and ru_caller='"+caller+"'" + idCon);
		if(bol){
			BaseUtil.showError("caller与规则名字必须唯一");
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateJProcessRule(String caller,String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		checkCallerAndRuleName(store);
		// 执行保存前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { formStore });
		// 执行保存操作
		String sql = SqlUtil.getUpdateSqlByFormStore(store, "jprocessrule", "ru_id");
		baseDao.execute(sql);
		// 记录操作
		baseDao.logger.update(caller, "ru_id", store.get("ru_id"));
		// 执行保存后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteJProcessRule(int id,String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[] { id});
		// 删除
		baseDao.deleteById("jprocessrule", "ru_id", id);
		// 记录操作
		baseDao.logger.delete(caller, "mr_id", id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[] { id});		
	}

	@SuppressWarnings("finally")
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Map<String, Object> checkSql(String sql) {
		Map<String, Object> map = new HashMap<String,Object>();
		Employee employee = SystemSession.getUser();
		StringBuffer sb = new StringBuffer();
		sql = sql.replace("@KEYVALUE", "0");
		sql = sql.replace("@EMID", employee.getEm_id().toString());
		sql = sql.replace("@EMCODE", "'"+employee.getEm_code()+"'");
		sql = sql.replace("@EMNAME", "'"+employee.getEm_name()+"'");	
		if(sql.endsWith(";")){
			sql = sql.substring(0,sql.length()-1);
		}
		sb.append("begin ");
		sb.append("execute immediate '").append(sql.replace("'", "''")).append("';rollback;end;");
		try{
			baseDao.execute(sb.toString());
			map.put("result",true);
		}catch(Exception e){
			map.put("result",false);
			String message = e.getCause().getMessage();
			map.put("errorInfo", message);
		}finally{
			return map;
		}
	}

}
