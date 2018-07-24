package com.uas.erp.service.scm;

import java.util.List;

import com.uas.erp.model.JSONTree;

public interface VendorKindService {
	void saveVendorKind(String formStore);
	void updateVendorKindById(String formStore);
	void deleteVendorKind(int vk_id);
	List<JSONTree> getJsonTrees(int parentid);
	void banned(int id);
	void resBanned(int id);
	String getVendorCodeByKind(String kind);
}
