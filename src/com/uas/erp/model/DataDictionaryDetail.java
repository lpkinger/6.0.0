package com.uas.erp.model;

import java.io.Serializable;
import java.util.List;

public class DataDictionaryDetail implements Serializable {

	/**
	 * 采用oracle自带视图
	 */
	private static final long serialVersionUID = 1L;

	private String table_name;
	private String column_name;
	private String data_type;
	private Integer data_length;
	private String comments;
	private String nullable;
	private List<Link> links;

	public String getTable_name() {
		return table_name;
	}

	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}

	public String getColumn_name() {
		return column_name;
	}

	public void setColumn_name(String column_name) {
		this.column_name = column_name.toLowerCase();
	}

	public String getData_type() {
		return data_type;
	}

	public void setData_type(String data_type) {
		this.data_type = data_type;
	}

	public Integer getData_length() {
		return data_length;
	}

	public void setData_length(Integer data_length) {
		this.data_length = data_length;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getNullable() {
		return nullable;
	}

	public void setNullable(String nullable) {
		this.nullable = nullable;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public static class Link implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String dl_title;
		private String dl_tablename;
		private String dl_fieldname;
		private String dl_link;
		private String dl_tokentab1;
		private String dl_tokencol1;
		private String dl_tokentab2;
		private String dl_tokencol2;

		public String getDl_title() {
			return dl_title;
		}

		public void setDl_title(String dl_title) {
			this.dl_title = dl_title;
		}

		public String getDl_tablename() {
			return dl_tablename;
		}

		public void setDl_tablename(String dl_tablename) {
			this.dl_tablename = dl_tablename;
		}

		public String getDl_fieldname() {
			return dl_fieldname;
		}

		public void setDl_fieldname(String dl_fieldname) {
			this.dl_fieldname = dl_fieldname;
		}

		public String getDl_link() {
			return dl_link;
		}

		public void setDl_link(String dl_link) {
			this.dl_link = dl_link;
		}

		public String getDl_tokentab1() {
			return dl_tokentab1;
		}

		public void setDl_tokentab1(String dl_tokentab1) {
			this.dl_tokentab1 = dl_tokentab1;
		}

		public String getDl_tokencol1() {
			return dl_tokencol1;
		}

		public void setDl_tokencol1(String dl_tokencol1) {
			this.dl_tokencol1 = dl_tokencol1;
		}

		public String getDl_tokentab2() {
			return dl_tokentab2;
		}

		public void setDl_tokentab2(String dl_tokentab2) {
			this.dl_tokentab2 = dl_tokentab2;
		}

		public String getDl_tokencol2() {
			return dl_tokencol2;
		}

		public void setDl_tokencol2(String dl_tokencol2) {
			this.dl_tokencol2 = dl_tokencol2;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((dl_fieldname == null) ? 0 : dl_fieldname.hashCode());
			result = prime * result + ((dl_link == null) ? 0 : dl_link.hashCode());
			result = prime * result + ((dl_tablename == null) ? 0 : dl_tablename.hashCode());
			result = prime * result + ((dl_title == null) ? 0 : dl_title.hashCode());
			result = prime * result + ((dl_tokencol1 == null) ? 0 : dl_tokencol1.hashCode());
			result = prime * result + ((dl_tokencol2 == null) ? 0 : dl_tokencol2.hashCode());
			result = prime * result + ((dl_tokentab1 == null) ? 0 : dl_tokentab1.hashCode());
			result = prime * result + ((dl_tokentab2 == null) ? 0 : dl_tokentab2.hashCode());
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
			Link other = (Link) obj;
			if (dl_fieldname == null) {
				if (other.dl_fieldname != null)
					return false;
			} else if (!dl_fieldname.equals(other.dl_fieldname))
				return false;
			if (dl_link == null) {
				if (other.dl_link != null)
					return false;
			} else if (!dl_link.equals(other.dl_link))
				return false;
			if (dl_tablename == null) {
				if (other.dl_tablename != null)
					return false;
			} else if (!dl_tablename.equals(other.dl_tablename))
				return false;
			if (dl_title == null) {
				if (other.dl_title != null)
					return false;
			} else if (!dl_title.equals(other.dl_title))
				return false;
			if (dl_tokencol1 == null) {
				if (other.dl_tokencol1 != null)
					return false;
			} else if (!dl_tokencol1.equals(other.dl_tokencol1))
				return false;
			if (dl_tokencol2 == null) {
				if (other.dl_tokencol2 != null)
					return false;
			} else if (!dl_tokencol2.equals(other.dl_tokencol2))
				return false;
			if (dl_tokentab1 == null) {
				if (other.dl_tokentab1 != null)
					return false;
			} else if (!dl_tokentab1.equals(other.dl_tokentab1))
				return false;
			if (dl_tokentab2 == null) {
				if (other.dl_tokentab2 != null)
					return false;
			} else if (!dl_tokentab2.equals(other.dl_tokentab2))
				return false;
			return true;
		}
	}
}
