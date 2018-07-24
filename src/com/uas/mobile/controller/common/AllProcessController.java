package com.uas.mobile.controller.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.mobile.model.AllProcess;
import com.uas.mobile.service.AllProcessService;

@Controller("allProcessController")
public class AllProcessController {

	@Autowired
	private AllProcessService allProcessService;
	
	/**
	 * 根据请求发送的时间与当前最新的待办事宜的时间比较获取需要推送的待办事宜，并将其推送给客户端
	 * @param request
	 * @param response
	 * @param session
	 * @param currentMaster 用户当前登录的账套
	 * @param employeeCode
	 * @param deviceToken 设备的Token(注册信鸽之后的设备唯一标识)
	 * @param requestid 用于比较最新的记录
	 * @param r 是否响铃 1是 0否
	 * @param v 是否振动 1是 0否
	 * @return
	 */
	@RequestMapping("/mobile/getnewprocess.action")
	@ResponseBody
	public Map<String, Object> getNewProcess(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, String employeeCode, String requestid, String currentMaster){
		response.setContentType("application/json");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if(employeeCode != null && requestid != null && currentMaster != null) {
			long lastTime = allProcessService.getLastTime(employeeCode, currentMaster);//当前待办事宜的最大的id
			long requestTime = Long.parseLong(requestid);//请求发送过来的id
			//第一次发送请求，requestidInt为0，不做处理
			//当requestTime小于lastTime时说明有新的未查看的待办事宜
			List<AllProcess> newProcessList = new ArrayList<AllProcess>() ;
			if(requestTime < lastTime && requestTime != 0) {
				 newProcessList = allProcessService.getAllProcessSince(employeeCode, requestTime, currentMaster);
			}
			List<AllProcess> allProcessList = allProcessService.getAllProcessByDealPersonCode(employeeCode, currentMaster);
			modelMap.put("lastid", lastTime);
			modelMap.put("success", true);
			//放list对象
			modelMap.put("allProcess", allProcessList);
			modelMap.put("newProcess", newProcessList);
		} else {
			modelMap.put("success", false);
			modelMap.put("reason", "非正常请求");
		}
		return modelMap;
	}
	
	/**
	 * 获取当前所有的待办事宜列表
	 * @param request
	 * @param response
	 * @param employeeCode
	 * @param currentMaster 用户当前登录的账套
	 * @return
	 */
	@RequestMapping("/mobile/getallprocess.action")
	@ResponseBody
	public Map<String, Object> getAllProcess(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, String employeeCode, String currentMaster){
		response.setContentType("application/json");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if(employeeCode!=null && currentMaster!=null){
			List<AllProcess> allProcess = new ArrayList<AllProcess>();
			allProcess = allProcessService.getAllProcessByDealPersonCode(employeeCode, currentMaster);
			int count = 0;
			if( ! CollectionUtils.isEmpty(allProcess)) {
				count = allProcess.size();
			}
			modelMap.put("count", count);
			modelMap.put("success", true);
			modelMap.put("allProcess", allProcess);
			modelMap.put("sessionId", request.getSession().getId());
		} else {
			modelMap.put("success", false);
			modelMap.put("reason", "非正常请求");
			modelMap.put("sessionId", request.getSession().getId());
		}
		return modelMap;
	}
	
	
}
