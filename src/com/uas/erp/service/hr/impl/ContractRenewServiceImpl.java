package com.uas.erp.service.hr.impl;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.HandlerService;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.ContractRenewService;

@Service
public class ContractRenewServiceImpl implements ContractRenewService {
	@Autowired
	private HandlerService handlerService;

	@Autowired
	private BaseDao baseDao;
	@Transactional
	@Override
	public void audit(int co_id, String  caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ContractRenew",
				"co_statuscode", "co_id=" + co_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] {co_id});
		//反应到人员合同表中Contract
		Object[] data=baseDao.getFieldsDataByCondition("ContractRenew", "co_title,co_depart,co_company,co_manager,co_connecter,co_phone,co_address,co_contractor,co_sex,co_card,co_conadd,co_conphone,co_conclass,co_begintime," +
				"co_endtime,co_probation,co_position,co_workaddress,co_salary,co_contratime,co_contractorcode,co_cocode", "co_id="+co_id);
		//处理date类型字段
		if(data[13]!=null){
			data[13]=Timestamp.valueOf(data[13].toString());
		}
		if(data[14]!=null){
			data[14]=Timestamp.valueOf(data[14].toString());
		}
		if(data[19]!=null){
			data[19]=Timestamp.valueOf(data[19].toString());
		}
		String updateSql="update Contract set co_title=?,co_depart=?,co_company=?,co_manager=?,co_connecter=?,co_phone=?,co_address=?,co_contractor=?,co_sex=?,co_card=?,co_conadd=?,co_conphone=?,co_conclass=?,co_begintime=?," +
				"co_endtime=?,co_probation=?,co_position=?,co_workaddress=?,co_salary=?,co_contratime=?,co_contractorcode=? where co_code=?";
		baseDao.execute(updateSql, data);
		// 执行审核操作
		baseDao.audit("ContractRenew", "co_id=" + co_id, "co_status", "co_statuscode", "co_auditdate", "co_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "co_id", co_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] {co_id});
	}

}
