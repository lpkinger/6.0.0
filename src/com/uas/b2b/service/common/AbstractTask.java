package com.uas.b2b.service.common;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EnterpriseService;

public abstract class AbstractTask {

	protected static final Logger logger = Logger.getLogger("SchedualTask");

	@Autowired
	protected BaseDao baseDao;

	@Autowired
	protected EnterpriseService enterpriseService;

	protected List<Master> getMasters() {
		return enterpriseService.getMasters();
	}

	/**
	 * 轮询方法
	 */
	public void execute() {
		logger.info(this.getClass() + " start");
		String sob = SpObserver.getSp();
		for (Master master : getMasters()) {
			if (master.b2bEnable()) {
				SpObserver.putSp(master.getMa_name());
				logger.info(this.getClass() + " switch master " + master.getMa_name());
				onExecute(master);
			}
		}
		SpObserver.putSp(sob);
		logger.info(this.getClass() + " end");
	}

	protected abstract void onExecute(Master master);

}
