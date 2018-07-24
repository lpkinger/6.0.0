package com.uas.erp.controller.excel;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.excel.ExcelCellService;
import com.uas.erp.service.excel.ExcelFileService;
import com.uas.erp.service.excel.ExcelFileTemplateService;
@Controller
public class ExcelCellController extends BaseController{
	@Autowired
	private ExcelCellService excelCellService;
	
	/**公共更新格子
	 * @param fileId
	 * @param actions
	 * @param isTpl
	 * @return
	 */
	@RequestMapping("/Excel/cell/updateBatchCells.action")
	@ResponseBody
	public Map<String, Object> updateBatchCells(
			int fileId,
			String actions,
			Boolean isTpl) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		excelCellService.updateBatchCells(actions, isTpl);
		modelMap.put("success", true);
		modelMap.put("info", "Changes Saved");
		return modelMap;
	}
	
}
