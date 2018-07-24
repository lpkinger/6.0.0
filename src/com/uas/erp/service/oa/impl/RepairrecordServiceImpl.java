package com.uas.erp.service.oa.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.HandlerService;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.RepairrecordService;

@Service
public class RepairrecordServiceImpl implements RepairrecordService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void deleteRepairrecord(int rr_id, String caller) {
		
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{rr_id});
		//删除purchase
		baseDao.deleteById("Repairrecord", "rr_id", rr_id);
		//记录操作
		baseDao.logger.delete(caller, "rr_id", rr_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{rr_id});
	}

}
