package com.uas.erp.service.hr;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.DataListDetail;
import com.uas.erp.model.FormDetail;
import com.uas.erp.model.PersonalPower;
import com.uas.erp.model.PositionPower;
import com.uas.erp.model.RolePower;

public interface JobPowerService {
	void update(String update, String caller, Boolean _self);
	
	void updateRolePower(String update, String caller, Boolean _self);
	
	List<PositionPower> getPositionPowersByCaller(String caller);
	
	List<RolePower> getRolePowersByCaller(String caller);
	
	List<PersonalPower> getPersonalPowersByCaller(String caller);

	List<PersonalPower> getPersonalPowersByEm(String caller, String emid);
	
	List<FormDetail> getFormDetails(String caller);
	
	Map<String, Object>getDetailGrids(String caller);

	Map<String, List<DataListDetail>> getDataList(String caller);

	void saveLimitFields(String data, String caller, String relativeCaller, int id, Boolean _self, Boolean islist);
	
	void saveRoleLimitFields(String data, String caller, String relativeCaller, int id, Boolean _self, Boolean islist);
	
	/**
	 * @param data
	 * @param jo_id
	 * @param _self
	 */
	void saveSpecialPower(String caller,String data, int jo_id, Boolean _self);

	void saveRoleSpecialPower(String caller,String data, int ro_id, Boolean _self);
	
	void savePowerLog(Object caller, Object joid, Object emid, String type,String powerstring);

	String vastPostPower(String caller, String to);

	void vastRefreshPower();

	List<Map<String, Object>> getRelativeSearchs(String caller);

}
