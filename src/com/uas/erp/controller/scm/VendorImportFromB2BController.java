package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.VendorImportFromB2BService;

@Controller
public class VendorImportFromB2BController {
	@Autowired
	private VendorImportFromB2BService vendorImportFromB2BService;
	/**
	 * 从平台获取可引进供应商信息
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 * */
	@RequestMapping("/scm/purchase/getVendorImportFromB2B.action")
	@ResponseBody
	public Map<String, Object> getVendorImportFromB2B(String condition,String caller, Integer start, Integer page, Integer limit) {
		Map<String, Object>  map = vendorImportFromB2BService.getVendorImportFromB2B(caller, condition, start, page, limit);
		return map;
	}
	/**
	 * 保存供应商信息至供应商引进
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/scm/purchase/importVendorFromB2B.action")
	@ResponseBody
	public Map<String, Object> importVendorFromB2B(String caller, String formStore, String param) {
		Map<String, Object>  map = vendorImportFromB2BService.importVendorFromB2B( caller,  formStore);
		return map;
	}
	/**
	 * 从平台获取可引进供应商物料
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 * */
	@RequestMapping("/scm/purchase/getVendorImpoertProdDetail.action")
	@ResponseBody
	public Map<String, Object> getVendorImpoertProdDetail(String productMatchCondition,String whereCondition,String caller,String ve_uu, Integer start, Integer page, Integer limit) {
		Map<String, Object>  map = vendorImportFromB2BService.getVendorImpoertProdDetail(caller, ve_uu,productMatchCondition,whereCondition, start, page, limit);
		return map;
	}
	/**
	 * 供应商资源库模糊查询B2B数据
	 * @param caller 
	 * @param condition 条件
	 * @param field 返回字段
	 * @return
	 */
	@RequestMapping("/scm/purchase/getVendorFormB2B.action")
	@ResponseBody
	public Map<String, Object> getVendorFormB2B(String caller,String condition,String field,String enUU){
		Map<String, Object> map =new HashMap<String, Object>();
		map = vendorImportFromB2BService.getVendorFormB2B(caller,field,condition,enUU);
		return map;
	}
	
}
