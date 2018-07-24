package com.uas.erp.service.scm.impl;

import java.math.BigDecimal; 
import java.util.ArrayList; 
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.PurchaseDao;
import com.uas.erp.dao.common.VerifyApplyDao;
import com.uas.erp.dao.common.impl.VerifyApplyDaoImpl;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.scm.VerifyApplyService;

/**
 * 采购收料单、委外收料单
 * 
 * @author yingp
 * 
 */
@Service
public class VerifyApplyServiceImpl implements VerifyApplyService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private VerifyApplyDao verifyApplyDao;	 
	
	@Autowired
	private PurchaseDao purchaseDao;

	@Override
	public void saveVerifyApply(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object code = store.get("va_code");
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("VerifyApply", "va_code='" + code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, store, grid);
		// 缺省应付供应商
		if (!StringUtil.hasText(store.get("va_receivecode"))) {
			store.put("va_receivecode", store.get("va_vendcode"));
			store.put("va_receivename", store.get("va_vendname"));
		}
		String type = String.valueOf(store.get("va_class"));
		// 保存Detail
		for (Map<Object, Object> map : grid) {
			map.put("vad_code", code);
			map.put("vad_status", "ENTERING");
			if (map.get("vad_whcode") == null || map.get("vad_whcode").equals("")) {
				map.put("vad_whcode", store.get("va_whcode"));
				map.put("vad_whname", store.get("va_whname"));
			}
		}
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "VerifyApply"));
		baseDao.execute(SqlUtil.getInsertSqlbyList(grid, "VerifyApplyDetail", "vad_id"));
		updatePrice(store.get("va_id"));
		if (baseDao.isDBSetting(caller, "autoCreatBatchCode")) {
			String toCaller = "委外收料单".equals(type) ? "ProdInOut!OutsideCheckIn" : "ProdInOut!PurcCheckin";
			List<Object[]> detailField = baseDao.getFieldsDatasByCondition("VerifyApplyDetail", new String[] { "vad_id", "vad_batchcode" },
					"vad_vaid=" + store.get("va_id") + " and nvl(vad_batchcode,' ')=' '");
			for (Object[] object : detailField) {
				baseDao.updateByCondition("VerifyApplyDetail", "vad_batchcode='" + baseDao.getBatchcode(toCaller) + "'", "vad_id="
						+ object[0]);
			}
		}
		baseDao.execute("update VerifyApplyDetail set vad_code=(select va_code from VerifyApply where vad_vaid=va_id) where vad_vaid="
				+ store.get("va_id") + " and not exists (select 1 from VerifyApply where vad_code=va_code)");
		baseDao.execute("update VerifyApplyDetail set (vad_whcode,vad_whname)=(select va_whcode,va_whname from VerifyApply where vad_vaid=va_id) where vad_vaid="
				+ store.get("va_id") + " and nvl(vad_whcode,' ')=' '");
		baseDao.logger.save(caller, "va_id", store.get("va_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, store, grid);
	}

	private void updatePrice(Object id) {
		baseDao.execute(
				"update VerifyApply set va_rate=(SELECT nvl(cm_crrate,0) from Currencysmonth where va_currency=cm_crname and cm_yearmonth=to_char(va_date,'yyyymm')) where va_id=?",
				id);
		baseDao.execute("update VerifyApply set va_intype='正常委外' where va_id=" + id + " and va_class='委外收料单' and nvl(va_intype,' ')=' '");
		baseDao.execute(
				"update VerifyApplydetail set vad_price=(select round(price+price*amount/case when total=0 then 1 else total end,8) from (select vad_id,(vad_orderprice*va_rate/(1+vad_taxrate/100)) price,(select sum(vad_qty*vad_orderprice*va_rate*(1+nvl(vad_taxrate,0)/100)) from VERIFYAPPLYDetail pp1 left join VERIFYAPPLY p1 on pp1.vad_vaid=p1.va_id where p1.va_id=VERIFYAPPLYdetail.vad_vaid) total,nvl((select sum(pd_rate*pd_amount) from ProdChargeDetailAN A where A.pd_anid=VERIFYAPPLYdetail.vad_vaid),0) amount from VERIFYAPPLYDetail left join VERIFYAPPLY on vad_vaid=va_id where vad_vaid=?) B where B.vad_id=VERIFYAPPLYdetail.vad_id) where vad_vaid=? and nvl(vad_price,0)=0",
				id, id);
		baseDao.execute(
				"update VerifyApplydetail set vad_total=round(vad_price*vad_qty,2),vad_ordertotal=round(vad_orderprice*vad_qty,2),vad_plancode=round(vad_orderprice*vad_qty*(select va_rate from VERIFYAPPLY where va_id=vad_vaid),2) where vad_vaid=?",
				id);
		baseDao.execute("update VerifyApplydetail set vad_barcode=round(vad_total-vad_plancode,2) where vad_vaid=?", id);
		baseDao.execute(
				"update VerifyApply set va_total=round((select sum(vad_orderprice*vad_qty) from VERIFYAPPLYdetail where va_id=vad_vaid),2) where va_id=?",
				id);
	}

	@Override
	public void updateVerifyApply(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		Object code = store.get("va_code");
		// 只能修改[在录入]的采购收料单!
		Object status = baseDao.getFieldDataByCondition("VerifyApply", "va_statuscode", "va_id=" + store.get("va_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, store, gstore);
		// 缺省应付供应商
		if (!StringUtil.hasText(store.get("va_receivecode"))) {
			store.put("va_receivecode", store.get("va_vendcode"));
			store.put("va_receivename", store.get("va_vendname"));
		}
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "VerifyApply", "va_id"));
		double tQty = 0;
		double qty = 0;
		double aq = 0;
		double returnqty = 0;
		double vad_qty = 0;
		StringBuffer sb = new StringBuffer();
		String type = String.valueOf(store.get("va_class"));
		Object intype = store.get("va_intype");
		intype = intype == null ? "正常委外" : intype;
		if ("委外收料单".equals(type)) {
			Map<Object, List<Map<Object, Object>>> orderGroup = BaseUtil.groupsMap(gstore, new Object[] { "vad_pucode", "nvl(vad_mcid,0)",
					"vad_jobcode" });
			Set<Object> orders = orderGroup.keySet();
			List<Map<Object, Object>> list = null;
			Object[] strs = null;
			Object orderCode = null;
			Object jobid = 0;
			Object jobCode = null;
			for (Object key : orders) {
				list = orderGroup.get(key);
				strs = BaseUtil.parseStr2Array(key.toString(), "#");
				jobCode = strs.length >= 3 ? strs[2] : "";
				jobid = strs.length >= 2 ? strs[1] : 0;
				orderCode = strs.length >= 1 ? strs[0] : "";
				tQty = 0;
				for (Map<Object, Object> s : list) {
					vad_qty = Double.parseDouble(String.valueOf(s.get("vad_qty")));
					// 如果vad_sourcecode有值，说明是从批量界面转过来的，不允许更新vad_pucode
					Object vadid = s.get("vad_id");
					if (vadid != null && !vadid.equals("") && !vadid.equals("0") && Integer.parseInt(vadid.toString()) != 0) {
						// 针对有来源，批量界面转单生成的不允许修改委外单号和数量
						Object objs[] = baseDao.getFieldsDataByCondition("VerifyApplyDetail", new String[] { "vad_sourcecode",
								"nvl(vad_qty,0) as vad_qty" }, "vad_vaid=" + store.get("va_id") + " and vad_detno ="+ s.get("vad_detno") +" and nvl(vad_sourcecode,' ')<>' '");
						if (objs != null) {
							if (!objs[0].equals(s.get("vad_pucode"))) {
								BaseUtil.showError("序号：" + s.get("vad_detno") + "，是通过批量界面转单生成的，不允许修改委外单号！");
							} else if (Double.valueOf(objs[1].toString()) != vad_qty) {
								BaseUtil.showError("序号：" + s.get("vad_detno") + "，是通过批量界面转单生成的，不允许修改收料数量！");
							}
						} else {
							tQty += vad_qty;
						}
					}
				}
				if (tQty > 0) {
					if ("正常委外".equals(intype)) {
						boolean exceed = baseDao.checkIf("Make", "ma_code='" + orderCode + "' and ma_qty - nvl(ma_haveqty,0)<" + tQty);
						if (exceed)
							sb.append("保存后将超过原委外工单").append(orderCode).append("的数量");
					} else if ("工序委外".equals(intype)) {
						boolean exceed = baseDao.checkIf("MakeCraft", "mc_id=" + jobid + " and mc_qty - nvl(mc_yqty,0)<" + tQty);
						if (exceed)
							sb.append("保存后将超过原工序委外单").append(jobCode).append("的数量");
					}

				}
			}
			if (sb.length() > 0) {
				BaseUtil.showError("收料数量超出委外数量！" + sb.toString());
			}
		} else {
			for (Map<Object, Object> s : gstore) {
				if (s.get("vad_pucode") != null && !"".equals(s.get("vad_pucode"))) {// 万利达科技提出客供料要能收料
					tQty = Double.parseDouble(String.valueOf(s.get("vad_qty")));
					qty = baseDao.getFieldValue("VerifyApplyDetail", "nvl(sum(vad_qty),0)", "vad_pucode='" + s.get("vad_pucode")
							+ "' and vad_pudetno=" + s.get("vad_pudetno") + " AND vad_id <>" + s.get("vad_id"), Double.class);
					returnqty = baseDao.getFieldValue("ProdIODetail", "nvl(sum(pd_outqty),0)", "pd_ordercode='" + s.get("vad_pucode")
							+ "' and pd_orderdetno=" + s.get("vad_pudetno") + " AND pd_piclass in ('采购验退单','不良品出库单') and pd_status>0",
							Double.class);
					purchaseDao.updatePurcYNotifyQTY(0, "select pd_id from purchasedetail where pd_code='" + s.get("vad_pucode")
							+ "' and pd_detno=" + s.get("vad_pudetno") + "");
					aq = baseDao.getFieldValue("PurchaseDetail", "nvl(pd_qty,0)-nvl(pd_frozenqty,0)", "pd_code='" + s.get("vad_pucode")
							+ "' and pd_detno=" + s.get("vad_pudetno"), Double.class);
					if(NumberUtil.add(aq, returnqty)<NumberUtil.add(qty, tQty)){
						sb.append("采购单号：").append(s.get("vad_pucode")).append("序号：").append(s.get("vad_pudetno")).append("超出数量：")
								.append(new BigDecimal(Double.toString(qty)).add(new BigDecimal(Double.toString(tQty)))
										.subtract(new BigDecimal(Double.toString(aq)))).append("<br>");
					}
				}
			}
			if (sb.length() > 0) {
				BaseUtil.showError("收料数量超出采购单数量-已冻结数量！" + sb.toString());
			}
		}
		// 修改Detail
		List<String> gridSql = new ArrayList<String>();
		for (Map<Object, Object> s : gstore) {
			if (s.get("vad_whcode") == null || s.get("vad_whcode").equals("")) {
				s.put("vad_whcode", store.get("va_whcode"));
				s.put("vad_whname", store.get("va_whname"));
			}
			Object vadid = s.get("vad_id");
			if (vadid == null || vadid.equals("") || vadid.equals("0") || Integer.parseInt(vadid.toString()) == 0) {// 新添加的数据，id不存在
				s.put("vad_code", code);
				s.put("vad_status", "ENTERING");
				gridSql.add(SqlUtil.getInsertSql(s, "VerifyApplyDetail", "vad_id"));
				if (s.get("vad_pucode") != null && !"".equals(s.get("vad_pucode"))) {
					verifyApplyDao.restorePurcYqty(vadid, Double.parseDouble(s.get("vad_qty").toString()), s.get("vad_pucode").toString(),
							Integer.valueOf(s.get("vad_pudetno").toString()));
				}
			} else {
				gridSql.add(SqlUtil.getUpdateSqlByFormStore(s, "VerifyApplyDetail", "vad_id"));	
				Object[] oldPurcInfo=baseDao.getFieldsDataByCondition("VerifyApplyDetail",new String[] {"vad_pucode","vad_pudetno","nvl(vad_qty,0)","nvl(vad_purcqty,0)","vad_class","vad_andid"}, "vad_id="+vadid);
				Double old = Double.parseDouble(oldPurcInfo[2].toString());
				Double vad_purcqty = Double.parseDouble(oldPurcInfo[3].toString()); 				
				if ("采购收料单".equals(oldPurcInfo[4].toString()) || "采购入库申请单".equals(oldPurcInfo[4].toString())) {
					if((oldPurcInfo[0] == null || oldPurcInfo[1] == null) && (s.get("vad_pucode") != null && !"".equals(s.get("vad_pucode")))){
						//更新新单已转
						baseDao.execute("update purchasedetail set pd_yqty=pd_yqty+"+(new BigDecimal(Double.parseDouble(s.get("vad_qty").toString()))) +","
								+ "pd_ypurcqty=nvl(pd_ypurcqty,0)+"+(new BigDecimal(Double.parseDouble((s.get("vad_purcqty")==null?"0":s.get("vad_purcqty").toString()))))+
								"where pd_code='"+s.get("vad_pucode")+"' and pd_detno="+s.get("vad_pudetno") );
						verifyApplyDao.updatePurcStatus2(s.get("vad_pucode").toString());
					}else if (oldPurcInfo[0]!=null && oldPurcInfo[1]!=null
							&&( !s.get("vad_pucode").toString().equals(oldPurcInfo[0].toString())
									|| !s.get("vad_pudetno").toString().equals(oldPurcInfo[1].toString()))){
						//更新原单已转
						baseDao.execute("update purchasedetail set pd_yqty=pd_yqty-"+(new BigDecimal(oldPurcInfo[2].toString())) +","
								+ "pd_ypurcqty=nvl(pd_ypurcqty,0)-("+(new BigDecimal(vad_purcqty))+") "
								+ "where pd_code='"+oldPurcInfo[0].toString()+"' and pd_detno="+oldPurcInfo[1].toString() );
						verifyApplyDao.updatePurcStatus2(oldPurcInfo[0].toString());
						if(s.get("vad_pucode") != null && !"".equals(s.get("vad_pucode"))){
							//更新新单已转
							baseDao.execute("update purchasedetail set pd_yqty=pd_yqty+"+(new BigDecimal(Double.parseDouble(s.get("vad_qty").toString()))) +","
									+ "pd_ypurcqty=nvl(pd_ypurcqty,0)+"+(new BigDecimal(Double.parseDouble((s.get("vad_purcqty")==null?"0":s.get("vad_purcqty").toString()))))+
									"where pd_code='"+s.get("vad_pucode")+"' and pd_detno="+s.get("vad_pudetno") );
							verifyApplyDao.updatePurcStatus2(s.get("vad_pucode").toString());
						}						
					}else if (s.get("vad_pucode") != null && !"".equals(s.get("vad_pucode")) &&  
							s.get("vad_pucode").toString().equals(oldPurcInfo[0].toString())
							&& s.get("vad_pudetno").toString().equals(oldPurcInfo[1].toString())) {
						// 修改采购单已转数量及状态
						verifyApplyDao.restorePurcWithQty(Integer.parseInt(vadid.toString()),
								(new BigDecimal(old)).subtract(new BigDecimal(Double.parseDouble(s.get("vad_qty").toString()))).doubleValue(),(new BigDecimal(vad_purcqty)).subtract(new BigDecimal(Double.parseDouble((s.get("vad_purcqty")==null?"0":s.get("vad_purcqty").toString())))).doubleValue());
					}
					
				}else if ("委外收料单".equals(oldPurcInfo[4].toString())){
					if (s.get("vad_pucode") != null && !"".equals(s.get("vad_pucode"))) {
						// 修改采购单已转数量及状态
						verifyApplyDao.restorePurcWithQty(Integer.parseInt(vadid.toString()),
								(new BigDecimal(old)).subtract(new BigDecimal(Double.parseDouble(s.get("vad_qty").toString()))).doubleValue(),(new BigDecimal(vad_purcqty)).subtract(new BigDecimal(Double.parseDouble((s.get("vad_purcqty")==null?"0":s.get("vad_purcqty").toString())))).doubleValue());
					}
				}
				
			}
		}
		baseDao.execute(gridSql);
		updatePrice(store.get("va_id"));
		if (baseDao.isDBSetting(caller, "autoCreatBatchCode")) {
			String toCaller = "委外收料单".equals(type) ? "ProdInOut!OutsideCheckIn" : "ProdInOut!PurcCheckin";
			List<Object[]> detailField = baseDao.getFieldsDatasByCondition("VerifyApplyDetail", new String[] { "vad_id", "vad_batchcode" },
					"vad_vaid=" + store.get("va_id") + " and nvl(vad_batchcode,' ')=' '");
			for (Object[] object : detailField) {
				baseDao.updateByCondition("VerifyApplyDetail", "vad_batchcode='" + baseDao.getBatchcode(toCaller) + "'", "vad_id="
						+ object[0]);
			}
		}
		baseDao.execute("update VerifyApplyDetail set vad_code=(select va_code from VerifyApply where vad_vaid=va_id) where vad_vaid="
				+ store.get("va_id") + " and not exists (select 1 from VerifyApply where vad_code=va_code)");
		baseDao.execute("update VerifyApplyDetail set (vad_whcode,vad_whname)=(select va_whcode,va_whname from VerifyApply where vad_vaid=va_id) where vad_vaid="
				+ store.get("va_id") + " and nvl(vad_whcode,' ')=' '");
		// 记录操作
		baseDao.logger.update(caller, "va_id", store.get("va_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, store, gstore);
	}

	@Override
	public void deleteVerifyApply(int id, String caller) {
		try {
			baseDao.execute("select va_id from VerifyApply where va_id=? for update wait 3", id);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			BaseUtil.showError("当前单据有其他人正在操作");
		} 
		// 只能删除在录入的采购收料单!
		Object status = baseDao.getFieldDataByCondition("VerifyApply", "va_statuscode", "va_id=" + id);
		StateAssert.delOnlyEntering(status);
		baseDao.delCheck("VerifyApply", id);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, id);
		// 删除
		verifyApplyDao.deleteVerifyApply(id);
		// 记录操作
		baseDao.logger.delete(caller, "va_id", id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, id);
	}

	@Override
	public String[] printVerifyApply(int id, String reportName, String condition, String caller) {
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, id);
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 记录操作
		baseDao.logger.print(caller, "va_id", id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, id);
		return keys;
	}

	@Override
	public void auditVerifyApply(int id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("VerifyApply", "va_statuscode", "va_id=" + id);
		StateAssert.auditOnlyCommited(status);
		baseDao.execute("update VerifyApplyDetail set vad_code=(select va_code from VerifyApply where vad_vaid=va_id) where vad_vaid=" + id
				+ " and not exists (select 1 from VerifyApply where vad_code=va_code)");
		String type = baseDao.getJdbcTemplate().queryForObject("select va_class from verifyapply where va_id=?", String.class, id);
		if ("采购收料单".equals(type)) {
			String err = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat('采购单号:'||vad_pucode||'采购序号:'||vad_pudetno||'物料编号:'||vad_prodcode) from (select vad_pucode,vad_pudetno,vad_prodcode from VerifyApplyDetail where vad_vaid=? and nvl(vad_pucode,' ')<>' ' and not exists (select 1 from PurchaseDetail where pd_code=vad_pucode and pd_detno=vad_pudetno and pd_prodcode=vad_prodcode))",
							String.class, id);
			if (err != null) {
				BaseUtil.showError("料号+采购单号+采购序号不存在!" + err);
			}
			if (baseDao.isDBSetting("CopCheck")) {
				String dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(vad_detno) from VerifyApplyDetail left join VerifyApply on vad_vaid=va_id left join Purchase on vad_pucode=pu_code where nvl(pu_cop,' ')<>nvl(va_cop,' ') and vad_vaid=?",
								String.class, id);
				if (dets != null) {
					BaseUtil.showError("明细行采购单所属公司更收料单所属公司不一致，不允许进行当前操作!行号：" + dets);
				}
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(vad_detno) from VerifyApplyDetail left join VerifyApply on vad_vaid=va_id left join warehouse on vad_whcode=wh_code where nvl(va_cop,' ')<>nvl(wh_cop,' ') and vad_vaid=?",
								String.class, id);
				if (dets != null) {
					BaseUtil.showError("明细行仓库所属公司更收料单所属公司不一致，不允许进行当前操作!行号：" + dets);
				}
			}
			factoryCheck(id);
			Double tQty = 0.0;
			Object aq = 0.0;
			Object qty = 0.0;
			Object returnqty = 0.0;
			StringBuffer sb = new StringBuffer();
			SqlRowList rs = baseDao.queryForRowSet("select * from VerifyApplyDetail where vad_vaid=?", id);
			while (rs.next()) {
				if (rs.getObject("vad_pucode") != null && !"".equals(rs.getObject("vad_pucode").toString())) {
					Object vadid = rs.getObject("vad_id");
					tQty = rs.getDouble("vad_qty");
					qty = baseDao.getFieldDataByCondition("VerifyApplyDetail", "nvl(sum(vad_qty),0)",
							"vad_pucode='" + rs.getObject("vad_pucode") + "' and vad_pudetno=" + rs.getObject("vad_pudetno")
									+ " AND vad_id <>" + vadid);
					returnqty = baseDao.getFieldDataByCondition("ProdioDetail", "sum(pd_outqty)",
							"pd_piclass in ('不良品出库单','采购验退单') and pd_status>0 and pd_ordercode='" + rs.getObject("vad_pucode")
									+ "' and pd_orderdetno=" + rs.getObject("vad_pudetno"));
					aq = baseDao.getFieldDataByCondition("PurchaseDetail", "nvl(pd_qty,0)-nvl(pd_frozenqty,0)",
							"pd_code='" + rs.getObject("vad_pucode") + "' and pd_detno=" + rs.getObject("vad_pudetno"));
					qty = qty == null ? 0 : qty;
					aq = aq == null ? 0 : aq;
					returnqty = returnqty == null ? 0 : returnqty;
					if(NumberUtil.add(String.valueOf(aq), String.valueOf(returnqty)) < NumberUtil.add(String.valueOf(qty), Double.toString(tQty))){
						sb.append("采购单号：")
								.append(rs.getObject("vad_pucode"))
								.append("序号：")
								.append(rs.getObject("vad_pudetno"))
								.append("超出数量：")
								.append(new BigDecimal(String.valueOf(qty)).add(new BigDecimal(Double.toString(tQty)))
										.subtract(new BigDecimal(String.valueOf(aq))).subtract(new BigDecimal(String.valueOf(returnqty))))
								.append("<br>");
					}
				}
			}
			if (sb.length() > 2) {
				BaseUtil.showError("收料数量超出采购单数量-已冻结数量！" + sb.toString());
			}
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, id);
		// 执行审核操作
		baseDao.audit("VerifyApply", "va_id=" + id, "va_status", "va_statuscode", "va_auditdate", "va_auditman");
		baseDao.audit("VerifyApplyDetail", "vad_vaid=" + id, "VAD_STATUS", "VAD_STATUSCODE");
		verifyApplyDao.updatesourceqty(id);
		// 执行提交操作
		baseDao.logger.audit(caller, "va_id", id);
		if ("采购收料单".equals(type)) {
			baseDao.updateByCondition("VerifyApply", "va_sendstatus='待上传'", "va_id=" + id);
		}
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, id);
	}

	@Override
	public void resAuditVerifyApply(int id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] objs = baseDao.getFieldsDataByCondition("VerifyApply", new String[] { "va_statuscode" }, "va_id=" + id);
		StateAssert.resAuditOnlyAudit(objs[0]);
		baseDao.resAuditCheck("VerifyApply", id);
		// 只能对未转进行反审核操作!
		int count = baseDao.getCountByCondition("VerifyApplyDetail", "vad_vaid=" + id + " and ve_code is not null");
		if (count > 0) {
			BaseUtil.showError("已转检验单的收料单不允许反审核操作！");
		}
		// 执行反审核操作
		baseDao.resAudit("VerifyApply", "va_id=" + id, "va_status", "va_statuscode", "va_auditdate", "va_auditman");
		baseDao.resOperate("VerifyApplyDetail", "vad_vaid=" + id, "VAD_STATUS", "VAD_STATUSCODE");
		verifyApplyDao.resauditsourceqty(id);
		// 记录操作
		baseDao.logger.resAudit(caller, "va_id", id);
	}

	@Override
	public void submitVerifyApply(int id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("VerifyApply", "va_statuscode", "va_id=" + id);
		StateAssert.submitOnlyEntering(status);
		baseDao.execute("update VerifyApplyDetail set vad_code=(select va_code from VerifyApply where vad_vaid=va_id) where vad_vaid=" + id
				+ " and not exists (select 1 from VerifyApply where vad_code=va_code)");
		updatePrice(id);
		String type = baseDao.getJdbcTemplate().queryForObject("select va_class from verifyapply where va_id=?", String.class, id);
		if ("采购收料单".equals(type)) {
			// 判断批号是否重复
			String errRows = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(vad_detno) from VerifyApplydetail where vad_vaid=? and (vad_batchcode,vad_prodcode,vad_whcode) in (select vad_batchcode,vad_prodcode,vad_whcode from (select count(1) c, vad_batchcode,vad_prodcode,vad_whcode from VerifyApplydetail where (vad_batchcode,vad_prodcode,vad_whcode) in (select vad_batchcode,vad_prodcode,vad_whcode from VerifyApplydetail where vad_vaid=?) group by vad_batchcode,vad_prodcode,vad_whcode) where c > 1)",
							String.class, id, id);
			if (errRows != null) {
				BaseUtil.showError("批号重复,行:" + errRows);
			} else {
				errRows = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wmsys.wm_concat(vad_detno) from VerifyApplydetail where vad_vaid=? and (vad_batchcode,vad_prodcode,vad_whcode) in (select ba_code,ba_prodcode,ba_whcode from batch)",
								String.class, id);
				if (errRows != null) {
					BaseUtil.showError("批号重复,行:" + errRows);
				}
			}
			String err = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat('采购单号:'||vad_pucode||'采购序号:'||vad_pudetno||'物料编号:'||vad_prodcode) from (select vad_pucode,vad_pudetno,vad_prodcode from VerifyApplyDetail where vad_vaid=? and nvl(vad_pucode,' ')<>' ' and not exists (select 1 from PurchaseDetail where pd_code=vad_pucode and pd_detno=vad_pudetno and pd_prodcode=vad_prodcode))",
							String.class, id);
			if (err != null) {
				BaseUtil.showError("料号+采购单号+采购序号不存在!" + err);
			}
			if (baseDao.isDBSetting("CopCheck")) {
				String dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(vad_detno) from VerifyApplyDetail left join VerifyApply on vad_vaid=va_id left join Purchase on vad_pucode=pu_code where nvl(pu_cop,' ')<>nvl(va_cop,' ') and vad_vaid=?",
								String.class, id);
				if (dets != null) {
					BaseUtil.showError("明细行采购单所属公司更收料单所属公司不一致，不允许进行当前操作!行号：" + dets);
				}
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(vad_detno) from VerifyApplyDetail left join VerifyApply on vad_vaid=va_id left join warehouse on vad_whcode=wh_code where nvl(va_cop,' ')<>nvl(wh_cop,' ') and vad_vaid=?",
								String.class, id);
				if (dets != null) {
					BaseUtil.showError("明细行仓库所属公司更收料单所属公司不一致，不允许进行当前操作!行号：" + dets);
				}
			}
			factoryCheck(id);
			Double tQty = 0.0;
			Object aq = 0.0;
			Object qty = 0.0;
			Object returnqty = 0.0;
			StringBuffer sb = new StringBuffer();
			SqlRowList rs = baseDao.queryForRowSet("select * from VerifyApplyDetail where vad_vaid=?", id);
			while (rs.next()) {
				if (rs.getObject("vad_pucode") != null && !"".equals(rs.getObject("vad_pucode").toString())) {
					Object vadid = rs.getObject("vad_id");
					tQty = rs.getDouble("vad_qty");
					qty = baseDao.getFieldDataByCondition("VerifyApplyDetail", "nvl(sum(vad_qty),0)",
							"vad_pucode='" + rs.getObject("vad_pucode") + "' and vad_pudetno=" + rs.getObject("vad_pudetno")
									+ " AND vad_id <>" + vadid);
					returnqty = baseDao.getFieldDataByCondition("ProdioDetail", "nvl(sum(pd_outqty),0)",
							"pd_piclass in ('不良品出库单','采购验退单') and pd_status>0 and pd_ordercode='" + rs.getObject("vad_pucode")
									+ "' and pd_orderdetno=" + rs.getObject("vad_pudetno"));
					aq = baseDao.getFieldDataByCondition("PurchaseDetail", "nvl(pd_qty,0)-nvl(pd_frozenqty,0)",
							"pd_code='" + rs.getObject("vad_pucode") + "' and pd_detno=" + rs.getObject("vad_pudetno"));
					qty = qty == null ? 0 : qty;
					aq = aq == null ? 0 : aq;
					returnqty = returnqty == null ? 0 : returnqty;
					if(NumberUtil.add(String.valueOf(aq), String.valueOf(returnqty))<NumberUtil.add(String.valueOf(qty),Double.toString(tQty))){
						sb.append("采购单号：")
								.append(rs.getObject("vad_pucode"))
								.append("序号：")
								.append(rs.getObject("vad_pudetno"))
								.append("超出数量：")
								.append(new BigDecimal(String.valueOf(qty)).add(new BigDecimal(Double.toString(tQty)))
										.subtract(new BigDecimal(String.valueOf(aq)))
										.subtract(new BigDecimal(String.valueOf(returnqty))))
								.append("<br>");
					}
				}
			}
			if (sb.length() > 2) {
				BaseUtil.showError("收料数量超出采购单数量-已冻结数量！" + sb.toString());
			}
			// 应付供应商为空时，抓供应商资料的缺省值
			baseDao.execute("UPDATE VerifyApply SET (va_receivecode,va_receivename)=(SELECT nvl(pu_receivecode,ve_code),nvl(pu_receivename,ve_name) FROM Vendor left join Purchase on pu_vendcode=ve_code WHERE pu_vendcode=va_vendcode) WHERE va_id="
					+ id + " AND NVL(va_receivecode,' ')=' '");
		}
		baseDao.execute("update VerifyApplyDetail set vad_prodcode=upper(ltrim(rtrim(vad_prodcode))) where vad_vaid=" + id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, id);
		// 默认仓库
		baseDao.updateByCondition("VerifyApplyDetail", "vad_whcode=(SELECT va_whcode FROM VerifyApply WHERE va_id=" + id + ")", "vad_vaid="
				+ id + " and (vad_whcode is null or trim(vad_whcode)='')");
		baseDao.submit("VerifyApply", "va_id=" + id, "va_status", "va_statuscode");
		baseDao.submit("VerifyApplyDetail", "vad_vaid=" + id, "VAD_STATUS", "VAD_STATUSCODE");
		// 执行提交操作
		baseDao.logger.submit(caller, "va_id", id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, id);
	}

	@Override
	public void resSubmitVerifyApply(int id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("VerifyApply", "va_statuscode", "va_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, id);
		// 执行反提交操作
		baseDao.resOperate("VerifyApply", "va_id=" + id, "va_status", "va_statuscode");
		baseDao.resOperate("VerifyApplyDetail", "vad_vaid=" + id, "VAD_STATUS", "VAD_STATUSCODE");
		// 记录操作
		baseDao.logger.resSubmit(caller, "va_id", id);
		handlerService.afterResSubmit(caller, id);
	}

	@Override
	public String turnStorage(String caller, int va_id) {
		int piid = 0;
		String msg = "";
		//判断已转数
		Object chekQty = baseDao.getJdbcTemplate().queryForObject("select wmsys.wm_concat('收料单：'||vad_code||'序号：'||vad_detno)  from VerifyApplyDetail where vad_qty <= vad_yqty and vad_vaid = "+va_id, String.class);
		if(chekQty !=null){
			BaseUtil.showError("检测到" + chekQty + "，本次数量超出可转数量！");
		}
		// 判断该收料通知单是否已经转入过采购验收单
		Object code = baseDao.getFieldDataByCondition("VerifyApply", "va_code", "va_id=" + va_id);
		code = baseDao.getFieldDataByCondition("ProdInOut", "pi_inoutno", "pi_sourcecode='" + code + "' and PI_REFNO='采购收料单'");
		if (code != null && !code.equals("")) {
			BaseUtil.showError("该收料单已转入过采购验收单,验收单号[" + code + "]");
		} else {
			// 转采购验收单
			piid = verifyApplyDao.turnStorage(va_id);
			if (piid != 0) {
				SqlRowList rs = baseDao.queryForRowSet("select vad_pucode,vad_pudetno,vad_qty from VerifyApplyDetail where vad_vaid=?",
						va_id);
				String pCode = null;
				int pDetno = 0;
				double yq = 0;
				Set<String> pCodes = new HashSet<String>();
				while (rs.next()) {
					pCode = rs.getString("vad_pucode");
					if (!pCodes.contains(pCode)) {
						pCodes.add(pCode);
					}
					pDetno = rs.getInt("vad_pudetno");
					yq = rs.getDouble("vad_qty");
					baseDao.updateByCondition("PurchaseDetail", "pd_status='PART2IN'", "pd_code='" + pCode + "' and pd_detno=" + pDetno);
					baseDao.updateByCondition("PurchaseDetail", "pd_status='TURNIN'", "pd_code='" + pCode + "' and pd_detno=" + pDetno
							+ " and pd_qty=" + yq);
				}
				Iterator<String> iter = pCodes.iterator();
				while (iter.hasNext()) {
					pCode = iter.next();
					int total = baseDao.getCountByCondition("PurchaseDetail", "pd_code='" + pCode + "'");
					int aud = baseDao.getCountByCondition("PurchaseDetail", "pd_code='" + pCode + "' AND nvl(pd_acceptqty,0)=0");
					int turn = baseDao.getCountByCondition("PurchaseDetail", "pd_code='" + pCode
							+ "' AND nvl(pd_acceptqty,0)=nvl(pd_qty,0)");
					String status = aud == total ? "" : (turn == total ? "TURNIN" : "PART2IN");
					baseDao.updateByCondition("Purchase",
							"pu_turnstatuscode='" + status + "',pu_turnstatus='" + BaseUtil.getLocalMessage(status) + "'", "pu_code='"
									+ pCode + "'");
				}
				// 记录操作
				baseDao.logger.turn("msg.turnStorage", caller, "va_id", va_id);
				Object pi_inoutno = baseDao.getFieldDataByCondition("ProdInOut", "pi_inoutno", "pi_id=" + piid);
				msg = "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?whoami=ProdInOut!PurcCheckin&formCondition=pi_idIS"
						+ piid + "&gridCondition=pd_piidIS" + piid + "')\">" + pi_inoutno + "</a>&nbsp;";
			}
		}
		return "入库成功!验收单号:&nbsp" + msg;
	}

	@Override
	@Transactional
	public String detailTurnIQC(String data) {
		return turnQC(data, "采购检验单", "VerifyApplyDetail");
	}

	@Override
	@Transactional
	public String detailTurnFQC(String data) {
		return turnQC(data, "委外检验单", "VerifyApplyDetail");
	}

	/**
	 * 转检验
	 * 
	 * @param data
	 * @param qcClass
	 * @param caller
	 * @param employee
	 * @param language
	 * @return
	 */

	private String turnQC(String data, String qcClass, String caller) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		String turnIds = "";
		String ids = BaseUtil.parseArray2Str(CollectionUtil.pluck(store, "vad_id"), ",");
		try {
			baseDao.execute("select vad_id from VerifyApplyDetail where vad_id in ("+ids+") for update nowait");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			BaseUtil.showError("当前单据有其他人正在操作，不能执行当前操作");
		}
		//判断已转数
		Object chekQty = baseDao.getJdbcTemplate().queryForObject("select wmsys.wm_concat('收料单：'||vad_code||'序号：'||vad_detno)  from VerifyApplyDetail where vad_qty <= vad_yqty and vad_id in ("+ids+")", String.class);
		if(chekQty !=null){
			BaseUtil.showError("检测到" + chekQty.toString() + "，本次数量超出可转数量！");
		}
		// 只取没转项
		Object vadcodes = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat('收料单：'||vad_code||'序号：'||vad_detno) vad_code from VerifyApplyDetail where vad_id in("
								+ ids
								+ ") and exists (select 1 from Qua_VerifyApplyDetail where Qua_VerifyApplyDetail.vad_code =VerifyApplyDetail.vad_code and Qua_VerifyApplyDetail.vad_detno =VerifyApplyDetail.vad_detno)",
						String.class);
		if (vadcodes != null) {
			//全部转检验单
			ids = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(vad_id) from VerifyApplyDetail left join verifyapply on vad_vaid=va_id where vad_id in("
									+ ids
									+ ") and not exists (select 1 from Qua_VerifyApplyDetail where Qua_VerifyApplyDetail.vad_code =VerifyApplyDetail.vad_code and Qua_VerifyApplyDetail.vad_detno =VerifyApplyDetail.vad_detno)",
							String.class);
			if (ids == null) {
				BaseUtil.showError("检测到" + vadcodes + "已全部转检验单,不允许重复转.");
			}
		}
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat('行号：'||vad_detno) from VerifyApplyDetail left join VerifyApply on vad_vaid=va_id where vad_id in(" + ids
						+ ") " + "and exists (select 1 from verifyapplychange left join verifyapplychangeDetail on vcd_vcid=vc_id"
						+ " where vcd_vacode=vad_code and vcd_vadetno=vad_detno and vc_statuscode<>'AUDITED')", String.class);
		if (dets != null) {
			// 如果存在部分未审核、部分已审核：先转已审核部分，对于未审核部分作为提示信息
			ids = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(vad_id) from VerifyApplyDetail left join verifyapply on vad_vaid=va_id where vad_id in("
									+ ids
									+ ") and not exists (select 1 from verifyapplychange left join verifyapplychangeDetail on vcd_vcid=vc_id where vc_statuscode<>'AUDITED' and vcd_vacode=vad_code and vcd_vadetno=vad_detno)",
							String.class);
			if (ids == null) {
				BaseUtil.showError("检测到有未审核的收料变更单,不允许转检验单.");
			}
		}
		String dets1 = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat('行号：'||vad_detno) from VerifyApplyDetail left join VerifyApply on vad_vaid=va_id where vad_id in(" + ids
						+ ") " + "and va_statuscode<>'AUDITED'", String.class);
		if (dets1 != null) {
			ids = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(vad_id) from VerifyApplyDetail left join verifyapply on vad_vaid=va_id where vad_id in(" + ids
							+ ") and va_statuscode='AUDITED'", String.class);
			if (ids == null) {
				BaseUtil.showError("检测到有未审核的收料单,不允许转检验单.");
			}
		}
		StringBuffer sb = new StringBuffer();
		String jyIds = baseDao.getJdbcTemplate().queryForObject(
				"select lob_concat(vad_id) from VerifyApplyDetail left join product on pr_code=vad_prodcode where vad_id in(" + ids
						+ ") and (pr_acceptmethod is null or pr_acceptmethod=' ' or pr_acceptmethod='0' or pr_acceptmethod='检验')",
				String.class);
		if (jyIds != null) {

			Map<Integer, String> res = verifyApplyDao.turnQC(jyIds, qcClass, "IQC", "UNAUDIT");
			turnIds += jyIds;
			for (Integer i : res.keySet()) {
				Object[] ve = baseDao.getFieldsDataByCondition("Qua_VerifyApplyDetail left join Product on vad_prodcode=pr_code",
						new String[] { "vad_qty", "pr_aql" }, "ve_id=" + i);
				if (ve != null) {
					baseDao.execute("update QUA_VerifyApplyDetail set (ve_samplingaqty, ve_samplingngjgqty)=(select max(nvl(ad_qty,0)), max(nvl(ad_maxngacceptqty,0)) "
							+ "from QUA_Aql,QUA_AqlDetail where al_id=ad_alid AND al_statuscode='AUDITED' and al_code=ve_aql and vad_qty>=ad_minqty and vad_qty<=ad_maxqty) "
							+ "where ve_id = " + i);
					baseDao.execute("update QUA_VerifyApplyDetail set ve_samplingaqty=nvl(vad_qty,0) where nvl(ve_samplingaqty,0)>nvl(vad_qty,0) and ve_id = "
							+ i);
					baseDao.execute("update QUA_VerifyApplyDetail set ve_testman=(select ve_testman from (select ve_testman from qua_verifyapplydetail where nvl(ve_testman,' ')<>' ' and vad_prodcode=(select vad_prodcode from qua_verifyapplydetail where ve_id="
							+ i + ") order by ve_date desc) where rownum<2) " + "where ve_id=" + i);
					/**
					 * 系统问题反馈：单据编号2016120717 问题：淼英辉正式账套，采购收料单转检验单的时候，要求自动带出检验员，
					 * 检验单界面检验员ve_testman取物料资料中的检验员pr_inspector。
					 * 
					 * @author wsy
					 */
					Object prodcode = baseDao.getFieldDataByCondition("QUA_VerifyApplyDetail", "vad_prodcode", "ve_id=" + i);
					baseDao.execute("update QUA_VerifyApplyDetail set ve_testman=(select pr_inspector from product where pr_code='"
							+ prodcode + "') where ve_id=" + i + " and  nvl(ve_testman,' ')=' '");

					baseDao.execute("update QUA_VerifyApplyDetail set (ve_brand,ve_oldfactory,ve_factoryspec)=(select pa_brand,pa_addressmark,pa_factoryspec from (select pa_brand,pa_addressmark,pa_factoryspec from ProductApproval where nvl(pa_statuscode,' ')='AUDITED' AND NVL(pa_finalresult,' ')='合格' and (pa_prodcode,pa_providecode) IN (select vad_prodcode,vad_vendcode from qua_verifyapplydetail where ve_id="
							+ i + ") order by pa_auditdate desc) where rownum<2) where ve_id=" + i);
					baseDao.execute(
							"update Qua_VerifyApplyDetailDet set ved_testman=(select ve_testman from Qua_VerifyApplyDetail where ve_id=ved_veid and nvl(ve_testman,' ')<>' ') where ved_veid=?",
							i);
					baseDao.execute(
							"update Qua_VerifyApplyDetailDet set ved_samplingqty=(select ve_samplingaqty from Qua_VerifyApplyDetail where ve_id=ved_veid and nvl(ve_samplingaqty,0)<>0) where ved_veid=?",
							i);

				}
			}
			for (Integer i : res.keySet()) {
				sb.append("转入成功,IQC检验单号:" + "<a href=\"javascript:openUrl('jsps/scm/qc/verifyApplyDetail2.jsp?whoami=" + caller
						+ "&formCondition=ve_idIS" + i + "&gridCondition=ved_veidIS" + i + "')\">" + res.get(i) + "</a>&nbsp;");
			}

		}
		jyIds = baseDao.getJdbcTemplate().queryForObject(
				"select lob_concat(vad_id) from VerifyApplyDetail left join product on pr_code=vad_prodcode where vad_id in(" + ids
						+ ") and (nvl(pr_acceptmethod,' ')='1' or nvl(pr_acceptmethod,' ')='不检验')", String.class);
		if (jyIds != null) {
			Map<Integer, String> res = verifyApplyDao.turnFreeQC(jyIds, qcClass, "IQC", "AUDITED");
			turnIds += turnIds.equals("") ? jyIds : "," + jyIds;
			for (Integer i : res.keySet()) {
				Object[] ve = baseDao.getFieldsDataByCondition("Qua_VerifyApplyDetail left join Product on vad_prodcode=pr_code",
						new String[] { "vad_qty", "pr_aql" }, "ve_id=" + i);
				if (ve != null) {
					baseDao.execute("update QUA_VerifyApplyDetail set (ve_samplingaqty, ve_samplingngjgqty)=(select max(nvl(ad_qty,0)), max(nvl(ad_maxngacceptqty,0)) "
							+ "from QUA_Aql,QUA_AqlDetail where al_id=ad_alid AND al_statuscode='AUDITED' and al_code=ve_aql and vad_qty>=ad_minqty and vad_qty<=ad_maxqty) "
							+ "where ve_id = " + i);
					baseDao.execute("update QUA_VerifyApplyDetail set ve_samplingaqty=nvl(vad_qty,0) where nvl(ve_samplingaqty,0)>nvl(vad_qty,0) and ve_id = "
							+ i);
					// 如果抽样数>送检数 ，则抽样数=送检数。
					baseDao.execute("update QUA_VerifyApplyDetail set ve_testman=(select ve_testman from (select ve_testman from qua_verifyapplydetail where nvl(ve_testman,' ')<>' ' and vad_prodcode=(select vad_prodcode from qua_verifyapplydetail where ve_id="
							+ i + ") order by ve_date desc) where rownum<2) " + "where ve_id=" + i);
					/**
					 * 系统问题反馈：单据编号2016120717 问题：淼英辉正式账套，采购收料单转检验单的时候，要求自动带出检验员，
					 * 检验单界面检验员ve_testman取物料资料中的检验员pr_inspector。
					 * 
					 * @author wsy
					 */
					Object prodcode = baseDao.getFieldDataByCondition("QUA_VerifyApplyDetail", "vad_prodcode", "ve_id=" + i);
					baseDao.execute("update QUA_VerifyApplyDetail set ve_testman=(select pr_inspector from product where pr_code='"
							+ prodcode + "') where ve_id=" + i + " and  nvl(ve_testman,' ')<>' '");

					baseDao.execute("update QUA_VerifyApplyDetail set (ve_brand,ve_oldfactory,ve_factoryspec)=(select pa_brand,pa_addressmark,pa_factoryspec from (select pa_brand,pa_addressmark,pa_factoryspec from ProductApproval where nvl(pa_statuscode,' ')='AUDITED' AND NVL(pa_finalresult,' ')='合格' and (pa_prodcode,pa_providecode) IN (select vad_prodcode,vad_vendcode from qua_verifyapplydetail where ve_id="
							+ i + ") order by pa_auditdate desc) where rownum<2) where ve_id=" + i);
					baseDao.execute(
							"update Qua_VerifyApplyDetailDet set ved_testman=(select ve_testman from Qua_VerifyApplyDetail where ve_id=ved_veid and nvl(ve_testman,' ')<>' ') where ved_veid=?",
							i);
					baseDao.execute(
							"update Qua_VerifyApplyDetailDet set ved_samplingqty=(select ve_samplingaqty from Qua_VerifyApplyDetail where ve_id=ved_veid and nvl(ve_samplingaqty,0)<>0) where ved_veid=?",
							i);
				}
				sb.append("转入成功,IQC检验单号:" + "<a href=\"javascript:openUrl('jsps/scm/qc/verifyApplyDetail2.jsp?whoami=" + caller
						+ "&formCondition=ve_idIS" + i + "&gridCondition=ved_veidIS" + i + "')\">" + res.get(i) + "</a>&nbsp;");
			}
			// 免检的收料单转检验后直接更新收料明细的上传状态
			baseDao.execute("update VerifyApplyDetail set vad_sendstatus='待上传' where vad_id in (" + jyIds + ")");
		}
		if (dets != null) {
			BaseUtil.appendError(sb.toString() + "<hr>存在未审核的收料变更单：" + dets);
		}
		if (dets1 != null) {
			BaseUtil.appendError(sb.toString() + "<hr>存在未审核的收料单：" + dets1);
		}
		try {
			baseDao.execute("insert into messagelog(ML_ID,ML_DATE,ML_MAN,ML_CONTENT,ML_RESULT,ML_SEARCH,CODE) "
					+ "select messageLog_SEQ.nextval,sysdate,'" + SystemSession.getUser().getEm_name()
					+ "','转检验','转检验成功','VerifyApply|va_id='||vad_vaid,vad_code "
					+ "from (select DISTINCT vad_vaid,vad_code from VerifyApplydetail where vad_id in(" + ids + "))");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return sb.toString();
	}

	static final String INSERTVERIFYAPPLYDETAILP = "INSERT INTO VerifyApplyDetailP(vadp_id,vadp_vadid,vadp_vacode,vadp_vaddetno,vadp_detno,"
			+ "vadp_qty,vadp_vendcode,vadp_vendname,vadp_prodcode,vadp_batchcode)" + " values (?,?,?,?,?,?,?,?,?,?)";

	@Override
	public String Subpackage(int va_id) {
		int barNum = 0;
		double packageqty = 0;
		double vadqty = 0;
		double remainQty = 0;
		double tqty = 0;
		Object vacode = baseDao.getFieldDataByCondition("VerifyApply", "va_code", "va_id=" + va_id);
		int count = baseDao.getCountByCondition("VerifyApplyDetailP", "vadp_vacode='" + vacode + "'");
		if (count > 0) {
			BaseUtil.showError("已经有过分装明细,如果需要重新分装请通过[清除分装明细]按钮先清除后再进行分装!");
		}
		SqlRowList rs = baseDao
				.queryForRowSet(
						"SELECT va_code,va_vendcode,va_vendname,vad_id,vad_detno,vad_qty,vad_unitpackage,vad_batchcode,vad_prodcode FROM VerifyApplyDetail left join VerifyApply on vad_vaid=va_id where vad_vaid=?",
						va_id);
		while (rs.next()) {
			packageqty = rs.getDouble("vad_unitpackage");
			vadqty = rs.getDouble("vad_qty");
			if (packageqty > 0 && vadqty > 0) {
				barNum = (int) (Math.ceil(vadqty / packageqty));
				remainQty = vadqty;
				for (int i = 1; i <= barNum; i++) {
					if (remainQty >= packageqty) {
						tqty = packageqty;
					} else {
						tqty = remainQty;
					}
					baseDao.execute(
							INSERTVERIFYAPPLYDETAILP,
							new Object[] { baseDao.getSeqId("VERIFYAPPLYDETAILP_SEQ"), rs.getInt("vad_id"), rs.getObject("va_code"),
									rs.getInt("vad_detno"), i, tqty, rs.getObject("va_vendcode"), rs.getObject("va_vendname"),
									rs.getObject("vad_prodcode"), rs.getObject("vad_batchcode") });
					remainQty = remainQty - tqty;
					if (remainQty <= 0) {
						break;
					}
				}
			}
		}
		return "分装确认成功!";
	}

	@Override
	public String ClearSubpackage(int va_id) {
		Object vacode = baseDao.getFieldDataByCondition("VerifyApply", "va_code", "va_id=" + va_id);
		baseDao.execute("delete from VerifyApplyDetailP where vadp_vacode='" + vacode + "'");
		return "清除分装明细成功!";
	}

	@Override
	public String[] printBar(int va_id, String reportName, String condition) {
		double vadpsumqty = 0;
		SqlRowList rs = baseDao
				.queryForRowSet(
						"SELECT vad_id, vad_code,vad_detno,vad_qty,vad_unitpackage FROM VerifyApplyDetail left join VerifyApply on vad_vaid=va_id where vad_vaid=?",
						va_id);
		while (rs.next()) {
			vadpsumqty = Double.parseDouble(baseDao.getFieldDataByCondition("VerifyApplyDetailP", "round(sum(vadp_qty),2)",
					"vadp_vadid=" + rs.getInt("vad_id")).toString());
			if (rs.getDouble(3) != vadpsumqty) {
				BaseUtil.showError("当前序号" + rs.getObject("vad_detno") + "的收料数量与分装明细总数不等,不能打印条码!");
			}
		}
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		return keys;
	}

	@Override
	public void ProduceBatch(int id, String caller) {
		List<Object[]> lists = baseDao.getFieldsDatasByCondition("verifyapplydetail", new String[] { "vad_id", "vad_batchcode" },
				"vad_vaid=" + id);
		for (Object[] data : lists) {
			if (data[1] == null || data[1].equals("")) {
				String batchcode = baseDao.getBatchcode("ProdInOut!PurcCheckin");
				baseDao.updateByCondition("verifyapplydetail", "vad_batchcode='" + batchcode + "'", "vad_id=" + data[0]);
			}
		}
		// 记录操作
		baseDao.logger.others("产生批号", "产生批号成功", caller, "va_id", id);
	}

	@Override
	public String generateBarcode(String caller, String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 表 verifyapplydetailp 字段
		int vad_qty = Integer.valueOf(store.get("vad_qty").toString());
		int vadp_qty = vad_qty;
		SqlRowList rs0 = baseDao.queryForRowSet("select nvl(SUM(nvl(vadp_qty,0)),0) suminqty from verifyapplydetailp where vadp_vacode='"
				+ store.get("vadp_vacode").toString() + "' and vadp_vaddetno=" + Integer.valueOf(store.get("vadp_vaddetno").toString()));
		if (rs0.next()) {
			vadp_qty = vad_qty - rs0.getInt("suminqty");
		}
		// 出入库单该明细行物料入库数总量大于批总量，不允许生成条码
		int rs = baseDao.getCount("select count(0) cn from verifyapplydetailp where vadp_vacode='" + store.get("vadp_vacode").toString()
				+ "' and vadp_vaddetno=" + Integer.valueOf(store.get("vadp_vaddetno").toString()) + " HAVING SUM(vadp_qty)>=" + vad_qty);
		if (rs > 0) {
			BaseUtil.showError("入库数的总和不允许超过了批总量，不允许操作");
		}
		insertBar(store, vadp_qty);
		baseDao.logger.others("生成条码", "生成条码成功", caller, "va_id", store.get("vad_id"));
		return null;
	}

	@Override
	public void batchGenBarcode(String caller, String formStore) {
		int bqty = 0;
		double aqty = 0;
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		double pr_zxbzs = Double.valueOf(store.get("pr_zxbzs").toString());
		double vadp_baqty = Double.valueOf(store.get("vadp_baqty").toString());// 批总量
		if (store.get("vadp_vacode") == null || store.get("vadp_vacode") == " ") {
			BaseUtil.showError("收料单明细行批号没有填写！");
		}
		Object[] obs = baseDao.getFieldsDataByCondition("VerifyApply left join VerifyApplyDetail on va_id=vad_vaid", new String[] {
				"va_statuscode", "to_char(va_indate,'yyyy-mm-dd')", "vad_batchcode" }, "va_code='" + store.get("vadp_vacode")
				+ "' and vad_id=" + store.get("vadp_vadid"));
		if (obs[0].equals("AUDITED")) {
			BaseUtil.showError("单据已审核不允许操作!");
		}
		// 采购收料单该明细行物料入库数总量[vad_qty]大于批总量[vadp_baqty]+条码数量，不允许生成条码
		int cn = baseDao.getCount("select count(0) cn from verifyapplydetailp where vadp_vacode='" + store.get("vadp_vacode").toString()
				+ "' and vadp_vaddetno=" + Integer.valueOf(store.get("vadp_vaddetno").toString()) + " HAVING SUM(vadp_qty)>"
				+ (Double.valueOf(store.get("vad_qty").toString()) - vadp_baqty));
		if (cn > 0) {
			BaseUtil.showError("入库数的总和超过来料总量[收料数量]，不允许操作");
		}

		if (store.get("vadp_madedate").equals("") || store.get("vadp_madedate") == null) {
			store.put("vadp_madedate", obs[1].toString());// 生产日期
		}
		store.put("vadp_indate", obs[1].toString());// 入库日期
		if (obs[2] != null) {
			store.put("vadp_batchcode", obs[2].toString());// 批号
		}
		Object ob = baseDao.getFieldDataByCondition("product", "pr_id", "pr_code='" + store.get("vadp_prodcode") + "'");
		store.put("vadp_prodid", ob.toString());// 物料ID

		if (store.get("vad_inbzs") != null && !store.get("vad_inbzs").equals("") && !store.get("vad_inbzs").toString().equals("")) {
			String[] ins = store.get("vad_inbzs").toString().split("#"); // 分割来料包装数
			// 计算来料包装数的总和
			double totalIns = 0;
			for (int i = 0; i < ins.length; i++) {
				totalIns = (new BigDecimal(ins[i])).add(new BigDecimal(Double.toString(totalIns))).doubleValue();
			}
			if (totalIns > vadp_baqty) {
				BaseUtil.showError("来料包装数的总和超过批总量，不允许操作");
			} else {
				for (int i = 0; i < ins.length; i++) {// 循环按照每个来料包装数生成，等于
					insertBar(store, Double.valueOf(ins[i]));
				}
				if (totalIns < vadp_baqty) {// 来料包装数总和小于批总量，按照最小包装数来生成条码
					double rest_qty = (new BigDecimal(Double.toString(vadp_baqty))).subtract(new BigDecimal(Double.toString(totalIns)))
							.doubleValue();
					bqty = (int) (rest_qty / pr_zxbzs);
					aqty = (new BigDecimal(Double.toString(rest_qty))).subtract(new BigDecimal(Double.toString(bqty * pr_zxbzs)))
							.doubleValue();
				}
			}
		} else {
			bqty = (int) (vadp_baqty / pr_zxbzs);
			aqty = (new BigDecimal(Double.toString(vadp_baqty))).subtract(new BigDecimal(Double.toString(bqty * pr_zxbzs))).doubleValue();
		}
		if (bqty >= 1) {
			for (int i = 0; i < bqty; i++) {
				insertBar(store, pr_zxbzs);
			}
		}
		if (aqty > 0) {
			insertBar(store, aqty);
		}
		baseDao.logger.others("批量生成条码", "批量生成条码成功", caller, "vad_id", store.get("vad_id"));
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveBarcodeDetail(String caller, String gridStore) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "verifyapplydetailp", "vadp_id");
		baseDao.execute(gridSql);
		SqlRowList rs = baseDao
				.queryForRowSet("select count(0) cn from VerifyApplyDetail left join VerifyApply on va_id=vad_vaid left join verifyapplydetailp on va_code=vadp_vacode  and vad_detno=vadp_vaddetno where vadp_vaddetno='"
						+ gstore.get(0).get("vadp_vaddetno").toString()
						+ "'and vadp_vacode='"
						+ gstore.get(0).get("vadp_vacode").toString() + "' having sum(vadp_qty)>vad_qty group by vad_qty");
		if (rs.next()) {
			if (rs.getInt("cn") > 0) {
				BaseUtil.showError("入库数总和不允许超过批总量！");
			}
		}
		baseDao.logger.others("保存修改条码", "成功", caller, "vad_id", gstore.get(0).get("vadp_vadid"));
	}

	private void insertBar(Map<Object, Object> store, double inqty) {
		Object[] obs = baseDao.getFieldsDataByCondition("Vendor left join VerifyApply on va_vendcode=ve_code OR va_vendname=ve_code",
				new String[] { "ve_id", "ve_code", "ve_name" }, "va_code='" + store.get("vadp_vacode") + "'");
		String bar_code = verifyApplyDao.barcodeMethod(store.get("vadp_prodcode").toString(), obs[0].toString(), 0);
		int vadp_id = baseDao.getSeqId("VERIFYAPPLYDETAILP_SEQ");
		// 生成verifyapplydetailp
		store.put("vadp_id", vadp_id);
		store.put("vadp_qty", inqty);
		store.put("vadp_barcode", bar_code);
		store.put("vadp_printstatus", "0");
		store.put("vadp_vendcode", obs[1].toString());
		store.put("vadp_vendname", obs[2].toString());
		store.remove("vad_qty");
		store.remove("vad_inbzs");
		store.remove("vadp_pkqty");
		store.remove("vadp_baqty");
		store.remove("pr_zxbzs");
		store.remove("vad_qty");
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "verifyapplydetailp", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
	}

	@Override
	public void deleteAllBarDetails(String caller, String code, int detno) {
		Object status = baseDao.getFieldDataByCondition("VerifyApply ", "va_statuscode", "va_code='" + code + "'");
		if (status.equals("AUDITED")) {
			BaseUtil.showError("单据已审核不允许删除!");
		}
		baseDao.deleteByCondition("package",
				"pa_outboxcode in (select distinct vadp_outboxcode from VerifyApplyDetailP left join VerifyApply on va_code=vadp_vacode  where va_code='"
						+ code + "' and vadp_vaddetno='" + detno + "')");
		baseDao.deleteByCondition("packagedetail",
				"pd_outboxcode in (select distinct vadp_outboxcode from VerifyApplyDetailP  where vadp_vacode='" + code
						+ "' and vadp_vaddetno='" + detno + "')");
		baseDao.deleteByCondition("VerifyApplyDetailP", "vadp_vacode='" + code + "' and vadp_vaddetno='" + detno + "'");
		// baseDao.logger.others("删除所有明细", "成功", caller, "vad_id", id);
	}

	@Override
	public void batchGenBO(String caller, String formStore) {// 批量生成条码和箱号
		int bqty = 0, pa_id = 0;
		Double aqty;
		Object ob;
		String out_boxcode = null, bar_code;
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Double pr_zxbzs = Double.valueOf(store.get("pr_zxbzs").toString());
		Double vadp_baqty = Double.valueOf(store.get("vadp_baqty").toString());
		if (store.get("vadp_vacode") == null || store.get("vadp_vacode") == " ") {
			BaseUtil.showError("收料单明细行批号没有填写！");
		}
		Object[] obs = baseDao.getFieldsDataByCondition("VerifyApply left join VerifyApplyDetail on va_id=vad_vaid", new String[] {
				"va_statuscode", "to_char(va_indate,'yyyy-mm-dd')", "vad_batchcode" }, "va_code='" + store.get("vadp_vacode")
				+ "' and vad_id=" + store.get("vadp_vadid"));
		if (obs[0].equals("AUDITED")) {
			BaseUtil.showError("单据已审核不允许操作!");
		}
		// 采购收料单该明细行物料入库数总量[vad_qty]大于批总量[vadp_baqty]，不允许生成条码
		int cn = baseDao.getCount("select count(0) cn from verifyapplydetailp where vadp_vacode='" + store.get("vadp_vacode").toString()
				+ "' and vadp_vaddetno=" + Integer.valueOf(store.get("vadp_vaddetno").toString()) + " HAVING SUM(vadp_qty)>"
				+ (Double.valueOf(store.get("vad_qty").toString()) - vadp_baqty));
		if (cn > 0) {
			BaseUtil.showError("入库数的总和允许超过了来料总量[收料数量]，不允许操作");
		}
		// 供应商ID
		Object ve_id = baseDao.getFieldDataByCondition("Vendor left join VerifyApply on va_vendcode=ve_code", "ve_id",
				"va_code='" + store.get("vadp_vacode") + "'");
		if (obs[2] != null) {
			store.put("vadp_batchcode", obs[2].toString());// 批号
		}
		// 物料ID
		ob = baseDao.getFieldDataByCondition("product", "pr_id", "pr_code='" + store.get("vadp_prodcode") + "'");
		store.put("vadp_prodid", ob.toString());
		// 获取盘盈单明细行入库日期或者单据日期,一般情况下是一致的
		ob = baseDao.getFieldDataByCondition("VerifyApply left join VerifyApplyDetail  on va_id=vad_vaid",
				"nvl(to_char(ve_indate,'yyyy-mm-dd'),to_char(va_indate,'yyyy-mm-dd')) va_indate", "va_code='"
						+ store.get("vadp_vacode").toString() + "'");
		store.put("vadp_indate", ob.toString());// 入库日期
		if (store.get("vadp_madedate").equals("") && store.get("vadp_madedate") == null) {
			store.put("vadp_madedate", ob.toString());// 生产日期
		}
		bqty = (int) (vadp_baqty / pr_zxbzs);// 条码数量是最小包装数的条数
		aqty = (new BigDecimal(Double.toString(vadp_baqty))).subtract(new BigDecimal(Double.toString(bqty * pr_zxbzs))).doubleValue();// 条码零头

		Double pk_qty = Double.valueOf(store.get("vadp_pkqty").toString()); // 箱内总数
		int bqtyB = 0;
		bqtyB = (int) (vadp_baqty / pk_qty); // 整数箱数
		Double aqtyB = (new BigDecimal(Double.toString(vadp_baqty))).subtract(new BigDecimal(Double.toString(bqtyB * pk_qty)))
				.doubleValue();// 条码零头
		int sumJ = (int) (pk_qty / Double.valueOf(store.get("pr_zxbzs").toString()));// 每箱件数

		if (bqty >= 1) {
			for (int i = 0; i < bqty; i++) {
				if (i % sumJ == 0) {
					out_boxcode = verifyApplyDao.outboxMethod(store.get("vadp_prodid").toString(), "2");
					pa_id = baseDao.getSeqId("PACKAGE_SEQ");
					if (i >= bqty - sumJ && aqtyB > 0) { // 生成箱号
						baseDao.execute("insert into package (pa_id,pa_outboxcode,pa_prodcode,pa_packdate,pa_packageqty,pa_totalqty,pa_status,pa_indate)values("
								+ pa_id
								+ ",'"
								+ out_boxcode
								+ "','"
								+ store.get("vadp_prodcode")
								+ "',sysdate,'"
								+ (bqty - sumJ)
								+ "','"
								+ aqtyB + "','0',to_date('" + ob.toString() + "','yyyy-MM-dd'))");
					} else {
						baseDao.execute("insert into package (pa_id,pa_outboxcode,pa_prodcode,pa_packdate,pa_packageqty,pa_totalqty,pa_status,pa_indate)values("
								+ pa_id
								+ ",'"
								+ out_boxcode
								+ "','"
								+ store.get("vadp_prodcode")
								+ "',sysdate,'"
								+ sumJ
								+ "','"
								+ pk_qty
								+ "','0',to_date('" + ob.toString() + "','yyyy-MM-dd'))");
					}
				}
				bar_code = verifyApplyDao.barcodeMethod(store.get("vadp_prodcode").toString(), ve_id.toString(), 0);
				insertBarPd(caller, pa_id, out_boxcode, pr_zxbzs, bar_code, store);
			}
		}
		if (aqty > 0) {
			bar_code = verifyApplyDao.barcodeMethod(store.get("bdd_prodcode").toString(), ve_id.toString(), 0);
			insertBarPd(caller, pa_id, out_boxcode, aqty, bar_code, store);
		}
		baseDao.logger.others("批量生成条码和箱号", "批量生成条码和箱号成功", caller, "va_id", store.get("vad_id"));
	}

	private void insertBarPd(String caller, int pa_id, String out_boxcode, Double qty, String bar_code, Map<Object, Object> store) {
		List<String> sqls = new ArrayList<String>();
		Object[] obs = baseDao.getFieldsDataByCondition("Vendor left join VerifyApply on va_vendcode=ve_code OR va_vendname=ve_code",
				new String[] { "ve_code", "ve_name" }, "va_code='" + store.get("vadp_vacode") + "'");
		int vadp_id = baseDao.getSeqId("VERIFYAPPLYDETAILP_SEQ");
		// 生成verifyapplydetailp
		store.put("vadp_id", vadp_id);
		store.put("vadp_qty", qty);
		store.put("vadp_barcode", bar_code);
		store.put("vadp_printstatus", "0");
		store.put("vadp_vendcode", obs[0].toString());
		store.put("vadp_vendname", obs[1].toString());
		store.remove("vad_qty");
		store.remove("vadp_pkqty");
		store.remove("vadp_baqty");
		store.remove("pr_zxbzs");
		store.remove("vad_qty");
		store.remove("vad_inbzs");
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "verifyapplydetailp", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		sqls.add("insert into packageDetail (pd_id,pd_paid,pd_outboxcode,pd_barcode,pd_innerqty) values ("
				+ baseDao.getSeqId("PACKAGEDETAIL_SEQ") + "," + pa_id + ",'" + out_boxcode + "','" + bar_code + "','" + qty + "')");
		sqls.add("update VerifyApplyDetailP set vadp_outboxcode='" + out_boxcode + "',vadp_outboxid=" + pa_id + " where vadp_id=" + vadp_id);
		baseDao.execute(sqls);
	}

	// 收料单主表收货工厂与明细采购单收货工厂不一致，不允许提交审核
	private void factoryCheck(Object va_id) {
		if (baseDao.isDBSetting("VerifyApply", "mrpSeparateFactory")) {
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select WM_CONCAT(vad_detno) from VERIFYAPPLYDETAIL left join VERIFYAPPLY on vad_vaid=va_id left join Purchasedetail on vad_pucode=pd_code and vad_pudetno=pd_detno where vad_vaid=? and va_class='采购收料单' and nvl(va_factory,' ')<>' ' and nvl(va_factory,' ')<>nvl(pd_factory,' ')",
							String.class, va_id);
			if (dets != null) {
				BaseUtil.showError("明细行采购单收货工厂与当前单主表收货工厂不一致，不允许进行当前操作！行号：" + dets);
			}
		}
	}

	/**
	 * 收料单拆分
	 */
	@Override
	public void splitVerifyApply(String formdata, String data, String caller) {
		Map<Object, Object> formmap = BaseUtil.parseFormStoreToMap(formdata);
		int vad_id = Integer.parseInt(formmap.get("vad_id").toString());
		int vad_vaid = Integer.parseInt(formmap.get("vad_vaid").toString());
		Double va_rate = baseDao.getJdbcTemplate().queryForObject("select va_rate from VERIFYAPPLY where va_id=?", Double.class, vad_vaid);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(vad_detno) from VERIFYAPPLYDETAIL where vad_vaid=?"
								+ " and exists (select 1 from VerifyApplyChange left join VerifyApplyChangedetail on vc_id=vcd_vcid where vcd_vadid=vad_id and vc_statuscode in ('COMMITED','ENTERING'))",
						String.class, vad_id);
		if (dets != null) {
			BaseUtil.showError("收料单存在未审核的收料变更单，不允许拆分!行号：" + dets);
		}
		String SQLStr = "";
		int basedetno = Integer.parseInt(formmap.get("vad_detno").toString());
		Map<String, Object> currentMap = new HashMap<String, Object>();
		Object vad_qty = null;
		SqlRowList cur = baseDao.queryForRowSet("select * from VERIFYAPPLYDETAIL where vad_id=" + vad_id);
		if (cur.next()) {
			currentMap = cur.getCurrentMap();
			vad_qty = cur.getDouble("vad_qty");
		} else
			BaseUtil.showError("原始明细已不存在!无法拆分!");
		SqlRowList sl = baseDao.queryForRowSet("select max(vad_detno) from VERIFYAPPLYDETAIL where vad_vaid=" + vad_vaid);
		int newdetno = 0;
		if (sl.next()) {
			newdetno = sl.getInt(1) == -1 ? basedetno + 1 : sl.getInt(1);
		}
		List<Map<Object, Object>> gridmaps = BaseUtil.parseGridStoreToMaps(data);
		Map<Object, Object> map = new HashMap<Object, Object>();
		Object sdid = null;
		int pdqty = 0;
		int pddetno = 0;
		int sumqty = 0;
		SqlRowList sl2 = null;
		for (int i = 0; i < gridmaps.size(); i++) {
			sumqty = sumqty + Integer.parseInt(gridmaps.get(i).get("vad_qty").toString());
		}
		if (sumqty != Integer.parseInt(currentMap.get("vad_qty").toString())) {
			BaseUtil.showError("拆分后总数必须保持一致!");
		}
		Object newpd_qty = null;
		for (int i = 0; i < gridmaps.size(); i++) {
			map = gridmaps.get(i);
			sdid = map.get("vad_id");
			pddetno = Integer.parseInt(map.get("vad_detno").toString());
			pdqty = Integer.parseInt(map.get("vad_qty").toString());
			if (sdid != null && Integer.parseInt(sdid.toString()) != 0) {
				newpd_qty = pdqty;
				/* newpd_deliveryreply = map.get("pd_deliveryreply"); */
				sl2 = baseDao
						.queryForRowSet("select vad_qty,NVL(vad_yqty,0)vad_yqty,vad_code,vad_detno from VERIFYAPPLYDETAIL where vad_id="
								+ sdid);
				if (sl2.next()) {
					SQLStr = "update VERIFYAPPLYDETAIL set vad_qty=" + pdqty + ",vad_ordertotal=round(vad_orderprice*" + pdqty
							+ ",2),vad_plancode=round(((vad_orderprice*" + pdqty + ")/" + va_rate + ")*" + pdqty
							+ ",2),vad_total=round(vad_price*" + pdqty + ",2)" + " where vad_id=" + sdid;
					baseDao.execute(SQLStr);
					baseDao.execute("update VerifyApplydetail set vad_barcode=round(vad_total-vad_plancode,2) where vad_vaid=?", sdid);
					// 更新仓库信息
					if (map.get("vad_whcode") != null && !"".equals(map.get("vad_whcode"))) {
						baseDao.execute("update VERIFYAPPLYDETAIL set  vad_whcode='" + map.get("vad_whcode").toString() + "',vad_whname='"
								+ map.get("vad_whname").toString() + "' where vad_vaid=" + vad_vaid + "");
					}
				} else {
					BaseUtil.showError("序号 :[" + pddetno + "] ，明细数据已经不存在，不能拆分!");
				}

			} else {
				boolean bool = true;
				while (bool) {
					newdetno++;
					bool = baseDao.checkIf("VERIFYAPPLYDETAIL", "vad_vaid=" + vad_vaid + " AND vad_detno=" + newdetno);
					if (!bool)
						break;
				}
				currentMap.remove("vad_whcode");
				currentMap.remove("vad_whname");
				currentMap.remove("vad_batchcode");
				currentMap.put("vad_batchcode", map.get("vad_batchcode").toString());
				currentMap.remove("vad_detno");
				currentMap.put("vad_detno", newdetno);
				currentMap.remove("vad_id");
				currentMap.put("vad_id", baseDao.getSeqId("VERIFYAPPLYDETAIL_SEQ"));
				currentMap.remove("vad_qty");
				currentMap.put("vad_qty", pdqty);
				currentMap.remove("vad_yqty");
				currentMap.put("vad_yqty", 0);
				currentMap.remove("ve_okqty");
				currentMap.put("ve_okqty", 0);
				currentMap.remove("ve_notokqty");
				currentMap.put("ve_notokqty", 0);
				currentMap.remove("vad_ordertotal");
				currentMap.put("vad_ordertotal",
						NumberUtil.formatDouble(pdqty * Double.parseDouble(currentMap.get("vad_orderprice").toString()), 2));
				currentMap.remove("vad_plancode");
				currentMap.put("vad_plancode",
						NumberUtil.formatDouble(pdqty * (Double.parseDouble(currentMap.get("vad_orderprice").toString()) / va_rate), 2));
				baseDao.execute(SqlUtil.getInsertSqlByMap(currentMap, "VERIFYAPPLYDETAIL"));
				if (map.get("vad_whcode") != null) {
					baseDao.execute("update VERIFYAPPLYDETAIL set  vad_whcode='" + map.get("vad_whcode").toString() + "',vad_whname='"
							+ map.get("vad_whname").toString() + "' where vad_vaid=" + vad_vaid + " and vad_detno=" + newdetno);
				}
			}

		}
		/**
		 * 问题反馈单号：2017010110 处理：修改记录日志
		 * 
		 * @author wsy
		 */
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "订单拆分", "明细行:" + basedetno + "=>被拆分,原数量：" + vad_qty
				+ ";新数量:" + newpd_qty + "", "VerifyApply|va_id=" + vad_vaid));
	}
}
