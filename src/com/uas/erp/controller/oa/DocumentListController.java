package com.uas.erp.controller.oa;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.ExcelToHtml;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.PathUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.WordToHtml;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.DocumentList;
import com.uas.erp.model.FileUpload;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.common.FilePathService;
import com.uas.erp.service.common.FormAttachService;
import com.uas.erp.service.oa.DocumentListService;
import com.uas.erp.service.oa.DocumentPowerService;

@Controller
public class DocumentListController {
	@Autowired
	private FilePathService filePathService;
	@Autowired
	private FormAttachService formAttachService;
	@Autowired
	private DocumentListService documentListService;
	@Autowired
	private DocumentPowerService documentPowerService;

	@SuppressWarnings("resource")
	@RequestMapping("/oa/documentlist/upload.action")
	public @ResponseBody
	String upload(String caller, String em_code, FileUpload uploadItem, int dcl_id) {
		String pa = "WD_" + dcl_id;
		try {
			String filename = uploadItem.getFile().getOriginalFilename();
			long size = uploadItem.getFile().getSize();
			if (size > 500000) {
				return "{error: '文件过大'}";
			}
			String path = PathUtil.getDocPath();
			File file = new File(path);
			if (!file.exists()) {
				file.mkdir();
			}
			path = path + File.separator + pa;
			file = new File(path);
			if (!file.exists()) {
				file.mkdir();
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
			int id = filePathService.saveFilePath(path, (int) size, filename, SystemSession.getUser());
			return "{success: true, filepath: " + id + ",size: " + size + "}";
		} catch (Exception e) {
			return "{error: '文件过大,上传失败'}";
		}
	}

	@RequestMapping("/doc/download.action")
	@ResponseBody
	public String download(HttpServletResponse response, HttpServletRequest request, String fileName, int folderId) throws IOException {
		String path = request.getParameter("path");
		String escape = request.getParameter("escape");
		documentPowerService.checkDownloadPower(folderId);
		return documentListService.download(response,path,escape,fileName);
	}

	@RequestMapping("/doc/downloadbyIds.action")
	@ResponseBody
	public void downloadByIds(HttpServletResponse response, HttpServletRequest request,int folderId) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		documentPowerService.checkDownloadPower(folderId);
		String ids = request.getParameter("ids");
		if(StringUtil.hasText(ids)){
			documentListService.downloadbyIds(response,ids,null);
		}
	}
	
	
	@RequestMapping("/oa/document/getDirectoryByCondition.action")
	@ResponseBody
	public Map<String, Object> getInfoByCondition(String caller, HttpServletRequest request, int parentId, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("tree", documentListService.getDirectoryByCondition(parentId, condition, caller));
		modelMap.put("sucess", true);
		return modelMap;
	}

	/**
	 * 保存目录
	 */
	@RequestMapping("oa/doc/saveDir.action")
	@ResponseBody
	public Map<String, Object> saveDir(String caller, HttpServletRequest request, String formStore) {
		boolean bool = documentListService.save(caller, formStore);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("needflow", bool);
		return modelMap;
	}

