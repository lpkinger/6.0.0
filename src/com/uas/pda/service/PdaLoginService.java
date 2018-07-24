package com.uas.pda.service;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


public interface PdaLoginService {
	public String login(String master,String j_username,String j_password, String ip,  boolean isMobile,HttpSession session);
	public String logout(HttpServletRequest request);
	
}
