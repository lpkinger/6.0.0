package com.uas.erp.service.sys.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.sys.AlertInstanceService;

import net.sf.json.JSONObject;

@Service
public class AlertInstanceServiceImpl implements AlertInstanceService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void save(String caller, String baseFormStore, String paramFormStore, String assignGridRecord) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(baseFormStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
				
		Map<Object, Object> map1 = BaseUtil.parseFormStoreToMap(baseFormStore);
		int count = baseDao.getCountByCondition("ALERT_ITEM_INSTANCE", "aii_code='"+map1.get("aii_code")+"'");
		if(count > 0){
			BaseUtil.showError("编号\""+map1.get("aii_code")+"\"已被占用");
		}
		Object code = baseDao.getFieldDataByCondition("ALERT_ITEM", "ai_number", "ai_id="+map1.get("aii_itemid"));
		if(code != null && !map1.get("aii_code").toString().startsWith(code.toString())) {
			BaseUtil.showError("编号\""+map1.get("aii_code")+"\"为正确以\""+code.toString()+"\"开头");
		}
		String formSql1 = SqlUtil.getInsertSqlByFormStore(map1, "ALERT_ITEM_INSTANCE", new String[]{}, new Object[]{});
		baseDao.execute(formSql1);
		
		Map<Object, Object> map2 = BaseUtil.parseFormStoreToMap(paramFormStore);
		Set<Object> keys = map2.keySet() ;// 得到全部的key
		Iterator<Object> iter = keys.iterator() ;
		while(iter.hasNext()){
			String str = iter.next().toString();
			String formSql2 = "INSERT into ALERT_INSTANCE_VAR (AIV_ID,AIV_AIIID,AIV_NAME,AIV_VALUE) VALUES ("+baseDao.getSeqId("ALERT_INSTANCE_VAR_SEQ")+",'"+map1.get("aii_id")+"','"+str+"','"+map2.get(str)+"')";
			baseDao.execute(formSql2);
		};
		
		List<Map<Object, Object>> assign = BaseUtil.parseGridStoreToMaps(assignGridRecord);
		if(assign.size() > 0) {
			for(Map<Object, Object> map : assign){
				map.put("aia_id", baseDao.getSeqId("ALERT_INSTANCE_ASSIGN_SEQ"));
				map.put("aia_aiiid", map1.get("aii_id"));
			}
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(assign, "ALERT_INSTANCE_ASSIGN");
		baseDao.execute(gridSql);
		
		// 记录操作
		baseDao.logger.save(caller, "aii_id", store.get("aii_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}
	@Override
	public void update(String caller, String baseFormStore, String paramFormStore, String assignGridRecord) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(baseFormStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		
		Map<Object, Object> map1 = BaseUtil.parseFormStoreToMap(baseFormStore);
		int count = baseDao.getCountByCondition("ALERT_ITEM_INSTANCE", "aii_code='"+map1.get("aii_code")+"' and aii_id<>"+map1.get("aii_id")+"");
		if(count > 0){
			BaseUtil.showError("编号\""+map1.get("aii_code")+"\"已被占用");
		}
		Object code = baseDao.getFieldDataByCondition("ALERT_ITEM", "ai_number", "ai_id="+map1.get("aii_itemid"));
		if(code != null && !map1.get("aii_code").toString().startsWith(code.toString())) {
			BaseUtil.showError("编号\""+map1.get("aii_code")+"\"未正确以\""+code.toString()+"\"开头");
		}
		String formSql1 = SqlUtil.getUpdateSqlByFormStore(map1, "ALERT_ITEM_INSTANCE", "aii_id");
		baseDao.execute(formSql1);
		
		// 先删除原有的参数
		baseDao.execute("delete from ALERT_INSTANCE_VAR where AIV_AIIID = "+map1.get("aii_id"));
		
		Map<Object, Object> map2 = BaseUtil.parseFormStoreToMap(paramFormStore);
		Set<Object> keys = map2.keySet() ;// 得到全部的key
		Iterator<Object> iter = keys.iterator() ;
		while(iter.hasNext()){
			String str = iter.next().toString();
			String formSql2 = "INSERT into ALERT_INSTANCE_VAR (AIV_ID,AIV_AIIID,AIV_NAME,AIV_VALUE) VALUES ("+baseDao.getSeqId("ALERT_INSTANCE_VAR_SEQ")+",'"+map1.get("aii_id")+"','"+str+"','"+map2.get(str)+"')";
			baseDao.execute(formSql2);
		};
		
		List<Map<Object, Object>> assign = BaseUtil.parseGridStoreToMaps(assignGridRecord);
		if(assign.size() > 0) {
			List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(assign, "ALERT_INSTANCE_ASSIGN", "aia_id");
			System.out.println(gridSql);
			for(Map<Object, Object> map : assign){
				Object aaid = map.get("aia_id");
				if(aaid == null || aaid.equals("") || aaid.equals("0") || Integer.parseInt(aaid.toString()) == 0){
					map.put("aia_aiiid", map1.get("aii_id"));
					map.put("aia_id", baseDao.getSeqId("ALERT_INSTANCE_ASSIGN_SEQ"));
					baseDao.execute(SqlUtil.getInsertSql(map, "ALERT_INSTANCE_ASSIGN", "aia_id"));
				}
			}
			baseDao.execute(gridSql);
		}
		
		// 记录操作
		baseDao.logger.update(caller, "aii_id", store.get("aii_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}
	@Override
	public void submit(String caller, int id) {
		// 只能对状态为[在录入]的单据进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ALERT_ITEM_INSTANCE", "aii_statuscode", "aii_id=" + id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller,new Object[] { id });
		// 执行提交操作
		baseDao.submit("ALERT_ITEM_INSTANCE", "aii_id=" + id, "aii_status", "aii_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "aii_id", id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller,new Object[] { id });
	}
	@Override
	public void resSubmit(String caller, int id) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ALERT_ITEM_INSTANCE", "aii_statuscode", "aii_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行提交前的其它逻辑
		handlerService.beforeResSubmit(caller,new Object[] { id });
		//执行反提交操作
		baseDao.resOperate("ALERT_ITEM_INSTANCE", "aii_id=" + id, "aii_status", "aii_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "aii_id", id);
		// 执行提交后的其它逻辑
		handlerService.afterResSubmit(caller,new Object[] { id });
	}
	@Override
	public void audit(String caller, int id) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ALERT_ITEM_INSTANCE", "aii_statuscode", "aii_id=" + id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { id });
		// 生成条件sql
		String[] fields = {"aa_field","aa_dbfind","aiv_value","aa_type","aa_values"};
		List<JSONObject> datas = baseDao.getFieldsJSONDatasByCondition("ALERT_ITEM_INSTANCE left join ALERT_INSTANCE_VAR on AIV_AIIID=AII_ID left join alert_args on aii_itemid=aa_aiid and aiv_name=aa_field", fields , "aii_id="+id+" and aa_field is not null and aiv_value is not null");
		StringBuffer condition = new StringBuffer();
		for(int o = 0; o< datas.size(); o++) {
			JSONObject data = datas.get(o);
			String field = data.containsKey("aa_field")?data.getString("aa_field"):"";
			String type = data.containsKey("aa_type")?data.getString("aa_type"):"";
			String con = data.containsKey("aa_dbfind")?data.getString("aa_dbfind"):"";
			String value = data.containsKey("aiv_value")?data.getString("aiv_value"):"";
			String values = data.containsKey("aa_values")?data.getString("aa_values"):"";
			StringBuffer c = new StringBuffer();
			if(type.equals("S")) {
				if(con.equals("vague")) {// 包含
					c.append("(instr("+field+",''"+value+"'')>0)");
				}else if(con.equals("novague")) {// 不包含
					c.append("(instr("+field+",''"+value+"'')=0 or "+field+" is null)");
				}else if(con.equals("head")) {// 开头是
					c.append("(instr("+field+",''"+value+"'')=1)");
				}else if(con.equals("end")) {// 结尾是
					c.append("(instr("+field+",''"+value+"'',-1,1)=LENGTH("+field+")-length(''"+value+"'')+1 and LENGTH("+field+")>=length(''"+value+"''))");
				}else if(con.equals("nodirect")) {// 不等于
					c.append("("+field+"!=''"+value+"'' or "+field+" is null) ");
				}else { // 否则同  direct 等于条件
					c.append("("+field+"=''"+value+"'')");
				}
			}else if(type.equals("D")) {
				if(con.isEmpty()) {
					con = "=";
				}
				c.append("(to_char("+field+",''yyyy-MM-dd'')"+con+"''"+value+"'')");
			}else if(type.equals("CBG")) {
				if(con.isEmpty()) {
					con = "in";
				}
				String[] ss = values.split(";");
				
				String[] varr = value.replace("[", "").replace("]", "").replace(" ", "").split(",");
			    TreeSet<String> hset = new TreeSet<String>(Arrays.asList(varr)); // 用TreeSet去重
			    Iterator<String> i = hset.iterator();
			    String p = "";
			    while(i.hasNext()){
			    	String val = i.next();
			    	boolean flag = false;
			    	if(val.equals("0")) {
			    		for(int x=0;x<ss.length;x++) {
			    			// CBG不勾选时数据为0，去除不勾选的数据并保留人为设置inputValue为0的数据
			    			if(ss[x].split(":")[1].trim().equals("0")) {
			    				flag = true;
			    				break;
			    			}
			    		}
			    	}else {
			    		flag = true;
			    	}
			    	if(flag) {
		    			p += ("''");
						p += (val);
						p += ("''");
						p += (",");
		    		}
			    }
			    if(!p.equals("")){
			    	c.append("("+field+" "+con+" ("+p.substring(0, p.lastIndexOf(","))+")"+")");
			    }
			}else if(type.equals("N") || type.equals("YN") || type.equals("C") || type.equals("R")) {
				if(con.isEmpty()) {
					con = "=";
				}
				c.append("("+field+" "+con+" ''"+value+"'')");
			}else  {
				c.append("1=1");
			}
			if(!c.toString().isEmpty()) {
				if(o!=0)condition.append(" and ");
				condition.append(c);
			}
		}
		String sql = "update ALERT_ITEM_INSTANCE set AII_CONDITION = '"+condition+"' where aii_id = "+id;
		baseDao.execute(sql);
		//执行审核操作
		baseDao.audit("ALERT_ITEM_INSTANCE", "aii_id=" + id, "aii_status", "aii_statuscode", "aii_auditdate", "aii_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "aii_id", id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { id });
	}
	@Override
	public void resAudit(String caller, int id) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ALERT_ITEM_INSTANCE", "aii_statuscode", "aii_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核前的其他逻辑
		handlerService.beforeResAudit(caller, new Object[] { id });
		String sql = "update ALERT_ITEM_INSTANCE set AII_CONDITION = null where aii_id = "+id;
		baseDao.execute(sql);
		// 执行反审核操作
		baseDao.resAudit("ALERT_ITEM_INSTANCE", "aii_id=" + id, "aii_status", "aii_statuscode", "aii_auditdate", "aii_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "aii_id", id);
		//执行反审核后的其他逻辑
		handlerService.afterResAudit(caller, new Object[] { id });
	}
	@Override
	public void delete(String caller, int id) {
		// 只能对状态为[在录入]的订单进行删除操作!
		Object status = baseDao.getFieldDataByCondition("ALERT_ITEM_INSTANCE", "aii_statuscode", "aii_id=" + id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[] { id});
		//执行删除操作
		baseDao.deleteById("ALERT_INSTANCE_VAR", "aiv_aiiid", id);
		baseDao.deleteById("ALERT_INSTANCE_ASSIGN", "aia_aiiid", id);
		baseDao.deleteById("ALERT_ITEM_INSTANCE", "aii_id", id);
		// 记录操作
		baseDao.logger.delete(caller, "aii_id", id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[] { id});
	}
	@Override
	public void banned(String caller, int id) {
		// 只能对状态为[已审核]的订单进行操作!
		Object status = baseDao.getFieldDataByCondition("ALERT_ITEM_INSTANCE", "aii_statuscode", "aii_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		//执行启用操作前的其他逻辑
		handlerService.handler(caller, "banned", "before", new Object[] { id });
		baseDao.updateByCondition("ALERT_ITEM_INSTANCE", "aii_enable = 0", "aii_id=" + id);
		//记录操作
		baseDao.logger.others("禁用", "禁用成功", caller, "aii_id", id);
		//执行启用操作后的其他逻辑
		handlerService.handler(caller, "banned", "after", new Object[] { id });
	}
	@Override
	public void resBanned(String caller, int id) {
		// 只能对状态为[已审核]的订单进行操作!
		Object status = baseDao.getFieldDataByCondition("ALERT_ITEM_INSTANCE", "aii_statuscode", "aii_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		//执行启用操作前的其他逻辑
		handlerService.handler(caller, "resBanned", "before", new Object[] { id });
		baseDao.updateByCondition("ALERT_ITEM_INSTANCE", "aii_enable = -1", "aii_id=" + id);
		//记录操作
		baseDao.logger.others("启用", "启用成功", caller, "aii_id", id);
		//执行启用操作后的其他逻辑
		handlerService.handler(caller, "resBanned", "after", new Object[] { id });
	}
	@Override
	public List<JSONObject> getParamItems(String itemId,String instanceId) {
		String[] fields1 = {"aa_id","aa_code","aa_desc","aa_type","aa_dbfind","aa_detno","aa_aiid","aa_field","aa_values","aa_valuessql","aa_width"};
		List<JSONObject> datas1 = new ArrayList<JSONObject>();
		if(itemId != null && !itemId.isEmpty()) {
			datas1 = baseDao.getFieldsJSONDatasByCondition("ALERT_ARGS",fields1, "aa_aiid="+itemId+" order by aa_detno ASC");
		}else if(instanceId != null && !instanceId.isEmpty()) {
			String[] fields2 = {"aii_statuscode","aa_id","aa_code","aa_desc","aa_type","aa_dbfind","aa_detno","aa_aiid","aa_field","aa_values","aa_valuessql","aa_width","aiv_value"};
			datas1 = baseDao.getFieldsJSONDatasByCondition("alert_item_instance left join alert_args on aa_aiid=aii_itemid left join alert_instance_var on aiv_aiiid=aii_id and aa_field=aiv_name",fields2, "aii_id="+instanceId+" and aa_id is not null order by aa_detno ASC");
		}

		return datas1;
	}
	@Override
	public List<JSONObject> getAssign(String instanceId) {
		String[] fields1 = {"aia_id", "aia_aiiid", "aia_detno", "aia_mans", "aia_condition", "aia_mansql", "aia_mancode"};
		List<JSONObject> datas1 = new ArrayList<JSONObject>();
		datas1 = baseDao.getFieldsJSONDatasByCondition("ALERT_INSTANCE_ASSIGN",fields1, "aia_aiiid="+instanceId);
		return datas1;
	}
	@Override
	public List<JSONObject> getOutputParams(String itemId) {
		String[] fields1 = {"ao_id", "ao_resultname", "ao_resultdesc", "ao_resulttype", "ao_aiid", "ao_detno"};
		List<JSONObject> datas1 = new ArrayList<JSONObject>();
		datas1 = baseDao.getFieldsJSONDatasByCondition("ALERT_OUTPUT",fields1, "ao_aiid="+itemId);
		return datas1;
	}
	
}
