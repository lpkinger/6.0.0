package com.uas.erp.service.hr.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.PasswordEncryUtil;
import com.uas.erp.core.Pinyin;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.dao.common.HrJobDao;
import com.uas.erp.dao.common.HrOrgStrDao;
import com.uas.erp.model.CheckBoxTree;
import com.uas.erp.model.CheckTree;
import com.uas.erp.model.Employee;
import com.uas.erp.model.HRJob;
import com.uas.erp.model.HROrg;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.hr.HrOrgService;

@Service
public class HrOrgServiceImpl implements HrOrgService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private HrOrgStrDao hrOrgStrDao;
	@Autowired
	private HrJobDao  hrJobDao;
	@Override
	public void saveHrOrg(String formStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String remark = null;
		boolean hasRemark = store.containsKey("OR_REMARK");
		if(hasRemark) {
			remark = String.valueOf(store.get("OR_REMARK"));
			store.remove("OR_REMARK");
		}
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("HrOrg", "or_code='" + store.get("or_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 检查父组织ID是否是自己或者子组织
		checkParentHrOrg(String.valueOf(store.get("or_id")),String.valueOf(store.get("or_subof")));
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "HrOrg", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.execute("update hrorg A set or_subof=nvl((select or_id from hrorg B where A.or_pcode=B.or_code),0) where or_id="+ store.get("or_id"));
		baseDao.execute("update hrorg A set or_parentname=(select or_name from hrorg B where A.or_pcode=B.or_code) where or_id="+ store.get("or_id"));
		baseDao.execute("update hrorg A set or_level=(nvl((select or_level from hrorg B where A.or_subof=B.or_id),0) + 1) where or_id="+ store.get("or_id"));
		baseDao.execute("update hrorg set or_path = (select SUBSTR(a.path_,2) from (select or_id id_,or_name, sys_connect_by_path(or_name,'-') path_ from hrorg "
				+ "START WITH or_subof = 0   CONNECT BY PRIOR or_id = or_subof) a  where a.id_ = or_id) "
				+ "where or_name is not null and  or_id =" + store.get("or_id"));
		if(hasRemark) {
			baseDao.saveClob("HrOrg", "or_remark", remark, "or_id=" + store.get("or_id"));
		}
		// 记录操作
		baseDao.logger.save(caller, "or_id", store.get("or_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}
	
	@Override
	public void deleteHrOrg(int or_id, String  caller,Boolean JobOrgNoRelation) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller,  new Object[] { or_id});
		int count = baseDao.getCount("select count(*) from HrOrg where or_subof=" + or_id);
		if(count > 0){
			BaseUtil.showError("该组织已有下级组织，不允许删除！");
		}
		if(!JobOrgNoRelation){
			count = baseDao.getCount("select count(*) from Job where jo_orgid=" + or_id);
			if(count > 0){
				BaseUtil.showError("该组织已有岗位资料，不允许删除！");
			}
		}
		Object or_subof = baseDao.getFieldDataByCondition("HrOrg", "or_subof", "or_id=" +or_id);
		// 删除
		baseDao.deleteById("HrOrg", "or_id", or_id);
		/*此组织的父级组织没有下级组织时，更新是否末级为是(-1)
		 * */
		if(!"0".equals(or_subof.toString())){
			int count1 = baseDao.getCount("select count(*) from Hrorg where or_subof=" + or_subof);
			if(count1 == 0){
				baseDao.updateByCondition("HrOrg","or_isleaf=-1", "or_id="+or_subof);
			}
		}
		// 记录操作
		baseDao.logger.delete(caller, "or_id", or_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller,  new Object[] { or_id});
	}

	@Override
	public void updateHrOrgById(String formStore, String  caller,Boolean JobOrgNoRelation) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String remark = null;
		boolean hasRemark = store.containsKey("OR_REMARK");
		if(hasRemark) {
			remark = String.valueOf(store.get("OR_REMARK"));
			store.remove("OR_REMARK");
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 检查父组织ID是否是自己或者子组织
		checkParentHrOrg(String.valueOf(store.get("or_id")),String.valueOf(store.get("or_subof")));
		// 修改
		Object orid=store.get("or_id");
		Object orcode=baseDao.getFieldValue("HRORG", "or_code", "or_id='"+orid+"'", String.class);
		if(orcode!=null && !orcode.equals(store.get("or_code"))){
			boolean bool=baseDao.checkIf("job", "nvl(jo_orgcode,' ')=(select or_code from hrorg where or_id="+orid+")");
			if(bool) BaseUtil.showError("当前组织关联有其他岗位信息，不允许修改编号!");
		}
		if(!JobOrgNoRelation){
			baseDao.updateByCondition("job","jo_orgname='"+store.get("or_name")+"'", "nvl(jo_orgcode,' ')='"+orcode+"'");
		}
		//,em_depart='"+store.get("or_department")+"',em_departmentcode='"+store.get("or_departmentcode")+"'
		baseDao.updateByCondition("employee","em_defaultorname='"+store.get("or_name")+"'", "nvl(Em_Defaultorcode,' ')='"+orcode+"'");
		baseDao.updateByCondition("hrorg","or_parentname='"+store.get("or_name")+"'", "nvl(or_subof,0)='"+orid+"'");
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "HrOrg", "or_id");
		baseDao.execute(formSql);
		baseDao.execute("update hrorg A set or_subof=nvl((select or_id from hrorg B where A.or_pcode=B.or_code),0) where or_id="+ store.get("or_id"));
		baseDao.execute("update hrorg A set or_parentname=(select or_name from hrorg B where A.or_pcode=B.or_code) where or_id="+ store.get("or_id"));
		baseDao.execute("update hrorg A set or_level=(nvl((select or_level from hrorg B where A.or_subof=B.or_id),0) + 1) where or_id=" + store.get("or_id"));
		baseDao.execute("update hrorg set or_path = (select SUBSTR(a.path_,2) from (select or_id id_,or_name, sys_connect_by_path(or_name,'-') path_ from hrorg "
				+ "START WITH or_subof = 0   CONNECT BY PRIOR or_id = or_subof) a  where a.id_ = or_id) "
				+ "where or_name is not null and  or_id =" + store.get("or_id"));
		if(hasRemark) {
			baseDao.saveClob("HrOrg", "or_remark", remark, "or_id=" + store.get("or_id"));
		}
		// 记录操作
		baseDao.logger.update(caller, "or_id", store.get("or_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public List<CheckBoxTree> getHrOrgStrTree(int parentid, String language) {

		List<CheckBoxTree> tree = new ArrayList<CheckBoxTree>();
		List<HROrg> hrList = hrOrgStrDao.getHrOrgbyParentId(parentid);
		for (HROrg hrOrg : hrList) {
			tree.add(new CheckBoxTree(hrOrg,SystemSession.getLang()));
		}

		return tree;
	}

	@Override
	public List<CheckBoxTree> getAllHrOrgsTree(String language,int parentId,Boolean JobOrgNoRelation) {
		return get(language, "nvl(or_statuscode,' ') <> 'DISABLE'",parentId, false,JobOrgNoRelation);
	}

	public List<CheckBoxTree> getOrgTrees(String language,String condition,Boolean JobOrgNoRelation) {
		baseDao.execute("update hrorg set or_isleaf=0");
		baseDao.execute("update hrorg set or_isleaf=1 where or_id not in(select or_subof from hrorg)");
		return get(language, condition, true,JobOrgNoRelation);
	}

	public CheckBoxTree recursionFn(List<HROrg> hrOrgs, HROrg hrOrg) {
		CheckBoxTree checkBoxTree = new CheckBoxTree();
		checkBoxTree.setId(hrOrg.getOr_id());
		checkBoxTree.setParentId(hrOrg.getOr_subof());
		checkBoxTree.setText(hrOrg.getOr_headmanname() + "  (" + hrOrg.getOr_department() + ")");
		checkBoxTree.setQtip(hrOrg.getOr_headmancode());
		List<HROrg> hList = new ArrayList<HROrg>();
		List<CheckBoxTree> childrenBoxTrees = new ArrayList<CheckBoxTree>();
		if (hrOrg.getOr_isleaf() == 1) {
			checkBoxTree.setAllowDrag(true);
			checkBoxTree.setLeaf(false);
			checkBoxTree.setChildren(new ArrayList<CheckBoxTree>());
		} else {
			checkBoxTree.setLeaf(false);
			CheckBoxTree childrenBoxTree = new CheckBoxTree();
			hList = getChildren(hrOrgs, hrOrg);
			Iterator<HROrg> hIterator = hList.iterator();
			while (hIterator.hasNext()) {
				HROrg hrOrg2 = (HROrg) hIterator.next();
				childrenBoxTree = recursionFn(hrOrgs, hrOrg2);
				childrenBoxTrees.add(childrenBoxTree);
			}
			checkBoxTree.setChildren(childrenBoxTrees);
		}
		return checkBoxTree;
	}

	public boolean isLeaf(List<HROrg> hrOrgs, HROrg hrOrg) {
		return getChildren(hrOrgs, hrOrg).size() > 0 ? false : true;
	}

	public List<HROrg> getChildren(List<HROrg> hrOrgs, HROrg hrOrg) {

		List<HROrg> hrOrgList = new ArrayList<HROrg>();
		Iterator<HROrg> hrOrgIterator = hrOrgs.iterator();
		HROrg hOrg = new HROrg();
		while (hrOrgIterator.hasNext()) {
			hOrg = hrOrgIterator.next();
			if (hOrg.getOr_subof() == hrOrg.getOr_id()) {		
				hrOrgList.add(hOrg);
			}
		}
		return hrOrgList;
	}

	public List<CheckBoxTree> get(String caller, String condition, boolean isOrgNode,boolean JobOrgNoRelation ) {
		List<HROrg> orgList = hrOrgStrDao.getAllHrOrgs(condition);
		List<CheckBoxTree> treeList = new ArrayList<CheckBoxTree>();
		
		for (HROrg hrOrg : orgList) {
			CheckBoxTree ct = new CheckBoxTree(hrOrg, SystemSession.getLang());
			treeList.add(ct);
			List<CheckBoxTree> children = new ArrayList<CheckBoxTree>();
			List<Employee> emList = employeeDao.getEmployeesByOrId(hrOrg.getOr_id());
			if (emList != null) {
				for (Employee employee : emList) {
						children.add(new CheckBoxTree(employee, SystemSession.getLang(), isOrgNode));
				}
			}
			ct.setChildren(children);
		}
		List<CheckBoxTree> root = new ArrayList<CheckBoxTree>();
		for (CheckBoxTree ct : treeList) {
			if ("0".equals(ct.getParentId().toString())) {
				root.add(ct);
			}
		}
		getTree(treeList, root);
		return root;							
	}
	
	public List<CheckBoxTree> get(String caller, String condition, int parentId,boolean isOrgNode,Boolean JobOrgNoRelation) {
		if(condition != null && condition.length() > 0){
			condition+=" and or_subof="+parentId;
		}else{
			condition="or_subof="+parentId;
		}
		List<CheckBoxTree> treeList = new ArrayList<CheckBoxTree>();
		int index=1;
		if(parentId!=0){
			List<Employee> emList=new ArrayList<Employee>();
			if(JobOrgNoRelation){
				emList=employeeDao.getEmployeesByOrgId(parentId);
			}else{
				String codes=baseDao.getFieldValue("Job", "Wmsys.wm_concat(jo_code)", "jo_orgid="+parentId, String.class);
				if(codes!=null){
					String []s=codes.split(",");
					emList=employeeDao.getEmployeesByJobs(s);
				}
			}
			if (emList != null) {
				for (Employee employee : emList) {
					employee.setEm_imageid(index);
					index++;
					CheckBoxTree children = new CheckBoxTree(employee,SystemSession.getLang(), isOrgNode);
					treeList.add(children);
				}
			}
		}
		List<HROrg> orgList = hrOrgStrDao.getAllHrOrgs(condition);
		for (HROrg hrOrg : orgList) {
			CheckBoxTree ct = new CheckBoxTree(hrOrg,SystemSession.getLang());
			treeList.add(ct);
		}
		return treeList;
	}

	public void getTree(List<CheckBoxTree> list, List<CheckBoxTree> root) {
		for (CheckBoxTree ct : root) {
			List<CheckBoxTree> cts = findById(list, ct.getId());
			if (cts.size() != 0) {
				ct.getChildren().addAll(findById(list, ct.getId()));
				getTree(list, ct.getChildren());
			}
		}
	}

	public List<CheckBoxTree> findById(List<CheckBoxTree> list, Object id) {
		List<CheckBoxTree> cts = new ArrayList<CheckBoxTree>();
		for (CheckBoxTree ct : list) {
			if (ct.getParentId().equals(id)) {
				cts.add(ct);
			}
		}
		return cts;
	}

	@Override
	public HROrg getHROrgByCode(String em_code) {
		return hrOrgStrDao.getHrOrgByCode(em_code);
	}

	@Override
	public List<Employee> getEmployeesByOrId(int or_id) {
		return employeeDao.getEmployeesByOrId(or_id);
	}

	@Override
	public List<CheckTree> getHrOrgTree(String language) {

		List<CheckTree> tree = new ArrayList<CheckTree>();
		List<HROrg> hrOrgs = hrOrgStrDao.getAllHrOrgs(null);
		CheckTree checkTree = new CheckTree();
		for (HROrg hrOrg : hrOrgs) {
			if (hrOrg.getOr_subof() == 0) {
				checkTree = recursionCheckTree(hrOrgs, hrOrg);
				tree.add(checkTree);
			}
		}
		return tree;
	}

	public CheckTree recursionCheckTree(List<HROrg> hrOrgs, HROrg hrOrg) {
		CheckTree checkTree = new CheckTree();
		checkTree.setId(hrOrg.getOr_id());
		checkTree.setParentId(hrOrg.getOr_subof());
		checkTree.setText(hrOrg.getOr_department());
		List<HROrg> hList = new ArrayList<HROrg>();
		List<CheckTree> childrenBoxTrees = new ArrayList<CheckTree>();
		if (hrOrg.getOr_isleaf() == 1) {
			checkTree.setAllowDrag(true);
			checkTree.setLeaf(false);
			checkTree.setChildren(new ArrayList<CheckTree>());
		} else {
			checkTree.setLeaf(false);
			CheckTree childrenBoxTree = new CheckTree();
			hList = getChildren(hrOrgs, hrOrg);
			Iterator<HROrg> hIterator = hList.iterator();
			while (hIterator.hasNext()) {
				HROrg hrOrg2 = (HROrg) hIterator.next();
				childrenBoxTree = recursionCheckTree(hrOrgs, hrOrg2);
				childrenBoxTrees.add(childrenBoxTree);
			}
			checkTree.setChildren(childrenBoxTrees);
		}
		return checkTree;
	}

	@Override
	public List<CheckTree> getHrOrgMap(String language) {
		List<CheckTree> tree = new ArrayList<CheckTree>();
		List<HROrg> hrOrgs = hrOrgStrDao.getAllHrOrgs(null);
		CheckTree checkTree = new CheckTree();
		for (HROrg hrOrg : hrOrgs) {
			if (hrOrg.getOr_subof() == 0) {
				checkTree = recursionCheckMap(hrOrgs, hrOrg);
				tree.add(checkTree);
			}
		}
		return tree;
	}
	
	private CheckTree recursionCheckMap(List<HROrg> hrOrgs, HROrg hrOrg) {
		CheckTree checkTree = new CheckTree();
		checkTree.setId(hrOrg.getOr_id());
		checkTree.setParentId(hrOrg.getOr_subof());
		checkTree.setText(hrOrg.getOr_name());
		List<HROrg> hList = new ArrayList<HROrg>();
		List<CheckTree> childrenBoxTrees = new ArrayList<CheckTree>();
		if (hrOrg.getOr_isleaf() == 1) {
			checkTree.setAllowDrag(true);
			checkTree.setLeaf(false);
			checkTree.setChildren(new ArrayList<CheckTree>());
		} else {
			checkTree.setLeaf(false);
			CheckTree childrenBoxTree = new CheckTree();
			hList = getChildren(hrOrgs, hrOrg);
			Iterator<HROrg> hIterator = hList.iterator();
			while (hIterator.hasNext()) {
				HROrg hrOrg2 = (HROrg) hIterator.next();
				childrenBoxTree = recursionCheckMap(hrOrgs, hrOrg2);
				childrenBoxTrees.add(childrenBoxTree);
			}
			checkTree.setChildren(childrenBoxTrees);
		}
		return checkTree;
	}
	
	public List<HROrg> getEmployee(List<HROrg> hrOrgs, HROrg hrOrg) {
		List<HROrg> hrOrgList = new ArrayList<HROrg>();
		return hrOrgList;
	}

	@Override
	public HROrg getHROrgByEmId(int em_id) {
		return hrOrgStrDao.getHrOrgByEmId(em_id);
	}

	@Override
	public void deleteEmployee(int em_id) {
		baseDao.updateByCondition("employee", "em_defaultorid=0,em_defaultorname=''", "em_id=" + em_id);
	}

	@Override
	public void updateEmployee(int em_id, int hrOrgid, String hrOrgName) {
		baseDao.updateByCondition("employee", "em_defaultorid=" + hrOrgid + ",em_defaultorname='" + hrOrgName + "'",
				"em_id=" + em_id);
	}

	@Override
	//@Cacheable(value = "OrgJobEmployees",  key = "#sob+'getOrgsAndEmployees'")
	public List<JSONTree> getHrOrgsTreeAndEmployees(String  caller,Integer parentId,Boolean JobOrgNoRelation) {
		return getOrgsAndEmployees(parentId, JobOrgNoRelation);}
	
	@Override
	public List<JSONTree> getAgentHrOrgsTreeAndEmployees(String caller,Integer parentId, Boolean JobOrgNoRelation) {
		return getAgentOrgsAndEmployees(parentId,JobOrgNoRelation);
	}
	public List<JSONTree> getAgentOrgsAndEmployees(Integer parentId,Boolean JobOrgNoRelation) {
		List<JSONTree> treeList = new ArrayList<JSONTree>();
		Employee employee = SystemSession.getUser();
		Object agentname = baseDao.getFieldDataByCondition("hrorg", "agentname", "or_id="+employee.getEm_defaultorid());
		List<HROrg> orgList = hrOrgStrDao.getAllHrOrgs("nvl(or_statuscode,' ') <> 'DISABLE' and or_subof="+parentId+"and agentname='"+agentname+"'");//组织
		List<Employee> employees=new ArrayList<Employee>();//人员
		List<HRJob> hrjobs=new ArrayList<HRJob>();//岗位
		for (HROrg hrOrg : orgList) {
			if(JobOrgNoRelation){
				employees=employeeDao.getEmployeesByOrgId(hrOrg.getOr_id());
				hrjobs=null;
			}else{
				String codes=baseDao.getFieldValue("Job", "Wmsys.wm_concat(jo_code)", "jo_orgid="+hrOrg.getOr_id()+" and nvl(ISAGENT,0)=-1", String.class);
				if(codes!=null){
					String []s=codes.split(",");
					employees=employeeDao.getEmployeesByJobs(s);
				}
				hrjobs=hrJobDao.getAgentJobsByOrgId(hrOrg.getOr_id());
			}
			JSONTree jsonTree = new JSONTree();
			jsonTree.setId(hrOrg.getOr_id());
			jsonTree.setText(hrOrg.getOr_name());
			jsonTree.setData(employees);
			jsonTree.setOtherInfo(hrjobs);
			jsonTree.setCls("x-tree-parent");
			jsonTree.setAllowDrag(true);
			jsonTree.setLeaf(false);							
			treeList.add(jsonTree);
		}
		return treeList;
	}
	public List<JSONTree> getOrgsAndEmployees(Integer parentId,Boolean JobOrgNoRelation) {
		List<JSONTree> treeList = new ArrayList<JSONTree>();
		List<HROrg> orgList = hrOrgStrDao.getAllHrOrgs("nvl(or_statuscode,' ') <> 'DISABLE' and or_subof="+parentId);//组织
		List<Employee> employees=new ArrayList<Employee>();//人员
		List<HRJob> hrjobs=new ArrayList<HRJob>();//岗位
		for (HROrg hrOrg : orgList) {
			if(JobOrgNoRelation){
				employees=employeeDao.getEmployeesByOrgId(hrOrg.getOr_id());
				hrjobs=null;
			}else{
				String codes=baseDao.getFieldValue("Job", "Wmsys.wm_concat(jo_code)", "jo_orgid="+hrOrg.getOr_id(), String.class);
				if(codes!=null){
					String []s=codes.split(",");
					employees=employeeDao.getEmployeesByJobs(s);
				}
				hrjobs=hrJobDao.getJobsByOrgId(hrOrg.getOr_id());
			}
			JSONTree jsonTree = new JSONTree();
			jsonTree.setId(hrOrg.getOr_id());
			jsonTree.setText(hrOrg.getOr_name());
			jsonTree.setData(employees);
			jsonTree.setOtherInfo(hrjobs);
			jsonTree.setCls("x-tree-parent");
			jsonTree.setAllowDrag(true);
			jsonTree.setLeaf(false);							
			treeList.add(jsonTree);
		}
		return treeList;
	}
	private JSONTree recursionOrgTrees(List<HROrg> orgList, HROrg hrOrg,List<Employee> employees,List<HRJob> jobs) {
		JSONTree jsonTree = new JSONTree();
		jsonTree.setId(hrOrg.getOr_id());
		jsonTree.setParentId(hrOrg.getOr_subof());
		jsonTree.setText(hrOrg.getOr_name());
		jsonTree.setData(filterEmployees(hrOrg.getOr_id(),employees));
		jsonTree.setOtherInfo(filterJobs(hrOrg.getOr_id(), jobs));
		List<HROrg> hList = new ArrayList<HROrg>();
		jsonTree.setCls("x-tree-parent");
		List<JSONTree> childrenTrees = new ArrayList<JSONTree>();
		if (hrOrg.getOr_isleaf() == 1) {
			jsonTree.setAllowDrag(true);
			jsonTree.setLeaf(false);					
		} else {
			jsonTree.setLeaf(false);
			JSONTree childrenTree = new JSONTree();
			hList = getChildren(orgList, hrOrg);
			Iterator<HROrg> hIterator = hList.iterator();
			while (hIterator.hasNext()) {
				HROrg hrOrg2 = (HROrg) hIterator.next();
				childrenTree = recursionOrgTrees(orgList, hrOrg2,employees,jobs);
				childrenTrees.add(childrenTree);
			}
			jsonTree.setChildren(childrenTrees);
		}
		return jsonTree;
	}
	private List<Employee>  filterEmployees(int orid,List<Employee> employees){
		List<Employee> emps=new ArrayList<Employee>();
		Employee e = null;
		Iterator<Employee> ems=employees.iterator();
		while(ems.hasNext()){  
			e=ems.next();  
			if(orid==e.getEm_defaultorid()){  
				emps.add(e);			
			}  
		}
		employees.removeAll(emps);
		return emps;
	}
	private List<HRJob>  filterJobs(int orid,List<HRJob> jobs){
		List<HRJob> js=new ArrayList<HRJob>();
		HRJob job = null;
		Iterator<HRJob> hrjobs=jobs.iterator();
		while(hrjobs.hasNext()){  
			job=hrjobs.next();  
			if(orid==job.getJo_orgid()){  
				js.add(job);			
			}  
		}
		jobs.removeAll(js);
		return js;
	}

	@Override
	public String refreshOrgLevel() {
		baseDao.execute("update hrorg a set a.or_level=" +
				"(select b.olevel from(select or_id,or_subof,level olevel from hrorg " +
				"start with or_subof=0 connect by prior or_id=or_subof) b where a.or_id=b.or_id)");
		return "";
	}
	
	@Override
	public String refreshPositionLevel() {
		baseDao.execute("update job a set jo_level=(select jolevel from (select jo_id,jo_subof,JO_LEVEL,level jolevel from job start with jo_subof=0 connect by prior jo_id=jo_subof) b where a.jo_id=b.jo_id)");
		return "";
	}
	
	@Override
	public String refreshOrgEmployees() {
		baseDao.execute("Begin execute immediate "+
				"'DECLARE "+
				"CURSOR V_CURSOR IS SELECT * FROM hrorg; "+
				"BEGIN "+
				"delete hrorgemployees; "+
				"FOR C1 IN V_CURSOR LOOP "+
				"insert into hrorgemployees(om_orid,om_emid) ( SELECT  C1.or_id,em_id "+
				"FROM  hrorg A left join employee on A.or_id=em_defaultorid  where em_id is not null "+
				"START WITH A.or_id = C1.or_id "+
				"CONNECT BY PRIOR A.or_id = A.or_subof) ; " +    
				"END LOOP; "+
				"END;' ; "+
				"end; ");
		return "";
	}
	/**
	 * @author wsy 刷新下属岗位人员对照关系
	 * */
	@Override
	public String refreshJobEmployees() {
		baseDao.procedure("SP_REFRESH_HRJOBEMPLOYEES",new String[]{});
		return "";
	}
	@Override
	@CacheEvict(value = {"OrgJobEmployees","hrjob"}, allEntries = true)
	public String refreshOrgJobEmployeeTree(String  caller) {
		return "";
	}

	@Override
	public List<JSONTree> getTreeNode(int parentId, String condition) {
		List<JSONTree> tree=new ArrayList<JSONTree>();
		List<HROrg> orgs=hrOrgStrDao.getHrOrgbyParentId(parentId);
		for(HROrg org:orgs){
			tree.add(new JSONTree(org,false));
		}
		List<Employee> employees=employeeDao.getEmployeesByConditon("nvl(em_defaultorid,-1)="+parentId);
		if(employees!=null){
			for(Employee em:employees){
				tree.add(new JSONTree(em));
			}
		}
		return tree;
	}

	@Override
	public HROrg addOrgByParent(int parentId) {
		int orgId=baseDao.getSeqId("hrorg_seq");
		HROrg org=new HROrg();
		HROrg parentOrg=null;
		org.setOr_code("ORG"+orgId);
		try{
			parentOrg=baseDao.getJdbcTemplate().queryForObject("select * from hrorg where or_id=?",new BeanPropertyRowMapper<HROrg>(HROrg.class),parentId);
			org.setOr_code(parentOrg.getOr_code()+"-"+orgId);
			org.setOr_departmentcode(parentOrg.getOr_departmentcode());
			org.setOr_department(parentOrg.getOr_department());
		}catch(Exception e){
			e.printStackTrace();
		}	
		org.setOr_subof(parentId);
		org.setOr_id(orgId);
		org.setOr_name("新组织");
		org.setOr_isleaf(0);
		baseDao.save(org, "HRORG");
		return org;
	}

	@Override
	public void resAuditHrOrg(int id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("HrOrg", "or_statuscode", "or_id=" +id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核前的其它逻辑
		handlerService.handler(caller, "resAudit", "before", new Object[] { id });
		// 执行反审核操作
		baseDao.resOperate("HrOrg", "or_id=" + id, "or_status", "or_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "or_id", id);
		// 执行反审核后的其它逻辑
		handlerService.handler(caller, "resAudit", "after", new Object[] { id });
	}

	@Override
	public void auditHrOrg(int id, String caller) {
		Object[] datas=baseDao.getFieldsDataByCondition("HrOrg",new String[]{"or_statuscode","or_subof"}, "or_id="+id);
		StateAssert.auditOnlyCommited(datas[0]);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { id });
		/*
		 * 此组织有父级组织时，将父级组织是否末级改为否（0）
		 */
		if(!"0".equals(datas[1].toString())){
			baseDao.updateByCondition("HrOrg","or_isleaf=0", "or_id="+datas[1]);
		}
		// 执行审核操作
		baseDao.audit("HrOrg", "or_id=" + id, "or_status", "or_statuscode");
		// 记录操作
		baseDao.logger.audit(caller, "or_id", id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { id });	
	}

	@Override
	public Map<String, String> getEmployees(int id) {
		Map<String, String> map = new HashMap<String, String>();
		SqlRowList rs=baseDao.queryForRowSet("select em_id,em_name from hrorgemployees left join employee on om_emid=em_id where em_class<>'离职' and om_orid="+id);
		StringBuilder ids = new StringBuilder();
		StringBuilder names = new StringBuilder();
		while(rs.next()){
			ids.append(rs.getInt("em_id")).append(",");
			names.append(rs.getString("em_name")).append(",");
		}
		if(names.toString().length()>0){
			map.put("value",names.toString().substring(0,names.length()-1));
			map.put("value1",ids.toString().substring(0,ids.length()-1));
		}else{
			map.put("value","");
			map.put("value1","");
		}
		return map;
	}

	@Override
	@CacheEvict(value = "OrgJobEmployees",  key = "#sob+'getOrgsAndEmployees'")
	public void bannedHrOrg(int id, String caller) {
		handlerService.handler(caller,"banned","before",new Object[]{id});
		int count = baseDao.getCount("select count(*) from Hrorg where nvl(or_statuscode,' ')<>'DISABLE' and or_subof=" +  id);
		StringBuffer sb=new StringBuffer();
		if(count > 0){
			sb.append("该组织有未禁用的下级组织，不允许禁用！").append("<hr>");
		}
		int JobOrgNoRelation=SystemSession.getUser().getJoborgnorelation();
		if(JobOrgNoRelation==0){
			count = baseDao.getCount("select count(*) from job where nvl(jo_statuscode,' ')<>'DISABLE' and jo_orgid=" + id);
			if(count > 0){
				sb.append("该该组织有未禁用的岗位资料，不允许禁用！").append("<hr>");
			}
		}
		if(!baseDao.checkByCondition("Employee", "nvl(em_class,' ')<>'离职' and nvl(em_defaultorid,0)="+ id)){
			sb.append("该组织存在相应的人员信息，不允许禁用！").append("<hr>");
		}
		if(sb.length()>0){
			BaseUtil.showError(sb.toString());
		}
		baseDao.banned("Hrorg", "or_id="+id, "or_status","or_statuscode");
		baseDao.logger.banned(caller, "or_id",id);
		handlerService.handler(caller,"banned","after",new Object[]{id});
		
	}

	@Override
	@CacheEvict(value = "OrgJobEmployees",  key = "#sob+'getOrgsAndEmployees'")
	public void resBannedHrOrg(int id, String caller) {
	// 只能反禁用已禁用的单据!
		Object status = baseDao.getFieldDataByCondition("Hrorg", "or_statuscode", "or_id=" +id);
		if (!"DISABLE".equals(status)) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resBanned_onlyBanned"));
		}
		handlerService.handler(caller, "resBanned", "before", new Object[] { id });
		// 反禁用(修改物料状态为在录入)
		baseDao.resOperate("Hrorg", "or_id=" + id, "or_status", "or_statuscode");
		// 记录操作
		baseDao.logger.resBanned(caller, "or_id", id);
		// 执行反禁用后的其它逻辑
		handlerService.handler(caller, "resBanned", "after", new Object[] {id });
	}

	@Override
	public Map<String, Object> getHrOrg() {
		Map<String,Object> map=new HashMap<String, Object>();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		SqlRowList rs = baseDao.queryForRowSet("select or_id,or_code,or_name,or_headmancode,or_headmanname,em_mobile,em_defaulthsname,em_email,em_sex "
				+ "from hrorg left join employee  on or_headmancode=em_code where nvl(or_statuscode,' ')<>'DISABLE'");
		while(rs.next()) {
			Map<String, Object> data=new HashMap<String, Object>();
			data.put("or_id", rs.getGeneralInt("or_id"));
			data.put("or_code", rs.getGeneralString("or_code"));
			data.put("or_name", rs.getGeneralString("or_name"));
			data.put("or_headmancode", rs.getGeneralString("or_headmancode"));
			data.put("or_headmanname", rs.getGeneralString("or_headmanname"));
			data.put("em_mobile", rs.getGeneralString("em_mobile"));
			data.put("em_defaulthsname", rs.getGeneralString("em_defaulthsname"));
			data.put("em_email", rs.getGeneralString("em_email"));
			data.put("em_sex", rs.getGeneralString("em_sex"));
			datas.add(data);	
		} 
		map.put("data", datas);
		return map;
	}

	@Override
	public Map<String, Object> saveHrOrgAndEmp(String param,String type) throws UnsupportedEncodingException {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(param);
		Map<Object, Object> emStore = new HashMap<Object, Object>();
		Map<String,Object> map=new HashMap<String, Object>();
		List<String> sqls=new ArrayList<String>();
		Object emname=null;
		if("or".equals(type)){
			emname=store.get("or_headmanname");
			//将组织中人员信息添加到emStore中
			emStore.put("em_name", emname);
			emStore.put("em_defaulthsname", store.get("em_defaulthsname"));
			emStore.put("em_email", store.get("em_email"));
			emStore.put("em_mobile", store.get("em_mobile"));
			emStore.put("em_position", store.get("em_defaulthsname"));
			emStore.put("em_sex", store.get("em_sex"));
			//移除组织表中不存在字段
			store.remove("em_defaulthsname");
			store.remove("em_email");
			store.remove("em_mobile");
			store.remove("em_sex");
			store.put("or_department",store.get("or_name"));
			if(!baseDao.checkIf("job","jo_name='"+emStore.get("em_defaulthsname")+"'")){
				Object jocode=baseDao.sGetMaxNumber("Job", 1);
				baseDao.execute("insert into job (jo_id,jo_code,jo_name,jo_status,jo_statuscode) values (JOB_SEQ.nextval,'"+jocode+"','"+emStore.get("em_defaulthsname")+"','已审核','AUDITED')");
			}
			baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "HrOrg", "or_id"));//更新组织信息
			if(emname!=null){
				Object[] ob=baseDao.getFieldsDataByCondition("employee", new String[]{"em_id","em_code"},"em_name='"+emname+"' and em_mobile='"+emStore.get("em_mobile")+"'");
				Object emid=null;
				if(ob!=null){
					emid=ob[0];
					//手机号不能重复
					if(baseDao.checkIf("employee", "em_mobile='"+emStore.get("em_mobile")+"' and em_id<>"+emid)){
						BaseUtil.showError("当前手机号码已被使用，请重新确认");;
					}
					emStore.put("em_code",ob[1]);
					emStore.put("em_id",emid);
					baseDao.execute(SqlUtil.getUpdateSqlByFormStore(emStore, "Employee", "em_id"));
				}else{
					//手机号不能重复
					if(baseDao.checkIf("employee", "em_mobile='"+emStore.get("em_mobile")+"'")){
						BaseUtil.showError("当前手机号码已被使用，请重新确认");;
					}
					emid= baseDao.getSeqId("EMPLOYEE_SEQ");
					emStore.put("em_id",emid);
					emStore.put("em_type","normal");
					emStore.put("em_class","正式");
					//密码加密
					emStore.put("em_password",PasswordEncryUtil.encryptPassword("111111", String.valueOf(emStore.get("em_mobile"))));
					emStore.put("em_status","已审核");
					emStore.put("em_statuscode","AUDITED");
					String emcode=Pinyin.converterToSpell(emname.toString()).toUpperCase().split(",")[0];
					if(baseDao.checkIf("employee", "em_code='"+emcode+"'")){
						String code = baseDao.getJdbcTemplate().queryForObject(
								"select  nvl(max(translate(em_code,'9876543210'||em_code,'9876543210')),0)+1 from  employee where"
								+ " em_code like '"+emcode+"%' and  REGEXP_LIKE(translate(em_code, '9876543210' ||em_code, '9876543210'),'(^[+-]?\\d{0,}\\.?\\d{0,}$)')", String.class);
						if (code != null) {
							emcode=emcode+code;
						}
					}
					emStore.put("em_code",emcode);
					if("".equals(store.get("or_headmancode"))){
						sqls.add("update HrOrg set or_headmancode='"+emcode+"' where or_id="+store.get("or_id"));	
					}					
					baseDao.execute(SqlUtil.getInsertSqlByFormStore(emStore, "Employee", new String[] {}, new Object[] {}));
				}
				sqls.add("update hrorg set or_headmancode='"+emStore.get("em_code")+"' where or_id="+store.get("or_id"));
				//更新人员组织信息
				sqls.add("update employee set (em_defaultorid,em_defaultorname,em_defaultorcode,em_departmentcode,em_depart)=(select or_id,or_name,or_code,or_code,or_name from hrorg"
						+ " where or_id="+store.get("or_id")+") where nvl(em_defaultorid,0)=0 and  em_id="+emid);
				sqls.add("update employee set (em_defaulthsid,em_defaulthscode)=(select jo_id,jo_code from job"
						+ " where jo_name=em_defaulthsname) where  em_id="+emid);
				//更新组织层级
				sqls.add("update hrorg A set or_level=(nvl((select or_level from hrorg B where A.or_subof=B.or_id),0) + 1) where or_id="+ store.get("or_id"));
				//更新组织是否末级
				sqls.add("update hrorg set or_isleaf=0");
				sqls.add("update hrorg set or_isleaf=1 where or_id not in(select or_subof from hrorg)");
				//更新组织父组织名称
				sqls.add("update hrorg set or_parentname='"+store.get("or_name")+"' where nvl(or_subof,0)="+store.get("or_id"));
				//根据组织信息更新部门信息
				sqls.add("update department set (dp_headmancode,dp_headmanname,dp_isleaf,dp_level,dp_pcode,dp_parentdpname)=(select or_headmancode,or_headmanname,0-or_isleaf,or_level,or_pcode,or_parentname from hrorg where or_departmentcode=dp_code)  where dp_code='"+store.get("or_code")+"'");
				//更新人员部门信息
				sqls.add("update employee set em_departmentcode=em_defaultorcode,em_depart=em_defaultorname where em_id="+emid);
			}
		}else{
			emStore=store;
			emname=store.get("em_name"); 
			String emcode=Pinyin.converterToSpell(emname.toString()).toUpperCase().split(",")[0];
			emStore.put("em_defaulthsname", store.get("em_position"));
			//岗位不存在添加岗位
			if(!baseDao.checkIf("job","jo_name='"+emStore.get("em_defaulthsname")+"'")){
				Object jocode=baseDao.sGetMaxNumber("Job", 1);
				baseDao.execute("insert into job (jo_id,jo_code,jo_name,jo_status,jo_statuscode) "
						+ "values(JOB_SEQ.nextval,'"+jocode+"','"+emStore.get("em_defaulthsname")+"','已审核','AUDITED')");
			}
			Object emid=null;
			String con="";
			if(store.get("em_id")!=null&&!"".equals(store.get("em_id"))&&!"0".equals(store.get("em_id"))){
				//手机号不能重复
				if(baseDao.checkIf("employee", "em_mobile='"+emStore.get("em_mobile")+"' and em_id<>"+emid)){
					BaseUtil.showError("当前手机号码已被使用，请重新确认");;
				}
				emid=store.get("em_id");
				con=" and em_id<>"+emid;
				baseDao.execute(SqlUtil.getUpdateSqlByFormStore(emStore, "Employee", "em_id"));
			}else{
				if(baseDao.checkIf("employee", "em_mobile='"+emStore.get("em_mobile")+"'")){
					BaseUtil.showError("当前手机号码已被使用，请重新确认");;
				}
				emStore.put("em_type","normal");
				emStore.put("em_class","正式");
				//密码加密
				emStore.put("em_password",PasswordEncryUtil.encryptPassword("111111", String.valueOf(emStore.get("em_mobile"))));
				emStore.put("em_status","已审核");
				emStore.put("em_statuscode","AUDITED");
				if(baseDao.checkIf("employee", "em_code='"+emcode+"'")){
					String code = baseDao.getJdbcTemplate().queryForObject(
							"select  nvl(max(translate(em_code,'9876543210'||em_code,'9876543210')),0)+1 from  employee where"
							+ " em_code like '"+emcode+"%' and  REGEXP_LIKE(translate(em_code, '9876543210' ||em_code, '9876543210'),'(^[+-]?\\d{0,}\\.?\\d{0,}$)')"+con, String.class);
					if (code != null) {
						emcode=emcode+code;
					}
				}
				emStore.put("em_code", emcode);
				emid= baseDao.getSeqId("EMPLOYEE_SEQ");
				emStore.put("em_id",emid);
				baseDao.execute(SqlUtil.getInsertSqlByFormStore(emStore, "Employee", new String[] {}, new Object[] {}));
			}
			sqls.add("update employee set (em_defaultorname,em_defaultorcode,em_departmentcode,em_depart)=(select or_name,or_code,or_departmentcode,or_department from hrorg"
					+ " where or_id=em_defaultorid) where   em_id="+emid);
			sqls.add("update employee set (em_defaulthsid,em_defaulthscode)=(select jo_id,jo_code from job"
					+ " where jo_name=em_defaulthsname) where  em_id="+emid);
			map.put("id", emid);
		}
		baseDao.execute(sqls);
		baseDao.execute("update employee set em_enid=(select en_id from enterprise ) where nvl(em_enid,0)=0 ");
		return map;
	}

	@Override
	public void saveHrOrgSaas(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		if(baseDao.checkIf("HRORG", "or_name='"+store.get("or_name")+"' and or_subof="+store.get("or_subof"))){
			BaseUtil.showError("当前父级组织下此组织名称已存在");
		}	
		Object orid = store.get("or_id");
		if(orid.equals("0")){
			orid = baseDao.getSeqId("EMPLOYEE_SEQ");
		}
		store.put("or_id",orid);
		String remark = null;
		boolean hasRemark = store.containsKey("or_remark");
		if(hasRemark) {
			remark = String.valueOf(store.get("or_remark"));
			store.remove("or_remark");
		}
		Object parentId=store.get("or_subof");
		int level=1;
		if(parentId!=null){
			if(!"0".equals(parentId)){
				Object[] ob=baseDao.getFieldsDataByCondition("hrorg", new String[]{"or_code","or_name","or_level"}, "or_id="+parentId);
				level=Integer.parseInt(ob[2].toString())+1;
				store.put("or_pcode",ob[0]);
				store.put("or_parentname",ob[1]);
			}
		}
		Object orcode=baseDao.getFieldDataByCondition("hrorg","max(or_code)+1 or_code", "or_level="+level);
		if(orcode!=null) {
			store.put("or_code", orcode);
		}else{
			store.put("or_code", level+"01");
		}
		//部门
		store.put("or_departmentcode",orcode);
		store.put("or_department", store.get("or_name"));  //get 组织编号和组织名称给部门
		store.put("or_status","已审核");
		store.put("or_statuscode","AUDITED");
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "HrOrg", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.execute("update hrorg A set or_level=(nvl((select or_level from hrorg B where A.or_subof=B.or_id),0) + 1) where or_id="+ orid);
		baseDao.execute("update hrorg set or_isleaf=0");
		baseDao.execute("update hrorg set or_isleaf=1 where or_id not in(select or_subof from hrorg)");
		if(hasRemark) {
			baseDao.saveClob("HrOrg", "or_remark", remark, "or_id=" + orid);
		}
		if(!baseDao.checkIf("department", "dp_code='"+orcode+"'")){
			baseDao.execute("insert into department (dp_id,dp_code,dp_name,dp_isleaf,dp_status,dp_statuscode,dp_headmancode,dp_headmanname,dp_level,dp_pcode,dp_parentdpname) "
					+ "select DEPARTMENT_SEQ.nextval,or_code,or_name,-1,or_status,or_statuscode,or_headmancode,or_headmanname,or_level,or_pcode,or_parentname from hrorg where or_id="+orid);
		}else{
			baseDao.execute("update department set (dp_name,dp_headmancode,dp_headmanname,dp_isleaf,dp_level)=(select oe_name,or_headmancode,or_headmanname,0-or_isleaf,or_level from hrorg where or_code=dp_code)  where dp_code='"+store.get("or_code")+"'");
		}
		baseDao.execute("update department set dp_isleaf=0");
		baseDao.execute("update department set dp_isleaf=-1 where dp_code not in(select dp_pcode from department where dp_pcode is not null)");
	}
	/**
	 *  新增SaasHrOrg删除操作
	 */
	@Override
	public void deleteHrOrgSaas(int or_id, String  caller){
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller,  new Object[] { or_id});
		int count = baseDao.getCount("select count(*) from HrOrg where or_subof=" + or_id);
		if(count > 0){
			BaseUtil.showError("该组织已有下级组织，不允许删除！");
		}
		if(baseDao.checkIf("employee","em_defaultorid="+or_id+" and nvl(em_class,' ')<>'离职'")){
			BaseUtil.showError("该组织有未离职的员工，不允许删除！");
		}
		Object[] or_subof = baseDao.getFieldsDataByCondition("HrOrg", "or_subof,or_code", "or_id=" +or_id);
		// 删除
		baseDao.deleteByCondition("department", "dp_code='"+or_subof[1]+"'");
		baseDao.deleteById("HrOrg", "or_id", or_id);
		/*此组织的父级组织没有下级组织时，更新是否末级为是(-1)
		 * */
		if(!"0".equals(or_subof.toString())){
			int count1 = baseDao.getCount("select count(*) from Hrorg where or_subof=" + or_subof[0]);
			if(count1 == 0){
				baseDao.updateByCondition("HrOrg","or_isleaf=-1", "or_id="+or_subof[0]);
			}
		}
		baseDao.execute("update department set dp_isleaf=0");
		baseDao.execute("update department set dp_isleaf=-1 where dp_code not in(select dp_pcode from department where dp_pcode is not null)");
		// 记录操作
		baseDao.logger.delete(caller, "or_id", or_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller,  new Object[] { or_id});	
	}
	
	@Override
	public void updateHrOrgByIdSaas(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		if(baseDao.checkIf("HRORG", "or_name='"+store.get("or_name")+"'and or_subof="+store.get("or_subof")+" and or_id<>"+store.get("or_id"))){
			BaseUtil.showError("当前父级组织下此组织名称已存在");
		}	
		String remark = null;
		boolean hasRemark = store.containsKey("or_remark");
		if(hasRemark) {
			remark = String.valueOf(store.get("or_remark"));
			store.remove("or_remark");
		}
		// 修改
		Object orid=store.get("or_id");
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "HrOrg", "or_id");
		baseDao.execute(formSql);
		if(hasRemark) {
			baseDao.saveClob("HrOrg", "or_remark", remark, "or_id=" + store.get("or_id"));
		}
		baseDao.updateByCondition("employee","em_defaultorname='"+store.get("or_name")+"'", "nvl(Em_Defaultorcode,' ')='"+store.get("or_code")+"'");
		baseDao.updateByCondition("hrorg","or_parentname='"+store.get("or_name")+"'", "nvl(or_subof,0)='"+orid+"'");
		baseDao.execute("update hrorg A set or_level=(nvl((select or_level from hrorg B where A.or_subof=B.or_id),0) + 1) where or_id=" + orid);
		baseDao.execute("update hrorg set or_isleaf=0");
		baseDao.execute("update hrorg set or_isleaf=1 where or_id not in(select or_subof from hrorg)");
		baseDao.execute("update department set (dp_name,dp_headmancode,dp_headmanname,dp_isleaf,dp_level)=(select or_name,or_headmancode,or_headmanname,0-or_isleaf,or_level from hrorg where or_code=dp_code)  where dp_code='"+store.get("or_code")+"'");
		// 也更新部门名称
	}

	@Override
	public List<JSONTree> getHrOrgTreeSaas() {
		List<JSONTree> tree = new ArrayList<JSONTree>();
		List<HROrg> hrOrgs = hrOrgStrDao.getAllHrOrgs(null);
		JSONTree jsonTree = new JSONTree();
		for (HROrg hrOrg : hrOrgs) {
			if (hrOrg.getOr_subof() == 0) {
				jsonTree = recursionTree(hrOrgs, hrOrg);
				tree.add(jsonTree);
			}
		}
		return tree;
	}
	public JSONTree recursionTree(List<HROrg> hrOrgs, HROrg hrOrg) {
		JSONTree jsonTree = new JSONTree();
		jsonTree.setId(hrOrg.getOr_id());
		jsonTree.setParentId(hrOrg.getOr_subof());
		jsonTree.setText(hrOrg.getOr_name());
		List<HROrg> hList = new ArrayList<HROrg>();
		List<JSONTree> childrenTrees = new ArrayList<JSONTree>();
		if (hrOrg.getOr_isleaf() == 1) {
			jsonTree.setAllowDrag(true);
			jsonTree.setLeaf(false);
			jsonTree.setChildren(new ArrayList<JSONTree>());
		} else {
			jsonTree.setLeaf(false);
			JSONTree childrenTree = new JSONTree();
			hList = getChildren(hrOrgs, hrOrg);
			Iterator<HROrg> hIterator = hList.iterator();
			while (hIterator.hasNext()) {
				HROrg hrOrg2 = (HROrg) hIterator.next();
				childrenTree= recursionTree(hrOrgs, hrOrg2);
				childrenTrees.add(childrenTree);
			}
			jsonTree.setChildren(childrenTrees);
		}
		return jsonTree;
	}

	/**
	 * @author wsy
	 * 	组织架构设置树
	 */	 
	@Override
	public List<JSONTree> getChildTreeNode(String condition) {
		List<JSONTree> tree=new ArrayList<JSONTree>();
		List<HROrg> orgs= hrOrgStrDao.getAllHrOrgs(condition);
		for(HROrg org:orgs){
			tree.add(new JSONTree(org,false));
		}
		return tree;
	} 

	@Override
	public void updateHrOrg(String formStore) {
		Map<Object,Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "HrOrg", "or_id");
		baseDao.execute(formSql);
		}

	@Override
	public void deleteHrOrgById(int id) {

		int count = baseDao.getCount("select count(*) from HrOrg where or_subof=" + id);
		if(count > 0){
			BaseUtil.showError("该组织已有下级组织，不允许删除！");
		}
		Object or_subof = baseDao.getFieldDataByCondition("HrOrg", "or_subof", "or_id=" +id);
		// 删除
		baseDao.deleteById("HrOrg", "or_id", id);
		/*此组织的父级组织没有下级组织时，更新是否末级为是(-1)
		 * */
		if(!"0".equals(or_subof.toString())){
			int count1 = baseDao.getCount("select count(*) from Hrorg where or_subof=" + or_subof);
			if(count1 == 0){
				baseDao.updateByCondition("HrOrg","or_isleaf=-1", "or_id="+or_subof);
			}
		}
	}

	@Override
	public HROrg addOrg(int parentId) {
		int orgId=baseDao.getSeqId("hrorg_seq");
		HROrg org=new HROrg();
		HROrg parentOrg=null;
		org.setOr_code("ORG"+orgId);
		try{
			parentOrg=baseDao.getJdbcTemplate().queryForObject("select * from hrorg where or_id=?",new BeanPropertyRowMapper<HROrg>(HROrg.class),parentId);
			org.setOr_code(parentOrg.getOr_code()+"-"+orgId);
			org.setAgentuu(parentOrg.getAgentuu());
		}catch(Exception e){
			e.printStackTrace();
		}
		org.setOr_subof(parentId);
		org.setOr_id(orgId);
		org.setOr_name("新组织");
		org.setOr_status("已审核");
		org.setOr_statuscode("AUDITED");
		org.setOr_isleaf(0);
		baseDao.save(org, "HRORG");
		try{
			baseDao.execute("update hrorg set AGENTNAME=(select cu_name from customer where cu_uu=AGENTUU) where or_id="+orgId);
		}catch(Exception e){
			e.printStackTrace();
		}
		return org;
	}
	
	/**
	 * @author lidy  反馈编号：2017120290    检查父组织ID是否是自己或者子组织。  
	 * @param or_id 组织ID
	 * @param or_subof 父组织ID
	 */
	private void checkParentHrOrg(String or_id,String or_subof){
		if(or_subof!=null&&!"".equals(or_subof.trim())&&!"0".equals(or_subof.trim())){	
			int count = 0;
			try{
				count = baseDao.getCountByCondition("dual", or_subof+" in (select or_id from hrorg START WITH or_id="+or_id+" connect by PRIOR or_id = or_subof)");
			}catch(Exception e){
				BaseUtil.appendError("该组织资料原来的父组织编号为本组织编号或者子组织编号，请确认修改后父组织编号是否正确");
			}
			if(count>0){
				BaseUtil.showError("父组织编号不能为本组织编号或者子组织编号");
			}
		}
	}
	/**
	 * 获取系统参数设置的组织架构图的level值
	 * @return
	 */
	public int getHrOrgMapLevel(){
		Object level = baseDao.getFieldDataByCondition("configs", "data", "caller='sys' and code='hrOrgMap'");
		String levelStr = String.valueOf(level);
		if(levelStr != null && levelStr != "null"){
			return Integer.parseInt(levelStr);
		}else{
			return 4;
		}		
	}
	
	/**
	 * 修改系统参数设置的组织架构图的level值
	 * @return
	 */
	public void updateHrOrgMapLevel(int level){
		baseDao.updateByCondition("configs", "data = " + level, "code = 'hrOrgMap' and caller = 'sys'");
	}
}
