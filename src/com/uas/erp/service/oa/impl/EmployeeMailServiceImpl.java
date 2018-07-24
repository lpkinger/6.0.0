package com.uas.erp.service.oa.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.dao.common.EmployeeMailDao;
import com.uas.erp.dao.common.HrOrgStrDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.EmployeeMail;
import com.uas.erp.model.HROrg;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.oa.EmployeeMailService;

@Service
public class EmployeeMailServiceImpl implements EmployeeMailService {
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private EmployeeMailDao employeeMailDao;
	@Autowired
	private HrOrgStrDao hrOrgStrDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	@CacheEvict(value = "AddrBook", allEntries = true)
	public void saveAddrBook(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "EmployeeMail", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save("EmployeeMail", "emm_id", store.get("emm_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	@CacheEvict(value = "AddrBook", allEntries = true)
	public void deleteAddrBook(int emm_id) {
		// 执行删除前的其它逻辑
		handlerService.handler("EmployeeMail", "delete", "before", new Object[] { emm_id });
		// 删除
		baseDao.deleteById("EmployeeMail", "emm_id", emm_id);
		// 记录操作
		baseDao.logger.delete("EmployeeMail", "emm_id", emm_id);
		// 执行删除后的其它逻辑
		handlerService.handler("EmployeeMail", "delete", "after", new Object[] { emm_id });
	}

	@Override
	@CacheEvict(value = "AddrBook", allEntries = true)
	public void updateAddrBookById(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.handler("EmployeeMail", "save", "before", new Object[] { formStore });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "EmployeeMail", "emm_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update("EmployeeMail", "emm_id", store.get("emm_id"));
		// 执行修改后的其它逻辑
		handlerService.handler("EmployeeMail", "save", "after", new Object[] { formStore });
	}

	@Override
	@Cacheable(value = "AddrBook", key = "#master + '@' + #parentid + 'getAddrBook'")
	public List<JSONTree> getJsonTrees(String master, int parentid) {
		List<JSONTree> tree = new ArrayList<JSONTree>();
		/*List<EmployeeMail> list = employeeMailDao.getEmployeeMailByParentId(parentid);*/
		baseDao.execute("update hrorg set or_isleaf=0");
		baseDao.execute("update hrorg set or_isleaf=1 where or_id not in(select or_subof from hrorg)");
		if (parentid == -1) {
			JSONTree jt = new JSONTree();
			jt.setId("0");
			jt.setText("员工通讯录");
			jt.setParentId(-1); 
			jt.setLeaf(false);
			jt.setCls("x-tree-cls-root");
			/*jt.setChildren(parseTree(0));*/
			tree.add(jt);
		}else if(parentid==0){
			List<HROrg> orgList = hrOrgStrDao.getHrOrgbyParentId(parentid);
			for (HROrg org : orgList) {
				Object pid = parentid == 0 ? -1 : "org-" + parentid;
				JSONTree node = new JSONTree(org, pid, "org-");
				tree.add(node);
			}		
		}
		else{
			Object isleaf=baseDao.getFieldDataByCondition("hrOrg", "or_isleaf", "or_id="+parentid);
			String leaf=String.valueOf(isleaf);
			if(leaf.equals("1")){
				Object pid = "org-"+ parentid;
				List<Employee> employees = employeeDao.getEmployeesByOrgId(parentid);
				for (Employee e : employees) { 
				tree.add(new JSONTree(e, pid));
				}
			}else{
				List<HROrg> orgList = hrOrgStrDao.getHrOrgbyParentId(parentid);
				for (HROrg org : orgList) {
					Object pid = parentid == 0 ? -1 : "org-" + parentid;
					JSONTree node = new JSONTree(org, pid, "org-");
					tree.add(node);
				}	
			}
		}
		return tree;
	}

	private List<JSONTree> parseTree(int or_id) {
		List<HROrg> orgList = hrOrgStrDao.getHrOrgbyParentId(or_id);
		List<JSONTree> orgTree = new ArrayList<JSONTree>();
		JSONTree node;
		List<JSONTree> child;
		List<Employee> employees = employeeDao.getEmployees(SpObserver.getSp());
		Object pid = or_id == 0 ? -1 : "org-" + or_id;
		for (HROrg org : orgList) {
			node = new JSONTree(org, pid, "org-");
			child = new ArrayList<JSONTree>();
			if (Math.abs(org.getOr_isleaf()) == 1) {
				for (Employee e : employees) { 
					if (e.getEm_defaultorid() == org.getOr_id()) {
						child.add(new JSONTree(e, node.getId()));
					}
				}
			} else {
				child = parseTree(org.getOr_id());
			}
			node.setChildren(child);
			orgTree.add(node);
		}
		return orgTree;
	}

	@Override
	@Cacheable(value = "mailAddrs", key = "#employee.em_master + '@' + #employee.em_id + 'getJSONMail'")
	public List<JSONTree> getJSONMail() {
		Employee employee = SystemSession.getUser();
		List<JSONTree> tree = new ArrayList<JSONTree>();
		List<EmployeeMail> mails = employeeDao.getEmployeeMails(employee.getEm_id());
		List<Employee> employees = employeeDao.getEmployees(employee.getEm_master());
		JSONTree jt = new JSONTree();
		jt.setId(-1);
		jt.setText("企业联系人");
		jt.setParentId(0);
		jt.setLeaf(false);
		jt.setCls("x-tree-cls-root");
		List<JSONTree> enTree = new ArrayList<JSONTree>();
		List<HROrg> orgList = hrOrgStrDao.getAllHrOrgs(null);
		JSONTree ojt;
		List<JSONTree> orgTree;
		for (HROrg org : orgList) {
			ojt = new JSONTree(org, -1);
			orgTree = new ArrayList<JSONTree>();
			for (Employee e : employees) {
				if (e.getEm_defaultorid() == org.getOr_id()) {
					orgTree.add(new JSONTree(ojt.getId()));
				}
			}
			ojt.setChildren(orgTree);
			enTree.add(ojt);
		}
		jt.setChildren(enTree);
		tree.add(jt);
		for (EmployeeMail s : mails) {
			JSONTree ct = new JSONTree();
			ct = recursionFn(mails, s);
			ct.setLeaf(false);
			ct.setCls("x-tree-cls-root");
			tree.add(ct);
		}
		return tree;
	}

	private JSONTree recursionFn(List<EmployeeMail> list, EmployeeMail s) {
		JSONTree jt = new JSONTree();
		jt.setId(s.getEmm_id());
		jt.setParentId(s.getEmm_parentid());
		jt.setText(s.getEmm_friendname());
		jt.setQtip(s.getEmm_friendmail());
		if (hasChild(list, s)) {
			if (s.getEmm_parentid() == 0) {
				jt.setCls("x-tree-cls-root");
			} else {
				jt.setCls("x-tree-cls-parent");
			}
			jt.setQtitle("");
			jt.setLeaf(false);
			List<EmployeeMail> childList = getChildList(list, s);
			Iterator<EmployeeMail> it = childList.iterator();
			List<JSONTree> children = new ArrayList<JSONTree>();
			JSONTree ct = new JSONTree();
			while (it.hasNext()) {
				EmployeeMail n = (EmployeeMail) it.next();
				ct = recursionFn(list, n);
				children.add(ct);
			}
			jt.setChildren(children);
		} else {
			jt.setCls("x-tree-cls-node");
			jt.setAllowDrag(true);
			jt.setLeaf(true);
			jt.setChildren(new ArrayList<JSONTree>());
		}
		return jt;
	}

	private boolean hasChild(List<EmployeeMail> list, EmployeeMail s) {
		return getChildList(list, s).size() > 0 ? true : false;
	}

	private List<EmployeeMail> getChildList(List<EmployeeMail> list, EmployeeMail s) {
		List<EmployeeMail> li = new ArrayList<EmployeeMail>();
		Iterator<EmployeeMail> it = list.iterator();
		while (it.hasNext()) {
			EmployeeMail n = (EmployeeMail) it.next();
			if ((n.getEmm_parentid()) == (s.getEmm_id())) {
				li.add(n);
			}
		}
		return li;
	}

	/**
	 * @param id
	 *            -em_id
	 */
	@Override
	public EmployeeMail getEmployeeMailByEmployee(int id) {
		Employee employee = employeeDao.getEmployeeByEmId(-id);
		if (employee != null) {
			return new EmployeeMail(employee);
		}
		return null;
	}
}
