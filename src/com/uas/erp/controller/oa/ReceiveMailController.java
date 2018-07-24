package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.model.Mail;
import com.uas.erp.service.oa.ReceMailService;

@Controller
public class ReceiveMailController {
	@Autowired
	private ReceMailService receMailService;

	/**
	 * 从数据库加载未读邮件
	 */
	@RequestMapping(value = "/oa/mail/getUnReadMail.action")
	@ResponseBody
	public Map<String, Object> getUnReadMail(String caller, int page,
			int pageSize) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Mail> mails = receMailService.getUnReadMails(page, pageSize);
		if (mails == null || mails.size() == 0) {
			receMailService.getNewMails();
			mails = receMailService.getUnReadMails(page,pageSize);
		}
		modelMap.put("mails", mails);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 从数据库加载已读邮件
	 */
	@RequestMapping(value = "/oa/mail/getReadMail.action")
	@ResponseBody
	public Map<String, Object> getReadMail(String caller, int page, int pageSize) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Mail> mails = receMailService.getHaveReadMail(page, pageSize);
		modelMap.put("mails", mails);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 从数据库加载所有收到的邮件
	 */
	@RequestMapping(value = "/oa/mail/getAllReceMail.action")
	@ResponseBody
	public Map<String, Object> getAllReceMail(String caller, int page,
			int pageSize) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Mail> mails = receMailService.getAllReceMail(
				page, pageSize);
		if (mails == null || mails.size() == 0) {
			receMailService.getNewMails();
			mails = receMailService.getAllReceMail(page,pageSize);
		}
		modelMap.put("mails", mails);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 从数据库加载所有删除的收到的邮件
	 */
	@RequestMapping(value = "/oa/mail/getDeletedReadMail.action")
	@ResponseBody
	public Map<String, Object> getDeletedReceMail(String caller, int page,
			int pageSize) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Mail> mails = receMailService.getDeletedReceMail( page, pageSize);
		modelMap.put("mails", mails);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 从数据库加载所有删除的发送的邮件
	 */
	@RequestMapping(value = "/oa/mail/getDeletedPostMail.action")
	@ResponseBody
	public Map<String, Object> getDeletedPostMail(String caller, int page,
			int pageSize) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Mail> mails = receMailService.getDeletedPostMail( page, pageSize);
		modelMap.put("mails", mails);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 从数据库加载所有已发送的邮件
	 */
	@RequestMapping(value = "/oa/mail/getPostedMail.action")
	@ResponseBody
	public Map<String, Object> getPostedMail(String caller, int page,
			int pageSize) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Mail> mails = receMailService.getPostedMail(page, pageSize);
		if (mails == null || mails.size() == 0) {
			receMailService.getNewMails();
			mails = receMailService.getPostedMail( page,pageSize);
		}
		modelMap.put("mails", mails);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 从数据库加载所有草稿箱的邮件
	 */
	@RequestMapping(value = "/oa/mail/getDraftMail.action")
	@ResponseBody
	public Map<String, Object> getDraftMail(String caller, int page,
			int pageSize) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Mail> mails = receMailService.getDraftMail(page, pageSize);
		modelMap.put("mails", mails);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 从数据库加载指定邮件的详细信息
	 */
	@RequestMapping(value = "/oa/mail/getMailDetail.action")
	@ResponseBody
	public Map<String, Object> getMailDetail(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Mail mail = receMailService.getMailDetail(id);
		modelMap.put("mail", mail);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 从邮件服务器读取未读邮件
	 */
	@RequestMapping(value = "/oa/mail/getNewMail.action")
	@ResponseBody
	public Map<String, Object> getNewMails(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		receMailService.getNewMails();
		return modelMap;
	}

	/**
	 * 修改邮件status
	 * 
	 * @param id
	 *            邮件ID
	 * @param status
	 *            状态码
	 */
	@RequestMapping(value = "/oa/mail/changeMailStatus.action")
	@ResponseBody
	public Map<String, Object> changeMailStatus(String caller, int id,
			int status) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		receMailService.changeMailStatus(id, status);
		return modelMap;
	}

	/**
	 * 修改邮件status
	 * 
	 * @param id
	 *            邮件ID
	 * @param status
	 *            状态码
	 */
	@RequestMapping(value = "/oa/mail/updateMailStatus.action")
	@ResponseBody
	public Map<String, Object> updateMailStatus(String caller, int[] id,
			int status) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		receMailService.updateMailStatus(id, status);
		return modelMap;
	}
}
