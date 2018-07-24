package com.uas.erp.controller.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lowagie.text.DocumentException;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.PathUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.model.ConditionItem;
import com.uas.erp.model.Employee;
import com.uas.erp.model.FileUpload;
import com.uas.erp.service.common.ExcelService;
import com.uas.erp.service.common.FilePathService;

@Controller
public class ExcelController {
	@Autowired
	private FilePathService filePathService;
	@Autowired
	private ExcelService excelService;

	/** Excel 上传 */
	@SuppressWarnings("resource")
	@RequestMapping("/excel/uploadExcel.action")
	@ResponseBody
	public String upload(HttpSession session, String em_code, FileUpload uploadItem) {
		try {
			String filename = uploadItem.getFile().getOriginalFilename();
			String ft = filename.substring(filename.lastIndexOf(".") + 1);
			if (ft.equals(".xls") || ft.equals(".xlsx"))
				return "{success:false,info:'请选择正确格式的文件!'}";
			long size = uploadItem.getFile().getSize();
			if (size > 104857600) {
				return "{error: '文件过大'}";
			}
			String path = PathUtil.getFilePath() + "postattach";
			File file = new File(path);
			if (!file.isDirectory()) {
				file.mkdir();
				path = path + File.separator + em_code;
				new File(path).mkdir();
			} else {
				path = path + File.separator + em_code;
				file = new File(path);
				if (!file.isDirectory()) {
					file.mkdir();
				}
			}
			path = path + File.separator + filename;
			file = new File(path);
			BufferedOutputStream bos = null;
			BufferedInputStream bis = null;
			try {
				bos = new BufferedOutputStream(new FileOutputStream(file));
				bis = new BufferedInputStream(uploadItem.getFile().getInputStream());
				int c;
				while ((c = bis.read()) != -1) {
					bos.write(c);
					bos.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			int id = filePathService.saveFilePath(path, (int) size, filename,
					(Employee) session.getAttribute("employee"));
			return "{success: true, filepath: " + id + ",size: " + size + ",path:\"../postattach/" + em_code + "/"
					+ filename.substring(filename.lastIndexOf(".") + 1) + "\"}";
		} catch (Exception e) {
			return "{error: '文件过大,上传失败'}";
		}
	}

	@RequestMapping("/excel/loadJson.action")
	@ResponseBody
	public String loadJson(HttpSession session, int fieldId) {
		InputStream is = null;
		String filePath = filePathService.getFilepath(fieldId);
		Workbook wbs = null;
		try {
			String ft = filePath.substring(filePath.lastIndexOf(".") + 1);
			is = new FileInputStream(new File(filePath));
			if (ft.equals("xls")) {
				wbs = new HSSFWorkbook(is);
			} else if (ft.equals("xlsx")) {
				wbs = new XSSFWorkbook(is);

			} else if (ft.equals("txt")) {
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				br.close();
			} else {
				return new String("{error: 'excel文件的格式不太规范,导入失败<hr>可以尝试将文件另存为,然后导入'}".getBytes("utf-8"), "iso8859-1");
			}
			boolean bool = excelService.getJsonData(wbs, filePath.substring(filePath.lastIndexOf("/") + 1), fieldId);
			if (bool) {
				// Excel 解析成功之后要删除
				File file = new File(filePath);
				// 路径为文件且不为空则进行删除
				if (file.isFile() && file.exists()) {
					file.delete();
				}
			}
			String r = "{success: true}";
			return r;
			// return new String(r.getBytes("utf-8"), "iso8859-1");
		} catch (Exception e) {
			e.printStackTrace();
			try {
				return new String("{error: 'excel文件的格式不太规范,导入失败<hr>可以尝试将文件另存为,然后导入'}".getBytes("utf-8"), "iso8859-1");
			} catch (UnsupportedEncodingException e1) {
				return "{success: false}";
			}
		} finally {
			try {
				is.close();
			} catch (IOException e) {

			}
		}
	}

	/** 是否加密 */
	@RequestMapping("/excel/isShield.action")
	@ResponseBody
	public String isShield(HttpSession session, int id) {
		return "{success:true,fileShield:'no'}";
	}

	/**
	 * 更新Excel数据 由于更新次数可能很多 所以还是存表
	 **/
	@RequestMapping("/excel/updateBatchCells.action")
	@ResponseBody
	public String updateBatchCells(HttpSession session, String tabId, String cellJsons) {
		Employee employee = (Employee) session.getAttribute("employee");
		excelService.updateBatchCells(tabId, cellJsons, employee);
		return "{success:true}";
	}

	@RequestMapping("/excel/saveAsExcel.action")
	@ResponseBody
	public String saveAsExcel(HttpSession session, String folderId, String name, String oldFileId) {
		Employee employee = (Employee) session.getAttribute("employee");
		excelService.saveAsExcel(name, employee);
		return "{success:true}";
	}

	@RequestMapping("/excel/saveJsonAs.action")
	@ResponseBody
	public String saveAsTemplate(HttpSession session, String inJson) {
		Employee employee = (Employee) session.getAttribute("employee");
		excelService.saveAsTemplate(inJson, employee);
		return "{success:true}";
	}

	/**
	 * @RequestMapping("excel/LoadJsonByTemplate.action")
	 * @ResponseBody public String loadJsonByTemplate(HttpSession session,int
	 *               etId){ Employee
	 *               employee=(Employee)session.getAttribute("employee");
	 *               boolean bool=excelService.LoadJsonByTemplate(etId,
	 *               employee); return "{success:true}"; }
	 **/
	@RequestMapping("excel/getExcelTemplate.action")
	@ResponseBody
	public Map<String, Object> getExcelTemplate(HttpSession session, String exname, String folderId, int limit,
			String query, int start) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("success", true);
		int enid = (Integer) session.getAttribute("en_uu");
		int count = excelService.getTemplateCount(query, enid, employee);
		JSONObject obj = excelService.getExcelTemplateByPage(limit, start, count, query, enid, employee);
		modelMap.put("totalCount", count);
		modelMap.put("results", obj.getJSONArray("data"));
		modelMap.put("metaData", obj.getJSONObject("meta"));
		return modelMap;
	}

	@RequestMapping("excel/getTemplateCondition.action")
	@ResponseBody
	public Map<String, Object> getTemplateCondition(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<ConditionItem> items = excelService.getTemplateCondition(id);
		modelMap.put("success", true);
		modelMap.put("items", items);
		modelMap.put("title", "条件输入");
		return modelMap;
	}

	@RequestMapping("excel/ishaveCondition.action")
	@ResponseBody
	public Map<String, Object> ishaveCondition(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		boolean bool = excelService.ishaveCondition(id);
		modelMap.put("success", true);
		modelMap.put("havecondition", bool);
		return modelMap;
	}

	@RequestMapping("excel/loadExcelTemplate.action")
	@ResponseBody
	public Map<String, Object> getLoadExcelTemplate(HttpSession session, int id, String colcondition,
			String cellcondition, String isTemplate) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("success", true);
		excelService.getJsonDataByTemplate(id, colcondition, cellcondition, isTemplate, employee);
		return modelMap;
	}

