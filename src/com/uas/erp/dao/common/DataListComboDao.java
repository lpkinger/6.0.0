package com.uas.erp.dao.common;

import java.util.List;
import com.uas.erp.model.DataListCombo;

public interface DataListComboDao {
	List<DataListCombo> getComboxsByCaller(String caller, String sob);
	List<DataListCombo> getComboxsByCallerAndField(String caller,String Field);
	List<DataListCombo> getComboxsByCallers(String callers);
}
