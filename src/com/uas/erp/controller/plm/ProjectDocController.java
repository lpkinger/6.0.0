package com.uas.erp.controller.plm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.ExcelToHtml;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.PptToHtml;
import com.uas.erp.core.WordToHtml;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.ProjectDocPowerDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.FileUpload;
import com.uas.erp.service.common.FormAttachService;
import com.uas.erp.service.plm.ProjectDocService;

@Controller
public class ProjectDocController extends BaseController{
	
	@Autowired
	private ProjectDocService projectDocService;
	
	@Autowired
	private ProjectDocPowerDao projectDocPowerDao;
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private FormAttachService  formAttachService;
	
	@RequestMapping("/plm/project/getProjectFileTree.action")
	@ResponseBody
	public Map<String, Object> getProjectFileTree(String condition,String checked) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<String, Object>> tree = projectDocService.getProjectFileTree(condition,checked);
		modelMap.put("tree", tree);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/plm/project/getProjectFileList.action")  
	@ResponseBody 
	public Map<String, Object> getFileList(HttpSession session,String formCondition,Integer id,Integer kind,Integer page,
			Integer start,Integer limit,Integer _noc, String search, Integer canRead) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		boolean _canRead = false;
		if (canRead!=null && canRead ==1) {
			_canRead = true;
		}
		modelMap = projectDocService.getFileList(formCondition, id, kind, page, start, limit, _noc, search, _canRead);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/project/saveAndUpdateProjectFileList.action")  
	@ResponseBody 
	public Map<String, Object> saveAndUpdateTree(HttpSession session,String create,String update,Integer _noc) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		modelMap = projectDocService.saveAndUpdateTree(create, update,_noc);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/plm/project/exportProjectExcel.action")  
	@ResponseBody 
	public Map<String, Object> exportProjectExcel(HttpSession session,HttpServletRequest request,HttpServletResponse response,String formCondition,String prids) throws IOException {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		HSSFWorkbook workbook = (HSSFWorkbook) projectDocService.downloadAsExcel(formCondition,prids);
		Object name = baseDao.getFieldDataByCondition("project", "prj_name", formCondition);
		String title = name.toString();
		String filename = URLEncoder.encode(title + ".xls", "UTF-8");
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment;filename=" + filename);
		OutputStream out;
		out = response.getOutputStream();
		workbook.write(out);
		out.close();		
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/plm/project/deleteProjectFile.action")  
	@ResponseBody 
	public Map<String, Object> deleteNode(HttpSession session,String id,String type,Integer _noc) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		projectDocService.deleteNode(id, type,_noc);		
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/plm/project/getProjectMsg.action")  
	@ResponseBody 
	public Map<String, Object> getProjectMsg(HttpSession session,String formCondition) {
		Map<String,Object> modelMap = null;
		modelMap = projectDocService.getProjectMsg(formCondition);		
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/plm/project/ifMainTaskOpen.action")  
	@ResponseBody 
	public Map<String, Object> ifMainTaskOpen(HttpSession session,String condition) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		modelMap.put("maintaskactive",projectDocService.ifMainTaskOpen(condition));		
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/plm/project/getFilePowers.action")  
	@ResponseBody 
	public Map<String, Object> getFilePowers(HttpSession session,Integer docid,Integer prjid,Integer _noc) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		modelMap.put("data",projectDocService.getFilePowers(docid,prjid,_noc));		
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/plm/project/saveFilePowers.action")  
	@ResponseBody 
	public Map<String, Object> saveFilePowers(HttpSession session,Boolean appyforChilds,String filePowers,Integer _noc) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		projectDocService.saveFilePowers(appyforChilds,filePowers,_noc);		
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/plm/project/getHtml.action")
	@ResponseBody
	public Map<String, Object> getHtml(HttpServletRequest request, Integer folderId,String id, Integer _noc, Integer canRead) throws Exception {
		JSONObject obj = formAttachService.getFiles(id).getJSONObject(0);
		String path = obj.getString("fp_path");
		String fileName = obj.getString("fp_name");
		String type = request.getParameter("type");
		if (canRead==null || canRead ==0) {
			 projectDocPowerDao.powerForRead(folderId, _noc);
		 }
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String newPath = "";
		try {
			if ("doc".equals(type) || "docx".equals(type)) {
				newPath = WordToHtml.getWord(path, null, fileName);
			}
			if ("xls".equals(type) || "xlsx".equals(type)) {
				newPath = ExcelToHtml.getHtml(path, fileName);
			}
			if ("pdf".equals(type)){
				newPath = path;
			}
		} catch (Exception e) {
			BaseUtil.showError("打开文件失败！请先下载再打开");
		}
		modelMap.put("newPath", newPath);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 文件上传
	 */
	 @ResponseBody
	@RequestMapping(value="/plm/project/upload.action",produces = "text/html;charset=UTF-8")
	public String upload(HttpSession session,Integer fieldId,String condition,Integer _noc,FileUpload uploadItem) {
		 
		 Employee employee = (Employee) session.getAttribute("employee");
		 
		 return projectDocService.upload(employee, fieldId, condition, _noc, uploadItem);
	}
	 
	 @RequestMapping("/plm/project/downloadbyId.action")
		public void downloadById(HttpServletResponse response, HttpServletRequest request, Integer folderId, Integer _noc, Integer canRead) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		 	if (canRead==null||canRead==0) {
		 		projectDocPowerDao.powerForDown(folderId, _noc);
			}
		 	String id = request.getParameter("id");
			JSONObject obj = formAttachService.getFiles(id).getJSONObject(0);
			String path = obj.getString("fp_path");
			String size = "0";
			InputStream in = null;
			try {
				if (path.startsWith("B2B://")) {// 文件在云平台
					path = SystemSession.getUser().getCurrentMaster().getMa_b2bwebsite() + "/" + path.substring(6);
					size = obj.getString("fp_size");
					in = HttpUtil.download(path);
				} else if (path.startsWith("http:")) {// 文件存放在文件系统，这里存放的是http接口地址
					in = HttpUtil.download(path);
					size = String.valueOf(in.available());
				} else {
					File file = new File(path);
					in = new FileInputStream(file);
					size = String.valueOf(file.length());
				}
			} catch (FileNotFoundException e) {
				BaseUtil.showError("文件未找到！");
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
	
	 @RequestMapping("/plm/project/getPPTHtml.action")
	 @ResponseBody
	 public Map<String, Object> getPPTHtml(HttpServletRequest request, Integer folderId,String id, Integer _noc, Integer canRead){
		 JSONObject obj = formAttachService.getFiles(id).getJSONObject(0);
		 String path = obj.getString("fp_path");
		 String fileName = obj.getString("fp_name");
		 
		 if (canRead==null || canRead ==0) {
			 projectDocPowerDao.powerForRead(folderId, _noc);
		 }
		 
		 Map<String, Object> modelMap = new HashMap<String, Object>();
		 try{
			 modelMap.put("pageSize", PptToHtml.getHtml(path, fileName).get("pageSize"));
			 modelMap.put("path", PptToHtml.getHtml(path, fileName).get("path"));
		 }catch (Exception e){
			 BaseUtil.showError("打开文件失败！请先下载再打开");
		 }
		 modelMap.put("success", true);
		 return modelMap;
	 }
	 
	 @RequestMapping("/plm/project/getImage.action")
	 public void getImage(String path, String page,HttpServletResponse response) throws IOException{
		 OutputStream out = null;
		 FileInputStream fis = null;
		 try {
			 fis = new FileInputStream(new File(path+File.separator+page+".jpeg"));
			 response.setContentType("image/jped");
			 response.setContentType("text/html; charset=UTF-8");
			 out = response.getOutputStream();
			 int len = 0;  
			 byte[] buffer = new byte[1024 * 1024];  
			 while ((len = fis.read(buffer)) != -1){  
			     out.write(buffer,0,len);  
			 }  
			 out.flush();
		} catch (Exception e) {
			BaseUtil.showError("打开文件失败！请先下载再打开");
		} finally{
			out.close();  
            fis.close();
		}
	 }
}