	// 恢复界面
	@RequestMapping("excel/ResetUI.action")
	@ResponseBody
	public String ResetExcelTemplate(HttpSession session) {
		return excelService.getResetData();
	}

	// 删除模板
	@RequestMapping("excel/deleteTemplate.action")
	@ResponseBody
	public String deleteTemplate(HttpSession session, int id) {
		excelService.deleteTemplateById(id);
		return "{success:true}";
	}

	@RequestMapping("excel/DownLoadAsExcel.action")
	@ResponseBody
	public void DownLoad(HttpServletResponse response, HttpServletRequest request, HttpSession session, String type,
			String title) throws IOException {
		Employee employee = (Employee) session.getAttribute("employee");
		HSSFWorkbook workbook = (HSSFWorkbook) excelService.downLoadAsExcel(type, employee);
		String filename = URLEncoder.encode(title + "." + type, "UTF-8");
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment;filename=" + filename);
		OutputStream out;
		out = response.getOutputStream();
		workbook.write(out);
		out.close();
	}

	@RequestMapping("excel/DownLoadAsPDF.action")
	@ResponseBody
	public void DownLoadAsPDF(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			String title) throws IOException, DocumentException {
		Employee employee = (Employee) session.getAttribute("employee");
		ByteArrayOutputStream ba = excelService.downLoadAsPDF(title, employee);
		response.setHeader("Content-disposition", "attachment; filename="
				+ new String(title.getBytes("gb2312"), "iso8859-1"));
		response.setContentType("application/pdf");
		response.setContentLength(ba.size());
		OutputStream out = response.getOutputStream();
		ba.writeTo(out);
		out.flush();
		out.close();
	}

