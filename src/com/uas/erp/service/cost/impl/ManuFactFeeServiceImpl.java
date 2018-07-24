package com.uas.erp.service.cost.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.cost.ManuFactFeeService;

@Service("ManuFactFeeService")
public class ManuFactFeeServiceImpl implements ManuFactFeeService{
	
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void saveManuFactFee(String formStore, String language, Employee employee) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的供应商资料已经存在,不能新增
		boolean bool = baseDao.checkByCondition("ManuFactFee", "mf_code='" + store.get("mf_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist", language));
		}
		checkCloseMonth("MONTH-T", store.get("mf_yearmonth"));
		//执行保存前的其它逻辑
		handlerService.handler("ManuFactFee", "save", "before", new Object[]{store, language});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ManuFactFee", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.save", language), 
				BaseUtil.getLocalMessage("msg.saveSuccess", language), "ManuFactFee|mf_id=" + store.get("mf_id")));
		//执行保存后的其它逻辑
		handlerService.handler("ManuFactFee", "save", "after", new Object[]{store, language});
	}

	public void checkCloseMonth(String type, Object yearmonth) {
		boolean bool =baseDao.checkIf("PeriodsDetail",
				"pd_code='"+type+"' and pd_status=99 and pd_detno=" + yearmonth);
		if (bool) {
			BaseUtil.showError("单据所属期间已结账，不允许进行当前操作!");
		}
	}
	
	@Override
	public void updateManuFactFee(String formStore, String language, Employee employee) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("ManuFactFee", "mf_statuscode", "mf_id=" + store.get("mf_id"));
		if(!status.equals("ENTERING")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering", language));
		}
		checkCloseMonth("MONTH-T", store.get("mf_yearmonth"));
		//执行修改前的其它逻辑
		handlerService.handler("ManuFactFee", "save", "before", new Object[]{store, language});
		String sql = SqlUtil.getUpdateSqlByFormStore(store, "ManuFactFee", "mf_id");
		baseDao.execute(sql);
		//记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.update", language), 
				BaseUtil.getLocalMessage("msg.updateSuccess", language), "ManuFactFee|mf_id=" + store.get("mf_id")));
		//执行修改后的其它逻辑
		handlerService.handler("ManuFactFee", "save", "after", new Object[]{store, language});
	}

	@Override
	public void deleteManuFactFee(int mf_id, String language, Employee employee) {
		//只能删除[在录入]的供应商资料
		Object[] status = baseDao.getFieldsDataByCondition("ManuFactFee", new String[]{"mf_statuscode", "mf_yearmonth"}, "mf_id=" + mf_id);
		StateAssert.delOnlyEntering(status[0]);
		checkCloseMonth("MONTH-T", status[1]);
		//执行删除前的其它逻辑
		handlerService.handler("ManuFactFee", "delete", "before", new Object[]{mf_id, language, employee});
		//执行删除操作
		baseDao.deleteById("ManuFactFee", "mf_id", mf_id);
		//记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.delete", language), 
				BaseUtil.getLocalMessage("msg.deleteSuccess", language), "ManuFactFee|mf_id=" + mf_id));
		//执行删除后的其它逻辑
		handlerService.handler("ManuFactFee", "delete", "after", new Object[]{mf_id, language, employee});
	}

	@Override
	public void auditManuFactFee(int mf_id, String language, Employee employee) {
		//只能审核[已提交]的供应商
		Object[] status = baseDao.getFieldsDataByCondition("ManuFactFee", new String[]{"mf_statuscode", "mf_yearmonth"}, "mf_id=" + mf_id);
		StateAssert.auditOnlyCommited(status[0]);
		checkCloseMonth("MONTH-T", status[1]);
		//执行审核前的其它逻辑
		handlerService.handler("ManuFactFee", "audit", "before", new Object[]{mf_id, language});
		//执行审核操作
		baseDao.updateByCondition("ManuFactFee", "mf_statuscode='AUDITED',mf_status='" + 
				BaseUtil.getLocalMessage("AUDITED", language) + "'", "mf_id=" + mf_id);
		//记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.audit", language), 
				BaseUtil.getLocalMessage("msg.auditSuccess", language), "ManuFactFee|mf_id=" + mf_id));
		//执行审核后的其它逻辑
		handlerService.handler("ManuFactFee", "audit", "after", new Object[]{mf_id, language});
	}

	@Override
	public void resAuditManuFactFee(int mf_id, String language, Employee employee) {
		//只能反审核[已审核]的供应商
		Object[] status = baseDao.getFieldsDataByCondition("ManuFactFee", new String[]{"mf_statuscode", "mf_yearmonth"}, "mf_id=" + mf_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		checkCloseMonth("MONTH-T", status[1]);
		//执行反审核操作
		baseDao.updateByCondition("ManuFactFee", "mf_statuscode='ENTERING',mf_status='" + 
				BaseUtil.getLocalMessage("ENTERING", language) + "'", "mf_id=" + mf_id);
		//记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.resAudit", language), 
				BaseUtil.getLocalMessage("msg.resAuditSuccess", language), "ManuFactFee|mf_id=" + mf_id));
	}

	@Override
	public void submitManuFactFee(int mf_id, String language, Employee employee) {
		//只能提交[在录入]的供应商
		Object[] status = baseDao.getFieldsDataByCondition("ManuFactFee", new String[]{"mf_statuscode", "mf_yearmonth"}, "mf_id=" + mf_id);
		StateAssert.submitOnlyEntering(status[0]);
		checkCloseMonth("MONTH-T", status[1]);
		//执行提交前的其它逻辑
		handlerService.handler("ManuFactFee", "commit", "before", new Object[]{mf_id, language, employee});
		//执行提交操作
		baseDao.updateByCondition("ManuFactFee", "mf_statuscode='COMMITED', mf_status='" + 
				BaseUtil.getLocalMessage("COMMITED", language) + "'", "mf_id=" + mf_id);
		//记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.submit", language), 
				BaseUtil.getLocalMessage("msg.submitSuccess", language), "ManuFactFee|mf_id=" + mf_id));
		//执行提交后的其它逻辑
		handlerService.handler("ManuFactFee", "commit", "after", new Object[]{mf_id, language, employee});
	}

	@Override
	public void resSubmitManuFactFee(int mf_id, String language, Employee employee) {
		//只能对状态为[已提交]的单据进行反提交操作
		Object[] status = baseDao.getFieldsDataByCondition("ManuFactFee", new String[]{"mf_statuscode", "mf_yearmonth"}, "mf_id=" + mf_id);
		StateAssert.resSubmitOnlyCommited(status[0]);
		checkCloseMonth("MONTH-T", status[1]);
		//执行反提交操作
		baseDao.updateByCondition("ManuFactFee", "mf_statuscode='ENTERING', mf_status='" + 
				BaseUtil.getLocalMessage("ENTERING", language) + "'", "mf_id=" + mf_id);
		//记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.resSubmit", language), 
				BaseUtil.getLocalMessage("msg.resSubmitSuccess", language), "ManuFactFee|mf_id=" + mf_id));
	}
}
