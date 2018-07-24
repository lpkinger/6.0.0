package com.uas.erp.controller.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import sun.swing.StringUIClientPropertyKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.ExcelUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.PowerDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.FormItems;
import com.uas.erp.model.FormPanel;
import com.uas.erp.service.common.SingleFormItemsService;
import com.uas.erp.service.ma.ConfigService;

/**
 * 处理单表录入和查看界面form的数据加载
 * 
 * @author yingp
 * @date 2012-07-31 15:32:57
 */
@SuppressWarnings("deprecation")
@Controller
public class SingleFormItemsController {
	@Autowired
	private PowerDao powerDao;

	@Autowired
	private SingleFormItemsService singleFormItemsService;

	@Autowired
	private ConfigService configService;
	
	@Autowired
	private BaseDao baseDao;

	@RequestMapping(value = "/common/singleFormItems.action")
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
		FormPanel panel = singleFormItemsService.getFormItemsByCaller(caller, condition, language, employee,isCloud);
		List<FormItems> items = panel.getItems();
		if (condition.equals("")) {
			//基础资料维护设置账套权限
			for(String powerCallers:powerDao.getUnEditableCallers(SpObserver.getSp())){		
				for(String powerCaller:powerCallers.split(",")){						
					boolean bool = caller.equals(powerCaller);
					if(bool){
						BaseUtil.showError("ERR_POWER_026:当前账套没有<新增>该单据的权限!");
					}
				}
			}
			// 表示是单表录入界面
			List <FormItems> MTItems=new ArrayList<FormItems>();
			for (FormItems item : items) {
				Object value = decodeDefaultValue(session, item.getValue(), language);
				item.setValue(value);
				//MT类型 剔除的字段
				if("multifield".equals(item.getXtype())&&!StringUtil.hasText(item.getLogic())){
					MTItems.add(item);
				}
			}
			if(MTItems.size()>0){
				Iterator<FormItems> it=items.iterator();
				while(it.hasNext()){
					FormItems item=it.next();
					//修改multi类型配置的默认值 支持字段分开设置
					if("multifield".equals(item.getXtype())){
						for (FormItems MT : MTItems) {
							//移除MT logic为空的
							if(item.getName().equals(MT.getName())){
								it.remove();
							} 
							//将第二个有默认值拼接到mt字段value
							if(String.valueOf(MT.getName()).equals(item.getLogic()) && StringUtil.hasText(MT.getValue())){
								item.setValue(item.getValue()+";"+MT.getValue());
								break;
							}
						}	
					}
				}
			}
			
			if(_copyConf!=null &&!"".equals(_copyConf)){
				Map<String, Object> data =getCopydata(session,caller, _copyConf,panel,language,isCloud);
				if (data != null) {
					modelMap.put("data", BaseUtil.parseMap2Str(data));
				}
			}
		} else {// 表示是单表显示界面
			Map<String, Object> data = singleFormItemsService.getFormData(caller, condition,isCloud);
			Iterator<FormItems> it=items.iterator();
			while(it.hasNext()){
				FormItems item=it.next();
				//修改multi类型配置的默认值 支持字段分开设置
				if("multifield".equals(item.getXtype()) && !StringUtil.hasText(item.getLogic())){	
					it.remove();
				}
			}
			if (data != null) {
				modelMap.put("data", BaseUtil.parseMap2Str(data));
			} else {
				for (FormItems item : items) {
					Object value = decodeDefaultValue(session, item.getValue(), language);
					item.setValue(value);
				}
			}
		}
		modelMap.put("items",items);
		modelMap.put("buttons", panel.getButtons());
		modelMap.put("keyField", panel.getFo_keyField());
		modelMap.put("codeField", panel.getCodeField());
		modelMap.put("tablename", panel.getTablename());
		modelMap.put("statusField", panel.getStatusField());
		modelMap.put("statuscodeField", panel.getStatuscodeField());
		modelMap.put("fo_id", panel.getFo_id());
		modelMap.put("fo_isPrevNext", panel.getFo_isPrevNext());
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
	 * form只读信息
	 * 
	 * @param session
	 * @param caller
	 * @param condition
	 * @param master
	 * @return
	 */
	@RequestMapping(value = "/common/rsForm.action")
	@ResponseBody
	public JSONObject getReadOnlyForm(HttpSession session, String caller, String condition, String master, String url) {
		String language = (String) session.getAttribute("language");
		language = language == null ? "zh_CN" : language;
		Employee employee = (Employee) session.getAttribute("employee");
		// 传回的账套不为空;
		if (master != null)
			SpObserver.putSp(master);
		return singleFormItemsService.getReadOnlyForm(caller, condition, url, language, employee);
	}

