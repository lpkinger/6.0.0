package com.uas.erp.service.oa.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.SignDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Sign;
import com.uas.erp.service.oa.SignService;

@Service
public class SignServiceImpl implements SignService{
	@Autowired
	private SignDao signDao;
	@Autowired
	private BaseDao baseDao;
	@Override
	public Sign getMySign(Employee employee) {
		return signDao.getMySign(employee.getEm_code());
	}
	@Override
	public void signin(Employee employee, String reason) {
		Sign sign = new Sign();
		sign.setSi_id(baseDao.getSeqId("SIGN_SEQ"));
		sign.setSi_emcode(employee.getEm_code());
		sign.setSi_emid(employee.getEm_id());
		sign.setSi_emname(employee.getEm_name());
		sign.setSi_in(new Date());
		sign.setSi_inreason(reason);
		baseDao.save(sign);
	}
	@Override
	public void signout(Employee employee, String reason) {
		Sign sign = signDao.getMySign(employee.getEm_code());
		if(sign != null) {
			StringBuffer sb = new StringBuffer();
			sb.append("si_out=to_date('" + DateUtil.currentDateString(Constant.YMD_HMS) + "','yyyy-MM-dd HH24:mi:ss')");
			if(reason != null){
				sb.append(",si_outreason='" + reason + "'");
			}
			baseDao.updateByCondition("sign", sb.toString(), "si_id=" + sign.getSi_id());
		}
	}
	
}
