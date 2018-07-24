package com.uas.erp.controller.ma;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.common.ReportService;
import com.uas.erp.service.ma.MAFormService;

@Controller
public class MAFormController {

	@Autowired
	private MAFormService maFormService;
	@Autowired
	private ReportService reportService;

	/**
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/ma/saveForm.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		maFormService.save(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 */
	@RequestMapping("/ma/deleteForm.action")
	@ResponseBody
	public Map<String, Object> delete(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		maFormService.delete(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/ma/updateForm.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param, String param2, String param3) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		maFormService.update(formStore, param, param2, param3);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 */
	@RequestMapping("/ma/deleteMultiForm.action")
	@ResponseBody
	public Map<String, Object> mdelete(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		maFormService.mdelete(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 */
	@RequestMapping("/ma/updateMultiForm.action")
	@ResponseBody
	public Map<String, Object> mupdate(String formData, String formAdded, String formUpdated, String formDeleted, String gridAdded,
			String gridUpdated, String gridDeleted) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (formData != null)
			maFormService.update(formData, formAdded, formUpdated, formDeleted);
		if (gridAdded != null || gridUpdated != null || gridDeleted != null)
			maFormService.updateDetailGrid(gridAdded, gridUpdated, gridDeleted);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 
	 * */
	@RequestMapping("/ma/saveMultiForm.action")
	@ResponseBody
	public Map<String, Object> saveMultiForm(String formStore, String param, String param2) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		maFormService.saveMultiForm(formStore, param);
		maFormService.saveDetailGrid(param2);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 配置界面 获得combo
	 * */
	@RequestMapping(value = "/common/getComboDataByCallerAndField.action")
	@ResponseBody
	public Map<String, Object> getComboDataByField(String caller, String field) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", maFormService.getComboDataByField(caller, field));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 配置界面 保存combo
	 * */
	@RequestMapping(value = "/common/saveCombo.action")
	@ResponseBody
	public Map<String, Object> saveCombo(String gridStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		maFormService.saveCombo(gridStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 配置界面 删除 Combo
	 * */
	@RequestMapping(value = "/common/deleteCombo.action")
	@ResponseBody
	public Map<String, Object> deleteCombo(String id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		maFormService.deleteCombo(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 
	 */
	@RequestMapping("/ma/saveFormBook.action")
	@ResponseBody
	public Map<String, Object> saveFormBook(Integer foid, String text) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		maFormService.saveFormBook(foid, text);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 检测填写的字段在表中是否存在
	 */
	@RequestMapping("/ma/checkFields.action")
	@ResponseBody
	public boolean checkFields(String table, String field) {
		boolean success = maFormService.checkFields(table, field);
		return success;
	}

	/**
	 * 设置列表caller
	 */
	@RequestMapping("/ma/setListCaller.action")
	@ResponseBody
	public Map<String, Object> setListCaller(String caller, String dl_caller, String lockpage) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", maFormService.setListCaller(caller, dl_caller, lockpage));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 设置关联列表caller
	 * 
	 * @param caller
	 * @param re_caller
	 * @param lockpage
	 * @return
	 */

	@RequestMapping("/ma/setRelativeCaller.action")
	@ResponseBody
	public Map<String, Object> setRelativeCaller(String caller, String re_caller, String lockpage) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", maFormService.setRelativeCaller(caller, re_caller, lockpage));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新detailgrid
	 */
	@RequestMapping("/ma/updateDetail.action")
	@ResponseBody
	public Map<String, Object> updatedetail(String table, String gridAdded, String gridUpdated, String gridDeleted) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (gridAdded != null || gridUpdated != null || gridDeleted != null)
			maFormService.updateDetail(table, gridAdded, gridUpdated, gridDeleted);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 多次打印
	 */
	@RequestMapping(value = "/ma/printMT.action", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> printMT(HttpServletRequest request, int id, String caller, String reportName, String condition) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("info", reportService.printMT(id, caller, reportName, condition, request));
		map.put("success", true);
		return map;

	}
}
