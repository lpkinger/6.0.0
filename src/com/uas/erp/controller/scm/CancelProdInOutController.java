package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.model.FormItems;
import com.uas.erp.model.FormPanel;
import com.uas.erp.model.GridPanel;
import com.uas.erp.service.scm.CancelProdInOutService;
import com.uas.erp.service.ma.ConfigService;

import net.sf.json.JSONObject;
@Controller
public class CancelProdInOutController {
	@Autowired
	private CancelProdInOutService cancelProdInOutService;

	@Autowired
	private ConfigService configService;
	
	@Autowired
	private BaseDao baseDao;
	@RequestMapping(value = "/scm/getCancelFormItems.action")
	@ResponseBody
	public Map<String, Object> getFormItems(HttpSession session,HttpServletRequest req, String caller, String condition, String master,String _copyConf) {
		String language = SystemSession.getLang();
		language = language == null ? "zh_CN" : language;
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = SystemSession.getUser();
		// 传回的账套不为空;
		if (master != null) {
			SpObserver.putSp(master);
		}
		boolean isCloud=Constant.UAS_CLOUD.equals(req.getParameter("_config"));
		FormPanel panel = cancelProdInOutService.getFormItemsByCaller(caller, condition, language, employee,isCloud);
		List<FormItems> items = panel.getItems();
		if (condition.equals("")) {
			// 表示是单表录入界面
			for (FormItems item : items) {
				Object value = decodeDefaultValue(session, item.getValue(), language);
				item.setValue(value);
			}
			if(_copyConf!=null &&!"".equals(_copyConf)){
				Map<String, Object> data =getCopydata(session,caller, _copyConf,panel,language,isCloud);
				if (data != null) {
					modelMap.put("data", BaseUtil.parseMap2Str(data));
				}
			}
		} else {// 表示是单表显示界面
			Map<String, Object> data = cancelProdInOutService.getFormData(caller, condition,isCloud);
			if (data != null) {
				modelMap.put("data", BaseUtil.parseMap2Str(data));
			} else {
				for (FormItems item : items) {
					Object value = decodeDefaultValue(session, item.getValue(), language);
					item.setValue(value);
				}
			}
		}
		modelMap.put("items", items);//去掉button
		modelMap.put("keyField", panel.getFo_keyField());
		modelMap.put("buttons", "erpPrintPDFButton");
		modelMap.put("codeField", panel.getCodeField());
		modelMap.put("tablename", panel.getTablename());
		modelMap.put("statusField", panel.getStatusField());
		modelMap.put("statuscodeField", panel.getStatuscodeField());
		modelMap.put("fo_id", panel.getFo_id());
		modelMap.put("fo_detailMainKeyField", panel.getFo_detailMainKeyField());
		modelMap.put("fo_detailGridOrderBy", panel.getFo_detailGridOrderBy());
		modelMap.put("dealUrl", panel.getDealUrl());
		modelMap.put("title", panel.getTitle());
		modelMap.put("limits", panel.getLimitFields());
		modelMap.put("detailkeyfield", panel.getFo_detailkeyfield());
		modelMap.put("mainpercent", panel.getFo_mainpercent());
		modelMap.put("detailpercent", panel.getFo_detailpercent());
		// 必填项label特殊颜色
		JSONObject config = configService.getConfigByCallerAndCode("sys", "necessaryFieldColor");
		if (config != null && config.get("data") != null)
			modelMap.put("necessaryFieldColor", config.get("data"));
		return modelMap;
	}
	
	/**
	 * 将数据库里面的defaultvalue转化成实际要显示的值
	 * 
	 * @param value
	 *            formDetail.getFd_defaultvalue()
	 */
	public Object decodeDefaultValue(HttpSession session, Object value, String language) {
		if (value != null && !value.equals("null")) {
			String val = value.toString();
			if (val.contains("getCurrentDate()")) {
				return DateUtil.parseDateToString(null, Constant.YMD);
			} else if (val.contains("getCurrentTime()")) {
				return DateUtil.parseDateToString(null, Constant.YMD_HMS);
			} else if (val.contains("session:")) {
				Object obj = session.getAttribute(val.trim().split(":")[1]);
				return (obj == null) ? "" : obj;
			} else if (val.contains("getLocal(")) {
				Object obj = BaseUtil.getLocalMessage(val.substring(val.indexOf("(") + 1, val.lastIndexOf(")")), language);
				return (obj == null) ? "" : obj;
			}
			return value;
		} else {
			return "";
		}
	}
	
	private Map<String, Object> getCopydata(HttpSession session,String caller,String _copyConf,FormPanel panel,String language,boolean isCloud){
		Map<Object, Object> copyConf = BaseUtil.parseFormStoreToMap(_copyConf);
		Map<String, Object> data=null;
		if(!"".equals(panel.getFo_keyField())){
			data =cancelProdInOutService.getFormData(caller, panel.getFo_keyField()+"="+copyConf.get("keyValue"),isCloud);
			data.put(panel.getFo_keyField(),"");//清空主键
			if(!"".equals(panel.getCodeField())){
				data.put(panel.getCodeField(),"");//清空编号
			}
			SqlRowList rs = baseDao.queryForRowSet("select cc_field,cc_copyvalue from　COPYCONFIGS where cc_findkind='main' and cc_caller=?", caller);
			while (rs.next()) {
				Object value = decodeDefaultValue(session, rs.getString("cc_copyvalue"), language);
				data.put(rs.getString("cc_field"),value);
			}
		}
		return data;
	}
	
	@RequestMapping(value = "/scm/getCancelGridItems.action")
	@ResponseBody
	public Map<String, Object> getGridFields(HttpServletRequest req,String caller, String condition, Integer start,
			Integer end, String master, Integer _m,String _config,String _copyConf) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (master != null && !master.equals(""))
		SpObserver.putSp(master);
		boolean isCloud=Constant.UAS_CLOUD.equals(req.getParameter("_config"));
		Employee employee = SystemSession.getUser();
		GridPanel gridPanel = cancelProdInOutService.getGridPanelByCaller(caller, condition, start,
				end, _m,isCloud, _copyConf , employee);
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
}
