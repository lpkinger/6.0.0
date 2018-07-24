package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fa.SubsidiarySetService;

@Controller
public class SubsidiarySetController extends BaseController {
	@Autowired
	private SubsidiarySetService subsidiarySetService;

	/**
	 * 获取子公司数据
	 * 
	 * @return
	 */
	@RequestMapping("fa/gla/getSubsidiarySet.action")
	@ResponseBody
	public Map<String, Object> getSubsidiarySet(Boolean isCheck) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", subsidiarySetService.getSubsidiarySet(isCheck));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获取股东
	 * 
	 * @param checkcode
	 *            关联ID
	 * @return
	 */
	@RequestMapping("fa/gla/getShareholdersRateSet.action")
	@ResponseBody
	public Map<String, Object> getShareholdersRateSet(String checkcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", subsidiarySetService.getShareholdersRateSet(checkcode));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存所有子公司设置
	 * 
	 * @param session
	 * @param CheckItems
	 *            子公司设置明细表数据
	 * @return
	 */
	@RequestMapping("fa/gla/saveSubsidiarySet.action")
	@ResponseBody
	public Map<String, Object> saveSubsidiarySet(HttpSession session, String CheckItems) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		subsidiarySetService.saveSubsidiarySet(CheckItems);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存子公司股东信息
	 * 
	 * @param checkcode
	 *            子公司ID
	 * @param ParamSets
	 *            股东信息明细表数据
	 * @return
	 */
	@RequestMapping("fa/gla/saveShareholdersRateSet.action")
	@ResponseBody
	public Map<String, Object> saveShareholdersRateSet(String checkcode, String ParamSets) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		subsidiarySetService.saveShareholdersRateSet(checkcode, ParamSets);
		modelMap.put("success", true);
		return modelMap;
	}

}
