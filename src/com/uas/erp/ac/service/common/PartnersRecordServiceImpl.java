package com.uas.erp.ac.service.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.JsonObject;
import com.uas.b2b.model.InvitationRecord;
import com.uas.b2b.model.PageInfo;
import com.uas.erp.controller.ac.model.BasePartnersInfo;
import com.uas.erp.controller.ac.model.UserSpaceDetail;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.sso.entity.PartnershipRecordView;
import com.uas.sso.entity.RequestStatus;
import com.uas.sso.entity.UserSpaceView;
import com.uas.sso.support.Page;
import com.uas.sso.util.AccountUtils;

@Service
public class PartnersRecordServiceImpl implements PartnersRecordService {

	@Autowired
	private EnterpriseService enterpriseService;

	@Autowired
	private BaseDao baseDao;

	@Override
	public Map<String, Object> getUserSpaceDetails(String keyword, Integer start, Integer pageNumber, Integer pageSize) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		Master master = SystemSession.getUser().getCurrentMaster();
		PageInfo pageInfo = new PageInfo();
		pageInfo.setKeyword(keyword);
		pageInfo.setPageNumber(pageNumber);
		pageInfo.setPageSize(pageSize);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", FlexJsonUtil.toJsonDeep(pageInfo));
		String b2burl = null;
		if (master.getMa_b2bwebsite() == null || master.getMa_b2bwebsite().equals("null")) {
			b2burl = "http://uas.ubtob.com";
		} else {
			b2burl = master.getMa_b2bwebsite();
		}
		Response response = HttpUtil.sendGetRequest(b2burl+"/erp/userSpaceDetail?access_id=" + master.getMa_uu(), params, true,
				master.getMa_accesssecret());
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			String data = response.getResponseText();
			if (StringUtil.hasText(data)) {
				Page<UserSpaceView> details = JSONObject.parseObject(data,
						new TypeReference<Page<UserSpaceView>>() {
						});
				List<UserSpaceDetail> spaceDetails = new ArrayList<UserSpaceDetail>();
				if(details.getContent()!=null){
					for (UserSpaceView detail : details.getContent()) {
						UserSpaceDetail spacetDetail = new UserSpaceDetail();
						spacetDetail.setAddress(detail.getRegAddress());
						spacetDetail.setAdminEmail(detail.getAdmin().getEmail());
						spacetDetail.setAdminName(detail.getAdmin().getVipName());
						spacetDetail.setAdminTel(detail.getAdmin().getMobile());
						spacetDetail.setArea(detail.getArea());
						spacetDetail.setBusinessCode(detail.getBusinessCode());
						spacetDetail.setBusinessCodeImage(detail.getBusinessCodeImage());
						spacetDetail.setCorporation(detail.getCorporation());
						spacetDetail.setLogoImage(detail.getLogoImage());
						spacetDetail.setName(detail.getSpaceName());
						spacetDetail.setRegisterDate(detail.getRegisterDate());
						spacetDetail.setProfession(detail.getProfession());
						spacetDetail.setTags(detail.getTags());
						spacetDetail.setUu(detail.getSpaceUU());
						RequestStatus request = AccountUtils.getStatusByCustUidAndVendUid(spacetDetail.getBusinessCode(), enterpriseService
								.getEnterprise().getEn_Businesscode());
						if (request != null) {
							spacetDetail.setRequestStatus(request.getStatusCode());
							spacetDetail.setMethod(request.getMethod());
						}
						spaceDetails.add(spacetDetail);
					
			}
				}
				map.put("count", details.getTotalElements());
				map.put("data", spaceDetails);
			}
		}
		return map;
	}

	@Override
	public Map<String, Object> getAllPartnersInfosByBusinessCode(String keyword, Integer start, Integer pageNumber, Integer pageSize,
			Integer statusCode) throws Exception {
		ModelMap map = new ModelMap();
		PageInfo pageInfo = new PageInfo();
		pageInfo.setKeyword(keyword);
		pageInfo.setPageNumber(pageNumber);
		pageInfo.setPageSize(pageSize);
		Master master = SystemSession.getUser().getCurrentMaster();
		String b2burl = null;
		Page<BasePartnersInfo> bPage = new Page<BasePartnersInfo>();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", FlexJsonUtil.toJsonDeep(pageInfo));
		if (master.getMa_b2bwebsite() == null || master.getMa_b2bwebsite().equals("null") ) {
			b2burl = "http://uas.ubtob.com";
		} else {
			b2burl = master.getMa_b2bwebsite();
		}
		Response response = HttpUtil.sendGetRequest(b2burl+"/erp/userSpaceDetail/request?access_id=" + master.getMa_uu(), params, true,
				master.getMa_accesssecret());
		List<BasePartnersInfo> baseInfos = new ArrayList<BasePartnersInfo>();
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			String data = response.getResponseText();
			if (StringUtil.hasText(data)) {
				bPage = JSONObject.parseObject(data,
						new TypeReference<Page<BasePartnersInfo>>() {
						});
				if(bPage.getContent()!=null){
					baseInfos.addAll(bPage.getContent());
				}
			}
		}
		map.put("count", (bPage==null?0:bPage.getTotalElements()));
		map.put("data", baseInfos);
		return map;
	}

	@Override
	public Map<String, Object> invite(String formStore) throws Exception {
		Master master = SystemSession.getUser().getCurrentMaster();
		String b2burl = null;
		Map<String, Object> map = new HashMap<String, Object>();
		InvitationRecord inRecord = JSON.parseObject(formStore,InvitationRecord.class);
		inRecord.setUseruu(SystemSession.getUser().getEm_uu());
		inRecord.setEnuu(master.getMa_uu());
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", FlexJsonUtil.toJsonDeep(inRecord));
		if (master.getMa_b2bwebsite() == null || master.getMa_b2bwebsite().equals("null")) {
			b2burl = "http://uas.ubtob.com";
		} else {
			b2burl = master.getMa_b2bwebsite();
		}
		Response response = HttpUtil.sendPostRequest(b2burl+"/erp/vendor/invite?access_id=" + master.getMa_uu(), params, true,
				master.getMa_accesssecret());
		if (response.getStatusCode()!=500) {
			map.put("success", true);
		}
		return map;
	}

	@Override
	public Map<String, Object> getNewPartners(String keyword, Integer start,
			Integer pageNumber, Integer pageSize, Integer statusCode) throws Exception {
		ModelMap map = new ModelMap();
		String businessCode = enterpriseService.getEnterprise().getEn_Businesscode();
		Page<PartnershipRecordView> pageRecords = AccountUtils.getAllRequest(businessCode, statusCode, keyword, pageNumber, pageSize);
		List<PartnershipRecordView> records = pageRecords.getContent();
		List<BasePartnersInfo> baseInfos = new ArrayList<BasePartnersInfo>();
		if (!CollectionUtils.isEmpty(records)) {
			for (PartnershipRecordView record : records) {
				BasePartnersInfo partner = new BasePartnersInfo();
				if (record.getCustUID().equals(businessCode)) {
					partner.setRequestDate(record.getRequestDate());
					partner.setAppId(record.getAppId());
					partner.setId(record.getId());
					partner.setOperateDate(record.getOperateDate());
					partner.setVendName(record.getVendName());					
					partner.setVendUID(record.getVendUID());
					partner.setVendUserCode(record.getVendUserCode().toString());
					partner.setVendUserName(record.getVendUserName());
					partner.setVendUserTel(record.getVendUserTel());
					partner.setMethod(Constant.YES);
					partner.setStatusCode(record.getStatusCode());
					partner.setCustUserName(record.getCustUserName());
					partner.setCustUserTel(record.getCustUserTel());
					partner.setReason(record.getReason());
					baseInfos.add(partner);
				}
				if (record.getVendUID().equals(businessCode)) {
					partner.setRequestDate(record.getRequestDate());
					partner.setAppId(record.getAppId());
					partner.setOperateDate(record.getOperateDate());
					partner.setId(record.getId());
					partner.setVendName(record.getCustName());
					partner.setVendUID(record.getCustUID());
					partner.setVendUserCode(record.getVendUserCode().toString());
					partner.setVendUserName(record.getVendUserName());
					partner.setVendUserTel(record.getVendUserTel());
					partner.setMethod(Constant.NO);
					partner.setStatusCode(record.getStatusCode());
					partner.setCustUserName(record.getCustUserName());
					partner.setCustUserTel(record.getCustUserTel());
					partner.setReason(record.getReason());
					baseInfos.add(partner);
				}
			}
		}
		map.put("count", pageRecords.getTotalElements());
		map.put("data", baseInfos);
		return map;
	}
	@Override
	public Map<String, Object> sync() {
		Map<String,Object> map = new HashMap<String, Object>();
		Master master = SystemSession.getUser().getCurrentMaster();
		HashMap<String, String> params = new HashMap<String, String>();
		String b2burl = null;
		params.put("enUU",master.getMa_uu().toString());
		if (master.getMa_b2bwebsite() == null || master.getMa_b2bwebsite().equals("null")) {
			b2burl = "http://uas.ubtob.com";
		} else {
			b2burl = master.getMa_b2bwebsite();
		}
		try {
			Response response = HttpUtil.sendGetRequest(b2burl+"/erp/userSpaceDetail/synchronize?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					Map<Object, Object> maps = FlexJsonUtil.fromJson(data);
					List<Map<Object, Object>> listVendors = (List<Map<Object, Object>>) maps.get("B2BVendors");
					List<Map<Object, Object>> listCustomers = (List<Map<Object, Object>>) maps.get("B2BCustomers");
					List<String> listSql = new ArrayList<String>();
					String sql = "";
					String code="";
					int id ;
					for(Map<Object, Object> m : listVendors){
						boolean bool = baseDao.checkIf("vendor", "ve_uu='"+m.get("ve_uu")+"'");
						if(!bool){
							code = baseDao.sGetMaxNumber("vendor", 2);
							id = baseDao.getSeqId("VENDOR_SEQ");
							sql = "insert into vendor(ve_id,ve_code,ve_name,ve_shortname,ve_webserver,ve_uu,ve_tel,ve_email,ve_contact,ve_add1,ve_auditstatus,ve_auditstatuscode) values('"+id+"','"+code+"','"+m.get("ve_name")+"','"+m.get("ve_shortname")+"','"+m.get("ve_webserver")+"','"+m.get("ve_uu")+"','"+m.get("ve_tel")+"','"+m.get("ve_email")+"','"+m.get("ve_contact")+"','"+m.get("ve_add1")+"','在录入','ENTERING')";
							listSql.add(sql);
						}
					}
					for(Map<Object, Object> m : listCustomers){
						boolean bool = baseDao.checkIf("customer", "cu_uu='"+m.get("cu_uu")+"'");
						if(!bool){
							code = baseDao.sGetMaxNumber("customer", 2);
							id = baseDao.getSeqId("CUSTOMER_SEQ");
							sql = "insert into customer(cu_id,cu_code,cu_name,cu_shortname,cu_businesscode,cu_uu,cu_tel,cu_email,cu_contact,cu_add1,cu_auditstatus,cu_auditstatuscode) values('"+id+"','"+code+"','"+m.get("cu_name")+"','"+m.get("cu_shortname")+"','"+m.get("cu_businesscode")+"','"+m.get("cu_uu")+"','"+m.get("cu_tel")+"','"+m.get("cu_email")+"','"+m.get("cu_contact")+"','"+m.get("cu_add1")+"','在录入','ENTERING')";
							listSql.add(sql);
						}
					}
					baseDao.execute(listSql);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	@Override
	public Map<String, Object> addprevendor(String info) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(info);
		Map<String, Object> map = new HashMap<String, Object>();
		Object pv_id = baseDao.getSeqId("PREVENDOR_SEQ");
		Object pv_code = baseDao.sGetMaxNumber("PREVENDOR", 2);
		boolean bool = baseDao.checkIf("PREVENDOR", "ve_name='"+store.get("name")+"'");
		if(bool){
			map.put("log", "供应商引进已存在名称为'"+store.get("name")+"'的资料！");
			return map;
		}
		String sql = "insert into PREVENDOR(ve_id,ve_code,ve_name,ve_shortname,ve_webserver,ve_add1,ve_contact,ve_tel,ve_email,VE_CURRENCY,ve_auditstatus,ve_auditstatuscode,ve_turnstatus,ve_turnstatuscode,VE_TAGS,VE_PROFESSION,ve_uu) values ('"+pv_id+"','"+pv_code+"','"+store.get("name")+"','"+store.get("shortname")+"','"+store.get("businessCode")+"','"+store.get("address")+"','"+store.get("adminName")+"','"+store.get("adminTel")+"','"+store.get("adminEmail")+"','RMB','在录入','ENTERING','未转正式','UNTURN','"+store.get("tags")+"','"+store.get("profession")+"','"+store.get("uu")+"')";
		baseDao.execute(sql);
		map.put("success", true);
		return map;
	}
}
