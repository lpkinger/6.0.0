package com.uas.erp.core.logging;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileBuffer {

	private String filePath;
	private BufferedWriter writer;
	private BufferedReader reader;

	/**
	 * @param folderPath
	 *            文件夹目录
	 * @param fileName
	 *            文件名
	 */
	public FileBuffer(String folderPath, String fileName) {
		File file = new File(folderPath);
		if (!file.isDirectory())
			file.mkdirs();
		this.filePath = folderPath + File.separator + fileName;
	}

	/**
	 * 文件里面追加一行内容
	 * 
	 * @param content
	 * @return
	 */
	public synchronized boolean append(String content) {
		boolean isEmpty = false;
		if (writer == null)
			try {
				File file = new File(filePath);
				if (!file.exists())
					file.createNewFile();
				writer = new BufferedWriter(new FileWriter(file, true));
				isEmpty = true;
			} catch (IOException e) {
				return false;
			}
		try {
			writer.write((isEmpty ? "" : "\r\n") + content);
			writer.flush();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public synchronized String readLine() {
		if (writer != null)
			close();
		if (reader == null) {
			File file = new File(filePath);
			if (file.exists()) {
				try {
					InputStreamReader streamReader = new InputStreamReader(new FileInputStream(file));
					reader = new BufferedReader(streamReader);
				} catch (FileNotFoundException e) {
					return null;
				}
			}
		}
		if (reader != null)
			try {
				return reader.readLine();
			} catch (IOException e) {
			}
		return null;
	}

	/**
	 * 关闭文件写入流
	 */
	protected synchronized void close() {
		if (writer != null)
			try {
				writer.close();
			} catch (IOException e) {

			} finally {
				writer = null;
			}
	}

	public synchronized void delete() {
		if (writer != null)
			try {
				writer.close();
			} catch (IOException e1) {
			} finally {
				writer = null;
			}
		if (reader != null)
			try {
				reader.close();
			} catch (IOException e) {
			} finally {
				reader = null;
			}
		File file = new File(filePath);
		if (file.exists())
			file.delete();
	}

	public synchronized boolean isEmpty() {
		boolean empty = this.writer == null;
		if (!empty) {
			File file = new File(filePath);
			empty = !file.exists();
			if (!empty)
				empty = file.length() == 0;
		}
		return empty;
	}

}
