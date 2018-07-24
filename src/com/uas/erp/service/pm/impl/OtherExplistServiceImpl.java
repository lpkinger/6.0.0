package com.uas.erp.service.pm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.OtherExplistService;

@Service("otherExplistService")
public class OtherExplistServiceImpl implements OtherExplistService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveOtherExplist(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		baseDao.asserts.nonExistCode("OtherExplist", "ma_code", store.get("ma_code"));
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, store, grid);
		// 保存OtherExplist
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "OtherExplist"));
		baseDao.execute(SqlUtil.getInsertSqlbyList(grid, "OtherExplistDetail", "md_id"));
		// 记录操作
		baseDao.logger.save(caller, "ma_id", store.get("ma_id"));
		updateDatas(store.get("ma_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void deleteOtherExplist(int ma_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("OtherExplist", "ma_statuscode", "ma_id=" + ma_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { ma_id });
		// 删除OtherExplist
		baseDao.deleteById("OtherExplist", "ma_id", ma_id);
		// 删除OtherExplistDetail
		baseDao.deleteById("OtherExplistDetail", "md_maid", ma_id);
		// 记录操作
		baseDao.logger.delete(caller, "ma_id", ma_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { ma_id });
	}

	@Override
	public void updateOtherExplistById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("OtherExplist", "ma_statuscode", "ma_id=" + store.get("ma_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, store, gstore);
		// 修改OtherExplist
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "OtherExplist", "ma_id"));
		// 修改OtherExplistDetail
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "OtherExplistDetail", "md_id"));
		updateDatas(store.get("ma_id"));
		// 记录操作
		baseDao.logger.update(caller, "ma_id", store.get("ma_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, store, gstore);
	}

	@Override
	public void printOtherExplist(int ma_id, String caller) {
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, new Object[] { ma_id });
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "ma_id", ma_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, new Object[] { ma_id });
	}

	@Override
	public void auditOtherExplist(int ma_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("OtherExplist", "ma_statuscode", "ma_id=" + ma_id);
		StateAssert.auditOnlyCommited(status);
		String dets = baseDao.queryForObject("select wm_concat(md_detno) from OtherExplistDetail where md_maid=? and nvl(md_price,0)=0",
				String.class, ma_id);
		if (dets != null) {
			BaseUtil.showError("单价为0,不能提交！行号" + dets);
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { ma_id });
		// 执行审核操作
		baseDao.audit("OtherExplist", "ma_id=" + ma_id, "ma_status", "ma_statuscode", "ma_auditdate", "ma_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "ma_id", ma_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { ma_id });
	}

	@Override
	public void resAuditOtherExplist(int ma_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("OtherExplist", new String[] { "ma_statuscode", "ma_code" }, "ma_id=" + ma_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(pi_inoutno) from prodinout where PI_SOURCECODE=? and pi_code='加工委外单'", String.class, status[1]);
		if (dets != null) {
			BaseUtil.showError("已转加工验收单[" + dets + "]不允许反审核!");
		}
		// 执行反审核操作
		baseDao.resAudit("OtherExplist", "ma_id=" + ma_id, "ma_status", "ma_statuscode", "ma_auditdate", "ma_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "ma_id", ma_id);
	}

	@Override
	public void submitOtherExplist(int ma_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("OtherExplist", "ma_statuscode", "ma_id=" + ma_id);
		StateAssert.submitOnlyEntering(status);
		updateDatas(ma_id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { ma_id });
		// 执行提交操作
		baseDao.submit("OtherExplist", "ma_id=" + ma_id, "ma_status", "ma_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "ma_id", ma_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { ma_id });
	}

	@Override
	public void resSubmitOtherExplist(int ma_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("OtherExplist", "ma_statuscode", "ma_id=" + ma_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { ma_id });
		// 执行反提交操作
		baseDao.resOperate("OtherExplist", "ma_id=" + ma_id, "ma_status", "ma_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "ma_id", ma_id);
		handlerService.afterResSubmit(caller, new Object[] { ma_id });
	}

	private void updateDatas(Object ma_id) {
		SqlRowList rs = baseDao.queryForRowSet("select ma_vendcode,ma_currency,ma_code from OTHEREXPLIST where ma_id=?", ma_id);
		if (rs.next()) {
			baseDao.execute("update OtherExplistDetail set md_code=? where md_maid=" + ma_id
					+ " and not exists (select 1 from OTHEREXPLIST where md_code=ma_code)", rs.getString("ma_code"));
			baseDao.execute(
					"update OtherExplistDetail set md_taxrate=(select max(ppd_rate) from OtherExplistDetailDet,purchasepricedetail where oed_mdid=md_id and oed_prodcode=ppd_prodcode and ppd_vendcode=?) where md_maid=? and nvl(md_taxrate,0)=0",
					rs.getString("ma_vendcode"), ma_id);
			baseDao.execute("update OTHEREXPLIST set MA_COP=(select max(PR_COP) from OTHEREXPLISTDETAIL,PRODUCT where MD_MAID=MA_ID and MD_PRODCODE=PR_CODE) where nvl(MA_COP,' ')=' ' and ma_id="
					+ ma_id);
			// 如果单价为空更新为取对应核价单中供应商+币别+物料对应的有效单价
			SqlRowList rs1 = baseDao
					.queryForRowSet(
							"select md_id,md_prodcode from OtherExplistDetail where md_Maid=? and not exists(select 1 from OtherExplistDetailDet where oed_mdid=md_id) ",
							ma_id);
			while (rs1.next()) {
				SqlRowList rs2 = baseDao
						.queryForRowSet(
								"SELECT ppd_price,ppd_rate from purchaseprice,purchasePriceDetail WHERE pp_code=ppd_code and ppd_vendcode=? and ppd_currency=? and ppd_prodcode=? and pp_status='已审核' and ppd_status='有效' and pp_kind='委外' order by pp_auditdate desc,ppd_lapqty desc",
								rs.getString("ma_vendcode"), rs.getString("ma_currency"), rs1.getString("md_prodcode"));
				if (rs2.next()) {
					baseDao.execute("update OtherExplistDetail set md_price=?,md_taxrate=? where md_id=?", rs2.getDouble("ppd_price"),
							rs2.getDouble("ppd_rate"), rs1.getInt("md_id"));
				}
			}
			baseDao.execute("UPDATE OtherExplistDetail SET md_netprice=ROUND(md_price*(1-md_taxrate/(100+md_taxrate)),8) WHERE md_maid="
					+ ma_id);
			baseDao.execute("UPDATE OtherExplistDetail SET md_total=round(md_price*md_qty,2),md_nettotal=round(md_qty*md_netprice,2) WHERE md_maid="
					+ ma_id);
			baseDao.execute("UPDATE OtherExplistDetail SET md_mjcode=(select ma_mjcode from OtherExplist where ma_id=md_maid) WHERE md_maid="
					+ ma_id + " and nvl(md_mjcode,' ')=' '");
			baseDao.execute("update OtherExplist set ma_total=round(nvl((select sum(md_total) from OtherExplistDetail where md_maid=ma_id),0),2) where ma_id="
					+ ma_id);
		}
	}

	@Override
	public void endOtherExplist(int ma_id, String caller) {
		// 只能对状态为[已审核]的订单进行结案操作!
		Object status = baseDao.getFieldDataByCondition("OtherExplist", "ma_statuscode", "ma_id=" + ma_id);
		StateAssert.end_onlyAudited(status);
		baseDao.execute("update OtherExplist set ma_status='" + BaseUtil.getLocalMessage("FINISH")
				+ "', ma_statuscode='FINISH' where ma_id=" + ma_id);
		// 记录操作
		baseDao.logger.end(caller, "ma_id", ma_id);
	}

	@Override
	public void resEndOtherExplist(int ma_id, String caller) {
		// 只能对状态为[已结案]的订单进行反结案操作!
		Object status = baseDao.getFieldDataByCondition("OtherExplist", "ma_statuscode", "ma_id=" + ma_id);
		if (!status.equals("FINISH")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resEnd_onlyEnd"));
		}
		baseDao.execute("update OtherExplist set ma_status='" + BaseUtil.getLocalMessage("AUDITED")
				+ "', ma_statuscode='AUDITED' where ma_id=" + ma_id);
		baseDao.logger.resEnd(caller, "ma_id", ma_id);
	}

	@Override
	public void updateOtherExplistDetail(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "OtherExplistDetailDet", "oed_id"));
		Object md_id = store.get("md_id");
		Object[] obs = baseDao.getFieldsDataByCondition("OtherExplist left join OtherExplistDetail on md_maid=ma_id", new String[] {
				"ma_vendcode", "ma_currency", "ma_id" }, "md_id=" + md_id);
		if (obs == null || "".equals(obs)) {
			BaseUtil.showError("加工委外单不存在或者已删除！");
		}
		baseDao.execute("update OtherExplistDetailDet set oed_maid=? where oed_mdid=?", obs[2], md_id);
		// 如果有输入线长、高度则数量=线长*高度
		baseDao.execute("UPDATE OtherExplistDetailDet SET oed_qty=round(oed_length*oed_height,2) where oed_mdid='" + md_id
				+ "' and nvl(oed_length,0)>0 and nvl(oed_height,0)>0 ");
		// 取单价
		SqlRowList rs = baseDao.queryForRowSet("SELECT * from OtherExplistDetailDet WHERE oed_mdid=?", store.get("md_id"));
		while (rs.next()) {
			double currencyPrice = 0;
			double currencyRate = 0;
			// 供应商定价
			SqlRowList rs1 = baseDao
					.queryForRowSet(
							"SELECT ppd_price,ppd_rate from purchaseprice,purchasePriceDetail WHERE pp_code=ppd_code and ppd_vendcode=? and ppd_currency=? and ppd_prodcode=? and pp_status='已审核' and ppd_status='有效' order by pp_auditdate desc,ppd_lapqty desc ",
							obs[0], obs[1], rs.getString("oed_prodcode"));
			if (rs1.next()) {
				currencyPrice = rs1.getDouble("ppd_price");
				currencyRate = rs1.getDouble("ppd_rate");
			}
			if (currencyPrice > 0) {
				baseDao.execute("UPDATE OtherExplistDetailDet SET oed_price=" + currencyPrice + ",oed_remark2='' where oed_id=?",
						rs.getInt("oed_id"));
			}
			baseDao.execute("UPDATE OtherExplistDetailDet SET oed_total=round(nvl(oed_price,0)*oed_qty,2) where oed_id=?",
					rs.getInt("oed_id"));
		}
		// 最低价处理
		rs = baseDao
				.queryForRowSet("select OtherExplistDetailDet.*,pr_standardprice from OtherExplistDetailDet,product where oed_prodcode=pr_code and nvl(pr_standardprice,0)>0 and nvl(oed_total,0)<nvl(pr_standardprice,0) and oed_mdid='"
						+ store.get("md_id") + "'");
		while (rs.next()) {
			baseDao.execute("UPDATE OtherExplistDetailDet SET oed_total=?,oed_price=round(?/oed_qty,5),oed_remark2='基准价' where oed_id=?",
					rs.getDouble("pr_standardprice"), rs.getDouble("pr_standardprice"), rs.getInt("oed_id"));
		}
		// 计算OtherExplistDetail中的md_price
		baseDao.execute(
				"update OtherExplistDetail set md_price=round(nvl((select sum(nvl(oed_price,0)*oed_qty) from OtherExplistDetailDet where oed_mdid=?),0)/nvl(md_qty,1),4) where md_id=?",
				md_id, md_id);
		baseDao.execute("update OtherExplistDetail set md_total=round(nvl(md_price,0)*md_qty,2) where md_id=?", md_id);
	}

	@Override
	public void updateOtherExplistInfo(int ma_id, String vecode, String currency, String param, String caller) {
		List<String> sqls = new ArrayList<String>();
		if (baseDao.checkIf("OtherExplistDetailDet", "oed_maid=" + ma_id)) {
			SqlRowList rs = baseDao.queryForRowSet("SELECT * from OtherExplistDetailDet WHERE oed_maid=?", ma_id);
			StringBuffer err = new StringBuffer();
			while (rs.next()) {
				double currencyPrice = 0;
				// 供应商定价
				SqlRowList rs1 = baseDao
						.queryForRowSet(
								"SELECT ppd_price,ppd_rate from purchaseprice,purchasePriceDetail"
										+ " WHERE pp_code=ppd_code and ppd_vendcode=? and ppd_currency=? and ppd_prodcode=? and pp_status='已审核' and ppd_status='有效' "
										+ "order by pp_auditdate desc,ppd_lapqty desc ", vecode, currency, rs.getString("oed_prodcode"));
				if (rs1.next()) {
					currencyPrice = rs1.getDouble("ppd_price");
					if (currencyPrice > 0) {
						sqls.add("UPDATE OtherExplistDetailDet SET oed_price=" + currencyPrice + ",oed_remark2='' where oed_id="
								+ rs.getInt("oed_id"));
						sqls.add("UPDATE OtherExplistDetailDet SET oed_total=round(nvl(oed_price,0)*oed_qty,2) where oed_id="
								+ rs.getInt("oed_id"));
					}
				} else {
					err.append("物料：" + rs.getString("oed_prodcode") + "<br>");
				}
			}
			if (err.length() > 0) {
				BaseUtil.showError("供应商：" + vecode + " ,币别 :" + currency + "在核价单中没有有效价格：<hr>" + err.toString());
			}
			sqls.add("update OtherExplistDetail set md_price=round(nvl((select sum(nvl(oed_price,0)*oed_qty) from OtherExplistDetailDet where oed_mdid=md_id),0)/nvl(md_qty,1),4) where md_maid="
					+ ma_id);
		} else {
			List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
			for (Map<Object, Object> map : gstore) {
				sqls.add("update OtherExplistDetail set md_price=" + map.get("md_price") + ",md_taxrate=" + map.get("md_taxrate")
						+ " where md_id=" + map.get("md_id"));
			}
		}
		baseDao.execute(sqls);
		baseDao.execute("update OtherExplist set (ma_vendcode,ma_vendname)=(select ve_code,ve_name from vendor where ve_code='" + vecode
				+ "') where ma_id=" + ma_id);
		baseDao.execute("UPDATE OtherExplistDetail SET md_netprice=ROUND(md_price*(1-md_taxrate/(100+md_taxrate)),8) WHERE md_maid="
				+ ma_id);
		baseDao.execute("UPDATE OtherExplistDetail SET md_total=round(md_price*md_qty,2),md_nettotal=round(md_qty*md_netprice,2) WHERE md_maid="
				+ ma_id);
	}

}
