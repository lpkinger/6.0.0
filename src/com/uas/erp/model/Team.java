package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;

public class Team implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int team_id;
	private String team_name;
	private int team_prjid;
	private String team_prjname;
	private int team_mothergroupid;
	private String recorder;
	private Date recorddate;
	private String team_code;

	public int getTeam_id() {
		return team_id;
	}

	public void setTeam_id(int team_id) {
		this.team_id = team_id;
	}

	public String getTeam_name() {
		return team_name;
	}

	public void setTeam_name(String team_name) {
		this.team_name = team_name;
	}

	public int getTeam_prjid() {
		return team_prjid;
	}

	public void setTeam_prjid(int team_prjid) {
		this.team_prjid = team_prjid;
	}

	public String getTeam_prjname() {
		return team_prjname;
	}

	public void setTeam_prjname(String team_prjname) {
		this.team_prjname = team_prjname;
	}

	public int getTeam_mothergroupid() {
		return team_mothergroupid;
	}

	public void setTeam_mothergroupid(int team_mothergroupid) {
		this.team_mothergroupid = team_mothergroupid;
	}

	public String getRecorder() {
		return recorder;
	}

	public void setRecorder(String recorder) {
		this.recorder = recorder;
	}

	public Date getRecorddate() {
		return recorddate;
	}

	public void setRecorddate(Date recorddate) {
		this.recorddate = recorddate;
	}

	public String getTeam_code() {
		return team_code;
	}

	public void setTeam_code(String team_code) {
		this.team_code = team_code;
	}
}
