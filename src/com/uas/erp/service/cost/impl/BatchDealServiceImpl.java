package com.uas.erp.service.cost.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ProdInOutDao;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.cost.BatchDealService;

@Service("costBatchDealService")
public class BatchDealServiceImpl implements BatchDealService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private ProdInOutDao prodInOutDao;
	@Autowired
	private VoucherDao voucherDao;

	/**
	 * 核算作业
	 */
	@Override
	public void accountProdio(String language, Employee employee, String caller, String data, String condition, String condParams) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String piclass = null;
		int type = 0;
		if (maps.size() > 0) {
			piclass = maps.get(0).get("pd_piclass").toString();
			type = Integer.parseInt(maps.get(0).get("pi_type").toString()); // 年月
		} else if (StringUtil.hasText(condParams)) {
			Map<Object, Object> params = BaseUtil.parseFormStoreToMap(condParams);
			piclass = String.valueOf(params.get("pi_class"));
			type = Integer.parseInt(params.get("pi_type").toString());
		} else
			return;
		int flowid = baseDao.getSeqId("ACCOUNTPRODIO_SEQ");
		if (!"拨入单".equals(piclass) && !"销售拨入单".equals(piclass)) {
			if (maps.size() > 0) {
				Object[] idObj = CollectionUtil.pluck(maps, "pd_id");
				StringBuffer cond = new StringBuffer();
				if (idObj.length > 1000) {
					for (int i = 0, j = idObj.length, k = j / 1000; i < k; i++) {
						int l = (i + 1) * 1000 > j ? (j - i * 1000) : 1000;
						Object[] obj = new Object[l];
						System.arraycopy(idObj, i * 1000, obj, 0, l);
						if (cond.length() > 0)
							cond.append(" or ");
						cond.append("pd_id in (").append(BaseUtil.parseArray2Str(obj, ",")).append(")");
					}
				} else {
					cond.append("pd_id in (").append(BaseUtil.parseArray2Str(idObj, ",")).append(")");
				}
				if (cond.length() > 0) {
					baseDao.updateByCondition("ProdIODetail", "pd_flowid=" + flowid, cond.toString());
				}
			} else if (StringUtil.hasText(condition)) {
				baseDao.updateByCondition("ProdIODetail", "pd_flowid=" + flowid,
						"pd_id in (select pd_id from prodinout left join prodiodetail on pi_id=pd_piid where " + condition + ")");
			}
		}
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(distinct '单号：'||pd_inoutno||'类型：'||pd_piclass) from ProdIODetail left join ProdInOut on pd_piid=pi_id where pd_flowid=? and nvl(pi_vouchercode,' ')<>' '",
						String.class, flowid);
		if (dets != null) {
			BaseUtil.showError("单据已制作凭证!" + dets);
		}
		String res = baseDao.callProcedure("SP_CACPUTINCOST", new Object[] { type, flowid, employee.getEm_name(), piclass });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
	}

	/**
	 * 取价作业
	 */
	public void getPrice(String language, Employee employee, String caller, String data, String condition, String condParams) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String piclass = null;// 单据类型
		String priceKind = null;// 取价原则
		Integer yearmonth = 0;
		if (maps.size() > 0) {
			yearmonth = Integer.parseInt(maps.get(0).get("pi_type").toString());
			priceKind = String.valueOf(maps.get(0).get("pi_remark"));
		} else if (StringUtil.hasText(condParams)) {
			Map<Object, Object> params = BaseUtil.parseFormStoreToMap(condParams);
			yearmonth = Integer.parseInt(params.get("pi_type").toString());
			priceKind = String.valueOf(params.get("pi_remark"));
		} else
			return;
		int flowid = baseDao.getSeqId("ACCOUNTPRODIO_SEQ");
		if (maps.size() > 0) {
			Object[] idObj = CollectionUtil.pluck(maps, "pd_id");
			StringBuffer cond = new StringBuffer();
			if (idObj.length > 1000) {
				for (int i = 0, j = idObj.length, k = j / 1000; i < k; i++) {
					int l = (i + 1) * 1000 > j ? (j - i * 1000) : 1000;
					Object[] obj = new Object[l];
					System.arraycopy(idObj, i * 1000, obj, 0, l);
					if (cond.length() > 0)
						cond.append(" or ");
					cond.append("pd_id in (").append(BaseUtil.parseArray2Str(obj, ",")).append(")");
				}
			} else {
				cond.append("pd_id in (").append(BaseUtil.parseArray2Str(idObj, ",")).append(")");
			}
			if (cond.length() > 0) {
				baseDao.updateByCondition("ProdIODetail", "pd_flowid=" + flowid, cond.toString());
			}
		} else if (StringUtil.hasText(condition)) {
			baseDao.updateByCondition("ProdIODetail", "pd_flowid=" + flowid,
					"pd_id in (select pd_id from prodinout left join prodiodetail on pi_id=pd_piid where " + condition + ")");
		}
		Map<String, Object> period = voucherDao.getPeriodsDate("MONTH-F", yearmonth);
		Object startDate = period.get("pd_startdate");
		Object endDate = period.get("pd_enddate");
		StringBuffer sb = new StringBuffer();
		if (maps.size() > 0) {
			String res = baseDao.callProcedure("SP_GETPRICEBYPRINCIPLE", new Object[] { caller, priceKind, yearmonth, flowid });
			if (res != null && !res.trim().equals("")) {
				BaseUtil.showError(res);
			}
		} else {
			sb = getPriceByCondition(yearmonth, priceKind, startDate, endDate, flowid, condition);
			if (sb.length() > 0) {
				BaseUtil.showError(sb.toString());
			}
		}
	}

	private StringBuffer getPriceByCondition(int yearmonth, String priceKind, Object startDate, Object endDate, int flowid, String condition) {
		SqlRowList rs = baseDao
				.queryForRowSet("select pd_id,pd_piclass,pd_ordercode,pd_orderdetno,pd_prodcode,pd_whcode,pi_rate from prodinout left join prodiodetail on pi_id=pd_piid where "
						+ condition + " order by pi_class");
		String piclass = null;
		String ordercode = null;
		Object orderdetno = 0;
		int pdid = 0;
		Double pirate = 0.0;
		String prodcode = null;
		String whcode = null;
		String accountKey = null;
		Set<String> accounted = new HashSet<String>();
		StringBuffer sb = new StringBuffer();
		while (rs.next()) {
			pdid = rs.getInt("pd_id");
			piclass = rs.getString("pd_piclass");
			ordercode = rs.getString("pd_ordercode");
			prodcode = rs.getString("pd_prodcode");
			whcode = rs.getString("pd_whcode");
			orderdetno = rs.getGeneralInt("pd_orderdetno");
			pirate = rs.getGeneralDouble("pi_rate");
			accountKey = piclass + "," + priceKind;
			if (!accounted.contains(accountKey)) {
				prodInOutDao.account(piclass, priceKind, startDate, endDate, flowid);
				accounted.add(accountKey);
			}
			String res = prodInOutDao.getPrice(piclass, priceKind, prodcode, startDate, endDate, ordercode, orderdetno, pirate, whcode,
					yearmonth, pdid, flowid);
			if (res != null) {
				sb.append(" <br/> ").append(res);
			}
		}
		return sb;
	}

	/**
	 * 单价重置
	 */
	@Override
	public void resPrice(String language, Employee employee, String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String ids = BaseUtil.parseArray2Str(CollectionUtil.pluck(maps, "pd_id"), ",");
		if (ids != null) {
			baseDao.updateByCondition("ProdIODetail", "pd_avprice=0", "pd_id in (" + ids + ")");
		}
	}

	@Override
	public void batchSave(String language, Employee employee, String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> s : maps) {
			Object pdid = s.get("pd_id");
			Object avprice = s.get("pd_avprice");
			Object price = s.get("pd_price");
			if (price != null && price.equals(avprice)) {
				baseDao.updateByCondition("ProdIODetail", "pd_accountstatuscode='ACCOUNTED',pd_accountstatus='已核算',pd_avprice=" + avprice,
						"pd_id=" + pdid);
			} else {
				baseDao.updateByCondition("ProdIODetail", "pd_pricekind='手工核算',pd_avprice=" + avprice, "pd_id=" + pdid);
			}
		}
	}

	@Override
	public void consistency(Integer param, String language, Employee employee) {
		String sql = "select piclass1,picode1,no1,price1,qty1,piclass2,picode2,no2,price2,qty2 from (select pi_class piclass1 ,pd_inoutno picode1,pd_pdno no1,nvl(pd_price,0) price1,nvl(pd_outqty,0)+nvl(pd_inqty,0) qty1,pi_relativeplace code1 FROM prodinout,prodiodetail where pi_id=pd_piid and to_char(pi_date,'yyyymm')="
				+ param
				+ " and pi_statuscode='POSTED' and pi_class='拨出单') full join (select pi_class piclass2 ,pd_inoutno picode2,pd_pdno no2,nvl(pd_price,0) price2,nvl(pd_outqty,0)+nvl(pd_inqty,0) qty2,pi_relativeplace code2 FROM prodinout,prodiodetail where pi_id=pd_piid and to_char(pi_date,'yyyymm')="
				+ param
				+ " and pi_statuscode='POSTED' and pi_class='拨入单') on code2=picode1 and no1=no2 where (nvl(price1,0)<>nvl(price2,0) or nvl(qty1,0)<>nvl(qty2,0))";
		SqlRowList rs = baseDao.queryForRowSet(sql);
		if (rs.hasNext()) {
			StringBuffer sb = new StringBuffer();
			sb.append("拨出拨入单成本价、数量不一致！<hr/>");
			while (rs.next()) {
				String outno = rs.getGeneralString("picode1");
				String inno = rs.getGeneralString("picode2");
				int outdetno = rs.getGeneralInt("no1");
				int intdetno = rs.getGeneralInt("no2");
				double outprice = rs.getGeneralDouble("price1");
				double inprice = rs.getGeneralDouble("price2");
				double outqty = rs.getGeneralDouble("qty1");
				double inqty = rs.getGeneralDouble("qty2");
				if (StringUtil.hasText(outno) && outdetno != 0) {
					if (StringUtil.hasText(inno) && intdetno != 0) {
						if (outprice != inprice) {
							sb.append("拨出单[" + outno + "]行号[" + outdetno + "]成本单价[" + outprice + "]，拨入单[" + inno + "]行号[" + intdetno
									+ "]成本单价[" + inprice + "]<hr/>");
						}
						if (outqty != inqty) {
							sb.append("拨出单[" + outno + "]行号[" + outdetno + "]数量[" + outqty + "]，拨入单[" + inno + "]行号[" + intdetno + "]数量["
									+ inqty + "]<hr/>");
						}
					} else {
						sb.append("拨出单[" + outno + "]行号[" + outdetno + "]无对应的拨入单<hr/>");
					}
				} else {
					if (StringUtil.hasText(inno) && intdetno != 0) {
						sb.append("拨入单[" + inno + "]行号[" + intdetno + "]无对应的拨出单<hr/>");
					}
				}
			}
			if (sb.length() > 0) {
				BaseUtil.showError(sb.toString());
			}
		} else {
			BaseUtil.showError("拨出拨入数据检测一致!");
		}
	}

	@Override
	public void consistencySale(Integer param, String language, Employee employee) {
		String sql = "select piclass1,picode1,no1,price1,qty1,piclass2,picode2,no2,price2,qty2 from (select pi_class piclass1 ,pd_inoutno picode1,pd_pdno no1,nvl(pd_price,0) price1,nvl(pd_outqty,0)+nvl(pd_inqty,0) qty1,pi_relativeplace code1 FROM prodinout,prodiodetail where pi_id=pd_piid and to_char(pi_date,'yyyymm')="
				+ param
				+ " and pi_statuscode='POSTED' and pi_class='销售拨出单') full join (select pi_class piclass2 ,pd_inoutno picode2,pd_pdno no2,nvl(pd_price,0) price2,nvl(pd_outqty,0)+nvl(pd_inqty,0) qty2,pi_relativeplace code2 FROM prodinout,prodiodetail where pi_id=pd_piid and to_char(pi_date,'yyyymm')="
				+ param
				+ " and pi_statuscode='POSTED' and pi_class='销售拨入单') on code2=picode1 and no1=no2 where (nvl(price1,0)<>nvl(price2,0) or nvl(qty1,0)<>nvl(qty2,0))";
		SqlRowList rs = baseDao.queryForRowSet(sql);
		if (rs.hasNext()) {
			StringBuffer sb = new StringBuffer();
			sb.append("销售拨出拨入单成本价、数量不一致！<hr/>");
			while (rs.next()) {
				String outno = rs.getGeneralString("picode1");
				String inno = rs.getGeneralString("picode2");
				int outdetno = rs.getGeneralInt("no1");
				int intdetno = rs.getGeneralInt("no2");
				double outprice = rs.getGeneralDouble("price1");
				double inprice = rs.getGeneralDouble("price2");
				double outqty = rs.getGeneralDouble("qty1");
				double inqty = rs.getGeneralDouble("qty2");
				if (StringUtil.hasText(outno) && outdetno != 0) {
					if (StringUtil.hasText(inno) && intdetno != 0) {
						if (outprice != inprice) {
							sb.append("销售拨出单[" + outno + "]行号[" + outdetno + "]成本单价[" + outprice + "]，销售拨入单[" + inno + "]行号[" + intdetno
									+ "]成本单价[" + inprice + "]<hr/>");
						}
						if (outqty != inqty) {
							sb.append("销售拨出单[" + outno + "]行号[" + outdetno + "]数量[" + outqty + "]，销售拨入单[" + inno + "]行号[" + intdetno
									+ "]数量[" + inqty + "]<hr/>");
						}
					} else {
						sb.append("销售拨出单[" + outno + "]行号[" + outdetno + "]无对应的销售拨入单<hr/>");
					}
				} else {
					if (StringUtil.hasText(inno) && intdetno != 0) {
						sb.append("销售拨入单[" + inno + "]行号[" + intdetno + "]无对应的销售拨出单<hr/>");
					}
				}
			}
			if (sb.length() > 0) {
				BaseUtil.showError(sb.toString());
			}
		} else {
			BaseUtil.showError("销售拨出拨入数据检测一致!");
		}
	}

	@Override
	public void shareFee(String language, Employee employee, String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Double perPrice = 0.0;
		if (maps.size() > 0) {
			Double gstotal = Double.parseDouble(maps.get(0).get("pi_total").toString());
			if (maps.get(0).get("pi_idcode") == null) {
				BaseUtil.showError("请填写关税单号！");
			}
			String gscode = maps.get(0).get("pi_idcode").toString();
			String ids = BaseUtil.parseArray2Str(CollectionUtil.pluck(maps, "pd_id"), ",");
			Double totalQty = Double.parseDouble(baseDao.getFieldDataByCondition("ProdIODetail", "sum(nvl(pd_inqty,0))",
					"pd_id in (" + ids + ")").toString());
			if (totalQty > 0) {
				perPrice = NumberUtil.formatDouble(gstotal / totalQty, 8); // 根据数量得出单位数量的关税金额
				if (perPrice >= 0) {
					baseDao.execute("update prodiodetail set pd_taxamount=round(pd_inqty*" + perPrice + ",4) where pd_id in (" + ids + ")");
					baseDao.execute("update prodiodetail set pd_taxprice=" + perPrice + ",pd_nxlh='" + gscode + "' where pd_id in (" + ids
							+ ")");
					baseDao.execute("update prodiodetail set pd_avprice=round(nvl(pd_avprice,0)+nvl(pd_taxprice,0),8) where pd_id in ("
							+ ids + ")");
				}
			}
		}
	}

	/**
	 * 成本表实际工时维护
	 */
	@Override
	public void vastSaveCostDetail(String language, Employee employee, String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Object cd_standardtime = null;
		Object cd_costdiffer = null;
		for (Map<Object, Object> map : maps) {
			// 成本表实际工时维护
			if (map.containsKey("cd_standardtime")) {
				cd_standardtime = map.get("cd_standardtime");
				baseDao.updateByCondition("CostDetail", "cd_standardtime=" + cd_standardtime, "cd_id=" + map.get("cd_id"));
			}
			// 成本表成本调整金额
			if (map.containsKey("cd_costdiffer")) {
				cd_costdiffer = map.get("cd_costdiffer");
				baseDao.updateByCondition("CostDetail", "cd_costdiffer=" + cd_costdiffer, "cd_id=" + map.get("cd_id"));
			}
		}
	}

	/**
	 * 成本表：无值转出生成凭证
	 */
	@Override
	public void vastNowhVoucherCredit(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		int month = DateUtil.getYearmonth();
		if (maps.size() > 0 && StringUtil.hasText(maps.get(0).get("cd_yearmonth"))) {
			month = Integer.parseInt(maps.get(0).get("cd_yearmonth").toString());
		}
		SqlRowList rs = baseDao.queryForRowSet("select vo_code from voucher where vo_explanation like '%无值完工转出成本的凭证' and vo_yearmonth=?",
				month);
		if (rs.next()) {
			BaseUtil.showError("已经存在无值完工转出成本的凭证" + rs.getObject("vo_code") + "，不能重复生成!");
		}
		String catcode = null;
		// 生产成本直接材料科目
		String directMaterialsCatecode = baseDao.getDBSetting("CheckAccount!COST", "DirectMaterialsCatecode");
		// 委托加工物资科目
		String processingCatecode = baseDao.getDBSetting("CheckAccount!COST", "ProcessingCatecode");
		if (directMaterialsCatecode == null)
			BaseUtil.showError("生产成本直接材料科目未设置.");
		if (processingCatecode == null)
			BaseUtil.showError("委托加工物资科目未设置.");
		rs = baseDao
				.queryForRowSet(
						"select cd_yearmonth,cd_makecode, ma_tasktype, SUM(nvl(cd_turnoutamountnocost,0)) amount from CostDetail,make where cd_makecode=ma_code and cd_yearmonth=? and nvl(cd_turnoutamountnocost,0)<>0 GROUP BY cd_yearmonth,cd_makecode,ma_tasktype",
						month);
		if (rs.hasNext()) {
			int voId = baseDao.getSeqId("voucher_seq");
			String voCode = baseDao.sGetMaxNumber("Voucher", 2);
			Map<String, Object> periods = voucherDao.getPeriodsDate("MONTH-A", month);
			String lead = StringUtil.valueOf(periods.get("vo_lead"));
			String vo_number = voucherDao.getVoucherNumber(String.valueOf(month), lead, null);
			baseDao.execute("INSERT INTO Voucher(vo_id,vo_code,vo_yearmonth,vo_lead,vo_number,vo_emid,vo_recordman,vo_status,"
					+ "vo_statuscode,vo_recorddate,vo_explanation,vo_currencytype,vo_printstatus,vo_date)" + " VALUES (" + voId + ",'"
					+ voCode + "'," + month + ",'" + (lead == null ? "" : lead) + "'," + vo_number + ","
					+ SystemSession.getUser().getEm_id() + ",'" + SystemSession.getUser().getEm_name() + "','"
					+ BaseUtil.getLocalMessage("ENTERING") + "','ENTERING',sysdate,'无值完工转出成本的凭证',0,'未打印'," + periods.get("pd_enddate")
					+ ")");
			int count = 1;
			while (rs.next()) {
				if ("MAKE".equals(rs.getGeneralString("ma_tasktype"))) {
					catcode = directMaterialsCatecode;
				} else if ("OS".equals(rs.getGeneralString("ma_tasktype"))) {
					catcode = processingCatecode;
				}
				baseDao.execute("INSERT INTO VoucherDetail(vd_id,vd_void,vd_detno,vd_catecode,vd_explanation,vd_debit,"
						+ "vd_credit,vd_currency,vd_yearmonth) VALUES (VoucherDetail_seq.nextval," + voId + "," + (count++) + ",'"
						+ catcode + "','工单" + rs.getObject("cd_makecode") + "无值完工转出成本的凭证',0," + rs.getGeneralDouble("amount") + ",'RMB',"
						+ month + ")");
			}
			baseDao.execute("INSERT INTO VoucherDetail(vd_id,vd_void,vd_detno,vd_catecode,vd_explanation,vd_debit,"
					+ "vd_credit,vd_currency,vd_yearmonth) VALUES (VoucherDetail_seq.nextval," + voId + "," + (count + 1) + ",null,null,"
					+ baseDao.getSummaryByField("VoucherDetail", "vd_credit", "vd_void=" + voId) + ",0,'RMB'," + month + ")");
			voucherDao.validVoucher(voId);
			String error = baseDao.getJdbcTemplate().queryForObject("select vo_errstring from Voucher Where vo_id=?", String.class, voId);
			String codeStr = "<a href=\"javascript:openUrl('jsps/fa/ars/voucher.jsp?formCondition=vo_idIS" + voId
					+ "&gridCondition=vd_voidIS" + voId + "')\">" + voCode + "</a><br>";
			if ((error != null && error.trim().length() > 0)) {
				BaseUtil.showError("产生的凭证有问题，请打开凭证查看!<br>" + codeStr);
			} else {
				BaseUtil.showError("已成功产生无值完工转出成本的凭证!<br>" + codeStr);
			}
		} else {
			BaseUtil.showError("没有需要处理的数据");
		}
	}

	/**
	 * 成本表：调整差异生成凭证
	 */
	@Override
	public void vastDifferVoucherCredit(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		int month = DateUtil.getYearmonth();
		if (maps.size() > 0 && StringUtil.hasText(maps.get(0).get("cd_yearmonth"))) {
			month = Integer.parseInt(maps.get(0).get("cd_yearmonth").toString());
		}
		SqlRowList rs = baseDao.queryForRowSet("select vo_code from voucher where vo_explanation like '%成本差异调整的凭证' and vo_yearmonth=?",
				month);
		if (rs.next()) {
			BaseUtil.showError("已经存在成本差异调整的凭证" + rs.getObject("vo_code") + "，不能重复生成!");
		}
		String catcode = null;
		// 生产成本直接材料科目
		String directMaterialsCatecode = baseDao.getDBSetting("CheckAccount!COST", "DirectMaterialsCatecode");
		// 委托加工物资科目
		String processingCatecode = baseDao.getDBSetting("CheckAccount!COST", "ProcessingCatecode");
		// 材料成本差异科目
		String materialsCatecode = baseDao.getDBSetting("MakeFeeClose", "materialsCatecode");
		if (directMaterialsCatecode == null)
			BaseUtil.showError("生产成本直接材料科目未设置.");
		if (processingCatecode == null)
			BaseUtil.showError("委托加工物资科目未设置.");
		if (materialsCatecode == null)
			BaseUtil.showError("材料成本差异科目未设置.");
		rs = baseDao
				.queryForRowSet(
						"select cd_yearmonth,cd_makecode, ma_tasktype, SUM(nvl(cd_costdiffer,0)) amount from CostDetail,make where cd_makecode=ma_code and cd_yearmonth=? and nvl(cd_costdiffer,0)<>0 GROUP BY cd_yearmonth,cd_makecode,ma_tasktype",
						month);
		if (rs.hasNext()) {
			int voId = baseDao.getSeqId("voucher_seq");
			String voCode = baseDao.sGetMaxNumber("Voucher", 2);
			Map<String, Object> periods = voucherDao.getPeriodsDate("MONTH-A", month);
			String lead = StringUtil.valueOf(periods.get("vo_lead"));
			String vo_number = voucherDao.getVoucherNumber(String.valueOf(month), lead, null);
			baseDao.execute("INSERT INTO Voucher(vo_id,vo_code,vo_yearmonth,vo_lead,vo_number,vo_emid,vo_recordman,vo_status,"
					+ "vo_statuscode,vo_recorddate,vo_explanation,vo_currencytype,vo_printstatus,vo_date)" + " VALUES (" + voId + ",'"
					+ voCode + "'," + month + ",'" + (lead == null ? "" : lead) + "'," + vo_number + ","
					+ SystemSession.getUser().getEm_id() + ",'" + SystemSession.getUser().getEm_name() + "','"
					+ BaseUtil.getLocalMessage("ENTERING") + "','ENTERING',sysdate,'成本差异调整的凭证',0,'未打印'," + periods.get("pd_enddate") + ")");
			int count = 1;
			while (rs.next()) {
				if ("MAKE".equals(rs.getGeneralString("ma_tasktype"))) {
					catcode = directMaterialsCatecode;
				} else if ("OS".equals(rs.getGeneralString("ma_tasktype"))) {
					catcode = processingCatecode;
				}
				baseDao.execute("INSERT INTO VoucherDetail(vd_id,vd_void,vd_detno,vd_catecode,vd_explanation,vd_debit,"
						+ "vd_credit,vd_currency,vd_yearmonth) VALUES (VoucherDetail_seq.nextval," + voId + "," + (count++) + ",'"
						+ catcode + "','工单" + rs.getObject("cd_makecode") + "成本差异调整的凭证',0," + rs.getGeneralDouble("amount") + ",'RMB',"
						+ month + ")");
			}
			baseDao.execute("INSERT INTO VoucherDetail(vd_id,vd_void,vd_detno,vd_catecode,vd_explanation,vd_debit,"
					+ "vd_credit,vd_currency,vd_yearmonth) VALUES (VoucherDetail_seq.nextval," + voId + "," + (count + 1) + ",'"
					+ materialsCatecode + "',null,0,(-1)*(" + baseDao.getSummaryByField("VoucherDetail", "vd_credit", "vd_void=" + voId)
					+ "),'RMB'," + month + ")");
			voucherDao.validVoucher(voId);
			String error = baseDao.getJdbcTemplate().queryForObject("select vo_errstring from Voucher Where vo_id=?", String.class, voId);
			String codeStr = "<a href=\"javascript:openUrl('jsps/fa/ars/voucher.jsp?formCondition=vo_idIS" + voId
					+ "&gridCondition=vd_voidIS" + voId + "')\">" + voCode + "</a><br>";
			if ((error != null && error.trim().length() > 0)) {
				BaseUtil.showError("产生的凭证有问题，请打开凭证查看!<br>" + codeStr);
			} else {
				BaseUtil.showError("已成功产生成本差异调整的凭证!<br>" + codeStr);
			}
		} else {
			BaseUtil.showError("没有需要处理的数据");
		}
	}

	/**
	 * 月结表：调整金额维护
	 */
	@Override
	public void vastSaveCostDetailMaterial(String language, Employee employee, String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Object cdm_costdiffer = null;
		Object cdm_id = 0;
		for (Map<Object, Object> map : maps) {
			cdm_id = map.get("cdm_id");
			SqlRowList rs = baseDao.queryForRowSet("select cdm_mmcode,cdm_mmdetno,cdm_cdid from CostDetailMaterial where cdm_id=?", cdm_id);
			if (rs.next()) {
				// 成本表成本调整金额
				if (map.containsKey("cdm_costdiffer")) {
					cdm_costdiffer = map.get("cdm_costdiffer") == null ? 0 : map.get("cdm_costdiffer");
					baseDao.updateByCondition("CostDetailMaterial", "cdm_costdiffer=" + cdm_costdiffer, "cdm_id=" + cdm_id);
					baseDao.updateByCondition(
							"CostDetailMaterial",
							"cdm_endamount =ROUND(NVL(cdm_beginamount, 0) + NVL(cdm_nowgetamount, 0) - NVL(cdm_nowbackamount, 0) + NVL(cdm_nowaddamount, 0) - NVL(cdm_nowturnoutamount,0) - NVL(cdm_nowscrapamount, 0)+NVL(cdm_costdiffer, 0),2) ",
							"cdm_id=" + cdm_id);
					baseDao.updateByCondition("CostDetail",
							"cd_costdiffer =nvl((select sum(nvl(cdm_costdiffer,0)) from CostDetailMaterial where cd_id=cdm_cdid),0)",
							"cd_id=" + rs.getGeneralInt("cdm_cdid"));
					baseDao.updateByCondition("CostDetail",
							"cd_endcostamount =nvl((select sum(nvl(cdm_endamount,0)) from CostDetailMaterial where cd_id=cdm_cdid),0)",
							"cd_id=" + rs.getGeneralInt("cdm_cdid"));
					baseDao.logger.others("月结表金额调整", "工单号[" + rs.getObject("cdm_mmcode") + "]用料序号[" + rs.getObject("cdm_mmdetno")
							+ "]调整金额[" + cdm_costdiffer + "]", "Make!OnCost!Deal", "cdm_id", cdm_id);
				}
			}
		}
	}
}