package com.uas.erp.controller.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.core.web.DocumentConfig;
import com.uas.erp.core.web.ExcelViewUtils;
import com.uas.erp.model.Employee;
import com.uas.erp.model.FileUpload;
import com.uas.erp.model.GridPanel;
import com.uas.erp.model.InitDetail;
import com.uas.erp.service.common.InitService;

@Controller
public class InitController {
	@Autowired
	private InitService initService;

	@RequestMapping("/system/init.action")
	public ModelAndView saasErrorPage() {
		Map<String, Object> params = new HashMap<String, Object>();
		Employee em = SystemSession.getUser();
		String installtype = null;
	    if(!"admin".equals(em.getEm_type())) BaseUtil.showError("请联系管理员进行相关操作!");
		try {
			installtype = em.getCurrentMaster().getMa_installtype();
		} catch (Exception e) {
			installtype = "Make";
		}
		params.put("installtype", installtype);
		return new ModelAndView("/sys/sysinit", params);
	}

	/**
	 * 根据父节点加载子节点
	 */
	@RequestMapping(value = "/system/initTree.action")
	@ResponseBody
	public Map<String, Object> getInitsByParentId(HttpSession session, int pid) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("tree", initService.getInitTree(pid));
		return modelMap;
	}

	/**
	 * 保存初始化配置
	 */
	@RequestMapping(value = "/system/saveInitDetail.action")
	@ResponseBody
	public Map<String, Object> saveInitDetail(HttpSession session, String data) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		initService.saveInitDetail(employee,data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 根据caller调取需初始化字段
	 */
	@RequestMapping(value = "/system/initDetails.action")
	@ResponseBody
	public Map<String, Object> getInitsByCaller(HttpSession session, String caller) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", initService.getInitDetails(caller));
		return modelMap;
	}

	/**
	 * 生成excel模板
	 * 
	 * @throws IOException
	 */
	@RequestMapping("/system/initTemplate.xls")
	public ModelAndView createExcel(HttpSession session, HttpServletResponse response, HttpServletRequest request, String caller,
			String title) throws IOException {
		List<InitDetail> details = initService.getInitDetails(caller);
		DocumentConfig config = new DocumentConfig();	
		List<Map<String, Object>> datas =  new ArrayList<Map<String, Object>>();
		Map<String, Object> data = new HashMap<String, Object>();
		for (InitDetail d : details) {
			if (d.getId_visible() == 1) {
				if(d.getId_need()==1) config.getHeaders().put(d.getId_field(), d.getId_caption()+"(必填)");
				else config.getHeaders().put(d.getId_field(), d.getId_caption());
				config.getWidths().put(d.getId_field(), d.getId_width());
				config.getComments().put(d.getId_field(), d.getId_rule());
				if (d.getId_type().startsWith("number")) {
					config.getTypes().put(d.getId_field(), "0.000000");		
					data.put(d.getId_field(), 0);
				} else {
					config.getTypes().put(d.getId_field(), "");	
					data.put(d.getId_field(), "");
				}
			}
		}
		if (initService.getDemoData(caller)!=null) datas=initService.getDemoData(caller);
		else datas.add(data);
		Employee employee = (Employee) session.getAttribute("employee");
		return new ModelAndView(ExcelViewUtils.getView(config, datas, new String(title.getBytes("utf-8"), "iso8859-1"), employee));
	}

	/**
	 * 生成错误数据excel
	 * 
	 * @throws IOException
	 */
	@RequestMapping("/system/initError.xls")
	public ModelAndView exportError(HttpSession session, HttpServletResponse response, HttpServletRequest request, String caller,
			String title, Integer id) throws IOException {
		List<InitDetail> details = initService.getInitDetails(caller);
		DocumentConfig config = new DocumentConfig();
		List<Map<String, Object>> datas = initService.getErrInitData(id);
		for (InitDetail d : details) {
			if (d.getId_visible() == 1) {
				if(d.getId_need()==1) config.getHeaders().put(d.getId_field(), d.getId_caption()+"(必填)");
				else config.getHeaders().put(d.getId_field(), d.getId_caption());
				config.getWidths().put(d.getId_field(), d.getId_width());
				config.getComments().put(d.getId_field(), d.getId_rule());
				if (d.getId_type().startsWith("number")) {
					config.getTypes().put(d.getId_field(), "0.000000");
				} else {
					config.getTypes().put(d.getId_field(), "");
				}
			}
		}
		Employee employee = (Employee) session.getAttribute("employee");
		return new ModelAndView(ExcelViewUtils.getView(config, datas, new String(title.getBytes("utf-8"), "iso8859-1"), employee));
	}

	/**
	 * 生成所有数据excel
	 * 
	 * @throws IOException
	 */
	@RequestMapping("/system/initAll.xls")
	public ModelAndView exportAll(HttpSession session, HttpServletResponse response, HttpServletRequest request, String caller,
			String title, Integer id) throws IOException {
		List<InitDetail> details = initService.getInitDetails(caller);
		DocumentConfig config = new DocumentConfig();
		List<Map<String, Object>> datas = initService.getInitData("id_ilid=" + id);
		for (InitDetail d : details) {
			if (d.getId_visible() == 1) {
				if(d.getId_need()==1) config.getHeaders().put(d.getId_field(), d.getId_caption()+"(必填)");
				else config.getHeaders().put(d.getId_field(), d.getId_caption());				
				config.getWidths().put(d.getId_field(), d.getId_width());
				config.getComments().put(d.getId_field(), d.getId_rule());
				if (d.getId_type().startsWith("number")) {
					config.getTypes().put(d.getId_field(), "0.000000");
				} else {
					config.getTypes().put(d.getId_field(), "");
				}
			}
		}
		Employee employee = (Employee) session.getAttribute("employee");
		return new ModelAndView(ExcelViewUtils.getView(config, datas, new String(title.getBytes("utf-8"), "iso8859-1"), employee));
	}

	/**
	 * 初始化导入数据时， 从excel读取的数据直接先存放在临时表initdata
	 */
	@RequestMapping("/system/initImport.action")
	public @ResponseBody String upexcel(FileUpload uploadItem, String caller) {
		InputStream is = null;
		try {
			CommonsMultipartFile file = uploadItem.getFile();
			long size = file.getSize();
			if (size > 104857600) {
				return new String(("{error: '文件过大', size:" + size + "}").getBytes("utf-8"), "iso8859-1");
			} else {
				initService.clearBefore(caller);
				String ft = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
				is = uploadItem.getFile().getInputStream();
				GridPanel gridPanel = null;
				int ilid = 0;
				if (ft.equals("xls")) {
					InitImport init = new InitImport(is, caller);
					ilid = init.getIlid();
					gridPanel = init.getPanel();
				}else if(ft.equals("xlsx")){
					InitImportXlsx init = new InitImportXlsx(is, caller);
					ilid = init.getIlid();
					gridPanel = init.getPanel();
				}else {
					return new String("{error: 'excel文件的格式不太规范,导入失败<hr>可以尝试将文件另存为.xls文件,或将数据选择性粘贴到excel模板,然后导入'}".getBytes("utf-8"),
							"iso8859-1");
				}
				String r = "{success: true, count: " + gridPanel.getDataCount() + ",ilid:" + ilid + "}";
				return new String(r.getBytes("utf-8"), "iso8859-1");
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				return new String("{error: 'excel文件的格式不太规范,导入失败<hr>可以尝试将文件另存为.xls文件,或将数据选择性粘贴到excel模板,然后导入'}".getBytes("utf-8"),
						"iso8859-1");
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
	 * 根据condition取已保存在initdata的数据
	 */
	@RequestMapping(value = "/system/getInitData.action")
	@ResponseBody
	public Map<String, Object> getInitData(HttpSession session, String condition) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", initService.getInitDatas(condition));
		return modelMap;
	}

	/**
	 * 根据condition取已保存在initdata中校验出错的数据
	 */
	@RequestMapping(value = "/system/getErrData.action")
	@ResponseBody
	public Map<String, Object> getErrData(HttpSession session, Integer id, String condition) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", initService.getErrDatas(id, condition));
		return modelMap;
	}

	/**
	 * 修改initdata的数据
	 */
	@RequestMapping(value = "/system/updateInitData.action")
	@ResponseBody
	public Map<String, Object> updateInitData(HttpSession session, String data) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		initService.updateInitData(data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除指定初始化数据，只能是未转正式的
	 */
	@RequestMapping(value = "/system/deleteInitData.action")
	@ResponseBody
	public Map<String, Object> deleteInitData(HttpSession session, Integer id) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		initService.deleteInitData(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除初始化数据，所有已转正式的
	 */
	@RequestMapping(value = "/system/init/clear.action")
	@ResponseBody
	public Map<String, Object> clearInitData(HttpSession session) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		initService.clearInitData();
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除错误数据
	 */
	@RequestMapping(value = "/system/init/errdelete.action")
	@ResponseBody
	public Map<String, Object> deleteErrInitData(HttpSession session, Integer id) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		initService.deleteErrInitData(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * uu号登陆
	 */
	@RequestMapping(value = "/system/uulogin.action")
	@ResponseBody
	public Map<String, Object> uulogin(HttpSession session, String en_uu, String em_uu, String em_password) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		try {
			URL url = new URL("http://www.usoftchina.com/usoft/login_login?en_uu=" + en_uu + "&em_email=" + em_uu + "&em_password="
					+ em_password + "&language=jian");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			InputStream inputStream = connection.getInputStream();
			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(reader);
			String str = null;
			StringBuffer sb = new StringBuffer();
			while ((str = bufferedReader.readLine()) != null) {
				sb.append(str);
			}
			bufferedReader.close();
			reader.close();
			inputStream.close();
			connection.disconnect();
			modelMap.put("success", true);
			modelMap.put("data", sb.toString());
		} catch (Exception e) {
			modelMap.put("success", true);
		}
		return modelMap;
	}

	/**
	 * 根据caller调取需导入数据历史
	 */
	@RequestMapping(value = "/system/initHistory.action")
	@ResponseBody
	public Map<String, Object> getInitsHistory(HttpSession session, String caller) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", initService.getInitHistory(caller));
		return modelMap;
	}

	/**
	 * 删除前。删除上次校验结果
	 */
	@RequestMapping(value = "/system/beforeCheckLog.action")
	@ResponseBody
	public Map<String, Object> beforeCheckLog(HttpSession session, Integer id) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		initService.beforeCheckLog(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 校验数据
	 */
	@RequestMapping(value = "/system/checkInitData.action")
	@ResponseBody
	public Map<String, Object> checkInitData(HttpSession session, Integer id) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		initService.check(id);
		return modelMap;
	}

	/**
	 * 校验准备完毕后，正式校验
	 */
	@RequestMapping(value = "/system/afterCheckLog.action")
	@ResponseBody
	public Map<String, Object> afterCheckLog(HttpSession session, Integer id) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		initService.afterCheckLog(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 校验结果
	 */
	@RequestMapping(value = "/system/getCheckResult.action")
	public void getCheckResult(HttpServletResponse response, HttpSession session, Integer id) throws Exception {
		InputStream in = initService.getResult(id);
		OutputStream os = response.getOutputStream();
		int data = 0;
		byte[] buffer = new byte[10240];
		while ((data = in.read(buffer)) != -1) {
			os.write(buffer, 0, data);
		}
		in.close();
		os.close();
	}

	/**
	 * 删除校验结果
	 */
	@RequestMapping(value = "/system/beforeToFormal.action")
	@ResponseBody
	public Map<String, Object> beforeToFormal(HttpSession session, Integer id) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		initService.beforeToFormal(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转正式
	 */
	@RequestMapping(value = "/system/toFormalData.action")
	@ResponseBody
	public Map<String, Object> toFormalData(HttpSession session, Integer id, Integer start, Integer end) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		initService.toFormalData(employee, id, start, end);
		return modelMap;
	}

	/**
	 * 删除校验结果
	 */
	@RequestMapping(value = "/system/afterToFormal.action")
	@ResponseBody
	public Map<String, Object> afterToFormal(HttpSession session, Integer id) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		initService.afterToFormal(id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存到示例数据
	 */
	@RequestMapping(value = "/system/init/todemo.action")
	@ResponseBody
	public Map<String, Object> toDemo(HttpSession session, Integer id,String caller) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		initService.toDemo(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 匹配料号
	 * lidy:【新功能需求】【2017090453】【产品生命周期管理】【BOM批量导入界面添加“匹配料号”按钮】
	 */
	@RequestMapping(value = "/system/matchingCode.action")
	@ResponseBody
	public Map<String, Object> matchingCode(HttpSession session, Integer id,String caller) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		initService.matchingCode(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
