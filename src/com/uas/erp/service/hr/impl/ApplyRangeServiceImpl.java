package com.uas.erp.service.hr.impl;

import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.dao.common.HrOrgStrDao;
import com.uas.erp.model.*;
import com.uas.erp.service.hr.ApplyRangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ApplyRangeServiceImpl implements ApplyRangeService{
	
	@Autowired
	private EmployeeDao employeeDao;
	
	@Autowired
	private HrOrgStrDao hrOrgStrDao;

    @Autowired
    private BaseDao baseDao;

	@Override
	public List<CheckBoxTree> getAllHrOrgsTree(String caller) {
		return get(caller);
	}

    @Override
    public void setEmpAttendItem(int aiid, String ids, String caller) {
        String [] empIds = ids.split(",");
        List<String> sqls = new ArrayList<String>();
        String sql = "";
        for (int i = 0; i < empIds.length; i++) {
            SqlRowList list = baseDao.queryForRowSet("SELECT * FROM EMPATTENDITEM WHERE EAI_AIID=" + aiid + " AND EAI_EMID=" + empIds[i]);
            if (list.getResultList() == null || list.getResultList().size() <= 0) {
                int id = baseDao.getSeqId("EMPATTENDITEM_SEQ");
                sql = String.format("INSERT INTO EMPATTENDITEM(EAI_ID,EAI_AIID,EAI_EMID) VALUES(%d,%d,%d)",
                        id, aiid, Integer.parseInt(empIds[i]));
                sqls.add(sql);
            }
        }

        if (sqls.size() > 0) {
            baseDao.execute(sqls);
        }
    }

    public List<HROrg> getChildren(List<HROrg> hrOrgs,HROrg hrOrg){
		
		List<HROrg> hrOrgList = new ArrayList<HROrg>();
		Iterator<HROrg> hrOrgIterator = hrOrgs.iterator();
		HROrg hOrg = new HROrg();
		while (hrOrgIterator.hasNext()) {
			hOrg = hrOrgIterator.next();
			if (hOrg.getOr_subof() == hrOrg.getOr_id()){
				hrOrgList.add(hOrg);
			}
		}
		return hrOrgList;
	}
	
	public List<CheckBoxTree> get(String caller){
		List<HROrg> orgList = hrOrgStrDao.getAllHrOrgs(null);
		List<CheckBoxTree> treeList = new ArrayList<CheckBoxTree>();
		for (HROrg hrOrg : orgList) {
			CheckBoxTree ct = new CheckBoxTree(hrOrg, SystemSession.getLang(),caller);
            ct.setText(hrOrg.getOr_department());
			treeList.add(ct);
			List<Employee> emList = employeeDao.getEmployeesByOrIdWithWDM(hrOrg.getOr_id(),caller);
			List<CheckBoxTree> children = new ArrayList<CheckBoxTree>();
			
			if(emList != null){
				for (Employee employee : emList) {
					children.add(new CheckBoxTree(employee, SystemSession.getLang(), false, caller));
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
	
	public void getTree(List<CheckBoxTree> list, List<CheckBoxTree> root){
		for (CheckBoxTree ct : root) {
			List<CheckBoxTree> cts = findById(list, ct.getId());
			if (cts.size() != 0) {
				ct.getChildren().addAll(findById(list, ct.getId()));				
				getTree(list, ct.getChildren());
			}
		}
	}
	public List<CheckBoxTree> findById(List<CheckBoxTree> list, Object id){
		List<CheckBoxTree> cts = new ArrayList<CheckBoxTree>();
		for (CheckBoxTree ct : list) {
			if (ct.getParentId().equals(id)) {
				cts.add(ct);
			}
		}
		return cts;
	}
}
