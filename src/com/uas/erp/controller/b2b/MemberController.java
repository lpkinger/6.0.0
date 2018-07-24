package com.uas.erp.controller.b2b;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.model.QueriableUser;
import com.uas.erp.service.b2b.QueriableService;

@Controller
@RequestMapping("/b2b/queriable")
public class MemberController {

	@Autowired
	private QueriableService queriableService;

	@RequestMapping(value = "/members.action", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getMembers(String name, String shortName, Long uu) throws UnsupportedEncodingException {
		return queriableService.findMembersByVendor(name, shortName, uu);
	}

	@RequestMapping(value = "/users.action", method = RequestMethod.GET)
	@ResponseBody
	public QueriableUser getUsers(Long enUU, Long userUU) {
		return queriableService.findUserByUU(enUU, userUU);
	}
	
	@RequestMapping(value = "/searchEnterprise.action", method = RequestMethod.GET)
	@ResponseBody
	public List<Map<String, String>> searchEnterprise(@RequestParam(value = "key", required = true) String key)
			throws UnsupportedEncodingException {
		return  queriableService.findEnterprisesByKey(key);
	}

}
