package com.uas.erp.dao.common;

import net.sf.json.JSONObject;

public interface FeePleaseDao {
	JSONObject turnCLFBX(int id, String caller, Object master);

	int turnFYBX(int id, String caller);

	int turnYHFKSQ(int id, String caller);

	int turnYWZDBX(int id, String caller);

	int jksqturnFYBX(int id, String caller);

	public JSONObject turnBillAP(int id, double thisamount, String caller);

	public JSONObject turnBillARChange(int id, double thisamount, String caller);
}
