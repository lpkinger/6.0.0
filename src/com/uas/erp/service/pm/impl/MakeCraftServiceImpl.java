package com.uas.erp.service.pm.impl;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.uas.erp.dao.common.PurchaseDao;
import com.uas.erp.service.pm.MakeCraftService;

@Service("makeCraftService")
public class MakeCraftServiceImpl implements MakeCraftService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private PurchaseDao purchaseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveMakeCraft(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		baseDao.asserts.nonExistCode("MakeCraft", "mc_code", store.get("mc_code"));
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, store, grid);
		// 保存MakeCraft
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "MakeCraft"));
		baseDao.execute(SqlUtil.getInsertSqlbyList(grid, "MakeCraftDetail", "mcd_id"));
		updateDate(store.get("mc_id"), caller);
		baseDao.execute("update makeCraft set mc_maid=(select ma_id from make where ma_code=mc_makecode) where nvl(mc_makecode,' ')<>' ' and mc_id="
				+ store.get("mc_id"));
		// 记录操作
		baseDao.logger.save(caller, "mc_id", store.get("mc_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void deleteMakeCraft(int mc_id, String caller) {
		// 只能删除在录入的单据!
		Object[] obs = baseDao.getFieldsDataByCondition("MakeCraft", new String[]{"mc_statuscode","mc_makecode","mc_detno"}, "mc_id=" + mc_id);
		StateAssert.delOnlyEntering(obs[0]);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { mc_id });
		if(("MakeCraftPlant".equals(caller) || "MakeCraftStep".equals(caller)) && baseDao.isDBSetting("usingMakeCraft")){
			String makecode=(obs[1]== null ? "" : obs[1].toString() );
			int mcdetno=(obs[2] ==null ? 0 : Integer.parseInt(obs[2].toString()));
			int count=baseDao.getCount("select count(1) from stepio where si_makecode='"+ makecode+"' and  (st_outno="+mcdetno+" or st_inno="+mcdetno+")");
			if(count>0){
				BaseUtil.showError("作业单已有车间出入单据，不允许删除!");
			}
			int count1=baseDao.getCount("select count(1) from prodiodetail where nvl(pd_jobcode,' ')=(select mc_code from makecraft where mc_id="+mc_id+")");
			if(count1>0){
				BaseUtil.showError("已有出入库单据，不允许删除!");
			}
		}
		// 删除MakeCraft
		baseDao.deleteById("MakeCraft", "mc_id", mc_id);
		// 删除MakeCraftDetail
		baseDao.deleteById("MakeCraftDetail", "mcd_mcid", mc_id);
		// 记录操作
		baseDao.logger.delete(caller, "mc_id", mc_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { mc_id });
	}

	@Override
	public void updateMakeCraftById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("MakeCraft", "mc_statuscode", "mc_id=" + store.get("mc_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, store, gstore);
		// 修改MakeCraft
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "MakeCraft", "mc_id"));
		// 修改MakeCraftDetail
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "MakeCraftDetail", "mcd_id"));
		updateDate(store.get("mc_id"), caller);
		baseDao.execute("update makeCraft set mc_maid=(select ma_id from make where ma_code=mc_makecode) where nvl(mc_makecode,' ')<>' ' and mc_id="
				+ store.get("mc_id"));
		// 记录操作
		baseDao.logger.update(caller, "mc_id", store.get("mc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, store, gstore);
	}

	@Override
	public void printMakeCraft(int mc_id, String caller) {
		updateDate(mc_id, caller);
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, new Object[] { mc_id });
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "mc_id", mc_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, new Object[] { mc_id });
	}

	private void updateDate(Object mc_id, String caller) {
		if ("MakeCraftStep".equals(caller)) {
			SqlRowList rs = baseDao.queryForRowSet("select * from MakeCraft where mc_id=? and mc_tasktype='工序委外单'", mc_id);
			if (rs.next()) {
				if (rs.getGeneralDouble("mc_purcqty") == 0) {
					baseDao.execute("update makecraft set mc_purcqty=round(mc_qty/nvl((select pr_purcrate from product where mc_prodcode=pr_code),1),2) where mc_id="
							+ mc_id);
				}
			}
		}
	}

	@Override
	public void auditMakeCraft(int mc_id, String caller) {
		SqlRowList rs = baseDao.queryForRowSet("select * from MakeCraft where mc_id=?", mc_id);
		if (rs.next()) {
			// 只能对状态为[已提交]的订单进行审核操作!
			StateAssert.auditOnlyCommited(rs.getObject("mc_statuscode"));
			if("工序委外单".equals(rs.getObject("mc_tasktype"))){
				if (!StringUtil.hasText(rs.getObject("mc_vendcode"))) {
					BaseUtil.showError("委外商号不能为空！");
				}
				if (!StringUtil.hasText(rs.getObject("mc_currency"))) {
					BaseUtil.showError("币别不能为空！");
				}
			}			
			// 执行审核前的其它逻辑
			handlerService.beforeAudit(caller, new Object[] { mc_id });
			// 判断排位表编号是否正确
			checkPscode(mc_id);
			// 执行审核操作
			baseDao.audit("MakeCraft", "mc_id=" + mc_id, "mc_status", "mc_statuscode", "mc_auditdate", "mc_auditman");
			// 记录操作
			baseDao.logger.audit(caller, "mc_id", mc_id);
			// 执行审核后的其它逻辑
			handlerService.afterAudit(caller, new Object[] { mc_id });
		} 
		
	}

	@Override
	public void resAuditMakeCraft(int mc_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] obs = baseDao.getFieldsDataByCondition("MakeCraft", new String[]{"mc_statuscode","mc_makecode","mc_detno"}, "mc_id=" + mc_id);
		StateAssert.resAuditOnlyAudit(obs[0]);
		// 判断作业单有备料单，或者有序列号ms_status>0
		Object ob = baseDao.getFieldDataByCondition("makePrepare left join makeCraft on mc_code=mp_mccode", "mp_code", "mc_id=" + mc_id);
		if (ob != null) {
			BaseUtil.showError("作业单存在备料单，不允许反审核!");
		}
		// 判断序列号是否已经上料
		SqlRowList rs = baseDao.queryForRowSet("select count(0) cn from makeserial where ms_mcid=" + mc_id + " and nvl(ms_status,0)>0");
		if (rs.next() && rs.getInt("cn") > 0) {
			BaseUtil.showError("作业单已有序列号上线，不允许反审核!");
		}
		if(("MakeCraftPlant".equals(caller) || "MakeCraftStep".equals(caller)) && baseDao.isDBSetting("usingMakeCraft")){
			String makecode=(obs[1]== null ? "" : obs[1].toString() );
			int mcdetno=(obs[2] ==null ? 0 : Integer.parseInt(obs[2].toString()));
			int count=baseDao.getCount("select count(1) from stepio where si_makecode='"+ makecode+"' and  (st_outno="+mcdetno+" or st_inno="+mcdetno+")");
			if(count>0){
				BaseUtil.showError("作业单已有车间出入单据，不允许反审核!");
			}
			int count1=baseDao.getCount("select count(1) from prodiodetail where nvl(pd_jobcode,' ')=(select mc_code from makecraft where mc_id="+mc_id+")");
			if(count1>0){
				BaseUtil.showError("已有出入库单据，不允许反审核!");
			}
		}
		// 执行反审核操作
		baseDao.resAudit("MakeCraft", "mc_id=" + mc_id, "mc_status", "mc_statuscode", "mc_auditdate", "mc_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "mc_id", mc_id);
	}

	@Override
	public void submitMakeCraft(int mc_id, String caller) {
		updateDate(mc_id, caller);
		// 只能对状态为[在录入]的订单进行提交操作!
		SqlRowList rs = baseDao.queryForRowSet("select * from MakeCraft where mc_id=?", mc_id);
		if (rs.next()) {
			StateAssert.submitOnlyEntering(rs.getObject("mc_statuscode"));
			// 执行提交前的其它逻辑
			handlerService.beforeSubmit(caller, new Object[] { mc_id });
			// 判断排位表编号是否正确
			checkPscode(mc_id);
			// 执行提交操作
			baseDao.submit("MakeCraft", "mc_id=" + mc_id, "mc_status", "mc_statuscode");
			// 记录操作
			baseDao.logger.submit(caller, "mc_id", mc_id);
			// 执行提交后的其它逻辑
			handlerService.afterSubmit(caller, new Object[] { mc_id });
		}
	}

	@Override
	public void resSubmitMakeCraft(int mc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MakeCraft", "mc_statuscode", "mc_id=" + mc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { mc_id });
		// 执行反提交操作
		baseDao.resOperate("MakeCraft", "mc_id=" + mc_id, "mc_status", "mc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "mc_id", mc_id);
		handlerService.afterResSubmit(caller, new Object[] { mc_id });
	}

	@Override
	public void endMakeCraft( String caller,int mc_id) {
		// 只能对状态为[已审核]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("MakeCraft", "mc_statuscode,mc_makecode,mc_code", "mc_id=" + mc_id);
		StateAssert.end_onlyAudited(status[0]);
		SqlRowList rs = baseDao.queryForRowSet("select mm_prodcode from MakeMaterial where mm_code='"+status[1]+"' and mm_mdcode='"+status[2]+"' and nvl(mm_clashqty,0)<>0 and nvl(mm_havegetqty,0)+nvl(mm_stepinqty,0)-nvl(mm_clashqty,0)<>0");
		if(rs.next()){
			BaseUtil.showError("明细行存在在制冲减数、结存数不为0的行，不允许结案");
		}
		// 执行结案操作
		baseDao.updateByCondition("MakeCraft", "mc_status='" + BaseUtil.getLocalMessage("FINISH") + "',mc_statuscode='FINISH'", "mc_id="
				+ mc_id);
		// 开启工厂模式，已结案作业单对应工单的明细行作业单均结案时,也结案该工单. maz
		if (baseDao.isDBSetting("usingMakeCraft")) {
			Object sign = baseDao.getFieldDataByCondition("MakeCraft", "count(*)", "mc_makecode='"+status[1]+"' and mc_status<>'已结案'");
			if(StringUtil.hasText(sign) && "0".equals(sign.toString())){
				baseDao.updateByCondition("Make", "ma_status='" + BaseUtil.getLocalMessage("FINISH") + "',ma_statuscode='FINISH'", "ma_code='"
						+ status[1] + "'");
				Object ma_id = baseDao.getFieldDataByCondition("Make", "ma_id", "ma_code='"+status[1]+"'");
				baseDao.logger.others("结案操作", "明细工厂单均结案自动结案", "Make!Base",
						"ma_id", ma_id);
			}
		}
		// 记录操作
		baseDao.logger.end(caller, "mc_id", mc_id);
	}
	
	@Override
	public void forceEndMakeCraft( String caller,int mc_id) {
		// 只能对状态为[已审核]的订单进行结案操作!
		Object[] status = baseDao.getFieldsDataByCondition("MakeCraft", "mc_statuscode,mc_makecode,mc_code", "mc_id=" + mc_id);
		StateAssert.end_onlyAudited(status[0]);
		// 执行结案操作
		baseDao.updateByCondition("MakeCraft", "mc_status='" + BaseUtil.getLocalMessage("FINISH") + "',mc_statuscode='FINISH'", "mc_id="
				+ mc_id);
		// 开启工厂模式，已结案作业单对应工单的明细行作业单均结案时,也结案该工单. maz
		if (baseDao.isDBSetting("usingMakeCraft")) {
			Object sign = baseDao.getFieldDataByCondition("MakeCraft", "count(*)", "mc_makecode='"+status[1]+"' and mc_status<>'已结案'");
			if(StringUtil.hasText(sign) && "0".equals(sign.toString())){
				baseDao.updateByCondition("Make", "ma_status='" + BaseUtil.getLocalMessage("FINISH") + "',ma_statuscode='FINISH'", "ma_code='"
						+ status[1] + "'");
				Object ma_id = baseDao.getFieldDataByCondition("Make", "ma_id", "ma_code='"+status[1]+"'");
				baseDao.logger.others("结案操作", "明细工厂单均结案自动结案", "Make!Base",
						"ma_id", ma_id);
			}
		}
		// 记录操作
		baseDao.logger.others("强制结案操作", "强制结案", caller,
				"mc_id", mc_id);
	}

	@Override
	public void resEndMakeCraft(int mc_id, String caller) {
		// 只能对状态为[已结案]的订单进行反结案操作!
		Object[] status = baseDao.getFieldsDataByCondition("MakeCraft", "mc_statuscode,mc_makecode", "mc_id=" + mc_id);
		StateAssert.resEnd_onlyAudited(status[0]);
		boolean bool = baseDao.checkIf("Make", "ma_code='" + status[1] + "' and ma_statuscode='FINISH'");
		if (bool) {
			BaseUtil.showError("请先反结案工单!");
		}
		// 执行反结案操作
		baseDao.updateByCondition("MakeCraft", "mc_status='" + BaseUtil.getLocalMessage("AUDITED") + "',mc_statuscode='AUDITED'", "mc_id="
				+ mc_id);

		// 记录操作
		baseDao.logger.resEnd(caller, "mc_id", mc_id);
	}

	// 判断排位表编号
	private void checkPscode(int mc_id) {
		// 判断排位表是否存在，状态是否为已审核，排位表中的产品编号与作业单中的产品编号，所属产线代码是否一致
		Object[] obs = baseDao.getFieldsDataByCondition("makeCraft", new String[] { "mc_pscode", "mc_prodcode", "mc_linecode" }, "mc_id="
				+ mc_id);
		if (obs[0] != null) {
			String ps_code = obs[0].toString();
			Object ob = baseDao.getFieldDataByCondition("productsmt", "ps_statuscode", "ps_code='" + ps_code + "'");
			if (ob != null) {
				if (!ob.toString().equals("AUDITED")) {
					BaseUtil.showError("排位表编号:" + ps_code + "未审核!");
				}
				int cn = baseDao.getCount("select count(1)　from  productsmt where ps_code='" + ps_code + "' and ps_prodcode='" + obs[1]
						+ "'");
				if (cn == 0) {
					BaseUtil.showError("排位表编号:" + ps_code + "中的物料编号与作业单中的产品编号不一致!");
				}
				cn = baseDao.getCount("select count(1)　from productsmt where  ps_code='" + ps_code + "' and ps_linecode='" + obs[2] + "'");
				if (cn == 0) {
					BaseUtil.showError("排位表编号:" + ps_code + "中的线别与作业单中的线别不一致!");
				}
			} else {
				BaseUtil.showError("排位表编号:" + ps_code + "不存在!");
			}
		}
	}

	@Override
	public Map<String, Object> getMakeCraft(String caller) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> workcenter = new ArrayList<Map<String, Object>>();
		String sql = "select wc_code,wc_name from WORKCENTER";
		workcenter = baseDao.queryForList(sql);
		map.put("data", workcenter);
		return map;
	}

	@Override
	public Map<String, Object> getWorkCenter(String wc_code) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> workcenter = new ArrayList<Map<String, Object>>();
		String sql = " select * from MakeCraft Left join Product ON mc_prodcode=pr_code left join WorkCenter on mc_wccode=wc_code where mc_statuscode not in('FINISH','COMPLETED') and mc_tasktype='车间作业单' and  mc_wccode='"
				+ wc_code + "' order by mc_code,mc_id";
		workcenter = baseDao.queryForList(sql);
		map.put("data", workcenter);
		return map;
	}

	@Override
	public String vastTurnCraftTransfer(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String log = null;
		StringBuffer sb = new StringBuffer();
		for (Map<Object, Object> map : maps) {
			Object uncommitqty = baseDao.getFieldDataByCondition(
					"stepIO left join makecraft on mc_makecode=si_makecode and mc_detno=st_outno", "nvl(sum(si_qty),0)",
					" st_class='工序报废' and " + "si_statuscode in ('COMMITED' ,'AUDITED' ) and mc_id=" + map.get("mc_id").toString());
			int si_id = baseDao.getSeqId("STEPIO_SEQ");
			String code = baseDao.sGetMaxNumber("Stepio!CraftTransfer", 2);
			baseDao.execute("insert into stepio(si_id,st_class,si_code,si_status,si_statuscode,si_indate,si_recorder,si_makecode,"
					+ "si_prodcode,st_outno,st_outstep,st_outwccode,st_outwcname,si_qty,st_outman,st_date,st_cop,st_type) " + "values("
					+ si_id
					+ ",'工序转移','"
					+ code
					+ "','在录入','ENTERING',sysdate,'"
					+ SystemSession.getUser().getEm_name()
					+ "','"
					+ map.get("mc_makecode")
					+ "','"
					+ map.get("mc_prodcode")
					+ "',"
					+ map.get("mc_detno")
					+ ",'"
					+ map.get("mc_stepname")
					+ "','"
					+ map.get("mc_wccode")
					+ "','"
					+ map.get("wc_name")
					+ "',"
					+ map.get("mc_onmake")
					+ "-"
					+ uncommitqty
					+ ",'"
					+ SystemSession.getUser().getEm_name() + "',sysdate,'" + map.get("mc_cop") + "','正常转序')");
			int count = baseDao.getCount("select Count(1) from  makecraft where mc_craftcode='" + map.get("mc_subofcode")
					+ "' and mc_makecode='" + map.get("mc_makecode") + "'");
			if (count > 0) {
				Object[] ob = baseDao.getFieldsDataByCondition("makeCraft left join workcenter on mc_wccode=wc_code", new String[] {
						"mc_detno", "mc_wccode", "wc_name", "mc_stepname" }, "mc_makecode='" + map.get("mc_makecode")
						+ "' and mc_craftcode='" + map.get("mc_subofcode") + "'");
				baseDao.execute("update stepio set st_inno='" + ob[0] + "',st_inmakecode='" + map.get("mc_makecode") + "',st_inwccode='"
						+ ob[1] + "'," + "st_inwcname='" + ob[2] + "',st_instep='" + ob[3] + "'  where si_id='" + si_id + "'");
			}
			log = "转入成功,工序转移单号:"
					+ "<a href=\"javascript:openUrl('jsps/pm/make/Stepio.jsp?whoami=Stepio!CraftTransfer&formCondition=si_idIS" + si_id
					+ "')\">" + code + "</a>&nbsp;";
			sb.append(log).append("<hr>");
			// 记录日志
		}
		return sb.toString();
	}

	@Override
	public String vastTurnCraftJump(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String log = null;
		StringBuffer sb = new StringBuffer();
		for (Map<Object, Object> map : maps) {
			Object uncommitqty = baseDao.getFieldDataByCondition(
					"stepIO left join makecraft on mc_makecode=si_makecode and mc_detno=st_outno", "nvl(sum(si_qty),0)",
					" st_class='工序报废' and " + "si_statuscode in ('COMMITED' ,'AUDITED' ) and mc_id=" + map.get("mc_id").toString());
			int si_id = baseDao.getSeqId("STEPIO_SEQ");
			String code = baseDao.sGetMaxNumber("Stepio!CraftJump", 2);
			baseDao.execute("insert into stepio(si_id,st_class,si_code,si_status,si_statuscode,si_indate,si_recorder,si_makecode,"
					+ "si_prodcode,st_outno,st_outstep,st_outwccode,st_outwcname,si_qty,st_outman,st_date,st_cop) " + "values("
					+ si_id
					+ ",'工序跳转','"
					+ code
					+ "','在录入','ENTERING',sysdate,'"
					+ SystemSession.getUser().getEm_name()
					+ "','"
					+ map.get("mc_makecode")
					+ "','"
					+ map.get("mc_prodcode")
					+ "',"
					+ map.get("mc_detno")
					+ ",'"
					+ map.get("mc_stepname")
					+ "','"
					+ map.get("mc_wccode")
					+ "','"
					+ map.get("wc_name")
					+ "',"
					+ map.get("mc_onmake")
					+ "-"
					+ uncommitqty
					+ ",'"
					+ SystemSession.getUser().getEm_name() + "',sysdate,'" + map.get("mc_cop") + "')");
			log = "转入成功,工序跳转单号:" + "<a href=\"javascript:openUrl('jsps/pm/make/Stepio.jsp?whoami=Stepio!CraftJump&formCondition=si_idIS"
					+ si_id + "')\">" + code + "</a>&nbsp;";
			sb.append(log).append("<hr>");
			// 记录日志
		}
		return sb.toString();
	}

	@Override
	public String vastTurnMadeIN(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String log = null;
		StringBuffer sb = new StringBuffer();
		for (Map<Object, Object> map : maps) {
			int id = baseDao.getSeqId("PRODINOUT_SEQ");
			String code = baseDao.sGetMaxNumber("ProdInOut!Make!In", 2);
			baseDao.execute("insert into prodinout (pi_id,pi_inoutno,pi_class,pi_date,pi_invostatus,pi_invostatuscode,pi_recordman,pi_recorddate,pi_cop,"
					+ "pi_status,pi_statuscode, PI_PRINTSTATUS,PI_PRINTSTATUSCODE) "
					+ "values("
					+ id
					+ ",'"
					+ code
					+ "','完工入库单',sysdate,'在录入','ENTERING','"
					+ SystemSession.getUser().getEm_name()
					+ "',sysdate,'"
					+ map.get("mc_cop")
					+ "'," + "'未过账','UNPOST','未打印','UNPRINT')");

			Object uncommitqty = baseDao.getFieldDataByCondition(
					"stepIO left join makecraft on mc_makecode=si_makecode and mc_detno=st_outno", "nvl(sum(si_qty),0)",
					" st_class='工序报废' and " + "si_statuscode in ('COMMITED' ,'AUDITED' ) and mc_id=" + map.get("mc_id"));
			baseDao.execute("insert into prodiodetail(pd_id,pd_piid,pd_inoutno,pd_pdno,pd_status,pd_piclass,pd_inqty,pd_outqty,pd_prodcode,"
					+ "pd_ordercode,pd_orderdetno,pd_description,pd_jobcode,pd_mcid) "
					+ "values(PRODIODETAIL_SEQ.NEXTVAL,"
					+ id
					+ ",'"
					+ code
					+ "',1,0,'完工入库单',"
					+ map.get("mc_onmake")
					+ "-"
					+ uncommitqty
					+ ",0,'"
					+ map.get("mc_prodcode")
					+ "',"
					+ "'"
					+ map.get("mc_makecode")
					+ "','"
					+ map.get("mc_detno")
					+ "','良品入库','"
					+ map.get("mc_code")
					+ "',"
					+ map.get("mc_id")
					+ ")");
			log = "转入成功,完工入库单单号:"
					+ "<a href=\"javascript:openUrl('jsps/pm/mes/prodInOut.jsp?whoami=ProdInOut!ProcessFinish&formCondition=pi_idIS" + id
					+ "&gridCondition=mc_pdidIS" + id + "')\">" + code + "</a>&nbsp;";
			
			// 重新更新已转完工入库数
			baseDao.execute("update make set ma_tomadeqty=(select sum(pd_inqty)-sum(case when pd_status=99 then pd_outqty else 0 end) "
					+ "from prodiodetail where pd_ordercode=ma_code and pd_piclass in('完工入库单','委外验收单','委外验退单') and pd_prodcode=ma_prodcode) "
					+ "where ma_code='"	+ map.get("mc_makecode")+"' and ma_prodcode='"+map.get("mc_prodcode")+"'");
			baseDao.execute("update make set ma_tomadeqty=ma_madeqty where ma_code='" + map.get("mc_makecode")+"' and ma_madeqty>ma_tomadeqty and ma_prodcode='"+map.get("mc_prodcode")+"'");
			
			sb.append(log).append("<hr>");
			// 记录日志
		}
		return sb.toString();
	}

	@Override
	public String vastTurnCraftBack(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String log = null;
		StringBuffer sb = new StringBuffer();
		for (Map<Object, Object> map : maps) {
			int si_id = baseDao.getSeqId("STEPIO_SEQ");
			String code = baseDao.sGetMaxNumber("Stepio!CraftBack", 2);
			baseDao.execute("insert into stepio(si_id,st_class,si_code,si_status,si_statuscode,si_indate,si_recorder,si_makecode,"
					+ "st_outno,st_outstep,st_outwccode,st_outwcname,si_qty,st_outman,st_date,st_cop) " + "values("
					+ si_id
					+ ",'工序退制','"
					+ code
					+ "','在录入','ENTERING',sysdate,'"
					+ SystemSession.getUser().getEm_name()
					+ "','"
					+ map.get("mc_makecode")
					+ "',"
					+ map.get("mc_detno")
					+ ",'"
					+ map.get("mc_stepname")
					+ "','"
					+ map.get("mc_wccode")
					+ "','"
					+ map.get("wc_name")
					+ "',"
					+ map.get("mc_onmake")
					+ ",'"
					+ SystemSession.getUser().getEm_name() + "',sysdate,'" + map.get("mc_cop") + "')");
			Object bomid=baseDao.getFieldDataByCondition("bom","bo_id","bo_mothercode='"+map.get("mc_prodcode")+"'");
			int count =baseDao.getCount("select count(1) from makecraft where mc_makecode='"+map.get("mc_makecode")+"' and mc_subofcode='"+map.get("mc_craftcode")+"' "
					+ "and mc_prodcode in (select bo_mothercode from bomdetail left join bom on bd_sonbomid=bo_id and bd_bomid="+ (bomid == null ? 0 : bomid) +")"); 
			if(count==1){
				Object[] ob=baseDao.getFieldsDataByCondition("makecraft left join workcenter on mc_wccode=wc_code",
						new String[] {"mc_prodcode","mc_detno","mc_stepname","mc_wccode","wc_name","mc_stepoutqty"},
						"mc_makecode='"+map.get("mc_makecode")+"' and mc_subofcode='"+map.get("mc_craftcode")+"' and "
								+ "mc_prodcode in (select bo_mothercode from bomdetail left join bom on bd_sonbomid=bo_id and bd_bomid="+ (bomid == null ? 0 : bomid) +")");
				baseDao.execute("update stepio set si_prodcode='"+ob[0]+"' ,st_inno="+ob[1]+",st_instep='"+ob[2]+"' ,st_inwccode='"+ob[3]+"',st_inwcname='"+ob[4]+"' "
						+ ", si_qty="+ob[5]+" where si_id="+si_id);
			}
			log = "转入成功,工序退制单号:" + "<a href=\"javascript:openUrl('jsps/pm/make/Stepio.jsp?whoami=Stepio!CraftBack&formCondition=si_idIS"
					+ si_id + "')\">" + code + "</a>&nbsp;";
			sb.append(log).append("<hr>");
			// 记录日志
		}
		return sb.toString();
	}

	@Override
	public String vastTurnCraftReturn(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String log = null;
		StringBuffer sb = new StringBuffer();
		for (Map<Object, Object> map : maps) {
			int id = baseDao.getSeqId("PRODINOUT_SEQ");
			String code = baseDao.sGetMaxNumber("ProdInOut!ProcessReturn", 2);
			baseDao.execute("insert into prodinout (pi_id,pi_inoutno,pi_class,pi_date,pi_invostatus,pi_invostatuscode,pi_recordman,pi_recorddate,pi_cop,"
					+ "pi_status,pi_statuscode, PI_PRINTSTATUS,PI_PRINTSTATUSCODE) "
					+ "values("
					+ id
					+ ",'"
					+ code
					+ "','工序退料单',sysdate,'在录入','ENTERING','"
					+ SystemSession.getUser().getEm_name()
					+ "',sysdate,'"
					+ map.get("mc_cop")
					+ "','未过账','UNPOST','未打印','UNPRINT')");
			baseDao.execute("insert into prodiodetail(pd_id,pd_piid,pd_inoutno,pd_pdno,pd_status,pd_piclass,pd_inqty,pd_outqty,pd_prodcode,"
					+ "pd_ordercode,pd_orderdetno,pd_description,pd_jobcode,pd_mcid) "
					+ "values(PRODIODETAIL_SEQ.NEXTVAL,"
					+ id
					+ ",'"
					+ code
					+ "',1,0,'工序退料单',"
					+ map.get("mc_onmake")
					+ ",0,'"
					+ map.get("mc_prodcode")
					+ "','"
					+ map.get("mc_makecode")
					+ "','" + map.get("mc_detno") + "','良品退仓','" + map.get("mc_code") + "'," + map.get("mc_id") + ")");
			log = "转入成功,工序退料单号:"
					+ "<a href=\"javascript:openUrl('jsps/pm/mes/prodInOut.jsp?whoami=ProdInOut!ProcessReturn&formCondition=pi_idIS" + id
					+ "&gridCondition=mc_pdidIS" + id + "')\">" + code + "</a>&nbsp;";
			sb.append(log).append("<hr>");
			// 记录日志
		}
		return sb.toString();
	}

	@Override
	public String vastTurnCraftScrap(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String log = null;
		StringBuffer sb = new StringBuffer();
		for (Map<Object, Object> map : maps) {
			int si_id = baseDao.getSeqId("STEPIO_SEQ");
			String code = baseDao.sGetMaxNumber("Stepio!CraftScrap", 2);
			Object whcode = baseDao.getFieldDataByCondition("warehouse", "max(wh_code)",
					"wh_description like '%报废%' and  wh_cop='" + map.get("mc_cop") + "'");
			baseDao.execute("insert into stepio(si_id,st_class,si_code,si_status,si_statuscode,si_indate,si_recorder,si_makecode,"

			+ "si_prodcode,st_outno,st_outstep,st_outwccode,st_outwcname,si_qty,st_outman,st_date,st_cop,st_whcode) " + "values(" + si_id
					+ ",'工序报废','" + code + "','在录入','ENTERING',sysdate,'" + SystemSession.getUser().getEm_name() + "','"
					+ map.get("mc_makecode") + "'," + "'" + map.get("mc_prodcode") + "'," + map.get("mc_detno") + ",'"
					+ map.get("mc_stepname") + "','" + map.get("mc_wccode") + "','" + map.get("wc_name") + "'," + map.get("mc_onmake")
					+ ",'" + SystemSession.getUser().getEm_name() + "',sysdate,'" + map.get("mc_cop") + "','" + whcode + "')");
			log = "转入成功,工序报废单号:" + "<a href=\"javascript:openUrl('jsps/pm/make/Stepio.jsp?whoami=Stepio!CraftScrap&formCondition=si_idIS"
					+ si_id + "&gridCondition=sd_siidIS" + si_id + "')\">" + code + "</a>&nbsp;";
			sb.append(log).append("<hr>");
			// 记录日志
		}
		return sb.toString();
	}

	@Override
	public void updateOSVendor(Integer id, String vendcode, String currency,
			String taxrate, String price, String paymc, String paym,
			String mc_servicer, String remark, String apvendcode, String caller) { 
		if (!mc_servicer.equals("1") && !mc_servicer.equals("-1")) {
			mc_servicer = "0";
		}
		Object vendname = baseDao.getFieldDataByCondition("Vendor", "ve_name", "ve_code='" + vendcode + "'");
		baseDao.updateByCondition("MakeCraft", "mc_vendcode='" + vendcode + "', mc_vendname='" + vendname + "', mc_currency='" + currency
				+ "',mc_taxrate= " + taxrate + ", mc_price=round(" + price + ",8), mc_paymentscode='" + paymc + "', mc_payments='" + paym + "',"
						+ "mc_servicer='" + mc_servicer + "'", "mc_id ="+id);
		if (StringUtil.hasText(apvendcode)) {
			baseDao.execute("update MakeCraft set Mc_APVENDCODE='" + apvendcode + "' where mc_id=" + id);
			baseDao.execute("update MakeCraft set Mc_APVENDNAME=(select ve_name from vendor where ve_code=Mc_APVENDCODE) where mc_id=" + id
					+ " and nvl(Mc_APVENDCODE,' ')<>' '");		
		} else {
			baseDao.execute("update MakeCraft set (Mc_APVENDCODE,Mc_APVENDNAME)=(select ve_apvendcode,ve_apvendname from vendor where ve_code=mc_vendcode) where mc_id="
					+ id);
		}
		// 更新汇率
		baseDao.execute("update makeCraft set mc_rate=(select cm_crrate from currencysmonth where mc_currency=cm_crname and "
				+ "cm_yearmonth=to_char(mc_date,'yyyymm')) where mc_id=?", id);
		baseDao.execute("update makeCraft set mc_price=0 where nvl(mc_servicer,0)<>0 and mc_id=?", id);//免费加工
		 
		if (remark != null && !"".equals(remark)) {
			baseDao.updateByCondition("MakeCraft", "mc_remark='" + remark + "'", "mc_id =" + id);
		} 
		
		// 记录操作
		baseDao.logger.others("更新委外信息", "msg.updateSuccess", caller, "ma_id", id); 
		// 到物料核价单取单价
		if ("0".equals(mc_servicer) && baseDao.checkIf("makecraft", "mc_id="+id+" and nvl(mc_price,0)=0")) {
			int count = baseDao
					.getCount("select count(1) from PURCHASEPRICEDETAIL LEFT JOIN PURCHASEPRICE ON PPD_PPID=PP_ID LEFT JOIN CURRENCYS ON PPD_CURRENCY=CR_NAME LEFT JOIN VENDOR ON PPD_VENDCODE=VE_CODE left join MakeCraft on PPD_PRODCODE=MC_PRODCODE AND PPD_VENDCODE=NVL(MC_VENDCODE,PPD_VENDCODE) AND PPD_CURRENCY=NVL(MC_CURRENCY,PPD_CURRENCY) where mc_id="
							+ id
							+ " and PP_KIND='委外' AND PPD_LAPQTY<=NVL(MC_QTY,0) and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID'");
			if (count > 0) {
				baseDao.execute("UPDATE MAKECRAFT SET (MC_PRICE,MC_VENDCODE,MC_VENDNAME,MC_CURRENCY,MC_RATE,mc_taxrate)=("
						+ "SELECT PRICE,PPD_VENDCODE,VE_NAME,PPD_CURRENCY,CR_RATE,PPD_RATE FROM (SELECT PPD_VENDCODE,VE_NAME,PPD_RATE,CR_RATE,PPD_CURRENCY,PPD_PRICE,PPD_PRICE * CR_RATE AS PRICE,RANK() OVER (PARTITION BY PPD_PRODCODE ORDER BY (PPD_PRICE * CR_RATE ) ASC ,PPD_ID DESC) MM,PPD_ID "
						+ "FROM PURCHASEPRICEDETAIL LEFT JOIN PURCHASEPRICE ON PPD_PPID=PP_ID LEFT JOIN CURRENCYS ON PPD_CURRENCY=CR_NAME LEFT JOIN VENDOR ON PPD_VENDCODE=VE_CODE left join MakeCraft on PPD_PRODCODE=MC_PRODCODE AND PPD_VENDCODE=NVL(MC_VENDCODE,PPD_VENDCODE) AND PPD_CURRENCY=NVL(MC_CURRENCY,PPD_CURRENCY)  "
						+ "WHERE  mc_id= "+id+" AND PPD_PRODCODE=MC_PRODCODE AND PPD_VENDCODE=NVL(MC_VENDCODE,PPD_VENDCODE) AND PPD_CURRENCY=NVL(MC_CURRENCY,PPD_CURRENCY) AND PP_KIND='委外' AND PPD_LAPQTY<=NVL(MC_QTY,0) "
						+ "and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID') where mm=1) where mc_id="
						+ id);
			} else {
				BaseUtil.showErrorOnSuccess("核价单中未找到单价，请手工填写单价和委外商!");
			}
		}
				
	}

}
