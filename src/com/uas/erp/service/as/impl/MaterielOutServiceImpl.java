package com.uas.erp.service.as.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.dialect.function.NvlFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.as.MaterielOutService;


@Service
public class MaterielOutServiceImpl implements MaterielOutService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveMaterielOut(String formStore, String param,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[]{store});		
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "AS_MAKEOut", new String[]{}, new Object[]{});
		baseDao.execute(formSql);	
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		List<String> sqls = new ArrayList<String>();
		for(Map<Object, Object> s:gstore){
			if(s.get("amod_id") == null || s.get("amod_id").equals("") || s.get("amod_id").equals("0")){//新添加的数据，id不存在
				int amod_id = baseDao.getSeqId("AS_MAKEOUTDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "AS_MAKEOUTDETAIL", new String[]{"amod_id"}, new Object[]{amod_id});
				sqls.add(sql);
			}
		}
		baseDao.execute(sqls);
		baseDao.execute("update AS_MAKEOUTDETAIL set amod_outqty=amod_chuqty,amod_code='"+store.get("amo_code")+"' where amod_amoid="+store.get("amo_id")+"");
		baseDao.logger.save(caller, "amo_id", store.get("amo_id"));
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void updateMaterielOutById(String formStore,String param,String caller) {		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[]{store});
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "AS_MAKEOut", "amo_id");
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(param, "AS_MAKEOUTDETAIL", "amod_id");
		baseDao.execute(gridSql);
		baseDao.execute(formSql);
		baseDao.execute("update AS_MAKEOUTDETAIL set amod_outqty=amod_chuqty,amod_code='"+store.get("amo_code")+"' where amod_amoid="+store.get("amo_id")+"");
		//记录操作
		baseDao.logger.update(caller, "amo_id", store.get("amo_id"));
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void deleteMaterielOut(int amo_id, String caller) {		
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{amo_id});
		//回写申请单已转数
		String sql="select amod_id,amod_amadid,amod_amacode,amod_outqty from as_makeoutdetail where amod_amoid="+amo_id;
		SqlRowList rs1 = baseDao.queryForRowSet(sql);
		while(rs1.next()){
			Object ama_id = baseDao.getFieldDataByCondition("AS_MAKEAPPLY",
					"ama_id", "ama_code='" + rs1.getString("amod_amacode")+"'");
			baseDao.execute("update As_makeapplydetail set amad_tqty=nvl(amad_tqty,0)-"+rs1.getInt("amod_outqty")+" where amad_amaid="+ama_id+" and amad_id="+rs1.getInt("amod_amadid"));
		}
		//删除内容
		baseDao.deleteById("AS_MAKEOut", "amo_id", amo_id);
		baseDao.deleteById("As_makeoutdetail", "amod_amoid", amo_id );
		baseDao.logger.delete(caller, "amo_id", amo_id);
		handlerService.handler(caller, "delete", "after", new Object[]{amo_id});
	}

	@Override
	public void auditMaterielOut(int amo_id, String caller) {
		//只能对已提交进行审核操作
	//	Object status = baseDao.getFieldDataByCondition("MaterielOut", "mpf_statuscode", "amo_id=" + amo_id);
		Object ob=baseDao.getFieldDataByCondition("AS_MAKEOut", "amo_statuscode",  "amo_id=" + amo_id);
		StateAssert.auditOnlyCommited(ob);
		//执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[]{amo_id});
		//审核时的处理逻辑
		String sql="select amod_id,amod_amadid,amod_amacode,amod_outqty from as_makeoutdetail where amod_amoid="+amo_id;
		SqlRowList rs1 = baseDao.queryForRowSet(sql);
		while(rs1.next()){
			Object ama_id = baseDao.getFieldDataByCondition("AS_MAKEAPPLY",
					"ama_id", "ama_code='" + rs1.getString("amod_amacode")+"'");
			baseDao.execute("update As_makeapplydetail set amad_outqty=nvl(amad_outqty,0)+"+rs1.getInt("amod_outqty")+" where amad_amaid="+ama_id+" and amad_id="+rs1.getInt("amod_amadid"));
		}
		//执行审核操作,待写
		baseDao.audit("AS_MAKEOut", "amo_id=" + amo_id, "amo_status", "amo_statuscode", "amo_auditdate", "amo_auditor");
		//记录操作
		baseDao.logger.audit(caller, "amo_id", amo_id);
		handlerService.handler(caller, "audit", "after", new Object[]{amo_id});
	}
	@Override
	public void submitMaterielOut(int amo_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("AS_MAKEOut", "amo_statuscode", "amo_id=" + amo_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { amo_id });
		// 执行提交操作
		baseDao.submit("AS_MAKEOut", "amo_id=" + amo_id, "amo_status", "amo_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "amo_id", amo_id);
		handlerService.handler(caller, "commit", "after", new Object[] { amo_id });
	}
	@Override
	public void resSubmitMaterielOut(int amo_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("AS_MAKEOut", "amo_statuscode", "amo_id=" + amo_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { amo_id });
		// 执行反提交操作
		baseDao.resOperate("AS_MAKEOut", "amo_id=" + amo_id, "amo_status", "amo_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "amo_id", amo_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { amo_id });
	}
	@Override
	public void resAuditMaterielOut(int amo_id, String caller) {
		// 只能对状态为[已审核]的表单进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("AS_MAKEOut", new String[]{"amo_statuscode","amo_code"}, "amo_id=" + amo_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		Object outcode = baseDao.getFieldDataByCondition("AS_MAKERETURNDETAIL", "max(amrd_amrid)", "amrd_amocode='"+status[1]+"'");
		if(outcode!=null){
			Object code = baseDao.getFieldDataByCondition("AS_MAKERETURN", "amr_code", "amr_id='"+outcode+"'");
			BaseUtil.showError("该出库单有关联的归还单，不允许反审核，请先删除关联的归还单，归还单号："+ "<a href=\"javascript:openUrl('jsps/as/port/materielreturn.jsp?formCondition=amr_idIS" + outcode
							+ "&gridCondition=amrd_amridIS" + outcode + "&whoami=MaterielReturn')\">" +code + "</a>");
		}
		String sql="select amod_id,amod_amadid,amod_amacode,amod_outqty from as_makeoutdetail where amod_amoid="+amo_id;
		SqlRowList rs1 = baseDao.queryForRowSet(sql);
		while(rs1.next()){
			Object ama_id = baseDao.getFieldDataByCondition("AS_MAKEAPPLY",
					"ama_id", "ama_code='" + rs1.getString("amod_amacode")+"'");
			baseDao.execute("update As_makeapplydetail set amad_outqty=nvl(amad_outqty,0)-"+rs1.getInt("amod_outqty")+" where amad_amaid="+ama_id+" and amad_id="+rs1.getInt("amod_amadid"));
		}
		// 执行反审核操作
		baseDao.updateByCondition(
				"AS_MAKEOut",
				"amo_statuscode='ENTERING',amo_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',amo_auditor=''", "amo_id=" + amo_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "amo_id", amo_id);
	}
	
	@Override
	public void updateMaterialQtyChangeInProcss(String caller,String formStore, String gridStore) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		String table = "";
		String mxid ="";
		String sql = "";
		for(Map<Object, Object> s:gstore){
			if("MaterielOut".equals(caller)){
				table = "AS_MakeOutDetail";
				mxid = "amod_id";
				sql = "update AS_MakeApplyDetail set amad_tqty='"+s.get("amod_chuqty")+"' where amad_id='"+s.get("amod_amadid")+"'";
			}else if("StandbyOut".equals(caller)){
				table = "AS_StandByOutDetail";
				mxid = "sod_id";
				sql = "update AS_STANDBYDETAIL set SAD_OUT='"+s.get("sod_chuqty")+"' where sad_id='"+s.get("sod_sourceid")+"'";
			}
			baseDao.execute(sql);
		}
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, table, mxid);
		baseDao.execute(gridSql);
	}
}
