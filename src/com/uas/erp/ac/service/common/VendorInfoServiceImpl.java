package com.uas.erp.ac.service.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.uas.sso.support.Page;
import com.uas.b2b.model.PageInfo;
import com.uas.b2b.model.Vendor;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Master;

@Service
public class VendorInfoServiceImpl implements VendorInfoService {

	@Autowired
	private BaseDao baseDao;

	@Override
	public Map<String, Object> vendors(String keyword, Integer pageNumber, Integer pageSize) throws Exception{
		Master master = SystemSession.getUser().getCurrentMaster();
		PageInfo pageInfo = new PageInfo();
		pageInfo.setKeyword(keyword);
		pageInfo.setPageNumber(pageNumber);
		pageInfo.setPageSize(pageSize);
		HashMap<String, String> params = new HashMap<String, String>();
		Map<String, Object> map = new HashMap<String, Object>();
		params.put("pageInfo", FlexJsonUtil.toJsonDeep(pageInfo));
		String b2burl = null;
		if (master.getMa_b2bwebsite() == null || master.getMa_b2bwebsite().equals("null")) {
			b2burl = "http://uas.ubtob.com";
		} else {
			b2burl = master.getMa_b2bwebsite();
		}
		Response response = HttpUtil.sendGetRequest(b2burl+"/erp/userSpaceDetail/vendor?access_id=" + master.getMa_uu(), params, true,
				master.getMa_accesssecret());
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			String data = response.getResponseText();
			if (StringUtil.hasText(data)) {
				Page<Vendor> details = JSONObject.parseObject(data,
						new TypeReference<Page<Vendor>>() {
						});
				map.put("data", details.getContent());
				map.put("count", details.getTotalElements());
			}
		}
		return map;
	}

	protected int count(String sql, Object... args) {
		return baseDao.queryForObject(!sql.startsWith("select") ? ("select count(1) " + sql) : sql, Integer.class,
				args);
	}

	@Override
	public Map<String, Object> getVendorData(String caller, String condition) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Vendor> vendors = new ArrayList<Vendor>();
		String sql = "select * from vendor where ve_uu is null and ve_auditstatus='已审核'";
		if(condition!=null &&!"".equals(condition)){
			sql = sql+"and "+condition;
		}
		SqlRowList rs1 = baseDao.queryForRowSet(sql);
		while(rs1.next()){
			Vendor vendor = new Vendor();
			vendor.setVe_id(rs1.getInt("ve_id"));
			vendor.setVe_code(rs1.getString("ve_code"));
			vendor.setVe_name(rs1.getString("ve_name"));
			vendor.setVe_add1(rs1.getString("ve_add1"));
			vendor.setVe_contact(rs1.getString("ve_contact"));
			vendor.setVe_tel(rs1.getString("ve_tel"));
			vendor.setVe_shortname(rs1.getString("ve_shortname"));
			vendor.setVe_webserver(rs1.getString("ve_webserver"));
			vendor.setVe_uu(rs1.getString("ve_uu"));
			vendor.setB2b(0);
			vendors.add(vendor);
		}
		map.put("data", vendors);
		return map;
	}

	@Override
	public void updateVendorData(String id, String uu) {
		baseDao.updateByCondition("vendor", "ve_uu='"+(uu==null?"":uu)+"'", "ve_id='"+id+"' and ve_uu is null");
	}

	@Override
	public ModelMap vendUse(Integer id, Integer hasRelative, Integer type,String vendUID) throws Exception {
		Master master = SystemSession.getUser().getCurrentMaster();
		HashMap<String, String> params = new HashMap<String, String>();
		ModelMap map = new ModelMap();
		String b2burl = null;
		if (master.getMa_b2bwebsite() == null || master.getMa_b2bwebsite().equals("null")) {
			b2burl = "http://uas.ubtob.com";
		} else {
			b2burl = master.getMa_b2bwebsite();
		}
		/**
		 * 如果已经建立关系，则调用启用或者禁用接口，否则调用添加供应商接口
		 * hasRelative -  1:已经建立关系     0:未建立关系
		 * type -   1:启用   0:禁用
		 */
		if(hasRelative==1){
			params.put("id",id.toString());
		}else {
			params.put("businessCode",vendUID);
		}
		if(hasRelative==1&&type==1){
			Response response = HttpUtil.sendPostRequest(b2burl+"/erp/userSpaceDetail/activeVend?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				map.put("success", true);
			}
		}
		if(hasRelative==1&&type==0){
			Response response = HttpUtil.sendPostRequest(b2burl+"/erp/userSpaceDetail/relieve?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				map.put("success", true);
			}
		}
		if(hasRelative==0){
			Response response = HttpUtil.sendPostRequest(b2burl+"/erp/userSpaceDetail/addSupplier?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				map.put("success", true);
			}
		}
		return map;
	}

	@Override
	public Map<String, Object> erpVendors(String keyword, Integer page,
			Integer limit) {
		Map<String, Object> map = new HashMap<String, Object>();
		String countSql = "select count(1) from vendor where VE_AUDITSTATUS='已审核' and nvl(ve_uu,' ')=' '";
		String querySql = "select * from (select t.*,rownum r from (select ve_id,ve_code,ve_name,ve_shortname,ve_webserver,ve_uu,ve_contact,ve_legalman,ve_email,ve_mobile,ve_tel,ve_add1 from vendor where VE_AUDITSTATUS='已审核' and nvl(ve_uu,' ')=' '";
		Object[] args = new Object[] {};
		if (null != keyword) {
			countSql = countSql + " and ve_name like '%" + keyword + "%'";
			querySql = querySql + " and ve_name like '%" + keyword + "%'";
		}
		int total = count(countSql.toString(), args);

		querySql = querySql + " order by ve_id) t where rownum <= " + page * limit + ") where r >= "
				+ ((page - 1) * limit + 1);
		/*args = Arrays.copyOf(args, args.length + 2);
		args[args.length - 2] = page * limit;
		args[args.length - 1] = (page - 1) * limit + 1;*/
		List<Vendor> content = null;
		try {
			content = baseDao.query(querySql, Vendor.class);
		} catch (EmptyResultDataAccessException e) {

		}
		map.put("data", content);
		map.put("count", total);
		return map;
	}

	@Override
	public Map<String, Object> services(String keyword, Integer page,
			Integer limit) {
		Master master = SystemSession.getUser().getCurrentMaster();
		PageInfo pageInfo = new PageInfo();
		pageInfo.setKeyword(keyword);
		pageInfo.setPageNumber(page);
		pageInfo.setPageSize(limit);
		HashMap<String, String> params = new HashMap<String, String>();
		Map<String, Object> map = new HashMap<String, Object>();
		params.put("pageInfo", FlexJsonUtil.toJsonDeep(pageInfo));
		String b2burl = null;
		if (master.getMa_b2bwebsite() == null || master.getMa_b2bwebsite().equals("null")) {
			b2burl = "http://uas.ubtob.com";
		} else {
			b2burl = master.getMa_b2bwebsite();
		}
		Response response;
		try {
			response = HttpUtil.sendGetRequest(b2burl+"/erp/userSpaceDetail/servicer?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					Page<Vendor> details = JSONObject.parseObject(data,
							new TypeReference<Page<Vendor>>() {
							});
					map.put("data", details.getContent());
					map.put("count", details.getTotalElements());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//int count = 0;
		return map;
	}

	@Override
	public Map<String, Object> serviceUse(Integer id, Integer hasRelative,
			Integer type, String vendUID) {
		Master master = SystemSession.getUser().getCurrentMaster();
		HashMap<String, String> params = new HashMap<String, String>();
		ModelMap map = new ModelMap();
		String b2burl = null;
		if (master.getMa_b2bwebsite() == null || master.getMa_b2bwebsite().equals("null")) {
			b2burl = "http://uas.ubtob.com";
		} else {
			b2burl = master.getMa_b2bwebsite();
		}
		/**
		 * 如果已经建立关系，则调用启用或者禁用接口，否则调用添加供应商接口
		 * hasRelative -  1:已经建立关系     0:未建立关系
		 * type -   1:启用   0:禁用
		 */
		if(hasRelative==1){
			params.put("id",id.toString());
		}else {
			params.put("businessCode",vendUID);
		}
		if(hasRelative==1&&type==1){
			Response response;
			try {
				response = HttpUtil.sendPostRequest(b2burl+"/erp/userSpaceDetail/activeServicer?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					map.put("success", true);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(hasRelative==1&&type==0){
			Response response;
			try {
				response = HttpUtil.sendPostRequest(b2burl+"/erp/userSpaceDetail/relieveServicer?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					map.put("success", true);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(hasRelative==0){
			Response response;
			try {
				response = HttpUtil.sendPostRequest(b2burl+"/erp/userSpaceDetail/addServicer?access_id=" + master.getMa_uu(), params, true,
						master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					map.put("success", true);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return map;
	}
}
