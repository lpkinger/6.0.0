package com.uas.erp.model;

import java.io.Serializable;
import java.util.List;

import com.uas.erp.model.Bench.BenchSceneGrid;

public class Filter implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String dataIndex;
	private String xtype = "textfield";
	private String filtertype;
	private boolean hideTrigger = false;
	private String queryMode = "local";
	private String displayField = "display";
	private String valueField = "value";
	private ComboStore store;
	private boolean autoDim = true;// 自动模糊匹配
	private boolean ignoreCase = false;// 不区分大小写
	private boolean exactSearch = false; // 是否精确查找
	private Object args;// 传参数

	public Object getArgs() {
		return args;
	}

	public void setArgs(Object args) {
		this.args = args;
	}

	public String getDataIndex() {
		return dataIndex;
	}

	public void setDataIndex(String dataIndex) {
		this.dataIndex = dataIndex;
	}

	public String getXtype() {
		return xtype;
	}

	public String getFiltertype() {
		return filtertype;
	}

	public void setFiltertype(String filtertype) {
		this.filtertype = filtertype;
	}

	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	public void setXtype(String xtype) {
		this.xtype = xtype;
	}

	public boolean isHideTrigger() {
		return hideTrigger;
	}

	public void setHideTrigger(boolean hideTrigger) {
		this.hideTrigger = hideTrigger;
	}

	public String getQueryMode() {
		return queryMode;
	}

	public void setQueryMode(String queryMode) {
		this.queryMode = queryMode;
	}

	public boolean isAutoDim() {
		return autoDim;
	}

	public void setAutoDim(boolean autoDim) {
		this.autoDim = autoDim;
	}

	public String getDisplayField() {
		return displayField;
	}

	public void setDisplayField(String displayField) {
		this.displayField = displayField;
	}

	public String getValueField() {
		return valueField;
	}

	public void setValueField(String valueField) {
		this.valueField = valueField;
	}

	public ComboStore getStore() {
		return store;
	}

	public void setStore(ComboStore store) {
		this.store = store;
	}

	public boolean isExactSearch() {
		return exactSearch;
	}

	public void setExactSearch(boolean exactSearch) {
		this.exactSearch = exactSearch;
	}

	public Filter() {

	}

	public Filter(DataListDetail detail, List<DataListCombo> combos, String language) {
		this.dataIndex = detail.getDld_field();
		if (dataIndex.contains(" ")) {
			String[] strs = dataIndex.split(" ");
			dataIndex = strs[strs.length - 1];
		}
		String type = detail.getDld_fieldtype();
		if (type != null) {
			if (type.equals("N")) {
				this.xtype = "textfield";
				this.filtertype = "numberfield";
				this.hideTrigger = true;
			} else if (type.equals("D")) {
				this.xtype = "datefield";
				this.hideTrigger = false;
			} else if (type.equals("DT")) {
				this.xtype = "datetimefield";
				this.hideTrigger = false;
			} else if (type.equals("C")) {
				this.xtype = "combo";
				this.hideTrigger = false;
				this.store = new ComboStore(combos, this.dataIndex, language);
			} else if (type.equals("LS")) {// 数据量较大，必须手动输入%
				this.xtype = "textfield";
				this.autoDim = false;
			} else if (type.equals("ES")) {
				this.xtype = "textfield";
				this.exactSearch = true;
			} else if (type.equals("CS")) {// 忽略大小写的text
				this.xtype = "textfield";
				this.ignoreCase = true;
			} else if (type.equals("MD")) {
				this.xtype = "monthdatefield";
				this.hideTrigger = false;
			} else if (type.matches("MONTH-[A-Z]")) {
				this.xtype = "monthdatefield";
				this.hideTrigger = false;
				this.args = type;
			} else if (type.equals("yncolumn")) {
				this.xtype = "combo";
				this.hideTrigger = false;
				this.store = ComboStore.yesnoCombo(dataIndex);
			}else if(type.equals("F")||type.matches("^F\\d{1}$")){
				this.xtype = "numberfield";
			} 
			else {
				this.xtype = "textfield";
			}
		}
	}
	
	public Filter(BenchSceneGrid detail, List<DataListCombo> combos, String language) {
		this.dataIndex = detail.getSg_field();
		if (dataIndex.contains(" ")) {
			String[] strs = dataIndex.split(" ");
			dataIndex = strs[strs.length - 1];
		}
		String type = detail.getSg_type();
		if (type != null) {
			if (type.equals("N")) {
				this.xtype = "textfield";
				this.filtertype = "numberfield";
				this.hideTrigger = true;
			} else if (type.equals("D")) {
				this.xtype = "datefield";
				this.hideTrigger = false;
			} else if (type.equals("DT")) {
				this.xtype = "datetimefield";
				this.hideTrigger = false;
			} else if (type.equals("C")) {
				this.xtype = "combo";
				this.hideTrigger = false;
				this.store = new ComboStore(combos, this.dataIndex, language);
			} else if (type.equals("LS")) {// 数据量较大，必须手动输入%
				this.xtype = "textfield";
				this.autoDim = false;
			} else if (type.equals("ES")) {
				this.xtype = "textfield";
				this.exactSearch = true;
			} else if (type.equals("CS")) {// 忽略大小写的text
				this.xtype = "textfield";
				this.ignoreCase = true;
			} else if (type.equals("MD")) {
				this.xtype = "monthdatefield";
				this.hideTrigger = false;
			} else if (type.matches("MONTH-[A-Z]")) {
				this.xtype = "monthdatefield";
				this.hideTrigger = false;
				this.args = type;
			} else if (type.equals("yncolumn")) {
				this.xtype = "combo";
				this.hideTrigger = false;
				this.store = ComboStore.yesnoCombo(dataIndex);
			}else if(type.equals("F")||type.matches("^F\\d{1}$")){
				this.xtype = "numberfield";
			} 
			else {
				this.xtype = "textfield";
			}
		}
	}

	public Filter(DetailGrid detail, List<DataListCombo> combos, String language) {
		this.dataIndex = detail.getDg_field();
		String type = detail.getDg_type();
		if (type != null) {
			if (type.equals("numbercolumn") || type.startsWith("floatcolumn")) {
				this.xtype = "numberfield";
				this.hideTrigger = true;
			} else if (type.equals("datecolumn")) {
				this.xtype = "datefield";
				this.hideTrigger = false;
			} else if (type.equals("datetimecolumn")) {
				this.xtype = "datetimefield";
				this.hideTrigger = false;
			} else if (type.equals("combo")) {
				this.xtype = "combo";
				this.hideTrigger = false;
				this.store = new ComboStore(combos, this.dataIndex, language);
			} else {
				this.xtype = "textfield";
			}
		}
	}

	public Filter(DBFindSetDetail detail, List<DataListCombo> combos, String language) {
		this.dataIndex = detail.getDd_fieldname();
		String type = detail.getDd_fieldtype();
		if (type != null) {
			if (type.equals("N")) {
				this.xtype = "numberfield";
				this.hideTrigger = true;
			} else if (type.equals("D")) {
				this.xtype = "datefield";
				this.hideTrigger = false;
			} else if (type.equals("DT")) {
				this.xtype = "datetimefield";
				this.hideTrigger = false;
			} else if (type.equals("C")) {
				this.xtype = "combo";
				this.hideTrigger = false;
				this.store = new ComboStore(combos, this.dataIndex, language);
			} else if (type.equals("yncolumn")) {
				this.xtype = "combo";
				this.hideTrigger = false;
				this.store = ComboStore.yesnoCombo(dataIndex);
			}else if (type.equals("LS")) {// 数据量较大，必须手动输入%
				this.xtype = "textfield";
				this.autoDim = false;
			} else if (type.equals("ES")) {
				this.xtype = "textfield";
				this.exactSearch = true;
			} else if (type.equals("CS")) {// 忽略大小写的text
				this.xtype = "textfield";
				this.ignoreCase = true;
			}else {
				this.xtype = "textfield";
			}
		}
	}

	public Filter(RelativeSearch.Grid grid) {
		this.dataIndex = grid.getRsg_field();
		String type = grid.getRsg_type();
		if (type != null) {
			if (type.equals("numbercolumn")) {
				this.xtype = "textfield";
				this.filtertype = "numberfield";
				this.hideTrigger = true;
			} else if (type.equals("datecolumn")) {
				this.xtype = "datefield";
				this.hideTrigger = false;
			} else if (type.equals("datetimecolumn")) {
				this.xtype = "datetimefield";
				this.hideTrigger = false;
			} else if (type.equals("combocolumn")) {
				this.xtype = "combo";
				this.hideTrigger = false;
				//关联查询Grid中设置为下拉框时，获取rsg_combovalue下拉框配置
				this.store = new ComboStore(grid.getRsg_combovalue());
			} else {
				this.xtype = "textfield";
			}
		}
	}
	public Filter(String field, String type,List<DataListCombo> combos, String language) {
		this.dataIndex = field;
		if (dataIndex.contains(" ")) {
			String[] strs = dataIndex.split(" ");
			dataIndex = strs[strs.length - 1];
		}
		if (type != null) {
			if (type.equals("N")) {
				this.xtype = "textfield";
				this.filtertype = "numberfield";
				this.hideTrigger = true;
			} else if (type.equals("D")) {
				this.xtype = "datefield";
				this.hideTrigger = false;
			} else if (type.equals("DT")) {
				this.xtype = "datetimefield";
				this.hideTrigger = false;
			} else if (type.equals("C")) {
				this.xtype = "combo";
				this.hideTrigger = false;
				this.store = new ComboStore(combos, this.dataIndex, language);
			} else if (type.equals("YN")) {
				this.xtype = "combo";
				this.hideTrigger = false;
				this.store = ComboStore.yesnoCombo(dataIndex);
			}else if (type.equals("CS")) {// 忽略大小写的text
				this.xtype = "textfield";
				this.ignoreCase = true;
			}else {
				this.xtype = "textfield";
			}
		}
	}
}
