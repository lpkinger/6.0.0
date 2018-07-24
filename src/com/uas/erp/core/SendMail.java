package com.uas.erp.core;

import java.util.Properties;
import java.util.Random;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/*import com.uas.mail.SendMail;*/

/**
 * 本类通过SMTP协议发送邮件到指定邮箱地址
 * 
 * @since 优软内部系统1.0
 */
public class SendMail {
	
	/**
	 * MIME邮件对象
	 */
	private MimeMessage mimeMsg;
	
	/**
	 * 邮件会话对象
	 */
	private Session session;
	
	/**
	 * 系统属性
	 */
	private Properties props;
	
	/**
	 * SMTP是否需要认证
	 */
//	private boolean needAuth = false;
	
	/**
	 * SMTP认证时需要的用户名
	 */
	private String username = "";
	
	/**
	 * SMTP认证时需要的密码
	 */
	private String password = "";
	
	/**
	 * Multipart对象，邮件内容，标题，附件等内容均添加到其中后再生成MimeMessage对象
	 */
	private Multipart mp;
	
	/**
	 * 发送邮件类的构造方法，不带任何参数
	 */
	public SendMail() {
	}
	
	/**
	 * 发邮件类的构造方法，带参数
	 * @param smtp SMTP主机地址
	 */
	public SendMail(String smtp) {
		setSmtpHost(smtp);
		createMimeMessage();	
	}
	
	/**
	 * 设置SMTP服务器的set方法
	 * @param hostName SMTP主机地址
	 */
	public void setSmtpHost(String hostName) {
		if(props == null)
			
			//获取系统属性对象
			props = System.getProperties();
		
		//设置SMTP主机
		props.put("mail.smtp.host", hostName);
	}
	
	/**
	 * 创建MIME邮件对象
	 * @return true 创建成功; false 创建失败
	 */
	public boolean createMimeMessage() {
		
		try{
			//获取邮件会话对象
			session = Session.getDefaultInstance(props, null);
		} catch (Exception e) {
			System.err.println("获取邮件会话对象时发生错误！" + e);
			return false;
		}
		
		try{
			//创建MIME邮件对象
			mimeMsg = new MimeMessage(session);
			mp = new MimeMultipart();
			
			return true;
		} catch (Exception e) {
			System.err.println("创建MIME邮件对象失败！" + e);
			return false;
		}
	}
	
	/**
	 * 是否需要设置SMTP身份认证
	 * @param need 是否需要认证
	 */
	public void setNeedAuth(boolean need) {
		if (props == null)
			props = System.getProperties();
		
		if (need) {
			props.put("mail.smtp.auth", "true");
		} else {
			props.put("mail.smtp.auth", "false");
		}
	}
	
	/**
	 * 设置用户名和密码
	 * @param name 用户名
	 * @param pass 密码
	 */
	public void setNamePass(String name,String pass) {
		username = name;
		password = pass;	
	}
	
	/**
	 * 设置邮件主题
	 * @param mailSubject 邮件主题
	 * @return
	 */
	public boolean setSubject(String mailSubject) {
		try {
			mimeMsg.setSubject(mailSubject);
			return true;
		} catch (Exception e) {
			System.err.println("设置邮件主题发生错误！");
			return false;
		}
		 
	}
	
	/**
	 * 设置邮件正文
	 * @param mailBody 邮件正文
	 * @return
	 */
	public boolean setBody(String mailBody) {
		try{
			BodyPart bp = new MimeBodyPart();
			bp.setContent("<meta http-equiv=Content-Type content=text/html;charset=gb2312>" + mailBody, 
					"text/html;charset=GB2312");
			mp.addBodyPart(bp);
			return true;	
		} catch (Exception e) {
			System.err.println("设置邮件正文时发生错误！" + e);
			return false;	
		}
		
	}
	
	/**
	 * 是否添加附件
	 * @param filename 文件名称
	 * @return
	 */
	public boolean addFileAffix (String filename) {
		try{
			BodyPart bp = new MimeBodyPart();
			FileDataSource fileds = new FileDataSource(filename);
			bp.setDataHandler(new DataHandler(fileds));
			bp.setFileName(fileds.getName());
			mp.addBodyPart(bp);
			return true;
		} catch (Exception e) {
			System.err.println("增加邮件附件：" + filename+"发生错误！" + e);
			return false;	
		}
	}
	
