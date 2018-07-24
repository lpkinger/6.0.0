package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.PaymentsPurchaseDetailService;

@Service("paymentsPurchaseDetailService")
public class PaymentsPurchaseDetailServiceImpl implements PaymentsPurchaseDetailService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	@Override
	public void savePayments(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Payments", "pa_code='" + store.get("pa_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store, grid});
		//保存Payments
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Payments", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		////保存PaymentsDetail
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "PaymentsDetail", "pad_id");
		baseDao.execute(gridSql);
		//多账期付款方式的定义中, 保存时, 如果比率加起来不等于100, 则提示, 但给保存,不给审核
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		int count = 0;
		for(Map<Object, Object> map:gstore){
			String rate = map.get("pad_rate").toString();
			if(rate != null){
				count += Integer.parseInt(rate.replace("%", ""));
			}
		}
		if(count != 100){
			BaseUtil.showError("AFTERSUCCESS" + BaseUtil.getLocalMessage("purcPaymentsDetail_rate"));
		}
		baseDao.logger.save(caller, "pa_id", store.get("pa_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store, grid});
	}
	@Override
	public void deletePayments(int pa_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("Payments", "pa_auditstatuscode", "pa_id=" + pa_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, pa_id);
		//删除Payments
		baseDao.deleteById("Payments", "pa_id", pa_id);
		//删除PaymentsDetail
		baseDao.deleteById("paymentsdetail", "pad_paid", pa_id);
		//记录操作
		baseDao.logger.delete(caller, "pa_id", pa_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, pa_id);
	}
	
	@Override
	public void updatePaymentsById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);	
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("Payments", "pa_auditstatuscode", "pa_id=" + store.get("pa_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store, gstore});
		//修改Payments
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Payments", "pa_id");
		baseDao.execute(formSql);
		//修改PaymentsDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "PaymentsDetail", "pad_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("pad_id") == null || s.get("pad_id").equals("") || s.get("pad_id").equals("0") ||
					Integer.parseInt(s.get("pad_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("PAYMENTSDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "PaymentsDetail", new String[]{"pad_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//多账期付款方式的定义中, 保存时, 如果比率加起来不等于100, 则提示, 但给保存,不给审核
		int count = 0;
		for(Map<Object, Object> map:gstore){
			String rate = map.get("pad_rate").toString();
			if(rate != null){
				count += Integer.parseInt(rate.replace("%", ""));
			}
		}
		if(count != 100){
			BaseUtil.showError("AFTERSUCCESS" + BaseUtil.getLocalMessage("purcPaymentsDetail_rate"));
		}
		baseDao.logger.update(caller, "pa_id", store.get("pa_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store, gstore});
	}
	@Override
	public void auditPayments(int pa_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Payments", "pa_auditstatuscode", "pa_id=" + pa_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, pa_id);
		//执行审核操作
		baseDao.audit("Payments", "pa_id=" + pa_id, "PA_AUDITSTATUS", "pa_auditstatuscode");
		//记录操作
		baseDao.logger.audit(caller, "pa_id", pa_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, pa_id);
	}
	
	@Override
	public void resAuditPayments(int pa_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Payments", "pa_auditstatuscode", "pa_id=" + pa_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("Payments", "pa_id=" + pa_id, "PA_AUDITSTATUS", "pa_auditstatuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "pa_id", pa_id);
	}
	@Override
	public void submitPayments(int pa_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Payments", "pa_auditstatuscode", "pa_id=" + pa_id);
		StateAssert.submitOnlyEntering(status);
		//多账期付款方式的定义中, 保存时, 如果比率加起来不等于100, 则提示, 但给保存,不给审核
		List<Object> objs = baseDao.getFieldDatasByCondition("PaymentsDetail", "pad_rate", "pad_paid=" + pa_id);
		int count = 0;
		for(Object obj:objs){
			String rate = obj.toString();
			if(rate != null){
				count += Integer.parseInt(rate.replace("%", ""));
			}
		}
		if(count != 100){
			BaseUtil.showError(BaseUtil.getLocalMessage("purcPaymentsDetail_rate"));
		}
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, pa_id);
		//执行提交操作
		baseDao.submit("Payments", "pa_id=" + pa_id, "PA_AUDITSTATUS", "pa_auditstatuscode");
		//记录操作
		baseDao.logger.submit(caller, "pa_id", pa_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, pa_id);
	}
	
	@Override
	public void resSubmitPayments(int pa_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Payments", "pa_auditstatuscode", "pa_id=" + pa_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeSubmit(caller, pa_id);
		//执行反提交操作
		baseDao.resOperate("Payments", "pa_id=" + pa_id, "PA_AUDITSTATUS", "pa_auditstatuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "pa_id", pa_id);
		handlerService.afterResSubmit(caller, pa_id);
	}
	
	@Override
	public void bannedPayments(int pa_id, String caller) {
		//执行禁用操作
		baseDao.banned("Payments", "pa_id=" + pa_id, "PA_AUDITSTATUS", "pa_auditstatuscode");
		//记录操作
		baseDao.logger.banned(caller, "pa_id", pa_id);
	}

	@Override
	public void resBannedPayments(int pa_id, String caller) {
		//执行反禁用操作
		baseDao.resOperate("Payments", "pa_id=" + pa_id, "PA_AUDITSTATUS", "pa_auditstatuscode");
		//记录操作
		baseDao.logger.resBanned(caller, "pa_id", pa_id);
	}
}
