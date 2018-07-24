package com.uas.erp.service.common.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.DocumentCatalogDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.DocumentCatalog;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.common.DocumentCatalogService;

@Service("documentcatalogService")
public class DocumentCatalogServiceImpl implements DocumentCatalogService{
	@Autowired
	private DocumentCatalogDao documentcatalogDao;
	@Autowired
	private BaseDao baseDao;
	
	@Override
	public List<JSONTree> getJSONTree() {
		List<JSONTree> tree = new ArrayList<JSONTree>();
		List<DocumentCatalog> list = documentcatalogDao.getDocumentCatalogs();
		for(DocumentCatalog s:list){
			JSONTree ct = new JSONTree();
			if(s.getDc_ParentId() == 0){
				ct = recursionFn(list, s);
				tree.add(ct);
			}
		}
		return tree;
	}
	
	private JSONTree recursionFn(List<DocumentCatalog> list, DocumentCatalog s) {
		JSONTree jt = new JSONTree();
		jt.setId(s.getDc_Id());
		jt.setParentId(s.getDc_ParentId());
		jt.setText(s.getDc_DisplayName());
		jt.setQtip(s.getDc_DisplayName());
		if (hasChild(list, s)) {
			if(s.getDc_ParentId() == 0){
				jt.setCls("x-tree-cls-root");
			} else {
				jt.setCls("x-tree-cls-parent");
			}
			jt.setQtitle("");
			jt.setLeaf(false);
			List<DocumentCatalog> childList = getChildList(list, s);
			Iterator<DocumentCatalog> it = childList.iterator();
			List<JSONTree> children = new ArrayList<JSONTree>();
			JSONTree ct = new JSONTree();
			while (it.hasNext()) {
				DocumentCatalog n = (DocumentCatalog) it.next();
				ct = recursionFn(list, n);
				children.add(ct);
			}
			jt.setChildren(children);
		} else {
			jt.setCls("x-tree-cls-node");
			jt.setQtitle(s.getDc_TabTitle());
			jt.setUrl(s.getDc_Url());
			jt.setAllowDrag(true);
			jt.setLeaf(true);
//			jt.setFilesize(s.getDc_filesize());
//			jt.setUpdatetime(s.getDc_updatetime());
//			jt.setVersion(s.getDc_version());
			jt.setChildren(new ArrayList<JSONTree>());
		}
		return jt;
	}
	//判断是否有子节点
	private boolean hasChild(List<DocumentCatalog> list, DocumentCatalog s) {
		return getChildList(list, s).size() > 0 ? true : false;
	}

	//获取子节点列表
	private List<DocumentCatalog> getChildList(List<DocumentCatalog> list, DocumentCatalog s) {
		List<DocumentCatalog> li = new ArrayList<DocumentCatalog>();
		Iterator<DocumentCatalog> it = list.iterator();
		while (it.hasNext()) {
			DocumentCatalog n = (DocumentCatalog) it.next();
			//父id等于id时 有子节点 添加该条数据
			if((n.getDc_ParentId()) == (s.getDc_Id())){
				li.add(n);
			}
		}
		return li;
	}
	/**
	 * 改进的方法
	 * @param parentId 父节点id 通过此参数，每次点击父节点时，若其子节点未加载，就将parentId传回来，返回其子节点
	 */
	@Override
	@Cacheable(value="document",key="#employee.em_master + '@' + #parentId + 'getJSONTreeByParentId'")
	public List<JSONTree> getJSONTreeByParentId(int parentId, String language, Employee employee) {
		List<JSONTree> tree = new ArrayList<JSONTree>();
		List<DocumentCatalog> list = documentcatalogDao.getDocumentCatalogsByParentId(parentId);
		for(DocumentCatalog dc:list){
			tree.add(new JSONTree(dc));
		}
		return tree;
	}
	/**
	 * treepanel的searchfield从后台拿搜索结果
	 * @param search 条件
	 */
	@Override
	public List<JSONTree> getJSONTreeBySearch(String search) {
		List<JSONTree> tree = new ArrayList<JSONTree>();
		List<DocumentCatalog> list = documentcatalogDao.getDocumentCatalogsBySearch(search);
		for(DocumentCatalog s:list){
			JSONTree ct = new JSONTree();
			if(s.getDc_ParentId() == 0){
				ct = recursionFn(list, s);
				tree.add(ct);
			}
		}
		return tree;
	}
	
	@Override
	public String getDocumentPath(String node_id) {
		int id = Integer.parseInt(node_id);
		return documentcatalogDao.getPathById(id);
	}
	
	@Override
	@CacheEvict(value="document",allEntries=true)
	public void insertDocumentCatalog(DocumentCatalog dc, Employee employee,
			String language) {
		documentcatalogDao.insertDocumentCatalog(dc, employee);
		try {
			// 记录操作
			baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
					.getLocalMessage("msg.save", language), BaseUtil
					.getLocalMessage("msg.saveSuccess", language),
					"DocumentCatalog|dc_id=" + dc.getDc_Id()));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public DocumentCatalog getDocumentCatalogById(int id) {
		return documentcatalogDao.getDocumentCatalogById(id);
	}
	
	@Override
	public List<DocumentCatalog> getFileListById(int id) {
		List<DocumentCatalog> list = new ArrayList<DocumentCatalog>();
		List<DocumentCatalog> dcs = documentcatalogDao.getDocumentCatalogsByParentId(id);
		if (dcs != null) {
			for (DocumentCatalog dc : dcs) {
				if (dc.getDc_isfile().equals("T")) {
					list.add(dc);
				} else if(dc.getDc_isfile().equals("F")){
					addList(list, getFileListById(dc.getDc_Id()));
				}
			}			
		}
		return list;
		
	}	
	public List<DocumentCatalog> addList(List<DocumentCatalog> ol, List<DocumentCatalog> tl){
		for (DocumentCatalog dc : tl) {
			ol.add(dc);
		}
		return ol;
	}
	
	@Override
	public void deleteByVersion(int dcl_number, int dc_ParentId) {
		documentcatalogDao.deleteByVersion(dcl_number, dc_ParentId);		
	}
}
