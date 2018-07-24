package com.uas.erp.core;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.commons.lang.StringUtils;

/**
 * 生成java文件、编译java文件
 */
public class ExecuteUtil {

	private static StringBuffer sb;

	private static void createPackage(String packageName) {
		sb.append("package ");
		sb.append(packageName);
		sb.append(";\n");
	}

	private static void createImport(String importedClassName) {
		sb.append("import ");
		sb.append(importedClassName);
		sb.append(";\n");
	}

	private static void createMethodForSave(String methodName, String body) {
		sb.append("public void ");
		sb.append(methodName);
		sb.append("(");
		sb.append("HashMap<Object, Object> store");
		sb.append(", ");
		sb.append("ArrayList<Map<Object, Object>> gstore");
		sb.append(", ");
		sb.append("String language");
		sb.append(")");
		sb.append(" {");
		sb.append(";\n");
		sb.append(body);
		sb.append(";\n");
		sb.append(" }");
		sb.append(";\n");
	}

	private static void createMethodForDel(String methodName, String body) {
		sb.append("public void ");
		sb.append(methodName);
		sb.append("(");
		sb.append("Integer id");
		sb.append(", ");
		sb.append("String language");
		sb.append(")");
		sb.append(" {");
		sb.append(";\n");
		sb.append(body);
		sb.append(";\n");
		sb.append(" }");
		sb.append(";\n");
	}

	private static void createMethodForDelDetail(String methodName, String body) {
		sb.append("public void ");
		sb.append(methodName);
		sb.append("(");
		sb.append("String condition");
		sb.append(", ");
		sb.append("String language");
		sb.append(")");
		sb.append(" {");
		sb.append(";\n");
		sb.append(body);
		sb.append(";\n");
		sb.append(" }");
		sb.append(";\n");
	}

	private static void createMethodForCommit(String methodName, String body) {
		sb.append("public void ");
		sb.append(methodName);
		sb.append("(");
		sb.append("Integer id");
		sb.append(", ");
		sb.append("String language");
		sb.append(", ");
		sb.append("Employee employee");
		sb.append(")");
		sb.append(" {");
		sb.append(";\n");
		sb.append(body);
		sb.append(";\n");
		sb.append(" }");
		sb.append(";\n");
	}

	/**
	 * 自定义逻辑的实现 根据提交的类名、方法名、方法，写成.java文件并编译
	 * 
	 * @param packageName
	 *            包名
	 * @param className
	 *            类名
	 * @param methodName
	 *            方法名
	 * @param methodType
	 *            方法类型{save,delete,deletedetail,commit,audit...}
	 * @param body
	 *            内容
	 */
	public static void execute(String packageName, String className, String methodName, String methodType, String body) {
		// 生成java文件内容
		sb = new StringBuffer();
		createPackage(packageName);
		createImport("java.util.*");
		createImport("com.uas.erp.*");
		createImport("org.springframework.*");
		sb.append("public class ");
		sb.append(className);
		sb.append(" {");
		sb.append("\n");
		if (methodType.equals("save")) {
			createMethodForSave(methodName, body);
		} else if (methodType.equals("delete")) {
			createMethodForDel(methodName, body);
		} else if (methodType.equals("deletedetail")) {
			createMethodForDelDetail(methodName, body);
		} else if (methodType.equals("commit")) {
			createMethodForCommit(methodName, body);
		}
		sb.append("\n");
		sb.append("}");
		// 生成java文件
		createJavaFile(packageName, className, sb.toString());
		// 编译
		compileJavaFile(packageName, className);
	}

