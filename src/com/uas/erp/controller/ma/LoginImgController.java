package com.uas.erp.controller.ma;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
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

import com.uas.erp.core.FileUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.Employee;
import com.uas.erp.model.FileUpload;
import com.uas.erp.model.PersonalPower;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.ma.LoginImgService;
import com.uas.erp.service.ma.LogoService;

/**
 * @author lidy
 * @sice 2017-12-04
 * 系统登录背景图片设置
 * */
@Controller
public class LoginImgController {
	@Autowired
	private LoginImgService loginImgService;
	@Autowired
	private EnterpriseService enterpriseService;
	/**
	 * 检查登录背景图片是否有设置
	 * @return
	 */
	@RequestMapping("/ma/loginImg/hasLoginImg.action")
	@ResponseBody 
	public Map<String , Object>  hasLoginImg() {
		Map<String , Object> map = new HashMap<String,Object>();
		map = loginImgService.hasLoginImg();
		return map;
	}
	
	/**
	 * 删除登录背景图片
	 * @return
	 */
	@RequestMapping("/ma/loginImg/deleteLoginImg.action")
	@ResponseBody 
	public Map<String , Object>  deleteLoginImg() {
		Map<String , Object> map = new HashMap<String,Object>();
		loginImgService.deleteLoginImg();
		map.put("success", true);
		return map;
	}
	
	/**
	 * 保存背景图片
	 * @param session
	 * @param em_code
	 * @param caller
	 * @param uploadItem
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ma/loginImg/save.action")
	public String upload(HttpSession session, String em_code,String caller, FileUpload uploadItem) {
		 try {
			String type=uploadItem.getFile().getContentType();
			if(!"image".equals(type.split("/")[0])){
				return "{success:false}";
			}
			String filename = uploadItem.getFile().getOriginalFilename();
			long size = uploadItem.getFile().getSize();
			if (size > 1048576) {
				return "{success:false}";
			}
			String path = FileUtil.saveFile(uploadItem.getFile(), em_code);
			int id = loginImgService.save(path, (int) size, filename, (Employee) session.getAttribute("employee"));
			return "{success: true, filepath: " + id + ",size: " + size + ",path:\"" + path + "\"}";
		} catch (Exception e) {
			e.printStackTrace();
			return "{error: '文件过大,上传失败'}";
		}
	}
	
	/**
	 * 获取登录背景照片
	 * @param response
	 * @param request
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 */
	@RequestMapping("/loginImg/getLoginImg.action")
	public void getLoginImg(HttpServletResponse response, HttpServletRequest request) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		loginImgService.getLoginImg(response,request);
	}
	

}
