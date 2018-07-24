package com.uas.erp.controller.plm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.mpp.MPPReader;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.common.FilePathService;
import com.uas.erp.service.plm.ProjectMainTaskService;

@Controller
public class ProjectMainTaskController {
	@Autowired
	private ProjectMainTaskService projectMainTaskService;
	@Autowired
	private FilePathService filePathService;

	@RequestMapping("/plm/main/saveProjectMainTask.action")
	@ResponseBody
	public Map<String, Object> saveProjectMainTask(HttpSession session, String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectMainTaskService.saveProjectMainTask(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/main/deleteProjectMainTask.action")
	@ResponseBody
	public Map<String, Object> deleteProjectMainTask(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectMainTaskService.deleteProjectMainTask(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/main/updateProjectMainTask.action")
	@ResponseBody
	public Map<String, Object> updateProjectMainTask(HttpSession session, String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectMainTaskService.updateProjectMainTask(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/main/submitProjectMainTask.action")
	@ResponseBody
	public Map<String, Object> submitProjectMainTask(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectMainTaskService.submitProjectMainTask(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/main/resSubmitProjectMainTask.action")
	@ResponseBody
	public Map<String, Object> resSubmitProjectMainTask(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectMainTaskService.resSubmitProjectMainTask(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/main/auditProjectMainTask.action")
	@ResponseBody
	public Map<String, Object> auditProjectMainTask(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectMainTaskService.auditProjectMainTask(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/main/resAuditProjectMainTask.action")
	@ResponseBody
	public Map<String, Object> resAuditProjectMainTask(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectMainTaskService.resAuditProjectMainTask(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/main/end.action")
	@ResponseBody
	public Map<String, Object> End(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectMainTaskService.End(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/main/resEnd.action")
	@ResponseBody
	public Map<String, Object> resEnd(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectMainTaskService.resEnd(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转任务执行单
	 * */
	@RequestMapping("plm/main/TurnTask.action")
	@ResponseBody
	public Map<String, Object> TurnTask(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectMainTaskService.TurnTask(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 加载任务节点
	 * */
	@RequestMapping("plm/main/LoadTaskNode.action")
	@ResponseBody
	public Map<String, Object> LoadTaskNode(HttpSession session, int id, String type, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectMainTaskService.LoadTaskNode(id, type, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * Excel导入
	 * 
	 * @throws IOException
	 * */
	@RequestMapping("plm/main/ImportExcel.action")
	@ResponseBody
	public String ImportExcel(HttpSession session, int id, int fileId, String startdate) throws IOException {
		InputStream is = null;
		String filePath = filePathService.getFilepath(fileId);
		Workbook wbs = null;
		String ft = filePath.substring(filePath.lastIndexOf(".") + 1);
		is = new FileInputStream(new File(filePath));
		if (ft.equals("xls")) {
			wbs = new HSSFWorkbook(is);
		} else if (ft.equals("xlsx")) {
			wbs = new XSSFWorkbook(is);
		} else {
			return new String("{error: 'excel文件的格式不太规范,导入失败<hr>可以尝试将文件另存为,然后导入'}".getBytes("utf-8"), "iso8859-1");
		}
		boolean bool = projectMainTaskService.ImportExcel(id, wbs, filePath.substring(filePath.lastIndexOf("/") + 1), startdate);
		if (bool) {
			// Excel 解析成功之后要删除
			File file = new File(filePath);
			// 路径为文件且不为空则进行删除
			if (file.isFile() && file.exists()) {
				file.delete();
			}
		}
		String r = "{success: true}";
		is.close();
		return r;
	}

	@RequestMapping("plm/main/ImportMpp.action")
	@ResponseBody
	public String ImportMpp(HttpSession session, int id, int fileId, String startdate) {
		InputStream is = null;
		String filePath = filePathService.getFilepath(fileId);
		MPPReader reader = null;
		ProjectFile pf = null;
		try {
			String ft = filePath.substring(filePath.lastIndexOf(".") + 1);
			is = new FileInputStream(new File(filePath));
			if (ft.equals("mpp")) {
				reader = new MPPReader();
				pf = reader.read(is);
			} else
				return new String("{error: 'MPP文件的格式不太规范,导入失败<hr>可以尝试将文件另存为,然后导入'}".getBytes("utf-8"), "iso8859-1");
			boolean bool = projectMainTaskService.ImportMpp(id, pf, filePath.substring(filePath.lastIndexOf("/") + 1), startdate);
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
		} catch (Exception e) {
			e.printStackTrace();
			try {
				return new String("{error: 'MPP文件的格式不太规范,导入失败<hr>可以尝试将文件另存为,然后导入'}".getBytes("utf-8"), "iso8859-1");
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
}
