package com.uas.erp.service.as.impl;


import java.util.List;
import java.util.Map;

import org.docx4j.model.datastorage.XPathEnhancerParser.main_return;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.service.as.StandbyBackService;

@Service
public class StandbyBackImpl implements StandbyBackService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;	

	public void saveStandbyBack(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		//保存detail
		for (Map<Object, Object> s : grid) {
			s.put("sbd_id", baseDao.getSeqId("As_StandbyBackDetail_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"As_StandbyBackDetail");
		baseDao.execute(gridSql);
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "As_StandbyBack",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "sb_id", store.get("sb_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });

	}
	
	public void deleteStandbyBack(int ct_id, String caller) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("As_StandbyBack",
				"sb_statuscode", "sb_id=" + ct_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.delete_onlyEntering"));
		}
		String sql="select sbd_id,sbd_sourceid,sbd_code,sbd_backpty from as_standbybackdetail where sbd_sbid="+ct_id;
		SqlRowList rs1 = baseDao.queryForRowSet(sql);
		while(rs1.next()){
			Object so_id = baseDao.getFieldDataByCondition("As_StandbyOut",
					"so_id", "so_code='" + rs1.getString("sbd_code")+"'");
			baseDao.execute("update as_standbyOutdetail set sod_yzqty=nvl(sod_yzqty,0)-"+rs1.getInt("sbd_backpty")+" where sod_soid="+so_id+" and sod_id="+rs1.getInt("sbd_sourceid"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { ct_id });
		// 删除CustTurn
		baseDao.deleteById("As_StandbyBack", "sb_id", ct_id);
		// 删除detail
		baseDao.deleteById("As_StandbyBackdetail", "sbd_sbid", ct_id);
		// 记录操作
		baseDao.logger.delete(caller, "sb_id", ct_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ct_id);
	}
	
	public void updateStandbyBack(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("As_StandbyBack",
				"sb_statuscode", "sb_id=" + store.get("sb_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.update_onlyEntering"));
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		//update detail
		if(!gstore.isEmpty() || null != gstore ){
			for(Map<Object, Object> nstore : gstore){
				Object sbd_code=nstore.get("sbd_code");
				Object sbd_sourceid=nstore.get("sbd_sourceid");
				Object sbd_id=nstore.get("sbd_id");
				Object sum=baseDao.getFieldDataByCondition("AS_STANDBYBACKDETAIL", "sum(SBD_BACKPTY)", "sbd_code='"+sbd_code+"' and sbd_sourceid="+sbd_sourceid+" and sbd_id<>"+sbd_id);
				int sum1=Integer.parseInt(sum==null?"0":sum.toString());
				int sum2=Integer.parseInt(nstore.get("sbd_backpty")==null?"0":nstore.get("sbd_backpty").toString());
				int total=sum1+sum2;
				Object so_id=baseDao.getFieldDataByCondition("as_standbyOut", "so_id", "so_code='"+sbd_code+"'");
				Object so_count=baseDao.getFieldDataByCondition("as_standbyOutdetail", "sod_out", "sod_soid="+so_id+" and sod_id="+sbd_sourceid);
				int sa_count1=Integer.parseInt(so_count==null?"0":so_count.toString());
				if(sa_count1<total){
					BaseUtil.showError("累计已转数不能大于备用机出库单出库总数量!");
				}else{
					baseDao.execute("update as_standbyOutdetail set sod_yzqty="+total+" where sod_soid="+so_id+" and sod_id="+sbd_sourceid);
				}
				
			}
		}
		List<String> gridSql = SqlUtil.getInsertOrUpdateSql(gstore, "As_StandbyBackdetail", "sbd_id");
		baseDao.execute(gridSql);
		// 修改CustTurn
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "As_StandbyBack",
				"sb_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "sb_id", store.get("sb_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });

	}
	
	public void submitStandbyBack(int ct_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("As_StandbyBack",
				"sb_statuscode", "sb_id=" + ct_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.submit_onlyEntering"));
		}
		String sql="select SBD_CODE,SBD_OUTPTY,SBD_BACKPTY,SBD_ROW from As_StandbyBackDetail where sbd_sbid="+ct_id;
		SqlRowList rs = baseDao.queryForRowSet(sql);
		while(rs.next()){
			if(rs.getInt("SBD_BACKPTY")>rs.getInt("SBD_OUTPTY")){
				BaseUtil.showError("明细行本次归还数量不能大于剩余归还数量!");
			}
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ct_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"As_StandbyBack",
				"sb_statuscode='COMMITED',sb_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'",
				"sb_id=" + ct_id);
		// 记录操作
		baseDao.logger.submit(caller, "sb_id", ct_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ct_id);

	}
	
	public void resSubmitStandbyBack(int ct_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("As_StandbyBack",
				"sb_statuscode", "sb_id=" + ct_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.resSubmit_onlyCommited"));
		}
		handlerService.beforeResSubmit(caller, ct_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"As_StandbyBack",
				"sb_statuscode='ENTERING',sb_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'",
				"sb_id=" + ct_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "sb_id", ct_id);
		// 执行提交后的其它逻辑
		handlerService.afterResSubmit(caller, ct_id);

	}
	
	public void auditStandbyBack(int ct_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("As_StandbyBack",
				"sb_statuscode", "sb_id=" + ct_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ct_id);
		// 执行审核操作
		Employee employee = SystemSession.getUser();
		baseDao.updateByCondition(
				"As_StandbyBack",
				"sb_statuscode='AUDITED',sb_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',SB_AUDITEMAN='"+employee.getEm_name()+"',SB_AUDITEDATE=sysdate", "sb_id=" + ct_id);
		String sql="select SBD_CODE,SBD_OUTPTY,SBD_BACKPTY,SBD_SOURCEID from As_StandbyBackDetail where sbd_sbid="+ct_id;
		SqlRowList rs = baseDao.queryForRowSet(sql);
		while(rs.next()){
			baseDao.execute("update As_StandbyBackDetail set SBD_OUTPTY=nvl(SBD_OUTPTY,0)-nvl(SBD_BACKPTY,0)");
			Object so_id = baseDao.getFieldDataByCondition("As_StandbyOut",
					"so_id", "so_code='" + rs.getString("SBD_CODE") +"'");
			baseDao.execute("update As_StandbyOutDetail set SOD_BACK=nvl(SOD_BACK,0)+"+rs.getInt("SBD_BACKPTY")+" where sod_soid="+so_id+" and sod_id="+rs.getInt("SBD_SOURCEID"));
			baseDao.execute("update As_StandbyOutDetail set SOD_UNBACK=nvl(SOD_OUT,0)-nvl(SOD_BACK,0) where sod_soid="+so_id+" and sod_id="+rs.getInt("SBD_SOURCEID"));
		}
		// 记录操作
		baseDao.logger.audit(caller, "sb_id", ct_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ct_id);

	}
	
	public void resAuditStandbyBack(int ct_id, String caller) {
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, ct_id);
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("As_StandbyBack",
				"sb_statuscode", "sb_id=" + ct_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.resAudit_onlyAudit"));
		}
		// 执行反审核操作
		baseDao.updateByCondition(
				"As_StandbyBack",
				"sb_statuscode='ENTERING',sb_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',Sb_APPLICATIONMAN='',Sb_APPLICATIONDATE=null", "sb_id=" + ct_id);
		String sql="select SBD_CODE,SBD_OUTPTY,SBD_BACKPTY,SBD_SOURCEID from As_StandbyBackDetail where sbd_sbid="+ct_id;
		SqlRowList rs = baseDao.queryForRowSet(sql);
		while(rs.next()){
			baseDao.execute("update As_StandbyBackDetail set SBD_OUTPTY=nvl(SBD_OUTPTY,0)+nvl(SBD_BACKPTY,0)");
			Object so_id = baseDao.getFieldDataByCondition("As_StandbyOut",
					"so_id", "so_code='" + rs.getString("SBD_CODE") +"'");
			baseDao.execute("update As_StandbyOutDetail set SOD_BACK=nvl(SOD_BACK,0)-nvl("+rs.getInt("SBD_BACKPTY")+",0) where sod_soid="+so_id+" and sod_id="+rs.getInt("SBD_SOURCEID"));
			baseDao.execute("update As_StandbyOutDetail set SOD_UNBACK=nvl(SOD_OUT,0)-nvl(SOD_BACK,0) where sod_soid="+so_id+" and sod_id="+rs.getInt("SBD_SOURCEID"));
		}
		// 记录操作
		baseDao.logger.resAudit(caller, "sb_id", ct_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, ct_id);

	}

}
