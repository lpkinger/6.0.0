package com.uas.erp.model.excel;

import java.io.Serializable;

public class SheetTabIdOrderInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3564482361354207270L;
	
	private Integer tabId;

	private Integer tabOrder;

	public SheetTabIdOrderInfo() {
		super();
	}

	public Integer getTabId() {
		return tabId;
	}

	public void setTabId(Integer tabId) {
		this.tabId = tabId;
	}

	public Integer getTabOrder() {
		return tabOrder;
	}

	public void setTabOrder(Integer tabOrder) {
		this.tabOrder = tabOrder;
	}

	

}
