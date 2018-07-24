package com.uas.erp.service.oa.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.sun.mail.pop3.POP3Folder;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.MailDao;
import com.uas.erp.model.Mail;
import com.uas.erp.model.ReceMail;
import com.uas.erp.service.oa.ReceMailService;

@Service
public class ReceMailServiceImpl implements ReceMailService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private MailDao mailDao;

	@Override
	@Cacheable(value="mails",key="#employee.em_email + #page + #pageSize + 'getUnReadMails'")
	public List<Mail> getUnReadMails( int page, int pageSize) {
		this.checkCurrentMail();
		return mailDao.getUnReadMail(SystemSession.getUser().getEm_email(), page, pageSize);
	}
	/**
	 * 判断当前用户邮箱设置是否正确
	 */
	private void checkCurrentMail(){
		//检测当前账号下的默认邮箱
		String from = SystemSession.getUser().getEm_email();
		String regex = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
		if(from == null || !from.matches(regex)){
			BaseUtil.showError(BaseUtil.getLocalMessage("oa.mail.emailisnull"));
		}
		//检测当前账号下的默认邮箱密码
		String password = SystemSession.getUser().getEm_mailpassword();
		if(password == null || password.equals("")){
			BaseUtil.showError(BaseUtil.getLocalMessage("oa.mail.passwordisnull"));
		}
	}

	@Override
	@CacheEvict(value="mails",allEntries=true)
	public synchronized void getNewMails() {
		this.checkCurrentMail();
		try{
			Properties props = new Properties();
			//存储接收邮件服务器使用的协议
			props.setProperty("mail.store.protocol", "pop3");
			//设置接收邮件服务器的地址
			props.setProperty("mail.pop3.host", "pop3.usoftchina.com");
			props.setProperty("mail.pop3.auth", "true");
			//根据属性新建一个邮件会话.  
			Session session = Session.getInstance(props);
			//从会话对象中获得POP3协议的Store对象  
			Store store = session.getStore("pop3");
			//如果需要查看接收邮件的详细信息，需要设置Debug标志  
			session.setDebug(false);
			store.connect("pop3.usoftchina.com", SystemSession.getUser().getEm_email(), SystemSession.getUser().getEm_mailpassword());  
			POP3Folder folder = (POP3Folder)store.getFolder("INBOX");
			folder.open(Folder.READ_ONLY);
			Message[] msgs = folder.getMessages();
			//pop3协议无法判断邮件是否为已读和未读
			//每个邮件都对应了一个UID的
			//从pop3协议服务器接收邮件时，先解析UID，再从数据库找有没有对应的UID，如果没有则为未读
			FetchProfile profile = new FetchProfile();    
		    profile.add(UIDFolder.FetchProfileItem.UID);
		    folder.fetch(msgs, profile);
			try{
				ReceMail rm = null;
				Mail mail = null;
				List<Mail> mails = new ArrayList<Mail>();
				for(int i = 0;i < msgs.length ;i++){
					String uid = folder.getUID(msgs[i]);
					boolean bool = baseDao.checkByCondition("Mail", "ma_to='" + SystemSession.getUser().getEm_email() + "'" +
							" AND ma_uid='" + uid + "'");
					if(bool){//不存在，说明是新邮件
						try {
							rm = new ReceMail((MimeMessage) msgs[i]);
							mail = rm.receive(msgs[i], SystemSession.getUser(), uid);
							mail.setMa_uid(uid);
							mail.setMa_receaddr(SystemSession.getUser().getEm_email());
							mails.add(mail);
						} catch (Exception e){
							
						}
					}
				}
				mailDao.saveNewReceMail(mails);
			} finally {
				try{
					folder.close(false);
				}catch(Exception e){
					
				}
				try{
					store.close();
				}catch(Exception e){}
			}
		} catch (Exception e){
			e.printStackTrace();
			BaseUtil.showError(e.getMessage());
		}
	}
	@Override
	@Cacheable(value="mails",key="#employee.em_email + #page + #pageSize + 'getHaveReadMail'")
	public List<Mail> getHaveReadMail(
			int page, int pageSize) {
		return mailDao.getHaveReadMail(SystemSession.getUser().getEm_email(), page, pageSize);
	}
	@Override
	@Cacheable(value="mails",key="#employee.em_email + #page + #pageSize + 'getAllReceMail'")
	public List<Mail> getAllReceMail(
			int page, int pageSize) {
		return mailDao.getAllReceMail(SystemSession.getUser().getEm_email(), page, pageSize);
	}
	@Override
	@Cacheable(value="mails",key="#employee.em_email + #page + #pageSize + 'getDeletedReceMail'")
	public List<Mail> getDeletedReceMail(
			int page, int pageSize) {
		return mailDao.getDeletedReceMail(SystemSession.getUser().getEm_email(), page, pageSize);
	}
	@Override
	@Cacheable(value="mails",key="#employee.em_email + #page + #pageSize + 'getDeletedPostMail'")
	public List<Mail> getDeletedPostMail(
			int page, int pageSize) {
		return mailDao.getDeletedPostMail(SystemSession.getUser().getEm_email(), page, pageSize);
	}
	@Override
	@Cacheable(value="mails",key="#employee.em_email + #page + #pageSize + 'getPostedMail'")
	public List<Mail> getPostedMail(
			int page, int pageSize) {
		return mailDao.getPostedMail(SystemSession.getUser().getEm_email(), page, pageSize);
	}
	@Override
	@Cacheable(value="mails",key="#employee.em_email + #page + #pageSize + 'getDraftMail'")
	public List<Mail> getDraftMail(
			int page, int pageSize) {
		return mailDao.getDraftMail(SystemSession.getUser().getEm_email(), page, pageSize);
	}
	@Override
	@Cacheable(value="mail",key="#id + 'getMailDetail'")
	@CacheEvict(value="mails",allEntries=true)
	public Mail getMailDetail(int id) {
		return mailDao.getMailDetail(id);
	}
	@Override
	@CacheEvict(value="mails",allEntries=true)
	public void changeMailStatus(int id, int status) {
		mailDao.updateStatus(id, status);
	}
	@Override
	@CacheEvict(value="mails",allEntries=true)
	public void updateMailStatus(int[] id, int status) {
		for(int i:id){
			mailDao.updateStatus(i, status);
		}
	}
}
