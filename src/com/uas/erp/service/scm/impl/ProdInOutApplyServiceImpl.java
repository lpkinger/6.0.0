package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.model.Key;
import com.uas.erp.service.scm.ProdInOutApplyService;


@Service("prodInOutApplyService")
public class ProdInOutApplyServiceImpl implements ProdInOutApplyService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private TransferRepository transferRepository;
	
	@Override
	public void saveProdInOutApply(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ProdInOutApply", "pi_code='" + store.get("pi_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store, grid});
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "ProdInOutApply"));
		baseDao.execute(SqlUtil.getInsertSqlbyList(grid, "ProdIOApplyDetail", "pd_id"));
		Object pi_id = store.get("pi_id");
		baseDao.execute("update ProdIOApplyDetail set (pd_code,pd_piclass)=(select pi_code,pi_class from prodinoutapply where pd_piid=pi_id) where pd_piid=" + pi_id);
		baseDao.execute("update prodioApplydetail set (pd_whcode,pd_whname)=(select pi_whcode,pi_whname from prodinoutApply where pd_piid=pi_id) where pd_piid="
				+ pi_id + " and NVL(pd_whcode,' ')=' '");
		baseDao.execute("update prodioApplydetail set pd_prodmadedate=(select pi_date from prodinoutApply where pd_piid=pi_id) where pd_piid="
				+ pi_id + " and pd_prodmadedate is null");
		baseDao.updateByCondition("ProdIOApplyDetail",
				"pd_orderprice=(select pd_price from PurchaseDetail where pd_code=pd_ordercode and pd_detno=pd_orderdetno)", "pd_piid="
						+ pi_id + " and nvl(pd_orderprice,0)=0");
		baseDao.updateByCondition(
				"ProdIOApplyDetail",
				"pd_ordertotal=round(nvl(pd_orderprice,0)*nvl(pd_outqty,0),2), pd_total=round(nvl(pd_price,0)*nvl(pd_outqty,0),2)",
				"pd_piid=" + pi_id);
		baseDao.updateByCondition("ProdIOApplyDetail",
				"pd_taxtotal=round(pd_orderprice*pd_outqty*nvl(pd_taxrate,0)/(100+nvl(pd_taxrate,0)),2)", "pd_piid=" + pi_id);
		baseDao.updateByCondition("ProdInOutApply",
				"pi_total=(SELECT round(sum(nvl(pd_orderprice,0)*nvl(pd_outqty,0)),2) FROM ProdIOApplyDetail WHERE pd_piid="
						+ pi_id + ")", "pi_id=" + pi_id);
		baseDao.logger.save(caller, "pi_id", store.get("pi_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store, grid});
	}
	
	@Override
	public void deleteProdInOutApply(int pi_id, String caller) {
		//只能删除在录入的单据!
		Object[] status = baseDao.getFieldsDataByCondition("ProdInOutApply", new String[]{"pi_statuscode","pi_code"}, "pi_id=" + pi_id);
		StateAssert.delOnlyEntering(status[0]);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, pi_id);
		//删除ProdInOutApply
		baseDao.deleteById("ProdInOutApply", "pi_id", pi_id);
		//删除ProdIOApplyDetail
		baseDao.deleteById("ProdIOApplyDetail", "pd_piid", pi_id);
		//记录操作
		baseDao.logger.delete(caller, "pi_id", pi_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, pi_id);
	}
	
	@Override
	public void updateProdInOutApplyById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object[] status = baseDao.getFieldsDataByCondition("ProdInOutApply", new String[]{"pi_statuscode","pi_cardcode"}, "pi_id=" + store.get("pi_id"));
		StateAssert.updateOnlyEntering(status[0]);	
		Object pi_id = store.get("pi_id");
		Object pi_cardcode = store.get("pi_cardcode");
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store, gstore});
		// 修改ProductSMT
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "ProdInOutApply", "pi_id"));
		// 修改ProductSMTDetail
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "ProdIOApplyDetail", "pd_id"));
		baseDao.execute("update ProdIOApplyDetail set (pd_code,pd_piclass)=(select pi_code,pi_class from prodinoutapply where pd_piid=pi_id) where pd_piid=" + pi_id);
		baseDao.execute("update prodioApplydetail set (pd_whcode,pd_whname)=(select pi_whcode,pi_whname from prodinoutApply where pd_piid=pi_id) where pd_piid="
				+ pi_id + " and NVL(pd_whcode,' ')=' '");
		baseDao.execute("update prodioApplydetail set pd_prodmadedate=(select pi_date from prodinoutApply where pd_piid=pi_id) where pd_piid="
				+ pi_id + " and pd_prodmadedate is null");
		baseDao.updateByCondition("ProdIOApplyDetail",
				"pd_orderprice=(select pd_price from PurchaseDetail where pd_code=pd_ordercode and pd_detno=pd_orderdetno)", "pd_piid="
						+ pi_id + " and nvl(pd_orderprice,0)=0");
		baseDao.updateByCondition(
				"ProdIOApplyDetail",
				"pd_ordertotal=round(nvl(pd_orderprice,0)*nvl(pd_outqty,0),2), pd_total=round(nvl(pd_price,0)*nvl(pd_outqty,0),2)",
				"pd_piid=" + pi_id);
		baseDao.updateByCondition("ProdIOApplyDetail",
				"pd_taxtotal=round(pd_orderprice*pd_outqty*nvl(pd_taxrate,0)/(100+nvl(pd_taxrate,0)),2)", "pd_piid=" + pi_id);
		baseDao.updateByCondition("ProdInOutApply",
				"pi_total=(SELECT round(sum(nvl(pd_orderprice,0)*nvl(pd_outqty,0)),2) FROM ProdIOApplyDetail WHERE pd_piid="
						+ pi_id + ")", "pi_id=" + pi_id);
		//记录操作
		baseDao.logger.update(caller, "pi_id", pi_id);
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store, gstore});
		if(pi_cardcode != null && !"".equals(pi_cardcode) && status[1] != null && !"".equals(status[1])){
			if(!pi_cardcode.equals(status[1])){
				BaseUtil.appendError("供应商已变更，请重新抓取价格！");
			}
		}
	}
	
	@Override
	public void auditProdInOutApply(int pi_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ProdInOutApply", "pi_statuscode", "pi_id=" + pi_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, pi_id);
		//执行审核操作
		baseDao.audit("ProdInOutApply", "pi_id=" + pi_id, "pi_status", "pi_statuscode", "pi_auditdate", "pi_auditman");
		//记录操作
		baseDao.logger.audit(caller, "pi_id", pi_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, pi_id);
	}
	
	@Override
	public void resAuditProdInOutApply(int pi_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ProdInOutApply", "pi_statuscode", "pi_id=" + pi_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resAudit("ProdInOutApply", "pi_id=" + pi_id, "pi_status", "pi_statuscode", "pi_auditdate", "pi_auditman");
		//记录操作
		baseDao.logger.resAudit(caller, "pi_id", pi_id);
	}
	
	@Override
	public void submitProdInOutApply(int pi_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("ProdInOutApply", new String[]{"pi_statuscode","pi_code"}, "pi_id=" + pi_id);
		StateAssert.submitOnlyEntering(status[0]);
		baseDao.execute("update ProdIOApplyDetail set (pd_code,pd_piclass)=(select pi_code,pi_class from prodinoutapply where pd_piid=pi_id) where pd_piid=" + pi_id);
		baseDao.execute("update prodioApplydetail set (pd_whcode,pd_whname)=(select pi_whcode,pi_whname from prodinoutApply where pd_piid=pi_id) where pd_piid="
				+ pi_id + " and NVL(pd_whcode,' ')=' '");
		baseDao.execute("update prodioApplydetail set pd_prodmadedate=(select pi_date from prodinoutApply where pd_piid=pi_id) where pd_piid="
				+ pi_id + " and pd_prodmadedate is null");
		String dets = null;
		if (baseDao.isDBSetting("warehouseCheck")) {
			// 出入库单主记录与明细行仓库必须一致
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIOApplyDetail left join ProdInOutApply on pd_piid=pi_id where pi_id=? and nvl(pi_whcode,' ')<>nvl(pd_whcode,' ') ",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细行仓库与当前单主表仓库不一致，不允许进行当前操作！行号：" + dets);
			}
		}
		if("ProdInOutApply!WWYT".equals(caller)){
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat('行:'||pd_pdno||',仓库:'||pd_whcode) from prodioApplydetail left join warehouse on wh_code=pd_whcode where pd_piid=? and nvl(pd_whcode,' ')<>' ' and nvl(wh_statuscode,' ')='DISABLE'",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("仓库已禁用，不允许进行当前操作:" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat('行:'||pd_pdno||',仓库:'||pd_whcode) from prodioApplydetail left join warehouse on wh_code=pd_whcode where pd_piid=? and nvl(pd_whcode,' ')<>' ' and pd_whcode not in (select wh_code from warehouse)",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("仓库不存在，不允许进行当前操作:" + dets);
			}
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(pd_pdno) from ProdIOApplyDetail left join ProdinoutApply on pd_piid=pi_id left join make on ma_code=pd_ordercode where "
							+ "pd_piid=? and nvl(pi_cardcode,' ')<>nvl(ma_vendcode,' ') and pd_piclass in ('委外验退申请单') "
							+ "and nvl(pd_ordercode,' ')<>' '", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细委外单委外商与单据委外商不一致，不允许进行当前操作!行号：" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIOApplyDetail where pd_piid=? and pd_piclass in ('委外验退申请单') and not exists (select 1 from make where pd_ordercode=ma_code and pd_prodcode=ma_prodcode)",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("委外单+物料编号不存在，不允许进行当前操作！行号：" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIOApplyDetail left join ProdinoutApply on pd_piid=pi_id left join Make on ma_code=pd_ordercode where pd_piid=? and nvl(pi_currency,' ')<>nvl(ma_currency,' ')"
									+ " and pd_piclass in ('委外验退申请单') and nvl(pd_ordercode,' ')<>' '", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细委外单与单据币别不一致，不允许进行当前操作!行号：" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIOApplyDetail left join Make on ma_code=pd_ordercode left join warehouse on pd_whcode=wh_code where pd_piid=? and nvl(ma_bonded,0)<>nvl(wh_bonded,0)"
									+ " and pd_piclass in ('委外验退申请单') and nvl(pd_ordercode,' ')<>' '", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("委外单的保税属性与仓库的保税属性不一致，不允许进行当前操作!行号：" + dets);
			}
		}
		if("ProdInOutApply!CGYT".equals(caller)){
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIOApplyDetail where pd_piid=?"
									+ " and pd_piclass in ('采购验退申请单') and nvl(pd_ordercode,' ')<>' ' and  not exists (select pd_code,pd_detno from purchasedetail where purchasedetail.pd_code=ProdIOApplyDetail.pd_ordercode and purchasedetail.pd_detno=ProdIOApplyDetail.pd_orderdetno)", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("采购单号+采购序号不存在，不允许进行当前操作!行号：" + dets);
			}
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(pd_pdno) from ProdIOApplyDetail left join PurchaseDetail on pd_ordercode=purchasedetail.pd_code and pd_orderdetno=purchasedetail.pd_detno where "
							+ "pd_piid=? and nvl(purchasedetail.pd_mrpstatuscode,' ') in ('FREEZE','FINISH') and pd_piclass in ('采购验退申请单') "
							+ "and nvl(pd_ordercode,' ')<>' '", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("采购单明细已冻结或者已结案，不允许进行当前操作!行号：" + dets);
			}
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(pd_pdno) from ProdIOApplyDetail left join ProdinoutApply on pd_piid=pi_id left join Purchase on pu_code=pd_ordercode where "
							+ "pd_piid=? and nvl(pi_cardcode,' ')<>nvl(pu_vendcode,' ') and pd_piclass in ('采购验退申请单') "
							+ "and nvl(pd_ordercode,' ')<>' '", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细采购单与单据供应商不一致，不允许进行当前操作!行号：" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIOApplyDetail left join ProdinoutApply on pd_piid=pi_id left join Purchase on pu_code=pd_ordercode where "
									+ "pd_piid=? and nvl(pi_receivecode,' ')<>nvl(pu_receivecode,' ') and pd_piclass in ('采购验退申请单') and nvl(pd_ordercode,' ')<>' '",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细采购单与单据应付供应商不一致，不允许进行当前操作!行号：" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIOApplyDetail left join ProdinoutApply on pd_piid=pi_id left join Purchase on pu_code=pd_ordercode where pd_piid=? and nvl(pi_currency,' ')<>nvl(pu_currency,' ')"
									+ " and pd_piclass in ('采购验退申请单') and nvl(pd_ordercode,' ')<>' '", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细采购单与单据币别不一致，不允许进行当前操作!行号：" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIOApplyDetail left join PurchaseDetail on purchasedetail.pd_code=pd_ordercode and purchasedetail.pd_detno=pd_orderdetno left join warehouse on ProdIOApplyDetail.pd_whcode=wh_code where pd_piid=? and nvl(PurchaseDetail.pd_bonded,0)<>nvl(wh_bonded,0)"
									+ " and pd_piclass in ('采购验退申请单') and nvl(pd_ordercode,' ')<>' '", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("采购单的保税属性与仓库的保税属性不一致，不允许进行当前操作!行号：" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIOApplyDetail left join PurchaseDetail on purchasedetail.pd_code=pd_ordercode "
							+ "and purchasedetail.pd_detno=pd_orderdetno where pd_piid=? and nvl(PurchaseDetail.pd_acceptqty,0)<nvl(pd_outqty,0)  "
							+ "and pd_piclass in ('采购验退申请单') and nvl(pd_ordercode,' ')<>' '", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("数量超出采购单验收数量!行号：" + dets);
			}
		}
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, pi_id);
		//执行提交操作
		baseDao.submit("ProdInOutApply", "pi_id=" + pi_id, "pi_status", "pi_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "pi_id", pi_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, pi_id);
	}
	
	@Override
	public void resSubmitProdInOutApply(int pi_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ProdInOutApply", "pi_statuscode", "pi_id=" + pi_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, pi_id);
		//执行反提交操作
		baseDao.resOperate("ProdInOutApply", "pi_id=" + pi_id, "pi_status", "pi_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "pi_id", pi_id);
		handlerService.afterResSubmit(caller, pi_id);
	}

	/**
	 * 验退申请单转验退单
	 * 
	 * @author mad
	 */
	@Override
	public String applyTurnProdIO(String caller, String data, String type) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String piclass = null;
		String tocaller = null;
		if ("ProdInOut!PurcCheckout".equals(type)) {
			piclass = "采购验退单";
			tocaller = "Apply!ToProdPurcOut";
		} else if ("ProdInOut!OutesideCheckReturn".equals(type)) {
			piclass = "委外验退单";
			tocaller = "Apply!ToProdOSOut";
		}
		StringBuffer sb = new StringBuffer();
		Object[] objs = null;
		String log = null;
		Object id = null;
		String code = null;
		for (Map<Object, Object> map : maps) {
			int pdid = Integer.parseInt(map.get("pd_id").toString());
			double tqty = Double.parseDouble(map.get("pd_tqty").toString());
			objs = baseDao.getFieldsDataByCondition(
					"ProdIOApplyDetail",
					new String[] { "pd_code", "pd_pdno", "pd_yqty",
							"pd_outqty" }, "pd_id=" + pdid
							+ " AND nvl(pd_yqty, 0)+" + tqty + ">pd_outqty");
			if (objs != null) {
				sb.append("申请单号:" + objs[0] + ",行号:" + objs[1] + ",申请单数量:"
						+ objs[3] + ",无法转出.已转" + type + "数量:" + objs[2]
						+ ",本次数量:" + tqty + "<hr/>");
				maps.remove(map);
				continue;
			}
		}
		if (maps.size() > 0) {
			Integer piid = baseDao.getFieldValue("ProdIOApplyDetail",
					"pd_piid", "pd_id=" + maps.get(0).get("pd_id"),
					Integer.class);
			int pi_id = 0;
			Key key = transferRepository.transfer(tocaller, piid);
			pi_id = key.getId();
			id = piid;
			code = key.getCode();
			transferRepository.transfer(tocaller, maps, key);
			log = piclass + "号:"
					+ "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
					+ pi_id + "&gridCondition=pd_piidIS" + pi_id
					+ "&whoami="+type+"')\">" + key.getCode()
					+ "</a>&nbsp;";
			baseDao.updateByCondition("ProdIOApplyDetail",
					"pd_orderprice=(select pd_price from PurchaseDetail where pd_code=pd_ordercode and pd_detno=pd_orderdetno)", "pd_piid="
							+ pi_id + " and nvl(pd_orderprice,0)=0");
			baseDao.updateByCondition(
					"ProdIOApplyDetail",
					"pd_ordertotal=round(nvl(pd_orderprice,0)*nvl(pd_outqty,0),2), pd_total=round(nvl(pd_price,0)*nvl(pd_outqty,0),2)",
					"pd_piid=" + pi_id);
			baseDao.updateByCondition("ProdIOApplyDetail",
					"pd_taxtotal=round(pd_orderprice*pd_outqty*nvl(pd_taxrate,0)/(100+nvl(pd_taxrate,0)),2)", "pd_piid=" + pi_id);
			baseDao.updateByCondition("ProdInOutApply",
					"pi_total=(SELECT round(sum(nvl(pd_orderprice,0)*nvl(pd_outqty,0)),2) FROM ProdIOApplyDetail WHERE pd_piid="
							+ pi_id + ")", "pi_id=" + pi_id);
			//转验退单后配置
			handlerService.handler("ProdInOutApply!CGYT", "turn", "after",new Object[] { pi_id });
		}
		handlerService.handler(caller, "turnProdIO", "after",
				new Object[] { maps });
				
		//生成日志
		baseDao.logger.others("转"+piclass, "转入成功,单号:"+code, "ProdInOutApply!CGYT", "pi_id", id);
		return "转入成功<hr>" + log;
	}
}
