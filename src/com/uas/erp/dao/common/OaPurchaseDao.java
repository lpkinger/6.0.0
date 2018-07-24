package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.uas.erp.model.Employee;

public interface OaPurchaseDao {

	void checkPdYqty(List<Map<Object, Object>> maps);

	String turnAccept(String caller, List<Map<Object, Object>> list,
			Employee employee, String language);

	void deleteById(String string, String string2, int op_id);

	JSONObject newOAPurchaseWithVendor(String type, int parseInt,
			String vendcode, String vendname,  String currency);

	void toAppointedOAPurchase(Object code, int adid, double tqty);

	JSONObject getOAPurchasePrice(String vendcode, String prodcode, String currency, double qty, String od_date);

	String getPrice(int op_id);

}