	/**
	 * 在指定目录下创建 java源文件文件；
	 * 
	 * @param packageName
	 *            包名
	 * @param className
	 *            类名
	 */
	private static void createJavaFile(String packageName, String className, String body) {
		String filePATH = PathUtil.getOpenPath() + File.separator + packageName + File.separator;
		File file = new File(filePATH);
		if (!file.isDirectory()) {
			file.mkdir();
		}
		file = new File(filePATH, className + ".java");
		try {
			PrintWriter out = new PrintWriter(new FileOutputStream(file));
			out.println(body);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 编译java文件
	 */
	private static void compileJavaFile(String packageName, String className) {
		try {
			DiagnosticCollector<JavaFileObject> dia = new DiagnosticCollector<JavaFileObject>();
			boolean c = compiler(PathUtil.getOpenPath() + File.separator + packageName + File.separator + className + ".java",
					PathUtil.getOpenPath() + File.separator + packageName, PathUtil.getPath(), dia);
			if (!c) {
				for (Diagnostic<? extends JavaFileObject> obj : dia.getDiagnostics()) {
					System.out.println(obj.getCode());
					System.out.println(obj.getSource());
					System.out.println(obj.getMessage(null));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 编译java文件
	 * 
	 * @param filePath
	 *            文件或者目录（若为目录，自动递归编译）
	 * @param sourceDir
	 *            java源文件存放目录
	 * @param targetDir
	 *            编译后class类文件存放目录
	 * @param diagnostics
	 *            存放编译过程中的错误信息
	 * @return
	 * @throws Exception
	 */
	private static boolean compiler(String filePath, String sourceDir, String targetDir, DiagnosticCollector<JavaFileObject> diagnostics)
			throws Exception {
		// 获取编译器实例
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		// 获取标准文件管理器实例
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
		try {
			if (StringUtils.isEmpty(filePath) && StringUtils.isEmpty(sourceDir) && StringUtils.isEmpty(targetDir)) {
				return false;
			}
			// 得到filePath目录下的所有java源文件
			File sourceFile = new File(filePath);
			List<File> sourceFileList = new ArrayList<File>();
			getSourceFiles(sourceFile, sourceFileList);
			// 没有java文件，直接返回
			if (sourceFileList.size() == 0) {
				return false;
			}
			List<File> sourceFileList1 = new ArrayList<File>();
			getSourceJars(new File(PathUtil.getFilePath() + File.separator + "WEB-INF" + File.separator), sourceFileList1);
			Iterator<File> iterator = sourceFileList1.iterator();
			StringBuffer sb = new StringBuffer(PathUtil.getPath());
			while (iterator.hasNext()) {
				sb.append(iterator.next() + ";");// 将lib里面的jar包放在classpath，这样才能在web项目运行时动态编译
			}
			// 获取要编译的编译单元
			Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(sourceFileList);
			/**
			 * 编译选项，在编译java文件时，编译程序会自动的去寻找java文件引用的其他的java源文件或者class。 -sourcepath选项就是定义java源文件的查找目录， -classpath选项就是定义class文件的查找目录。
			 */
			Iterable<String> options = Arrays.asList("-d", targetDir, "-sourcepath", sourceDir, "-classpath", sb.toString());
			CompilationTask compilationTask = compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits);
			// 运行编译任务
			return compilationTask.call();
		} finally {
			fileManager.close();
		}
	}

	/**
	 * 查找该目录下的所有的java文件
	 * 
	 * @param sourceFile
	 * @param sourceFileList
	 * @throws Exception
	 */
	private static void getSourceFiles(File sourceFile, List<File> sourceFileList) throws Exception {
		if (sourceFile.exists() && sourceFileList != null) {// 文件或者目录必须存在
			if (sourceFile.isDirectory()) {// 若file对象为目录
				// 得到该目录下以.java结尾的文件或者目录
				File[] childrenFiles = sourceFile.listFiles(new FileFilter() {
					public boolean accept(File pathname) {
						if (pathname.isDirectory()) {
							return true;
						} else {
							String name = pathname.getName();
							return name.endsWith(".java") ? true : false;
						}
					}
				});
				// 递归调用
				for (File childFile : childrenFiles) {
					getSourceFiles(childFile, sourceFileList);
				}
			} else {// 若file对象为文件
				sourceFileList.add(sourceFile);
			}
		}
	}

	/**
	 * 查找目录下所有jar包
	 */
	private static void getSourceJars(File sourceFile, List<File> sourceFileList) throws Exception {
		if (sourceFile.exists() && sourceFileList != null) {// 文件或者目录必须存在
			if (sourceFile.isDirectory()) {// 若file对象为目录
				// 得到该目录下以.jar结尾的文件或者目录
				File[] childrenFiles = sourceFile.listFiles(new FileFilter() {
					public boolean accept(File pathname) {
						if (pathname.isDirectory()) {
							return true;
						} else {
							String name = pathname.getName();
							return name.endsWith(".jar") ? true : false;
						}
					}
				});
				// 递归调用
				for (File childFile : childrenFiles) {
					getSourceFiles(childFile, sourceFileList);
				}
			} else {// 若file对象为文件
				sourceFileList.add(sourceFile);
			}
		}
	}
}
