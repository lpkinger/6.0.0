package com.uas.erp.service.hr.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.SignCardService;
import com.uas.erp.service.oa.PagingReleaseService;

@Service
public class SignCardServiceImpl implements SignCardService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private PagingReleaseService pagingReleaseService;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveSignCard(String formStore, String caller) throws ParseException {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// sc_emnames,sc_emids是clob字段，特殊处理
		String sc_emnames = store.get("sc_emnames").toString();
		store.remove("sc_emnames");
		String sc_emids = store.get("sc_emids").toString();
		store.remove("sc_emids");
		store.put("sc_signtime", store.get("sc_signtime_date").toString()+store.get("sc_signtime_time").toString());
		store.remove("sc_signtime_date");
		store.remove("sc_signtime_time");
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "SignCard",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.saveClob("SignCard", "sc_emnames", sc_emnames,"sc_id=" + store.get("sc_id"));
		baseDao.saveClob("SignCard", "sc_emids", sc_emids,"sc_id=" + store.get("sc_id"));
		// 记录操作
		baseDao.logger.save(caller, "sc_id", store.get("sc_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });

	}

	@Override
	public void updateSignCard(String formStore, String caller) throws ParseException {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// sc_emnames,sc_emids是clob字段，特殊处理
		String sc_emnames = store.get("sc_emnames").toString();
		store.remove("sc_emnames");
		String sc_emids = store.get("sc_emids").toString();
		store.remove("sc_emids");
		store.put("sc_signtime", store.get("sc_signtime_date").toString()+store.get("sc_signtime_time").toString());
		store.remove("sc_signtime_date");
		store.remove("sc_signtime_time");
		// 修改SignCard
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "SignCard","sc_id");
		baseDao.execute(formSql);
		baseDao.saveClob("SignCard", "sc_emnames", sc_emnames,"sc_id=" + store.get("sc_id"));
		baseDao.saveClob("SignCard", "sc_emids", sc_emids,"sc_id=" + store.get("sc_id"));
		// 记录操作
		baseDao.logger.update(caller, "sc_id", store.get("sc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteSignCard(int sc_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { sc_id });
		// 删除
		baseDao.deleteById("SignCard", "sc_id", sc_id);
		baseDao.deleteById("SignCardDetail", "scd_scid", sc_id);
		// 记录操作
		baseDao.logger.delete(caller, "sc_id", sc_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { sc_id });
	}

	@Override
	public void auditSignCard(int sc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("SignCard","sc_statuscode", "sc_id=" + sc_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { sc_id });
		// 执行审核操作
		baseDao.audit("SignCard", "sc_id=" + sc_id, "sc_status","sc_statuscode","sc_auditdate", "sc_auditer");
		// 记录操作
		baseDao.logger.audit(caller, "sc_id", sc_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { sc_id });
	}

	@Override
	public void resAuditSignCard(int sc_id, String caller) {
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, new Object[] { sc_id});
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("SignCard","sc_statuscode", "sc_id=" + sc_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("SignCard", "sc_id=" + sc_id, "sc_status", "sc_statuscode","sc_auditdate", "sc_auditer");
		baseDao.deleteById("SignCardDetail", "scd_scid", sc_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "sc_id", sc_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, new Object[] { sc_id});	
	}

	@Override
	public void submitSignCard(int sc_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("SignCard","sc_statuscode", "sc_id=" + sc_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { sc_id });
		// 执行提交操作
		baseDao.submit("SignCard", "sc_id=" + sc_id, "sc_status","sc_statuscode");
		Object emids = baseDao.getFieldDataByCondition("SignCard","sc_emids", "sc_id=" + sc_id);
		insertAllEmps(emids, sc_id);
		// 记录操作
		baseDao.logger.submit(caller, "sc_id", sc_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { sc_id });
	}

	@Override
	public void resSubmitSignCard(int sc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("SignCard","sc_statuscode", "sc_id=" + sc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[]{sc_id});
		// 执行反提交操作
		baseDao.resOperate("SignCard", "sc_id=" + sc_id, "sc_status","sc_statuscode");
		baseDao.deleteById("SignCardDetail", "scd_scid", sc_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "sc_id", sc_id);
		handlerService.handler(caller, "resCommit", "after", new Object[]{sc_id});
	}
	/**
	 * 把员工插入到明细中
	 */
	private void insertAllEmps(Object emids,Object scid){
		int detno = 1;
		List<String> sqls = new ArrayList<String>();
		for (String emid : emids.toString().split(";")) {
			String sqldetail = "insert into SignCardDetail(scd_detno,scd_emid,scd_scid,scd_emcode,scd_emcardcode) select "+detno++ +",em_id,"+scid+",em_code,em_cardcode from employee where em_id="+emid;				
			sqls.add(sqldetail);
		}
		baseDao.execute(sqls);
	}

	@Override
	public void endSignCard(int sc_id, String caller) {
		// 执行禁用前的其它逻辑
		handlerService.handler(caller, "end", "before", new Object[] { sc_id });
		// 执行禁用操作
		baseDao.updateByCondition("SignCard", "sc_statuscode='FINISH',sc_status='" + BaseUtil.getLocalMessage("FINISH") + "'", "sc_id="
				+ sc_id);
		// 记录操作
		baseDao.logger.others("msg.end", "msg.endSuccess", caller, "sc_id", sc_id);
		// 执行禁用后的其它逻辑
		handlerService.handler(caller, "end", "after", new Object[] { sc_id });
	}

	@Override
	public void resEndSignCard(int sc_id, String caller) {
		// 只能对状态为[已结案]的订单进行反结案操作!
		Object status = baseDao.getFieldDataByCondition("SignCard", "sc_statuscode", "sc_id=" + sc_id);
		if (!status.equals("FINISH")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resEnd_onlyEnd"));
		}
		// 反结案
		baseDao.updateByCondition("SignCard", "sc_statuscode='AUDITED',sc_status='" + BaseUtil.getLocalMessage("AUDITED") + "'", "sc_id="
				+ sc_id);
		// 记录操作
		baseDao.logger.others("msg.resEnd", "msg.resEndSuccess", caller, "sc_id", sc_id);
	}
}
