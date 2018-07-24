package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Employee;
import com.uas.erp.service.scm.ProductService;

@Controller
public class ProductController {
	@Autowired
	private ProductService productService;
	/**
	 * 保存product!base
	 * @param formStore form数据
	 * @param param 其它数据
	 */
	@RequestMapping("/scm/product/saveProduct.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productService.saveProduct(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/product/updateProduct.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productService.updateProductById(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 更改承认状态
	 */
	@RequestMapping("/scm/product/updateProductStatus.action")  
	@ResponseBody 
	public Map<String, Object> updateStatus(String caller, String value,String crman,String remark,int id,String date,String mfile) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productService.updateProductStatus(id,value,crman,remark,date,caller,mfile);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 更改优选等级
	 */
	@RequestMapping("/scm/product/updateProductLevel.action")  
	@ResponseBody 
	public Map<String, Object> updateProductLevel(String caller, String value, String remark, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productService.updateProductLevel(id, value, remark, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/scm/product/deleteProduct.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productService.deleteProduct(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/product/submitProduct.action")  
	@ResponseBody 
	public Map<String, Object> submitProduct(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productService.submitProduct(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/product/resSubmitProduct.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitProduct(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productService.resSubmitProduct(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/product/auditProduct.action")  
	@ResponseBody 
	public Map<String, Object> auditProduct(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productService.auditProduct(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/product/resAuditProduct.action")  
	@ResponseBody 
	public Map<String, Object> resAuditProduct(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productService.resAuditProduct(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 禁用
	 */
	@RequestMapping("/scm/product/bannedProduct.action")  
	@ResponseBody 
	public Map<String, Object> bannedProduct(String caller, int id, String remark) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productService.bannedProduct(id, remark, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反禁用
	 */
	@RequestMapping("/scm/product/resBannedProduct.action")  
	@ResponseBody 
	public Map<String, Object> resBannedProduct(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productService.resBannedProduct(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 物料资料批量抛转
	 * @param id 物料ID
	 * @param ma_id 帐套ID
	 */
	@RequestMapping(value="/scm/product/postProduct.action")
	@ResponseBody
	public Map<String, Object> vastPost(HttpSession session, int[] id, int ma_id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee)session.getAttribute("employee");
		modelMap.put("log", productService.postProduct(id, employee.getEm_maid(), ma_id));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转打样
	 */
	@RequestMapping("/scm/product/prodturnsample.action")  
	@ResponseBody 
	public Map<String, Object> prodturnsample(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int prid = productService.prodturnsample(id, caller);
		modelMap.put("id", prid);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 物料标准单价变更
	 */
	@RequestMapping("/scm/product/changeStandardPrice.action")  
	@ResponseBody 
	public Map<String, Object> changeStandardPrice(HttpSession session,String caller, String data) {
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productService.changeStandardPrice(employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 转招标
	 */
	@RequestMapping("/scm/product/turnTender.action")  
	@ResponseBody 
	public Map<String, Object> turnTender(Integer id, String caller,String title,String qty) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("msg", productService.turnTender(id, caller, title, qty));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 去配置后缀
	 */
	@RequestMapping("/scm/product/getCodePostfix.action")  
	@ResponseBody 
	public Map<String, Object> getCodePostfix(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("code", productService.getCodePostfix(caller));
		modelMap.put("success", true);
		return modelMap;
	}
}
