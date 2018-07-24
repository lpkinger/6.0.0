package com.uas.erp.service.drp.impl;

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

import com.uas.erp.model.MessageLog;
import com.uas.erp.service.drp.SaleForecastAskService;

@Service("saleForecastServiceAsk")
public class SaleForecastAskServiceImpl implements SaleForecastAskService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveSaleForecastAsk(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("SaleForecastAsk", "sf_code='"
				+ store.get("sf_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存SaleForecastAsk
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"SaleForecastAsk", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存SaleForecastAskDetail
		Object[] sd_id = new Object[grid.size()];
		for (int i = 0; i < grid.size(); i++) {
			Map<Object, Object> map = grid.get(i);
			sd_id[i] = baseDao.getSeqId("SALEFORECASTASKDETAIL_SEQ");
			map.put("sd_id", sd_id[i]);
			map.put("sd_statuscode", "ENTERING");
			map.put("sd_status", BaseUtil.getLocalMessage("ENTERING"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"SaleForecastAskDetail");
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "sf_id", store.get("sf_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void deleteSaleForecastAsk(int sf_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("SaleForecastAsk",
				"sf_statuscode", "sf_id=" + sf_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, sf_id);
		// 删除SaleForecastAsk
		baseDao.deleteById("SaleForecastAsk", "sf_id", sf_id);
		// 删除SaleForecastAskDetail
		baseDao.deleteById("SaleForecastAskdetail", "sd_sfid", sf_id);
		// 记录操作
		baseDao.logger.delete(caller, "sf_id", sf_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, sf_id);
	}

	@Override
	public void updateSaleForecastAskById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("SaleForecastAsk",
				"sf_statuscode", "sf_id=" + store.get("sf_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改SaleForecastAsk
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"SaleForecastAsk", "sf_id");
		baseDao.execute(formSql);
		// 修改SaleForecastAskDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore,
				"SaleForecastAskDetail", "sd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("sd_id") == null || s.get("sd_id").equals("")
					|| s.get("sd_id").equals("0")
					|| Integer.parseInt(s.get("sd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("SaleForecastAskDETAIL_SEQ");
				s.put("sd_id", id);
				s.put("sd_statuscode", "ENTERING");
				s.put("sd_status", BaseUtil.getLocalMessage("ENTERING"));
				String sql = SqlUtil.getInsertSqlByMap(s,
						"SaleForecastAskDetail", new String[] { "sd_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "sf_id", store.get("sf_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public String[] printSaleForecastAsk(int sf_id, String caller,
			String reportName, String condition) {
		// 只能打印审核后的单据!
		Object status = baseDao.getFieldDataByCondition("SaleForecastAsk",
				"sf_statuscode", "sf_id=" + sf_id);
		if (!status.equals("AUDITED") && !status.equals("PARTRECEIVED")
				&& !status.equals("RECEIVED") && !status.equals("NULLIFIED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.print_onlyAudit"));
		}
		// 执行打印前的其它逻辑
		handlerService.handler("SaleForecastAsk", "print", "before",
				new Object[] { sf_id });
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 执行打印操作
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),
				BaseUtil.getLocalMessage("msg.print"), BaseUtil
						.getLocalMessage("msg.printSuccess"),
				"SaleForecastAsk|sf_id=" + sf_id));
		// 执行打印后的其它逻辑
		handlerService.handler("SaleForecastAsk", "print", "after",
				new Object[] { sf_id });
		return keys;
	}

	@Override
	public void auditSaleForecastAsk(int sf_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("SaleForecastAsk",
				"sf_statuscode", "sf_id=" + sf_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, sf_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"SaleForecastAsk",
				"sf_statuscode='AUDITED',sf_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',sf_auditer='"
						+ SystemSession.getUser().getEm_name()
						+ "',sf_auditdate=sysdate", "sf_id=" + sf_id);
		baseDao.updateByCondition(
				"SaleForecastAskDetail",
				"sd_statuscode='AUDITED',sd_status='"
						+ BaseUtil.getLocalMessage("AUDITED") + "'", "sd_sfid="
						+ sf_id);
		// 记录操作
		baseDao.logger.audit(caller, "sf_id", sf_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, sf_id);
	}

	@Override
	public void resAuditSaleForecastAsk(int sf_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("SaleForecastAsk",
				"sf_statuscode", "sf_id=" + sf_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, sf_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"SaleForecastAsk",
				"sf_statuscode='ENTERING',sf_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',sf_auditer='',sf_auditdate=null", "sf_id=" + sf_id);
		baseDao.updateByCondition(
				"SaleForecastAskDetail",
				"sd_statuscode='ENTERING',sd_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'",
				"sd_sfid=" + sf_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "sf_id", sf_id);
		handlerService.afterResAudit(caller, sf_id);
	}

	@Override
	public void submitSaleForecastAsk(int sf_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("SaleForecastAsk",
				"sf_statuscode", "sf_id=" + sf_id);
		StateAssert.submitOnlyEntering(status);
		// 只能选择已审核的客户!
		Object code = baseDao.getFieldDataByCondition("SaleForecastAsk",
				"sf_custcode", "sf_id=" + sf_id);
		status = baseDao.getFieldDataByCondition("Customer",
				"cu_auditstatuscode", "cu_code='" + code + "'");
		if (status != null && !status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("customer_onlyAudited")
					+ "<a href=\"javascript:openUrl('jsps/scm/sale/customerBase.jsp?formCondition=cu_codeIS"
					+ code + "')\">" + code + "</a>&nbsp;");
		}
		List<Object> codes2 = baseDao.getFieldDatasByCondition(
				"SaleForecastAskDetail", "sd_custcode", "sd_sfid=" + sf_id);
		for (Object c : codes2) {
			status = baseDao.getFieldDataByCondition("Customer",
					"cu_auditstatuscode", "cu_code='" + c + "'");
			if (status != null && !status.equals("AUDITED")) {
				BaseUtil.showError(BaseUtil
						.getLocalMessage("customer_onlyAudited")
						+ "<a href=\"javascript:openUrl('jsps/scm/sale/customer.jsp?formCondition=cu_codeIS"
						+ c + "')\">" + c + "</a>&nbsp;");
			}
		}
		// 只能选择已审核的物料!
		List<Object> codes = baseDao.getFieldDatasByCondition(
				"SaleForecastAskDetail", "sd_prodcode", "sd_sfid=" + sf_id);
		for (Object c : codes) {
			status = baseDao.getFieldDataByCondition("Product",
					"pr_statuscode", "pr_code='" + c + "'");
			if (!status.equals("AUDITED")) {
				BaseUtil.showError(BaseUtil
						.getLocalMessage("product_onlyAudited")
						+ "<a href=\"javascript:openUrl('jsps/scm/product/product.jsp?formCondition=pr_codeIS"
						+ c + "')\">" + c + "</a>&nbsp;");
			}
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, sf_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"SaleForecastAsk",
				"sf_statuscode='COMMITED',sf_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "sf_id="
						+ sf_id);
		baseDao.updateByCondition(
				"SaleForecastAskDetail",
				"sd_statuscode='COMMITED',sd_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'",
				"sd_sfid=" + sf_id);
		// 记录操作
		baseDao.logger.submit(caller, "sf_id", sf_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, sf_id);
	}

	@Override
	public void resSubmitSaleForecastAsk(int sf_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("SaleForecastAsk",
				"sf_statuscode", "sf_id=" + sf_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, sf_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"SaleForecastAsk",
				"sf_statuscode='ENTERING',sf_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "sf_id="
						+ sf_id);
		baseDao.updateByCondition(
				"SaleForecastAskDetail",
				"sd_statuscode='ENTERING',sd_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'",
				"sd_sfid=" + sf_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "sf_id", sf_id);
		handlerService.afterResSubmit(caller, sf_id);
	}

	@Override
	public void saveSaleForecastAskChangedate(String caller, String data) {
		// 修改SaleForecastAskDetail 出货日期和有效日期
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(data);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore,
				"SaleForecastAskDetail", "sd_id");
		baseDao.execute(gridSql);
		// 记录操作
		if (gstore.size() > 0) {
			baseDao.logMessage(new MessageLog(SystemSession.getUser()
					.getEm_name(), BaseUtil.getLocalMessage("msg.update"),
					BaseUtil.getLocalMessage("msg.updateSuccess"),
					"SaleForecastAsk|sf_id=" + gstore.get(0).get("sd_sfid")));
		}
	}

	@Override
	public void printSaleForecastAsk(int id, String caller) {
		// 执行打印前的其它逻辑
		handlerService.handler("SaleForecastAsk", "print", "before",
				new Object[] { id });
		// 执行打印操作
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),
				BaseUtil.getLocalMessage("msg.print"), BaseUtil
						.getLocalMessage("msg.printSuccess"),
				"SaleForecastAsk|sa_id=" + id));
		// 执行打印后的其它逻辑
		handlerService.handler("SaleForecastAsk", "print", "after",
				new Object[] { id });
	}

	@Override
	public void endSaleForecastAsk(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("SaleForecastAsk",
				"sf_statuscode", "sf_id=" + id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.end_onlyAudited"));
		}
		// 结案
		baseDao.updateByCondition(
				"SaleForecastAsk",
				"sf_statuscode='FINISH',sf_status='"
						+ BaseUtil.getLocalMessage("FINISH") + "'", "sf_id="
						+ id);
		baseDao.updateByCondition("SaleForecastAskDetail",
				"sd_status='FINISH'", "sd_sfid=" + id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),
				BaseUtil.getLocalMessage("msg.end"), BaseUtil
						.getLocalMessage("msg.endSuccess"),
				"SaleForecastAsk|sf_id=" + id));

	}

	@Override
	public void resEndSaleForecastAsk(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("SaleForecastAsk",
				"sf_statuscode", "sf_id=" + id);
		if (!status.equals("FINISH")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resEnd_onlyEnd"));
		}
		// 反结案
		baseDao.updateByCondition(
				"SaleForecastAsk",
				"sf_statuscode='ENTERING',sf_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "sa_id="
						+ id);
		baseDao.updateByCondition("SaleForecastAskDetail",
				"sd_status='ENTERING'", "sd_sfid=" + id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),
				BaseUtil.getLocalMessage("msg.resEnd"), BaseUtil
						.getLocalMessage("msg.resEndSuccess"),
				"SaleForecastAsk|sf_id=" + id));
	}

}
