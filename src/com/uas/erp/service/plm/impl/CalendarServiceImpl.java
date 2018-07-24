package com.uas.erp.service.plm.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.CalendarDao;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.Employee;
import com.uas.erp.service.plm.CalendarService;

@Service
public class CalendarServiceImpl implements CalendarService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private CalendarDao calendarDao;
	@Autowired
	private DetailGridDao detailGridDao;

	@Override
	public void saveEvents(String addData, String updateData, String deleteData) {
		// 保存 修改 删除
		if (addData.contains("}")) {
			calendarDao.save(addData,"EventId");
		}
		if (updateData.contains("}")) {
			calendarDao.update(updateData, "EventId");
		}
		if (deleteData.contains("}")) {
			calendarDao.delete(deleteData, "EventId");
		}
	}

	@Override
	public String getCalendar(String caller, String emid, Employee employee) {
		List<DetailGrid> detailGrids = detailGridDao.getDetailGridsByCaller(caller, SpObserver.getSp());
		String condition = emid.equals("") ? "" : " WHERE ra_emid=" + emid;
		return BaseUtil.parseGridStore2Str(baseDao.getDetailGridData(detailGrids, condition, employee, null, null));
	}

	@Override
	public String getMyCalendar(String emcode,String condition) {
		return calendarDao.getMyData(emcode,condition);

	}

	@Override
	public String getMyAgenda(String emid) {
		return calendarDao.getMyAgenda(emid);

	}
}
