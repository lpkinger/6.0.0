package com.uas.erp.service.oa.impl;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.oa.TaskTemplateService;
@Service
public class TaskTemplateServiceImpl implements TaskTemplateService {
	@Autowired
	private BaseDao baseDao;
	@Override
	public void saveTaskTemplate(String formStore) {
		// TODO Auto-generated method stub
		Map<Object,Object> map=BaseUtil.parseFormStoreToMap(formStore);
		map.put("TT_UPDATETIME",DateUtil.parseDateToString(new Date(), Constant.YMD_HMS));
		String reportformula=map.get("TT_REPORTFORMULA")!=null?String.valueOf(map.get("TT_REPORTFORMULA")):null;
		map.remove("TT_REPORTFORMULA");
		baseDao.execute(SqlUtil.getInsertSqlByMap(map, "TaskTemplate"));
		if(reportformula!=null)baseDao.saveClob("TaskTemplate", "TT_REPORTFORMULA", reportformula, "TT_ID="+map.get("tt_id"));
		baseDao.logger.save("TaskTemplate", "tt_id", map.get("tt_id"));	     
	}

	@Override
	public void updateTaskTemplate(String formStore) {
		// TODO Auto-generated method stub
		Employee em=SystemSession.getUser();
		Map<Object,Object> map=BaseUtil.parseFormStoreToMap(formStore);
		String reportformula=map.get("TT_REPORTFORMULA")!=null?String.valueOf(map.get("TT_REPORTFORMULA")):null;
		map.remove("TT_REPORTFORMULA");
		map.put("TT_UPDATER", em.getEm_name());
		map.put("TT_UPDATETIME",DateUtil.parseDateToString(new Date(), Constant.YMD_HMS));
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(map, "TaskTemplate","tt_id"));
		if(reportformula!=null )baseDao.saveClob("TaskTemplate", "TT_REPORTFORMULA",reportformula, "TT_ID="+map.get("tt_id"));
		baseDao.logger.save("TaskTemplate", "tt_id", map.get("tt_id"));
	}

	@Override
	public void deleteTaskTemplate(int id) {
		// TODO Auto-generated method stub
        baseDao.deleteById("TaskTemplate", "tt_id", id);
        baseDao.logger.delete("TaskTemplate", "tt_id", id);
	}

	@Override
	public void resBannedTaskTemplate(int id) {
		// TODO Auto-generated method stub
       baseDao.resOperate("TaskTemplate", "tt_id="+id,"tt_status", "tt_statuscode");
	}

	@Override
	public void bannedTaskTemplate(int id) {
		// TODO Auto-generated method stub
		 baseDao.banned("TaskTemplate", "tt_id="+id,"tt_status", "tt_statuscode");
	}

	@Override
	public String getTaskNodes(String caller, int keyvalue) {
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	public void loadTaskTemplate(String caller, int keyValue) {
		// TODO Auto-generated method stub
		baseDao.callProcedure("SYS_AUTOTASK", new Object[]{caller,keyValue});
	}

}