	/**
	 * 设置发件人
	 * @param from 发件人
	 * @return
	 */
	public boolean setFrom (String from) {
		try{
			//设置发信人
			mimeMsg.setFrom(new InternetAddress(from));
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	/**
	 * 设置收件人
	 * @param to 收件人
	 * @return
	 */
	public boolean setTo (String to) {
		if (to == null)
			return false;
		try {
			mimeMsg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 添加抄送
	 * @param copyto 抄送
	 * @return
	 */
	public boolean setCopyTo (String copyto) {
		if(copyto == null)
			return false;
		try{
			mimeMsg.setRecipients(Message.RecipientType.CC, (Address[])InternetAddress.parse(copyto));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 发送邮件
	 * @return
	 */
	public boolean sendout() {
		try{
			mimeMsg.setContent(mp);
			mimeMsg.saveChanges();
			Session mailsSession = Session.getInstance(props, null);
			Transport transport = mailsSession.getTransport("smtp");
			transport.connect((String)props.get("mail.smtp.host"), username, password);
			transport.sendMessage(mimeMsg, mimeMsg.getRecipients(Message.RecipientType.TO));
//			transport.send(mimeMsg);
			transport.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 发给企业邮箱的欢迎使用邮件
	 * 
	 * @param mailAddress 企业邮箱地址
	 */
	public void sendMailto (String mailAddress) {
		String mailbody = "<meta http-equiv=Content-Type content=text/html;charset=gb2312>"+
				"<div align=center>欢迎使用<b><a href=http://www.usoftchina.com>深圳市优软科技有限公司</b>"+"产品</div>";
		
					SendMail themail = new SendMail("smtp.usoftchina.com");
					themail.setNeedAuth(true);
					if(themail.setSubject("欢迎使用优软产品") == false) return;
					if(themail.setBody(mailbody) == false) return;
					if(themail.setTo(mailAddress) == false) return;
					if(themail.setFrom("lirj@usoftchina.com") == false) return;
					//选择是否使用附件
//					if(themail.addFileAffix("d:\\test.java") == false) return;
					themail.setNamePass("lirj@usoftchina.com", "lrj610326");
					if(themail.sendout() == false) return;
	}
	/**
	 * 发给用户的账号激活邮件
	 * 
	 * @param mailAddress 用户的邮箱地址
	 * @param username 用户注册使用的用户名
	 * @param validateCode 系统产生的随机验证码
	 */
	public void sendMailto(String mailAddress,String username,String validateCode) {
			
		String mailbody = "<meta http-equiv=Content-Type content=text/html;charset=gb2312>"+
				"<h>亲：</h><br/>"+"<p>    您正在注册使用优软产品，可以使用下面链接激活账号。</p><br/>"+
		"<p>点击下面链接激活账号，48小时有效，否则重新注册账号，链接只能使用一次，请尽快激活!</p>"+
		"<br/><a href=http://localhost:8080/B2B/efficiencyAction?username="+username+"&validateCode="+validateCode+">点击这里</a><br/>";
					SendMail themail = new SendMail("smtp.usoftchina.com");
					themail.setNeedAuth(true);
					if(themail.setSubject("优软软件注册账号邮件激活") == false) return;
					if(themail.setBody(mailbody) == false) return;
					if(themail.setTo(mailAddress) == false) return;
					if(themail.setFrom("lirj@usoftchina.com") == false) return;
					//选择是否使用附件
//					if(themail.addFileAffix("d:\\test.java") == false) return;
					themail.setNamePass("lirj@usoftchina.com", "mima");
					if(themail.sendout() == false) return;	
	}
	
	
	
	public String getRandom(){
		Random random = new Random();
		//存放随机产生的8位整数
		String n = "";	
		//用来取随机数
		int randomGet;
		do {
			//产生48到57的随机数（0-9的键位值）
			randomGet = Math.abs(random.nextInt())%10 + 48;
			//产生97到122的随机数（a-z的键位值）
//			randomGet = Math.abs(random.nextInt())%26 + 97;
			char num = (char)randomGet;
			String d = Character.toString(num);
			n += d;
		} while (n.length() < 8);

		//查询随机产生的8位随机数n的值
		return n;
	}
	
}
