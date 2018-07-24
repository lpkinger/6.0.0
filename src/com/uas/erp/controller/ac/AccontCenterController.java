package com.uas.erp.controller.ac;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.uas.erp.ac.service.common.PartnersRecordService;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Enterprise;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.sso.AccountConfig;
import com.uas.sso.entity.PartnershipRecordView;
import com.uas.sso.entity.UserView;
import com.uas.sso.util.AccountUtils;

@Controller
public class AccontCenterController {

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private PartnersRecordService partnersRecordService;

	@Autowired
	private EnterpriseService enterpriseService;

	/**
	 * 跳转账户中心-企业圈
	 */
	@RequestMapping("ac/businessGroup.action")
	public void redirectContactPage(HttpServletResponse response) {
		Employee employee = SystemSession.getUser();
		if (employee != null && employee.getEm_mobile().matches(Constant.REGEXP_MOBILE)
				&& null != employee.getCurrentMaster().getMa_manageid()) {
			if (employee.getEm_uu() == null) {
				employeeService.postToAccountCenter(employee);
			}
			UserView user = new UserView();
			user.setUserUU(employee.getEm_uu());
			user.setVipName(employee.getEm_name());
			user.setMobile(employee.getEm_mobile());
			user.setEmail(employee.getEm_email());
			user.setIdCard(employee.getEm_iccode());
			try {
				String token = AccountUtils.getAccessToken(user);
				response.sendRedirect(String.format("%s/webpage?appId=%s&access_token=%s&redirect_page=%s", AccountConfig.getUserSaveUrl(),
						BaseUtil.getAppId(), token, "business/groups"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 通过营业执照号获取当前企业的申请列表
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("ac/myPartners.action")
	@ResponseBody
	public Map<String, Object> getByBusinessCode(String keyword, Integer start, Integer page, Integer limit, Integer statusCode)
			throws Exception {
		return partnersRecordService.getAllPartnersInfosByBusinessCode(keyword, start, page, limit, statusCode);
	}

	/**
	 * 获取新的合作伙伴
	 * 
	 * @author wsy
	 */
	@RequestMapping("ac/getNewPartners.action")
	@ResponseBody
	public Map<String, Object> getNewPartners(String keyword, Integer start, Integer page, Integer limit, Integer statusCode)
			throws Exception {
		return partnersRecordService.getNewPartners(keyword, start, page, limit, statusCode);
	}

	/**
	 * 企业圈-企业列表，通过关键词搜索
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("ac/enterpriseList.action")
	@ResponseBody
	public Map<String, Object> getUserSpaceDetails(String keyword, Integer start, Integer page, Integer limit) throws Exception {
		return partnersRecordService.getUserSpaceDetails(keyword, start, page, limit);
	}

	/**
	 * 新增合作伙伴关系
	 * 
	 * @param name
	 * @param businessCode
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("ac/newPartners.action")
	@ResponseBody
	public ResponseEntity<ModelMap> addNewPartner(String name, String businessCode) throws Exception {
		PartnershipRecordView newrecord = new PartnershipRecordView();
		ModelMap map = new ModelMap();
		Enterprise enterprise = enterpriseService.getEnterprise();
		if (businessCode.equals(enterprise.getEn_Businesscode())) {
			map.put("error", "不能添加自己为合作伙伴");
		} else {
			newrecord.setVendName(name);
			newrecord.setVendUID(businessCode);
			newrecord.setAppId(BaseUtil.getAppId());
			newrecord.setCustName(enterprise.getEn_Name());
			newrecord.setCustUID(enterprise.getEn_Businesscode());
			newrecord.setCustUserCode(SystemSession.getUser().getEm_uu());
			newrecord.setCustUserEmail(SystemSession.getUser().getEm_email());
			newrecord.setCustUserName(SystemSession.getUser().getEm_name());
			newrecord.setCustUserTel(SystemSession.getUser().getEm_mobile());
			String result = AccountUtils.addNewRecord(newrecord);
			JSONObject object = JSON.parseObject(result);
			map.put("error", object.getString("error"));
			map.put("success", object.getString("success"));
		}
		return new ResponseEntity<ModelMap>(map, HttpStatus.OK);
	}

	/**
	 * 企业同意申请
	 * 
	 * @throws Exception
	 */
	@RequestMapping("ac/agreeRequest.action")
	@ResponseBody
	public ModelMap agreeRequest(Long id) throws Exception {
		ModelMap map = new ModelMap();
		String result = AccountUtils.acceptRequest(id, SystemSession.getUser().getEm_mobile(), BaseUtil.getAppId());
		map.put("result", result);
		return map;
	}

	/**
	 * 企业拒绝申请
	 * 
	 * @param id
	 * @param reason
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("ac/refuseRequest.action")
	@ResponseBody
	public ModelMap refuseRequest(Long id, String reason) throws Exception {
		ModelMap map = new ModelMap();
		String result = AccountUtils.rejectRequest(id, reason, SystemSession.getUser().getEm_mobile(), BaseUtil.getAppId());
		map.put("result", result);
		return map;
	}

	/**
	 * 邀请注册ac/invite.action
	 * 
	 * @throws Exception
	 */
	@RequestMapping("ac/invite.action")
	@ResponseBody
	public Map<String, Object> invite(String formStore) throws Exception {
		return partnersRecordService.invite(formStore);
	}

	/**
	 * 一键同步ac/sync.action
	 */
	@RequestMapping("ac/sync.action")
	@ResponseBody
	public Map<String, Object> sync() throws Exception {
		return partnersRecordService.sync();
	}

	/**
	 * addprevendor
	 */
	@RequestMapping("ac/addprevendor.action")
	@ResponseBody
	public Map<String, Object> addprevendor(String info) {
		return partnersRecordService.addprevendor(info);
	}
}
