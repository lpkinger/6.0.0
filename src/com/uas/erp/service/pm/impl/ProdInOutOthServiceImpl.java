package com.uas.erp.service.pm.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.uas.erp.dao.common.ProdInOutDao;
import com.uas.erp.service.pm.ProdInOutOthService;

@Service("prodInOutOthService")
public class ProdInOutOthServiceImpl implements ProdInOutOthService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private ProdInOutDao prodInOutDao;

	@Override
	public void deleteProdInOut(int pd_id, String caller) {
		// 只能删除在录入的单据!
		Object[] status = baseDao.getFieldsDataByCondition("ProdInOut left join ProdIOdetail on pi_id=pd_piid", new String[] {
				"pi_invostatuscode", "pi_statuscode" }, "pd_id=" + pd_id);
		Object piid=baseDao.getFieldDataByCondition("prodiodetail", "pd_piid", "pd_id="+pd_id);
		if ("POSTED".equals(status[1])) {
			BaseUtil.showError("只能删除未过账的单据！");
		}
		if (!"ENTERING".equals(status[0])) {
			BaseUtil.showError("只能删除在录入的单据！");
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { pd_id });
		// 删除ProdInOut
		int count = baseDao.getCount("select count(1) from prodiodetail where pd_piid=(select pd_piid from ProdIODetail where pd_id="
				+ pd_id + ")");
		if (count == 1) {
			baseDao.execute("delete from ProdInOut where pi_id = (select pd_piid from ProdIODetail where pd_id=" + pd_id + ")");
		}
		// 删除ProdIODetail
		baseDao.deleteById("ProdIODetail", "pd_id", pd_id);
		// 删除makeclash
		baseDao.deleteById("makeclash", "mc_pdid", pd_id);
		// 记录操作
		baseDao.logger.delete(caller, "pd_id", pd_id);
		baseDao.logger.delete(caller, "pi_id", piid);
		
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { pd_id });
	}

	@Override
	public void updateProdInOutById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		Object pd_id = store.get("pd_id");
		// 只能修改[在录入]的资料!
		Object[] status = baseDao.getFieldsDataByCondition("ProdInOut left join ProdIODetail on pd_piid=pi_id", new String[] {
				"pi_invostatuscode", "pd_piid", "pi_class","pd_ordercode","pd_prodcode" }, "pd_id=" + pd_id);
		Object pi_id = status[1];
		StateAssert.updateOnlyEntering(status[0]);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		baseDao.getEndDate(caller, pi_id);
		boolean isProdIn = baseDao.isProdIn(caller);
		if ((isProdIn || "ProdInOut!ProcessFinish".equals(caller))&& !StringUtil.hasText(store.get("pd_batchcode"))) {
			if("ProdInOut!ProcessFinish".equals(caller)){
				store.put("pd_batchcode", baseDao.getBatchcode("ProdInOut!Make!In"));
			}else{
				store.put("pd_batchcode", baseDao.getBatchcode(caller));
			}
		}
		Object piid=baseDao.getFieldDataByCondition("prodiodetail", "pd_piid", "pd_id="+pd_id);
		// 修改ProdInOut
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "ProdInOut", "pi_id", "pi_"));
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "ProdIODetail", "pd_id", "pd_"));
		// 修改makeclash
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "makeclash", "mc_id"));
		baseDao.execute("update makeclash set (mc_code,mc_class)=(select pd_inoutno,pd_piclass from ProdIODetail where pd_id=mc_pdid) where mc_pdid="
				+ pd_id);
		getTotal(pd_id);
		if("ProdInOut!ProcessFinish".equals(caller)){
			// 重新更新已转完工入库数
			baseDao.execute("update make set ma_tomadeqty=(select sum(pd_inqty)-sum(case when pd_status=99 then pd_outqty else 0 end) "
								+ "from prodiodetail where pd_ordercode=ma_code and pd_piclass in('完工入库单','委外验收单','委外验退单') and pd_prodcode=ma_prodcode) "
								+ "where ma_code='"	+ status[3] +"' and ma_prodcode='"+status[4]+"'");
			baseDao.execute("update make set ma_tomadeqty=ma_madeqty where ma_code='" + status[3] +"' and ma_madeqty>ma_tomadeqty and"
					+ " ma_prodcode='"+status[4]+"'");						
		}
		// 记录操作
		baseDao.logger.update(caller, "pd_id", pd_id);
		baseDao.logger.update(caller, "pi_id", piid);
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void printProdInOut(int pd_id, String caller) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select pi_invostatuscode,pi_statuscode,pd_jobcode,pd_inqty from ProdInOut left join ProdIODetail on pd_piid=pi_id where pd_id=?",
						pd_id);
		if (rs.next()) {
			if ("UNPOST".equals(rs.getGeneralString("pi_statuscode"))) {
				double uncommitqty = baseDao.getSummaryByField(
						"stepIO left join makecraft on mc_makecode=si_makecode and mc_detno=st_outno ", "si_qty",
						" mc_code='" + rs.getObject("pd_jobcode") + "' and st_class='工序报废' and si_status='已提交'");
				double onmake = baseDao.getSummaryByField("makecraft ", "mc_onmake", " mc_code='" + rs.getObject("pd_jobcode") + "'");
				if (onmake - uncommitqty < rs.getGeneralDouble("pd_inqty")) {
					BaseUtil.showError("在制数[" + onmake + "]待报废数[" + uncommitqty + "]完工数量不能大于在站可移交数量[" + (onmake - uncommitqty) + "]");
				}
			}
		}
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, new Object[] { pd_id });
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "pd_id", pd_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, new Object[] { pd_id });
	}

	@Override
	public void auditProdInOut(int pd_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("ProdInOut left join ProdIODetail on pi_id=pd_piid", new String[] {
				"pi_statuscode", "pi_invostatuscode", "pi_id" }, "pd_id=" + pd_id);
		StateAssert.auditOnlyCommited(status[1]);
		if ("POSTED".equals(status[0])) {
			BaseUtil.showError("只能对未过账的单据进行审核操作！");
		}
		check(pd_id);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { pd_id });
		Object piid=baseDao.getFieldDataByCondition("prodiodetail", "pd_piid", "pd_id="+pd_id);		
		// 执行审核操作
		baseDao.audit("ProdInOut", "pi_id=" + status[2], "pi_invostatus", "pi_invostatuscode", "pi_auditdate", "pi_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "pd_id", pd_id);
		baseDao.logger.audit(caller, "pi_id", piid);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { pd_id });
	}

	@Override
	public void resAuditProdInOut(int pd_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("ProdInOut left join ProdIODetail on pi_id=pd_piid", new String[] {
				"pi_statuscode", "pi_invostatuscode", "pi_id" }, "pd_id=" + pd_id);
		StateAssert.resAuditOnlyAudit(status[1]);
		if ("POSTED".equals(status[0])) {
			BaseUtil.showError("只能对未过账的单据进行反审核操作！");
		}
		Object piid=baseDao.getFieldDataByCondition("prodiodetail", "pd_piid", "pd_id="+pd_id);		
		// 执行反审核操作
		baseDao.resAudit("ProdInOut", "pi_id=" + status[2], "pi_invostatus", "pi_invostatuscode", "pi_auditdate", "pi_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "pd_id", pd_id);
		baseDao.logger.resAudit(caller, "pi_id", piid);
	}

	private void getTotal(Object pd_id) {
		SqlRowList rs = baseDao.queryForRowSet("select * from ProdInOut left join ProdIODetail on pd_piid=pi_id where pd_id=?", pd_id);
		if (rs.next()) {
			Object pi_id = rs.getObject("pi_id");
			if ("完工入库单".equals(rs.getGeneralString("pi_class"))) {
				baseDao.execute("update ProdIOdetail set pd_jobcode=(select mc_code from makecraft where mc_makecode=pd_ordercode and pd_prodcode=mc_prodcode) where nvl(pd_jobcode,' ')=' ' and pd_id="
						+ pd_id);
			}
			baseDao.execute("update ProdIOdetail set pd_total=round(pd_price*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2) where pd_id=" + pd_id);
			baseDao.execute("update prodinout set pi_total=nvl((select sum(pd_total) from ProdIOdetail where pd_piid=pi_id),2) where pi_id ="
					+ pi_id);
			// 在制数
			baseDao.execute("update ProdIODetail set pd_totaloutqty=nvl((select mc_onmake from makecraft where mc_makecode=pd_ordercode and mc_code=pd_jobcode),0) where pd_id="
					+ pd_id);
			// 显示可供冲减套料数量
			Object qty = 0;
			SqlRowList rs1 = baseDao
					.queryForRowSet(
							"select min( floor((nvl(mm_havegetqty,0)+nvl(mm_stepinqty,0)-nvl(mm_clashqty,0)) /mm_oneuseqty)) qty "
									+ "from makematerial left join makecraft on mc_makecode=mm_code and mc_code=mm_mdcode where mm_code=? and mm_mdcode=? "
									+ "and mm_qty>0 and mm_oneuseqty>0 and mm_supplytype='推式' and nvl(mm_supplytype,' ')<>'虚拟件'",
							rs.getObject("pd_ordercode"), rs.getObject("pd_jobcode"));
			if (rs1.next()) {
				qty = rs1.getGeneralDouble("qty");
			}
			baseDao.execute("update ProdIODetail set pd_beginqty=" + qty + " where pd_id=" + pd_id);
			// 本次冲减原料数
			if (rs.getGeneralDouble("pd_inqty") < Double.parseDouble(qty.toString())) {
				qty = rs.getGeneralDouble("pd_inqty");
			}			
			baseDao.execute("update ProdIODetail set pd_nowsumqty=nvl((select sum(mc_clashqty) from MakeClash where MC_PDID=pd_id),0) where pd_id="
					+ pd_id);
		}
	}

	private void check(int pd_id) {
		// 当物料资料中转换率为1或双单位相同时，采购单位数量=库存单位数量
		baseDao.execute("update ProdIOdetail set pd_purcqty=nvl(pd_inqty,0)+nvl(pd_outqty,0), pd_purcrate=1 where pd_id="
				+ pd_id
				+ " and exists (select 1 from product where pd_prodcode=pr_code and (nvl(pr_purcrate,0)=0 or nvl(pr_unit,' ')=nvl(pr_purcunit,' ')))");
		baseDao.execute("update ProdIOdetail set pd_purcqty=nvl(pd_inqty,0)+nvl(pd_outqty,0), pd_purcrate=1 where pd_id=" + pd_id
				+ " and pd_piclass='完工入库单' and nvl(pd_purcqty,0)=0");
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select pd_prodcode,pd_jobcode,pi_groupcode,nvl(pd_inqty,0)+nvl(pd_outqty,0) pd_qty,pd_piclass,pd_purcrate,pd_piid,pd_jobcode,pd_ordercode from ProdInOut left join ProdIODetail on pd_piid=pi_id where pd_id=?",
						pd_id);
		if (rs.next()) {
			double qty = rs.getGeneralDouble("pd_qty");
			if (!StringUtil.hasText(rs.getGeneralString("pd_prodcode"))) {
				BaseUtil.showError("产品编号为空！");
			}
			if (qty <= 0) {
				BaseUtil.showError("数量为0，不能进行当前操作!");
			}
			if ("完工入库单".equals(rs.getObject("pd_piclass"))) {
				Object macode = rs.getObject("pd_ordercode");
				Object prodcode = rs.getObject("pd_prodcode");
				Object jobcode = rs.getObject("pd_jobcode");
				int count = baseDao.getCount("select count(1) from make where ma_code='" + rs.getObject("pd_ordercode")
						+ "' and ma_prodcode='" + rs.getObject("pd_prodcode") + "'");
				if (count == 0 && !StringUtil.hasText(jobcode)) {
					BaseUtil.showError("半成品入库必须填写作业单号！");
				}
				SqlRowList mc = baseDao
						.queryForRowSet("select mc_onmake,mc_whoutqty,mc_qty,mc_id from make left join makecraft on mc_makecode=ma_code where ma_code='"
								+ macode + "' and ma_prodcode='" + prodcode + "' and mc_code='" + jobcode + "'");
				if (mc.next()) {
					if (qty > mc.getGeneralDouble("mc_onmake")) {
						BaseUtil.showError("物料[" + prodcode + "]入库数量大于在制可完工数！");
					}
				}
			}
			if (!"盘盈调整单".equals(rs.getObject("pd_piclass")) && !"盘亏调整单".equals(rs.getObject("pd_piclass"))) {
				if (rs.getGeneralDouble("pd_purcrate") == 0) {
					BaseUtil.showError("采购单位数量为0，不能进行当前操作!");
				}
			}
			// 入库数不能大于在站可移交数
			double uncommitqty = baseDao.getSummaryByField("stepIO left join makecraft on mc_makecode=si_makecode and mc_detno=st_outno ",
					"si_qty", " mc_code='" + rs.getObject("pd_jobcode") + "' and st_class='工序报废' and si_status='已提交'");
			double onmake = baseDao.getSummaryByField("makecraft ", "mc_onmake", " mc_code='" + rs.getObject("pd_jobcode") + "'");
			if (onmake - uncommitqty < rs.getGeneralDouble("pd_inqty")) {
				BaseUtil.showError("在制数[" + onmake + "]待报废数[" + uncommitqty + "]完工数量不能大于在站可移交数量[" + (onmake - uncommitqty) + "]");
			}
			if ("完工入库单".equals(rs.getObject("pd_piclass"))) {
				baseDao.execute("update ProdIOdetail set pd_jobcode=(select mc_code from makecraft where mc_makecode=pd_ordercode and pd_prodcode=mc_prodcode) where nvl(pd_jobcode,' ')=' ' and pd_id="
						+ pd_id);
			}
			baseDao.execute("update ProdIOdetail set pd_total=round(pd_price*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2) where pd_id=" + pd_id);
			baseDao.execute("update prodinout set pi_total=nvl((select sum(pd_total) from ProdIOdetail where pd_piid=pi_id),2) where pi_id ="
					+ rs.getObject("pd_piid"));
			SqlRowList makeclash = baseDao.queryForRowSet(
					"select * from makeclash left join stepio on mc_clashcode=si_code and mc_clashclass=st_class where mc_pdid=?", pd_id);
			while (makeclash.next()) {
				int mc_id = rs.getGeneralInt("mc_id");
				double tqty = rs.getGeneralDouble("mc_clashqty");
				double sqty = rs.getGeneralDouble("si_qty");
				if (StringUtil.hasText(rs.getObject("mc_clashcode")) && StringUtil.hasText(rs.getObject("mc_clashclass"))) {
					Object si_code = rs.getObject("mc_clashcode");
					Object st_class = rs.getObject("mc_clashclass");
					Object yqty = baseDao.getFieldDataByCondition("makeclash", "sum(mc_clashqty)", "mc_clashcode='" + si_code
							+ "' and mc_clashclass='" + st_class + "' AND mc_id <>" + mc_id);
					yqty = yqty == null ? 0 : yqty;
					if (Double.parseDouble(yqty.toString()) + tqty > sqty) {
						BaseUtil.showError("本次冲减数+已冲减数>可冲减数：本次冲减数[" + tqty + "]已冲减数[" + yqty + "]可冲减数[" + sqty + "]！");
					}
				}
			}
		}

	}

	@Override
	public void submitProdInOut(int pd_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("ProdInOut left join ProdIODetail on pi_id=pd_piid", new String[] {
				"pi_statuscode", "pi_invostatuscode", "pi_id" }, "pd_id=" + pd_id);
		StateAssert.submitOnlyEntering(status[1]);
		if ("POSTED".equals(status[0])) {
			BaseUtil.showError("只能对未过账的单据进行提交操作！");
		}
		check(pd_id);
		getTotal(pd_id);
		Object piid=baseDao.getFieldDataByCondition("prodiodetail", "pd_piid", "pd_id="+pd_id);				
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { pd_id });
		// 执行提交操作
		baseDao.submit("ProdInOut", "pi_id=" + status[2], "pi_invostatus", "pi_invostatuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pd_id", pd_id);
		baseDao.logger.submit(caller, "pi_id", piid);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { pd_id });
	}

	@Override
	public void resSubmitProdInOut(int pd_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("ProdInOut left join ProdIODetail on pi_id=pd_piid", new String[] {
				"pi_statuscode", "pi_invostatuscode", "pi_id" }, "pd_id=" + pd_id);
		StateAssert.resSubmitOnlyCommited(status[1]);
		if ("POSTED".equals(status[0])) {
			BaseUtil.showError("只能对未过账的单据进行反提交操作！");
		}
		Object piid=baseDao.getFieldDataByCondition("prodiodetail", "pd_piid", "pd_id="+pd_id);		
		handlerService.beforeResSubmit(caller, new Object[] { pd_id });
		// 执行反提交操作
		baseDao.resOperate("ProdInOut", "pi_id=" + status[2], "pi_invostatus", "pi_invostatuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pd_id", pd_id);
		baseDao.logger.resSubmit(caller, "pi_id", piid);
		handlerService.afterResSubmit(caller, new Object[] { pd_id });
	}

	@Override
	public void postProdInOut(int pd_id, String caller) {
		// 只能对状态为[未过账]的订单进行过账操作!
		Object[] status = baseDao.getFieldsDataByCondition("ProdInOut left join ProdIODetail on pi_id=pd_piid", new String[] {
				"pi_statuscode", "pd_piid", "pd_inoutno", "pd_piclass" }, "pd_id=" + pd_id);
		if (status[0].equals("POSTED")) {
			BaseUtil.showError("只能对未过账的单据进行过账操作！");
		}
		check(pd_id);
		getTotal(pd_id);
		handlerService.beforePost(caller, new Object[] { pd_id });
		Object pi_id = status[1];
		boolean isProdIn = baseDao.isProdIn(caller);
		SqlRowList rs = null; 
		if (baseDao.isDBSetting("cgyCheck")) {
			Object type = baseDao.getFieldDataByCondition("Employee", "em_code", "em_code='" + SystemSession.getUser().getEm_code() + "'");
			if (type != null) {
				String dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') from prodinout,prodiodetail where pi_id=pd_piid and pi_id=? and pd_id not in "
										+ "(select pd_id from prodinout,prodiodetail,warehouse,warehouseman where pi_id=pd_piid and pd_whcode=wh_code and wh_id=wm_whid "
										+ "and pi_id=? and wm_cgycode=?) and rownum<20", String.class, pi_id, pi_id,
								SystemSession.getUser().getEm_code());
				if (dets != null) {
					BaseUtil.showError("明细行仓库对应的仓管员与当前过账人不一致，不允许进行当前操作！" + dets);
				}
			}
		}
		if (isProdIn || "ProdInOut!ProcessFinish".equals(caller)) {
			// 入库类单据:如果pd_location为空，默认等于物料资料里的仓位pr_location
			baseDao.execute(
					"update prodiodetail set pd_location=(select pr_location from product where pr_code=pd_prodcode) where pd_piid=? and nvl(pd_location,' ')=' '",
					pi_id);

			rs = baseDao.queryForRowSet("SELECT pd_id FROM ProdioDetail where pd_piid=? and nvl(pd_batchcode,' ')=' '", pi_id);
			while (rs.next()) {
				if("ProdInOut!ProcessFinish".equals(caller)){ 
					baseDao.execute("update prodiodetail set pd_batchcode=? where pd_id=?", baseDao.getBatchcode("ProdInOut!Make!In"), rs.getInt("pd_id"));
				}else{
					baseDao.execute("update prodiodetail set pd_batchcode=? where pd_id=?", baseDao.getBatchcode(caller), rs.getInt("pd_id"));
				}
				
			}
			// 入库单有生成条码但条码对应的物料或数量或批次号与明细行不一致则不允许过账
			rs = baseDao
					.queryForRowSet(
							"select wm_concat(pd_pdno)no,count(1)cn from prodiodetail left join (select sum(bi_inqty)inqty,bi_pdno,max(bi_prodcode)bi_prodcode,max(bi_batchcode)bi_batchcode from barcodeio"
									+ " where bi_piid=? group by bi_pdno) on bi_pdno=pd_pdno where pd_piid=? and pd_inqty>0 and inqty>0 "
									+ " and (nvl(inqty,0)<>pd_inqty or bi_prodcode<>pd_prodcode or bi_batchcode<>pd_batchcode) and rownum<30",
							pi_id, pi_id);
			if (rs.next() && rs.getInt("cn") > 0) {
				BaseUtil.showError("条码与明细行中的物料或数量或批次号不一致，不允许过账，请先清除不一致条码!行号：" + rs.getString("no"));
			}
		}
		baseDao.getEndDate(caller, pi_id);
		String res = null;
		baseDao.procedure("SP_GetCostPrice", new Object[] { status[3].toString(), status[2].toString() });
		res = baseDao.callProcedure("Sp_SplitProdOut",
				new Object[] { status[3].toString(), status[2].toString(), String.valueOf(SystemSession.getUser().getEm_name()) });
		if (res != null && !res.trim().equals("")) {
			// 重新添加提示限制信息
			BaseUtil.showErrorOnSuccess(res + "  " + status[3].toString() + status[2].toString() + "，过账失败!");
		}
		if (baseDao.isDBSetting(caller, "ifBatchCodeNotChange") && baseDao.isDBSetting(caller, "autoPostIn")) {
			// 拨出单过帐后产生的拨入单批号不变，同一物料同仓库不能同时入两次相同的批号
			SqlRowList rs1 = baseDao
					.queryForRowSet("select  count(1)n, wm_concat(pd_pdno)detno from (select pd_batchcode,pd_inwhcode,pd_prodcode,min(pd_pdno)pd_pdno,count(1)c from  ProdIODetail where pd_piid="
							+ pi_id + " and pd_batchcode<>' ' group by pd_batchcode,pd_inwhcode,pd_prodcode ) where c> 1");
			if (rs1.next()) {
				if (rs1.getInt("n") > 0) {
					BaseUtil.showError("拨出单过帐后产生的拨入单批号不变，同一物料同拨入仓库批号不能相同！行号：" + rs1.getString("detno"));
				}
			}
		}
		// 存储过程
		res = baseDao.callProcedure("Sp_CommitProdInout",
				new Object[] { status[3].toString(), status[2].toString(), String.valueOf(SystemSession.getUser().getEm_name()) });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
		baseDao.updateByCondition("ProdInOut", "pi_statuscode='POSTED',pi_status='" + BaseUtil.getLocalMessage("POSTED")
				+ "',pi_inoutman='" + SystemSession.getUser().getEm_name() + "',pi_date1=sysdate,pi_sendstatus='待上传'", "pi_id=" + pi_id);
		if (!prodInOutDao.isIn(caller) && !"ProdInOut!ProcessFinish".equals(caller)) {// 出库类单据过账时，根据批号抓取batch.ba_vendorrate到prodiodetail.pd_vendorrate
			baseDao.execute("update prodiodetail set pd_vendorrate=(select ba_vendorrate from batch where ba_code=pd_batchcode"
					+ " and ba_whcode =pd_whcode and ba_prodcode=pd_prodcode ) where pd_piid=" + pi_id + " and nvl(pd_vendorrate,0)=0");
		}
		Object piid=baseDao.getFieldDataByCondition("prodiodetail", "pd_piid", "pd_id="+pd_id);				
		// 处理转换率、采购单位数量
		rs = baseDao
				.queryForRowSet(
						"select pi_whcode,pd_prodcode,pd_batchcode,pd_inqty,pd_outqty,pd_purcqty,pr_purcrate,pd_inoutno,pd_piclass,pd_pdno,pd_id from prodinout,prodiodetail,product where pi_inoutno=pd_inoutno and pi_class=pd_piclass and pd_prodcode=pr_code and pd_piid=? ",
						pi_id);
		double inQty2 = 0, purcQty2 = 0, purcRate2 = 0;
		while (rs.next()) {
			// 入库单
			if (rs.getGeneralDouble("pd_inqty") > 0) {
				inQty2 = rs.getGeneralDouble("pd_inqty");
				purcQty2 = rs.getGeneralDouble("pd_purcqty");
				if (purcQty2 > 0) {
					purcRate2 = NumberUtil.formatDouble(Math.abs(inQty2 / purcQty2), 8);
				}
				if (purcRate2 == 0) {
					purcRate2 = rs.getGeneralDouble("pr_purcrate");
				}
				if (purcRate2 == 0) {
					purcRate2 = 1;
				}
				baseDao.execute("update batch set ba_purcrate=" + purcRate2 + " where ba_prodcode='" + rs.getObject("pd_prodcode")
						+ "' and ba_whcode='" + rs.getObject("pd_whcode") + "' and ba_code='" + rs.getObject("pd_batchcode") + "'");
				baseDao.execute("update prodiodetail set pd_purcrate=" + purcRate2 + " where pd_id=" + rs.getObject("pd_id"));
				baseDao.execute("update prodiodetail set pd_purcqty=" + NumberUtil.formatDouble(inQty2 / purcRate2, 4) + " where pd_id="
						+ rs.getObject("pd_id"));
			}
			// 出库
			if (rs.getGeneralDouble("pd_outqty") > 0) {
				inQty2 = rs.getGeneralDouble("pd_outqty");
				purcQty2 = rs.getGeneralDouble("pd_purcqty");
				purcRate2 = 1;
				if (purcQty2 > 0) {
					purcRate2 = NumberUtil.formatDouble(inQty2 / purcQty2, 8);
				}
				if (purcRate2 == 0) {
					purcRate2 = rs.getGeneralDouble("pr_purcrate");
				}
				if (purcRate2 == 0) {
					purcRate2 = 1;
				}
				baseDao.execute("update prodiodetail set pd_purcrate=" + purcRate2 + " where pd_id=" + rs.getObject("pd_id"));
			}
		}
		baseDao.logger.post(caller, "pd_id", pd_id);
		baseDao.logger.post(caller, "pi_id", piid);
		handlerService.afterPost(caller, new Object[] { pd_id });
	}

	@Override
	public void resPostProdInOut(int pd_id, String caller) {
		Object[] status = baseDao.getFieldsDataByCondition("ProdInOut left join ProdIODetail on pi_id=pd_piid", new String[] {
				"pi_statuscode", "pi_id", "pi_class", "pi_inoutno" }, "pd_id=" + pd_id);
		SqlRowList rs = baseDao.queryForRowSet("SELECT * FROM ProdInOut left join ProdIODetail on pi_id=pd_piid where pd_id=?", pd_id);
		if (rs.next()) {
			StateAssert.resPostOnlyPosted(rs.getObject("pi_statuscode"));
			Object pi_id = rs.getObject("pi_id");
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat('行号：'||detno||'，'||piclass||'号：'||picode) from (select a.pd_pdno detno, b.pd_piclass piclass, b.pd_inoutno picode from ProdIODetail a left join prodiodetail b on a.pd_id=b.pd_ioid where a.pd_piid=? and nvl(a.pd_yqty,0)>0)",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("已转出单据,不允许反过账！" + dets);
			}
			// 若入库单明细行物料号+批号+仓库出现在已过账的成本调整单中，且成本调整单制作了凭证的，限制反过账
			if (prodInOutDao.isIn(caller) || "ProdInOut!ProcessFinish".equals(caller)) {
				SqlRowList rs_checkCostChange = baseDao
						.queryForRowSet(
								"select pd_pdno,WMSYS.WM_CONCAT(DISTINCT pi_inoutno) pi_inoutno from (select a.pd_pdno,a.pd_id,b.pi_inoutno "
										+ "from prodiodetail a left join (select pd_batchcode,pd_prodcode,pd_whcode,pi_vouchercode,pi_inoutno from prodiodetail "
										+ "left join prodinout on pd_piid=pi_id where  pi_class='成本调整单' and pi_statuscode='POSTED' and "
										+ "nvl(pi_vouchercode,' ')<>' ') b on a.pd_batchcode=b.pd_batchcode and a.pd_prodcode=b.pd_prodcode "
										+ "and a.pd_whcode=b.pd_whcode where a.pd_piid=?  and nvl(b.pi_vouchercode,' ')<>' ') "
										+ "group by pd_id,pd_pdno", pi_id);
				if (rs_checkCostChange.next()) {
					BaseUtil.showError("行" + rs_checkCostChange.getInt("pd_pdno") + "物料批次关联的成本调整单"
							+ rs_checkCostChange.getString("pi_inoutno") + "已制作凭证，请先取消凭证！");
				}
			}
			if (baseDao.isDBSetting("cgyCheck")) {
				Object type = baseDao.getFieldDataByCondition("Employee", "em_code", "em_code='" + SystemSession.getUser().getEm_code() + "'");
				if (type != null) {
					 dets = baseDao
							.getJdbcTemplate()
							.queryForObject(
									"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') from prodinout,prodiodetail where pi_id=pd_piid and pi_id=? and pd_id not in "
											+ "(select pd_id from prodinout,prodiodetail,warehouse,warehouseman where pi_id=pd_piid and pd_whcode=wh_code and wh_id=wm_whid "
											+ "and pi_id=? and wm_cgycode=?) and rownum<20", String.class, pi_id, pi_id,
									SystemSession.getUser().getEm_code());
					if (dets != null) {
						BaseUtil.showError("明细行仓库对应的仓管员与当前过账人不一致，不允许进行当前操作！" + dets);
					}
				}
			}
			Object piid=baseDao.getFieldDataByCondition("prodiodetail", "pd_piid", "pd_id="+pd_id);							
			handlerService.beforeResPost(caller, new Object[] { pd_id });
			String res = baseDao.callProcedure("Sp_UnCommitProdInout", new Object[] { status[2].toString(), status[3].toString() });
			if (res != null && !res.trim().equals("")) {
				BaseUtil.showError(res);
			}
			baseDao.updateByCondition("ProdInOut", "pi_statuscode='UNPOST',pi_status='" + BaseUtil.getLocalMessage("UNPOST")
					+ "',pi_inoutman=null,pi_date1=null", "pi_id=" + pi_id);
			// 记录操作
			baseDao.logger.resPost(caller, "pd_id", pd_id);
			baseDao.logger.resPost(caller, "pi_id", piid);
			handlerService.afterResPost(caller, new Object[] { pd_id });
		}
	}

	@Override
	public void saveProdIOClash(String caller, String data, int id, double clashqty) {
		Object[] ob = baseDao.getFieldsDataByCondition("prodiodetail", new String[] { "pd_status", "pd_inoutno", "pd_piclass","pd_prodcode" }, "pd_id="
				+ id);
		
		if (data != null && !"null".equals(data)) {
			baseDao.execute("delete makeclash where MC_PDID=" + id);
			List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
			for (Map<Object, Object> map : maps) {
				int count = baseDao.getCount("Select count(1) from stepIO where si_qty-nvl(st_clashqty,0)>="
						+ (map.get("st_inqty") == null ? 0 : map.get("st_inqty")) + " and " + "si_code='" + map.get("si_code")
						+ "' and si_status='已过账'");
				if (count > 0) {
					baseDao.execute("insert into makeclash(mc_id,mc_pdid,mc_code,mc_class,mc_clashclass,mc_clashcode,mc_clashqty,mc_prodcode) "
							+ "values(makeclash_seq.nextval,"
							+ id
							+ ",'"
							+ ob[1]
							+ "','"
							+ ob[2]
							+ "','"
							+ map.get("st_class")
							+ "','"
							+ map.get("si_code") + "','" + map.get("st_inqty") + "','" + map.get("si_prodcode") + "')");
				}
			}
		}else if("工序退料单".equals(ob[2].toString())){
			baseDao.execute("insert into makeclash(mc_id,mc_pdid,mc_code,mc_class,mc_clashclass,mc_clashcode,mc_clashqty,mc_prodcode) "
					+ "values(makeclash_seq.nextval," + id + ",'" + ob[1] + "','" + ob[2] + "','套料','" + ob[1] + "',0 ,'"+ob[3]+"')");
		}
		if (clashqty > 0) {
			baseDao.execute("insert into makeclash(mc_id,mc_pdid,mc_code,mc_class,mc_clashclass,mc_clashcode,mc_clashqty,mc_prodcode) "
					+ "values(makeclash_seq.nextval," + id + ",'" + ob[1] + "','" + ob[2] + "','套料','" + ob[1] + "'," + clashqty + ",'"+ob[3]+"')");
		}

	}

	@Override
	public Map<String, Object> getClashInfo(String caller, String con) {
		Map<String, Object> map = new HashMap<String, Object>();
		int saveclash = 0, setclash = 0;
		double mconmake = 0, mcremain = 0, clashqty = 0;
		Object[] ob = baseDao.getFieldsDataByCondition("prodinout left join prodiodetail on pi_id=pd_piid", new String[] {
				"pi_invostatuscode", "pd_inoutno", "pd_piclass", "pd_ordercode", "pd_orderdetno" }, con);
		Object onmake = baseDao.getFieldDataByCondition("makeCraft", "mc_onmake", "mc_makecode='" + (ob[3] == null ? "" : ob[3])
				+ "' and  mc_detno='" + (ob[4] == null ? 0 : ob[4]) + "'");
		Object reamin = baseDao.getFieldDataByCondition("makematerial left join makecraft on mc_makecode=mm_code and mc_code=mm_mdcode left join make on mm_maid=ma_id",
				"min(ceil((nvl(mm_havegetqty,0)+nvl(mm_stepinqty,0)-nvl(mm_clashqty,0)) /mm_oneuseqty)) ", "mm_code='"
						+ (ob[3] == null ? "" : ob[3]) + "' and mc_detno=" + (ob[4] == null ? 0 : ob[4])
						+ " and mm_qty>0 and mm_oneuseqty>0 and mm_supplytype='PUSH'");
		Object clash = baseDao.getFieldDataByCondition("makeclash", "mc_clashqty", "mc_class='" + ob[2] + "' and mc_code='" + ob[1]
				+ "' and  mc_clashclass='套料'");
		mconmake = onmake == null ? 0 : Double.parseDouble(onmake.toString());
		mcremain = reamin == null ? 0 : Double.parseDouble(reamin.toString());
		clashqty = clash == null ? 0 : Double.parseDouble(clash.toString());
		int count = baseDao.getCount("select count(1) from makeclash where mc_code='" + ob[1] + "' and mc_class='" + ob[2] + "'");
		if (count == 0) {
			saveclash = 1;
			setclash = 0;
			clashqty=mcremain;
		} else {
			saveclash = 0;
			setclash = 1;
		}
		if("工序退料单".equals(ob[2].toString())){
			clashqty=0;
		}
		if (!"ENTERING".equals(ob[0])) {
			saveclash = 0;
			setclash = 0;
		}
		map.put("saveclash", saveclash);
		map.put("setclash", setclash);
		map.put("mconmake", mconmake);
		map.put("mcremain", mcremain);
		map.put("clashqty", clashqty);
		return map;
	}

	@Override
	public void setProdIOClash(int id, String caller) {
		Object[] ob=baseDao.getFieldsDataByCondition("prodinout",new String[] {"pi_invostatuscode","PI_INOUTNO","PI_class"},"pi_id="+id);
		if("ENTERING".equals(ob[0])){
			baseDao.execute("delete makeclash where mc_code='"+ob[1]+"' and mc_class='"+ob[2]+"'");
		}	
	}
}
