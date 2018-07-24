package com.uas.erp.controller.b2c;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.api.b2c_erp.baisc.service.KindService;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.model.FileUpload;
import com.uas.erp.service.b2c.DeviceInApplyService;


@Controller
public class DeviceInApplyController {

	@Autowired
	private DeviceInApplyService deviceInApplyService;
	
	@Autowired
	private KindService KindService;
	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/b2c/product/saveDeviceInApply.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		param="e";
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceInApplyService.saveDeviceInApply(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除数据 包括明细
	 */
	@RequestMapping("/b2c/product/deleteDeviceInApply.action")
	@ResponseBody
	public Map<String, Object> deleteDeviceInApplyio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceInApplyService.deleteDeviceInApply(id, caller);
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
	@RequestMapping("/b2c/product/updateDeviceInApply.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceInApplyService.updateDeviceInApplyById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/b2c/product/submitDeviceInApply.action")
	@ResponseBody
	public Map<String, Object> submitDeviceInApplyio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceInApplyService.submitDeviceInApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/b2c/product/resSubmitDeviceInApply.action")
	@ResponseBody
	public Map<String, Object> resSubmitDeviceInApplyio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceInApplyService.resSubmitDeviceInApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/b2c/product/auditDeviceInApply.action")
	@ResponseBody
	public Map<String, Object> auditDeviceInApplyio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceInApplyService.auditDeviceInApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/b2c/product/resAuditDeviceInApply.action")
	@ResponseBody
	public Map<String, Object> resAuditDeviceInApplyio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deviceInApplyService.resAuditDeviceInApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取商城对应的器件数据
	 */
	@RequestMapping("/b2c/product/getDeviceData.action")
	@ResponseBody
	public Map<String, Object> getDeviceData(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",deviceInApplyService.getDeviceData(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	/**
	 * 获取所有类目信息*
	 */
	
	@RequestMapping("/b2c/product/getAllKind.action")
	@ResponseBody
	public Map<String, Object> getAllKind(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",deviceInApplyService.getAllKind(caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获取所有品牌信息
	 */
	@RequestMapping("/b2c/product/getAllBrand.action")
	@ResponseBody
	public Map<String, Object> getAllBrand(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",deviceInApplyService.getAllBrand(caller));
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/b2c/product/getSearchData.action")
	@ResponseBody
	public Map<String, Object> getSearchData(String searchWord,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",deviceInApplyService.getSearchData(searchWord,caller));
		modelMap.put("success", true);
		return modelMap;
	}
	//获取原厂型号联想词
	@RequestMapping("/b2c/product/getOldSpecData.action")
	@ResponseBody
	public Map<String, Object> getOldSpecData(String searchWord,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",deviceInApplyService.getOldSpecData(searchWord,caller));
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/b2c/product/getProperties.action")
	@ResponseBody
	public Map<String, Object> getPropertiesById(Long id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<String, Object> test = new HashMap<String, Object>();
		try {
			test = deviceInApplyService.getPropertiesById(id);
		} catch (Exception e) {
			test = null;
		}
		modelMap.put("data",test);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/b2c/product/getDeviceByUUID.action")
	@ResponseBody
	public Map<String, Object> getDeviceByUUID(String UUID,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",deviceInApplyService.findDeviceByUUID(UUID, caller));
		modelMap.put("success", true);
		return modelMap;
	}
	
	//获取类目模糊搜索
	@RequestMapping("/b2c/product/getKindData.action")
	@ResponseBody
	public Map<String, Object> getKindData(String searchWord,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",deviceInApplyService.getKindData(searchWord, caller));
		modelMap.put("success", true);
		return modelMap;
	}
	
	//获取类目模糊搜索
	@RequestMapping("/b2c/product/getATypeAssociation.action")
	@ResponseBody
	public Map<String, Object> getATypeAssociation(Long kindid,Long id,String searchword,Long shownum,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",deviceInApplyService.getATypeAssociation(kindid,id,searchword,shownum, caller));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 保存品牌logo
	 */
	@RequestMapping("/b2c/product/saveFile.action")
	public @ResponseBody String saveFile(HttpSession session, FileUpload uploadItem) {
		String str = deviceInApplyService.saveFile(uploadItem.getFile());
		return "{success:true,filepath:\""+str+"\"}";
	}
	
	//获取类目模糊搜索
	@RequestMapping("/b2c/product/checkBrandAndCode.action")
	@ResponseBody
	public Map<String, Object> CheckBrandAndCode(String nameCn,String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",deviceInApplyService.checkBrandAndCode(nameCn, code));
		modelMap.put("success", true);
		return modelMap;
	}
	
	//获取封装规格
	@RequestMapping("/b2c/product/getPackaging.action")
	@ResponseBody
	public Map<String, Object> getPackaging(Long kindid) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",deviceInApplyService.getPackaging(kindid));
		modelMap.put("success", true);
		return modelMap;
	}
}
