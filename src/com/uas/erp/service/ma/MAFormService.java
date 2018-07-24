package com.uas.erp.service.ma;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.DataListCombo;
import com.uas.erp.model.FormWrap;

public interface MAFormService {
	void save(String form, String formdetail);

	void update(String form, String add, String update, String del);

	void updateDetailGrid(String add, String update, String del);

	void delete(int id);

	void mdelete(int id);

	boolean checkCaller(String caller);

	int getIdByCaller(String caller);

	boolean checkProcessDeployCaller(String caller);

	boolean checkProcessSetCaller(String caller);

	List<DataListCombo> getComboDataByField(String caller, String field);

	void saveCombo(String gridStore);

	void deleteCombo(String id);

	void saveMultiForm(String formStore, String param);

	void saveDetailGrid(String detailparam);

	void saveFormBook(Integer foid, String text);

	boolean checkFields(String table, String field);

	String setListCaller(String caller, String dl_caller, String lockpage);

	String setRelativeCaller(String caller, String re_caller, String lockpage);

	void updateDetail(String table, String gridAdded, String gridUpdated, String gridDeleted);

	/**
	 * 导出配置
	 * 
	 * @param ids
	 * @param gridCallers
	 * @return
	 */
	FormWrap exportForms(String[] ids, String[] gridCallers);

	/**
	 * 导入配置
	 * 
	 * @param formWrap
	 */
	void importForms(FormWrap formWrap);
	
	/*
	 * 生成流程设置
	 */
	<T> void InsertIntoProcessSet(Map<T, Object> store);
}
