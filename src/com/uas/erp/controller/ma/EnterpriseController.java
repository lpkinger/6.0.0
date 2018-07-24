package com.uas.erp.controller.ma;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.FileUpload;
import com.uas.erp.service.ma.EnterpriseService;

@Controller
public class EnterpriseController {
	@Autowired
	private EnterpriseService enterpriseService;

	/**
	 * 修改form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/ma/logic/updateEnterprise.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		enterpriseService.updateEnterpriseById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * saas初始化修改企业信息
	 * @param caller
	 * @param formStore
	 * @return
	 */
	@RequestMapping("/ma/logic/SaasupdateEnterprise.action")
	@ResponseBody
	public Map<String,Object> SaasUpdate(String formStore){
		String caller="Enterprise";
		Map<String,Object> modelMap=new HashMap<String, Object>();
		enterpriseService.saasupdateEnterpriseById(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/ma/logic/regB2BEnterprise.action")
	@ResponseBody
	public Map<String, Object> regB2BEnterprise(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		enterpriseService.regB2BEnterprise(id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/ma/logic/uploadLogo.action")
	@ResponseBody
	public String uploadLogo(HttpSession session, FileUpload uploadItem) {
		try {
			String type = uploadItem.getFile().getContentType();
			if (!"image".equals(type.split("/")[0])) {
				return "{success:false}";
			}
			long size = uploadItem.getFile().getSize();
			if (size > 20000) {
				return "{success:false}";
			}
			enterpriseService.saveLogo(uploadItem.getFile());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "{success:true}";
	}

	@RequestMapping("/ma/logic/getLogo.action")
	@ResponseBody
	public void getLogo(HttpServletRequest request, HttpServletResponse response) throws SQLException {
		OutputStream os = null;
		InputStream is = null;
		try {
			os = response.getOutputStream();
			Blob logo = enterpriseService.getLogo();
			is = logo.getBinaryStream();
			byte[] b = new byte[4096];
			int n = 0;
			while ((n = is.read(b)) != -1) {
				os.write(b, 0, n);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				os.flush();
				os.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping("ma/logic/hasLogo.action")
	@ResponseBody
	public boolean hasLogo(HttpSession session) {
		return enterpriseService.hasLogo();
	}

	@RequestMapping("ma/logic/setMasterInfo.action")
	@ResponseBody
	public Map<String, Object> setMasterInfo(String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		enterpriseService.setMasterInfo(param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
