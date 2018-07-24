package com.uas.erp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.uas.erp.core.DateUtil;
import com.uas.erp.dao.Saveable;

/**
 * 新闻
 */
public class News implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int ne_id;
	private String ne_releaser;
	private String ne_theme;
	private Date ne_releasedate;
	private String ne_type;
	private String ne_code;
	private String ne_content;
	private List<String> ne_attachs = new ArrayList<String>();
	private int ne_browsenumber;
	private String ne_feel;
	private List<String> files = new ArrayList<String>();// 新闻内容里面的图片、声音、视频等
	private List<NewsComment> comments = new ArrayList<NewsComment>();// 新闻评论
	private News prevNews;
	private News nextNews;
	private Integer ne_istop;
	private String headerImg;

	public Integer getNe_istop() {
		return ne_istop;
	}

	public void setNe_istop(Integer ne_istop) {
		ne_istop = ne_istop == null ? 0 : ne_istop;
		this.ne_istop = ne_istop;
	}

	public int getNe_id() {
		return ne_id;
	}

	public void setNe_id(int ne_id) {
		this.ne_id = ne_id;
	}

	public String getNe_releaser() {
		return ne_releaser;
	}

	public void setNe_releaser(String ne_releaser) {
		this.ne_releaser = ne_releaser;
	}

	public String getNe_feel() {
		return ne_feel;
	}

	public void setNe_feel(String ne_feel) {
		this.ne_feel = ne_feel;
	}

	public String getNe_theme() {
		return ne_theme;
	}

	public void setNe_theme(String ne_theme) {
		this.ne_theme = ne_theme;
	}

	public Date getNe_releasedate() {
		return ne_releasedate;
	}

	public void setNe_releasedate(Date ne_releasedate) {
		this.ne_releasedate = ne_releasedate;
	}

	public String getNe_type() {
		return ne_type;
	}

	public void setNe_type(String ne_type) {
		this.ne_type = ne_type;
	}

	public String getNe_code() {
		return ne_code;
	}

	public void setNe_code(String ne_code) {
		this.ne_code = ne_code;
	}

	public String getNe_content() {
		return ne_content;
	}

	public void setNe_content(String ne_content) {
		this.ne_content = ne_content;
	}

	public int getNe_browsenumber() {
		return ne_browsenumber;
	}

	public void setNe_browsenumber(int ne_browsenumber) {
		this.ne_browsenumber = ne_browsenumber;
	}

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}

	public List<NewsComment> getComments() {
		return comments;
	}

	public void setComments(List<NewsComment> comments) {
		this.comments = comments;
	}

	public News getPrevNews() {
		return prevNews;
	}

	public void setPrevNews(News prevNews) {
		this.prevNews = prevNews;
	}

	public News getNextNews() {
		return nextNews;
	}

	public void setNextNews(News nextNews) {
		this.nextNews = nextNews;
	}

	public List<String> getNe_attachs() {
		return ne_attachs;
	}

	public void setNe_attachs(List<String> ne_attachs) {
		this.ne_attachs = ne_attachs;
	}

	public String getHeaderImg() {
		return headerImg;
	}

	public void setHeaderImg(String headerImg) {
		this.headerImg = headerImg;
	}

	public String getNe_datestr() {
		return DateUtil.parseDateToString(ne_releasedate, "MM月dd日 HH:mm");
	}

	@Override
	public String table() {
		return "news";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "ne_id" };
	}

}
