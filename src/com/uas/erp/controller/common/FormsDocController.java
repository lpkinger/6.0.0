package com.uas.erp.controller.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;










import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.ExcelToHtml;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.PptToHtml;
import com.uas.erp.core.WordToHtml;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Employee;
import com.uas.erp.model.FileUpload;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.common.FormAttachService;
import com.uas.erp.service.common.FormsDocService;

@Controller
public class FormsDocController {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	FormsDocService formsDocService;
	
	@Autowired
	private FormAttachService formAttachService;
	
	@Autowired
	private EnterpriseService enterpriseService;
	
	@RequestMapping("/common/FormsDoc/getFileList.action")
	@ResponseBody
	public Map<String, Object> getFileList(String caller, Integer formsid, Integer id, Integer kind, Integer page,
			Integer start, Integer limit, String search) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = formsDocService.getFileList(caller, formsid, id, kind, page, start, limit, search);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 文件上传
	 */
	@ResponseBody
	@RequestMapping(value = "/common/FormsDoc/upload.action", produces = "text/html;charset=UTF-8")
	public String upload(HttpSession session, String caller, Integer fieldId, String condition, FileUpload uploadItem) {

		Employee employee = (Employee) session.getAttribute("employee");

		return formsDocService.upload(employee, caller, fieldId, condition, uploadItem);
	}

	@RequestMapping("/common/FormsDoc/saveAndUpdateFormsDocFileList.action")
	@ResponseBody
	public Map<String, Object> saveAndUpdateTree(HttpSession session, String caller, String create, String update) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = formsDocService.saveAndUpdateTree(caller, create, update);
		modelMap.put("success", true);
		return modelMap;
	}


	@RequestMapping("/common/FormsDoc/deleteFormsDocFile.action")
	@ResponseBody
	public Map<String, Object> deleteNode(String caller, String id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		formsDocService.deleteNode(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/common/FormsDoc/downloadbyId.action")
	public void downloadById(HttpServletResponse response, HttpServletRequest request) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		String caller = request.getParameter("caller");
		if (baseDao.isDBSetting(caller,"fileInParent")) {
			Employee employee = (Employee) request.getSession().getAttribute("employee");
			Master master = employee.getCurrentMaster();
			Master parentMaster = null;
			if (master != null && master.getMa_pid()!= null && master.getMa_pid() > 0) {
				parentMaster = enterpriseService.getMasterByID(master.getMa_pid());
			}
			if (parentMaster!=null) {
				SpObserver.putSp(parentMaster.getMa_name());
			}
		}
		
		String id = request.getParameter("id");
		JSONObject obj = formAttachService.getFiles(id).getJSONObject(0);
		String path = obj.getString("fp_path");
		String size = "0";
		InputStream in = null;
		
		if (path.startsWith("http:")||path.startsWith("https:")) {
			size = obj.getString("fp_size");
			in = HttpUtil.download(path);
		}else if (path.startsWith("ftp:") || path.startsWith("sftp:")) {
			// 存放在其他网络资源中，直接跳转至链接地址
			response.sendRedirect(path);
			return;
		}else if (path.startsWith("B2B://")) {// 文件在云平台
			path = SystemSession.getUser().getCurrentMaster().getMa_b2bwebsite() + "/" + path.substring(6);
			size = obj.getString("fp_size");
			in = HttpUtil.download(path);
		} else {
			File file = new File(path);
			in = new FileInputStream(file);
			size = String.valueOf(file.length());
		}
		OutputStream os = response.getOutputStream();
		response.addHeader("Content-Disposition", "attachment;filename="
				+ new String(obj.getString("fp_name").getBytes("utf-8"), "iso-8859-1"));
		response.addHeader("Content-Length", size);
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/octec-stream");
		int data = 0;
		while ((data = in.read()) != -1) {
			os.write(data);
		}
		in.close();
		os.close();
	}
	
	@RequestMapping("/common/FormsDoc/getHtml.action")
	@ResponseBody
	public Map<String, Object> getHtml(HttpServletRequest request, String caller, String folderId) throws Exception {
		
		if (baseDao.isDBSetting(caller, "fileInParent")) {
			Employee employee = (Employee) request.getSession().getAttribute("employee");
			Master master = employee.getCurrentMaster();
			Master parentMaster = null;
			if (master != null && master.getMa_pid()!= null && master.getMa_pid() > 0) {
				parentMaster = enterpriseService.getMasterByID(master.getMa_pid());
			}
			if (parentMaster!=null) {
				SpObserver.putSp(parentMaster.getMa_name());
			}
		}
		
		JSONObject obj = formAttachService.getFiles(folderId).getJSONObject(0);
		String path = obj.getString("fp_path");
		String fileName = obj.getString("fp_name");
		String type = request.getParameter("type");
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String newPath = "";
		try {
			if ("doc".equals(type.toLowerCase()) || "docx".equals(type.toLowerCase())) {
				newPath = WordToHtml.getWord(path, null,fileName);
			}
			if ("xls".equals(type.toLowerCase()) || "xlsx".equals(type.toLowerCase())) {
				newPath = ExcelToHtml.getHtml(path,fileName);
			}
			if ("png".equals(type.toLowerCase()) || "jpg".equals(type.toLowerCase()) || "gif".equals(type.toLowerCase()) || 
				"jpeg".equals(type.toLowerCase()) || "pdf".equals(type.toLowerCase())){
				newPath = path;
			}
			if ("ppt".equals(type.toLowerCase()) || "pptx".equals(type.toLowerCase())) {
				modelMap.putAll(PptToHtml.getHtml(path, fileName));
			}
		} catch (Exception e) {
			BaseUtil.showError("打开文件失败！");
		}
		if (!("ppt".equals(type.toLowerCase()) || "pptx".equals(type.toLowerCase()))) {
			modelMap.put("newPath", newPath);
		}
		
		modelMap.put("success", true);
		return modelMap;
	}
}
