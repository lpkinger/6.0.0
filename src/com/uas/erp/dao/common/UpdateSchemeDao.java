package com.uas.erp.dao.common;

import java.util.List;

import com.uas.erp.model.InitToFormal;
import com.uas.erp.model.UpdateScheme;
import com.uas.erp.model.UpdateSchemeData;

public interface UpdateSchemeDao {

	void save(List<UpdateSchemeData> datas);

	String updateData(String keyField, String tableName, List<InitToFormal> datas);

	UpdateScheme getUpdateScheme(String id);

	void saveUpdateScheme(UpdateScheme scheme);

}
