package com.uas.erp.service.scm;

public interface BarcodeSetService {

	public void saveSerail(String formStore, String gridStore, String caller);

	public void updateSerail(String formStore, String param, String caller);

	public void deleteSerail(String caller, int bs_id);

	public void auditSerail(String caller, int bs_id);

	public void bannedSerial(String caller, int bs_id);

	public void resBannedSerail(String caller, int bs_id);

	public void resAuditSerail(String caller, int bs_id);

	public void submitSerail(String caller, int bs_id);

	public void resSubmitSerail(String caller, int bs_id);

}
