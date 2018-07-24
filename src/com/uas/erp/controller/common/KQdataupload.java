package com.uas.erp.controller.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.common.KQdatauploadService;


@Controller
public class KQdataupload {
	
	@Autowired
	KQdatauploadService kqservice;
	
	@RequestMapping("/windowsdatatolinux/import.action")
	@ResponseBody
	public String upload(String data,String master){
		String result="false";
		result=kqservice.upload(data, master);
		return result;
	}
	
	
	
	
	
	
}
