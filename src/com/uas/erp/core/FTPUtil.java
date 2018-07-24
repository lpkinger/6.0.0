package com.uas.erp.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class FTPUtil {
	 private static FTPClient ftp = new FTPClient();
	 public static boolean uploadFile(String url,int port,String username, String password, String path, String filename, InputStream input) throws Exception  {
		 	boolean success = false;
		    boolean connect = false;
		    boolean directory = false;
		    try {
			    int reply;
			    ftp.connect(url, port);//连接FTP服务器
			    //如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
			    ftp.login(username, password);//登录
			    reply = ftp.getReplyCode();
			    if (!FTPReply.isPositiveCompletion(reply)) {
			    	ftp.disconnect();
			    	connect = true;
			    }
			    ftp.setFileType(FTP.BINARY_FILE_TYPE);
			    ftp.setBufferSize(1024*1024*10);
			    if (!ftp.changeWorkingDirectory(path)) {  
	                //如果目录不存在创建目录  
	                String[] dirs = path.split("/");  
	                String tempPath = "";  
	                for (String dir : dirs) {  
	                    if (null == dir || "".equals(dir)) continue;
	                    dir = new String(dir.getBytes("UTF-8"),"iso-8859-1");
	                    tempPath += "/" + dir;
	                    if (!ftp.changeWorkingDirectory(tempPath)) {  
	                        if (!ftp.makeDirectory(tempPath)) {
	                        	directory = true;
	                        } else {  
	                            ftp.changeWorkingDirectory(tempPath);  
	                        }  
	                    }  
	                }
	            } 
			    boolean b = ftp.storeFile(filename, input);
			    input.close();
			    ftp.logout();
			    if(connect){
			    	BaseUtil.showError("连接FTP服务器失败!");
			    }
			    if(directory){
			    	BaseUtil.showError("FTP服务器创建目录失败!");
			    }
			    if(!b){
			    	BaseUtil.showError("文件上传失败!");
			    }
			    success = true;
		    } catch (IOException e) {
		    	e.printStackTrace();
			} finally {
			    if (ftp.isConnected()) {
				    try {
				    	ftp.disconnect();
				    } catch (IOException ioe) {
				    }
			    }
			}
		    return success;
	    }
	 
	public static boolean deleteFtpFile(String url, int port, String username, String password, String remotePath, String fileName){  
	        boolean success = false;
	        FTPClient ftp = new FTPClient();
	        try
	        {
	            int reply;
	            // 连接FTP服务器
	            if (port > -1){
	                ftp.connect(url, port);
	            }else{
	                ftp.connect(url);
	            }
	            // 登录
	            ftp.login(username, password);
	            reply = ftp.getReplyCode();
	            if (!FTPReply.isPositiveCompletion(reply)){
	                ftp.disconnect();
	                return success;
	            }
	            // 转移到FTP服务器目录
	            ftp.changeWorkingDirectory(remotePath);
	            success = ftp.deleteFile(remotePath + "/" + fileName);
	            ftp.logout();
	        }catch (IOException e){
	            success = false;
	        }finally{
	            if (ftp.isConnected()){
	                try{
	                    ftp.disconnect();
	                }catch (IOException e)
	                {}
	            }
	        }
	        return success;
	    }
	public static void download(String url,int port,String username, String password, String path, String filename,String name, HttpServletResponse response) {
		  try {
			  int reply;
			  ftp.connect(url, port);//连接FTP服务器
			  //如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
			  ftp.login(username, password);//登录
			  reply = ftp.getReplyCode();
			  if (!FTPReply.isPositiveCompletion(reply)) {
				  ftp.disconnect();
			  }
			  ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			  ftp.changeWorkingDirectory(path);
			  OutputStream outputStream = response.getOutputStream();
			  FTPFile[] ff = ftp.listFiles();
			  for(FTPFile f : ff){
				  if (f.getName().equals(filename)) {
				   response.addHeader("Content-Disposition", "attachment;filename=" + new String(name.getBytes("utf-8"),"ISO-8859-1"));
				   String size = String.valueOf(f.getSize());
				   response.addHeader("Content-Length", size);
				   response.setCharacterEncoding("utf-8");
				   response.setContentType("application/octec-stream");
			       ftp.retrieveFile(new String(f.getName().getBytes("utf-8"),"ISO-8859-1"), outputStream);
			       outputStream.flush();  
			       outputStream.close();
				  }
			  }
		  } catch (IOException ex) {
			  ex.printStackTrace();
			  throw new RuntimeException(ex);
		  }
		}
	}
