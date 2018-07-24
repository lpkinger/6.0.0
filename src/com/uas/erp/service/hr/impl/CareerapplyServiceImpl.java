package com.uas.erp.service.hr.impl;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.PasswordEncryUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.hr.CareerapplyService;

@Service
public class CareerapplyServiceImpl implements CareerapplyService {
	
	
	static final String turnEmployee  ="insert into employee(em_name,em_sex,em_defaultorname,em_defaultorcode,em_defaultorid,"
			+ "em_position,em_defaulthscode,em_defaulthsid,em_depart,em_class,em_id,em_code,em_departmentcode)values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String update ="update Careerapply set ca_isturn='1' where ca_id=?";
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCareerapply(String formStore, String gridStore,String caller) {		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller,  new Object[]{store,gstore});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Careerapply", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//保存CareerapplyDetail
		Object[] cd_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			cd_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				cd_id[i] = baseDao.getSeqId("CareerapplyDETAIL_SEQ");
			}
		} else {
			cd_id[0] = baseDao.getSeqId("CareerapplyDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "CareerapplyDetail", "cd_id", cd_id);
		baseDao.execute(gridSql);
		checkEmCode(store.get("ca_id"));
		//记录操作
		baseDao.logger.save(caller, "ca_id", store.get("ca_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller,  new Object[]{store,gstore});
	}
	
	private void checkEmCode(Object ca_id) {
		// 判断明细行员工编号在人员资料中是否存在重复
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(cd_emcode) from CareerapplyDetail where cd_caid=? and nvl(cd_emcode,' ')<>' ' and exists (select em_code from employee where em_code=cd_emcode)", String.class, ca_id);
		if (dets != null) {
			BaseUtil.showError("明细行员工编号在人员资料中已存在!员工编号："+dets);
		}
	}

	@Override
	public void updateCareerapplyById(String formStore,String gridStore, String caller) {		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[]{store,gstore});
		//修改Careerapply
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Careerapply", "ca_id");
		baseDao.execute(formSql);
		//修改CareerapplyDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "CareerapplyDetail", "cd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("cd_id") == null || s.get("cd_id").equals("") || s.get("cd_id").toString().equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("CareerapplyDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "CareerapplyDetail", new String[]{"cd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		checkEmCode(store.get("ca_id"));
		//记录操作
		baseDao.logger.update(caller, "ca_id", store.get("ca_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store,gstore});

	}

	@Override
	public void deleteCareerapply(int ca_id, String  caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller,ca_id);
		baseDao.execute("update recuitinfo set re_enroll='否',re_isincaree='0' where re_id in (select cd_sourceid from Careerapplydetail where cd_caid="+ca_id+")");
		//删除purchase
		baseDao.deleteById("Careerapply", "ca_id", ca_id);
		//删除purchaseDetail
		baseDao.deleteById("Careerapplydetail", "cd_caid", ca_id);
		//记录操作
		baseDao.logger.delete(caller, "ca_id", ca_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller,ca_id);
	}

	@Override
	public void auditCareerapply(int ca_id, String  caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Careerapply", "ca_statuscode", "ca_id=" + ca_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ca_id);
		checkEmCode(ca_id);
		//执行审核操作
		baseDao.audit("Careerapply", "ca_id=" + ca_id, "ca_status", "ca_statuscode", "ca_auditdate", "ca_auditor");
		/*String insertDetSql="insert into employee(em_name,em_sex,em_defaultorname,em_position,em_depart,em_class,em_id,em_code)" +
				"select cd_name,cd_sex,cd_hrorg,cd_position,cd_depart,'试用',Careerapplydetail_seq.nextval,cd_emcode  from Careerapplydetail where cd_caid="+ca_id;
		baseDao.execute(insertDetSql);
		baseDao.execute(update, ca_id);*/
		//记录操作
		baseDao.logger.audit(caller, "ca_id", ca_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, ca_id);
	}

	@Override
	public void resAuditCareerapply(int ca_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Careerapply", "ca_statuscode", "ca_id=" + ca_id);
		StateAssert.resAuditOnlyAudit(status);
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(cd_detno) from CareerapplyDetail where cd_caid=? and nvl(cd_isturn,0)<>0", String.class, ca_id);
		if (dets != null) {
			BaseUtil.showError("明细行员工已转试用，不允许进行反审核操作！行号："+dets);
		}
		//执行反审核操作
		baseDao.resAudit("Careerapply", "ca_id=" + ca_id, "ca_status", "ca_statuscode", "ca_auditdate", "ca_auditor");
		//记录操作
		baseDao.logger.resAudit(caller, "ca_id", ca_id);
		
	}

	@Override
	public void submitCareerapply(int ca_id, String  caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Careerapply", "ca_statuscode", "ca_id=" + ca_id);
		StateAssert.submitOnlyEntering(status);
		checkEmCode(ca_id);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ca_id);
		//执行提交操作
		baseDao.submit("Careerapply", "ca_id=" + ca_id, "ca_status", "ca_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "ca_id", ca_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ca_id);
		
	}

	@Override
	public void resSubmitCareerapply(int ca_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Careerapply", "ca_statuscode", "ca_id=" + ca_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, ca_id);
		//执行反提交操作
		baseDao.resOperate("Careerapply",  "ca_id=" + ca_id, "ca_status", "ca_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "ca_id", ca_id);
		handlerService.afterResSubmit(caller, ca_id);
		
	}

	@Override
	public void turnEmployee(String  caller, String param, int id) {
		JSONArray gridJsonArray = JSONArray.fromObject(param);
		JSONObject gridjJson = new JSONObject();
		int i=0;
		int careerId=0;
		try {
			for (i=0;i<gridJsonArray.size();i++) {
				if(careerId==0){
					careerId = gridJsonArray.getJSONObject(i).getInt("cd_caid");
				}
				gridjJson = gridJsonArray.getJSONObject(i);
				baseDao.execute(turnEmployee, new Object[]{gridjJson.getString("cd_name"),gridjJson.getString("cd_sex"),
						gridjJson.getString("cd_hrorg"),gridjJson.getString("cd_defaultorcode"),gridjJson.getString("cd_defaultorid"),
						gridjJson.getString("cd_position"),gridjJson.getString("cd_defaulthscode"),gridjJson.getString("cd_defaulthsid"),
						gridjJson.getString("cd_depart"),"试用",baseDao.getSeqId("Employee_SEQ"),gridjJson.getString("cd_emcode")});
			}
			baseDao.execute(update, new Object[]{id});
			baseDao.execute("update Careerapplydetail set cd_isturn=1 where cd_caid=?", new Object[] { id });
			baseDao.logger.getMessageLog(BaseUtil.getLocalMessage("msg.turnEmployee"), BaseUtil.getLocalMessage("msg.turnEmployeeSuccess"), caller, "ca_id", careerId);
		}
		catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public String vastTurnEmployee(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		String log = null;
		int cd_id = 0;
		int index = 0;
		int month = 0;
		int emid = 0;
		String code = null;
		if (maps.size() > 0) {
			for (Map<Object, Object> map : maps) {
				index ++;
				emid = baseDao.getSeqId("Employee_SEQ");
				code = baseDao.sGetMaxNumber("Employeemanager", 2);
				cd_id = Integer.parseInt(map.get("cd_id").toString());
				month = Integer.parseInt(map.get("ca_month").toString());
				SqlRowList rs = baseDao.queryForRowSet("select * from Careerapplydetail where cd_id=?",
						new Object[] { cd_id });
				if (rs.next()) {
					if(rs.getObject("cd_emcode") != null && !"".equals(rs.getString("cd_emcode"))){
						code = rs.getString("cd_emcode");
					}
					baseDao.execute(turnEmployee, new Object[]{rs.getString("cd_name"), rs.getString("cd_sex"),
							rs.getString("cd_hrorg"), rs.getString("cd_defaultorcode"), rs.getObject("cd_defaultorid"),
							rs.getString("cd_position"), rs.getString("cd_defaulthscode"), rs.getObject("cd_defaulthsid"), 
							rs.getString("cd_depart"), "试用", emid, code,rs.getString("cd_departcode")});
					baseDao.execute("update employee set em_indate=sysdate, em_shmonth=" + month + ",em_status='"+BaseUtil.getLocalMessage("ENTERING")+"' ,em_statuscode='ENTERING' ,em_type='normal',em_password='"+PasswordEncryUtil.encryptPassword("111111", null)+"' where em_id=" + emid);
					baseDao.execute("update Careerapplydetail set cd_emcode='"+code+"' where cd_id=? and nvl(cd_emcode,' ')=' '", new Object[] { cd_id });
					baseDao.execute("update Careerapplydetail set cd_isturn=1 where cd_id=?", new Object[] { cd_id });
					log = "转入成功,人员单号:" + "<a href=\"javascript:openUrl('jsps/hr/emplmana/employee/employee.jsp?formCondition=em_idIS" + emid
							+ "')\">" + code + "</a>&nbsp;";
					sb.append(index).append(":").append(log).append("<hr>");
				}
			}
		}
		return sb.toString();
	}
	
}
