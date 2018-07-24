package com.uas.erp.core;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.http.client.ClientProtocolException;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.TextRun;
import org.apache.poi.hslf.usermodel.RichTextRun;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

public class PptToHtml {

	public static Map<String, Object> getHtml(String filepath, String fileName){
		Map<String, Object> map = new HashMap<String, Object>();
		InputStream input = null;
		String path = null;
		StringBuffer sb = new StringBuffer();
		if (filepath.startsWith("https://dfs.ubtob.com")) {
			path = filepath.substring(0, filepath.lastIndexOf("."));
		}else if(!(filepath.startsWith("http:") || filepath.startsWith("https:") || filepath.startsWith("B2B://"))){
			path = filepath.substring(0, filepath.lastIndexOf("."));
		}
		if (path==null) {
			return null;
		}else{
			File newFile = new File(path);
			if(newFile != null && newFile.isDirectory() && newFile.exists()){		//如果文件已经解析过
				map.put("path", path);
				map.put("pageSize", newFile.listFiles().length-1);
				return map;
			}else{
				try {
					if (filepath.startsWith("https://dfs.ubtob.com")) {
						input = HttpUtil.download(filepath);
					}else if(!(filepath.startsWith("http:") || filepath.startsWith("https:") || filepath.startsWith("B2B://"))){
						//先检查文件存不存在,不存在则返回null
						File file = new File(filepath);
						input = new FileInputStream(file);
					}
				} catch (Exception e) {
					return null;
				}
				File dir = new File(filepath.substring(0, filepath.lastIndexOf(".")));
				if(!dir.exists()){
					dir.mkdir();
				}
				//2003
				if (filepath.endsWith(".ppt") || filepath.endsWith(".PPT") || (fileName!=null&&(fileName.endsWith(".ppt") || fileName.endsWith(".PPT")))) {
					try {
						SlideShow ppt = new SlideShow(input);
						input.close();
						Dimension pageSize = ppt.getPageSize();
						Slide[] slide = ppt.getSlides();
						for (int i = 0; i < slide.length; i++) {   
							// 防止中文乱码
							TextRun[] truns = slide[i].getTextRuns(); 
							for ( int k=0;k<truns.length;k++){        
								RichTextRun[] rtruns = truns[k].getRichTextRuns();        
				                for(int l=0;l<rtruns.length;l++){        
				                	rtruns[l].setFontIndex(1);        
				                	rtruns[l].setFontName("宋体");    
				                	int currentFontSize = rtruns[l].getFontSize();
				                	if((currentFontSize <= 0) || (currentFontSize >= 26040)){
				                		rtruns[l].setFontSize(18);
				                	}
			                	}        
							}        
							BufferedImage img = new BufferedImage(pageSize.width,pageSize.height, BufferedImage.TYPE_INT_RGB); 
							Graphics2D graphics = img.createGraphics(); 
							graphics.fill(new Rectangle2D.Float(0, 0, pageSize.width, pageSize.height));
					    	slide[i].draw(graphics);
					    	// 这里设置图片的存放路径和图片的格式(jpeg,png,bmp等等),注意生成文件路径   
					    	FileOutputStream out = new FileOutputStream(path+File.separator+i+".jpeg");
						    ImageIO.write(img, "jpeg", out);
						    out.close();   
						}
						map.put("path", path);
						map.put("pageSize", slide.length-1);
						return map;
					} catch (Exception e){
						e.printStackTrace();
					}
				}
				
				//2007+
				if(fileName != null && (filepath.endsWith(".pptx") || filepath.endsWith(".PPTX"))){
			        try {
						XMLSlideShow ppt = new XMLSlideShow(input);
						input.close();
						
						Dimension pageSize = ppt.getPageSize();  // size of the canvas in points
						XSLFSlide[] slide = ppt.getSlides();
						for(int i = 0 ; i < slide.length; i++){
							// 防止中文乱码
							XSLFShape[] shapes = slide[i].getShapes();
							for(XSLFShape shape : shapes){
							if(shape instanceof XSLFTextShape) {    
			                       XSLFTextShape tsh = (XSLFTextShape)shape;    
			                       for(XSLFTextParagraph p : tsh){    
			                           for(XSLFTextRun r : p){    
			                               r.setFontFamily("宋体");    
			                           }    
			                       }    
			                   }  
							}
							BufferedImage img = new BufferedImage(pageSize.width,pageSize.height, BufferedImage.TYPE_INT_RGB); 
							Graphics2D graphics = img.createGraphics();   
							slide[i].draw(graphics);
							// 这里设置图片的存放路径和图片的格式(jpeg,png,bmp等等),注意生成文件路径   
							FileOutputStream out = new FileOutputStream(path+File.separator+i+".jpeg");
						    ImageIO.write(img, "jpeg", out);   
						    out.close();
						}
						map.put("path", path);
						map.put("pageSize", slide.length-1);
						return map;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}
	
		
}
