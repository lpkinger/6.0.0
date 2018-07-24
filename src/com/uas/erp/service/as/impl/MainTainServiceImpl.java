package com.uas.erp.service.as.impl;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.service.as.MainTainService;

@Service
public class MainTainServiceImpl implements MainTainService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;	

	public void saveMainTain(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		//保存detail
		for (Map<Object, Object> s : grid) {
			s.put("mtd_id", baseDao.getSeqId("MainTainDetail_sequence"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"MainTainDetail_user");
		baseDao.execute(gridSql);
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "MainTain_user",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "mt_id", store.get("mt_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });

	}
	
	public void deleteMainTain(int ct_id, String caller) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("MainTain_user",
				"mt_statuscode", "mt_id=" + ct_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { ct_id });
		// 删除CustTurn
		baseDao.deleteById("MainTain_user", "mt_id", ct_id);
		// 删除detail
		baseDao.deleteById("MainTainDetail_user", "mtd_mtid", ct_id);
		// 记录操作
		baseDao.logger.delete(caller, "mt_id", ct_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ct_id);

	}
	
	public void updateMainTain(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		/*Object status = baseDao.getFieldDataByCondition("MainTain_user",
				"MT_STATUSCODE", "mt_id=" + store.get("mt_id"));
		if (!"ENTERING".equals(status)&&!"AUDITED".equals(status)) {
			BaseUtil.showError("只能更新【在录入】和【已审核】状态的单据!");
		}*/
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		//update detail
		List<String> gridSql = SqlUtil.getInsertOrUpdateSql(gstore, "MainTainDetail_user", "mtd_id");
		baseDao.execute(gridSql);
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "MainTain_user",
				"mt_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "mt_id", store.get("mt_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });

	}
	
	public void submitMainTain(int ct_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MainTain_user",
				"mt_statuscode", "mt_id=" + ct_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.submit_onlyEntering"));
		}
		Object MT_TYPE = baseDao.getFieldDataByCondition("MainTain_user",
				"MT_TYPE", "mt_id=" + ct_id);
		if("charge".equals(MT_TYPE)){
			Object mt_code=baseDao.getFieldDataByCondition("MainTain_user", "MT_CODE", "mt_id="+ct_id);
			Object[] value=baseDao.getFieldsDataByCondition("MainTain_user", "MT_CKCODE,MT_ROW", "mt_id="+ct_id);
			Object[] sb_id=baseDao.getFieldsDataByCondition("AS_STANDBYOUT", "so_id", "so_code= '"+value[0]+"'");
			if(value[0]==null){
				BaseUtil.showError("选择冲备用机类型请选择相应的备用机出库单");
			}
			Object sod_unback=baseDao.getFieldDataByCondition("AS_STANDBYOUTDETAIL", "sod_unback", "sod_soid="+sb_id[0]+" and sod_deptno="+value[1]);
			int count1=baseDao.getCount("select count(*) from MainTain_user where mt_ckcode='"+value[0]+"' and mt_row="+value[1]+" and mt_statuscode='COMMITED'");
			int count2=baseDao.getCount("select count(*) from AS_STANDBYBACK where SB_FROMCODE='"+mt_code+"' and sb_statuscode='COMMITED'");
			if((Integer.parseInt((sod_unback==null?"0":sod_unback).toString())-count1-count2)<=0){
				BaseUtil.showError("没有足够的可冲销数量！");
			}
			Object MT_CUSTCODE = baseDao.getFieldDataByCondition("MainTain_user",
					"MT_CUSTCODE", "mt_id=" + ct_id);
			Object MT_CKCODE = baseDao.getFieldDataByCondition("MainTain_user",
					"MT_CKCODE", "mt_id=" + ct_id);
			Object SO_CUSTCODE = baseDao.getFieldDataByCondition("AS_standbyout",
					"SO_CUSTCODE", "SO_CODE='" + MT_CKCODE+"'");
			if(!(MT_CUSTCODE==null?"":MT_CUSTCODE).equals((SO_CUSTCODE==null?"":SO_CUSTCODE))){
				BaseUtil.showError("维修申请单的客户和所关联的备用机出库单的客户不一致!");
			}
			
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ct_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"MainTain_user",
				"mt_statuscode='COMMITED',mt_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'",
				"mt_id=" + ct_id);
		// 记录操作
		baseDao.logger.submit(caller, "mt_id", ct_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ct_id);

	}
	
	public void resSubmitMainTain(int ct_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MainTain_user",
				"mt_statuscode", "mt_id=" + ct_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.resSubmit_onlyCommited"));
		}
		handlerService.beforeResSubmit(caller, ct_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"MainTain_user",
				"mt_statuscode='ENTERING',mt_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'",
				"mt_id=" + ct_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "mt_id", ct_id);
		// 执行提交后的其它逻辑
		handlerService.afterResSubmit(caller, ct_id);

	}
	
	public void auditMainTain(int ct_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("MainTain_user",
				"mt_statuscode", "mt_id=" + ct_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ct_id);
		Object type = baseDao.getFieldDataByCondition("MainTain_user",
				"mt_type", "mt_id=" + ct_id);
		if ("charge".equals(type)) {
			String sql = "select DISTINCT MT_ID,MT_CODE,MT_CUSTNAME,MT_CUSTCODE,MT_FNCODE,MT_JX,MT_CKCODE,MT_ROW,MT_APPLICATIONMAN,MT_APPLICATIONMANCODE,MT_APPLICATIONDAPT,MT_APPLICATIONDEPTCODE from MainTain_user where mt_id="
					+ ct_id;
			SqlRowList rs = baseDao.queryForRowSet(sql);
			while (rs.next()) {
				String sb_code = baseDao.sGetMaxNumber("StandbyBack", 2);
				baseDao.execute("insert into AS_StandbyBack(SB_ID,SB_CODE,SB_CUSTNAME,SB_CUSTNAMECODE,SB_APPLICATIONMAN,SB_APPLICATIONMANCODE,SB_APPLICATIONDAPT,SB_APPLICATIONDAPTCODE,SB_FROMCODE,SB_STATUSCODE,SB_STATUS,SB_AUDITEDATE,SB_NUMBER1)"
						+ "values(AS_StandbyBack_seq.nextval,'"+sb_code+"','" + rs.getString("MT_CUSTNAME") + "','" + rs.getString("MT_CUSTCODE") + "','"
						+ rs.getString("MT_APPLICATIONMAN") + "'" + ",'" + rs.getString("MT_APPLICATIONMANCODE") + "','"
						+ rs.getString("MT_APPLICATIONDAPT") + "','" + rs.getString("MT_APPLICATIONDEPTCODE") + "','"
						+ rs.getString("MT_CODE") + "','" + "AUDITED" + "','"
						+ "已审核" + "',sysdate,"+rs.getInt("MT_ID")+")");
				Object sb_id = baseDao.getFieldDataByCondition("AS_StandbyBack",
						"sb_id", "SB_FROMCODE='" +rs.getString("MT_CODE")+"'");
				Object so_id = baseDao.getFieldDataByCondition("AS_StandbyOut",
						"so_id", "SO_CODE='" + rs.getString("MT_CKCODE")+"'");
				Object[] sod_id=baseDao.getFieldsDataByCondition("As_StandbyOutdetail", new String[]{"sod_id","sod_text1"}, "sod_soid="+so_id+" and sod_deptno="+rs.getInt("MT_ROW"));
				baseDao.execute(
						"update AS_StandbyOutDetail set SOD_BACK=NVL(SOD_BACK,0)+1,SOD_UNBACK=NVL(SOD_UNBACK,0)-1,SOD_YZQTY=NVL(SOD_YZQTY,0)+1 WHERE SOD_SOID="
								+ so_id +" and sod_deptno="+rs.getString("MT_ROW"));
				Object SOD_UNBACK = baseDao.getFieldDataByCondition("AS_StandbyOutDetail",
						"SOD_UNBACK", "SOD_SOID=" +so_id+" and sod_deptno="+rs.getInt("MT_ROW"));
				baseDao.execute("insert into AS_StandbyBackDetail(SBD_ID,SBD_SBID,SBD_CODE,SBD_ROW,SBD_JX,SBD_REMARK,SBD_BACKPTY,SBD_OUTPTY,SBD_SOURCEID,SBD_TEXT1)"
						+ "values(AS_StandbyBackDetail_seq.nextval," + sb_id + ",'" + rs.getString("MT_CKCODE") + "',"
						+ rs.getInt("MT_ROW") + "" + ",'" + rs.getString("MT_JX") + "','"
						+ rs.getString("MT_FNCODE") + "'," + 1 + ","
						+ SOD_UNBACK + ","+sod_id[0]+",'"+sod_id[1]+"')");
			}
		}
		// 执行审核操作
		Employee employee = SystemSession.getUser();
		baseDao.updateByCondition(
				"MainTain_user",
				"mt_statuscode='AUDITED',mt_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',MT_AUDITEMAN='"+employee.getEm_name()+"',MT_AUDITEDATE=sysdate", "mt_id=" + ct_id);
		// 记录操作
		baseDao.logger.audit(caller, "mt_id", ct_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ct_id);

	}
	
	public void resAuditMainTain(int ct_id, String caller) {
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, ct_id);
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object MT_CODE = baseDao.getFieldDataByCondition("MainTain_user",
				"MT_CODE", "mt_id=" + ct_id);
		Object sb_id = baseDao.getFieldDataByCondition("AS_StandbyBack",
				"max(sb_id)", "sb_fromcode='" + MT_CODE+"'");
		Object sb_code = baseDao.getFieldDataByCondition("AS_StandbyBack",
				"sb_code", "sb_id=" +sb_id );
		boolean s=baseDao.checkIf("AS_StandbyBack", "SB_FROMCODE='"+MT_CODE+"'");
		if(s){
			BaseUtil.showError("此单据有关联的《备用机归还单》，单号为:"
					+ "<a href=\"javascript:openUrl('jsps/as/port/StandbyBack.jsp?formCondition=sb_idIS" + sb_id
					+ "&gridCondition=sbd_sbidIS" + sb_id + "')\">" + sb_code + "</a>&nbsp;");
		}
		// 执行反审核操作
		baseDao.updateByCondition(
				"MainTain_user",
				"mt_statuscode='ENTERING',mt_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',MT_APPLICATIONMAN=''", "mt_id=" + ct_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "mt_id", ct_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, ct_id);

	}

	@Override
	public void marketMainTain(int ct_id,String value, String caller) {
		// TODO Auto-generated method stub
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("MainTain_user",
				"mt_statuscode", "mt_id=" + ct_id);
		Object type = baseDao.getFieldDataByCondition("MainTain_user",
				"mt_type", "mt_id=" + ct_id);
		Employee employee = SystemSession.getUser();
		if("AUDITED".equals(status)){
			if("索赔".equals(type)){
			baseDao.updateByCondition(
						"MainTain_user",
						"mt_statuscode='STORAGED',mt_status='"
								+ "已入库"
								+ "',MT_APPLICATIONMAN='" + employee.getEm_name()
								+ "',MT_APPLICATIONDATE=sysdate", "mt_id=" + ct_id);
			}else{
			baseDao.updateByCondition(
					"MainTain_user",
					"mt_statuscode='ONLINE',mt_status='"
							+ "待上线"
							+ "',MT_APPLICATIONMAN='" + employee.getEm_name()
							+ "',MT_APPLICATIONDATE=sysdate", "mt_id=" + ct_id);
			}
		}else if("ONLINE".equals(status)){
			baseDao.updateByCondition(
					"MainTain_user",
					"mt_statuscode='REPAIRING',mt_status='"
							+ "返修中"
							+ "',MT_APPLICATIONMAN='" + employee.getEm_name()
							+ "',MT_APPLICATIONDATE=sysdate", "mt_id=" + ct_id);	
		}else if("REPAIRING".equals(status)){
			Object mt_type = baseDao.getFieldDataByCondition("MainTain_user",
					"MT_TYPE", "mt_id=" + ct_id);
			if("charge".equals(mt_type)){
				baseDao.updateByCondition(
						"MainTain_user",
						"mt_statuscode='CHARGING',mt_status='"
								+ "核销"
								+ "',MT_APPLICATIONMAN='" + employee.getEm_name()
								+ "',MT_APPLICATIONDATE=sysdate", "mt_id=" + ct_id);
			}else if("back".equals(mt_type)){
				baseDao.updateByCondition(
						"MainTain_user",
						"mt_statuscode='RETURNING',mt_status='"
								+ "待归还"
								+ "',MT_APPLICATIONMAN='" + employee.getEm_name()
								+ "',MT_APPLICATIONDATE=sysdate", "mt_id=" + ct_id);
			}		
		}else if("RETURNING".equals(status)){
			baseDao.updateByCondition(
					"MainTain_user",
					"mt_statuscode='RETURNED',mt_status='"
							+ "已归还"
							+ "',MT_APPLICATIONMAN='" + employee.getEm_name()
							+ "',MT_APPLICATIONDATE=sysdate", "mt_id=" + ct_id);
		}else{
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"该状态不能执行确认操作！"));
		}
	}
}
