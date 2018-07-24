package com.uas.erp.service.ma;

import java.util.List;

import net.sf.json.JSONObject;

import com.uas.erp.model.DataDictionary;
import com.uas.erp.model.DataDictionaryDetail;
import com.uas.erp.model.DataRelation;
import com.uas.erp.model.Page;

public interface MADataDictionaryService {

	boolean checkTable(String table);

	List<DataDictionaryDetail> getDataDictionary(String table);

	List<DataDictionary> getDataDictionaries(String tables);

	Page<DataDictionary> getPageDataDictionary(String query, int page, int start, int limit);

	void alter(String col_update, String col_create, String col_remove, String ind_update, String ind_create, String ind_remove, String formStore,String gridStore);

	List<DataRelation> getDataRelations(String tablename);

	List<JSONObject> getRelation_Col_Comments(String tablename);
	List<JSONObject> getRelation_Tab_Comments(String tablename);
	void refresh(String tablename);
}
