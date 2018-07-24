package com.uas.mobile.service.impl;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.MobileSessionContext;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.DataList;
import com.uas.erp.model.Employee;
import com.uas.mobile.model.MobileTask;
import com.uas.mobile.model.Stats;
import com.uas.mobile.service.CanlendarTaskService;
@Service
public class CanlendarTaskServiceImpl implements CanlendarTaskService{
	@Autowired
	private BaseDao baseDao;

	@Override
	public List<MobileTask> getCanlendarTask(String condition,String sessionId) {
		// TODO Auto-generated method stub
		String sql="select * from Warntask_View";
		HttpSession session=MobileSessionContext.getInstance().getSessionById(sessionId);
		
		if(session!=null)  {
			Employee em=(Employee)session.getAttribute("employee");
			sql+=" where RESOURCECODE='"+em.getEm_code()+"'";
		}
		else BaseUtil.showError("断开连接!");
		if(condition!=null) sql+=" and "+condition;
		List<MobileTask>  tasks=baseDao.getJdbcTemplate().query(sql, new BeanPropertyRowMapper<MobileTask>(MobileTask.class));
		return tasks;
	}
	

}
