package com.uas.erp.controller.ma;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

import com.sun.org.apache.bcel.internal.generic.RETURN;
import com.uas.erp.core.ExcelUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.FileUpload;
import com.uas.erp.model.GridPanel;
import com.uas.erp.model.UpdateSchemeDetail;
import com.uas.erp.service.ma.UpdateSchemeService;

@Controller
public class UpdateSchemeController {
	@Autowired
	private UpdateSchemeService updateSchemeService;
	@Autowired
	private BaseDao baseDao;

	/**
	 * 保存
	 */
	@RequestMapping("/ma/update/saveUpdateScheme.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		updateSchemeService.saveUpdateScheme(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/ma/update/updateUpdateScheme.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		updateSchemeService.updateUpdateScheme(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/ma/update/deleteUpdateScheme.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		updateSchemeService.deleteUpdateScheme(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/ma/getTreeNode.action")
	@ResponseBody
	public Map<String, Object> getTreeNode(String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("result", updateSchemeService.getTreeNode(condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获得可更新字段
	 */
	@RequestMapping("/ma/getColumns.action")
	@ResponseBody
	public Map<String, Object> getColumns(String tablename) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", updateSchemeService.getColumns(tablename));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 根据UpdateteScheme id获取可勾选的所有列
	 */
	@RequestMapping("/ma/update/getUpdateDetail.action")
	@ResponseBody
	public Map<String, Object> getUpdateDetail(Integer id, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String con = condition != null ? condition : "";
		modelMap.put("indexfields", updateSchemeService.getIndexFields(id));
		modelMap.put("updatedetails", updateSchemeService.getUpdateDetails(id, con));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 根据em_code 获取可更新方案
	 */
	@RequestMapping("/ma/update/getUpdateScheme.action")
	@ResponseBody
	public Map<String, Object> getUpdateSchemes(String em_code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("schemes", updateSchemeService.getUpdateSchemes(em_code));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 根据勾选的字段生成excel模板
	 * 
	 * @throws IOException
	 */
	@RequestMapping("/ma/update/exportExcel.xls")
	public ModelAndView exportExcel(HttpSession session, HttpServletResponse response, HttpServletRequest request, Integer id,
			String title, String checked) throws IOException {
		String con = "field_ in ('" + checked.replaceAll(",", "','") + "')";
		updateSchemeService.updateChecked(id, con);
		List<UpdateSchemeDetail> indexfields = updateSchemeService.getIndexFields(id);
		List<UpdateSchemeDetail> schemeDetails = updateSchemeService.getUpdateDetails(id, con);
		Map<String, String> headers = new LinkedHashMap<String, String>();
		Map<String, Integer> widths = new HashMap<String, Integer>();
		Map<String, String> types = new HashMap<String, String>();
		List<Map<Object, Object>> datas = new ArrayList<Map<Object, Object>>();
		Map<Object, Object> data = new HashMap<Object, Object>();
		for (UpdateSchemeDetail d : indexfields) {
			headers.put(d.getField_(), d.getCaption_());
			widths.put(d.getField_(), d.getWidth_());
			types.put(d.getField_(), "");
			data.put(d.getField_(), "");
		}
		for (UpdateSchemeDetail d : schemeDetails) {
			headers.put(d.getField_(), d.getCaption_());
			widths.put(d.getField_(), d.getWidth_());
			if (d.getType_().startsWith("NUMBER") || d.getType_().startsWith("FLOAT")) {
				types.put(d.getField_(), "0.000000");
				data.put(d.getField_(), 0);
			} else {
				types.put(d.getField_(), "");
				data.put(d.getField_(), "");
			}
		}
		datas.add(data);
		Employee employee = (Employee) session.getAttribute("employee");
		return new ModelAndView(
				new ExcelUtil(headers, widths, types, datas, new String(title.getBytes("utf-8"), "iso8859-1"), employee).getView());
	}

	/**
	 * 上传待更新数据 存在表updateSchemeData中
	 */
	@RequestMapping("/ma/update/importData.action")
	public @ResponseBody String upexcel(FileUpload uploadItem, Integer id) {
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
				int ulid = 0;
				if (ft.equals("xls") || ft.equals("xlsx")) {
					UpdateImport update = new UpdateImport(is, id);
					ulid = update.getUlid();
					gridPanel = update.getPanel();
				} else {
					return new String("{error: 'excel文件的格式不太规范,导入失败<hr>可以尝试将文件另存为.xls文件,或将数据选择性粘贴到excel模板,然后导入'}".getBytes("utf-8"),
							"iso8859-1");
				}
				String r = "{success: true, count: " + gridPanel.getDataCount() + ",ulid:" + ulid + "}";
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
	 * 获得更新方案
	 */
	@RequestMapping("/ma/getUpdateScheme.action")
	@ResponseBody
	public Map<String, Object> getUpdateScheme(String id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", updateSchemeService.getUpdateScheme(id));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获得更新方案
	 */
	@RequestMapping("/ma/getOtherData.action")
	@ResponseBody
	public Map<String, Object> getOtherData(String id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", updateSchemeService.getOtherData(id));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 根据condition取已保存在updateSchemeData的数据
	 */
	@RequestMapping(value = "/ma/update/getUpdateData.action")
	@ResponseBody
	public Map<String, Object> getInitData(HttpSession session, String condition) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", updateSchemeService.getUpdateDatas(condition));
		return modelMap;
	}

	/**
	 * 校验数据
	 */
	@RequestMapping(value = "/ma/update/checkData.action")
	@ResponseBody
	public Map<String, Object> checkData(HttpSession session, Integer ulid) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		updateSchemeService.checkData(ulid);// 索引列必填
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 执行更新操作
	 */
	@RequestMapping(value = "/ma/update/updateData.action")
	@ResponseBody
	public Map<String, Object> updateData(HttpSession session, Integer ulid) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		updateSchemeService.updateData(employee, ulid);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 根据scheme_id调取更新数据历史
	 */
	@RequestMapping(value = "/ma/update/updateHistory.action")
	@ResponseBody
	public Map<String, Object> getInitsHistory(HttpSession session, Integer id) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", updateSchemeService.getUpdateHistory(id));
		return modelMap;
	}

	@RequestMapping("/ma/update/getEmpdbfindData.action")
	@ResponseBody
	public Map<String, String> getEmpdbfindData(HttpSession session, String fields, String condition, int page, int pagesize) {
		Map<String, String> modelMap = new HashMap<String, String>();
		modelMap.put("data", updateSchemeService.getEmpdbfindData(fields, condition, page, pagesize));
		modelMap.put("success", "true");
		return modelMap;
	}
	
	/**
	 * 导出错误数据
	 * 
	 * @throws IOException
	 */
	@RequestMapping("/ma/update/exportErrorExcel.xls")
	public ModelAndView exportErrorExcel(HttpSession session, HttpServletResponse response, HttpServletRequest request, Integer id,
			String title) throws IOException {
		String UL_USID=baseDao.getFieldDataByCondition("UPDATESCHEMELOG", "UL_USID", "UL_ID='"+id+"'").toString();
		int ULUSID=Integer.parseInt(UL_USID);
		List<UpdateSchemeDetail> indexfields = updateSchemeService.getIndexFields(ULUSID);
		List<UpdateSchemeDetail> schemeDetails = updateSchemeService.getUpdateDetails(ULUSID, "CHECKED_=1");
		List<Map<Object, Object>> datas = new ArrayList<Map<Object, Object>>();
		Map<String, String> headers = new LinkedHashMap<String, String>();
		Map<String, Integer> widths = new HashMap<String, Integer>();
		Map<String, String> types = new HashMap<String, String>();
	
		Map<Object, Object> data = new HashMap<Object, Object>();
		for (UpdateSchemeDetail d : indexfields) {
			headers.put(d.getField_(), d.getCaption_());
			widths.put(d.getField_(), d.getWidth_());
			types.put(d.getField_(), "");
			
		}
		for (UpdateSchemeDetail d : schemeDetails) {
			headers.put(d.getField_(), d.getCaption_());
			widths.put(d.getField_(), d.getWidth_());
			if (d.getType_().startsWith("NUMBER") || d.getType_().startsWith("FLOAT")) {
				types.put(d.getField_(), "0.000000");
				
			} else {
				types.put(d.getField_(), "");
				
			}
		}
		datas=updateSchemeService.getErrData(id);		
		Employee employee = (Employee) session.getAttribute("employee");
		return new ModelAndView(
				new ExcelUtil(headers, widths, types, datas, new String(title.getBytes("utf-8"), "iso8859-1"), employee).getView());
		
	
	}
	
	
}
