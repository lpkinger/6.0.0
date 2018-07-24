package com.uas.erp.service.scm.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.PreSaleDao;
import com.uas.erp.service.scm.PreSaleService;

@Service
public class PreSaleServiceImpl implements PreSaleService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private PreSaleDao preSaleDao;
	@Override
	public void savePreSale(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("PreSale", "ps_code='" + store.get("ps_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PreSale", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		baseDao.logger.save(caller, "ps_id", store.get("ps_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}
	
	@Override
	public void deletePreSale(int ps_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("PreSale", "ps_statuscode", "ps_id=" + ps_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, ps_id);
		//删除PreSale
		baseDao.deleteById("PreSale", "ps_id", ps_id);		
		//记录操作
		baseDao.logger.delete(caller, "ps_id", ps_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, ps_id);
	}
	
	@Override
	public void updatePreSaleById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PreSale", "ps_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "ps_id", store.get("ps_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}
	@Override
	public void auditPreSale(int ps_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("PreSale", "ps_statuscode", "ps_id=" + ps_id);
		StateAssert.auditOnlyCommited(status);
		handlerService.beforeAudit(caller, ps_id);
		//执行反审核操作
		baseDao.audit("PreSale", "ps_id=" + ps_id, "ps_status", "ps_statuscode", "PS_AUDITDATE", "PS_AUDITMAN");
		//记录操作
		baseDao.logger.audit(caller, "ps_id", ps_id);
		handlerService.afterAudit(caller, ps_id);
	}
	@Override
	public void resAuditPreSale(int ps_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("PreSale", "ps_statuscode", "ps_id=" + ps_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, ps_id);
		//执行反审核操作
		baseDao.resOperate("PreSale", "ps_id=" + ps_id, "ps_status", "ps_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "ps_id", ps_id);
		handlerService.afterResAudit(caller, ps_id);
	}
	@Override
	public void submitPreSale(int ps_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("PreSale", "ps_statuscode", "ps_id=" + ps_id);
		StateAssert.submitOnlyEntering(status);
		handlerService.beforeSubmit(caller, ps_id);
		//执行反提交操作
		baseDao.submit("PreSale", "ps_id=" + ps_id, "ps_status", "ps_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "ps_id", ps_id);
		handlerService.afterSubmit(caller, ps_id);
	}
	@Override
	public void resSubmitPreSale(int ps_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("PreSale", "ps_statuscode", "ps_id=" + ps_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, ps_id);
		//执行反提交操作
		baseDao.resOperate("PreSale", "ps_id=" + ps_id, "ps_status", "ps_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "ps_id", ps_id);
		handlerService.afterResSubmit(caller, ps_id);
	}
	@Override
	public int turnSale(int ps_id, String caller) {
		int said = 0;
		//判断该单是否已经转入过销售单
		Object code = baseDao.getFieldDataByCondition("PreSale", "ps_code", "ps_id=" + ps_id);
		code = baseDao.getFieldDataByCondition("sale", "sa_code", "sa_source='" + code + "'");
		if(code != null && !code.equals("")){
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.presale.haveturn") + 
					"<a href=\"javascript:openUrl('jsps/scm/sale/sale.jsp?formCondition=sa_codeIS" + code + "&gridCondition=sd_codeIS" + code + "')\">" + code + "</a>&nbsp;");
		} else {
			//转销售
			said = preSaleDao.turnSale(ps_id);
			//修改报价单状态
			baseDao.updateByCondition("PreSale", "ps_statuscode='TURNSA',ps_status='" + 
					BaseUtil.getLocalMessage("TURNSA") + "'", "ps_id=" + ps_id);
			//记录操作
			baseDao.logger.turn("msg.turnSale", "PreSale", "ps_id", ps_id);
		}
		return said;
	}
	
	
	@Override
	public String turnPreSaleToSale(int ps_id, String type) {
		String url = "";
		//判断该单是否已经转入过销售单
		//ycsale   sale    nonsale
		if(type.equals("ycsale")){
			Object[] o = baseDao.getFieldsDataByCondition("SaleForecast", new String[]{"sf_code","sf_id"}, "sf_sourceid=" + ps_id);
			if(o != null && o.length==2){
				if(o[0] != null){
					BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.presale.haveturn") + 
							"<a href=\"javascript:openUrl('jsps/scm/sale/saleForecast.jsp?formCondition=sf_idIS" + o[1] + "&gridCondition=sd_sfidIS" + o[1] + "')\">" + o[0] + "</a>&nbsp;");
				}
			}
		}else if(type.equals("sale")){
			Object[] o = baseDao.getFieldsDataByCondition("Sale", new String[]{"sa_code","sa_id"}, "sa_source='正常' and sa_sourceid=" + ps_id);
			if(o != null && o.length==2){
				if(o[0] != null){
					BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.presale.haveturn") + 
							"<a href=\"javascript:openUrl('jsps/scm/sale/sale.jsp?whoami=Sale&formCondition=sa_idIS" + o[1] + "&gridCondition=sd_saidIS" + o[1] + "')\">" + o[0] + "</a>&nbsp;");
				}
			}
			
		}else if(type.equals("nonsale")){
			Object[] o = baseDao.getFieldsDataByCondition("Sale", new String[]{"sa_code","sa_id"}, "sa_source='非正常' and sa_sourceid=" + ps_id);
			if(o != null && o.length==2){
				if(o[0] != null){
					BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.presale.haveturn") + 
							"<a href=\"javascript:openUrl('jsps/scm/sale/sale.jsp?whoami=Sale!Abnormal&formCondition=sa_idIS" + o[1] + "&gridCondition=sd_saidIS" + o[1] + "')\">" + o[0] + "</a>&nbsp;");
				}
			}
			
		}
		
		
		//转销售			
		String res = baseDao.callProcedure("TR_SALE", new Object[]{ps_id,type,SystemSession.getUser().getEm_name()});
		if(res.equals("OK")){
			baseDao.updateByCondition("PreSale", "ps_statuscode='TURNSA',ps_status='" + 
					BaseUtil.getLocalMessage("TURNSA") + "'", "ps_id=" + ps_id);
			baseDao.logger.turn("msg.turnSale", "PreSale", "ps_id", ps_id);
			Object code = baseDao.getFieldDataByCondition("PreSale", "ps_source", "ps_id='"+ps_id+"'");
			
			if(type.equals("ycsale")){
				Object rid = baseDao.getFieldDataByCondition("SaleForecast", "sf_id", "sf_code='"+code+"'");
				
				url = "转入销售预测单成功,销售预测单号:" +
						"<a href=\"javascript:openUrl('jsps/scm/sale/saleForecast.jsp?formCondition=sf_codeIS" + code + "&gridCondition=sd_sfidIS"+rid+"')\">" + code + "</a>&nbsp;";
			}else if (type.equals("sale")){
				Object rid = baseDao.getFieldDataByCondition("Sale", "sa_id", "sa_code='"+code+"'");
				url = "转入销售单成功,销售单号:" +
						"<a href=\"javascript:openUrl('jsps/scm/sale/sale.jsp?whoami=Sale&formCondition=sa_codeIS" + code + "&gridCondition=sd_saidIS"+rid+"')\">" + code + "</a>&nbsp;";
			}else if (type.equals("nonsale")){
				Object rid = baseDao.getFieldDataByCondition("Sale", "sa_id", "sa_code='"+code+"'");
				url = "转入非正常销售单成功,非正常销售单号:" +
						"<a href=\"javascript:openUrl('jsps/scm/sale/sale.jsp?whoami=Sale!Abnormal&formCondition=sa_codeIS" + code + "&gridCondition=sd_saidIS"+rid+"')\">" + code + "</a>&nbsp;";
			}
//			sb.append("转入成功,应付票据号:"
//					+ "<a href=\"javascript:openUrl('jsps/fa/gs/billAP.jsp?whoami=BillAP&formCondition=bap_idIS"
//					+ code.getInt("bap_id") + code.getString("bap_code") + "</a>&nbsp;");
			
		}else{
			BaseUtil.showError(res);
		}

		return url;
	}
	
	@Override
	public Map<String, Object> getOtherPreSaleValues(int ps_id) {
		Map<String ,Object > map = new HashMap<String, Object>();
		String[] fields = getFieldsName();
		String[] ss = baseDao.getStringFieldsDataByCondition("PreSale", fields, "ps_id="+ps_id);
		for(int i=0;i<fields.length;i++){
			if(ss[i] != null){
				if(!ss[i].toString().equals("null")&&!ss[i].toString().equals(""))
					map.put(fields[i], ss[i]);
			}
		}
		return map;
	}
	
	private String[] getFieldsName(){
		String[] s = new String[92]; //68+24
		int rg = 1;
		int ta = 1;
		int tf = 1;
		int index = 0;
		for(int i =0 ; i <18;i++){
			
			s[index] = "rg_"+rg;
			index ++;
			rg++;
			s[index] = "ta_"+ta;
			index ++;
			ta++;
			s[index] = "tf_"+tf;
			index ++;
			tf++;
		}
		for(int i = 54 ; i < 68; i++){
			s[index] = "tf_"+tf;
			index ++;
			tf ++;
		}
		for(int i =33 ; i <=40;i++){
			
			s[index] = "rg_"+tf;
			index ++;
			s[index] = "ta_"+tf;
			index ++;
			s[index] = "tf_"+tf;
			index ++;
			tf++;
		}
		return s;
		
	}
}
