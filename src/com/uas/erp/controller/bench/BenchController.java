package com.uas.erp.controller.bench;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.interceptor.InterceptorUtil;
import com.uas.erp.model.Bench;
import com.uas.erp.model.Employee;
import com.uas.erp.service.bench.BenchService;

@Controller
public class BenchController {
	@Autowired
	BenchService benchService;
	
	//是否需要权限控制
	private boolean noControl(HttpServletRequest req){
		Employee employee = (Employee) req.getSession().getAttribute("employee");
		if (InterceptorUtil.noControl(req)||"admin".equals(employee.getEm_type())){
			return true;
		}
		return false;
	}
	
	@RequestMapping(value = "/bench/getBench.action")
	@ResponseBody
	public Map<String,Object> getBench(HttpServletRequest req, HttpSession session, String bccode, String condition){	
		Map<String,Object> map = new HashMap<String,Object>();
		boolean noControl = noControl(req);
		Employee employee= (Employee) session.getAttribute("employee");
		boolean isCloud = Constant.UAS_CLOUD.equals(req.getParameter("_config"));
		Bench bench = benchService.getBench(employee, bccode, isCloud, noControl, condition);
		map.put("bench", bench);
		map.put("success", true);
		return map;
	}
	
	@RequestMapping(value = "/bench/getBenchSenceConfig.action")
	@ResponseBody
	public Map<String,Object> getBenchSence(HttpServletRequest req, HttpSession session, String condition, String bscode, Integer page,Integer pageSize){	
		boolean noControl = noControl(req);
		/**
		 * 配置来源 区分是否从优软平台获取配置
		 **/
		boolean isCloud = Constant.UAS_CLOUD.equals(req.getParameter("_config"));
		Employee employee= (Employee) session.getAttribute("employee");
		Map<String,Object> map = benchService.getBenchSceneConfig(employee, bscode, condition, page, pageSize, isCloud, noControl);
		map.put("success", true);
		return map;
	}
	
	@RequestMapping(value = "/bench/datalist/data.action")
	@ResponseBody
	public Map<String,Object> getBenchSenceGridData(HttpServletRequest req, HttpSession session, String condition, String bscode, Integer page, Integer pageSize, 
			String orderby,boolean fromHeader){	
		boolean noControl = noControl(req);
		/**
		 * 配置来源 区分是否从优软平台获取配置
		 **/
		boolean isCloud = Constant.UAS_CLOUD.equals(req.getParameter("_config"));
		Employee employee= (Employee) session.getAttribute("employee");
		Map<String,Object> map = benchService.getBenchSceneGridData(employee, bscode, condition, page, pageSize, orderby, isCloud, noControl,fromHeader);
		map.put("success", true);
		return map;
	}
	
	@RequestMapping(value = "/bench/getFlowchartConfig.action")
	@ResponseBody
	public JSONObject getFlowchartConfig(HttpServletRequest req, String bccode){	
		JSONObject flowchartConfig = benchService.getFlowchartConfig(bccode);
		flowchartConfig.put("success", true);
		return flowchartConfig;
	}
	
	/**
	 * 搜索工作台内容
	 **/
	@RequestMapping(value = "/bench/searchBench.action")
	@ResponseBody
	public Map<String,Object> searchBench(HttpServletRequest req, String benchcode, String search){	
		Map<String,Object> map = new HashMap<String,Object>();
		boolean noControl = noControl(req);
		boolean isCloud = Constant.UAS_CLOUD.equals(req.getParameter("_config"));
		map.put("benchs", benchService.searchBench(benchcode, isCloud, noControl, search));
		map.put("success", true);
		return map;
	}
	
	/**
	 * 判断对应工作台、业务、场景是否存在
	 **/
	@RequestMapping(value = "/bench/isExist.action")
	@ResponseBody
	public Map<String,Object> isExist(HttpServletRequest req, HttpSession session, String bench, String business, String scene){	
		Map<String,Object> map = new HashMap<String,Object>();
		boolean noControl = noControl(req);
		boolean isCloud = Constant.UAS_CLOUD.equals(req.getParameter("_config"));
		Employee employee= (Employee) session.getAttribute("employee");
		map.put("exist", benchService.isExist(isCloud, noControl, employee, bench, business, scene));
		map.put("success", true);
		return map;
	}
}
