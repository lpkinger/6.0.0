package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VoucherDao;

import com.uas.erp.service.fa.BankPlanService;

@Service("bankPlanService")
public class BankPlanServiceImpl implements BankPlanService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private VoucherDao voucherDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveBankPlan(String caller, String formStore, String gridStore,
			String assStore, String assMainStore) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> ass = BaseUtil.parseGridStoreToMaps(assStore);
		handlerService.beforeSave(caller, new Object[] { store, grid, ass });
		// 删除不是主表字段
		if (store.containsKey("cu_name")) {
			store.remove("cu_name");
		}
		if (store.containsKey("ca_asstype")) {
			store.remove("ca_asstype");
		}
		if (store.containsKey("ca_assname")) {
			store.remove("ca_assname");
		}
		int yearmonth = voucherDao.getPeriodsFromDate("Month-C",
				store.get("ab_date").toString());
		store.put("ab_yearmonth", yearmonth);
		// 主表form中添加的默认信息
		store.put("ab_statuscode", "UNPOST");
		store.put("ab_status", BaseUtil.getLocalMessage("UNPOST"));
		store.put("ab_printstatuscode", "UNPRINT");
		store.put("ab_printstatus", BaseUtil.getLocalMessage("UNPRINT"));
		store.put("ab_paystatuscode", "UNCOLLECT");
		store.put("ab_paystatus", BaseUtil.getLocalMessage("UNCOLLECT"));
		// int ab_id = Integer.parseInt(store.get("ab_id").toString());
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "ARBill"));
		// //主表辅助核算保存S
		// for(Map<Object, Object> am:assMain){
		// am.put("ass_conid", ab_id);
		// am.put("ass_id", baseDao.getSeqId("PRERECASS_SEQ"));
		//
		// }
		// baseDao.execute(SqlUtil.getInsertSqlbyGridStore(assMain,
		// "PreRecAss"));
		// 主表辅助核算保存O
		Map<Object, List<Map<Object, Object>>> list = BaseUtil.groupMap(ass,
				"dass_condid");
		int id;
		for (Map<Object, Object> map : grid) {
			if (map.containsKey("ca_asstype")) {
				map.remove("ca_asstype");
			}
			if (map.containsKey("ca_assname")) {
				map.remove("ca_assname");
			}
			if (map.containsKey("pr_detail")) {
				map.remove("pr_detail");
			}
			if (map.containsKey("pd_invototal")) {
				map.remove("pd_invototal");
			}
			if (map.containsKey("abd_totalbillprice")) {
				map.remove("abd_totalbillprice");
			}
			if (map.containsKey("gsd_amount")) {
				map.remove("gsd_amount");
			}
			if (map.containsKey("gsd_invototal")) {
				map.remove("gsd_invototal");
			}

			id = baseDao.getSeqId("ARBILLDETAIL_SEQ");
			ass = list.get(String.valueOf(map.get("abd_id")));
			if (ass != null) {
				for (Map<Object, Object> m : ass) {// PreRecDetailAss
					m.put("dass_condid", id);
					m.put("dass_id", baseDao.getSeqId("ARBILLDETAILASS_SEQ"));
				}
				baseDao.execute(SqlUtil.getInsertSqlbyGridStore(ass,
						"ARBillDetailAss"));
			}
			map.put("abd_id", id);
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(grid, "ARBillDetail"));
		try {
			// 记录操作
			baseDao.logger.save(caller, "ab_id", store.get("ab_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		handlerService.afterSave(caller, new Object[] { store, grid, ass });
	}

	@Override
	public void updateBankPlanById(String caller, String formStore,
			String gridStore, String assStore, String assMainStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> ass = BaseUtil.parseGridStoreToMaps(assStore);
		// 只能修改[在录入]的资料!
		handlerService
				.beforeUpdate(caller, new Object[] { store, gstore, ass });
		Object status[] = baseDao.getFieldsDataByCondition("ARBill",
				new String[] { "ab_auditstatuscode", "ab_statuscode" },
				"ab_id=" + store.get("ab_id"));
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.update_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.update_onlyUnPost"));
		}
		int yearmonth = voucherDao.getPeriodsFromDate("Month-C",
				store.get("ab_date").toString());
		store.put("ab_yearmonth", yearmonth);
		if (store.containsKey("cu_name")) {
			store.remove("cu_name");
		}
		if (store.containsKey("ca_asstype")) {
			store.remove("ca_asstype");
		}
		if (store.containsKey("ca_assname")) {
			store.remove("ca_assname");
		}
		for (Map<Object, Object> g : gstore) {
			if (g.containsKey("ca_asstype")) {
				g.remove("ca_asstype");
			}
			if (g.containsKey("ca_assname")) {
				g.remove("ca_assname");
			}
			if (g.containsKey("pr_detail")) {
				g.remove("pr_detail");
			}
			if (g.containsKey("pd_invototal")) {
				g.remove("pd_invototal");
			}
			if (g.containsKey("abd_totalbillprice")) {
				g.remove("abd_totalbillprice");
			}
			if (g.containsKey("gsd_amount")) {
				g.remove("gsd_amount");
			}
			if (g.containsKey("gsd_invototal")) {
				g.remove("gsd_invototal");
			}
		}
		// 保存arbilldetail前 先判断本次开票数量是否发生改变 还原应收批量开票的数据 逻辑开始
		if ("ARBill!IRMA".equals(caller)) {
			for (Map<Object, Object> map : gstore) {
				String sourcekind = map.get("abd_sourcekind").toString();
				int gsd_id = Integer.parseInt(map.get("abd_sourcedetailid")
						.toString());
				int abd_id = Integer.parseInt(map.get("abd_id").toString());
				String selectqty = "select abd_thisvoqty,abd_aramount from arbilldetail where abd_id='"
						+ abd_id + "'";
				SqlRowList rs = baseDao.queryForRowSet(selectqty);
				// 数据库里保存的本次开票数量
				double abd_thisvoqty_old = 0;
				// 数据库里保存的本次开票价税金额
				double abd_aramount_old = 0;
				if (rs.next()) {
					abd_thisvoqty_old = rs.getDouble(1);
					abd_aramount_old = rs.getDouble(2);
				}
				int pd_id = Integer.parseInt(map.get("abd_pdid").toString());
				String selectinvoqty = "";
				if (sourcekind.trim().equals("GOODSSEND")) {
					selectinvoqty = "select gsd_showinvoqty from goodssenddetail where gsd_id= '"
							+ gsd_id + "'";
				} else {
					selectinvoqty = "select pd_showinvoqty from prodiodetail where pd_id = '"
							+ pd_id + "'";
				}
				SqlRowList rs1 = baseDao.queryForRowSet(selectinvoqty);
				// 已转发票数
				double pd_invoqty = 0;
				if (rs1.next()) {
					pd_invoqty = rs1.getDouble(1);
				}
				// 修改后的本次开票数量
				double abd_thisvoqty = Double.parseDouble(map.get(
						"abd_thisvoqty").toString());
				double abd_aramount = Double.parseDouble(map
						.get("abd_aramount").toString()); // 修改后的本次开票价税金额
				// 发货数量
				double abd_qty = Double.parseDouble(map.get("abd_qty")
						.toString()); // 发货数量

				// abd_qty - pd_invoqty = 剩余的 . 剩余的+abd_thisvoqty >abd_qty
				// abd_qty -pd_invoqty +abd_thisvoqty>abd_qty abd_thisvoqty -
				// pd_invoqty >0
				// 界面上已经对本次开票数进行了修改

				if (abd_qty >= 0) {
					double bo = abd_qty - pd_invoqty + abd_thisvoqty_old
							- abd_thisvoqty;
					if (abd_thisvoqty != abd_thisvoqty_old) {
						// if(pd_invoqty+abd_thisvoqty>abd_qty){
						if (bo < 0) {
							// 修改后的发票数+已开票数大于发货数量 报错
							BaseUtil.showError(BaseUtil
									.getLocalMessage("fa.ars.arbill.turnArbillQtyissmaill"));
						} else {
							// 修改后的发票数+已开票数 小于等于发货数量 可进行正常修改
							// 根据单据中的来源id 查找prodiodetail 表中对应的数据
							// int pd_invoqty =
							// abd_invoqty-(abd_thisvoqty_old-abd_thisvoqty);
							double sheyu = abd_thisvoqty_old - abd_thisvoqty;
							double yue = abd_aramount_old - abd_aramount;
							String updatesqlm = "";
							String updatesqld = "";
							String updatesqla = "";
							if (sourcekind.trim().equals("GOODSSEND")) {
								updatesqlm = "update goodssend set gs_invostatuscode='PARTAR',gs_invostatus='"
										+ BaseUtil.getLocalMessage("PARTAR")
										+ "' "
										+ "where gs_id = (select gsd_gsid from goodssenddetail where gsd_id = '"
										+ gsd_id + "')";
								updatesqld = "update goodssenddetail set gsd_statuscode ='PARTAR',gsd_showinvoqty = gsd_showinvoqty-("
										+ sheyu
										+ ") where gsd_id = '"
										+ gsd_id
										+ "'";
								updatesqla = "update arbilldetail set abd_thisvoqty='"
										+ abd_thisvoqty
										+ "' where abd_id ='"
										+ abd_id + "'";

							} else {
								updatesqlm = "update prodinout set pi_billstatuscode='PARTAR',pi_billstatus='"
										+ BaseUtil.getLocalMessage("PARTAR")
										+ "' "
										+ "where pi_id = (select pd_piid from prodiodetail where pd_id = '"
										+ pd_id + "')";
								updatesqld = "update prodiodetail set pd_auditstatus ='PARTAR',pd_showinvoqty = pd_showinvoqty-("
										+ sheyu
										+ ") where pd_id = '"
										+ pd_id
										+ "'";
								updatesqla = "update arbilldetail set abd_thisvoqty='"
										+ abd_thisvoqty
										+ "' where abd_id ='"
										+ abd_id + "'";
							}

							List<String> sqllist = new ArrayList<String>();
							sqllist.add(updatesqlm);
							sqllist.add(updatesqld);
							sqllist.add(updatesqla);
							baseDao.execute(sqllist);
							// 如果pd_invoqty 变为0 则数据状态为已审核 审核/部分开票 AUDITED/PARTAR
							// 改变数据状态为部分开票 改变pd_invoqty（已转发票数）
							// abd_invoqty(arbill中记录的已转发票数)
						}
					}
				} else {
					double bo = abd_qty - pd_invoqty + abd_thisvoqty_old
							- abd_thisvoqty;
					if (abd_thisvoqty != abd_thisvoqty_old) {
						// if(pd_invoqty+abd_thisvoqty>abd_qty){
						if (bo > 0) {
							// 修改后的发票数+已开票数大于发货数量 报错
							BaseUtil.showError(BaseUtil
									.getLocalMessage("fa.ars.arbill.turnArbillQtyissmaill"));
						} else {
							// 修改后的发票数+已开票数 小于等于发货数量 可进行正常修改
							// 根据单据中的来源id 查找prodiodetail 表中对应的数据
							// int pd_invoqty =
							// abd_invoqty-(abd_thisvoqty_old-abd_thisvoqty);
							double sheyu = abd_thisvoqty_old - abd_thisvoqty;
							double yue = abd_aramount_old - abd_aramount;
							String updatesqlm = "update prodinout set pi_billstatuscode='PARTAR',pi_billstatus='"
									+ BaseUtil.getLocalMessage("PARTAR")
									+ "' "
									+ "where pi_id = (select pd_piid from prodiodetail where pd_id = '"
									+ pd_id + "')";
							String updatesqld = "update prodiodetail set pd_auditstatus ='PARTAR',pd_showinvoqty = pd_showinvoqty-("
									+ sheyu + ") where pd_id = '" + pd_id + "'";
							String updatesqla = "update arbilldetail set abd_thisvoqty='"
									+ abd_thisvoqty
									+ "' where abd_id ='"
									+ abd_id + "'";
							List<String> sqllist = new ArrayList<String>();
							sqllist.add(updatesqlm);
							sqllist.add(updatesqld);
							sqllist.add(updatesqla);
							baseDao.execute(sqllist);
							// 如果pd_invoqty 变为0 则数据状态为已审核 审核/部分开票 AUDITED/PARTAR
							// 改变数据状态为部分开票 改变pd_invoqty（已转发票数）
							// abd_invoqty(arbill中记录的已转发票数)
						}
					}
				}
			}
			// 还原应收批量开票的数据 逻辑结束
		}
		// 修改ARBill
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "ARBill",
				"ab_id"));
		Map<Object, List<Map<Object, Object>>> list = BaseUtil.groupMap(ass,
				"dass_condid");
		int id;
		List<String> gridSql = null;
		if (gstore.size() > 0) {
			gridSql = new ArrayList<String>();
			for (Map<Object, Object> s : gstore) {
				if (s.get("abd_id") == null || s.get("abd_id").equals("")
						|| s.get("abd_id").equals("0")
						|| Integer.parseInt(s.get("abd_id").toString()) <= 0) {
					id = baseDao.getSeqId("ARBILLDETAIL_SEQ");
					ass = list.get(String.valueOf(s.get("abd_id")));
					if (ass != null) {
						for (Map<Object, Object> m : ass) {// VoucherDetailAss
							m.put("dass_condid", id);
							m.put("dass_id",
									baseDao.getSeqId("ARBILLDETAILASS_SEQ"));
						}
						baseDao.execute(SqlUtil.getInsertSqlbyGridStore(ass,
								"ARBillDetailAss"));
					}
					s.put("abd_id", id);
					gridSql.add(SqlUtil.getInsertSqlByMap(s, "ARBillDetail"));
				} else {
					gridSql.add(SqlUtil.getUpdateSqlByFormStore(s,
							"ARBillDetail", "abd_id"));
					id = Integer.parseInt(s.get("abd_id").toString());
					ass = list.get(String.valueOf(id));
					if (ass != null) {
						List<String> sqls = SqlUtil.getUpdateSqlbyGridStore(
								ass, "ARBillDetailAss", "dass_id");
						for (Map<Object, Object> m : ass) {
							if (m.get("dass_id") == null
									|| m.get("dass_id").equals("")
									|| m.get("dass_id").equals("0")
									|| Integer.parseInt(m.get("dass_id")
											.toString()) <= 0) {
								m.put("dass_id",
										baseDao.getSeqId("ARBILLDETAILASS_SEQ"));
								sqls.add(SqlUtil.getInsertSqlByMap(m,
										"ARbillDetailAss"));
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
					List<String> sqls = SqlUtil.getUpdateSqlbyGridStore(ass,
							"ARBillDetailAss", "dass_id");
					for (Map<Object, Object> m : ass) {
						if (m.get("dass_id") == null
								|| m.get("dass_id").equals("")
								|| m.get("dass_id").equals("0")
								|| Integer
										.parseInt(m.get("dass_id").toString()) <= 0) {
							m.put("dass_id",
									baseDao.getSeqId("ARBILLDETAILASS_SEQ"));
							sqls.add(SqlUtil.getInsertSqlByMap(m,
									"ARBillDetailAss"));
						}
					}
					baseDao.execute(sqls);
				}
			}
		}
		// //修改ARBillDetail
		// List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
		// "ARBillDetail", "abd_id");
		// for(Map<Object, Object> s:gstore){
		// if(s.get("abd_id") == null
		// ||Integer.parseInt(s.get("abd_id").toString())==0){//新添加的数据，id不存在
		// int id = baseDao.getSeqId("ARBillDETAIL_SEQ");
		// String sql = SqlUtil.getInsertSqlByMap(s, "ARBillDetail", new
		// String[]{"abd_id"}, new Object[]{id});
		// gridSql.add(sql);
		// }
		// }
		// baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "ab_id", store.get("ab_id"));
		// 更新ARBill主表的金额
		baseDao.execute("update arbill set ab_aramount=round((select sum(abd_aramount) from arbilldetail where abd_abid="
				+ store.get("ab_id") + "),2) where ab_id=" + store.get("ab_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore, ass });
	}

	@Override
	public void deleteBankPlan(String caller, int ab_id) {
		Object status[] = baseDao.getFieldsDataByCondition("ARBill",
				new String[] { "ab_auditstatuscode", "ab_statuscode" },
				"ab_id=" + ab_id);
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.delete_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, ab_id);
		// 删除ARBill
		baseDao.deleteById("ARBill", "ab_id", ab_id);
		// 删除ARBillDetail
		baseDao.deleteById("ARBilldetail", "abd_abid", ab_id);
		// 记录操作
		baseDao.logger.delete(caller, "ab_id", ab_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ab_id);
	}

	@Override
	public String[] printBankPlan(String caller, int ab_id, String reportName,
			String condition) {
		// 只能对状态为[未打印]的订单进行打印操作! 已打印的 也可继续打印
		// Object status = baseDao.getFieldDataByCondition("ARBill",
		// "ab_printstatuscode", "ab_id=" + ab_id);
		// // if(!status.equals("UNPRINT")){
		// //
		// BaseUtil.showError(BaseUtil.getLocalMessage("fa.ars.ARBill.audit_onlyCommited"));
		// // }
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, ab_id);
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		baseDao.updateByCondition(
				"ARBill",
				"ab_printstatuscode='PRINTED',ab_printstatus='"
						+ BaseUtil.getLocalMessage("PRINTED") + "'", "ab_id="
						+ ab_id);
		// 记录操作
		baseDao.logger.print(caller, "ab_id", ab_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, ab_id);
		return keys;
	}

	@Override
	public void auditBankPlan(String caller, int ab_id) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ARBill",
				"ab_auditstatuscode", "ab_id=" + ab_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ab_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"ARBill",
				"ab_auditstatuscode='AUDITED',ab_auditstatus='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',AB_AUDITMAN='"
						+ SystemSession.getUser().getEm_name()
						+ "',AB_AUDITdate=sysdate", "ab_id=" + ab_id);
		// 记录操作
		baseDao.logger.audit(caller, "ab_id", ab_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ab_id);
	}

	@Override
	public void resAuditBankPlan(String caller, int ab_id) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] objs = baseDao.getFieldsDataByCondition("ARBill",
				new String[] { "ab_auditstatuscode", "ab_statuscode" },
				"ab_id=" + ab_id);
		if (!objs[0].equals("AUDITED") || objs[1].equals("POSTED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resAudit_onlyAudit"));
		}
		handlerService.beforeResAudit(caller, ab_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"ARBill",
				"ab_auditstatuscode='ENTERING',ab_auditstatus='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',AB_AUDITMAN='',AB_AUDITdate=null", "ab_id="
						+ ab_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ab_id", ab_id);
		handlerService.afterResAudit(caller, ab_id);
	}

	@Override
	public void submitBankPlan(String caller, int ab_id) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ARBill",
				"ab_auditstatuscode", "ab_id=" + ab_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ab_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"ARBill",
				"ab_auditstatuscode='COMMITED',ab_auditstatus='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "ab_id="
						+ ab_id);
		// 记录操作
		baseDao.logger.submit(caller, "ab_id", ab_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ab_id);
	}

	@Override
	public void resSubmitBankPlan(String caller, int ab_id) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ARBill",
				"ab_auditstatuscode", "ab_id=" + ab_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeSubmit(caller, ab_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"ARBill",
				"ab_auditstatuscode='ENTERING',ab_auditstatus='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "ab_id="
						+ ab_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ab_id", ab_id);
		handlerService.afterResSubmit(caller, ab_id);
	}

}
