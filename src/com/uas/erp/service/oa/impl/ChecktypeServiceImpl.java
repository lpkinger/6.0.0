package com.uas.erp.service.oa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.oa.ChecktypeService;

@Service
public class ChecktypeServiceImpl implements ChecktypeService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveChecktype(String formStore, String language,
			Employee employee) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		
		handlerService.handler("Checktype", "save", "before", new Object[]{formStore, language});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Checktype", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logMessage(new MessageLog(employee.getEm_name(), 
					BaseUtil.getLocalMessage("msg.save", language), 
					BaseUtil.getLocalMessage("msg.saveSuccess", language), 
					"Checktype|ct_id=" + store.get("ct_id")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.handler("Checktype", "save", "after", new Object[]{formStore, language});

	}

	@Override
	public void updateChecktypeById(String formStore, String language,
			Employee employee) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.handler("Checktype", "save", "before", new Object[]{formStore, language});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Checktype", "ct_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.update", language), 
				BaseUtil.getLocalMessage("msg.updateSuccess", language), "Checktype|ct_id=" + store.get("ct_id")));
		//执行修改后的其它逻辑
		handlerService.handler("Checktype", "save", "after", new Object[]{formStore, language});


	}

	@Override
	public void deleteChecktype(int ct_id, String language, Employee employee) {
		
		handlerService.handler("Checktype", "delete", "before", new Object[]{ct_id, language, employee});
		//删除
		baseDao.deleteById("Checktype", "ct_id", ct_id);
		//记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), 
				BaseUtil.getLocalMessage("msg.delete", language), 
				BaseUtil.getLocalMessage("msg.deleteSuccess", 
						language), "Checktype|ct_id=" + ct_id));
		//执行删除后的其它逻辑
		handlerService.handler("Checktype", "delete", "after", new Object[]{ct_id, language, employee});

	}

}
