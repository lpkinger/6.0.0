package com.uas.erp.controller.common;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Employee;
import com.uas.erp.service.common.ProcessTransferService;
@Controller
public class ProcessTransferController {
	@Autowired
	private ProcessTransferService processTransferService;
	@RequestMapping("/common/saveProcessTransfer.action")
	@ResponseBody
	public Map<String,Object> saveProcessTransfer(HttpSession session,String formStore){
		String language=(String)session.getAttribute("language");
		Employee employee=(Employee)session.getAttribute("employee");
        processTransferService.saveProcessTransfer(formStore, language, employee);
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("success",true);
        return map;
	}
	@RequestMapping("/common/updateProcessTransfer.action")
	@ResponseBody
	public Map<String,Object> updateProcessTransfer(HttpSession session,String formStore){
		String language=(String)session.getAttribute("language");
		Employee employee=(Employee)session.getAttribute("employee");
        processTransferService.updateProcessTransfer(formStore, language, employee);
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("sucess",true);
        return map;
	}
	@RequestMapping("/common/deleteProcessTransfer.action")
	@ResponseBody
	public Map<String,Object> deleteProcessTransfer(HttpSession session,int id){
		String language=(String)session.getAttribute("language");
		Employee employee=(Employee)session.getAttribute("employee");
        processTransferService.deleteProcessTransfer(id, language, employee);
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("sucess",true);
        return map;
	}
	@RequestMapping("/common/disabledProcessTransfer.action")
	@ResponseBody
	public Map<String,Object> disabledProcessTransfer(HttpSession session,int id){
		String language=(String)session.getAttribute("language");
		Employee employee=(Employee)session.getAttribute("employee");
        processTransferService.disabledProcessTransfer(id, language, employee);
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("success",true);
        return map;
	}
	@RequestMapping("/common/abledProcessTransfer.action")
	@ResponseBody
	public Map<String,Object> abledProcessTransfer(HttpSession session,int id){
		String language=(String)session.getAttribute("language");
		Employee employee=(Employee)session.getAttribute("employee");
        processTransferService.abledProcessTransfer(id, language, employee);
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("success",true);
        return map;
	}
}
