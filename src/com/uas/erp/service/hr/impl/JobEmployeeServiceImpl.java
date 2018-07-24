package com.uas.erp.service.hr.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.JobEmployeeService;

@Service
public class JobEmployeeServiceImpl implements JobEmployeeService {
	@Autowired
	private BaseDao baseDao;

	@Override
	public void saveJobEmployee(String formStore, String gridStore,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "JobEmployee", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
	}

	@Override
	public void updateJobEmployeeById(String formStore, String gridStore,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "JobEmployee", "je_id");
		baseDao.execute(formSql);
	}

	@Override
	public void deleteJobEmployee(int je_id, String caller) {
		
		baseDao.deleteById("JobEmployee", "je_id", je_id);
	}

	@Override
	public void printJobEmployee(int je_id, String caller) {
	
	}

	@Override
	public void auditJobEmployee(int je_id, String caller) {
		

	}

	@Override
	public void resAuditJobEmployee(int je_id, String caller) {
		// TODO Auto-generated method stub

	}

	@Override
	public void submitJobEmployee(int je_id, String caller) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resSubmitJobEmployee(int je_id, String caller) {
		// TODO Auto-generated method stub

	}

}
