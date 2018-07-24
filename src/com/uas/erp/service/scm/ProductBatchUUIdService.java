package com.uas.erp.service.scm;

import java.util.List;
import java.util.Map;

import com.uas.api.b2c_erp.baisc.model.ComponentInfoUas;
import com.uas.api.domain.IPage;

public interface ProductBatchUUIdService {

	List<Map<Object, Object>> getProductKindTree(String type, Long parentid);

	IPage<ComponentInfoUas> getProductComponent(Long kindId, int page, int pageSize, String orispeccode);

	List<ComponentInfoUas> getUUIdByCode(String code);

	List<ComponentInfoUas> getByUUIds(String ids);

	Map<String, Object> getPageAccess();

	void loadAllProd(String caller, String condition, String code);

	void loadProd(String caller, String data, String code);

	void removeUUId(String caller, String code, String data);

	void batchGetByOriCode(List<String> oriCodes,String code);

	public List<Map<Object, Object>> searchByOrispecode(String caller, String code);
	
	public List<Map<Object, Object>> searchByKindcode(String caller, String code);

	void confirmUUId(String param, String caller);
	
	/**
	 * 匹配时确认UAS中的go_erpunit单位
	 * @return
	 */
	public String getUASUnit(String unit,String erpunit);
	
	/**
	 * 计算单位换算比例
	 * @param erpunit
	 * @param unit
	 * @return
	 */
    public double getUnitRate(String erpunit,String unit);
}
