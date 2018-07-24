package com.uas.api.serve.controller;

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
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.uas.api.serve.service.ServeCommonService;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.PathUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.common.FilePathService;
import com.uas.erp.service.common.FormAttachService;
import com.uas.erp.service.common.ProcessService;
import com.uas.mobile.model.ListView;
import com.uas.mobile.service.AddrBookService;
import com.uas.mobile.service.ListService;
import com.uas.mobile.service.OAMeetingService;
import com.uas.mobile.service.PanelService;

/**
 * 产城API通用接口的Controller
 * 
 * @author chenrh
 * @since 2017年11月14日 13:39:10
 */

@Controller
public class ServeCommonController {
     
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private ListService listService;
	
	@Autowired
	private AddrBookService addrBookService;
	
	@Autowired
	private OAMeetingService oaMeetingService;
	
	@Autowired
	private FilePathService filePathService;
	
	@Autowired
	private PanelService panelService;
	
	@Autowired
	private ProcessService processService;
	
	@Autowired
	private ServeCommonService serveCommonService;
	
	@Autowired
	private FormAttachService formAttachService;
	
	/**
	 * 获取默认服务信息
	 */
	@RequestMapping("/api/serve/getDefaultServices.action")  
	@ResponseBody 
	public Map<String, Object> getDefaultServices(HttpServletRequest request,String kind) {
		String basePath = BaseUtil.getBasePath(request);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("serves", serveCommonService.getDefaultServices(basePath, kind));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改默认服务
	 */
	@RequestMapping("/api/serve/setDefaultServices.action")  
	@ResponseBody 
	public Map<String, Object> setDefaultServices(HttpSession session,String kind, String ids) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		serveCommonService.setDefaultServices(kind,ids);
		modelMap.put("success", true);
		return modelMap;
	}

	
	/**
	 * 获取服务配置
	 */
	@RequestMapping("/api/serve/config.action")  
	@ResponseBody 
	public Map<String, Object> getConfig(HttpSession session, Long serve_id, String operate_type, String condition, String id, String config) {
		
		Map<String,Object> map=new HashMap<String, Object>(); 
		boolean isUpdate = StringUtil.hasText(id)&&!"0".equals(id);
		if(isUpdate){
			operate_type =operate_type==null?"Update":operate_type;
		}else {
			operate_type =operate_type==null?"Add":operate_type;
		}
		Object [] serviceDet = baseDao.getFieldsDataByCondition("ServiceDet", new String[]{"sd_caller","sd_url","sd_id"}, "sd_svid = "+serve_id+" and sd_operate = '"+operate_type+"'");
		String caller = null;
		if (serviceDet==null) {
			if (isUpdate) {
				serviceDet = baseDao.getFieldsDataByCondition("ServiceDet", new String[]{"sd_caller","sd_url","sd_id"}, "sd_svid = "+serve_id+" and sd_operate = 'Add'");
			}
			if (serviceDet==null) {
				BaseUtil.showError("没有这个操作，请联系管理员！");
			}
		}else{
			caller = String.valueOf(serviceDet[0]);
		}
		if (!StringUtil.hasText(caller)) {
			BaseUtil.showError("未绑定配置，请联系管理员！");
		}else{
			String keyField = baseDao.queryForObject("select fo_keyField from form where fo_caller = ?", String.class, caller);
			if (StringUtil.hasText(keyField)) {
				config=config==null?"0":config;
				if (isUpdate) {
					map.put("datas", listService.getFormAndGridData(caller, id,"1",config,session));
				}else{
					condition = condition==null?"1=1":condition;
					map.put("data", panelService.getFormAndGridDetail(caller,condition,"1",config,session));
					String desc = baseDao.queryForObject("select sv_desc from service where sv_id = ?", String.class, serve_id);
					map.put("servedesc", desc);
				}
				map.put("operateid", serviceDet[2]);
				map.put("operateurl", serviceDet[1]);
				map.put("caller", caller);
				map.put("keyField", keyField);
				map.put("success", true);
			}else{
				BaseUtil.showError("主键未配置，请联系管理员！");
			}
		}
    	return map;
	}
	
	/**
	 * 通用保存接口
	 */
	@RequestMapping("/api/serve/save.action")  
	@ResponseBody 
	public Map<String, Object> save(HttpSession session,HttpServletRequest request, String data, Long operate_id) {
		Employee employee=(Employee)request.getSession().getAttribute("employee");
		if(employee==null) BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();	
		String caller = baseDao.queryForObject("select sv_caller from ServiceDet,Service where sd_svid = sv_id and sd_id = ?", String.class, operate_id);
		if (StringUtil.hasText(caller)) {
			modelMap = oaMeetingService.commonSaveAndSubmit(caller,data,null,employee.getEm_code(),employee.getEm_name());
			modelMap.put("success", true);
		}else{
			BaseUtil.showError("未绑定配置，请联系管理员！");
		}
		return modelMap;
	}
	
	/**
	 * 通用更新接口
	 */
	@RequestMapping("/api/serve/update.action")  
	@ResponseBody 
	public Map<String, Object> update(HttpSession session, String data, Integer keyid, Long operate_id) {
		Employee employee = (Employee)session.getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String caller = baseDao.queryForObject("select sd_caller from ServiceDet where sd_id = ?", String.class, operate_id);
		if (StringUtil.hasText(caller)) {
			addrBookService.commonUpdate(caller, data, null, keyid);
			modelMap.put("success", true);
		}else{
			BaseUtil.showError("未绑定配置，请联系管理员！");
		}
		return modelMap;
	}
	
