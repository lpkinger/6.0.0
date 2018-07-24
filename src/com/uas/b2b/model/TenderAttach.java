package com.uas.b2b.model;

import java.io.Serializable;

public class TenderAttach implements Serializable{

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	/**
	 * 文件名称
	 */
	private String name;

	/**
	 * 文件大小
	 */
	private int size;
	
	private String path;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public TenderAttach() {
	}

	/**
	 * @param fileName
	 *            文件名
	 * @param size
	 *            大小
	 */
	public TenderAttach(String fileName, int size) {
		this.name = fileName;
		this.size = size;
	}
	

}
