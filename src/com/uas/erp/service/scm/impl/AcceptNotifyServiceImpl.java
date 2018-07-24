package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.AcceptNotifyDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.scm.AcceptNotifyService;

@Service("acceptNotifyService")
public class AcceptNotifyServiceImpl implements AcceptNotifyService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private AcceptNotifyDao acceptNotifyDao;

	@Override
	public void saveAcceptNotify(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("AcceptNotify", "an_code='" + store.get("an_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, grid });
		// 保存AcceptNotify
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "AcceptNotify", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存AcceptNotifyDetail
		for (Map<Object, Object> map : grid) {
			if (map.get("and_whcode") == null || map.get("and_whcode").equals("")) {
				map.put("and_whcode", store.get("an_whcode"));
				map.put("and_whname", store.get("an_whname"));
			}
			if (map.get("and_batchcode") == null || map.get("and_batchcode").equals("")) {
				map.put("and_batchcode", baseDao.getBatchcode("ProdInOut!PurcCheckin"));
			}
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "acceptnotifydetail", "and_id");
		baseDao.execute(gridSql);
		updatePrice(store.get("an_id"));
		baseDao.execute(
				"update AcceptNotify set an_total=round((select sum(and_orderprice*and_inqty) from acceptnotifydetail where an_id=and_anid),2) where an_id=?",
				store.get("an_id"));
		// 记录操作
		baseDao.logger.save(caller, "an_id", store.get("an_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, grid });
	}

	@Override
	public void deleteAcceptNotify(String caller, int an_id) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("AcceptNotify", "an_statuscode", "an_id=" + an_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler("AcceptNotify", "delete", "before", new Object[] { an_id });
		// 删除AcceptNotify
		baseDao.deleteById("AcceptNotify", "an_id", an_id);
		// 删除AcceptNotify前，更新采购单数据
		acceptNotifyDao.deleteAcceptNotify(an_id);
		// 记录操作
		baseDao.logger.delete(caller, "an_id", an_id);
		// 执行删除后的其它逻辑
		handlerService.handler("AcceptNotify", "delete", "after", new Object[] { an_id });
	}

	@Override
	public void updateAcceptNotifyById(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("AcceptNotify", "an_statuscode", "an_id=" + store.get("an_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "AcceptNotify", "an_id");
		Object anid = store.get("an_id");
		// 修改明细数据
		List<String> gridSql = SqlUtil.getInsertOrUpdateSql(gstore, "AcceptNotifyDetail", "and_id");
		gridSql.add(0, formSql);// 修改主记录
		// 检测是否超数
		String checkSql = "select wmsys.wm_concat('采购单:'||pd_code||'行'||pd_detno) from purchasedetail where (pd_code,pd_detno) in (select and_ordercode,and_orderdetno from acceptnotifydetail where and_anid="
				+ anid
				+ ") and nvl(pd_qty,0)+nvl(pd_backqty,0)<nvl((select sum(and_inqty) from acceptnotifydetail where and_ordercode=pd_code and and_orderdetno=pd_detno),0)";
		// 更新原采购单已转数
		List<String> updatePurcQty = new ArrayList<String>();
		updatePurcQty
				.add("update purchasedetail set pd_yqty=NVL((select sum(and_inqty) from AcceptNotifyDetail where and_ordercode=pd_code and and_orderdetno=pd_detno)-(select nvl(sum(pd_outqty),0) from prodiodetail where pd_ordercode=pd_code and pd_orderdetno=pd_detno and pd_piclass in ('采购验退单','不良品出库单')),0) where (pd_code,pd_detno) in (select and_ordercode,and_orderdetno from AcceptNotifyDetail where and_anid="
						+ anid + ")");
		updatePurcQty
				.add("update purchasedetail set pd_status='PART2SN' where (pd_code,pd_detno) in (select and_ordercode,and_orderdetno from AcceptNotifyDetail where and_anid="
						+ anid + ") and nvl(pd_qty,0)>nvl(pd_yqty,0) and nvl(pd_yqty,0) >0");
		updatePurcQty
				.add("update purchasedetail set pd_status='TURNSN' where (pd_code,pd_detno) in (select and_ordercode,and_orderdetno from AcceptNotifyDetail where and_anid="
						+ anid + ") and nvl(pd_qty,0)=nvl(pd_yqty,0) and nvl(pd_yqty,0) >0");
		updatePurcQty
				.add("update purchasedetail set pd_status='AUDITED' where (pd_code,pd_detno) in (select and_ordercode,and_orderdetno from AcceptNotifyDetail where and_anid="
						+ anid + ") and nvl(pd_yqty,0)=0");
		updatePurcQty.add("update purchase set pu_turnstatuscode='PART2SN',pu_turnstatus='" + BaseUtil.getLocalMessage("PART2SN")
				+ "' where pu_code in (select and_ordercode from AcceptNotifyDetail where and_anid=" + anid
				+ ") and (select count(1) from purchasedetail where pd_puid=pu_id and pd_status='PART2SN')>0");
		updatePurcQty.add("update purchase set pu_turnstatuscode='TURNSN',pu_turnstatus='" + BaseUtil.getLocalMessage("TURNSN")
				+ "' where pu_code in (select and_ordercode from AcceptNotifyDetail where and_anid=" + anid
				+ ") and (select count(1) from purchasedetail where pd_puid=pu_id and (pd_status='PART2SN' or pd_status='AUDITED'))=0");
		String execMsg = baseDao.executeWithCheck(gridSql, updatePurcQty, checkSql);
		if (execMsg != null) {
			BaseUtil.showError("数量超过原采购单数量:<br>" + execMsg);
		}
		baseDao.updateByCondition("AcceptNotify", "an_cop=(select en_shortname from enterprise)", "an_id=" + store.get("an_id")
				+ " and nvl(an_cop,' ')=' '");
		List<Object[]> detailField = baseDao.getFieldsDatasByCondition("AcceptNotifyDetail", new String[] { "and_id", "and_batchcode" },
				"and_anid=" + store.get("an_id") + " and nvl(and_batchcode,' ')=' '");
		for (Object[] object : detailField) {
			baseDao.updateByCondition("AcceptNotifyDetail", "and_batchcode='" + baseDao.getBatchcode("ProdInOut!PurcCheckin") + "'",
					"and_id=" + object[0]);
		}
		// 采购金额
		updatePrice(store.get("an_id"));
		// 记录操作
		baseDao.logger.update(caller, "an_id", store.get("an_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
	}

	private void updatePrice(Object id) {
		baseDao.execute(
				"update AcceptNotify set an_rate=(SELECT nvl(cm_crrate,0) from Currencysmonth where an_currency=cm_crname and cm_yearmonth=to_char(an_date,'yyyymm')) where an_id=?",
				id);
		baseDao.execute(
				"update AcceptNotifydetail set and_price=(select round(price+price*amount/case when total=0 then 1 else total end,8) from (select and_id,(and_orderprice*an_rate/(1+and_taxrate/100)) price,(select sum(and_inqty*and_orderprice*an_rate*(1+nvl(and_taxrate,0)/100)) from AcceptNotifyDetail pp1 left join AcceptNotify p1 on pp1.and_anid=p1.an_id where p1.an_id=acceptnotifydetail.and_anid) total,nvl((select sum(pd_rate*pd_amount) from ProdChargeDetailAN A where A.pd_anid=acceptnotifydetail.and_anid),0) amount from AcceptNotifyDetail left join AcceptNotify on and_anid=an_id where and_anid=?) B where B.and_id=acceptnotifydetail.and_id) where and_anid=? and nvl(and_price,0)=0",
				id, id);
		baseDao.execute(
				"update AcceptNotifydetail set and_total=round(and_price*and_inqty,2),and_ordertotal=round(and_orderprice*and_inqty,2),and_plancode=round(and_orderprice*and_inqty*(select an_rate from AcceptNotify where an_id=and_anid),2) where and_anid=?",
				id);
		baseDao.execute("update AcceptNotifydetail set and_barcode=round(and_total-and_plancode,2) where and_anid=?", id);
		baseDao.execute(
				"update AcceptNotify set an_total=round((select sum(and_orderprice*and_inqty) from acceptnotifydetail where an_id=and_anid),2) where an_id=?",
				id);
	}

	@Override
	public String[] printAcceptNotify(int an_id, String caller, String reportName, String condition) {
		// 执行打印前的其它逻辑
		// 执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[] { an_id });
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 修改打印状态
		baseDao.print("AcceptNotify", "an_id=" + an_id, "an_printstatus", "an_printstatuscode");
		// 记录操作
		baseDao.logger.print(caller, "an_id", an_id);
		// 执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[] { an_id });
		return keys;
	}

	@Override
	public void auditAcceptNotify(String caller, int an_id) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("AcceptNotify", "an_statuscode", "an_id=" + an_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { an_id });
		// 执行审核操作
		baseDao.audit("AcceptNotify", "an_id=" + an_id, "an_status", "an_statuscode");
		// 记录操作
		baseDao.logger.audit(caller, "an_id", an_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { an_id });
	}

	@Override
	public void resAuditAcceptNotify(String caller, int an_id) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("AcceptNotify", "an_statuscode", "an_id=" + an_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核前的其它逻辑
		handlerService.handler(caller, "resAudit", "before", new Object[] { an_id });
		// 执行反审核操作
		baseDao.resOperate("AcceptNotify", "an_id=" + an_id, "an_status", "an_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "an_id", an_id);
		// 执行反审核后的其它逻辑
		handlerService.handler(caller, "resAudit", "after", new Object[] { an_id });
	}

	@Override
	public void submitAcceptNotify(String caller, int an_id) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("AcceptNotify", "an_statuscode", "an_id=" + an_id);
		StateAssert.submitOnlyEntering(status);
		updatePrice(an_id);
		// 判断是否超采购数收料
		acceptNotifyDao.checkQty(an_id);
		// 判断批号是否重复
		String errRows = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(and_detno) from acceptnotifydetail where and_anid=? and (and_batchcode,and_prodcode,and_whcode) in (select and_batchcode,and_prodcode,and_whcode from (select count(1) c, and_batchcode,and_prodcode,and_whcode from acceptnotifydetail where (and_batchcode,and_prodcode,and_whcode) in (select and_batchcode,and_prodcode,and_whcode from acceptnotifydetail where and_anid=?) group by and_batchcode,and_prodcode,and_whcode) where c > 1)",
						String.class, an_id, an_id);
		if (errRows != null) {
			BaseUtil.showError("批号重复,行:" + errRows);
		} else {
			errRows = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(and_detno) from acceptnotifydetail where and_anid=? and (and_batchcode,and_prodcode,and_whcode) in (select ba_code,ba_prodcode,ba_whcode from batch)",
							String.class, an_id);
			if (errRows != null) {
				BaseUtil.showError("批号重复,行:" + errRows);
			}
		}
		String notEnoughSale = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat('<br>行:'||and_detno||',PO数:'||nvl(pd_qty,0)||',通知单已提交数:'||nvl(and_commitedqty,0)||',本次数:'||nvl(and_inqty,0)) from (select and_detno,and_inqty,(select sum(nvl(and_inqty,0)) from acceptnotifydetail left join acceptnotify on an_id=and_anid where and_ordercode=A.and_ordercode and and_orderdetno=A.and_orderdetno and an_statuscode<>'ENTERING') and_commitedqty,nvl(pd_qty,0)+nvl(pd_backqty,0) pd_qty from acceptnotifydetail A left join purchasedetail on pd_code=and_ordercode and pd_detno=and_orderdetno where and_anid=?) where nvl(pd_qty,0)<nvl(and_commitedqty,0)+nvl(and_inqty,0)",
						String.class, an_id);
		if (notEnoughSale != null) {
			BaseUtil.showError("数量超过了PO数量,提交失败:<br>" + notEnoughSale);
		}
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { an_id });
		// 执行提交操作
		baseDao.submit("AcceptNotify", "an_id=" + an_id, "an_status", "an_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "an_id", an_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { an_id });
	}

	@Override
	public void resSubmitAcceptNotify(String caller, int an_id) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("AcceptNotify", "an_statuscode", "an_id=" + an_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { an_id });
		// 执行反提交操作
		baseDao.resOperate("AcceptNotify", "an_id=" + an_id, "an_status", "an_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "an_id", an_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { an_id });
	}

	@Override
	public int turnVerifyApply(String caller, int an_id) {
		int vaid = 0;
		Object vendcode = baseDao.getFieldDataByCondition("AcceptNotify", "an_vendcode", "an_id=" + an_id);
		if (vendcode != null && !vendcode.equals("")) {

		} else {
			Object venduu = baseDao.getFieldDataByCondition("AcceptNotify", "an_venduu", "an_id=" + an_id);
			if (venduu != null && !venduu.equals("")) {
				String sql = "update AcceptNotify set (an_vendcode,an_vendname)=(select pu_vendcode,pu_vendname from AcceptNotifydetail left join purchase on and_ordercode=pu_code  where and_anid="
						+ an_id + " and rownum=1) where an_id=" + an_id;
				baseDao.execute(sql);
			}
		}
		boolean haveturn = baseDao.checkByCondition("AcceptNotifyDetail", "and_anid=" + an_id + " and and_inqty-NVL(and_yqty,0)>0 ");
		if (haveturn) {
			BaseUtil.showError("本单已转收料单，不能重复转");
		}
		// 转采购收料单
		vaid = acceptNotifyDao.turnVerifyApply(an_id);
		// 修改收料通知单状态
		baseDao.updateByCondition("AcceptNotify", "an_statuscode='TURNVA',an_status='" + BaseUtil.getLocalMessage("TURNVA") + "'  ",
				"an_id=" + an_id);
		// 将通知单中的条码转入到收料单
		SqlRowList rs = baseDao.queryForRowSet("select and_id,and_inqty from AcceptNotifyDetail where and_anid=" + an_id);
		if (rs.next()) {
			for (Map<String, Object> map : rs.getResultList()) {
				// 判断通知单是否有条码
				Double bar_qty = baseDao.getJdbcTemplate().queryForObject(
						"select NVL(sum(nvl(ban_qty,0)),0) qty from BarAcceptNotify where ban_andid=?", Double.class, map.get("and_id"));
				if (bar_qty > 0) {// 有条码
					// 判断明细行条码数量是否等于通知单内该行明细的数量
					if (bar_qty.equals(Double.valueOf(map.get("and_inqty").toString()))) {// 都相等，将通知单条码转入
						baseDao.execute("insert into VerifyApplyDetailP(vadp_id,vadp_vadid,vadp_vaddetno,vadp_vacode,vadp_prodcode,vadp_prodid,"
								+ "vadp_qty,vadp_barcode,vadp_vendcode,vadp_vendname,vadp_madedate,vadp_outboxcode,vadp_outboxid) "
								+ " select VERIFYAPPLYDETAILP_SEQ.nextval,vad_id,vad_detno,vad_code,ban_prodcode,ban_prodid, "
								+ " ban_qty,ban_barcode,ban_vendcode,vad_vendname,vad_madedate,ban_outboxcode,ban_outboxid "
								+ " from barAcceptNotify left join acceptNotifydetail on and_id=ban_andid left join verifyapplydetail on vad_andid=and_id where vad_vaid="
								+ vaid + " and and_id=" + map.get("and_id"));

					}
				}
			}
		}
		//带入通知单里面采购单中明细表的采购单中的所属公司
		SqlRowList rs2 = baseDao.queryForRowSet("select * from (select pu_cop from AcceptNotifyDetail left join PurchaseDetail on and_orderid= pd_id left join Product on and_prodcode=pr_code left join Purchase on pd_puid=pu_id where and_anid=?" + " and nvl(pu_cop,' ')<>' ' order by pu_id) where rownum<2",an_id);
		while(rs2.next()){
			Object vacop = baseDao.getFieldDataByCondition("VerifyApply", "va_cop", "va_id="+vaid);
			Object pucop = baseDao.getFieldDataByCondition("(select pu_cop from AcceptNotifyDetail left join PurchaseDetail on and_orderid= pd_id left join Product on and_prodcode=pr_code left join Purchase on pd_puid=pu_id where and_anid="+an_id+" and nvl(pu_cop,' ')<>' ' order by pu_id)", "pu_cop", "rownum<2");
			if(vacop==null){
				baseDao.updateByCondition("VerifyApply", "va_cop='"+pucop+"'", "va_id="+vaid);
			}				
		}
		return vaid;
	}

	@Override
	public String turnProdio(String caller, int an_id) {
		int piid = 0;
		// 判断该收料通知单是否已经转入过采购验收单
		Object code = baseDao.getFieldDataByCondition("AcceptNotify", "an_code", "an_id=" + an_id);
		code = baseDao.getFieldDataByCondition("ProdInOut", "pi_inoutno", "pi_sourcecode='" + code + "'");
		if (code != null && !code.equals("")) {
			BaseUtil.showError("该收料通知单已转入过采购验收单,验收单号[" + code + "]");
		} else {
			// 转采购验收单
			piid = acceptNotifyDao.turnProdio(an_id);
			// 执行提交操作
			baseDao.updateByCondition("ProdInOut",
					"pi_total=(SELECT round(sum(nvl(pd_orderprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0))),2) FROM ProdIODetail WHERE pd_piid="
							+ piid + ")", "pi_id=" + piid);
			baseDao.updateByCondition("ProdInOut", "pi_totalupper=L2U(nvl(pi_total,0))", "pi_id=" + piid);
			// 修改收料通知单状态
			baseDao.updateByCondition("AcceptNotify", "an_statuscode='TURNIN',an_status='" + BaseUtil.getLocalMessage("TURNIN") + "'",
					"an_id=" + an_id);
		}
		return "入库成功!";
	}

	@Override
	public void saveAcceptNotifyQty(String data) {
		// 修改收料通知单明细数量
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(data);
		if(baseDao.checkIf("AcceptNotifyDetail", "and_id="+map.get("and_id")+" and nvl(and_yqty,0)>"+map.get("and_inqty"))){
			BaseUtil.showError("当前数量不能小于已转数量!");
		}
		Object andid = map.get("and_id");
		Employee employee = SystemSession.getUser();
		Object[] datas = baseDao.getFieldsDataByCondition("AcceptNotifyDetail", new String[] { "and_yqty", "nvl(and_b2bqty,and_inqty)",
				"and_detno", "and_inqty", "and_anid" }, "and_id=" + andid);
		if (datas != null) {
		//if (datas != null && (datas[0] == null || String.valueOf(datas[0]).equals("0"))) {
			double b2bqty = Double.parseDouble(String.valueOf(datas[1]));
			double qty = Double.parseDouble(String.valueOf(map.get("and_inqty")));
			if (qty > b2bqty)
				BaseUtil.showError("当前数量不能大于已上传数量!");
			else {
				baseDao.updateByCondition("AcceptNotifyDetail", "and_sendstatus='待上传',and_inqty='" + qty + "' ", "and_id=" + andid);
				// 更新送货提醒的已转数
				baseDao.execute("update purchasenotify a set a.pn_endqty=(select sum(nvl(b.and_inqty,0)) from AcceptNotifyDetail b where a.pn_id=b.and_pnid) where a.pn_id in (select c.and_pnid from AcceptNotifyDetail c where c.and_id="
						+ andid + ")");
				//更新送货提醒状态
				baseDao.execute("update purchasenotify set pn_status='未确认',pn_statuscode='UNCONFIRM' where nvl(pn_endqty,0)=0  and pn_id in (select c.and_pnid from AcceptNotifyDetail c where c.and_id="+andid + ")");
				baseDao.execute("update purchasenotify set pn_status='部分发货' where nvl(pn_endqty,0)>0 and nvl(pn_endqty,0)<nvl(pn_qty,0) and pn_id in (select c.and_pnid from AcceptNotifyDetail c where c.and_id="+andid + ")");
				baseDao.execute("update purchasenotify set pn_status='已发货' where nvl(pn_endqty,0)=nvl(pn_qty,0) and pn_id in (select c.and_pnid from AcceptNotifyDetail c where c.and_id="+andid + ")");
				
				// 修改收料通知单状态
				baseDao.execute("update AcceptNotify set an_statuscode='AUDITED',an_status='" + BaseUtil.getLocalMessage("AUDITED")
						+ "' where (select count(1) from AcceptNotifydetail where and_anid=an_id and an_id=" + datas[4]
						+ " and nvl(and_yqty,0)=0)=0 and an_id= " + datas[4] + " and an_statuscode<>'AUDITED' ");
				baseDao.execute("update AcceptNotify set an_statuscode='TURNVA',an_status='" + BaseUtil.getLocalMessage("TURNVA")
						+ "' where (select count(1) from AcceptNotifydetail where and_anid=an_id and an_id=" + datas[4]
						+ " and nvl(and_yqty,0)<>nvl(and_inqty,0))=0 and an_id= " + datas[4] + " and an_statuscode<>'TURNVA' ");
				baseDao.execute("update AcceptNotify set an_statuscode='PART2VA',an_status='" + BaseUtil.getLocalMessage("PART2VA")
						+ "' where (select count(1) from AcceptNotifydetail where and_anid=an_id and an_id=" + datas[4]
						+ " and nvl(and_yqty,0)>0 and nvl(and_yqty,0)<nvl(and_inqty,0))>0 and an_id= " + datas[4]
						+ " and an_statuscode<>'PART2VA' ");
				baseDao.logMessage(new MessageLog(employee.getEm_name(), "修改操作", "第" + datas[2] + "行 数量由" + datas[3] + "修改为" + qty,
						"AcceptNotify|an_id=" + datas[4]));
			}

		} //else	BaseUtil.showError("当前单据已转收料不能修改");

	}

	@Override
	public void backAll(int id) {
		Employee employee = SystemSession.getUser();
		Object haveturned = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(and_detno) from AcceptNotifydetail  where and_anid=? and nvl(and_yqty,0)<>0 ", String.class, id);
		if (haveturned != null)
			BaseUtil.showError("第" + haveturned + "行已转收料无法取消!");
		else {
			baseDao.updateByCondition("AcceptNotifyDetail", "and_sendstatus='待上传',and_inqty=0", "and_anid=" + id
					+ " and nvl(and_inqty,0)<>0");
			// 全部拒收后更新送货提醒的已转数
			baseDao.execute("update purchasenotify a  set a.pn_endqty=(select sum(nvl(b.and_inqty,0)) from AcceptNotifyDetail b where a.pn_id=b.and_pnid) where a.pn_id in (select c.and_pnid from AcceptNotifyDetail c where c.and_anid="
					+ id + ")");
			baseDao.execute("update purchasenotify set pn_status='未确认',pn_statuscode='UNCONFIRM' where nvl(pn_endqty,0)=0  and pn_id in (select c.and_pnid from AcceptNotifyDetail c where c.and_anid="+id + ")");
			baseDao.execute("update purchasenotify set pn_status='部分发货' where nvl(pn_endqty,0)>0 and nvl(pn_endqty,0)<nvl(pn_qty,0) and pn_id in (select c.and_pnid from AcceptNotifyDetail c where c.and_anid="+id + ")");
			baseDao.execute("update purchasenotify set pn_status='已发货' where nvl(pn_endqty,0)=nvl(pn_qty,0) and pn_id in (select c.and_pnid from AcceptNotifyDetail c where c.and_anid="+id + ")");
			baseDao.logMessage(new MessageLog(employee.getEm_name(), "全部拒收", "拒收成功", "AcceptNotify|an_id=" + id));
		}
	}
}
