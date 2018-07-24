package com.uas.b2b.model;

public class InquiryMouldDecide {
	private long in_id;
	private Long b2b_im_id;
	private String in_code;
	private String in_adoptstatus;
	private String in_checksendstatus;

	public long getIn_id() {
		return in_id;
	}

	public void setIn_id(long in_id) {
		this.in_id = in_id;
	}

	public Long getB2b_im_id() {
		return b2b_im_id;
	}

	public void setB2b_im_id(Long b2b_im_id) {
		this.b2b_im_id = b2b_im_id;
	}

	public String getIn_code() {
		return in_code;
	}

	public void setIn_code(String in_code) {
		this.in_code = in_code;
	}

	public String getIn_adoptstatus() {
		return in_adoptstatus;
	}

	public void setIn_adoptstatus(String in_adoptstatus) {
		this.in_adoptstatus = in_adoptstatus;
	}

	public String getIn_checksendstatus() {
		return in_checksendstatus;
	}

	public void setIn_checksendstatus(String in_checksendstatus) {
		this.in_checksendstatus = in_checksendstatus;
	}

}
