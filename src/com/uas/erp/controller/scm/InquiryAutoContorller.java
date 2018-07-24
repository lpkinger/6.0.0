package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.InquiryAutoService;

@Controller
public class InquiryAutoContorller extends BaseController{
	@Autowired
	private InquiryAutoService inquiryAutoService;
	/**
	 * 取分段报价信息
	 * 
	 * @param in_id
	 *            询价单ID
	 */
	@RequestMapping("/scm/purchase/InquiryAuto/det.action")
	@ResponseBody
	public List<Map<String, Object>> getDet(Integer in_id) {
		return inquiryAutoService.getStepDet(in_id);
	}
	
	/**
	 * 转物料核价单
	 */
	@RequestMapping("/scm/purchase/turnAutoPurcPrice.action")
	@ResponseBody
	public Map<String, Object> turnPurcPrice(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		id = inquiryAutoService.turnPurcPrice(id, caller);
		modelMap.put("id", id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 最终判定
	 */
	@RequestMapping("/scm/purchase/agreeAutoPrice.action")
	@ResponseBody
	public Map<String, Object> agreeAutoPrice(int id, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		inquiryAutoService.agreeAutoPrice(id, param);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/purchase/submitInquiryAuto.action")
	@ResponseBody
	public Map<String, Object> submitInquiryAuto(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		inquiryAutoService.submitInquiryAuto(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/purchase/resSubmitInquiryAuto.action")
	@ResponseBody
	public Map<String, Object> resSubmitInquiryAuto(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		inquiryAutoService.resSubmitInquiryAuto(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除明细
	 */
	@RequestMapping("/scm/purchase/deleteAutoDet.action")
	@ResponseBody
	public Map<String, Object> deleteAutoDet(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		inquiryAutoService.deleteAutoDet(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/scm/purchase/deleteAuto.action")
	@ResponseBody
	public Map<String, Object> deleteAuto(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		inquiryAutoService.deleteAuto(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
