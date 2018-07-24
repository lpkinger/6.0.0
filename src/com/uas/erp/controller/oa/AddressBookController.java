package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.AddressBookService;

@Controller
public class AddressBookController {
	@Autowired
	private AddressBookService addressBookService;

	@RequestMapping("/oa/addressbook/getAddressBookGroup.action")
	@ResponseBody
	public Map<String, Object> getAddressBookGroup(String caller,
			HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int emid = Integer.parseInt(session.getAttribute("em_id").toString());
		modelMap.put("tree", addressBookService.getJSONGroup(caller, emid));
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/addressbook/saveAddressBookGroup.action")
	@ResponseBody
	public Map<String, Object> saveAddressBookGroup(String caller,
			String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		addressBookService.saveAddressBookGroup(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/addressbook/updateAddressBookGroup.action")
	@ResponseBody
	public Map<String, Object> updateAddressBookGroup(String caller,
			String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		addressBookService.updateAddressBookGroup(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/addressbook/deleteAddressBookGroup.action")
	@ResponseBody
	public Map<String, Object> deleteAddressBookGroup(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		addressBookService.deleteAddressBookGroup(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/addressbook/removeToOtherGroup.action")
	@ResponseBody
	public Map<String, Object> removeToOtherGroup(String caller, int id,
			String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		addressBookService.removeToOtherGroup(id, data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 维护个人通讯录
	 */
	@RequestMapping("/oa/addressbook/saveAddressPerson.action")
	@ResponseBody
	public Map<String, Object> saveAddressPerson(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();

		addressBookService.saveAddressPerson(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/addressbook/updateAddressPerson.action")
	@ResponseBody
	public Map<String, Object> updateAddressPerson(String caller,
			String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		addressBookService.updateAddressPerson(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/addressbook/deleteAddressPerson.action")
	@ResponseBody
	public Map<String, Object> deleteAddressPerson(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		addressBookService.deleteAddressPerson(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/addressbook/sharedToOther.action")
	@ResponseBody
	public Map<String, Object> sharedToOther(String caller, String formStore,
			String type, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		addressBookService.sharedToOther(formStore, type, data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/addressbook/getEmployee.action")
	@ResponseBody
	public Map<String, Object> getEmployee(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("tree", addressBookService.getEmployee(caller));
		modelMap.put("success", true);
		return modelMap;
	}
}
