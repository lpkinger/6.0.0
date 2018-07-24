package com.uas.erp.service.hr.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.hr.DepartmentnewService;

@Service
public class DepartmentnewServiceImpl implements DepartmentnewService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveDepartment(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);		
		handlerService.beforeSave(caller, new Object[]{store});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Department", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		baseDao.execute("update Department A set dp_level=(nvl((select dp_level from Department B where A.dp_subof=B.dp_id),0) + 1) where dp_id="+ store.get("dp_id"));
		try{
			//记录操作
			baseDao.logger.save(caller, "dp_id", store.get("dp_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void updateDepartmentById(String formStore, String caller) {		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		//修改
		/**限制当前编号存在其他组织或人事资料中不允许删除*/
		Object dpid=store.get("dp_id");
		Object dpcode=baseDao.getFieldValue("department", "dp_code", "dp_id='"+dpid+"'", String.class);
		if(dpcode!=null && !dpcode.equals(store.get("dp_code"))){
			boolean bool=baseDao.checkIf("hrorg", "nvl(or_departmentcode,' ')=(select dp_code from department where dp_id="+dpid+")");
			if(bool) BaseUtil.showError("当前核算部门关联有其他组织信息，不允许修改编号!");
		}	
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Department", "dp_id");
		baseDao.execute(formSql);
		baseDao.execute("update Department A set dp_level=(nvl((select dp_level from Department B where A.dp_subof=B.dp_id),0) + 1) where dp_id="+ store.get("dp_id"));
		//记录操作
		baseDao.logger.update(caller, "dp_id", store.get("dp_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
	}

	@Override
	public void deleteDepartment(int dp_id, String caller) {
		handlerService.beforeDel(caller,  new Object[]{dp_id});
		int count = baseDao.getCount("select count(*) from Department where dp_subof=" + dp_id);
		if(count > 0){
			BaseUtil.showError("该部门有下级核算部门，不允许删除！");
		}
		boolean bool=baseDao.checkIf("hrorg","nvl(or_departmentcode,' ')=(select  dp_code from department where dp_id="+dp_id+")");
		if(bool) BaseUtil.showError("当前核算部门存在关联组织，不允许删除!");
		baseDao.delCheck("Department", dp_id);
		//
		Object dp_subof = baseDao.getFieldDataByCondition("Department", "dp_subof", "dp_id=" + dp_id);
		//删除
		baseDao.deleteById("Department", "dp_id", dp_id);
		/*此部门的父级部门没有下级部门时，更新是否末级为是(-1)
		 * */
		if(!"0".equals(dp_subof.toString())){
			int count1 = baseDao.getCount("select count(*) from Department where dp_subof=" + dp_subof);
			if(count1 == 0){
				baseDao.updateByCondition("department","dp_isleaf=-1", "dp_id="+dp_subof);
			}
		}
		//记录操作
		baseDao.logger.delete(caller, "dp_id", dp_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller,  new Object[]{dp_id});
	}

	@Override
	public List<JSONObject> getDepartments() {
		SqlRowList rs = baseDao.queryForRowSet("SELECT dp_id,dp_code,dp_name FROM Department order by dp_id asc");
		List<JSONObject> list = new ArrayList<JSONObject>();
		JSONObject obj = null;
		while(rs.next()) {
			obj = new JSONObject();
			obj.put("dp_id", rs.getObject(1));
			obj.put("dp_code", rs.getObject(2));
			obj.put("dp_name", rs.getObject(3));
			list.add(obj);
		}
		return list;
	}

	@Override
	public void resAuditDepartment(int dp_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Department", "dp_statuscode", "dp_id=" + dp_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核前的其它逻辑
		handlerService.handler(caller, "resAudit", "before", new Object[] { dp_id });
		// 执行反审核操作
		baseDao.resOperate("Department", "dp_id=" + dp_id, "dp_status", "dp_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "dp_id", dp_id);
		// 执行反审核后的其它逻辑
		handlerService.handler(caller, "resAudit", "after", new Object[] { dp_id });
	}

	@Override
	public void auditDepartment(int id, String caller) {
		Object[] datas=baseDao.getFieldsDataByCondition("Department",new String[]{"dp_statuscode","dp_name","dp_code","dp_subof"}, "dp_id="+id);
		StateAssert.auditOnlyCommited(datas[0]);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { id });
		baseDao.getFieldValue("department","dp_name","dp_id="+id,String.class);
		baseDao.updateByCondition("hrorg","Or_Department='"+datas[1]+"'", "nvl(or_departmentcode,' ')='"+datas[2]+"'");
		baseDao.updateByCondition("employee","em_depart='"+datas[1]+"'", "nvl(em_departmentcode,' ')='"+datas[2]+"'");
		baseDao.updateByCondition("department","dp_parentdpname='"+datas[1]+"'", "nvl(dp_subof,0)='"+id+"'");
		/*
		 * 此部门有父级部门时，将父级部门是否末级改为否（0）
		 */
		if(datas[3]!=null&&!"0".equals(datas[3].toString())){
			baseDao.updateByCondition("department","dp_isleaf=0", "dp_id="+datas[3]);
		}
		// 执行审核操作
		baseDao.audit("Department", "dp_id=" + id, "dp_status", "dp_statuscode");
		// 记录操作
		baseDao.logger.audit(caller, "dp_id", id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { id });
	}
}
