package com.uas.erp.controller.ma;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.ma.InitGuideService;

/**
 * 初始化向导
 * 
 * @author yingp
 * 
 */
@Controller
public class InitGuideController {

	@Autowired
	private InitGuideService initGuideService;

	/**
	 * 校验基础配置和基础数据
	 * 
	 * @return
	 */
	@RequestMapping("/ma/guide/checkbase.action")
	@ResponseBody
	public String checkBase() {
		return initGuideService.checkBaseTables();
	}
	
	/**
	 * 导入前校验是否必须资料已导入
	 * 
	 * @return
	 */
	@RequestMapping("/ma/guide/checkbefore.action")
	@ResponseBody
	public String checkBefore(String tables) {
		return initGuideService.checkBaseTables();
	}
	
	@RequestMapping("/ma/guide/checktab.action")
	@ResponseBody
	public String checkTab(String t) {
		return initGuideService.checkTab(t);
	}
	
	@RequestMapping("/ma/guide/repair.action")
	@ResponseBody
	public String repair(String tbs) {
		initGuideService.repairTabs(tbs);
		return "success";
	}
	
	@RequestMapping("/ma/guide/count.action")
	@ResponseBody
	public int getCount(String t, String c) {
		return initGuideService.getCount(t, c);
	}
}
