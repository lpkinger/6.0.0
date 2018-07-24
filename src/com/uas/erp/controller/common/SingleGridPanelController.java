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

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.model.FormPanel;
import com.uas.erp.model.GridPanel;
import com.uas.erp.service.common.SingleGridPanelService;
import com.uas.erp.service.ma.ConfigService;

/**
 * 处理单表录入界面grid数据加载请求 根据caller拿到grid的fields以及coummns等
 * 
 * @author yingp
 * @date 2012-07-31 15:27:03
 */
@Controller
public class SingleGridPanelController {
	@Autowired
	private SingleGridPanelService singleGridPanelService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private BaseDao baseDao;

	@RequestMapping(value = "/common/singleGridPanel.action")
	@ResponseBody
	public Map<String, Object> getGridFields(HttpServletRequest req,String caller, String condition, Integer start,
			Integer end, String master, Integer _m,String _config,String _copyConf) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (master != null && !master.equals(""))
		SpObserver.putSp(master);
		boolean isCloud=Constant.UAS_CLOUD.equals(req.getParameter("_config"));
		GridPanel gridPanel = singleGridPanelService.getGridPanelByCaller(caller, condition, start,
				end, _m,isCloud, _copyConf);
		modelMap.put("fields", gridPanel.getGridFields());
		modelMap.put("columns", gridPanel.getGridColumns());
		modelMap.put("dbfinds", gridPanel.getDbfinds());
		modelMap.put("limits", gridPanel.getLimits());
		if (condition.equals("")) {// 表示是单表录入界面
			// 为grid空白行设置一些默认值
			if(_copyConf!=null &&!"".equals(_copyConf)){//将来源单据数据按配置替换后返回
				modelMap.put("data", gridPanel.getDataString());
			}
		} else {// 表示是单表显示界面
			modelMap.put("data", gridPanel.getDataString());
		}
		// 必填项label特殊颜色
		JSONObject config = configService.getConfigByCallerAndCode("sys", "necessaryFieldColor");
		if (config != null && config.get("data") != null)
			modelMap.put("necessaryFieldColor", config.get("data"));
		return modelMap;
	}
	
	@RequestMapping(value = "/common/rsGrid.action")
	@ResponseBody
	public Map<String, Object> getReadOnlyGrid(HttpSession session, String caller, String condition, Integer start, Integer end,
			String master, String url) {
		String language = (String) session.getAttribute("language");
		language = language == null ? "zh_CN" : language;
		Employee employee = (Employee) session.getAttribute("employee");
		if (master != null && !master.equals(""))
			SpObserver.putSp(master);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", singleGridPanelService.getReadOnlyGrid(caller, condition, url, language, employee, start, end));
		return modelMap;
	}

	@RequestMapping(value = "/common/loadNewGridStore.action")
	@ResponseBody
	public Map<String, Object> getNewGridStore(HttpServletRequest req,HttpSession session, String caller, String condition, Integer start,
			Integer end,String _config) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		boolean isCloud=Constant.UAS_CLOUD.equals(req.getParameter("_config"));
		if (condition != null && !"".equals(condition)) {
			Employee employee = (Employee) session.getAttribute("employee");
			modelMap.put("data", singleGridPanelService.getRecordByCode(caller, condition, employee, start, end,isCloud));
		}
		return modelMap;
	}

	/**
	 * 例如：当用户在某一行输入完物料编号，并移开光标后，自动根据编号调出整条物料数据
	 */
	@RequestMapping("/common/getRecordByCode.action")
	@ResponseBody
	public Map<String, Object> getRecordByCode(HttpSession session, String caller, String condition, Integer start,
			Integer end) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("data", singleGridPanelService.getRecordByCode(caller, condition, employee, start, end,false));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除grid某一条
	 */
	@RequestMapping("/common/deleteDetail.action")
	@ResponseBody
	public Map<String, Object> deleteDetail(HttpSession session, String caller,String gridcaller, String condition, String autodelete,String gridReadOnly) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleGridPanelService.deleteDetail(caller,gridcaller, condition, autodelete,gridReadOnly);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除明细时，修改明细行排序
	 */
	@RequestMapping("/common/setDetailDetno.action")
	@ResponseBody
	public Map<String, Object> setDetailDetno(HttpSession session, String caller, String dfield, String mfield,
			Integer id, int detno) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleGridPanelService.setDetailDetno(caller, dfield, mfield, id, detno);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * Grid Button
	 */
	@RequestMapping("/common/gridButton.action")
	@ResponseBody
	public Map<String, Object> getGridButton(HttpSession session, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("buttons", String.valueOf(singleGridPanelService.getGridButton(caller)));
		return modelMap;
	}

	/**
	 * GridPage.jsp 批量保存、修改
	 */
	@RequestMapping(value = "/common/batchSave.action")
	@ResponseBody
	public Map<String, Object> vastSave(HttpSession session, String caller, String gridStore) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleGridPanelService.batchSave(language, employee, caller, gridStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * batchDeal.jsp 批量保存、修改
	 */
	@RequestMapping(value = "/common/batchUpdate.action")
	@ResponseBody
	public Map<String, Object> batchSave(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleGridPanelService.batchSave(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * save itemgrid
	 */
	@RequestMapping(value = "/common/saveItemGrid.action")
	@ResponseBody
	public Map<String, Object> saveItemGrid(HttpSession session, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleGridPanelService.saveItemGrid(language, employee, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * update itemgrid
	 */
	@RequestMapping(value = "/common/updateItemGrid.action")
	@ResponseBody
	public Map<String, Object> updateItemGrid(HttpSession session, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleGridPanelService.updateItemGrid(language, employee, data);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping(value = "/common/batchdeal/data.action")
	@ResponseBody
	public Map<String, Object> getGridDatas(HttpServletRequest req,String caller, String condition, Integer page,
			Integer pageSize, String master, Integer _m,String _config,String _copyConf,String orderby) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (master != null && !master.equals(""))
		SpObserver.putSp(master);
		boolean isCloud=Constant.UAS_CLOUD.equals(req.getParameter("_config"));
		GridPanel gridPanel = singleGridPanelService.getGridDatas(caller, condition, page,
				pageSize, _m,isCloud, _copyConf,orderby);
		modelMap.put("fields", gridPanel.getGridFields());
		modelMap.put("columns", gridPanel.getGridColumns());
		modelMap.put("dbfinds", gridPanel.getDbfinds());
		modelMap.put("limits", gridPanel.getLimits());
		modelMap.put("count", gridPanel.getDataCount());
		modelMap.put("data", gridPanel.getDataString());
		// 必填项label特殊颜色
		JSONObject config = configService.getConfigByCallerAndCode("sys", "necessaryFieldColor");
		if (config != null && config.get("data") != null)
			modelMap.put("necessaryFieldColor", config.get("data"));
		return modelMap;
	}
}
