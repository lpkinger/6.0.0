package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.b2b.model.InquiryDetailDet;
import com.uas.erp.core.BaseController;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.model.Employee;
import com.uas.erp.service.scm.InquiryService;

@Controller
public class InquiryController extends BaseController {
	@Autowired
	private InquiryService inquiryService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/scm/purchase/saveInquiry.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param, String param2, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		inquiryService.saveInquiry(formStore, param, param2, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 取分段报价信息
	 * 
	 * @param in_id
	 *            询价单ID
	 */
	@RequestMapping("/scm/purchase/inquiry/det.action")
	@ResponseBody
	public List<Map<String, Object>> getDet(Integer in_id) {
		return inquiryService.getStepDet(in_id);
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/scm/purchase/deleteInquiry.action")
	@ResponseBody
	public Map<String, Object> deleteInquiry(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		inquiryService.deleteInquiry(id, caller);
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
	@RequestMapping("/scm/purchase/updateInquiry.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param, String param2, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		inquiryService.updateInquiryById(formStore, param, param2, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/scm/purchase/printInquiry.action")
	@ResponseBody
	public Map<String, Object> printInquiry(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		inquiryService.printInquiry(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/purchase/submitInquiry.action")
	@ResponseBody
	public Map<String, Object> submitInquiry(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		inquiryService.submitInquiry(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/purchase/resSubmitInquiry.action")
	@ResponseBody
	public Map<String, Object> resSubmitInquiry(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		inquiryService.resSubmitInquiry(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/purchase/auditInquiry.action")
	@ResponseBody
	public Map<String, Object> auditInquiry(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		inquiryService.auditInquiry(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/purchase/resAuditInquiry.action")
	@ResponseBody
	public Map<String, Object> resAuditInquiry(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		inquiryService.resAuditInquiry(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转物料核价单
	 */
	@RequestMapping("/scm/purchase/turnPurcPrice.action")
	@ResponseBody
	public Map<String, Object> turnPurcPrice(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		id = inquiryService.turnPurcPrice(id, caller);
		modelMap.put("id", id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 查找回复记录
	 * 
	 * @param id
	 *            采购订单ID
	 */
	@RequestMapping("scm/inquiry/getReply.action")
	@ResponseBody
	public List<InquiryDetailDet> getReply(int id) {
		return inquiryService.findReplyByInid(id);
	}

	/**
	 * 更新信息
	 * */
	@RequestMapping("/scm/purchase/inquiry/updateInfo.action")
	@ResponseBody
	public Map<String, Object> updateInfo(HttpSession session, int id, String purpose, String remark, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		inquiryService.updateInfo(id, purpose, remark, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 作废
	 */
	@RequestMapping("/scm/purchase/nullifyInquiry.action")
	@ResponseBody
	public Map<String, Object> nullifyInquiry(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		inquiryService.nullifyInquiry(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 复制
	 * 
	 * @param session
	 * @param id
	 * @param caller
	 * @return
	 */
	@RequestMapping("/scm/purchase/copyInquiry.action")
	@ResponseBody
	public Map<String, Object> copyInquiry(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", inquiryService.copyInquiry(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 新的最终判定
	 */
	@RequestMapping("/scm/purchase/agreeInquiryPrice.action")
	@ResponseBody
	public Map<String, Object> agreeInquiryPrice(int id, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		inquiryService.agreeInquiryPrice(id, param);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 采购询价单入口:全部物料
	 */
	@RequestMapping(value = "/scm/purchase/getAllPurc.action")
	@ResponseBody
	public Map<String, Object> getAllPurc(String caller, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", inquiryService.getAllPurc(caller, condition));
		return modelMap;
	}
	/**
	 * 采购询价单入口:BOM物料
	 */
	@RequestMapping(value = "/scm/purchase/getBom.action")
	@ResponseBody
	public Map<String, Object> getBom(String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", inquiryService.getBom(caller, param));
		return modelMap;
	}
	/**
	 * 采购询价单入口:发起询价
	 */
	@RequestMapping(value = "/scm/purchase/inquiryInlet.action")
	@ResponseBody
	public Map<String, Object> startIQ(String caller, String formStore, String param1 , String param2, String param3) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", inquiryService.startIQ(caller, formStore,param1,param2,param3));
		modelMap.put("success", true);
		return modelMap;
	}
}
