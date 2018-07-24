package com.uas.erp.service.scm.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Master;
import com.uas.erp.service.scm.CustomerService;

@Service("customerService")
public class CustomerServiceImpl implements CustomerService{
	
	@Autowired
	private BaseDao baseDao;
	@Override
	public void saveCustomer(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		///当前编号的记录已经存在,不能新增
		boolean bool = baseDao.checkByCondition("Customer", "cu_code='" + store.get("cu_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.common.save_codeHasExist"));
		}
		if(store.get("cu_id") == null || store.get("cu_id").equals("") || store.get("cu_id").equals("0") || 
				Integer.parseInt(store.get("cu_id").toString()) == 0){
			store.put("cu_id", baseDao.getSeqId("CUSTOMER_SEQ"));
		}
		//执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Customer", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save(caller, "cu_id", store.get("cu_id"));;
	}
	@Override
	public void updateCustomer(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		if(store.get("cu_id") == null || store.get("cu_id").equals("") || store.get("cu_id").equals("0") || 
				Integer.parseInt(store.get("cu_id").toString()) == 0){
			saveCustomer(formStore, caller);
		} else {
			//只能修改[在录入]的客户资料
			Object status = baseDao.getFieldDataByCondition("Customer", "cu_auditstatuscode", "cu_id=" + store.get("cu_id"));
			StateAssert.updateOnlyEntering(status);
			//执行修改操作
			String sql = SqlUtil.getUpdateSqlByFormStore(store, "Customer", "cu_id");
			baseDao.execute(sql);
			//记录操作
			baseDao.logger.update(caller, "cu_id", store.get("cu_id"));;
		}
	}
	@Override
	public void updateUU(Integer id, String uu, String caller,String cu_businesscode, String cu_lawman, String cu_add1) {
		// TODO Auto-generated method stub
		int countnum=baseDao.getCount("select count(*) from customer where cu_uu=replace('" + uu + "',' ','')  and cu_id<>"+id);
		if(countnum>0){
		   BaseUtil.showError("UU号已经存在客户资料!");
		}
		cu_businesscode = StringUtil.hasText(cu_businesscode)?cu_businesscode:"";
		cu_lawman = StringUtil.hasText(cu_lawman)?cu_lawman:"";
		cu_add1 = StringUtil.hasText(cu_add1)?cu_add1:"";
		//baseDao.updateByCondition("Customer", "cu_uu=replace('" + uu + "',' ','') ,cu_b2benable=1", "cu_id=" + id);
		//去平台建立供应关系
		String error=relationship(uu);
		if(error!=null){
			BaseUtil.showError(error);
		}else{
			baseDao.updateByCondition("Customer", "cu_uu=replace('" + uu + "',' ','') ,cu_b2benable=1,cu_businesscode='"+cu_businesscode+"',cu_lawman='"+cu_lawman+"',cu_add1='"+cu_add1+"'", "cu_id=" + id);
		}
		// 维护UU号的同时需要更新对应的PO上传状态		
		baseDao.logger.others("修改UU号", "msg.updateSuccess", "Customer", "cu_id", id);
	}
	
	public String relationship(String custuu) {
		Master master = SystemSession.getUser().getCurrentMaster();
		HashMap<String, String> params = new HashMap<String, String>();
		if (master.getMa_uu() > 0) {
			params.put("otheruu", custuu.toString());
			try {
				Response response = HttpUtil.sendPostRequest(
						master.getMa_b2bwebsite()
								+ "/erp/relationship?access_id="
								+ master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					Map<String, Object> backInfo = FlexJsonUtil.fromJson(
							response.getResponseText(), HashMap.class);
					if (backInfo.get("ok").equals(true)) {

					} else {
						return backInfo.get("error").toString();
					}
				}
			} catch (Exception e) {

			}
		}
		return null;
	}
	@Override
	public Map<String, Object> getCustLabelCode(String condition) {
		SqlRowList rs = baseDao.queryForRowSet("select distinct lps_caller,lps_labelurl,lps_statuscode from prodinout left join customer on cu_code=pi_cardcode left join labelPrintsetting on lps_code=cu_labelcode where pi_id in("+condition+")");
		if(rs.next()){
			if(rs.size()>1){
			 BaseUtil.showError("选择的出货单，存在不同的默认标签格式，不能打印");
			}
			if(rs.size() == 1 && rs.getObject("lps_statuscode")== null){
				BaseUtil.showError("出货单不存在默认标签模板");
			}else if(!rs.getString("lps_statuscode").equals("AUDITED")){
				BaseUtil.showError("模板标签：["+rs.getString("lps_code")+",未审核!]");
			}
		}else{
			BaseUtil.showError("出货单不存在默认标签模板");
		}
		return rs.getCurrentMap();
	}
}
