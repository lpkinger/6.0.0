package com.uas.erp.service.pm;

public interface FeatureValueSetService {

	String getDataFieldByCondition(String tablename, String field, String condition, String caller);

	void updateDataFieldByCondition(String tablename, String[] field, String[] fieldvalue, String condition, String caller);
	
	String getRealCode(String prodcode, String specdescription, String fromwhere, String caller);

	Object[] getDataFieldsByCondition(String tablename, String[] field,
			String condition, String caller);
	
}
