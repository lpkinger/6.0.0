package com.uas.erp.controller.common;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.uas.erp.model.FileUpload;
import com.uas.erp.service.common.BatchUpdateService;
@Controller("batchUpdateController")
public class BatchUpdateController {
	
	@Autowired
	private BatchUpdateService batchUpdateService;
	@RequestMapping("/common/update/batchUpdate.action") 
	public @ResponseBody String upexcel(FileUpload uploadItem,String caller) {
		InputStream is = null;
		try{
			CommonsMultipartFile file = uploadItem.getFile();
			long size = file.getSize();
			if(size > 104857600){
				return new String(("{error: '文件过大', size:" + size + "}").getBytes("utf-8"), "iso8859-1");
			} else {
				String ft = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
				is = uploadItem.getFile().getInputStream();
				
				 String str="";
				if (ft.equals("xls")) {
					HSSFWorkbook wbs = new HSSFWorkbook(is);  
		            HSSFSheet sheet = wbs.getSheetAt(0);
		           str=batchUpdateService.importExcel(caller, sheet);
				}else{
					return new String("{error: 'excel文件的格式不太规范,导入失败<hr>可以尝试将文件另存为.xls,然后导入'}".getBytes("utf-8"), "iso8859-1");
				}
				
				return new String(("{success: true,data:'"+str+"'}").getBytes("utf-8"),"iso8859-1");
			}
		} catch (Exception e){
			e.printStackTrace();
			try {
				return new String("{error: 'excel文件的格式不太规范,导入失败<hr>可以尝试将文件另存为.xls,然后导入'}".getBytes("utf-8"), "iso8859-1");
			} catch (UnsupportedEncodingException e1) {
				return "{success: false}";
			}
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				
			}
		}
	}
}