	/**
	 * 更新目录
	 */
	@RequestMapping("oa/doc/updateDir.action")
	@ResponseBody
	public Map<String, Object> updateDir(String caller, HttpServletRequest request, String formStore) {
		documentListService.update(formStore, caller);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存文档
	 * */
	@RequestMapping("oa/doc/saveDoc.action")
	@ResponseBody
	public Map<String, Object> saveDoc(String caller, HttpServletRequest request, String formStore) {
		boolean bool = documentListService.save(caller, formStore);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("needflow", bool);
		return modelMap;
	}

	/**
	 * 更新文档
	 */
	@RequestMapping("oa/doc/updateDoc.action")
	@ResponseBody
	public Map<String, Object> updateDoc(String caller, HttpServletRequest request, String formStore) {
		documentListService.update(formStore, caller);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 * */
	@RequestMapping("oa/documentlist/delete.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, HttpServletRequest request, int id) {
		documentListService.delete(id, caller);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("oa/documentlist/deleteDoc.action")
	@ResponseBody
	public Map<String, Object> deleteDoc(String caller, HttpServletRequest request, String data) {
		documentListService.deleteDoc(data, caller);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 加载子文件夹
	 * */
	@RequestMapping("oa/documetlist/loadDir.action")
	@ResponseBody
	public Map<String, Object> loadDir(String caller, HttpServletRequest request, int parentId, String condition) {
		List<JSONTree> tree = documentListService.loadDir(parentId, condition, caller);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("tree", tree);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 按parentId 找文件和文件夹
	 * */
	@RequestMapping("oa/docuemtlist/getDocumentsByParentId.action")
	@ResponseBody
	public Map<String, Object> getDocuments(String caller, int parentId, String condition) {
		List<DocumentList> documents = documentListService.getDocumentsByParentId(parentId, condition, caller);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("docs", documents);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 部分信息更新操作
	 * */
	@RequestMapping("oa/documentlist/DocUpdateByType.action")
	@ResponseBody
	public Map<String, Object> DocUpdateByType(String caller, String formStore, String type) {
		documentListService.DocUpdateByType(caller, formStore, type);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 文档变更
	 * */
	@RequestMapping("oa/DocChange/save.action")
	@ResponseBody
	public Map<String, Object> saveChange(String caller, String formStore) {
		documentListService.saveChange(formStore, caller);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 发表文档评论
	 * */
	@RequestMapping("oa/Doc/review.action")
	@ResponseBody
	public Map<String, Object> review(String caller, String formStore) {
		documentListService.review(formStore, caller);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 移动文件或移动文件夹
	 * */
	@RequestMapping("oa/doc/moveDoc.action")
	@ResponseBody
	public Map<String, Object> moveDoc(String data, int folderId) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		documentListService.moveDoc(data, folderId);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 设置关联文档
	 * */
	@RequestMapping("oa/doc/relateDoc.action")
	@ResponseBody
	public Map<String, Object> relateDoc(String data, String relateCode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		documentListService.relateDoc(data, relateCode);
		modelMap.put("success", true);
		return modelMap;
	}

	// dyl
	@RequestMapping("/doc/read.action")
	@ResponseBody
	public void read(HttpServletResponse response, HttpServletRequest request, String fileName, int folderId) {
		String path = request.getParameter("path");
		InputStream in = null;
		try {
			if (path.startsWith("http:")||path.startsWith("https:")) {
				in = HttpUtil.download(path);
			}else if(!path.startsWith("B2B://")){
				File file = new File(path);
				in = new FileInputStream(file);
			}
			OutputStream os = response.getOutputStream();
			response.setContentType("application/pdf");
			byte[] data = new byte[1024];
			while ((in.read(data)) != -1) {
				os.write(data);
			}
			os.flush();
			in.close();
			os.close();
		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	@RequestMapping("oa/doc/getHtml.action")
	@ResponseBody
	public Map<String, Object> getHtml(HttpServletRequest request, int folderId) throws Exception {
		String path = request.getParameter("path");
		String type = request.getParameter("type");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String newPath = "";
		if ("doc".equals(type) || "docx".equals(type)) {
			newPath = WordToHtml.getWord(path, null, null);
		}
		if ("xls".equals(type) || "xlsx".equals(type)) {
			newPath = ExcelToHtml.getHtml(path, null);
		}
		modelMap.put("success", true);
		modelMap.put("newPath", newPath);
		return modelMap;
	}
	
	@RequestMapping("/doc/readPng.action")
	@ResponseBody
	public void readPng(HttpServletResponse response, HttpServletRequest request, String fileName, int folderId, String type) {
		String path = request.getParameter("path");
		try {
			File file = new File(path);
			InputStream in = new FileInputStream(file);
			OutputStream os = response.getOutputStream();
			if("png"==type) {
				response.setContentType("application/png");
			}else if("jpg"==type){
				response.setContentType("application/jpg");
			}
			byte[] data = new byte[1024];
			while ((in.read(data)) != -1) {
				os.write(data);
			}
			os.flush();
			in.close();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/doc/searchTree.action")
	@ResponseBody
	public Map<String, Object> search(String condition){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Set<DocumentList> list = documentListService.getFilesBySearch(condition);
		modelMap.put("fileList", list);
		return modelMap;
	}
	
	@RequestMapping("oa/doc/checkPower.action")
	@ResponseBody
	public boolean checkPower(int folderId, String type){
		boolean hasPower = documentPowerService.CheckPowerByFolderId(folderId, "dp_control");
		if(hasPower){
			return hasPower;
		}
		hasPower = documentPowerService.CheckPowerByFolderId(folderId, type);
		return hasPower;
	}
	
	@RequestMapping("oa/doc/getDocLog.action")
	@ResponseBody
	public List<Map<String, Object>> getDocLog(int docId){
		return documentListService.getDocLog(docId);
	}
	
}
