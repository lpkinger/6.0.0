package com.uas.erp.service.ma;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.RoleSpecialPower;
import com.uas.erp.model.SpecialPower;
import com.uas.erp.model.SysSpecialPower;

public interface PowerService {

	void save(String save);

	void update(String update);

	void delete(int id);

	List<SysSpecialPower> getSysSpecialPowers(String caller);

	List<SpecialPower> getSpecialPowerByEmpl(String caller, Integer em_id);

	List<SpecialPower> getSpecialPowerByJob(String caller, Integer jo_id);

	List<RoleSpecialPower> getSpecialPowerByRole(String caller, Integer ro_id);
	
	void saveSysSpecialPowers(String caller, String data);

	void copyPower(int fromId, String toIds);

	void copypowerFromStandard(String param);

	void deleteSysSpecialPowerById(int id,Integer sbid);

	String compareJobPower(String jobs);

	void syncPower(String type,String to, String data);

	int getPowerCount(String condition,String tableName);

	List<Map<String, Object>> getPowerData( String condition, int page, int pageSize,String tableName);

	void refreshPower(String to);
	
	List<Map<String,Object>> getSceneBtnPowers(String benchcode, Integer joid, Integer emid);
	
	void saveSceneBtnPowers(String benchcode, String joid, String emid,String data);
}
