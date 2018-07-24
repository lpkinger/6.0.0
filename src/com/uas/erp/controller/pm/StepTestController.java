package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.pm.StepTestService;

@Controller
public class StepTestController {
    @Autowired 
    private StepTestService stepTestService;
	
    
    /**
     * 根据录入的资源编号获取资源相关信息
     * @param condition
     * @return
     */
	@RequestMapping("/pm/mes/getSourceM.action")
	@ResponseBody
	public Map<String, Object> getSourceM(String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", stepTestService.getSourceM(condition));
		modelMap.put("success", true);
		return modelMap;
	}
    /**
     * 根据选择的资源编号作业单号等获取主表数据
     * @param condition
     * @return
     */
	@RequestMapping("/pm/mes/getFormStore.action")
	@ResponseBody
	public Map<String, Object> getFormStore(String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", stepTestService.getFormStore(condition));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 判断输入的序列号工序编号是否等于当前工序
	 * @param makecode
	 * @param stepcode
	 * @param mscode
	 * @return
	 */
	@RequestMapping("/pm/mes/checkStep.action")
	@ResponseBody
	public Map<String, Object> checkStep(String makecode,String stepcode,String mscode,String mccode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",stepTestService.checkStep(makecode,stepcode,mscode,mccode));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 确认合格通过
	 * @param mcd_stepcode
	 * @param mc_code
	 * @param sc_code
	 * @param ms_code
	 * @return
	 */
	@RequestMapping("/pm/mes/confirmQualified.action")
	@ResponseBody
	public Map<String, Object> confirmQualified(String mcd_stepcode,String mc_code,String sc_code,String ms_code,String makecode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",stepTestService.confirmQualified(mcd_stepcode,mc_code,sc_code,ms_code,makecode));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存不良原因
	 * @param mcd_stepcode
	 * @param mc_code
	 * @param sc_code
	 * @param ms_code
	 * @param bc_reason
	 * @return
	 */
	@RequestMapping("/pm/mes/saveBadReason.action")
	@ResponseBody
	public Map<String, Object> saveBadReason(String mcd_stepcode,String mc_code,String sc_code,String ms_code,String bc_reason,String bc_remark) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		stepTestService.saveBadReason(mcd_stepcode,mc_code,sc_code,ms_code,bc_reason,bc_remark);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 
	 * @param mcd_stepcode
	 * @param mc_code
	 * @param sc_code
	 * @param ms_code
	 * @param st_rcode
	 * @return
	 */	
	@RequestMapping("/pm/mes/confirmRepairStep.action")
	@ResponseBody
	public Map<String, Object> confirmRBad(String mcd_stepcode,String mc_code,String sc_code,String ms_code,String st_rcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",stepTestService.confirmRepairStep(mcd_stepcode,mc_code,sc_code,ms_code,st_rcode));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 根据不良组别获取不良原因store
	 * @param condition
	 * @return
	 */
	@RequestMapping("/pm/mes/getBadCode.action")
	@ResponseBody
	public Map<String, Object> getBadCode(String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",stepTestService.getBadCode(condition));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除序列号中的不良原因
	 * @param condition
	 * @return
	 */	
	@RequestMapping("/pm/mes/deleteTestBadCode.action")
	@ResponseBody
	public Map<String, Object> deleteTestBadCode(String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		stepTestService.deleteTestBadCode(condition);
		modelMap.put("success", true);
		return modelMap;
	}
		
	/**
	 * 获取不良组别
	 * @param condition
	 * @return
	 */
	@RequestMapping("/pm/mes/getBadGroup.action")
	@ResponseBody
	public Map<String, Object> getBadGroup() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",stepTestService.getBadGroup());
		modelMap.put("success", true);
		return modelMap;
	}
	
}
