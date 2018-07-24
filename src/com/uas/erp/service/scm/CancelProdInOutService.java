package com.uas.erp.service.scm;

import java.util.Map;

import com.uas.erp.model.Employee;
import com.uas.erp.model.FormPanel;
import com.uas.erp.model.GridPanel;

public interface CancelProdInOutService {
	FormPanel getFormItemsByCaller(String caller, String condition, String language, Employee employee, boolean isCloud);

	Map<String, Object> getFormData(String caller, String condition, boolean isCloud);
	
	GridPanel getGridPanelByCaller(String caller, String condition, Integer start,
			Integer end, Integer _m, boolean isCloud,String _copyConf, Employee employee);
}
