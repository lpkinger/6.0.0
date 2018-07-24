package com.uas.erp.controller.common;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.support.MobileSessionContext;
import com.uas.erp.model.Employee;

@Controller
public class InterfaceSessionController {
	
	/**
	 * 获取用户sessionID,用于跳转至ERP_SERVE项目页面
	 * @param req
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("/common/redirect.action")
	@ResponseBody
	public void getSession(HttpServletRequest req,HttpServletResponse res, String page) throws IOException{	
		HttpSession se = req.getSession();
		MobileSessionContext context = MobileSessionContext.getInstance();
		context.destroySession(se);
		context.createSession(se);
		String sessionID = se.getId();
		//重定向
		page = "http://"+req.getServerName()+":"+req.getServerPort()+"/ERP_SERVE/"+page;
		res.sendRedirect(page+"?token="+sessionID);
	}
	
	@RequestMapping("/common/checkSession.action")
	@ResponseBody
	public Employee checkSession(HttpServletRequest req,HttpServletResponse res,String token) throws IOException{
		MobileSessionContext context = MobileSessionContext.getInstance();
		HttpSession se = context.getSessionById(token);
		if(se != null){
			/*context.destroySession(se);*/
			return (Employee) se.getAttribute("employee");
		}
		return null;
	}
	
	/**
	 * 将session中的employee写入/ERP中的token中
	 * @param req
	 * @param res
	 * @param page
	 * @throws IOException
	 */
	@RequestMapping("/common/redirectTest.action")
	@ResponseBody
	public void redirectTest(HttpServletRequest req,HttpServletResponse res, String page) throws IOException{	
		String token = UUID.randomUUID().toString().replaceAll("\\-", "");
		//随机数
		ServletContext ContextA =req.getSession().getServletContext(); 
		ContextA.setAttribute(token, req.getSession());
		//重定向
		page = "http://"+req.getServerName()+":"+req.getServerPort()+"/ERP_SERVE/"+page;
		res.sendRedirect(page+"?token="+token);
	}
}
