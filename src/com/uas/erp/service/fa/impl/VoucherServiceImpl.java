package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Workbook;
import org.drools.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Operation;
import com.uas.erp.core.bind.Status;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.MessageLog;
import com.uas.erp.model.VoucherDetailAss;
import com.uas.erp.service.fa.VoucherService;

@Service("voucherService")
public class VoucherServiceImpl implements VoucherService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private VoucherDao voucherDao;
	@Autowired
	private DetailGridDao detailGridDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveVoucher(String formStore, String[] gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Voucher", "vo_code='" + store.get("vo_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		store.put("vo_emid", SystemSession.getUser().getEm_id());
		store.put("vo_recordman", SystemSession.getUser().getEm_name());
		store.put("vo_status", BaseUtil.getLocalMessage("ENTERING"));
		store.put("vo_statuscode", "ENTERING");
		store.put("vo_printstatus", BaseUtil.getLocalMessage("UNPRINT"));
		int yearmonth = 0;
		if (baseDao.isDBSetting("auditDuring") && !"0".equals(store.get("vo_adjust"))) {
			if (!StringUtil.hasText(store.get("vo_yearmonth"))) {
				BaseUtil.showError("请选择期间！");
			}
			yearmonth = Integer.parseInt(store.get("vo_yearmonth").toString());
		} else {
			yearmonth = voucherDao.getPeriodsFromDate("Month-A", store.get("vo_date").toString());
			store.put("vo_yearmonth", yearmonth);
		}
		// 判断当前期间是否结账
		int count = baseDao.getCount("select count(1) from Periodsdetail where pd_code='Month-A' and pd_status=99 and pd_detno="
				+ yearmonth);
		if (count != 0) {
			int nowym = voucherDao.getNowPddetno("Month-A");// 当前期间
			BaseUtil.showError("期间" + yearmonth + "已经结转,当前总账期间:" + nowym + "<br>请修改日期，或反结转总账.");
		}
		store.put("vo_number", voucherDao.getVoucherNumber(String.valueOf(yearmonth), StringUtil.valueOf(store.get("vo_lead")), null));
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		List<String> sqls = new ArrayList<String>();
		sqls.add(SqlUtil.getInsertSqlByMap(store, "Voucher"));
		// 保存VoucherFlow
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore[1]);
		for (Map<Object, Object> map : grid) {
			map.put("vd_yearmonth", yearmonth);
		}
		sqls.addAll(SqlUtil.getInsertSqlbyList(grid, "VoucherFlow", "vf_id"));
		// 保存VoucherDetail
		grid = BaseUtil.parseGridStoreToMaps(gridStore[0]);
		List<Map<Object, Object>> assgrid = BaseUtil.parseGridStoreToMaps(gridStore[2]);
		Map<Object, List<Map<Object, Object>>> list = BaseUtil.groupMap(assgrid, "vds_vdid");
		for (Map<Object, Object> map : grid) {
			int id = baseDao.getSeqId("VOUCHERDETAIL_SEQ");
			assgrid = list.get(map.get("vd_id"));
			if (assgrid != null) {
				for (Map<Object, Object> m : assgrid) {// VoucherDetailAss
					m.put("vds_vdid", id);
					m.put("vds_type", "Voucher");
				}
				sqls.addAll(SqlUtil.getInsertSqlbyList(assgrid, "VoucherDetailAss", "vds_id"));
			}
			map.put("vd_id", id);
		}
		sqls.addAll(SqlUtil.getInsertSqlbyGridStore(grid, "VoucherDetail"));
		baseDao.execute(sqls);
		// 记录操作
		baseDao.logger.save(caller, "vo_id", store.get("vo_id"));
		baseDao.execute("update VoucherDetail set vd_creditcashflow=NVL(vd_credit,0),vd_debitcashflow=NVL(vd_debit,0) where vd_void="
				+ store.get("vo_id"));
		// valid Voucher
		voucherDao.validVoucher(Integer.parseInt(store.get("vo_id").toString()));
		insertVoucherFlow(store.get("vo_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, store);
	}

	@Override
	public void deleteVoucher(int vo_id, String caller) {
		String state = baseDao.getFieldValue("Voucher", "vo_statuscode", "vo_id=" + vo_id, String.class);
		StateAssert.delOnlyEntering(state);
		checkYm(vo_id);
		String source = baseDao.getJdbcTemplate().queryForObject("SELECT vo_source FROM Voucher WHERE vo_id=?", String.class, vo_id);
		if (source != null && source.length() > 0) {
			BaseUtil.showError("凭证有来源【" + source + "】,无法删除");
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, vo_id);
		baseDao.execute("update SalaryBill set sb_vouchercode=null where sb_code=(select vo_refno from Voucher where vo_id=" + vo_id
				+ " and vo_explanation like '%计提研发工资')");
		// 删除
		baseDao.deleteById("Voucher", "vo_id", vo_id);
		// 删除VoucherFlow
		baseDao.deleteById("VoucherFlow", "vf_voucherid", vo_id);
		// 删除VoucherDetailAss
		baseDao.deleteByCondition("VoucherDetailAss", "vds_vdid in(select vd_id from voucherdetail where vd_void=" + vo_id + ")");
		// 删除VoucherDetail
		baseDao.deleteById("VoucherDetail", "vd_void", vo_id);
		// 记录操作
		baseDao.logger.delete(caller, "vo_id", vo_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, vo_id);
	}

	@Override
	public void updateVoucherById(String formStore, String[] gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int vId = Integer.parseInt(store.get("vo_id").toString());
		// 只能修改未记账的凭证
		Object status = baseDao.getFieldDataByCondition("Voucher", "vo_statuscode", "vo_id=" + vId);
		StateAssert.updateOnlyEntering(status);
		Integer yearmonth = Integer.parseInt(String.valueOf(store.get("vo_yearmonth")));
		String newLead = StringUtil.nvl(store.get("vo_lead"), "");
		String oldLead = StringUtil.nvl(baseDao.getFieldValue("voucher", "vo_lead", "vo_id=" + vId, String.class), "");
		int ym = 0;
		if (baseDao.isDBSetting("auditDuring") && !"0".equals(store.get("vo_adjust"))) {
			ym = baseDao.getFieldValue("voucher", "vo_yearmonth", "vo_id=" + vId, Integer.class);
			if (yearmonth != ym) {
				store.put("vo_number", voucherDao.getVoucherNumber(String.valueOf(yearmonth), newLead, null));
			} else if (!newLead.equals(oldLead)) {
				store.put("vo_number", voucherDao.getVoucherNumber(String.valueOf(yearmonth), newLead, null));
			}
		} else {
			ym = voucherDao.getPeriodsFromDate("Month-A", store.get("vo_date").toString());
			if (yearmonth != ym) {
				yearmonth = ym;
				store.put("vo_yearmonth", yearmonth);
				store.put("vo_number", voucherDao.getVoucherNumber(String.valueOf(ym), newLead, null));
			} else if (!newLead.equals(oldLead)) {
				store.put("vo_number", voucherDao.getVoucherNumber(String.valueOf(ym), newLead, null));
			}
		}
		// 判断当前期间是否结账
		int count = baseDao.getCount("select count(1) from Periodsdetail where pd_code='Month-A' and pd_status=99 and pd_detno="
				+ yearmonth);
		if (count != 0) {
			int nowym = voucherDao.getNowPddetno("Month-A");// 当前期间
			BaseUtil.showError("期间" + yearmonth + "已经结转,当前总账期间:" + nowym + "<br>请修改日期，或反结转总账.");
		}
		if (store.get("vo_number") == null) {
			store.put("vo_number", voucherDao.getVoucherNumber(String.valueOf(store.get("vo_yearmonth")), newLead, null));
		}
		// 执行修改前的其它逻辑
		handlerService.handler("Voucher", "save", "before", new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Voucher", "vo_id");
		baseDao.execute(formSql);
		// 修改VoucherFlow
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore[1]);
		List<String> gridSql = null;
		if (grid.size() > 0) {
			gridSql = SqlUtil.getUpdateSqlbyGridStore(grid, "VoucherFlow", "vf_id");
			for (Map<Object, Object> s : grid) {
				if (s.get("vf_id") == null || s.get("vf_id").equals("") || s.get("vf_id").equals("0")
						|| Integer.parseInt(s.get("vf_id").toString()) == 0) {
					s.put("vf_id", baseDao.getSeqId("VOUCHERFLOW_SEQ"));
					String sql = SqlUtil.getInsertSqlByMap(s, "VoucherFlow");
					gridSql.add(sql);
				}
			}
			baseDao.execute(gridSql);
		}
		// 保存VoucherDetail
		grid = BaseUtil.parseGridStoreToMaps(gridStore[0]);
		if (grid.size() > 0) {
			gridSql = SqlUtil.getUpdateSqlbyGridStore(grid, "VoucherDetail", "vd_id");
			gridSql.add(0, "update VoucherDetail set vd_detno=-vd_detno where vd_void=" + store.get("vo_id"));
			List<Map<Object, Object>> assgrid = BaseUtil.parseGridStoreToMaps(gridStore[2]);
			Map<Object, List<Map<Object, Object>>> list = BaseUtil.groupMap(assgrid, "vds_vdid");
			for (Map<Object, Object> s : grid) {
				if (s.get("vd_id") == null || s.get("vd_id").equals("") || s.get("vd_id").equals("0")
						|| Integer.parseInt(s.get("vd_id").toString()) <= 0) {
					int id = baseDao.getSeqId("VOUCHERDETAIL_SEQ");
					assgrid = list.get(String.valueOf(s.get("vd_id")));
					if (assgrid != null) {
						for (Map<Object, Object> m : assgrid) {// VoucherDetailAss
							m.put("vds_vdid", id);
							m.put("vds_type", "Voucher");
						}
						// 保存VoucherDetailAsss
						List<String> sqls = SqlUtil.getInsertSqlbyList(assgrid, "VoucherDetailAss", "vds_id");
						baseDao.execute(sqls);
					}
					s.put("vd_id", id);
					gridSql.add(SqlUtil.getInsertSqlByMap(s, "VoucherDetail"));
				} else {
					// 科目有修改的情况下，先删除之前科目的辅助核算
					gridSql.add("delete from voucherdetailass where vds_vdid="
							+ s.get("vd_id")
							+ " and instr(nvl((select ca_assname from category left join voucherdetail on ca_code=vd_catecode where vd_id=vds_vdid and ca_assname is not null),' '), vds_asstype) = 0");
				}
			}
			for (Object key : list.keySet()) {
				Integer id = Integer.parseInt(String.valueOf(key));
				if (id > 0) {
					assgrid = list.get(key);
					if (assgrid != null) {
						for (Map<Object, Object> map : assgrid) {
							// 科目修改的情况下，辅助核算类型可能一样
							if (!StringUtil.hasText(map.get("vds_id")) || Integer.parseInt(String.valueOf(map.get("vds_id"))) <= 0) {
								gridSql.add("delete from VoucherDetailAss where vds_vdid=" + map.get("vds_vdid") + " and vds_asstype='"
										+ map.get("vds_asstype") + "'");
							}
						}
						List<String> sqls = SqlUtil.getInsertOrUpdateSqlbyGridStore(assgrid, "VoucherDetailAss", "vds_id");
						gridSql.addAll(sqls);
					}
				}
			}
			gridSql.add("update VoucherDetail set vd_detno=abs(vd_detno) where vd_void=" + store.get("vo_id"));
			baseDao.execute(gridSql);
		} else {
			grid = BaseUtil.parseGridStoreToMaps(gridStore[2]);
			gridSql = SqlUtil.getInsertOrUpdateSqlbyGridStore(grid, "VoucherDetailAss", "vds_id");
			// 保存VoucherDetailAss
			baseDao.execute(gridSql);
		}
		baseDao.updateByCondition("VoucherDetail", "vd_yearmonth=" + yearmonth, "vd_void=" + store.get("vo_id"));
		// 记录操作
		baseDao.logger.update(caller, "vo_id", store.get("vo_id"));
		baseDao.execute("update VoucherDetail set vd_creditcashflow=NVL(vd_credit,0),vd_debitcashflow=NVL(vd_debit,0) where vd_void="
				+ store.get("vo_id"));
		// valid Voucher
		voucherDao.validVoucher(vId);
		insertVoucherFlow(vId);
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void auditVoucher(int vo_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Voucher", "vo_statuscode", "vo_id=" + vo_id);
		StateAssert.auditOnlyCommited(status);
		checkYm(vo_id);
		voucherDao.validVoucher(vo_id);
		// 状态必须 【正常】
		status = baseDao.getFieldDataByCondition("Voucher", "vo_errstring", "vo_id=" + vo_id);
		if (status != null && status.toString().length() > 1) {
			BaseUtil.showError("状态为【" + status + "】，不允许审核!");
		}
		if (baseDao.isDBSetting("Voucher", "noAuditOwnner")) {
			String overVo = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(vo_code) from voucher where vo_id =" + vo_id + " and vo_recordman='"
							+ SystemSession.getUser().getEm_name() + "'", String.class);
			if (overVo != null)
				BaseUtil.showError("不允许审核自己制作的凭证：" + overVo);
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit("Voucher", vo_id);
		// 执行审核操作
		baseDao.updateByCondition("Voucher", "VO_ISUPDATE=0,vo_statuscode='AUDITED',vo_status='" + BaseUtil.getLocalMessage("AUDITED")
				+ "',vo_checkby='" + SystemSession.getUser().getEm_name() + "', VO_AUDITER='" + SystemSession.getUser().getEm_name()
				+ "',VO_AUDITDATE=sysdate", "vo_id=" + vo_id);
		// 记录操作
		baseDao.logger.audit(caller, "vo_id", vo_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, vo_id);
	}

	@Override
	public void resAuditVoucher(int vo_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Voucher", "vo_statuscode", "vo_id=" + vo_id);
		StateAssert.resAuditOnlyAudit(status);
		checkYm(vo_id);
		handlerService.beforeResAudit(caller, vo_id);
		if (baseDao.isDBSetting("Voucher", "noResAuditOther")) {
			String overVo = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(vo_code) from voucher where vo_id=" + vo_id + " and vo_auditer<>'"
							+ SystemSession.getUser().getEm_name() + "'", String.class);
			if (overVo != null)
				BaseUtil.showError("不允许反审核他人审核的凭证：" + overVo);
		}
		// 执行反审核操作
		String resAuditStatus = baseDao.getDBSetting(caller, "voucherStatus");
		String statuscode = "ENTERING";
		if (resAuditStatus != null) {
			// 反审核状态是已提交
			if ("1".equals(resAuditStatus)) {
				statuscode = "COMMITED";
			}
			// 反审核状态是在录入
			if ("0".equals(resAuditStatus)) {
				statuscode = "ENTERING";
			}
		}
		baseDao.updateByCondition("Voucher",
				"VO_ISUPDATE=0,vo_statuscode='" + statuscode + "',vo_status='" + BaseUtil.getLocalMessage(statuscode)
						+ "',vo_checkby=null, VO_AUDITER=null,VO_AUDITDATE=null", "vo_id=" + vo_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "vo_id", vo_id);
		handlerService.afterResAudit(caller, vo_id);
	}

	@Override
	public void submitVoucher(int vo_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Voucher", "vo_statuscode", "vo_id=" + vo_id);
		StateAssert.submitOnlyEntering(status);
		checkYm(vo_id);
		baseDao.execute("update VoucherDetail set vd_creditcashflow=NVL(vd_credit,0),vd_debitcashflow=NVL(vd_debit,0) where vd_void="
				+ vo_id);
		voucherDao.validVoucher(vo_id);
		insertVoucherFlow(vo_id);
		// 状态必须 【正常】
		status = baseDao.getFieldDataByCondition("Voucher", "vo_errstring", "vo_id=" + vo_id);
		if (status != null && status.toString().length() > 1) {
			BaseUtil.showError("状态为【" + status + "】，不允许提交!");
		}
		if (baseDao.isDBSetting("Voucher", "voucherFlowCheck")) {
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(vd_detno) from voucherdetail, voucher, category where vd_void=vo_id and vd_catecode=ca_code "
									+ " and vo_id=? and nvl(vd_catecode,' ')<> ' ' and nvl(VD_FLOWCODE,' ')=' ' and nvl(vo_iscashflow,0)<>0 and nvl(ca_cashflow,0)=0",
							String.class, vo_id);
			if (dets != null) {
				BaseUtil.showError("没设置现金流量项目，不允许提交！行号：" + dets);
			}
		}
		String defaultCurrency = baseDao.getDBSetting("sys", "defaultCurrency");
		// 科目没有设置外币核算不允许填写本位币以外的币别
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(vd_detno) from voucherdetail, category where vd_catecode=ca_code and vd_void=? "
						+ "and nvl(vd_catecode,' ')<> ' ' and nvl(ca_currencytype,0)=0 and nvl(vd_currency,'" + defaultCurrency + "')<>'"
						+ defaultCurrency + "'", String.class, vo_id);
		if (dets != null) {
			BaseUtil.showError("科目没有设置外币核算不允许填写非本位币的币别！行号：" + dets);
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, vo_id);
		// 执行提交操作
		baseDao.updateByCondition("Voucher", "VO_ISUPDATE=0,vo_statuscode='COMMITED',vo_status='" + BaseUtil.getLocalMessage("COMMITED")
				+ "'", "vo_id=" + vo_id);
		// 记录操作
		baseDao.logger.submit(caller, "vo_id", vo_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, vo_id);
	}

	private void checkYm(int vo_id) {
		int myym = baseDao.getFieldValue("Voucher", "vo_yearmonth", "vo_id=" + vo_id, Integer.class);
		int count = baseDao.getCount("select count(1) from Periodsdetail where pd_code='Month-A' and pd_status=99 and pd_detno=" + myym);
		if (count != 0) {
			int nowym = voucherDao.getNowPddetno("Month-A");// 当前期间
			BaseUtil.showError("期间" + myym + "已经结转,当前总账期间:" + nowym + "<br>。不允许操作总账期间已结账的凭证！");
		}
	}

	@Override
	public void resSubmitVoucher(int vo_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Voucher", "vo_statuscode", "vo_id=" + vo_id);
		StateAssert.resSubmitOnlyCommited(status);
		checkYm(vo_id);
		handlerService.beforeResSubmit(caller, vo_id);
		baseDao.execute("DELETE FROM VoucherFlow WHERE vf_voucherid=" + vo_id);
		// 执行反提交操作
		baseDao.updateByCondition("Voucher", "VO_ISUPDATE=0,vo_statuscode='ENTERING',vo_status='" + BaseUtil.getLocalMessage("ENTERING")
				+ "'", "vo_id=" + vo_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "vo_id", vo_id);
		handlerService.afterResSubmit(caller, vo_id);
	}

	/**
	 * 删除明细时，重新计算明细状态
	 */
	public String validVoucher(int id) {
		voucherDao.validVoucher(id);
		return baseDao.getJdbcTemplate().queryForObject("select vo_errstring from Voucher where vo_id=?", String.class, id);
	}

	public void insertVoucherFlow(Object vId) {
		baseDao.execute("update voucherdetail set (vd_flowcode,vd_flowname)=(select CA_DEFAULTCASHCODE,CA_DEFAULTCASHFLOW from category where ca_code=VD_CATECODE) where nvl(VD_CATECODE,' ')<>' ' and nvl(vd_flowcode,' ')=' ' "
				+ "and vd_id in (select vd_id from voucherdetail,voucher,category where vd_void=vo_id and vd_catecode=ca_code and nvl(vd_catecode,' ')<>' ' and nvl(vo_iscashflow,0)<>0 and nvl(ca_cashflow,0)=0 and vo_id="
				+ vId + ")");
		baseDao.execute("delete from voucherflow where VF_VOUCHERID=" + vId);
		baseDao.execute("insert into voucherflow (VF_ID, VF_VOUCHERID, VF_DETNO, VF_FLOWCODE, VF_FLOWNAME, VF_INAMOUNT, VF_OUTAMOUNT, VF_VDID) "
				+ "select VOUCHERFLOW_SEQ.NEXTVAL, vd_void, rownum, vd_flowcode, vd_flowname, vd_credit,vd_debit, VD_ID FROM voucherdetail,voucher,category WHERE vd_void=vo_id and vd_catecode=ca_code and VD_VOID="
				+ vId + "and nvl(vd_catecode,' ')<>' ' and nvl(vo_iscashflow,0)<>0 and nvl(ca_cashflow,0)=0");
		baseDao.execute("update voucherflow set VF_FLOWID=(select CF_ID from cashflowitem where VF_FLOWCODE=CF_CODE) where VF_VOUCHERID="
				+ vId + " and nvl(VF_FLOWCODE,' ')<>' '");
	}

	/**
	 * 复制凭证
	 */
	@Override
	public JSONObject copyVoucher(int id) {
		Map<String, Object> dif = new HashMap<String, Object>();
		// Copy 凭证
		int nId = baseDao.getSeqId("VOUCHER_SEQ");
		dif.put("vo_id", nId);
		Map<String, Object> period = voucherDao.getJustPeriods("Month-A");
		dif.put("vo_date", DateUtil.parseDateToOracleString(null, DateUtil.parse(String.valueOf(period.get("PD_ENDDATE")), null)));
		Object yearmonth = period.get("PD_DETNO");
		dif.put("vo_yearmonth", yearmonth);
		String lead = baseDao.getFieldValue("voucher", "vo_lead", "vo_id=" + id, String.class);
		String num = voucherDao.getVoucherNumber(String.valueOf(yearmonth), lead, null);
		dif.put("vo_number", num);
		String code = baseDao.sGetMaxNumber("Voucher", 2);
		dif.put("vo_code", "'" + code + "'");
		dif.put("vo_emid", SystemSession.getUser().getEm_id());
		dif.put("vo_recordman", "'" + SystemSession.getUser().getEm_name() + "'");
		dif.put("vo_status", "'" + Status.ENTERING.display() + "'");
		dif.put("vo_statuscode", "'" + Status.ENTERING.code() + "'");
		dif.put("vo_recorddate", "sysdate");
		dif.put("vo_source", "null");
		dif.put("vo_sourcecode", "null");
		dif.put("vo_createkind", "null");
		dif.put("vo_writeby", "null");
		dif.put("vo_checkby", "null");
		dif.put("vo_auditer", "null");
		dif.put("vo_auditdate", "null");
		dif.put("vo_printstatus", "'" + Status.UNPRINT.display() + "'");
		baseDao.copyRecord("Voucher", "Voucher", "vo_id=" + id, dif);
		// Copy 凭证明细
		SqlRowList list = baseDao.queryForRowSet("SELECT vd_id FROM VoucherDetail WHERE vd_void=?", id);
		SqlRowList ass = null;
		Integer dId = null;
		while (list.next()) {
			dif = new HashMap<String, Object>();
			dId = baseDao.getSeqId("VOUCHERDETAIL_SEQ");
			dif.put("vd_id", dId);
			dif.put("vd_void", nId);
			baseDao.copyRecord("VoucherDetail", "VoucherDetail", "vd_id=" + list.getInt("vd_id"), dif);
			// Copy 辅助核算
			ass = baseDao.queryForRowSet("SELECT vds_id FROM VoucherDetailAss WHERE vds_vdid=?", list.getInt("vd_id"));
			while (ass.next()) {
				dif = new HashMap<String, Object>();
				dif.put("vds_id", baseDao.getSeqId("VOUCHERDETAILASS_SEQ"));
				dif.put("vds_vdid", dId);
				baseDao.copyRecord("VoucherDetailAss", "VoucherDetailAss", "vds_id=" + ass.getInt("vds_id"), dif);
			}
		}
		// Copy 现金流
		list = baseDao.queryForRowSet("SELECT vf_id FROM VoucherFlow WHERE vf_voucherid=?", id);
		while (list.next()) {
			dif = new HashMap<String, Object>();
			dif.put("vf_id", baseDao.getSeqId("VOUCHERFLOW_SEQ"));
			dif.put("vf_voucherid", nId);
			baseDao.copyRecord("VoucherFlow", "VoucherFlow", "vf_id=" + list.getInt("vf_id"), dif);
		}
		JSONObject obj = new JSONObject();
		obj.put("vo_id", nId);
		obj.put("vo_number", num);
		obj.put("vo_code", code);
		return obj;
	}

	/**
	 * 凭证制作
	 */

	/**
	 * 凭证取消
	 */
	/**
	 * 凭证编号断号重排
	 */
	@Override
	public void insertBreakVoNumber(String data, String caller) {
		List<Map<Object, Object>> list = BaseUtil.parseGridStoreToMaps(data);
		String cond = null;
		for (Map<Object, Object> m : list) {
			cond = "vo_id=" + m.get("vo_id");
			Object ol = baseDao.getFieldDataByCondition("Voucher", "vo_number", cond);
			baseDao.updateByCondition("Voucher", "vo_number=-" + m.get("vo_number"), cond);
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "凭证号重排", "重排成功.原凭证号:" + ol + ",新凭证号:"
					+ m.get("vo_number"), "Voucher|" + cond));
		}
		baseDao.updateByCondition("Voucher", "vo_number=abs(vo_number)", "vo_number<0");
	}

	/**
	 * 凭证记账
	 */
	@Override
	public String accountVoucher(Integer month, String caller) {
		baseDao.execute("update voucher set vo_total=(select sum(nvl(vd_debit,0)) from voucherdetail where vd_void=vo_id) where vo_yearmonth="
				+ month);
		baseDao.execute("update voucher set vo_totalupper=L2U(vo_total) where vo_yearmonth=" + month);
		List<String> voids = new ArrayList<String>();
		SqlRowList rs = baseDao.queryForRowSet("select vo_id from voucher where vo_statuscode='AUDITED' and vo_yearmonth=?",
				new Object[] { month });
		while (rs.next()) {
			voids.add(rs.getString("vo_id"));
		}

		String msg = baseDao.callProcedure("SP_WriteVoucher", new Object[] { month });
		// no matter write is success or failed
		baseDao.execute("update voucher set VO_WRITEBY=? where vo_yearmonth=? and vo_statuscode='ACCOUNT' and VO_WRITEBY is null",
				SystemSession.getUser().getEm_name(), month);
		if (baseDao.isDBSetting("Voucher!Record!Deal", "autoSFS")) {
			String res = baseDao.callProcedure("FA_SFS", new Object[] { month });
			if (StringUtil.hasText(res)) {
				BaseUtil.showError(res);
			}
		}
		if (voids.size() > 0) {
			// 记录操作日志
			List<List<String>> idList = CollectionUtil.split(voids, 500);
			for (List<String> ids : idList) {
				baseDao.execute("INSERT INTO MessageLog(ml_date,ml_man,ml_content,ml_result,ml_search) select sysdate,'"
						+ SystemSession.getUser().getEm_name() + "','批量记账','记账成功','Voucher|vo_id='||vo_id from voucher where vo_id in("
						+ CollectionUtil.toString(ids) + ") and vo_statuscode='ACCOUNT'");
			}
		}
		return msg;
	}

	/**
	 * 凭证取消记账
	 */
	@Override
	public String resAccountVoucher(Integer month, String caller) {
		List<String> voids = new ArrayList<String>();
		SqlRowList rs = baseDao.queryForRowSet("select vo_id from voucher where vo_statuscode='ACCOUNT' and vo_yearmonth=?",
				new Object[] { month });
		while (rs.next()) {
			voids.add(rs.getString("vo_id"));
		}
		String msg = baseDao.callProcedure("SP_UnWriteVoucher", new Object[] { month });
		if (voids.size() > 0) {
			// 记录操作日志
			List<List<String>> idList = CollectionUtil.split(voids, 500);
			for (List<String> ids : idList) {
				baseDao.execute("INSERT INTO MessageLog(ml_date,ml_man,ml_content,ml_result,ml_search) select sysdate,'"
						+ SystemSession.getUser().getEm_name() + "','批量反记账','反记账成功','Voucher|vo_id='||vo_id from voucher where vo_id in("
						+ CollectionUtil.toString(ids) + ") and vo_statuscode<>'ACCOUNT'");
			}
		}
		return msg;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@SuppressWarnings("rawtypes")
	public boolean ImportExcel(int id, Workbook wbs, String substring, String caller) {
		checkYm(id);
		int sheetnum = wbs.getNumberOfSheets();
		Object textValue = "";
		/**
		 * 清除所有 该凭证的辅助核算
		 * */
		baseDao.deleteByCondition("VoucherDetailAss", "vds_vdid in (select vd_id from  VoucherDetail  where  vd_void=" + id + ")");
		baseDao.deleteByCondition("VoucherDetail", " vd_void=" + id);
		List<Map<String, Object>> maplist = new ArrayList<Map<String, Object>>();
		List<DetailGrid> details = detailGridDao.getDetailGridsByCaller("Voucher!DetailandAss!Export", SpObserver.getSp());
		Object groupdetno = null;
		Map<String, Object> modelMap = null;
		DetailGrid detail = null;
		Map<String, List<Map<String, Object>>> voucherDetails = new TreeMap<String, List<Map<String, Object>>>(new Comparator<String>() {
			public int compare(String obj1, String obj2) {
				return Integer.valueOf(obj1) - Integer.valueOf(obj2);
			}
		});
		List<Map<String, Object>> maps = null;
		if (sheetnum > 0) {
			HSSFSheet sheet = (HSSFSheet) wbs.getSheetAt(0);
			DataFormatter fmt = new DataFormatter();

			// 再遍历行 从第2行开始
			for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
				HSSFRow row = sheet.getRow(i);
				if (row != null) {
					modelMap = new HashMap<String, Object>();
					String str1 = "";
					for (int j = 0; j < row.getLastCellNum(); j++) {
						textValue = "";
						HSSFCell cell = row.getCell(j);
						textValue = fmt.formatCellValue(cell);
						detail = details.get(j);
						if ((detail.getDg_field().equals("vd_explanation") || detail.getDg_field().equals("vd_catecode"))
								&& ("".equals(textValue))) {
							BaseUtil.showError("表格第" + (i + 1) + "行 没有设置" + detail.getDg_caption());
						} else
							modelMap.put(detail.getDg_field(), textValue.toString().trim());

						if (detail.getDg_field().equals("vd_explanation") || detail.getDg_field().equals("vd_catecode")
								|| detail.getDg_field().equals("vd_debit") || detail.getDg_field().equals("vd_credit")) {
							// 要比较的字段
							str1 += "#";
							str1 += textValue.toString().trim();
						}
					}
					modelMap.put("detno", i + 1);// 为每条数据生成行号
					modelMap.put("iscombine", 0);
					modelMap.put("str1", str1);
					if (!modelMap.isEmpty())
						maplist.add(modelMap);

				}
			}
		}

		for (Map<String, Object> map1 : maplist) {
			Object str1 = map1.get("str1");
			Object ass1 = map1.get("vds_asstype");
			Object detno = map1.get("detno");
			Object vd_detno = map1.get("vd_detno");
			Object asscount = baseDao.getFieldDataByCondition("category", "length(ca_assname)-length(replace(ca_assname,'#',''))+1",
					"ca_code='" + map1.get("vd_catecode") + "'");
			// 科目设了多辅助核算（辅助核算大于1）或不设辅助核算时才合并
			if (map1.get("iscombine").equals(0) && ass1 != null && !StringUtils.isEmpty(ass1.toString())
					&& ((asscount != null && !asscount.toString().equals("1")) || asscount == null)) {
				if (asscount != null && !asscount.toString().equals("1") && (vd_detno == null || vd_detno.equals("")))
					BaseUtil.showError("表格行" + map1.get("detno") + "科目" + map1.get("vd_catecode") + "设置了多辅助核算，需填写序号");
				for (Map<String, Object> map2 : maplist) {
					// 科目中填了多辅助核算
					if (asscount != null && !asscount.toString().equals("1") && vd_detno != null && !vd_detno.equals("")
							&& vd_detno.equals(map2.get("vd_detno")) && !str1.equals(map2.get("str1"))) {
						BaseUtil.showError("表格行" + detno + "、行" + map2.get("detno") + "序号一致，摘要+科目编号+借、贷方金额不一致！");
					}
					if (((asscount != null && !asscount.toString().equals("1") && vd_detno != null && !vd_detno.equals("") && vd_detno
							.equals(map2.get("vd_detno"))) || asscount == null)
							&& str1.equals(map2.get("str1"))
							&& !detno.equals(map2.get("detno"))
							&& (map2.get("vds_asstype") != null && !map2.get("vds_asstype").equals(ass1))) {
						map2.put("detno", map1.get("detno"));
						map2.put("iscombine", 1);
					}
				}
			}
		}

		for (Map<String, Object> map : maplist) {
			groupdetno = map.get("detno");
			if (groupdetno != null) {
				if (voucherDetails.containsKey(groupdetno.toString())) {
					voucherDetails.get(groupdetno.toString()).add(map);
				} else {
					maps = new ArrayList<Map<String, Object>>();
					maps.add(map);
					voucherDetails.put(groupdetno.toString(), maps);
				}
			}
		}

		List<String> sqls = new ArrayList<String>();
		Object vddetno = null;
		int formdetno = 1;
		for (Iterator iterator = voucherDetails.keySet().iterator(); iterator.hasNext();) {
			vddetno = iterator.next().toString();
			maps = voucherDetails.get(vddetno);
			Map<String, Object> map1 = new HashMap<String, Object>();
			int vd_id = baseDao.getSeqId("VoucherDetail_SEQ");
			if (maps.get(0).get("vd_catecode") != null && !maps.get(0).get("vd_catecode").equals("")) {
				Object asscount = baseDao.getFieldDataByCondition("category", "length(ca_assname)-length(replace(ca_assname,'#',''))+1",
						"ca_code='" + maps.get(0).get("vd_catecode") + "'");
				if (asscount != null && !asscount.toString().equals("1") && !asscount.toString().equals(maps.size() + "")) {
					BaseUtil.showError("表格行" + maps.get(0).get("detno") + "记录数" + maps.size() + "与科目辅助核算数" + asscount + "不一致！");
				}
			}
			if (maps.get(0).get("vd_catecode") != null && !maps.get(0).get("vd_catecode").equals("")) {
				map1.put("vd_id", vd_id);
				map1.put("vd_void", id);
				map1.put("vd_detno", formdetno);
				map1.put("vd_explanation", maps.get(0).get("vd_explanation"));
				map1.put("vd_catecode", maps.get(0).get("vd_catecode"));
				map1.put("vd_debit", StringUtil.nvl(maps.get(0).get("vd_debit"), "0"));
				map1.put("vd_credit", StringUtil.nvl(maps.get(0).get("vd_credit"), "0"));
				sqls.add(SqlUtil.getInsertSqlByMap(map1, "VoucherDetail"));
				formdetno++;
			}
			int basedetno = 1;
			for (Map<String, Object> map : maps) {
				if (map.get("vds_asstype") != null && !map.get("vds_asstype").equals("")) {
					if (map.get("vds_assname") == null || map.get("vds_assname").equals("")) {
						map.remove("vds_assname");
						Object[] objs = baseDao.getFieldsDataByCondition("ASSKIND", new String[] { "AK_TABLE", "AK_ASSCODE", "AK_ASSNAME",
								"AK_ID" }, "ak_name='" + map.get("vds_asstype") + "'");
						if (objs != null && objs[0] != null && objs[1] != null && objs[2] != null) {
							if (objs[0].equals("AssKindDetail"))
								map.put("vds_assname",
										baseDao.getFieldDataByCondition("AssKindDetail", "akd_assname",
												"akd_asscode='" + map.get("vds_asscode") + "' and akd_akid =" + objs[3]));
							else
								map.put("vds_assname",
										baseDao.getFieldDataByCondition(objs[0].toString(), objs[2].toString(),
												objs[1] + "='" + map.get("vds_asscode") + "'"));
						}
					} else if (map.get("vds_assname") != null && map.get("vds_asscode") != null) {

						Object vds_assname = null;
						Object[] objs = baseDao.getFieldsDataByCondition("ASSKIND", new String[] { "AK_TABLE", "AK_ASSCODE", "AK_ASSNAME",
								"AK_ID" }, "ak_name='" + map.get("vds_asstype") + "'");
						if (objs != null && objs[0] != null && objs[1] != null && objs[2] != null) {
							if (objs[0].equals("AssKindDetail"))
								vds_assname = baseDao.getFieldDataByCondition("AssKindDetail", "akd_assname",
										"akd_asscode='" + map.get("vds_asscode") + "' and akd_akid =" + objs[3]);
							else
								vds_assname = baseDao.getFieldDataByCondition(objs[0].toString(), objs[2].toString(),
										objs[1] + "='" + map.get("vds_asscode") + "'");
						}
						if (!map.get("vds_assname").equals(vds_assname)) {
							BaseUtil.showError("辅助核算编号:" + map.get("vds_asscode") + "与辅助核算名称:" + map.get("vds_assname") + "不匹配");

						}

					}
					map.put("vds_vdid", vd_id);
					map.remove("vd_explanation");
					map.remove("vd_catecode");
					map.remove("ca_description");
					map.remove("vd_debit");
					map.remove("vd_credit");
					map.remove("str1");
					map.remove("detno");
					map.remove("vd_detno");
					map.remove("iscombine");
					map.put("vds_detno", basedetno);
					map.put("vds_type", "Voucher");
					map.put("vds_id", baseDao.getSeqId("VoucherDetailASS_SEQ"));
					sqls.add(SqlUtil.getInsertSqlByMap(map, "VoucherDetailASS"));
					basedetno++;
				}
			}
		}
		baseDao.execute(sqls);
		// 校验凭证
		voucherDao.validVoucher(id);
		return true;
	}

	@Override
	public String[] printVoucher(int vo_id, String reportName, String condition, String caller) {
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, vo_id);
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 修改打印状态
		baseDao.updateByCondition("Voucher", "vo_printstatuscode='PRINTED',vo_printstatus='" + BaseUtil.getLocalMessage("PRINTED") + "'",
				"vo_id=" + vo_id);
		// 记录操作
		baseDao.logger.print(caller, "vo_id", vo_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, vo_id);
		return keys;
	}

	@Override
	public void vastAudit(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		int nowym = voucherDao.getNowPddetno("Month-A");// 当前期间
		String idStr = CollectionUtil.pluckSqlString(maps, "vo_id");
		String overVo = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(vo_code) from (select vo_code from voucher where vo_id in (" + idStr
						+ ") and rpad(vo_yearmonth,7,'0')<rpad(?,7,'0')) where rownum < 20", String.class, nowym);
		if (overVo != null)
			BaseUtil.showError("不允许操作总账期间已结账的凭证：" + overVo);

		if (baseDao.isDBSetting("Voucher", "noAuditOwnner")) {
			overVo = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(vo_code) from (select vo_code from voucher where vo_id in (" + idStr + ") and vo_recordman='"
							+ SystemSession.getUser().getEm_name() + "') where rownum < 20", String.class);
			if (overVo != null)
				BaseUtil.showError("不允许审核自己制作的凭证：" + overVo);
		}
		// 重新校验凭证状态，可能提交之后，科目资料有变
		SqlRowList rs = baseDao.queryForRowSet("select vo_code from voucher where vo_id in (" + idStr + ") order by vo_code");
		while (rs.next()) {
			baseDao.callProcedure("SP_VOUCHERCHECK", new Object[] { rs.getString(1) });
		}
		String errStr = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(vo_code) from (select vo_code from voucher where vo_id in (" + idStr
						+ ") and vo_errstring is not null) where rownum < 20", String.class);
		if (errStr != null)
			BaseUtil.showError("凭证状态错误：<hr>" + errStr.replace(",", "<br>"));
		List<String> sqls = new ArrayList<String>();
		Operation operation = Operation.AUDIT;
		Status status = operation.getResultStatus();
		String man = SystemSession.getUser().getEm_name();
		sqls.add("update voucher set VO_ISUPDATE=0,vo_status='" + status.display() + "',vo_statuscode='" + status.code() + "',vo_checkby='"
				+ man + "', VO_AUDITER='" + man + "',VO_AUDITDATE=sysdate where vo_id in (" + idStr + ")");
		for (Map<Object, Object> m : maps) {
			sqls.add(baseDao.logger.getMessageLog(operation, "Voucher", "vo_id", m.get("vo_id")).getSql());
		}
		baseDao.execute(sqls);
	}

	@Override
	public void vastUnAudit(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		int nowym = voucherDao.getNowPddetno("Month-A");// 当前期间
		String idStr = CollectionUtil.pluckSqlString(maps, "vo_id");
		String overVo = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(vo_code) from (select vo_code from voucher where vo_id in (" + idStr
						+ ") and rpad(vo_yearmonth,7,'0')<rpad(?,7,'0')) where rownum < 20", String.class, nowym);
		if (overVo != null)
			BaseUtil.showError("不允许操作总账期间已结账的凭证：" + overVo);
		if (baseDao.isDBSetting("Voucher", "noResAuditOther")) {
			overVo = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(vo_code) from (select vo_code from voucher where vo_id in (" + idStr + ") and vo_auditer<>'"
							+ SystemSession.getUser().getEm_name() + "') where rownum < 20", String.class);
			if (overVo != null)
				BaseUtil.showError("不允许反审核他人审核的凭证：" + overVo);
		}
		List<String> sqls = new ArrayList<String>();
		Operation operation = Operation.RESAUDIT;
		String status = "ENTERING";
		String resAuditStatus = baseDao.getDBSetting("Voucher", "voucherStatus");
		if (resAuditStatus != null) {
			// 反审核状态是已提交
			if ("1".equals(resAuditStatus)) {
				status = "COMMITED";
			}
			// 反审核状态是在录入
			if ("0".equals(resAuditStatus)) {
				status = "ENTERING";
			}
		}
		sqls.add("update voucher set VO_ISUPDATE=0,vo_status='" + BaseUtil.getLocalMessage(status) + "',vo_statuscode='" + status
				+ "',vo_checkby=null, VO_AUDITER=null,VO_AUDITDATE=null" + " where vo_id in (" + idStr + ")");
		for (Map<Object, Object> m : maps) {
			sqls.add(baseDao.logger.getMessageLog(operation, "Voucher", "vo_id", m.get("vo_id")).getSql());
		}
		baseDao.execute(sqls);
	}

	@Override
	public Map<String, Object> getVoucherCount() {
		Map<String, Object> map = voucherDao.getJustPeriods("MONTH-A");
		int month = Integer.parseInt(map.get("PD_DETNO").toString());
		int total = baseDao.getCountByCondition("Voucher", "vo_yearmonth=" + month);
		int enter = baseDao.getCountByCondition("Voucher", "vo_yearmonth=" + month + " and vo_statuscode='ENTERING'");
		int commit = baseDao.getCountByCondition("Voucher", "vo_yearmonth=" + month + " and vo_statuscode='COMMITED'");
		int audit = baseDao.getCountByCondition("Voucher", "vo_yearmonth=" + month + " and vo_statuscode='AUDITED'");
		Map<String, Object> obj = new HashMap<String, Object>();
		obj.put("total", total);
		obj.put("enter", enter);
		obj.put("commit", commit);
		obj.put("audit", audit);
		return obj;
	}

	@Override
	public List<VoucherDetailAss> findAss(int vo_id) {
		return voucherDao.getAssByVoucherId(vo_id);
	}

	@Override
	public void auditDuring(int year, boolean myear, boolean eyear) {
		String yearmonth = "";
		int count = 0;
		if (myear) {
			yearmonth = year + "061";
			count = baseDao.getCount("select count(1) from auditduring where ad_yearmonth=" + yearmonth);
			if (count == 0) {
				int vdid = baseDao.getSeqId("AUDITDURING_SEQ");
				String sql = "Insert Into Auditduring(Ad_Id,Ad_Yearmonth,Ad_Date,Ad_Remark,ad_isuse) Values (" + vdid + "," + yearmonth
						+ ",To_Date('" + year + "-06-30','yyyy-mm-dd'),'年中审计期间',1)";
				baseDao.execute(sql);
			} else {
				baseDao.execute("update Auditduring set ad_isuse=1 where Ad_Yearmonth=" + yearmonth);
			}
		} else {
			yearmonth = year + "061";
			count = baseDao.getCount("select count(1) from auditduring where ad_yearmonth=" + yearmonth);
			if (count > 0) {
				baseDao.execute("update Auditduring set ad_isuse=0 where Ad_Yearmonth=" + yearmonth);
			}
		}
		if (eyear) {
			yearmonth = year + "121";
			count = baseDao.getCount("select count(1) from auditduring where ad_yearmonth=" + yearmonth);
			if (count == 0) {
				int vdid = baseDao.getSeqId("AUDITDURING_SEQ");
				String sql = "Insert Into Auditduring(Ad_Id,Ad_Yearmonth,Ad_Date,Ad_Remark,ad_isuse) Values (" + vdid + "," + yearmonth
						+ ",To_Date('" + year + "-12-31','yyyy-mm-dd'),'年末审计期间',1)";
				baseDao.execute(sql);
			} else {
				baseDao.execute("update Auditduring set ad_isuse=1 where Ad_Yearmonth=" + yearmonth);
			}
		} else {
			yearmonth = year + "121";
			count = baseDao.getCount("select count(1) from auditduring where ad_yearmonth=" + yearmonth);
			if (count > 0) {
				baseDao.execute("update Auditduring set ad_isuse=0 where Ad_Yearmonth=" + yearmonth);
			}
		}
	}

	/**
	 * 红冲凭证
	 */
	@Override
	@Transactional
	public JSONObject rushRedVoucher(int id) {
		Map<String, Object> dif = new HashMap<String, Object>();
		// 凭证
		int nId = baseDao.getSeqId("VOUCHER_SEQ");
		dif.put("vo_id", nId);
		Map<String, Object> period = voucherDao.getJustPeriods("Month-A");
		dif.put("vo_date", DateUtil.parseDateToOracleString(null, DateUtil.parse(String.valueOf(period.get("PD_ENDDATE")), null)));
		Object yearmonth = period.get("PD_DETNO");
		dif.put("vo_yearmonth", yearmonth);
		String lead = baseDao.getFieldValue("voucher", "vo_lead", "vo_id=" + id, String.class);
		String num = voucherDao.getVoucherNumber(String.valueOf(yearmonth), lead, null);
		dif.put("vo_number", num);
		String code = baseDao.sGetMaxNumber("Voucher", 2);
		dif.put("vo_code", "'" + code + "'");
		dif.put("vo_emid", SystemSession.getUser().getEm_id());
		dif.put("vo_recordman", "'" + SystemSession.getUser().getEm_name() + "'");
		dif.put("vo_status", "'" + Status.ENTERING.display() + "'");
		dif.put("vo_statuscode", "'" + Status.ENTERING.code() + "'");
		dif.put("vo_recorddate", "sysdate");
		dif.put("vo_source", "null");
		dif.put("vo_sourcecode", "null");
		dif.put("vo_createkind", "null");
		dif.put("vo_writeby", "null");
		dif.put("vo_checkby", "null");
		dif.put("vo_auditer", "null");
		dif.put("vo_auditdate", "null");
		dif.put("vo_printstatus", "'" + Status.UNPRINT.display() + "'");
		dif.put("vo_isupdate", "0");
		baseDao.copyRecord("Voucher", "Voucher", "vo_id=" + id, dif);
		// 凭证明细
		SqlRowList list = baseDao
				.queryForRowSet(
						"SELECT vd_id,case when nvl(vo_lead,' ')=' ' then to_char(vo_number) else vo_lead||vo_number end vo_code,to_char(vo_date,'yyyy-mm-dd') vo_date,vd_explanation,vd_doubledebit,vd_doublecredit,vd_debit,vd_credit FROM voucher,VoucherDetail WHERE vo_id=vd_void and vd_void=?",
						id);
		SqlRowList ass = null;
		Integer dId = null;
		while (list.next()) {
			dif = new HashMap<String, Object>();
			dId = baseDao.getSeqId("VOUCHERDETAIL_SEQ");
			dif.put("vd_id", dId);
			dif.put("vd_void", nId);
			dif.put("vd_explanation",
					"'冲销" + list.getObject("vo_date") + "：" + list.getObject("vo_code") + "号凭证[" + list.getObject("vd_explanation") + "]'");
			dif.put("vd_doubledebit", -1 * list.getGeneralDouble("vd_doubledebit"));
			dif.put("vd_doublecredit", -1 * list.getGeneralDouble("vd_doublecredit"));
			dif.put("vd_debit", -1 * list.getGeneralDouble("vd_debit"));
			dif.put("vd_credit", -1 * list.getGeneralDouble("vd_credit"));
			dif.put("vd_flowcode", "null");
			dif.put("vd_flowname", "null");
			dif.put("vd_creditcashflow", 0);
			dif.put("vd_creditcashflow", 0);
			baseDao.copyRecord("VoucherDetail", "VoucherDetail", "vd_id=" + list.getInt("vd_id"), dif);
			// Copy 辅助核算
			ass = baseDao.queryForRowSet("SELECT vds_id FROM VoucherDetailAss WHERE vds_vdid=?", list.getInt("vd_id"));
			while (ass.next()) {
				dif = new HashMap<String, Object>();
				dif.put("vds_id", baseDao.getSeqId("VOUCHERDETAILASS_SEQ"));
				dif.put("vds_vdid", dId);
				baseDao.copyRecord("VoucherDetailAss", "VoucherDetailAss", "vds_id=" + ass.getInt("vds_id"), dif);
			}
		}
		JSONObject obj = new JSONObject();
		obj.put("vo_id", nId);
		obj.put("vo_number", num);
		obj.put("vo_code", code);
		return obj;
	}

}
