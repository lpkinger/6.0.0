package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.TaskJSONTree;
import com.uas.erp.service.plm.TaskTreeService;
@Controller
public class TaskTreeController {
	@Autowired
	private TaskTreeService taskTreeService;
	@RequestMapping(value="/common/TaskTree.action")
	@ResponseBody
	public Map<String, Object> getTreeByParentId(HttpSession session, int parentId, String condition) throws Exception{
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<TaskJSONTree> tree = taskTreeService.getJSONTreeByParentId(parentId, condition);
		modelMap.put("tree", tree);
		return modelMap;
	}
	
}
