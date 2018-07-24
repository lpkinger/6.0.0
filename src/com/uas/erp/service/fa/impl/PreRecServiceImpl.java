package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.fa.PreRecService;

@Service("preRecService")
public class PreRecServiceImpl implements PreRecService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void savePreRec(String caller, String formStore, String gridStore, String assStore, String assMainStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> ass = BaseUtil.parseGridStoreToMaps(assStore);
		List<Map<Object, Object>> assMain = BaseUtil.parseGridStoreToMaps(assMainStore);
		handlerService.beforeSave(caller, new Object[] { store, grid, ass, assMain });

		// 删除不是主表字段
		if (store.containsKey("ca_asstype")) {
			store.remove("ca_asstype");
		}
		if (store.containsKey("ca_assname")) {
			store.remove("ca_assname");
		}

		// checkNowbalance( store.get("pr_id"));

		// 主表form中添加的默认信息
		store.put("pr_statuscode", "UNPOST");
		store.put("pr_status", BaseUtil.getLocalMessage("UNPOST"));
		store.put("pr_printstatuscode", "UNPRINT");
		store.put("pr_printstatus", BaseUtil.getLocalMessage("UNPRINT"));
		int pr_id = Integer.parseInt(store.get("pr_id").toString());
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "PreRec"));

		// 主表辅助核算保存S
		for (Map<Object, Object> am : assMain) {
			am.put("ass_conid", pr_id);
			am.put("ass_id", baseDao.getSeqId("PRERECASS_SEQ"));

		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(assMain, "PreRecAss"));
		// 主表辅助核算保存O
		Map<Object, List<Map<Object, Object>>> list = BaseUtil.groupMap(ass, "dass_condid");
		int id;
		for (Map<Object, Object> map : grid) {
			if (map.containsKey("sa_prepayamount")) {
				map.remove("sa_prepayamount");
			}
			if (map.containsKey("ca_asstype")) {
				map.remove("ca_asstype");
			}
			if (map.containsKey("ca_assname")) {
				map.remove("ca_assname");
			}

			id = baseDao.getSeqId("PRERECDETAIL_SEQ");
			ass = list.get(String.valueOf(map.get("prd_id")));
			if (ass != null) {
				for (Map<Object, Object> m : ass) {// PreRecDetailAss
					m.put("dass_condid", id);
					m.put("dass_id", baseDao.getSeqId("PRERECDETAILASS_SEQ"));
				}
				baseDao.execute(SqlUtil.getInsertSqlbyGridStore(ass, "PreRecDetailAss"));
			}
			map.put("prd_id", id);
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(grid, "PreRecDetail"));
		baseDao.execute("update PreRecDetail set PRD_CODE=(select pr_code from PreRec where prd_prid=pr_id) where prd_prid=" + pr_id
				+ " and not exists (select 1 from PreRec where PRD_CODE=pr_code)");
		// 记录操作
		baseDao.logger.save(caller, "pr_id", pr_id);
		handlerService.afterSave(caller, new Object[] { store, grid, ass, assMain });
	}

	void checkVoucher(Object id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pr_vouchercode) from PreRec where pr_id=? and nvl(pr_vouchercode,' ') <>' ' and pr_vouchercode<>'UNNEED'",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("已有凭证，不允许进行当前操作!凭证编号：" + dets);
		}
	}

	private void checkAss(int pr_id) {
		baseDao.execute(
				"delete from PreRecass where ASS_ID in (select ASS_ID from PreRec left join PreRecass on ASS_CONID=pr_id left join category on ca_code=pr_accountcode where pr_id=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ASS_ASSNAME)=0)",
				pr_id);
		baseDao.execute(
				"delete from PreRecass where ASS_CONID in (select pr_id from PreRec left join category on ca_code=pr_accountcode where pr_id=? and nvl(ca_asstype,' ')=' ')",
				pr_id);
		baseDao.execute(
				"delete from PreRecdetailass where DASS_ID in (select DASS_ID from PreRecdetail left join PreRecdetailass on DASS_CONDID=prd_id left join category on ca_code=prd_catecode where prd_prid=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,DASS_ASSNAME)=0)",
				pr_id);
		baseDao.execute(
				"delete from PreRecdetailass where DASS_CONDID in (select prd_id from PreRec left join PreRecdetail on prd_prid=pr_id left join category on ca_code=prd_catecode where pr_id=? and nvl(ca_asstype,' ')=' ')",
				pr_id);
		// 辅助核算不完善
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(pr_code) from PreRec left join PreRecass on ASS_CONID=pr_id left join category on ca_code=pr_accountcode where pr_id=? and nvl(ca_assname,' ')<>' ' and (nvl(ASS_ASSTYPE,' ')=' ' or nvl(ASS_CODEFIELD,' ')=' ' or nvl(ASS_NAMEFIELD,' ')=' ') order by pr_id",
						String.class, pr_id);
		if (dets != null) {
			BaseUtil.showError("主表辅助核算不完善，不允许进行当前操作!");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(prd_detno) from PreRecdetail left join PreRecdetailass on DASS_CONDID=prd_id left join category on ca_code=prd_catecode where prd_prid=? and nvl(ca_assname,' ')<>' ' and (nvl(DASS_ASSTYPE,' ')=' ' or nvl(DASS_ASSNAME,' ')=' ' or nvl(DASS_CODEFIELD,' ')=' ') order by prd_detno",
						String.class, pr_id);
		if (dets != null) {
			BaseUtil.showError("明细行辅助核算不完善，不允许进行当前操作!行号：" + dets);
		}
		// 核算项重复
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(pr_code) from (select count(1) c,pr_code,ASS_ASSTYPE from PreRec left join PreRecass on ASS_CONID=pr_id where pr_id=? and nvl(ASS_ASSTYPE,' ')<>' ' group by pr_code,ASS_ASSTYPE) where c>1 order by pr_code",
						String.class, pr_id);
		if (dets != null) {
			BaseUtil.showError("主表辅助核算核算项重复，不允许进行当前操作!");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(prd_detno) from (select count(1) c,prd_detno,DASS_ASSTYPE from PreRecdetail left join PreRecdetailass on DASS_CONDID=prd_id where prd_prid=? and nvl(DASS_ASSTYPE,' ')<>' ' group by prd_detno,DASS_ASSTYPE) where c>1 order by prd_detno",
						String.class, pr_id);
		if (dets != null) {
			BaseUtil.showError("明细行辅助核算核算项重复，不允许进行当前操作!行号：" + dets);
		}
		// 核算项错误
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(pr_code) from PreRec left join PreRecass on ASS_CONID=pr_id left join category on ca_code=pr_accountcode where pr_id=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ASS_ASSNAME)=0 order by pr_code",
						String.class, pr_id);
		if (dets != null) {
			BaseUtil.showError("主表核算项错误，不允许进行当前操作!");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(prd_detno) from PreRecdetail left join PreRecdetailass on DASS_CONDID=prd_id left join category on ca_code=prd_catecode where prd_prid=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,DASS_ASSNAME)=0 order by prd_detno",
						String.class, pr_id);
		if (dets != null) {
			BaseUtil.showError("明细行核算项错误，不允许进行当前操作!行号：" + dets);
		}
		// 核算项不存在
		String str = "";
		StringBuffer error = new StringBuffer();
		SqlRowList rs1 = baseDao
				.queryForRowSet(
						"select 'select '||prd_detno||',count(1) from '||ak_table||' where '||ak_asscode||'='''||DASS_CODEFIELD||''' and '||AK_ASSNAME||'='''||DASS_NAMEFIELD||'''' from PreRecdetailass left join asskind on DASS_ASSNAME=ak_name left join PreRecdetail on DASS_CONDID=prd_id where prd_prid=? order by prd_detno",
						pr_id);
		while (rs1.next()) {
			SqlRowList rd = baseDao.queryForRowSet(rs1.getString(1));
			if (rd.next() && rd.getInt(2) == 0) {
				if (StringUtil.hasText(str))
					str = str + ",";
				str += rd.getInt(1);
			}
		}
		if (str.length() > 0)
			error.append("核算编号+核算名称不存在,行:").append(str).append(";");
		BaseUtil.showError(error.toString());
		rs1 = baseDao
				.queryForRowSet(
						"select 'select count(1) from '||ak_table||' where '||ak_asscode||'='''||ASS_CODEFIELD||''' and '||AK_ASSNAME||'='''||ASS_NAMEFIELD||'''' from PreRecass left join asskind on ASS_ASSNAME=ak_name left join PreRec on ASS_CONID=pr_id where pr_id=? order by pr_code",
						pr_id);
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
			BaseUtil.showError("主表核算编号+核算类型不存在，不允许进行当前操作!");
	}

	public void checkNowbalance(Object pr_id) {
		// 判断本次预收退款金额是否大于 已收金额
		String errNowbalance = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(prd_detno) from PreRecDetail left join sale on sa_code=prd_ordercode left join PreRec left join category on ca_code=pr_accountcode on prd_prid = pr_id where pr_id =? and nvl(pr_kind,' ')='预收退款单' and abs(prd_nowbalance)>abs(sa_prepayamount)",
						String.class, pr_id);
		if (errNowbalance != null)
			BaseUtil.showError("来源单据的次预收退款金额大于已收金额，行：" + errNowbalance);
		SqlRowList rs = baseDao.queryForRowSet(
				"select pr_cmrate,round(pr_cmamount/pr_amount,8) from PreRec left join category on ca_code=pr_accountcode where pr_id=?",
				pr_id);
		if (rs.next() && rs.getGeneralDouble(1, 8) != rs.getGeneralDouble(2, 8))
			BaseUtil.showError(String.format("冲账汇率%s与汇率%s不一致", rs.getGeneralDouble(1, 8), rs.getGeneralDouble(2, 8)));
	}

	@Override
	public void updatePreRecById(String caller, String formStore, String gridStore, String assStore, String assMainStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的采购单资料!

		Object status[] = baseDao.getFieldsDataByCondition("PreRec", new String[] { "pr_auditstatuscode", "pr_statuscode" }, "pr_id="
				+ store.get("pr_id"));
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyUnPost"));
		}
		checkVoucher(store.get("pr_id"));
		// checkNowbalance( store.get("pr_id"));

		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> ass = BaseUtil.parseGridStoreToMaps(assStore);
		List<Map<Object, Object>> assMain = BaseUtil.parseGridStoreToMaps(assMainStore);

		handlerService.beforeUpdate(caller, new Object[] { store, grid, ass, assMain });

		if (store.containsKey("ca_asstype")) {
			store.remove("ca_asstype");
		}
		if (store.containsKey("ca_assname")) {
			store.remove("ca_assname");
		}

		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "PreRec", "pr_id"));
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(assMain, "PreRecAss", "ass_id"));
		baseDao.execute(
				"delete from PreRecAss where ass_id in (select ass_id from PreRec left join PreRecAss on ass_conid=pr_id left join category on ca_code=pr_accountcode where pr_id=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ass_assname)=0)",
				store.get("pr_id"));

		Map<Object, List<Map<Object, Object>>> list = BaseUtil.groupMap(ass, "dass_condid");
		int id;
		List<String> gridSql = null;
		for (Map<Object, Object> s : grid) {
			if (s.containsKey("ca_asstype")) {
				s.remove("ca_asstype");
			}
			if (s.containsKey("ca_assname")) {
				s.remove("ca_assname");
			}
			if (s.containsKey("sa_prepayamount")) {
				s.remove("sa_prepayamount");
			}
		}
		if (grid.size() > 0) {
			// gridSql = SqlUtil.getUpdateSqlbyGridStore(grid, "PreRecDetail",
			// "prd_id");
			gridSql = new ArrayList<String>();
			for (Map<Object, Object> s : grid) {
				if (s.get("prd_id") == null || s.get("prd_id").equals("") || s.get("prd_id").equals("0")
						|| Integer.parseInt(s.get("prd_id").toString()) <= 0) {
					id = baseDao.getSeqId("PRERECDETAIL_SEQ");
					ass = list.get(String.valueOf(s.get("prd_id")));
					if (ass != null) {
						for (Map<Object, Object> m : ass) {// VoucherDetailAss
							m.put("dass_condid", id);
							m.put("dass_id", baseDao.getSeqId("PRERECDETAILASS_SEQ"));
						}
						baseDao.execute(SqlUtil.getInsertSqlbyGridStore(ass, "PreRecDetailAss"));
					}
					s.put("prd_id", id);
					gridSql.add(SqlUtil.getInsertSqlByMap(s, "PreRecDetail"));
				} else {
					gridSql.add(SqlUtil.getUpdateSqlByFormStore(s, "PreRecDetail", "prd_id"));
					id = Integer.parseInt(s.get("prd_id").toString());
					ass = list.get(String.valueOf(id));
					if (ass != null) {
						List<String> sqls = SqlUtil.getUpdateSqlbyGridStore(ass, "PreRecDetailAss", "dass_id");
						for (Map<Object, Object> m : ass) {
							if (m.get("dass_id") == null || m.get("dass_id").equals("") || m.get("dass_id").equals("0")
									|| Integer.parseInt(m.get("dass_id").toString()) <= 0) {
								m.put("dass_id", baseDao.getSeqId("PRERECDETAILASS_SEQ"));
								sqls.add(SqlUtil.getInsertSqlByMap(m, "PreRecDetailAss"));
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
					List<String> sqls = SqlUtil.getUpdateSqlbyGridStore(ass, "PreRecDetailAss", "dass_id");
					for (Map<Object, Object> m : ass) {
						if (m.get("dass_id") == null || m.get("dass_id").equals("") || m.get("dass_id").equals("0")
								|| Integer.parseInt(m.get("dass_id").toString()) <= 0) {
							m.put("dass_id", baseDao.getSeqId("PRERECDETAILASS_SEQ"));
							sqls.add(SqlUtil.getInsertSqlByMap(m, "PreRecDetailAss"));
						}
					}
					baseDao.execute(sqls);
				}
			}
		}
		baseDao.execute("update PreRecDetail set PRD_CODE=(select pr_code from PreRec where prd_prid=pr_id) where prd_prid="
				+ store.get("pr_id") + " and not exists (select 1 from PreRec where PRD_CODE=pr_code)");
		// 记录操作
		baseDao.logger.update(caller, "pr_id", store.get("pr_id"));
		handlerService.afterUpdate(caller, new Object[] { store, grid, ass, assMain });
	}

	@Override
	public void deletePreRec(String caller, int pr_id) {
		// 只能删除在录入的采购单!
		Object status[] = baseDao.getFieldsDataByCondition("PreRec", new String[] { "pr_auditstatuscode", "pr_statuscode", "pr_kind" },
				"pr_id=" + pr_id);
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError("只能删除[在录入]的" + status[2] + "！");
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError("只能删除[未过账]的" + status[2] + "！");
		}
		checkVoucher(pr_id);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, pr_id);
		// 删除PreRec
		baseDao.deleteById("PreRec", "pr_id", pr_id);
		// 删除PreRecdetail
		baseDao.deleteById("PreRecDetail", "prd_prid", pr_id);
		// 记录操作
		baseDao.logger.delete(caller, "pr_id", pr_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, pr_id);
	}

	@Override
	public void printPreRec(String caller, int pr_id) {
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, pr_id);
		// 执行审核操作
		baseDao.updateByCondition("PreRec", "pr_printstatuscode='PRINTED',pr_printstatus='" + BaseUtil.getLocalMessage("PRINTED") + "'",
				"pr_id=" + pr_id);
		// 记录操作
		baseDao.logger.print(caller, "pr_id", pr_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, pr_id);
	}

	@Override
	public void auditPreRec(String caller, int pr_id) {
		baseDao.execute("update PreRecDetail set PRD_CODE=(select pr_code from PreRec where prd_prid=pr_id) where prd_prid=" + pr_id
				+ " and not exists (select 1 from PreRec where PRD_CODE=pr_code)");
		Object status = baseDao.getFieldDataByCondition("PreRec", "pr_auditstatuscode", "pr_id=" + pr_id);
		StateAssert.auditOnlyCommited(status);
		// 执行过账前的其它逻辑
		handlerService.beforeAudit(caller, pr_id);
		// 执行审核操作
		baseDao.updateByCondition("PreRec", "pr_auditstatuscode='AUDITED',pr_auditstatus='" + BaseUtil.getLocalMessage("AUDITED")
				+ "',pr_auditer='" + SystemSession.getUser().getEm_name() + "',pr_auditdate=sysdate", "pr_id=" + pr_id);
		// 记录操作
		baseDao.logger.audit(caller, "pr_id", pr_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, pr_id);
	}

	@Override
	public void resAuditPreRec(String caller, int pr_id) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] objs = baseDao
				.getFieldsDataByCondition("PreRec", new String[] { "pr_auditstatuscode", "pr_statuscode" }, "pr_id=" + pr_id);
		if (!objs[0].equals("AUDITED") || objs[1].equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
		}
		// handlerService.handler(caller, "resAudit", "before", new
		// Object[]{pr_id});
		handlerService.beforeResAudit(caller, pr_id);
		// 执行反审核操作
		baseDao.updateByCondition("PreRec", "pr_auditstatuscode='ENTERING',pr_auditstatus='" + BaseUtil.getLocalMessage("ENTERING")
				+ "',pr_auditer='',pr_auditdate=null", "pr_id=" + pr_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "pr_id", pr_id);
		handlerService.afterResAudit(caller, pr_id);
		// handlerService.handler(caller, "resAudit", "after", new
		// Object[]{pr_id});
	}

	@Override
	public void submitPreRec(String caller, int pr_id) {
		baseDao.execute("update PreRecDetail set PRD_CODE=(select pr_code from PreRec where prd_prid=pr_id) where prd_prid=" + pr_id
				+ " and not exists (select 1 from PreRec where PRD_CODE=pr_code)");
		Object status = baseDao.getFieldDataByCondition("PreRec", "pr_auditstatuscode", "pr_id=" + pr_id);
		StateAssert.submitOnlyEntering(status);
		baseDao.execute("update PreRec set pr_jsamount=nvl(pr_cmamount,0)-round((select NVL(sum(prd_nowbalance),0) from PreRecDetail where nvl(prd_catecode,' ')<>' ' and prd_prid=pr_id),2) where pr_id="
				+ pr_id);
		// checkNowbalance(pr_id);
		if ("PreRec!Ars!DERE".equals(caller)) {
			int count = baseDao.getCountByCondition("PreRecDetail", "prd_prid=" + pr_id + " and nvl(prd_ordercode,' ')<>' '");
			if (count > 0) {
				String err = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pr_code) from PreRec left join (select round(sum(nvl(prd_nowbalance,0)),2) nowamount, prd_prid from PreRecDetail group by prd_prid) on pr_id=prd_prid where pr_id=? and abs(nowamount)<>abs(round(pr_cmamount,2))",
								String.class, pr_id);
				if (err != null)
					BaseUtil.showError("明细本次结算金额与主表冲账金额不等，不允许进行当前操作！");
			}
		}
		checkAss(pr_id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, pr_id);
		// 执行提交操作
		baseDao.updateByCondition("PreRec", "pr_auditstatuscode='COMMITED',pr_auditstatus='" + BaseUtil.getLocalMessage("COMMITED") + "'",
				"pr_id=" + pr_id);
		// 记录操作
		baseDao.logger.submit(caller, "pr_id", pr_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, pr_id);
	}

	@Override
	public void resSubmitPreRec(String caller, int pr_id) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("PreRec", "pr_auditstatuscode", "pr_id=" + pr_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, pr_id);
		// 执行反提交操作
		baseDao.updateByCondition("PreRec", "pr_auditstatuscode='ENTERING',pr_auditstatus='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"pr_id=" + pr_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "pr_id", pr_id);
		handlerService.afterResSubmit(caller, pr_id);
	}

	@Override
	public void postPreRec(String caller, int pr_id) {
		baseDao.execute("update PreRecDetail set PRD_CODE=(select pr_code from PreRec where prd_prid=pr_id) where prd_prid=" + pr_id
				+ " and not exists (select 1 from PreRec where PRD_CODE=pr_code)");
		Object status = baseDao.getFieldDataByCondition("PreRec", "pr_statuscode", "pr_id=" + pr_id);
		if (status.equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.post_onlyUnPost"));
		}
		if (baseDao.isDBSetting(caller, "postNotEnd")) {
			Object detno = baseDao
					.getFieldDataByCondition(
							"PreRec left join PreRecDetail on pr_id=prd_prid " + "left join Sale on prd_ordercode=sa_code",
							"wm_concat(distinct sa_code)", "sa_statuscode='FINISH' and pr_id=" + pr_id);
			if (!(detno == null || "".equals(detno.toString().trim()))) {
				BaseUtil.showError("销售订单：" + detno.toString() + "已结案");
			}
		}
		baseDao.execute("update PreRec set pr_jsamount=nvl(pr_cmamount,0)-round((select NVL(sum(prd_nowbalance),0) from PreRecDetail where nvl(prd_catecode,' ')<>' ' and prd_prid=pr_id),2) where pr_id="
				+ pr_id);
		// checkNowbalance(pr_id);
		checkAss(pr_id);
		if ("PreRec!Ars!DERE".equals(caller)) {
			if ("PreRec!Ars!DERE".equals(caller)) {
				int count = baseDao.getCountByCondition("PreRecDetail", "prd_prid=" + pr_id + " and nvl(prd_ordercode,' ')<>' '");
				if (count > 0) {
					String err = baseDao
							.getJdbcTemplate()
							.queryForObject(
									"select wm_concat(pr_code) from PreRec left join (select round(sum(nvl(prd_nowbalance,0)),2) nowamount, prd_prid from PreRecDetail group by prd_prid) on pr_id=prd_prid where pr_id=? and abs(nowamount)<>abs(round(pr_cmamount,2))",
									String.class, pr_id);
					if (err != null)
						BaseUtil.showError("明细本次结算金额与主表冲账金额不等，不允许进行当前操作！");
				}
			}
		}
		// 过账前的其它逻辑
		handlerService.beforePost(caller, pr_id);
		// 执行过账操作
		Object obj = baseDao.getFieldDataByCondition("PreRec", "pr_code", "pr_id=" + pr_id);
		// 存储过程
		String res = baseDao.callProcedure("Sp_CommitePreRec", new Object[] { obj });
		if (res.trim().equals("ok")) {
			baseDao.updateByCondition("PreRec", "pr_statuscode='POSTED',pr_status='" + BaseUtil.getLocalMessage("POSTED") + "'", "pr_id="
					+ pr_id);
			baseDao.updateByCondition("PreRecDetail", "prd_status=99,prd_statuscode='POSTED'", "prd_prid=" + pr_id);
			// 记录操作
			baseDao.logger.post(caller, "pr_id", pr_id);
		} else {
			BaseUtil.showError(res);
		}
		boolean bool = baseDao.checkIf("user_tab_columns", "table_name='BILLARCHEQUE'");
		if (bool) {
			Object source = baseDao.getFieldDataByCondition("PreRec", "pr_sourceid", "pr_id=" + pr_id
					+ " and pr_source='Bank' and pr_kind='预收款'");
			if (source != null) {
				baseDao.execute("update billarcheque set bar_settleamount=bar_doublebalance,bar_leftamount=0,bar_nowstatus='已收款' where exists (select 1 from accountregister where bar_id=ar_sourceid and ar_sourcetype=bar_kind and ar_id="
						+ source + ")");
			}
		}
		// 更新过账人和过账日期
		baseDao.updateByCondition("PreRec", "pr_postman='" + SystemSession.getUser().getEm_name() + "',pr_postdate=sysdate", "pr_id="
				+ pr_id);
		// 执行过账后的其它逻辑
		handlerService.afterPost(caller, pr_id);
	}

	@Override
	public void resPostPreRec(String caller, int pr_id) {
		Object status = baseDao.getFieldDataByCondition("PreRec", "pr_statuscode", "pr_id=" + pr_id);
		if (!status.equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resPost_onlyPost"));
		}
		checkVoucher(pr_id);
		// 过账前的其它逻辑
		// handlerService.handler(caller, "resPost", "before", new
		// Object[]{pr_id, employee});
		handlerService.beforeResPost(caller, pr_id);
		// 执行过账操作
		Object obj = baseDao.getFieldDataByCondition("PreRec", "pr_code", "pr_id=" + pr_id);
		// 存储过程
		String res = baseDao.callProcedure("Sp_UnCommitePreRec", new Object[] { obj });
		if (res.trim().equals("ok")) {
			baseDao.updateByCondition("PreRec",
					"pr_auditstatuscode='ENTERING',pr_statuscode='UNPOST',pr_auditstatus='" + BaseUtil.getLocalMessage("ENTERING")
							+ "',pr_status='" + BaseUtil.getLocalMessage("UNPOST") + "'", "pr_id=" + pr_id);
			baseDao.updateByCondition("PreRecDetail", "prd_status=0,prd_statuscode='ENTERING'", "prd_prid=" + pr_id);
			// 记录操作
			baseDao.logger.resPost(caller, "pr_id", pr_id);
		} else {
			BaseUtil.showError(res);
		}
		boolean bool = baseDao.checkIf("user_tab_columns", "table_name='BILLARCHEQUE'");
		if (bool) {
			Object source = baseDao.getFieldDataByCondition("PreRec", "pr_sourceid", "pr_id=" + pr_id
					+ " and pr_source='Bank' and pr_kind='预收款'");
			if (source != null) {
				baseDao.execute("update billarcheque set bar_settleamount=0,bar_leftamount=bar_doublebalance,bar_nowstatus='未收款' where exists (select 1 from accountregister where bar_id=ar_sourceid and ar_sourcetype=bar_kind and ar_id="
						+ source + ")");
			}
		}
		// 更新过账人和过账日期
		baseDao.updateByCondition("PreRec", "pr_postman=null,pr_postdate=null", "pr_id=" + pr_id);
		// 执行过账后的其它逻辑
		handlerService.afterResPost(caller, pr_id);
	}

	@Override
	public List<Map<String, Object>> sellerPreRec(int pr_id, String emcode, String thisamount, String caller) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		SqlRowList rs = baseDao.queryForRowSet("SELECT * FROM PreRec where pr_id=" + pr_id);
		if (rs.next()) {
			double tamount = NumberUtil.formatDouble(Double.parseDouble(thisamount), 2);
			double amount = NumberUtil.formatDouble(rs.getGeneralDouble("pr_jsamount"), 2);
			double bap = NumberUtil.formatDouble(
					baseDao.getSummaryByField("PREREC", "nvl(pr_jsamount,0)*-1", "pr_source='业务员转预收' and pr_sourceid=" + pr_id
							+ " and pr_sellercode='" + rs.getObject("pr_sellercode") + "'"), 2);
			if (Math.abs(tamount) > Math.abs(amount - bap)) {
				BaseUtil.showError("本次转金额[" + tamount + "]不能大于预收挂账金额-业务员已转预收金额[" + (amount - bap) + "]");
			}
			Map<String, Object> dif1 = new HashMap<String, Object>();
			int prid1 = baseDao.getSeqId("PREREC_SEQ");
			String prcode1 = baseDao.sGetMaxNumber("PreRec", 2);
			dif1.put("pr_id", prid1);
			dif1.put("pr_code", "'" + prcode1 + "'");
			dif1.put("pr_status", "'" + BaseUtil.getLocalMessage("UNPOST") + "'");
			dif1.put("pr_statuscode", "'UNPOST'");
			dif1.put("pr_auditstatus", "'" + BaseUtil.getLocalMessage("ENTERING") + "'");
			dif1.put("pr_auditstatuscode", "'ENTERING'");
			dif1.put("pr_cmstatus", "'" + BaseUtil.getLocalMessage("UNSTRIKE") + "'");
			dif1.put("pr_cmstatuscode", "'UNSTRIKE'");
			dif1.put("pr_printstatus", "'" + BaseUtil.getLocalMessage("UNPRINT") + "'");
			dif1.put("pr_printstatuscode", "'UNPRINT'");
			dif1.put("pr_vouchercode", "null");
			dif1.put("pr_voucherid", 0);
			dif1.put("pr_havebalance", 0);
			dif1.put("pr_amount", Double.parseDouble(thisamount) * (-1));
			dif1.put("pr_jsamount", Double.parseDouble(thisamount) * (-1));
			dif1.put("pr_cmamount", Double.parseDouble(thisamount) * (-1));
			dif1.put("pr_cmrate", 1);
			dif1.put("pr_date", "sysdate");
			dif1.put("pr_indate", "sysdate");
			dif1.put("pr_sourceid", rs.getGeneralInt("pr_id"));
			dif1.put("pr_sourcecode", rs.getGeneralInt("pr_code"));
			dif1.put("pr_source", "'业务员转预收'");
			dif1.put("pr_auditer", "null");
			dif1.put("pr_auditdate", "null");
			baseDao.copyRecord("PreRec", "PreRec", "pr_id=" + pr_id, dif1);

			Map<String, Object> dif2 = new HashMap<String, Object>();
			int prid2 = baseDao.getSeqId("PREREC_SEQ");
			String prcode2 = baseDao.sGetMaxNumber("PreRec", 2);
			dif2.put("pr_id", prid2);
			dif2.put("pr_code", "'" + prcode2 + "'");
			dif2.put("pr_status", "'" + BaseUtil.getLocalMessage("UNPOST") + "'");
			dif2.put("pr_statuscode", "'UNPOST'");
			dif2.put("pr_auditstatus", "'" + BaseUtil.getLocalMessage("ENTERING") + "'");
			dif2.put("pr_auditstatuscode", "'ENTERING'");
			dif2.put("pr_cmstatus", "'" + BaseUtil.getLocalMessage("UNSTRIKE") + "'");
			dif2.put("pr_cmstatuscode", "'UNSTRIKE'");
			dif2.put("pr_printstatus", "'" + BaseUtil.getLocalMessage("UNPRINT") + "'");
			dif2.put("pr_printstatuscode", "'UNPRINT'");
			dif2.put("pr_vouchercode", "null");
			dif2.put("pr_voucherid", 0);
			dif2.put("pr_havebalance", 0);
			dif2.put("pr_amount", Double.parseDouble(thisamount));
			dif2.put("pr_jsamount", Double.parseDouble(thisamount));
			dif2.put("pr_cmamount", Double.parseDouble(thisamount));
			dif1.put("pr_cmrate", 1);
			dif2.put("pr_date", "sysdate");
			dif2.put("pr_indate", "sysdate");
			dif2.put("pr_sourceid", rs.getGeneralInt("pr_id"));
			dif2.put("pr_sourcecode", rs.getGeneralInt("pr_code"));
			dif2.put("pr_source", "'业务员转预收'");
			dif2.put("pr_auditer", "null");
			dif2.put("pr_auditdate", "null");
			dif2.put("pr_sellercode", "'" + emcode + "'");
			baseDao.copyRecord("PreRec", "PreRec", "pr_id=" + pr_id, dif2);
			baseDao.execute("update prerec set (pr_sellerid,pr_seller)=(select em_id,em_name from employee where em_code=pr_sellercode) where pr_id="
					+ prid2);
			// Copy 主表辅助核算
			SqlRowList ass = baseDao.queryForRowSet("SELECT ASS_ID FROM PRERECASS WHERE ASS_CONID=?", pr_id);
			while (ass.next()) {
				dif1 = new HashMap<String, Object>();
				dif1.put("ass_id", baseDao.getSeqId("PRERECASS_SEQ"));
				dif1.put("ASS_CONID", prid1);
				baseDao.copyRecord("PRERECASS", "PRERECASS", "ass_id=" + ass.getInt("ass_id"), dif1);
				dif2 = new HashMap<String, Object>();
				dif2.put("ass_id", baseDao.getSeqId("PRERECASS_SEQ"));
				dif2.put("ASS_CONID", prid2);
				baseDao.copyRecord("PRERECASS", "PRERECASS", "ass_id=" + ass.getInt("ass_id"), dif2);
			}
			postPreRec(caller, prid1);
			postPreRec(caller, prid2);
			Map<String, Object> map1 = new HashMap<String, Object>();
			map1.put("id", prid1);
			map1.put("code", prcode1);
			list.add(map1);
			Map<String, Object> map2 = new HashMap<String, Object>();
			map2.put("id", prid2);
			map2.put("code", prcode2);
			list.add(map2);
			baseDao.logger.others("业务员转预收", "转入成功", caller, "pr_id", pr_id);
		}
		return list;
	}
}
