package com.uas.erp.service.scm;

public interface PackageStandardService {
	void savePackageStandard(String formStore, String caller);
	void updatePackageStandardById(String formStore, String caller);
	void deletePackageStandard(int ps_id, String caller);
}
