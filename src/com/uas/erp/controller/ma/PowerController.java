package com.uas.erp.controller.ma;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.RelativeSearch;
import com.uas.erp.service.common.SingleFormItemsService;
import com.uas.erp.service.ma.PowerService;

@Controller
public class PowerController {

	@Autowired
	private PowerService powerService;

	@Autowired
	private SingleFormItemsService singleFormItemsService;
	
	@RequestMapping(value = "/ma/getPowerData.action", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getData(HttpServletRequest request,String condition, int page, int pageSize,String tableName) {
		Map<String,Object> map = new HashMap<String,Object>();
		List<Map<String, Object>> modelMap =powerService.getPowerData(condition
				, page, pageSize,tableName);
		map.put("data", modelMap);
		return map;
	}
	@RequestMapping(value = "/ma/getPowerCount.action", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getCount(HttpServletRequest request, String condition,String tableName) {
		Map<String, Object> modelMap =new HashMap<String, Object>();
		modelMap.put("count", powerService.getPowerCount(condition,tableName));		
		return modelMap;
	}
	/**
	 * 保存
	 */
	@RequestMapping("/ma/savePower.action")
	@ResponseBody
	public Map<String, Object> save(String save, String update) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (save != null && !save.equals("")) {
			powerService.save(save);
		}
		if (update != null && !update.equals("")) {
			powerService.update(update);
		}
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/ma/deletePower.action")
	@ResponseBody
	public Map<String, Object> delete(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		powerService.delete(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 特殊权限
	 */
	@RequestMapping("/ma/power/getSysSpecialPowers.action")
	@ResponseBody
	public Map<String, Object> getSysSpecialPowers(String caller, Integer em_id, Integer jo_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 特殊权限资源
		modelMap.put("data", powerService.getSysSpecialPowers(caller));
		// 特殊权限分配
		if (em_id != null) {
			modelMap.put("specialPower", powerService.getSpecialPowerByEmpl(caller, em_id));
		} else if (jo_id != null) {
			modelMap.put("specialPower", powerService.getSpecialPowerByJob(caller, jo_id));
		}
		// 关联查询方案
		List<RelativeSearch> searchs = singleFormItemsService.getRelativeSearchForPower(caller);
		// 关联查询权限分配
		if (searchs != null) {
			modelMap.put("relativeSearch", searchs);
			if (em_id != null){
				modelMap.put("relativeLimit", singleFormItemsService.getRelativeSearchLimitsByEmpl(caller, em_id));
			} else if (jo_id != null) {
				modelMap.put("relativeLimit", singleFormItemsService.getRelativeSearchLimitsByJob(caller, jo_id));
			}
		}
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 角色特殊权限
	 */
	@RequestMapping("/ma/power/getSysSpecialPowersByRole.action")
	@ResponseBody
	public Map<String, Object> getSysRoleSpecialPowers(String caller, Integer ro_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		//特殊权限
		modelMap.put("data", powerService.getSysSpecialPowers(caller));
		//角色特殊权限
		modelMap.put("specialPower", powerService.getSpecialPowerByRole(caller, ro_id));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存特殊权限
	 */
	@RequestMapping("/ma/power/saveSysSpecialPowers.action")
	@ResponseBody
	public Map<String, Object> saveSysSpecialPowers(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		powerService.saveSysSpecialPowers(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 不同岗位间权限复制
	 */
	@RequestMapping("/ma/power/copypower.action")
	@ResponseBody
	public String copyPower(Integer f, String t) {
		powerService.copyPower(f, t);
		return "success";
	}

	/**
	 * 从权限标准库复制权限
	 */
	@RequestMapping("ma/power/copypowerFromStandard.action")
	@ResponseBody
	public Map<String, Object> copypowerFromStandard(String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		powerService.copypowerFromStandard(param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * saas版从对应标准库复制权限
	 * */
	@RequestMapping("ma/power/SaasCopypowerFromStandard.action")
	@ResponseBody
	public Map<String, Object> SaasCopypowerFromStandard(String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		powerService.copypowerFromStandard(param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 特殊权限删除
	 */
	@RequestMapping("/ma/power/deleteSysSpecialPowerById.action")
	@ResponseBody
	public Map<String, Object> deleteSysSpecialPowerById(int id, String caller,Integer sbid) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		powerService.deleteSysSpecialPowerById(id,sbid);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 权限同步
	 * */
	@RequestMapping("/ma/power/syncPower.action")
	@ResponseBody
	public Map<String,Object> syncPower(HttpSession session,String caller, String to, String data){
		Map<String,Object> modelMap=new HashMap<String, Object>();
		modelMap.put("success",true);
		powerService.syncPower(caller,to,data);
		return modelMap;
	}
	
	/**
	 * 权限同步
	 * */
	@RequestMapping("/ma/power/refreshPower.action")
	@ResponseBody
	public Map<String,Object> refreshPower(HttpSession session,String to){
		Map<String,Object> modelMap=new HashMap<String, Object>();
		modelMap.put("success",true);
		powerService.refreshPower(to);
		return modelMap;
	}
	
	/**
	 * 获取场景按钮权限
	 * */
	@RequestMapping("/ma/power/getSceneBtnPowers.action")
	@ResponseBody
	public Map<String,Object> getSceneBtnPowers(String benchcode,Integer joid, Integer emid){
		Map<String,Object> modelMap=new HashMap<String, Object>();
		modelMap.put("powers",powerService.getSceneBtnPowers(benchcode,joid, emid));
		modelMap.put("success",true);
		
		return modelMap;
	}
	
	/**
	 * 保存场景按钮权限
	 * */
	@RequestMapping("/ma/power/saveSceneBtnPowers.action")
	@ResponseBody
	public Map<String,Object> saveSceneBtnPowers(String benchcode,String joid, String emid,String data){
		Map<String,Object> modelMap=new HashMap<String, Object>();
		powerService.saveSceneBtnPowers(benchcode,joid, emid,data);
		modelMap.put("success",true);
		
		return modelMap;
	}
}
