package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.CategoryStrDao;
import com.uas.erp.model.Category;
import com.uas.erp.model.CheckBoxTree;
import com.uas.erp.service.fa.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {
	@Autowired
	private CategoryStrDao categoryStrDao;
	@Autowired
	private BaseDao baseDao;

	@Override
	public List<CheckBoxTree> getAllCategoryTree(String caller, String condition) {
		return get(caller, condition);
	}

	public List<CheckBoxTree> get(String caller, String condition) {
		List<CheckBoxTree> treeList = new ArrayList<CheckBoxTree>(); // 需要返回的已经组装好的树
		List<Category> cateList = categoryStrDao.getAllCategorys(condition); // 得到所有的科目
		for (Category c : cateList) {
			CheckBoxTree cb = new CheckBoxTree();
			if (c.getCa_subof() == 0) {
				cb = getCheckBoxTree(cateList, c, caller);
				treeList.add(cb);
			}
		}
		return treeList;

	}

	private CheckBoxTree getCheckBoxTree(List<Category> list, Category c,
			String caller) {
		CheckBoxTree cb = new CheckBoxTree();
		cb.setCls("");
		cb.setIconCls("");
		cb.setId(c.getCa_id());
		cb.setCaname(c.getCa_name());
		cb.setText(c.getCa_code() + "-" + c.getCa_description());
		cb.setQtip(c.getCa_code());
		cb.setCurrency(c.getCa_currency());
		cb.setType(c.getCa_type());
		cb.setTypename(c.getCa_typename());
		cb.setCaclass(c.getCa_class());
		cb.setCaasstype(c.getCa_asstype());
		cb.setCaassname(c.getCa_assname());
		cb.setCalevel(String.valueOf(c.getCa_level()));
		cb.setData(c);
		// 有子节点
		if (hasChild(list, c)) {
			cb.setLeaf(false);
			cb.setQtitle("");
			if (caller.trim().equals("Category!Base")) {
				cb.setChecked(false);
			}
			List<Category> childList = getChildList(list, c);
			Iterator<Category> it = childList.iterator();
			List<CheckBoxTree> children = new ArrayList<CheckBoxTree>();
			CheckBoxTree cbt = new CheckBoxTree();
			while (it.hasNext()) {
				Category n = it.next();
				cbt = getCheckBoxTree(list, n, caller);
				children.add(cbt);
			}
			cb.setChildren(children);

			// 没有子节点
		} else {
			cb.setLeaf(true);
			cb.setQtitle(c.getCa_description());
			cb.setChildren(new ArrayList<CheckBoxTree>());
			cb.setChecked(false);
		}
		return cb;
	}

	// 判断是否有子节点
	private boolean hasChild(List<Category> list, Category c) {
		return getChildList(list, c).size() > 0 ? true : false;
	}

	private List<Category> getChildList(List<Category> list, Category c) {

		List<Category> li = new ArrayList<Category>();
		Iterator<Category> it = list.iterator();
		while (it.hasNext()) {
			Category ca = it.next();
			if (ca.getCa_subof() == c.getCa_id()) {
				li.add(ca);
			}
		}
		return li;

	}

	/**
	 * 在form中使用科目树 得到所需要的form中的赋值字段 从dbfindsetui中得到
	 */
	@Override
	public Map<String, Object> getToUi(String key, String caller) {
		Map<String, Object> findToUiMap = new HashMap<String, Object>();
		String findToUiSql = "select ds_findtoui from dbfindsetui where ds_whichui = '"
				+ key + "' and ds_caller = '" + caller + "'";
		SqlRowList rs = baseDao.queryForRowSet(findToUiSql);
		String findToUi = "";
		if (rs.next()) {
			findToUi = rs.getString(1);
		}
		if (null != findToUi && !"".equals(findToUi)) {
			String[] str = findToUi.split("#");
			for (String st : str) {
				String[] s = st.split(",");
				findToUiMap.put(s[0], s[1]);
			}

		}

		return findToUiMap;
	}

	@Override
	public List<String> getCateClass() {
		SqlRowList rs = baseDao
				.queryForRowSet("select distinct ca_class from category where nvl(ca_class, ' ')<>' '");
		List<String> list = new ArrayList<String>();
		while (rs.next()) {
			list.add(rs.getString(1));
		}
		return list;
	}

	// public List<CheckBoxTree> get(String language){
	// List<HROrg> orgList = categoryStrDao.getAllCategorys();
	// List<CheckBoxTree> treeList = new ArrayList<CheckBoxTree>();
	// for (HROrg hrOrg : orgList) {
	// CheckBoxTree ct = new CheckBoxTree(hrOrg, language);
	// treeList.add(ct);
	// List<Employee> emList = employeeDao.getEmployeesByOrId(hrOrg.getOr_id());
	// List<CheckBoxTree> children = new ArrayList<CheckBoxTree>();
	// for (Employee employee : emList) {
	// if (!employee.getEm_code().equals(ct.getQtip())) {
	// children.add(new CheckBoxTree(employee, language));
	// }
	// }
	// ct.setChildren(children);
	// }
	// List<CheckBoxTree> root = new ArrayList<CheckBoxTree>();
	// for (CheckBoxTree ct : treeList) {
	// if (ct.getParentId() == 0) {
	// root.add(ct);
	// }
	// }
	// getTree(treeList, root);
	// return root;
	// }
	//
	// public void getTree(List<CheckBoxTree> list, List<CheckBoxTree> root){
	// for (CheckBoxTree ct : root) {
	// List<CheckBoxTree> cts = findById(list, ct.getId());
	// if (cts.size() != 0) {
	// ct.getChildren().addAll(findById(list, ct.getId()));
	// getTree(list, ct.getChildren());
	// }
	// }
	// }
	// public List<CheckBoxTree> findById(List<CheckBoxTree> list , int id){
	// List<CheckBoxTree> cts = new ArrayList<CheckBoxTree>();
	// for (CheckBoxTree ct : list) {
	// if (ct.getParentId() == id) {
	// cts.add(ct);
	// }
	// }
	// return cts;
	// }
}
