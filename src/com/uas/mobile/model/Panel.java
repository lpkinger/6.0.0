package com.uas.mobile.model;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
public class Panel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<PanelItem> panelItems;
	private List<ListColumn> columns;
	private List<Map<String,Object>> listdata;
	private Map<String,Object> formdata;


	public List<PanelItem> getPanelItems() {
		return panelItems;
	}
	public void setPanelItems(List<PanelItem> panelItems) {
		this.panelItems = panelItems;
	}
	public List<ListColumn> getColumns() {
		return columns;
	}
	public void setColumns(List<ListColumn> columns) {
		this.columns = columns;
	}
	public List<Map<String, Object>> getListdata() {
		return listdata;
	}
	public void setListdata(List<Map<String, Object>> listdata) {
		this.listdata = listdata;
	}
	public Map<String, Object> getFormdata() {
		return formdata;
	}
	public void setFormdata(Map<String, Object> formdata) {
		this.formdata = formdata;
	}
}
