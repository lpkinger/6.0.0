package com.uas.erp.controller.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FTPUtil;
import com.uas.erp.core.FileUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.PowerDao;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.Employee;
import com.uas.erp.model.EmpsJobs;
import com.uas.erp.model.FileUpload;
import com.uas.erp.model.FileUploadTemp;
import com.uas.erp.model.GridPanel;
import com.uas.erp.model.PersonalPower;
import com.uas.erp.model.PositionPower;
import com.uas.erp.service.common.FilePathService;
import com.uas.erp.service.common.FileUploadTempService;
import com.uas.erp.service.common.SingleGridPanelService;
import com.uas.erp.service.common.SysnavigationService;

import net.sf.json.JSONObject;

@Controller
public class UploadController {
	@Autowired
	private FilePathService filePathService;
	@Autowired
	private FileUploadTempService fileUploadTempService;
	@Autowired
	private SingleGridPanelService singleGridPanelService;
	@Autowired
	private SysnavigationService sysnavigationService;
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private PowerDao powerDao;
	
	/**
	 * 根据批次号获取临时文件上传信息
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/common/getGridData.action")
	public List<FileUploadTemp> getGridData(int id) {
		return fileUploadTempService.getGridData(id);
	}
	
	/**
	 * 一键匹配，调用存储过程uploadfilematch;
	 */
	@ResponseBody
	@RequestMapping(value="/common/doMatchData.action",method=RequestMethod.GET)
	public Map<String, Object> doMatchData(String datatype,int id) {
		Map<String, Object> modelMap = new HashMap<String,Object>();
		if (!"".equals(datatype)&& datatype!=null ) {
			fileUploadTempService.doMatchData(datatype, id);
			modelMap.put("success", true);
		}else {
			modelMap.put("success", false);
		}
		return modelMap;
	}
	
	/**
	 * 在文件上传之前,预览上传文件信息
	 * @return 
	 */
	@ResponseBody
	@RequestMapping(value="/common/putGridData.action",method=RequestMethod.POST)
	public Map<String, Object> putGridData(String data) {
		Map<String, Object> modelMap = new HashMap<String,Object>();
		if (!"".equals(data)) {
			modelMap.put("success", true);
			modelMap.put("id", fileUploadTempService.putGridData(data));
		}else {
			modelMap.put("success", false);
		}
		return modelMap;
		
	}
	/**
	 * 解决额外上传100m附件无法更新临时表条件，
	 */
	@ResponseBody
	@RequestMapping(value="/common/extraUploadFail.action",method=RequestMethod.GET)
	public void extraUploadFail(String filename,int id) {
		String condition = "fl_name="+"'"+filename+"'"+" AND "+"fl_id="+id;
		fileUploadTempService.updatefileUploadTemp("fl_uploadres='上传失败' , fl_uploadstatus=0", condition);
	}
	
	/**
	 * 检查是否有上传文件的权限
	 * @param session
	 * @param caller
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/common/checkUploadPower.action",method=RequestMethod.GET)
	public Map<String, Object> checkUploadPower(HttpSession session,String caller) {
		Map<String, Object> modelMap = new HashMap<String,Object>();
		Employee employee=(Employee)session.getAttribute("employee");
		if (!"admin".equals(employee.getEm_type())) {
			boolean bool = checkJobPower(caller, employee);
			if (!bool) {
				// 查看是否有个人权限 true:表示有权限，false表示没权限
				bool = powerDao.getSelfPowerByType(caller, PersonalPower.SAVE, employee);
				if (!bool) {
					modelMap.put("success", false);
					return modelMap;
				}
			}
		}
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 多文件上传
	 */
	@ResponseBody
	@RequestMapping(value="/common/uploadMulti.action",method=RequestMethod.POST)
	public Map<String, Object> uploadMulti(HttpSession session,@RequestParam("file") MultipartFile file,
			String em_code,int id,String datatype) {
		Map<String, Object> modelMap = new HashMap<String,Object>();
		modelMap.put("success", fileUploadTempService.uploadMulti(session, file, em_code,id, datatype));
		return modelMap;
	}
	
	
	
