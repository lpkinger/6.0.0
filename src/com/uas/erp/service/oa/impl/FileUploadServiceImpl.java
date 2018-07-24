package com.uas.erp.service.oa.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.stereotype.Service;

import com.uas.erp.model.Employee;
import com.uas.erp.service.oa.FileUploadService;

@Service
public class FileUploadServiceImpl implements FileUploadService{
	
	
	@Override
	public void fileUpload(File file, String language,
			Employee employee) {
		try{
			InputStream is=new FileInputStream(file);
//			String fileName = file.substring(file.lastIndexOf("\\"));
			String fileName = file.getName();
//			File fileDir=new File("D:/ERP");
//		    System.out.println("D:/ERP" + employee.getEm_uu());
//		    if (!fileDir.exists()) {
//				fileDir.mkdir();
//			}
			File fileDir=new File("D:/ERP/" + employee.getEm_uu());
//		    System.out.println("D:/ERP/" + employee.getEm_uu());
		    if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
		    File newFile=new File("D:/ERP/" + employee.getEm_uu() + "/" +  fileName);  
		    if (!newFile.exists()) {
		    	newFile.createNewFile();
			}
		    OutputStream os=new FileOutputStream(newFile);  
		    byte[] buffer=new byte[1024];  
		    int lenth=0;  
		    while(-1!=(lenth=(is.read(buffer))))  
		    {  
		         os.write(buffer,0, lenth);   
		    }  
		    os.close();  
		    is.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	

}
