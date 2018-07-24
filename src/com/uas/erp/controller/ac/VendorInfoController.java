package com.uas.erp.controller.ac;

import java.util.HashMap;
import java.util.Map;

import javax.xml.ws.RequestWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uas.erp.ac.service.common.VendorInfoService;

/**
 * 企业圈的供应商
 * 
 * @author hejq
 * @time 创建时间：2017年6月7日
 */
@RestController
public class VendorInfoController {

	@Autowired
	private VendorInfoService vendorInfoService;

	/**
	 * 供应商
	 */
	@RequestMapping("ac/vendors.action")
	public Map<String, Object> vendors(String keyword, Integer page, Integer limit) throws Exception {
		return vendorInfoService.vendors(keyword, page, limit);
	}
	/**
	 * 服务商
	 */
	@RequestMapping("ac/services.action")
	public Map<String, Object> services(String keyword, Integer page, Integer limit) throws Exception {
		return vendorInfoService.services(keyword, page, limit);
	}
	/**
	 * 获取erp供应商资料
	 * @param caller
	 * @param condition
	 * @return
	 */
	@RequestMapping("ac/erpVendors.action")
	public Map<String, Object> erpVendors(String keyword, Integer page, Integer limit) throws Exception {
		return vendorInfoService.erpVendors(keyword, page, limit);
	}
	@RequestMapping("ac/getVendorData.action")
	public Map<String, Object> getVendorData(String caller,String condition) {
		return vendorInfoService.getVendorData(caller, condition);
	}
	@RequestMapping("ac/updateVendorData.action")
	public Map<String, Object> updateVendorData(String id,String uu) {
		Map<String,Object> map = new HashMap<String, Object>();
		vendorInfoService.updateVendorData(id, uu);
		map.put("success",true);
		return map;
	}
	@RequestMapping("ac/vendUse.action")
	public Map<String,Object> vendUse(Integer id,Integer hasRelative,Integer type,String vendUID) throws Exception{
		return vendorInfoService.vendUse(id,hasRelative,type,vendUID);
	}//ac/serviceUse.action
	@RequestMapping("ac/serviceUse.action")
	public Map<String,Object> serviceUse(Integer id,Integer hasRelative,Integer type,String vendUID) throws Exception{
		return vendorInfoService.serviceUse(id,hasRelative,type,vendUID);
	}
}
