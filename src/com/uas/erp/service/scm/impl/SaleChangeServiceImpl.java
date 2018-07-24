package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.SaleChangeDao;
import com.uas.erp.dao.common.SaleDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.scm.SaleChangeService;
import com.uas.erp.service.scm.SaleClashService;
import com.uas.erp.service.scm.SaleDetailDetService;

@Service("saleChangeService")
public class SaleChangeServiceImpl implements SaleChangeService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private SaleChangeDao saleChangeDao;
	@Autowired
	private SaleDao saleDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private SaleDetailDetService saleDetailDetService;
	@Autowired
	private SaleClashService saleClashService;

	@Override
	public void saveSaleChange(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("SaleChange", "sc_code='" + store.get("sc_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		String codes = CollectionUtil.pluckSqlString(grid, "scd_sacode");
		if (codes.length() > 0) {
			String err = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select WM_CONCAT('变更单号:'||sc_code||'订单号:'||scd_sacode) from (select  distinct sc_code,scd_sacode from  SaleChangeDetail left join SaleChange on sc_id=scd_scid where scd_sacode in ("
									+ codes + ") and sc_statuscode<>'AUDITED' AND sc_id <>" + store.get("sc_id") + ")", String.class);
			if (err != null) {
				BaseUtil.showError("当前订单存在待审批的销售变更单，不允许进行当前操作!" + err);
			}
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, grid });
		// 保存SaleChange
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "SaleChange", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存SaleChangeDetail
		for (Map<Object, Object> m : grid) {
			m.put("scd_id", baseDao.getSeqId("SALECHANGEDETAIL_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "SaleChangeDetail");
		baseDao.execute(gridSql);
		// 更新销售变更单PMC日期
		baseDao.updateByCondition(
				"salechangedetail",
				"scd_pmcdate=(select sd_pmcdate from saledetail left join sale  on sd_said=sa_id where scd_sacode=sa_code and scd_sddetno=sd_detno)",
				"scd_scid='" + store.get("sc_id") + "' and scd_sacode is not null");
		baseDao.logger.save(caller, "sc_id", store.get("sc_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, grid });
	}

	@Override
	public void deleteSaleChange(String caller, int sc_id) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("SaleChange", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { sc_id });
		// 删除SaleChange
		baseDao.deleteById("SaleChange", "sc_id", sc_id);
		// 删除SaleChangeDetail
		baseDao.deleteById("salechangedetail", "scd_scid", sc_id);
		// 记录操作
		baseDao.logger.delete(caller, "sc_id", sc_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { sc_id });
	}

	@Override
	public void updateSaleChangeById(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("SaleChange", "sc_statuscode", "sc_id=" + store.get("sc_id"));
		StateAssert.updateOnlyEntering(status);
		String codes = CollectionUtil.pluckSqlString(gstore, "scd_sacode");
		if (codes.length() > 0) {
			String err = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select WM_CONCAT('变更单号:'||sc_code||'订单号:'||scd_sacode) from (select  distinct sc_code,scd_sacode from  SaleChangeDetail left join SaleChange on sc_id=scd_scid where scd_sacode in ("
									+ codes + ") and sc_statuscode<>'AUDITED' AND sc_id <>" + store.get("sc_id") + ")", String.class);
			if (err != null) {
				BaseUtil.showError("当前订单存在待审批的销售变更单，不允许进行当前操作!" + err);
			}
		}
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, store, gstore);
		// 修改SaleChange
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "SaleChange", "sc_id"));
		// 修改SaleChangeDetail
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "SaleChangeDetail", "scd_id"));
		// 记录操作
		baseDao.logger.update(caller, "sc_id", store.get("sc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, store, gstore);
	}

	// @Override
	// public void auditSaleChange(int sc_id, String caller) {
	// // 只能对状态为[已提交]的订单进行审核操作!
	// Object status = baseDao.getFieldDataByCondition("SaleChange",
	// "sc_statuscode", "sc_id=" + sc_id);
	// StateAssert.auditOnlyCommited(status);
	// try {
	// baseDao.procedure("SCM_SALE_CHANGE_AUDIT", new Object[] { sc_id,
	// SystemSession.getUser().getEm_name() });
	// } catch (Exception ex) {
	// BaseUtil.showError(ex.getCause().getMessage());
	// }
	// StringBuffer sb = new StringBuffer();
	// SqlRowList rs = baseDao.queryForRowSet(
	// "select distinct scd_sacode,sa_source from SaleChangeDetail,sale where scd_scid=? and scd_sacode=sa_code",
	// sc_id);
	// while (rs.next()) {
	// String sacode = rs.getString(1);
	// String cal = "Sale";
	// if ("非正常".equals(rs.getGeneralString(2))) {
	// cal = "Sale!Abnormal";
	// }
	// sb.append("<a href=\"javascript:openUrl('jsps/scm/sale/sale.jsp?formCondition=sa_codeIS"
	// + sacode + "&gridCondition=sd_codeIS"
	// + sacode + "&whoami=" + cal + "')\">单号:" + sacode +
	// ",点击查看</a>&nbsp;<br>");
	// }
	// if (sb.length() > 0) {
	// BaseUtil.showErrorOnSuccess("信息已自动反馈到销售单&nbsp;&nbsp;<br>" +
	// sb.toString());
	// }
	// }

	public void auditSaleChange(int sc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("SaleChange", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.auditOnlyCommited(status);
		// 判断新数量是否小于订单已发数量，小于订单已发数量，提示不能审核
		String qtyErr = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat('<br>行:'||scd_detno||',发货数:'||sd_sendqty||',新数量:'||scd_newqty||',已转数量:'||scd_yqty) from SaleChangeDetail left join saledetail on sd_code=scd_sacode and sd_detno=scd_sddetno where scd_scid=? and (scd_newqty<nvl(sd_sendqty,0) or scd_newqty<nvl(sd_yqty,0)) and nvl(scd_qty,0)<>NVL(scd_newqty,0) and rownum<=50",
						String.class, sc_id);
		if (qtyErr != null) {
			BaseUtil.showError("新数量小于订单已发数量或者已转数量:" + qtyErr);
		}
		qtyErr = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(scd_detno) from SaleChangeDetail where scd_scid=? and trunc(scd_delivery) <> trunc(scd_newdelivery) and trunc(scd_newdelivery)<trunc(sysdate)",
						String.class, sc_id);
		if (qtyErr != null) {
			BaseUtil.showError("新交货日期小于系统当前日期，不允许提交！行号：" + qtyErr);
		}
		String ordercode = "";
		double newqty = 0;
		int sddetno = 0;
		Double newprice;
		Double price;
		Double newtaxrate;
		Double taxrate;
		String prodcode = "";
		String newprodcode = "";
		String scd_newdelivery = "";
		String scd_newpmcdate = "";
		String scd_pmcdate = "";
		String prodcustcode = "";
		String newprodcustcode = "";
		double qty = 0;
		String newCurrency = null;
		double patent = 0;
		double newpatent = 0;
		double commission = 0;
		double newcommission = 0;
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('订单号:'||scd_sacode||'订单行号:'||scd_sddetno) from SaleChangeDetail where scd_scid=? and not exists (select sd_code,sd_detno from saledetail where sd_code=scd_sacode and sd_detno=scd_sddetno and sd_statuscode='AUDITED')  and rownum<=50",
						String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("订单号+订单序号状态不等于已审核或者不存在，不允许进变更!" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(scd_detno) from SaleChangeDetail where  scd_scid=? and not exists (select sd_code,sd_detno,sd_prodcode from saledetail where sd_code=scd_sacode and sd_detno=scd_sddetno and sd_prodcode=scd_prodcode)  and rownum<=50",
						String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("订单号+订单序号+物料编号在销售订单中不存在，不允许进变更!行号" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('行号:'||scd_detno||',订单号:'||scd_sacode||',订单行号:'||scd_sddetno||',通知单号:'||snd_code||',通知单行号:'||snd_pdno) from (select scd_detno,scd_sddetno,snd_code,snd_pdno,scd_sacode from SaleChangeDetail left join SendNotifyDetail on scd_sacode=snd_ordercode and scd_sddetno=snd_orderdetno where nvl(snd_yqty,0)>0 and nvl(snd_outqty,0)> nvl(snd_yqty,0) and scd_prodcode<>scd_newprodcode and scd_scid=?  and rownum<=50)",
						String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("订单号+订单行号所在的通知单未全部出货，不允许进行料号变更!" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('行号:'||scd_detno||',订单号:'||scd_sacode||',订单行号:'||scd_sddetno||',出入库号:'||pd_inoutno||',出入库行号:'||pd_pdno||',出入库类型:'||pd_piclass) from (select scd_detno,scd_sacode,scd_sddetno,pd_inoutno,pd_pdno,pd_piclass from SaleChangeDetail left join Prodiodetail on scd_sacode=pd_ordercode and scd_sddetno=pd_orderdetno where pd_status<>99 and scd_prodcode<>scd_newprodcode and scd_scid=?  and rownum<=50)",
						String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("订单号+订单行号所在的出入库单未全部过账，不允许进行料号变更!" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('订单号:'||scd_sacode||',订单行号:'||scd_sddetno||',退货单号:'||pd_inoutno||',退货单行号:'||pd_pdno) from (select scd_sacode,scd_sddetno,pd_inoutno,pd_pdno from SaleChangeDetail left join Prodiodetail on scd_sacode=pd_ordercode and scd_sddetno=pd_orderdetno where pd_status<>99 and pd_piclass='销售退货单' and scd_scid=?  and rownum<=50)",
						String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("订单号+订单行号所在的销售退货单未全部过账，不允许进行变更!" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(scd_detno) from SaleChangeDetail left join SaleDetail on scd_sacode=sd_code and scd_sddetno=sd_detno where scd_scid=? and nvl(scd_newqty,0)<>nvl(scd_qty,0) and (nvl(scd_newqty,0)<nvl(sd_sendqty,0) or nvl(scd_newqty,0)<nvl(sd_yqty,0))",
						String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("新数量小于销售单明细已出货数量或者已转数量，不允许进行变更!行号：" + dets);
		}
		List<Object[]> sa = baseDao.getFieldsDatasByCondition("SaleChangeDetail", new String[] { "scd_sacode", "scd_sddetno",
				"scd_newprodcode", "scd_prodcode" }, "scd_scid=" + sc_id);
		for (Object[] c : sa) {
			int count;
			// 有已转发货通知或发货单的不允许变更料号
			if (c[2] != null && !c[2].equals("") && c[3] != null && !c[2].equals(c[3])) {
				count = baseDao.getCountByCondition("prodiodetail", "pd_ordercode='" + c[0] + "' and pd_orderdetno='" + c[1]
						+ "' and pd_piclass='出货单' ");
				if (count > 1) {
					BaseUtil.showError("订单号单[" + c[0] + "],序号[" + c[1] + "]已经存在发货单,不能变更料号");
				}
				count = baseDao.getCountByCondition("sendnotifydetail", "snd_ordercode='" + c[0] + "' and snd_orderdetno='" + c[1] + "'  ");
				if (count > 1) {
					BaseUtil.showError("订单号单[" + c[0] + "],序号[" + c[1] + "]已经有发货通知单,不能变更料号");
				}
			}
		}
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { sc_id });
		// 信息自动反馈到销售单
		List<String> sa_codes = new ArrayList<String>();
		// 更新订单对应的数据
		try {
			int argCount = 0;
			sa_codes = saleChangeDao.catchSale(sc_id);// ?
			SqlRowList rs = baseDao.queryForRowSet(
					"select abs(nvl(sc_withprodio,0)),abs(nvl(sc_withnotify,0)),sc_type from SaleChange where sc_id=?", sc_id);
			if (rs.next()) {
				boolean withProdio = 1 == rs.getGeneralInt(1);
				boolean withNotify = 1 == rs.getGeneralInt(2);
				String type = rs.getString("sc_type");
				SqlRowList uprs = baseDao.queryForRowSet("select * from SaleChangeDetail where scd_scid=?", sc_id);
				List<String> sqls = new ArrayList<String>();
				while (uprs.next()) {
					sddetno = uprs.getInt("scd_sddetno");
					ordercode = uprs.getString("scd_sacode").toString();
					newprodcode = uprs.getString("scd_newprodcode");
					newprice = uprs.getDouble("scd_newprice");
					price = uprs.getDouble("scd_price");
					newtaxrate = uprs.getGeneralDouble("scd_newtaxrate");
					taxrate = uprs.getGeneralDouble("scd_taxrate");
					prodcode = uprs.getString("scd_prodcode");
					qty = uprs.getDouble("scd_qty");
					newqty = uprs.getDouble("scd_newqty");
					newCurrency = uprs.getString("scd_newcurrency");
					scd_newdelivery = uprs.getString("scd_newdelivery");
					scd_newpmcdate = uprs.getString("scd_newpmcdate");
					scd_pmcdate = uprs.getString("scd_pmcdate");
					patent = uprs.getDouble("scd_patent");
					newpatent = uprs.getDouble("scd_newpatent");
					commission = uprs.getDouble("scd_commission");
					newcommission = uprs.getDouble("scd_newcommission");
					prodcustcode = uprs.getString("scd_prodcustcode");
					newprodcustcode = uprs.getString("scd_newprodcustcode");
					if (newprodcode != null && !"".equals(newprodcode) && !newprodcode.equals(prodcode)) {
						// 更新订单物料料号
						sqls.add("update SaleDetail set sd_prodcode='" + newprodcode + "' where sd_code='" + ordercode + "' and sd_detno="
								+ sddetno);
					}
					// 当原、新客户料号均不为空且不相等时更新订单客户料号
					newprodcustcode = newprodcustcode == null ? "" : newprodcustcode;
					prodcustcode = prodcustcode == null ? "" : prodcustcode;
					String newcustp = newprodcustcode + prodcustcode;
					if (newcustp != "" && !prodcustcode.equals(newprodcustcode)) {
						sqls.add("update SaleDetail set sd_prodcustcode='" + newprodcustcode + "' where sd_code='" + ordercode
								+ "' and sd_detno=" + sddetno);
					}
					if (newCurrency != null && !"".equals(newCurrency)) {// 有可能改汇率
						// 更新币别
						Double rate = baseDao.getJdbcTemplate().queryForObject("select cr_rate from currencys where cr_name=?",
								Double.class, newCurrency);
						sqls.add("update Sale set sa_currency='" + newCurrency + "',sa_rate=" + rate + " where sa_code='" + ordercode + "'");
						if (withProdio) {
							sqls.add("update ProdInOut set pi_currency='" + newCurrency + "',pi_rate=" + rate
									+ " where pi_id in(select pd_piid from ProdIoDetail where pd_ordercode='" + ordercode
									+ "' and pd_orderdetno=" + sddetno + " and pd_piclass in ('出货单','销售退货单') and pd_status=0)");
						}
						if (withNotify) {
							sqls.add("update SendNotify set sn_currency='" + newCurrency + "',sn_rate=" + rate
									+ " where sn_id in (select snd_snid from SendNotifyDetail where snd_ordercode='" + ordercode
									+ "' and snd_orderdetno=" + sddetno + ")");
						}
					}
					if (NumberUtil.compare(newprice, price) != 0) {
						// 更新订单单价
						sqls.add("update SaleDetail set sd_price=" + newprice + ",sd_total=round(" + newprice
								+ "*sd_qty,2) where sd_code='" + ordercode + "' and sd_detno=" + sddetno);
						if (withProdio) {
							sqls.add("update ProdIoDetail set pd_sendprice=" + newprice + ",pd_taxtotal=round(" + newprice
									+ "*pd_outqty,2) where pd_ordercode='" + ordercode + "' and pd_orderdetno=" + sddetno
									+ " and pd_piclass in ('出货单','销售退货单') and pd_status=0");
							sqls.add("update ProdIoDetail set pd_netprice=round(pd_sendprice/(1+pd_taxrate/100),8) where pd_ordercode='"
									+ ordercode + "' and pd_orderdetno=" + sddetno + " and pd_piclass in ('出货单','销售退货单') and pd_status=0");
							sqls.add("update ProdIoDetail set pd_nettotal=round(pd_netprice*pd_outqty,2) where pd_ordercode='" + ordercode
									+ "' and pd_orderdetno=" + sddetno + " and pd_piclass in ('出货单','销售退货单') and pd_status=0");
						}
						if (withNotify) {
							sqls.add("update SendNotifyDetail set snd_sendprice=" + newprice + ",snd_total=round(" + newprice
									+ "*snd_outqty,2) where snd_ordercode='" + ordercode + "' and snd_orderdetno=" + sddetno);
							sqls.add("update SendNotifyDetail set snd_netprice=round(snd_sendprice/(1+snd_taxrate/100),8) where snd_ordercode='"
									+ ordercode + "' and snd_orderdetno=" + sddetno);
							sqls.add("update SendNotifyDetail set snd_taxtotal=round(snd_netprice*snd_outqty,2) where snd_ordercode='"
									+ ordercode + "' and snd_orderdetno=" + sddetno);
						}
					}
					if (NumberUtil.compare(newtaxrate, taxrate) != 0) {
						// 更新订单税率
						sqls.add("update SaleDetail set sd_taxrate=" + newtaxrate + " where sd_code='" + ordercode + "' and sd_detno="
								+ sddetno);
						sqls.add("update SaleDetail set sd_costprice=round(sd_price/(1+sd_taxrate/100),8) " + "where sd_code='" + ordercode
								+ "' and sd_detno=" + sddetno);
						sqls.add("update SaleDetail set sd_taxtotal=round(sd_costprice*sd_qty,2) " + "where sd_code='" + ordercode
								+ "' and sd_detno=" + sddetno);
						if (withProdio) {
							sqls.add("update ProdIoDetail set pd_taxrate=" + newtaxrate + " where pd_ordercode='" + ordercode
									+ "' and pd_orderdetno=" + sddetno + " and pd_piclass in ('出货单','销售退货单') and pd_status=0");
							sqls.add("update ProdIoDetail set pd_netprice=round(pd_sendprice/(1+pd_taxrate/100),8) where pd_ordercode='"
									+ ordercode + "' and pd_orderdetno=" + sddetno + " and pd_piclass in ('出货单','销售退货单') and pd_status=0");
							sqls.add("update ProdIoDetail set pd_nettotal=round(pd_netprice*pd_outqty,2),pd_ordertotal=round(pd_sendprice*pd_outqty,2) where pd_ordercode='"
									+ ordercode + "' and pd_orderdetno=" + sddetno + " and pd_piclass in ('出货单','销售退货单') and pd_status=0");
						}
						if (withNotify) {
							sqls.add("update SendNotifyDetail set snd_taxrate=" + newtaxrate + " where snd_ordercode='" + ordercode
									+ "' and snd_orderdetno=" + sddetno);
							sqls.add("update SendNotifyDetail set snd_netprice=round(snd_sendprice/(1+snd_taxrate/100),8) where snd_ordercode='"
									+ ordercode + "' and snd_orderdetno=" + sddetno);
							sqls.add("update SendNotifyDetail set snd_taxtotal=round(snd_netprice*snd_outqty,2) where snd_ordercode='"
									+ ordercode + "' and snd_orderdetno=" + sddetno);
						}
					}
					if (newqty != qty) {
						// 更新订单数量
						sqls.add("update SaleDetail set sd_qty='" + newqty + "',sd_total=round(" + newqty + "*sd_price,2) where sd_code='"
								+ ordercode + "' and sd_detno=" + sddetno);
					}
					if (NumberUtil.compare(newprice, price) != 0 || NumberUtil.compare(newtaxrate, taxrate) != 0 || newqty != qty) {
						sqls.add("update SaleDetail set sd_costprice=round(sd_price/(1+sd_taxrate/100),8) " + "where sd_code='" + ordercode
								+ "' and sd_detno=" + sddetno);
						sqls.add("update SaleDetail set sd_taxtotal=round(sd_costprice*sd_qty,2) " + "where sd_code='" + ordercode
								+ "' and sd_detno=" + sddetno);
						if (withProdio) {
							sqls.add("update ProdIoDetail set pd_netprice=round(pd_sendprice/(1+pd_taxrate/100),8) where pd_ordercode='"
									+ ordercode + "' and pd_orderdetno=" + sddetno + " and pd_piclass in ('出货单','销售退货单') and pd_status=0");
							sqls.add("update ProdIoDetail set pd_nettotal=round(pd_netprice*pd_outqty,2),pd_ordertotal=round(pd_sendprice*pd_outqty,2) where pd_ordercode='"
									+ ordercode + "' and pd_orderdetno=" + sddetno + " and pd_piclass in ('出货单','销售退货单') and pd_status=0");
						}
						if (withNotify) {
							sqls.add("update SendNotifyDetail set snd_netprice=round(snd_sendprice/(1+snd_taxrate/100),8) where snd_ordercode='"
									+ ordercode + "' and snd_orderdetno=" + sddetno);
							sqls.add("update SendNotifyDetail set snd_taxtotal=round(snd_netprice*snd_outqty,2) where snd_ordercode='"
									+ ordercode + "' and snd_orderdetno=" + sddetno);
						}
					}
					if (!"PRODUCT".equals(type)) {
						if (scd_newdelivery != null && !"".equals(scd_newdelivery)) {
							sqls.add("update SaleDetail set sd_delivery=to_date('" + scd_newdelivery.substring(0, 10)
									+ "','yyyy-MM-dd') where sd_code='" + ordercode + "' and sd_detno=" + sddetno);
						}
						if (scd_newpmcdate != null && !"".equals(scd_newpmcdate) && !"null".equals(scd_newpmcdate)
								&& !scd_newpmcdate.equals(scd_pmcdate)) {
							sqls.add("update SaleDetail set sd_pmcdate=to_date('" + scd_newpmcdate.substring(0, 10)
									+ "','yyyy-MM-dd') where sd_code='" + ordercode + "' and sd_detno=" + sddetno);
						}
					}
					if (newpatent != patent) {
						// 更新专利
						sqls.add("update SaleDetail set sd_patent=" + newpatent + " where sd_code='" + ordercode + "' and sd_detno="
								+ sddetno);
					}
					if (newcommission != commission) {
						// 更新佣金
						sqls.add("update SaleDetail set sd_commission=" + newcommission + " where sd_code='" + ordercode
								+ "' and sd_detno=" + sddetno);
					}
					argCount = baseDao.getCountByCondition("user_tab_columns",
							"table_name='SALECHANGEDETAIL' and column_name in ('SCD_BONDED','SCD_NEWBONDED')");
					if (argCount == 2) {
						if (uprs.getInt("scd_bonded") != uprs.getInt("scd_newbonded")) {
							// 更新是否保税
							sqls.add("update SaleDetail set sd_bonded=" + uprs.getInt("scd_newbonded") + " where sd_code='" + ordercode
									+ "' and sd_detno=" + sddetno);
						}
					}
					argCount = baseDao.getCountByCondition("user_tab_columns",
							"table_name='SALECHANGEDETAIL' and column_name in ('SCD_NEWPAYMENTS','SCD_PAYMENTS')");
					if (argCount == 2) {
						if (uprs.getObject("scd_payments") != uprs.getObject("scd_newpayments")) {
							// 更新收款方式
							sqls.add("update Sale set sa_paymentscode='" + uprs.getObject("scd_newpayments") + "' where sa_code='"
									+ ordercode + "'");
							sqls.add("update Sale set (sa_paymentsid,sa_payments)=(select pa_id ,pa_name from payments where pa_code=sa_paymentscode AND pa_class='收款方式') where sa_code='"
									+ ordercode + "'");
							if (withNotify) {
								sqls.add("update SendNotify set sn_paymentscode='" + uprs.getObject("scd_newpayments")
										+ "' where sn_id in (select snd_snid from SendNotifyDetail where snd_ordercode='" + ordercode
										+ "' and snd_orderdetno=" + sddetno + ")");
								sqls.add("update SendNotify set (sn_payments,sn_payment)=(select pa_id ,pa_name from payments where pa_code=sn_paymentscode AND pa_class='收款方式') where sn_id in (select snd_snid from SendNotifyDetail where snd_ordercode='"
										+ ordercode + "' and snd_orderdetno=" + sddetno + ")");
							}
							if (withProdio) {
								sqls.add("update ProdInOut set pi_paymentcode='" + uprs.getObject("scd_newpayments")
										+ "' where pi_id in(select pd_piid from ProdIoDetail where pd_ordercode='" + ordercode
										+ "' and pd_orderdetno=" + sddetno + " and pd_piclass in ('出货单','销售退货单') and pd_status=0)");
								sqls.add("update ProdInOut set pi_payment=(select pa_name from payments where pa_code=pi_paymentcode AND pa_class='收款方式') where pi_id in(select pd_piid from ProdIoDetail where pd_ordercode='"
										+ ordercode
										+ "' and pd_orderdetno="
										+ sddetno
										+ " and pd_piclass in ('出货单','销售退货单') and pd_status=0)");
							}
						}
					}
					argCount = baseDao
							.getCountByCondition("user_tab_columns",
									"table_name='SALECHANGEDETAIL' and column_name in ('SCD_SELLERCODE','SCD_NEWSELLERCODE', 'SCD_SELLERNAME','SCD_NEWSELLERNAME')");
					if (argCount == 4) {
						if (uprs.getObject("scd_sellercode") != null && uprs.getObject("scd_newsellercode") != null) {
							if (!uprs.getObject("scd_sellercode").equals(uprs.getObject("scd_newsellercode"))) {
								// 更新业务员
								sqls.add("update Sale set sa_sellercode='" + uprs.getObject("scd_newsellercode") + "', sa_seller='"
										+ uprs.getObject("scd_newsellername") + "' where sa_code='" + ordercode + "'");
								/**
								 * 反馈编号：2017050706
								 * 销售订单变更时，如果勾选通知单、出货单，支持变更业务员，当出货单未过账时
								 * ，更新出货单，且更新对应的通知单，出货单已过账，不更新，且也不更新通知单；
								 * 反馈编号：2017080049 通知单未转出货单，销售变更单也能变更通知单；
								 * 
								 * @author wsy
								 */
								String sql = "select * from prodinout where pi_id in(select pd_piid from ProdIoDetail where pd_ordercode='"
										+ ordercode + "' and pd_orderdetno=" + sddetno
										+ " and pd_piclass in ('出货单','销售退货单') and pd_status=0)";
								Object piid = baseDao.getFieldDataByCondition("ProdIoDetail", "pd_piid", "pd_ordercode='" + ordercode
										+ "' and pd_orderdetno=" + sddetno + "");
								int count = baseDao.getCount(sql);
								if (count > 0 || piid == null) {
									if (withNotify) {
										sqls.add("update SendNotify set sn_sellercode='" + uprs.getObject("scd_newsellercode")
												+ "',sn_sellername='" + uprs.getObject("scd_newsellername")
												+ "' where sn_id in (select snd_snid from SendNotifyDetail where snd_ordercode='"
												+ ordercode + "' and snd_orderdetno=" + sddetno + ")");
									}
									if (withProdio) {
										sqls.add("update ProdInOut set pi_sellercode='" + uprs.getObject("scd_newsellercode")
												+ "',pi_sellername='" + uprs.getObject("scd_newsellername")
												+ "' where pi_id in(select pd_piid from ProdIoDetail where pd_ordercode='" + ordercode
												+ "' and pd_orderdetno=" + sddetno + " and pd_piclass in ('出货单','销售退货单') and pd_status=0)");
									}
								}
							}
						}
					}
					argCount = baseDao.getCountByCondition("user_tab_columns",
							"table_name='SALECHANGEDETAIL' and column_name in ('SCD_NEWPOCODE','SCD_POCODE')");
					if (argCount == 2) {
						if (uprs.getObject("scd_newpocode") != null) {
							if (!uprs.getGeneralString("scd_pocode").equals(uprs.getGeneralString("scd_newpocode"))) {
								// 更新客户PO号
								sqls.add("update Sale set sa_pocode='" + uprs.getGeneralString("scd_newpocode") + "' where sa_code='"
										+ ordercode + "'");
							}
						}
					}
					argCount = baseDao.getCountByCondition("user_tab_columns",
							"table_name='SALECHANGEDETAIL' and column_name in ('SCD_CUSTPRODCODE','SCD_NEWCUSTPRODCODE')");
					if (argCount == 2) {
						if (uprs.getObject("scd_newcustprodcode") != null) {
							if (!uprs.getGeneralString("scd_custprodcode").equals(uprs.getGeneralString("scd_newcustprodcode"))) {
								// 更新客户物料号
								sqls.add("update SaleDetail set sd_custprodcode='" + uprs.getGeneralString("scd_newcustprodcode")
										+ "',sd_custproddetail='" + uprs.getGeneralString("scd_newprodname") + "',sd_prodcustcode='"
										+ uprs.getGeneralString("scd_newprodspec") + "' where sd_code='" + ordercode + "' and sd_detno="
										+ sddetno);
							}
						}
					}
					argCount = baseDao
							.getCountByCondition("user_tab_columns",
									"table_name='SALECHANGEDETAIL' and column_name in ('SCD_NETPRICE','SCD_NEWNETPRICE', 'SCD_BGPRICE','SCD_NEWBGPRICE')");
					if (argCount == 4) {
						if (uprs.getGeneralDouble("scd_netprice") != uprs.getGeneralDouble("scd_newnetprice")) {
							// 更新对账价
							sqls.add("update SaleDetail set sd_netprice=" + uprs.getGeneralDouble("scd_newnetprice") + " where sd_code='"
									+ ordercode + "' and sd_detno=" + sddetno);
							sqls.add("update SaleDetail set sd_nettotal=round(nvl(sd_netprice,0)*nvl(sd_qty,0),2) where sd_code='"
									+ ordercode + "' and sd_detno=" + sddetno);
						}
						if (uprs.getGeneralDouble("scd_bgprice") != uprs.getGeneralDouble("scd_newbgprice")) {
							// 更新报关单价
							sqls.add("update SaleDetail set sd_bgprice=" + uprs.getGeneralDouble("scd_newbgprice") + " where sd_code='"
									+ ordercode + "' and sd_detno=" + sddetno);
						}
					}
					
					/**
					 *
					 * 
					 * @author wsy
					 */
					SqlRowList changeConfigDetail = baseDao.queryForRowSet("select CC_OLDFIELD,CC_NEWFIELD,CC_TOFIELD,CC_AFFECTEDFIELD,nvl(cc_ismain,0) from changeconfig where cc_caller='SaleChange'");
					String cc_oldfiledDetail = "";
					String cc_newfiledDetail = "";
					String cc_tofieldDetail = "";
					int cc_ismain=0;
					String[] cc_affectedfieldDetail;
					List<String[]> listDetail = new ArrayList<String[]>();
					while (changeConfigDetail.next()) {
						cc_oldfiledDetail = changeConfigDetail.getString(1);
						cc_newfiledDetail = changeConfigDetail.getString(2);
						cc_tofieldDetail = changeConfigDetail.getString(3);
						cc_ismain = changeConfigDetail.getInt(5);
						if(changeConfigDetail.getString(4)!=null && !"".equals(changeConfigDetail.getString(4))){
							cc_affectedfieldDetail = changeConfigDetail.getString(4).split(";");
							listDetail.add(cc_affectedfieldDetail);
						}
						if (cc_oldfiledDetail != null && cc_newfiledDetail != null) {
							if(cc_ismain==0){
								argCount = baseDao.getCountByCondition("user_tab_columns", "table_name='SALECHANGEDETAIL' and column_name in ('"
										+ cc_oldfiledDetail.toUpperCase() + "','" + cc_newfiledDetail.toUpperCase() + "')");
								if (argCount == 2) {
									if (uprs.getString(cc_newfiledDetail) != null
											&& !uprs.getString(cc_newfiledDetail).equals(uprs.getString(cc_oldfiledDetail))) {
										int count = baseDao.getCountByCondition("user_tab_columns", "table_name='SALEDETAIL' and column_name in ('" + cc_tofieldDetail.toUpperCase() + "')");
										if (count == 1) {
											baseDao.execute("update saledetail set " + cc_tofieldDetail + "='"
													+ uprs.getString(cc_newfiledDetail) + "' where sd_code='" + ordercode + "' AND sd_detno="
													+ sddetno);
										}
									}
								}
							}else if(cc_ismain==1){
								argCount = baseDao.getCountByCondition("user_tab_columns", "table_name='SALECHANGEDETAIL' and column_name in ('"
										+ cc_oldfiledDetail.toUpperCase() + "','" + cc_newfiledDetail.toUpperCase() + "')");
								if(argCount==2){
									if (uprs.getString(cc_newfiledDetail) != null
											&& !uprs.getString(cc_newfiledDetail).equals(uprs.getString(cc_oldfiledDetail))) {
										int count = baseDao.getCountByCondition("user_tab_columns", "table_name='SALE' and column_name in ('" + cc_tofieldDetail.toUpperCase() + "')");
										if (count == 1) {
											baseDao.execute("update sale set " + cc_tofieldDetail + "='"
													+ uprs.getString(cc_newfiledDetail) + "' where sa_code='" + ordercode + "' ");
										}
									}
								}
							}
						}
					}
					/**
					 * 层级更新   以分号分割，循环更新
					 */
					if(listDetail.size()>0){
						int length = listDetail.get(0).length;
						for(int i=0;i<length;i++){
							String sql = "";
							for(String[] s : listDetail){
								sql = sql + ("@null".equals(s[i])?"":(s[i])+",");
							}
							if(!"".equals(sql)){
								sql = sql.substring(0, sql.length()-1);
								baseDao.execute("update saledetail set "+sql+" where sd_code='"+ordercode+"' and sd_detno="+sddetno+"");
							}
						}
					}
					sqls.add("update sale set sa_updatedate=sysdate,sa_updateman='" + SystemSession.getUser().getEm_name()
							+ "' where sa_code ='" + ordercode + "'");
					sqls.add("update sale set sa_total=nvl((select sum(nvl(sd_total,0)) from saledetail where saledetail.sd_said = sale.sa_id),0) where sa_code ='"
							+ ordercode + "'");
					sqls.add("update sale set sa_totalupper=L2U(nvl(sa_total,0)) where sa_code ='" + ordercode + "'");
				}
				baseDao.execute(sqls);
				
			}
		} catch (Exception ex) {
			BaseUtil.showError(ex.toString());
			return;
		}
		// 执行审核操作
		baseDao.audit("SaleChange", "sc_id=" + sc_id, "sc_status", "sc_statuscode", "sc_auditdate", "sc_auditman");
		/**
		 * 反馈编号：2017020686
		 * 
		 * @author wsy 销售变更单审核后更新销售订单最近变更日期
		 */
		List<String> sqls = new ArrayList<String>();
		for (String sa_code : sa_codes) {
			sqls.add("update sale set sa_recentchangetime=sysdate where sa_code='" + sa_code + "'");
		}
		baseDao.execute(sqls);

		// 记录操作
		baseDao.logger.audit(caller, "sc_id", sc_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { sc_id });
		// 更新排程
		SqlRowList uprs = baseDao
				.queryForRowSet("select scd_newprodcode,scd_newqty,scd_newprice,scd_sddetno,scd_sacode from SaleChangeDetail where scd_scid="
						+ sc_id + "");
		String whereString = null;
		sddetno = 0;
		while (uprs.next()) {
			sddetno = uprs.getInt("scd_sddetno");
			ordercode = uprs.getString("scd_sacode").toString();
			whereString = "sd_code='" + ordercode + "' and sd_detno=" + sddetno;
			// 更新排程
			saleDetailDetService.SetSaleDelivery(whereString);
		}
		StringBuffer sb = new StringBuffer();
		for (String c : sa_codes) {
			String cal = null;
			Object source = baseDao.getFieldDataByCondition("sale", "sa_source", "sa_code='" + c + "'");
			saleChangeDao.updateSaleStatus(c);
			if (source != null) {
				saleDao.updateSaleTotal(c);
				// 主表转通知状态更新
				if ("非正常".equals(source)) {
					cal = "Sale!Abnormal";
				} else {
					cal = "Sale";
				}
				sb.append("<a href=\"javascript:openUrl('jsps/scm/sale/sale.jsp?formCondition=sa_codeIS" + c + "&gridCondition=sd_codeIS"
						+ c + "&whoami=" + cal + "')\">单号:" + c + ",点击查看</a>&nbsp;");
			}
		}
		// 调用冲销
		creatSaleClash(sc_id);
		if (sb.length() > 0) {
			BaseUtil.showErrorOnSuccess("信息已自动反馈到销售单&nbsp;&nbsp;<br>" + sb.toString());
		}
	}

	void creatSaleClash(int sc_id) {
		Employee employee = SystemSession.getUser();
		SqlRowList rs = baseDao.queryForRowSet("select distinct scd_sacode sacode from SaleChangeDetail where scd_scid=" + sc_id
				+ " and nvl(scd_ifrestoreforecast,0)<>0");
		while (rs.next()) {
			Object ordercode = rs.getObject("sacode");
			String clashcode = "";
			int csid = 0, detno = 1;
			SqlRowList rs1 = baseDao.queryForRowSet("select * from SaleChangeDetail where scd_scid=" + sc_id + " and scd_sacode='"
					+ ordercode + "' and nvl(scd_ifrestoreforecast,0)<>0 and nvl(scd_qty,0) > nvl(scd_newqty,0)");
			while (rs1.next()) {
				double clashedqty = 0, thisqty = 0;
				double needclashqty = rs1.getGeneralDouble("scd_qty") - rs1.getGeneralDouble("scd_newqty");
				Object sdid = baseDao.getFieldDataByCondition("saledetail", "sd_id",
						"sd_code='" + ordercode + "' and sd_detno=" + rs1.getInt("scd_sddetno"));
				SqlRowList saleclash = baseDao
						.queryForRowSet("select * from saleclashdetail left join saleclash on scd_scid=sc_id left join saleforecast on scd_ordercode=sf_code left join saleforecastdetail on sd_sfid=sf_id and scd_orderdetno=sd_detno where sc_source='销售单' and scd_fromcode='"
								+ ordercode
								+ "' and nvl(scd_clashqty,0)>0 and nvl(scd_clashqty,0)-NVL(scd_cancelqty,0)>0 and sc_statuscode='AUDITED'");
				while (saleclash.next() && clashedqty < needclashqty) {
					double remain = saleclash.getGeneralDouble("scd_clashqty") - saleclash.getGeneralDouble("scd_cancelqty");
					thisqty = remain > needclashqty - clashedqty ? needclashqty - clashedqty : remain;
					if (clashcode == "") {
						clashcode = baseDao.sGetMaxNumber("SaleClash", 2);
						csid = baseDao.getSeqId("SALECLASH_SEQ");
						baseDao.execute("insert into SaleClash(sc_id,sc_code,sc_date,sc_status,sc_statuscode,sc_recorder,sc_source,sc_sourceid,sc_sourcecode)values("
								+ csid
								+ ",'"
								+ clashcode
								+ "',sysdate,'"
								+ BaseUtil.getLocalMessage("COMMITED")
								+ "','COMMITED','"
								+ employee.getEm_name() + "','销售单'," + saleclash.getGeneralInt("sc_sourceid") + ",'" + ordercode + "')   ");
					}
					baseDao.execute("insert into SaleClashDetail(scd_id,scd_scid,scd_detno,scd_prodcode,scd_clashqty,scd_ordercode,scd_orderdetno,scd_sourcedetid,scd_fromcode,scd_cancelid)values("
							+ "SALECLASHDETAIL_SEQ.NEXTVAL,"
							+ csid
							+ ",'"
							+ detno
							+ "','"
							+ saleclash.getObject("scd_prodcode")
							+ "','"
							+ thisqty
							+ "','"
							+ saleclash.getString("sf_code")
							+ "','"
							+ saleclash.getInt("sd_detno")
							+ "',"
							+ sdid
							+ ",'"
							+ ordercode + "'," + saleclash.getInt("scd_id") + ")");
					baseDao.execute("UPDATE SaleClashDetail set scd_cancelqty=nvl(scd_cancelqty,0)+" + (thisqty) + " where scd_id="
							+ saleclash.getInt("scd_id"));
					detno = detno + 1;
					clashedqty = clashedqty + thisqty;
				}
				if (csid > 0) {
					baseDao.execute("UPDATE SaleClashDetail set scd_clashqty=0-scd_clashqty  where scd_scid=" + csid);
				}
				if (!"".equals(clashcode)) {
					// 审核冲销单
					saleClashService.auditSaleClash(csid, "SaleClash");
				}
			}
		}
	}

	@Override
	public void resAuditSaleChange(String caller, int sc_id) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("SaleChange", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("SaleChange", "sc_id=" + sc_id, "sc_status", "sc_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "sc_id", sc_id);
	}

	@Override
	public void submitSaleChange(String caller, int sc_id) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("SaleChange", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.submitOnlyEntering(status);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(scd_detno) from SaleChangeDetail where scd_scid=? and trunc(scd_delivery) <> trunc(scd_newdelivery) and trunc(scd_newdelivery)<trunc(sysdate)",
						String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("新交货日期小于系统当前日期，不允许提交！行号：" + dets);
		}
		List<Object[]> sa = baseDao.getFieldsDatasByCondition("SaleChangeDetail", new String[] { "scd_sacode", "scd_sddetno",
				"scd_newprodcode", "scd_prodcode" }, "scd_scid=" + sc_id);
		for (Object[] c : sa) {
			int count = baseDao.getCountByCondition("SaleChangeDetail left join SaleChange on scd_scid=sc_id", "scd_sacode='" + c[0]
					+ "' and sc_statuscode = 'COMMITED' and scd_sddetno=" + c[1]);
			/**
			 * 反馈编号：2017030649
			 * 
			 * @author wsy 修改count>0
			 */
			if (count > 0) {// 同一销售单只能存在一张已提交未审核的变更单
				BaseUtil.showError("订单号单[" + c[0] + "],序号[" + c[1] + "]只能存在一张已提交未审核的变更单");
			}
			// 有已转发货通知或发货单的不允许变更料号
			if (c[2] != null && !c[2].equals("") && c[3] != null && !c[2].equals(c[3])) {
				count = baseDao.getCountByCondition("prodiodetail", "pd_ordercode='" + c[0] + "' and pd_orderdetno='" + c[1]
						+ "' and pd_piclass='出货单' ");
				if (count > 0) {
					BaseUtil.showError("订单号单[" + c[0] + "],序号[" + c[1] + "]已经存在发货单,不能变更料号");
				}
				count = baseDao.getCountByCondition("sendnotifydetail", "snd_ordercode='" + c[0] + "' and snd_orderdetno='" + c[1] + "'  ");
				if (count > 0) {
					BaseUtil.showError("订单号单[" + c[0] + "],序号[" + c[1] + "]已经有发货通知单,不能变更料号");
				}
			}
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('订单号:'||scd_sacode||'订单行号:'||scd_sddetno) from SaleChangeDetail where scd_scid=?  and not exists (select sd_code,sd_detno from saledetail where sd_code=scd_sacode and sd_detno=scd_sddetno and sd_statuscode='AUDITED') and rownum<=50",
						String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("订单号+订单序号状态不等于已审核或者不存在，不允许进变更!" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(scd_detno) from SaleChangeDetail where  scd_scid=? and not exists (select sd_code,sd_detno,sd_prodcode from saledetail where sd_code=scd_sacode and sd_detno=scd_sddetno and sd_prodcode=scd_prodcode)  and rownum<=50",
						String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("订单号+订单序号+物料编号在销售订单中不存在，不允许进变更!行号" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('订单号:'||scd_sacode||',订单行号:'||scd_sddetno||',退货单号:'||pd_inoutno||',退货单行号:'||pd_pdno) from (select scd_sacode,scd_sddetno,pd_inoutno,pd_pdno from SaleChangeDetail left join Prodiodetail on scd_sacode=pd_ordercode and scd_sddetno=pd_orderdetno where pd_status<>99 and pd_piclass='销售退货单' and scd_scid=?  and rownum<=50)",
						String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("订单号+订单行号所在的销售退货单未全部过账，不允许进行变更!" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('订单号:'||scd_sacode||'订单行号:'||scd_sddetno) from SaleChangeDetail left join SaleDetail on scd_sacode=sd_code and scd_sddetno=sd_detno where (nvl(scd_newqty,0)<nvl(sd_yqty,0) or nvl(scd_newqty,0) < nvl(sd_sendqty,0)) and scd_scid=?",
						String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("新数量小于已转数或已出货数，不允许提交!" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('行号:'||scd_detno||',订单号:'||scd_sacode||',订单行号:'||scd_sddetno) from (select scd_detno,scd_sacode,scd_sddetno from SaleChangeDetail left join SaleDetail on scd_sacode=sd_code and scd_sddetno=sd_detno where (nvl(sd_yqty,0)>0 or nvl(sd_sendqty,0)> 0) and scd_prodcode<>scd_newprodcode and scd_scid=?  and rownum<=50)",
						String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("订单号+订单已转，不允许进行料号变更!" + dets);
		}
		String remark = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat('行'||scd_detno||':'||err_info||' ') from (select scd_detno,"
						+ " case when scd_newprodcode<>scd_prodcode then '物料.' else '' end ||"
						+ " case when scd_newprice<>scd_price then '单价.' else '' end ||"
						+ " case when scd_newqty<>scd_qty then '数量.' else '' end ||"
						+ " case when scd_newpatent<>scd_patent then '专利.' else '' end ||"
						+ " case when scd_newcommission<>scd_commission then '佣金.' else '' end ||"
						+ " case when scd_newtaxrate<>scd_taxrate then '税率.' else '' end ||"
						+ " case when scd_newbeipin<>scd_beipin then '备品.' else '' end || "
						+ " case when scd_newpayments<>scd_payments then '收款方式.' else '' end ||"
						+ " case when scd_newdelivery<>scd_delivery then '交货日期.' else '' end ||"
						+ " case when scd_newdescription<>scd_description then '产品描述' else '' end err_info"
						+ " from SaleChangeDetail where scd_scid=?  and rownum<=30) where err_info is not null", String.class, sc_id);
		if (remark != null) {
			baseDao.execute("update SaleChange set sc_info=? where sc_id=?", remark, sc_id);
		}
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { sc_id });
		// 执行提交操作
		baseDao.submit("SaleChange", "sc_id=" + sc_id, "sc_status", "sc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "sc_id", sc_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { sc_id });
	}

	@Override
	public void resSubmitSaleChange(String caller, int sc_id) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("SaleChange", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "after", new Object[] { sc_id });
		// 执行反提交操作
		baseDao.resOperate("SaleChange", "sc_id=" + sc_id, "sc_status", "sc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "sc_id", sc_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { sc_id });
	}

	@Override
	public String[] printSaleChange(String caller, int sc_id, String reportName, String condition) {
		// 执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[] { sc_id });
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 记录操作
		baseDao.logger.post(caller, "sc_id", sc_id);
		// 记录打印次数
		baseDao.updateByCondition("SaleChange", "sc_count=nvl(sc_count,0)+1", "sc_id=" + sc_id);
		// 执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[] { sc_id });
		return keys;
	}
}
