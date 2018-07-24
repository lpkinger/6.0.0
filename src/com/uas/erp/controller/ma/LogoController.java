package com.uas.erp.controller.ma;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.FileUpload;
import com.uas.erp.service.ma.LogoService;

/**
 * 系统logo设置
 * */
@Controller
@RequestMapping("ma/logo")
public class LogoController {
	@Autowired
	private LogoService logoService;
	@RequestMapping("/save.action")
	public @ResponseBody String  upload(HttpSession session,  FileUpload uploadItem) {
		try {			
			String type=uploadItem.getFile().getContentType();
			if(!"image".equals(type.split("/")[0])){
				return "{success:false}";
			}
			long size = uploadItem.getFile().getSize();
			if (size > 8000) {
				return "{success:false}";
			}
			logoService.saveLogo(uploadItem.getFile());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "{success:true}";
	}
	/*saas初始化企业logo上传*/
	@RequestMapping("/saveSaasLogo.action")
	@ResponseBody
	public  String saveSaasLogo(HttpSession session,  FileUpload uploadItem) {
		try {			
			String type=uploadItem.getFile().getContentType();
			if(!"image".equals(type.split("/")[0])){
				return "{success:false}";
			}
			long size = uploadItem.getFile().getSize();
			if (size > 8000) {
				return "{success:false}";
			}
			if(logoService.hasLogo()){
				logoService.del();
			};
			logoService.saveLogo(uploadItem.getFile());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "{success:true}";
	}
	@RequestMapping("/hasLogo.action")
	@ResponseBody
	public  boolean hasLogo(HttpSession session) {
		return logoService.hasLogo();
	}
	@RequestMapping("/get.action")
	@ResponseBody
	public void getLogo(HttpServletRequest request, HttpServletResponse response){
		OutputStream os = null;
		ByteArrayInputStream bais =null;
		try {
			os = response.getOutputStream();
			response.setCharacterEncoding("utf-8");
			response.setContentType("image/jpeg");
			byte[] bytes=logoService.getLogo();
			if(bytes!=null){
				bais= new ByteArrayInputStream(bytes);    
				BufferedImage bi =ImageIO.read(bais);  
				ImageIO.write(bi, "png", os);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				os.flush();
				os.close();
				bais.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	@RequestMapping("/del.action")
	@ResponseBody
	public void del(){
		logoService.del();
	}

}
