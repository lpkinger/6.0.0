package com.uas.erp.service.oa;
public interface DocumentPowerService {
	public void setDocPower(String folderoId,String powers,String objects, int sub);
	public void updatePowerSet(String param);
	public void deletePowerSet(String param);
	public void checkSeePower(int folderId);
	public void checkDeletePower(int folderId);
	public void checkSavePower(int folderId);
	public void checkControlPower(int folderId);
	public void checkReadPower(int folderId);
	public void checkDownloadPower(int folderId);
	public void checkPrintPower(int folderId);
	public void checkSharePower(int folderId);
	public void checkJouranalPower(int folderId);
	public boolean CheckPowerByFolderId(int folderId, String type);
}
