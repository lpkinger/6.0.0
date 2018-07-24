package com.uas.erp.service.plm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.TeamMemberDao;
import com.uas.erp.model.Teammember;
import com.uas.erp.service.plm.TeamMemberService;
@Service
public class TeamMemberServiceImpl implements TeamMemberService {
	
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private TeamMemberDao teammemberDao;
	@Override
	public void saveTeamMember(String formStore, String param) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行保存前的其它逻辑
		handlerService.handler("Teammember", "save", "before", new Object[]{store});
		//执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Teammember", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save("Teammember", "tm_id", store.get("tm_id"));
		//执行保存后的其它逻辑
		handlerService.handler("Teammember", "save", "after", new Object[]{store});
	}

	@Override
	public Teammember getTeamMemberByIdCode(int team_id, String employee_code) {
		return  teammemberDao.getTeammemberByIdCode(team_id, employee_code);
	}

}
