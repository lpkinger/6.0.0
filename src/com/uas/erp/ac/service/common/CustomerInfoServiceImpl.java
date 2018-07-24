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
import com.uas.b2b.model.Customer;
import com.uas.b2b.model.PageInfo;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Master;
import com.uas.sso.support.Page;

@Service
public class CustomerInfoServiceImpl implements CustomerInfoService {

	@Autowired
	private BaseDao baseDao;

	@Override
	public Map<String, Object> customers(String keyword, Integer pageNumber, Integer pageSize) throws Exception {
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
		Response response = HttpUtil.sendGetRequest(b2burl+"/erp/userSpaceDetail/customer?access_id=" + master.getMa_uu(), params, true,
				master.getMa_accesssecret());
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			String data = response.getResponseText();
			if (StringUtil.hasText(data)) {
				Page<Customer> details = JSONObject.parseObject(data,
						new TypeReference<Page<Customer>>() {
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
	public Map<String, Object> getCustomerData(String caller, String condition) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Customer> customers = new ArrayList<Customer>();
		String sql = "select * from customer where cu_uu is null and cu_auditstatus='已审核'";
		if(condition!=null &&!"".equals(condition)){
			sql = sql+"and "+condition;
		}
		SqlRowList rs1 = baseDao.queryForRowSet(sql);
		while(rs1.next()){
			Customer customer = new Customer();
			customer.setCu_id(rs1.getInt("cu_id"));
			customer.setCu_code(rs1.getString("cu_code"));
			customer.setCu_name(rs1.getString("cu_name"));
			customer.setCu_add1(rs1.getString("cu_add1"));
			customer.setCu_contact(rs1.getString("cu_contact"));
			customer.setCu_tel(rs1.getString("cu_tel"));
			customer.setCu_shortname(rs1.getString("cu_shortname"));
			customer.setCu_businesscode(rs1.getString("cu_businesscode"));
			customer.setCu_uu(rs1.getString("cu_uu"));
			customer.setB2b(0);
			customers.add(customer);
		}
		map.put("data", customers);
		return map;
	}

	@Override
	public void updateCustomerData(String id, String uu) {
		baseDao.updateByCondition("customer", "cu_uu='"+(uu==null?"":uu)+"'", "cu_id='"+id+"' and cu_uu is null");
	}

	@Override
	public Map<String, Object> customerUse(Integer id, Integer hasRelative,Integer type, String vendUID) throws Exception {
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
			Response response = HttpUtil.sendPostRequest(b2burl+"/erp/userSpaceDetail/activeCust?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				map.put("success", true);
			}
		}
		if(hasRelative==1&&type==0){
			Response response = HttpUtil.sendPostRequest(b2burl+"/erp/userSpaceDetail/disableCust?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				map.put("success", true);
			}
		}
		if(hasRelative==0){
			Response response = HttpUtil.sendPostRequest(b2burl+"/erp/userSpaceDetail/addCust?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				map.put("success", true);
			}
		}
		return map;
	}

	@Override
	public Map<String, Object> erpCustomers(String keyword, Integer page,
			Integer limit) {
		Map<String, Object> map = new HashMap<String, Object>();
		String countSql = "select count(1) from customer where CU_AUDITSTATUS='已审核' and nvl(cu_uu,0)=0";
		String querySql = "select * from (select t.*,rownum r from (select cu_id,cu_code,cu_name,cu_shortname,cu_webserver,cu_uu,cu_contact,cu_lawman,cu_email,cu_mobile,cu_tel,cu_businesscode,cu_add1 from customer where CU_AUDITSTATUS='已审核' and nvl(cu_uu,0)=0";
		Object[] args = new Object[] {};
		if (null != keyword) {
			countSql = countSql + " and cu_name like '%" + keyword + "%'";
			querySql = querySql + " and cu_name like '%" + keyword + "%'";
		}
		int total = count(countSql.toString(), args);

		querySql = querySql + " order by cu_id) t where rownum <= " + page * limit + ") where r >= "
				+ ((page - 1) * limit + 1);
		/*args = Arrays.copyOf(args, args.length + 2);
		args[args.length - 2] = page * limit;
		args[args.length - 1] = (page - 1) * limit + 1;*/
		List<Customer> content = null;
		try {
			content = baseDao.query(querySql, Customer.class);
		} catch (EmptyResultDataAccessException e) {
			
		}
		map.put("data", content);
		map.put("count", total);
		return map;
	}
}