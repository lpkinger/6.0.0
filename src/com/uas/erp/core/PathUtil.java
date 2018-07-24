package com.uas.erp.core;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class PathUtil {

	// classes文件夹
	private static String CLASSPATH;
	// WEB-INF文件夹
	private static String WEBPATH;
	// 系统路径//ERP路径
	private static String SYSPATH;
	// 自定义逻辑(主要逻辑)存放路径
	private static String LOGICPATH;
	// 默认的附件路径,和项目同级目录
	private static String FILEPATH;
	// 自定义报表rpt文件
	private static String RPTPATH;
	// 补丁包源文件路劲
	private static String PATCHPATH;
	// Excel路径
	private static String EXCELPATH;
	// 自定义逻辑的java代码目录
	private static String OPENPATH;
	// 邮件收发附件存放地,默认为FILEPATH/mail
	private static String MAILPATH;
	private static String DOCUMENTPATH;
	private static String OFFICIALDOCUMENTPATH;
	private static String TEMPPATH;
	// 帮助文档路径
	private static String HELPPATH;

	/**
	 * 系统class类加载路径
	 */
	@SuppressWarnings("rawtypes")
	private static void setClassPath() {
		try {
			Class objClass = ContextUtil.getApplicationContext().getClass();
			String strRealPath = objClass.getClassLoader().getResource("").getFile();
			try {
				strRealPath = URLDecoder.decode(strRealPath, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			CLASSPATH = strRealPath;
			if (CLASSPATH.contains("/")) {
				CLASSPATH = "/" + CLASSPATH;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return classes目录
	 */
	public static String getClassPath() {
		if (CLASSPATH == null) {
			setClassPath();
		}
		return CLASSPATH;
	}

	/**
	 * @return web-inf目录
	 */
	public static String getPath() {
		if (WEBPATH == null) {
			File objFile = new File(getClassPath());
			WEBPATH = objFile.getParent() + File.separator;
		}
		return WEBPATH;
	}

	/**
	 * 设置系统路径,
	 */
	private static void setSysPath() {
		File objFile = new File(getPath());
		SYSPATH = objFile.getParent() + File.separator;
	}

	/**
	 * 项目路径
	 * 
	 * @return
	 */
	public static String getSysPath() {
		if (SYSPATH == null) {
			setSysPath();
		}
		return SYSPATH;
	}

	/**
	 * 设置附件路径, 与项目目录同级
	 */
	private static void setFilePath() {
		String path = BaseUtil.getXmlSetting("filepath");
		if (path != null) {
			FILEPATH = path;
		} else {
			File objFile = new File(getSysPath());
			FILEPATH = objFile.getParent() + File.separator;
		}
	}

	public static String getFilePath() {
		if (FILEPATH == null) {
			setFilePath();
		}
		return FILEPATH;
	}

	/**
	 * 设置补丁路径, 与项目目录同级
	 */
	private static void setPatchPath() {
		PATCHPATH = getFilePath() + "mypatch";
		File file = new File(PATCHPATH);
		if (!file.isDirectory()) {
			file.mkdir();
		}
	}

	public static String getPatchPath() {
		if (PATCHPATH == null) {
			setPatchPath();
		}
		return PATCHPATH;
	}

	private static void setTempPath() {
		TEMPPATH = getSysPath() + "temp";
		File file = new File(TEMPPATH);
		if (!file.isDirectory()) {
			file.mkdir();
		}
	}

	public static String getTempPath() {
		if (TEMPPATH == null) {
			setTempPath();
		}
		return TEMPPATH;
	}

	public static String getHelpPath() {
		if (HELPPATH == null) {
			setHelpPath();
		}
		return HELPPATH;
	}

	private static void setHelpPath() {
		HELPPATH = getFilePath() + "help";
		File file = new File(HELPPATH);
		if (!file.isDirectory()) {
			file.mkdir();
		}
	}

	/**
	 * 自定义报表rept文件路径
	 */
	private static void setRptPath() {
		RPTPATH = getFilePath() + "rpts";
		File file = new File(RPTPATH);
		if (!file.isDirectory()) {
			file.mkdir();
		}
	}

	public static String getRptPath() {
		if (RPTPATH == null) {
			setRptPath();
		}
		return RPTPATH;
	}

	/**
	 * 设置自定义逻辑的java代码目录
	 */
	private static void setOpenPath() {
		OPENPATH = getFilePath() + "openservice";
		File file = new File(OPENPATH);
		if (!file.isDirectory()) {
			file.mkdir();
		}
	}

	public static String getOpenPath() {
		if (OPENPATH == null) {
			setOpenPath();
		}
		return OPENPATH;
	}

	/**
	 * 设置邮件附件路径
	 */
	private static void setMailPath() {
		MAILPATH = getFilePath() + "mail";
		File file = new File(MAILPATH);
		if (!file.isDirectory()) {
			file.mkdir();
		}
	}

	public static String getMailPath() {
		if (MAILPATH == null) {
			setMailPath();
		}
		return MAILPATH;
	}

	/**
	 * 文档路径
	 */
	private static void setDocPath() {
		DOCUMENTPATH = getFilePath() + "document";
		File file = new File(DOCUMENTPATH);
		if (!file.isDirectory()) {
			file.mkdir();
		}
	}

	public static String getDocPath() {
		if (DOCUMENTPATH == null) {
			setDocPath();
		}
		return DOCUMENTPATH;
	}

	/**
	 * 文档路径
	 */
	private static void setOfficePath() {
		OFFICIALDOCUMENTPATH = getFilePath() + "officialDocument";
		File file = new File(OFFICIALDOCUMENTPATH);
		if (!file.isDirectory()) {
			file.mkdir();
		}
	}

	public static String getOfficePath() {
		if (OFFICIALDOCUMENTPATH == null) {
			setOfficePath();
		}
		return OFFICIALDOCUMENTPATH;
	}

	/**
	 * EXcel 路径
	 * */
	private static void setExcelPath() {
		String str = null;
		try {
			str = BaseUtil.class.getResource("").getPath();
			str = URLDecoder.decode(str, "utf-8");
			if (str.contains("classes")) {
				str = str.substring(0, str.lastIndexOf("classes/"));
			}
			if (str.contains("/")) {
				str = "/" + str;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String ch = File.separator;
		if (str.contains("/")) {
			ch = "/";
		} else {
			ch = "\\";
		}
		String[] arr = BaseUtil.parseStr2Array(str, ch);
		StringBuffer sb = new StringBuffer();
		if (str.contains("/")) {
			sb.append(ch);
		}
		for (int i = 0; i < arr.length; i++) {
			if (i < arr.length - 1) {// 去掉最后两级目录
				sb.append(arr[i]);
				sb.append(File.separator);
			}
		}
		EXCELPATH = sb.toString() + "Excel";
		File file = new File(EXCELPATH);
		if (!file.isDirectory()) {
			file.mkdir();
		}
	}

	public static String getExcelPath() {
		if (EXCELPATH == null) {
			setExcelPath();
		}
		return EXCELPATH;
	}

	/**
	 * 自定义逻辑(主要逻辑)路径
	 */
	private static void setLogicPath() {
		LOGICPATH = getFilePath() + "logic";
		File file = new File(LOGICPATH);
		if (!file.isDirectory()) {
			file.mkdir();
		}
	}

	public static String getLogicPath() {
		if (LOGICPATH == null) {
			setLogicPath();
		}
		return LOGICPATH;
	}

}
