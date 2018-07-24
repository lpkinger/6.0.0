package com.uas.erp.controller.wisdomPark;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FileUtil;
import com.uas.erp.core.PathUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.service.wisdomPark.NewsCenterService;

@Controller
public class NewsCenterController {
	
	
	@Autowired
	private NewsCenterService newsCenterService;
	
	@Autowired BaseDao baseDao;
	
	private String getError(String message) {
		JSONObject obj = new JSONObject();
		String result = "";
		obj.put("error", 1);
		obj.put("message", message);
		try {
			result = new String(obj.toJSONString().getBytes("utf-8"),"iso8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public class NameComparator implements Comparator<Hashtable<String, Object>> {
		public int compare(Hashtable<String, Object> a, Hashtable<String, Object> b) {
			if (((Boolean)a.get("is_dir")) && !((Boolean)b.get("is_dir"))) {
				return -1;
			} else if (!((Boolean)a.get("is_dir")) && ((Boolean)a.get("is_dir"))) {
				return 1;
			} else {
				return ((String)a.get("filename")).compareTo((String)b.get("filename"));
			}
		}
	}
	
	public class SizeComparator implements Comparator<Hashtable<String, Object>> {
		public int compare(Hashtable<String, Object> a, Hashtable<String, Object> b) {
			if (((Boolean)a.get("is_dir")) && !((Boolean)b.get("is_dir"))) {
				return -1;
			} else if (!((Boolean)a.get("is_dir")) && ((Boolean)b.get("is_dir"))) {
				return 1;
			} else {
				if (((Long)a.get("filesize")) > ((Long)b.get("filesize"))) {
					return 1;
				} else if (((Long)a.get("filesize")) < ((Long)b.get("filesize"))) {
					return -1;
				} else {
					return 0;
				}
			}
		}
	}
	
	public class TypeComparator implements Comparator<Hashtable<String, Object>> {
		public int compare(Hashtable<String, Object> a, Hashtable<String, Object> b) {
			if (((Boolean)a.get("is_dir")) && !((Boolean)b.get("is_dir"))) {
				return -1;
			} else if (!((Boolean)a.get("is_dir")) && ((Boolean)b.get("is_dir"))) {
				return 1;
			} else {
				return ((String)a.get("filetype")).compareTo((String)b.get("filetype"));
			}
		}
	}
	
	@RequestMapping("/wisdomPark/file/upload.action")
	@ResponseBody
	public String upload(HttpServletRequest request, HttpServletResponse response, @RequestParam("imgFile") MultipartFile[] files, String dir){		
		
		//文件保存目录路径
		String savePath = "attached" + File.separator;

		response.setContentType("text/html; charset=UTF-8");
		
		//最大文件大小
		long maxSize = 1000000;
		
		try{
			//定义允许上传的文件扩展名
			HashMap<String, String> extMap = new HashMap<String, String>();
			extMap.put("image", "gif,jpg,jpeg,png,bmp");
			extMap.put("flash", "swf,flv");
			extMap.put("media", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
			extMap.put("file", "doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2");
	
			if(files.length==0){
				return getError("请选择文件!");
			}
			
			if (dir == null) {
				dir = "image";
			}
			if(!extMap.containsKey(dir)){
				return getError("目录名不正确。");
			}
			
			//创建文件夹
			savePath += dir  + File.separator;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String ymd = sdf.format(new Date());
			savePath += ymd;
				
			MultipartFile file = files[0];
			
			//检查文件大小
			if(file.getSize() > maxSize){
				return getError("上传文件大小超过限制。");
			}
			
			String fileName = file.getOriginalFilename();
			
			//检查扩展名
			String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
			if(!Arrays.<String>asList(extMap.get(dir).split(",")).contains(fileExt)){
				return getError("上传文件扩展名是不允许的扩展名。\n只允许" + extMap.get(dir) + "格式。");
			}
			
			String path = FileUtil.saveFile(file, savePath);
			JSONObject obj = new JSONObject();
			obj.put("error", 0);
			if (!(path.startsWith("http:") || path.startsWith("https:") || path.startsWith("B2B://"))) {
				String outUrl = baseDao.getFieldValue("Enterprise", "en_erpurl", "1=1",String.class);
				String baseUrl = "";
				if(StringUtil.hasText(outUrl)&&(outUrl.startsWith("http://")||outUrl.startsWith("https://"))){
					baseUrl = outUrl + (outUrl.endsWith("/")?"":"/");
				}else{
					baseUrl = BaseUtil.getBasePath(request);
				}
				String rootPath = PathUtil.getFilePath() + "postattach" + File.separator + "attached" + File.separator;
				path = path.substring(rootPath.length());
				String sob = SpObserver.getSp();
				obj.put("url", baseUrl + "public/download.action?master="+sob+"?path="+path);
			}else{
				obj.put("url", path);
			}
			
			return new String(obj.toJSONString().getBytes("utf-8"),"iso8859-1");
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			return getError("上传文件失败。");
		}
		return null;	
	}
	
	@RequestMapping("/wisdomPark/file/fileManager.action")
	@ResponseBody
	public String fileManager(HttpServletRequest request, HttpServletResponse response, String path, String dir, String order){		
		//根目录路径，可以指定绝对路径，比如 /var/www/attached/
		String rootPath = PathUtil.getFilePath() + "postattach" + File.separator + "attached" + File.separator;
		//根目录URL，可以指定绝对路径，比如 http://www.yoursite.com/attached/
		String outUrl = baseDao.getFieldValue("Enterprise", "en_erpurl", "1=1",String.class);
		String rootUrl = "";
		if(StringUtil.hasText(outUrl)&&(outUrl.startsWith("http://")||outUrl.startsWith("https://"))){
			rootUrl = outUrl + (outUrl.endsWith("/")?"":"/");
		}else{
			rootUrl = BaseUtil.getBasePath(request);
		}
		String sob = SpObserver.getSp();
		rootUrl  += "public/download.action?master="+sob+"&path=";
		//图片扩展名
		String[] fileTypes = new String[]{"gif", "jpg", "jpeg", "png", "bmp"};
		try {
			if (dir != null) {
				if(!Arrays.<String>asList(new String[]{"image", "flash", "media", "file"}).contains(dir)){
					return "Invalid Directory name.";
					
				}
				rootPath += dir  + File.separator;
				rootUrl += dir  + "/";
				File saveDirFile = new File(rootPath);
				if (!saveDirFile.exists()) {
					saveDirFile.mkdirs();
				}
			}
			//根据path参数，设置各路径和URL
			String currentPath = rootPath + path;
			String currentUrl = rootUrl + path;
			String currentDirPath = path;
			String moveupDirPath = "";
			if (!"".equals(path)) {
				String str = currentDirPath.substring(0, currentDirPath.length() - 1);
				moveupDirPath = str.lastIndexOf(File.separator) >= 0 ? str.substring(0, str.lastIndexOf(File.separator) + 1) : "";
			}
			
			//排序形式，name or size or type
	
			//不允许使用..移动到上一级目录
			if (path.indexOf("..") >= 0) {
				return "Access is not allowed.";
				
			}
			//最后一个字符不是/
			if (!"".equals(path) && !path.endsWith("/")) {
				return "Parameter is not valid.";
				
			}
			//目录不存在或不是目录
			File currentPathFile = new File(currentPath);
			if(!currentPathFile.isDirectory()){
				return "Directory does not exist.";
				
			}
			
			//遍历目录取的文件信息
			List<Hashtable<String, Object>> fileList = new ArrayList<Hashtable<String, Object>>();
			if(currentPathFile.listFiles() != null) {
				for (File file : currentPathFile.listFiles()) {
					Hashtable<String, Object> hash = new Hashtable<String, Object>();
					String fileName = file.getName();
					if(file.isDirectory()) {
						hash.put("is_dir", true);
						hash.put("has_file", (file.listFiles() != null));
						hash.put("filesize", 0L);
						hash.put("is_photo", false);
						hash.put("filetype", "");
					} else if(file.isFile()){
						String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
						hash.put("is_dir", false);
						hash.put("has_file", false);
						hash.put("filesize", file.length());
						hash.put("is_photo", Arrays.<String>asList(fileTypes).contains(fileExt));
						hash.put("filetype", fileExt);
					}
					hash.put("filename", fileName);
					hash.put("datetime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(file.lastModified()));
					fileList.add(hash);
				}
			}
	
			if ("size".equals(order)) {
				Collections.sort(fileList, new SizeComparator());
			} else if ("type".equals(order)) {
				Collections.sort(fileList, new TypeComparator());
			} else {
				Collections.sort(fileList, new NameComparator());
			}
			JSONObject result = new JSONObject();
			result.put("moveup_dir_path", moveupDirPath);
			result.put("current_dir_path", currentDirPath);
			result.put("current_url", currentUrl);
			result.put("total_count", fileList.size());
			result.put("file_list", fileList);
	
			response.setContentType("application/json; charset=UTF-8");
		
			return new String(result.toJSONString().getBytes("utf-8"),"iso8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;	
	}
	
	/**
	 * 文件下载
	 * 
	 * @param path
	 *            文件路径
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	@RequestMapping("/public/download.action")
	@ResponseBody
	public void download(HttpServletResponse response, HttpServletRequest request) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		String path = request.getParameter("path");
		String size = null;
		//path = new String(path.getBytes("iso-8859-1"), "utf-8");
		InputStream in = null;
		File file = null;
		String rootPath = PathUtil.getFilePath() + "postattach" + File.separator + "attached" + File.separator;
		file = new File(rootPath+path);
		in = new FileInputStream(file);
		size = String.valueOf(file.length());
		OutputStream os = response.getOutputStream();
		String fileName = new String(file.getName().getBytes("utf-8"), "iso-8859-1");
		fileName = fileName.replace(",", " ");
		response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
		response.addHeader("Content-Length", size);
		response.setCharacterEncoding("iso-8859-1");
		response.setContentType("application/octec-stream");
		int data = 0;
		while ((data = in.read()) != -1) {
			os.write(data);
		}
		in.close();
		os.close();
	}
	
	//删除新闻类型
	@RequestMapping("/wisdomPark/newsCenter/deleteNewsType.action")
	@ResponseBody
	public Map<String, Object> deleteNewsType(String caller, int id){	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		newsCenterService.deleteNewsType(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//保存新闻
	@RequestMapping("/wisdomPark/newsCenter/saveNews.action")
	@ResponseBody
	public Map<String, Object> saveNews(String caller, String formStore){	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		newsCenterService.saveNews(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
		
	}
	
	//更新新闻
	@RequestMapping("/wisdomPark/newsCenter/updateNews.action")
	@ResponseBody
	public Map<String, Object> updateNews(String caller, String formStore){	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		newsCenterService.updateNews(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//删除新闻
	@RequestMapping("/wisdomPark/newsCenter/deleteNews.action")
	@ResponseBody
	public Map<String, Object> deleteNews(String caller, int id){	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		newsCenterService.deleteNews(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//发布新闻
	@RequestMapping("/wisdomPark/newsCenter/publishNews.action")
	@ResponseBody
	public Map<String, Object> publishNews(String caller, int id){	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		newsCenterService.publishNews(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//撤销新闻
	@RequestMapping("/wisdomPark/newsCenter/cancelNews.action")
	@ResponseBody
	public Map<String, Object> cancelNews(String caller, int id){	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		newsCenterService.cancelNews(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//获取新闻
	@RequestMapping("/public/getNewsHtml.action")
	@ResponseBody
	public Map<String, Object> getNewsHtml(int id){	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("newshtml",newsCenterService.getNewsHtml(id));
		modelMap.put("success", true);
		return modelMap;
	}
}
