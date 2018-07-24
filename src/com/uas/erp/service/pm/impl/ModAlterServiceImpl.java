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
import com.uas.erp.dao.common.ModAlterDao;
import com.uas.erp.model.FileUpload;
import com.uas.erp.service.pm.ModAlterService;

import net.sf.json.JSONObject;

@Service("modAlterService")
public class ModAlterServiceImpl implements ModAlterService{
	@Autowired
	private BaseDao baseDao;	
	@Autowired
	private ModAlterDao modAlterDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveModAlter(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("MOD_ALTER", "al_code='" + store.get("al_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller,  new Object[]{store,gstore});
		//保存ModAlter
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "MOD_ALTER", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//保存ModAlterDetail
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		for(Map<Object, Object> s:grid){
			s.put("ald_id", baseDao.getSeqId("MOD_ALTERDETAIL_SEQ"));
			s.put("ald_code", store.get("al_code"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "MOD_ALTERDETAIL");
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "al_code", store.get("al_code"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller,  new Object[]{store,gstore});
	}
	
	@Override
	public void deleteModAlter(int al_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("MOD_ALTER", "al_statuscode", "al_id=" + al_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{al_id});;
		//删除ModAlter
		baseDao.deleteById("MOD_ALTER", "al_id", al_id);
		//删除ModAlterDetail
		baseDao.deleteById("MOD_ALTERDETAIL", "ald_alid", al_id);
		//记录操作
		baseDao.logger.delete(caller, "al_id", al_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{al_id});
	}
	
	@Override
	public void updateModAlterById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("MOD_ALTER", "al_statuscode", "al_id=" + store.get("al_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller,  new Object[]{store,gstore});
		//修改ModAlter
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "MOD_ALTER", "al_id");
		baseDao.execute(formSql);
		//修改ModAlterDetail		
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "MOD_ALTERDETAIL", "ald_id");
		for(Map<Object, Object> s:gstore){
			if (s.get("ald_id") == null || s.get("ald_id").equals("") || s.get("ald_id").equals("0")
					|| Integer.parseInt(s.get("ald_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("MOD_ALTERDETAIL_SEQ");
				s.put("ald_code", store.get("al_code"));
				String sql = SqlUtil.getInsertSqlByMap(s, "MOD_ALTERDETAIL", new String[] { "ald_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "al_id", store.get("al_id"));
		
		//执行修改后的其它逻辑
		handlerService.afterSave(caller,  new Object[]{store,gstore});
	}
	
	@Override
	public String[] printModAlter(int al_id,String caller, String reportName,String condition) {
		//只能打印审核后的单据!
		/*Object status = baseDao.getFieldDataByCondition("MOD_ALTER", "al_statuscode", "al_id=" + al_id);
		if(!status.equals("AUDITED")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.print_onlyAudit"));
		}
		//执行打印前的其它逻辑
		handlerService.handler("Alter!Mould", "print", "before", new Object[]{al_id});*/
		//执行打印操作
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		//记录操作
		baseDao.logger.print(caller, "al_id", al_id);
		//执行打印后的其它逻辑
		handlerService.afterPrint(caller, new Object[]{al_id});
		return keys;
	}
	
	@Override
	public void auditModAlter(int al_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("MOD_ALTER", "al_statuscode", "al_id=" + al_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{al_id});
		//执行审核操作
		baseDao.audit("MOD_ALTER", "al_id=" + al_id, "al_status", "al_statuscode", "al_auditdate", "al_auditman");
		//记录操作
		baseDao.logger.audit(caller, "al_id", al_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{al_id});
	}
	
	@Override
	public void resAuditModAlter(int al_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("MOD_ALTER", "al_statuscode", "al_id=" + al_id);
		StateAssert.resAuditOnlyAudit(status);
		Object code = baseDao.getFieldDataByCondition("MOD_ALTER", "al_code", "al_id=" + al_id);
		code = baseDao.getFieldDataByCondition("MOULDFEEPLEASE", "mp_code", "mp_changecode='" + code + "'");
		if(code != null && !code.equals("")){
			BaseUtil.showError("该模具修改申请单已转入模具付款申请单，不允许反审核！申请单号："+ code);
		}
		code = baseDao.getFieldDataByCondition("PriceMould", "pd_code", "pd_sourcecode='" + code + "' and nvl(pd_sourcetype,' ')='模具修改申请单'");
		if(code != null && !code.equals("")){
			BaseUtil.showError("该模具修改申请单已转入模具报价单，不允许反审核！报价单号："+ code);
		}
		code = baseDao.getFieldDataByCondition("Mod_sale", "msa_code", "msa_sourcecode='" + code + "' and nvl(MSA_SOURCETYPE,' ')='模具修改申请单'");
		if(code != null && !code.equals("")){
			BaseUtil.showError("该模具修改申请单已转入模具销售单，不允许反审核！销售单号："+ code);
		}
		//执行反审核操作
		baseDao.resOperate("MOD_ALTER", "al_id=" + al_id, "al_status", "al_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "al_id", al_id);
	}
	
	@Override
	public void submitModAlter(int al_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MOD_ALTER", "al_statuscode", "al_id=" + al_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{al_id});
		//执行提交操作
		baseDao.submit("MOD_ALTER", "al_id=" + al_id, "al_status", "al_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "al_id", al_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{al_id});
	}
	
	@Override
	public void resSubmitModAlter(int al_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MOD_ALTER", "al_statuscode", "al_id=" + al_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[]{al_id});
		//执行反提交操作
		baseDao.resOperate("MOD_ALTER", "al_id=" + al_id, "al_status", "al_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "al_id", al_id);
		handlerService.afterResSubmit(caller, new Object[]{al_id});
	}
	
	
	@Override
	public int turnFeePlease(int al_id, String caller) {
		int mpid = 0;
		//判断该模具修改申请单已转入模具付款申请单
		Object code = baseDao.getFieldDataByCondition("MOD_ALTER", "al_code", "al_id=" + al_id);
		code = baseDao.getFieldDataByCondition("MOULDFEEPLEASE", "mp_code", "mp_changecode='" + code + "'");
		if(code != null && !code.equals("")){
			BaseUtil.showError("该模具修改申请单已转入模具付款申请单,申请单号" + code);
		} else {
			SqlRowList rs = baseDao.queryForRowSet("SELECT sum(nvl(ald_amount,0)) from MOD_alterDetail where ald_alid=?",al_id);
			Double aldamount = 0.0;
			if(rs.next()){
				aldamount = rs.getDouble(1);
				if(aldamount == 0){
					BaseUtil.showError("该模具修改申请单没有申请金额不能转!");
				}
			}
			mpid = modAlterDao.turnFeePlease(al_id, aldamount);
			baseDao.updateByCondition("MOD_ALTER", "al_tostatus='已转付款申请'", "al_id=" + al_id);
		}
		return mpid;
	}

	@Override
	public String turnMouldSale(int al_id) {
		JSONObject j = null;
		StringBuffer sb = new StringBuffer();
		int msa_id = 0;
		//判断该模具修改申请单是否已经转入过模具销售单
		Object code = baseDao.getFieldDataByCondition("MOD_ALTER", "al_code", "al_id=" + al_id);
		code = baseDao.getFieldDataByCondition("MOD_SALE", "msa_code", "msa_sourcecode='" + code + "' and msa_sourcetype='模具修改申请单'");
		if(code != null && !code.equals("")){
			BaseUtil.showError(BaseUtil.getLocalMessage("pm.mould.modalter.haveturnmodsale") + 
					"<a href=\"javascript:openUrl('jsps/pm/mould/mouldSale.jsp?formCondition=msa_codeIS" + code + "&gridCondition=msd_codeIS" + code + "')\">" + code + "</a>&nbsp;");
		} else {
			j = modAlterDao.turnMouldSale(al_id);
			if (j != null) {
				msa_id = j.getInt("msa_id");
				sb.append("转入成功,模具销售单号:" + "<a href=\"javascript:openUrl('jsps/pm/mould/mouldSale.jsp?formCondition=msa_idIS"
						+ msa_id + "&gridCondition=msd_msaidIS" + msa_id + "')\">"
						+ j.getString("msa_code") + "</a>&nbsp;");
				//修改申请单状态
				baseDao.updateByCondition("MOD_ALTER", "AL_TURNSALECODE='TURNSA',AL_TURNSALE='" + 
						BaseUtil.getLocalMessage("TURNSA") + "'", "al_id=" + al_id);
				//记录操作
				baseDao.logger.turn("转模具销售单操作", "Alter!Mould", "al_id", al_id);
			}
		}
		return sb.toString();
	}

	@Override
	public List<Map<String, Object>> turnPriceMould(int al_id) {
		List<Map<String, Object>> pdid = null;
		//判断该开模申请单是否已经转入过模具报价单
		Object code = baseDao.getFieldDataByCondition("MOD_ALTER", "al_code", "al_id=" + al_id);
		code = baseDao.getFieldDataByCondition("PriceMould", "pd_code", "pd_appmouldcode='" + code + "' and pd_sourcetype='模具修改申请单'");
		if(code != null && !code.equals("")){
			BaseUtil.showError(BaseUtil.getLocalMessage("pm.mould.modalter.haveturnpricemould") + 
					"<a href=\"javascript:openUrl('jsps/pm/mould/priceMould.jsp?formCondition=pd_codeIS" + code + "&gridCondition=pmd_codeIS" + code + "')\">" + code + "</a>&nbsp;");
		} else {
			pdid = modAlterDao.turnPriceMould(al_id);
			//修改报价单状态
			baseDao.updateByCondition("MOD_ALTER", "al_turnpricecode='TURNPM',al_turnprice='" + 
					BaseUtil.getLocalMessage("TURNPM") + "'", "al_id=" + al_id);
			//记录操作
			baseDao.logger.turn("转模具报价单操作", "Alter!Mould", "al_id", al_id);
		}
		return pdid;
	}

	@Override
	public void uploadDetailFile(String params,String caller,String code,String keyvalue,String keyField) {
		Map<Object,Object> data=BaseUtil.parseFormStoreToMap(params);
		int aldid = Integer.parseInt(data.get("MF_ALDID").toString());
		Object versionDate = baseDao.getFieldDataByCondition("mod_fileversion", "max(mf_version)", "mf_aldid="+aldid);
		if(versionDate!=null) {
			data.put("mf_version", Integer.parseInt(versionDate.toString())+1);
		}else{
			data.put("mf_version",1);
		}
		List<String> sqls=new ArrayList<String>();
		sqls.add(SqlUtil.getInsertSqlByMap(data, "mod_fileversion"));
		baseDao.execute(sqls);
		baseDao.execute("insert into messagelog(ml_date,ml_man,ml_content,ml_result,ml_search,code) values("
				+ "sysdate,'"+SystemSession.getUser().getEm_name()+"','上传附件','上传成功："+data.get("MF_FILENAME")+"',"
			    + "'"+caller+"|"+keyField+"="+keyvalue+"','"+code+"')");
	}

	@Override
	public void deleteDetailFile(Integer id,String caller,String code,String keyvalue,String keyField) {
		String fileName = String.valueOf(baseDao.getFieldDataByCondition("mod_fileversion", "mf_filename", "mf_id="+id));
		baseDao.deleteByCondition("mod_fileversion", "mf_id=?", id);
		baseDao.execute("insert into messagelog(ml_date,ml_man,ml_content,ml_result,ml_search,code) values("
				+ "sysdate,'"+SystemSession.getUser().getEm_name()+"','删除附件','删除成功:"+fileName+"',"
			    + "'"+caller+"|"+keyField+"="+keyvalue+"','"+code+"')");
	}
}

