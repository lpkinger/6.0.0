package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.RecBalanceService;

@Service
public class RecBalanceServiceImpl implements RecBalanceService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private VoucherDao voucherDao;

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

	private void checkAss(int id) {
		baseDao.execute(
				"delete from RecBalanceass where ASS_ID in (select ASS_ID from RecBalance left join RecBalanceass on ASS_CONID=rb_id left join category on ca_code=rb_bankno where rb_id=? and rb_kind in ('收款单','应收退款单') and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ASS_ASSNAME)=0)",
				id);
		baseDao.execute(
				"delete from RecBalanceass where ASS_CONID in (select rb_id from RecBalance left join category on ca_code=rb_bankno where rb_id=? and rb_kind in ('收款单','应收退款单') and nvl(ca_asstype,' ')=' ')",
				id);
		baseDao.execute(
				"delete from RecBalanceass where ASS_ID in (select ASS_ID from RecBalance left join RecBalanceass on ASS_CONID=rb_id left join category on ca_code=rb_catecode where rb_id=? and rb_kind not in ('收款单','应收退款单') and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ASS_ASSNAME)=0)",
				id);
		baseDao.execute(
				"delete from RecBalanceass where ASS_CONID in (select rb_id from RecBalance left join category on ca_code=rb_catecode where rb_id=? and rb_kind not in ('收款单','应收退款单') and nvl(ca_asstype,' ')=' ')",
				id);
		baseDao.execute(
				"delete from RecBalancedetailass where DASS_ID in (select DASS_ID from RecBalancedetail left join RecBalancedetailass on DASS_CONDID=rbd_id left join category on ca_code=rbd_catecode where rbd_rbid=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,DASS_ASSNAME)=0)",
				id);
		baseDao.execute(
				"delete from RecBalancedetailass where DASS_CONDID in (select rbd_id from RecBalance left join RecBalancedetail on rbd_rbid=rb_id left join category on ca_code=rbd_catecode where rb_id=? and nvl(ca_asstype,' ')=' ')",
				id);
		// 辅助核算不完善
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(rb_code) from RecBalance left join RecBalanceass on ASS_CONID=rb_id left join category on ca_code=rb_bankno where rb_id=?  and rb_kind in ('收款单','应收退款单') and nvl(ca_assname,' ')<>' ' and (nvl(ASS_ASSTYPE,' ')=' ' or nvl(ASS_CODEFIELD,' ')=' ' or nvl(ASS_NAMEFIELD,' ')=' ') order by rb_id",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("主表辅助核算不完善，不允许进行当前操作!");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(rb_code) from RecBalance left join RecBalanceass on ASS_CONID=rb_id left join category on ca_code=rb_catecode where rb_id=?  and rb_kind not in ('收款单','应收退款单') and nvl(ca_assname,' ')<>' ' and (nvl(ASS_ASSTYPE,' ')=' ' or nvl(ASS_CODEFIELD,' ')=' ' or nvl(ASS_NAMEFIELD,' ')=' ') order by rb_id",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("主表辅助核算不完善，不允许进行当前操作!");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(rbd_detno) from RecBalancedetail left join RecBalancedetailass on DASS_CONDID=rbd_id left join category on ca_code=rbd_catecode where rbd_rbid=? and nvl(ca_assname,' ')<>' ' and (nvl(DASS_ASSTYPE,' ')=' ' or nvl(DASS_ASSNAME,' ')=' ' or nvl(DASS_CODEFIELD,' ')=' ') order by rbd_detno",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("明细行辅助核算不完善，不允许进行当前操作!行号：" + dets);
		}
		// 核算项重复
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(rb_code) from (select count(1) c,rb_code,ASS_ASSTYPE from RecBalance left join RecBalanceass on ASS_CONID=rb_id where rb_id=? and nvl(ASS_ASSTYPE,' ')<>' ' group by rb_code,ASS_ASSTYPE) where c>1 order by rb_code",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("主表辅助核算核算项重复，不允许进行当前操作!");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(rbd_detno) from (select count(1) c,rbd_detno,DASS_ASSTYPE from RecBalancedetail left join RecBalancedetailass on DASS_CONDID=rbd_id where rbd_rbid=? and nvl(DASS_ASSTYPE,' ')<>' ' group by rbd_detno,DASS_ASSTYPE) where c>1 order by rbd_detno",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("明细行辅助核算核算项重复，不允许进行当前操作!行号：" + dets);
		}
		// 核算项错误
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(rb_code) from RecBalance left join RecBalanceass on ASS_CONID=rb_id left join category on ca_code=rb_bankno where rb_id=? and rb_kind in ('收款单','应收退款单') and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ASS_ASSNAME)=0 order by rb_code",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("主表核算项错误，不允许进行当前操作!");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(rb_code) from RecBalance left join RecBalanceass on ASS_CONID=rb_id left join category on ca_code=rb_catecode where rb_id=? and rb_kind not in ('收款单','应收退款单') and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ASS_ASSNAME)=0 order by rb_code",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("主表核算项错误，不允许进行当前操作!");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(rbd_detno) from RecBalancedetail left join RecBalancedetailass on DASS_CONDID=rbd_id left join category on ca_code=rbd_catecode where rbd_rbid=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,DASS_ASSNAME)=0 order by rbd_detno",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("明细行核算项错误，不允许进行当前操作!行号：" + dets);
		}
		// 核算项不存在
		String str = "";
		StringBuffer error = new StringBuffer();
		SqlRowList rs1 = baseDao
				.queryForRowSet(
						"select 'select '||rbd_detno||',count(1) from '||ak_table||' where '||ak_asscode||'='''||DASS_CODEFIELD||''' and '||AK_ASSNAME||'='''||DASS_NAMEFIELD||'''' from RecBalancedetailass left join asskind on DASS_ASSNAME=ak_name left join RecBalancedetail on DASS_CONDID=rbd_id where rbd_rbid=? order by rbd_detno",
						id);
		while (rs1.next()) {
			SqlRowList rd = baseDao.queryForRowSet(rs1.getString(1));
			if (rd.next() && rd.getInt(2) == 0) {
				if (StringUtil.hasText(str))
					str = str + ",";
				str += rd.getInt(1);
			}
		}
		if (str.length() > 0)
			error.append("核算编号+核算类型不存在,行:").append(str).append(";");
		BaseUtil.showError(error.toString());
		rs1 = baseDao
				.queryForRowSet(
						"select 'select count(1) from '||ak_table||' where '||ak_asscode||'='''||ASS_CODEFIELD||''' and '||AK_ASSNAME||'='''||ASS_NAMEFIELD||'''' from RecBalanceass left join asskind on ASS_ASSNAME=ak_name left join RecBalance on ASS_CONID=rb_id where rb_id=? order by rb_code",
						id);
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

	@Override
	public void saveRecBalance(String caller, String formStore, String gridStore, String assStore, String assMainStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore); // 主表form数据
		checkDate(store.get("rb_date").toString());
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore); // 从表grid数据
		List<Map<Object, Object>> ass = BaseUtil.parseGridStoreToMaps(assStore); // 从表辅助核算grid数据
		List<Map<Object, Object>> assMain = BaseUtil.parseGridStoreToMaps(assMainStore); // 主表辅助核算grid数据
		// checkLimit(store.get("rb_id"));
		handlerService.beforeSave(caller, new Object[] { store, grid });

		// 主表form中添加的默认信息
		store.put("rb_statuscode", "UNPOST");
		store.put("rb_status", BaseUtil.getLocalMessage("UNPOST"));
		store.put("rb_printstatuscode", "UNPRINT");
		store.put("rb_printstatus", BaseUtil.getLocalMessage("UNPRINT"));
		store.put("rb_auditstatuscode", "ENTERING");
		store.put("rb_auditstatus", BaseUtil.getLocalMessage("ENTERING"));
		store.put("rb_strikestatuscode", "UNSTRIKE");
		store.put("rb_strikestatus", BaseUtil.getLocalMessage("UNSTRIKE"));
		// 保存RecBalance
		int rb_id = Integer.parseInt(store.get("rb_id").toString());
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "RecBalance"));
		// 主表辅助核算保存S
		for (Map<Object, Object> am : assMain) {
			am.put("ass_conid", rb_id);
			am.put("ass_id", baseDao.getSeqId("RECBALANCEASS_SEQ"));
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(assMain, "RecBalanceAss"));
		// 主表辅助核算保存O

		Map<Object, List<Map<Object, Object>>> list = BaseUtil.groupMap(ass, "dass_condid");
		int id;
		// //保存RecBalanceDetail
		for (Map<Object, Object> map : grid) {
			id = baseDao.getSeqId("RECBALANCEDETAIL_SEQ");
			ass = list.get(String.valueOf(map.get("rbd_id")));
			if (ass != null) {
				for (Map<Object, Object> m : ass) {// RecBalanceDetailAss
					m.put("dass_condid", id);
					m.put("dass_id", baseDao.getSeqId("RECBALANCEDETAILASS_SEQ"));
				}
				baseDao.execute(SqlUtil.getInsertSqlbyGridStore(ass, "RecBalanceDetailAss"));
			}
			map.put("rbd_id", id);
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(grid, "RecBalanceDetail"));
		// 记录操作
		baseDao.logger.save(caller, "rb_id", rb_id);
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void updateRecBalanceById(String caller, String formStore, String gridStore, String assStore, String assMainStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的采购单资料!
		Object status[] = baseDao.getFieldsDataByCondition("RecBalance", new String[] { "rb_auditstatuscode", "rb_statuscode" }, "rb_id="
				+ store.get("rb_id"));
		// Object status = baseDao.getFieldDataByCondition("RecBalance",
		// "rb_auditstatuscode", "rb_id=" + store.get("rb_id"));
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyUnPost"));
		}
		checkVoucher(store.get("rb_id"));
		checkDate(store.get("rb_date").toString());

		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> ass = BaseUtil.parseGridStoreToMaps(assStore);
		List<Map<Object, Object>> assMain = BaseUtil.parseGridStoreToMaps(assMainStore);
		handlerService.beforeUpdate(caller, new Object[] { store, grid });

		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "RecBalance", "rb_id"));
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(assMain, "RecBalanceAss", "ass_id"));
		baseDao.execute(
				"delete from RecBalanceAss where ass_id in (select ass_id from RecBalance left join RecBalanceAss on ass_conid=rb_id left join category on ca_code=rb_bankno where rb_id=? and rb_kind in ('收款单','应收退款单') and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ass_assname)=0)",
				store.get("rb_id"));
		Map<Object, List<Map<Object, Object>>> list = BaseUtil.groupMap(ass, "dass_condid");
		int id;
		List<String> gridSql = null;
		if (grid.size() > 0) {
			// gridSql = SqlUtil.getUpdateSqlbyGridStore(grid,
			// "RecBalanceDetail", "rbd_id");
			gridSql = new ArrayList<String>();
			for (Map<Object, Object> s : grid) {

				if (s.get("rbd_id") == null || s.get("rbd_id").equals("") || s.get("rbd_id").equals("0")
						|| Integer.parseInt(s.get("rbd_id").toString()) <= 0) {
					id = baseDao.getSeqId("RECBALANCEDETAIL_SEQ");
					ass = list.get(String.valueOf(s.get("rbd_id")));
					if (ass != null) {
						for (Map<Object, Object> m : ass) {// VoucherDetailAss
							m.put("dass_condid", id);
							m.put("dass_id", baseDao.getSeqId("RECBALANCEDETAILASS_SEQ"));
						}
						baseDao.execute(SqlUtil.getInsertSqlbyGridStore(ass, "RecBalanceDetailAss"));
					}
					s.put("rbd_id", id);
					gridSql.add(SqlUtil.getInsertSqlByMap(s, "RecBalanceDetail"));
				} else {
					gridSql.add(SqlUtil.getUpdateSqlByFormStore(s, "RecBalanceDetail", "rbd_id"));
					id = Integer.parseInt(s.get("rbd_id").toString());
					ass = list.get(String.valueOf(id));
					if (ass != null) {
						List<String> sqls = SqlUtil.getUpdateSqlbyGridStore(ass, "RecBalanceDetailAss", "dass_id");
						for (Map<Object, Object> m : ass) {
							if (m.get("dass_id") == null || m.get("dass_id").equals("") || m.get("dass_id").equals("0")
									|| Integer.parseInt(m.get("dass_id").toString()) <= 0) {
								m.put("dass_id", baseDao.getSeqId("RECBALANCEDETAILASS_SEQ"));
								sqls.add(SqlUtil.getInsertSqlByMap(m, "RecBalanceDetailAss"));
							}
						}
						baseDao.execute(sqls);
					}
				}
			}
			baseDao.execute(gridSql);
		} else {
			Set<Object> items = list.keySet();
			for (Object i : items) {
				ass = list.get(String.valueOf(i));
				if (ass != null) {
					List<String> sqls = SqlUtil.getUpdateSqlbyGridStore(ass, "RecBalanceDetailAss", "dass_id");
					for (Map<Object, Object> m : ass) {
						if (m.get("dass_id") == null || m.get("dass_id").equals("") || m.get("dass_id").equals("0")
								|| Integer.parseInt(m.get("dass_id").toString()) <= 0) {
							m.put("dass_id", baseDao.getSeqId("RECBALANCEDETAILASS_SEQ"));
							sqls.add(SqlUtil.getInsertSqlByMap(m, "RecBalanceDetailAss"));
						}
					}
					baseDao.execute(sqls);
				}
			}
		}
		// 记录操作
		baseDao.logger.update(caller, "rb_id", store.get("rb_id"));
		handlerService.afterUpdate(caller, new Object[] { store, grid });
	}

	void checkVoucher(Object id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(rb_vouchercode) from RecBalance where rb_id=? and rb_vouchercode is not null and rb_vouchercode <>'UNNEED'",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("已有凭证，不允许进行当前操作!凭证编号：" + dets);
		}
	}

	@Override
	public void deleteRecBalance(String caller, int rb_id) {
		if ("RecBalance!IMRE".equals(caller)) {
			baseDao.execute(
					"update recbalance set RB_SOURCECODE=RB_SOURCE WHERE RB_SOURCE IS NOT NULL AND RB_SOURCECODE IS NULL and nvl(RB_SOURCEID,0)<>0 and rb_kind ='冲应收款' and RB_ID=?",
					rb_id);
			baseDao.execute("update recbalance set RB_SOURCE='Bank' WHERE RB_SOURCECODE=RB_SOURCE and rb_kind ='冲应收款' and rb_id=?", rb_id);
		}
		Object status[] = baseDao.getFieldsDataByCondition("RecBalance", new String[] { "rb_auditstatuscode", "rb_statuscode", "rb_kind" },
				"rb_id=" + rb_id);
		SqlRowList rs = baseDao.queryForRowSet(
				"select rb_auditstatuscode,rb_statuscode,rb_kind,rb_aramount,rb_sourceid,rb_source from RecBalance where rb_id=?", rb_id);
		if (rs.next()) {
			if (!"ENTERING".equals(rs.getObject("rb_auditstatuscode"))) {
				BaseUtil.showError("只能删除[在录入]的" + status[2] + "！");
			}
			if (!"UNPOST".equals(rs.getObject("rb_statuscode"))) {
				BaseUtil.showError("只能删除[未过账]的" + status[2] + "！");
			}
			checkVoucher(rb_id);
			// 执行删除前的其它逻辑
			handlerService.beforeDel(caller, rb_id);
			// 删除RecBalance
			baseDao.deleteById("RecBalance", "rb_id", rb_id);
			// 删除RecBalanceDetail
			baseDao.deleteById("RecBalanceDetail", "rbd_rbid", rb_id);
			// 删除RecBalancePRDetail
			baseDao.deleteById("RecBalancePRDetail", "rbpd_rbid", rb_id);
			if ("RecBalance!IMRE".equals(caller) && "Bank".equals(rs.getGeneralString("rb_source"))) {
				baseDao.execute(
						"UPDATE ACCOUNTREGISTER SET ar_recamount=NVL((SELECT SUM(rb_amount) FROM RECBALANCE WHERE rb_kind='冲应收款' AND rb_sourceid=ar_id AND rb_source='Bank'),0) where ar_id=?",
						rs.getObject("rb_sourceid"));
			}
			// 记录操作
			baseDao.logger.delete(caller, "rb_id", rb_id);
			// 执行删除后的其它逻辑
			handlerService.afterDel(caller, rb_id);
		}
	}

	@Override
	public void printRecBalance(String caller, int rb_id) {
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, rb_id);
		// 执行审核操作
		baseDao.updateByCondition("RecBalance",
				"rb_printstatuscode='PRINTED',rb_printstatus='" + BaseUtil.getLocalMessage("PRINTED") + "'", "rb_id=" + rb_id);
		// 记录操作
		baseDao.logger.print(caller, "rb_id", rb_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, rb_id);
	}

	@Override
	public void auditRecBalance(String caller, int rb_id) {
		Object[] args = baseDao.getFieldsDataByCondition("RecBalance", "rb_auditstatuscode,rb_date", "rb_id=" + rb_id);
		StateAssert.auditOnlyCommited(args[0]);
		checkDate(args[1].toString().substring(0, 10));
		// 执行过账前的其它逻辑
		handlerService.beforeAudit(caller, rb_id);
		// 执行审核操作
		baseDao.updateByCondition("RecBalance", "rb_auditstatuscode='AUDITED',rb_auditstatus='" + BaseUtil.getLocalMessage("AUDITED")
				+ "',RB_AUDITER='" + SystemSession.getUser().getEm_name() + "',RB_AUDITdate=sysdate", "rb_id=" + rb_id);
		// 记录操作
		baseDao.logger.audit(caller, "rb_id", rb_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, rb_id);
	}

	@Override
	public void resAuditRecBalance(String caller, int rb_id) {
		// 执行反审核操作
		Object[] objs = baseDao.getFieldsDataByCondition("RecBalance", new String[] { "rb_auditstatuscode", "rb_statuscode" }, "rb_id="
				+ rb_id);
		if (!objs[0].equals("AUDITED") || objs[1].equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
		}
		handlerService.beforeResAudit(caller, rb_id);
		baseDao.updateByCondition("RecBalance", "rb_auditstatuscode='ENTERING',rb_auditstatus='" + BaseUtil.getLocalMessage("ENTERING")
				+ "',RB_AUDITER='',RB_AUDITdate=null", "rb_id=" + rb_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "rb_id", rb_id);
		handlerService.afterResAudit(caller, rb_id);
	}

	/*
	 * xiongcy
	 * 
	 * public void checkLimit(Object rb_id ){
	 * 
	 * // 判断来源单据的客户与发票日期是否一致
	 * 
	 * String errMonths = baseDao .getJdbcTemplate() .queryForObject(
	 * "select wm_concat(rbd_detno) from recbalancedetail left join recbalance on rbd_rbid=rb_id left join arbill on rbd_ordercode=ab_code where rb_id=? and to_char(rb_date,'yyyymm')<=to_char(ab_date,'yyyymm')"
	 * , String.class, rb_id); if (errMonths != null)
	 * BaseUtil.showError("来源单据的客户与发票客户不一致,行：" + errMonths);
	 * 
	 * // 判断来源单据的冲账金额与 本次结算金额是否一致
	 * 
	 * SqlRowList rs = baseDao .queryForRowSet(
	 * "select rb_cmamount,(select sum(rbd_nowbalance) from recbalancedetail where rbd_rbid=rb_id and rbd_ordercode is not null) nowbalance from recbalance where rb_id=?"
	 * , rb_id); if (rs.next() && rs.getGeneralDouble(1, 2) !=
	 * rs.getGeneralDouble(2, 2))
	 * BaseUtil.showError(String.format("冲账金额%s与 本次结算金额%s不一致",
	 * rs.getGeneralDouble(1, 2), rs.getGeneralDouble(2, 2)));
	 * 
	 * // 判断来源单据的冲账金额与 本次结算汇率是否一致
	 * 
	 * 
	 * rs = baseDao .queryForRowSet(
	 * "select rb_cmrate,round(rb_cmamount/rb_amount,8) from recbalance where rb_id=?"
	 * , rb_id); if (rs.next() && rs.getGeneralDouble(1, 8) !=
	 * rs.getGeneralDouble(2, 8))
	 * BaseUtil.showError(String.format("冲账汇率%s与汇率%s不一致",
	 * rs.getGeneralDouble(1,8), rs.getGeneralDouble(2,8))); //
	 * 判断来源单据的客户与发票客户是否一致
	 * 
	 * String errCusts = baseDao .getJdbcTemplate() .queryForObject(
	 * "select wm_concat(rbd_detno) from recbalancedetail left join recbalance on rbd_rbid=rb_id left join arbill on rbd_ordercode=ab_code where rb_id=? and rb_custcode<>ab_custcode"
	 * , String.class, rb_id); if (errCusts != null)
	 * BaseUtil.showError("来源单据的客户与发票客户不一致,行：" + errCusts);
	 * 
	 * // 判断来源单据的币别与发票的是否一致
	 * 
	 * String errCurrs = baseDao .getJdbcTemplate() .queryForObject(
	 * "select wm_concat(rbd_detno) from recbalancedetail left join recbalance on rbd_rbid=rb_id left join arbill on rbd_ordercode=ab_code where rb_id=? and rb_cmcurrency<>ab_currency"
	 * , String.class, rb_id); if (errCurrs != null)
	 * BaseUtil.showError("来源单据的客户与发票币别不一致,行：" + errCurrs); };
	 */

	@Override
	public void submitRecBalance(String caller, int rb_id) {
		baseDao.execute("UPDATE RECBALANCE SET RB_RATE=NVL((SELECT CM_CRRATE FROM CURRENCYSMONTH WHERE RB_CURRENCY=CM_CRNAME AND TO_CHAR(RB_DATE,'yyyymm')=CM_YEARMONTH),0) "
				+ "WHERE rb_id=" + rb_id);
		Object[] args = baseDao.getFieldsDataByCondition("RecBalance", "rb_auditstatuscode,rb_date", "rb_id=" + rb_id);
		StateAssert.submitOnlyEntering(args[0]);
		checkDate(args[1].toString().substring(0, 10));
		if ("RecBalance!PBIL".equals(caller) || "RecBalance!IMRE".equals(caller) || "RecBalance!ARRM".equals(caller)) {
			baseDao.execute("update RecBalance set rb_aramount=round((select NVL(sum(rbd_nowbalance),0) from RecBalanceDetail where nvl(rbd_ordercode,' ')<>' ' and rbd_rbid=rb_id),2) where rb_id="
					+ rb_id);
		}
		// 收款单提交更新预收余额
		if ("RecBalance!PBIL".equals(caller)) {
			if (baseDao.checkIf("user_tab_columns", "table_name = 'RECBALANCE' AND column_name = 'RB_PREPAYAMOUNT'")) {
				String sql = "UPDATE RECBALANCE SET rb_prepayamount=(SELECT nvl(ca_prepayamount,0) FROM custar WHERE ca_custcode=rb_custcode AND ca_currency=rb_cmcurrency) WHERE rb_id='"
						+ rb_id + "'";
				baseDao.execute(sql);
			}
		}
		if ("RecBalance!ARRM".equals(caller)) {
			int count = baseDao.getCount("SELECT count(distinct ab_sendtype) from recbalancedetail,arbill where rbd_rbid=" + rb_id
					+ " and AB_CODE=rbd_ordercode");
			if (count > 1) {
				BaseUtil.showError("发票类型不同的发票在同一张应收款转销单，不能提交！");
			}
		}

		if ("RecBalance!PTAR".equals(caller)) {
			SqlRowList rs = baseDao
					.queryForRowSet(
							"select rbpd_ordercode,round(nvl(rbpd_nowbalance,0),2) rbpd_nowbalance,"
									+ "rbpd_detno,pr_id,pr_kind,pr_statuscode,round(CASE WHEN pr_kind='预收退款单' THEN -1 ELSE 1 END *nvl(pr_jsamount,0),2) pr_jsamount,round(nvl(pr_havebalance,0),2) pr_havebalance "
									+ "from RecBalancePrDetail left join (select pr_id,pr_kind,pr_statuscode,pr_jsamount,pr_havebalance from PreRec "
									+ "where (pr_kind='预收款' or pr_kind='初始化' or pr_kind='预收退款单'))  ON rbpd_sourceid = pr_id  where rbpd_rbid = ?",
							rb_id);
			while (rs.next()) {
				if (rs.getGeneralInt("pr_id") == 0) {
					BaseUtil.showError("序号：" + rs.getGeneralInt("rbpd_detno") + "，预收款或预收退款单(" + rs.getString("rbpd_ordercode")
							+ ")不存在，不能提交！");
				}
				double nowbalance = rs.getGeneralDouble("rbpd_nowbalance");
				double havebalance = rs.getGeneralDouble("pr_havebalance");
				double jsamount = rs.getGeneralDouble("pr_jsamount");
				if (NumberUtil.compare(jsamount, (nowbalance + havebalance), 2) == -1) {
					if ("预收款".equals(rs.getString("pr_kind"))) {
						BaseUtil.showError("序号[" + rs.getGeneralInt("rbpd_detno") + "]超预收款单[" + rs.getString("rbpd_ordercode")
								+ "]的预收金额，不能提交！");
					} else if ("预收退款单".equals(rs.getString("pr_kind"))) {
						BaseUtil.showError("序号[" + rs.getGeneralInt("rbpd_detno") + "]超预收退款单[" + rs.getString("rbpd_ordercode")
								+ "]的预收退款金额，不能提交！");
					}
				}
				if (!"POSTED".equals(rs.getString("pr_statuscode"))) {
					if ("预收款".equals(rs.getString("pr_kind"))) {
						BaseUtil.showError("序号[" + rs.getGeneralInt("rbpd_detno") + "]预收款单[" + rs.getString("rbpd_ordercode")
								+ "]未过账，不能提交！");
					} else if ("预收退款单".equals(rs.getString("pr_kind"))) {
						BaseUtil.showError("序号[" + rs.getGeneralInt("rbpd_detno") + "]预收退款单[" + rs.getString("rbpd_ordercode")
								+ "]未过账，不能提交！");
					}
				}
			}
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, rb_id);
		checkAss(rb_id);
		// 执行提交操作
		baseDao.submit("RecBalance", "rb_id=" + rb_id, "rb_auditstatus", "rb_auditstatuscode");
		// 记录操作
		baseDao.logger.submit(caller, "rb_id", rb_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, rb_id);
	}

	@Override
	public void resSubmitRecBalance(String caller, int rb_id) {
		Object status = baseDao.getFieldDataByCondition("RecBalance", "rb_auditstatuscode", "rb_id=" + rb_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, rb_id);
		// 执行反提交操作
		baseDao.updateByCondition("RecBalance", "rb_auditstatuscode='ENTERING',rb_auditstatus='" + BaseUtil.getLocalMessage("ENTERING")
				+ "'", "rb_id=" + rb_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "rb_id", rb_id);
		handlerService.afterResSubmit(caller, rb_id);
	}

	@Override
	public void postRecBalance(String caller, int rb_id) {
		baseDao.execute("UPDATE RECBALANCE SET RB_RATE=NVL((SELECT CM_CRRATE FROM CURRENCYSMONTH WHERE RB_CURRENCY=CM_CRNAME AND TO_CHAR(RB_DATE,'yyyymm')=CM_YEARMONTH),0) "
				+ "WHERE rb_id=" + rb_id);
		Object status = baseDao.getFieldDataByCondition("RecBalance", "rb_statuscode", "rb_id=" + rb_id);
		if (status.equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.post_onlyUnPost"));
		}
		if ("RecBalance!PBIL".equals(caller) || "RecBalance!IMRE".equals(caller) || "RecBalance!ARRM".equals(caller)) {
			baseDao.execute("update RecBalance set rb_aramount=round((select NVL(sum(rbd_nowbalance),0) from RecBalanceDetail where nvl(rbd_ordercode,' ')<>' ' and rbd_rbid=rb_id),2) where rb_id="
					+ rb_id);
		}
		baseDao.execute("update recbalance set RB_SELLERCODE=(select max(em_code) from employee where RB_SELLER=em_name) where rb_id= "
				+ rb_id + " and not exists (select 1 from employee where em_code=RB_SELLERCODE and em_name=RB_SELLER)");
		// checkLimit( rb_id );
		if ("RecBalance!RRCW".equals(caller)) {
			SqlRowList billcode = baseDao.queryForRowSet(
					"select rbap_ordercode from RECBALANCEAP where rbap_rbid=? and nvl(rbap_ordercode,' ')<>' '", rb_id);
			while (billcode.next()) {
				baseDao.procedure("SP_APLOCKAMOUNT2", new Object[] { billcode.getObject("rbap_ordercode") });
			}
		}
		if ("RecBalance!ARRM".equals(caller)) {
			int count = baseDao.getCount("SELECT count(distinct ab_sendtype) from recbalancedetail,arbill where rbd_rbid=" + rb_id
					+ " and AB_CODE=rbd_ordercode");
			if (count > 1) {
				BaseUtil.showError("发票类型不同的发票在同一张应收款转销单，不能过账！");
			}
		}
		if ("RecBalance!PTAR".equals(caller)) {
			SqlRowList rs = baseDao
					.queryForRowSet(
							"select rbpd_ordercode,round(nvl(rbpd_nowbalance,0),2) rbpd_nowbalance,"
									+ "rbpd_detno,pr_id,pr_kind,pr_statuscode,round(CASE WHEN pr_kind='预收退款单' THEN -1 ELSE 1 END *nvl(pr_jsamount,0),2) pr_jsamount,round(nvl(pr_havebalance,0),2) pr_havebalance "
									+ "from RecBalancePrDetail left join (select pr_id,pr_kind,pr_statuscode,pr_jsamount,pr_havebalance from PreRec "
									+ "where (pr_kind='预收款' or pr_kind='初始化' or pr_kind='预收退款单'))  ON rbpd_sourceid = pr_id  where rbpd_rbid = ?",
							rb_id);
			while (rs.next()) {
				if (rs.getGeneralInt("pr_id") == 0) {
					BaseUtil.showError("序号[" + rs.getGeneralInt("rbpd_detno") + "]预收款或预收退款单[" + rs.getString("rbpd_ordercode")
							+ "]不存在，不能过账！");
				}
				if (!"POSTED".equals(rs.getString("pr_statuscode"))) {
					if ("预收款".equals(rs.getString("pr_kind"))) {
						BaseUtil.showError("序号[" + rs.getGeneralInt("rbpd_detno") + "]预收款单[" + rs.getString("rbpd_ordercode")
								+ "]未过账，不能过账！");
					} else if ("预收退款单".equals(rs.getString("pr_kind"))) {
						BaseUtil.showError("序号[" + rs.getGeneralInt("rbpd_detno") + "]预收退款单[" + rs.getString("rbpd_ordercode")
								+ "]未过账，不能过账！");
					}
				}

				double nowbalance = rs.getGeneralDouble("rbpd_nowbalance");
				double havebalance = rs.getGeneralDouble("pr_havebalance");
				double jsamount = rs.getGeneralDouble("pr_jsamount");
				if (NumberUtil.compare(jsamount, (nowbalance + havebalance), 2) == -1) {
					if ("预收款".equals(rs.getString("pr_kind"))) {
						BaseUtil.showError("序号[" + rs.getGeneralInt("rbpd_detno") + "]超预收款单[" + rs.getString("rbpd_ordercode")
								+ "]的预收金额，不能过账！");
					} else if ("预收退款单".equals(rs.getString("pr_kind"))) {
						BaseUtil.showError("序号[" + rs.getGeneralInt("rbpd_detno") + "]超预收退款单[" + rs.getString("rbpd_ordercode")
								+ "]的预收退款金额，不能过账！");
					}
				}

			}
		}
		// 过账前的其它逻辑
		handlerService.beforePost(caller, rb_id);
		checkAss(rb_id);
		// 执行过账操作
		Object obj = baseDao.getFieldDataByCondition("RecBalance", "rb_code", "rb_id=" + rb_id);
		// 存储过程
		String res = baseDao.callProcedure("Sp_CommiteRec", new Object[] { obj });
		if (res.trim().equals("ok")) { // recBalance过账成功
			// 不要删
			if (caller.equals("RecBalance!RRCW")) { // 应收冲应付
				// select pb_code from RecBalance where pb_sourceid=v_rb_id and
				// pb_kind='应收冲应付'
				Object pbcode = baseDao.getFieldDataByCondition("PayBalance", "pb_code", "pb_kind='应收冲应付' and pb_sourceid='" + rb_id + "'");
				if (pbcode != null && !pbcode.equals("")) { // pb_code 存在
					String pb_post_str = baseDao.callProcedure("Sp_CommitePay", new Object[] { pbcode });
					if (pb_post_str.trim().equals("ok") || pb_post_str == null) { // RecBalance
						// 改变RecBalance 状态
						Object pbid = baseDao.getFieldDataByCondition("PayBalance", "pb_id", "pb_code='" + pbcode + "'");
						baseDao.updateByCondition("PayBalance", "pb_statuscode='POSTED',pb_status='" + BaseUtil.getLocalMessage("POSTED")
								+ "'", "pb_id=" + pbid);
						baseDao.updateByCondition("PayBalanceDetail", "pbd_status=99,pbd_statuscode='POSTED'", "pbd_pbid=" + pbid);
						// 记录操作
						baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.post"),
								BaseUtil.getLocalMessage("msg.postSuccess"), caller + "|pb_id=" + pbid));
						// 改变RecBalance 状态
						baseDao.updateByCondition("RecBalance", "rb_statuscode='POSTED',rb_status='" + BaseUtil.getLocalMessage("POSTED")
								+ "'", "rb_id=" + rb_id);
						baseDao.updateByCondition("RecBalanceDetail", "rbd_status=99,rbd_statuscode='POSTED'", "rbd_rbid=" + rb_id);
						// 记录操作
						baseDao.logger.post(caller, "rb_id", rb_id);
					} else { // RecBalance 反过账不成功 对recbalance 进行反过账
						Object pbid = baseDao.getFieldDataByCondition("PayBalance", "pb_id", "pb_code='" + pbcode + "'");
						baseDao.deleteByCondition("PayBalanceDetail", "pbd_pbid='" + pbid + "'");
						baseDao.deleteByCondition("PayBalance", "pb_id='" + pbid + "'");
						String respost_rb_str1 = baseDao.callProcedure("Sp_UnCommiteRec", new Object[] { obj });
						if (respost_rb_str1.trim().equals("ok")) { // 反过账recbalance
							// BaseUtil.showError("recbalance反过账未成功,过账失败!");
							baseDao.updateByCondition("RecBalance", "rb_auditstatuscode='ENTERING',rb_statuscode='UNPOST',rb_auditstatus='"
									+ BaseUtil.getLocalMessage("ENTERING") + "',rb_status='" + BaseUtil.getLocalMessage("UNPOST") + "'",
									"rb_id=" + rb_id);
							baseDao.updateByCondition("RecBalanceDetail", "rbd_status=0,rbd_statuscode='ENTERING'", "rbd_rbid=" + rb_id);
						} else {
							// BaseUtil.showError("recbalance反过账未成功,过账失败!");
						}
						BaseUtil.showError((respost_rb_str1.equals("ok") ? "" : respost_rb_str1)
								+ (pb_post_str.equals("ok") ? "" : pb_post_str));
					}
				} else { // pb_code 不存在 对recbalance 进行反过账
					String respost_rb_str2 = baseDao.callProcedure("Sp_UnCommiteRec", new Object[] { obj });
					if (respost_rb_str2.trim().equals("ok")) { // 反过账recbalance
						BaseUtil.showError("付款单过账不成功");
					} else {
						BaseUtil.showError(respost_rb_str2);
					}
				}
			} else { // 其他
				baseDao.updateByCondition("RecBalance", "rb_statuscode='POSTED',rb_status='" + BaseUtil.getLocalMessage("POSTED") + "'",
						"rb_id=" + rb_id);
				baseDao.updateByCondition("RecBalanceDetail", "rbd_status=99,rbd_statuscode='POSTED'", "rbd_rbid=" + rb_id);
				// 记录操作
				baseDao.logger.post(caller, "rb_id", rb_id);
			}
		} else {
			BaseUtil.showError(res);
		}
		// 更新来源支票收款状态
		Object source = baseDao.getFieldDataByCondition("RecBalance", "rb_sourceid", "rb_id=" + rb_id
				+ " and rb_source='Bank' and rb_kind='收款单'");
		if (source != null) {
			baseDao.execute("update billarcheque set bar_settleamount=bar_doublebalance,bar_leftamount=0,bar_nowstatus='已收款' where exists (select 1 from accountregister where bar_id=ar_sourceid and ar_sourcetype=bar_kind and ar_id="
					+ source + ")");
		}
		// 过账之后计算锁定金额
		if ("RecBalance!RRCW".equals(caller)) {
			SqlRowList billcode = baseDao.queryForRowSet(
					"select rbap_ordercode from RECBALANCEAP where rbap_rbid=? and nvl(rbap_ordercode,' ')<>' '", rb_id);
			while (billcode.next()) {
				baseDao.procedure("SP_APLOCKAMOUNT2", new Object[] { billcode.getObject("rbap_ordercode") });
			}
		}
		// 执行过账后的其它逻辑
		handlerService.afterPost(caller, rb_id);
	}

	@Override
	@Transactional
	public void resPostRecBalance(String caller, int rb_id) {
		Object status = baseDao.getFieldDataByCondition("RecBalance", "rb_statuscode", "rb_id=" + rb_id);
		if (!status.equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resPost_onlyPost"));
		}
		checkVoucher(rb_id);
		// 过账前的其它逻辑
		handlerService.beforeResPost(caller, rb_id);
		// 执行过账操作
		Object obj = baseDao.getFieldDataByCondition("RecBalance", "rb_code", "rb_id=" + rb_id);
		if (caller.equals("RecBalance!RRCW")) {// 应收冲应付
			// 查找PayBalance对应的应收冲应付单据
			Object[] pb = baseDao.getFieldsDataByCondition("PayBalance", new String[] { "pb_code", "pb_statuscode", "pb_id" },
					"pb_kind='应收冲应付' and pb_sourceid=" + rb_id);
			if (pb != null && pb[0] != null) {
				Object pbcode = pb[0];
				Object pbid = pb[2];
				if ("POSTED".equals(pb[1])) {
					// 反过账PayBalance
					String respost_pb = baseDao.callProcedure("Sp_UnCommitePay", new Object[] { pbcode });
					if (respost_pb == null || "OK".equals(respost_pb.toUpperCase())) {
						baseDao.updateByCondition("PayBalance", "pb_auditstatuscode='ENTERING',pb_statuscode='UNPOST',pb_auditstatus='"
								+ BaseUtil.getLocalMessage("ENTERING") + "',pb_status='" + BaseUtil.getLocalMessage("UNPOST") + "'",
								"pb_id=" + pbid);
						baseDao.updateByCondition("PayBalanceDetail", "pbd_status=0,pbd_statuscode='ENTERING'", "pbd_pbid=" + pbid);
						// 记录操作
						baseDao.logger.resPost(caller, "pb_id", pbid);
					} else {
						// 反过账不成功 (报错PayBalance反过账失败)
						BaseUtil.showError(respost_pb);
					}
				}
				// 反过账Recbalance
				String respost_rb = baseDao.callProcedure("Sp_UnCommiteRec", new Object[] { obj });
				// 反过账成功 (改变recbalance 状态 删除对应的PayBalance)
				if (respost_rb == null || "OK".equals(respost_rb.toUpperCase())) {
					baseDao.updateByCondition("RecBalance", "rb_auditstatuscode='ENTERING',rb_statuscode='UNPOST',rb_auditstatus='"
							+ BaseUtil.getLocalMessage("ENTERING") + "',rb_status='" + BaseUtil.getLocalMessage("UNPOST") + "'", "rb_id="
							+ rb_id);
					baseDao.updateByCondition("RecBalanceDetail", "rbd_status=0,rbd_statuscode='ENTERING'", "rbd_rbid=" + rb_id);
					// 记录操作
					baseDao.logger.resPost(caller, "rb_id", rb_id);
					baseDao.deleteByCondition("PayBalanceDetail", "pbd_pbid='" + pbid + "'");
					baseDao.deleteByCondition("PayBalance", "pb_id='" + pbid + "'");
				} else {
					// 反过账失败(报错recbalance 反过账失败 RecBalance 过账 )
					BaseUtil.showError(BaseUtil.getLocalMessage("fa.ars.recbalance.resPostError1"));
					String post_pb = baseDao.callProcedure("Sp_CommitePay", new Object[] { pbcode });
					// 过账RecBalance
					if (post_pb == null || "OK".equals(post_pb.toUpperCase())) {
						baseDao.updateByCondition("PayBalance", "pb_statuscode='POSTED',pb_status='" + BaseUtil.getLocalMessage("POSTED")
								+ "'", "pb_id=" + pbid);
						baseDao.updateByCondition("PayBalanceDetail", "pbd_status=99,pbd_statuscode='POSTED'", "pbd_pbid=" + pbid);
						// 记录操作
						baseDao.logger.post(caller, "pb_id", pbid);
					} else {
						BaseUtil.showError(post_pb);
					}
				}
			} else {// RecBalance单号不存在
				// 报错 对应的应收冲应付单不存在
				BaseUtil.showError(BaseUtil.getLocalMessage("fa.ars.recbalance.resPostError2"));
			}
		} else {// 其他
			// 存储过程
			String res = baseDao.callProcedure("Sp_UnCommiteRec", new Object[] { obj });
			if (res == null || "OK".equals(res.toUpperCase())) {
				baseDao.updateByCondition("RecBalance",
						"rb_auditstatuscode='ENTERING',rb_statuscode='UNPOST',rb_auditstatus='" + BaseUtil.getLocalMessage("ENTERING")
								+ "',rb_status='" + BaseUtil.getLocalMessage("UNPOST") + "'", "rb_id=" + rb_id);
				baseDao.updateByCondition("RecBalanceDetail", "rbd_status=0,rbd_statuscode='ENTERING'", "rbd_rbid=" + rb_id);
				// 记录操作
				baseDao.logger.resPost(caller, "rb_id", rb_id);
			} else {
				BaseUtil.showError(res);
			}
		}
		// 更新来源支票的收款状态
		Object source = baseDao.getFieldDataByCondition("RecBalance", "rb_sourceid", "rb_id=" + rb_id
				+ " and rb_source='Bank' and rb_kind='收款单'");
		if (source != null) {
			baseDao.execute("update billarcheque set bar_settleamount=0,bar_leftamount=bar_doublebalance,bar_nowstatus='未收款' where exists (select 1 from accountregister where bar_id=ar_sourceid and ar_sourcetype=bar_kind and ar_id="
					+ source + ")");
		}
		// 反过账之后计算锁定金额
		if ("RecBalance!RRCW".equals(caller)) {
			SqlRowList billcode = baseDao.queryForRowSet(
					"select rbap_ordercode from RECBALANCEAP where rbap_rbid=? and nvl(rbap_ordercode,' ')<>' '", rb_id);
			while (billcode.next()) {
				baseDao.procedure("SP_APLOCKAMOUNT2", new Object[] { billcode.getObject("rbap_ordercode") });
			}
		}
		// 执行过账后的其它逻辑
		handlerService.afterResPost(caller, rb_id);
	}

	@Override
	public void saveRecBalancePRDetail(String caller, String formStore, String gridStore1, String gridStore2, String assStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore1 = BaseUtil.parseGridStoreToMaps(gridStore1);
		List<Map<Object, Object>> gstore2 = BaseUtil.parseGridStoreToMaps(gridStore2);
		List<Map<Object, Object>> ass = BaseUtil.parseGridStoreToMaps(assStore);
		List<List<Map<Object, Object>>> gstore = new ArrayList<List<Map<Object, Object>>>();
		gstore.add(gstore1);
		gstore.add(gstore2);
		handlerService.beforeSave(caller, new Object[] { store, gstore1, ass, gstore2 });
		store.put("rb_statuscode", "UNPOST");
		store.put("rb_status", BaseUtil.getLocalMessage("UNPOST"));
		store.put("rb_printstatuscode", "UNPRINT");
		store.put("rb_printstatus", BaseUtil.getLocalMessage("UNPRINT"));
		store.put("rb_auditstatuscode", "ENTERING");
		store.put("rb_auditstatus", BaseUtil.getLocalMessage("ENTERING"));
		store.put("rb_strikestatuscode", "UNSTRIKE");
		store.put("rb_strikestatus", BaseUtil.getLocalMessage("UNSTRIKE"));
		// 保存RecBalance
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "RecBalance"));
		Map<Object, List<Map<Object, Object>>> list = BaseUtil.groupMap(ass, "dass_condid");
		int id;
		// 保存RecBalancePRDetail
		List<String> gridSql1 = SqlUtil.getInsertOrUpdateSql(gstore1, "RecBalancePRDetail", "rbpd_id");
		baseDao.execute(gridSql1);
		// 保存RecBalanceDetail
		for (Map<Object, Object> map : gstore2) {
			if (map.containsKey("ca_asstype")) {
				map.remove("ca_asstype");
			}
			if (map.containsKey("ca_assname")) {
				map.remove("ca_assname");
			}
			id = baseDao.getSeqId("RECBALANCEDETAIL_SEQ");
			ass = list.get(String.valueOf(map.get("rbd_id")));
			if (ass != null) {
				for (Map<Object, Object> m : ass) {// RecBalanceDetailAss
					m.put("dass_condid", id);
					m.put("dass_id", baseDao.getSeqId("RECBALANCEDETAILASS_SEQ"));
				}
				baseDao.execute(SqlUtil.getInsertSqlbyGridStore(ass, "RecBalanceDetailAss"));
			}
			map.put("rbd_id", id);
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(gstore2, "RecBalanceDetail"));
		baseDao.execute("update RecBalanceDetail set rbd_currency='RMB' where nvl(rbd_currency,' ')=' ' and rbd_rbid=?", store.get("rb_id"));
		int count = baseDao.getCount("select count(1) from custar where ca_custcode='" + store.get("rb_custcode") + "' and ca_currency='"
				+ store.get("rb_currency") + "'");
		if (count == 0) {
			baseDao.execute("insert into CUSTAR(CA_CUSTCODE,CA_CURRENCY,CA_AMOUNT) values ('" + store.get("rb_custcode") + "','"
					+ store.get("rb_currency") + "',0)");
		}
		baseDao.execute("update RecBalance set rb_beginlast=nvl((select nvl(ca_prepayamount,0) from custar where ca_custcode=rb_custcode and rb_currency=ca_currency),0) where rb_id="
				+ store.get("rb_id"));
		// 记录操作
		baseDao.logger.save(caller, "rb_id", store.get("rb_id"));
		handlerService.afterSave(caller, new Object[] { store, gstore1, ass, gstore2 });
	}

	@Override
	public void updateRecBalancePRDetailById(String caller, String formStore, String gridStore1, String gridStore2, String assStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore1 = BaseUtil.parseGridStoreToMaps(gridStore1);
		List<Map<Object, Object>> gstore2 = BaseUtil.parseGridStoreToMaps(gridStore2);
		List<Map<Object, Object>> ass = BaseUtil.parseGridStoreToMaps(assStore);
		List<List<Map<Object, Object>>> gstore = new ArrayList<List<Map<Object, Object>>>();
		gstore.add(gstore1);
		gstore.add(gstore2);
		Object status[] = baseDao.getFieldsDataByCondition("RecBalance", new String[] { "rb_auditstatuscode", "rb_statuscode" }, "rb_id="
				+ store.get("rb_id"));
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyUnPost"));
		}
		handlerService.beforeUpdate(caller, new Object[] { store, gstore1, ass, gstore2 });
		// 修改RecBalance
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "RecBalance", "rb_id"));
		// 保存RecBalancePRDetail
		List<String> gridSql1 = SqlUtil.getInsertOrUpdateSql(gstore1, "RecBalancePRDetail", "rbpd_id");
		baseDao.execute(gridSql1);
		// 修改RecBalanceDetail
		Map<Object, List<Map<Object, Object>>> list = BaseUtil.groupMap(ass, "dass_condid");
		int id;
		List<String> gridSql = null;
		for (Map<Object, Object> s : gstore2) {
			if (s.containsKey("ca_asstype")) {
				s.remove("ca_asstype");
			}
			if (s.containsKey("ca_assname")) {
				s.remove("ca_assname");
			}
		}
		if (gstore2.size() > 0) {
			gridSql = new ArrayList<String>();
			for (Map<Object, Object> s : gstore2) {
				if (s.get("rbd_id") == null || s.get("rbd_id").equals("") || s.get("rbd_id").equals("0")
						|| Integer.parseInt(s.get("rbd_id").toString()) <= 0) {
					id = baseDao.getSeqId("RECBALANCEDETAIL_SEQ");
					ass = list.get(String.valueOf(s.get("rbd_id")));
					if (ass != null) {
						for (Map<Object, Object> m : ass) {// VoucherDetailAss
							m.put("dass_condid", id);
							m.put("dass_id", baseDao.getSeqId("RECBALANCEDETAILASS_SEQ"));
						}
						baseDao.execute(SqlUtil.getInsertSqlbyGridStore(ass, "RecBalanceDetailAss"));
					}
					s.put("rbd_id", id);
					gridSql.add(SqlUtil.getInsertSqlByMap(s, "RecBalanceDetail"));
				} else {
					gridSql.add(SqlUtil.getUpdateSqlByFormStore(s, "RecBalanceDetail", "rbd_id"));

					id = Integer.parseInt(s.get("rbd_id").toString());
					ass = list.get(String.valueOf(id));
					if (ass != null) {
						List<String> sqls = SqlUtil.getUpdateSqlbyGridStore(ass, "RecBalanceDetailAss", "dass_id");
						for (Map<Object, Object> m : ass) {
							if (m.get("dass_id") == null || m.get("dass_id").equals("") || m.get("dass_id").equals("0")
									|| Integer.parseInt(m.get("dass_id").toString()) <= 0) {
								m.put("dass_id", baseDao.getSeqId("RECBALANCEDETAILASS_SEQ"));
								sqls.add(SqlUtil.getInsertSqlByMap(m, "RecBalanceDetailAss"));
							}
						}
						baseDao.execute(sqls);
					}
				}
			}
			baseDao.execute(gridSql);
		} else {
			Set<Object> items = list.keySet();
			for (Object i : items) {
				ass = list.get(String.valueOf(i));
				if (ass != null) {
					List<String> sqls = SqlUtil.getUpdateSqlbyGridStore(ass, "RecBalanceDetailAss", "dass_id");
					for (Map<Object, Object> m : ass) {
						if (m.get("dass_id") == null || m.get("dass_id").equals("") || m.get("dass_id").equals("0")
								|| Integer.parseInt(m.get("dass_id").toString()) <= 0) {
							m.put("dass_id", baseDao.getSeqId("RECBALANCEDETAILASS_SEQ"));
							sqls.add(SqlUtil.getInsertSqlByMap(m, "RecBalanceDetailAss"));
						}
					}
					baseDao.execute(sqls);
				}
			}
		}
		baseDao.execute("update RecBalanceDetail set rbd_currency='RMB' where nvl(rbd_currency,' ')=' ' and rbd_rbid=?", store.get("rb_id"));
		int count = baseDao.getCount("select count(1) from custar where ca_custcode='" + store.get("rb_custcode") + "' and ca_currency='"
				+ store.get("rb_currency") + "'");
		if (count == 0) {
			baseDao.execute("insert into CUSTAR(CA_CUSTCODE,CA_CURRENCY,CA_AMOUNT) values ('" + store.get("rb_custcode") + "','"
					+ store.get("rb_currency") + "',0)");
		}
		baseDao.execute("update RecBalance set rb_beginlast=nvl((select nvl(ca_prepayamount,0) from custar where ca_custcode=rb_custcode and rb_currency=ca_currency),0) where rb_id="
				+ store.get("rb_id"));
		// 记录操作
		baseDao.logger.update(caller, "rb_id", store.get("rb_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore1, ass, gstore2 });
	}

	@Override
	public void catchPR(String caller, String formStore, String startdate, String enddate) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int rb_id = Integer.parseInt(store.get("rb_id").toString());
		Object status[] = baseDao.getFieldsDataByCondition("RecBalance", new String[] { "rb_auditstatuscode", "rb_statuscode" }, "rb_id="
				+ store.get("rb_id"));
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyUnPost"));
		}
		startdate = startdate == null ? "1970-01-01" : startdate;
		enddate = enddate == null ? DateUtil.format(DateUtil.overDate(null, 100), Constant.YMD) : enddate;
		String res = baseDao.callProcedure("Ct_CatchPrToRb", new Object[] { rb_id, startdate, enddate });
		if (res.trim().equals("ok")) {
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.post"), BaseUtil
					.getLocalMessage("msg.saveSuccess"), caller + "|rb_id=" + rb_id));
		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public void cleanPR(String caller, String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int rb_id = Integer.parseInt(store.get("rb_id").toString());
		Object status[] = baseDao.getFieldsDataByCondition("RecBalance", new String[] { "rb_auditstatuscode", "rb_statuscode" }, "rb_id="
				+ rb_id);
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		baseDao.deleteByCondition("RecBalancePRDetail", "rbpd_rbid='" + rb_id + "'");
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.delete"), BaseUtil
				.getLocalMessage("msg.deleteSuccess"), caller + "|rbpd_rbid=" + rb_id));
	}

	@Override
	public void catchAB(String caller, String formStore, String startdate, String enddate, String bicode) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int rb_id = Integer.parseInt(store.get("rb_id").toString());
		Object status[] = baseDao.getFieldsDataByCondition("RecBalance", new String[] { "rb_auditstatuscode", "rb_statuscode" }, "rb_id="
				+ store.get("rb_id"));
		String res = "";
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyUnPost"));
		}
		startdate = startdate == null ? "1970-01-01" : startdate;
		enddate = enddate == null ? DateUtil.format(DateUtil.overDate(null, 100), Constant.YMD) : enddate;
		if (caller.equals("RecBalance!TK")) {
			res = baseDao.callProcedure("CT_CATCHABTORB_BACK", new Object[] { rb_id, startdate, enddate });
		} else {
			if (bicode == null || "".equals(bicode.trim())) {
				res = baseDao.callProcedure("CT_CATCHABTORB", new Object[] { rb_id, startdate, enddate });
			} else {
				for (String code : bicode.toString().trim().split("#")) {
					int count = 0;
					if (baseDao.isDBSetting("RecBalance!PBIL", "useAPCheck")) {
						count = baseDao.getCountByCondition("ARCheck left join ARCHECKDETAIL on ac_id = ad_acid", "ac_code='" + code + "'");
					}else {
						count = baseDao.getCountByCondition("BillOutDetail", "ard_code='" + code + "'");
					}
					if (count > 0) {
						String rs = baseDao.callProcedure("CT_CATCHABTORB_BR", new Object[] { rb_id, startdate, enddate, code });
						if (!rs.trim().equals("ok")) {
							BaseUtil.showError(rs);
						}
					} else {
						BaseUtil.showError("票据[" + code + "]没有发票明细！");
					}
				}
				res = "ok";
			}
		}
		baseDao.execute("update recbalance set rb_aramount =(select sum(rbd_nowbalance) from RecBalanceDetail where nvl(rbd_ordercode,' ') <>' ' and rbd_rbid="
				+ store.get("rb_id") + ") where rb_id=" + store.get("rb_id"));
		if (res.trim().equals("ok")) {
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.getBill"), BaseUtil
					.getLocalMessage("msg.getSuccess"), caller + "|rb_id=" + rb_id));
		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public void cleanAB(String caller, String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int rb_id = Integer.parseInt(store.get("rb_id").toString());
		Object status[] = baseDao.getFieldsDataByCondition("RecBalance", new String[] { "rb_auditstatuscode", "rb_statuscode" }, "rb_id="
				+ rb_id);
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		baseDao.deleteByCondition("RecBalanceDetail", "rbd_rbid='" + rb_id + "'");
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.delete"), BaseUtil
				.getLocalMessage("msg.deleteSuccess"), caller + "|rbd_rbid=" + rb_id));
	}

	/*
	 * gridStore1 --> RecBalanceApgridStore2 --> RecBalanceDetailgridAss1 -->
	 * RecBalanceApAssgridAss2 --> RecBalanceDetailAss (non-Javadoc)
	 * 
	 * @see
	 * com.uas.erp.service.fa.RecBalanceService#saveRecBalanceAP(java.lang.String
	 * , java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, com.uas.erp.model.Employee)
	 */
	@Override
	public void saveRecBalanceAP(String caller, String formStore, String gridStore1, String gridStore2, String gridAss1, String gridAss2) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore1 = BaseUtil.parseGridStoreToMaps(gridStore1);
		List<Map<Object, Object>> gstore2 = BaseUtil.parseGridStoreToMaps(gridStore2);
		List<Map<Object, Object>> ass1 = BaseUtil.parseGridStoreToMaps(gridAss1);
		List<Map<Object, Object>> ass2 = BaseUtil.parseGridStoreToMaps(gridAss2);
		// List<Map<Object, Object>> ass =
		// BaseUtil.parseGridStoreToMaps(assStore);
		List<List<Map<Object, Object>>> gstore = new ArrayList<List<Map<Object, Object>>>();
		gstore.add(gstore1);
		gstore.add(gstore2);
		handlerService.beforeSave(caller, new Object[] { store, gstore1, gstore2, ass1, ass2 });
		store.put("rb_statuscode", "UNPOST");
		store.put("rb_status", BaseUtil.getLocalMessage("UNPOST"));
		store.put("rb_printstatuscode", "UNPRINT");
		store.put("rb_printstatus", BaseUtil.getLocalMessage("UNPRINT"));
		store.put("rb_auditstatuscode", "ENTERING");
		store.put("rb_auditstatus", BaseUtil.getLocalMessage("ENTERING"));
		store.put("rb_strikestatuscode", "UNSTRIKE");
		store.put("rb_strikestatus", BaseUtil.getLocalMessage("UNSTRIKE"));
		// 保存RecBalance
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "RecBalance"));
		// Map<Object, List<Map<Object, Object>>> list = BaseUtil.groupMap(ass,
		// "dass_condid");
		int id;
		Map<Object, List<Map<Object, Object>>> list1 = BaseUtil.groupMap(ass1, "dass_condid");
		Map<Object, List<Map<Object, Object>>> list2 = BaseUtil.groupMap(ass2, "dass_condid");

		// 剔除需要保存在RecBalanceAp中的无用字段数据
		for (Map<Object, Object> map : gstore1) {
			if (map.containsKey("ca_asstype")) {
				map.remove("ca_asstype");
			}
			if (map.containsKey("ca_assname")) {
				map.remove("ca_assname");
			}
			id = baseDao.getSeqId("RecBalanceAP_SEQ");
			ass1 = list1.get(String.valueOf(map.get("rbap_id")));
			if (ass1 != null) {
				for (Map<Object, Object> m : ass1) {
					m.put("dass_condid", id);
					m.put("dass_id", baseDao.getSeqId("RecBalanceAPAss_SEQ"));
				}
				// 保存辅助核算 RecBalanceAPAss
				baseDao.execute(SqlUtil.getInsertSqlbyGridStore(ass1, "RecBalanceAPAss"));
			}
			map.put("rbap_id", id);
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(gstore1, "RecBalanceAP"));

		// 剔除需要保存在RecBalanceAp中的无用字段数据
		for (Map<Object, Object> map : gstore2) {
			if (map.containsKey("ca_asstype")) {
				map.remove("ca_asstype");
			}
			if (map.containsKey("ca_assname")) {
				map.remove("ca_assname");
			}
			id = baseDao.getSeqId("RecBalanceDetail_SEQ");
			ass2 = list2.get(String.valueOf(map.get("rbd_id")));
			if (ass2 != null) {
				for (Map<Object, Object> m : ass2) {
					m.put("dass_condid", id);
					m.put("dass_id", baseDao.getSeqId("RecBalanceDetailAss_SEQ"));
				}
				// 保存辅助核算 RecBalanceAPAss
				baseDao.execute(SqlUtil.getInsertSqlbyGridStore(ass2, "RecBalanceDetailAss"));
			}
			map.put("rbd_id", id);
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(gstore2, "RecBalanceDetail"));
		baseDao.execute("update recbalance set rb_aramount =(select sum(rbd_nowbalance) from RecBalanceDetail where nvl(rbd_ordercode,' ') <>' ' and rbd_rbid="
				+ store.get("rb_id") + ") where rb_id=" + store.get("rb_id"));
		baseDao.execute("update recbalance set rb_apamount =(select sum(rbap_nowbalance) from RecBalanceAP where nvl(rbap_ordercode,' ') <>' ' and rbap_rbid="
				+ store.get("rb_id") + ") where rb_id=" + store.get("rb_id"));
		// 记录操作
		baseDao.logger.save(caller, "rb_id", store.get("rb_id"));
		handlerService.beforeSave(caller, new Object[] { store, gstore1, gstore2, ass1, ass2 });
	}

	@Override
	public void updateRecBalanceAPById(String caller, String formStore, String gridStore1, String gridStore2, String gridAss1,
			String gridAss2) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore1 = BaseUtil.parseGridStoreToMaps(gridStore1);
		List<Map<Object, Object>> gstore2 = BaseUtil.parseGridStoreToMaps(gridStore2);
		List<Map<Object, Object>> ass1 = BaseUtil.parseGridStoreToMaps(gridAss1);
		List<Map<Object, Object>> ass2 = BaseUtil.parseGridStoreToMaps(gridAss2);
		List<List<Map<Object, Object>>> gstore = new ArrayList<List<Map<Object, Object>>>();
		gstore.add(gstore1);
		gstore.add(gstore2);
		Object status[] = baseDao.getFieldsDataByCondition("RecBalance", new String[] { "rb_auditstatuscode", "rb_statuscode" }, "rb_id="
				+ store.get("rb_id"));
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyUnPost"));
		}
		handlerService.beforeUpdate(caller, new Object[] { store, gstore1, gstore2, ass1, ass2 });
		// 修改RecBalance
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "RecBalance", "rb_id"));
		// 保存RecBalanceAP------------------------
		Map<Object, List<Map<Object, Object>>> list1 = BaseUtil.groupMap(ass1, "dass_condid");
		Map<Object, List<Map<Object, Object>>> list2 = BaseUtil.groupMap(ass2, "dass_condid");
		int id;
		if (gstore1.size() > 0) {
			List<String> gridSql = new ArrayList<String>();
			for (Map<Object, Object> s : gstore1) {
				if (s.containsKey("ca_asstype")) {
					s.remove("ca_asstype");
				}
				if (s.containsKey("ca_assname")) {
					s.remove("ca_assname");
				}
				if (s.get("rbap_id") == null || s.get("rbap_id").equals("") || s.get("rbap_id").equals("0")
						|| Integer.parseInt(s.get("rbap_id").toString()) <= 0) {
					id = baseDao.getSeqId("RECBALANCEAP_SEQ");
					ass1 = list1.get(String.valueOf(s.get("rbap_id")));
					if (ass1 != null) {
						for (Map<Object, Object> m : ass1) {// VoucherDetailAss
							m.put("dass_condid", id);
							m.put("dass_id", baseDao.getSeqId("RECBALANCEAPASS_SEQ"));
						}
						baseDao.execute(SqlUtil.getInsertSqlbyGridStore(ass1, "RecBalanceApAss"));
					}
					s.put("rbap_id", id);
					gridSql.add(SqlUtil.getInsertSqlByMap(s, "RecBalanceAP"));
				} else {
					gridSql.add(SqlUtil.getUpdateSqlByFormStore(s, "RecBalanceAP", "rbap_id"));
					id = Integer.parseInt(s.get("rbap_id").toString());
					ass1 = list1.get(String.valueOf(id));
					if (ass1 != null) {
						List<String> sqls = SqlUtil.getUpdateSqlbyGridStore(ass1, "RecBalanceAPAss", "dass_id");
						for (Map<Object, Object> m : ass1) {
							if (m.get("dass_id") == null || m.get("dass_id").equals("") || m.get("dass_id").equals("0")
									|| Integer.parseInt(m.get("dass_id").toString()) <= 0) {
								m.put("dass_id", baseDao.getSeqId("RECBALANCEAPASS_SEQ"));
								sqls.add(SqlUtil.getInsertSqlByMap(m, "RecBalanceApAss"));
							}
						}
						baseDao.execute(sqls);
					}
				}
			}
			baseDao.execute(gridSql);
		} else {
			Set<Object> items = list1.keySet();
			for (Object i : items) {
				ass1 = list1.get(String.valueOf(i));
				if (ass1 != null) {
					List<String> sqls = SqlUtil.getUpdateSqlbyGridStore(ass1, "RecBalanceApAss", "dass_id");
					for (Map<Object, Object> m : ass1) {
						if (m.get("dass_id") == null || m.get("dass_id").equals("") || m.get("dass_id").equals("0")
								|| Integer.parseInt(m.get("dass_id").toString()) <= 0) {
							m.put("dass_id", baseDao.getSeqId("RECBALANCEAPASS_SEQ"));
							sqls.add(SqlUtil.getInsertSqlByMap(m, "RecBalanceApAss"));
						}
					}
					baseDao.execute(sqls);
				}
			}
		}

		if (gstore2.size() > 0) {
			List<String> gridSql = new ArrayList<String>();
			for (Map<Object, Object> s : gstore2) {
				if (s.containsKey("ca_asstype")) {
					s.remove("ca_asstype");
				}
				if (s.containsKey("ca_assname")) {
					s.remove("ca_assname");
				}
				if (s.get("rbd_id") == null || s.get("rbd_id").equals("") || s.get("rbd_id").equals("0")
						|| Integer.parseInt(s.get("rbd_id").toString()) <= 0) {
					id = baseDao.getSeqId("RECBALANCEDETAIL_SEQ");
					ass2 = list2.get(String.valueOf(s.get("rbd_id")));
					if (ass2 != null) {
						for (Map<Object, Object> m : ass2) {// VoucherDetailAss
							m.put("dass_condid", id);
							m.put("dass_id", baseDao.getSeqId("RECBALANCEDETAILASS_SEQ"));
						}
						baseDao.execute(SqlUtil.getInsertSqlbyGridStore(ass2, "RecBalanceDetailAss"));
					}
					s.put("rbd_id", id);
					gridSql.add(SqlUtil.getInsertSqlByMap(s, "RecBalanceDetail"));
				} else {
					gridSql.add(SqlUtil.getUpdateSqlByFormStore(s, "RecBalanceDetail", "rbd_id"));
					id = Integer.parseInt(s.get("rbd_id").toString());
					ass2 = list2.get(String.valueOf(id));
					if (ass2 != null) {
						List<String> sqls = SqlUtil.getUpdateSqlbyGridStore(ass2, "RecBalanceDetailAss", "dass_id");
						for (Map<Object, Object> m : ass2) {
							if (m.get("dass_id") == null || m.get("dass_id").equals("") || m.get("dass_id").equals("0")
									|| Integer.parseInt(m.get("dass_id").toString()) <= 0) {
								m.put("dass_id", baseDao.getSeqId("RECBALANCEDETAILASS_SEQ"));
								sqls.add(SqlUtil.getInsertSqlByMap(m, "RecBalanceDetailAss"));
							}
						}
						baseDao.execute(sqls);
					}
				}
			}
			baseDao.execute(gridSql);
		} else {
			Set<Object> items = list2.keySet();
			for (Object i : items) {
				ass2 = list2.get(String.valueOf(i));
				if (ass2 != null) {
					List<String> sqls = SqlUtil.getUpdateSqlbyGridStore(ass2, "RecBalanceDetailAss", "dass_id");
					for (Map<Object, Object> m : ass2) {
						if (m.get("dass_id") == null || m.get("dass_id").equals("") || m.get("dass_id").equals("0")
								|| Integer.parseInt(m.get("dass_id").toString()) <= 0) {
							m.put("dass_id", baseDao.getSeqId("RECBALANCEDETAILASS_SEQ"));
							sqls.add(SqlUtil.getInsertSqlByMap(m, "RecBalanceDetailAss"));
						}
					}
					baseDao.execute(sqls);
				}
			}
		}
		baseDao.execute("update recbalance set rb_aramount =(select sum(rbd_nowbalance) from RecBalanceDetail where nvl(rbd_ordercode,' ') <>' ' and rbd_rbid="
				+ store.get("rb_id") + ") where rb_id=" + store.get("rb_id"));
		baseDao.execute("update recbalance set rb_apamount =(select sum(rbap_nowbalance) from RecBalanceAP where nvl(rbap_ordercode,' ') <>' ' and rbap_rbid="
				+ store.get("rb_id") + ") where rb_id=" + store.get("rb_id"));
		// 记录操作
		baseDao.logger.update(caller, "rb_id", store.get("rb_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore1, gstore2, ass1, ass2 });
	}

	@Override
	public void catchAP(String caller, String formStore, String startdate, String enddate) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int rb_id = Integer.parseInt(store.get("rb_id").toString());
		Object status[] = baseDao.getFieldsDataByCondition("RecBalance", new String[] { "rb_auditstatuscode", "rb_statuscode" }, "rb_id="
				+ store.get("rb_id"));
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyUnPost"));
		}
		startdate = startdate == null ? "1970-01-01" : startdate;
		enddate = enddate == null ? DateUtil.format(DateUtil.overDate(null, 100), Constant.YMD) : enddate;
		String res = baseDao.callProcedure("CT_CATCHAPTORB", new Object[] { rb_id, startdate, enddate });
		baseDao.execute("update recbalance set rb_aramount =(select sum(rbd_nowbalance) from RecBalanceDetail where nvl(rbd_ordercode,' ') <>' ' and rbd_rbid="
				+ rb_id + ") where rb_id=" + rb_id);
		baseDao.execute("update recbalance set rb_apamount =(select sum(rbap_nowbalance) from RecBalanceAP where nvl(rbap_ordercode,' ') <>' ' and rbap_rbid="
				+ rb_id + ") where rb_id=" + rb_id);
		if (res.trim().equals("ok")) {
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.getARBill"), BaseUtil
					.getLocalMessage("msg.getSuccess"), caller + "|rb_id=" + rb_id));
		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public void cleanAP(String caller, String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int rb_id = Integer.parseInt(store.get("rb_id").toString());
		Object status[] = baseDao.getFieldsDataByCondition("RecBalance", new String[] { "rb_auditstatuscode", "rb_statuscode" }, "rb_id="
				+ rb_id);
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		baseDao.execute("update recbalance set rb_aramount =0,rb_apamount =0 where rb_id=" + rb_id);
		// baseDao.execute("update recbalance set rb_apamount =0 where rb_id=" +
		// rb_id);
		baseDao.deleteByCondition("RecBalanceAP", "rbap_rbid=" + rb_id);
		baseDao.logger.others("清除应付发票", "清除成功", caller, "rb_id", rb_id);
	}

	@Override
	public void catchAR(String caller, String formStore, String startdate, String enddate) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int rb_id = Integer.parseInt(store.get("rb_id").toString());
		Object status[] = baseDao.getFieldsDataByCondition("RecBalance", new String[] { "rb_auditstatuscode", "rb_statuscode" }, "rb_id="
				+ store.get("rb_id"));
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyUnPost"));
		}
		startdate = startdate == null ? "1970-01-01" : startdate;
		enddate = enddate == null ? DateUtil.format(DateUtil.overDate(null, 100), Constant.YMD) : enddate;
		String res = baseDao.callProcedure("Ct_CatchAbToRb", new Object[] { rb_id, startdate, enddate });
		baseDao.execute("update recbalance set rb_aramount =(select sum(rbd_nowbalance) from RecBalanceDetail where nvl(rbd_ordercode,' ') <>' ' and rbd_rbid="
				+ rb_id + ") where rb_id=" + rb_id);
		baseDao.execute("update recbalance set rb_apamount =(select sum(rbap_nowbalance) from RecBalanceAP where nvl(rbap_ordercode,' ') <>' ' and rbap_rbid="
				+ rb_id + ") where rb_id=" + rb_id);
		if (res.trim().equals("ok")) {
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.getARBill"), BaseUtil
					.getLocalMessage("msg.getSuccess"), caller + "|rb_id=" + rb_id));
		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public void cleanAR(String caller, String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int rb_id = Integer.parseInt(store.get("rb_id").toString());
		Object status[] = baseDao.getFieldsDataByCondition("RecBalance", new String[] { "rb_auditstatuscode", "rb_statuscode" }, "rb_id="
				+ rb_id);
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		baseDao.execute("update recbalance set rb_aramount =0 where rb_id=" + rb_id);
		baseDao.execute("update recbalance set rb_apamount =0 where rb_id=" + rb_id);
		baseDao.deleteByCondition("RecBalanceDetail", "rbd_rbid=" + rb_id);
		baseDao.logger.others("清除应收发票", "清除成功", caller, "rb_id", rb_id);
	}

	@Override
	public List<?> getPreRec(String custcode, String currency) {
		String sql = "SELECT PR_ID, PR_CODE, PR_DATE, PR_CUSTCODE, PR_CURRENCY, PR_JSAMOUNT, PR_HAVEBALANCE, PR_JSAMOUNT-NVL(PR_HAVEBALANCE,0) PR_THISAMOUNT, PR_KIND, PR_ORDERCODE "
				+ "FROM (select pr_id,pr_code,pr_date,pr_kind,pr_custcode, pr_cmcurrency pr_currency,case when pr_kind='预收退款单' then 0-nvl(pr_jsamount,0) else pr_jsamount end pr_jsamount,"
				+ "case when pr_kind='预收退款单' then 0-nvl(pr_havebalance,0) else pr_havebalance end pr_havebalance, pr_ordercode "
				+ "from PreRec full join (select pr_code pr_code1, WMSYS.WM_CONCAT(prd_ordercode) pr_ordercode "
				+ "from PreRec,PreRecDetail where pr_id=prd_prid and abs(nvl(pr_jsamount,0))>abs(nvl(pr_havebalance,0)) group by pr_code) on pr_code1=pr_code "
				+ "WHERE ((pr_kind='预收款' or pr_kind='初始化' or pr_kind='预收退款单') and pr_statuscode='POSTED' and pr_custcode=? and pr_cmcurrency=?) AND abs(nvl(pr_jsamount,0))<>abs(nvl(pr_havebalance,0)))";
		SqlRowList list = baseDao.queryForRowSet(sql, custcode, currency);
		return list.getResultList();
	}

	@Override
	public List<?> getARBill(String custcode, String currency) {
		String sql = null;
		if (baseDao.isDBSetting("useBillOutAR")) {
			sql = "SELECT AB_ID, AB_CODE, AB_DATE, AB_CLASS, AB_CUSTCODE, AB_CURRENCY, AB_ARAMOUNT, AB_PAYAMOUNT, AB_ORDERCODE,AB_SENDTYPE, AB_ARAMOUNT-nvl(AB_PAYAMOUNT,0) AB_THISAMOUNT, AB_INVOAMOUNT, AB_INVOSTATUS "
					+ "FROM (select ab_id,ab_code,ab_date,ab_class,ab_custcode, ab_currency,ab_aramount,ab_payamount,ab_ordercode,ab_sendtype, ab_invoamount, ab_invostatus "
					+ "from ARBill full join (select ab_code ab_code1,WMSYS.WM_CONCAT(abd_ordercode) ab_ordercode "
					+ "from ARBill,ARBillDetail where ab_id=abd_abid and abs(nvl(ab_aramount,0))>abs(nvl(ab_payamount,0)) group by ab_code) on ab_code1=ab_code "
					+ "WHERE ((ab_class='应收发票' or ab_class='初始化' or ab_class='应收款转销' or ab_class='其它应收单') and ab_statuscode='POSTED' and ab_custcode=? and ab_currency=?) "
					+ "and nvl(ab_invoamount,0)<>0 and abs(nvl(ab_aramount,0))<>abs(nvl(ab_payamount,0)))";
		} else {
			sql = "SELECT AB_ID, AB_CODE, AB_DATE, AB_CLASS, AB_CUSTCODE, AB_CURRENCY, AB_ARAMOUNT, AB_PAYAMOUNT, AB_ORDERCODE,AB_SENDTYPE, AB_ARAMOUNT-nvl(AB_PAYAMOUNT,0) AB_THISAMOUNT, AB_INVOAMOUNT, AB_INVOSTATUS  "
					+ "FROM (select ab_id,ab_code,ab_date,ab_class,ab_custcode, ab_currency,ab_aramount,ab_payamount,ab_ordercode,ab_sendtype, ab_invoamount, ab_invostatus "
					+ "from ARBill full join (select ab_code ab_code1,WMSYS.WM_CONCAT(abd_ordercode) ab_ordercode "
					+ "from ARBill,ARBillDetail where ab_id=abd_abid and abs(nvl(ab_aramount,0))>abs(nvl(ab_payamount,0)) group by ab_code) on ab_code1=ab_code "
					+ "WHERE ((ab_class='应收发票' or ab_class='初始化' or ab_class='应收款转销' or ab_class='其它应收单') and ab_statuscode='POSTED' and ab_custcode=? and ab_currency=?) AND abs(nvl(ab_aramount,0))<>abs(nvl(ab_payamount,0)))";
		}
		SqlRowList list = baseDao.queryForRowSet(sql, custcode, currency);
		return list.getResultList();
	}
}