	/**
	 * 通用查询接口
	 */
	@RequestMapping("/api/serve/query.action")  
	@ResponseBody 
	public Map<String, Object> query(HttpSession session, HttpServletRequest req, Integer page, Integer pageSize, 
			Long serve_id, String condition, Integer _f, Boolean _self, String orderby) {
		Map<String,Object> map=new HashMap<String, Object>(); 
		String caller = baseDao.queryForObject("select sv_caller from Service where sv_id = ?", String.class, serve_id);
		if (!StringUtil.hasText(caller)) {
			BaseUtil.showError("未绑定配置，请联系管理员！");
		}else{
			condition = condition==null?"":condition;
			Boolean self = (Boolean)req.getAttribute("_self");
			self = (_self==null?false:_self)||(self==null?false:self);
			Employee employee=(Employee) session.getAttribute("employee");
			ListView  view = listService.getListGridByCaller(caller, condition, page, pageSize, orderby, self, _f,employee,null);
	        map.put("listdata", view.getListdata());
	        map.put("columns", view.getColumns());
	        map.put("keyField", view.getKeyField());
	        map.put("pfField", view.getPfField());
		}
		return map;
	}
	
	/**
	 * 上传文件
	 * 
	 * @param em_code
	 *            人员编号
	 * @param img
	 * @param master
	 * @return id 上传图片成功后回传给客户端表filepath的fp_id值
	 */
	@RequestMapping("/api/serve/uploadAttach.action")
	@ResponseBody
	public Map<String, Object> uploadAttach(HttpServletRequest request,MultipartFile file) {
		Employee employee = (Employee) request.getSession().getAttribute("employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
			
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", upload(employee, file));
		modelMap.put("success", true);
		return modelMap;
	}

	private Integer upload(Employee employee, MultipartFile img) {
		String filename = img.getOriginalFilename();
		long size = img.getSize();
		if (size > 104857600) {
			BaseUtil.showError("文件过大!");
		}
		String path = getFilePath(filename, employee.getEm_code());
		File file = new File(path);
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(file));
			bis = new BufferedInputStream(img.getInputStream());
			int c;
			while ((c = bis.read()) != -1) {
				bos.write(c);
				bos.flush();
			}
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int id = filePathService.saveFilePath(path, (int) size, filename, employee);
		return id;
	}

	/**
	 * 文件实际存放的硬盘路径
	 * 
	 * @param fileName
	 * @return
	 */
	private String getFilePath(String fileName, String em_code) {
		String uuid = UUID.randomUUID().toString().replaceAll("\\-", "");
		String suffix = fileName.indexOf(".") != -1 ? fileName.substring(
				fileName.lastIndexOf("."), fileName.length()) : "";
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
		return path + File.separator + uuid + suffix;
	}
	
	@RequestMapping(value = "/api/serve/getFilePaths.action")
	@ResponseBody
	public Map<String, Object> getFilePaths(HttpSession session, String id, String field) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("files", formAttachService.getFiles(id));
		modelMap.put("success", true);
		return modelMap;
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
	@RequestMapping("/api/serve/download.action")
	public void download(HttpServletResponse response, HttpServletRequest request, String fileName) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		String path = request.getParameter("path");
		String escape = request.getParameter("escape");
		String size = request.getParameter("size");
		if (!"1".equals(escape)) {
			path = new String(path.getBytes("iso-8859-1"), "utf-8");
		}
		InputStream in = null;
		File file = null;
		if (path.startsWith("http:")||path.startsWith("https:")) {
			in = HttpUtil.download(path);
		}else if (path.startsWith("ftp:") || path.startsWith("sftp:")) {
			// 存放在其他网络资源中，直接跳转至链接地址
			response.sendRedirect(path);
			return;
		}else if (path.startsWith("B2B://")) {// 文件在云平台
			path = SystemSession.getUser().getCurrentMaster().getMa_b2bwebsite() + "/" + path.substring(6);
			in = HttpUtil.download(path);
		} else {
			file = new File(path);
			in = new FileInputStream(file);
		}
		if (size==null) {
			size = String.valueOf(file.length());
		}
		OutputStream os = response.getOutputStream();
		if (fileName == null && file != null)
			fileName = new String(file.getName().getBytes("utf-8"), "iso-8859-1");
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
	
	@RequestMapping("/api/serve/downloadbyId.action")
	public void downloadById(HttpServletResponse response, HttpServletRequest request) throws IOException, KeyManagementException, NoSuchAlgorithmException {
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
	
	
	//获取流程节点信息
	@RequestMapping("/api/serve/getProcesses.action")
	@ResponseBody
	public Map<String, Object> getProcesses(HttpServletRequest request, Long serve_id, Integer id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("process", serveCommonService.getProcesses(serve_id, id));
		modelMap.put("success", true);
		return modelMap;
	}
	
	//获取流程处理人信息
	@RequestMapping("/api/serve/getMultiNodeAssigns.action")
	@ResponseBody
	public Map<String, Object> hasMoreAssigns(String caller, int id) {
		return processService.getMultiNodeAssigns(caller, id);
	}
	
	//激活节点任务
	@RequestMapping("/api/serve/takeOverTask.action")
	@ResponseBody
	public Map<String, Object> takeOverTask(HttpSession session, String em_code, String nodeId,String params,boolean needreturn) {
		Map<String, Object> map = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		if(params!=null & params!=""){
			List<Map<Object, Object>> pstore = BaseUtil.parseGridStoreToMaps(params);
			for (Map<Object, Object> store : pstore) {			
				processService.takeOverTask(String.valueOf(store.get("em_code")),String.valueOf(store.get("nodeId")), employee,needreturn);
			}
		}
		else {processService.takeOverTask(em_code, nodeId, employee,needreturn);}
		map.put("success", true);
		return map;
	}

}
