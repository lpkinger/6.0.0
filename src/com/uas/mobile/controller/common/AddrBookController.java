package com.uas.mobile.controller.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.PathUtil;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Employee;
import com.uas.erp.model.FileUpload;
import com.uas.erp.model.Task;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.FilePathService;
import com.uas.erp.service.plm.TaskService;
import com.uas.mobile.service.AddrBookService;
import com.uas.mobile.service.PanelService;

@Controller("addrBookController")
public class AddrBookController {

	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private AddrBookService addrBookService;
	@Autowired
	private FilePathService filePathService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private PanelService panelService;

	/**
	 * 获取第一层组织
	 * 
	 * @param master
	 * @return
	 */
	@RequestMapping("/mobile/getRootHrorg.action")
	@ResponseBody
	public Map<String, Object> getRootHrorg(HttpServletRequest request,
			String master) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");

		SpObserver.putSp(master);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<String, Object>> hrorgs = null;
		String localSobName = "";
		hrorgs = addrBookService.getRootHrorg();
		localSobName = addrBookService.getSobName(master);
		modelMap.put("success", true);
		modelMap.put("master", localSobName);
		modelMap.put("hrorgs", hrorgs);
		return modelMap;
	}

	/**
	 * 获取下级组织
	 */
	@RequestMapping("/mobile/getLeafHrorg.action")
	@ResponseBody
	public Map<String, Object> getLeafHrorg(HttpServletRequest request,
			int or_id, int page, int pageSize) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");

		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<String, Object>> hrorgs = null;
		List<Map<String, Object>> employees = null;
		int start = 0;
		int end = 0;
		if (page == 0)
			page = 1;
		start = (page - 1) * pageSize;
		end = page * pageSize;
		hrorgs = addrBookService.getLeafHrorg(or_id);
		employees = addrBookService.getEmployeesByOrId(or_id, start, end);
		modelMap.put("success", true);
		modelMap.put("hrorgs", hrorgs);
		modelMap.put("employees", employees);
		return modelMap;
	}

	/**
	 * 获取所有的组织和员工
	 * 
	 * @param request
	 * @param master
	 * @return
	 */
	@RequestMapping("/mobile/getAllHrorgEmps.action")
	@ResponseBody
	public Map<String, Object> getAllHrorgEmps(HttpServletRequest request,
			String master, String lastdate) {
		//移动端employee只传入emcode	
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		SpObserver.putSp(master);
		if (employee == null){
			BaseUtil.showError("会话已断开!");
		}else{
			employee=employeeService.getEmployeeByEmcode(employee.getEm_code());
			if(employee == null){
				BaseUtil.showError("会话已断开!");
			}
		}	

		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<String, Object>> hrorgs = null;
		List<Map<String, Object>> employees = null;
		String sysdate = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		sysdate = df.format(new Date());
		String type=employee.getEm_class();
		Integer orid=employee.getEm_defaultorid();
		String departcode=employee.getEm_departmentcode()==null?"NoDepartment":employee.getEm_departmentcode();
		employees = "外部".equals(type)?addrBookService.getOuterEmps(lastdate, departcode):addrBookService.getAllEmps(lastdate);
		hrorgs ="外部".equals(type)?addrBookService.getOuterHrorg(lastdate,orid):addrBookService.getAllHrorg(lastdate);
		modelMap.put("success", true);
		modelMap.put("hrorgs", hrorgs);
		modelMap.put("employees", employees);
		modelMap.put("sysdate", sysdate);
		return modelMap;
	}
	
	/**
	 * 人员查询
	 * 
	 * @param request
	 * @param em_name
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/mobile/QueryEmployee.action")
	@ResponseBody
	public Map<String, Object> QueryEmployee(HttpServletRequest request,
			String em_name) throws UnsupportedEncodingException {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");

		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<String, Object>> employees = null;
		employees = addrBookService.queryEmployeeByName(em_name);
		modelMap.put("success", true);
		modelMap.put("employees", employees);
		return modelMap;
	}

	@RequestMapping("/mobile/getEmployeeInfo.action")
	@ResponseBody
	public Map<String, Object> getEmployeeInfo(HttpServletRequest request,
			int em_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee1 = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee1 == null)
			BaseUtil.showError("会话已断开!");

		Employee employee = employeeService.getEmployeeById(em_id);
		List<Object> jobs = addrBookService.getJobs(em_id);
		modelMap.put("success", true);
		modelMap.put("em_code", employee.getEm_code());
		modelMap.put("em_name", employee.getEm_name());
		modelMap.put("em_position", employee.getEm_position());
		modelMap.put("em_jobs", jobs);
		modelMap.put("em_defaultorname", employee.getEm_defaultorname());
		modelMap.put("em_depart", employee.getEm_depart());
		modelMap.put("em_tel", employee.getEm_tel());
		modelMap.put("em_mobile", employee.getEm_mobile());
		modelMap.put("em_email", employee.getEm_email());
		modelMap.put("em_uu", employee.getEm_uu());
		modelMap.put("em_imageid", employee.getEm_imageid());
		modelMap.put("em_photourl", employee.getEm_photourl());
		return modelMap;
	}

	/**
	 * 根据人员code获取em_photourl和em_name
	 */
	@RequestMapping("/mobile/getEmployeePic.action")
	@ResponseBody
	public Map<String, Object> getEmployeePic(HttpServletRequest request,
			String em_code) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");

		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("obj", addrBookService.getEmployeePic(em_code));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 根据人员code获取em_id和em_name
	 */
	@RequestMapping("/mobile/getEmployeeByCode.action")
	@ResponseBody
	public Map<String, Object> getEmployeeByCode(HttpServletRequest request,
			String em_code) {
		Employee employee1 = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee1 == null)
			BaseUtil.showError("会话已断开!");

		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = employeeService.getEmployeeByName(em_code);
		modelMap.put("em_id", employee.getEm_id());
		modelMap.put("em_name", employee.getEm_name());
		modelMap.put("em_imageid", employee.getEm_imageid());
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 上传人员图片
	 * 
	 * @param em_code
	 *            人员编号
	 * @param img
	 * @param master
	 * @return id 上传图片成功后回传给客户端表filepath的fp_id值
	 */
	@RequestMapping("/mobile/uploadEmployeeAttach.action")
	@ResponseBody
	public Map<String, Object> uploadEmployeeAttach(HttpServletRequest request,
			String em_code, MultipartFile img, String master, String type) {// 新加参数type=common
																			// 表示是公用的传图片接口，不更新人员表图片信息
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		SpObserver.putSp(master);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (type == null)
			type = "employee";
		modelMap.put("id", upload(em_code, img, master, type));
		modelMap.put("success", true);
		return modelMap;
	}

	public @ResponseBody String upload(String em_code, MultipartFile img,
			String master, String type) {
		String filename = img.getOriginalFilename();
		long size = img.getSize();
		if (size > 104857600) {
			// System.out.println("{error: '文件过大'}");
			return "{error: '文件过大'}";
		}
		String path = getFilePath(filename, em_code);
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
		Employee employee = employeeService.getEmployeeByName(em_code);
		int id = filePathService.saveFilePath(path, (int) size, filename,
				employee);
		// 更新人员资料中人员图片id和URL
		if (type != null && type.equals("employee"))
			addrBookService.updateEmployeePic(employee.getEm_id(), id, path);

		return "{id:" + id + "}";
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

	/**
	 * @param id
	 *            filepath表fp_id
	 * @param master
	 * @return 返回图片的url
	 */
	@RequestMapping("/mobile/getEmployeeAttach.action")
	@ResponseBody
	public Map<String, Object> getEmployeeAttach(HttpServletRequest request,
			int id, String master) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		SpObserver.putSp(master);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("url", filePathService.getFilepath(id));
		return modelMap;
	}

	/**
	 * @param taskcode
	 * @param master
	 * @return 返回附件的url
	 */
	@RequestMapping("/mobile/getAttachByTaskcode.action")
	@ResponseBody
	public Map<String, Object> getAttachByTaskcode(HttpServletRequest request,
			String taskcode, String master) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		SpObserver.putSp(master);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<String> urlArr = new ArrayList<String>();
		String attachs = taskService.getTaskByCode(taskcode).getAttachs();
		if (attachs != "" && attachs != null) {
			String[] idArr = attachs.split(";");
			for (String id : idArr) {
				if (id != "" && id != null)
					urlArr.add(filePathService.getFilepath(Integer.parseInt(id)));
			}
		}
		modelMap.put("success", true);
		modelMap.put("url", urlArr.toString());
		return modelMap;
	}

	/**
	 * 多附件上传
	 * 
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	@RequestMapping("/mobile/uploadAttachs.action")
	@ResponseBody
	public Map<String, Object> uploadAttachs(HttpServletRequest request,
			String master) throws IllegalStateException, IOException {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		SpObserver.putSp(master);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Integer> idArr = new ArrayList<Integer>();
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		// 判断 request 是否有文件上传,即多部分请求
		if (multipartResolver.isMultipart(request)) {
			// 转换成多部分request
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			// 取得request中的所有文件名
			Iterator<String> iter = multiRequest.getFileNames();
			while (iter.hasNext()) {
				// 取得上传文件
				MultipartFile file = multiRequest.getFile(iter.next());
				if (file != null) {
					// 取得当前上传文件的文件名称
					String myFileName = file.getOriginalFilename();
					long size = file.getSize();
					// 如果名称不为"",说明该文件存在，否则说明该文件不存在
					if (myFileName.trim() != "") {
						// 定义上传路径
						String path = getFilePath(myFileName,
								employee.getEm_code());
						File localFile = new File(path);
						file.transferTo(localFile);
						int id = filePathService.saveFilePath(path, (int) size,
								myFileName, employee);
						idArr.add(id);
					}
				}
			}
		}
		modelMap.put("id", idArr.toString());
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存Commentsback_mobile
	 */
	@RequestMapping("/mobile/Commentsback_mobile.action")
	@ResponseBody
	public Map<String, Object> save(HttpServletRequest request,String caller, String formStore,MultipartFile img1,MultipartFile img2,MultipartFile img3) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap=addrBookService.Commentsback_mobile(formStore, caller,img1,img2,img3);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存Commentsback_mobile
	 */
	@RequestMapping("/mobile/AllNotecount.action")
	@ResponseBody
	public Map<String, Object> allcount(HttpServletRequest request,
			String master, String emid) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		Map<String, Object> a = null;
		a = addrBookService.getAllNoteCount(master, emid);
		return a;
	}

	/**
	 * APP根据组织架构创建管理群,hrorg_mobile表的oa_reamrk字段代表是否创建，1为创建
	 */
	@RequestMapping("/mobile/update_hrorgmobile.action")
	@ResponseBody
	public Map<String, Object> updatemobile(HttpServletRequest request,
			String orid, int kind) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String thid = "";
		thid = addrBookService.update_hrorgmobile(orid, kind);
		modelMap.put("or_id", thid);
		return modelMap;
	}

	/**
	 * 手机保存工作报告接口
	 */
	@RequestMapping("/mobile/addWorkReport.action")
	@ResponseBody
	public Map<String, Object> addWorkReport(HttpServletRequest request,
			String caller, String formStore) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", addrBookService.addWorkReport(caller, formStore));
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}
	
	/**
	 * 手机读取界面配置接口
	 */
	@RequestMapping("/mobile/formConfig.action")
	@ResponseBody
	public Map<String, Object> formConfig(HttpServletRequest request,
			String caller) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("formdetail", addrBookService.formConfig(caller));
		modelMap.put("gridetail", addrBookService.gridConfig(caller));
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 手机MAC地址保存
	 */
	@RequestMapping("/mobile/addMobileMac.action")
	@ResponseBody
	public Map<String, Object> addMobileMac(HttpServletRequest request,
			String emcode, String macAddress) {
		System.out.println("macAddress=" + macAddress);
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		addrBookService.addMobileMac(emcode, macAddress);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 查询是mac地址
	 */
	@RequestMapping("/mobile/queryMobileMac.action")
	@ResponseBody
	public Map<String, Object> queryMobileMac(HttpServletRequest request,
			String emcode, String macaddress) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("macaddress",
				addrBookService.queryMobileMac(emcode, macaddress));
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 获取工作日报
	 */
	@RequestMapping("/mobile/getWorkDaily.action")
	@ResponseBody
	public Map<String, Object> getWorkDaily(HttpServletRequest request,
			String emcode, int pageIndex) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("listdata",
				addrBookService.getWorkDaily(emcode, pageIndex));
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}
	
	/**
	 * 获取工作汇报（日报、周报、月报）
	 */
	@RequestMapping("/mobile/getWorkReports.action")
	@ResponseBody
	public Map<String, Object> getWorkReports(HttpServletRequest request,
			String emcode, int pageIndex,String caller) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("listdata",
				addrBookService.getWorkReports(emcode, pageIndex,caller));
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 补卡申请
	 */
	@RequestMapping("/mobile/addSignCard.action")
	@ResponseBody
	public Map<String, Object> addSignCard(HttpServletRequest request,
			String caller, String formStore) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		addrBookService.addSignCard(caller, formStore);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		return modelMap;
	}

	/**
	 * 更新APP配置
	 */
	@RequestMapping("/mobile/configUpdate.action")
	@ResponseBody
	public Map<String, Object> configUpdate(HttpServletRequest request,
			String caller, String formStore, String gridStore) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		addrBookService.configUpdate(caller, formStore, gridStore);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 通用更新方法，仅限于没有业务逻辑的单据
	 */
	@RequestMapping("/mobile/commonUpdate.action")
	@ResponseBody
	public Map<String, Object> commonUpdate(HttpServletRequest request,
			String caller, String formStore, String gridStore, int keyid) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		addrBookService.commonUpdate(caller, formStore, gridStore, keyid);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 通用删除，仅限于没有业务逻辑的单据
	 */
	@RequestMapping("/mobile/commondelete.action")
	@ResponseBody
	public Map<String, Object> commondelete(HttpServletRequest request,
			String caller, int id) {

		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		addrBookService.commondelete(caller, id);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 获取外勤计划列表
	 */
	@RequestMapping("/mobile/mobileoutplan.action")
	@ResponseBody
	public Map<String, Object> mobileoutplan(HttpServletRequest request,
			String emcode) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();

		modelMap.put("data", addrBookService.mobileoutplan(emcode));
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 自动外勤方法
	 */
	@RequestMapping("/mobile/addAutoSign.action")
	@ResponseBody
	public Map<String, Object> addAutoSign(HttpServletRequest request,
			String caller, String formStore, int mpd_id) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		addrBookService.addAutoSign(caller, formStore, mpd_id);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 判断当天是否有外勤计划
	 */
	@RequestMapping("/mobile/yesornoplan.action")
	@ResponseBody
	public Map<String, Object> yesornoplan(HttpServletRequest request,
			String emcode) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();

		modelMap.put("isOffline", addrBookService.yesornoplan(emcode));
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	// 移动端获取formdetail和detailgrid数据
	@RequestMapping(value = "/mobile/getformandgriddetail.action")
	@ResponseBody
	public Map<String, Object> getFormAndGridDetail(HttpServletRequest req,
			String caller, String condition,int id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", addrBookService.getFormAndGridDetail(caller, condition,id));
		/** OA表单获取配置接口修改,返回form配置的主键、状态码等信息 */
		map.put("config", panelService.getFormConfig(caller));
		map.put("sessionId", req.getSession().getId());
		map.put("success", true);
		return map;
	}
	/**
	 * 保存外勤设置
	 */
	@RequestMapping(value = "/mobile/addOutSet.action")
	@ResponseBody
	public Map<String, Object> addOutSet(HttpServletRequest req,
			String caller, String formStore) {
		Map<String, Object> map = new HashMap<String, Object>();
		addrBookService.addOutSet(caller, formStore);
		map.put("sessionId", req.getSession().getId());
		map.put("success", true);
		return map;
	}
	/**
	 * 获取是否为自动外勤
	 */
	@RequestMapping(value = "/mobile/getOutSetInfo.action")
	@ResponseBody
	public Map<String, Object> getOutSet(HttpServletRequest req) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("result", addrBookService.getOutSet());
		map.put("sessionId", req.getSession().getId());
		map.put("success", true);
		return map;
	}
	/**
	 * OA通用单据反提交
	 */
	@RequestMapping(value = "/mobile/commonres.action")
	@ResponseBody
	public Map<String, Object> commonres(HttpServletRequest req,
			String caller, int id) {
		Map<String, Object> map = new HashMap<String, Object>();
		addrBookService.commonres(caller,id);
		map.put("sessionId", req.getSession().getId());
		map.put("success", true);
		return map;
	}
	/**
	 * 日报数据
	 */
	@RequestMapping(value = "/mobile/getsingledata.action")
	@ResponseBody
	public Map<String, Object> getsingledata(HttpServletRequest req, int id) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("data", addrBookService.getsingledata(id));
		map.put("sessionId", req.getSession().getId());
		map.put("success", true);
		return map;
	}
	
	/**
	 * 获取单条工作汇报（日报、周报、月报）数据
	 */
	@RequestMapping(value = "/mobile/getsingleWorkReports.action")
	@ResponseBody
	public Map<String, Object> getsingleWorkReports(HttpServletRequest req, int id , String caller) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("data", addrBookService.getsingleWorkReports(caller , id));
		map.put("sessionId", req.getSession().getId());
		map.put("success", true);
		return map;
	}
	
	/**
	 * 更新签退
	 */
	@RequestMapping(value = "/mobile/mobileplanUpdate.action")
	@ResponseBody
	public Map<String, Object> mobileplanUpdate(HttpServletRequest request, int id) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		Map<String, Object> map = new HashMap<String, Object>();
		addrBookService.mobileplanUpdate(id);
		map.put("sessionId", request.getSession().getId());
		map.put("success", true);
		return map;
	}

	/**
	 * 获取工作汇报（日报、周报、月报）的初始化数据
	 */
	@RequestMapping(value = "/mobile/getWorkReportInit.action")
	@ResponseBody
	public Map<String, Object> getWorkReportInit(HttpServletRequest request, String emcode , String caller) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("todayData", addrBookService.getTodayData(emcode,caller));
		map.put("YesterdayData", addrBookService.getYesterdayData(emcode,caller));
		map.put("data", addrBookService.getWorkReportInit(emcode,caller));
		map.put("sessionId", request.getSession().getId());
		map.put("success", true);
		return map;
	}
}
