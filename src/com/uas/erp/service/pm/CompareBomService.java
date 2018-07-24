package com.uas.erp.service.pm;

import com.uas.erp.model.GridPanel;

public interface CompareBomService {

	GridPanel getGridData(String condition,boolean bd_single, boolean bd_difbom,String caller);
}
