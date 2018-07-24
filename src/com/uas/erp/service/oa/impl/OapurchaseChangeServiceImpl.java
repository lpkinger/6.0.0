package com.uas.erp.service.oa.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.oa.OapurchaseChangeService;

@Service
public class OapurchaseChangeServiceImpl implements OapurchaseChangeService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveOapurchaseChange(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 保存OapurchaseChange
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"OapurchaseChange", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存Contact
		for (Map<Object, Object> s : grid) {
			s.put("ocd_id", baseDao.getSeqId("OapurchaseChangedet_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"OapurchaseChangedet");
		baseDao.execute(gridSql);
		// 记录操作
					baseDao.logger.save(caller, "oc_id", store.get("oc_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteOapurchaseChange(int oc_id, String caller) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("OapurchaseChange",
				"oc_statuscode", "oc_id=" + oc_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { oc_id });
		// 删除OapurchaseChange
		baseDao.deleteById("OapurchaseChange", "oc_id", oc_id);
		// 删除Contact
		baseDao.deleteById("OapurchaseChangedet", "ocd_ocid", oc_id);
		// 记录操作
		baseDao.logger.delete(caller, "oc_id", oc_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { oc_id });
	}

	@Override
	public void updateOapurchaseChangeById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("OapurchaseChange",
				"oc_statuscode", "oc_id=" + store.get("oc_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改OapurchaseChange
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"OapurchaseChange", "oc_id");
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"OapurchaseChangedet", "ocd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("ocd_id") == null || s.get("ocd_id").equals("")
					|| s.get("ocd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("OapurchaseChangedet_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s,
						"OapurchaseChangedet", new String[] { "ocd_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "oc_id", store.get("oc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void submitOapurchaseChange(int oc_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("OapurchaseChange",
				"oc_statuscode", "oc_id=" + oc_id);
		StateAssert.submitOnlyEntering(status);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(oc_code) from OapurchaseChange left join OapurchaseChangeDet on oc_id=ocd_ocid where nvl(oc_statuscode,' ') in ('COMMITED','ENTERING') and oc_id<>?"
								+ " and (ocd_opcode,ocd_oddetno) in (select ocd_opcode,ocd_oddetno from OapurchaseChangeDet where ocd_ocid=?)",
						String.class, oc_id, oc_id);
		if (dets != null) {
			BaseUtil.showError("采购单号+采购行号存在未审核的采购变更单，不允许提交！变更单号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('明细行号：'||ocd_detno||'状态：'||op_statuscode) from OapurchaseChangeDet left join Oapurchase on ocd_opcode=op_code where nvl(op_statuscode,' ')<>'AUDITED' and ocd_ocid=?",
						String.class, oc_id);
		if (dets != null) {
			BaseUtil.showError("明细行需要变更的采购单状态不等于已审核，不允许提交！" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('采购单号：'||ocd_opcode||'采购行号：'||ocd_oddetno) from OapurchaseChangeDet where nvl(ocd_oldprocode,' ')<>nvl(ocd_newprocode,' ')"
								+ " and (ocd_opcode,ocd_oddetno) in (select pd_ordercode,pd_orderdetno from ProdIODetail where pd_piclass in ('用品验收单','用品验退单')) and ocd_ocid=?",
						String.class, oc_id);
		if (dets != null) {
			BaseUtil.showError("出入库单已使用，不允许变更料号！" + dets);
		}
		//变更的数量不能小于已转数或已验收数
		List<Object[]> data = baseDao
				.getFieldsDatasByCondition(
						"oapurchasedetail left join OapurchaseChangeDet on od_id=ocd_odid",
						new String[] { "nvl(od_yqty,0)", "nvl(od_ysqty,0)",
								"nvl(ocd_newneed,0)","ocd_detno" }, "ocd_ocid=" + oc_id);
		for (Object[] d : data) {
			int needNum=Integer.parseInt(d[2].toString());
			if(needNum<Integer.parseInt(d[0].toString())){
				BaseUtil.showError("第"+d[3]+"行,新需求数量小于已转数量，不允许提交！");
			}
			if(needNum<Integer.parseInt(d[1].toString())){
				BaseUtil.showError("第"+d[3]+"行,新需求数量小于已过账数量，不允许提交！");
			}
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { oc_id });
		// 执行提交操作
		baseDao.submit("OapurchaseChange", "oc_id=" + oc_id, "oc_status",
				"oc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "oc_id", oc_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { oc_id });
	}

	@Override
	public void resSubmitOapurchaseChange(int oc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("OapurchaseChange",
				"oc_statuscode", "oc_id=" + oc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { oc_id });
		// 执行反提交操作
		baseDao.resOperate("OapurchaseChange", "oc_id=" + oc_id, "oc_status",
				"oc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "oc_id", oc_id);
		handlerService.afterResSubmit(caller, new Object[] { oc_id });
	}

	@Override
	@Transactional
	public void auditOapurchaseChange(int oc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("OapurchaseChange",
				new String[]{"oc_statuscode","nvl(oc_application,0)"}, "oc_id=" + oc_id);
		StateAssert.auditOnlyCommited(status[0]);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, oc_id);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(oc_code) from OapurchaseChange left join OapurchaseChangeDet on oc_id=ocd_ocid where nvl(oc_statuscode,' ') in ('COMMITED','ENTERING') and oc_id<>?"
								+ " and (ocd_opcode,ocd_oddetno) in (select ocd_opcode,ocd_oddetno from PurchaseChangedetail where ocd_ocid=?)",
						String.class, oc_id, oc_id);
		if (dets != null) {
			BaseUtil.showError("采购单号+采购行号存在未审核的采购变更单，不允许审核！变更单号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('明细行号：'||ocd_detno||'状态：'||op_statuscode) from OapurchaseChangeDet left join Oapurchase on ocd_opcode=op_code where nvl(op_statuscode,' ')<>'AUDITED' and ocd_ocid=?",
						String.class, oc_id);
		if (dets != null) {
			BaseUtil.showError("明细行需要变更的采购单状态不等于已审核，不允许审核！" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('采购单号：'||ocd_opcode||'采购行号：'||ocd_oddetno) from OapurchaseChangeDet where nvl(ocd_oldprocode,' ')<>nvl(ocd_newprocode,' ')"
								+ " and (ocd_opcode,ocd_oddetno) in (select pd_ordercode,pd_orderdetno from ProdIODetail where pd_piclass in ('用品验收单','用品验退单')) and ocd_ocid=?",
						String.class, oc_id);
		if (dets != null) {
			BaseUtil.showError("出入库单已使用，不允许变更料号!：" + dets);
		}
		// 反应到原用品采购单上
		List<Object[]> gridData = baseDao.getFieldsDatasByCondition(
				"OapurchaseChangedet", new String[] { "ocd_newprocode",
						"ocd_newproname", "ocd_newprounit", "ocd_newneed",
						"ocd_newprice", "ocd_newtotal", "ocd_odid", "ocd_oldneed" },
				"ocd_ocid=" + oc_id);
		List<String> sqls = new ArrayList<String>();
		for (Object[] gData : gridData) {
			double newqty = Double.parseDouble(gData[3].toString());
			double oldqty = Double.parseDouble(gData[7].toString());
			String sql1 = "update oapurchasedetail set od_procode='" + gData[0]
					+ "',od_proname='" + gData[1] + "',od_prounit='" + gData[2]
					+ "',od_neednumber=" + newqty + ",od_price=" + gData[4]
					+ ",od_total=" + gData[5] + " where od_id=" + gData[6];
			sqls.add(sql1);
			if (Integer.parseInt(status[1].toString()) == 1) {
				if (oldqty-newqty > 0){
					SqlRowList rs = baseDao.queryForRowSet("select od_oacode,od_oadetno from oapurchasedetail where od_id=?", new Object[] { gData[6] });
					if(rs.next()){
						Object odid = baseDao.getFieldDataByCondition("oaapplicationdetail left join Oaapplication on oa_id=od_oaid",
								"od_id", "oa_code='" + rs.getObject("od_oacode") + "' and od_detno=" + rs.getObject("od_oadetno"));
						String sql2 = "update oaapplicationdetail set od_yqty=nvl(od_yqty,0)-" + (oldqty-newqty)
							+ " where od_id =" + odid;
						sqls.add(sql2);
					}
				}
			}
		}
		baseDao.execute(sqls);
		//更新原采购单主表金额、大写金额
		baseDao.execute("update Oapurchase set op_total=(select round(sum(od_total),2) from OapurchaseDetail where od_oaid=op_id) where op_code in (select distinct ocd_opcode from OapurchaseChangeDet where ocd_ocid="+oc_id+")");
		baseDao.execute("update Oapurchase set op_totalupper=L2U(nvl(op_total,0)) WHERE op_code in (select distinct ocd_opcode from OapurchaseChangeDet where ocd_ocid="+oc_id+")");
		// 执行审核操作
		baseDao.audit("OapurchaseChange", "oc_id=" + oc_id, "oc_status",
				"oc_statuscode");
		// 记录操作
		baseDao.logger.audit(caller, "oc_id", oc_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, oc_id);
	}
}
