package com.uas.erp.service.common;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.uas.erp.model.Employee;
import com.uas.erp.model.Form;
import com.uas.erp.model.FormPanel;
import com.uas.erp.model.MessageLog;
import com.uas.erp.model.RelativeSearch;
import com.uas.erp.model.RelativeSearchLimit;

public interface SingleFormItemsService {
	FormPanel getFormItemsByCaller(String caller, String condition, String language, Employee employee, boolean isCloud);

	/**
	 * 只读form，无需formdetail的大部分配置，无需column布局显示
	 * 
	 * @param caller
	 * @param condition
	 * @param url
	 *            界面的url
	 * @param language
	 * @param employee
	 * @return
	 */
	JSONObject getReadOnlyForm(String caller, String condition, String url, String language, Employee employee);

	Form getForm(String caller);

	String getFormDataByCaller(String caller, String condition);

	Map<String, Object> getFormData(String caller, String condition, boolean isCloud);

	int getId(String seq);

	Object getFieldData(String caller, String field, String condition);

	String getFieldDatas(String caller, String field, String condition);

	JSONObject getFieldsData(String caller, String fields, String condition);

	List<?> getFieldsDatas(String caller, String fields, String condition);

	boolean checkFieldValue(String caller, String condition);

	void updateByCondition(String table, String update, String condition, Employee employee, String type, String caller);

	void updateAttachField(String table, String update, String condition, Employee employee, String type, String caller);

	String getCodeString(String caller, String table, int type);

	String getPayDate(String paymentmethodid, String startdateString);

	void vastDelete(String language, Employee employee, String caller, String data);

	void vastSubmit(String language, Employee employee, String caller, String data);

	void vastAudit(String language, Employee employee, String caller, String data);

	void vastResAudit(String language, Employee employee, String caller, String data);

	void vastSend(String language, Employee employee, String caller, String data);

	void vastFreeze(String language, Employee employee, String caller, String data);

	void vastFreezeDetail(String language, Employee employee, String caller, String data);

	void vastResStart(String language, Employee employee, String caller, String data);

	void vastResFinish(String language, Employee employee, String caller, String data);

	void vastResStartDetail(String language, Employee employee, String caller, String data);

	void vastResCloseDetail(String language, Employee employee, String caller, String data);

	void vastClose(String language, Employee employee, String caller, String data);

	void vastCloseDetail(String language, Employee employee, String caller, String data);

	void vastCloseSaleDetail(String language, Employee employee, String caller, String data);// 销售单加结案原因

	void vastClosePurchaseDetail(String language, Employee employee, String caller, String data);// 采购加结案原因

	void vastFreezePurchaseDetail(String language, Employee employee, String caller, String data);// 采购单加冻结原因
	
	void vastResFreezePurchaseDetail(String language, Employee employee, String caller, String data);// 采购单加冻结原因

	void vastFreezeSaleDetail(String language, Employee employee, String caller, String data);// 销售单加冻结原因

	void vastCloseSaleForecastDetail(String language, Employee employee, String caller, String data);// 销售预测单加结案原因

	void vastCloseSendNotifyDetail(String language, Employee employee, String caller, String data);// 出货通知单加结案原因

	String vastPost(String language, Employee employee, String caller, String to, String data);
	
	String specialPost(String language, Employee employee, String caller, String to, String data);

	void vastCancel(String language, Employee employee, String caller, String data);

	void vastCancelDetail(String language, Employee employee, String caller, String data);

	List<MessageLog> getMessageLogs(String caller, Object id);

	List<MessageLog> getMyMessageLogs(String caller, int id, Employee employee);

	String beforeQuery(String caller, String condition, Employee employee);
	
	void afterQuery(String caller, String condition, Employee employee);

	/**
	 * 重置已转数
	 * 
	 * @param tab
	 */
	void resetQty(String tab);

	/**
	 * 刷新同步状态
	 * 
	 */
	String refreshSync(Employee employee, String caller, String to, String data);

	/**
	 * Form--关联查询
	 * 
	 * @param caller
	 * @return
	 */
	List<Map<String, Object>> getRelativeSearchs(String caller);

	int getSearchDataCount(String tabName, String condition);

	/**
	 * 关联查询取数据
	 * 
	 * @param id
	 * @param tabName
	 * @param condition
	 * @param fields
	 * @param start
	 * @param end
	 * @return
	 */
	JSONArray getSearchData(Integer id, String tabName, String condition, String fields, int start, int end);

	/**
	 * 关联查询配置 <br>
	 * （用于分配权限）
	 * 
	 * @param caller
	 * @return
	 */
	List<RelativeSearch> getRelativeSearchForPower(String caller);

	List<RelativeSearchLimit> getRelativeSearchLimitsByEmpl(String caller, Integer em_id);

	List<RelativeSearchLimit> getRelativeSearchLimitsByJob(String caller, Integer jo_id);

	void saveRelativeSearchLimit(String limits, int id, Boolean _self);

	/**
	 * 关联查询的合计
	 * 
	 * @param id
	 * @param condition
	 * @return
	 */
	JSONObject getSearchSummary(Integer id, String condition);

	String getFormHelpDoc(String caller);

	String getDemoUrl(String caller, String master, String webpath);

	String getDemoWebSite(String caller);

	String getPageCallerByFlow(String caller, String url);
	
	List<Map<String, Object>> getMessageInfo(String caller,String id);

	Map<String, Object> getLogicMessageLogs(String caller, String context, int page, int limit);
	
	void vastBanned(String language, Employee employee, String caller, String data);
	
	void vastResBanned(String language, Employee employee, String caller, String data);

	void vastCloseApplicationDetail(String language, Employee employee,
			String caller, String data);
	
	void BusinessTripOpen(String emcodes);

}
