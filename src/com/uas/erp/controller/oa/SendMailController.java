package com.uas.erp.controller.oa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.Mail;
import com.uas.erp.service.common.FilePathService;
import com.uas.erp.service.oa.EmployeeMailService;
import com.uas.erp.service.oa.SendMailService;

@Controller
public class SendMailController {
	@Autowired
	private SendMailService sendMailService;
	@Autowired
	private EmployeeMailService employeeMailService;
	@Autowired
	private FilePathService filePathService;
	/**
	 * 加载employees以及好友的email地址
	 */
	@RequestMapping(value="/oa/mail/getEmployeeMail.action")
	@ResponseBody
	public Map<String, Object> getTreeByParentId(){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<JSONTree> tree = employeeMailService.getJSONMail();
		modelMap.put("tree", tree);
		return modelMap;
	}
	/**
	 * 发送邮件
	 * @param mail 邮件数据
	 */
	@RequestMapping("oa/mail/send.action")  
	@ResponseBody 
	public Map<String, Object> send(String receAddr, String subject, String[] files, String context) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Mail m = new Mail();
		m.setMa_context(context);
		m.setMa_receaddr(receAddr);
		m.setMa_subject(subject);
		List<String> f = new ArrayList<String>();
		if(files != null && files.length > 0){
			for(String pathid:files){
				if(pathid != null){
					String path = filePathService.getFilepath(Integer.parseInt(pathid));
					if(path != null){
						f.add(path);
					}
				}
			}
		}
		m.setFiles(f);
		sendMailService.sendSysMail("1", "2", "3");
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 保存邮件到草稿箱
	 * @param mail 邮件数据
	 */
	@RequestMapping("oa/mail/save.action")  
	@ResponseBody 
	public Map<String, Object> save(String receAddr, String subject, String context) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Mail m = new Mail();
		m.setMa_context(context);
		m.setMa_receaddr(receAddr);
		m.setMa_subject(subject);
		sendMailService.saveMail(m);
		modelMap.put("success", true);
		return modelMap;
	}
}