	/**
	 * 取form的store
	 * 
	 * @param session
	 * @param caller
	 * @param condition
	 * @return
	 */
	@RequestMapping(value = "/common/loadNewFormStore.action")
	@ResponseBody
	public Map<String, Object> getNewFormStore(HttpSession session, String caller, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (condition != null && !"".equals(condition)) {
			String data = singleFormItemsService.getFormDataByCaller(caller, condition);
			if (data != null) {
				modelMap.put("data", data);
			}
		}
		return modelMap;
	}

	/**
	 * 拿到id
	 * 
	 * @param seq
	 *            序列名
	 */
	@RequestMapping("/common/getId.action")
	@ResponseBody
	public Map<String, Object> getId(HttpServletRequest request,HttpSession session, String seq) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", singleFormItemsService.getId(seq));
		modelMap.put("sessionId", request.getSession().getId());
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 拿到编号
	 */
	@RequestMapping("/common/getCodeString.action")
	@ResponseBody
	public Map<String, Object> getCode(HttpServletRequest request,HttpSession session, String caller, String table, int type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("code", singleFormItemsService.getCodeString(caller, table, type));
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 
	 */
	@RequestMapping("/common/getPayDate.action")
	@ResponseBody
	public Map<String, Object> getPayDate(HttpSession session, String paymentmethodid, String startdateString) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("paydate", singleFormItemsService.getPayDate(paymentmethodid, startdateString));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新
	 */
	@RequestMapping("/common/updateByCondition.action")
	@ResponseBody
	public Map<String, Object> updateByCondition(HttpSession session, String table, String update, String condition) {
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.updateByCondition(table, update, condition, employee, null, null);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改附件
	 */
	@RequestMapping("/common/attach/change.action")
	@ResponseBody
	public Map<String, Object> changeAttachField(HttpSession session, String caller, String table, String update, String condition,
			String type) {
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.updateAttachField(table, update, condition, employee, type, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改附件update
	 */
	@RequestMapping("/common/attach/update.action")
	@ResponseBody
	public Map<String, Object> updateAttachField(HttpSession session, String caller, String table, String update, String condition,
			String type) {
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.updateAttachField(table, update, condition, employee, type, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 拿到关于field的数据
	 * 
	 * @param caller
	 *            tablename
	 * @param field
	 *            待取值的字段
	 * @param condition
	 *            带入的条件
	 */
	@RequestMapping("/common/getFieldData.action")
	@ResponseBody
	public Map<String, Object> getFieldData(HttpSession session, String field, String caller, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", singleFormItemsService.getFieldData(caller, field, condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 拿到关于field的数据 多条数据
	 * 
	 * @param caller
	 *            tablename
	 * @param field
	 *            待取值的字段
	 * @param condition
	 *            带入的条件
	 */
	@RequestMapping("/common/getFieldDatas.action")
	@ResponseBody
	public Map<String, Object> getFieldDatas(HttpSession session, String field, String caller, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", singleFormItemsService.getFieldDatas(caller, field, condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 拿到关于fields的数据
	 * 
	 * @param caller
	 *            tablename
	 * @param fields
	 *            待取值的字段
	 * @param condition
	 *            带入的条件
	 */
	@RequestMapping("/common/getFieldsData.action")
	@ResponseBody
	public Map<String, Object> getFieldsData(HttpSession session, String fields, String caller, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", singleFormItemsService.getFieldsData(caller, fields, condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 拿到关于fields的数据
	 * 
	 * @param caller
	 *            tablename
	 * @param fields
	 *            字段
	 * @param condition
	 *            带入的条件
	 */
	@RequestMapping("/common/getFieldsDatas.action")
	@ResponseBody
	public Map<String, Object> getFieldsDatas(HttpSession session, String fields, String caller, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", singleFormItemsService.getFieldsDatas(caller, fields, condition).toString());
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 
	 */
	@RequestMapping("/common/checkFieldData.action")
	@ResponseBody
	public Map<String, Object> checkFieldData(HttpSession session, String caller, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", singleFormItemsService.checkFieldValue(caller, condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 日志
	 */
	@RequestMapping("/common/getMessageLogs.action")
	@ResponseBody
	public Map<String, Object> getMessageLogs(HttpSession session, String caller, String id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("logs", singleFormItemsService.getMessageLogs(caller, id));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 日志
	 */
	@RequestMapping("/common/getMyMessageLogs.action")
	@ResponseBody
	public Map<String, Object> getMyMessageLogs(HttpSession session, String caller, int id) {
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("logs", singleFormItemsService.getMyMessageLogs(caller, id, employee));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 逻辑配置日志
	 */
	@RequestMapping("/common/getLogicMessageLogs.action")
	@ResponseBody
	public Map<String, Object> getLogicMessageLogs(HttpSession session, String caller,String context,int page,int limit) {
		return singleFormItemsService.getLogicMessageLogs(caller, context,page, limit) ;
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

	/**
	 * batchDeal.jsp整批作废
	 */
	@RequestMapping(value = "/common/form/vastCancel.action")
	@ResponseBody
	public Map<String, Object> vastCancel(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.vastCancel(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批抛转
	 */
	@RequestMapping(value = "/common/form/vastPost.action")
	@ResponseBody
	public Map<String, Object> vastPost(HttpSession session, String caller, String to, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", singleFormItemsService.vastPost(language, employee, caller, to, data));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 自定义同步
	 */
	@RequestMapping(value = "/common/form/specialPost.action")
	@ResponseBody
	public Map<String, Object> specialPost(HttpSession session, String caller, String to, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", singleFormItemsService.specialPost(language, employee, caller, to, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 刷新同步状态
	 */
	@RequestMapping(value = "/common/form/refreshsync.action")
	@ResponseBody
	public Map<String, Object> refreshSync(HttpSession session, String caller, String to, String data) {
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", singleFormItemsService.refreshSync(employee, caller, to, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批结案(针对整张单据)
	 */
	@RequestMapping(value = "/common/form/vastClose.action")
	@ResponseBody
	public Map<String, Object> vastClose(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.vastClose(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批结案(针对明细)
	 */
	@RequestMapping(value = "/common/form/vastCloseDetail.action")
	@ResponseBody
	public Map<String, Object> vastCloseDetail(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.vastCloseDetail(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批结案销售单(针对明细)
	 */
	@RequestMapping(value = "/common/form/vastCloseSaleDetail.action")
	@ResponseBody
	public Map<String, Object> vastCloseSaleDetail(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.vastCloseSaleDetail(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批结案采购单(针对明细)
	 */
	@RequestMapping(value = "/common/form/vastClosePurchaseDetail.action")
	@ResponseBody
	public Map<String, Object> vastClosePurchaseDetail(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.vastClosePurchaseDetail(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 整批结案请购单(针对明细)
	 */
	@RequestMapping(value = "/common/form/vastCloseApplicationDetail.action")
	@ResponseBody
	public Map<String, Object> vastCloseApplicationDetail(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.vastCloseApplicationDetail(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批冻结采购单(针对明细)
	 */
	@RequestMapping(value = "/common/form/vastFreezePurchaseDetail.action")
	@ResponseBody
	public Map<String, Object> vastFreezePurchaseDetail(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.vastFreezePurchaseDetail(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 采购单明细取消冻结(针对明细)
	 */
	@RequestMapping(value = "/common/form/vastResFreezePurchaseDetail.action")
	@ResponseBody
	public Map<String, Object> vastResFreezePurchaseDetail(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.vastResFreezePurchaseDetail(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批冻结销售单(针对明细)
	 */
	@RequestMapping(value = "/common/form/vastFreezeSaleDetail.action")
	@ResponseBody
	public Map<String, Object> vastFreezeSaleDetail(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.vastFreezeSaleDetail(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批结案销售预测单(针对明细)
	 */
	@RequestMapping(value = "/common/form/vastCloseSaleForecastDetail.action")
	@ResponseBody
	public Map<String, Object> vastCloseSaleForecastDetail(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.vastCloseSaleForecastDetail(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批结案出货通知单(针对明细)
	 */
	@RequestMapping(value = "/common/form/vastCloseSendNotifyDetail.action")
	@ResponseBody
	public Map<String, Object> vastCloseSendNotifyDetail(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.vastCloseSendNotifyDetail(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量作废
	 */
	@RequestMapping(value = "/common/form/vastCancelDetail.action")
	@ResponseBody
	public Map<String, Object> vastCancleDetail(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.vastCancelDetail(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批重启
	 */
	@RequestMapping(value = "/common/form/vastResStart.action")
	@ResponseBody
	public Map<String, Object> vastResStart(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.vastResStart(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批反结案(单张)
	 */
	@RequestMapping(value = "/common/form/vastResFinish.action")
	@ResponseBody
	public Map<String, Object> vastResFinish(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.vastResFinish(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批重启明细
	 */
	@RequestMapping(value = "/common/form/vastResStartDetail.action")
	@ResponseBody
	public Map<String, Object> vastResStartDetail(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.vastResStartDetail(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批反结案明细
	 */
	@RequestMapping(value = "/common/form/vastResCloseDetail.action")
	@ResponseBody
	public Map<String, Object> vastResCloseDetail(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.vastResCloseDetail(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批冻结
	 */
	@RequestMapping(value = "/common/form/vastFreeze.action")
	@ResponseBody
	public Map<String, Object> vastFreeze(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.vastFreeze(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批冻结(针对明细)
	 */
	@RequestMapping(value = "/common/form/vastFreezeDetail.action")
	@ResponseBody
	public Map<String, Object> vastFreezeDetail(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.vastFreezeDetail(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批发出
	 */
	@RequestMapping(value = "/common/form/vastSend.action")
	@ResponseBody
	public Map<String, Object> vastSend(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.vastSend(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批审核
	 */
	@RequestMapping(value = "/common/form/vastAudit.action")
	@ResponseBody
	public Map<String, Object> vastAudit(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.vastAudit(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批反审核
	 */
	@RequestMapping(value = "/common/form/vastResAudit.action")
	@ResponseBody
	public Map<String, Object> vastResAudit(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.vastResAudit(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批提交
	 */
	@RequestMapping(value = "/common/form/vastSubmit.action")
	@ResponseBody
	public Map<String, Object> vastSubmit(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.vastSubmit(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批删除
	 */
	@RequestMapping(value = "/common/form/vastDelete.action")
	@ResponseBody
	public Map<String, Object> vastDelete(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.vastDelete(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 查询之前的逻辑(批量处理界面、查询界面) 针对有些匹配到employee设置的权限
	 */
	@RequestMapping(value = "/common/form/beforeQuery.action")
	@ResponseBody
	public Map<String, Object> beforeQuery(HttpSession session, String caller, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		String str = singleFormItemsService.beforeQuery(caller, condition, employee);
		modelMap.put("data", str);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 查询之后的逻辑(批量处理界面、查询界面) 针对有些匹配到employee设置的权限
	 */
	@RequestMapping(value = "/common/form/afterQuery.action")
	@ResponseBody
	public Map<String, Object> afterQuery(HttpSession session, String caller, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		singleFormItemsService.afterQuery(caller, condition, employee);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 重置已转数
	 */
	@RequestMapping(value = "/common/resetqty.action")
	@ResponseBody
	public Map<String, Object> resetQty(String tab) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.resetQty(tab);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * Form--关联查询
	 */
	@RequestMapping(value = "/common/form/relativeSearch.action")
	@ResponseBody
	public Map<String, Object> relativeSearch(HttpSession session, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", singleFormItemsService.getRelativeSearchs(caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * Form--关联查询--数据
	 */
	@RequestMapping(value = "/common/form/search.action")
	@ResponseBody
	public Map<String, Object> search(HttpSession session, Integer _id, String _tab, String _cond, String _fies, int _start, int _end) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int count = singleFormItemsService.getSearchDataCount(_tab, _cond);
		modelMap.put("count", count);
		if (count > 0) {
			modelMap.put("data", singleFormItemsService.getSearchData(_id, _tab, _cond, _fies, _start, _end));
			// 合计
			modelMap.put("summary", singleFormItemsService.getSearchSummary(_id, _cond));
		} else {
			modelMap.put("data", new JSONArray());
		}
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 导出关联查询excel导出
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/common/form/relativeSearch.xls")
	public ModelAndView createGridExcel(HttpSession session, HttpServletResponse response, HttpServletRequest request, String columns,
			String title, Integer _id, String _tab, String condition, String _fies) throws IOException {
		columns = new String(columns.getBytes("ISO-8859-1"), "UTF-8");
		if (condition != null)
			condition = new String(condition.getBytes("ISO-8859-1"), "UTF-8");
		Employee employee = (Employee) session.getAttribute("employee");
		return new ModelAndView(new ExcelUtil(BaseUtil.parseGridStoreToMaps(columns), singleFormItemsService.getSearchData(_id, _tab,
				condition, _fies, 0, ExcelUtil.maxSize), title, employee).getView());
	}

	/**
	 * Form --获取form关联的帮助文档
	 * */
	@RequestMapping(value = "/common/form/getHelpDoc.action")
	@ResponseBody
	public Map<String, Object> getFormHelpDoc(HttpSession session, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String path = singleFormItemsService.getFormHelpDoc(caller);
		modelMap.put("success", true);
		modelMap.put("path", path);
		return modelMap;
	}

	/**
	 * Form --获取demo数据模型
	 * */
	@RequestMapping(value = "/demo/skipDemo.action")
	public ModelAndView skipDemo(HttpServletResponse response, HttpServletRequest request, String caller, String master) throws IOException {
		if (master != null)
			SpObserver.putSp(master);
		String url = singleFormItemsService.getDemoUrl(caller, master, BaseUtil.getBasePath(request));
		ModelMap m = new ModelMap();
		m.put("url", url);
		return new ModelAndView("ma/demopage", m);
	}

	@RequestMapping(value = "/common/form/reqDemo.action")
	@ResponseBody
	public String getDemoUrl(HttpServletRequest request, String caller) {
		return singleFormItemsService.getDemoWebSite(caller);
	}

	@RequestMapping(value = "/common/form/getPageCaller.action")
	@ResponseBody
	public String getPageCaller(String caller, String url) {
		return singleFormItemsService.getPageCallerByFlow(caller, url);
	}
	/**
	 * 通用复制功能获取来源单据数据并按照配置进行替换
	 * @param session
	 * @param caller
	 * @param _copyConf 来源单据条件
	 * @param panel
	 * @param language
	 * @param isCloud
	 * @return
	 */
	private Map<String, Object> getCopydata(HttpSession session,String caller,String _copyConf,FormPanel panel,String language,boolean isCloud){
		Map<Object, Object> copyConf = BaseUtil.parseFormStoreToMap(_copyConf);
		Map<String, Object> data=null;
		if(!"".equals(panel.getFo_keyField())){
			data =singleFormItemsService.getFormData(caller, panel.getFo_keyField()+"="+copyConf.get("keyValue"),isCloud);
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

	@RequestMapping(value = "common/getMessageInfo.action")
	@ResponseBody
	public Map<Object, Object>getMessageInfo(String caller, String id) {
		
		Map<Object, Object> map=new HashMap<Object, Object>();
		map.put("logs", singleFormItemsService.getMessageInfo(caller, id));
		map.put("success", true);
 		return map;
	}
	
	/**
	 * 整批禁用(针对整张单据)
	 */
	@RequestMapping(value = "/common/form/vastBanned.action")
	@ResponseBody
	public Map<String, Object> vastBanned(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.vastBanned(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 整批反禁用(针对整张单据)
	 */
	@RequestMapping(value = "/common/form/vastResBanned.action")
	@ResponseBody
	public Map<String, Object> vastResBanned(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.vastResBanned(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 差旅人员开通
	 */
	@RequestMapping(value = "/common/form/BusinessTripOpen.action")
	@ResponseBody
	public Map<String, Object> BusinessTripOpen(HttpSession session, String emcodes) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		singleFormItemsService.BusinessTripOpen(emcodes);
		modelMap.put("success", true);
		return modelMap;
	}
}
