package com.uas.erp.service.common;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.uas.erp.model.Employee;
import com.uas.erp.model.SubsFormula;
import com.uas.erp.model.SubsFormulaDet;
import com.uas.erp.model.SubsNum;

public interface ChartsService {

	public List<SubsFormulaDet> getSubsFormulaDet(Integer formulaId);

	public void save(String formStore, String gridStore, String caller,String subsConditions, String subsRelationConfig);

	public void delete(String caller, int id);

	public void update(String formStore, String gridStore, String caller,String param1, String param2);

	public List<Map<String, Object>> getSubsData(Integer numId, Integer mainId, Integer insId, int emId);

	public byte[] SaveImage(MultipartFile file, Integer id, String table);

	public void removeSubsMans(String numIds, String emcode);

	public List<Map<String, Object>> getSubsNums(String condition);

	public List<Map<String, Object>> getSubsDataDetail(Integer formulaId, Integer insId);

	public byte[] getImage(Integer id, String table) throws IOException;

	public void submit(int id, String caller);

	public void resSubmit(int id, String caller);

	public void audit(int id, String caller);

	public void resAudit(int id, String caller);

	public void bannedCharts(int id, String caller);

	public void resBannedCharts(int id, String caller);

	public void testSubsFormula(Employee employee, int id, String caller);
	
	public String batchTestSubsFormula(Employee employee,String ids);

	public List<Map<String, Object>> getPreviewDatas(int id, String caller, String params);

	public void vastCancelSubsApply(String caller, String datas);

	public String vastAddSubsApply(String caller, String ids, Employee employee);

	public void auditSubsApply(int id, String caller);

	public void resAuditSubsApply(int id, String caller);

	public List<Map<String, Object>> getPersonalSubs(String em_code);

	public List<Map<String, Object>> getApplySubs(String em_code);

	public List<Map<String, Object>> getPersonalApplySubs(String em_code);

	public List<Object[]> getPreviewMain(Integer id);

	public Object[] getPreviewsMain(Integer id);

	public String getPreviewTitle(Integer id);

	public Map<String, Object> getSubsData(int id, String caller);

	public Object getMainImg(Integer id);

	/**
	 * 查找订阅号（包括订阅项）
	 * 
	 * @param id
	 * @return
	 */
	SubsNum getSubsNum(int id);

	/**
	 * 保存订阅号（包括订阅项）
	 * 
	 * @param subs
	 */
	void saveSubsNum(SubsNum subs);

	/**
	 * 查找订阅项
	 * 
	 * @param id
	 * @return
	 */
	SubsFormula getSubsFormula(int id);

	/**
	 * 保存订阅项
	 * 
	 * @param formula
	 */
	void saveSubsFormula(SubsFormula formula);

}
