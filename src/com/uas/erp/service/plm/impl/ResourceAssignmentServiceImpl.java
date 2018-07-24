package com.uas.erp.service.plm.impl;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.ResourceAssignmentDao;
import com.uas.erp.service.plm.ResourceAssignmentService;
@Service("resourceAssignmentService")
public class ResourceAssignmentServiceImpl implements ResourceAssignmentService{
@Autowired
private ResourceAssignmentDao resourceAssignmentDao;
@Autowired
private BaseDao baseDao;
	@Override
	public JSONArray getData(String caller, String condition) {
		return resourceAssignmentDao.getData(caller, condition);
	}
	@Override
	public JSONObject getResourceData(String prjplanid) {
		// TODO Auto-generated method stub
		return resourceAssignmentDao.getResourceData(prjplanid);
	}
//	public JSONObject getTaskResourceData(){}
	@Override
	public void saveAssignment(String jsonData) {
		Object[] id = new Object[1];
		if(jsonData.contains("},")){//明细行有多行数据哦
			String[] datas = jsonData.split("},");
			id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				id[i] = baseDao.getSeqId("RESOURCEASSIGNMENT_SEQ");
			}
		} else {
			id[0] = baseDao.getSeqId("RESOURCEASSIGNMENT_SEQ");
		}
		resourceAssignmentDao.saveAssignment(jsonData,"Id",id);
	}
	@Override
	public void updateAssignment(String jsonData) {
		// TODO Auto-generated method stub
		resourceAssignmentDao.updateAssignment(jsonData,"Id");
	}
	@Override
	public void deleteAssignment(String jsonData) {
		// TODO Auto-generated method stub
		resourceAssignmentDao.deleteAssignment(jsonData,"Id");
	}
	@Override
	public JSONArray getTaskResourceData(String caller, String condition) {
		
		return resourceAssignmentDao.getTaskResourceData(caller,  condition);
	}
	@Override
	public JSONArray getTaskAssignmentData(String caller, String condition) {
		// TODO Auto-generated method stub
		return resourceAssignmentDao.getTaskAssignmentData(caller, condition);
	}
}
