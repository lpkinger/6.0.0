package com.uas.erp.controller.ma;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.ExcelUtil;
import com.uas.erp.model.Employee;
import com.uas.erp.service.ma.SearchTemplateService;

@Controller
public class SearchTemplateController {

	@Autowired
	private SearchTemplateService searchTemplateService;

	/**
	 * 保存
	 */
	@RequestMapping("/ma/search/temp/s.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String title, String datas, String condition, String sorts, String limits) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		searchTemplateService.save(caller, title, datas, condition, sorts, limits);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/ma/search/temp/u.action")
	@ResponseBody
	public Map<String, Object> update(String caller, Integer sId, String datas, String condition, String sorts, String limits,
			String preHook) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		searchTemplateService.update(caller, sId, datas, condition, sorts, limits, preHook);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改方案描述
	 */
	@RequestMapping("/ma/search/temp/t.action")
	@ResponseBody
	public Map<String, Object> updateTitle(String title, Integer sId) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		searchTemplateService.updateTitle(title, sId);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * （未经任何修改的）复制方案
	 */
	@RequestMapping("/ma/search/temp/c.action")
	@ResponseBody
	public Map<String, Object> copy(Integer sId, String title) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		searchTemplateService.copy(title, sId);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/ma/search/temp/d.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, Integer sId) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		searchTemplateService.delete(caller, sId);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 取模板
	 */
	@RequestMapping("/ma/search/temp/g.action")
	@ResponseBody
	public Map<String, Object> get(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", searchTemplateService.getSearchTemplates(caller));
		// 最近一次使用的模板ID
		modelMap.put("lastId", searchTemplateService.getLastSearchLog(caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 记录选择的模板
	 */
	@RequestMapping("/ma/search/log.action")
	@ResponseBody
	public Map<String, Object> log(String caller, Integer sId) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		searchTemplateService.log(caller, sId);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 数据字典关系
	 */
	@RequestMapping("/ma/search/relation.action")
	@ResponseBody
	public Map<String, Object> getRelation(String tables) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", searchTemplateService.getRelation(tables));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 查找数据
	 */
	@RequestMapping("/common/search.action")
	@ResponseBody
	public Map<String, Object> getData(Integer sId, String filter, String sorts, Integer start, Integer end) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("datas", searchTemplateService.getData(sId, JSON.parseObject(filter), sorts, start, end).getResultList());
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 按模板导出数据
	 * 
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/common/search/excel.xls")
	@ResponseBody
	public ModelAndView exportExcel(HttpSession session, Integer sId, String filter, String sorts, String columns, String title)
			throws UnsupportedEncodingException {
		columns = new String(columns.getBytes("ISO-8859-1"), "UTF-8");
		filter = new String(filter.getBytes("ISO-8859-1"), "UTF-8");
		Employee employee = (Employee) session.getAttribute("employee");
		return new ModelAndView(new ExcelUtil(BaseUtil.parseGridStoreToMaps(columns), searchTemplateService.getData(sId,
				JSON.parseObject(filter), sorts, 1, ExcelUtil.maxSize), title, employee, null).getView());
	}

	/**
	 * 复制方案到新的导航栏，检查导航栏及方案是否存在
	 */
	@RequestMapping("/ma/search/temp/checkCaller.action")
	@ResponseBody
	public Map<String, Object> checkCaller(String caller, String title) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("result", searchTemplateService.checkCaller(caller, title));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 复制方案到新的导航栏
	 */
	@RequestMapping("/ma/search/temp/duplTemp.action")
	@ResponseBody
	public Map<String, Object> duplTemp(String caller, Integer sId, String title) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		searchTemplateService.duplTemp(caller, sId, title);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存APP端方案是否启用
	 */
	@RequestMapping("/ma/search/saveAppuse.action")
	@ResponseBody
	public Map<String, Object> saveAppuse(Integer st_id, Integer check) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		searchTemplateService.saveAppuse(st_id, check);
		modelMap.put("success", true);
		return modelMap;
	}
}
