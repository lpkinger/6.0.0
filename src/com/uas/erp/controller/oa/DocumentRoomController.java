package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.oa.DocumentRoomService;

@Controller
public class DocumentRoomController {
	@Autowired
	private DocumentRoomService documentRoomService;

	@RequestMapping("/oa/officialDocument/fileManagement/saveDocumentRoom.action")
	@ResponseBody
	public Map<String, Object> saveDocumentRoom(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		documentRoomService.saveDocumentRoom(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/officialDocument/fileManagement/deleteDocumentRoom.action")
	@ResponseBody
	public Map<String, Object> deleteDocumentRoom(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		documentRoomService.deleteDocumentRoom(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/officialDocument/fileManagement/updateDocumentRoom.action")
	@ResponseBody
	public Map<String, Object> updateDocumentRoom(String caller,
			String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		documentRoomService.updateDocumentRoom(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/officialDocument/fileManagement/getDocumentRoomTree.action")
	@ResponseBody
	public Map<String, Object> getDocumentRoom(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<JSONTree> tree = documentRoomService.getJSONTree(caller);
		modelMap.put("tree", tree);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/officialDocument/fileManagement/addDept.action")
	@ResponseBody
	public Map<String, Object> addDept(String caller, String[] dept,
			int[] deptid, int drid) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		for (int i = 0; i < deptid.length; i++) {
			documentRoomService.addDept(drid, dept[i], deptid[i]);
		}
		modelMap.put("success", true);
		return modelMap;
	}

}
