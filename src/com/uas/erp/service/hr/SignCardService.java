package com.uas.erp.service.hr;

import java.text.ParseException;


public interface SignCardService {
	void saveSignCard(String formStore, String  caller) throws ParseException;
	void updateSignCard(String formStore, String  caller) throws ParseException;
	void deleteSignCard(int sc_id, String  caller);
	void auditSignCard(int sc_id, String  caller);
	void resAuditSignCard(int sc_id, String  caller);
	void submitSignCard(int sc_id, String  caller);
	void resSubmitSignCard(int sc_id, String  caller);
	void endSignCard(int sc_id, String caller);
	void resEndSignCard(int sc_id, String caller);
}