	/**
	 * 检查权限
	 * @param session
	 * @param caller
	 * @return
	 */
	public Boolean checkPower(HttpSession session,String caller){
		 //20170331 zyc 文档管理界面上传不受caller管控
		if(caller!=null&&!"".equals(caller)&&!"DOCManage".equals(caller)&&!"JProcess".equals(caller)){
			Employee employee=(Employee)session.getAttribute("employee");
			if (!"admin".equals(employee.getEm_type())) {
				boolean bool = checkJobPower(caller, employee);
				if (!bool) {
					// 查看是否有个人权限 true:表示有权限，false表示没权限
					bool = powerDao.getSelfPowerByType(caller, PersonalPower.ADD, employee);
					if (!bool) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	
	/**
	 * 文件上传,支持多文件
	 */
	@ResponseBody
	@RequestMapping("/common/uploadFiles.action")
	public String uploadFiles(HttpSession session, String em_code,String caller,@RequestParam("file") MultipartFile[] files) {
		Map<String, Object> modelMap = new HashMap<String,Object>();
		modelMap.put("success", true);
		if (!checkPower(session,caller)) {
			modelMap.put("noPower", true);
			return JSONArray.toJSONString(modelMap);
		}
		modelMap.put("data", fileUploadTempService.uploadFiles(session,files, em_code,caller));
		return JSONArray.toJSONString(modelMap);
	}
	
	/**
	 * 文件上传,兼容旧的前台代码
	 */
	@ResponseBody
	@RequestMapping("/common/upload.action")
	public String upload(HttpSession session, String em_code,String caller, FileUpload uploadItem) {
		 try {
			 //20170331 zyc 文档管理界面上传不受caller管控
			if(caller!=null&&!"".equals(caller)&&!"DOCManage".equals(caller)&&!"JProcess".equals(caller)){
				Employee employee=(Employee)session.getAttribute("employee");
				if (!"admin".equals(employee.getEm_type())) {
					boolean bool = checkJobPower(caller, employee);
					if (!bool) {
						// 查看是否有个人权限 true:表示有权限，false表示没权限
						bool = powerDao.getSelfPowerByType(caller, PersonalPower.ADD, employee);
						if (!bool) {
							return new String(
									"{success: true,error:'您没有<上传>附件的权限!'}"
											.getBytes("utf-8"),
									"iso8859-1");
						
						}
					}
				}
			}			
			String filename = uploadItem.getFile().getOriginalFilename();
			long size = uploadItem.getFile().getSize();
			if (size > 104857600) {
				return "{error: '文件过大'}";
			}
			String path = FileUtil.saveFile(uploadItem.getFile(), em_code);
			int id = filePathService.saveFilePath(path, (int) size, filename, (Employee) session.getAttribute("employee"));
			return "{success: true, filepath: " + id + ",size: " + size + ",path:\"" + path + "\"}";
						
		} catch (Exception e) {
			e.printStackTrace();
			return "{error: '文件过大,上传失败'}";
		}
	}
	
	/**
	 * 文件上传   BOM模具加工方式
	 */
	@ResponseBody
	@RequestMapping("/common/uploadfile.action")
	public String uploadfile(HttpSession session, String em_code,String caller, @RequestParam("file") MultipartFile file) {
		 try {	
			String filename = file.getOriginalFilename();
			long size = file.getSize();
			if (size > 104857600) {
				return "{error: '文件过大'}";
			}
			String path = FileUtil.saveFile(file, em_code);
			int id = filePathService.saveFilePath(path, (int) size, filename, (Employee) session.getAttribute("employee"));
			return "{success: true, filepath: " + id + ",size: " + size + ",path:\"" + path + "\"}";
		} catch (Exception e) {
			e.printStackTrace();
			return "{error: '文件过大,上传失败'}";
		}
	}
	
	//按照组织判断权限
	private boolean checkJobPower(String caller, Employee employee) {
			String sob = employee.getEm_master();
			// 默认岗位设置
			boolean bool = powerDao.getPowerByType(caller, PositionPower.SAVE, sob, employee.getEm_defaulthsid());
			if (!bool && employee.getEmpsJobs() != null) {
				// 按员工岗位关系取查找权限
				for (EmpsJobs empsJob : employee.getEmpsJobs()) {
					bool = powerDao.getPowerByType(caller, PositionPower.SAVE, sob, empsJob.getJob_id());
					if (bool)
						break;
				}
			}
			return bool;
		}

	/**
	 * 报表文件上传
	 */
	@SuppressWarnings("resource")
	@RequestMapping("/common/uploadRP.action")
	public @ResponseBody String uploadRP(HttpSession session, String em_code, FileUpload uploadItem) {
		try {
			String filename = uploadItem.getFile().getOriginalFilename();
			long size = uploadItem.getFile().getSize();
			if (size > 104857600) {
				return "{error: '文件过大'}";
			}
			String path = "/usr/rpts/" + filename;
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
			}
			int id = filePathService.saveFilePath(path, (int) size, filename, (Employee) session.getAttribute("employee"));
			return "{success: true, filepath: " + id + ",size: " + size + ",path:\"" + path + "\"}";
		} catch (Exception e) {
			return "{error: '文件过大,上传失败'}";
		}
	}

	/**
	 * excel导入 poi方法 注意：用【导出】功能导出的xls文件，必须先另存为，然后才能正确导入 ******************************************* txt导入
	 */
	@RequestMapping("/common/upexcel.action")
	public @ResponseBody String upexcel(FileUpload uploadItem) {
		InputStream is = null;
		try {
			CommonsMultipartFile file = uploadItem.getFile();
			long size = file.getSize();
			if (size > 104857600) {
				return new String(("{error: '文件过大', size:" + size + "}").getBytes("utf-8"), "iso8859-1");
			} else {
				String ft = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
				is = uploadItem.getFile().getInputStream();
				GridPanel gridPanel = null;
				if (ft.equals("xls")) {
					HSSFWorkbook wbs = new HSSFWorkbook(is);
					HSSFSheet sheet = wbs.getSheetAt(0);
					gridPanel = new GridPanel(sheet);
				} else if (ft.equals("txt")) {
					BufferedReader br = new BufferedReader(new InputStreamReader(is));
					gridPanel = new GridPanel(br);
					br.close();
				}else if(ft.equals("xlsx")){
					Workbook wb = new XSSFWorkbook(is);
					Sheet sheet = wb.getSheetAt(0);
					gridPanel = new GridPanel(sheet);
				}else {
					return new String("{error: 'excel文件的格式不太规范,导入失败<hr>可以尝试将文件另存为.xls,然后导入'}".getBytes("utf-8"), "iso8859-1");
				}
				String dataString = gridPanel.getDataString().replaceAll("<", "&lt;").replaceAll(">", "&gt;");
				gridPanel.setDataString(dataString);
				JSONObject jsonObject = JSONObject.fromObject(gridPanel);
				String r = "{success: true, grid:" + jsonObject.toString() + "}";
				return new String(r.getBytes("utf-8"), "iso8859-1");
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				return new String("{error: 'excel文件的格式不太规范,导入失败<hr>可以尝试将文件另存为.xls,然后导入'}".getBytes("utf-8"), "iso8859-1");
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

	/**
	 * 图片上传
	 */
	@RequestMapping("/common/uploadPic.action")
	public @ResponseBody String uploadPic(HttpSession session, String em_code,String caller, FileUpload uploadItem) {
		return upload(session,em_code,caller,uploadItem);
	}

	/** 明细行直接插入EXCEL */
	@RequestMapping("/common/insertByExcel.action")
	@ResponseBody
	public String insertByExcel(FileUpload uploadItem, String caller, int keyValue) {
		InputStream is = null;
		try {
			CommonsMultipartFile file = uploadItem.getFile();
			long size = file.getSize();
			if (size > 104857600) {
				return new String(("{error: '文件过大', size:" + size + "}").getBytes("utf-8"), "iso8859-1");
			} else {
				String ft = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
				is = uploadItem.getFile().getInputStream();
				GridPanel gridPanel = null;
				List<DetailGrid> details = singleGridPanelService.getDetailsByCaller(caller);
				if (ft.equals("xls")) {
					HSSFWorkbook wbs = new HSSFWorkbook(is);
					HSSFSheet sheet = wbs.getSheetAt(0);
					gridPanel = new GridPanel(sheet, details, keyValue);
				} else if (ft.equals("txt")) {
					BufferedReader br = new BufferedReader(new InputStreamReader(is));
					gridPanel = new GridPanel(br);
					br.close();
				}else if(ft.equals("xlsx")){
					Workbook wb = new XSSFWorkbook(is);
					Sheet sheet = wb.getSheetAt(0);
					gridPanel = new GridPanel(sheet,details,keyValue);
				} else {
					return new String("{error: 'excel文件的格式不太规范,导入失败<hr>可以尝试将文件另存为.xls,然后导入'}".getBytes("utf-8"), "iso8859-1");
				}
				singleGridPanelService.batchSave(caller, JSONObject.fromObject(gridPanel).get("jsonList").toString(), keyValue);
				return "{success: true}";
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				return new String("{error: 'excel文件的格式不太规范,导入失败<hr>可以尝试将文件另存为.xls,然后导入'}".getBytes("utf-8"), "iso8859-1");
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
	/** 明细行直接插入EXCEL 先删除原有数据*/
	@RequestMapping("/common/directInsertByExcel.action")
	@ResponseBody
	public String directInsertByExcel(FileUpload uploadItem, String caller, int keyValue) {
		singleGridPanelService.deletegridbycaller(caller,keyValue);
		return insertByExcel(uploadItem,caller,keyValue);
		
	}
	
	/**
	 * 文件上传
	 */
	@RequestMapping("/common/uploadPageinstruction.action")
	public @ResponseBody String uploadPageinstruction(HttpSession session,String caller,String field, FileUpload uploadItem,int id) {
		try {
			long size = uploadItem.getFile().getSize();
			if (size > 104857600) {
				return "{error: '文件过大'}";
			}
			String path = FileUtil.saveFile(uploadItem.getFile(),SystemSession.getUser().getEm_code());
			sysnavigationService.savePageinstruction(caller,field,path,id);
			return "{success: true,path:\"" + path + "\"}";
		} catch (Exception e) {
			return "{error: '文件过大,上传失败'}";
		}
	}
	
	/**
	 * 文件上传FTP
	 */
	@ResponseBody
	@RequestMapping("/common/uploadFTP.action")
	public String uploadFTP(HttpSession session, String em_code, FileUpload uploadItem, String sf_id, String sfd_id) {
		try {
			String filename = uploadItem.getFile().getOriginalFilename();
			long size = uploadItem.getFile().getSize();
			Object[] obj = baseDao.getFieldsDataByCondition("SYS_FTP", new String[]{"sf_ip","sf_port","sf_username","sf_password","sf_defaultpath"}, "sf_id="+sf_id);
			String ip = (String) obj[0];
			int port =  Integer.parseInt(obj[1].toString());
			String username = (String) obj[2];
			String password = obj[3]==null?"":(String)obj[3];
			String path = (String)obj[4];
			if(!"".equals(sfd_id)&&sfd_id!=null){
				SqlRowList rs = baseDao.queryForRowSet("select wm_concat(sfd_path) sfd_path from sys_ftpdetail start with sfd_id=? connect by prior sfd_parentid=sfd_id",sfd_id);
				if(rs.next()){
					String[] sfd_path = rs.getString("sfd_path").split(",");
					Collections.reverse(Arrays.asList(sfd_path));
					path=StringUtils.join(sfd_path, "");
				}
			}
			String uuid = UUID.randomUUID().toString().replaceAll("\\-", "");
			String suffix = filename.indexOf(".") != -1 ? filename.substring(filename.lastIndexOf("."), filename.length()) : "";
			String filenameftp= uuid + suffix;
			boolean ftp =  true;
			if(size < 10240){
				InputStream i = uploadItem.getFile().getInputStream();
				ftp = FTPUtil.uploadFile(ip, port, username, password, path, filenameftp, i);
			}else{
				DiskFileItem fi = (DiskFileItem)uploadItem.getFile().getFileItem(); 
			    File f = fi.getStoreLocation();
				InputStream input = new FileInputStream(f);
				ftp = FTPUtil.uploadFile(ip, port, username, password, path, filenameftp, input);
			}
			if(!ftp){
				return new String(
						"{success: true,error: '文件上传失败!'}"
						.getBytes("utf-8"),
				"iso8859-1");
			}
			path=path+filenameftp;
			int id = filePathService.saveFilePath(path, (int) size, filename, (Employee) session.getAttribute("employee"));
			return "{success: true, filepath: " + id + ",size: " + size + ",path:\"" + path + "\"}";
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if("连接FTP服务器失败!".equals(e.getMessage())){
					return new String(
							"{success: true,error: '连接FTP服务器失败!'}"
									.getBytes("utf-8"),
							"iso8859-1");
				}else if("FTP服务器切换目录失败!".equals(e.getMessage())){
					return new String(
							"{success: true,error: 'FTP服务器切换目录失败!'}"
									.getBytes("utf-8"),
							"iso8859-1");
				}else if("文件上传失败!".equals(e.getMessage())){
					return new String(
							"{success: true,error: '文件上传失败!'}"
									.getBytes("utf-8"),
							"iso8859-1");
				}else{
					return new String(
							"{success: true,error: '上传失败!'}"
									.getBytes("utf-8"),
							"iso8859-1");
				}
			}catch(UnsupportedEncodingException e1) {
				return "{success: false}";
			}
		}
	}
	 
	@RequestMapping("/common/getFTPData.action")
	@ResponseBody
	public Map<String, Object> getFTPData() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", filePathService.getData());
		modelMap.put("success", true);
		return modelMap;
	}
	 
	@RequestMapping("/common/getFTPDetailData.action")
	@ResponseBody
	public Map<String, Object> getFTPDetailData(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", filePathService.getDetailData(id));
		modelMap.put("success", true);
		return modelMap;
	}
}
