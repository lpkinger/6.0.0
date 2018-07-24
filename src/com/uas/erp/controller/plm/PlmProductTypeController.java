package com.uas.erp.controller.plm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.mpp.MPPReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.FileUtil;
import com.uas.erp.core.exception.SystemException;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.Employee;
import com.uas.erp.model.FileUpload;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.plm.PlmProductTypeService;

@Controller
public class PlmProductTypeController {
	@Autowired
	private PlmProductTypeService plmProducetTypeService;

	/**
	 *@author wsy 
	 */
	@RequestMapping("/plm/base/getRootProductType.action")
	@ResponseBody
	public Map<String, Object> getRootProductType(HttpSession session,int parentid) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<JSONTree> tree = plmProducetTypeService.getRootProductType(parentid);
		modelMap.put("tree", tree);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 *@author wsy 
	 */
	@RequestMapping("/plm/base/saveProductKind.action")
	@ResponseBody
	public Map<String, Object> saveProductKind(HttpSession session, String formStore, int parentId) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("productType", plmProducetTypeService.saveProductKind(formStore, parentId));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * @author wsy
	 */

	@RequestMapping("/plm/base/deleteProductKind.action")
	@ResponseBody
	public Map<String, Object> deleteProductKind(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		plmProducetTypeService.deleteProductKind(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * @author wsy
	 */
	@RequestMapping("/plm/base/updateProductKind.action")
	@ResponseBody
	public Map<String, Object> updateProductKind(HttpSession session, String formStore, int parentId) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		plmProducetTypeService.updateProductKind(formStore, parentId);
		modelMap.put("success", true);
		return modelMap;
	}

//	@RequestMapping("/plm/base/saveProjectPhase.action")
//	@ResponseBody
//	public Map<String, Object> saveProjectPhase(String productTypecode,String formStore){
//		Map<String, Object> modelMap = new HashMap<String,Object>();
//		plmProducetTypeService.saveProjectPhase(productTypecode,formStore);
//		modelMap.put("success", true);
//		return modelMap;
//	}
	
	@RequestMapping("/plm/base/updateProjectPhase.action")
	@ResponseBody
	public Map<String, Object> updateProjectPhase(String formStore){
		Map<String, Object> modelMap = new HashMap<String,Object>();
		plmProducetTypeService.updateProjectPhase(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/plm/base/getProjectPhaseData.action")
	@ResponseBody
	public Map<String, Object> getProjectPhaseData(String condition,Integer start,Integer page,Integer limit) {

		return plmProducetTypeService.getProjectPhaseData(condition,start,page,limit);
	}
	
	@RequestMapping("/plm/base/saveProjectPhase.action")
	@ResponseBody
	public Map<String, Object> saveProjectPhase(String productTypeCode,String gridStore){
		Map<String, Object> modelMap = new HashMap<String,Object>();
		plmProducetTypeService.saveProjectPhase(productTypeCode,gridStore);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/plm/base/deleteProjectPhase.action")
	@ResponseBody
	public Map<String, Object> deleteProjectPhase(String id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		plmProducetTypeService.deleteProjectPhase(id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/plm/base/getFileList.action")  
	@ResponseBody 
	public Map<String, Object> getFileList(HttpSession session,String productTypeCode,Integer id,Integer kind,Integer page,Integer start,Integer limit) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = plmProducetTypeService.getFileList(productTypeCode,id, kind,page,start,limit);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/plm/base/saveAndUpdateTree.action")  
	@ResponseBody 
	public Map<String, Object> saveAndUpdateTree(HttpSession session,String create,String update) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		modelMap = plmProducetTypeService.saveAndUpdateTree(create, update);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/plm/base/deleteNode.action")  
	@ResponseBody 
	public Map<String, Object> deleteNode(HttpSession session,String id,String type,String productTypeCode) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		plmProducetTypeService.deleteNode(id, type, productTypeCode);		
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/plm/base/filetree.action")
	@ResponseBody
	public Map<String, Object> getProjectFileTree(String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<String, Object>> tree = plmProducetTypeService.getProjectFileTree(condition);
		modelMap.put("tree", tree);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/plm/base/getTaskBookTree.action")
	@ResponseBody
	public Map<String, Object> getTaskBookTree(String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<String, Object>> tree = plmProducetTypeService.getTaskBookTree(condition);
		modelMap.put("tree", tree);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/plm/base/ImportProjectFile.action")
	@ResponseBody
	public String ImportMpp(HttpSession session,String productTypeCode,FileUpload uploadItem) {
		
		InputStream is = null;
		MPPReader reader = null;
		ProjectFile pf = null;
		Employee employee=SystemSession.getUser();
		
		try {
			long size = uploadItem.getFile().getSize();
			if (size > 104857600) {
				return "{error: '文件过大'}";
			}
			String path = FileUtil.saveFile(uploadItem.getFile(), employee.getEm_code());
			is = new FileInputStream(new File(path));
			reader = new MPPReader();
			pf = reader.read(is);
			boolean bool = plmProducetTypeService.ImportMpp(productTypeCode,pf);
			if (bool) {
				File file = new File(path);
				if (file.isFile() && file.exists()) {
					file.delete();
				}
			}
			String r = "{success: true}";
			return r;
		} catch (SystemException e) {
			e.printStackTrace();
				String error=e.getMessage()!=null?"{error: '"+e.getMessage()+"'}":"{error: 'MPP文件的格式不太规范,导入失败<hr>可以尝试将文件另存为,然后导入'}";
				try {
					return new String(error.getBytes("utf-8"), "iso8859-1");
				} catch (UnsupportedEncodingException e1) {
					return "{success: false}";
				}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				return new String("{error: '文件格式不正确，请重新选择文件！'}".getBytes("utf-8"), "iso8859-1");
			} catch (UnsupportedEncodingException e1) {
				return "{success: false}";
			}
		} finally {
			try {
				is.close();
			} catch (IOException e) {

			}
		}
	}
	
	@RequestMapping("/plm/base/updateTaskBookTeamplates.action")
	@ResponseBody
	public Map<String,Object> updateTaskBookTeamplates(String productTypeCode,String gridStore) {
		Map<String,Object> modelMap=new HashMap<String, Object>();
		plmProducetTypeService.updateTaskBookTeamplates(productTypeCode,gridStore);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/plm/base/deleteTaskBookTeamplate.action")
	@ResponseBody
	public Map<String,Object> deleteTaskBookTeamplate(String productTypeCode) {
		Map<String,Object> modelMap=new HashMap<String, Object>();
		plmProducetTypeService.deleteTaskBookTeamplate(productTypeCode);
		modelMap.put("success", true);
		return modelMap;
	}
}
