package com.uas.erp.service.ma;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.Employee;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.UpdateScheme;
import com.uas.erp.model.UpdateSchemeData;
import com.uas.erp.model.UpdateSchemeDetail;
import com.uas.erp.model.UpdateSchemeLog;

public interface UpdateSchemeService {
	
	List<Map<Object, Object>> getErrData(int id);

	void saveUpdateScheme(String formStore, String gridStore, String caller);

	void updateUpdateScheme(String formStore, String gridStore, String caller);

	void deleteUpdateScheme(int id, String caller);

	List<JSONTree> getTreeNode(String condition);

	List<Map<String, Object>> getColumns(String tablename);

	public List<UpdateSchemeDetail> getIndexFields(Integer id);

	public List<UpdateSchemeDetail> getUpdateDetails(Integer id, String condition);

	List<Map<String, Object>> getUpdateSchemes(String em_code);

	void updateChecked(Integer id, String condition);

	int saveUpdateData(Integer id, List<String> data, Integer ulid);

	Map<String, Object> getUpdateScheme(String id);

	List<Map<String, Object>> getOtherData(String id);

	List<UpdateSchemeData> getUpdateDatas(String condition);

	void updateData(Employee employee, Integer ulid);

	List<UpdateSchemeLog> getUpdateHistory(Integer id);

	void checkData(Integer ulid);

	String getEmpdbfindData(String fields, String condition, int page, int pagesize);

	/**
	 * 导出更新方案
	 * 
	 * @param id
	 * @return
	 */
	UpdateScheme exportUpdateScheme(String id);

	/**
	 * 导入更新方案
	 * 
	 * @param scheme
	 */
	void importUpdateScheme(UpdateScheme scheme);

}
