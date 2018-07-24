package com.uas.erp.model;

import java.util.List;

public abstract interface Page<T> {
	
	public abstract int getTotalCount();
	
	public abstract List<T> getTarget();
	
}
