package com.uas.erp.service.b2c;

import java.util.Map;

public interface B2CSettingService {

	void saveB2CCustomer(String param, String caller);

	void saveB2CVendor(String param, String caller);

	void startB2C(String caller);

	void saveB2CSaleKind(String param, String caller);

	Map<String,Object> getB2CSetting(String caller);

}
