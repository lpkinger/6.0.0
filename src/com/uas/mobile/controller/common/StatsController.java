package com.uas.mobile.controller.common;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.mobile.model.Stats;
import com.uas.mobile.service.StatsService;
@Controller
public class StatsController {
	@Autowired
	private StatsService statsService;
	@RequestMapping("/mobile/common/Stats.action")
	@ResponseBody
	public Map<String,Object> getStats(HttpServletRequest request,HttpSession session){
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("Stats", statsService.getStats());
		map.put("sessionId", request.getSession().getId());
		return map;
	}
	@RequestMapping("/mobile/common/getStatsByConfig.action")
	@ResponseBody
	public Map<String,Object> getStatsByConfig(HttpSession session,HttpServletRequest request,int Id,String Config){
		Map<String,Object> map=new HashMap<String, Object>();
	    Stats stats=statsService.getStats(Id, Config);
	    map.put("title", stats.getSt_title());
	    map.put("data", stats.getDatas());
	    map.put("keyField", stats.getSt_keyfield());
	    map.put("valueField",stats.getSt_valuefield());
	    map.put("sessionId", request.getSession().getId());
		return map;
	}
	
}
