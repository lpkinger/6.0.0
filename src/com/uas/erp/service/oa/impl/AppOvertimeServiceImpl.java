package com.uas.erp.service.oa.impl;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.AppOvertimeService;

@Service
public class AppOvertimeServiceImpl implements AppOvertimeService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveAppOvertime(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "AppOvertime", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save(caller, "ao_id", store.get("ao_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
		
	}

	@Override
	public void updateAppOvertime(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,new Object[]{store});
		//修改AppOvertime
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "AppOvertime", "ao_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "ao_id", store.get("ao_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[]{store});
		
	}

	@Override
	public void deleteAppOvertime(int ao_id, String caller) {
		//执行删除前的其它逻辑
				handlerService.beforeDel(caller,new Object[]{ao_id});
				//删除purchase
				baseDao.deleteById("AppOvertime", "ao_id", ao_id);
				//记录操作
				baseDao.logger.delete(caller, "ao_id", ao_id);
				//执行删除后的其它逻辑
				handlerService.afterDel(caller, new Object[]{ao_id});
		
	}

	@Override
	public void auditAppOvertime(int ao_id, String caller) {
		//执行审核前的其它逻辑
		        handlerService.beforeAudit(caller,new Object[]{ao_id});	
				//执行审核操作
				baseDao.audit("AppOvertime", "ao_id=" + ao_id, "ao_status", "ao_statuscode", "ao_auditdate", "ao_auditman");
				//记录操作
				baseDao.logger.audit(caller, "ao_id", ao_id);
				//执行审核后的其它逻辑
				handlerService.afterAudit(caller,new Object[]{ao_id});		
	}

	@Override
	public void resAuditAppOvertime(int ao_id, String caller) {
		//执行反审核操作
				baseDao.resOperate("AppOvertime", "ao_id=" + ao_id, "ao_status", "ao_statuscode");
				//记录操作
				baseDao.logger.resAudit(caller, "ao_id", ao_id);		
	}

	@Override
	public void submitAppOvertime(int ao_id, String caller) {
		//执行提交前的其它逻辑
		        handlerService.beforeSubmit(caller, new Object[]{ao_id});
				//执行提交操作
				baseDao.submit("AppOvertime", "ao_id=" + ao_id, "ao_status", "ao_statuscode");
				//记录操作
				baseDao.logger.submit(caller, "ao_id", ao_id);
				//执行提交后的其它逻辑
				handlerService.afterSubmit(caller, new Object[]{ao_id});		
	}

	@Override
	public void resSubmitAppOvertime(int ao_id, String caller) {
		//执行反提交操作
				baseDao.resOperate("AppOvertime", "ao_id=" + ao_id, "ao_status", "ao_statuscode");
				//记录操作
				baseDao.logger.resSubmit(caller, "ao_id", ao_id);
		
	}

}
