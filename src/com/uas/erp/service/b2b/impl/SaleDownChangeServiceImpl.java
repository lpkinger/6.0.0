package com.uas.erp.service.b2b.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.bind.Status;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlMap;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.b2b.SaleDownChangeService;
import com.uas.erp.service.scm.SaleChangeService;

@Service("saleDownChangeService")
public class SaleDownChangeServiceImpl implements SaleDownChangeService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private SaleChangeService saleChangeService;

	@Override
	public void submitSaleDownChange(int sc_id, String caller) {
		checkNewProduct(sc_id);
		// 执行提交前的其它逻辑
		SqlRowList rs = baseDao.queryForRowSet(
				"select sc_statuscode,sc_agreed,sc_newpaymentscode,sc_payments,sc_newpayments from SaleDownChange where sc_id=?", sc_id);
		if (rs.next()) {
			StateAssert.submitOnlyEntering(rs.getString(1));
			if (rs.getString(3) == null && !rs.getString(4).equals(rs.getString(5)))
				BaseUtil.showError("请先选择新付款方式编号！");
			handlerService.beforeSubmit(caller, sc_id);
			// 执行提交操作
			baseDao.submit("SaleDownChange", "sc_id=" + sc_id, "sc_status", "sc_statuscode");
			// 记录操作
			baseDao.logger.submit(caller, "sc_id", sc_id);
			// 执行提交后的其它逻辑
			handlerService.afterSubmit(caller, sc_id);
			if (rs.getGeneralInt(2) != 0) {
				// 客户已确认变更，不需要走流程确认，自动审核
				auditSaleDownChange(sc_id, caller);
			}
		}
	}

	@Override
	public void resSubmitSaleDownChange(int sc_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("SaleDownChange", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, sc_id);
		// 执行反提交操作
		baseDao.resOperate("SaleDownChange", "sc_id=" + sc_id, "sc_status", "sc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "sc_id", sc_id);
		handlerService.afterResSubmit(caller, sc_id);
	}

	@Override
	public void auditSaleDownChange(int sc_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("SaleDownChange", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.auditOnlyCommited(status);
		handlerService.beforeAudit(caller, sc_id);
		// 执行审核操作
		baseDao.audit("SaleDownChange", "sc_id=" + sc_id, "sc_status", "sc_statuscode", "sc_auditdate", "sc_auditman"); // 记录操作
		baseDao.logger.audit(caller, "sc_id", sc_id);
		handlerService.afterAudit(caller, sc_id);
		baseDao.execute("update SaleDownChange set sc_sendstatus='待上传' where sc_id=? and nvl(sc_sendstatus,' ')<>'已下载'", sc_id);
		Short sc_agreed = baseDao.getFieldValue("SaleDownChange", "abs(nvl(sc_agreed,0))", "sc_id=" + sc_id, Short.class);
		if (sc_agreed == Constant.YES) {
			// 变更SaleDown
			changeSaleDown(sc_id);
			// 变更Sale
			changeSale(sc_id);
		}
	}

	/**
	 * 变更信息写到SaleDown
	 * 
	 * @param sc_id
	 */
	private void changeSaleDown(int sc_id) {
		baseDao.execute(
				"update SaleDown set (sa_payments,sa_currency,sa_rate)=(select max(nvl(sc_newpayments,sc_payments)),max(nvl(sc_currency,sc_newcurrency)),max(nvl(sc_rate,sc_newrate)) from SaleDownChange where sc_sacode=sa_code and sc_id=?) where sa_code=(select sc_sacode from SaleDownChange where sc_id=?)",
				sc_id, sc_id);
		baseDao.execute(
				"update SaleDownDetail set (sd_custprodcode,sd_custproddetail,sd_custprodspec,sd_custprodunit,sd_prodcode)=(select max(scd_newcustprodcode),max(scd_newcustproddetail),max(scd_newcustprodspec),max(scd_newcustprodunit),max(scd_newprodcode) from SaleDownChangeDetail where scd_sacode=sd_code and scd_sddetno=sd_detno and scd_scid=?) where exists (select 1 from SaleDownChangeDetail where scd_scid=? and scd_sacode=sd_code and scd_sddetno=sd_detno and nvl(scd_newprodcode,scd_prodcode)<>scd_prodcode)",
				sc_id, sc_id);
		baseDao.execute(
				"update SaleDownDetail set (sd_delivery,sd_qty,sd_price,sd_taxrate)=(select max(nvl(scd_newdelivery,scd_delivery)),max(nvl(scd_newqty,scd_qty)),max(nvl(scd_newprice,scd_price)),max(nvl(scd_newtaxrate,scd_taxrate)) from SaleDownChangeDetail where scd_scid=? and scd_sacode=sd_code and scd_sddetno=sd_detno) where exists (select 1 from SaleDownChangeDetail where scd_scid=? and scd_sacode=sd_code and scd_sddetno=sd_detno)",
				sc_id, sc_id);
	}

	/**
	 * 若销售订单已产生，则同时修改销售订单
	 * 
	 * @param sc_id
	 */
	private void changeSale(int sc_id) {
		Integer sa_id = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select max(sa_id) from sale where (sa_pocode,sa_custcode)=(select sa_code,sa_custcode from saledown left join saledownchange on sa_code=sc_sacode and sa_customeruu=sc_custuu where sc_id=?)",
						Integer.class, sc_id);
		if (sa_id != null) {
			int changeId = createSaleChange(sa_id, sc_id);
			if (changeId != 0) {
				try {
					saleChangeService.auditSaleChange(changeId, "SaleChange");
				} catch (RuntimeException e) {

				}
			}
		}
	}

	/**
	 * 直接生成一张销售变更单
	 */
	private int createSaleChange(int sa_id, int sc_id) {
		SqlRowList rs = baseDao.queryForRowSet(
				"select * from sale,saledownchange where sa_id=? and sc_id=? and sa_pocode=sc_sacode and sa_custcode=sc_custcode", sa_id,
				sc_id);
		if (rs.next()) {
			int changeId = baseDao.getSeqId("SaleChange_SEQ");
			String code = baseDao.sGetMaxNumber("SaleChange", 2);
			SqlMap sql = new SqlMap("SaleChange");
			sql.set("sc_id", changeId);
			sql.set("sc_code", code);
			sql.set("sc_saleid", rs.getInt("sa_id"));
			sql.set("sc_salecode", rs.getString("sa_code"));
			sql.set("sc_recorderman", rs.getString("sc_recorder"));
			sql.setSpecial("sc_indate", "sysdate");
			sql.set("sc_remark", rs.getString("sc_remark"));
			sql.set("sc_status", Status.COMMITED.display());
			sql.set("sc_statuscode", Status.COMMITED.code());
			sql.set("sc_pocode", rs.getString("sa_pocode"));
			sql.set("sc_sakind", rs.getGeneralString("sa_kind"));
			sql.execute();
			SqlRowList rd = baseDao
					.queryForRowSet(
							"select * from saledetail,sale,saledownchangedetail,saledownchange where sd_said=? and scd_scid=? and sd_said=sa_id and scd_scid=sc_id and sa_pocode=sc_sacode and sd_detno=scd_sddetno",
							sa_id, sc_id);
			while (rd.next()) {
				sql = new SqlMap("SaleChangeDetail");
				sql.setSpecial("scd_id", "SaleChangeDetail_SEQ.nextval");
				sql.set("scd_scid", changeId);
				sql.set("scd_code", code);
				sql.set("scd_said", rs.getInt("sa_id"));
				sql.set("scd_sacode", rs.getString("sa_code"));
				sql.set("scd_sddetno", rd.getInt("sd_detno"));
				sql.set("scd_prodid", rs.getGeneralInt("sd_prodid"));
				sql.set("scd_prodcode", rs.getString("sd_prodcode"));
				sql.set("scd_newprodcode", rd.getString("scd_newprodcode"));
				sql.set("scd_qty", rd.getObject("sd_qty"));
				sql.set("scd_newqty", rd.getObject("scd_newqty"));
				sql.set("scd_price", rd.getObject("sd_price"));
				sql.set("scd_newprice", rd.getObject("scd_newprice"));
				sql.setDate("scd_delivery", rd.getDate("sd_delivery"));
				sql.setDate("scd_newdelivery", rd.getDate("scd_newdelivery"));
				sql.set("scd_taxrate", rd.getObject("sd_taxrate"));
				sql.set("scd_newtaxrate", rd.getObject("scd_newtaxrate"));
				sql.set("scd_payments", rs.getGeneralString("sa_paymentscode"));
				sql.set("scd_paymentname", rs.getGeneralString("sa_payments"));
				sql.set("scd_newpayments", rs.getGeneralString("sc_newpaymentscode"));
				sql.set("scd_currency", rs.getString("sa_currency"));
				sql.set("scd_newcurrency", rs.getString("sc_newcurrency"));
				// sql.set("scd_rate", rs.getObject("sa_rate"));
				// sql.set("scd_newrate", rs.getObject("sc_newrate"));
				sql.set("scd_total", rd.getObject("sd_total"));
				sql.set("scd_newtotal", NumberUtil.formatDouble(rd.getGeneralDouble("scd_newqty") * rd.getGeneralDouble("scd_newprice"), 2));
				sql.execute();
			}
			return changeId;
		}
		return 0;
	}

	/**
	 * 判断物料资料是否已按客户物料关系更新进去
	 * 
	 * @param sc_id
	 */
	private void checkNewProduct(int sc_id) {
		baseDao.execute(
				"update SaleDownChangeDetail sd1 set scd_newprodcode=(select max(pc_prodcode) from productcustomer,saledownchange,saledownchangedetail sd2 where sc_id=scd_scid and pc_custcode=sc_custcode and pc_custprodcode=sd2.scd_newcustprodcode and nvl(pc_custproddetail,' ')=nvl(sd2.scd_newcustproddetail,' ') and nvl(pc_custprodspec,' ')=nvl(sd2.scd_newcustprodspec,' ') and sd1.scd_id=sd2.scd_id) where scd_scid = ?",
				sc_id);
		String noneProduct = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(scd_detno) from SaleDownChangeDetail where scd_scid=? and scd_newprodcode is null", String.class, sc_id);
		if (noneProduct != null)
			BaseUtil.showError("行：" + noneProduct + " 的新物料还未建立【客户物料对照关系】");
	}

	@Override
	public void onChangeAgreed(String sc_code) {
		Integer sc_id = baseDao.getFieldValue("SaleDownChange", "sc_id", "sc_code='" + sc_code + "'", Integer.class);
		// 变更SaleDown
		changeSaleDown(sc_id);
		// 变更Sale
		changeSale(sc_id);
	}

	@Override
	public String confirmSaleDownChange(int id, int agreed, String remark) {
		// TODO Auto-generated method stub
		checkNewProduct(id);
		baseDao.execute("update SaleDownChange set sc_sendstatus='待上传',sc_agreed=" + agreed + ",sc_replyremark='" + remark
				+ "' where sc_id=? and nvl(sc_sendstatus,' ')<>'已下载'", id);
		baseDao.audit("SaleDownChange", "sc_id=" + id, "sc_status", "sc_statuscode", "sc_auditdate", "sc_auditman"); // 记录操作
		baseDao.logger.audit("SaleDownChange", "sc_id", id);
		Short sc_agreed = baseDao.getFieldValue("SaleDownChange", "abs(nvl(sc_agreed,0))", "sc_id=" + id, Short.class);
		if (sc_agreed == Constant.YES) {
			// 变更SaleDown
			changeSaleDown(id);
			// 变更Sale
			changeSale(id);
		}
		return null;
	}
	
	@Override
	public void updateSaleDownChange(String caller,String formStore,String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave("SaleDownChange", new Object[] { gstore, gstore });
		String formSql=SqlUtil.getUpdateSqlByFormStore(store, "SaleDownChange", "sc_id");
//		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "SaleDownChangeDetail", "scd_id");
//		for (Map<Object, Object> s : gstore) {
//			if (s.get("scd_id") == null || s.get("scd_id").equals("") || s.get("scd_id").toString().equals("0")) {// 新添加的数据，id不存在
//				int id = baseDao.getSeqId("SaleDownChangeDetail_SEQ");
//				String sql = SqlUtil.getInsertSqlByMap(s, "SaleDownChangeDetail", new String[] { "scd_id" },
//						new Object[] { id });
//				gridSql.add(sql);
//			}
//		}
		baseDao.execute(formSql);
//		baseDao.execute(gridSql);
		baseDao.logger.update("SaleDownChange", "sc_id", store.get("sc_id"));
	}
}
