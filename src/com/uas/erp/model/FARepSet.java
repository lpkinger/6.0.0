package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class FARepSet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String fs_code;
	private String fs_name;
	private String fs_head;
	private String fs_title1;
	private String fs_title2;
	private String fs_righthead;
	private String fs_righttitle1;
	private String fs_righttitle2;
	private String fs_right;
	private String fs_type;
	private String fs_status;
	private String fs_statuscode;
	private Integer fs_id;
	private Date fs_indate;
	private List<FARepSetDet> dets;

	public String getFs_code() {
		return fs_code;
	}

	public void setFs_code(String fs_code) {
		this.fs_code = fs_code;
	}

	public String getFs_name() {
		return fs_name;
	}

	public void setFs_name(String fs_name) {
		this.fs_name = fs_name;
	}

	public String getFs_head() {
		return fs_head;
	}

	public void setFs_head(String fs_head) {
		this.fs_head = fs_head;
	}

	public String getFs_title1() {
		return fs_title1;
	}

	public void setFs_title1(String fs_title1) {
		this.fs_title1 = fs_title1;
	}

	public String getFs_title2() {
		return fs_title2;
	}

	public void setFs_title2(String fs_title2) {
		this.fs_title2 = fs_title2;
	}

	public String getFs_righthead() {
		return fs_righthead;
	}

	public void setFs_righthead(String fs_righthead) {
		this.fs_righthead = fs_righthead;
	}

	public String getFs_righttitle1() {
		return fs_righttitle1;
	}

	public void setFs_righttitle1(String fs_righttitle1) {
		this.fs_righttitle1 = fs_righttitle1;
	}

	public String getFs_righttitle2() {
		return fs_righttitle2;
	}

	public void setFs_righttitle2(String fs_righttitle2) {
		this.fs_righttitle2 = fs_righttitle2;
	}

	public String getFs_right() {
		return fs_right;
	}

	public void setFs_right(String fs_right) {
		this.fs_right = fs_right;
	}

	public String getFs_type() {
		return fs_type;
	}

	public void setFs_type(String fs_type) {
		this.fs_type = fs_type;
	}

	public String getFs_status() {
		return fs_status;
	}

	public void setFs_status(String fs_status) {
		this.fs_status = fs_status;
	}

	public String getFs_statuscode() {
		return fs_statuscode;
	}

	public void setFs_statuscode(String fs_statuscode) {
		this.fs_statuscode = fs_statuscode;
	}

	public Integer getFs_id() {
		return fs_id;
	}

	public void setFs_id(Integer fs_id) {
		this.fs_id = fs_id;
	}

	public Date getFs_indate() {
		return fs_indate;
	}

	public void setFs_indate(Date fs_indate) {
		this.fs_indate = fs_indate;
	}

	public List<FARepSetDet> getDets() {
		return dets;
	}

	public void setDets(List<FARepSetDet> dets) {
		this.dets = dets;
	}

	public static class FARepSetDet {

		private Integer fsd_id;
		private Integer fsd_fsid;
		private int fsd_detno;
		private String fsd_name;
		private Integer fsd_step;
		private Integer fsd_rate;
		private String fsd_formula1;
		private String fsd_formula2;
		private String fsd_rightname;
		private Integer fsd_rightstep;
		private Integer fsd_rightrate;
		private String fsd_rightformula1;
		private String fsd_rightformula2;
		private String fsd_code;

		public Integer getFsd_id() {
			return fsd_id;
		}

		public void setFsd_id(Integer fsd_id) {
			this.fsd_id = fsd_id;
		}

		public Integer getFsd_fsid() {
			return fsd_fsid;
		}

		public void setFsd_fsid(Integer fsd_fsid) {
			this.fsd_fsid = fsd_fsid;
		}

		public int getFsd_detno() {
			return fsd_detno;
		}

		public void setFsd_detno(int fsd_detno) {
			this.fsd_detno = fsd_detno;
		}

		public String getFsd_name() {
			return fsd_name;
		}

		public void setFsd_name(String fsd_name) {
			this.fsd_name = fsd_name;
		}

		public Integer getFsd_step() {
			return fsd_step;
		}

		public void setFsd_step(Integer fsd_step) {
			this.fsd_step = fsd_step;
		}

		public Integer getFsd_rate() {
			return fsd_rate;
		}

		public void setFsd_rate(Integer fsd_rate) {
			this.fsd_rate = fsd_rate;
		}

		public String getFsd_formula1() {
			return fsd_formula1;
		}

		public void setFsd_formula1(String fsd_formula1) {
			this.fsd_formula1 = fsd_formula1;
		}

		public String getFsd_formula2() {
			return fsd_formula2;
		}

		public void setFsd_formula2(String fsd_formula2) {
			this.fsd_formula2 = fsd_formula2;
		}

		public String getFsd_rightname() {
			return fsd_rightname;
		}

		public void setFsd_rightname(String fsd_rightname) {
			this.fsd_rightname = fsd_rightname;
		}

		public Integer getFsd_rightstep() {
			return fsd_rightstep;
		}

		public void setFsd_rightstep(Integer fsd_rightstep) {
			this.fsd_rightstep = fsd_rightstep;
		}

		public Integer getFsd_rightrate() {
			return fsd_rightrate;
		}

		public void setFsd_rightrate(Integer fsd_rightrate) {
			this.fsd_rightrate = fsd_rightrate;
		}

		public String getFsd_rightformula1() {
			return fsd_rightformula1;
		}

		public void setFsd_rightformula1(String fsd_rightformula1) {
			this.fsd_rightformula1 = fsd_rightformula1;
		}

		public String getFsd_rightformula2() {
			return fsd_rightformula2;
		}

		public void setFsd_rightformula2(String fsd_rightformula2) {
			this.fsd_rightformula2 = fsd_rightformula2;
		}

		public String getFsd_code() {
			return fsd_code;
		}

		public void setFsd_code(String fsd_code) {
			this.fsd_code = fsd_code;
		}
	}

}
