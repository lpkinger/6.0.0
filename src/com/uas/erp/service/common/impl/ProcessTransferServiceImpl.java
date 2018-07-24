package com.uas.erp.service.common.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.common.ProcessTransferService;
@Service
public class ProcessTransferServiceImpl  implements ProcessTransferService{
	@Autowired
	private BaseDao  baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveProcessTransfer(String formStore, String language,Employee employee) {
		// TODO Auto-generated method stub
		handlerService.handler("JProcessTransfer", "save", "before",new Object[]{formStore,language,employee});
		Map<Object, Object> map=BaseUtil.parseFormStoreToMap(formStore);
		baseDao.execute(SqlUtil.getInsertSqlByMap(map, "JProcessTransfer"));
		try{
			//记录操作
			baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.save", language), 
					BaseUtil.getLocalMessage("msg.saveSuccess", language), "JProcessTransfer|jt_id=" + map.get("jt_id")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.handler("JProcessTransfer","save","after", new Object[]{map,language,employee});
	}
	@Override
	public void deleteProcessTransfer(int id, String language, Employee employee) {
		// TODO Auto-generated method stub
		handlerService.handler("JProcessTransfer", "delete", "before",new Object[]{id,language,employee});
	    baseDao.deleteById("JProcessTransfer", "jt_id", id);
		try{
			//记录操作
			baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.save", language), 
					BaseUtil.getLocalMessage("msg.saveSuccess", language), "JProcessTransfer|jt_id=" +id));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.handler("JProcessTransfer","save","after", new Object[]{id,language,employee});
	}

	@Override
	public void updateProcessTransfer(String formStore, String language,Employee employee) {
		// TODO Auto-generated method stub
		handlerService.handler("JProcessTransfer", "update", "before",new Object[]{formStore,language,employee});
		Map<Object,Object> store=BaseUtil.parseFormStoreToMap(formStore);
	    baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store,"JProcessTransfer", "jt_id"));
		try{
			//记录操作
			baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.save", language), 
					BaseUtil.getLocalMessage("msg.saveSuccess", language), "JProcessTransfer|jt_id=" +store.get("jt_id")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.handler("JProcessTransfer","save","after", new Object[]{store.get("jt_id"),language,employee});
	}

	@Override
	public void disabledProcessTransfer(int id, String language,Employee employee) {
		// TODO Auto-generated method stub	
		//禁用操作
		// 执行禁用前的其它逻辑
		handlerService.handler("JProcessTransfer", "banned", "before", new Object[] { id, language, employee });
		baseDao.updateByCondition("JProcessTransfer", "jt_statuscode='VALID',jt_status='" + 
				BaseUtil.getLocalMessage("VALID", language) + "'", "jt_id=" + id);
		handlerService.handler("JProcessTransfer", "banned", "after", new Object[] { id, language, employee });
	}

	@Override
	public void abledProcessTransfer(int id, String language, Employee employee) {
		// TODO Auto-generated method stub
        //启用操作
		// 执行禁用前的其它逻辑
		handlerService.handler("JProcessTransfer", "resBanned", "before", new Object[] { id, language, employee });
		baseDao.updateByCondition("JProcessTransfer", "jt_statuscode='UNVALID',jt_status='" + 
				BaseUtil.getLocalMessage("UNVALID", language) + "'", "jt_id=" + id);
		handlerService.handler("JProcessTransfer", "resBanned", "after", new Object[] { id, language, employee });
	}

}
