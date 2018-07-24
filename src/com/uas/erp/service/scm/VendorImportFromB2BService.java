package com.uas.erp.service.scm;

import java.util.Map;

public interface VendorImportFromB2BService {
	
	public Map<String, Object> getVendorImportFromB2B(String caller,String condition, Integer start, Integer page, Integer limit);

	public Map<String, Object> importVendorFromB2B(String caller, String formStore);

	public Map<String, Object> getVendorImpoertProdDetail(String caller, String en_uu, String productMatchCondition, String whereCondition, Integer start, Integer page, Integer limit);

	Map<String, Object> getVendorFormB2B(String caller, String field, String condition,String enUU);

}
