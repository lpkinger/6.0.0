package com.uas.erp.service.oa;

import java.util.List;

import com.uas.erp.model.Mail;

public interface SendMailService {
	void sendMail(Mail mail);
	void saveMail(Mail mail);
	void sendSysMail(String title, String context,String tomail);
	void sendSysMail(String title, String context,String tomail,List<String> files);
	Boolean sendSysMail(String title, String context,String tomail,String files) throws Exception;
}
