package com.uas.erp.service.pm.impl;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.service.pm.MRPOptionService;
@Service("mRPOptionService")
public class MRPOptionServiceImpl implements MRPOptionService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveMRPOption(String formStore, String caller) {
		String fields=baseDao.getformFieldsbyTable("MRPOption");
		String str[]=fields.split(",");
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
          for(int i=0;i<str.length;i++){
		    if(store.get(str[i])==null){
		    	store.put(str[i],0);
		    }
		}
          handlerService.beforeSave(caller, new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "MRPOption", new String[]{}, new Object[]{});
	
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "mo_id", store.get("mo_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}
	
	@Override
	public void deleteMRPOption(int mo_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("MRPOption", "mo_statuscode", "mo_id=" + mo_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{mo_id});
		//删除MRPOption
		baseDao.deleteById("MRPOption", "mo_id", mo_id);		
		//记录操作
		baseDao.logger.delete(caller, "mo_id", mo_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{mo_id});
	}
	
	@Override
	public void updateMRPOption(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("MRPOption", "mo_statuscode", "mo_id=" + store.get("mo_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller,new Object[]{store});
		//修改
		String fields=baseDao.getformFieldsbyTable("MRPOption");
		String str[]=fields.split(",");
          for(int i=0;i<str.length;i++){
		    if(store.get(str[i])==null){
		    	store.put(str[i],0);
		    }
		}
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "MRPOption", "mo_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "mo_id", store.get("mo_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller,new Object[]{store});
	}

	@Override
	public void auditMRPOption(int mo_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("MRPOption", "mo_statuscode", "mo_id=" + mo_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller,new Object[]{mo_id});
		//执行审核操作
		baseDao.audit("MRPOption", "mo_id=" + mo_id, "mo_status", "mo_statuscode", "mo_auditdate", "mo_auditman");
		//记录操作
		baseDao.logger.audit(caller, "mo_id", mo_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller,new Object[]{mo_id});
	}
	
	@Override
	public void resAuditMRPOption(int mo_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("MRPOption", "mo_statuscode", "mo_id=" + mo_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("MRPOption", "mo_id=" + mo_id, "mo_status", "mo_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "mo_id", mo_id);
	}
	
	@Override
	public void submitMRPOption(int mo_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MRPOption", "mo_statuscode", "mo_id=" + mo_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{mo_id});
		//执行提交操作
		baseDao.submit("MRPOption", "mo_id=" + mo_id, "mo_status", "mo_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "mo_id", mo_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{mo_id});
	}
	
	@Override
	public void resSubmitMRPOption(int mo_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MRPOption", "mo_statuscode", "mo_id=" + mo_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller,  new Object[]{mo_id});
		//执行反提交操作
		baseDao.resOperate("MRPOption", "mo_id=" + mo_id, "mo_status", "mo_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "mo_id", mo_id);
		handlerService.afterResSubmit(caller,  new Object[]{mo_id});
	}	
}
