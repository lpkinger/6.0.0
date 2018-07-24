package com.uas.erp.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import com.uas.erp.core.HttpUtil.Response;

/**
 * @author yingp
 *
 */
public class FileUtil {

	/**
	 * 压缩文件
	 * 
	 * @param srcFiles
	 *            待压缩文件
	 * @param zipFile
	 *            压缩后保存的文件
	 * @return
	 */
	public static boolean zip(String[] srcFiles, String zipFile) {
		String[] fileNames = new String[srcFiles.length];
		for (int i = 0; i < srcFiles.length; i++) {
			fileNames[i] = getFileName(srcFiles[i]);
		}
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(zipFile));
			ZipOutputStream zos = new ZipOutputStream(bos);
			String entryName = null;
			for (int i = 0; i < fileNames.length; i++) {
				entryName = fileNames[i];
				ZipEntry entry = new ZipEntry(entryName);
				zos.putNextEntry(entry);
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcFiles[i]));
				byte[] b = new byte[1024];
				while (bis.read(b, 0, 1024) != -1) {
					zos.write(b, 0, 1024);
				}
				bis.close();
				zos.closeEntry();
			}
			zos.flush();
			zos.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 解析文件名
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileName(String filePath) {
		int location = filePath.lastIndexOf(File.separator);
		String fileName = filePath.substring(location + 1);
		return fileName;
	}

	/**
	 * 解压文件
	 * 
	 * @param zipFile
	 *            压缩文件
	 * @param parentDir
	 *            解压到的路径
	 * @return
	 */
	public static boolean unzip(String zipFile, String parentDir) {
		try {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(zipFile));
			ZipInputStream zis = new ZipInputStream(bis);
			BufferedOutputStream bos = null;
			ZipEntry entry = null;
			while ((entry = zis.getNextEntry()) != null) {
				String entryName = entry.getName();
				bos = new BufferedOutputStream(new FileOutputStream(parentDir + entryName));
				int b = 0;
				while ((b = zis.read()) != -1) {
					bos.write(b);
				}
				bos.flush();
				bos.close();
			}
			zis.close();
			return true;
		} catch (IOException e) {

		}
		return false;
	}

	/**
	 * 保存文件
	 * 
	 * <pre>
	 * 优先判断是否设置了指定的文件系统，否则保存到本地
	 * </pre>
	 * 
	 * @param file
	 * @return
	 */
	public static String saveFile(MultipartFile file, String em_code) {
		String path = uploadToFs(file);
		if (path == null) {
			path = uploadToLocal(file, em_code);
		}
		if(path.contains("\\")){//上传到本地返回路径修改
			path=path.replace("\\", "/");
		}
		return path;
	}

	/**
	 * 上传到文件系统
	 * 
	 * <pre>
	 * rest接口
	 * Post: http://10.10.100.200:9999/file/upload
	 * Return: {"path": "http://dfs.ubtoc.com/group1/M00/00/32/CgpkyFc6OBiALiQ4ABetuG5lVxw921.pdf"}
	 * </pre>
	 * 
	 * <pre>
	 * 基于dubbo的hessian方式调用服务已经测试通过，下个版本saas系统建议采用
	 * </pre>
	 * 
	 * @return
	 */
	private static String uploadToFs(MultipartFile file) {
		String uploadApi = BaseUtil.getXmlSetting("api.fs.upload");
		if (uploadApi != null) {
			try {
				Response response = HttpUtil.upload(uploadApi, file, null, false, null);
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					Map<String, String> data = FlexJsonUtil.fromJson(response.getResponseText());
					return data.get("path");
				}
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * 上传到本地磁盘
	 * 
	 * @return
	 */
	private static String uploadToLocal(MultipartFile file, String em_code) {
		String path = getFilePath(file.getOriginalFilename(), em_code);
		try {
			FileUtils.copyInputStreamToFile(file.getInputStream(), new File(path));
			return path;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	/**
	 * 生成文件实际存放的硬盘唯一路径
	 * 
	 * @param fileName
	 * @return
	 */
	private static String getFilePath(String fileName, String em_code) {
		String uuid = UUID.randomUUID().toString().replaceAll("\\-", "");
		String suffix = fileName.indexOf(".") != -1 ? fileName.substring(fileName.lastIndexOf("."), fileName.length()) : "";
		String path = PathUtil.getFilePath() + "postattach";
		File file = new File(path);
		if (!file.isDirectory()) {
			file.mkdir();
			path = path + File.separator + em_code;
			new File(path).mkdir();
		} else {
			path = path + File.separator + em_code;
			file = new File(path);
			if (!file.isDirectory()) {
				file.mkdir();
			}
		}
		return path + File.separator + uuid + suffix;
	}
	
	/** 
	 * 删除单个文件 
	 * @param   sPath 被删除文件path 
	 * @return 删除成功返回true，否则返回false 
	 */  
	public static boolean deleteFile(String sPath) {  
	    boolean flag = false;  
	    if (sPath.startsWith("http:") || sPath.startsWith("https:") || sPath.startsWith("ftp:") || 
	    		sPath.startsWith("sftp:") || sPath.startsWith("B2B://")) {
	    	return false;
		}
	    File file = new File(sPath);  
	    // 路径为文件且不为空则进行删除  
	    if (file.isFile() && file.exists()) {  
	        file.delete();  
	        flag = true;  
	    }  
	    return flag;  
	}  

}
