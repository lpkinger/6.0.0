package com.uas.erp.service.scm.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.uas.b2b.core.PSHttpUtils;
import com.uas.b2b.model.Product;
import com.uas.b2b.model.VendorRecommend;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Master;
import com.uas.erp.service.scm.VendorImportFromB2BService;
import com.uas.sso.support.Page;

@Service
public class VendorImportFromB2BServiceImpl implements VendorImportFromB2BService{

	@Autowired
	BaseDao baseDao;
	@Override
	public Map<String, Object> getVendorImportFromB2B(String caller,String condition, Integer start, Integer page, Integer pageSize) {
		
		Map<String, Object> res = new HashMap<String, Object>();
		Master master = SystemSession.getUser().getCurrentMaster();
		String b2burl = null;
		if (master.getMa_b2bwebsite() == null || "".equals(master.getMa_b2bwebsite())) {
			b2burl = "http://uas.ubtob.com";
		} else {
			b2burl = master.getMa_b2bwebsite();
		}
		b2burl += "/public/recommend/list";
		Response response;
		HashMap<String, String> params = new HashMap<String, String>();
		if(condition!=null){
			condition = condition.replace("#","\\");
			Map<Object, Object> map = BaseUtil.parseFormStoreToMap(condition);
			if(map!=null&&map.size()>0){
				if(!ifnull(map.get("enterpriseMatchCondition"))){
					params.put("enterpriseMatchCondition",map.get("enterpriseMatchCondition").toString());
				}
				if(!ifnull(map.get("productMatchCondition"))){
					params.put("productMatchCondition", map.get("productMatchCondition").toString());
				}
			}
		}
		Object enUU = master.getMa_uu();
		if(StringUtil.hasText(enUU)){
			params.put("enUU", enUU.toString());
		}else params.put("enUU","");
		params.put("page", page.toString());
		params.put("size", pageSize.toString());
		try {
			response = HttpUtil.sendGetRequest(b2burl, params, false, null);
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String vendorRecommenDdata = response.getResponseText();
				if (StringUtil.hasText(vendorRecommenDdata)) {
					 Page<VendorRecommend> vendorRecommendPage = JSONObject.parseObject(vendorRecommenDdata,
								new TypeReference<Page<VendorRecommend>>() {});
					 res.put("count", vendorRecommendPage.getTotalElements());
					 res.put("data", vendorRecommendPage.getContent());
				}
			}else{
				if(StringUtils.isEmpty(response.getResponseText()))
					BaseUtil.showError("程序错误。错误码："+response.getStatusCode());
				else BaseUtil.showError(response.getResponseText());
			}
		}catch (Exception e) {
 			e.printStackTrace();
		}
		return res;
	}
	private boolean  ifnull(Object o){
		if(o == null){
			return true;
		}else if(o.toString().replaceAll(" ", "").equals("1=1")||o.toString().replaceAll(" ", "").equals("")){
			return true;
		}else return false;
	}
	@Override
	public Map<String, Object> importVendorFromB2B(String caller, String formStore) {
		//delete_
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(formStore);
		Map<String, Object> res = new HashMap<String, Object>();
		int ve_id =  baseDao.getSeqId("PURCHASENOTIFY_SEQ");
		String ve_code = baseDao.sGetMaxNumber("PreVendor",2);
		map.put("ve_id", ve_id);
		map.put("ve_code", ve_code);
		map.put("ve_auditstatuscode", "ENTERING");
		map.put("ve_auditstatus", "在录入");
		map.put("ve_name", map.get("en_name"));
		map.put("ve_uu", map.get("en_uu"));
		map.put("ve_shortname", map.get("en_shortname"));
		map.put("ve_add1", map.get("en_address"));
		map.put("ve_tel", map.get("en_tel"));
		map.put("ve_email", map.get("en_email"));
		map.put("ve_legalman", map.get("en_corporation"));
		map.put("ve_webserver", map.get("en_businesscode"));
		map.put("ve_businessrange", map.get("en_tags"));
		map.put("ve_industry", map.get("en_profession"));
		map.put("ve_contact", map.get("en_contactman"));
		map.put("ve_mobile", map.get("en_contacttel"));
		map.put("ve_currency", map.get("en_currency"));
		
		try{
			baseDao.execute(SqlUtil.getInsertSqlByMap(map, "PreVendor"));	
		}catch(Exception e){
			BaseUtil.showError(e.getMessage());
		}
		res.put("success", true);
		res.put("log", "引入供应商成功，单号：<a href=\"javascript:openUrl('PreVendor','供应商引进','/jsps/scm/purchase/preVendor.jsp?whoami=PreVendor&formCondition=ve_idIS"+ve_id+"&gridCondition=nullIS" +ve_id+"')\">" + ve_code + "</a> &nbsp; ");
		return res;
	}
	@Override
	public Map<String, Object> getVendorImpoertProdDetail(String caller, String en_uu, String productMatchCondition, String whereCondition,Integer start, Integer page, Integer pageSize) {
		Map<String, Object> res = new HashMap<String, Object>();
		Master master = SystemSession.getUser().getCurrentMaster();
		JSONObject params = new JSONObject();
		if(!StringUtils.isEmpty(whereCondition)){
			params.put("whereCondition",whereCondition);
		}else{
			params.put("whereCondition","1=1");
		}
		String b2burl = null;
		if (master.getMa_b2bwebsite() == null || "".equals(master.getMa_b2bwebsite())) {
			b2burl = "http://uas.ubtob.com";
		} else {
			b2burl = master.getMa_b2bwebsite();
		}
		b2burl += "/public/recommend/detail";
		Object enUU = master.getMa_uu();
		if(StringUtil.hasText(enUU)){
			params.put("enUU", enUU.toString());
		}else params.put("enUU","");
		params.put("vendUU", en_uu);
		params.put("page", page.toString());
		params.put("size", pageSize.toString());
		
		try {
			com.uas.b2b.core.PSHttpUtils.Response response = PSHttpUtils.sendGetRequest(b2burl, params);
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String vendorProductdata = response.getResponseText();
				if (StringUtil.hasText(vendorProductdata)) {
					 Page<Product> ProductPage = JSONObject.parseObject(vendorProductdata,
								new TypeReference<Page<Product>>() {});
					 res.put("count", ProductPage.getTotalElements());
					 res.put("data", ProductPage.getContent());
				}
			}else{
				if(StringUtils.isEmpty(response.getResponseText()))
					BaseUtil.showError("程序错误。错误码："+response.getStatusCode());
				else BaseUtil.showError(response.getResponseText());
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	@Override
	public Map<String, Object> getVendorFormB2B(String caller, String field,String condition,String enUU) {
		Map<String, Object> res = new HashMap<String, Object>();
		Master master = SystemSession.getUser().getCurrentMaster();
		JSONObject params = new JSONObject();
		if(!StringUtils.isEmpty(condition)){
			params.put("condition",condition);
		}else{
			params.put("condition","1=1");
		}
		String b2burl = null;
		if (master.getMa_b2bwebsite() == null || "".equals(master.getMa_b2bwebsite())) {
			b2burl = "http://uas.ubtob.com";
		} else {
			b2burl = master.getMa_b2bwebsite();
		}
		b2burl += "/public/recommend/product/associate"; 
		if(StringUtils.isEmpty(field)){
			field = "pr_brand";
		}
		params.put("field", field);
		try {
			com.uas.b2b.core.PSHttpUtils.Response response = PSHttpUtils.sendGetRequest(b2burl, params);
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					 List<Product> products = JSONObject.parseObject(data,
							new TypeReference<List<Product>>() {});
					 res.put("data", products);
					 res.put("count", products.size());
				}
			}else{
				if(StringUtils.isEmpty(response.getResponseText()))
					BaseUtil.showError("程序错误。错误码："+response.getStatusCode());
				else BaseUtil.showError(response.getResponseText());
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
}
