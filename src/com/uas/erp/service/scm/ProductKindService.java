package com.uas.erp.service.scm;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.uas.erp.model.Employee;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.ProductKind;

public interface ProductKindService {
	void saveProductKind(String formStore, String caller);

	void updateProductKindById(String formStore, String caller);

	void deleteProductKind(int pk_id, String caller);
	
	void submitProductKind(int pk_id,String caller);
	
	void resSubmitProductKind(int pk_id,String caller);
	
	void auditProductKind(int pk_id,String caller);
	
	void resAuditProductKind(int pk_id,String caller);

	List<JSONTree> getJsonTrees(Employee employee, int parentid,String allKind, String caller);

	String getProductKindNum(int id,String postfix);
	
	/**
	 * 按物料编码类型去编号
	 * @param k1
	 * @param k2
	 * @param k3
	 * @param k4
	 * @return
	 */
	String getProductKindNumByKind(String k1, String k2, String k3, String k4,String postfix);
	
	/**
	 * 失效、转有效
	 * @param id pk_id
	 * @param bool 是否有效
	 */
	void setEffective(int id, Boolean bool);
	
	void updateProdLoss(int pk_id, String caller);
	ProductKind addProductKindByParent(int parentId);

	List<JSONTree> getJSONTreeBySearch(String search, Employee employee);
	
	List<Map<String,Object>> getPrKind(String tablename, String fields, String condition);
}
