package com.uas.erp.model;

import java.io.Serializable;

public class GridFields implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String type = "string";
	private String format = "";
	private boolean useNull=false;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public GridFields() {

	}

	public GridFields(DetailGrid grid) {
		this.name = grid.getDg_field();
		if (name.contains(" ")) {// column有取别名
			String[] strs = name.split(" ");
			name = strs[strs.length - 1];
		}
		if (grid.getDg_type().equals("numbercolumn")) {
			this.type = "number";
			this.format = "";
		} else if (grid.getDg_type().equals("floatcolumn")) {
			this.type = "number";
			this.format = "0.00";
		} else if (grid.getDg_type().matches("floatcolumn\\d{1}")||grid.getDg_type().matches("nfloatcolumn\\d{1}")) {//  nfloatcolumn自动展示小数位  类型 
			this.type = "number";
			this.format = "0.";
			int length = 0;
			if(grid.getDg_type().indexOf("nfloatcolumn")>-1){
				length = Integer.parseInt(grid.getDg_type().replace("nfloatcolumn", ""));
			}else{
				length = Integer.parseInt(grid.getDg_type().replace("floatcolumn", ""));
			}
			for (int i = 0; i < length; i++) {
				this.format += "0";
			}
		} else if (grid.getDg_type().matches("nullnumbercolumn\\d{1}")) {
			this.type = "number";
			this.format = "0.";
			this.useNull = true;
			int length = Integer.parseInt(grid.getDg_type().replace("nullnumbercolumn", ""));
			for (int i = 0; i < length; i++) {
				this.format += "0";
			}
		} else if (grid.getDg_type().equals("datecolumn")) {
			this.type = "date";
			this.format = "Y-m-d";
		} else if (grid.getDg_type().equals("datetimecolumn")) {
			this.type = "date";
			this.format = "Y-m-d H:i:s";
		} else if (grid.getDg_type().contains("booleancolumn") || grid.getDg_type().contains("boolean")) {
			this.type = "boolean";
		} else if (grid.getDg_type().contains("checkcolumn")) {
			this.type = "bool";
		} else if (grid.getDg_type().contains("tfcolumn")) {
			this.type = "tf";
		} else if (grid.getDg_type().contains("yncolumn")) {
			this.type = "yn";
		} else {
			this.type = "string";
			this.format = "";
		}
	}

	public GridFields(DataListDetail detail) {
		this.name = detail.getDld_field();
		if (name.contains(" ")) {// column有取别名
			String[] strs = name.split(" ");
			name = strs[strs.length - 1];
		}
		if (detail.getDld_fieldtype().equals("N")) {
			this.type = "number";
			this.format = "0";
		} else if (detail.getDld_fieldtype().equals("D")) {
			this.type = "date";
			this.format = "Y-m-d";
		} else if (detail.getDld_fieldtype().equals("DT")) {
			this.type = "date";
			this.format = "Y-m-d H:i:s";
		} else {
			this.format = "";
		}
	}

	public GridFields(DBFindSetDetail detail) {
		this.name = detail.getDd_fieldname();
		if (name.contains(" ")) {// column有取别名
			String[] strs = name.split(" ");
			name = strs[strs.length - 1];
		}
	}

	public GridFields(String field) {
		this.name = field;
		if (name.contains("."))
			name = name.replace(".", "_");
		else if (name.contains(" ")) {
			String[] strs = name.split(" ");
			name = strs[strs.length - 1];
		}
	}

	public GridFields(RelativeSearch.Grid grid) {
		this.name = grid.getRsg_field();
		if (name.contains(" ")) {// column有取别名
			String[] strs = name.split(" ");
			name = strs[strs.length - 1];
		}
		if ("numbercolumn".equals(grid.getRsg_type())) {
			this.type = "number";
			this.format = "";
		} else if ("floatcolumn".equals(grid.getRsg_type())) {
			this.type = "number";
			this.format = "0.00";
		} else if (grid.getRsg_type().matches("floatcolumn\\d{1}")) {
			this.type = "number";
			this.format = "0.";
			int length = Integer.parseInt(grid.getRsg_type().replace("floatcolumn", ""));
			for (int i = 0; i < length; i++) {
				this.format += "0";
			}
		} else if ("datecolumn".equals(grid.getRsg_type())) {
			this.type = "date";
			this.format = "Y-m-d";
		} else if ("datetimecolumn".equals(grid.getRsg_type())) {
			this.type = "date";
			this.format = "Y-m-d H:i:s";
		} else if (grid.getRsg_type().contains("booleancolumn") || grid.getRsg_type().contains("boolean")) {
			this.type = "boolean";
		} else if (grid.getRsg_type().contains("checkcolumn")) {
			this.type = "bool";
		} else if (grid.getRsg_type().contains("tfcolumn")) {
			this.type = "tf";
		} else if (grid.getRsg_type().contains("yncolumn")) {
			this.type = "yn";
		} else {
			this.type = "string";
			this.format = "";
		}
	}
	
	public GridFields(String field,String type) {
		this.name = field;
		if (name.contains("."))
			name = name.replace(".", "_");
		else if (name.contains(" ")) {
			String[] strs = name.split(" ");
			name = strs[strs.length - 1];
		}
		if(type.equals("N")){
			this.type = "number";
			this.format = "0";
		} else if (type.equals("D")) {
			this.type = "date";
			this.format = "Y-m-d";
		} else if (type.equals("DT")) {
			this.type = "date";
			this.format = "Y-m-d H:i:s";
		} else {
			this.format = "";
		}
	}

	public boolean isUseNull() {
		return useNull;
	}

	public void setUseNull(boolean useNull) {
		this.useNull = useNull;
	}
}
