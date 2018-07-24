package com.uas.erp.service.scm;

import java.util.List;
import java.util.Map;

public interface ProductVendorService {
	void updateProductVendorById(String formStore, String gridStore, String caller);
	List<Map<String, Object>>  loadProductVendor( String prodcode);
	void updateVendorRate(String gridStore,String formStore,String caller);
}
