package com.uas.erp.core.support;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

/**
 *@author zhouy
 *手机登陆 支持离线登陆
 **/
public class MobileSessionContext {
	private static  MobileSessionContext mobileSessionContext;
	private Map<String,HttpSession> sessionMap;
	private MobileSessionContext() {     
		sessionMap = new HashMap<String, HttpSession>();     
    }     
	public static MobileSessionContext getInstance(){
		if(mobileSessionContext==null){
			mobileSessionContext=new MobileSessionContext();
		}
		return mobileSessionContext;
	}
	public synchronized void createSession(HttpSession session){
	   if(session!=null) 
		 sessionMap.put(session.getId(), session);
	}
	public synchronized void destroySession(HttpSession session){
		if(session!=null)  sessionMap.remove(session.getId());
	}
	public synchronized HttpSession getSessionById(String sessionId){
		return sessionMap.get(sessionId);
	}
}
