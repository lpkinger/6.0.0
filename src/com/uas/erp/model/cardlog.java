package com.uas.erp.model;


public class cardlog {
	private String cl_emname;
	private String cl_emcode;
	private String cl_cardcode;
	private String cl_time;
	
	public cardlog(){
		
	}

	public String getCl_emname() {
		return cl_emname;
	}

	public void setCl_emname(String cl_emname) {
		this.cl_emname = cl_emname;
	}

	public String getCl_emcode() {
		return cl_emcode;
	}

	public void setCl_emcode(String cl_emcode) {
		this.cl_emcode = cl_emcode;
	}

	public String getCl_cardcode() {
		return cl_cardcode;
	}

	public void setCl_cardcode(String cl_cardcode) {
		this.cl_cardcode = cl_cardcode;
	}

	public String getCl_time() {
		return cl_time;
	}

	public void setCl_time(String cl_time) {
		this.cl_time = cl_time;
	}

	@Override
	public String toString() {
		return "cardlog [cl_emname=" + cl_emname + ", cl_emcode=" + cl_emcode + ", cl_cardcode=" + cl_cardcode
				+ ", cl_time=" + cl_time + "]";
	}
}
