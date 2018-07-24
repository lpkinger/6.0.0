package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.model.Key;
import com.uas.erp.service.scm.EvaluationService;

@Service("evaluationService")
public class EvaluationServiceImpl implements EvaluationService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Autowired
	private TransferRepository transferRepository;

	@Override
	public void saveEvaluation(String formStore, String gridStore, String gridStore2, String gridStore3) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> grid2 = BaseUtil.parseGridStoreToMaps(gridStore2);
		List<Map<Object, Object>> grid3 = BaseUtil.parseGridStoreToMaps(gridStore3);
		// 执行保存前的其它逻辑
		handlerService.beforeSave("Evaluation", new Object[] { store, grid, grid2, grid3 });
		// 保存Evaluation
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Evaluation", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存EvaluationDetail
		if (gridStore != null && !"".equals(gridStore)) {
			for (Map<Object, Object> m : grid) {
				m.put("evd_id", baseDao.getSeqId("EvaluationDetail_SEQ"));
			}
			getEvaluationDetail(grid);
		}
		// 保存EvaluationProduct
		if (gridStore2 != null && !"".equals(gridStore2)) {
			for (Map<Object, Object> m : grid2) {
				m.put("evp_id", baseDao.getSeqId("EvaluationProduct_SEQ"));
			}
		}
		// 保存EvaluationProcess
		if (gridStore3 != null && !"".equals(gridStore3)) {
			for (Map<Object, Object> m : grid3) {
				m.put("evp_id", baseDao.getSeqId("EvaluationProcess_SEQ"));
			}
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(grid, "EvaluationDetail"));
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(grid2, "EvaluationProduct"));
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(grid3, "EvaluationProcess"));
		// 物料成本A ev_materialcost
		getTotal(store.get("ev_id"));
		baseDao.execute("update Evaluation set ev_materialcost = round(nvl((select sum(nvl(evd_doubleamount,0)) from evaluationdetail where evd_evid=ev_id and evd_level='0'),0),2) where ev_id="
				+ store.get("ev_id"));
		// 记录操作
		baseDao.logger.save("Evaluation", "ev_id", store.get("ev_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave("Evaluation", new Object[] { store, grid, grid2, grid3 });
	}

	@Override
	public void deleteEvaluation(int ev_id) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("Evaluation", "ev_checkstatuscode", "ev_id=" + ev_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel("Evaluation", ev_id);

		// 删除Evaluation
		baseDao.deleteById("Evaluation", "ev_id", ev_id);
		// 删除EvaluationReferDetail
		baseDao.deleteById("EvaluationReferDetail", "evd_evid", ev_id);
		// 删除EvaluationDetail
		baseDao.deleteById("EvaluationDetail", "evd_evid", ev_id);
		// 删除EvaluationProduct
		baseDao.deleteById("EvaluationProduct", "evp_evid", ev_id);
		// 删除EvaluationProcess
		baseDao.deleteById("EvaluationProcess", "evp_evid", ev_id);

		// 记录操作
		baseDao.logger.delete("Evaluation", "ev_id", ev_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel("Evaluation", ev_id);
	}

	@Override
	public void updateEvaluationById(String formStore, String gridStore, String gridStore2, String gridStore3) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> gstore2 = BaseUtil.parseGridStoreToMaps(gridStore2);
		List<Map<Object, Object>> gstore3 = BaseUtil.parseGridStoreToMaps(gridStore3);
		// 只能修改[在录入]的单据资料!
		Object[] status = baseDao.getFieldsDataByCondition("Evaluation", new String[] { "ev_checkstatuscode", "nvl(ev_materialcost,0)" },
				"ev_id=" + store.get("ev_id"));
		StateAssert.updateOnlyEntering(status[0]);
		// 执行修改前的其它逻辑
		handlerService.beforeSave("Evaluation", new Object[] { store, gstore, gstore2, gstore3 });
		// 修改Evaluation
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Evaluation", "ev_id");
		baseDao.execute(formSql);
		// 修改EvaluationDetail
		if (gstore.size() > 0) {
			getEvaluationDetail(gstore);
			double changeamount = 0.0;
			for (Map<Object, Object> s : gstore) {
				Object evdid = s.get("evd_id");
				if (StringUtil.hasText(s.get("evd_prodcode")) && StringUtil.hasText(s.get("evd_price"))
						&& StringUtil.hasText(s.get("evd_qty"))) {
					double newamount = NumberUtil.formatDouble(
							Double.parseDouble(s.get("evd_price").toString()) * Double.parseDouble(s.get("evd_qty").toString()), 5);
					if (evdid == null || evdid.equals("") || evdid.equals("0") || Integer.parseInt(evdid.toString()) == 0) {// 新添加的数据，id不存在
						changeamount = changeamount + newamount;
					} else {
						SqlRowList rs = baseDao.queryForRowSet("select evd_amount from EvaluationDetail where evd_id=?", evdid);
						if (rs.next()) {
							System.out.println(newamount);
							changeamount = changeamount + (newamount - rs.getGeneralDouble("evd_amount"));
						}
					}
				}
			}
			baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "EvaluationDetail", "evd_id"));
			baseDao.execute("update Evaluation set ev_materialcost=round(nvl(ev_materialcost,0)+" + changeamount + ",2) where ev_id="
					+ store.get("ev_id"));
		}
		// 修改EvaluationProduct
		if (gstore2.size() > 0) {
			baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore2, "EvaluationProduct", "evp_id"));
		}
		// 修改EvaluationProcess
		if (gstore3.size() > 0) {
			baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore3, "EvaluationProcess", "evp_id"));
		}
		getTotal(store.get("ev_id"));
		// 记录操作
		baseDao.logger.update("Evaluation", "ev_id", store.get("ev_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave("Evaluation", new Object[] { store, gstore, gstore2, gstore3 });
	}

	private void getTotal(Object ev_id) {
		baseDao.execute("update EvaluationDetail set evd_amount = round(nvl(evd_price,0)*nvl(evd_qty,0),5) where evd_evid=" + ev_id
				+ " and nvl(evd_price,0)<>0 and nvl(evd_qty,0)<>0");
		// 开发费用
		Object EVP_DEVELOPERFEES = baseDao.getFieldDataByCondition("EVALUATIONPRODUCT", "sum(nvl(EVP_DEVELOPERFEES,0))", "evp_evid="+ev_id);
		if(EVP_DEVELOPERFEES!=null&&!"0".equals(EVP_DEVELOPERFEES.toString())){
			baseDao.execute("update Evaluation set EV_DEVELOPERFEES = "+EVP_DEVELOPERFEES+" where ev_id="+ ev_id);
		}
		// 制造成本B ev_makecost
		Object evp_sipmcprcosts = baseDao.getFieldDataByCondition("EVALUATIONPROCESS", "sum(nvl(evp_sipmcprcosts,0))", "evp_evid="+ev_id);
		if(evp_sipmcprcosts!=null&&!"0".equals(evp_sipmcprcosts.toString())){
			baseDao.execute("update Evaluation set ev_makecost = "+evp_sipmcprcosts+" where ev_id="+ ev_id);
		}
		// 开发及样机费用C ev_kfyjfee: C=[开发费用+样机总数*(A+B)]/年需求
		baseDao.execute("update Evaluation set ev_kfyjfee = round((nvl(EV_DEVELOPERFEES,0)+nvl(ev_yjqty,0)*(ev_materialcost+ev_makecost))/ev_yearqty,2) where ev_id="
				+ ev_id + " and nvl(ev_yearqty,0)<>0");
		// 许可费用D ev_licensefee
		baseDao.execute("update Evaluation set ev_licensefee = nvl((select sum(nvl(evp_permitfee,0)) from EVALUATIONPRODUCT where evp_evid=ev_id),0) where ev_id="
				+ ev_id);
		// 认证费E ev_certificationfee
		baseDao.execute("update Evaluation set ev_certificationfee = nvl((select sum(nvl(evp_anthenticationfee,0)) from EVALUATIONPRODUCT where evp_evid=ev_id),0) where ev_id="
				+ ev_id);
		// 认证费许可费 ev_permitfee
		baseDao.execute("update Evaluation set ev_permitfee = nvl((select sum(nvl(evp_anthenticationfee,0))+sum(nvl(evp_permitfee,0)) from EVALUATIONPRODUCT where evp_evid=ev_id),0) where ev_id="
				+ ev_id);
		// 模具费F ev_mouldfee： F=模具总价/年需求
		baseDao.execute("update Evaluation set ev_mouldfee = round(nvl((select sum(nvl(evp_mouldfee,0)) from EVALUATIONPRODUCT where evp_evid=ev_id),0)/ev_yearqty,2) where ev_id="
				+ ev_id + " and nvl(ev_yearqty,0)<>0");
		// 报废比例G ev_scrapratio
		// 期间费及利润H ev_duringfee
		// 物流费I ev_logisticsfee

		// 单位成本（不含税）K ev_unitcost：K=[(A+B)*(1+G)+C+认证费许可费+F]+I+J
		baseDao.execute("update Evaluation set ev_unitcost=round((((nvl(ev_materialcost,0)+nvl(ev_makecost,0))*(1+nvl(ev_scrapratio,0))+nvl(ev_kfyjfee,0)+nvl(ev_permitfee,0)+nvl(ev_mouldfee,0))*(1+nvl(ev_duringfee,0))+nvl(ev_logisticsfee,0)+nvl(ev_othercost,0))/ev_rate ,2) where ev_id="
				+ ev_id);
		// 单位成本（含税）L ev_taxunitcost：L=K*(1+17%)
		Object cr_taxrate = baseDao.getFieldDataByCondition("Currencys", "nvl(cr_taxrate,0)", "cr_name = (select ev_currency from Evaluation where ev_id="+ev_id+")");
		baseDao.execute("update Evaluation set ev_taxunitcost=round(NVL(ev_unitcost,0)*(1+"+Double.parseDouble(cr_taxrate.toString())/100+"),2) where ev_id=" + ev_id);
		// 总成本
		baseDao.execute("update Evaluation set ev_cost=round((nvl(ev_materialcost,0)+nvl(ev_makecost,0))*(1+nvl(ev_scrapratio,0))+nvl(ev_kfyjfee,0)+nvl(ev_permitfee,0)+nvl(ev_mouldfee,0)+nvl(ev_logisticsfee,0)+nvl(ev_othercost,0),2) where ev_id="
				+ ev_id);
		// 毛利率=[建议售价(未税)*报价币别汇率-总成本]/[建议售价(未税)*报价币别汇率]
		baseDao.execute("update Evaluation set ev_grossprofitrate=round((ev_unitcost*NVL(ev_rate,0)-nvl(ev_cost,0))/(NVL(ev_rate,0)*ev_unitcost),2) where ev_id=" + ev_id
				+ " and nvl(ev_unitcost,0)<>0 and NVL(ev_rate,0)<>0");
	}

	@Override
	public String[] printEvaluation(int ev_id, String reportName, String condition) {
		// 执行打印前的其它逻辑
		handlerService.beforePrint("Evaluation", ev_id);
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 修改打印状态
		baseDao.print("Evaluation", "ev_id=" + ev_id, "ev_printstatus", "ev_printstatuscode");
		// 记录操作
		baseDao.logger.print("Evaluation", "ev_id", ev_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint("Evaluation", ev_id);
		return keys;
	}

	@Override
	public void auditEvaluation(int ev_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Evaluation", "ev_checkstatuscode", "ev_id=" + ev_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit("Evaluation", ev_id);
		// 执行审核操作
		baseDao.audit("Evaluation", "ev_id=" + ev_id, "ev_checkstatus", "ev_checkstatuscode", "ev_auditdate", "ev_auditman");
		// 记录操作
		baseDao.logger.audit("Evaluation", "ev_id", ev_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit("Evaluation", ev_id);
	}

	@Override
	public void resAuditEvaluation(int ev_id) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Evaluation", "ev_checkstatuscode", "ev_id=" + ev_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit("Evaluation", ev_id);
		boolean haveturn = baseDao.checkByCondition("Quotation", "qu_sourceid=" + ev_id);
		if (!haveturn) {
			BaseUtil.showError("已转报价单，不允许反审核！");
		}
		// 执行反审核操作
		baseDao.resOperate("Evaluation", "ev_id=" + ev_id, "ev_checkstatus", "ev_checkstatuscode");
		// 记录操作
		baseDao.logger.resAudit("Evaluation", "ev_id", ev_id);
		handlerService.afterResAudit("Evaluation", ev_id);
	}

	@Override
	public void submitEvaluation(int ev_id) {
		getTotal(ev_id);
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Evaluation", "ev_checkstatuscode", "ev_id=" + ev_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit("Evaluation", ev_id);
		// 执行提交操作
		baseDao.submit("Evaluation", "ev_id=" + ev_id, "ev_checkstatus", "ev_checkstatuscode");
		// 记录操作
		baseDao.logger.submit("Evaluation", "ev_id", ev_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit("Evaluation", ev_id);
	}

	@Override
	public void resSubmitEvaluation(int ev_id) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Evaluation", "ev_checkstatuscode", "ev_id=" + ev_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行提交前的其它逻辑
		handlerService.beforeResSubmit("Evaluation", ev_id);
		// 执行提交操作
		baseDao.resOperate("Evaluation", "ev_id=" + ev_id, "ev_checkstatus", "ev_checkstatuscode");
		// 记录操作
		baseDao.logger.resSubmit("Evaluation", "ev_id", ev_id);
		// 执行提交后的其它逻辑
		handlerService.afterResSubmit("Evaluation", ev_id);
	}

	@Override
	public void bannedEvaluation(int ev_id) {
		// 执行禁用操作
		baseDao.banned("Evaluation", "ev_id=" + ev_id, "ev_checkstatus", "ev_checkstatuscode");
		// 记录操作
		baseDao.logger.banned("Evaluation", "ev_id", ev_id);
	}

	@Override
	public void resBannedEvaluation(int ev_id) {
		// 执行反禁用操作
		baseDao.resOperate("Evaluation", "ev_id=" + ev_id, "ev_checkstatus", "ev_checkstatuscode");
		// 记录操作
		baseDao.logger.resBanned("Evaluation", "ev_id", ev_id);
	}

	static String GETBOMDETAIL = "select pr_detail,pr_spec,pr_unit,bs_soncode,bs_actqty,nvl(bs_purcpricermb,0) bs_purcpricermb,nvl(bs_osprice,0) bs_osprice,pr_id,pr_manutype,bs_level,bs_ifrep,bs_standardprice,nvl(bs_rate,0) bs_rate,bs_m,bs_l "
			+ "from bomstruct,product where pr_code=bs_soncode and bs_topbomid=? and bs_topmothercode=? order by bs_idcode ";
	static final String INSERTEVALUATIONREFERDETAIL = "INSERT INTO evaluationreferdetail(evd_id, evd_evid, evd_detno, evd_prodcode, evd_qty,"
			+ "evd_currency,evd_rate,evd_doubleprice,evd_price,evd_amount,evd_prodid,evd_iscustprod,evd_level,evd_ifrep,evd_prodname,evd_prodspec,evd_produnit) values"
			+ "(EVALUATIONREFERDETAIL_SEQ.nextval,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String INSERTEVALUATIONDETAIL2 = "INSERT INTO EvaluationDetail(evd_id, evd_evid, evd_detno, evd_prodcode, evd_bomid) values"
			+ "(EVALUATIONDETAIL_SEQ.nextval,?,?,?,?)";
	static final String INSERTEVALUATIONDETAIL = "INSERT INTO EvaluationDetail(evd_id, evd_evid, evd_detno, evd_prodcode, evd_qty,"
			+ "evd_currency,evd_rate,evd_doubleprice,evd_price,evd_amount,evd_prodid,evd_iscustprod,evd_level,evd_ifrep,evd_prodname,evd_prodspec,evd_produnit,evd_doubleamount) values"
			+ "(EVALUATIONDETAIL_SEQ.nextval,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	@Override
	public void calBOMOfferCost(int ev_id, int bo_id, String pr_code) {
		// 只能对状态为[在录入]的订单进行成本计算操作!
		Object status = baseDao.getFieldDataByCondition("Evaluation", "ev_checkstatuscode", "ev_id=" + ev_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError("只能对[在录入]的单据进行成本计算操作!");
		}
		baseDao.updateByCondition("Evaluation", "ev_offerbomid =" + bo_id + ",ev_offerprcode='" + pr_code + "'", "ev_id =" + ev_id);
		baseDao.deleteById("EvaluationDetail", "evd_evid", ev_id);
		Object prspecvalue = null;
		Object prrefno = null;
		if (!pr_code.equals("") && bo_id == 0) {
			prspecvalue = baseDao.getFieldDataByCondition("product", "pr_specvalue", "pr_code='" + pr_code + "'");
			try {
				if (!prspecvalue.equals("")) {
					if (prspecvalue.equals("SPECIFIC")) {
						prrefno = baseDao.getFieldDataByCondition("product", "pr_refno", "pr_code='" + pr_code + "'");
						bo_id = Integer.parseInt(baseDao.getFieldDataByCondition("bom", "bo_id", "bo_mothercode='" + prrefno + "'")
								.toString());
					} else {
						bo_id = Integer.parseInt(baseDao.getFieldDataByCondition("bom", "bo_id", "bo_mothercode='" + pr_code + "'")
								.toString());
					}
				}
			} catch (Exception ex) {
				BaseUtil.showError(ex.getMessage());
			}
		}
		try {

			baseDao.procedure("SP_COSTCOUNT", new Object[] { bo_id, pr_code, "最新采购单价" });

			Double evrate = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select nvl(cr_rate,1) from Evaluation left join Currencys on ev_currency=cr_name where ev_id=? and nvl(cr_status,' ')<>'已禁用'",
							Double.class, ev_id);

			// 估价单计算采集
			baseDao.execute("merge into BomStruct using (select cr_rate,cr_name from currencys where nvl(cr_status,' ')<>'已禁用') src on( bs_currency=cr_name) when matched then update set bs_l=(CASE WHEN bs_currency='RMB' then bs_purcprice/(1+bs_rate/100) ELSE bs_purcprice*cr_rate  END) where bs_topbomid="
					+ bo_id
					+ " and bs_topmothercode='"
					+ pr_code
					+ "' and (nvl(bs_sonbomid,0)=0 or nvl(bs_sonbomid,0) in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 ))  ");
			baseDao.execute("update BomStruct set bs_m=bs_l*bs_actqty where bs_topbomid="
					+ bo_id
					+ " and bs_topmothercode='"
					+ pr_code
					+ "' and (nvl(bs_sonbomid,0)=0 or nvl(bs_sonbomid,0) in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 ))");
			baseDao.execute("update BomStruct set bs_currency='RMB',bs_purcprice=bs_osprice,bs_purcpricermb=0,bs_totalpurcpricermb=0,bs_totalpurcpriceusd=0 where bs_topbomid="
					+ bo_id
					+ " and bs_topmothercode='"
					+ pr_code
					+ "' and (nvl(bs_sonbomid,0)>0 or bs_soncode='"
					+ pr_code
					+ "') and nvl(bs_sonbomid,0) not in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 ) ");

			String SQLStr = "select bs_idcode,bs_soncode from BomStruct where bs_topbomid="
					+ bo_id
					+ " and bs_topmothercode='"
					+ pr_code
					+ "' and nvl(bs_sonbomid,0)>0 and nvl(bs_sonbomid,0) not in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 ) ORDER BY bs_level";
			SqlRowList rs = baseDao.queryForRowSet(SQLStr);
			while (rs.next()) {// bs_osprice 在存储过程中计算出来的值是含税的委外单价
				SQLStr = "SELECT sum(nvl(bs_m,0)) from BomStruct WHERE bs_topbomid=" + bo_id + " and bs_topmothercode='" + pr_code
						+ "' and  bs_mothercode='" + rs.getString("bs_soncode") + "' ";
				SqlRowList rsthis = baseDao.queryForRowSet(SQLStr);
				if (rsthis.next()) {
					SQLStr = "update bomstruct set bs_m=round((" + rsthis.getString(1)
							+ "+nvl(bs_osprice,0)/(1+nvl(bs_rate,0)/100)),8)*bs_baseqty " + " where bs_topbomid=" + bo_id
							+ " and bs_idcode=" + rs.getString("bs_idcode");
					baseDao.execute(SQLStr);
				}
			}
			// 取出本位币以及本位币对应的税率,将原来默认插入RMB 和默认17的税率修改成本位币
			String defaultCurrency = baseDao.getDBSetting("defaultCurrency");
			Object defaultrate = baseDao.getFieldDataByCondition("currencys", "cr_taxrate", "CR_CODE='" + defaultCurrency
					+ "' and cr_statuscode='AUDTIED'");
			float bs_rate = 0;
			if (defaultrate != null && StringUtils.isNumeric(defaultrate.toString())) {
				bs_rate = Float.valueOf(defaultrate.toString());
			}
			rs = baseDao.queryForRowSet(GETBOMDETAIL, bo_id, pr_code);
			int count = 1;
			while (rs.next()) {// 委外单价（除税）+下阶成本
				Double doubleprice = rs.getGeneralDouble("bs_l");
				int iscustprod = 0;// 'CUSTOFFER'
				if ("CUSTOFFER".equals(rs.getString("pr_manutype"))) {
					iscustprod = -1;
				} else if ("OSMAKE".equals(rs.getString("pr_manutype"))) {
					doubleprice = rs.getGeneralDouble("bs_osprice") / (1 + rs.getGeneralDouble("bs_rate") / 100);
				}
				Double price = NumberUtil.formatDouble(doubleprice / evrate, 6);// 估价币别汇率
				baseDao.getJdbcTemplate().update(
						INSERTEVALUATIONDETAIL,
						new Object[] { ev_id, count++, rs.getString("bs_soncode"), rs.getGeneralDouble("bs_actqty"), defaultCurrency,
								bs_rate, doubleprice, price, NumberUtil.formatDouble(rs.getGeneralDouble("bs_m") / evrate, 6),
								rs.getInt("pr_id"), iscustprod, rs.getString("bs_level"), rs.getGeneralInt("bs_ifrep"),
								rs.getString("pr_detail"), rs.getString("pr_spec"), rs.getString("pr_unit"),NumberUtil.formatDouble(rs.getGeneralDouble("bs_m") , 6)});
			}
			baseDao.execute(
					"update evaluationdetail set evd_amount=round(nvl(evd_amount,0),5) where evd_evid=? and evd_level='0' and evd_prodcode=?",
					ev_id, pr_code);
			//原币金额
			//baseDao.execute("update EvaluationDetail set EVD_DOUBLEAMOUNT = (select sum(nvl(evd_doubleamount,0)) from EvaluationDetail where evd_evid="+ev_id+" and evd_level<>'0') where evd_evid="+ev_id+" and evd_level='0' and evd_prodcode='"+pr_code+"'");
			Double amount = baseDao.queryForObject("select nvl(EVD_DOUBLEAMOUNT,0) from evaluationdetail where evd_evid= ? and evd_level='0' "
					+ "and evd_prodcode= ?", Double.class, ev_id, pr_code);
			if (amount == null) {
				amount = (double) 0;
			}
			baseDao.execute("update Evaluation set ev_materialcost = round(" + amount + ",2) where ev_id=" + ev_id);
			baseDao.execute("update Evaluation set ev_cost=round((nvl(ev_materialcost,0)+nvl(ev_makecost,0))*(1+nvl(ev_scrapratio,0))+nvl(ev_kfyjfee,0)+nvl(ev_permitfee,0)+nvl(ev_mouldfee,0)+nvl(ev_logisticsfee,0)+nvl(ev_othercost,0),2) where ev_id="
					+ ev_id);
			getTotal(ev_id);
		} catch (Exception e) {
			BaseUtil.showError(e.getMessage());
		}
	}

	@Override
	public void calBOMCost(int ev_id, int bo_id, String pr_code) {
		// 只能对状态为[在录入]的订单进行成本计算操作!
		Object status = baseDao.getFieldDataByCondition("Evaluation", "ev_checkstatuscode", "ev_id=" + ev_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError("只能对[在录入]的单据进行成本计算操作!");
		}
		baseDao.deleteById("EvaluationReferDetail", "evd_evid", ev_id);
		Object prspecvalue = null;
		Object prrefno = null;
		if (!pr_code.equals("") && bo_id == 0) {
			prspecvalue = baseDao.getFieldDataByCondition("product", "pr_specvalue", "pr_code='" + pr_code + "'");
			try {
				if (!prspecvalue.equals("")) {
					if (prspecvalue.equals("SPECIFIC")) {
						prrefno = baseDao.getFieldDataByCondition("product", "pr_refno", "pr_code='" + pr_code + "'");
						bo_id = Integer.parseInt(baseDao.getFieldDataByCondition("bom", "bo_id", "bo_mothercode='" + prrefno + "'")
								.toString());
					} else {
						bo_id = Integer.parseInt(baseDao.getFieldDataByCondition("bom", "bo_id", "bo_mothercode='" + pr_code + "'")
								.toString());
					}
				}
			} catch (Exception ex) {
				BaseUtil.showError(ex.getMessage());
			}
		}
		try {

			baseDao.procedure("SP_COSTCOUNT", new Object[] { bo_id, pr_code, "最新采购单价" });

			Double evrate = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select nvl(cr_rate,1) from Evaluation left join Currencys on ev_currency=cr_name where ev_id=? and nvl(cr_status,' ')<>'已禁用'",
							Double.class, ev_id);

			// 估价单计算采集
			baseDao.execute("merge into BomStruct using (select cr_rate,cr_name from currencys where nvl(cr_status,' ')<>'已禁用') src on( bs_currency=cr_name) when matched then update set bs_l=(CASE WHEN bs_currency='RMB' then bs_purcprice/(1+bs_rate/100) ELSE bs_purcprice*cr_rate  END) where bs_topbomid="
					+ bo_id
					+ " and bs_topmothercode='"
					+ pr_code
					+ "' and (nvl(bs_sonbomid,0)=0 or nvl(bs_sonbomid,0) in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 ))  ");
			baseDao.execute("update BomStruct set bs_m=bs_l*bs_actqty where bs_topbomid="
					+ bo_id
					+ " and bs_topmothercode='"
					+ pr_code
					+ "' and (nvl(bs_sonbomid,0)=0 or nvl(bs_sonbomid,0) in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 ))");
			baseDao.execute("update BomStruct set bs_currency='RMB',bs_purcprice=bs_osprice,bs_purcpricermb=0,bs_totalpurcpricermb=0,bs_totalpurcpriceusd=0 where bs_topbomid="
					+ bo_id
					+ " and bs_topmothercode='"
					+ pr_code
					+ "' and (nvl(bs_sonbomid,0)>0 or bs_soncode='"
					+ pr_code
					+ "') and nvl(bs_sonbomid,0) not in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 ) ");

			String SQLStr = "select bs_idcode,bs_soncode from BomStruct where bs_topbomid="
					+ bo_id
					+ " and bs_topmothercode='"
					+ pr_code
					+ "' and nvl(bs_sonbomid,0)>0 and nvl(bs_sonbomid,0) not in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 ) ORDER BY bs_level";
			SqlRowList rs = baseDao.queryForRowSet(SQLStr);
			while (rs.next()) {// bs_osprice 在存储过程中计算出来的值是含税的委外单价
				SQLStr = "SELECT sum(nvl(bs_m,0)) from BomStruct WHERE bs_topbomid=" + bo_id + " and bs_topmothercode='" + pr_code
						+ "' and  bs_mothercode='" + rs.getString("bs_soncode") + "' ";
				SqlRowList rsthis = baseDao.queryForRowSet(SQLStr);
				if (rsthis.next()) {
					SQLStr = "update bomstruct set bs_m=round((" + rsthis.getString(1)
							+ "+nvl(bs_osprice,0)/(1+nvl(bs_rate,0)/100)),8)*bs_baseqty " + " where bs_topbomid=" + bo_id
							+ " and bs_idcode=" + rs.getString("bs_idcode");
					baseDao.execute(SQLStr);
				}
			}
			// 取出本位币以及本位币对应的税率,将原来默认插入RMB 和默认17的税率修改成本位币
			String defaultCurrency = baseDao.getDBSetting("defaultCurrency");
			Object defaultrate = baseDao.getFieldDataByCondition("currencys", "cr_taxrate", "CR_CODE='" + defaultCurrency
					+ "' and cr_statuscode='AUDTIED'");
			float bs_rate = 0;
			if (defaultrate != null && StringUtils.isNumeric(defaultrate.toString())) {
				bs_rate = Float.valueOf(defaultrate.toString());
			}
			rs = baseDao.queryForRowSet(GETBOMDETAIL, bo_id, pr_code);
			int count = 1;
			while (rs.next()) {// 委外单价（除税）+下阶成本
				Double doubleprice = rs.getGeneralDouble("bs_l");
				int iscustprod = 0;// 'CUSTOFFER'
				if ("CUSTOFFER".equals(rs.getString("pr_manutype"))) {
					iscustprod = -1;
				} else if ("OSMAKE".equals(rs.getString("pr_manutype"))) {
					doubleprice = rs.getGeneralDouble("bs_osprice") / (1 + rs.getGeneralDouble("bs_rate") / 100);
				}
				Double price = NumberUtil.formatDouble(doubleprice / evrate, 6);// 估价币别汇率
				baseDao.getJdbcTemplate().update(
						INSERTEVALUATIONREFERDETAIL,
						new Object[] { ev_id, count++, rs.getString("bs_soncode"), rs.getGeneralDouble("bs_actqty"), defaultCurrency,
								bs_rate, doubleprice, price, NumberUtil.formatDouble(rs.getGeneralDouble("bs_m") / evrate, 6),
								rs.getInt("pr_id"), iscustprod, rs.getString("bs_level"), rs.getGeneralInt("bs_ifrep"),
								rs.getString("pr_detail"), rs.getString("pr_spec"), rs.getString("pr_unit") });
			}
			baseDao.execute(
					"update evaluationreferdetail set evd_amount=round(nvl(evd_amount,0),5) where evd_evid=? and evd_level='0' and evd_prodcode=?",
					ev_id, pr_code);

			// 检查报价材料成本计算，如果没有明细则把“参考材料明细”的内容同样插入到这里
			int Count = baseDao.getCountByCondition("EvaluationDetail", "evd_evid = " + ev_id);
			if (Count == 0) {
				baseDao.execute("INSERT INTO EVALUATIONDETAIL(EVD_ID,EVD_EVID,EVD_DETNO,EVD_PRODID,EVD_QTY,EVD_ISCUSTPROD,EVD_CURRENCY,EVD_RATE,EVD_DOUBLEPRICE,EVD_PRICE,EVD_AMOUNT,EVD_REFCODE,EVD_REMARK,EVD_PRODCODE,EVD_LEVEL,EVD_IFREP,EVD_PRODNAME,EVD_PRODSPEC,EVD_PRODUNIT) SELECT EVALUATIONDETAIL_SEQ.nextval,EVD_EVID,EVD_DETNO,EVD_PRODID,EVD_QTY,EVD_ISCUSTPROD,EVD_CURRENCY,EVD_RATE,EVD_DOUBLEPRICE,EVD_PRICE,EVD_AMOUNT,EVD_REFCODE,EVD_REMARK,EVD_PRODCODE,EVD_LEVEL,EVD_IFREP,EVD_PRODNAME,EVD_PRODSPEC,EVD_PRODUNIT FROM EVALUATIONREFERDETAIL WHERE EVD_EVID = "
						+ ev_id);
				Double amount = baseDao.queryForObject(
						"select nvl(evd_amount,0) from evaluationdetail where evd_evid= ? and evd_level='0' and evd_prodcode= ?",
						Double.class, ev_id, pr_code);
				if (amount == null) {
					amount = (double) 0;
				}
				baseDao.execute("update Evaluation set ev_materialcost = round(" + amount + ",2) where ev_id=" + ev_id);
			}
			baseDao.execute("update Evaluation set ev_cost=round((nvl(ev_materialcost,0)+nvl(ev_makecost,0))*(1+nvl(ev_scrapratio,0))+nvl(ev_kfyjfee,0)+nvl(ev_permitfee,0)+nvl(ev_mouldfee,0)+nvl(ev_logisticsfee,0)+nvl(ev_othercost,0),2) where ev_id="
					+ ev_id);
		} catch (Exception e) {
			BaseUtil.showError(e.getMessage());
		}
	}

	@Override
	public void bomInsert(int ev_id) {
		int count = baseDao.getCountByCondition("EvaluationDetail", "evd_evid=" + ev_id);
		if (count > 0) {
			BaseUtil.showError("明细里已经有产品明细不能导入!");
		} else {
			SqlRowList rs = baseDao
					.queryForRowSet("select bo_mothercode,bo_id from bom where bo_statuscode in ('COMMITED','AUDITED') order by bo_mothercode");
			int detno = 1;
			while (rs.next()) {
				baseDao.getJdbcTemplate().update(INSERTEVALUATIONDETAIL2,
						new Object[] { ev_id, detno++, rs.getObject("bo_mothercode"), rs.getObject("bo_id") });
			}
			Object maxdetno = baseDao.getFieldDataByCondition("EvaluationDetail", "max(evd_detno)+1", "evd_evid=" + ev_id);
			rs = baseDao.queryForRowSet("select pr_code from product where pr_specvalue='SPECIFIC' and pr_statuscode='AUDITED'");
			detno = Integer.parseInt(maxdetno.toString());
			while (rs.next()) {
				baseDao.getJdbcTemplate().update(INSERTEVALUATIONDETAIL2, new Object[] { ev_id, detno++, rs.getObject("pr_code"), 0 });
			}
		}
		// 记录操作
		baseDao.logger.others("导入建立BOM的产品", "导入成功", "Evaluation", "ev_id", ev_id);
	}

	@Override
	public void bomVastCost(int ev_id) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Evaluation", "ev_checkstatuscode", "ev_id=" + ev_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError("只能对[在录入]的单据进行操作!");
		}
		// 存储过程
		String res = baseDao.callProcedure("SP_CACBOMCOST", new Object[] { ev_id });
		if (res != null && !res.trim().equals("") && !"OK".equals(res.toUpperCase())) {
			BaseUtil.showError(res);
		}
		// 记录操作
		baseDao.logger.others("批量计算产品BOM成本", "计算成功", "Evaluation", "ev_id", ev_id);
	}

	@Override
	public int turnQuotation(int id) {
		Key key = transferRepository.transfer("Evaluation", id);
		int quid = key.getId();
		baseDao.execute("update quotation set qu_sellerid=(select qu_id from employee where em_code=qu_sellercode) where qu_id=" + quid);
		// 转入明细
		transferRepository.transferDetail("Evaluation", id, key);
		baseDao.logger.others("转报价单", "转报价单成功", "Evaluation", "ev_id", id);
		return quid;
	}

	final static String[] GETCURRENCY = {
			"SELECT ROUND(NVL(price0,0),8) doubleprice,ppd_currency currency, ppd_rate rate,ROUND(NVL(price,0),8) price  FROM(SELECT ppd_price*CASE WHEN ve_apvendcode='02.01.028' AND ppd_price>=10 THEN 1.01 WHEN ve_apvendcode='02.01.028' AND ppd_price<10 THEN 1.02 ELSE 1 END price0,ppd_currency,ppd_rate,ppd_price * (1 - NVL(ppd_rate, 0) / (100 + NVL(ppd_rate, 0))) * cr_rate*CASE WHEN ve_apvendcode='02.01.028' AND ppd_price>=10 THEN 1.01 WHEN ve_apvendcode='02.01.028' AND ppd_price<10 THEN 1.02 ELSE 1 END price FROM  purchaseprice,PurchasePriceDetail,Currencys,vendor WHERE  pp_id = ppd_ppid AND ppd_currency = cr_name AND ppd_vendcode=ve_code AND ppd_prodcode =? AND pp_status = '已审核' AND    ppd_status = '有效' AND NVL(pp_kind, ' ') = '采购' AND NVL(ppd_price, 0) > 0 ORDER BY pp_indate DESC) WHERE ROWNUM=1",
			"SELECT ROUND(NVL(price0,0),8) doubleprice,pu_currency currency, pd_rate rate,ROUND(NVL(price,0),8) price  FROM(SELECT pd_price*CASE WHEN ve_apvendcode='02.01.028' AND pd_price>=10 THEN 1.01 WHEN ve_apvendcode='02.01.028' AND pd_price<10 THEN 1.02 ELSE 1 END price0,pu_currency,pd_rate,pd_price * (1 - pd_rate / (100 + pd_rate)) * cr_rate*CASE WHEN ve_apvendcode='02.01.028' AND pd_price>=10 THEN 1.01 WHEN ve_apvendcode='02.01.028' AND pd_price<10 THEN 1.02 ELSE 1 END price FROM purchase,purchasedetail ,currencys,vendor WHERE  pu_code = pd_code AND    pu_currency = cr_name AND pu_vendcode=ve_code AND    pd_prodcode = ? AND (pu_status = '已审核' OR pu_status = '已提交' OR pu_status = '部分收货' OR pu_status = '已收货' OR pu_status = '已结案') AND NVL(pd_price, 0) > 0 ORDER BY pu_date DESC) WHERE ROWNUM=1",
			"SELECT ROUND(NVL(price0,0),8) doubleprice,pi_currency currency, pd_taxrate rate,ROUND(NVL(price,0),8) price  FROM(SELECT pd_orderprice*CASE WHEN ve_apvendcode='02.01.028' AND pd_orderprice>=10 THEN 1.01 WHEN ve_apvendcode='02.01.028' AND pd_orderprice<10 THEN 1.02 ELSE 1 END price0,pi_currency,pd_taxrate,pd_orderprice * (1 - pd_taxrate / (100 + pd_taxrate)) * cr_rate*CASE WHEN ve_apvendcode='02.01.028' AND pd_orderprice>=10 THEN 1.01 WHEN ve_apvendcode='02.01.028' AND pd_orderprice<10 THEN 1.02 ELSE 1 END price FROM prodinout,prodiodetail,currencys,vendor WHERE    pi_inoutno = pd_inoutno AND pi_class = pd_piclass AND pi_currency = cr_name AND pi_cardcode=ve_code AND pd_prodcode  = ? AND pi_statuscode = 'POSTED' AND pi_class='采购验收单' AND NVL(pd_price, 0) > 0 ORDER BY pi_date DESC) WHERE ROWNUM=1" };

	private void getEvaluationDetail(List<Map<Object, Object>> gstore) {
		for (Map<Object, Object> s : gstore) {
			Double qty = (double) 0;
			if (s.get("evd_qty") != null && !"".equals(s.get("evd_qty").toString())) {
				qty = Double.parseDouble(s.get("evd_qty").toString());
			}
			//evd_doubleprice原币单价 
			if (!StringUtil.hasText(s.get("evd_doubleprice")) || "0".equals(s.get("evd_doubleprice"))) {
				SqlRowList rs = null;
				for (int i = 0; i < 3; i++) {
					rs = baseDao.queryForRowSet(GETCURRENCY[i], s.get("evd_prodcode"));
					if (rs.hasNext()) {
						while (rs.next()) {
							System.out.println(rs.getGeneralDouble("doubleprice"));
							s.put("evd_currency", rs.getString("currency"));
							s.put("evd_rate", rs.getGeneralFloat("rate"));
							s.put("evd_doubleprice", rs.getGeneralDouble("doubleprice"));
							s.put("evd_price", rs.getGeneralDouble("price"));
							s.put("evd_amount", NumberUtil.formatDouble(rs.getGeneralDouble("price") * qty, 5));
						}
						break;
					}
				}
			}
			Object[] prod = baseDao.getFieldsDataByCondition("product", "pr_detail,pr_spec,pr_unit", "pr_code='" + s.get("evd_prodcode")
					+ "'");
			if (prod != null) {
				if (s.get("evd_prodname") == null || "".equals(s.get("evd_prodname").toString())) {
					s.put("evd_prodname", prod[0]);
				}
				if (s.get("evd_prodspec") == null || "".equals(s.get("evd_prodspec").toString())) {
					s.put("evd_prodspec", prod[1]);
				}
				if (s.get("evd_produnit") == null || "".equals(s.get("evd_produnit").toString())) {
					s.put("evd_produnit", prod[2]);
				}
			}
		}
	}

	@Override
	public void clearBomOffer(int ev_id) {
		// 删除EvaluationDetail
		try {
			baseDao.deleteById("EvaluationDetail", "evd_evid", ev_id);
			baseDao.execute("update Evaluation set ev_materialcost=0 where ev_id=" + ev_id);
			baseDao.execute("update Evaluation set ev_kfyjfee = round((nvl(EV_DEVELOPERFEES,0)+nvl(ev_yjqty,0)*(ev_materialcost+ev_makecost))/ev_yearqty,2) where ev_id="
					+ ev_id + " and nvl(ev_yearqty,0)<>0");
			// 单位成本（未税）
			baseDao.execute("update Evaluation set ev_unitcost=round(((nvl(ev_materialcost,0)+nvl(ev_makecost,0))*(1+nvl(ev_scrapratio,0))+nvl(ev_kfyjfee,0)+nvl(ev_permitfee,0)+nvl(ev_mouldfee,0))*(1+nvl(ev_duringfee,0))+nvl(ev_logisticsfee,0)+nvl(ev_othercost,0),2) where ev_id="
					+ ev_id);
			Object ev_rate = baseDao.getFieldDataByCondition("Evaluation left join Currencys  on ev_currency=cr_name", "cr_taxrate", "ev_id="+ev_id);
			double rate = ev_rate == null ? 0.17 : Double.parseDouble(ev_rate.toString())/100;
			// 单位成本（含税）L ev_taxunitcost：L=K*(1+17%)
			baseDao.execute("update Evaluation set ev_taxunitcost=round(NVL(ev_unitcost,0)*(1+"+rate+"),2) where ev_id=" + ev_id);
			// 总成本
			baseDao.execute("update Evaluation set ev_cost=round((nvl(ev_materialcost,0)+nvl(ev_makecost,0))*(1+nvl(ev_scrapratio,0))+nvl(ev_kfyjfee,0)+nvl(ev_permitfee,0)+nvl(ev_mouldfee,0)+nvl(ev_logisticsfee,0)+nvl(ev_othercost,0),2) where ev_id="
					+ ev_id);
			// 毛利率=[建议售价(未税)*报价币别汇率-总成本]/[建议售价(未税)*报价币别汇率]
			baseDao.execute("update Evaluation set ev_grossprofitrate=round((ev_unitcost*NVL(ev_rate,0)-nvl(ev_cost,0))/(NVL(ev_rate,0)*ev_unitcost),2) where ev_id=" + ev_id
					+ " and nvl(ev_unitcost,0)<>0 and NVL(ev_rate,0)<>0");
		} catch (Exception e) {
			BaseUtil.showError("清除报价材料明细失败，错误：" + e.getMessage());
		}

	}

	@Override
	public void deleteEvaluationDetail(String evd_id, String caller) {
		SqlRowList rs = baseDao.queryForRowSet(
				"select evd_evid,evd_amount,evd_detno from Evaluation,EvaluationDetail where ev_id=evd_evid and evd_id=?", evd_id);
		if (rs.next()) {
			int ev_id = rs.getGeneralInt("evd_evid");
			baseDao.execute("update Evaluation set ev_materialcost=round(nvl(ev_materialcost,0)-" + rs.getGeneralDouble("evd_amount")
					+ ",2) where ev_id=" + ev_id);
			baseDao.execute("update Evaluation set ev_kfyjfee = round((nvl(EV_DEVELOPERFEES,0)+nvl(ev_yjqty,0)*(ev_materialcost+ev_makecost))/ev_yearqty,2) where ev_id="
					+ ev_id + " and nvl(ev_yearqty,0)<>0");
			// 单位成本（不含税）K ev_unitcost：K=[(A+B)*(1+G)+C+认证费许可费+F]+I+J
			baseDao.execute("update Evaluation set ev_unitcost=round(((nvl(ev_materialcost,0)+nvl(ev_makecost,0))*(1+nvl(ev_scrapratio,0))+nvl(ev_kfyjfee,0)+nvl(ev_permitfee,0)+nvl(ev_mouldfee,0))*(1+nvl(ev_duringfee,0))+nvl(ev_logisticsfee,0)+nvl(ev_othercost,0),2) where ev_id="
					+ ev_id);
			Object ev_rate = baseDao.getFieldDataByCondition("Evaluation left join Currencys  on ev_currency=cr_name", "cr_taxrate", "ev_id="+ev_id);
			double rate = ev_rate == null ? 0.17 : Double.parseDouble(ev_rate.toString());
			// 单位成本（含税）L ev_taxunitcost：L=K*(1+17%)
			baseDao.execute("update Evaluation set ev_taxunitcost=round(NVL(ev_unitcost,0)*(1+"+rate+"),2) where ev_id=" + ev_id);
			// 总成本
			baseDao.execute("update Evaluation set ev_cost=round((nvl(ev_materialcost,0)+nvl(ev_makecost,0))*(1+nvl(ev_scrapratio,0))+nvl(ev_kfyjfee,0)+nvl(ev_permitfee,0)+nvl(ev_mouldfee,0)+nvl(ev_logisticsfee,0)+nvl(ev_othercost,0),2) where ev_id="
					+ ev_id);
			// 毛利率=[建议售价(未税)*报价币别汇率-总成本]/[建议售价(未税)*报价币别汇率]
			baseDao.execute("update Evaluation set ev_grossprofitrate=round((ev_unitcost*NVL(ev_rate,0)-nvl(ev_cost,0))/(NVL(ev_rate,0)*ev_unitcost),2) where ev_id=" + ev_id
					+ " and nvl(ev_unitcost,0)<>0 and NVL(ev_rate,0)<>0");
			baseDao.deleteByCondition("EvaluationDetail", "evd_id=" + evd_id);
			// 记录操作
			baseDao.logger.others("删除明细，行：" + rs.getGeneralInt("evd_detno") + ",金额：" + rs.getGeneralDouble("evd_amount"), "删除成功", caller,
					"ev_id", ev_id);
		}
	}
}
