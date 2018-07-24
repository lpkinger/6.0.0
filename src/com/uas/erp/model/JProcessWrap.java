package com.uas.erp.model;

import java.util.List;

public class JProcessWrap {

	private JProcessSet jProcessSet;
	private JProcessDeploy jProcessDeploy;
	private List<JprocessButton> buttons;

	public JProcessSet getjProcessSet() {
		return jProcessSet;
	}

	public void setjProcessSet(JProcessSet jProcessSet) {
		this.jProcessSet = jProcessSet;
	}

	public JProcessDeploy getjProcessDeploy() {
		return jProcessDeploy;
	}

	public void setjProcessDeploy(JProcessDeploy jProcessDeploy) {
		this.jProcessDeploy = jProcessDeploy;
	}

	public List<JprocessButton> getButtons() {
		return buttons;
	}

	public void setButtons(List<JprocessButton> buttons) {
		this.buttons = buttons;
	}

}
