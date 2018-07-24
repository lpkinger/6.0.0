package com.uas.erp.service.pm;

import java.util.Map;

public interface PackageTransferService {

	void getPackageDetailSerial(String condition);

	Map<String,Object> generateNewPackage(double pa_totalqtynew, String pa_oldcode);

	Map<String,Object> getFormTStore(String condition);

}
