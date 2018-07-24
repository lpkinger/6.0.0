package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.SMTFeedService;

@Controller
public class SMTFeedController extends BaseController {
	@Autowired
	private SMTFeedService SMTFeedService;

	@RequestMapping("/pm/mes/getSMTFeed.action")
	@ResponseBody
	public Map<String, Object> getSMTFeed(String mpcode, String fecode, String mlscode, String macode, String table, String barcode, String mccode, String licode, String devcode, String sccode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("qty", SMTFeedService.getSMTFeed(mpcode, fecode, mlscode, macode, table, barcode, mccode, licode, devcode, sccode));
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/pm/mes/backSMTFeed.action")
	@ResponseBody
	public Map<String, Object> backSMTFeed(String mlscode, String macode, String mccode, String licode, String devcode, String sccode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SMTFeedService.backSMTFeed(mlscode, macode, mccode, licode, devcode, sccode);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/pm/mes/changeSMTFeed.action")
	@ResponseBody
	public Map<String, Object> changeSMTFeed( String mlscode, String macode, String table, String barcode, String mccode, String licode, String devcode, String sccode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SMTFeedService.changeSMTFeed(mlscode, macode, table, barcode, mccode, licode, devcode, sccode);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/pm/mes/blankAll.action")
	@ResponseBody
	public Map<String, Object> blankAll(String macode, String devcode, String mccode,String sccode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SMTFeedService.blankAll( macode, devcode, mccode,sccode);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/pm/mes/enableDevice.action")
	@ResponseBody
	public Map<String, Object> enableDevice(String decode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SMTFeedService.enableDevice(decode);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/pm/mes/stopDevice.action")
	@ResponseBody
	public Map<String, Object> stopDevice(String decode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SMTFeedService.stopDevice(decode);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/pm/mes/addSMTFeed.action")
	@ResponseBody
	public Map<String, Object> addSMTFeed(String mlscode, String macode, String table, String barcode, String mccode, String licode, String devcode, String sccode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SMTFeedService.addSMTFeed(mlscode, macode, table, barcode, mccode, licode, devcode, sccode);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 执行筛选前判断作业单的料站表编号是否存在
	 * @param caller
	 * @param condition
	 * @return
	 */
	@RequestMapping("/pm/mes/beforeSMTFeedQuery.action")
	@ResponseBody
	public Map<String, Object> beforeSMTFeedQuery(String caller,String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("message", SMTFeedService.beforeSMTFeedQuery(caller,condition));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 工单切换
	 * @param mc_devcode
	 * @param ms_code
	 * @param mc_makecode
	 * @param mc_linecode
	 * @param mcCode
	 * @param makeCode
	 * @return
	 */
	@RequestMapping("/pm/mes/confirmChangeMake.action")
	@ResponseBody
	public Map<String, Object> confirmChangeMake(String mc_devcode,String mc_code,String mc_makecode,String mc_linecode,String mcCode,String makeCode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SMTFeedService.confirmChangeMake(mc_devcode,mc_code,mc_makecode,mc_linecode,mcCode,makeCode);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 导入备料单数据
	 * @param mc_devcode
	 * @param mc_code
	 * @param mc_makecode
	 * @param mc_linecode
	 * @param mp_code 备料单号
	 * @return
	 */
	@RequestMapping("/pm/mes/confirmImportMPData.action")
	@ResponseBody
	public Map<String, Object> confirmImportMPData(String mc_devcode,String mc_code,String mc_makecode,String mc_linecode,String mp_code,String sccode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SMTFeedService.confirmImportMPData(mc_devcode,mc_code,mc_makecode,mc_linecode,mp_code, sccode);
		modelMap.put("success", true);
		return modelMap;
	}
}
