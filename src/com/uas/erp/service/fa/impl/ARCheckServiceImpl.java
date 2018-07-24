package com.uas.erp.service.fa.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.JacksonUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.dao.common.ARCheckDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Key;
import com.uas.erp.service.fa.ARCheckService;

@Service("arCheckService")
public class ARCheckServiceImpl implements ARCheckService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private ARCheckDao arCheckDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private TransferRepository transferRepository;

	@Override
	public void saveARCheck(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		String code = store.get("ac_code").toString();
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ARCheck", "ac_code='" + code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler("ARCheck", "save", "before", new Object[] { formStore, gridStore });
		// 保存ARCheck
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "ARCheck"));
		baseDao.execute(SqlUtil.getInsertSqlbyList(grid, "ARCheckDetail", "ad_id"));
		baseDao.logger.save("ARCheck", "ac_id", store.get("ac_id"));
		// 执行保存后的其它逻辑
		handlerService.handler("ARCheck", "save", "after", new Object[] { formStore, gridStore });
	}

	@Override
	public void deleteARCheck(int ac_id) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("ARCheck", "ac_statuscode", "ac_id=" + ac_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		arCheckDao.deleteARCheck(ac_id);
		// 执行删除前的其它逻辑
		handlerService.handler("ARCheck", "delete", "before", new Object[] { ac_id });
		// 删除ARCheck
		baseDao.deleteById("ARCheck", "ac_id", ac_id);
		// 删除ARCheckDetail
		baseDao.deleteById("ARCheckdetail", "ad_acid", ac_id);
		// 记录操作
		baseDao.logger.delete("ARCheck", "ac_id", ac_id);
		// 执行删除后的其它逻辑
		handlerService.handler("ARCheck", "delete", "after", new Object[] { ac_id });
	}

	@Override
	public void updateARCheckById(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("ARCheck", "ac_statuscode", "ac_id=" + store.get("ac_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		// 执行修改前的其它逻辑
		handlerService.handler("ARCheck", "save", "before", new Object[] { store, gstore });
		// 修改ARCheck
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "ARCheck", "ac_id"));
		Object adid = null;
		Object sourcetype = null;
		Object qty = null;
		Integer sourcedetailid = null;
		double tQty = 0;// 收料通知单修改数量
		SqlRowList rs = null;
		boolean type = baseDao.isDBSetting("autoCreateArBill");
		String checksql1 = "SELECT abd_code,abd_detno,abd_qty FROM ARBillDetail WHERE abd_id=? and abs(abd_qty)<?";
		String checksql3 = "SELECT abd_code,abd_detno,abd_qty FROM ARBillDetail WHERE abd_pdid=? and abs(abd_qty)<?";
		String checksql2 = "SELECT pd_inoutno,pd_pdno,nvl(pd_outqty,0)-nvl(pd_inqty,0) pd_qty,pd_piclass FROM ProdIODetail WHERE pd_id=? and abs(nvl(pd_inqty,0)+nvl(pd_outqty,0))<?";
		for (Map<Object, Object> s : gstore) {
			adid = s.get("ad_id");
			tQty = Double.parseDouble(s.get("ad_qty").toString());
			if (adid != null && Integer.parseInt(adid.toString()) != 0) {
				Object[] objs = baseDao.getFieldsDataByCondition("ARCheckDetail", new String[] { "nvl(ad_sourcedetailid,0)",
						"nvl(ad_qty,0)", "ad_sourcetype" }, "ad_id=" + adid + " and nvl(ad_sourcedetailid,0)<>0");
				if (objs != null && objs[0] != null) {
					sourcedetailid = Integer.parseInt(String.valueOf(objs[0]));
					sourcetype = String.valueOf(objs[2]);
					if (sourcedetailid != null && sourcedetailid > 0) {
						if (type) {
							if ("ARBILL".equals(sourcetype)) {
								qty = baseDao.getFieldDataByCondition("ARCheckDetail", "sum(nvl(ad_qty,0))", "ad_sourcedetailid="
										+ sourcedetailid + "and nvl(ad_sourcetype,' ')='ARBILL' AND ad_id <>" + adid);
								qty = qty == null ? 0 : qty;
								rs = baseDao.queryForRowSet(checksql1, sourcedetailid, Math.abs(Double.parseDouble(qty.toString()) + tQty));
								if (rs.next()) {
									StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],应收发票号:").append(rs.getString("abd_code"))
											.append(",行号:").append(rs.getInt("abd_detno")).append(",发票数量:").append(rs.getDouble("abd_qty"))
											.append(",已转数量:").append(qty).append(",本次数量:").append(tQty);
									BaseUtil.showError(sb.toString());
								}
								baseDao.updateByCondition("ARBillDetail", "abd_ycheck=" + (Double.parseDouble(String.valueOf(qty)) + tQty),
										"abd_id=" + sourcedetailid);
							} else if ("PRODINOUT".equals(sourcetype)) {
								qty = baseDao.getFieldDataByCondition("ARCheckDetail", "sum(nvl(ad_qty,0))", "ad_sourcedetailid="
										+ sourcedetailid + "and nvl(ad_sourcetype,' ')='PRODINOUT' AND ad_id <>" + adid);
								qty = qty == null ? 0 : qty;
								rs = baseDao.queryForRowSet(checksql3, sourcedetailid, Math.abs(Double.parseDouble(qty.toString()) + tQty));
								if (rs.next()) {
									StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],应收发票号:").append(rs.getString("abd_code"))
											.append(",行号:").append(rs.getInt("abd_detno")).append(",发票数量:").append(rs.getDouble("abd_qty"))
											.append(",已转数量:").append(qty).append(",本次数量:").append(tQty);
									BaseUtil.showError(sb.toString());
								}
								baseDao.updateByCondition("ARBillDetail", "abd_ycheck=" + (Double.parseDouble(String.valueOf(qty)) + tQty),
										"abd_pdid=" + sourcedetailid);
							}
						} else {
							if ("PRODINOUT".equals(sourcetype)) {
								qty = baseDao.getFieldDataByCondition("ARCheckDetail", "sum(nvl(ad_qty,0))", "ad_sourcedetailid="
										+ sourcedetailid + "and nvl(ad_sourcetype,' ')='PRODINOUT' AND ad_id <>" + adid);
								qty = qty == null ? 0 : qty;
								rs = baseDao.queryForRowSet(checksql2, sourcedetailid, Math.abs(Double.parseDouble(qty.toString()) + tQty));
								if (rs.next()) {
									StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],发出商品号:").append(rs.getString("gs_code"))
											.append(",行号:").append(rs.getInt("gsd_detno")).append(",数量:").append(rs.getDouble("gsd_qty"))
											.append(",已转数量:").append(qty).append(",本次数量:").append(tQty);
									BaseUtil.showError(sb.toString());
								}
								baseDao.updateByCondition("ProdIODetail", "pd_ycheck=" + (Double.parseDouble(String.valueOf(qty)) + tQty),
										"pd_id=" + sourcedetailid);
							}
						}
					}
				}
			}
		}
		// 修改ARCheckDetail
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "ARCheckDetail", "ad_id"));
		baseDao.execute("update ARCheckDetail set ad_amount=round(nvl(ad_qty*round(ad_price,8),0),2) where ad_acid=" + store.get("ac_id"));
		baseDao.execute("update ARCHECK set ac_checkamount=round(nvl((select sum(ad_amount) from ARCHECKDETAIL where ad_acid=ac_id),0),2) where ac_id="
				+ store.get("ac_id"));
		// 记录操作
		baseDao.logger.update("ARCheck", "ac_id", store.get("ac_id"));
		// 执行修改后的其它逻辑
		handlerService.handler("ARCheck", "save", "after", new Object[] { store, gstore });
	}

	@Override
	public String[] printARCheck(int ac_id, String reportName, String condition) {
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 执行打印前的其它逻辑
		handlerService.handler("ARCheck", "print", "before", new Object[] { ac_id });
		// 执行打印操作
		// 记录操作
		baseDao.logger.print("ARCheck", "ac_id", ac_id);
		// 执行打印后的其它逻辑
		handlerService.handler("ARCheck", "print", "after", new Object[] { ac_id });
		return keys;
	}

	@Override
	public void auditARCheck(int ac_id) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ARCheck", "ac_statuscode", "ac_id=" + ac_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.handler("ARCheck", "audit", "before", new Object[] { ac_id });
		// 执行审核操作
		baseDao.updateByCondition("ARCheck",
				"ac_statuscode='AUDITED',ac_sendstatus='待上传',ac_status='" + BaseUtil.getLocalMessage("AUDITED") + "'", "ac_id=" + ac_id);
		// 审核之后自动确认
		if (baseDao.isDBSetting("ARCheck", "autoConfirm")) {
			confirmARCheck(ac_id);
		}
		// 记录操作
		baseDao.logger.audit("ARCheck", "ac_id", ac_id);
		// 执行审核后的其它逻辑
		handlerService.handler("ARCheck", "audit", "after", new Object[] { ac_id });
	}

	@Override
	public void resAuditARCheck(int ac_id) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ARCheck", "ac_statuscode", "ac_id=" + ac_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
		}
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(ad_detno) from ARCheckDetail where ad_acid=? and nvl(ad_yqty,0)>0", String.class, ac_id);
		if (dets != null) {
			BaseUtil.showError("明细行已转入发票，不允许进行当前操作!行号：" + dets);
		}
		// 执行审核前的其它逻辑
		handlerService.handler("ARCheck", "resAudit", "before", new Object[] { ac_id });
		// 执行反审核操作
		baseDao.updateByCondition("ARCheck", "ac_statuscode='ENTERING',ac_status='" + BaseUtil.getLocalMessage("ENTERING") + "'", "ac_id="
				+ ac_id);
		// 执行审核后的其它逻辑
		handlerService.handler("ARCheck", "resAudit", "after", new Object[] { ac_id });
		// 记录操作
		baseDao.logger.resAudit("ARCheck", "ac_id", ac_id);
	}

	@Override
	public void submitARCheck(int ac_id) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("ARCheck", new String[] { "ac_statuscode", "ac_fromdate", "ac_todate",
				"round(nvl(ac_checkamount,0),1)" }, "ac_id=" + ac_id);
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.submit_onlyEntering"));
		}

		// 根据明细重新计算对账金额
		baseDao.execute("update ARCHECK set ac_checkamount=(select sum(ad_amount) from ARCHECKDETAIL where ad_acid=ac_id) where ac_id="
				+ ac_id);

		if (status[1] == null) {
			BaseUtil.showError("对账起始日期不能为空！");
		}
		if (status[2] == null) {
			BaseUtil.showError("对账截至日期不能为空！");
		}
		if (baseDao.isDBSetting("ARCheck", "APARCheckAccount")) {
			baseDao.procedure("SP_ARCHECKBEGIN", new Object[] { ac_id });
		}
		// 执行提交前的其它逻辑
		handlerService.handler("ARCheck", "commit", "before", new Object[] { ac_id });
		// 执行提交操作
		baseDao.updateByCondition("ARCheck", "ac_statuscode='COMMITED',ac_status='" + BaseUtil.getLocalMessage("COMMITED") + "'", "ac_id="
				+ ac_id);
		// 记录操作
		baseDao.logger.submit("ARCheck", "ac_id", ac_id);
		if (baseDao.isDBSetting("ARCheck", "CheckAutoAudit")) {
			SqlRowList rs = baseDao
					.queryForRowSet(
							"select round(sum(ab_aramount),1) from arbill where ab_statuscode='POSTED' and (ab_custcode,ab_sellercode,ab_currency) in (select ac_custcode,ac_sellercode,ac_currency from ARCheck where ac_id=?) "
									+ "and to_char(ab_date,'yyyymmdd')<=to_char(to_date('"
									+ status[2]
									+ "','yyyy-mm-dd hh24:mi:ss'),'yyyymmdd') and to_char(ab_date,'yyyymmdd')>=to_char(to_date('"
									+ status[1] + "','yyyy-mm-dd hh24:mi:ss'),'yyyymmdd')", ac_id);
			if (rs.next()) {
				if (rs.getGeneralDouble(1) == Double.parseDouble(status[3].toString())) {
					auditARCheck(ac_id);
				}
			}
		}
		status = baseDao.getFieldsDataByCondition("ARCheck", new String[] { "ac_statuscode" }, "ac_id=" + ac_id);
		if ("COMMITED".equals(status[0])) {
			// 执行提交后的其它逻辑
			handlerService.handler("ARCheck", "commit", "after", new Object[] { ac_id });
		}
	}

	@Override
	public void resSubmitARCheck(int ac_id) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ARCheck", "ac_statuscode", "ac_id=" + ac_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resSubmit_onlyCommited"));
		}
		handlerService.handler("ARCheck", "resCommit", "before", new Object[] { ac_id });
		// 执行反提交操作
		baseDao.updateByCondition("ARCheck", "ac_statuscode='ENTERING',ac_status='" + BaseUtil.getLocalMessage("ENTERING") + "'", "ac_id="
				+ ac_id);
		// 记录操作
		baseDao.logger.resSubmit("ARCheck", "ac_id", ac_id);
		handlerService.handler("ARCheck", "resCommit", "after", new Object[] { ac_id });
	}

	@Override
	public void accountedARCheck(int ac_id) {
		// 只能对状态为[未记账]的订单进行操作!
		Object[] status = baseDao.getFieldsDataByCondition("ARCheck", new String[] { "ac_statuscode", "ac_code" }, "ac_id=" + ac_id);
		if (!status[0].equals("AUDITED") && !status[0].equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.account_onlyCommited"));
		}
		// 执行记账前的其它逻辑
		handlerService.handler("ARCheck", "account", "before", new Object[] { ac_id });
		// 执行记账操作
		// 存储过程
		String res = baseDao.callProcedure("SP_COMMITEARCHECK", new Object[] { status[1] });
		if (res != null && !res.trim().equals("") && !"OK".equals(res.toUpperCase())) {
			BaseUtil.showError(res);
		}
		baseDao.updateByCondition("ARCheck", "ac_statuscode='POSTED',ac_status='已过账',ac_postdate=sysdate,ac_postman='"
				+ SystemSession.getUser().getEm_name() + "'", "ac_id=" + ac_id);
		// 记录操作
		baseDao.logger.others("msg.account", "msg.accountSuccess", "ARCheck", "ac_id", ac_id);
		// 执行记账后的其它逻辑
		handlerService.handler("ARCheck", "account", "after", new Object[] { ac_id });
	}

	@Override
	public void resAccountedARCheck(int ac_id) {
		// 只能对状态为[已记账]的订单进行反记账操作!
		Object[] status = baseDao.getFieldsDataByCondition("ARCheck", new String[] { "ac_statuscode", "ac_code" }, "ac_id=" + ac_id);
		if (!status[0].equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAccount_onlyAccount"));
		}
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(ad_detno) from ARCheckDetail where ad_acid=? and nvl(ad_yqty,0)>0", String.class, ac_id);
		if (dets != null) {
			BaseUtil.showError("明细行已转入发票，不允许进行当前操作!行号：" + dets);
		}
		// 执行反记账操作
		String res = baseDao.callProcedure("SP_UNCOMMITEARCHECK", new Object[] { status[1] });
		if (res != null && !res.trim().equals("") && !"OK".equals(res.toUpperCase())) {
			BaseUtil.showError(res);
		}
		baseDao.updateByCondition("ARCheck", "ac_statuscode='AUDITED',ac_status='" + BaseUtil.getLocalMessage("AUDITED")
				+ "',ac_postdate=null,ac_postman=null", "ac_id=" + ac_id);
		// 记录操作
		baseDao.logger.others("msg.resAccount", "msg.resAccountSuccess", "ARCheck", "ac_id", ac_id);
	}

	@Override
	public void confirmARCheck(int ac_id) {
		if (baseDao.isDBSetting("autoCreateArBill") && baseDao.isDBSetting("ARCheck", "priceArCheck")) {
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select str_concat(ad_detno) from archeckdetail,arbilldetail,arbill where ad_pdid=abd_pdid and abd_abid=ab_id and ad_acid=? and ab_class='应收发票' and nvl(ad_sourcetype,' ')='PRODINOUT' and (nvl(ad_price,0)<>nvl(abd_thisvoprice,0) or nvl(ad_taxrate,0)<>nvl(abd_taxrate,0)) and exists (select 1 from arbilldetail where ad_pdid=abd_pdid)",
							String.class, ac_id);
			if (dets != null) {
				BaseUtil.showError("单价、税率与应收发票不一致！行号：" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select str_concat(ad_detno) from archeckdetail,arbilldetail,arbill where ad_sourcedetailid=abd_id and abd_abid=ab_id and ad_acid=? and ab_class='应收发票' and nvl(ad_sourcetype,' ')='ARBILL' and (nvl(ad_price,0)<>nvl(abd_thisvoprice,0) or nvl(ad_taxrate,0)<>nvl(abd_taxrate,0))",
							String.class, ac_id);
			if (dets != null) {
				BaseUtil.showError("单价、税率与应收发票不一致！行号：" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select str_concat(ad_detno) from archeckdetail,arbilldetail,arbill where ad_sourcedetailid=abd_id and abd_abid=ab_id and ad_acid=? and ab_class='其它应收单' and nvl(ad_sourcetype,' ')='ARBILL' and (nvl(ad_price,0)<>nvl(abd_price,0) or nvl(ad_taxrate,0)<>nvl(abd_taxrate,0))",
							String.class, ac_id);
			if (dets != null) {
				BaseUtil.showError("单价、税率与其它应付单不一致！行号：" + dets);
			}
		}
		baseDao.updateByCondition("ARCheck", "ac_confirmstatus='已确认', ac_confirmdate=sysdate", "ac_id=" + ac_id);
		baseDao.execute("update prodiodetail set PD_YCHECK=nvl((select sum(ad_qty) from archeckdetail where ad_pdid=pd_id and ad_sourcetype='PRODINOUT'),0) where pd_id in (select ad_pdid from archeckdetail where ad_acid="
				+ ac_id + " and ad_sourcetype='PRODINOUT')");
		// 确认对账后更新发票应收日期
		int count = baseDao
				.getCount("select count(1) from ARCheck left join payments on ac_paymentcode=pa_code and pa_class='收款方式' where ac_id="
						+ ac_id + " and nvl(PA_BEGINBY,0)=8");
		if (count > 0) {
			baseDao.procedure("SP_PAYDATEBYCHECK_CUST", new Object[] { ac_id });
		}
		// 记录操作
		baseDao.logger.others("确认对账", "确认成功", "ARCheck", "ac_id", ac_id);
		// 确认之后自动开票
		if (baseDao.isDBSetting("ARCheck", "autoBill")) {
			List<Map<Object, Object>> details = baseDao.getJdbcTemplate().query(
					"select ad_qty,ad_id,ad_sourcetype from ARCheckDetail where ad_acid=?", new RowMapper<Map<Object, Object>>() {

						@Override
						public Map<Object, Object> mapRow(ResultSet rs, int index) throws SQLException {
							Map<Object, Object> map = new HashMap<Object, Object>();
							map.put("ad_qty", rs.getObject("ad_qty"));
							map.put("ad_tqty", rs.getObject("ad_qty"));
							map.put("ad_sourcetype", rs.getObject("ad_sourcetype"));
							map.put("ad_id", rs.getObject("ad_id"));
							return map;
						}

					}, ac_id);
			turnBill("ARCheck!ToBill!Deal", details);
		}
		// 执行确认对账后的其它逻辑
		handlerService.handler("ARCheck", "confirmCheck", "after", new Object[] { ac_id });
	}

	@Override
	public void cancelARCheck(int ac_id) {
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(ad_detno) from archeckdetail where ad_acid=? and nvl(ad_yqty,0)>0", String.class, ac_id);
		if (dets != null) {
			if (baseDao.isDBSetting("ARCheck", "autoBill")) {
				BaseUtil.showError("已确认开票，不允许取消确认！行号：" + dets);
			} else {
				BaseUtil.appendError("已确认开票，不允许取消确认！行号：" + dets);
			}
		}
		// 取消确认对账后更新发票应收日期
		int count = baseDao
				.getCount("select count(1) from ARCheck left join payments on ac_paymentcode=pa_code and pa_class='收款方式' where ac_id="
						+ ac_id + " and nvl(PA_BEGINBY,0)=8");
		if (count > 0) {
			baseDao.execute("MERGE INTO (SELECT * FROM ARBILL WHERE AB_STATUSCODE='POSTED' AND EXISTS (SELECT 1 FROM PAYMENTS WHERE PA_CLASS='收款方式' AND AB_PAYMENTCODE=PA_CODE AND NVL(PA_BEGINBY,0)=8)) AB "
					+ "USING (SELECT AD_SOURCECODE,MAX(AC_ARDATE) AC_ARDATE FROM ARCHECK,ARCHECKDETAIL WHERE AC_ID=AD_ACID AND AC_ID="
					+ ac_id
					+ " AND AD_SOURCETYPE='ARBILL' AND AC_CONFIRMSTATUS='已确认' GROUP BY AD_SOURCECODE) BI "
					+ "on (AB.ab_code=BI.AD_SOURCECODE) when matched then update set AB.ab_paydate=nvl(BI.AC_ARDATE,to_date('2099-12-31','yyyy-mm-dd'))");
			baseDao.execute("UPDATE ARBILL SET AB_PAYDATE=TO_DATE('2099-12-31','yyyy-mm-dd') WHERE EXISTS (SELECT 1 FROM ARCHECKDETAIL WHERE AD_ACID="
					+ ac_id
					+ " AND AD_SOURCECODE=AB_CODE AND AD_SOURCETYPE='ARBILL') AND NOT EXISTS (SELECT 1 FROM ARCHECK,ARCHECKDETAIL "
					+ "WHERE AC_ID=AD_ACID AND AD_SOURCECODE=AB_CODE AND AD_SOURCETYPE='ARBILL' AND AC_CONFIRMSTATUS='已确认') "
					+ "AND EXISTS (SELECT 1 FROM PAYMENTS WHERE PA_CLASS='收款方式' and ab_paymentcode=pa_code and nvl(PA_BEGINBY,0)=8) ");
		}
		baseDao.updateByCondition("ARCheck", "ac_confirmstatus=null, ac_confirmdate=null", "ac_id=" + ac_id);
		// 记录操作
		baseDao.logger.others("取消确认", "取消成功", "ARCheck", "ac_id", ac_id);
		// 执行取消确认对账后的其它逻辑
		handlerService.handler("ARCheck", "cancelCheck", "after", new Object[] { ac_id });
	}

	@Override
	public void updateDetailInfo(String data, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(data);
		Object adid = null;
		Object sourcetype = null;
		Object qty = null;
		Integer sourcedetailid = null;
		double tQty = 0;// 收料通知单修改数量
		SqlRowList rs = null;
		String checksql1 = "SELECT abd_code,abd_detno,abd_qty FROM ARBillDetail WHERE abd_id=? and abs(abd_qty)<?";
		String checksql2 = "SELECT gs_code,gsd_detno,gsd_qty FROM GoodsSendDetail left join GoodsSend on gsd_gsid=gs_id WHERE gsd_id=? and abs(gsd_qty)<?";
		adid = store.get("ad_id");
		tQty = Double.parseDouble(store.get("ad_qty").toString());
		if (adid != null && Integer.parseInt(adid.toString()) != 0) {
			Object[] objs = baseDao.getFieldsDataByCondition("ARCheckDetail", new String[] { "nvl(ad_sourcedetailid,0)", "nvl(ad_qty,0)",
					"ad_sourcetype" }, "ad_id=" + adid + " and nvl(ad_sourcedetailid,0)<>0");
			if (objs != null && objs[0] != null) {
				sourcedetailid = Integer.parseInt(String.valueOf(objs[0]));
				sourcetype = String.valueOf(objs[2]);
				if (sourcedetailid != null && sourcedetailid > 0) {
					if ("ARBILL".equals(sourcetype)) {
						qty = baseDao.getFieldDataByCondition("ARCheckDetail", "sum(nvl(ad_qty,0))", "ad_sourcedetailid=" + sourcedetailid
								+ "and nvl(ad_sourcetype,' ') ='ARBILL' AND ad_id <>" + adid);
						qty = qty == null ? 0 : qty;
						rs = baseDao.queryForRowSet(checksql1, sourcedetailid, Math.abs(Double.parseDouble(qty.toString()) + tQty));
						if (rs.next()) {
							StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],应收发票号:").append(rs.getString("abd_code")).append(",行号:")
									.append(rs.getInt("abd_detno")).append(",发票数量:").append(rs.getDouble("abd_qty")).append(",已转数量:")
									.append(qty).append(",本次数量:").append(tQty);
							BaseUtil.showError(sb.toString());
						}
						baseDao.updateByCondition("ARBillDetail", "abd_ycheck=" + (Double.parseDouble(String.valueOf(qty)) + tQty),
								"abd_id=" + sourcedetailid);
					} else if ("发出商品".equals(sourcetype) || "GoodsSendGs".equals(sourcetype)) {
						qty = baseDao.getFieldDataByCondition("ARCheckDetail", "sum(nvl(ad_qty,0))", "ad_sourcedetailid=" + sourcedetailid
								+ "and nvl(ad_sourcetype,' ') in ('发出商品','GoodsSendGs') AND ad_id <>" + adid);
						qty = qty == null ? 0 : qty;
						rs = baseDao.queryForRowSet(checksql2, sourcedetailid, Math.abs(Double.parseDouble(qty.toString()) + tQty));
						if (rs.next()) {
							StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],发出商品号:").append(rs.getString("gs_code")).append(",行号:")
									.append(rs.getInt("gsd_detno")).append(",数量:").append(rs.getDouble("gsd_qty")).append(",已转数量:")
									.append(qty).append(",本次数量:").append(tQty);
							BaseUtil.showError(sb.toString());
						}
						baseDao.updateByCondition("GoodsSendDetail", "gsd_ycheck=" + (Double.parseDouble(String.valueOf(qty)) + tQty),
								"gsd_id=" + sourcedetailid);
					}
				}
			}
		}
		baseDao.execute("update ARCheckDetail set ad_qty=" + tQty + " where ad_id=" + adid);
		baseDao.execute("update ARCheckDetail set ad_amount=ad_qty*ad_price,ad_sendstatus='待上传' where ad_id=" + adid);
	}

	@Override
	public String turnBill(String caller, String data) {
		List<Map<Object, Object>> maps = JacksonUtil.fromJsonArray(data);
		return turnBill(caller, maps);
	}

	public String turnBill(String caller, List<Map<Object, Object>> maps) {
		StringBuffer sb = new StringBuffer();
		Object y = 0;
		Object sourceqty = 0;
		SqlRowList rs = null;
		SqlRowList rs1 = null;
		Integer acid = 0;
		boolean sourcetype = baseDao.isDBSetting("autoCreateArBill");
		Object type = maps.get(0).get("ad_sourcetype");
		String tocaller = null;
		String log = null;
		if (sourcetype) {
			if ("ARBILL".equals(type)) {
				tocaller = "ARCheck!ToBillOut";
			} else if ("PRODINOUT".equals(type)) {
				tocaller = "ARCheck!ToBillOut!PD";
			}
		} else {
			tocaller = "ARCheck!ToARBill";
		}
		for (Map<Object, Object> map : maps) {
			int adid = Integer.parseInt(map.get("ad_id").toString());
			double tqty = Double.parseDouble(map.get("ad_tqty").toString());
			double qty = Double.parseDouble(map.get("ad_qty").toString());
			if (qty < 0 && tqty > 0) {
				BaseUtil.showError("对账数量为负数的，本次转数量不能为正数！");
			}
			if (qty > 0 && tqty < 0) {
				BaseUtil.showError("对账量为正数的，本次转数量不能为负数！");
			}
			if (tqty == 0) {
				BaseUtil.showError("本次转数量不能为0！");
			}
			rs = baseDao.queryForRowSet("SELECT ad_sourcecode,ad_sourcedetno,ad_pdid,ad_detno FROM archeckdetail WHERE ad_id=" + adid);
			if (rs.next()) {
				if (sourcetype) {
					if ("ARBILL".equals(type)) {
						y = baseDao.getFieldDataByCondition(
								"BillOutDetail",
								"sum(nvl(ard_nowqty,0))",
								"ard_ordercode='" + rs.getObject("ad_sourcecode") + "' and ard_orderdetno="
										+ rs.getObject("ad_sourcedetno"));
						sourceqty = baseDao.getFieldDataByCondition("ARBillDetail", "nvl(abd_qty,0)",
								"abd_code='" + rs.getObject("ad_sourcecode") + "' and abd_detno=" + rs.getObject("ad_sourcedetno"));
					} else if ("PRODINOUT".equals(type)) {
						rs1 = baseDao.queryForRowSet("SELECT abd_code,abd_detno,abd_qty FROM ARBillDetail WHERE ABD_PDID="
								+ rs.getGeneralInt("ad_pdid"));
						if (rs1.next()) {
							y = baseDao.getFieldDataByCondition("BillOutDetail", "sum(nvl(ard_nowqty,0))",
									"ard_ordercode='" + rs1.getObject("abd_code") + "' and ard_orderdetno=" + rs1.getObject("abd_detno"));
							sourceqty = rs1.getObject("abd_qty");
						} else {
							sb.append("出入库单对应的发票不存在,行号:").append(rs.getInt("ad_detno")).append("<hr/>");
						}
					}
				} else {
					y = baseDao.getFieldDataByCondition("ARBillDetail", "sum(nvl(abd_qty,0))", "ABD_PDID=" + rs.getGeneralInt("ad_pdid"));
					sourceqty = baseDao.getFieldDataByCondition("ProdIODetail", "nvl(pd_outqty,0)-nvl(pd_inqty,0)",
							"pd_id=" + rs.getGeneralInt("ad_pdid"));
				}
			}
			y = y == null ? 0 : y;
			sourceqty = sourceqty == null ? 0 : sourceqty;
			if (Math.abs(Double.parseDouble(y.toString()) + tqty) > Math.abs(Double.parseDouble(sourceqty.toString()))) {
				sb.append("[本次数量填写超出可转数量],行号:").append(rs.getInt("ad_detno")).append(",数量:").append(sourceqty).append(",已转数:").append(y)
						.append(",本次数:").append(tqty).append("<hr/>");
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}
		if (maps.size() > 0) {
			acid = baseDao.getFieldValue("archeckdetail", "ad_acid", "ad_id=" + maps.get(0).get("ad_id"), Integer.class);
			int id = 0;
			Key key = transferRepository.transfer(tocaller, acid);
			id = key.getId();
			transferRepository.transfer(tocaller, maps, key);
			if (sourcetype) {
				Timestamp bi_date = baseDao.getJdbcTemplate().queryForObject("select bi_date from BillOut where bi_id=?", Timestamp.class,
						id);
				rs = baseDao
						.queryForRowSet(
								"SELECT ab_paymentcode,bi_custcode FROM (SELECT AB_PAYMENTCODE,bi_custcode FROM BILLOUT left join BILLOUTDETAIL on bi_id=ard_biid LEFT JOIN ARBILL ON ARD_ORDERCODE=AB_CODE WHERE ARD_BIID=? AND NVL(AB_PAYMENTCODE,' ')<>' ' ORDER BY ARD_DETNO) WHERE ROWNUM<2",
								id);
				if (rs.next()) {
					String res = baseDao.callProcedure("SP_GETPAYDATE_CUST",
							new Object[] { bi_date, rs.getObject("ab_paymentcode"), 0, rs.getObject("bi_custcode") });
					baseDao.updateByCondition("BILLOUT", "bi_paydate='" + res + "'", " bi_id=" + id);
				}
			}
			if (sourcetype) {
				baseDao.execute("update BillOutDetail set ard_nowbalance=round(nvl(ard_nowqty,0)*round(nvl(ard_nowprice,0),8),2) where ard_biid="
						+ id);
				baseDao.execute("update BillOutDetail set ard_taxamount=round(ard_nowbalance*nvl(ard_taxrate,0)/(100+nvl(ard_taxrate,0)),2) where ard_biid="
						+ id);
				baseDao.execute("update BillOut set bi_amount=round(nvl((select sum(round(ard_nowbalance,2)) from BillOutDetail where ard_biid=bi_id),0),2) where bi_id="
						+ id);
				baseDao.execute("update BillOut set bi_taxamount=round(nvl((select sum(round(ard_taxamount,2)) from BillOutDetail where ard_biid=bi_id),2),2) + nvl(bi_taxdiffer,0) where bi_id="
						+ id);
				baseDao.execute("update BillOut set (bi_departmentcode,bi_department)=(select ab_departmentcode,ab_departmentname from BillOutDetail left join arbill on ard_ordercode=ab_code where ard_biid=bi_id and ard_detno=1 and nvl(ab_departmentname,' ')<>' ') where bi_id="
						+ id);
				baseDao.execute("update ARBILLDETAIL SET abd_yqty=(SELECT NVL(SUM(ard_nowqty),0) FROM BillOutDetail WHERE ard_ordercode=ABD_CODE AND ard_orderdetno=ABD_DETNO and nvl(ard_adid,0)=0)"
						+ " WHERE EXISTS (SELECT 1 FROM BillOutDetail WHERE ard_ordercode=ABD_CODE AND ard_orderdetno=ABD_DETNO AnD ard_biid="
						+ id + ")");
				log = "发票号:" + "<a href=\"javascript:openUrl('jsps/fa/ars/billOut.jsp?formCondition=bi_idIS" + id
						+ "&gridCondition=ard_biidIS" + id + "&whoami=BillOut')\">" + key.getCode() + "</a>&nbsp;";
			} else {
				baseDao.execute("update arbilldetail set abd_code=(select ab_code from arbill where abd_abid=ab_id) WHERE abd_abid=" + id);
				baseDao.execute("update arbilldetail set abd_aramount=ROUND(nvl(abd_thisvoprice,0)*nvl(abd_qty,0),2) WHERE abd_abid=" + id);
				baseDao.execute("update arbilldetail set abd_noaramount=ROUND(nvl(abd_thisvoprice,0)*nvl(abd_qty,0)/(1+nvl(abd_taxrate,0)/100),2) WHERE abd_abid="
						+ id);
				baseDao.execute("update arbilldetail set abd_taxamount=NVL(abd_aramount,0)-NVL(abd_noaramount,0) WHERE abd_abid=" + id);
				baseDao.execute("update arbilldetail SET abd_yqty=(SELECT NVL(SUM(ard_nowqty),0) FROM BillOutDetail WHERE ard_ordercode=ABD_CODE AND ard_orderdetno=ABD_DETNO and nvl(ard_adid,0)=0)"
						+ " WHERE abd_abid="
						+ id
						+ " and EXISTS (SELECT 1 FROM BillOutDetail WHERE ard_ordercode=ABD_CODE AND ard_orderdetno=ABD_DETNO)");
				// 更新ARBill主表的金额
				baseDao.execute("update arbill set ab_aramount=round((select sum(nvl(abd_aramount,0)) from arbilldetail where abd_abid="
						+ id + "),2) where ab_id=" + id);
				baseDao.execute("update arbill set ab_taxamount=(select sum(round(((nvl(abd_thisvoprice,0)*nvl(abd_qty,0)*nvl(abd_taxrate,0)/100)/(1+nvl(abd_taxrate,0)/100)),2)) from arbilldetail where abd_abid="
						+ id + ")+ab_differ where ab_id=" + id);
				baseDao.execute("update arbill set (ab_departmentcode,ab_departmentname)=(select pi_departmentcode,pi_departmentname from ARBILLDetail left join ProdInOut on abd_pdinoutno=pi_inoutno where abd_abid=ab_id and abd_detno=1 and nvl(pi_departmentname,' ')<>' ') where ab_id="
						+ id);
				baseDao.execute("update ProdIODetail SET pd_showinvoqty=(SELECT NVL(SUM(abd_qty),0) FROM ARBillDetail WHERE abd_sourcedetailid=pd_id AND abd_sourcekind='PRODIODETAIL' and nvl(abd_adid,0)=0)"
						+ " WHERE EXISTS (SELECT 1 FROM ARBillDetail WHERE abd_sourcedetailid=pd_id AND abd_sourcekind='PRODIODETAIL' AND abd_abid="
						+ id + ")");
				baseDao.execute("update goodssenddetail SET gsd_showinvoqty=(SELECT NVL(SUM(abd_qty),0) FROM ARBillDetail WHERE abd_sourcedetailid=gsd_id AND abd_sourcekind='GOODSSEND')"
						+ " WHERE EXISTS (SELECT 1 FROM ARBillDetail WHERE abd_sourcedetailid=gsd_id AND abd_sourcekind='GOODSSEND' AND abd_abid="
						+ id + ")");
				log = "发票号:" + "<a href=\"javascript:openUrl('jsps/fa/ars/arbill.jsp?formCondition=ab_idIS" + id
						+ "&gridCondition=abd_abidIS" + id + "&whoami=ARBill!IRMA')\">" + key.getCode() + "</a>&nbsp;";
			}
		}
		// 更新主表开票状态
		arCheckDao.updateBillStatus(acid);
		return "转入成功<hr>" + log;
	}

	@Override
	public void submitARCheckConfirm(int id) {
		handlerService.handler("ARCheck!Confirm", "commit", "before", new Object[] { id });
		baseDao.execute("update ARCheck set ac_confirmstatus='已提交' where ac_id=" + id);
		baseDao.logger.others("提交(确认)", "提交成功", "ARCheck", "ac_id", id);
		handlerService.handler("ARCheck!Confirm", "commit", "after", new Object[] { id });
	}

	@Override
	public void resSubmitARCheckConfirm(int id) {
		handlerService.handler("ARCheck!Confirm", "resCommit", "before", new Object[] { id });
		baseDao.execute("update ARCheck set ac_confirmstatus='未确认' where ac_id=" + id);
		baseDao.logger.others("反提交(确认)", "反提交成功", "ARCheck", "ac_id", id);
		handlerService.handler("ARCheck!Confirm", "resCommit", "after", new Object[] { id });
	}

	final static String RECBALANCENOTICEDETAIL = "insert into RECBALANCENOTICEDETAIL(RBD_ID, RBD_RBID, RBD_DETNO, "
			+ "RBD_ORDERID, RBD_ABCODE, RBD_CURRENCY,RBD_PAYMENTS, RBD_DATE, RBD_AMOUNT, RBD_ORDERTYPE, "
			+ "RBD_SELLERCODE,RBD_SELLERNAME,RBD_BQTY,RBD_ZQTY,RBD_ADID) values (RECBALANCENOTICEDETAIL_SEQ.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	@Override
	public String turnRecBalanceNotice(int id, String data, String caller) {
		String log = null;
		Object sourcetype = null;
		int rb_id = 0;
		int detno = 1;
		Employee employee = SystemSession.getUser();
		String code = null;
		SqlRowList rs = null;
		SqlRowList rs1 = null;
		Double amount = 0.0;
		List<Map<Object, Object>> maps = JacksonUtil.fromJsonArray(data);
		rs = baseDao.queryForRowSet(
				"SELECT * FROM archeck left join archeckdetail on ac_id=ad_acid left join Prodiodetail on ad_pdid=pd_id WHERE ac_id=?", id);
		if (rs.next()) {
			sourcetype = rs.getGeneralString("ad_sourcetype");
			rb_id = baseDao.getSeqId("RECBALANCENOTICE_SEQ");
			code = baseDao.sGetMaxNumber("RecBalanceNotice", 2);
			baseDao.execute("INSERT INTO RecBalanceNotice(rb_id, rb_code, rb_kind, rb_custid, rb_custcode, rb_custname, rb_date, rb_currency, "
					+ "rb_rate, rb_cmcurrency, rb_sellercode, rb_sellername, rb_departmentcode, rb_departmentname, "
					+ "rb_status, rb_remark, rb_recorder, rb_recorddate, rb_statuscode,rb_archeckid)"
					+ " select "
					+ rb_id
					+ ",'"
					+ code
					+ "','应收款',cu_id,ac_custcode,ac_custname,sysdate,ac_currency,"
					+ "ac_rate,ac_currency,ac_sellercode,ac_sellername,em_departmentcode,em_depart,"
					+ "'"
					+ BaseUtil.getLocalMessage("ENTERING")
					+ "','对账单号'||'"
					+ rs.getObject("ac_code")
					+ "','"
					+ employee.getEm_name()
					+ "',sysdate, 'ENTERING',ac_id from archeck left join Customer on ac_custcode=cu_code left join Employee on ac_sellercode=em_code where ac_id="
					+ id);
		}
		for (Map<Object, Object> map : maps) {
			int adid = Integer.parseInt(map.get("ad_id").toString());
			int addetno = Integer.parseInt(map.get("ad_detno").toString());
			double bqty = Double.parseDouble(map.get("ad_bqty").toString());
			double qty = Double.parseDouble(map.get("ad_qty").toString());
			double zqty = Double.parseDouble(map.get("ad_zqty").toString());
			if (qty < 0 && bqty > 0) {
				BaseUtil.showError("对账数量为负数的，本次转数量不能为正数！");
			}
			if (qty > 0 && bqty < 0) {
				BaseUtil.showError("对账量为正数的，本次转数量不能为负数！");
			}
			if (bqty == 0) {
				BaseUtil.showError("本次转数量不能为0！");
			}
			if (Math.abs(zqty + bqty) > Math.abs(qty)) {
				BaseUtil.showError("<hr>[本次数量填写超出可转数量],行号：" + addetno + ",数量:" + qty + ",已转数:" + zqty + ",本次数:" + bqty + "<hr/>");
			}
			baseDao.execute("update prodiodetail set pd_bqty=" + bqty + ",pd_zqty=" + zqty
					+ " where pd_id=(select ad_pdid from archeckdetail where ad_id=" + adid + ")");
			if (rb_id != 0) {
				if ("ARBILL".equals(sourcetype)) {
					rs1 = baseDao
							.queryForRowSet(
									"select * from (select round(pd_bqty*ad_price,2) ad_amount, ad_sourcecode  from archeckdetail left join Prodiodetail on ad_pdid=pd_id where ad_id=?) left join ARBill on ab_code=ad_sourcecode",
									adid);
					while (rs1.next()) {
						baseDao.execute(
								RECBALANCENOTICEDETAIL,
								new Object[] { rb_id, detno++, rs1.getObject("ab_id"), rs1.getObject("ab_code"),
										rs1.getObject("ab_currency"), rs1.getObject("ab_payments"), rs1.getObject("ab_date"),
										rs1.getObject("ad_amount"), rs1.getObject("ab_class"), rs1.getObject("ab_sellercode"),
										rs1.getObject("ab_seller"), bqty, zqty + bqty, adid });
						amount += rs1.getDouble("ad_amount");
					}
				} else if ("PRODINOUT".equals(sourcetype)) {
					boolean bill = baseDao.isDBSetting("autoCreateApBill") && baseDao.isDBSetting("useBillOutAP");
					if (bill) {
						rs1 = baseDao
								.queryForRowSet(
										"select * from (select round(pd_bqty*ad_price,2) ad_amount, ad_sourcecode from archeckdetail left join Prodiodetail on ad_pdid=pd_id where ad_id=?) left join ARBill on ab_code=ad_sourcecode",
										adid);
					} else {
						rs1 = baseDao
								.queryForRowSet(
										"select * from (select round(pd_bqty*ad_price,2) ad_amount, ad_sourcecode from archeckdetail left join archeck on ad_acid=ac_id left join Prodiodetail on ad_pdid=pd_id where ad_id=?),ARBill where exists (select 1 from archeck where ac_id=? and ab_sourcecode=ac_code)",
										adid, id);
					}
					while (rs1.next()) {
						baseDao.execute(
								RECBALANCENOTICEDETAIL,
								new Object[] { rb_id, detno++, rs1.getObject("ab_id"), rs1.getObject("ab_code"),
										rs1.getObject("ab_currency"), rs1.getObject("ab_payments"), rs1.getObject("ab_date"),
										rs1.getObject("ad_amount"), rs1.getObject("ab_class"), rs1.getObject("ab_sellercode"),
										rs1.getObject("ab_seller"), bqty, bqty + zqty, adid });
						amount += rs1.getDouble("ad_amount");
					}
				}
				baseDao.execute("update archeckdetail set ad_zqty=? where ad_id=?", new Object[] { zqty + bqty, adid });

			}
		}
		if (amount != 0) {
			baseDao.execute("update RecBalanceNotice set rb_amount=?,rb_cmamount=?,rb_actamount=? where rb_id=?", new Object[] { amount,
					amount, amount, rb_id });
		}
		log = "转入成功<hr> 回款通知单号:" + "<a href=\"javascript:openUrl('jsps/fa/ars/recBalanceNotice.jsp?formCondition=rb_idIS" + rb_id
				+ "&gridCondition=rbd_rbidIS" + rb_id + "&whoami=RecBalanceNotice!YS')\">" + code + "</a>&nbsp;";
		return log;
	}
}
