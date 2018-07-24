package com.uas.pda.dao;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.Page;

public interface PdaCommonDao {
	public Page<Map<String, Object>> getInOutData(String conditon,String inoutNo,String whcode, int pi_id);
	public List<Map<String, Object>>  changeKeyToLowerCase (List<Map<String, Object>> list);
	public Map<String, Object>  changeKeyToLowerCase (Map<String, Object> map);
	/**
	 * 根据湿敏元件等级获取寿命
	 * @param level
	 * @return
	 */
	public double getMsdTime(String level);
	
	/**
	 * 获取湿敏元件剩余寿命，根据配置入柜参数,等级
	 * @param code
	 * @return
	 */
	public double getMsdRestTime(String code);
	
	public List<Map<String, Object>> getProdInOut(String condition,String inoutNo, String whcode);
	
	public List<Map<String, Object>> getCheckProdInOut(String condition, String inoutNo, String whcode);

}
