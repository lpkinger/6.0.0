package com.uas.erp.service.scm;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface BarcodeScanService {
	List<?> getProdioBarcode(int piid, boolean iswcj);

	void insertProdioBarcode(int piid, String inoutno, String lotNo,String DateCode,String remark, String prcode, int qty);

	void deleteProdioBarcode(int piid, String inoutno, String lotNo, String prcode);

	void clearProdioBarcode(int piid, String prcode);
	Map<String, Object> printBarcode(int id, String reportName, HttpServletRequest request);
}
