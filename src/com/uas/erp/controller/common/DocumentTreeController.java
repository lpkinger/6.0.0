package com.uas.erp.controller.common;

//import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Employee;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.common.DocumentCatalogService;

//import com.uas.erp.service.common.TreeService;

/**
 * TreeAction
 * 
 * @author yingp
 * @date 2012-07-16 08:35:00
 */
@Controller
public class DocumentTreeController {

	@Autowired
	private DocumentCatalogService documentcatalogService;

	// @Autowired
	// private TreeService treeService;
	/**
	 * 拿到树所有节点信息 3000条以上数据时比较慢，改用getTreeByParentId方法
	 */
	@RequestMapping(value = "/common/documentTree.action", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getTree(HttpServletResponse resp) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<JSONTree> tree = documentcatalogService.getJSONTree();
		modelMap.put("tree", tree);
		return modelMap;
		// String tree = treeService.getTreeString();
		// PrintWriter out = resp.getWriter();
		// out.println(tree);
		// out.flush();
		// out.close();
	}

	/**
	 * 根据父节点加载子节点
	 */
	@RequestMapping(value = "/common/lazyDocumentTree.action")
	@ResponseBody
	public Map<String, Object> getTreeByParentId(HttpSession session, int parentId) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		List<JSONTree> tree = documentcatalogService.getJSONTreeByParentId(parentId,
				(String) session.getAttribute("language"), employee);
		modelMap.put("tree", tree);
		return modelMap;
	}

	/**
	 * 按搜索条件加载树
	 */
	@RequestMapping(value = "/common/searchDocumentTree.action")
	@ResponseBody
	public Map<String, Object> getTreeBySearch(String search) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<JSONTree> tree = documentcatalogService.getJSONTreeBySearch(search);
		modelMap.put("tree", tree);
		return modelMap;
	}
}
