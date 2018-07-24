package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.PreProductService;
/**
 * 新物料申请
 * @view jsps/scm/product/preProduct.jsp
 * @author madan
 * @date 2012-8-21 1:13:23
 */
@Controller
public class PreProductController {
	@Autowired
	private PreProductService preProductService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/product/savePreProduct.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preProductService.savePreProduct(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/scm/product/deletePreProduct.action")  
	@ResponseBody 
	public Map<String, Object> deletePreProduct(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preProductService.deletePreProduct(id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 */
	@RequestMapping("/scm/product/updatePreProduct.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preProductService.updatePreProductById(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/product/submitPreProduct.action")  
	@ResponseBody 
	public Map<String, Object> submitPreVendor(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preProductService.submitPreProduct(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/product/resSubmitPreProduct.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitPreProduct(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preProductService.resSubmitPreProduct(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/product/auditPreProduct.action")  
	@ResponseBody 
	public Map<String, Object> auditPreProduct(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = preProductService.auditPreProduct(id,caller);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/product/resAuditPreProduct.action")  
	@ResponseBody 
	public Map<String, Object> resAuditPreProduct(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preProductService.resAuditPreProduct(id,caller);
		modelMap.put("success", true);
		return modelMap;
	} 
	/**
	 * 转正式物料
	 */
	@RequestMapping("/scm/product/turnFormal.action")  
	@ResponseBody 
	public Map<String, Object> turnFormal(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int prid = preProductService.turnFormal(id);
		modelMap.put("id", prid);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/product/getkind.action")  
	@ResponseBody 
	public Map<String, Object> getkind(String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String kind= preProductService.getkind(code);
		modelMap.put("result", kind);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转询价
	 */
	@RequestMapping("/scm/product/turninquiry.action")  
	@ResponseBody 
	public Map<String, Object> turninquiry(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int prid = preProductService.turninquiry(id);
		modelMap.put("id", prid);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转打样
	 */
	@RequestMapping("/scm/product/turnsample.action")  
	@ResponseBody 
	public Map<String, Object> turnsample(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int prid = preProductService.turnsample(id);
		modelMap.put("id", prid);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * @author wsy
	 */
	@RequestMapping("/scm/product/getPreCount.action")  
	@ResponseBody 
	public Map<String, Object> getPreCount(String pre_spec,int pre_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("count", preProductService.getPreCount(pre_spec,pre_id));
		return modelMap;
	}
}
