package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.bind.Constant;
import com.uas.erp.service.scm.VendorService;

@Controller
public class VendorController {
	@Autowired
	private VendorService vendorService;

	/**
	 * 保存vendor
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/scm/purchase/saveVendor.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorService.saveVendor(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存从账户中心获取的数据
	 * 
	 * @param formStore
	 * 
	 *
	 */
	@RequestMapping("/scm/purchase/saveVendorSimple.action")
	@ResponseBody
	public Map<String, Object> save(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int ve_id = vendorService.saveVendorSimple(formStore);
		modelMap.put("ve_id", ve_id);
		return modelMap;
	}

	/**
	 * 判断vendor是否已在客户列表
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/scm/purchase/checkVendor.action")
	@ResponseBody
	public Map<String, Object> check(HttpSession session, int ve_otherenid) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int ve_enid = (Integer) session.getAttribute("en_uu");
		modelMap.put("success", true);
		if (!vendorService.checkVendorByEnId(ve_enid, ve_otherenid)) {
			modelMap.put("success", false);
		}
		return modelMap;
	}

	/**
	 * 更新批量从平台获取的供应商UU号的检测状态为已检测
	 * 
	 * @param id
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/scm/purchase/checkVendorUU.action")
	@ResponseBody
	public Map<String, Object> checkVendorUU(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorService.checkVendorUU(data,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 批量启用B2B收料
	 */
	@RequestMapping("/scm/purchase/openB2BDelivery.action")
	@ResponseBody
	public Map<String, Object> openB2BDelivery(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorService.updateB2BPro(data,"ve_ifdeliveryonb2b=-1");
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 批量取消B2B收料
	 */
	@RequestMapping("/scm/purchase/cancelB2BDelivery.action")
	@ResponseBody
	public Map<String, Object> cancelB2BDelivery(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorService.updateB2BPro(data,"ve_ifdeliveryonb2b=0");
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 启用B2B对账
	 */
	@RequestMapping("/scm/purchase/openB2BCheck.action")
	@ResponseBody
	public Map<String, Object> openB2BCheck(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorService.updateB2BPro(data,"ve_b2bcheck=-1");
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 取消B2B对账
	 */
	@RequestMapping("/scm/purchase/cancelB2BCheck.action")
	@ResponseBody
	public Map<String, Object> cancelB2BCheck(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorService.updateB2BPro(data,"ve_b2bcheck=0");
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改vendor
	 */
	@RequestMapping("/scm/purchase/updateVendor.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorService.updateVendor(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除vendor
	 */
	@RequestMapping("/scm/purchase/deleteVendor.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorService.deleteVendor(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核vendor
	 */
	@RequestMapping("/scm/purchase/auditVendor.action")
	@ResponseBody
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorService.auditVendor(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核vendor
	 */
	@RequestMapping("/scm/purchase/resAuditVendor.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorService.resAuditVendor(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交vendor
	 */
	@RequestMapping("/scm/purchase/submitVendor.action")
	@ResponseBody
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorService.submitVendor(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交vendor
	 */
	@RequestMapping("/scm/purchase/resSubmitVendor.action")
	@ResponseBody
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorService.resSubmitVendor(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 禁用vendor
	 */
	@RequestMapping("/scm/purchase/bannedVendor.action")
	@ResponseBody
	public Map<String, Object> banned(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorService.bannedVendor(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反禁用vendor
	 */
	@RequestMapping("/scm/purchase/resBannedVendor.action")
	@ResponseBody
	public Map<String, Object> resBanned(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorService.resBannedVendor(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改vendor UU
	 * 
	 * @param uu
	 *            供应商UU号
	 */
	@RequestMapping("/scm/vendor/updateUU.action")
	@ResponseBody
	public Map<String, Object> updateUU(String caller, Integer id, String uu, String name, String shortName,
			String isb2b, String b2bcheck,Integer checked,String ve_webserver,String ve_legalman,String ve_add1) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorService.updateUU(id, uu, name, shortName, isb2b, b2bcheck,(checked == null ? false : (checked == Constant.YES)),
				caller,ve_webserver,ve_legalman,ve_add1);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改vendor level
	 * 
	 * @param ve_level
	 *            供应商等级
	 */
	@RequestMapping("/scm/vendor/updateLevel.action")
	@ResponseBody
	public Map<String, Object> updateLevel(String caller, Integer id, String ve_level) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorService.updateLevel(id, ve_level, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量修改vendor
	 */
	@RequestMapping("/scm/vendor/batchUpdateVendor.action")
	@ResponseBody
	public Map<String, Object> batchUpdateVendor(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorService.batchUpdateVendor(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 根据{供应商类型}取对应的编号
	 */
	@RequestMapping(value = "/scm/vendor/getVendorCodeNum.action")
	@ResponseBody
	public Map<String, Object> getVendorKindNum(String ve_kind) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("number", vendorService.getVendorKindNum(ve_kind));
		modelMap.put("success", true);
		return modelMap;
	}

	/*
	 * 供应商一键注册
	 */
	@RequestMapping("/scm/vendor/regB2BVendor.action")
	@ResponseBody
	public Map<String, Object> regB2BVendor(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorService.regB2BVendor(id);
		modelMap.put("success", true);
		return modelMap;
	}
}
