package com.uas.erp.controller.common;

import java.io.File;
import java.io.FileInputStream;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.ExcelToHtml;
import com.uas.erp.core.ExportObserve;
import com.uas.erp.core.FTPUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.JSONUtil;
import com.uas.erp.core.PdfUtil;
import com.uas.erp.core.WordToHtml;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.core.web.ExcelViewUtils;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.common.FormAttachService;
import com.uas.erp.service.common.SysnavigationService;
import com.uas.erp.service.oa.DocumentListService;

@Controller
public class DownLoadController {

	@Autowired
	private FormAttachService formAttachService;
	
	@Autowired
	private SysnavigationService sysnavigationService;
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private DocumentListService documentListService;
	
	/**
	 * 文件下载
	 * 
	 * @param path
	 *            文件路径
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	@RequestMapping("/common/download.action")
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

	@RequestMapping("/common/downloadbyId.action")
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
		response.addHeader("Content-Disposition", "attachment;filename=\""
				+ new String(obj.getString("fp_name").getBytes("utf-8"), "iso-8859-1")+"\"");
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
	
	/**
	 * 附件批量下载
	 * @param zipName 压缩包名称，（没有则用第一个文件名当压缩包名称）
	 * @param ids  文件id串，用英文分号;隔开  
	 */
	@RequestMapping(value = "/common/downloadbyIds.action")
	@ResponseBody
	public void BOMAttachDownload(HttpServletResponse response, HttpServletRequest request,String zipName)  throws IOException, KeyManagementException, NoSuchAlgorithmException {
		String ids = request.getParameter("ids");
		documentListService.downloadbyIds(response,ids,zipName);
    }

	/**
	 * 生成excel导出
	 * 
	 * @throws IOException
	 */
	@RequestMapping("/common/excel/create.xls")
	public ModelAndView createExcel(HttpServletRequest request, HttpSession session, String caller, String type, String condition,
			String title, String fields, Integer lg) throws IOException {
		condition = new String(condition.getBytes("ISO-8859-1"), "UTF-8");
		condition = condition.replace("%3D", "=");
		Employee employee = (Employee) session.getAttribute("employee");		
		Object _self = request.getAttribute("_self");
		boolean self = (_self == null ? false : Boolean.parseBoolean(_self.toString()));
		if ("true".equals(request.getParameter("_self")))
			self = true;
		boolean _jobemployee = false;
		if (request.getAttribute("_jobemployee")!=null&&"true".equals(request.getAttribute("_jobemployee").toString())){
			_jobemployee = true;
		}
		return new ModelAndView(ExcelViewUtils.getView(caller, type, condition, title, fields, self, lg, employee,_jobemployee,request));
	}

	/**
	 * 生成excel导出
	 * 
	 * @throws IOException
	 */
	@RequestMapping("/common/excel/grid.xls")
	public ModelAndView createGridExcel(HttpSession session, HttpServletResponse response, HttpServletRequest request, String columns,
			String datas, String title) throws IOException {
		datas = new String(datas.getBytes("ISO-8859-1"), "UTF-8");
		columns = new String(columns.getBytes("ISO-8859-1"), "UTF-8");
		Employee employee = (Employee) session.getAttribute("employee");
		List<Map<String, Object>> colList = FlexJsonUtil.fromJsonArray(columns, HashMap.class);
		List<Map<String, Object>> dataList = FlexJsonUtil.fromJsonArray(datas, HashMap.class);
		return new ModelAndView(ExcelViewUtils.getView(colList, dataList, title, employee));
	}

	/**
	 * 生成pdf导出
	 * 
	 * @throws IOException
	 */
	@RequestMapping("/common/document/grid.pdf")
	public ModelAndView createGridPdf(HttpSession session, HttpServletResponse response, HttpServletRequest request, String columns,
			String datas, String title) throws IOException {
		datas = new String(datas.getBytes("ISO-8859-1"), "UTF-8");
		columns = new String(columns.getBytes("ISO-8859-1"), "UTF-8");
		Employee employee = (Employee) session.getAttribute("employee");
		return new ModelAndView(new PdfUtil(BaseUtil.parseGridStoreToMaps(columns), JSONUtil.toMapList(datas), title, employee));
	}

	/**
	 * 生成excel导出
	 * 
	 * @throws IOException
	 */
	@RequestMapping("/common/excel/gridWithRemark.xls")
	public ModelAndView createGridExcelWithRemark(HttpSession session, HttpServletResponse response, HttpServletRequest request,
			String columns, String datas, String title, String remark) throws IOException {
		datas = new String(datas.getBytes("ISO-8859-1"), "UTF-8");
		columns = new String(columns.getBytes("ISO-8859-1"), "UTF-8");
		remark = new String(remark.getBytes("ISO-8859-1"), "UTF-8");
		Employee employee = (Employee) session.getAttribute("employee");
		List<Map<String, Object>> colList = FlexJsonUtil.fromJsonArray(columns, HashMap.class);
		List<Map<String, Object>> dataList = FlexJsonUtil.fromJsonArray(datas, HashMap.class);
		return new ModelAndView(ExcelViewUtils.getView(colList, dataList, title, remark, employee));
	}

