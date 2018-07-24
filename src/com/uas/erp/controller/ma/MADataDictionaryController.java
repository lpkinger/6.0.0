package com.uas.erp.controller.ma;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.StringUtil;
import com.uas.erp.model.DataDictionary;
import com.uas.erp.model.Page;
import com.uas.erp.service.ma.MADataDictionaryService;

@Controller
public class MADataDictionaryController {

	@Autowired
	private MADataDictionaryService maDataDictionaryService;

	/**
	 * 数据字典明细
	 * 
	 * @param session
	 * @param formStore
	 * @param table
	 * @return
	 */
	@RequestMapping("/ma/getDataDictionary.action")
	@ResponseBody
	public Map<String, Object> get(String formStore, String table) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("datadictionary", maDataDictionaryService.getDataDictionary(table));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 数据字典
	 * 
	 * @param session
	 * @param formStore
	 * @param table
	 * @return
	 */
	@RequestMapping("/ma/getDataDictionaries.action")
	@ResponseBody
	public Map<String, Object> getAll(String formStore, String tables) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("datas", maDataDictionaryService.getDataDictionaries(tables));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 查找数据字典
	 * 
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/ma/dataDictionary/search.action")
	@ResponseBody
	public Map<String, Object> search(String query, int page, int start, int limit)
			throws UnsupportedEncodingException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Page<DataDictionary> p = maDataDictionaryService.getPageDataDictionary(StringUtil.unescape(query), page, start,
				limit);
		modelMap.put("totalCount", p.getTotalCount());
		modelMap.put("datas", p.getTarget());
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 使用oracle内置视图
	 * */
	@RequestMapping("/ma/dataDictionary/alter.action")
	@ResponseBody
	public Map<String, Object> alter(String Col_update,String Col_create,String Col_remove,String Ind_update,String Ind_create,String Ind_remove,String formStore,String gridStore){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		maDataDictionaryService.alter(Col_update,Col_create,Col_remove,Ind_update,Ind_create,Ind_remove,formStore,gridStore);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 获取表关联关系
	 * */
	@RequestMapping(value="/ma/getDatarelations.action",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getRelations(String tablename){
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("relations", maDataDictionaryService.getDataRelations(tablename));
		map.put("relations_col_comments",maDataDictionaryService.getRelation_Col_Comments(tablename));
		map.put("relations_tab_comments",maDataDictionaryService.getRelation_Tab_Comments(tablename));
		map.put("success",true);
		return map;
	}
	/**
	 * 根据表名刷新表中varchar2类型字段对应formdetail，detailgrid，initdetail配置的字段长度
	 * */
	@RequestMapping("/ma/dataDictionary/refresh.action")
	@ResponseBody
	public Map<String, Object> refresh(String tablename){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		maDataDictionaryService.refresh(tablename);
		modelMap.put("success", true);
		return modelMap;
	}
	
}
