package com.uas.erp.controller.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.bind.Constant;
import com.uas.erp.model.Employee;
import com.uas.erp.model.GridPanel;
import com.uas.erp.service.common.DbfindService;

@Controller
public class DbfindController {
	@Autowired
	private DbfindService dbfindService;
	/**
	 * grid的dbfind
	 */
	@RequestMapping(value = "/common/dbfind.action")
	@ResponseBody
	public Map<String, Object> getGrid(HttpServletRequest req,HttpSession session, String which, String caller, String field, String condition, String ob,
			int page, int pageSize) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		GridPanel gridPanel = new GridPanel();
		String language = (String) session.getAttribute("language");
		boolean isCloud=Constant.UAS_CLOUD.equals(req.getParameter("_config"));
		if (which.equals("grid")) {
			gridPanel = dbfindService.getDbfindGridByCaller(caller, condition, ob, page, pageSize, language,isCloud);
		} else {
			gridPanel = dbfindService.getDbfindGridByField(caller, field, condition, page, pageSize,isCloud);
		}
		modelMap.put("dbfinds", gridPanel.getDbfinds());
		modelMap.put("fields", gridPanel.getGridFields());
		modelMap.put("columns", gridPanel.getGridColumns());
		modelMap.put("data", gridPanel.getDataString());
		modelMap.put("reset", gridPanel.isAllowreset());
		modelMap.put("autoHeight", gridPanel.isAutoHeight());
		return modelMap;
	}

	/**
	 * 分页查询
	 * 
	 * @return count 结果总数
	 */
	@RequestMapping(value = "/common/dbfindCount.action")
	@ResponseBody
	public Map<String, Object> getCount(HttpServletRequest req,String which, String caller, String field, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		boolean isCloud=Constant.UAS_CLOUD.equals(req.getParameter("_config"));
		if (which.equals("grid")) {
			modelMap.put("count", dbfindService.getCountByCaller(caller, condition,isCloud,false));
		} else {
			modelMap.put("count", dbfindService.getCountByField(caller, field, condition,isCloud,false));
		}
		return modelMap;
	}

	/**
	 * dbfind单元格blur之后，自动获取数据，如果数据不止一行，则显示dbfind-window 否则就直接将值赋给指定字段
	 */
	@RequestMapping(value = "/common/autoDbfind.action")
	@ResponseBody
	public Map<String, Object> autoGetData(HttpServletRequest req,HttpSession session, String which, String caller, String ob, String field, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String language = (String) session.getAttribute("language");
		boolean isCloud=Constant.UAS_CLOUD.equals(req.getParameter("_config"));
		int count = 0;
		if (which.equals("grid")) {
			count = dbfindService.getCountByCaller(caller, condition,isCloud,true);
		} else {
			count = dbfindService.getCountByField(caller, field, condition,isCloud,true);
		}
		if (count == 1) {
			GridPanel gridPanel = new GridPanel();
			if (which.equals("grid")) {
				gridPanel = dbfindService.getDbfindGridByCaller(caller, condition, ob, 1, 1, language,isCloud);
			} else {
				gridPanel = dbfindService.getDbfindGridByField(caller, field, condition, 1, 1,isCloud);
			}
			modelMap.put("dbfinds", gridPanel.getDbfinds());
			modelMap.put("data", gridPanel.getDataString());
			modelMap.put("reset", gridPanel.isAllowreset());
		}
		return modelMap;
	}

	/**
	 * 通过caller和field获得Dbfind的配置信息
	 **/
	@RequestMapping(value = "/common/getDbFindSetUI.action")
	@ResponseBody
	public Map<String, Object> getDbFindSetUI(HttpServletRequest req,String caller, String field, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		boolean isCloud=Constant.UAS_CLOUD.equals(req.getParameter("_config"));
		JSONObject obj = dbfindService.getDbFindSetUIByField(caller, field, condition,isCloud);
		modelMap.put("formdata", obj.get("formdata"));
		modelMap.put("griddata", obj.get("griddata"));
		modelMap.put("fields", obj.get("fields"));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 通过关联caller获得Dbfind的查找字段配置信息
	 **/
	@RequestMapping(value = "/common/getDbFindFields.action")
	@ResponseBody
	public Map<String, Object> getDbFindFields(String table) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("findfields", dbfindService.getDbFindFields(table));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除部分字段
	 * */
	@RequestMapping(value = "/common/deleteDbFindField.action")
	@ResponseBody
	public Map<String, Object> deleteDbFindField(String field, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dbfindService.deleteDbFindField(field, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存 修改
	 * */
	@RequestMapping(value = "/common/saveDbfindSetUI.action")
	@ResponseBody
	public Map<String, Object> saveDbFindSetUI(HttpSession session, String caller, String formStore, String gridStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", dbfindService.saveDbFindSetUI(caller, formStore, gridStore));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除Dbfind
	 * */
	@RequestMapping(value = "/common/deleteDbfindSetUI.action")
	@ResponseBody
	public Map<String, Object> deleteDbFindSetUI(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dbfindService.deleteDbfindSetUI(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获得DbFindSetGrid
	 * */
	@RequestMapping(value = "/common/getDbFindSetGrid.action")
	@ResponseBody
	public Map<String, Object> getDbFindSetGrid(String caller, String field) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", dbfindService.getDbFindSetGridByCaller(caller, field));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获得DbFindSetGrid 查找字段
	 * */
	@RequestMapping(value = "/common/getDbFindSetGridFields.action")
	@ResponseBody
	public Map<String, Object> getDbFindSetGridFields(HttpSession session, String caller, String field) {
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", dbfindService.getDbFindSetGridFieldsByCallerAndFields(employee, caller, field));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存DbFindSetGrid
	 * */
	@RequestMapping(value = "/common/saveDbFindSetGrid.action")
	@ResponseBody
	public Map<String, Object> getDbFindSetGridFields(String caller, String field, String table,String dgfield, String gridStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dbfindService.saveDbFindSetGrid(caller, field, table,dgfield, gridStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 DBFindSetGrid
	 * */
	@RequestMapping(value = "/common/deleteDbFindSetGrid.action")
	@ResponseBody
	public Map<String, Object> getDbFindSetGridFields(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dbfindService.deleteDBFindSetGrid(id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 通过所需表获得下拉相关下拉项caller
	 **/
	@RequestMapping(value = "/common/getDlccallerByTables.action")
	@ResponseBody
	public Map<String, Object> getDlccallerByTables(String table,String fields) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("callers", dbfindService.getDlccallerByTable(table,fields));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 获取tpl模板和搜索数据
	 * @param table
	 * @param fields
	 * @return
	 */
	@RequestMapping(value = "/common/getSearchData.action")
	@ResponseBody
	public Map<String, Object> getSearchData(String table,String field,String condition,String configSearchCondition,String name,String caller,String type,String searchTpl) {
		String tpl=" ";
		String searchtable=" ";
		String searchCondition=" ";
		String releaseField=" ";
		String data="";
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<String> result=dbfindService.getSearchData(table,field,condition,configSearchCondition,name,caller,type,searchTpl);
		if(result!=null && !result.isEmpty() && result.size()>1){
			tpl=result.get(0);
			searchtable=result.get(1);
			searchCondition=result.get(2);
			releaseField=result.get(3);
			data=result.get(4);
		}else if(result!=null && !result.isEmpty() && result.size()==1){
			data=result.get(0);
		}
		modelMap.put("tpl", tpl);
		modelMap.put("searchtable", searchtable);
		modelMap.put("searchCondition", searchCondition);
		modelMap.put("releaseField", releaseField);
		modelMap.put("data", data);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping(value = "/common/getComboBoxTriggerData.action")
	@ResponseBody
	public Map<String, Object> getComboBoxTriggerData(String userid,String text){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", dbfindService.getComboBoxTriggerData(userid,text));
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping(value = "/common/saveToCommonWords.action")
	@ResponseBody
	public Map<String, Object> saveToCommonWords(String value){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		
		modelMap.put("success", dbfindService.saveToCommonWords(value));
		return modelMap;
	}
	
	@RequestMapping(value = "/common/deleteCommonWords.action")
	@ResponseBody
	public Map<String, Object> deleteCommonWords(String id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		
		modelMap.put("success", dbfindService.deleteCommonWords(id));
		return modelMap;
	}
	
	@RequestMapping(value = "/common/getOrderChange.action")
	@ResponseBody
	public Map<String, Object> orderChange(HttpServletRequest req,HttpSession session, String which, String caller, String ob, String field, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String language = (String) session.getAttribute("language");
		boolean isCloud=Constant.UAS_CLOUD.equals(req.getParameter("_config"));
		GridPanel gridPanel = new GridPanel();
		if (which.equals("grid")) {
			gridPanel = dbfindService.getDbfindGridByCaller(caller, condition, ob, 1, 1000, language,isCloud);
		} else {
			gridPanel = dbfindService.getDbfindGridByField(caller, field, condition, 1, 1,isCloud);
		}
		modelMap.put("dbfinds", gridPanel.getDbfinds());
		modelMap.put("data", gridPanel.getDataString());
		return modelMap;
	}
}
