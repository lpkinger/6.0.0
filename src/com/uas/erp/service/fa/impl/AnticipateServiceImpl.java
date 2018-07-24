package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.fa.AnticipateService;

@Service("anticipateService")
public class AnticipateServiceImpl implements AnticipateService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private VoucherDao voucherDao;

	@Override
	public void createAnticipate(String date, String cucode, String emcode, String dpcode) {
		if (date == null) {
			BaseUtil.showError("请选定截止日期！");
		}
		Employee employee = SystemSession.getUser();
		cucode = cucode == "" ? null : cucode;
		emcode = emcode == "" ? null : emcode;
		dpcode = dpcode == "" ? null : dpcode;
		cucode = cucode == "" ? null : cucode;
		String res = baseDao.callProcedure("SP_CREATEANTICIPATE", new Object[] { date, cucode, emcode, dpcode, employee.getEm_id() });
		if (res.equals("OK")) {

		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public void saveAnticipate(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		String code = store.get("an_code").toString();
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Anticipate", "an_code='" + code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		checkDate(store.get("an_date").toString());
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存Anticipate
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "Anticipate"));
		baseDao.execute(SqlUtil.getInsertSqlbyList(grid, "AnticipateDetail", "and_id"));
		getTotal(store.get("an_id"));
		// 记录操作
		baseDao.logger.save(caller, "an_id", store.get("an_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	/**
	 * 单据日期是否超期
	 */
	private void checkDate(String date) {
		int yearmonth = voucherDao.getPeriodsFromDate("Month-C", date);
		int nowym = voucherDao.getNowPddetno("Month-C");// 当前期间
		if (yearmonth < nowym) {
			BaseUtil.showError("期间" + yearmonth + "已经结转,当前账期在:" + nowym + "<br>请修改日期，或反结转应收账.");
		}
	}

	@Override
	public void updateAnticipateById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("Anticipate", "an_statuscode", "an_id=" + store.get("an_id"));
		StateAssert.updateOnlyEntering(status);
		checkDate(store.get("an_date").toString());
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改Anticipate
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "Anticipate", "an_id"));
		// 修改AnticipateDetail
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "AnticipateDetail", "and_id"));
		getTotal(store.get("an_id"));
		// 记录操作
		baseDao.logger.update(caller, "an_id", store.get("an_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteAnticipate(int an_id, String caller) {
		// 只能删除在录入的采购单!
		Object status = baseDao.getFieldDataByCondition("Anticipate", "an_statuscode", "an_id=" + an_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, an_id);
		// 删除Anticipate
		baseDao.deleteById("Anticipate", "an_id", an_id);
		// 删除AnticipateDetail
		baseDao.deleteById("Anticipatedetail", "and_anid", an_id);
		// 记录操作
		baseDao.logger.delete(caller, "an_id", an_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, an_id);
	}

	@Override
	public void auditAnticipate(int an_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Anticipate", "an_statuscode", "an_id=" + an_id);
		StateAssert.auditOnlyCommited(status);
		getTotal(an_id);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, an_id);
		// 执行审核操作
		baseDao.audit("Anticipate", "an_id=" + an_id, "an_status", "an_statuscode", "an_auditdate", "an_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "an_id", an_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, an_id);
	}

	@Override
	public void resAuditAnticipate(int an_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Anticipate", "an_statuscode", "an_id=" + an_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, an_id);
		// 执行反审核操作
		baseDao.resAudit("Anticipate", "an_id=" + an_id, "an_status", "an_statuscode", "an_auditdate", "an_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "an_id", an_id);
		handlerService.afterResAudit(caller, an_id);
	}

	@Override
	public void submitAnticipate(int an_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Anticipate", "an_statuscode", "an_id=" + an_id);
		StateAssert.submitOnlyEntering(status);
		getTotal(an_id);
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(an_code) from Anticipate where an_id=? and nvl(an_type,' ')='内部催收' and an_backdate is null",
				String.class, an_id);
		if (dets != null) {
			BaseUtil.showError("预计回款时间未填写，不能提交！");
		}
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(an_code) from Anticipate where an_id=? and nvl(an_type,' ')='内部催收' and nvl(an_backamount,0)=0",
				String.class, an_id);
		if (dets != null) {
			BaseUtil.showError("预计回款金额未填写，不能提交！");
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, an_id);
		// 执行提交操作
		baseDao.submit("Anticipate", "an_id=" + an_id, "an_status", "an_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "an_id", an_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, an_id);
	}

	@Override
	public void resSubmitAnticipate(int an_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Anticipate", "an_statuscode", "an_id=" + an_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, an_id);
		// 执行反提交操作
		baseDao.resOperate("Anticipate", "an_id=" + an_id, "an_status", "an_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "an_id", an_id);
		handlerService.afterResSubmit(caller, an_id);
	}

	@Override
	public String[] printAnticipate(int an_id, String reportName, String condition, String caller) {
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, an_id);
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 记录操作
		baseDao.logger.print(caller, "an_id", an_id);
		// 记录打印次数
		baseDao.updateByCondition("Anticipate", "an_count=nvl(an_count,0)+1", "an_id=" + an_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, an_id);
		return keys;
	}

	@Override
	public void refreshAnticipateBack(String caller, String from, String to) {
		if (from == null) {
			BaseUtil.showError("请选定起始日期！");
		}
		if (to == null) {
			BaseUtil.showError("请选定截止日期！");
		}
		// 刷新催收后实际回款金额
		String res = baseDao.callProcedure("FA_REFRESHANTICIPATE", new Object[] { from, to });
		if (res == null || res.trim().equals("ok")) {

		} else {
			BaseUtil.showError(res);
		}
	}

	private void getTotal(Object an_id) {
		baseDao.execute("update Anticipate set an_amount=round(nvl((select sum(nvl(and_amount,0)) from AnticipateDetail where and_anid=an_id),0),2) where an_id="
				+ an_id);
		baseDao.execute("update Anticipate set an_nowamount=round(nvl((select sum(nvl(and_nowamount,0)) from AnticipateDetail where and_anid=an_id),0),2) where an_id="
				+ an_id);
		baseDao.execute("update Anticipate set an_amount1=round(nvl((select sum(nvl(and_amount1,0)) from AnticipateDetail where and_anid=an_id),0),2) where an_id="
				+ an_id);
		baseDao.execute("update Anticipate set an_amount2=round(nvl((select sum(nvl(and_amount2,0)) from AnticipateDetail where and_anid=an_id),0),2) where an_id="
				+ an_id);
		baseDao.execute("update Anticipate set an_amount3=round(nvl((select sum(nvl(and_amount3,0)) from AnticipateDetail where and_anid=an_id),0),2) where an_id="
				+ an_id);
		baseDao.execute("update Anticipate set an_amount7=round(nvl((select sum(nvl(and_amount7,0)) from AnticipateDetail where and_anid=an_id),0),2) where an_id="
				+ an_id);
		baseDao.execute("update Anticipate set an_amount12=round(nvl((select sum(nvl(and_amount12,0)) from AnticipateDetail where and_anid=an_id),0),2) where an_id="
				+ an_id);
		baseDao.execute("update Anticipate set an_actbackamount=round(nvl((select sum(nvl(and_actbackamount,0)) from AnticipateDetail where and_anid=an_id),0),2) where an_id="
				+ an_id);
	}
}
