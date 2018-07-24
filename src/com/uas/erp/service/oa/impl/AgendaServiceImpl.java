package com.uas.erp.service.oa.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.common.AgendaDao;
import com.uas.erp.model.Agenda;
import com.uas.erp.service.oa.AgendaService;

@Service
public class AgendaServiceImpl implements AgendaService {

	/*@Autowired
	private BaseDao baseDao;*/
	@Autowired
	private  AgendaDao agendaDao;
	/*@Autowired
	private HandlerService handlerService;*/
	@Override
	public void deleteById(int id) {
		agendaDao.delete(id);		
	}
	@Override
	public List<Agenda> getArrangeList(String caller, int page, int pageSize) {
		return agendaDao.getArrangeList(SystemSession.getUser().getEm_id(), page, pageSize);
	}
	@Override
	public int getArrangeListCount(String caller) {
		return agendaDao.getArrangeListCount(SystemSession.getUser().getEm_id());
	}
	@Override
	public int getSearchCount(String condition) {
		return agendaDao.getSearchCount(condition);
	}
	@Override
	public List<Agenda> searchByCondition(String condition, int page, int pageSize) {
		return agendaDao.getByCondition(condition, page, pageSize);
	}
	@Override
	public Agenda getAgendaById(int id) {
		return agendaDao.getAgendaById(id);
	}
	@Override
	public List<Agenda> getList(String caller, int page, int pageSize) {
		return agendaDao.getList(SystemSession.getUser().getEm_id(), page, pageSize);
	}
	@Override
	public int getListCount(String caller) {
		return agendaDao.getListCount(SystemSession.getUser().getEm_id());
	}
}
