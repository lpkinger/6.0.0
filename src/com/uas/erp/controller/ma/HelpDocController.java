package com.uas.erp.controller.ma;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.PathUtil;
import com.uas.erp.model.Employee;
import com.uas.erp.model.FileUpload;
import com.uas.erp.service.common.FilePathService;
import com.uas.erp.service.ma.HelpDocService;

@Controller
@RequestMapping("ma/help/")
public class HelpDocController {
	@Autowired
	private HelpDocService helpDocService;
    @Autowired
    private FilePathService filePathService;
	@RequestMapping(value="saveHelpDoc.action",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> saveHelpDoc(HttpSession session,String data){
		Map<String,Object> map=new HashMap<String, Object>();
		helpDocService.saveDoc(data);
		map.put("success",true);
		return map;
	}
	@RequestMapping(value="getHelpInfo.action",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getHelpInfo(HttpSession session,String caller){
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("data",helpDocService.getHelpInfo(caller));
		map.put("success", true);
		return map;
	}
	@RequestMapping(value="getDocLogs.action",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getDocLogs(HttpSession session,String caller){
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("logs",helpDocService.getUpdateLogs(caller));
		map.put("success", true);
		return map;
	}
	@RequestMapping(value="scan.action",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getScanPath(HttpSession session,String caller){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String path = helpDocService.getHelpDoc(caller);
		modelMap.put("success", true);
		modelMap.put("path", path);
		return modelMap;
	}
	@SuppressWarnings("resource")
	@RequestMapping(value="upload.action")
	@ResponseBody
	public String upload(HttpSession session, FileUpload uploadItem) {
		try {
			String filename = uploadItem.getFile().getOriginalFilename();
			long size = uploadItem.getFile().getSize();
			if (size > 104857600) {
				return "{error: '文件过大'}";
			}
			String path = getFilePath(filename);
			File file = new File(path);
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
			return "{success: true, filepath: " + id + ",size: " + size + ",path:\"" + path + "\"}";
		} catch (Exception e) {
			e.printStackTrace();
			return "{error: '文件过大,上传失败'}";
		}
	}

	/**
	 * 文件实际存放的硬盘路径
	 * 
	 * @param fileName
	 * @return
	 */
	private String getFilePath(String fileName) {
		String uuid = UUID.randomUUID().toString().replaceAll("\\-", "");
		String suffix = fileName.indexOf(".") != -1 ? fileName.substring(fileName.lastIndexOf("."), fileName.length())
				: "";
		String path = PathUtil.getHelpPath();
		File file = new File(path);
		if (!file.isDirectory()) {
			file.mkdir();
			new File(path).mkdir();
		} else {
			file = new File(path);
			if (!file.isDirectory()) {
				file.mkdir();
			}
		}
		return path + File.separator + uuid + suffix;
	}
	/**
	 * 用户下载帮助文档
	 * */
	@RequestMapping(value="download.action")
	@ResponseBody
	public void download(HttpSession session){
		helpDocService.download();
	}

}
