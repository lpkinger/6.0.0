package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.b2b.model.DeputyOrder;
import com.uas.b2b.model.TaskLog;
import com.uas.b2c.service.seller.SaleOrderService;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.bind.Status;
import com.uas.erp.core.support.Assert;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.dao.common.SaleDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Key;
import com.uas.erp.model.Master;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.common.ProcessService;
import com.uas.erp.service.common.SingleFormItemsService;
import com.uas.erp.service.scm.SaleClashService;
import com.uas.erp.service.scm.SaleService;

@Service
public class SaleServiceImpl implements SaleService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private SaleDao saleDao;
	@Autowired
	private SaleClashService saleClashService;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private ProcessService processService;
	@Autowired
	private TransferRepository transferRepository;
	@Autowired
	private SingleFormItemsService singleFormItemsService;
	@Autowired
	private SaleOrderService saleOrderService;

	private void checkCode(Object sa_id, Object sacode) {
		// 判断订单编号在订单资料中是否存在重复
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(sa_code) from Sale where sa_code=? and sa_id<>?", String.class, sacode, sa_id);
		if (dets != null) {
			BaseUtil.showError("订单编号在销售订单中已存在!订单号：" + dets);
		}
		// 判断订单编号在销售预测单中是否存在重复
		dets = baseDao.getJdbcTemplate().queryForObject("select WM_CONCAT(sf_code) from SaleForecast where sf_code=?",
				String.class, sacode);
		if (dets != null) {
			BaseUtil.showError("订单编号在销售预测单已存在!预测单号：" + dets);
		}
	}

	@Override
	public void deleteSale(int sa_id, String caller) {
		// 只能删除[在录入]的订单资料!
		Object status = baseDao.getFieldDataByCondition("Sale", "sa_statuscode", "sa_id=" + sa_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, sa_id);
		baseDao.delCheck("Sale", sa_id);
		// 还原SaleDown、SaleNotifyDown
		baseDao.execute(
				"update saledown set sa_status=?,sa_statuscode=? where sa_code=(select sale.sa_pocode from sale where sale.sa_id=?)",
				Status.AUDITED.display(), Status.AUDITED.code(), sa_id);
		baseDao.execute(
				"update salenotifydown set sn_ordercode=null,sn_orderdetno=null where sn_ordercode=(select sale.sa_code from sale where sa_id=?)",
				sa_id);
		/*
		 *  反馈编号2017080199  信扬代采订单状态变更  maz 
		 */
		List<DeputyOrder>orders = new ArrayList<DeputyOrder>();
		String sql = "select sa_fromcode code, sa_code salecode, sa_backreason remark from sale where sa_sendstatus = '待上传' and sa_kind = '代采订单'";
		orders = baseDao.query(sql, DeputyOrder.class);
		Master master  = SystemSession.getUser().getCurrentMaster();
		if (!CollectionUtils.isEmpty(orders)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJsonDeep(orders));
			try {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/sale/deputyOrder/notAgreed/back?access_id="
						+ master.getMa_uu(), params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
				}
				baseDao.save(new TaskLog("(卖家)代采订单--删除代采订单", 0, response));
			} catch (Exception e) {  
				e.printStackTrace();
			}
		}
		// 删除sale
		baseDao.deleteById("sale", "sa_id", sa_id);
		// 删除saleDetail
		saleDao.deleteSale(sa_id);
		// 记录操作
		baseDao.logger.delete(caller, "sa_id", sa_id);
		if ("Sale!Abnormal".equals(caller)) {
			baseDao.logger.delete("Sale", "sa_id", sa_id);
		}
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, sa_id);
	}

	@Override
	public void saveSale(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		int said = Integer.parseInt(store.get("sa_id").toString());
		if (store.get("sa_code") == null || "".equals(store.get("sa_code"))) {
			BaseUtil.showError("订单编号不能为空！");
		}
		checkPrecision(said);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("sale", "sa_code='" + store.get("sa_code") + "'");
		Assert.isTrue(bool, "scm.sale.sale.save_sacodeHasExist");
		checkCode(store.get("sa_id"), store.get("sa_code"));
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		store.put("sa_printstatuscode", Status.UNPRINT.code());
		store.put("sa_printstatus", Status.UNPRINT.display());
		if(baseDao.isDBSetting("Sale","addClerkCode") && StringUtil.hasText(store.get("sa_sellercode"))){
			Object code = store.get("sa_code");
			store.remove("sa_code");
			store.put("sa_code", code.toString()+store.get("sa_sellercode").toString());
		}
		// 保存sale
		String formSql = SqlUtil.getInsertSqlByMap(store, "Sale");
		baseDao.execute(formSql);
		// 保存saleDetail
		for (Map<Object, Object> map : grid) {
			map.put("sd_statuscode", Status.ENTERING.code());
			map.put("sd_status", Status.ENTERING.display());
			map.put("sd_code", store.get("sa_code"));
			map.remove("sd_yqty");
			map.remove("sd_sendqty");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "SaleDetail", "sd_id");
		baseDao.execute(gridSql);
		String priceerror = saleDao.getPrice(said);
		defaultTax(caller, store.get("sa_id"));
		getTotal(store.get("sa_id"));
		// 记录操作
		baseDao.logger.save(caller, "sa_id", store.get("sa_id"));
		if ("Sale!Abnormal".equals(caller)) {
			baseDao.logger.save("Sale", "sa_id", store.get("sa_id"));
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
		baseDao.execute(
				"update saledetail set sd_saleforecastdetailid=(select sd_id from SaleForecastDetail,SaleForecast where sd_sfid=sf_id and SaleForecast.sf_code=SaleDetail.sd_forecastcode and SaleDetail.sd_forecastdetno=SaleForecastDetail.sd_detno) where sd_said="
						+ said + " and nvl(sd_forecastcode,' ')<>' '");
		/*
		 * baseDao.execute(
		 * "update saledetail set sd_pmcdate=sd_delivery where sd_said=" + said
		 * + " and sd_pmcdate is null");
		 */// 更新pmc日期的操作放到了审核的时候
		baseDao.execute("update saledetail set sd_actdelivery=sd_delivery where sd_said=" + said
				+ " and sd_actdelivery is null");
		baseDao.execute(
				"update saledetail set sd_bomid=(select NVL(max(bo_id),0) from bom where bo_mothercode=sd_prodcode) where sd_said="
						+ store.get("sa_id"));
		// maz 2017100231 销售订单保存更新时也抓取上一次的销售单价
		SqlRowList rs1 = baseDao.queryForRowSet("select sa_custcode,sa_currency,sd_prodcode,sd_id from sale left join saledetail on sa_id=sd_said where sa_id="+store.get("sa_id")+"");
		while(rs1.next()){
			baseDao.execute("update saledetail set sd_lastprice=nvl((select sd_price from (select sd_price from saledetail left join sale on sd_said=sa_id where sa_custcode='"+rs1.getString("sa_custcode")+"' and sa_currency='"+rs1.getString("sa_currency")+"' and sd_prodcode='"+rs1.getString("sd_prodcode")+"' and sa_status='已审核' and sa_id<>"+store.get("sa_id")+" order by sa_auditdate desc,sd_id desc) where rownum=1),0) where sd_id="+rs1.getString("sd_id")+"");
		}
		sale_commit_minus(store.get("sa_id"));
		checkPO(store.get("sa_code"), store.get("sa_pocode"));
		defaultCycle(caller,store.get("sa_id"));
		if (priceerror != null && priceerror.length() > 0) {
			BaseUtil.appendError(priceerror.toString());
		}
	}

	public int saveCustomerSimple(String formStore) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);

		String cu_code = baseDao.sGetMaxNumber("CUSTOMER", 2);
		int cu_id = baseDao.getSeqId("CUSTOMER_SEQ");

		Object cu_name = store.get("cu_name");

		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put("cu_id", cu_id);
		map.put("cu_code", cu_code);
		map.put("cu_name", cu_name);

		// 保存sale
		String formSql = SqlUtil.getInsertSqlByMap(map, "customer");
		baseDao.execute(formSql);

		return cu_id;
	}

	/**
	 * 客户PO号重复
	 */
	private void checkPO(Object sacode, Object pocode) {
		String sa_pocode = "";
		if(pocode!=null){
			sa_pocode = pocode.toString().replace("'", "''");
		}
		Object code = baseDao.getFieldDataByCondition("Sale", "sa_code",
				"sa_code <> '" + sacode + "' AND sa_pocode='" + sa_pocode + "'");
		if (code != null) {
			BaseUtil.appendError("客户PO号在其它销售订单中已存在，销售单号：" + code);
		}
	}

	void getTotal(Object said) {
		baseDao.execute("update saledetail set sd_code=(select sa_code from sale where sd_said=sa_id) where sd_said="
				+ said + " and not exists (select 1 from sale where sd_code=sa_code)");
		baseDao.execute("update Saledetail set sd_total=round(nvl(sd_price,0)*nvl(sd_qty,0),2) where sd_said=" + said);
		baseDao.execute(
				"update Saledetail set sd_costprice=round(nvl(sd_price,0)/(1+nvl(sd_taxrate,0)/100),6) where sd_said="
						+ said);
		baseDao.execute(
				"update Saledetail set sd_taxtotal=round(nvl(sd_costprice,0)*nvl(sd_qty,0),2) where sd_said=" + said);
		// 修改主单据的总金额
		baseDao.execute(
				"update sale set sa_total=nvl((select sum(nvl(sd_total,0)) from saledetail where saledetail.sd_said = sale.sa_id),0) where sa_id="
						+ said);
		baseDao.execute("update sale set sa_totalupper=L2U(nvl(sa_total,0)) WHERE sa_id=" + said);
	}

	@Override
	public String updateSale(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		if (store.get("sa_code") == null || "".equals(store.get("sa_code"))) {
			BaseUtil.showError("订单编号不能为空！");
		}
		checkCode(store.get("sa_id"), store.get("sa_code"));
		// 只能修改[在录入]的订单资料!
		Object status = baseDao.getFieldDataByCondition("Sale", "sa_statuscode", "sa_id=" + store.get("sa_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		int said = Integer.parseInt(store.get("sa_id").toString());
		checkPrecision(said);
		// 设置更新时间
		store.put("sa_updateman", SystemSession.getUser().getEm_name());
		store.put("sa_updatedate", DateUtil.currentDateString(Constant.YMD_HMS));
		// 更新sale
		for(Map<Object, Object> s : gstore){
			s.remove("sd_yqty");
		}
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Sale", "sa_id");
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "SaleDetail", "sd_id");
		for (Map<Object, Object> s : gstore) {
			Object sdid = s.get("sd_id");
			s.put("sd_total",
					Double.parseDouble(s.get("sd_qty").toString()) * Double.parseDouble(s.get("sd_price").toString()));
			if (sdid == null || sdid.equals("") || sdid.equals("0") || Integer.parseInt(sdid.toString()) == 0) {// 新添加的数据，id不存在
				s.put("sd_statuscode", Status.ENTERING.code());
				s.put("sd_code", store.get("sa_code"));
				s.put("sd_status", Status.ENTERING.display());
				/* s.remove("sd_pmcdate"); */// pmc日期可以填写与交货日期不一样，所以不能强制清空
				s.remove("sd_sendqty");
				String sql = SqlUtil.getInsertSql(s, "SaleDetail", "sd_id");
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		String priceerror = saleDao.getPrice(said);
		defaultTax(caller, said);
		getTotal(said);
		// 记录操作
		baseDao.logger.update(caller, "sa_id", store.get("sa_id"));
		if ("Sale!Abnormal".equals(caller)) {
			baseDao.logger.update("Sale", "sa_id", store.get("sa_id"));
		}
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
		/*
		 * baseDao.execute(
		 * "update saledetail set sd_pmcdate=sd_delivery where sd_said=" +
		 * store.get("sa_id") + " and sd_pmcdate is null");
		 */// 更新pmc日期操作放到了审核的时候
		baseDao.execute("update saledetail set sd_actdelivery=sd_delivery where sd_said=" + store.get("sa_id")
				+ " and sd_actdelivery is null");
		//20170303新增更新，用户在刚开始的时候选择了冲销的预测单号和明细，抓取到了预测明细ID，但是后面又把预测单号给清空了，清空单号的同时需要清空预测的明细ID,否则会导致预测单明细已转数发现错误
		baseDao.execute(
				"update saledetail set sd_saleforecastdetailid=0 where sd_said=? and nvl(sd_forecastcode,' ')=' ' and nvl(sd_saleforecastdetailid,0)<>0",said);		
		baseDao.execute(
				"update saledetail set sd_saleforecastdetailid=(select sd_id from SaleForecastDetail,SaleForecast where sd_sfid=sf_id and SaleForecast.sf_code=SaleDetail.sd_forecastcode and SaleDetail.sd_forecastdetno=SaleForecastDetail.sd_detno) where sd_said="
						+ said + " and nvl(sd_forecastcode,' ')<>' '");
		baseDao.execute(
				"update saledetail set sd_bomid=(select NVL(max(bo_id),0) from bom where bo_mothercode=sd_prodcode) where sd_said="
						+ store.get("sa_id"));
		// maz 2017100231 销售订单保存更新时也抓取上一次的销售单价
		SqlRowList rs1 = baseDao.queryForRowSet("select sa_custcode,sa_currency,sd_prodcode,sd_id from sale left join saledetail on sa_id=sd_said where sa_id="+store.get("sa_id")+"");
		while(rs1.next()){
			baseDao.execute("update saledetail set sd_lastprice=nvl((select sd_price from (select sd_price from saledetail left join sale on sd_said=sa_id where sa_custcode='"+rs1.getString("sa_custcode")+"' and sa_currency='"+rs1.getString("sa_currency")+"' and sd_prodcode='"+rs1.getString("sd_prodcode")+"' and sa_status='已审核' and sa_id<>"+store.get("sa_id")+" order by sa_auditdate desc,sd_id desc) where rownum=1),0) where sd_id="+rs1.getString("sd_id")+"");
		}
		sale_commit_minus(store.get("sa_id"));
		checkPO(store.get("sa_code"), store.get("sa_pocode"));
		defaultCycle(caller,store.get("sa_id"));
		if (priceerror != null && priceerror.length() > 0) {
			BaseUtil.appendError(priceerror.toString());
		}
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void auditSale(int sa_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("Sale",
				new String[] { "sa_statuscode", "sa_code", "sa_pocode", "sa_custcode", "sa_recorder" },
				"sa_id=" + sa_id);
		StateAssert.auditOnlyCommited(status[0]);
		checkCustomer(sa_id);
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(sd_detno) from SaleDetail,sale,salekind where sd_said=sa_id and sa_kind=sk_name and NVL(sk_pricekind,' ')<>'NG' AND nvl(sd_isspecial,0)=0 and nvl(sd_price,0)< nvl(sd_purcprice,0) and sd_said=?",
				String.class, sa_id);
		if (dets != null) {
			BaseUtil.showError("不允许特价的情况下，销售单价小于订价，不允许审核！行号：" + dets);
		}
		if (baseDao.isDBSetting("updateBusinessChance")) {
			// 更新当前商机阶段
			String item = baseDao.getJdbcTemplate().queryForObject(
					"select case when sk_issample=0 then 'Sale' when sk_issample=-1 then 'Sample' else 'Sale' end from Sale left join SaleKind on sk_name=sa_kind where sa_id=?",
					String.class, sa_id);
			Object bsname = baseDao.getFieldDataByCondition("BusinessChanceStage", "bs_name",
					"bs_relativeitem='" + item + "'");
			Object date = baseDao.getFieldDataByCondition("Sale", "to_char(sa_date,'yyyy-MM-dd')", "sa_id=" + sa_id);

			List<Object[]> data = baseDao.getFieldsDatasByCondition("SaleDetail  left join Sale on sd_said=sa_id",
					new String[] { "sd_bcid", "sd_prodcode", "sd_detno" }, "sd_said=" + sa_id);
			for (Object[] os : data) {
				if (os[0] == null || os[0].equals("") || os[0].equals("0") || Integer.parseInt(os[0].toString()) == 0) {
					String sql = "update saledetail set sd_bcid=nvl((select max(bc_id) from Sale,businesschance"
							+ " where sa_id=sd_said and sa_custcode=bc_custcode and sd_prodcode=bc_model and bc_status<>'已结案'),0) where "
							+ "sd_said=" + sa_id + " and nvl(sd_bcid,0)=0";
					baseDao.execute(sql);
				}
				Object bc_id = baseDao.getFieldDataByCondition("saledetail", "sd_bcid",
						" sd_prodcode='" + os[1] + "' and sd_said=" + sa_id);
				Integer bs_detno = baseDao.getFieldValue("businesschancestage", "bs_detno", "bs_name='" + bsname + "'",
						Integer.class);
				Integer bs_detno1 = baseDao.getFieldValue(
						"businesschance left join businesschancestage on bs_name=bc_currentprocess", "nvl(bs_detno,0)",
						"bc_id=" + bc_id, Integer.class);
				if (bs_detno != null && bs_detno1 != null) {
					if (bs_detno >= bs_detno1) {
						if (item.equals("Sale")) {
							baseDao.updateByCondition("BusinessChance",
									"bc_currentprocess='" + bsname + "',bc_desc" + bs_detno + "='" + bsname
											+ "',bc_date" + bs_detno + "=to_date('" + date.toString()
											+ "','yyyy-MM-dd')",
									"bc_id=" + bc_id);
							if (bsname != null && Integer.parseInt(bc_id.toString()) != 0) {
								Object bscode = baseDao.getFieldDataByCondition("BusinessChanceStage", "bs_code",
										"bs_name='" + bsname + "'");
								// 插入一条记录到商机动态表
								int bcd_id = baseDao.getSeqId("BusinessChanceData_seq");
								String link = "jsps/scm/sale/sale.jsp?whoami=Sale&formCondition=sa_idIS" + sa_id
										+ "&gridCondition=sd_saidIS" + sa_id;
								String contactSql = "insert into BusinessChanceData (bcd_id,bcd_bcid,bcd_code,bcd_bscode,bcd_bsname,bcd_date,bcd_man,bcd_statuscode,bcd_status,bcd_sourcecode,bcd_sourcelink) values ("
										+ bcd_id + "," + bc_id + ",'" + baseDao.sGetMaxNumber("BusinessChanceData", 2)
										+ "','" + bscode + "','" + bsname + "'," + "to_date('" + date.toString()
										+ "','yyyy-MM-dd')" + ",'" + status[4] + "','" + "ENTERING" + "','"
										+ BaseUtil.getLocalMessage("ENTERING") + "','" + status[1] + "','" + link
										+ "')";
								baseDao.execute(contactSql);
							}
						}
					}
				}
			}
		}
		if (baseDao.isDBSetting("CopCheck")) {
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(sd_detno) from SaleDetail left join Sale on sd_said=sa_id where (sd_forecastcode, sd_forecastdetno) in (select saleforecast.sf_code, saleforecastdetail.sd_detno from saleforecastdetail left join saleforecast on saleforecast.sf_id=saleforecastdetail.sd_sfid where nvl(Sale.sa_cop,' ')<>nvl(saleforecast.sf_cop,' ')) and sd_said=?",
					String.class, sa_id);
			if (dets != null) {
				BaseUtil.showError("明细行预测单所属公司与销售单所属公司不一致，不允许进行当前操作!行号：" + dets);
			}
		}
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(sd_detno) from SaleDetail left join Product on sd_prodcode=pr_code left join Productlevel on pr_level=pl_levcode where sd_said=? and nvl(pl_nosale,0)<>0",
				String.class, sa_id);
		if (dets != null) {
			BaseUtil.showError("明细行物料的物料等级属性为不可销售，不允许进行当前操作！行号：" + dets);
		}
		// 销售订单审核时 驳回状态修改为 已同意待上传
		defaultTax(caller, sa_id);
		allowZeroTax(caller, sa_id);
		getTotal(sa_id);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, sa_id);
		// 执行审核操作
		baseDao.audit("Sale", "sa_id=" + sa_id, "sa_status", "sa_statuscode", "sa_auditdate", "sa_auditman");
		baseDao.audit("SaleDetail", "sd_said=" + sa_id, "sd_status", "sd_statuscode");
		// 当PMC日期为空时，自动把交货日期赋值过去
		baseDao.execute(
				"update saledetail set sd_pmcdate=sd_delivery where sd_said=" + sa_id + " and sd_pmcdate is null");
		saleClashService.createSaleClash(sa_id, "Sale");
		int creatbill = baseDao.getCountByCondition("Sale left join salekind on sa_kind=sk_name",
				"sa_id=" + sa_id + " and nvl(sk_createbill,0)=1");
		int bill = baseDao.getCountByCondition("ARBILL", "ab_sourceid=" + sa_id + " and AB_SOURCETYPE='销售订单'");
		if (bill == 0) {
			if (creatbill > 0) {
				Key key = transferRepository.transfer("Sale!ToARBill", sa_id);
				// 转入明细
				transferRepository.transferDetail("Sale!ToARBill", sa_id, key);
				baseDao.execute(
						"update arbilldetail set abd_code=(select ab_code from arbill where abd_abid=ab_id) where abd_abid="
								+ key.getId() + " and not exists (select 1 from arbill where abd_code=ab_code)");
				baseDao.execute("update arbilldetail set abd_aramount=ROUND(abd_thisvoprice*abd_qty,2) WHERE abd_abid="
						+ key.getId());
				baseDao.execute(
						"update arbilldetail set abd_noaramount=ROUND(abd_thisvoprice*abd_qty/(1+abd_taxrate/100),2) WHERE abd_abid="
								+ key.getId());
				baseDao.execute(
						"update arbilldetail set abd_taxamount=NVL(abd_aramount,0)-NVL(abd_noaramount,0) WHERE abd_abid="
								+ key.getId());
				// 更新ARBill主表的金额
				baseDao.execute(
						"update arbill set ab_aramount=round((select sum(abd_aramount) from arbilldetail where abd_abid="
								+ key.getId() + "),2) where ab_id=" + key.getId());
				baseDao.execute(
						"update arbill set ab_taxamount=(select sum(round(((abd_thisvoprice*abd_qty*abd_taxrate/100)/(1+abd_taxrate/100)),2)) from arbilldetail where abd_abid="
								+ key.getId() + ")+ab_differ where ab_id=" + key.getId());
				baseDao.execute(
						"update saledetail set sd_statuscode='FINISH',sd_status='已结案',sd_enddate=sysdate where sd_said="
								+ sa_id);
				baseDao.execute("update sale set sa_statuscode='FINISH',sa_status='已结案',sa_enddate=sysdate where sa_id="
						+ sa_id);

			}
		}
		// 自动生成客户物料对照表的参数配置
		if (baseDao.isDBSetting(caller, "autoProductCustomer")) {
			// 先取出这张销售订单的这个客户的最大序号
			Object obj = baseDao.getFieldDataByCondition("ProductCustomer", "nvl(max(PC_DETNO),0)",
					"pc_custcode=(select sa_custcode from sale where sa_id=" + sa_id + ")");
			int detno = Integer.parseInt(obj.toString());
			String sql2 = "select DISTINCT cu_id,pr_id,sd_custprodcode,sd_custproddetail,sd_prodcustcode,pr_unit,sa_custcode,sa_custname,sd_prodcode from sale left join saledetail on sd_said=sa_id left join customer on sa_custcode=cu_code left join Product on sd_prodcode=pr_code where sa_id='"
					+ sa_id
					+ "' and nvl(sd_custprodcode,' ')<>' 'and not exists (select 1 from ProductCustomer where pc_custcode=sa_custcode and pc_prodcode=sd_prodcode)";
			String sql3 = "select DISTINCT cu_id,cu_code,pr_id,nvl(sd_custprodcode,' '),nvl(sd_custproddetail,' '),nvl(sd_prodcustcode,' '),pr_unit,sa_custcode,sa_custname,sd_prodcode from sale left join saledetail on sd_said=sa_id left join customer on sa_custcode=cu_code left join Product on sd_prodcode=pr_code where sa_id='"
					+ sa_id
					+ "' and nvl(sd_custprodcode,' ')<>' 'and exists (select 1 from ProductCustomer where pc_custcode=sa_custcode and pc_prodcode=sd_prodcode)";
			SqlRowList rs2 = baseDao.queryForRowSet(sql3);
			StringBuffer sb = new StringBuffer();
			while (rs2.next()) {
				String sd_prodcode = rs2.getString("sd_prodcode");
				String sql4 = "select nvl(pc_custprodcode,' '),nvl(pc_custproddetail,' '),nvl(pc_custprodspec,' ') from ProductCustomer where pc_custcode='"
						+ rs2.getString("cu_code") + "' and pc_prodcode='" + sd_prodcode + "'";
				SqlRowList rs3 = baseDao.queryForRowSet(sql4);
				while (rs3.next()) {
					/*
					 * if(!((rs3.getString("pc_custprodcode")==null?"":rs3.
					 * getString ( "pc_custprodcode")).equals(rs2.getString(
					 * "sd_custprodcode"
					 * )))||!((rs3.getString("pc_custproddetail")==null?"":rs3.
					 * getString("pc_custproddetail")).equals(rs2.getString(
					 * "sd_custproddetail"
					 * ))&&(rs3.getString("pc_custprodcode")==
					 * null?"":rs3.getString
					 * ("pc_custprodcode"))!=rs2.getString("sd_custproddetail"
					 * )))
					 * {//||!((rs3.getString("pc_custproddetail")==null?"":rs3
					 * .getString("pc_custproddetail")).equals(rs2.getString(
					 * "sd_custproddetail"
					 * )))||!((rs3.getString("pc_custprodspec"
					 * )==null?"":rs3.getString
					 * ("pc_custprodspec")).equals(rs2.getString
					 * ("sd_prodcustcode"))) sb.append("物料编号:"+sd_prodcode+
					 * "对应的客户物料编号、客户物料名称、客户规格型号与客户物料对照表中的数据不一致，请确认!<br>"); }
					 */

					if (!(rs3.getString("nvl(pc_custprodcode,'')").equals(rs2.getString("nvl(sd_custprodcode,'')")))
							|| !(rs3.getString("nvl(pc_custproddetail,'')")
									.equals(rs2.getString("nvl(sd_custproddetail,'')")))
							|| !(rs3.getString("nvl(pc_custprodspec,'')")
									.equals(rs2.getString("nvl(sd_prodcustcode,'')")))) {
						sb.append("物料编号:" + sd_prodcode + "对应的客户物料编号、客户物料名称、客户规格型号与客户物料对照表中的数据不一致，请确认!<br>");
					}
				}

			}
			if (sb.length() > 0) {
				BaseUtil.appendError(sb.toString());
			}
			SqlRowList rs1 = baseDao.queryForRowSet(sql2);
			while (rs1.next()) {
				baseDao.execute("insert into ProductCustomer(PC_ID,PC_CUSTID,PC_DETNO,PC_PRODID,PC_CUSTPRODCODE,"
						+ "PC_CUSTPRODDETAIL,PC_CUSTPRODSPEC,PC_CUSTPRODUNIT,PC_CUSTCODE,PC_CUSTNAME,PC_PRODCODE) "
						+ "values(ProductCustomer_seq.nextval," + rs1.getString("cu_id") + "," + (++detno) + ",'"
						+ rs1.getString("pr_id") + "'" + ",'" + rs1.getGeneralString("sd_custprodcode") + "','"
						+ rs1.getGeneralString("sd_custproddetail") + "','" + rs1.getGeneralString("sd_prodcustcode") + "','"
						+ rs1.getGeneralString("pr_unit") + "','" + rs1.getString("sa_custcode") + "','"
						+ rs1.getString("sa_custname") + "','" + rs1.getString("sd_prodcode") + "')");
			}
		}
		// 记录操作
		baseDao.logger.audit(caller, "sa_id", sa_id);
		/**
		 * @author wsy
		 * 反馈编号：2017040088
		 * 审核时将当前日期更新到对应客户资料中最近交易日期
		 */
		baseDao.updateByCondition("Customer", "cu_transdate=sysdate", "cu_code='"+status[3]+"'");
		if ("Sale!Abnormal".equals(caller)) {
			baseDao.logger.audit("Sale", "sa_id", sa_id);
		}
		//2018030110  新增一个备品冲销  maz  18-03-27
		StockProd(sa_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, sa_id);
	}

	@Override
	public void resAuditSale(int sa_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] objs = baseDao.getFieldsDataByCondition("Sale",
				new String[] { "sa_statuscode", "sa_turnstatuscode", "sa_sendstatuscode" }, "sa_id=" + sa_id);
		if (!String.valueOf(objs[0]).equals("AUDITED") || String.valueOf(objs[1]).equals("TURNSN")
				|| String.valueOf(objs[1]).equals("PART2SN") || String.valueOf(objs[2]).equals("TURNOUT")
				|| String.valueOf(objs[2]).equals("PARTOUT")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.sale.resaudit_onlyAudited"));
		}
		// 从B2C平台生成的销售订单不允许反审核
		/*Object ob = baseDao.getFieldDataByCondition("sale", "sa_id", "sa_id=" + sa_id + " and sa_ordertype='B2C'");
		if (ob != null) {
			BaseUtil.showError("通过优软商城生成的销售订单不允许反审核");
		}*/
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(sd_detno) from SaleDetail where nvl(sd_statuscode, ' ') in ('FINISH','FREEZE','NULLIFIED') and sd_said=?",
				String.class, sa_id);
		if (dets != null) {
			BaseUtil.showError("明细行已结案、已冻结、已作废，不允许反审核!行号：" + dets);
		}
		// 执行审核前的其它逻辑
		handlerService.beforeResAudit(caller, sa_id);
		baseDao.resAuditCheck("Sale", sa_id);
		// 反审核操作
		baseDao.resAudit("Sale", "sa_id=" + sa_id, "sa_status", "sa_statuscode", "sa_auditdate", "sa_auditman");
		baseDao.resOperate("SaleDetail", "sd_said=" + sa_id, "sd_status", "sd_statuscode");
		saleClashService.cancelSaleClash(sa_id, "Sale");
		// 记录操作
		baseDao.logger.resAudit(caller, "sa_id", sa_id);
		deleteSaleClash(sa_id); //删除对应的预测冲销单并且还原冲销数量。
		if ("Sale!Abnormal".equals(caller)) {
			baseDao.logger.resAudit("Sale", "sa_id", sa_id);
		}
		// 执行审核后的其它逻辑
		handlerService.afterResAudit(caller, sa_id);
	}

	@Override
	public void submitSale(int sa_id, String caller) {
		// 只能提交状态为[在录入]的合同!
		Object status[] = baseDao.getFieldsDataByCondition("Sale", new String[] { "sa_statuscode", "sa_currency" },
				"sa_id=" + sa_id);
		StateAssert.submitOnlyEntering(status[0]);
		if (StringUtil.hasText(status[1])) {
			int count = baseDao.getCount(
					"select count(*) from currencysmonth,Sale where cm_crname=sa_currency and cm_yearmonth=to_char(sa_date,'yyyymm') and sa_id="
							+ sa_id);
			if (count == 0) {
				BaseUtil.showError("币别[" + status[1] + "]未设置月度汇率，不能提交！");
			}
		} else {
			BaseUtil.showError("销售单币别为空，不能提交！");
		}
		checkCustomer(sa_id);
		// 判断明细行物料是否有未审核的，有未审核不让提交
		String selectSQL = "select sd_detno,sd_prodcode,pr_status,pr_code from SaleDetail left join product on pr_code=sd_prodcode where sd_said="
				+ sa_id + " and NVL(pr_statuscode,' ')<>'AUDITED' ";
		SqlRowList rs = baseDao.queryForRowSet(selectSQL);
		int detno = 0;
		while (rs.next()) {
			detno = rs.getInt("sd_detno");
			if (rs.getObject("pr_code") != null) {
				BaseUtil.showError("序号" + String.valueOf(detno) + "物料未审核，不能提交！");
				return;
			} else {
				BaseUtil.showError("序号" + String.valueOf(detno) + "物料不存在，不能提交！");
				return;
			}
		}
		// 判断客户是否未审核或已禁用
		Object cust = baseDao.getFieldDataByCondition("Sale", "sa_custcode", "sa_id=" + sa_id);
		Object custatus = baseDao.getFieldDataByCondition("Customer", "cu_auditstatuscode", "cu_code='" + cust + "'");
		if (custatus != null && custatus.equals("AUDITED")) {
			String priceerror = saleDao.getPrice(sa_id);
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(sd_detno) from SaleDetail left join sale on sd_said=sa_id left join SaleKind on sa_kind=sk_name where nvl(sk_issaleprice,0)<>0 and nvl(sd_purcprice,0)=0 and sa_id=?",
					String.class, sa_id);
			if (dets != null) {
				BaseUtil.showError("该订单类型不允许定价为0！行号：" + dets);
			}
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(sd_detno) from SaleDetail left join sale on sd_said=sa_id left join SaleKind on sa_kind=sk_name where nvl(sk_allowzero,0)=0 and nvl(sd_isspecial,0)=0 and nvl(sd_price,0)=0 and sa_id=?",
					String.class, sa_id);
			if (dets != null) {
				BaseUtil.showError("不允许特价的情况下，该订单类型不允许单价为0！行号：" + dets);
			}
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select WM_CONCAT(sd_detno) from SaleDetail,sale,salekind where sd_said=sa_id and sa_kind=sk_name and NVL(sk_pricekind,' ')<>'NG' AND nvl(sd_isspecial,0)=0 and nvl(sd_price,0)< nvl(sd_purcprice,0) and sd_said=?",
					String.class, sa_id);
			if (dets != null) {
				BaseUtil.showError("不允许特价的情况下，销售单价小于订价，不允许提交!行号：" + dets);
			}
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(sd_detno) from saledetail left join sale on sd_said=sa_id where sa_id=? and (to_char(sd_delivery,'yyyymmdd')<to_char(sysdate,'yyyymmdd') or sd_delivery is null)",
					String.class, sa_id);
			if (dets != null) {
				BaseUtil.showError("交货日期不能为空并且不能小于系统当前日期！行号：" + dets);
			}
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(sd_detno) from sale,saledetail,salekind where sa_id=? and sd_said=sa_id and sk_name=sa_kind and nvl(sd_forecastcode,' ')<>' ' and nvl(sk_clashoption,' ')<>'订单冲销'",
					String.class, sa_id);
			if (dets != null) {
				BaseUtil.showError("当前订单的冲销类型不是【订单冲销】，无需选择预测单号！行号：" + dets);
			}
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(sd_detno) from saledetail,saleforecast,SaleForecastKind where sd_said=? and nvl(sd_forecastcode,' ')<>' ' and sd_forecastcode=saleforecast.sf_code and SaleForecast.sf_kind=SaleForecastKind.sf_name and nvl(SaleForecastKind.sf_clashoption,' ') not in ('SALE','订单冲销')",
					String.class, sa_id);
			if (dets != null) {
				BaseUtil.showError("明细行选择的预测单冲销类型不是【订单冲销】，不能提交！行号：" + dets);
			}
			if (baseDao.isDBSetting("CopCheck")) {
				dets = baseDao.getJdbcTemplate().queryForObject(
						"select wm_concat(sd_detno) from SaleDetail left join Sale on sd_said=sa_id where (sd_forecastcode, sd_forecastdetno) in (select saleforecast.sf_code, saleforecastdetail.sd_detno from saleforecastdetail left join saleforecast on saleforecast.sf_id=saleforecastdetail.sd_sfid where nvl(Sale.sa_cop,' ')<>nvl(saleforecast.sf_cop,' ')) and sd_said=?",
						String.class, sa_id);
				if (dets != null) {
					BaseUtil.showError("明细行预测单所属公司与销售单所属公司不一致，不允许进行当前操作！行号：" + dets);
				}
			}
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(sd_detno) from SaleDetail left join Product on sd_prodcode=pr_code left join Productlevel on pr_level=pl_levcode where sd_said=? and nvl(pl_nosale,0)<>0",
					String.class, sa_id);
			if (dets != null) {
				BaseUtil.showError("明细行物料的物料等级属性为不可销售，不允许进行当前操作！行号：" + dets);
			}
			checkPrecision(sa_id);
			// 符合冲销政策检测
			check_saleclash(caller, sa_id);
			defaultTax(caller, sa_id);
			allowZeroTax(caller, sa_id);
			getTotal(sa_id);
			sale_commit_minus(sa_id);
			// 判断订单明细是否填写数量
			boolean bool = baseDao.checkByCondition("SaleDetail",
					"sd_said=" + sa_id + " AND (sd_qty is null OR sd_qty=0)");
			if (baseDao.isDBSetting(caller, "autoProductCustomer") && !baseDao.isDBSetting(caller,"allowCustprodcode")) {
				// 提交前判断是否有相同的内部料号对应不同的客户料号
				String selectSQL1 = "select WMSYS.WM_CONCAT(sd_custprodcode),sd_prodcode from saledetail where sd_said='"
						+ sa_id
						+ "' and sd_custprodcode is not null group by sd_prodcode having count(DISTINCT SD_CUSTPRODCODE)>1";
				SqlRowList rs1 = baseDao.queryForRowSet(selectSQL1);
				StringBuffer err2 = new StringBuffer();
				while (rs1.next()) {
					err2.append("物料编号为:" + rs1.getString("sd_prodcode") + "的明细行，具有不同的客户物料编号:"
							+ rs1.getString("WMSYS.WM_CONCAT(sd_custprodcode)") + "<br>");
				}
				if (err2.length() > 0) {
					BaseUtil.showError(err2.toString());
				}
				 
				// 提交前判断是否有内部料号，客户料号相同对应不同的客户型号和客户物料名称
				String selectSQL2 = "SELECT DISTINCT P1.SD_PRODCODE, P1.SD_CUSTPRODCODE FROM SALEDETAIL P1,SALEDETAIL P2 WHERE P1.SD_PRODCODE=P2.SD_PRODCODE AND P1.SD_CUSTPRODCODE=P2.SD_CUSTPRODCODE AND (P1.SD_PRODCUSTCODE<>P2.SD_PRODCUSTCODE OR P1.SD_CUSTPRODDETAIL<>P2.SD_CUSTPRODDETAIL) AND P1.SD_ID<>P2.SD_ID and P1.sd_said='"
						+ sa_id + "'and P2.sd_said='" + sa_id + "'";
				SqlRowList rs2 = baseDao.queryForRowSet(selectSQL2);
				StringBuffer err1 = new StringBuffer();
				while (rs2.next()) {
					err1.append("物料编号为：" + rs2.getString("SD_PRODCODE") + "客户物料编号为：" + rs2.getString("sd_custprodcode"))
							.append("的明细行具有不同的客户物料名称，客户型号！<br>");
				}
				if (err1.length() > 0) {
					BaseUtil.showError(err1.toString());
				}
				String sql3 = "select DISTINCT cu_id,cu_code,pr_id,nvl(sd_custprodcode,' '),nvl(sd_custproddetail,' '),nvl(sd_prodcustcode,' '),pr_unit,sa_custcode,sa_custname,sd_prodcode from sale left join saledetail on sd_said=sa_id left join customer on sa_custcode=cu_code left join Product on sd_prodcode=pr_code where sa_id='"
						+ sa_id
						+ "' and nvl(sd_custprodcode,' ')<>' 'and exists (select 1 from ProductCustomer where pc_custcode=sa_custcode and pc_prodcode=sd_prodcode)";
				SqlRowList rs4 = baseDao.queryForRowSet(sql3);
				StringBuffer sb = new StringBuffer();
				while (rs4.next()) {
					String sd_prodcode = rs4.getString("sd_prodcode");
					String sql4 = "select nvl(pc_custprodcode,' '),nvl(pc_custproddetail,' '),nvl(pc_custprodspec,' ') from productcustomer where pc_custcode='"
							+ rs4.getString("cu_code") + "' and pc_prodcode='" + sd_prodcode + "'";
					SqlRowList rs3 = baseDao.queryForRowSet(sql4);
					while (rs3.next()) {
						if (!(rs3.getString("nvl(pc_custprodcode,'')").equals(rs4.getString("nvl(sd_custprodcode,'')")))
								|| !(rs3.getString("nvl(pc_custproddetail,'')")
										.equals(rs4.getString("nvl(sd_custproddetail,'')")))
								|| !(rs3.getString("nvl(pc_custprodspec,'')")
										.equals(rs4.getString("nvl(sd_prodcustcode,'')")))) {
							sb.append("物料编号:" + sd_prodcode + "对应的客户物料编号、客户物料名称、客户规格型号与客户物料对照表中的数据不一致，请确认!<br>");
						}
					}

				}
				if (sb.length() > 0) {
					BaseUtil.appendError(sb.toString());
				} 
				
			}
			if (bool) {
				// 执行提交前的其它逻辑
				handlerService.beforeSubmit(caller, sa_id);
				// 执行提交操作
				baseDao.submit("Sale", "sa_id=" + sa_id, "sa_status", "sa_statuscode");
				baseDao.submit("SaleDetail", "sd_said=" + sa_id, "sd_status", "sd_statuscode");
				// 记录操作
				baseDao.logger.submit(caller, "sa_id", sa_id);
				if ("Sale!Abnormal".equals(caller)) {
					baseDao.logger.submit("Sale", "sa_id", sa_id);
				}
				baseDao.execute(
						"update SaleDetail set sd_code=(select sa_code from sale where sa_id=sd_said) where sd_said=?",
						sa_id);
				// 修改主单据的总金额
				baseDao.execute(
						"update sale set sa_total=(select sum(sd_total) from saledetail where saledetail.sd_said = sale.sa_id) where sa_id="
								+ sa_id);
				baseDao.execute("update sale set sa_totalupper=L2U(sa_total) WHERE sa_id=" + sa_id);
				// 执行提交后的其它逻辑
				handlerService.afterSubmit(caller, sa_id);
				baseDao.execute(
						"update saledetail set sd_bomid=(select NVL(max(bo_id),0) from bom where bo_mothercode=sd_prodcode) where sd_said="
								+ sa_id);
				/**
				 * 反馈编号：2017020336
				 * @author wsy
				 * 根据客户+物料+币别抓取最近一笔已审核的销售订单中单价更新到上次销售单价字段
				 */
				SqlRowList rs1 = baseDao.queryForRowSet("select sa_custcode,sa_currency,sd_prodcode,sd_id from sale left join saledetail on sa_id=sd_said where sa_id="+sa_id+"");
				while(rs1.next()){
					baseDao.execute("update saledetail set sd_lastprice=nvl((select sd_price from (select sd_price from saledetail left join sale on sd_said=sa_id where sa_custcode='"+rs1.getString("sa_custcode")+"' and sa_currency='"+rs1.getString("sa_currency")+"' and sd_prodcode='"+rs1.getString("sd_prodcode")+"' and sa_status='已审核' and sa_id<>"+sa_id+" order by sa_auditdate desc,sd_id desc) where rownum=1),0) where sd_id="+rs1.getString("sd_id")+"");
				}
			} else {
				BaseUtil.showError("存在未填写数量的订单明细!");
			}
			if (priceerror != null && priceerror.length() > 0) {
				BaseUtil.appendError(priceerror.toString());
			}
		} else {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.sale.submit_cust")
					+ "<a href=\"javascript:openUrl('jsps/scm/sale/customerBase.jsp?formCondition=cu_codeIS" + cust
					+ "')\">" + cust + "</a>&nbsp;");
		}
	}

	@Override
	public void resSubmitSale(int sa_id, String caller) {
		// 只能对状态为[已提交]的合同进行反提交
		Object status = baseDao.getFieldDataByCondition("Sale", "sa_statuscode", "sa_id=" + sa_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, sa_id);
		// 执行反提交操作
		baseDao.resOperate("Sale", "sa_id=" + sa_id, "sa_status", "sa_statuscode");
		baseDao.resOperate("SaleDetail", "sd_said=" + sa_id, "sd_status", "sd_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "sa_id", sa_id);
		if ("Sale!Abnormal".equals(caller)) {
			baseDao.logger.resSubmit("Sale", "sa_id", sa_id);
		}
		handlerService.afterResSubmit(caller, sa_id);
	}

	@Override
	public String[] printSale(int sa_id, String reportName, String condition, String caller) {
		// 判断已审核才允许打印
		if (baseDao.isDBSetting(caller, "printNeedAudit")) {
			String status = baseDao.getFieldValue("Sale", "sa_statuscode", "sa_id=" + sa_id, String.class);
			StateAssert.printOnlyAudited(status);
		}
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, sa_id);
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 修改打印状态
		baseDao.updateByCondition("Sale",
				"sa_printstatuscode='PRINTED',sa_printstatus='" + BaseUtil.getLocalMessage("PRINTED") + "'",
				"sa_id=" + sa_id);
		// 记录操作
		baseDao.logger.print(caller, "sa_id", sa_id);
		if ("Sale!Abnormal".equals(caller)) {
			baseDao.logger.print("Sale", "sa_id", sa_id);
		}
		// 记录打印次数
		baseDao.updateByCondition("Sale", "sa_count=nvl(sa_count,0)+1", "sa_id=" + sa_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, sa_id);
		return keys;
	}

	public void getPrice(int sa_id) {
		saleDao.getPrice(sa_id);
	}

	@Override
	public int turnSendNotify(int sa_id, String caller) {
		int snid = 0;
		// 判断该销售单是否已经转入过转发货通知单
		Object code = baseDao.getFieldDataByCondition("SaleDetail", "sd_code", "sd_said=" + sa_id);
		code = baseDao.getFieldDataByCondition("sendNotifyDetail", "snd_code", "snd_sourcecode='" + code + "'");
		if (code != null && !code.equals("")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.sale.haveturn")
					+ "<a href=\"javascript:openUrl('jsps/scm/sale/sendNotify.jsp?formCondition=sn_codeIS" + code
					+ "&gridCondition=snd_codeIS" + code + "')\">" + code + "</a>&nbsp;");
		} else {
			// 转发货通知单
			snid = saleDao.turnSendNotify(sa_id);
			// 记录操作
			baseDao.logger.turn("msg.turnSendNotify", caller, "sa_id", sa_id);
		}
		return snid;
	}

	@Override
	public void submitTurnSale(int id) {
		handlerService.handler("Sale!TurnFormal", "commit", "before", new Object[] { id });
		String findSql = "select sd_detno,pr_code,pr_specvalue from saledetail,product where sd_prodcode=pr_code and sd_said='"
				+ id + "' and NVL(pr_specvalue,' ')='NOTSPECIFIC'";
		SqlRowList sl = baseDao.queryForRowSet(findSql);
		String msg = "";
		while (sl.next()) {
			msg += sl.getInt("sd_detno") + ",";
		}
		if (!msg.equals("")) {
			BaseUtil.showError("第 " + msg.substring(0, msg.lastIndexOf(",")) + " 行 物料仍是虚拟特征件，不能转正常订单!");
		}
		baseDao.execute("update sale set sa_commitstatus='已提交' where sa_id=" + id);
		handlerService.handler("Sale!TurnFormal", "commit", "after", new Object[] { id });
	}

	@Override
	public void resSubmitTurnSale(int id) {
		handlerService.handler("Sale!TurnFormal", "resCommit", "before", new Object[] { id });
		baseDao.execute("update sale set sa_commitstatus='未提交' where sa_id=" + id);
		handlerService.handler("Sale!TurnFormal", "resCommit", "after", new Object[] { id });
	}

	@Override
	public void turnNormalSale(int id) {
		baseDao.execute(
				"update sale set sa_source='',sa_relativecode='非正常转',sa_commitstatus='已转正常',sa_turndate=sysdate  where sa_id="
						+ id);
		baseDao.logger.turn("非正常订单转正常", "Sale", "sa_id", id);
		// 清除 批准流程
		String flowcaller = processService.getFlowCaller("Sale!TurnFormal");
		if (flowcaller != null) {
			processService.deletePInstance(id, "Sale!TurnFormal", "TurnSale");
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void splitSale(String formdata, String data) {
		Map<Object, Object> formmap = BaseUtil.parseFormStoreToMap(formdata);
		int sd_id = Integer.parseInt(formmap.get("sd_id").toString());
		int said = Integer.parseInt(formmap.get("sd_said").toString());
		Object pmcdate = baseDao.getFieldDataByCondition("saledetail", "sd_pmcdate", "sd_id=" + sd_id);
		boolean allow = baseDao.isDBSetting("Sale", "allowSplitWhenSync");
		if (!allow) {
			boolean isSync = baseDao.checkIf("sale", "sa_id=" + said + " and nvl(sa_sync,' ') like '已同步%'");
			if (isSync) {
				BaseUtil.showError("订单已抛转，无法拆分!");
			}
		}
		int basedetno = Integer.parseInt(formmap.get("sd_detno").toString());
		double baseqty = 0, splitqty = 0;
		List<String> sqls = new ArrayList<String>();
		Map<String, Object> currentMap = new HashMap<String, Object>();
		baseDao.execute("update saleDetail set sd_originalqty=sd_qty,sd_originaldetno=sd_detno where sd_id=" + sd_id
				+ " and sd_originaldetno is null");
		SqlRowList cur = baseDao.queryForRowSet("select * from saledetail where sd_id=" + sd_id);
		if (cur.next()) {
			currentMap = cur.getCurrentMap();
			baseqty = cur.getDouble("sd_qty");
		} else
			BaseUtil.showError("原始明细已不存在!无法拆分!");
		SqlRowList sccode = baseDao.queryForRowSet(
				"select sc_code from salechange left join salechangedetail on scd_scid=sc_id where nvl(sc_statuscode,' ')='COMMITED' "
						+ "and (scd_sacode,scd_sddetno)=(select sd_code,sd_detno from saledetail where sd_id=" + sd_id
						+ ")");
		if (sccode.next()) {
			BaseUtil.showError("原始明细存在已提交的销售变更单，无法拆分!销售变更单编号：" + sccode.getString("sc_code"));
		}
		SqlRowList sl = baseDao.queryForRowSet("select max(sd_detno) from saleDetail where sd_said=" + said);
		int newdetno = 0;
		if (sl.next()) {
			newdetno = sl.getInt(1) == -1 ? basedetno + 1 : sl.getInt(1);
		}
		List<Map<Object, Object>> gridmaps = BaseUtil.parseGridStoreToMaps(data);
		Map<Object, Object> map = new HashMap<Object, Object>();
		Object sdid = null;
		int sdqty = 0;
		int sddetno = 0;
		SqlRowList sl2 = null;
		// 判断总数量是否与拆分前一致
		for (int i = 0; i < gridmaps.size(); i++) {
			map = gridmaps.get(i);
			splitqty = NumberUtil.add(splitqty, Double.parseDouble(map.get("sd_qty").toString()));
		}
		if (splitqty != baseqty) {
			BaseUtil.showError("拆分后的总数跟当前序号总数不一致!");
		}
		Object newsd_qty = null;
		Object newsd_delivery = null;
		Object newsd_pmcdate = null;
		// 判断原始的序号 值不能
		for (int i = 0; i < gridmaps.size(); i++) {
			map = gridmaps.get(i);
			sdid = map.get("sd_id");
			sddetno = Integer.parseInt(map.get("sd_detno").toString());
			sdqty = Integer.parseInt(map.get("sd_qty").toString());
			//更新成最新选择的日期
			if (map.get("sd_pmcdate") != null) {
				pmcdate = map.get("sd_pmcdate");
			}
			if (sdid != null && Integer.parseInt(sdid.toString()) != 0) {
				newsd_qty = sdqty;
				newsd_delivery = map.get("sd_delivery");
				newsd_pmcdate = map.get("sd_pmcdate");				
				// 说明是原来已经拆分的订单 更新数量和交货日期 前台判定会有问题
				sl2 = baseDao.queryForRowSet("select sd_qty,sd_sendqty,sd_yqty from saledetail where sd_id=" + sdid);
				boolean b = baseDao.checkIf("SaleDetail",
						"sd_id=" + sd_id + " AND (sd_yqty>" + sdqty + " or sd_sendqty>" + sdqty + ")");
				if (b) {
					// 原始拆分后数量 不能小于
					BaseUtil.showError("原始拆分后的数量不能超过转发货或者转发货通知的数量!");
				}
				if (sl2.next()) {
					if (sdqty < sl2.getInt("sd_yqty") && sdqty < sl2.getInt("sd_sendqty")) {
						BaseUtil.showError("序号 :[" + sddetno + "] ,拆分后的数量小于已经转发货或者转发货通知的数量，不能拆分!");
					} else {
						sqls.add("update saledetail set sd_qty=" + sdqty + ",sd_delivery=to_date('"
								+ map.get("sd_delivery").toString() + "','yyyy-MM-dd'),sd_total=sd_price*" + sdqty
								+ " where sd_id=" + sdid);
						if (map.get("sd_pmcdate") != null) {
							sqls.add("update saledetail set sd_pmcdate=to_date('" + map.get("sd_pmcdate").toString()
									+ "','yyyy-MM-dd') where sd_id=" + sdid);
						}
					}
				} else
					BaseUtil.showError("序号 :[" + sddetno + "] ，明细数据已经不存在，不能拆分!");
			} else {

				boolean bool = true;
				while (bool) {
					newdetno++;
					bool = baseDao.checkIf("saledetail", "sd_said=" + said + " AND sd_detno=" + newdetno);
					if (!bool)
						break;
				}
				currentMap.remove("sd_pmcdate");
				currentMap.put("sd_pmcdate", pmcdate);
				currentMap.remove("sd_delivery");
				currentMap.put("sd_delivery", map.get("sd_delivery").toString());
				currentMap.remove("sd_detno");
				currentMap.put("sd_detno", newdetno);
				currentMap.remove("sd_id");
				currentMap.put("sd_id", baseDao.getSeqId("SALEDETAIL_SEQ"));
				currentMap.remove("sd_qty");
				currentMap.put("sd_qty", sdqty);
				currentMap.remove("sd_sendqty");
				currentMap.put("sd_sendqty", 0);
				currentMap.remove("sd_yqty");
				currentMap.put("sd_yqty", 0);
				currentMap.remove("sd_sendqty");
				currentMap.put("sd_sendqty", 0);
				currentMap.remove("sd_leaveassign");
				currentMap.put("sd_leaveassign", 0);
				currentMap.remove("sd_stockinqty");
				currentMap.put("sd_stockinqty", 0);
				currentMap.remove("sd_returnqty");
				currentMap.put("sd_returnqty", 0);
				currentMap.remove("sd_originaldetno");
				currentMap.put("sd_originaldetno", basedetno);
				currentMap.remove("sd_originalqty");
				currentMap.put("sd_originalqty", baseqty);
				currentMap.remove("sd_total");
				currentMap.remove("sd_tomakeqty");
				currentMap.put("sd_tomakeqty", 0);
				currentMap.put("sd_total", sdqty * Float.parseFloat(currentMap.get("sd_price").toString()));
				sqls.add(SqlUtil.getInsertSqlByMap(currentMap, "saledetail"));
			}
		}
		/**
		* 
		* */
		baseDao.execute(sqls);
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "订单拆分", "明细行:" + basedetno + "=>被拆分",
				"Sale|sa_id=" + said));
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "销售订单拆分", "明细行:" + basedetno + "=>被拆分,原数量："+baseqty+"、原PMC回复日期："+pmcdate.toString()
		+",新数量："+newsd_qty+"、新PMC回复日期："+newsd_pmcdate.toString(),
				"Sale|sa_id=" + said));		
		
		
	}

	@Override
	public void updatePMC(String data) {
		Map<Object, Object> formdata = BaseUtil.parseFormStoreToMap(data);
		int ISAllUpdate = Integer.parseInt(formdata.get("allupdate").toString());
		StringBuffer sb = new StringBuffer();
		Object pmc = formdata.get("pmcdate");
		Object pmcremark = formdata.get("pmcremark");
		if (pmc != null && !"".equals(pmc.toString()) && !"null".equals(pmc.toString())) {
			sb.append("sd_pmcdate=to_date('").append(pmc).append("','yyyy-MM-dd') ");
		}
		/**
		 * 单据编号：2017030071
		 * @author wsy
		 * 销售订单： 按钮“更改PMC回复交期” 按钮弹框中增加字段“PMC备注”+记录日志
		 */
		if(pmcremark != null && !"".equals(pmcremark.toString()) && !"null".equals(pmcremark.toString())){
			if(sb.length()>0){
				sb.append(",sd_pmcremark='"+pmcremark+"' ");
			}else{
				sb.append("sd_pmcremark='"+pmcremark+"' ");
			}
		}
		String updateSql = "update saledetail set " + sb.toString();
		Object sa_id = formdata.get("sd_said");
		StringBuffer log = new StringBuffer();//
		if (ISAllUpdate == 1) {
			updateSql = updateSql + " WHERE sd_said =" + sa_id;
			SqlRowList rs = baseDao.queryForRowSet(
					"select sd_detno,to_char(sd_pmcdate,'yyyy-mm-dd'),sd_pmcremark from saledetail where sd_said=? order by sd_detno",
					sa_id);
			while (rs.next()) {
				// 记录操作
				if(pmc != null && !"".equals(pmc.toString()) && !"null".equals(pmc.toString())&&!pmc.equals(rs.getString(2))){
					log.append("原交期:"+(rs.getString(2)==null?"无":rs.getString(2))+"修改为:"+pmc+";");
				}
				if(pmcremark != null && !"".equals(pmcremark.toString()) && !"null".equals(pmcremark.toString())&&!pmcremark.equals(rs.getString(3))){
					log.append(" 原备注:"+(rs.getString(3)==null?"无":rs.getString(3))+"修改为:"+pmcremark+"");
				}
				if(log.length()>0){
					baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "更新PMC交期",
							"行" + rs.getInt(1) + ":"+log+"", "Sale|sa_id=" + sa_id));
				}
				log.delete(0,log.length());
			}
		} else {
			updateSql = updateSql + " WHERE sd_id=" + formdata.get("sd_id");
			SqlRowList rs = baseDao.queryForRowSet(
					"select sd_detno,to_char(sd_pmcdate,'yyyy-mm-dd'),sd_said,sd_pmcremark from saledetail where sd_id=?",
					formdata.get("sd_id"));
			if (rs.next()) {
				// 记录操作
				if(pmc != null && !"".equals(pmc.toString()) && !"null".equals(pmc.toString())&&!pmc.equals(rs.getString(2))){
					log.append("原交期:"+(rs.getString(2)==null?"无":rs.getString(2))+"修改为:"+pmc+";");
				}
				if(pmcremark != null && !"".equals(pmcremark.toString()) && !"null".equals(pmcremark.toString())&&!pmcremark.equals(rs.getString(4))){
					log.append("原备注:"+(rs.getString(4)==null?"无":rs.getString(4))+"修改为:"+pmcremark+"");
				}
				if(log.length()>0){
					baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "更新PMC交期",
							"行" + rs.getInt(1) + ":"+log+"", "Sale|sa_id="+rs.getInt(3)));
				}
			}
		}
		baseDao.execute(updateSql);
	}

	@Override
	public JSONObject copySale(String caller, int id) {
		Employee employee = SystemSession.getUser();
		Map<String, Object> dif = new HashMap<String, Object>();
		// Copy Sale
		int nId = baseDao.getSeqId("SALE_SEQ");
		dif.put("sa_id", nId);
		dif.put("sa_date", "sysdate");
		dif.put("sa_recorddate", "sysdate");
		String code = baseDao.sGetMaxNumber(caller, 2);
		dif.put("sa_code", "'" + code + "'");
		dif.put("sa_sourceid", 0);
		dif.put("sa_recorderid", employee.getEm_id());
		dif.put("sa_recorder", "'" + employee.getEm_name() + "'");
		dif.put("sa_status", "'" + BaseUtil.getLocalMessage("ENTERING") + "'");
		dif.put("sa_statuscode", "'ENTERING'");
		dif.put("sa_auditman", "null");
		dif.put("sa_auditdate", "null");
		dif.put("sa_updateman", "null");
		dif.put("sa_updatedate", "null");
		dif.put("sa_turnstatus", "null");
		dif.put("sa_turnstatuscode", "null");
		dif.put("sa_sendstatus", "null");
		dif.put("sa_sendstatuscode", "null");
		dif.put("sa_printstatus", "null");
		dif.put("sa_count", 0);
		dif.put("sa_sync", "null");
		dif.put("sa_need1", "null");
		// 预收金额
		dif.put("sa_prepayamount", 0);
		baseDao.copyRecord("Sale", "Sale", "sa_id=" + id, dif);
		// Copy SaleDetail
		dif = new HashMap<String, Object>();
		dif.put("sd_id", "saledetail_seq.nextval");
		dif.put("sd_code", "'" + code + "'");
		dif.put("sd_said", nId);
		dif.put("sd_yqty", 0);
		dif.put("sd_sendqty", 0);
		dif.put("sd_leaveassign", 0);
		dif.put("sd_tomakeqty", 0);
		dif.put("sd_statuscode", "'ENTERING'");
		dif.put("sd_status", "'" + BaseUtil.getLocalMessage("ENTERING") + "'");
		dif.put("sd_pmcdate", "null");
		dif.put("sd_delivery", "null");
		baseDao.copyRecord("SaleDetail", "SaleDetail", "sd_said=" + id, dif);
		/**
		 * 复制日志记录
		 */
		String codeValue = baseDao.getFieldValue("SALE", "sa_code", "sa_id=" + id, String.class);
		baseDao.logger.copy(caller, codeValue, "sa_id", nId);
		JSONObject obj = new JSONObject();
		obj.put("id", nId);
		obj.put("code", code);
		return obj;
	}

	@Override
	public void updateDiscount(String caller, int id, String data, Boolean withOth) {
		List<String> sqls = new ArrayList<String>();
		List<Map<Object, Object>> datas = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer("行");
		for (Map<Object, Object> d : datas) {
			Object dis = d.get("sd_discount");
			Object code = d.get("sd_code");
			Object det = d.get("sd_detno");
			sqls.add(
					"update saledetail set sd_discount='" + dis + "' where sd_code='" + code + "' and sd_detno=" + det);
			if (withOth) {
				sqls.add("update sendnotifydetail set snd_discount='" + dis + "' where snd_ordercode='" + code
						+ "' and snd_orderdetno=" + det);
				sqls.add("update prodiodetail set pd_discount='" + dis + "' where pd_ordercode='" + code
						+ "' and pd_orderdetno=" + det);
				sqls.add("update invoicedetail set id_discount='" + dis + "' where id_ordercode='" + code
						+ "' and id_orderdetno=" + det);
			}
			sb.append(",").append(det);
		}
		baseDao.execute(sqls);
		baseDao.logger.others("更新比例", "msg.updateSuccess", caller, "sa_id", id);
	}

	@Override
	public String getCodeString(String caller, String table, int type, String conKind) {
		if (table == null || table.equals("")) {
			table = (String) baseDao.getFieldDataByCondition("form", "fo_table", "fo_caller='" + caller + "'");
		}
		String oldCode = baseDao.sGetMaxNumber(table.split(" ")[0], type);
		// 如果MakeKind中有设置对应的前缀码 用新前缀码替换旧前缀码
		Object newLCode = baseDao.getFieldDataByCondition("SALEKIND", "sk_excode", "sk_name='" + conKind + "'");
		if (newLCode != null) {
			if (!newLCode.toString().equals("")) {
				// 修改前缀
				oldCode = newLCode.toString() + oldCode;
			}
		}
		return oldCode;
	}

	@Override
	public void updateSalePayment(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String sql = SqlUtil.getUpdateSqlByFormStore(store, "SalePayment", "sp_id");
		baseDao.execute(sql);
		baseDao.execute("update salepayment set sp_recorder='" + SystemSession.getUser().getEm_name()
				+ "',sp_recorddate=sysdate where sp_id='" + store.get("sp_id") + "'");
		// 记录操作
		baseDao.logger.update("SalePayment", "sp_id", store.get("sp_id"));
	}

	@Override
	public void saleMrpOpen(int id, String caller) {
		SqlRowList rs = baseDao
				.queryForRowSet("select sd_said ,sd_detno from saledetail where sd_id=" + id + " and sd_mrpclosed<>0");
		if (rs.next()) {
			String sql = "update SaleDetail set sd_mrpclosed='0' where sd_id=" + id;
			baseDao.execute(sql);
			baseDao.logMessage(
					new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.openMrp"),
							BaseUtil.getLocalMessage("msg.openMrpSuccess") + "序号" + rs.getString("sd_detno"),
							caller + "|sa_id=" + rs.getString("sd_said")));
		}

	}

	@Override
	public void saleMrpClose(int id, String caller) {
		SqlRowList rs = baseDao.queryForRowSet(
				"select sd_said ,sd_detno from saledetail where sd_id=" + id + " and NVL(sd_mrpclosed,0)=0");
		if (rs.next()) {
			String sql = "update SaleDetail set sd_mrpclosed='-1' where sd_id=" + id;
			baseDao.execute(sql);
			baseDao.logMessage(
					new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.closeMrp"),
							BaseUtil.getLocalMessage("msg.closeMrpSuccess") + "序号" + rs.getString("sd_detno"),
							caller + "|sa_id=" + rs.getString("sd_said")));
		}
	}

	// 税率默认
	private void defaultTax(String caller, Object sa_id) {
		String defaultTax = baseDao.getDBSetting("Sale", "defaultTax");
		if (defaultTax != null) {
			// 税率强制等于币别表的默认税率
			if ("1".equals(defaultTax)) {
				baseDao.execute(
						"update SaleDetail set sd_taxrate=(select nvl(cr_taxrate,0) from currencys left join Sale on sa_currency=cr_name and cr_statuscode='CANUSE' where sd_said=sa_id)"
								+ " where sd_said=" + sa_id);
			}
			// 税率强制等于供应商资料的默认税率
			if ("2".equals(defaultTax)) {
				baseDao.execute(
						"update SaleDetail set sd_taxrate=(select nvl(cu_taxrate,0) from Customer left join Sale on sa_custcode=cu_code and cu_auditstatuscode='AUDITED' where sa_id=sd_said)"
								+ " where sd_said=" + sa_id);
			}
		}
	}

	// 本位币允许税率为0
	private void allowZeroTax(String caller, Object sa_id) {
		String currency = baseDao.getDBSetting("defaultCurrency");
		if (!baseDao.isDBSetting(caller, "allowZeroTax")) {
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select WM_CONCAT(sd_detno) from SaleDetail left join Sale on sd_said=sa_id where nvl(sd_taxrate,0)=0 and sa_currency='"
							+ currency + "' and sd_said=?",
					String.class, sa_id);
			if (dets != null) {
				BaseUtil.showError("本位币税率为0，不允许提交、审核！行号：" + dets);
			}
		}
		//增加销售类型 iszerorate字段来限制是否允许0税率 maz 2018070190
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(sd_detno) from sale left join saledetail on sa_id=sd_said left join salekind on sa_kind=sk_name where sa_id=? and sa_currency<>'"+currency+"' and sk_iszerorate=0 and nvl(sd_taxrate,0)=0",
				String.class, sa_id);
		if (dets != null) {
			BaseUtil.showError("销售类型不允许0税率，明细行存在0税率，不允许提交、审核！行号：" + dets);
		}
	}

	@Override
	public void getFittingData(String caller, String pr_code, String qty, String sa_id, String detno) {
		SqlRowList rs = null;
		rs = baseDao.queryForRowSet("select * from sale left join saledetail on sa_id=sd_said where sa_id='" + sa_id
				+ "' and sd_makeid='" + detno + "' ");
		if (rs.next()) {
			BaseUtil.showError("序号：" + detno + "已经载入了配件，不允许重复载入！");
		}

		rs = baseDao.queryForRowSet("select sd_delivery from sale left join saledetail on sa_id=sd_said where sa_id='"
				+ sa_id + "' and sd_prodcode='" + pr_code + "'");
		String time = null;

		if (rs.next()) {
			time = rs.getGeneralTimestamp("sd_delivery");
		}
		int detno2 = 0;
		rs = baseDao.queryForRowSet(
				"select max(sd_detno) det from sale left join saledetail on sa_id=sd_said where sa_id='" + sa_id + "'");
		if (rs.next()) {
			detno2 = rs.getInt("det");
		}
		Object sd_code = baseDao.getFieldDataByCondition("sale", "sa_code", "sa_id=" + sa_id);
		rs = baseDao.queryForRowSet(
				"select fbd_prodcode,fbd_qty from fittingbom left join fittingbomdetail on fb_id=fbd_fbid where fb_prodcode='"
						+ pr_code + "'and fb_statuscode='AUDITED'");
		if (rs.hasNext()) {
			while (rs.next()) {
				int id = baseDao.getSeqId("SALEDETAIL_SEQ");
				String prodCode = rs.getString("fbd_prodcode");
				double sdqty = NumberUtil.formatDouble(rs.getFloat("fbd_qty") * Integer.valueOf(qty), 4);
				String sql = "insert into saledetail (SD_ID,SD_SAID,SD_PRODCODE,SD_DELIVERY,SD_MAKEID,SD_QTY,SD_STATUS,SD_STATUSCODE,SD_DETNO ,SD_CODE) values("
						+ id + "," + sa_id + ",'" + prodCode + "'," + "to_date('" + time + "','yyyy-mm-dd hh24:mi:ss')"
						+ ",'" + detno + "'," + sdqty + ",'在录入','ENTERING'," + ++detno2 + ",'" + sd_code + "')";
				baseDao.execute(sql);
			}
		} else {
			BaseUtil.showError("序号：" + detno + "不存在配件！");
		}
	}

	// 是否符合冲销政策检测
	private void check_saleclash(String caller, Object sa_id) {
		Object dets;
		boolean uncheckbool = baseDao.isDBSetting("Sale", "ifNotClashCheck");
		if (uncheckbool) {// 不需要逻辑限制的，只验证单号填写是否正确，超预测的明细行要填写是否超预测为是
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(SD.sd_detno) from saledetail SD left join sale on sd_said=sa_id left join saleforecast on sf_code=SD.sd_forecastcode left join saleforecastdetail FD on sf_id=sd_sfid and FD.sd_detno=SD.sd_forecastdetno where sa_id=? and NVL(SD.sd_forecastcode,' ')<>' ' and FD.sd_id is null ",
					String.class, sa_id);
			if (dets != null) {
				BaseUtil.showError("预测单号填写错误，行号：" + dets);
			}
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(SD.sd_detno) from saledetail SD left join sale on sd_said=sa_id left join saleforecast on sf_code=SD.sd_forecastcode left join saleforecastdetail FD on sf_id=sd_sfid and FD.sd_detno=SD.sd_forecastdetno where sa_id=? and NVL(SD.sd_forecastcode,' ')<>' ' and NVL(FD.sd_qty,0)<SD.sd_qty  and NVL(sd_noforecast,0)=0 ",
					String.class, sa_id);
			if (dets != null) {
				BaseUtil.showError("订单数超出预测单剩余数！行号：" + dets);
			}
			return;
		}
		// 以下对需要跟订单类型的冲销原则批量的进行判定
		Object[] obj = baseDao.getFieldsDataByCondition("sale,SaleKind",
				"sk_clashfor,sk_clashoption,sk_clashkind,sa_custcode,sa_sellercode",
				"(sa_kind=sk_name or sa_kind=sk_code) and sa_id=" + sa_id);
		if (obj != null) {
			if (obj[0] == null) {
				BaseUtil.showError("订单类型中的冲销匹配规则没有填写，不允许进行当前操作！");
			}
			if (obj[1] == null) {
				BaseUtil.showError("订单类型中的冲销触发类型没有填写，不允许进行当前操作！");
			}
			if (obj[2] == null) {
				BaseUtil.showError("订单类型中的附加冲销条件没有填写，不允许进行当前操作！");
			}

			if (obj[0].equals("单号冲销")) {
				dets = baseDao.getJdbcTemplate().queryForObject(
						"select wmsys.wm_concat(sd_detno) from saledetail where sd_said=? and NVL(sd_noforecast,0)=0 and (nvl(sd_forecastcode,' ')=' ' or nvl(sd_forecastdetno,0)=0) ",
						String.class, sa_id);
				if (dets != null) {
					BaseUtil.showError("明细行预测单号跟预测行号有未填写的，不允许进行当前操作！行号：" + dets);
				}
				dets = baseDao.getJdbcTemplate().queryForObject(
						"select wmsys.wm_concat(SD.sd_detno) from saledetail SD left join sale on sd_said=sa_id left join saleforecast on sf_code=SD.sd_forecastcode left join saleforecastdetail FD on sf_id=sd_sfid and FD.sd_detno=SD.sd_forecastdetno where sa_id=? and (NVL(SD.sd_noforecast,0)=0 or SD.sd_forecastcode<>' ')and FD.sd_id is null ",
						String.class, sa_id);
				if (dets != null) {
					BaseUtil.showError("预测单号填写错误，行号：" + dets);
				}
				dets = baseDao.getJdbcTemplate().queryForObject(
						"select wmsys.wm_concat(SD.sd_detno) from saledetail SD left join sale on sd_said=sa_id left join saleforecast on sf_code=SD.sd_forecastcode left join saleforecastdetail FD on sf_id=sd_sfid and FD.sd_detno=SD.sd_forecastdetno where sa_id=? and NVL(sd_noforecast,0)=0 and NVL(FD.sd_qty,0)<SD.sd_qty ",
						String.class, sa_id);
				if (dets != null) {
					BaseUtil.showError("订单数超出预测单剩余数！行号：" + dets);
				}
				if (obj[1].equals("订单冲销")) {
					dets = baseDao.getJdbcTemplate().queryForObject(
							"select wmsys.wm_concat(sd_detno) from saledetail SD left join saleforecast F on F.sf_code=SD.sd_forecastcode left join saleforecastkind K on F.sf_kind=K.sf_name  where SD.sd_said=? and NVL(SD.sd_noforecast,0)=0 and NVL(K.sf_clashoption,' ') not in ('订单冲销','SALE') ",
							String.class, sa_id);
					if (dets != null) {
						BaseUtil.showError("填写的预测单号不是订单冲销类型的预测！行号：" + dets);
					}
				}
			} else if (obj[0].equals("料号冲销")) {
				if (obj[1].equals("订单冲销") || obj[1].equals("SALE")) {
					String condi = "";
					if (obj[2].equals("客户匹配")) {
						condi = " and sf_custcode='" + obj[3] + "'";
					} else if(obj[2].equals("业务员")){
						condi = " and saleforecast.sf_sellercode='"+obj[4]+"' ";
					}
					dets = baseDao.getJdbcTemplate().queryForObject(
							"select  wmsys.wm_concat('物料:'||A.sd_prodcode||'剩余预测数:'||B.qty) from (select sd_prodcode,sum(sd_qty) qty from saledetail where sd_said=? and NVL(sd_noforecast,0)=0 group by sd_prodcode)A left join (select sd_prodcode,NVL(sum(sd_qty),0)qty from saleforecast,saleforecastdetail,saleforecastkind where saleforecast.sf_id=sd_sfid and saleforecast.sf_kind=saleforecastkind.sf_name and saleforecastkind.sf_clashoption in ('订单冲销','SALE') and saleforecast.sf_statuscode='AUDITED' and sd_qty>0 and trunc(sd_enddate)>=trunc(sysdate) and NVL(sd_statuscode,' ')<>'FINISH' "
									+ condi
									+ " group by sd_prodcode)B on A.sd_prodcode=B.sd_prodcode where A.qty>NVL(B.qty,0)",
							String.class, sa_id);
					if (dets != null) {
						BaseUtil.showError("订单数超出预测单剩余数！" + dets);
					}
				}
			}
		}
	}

	@Override
	public void endSale(String data, String caller) {
		singleFormItemsService.vastCloseSaleDetail(SystemSession.getLang(), SystemSession.getUser(), caller, data);
	}

	@Override
	public void calBOMCost(int sa_id, String caller) {
		StringBuffer sb = new StringBuffer();
		SqlRowList rs = baseDao.queryForRowSet(
				"select * from saledetail left join BOM on bo_mothercode=sd_prodcode where sd_said=? and nvl(sd_prodcode,' ')<>' '",
				sa_id);
		while (rs.next()) {
			if (rs.getGeneralInt("bo_id") == 0) {
				sb.append("物料编号[").append(rs.getObject("sd_prodcode")).append("]没有建立相关BOM资料！").append("<hr>");
			} else if (!"AUDITED".equals(rs.getGeneralString("bo_statuscode"))) {
				sb.append("物料编号[").append(rs.getObject("sd_prodcode")).append("]BOM资料未审核！").append("<hr>");
			} else {
				int bo_id = rs.getGeneralInt("bo_id");
				String pr_code = rs.getGeneralString("sd_prodcode");
				baseDao.procedure("SP_COSTCOUNT", new Object[] { bo_id, pr_code, "最新采购单价" });
				baseDao.execute(
						"merge into BomStruct using (select cr_rate,cr_name from currencys where nvl(cr_status,' ')<>'已禁用') src on( bs_currency=cr_name) when matched then update set bs_l=(CASE WHEN bs_currency='RMB' then bs_purcprice/(1+bs_rate/100) ELSE bs_purcprice*nvl(cr_rate,0) END) where bs_topbomid="
								+ bo_id + " and (nvl(bs_sonbomid,0)=0 or nvl(bs_sonbomid,0) in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 ))");
				baseDao.execute("update BomStruct set bs_m=bs_l*bs_baseqty where bs_topbomid=" + bo_id
						+ " and (nvl(bs_sonbomid,0)=0 or nvl(bs_sonbomid,0) in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 ))");

				String SQLStr = "select bs_idcode,bs_soncode from BomStruct where bs_topbomid=?"
						+ " and bs_topmothercode=? and nvl(bs_sonbomid,0)>0 and nvl(bs_sonbomid,0) not in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 ) ORDER BY bs_level";
				SqlRowList rss = baseDao.queryForRowSet(SQLStr, bo_id, pr_code);
				while (rss.next()) {// bs_osprice 在存储过程中计算出来的值是含税的委外单价
					SQLStr = "SELECT sum(nvl(bs_m,0)) from BomStruct WHERE bs_topbomid=" + bo_id
							+ " and bs_topmothercode='" + pr_code + "' and  bs_mothercode='"
							+ rss.getString("bs_soncode") + "' ";
					SqlRowList rsthis = baseDao.queryForRowSet(SQLStr);
					if (rsthis.next()) {
						SQLStr = "update bomstruct set bs_m=round((" + rsthis.getString(1)
								+ "+nvl(bs_osprice,0)/(1+nvl(bs_rate,0)/100)),8)*bs_baseqty " + " where bs_topbomid="
								+ bo_id + " and bs_idcode=" + rss.getString("bs_idcode");
						baseDao.execute(SQLStr);
					}
				}
				Double sarate = baseDao.getJdbcTemplate()
						.queryForObject("select nvl(sa_rate,1) from sale where sa_id=?", Double.class, sa_id);

				baseDao.execute("update saledetail set sd_bomprice=round((select bs_m/" + sarate
						+ " from bomstruct where bs_topbomid=" + bo_id + " and bs_soncode='" + pr_code
						+ "'),6) where sd_id=" + rs.getGeneralInt("sd_id"));
			}
		}
		if (sb.length() > 0) {
			BaseUtil.appendError(sb.toString());
		}
	}

	/**
	 * 销售订单：是否负利润记算
	 */
	public void sale_commit_minus(Object sa_id) {
		if (baseDao.isDBSetting("Sale", "countMinus")) {
			List<Object[]> objs = baseDao.getFieldsDatasByCondition("saledetail left join sale on sd_said=sa_id",
					new String[] { "sd_id", "sd_prodcode", "sa_rate" }, " sa_id=" + sa_id);
			Double sa_rate = 0.0;
			if (objs.size() > 0 && objs.get(0)[2] != null)
				sa_rate = Double.parseDouble(objs.get(0)[2].toString());
			for (Object[] os : objs) {
				baseDao.updateByCondition("SaleDetail",
						"sd_flash=(select ba_price from (select ba_price from batch where ba_prodcode='" + os[1]
								+ "' and ba_remain>0 order by ba_date desc) where rownum=1)",
						"sd_id=" + os[0]);
			}
			if (sa_rate != 0) {
				baseDao.execute("update saledetail set sd_bodycost=round((sd_qty*sd_price*" + sa_rate
						+ "/(1+nvl(sd_taxrate,0)/100)-sd_qty*sd_flash)/(sd_qty*sd_price*" + sa_rate
						+ "/(1+nvl(sd_taxrate,0)/100))*100,2) where nvl(sd_price,0)>0 and nvl(sd_price,0)*nvl(sd_qty,0)<>0 and sd_said="
						+ sa_id);
			}
			int count = baseDao
					.getCount("select count(*) from saledetail where sd_said=" + sa_id + " and nvl(sd_bodycost,0)<0");
			String sa_minus = null;
			if (count > 0) {
				sa_minus = "是";
			} else {
				sa_minus = "否";
			}
			baseDao.execute("update sale set sa_minus='" + sa_minus + "' where sa_id=" + sa_id);
			baseDao.execute("update saledetail set sd_minus='是' where nvl(sd_bodycost,0)<0 and sd_said=" + sa_id);
			baseDao.execute("update saledetail set sd_minus='否' where nvl(sd_bodycost,0)>=0 and sd_said=" + sa_id);
		}
	}

	@Override
	public void confirmAgree(int sa_id, String caller) {
		// 判断销售sa_ordertype订单类型是否为B2C,sa_kind,是否已经确认过sa_confirmstatus
		SqlRowList rs = baseDao.queryForRowSet(
				"select sa_ordertype,sa_kind,sa_confirmstatus,sa_pocode from sale where sa_id=?", sa_id);
		if (rs.next()) {
			if (!"B2C".equals(rs.getObject("sa_ordertype"))) {
				BaseUtil.showError("该销售订单不是通过优软商城平台生成不需要确认");
			}
			if ("确认".equals(rs.getObject("sa_confirmstatus"))) {
				BaseUtil.showError("该销售订单已经确认接收，不需要重复确认");
			}
		}
		Object[] obs = baseDao.getFieldsDataByCondition("salekind", new String[] { "sk_statuscode", "sk_name" },
				"nvl(sk_ifb2c,0)<>0");
		if (obs == null) {
			BaseUtil.showError("请先维护属于优软商城订单的销售类型");
		} else if (obs[0] != null && !obs[0].toString().equals("AUDITED")) {
			BaseUtil.showError("请先审核属于优软商城订单的销售类型[" + obs[1] + "]");
		}
		// 更新销售类型
		if (!obs[1].toString().equals(rs.getObject("sa_kind"))) {
			baseDao.execute("update sale set sa_kind='" + obs[1].toString() + "' where sa_id=" + sa_id);
		}
		// 抓取客户，应收客户，收货客户
		String cust = baseDao.getDBSetting("B2CSetting", "B2CCusomter");
		if (!StringUtil.hasText(cust)) {
			BaseUtil.showError("请先维护优软商场中的优软商场客户编号参数配置");
		}
		// 判断客户编号是否存在，状态为已审核
		Object ob = baseDao.getFieldDataByCondition("customer", "cu_auditstatuscode", "cu_code='" + cust + "'");
		if (ob == null) {
			BaseUtil.showError("参数配置的优软商城客户编号[" + cust + "]不存在");
		} else if (ob != null && !ob.toString().equals("AUDITED")) {
			BaseUtil.showError("参数配置的优软商城客户编号[" + cust + "]未审核");
		}
		// 更新客户
		baseDao.execute(
				"update sale set (sa_custid,sa_custcode,sa_custname,sa_apcustcode,sa_apcustname,sa_sellerid,sa_sellercode,"
						+ " sa_seller,sa_paymentsid,sa_payments,sa_shcustcode,sa_shcustname,sa_paymentscode,sa_transport)="
						+ " (select cu_id,cu_code,cu_name,cu_arcode,cu_arname,cu_sellerid,cu_sellercode,"
						+ " cu_sellername,cu_paymentid,cu_payments,cu_shcustcode,cu_shcustname,cu_paymentscode,cu_shipment from customer where cu_code=?)"
						+ " where sa_id=?",
				cust, sa_id);
		// 确认接收
		saleOrderService.agree(sa_id, true);
	}

	/**
	 * 转商城发货，转出货单，批次号为上架的批次号
	 * 
	 * @param caller
	 * @param id
	 */
	@Override
	public String turnB2CSaleOut(int sa_id, String caller) {
		// 判断销售单类型，销售sa_ordertype订单类型是否为B2C,sa_kind,是否已经确认过sa_confirmstatus
		SqlRowList rs = baseDao.queryForRowSet(
				"select sa_ordertype,sa_kind,sa_confirmstatus,sa_pocode,sa_statuscode,sa_code from sale where sa_id=?",
				sa_id);
		if (rs.next()) {
			if (!"B2C".equals(rs.getObject("sa_ordertype"))) {
				BaseUtil.showError("只有通过优软商城生成的正式销售订单才可以转商城发货");
			}
			if (!"AUDITED".equals(rs.getString("sa_statuscode"))) {
				BaseUtil.showError("请先审核该销售订单再进行转商城发货");
			}
			if (!"确认".equals(rs.getObject("sa_confirmstatus"))) {
				BaseUtil.showError("请先确认该销售订单已经接收再转商城发货");
			}
			// 销售类型属于优软商城订单才允许转商城发货
			Object ob = baseDao.getFieldDataByCondition("sale left join salekind on sk_name=sa_kind", "sk_name",
					"sa_id=" + sa_id + " and nvl(sk_ifb2c,0)<>0");
			if (ob == null) {
				BaseUtil.showError("销售类型不是优软商城订单");
			}
			// 转整张单转发货
			String log = null;
			String codes = baseDao.getJdbcTemplate().queryForObject(
					"select WM_CONCAT(sa_code) from (select distinct sa_code from sale where sa_id =?"
							+ " and sa_code in (select distinct scd_sacode from SaleChangeDetail left join SaleChange on sc_id=scd_scid where sc_statuscode<>'AUDITED'))",
					String.class, sa_id);
			if (codes != null) {
				BaseUtil.showError("当有在待审批的销售变更单，不能进行转出操作!销售单号：" + codes);
			}
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat('订单行号：'||sd_detno) from SaleDetail where nvl(sd_statuscode, ' ')<>'AUDITED' and sd_said=?",
					String.class, sa_id);
			if (dets != null) {
				BaseUtil.showError("明细行状态不等于已审核，不能进行转出操作!" + dets);
			}
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select WM_CONCAT(distinct snd_ordercode) from SendNotifyDetail where nvl(snd_ordercode,' ')<>' ' and snd_ordercode in (select sa_code from sale where sa_id=?"
							+ ")",
					String.class, sa_id);
			if (dets != null) {
				BaseUtil.showError("销售订单已转过通知单,不能进行转出操作!销售单号：" + dets);
			}
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select distinct wm_concat('客户：'||sa_custcode) from sale left join SaleDetail on sd_said=sa_id where nvl(sa_custcode, ' ')<>' ' and sa_id =?"
							+ " and sa_custcode in (select cu_code from customer where cu_status='挂起')",
					String.class, sa_id);
			if (dets != null) {
				BaseUtil.showError("订单客户已挂起，不能进行转出操作!" + dets);
			}
			// 已转不允许重复转判断prodiodetail 是否有已转
			ob = baseDao.getFieldDataByCondition("prodinout left join prodiodetail on pd_piid=pi_id",
					"distinct pi_inoutno ", "pd_ordercode='" + rs.getString("sa_code") + "' and pi_class='出货单'");
			if (ob != null) {
				BaseUtil.showError("该销售订单已转出货单[" + ob + "],请勿重复转");
			}
			Key key = transferRepository.transfer("Sale!ToProdIO!Deal", sa_id);
			if (key != null) {
				int pi_id = key.getId();
				String inoutno = key.getCode();
				// 转入明细
				/*
				 * baseDao.execute(
				 * "insert into prodiodetail (PD_ID,pd_piid,pd_inoutno,pd_orderdetno,pd_ordercode,pd_prodcode,pd_sendprice,pd_outqty,pd_beipinoutqty,pd_pocode,pd_piclass,pd_status,"
				 * +
				 * " pd_auditstatus,pd_taxrate,pd_prodid,pd_sdid,pd_remark,pd_custprodcode,pd_custprodspec,pd_discount,pd_inqty,pd_pdno,pd_batchcode,pd_batchid,pd_whcode,pd_barcode) select PRODIODETAIL_SEQ.nextval,"
				 * +pi_id+",'"+inoutno+"',sd_detno,sa_code," +
				 * " sd_prodcode,sd_price,case when ba_remain>sd_qty then sd_qty else ba_remain end remain ,0,sa_pocode,'出货单',0,'ENTERING',sd_taxrate,pr_id,sd_id,sd_remark,sd_custprodcode,sd_prodcustcode,sd_discount,0,rownum,ba_code,ba_id,ba_whcode,gd_barcode"
				 * +
				 * " from sale left join saledetail on sa_id=sd_said left join product on sd_prodcode=pr_code left join goodsdetail on gd_prodcode=sd_prodcode and gd_b2bbatchcode=sd_b2cbarcode left join batch on ba_barcode=gd_barcode and ba_prodcode=gd_prodcode left join warehouse on wh_code=ba_whcode "
				 * + " where sa_id="+sa_id+
				 * " and nvl(wh_ifb2c,0)<>0 and nvl(wh_status,' ')='已审核' and ba_remain>0"
				 * );
				 */
				baseDao.execute(
						"insert into prodiodetail (PD_ID,pd_piid,pd_inoutno,pd_orderdetno,pd_ordercode,pd_prodcode,pd_sendprice,pd_outqty,pd_beipinoutqty,pd_pocode,pd_piclass,pd_status,"
								+ " pd_auditstatus,pd_taxrate,pd_prodid,pd_sdid,pd_remark,pd_custprodcode,pd_custprodspec,pd_discount,pd_inqty,pd_pdno,pd_whcode,pd_barcode) select PRODIODETAIL_SEQ.nextval,"
								+ pi_id + ",'" + inoutno + "',sd_detno,sa_code,"
								+ " sd_prodcode,sd_price,sd_qty,0,sa_pocode,'出货单',0,'ENTERING',sd_taxrate,pr_id,sd_id,sd_remark,sd_custprodcode,sd_prodcustcode,sd_discount,0,sd_detno,gu_whcode,gd_barcode"
								+ " from sale left join saledetail on sa_id=sd_said left join product on sd_prodcode=pr_code left join goodsdetail on gd_prodcode=sd_prodcode and gd_b2bbatchcode=sd_b2cbarcode left join goodsup on gu_id=gd_guid"
								+ " where sa_id=" + sa_id);
				baseDao.execute(
						"update ProdIODetail set pd_whname=(select wh_description from warehouse where pd_whcode=wh_code ) where pd_piid=? ",
						pi_id);
				baseDao.execute(
						"update ProdInOut set (pi_purposename, pi_expresscode)=(select CA_PERSON, CA_PHONE from CustomerAddress where pi_cardid=ca_cuid and ca_remark='是') where pi_id=?",
						pi_id);
				// 地址
				baseDao.execute(
						"update ProdInOut set pi_address=(select cu_add1 from customer where pi_cardcode=cu_code) where pi_id=? and nvl(pi_address,' ')=' '",
						pi_id);
				baseDao.execute(
						"update ProdIODetail set pd_netprice=ROUND(pd_netprice/(1 + pd_taxrate/ 100),6), pd_taxtotal=round(pd_sendprice*pd_outqty,2), pd_ordertotal=round(pd_outqty*pd_sendprice,2) where pd_piid=?",
						pi_id);
				baseDao.execute("update ProdIODetail set pd_nettotal=round(pd_outqty*pd_netprice,2) where pd_piid=?",
						pi_id);
				// 自动抓取批次
				String res = baseDao.callProcedure("SP_SPLITPRODOUT",
						new Object[] { "出货单", inoutno, String.valueOf(SystemSession.getUser().getEm_name()) });
				log = "转入成功,出货单号:"
						+ "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + pi_id
						+ "&gridCondition=pd_piidIS" + pi_id + "&whoami=ProdInOut!Sale')\">" + key.getCode()
						+ "</a>&nbsp;";
				// 修改销售单出库状态
				baseDao.updateByCondition("saledetail", "sd_yqty=sd_qty", "sd_said=" + sa_id);
				baseDao.updateByCondition("Sale",
						"sa_sendstatuscode='TURNOUT',sa_sendstatus='" + BaseUtil.getLocalMessage("TURNOUT") + "'",
						"sa_id=" + sa_id);
				handlerService.handler(caller, "turnProdIO", "after", new Object[] { sa_id });
				return log;
			}
		}
		return null;
	}

	@Override
	public void UpdateLD(int sd_id, String LDCode, String caller) {
		String sd_said = "";
		String sd_prodcode = "";
		Map<Object, Object> formdata = BaseUtil.parseFormStoreToMap(LDCode);
		String ldcode = formdata.get("LDCode").toString();
		SqlRowList rs = baseDao.queryForRowSet("select sd_said,sd_prodcode from saledetail where sd_id=" + sd_id);
		if (rs.next()) {
			sd_said = rs.getString("sd_said");
			sd_prodcode = rs.getString("sd_prodcode");
			baseDao.updateByCondition("saledetail", "sd_ldliaoma_user='" + ldcode + "'",
					"sd_said=" + sd_said + " and sd_prodcode='" + sd_prodcode + "'");
			baseDao.execute(baseDao.logger
					.getMessageLog("修改销售订单LD料码，物料编号" + sd_prodcode, "成功", caller, "sd_said", sd_said).getSql());
		}
	}

	@Override
	public String saleturnPurc(int id, String caller) {
		Object sa_code=baseDao.getFieldDataByCondition("Sale","sa_code","sa_id="+id);
		Object[] ob = baseDao.getFieldsDataByCondition("Purchase",new String[] { "pu_id", "pu_code"},
				"nvl(pu_sourcecode,' ')='" + sa_code+"'");
		if(ob!=null){
			BaseUtil.showError("该销售订单已转入过采购单,采购单号:<a href=\"javascript:openUrl('jsps/scm/purchase/purchase.jsp?formCondition=pu_idIS" + ob[0]
					+ "&gridCondition=pd_puidIS" + ob[0] + "')\">" + ob[1] + "</a>&nbsp;");
		}
		Key key = transferRepository.transfer("Sale!ToPurc", id);
		if (key != null) {
			int pu_id = key.getId();
			String pu_code = key.getCode();
			// 转入明细
			transferRepository.transferDetail("Sale!ToPurc", id, key);
			baseDao.updateByCondition("sale", "sa_turnpurchase='已转采购'", "sa_id=" + id);
			handlerService.handler(caller, "turn", "after", new Object[] {id});
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "转采购", "转采购成功",
					"Sale|sa_id=" + id));
			String log = "转入成功,采购单号:"
					+ "<a href=\"javascript:openUrl('jsps/scm/purchase/purchase.jsp?formCondition=pu_idIS" + pu_id
					+ "&gridCondition=pd_puidIS" + pu_id + "')\">" + pu_code+ "</a>&nbsp;";
			return log;
		}
		return null;
	}
    //信扬国际
	public Map<String, Object> chargerCalc(String data, String pickdate,int sa_deposit) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String sdidstr ="",sdqtystr="";
		for (Map<Object, Object> map : maps) {
			sdidstr+= "," + map.get("sd_id").toString();
			sdqtystr+= "," + map.get("sd_tqty").toString();
		}
		List<String> list = baseDao.callProcedureWithOut("Sp_chargerCalc",new Object[] {sdidstr,sdqtystr, pickdate,sa_deposit}, new Integer[]{1,2,3,4}, new Integer[]{5,6,7,8,9,10,11,12,13});
		modelMap.put("fee", list.get(0));
		modelMap.put("amount", list.get(1));
		modelMap.put("total", list.get(2));
		modelMap.put("degree", list.get(3));
		modelMap.put("xyjsbzzd", list.get(4));
		modelMap.put("chargerate", list.get(5));
		modelMap.put("thisamount", list.get(6));
		modelMap.put("yshkye", list.get(7));
		modelMap.put("N", list.get(8));
		return modelMap;
	}
	//驳回订单
	@Override
	public void updateSaleStatus(String caller,String value,int id){
		baseDao.logger.others("驳回操作", "驳回成功", caller, "sa_id", id);
		baseDao.updateByCondition("Sale", "sa_backreason='"+value+"',sa_backstatus='已驳回待上传'", "sa_id="+id);
	}
	//发起复审操作
	@Override
	public void recheck(String caller,int id){
		String flowCaller = "Sale!Recheck";
		Object recheckStatusCode = baseDao.getFieldDataByCondition("sale", "sa_recheckstatuscode", "sa_id="+id);
		if(recheckStatusCode!=null){
			if("COMMITED".equals(recheckStatusCode)){
				//如果是存在待审批的流程，则先反提交
				handlerService.handler(flowCaller, "resCommit", "before", new Object[] { id });
				handlerService.handler(flowCaller, "resCommit", "after", new Object[] { id });
			}
		}
		handlerService.handler(flowCaller, "commit", "before", new Object[] { id });
		baseDao.execute("update sale set sa_recheckstatus='复审中',sa_recheckstatuscode='COMMITED' where sa_id=" + id);
		handlerService.handler(flowCaller, "commit", "after", new Object[] { id });
		baseDao.logger.others("复审操作", "发起复审", "Sale", "sa_id", id);
	}
	
	//复审操作
	@Override
	public void recheckAudit(int id,String caller){
		String flowCaller = "Sale!Recheck";
		handlerService.handler(flowCaller, "audit", "before", new Object[] { id });
		baseDao.execute("update sale set sa_recheckstatus='已复审',sa_recheckstatuscode='AUDITED' where sa_id=" + id);
		handlerService.handler(flowCaller, "audit", "after", new Object[] { id });
		baseDao.logger.others("复审操作", "复审成功", "Sale", "sa_id", id);
	}
	
	//反复审操作
	@Override
	public void resRecheck(String caller,int id){
		String flowCaller = "Sale!Recheck";
		Object recheckStatusCode = baseDao.getFieldDataByCondition("sale", "sa_recheckstatuscode", "sa_id="+id);
		if(recheckStatusCode!=null){
			if("COMMITED".equals(recheckStatusCode)){
				handlerService.handler(flowCaller, "resCommit", "before", new Object[] { id });
				baseDao.execute("update sale set sa_recheckstatus=null,sa_recheckstatuscode=null where sa_id="+id);
				handlerService.handler(flowCaller, "resCommit", "after", new Object[] { id });
			}else{
				BaseUtil.showError("只能对待复审的单据进行反复审!");
			}
		}
		baseDao.logger.others("反复审操作", "反复审成功", "Sale", "sa_id", id);
	}
	 public void defaultCycle(String caller,Object sa_id){
		 if (baseDao.isDBSetting(caller, "defaultCycle")) {
			String days = baseDao.getDBSetting(caller,"cycleDays");
			if(days!=null && Integer.parseInt(days)>0){
				baseDao.execute("update saledetail set  sd_delivery = (case when sd_delivery is null then to_date(to_char(sysdate,'yyyy-mm-dd'),'yyyy-mm-dd')+"+days+" else sd_delivery end),sd_pmcdate = (case when sd_pmcdate is null then to_date(to_char(sysdate,'yyyy-mm-dd'),'yyyy-mm-dd')+"+days+" else sd_pmcdate end) where sd_said="+sa_id+"");
			}
		}
	 }
	 
	 public void StockProd(int sa_id){
		 List<Object[]> sale = baseDao.getFieldsDatasByCondition("Sale left join SaleDetail on sa_id=sd_said left join SaleKind on sk_name=sa_kind", new String[]{"sa_custcode","sa_seller","sd_prodcode","sk_clashoption","sk_clashkind","sd_qty","sa_code"}, "sa_id="+sa_id);
		 Employee employee = SystemSession.getUser();
		 int sc_id = baseDao.getSeqId("SaleClash_SEQ");
		 int scd_detno = 1;
		 //是备料冲销的才插入预测冲销单
		 if(sale != null && sale.get(0)[3] != null && "备料冲销".equals(sale.get(0)[3])){
			 //一张销售订单只能插入一张备料冲销单，需要先确认物料里面是否存在是备料冲销类型的销售预测才生成，这样判断再插入会特别麻烦，故先插入，后面没有对应的明细再删除。
			 baseDao.execute("Insert into SaleClash (SC_ID,SC_CODE,SC_DATE,SC_RECORDER,SC_STATUS,SC_SOURCE,SC_SOURCEID,SC_REMARK,SC_STATUSCODE,SC_SOURCECODE,SC_AUDITDATE,SC_AUDITMAN,SC_RECORDERID) values ("+sc_id+",'"+baseDao.sGetMaxNumber("SaleClash", 2)+"',sysdate,'"+employee.getEm_name()+"','已审核','销售单',"+sa_id+",null,'AUDITED','"+sale.get(0)[6]+"',null,null,"+employee.getEm_id()+")");
			 for(Object[]sa:sale){
				 //先根据明细行物料查询BOM展开合并共有多少个子件并且每个子件所需的数量。
				 List<Object[]> sonprod = baseDao.getFieldsDatasByCondition("MA_BOMSTRUCT_MERGE_VIEW", new String[]{"bs_soncode","bs_baseqty"}, "bs_topmothercode='"+sa[2]+"'");
				 if(sonprod!=null && sonprod.size()!=0){
					 for(Object[] prod:sonprod){
						 int bsqty = prod[1] == null ? 0 : Integer.parseInt(prod[1].toString());
						 int saqty = sa[5] == null ? 0 : Integer.parseInt(sa[5].toString());
						 int bsaqty = bsqty * saqty;
						 SqlRowList rs = new SqlRowList();
						 //查询所有子件编号存在销售预测类型是备料冲销的销售预测单据且预测数量大于已冲销数量的数据
						 if(sa[4]!=null && "客户匹配".equals(sa[4])){
							 rs = baseDao.queryForRowSet("select SaleForecastDetail.sd_id sd_id,SaleForecast.sf_code sf_code,SaleForecastDetail.sd_detno sd_detno,SaleForecastDetail.sd_qty-SaleForecastDetail.sd_clashsaleqty sd_qty from SaleForecast left join SaleForecastDetail on sd_sfid=sf_id left join SaleForecastKind on SaleForecast.sf_kind=SaleForecastKind.sf_name where sd_prodcode='"+prod[0]+"' and SaleForecastKind.sf_clashoption='STOCK' and SaleForecast.sf_custcode='"+sa[0]+"' and SaleForecastDetail.sd_qty>SaleForecastDetail.sd_clashsaleqty order by SaleForecast.sf_date asc");
						 }else if(sa[4]!=null && "业务员".equals(sa[4])){
							 rs = baseDao.queryForRowSet("select SaleForecastDetail.sd_id sd_id,SaleForecast.sf_code sf_code,SaleForecastDetail.sd_detno sd_detno,SaleForecastDetail.sd_qty-SaleForecastDetail.sd_clashsaleqty sd_qty from SaleForecast left join SaleForecastDetail on sd_sfid=sf_id left join SaleForecastKind on SaleForecast.sf_kind=SaleForecastKind.sf_name where sd_prodcode='"+prod[0]+"' and SaleForecastKind.sf_clashoption='STOCK' and SaleForecast.sf_name='"+sa[1]+"' and SaleForecastDetail.sd_qty>SaleForecastDetail.sd_clashsaleqty order by SaleForecast.sf_date asc");
						 }else{
							 rs = baseDao.queryForRowSet("select SaleForecastDetail.sd_id sd_id,SaleForecast.sf_code sf_code,SaleForecastDetail.sd_detno sd_detno,SaleForecastDetail.sd_qty-SaleForecastDetail.sd_clashsaleqty sd_qty from SaleForecast left join SaleForecastDetail on sd_sfid=sf_id left join SaleForecastKind on SaleForecast.sf_kind=SaleForecastKind.sf_name where sd_prodcode='"+prod[0]+"' and SaleForecastKind.sf_clashoption='STOCK' and SaleForecastDetail.sd_qty>SaleForecastDetail.sd_clashsaleqty order by SaleForecast.sf_date asc");
						 }
						 while(rs.next() && bsaqty>0){
							 int qty = bsaqty-rs.getInt("sd_qty")>0 ? rs.getInt("sd_qty") : bsaqty;
							 baseDao.execute("Insert into SaleClashdetail (scd_detno,scd_id,scd_scid,scd_ordercode,scd_orderdetno,scd_sfdetid,scd_prodcode,scd_clashqty,scd_remark,SCD_FROMCODE) values ("+scd_detno+++","+baseDao.getSeqId("SaleClashDetail_SEQ")+","+sc_id+",'"+rs.getObject("sf_code")+"',"+rs.getInt("sd_detno")+","+rs.getInt("sd_id")+",'"+prod[0]+"',"+qty+",'销售订单审核自动插入','"+sa[6]+"')");
							 baseDao.updateByCondition("SaleForecastDetail", "sd_clashsaleqty=sd_clashsaleqty+"+qty+",sd_qty=sd_qty-"+qty+"", "sd_id="+rs.getInt("sd_id"));
							 bsaqty = bsaqty-rs.getInt("sd_qty");
						 }
					 }
				 }else{//没有子件的物料直接找该物料的是备料冲销的销售预测单据数据。
					 SqlRowList rs = new SqlRowList();
					 if(sa[4]!=null && "客户匹配".equals(sa[4])){
						 rs = baseDao.queryForRowSet("select SaleForecastDetail.sd_id sd_id,SaleForecast.sf_code sf_code,SaleForecastDetail.sd_detno sd_detno,SaleForecastDetail.sd_qty-SaleForecastDetail.sd_clashsaleqty sd_qty from SaleForecast left join SaleForecastDetail on sd_sfid=sf_id left join SaleForecastKind on SaleForecast.sf_kind=SaleForecastKind.sf_name where sd_prodcode='"+sa[2]+"' and SaleForecastKind.sf_clashoption='STOCK' and SaleForecast.sf_custcode='"+sa[0]+"' and SaleForecastDetail.sd_qty>SaleForecastDetail.sd_clashsaleqty order by SaleForecast.sf_date asc");
					 }else if(sa[4]!=null && "业务员".equals(sa[4])){
						 rs = baseDao.queryForRowSet("select SaleForecastDetail.sd_id sd_id,SaleForecast.sf_code sf_code,SaleForecastDetail.sd_detno sd_detno,SaleForecastDetail.sd_qty-SaleForecastDetail.sd_clashsaleqty sd_qty from SaleForecast left join SaleForecastDetail on sd_sfid=sf_id left join SaleForecastKind on SaleForecast.sf_kind=SaleForecastKind.sf_name where sd_prodcode='"+sa[2]+"' and SaleForecastKind.sf_clashoption='STOCK' and SaleForecast.sf_name='"+sa[1]+"' and SaleForecastDetail.sd_qty>SaleForecastDetail.sd_clashsaleqty order by SaleForecast.sf_date asc");
					 }else{
						 rs = baseDao.queryForRowSet("select SaleForecastDetail.sd_id sd_id,SaleForecast.sf_code sf_code,SaleForecastDetail.sd_detno sd_detno,SaleForecastDetail.sd_qty-SaleForecastDetail.sd_clashsaleqty sd_qty from SaleForecast left join SaleForecastDetail on sd_sfid=sf_id left join SaleForecastKind on SaleForecast.sf_kind=SaleForecastKind.sf_name where sd_prodcode='"+sa[2]+"' and SaleForecastKind.sf_clashoption='STOCK' and SaleForecastDetail.sd_qty>SaleForecastDetail.sd_clashsaleqty order by SaleForecast.sf_date asc");
					 }
					 int saqty = sa[5] == null ? 0 : Integer.parseInt(sa[5].toString());
					 while(rs.next() && saqty>0){
						 int qty = saqty-rs.getInt("sd_qty")>0 ? rs.getInt("sd_qty") : saqty;
						 baseDao.execute("Insert into SaleClashdetail (scd_detno,scd_id,scd_scid,scd_ordercode,scd_orderdetno,scd_sfdetid,scd_prodcode,scd_clashqty,scd_remark,SCD_FROMCODE) values ("+scd_detno+++","+baseDao.getSeqId("SaleClashDetail_SEQ")+","+sc_id+",'"+rs.getObject("sf_code")+"',"+rs.getInt("sd_detno")+","+rs.getInt("sd_id")+",'"+sa[2]+"',"+qty+",'销售订单审核自动插入','"+sa[6]+"')");
						 baseDao.updateByCondition("SaleForecastDetail", "sd_clashsaleqty=sd_clashsaleqty+"+qty+",sd_qty=sd_qty-"+qty+"", "sd_id="+rs.getInt("sd_id"));
						 saqty = saqty-rs.getInt("sd_qty");
					 }
				 }
			 }
		 }
	 }
	 public void deleteSaleClash(int sa_id){
		 List<Object[]> saleclash = baseDao.getFieldsDatasByCondition("SaleClash left join SaleClashdetail on scd_scid=sc_id", new String[]{"scd_sfdetid","scd_clashqty"}, "SC_SOURCEID="+sa_id);
		 for(Object[]sc:saleclash){
			 double qty = sc[1] ==null?0: Double.parseDouble(sc[1].toString());
			 baseDao.execute("update SaleForecastDetail set sd_clashsaleqty=sd_clashsaleqty-"+qty+",sd_qty=sd_qty+"+qty+" where sd_id="+sc[0]);
		 }
	 }
	 /**
	  * 博信 销售订单转拨出单   maz 2018030287
	  */
	 @Override
	 public void turnPage(int id,String caller,String data){
		 if(data!=null && "Sale".equals(data)){
			 handlerService.handler(caller, "turnAppropriate", "after", new Object[] { id });
		 }
	 }
	 /**
	  * 2018040558  销售订单也进行物料精度限制，非用品精度为0的不允许数量出现小数位 maz
	  * @param sa_id
	  */
	 private void checkPrecision(int sa_id) { 
		 String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(sd_detno) from SaleDetail inner join Sale on sd_said=sa_id left join product on pr_code=sd_prodcode where "
									+ "sd_said=? and round(sd_qty,0)<>sd_qty and NVL(pr_precision,0)=0  and NVL(pr_groupcode,' ')<>'用品' ",
							String.class, sa_id);
		if (dets != null) {
			BaseUtil.showError("计算精度是0的物料不能以小数出入库!行号：" + dets);
		}
	 }
	 /**
	 * 2018040518   销售预测、销售订单、出货通知单提交、审核时增加限制：客户编号、客户名称，应收客户编号、应收客户名称与客户资料里编号、名称不一致的，限制提交、审核 maz
	 */
	public void checkCustomer(Integer id){
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(sa_code) from sale where sa_id=? and (sa_apcustcode,sa_apcustname) not in (select cu_code,cu_name from customer)",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("该张销售订单应收客户与客户资料不匹配，限制当前操作，请确认");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(sa_code) from sale where sa_id=? and (sa_custcode,sa_custname) not in (select cu_code,cu_name from customer)",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("该张销售订单客户与客户资料不匹配，限制当前操作，请确认");
		}
	}
}
