package com.uas.erp.service.plm;

import java.util.List;
import java.util.Map;

import net.sf.mpxj.ProjectFile;

import com.uas.erp.model.JSONTree;
import com.uas.erp.model.ProductType;

public interface PlmProductTypeService {
	
//	void saveProjectPhase(String productTypecode,String formStore);
	
	void updateProjectPhase(String formStore);
	
	Map<String, Object>getProjectPhaseData(String condition,Integer start,Integer page,Integer limit);
	
	void saveProjectPhase(String productTypeCode,String gridStore);
	
	void deleteProjectPhase(String id);

	Map<String,Object> saveAndUpdateTree(String create,String update);
	
	void deleteNode(String id,String type, String productTypeCode);
	
	Map<String,Object> getFileList(String productTypeCode,Integer id,Integer kind,Integer page,Integer start,Integer limit);

	List<JSONTree> getRootProductType(int parentid);

	ProductType saveProductKind(String formStore, int parentId);

	void deleteProductKind(int id, String caller);

	void updateProductKind(String formStore, int parentId);
	
	List<Map<String, Object>> getProjectFileTree(String condition);
	
	List<Map<String, Object>> getTaskBookTree(String condition);
	
	boolean ImportMpp(String productTypeCode,ProjectFile pf);
	
	void updateTaskBookTeamplates(String productTypeCode,String gridStore);
	
	void deleteTaskBookTeamplate(String productTypeCode);

}
