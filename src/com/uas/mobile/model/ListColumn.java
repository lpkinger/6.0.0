package com.uas.mobile.model;
import com.uas.erp.model.DataListDetail;
import com.uas.erp.model.DetailGrid;
public class ListColumn {
	private String  dataIndex;
	private String  caption;
	private int     width;
	private String  type;
	private String  format;
	private String  render;
	public String getDataIndex() {
		return dataIndex;
	}
	public void setDataIndex(String dataIndex) {
		this.dataIndex = dataIndex;
	}
	public String getCaption() {
		return caption;
	}
	public void setText(String text) {
		this.caption = text;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
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
	
	public String getRender() {
		return render;
	}
	public void setRender(String render) {
		this.render = render;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	public ListColumn(){

	}
	public ListColumn(DataListDetail detail, String language){
		this.dataIndex = detail.getDld_field();
		if (dataIndex.contains(" ")) {// column有取别名
			String[] strs = dataIndex.split(" ");
			dataIndex = strs[strs.length - 1];
		}
		if (language.equals("en_US")) {
			this.caption=detail.getDld_caption_en();
		} else if (language.equals("zh_TW")) {
			this.caption = detail.getDld_caption_fan();
		} else {
			this.caption = detail.getDld_caption();
		}
		this.width = detail.getDld_width();
		String type = detail.getDld_fieldtype();
		if (type.equals("N")) {
			this.type = "numberfield";
			this.format = "0,000";
		} else if (type.equals("F")) {
			this.type = "numberfield";
			this.format = "0,000.00";
		} else if (type.matches("^F\\d{1}$")) {
			this.type = "numberfield";
			this.format = "0,000.";
			int length = Integer.parseInt(type.replace("F", ""));
			for (int i = 0; i < length; i++) {
				this.format += "0";
			}
		} else if (type.equals("D")) {
			this.type = "datefield";
			this.format = "Y-m-d";
		} else if (type.equals("DT")) {
			this.type = "datetimefield";
			this.format = "Y-m-d H:i:s";
		}else if("H".equals(type)){
			this.width=0;
		}
	}
	public ListColumn(DetailGrid detail, String language){
		this.dataIndex = detail.getDg_field();
		if(language==null) language="zh_CN";
		if (language.equals("en_US")) {
			this.caption=detail.getDg_captionen();
		} else if (language.equals("zh_TW")) {
			this.caption = detail.getDg_captionfan();
		} else {
			this.caption = detail.getDg_caption();
		}
		type=detail.getDg_type();
		if (type.equals("numbercolumn")) {
			this.format = "0,000";
			this.type = "numberfield";
		} else if (type.equals("floatcolumn")) {
			this.type = "numberfield";
			this.format = "0,000.00";
		} else if (type.matches("^floatcolumn\\d{1}$")) {
			int length = Integer.parseInt(type.replace("floatcolumn", ""));
			for (int i = 0; i < length; i++) {
				this.format += "0";
			}
		} else if (type.contains("datecolumn")) {
			this.type="datefield";
			this.format = "Y-m-d";
		} else if (type.contains("datetimecolumn")) {
			this.type="datetimefield";
			this.format="Y-m-d H:m:s";
		}else {
			this.type="textfield";
		}
		this.width = detail.getDg_width();
		if(detail.getDg_logictype()!=null && detail.getDg_logictype().startsWith("jsps/scm/product/productBase.jsp")){
			this.render="PRODUCT";
		}
	}
	public ListColumn(String dataIndex,String caption,int width){
		this.dataIndex=dataIndex;
		this.caption=caption;
		this.width=width;
		this.type="textfield";
	}
}