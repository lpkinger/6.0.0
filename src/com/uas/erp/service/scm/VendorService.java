package com.uas.erp.service.scm;

public interface VendorService {
	void saveVendor(String formStore, String caller);

	int saveVendorSimple(String formStore);

	boolean checkVendorByEnId(int ve_enid, int ve_otherenid);

	void updateVendor(String formStore, String caller);

	void deleteVendor(int ve_id, String caller);

	void auditVendor(int ve_id, String caller);

	void resAuditVendor(int ve_id, String caller);

	void submitVendor(int ve_id, String caller);

	void resSubmitVendor(int ve_id, String caller);

	void bannedVendor(int ve_id, String caller);

	void resBannedVendor(int ve_id, String caller);

	void updateUU(Integer id, String uu, String name, String shortName, String isb2b, String b2bcheck,boolean checked, String caller, String ve_webserver, String ve_legalman, String ve_add1);

	void batchUpdateVendor(String data, String caller);

	void updateLevel(Integer id, String ve_level, String caller);

	void updateInfo(int id, String text);

	String getVendorKindNum(String vk_kind);

	void checkVendorUU(String data,String caller);

	void regB2BVendor(int id);

	void updateB2BPro(String data, String cond);
}
