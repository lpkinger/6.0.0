package com.uas.erp.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.client.ClientProtocolException;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.docx4j.Docx4J;
import org.docx4j.Docx4jProperties;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.w3c.dom.Document;

public class WordToHtml {
	public static String getWord(String filepath,String caller,String fileName){
		
		InputStream input = null;
		String name = null;
		if (filepath.startsWith("https://dfs.ubtob.com")) {
			name = filepath.substring(filepath.lastIndexOf("/") + 1, filepath.lastIndexOf(".") + 1);
		}else if(!(filepath.startsWith("http:") || filepath.startsWith("https:") || filepath.startsWith("B2B://"))){
			name = filepath.substring(filepath.lastIndexOf("/") + 1, filepath.lastIndexOf(".") + 1);
		}
		
		if (name==null) {
			return null;
		}
		
		File callerFile;
		String realPath="temp" + File.separator;
		final String path =caller==null? PathUtil.getTempPath() + File.separator+File.separator: PathUtil.getTempPath() + File.separator+caller+File.separator;
		//帮助文档根据CALLER建立独立文件夹
		if(caller!=null){
			callerFile=new File(PathUtil.getTempPath() + File.separator+caller);
			if(!callerFile.exists()) callerFile.mkdir();
			realPath += caller+File.separator;
		}
		File newFile = new File(path, name + "html");
		if (newFile.exists())
			return realPath + newFile.getName();
		try{
			if (filepath.startsWith("https://dfs.ubtob.com")) {
				input = HttpUtil.download(filepath);
			}else if(!(filepath.startsWith("http:") || filepath.startsWith("https:") || filepath.startsWith("B2B://"))){
				//先检查文件存不存在,不存在则返回null
				File file = new File(filepath);
				input = new FileInputStream(file);
			}
		}catch(FileNotFoundException e){
			return null;
		}catch (KeyManagementException e) {
			return null;
		} catch (ClientProtocolException e) {
			return null;
		} catch (NoSuchAlgorithmException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
		
		try {
			//如果是docx格式文件，则使用docx4j插件进行解析
			if (filepath.endsWith(".docx") || filepath.endsWith(".DOCX") || (fileName!=null&&(fileName.endsWith(".docx") || fileName.endsWith(".DOCX")))) {

				WordprocessingMLPackage wordMLPackage= Docx4J.load(input);  			
				String htmlPath = newFile.getAbsolutePath();
			    HTMLSettings htmlSettings = Docx4J.createHTMLSettings();  
			    String imageFilePath=htmlPath.substring(0,htmlPath.lastIndexOf(File.separator)+1);
			    htmlSettings.setImageDirPath(imageFilePath);    
			    htmlSettings.setWmlPackage(wordMLPackage); 
			    
			    OutputStream os = new FileOutputStream(htmlPath);  

			    Docx4jProperties.setProperty("docx4j.Convert.Out.HTML.OutputMethodXML", true);  

			    
			    Docx4J.toHTML(htmlSettings, os, Docx4J.FLAG_EXPORT_PREFER_XSL);  
			      
			}else{ //doc格式文件解析，以下为官方api例子
				HWPFDocument wordDocument = new HWPFDocument(input);
				WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder()
						.newDocument());
				wordToHtmlConverter.setPicturesManager(new PicturesManager() {
					public String savePicture(byte[] content, PictureType pictureType, String suggestedName, float widthInches,
							float heightInches) {
						File file = new File(path + suggestedName);
						try {
							OutputStream os = new FileOutputStream(file);
							os.write(content);
							os.close();
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return suggestedName;
					}
				});
				wordToHtmlConverter.processDocument(wordDocument);

				Document htmlDocument = wordToHtmlConverter.getDocument();
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				DOMSource domSource = new DOMSource(htmlDocument);
				StreamResult streamResult = new StreamResult(outStream);

				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer serializer = tf.newTransformer();
				serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
				serializer.setOutputProperty(OutputKeys.INDENT, "yes");
				serializer.setOutputProperty(OutputKeys.METHOD, "html");
				serializer.transform(domSource, streamResult);
				outStream.close();
				String content = new String(outStream.toByteArray());
				StringBuffer content1 = new StringBuffer(content);
				String s2 = "</head>";
				String addString = "<script type=\"text/javascript\">window.onload=function(){var oP=document.getElementsByTagName('p');for(var i=0;i<oP.length;i++){if(oP[i].childNodes.length==0){oP[i].innerHTML='&nbsp';}}}</script>";
				content1.insert(content1.indexOf(s2), addString);
				FileUtils.write(newFile, content, "utf-8");
			}		
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return realPath + newFile.getName();
	}
}
