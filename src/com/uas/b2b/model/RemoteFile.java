package com.uas.b2b.model;

public class RemoteFile {
	private String name;
	private Long size;
	private Long id;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String toSqlString(int primaryKey, String remoteUrl) {
		return "insert into filepath(fp_id,fp_name,fp_path,fp_size) values (" + primaryKey + ",'" + this.name + "','" + remoteUrl + "/"
				+ this.id + "'," + this.size + ")";
	}
}
