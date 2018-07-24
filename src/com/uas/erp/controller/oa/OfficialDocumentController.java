package com.uas.erp.controller.oa;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.PathUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.FileUpload;
import com.uas.erp.service.common.FilePathService;
import com.uas.erp.service.oa.OfficialDocumentService;

@Controller
public class OfficialDocumentController {

	@Autowired
	private FilePathService filePathService;
	@Autowired
	private OfficialDocumentService officialDocumentService;

	@RequestMapping("/oa/officialDocument/upload.action")
	public @ResponseBody String upload(String caller, String em_code, FileUpload uploadItem, String number) {
		try {
			String filename = uploadItem.getFile().getOriginalFilename();
			long size = uploadItem.getFile().getSize();
			if (size > 500000) {
				return "{error: '文件过大'}";
			}
			String path = PathUtil.getOfficePath();
			File file = new File(path);
			if (!file.exists()) {
				file.mkdir();
			}
			path = path + File.separator + number;
			file = new File(path);
			if (!file.exists()) {
				file.mkdir();
			}
			path = path + File.separator + filename;
			file = new File(path);
			BufferedOutputStream bos = null;
			BufferedInputStream bis = null;
			try {
				bos = new BufferedOutputStream(new FileOutputStream(file));
				bis = new BufferedInputStream(uploadItem.getFile().getInputStream());
				int c;
				while ((c = bis.read()) != -1) {
					bos.write(c);
					bos.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			int id = filePathService.saveFilePath(path, (int) size, filename, SystemSession.getUser());
			return "{success: true, filepath: " + id + ",size: " + size + "}";
		} catch (Exception e) {
			return "{error: '文件过大,上传失败'}";
		}
	}

	@RequestMapping("/oa/officialDocument/file.action")
	public @ResponseBody Map<String, Object> file(String filename, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String url1 = PathUtil.getOfficePath() + File.separator + filename;
		String url2 = PathUtil.getDocPath() + File.separator + "Document" + File.separator + "OfficialDocument" + File.separator + filename;
		File targetDir = new File(url2);
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}
		File sourceDir = new File(url1);
		if (sourceDir.exists()) {
			File[] files = sourceDir.listFiles();
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				String sf = url1 + File.separator + file.getName();
				String tf = url2 + File.separator + file.getName();
				System.out.println(sf);
				System.out.println(tf);
				// if (! new File(tf).exists()) {
				// new File(tf).mkdir();
				// }
				copyFile(sf, tf);
			}
		}
		modelMap.put("success", true);
		return modelMap;
	}

	public static void copyFile(String source, String target) {
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(source)));
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(target)));
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = in.read(b)) != -1) {
				out.write(b, 0, len);
			}
			out.flush();
			in.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 整批结案
	 */
	@RequestMapping(value = "/oa/officialDocument/vastFile.action")
	@ResponseBody
	public Map<String, Object> vastClose(String caller, String tablename, String[] field, int[] id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		officialDocumentService.vastFile(caller, tablename, field, id);
		modelMap.put("success", true);
		return modelMap;
	}

}
