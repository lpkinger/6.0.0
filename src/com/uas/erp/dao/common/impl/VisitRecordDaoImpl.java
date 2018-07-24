package com.uas.erp.dao.common.impl;

import org.springframework.stereotype.Repository;


import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.VisitRecordDao;
import com.uas.erp.model.Employee;
@Repository
public class VisitRecordDaoImpl extends BaseDao implements VisitRecordDao {
	final static String VisitRecordSql="select vr_recorder,vr_visittime from vr_id=?";
	final static String VisitRecordDetailSql="select vr_recorder,vr_visittime from vr_id=?";

	@Override
	public void turnReport(int vr_id, String language, Employee employee) {
		// TODO Auto-generated method stub
		
	}
}
