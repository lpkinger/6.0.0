package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.oa.EmployeeMailService;

@Controller
public class EmployeeMailController {
	@Autowired
	private EmployeeMailService employeeMailService;

	/**
	 * 保存AddrBook
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/oa/addrBook/saveAddrBook.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		employeeMailService.saveAddrBook(formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/oa/addrBook/updateAddrBook.action")
	@ResponseBody
	public Map<String, Object> update(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		System.out.println(formStore);
		employeeMailService.updateAddrBookById(formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/addrBook/deleteAddrBook.action")
	@ResponseBody
	public Map<String, Object> delete(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		employeeMailService.deleteAddrBook(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 根据父节点加载子节点
	 */
	@RequestMapping(value = "/oa/addrBook/getAddrBookTree.action")
	@ResponseBody
	public Map<String, Object> getTreeByParentId(String parentid) throws Exception {
		int prid;
		if(parentid.contains("org")){
			prid=Integer.parseInt(parentid.substring(4, parentid.length()));
		}else{
			prid=Integer.parseInt(parentid);
		}
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<JSONTree> tree = employeeMailService.getJsonTrees(SpObserver.getSp(), prid);
		modelMap.put("tree", tree);
		return modelMap;
	}

	/**
	 * 根据节点ID加载员工信息
	 */
	@RequestMapping(value = "/oa/addrBook/getEmployee.action")
	@ResponseBody
	public Map<String, Object> getEmployee(int id) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("tree", employeeMailService.getEmployeeMailByEmployee(id));
		return modelMap;
	}
}
