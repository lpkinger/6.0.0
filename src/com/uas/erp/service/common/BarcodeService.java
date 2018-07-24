package com.uas.erp.service.common;

import java.util.List;
import java.util.Map;

public interface BarcodeService {
	public List <Map<String, Object>> barcodePrint(String caller,String lps_caller,String gridStore,String printForm);

	public List <Map<String, Object>> barcodePrintAll(String caller, String lps_caller,String printStore,
			String printForm);

	public List <Map<String, Object>> printPurBarcode(String caller, String gridStore, String printForm);

	public List <Map<String, Object>> printAllPurBarcode(String caller, String printStore,
			String printForm);

	public void updatePrintStatus(String caller, String ids);

	public void updatePurPrintStatus(String caller, String ids);

}