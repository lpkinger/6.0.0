package com.uas.erp.service.plm.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.dao.common.DocumentDao;
import com.uas.erp.model.Document;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.plm.DOCManageService;
@Service
public class DOCManageServiceImpl implements DOCManageService {
@Autowired
 private DocumentDao dcumentDao;	
	@Override
	public List<JSONTree> getAllDirectorys() {
		return get();
	}
	public List<JSONTree> get(){
		//所有文件夹
		String condition="do_kind=1";
		List<Document> docs =dcumentDao.getDocumentByCondition(condition) ;
		List<JSONTree> treeList = new ArrayList<JSONTree>();
		for (Document doc : docs) {
			JSONTree ct = new JSONTree(doc);
			List<JSONTree> children = new ArrayList<JSONTree>();
		    ct.setCls("org");
			treeList.add(ct);
			ct.setChildren(children);
		}
		List<JSONTree> root = new ArrayList<JSONTree>();
		for (JSONTree ct : treeList) {
			if (Integer.parseInt(ct.getParentId().toString())==0) {
				root.add(ct);
			}
		}
		getTree(treeList, root);
		return root;
	}
	
	public void getTree(List<JSONTree> list, List<JSONTree> root){
		for (JSONTree ct : root) {
			List<JSONTree> cts = findById(list, ct.getId());
			if (cts.size() != 0) {
				ct.getChildren().addAll(findById(list, ct.getId()));				
				getTree(list, ct.getChildren());
			}
		}
	}
	public List<JSONTree> findById(List<JSONTree> list ,Object id){
		List<JSONTree> cts = new ArrayList<JSONTree>();
		for (JSONTree ct : list) {
			if (ct.getParentId().toString().equals(id.toString())) {
				cts.add(ct);
			}
		}
		return cts;
	}

}
