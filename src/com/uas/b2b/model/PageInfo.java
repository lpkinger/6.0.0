package com.uas.b2b.model;

public class PageInfo {

	private int pageSize;
	private int pageNumber;
	private String keyword;
	private Long enuu;
	private Long useruu;

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Long getEnuu() {
		return enuu;
	}

	public void setEnuu(Long enuu) {
		this.enuu = enuu;
	}

	public Long getUseruu() {
		return useruu;
	}

	public void setUseruu(Long useruu) {
		this.useruu = useruu;
	}

}
