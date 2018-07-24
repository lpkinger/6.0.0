package com.uas.erp.controller.common;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.drools.lang.dsl.DSLMapParser.mapping_file_return;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.JSONUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.Employee;
import com.uas.erp.model.GridPanel;
import com.uas.erp.service.common.DataListService;

/**
 * 处理前台datalist请求 根据caller，返回不同的datalist
 * 
 * @author yingp
 * @date 2012-07-31 15:01:17
 */
@Controller
public class DataListController {
	@Autowired
	private DataListService dataListService;

	@RequestMapping(value = "/common/datalist.action")
	@ResponseBody
	public Map<String, Object> getDataListGrid(HttpServletRequest req, HttpSession session, String caller, String condition, int page,
			int pageSize, Integer _f, String orderby,boolean fromHeader) {
		Boolean _self = (Boolean) req.getAttribute("_self");
		boolean _jobemployee = false;
		if ("true".equals(req.getParameter("_self")))
			_self = true;
		if ("true".equals(req.getParameter("_jobemployee")))
			_jobemployee = true;
		if (req.getAttribute("_jobemployee")!=null&&"true".equals(req.getAttribute("_jobemployee").toString())){
			_jobemployee = true;
		}

		/**
		 * 配置来源 区分是否从优软平台获取配置
		 **/
		boolean isCloud = Constant.UAS_CLOUD.equals(req.getParameter("_config"));
		Map<String, Object> modelMap = new HashMap<String, Object>();
		GridPanel gridPanel = dataListService.getDataListGridByCaller(caller, condition, page, pageSize, orderby, _self, _f, isCloud, fromHeader,_jobemployee);
		modelMap.put("defaultFilterCondition", gridPanel.getDefaultFilterCondition());
		modelMap.put("fields", gridPanel.getGridFields());
		modelMap.put("columns", gridPanel.getGridColumns());
		modelMap.put("basecolumns", gridPanel.getBaseColumns());
		modelMap.put("data", gridPanel.getDataString());
		modelMap.put("summarydata", gridPanel.getSummarydata());
		// grid行选择之后，要从selModel里面得到数据的字段，例如pu_id
		modelMap.put("keyField", gridPanel.getKeyField());
		// 同上，与主表字段对应的从表字段，方便从表的数据查询
		modelMap.put("pfField", gridPanel.getPfField());
		modelMap.put("url", gridPanel.getUrl());
		modelMap.put("relative", gridPanel.getRelative());
		modelMap.put("vastbutton", gridPanel.getVastbutton());
		modelMap.put("limits", gridPanel.getLimits());
		modelMap.put("_self", _self);
		modelMap.put("_jobemployee", _jobemployee);
		return modelMap;
	}
    /**  获取列表数据count
     *   @param caller 列表CALLER
     *   @param condition 列表查询条件
     *   @param fromHeader 存在筛选条件
     * 
     * */
	@RequestMapping(value = "/common/datalistCount.action")
	@ResponseBody
	public Map<String, Object> getDataListCount(HttpServletRequest req, String caller, String condition,boolean fromHeader) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		boolean isCloud = Constant.UAS_CLOUD.equals(req.getParameter("_config"));
		Boolean _self = (Boolean) req.getAttribute("_self");
		boolean _jobemployee = false;
		if ("true".equals(req.getParameter("_self")))
			_self = true;
		if ("true".equals(req.getParameter("_jobemployee")))
			_jobemployee = true;
		if (req.getAttribute("_jobemployee")!=null&&"true".equals(req.getAttribute("_jobemployee").toString())){
			_jobemployee = true;
		}
		modelMap.put("count", dataListService.getCountByCaller(caller, condition,_self,fromHeader, isCloud,_jobemployee));
		return modelMap;
	}

	/**
	 * @param req
	 * @param session
	 * @param caller
	 * @param condition
	 * @param page
	 * @param pageSize
	 * @param _f
	 *            快速查找模式
	 * @param _alia
	 *            别名模式（简化字段名，降低传输成本）
	 * @param orderby
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/common/datalist/data.action")
	@ResponseBody
	public Map<String, Object> getDataListData(HttpServletRequest req, HttpSession session, String caller, String condition, int page,
			int pageSize, Integer _f, Integer _alia, String orderby) throws UnsupportedEncodingException {
		Boolean _self = (Boolean) req.getAttribute("_self");
		boolean _jobemployee = false;
		if ("true".equals(req.getParameter("_self")))
			_self = true;
		boolean alia = _alia != null && Constant.YES == _alia;
		if (req.getAttribute("_jobemployee")!=null&&"true".equals(req.getAttribute("_jobemployee").toString())){
			_jobemployee = true;
		}
		boolean isCloud = Constant.UAS_CLOUD.equals(req.getParameter("_config"));
		Map<String, Object> modelMap = dataListService
				.getDataListData(caller, condition, page, pageSize, orderby, _self, alia, _f, isCloud,_jobemployee);
		modelMap.put("sessionId", req.getSession().getId());
		modelMap.put("_self", _self);
		return modelMap;
	}

	/**
	 * 整批抛转
	 */
	@RequestMapping(value = "/common/vastPost.action")
	@ResponseBody
	public Map<String, Object> vastPost(HttpSession session, String caller, int[] id) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dataListService.vastPost(language, employee, caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批结案
	 */
	@RequestMapping(value = "/common/vastClose.action")
	@ResponseBody
	public Map<String, Object> vastClose(HttpSession session, String caller, int[] id) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dataListService.vastClose(language, employee, caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批重启
	 */
	@RequestMapping(value = "/common/vastResStart.action")
	@ResponseBody
	public Map<String, Object> vastResStart(HttpSession session, String caller, int[] id) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dataListService.vastResStart(language, employee, caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * vastDatalist.jsp 整批保存 editDatlist.jsp
	 */
	@RequestMapping(value = "/common/vastSave.action")
	@ResponseBody
	public Map<String, Object> vastSave(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dataListService.vastSave(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批冻结
	 */
	@RequestMapping(value = "/common/vastFreeze.action")
	@ResponseBody
	public Map<String, Object> vastFreeze(HttpSession session, String caller, int[] id) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dataListService.vastFreeze(language, employee, caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批发出
	 */
	@RequestMapping(value = "/common/vastSend.action")
	@ResponseBody
	public Map<String, Object> vastSend(HttpSession session, String caller, int[] id) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dataListService.vastSend(language, employee, caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批审核
	 */
	@RequestMapping(value = "/common/vastAudit.action")
	@ResponseBody
	public Map<String, Object> vastAudit(HttpSession session, String caller, int[] id) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dataListService.vastAudit(language, employee, caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批提交
	 */
	@RequestMapping(value = "/common/vastSubmit.action")
	@ResponseBody
	public Map<String, Object> vastSubmit(HttpSession session, String caller, int[] id) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dataListService.vastSubmit(language, employee, caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批删除
	 */
	@RequestMapping(value = "/common/vastDelete.action")
	@ResponseBody
	public Map<String, Object> vastDelete(HttpSession session, String caller, int[] id) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dataListService.vastDelete(language, employee, caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批作废
	 */
	@RequestMapping(value = "/common/vastCancel.action")
	@ResponseBody
	public Map<String, Object> vastCancel(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dataListService.vastCancel(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批反过账
	 */
	@RequestMapping(value = "/common/vastResPost.action")
	@ResponseBody
	public Map<String, Object> vastResPost(HttpSession session, String caller, int[] id) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dataListService.vastResPost(language, employee, caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 采购询价单最终判定进价格库
	 */
	@RequestMapping(value = "/common/vastAgreeTurnPrice.action")
	@ResponseBody
	public Map<String, Object> AgreeToPrice(HttpSession session, String caller, int[] id) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dataListService.AgreeToPrice(language, employee, caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 采购询价单最终判定不进价格库
	 */
	@RequestMapping(value = "/common/vastNotAgreeTurnPrice.action")
	@ResponseBody
	public Map<String, Object> NotAgreeToPrice(HttpSession session, String caller, int[] id) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dataListService.NotAgreeToPrice(language, employee, caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 采购询价单最终判定全部进价格库
	 */
	@RequestMapping(value = "/common/vastAgreeAllTurnPrice.action")
	@ResponseBody
	public Map<String, Object> AgreeAllToPrice(HttpSession session, String caller, int id) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dataListService.AgreeAllToPrice(language, employee, caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 新增列表模板
	 */
	@RequestMapping(value = "/common/template/save.action")
	@ResponseBody
	public Map<String, Object> saveTemplate(HttpSession session, String caller, String fields, String desc) {
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dataListService.saveTemplate(caller, desc, fields, employee);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 设置个性化列表设置
	 * */
	@RequestMapping(value = "/common/saveEmpsDataListDetails.action")
	@ResponseBody
	public Map<String, Object> saveEmpsDataListDetails(HttpSession session, String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		dataListService.saveEmpsDataListDetails(caller, data, employee);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 重置个性化列表设置
	 * */
	@RequestMapping(value = "/common/resetEmpsDataListDetails.action")
	@ResponseBody
	public Map<String, Object> resetEmpsDataListDetails(HttpSession session, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dataListService.resetEmpsDataListDetails(caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * editDatalist.jsp 整批确认设置每个模块的当前期间
	 */
	@RequestMapping(value = "/common/vastConfirmPeriods.action")
	@ResponseBody
	public Map<String, Object> vastConfirmPeriods(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dataListService.vastConfirmPeriods(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * editDatalist.jsp 整批设置每个模块的初始化期间
	 */
	@RequestMapping(value = "/common/vastConfirmFirstPeriods.action")
	@ResponseBody
	public Map<String, Object> vastConfirmFirstPeriods(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dataListService.vastConfirmFirstPeriods(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 问题反馈编号：2016120061 获取所有的下拉框
	 */
	@RequestMapping(value = "/common/Datalist/getComboData.action")
	@ResponseBody
	public Map<String, Object> getComboDatalist(HttpServletRequest req, HttpSession session, String condition, int page, int start,
			int limit, String sort) throws UnsupportedEncodingException {
		condition = new String(condition.getBytes("iso8859-1"), "utf-8");
		Map<String, Object> modelMap = dataListService.getComboDatalist(condition, page, start, limit, sort);
		modelMap.put("sessionId", req.getSession().getId());
		return modelMap;
	}
	
	@RequestMapping(value = "/common/Datalist/getColumns.action")
	@ResponseBody
	public Map<String, Object> getColumns(String caller, boolean isCloud ) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		GridPanel gridPanel=dataListService.getColumns(caller,isCloud);
		modelMap.put("columns", gridPanel.getGridColumns());
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping(value = "/common/Datalist/getDataListFilterName.action")
	@ResponseBody
	public Map<String, Object> getDataListFilterName(String caller) {
		Map<String, Object> modeMap=new HashMap<String, Object>();
		List<Map<String, Object>> List =dataListService.getDataListFilterName(caller);
		modeMap.put("data", List);
		modeMap.put("success",true);
		return modeMap;
	}
	@RequestMapping(value = "/common/Datalist/getTreeNodeData.action")
	@ResponseBody
	public Map<String, Object> getTreeNodeData(String id) {
		Map<String, Object> modeMap=new HashMap<String, Object>();
		Map<String, Object> Map=new HashMap<String, Object>();
		List<Map<String, Object>> List =dataListService.getTreeNodeData(id);
		if(List.get(0).get("FILTERJSON_")!=null&&!"".equals(List.get(0).get("FILTERJSON_"))){
			Map.put("FILTERJSON_", JSONUtil.toMapList(List.get(0).get("FILTERJSON_").toString()));
		}else {
			Map.put("FILTERJSON_", "");
		}
		Map.put("SQLNAME_", List.get(0).get("SQLNAME_"));
		Map.put("SQL_", List.get(0).get("SQL_"));
		modeMap.put("data", Map);
		Employee employee = SystemSession.getUser();
		modeMap.put("isadmin",employee.getEm_type());
		modeMap.put("success",true);
		return modeMap;
	}
	@RequestMapping(value = "/common/Datalist/deleteTreeNode.action")
	@ResponseBody
	public Map<String, Object> deleteTreeNode(String id) {
		Map<String, Object> modeMap=new HashMap<String, Object>();
		modeMap.put("success",dataListService.deleteTreeNode(id));
		return modeMap;
	}
	
	@RequestMapping(value = "/common/Datalist/isAdmin.action")
	@ResponseBody
	public Map<String, Object> isAdmin() {
		Map<String, Object> modeMap=new HashMap<String, Object>();
		modeMap.put("success",true);
		modeMap.put("isAdmin", "admin".equals(SystemSession.getUser().getEm_type()));
		return modeMap;
	}
	
	@RequestMapping(value = "/common/Datalist/querySave.action")
	@ResponseBody
	public Map<String, Object> saveQuery(int id,String dataArr, boolean isDefalut, String caller) {
		Map<String, Object> modeMap=new HashMap<String, Object>();
		dataListService.saveQuery(id, dataArr, isDefalut, caller);
		modeMap.put("success",true);
		return modeMap;
	}
	
	@RequestMapping(value = "/common/Datalist/querySaveAnother.action")
	@ResponseBody
	public Map<String, Object> saveAnotherQuery(String queryName, boolean isDefault, boolean isNormal, String dataArr, String caller) {
		Map<String, Object> modeMap=new HashMap<String, Object>();
		dataListService.saveAnotherQuery(queryName, isDefault, isNormal, dataArr, caller);
		modeMap.put("success",true);
		return modeMap;
	}
	
	@RequestMapping(value = "/common/Datalist/setDefault.action")
	@ResponseBody
	public Map<String, Object> setDefault(String id,String caller) {
		Map<String, Object> modeMap=new HashMap<String, Object>();	
		dataListService.setDefault(id,caller);
		modeMap.put("success",true);
		return modeMap;
	}
	
	@RequestMapping(value = "/common/Datalist/hasFilterCondition.action")
	@ResponseBody
	public Map<String, Object> hasFilterCondition(String caller) {
		Map<String, Object> modeMap=new HashMap<String, Object>();	
		modeMap.put("success",dataListService.hasFilterCondition(caller));
		return modeMap;
	}
}
