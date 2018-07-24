package com.uas.erp.service.oa.impl;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Enterprise;
import com.uas.erp.model.Mail;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.oa.SendMailService;

@Component
public class SendMailServciceImpl implements SendMailService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private EnterpriseService enterpriseService;
	//@Autowired
	//private EmployeeDao employeeDao;
	private static JavaMailSenderImpl  mailSender;
    static {
    	 if(mailSender == null){    		 
    		 mailSender = new JavaMailSenderImpl();
    		 mailSender.setHost("smtp.usoftchina.com"); 
    		 mailSender.setPort(25);
    		 Properties p = new Properties();
    		 p.setProperty("mail.smtp.auth", "true");  
    		 mailSender.setJavaMailProperties(p);
    	 }
     }
	@Override
	public void sendMail(final Mail mail) {
		//检测当前账号下的默认邮箱
		String regex = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
		String email=baseDao.getDBSetting("email");
		String password=baseDao.getDBSetting("password");
		String emailsmtp=baseDao.getDBSetting("emailsmtp");
		if((!"".equals(email)&&!"null".equals(email))||(!"".equals(password)&&!"null".equals(password))||(!"".equals(emailsmtp)&&!"null".equals(emailsmtp))){
			
		}else{
			BaseUtil.showError("请先联系管理设置默认发件邮箱！");
		}
		mail.setMa_from(email);
		mailSender.setUsername(email);
		mailSender.setHost(emailsmtp);
		//检测当前账号下的默认邮箱密码
		//password = employee.getEm_mailpassword();
		if(password == null || password.equals("")){
			BaseUtil.showError(BaseUtil.getLocalMessage("oa.mail.passwordisnull"));
		}
		mailSender.setPassword(password);
		//设置邮件
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper;
		StringBuffer error = new StringBuffer();
		try {
			helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			// 邮箱名必须是已注册的，后面可以添加一个展示名，不添加默认为邮箱名  
			helper.setFrom(mail.getMa_from(), "优软OA系统(" + SystemSession.getUser().getEm_name() + ")");  
			helper.setSubject(mail.getMa_subject());  
			helper.setText(mail.getMa_context(), true); 
			helper.setSentDate(new Date());
			FileSystemResource file;  
            for(String s:mail.getFiles())//添加附件  
            {  
               file = new FileSystemResource(new File(s));//读取附件  
               if(file.exists()){
            	   helper.addAttachment(file.getFilename(), file);//向email中添加附件
               }
            }
            //发送
            String to = mail.getMa_receaddr();
            //EmployeeMail employeeMail = null;
            if(to != null && to.contains(";")){//多个邮件用;隔开
            	for(String t:to.split(";")){
            		if(t.length() > 0){
            			if(t.matches(regex)){
                			helper.setTo(t);
                			mailSender.send(mimeMessage);
     /*           			employeeMail = new EmployeeMail();
                			employeeMail.setEmm_emid(employee.getEm_id());
                			employeeMail.setEmm_friendmail(to);
                			employeeDao.saveEmployeeMail(employeeMail);*/
                		} else {
                			error.append(BaseUtil.getLocalMessage("oa.mail.emailiswrong") + "(" + t + ")<br/>");
                		}
            		}
            	}
            } else {
            	if(to.matches(regex)){
        			helper.setTo(to);
        			mailSender.send(mimeMessage);
      /*  			employeeMail = new EmployeeMail();
        			employeeMail.setEmm_emid(employee.getEm_id());
        			employeeMail.setEmm_friendmail(to);
        			employeeDao.saveEmployeeMail(employeeMail);*/
        		} else {
        			error.append(BaseUtil.getLocalMessage("oa.mail.emailiswrong") + "(" + to + ")");
        		}
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(error != null && error.length() > 0){
				BaseUtil.showError(error.toString());
			}
		}
	}
	@Override
	public void saveMail(Mail mail) {
		
	}
	
	/**
	 * 系统自动发送邮件
	 * @param mail 邮件数据
	 */	
	@Override
	public void sendSysMail(String title, String context,String tomail) {
		//title="邮件发送测试";
		//context="测试一下下";
		//tomail="68778130@qq.com";		
		String regex = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
		String email=baseDao.getDBSetting("Purchase", "SetEmail");
		String password=baseDao.getDBSetting("Purchase","SetEmailPassword");
		String emailsmtp=baseDao.getDBSetting("Purchase","SetEmailServer");
		Enterprise enterprise =enterpriseService.getEnterprise();
		String fromname=enterprise.getEn_Shortname();
		if((!"".equals(email)&&!"null".equals(email))||(!"".equals(password)&&!"null".equals(password))||(!"".equals(emailsmtp)&&!"null".equals(emailsmtp))){
			
		}else{
			BaseUtil.showError("请先联系管理设置默认发件邮箱！");
		}		
		mailSender.setUsername(email);
		mailSender.setPassword(password);
		mailSender.setHost(emailsmtp);
		//设置邮件
				MimeMessage mimeMessage = mailSender.createMimeMessage();
				MimeMessageHelper helper;
				StringBuffer error = new StringBuffer();
				try {
					helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
					// 邮箱名必须是已注册的，后面可以添加一个展示名，不添加默认为邮箱名  
					helper.setFrom(email, fromname); //展示名不能为空！
					helper.setSubject(title);  
					helper.setText(context, true); 
					helper.setSentDate(new Date());				
		            if(tomail != null && tomail.contains(";")){//多个邮件用;隔开
		            	for(String t:tomail.split(";")){
		            		if(t.length() > 0){
		            			if(t.matches(regex)){
		                			helper.setTo(t);
		                			mailSender.send(mimeMessage);		  
		                		} else {
		                			error.append(BaseUtil.getLocalMessage("oa.mail.emailiswrong") + "(" + t + ")<br/>");
		                		}
		            		}
		            	}
		            } else {
		            	if(tomail.matches(regex)){
		        			helper.setTo(tomail);
		        			mailSender.send(mimeMessage);		     
		        		} else {
		        			error.append(BaseUtil.getLocalMessage("oa.mail.emailiswrong") + "(" + tomail + ")");
		        		}
		            }
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if(error != null && error.length() > 0){
						BaseUtil.showError(error.toString());
					}
				}
	}
	
	/**
	 * 发带附件的邮件
	 * @param title
	 * 			邮件标题
	 * @param context
	 * 			邮件内容
	 * @param tomail
	 * 			邮件接收人
	 * @param files
	 * 			附件
	 */
	public void sendSysMail(String title, String context,String tomail,List<String> files){
		String regex = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
		String email=baseDao.getDBSetting("sendEmail", "SetEmail");
		String password=baseDao.getDBSetting("sendEmail","SetEmailPassword");
		String emailsmtp=baseDao.getDBSetting("sendEmail","SetEmailServer");
		Enterprise enterprise =enterpriseService.getEnterprise();
		String fromname=enterprise.getEn_Shortname();
		if((!"".equals(email)&&!"null".equals(email))||(!"".equals(password)&&!"null".equals(password))||(!"".equals(emailsmtp)&&!"null".equals(emailsmtp))){
			
		}else{
			BaseUtil.showError("请先联系管理设置默认发件邮箱！");
		}		
		mailSender.setUsername(email);
		mailSender.setPassword(password);
		mailSender.setHost(emailsmtp);
		//设置邮件
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper;
		StringBuffer error = new StringBuffer();
		try {
			helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			// 邮箱名必须是已注册的，后面可以添加一个展示名，不添加默认为邮箱名  
			helper.setFrom(email, fromname); //展示名不能为空！
			if(title == null || "".equals(title)){
				helper.setSubject(" ");
			}
			helper.setSubject(title); 
			if(context == null || "".equals(context)){
				helper.setText(" ", true); 
			}
			helper.setText(context, true); 
			helper.setSentDate(new Date());	
			//添加附件
			for(String filepath: files){
				FileSystemResource file = new FileSystemResource(filepath);
				helper.addAttachment(file.getFilename(), file);
			}
            if(tomail != null && tomail.contains(";")){//多个邮件用;隔开
            	for(String t:tomail.split(";")){
            		if(t.length() > 0){
            			if(t.matches(regex)){
                			helper.setTo(t);
                			mailSender.send(mimeMessage);		  
                		} else {
                			error.append(BaseUtil.getLocalMessage("oa.mail.emailiswrong") + "(" + t + ")<br/>");
                		}
            		}
            	}
            } else {
            	if(tomail.matches(regex)){
        			helper.setTo(tomail);
        			mailSender.send(mimeMessage);		     
        		} else {
        			error.append(BaseUtil.getLocalMessage("oa.mail.emailiswrong") + "(" + tomail + ")");
        		}
            }
		} catch (Exception e) {
			if(e instanceof MailSendException){
				BaseUtil.showError("邮件发送失败!");
			}
			e.printStackTrace();
		} finally {
			if(error != null && error.length() > 0){
				BaseUtil.showError(error.toString());
			}
		}
	}
	
	public Boolean sendSysMail(String title, String context,String tomail,String files) throws Exception{
		String email=baseDao.getDBSetting("batchMail", "SetEmail");
		String password=baseDao.getDBSetting("batchMail","SetEmailPassword");
		String emailsmtp=baseDao.getDBSetting("batchMail","SetEmailServer");
		Enterprise enterprise =enterpriseService.getEnterprise();
		String fromname=enterprise.getEn_Shortname();
		
		if((email==null||"".equals(email)) || (password==null||"".equals(password)) || (emailsmtp==null||"".equals(emailsmtp))){
			email = baseDao.getDBSetting("Purchase", "SetEmail");
			password=baseDao.getDBSetting("Purchase","SetEmailPassword");
			emailsmtp=baseDao.getDBSetting("Purchase","SetEmailServer");
		}
		
		if((email!=null&&!"".equals(email)&&!"null".equals(email))||(password!=null&&!"".equals(password)&&!"null".equals(password))||(emailsmtp!=null&&!"".equals(emailsmtp)&&!"null".equals(emailsmtp))){
			
		}else{
			throw new Exception("请先联系管理设置默认发件邮箱！");
		}		
		mailSender.setUsername(email);
		mailSender.setPassword(password);
		mailSender.setHost(emailsmtp);
		//设置邮件
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper;
		StringBuffer error = new StringBuffer();
		try {
			helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			// 邮箱名必须是已注册的，后面可以添加一个展示名，不添加默认为邮箱名  
			helper.setFrom(email, fromname); //展示名不能为空！
			if(title == null || "".equals(title)){
				helper.setSubject(" ");
			}
			helper.setSubject(title); 
			if(context == null || "".equals(context)){
				helper.setText(" ", true); 
			}
			helper.setText(context, true); 
			helper.setSentDate(new Date());	
			//添加附件
			if(files != null && !"".equals(files)){
				JSONArray array = JSON.parseArray(files);
				for(int i = 0; i < array.size(); i++){
					FileSystemResource file = new FileSystemResource(array.getJSONObject(i).getString("filepath"));
					helper.addAttachment(array.getJSONObject(i).getString("filename"), file);
				}
			}
			//添加收件人
			@SuppressWarnings("static-access")
			InternetAddress[] internetAddressTo = new InternetAddress().parse(tomail.replaceAll(";", ",")); 
        	helper.setTo(internetAddressTo);
        	mailSender.send(mimeMessage);
        	return true;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
}