	@RequestMapping("excel/savePanelAsExcel.action")
	@ResponseBody
	public void savePanelAsExcel(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			String caller, int id, String  o) throws IOException {
		Employee employee = (Employee) session.getAttribute("employee");
		String language = (String) session.getAttribute("language");
		HSSFWorkbook workbook = (HSSFWorkbook) excelService.savePanelAsExcel(caller, id, o,employee, language);
		String title = excelService.getFormTitle(caller, id);
		String filename = URLEncoder.encode(title + ".xls", "UTF-8");
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment;filename=" + filename);
		OutputStream out;
		out = response.getOutputStream();
		workbook.write(out);
		out.close();
	}

	/**
	 * 缺料表二维导出
	 * */
	@RequestMapping("excel/twoExport.action")
	@ResponseBody
	public void twoExport(HttpServletResponse response, HttpServletRequest request, HttpSession session, String data,
			String columns) throws IOException {
		Employee employee = (Employee) session.getAttribute("employee");
		String language = (String) session.getAttribute("language");
		HSSFWorkbook workbook = (HSSFWorkbook) excelService.twoExport(data, columns, employee, language);
		String filename = URLEncoder.encode("缺料.xls", "UTF-8");
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment;filename=" + filename);
		OutputStream out;
		out = response.getOutputStream();
		workbook.write(out);
		out.close();
	}

	/** 多个BOM多级展开 用量查询 */
	@RequestMapping("/excel/exportBatchBOMAsExcel.action")
	@ResponseBody
	public void exportBatchBOMAsExcel(HttpServletResponse response, HttpServletRequest request, HttpSession session)
			throws IOException {
		Employee employee = (Employee) session.getAttribute("employee");
		String language = (String) session.getAttribute("language");
		String title = "BOM用量查看" + DateUtil.parseDateToString(null, Constant.YMD);
		HSSFWorkbook workbook = (HSSFWorkbook) excelService.exportBatchBOMAsExcel(employee, language);
		String filename = URLEncoder.encode(title + ".xls", "UTF-8");
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment;filename=" + filename);
		OutputStream out;
		out = response.getOutputStream();
		workbook.write(out);
		out.close();
	}
	
	/** BOM有效性检测导出所有的问题详情 */
	@RequestMapping("/excel/exportBOMCheckMessageExcel.action")
	@ResponseBody
	public void exportBOMCheckMesExcel(HttpServletResponse response, HttpServletRequest request, HttpSession session,String bomId,String caller)
			throws IOException {
		String title = "BOM有效性检测详情" + DateUtil.parseDateToString(null, Constant.YMD);
		HSSFWorkbook workbook = (HSSFWorkbook) excelService.exportBOMCheckMesExcel(bomId,caller);
		String filename = URLEncoder.encode(title + ".xls", "UTF-8");
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment;filename=" + filename);
		OutputStream out;
		out = response.getOutputStream();
		workbook.write(out);
		out.close();
	}
	
	/** 
	 *  导出系统错误信息 
	 **/
	@RequestMapping(value="/excel/saveAsTxt.action",method=RequestMethod.POST)
	@ResponseBody
	public String writeToTxt(HttpServletResponse response,String error) throws IOException {
        byte[] result = error.getBytes();
        String title = DateUtil.format(new Date(), "yyyy-MM-dd-HH时mm分ss")+ "-导出错误信息.txt";
        String filename = URLEncoder.encode(title , "UTF-8");
        OutputStream os = response.getOutputStream();
        try {
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
            response.setContentType("text/plain;charset=utf-8");
            os.write(result);
            os.flush();
        }
        finally {
            if (os != null) {
                os.close();
            }
        }
        return "{success:true}";
     }
}
