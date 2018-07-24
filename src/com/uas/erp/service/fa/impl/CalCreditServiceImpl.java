package com.uas.erp.service.fa.impl;

import org.drools.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.HandlerService;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.fa.CalCreditService;

@Service
public class CalCreditServiceImpl implements CalCreditService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private EnterpriseService enterpriseService;

	@Override
	public void RefreshCredit(String param) {
		Master master = SystemSession.getUser().getCurrentMaster();
		Master parentMaster = null;
		if (master != null && !StringUtils.isEmpty(master.getMa_soncode())) {// 父级账套
			parentMaster = master;
		} else if (null != master.getMa_pid() && master.getMa_pid() > 0) {
			parentMaster = enterpriseService.getMasterByID(master.getMa_pid());
		}
		if ("group".equals(baseDao.getDBSetting("creditMethod")) && null != parentMaster
				&& !StringUtils.isEmpty(parentMaster.getMa_soncode())) {
			baseDao.procedure("SP_CALCREDIT", new Object[] { parentMaster.getMa_user(), parentMaster.getMa_soncode(), param });
		} else {
			baseDao.procedure("SP_CALCREDIT", new Object[] { "", "", param });
		}
	}
}
