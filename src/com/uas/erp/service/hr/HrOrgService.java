package com.uas.erp.service.hr;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import com.uas.erp.model.CheckBoxTree;
import com.uas.erp.model.CheckTree;
import com.uas.erp.model.Employee;
import com.uas.erp.model.HROrg;
import com.uas.erp.model.JSONTree;

public interface HrOrgService {
	void saveHrOrg(String formStore, String  caller);
	
	void updateHrOrgById(String formStore, String  caller,Boolean JobOrgNoRelation);
	
	void deleteHrOrg(int or_id, String  caller,Boolean JobOrgNoRelation);
	
	List<CheckBoxTree> getHrOrgStrTree(int parentid, String caller);
	
	List<CheckBoxTree> getAllHrOrgsTree(String caller,int parentId,Boolean JobOrgNoRelation);
	
	List<CheckBoxTree> getOrgTrees(String caller,String condition,Boolean JobOrgNoRelation);
	
	List<CheckTree> getHrOrgTree(String caller);
	
	HROrg getHROrgByCode(String em_code);
	
	List<Employee> getEmployeesByOrId(int or_id);
	
	HROrg getHROrgByEmId(int em_id);
	
	void deleteEmployee(int em_id);
	
	void updateEmployee(int em_id,int hrOrgid,String hrOrgName);

	List<JSONTree> getHrOrgsTreeAndEmployees(String caller,Integer parentId,Boolean JobOrgNoRelation);	

	List<JSONTree> getAgentHrOrgsTreeAndEmployees(String caller,Integer parentId, Boolean JobOrgNoRelation);
	
	String refreshOrgLevel();
	
	String refreshPositionLevel();
	
	String refreshOrgEmployees();

	String refreshOrgJobEmployeeTree(String  caller);

	List<JSONTree> getTreeNode(int parentId, String condition);

	HROrg addOrgByParent(int parentId);
	
	void resAuditHrOrg(int id, String  caller);

	void auditHrOrg(int id, String caller);
	
	Map<String, String> getEmployees(int id);
	
	void bannedHrOrg(int id, String  caller);
	
	void resBannedHrOrg(int id, String  caller);
	
	Object getChildTreeNode(String condition);

	void updateHrOrg(String formStore);

	void deleteHrOrgById(int id);

	HROrg addOrg(int parentId);
	
	Map<String, Object> getHrOrg();
	
	Map<String, Object> saveHrOrgAndEmp(String param,String type) throws UnsupportedEncodingException;
	
	void saveHrOrgSaas(String formStore);
	
	void deleteHrOrgSaas(int id, String  caller);  //新增删除的方法
	
	void updateHrOrgByIdSaas(String formStore);
	
	List<JSONTree> getHrOrgTreeSaas();
	public JSONTree recursionTree(List<HROrg> hrOrgs, HROrg hrOrg);

	String refreshJobEmployees();

	List<CheckTree> getHrOrgMap(String language);
	
	int getHrOrgMapLevel();
	
	void updateHrOrgMapLevel(int level);
}
