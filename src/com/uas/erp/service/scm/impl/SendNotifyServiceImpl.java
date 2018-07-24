package com.uas.erp.service.scm.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.SendNotifyDao;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.scm.SendNotifyService;

@Service("sendNotifyService")
public class SendNotifyServiceImpl implements SendNotifyService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private SendNotifyDao sendNotifyDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveSendNotify(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("SendNotify", "sn_code='" + store.get("sn_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, grid });
		// 保存SendNotify
		String formSql = SqlUtil.getInsertSqlByMap(store, "SendNotify");
		baseDao.execute(formSql);
		// //保存SendNotifyDetail
		StringBuffer sb = new StringBuffer();
		for (Map<Object, Object> s : grid) {
			if (StringUtil.hasText(s.get("snd_ordercode")) && StringUtil.hasText(s.get("snd_orderdetno"))) {
				sendNotifyDao.restoreSaleYqty(Double.parseDouble(s.get("snd_outqty").toString()), s.get("snd_ordercode").toString(),
						Integer.valueOf(s.get("snd_orderdetno").toString()));
			}
			if ((s.get("snd_sendprice") == null || "0".equals(s.get("snd_sendprice")))
					&& (s.get("snd_discountprice") == null || "0".equals(s.get("snd_discountprice")))) {
				sb.append("行号:" + s.get("snd_pdno") + "销售单价，报关/折扣价都为0<hr/>");
			}
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "SendNotifyDetail", "snd_id");
		gridSql.add("update SendNotifyDetail set snd_sdid=nvl((select sd_id from saledetail left join sale on sa_id=sd_said where sa_code=snd_ordercode and sd_detno=snd_orderdetno),0) where snd_snid="
				+ store.get("sn_id") + " and snd_ordercode is not null and nvl(snd_orderdetno, 0)<>0");
		baseDao.execute(gridSql);
		getTotal(store.get("sn_id"));
		baseDao.execute(
				"update sendnotifydetail set (SND_CUSTPRODCODE,SND_CUSTPRODSPEC,snd_custproddetail)=(select max(pc_custprodcode),max(pc_custprodspec),max(pc_custproddetail) from ProductCustomer left join Product on pc_prodid=pr_id left join customer on pc_custid=cu_id where cu_code=? and snd_prodcode=pr_code) where nvl(snd_custprodcode,' ')=' ' and snd_snid=?",
				store.get("sn_custcode"), store.get("sn_id"));
		useDefaultTax(caller, store.get("sn_id"));
		sendnotify_commit_minus(store.get("sn_id"));
		updateWare(store.get("sn_id"),store.get("sn_warehousecode"));
		baseDao.logger.save(caller, "sn_id", store.get("sn_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, store, grid);
		if(baseDao.isDBSetting(caller,"getPriceByNoOrder")){
			getSalePrice(store.get("sn_id"));
		}
		if (sb.length() > 0) {
			BaseUtil.appendError(sb.toString());
		}
		tipSellerBatch(caller, store.get("sn_id"));
	}

	void getTotal(Object sn_id) {
		baseDao.execute("update sendnotifydetail set snd_code=(select sn_code from sendnotify where snd_snid=sn_id) where snd_snid="
				+ sn_id + " and not exists (select 1 from sendnotify where snd_code=sn_code)");
		baseDao.execute("update sendnotifydetail set snd_total=round(snd_sendprice*snd_outqty,2) where snd_snid=" + sn_id);
		baseDao.execute("update sendnotifydetail set snd_netprice=round(snd_sendprice/(1+snd_taxrate/100),6) where snd_snid=" + sn_id);
		baseDao.execute("update sendnotifydetail set snd_taxtotal=round(snd_netprice*snd_outqty,2) where snd_snid=" + sn_id);
		baseDao.execute("update sendnotify set sn_total=(select sum(snd_total) from sendnotifydetail where sendnotifydetail.snd_snid = sendnotify.sn_id) where sn_id="
				+ sn_id);
	}

	@Override
	public void deleteSendNotify(int sn_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("SendNotify", "sn_statuscode", "sn_id=" + sn_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { sn_id });
		// 删除SendNotify及明细
		sendNotifyDao.deleteSendNotify(sn_id);
		// 记录操作
		baseDao.logger.delete(caller, "sn_id", sn_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { sn_id });
	}

	@Override
	@Transactional
	public void updateSendNotifyById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("SendNotify", "sn_statuscode", "sn_id=" + store.get("sn_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		// 修改SendNotify
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "SendNotify", "sn_id");
		baseDao.execute(formSql);
		// 修改SendNotifyDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "SendNotifyDetail", "snd_id");
		StringBuffer sb = new StringBuffer();
		for (Map<Object, Object> s : gstore) {
			Object sndid = s.get("snd_id");
			Object sdid = s.get("snd_sdid");
			if (sndid == null || sndid.equals("") || sndid.equals("0") || Integer.parseInt(sndid.toString()) == 0) {// 新添加的数据，id不存在
				s.put("snd_statuscode", "ENTERING");
				s.put("snd_code", store.get("sn_code"));
				if (StringUtil.hasText(s.get("snd_orderdetno")) && StringUtil.hasText(s.get("snd_ordercode"))) {
					if (sdid == null || sdid.equals("") || sdid.equals("0")) {
						sdid = baseDao.getFieldDataByCondition("saledetail left join sale on sa_id=sd_said", "sd_id",
								"sa_code='" + s.get("snd_ordercode") + "' and sd_detno=" + s.get("snd_orderdetno"));
						s.put("snd_sdid", sdid);
					}
					sendNotifyDao.restoreSaleYqty(Double.parseDouble(s.get("snd_outqty").toString()), s.get("snd_ordercode").toString(),
							Integer.valueOf(s.get("snd_orderdetno").toString()));
				}
				baseDao.execute(SqlUtil.getInsertSql(s, "SendNotifyDetail", "snd_id"));
			} else {
				// 销售单更新已转数，通知单状态，发货状态
				sendNotifyDao.restoreSaleWithQty(Integer.parseInt(sndid.toString()), Double.parseDouble(s.get("snd_outqty").toString()),
						s.get("snd_ordercode"), s.get("snd_orderdetno"));
				// 更新送货提醒的已转数
				sendNotifyDao.restoreNoticeWithQty(Integer.parseInt(sndid.toString()), Double.parseDouble(s.get("snd_outqty").toString()),
						s.get("snd_ordercode"), s.get("snd_orderdetno"));
			}
			if ((s.get("snd_sendprice") == null || "0".equals(s.get("snd_sendprice")))
					&& (s.get("snd_discountprice") == null || "0".equals(s.get("snd_discountprice")))) {
				sb.append("行号:" + s.get("snd_pdno") + "销售单价，报关/折扣价都为0<hr/>");
			}
		}
		baseDao.execute(gridSql);
		baseDao.execute("update sendnotifydetail set snd_sdid=(select sd_id from saledetail where sd_code =snd_ordercode and sd_detno=snd_orderdetno) where nvl(snd_ordercode,' ')<>' ' and nvl(snd_orderdetno,0)<>0 and snd_snid="
				+ store.get("sn_id"));
		useDefaultTax(caller, store.get("sn_id"));
		getTotal(store.get("sn_id"));
		// 执行如果明细仓库没填更新成物料的默认仓库
		baseDao.execute("update sendnotifydetail set (snd_warehousecode,snd_warehouse)=(select wh_code,wh_description from warehouse left join product on wh_code=pr_whcode where pr_code =sendnotifydetail.snd_prodcode)  where snd_snid="
				+ store.get("sn_id") + " and snd_warehousecode is null ");
		updateWare(store.get("sn_id"),store.get("sn_warehousecode"));
		baseDao.execute(
				"update sendnotifydetail set (SND_CUSTPRODCODE,SND_CUSTPRODSPEC,snd_custproddetail)=(select max(pc_custprodcode),max(pc_custprodspec),max(pc_custproddetail) from ProductCustomer left join Product on pc_prodid=pr_id left join customer on pc_custid=cu_id where cu_code=? and snd_prodcode=pr_code) where nvl(snd_custprodcode,' ')=' ' and snd_snid=?",
				store.get("sn_custcode"), store.get("sn_id"));
		// 更新批号ID(可能先选择了一个批号，snd_batchid有保存，然后又清除snd_batchcode保存)
		baseDao.execute(
				"update sendnotifydetail set snd_batchid=nvl((select ba_id from batch where ba_code=snd_batchcode and ba_prodcode=snd_prodcode and ba_whcode=snd_warehousecode),0) where snd_snid=?",
				store.get("sn_id"));
		sendnotify_commit_minus(store.get("sn_id"));
		// 记录操作
		baseDao.logger.update(caller, "sn_id", store.get("sn_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
		if(baseDao.isDBSetting(caller,"getPriceByNoOrder")){
			getSalePrice(store.get("sn_id"));
		}
		if (sb.length() > 0) {
			BaseUtil.appendError(sb.toString());
		}
		tipSellerBatch(caller, store.get("sn_id"));
	}
	
	@Transactional
	@Override
	public void auditSendNotify(int sn_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("SendNotify", "sn_statuscode", "sn_id=" + sn_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.audit_onlyCommited"));
		}
		getTotal(sn_id);
		check(caller, sn_id);
		checkCustomer(sn_id);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { sn_id });
		// 执行审核操作
		baseDao.audit("SendNotify", "sn_id=" + sn_id, "sn_status", "sn_statuscode", "sn_auditdate", "sn_auditman");
		baseDao.audit("SendNotifyDetail", "snd_snid=" + sn_id, "SND_STATUS", "snd_statuscode");
		// sendNotifyDao.checkSNDQty(0, sn_id);
		// 记录操作
		baseDao.logger.audit(caller, "sn_id", sn_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { sn_id });
	}

	@Override
	public void resAuditSendNotify(int sn_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("SendNotify", "sn_statuscode", "sn_id=" + sn_id);
		StateAssert.resAuditOnlyAudit(status);
		String err = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(snd_pdno) from sendnotifydetail where nvl(snd_yqty,0)>0 and snd_snid=?", String.class, sn_id);
		if (err != null) {
			BaseUtil.showError("明细行已转数大于0，不允许反审核!行号：" + err);
		}
		err = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(snd_pdno) from sendnotifydetail where nvl(snd_statuscode,' ')<>'AUDITED' and snd_snid=?", String.class,
				sn_id);
		if (err != null) {
			BaseUtil.showError("明细行状态不等于已审核，不允许反审核!行号：" + err);
		}
		err = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(snd_pdno) from sendnotifydetail where snd_snid=? and (snd_code, snd_pdno) in (select sc_sncode, scd_snddetno from SendNotifyChange left join SendNotifyChangeDetail on scd_scid=sc_id where sc_statuscode<>'AUDITED')",
						String.class, sn_id);
		if (err != null) {
			BaseUtil.showError("明细行存在未审核的销售通知单变更单，不允许反审核!行号：" + err);
		}
		baseDao.resAuditCheck("SendNotify", sn_id);
		// 执行反审核操作
		baseDao.resAudit("SendNotify", "sn_id=" + sn_id, "sn_status", "sn_statuscode", "sn_auditdate", "sn_auditman");
		baseDao.resOperate("SendNotifyDetail", "snd_snid=" + sn_id, "SND_STATUS", "snd_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "sn_id", sn_id);
	}

	@Override
	public void submitSendNotify(int sn_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("SendNotify", "sn_statuscode", "sn_id=" + sn_id);
		StateAssert.submitOnlyEntering(status);
		astrictSellerBatch(caller, sn_id);
		getTotal(sn_id);
		check(caller, sn_id);
		checkCustomer(sn_id);
		String err1 = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(snd_pdno) from sendnotifyDetail where snd_snid = ? and nvl(snd_sendprice,0)=0 and nvl(snd_discountprice,0)=0",
						String.class, sn_id);
		useDefaultTax(caller, sn_id);
		allowZeroTax(caller, sn_id);
		baseDao.execute("update sendnotifydetail set snd_sdid=(select sd_id from saledetail where sd_code =snd_ordercode and sd_detno=snd_orderdetno) where nvl(snd_ordercode,' ')<>' ' and snd_snid="
				+ sn_id);
		sendnotify_commit_minus(sn_id);
		Object wh_code = baseDao.getFieldDataByCondition("SendNotify", "sn_warehousecode", "sn_id="+sn_id);
		if(!StringUtil.hasText(wh_code)){
			SqlRowList rs = baseDao.queryForRowSet("select snd_warehousecode from SendNotifyDetail where snd_snid="+sn_id+" and snd_warehousecode is not null order by snd_pdno asc");
			if(rs.next()){
				baseDao.execute("update SendNotify set sn_warehousecode='"+rs.getObject("snd_warehousecode")+"',sn_warehouseid=(select wh_id from warehouse where wh_code='"+rs.getObject("snd_warehousecode")+"') where sn_id="+sn_id);
			}
		}
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { sn_id });
		// 执行提交操作
		baseDao.submit("SendNotify", "sn_id=" + sn_id, "sn_status", "sn_statuscode");
		baseDao.submit("SendNotifyDetail", "snd_snid=" + sn_id, "SND_STATUS", "snd_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "sn_id", sn_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { sn_id });
		if (err1 != null) {
			BaseUtil.showErrorOnSuccess("明细行销售价，报关/折扣价都为0!行号：" + err1);
		}
	}

	private void check(String caller, int sn_id) {
		Object type = baseDao.getFieldDataByCondition("SendNotify", "sn_outtype", "sn_id=" + sn_id);
		if ("".equals(type) || type == null) {
			baseDao.execute("update SendNotify set sn_outtype='正常出货' where sn_id=" + sn_id);
		}
		// 检查关联的订单+序号是否存在，状态是否有效(作废、结案的不能操作)
		String err = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat('订单号:'||snd_ordercode||'订单序号:'||snd_orderdetno) from (select snd_ordercode,snd_orderdetno from sendnotifydetail where snd_snid=? and  not exists (select sd_code,sd_detno from saledetail where sd_code=snd_ordercode and sd_detno=snd_orderdetno and sd_statuscode = 'AUDITED') and nvl(snd_ordercode,' ')<>' ')",
						String.class, sn_id);
		if (err != null) {
			BaseUtil.showError("订单号+序号不存在或者状态不等于已审核!" + err);
		}
		err = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat('订单号:'||snd_ordercode||'订单序号:'||snd_orderdetno||'物料编号:'||snd_prodcode) from (select snd_ordercode,snd_orderdetno,snd_prodcode from sendnotifydetail left join saledetail on snd_ordercode=sd_code and snd_orderdetno=sd_detno where snd_prodcode <> sd_prodcode and sd_statuscode<>'FINISH' and snd_snid=? and nvl(snd_ordercode,' ')<>' ')",
						String.class, sn_id);
		if (err != null) {
			BaseUtil.showError("料号与订单+序号料号不一致!" + err);
		}
		if(baseDao.isDBSetting("Sale", "zeroOutWhenHung")){
			err = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(sn_custcode) from sendnotify left join customer on sn_custcode=cu_code where sn_id = ? and cu_status='挂起'"
							+ "and not exists (select 1 from sendnotifydetail left join saledetail on sd_Code=snd_ordercode and sd_detno=snd_orderdetno "
							+ "where snd_snid=sn_id and nvl(snd_ordercode,' ')<>' ' and nvl(snd_orderdetno,0)<>0 and nvl(sd_price,0)=0)",
							String.class, sn_id);
			if (err != null) {
				BaseUtil.showError("客户资料状态为挂起，不允许进行当前操作!客户号：" + err);
			}
		}else{
			err = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(sn_custcode) from sendnotify left join customer on sn_custcode=cu_code where sn_id = ? and cu_status='挂起'",
							String.class, sn_id);
			if (err != null) {
				BaseUtil.showError("客户资料状态为挂起，不允许进行当前操作!客户号：" + err);
			}
		}
		if (!baseDao.isDBSetting(caller, "allowARCust")) {
			err = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(snd_pdno) from sendnotifydetail left join SendNotify on snd_snid=sn_id left join sale on snd_ordercode=sa_code where snd_snid=? and nvl(snd_ordercode,' ')<>' ' and nvl(sn_custcode,' ') <> nvl(sa_custcode,' ')",
							String.class, sn_id);
			if (err != null) {
				BaseUtil.showError("明细行订单客户编号与当前单客户编号不一致!行号：" + err);
			}
			err = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(snd_pdno) from sendnotifydetail left join SendNotify on snd_snid=sn_id left join sale on snd_ordercode=sa_code where snd_snid=? and nvl(snd_ordercode,' ')<>' ' and nvl(sn_arcustcode,' ') <> nvl(sa_apcustcode,' ')",
							String.class, sn_id);
			if (err != null) {
				BaseUtil.showError("明细行订单应收客户编号与当前单应收客户编号不一致!行号：" + err);
			}
		}
		if (baseDao.isDBSetting("CopCheck")) {
			// 明细行销售订单所属公司与当前单所属公司不一致，不允许进行当前操作
			err = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(snd_pdno) from sendnotifydetail left join SendNotify on snd_snid=sn_id left join sale on snd_ordercode=sa_code where snd_snid=? and nvl(sa_cop,' ')<>nvl(sn_cop,' ') and nvl(snd_ordercode,' ')<>' ' ",
							String.class, sn_id);
			if (err != null) {
				BaseUtil.showError("明细行销售订单所属公司与当前单所属公司不一致，不允许进行当前操作!行号：" + err);
			}
		}
		err = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat('行号['||snd_pdno||']批号['||snd_batchcode||']') from sendnotifydetail left join Batch on SND_BATCHID=ba_id where snd_snid=? and NVL(ba_inqty, 0)-NVL(ba_outqty, 0)<nvl(snd_outqty,0) and nvl(snd_batchcode,' ')<>' ' ",
						String.class, sn_id);
		if (err != null) {
			BaseUtil.showError("批号的余数将会出现负数！请修改数量或批号后再过账！" + err);
		}
	}

	@Override
	public void resSubmitSendNotify(int sn_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("SendNotify", "sn_statuscode", "sn_id=" + sn_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { sn_id });
		// 执行反提交操作
		baseDao.resOperate("SendNotify", "sn_id=" + sn_id, "sn_status", "sn_statuscode");
		baseDao.resOperate("SendNotifyDetail", "snd_snid=" + sn_id, "SND_STATUS", "snd_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "sn_id", sn_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { sn_id });
	}

	@Override
	public int turnProdIO(int sn_id, String caller) {
		int piid = 0;
		// 判断该请购单是否已经转入过出货单
		Object code = baseDao.getFieldDataByCondition("sendnotify", "sn_code", "sn_id=" + sn_id);
		code = baseDao.getFieldDataByCondition("prodinout", "pi_code", "pi_relativeplace='" + code + "'");
		if (code != null && !code.equals("")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.sendnotify.haveturn")
					+ "<a href=\"javascript:openUrl('jsps/scm/sale/prodInOut.jsp?whoami=ProdInOut!Sale&formCondition=pi_codeIS" + code
					+ "&gridCondition=pd_codeIS" + code + "')\">" + code + "</a>&nbsp;");
		} else {
			// 转出货单
			piid = sendNotifyDao.turnProdIN(sn_id);
			// 修改发货通知单状态
			baseDao.updateByCondition("SendNotify", "SN_SENDSTATUSCODE='TURNOUT',SN_SENDSTATUS='" + BaseUtil.getLocalMessage("TURNOUT")
					+ "'", "sn_id=" + sn_id);
			baseDao.updateByCondition("SendNotifyDetail", "snd_yqty=snd_outqty", "snd_snid=" + sn_id);
			// 记录操作
			baseDao.logger.turn("msg.turnProdIO", "SendNotify", "sn_id", sn_id);
		}
		return piid;
	}

	@Override
	public String[] printSendNotify(int sn_id, String caller, String reportName, String condition) {
		String err = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(sn_custcode) from sendnotify left join customer on sn_custcode=cu_code where sn_id = ? and cu_status='挂起'",
						String.class, sn_id);
		if (err != null) {
			BaseUtil.showError("客户资料状态为挂起，不允许进行当前操作!客户号：" + err);
		}
		// 执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[] { sn_id });
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 记录操作
		baseDao.logger.print(caller, "sn_id", sn_id);
		// 执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[] { sn_id });
		return keys;
	}

	/**
	 * 计算可用量
	 */
	@Override
	public void loadOnHandQty(int id) {
		SqlRowList rs = baseDao.queryForRowSet("SELECT snd_prodcode,snd_sdid,snd_id FROM SendNotifyDetail WHERE snd_snid=?", id);
		Object onhand = 0;
		Object sndqty = 0;
		StringBuffer sb = new StringBuffer();
		while (rs.next()) {
			onhand = baseDao.getFieldDataByCondition("io_pwonhand_view", "pw_onhand", "pw_prodcode='" + rs.getString(1) + "'");
			sndqty = baseDao.getFieldDataByCondition("SendNotifyDetail left join SendNotify on sn_id=snd_snid", "sum(snd_outqty)",
					"snd_sdid=" + rs.getInt(2) + " and sn_statuscode='AUDITED'");
			sndqty = sndqty == null ? 0 : sndqty;
			if (onhand != null && Double.parseDouble(onhand.toString()) != 0) {
				baseDao.updateByCondition("SendNotifyDetail",
						"snd_useqty=" + (Double.parseDouble(onhand.toString()) - Double.parseDouble(sndqty.toString())),
						"snd_id=" + rs.getInt(3));
			} else {
				sb.append("<br>物料:" + rs.getString(1) + "的良品库存为0!");
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showErrorOnSuccess(sb.toString());
		}
	}

	@Override
	public void splitSendtify(String formdata, String data, String caller) {
		Map<Object, Object> formmap = BaseUtil.parseFormStoreToMap(formdata);
		int snd_id = Integer.parseInt(formmap.get("snd_id").toString());
		int snd_snid = Integer.parseInt(formmap.get("snd_snid").toString());
		List<String> sqls = new ArrayList<String>();
		Map<String, Object> currentMap = new HashMap<String, Object>();
		SqlRowList cur = baseDao.queryForRowSet("select * from sendnotifydetail where snd_id=" + snd_id);
		if (cur.next()) {
			currentMap = cur.getCurrentMap();
		} else {
			BaseUtil.showError("原始明细已不存在!无法拆分!");
		}
		Object outqty = baseDao.getFieldDataByCondition("sendnotifydetail", "snd_outqty", "snd_id="+snd_id);
		Object newdetno = baseDao.getFieldDataByCondition("sendnotifydetail", "max(snd_pdno)", "snd_snid=" + snd_snid);
		List<Map<Object, Object>> gridmaps = BaseUtil.parseGridStoreToMaps(data);
		Map<Object, Object> map = new HashMap<Object, Object>();
		Object sdid = null;
		Double snd_outqty = null;
		BigDecimal sum = BigDecimal.valueOf(0);
		int snddetno = 0;
		Object newqty = 0;
		snddetno = Integer.parseInt(newdetno.toString());
		for (int i = 0; i < gridmaps.size(); i++) {
			map = gridmaps.get(i);
			sdid = map.get("snd_id");
			if(sdid.equals(snd_id)){
				newqty = map.get("snd_outqty");
			}
			snd_outqty = Double.valueOf(map.get("snd_outqty").toString());
			sum = BigDecimal.valueOf(snd_outqty).add(sum);
			if (sdid != null && Integer.parseInt(sdid.toString()) != 0) {
				sqls.add("update SendnotifyDETAIL set snd_outqty=" + snd_outqty + ",snd_warehousecode='" + map.get("snd_warehousecode")
						+ "',snd_warehouse='" + map.get("snd_warehouse") + "',snd_batchcode='" + map.get("snd_batchcode")
						+ "',snd_batchid='" + map.get("snd_batchid") + "',snd_total='" + snd_outqty
						* Float.parseFloat(currentMap.get("snd_sendprice").toString()) + "',snd_taxtotal='" + snd_outqty
						* Float.parseFloat(currentMap.get("snd_netprice").toString()) + "' where snd_id=" + sdid);
			} else {
				++snddetno;
				currentMap.remove("snd_pdno");
				currentMap.put("snd_pdno", snddetno);
				currentMap.remove("snd_id");
				currentMap.put("snd_id", baseDao.getSeqId("SendnotifyDETAIL_SEQ"));
				currentMap.remove("snd_outqty");
				currentMap.put("snd_outqty", snd_outqty);
				currentMap.remove("snd_warehousecode");
				currentMap.put("snd_warehousecode", map.get("snd_warehousecode"));
				currentMap.remove("snd_warehouse");
				currentMap.put("snd_warehouse", map.get("snd_warehouse"));
				currentMap.remove("snd_batchcode");
				currentMap.put("snd_batchcode", map.get("snd_batchcode"));
				currentMap.remove("snd_batchid");
				currentMap.put("snd_batchid", map.get("snd_batchid"));
				currentMap.remove("snd_total");
				currentMap.put("snd_total", snd_outqty * Float.parseFloat(currentMap.get("snd_sendprice").toString()));
				currentMap.remove("snd_taxtotal");
				currentMap.put("snd_taxtotal", snd_outqty * Float.parseFloat(currentMap.get("snd_netprice").toString()));
				sqls.add(SqlUtil.getInsertSqlByMap(currentMap, "SendnotifyDETAIL"));
			}
		}
		BigDecimal bd = new BigDecimal(outqty.toString());
		if(sum.compareTo(bd)!=0){
			BaseUtil.showError("分拆数量必须等于原数量!");
		}
		baseDao.execute(sqls);
		/**
		 * 问题反馈单据编号：2017020751
		 * @author wsy
		 *
		 */
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "拆分", "明细行:" + formmap.get("snd_pdno") + "=>被拆分,原数量："+outqty+";新数量:"+newqty+"", "SendNotify|sn_id="
				+ snd_snid));
		handlerService.handler(caller, "split", "after", new Object[] { snd_snid });
	}

	@Override
	public void saveShip(String formStore, String caller) {
		// 修改SendNotify
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "SendNotify", "sn_id");
		baseDao.execute(formSql);
		baseDao.logger.others("保存船务信息", "保存成功", "SendNotify", "sn_id", store.get("sn_id"));
	}

	// 税率默认
	private void useDefaultTax(String caller, Object sn_id) {
		String defaultTax = baseDao.getDBSetting("Sale", "defaultTax");
		if (defaultTax != null) {
			// 税率强制等于币别表的默认税率
			if ("1".equals(defaultTax)) {
				baseDao.execute("update SendNotifyDetail set snd_taxrate=(select cr_taxrate from currencys left join SendNotify on sn_currency=cr_name and cr_statuscode='CANUSE' where snd_snid=sn_id)"
						+ " where snd_snid=" + sn_id);
			}
			// 税率强制等于客户资料的默认税率
			if ("2".equals(defaultTax)) {
				baseDao.execute("update SendNotifyDetail set snd_taxrate=(select nvl(cu_taxrate,0) from Customer left join SendNotify on sn_custcode=cu_code and cu_auditstatuscode='AUDITED' where sn_id=snd_snid)"
						+ " where snd_snid=" + sn_id);
			}
		}
	}

	// 本位币允许税率为0
	private void allowZeroTax(String caller, Object sn_id) {
		if (!baseDao.isDBSetting("Sale", "allowZeroTax")) {
			String currency = baseDao.getDBSetting("defaultCurrency");
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select WM_CONCAT(snd_pdno) from SendNotifyDetail left join SendNotify on snd_snid=sn_id where nvl(snd_taxrate,0)=0 and sn_currency='"
							+ currency + "' and snd_snid=?", String.class, sn_id);
			if (dets != null) {
				BaseUtil.showError("本位币税率为0，不允许提交!行号：" + dets);
			}
		}
	}

	/**
	 * 只提示：只考虑当前业务员的可用批次
	 */
	private void tipSellerBatch(String caller, Object sn_id) {
		if (baseDao.isDBSetting(caller, "tipSellerBatch")) {
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(snd_pdno) from SendNotifyDetail left join SendNotify on snd_snid=sn_id  left join batch B on snd_batchcode=B.ba_code and snd_prodcode=B.ba_prodcode and Snd_Warehousecode=B.ba_whcode where sn_id=? and snd_batchcode<>' ' and exists(select 1 from batch C where C.ba_prodcode=snd_prodcode and C.ba_whcode=SendNotifyDetail.Snd_Warehousecode  and nvl(C.ba_remain,0)>0  and trunc(C.ba_date)<trunc(B.ba_date) and NVL(C.ba_kind,0)=0 and ba_sellercode<>' ' and ba_sellercode=sn_sellercode and not exists(select 1 from SendNotifyDetail A where A.snd_batchcode=ba_code and A.snd_prodcode=ba_prodcode and A.Snd_Warehousecode=ba_whcode and snd_statuscode<>'FINISH')) and rownum<20",
							String.class, sn_id);
			if (dets != null) {
				BaseUtil.appendError("明细行所选批号不是当前业务员的可用最早可用批次！行号：" + dets);
			}
		}
	}

	/**
	 * 限制：只考虑当前业务员的可用批次
	 */
	private void astrictSellerBatch(String caller, Object sn_id) {
		if (baseDao.isDBSetting(caller, "astrictSellerBatch")) {
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(snd_pdno) from SendNotifyDetail left join SendNotify on snd_snid=sn_id  left join batch B on snd_batchcode=B.ba_code and snd_prodcode=B.ba_prodcode and Snd_Warehousecode=B.ba_whcode where sn_id=? and snd_batchcode<>' ' and exists(select 1 from batch C where C.ba_prodcode=snd_prodcode and C.ba_whcode=SendNotifyDetail.Snd_Warehousecode  and nvl(C.ba_remain,0)>0  and trunc(C.ba_date)<trunc(B.ba_date) and NVL(C.ba_kind,0)=0 and ba_sellercode<>' ' and ba_sellercode=sn_sellercode and not exists(select 1 from SendNotifyDetail A where A.snd_batchcode=ba_code and A.snd_prodcode=ba_prodcode and A.Snd_Warehousecode=ba_whcode and snd_statuscode<>'FINISH')) and rownum<20",
							String.class, sn_id);
			if (dets != null) {
				BaseUtil.showError("明细行所选批号不是当前业务员的可用最早可用批次！行号：" + dets);
			}
		}
	}

	/**
	 * 销售订单：是否负利润记算
	 */
	public void sendnotify_commit_minus(Object sn_id) {
		if (baseDao.isDBSetting("Sale", "countMinus")) {
			//更新汇率
			baseDao.execute("update SENDNOTIFY set SN_RATE="
					+ "(select cm_crrate from CurrencysMonth where cm_crname=SN_CURRENCY and CM_YEARMONTH=TO_NUMBER(TO_CHAR(sn_date,'yyyymm'))) "
					+ "where sn_id="+sn_id+" and nvl(sn_rate,0)=0");
			baseDao.execute("update Customer set cu_rate="
					+ "(select cm_crrate from SENDNOTIFY left join customer on sn_custcode=cu_code left join  CurrencysMonth on cm_crname=CU_CURRENCY and CM_YEARMONTH=TO_NUMBER(TO_CHAR(sn_date,'yyyymm'))  where sn_id="+sn_id+") "
					+ "where cu_id=(select SN_CUSTID from SENDNOTIFY where sn_id="+sn_id+") and nvl(cu_rate,0)=0");
			
			List<Object[]> objs = baseDao.getFieldsDatasByCondition("sendnotifydetail left join sendnotify on snd_snid=sn_id",
					new String[] { "snd_id", "snd_prodcode", "sn_rate", "snd_batchcode" }, " sn_id=" + sn_id);
			Double sn_rate = 0.0;
			for (Object[] os : objs) {
				if (StringUtil.hasText(os[3])) {
					baseDao.updateByCondition("sendnotifydetail",
							"snd_costprice=(select ba_price from (select ba_price from batch where ba_prodcode='" + os[1]
									+ "' and ba_remain>0 and ba_code='" + os[3] + "' order by ba_date desc) where rownum=1)", "snd_id="
									+ os[0]);
				} else {
					baseDao.updateByCondition("sendnotifydetail",
							"snd_costprice=(select ba_price from (select ba_price from batch where ba_prodcode='" + os[1]
									+ "' and ba_remain>0 order by ba_date desc) where rownum=1)", "snd_id=" + os[0]);
				}
				sn_rate = Double.parseDouble(os[2].toString());
			}
			baseDao.execute("update sendnotifydetail set snd_profitrate=round((snd_outqty*snd_sendprice*" + sn_rate
					+ "/(1+nvl(snd_taxrate,0)/100)-snd_outqty*snd_costprice)/(snd_outqty*snd_sendprice*" + sn_rate
					+ "/(1+nvl(snd_taxrate,0)/100))*100,2) where nvl(snd_sendprice,0)>0 and snd_snid=" + sn_id);
			int count = baseDao.getCount("select count(*) from sendnotifydetail where snd_snid=" + sn_id + " and nvl(snd_profitrate,0)<0");
			String sn_minus = null;
			if (count > 0) {
				sn_minus = "是";
			} else {
				sn_minus = "否";
			}
			baseDao.execute("update sendnotify set sn_minus='" + sn_minus + "' where sn_id=" + sn_id);
			baseDao.execute("update sendnotifydetail set snd_minus='是' where nvl(snd_profitrate,0)<0 and snd_snid=" + sn_id);
			baseDao.execute("update sendnotifydetail set snd_minus='否' where nvl(snd_profitrate,0)>=0 and snd_snid=" + sn_id);
		}
	}
	
	// maz 2018010231  出货通知单主表仓库选择后，更新明细行仓库和出货单逻辑一样
	void updateWare(Object id,Object wh_code){
		if(StringUtil.hasText(wh_code)){
			baseDao.execute("update SendNotifyDetail set snd_warehousecode='"+wh_code+"',snd_warehouse=(select wh_description from warehouse where wh_code='"+wh_code+"') where snd_warehousecode is null and snd_warehouse is null and snd_snid="+id);
		}else{
			SqlRowList rs = baseDao.queryForRowSet("select snd_warehousecode from SendNotifyDetail where snd_snid="+id+" and snd_warehousecode is not null order by snd_pdno asc");
			if(rs.next()){
				baseDao.execute("update SendNotify set sn_warehousecode='"+rs.getObject("snd_warehousecode")+"',sn_warehouseid=(select wh_id from warehouse where wh_code='"+rs.getObject("snd_warehousecode")+"') where sn_id="+id);
			}
		}
	}
	
	/**
	 * 根据取价原则取销售价格表的价格给出货通知单。  maz  2018010522
	 */
	private void getSalePrice(Object id) {
		String datas = baseDao.getDBSetting("SendNotify","getPriceTenet");
		if(datas==null){//默认为客户+币别+料号
			datas = "A";
		}
    	SqlRowList rs = baseDao.queryForRowSet("SELECT * FROM SendNotifyDetail LEFT JOIN SendNotify on sn_id=snd_snid left join customer on sn_custcode=cu_code WHERE sn_id=?", id);
    	Object price = null;
    	if(!StringUtil.hasText(rs.getObject("snd_ordercode")) && !StringUtil.hasText(rs.getObject("snd_orderdetno"))){
			while (rs.next()) {
				if(datas.equals("A")){
					price = baseDao.getFieldDataByCondition("(select * from (select spd_price,spd_id from SalePriceDetail LEFT JOIN SalePrice on sp_id=spd_spid "
									+ "where spd_arcustcode='" + rs.getString("sn_custcode") + "' and spd_prodcode='" + rs.getString("snd_prodcode")+"' "
									+ "and spd_currency='"+rs.getString("sn_currency")+"' and spd_statuscode='VALID' "
									+ "order by sp_indate desc) order by spd_id desc) ", "spd_price",
									"rownum<2");//客户+币别+料号
				}else if(datas.equals("B")){
					price = baseDao.getFieldDataByCondition("(select * from (select spd_price,spd_id from SalePriceDetail LEFT JOIN SalePrice on sp_id=spd_spid "
							+ "where spd_pricetype='" + rs.getString("cu_pricetype") + "' and spd_prodcode='" + rs.getString("snd_prodcode")+"' "
							+ "and spd_currency='"+rs.getString("sn_currency")+"' and spd_statuscode='VALID' "
							+ "order by sp_indate desc) order by spd_id desc) ", "spd_price",
							"rownum<2");//取价类型+币别+料号
				}else if(datas.equals("C")){
					price = baseDao.getFieldDataByCondition("(select * from (select spd_price,spd_id from SalePriceDetail LEFT JOIN SalePrice on sp_id=spd_spid "
							+ "where spd_prodcode='" + rs.getString("snd_prodcode")+"' "
							+ "and spd_currency='"+rs.getString("sn_currency")+"' and spd_statuscode='VALID' "
							+ "order by sp_indate desc) order by spd_id desc) ", "spd_price",
							"rownum<2");//料号+币别
				}else if(datas.equals("D")){
					price = baseDao.getFieldDataByCondition("(select * from (select spd_price,spd_id from SalePriceDetail LEFT JOIN SalePrice on sp_id=spd_spid "
							+ "where spd_arcustcode='" + rs.getString("sn_custcode") + "' and spd_prodcode='" + rs.getString("snd_prodcode")+"' "
							+ "and spd_currency='"+rs.getString("sn_currency")+"' and spd_statuscode='VALID' and spd_taxrate="+rs.getDouble("snd_taxrate")+" "
							+ "order by sp_indate desc) order by spd_id desc) ", "spd_price",
							"rownum<2");//客户+币别+料号+税率
				}
				if (price!=null &&  Double.parseDouble(price.toString()) != 0) {
					if((!StringUtil.hasText(rs.getObject("snd_purcprice")) && !StringUtil.hasText(rs.getObject("snd_sendprice"))) || (StringUtil.hasText(rs.getObject("snd_sendprice")) && "0".equals(rs.getObject("snd_sendprice").toString()))){
						baseDao.updateByCondition("SendNotifyDetail", "snd_purcprice=" + price + ",snd_sendprice=" + price + "",
								"snd_id=" + rs.getInt("snd_id"));
					}else{
						baseDao.updateByCondition("SendNotifyDetail", "snd_purcprice=" + price + "",
								"snd_id=" + rs.getInt("snd_id"));
					}
				}
			}
		}
	}
	/**
	 * 2018040518   销售预测、销售订单、出货通知单提交、审核时增加限制：客户编号、客户名称，应收客户编号、应收客户名称与客户资料里编号、名称不一致的，限制提交、审核 maz
	 */
	public void checkCustomer(Integer id){
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(sn_code) from SendNotify where sn_id=? and (sn_arcustcode,sn_arcustname) not in (select cu_code,cu_name from customer)",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("该张出货通知单应收客户与客户资料不匹配，限制当前操作，请确认");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(sn_code) from SendNotify where sn_id=? and (sn_custcode,sn_custname) not in (select cu_code,cu_name from customer)",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("该张出货通知单客户与客户资料不匹配，限制当前操作，请确认");
		}
	}
}
