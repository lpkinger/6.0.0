package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.pm.CraftMaterialService;

@Controller
public class CraftMaterialController {
	@Autowired
	private CraftMaterialService CraftMaterialService;

	/**
	 * 上料采集筛选判断
	 * @param mccode
	 * @param licode
	 * @param sccode
	 * @param stepcode
	 * @return
	 */
	@RequestMapping("/pm/mes/checkCraftMaterialQuery.action")
	@ResponseBody
	public Map<String, Object> checkCraftMaterialQuery(String mccode,String sccode,String stepcode,String mcprodcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", CraftMaterialService.checkCraftMaterialQuery(mccode,sccode,stepcode,mcprodcode));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 上料回车键判断序列号
	 * @param barcode
	 * @param mccode
	 * @param licode
	 * @param sccode
	 * @param stepcode
	 * @return
	 */
	@RequestMapping("/pm/mes/checkCraftMaterialGet.action")
	@ResponseBody
	public Map<String, Object> checkCraftMaterialGet(String mscode,String mccode,String licode,String sccode,String stepcode,boolean ifGet) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", CraftMaterialService.checkCraftMaterialGet(mscode,mccode,licode,sccode,stepcode,ifGet));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 上料采集
	 * @param mccode
	 * @param licode
	 * @param sccode
	 * @param stepcode
	 * @return
	 */
	@RequestMapping("/pm/mes/getCraftMaterial.action")
	@ResponseBody
	public Map<String, Object> getCraftMaterial(String mscode,String mccode,String licode,String sccode,String stepcode,String barcode,int sp_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("result",CraftMaterialService.getCraftMaterial(mscode, mccode, licode, sccode, stepcode, barcode, sp_id));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 取消上料
	 * @param mccode
	 * @param licode
	 * @param sccode
	 * @param stepcode
	 * @return
	 */
	@RequestMapping("/pm/mes/backCraftMaterial.action")
	@ResponseBody
	public Map<String, Object> backCraftMaterial(String mscode,String mccode,String sccode,String barcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("result",CraftMaterialService.backCraftMaterial(mscode,mccode,sccode,barcode));
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/pm/mes/getBarDescription.action")
	@ResponseBody
	public Map<String, Object> getBarDescription(String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", CraftMaterialService.getBarDescription(condition));
		modelMap.put("success", true);
		return modelMap;
	}
	
}
