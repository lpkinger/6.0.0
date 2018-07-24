package com.uas.erp.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import com.sun.mail.imap.protocol.BASE64MailboxDecoder;
import com.uas.erp.core.PathUtil;

public class ReceMail {

	private MimeMessage msg = null;
	private String saveAttchPath = "";
	private StringBuffer bodytext = new StringBuffer();
	private String dateformate = "yyyy-MM-dd HH:mm:ss";
	private String filepaths;

	public ReceMail(MimeMessage msg) {
		this.msg = msg;
	}

	public void setMsg(MimeMessage msg) {
		this.msg = msg;
	}

	/**
	 * 获取发送邮件者信息
	 * 
	 * @return
	 * @throws MessagingException
	 */
	public String getFrom() {
		try {
			InternetAddress[] address = (InternetAddress[]) msg.getFrom();
			String personal = address[0].getPersonal();
			String from = address[0].getAddress();
			if (personal != null && from != null) {
				return personal + "<" + from + ">";
			} else if (personal != null && from == null) {
				return personal;
			} else if (personal == null && from != null) {
				return from;
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取邮件收件人，抄送，密送的地址和信息。根据所传递的参数不同 "to"-->收件人,"cc"-->抄送人地址,"bcc"-->密送地址
	 * 
	 * @param type
	 * @return
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	public String getMailAddress(String type) {
		try {
			String mailaddr = "";
			String addrType = type.toUpperCase();
			InternetAddress[] address = null;

			if (addrType.equals("TO") || addrType.equals("CC") || addrType.equals("BCC")) {
				if (addrType.equals("TO")) {
					address = (InternetAddress[]) msg.getRecipients(Message.RecipientType.TO);
				}
				if (addrType.equals("CC")) {
					address = (InternetAddress[]) msg.getRecipients(Message.RecipientType.CC);
				}
				if (addrType.equals("BCC")) {
					address = (InternetAddress[]) msg.getRecipients(Message.RecipientType.BCC);
				}

				if (address != null) {
					for (int i = 0; i < address.length; i++) {
						String mail = address[i].getAddress();
						if (mail == null) {
							mail = "";
						} else {
							mail = MimeUtility.decodeText(mail);
						}
						String personal = address[i].getPersonal();
						if (personal == null) {
							personal = "";
						} else {
							personal = MimeUtility.decodeText(personal);
						}
						String compositeto = personal + "<" + mail + ">";
						mailaddr += "," + compositeto;
					}
					mailaddr = mailaddr.substring(1);
				}
			} else {
				throw new RuntimeException("Error email Type!");
			}
			return mailaddr;
		} catch (MessagingException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取邮件主题
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 */
	public String getSubject() {
		try {
			String subject = "";
			subject = MimeUtility.decodeText(msg.getSubject());
			if (subject == null) {
				subject = "";
			}
			return subject;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (MessagingException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取邮件发送日期
	 * 
	 * @return
	 * @throws MessagingException
	 */
	public String getSendDate() {
		try {
			Date sendDate = msg.getSentDate();
			SimpleDateFormat smd = new SimpleDateFormat(dateformate);
			return smd.format(sendDate);
		} catch (MessagingException e) {
			return null;
		}
	}

	/**
	 * 获取邮件正文内容
	 * 
	 * @return
	 */
	public String getBodyText() {
		return bodytext.toString();
	}

	/**
	 * 解析邮件，将得到的邮件内容保存到一个stringBuffer对象中，解析邮件 主要根据MimeType的不同执行不同的操作，一步一步的解析
	 * 
	 * @param part
	 * @throws MessagingException
	 * @throws IOException
	 */
	public void getMailContent(Part part) {
		try {
			String contentType = part.getContentType();
			int nameindex = contentType.indexOf("name");
			boolean conname = false;
			if (nameindex != -1) {
				conname = true;
			}
			if (part.isMimeType("text/plain") && !conname) {
				bodytext.append((String) part.getContent());
			} else if (part.isMimeType("text/html") && !conname) {
				bodytext.append((String) part.getContent());
			} else if (part.isMimeType("multipart/*")) {
				Multipart multipart = (Multipart) part.getContent();
				int count = multipart.getCount();
				for (int i = 0; i < count; i++) {
					getMailContent(multipart.getBodyPart(i));
				}
			} else if (part.isMimeType("message/rfc822")) {
				getMailContent((Part) part.getContent());
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 判断邮件是否需要回执，如需回执返回true，否则返回false
	 * 
	 * @return
	 * @throws MessagingException
	 */
	public boolean getReplySign() {
		try {
			boolean replySign = false;
			String needreply[] = msg.getHeader("Disposition-Notification-TO");
			if (needreply != null) {
				replySign = true;
			}
			return replySign;
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 获取此邮件的message-id
	 * 
	 * @return
	 * @throws MessagingException
	 */
	public String getMessageId() {
		try {
			return msg.getMessageID();
		} catch (MessagingException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 判断是是否包含附件
	 * 
	 * @param part
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 */
	public boolean isContainAttch(Part part) throws MessagingException, IOException {
		boolean flag = false;
		// String contentType = part.getContentType();
		try {
			if (part.isMimeType("multipart/*")) {
				Multipart multipart = (Multipart) part.getContent();
				int count = multipart.getCount();
				for (int i = 0; i < count; i++) {
					BodyPart bodypart = multipart.getBodyPart(i);
					String dispostion = bodypart.getDisposition();
					if ((dispostion != null) && (dispostion.equals(Part.ATTACHMENT) || dispostion.equals(Part.INLINE))) {
						flag = true;
					} else if (bodypart.isMimeType("multipart/*")) {
						flag = isContainAttch(bodypart);
					} else {
						String conType = bodypart.getContentType();
						if (conType.toLowerCase().indexOf("appliaction") != -1) {
							flag = true;
						}
						if (conType.toLowerCase().indexOf("name") != -1) {
							flag = true;
						}
					}
				}
			} else if (part.isMimeType("message/rfc822")) {
				flag = isContainAttch((Part) part.getContent());
			}
		} catch (Exception e) {
			flag = false;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 保存附件
	 * 
	 * @param part
	 * @throws MessagingException
	 * @throws IOException
	 */
	public void saveAttchMent(Part part) {
		try {
			String filename = "";
			if (part.isMimeType("multipart/*")) {
				Multipart mp = (Multipart) part.getContent();
				for (int i = 0; i < mp.getCount(); i++) {
					BodyPart mpart = mp.getBodyPart(i);
					String dispostion = mpart.getDisposition();
					if ((dispostion != null) && (dispostion.equals(Part.ATTACHMENT) || dispostion.equals(Part.INLINE))) {
						filename = mpart.getFileName();
						if (filename.toLowerCase().indexOf("=?utf-8?") >= 0) {// 文件名用base64加密过的
							filename = BASE64MailboxDecoder.decode(filename);// 要先用base64解密
							filename = MimeUtility.decodeText(filename);
						} else {
							if (filename.toLowerCase().indexOf("gb2312") != -1) {
								filename = MimeUtility.decodeText(filename);
							}
						}
						saveFile(filename, mpart.getInputStream());
					} else if (mpart.isMimeType("multipart/*")) {
						saveAttchMent(mpart);
					}
				}

			} else if (part.isMimeType("message/rfc822")) {
				saveAttchMent((Part) part.getContent());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得保存附件的地址
	 * 
	 * @return
	 */
	public String getSaveAttchPath() {
		return saveAttchPath;
	}

	/**
	 * 设置保存附件地址
	 * 
	 * @param saveAttchPath
	 */
	public void setSaveAttchPath(String saveAttchPath) {
		this.saveAttchPath = saveAttchPath;
	}

	/**
	 * 设置日期格式
	 * 
	 * @param dateformate
	 */
	public void setDateformate(String dateformate) {
		this.dateformate = dateformate;
	}

	/**
	 * 保存文件内容
	 * 
	 * @param filename
	 * @param inputStream
	 * @throws IOException
	 */
	private void saveFile(String filename, InputStream inputStream) throws IOException {
		String storedir = getSaveAttchPath();
		String filepath = storedir + File.separator + filename;
		File storefile = new File(filepath);
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(storefile));
			bis = new BufferedInputStream(inputStream);
			int c;
			while ((c = bis.read()) != -1) {
				bos.write(c);
				bos.flush();
			}
			if (this.filepaths == null) {
				this.filepaths = filepath;
			} else {
				this.filepaths += ";" + filepath;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			bos.close();
			bis.close();
		}
	}

	public Mail receive(Part part, Employee employee, String uid) throws MessagingException, IOException, ParseException, SQLException {
		Mail mail = new Mail();
		mail.setMa_from(getFrom());
		boolean flag = isContainAttch(part);
		mail.setAttch(flag);
		mail.setReply(getReplySign());
		getMailContent(part);
		String subject = getSubject();
		mail.setMa_subject(subject);
		mail.setMa_context(getBodyText());
		mail.setMa_senddate(getSendDate());
		String path = PathUtil.getMailPath() + File.separator + "receattach";
		if (flag) {
			File file = new File(path);
			if (!file.exists()) {
				file.mkdir();
			} else {
				path = path + File.separator + employee.getEm_id();
				file = new File(path);
				if (!file.exists()) {
					file.mkdir();
				} else {
					path = path + File.separator + uid;
					file = new File(path);
					if (!file.exists()) {
						file.mkdir();
					}
				}
			}
			setSaveAttchPath(path);
			saveAttchMent(part);
			mail.setMa_attach(this.filepaths);
		}
		return mail;
	}

	/**
	 * 设置邮箱分组
	 * 
	 * @throws MessagingException
	 * @throws ParseException
	 */
	public String getMailGroup() throws MessagingException, ParseException {
		return CalcDay.getCalcDay(msg.getSentDate());
	}
}