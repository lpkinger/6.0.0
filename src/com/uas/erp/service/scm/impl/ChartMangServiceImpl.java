package com.uas.erp.service.scm.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.Assert;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.ChartMangService;

@Service
public class ChartMangServiceImpl implements ChartMangService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveChartMang(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行保存前的其它逻辑
		handlerService.handler("ChartMang", "save", "before", new Object[]{store});
		String ct_type = store.get("ct_type").toString();
		Object[] o = baseDao.getFieldsDataByCondition("CHARTMANGMAX", new String[]{"cm_leadcode","cm_max"}, "cm_type='"+ct_type+"'");
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String ma = o[1].toString().length()==1?"0"+o[1].toString():o[1].toString();
		String maxcode = o[0].toString()+"-"+format.format(date)+"-"+ma;
		store.put("ct_detail", maxcode);
		baseDao.updateByCondition("CHARTMANGMAX", "cm_max=cm_max+1", "cm_type='"+ct_type+"'");
		//执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ChartMang", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save("ChartMang", "ct_id", store.get("ct_id"));
		//执行保存后的其它逻辑
		handlerService.handler("ChartMang", "save", "after", new Object[]{store});
		
	}

	@Override
	public void updateChartMang(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("ChartMang", "ct_statuscode", "ct_id=" + store.get("ct_id"));
		Assert.isEquals("common.update_onlyEntering", "ENTERING", status);
		//执行修改前的其它逻辑
		handlerService.handler("ChartMang", "save", "before", new Object[]{store});
		//修改AskLeave
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ChartMang", "ct_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update("ChartMang", "ct_id", store.get("ct_id"));
		//执行修改后的其它逻辑
		handlerService.handler("ChartMang", "save", "after", new Object[]{formStore});
	}

	@Override
	public void deleteChartMang(int ct_id) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("ChartMang", "ct_statuscode", "ct_id=" + ct_id);
		Assert.isEquals("common.delete_onlyEntering", "ENTERING", status);
		//执行删除前的其它逻辑
		handlerService.handler("ChartMang", "delete", "before", new Object[]{ct_id});
		//删除
		baseDao.deleteById("ChartMang", "ct_id", ct_id);
		//记录操作
		baseDao.logger.delete("ChartMang", "ct_id", ct_id);
		//执行删除后的其它逻辑
		handlerService.handler("ChartMang", "delete", "after", new Object[]{ct_id});
	}

	@Override
	public void auditChartMang(int ct_id) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ChartMang", "ct_statuscode", "ct_id=" + ct_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit("ChartMang", ct_id);
		//执行审核操作
		baseDao.audit("ChartMang", "ct_id=" + ct_id, "ct_status", "ct_statuscode", "ct_auditdate", "ct_auditman");
		//记录操作
		baseDao.logger.audit("ChartMang", "ct_id", ct_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit("ChartMang", ct_id);
	}

	@Override
	public void resAuditChartMang(int ct_id) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ChartMang", "ct_statuscode", "ct_id=" + ct_id);
		Assert.isEquals("common.resAudit_onlyAudit", "AUDITED", status);
		//执行反审核操作
		baseDao.resOperate("ChartMang", "ct_id=" + ct_id, "ct_status", "ct_statuscode");
		//记录操作
		baseDao.logger.resAudit("ChartMang", "ct_id", ct_id);
	}

	@Override
	public void submitChartMang(int ct_id) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ChartMang", "ct_statuscode", "ct_id=" + ct_id);
		Assert.isEquals("common.submit_onlyEntering", "ENTERING", status);
		//执行提交前的其它逻辑
		handlerService.handler("ChartMang", "commit", "before", new Object[]{ct_id});
		//执行提交操作
		baseDao.submit("ChartMang", "ct_id=" + ct_id, "ct_status", "ct_statuscode");
		//记录操作
		baseDao.logger.submit("ChartMang", "ct_id", ct_id);
		//执行提交后的其它逻辑
		handlerService.handler("ChartMang", "commit", "after", new Object[]{ct_id});
	}

	@Override
	public void resSubmitChartMang(int ct_id) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ChartMang", "ct_statuscode", "ct_id=" + ct_id);
		Assert.isEquals("common.resSubmit_onlyCommited", "COMMITED", status);
		//执行反提交操作
		baseDao.resOperate("ChartMang", "ct_id=" + ct_id, "ct_status", "ct_statuscode");
		//记录操作
		baseDao.logger.resSubmit("ChartMang", "ct_id", ct_id);
	}
}
