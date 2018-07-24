package com.uas.erp.service.plm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.plm.ProjectTeamRoleService;

@Service
public class ProjectTeamRoleServiceImpl implements ProjectTeamRoleService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void updateProjectTeamRole(String formStore, String caller) {
			
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "TeamRole", "tr_id");
		baseDao.execute(formSql);
		baseDao.logger.update(caller, "tr_id", store.get("tr_id"));
		
	}

	@Override
	public void saveProjectTeamRole(String formStore, String caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "TeamRole", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.logger.save(caller, "tr_id", store.get("tr_id"));
	}

	@Override
	public void deleteProjectTeamRole(int id, String caller) {

		handlerService.handler(caller, "delete", "before", new Object[] { id });
		// 删除ProjectColor
		baseDao.deleteById("TeamRole", "tr_id", id);
		// 记录操作
		baseDao.logger.delete(caller, "tr_id", id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { id });
		
	}
}
