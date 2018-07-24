package com.uas.erp.service.pm.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.DispatchService;

@Service("dispatchService")
public class DispatchServiceImpl implements DispatchService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveDispatch(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Dispatch", "di_code='" + store.get("di_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 相同日期、相同车间、相同线别的只能存在一张生产日报
		if (!baseDao.isDBSetting(caller, "oneLineOneDepartOneDate")) {
			if (baseDao.getCount("select count(*) from Dispatch where to_char(di_date,'yyyy-mm-dd')='" + store.get("di_date")
					+ "' and di_lowdepartname='" + store.get("di_lowdepartname") + "' and di_groupcode='" + store.get("di_groupcode")
					+ "' and di_id<>" + store.get("di_id")) > 0) {
				BaseUtil.showError("相同日期、相同车间、相同线别的只能存在一张工时损时！");
			}
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid }); // 保存Dispatch
		double tQty = 0;
		double qty = 0;
		double yqty = 0;
		if (!baseDao.isDBSetting(caller, "uncheckMakeqty")) {
			StringBuffer sb = new StringBuffer();
			for (Map<Object, Object> s : grid) {
				if (s.get("did_makecode") != null && !"".equals(s.get("did_makecode"))) {
					tQty = Double.parseDouble(String.valueOf(s.get("did_overqty")));
					yqty = baseDao.getFieldValue("DispatchDetail", "nvl(sum(did_overqty),0)", "did_makecode='" + s.get("did_makecode")
							+ "' and did_stepcode='" + s.get("did_stepcode") + "' AND did_id <>" + s.get("did_id"), Double.class);
					qty = baseDao.getFieldValue("Make", "nvl(ma_qty,0)", "ma_code='" + s.get("did_makecode") + "'", Double.class);
					if (qty < yqty + tQty) {
						sb.append("制造单号：").append(s.get("did_makecode")).append("超出数量：").append((yqty + tQty - qty)).append("<br>");
					}
				}
			}
			if (sb.length() > 0) {
				BaseUtil.showError("明细行完成累计数超出制造单数量！" + sb.toString());
			}
		}
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Dispatch", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "DispatchDetail", "did_id");
		baseDao.execute(gridSql);
		getDatas(store.get("di_id"), caller);
		// 记录操作
		baseDao.logger.save(caller, "di_id", store.get("di_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });

	}

	private void getDatas(Object di_id, String caller) {
		if ("Dispatch".equals(caller)) {
			// 获取定额、定员、完工数、报废数、完工不良品数
			SqlRowList rs = baseDao.queryForRowSet("select * from dispatch,dispatchdetail where di_id=did_diid and did_diid=? ", di_id);
			while (rs.next()) {
				Object did_id = rs.getObject("did_id");
				Object bomid = baseDao.getFieldDataByCondition("bom", "bo_id", "bo_mothercode='" + rs.getObject("did_prodcode") + "' ");
				Object ration = 0;
				bomid = bomid == null ? 0 : bomid;
				if ("计件".equals(rs.getObject("di_type"))) {
					ration = baseDao.getFieldValue("(select ra_price from Ration WHERE ra_mothercode='" + rs.getString("did_jobprodcode")
							+ "' and ra_topbomid=" + bomid + " and ra_statuscode='AUDITED' order by ra_auditdate desc,ra_indate desc)",
							"ra_price", "rownum<2", Object.class);
					if (ration == null || Double.parseDouble(ration.toString()) == 0) {
						ration = baseDao.getFieldValue(
								"(select ra_price from Ration WHERE ra_mothercode='" + rs.getString("did_jobprodcode")
										+ "' and ra_statuscode='AUDITED' order by ra_auditdate desc,ra_indate desc)", "ra_price",
								"rownum<2", Object.class);
						ration = ration == null ? 0 : ration;
					}
				}
				// "定额"、“额定生产人数”根据 "产品编码"和"在制品编号" 》》定额列表的“ST值”和“定员”
				ration = baseDao.getFieldValue("(select ra_st from Ration WHERE ra_mothercode='" + rs.getString("did_jobprodcode")
						+ "' and ra_topbomid=" + bomid + " and ra_statuscode='AUDITED' order by ra_auditdate desc,ra_indate desc)",
						"ra_st", "rownum<2", Object.class);
				if (ration == null || Double.parseDouble(ration.toString()) == 0) {
					ration = baseDao
							.getFieldValue("(select ra_st from Ration WHERE ra_mothercode='" + rs.getString("did_jobprodcode")
									+ "' and ra_statuscode='AUDITED' order by ra_auditdate desc,ra_indate desc)", "ra_st", "rownum<2",
									Object.class);
					ration = ration == null ? 0 : ration;
				}
				baseDao.execute("update dispatchdetail set did_quota=" + ration + " where did_id=" + did_id);

				// 额定生产人数
				ration = baseDao.getFieldValue("(select ra_dy from Ration WHERE ra_mothercode='" + rs.getString("did_jobprodcode")
						+ "' and ra_topbomid=" + bomid + " and ra_statuscode='AUDITED' order by ra_auditdate desc,ra_indate desc)",
						"ra_dy", "rownum<2", Object.class);
				if (ration == null || Double.parseDouble(ration.toString()) == 0) {
					ration = baseDao
							.getFieldValue("(select ra_dy from Ration WHERE ra_mothercode='" + rs.getString("did_jobprodcode")
									+ "' and ra_statuscode='AUDITED' order by ra_auditdate desc,ra_indate desc)", "ra_dy", "rownum<2",
									Object.class);
					ration = ration == null ? 0 : ration;
				}
				baseDao.execute("update dispatchdetail set DID_RATIONUSER=" + ration + " where did_id=" + did_id);
				baseDao.execute("update dispatchdetail set did_price=" + ration + " where did_id=" + did_id);
				baseDao.execute("update dispatchdetail set did_quota=0 where did_id=" + did_id
						+ " and did_makecode in (select ma_code from make where nvl(ma_kind,' ')='返修工单')");
				// 完工数量、 完工不良品数、报废数
				if (!"计件".equals(rs.getObject("di_type")) && !"非标准".equals(rs.getObject("di_type"))) {
					// 1、完工良品数: 不等于"不良返修"+"不良入库"的完工入库单
					baseDao.execute("update dispatchdetail set did_finishqty=nvl((select sum(pd_inqty) from prodiodetail where pd_piclass='完工入库单' and pd_ordercode=did_makecode and pd_jobcode=did_jobcode and pd_description not in ('不良返修','不良入库') and pd_status>0),0) where did_id="
							+ did_id);
					// 1、完工良品数:工序转移+类型等于“工艺变更”的工序跳转单
					baseDao.execute("update dispatchdetail set did_finishqty=nvl(did_finishqty,0) + nvl((select sum(si_qty) from stepio,makecraft where si_makecode=mc_makecode and st_outno=mc_detno and (nvl(st_class,' ')='工序转移' or (nvl(st_class,' ')='工序跳转' and nvl(st_type,' ')='工艺变更')) and did_jobcode=mc_code and st_outgroupcode='"
							+ rs.getObject("di_groupcode") + "' and si_statuscode='POSTED'),0) where did_id=" + did_id);
					// 2、完工不良数：描述等于“不良返修”、“不良入库”的完工入库单
					baseDao.execute("update dispatchdetail set did_ngfinishqty=nvl((select sum(pd_inqty) from prodiodetail where pd_piclass='完工入库单' and pd_ordercode=did_makecode and pd_jobcode=did_jobcode and pd_description in ('不良返修','不良入库') and pd_status>0),0) where did_id="
							+ did_id);
					// 2、完工不良数:类型等于“不良返修”、“返修完工”的工序跳转单
					baseDao.execute("update dispatchdetail set did_ngfinishqty=nvl(did_ngfinishqty,0) + nvl((select sum(si_qty) from stepio,makecraft where si_makecode=mc_makecode and st_outno=mc_detno and nvl(st_class,' ')='工序跳转' and (nvl(st_type,' ')='不良返修' or nvl(st_type,' ')='返修完工') and did_jobcode=mc_code and st_outgroupcode='"
							+ rs.getObject("di_groupcode") + "' and si_statuscode='POSTED'),0) where did_id=" + did_id);
					// 3、报废数
					baseDao.execute("update dispatchdetail set did_ngfinishqty=nvl((select sum(si_qty) from stepio,makecraft where si_makecode=mc_makecode and st_outno=mc_detno and nvl(st_class,' ')='工序报废' and did_jobcode=mc_code and st_outgroupcode='"
							+ rs.getObject("di_groupcode")
							+ "' and si_statuscode IN ('POSTED','COMMITED','AUDITED')),0) where did_id="
							+ did_id);
				}
			}
			// 主表数据更新
			baseDao.execute("update dispatch set di_ration=nvl((select sum(nvl(did_quota,0)) from DispatchDetail where did_diid=di_id),0) where di_id="
					+ di_id);
			baseDao.execute("update dispatch set di_actuser=nvl((select count(distinct did_emcode) from DispatchDetail where did_diid=di_id),0) where di_id="
					+ di_id);
			baseDao.execute("update dispatch set di_losshours=nvl((select sum(nvl(did_losshours,0)) from DispatchDetail where did_diid=di_id),0) where di_id="
					+ di_id);
			baseDao.execute("update dispatch set di_actmakehours=nvl((select sum(nvl(did_workhours,0)) from DispatchDetail where did_diid=di_id),0) where di_id="
					+ di_id);
			baseDao.execute("update dispatch set di_usehours=nvl((select sum(nvl(did_workhours,0)+nvl(did_losshours,0)) from DispatchDetail where did_diid=di_id),0) where di_id="
					+ di_id);
			baseDao.execute("update dispatch set di_totalstophours=nvl((select sum(nvl(did_stophours,0)) from DispatchDetail where did_diid=di_id),0) where di_id="
					+ di_id);
			baseDao.execute("update dispatch set di_finishqty=nvl((select sum(nvl(did_finishqty,0)) from DispatchDetail where did_diid=di_id),0) where di_id="
					+ di_id);
			baseDao.execute("update dispatch set di_ngfinishqty=nvl((select sum(nvl(did_ngfinishqty,0)) from DispatchDetail where did_diid=di_id),0) where di_id="
					+ di_id);
			baseDao.execute("update dispatch set di_scrapqty=nvl((select sum(nvl(did_scrapqty,0)) from DispatchDetail where did_diid=di_id),0) where di_id="
					+ di_id);
			baseDao.execute("update dispatch set di_ngrate=round(nvl(di_ngfinishqty,0)/(nvl(di_finishqty,0)+nvl(di_ngfinishqty,0)+nvl(di_scrapqty,0))*100,4) where di_id="
					+ di_id + " and nvl(di_finishqty,0)+nvl(di_ngfinishqty,0)+nvl(di_scrapqty,0)<>0");
			baseDao.execute("update dispatch set di_scraprate=round(nvl(di_scrapqty,0)/(nvl(di_finishqty,0)+nvl(di_ngfinishqty,0)+nvl(di_scrapqty,0))*100,4) where di_id="
					+ di_id + " and nvl(di_finishqty,0)+nvl(di_ngfinishqty,0)+nvl(di_scrapqty,0)<>0");
			baseDao.execute("update dispatch set di_efficiency=round((nvl(di_finishqty,0)+nvl(di_ngfinishqty,0))*di_ration/di_actmakehours/3600,4) where di_id="
					+ di_id + " and nvl(di_actmakehours,0)<>0 and nvl(di_ration,0)<>0");
			baseDao.execute("update dispatch set di_validhours=round((nvl(di_finishqty,0)+nvl(di_ngfinishqty,0))*di_ration/3600,4) where di_id="
					+ di_id + " and nvl(di_ration,0)<>0");
			baseDao.execute("update dispatch set di_efficiency=1,di_validhours=nvl(di_usehours,0) where di_id=" + di_id
					+ " and nvl(di_ration,0)=0");
			baseDao.execute("update Dispatch set di_validhours=0,di_efficiency=0 where di_id=" + di_id + " and di_type='计件'");
			baseDao.execute("update Dispatch set di_totalworkhours=di_actmakehours,di_totallosshours=di_losshours where di_id=" + di_id);
		} else {
			// 更新计价系数默认值为 1
			baseDao.execute("update DispatchDetail set did_processcoefficient=1 where did_diid=? and nvl(did_processcoefficient,0)=0",
					di_id);
			// 根据配置更新 【计价类型】、【加工单价】、【标准工时】取制造单中对应的工序加工单价、标准工时；
			if (baseDao.isDBSetting(caller, "getDataFromMakeStep")) {
				baseDao.execute(
						"merge into DispatchDetail a using MakeCraftpiecework b on (a.did_diid=? and b.mcp_macode=a.did_makecode and b.mcp_stepcode=a.did_stepcode)  when matched then "
								+ "update set did_processtype=mcp_processtype,did_price=mcp_processprice,did_quota=mcp_standworkhour ",
						di_id);
			}
			try {// 安嵘取工序表中的加工单价更新到加工单价中，step 中有些字段是安嵘自己添加的
				baseDao.execute(
						"merge into DispatchDetail a using Step b on (a.did_diid=? and b.st_code=a.did_stepcode) when matched then update set did_price=st_cc_user,did_quota=st_aa_user",
						di_id);
			} catch (Exception e) {

			}
			// 更新计件工资
			countAmount(di_id);
			// 根据条件更新报废扣款
			double pricePerTime = 0;
			String time = baseDao.getDBSetting("sys", "pricePerTime");
			if (time != null) {
				try {
					Double.parseDouble(time);
				} catch (NumberFormatException e) {
					BaseUtil.showError("单位工时工价不是数字,请重新填写!");
				}
				pricePerTime = Double.valueOf(time);
			}
			SqlRowList rs = baseDao
					.queryForRowSet(
							"select did_stepcode,did_makecode,did_ngqty,did_id from dispatchdetail where did_diid=? and nvl(did_ngqty,0)>0 ",
							di_id);
			while (rs.next()) {
				SqlRowList rs2 = baseDao
						.queryForRowSet(
								"select nvl(sum(nvl(A.mcp_standworkhour,0)),0) workhour from MakeCraftPieceWork A left join MakeCraftPieceWork B on B.mcp_macode=A.mcp_macode "
										+ "where A.mcp_macode=? and A.mcp_stepcode=? and A.mcp_stepno>=B.mcp_stepno",
								rs.getString("did_makecode"), rs.getString("did_stepcode"));
				int did_id = rs.getInt("did_id");
				if (rs2.next()) {
					double ngprice = rs2.getDouble("workhour") * pricePerTime * rs.getDouble("did_ngqty");
					baseDao.execute("update dispatchdetail set did_ngprice=" + ngprice + " where did_id=?", did_id);
				}
			}
			// 根据类型计算计时工资
			baseDao.execute("update DispatchDetail set did_hourlyrate=" + pricePerTime
					+ "*nvl(did_maketime,0)*nvl(did_processcoefficient,1) where did_diid=? and did_processtype='计时工资'", di_id);

			baseDao.execute("update Dispatch set di_putinhours=(select sum(nvl(ad_zgs,0)) from AttendanceDetail left join Attendance on ad_atid=at_id where at_department=di_lowdepartname and at_lowdepartname=di_groupcode and to_char(at_attenddate,'yyyymmdd')=to_char(di_date,'yyyymmdd')) where di_id="
					+ di_id);
			baseDao.execute("update Dispatch set di_lostedhours=(select sum(nvl(lw_lostedhours,0)) from LOSSWORKTIME where di_lowdepartname=lw_lowdepartname and di_groupcode=lw_groupcode and to_char(lw_date,'yyyymmdd')=to_char(di_date,'yyyymmdd')) where di_id="
					+ di_id);
			if (baseDao.isDBSetting(caller, "isCountAll")) {
				baseDao.callProcedure("SP_DISPATCHCOUNT", new Object[] { di_id });
			}
		}
	}

	@Override
	public void deleteDispatch(int di_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("Dispatch", "di_statuscode", "di_id=" + di_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { di_id }); // 删除Dispatch
		baseDao.deleteById("Dispatch", "di_id", di_id);
		// 删除DispatchDetail
		baseDao.deleteById("Dispatchdetail", "did_diid", di_id);
		// 记录操作
		baseDao.logger.delete(caller, "di_id", di_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { di_id });
	}

	@Override
	public void updateDispatchById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("Dispatch", "di_statuscode", "di_id=" + store.get("di_id"));
		StateAssert.updateOnlyEntering(status);
		// 相同日期、相同车间、相同线别的只能存在一张生产日报
		if (!baseDao.isDBSetting(caller, "oneLineOneDepartOneDate")) {
			if (baseDao.getCount("select count(*) from Dispatch where to_char(di_date,'yyyy-mm-dd')='" + store.get("di_date")
					+ "' and di_lowdepartname='" + store.get("di_lowdepartname") + "' and di_groupcode='" + store.get("di_groupcode")
					+ "' and di_id<>" + store.get("di_id")) > 0) {
				BaseUtil.showError("相同日期、相同车间、相同线别的只能存在一张工时损时！");
			}
		}
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 修改Dispatch
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "Dispatch", "di_id"));
		// 修改DispatchDetail
		double tQty = 0;
		double qty = 0;
		double yqty = 0;
		StringBuffer sb = new StringBuffer();
		boolean check = baseDao.isDBSetting(caller, "uncheckMakeqty");// 不检查是否超制造单数量
		if (!check) {// 检查是否超制造单数量
			for (Map<Object, Object> s : gstore) {
				if (!(s.get("did_id") == null || s.get("did_id").equals("") || s.get("did_id").equals("0"))) {// 新添加的数据，id不存在

					if (s.get("did_makecode") != null && !"".equals(s.get("did_makecode"))) {
						tQty = Double.parseDouble(String.valueOf(s.get("did_overqty")));
						yqty = baseDao.getFieldValue("DispatchDetail", "nvl(sum(did_overqty),0)", "did_makecode='" + s.get("did_makecode")
								+ "' and did_stepcode='" + s.get("did_stepcode") + "' AND did_id <>" + s.get("did_id"), Double.class);
						qty = baseDao.getFieldValue("Make", "nvl(ma_qty,0)", "ma_code='" + s.get("did_makecode") + "'", Double.class);
						if (qty < yqty + tQty) {
							sb.append("制造单号：").append(s.get("did_makecode")).append("超出数量：").append((yqty + tQty - qty)).append("<br>");
						}
					}
				}
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError("明细行完成累计数超出制造单数量！" + sb.toString());
		}
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "DispatchDetail", "did_id"));
		getDatas(store.get("di_id"), caller);
		// 记录操作
		baseDao.logger.update(caller, "di_id", store.get("di_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void printDispatch(int di_id, String caller) {
		// 只能打印审核后的单据!
		Object status = baseDao.getFieldDataByCondition("Dispatch", "di_statuscode", "di_id=" + di_id);
		if (!status.equals("AUDITED") && !status.equals("PARTRECEIVED") && !status.equals("RECEIVED") && !status.equals("NULLIFIED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.print_onlyAudit"));
		}
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, new Object[] { di_id }); // 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "di_id", di_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, new Object[] { di_id });
	}

	@Override
	public void auditDispatch(int di_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Dispatch", "di_statuscode", "di_id=" + di_id);
		StateAssert.auditOnlyCommited(status);
		checkAll(di_id, caller);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { di_id }); // 执行审核操作
		baseDao.audit("Dispatch", "di_id=" + di_id, "di_status", "di_statuscode", "di_auditdate", "di_auditman");
		// 将数据写回制造单工艺表中累加流转数和报废数
		baseDao.execute(
				"merge into MakeCraftpiecework a using DispatchDetail b on (b.did_diid=? and b.did_makecode=a.mcp_macode and b.did_stepcode=a.mcp_stepcode) when matched then update set mcp_turnqty=nvl(mcp_turnqty,0)+nvl(did_overqty,0),mcp_scrapqty=nvl(mcp_scrapqty,0)+nvl(did_ngqty,0)",
				di_id);
		// 记录操作
		baseDao.logger.audit(caller, "di_id", di_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { di_id });
	}

	@Override
	public void resAuditDispatch(int di_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Dispatch", "di_statuscode", "di_id=" + di_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, new Object[] { di_id });
		// 执行反审核操作
		baseDao.resAudit("Dispatch", "di_id=" + di_id, "di_status", "di_statuscode", "di_auditdate", "di_auditman");
		// 生产日报反审核后将流转数量、报废数量 更新(扣除)至制造单工序表中
		// 用于安嵘 计件工资系统
		baseDao.execute(
				"merge into MakeCraftpiecework a  using DispatchDetail b on (b.did_diid=? and b.did_makecode=a.mcp_macode and b.did_stepcode=a.mcp_stepcode) when matched then update set mcp_turnqty=nvl(mcp_turnqty,0)-nvl(did_overqty,0),mcp_scrapqty=nvl(mcp_scrapqty,0)-nvl(did_ngqty,0)",
				di_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "di_id", di_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, new Object[] { di_id }); // 执行审核操作
	}

	@Override
	public void submitDispatch(int di_id, String caller) {
		if (!"Dispatch".equals(caller)) {
			baseDao.execute("update Dispatch set di_putinhours=(select sum(nvl(ad_zgs,0)) from AttendanceDetail left join Attendance on ad_atid=at_id where at_department=di_lowdepartname and at_lowdepartname=di_groupcode and to_char(at_attenddate,'yyyymmdd')=to_char(di_date,'yyyymmdd')) where di_id="
					+ di_id);
			baseDao.execute("update Dispatch set di_lostedhours=(select sum(nvl(lw_lostedhours,0)) from LOSSWORKTIME where di_lowdepartname=lw_lowdepartname and di_groupcode=lw_groupcode and to_char(lw_date,'yyyymmdd')=to_char(di_date,'yyyymmdd')) where di_id="
					+ di_id);
		}
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Dispatch", "di_statuscode", "di_id=" + di_id);
		StateAssert.submitOnlyEntering(status);
		checkAll(di_id, caller);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { di_id }); // 执行提交操作
		baseDao.submit("Dispatch", "di_id=" + di_id, "di_status", "di_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "di_id", di_id);
		// 执行提交后的其它逻辑
		if (baseDao.isDBSetting(caller, "isCountAll")) {
			count(di_id);
		}
		handlerService.afterSubmit(caller, new Object[] { di_id });
	}

	@Override
	public void resSubmitDispatch(int di_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Dispatch", "di_statuscode", "di_id=" + di_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { di_id }); // 执行反提交操作
		baseDao.resOperate("Dispatch", "di_id=" + di_id, "di_status", "di_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "di_id", di_id);
		handlerService.afterResSubmit(caller, new Object[] { di_id });
	}

	@Override
	public int copyDispatch(int id, String caller) {
		int diid = 0;
		try {
			SqlRowList rs = baseDao.queryForRowSet("select * from Dispatch where di_id=?", new Object[] { id });
			diid = baseDao.getSeqId("DISPATCH_SEQ");
			String code = baseDao.sGetMaxNumber("Dispatch", 2);
			if (rs.next()) {
				Map<String, Object> diffence = new HashMap<String, Object>();
				diffence.put("di_id", diid);
				diffence.put("di_code", "'" + code + "'");
				diffence.put("di_indate", "sysdate");
				diffence.put("di_recorder", "'" + SystemSession.getUser().getEm_name() + "'");
				diffence.put("di_statuscode", "'ENTERING'");
				diffence.put("di_status", "'" + BaseUtil.getLocalMessage("ENTERING") + "'");
				// 转入主表
				baseDao.copyRecord("Dispatch", "Dispatch", "di_id=" + id, diffence);
				// 转入从表
				rs = baseDao.queryForRowSet("SELECT did_id FROM DispatchDetail WHERE did_diid=? order by did_detno", id);
				diffence = new HashMap<String, Object>();
				diffence.put("did_diid", diid);
				diffence.put("did_code", "'" + code + "'");
				while (rs.next()) {
					diffence.put("did_id", baseDao.getSeqId("DISPATCHDETAIL_SEQ"));
					baseDao.copyRecord("DispatchDetail", "DispatchDetail", "did_id=" + rs.getInt("did_id"), diffence);
				}
			}
			return diid;
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
			return 0;
		}
	}

	/**
	 * 保存，更新的时候根据主表id 计算各个明细行的计件工资
	 * 
	 * @param id
	 */
	private void countAmount(Object id) {
		DecimalFormat df = new DecimalFormat("#.000000");
		List<Object[]> list = baseDao.getFieldsDatasByCondition("DispatchDetail", new String[] { "did_id", "nvl(did_price,0)",
				"nvl(did_overqty,0)", "nvl(did_perhourcost,0)", "nvl(DID_MAKETIME,0)", "nvl(did_processcoefficient,0)" }, "did_diid = '"
				+ id + "'");
		for (Object[] os : list) {
			double amount = Double.parseDouble(os[1].toString()) * Double.parseDouble(os[2].toString());
			double hourlyrate = Double.parseDouble(os[3].toString()) * Double.parseDouble(os[4].toString())
					* Double.parseDouble(os[5].toString());
			baseDao.updateByCondition("DispatchDetail", "did_amount='" + df.format(amount) + "',did_hourlyrate='" + df.format(hourlyrate)
					+ "'", "did_id='" + os[0].toString() + "'");
		}
	}

	/**
	 * 保存，更新,提交的时候根据主表id 计算相应字段的值
	 * 
	 * @param di_id
	 * 
	 */
	@Transactional
	private void count(int di_id) {
		List<String> sqls = new ArrayList<String>();
		String workshop = baseDao.getFieldDataByCondition("Dispatch", "di_lowdepartname", "di_id=" + di_id).toString();
		// 明细表
		// 生产点数did_manupoints=（投入数+完工数）*标准工时/点数
		if (!"SMT".equals(workshop)) {
			String sql1 = "update DispatchDetail set did_manupoints=0 where did_diid=" + di_id;
			sqls.add(sql1);
		} else {
			String sql1 = "update DispatchDetail set did_manupoints=(nvl(did_putinqty,0)+nvl(did_overqty,0))*nvl(did_quota,0) where did_diid="
					+ di_id;
			sqls.add(sql1);
		}

		// 目标点数=生产时间 H*每H点数
		String sql2 = "update DispatchDetail set did_destpoints=nvl(did_pointsperhour,0)*nvl(did_maketime,0) where did_diid=" + di_id;
		sqls.add(sql2);
		List<Object[]> detailData = baseDao.getFieldsDatasByCondition("DispatchDetail", new String[] { "did_id" }, "did_diid=" + di_id);
		// 投入累计,完成累计.大于工单数强制取工单数
		for (Object[] o : detailData) {
			String sql = "update DispatchDetail set (did_putinqtysum,did_overqtysum)="
					+ "(select case when sum(did_putinqty)>did_makeqty then did_makeqty else sum(did_putinqty) end did_putinqtysum, "
					+ "case when sum(did_overqty)>did_makeqty then did_makeqty else sum(did_overqty) end did_overqtysum "
					+ "from DispatchDetail where  (did_makecode,did_prodcode,nvl(did_stepcode,' '),did_makeqty) in "
					+ "(select a.did_makecode,a.did_prodcode,nvl(a.did_stepcode,' '),a.did_makeqty from DispatchDetail a where a.did_id="
					+ o[0] + " ) " + "group by  did_makecode,did_prodcode,nvl(did_stepcode,' '),did_makeqty) where did_id=" + o[0];
			sqls.add(sql);
		}
		// 入库累计,取工单完工数(是关联的,不用管)

		// 完工工时,取完成数*标准工时
		if (!"SMT".equals(workshop)) {
			String sql3 = "update DispatchDetail set did_overhours=nvl(did_overqty,0)*nvl(did_quota,0) where did_diid=" + di_id;
			sqls.add(sql3);
		}
		// 主表
		// 生产工时,投入工时-损失工时
		String sql4 = "update Dispatch set di_makehours=nvl(di_putinhours,0)-nvl(di_lostedhours,0) where di_id=" + di_id;
		sqls.add(sql4);
		// 完成工时,明细行完成工时之和;
		// 总投入数,明细行投入数之和
		// 总完成数,明细行完成数
		// 总生产点数,明细行生产点数之和
		// 总目标数,明细行目标点数之和
		// 总计划数 明细行计划数之和
		String sql5 = "update Dispatch set (di_overhours,di_putinqtys,di_overqtys,di_totalmanupoints,di_totaldestpoints,di_totalplanqtys)="
				+ "(select sum(did_overhours),sum(did_putinqty),sum(did_overqty),sum(did_manupoints),sum(did_destpoints),sum(did_planqty) from DispatchDetail where did_diid=di_id) where di_id="
				+ di_id;
		sqls.add(sql5);
		// 稼动率,总生产点数/总目标数
		// 计划达成率,总完成数/总计划数*100%
		String sql6 = "update Dispatch set di_jiadongrate=case when nvl(di_totaldestpoints,0)=0 then 0 else round(nvl(di_totalmanupoints,0)/nvl(di_totaldestpoints,0)*100, 2) end,"
				+ "di_planreachrate=case when nvl(di_totalplanqtys,0)=0 then 0 else round(nvl(di_overqtys,0)/nvl(di_totalplanqtys,0)*100,2) end where di_id="
				+ di_id;
		sqls.add(sql6);
		// 能率 1、主表车间不是SMT时，完成工时/生产工时*100%
		// 2、主表车间是SMT时，强制为0"
		// 生产性 1、主表车间不是SMT时，完成工时/投入工时*100%
		// 2、主表车间是SMT时，强制为0"
		if (!"SMT".equals(workshop)) {
			String sql7 = "update Dispatch set di_powermake=case when nvl(di_makehours,0)=0 then 0 else round(nvl(di_overhours,0)/nvl(di_makehours,0)*100,2) end,"
					+ "di_makeperform=case when nvl(di_putinhours,0)=0 then 0 else round(nvl(di_overhours,0)/nvl(di_putinhours,0)*100,2) end where di_id="
					+ di_id;
			sqls.add(sql7);
		} else {
			String sql7 = "update Dispatch set di_powermake=0,di_makeperform=0 where di_id=" + di_id;
			;
			sqls.add(sql7);
		}
		baseDao.execute(sqls);
	}

	private void checkAll(long di_id, String caller) {
		String errs = null;
		SqlRowList rs = null;
		// 同工单、同工序【流转数量】不能大于工单数量，报废数也不能小于0，流转数可以小于0，流转数小于0代表的是不良退
		boolean bo = baseDao.isDBSetting(caller, "checkTurnQty");
		if (bo) {
			errs = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(did_detno) detno from DispatchDetail where did_diid=? and  NVL(did_ngqty,0)<0 and rownum<20",
					String.class, di_id);
			if (errs != null) {
				BaseUtil.showError("报废数量不允许小于0，序号：[" + errs + "]");
			}
			double yqty = 0;
			rs = baseDao
					.queryForRowSet(
							"select did_makecode,did_stepcode,ma_qty from DispatchDetail left join make on did_makecode=ma_code where did_diid=? group by did_makecode,did_stepcode,ma_qty",
							di_id);
			StringBuffer sb = new StringBuffer();
			if (!baseDao.isDBSetting(caller, "uncheckMakeqty")) {
				while (rs.next()) {
					if (rs.getObject("did_makecode") != null && !"".equals(rs.getObject("did_makecode"))) {
						yqty = baseDao.getFieldValue("DispatchDetail", "nvl(sum(did_overqty),0)",
								"did_makecode='" + rs.getString("did_makecode") + "' and did_stepcode='" + rs.getString("did_stepcode")
										+ "'", Double.class);
						if (yqty > rs.getDouble("ma_qty")) {
							sb.append("制造单号：").append(rs.getObject("did_makecode")).append("超出数量：").append((yqty - rs.getDouble("ma_qty")))
									.append("<br>");
						}
					}
				}
			}
			if (sb.length() > 0) {
				BaseUtil.showError("流转数量不能大于工单数！" + sb.toString());
			}
		}
		// 根据明细行来判断制造单加工人和提交人
		if (baseDao.isDBSetting(caller, "checkEmname")) {
			rs = baseDao.queryForRowSet("select did_stepcode,did_makecode,did_emname from dispatchdetail where did_diid=" + di_id);
			String ErrorStr = "";
			while (rs.next()) {
				String did_stepcode = rs.getString("did_stepcode");
				String did_makecode = rs.getString("did_makecode");
				String did_emname = rs.getString("did_emname");
				SqlRowList rs1 = baseDao.queryForRowSet("select mcp_processman from makecraftpiecework where mcp_stepcode='" + did_stepcode
						+ "' and mcp_macode='" + did_makecode + "'");
				if (rs1.next()) {
					if (!rs1.getString("mcp_processman").contains(did_emname)) {
						ErrorStr += did_stepcode + ",";
					}
				}
			}
			if (ErrorStr != "") {
				ErrorStr = ErrorStr.substring(0, ErrorStr.length() - 1);
				BaseUtil.showError("明细行[" + ErrorStr + "]加工人与制造工序维护中加工人不符");
			}
		}
		// 加工人必须存在于同工单+同工序的加工人中
		bo = baseDao.isDBSetting(caller, "checkProcessMan");
		if (bo) {
			rs = baseDao
					.queryForRowSet(
							"select mcp_processmancode,did_emcode,did_detno,did_emname from dispatchdetail left join MakeCraftPieceWork on did_makecode=mcp_macode where did_diid=? and did_stepcode=mcp_stepcode",
							di_id);
			while (rs.next()) {
				if (rs.getObject("mcp_processmancode") != null) {
					String[] strman = rs.getString("mcp_processmancode").trim().split("#");
					if (!StringUtil.isInArray(strman, rs.getString("did_emcode"))) {
						BaseUtil.showError("序号：" + rs.getString("did_detno") + ",加工人不存在工单工序表中");
					}
				} else if (rs.getObject("mcp_processman") != null) {
					String[] strman = rs.getString("mcp_processman").trim().split("#");
					if (!StringUtil.isInArray(strman, rs.getString("did_emname"))) {
						BaseUtil.showError("序号：" + rs.getString("did_detno") + ",加工人不存在工单工序表中");
					}
				} else {
					BaseUtil.showError("请维护序号：" + rs.getString("did_detno") + ",制造单工序中的加工人");
				}
			}
		}
		bo = baseDao.isDBSetting(caller, "checkTurnQtyByStepNo");
		if (bo) {// 根据执行顺序限制下一工序流转数量不能大于上一工序流转数量
					// 判断是否存在上一工序
			rs = baseDao
					.queryForRowSet(
							"select B.mcp_stepcode st_code,B.mcp_macode ma_code ,T.did_detno detno ,T.mcp_stepcode ost_code from (select mcp_stepcode,mcp_macode ,mcp_stepno,did_detno from dispatchdetail left join MakeCraftPieceWork on did_makecode=mcp_macode "
									+ " where did_diid=? and mcp_stepcode=did_stepcode ) T , MakeCraftPieceWork B WHERE B.mcp_macode=T.mcp_macode "
									+ " AND B.mcp_stepno = T.mcp_stepno-1 and T.mcp_stepno<>0 ", di_id);
			while (rs.next()) {
				double oqty = baseDao.getFieldValue("DispatchDetail", "nvl(sum(did_overqty),0)", "did_makecode='" + rs.getString("ma_code")
						+ "' and did_stepcode='" + rs.getString("st_code") + "'", Double.class);
				double yqty = baseDao.getFieldValue("DispatchDetail", "nvl(sum(did_overqty),0)", "did_makecode='" + rs.getString("ma_code")
						+ "' and did_stepcode='" + rs.getString("ost_code") + "'", Double.class);
				if (yqty > oqty) {
					BaseUtil.showError("序号：" + rs.getString("detno") + "制造单当前工序的流转总数大于上一工序：" + rs.getString("st_code") + "的流转总数(" + oqty
							+ ")");
				}
			}
		}
	}

	@Override
	public void selectDispatchByMakeCode(String makecode, Integer id) {
		// 判断是否有明细
		int cn = baseDao.getCount("select count(1) from dispatchDetail where did_diid=" + id);
		if (cn > 0) {
			BaseUtil.showError("已经存在明细数据不允许载入工序！");
		}
		SqlRowList rs1 = baseDao.queryForRowSet("select ma_code,ma_prodcode from Make where ma_code=?", makecode);
		SqlRowList rs2 = baseDao.queryForRowSet(
				"select mcp_stepcode,mcp_stepname,mcp_processtype,mcp_processprice from MakeCraftPieceWork where mcp_macode=?", makecode);
		List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
		if (rs1.next()) {
			String ma_code = rs1.getString("ma_code");
			String ma_prodcode = rs1.getString("ma_prodcode");
			int detno = 1;
			while (rs2.next()) {
				Map<Object, Object> map = new HashMap<Object, Object>();
				String mcp_stepcode = rs2.getString("mcp_stepcode");
				String mcp_stepname = rs2.getString("mcp_stepname");
				String mcp_processtype = rs2.getString("mcp_processtype");
				Float mcp_processprice = rs2.getFloat("mcp_processprice");
				map.put("did_id", "");
				map.put("did_detno", detno);
				map.put("did_makecode", ma_code);
				map.put("did_prodcode", ma_prodcode);
				map.put("did_stepcode", mcp_stepcode);
				map.put("did_stepname", mcp_stepname);
				map.put("did_processtype", mcp_processtype);
				map.put("did_price", mcp_processprice);
				map.put("did_diid", id);
				map.put("did_processcoefficient", 1);
				list.add(map);
				detno++;
			}
		} else {
			BaseUtil.showError("制造单号不存在！");
		}
		// 写入到生产日报表中
		List<String> gridSql = SqlUtil.getInsertSqlbyList(list, "DispatchDetail", "did_id");
		baseDao.execute(gridSql);

	}

}
