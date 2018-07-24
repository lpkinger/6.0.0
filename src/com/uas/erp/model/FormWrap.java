package com.uas.erp.model;

import java.io.Serializable;
import java.util.List;

public class FormWrap implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] callers;
	private List<Form> forms;
	private List<RelativeSearch> searchs;
	private List<DataListCombo> combos;
	private List<DetailGrid> grids;
	private List<DBFindSet> dbFindSets;
	private List<DBFindSetGrid> dbFindSetGrids;
	private List<DBFindSetUI> dbFindSetUIs;
	private List<GridButton> buttons;

	public List<Form> getForms() {
		return forms;
	}

	public void setForms(List<Form> forms) {
		this.forms = forms;
	}

	public String[] getCallers() {
		return callers;
	}

	public void setCallers(String[] callers) {
		this.callers = callers;
	}

	public List<DetailGrid> getGrids() {
		return grids;
	}

	public void setGrids(List<DetailGrid> grids) {
		this.grids = grids;
	}

	public List<RelativeSearch> getSearchs() {
		return searchs;
	}

	public void setSearchs(List<RelativeSearch> searchs) {
		this.searchs = searchs;
	}

	public List<DataListCombo> getCombos() {
		return combos;
	}

	public void setCombos(List<DataListCombo> combos) {
		this.combos = combos;
	}

	public List<DBFindSet> getDbFindSets() {
		return dbFindSets;
	}

	public void setDbFindSets(List<DBFindSet> dbFindSets) {
		this.dbFindSets = dbFindSets;
	}

	public List<DBFindSetGrid> getDbFindSetGrids() {
		return dbFindSetGrids;
	}

	public void setDbFindSetGrids(List<DBFindSetGrid> dbFindSetGrids) {
		this.dbFindSetGrids = dbFindSetGrids;
	}

	public List<DBFindSetUI> getDbFindSetUIs() {
		return dbFindSetUIs;
	}

	public void setDbFindSetUIs(List<DBFindSetUI> dbFindSetUIs) {
		this.dbFindSetUIs = dbFindSetUIs;
	}

	public List<GridButton> getButtons() {
		return buttons;
	}

	public void setButtons(List<GridButton> buttons) {
		this.buttons = buttons;
	}

}
