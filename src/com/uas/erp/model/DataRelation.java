package com.uas.erp.model;

import java.io.Serializable;

public class DataRelation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String table_name_x;
	private String table_name_y;
	private String table_name_z;
	private String col_x_1;
	private String col_x_2;
	private String col_y_1;
	private String col_y_2;
	private String col_z_1;
	private String col_z_x_1;
	private String col_z_y_1;
	private Short prior_;

	public String getTable_name_x() {
		return table_name_x;
	}

	public void setTable_name_x(String table_name_x) {
		this.table_name_x = table_name_x;
	}

	public String getTable_name_y() {
		return table_name_y;
	}

	public void setTable_name_y(String table_name_y) {
		this.table_name_y = table_name_y;
	}

	public String getTable_name_z() {
		return table_name_z;
	}

	public void setTable_name_z(String table_name_z) {
		this.table_name_z = table_name_z;
	}

	public String getCol_x_1() {
		return col_x_1;
	}

	public void setCol_x_1(String col_x_1) {
		this.col_x_1 = col_x_1;
	}

	public String getCol_x_2() {
		return col_x_2;
	}

	public void setCol_x_2(String col_x_2) {
		this.col_x_2 = col_x_2;
	}

	public String getCol_y_1() {
		return col_y_1;
	}

	public void setCol_y_1(String col_y_1) {
		this.col_y_1 = col_y_1;
	}

	public String getCol_y_2() {
		return col_y_2;
	}

	public void setCol_y_2(String col_y_2) {
		this.col_y_2 = col_y_2;
	}

	public String getCol_z_1() {
		return col_z_1;
	}

	public void setCol_z_1(String col_z_1) {
		this.col_z_1 = col_z_1;
	}

	public String getCol_z_x_1() {
		return col_z_x_1;
	}

	public void setCol_z_x_1(String col_z_x_1) {
		this.col_z_x_1 = col_z_x_1;
	}

	public String getCol_z_y_1() {
		return col_z_y_1;
	}

	public void setCol_z_y_1(String col_z_y_1) {
		this.col_z_y_1 = col_z_y_1;
	}

	public Short getPrior_() {
		return prior_;
	}

	public void setPrior_(Short prior_) {
		this.prior_ = prior_;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((col_x_1 == null) ? 0 : col_x_1.hashCode());
		result = prime * result + ((col_x_2 == null) ? 0 : col_x_2.hashCode());
		result = prime * result + ((col_y_1 == null) ? 0 : col_y_1.hashCode());
		result = prime * result + ((col_y_2 == null) ? 0 : col_y_2.hashCode());
		result = prime * result + ((col_z_1 == null) ? 0 : col_z_1.hashCode());
		result = prime * result + ((col_z_x_1 == null) ? 0 : col_z_x_1.hashCode());
		result = prime * result + ((col_z_y_1 == null) ? 0 : col_z_y_1.hashCode());
		result = prime * result + ((prior_ == null) ? 0 : prior_.hashCode());
		result = prime * result + ((table_name_x == null) ? 0 : table_name_x.hashCode());
		result = prime * result + ((table_name_y == null) ? 0 : table_name_y.hashCode());
		result = prime * result + ((table_name_z == null) ? 0 : table_name_z.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataRelation other = (DataRelation) obj;
		if (col_x_1 == null) {
			if (other.col_x_1 != null)
				return false;
		} else if (!col_x_1.equals(other.col_x_1))
			return false;
		if (col_x_2 == null) {
			if (other.col_x_2 != null)
				return false;
		} else if (!col_x_2.equals(other.col_x_2))
			return false;
		if (col_y_1 == null) {
			if (other.col_y_1 != null)
				return false;
		} else if (!col_y_1.equals(other.col_y_1))
			return false;
		if (col_y_2 == null) {
			if (other.col_y_2 != null)
				return false;
		} else if (!col_y_2.equals(other.col_y_2))
			return false;
		if (col_z_1 == null) {
			if (other.col_z_1 != null)
				return false;
		} else if (!col_z_1.equals(other.col_z_1))
			return false;
		if (col_z_x_1 == null) {
			if (other.col_z_x_1 != null)
				return false;
		} else if (!col_z_x_1.equals(other.col_z_x_1))
			return false;
		if (col_z_y_1 == null) {
			if (other.col_z_y_1 != null)
				return false;
		} else if (!col_z_y_1.equals(other.col_z_y_1))
			return false;
		if (prior_ == null) {
			if (other.prior_ != null)
				return false;
		} else if (!prior_.equals(other.prior_))
			return false;
		if (table_name_x == null) {
			if (other.table_name_x != null)
				return false;
		} else if (!table_name_x.equals(other.table_name_x))
			return false;
		if (table_name_y == null) {
			if (other.table_name_y != null)
				return false;
		} else if (!table_name_y.equals(other.table_name_y))
			return false;
		if (table_name_z == null) {
			if (other.table_name_z != null)
				return false;
		} else if (!table_name_z.equals(other.table_name_z))
			return false;
		return true;
	}

}
