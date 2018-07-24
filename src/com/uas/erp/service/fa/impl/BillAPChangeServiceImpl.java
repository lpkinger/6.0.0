package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.BillAPChangeService;

@Service("billAPChangeService")
public class BillAPChangeServiceImpl implements BillAPChangeService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveBillAPChange(String formStore, String gridStore, String assMainStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> assMain = BaseUtil.parseGridStoreToMaps(assMainStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BillAPChange", "bpc_code='" + store.get("bpc_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		baseDao.checkCloseMonth("MONTH-B", store.get("bpc_date"));
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 保存BillAPChange
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BillAPChange", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		int bpc_id = Integer.parseInt(store.get("bpc_id").toString());
		// 主表辅助核算保存
		for (Map<Object, Object> am : assMain) {
			am.put("ass_conid", bpc_id);
			am.put("ass_id", baseDao.getSeqId("BILLAPCHANGEASS_SEQ"));

		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(assMain, "BillAPChangeAss"));
		for (Map<Object, Object> map : gstore) {
			map.put("bpd_id", baseDao.getSeqId("BILLAPCHANGEDETAIL_SEQ"));
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(gstore, "BillAPChangeDetail"));
		baseDao.execute("update BillAPChangeDetail  set bpd_amount=round(bpd_amount,2) where bpd_bpcid=" + store.get("bpc_id"));
		// 记录操作
		baseDao.logger.save(caller, "bpc_id", store.get("bpc_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	void checkVoucher(Object id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(BPC_VOUCHERCODE) from BillAPChange where bpc_id=? and nvl(BPC_VOUCHERCODE,' ') <>' ' and BPC_VOUCHERCODE<>'UNNEED'",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("已有凭证，不允许进行当前操作!凭证编号：" + dets);
		}
	}

	void check(Object id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(bpd_detno) from BillAPChange left join BillAPChangeDetail on bpc_id=bpd_bpcid where bpc_id=? and nvl(bpc_kind,' ')='兑现' and nvl(bpc_vendcode,' ')<>nvl(bpd_vendcode,' ')",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("从表供应商与主表供应商不一致，不允许进行当前操作！行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(bpd_detno) from BillAPChange left join BillAPChangeDetail on bpc_id=bpd_bpcid where bpc_id=? and nvl(bpc_kind,' ')='兑现' and (bpd_bapcode,bpd_vendcode,bpd_catecode,bpd_catecurrency) not in (select bap_code,bap_vendcode,bap_othercatecode,bap_currency from billap)",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("明细行的供应商、币别、借方科目和原票据不一致，不允许进行当前操作！行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(bpd_detno) from BillAPChange left join BillAPChangeDetail on bpc_id=bpd_bpcid left join billap on bpd_bapid=bap_id where bpc_id=? and to_char(bap_date,'yyyymm')>to_char(bpc_date,'yyyymm')",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("异动单单据日期所在月份小于明细票据单据日期所在月份，不允许进行当前操作！行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(bpd_detno) from BillAPChangeDetail left join BillAP on bpd_bapid=bap_id where nvl(bap_settleamount,0) + nvl(bpd_amount,0)> nvl(bap_doublebalance,0) AND bpd_bpcid=?",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("异动单明细行中票据剩余结算金额不够,不允许进行当前操作! 行号：" + dets);
		}
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(bpc_code) from BillAPChange where bpc_id=? and bpc_kind = '兑现' and nvl(bpc_catecode,' ')=' '",
				String.class, id);
		if (dets != null) {
			BaseUtil.showError("银行编号为空,不允许进行当前操作!");
		}
	}

	@Override
	public void deleteBillAPChange(int bpc_id, String caller) {
		// 只能删除在录入的单据!
		Object[] status = baseDao.getFieldsDataByCondition("BillAPChange", new String[] { "bpc_statuscode", "bpc_date" }, "bpc_id="
				+ bpc_id);
		StateAssert.delOnlyEntering(status[0]);
		baseDao.checkCloseMonth("MONTH-B", status[1]);
		checkVoucher(bpc_id);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, bpc_id);
		baseDao.execute("update billap set bap_changecate=null,bap_changereason=null,bap_changedate=null where bap_code in (select bpd_bapcode from BillAPChangeDetail where bpd_bpcid="
				+ bpc_id + ")");
		// 删除BillAPChange
		baseDao.deleteById("BillAPChange", "bpc_id", bpc_id);
		// 删除BillAPChangeDetail
		baseDao.deleteById("BillAPChangedetail", "bpd_bpcid", bpc_id);
		// 记录操作
		baseDao.logger.delete(caller, "bpc_id", bpc_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, bpc_id);
	}

	@Override
	public void updateBillAPChangeById(String formStore, String gridStore, String assMainStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> assMain = BaseUtil.parseGridStoreToMaps(assMainStore);
		// 只能修改[在录入]的资料!
		Object[] status = baseDao.getFieldsDataByCondition("BillAPChange", new String[] { "bpc_statuscode", "bpc_date" },
				"bpc_id=" + store.get("bpc_id"));
		StateAssert.updateOnlyEntering(status[0]);
		baseDao.checkCloseMonth("MONTH-B", status[1]);
		checkVoucher(store.get("bpc_id"));
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "BillAPChange", "bpc_id"));
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(assMain, "BillAPChangeAss", "ass_id"));
		baseDao.execute(
				"delete from BillAPChangeAss where ass_id in (select ass_id from BillAPChange left join BillAPChangeAss on ass_conid=bpc_id left join category on ca_code=bpc_catecode where bpc_id=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ass_assname)=0)",
				store.get("bpc_id"));
		List<String> gridSql = null;
		if (gstore.size() > 0) {
			gridSql = new ArrayList<String>();
			for (Map<Object, Object> s : gstore) {
				if (s.get("bpd_id") == null || s.get("bpd_id").equals("") || s.get("bpd_id").equals("0")
						|| Integer.parseInt(s.get("bpd_id").toString()) <= 0) {
					s.put("bpd_id", baseDao.getSeqId("BILLAPCHANGEDETAIL_SEQ"));
					gridSql.add(SqlUtil.getInsertSqlByMap(s, "BILLAPCHANGEDetail"));
				} else {
					gridSql.add(SqlUtil.getUpdateSqlByFormStore(s, "BILLAPCHANGEDetail", "bpd_id"));
				}
			}
			baseDao.execute(gridSql);
		}
		baseDao.execute("update BillAPChangeDetail  set bpd_amount=round(bpd_amount,2) where bpd_bpcid=" + store.get("bpc_id"));
		// 记录操作
		baseDao.logger.update(caller, "bpc_id", store.get("bpc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void printBillAPChange(int bpc_id, String caller) {
		// 只能打印审核后的单据!
		Object status = baseDao.getFieldDataByCondition("BillAPChange", "bpc_statuscode", "bpc_id=" + bpc_id);
		if (!status.equals("AUDITED") && !status.equals("PARTRECEIVED") && !status.equals("RECEIVED") && !status.equals("NULLIFIED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.print_onlyAudit"));
		}
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, bpc_id);
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "bpc_id", bpc_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, bpc_id);
	}

	@Override
	public void auditBillAPChange(int bpc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("BillAPChange", new String[] { "bpc_statuscode", "bpc_date" }, "bpc_id="
				+ bpc_id);
		StateAssert.auditOnlyCommited(status[0]);
		baseDao.checkCloseMonth("MONTH-B", status[1]);
		baseDao.execute("update BillAPChangeDetail  set bpd_amount=round(bpd_amount,2) where bpd_bpcid=" + bpc_id);
		check(bpc_id);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, bpc_id);
		// 执行审核操作
		baseDao.audit("BillAPChange", "bpc_id=" + bpc_id, "bpc_status", "bpc_statuscode", "bpc_auditdate", "bpc_auditer");
		// 记录操作
		baseDao.logger.audit(caller, "bpc_id", bpc_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, bpc_id);
	}

	@Override
	public void resAuditBillAPChange(int bpc_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("BillAPChange", new String[] { "bpc_statuscode", "bpc_date" }, "bpc_id="
				+ bpc_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		baseDao.checkCloseMonth("MONTH-B", status[1]);
		handlerService.beforeResAudit(caller, bpc_id);
		// 执行反审核操作
		baseDao.resAudit("BillAPChange", "bpc_id=" + bpc_id, "bpc_status", "bpc_statuscode", "bpc_auditdate", "bpc_auditer");
		// 记录操作
		baseDao.logger.resAudit(caller, "bpc_id", bpc_id);
		handlerService.afterResAudit(caller, bpc_id);
	}

	@Override
	public void submitBillAPChange(int bpc_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("BillAPChange", new String[] { "bpc_statuscode", "bpc_date" }, "bpc_id="
				+ bpc_id);
		StateAssert.submitOnlyEntering(status[0]);
		baseDao.checkCloseMonth("MONTH-B", status[1]);
		baseDao.execute("update BillAPChangeDetail set bpd_amount=round(bpd_amount,2) where bpd_bpcid=" + bpc_id);
		check(bpc_id);
		checkAss(bpc_id);
		int count = baseDao.getCount("select count(1) from BillAPChange where bpc_id=" + bpc_id
				+ " and round(bpc_amount,2)<>(select round(sum(bpd_amount),2) from BillAPChangeDetail where bpd_bpcid=bpc_id)");
		if (count > 0) {
			BaseUtil.showError("主表的贷方总额与明细行的借方金额合计不一致，不允许提交!");
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, bpc_id);
		// 执行提交操作
		baseDao.submit("BillAPChange", "bpc_id=" + bpc_id, "bpc_status", "bpc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "bpc_id", bpc_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, bpc_id);
	}

	@Override
	public void resSubmitBillAPChange(int bpc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("BillAPChange", new String[] { "bpc_statuscode", "bpc_date" }, "bpc_id="
				+ bpc_id);
		StateAssert.resSubmitOnlyCommited(status[0]);
		baseDao.checkCloseMonth("MONTH-B", status[1]);
		handlerService.beforeResSubmit(caller, bpc_id);
		// 执行反提交操作
		baseDao.resOperate("BillAPChange", "bpc_id=" + bpc_id, "bpc_status", "bpc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "bpc_id", bpc_id);
		handlerService.afterResSubmit(caller, bpc_id);
	}

	@Override
	public void accountedBillAPChange(int bpc_id, String caller) {
		Employee employee = SystemSession.getUser();
		// 只能对状态为[未记账]的订单进行操作!
		Object[] status = baseDao.getFieldsDataByCondition("BillAPChange", new String[] { "bpc_statuscode", "bpc_code", "bpc_date" },
				"bpc_id=" + bpc_id);
		baseDao.checkCloseMonth("MONTH-B", status[2]);
		baseDao.execute("update BillAPChangeDetail  set bpd_amount=round(bpd_amount,2) where bpd_bpcid=" + bpc_id);
		check(bpc_id);
		checkAss(bpc_id);
		// 执行记账前的其它逻辑
		handlerService.handler("BillAPChange", "account", "before", new Object[] { bpc_id });
		// 执行记账操作
		// 存储过程
		String res = baseDao.callProcedure("SP_COMMITEBILLAPCHAGNE", new Object[] { status[1], String.valueOf(employee.getEm_id()) });
		if (res != null && !res.trim().equals("") && !"OK".equals(res.toUpperCase())) {
			BaseUtil.showError(res);
		}
		// 插入产生银行登记明细的辅助核算
		String insertAssDetSql = "insert into accountregisterdetailass(ars_id,ars_ardid,ars_detno,ars_asstype,ars_asscode,ars_assname,ars_type) values (?,?,?,?,?,?,'AccountRegister!Bank')";
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select * from accountregister left join accountregisterdetail on ard_arid=ar_id where ar_sourcetype='应付票据异动' and ar_sourceid=? and nvl(ard_catecode,' ')<>' ' ",
						bpc_id);
		while (rs.next()) {
			Object catecode = rs.getObject("ard_catecode");
			int ardid = rs.getInt("ard_id");
			SqlRowList ass = baseDao.queryForRowSet("select ca_assname from category where ca_code=? and nvl(ca_assname,' ')<>' '",
					catecode);
			if (ass.next()) {
				String assStr = ass.getString("ca_assname");
				String[] codes = assStr.split("#");
				for (String assname : codes) {
					int i = baseDao.getCount("select count(1) from accountregisterdetailass where ars_ardid=" + ardid
							+ " and ARS_ASSTYPE='" + assname + "'");
					if (i == 0) {
						Object maxno = baseDao.getFieldDataByCondition("accountregisterdetailass", "max(nvl(ars_detno,0))", "ars_ardid="
								+ ardid);
						maxno = maxno == null ? 0 : maxno;
						int detno = Integer.parseInt(maxno.toString()) + 1;
						int arsid = baseDao.getSeqId("ACCOUNTREGISTERDETAILASS_SEQ");
						baseDao.execute(insertAssDetSql, new Object[] { arsid, ardid, detno, assname, null, null });
					}
					int arsid = baseDao.getFieldValue("accountregisterdetailass", "ars_id", "ars_ardid=" + ardid + " and ARS_ASSTYPE='"
							+ assname + "'", Integer.class);
					if ("部门".equals(assname) && StringUtil.hasText(rs.getObject("ar_departmentcode"))) {
						baseDao.execute("update accountregisterdetailass set ars_asscode='" + rs.getObject("ar_departmentcode")
								+ "', ars_assname='" + rs.getObject("ar_departmentname") + "' where ars_id=" + arsid);
					}
					if ("项目".equals(assname) && StringUtil.hasText(rs.getObject("ar_prjcode"))) {
						baseDao.execute("update accountregisterdetailass set ars_asscode='" + rs.getObject("ar_prjcode")
								+ "', ars_assname='" + rs.getObject("ar_prjname") + "' where ars_id=" + arsid);
					}
					if ("客户往来".equals(assname) && StringUtil.hasText(rs.getObject("ar_custcode"))) {
						baseDao.execute("update accountregisterdetailass set ars_asscode='" + rs.getObject("ar_custcode")
								+ "', ars_assname='" + rs.getObject("ar_custname") + "' where ars_id=" + arsid
								+ " and nvl(ars_asscode,' ')=' '");
					}
					if ("供应商往来".equals(assname) && StringUtil.hasText(rs.getObject("ar_vendcode"))) {
						baseDao.execute("update accountregisterdetailass set ars_asscode='" + rs.getObject("ar_vendcode")
								+ "', ars_assname='" + rs.getObject("ar_vendname") + "' where ars_id=" + arsid
								+ " and nvl(ars_asscode,' ')=' '");
					}
				}
			}
		}
		// 记录操作
		baseDao.logger.others("msg.account", "msg.accountSuccess", caller, "bpc_id", bpc_id);
		// 执行记账后的其它逻辑
		handlerService.handler("BillAPChange", "account", "after", new Object[] { bpc_id });
	}

	@Override
	public void resAccountedBillAPChange(int bpc_id, String caller) {
		Employee employee = SystemSession.getUser();
		// 只能对状态为[已记账]的订单进行反记账操作!
		Object[] status = baseDao.getFieldsDataByCondition("BillAPChange", new String[] { "bpc_statuscode", "bpc_code", "bpc_date" },
				"bpc_id=" + bpc_id);
		if (!status[0].equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAccount_onlyAccount"));
		}
		baseDao.checkCloseMonth("MONTH-B", status[2]);
		checkVoucher(bpc_id);
		// 执行反记账操作
		String res = baseDao.callProcedure("SP_UNCOMMITEBILLAPCHAGNE", new Object[] { status[1], String.valueOf(employee.getEm_id()) });
		if (res != null && !res.trim().equals("") && !"OK".equals(res.toUpperCase())) {
			BaseUtil.showError(res);
		}
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.resAccount"), BaseUtil
				.getLocalMessage("msg.resAccountSuccess"), "BillAPChange|bpc_id=" + bpc_id));
		handlerService.afterResAudit(caller, bpc_id);
	}

	private void checkAss(int bpc_id) {
		baseDao.execute(
				"delete from BILLAPCHANGEass where ASS_ID in (select ASS_ID from BILLAPCHANGE left join BILLAPCHANGEass on ASS_CONID=bpc_id left join category on ca_code=bpc_catecode where bpc_id=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ASS_ASSNAME)=0)",
				bpc_id);
		baseDao.execute(
				"delete from BILLAPCHANGEass where ASS_CONID in (select bpc_id from BILLAPCHANGE left join category on ca_code=bpc_catecode where bpc_id=? and nvl(ca_asstype,' ')=' ')",
				bpc_id);
		// 辅助核算不完善
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(bpc_code) from BILLAPCHANGE left join BILLAPCHANGEass on ASS_CONID=bpc_id left join category on ca_code=bpc_catecode where bpc_id=? and nvl(ca_assname,' ')<>' ' and (nvl(ASS_ASSTYPE,' ')=' ' or nvl(ASS_CODEFIELD,' ')=' ' or nvl(ASS_NAMEFIELD,' ')=' ') order by bpc_id",
						String.class, bpc_id);
		if (dets != null) {
			BaseUtil.showError("主表辅助核算不完善，不允许进行当前操作!");
		}
		// 核算项重复
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(bpc_code) from (select count(1) c,bpc_code,ASS_ASSTYPE from BILLAPCHANGE left join BILLAPCHANGEass on ASS_CONID=bpc_id where bpc_id=? and nvl(ASS_ASSTYPE,' ')<>' ' group by bpc_code,ASS_ASSTYPE) where c>1 order by bpc_code",
						String.class, bpc_id);
		if (dets != null) {
			BaseUtil.showError("主表辅助核算核算项重复，不允许进行当前操作!");
		}
		// 核算项错误
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(bpc_code) from BILLAPCHANGE left join BILLAPCHANGEass on ASS_CONID=bpc_id left join category on ca_code=bpc_catecode where bpc_id=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ASS_ASSNAME)=0 order by bpc_code",
						String.class, bpc_id);
		if (dets != null) {
			BaseUtil.showError("主表核算项错误，不允许进行当前操作!");
		}
		// 核算项不存在
		String str = "";
		SqlRowList rs1 = baseDao
				.queryForRowSet(
						"select 'select count(1) from '||ak_table||' where '||ak_asscode||'='''||ASS_CODEFIELD||''' and '||AK_ASSNAME||'='''||ASS_NAMEFIELD||'''' from BILLAPCHANGEass left join asskind on ASS_ASSNAME=ak_name left join BILLAPCHANGE on ASS_CONID=bpc_id where bpc_id=? order by bpc_code",
						bpc_id);
		if (rs1.next()) {
			str = "";
			SqlRowList rd = baseDao.queryForRowSet(rs1.getString(1));
			if (rd.next() && rd.getInt(1) == 0) {
				if (StringUtil.hasText(str))
					str = str + ",";
				str += rd.getInt(1);
			}
		}
		if (str.length() > 0)
			BaseUtil.showError("主表核算编号+核算名称不存在，不允许进行当前操作!");
	}
}
