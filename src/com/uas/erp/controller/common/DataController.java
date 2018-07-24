package com.uas.erp.controller.common;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.common.DataService;


@Controller
public class DataController {
	@Autowired
	private DataService dataService;
	
	/**
	 * 比对数据字典和所有用户表，获取两者差异
	 * @param session
	 * @param em_uu
	 * @return
	 */
	@RequestMapping("/common/dataTidy.action")  
	@ResponseBody 
	public Map<String, Object> dataTidy(HttpSession session, int em_uu) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
//		dataService.tidy();
		modelMap.put("success",true);
		modelMap.put("list1", dataService.insertDD());//得到数据字典中缺省的用户表信息
		modelMap.put("list2", dataService.createTables());//得到数据字典中存在而用户并没有创建的所有表
//		modelMap.put("list3", dataService.alterTable());//得到数据字典中存在的表字段，而用户表结构缺省的字段信息
//		modelMap.put("list4", dataService.insertDDD());//获取数据字典中缺省的用户表的相关字段信息
//		modelMap.put("list5", dataService.eqType());//比对用户表结构每个字段类型与数据字典中记录存在差异的
		return modelMap;
	}
	
	/**
	 * 得到数据字典中缺省的用户表信息
	 * @param session
	 * @param em_uu
	 * @return
	 */
	@RequestMapping("/common/dataDD.action")  
	@ResponseBody 
	public Map<String, Object> dataDD(HttpSession session, int em_uu) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success",true);
		modelMap.put("list", dataService.insertDD());
		return modelMap;
	}	
	
	/**
	 * 得到数据字典中存在而用户并没有创建的所有表
	 * @param session
	 * @param em_uu
	 * @return
	 */
	@RequestMapping("/common/dataTable.action")  
	@ResponseBody 
	public Map<String, Object> dataTable(HttpSession session, int em_uu) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success",true);
		modelMap.put("list", dataService.createTables());
		return modelMap;
	}	
	
	/**
	 * 根据用户表名去获取该表结构信息，显示到页面以将改变信息插入数据字典中
	 * @param session
	 * @param tablename 用户表名
	 * @return
	 */
	@RequestMapping("/common/getDetail.action")  
	@ResponseBody 
	public Map<String, Object> getDetail(HttpSession session, String tablename) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("list",dataService.getDetailByTablename(tablename));
		modelMap.put("success",true);
		return modelMap;
	}
	
	
	/**
	 * 根据用户表名去获取该表结构信息，显示到页面以将改变信息插入数据字典中
	 * @param session
	 * @param tablename 用户表名
	 * @return
	 */
	@RequestMapping("/common/getProperty.action")  
	@ResponseBody 
	public Map<String, Object> getProperty(HttpSession session, String tablename) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("list",dataService.getPropertyByTablename(tablename));
		modelMap.put("success",true);
		return modelMap;
	}
	/**
	 * 获取索引配置
	 * */
	@RequestMapping("/common/getColumnIndex.action")  
	@ResponseBody 
	public Map<String, Object> getColumnIndex(HttpSession session,String tablename) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("list",dataService.getColumnIndexByTablename(tablename));
		modelMap.put("success",true);
		return modelMap;
	}
	
	/**
	 * 根据表名去数据字典中获取该表结构详细信息，再发过来新建该用户表
	 * @param session
	 * @param tablenames 选择要新建的表
	 * @return
	 */
	@RequestMapping("/common/createTable.action")  
	@ResponseBody 
	public Map<String, Object> createTable(HttpSession session, String[] tablenames) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dataService.createTable(tablenames);
		modelMap.put("success",true);
		return modelMap;
	}
	
	@RequestMapping("/common/test.action")  
	@ResponseBody 
	public Map<String, Object> test(HttpSession session, int start, int limit) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success",true);
		modelMap.put("list", dataService.test(start, limit));
		modelMap.put("totalCount", dataService.testGetTotal());
		return modelMap;
	}
	
	@RequestMapping("/common/addData.action")  
	@ResponseBody 
	public Map<String, Object> addData(HttpSession session, int start, int limit) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success",true);
		modelMap.put("list", dataService.add(start, limit, 1));
		modelMap.put("totalCount", dataService.getTotal(1));
		return modelMap;
	}
	
	@RequestMapping("/common/addTable.action")  
	@ResponseBody 
	public Map<String, Object> addTable(HttpSession session, int start, int limit) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success",true);
		modelMap.put("list", dataService.add(start, limit, 2));
		modelMap.put("totalCount", dataService.getTotal(2));
		return modelMap;
	}
	
	@RequestMapping("/common/addTField.action")  
	@ResponseBody 
	public Map<String, Object> addTField(HttpSession session, int start, int limit) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success",true);
		modelMap.put("list", dataService.add(start, limit, 3));
		modelMap.put("totalCount", dataService.getTotal(3));
		return modelMap;
	}
	
	@RequestMapping("/common/addDField.action")  
	@ResponseBody 
	public Map<String, Object> addDField(HttpSession session, int start, int limit) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success",true);
		modelMap.put("list", dataService.add(start, limit, 4));
		modelMap.put("totalCount", dataService.getTotal(4));
		return modelMap;
	}
	
	@RequestMapping("/common/alterTable.action")  
	@ResponseBody 
	public Map<String, Object> alter(HttpSession session, String fields) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dataService.alter(fields);
		modelMap.put("success",true);
		return modelMap;
	}
	
	@RequestMapping("/common/addFields.action")  
	@ResponseBody 
	public Map<String, Object> addFields(HttpSession session, String fields) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String error = dataService.addFields(fields);
		if(error != ""){
			modelMap.put("error", "数据字典中不存在表" + error + ",请先回到上面插入表" + error + "到数据字典");
		} else {
			modelMap.put("success",true);			
		}
		return modelMap;
	}
}
