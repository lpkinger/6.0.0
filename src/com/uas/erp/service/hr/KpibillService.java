package com.uas.erp.service.hr;

import java.text.ParseException;
import java.util.Map;

public interface KpibillService {
	void updateKpibill(String formStore, String gridStore, String caller);
	void deleteKpibill(int kb_id, String caller);
	void submitKpibill(int kb_id, String  caller) throws ParseException;
	void resSubmitKpibill(int kb_id, String  caller) throws ParseException;
	String[] printKpibill(int kb_id, String  caller, String reportName, String condition);
	Map<String, Object> getScorefrom(String kt_kdbid,String kt_bemanid,String ktd_kiid);
}
