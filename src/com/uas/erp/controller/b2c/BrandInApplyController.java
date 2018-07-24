package com.uas.erp.controller.b2c;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.FileUpload;
import com.uas.erp.model.Master;
import com.uas.erp.service.b2c.BrandInApplyService;

@Controller
public class BrandInApplyController {

	@Autowired
	private BrandInApplyService brandInApplyService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/b2c/product/saveBrandInApply.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		brandInApplyService.saveBrandInApply(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除数据 包括明细
	 */
	@RequestMapping("/b2c/product/deleteBrandInApply.action")
	@ResponseBody
	public Map<String, Object> deleteBrandInApplyio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		brandInApplyService.deleteBrandInApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/b2c/product/updateBrandInApply.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		brandInApplyService.updateBrandInApplyById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/b2c/product/submitBrandInApply.action")
	@ResponseBody
	public Map<String, Object> submitBrandInApply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		brandInApplyService.submitBrandInApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/b2c/product/resSubmitBrandInApply.action")
	@ResponseBody
	public Map<String, Object> resSubmitBrandInApply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		brandInApplyService.resSubmitBrandInApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/b2c/product/auditBrandInApply.action")
	@ResponseBody
	public Map<String, Object> auditBrandInApplyio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		brandInApplyService.auditBrandInApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/b2c/product/resAuditBrandInApply.action")
	@ResponseBody
	public Map<String, Object> resAuditBrandInApplyio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		brandInApplyService.resAuditBrandInApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存品牌logo
	 */
	@RequestMapping("/b2c/product/saveBrandLogo.action")
	public @ResponseBody
	String saveBrandLogo(HttpSession session, FileUpload uploadItem) {
		String str = brandInApplyService.saveBrandLogo(uploadItem.getFile());
		return "{success:true,filepath:\"" + str + "\"}";
	}

	/**
	 * 获取商城对应的品牌数据
	 */
	@RequestMapping("/b2c/product/getBrandData.action")
	@ResponseBody
	public Map<String, Object> getBrandData(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", brandInApplyService.getBrandData(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获取商城对应的品牌数据
	 */
	@RequestMapping("/b2c/product/getBrandDataByUUID.action")
	@ResponseBody
	public Map<String, Object> getBrandDataByUUID(String caller, String UUID) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", brandInApplyService.getBrandDataByUUID(UUID, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/b2c/findBrandByPage.action")
	@ResponseBody
	public Map<String, Object> findBrandByPage(String caller, int page, int pageSize) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		brandInApplyService.findBrandByPage(caller, page, pageSize);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/b2c/findBrandAll.action")
	@ResponseBody
	public Map<String, Object> findBrandAll(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", brandInApplyService.findBrandAll(caller));
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/b2c/product/getUpdateBrand.action")
	@ResponseBody
	public Map<String, Object> getUpdateBrand(String nameCn, String nameEn, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", brandInApplyService.getUpdateBrand(nameCn, nameEn, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/b2c/product/checkBrandName.action")
	@ResponseBody
	public Map<String, Object> checkBrandName(String nameCn, String nameEn, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", brandInApplyService.CheckBrandName(nameCn, nameEn, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/b2c/product/checkB2BEnable.action")
	@ResponseBody
	public Map<String, Object> checkB2BEnable(String nameCn, String nameEn, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Master master = SystemSession.getUser().getCurrentMaster();
		if (!master.b2bEnable()) {
			modelMap.put("error", true);
		} else {
			modelMap.put("success", true);
		}
		return modelMap;
	}

}
