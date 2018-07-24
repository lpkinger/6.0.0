package com.uas.erp.service.common;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.Employee;
import com.uas.erp.model.GridPanel;

public interface SingleGridPanelService {
	/**
	 * @param caller
	 * @param condition
	 * @param language
	 * @param employee
	 * @param start
	 * @param end
	 * @param _m
	 *            =0表示无论master,都只取当前帐套配置和数据
	 * @param isCloud 
	 * @return
	 */
	GridPanel getGridPanelByCaller(String caller, String condition, Integer start,
			Integer end, Integer _m, boolean isCloud,String _copyConf);

	/**
	 * 只读Grid
	 * 
	 * @param caller
	 * @param condition
	 * @param language
	 * @param employee
	 * @param start
	 * @param end
	 * @return
	 */
	Map<String, Object> getReadOnlyGrid(String caller, String condition, String url, String language, Employee employee, Integer start,
			Integer end);

	List<Map<String, Object>> getRecordByCode(String caller, String condition, Employee employee, Integer start,
			Integer end, boolean isCloud);

	void deleteDetail(String caller,String gridCaller, String condition, String autodelete,String gridReadOnly);

	void setDetailDetno(String caller, String dfield, String mfield, Integer id, int detno);

	JSONArray getGridButton(String caller);

	void batchSave(String language, Employee employee, String caller, String data);

	void saveItemGrid(String language, Employee employee, String data);

	void updateItemGrid(String language, Employee employee, String data);
	List<DetailGrid> getDetailsByCaller(String caller);
	void batchSave(String caller,String string,int keyValue);
	void deletegridbycaller(String caller,int keyValue);
	GridPanel getGridDatas(String caller, String condition, Integer page,
			Integer pageSize, Integer _m, boolean isCloud,String _copyConf,String orderby);
}
