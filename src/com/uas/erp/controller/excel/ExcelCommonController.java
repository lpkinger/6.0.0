package com.uas.erp.controller.excel;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.excel.ExcelCellService;
import com.uas.erp.service.excel.ExcelCommonService;
import com.uas.erp.service.excel.ExcelElementService;
import com.uas.erp.service.excel.ExcelFileService;
import com.uas.erp.service.excel.ExcelFileTemplateService;
import com.uas.erp.service.excel.ExcelSheetService;

@Controller
public class ExcelCommonController {
	
	@Autowired
	private ExcelCellService excelCellService;
	
	@Autowired
	private ExcelFileTemplateService excelFileTplService;
	@Autowired
	private ExcelFileService excelFileService;
	
	
	@Autowired
	private ExcelElementService excelElementService;
	
	@Autowired
	private ExcelSheetService excelSheetService;
	
	@Autowired
	private ExcelCommonService excelCommonService;
	
	
	
	//需要
	/**公共载入文件
	 * @param fileId
	 * @param startCellId
	 * @param size
	 * @param throttleOfBigFile
	 * @param isTpl
	 * @return
	 */
	@RequestMapping("/Excel/common/loadExcelInfo.action")
	@ResponseBody
	public Map<String, Object> loadExcelInfo (Integer fileId, Integer startCellId, Integer size, int throttleOfBigFile, Boolean isTpl){
		return excelCommonService.loadExcelInfo(fileId, startCellId, size, throttleOfBigFile, isTpl);
	}
	
	
	/**公共载入文件5
	 * @param fileId
	 * @param startCellId
	 * @param size
	 * @param throttleOfBigFile
	 * @param isTpl
	 * @return
	 */
	@RequestMapping("/Excel/common/loadSheet5.action")
	@ResponseBody
	public Map<String, Object> loadExcelInfo5 (@RequestParam(value="sheetId", required = true) Integer tabId,
            @RequestParam(defaultValue = "false") Boolean notActiveTabFlag,
            @RequestParam Integer throttleOfBigFile,
            @RequestParam Integer size, 
            Boolean isTpl,
            HttpServletRequest request,
            HttpServletResponse response){
		return excelCommonService.loadExcelInfo5(tabId,notActiveTabFlag,throttleOfBigFile,size);
	}
	
	@RequestMapping("/Excel/common/loadCellOnDemand3.action")
	@ResponseBody
	public Map<String, Object> loadCellOnDemand3 (@RequestParam Integer fileId,
            @RequestParam Integer sheetId,
            @RequestParam(defaultValue = "0") Integer startCellId,
            @RequestParam Integer size,
            @RequestParam(defaultValue = "false") Boolean skipCal,
            HttpServletRequest request, HttpServletResponse response){
		return excelCommonService.loadCellOnDemand3(fileId,sheetId,startCellId,size,skipCal);
	}
	
	@RequestMapping("/Excel/common/loadRange3.action")
	@ResponseBody
	public Map<String, Object> loadRange3 (@RequestParam String range, 
            @RequestParam(defaultValue="0")Integer nextCellId,@RequestParam Integer limit, 
            HttpServletRequest request, HttpServletResponse response){
		return excelCommonService.loadRange3(range,nextCellId,limit,false);
	}
	
	
	
	
	/**公共载入文件元素
	 * @param fileId
	 * @param sheetId
	 * @param startElementId
	 * @param size
	 * @return
	 */
	@RequestMapping("/Excel/common/loadElementOnDemand.action")
	@ResponseBody
	public Map<String, Object> loadElementOnDemand (
			String fileId,
			String sheetId,
			@RequestParam(defaultValue = "0") Integer startElementId,
			Integer size){
		return excelElementService.loadElementOnDemand(fileId, sheetId, startElementId, size);
	}
	
	
	
	
	
	
	//需要
	/**公共改文件名
	 * @param id
	 * @param name
	 * @param description
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/Excel/common/changeFileName.action")
	@ResponseBody
	public Map<String, Object> changeFileName (@RequestParam(required = true) String id,
			@RequestParam(required = true) String name,
			@RequestParam(required = false) String description,
			Boolean isTpl,
			HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> modelMap = new HashMap<String,Object>();
		if (isTpl) {
			excelFileTplService.changeFileName(id,name,description);
		}else {
			excelFileService.changeFileName(id,name,description);
		}
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/Excel/common/getExcelInfo.action")
	@ResponseBody
	public Map<String, Object> getExcelInfo (
			int id,
			Boolean isTpl){
		if (isTpl) {
			return excelFileTplService.getExcelInfo(id);
		}
		return excelFileService.getExcelInfo(id);
	}
	
	
	
}