	/**
	 * 向客户端下载文件,弹出下载框.
	 * 
	 * @param response
	 *            (HttpServletResponse)
	 * @param file
	 *            (需要下载的文件)
	 * @param isDel
	 *            (下载完成后是否删除该文件)
	 * @throws IOException
	 */
	@RequestMapping("/common/export.action")
	public void exportFile(HttpServletResponse response, HttpServletRequest request, File file, boolean isDel) throws IOException {
		OutputStream out = null;
		InputStream in = null;
		// 获得文件名
		String filename = URLEncoder.encode(file.getName(), "UTF-8");
		// 定义输出类型(下载)
		response.setContentType("application/force-download");
		response.setHeader("Location", filename);
		// 定义输出文件头
		response.setHeader("Content-Disposition", "attachment;filename=" + filename);
		out = response.getOutputStream();
		in = new FileInputStream(file.getPath());
		byte[] buffer = new byte[1024];
		int i = -1;
		while ((i = in.read(buffer)) != -1) {
			out.write(buffer, 0, i);
		}
		in.close();
		out.close();
		if (isDel) {
			// 删除文件,删除前关闭所有的Stream.
			file.delete();
		}
	}

	@RequestMapping(value = "/common/getFormAttachs.action")
	@ResponseBody
	public Map<String, Object> getFormAttachs(HttpSession session, String caller, int keyvalue) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("attach", formAttachService.getFormAttachs(caller, keyvalue));
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/common/getFilePaths.action")
	@ResponseBody
	public Map<String, Object> getFilePaths(HttpSession session, String id, String field) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("files", formAttachService.getFiles(id));
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/common/beforeExport.action")
	@ResponseBody
	public Map<String, Object> beforeExprt(HttpServletRequest request,HttpSession session, String caller, String type, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		boolean self = false;
		boolean _jobemployee = false;
		if ("true".equals(request.getParameter("_self")))
			self = true;
		if (request.getAttribute("_jobemployee")!=null&&"true".equals(request.getAttribute("_jobemployee").toString())){
			_jobemployee = true;
		}
		int count = formAttachService.beforeExport(caller, type, condition, employee,self,_jobemployee,request);
		if (count > ExportObserve.warnSize) {
			if (ExportObserve.getInstance().size() > 0) {
				// 超过警告size时，只能允许一个进行
				modelMap.put("busy", true);
			} else {
				ExportObserve.getInstance().putObserve(session.getId());
			}
		}
		modelMap.put("count", count);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/common/downloadPageinstruction.action")
	public void downloadPageinstruction(HttpServletResponse response, HttpServletRequest request, String fileName,int id,String field) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		download( response,  request,  fileName);
		sysnavigationService.downloadPageinstruction(id,field);
	}
	
	@RequestMapping("/common/downloadbyFTP.action")
	public void downloadByFTP(HttpServletResponse response, String id,int ftpid,String fileN) throws IOException {
		JSONObject objs = formAttachService.getFiles(id).getJSONObject(0);
		Object[] obj = baseDao.getFieldsDataByCondition("SYS_FTP", new String[]{"sf_ip","sf_port","sf_username","sf_password","sf_defaultpath"}, "sf_id="+ftpid);
		String ip = (String) obj[0];
		int port =  Integer.parseInt(obj[1].toString());
		String username = (String) obj[2];
		String password = obj[3]==null?"":(String)obj[3];
		String fileName = objs.getString("fp_name");
		String path = objs.getString("fp_path");
		FTPUtil.download(ip, port, username, password, path, fileName, fileN, response);
	}
	
	@RequestMapping("/common/getHtml.action")
	@ResponseBody
	public Map<String, Object> getHtml(HttpServletRequest request, String folderId) throws Exception {
		JSONObject obj = formAttachService.getFiles(folderId).getJSONObject(0);
		String path = obj.getString("fp_path");
		String fileName = obj.getString("fp_name");
		String type = request.getParameter("type");
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String newPath = "";
		try {
			if ("doc".equals(type) || "docx".equals(type)) {
				newPath = WordToHtml.getWord(path, null, fileName);
			}
			if ("xls".equals(type) || "xlsx".equals(type)) {
				newPath = ExcelToHtml.getHtml(path,fileName);
			}
			if ("png".equals(type) || "jpg".equals(type) || "gif".equals(type) || "jpeg".equals(type) || "GIF".equals(type) || "JPG".equals(type) || "PNG".equals(type) || "pdf".equals(type)){
				newPath = path;
			}
		} catch (Exception e) {
			BaseUtil.showError("打开文件失败！");
		}
		modelMap.put("newPath", newPath);
		modelMap.put("success", true);
		return modelMap;
	}
}
