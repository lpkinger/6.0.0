package com.uas.erp.service.oa.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ContractTypeDao;
import com.uas.erp.dao.common.FeePleaseDao;
import com.uas.erp.model.ContractType;
import com.uas.erp.model.Employee;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.AccountRegisterBankService;
import com.uas.erp.service.oa.FeePleaseService;

@Service
public class FeePleaseServiceImpl implements FeePleaseService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private FeePleaseDao feePleaseDao;
	@Autowired
	private ContractTypeDao contractTypeDao;
	@Autowired
	private AccountRegisterBankService accountRegisterBankService;

	@Override
	public Object saveFeePlease(String formStore, String gridStore, String gridStore2, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		if ("FeePlease!CCSQ!new".equals(caller)) {
			checkTime(store);
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, grid });
		store.put("fp_printstatuscode", "UNPRINT");
		store.put("fp_printstatus", BaseUtil.getLocalMessage("UNPRINT"));
		if ("FeePlease!ZWSQ".equals(caller)) {// 总务申请
			double num = Double.parseDouble(store.get("fp_n3").toString()) + Double.parseDouble(store.get("fp_n4").toString());
			store.put("fp_pleaseamount", num);
			store.put("fp_n5", num);
		}
		if ("FeePlease!HKSQ".equals(caller)) {
			/*
			 * 累计还款总额应小于借款单已转金额 fp_n1 借款申请单已转金额 sum(fb_back)已提交和已审核的费用报销单中还款金额的和
			 * sum(fp_pleaseamount)已提交和已审核的还款申请单还款金额的和 结果fee:待还款金额
			 */
			String sql = "select round((fp_n1- "
					+ "nvl((select sum(FB_BACK) from feeback left join FEEPLEASE on FB_FPID=FP_ID where FP_STATUSCODE in('AUDITED','COMMITED') and FB_JKSQCODE='"
					+ store.get("fp_sourcecode") + "'),0)" + "-nvl((select sum(fp_pleaseamount) from feeplease where fp_sourcecode='"
					+ store.get("fp_sourcecode") + "' and FP_STATUSCODE in ('AUDITED','COMMITED')),0)),2) fee "
					+ "from feeplease where fp_code='" + store.get("fp_sourcecode") + "' and fp_kind='借款申请单'";
			SqlRowList rs = baseDao.queryForRowSet(sql);
			if (rs.next()) {
				double fee = rs.getGeneralDouble(1);
				if (NumberUtil.formatDouble(Double.parseDouble(store.get("fp_pleaseamount").toString()), 2) > fee + 0.01) {
					BaseUtil.showError("累计还款总额应小于待还款金额，待还款金额为:" + fee);
				}
			}
		}
		if ("FeePlease!FYBX".equals(caller)) {// 费用报销
			SqlRowList rs = baseDao
					.queryForRowSet(
							"select FP_SUMWGH,FP_THISHKAMOUNT from FeePlease where fp_id=? and nvl(FP_SUMWGH,0)>0 and nvl(FP_THISHKAMOUNT,0)<nvl(FP_SUMWGH,0)",
							store.get("fp_id"));
			if (rs.next()) {
				BaseUtil.appendError("报销人还有借款未归还，请先冲销借款金额！本次还款总额(本位币)[" + rs.getGeneralDouble("FP_THISHKAMOUNT") + "],总的未归还金额(本位币)["
						+ rs.getGeneralDouble("FP_SUMWGH") + "]");
			}
			/**
			 * fp_n1 借款申请单已转金额 sum(fb_back)已提交和已审核的费用报销单中还款金额的和
			 * sum(fp_pleaseamount)已提交和已审核的还款申请单还款金额的和 结果fee:待还款金额
			 * */
			/*
			 * String sql = "select sum(p.fp_n1- " +
			 * "nvl((select sum(FB_BACK) from feeback left join FEEPLEASE on FB_FPID=FP_ID where FP_STATUSCODE in('AUDITED','COMMITED') and FB_JKSQCODE=p.fp_code),0)"
			 * +
			 * "-nvl((select sum(fp_pleaseamount) from feeplease where fp_sourcecode=p.fp_code and FP_STATUSCODE in ('AUDITED','COMMITED')),0))"
			 * + "from feeplease p where p.fp_pleasemancode='" +
			 * store.get("fp_pleasemancode") +
			 * "' and p.fp_kind='借款申请单' and p.fp_statuscode='AUDITED'";
			 * SqlRowList rs = baseDao.queryForRowSet(sql); if (rs.next()) {
			 * double fee = rs.getGeneralDouble(1, 6); if (fee > 0)
			 * BaseUtil.appendError("该报销人有未还款的借款单！待还款总额为：" + fee); }
			 */
		}
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "FeePlease", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存FeePleaseDETAIL
		for (Map<Object, Object> map : grid) {
			map.put("fpd_id", baseDao.getSeqId("FEEPLEASEDETAIL_SEQ"));
			map.put("fpd_code", store.get("fp_code"));
			map.put("fpd_class", store.get("fp_kind"));
			if ("FeePlease!CCSQ".equals(caller)) {
				if (map.get("fpd_date1") == null || map.get("fpd_date1").equals("")) {
					map.put("fpd_date1", store.get("fp_prestartdate"));
				}
				if (map.get("fpd_date2") == null || map.get("fpd_date2").equals("")) {
					map.put("fpd_date2", store.get("fp_preenddate"));
				}
			}
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "FeePleaseDETAIL");
		baseDao.execute(gridSql);
		// 更新明细表金额字段为2位小数
		// "update FeePleaseDETAIL set fpd_n2=round(nvl(fpd_n2,0),2),fpd_n1=round(nvl(fpd_n1,0),2),fpd_total=round(nvl(fpd_total,0),2) where FPD_FPID="+store.get("fp_id");
		String updatesql = "update FeePleaseDETAIL set fpd_n2=round(nvl(fpd_n2,0),2),fpd_n1=round(nvl(fpd_n1,0),2),fpd_total=round(nvl(fpd_total,0),2) where fpd_fpid='"
				+ store.get("fp_id") + "'";
		baseDao.execute(updatesql);
		if ("FeePlease!CCSQ".equals(caller)) {// 出差申请
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(FPD_DETNO) from FeePleaseDetail where fpd_date1>fpd_date2 and FPD_FPID=?", String.class,
					store.get("fp_id"));
			if (dets != null) {
				BaseUtil.appendError("起始日期必须小于等于截止日期！行号：" + dets);
			}
		}
		if ("FeePlease!CLFBX".equals(caller)) {// 计算明细中的所有费用
			String sql = "update FeePleaseDetail set fpd_n8=fpd_n7 where nvl(fpd_n8,0)=0 and fpd_fpid=" + store.get("fp_id");
			baseDao.execute(sql);
			baseDao.updateByCondition(
					"FeePlease",
					"fp_pleaseamount="
							+ baseDao.getFieldDataByCondition("feepleasedetail", "sum(fpd_n8)", "fpd_fpid=" + store.get("fp_id")), "fp_id="
							+ store.get("fp_id"));
		}
		// 保存还款明细
		if (gridStore2 != null) {
			List<Map<Object, Object>> grid2 = BaseUtil.parseGridStoreToMaps(gridStore2);
			for (Map<Object, Object> map : grid2) {
				// 累计还款总额应小于待还款金额
				String sql = "select (fp_n1- "
						+ "nvl((select sum(FB_BACK) from feeback left join FEEPLEASE on FB_FPID=FP_ID where FP_STATUSCODE in('AUDITED','COMMITED') and FB_JKSQCODE='"
						+ map.get("fb_jksqcode") + "'),0)" + "-nvl((select sum(fp_pleaseamount) from feeplease where fp_sourcecode='"
						+ map.get("fb_jksqcode") + "' and FP_STATUSCODE in ('AUDITED','COMMITED')),0))fee "
						+ "from feeplease where fp_code='" + map.get("fb_jksqcode") + "' and fp_kind='借款申请单'";
				SqlRowList rs = baseDao.queryForRowSet(sql);
				if (rs.next()) {
					double fee = rs.getGeneralDouble(1, 6);
					if (Double.parseDouble(map.get("fb_back").toString()) > fee) {
						BaseUtil.appendError("还款明细第" + map.get("fb_detno") + ",待还款金额为:" + fee + "<br>");
					}
				}
				map.put("fb_id", baseDao.getSeqId("feeback_SEQ"));
			}
			List<String> gridSql2 = SqlUtil.getInsertSqlbyGridStore(grid2, "feeback");
			baseDao.execute(gridSql2);
		}
		getSumTotal(caller, store.get("fp_id"));
		// 记录操作
		baseDao.logger.save(caller, "fp_id", store.get("fp_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, grid });

		Object fpd_id = baseDao.getFieldsDatasByCondition("FeePleaseDetail", new String[] { "fpd_id", "fpd_detno" },
				"fpd_fpid=" + store.get("fp_id"));
		return fpd_id;// 手机端需求
	}

	void getSumTotal(String caller, Object fp_id) {
		if ("FeePlease!FYBX".equals(caller)) {
			baseDao.execute("update FeePleaseDetail set fpd_total=fpd_n2 where nvl(fpd_total,0)=0 and fpd_fpid=" + fp_id);
			baseDao.execute("update FeePleaseDetail set fpd_n1=fpd_total where (nvl(fpd_n1,0)=0 or nvl(fpd_n1,0)<>fpd_total)  and fpd_fpid="
					+ fp_id);
			baseDao.execute("update feeplease set fp_pleaseamount=nvl((select sum(nvl(fpd_n1,0)) from feepleasedetail where fpd_fpid=fp_id),0) where fp_id="
					+ fp_id);
			baseDao.execute("update feeplease set fp_amount=round(nvl(fp_pleaseamount,0)*nvl((select nvl(cm_crrate,0) from currencysmonth where fp_v13=cm_crname and to_char(fp_recorddate,'yyyymm')=cm_yearmonth),0),2) where fp_id="
					+ fp_id);
			baseDao.execute("update FeePlease set fp_v7='已支付' where nvl(fp_pleaseamount,0)=nvl(fp_n1,0)+nvl(fp_n6,0) and fp_id=" + fp_id);
			baseDao.execute("update FeePlease set fp_v7='部分支付' where nvl(fp_pleaseamount,0)>nvl(fp_n1,0)+nvl(fp_n6,0) and fp_id=" + fp_id);
			baseDao.execute("update FeePlease set fp_v7='未支付' where nvl(fp_n1,0)+nvl(fp_n6,0)=0 and fp_id=" + fp_id);
			baseDao.execute("update feeplease a set FP_SUMJK=round(nvl((select sum(nvl(fp_pleaseamount,0)*NVL(cm_crrate,0)) from feeplease b,currencysmonth where a.fp_pleasemancode=b.fp_pleasemancode AND b.fp_v13=cm_crname and to_char(sysdate,'yyyymm')=cm_yearmonth and b.fp_kind='借款申请单' and b.fp_statuscode in ('AUDITED','COMMITED')),0),2) where fp_kind='费用报销单' and fp_id="
					+ fp_id);
			baseDao.execute("update feeplease a set FP_SUMWGH=round(nvl((select sum((nvl(fp_n1,0)-nvl(fp_n3,0))*NVL(cm_crrate,0)) from feeplease b,currencysmonth where a.fp_pleasemancode=b.fp_pleasemancode AND b.fp_v13=cm_crname and to_char(sysdate,'yyyymm')=cm_yearmonth and b.fp_kind='借款申请单' and b.fp_statuscode in ('AUDITED')),0),2) where fp_kind='费用报销单' and fp_id="
					+ fp_id);
			baseDao.execute("update feeplease a set FP_THISHKAMOUNT=round(nvl((select sum(nvl(fb_back,0)*NVL(cm_crrate,0)) from FeeBack,feeplease b,currencysmonth where fb_fpid=a.fp_id and fb_jksqcode=b.fp_code and b.fp_v13=cm_crname and to_char(sysdate,'yyyymm')=cm_yearmonth and b.fp_kind='借款申请单'),0),2) where fp_kind='费用报销单' and fp_id="
					+ fp_id);
		}
		if ("FeePlease!JKSQ".equals(caller)) {
			baseDao.execute("update feeplease a set FP_SUMJK=round(nvl((select sum(nvl(fp_pleaseamount,0)*NVL(cm_crrate,0)) from feeplease b,currencysmonth where a.fp_pleasemancode=b.fp_pleasemancode AND b.fp_v13=cm_crname and to_char(sysdate,'yyyymm')=cm_yearmonth and b.fp_kind='借款申请单' and b.fp_statuscode in ('AUDITED','COMMITED')),0),2) where fp_id="
					+ fp_id);
			baseDao.execute("update feeplease a set FP_SUMWGH=round(nvl((select sum((nvl(fp_n1,0)-nvl(fp_n3,0))*NVL(cm_crrate,0)) from feeplease b,currencysmonth where a.fp_pleasemancode=b.fp_pleasemancode AND b.fp_v13=cm_crname and to_char(sysdate,'yyyymm')=cm_yearmonth and b.fp_kind='借款申请单' and b.fp_statuscode in ('AUDITED')),0),2) where fp_id="
					+ fp_id);
		}
		// 公章用印申请更新明细费用到主表
		if ("FeePlease!YZSYSQ".equals(caller)) {
			baseDao.execute("update FeePlease set FP_PLEASEAMOUNT=(select sum(fpd_n9) from FeePleaseDetail where fpd_fpid=" + fp_id + "),"
					+ "fp_n3=(select sum(fpd_n1) from FeePleaseDetail where fpd_fpid=" + fp_id + ") where fp_id=" + fp_id);
		}
		//差旅费用报销
		if("FeePlease!CLFBX".equals(caller)) {
			baseDao.execute("update FeePleaseDetail set fpd_n8=fpd_n7 where fpd_fpid="
					+ fp_id);
			baseDao.execute("update feeplease set fp_pleaseamount=nvl((select sum(nvl(fpd_n8,0)) from feepleasedetail where fpd_fpid=fp_id),0) where fp_id="
					+ fp_id);
			baseDao.execute("update feeplease set fp_amount=round(nvl(fp_pleaseamount,0)*nvl((select nvl(cm_crrate,0) from currencysmonth where fp_v13=cm_crname and to_char(fp_recorddate,'yyyymm')=cm_yearmonth),0),2) where fp_id="
					+ fp_id);
			baseDao.execute("update FeePlease set fp_v7='已支付' where nvl(fp_pleaseamount,0)=nvl(fp_n1,0)+nvl(fp_n6,0) and fp_id=" + fp_id);
			baseDao.execute("update FeePlease set fp_v7='部分支付' where nvl(fp_pleaseamount,0)>nvl(fp_n1,0)+nvl(fp_n6,0) and fp_id=" + fp_id);
			baseDao.execute("update FeePlease set fp_v7='未支付' where nvl(fp_n1,0)+nvl(fp_n6,0)=0 and fp_id=" + fp_id);
			
			baseDao.execute("update feeplease a set FP_SUMJK=round(nvl((select sum(nvl(fp_pleaseamount,0)*NVL(cm_crrate,0)) from feeplease b,currencysmonth where a.fp_pleasemancode=b.fp_pleasemancode AND b.fp_v13=cm_crname and to_char(sysdate,'yyyymm')=cm_yearmonth and b.fp_kind='借款申请单' and b.fp_statuscode in ('AUDITED','COMMITED')),0),2) where fp_kind='差旅费报销单' and fp_id="
					+ fp_id);
			baseDao.execute("update feeplease a set FP_SUMWGH=round(nvl((select sum((nvl(fp_n1,0)-nvl(fp_n3,0))*NVL(cm_crrate,0)) from feeplease b,currencysmonth where a.fp_pleasemancode=b.fp_pleasemancode AND b.fp_v13=cm_crname and to_char(sysdate,'yyyymm')=cm_yearmonth and b.fp_kind='借款申请单' and b.fp_statuscode in ('AUDITED')),0),2) where fp_kind='差旅费报销单' and fp_id="
					+ fp_id);
			baseDao.execute("update feeplease a set FP_THISHKAMOUNT=round(nvl((select sum(nvl(fb_back,0)*NVL(cm_crrate,0)) from FeeBack,feeplease b,currencysmonth where fb_fpid=a.fp_id and fb_jksqcode=b.fp_code and b.fp_v13=cm_crname and to_char(sysdate,'yyyymm')=cm_yearmonth and b.fp_kind='借款申请单'),0),2) where fp_kind='差旅费报销单' and fp_id="
					+ fp_id);
		}
	}

	@Override
	public Object updateFeePlease(String formStore, String gridStore, String gridStore2, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能更新在录入的单据!
		Object status = baseDao.getFieldDataByCondition("FeePlease", "fp_statuscode", "fp_id=" + store.get("fp_id"));
		StateAssert.updateOnlyEntering(status);
		if ("FeePlease!CCSQ!new".equals(caller)) {
			checkTime(store);
		}
		if ("FeePlease!FYBX".equals(caller)) {// 来源单据类型为OA单据批量转或公章用印申请转的费用报销单，更新时要判断金额范围并同步更新原单据已转金额。
			Object fp_source[] = baseDao.getFieldsDataByCondition("FeePlease", new String[] { "fp_sourcecaller", "fp_sourcecode",
					"fp_sourcekind" }, "fp_id=" + store.get("fp_id"));
			if ("FeePlease!YZSYSQ".equals(fp_source[0])) {
				for (Map<Object, Object> s : gstore) {
					if (s.get("fpd_d9") != null && s.get("fpd_d9").equals("FeePlease!YZSYSQ")) {
						Object[] obj = baseDao.getFieldsDataByCondition("FeePlease", new String[] { "fp_n3", "fp_n4" }, "fp_code='"
								+ fp_source[1] + "' and fp_kind='" + fp_source[2] + "'");
						Object ofpd_total = baseDao.getFieldDataByCondition("FeePleaseDetail", "fpd_total", "fpd_id=" + s.get("fpd_id"));
						double fee = Double.parseDouble(obj[0].toString()) + Double.parseDouble(ofpd_total.toString())
								- Double.parseDouble(obj[1].toString()) - Double.parseDouble(s.get("fpd_total").toString());
						if (Double.parseDouble(s.get("fpd_total").toString()) > 0 && fee >= 0) {
							baseDao.updateByCondition("FeePlease", "fp_n4=nvl(fp_n4,0)-" + ofpd_total + "+" + s.get("fpd_total"),
									"fp_code='" + fp_source[1] + "' and fp_kind='" + fp_source[2] + "'");
						} else {
							BaseUtil.showError("第" + s.get("fpd_detno") + "行报销金额超出来源单据剩余金额");
						}
					}
				}
			} else { // 如果来自OA单据批量转，更新OA单据CT_ISTURN状态
				boolean boolOA = baseDao
						.checkIf(
								"FEEPLEASE",
								"FP_SOURCECALLER IN (select FO_CALLER FROM FORM left join FORMDETAIL on fd_foid=fo_id  WHERE nvl(FD_TABLE,' ')='CUSTOMTABLE' and nvl(FD_FIELD,' ')='CT_SOURCEKIND' "
										+ "and nvl(FD_DEFAULTVALUE,' ')<>' ') AND FP_ID =" + store.get("fp_id"));
				if (boolOA) {
					Object FP_SOURCECALLER = baseDao.getFieldDataByCondition("FeePlease", "FP_SOURCECALLER", "fp_id=" + store.get("fp_id"));
					List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
					Map<String, Object> map = null;
					for (Map<Object, Object> s : gstore) {
						if (s.get("fpd_d9") != null && s.get("fpd_d9").equals(FP_SOURCECALLER)) {
							Object[] obj = baseDao.getFieldsDataByCondition("customtable", new String[] { "ct_amount", "ct_turnamount" },
									"ct_code='" + s.get("fpd_code") + "' and ct_caller='" + FP_SOURCECALLER + "'");
							Object ofpd_total = baseDao
									.getFieldDataByCondition("FeePleaseDetail", "fpd_total", "fpd_id=" + s.get("fpd_id"));
							double fee = Double.parseDouble(obj[0].toString()) + Double.parseDouble(ofpd_total.toString())
									- Double.parseDouble(obj[1].toString()) - Double.parseDouble(s.get("fpd_total").toString());
							if (Double.parseDouble(s.get("fpd_total").toString()) > 0 && fee >= 0) {
								map = new HashMap<String, Object>();
								map.put("ct_code", s.get("fpd_code"));
								map.put("ct_caller", FP_SOURCECALLER);
								map.put("ct_turnamount", Double.parseDouble(obj[1].toString()) - Double.parseDouble(ofpd_total.toString())
										+ Double.parseDouble(s.get("fpd_total").toString()));
								lists.add(map);
								continue;
							} else {
								BaseUtil.showError("第" + s.get("fpd_detno") + "行报销金额超出来源单据剩余金额");
							}
						}
					}
					for (Map<String, Object> list : lists) {
						baseDao.updateByCondition("customtable", "ct_turnamount=" + list.get("ct_turnamount"),
								"ct_code='" + list.get("ct_code") + "' and ct_caller='" + list.get("ct_caller") + "'");
					}
				}
			}
		}
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		store.put("fp_printstatuscode", "UNPRINT");
		store.put("fp_printstatus", BaseUtil.getLocalMessage("UNPRINT"));
		if ("FeePlease!ZWSQ".equals(caller)) {// 总务申请
			double num = Double.parseDouble(store.get("fp_n3").toString()) + Double.parseDouble(store.get("fp_n4").toString());
			store.put("fp_pleaseamount", num);
			store.put("fp_n5", num);
		}
		if ("FeePlease!FYBX".equals(caller)) {// 费用报销
			SqlRowList rs = baseDao
					.queryForRowSet(
							"select FP_SUMWGH,FP_THISHKAMOUNT from FeePlease where fp_id=? and nvl(FP_SUMWGH,0)>0 and nvl(FP_THISHKAMOUNT,0)<nvl(FP_SUMWGH,0)",
							store.get("fp_id"));
			if (rs.next()) {
				BaseUtil.appendError("报销人还有借款未归还，请先冲销借款金额！本次还款总额(本位币)[" + rs.getGeneralDouble("FP_THISHKAMOUNT") + "],总的未归还金额(本位币)["
						+ rs.getGeneralDouble("FP_SUMWGH") + "]");
			}
			/**
			 * fp_n1 借款申请单已转金额 sum(fb_back)已提交和已审核的费用报销单中还款金额的和
			 * sum(fp_pleaseamount)已提交和已审核的还款申请单还款金额的和 结果fee:待还款金额
			 * */
			/*
			 * String sql = "select sum(p.fp_n1- " +
			 * "nvl((select sum(FB_BACK) from feeback left join FEEPLEASE on FB_FPID=FP_ID where FP_STATUSCODE in('AUDITED','COMMITED') and FB_JKSQCODE=p.fp_code),0)"
			 * +
			 * "-nvl((select sum(fp_pleaseamount) from feeplease where fp_sourcecode=p.fp_code and FP_STATUSCODE in ('AUDITED','COMMITED')),0))"
			 * + "from feeplease p where p.fp_pleasemancode='" +
			 * store.get("fp_pleasemancode") +
			 * "' and p.fp_kind='借款申请单' and p.fp_statuscode='AUDITED'";
			 * SqlRowList rs = baseDao.queryForRowSet(sql); if (rs.next()) {
			 * double fee = rs.getGeneralDouble(1, 6); if (fee > 0)
			 * BaseUtil.appendError("该报销人有未还款的借款单！待还款总额为：" + fee); }
			 */
		}
		if ("FeePlease!HKSQ".equals(caller)) {// 还款申请本次还款总额应小于借款单未还金额（已转金额-已还款金额）
			double fb_back = baseDao.getSummaryByField("feeback left join FEEPLEASE on FB_FPID=FP_ID", "FB_BACK",
					"FP_STATUSCODE in ('AUDITED','COMMITED') and FB_JKSQCODE='" + store.get("fp_sourcecode") + "'");// 费用报销
			double fp_pleaseamount = baseDao.getSummaryByField("feeplease", "fp_pleaseamount",
					"fp_sourcecode='" + store.get("fp_sourcecode") + "' and FP_STATUSCODE in ('AUDITED','COMMITED')");
			Object fp_n1 = baseDao.getFieldDataByCondition("feeplease", "fp_n1", "fp_code='" + store.get("fp_sourcecode")
					+ "' and fp_kind='借款申请单'");
			double fee = NumberUtil.formatDouble(Double.parseDouble(fp_n1.toString()) - fb_back - fp_pleaseamount, 2);
			if (NumberUtil.formatDouble(Double.parseDouble(store.get("fp_pleaseamount").toString()), 2) > fee + 0.01) {
				BaseUtil.showError("累计还款总额应小于待还款金额，待还款金额为:" + fee);
			}
		}
		// 修改Evaluation
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "FeePlease", "fp_id");
		baseDao.execute(formSql);
		// 修改EvaluationDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "FeePleaseDetail", "fpd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("fpd_id") == null || s.get("fpd_id").equals("") || s.get("fpd_id").equals("0")
					|| Integer.parseInt(s.get("fpd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("FEEPLEASEDETAIL_SEQ");
				s.put("fpd_id", id);
				s.put("fpd_code", store.get("fp_code"));
				s.put("fpd_class", store.get("fp_kind"));
				if ("FeePlease!CCSQ".equals(caller)) {
					if (s.get("fpd_date1") == null || s.get("fpd_date1").equals("")) {
						s.put("fpd_date1", store.get("fp_prestartdate"));
					}
					if (s.get("fpd_date2") == null || s.get("fpd_date2").equals("")) {
						s.put("fpd_date2", store.get("fp_preenddate"));
					}
				}
				String sql = SqlUtil.getInsertSqlByMap(s, "FeePleaseDetail", new String[] { "fpd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		String updatesql = "update FeePleaseDETAIL set fpd_n2=round(nvl(fpd_n2,0),2),fpd_n1=round(nvl(fpd_n1,0),2),fpd_total=round(nvl(fpd_total,0),2) where fpd_fpid='"
				+ store.get("fp_id") + "'";
		baseDao.execute(updatesql);
		if ("FeePlease!CLFBX".equals(caller)) {// 计算明细中的所有费用
			String sql = "update FeePleaseDetail set fpd_n8=fpd_n7 where fpd_n8=0 and fpd_fpid=" + store.get("fp_id");
			baseDao.execute(sql);
			baseDao.updateByCondition(
					"FeePlease",
					"fp_pleaseamount="
							+ baseDao.getFieldDataByCondition("feepleasedetail", "sum(fpd_n8)", "fpd_fpid=" + store.get("fp_id")), "fp_id="
							+ store.get("fp_id"));
			SqlRowList rs = baseDao
					.queryForRowSet(
							"select FP_SUMWGH,FP_THISHKAMOUNT from FeePlease where fp_id=? and nvl(FP_SUMWGH,0)>0 and nvl(FP_THISHKAMOUNT,0)<nvl(FP_SUMWGH,0)",
							store.get("fp_id"));
			if (rs.next()) {
				BaseUtil.appendError("报销人还有借款未归还，请先冲销借款金额！本次还款总额(本位币)[" + rs.getGeneralDouble("FP_THISHKAMOUNT") + "],总的未归还金额(本位币)["
						+ rs.getGeneralDouble("FP_SUMWGH") + "]");
			}
		}
		if ("FeePlease!CCSQ".equals(caller)) {
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(FPD_DETNO) from FeePleaseDetail where fpd_date1>fpd_date2 and FPD_FPID=?", String.class,
					store.get("fp_id"));
			if (dets != null) {
				BaseUtil.appendError("起始日期必须小于等于截止日期！行号：" + dets);
			}
		}
		// 更新还款明细
		if (gridStore2 != null) {
			List<String> gridSql2 = SqlUtil.getUpdateSqlbyGridStore(gridStore2, "feeback", "fb_id");
			List<Map<Object, Object>> gstore2 = BaseUtil.parseGridStoreToMaps(gridStore2);
			for (Map<Object, Object> s : gstore2) {
				if (s.get("fb_id") == null || s.get("fb_id").equals("") || s.get("fb_id").equals("0")
						|| Integer.parseInt(s.get("fb_id").toString()) == 0) {// 新添加的数据，id不存在
					int id = baseDao.getSeqId("feeback_SEQ");
					s.put("fb_id", id);
					String sql = SqlUtil.getInsertSqlByMap(s, "feeback", new String[] { "fb_id" }, new Object[] { id });
					gridSql2.add(sql);
				}
				// 累计还款总额应小于待还款金额
				String sql = "select (fp_n1- "
						+ "nvl((select sum(FB_BACK) from feeback left join FEEPLEASE on FB_FPID=FP_ID where FP_STATUSCODE in('AUDITED','COMMITED') and FB_JKSQCODE='"
						+ s.get("fb_jksqcode") + "'),0)" + "-nvl((select sum(fp_pleaseamount) from feeplease where fp_sourcecode='"
						+ s.get("fb_jksqcode") + "' and FP_STATUSCODE in ('AUDITED','COMMITED')),0))fee "
						+ "from feeplease where fp_code='" + s.get("fb_jksqcode") + "' and fp_kind='借款申请单'";
				SqlRowList rs = baseDao.queryForRowSet(sql);
				if (rs.next()) {
					double fee = rs.getGeneralDouble(1, 6);
					if (Double.parseDouble(s.get("fb_back").toString()) > fee) {
						BaseUtil.appendError("还款明细第" + s.get("fb_detno") + "行，待还款金额为:" + fee + "<br>");
					}
				}
			}
			baseDao.execute(gridSql2);
		}
		getSumTotal(caller, store.get("fp_id"));
		// 记录操作
		baseDao.logger.update(caller, "fp_id", store.get("fp_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
		Object fpd_id = baseDao.getFieldsDatasByCondition("FeePleaseDetail", new String[] { "fpd_id", "fpd_detno" },
				"fpd_fpid=" + store.get("fp_id"));
		return fpd_id;// 手机端需求
	}

	@Override
	public void deleteFeePlease(int fp_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("FeePlease", "fp_statuscode", "fp_id=" + fp_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { fp_id });
		// 如果费用单来自客户拜访记录，则修改拜访记录
		Object from = baseDao.getFieldDataByCondition("FeePlease", "fp_sourcekind", "fp_id=" + fp_id);
		if ("客户拜访记录".equals(from)) {
			baseDao.updateByCondition("VisitRecord", "vr_isturnfeeplease='0'",
					"vr_code='" + baseDao.getFieldDataByCondition("FeePlease", "fp_sourcecode", "fp_id=" + fp_id) + "'");
		}
		if ("印章申请单".equals(from)) {
			Object[] objs = baseDao.getFieldsDataByCondition("FeePleaseDetail", new String[] { "fpd_code", "fpd_total" },
					"fpd_d9='FeePlease!YZSYSQ' and fpd_fpid=" + fp_id);
			if (objs != null)
				baseDao.updateByCondition("FeePlease", "fp_n4=nvl(fp_n4,0)-" + objs[1], "fp_code='" + objs[0] + "' and fp_kind='印章申请单'");
		}
		// 如果来自OA单据批量转，更新OA单据
		boolean boolOA = baseDao
				.checkIf(
						"FEEPLEASE",
						"FP_SOURCECALLER IN (select FO_CALLER FROM FORM left join FORMDETAIL on fd_foid=fo_id  WHERE nvl(FD_TABLE,' ')='CUSTOMTABLE' and nvl(FD_FIELD,' ')='CT_SOURCEKIND' "
								+ "and nvl(FD_DEFAULTVALUE,' ')<>' ') AND FP_ID =" + fp_id);
		if (boolOA) {
			Object FP_SOURCECALLER = baseDao.getFieldDataByCondition("FeePlease", "FP_SOURCECALLER", "fp_id=" + fp_id);
			List<Object[]> objs = baseDao.getFieldsDatasByCondition("FeePleaseDetail", new String[] { "fpd_code", "fpd_total" }, "fpd_d9='"
					+ FP_SOURCECALLER + "' and fpd_fpid=" + fp_id);
			if (objs.size() != 0) {
				for (Object[] obj : objs) {
					String sql = "update customtable set ct_turnamount=nvl(ct_turnamount,0)-" + obj[1] + " where ct_code='" + obj[0]
							+ "' and ct_caller='" + FP_SOURCECALLER + "'";
					baseDao.execute(sql);
				}
			}
		}
		// 删除FeePlease
		baseDao.deleteById("FeePlease", "fp_id", fp_id);
		// 删除FeePleaseDetail
		baseDao.deleteById("FeePleaseDetail", "fpd_fpid", fp_id);
		// 删除Feeback
		baseDao.deleteById("feeback", "fb_fpid", fp_id);
		// 记录操作
		baseDao.logger.delete(caller, "fp_id", fp_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { fp_id });
	}

	@Override
	public void auditFeePlease(int fp_id, String caller) {
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { fp_id });
		if ("FeePlease!FYBX".equals(caller)) {// 根据“还款明细”中的数据增加对应借款单里的“已还款金额”
			List<Object[]> res = baseDao.getFieldsDatasByCondition("feeback", new String[] { "FB_JKSQCODE", "fb_back", "fb_detno" },
					"fb_fpid=" + fp_id);
			if (res.size() != 0) {
				for (Object[] re : res) {
					String sql = "select nvl(fp_n1,0)-nvl(fp_n3,0) from feeplease where fp_kind='借款申请单' and fp_code='" + re[0] + "'";
					SqlRowList rs = baseDao.queryForRowSet(sql);
					if (rs.next()) {
						double fee = rs.getGeneralDouble(1, 6);
						if (Double.parseDouble(re[1].toString()) > fee) {
							BaseUtil.showError("还款明细第" + re[2].toString() + "行还款金额超过待还款金额，待还款金额为:" + fee);
						}
					}
				}
			}
			//更新借款申请单的金额
			baseDao.execute("update feeplease set fp_n3=nvl(fp_n3,0)+nvl((select sum(nvl(fb_back,0)) from feeback where fb_fpid="
					+ fp_id
					+ " and fb_jksqcode=feeplease.fp_code) ,0) where exists (select 1 from feeback where fb_jksqcode=feeplease.fp_code and fb_fpid="
					+ fp_id + ") and fp_kind='借款申请单'");
			// 已还款金额
			baseDao.execute("update feeplease set fp_n6=(select sum(fb_back) from feeback where fb_fpid=" + fp_id + ") where fp_id="
					+ fp_id);
			baseDao.execute("update FeePlease set fp_v7='已支付' where nvl(fp_pleaseamount,0)=nvl(fp_n1,0)+nvl(fp_n6,0) and fp_id=" + fp_id);
			baseDao.execute("update FeePlease set fp_v7='部分支付' where nvl(fp_pleaseamount,0)>nvl(fp_n1,0)+nvl(fp_n6,0) and fp_id=" + fp_id);
			baseDao.execute("update FeePlease set fp_v7='未支付' where nvl(fp_n1,0)+nvl(fp_n6,0)=0 and fp_id=" + fp_id);
		}
		if ("FeePlease!CLFBX".equals(caller)) {// 根据“还款明细”中的数据增加对应借款单里的“已还款金额”
			List<Object[]> res = baseDao.getFieldsDatasByCondition("feeback", new String[] { "FB_JKSQCODE", "fb_back", "fb_detno" },
					"fb_fpid=" + fp_id);
			if (res.size() != 0) {
				for (Object[] re : res) {
					String sql = "select nvl(fp_n1,0)-nvl(fp_n3,0) from feeplease where fp_kind='借款申请单' and fp_code='" + re[0] + "'";
					SqlRowList rs = baseDao.queryForRowSet(sql);
					if (rs.next()) {
						double fee = rs.getGeneralDouble(1, 6);
						if (Double.parseDouble(re[1].toString()) > fee) {
							BaseUtil.showError("还款明细第" + re[2].toString() + "行还款金额超过待还款金额，待还款金额为:" + fee);
						}
					}
				}
			}
			//更新借款申请单的金额
			baseDao.execute("update feeplease set fp_n3=nvl(fp_n3,0)+nvl((select sum(nvl(fb_back,0)) from feeback where fb_fpid="
					+ fp_id
					+ " and fb_jksqcode=feeplease.fp_code) ,0) where exists (select 1 from feeback where fb_jksqcode=feeplease.fp_code and fb_fpid="
					+ fp_id + ") and fp_kind='借款申请单'");
			// 差旅费报销已还款金额
			baseDao.execute("update feeplease set fp_n6=(select sum(fb_back) from feeback where fb_fpid=" + fp_id + ") where fp_id="
					+ fp_id);
			baseDao.execute("update FeePlease set fp_v7='已支付' where nvl(fp_pleaseamount,0)=nvl(fp_n1,0)+nvl(fp_n6,0) and fp_id=" + fp_id);
			baseDao.execute("update FeePlease set fp_v7='部分支付' where nvl(fp_pleaseamount,0)>nvl(fp_n1,0)+nvl(fp_n6,0) and fp_id=" + fp_id);
			baseDao.execute("update FeePlease set fp_v7='未支付' where nvl(fp_n1,0)+nvl(fp_n6,0)=0 and fp_id=" + fp_id);
		}
		if ("FeePlease!HKSQ".equals(caller)) {// 还款申请单:审核时，反应到借款申请单上
			Object[] ob = baseDao.getFieldsDataByCondition("feeplease", new String[] { "fp_sourcecode", "fp_pleaseamount" }, "fp_id="
					+ fp_id);
			Object[] ob1 = baseDao.getFieldsDataByCondition("feeplease", new String[] { "fp_n1", "fp_n3" }, "fp_code='" + ob[0].toString()
					+ "' and fp_kind='借款申请单'");
			double fee = NumberUtil.formatDouble(Double.parseDouble(ob1[0].toString()) - Double.parseDouble(ob1[1].toString()), 2);
			if (NumberUtil.formatDouble(Double.parseDouble(ob[1].toString()), 2) > fee + 0.01) {
				BaseUtil.showError("累计还款总额应小于待还款金额，待还款金额为:" + fee);
			}
			String sql = "update feeplease set fp_n3=nvl(fp_n3,0)+(select fp_pleaseamount from feeplease where fp_id=?) where fp_kind='借款申请单' and fp_code=(select fp_sourcecode from feeplease where fp_id=?)";
			baseDao.execute(sql, new Object[] { fp_id, fp_id });
		}
		// 执行审核操作
		baseDao.audit("FeePlease", "fp_id=" + fp_id, "fp_status", "fp_statuscode", "fp_auditdate", "fp_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "fp_id", fp_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { fp_id });
	}

	@Override
	public void resAuditFeePlease(int fp_id, String caller) {
		String local = SpObserver.getSp();
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ar_code) from accountregister where (ar_sourcetype,ar_source) in (select FP_KIND,fp_code from FeePlease where fp_id=?)",
						String.class, fp_id);
		if (dets != null) {
			BaseUtil.showError("已转银行登记，不允许反审核！银行登记：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(fp_kind||fp_code) from FEEPLEASE where (FP_SOURCEKIND,FP_SOURCECODE) in (select FP_KIND,fp_code from FeePlease where fp_id=?)",
						String.class, fp_id);
		if (dets != null) {
			BaseUtil.showError("已转" + dets + "，不允许反审核！");
		}
		if ("FeePlease!CCSQ".equals(caller) || "FeePlease!CCSQ!new".equals(caller)) {
			Object[] ma = baseDao.getFieldsDataByCondition("feeplease left join master on fp_turnmaster = ma_function",
					"fp_turnmaster,ma_user", "fp_id=" + fp_id);
			if (ma[0] != null && !local.equals(ma[1])) {
				// 抛转账套的差旅单
				String master = ma[1] == null ? "" : (ma[1] + ".");
				Object CCSQ = baseDao.getFieldDataByCondition(master + "FeePlease", "fp_code",
						"fp_kind='差旅费报销单' and fp_sourcekind='出差申请单' and fp_sourcecode=("
								+ "select fp_code||(SELECT '('||ma_function||')' from master where ma_user='" + local
								+ "') from feeplease where fp_id=" + fp_id + ")");
				if (CCSQ != null) {
					BaseUtil.showError("该出差申请单已转入过差旅费报销单,单号:" + CCSQ + ", 账套:" + ma[0] + "	不允许反审核!");
				}
			} else {
				// 未抛转的差旅单
				Object CCSQ = baseDao.getFieldDataByCondition("FeePlease", "fp_code",
						"fp_kind='差旅费报销单' and fp_sourcekind='出差申请单' and fp_sourcecode=(select fp_code from feeplease where fp_id=" + fp_id
								+ ")");
				if (CCSQ != null) {
					BaseUtil.showError("该出差申请单已转入过差旅费报销单,单号:" + CCSQ + "	 不允许反审核!");
				}
			}
		}
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "resAudit", "before", new Object[] { fp_id });
		// 执行反审核操作
		baseDao.resAudit("FeePlease", "fp_id=" + fp_id, "fp_status", "fp_statuscode", "fp_auditman", "fp_auditdate");
		if ("FeePlease!FYBX".equals(caller)) {// 根据“还款明细”中的数据减少对应借款单里的“已还款金额”
			baseDao.execute("update feeplease set fp_n3=fp_n3-(select nvl(fb_back,0) from feeback where fb_fpid=" + fp_id
					+ " and fb_jksqcode=fp_code) " + " where fp_code in (select fb_jksqcode from feeback where fb_fpid=" + fp_id
					+ ") and fp_kind='借款申请单'");
			// 费用报销单的'已还款金额'设为0
			baseDao.execute("update feeplease set fp_n6=0 where fp_id=" + fp_id);
			baseDao.execute("update FeePlease set fp_v7='部分支付' where nvl(fp_pleaseamount,0)>nvl(fp_n1,0)+nvl(fp_n6,0) and fp_id=" + fp_id);
			baseDao.execute("update FeePlease set fp_v7='未支付' where nvl(fp_n1,0)+nvl(fp_n6,0)=0 and fp_id=" + fp_id);
		}
		if ("FeePlease!HKSQ".equals(caller)) {// 还款申请单:反审核时，反应到借款申请单上
			String sql = "update feeplease set fp_n3=nvl(fp_n3,0)-(select fp_pleaseamount from feeplease where fp_id=?) where fp_kind='借款申请单' and fp_code=(select fp_sourcecode from feeplease where fp_id=?)";
			baseDao.execute(sql, new Object[] { fp_id, fp_id });
		}
		// 记录操作
		baseDao.logger.resAudit(caller, "fp_id", fp_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "resAudit", "after", new Object[] { fp_id });
	}

	@Override
	@Transactional
	public void submitFeePlease(int fp_id, String caller) {
		getSumTotal(caller, fp_id);
		Object status = baseDao.getFieldDataByCondition("FeePlease", "fp_statuscode", "fp_id=" + fp_id);
		StateAssert.submitOnlyEntering(status);
		if ("FeePlease!CCSQ".equals(caller)) {
			List<Object[]> data = baseDao.getFieldsDatasByCondition("FeePleaseDetail",
					new String[] { "fpd_date1", "fpd_date2", "fpd_detno" }, "fpd_fpid=" + fp_id);
			for (Object[] os : data) {
				Date start = DateUtil.parseStringToDate(os[0].toString(), "yyyy-MM-dd HH:mm:ss");
				Date end = DateUtil.parseStringToDate(os[1].toString(), "yyyy-MM-dd HH:mm:ss");
				if (start.getTime() > end.getTime()) {
					BaseUtil.showError("第" + os[2] + "行,开始时间大于结束时间!");
				}
			}
		}
		// 判断是否启用延期限制提交
		if ("FeePlease!CCSQ".equals(caller) || "FeePlease!CCSQ!new".equals(caller)) {
			commitNeedCheck(caller, fp_id);
		}
		if ("FeePlease!FYBX".equals(caller)) {
			SqlRowList rs = baseDao.queryForRowSet(
					"select fp_amount,FP_THISHKAMOUNT from FeePlease where fp_id=? and nvl(fp_amount,0)<nvl(FP_THISHKAMOUNT,0)", fp_id);
			if (rs.next()) {
				BaseUtil.showError("费用报销报销总额必须大于等于本次还款总额！报销总额(本位币)[" + rs.getGeneralDouble("fp_amount") + "],本次还款总额(本位币)["
						+ rs.getGeneralDouble("FP_THISHKAMOUNT") + "]");
			}
			List<Object[]> res = baseDao.getFieldsDatasByCondition("feeback", new String[] { "FB_JKSQCODE", "fb_back", "fb_detno" },
					"fb_fpid=" + fp_id);
			if (res.size() != 0) {// 累计还款总额应小于借款单已转金额
				for (Object[] re : res) {
					String sql = "select (fp_n1- "
							+ "nvl((select sum(FB_BACK) from feeback left join FEEPLEASE on FB_FPID=FP_ID where FP_STATUSCODE in('AUDITED','COMMITED') and FB_JKSQCODE='"
							+ re[0].toString() + "'),0)" + "-nvl((select sum(fp_pleaseamount) from feeplease where fp_sourcecode='"
							+ re[0].toString()
							+ "' and fp_sourcekind in ('出差申请单','借款申请单') and FP_STATUSCODE in ('AUDITED','COMMITED')),0))fee "
							+ "from feeplease where fp_code='" + re[0].toString() + "' and fp_kind='借款申请单'";
					SqlRowList rs1 = baseDao.queryForRowSet(sql);
					if (rs1.next()) {
						double fee = rs1.getGeneralDouble(1, 6);
						if (Double.parseDouble(re[1].toString()) > fee) {
							BaseUtil.showError("还款明细第" + re[2].toString() + "行还款金额超过待还款金额，待还款金额为:" + fee);
						}
					}
				}
			}
			// 费用报销的提交逻辑
			if (baseDao.isDBSetting(caller, "comitLogicfeeCategory")) {
				comitLogic_feeCategory(fp_id);
			}
		}
		if ("FeePlease!HKSQ".equals(caller)) {// 还款申请本次还款总额应小于借款单已转金额
			Object[] ob = baseDao.getFieldsDataByCondition("feeplease", new String[] { "fp_sourcecode", "fp_pleaseamount" }, "fp_id="
					+ fp_id);
			double fb_back = baseDao.getSummaryByField("feeback left join FEEPLEASE on FB_FPID=FP_ID", "FB_BACK",
					"FP_STATUSCODE in ('AUDITED','COMMITED') and FB_JKSQCODE='" + ob[0] + "'");// 费用报销
			double fp_pleaseamount = baseDao.getSummaryByField("feeplease", "fp_pleaseamount", "fp_sourcecode='" + ob[0]
					+ "' and FP_STATUSCODE in ('AUDITED','COMMITED')");
			Object fp_n1 = baseDao.getFieldDataByCondition("feeplease", "fp_n1", "fp_code='" + ob[0] + "' and fp_kind='借款申请单'");
			double fee = NumberUtil.formatDouble(Double.parseDouble(fp_n1.toString()) - fb_back - fp_pleaseamount, 2);
			if (NumberUtil.formatDouble(Double.parseDouble(ob[1].toString()), 2) > fee + 0.01) {
				BaseUtil.showError("累计还款总额应小于待还款金额，待还款金额为:" + fee);
			}
		}
		if ("FeePlease!CLFBX".equals(caller)) {// 计算差旅费明细的小计
			// 计算明细中的所有费用
			baseDao.updateByCondition("FeePlease",
					"fp_pleaseamount=" + baseDao.getFieldDataByCondition("feepleasedetail", "sum(fpd_n8)", "fpd_fpid=" + fp_id), "fp_id="
							+ fp_id);
			baseDao.updateByCondition("FeePlease", "fp_v7='未支付'", "fp_v7 is null and fp_id=" + fp_id);
			// 差旅费的提交逻辑
			if (baseDao.isDBSetting(caller, "comitLogicfeeCategory")) {
				comitLogic_feeCategory(fp_id);
			}
		}
		if ("FeePlease!CCSQ".equals(caller)) {// 计算差旅费明细的小计
			baseDao.execute("update FeePleaseDetail set fpd_n6=fpd_n5 where nvl(fpd_n6,0)=0 and fpd_fpid=" + fp_id);
		}
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { fp_id });
		// 执行提交操作
		baseDao.submit("FeePlease", "fp_id=" + fp_id, "fp_status", "fp_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "fp_id", fp_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { fp_id });
	}

	@Override
	public void resSubmitFeePlease(int fp_id, String caller) {
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "resCommit", "before", new Object[] { fp_id });
		// 执行反提交操作
		baseDao.resOperate("FeePlease", "fp_id=" + fp_id, "fp_status", "fp_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "fp_id", fp_id);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "resCommit", "after", new Object[] { fp_id });
	}

	@Override
	public void endFeePlease(int fp_id, String caller) {
		// 结案
		baseDao.updateByCondition("FeePlease", "fp_statuscode='FINISH',fp_status='" + BaseUtil.getLocalMessage("FINISH") + "'", "fp_id="
				+ fp_id);
		// 记录操作
		baseDao.logger.others("msg.end", "msg.endSuccess", caller, "fp_id", fp_id);
	}

	@Override
	public void resEndFeePlease(int fp_id, String caller) {
		// 只能对状态为[已结案]的订单进行反结案操作!
		Object status = baseDao.getFieldDataByCondition("FeePlease", "fp_statuscode", "fp_id=" + fp_id);
		if (!status.equals("FINISH")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resEnd_onlyEnd"));
		}
		// 反结案
		baseDao.resOperate("FeePlease", "fp_id=" + fp_id, "fp_status", "fp_statuscode");
		//参数配置：当选择已审核反结案后单据状态为已审核,默认为在录入
		if(baseDao.checkIf("configs", "code='resEndFPStatus' and caller='"+caller+"'")) {
			baseDao.updateByCondition("FeePlease", "fp_status=(select case when data=1 then '已审核' when data=0 then '在录入' end from configs where code='resEndFPStatus' and caller='"+caller+"'),fp_statuscode=(select case when data=1 then 'AUDITED' when data=0 then 'ENTERING'  end from configs where  code='resEndFPStatus' and caller='"+caller+"')", "fp_id=" + fp_id);
		}
		// 记录操作
		baseDao.logger.others("msg.resEnd", "msg.resEndSuccess", caller, "fp_id", fp_id);
	}

	@Override
	public Map<String, Object> turnCLFBX(int fp_id, String caller) {
		int fpid = 0;
		Object fpcode = null;
		JSONObject j = null;
		String localSob = SpObserver.getSp();
		Object sob = null;
		String sobName = null;
		Map<String, Object> map = new HashMap<String, Object>();
		Object[] code = baseDao.getFieldsDataByCondition("FeePlease", "fp_code,fp_turnmaster", "fp_id=" + fp_id);
		Object localSobName = baseDao.getFieldDataByCondition("master", "ma_function", "ma_user='" + localSob + "'");
		if (localSobName == null) {
			localSobName = "";
		}
		if (code[1] != null && !"".equals(code[1]) && !localSobName.equals(code[1])) {
			Object[] sobs = baseDao.getFieldsDataByCondition("master", "ma_user,ma_function", "ma_function='" + code[1] + "'");
			if (sobs == null) {
				BaseUtil.showError("没有该帐套!请核对后重试!");
			}
			sob = sobs[0];
			sobName = sobs[1].toString();
			Object[] feedata = baseDao.getFieldsDataByCondition(sobs[0] + "." + "FeePlease", new String[] { "fp_code", "fp_id" },
					"fp_sourcekind='出差申请单' and fp_sourcecode='" + code[0] + "(" + localSobName + ")" + "'");
			if (feedata != null) {
				BaseUtil.showError("转入失败,此出差申请已存在于" + code[1] + "的差旅费报销单中,单号为:" + feedata[0]);
			}
			j = feePleaseDao.turnCLFBX(fp_id, caller, sobs[0]);
			baseDao.logger.turn("转差旅费报销单", caller, "fp_id", fp_id);
			if (j != null) {
				fpid = j.getInt("fp_id");
				fpcode = j.getString("fp_code");
			}
			map.put("fp_id", fpid);
			map.put("fp_code", fpcode);
			map.put("sobName", sobName);
		} else {
			code = baseDao.getFieldsDataByCondition("FeePlease", new String[] { "fp_code", "fp_id" }, "fp_sourcecode='" + code[0]
					+ "' and fp_kind='差旅费报销单'");
			if (code != null && !code.equals("")) {
				BaseUtil.showError(BaseUtil.getLocalMessage("oa.fee.feeplease.haveturnCLFBX")
						+ "<a href=\"javascript:openUrl('jsps/oa/fee/feePlease.jsp?formCondition=fp_idIS" + code[1]
						+ "&gridCondition=fpd_fpidIS" + code[1] + "&whoami=FeePlease!CLFBX')\">" + code[0] + "</a>&nbsp;");
			} else {
				j = feePleaseDao.turnCLFBX(fp_id, caller, sob);
				baseDao.logger.turn("转差旅费报销单", caller, "fp_id", fp_id);
				if (j != null) {
					fpid = j.getInt("fp_id");
				}
			}
			map.put("fp_id", fpid);
			map.put("sobName", sobName);
		}
		return map;
	}

	@Override
	public String[] printFeePlease(int fp_id, String reportName, String condition, String caller) {
		// 判断已审核才允许打印
		if (baseDao.isDBSetting(caller, "printNeedAudit")) {
			String status = baseDao.getFieldValue("FeePlease", "fp_printstatus", "fp_id=" + fp_id, String.class);
			StateAssert.printOnlyAudited(status);
		}
		// 执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[] { fp_id });
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 修改打印状态
		baseDao.print("FeePlease", "fp_id=" + fp_id, "fp_printstatus", "fp_printstatuscode");
		// 记录操作
		baseDao.logger.print(caller, "fp_id", fp_id);
		// 执行打印后的其它逻辑
		handlerService.handler("caller", "print", "after", new Object[] { fp_id });
		return keys;
	}

	@Override
	public int turnFYBX(int fp_id, String caller) {
		int fpid = 0;
		Object[] code = baseDao.getFieldsDataByCondition("FeePlease", "fp_code", "fp_id=" + fp_id);
		code = baseDao.getFieldsDataByCondition("FeePlease", new String[] { "fp_code", "fp_id" }, "fp_sourcecode='" + code[0]
				+ "' and fp_kind='费用报销单'");
		if (code != null && StringUtil.hasText(code[0])) {
			BaseUtil.showError(BaseUtil.getLocalMessage("oa.fee.feeplease.haveturnFYBX")
					+ "<a href=\"javascript:openUrl('jsps/oa/fee/feePlease.jsp?formCondition=fp_idIS" + code[1]
					+ "&gridCondition=fpd_fpidIS" + code[1] + "&whoami=" + caller + "')\">" + code[0] + "</a>&nbsp;");
		} else {
			fpid = feePleaseDao.turnFYBX(fp_id, caller);
			baseDao.logger.turn("转费用报销单", caller, "fp_id", fp_id);
		}
		return fpid;
	}

	@Override
	public int turnYHFKSQ(int fp_id, String caller) {
		int fpid = 0;
		Object[] code = baseDao.getFieldsDataByCondition("FeePlease", "fp_code", "fp_id=" + fp_id);
		code = baseDao.getFieldsDataByCondition("FeePlease", new String[] { "fp_code", "fp_id" }, "fp_sourcecode='" + code[0]
				+ "' and fp_kind='银行付款申请单'");
		if (code != null && StringUtil.hasText(code[0])) {
			BaseUtil.showError(BaseUtil.getLocalMessage("oa.fee.feeplease.haveturnYHFKSQ")
					+ "<a href=\"javascript:openUrl('jsps/oa/fee/feePlease.jsp?formCondition=fp_idIS" + code[1]
					+ "&gridCondition=fpd_fpidIS" + code[1] + "&whoami=" + caller + "')\">" + code[0] + "</a>&nbsp;");
		} else {
			fpid = feePleaseDao.turnYHFKSQ(fp_id, caller);
			baseDao.logger.turn("转银行付款申请单", caller, "fp_id", fp_id);
		}
		return fpid;
	}

	@Override
	public int turnYWZDBX(int fp_id, String caller) {
		int fpid = 0;
		Object[] code = baseDao.getFieldsDataByCondition("FeePlease", "fp_code", "fp_id=" + fp_id);
		code = baseDao.getFieldsDataByCondition("FeePlease", new String[] { "fp_code", "fp_id" }, "fp_sourcecode='" + code[0]
				+ "' and fp_kind='业务招待费报销单'");
		if (code != null && StringUtil.hasText(code[0])) {
			BaseUtil.showError(BaseUtil.getLocalMessage("oa.fee.feeplease.haveturnYHFKSQ")
					+ "<a href=\"javascript:openUrl('jsps/oa/fee/feePlease.jsp?formCondition=fp_idIS" + code[1]
					+ "&gridCondition=fpd_fpidIS" + code[1] + "&whoami=" + caller + "')\">" + code[0] + "</a>&nbsp;");
		} else {
			fpid = feePleaseDao.turnYWZDBX(fp_id, caller);
			baseDao.logger.turn("转业务招待费报销单", caller, "fp_id", fp_id);
		}
		return fpid;
	}

	@Override
	public int jksqturnFYBX(int fp_id, String caller) {
		int fpid = 0;
		Object[] code = baseDao.getFieldsDataByCondition("FeePlease", "fp_code", "fp_id=" + fp_id);
		code = baseDao.getFieldsDataByCondition("FeePlease", new String[] { "fp_code", "fp_id" }, "fp_sourcecode='" + code[0]
				+ "' and fp_kind='费用报销单'");
		if (code != null && StringUtil.hasText(code[0])) {
			BaseUtil.showError(BaseUtil.getLocalMessage("oa.fee.feeplease.haveturnFYBX")
					+ "<a href=\"javascript:openUrl('jsps/oa/fee/feePlease2.jsp?formCondition=fp_idIS" + code[1]
					+ "&gridCondition=fpd_fpidIS" + code[1] + "&whoami=" + caller + "')\">" + code[0] + "</a>&nbsp;");
		} else {
			fpid = feePleaseDao.jksqturnFYBX(fp_id, caller);
			baseDao.logger.turn("转费用保险单", caller, "fp_id", fp_id);
		}
		return fpid;
	}

	@Override
	public void confirmFeePlease(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("FeePlease", "fp_statuscode", "fp_id=" + id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.confirm_onlyAudit"));
		}
		// 执行反审核操作
		baseDao.updateByCondition("FeePlease", "fp_v11='已处理'", "fp_id=" + id);
		// 记录操作
		baseDao.logger.others("msg.confirm", "msg.confirmSuccess", caller, "fp_id", id);
	}

	@Override
	@Transactional
	public String turnBankRegister(int id, String paymentcode, String payment, double thispayamount, String caller) {
		Object status = baseDao.getFieldDataByCondition("FeePlease", "fp_statuscode", "fp_id=" + id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError("审核后才能转银行登记!");
		}
		if (thispayamount == 0) {
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ar_code) from accountregister where (ar_sourcetype,ar_source) in (select FP_KIND,fp_code from FeePlease where fp_id=?)",
							String.class, id);
			if (dets != null) {
				BaseUtil.showError("已转银行登记，不允许转银行登记！银行登记：" + dets);
			}
		}
		// 更新科目信息
		if (paymentcode != null && !"".equals(paymentcode)) {
			String error = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
					String.class, paymentcode);
			if (error != null) {
				BaseUtil.showError("填写科目编号不存在，或者状态不等于已审核，或者不是末级科目，不允许转应付票据！");
			}
			error = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(CA_ISCASHBANK,0)=0", String.class, paymentcode);
			if (error != null) {
				BaseUtil.showError("付款科目有误，请填写银行现金科目！");
			}
			baseDao.execute("update FeePlease set fp_v11=?,fp_v10=? where fp_id=?", paymentcode, payment, id);
		}
		boolean success = false;
		int ar_id = baseDao.getSeqId("ACCOUNTREGISTER_SEQ");
		String code = baseDao.sGetMaxNumber("AccountRegister", 2);
		Object[] cate = baseDao.getFieldsDataByCondition(
				"Category left join currencysmonth on ca_currency=cm_crname and cm_yearmonth=to_char(sysdate,'yyyymm')", new String[] {
						"ca_id", "ca_currency", "nvl(cm_crrate,0)", "ca_description" }, "ca_code='" + paymentcode + "'");
		if (cate == null) {
			BaseUtil.showError("转银行登记的科目不存在！");
		}
		payment = cate[3].toString();
		double oldamount = thispayamount;
		Double fprate = 0.0;
		Object currency = baseDao.getFieldDataByCondition("FeePlease", "fp_v13", "fp_id=" + id);
		if (currency != null && cate[1] != null) {
			if (!currency.equals(cate[1])) {
				fprate = baseDao.getFieldValue("Currencysmonth", "nvl(cm_crrate,0)", "cm_crname='" + currency
						+ "' and cm_yearmonth=to_char(sysdate, 'yyyymm')", Double.class);
				// 月度汇率为空则提示
				if (fprate == null) {
					BaseUtil.showError("月度汇率未设置，请先设置!");
				}

				if (Double.parseDouble(cate[2].toString()) != 0) {
					thispayamount = NumberUtil.formatDouble(thispayamount * fprate / Double.parseDouble(cate[2].toString()), 2);
				} else {
					thispayamount = NumberUtil.formatDouble(thispayamount * fprate, 2);
				}
			}
		}
		if ("FeePlease!CLFBX".equals(caller)) {// 差旅费报销
			Object[] data = baseDao.getFieldsDataByCondition("FeePlease", new String[] { "fp_code", "fp_pleaseamount", "fp_department",
					"fp_departmentcode", "fp_remark", "fp_pleaseman", "fp_prjcode", "fp_prjname", "fp_cop" }, "fp_id=" + id);
			String insertSql = "insert into AccountRegister (ar_id,ar_recorddate,ar_date,ar_payment,ar_type,"
					+ "ar_code,ar_sourceid,ar_source,ar_sourcetype,ar_statuscode,ar_status,ar_recordman,"
					+ "ar_accountcode,ar_accountname,ar_accountcurrency,ar_cateid,ar_departmentcode,ar_departmentname,ar_memo,"
					+ "ar_prjcode,ar_prjname,ar_apamount,ar_cop) values(?,sysdate,sysdate,?,?,?,?,?,?,'ENTERING',?,?,?,?,?,?,?,?,?,?,?,?,?)";
			String remark = data[5] + "差旅费（" + data[4] + "）";// 银行登记备注默认为申请人+差旅费+原单据备注，若原备注为空，则备注为申请人+报销
			if (!StringUtil.hasText(data[4])) {
				remark = data[5] + "差旅费";
			}
			baseDao.execute(insertSql,
					new Object[] { ar_id, thispayamount, "费用", code, id, data[0], "差旅费报销单", BaseUtil.getLocalMessage("ENTERING"),
							SystemSession.getUser().getEm_name(), paymentcode, payment, cate[1], cate[0], data[3], data[2], remark,
							data[6], data[7], oldamount, data[8] });
			baseDao.execute("insert into AccountRegisterDetail(ard_id,ard_arid,ard_detno,ard_debit,ard_ordertype,ard_orderid,ard_ordercode,ard_orderdetno,ard_explanation,ard_catecode) "
					+ "select AccountRegisterDetail_seq.nextval,"
					+ ar_id
					+ ",rownum,fpd_n8,'差旅费报销单',"
					+ id
					+ ",'"
					+ data[0]
					+ "',fpd_detno,fpd_d3,fpd_catecode "
					+ "from (select * from FeePleaseDetail where fpd_fpid="
					+ id
					+ " order by fpd_detno)");
			baseDao.execute("update accountregister set ar_accountrate=(select nvl(cm_crrate,0) from currencysmonth where ar_accountcurrency=cm_crname and to_char(ar_date,'yyyymm')=cm_yearmonth) where ar_code='"
					+ code + "'");
			baseDao.execute("update accountregisterdetail set ard_catedesc=(select ca_description from category where ard_catecode=ca_code) where ard_arid="
					+ ar_id + " and nvl(ard_catecode,' ')<>' '");
			baseDao.execute("update accountregister set ar_currencytype=(select max(ca_currencytype) from category where ca_code in (select ard_catecode from accountregisterdetail where ar_id=ard_arid and nvl(ard_catecode,' ')<>' ')) where ar_id="
					+ ar_id);
			SqlRowList rs = baseDao.queryForRowSet("select * from accountregister where ar_id=? and nvl(ar_currencytype,0)<>0", ar_id);
			if (rs.next()) {
				baseDao.execute("update accountregisterdetail set ard_doubledebit=ard_debit where ard_arid=" + ar_id
						+ " and nvl(ard_catecode,' ')<>' '");
				baseDao.execute("update accountregisterdetail set ard_currency=(select fp_v13 from feeplease where fp_id=" + id
						+ ") where ard_arid=" + ar_id + " and nvl(ard_catecode,' ')<>' '");
				baseDao.execute("update accountregisterdetail set ard_rate=(select nvl(cm_crrate,0) from currencysmonth where cm_yearmonth=to_char(sysdate,'yyyymm') and ard_currency=cm_crname) where ard_arid="
						+ ar_id + " and nvl(ard_catecode,' ')<>' '");
				baseDao.execute("update accountregisterdetail set ard_debit=round(ard_doubledebit*ard_rate,2) where ard_arid=" + ar_id
						+ " and nvl(ard_catecode,' ')<>' '");
			}
			// 如果ca_asstype为部门或者员工则插入accountregisterdetailass表
			Object[] empdata = baseDao.getFieldsDataByCondition("employee", new String[] { "em_code", "em_name" }, "em_name='" + data[5]
					+ "'");
			Object[] deptdata = baseDao.getFieldsDataByCondition("department", new String[] { "dp_code", "dp_name" }, "dp_name='" + data[2]
					+ "' and dp_statuscode <> 'DISABLE'");
			Object dp_code = "";
			if (deptdata != null) {
				dp_code = deptdata[0];
			}
			String sql1 = "insert into accountregisterdetailass(ars_id,ars_ardid,ars_detno,ars_asstype,ars_asscode,ars_assname,ars_type) "
					+ "select accountregisterdetailass_seq.nextval,ard_id,1,'部门','"
					+ dp_code
					+ "','"
					+ data[2]
					+ "','AccountRegister!Bank' from accountregisterdetail,category where ard_catecode=ca_code and (ca_asstype like '%Dept%') and ard_arid="
					+ ar_id;
			String sql2 = "insert into accountregisterdetailass(ars_id,ars_ardid,ars_detno,ars_asstype,ars_asscode,ars_assname,ars_type) "
					+ "select accountregisterdetailass_seq.nextval,ard_id,2,'员工','"
					+ empdata[0]
					+ "','"
					+ empdata[1]
					+ "','AccountRegister!Bank' from accountregisterdetail,category where ard_catecode=ca_code and (ca_asstype like '%Empl%') and ard_arid="
					+ ar_id;
			String sql3 = "insert into accountregisterdetailass(ars_id,ars_ardid,ars_detno,ars_asstype,ars_asscode,ars_assname,ars_type) "
					+ "select accountregisterdetailass_seq.nextval,ard_id,3,'项目','"
					+ data[6]
					+ "','"
					+ data[7]
					+ "','AccountRegister!Bank' from accountregisterdetail,category where ard_catecode=ca_code and REGEXP_LIKE(ca_asstype, '(#|^)Otp') and ard_arid="
					+ ar_id;
			baseDao.execute(sql1);
			baseDao.execute(sql2);
			baseDao.execute(sql3);
			success = true;
		} else if ("FeePlease!FYBX".equals(caller)) {// 费用 报销
			Object[] data = baseDao.getFieldsDataByCondition("FeePlease", new String[] { "fp_code", "fp_pleaseamount", "fp_department",
					"fp_departmentcode", "fp_remark", "fp_pleaseman", "fp_n6", "fp_prjcode", "fp_prjname", "fp_pleasemancode", "fp_v3",
					"fp_cop" }, "fp_id=" + id);
			String insertSql = "insert into AccountRegister (" + "ar_id,ar_recorddate,ar_date,ar_payment,ar_type,"
					+ "ar_code,ar_sourceid,ar_source,ar_sourcetype,ar_statuscode,ar_status,ar_recordman,"
					+ "ar_accountcode,ar_accountname,ar_accountcurrency,ar_cateid,ar_departmentcode,ar_departmentname,ar_pleaseman,"
					+ "ar_memo,ar_prjcode,ar_prjname,ar_apamount,ar_cop) values("
					+ "?,sysdate,sysdate,?,?,?,?,?,?,'ENTERING',?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			String remark = data[5] + "报销（" + data[4] + "）";// 银行登记备注默认为申请人+报销+原单据备注，若原备注为空，则备注为申请人+报销
			if (!StringUtil.hasText(data[4])) {
				remark = data[5] + "报销";
			}
			remark = remark + " " + data[10];// 银行登记备注添加费用报销的【费用内容】
			baseDao.execute(insertSql,
					new Object[] { ar_id, thispayamount, "费用", code, id, data[0], "费用报销单", BaseUtil.getLocalMessage("ENTERING"),
							SystemSession.getUser().getEm_name(), paymentcode, payment, cate[1], cate[0], data[3], data[2], data[5],
							remark, data[7], data[8], oldamount, data[11] });
			baseDao.execute("update accountregister set ar_accountrate=(select nvl(cm_crrate,0) from currencysmonth where ar_accountcurrency=cm_crname and to_char(ar_date,'yyyymm')=cm_yearmonth) where ar_code='"
					+ code + "'");
			String insertDetSql = "insert into AccountRegisterDetail (ard_detno,ard_debit,ard_id,ard_arid,ard_explanation,ard_catecode) values (?,?,?,?,?,?)";
			String insertAssDetSql = "insert into accountregisterdetailass(ars_id,ars_ardid,ars_detno,ars_asstype,ars_asscode,ars_assname,ars_type) values (?,?,?,?,?,?,'AccountRegister!Bank')";
			SqlRowList fpd = baseDao.queryForRowSet(
					"select * from FeePleaseDetail left join Category on fpd_catecode=ca_code where fpd_fpid=?", id);
			while (fpd.next()) {
				int ardid = baseDao.getSeqId("ACCOUNTREGISTERDETAIL_SEQ");
				Object catecode = fpd.getObject("fpd_catecode");
				baseDao.execute(insertDetSql, new Object[] { fpd.getGeneralInt("fpd_detno"), fpd.getGeneralDouble("fpd_total"), ardid,
						ar_id, fpd.getObject("fpd_d7"), catecode });
				if (catecode != null) {
					SqlRowList ass = baseDao.queryForRowSet("select ca_assname from category where ca_code=? and nvl(ca_assname,' ')<>' '",
							catecode);
					if (ass.next()) {
						String assStr = ass.getString("ca_assname");
						String[] codes = assStr.split("#");
						for (String assname : codes) {
							Object maxno = baseDao.getFieldDataByCondition("accountregisterdetailass", "max(nvl(ars_detno,0))",
									"ars_ardid=" + ardid);
							maxno = maxno == null ? 0 : maxno;
							int detno = Integer.parseInt(maxno.toString()) + 1;
							int arsid = baseDao.getSeqId("ACCOUNTREGISTERDETAILASS_SEQ");
							if ("部门".equals(assname)) {
								baseDao.execute(insertAssDetSql, new Object[] { arsid, ardid, detno, assname, data[3], data[2] });
							}
							if ("员工".equals(assname)) {
								baseDao.execute(insertAssDetSql, new Object[] { arsid, ardid, detno, assname, data[9], data[5] });
							}
							if ("项目".equals(assname)) {
								if (StringUtil.hasText(fpd.getObject("fpd_prjcode"))) {
									baseDao.execute(
											insertAssDetSql,
											new Object[] { arsid, ardid, detno, assname, fpd.getObject("fpd_prjcode"),
													fpd.getObject("fpd_prjname") });
								} else {
									baseDao.execute(insertAssDetSql, new Object[] { arsid, ardid, detno, assname, data[7], data[8] });
								}
							}
							if ("销售合同".equals(assname)) {
								baseDao.execute(insertAssDetSql, new Object[] { arsid, ardid, detno, assname,
										fpd.getObject("fpd_billcode"), fpd.getObject("fpd_billcode") });
							}
							if ("费用项目".equals(assname)) {
								Object asscode = null;
								if (StringUtil.hasText(fpd.getObject("fpd_d1"))) {
									asscode = baseDao.getFieldDataByCondition("asskind left join asskinddetail on ak_id=akd_akid",
											"akd_asscode", "ak_code='FeeOtp' and akd_assname='" + fpd.getObject("fpd_d1") + "'");
								}
								baseDao.execute(insertAssDetSql,
										new Object[] { arsid, ardid, detno, assname, asscode, fpd.getObject("fpd_d1") });
							}
							if ("营业费用类别".equals(assname)) {
								baseDao.execute(
										insertAssDetSql,
										new Object[] { arsid, ardid, detno, assname, fpd.getObject("fpd_businesscode"),
												fpd.getObject("fpd_businessname") });
							}
						}
					}

				}
			}
			String adidstr = "";
			SqlRowList feeback = baseDao.queryForRowSet("select * from FeeBack where fb_fpid=? and nvl(fb_jksqcode,' ')<>' '", id);
			while (feeback.next()) {
				baseDao.execute("update FeePlease set fp_departmentcode=(select max(dp_code) from department where fp_department=dp_name) where fp_departmentcode is null and fp_code='"
						+ feeback.getObject("fb_jksqcode") + "' and fp_kind='借款申请单'");
				Object[] jksq = baseDao.getFieldsDataByCondition("FeePlease", new String[] { "fp_code", "fp_n3", "fp_department",
						"fp_departmentcode", "fp_remark", "fp_pleaseman", "fp_prjcode", "fp_prjname", "fp_v1", "fp_vendcode",
						"fp_vendname", "fp_pleasemancode", "fp_id", "fp_catecode" }, "fp_code='" + feeback.getObject("fb_jksqcode")
						+ "' and fp_kind='借款申请单'");
				String insertDetSql2 = "insert into AccountRegisterDetail (ard_id,ard_arid,ard_detno,ard_credit,ard_nowbalance, ard_explanation, ard_catecode) values (?,?,?,?,?,?,?)";
				if (jksq != null) {
					int ardid = baseDao.getSeqId("ACCOUNTREGISTERDETAIL_SEQ");
					baseDao.execute(
							insertDetSql2,
							new Object[] { ardid, ar_id,
									baseDao.getFieldDataByCondition("ACCOUNTREGISTERDETAIL", "max(ard_detno)+1", "ard_arid=" + ar_id),
									feeback.getGeneralDouble("fb_back"), jksq[1], "借款申请单" + jksq[0], jksq[13] });
					adidstr += "," + jksq[12].toString();
					Object catecode = jksq[13];
					if (catecode != null) {
						SqlRowList ass = baseDao.queryForRowSet(
								"select ca_assname from category where ca_code=? and nvl(ca_assname,' ')<>' '", catecode);
						if (ass.next()) {
							String assStr = ass.getString("ca_assname");
							String[] codes = assStr.split("#");
							for (String assname : codes) {
								Object maxno = baseDao.getFieldDataByCondition("accountregisterdetailass", "max(nvl(ars_detno,0))",
										"ars_ardid=" + ardid);
								maxno = maxno == null ? 0 : maxno;
								int detno = Integer.parseInt(maxno.toString()) + 1;
								int arsid = baseDao.getSeqId("ACCOUNTREGISTERDETAILASS_SEQ");
								if ("部门".equals(assname)) {
									baseDao.execute(insertAssDetSql, new Object[] { arsid, ardid, detno, assname, jksq[3], jksq[2] });
								}
								if ("员工".equals(assname)) {
									baseDao.execute(insertAssDetSql, new Object[] { arsid, ardid, detno, assname, jksq[11], jksq[5] });
								}
								if ("项目".equals(assname)) {
									baseDao.execute(insertAssDetSql, new Object[] { arsid, ardid, detno, assname, jksq[6], jksq[7] });
								}
							}
						}
					}
				}
			}
			adidstr += "," + id;
			if (!adidstr.equals("")) {
				adidstr = adidstr.substring(1);
			}
			baseDao.execute("update accountregisterdetail set ard_catedesc=(select ca_description from category where ard_catecode=ca_code) where ard_arid="
					+ ar_id + " and nvl(ard_catecode,' ')<>' '");
			baseDao.execute("update accountregister set ar_currencytype=(select max(ca_currencytype) from category where ca_code in (select ard_catecode from accountregisterdetail where ar_id=ard_arid and nvl(ard_catecode,' ')<>' ')) where ar_id="
					+ ar_id);
			SqlRowList rs = baseDao.queryForRowSet("select * from accountregister where ar_id=? and nvl(ar_currencytype,0)<>0", ar_id);
			if (rs.next()) {
				baseDao.execute("update accountregisterdetail set ard_currency=(select fp_v13 from feeplease where fp_id in (" + id
						+ ")) where ard_arid=" + ar_id);
				baseDao.execute("update accountregisterdetail set ard_rate=(select nvl(cm_crrate,0) from currencysmonth where cm_yearmonth=to_char(sysdate,'yyyymm') and ard_currency=cm_crname) where ard_arid="
						+ ar_id + " and nvl(ard_currency,' ')<>' '");
				baseDao.execute("update accountregisterdetail set ard_doubledebit=ard_debit where ard_arid=" + ar_id
						+ " and nvl(ard_currency,' ')<>' '");
				baseDao.execute("update accountregisterdetail set ard_doublecredit=ard_credit where ard_arid=" + ar_id
						+ " and nvl(ard_currency,' ')<>' '");
				baseDao.execute("update accountregisterdetail set ard_debit=round(ard_doubledebit*ard_rate,2),ard_credit=round(ard_doublecredit*ard_rate,2) where ard_arid="
						+ ar_id + " and nvl(ard_currency,' ')<>' '");
			}
			success = true;
		} else if ("FeePlease!JKSQ".equals(caller)) {// 借款申请
			Object[] data = baseDao.getFieldsDataByCondition("FeePlease left join department on fp_department=dp_name", new String[] {
					"fp_code", "fp_pleaseamount", "fp_department", "dp_code", "fp_remark", "fp_pleaseman", "fp_prjcode", "fp_prjname",
					"fp_v1", "fp_vendcode", "fp_vendname", "fp_pleasemancode", "fp_catecode", "fp_cop" }, "fp_id=" + id);
			String insertSql = "insert into AccountRegister (ar_id,ar_recorddate,ar_date,ar_payment,ar_type,"
					+ "ar_code,ar_sourceid,ar_source,ar_sourcetype,ar_statuscode,ar_status,ar_recordman,"
					+ "ar_accountcode,ar_accountname,ar_accountcurrency,ar_cateid,ar_departmentcode,"
					+ "ar_departmentname,ar_pleaseman,ar_memo,ar_prjcode,ar_prjname,ar_apamount,ar_cop) values("
					+ "?,sysdate,sysdate,?,?,?,?,?,?,'ENTERING',?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			String remark = data[5] + "借款（" + data[4] + "）";// 银行登记备注默认为申请人+借款+原单据备注，若原备注为空，则备注为申请人+报销
			if (!StringUtil.hasText(data[4])) {
				remark = data[5] + "借款";
			}
			baseDao.execute(insertSql,
					new Object[] { ar_id, thispayamount, "其它付款", code, id, data[0], "借款申请单", BaseUtil.getLocalMessage("ENTERING"),
							SystemSession.getUser().getEm_name(), paymentcode, payment, cate[1], cate[0], data[3], data[2], data[5],
							remark, data[6], data[7], oldamount, data[13] });
			baseDao.execute("update accountregister set ar_accountrate=(select nvl(cm_crrate,0) from currencysmonth where ar_accountcurrency=cm_crname and to_char(ar_date,'yyyymm')=cm_yearmonth) where ar_code='"
					+ code + "'");
			String insertDetSql = "insert into AccountRegisterDetail (ard_id,ard_arid,ard_detno,ard_debit,ard_catecode,"
					+ "ard_nowbalance) values (ACCOUNTREGISTERDETAIL_SEQ.NEXTVAL,?,1,?,?,?)";
			if (data[12] != null) {
				baseDao.execute(insertDetSql, new Object[] { ar_id, thispayamount, data[12], data[1] });
				baseDao.execute("update AccountRegisterDetail set ard_catedesc=(select ca_description from category where ard_catecode=ca_code) where ard_arid="
						+ ar_id + " and nvl(ard_catecode,' ')<>' '");
				baseDao.execute("update accountregister set ar_currencytype=(select max(ca_currencytype) from category where ca_code in (select ard_catecode from accountregisterdetail where ar_id=ard_arid and nvl(ard_catecode,' ')<>' ')) where ar_id="
						+ ar_id);
				SqlRowList rs1 = baseDao.queryForRowSet("select * from accountregister where ar_id=? and nvl(ar_currencytype,0)<>0", ar_id);
				if (rs1.next()) {
					baseDao.execute("update accountregisterdetail set ard_doubledebit=ard_debit where ard_arid=" + ar_id
							+ " and nvl(ard_catecode,' ')<>' '");
					baseDao.execute("update accountregisterdetail set ard_currency=(select fp_v13 from feeplease where fp_id=" + id
							+ ") where ard_arid=" + ar_id + " and nvl(ard_catecode,' ')<>' '");
					baseDao.execute("update accountregisterdetail set ard_rate=(select nvl(cm_crrate,0) from currencysmonth where cm_yearmonth=to_char(sysdate,'yyyymm') and ard_currency=cm_crname) where ard_arid="
							+ ar_id + " and nvl(ard_catecode,' ')<>' '");
					baseDao.execute("update accountregisterdetail set ard_debit=round(ard_doubledebit*ard_rate,2) where ard_arid=" + ar_id
							+ " and nvl(ard_catecode,' ')<>' '");
				}
				// 如果ca_asstype为部门或者员工则插入accountregisterdetailass表
				String sql1 = "insert into accountregisterdetailass(ars_id,ars_ardid,ars_detno,ars_asstype,ars_asscode,ars_assname,ars_type) "
						+ "select accountregisterdetailass_seq.nextval,ard_id,1,'员工','"
						+ data[11]
						+ "','"
						+ data[5]
						+ "','AccountRegister!Bank' from accountregisterdetail,category where ard_catecode=ca_code and (ca_asstype like '%Empl%') and ard_arid="
						+ ar_id;
				String sql2 = "insert into accountregisterdetailass(ars_id,ars_ardid,ars_detno,ars_asstype,ars_asscode,ars_assname,ars_type) "
						+ "select accountregisterdetailass_seq.nextval,ard_id,2,'其它应收往来','"
						+ data[9]
						+ "','"
						+ data[10]
						+ "','AccountRegister!Bank' from accountregisterdetail,category where ard_catecode=ca_code and (ca_asstype like '%Otc%') and ard_arid="
						+ ar_id;
				String sql3 = "insert into accountregisterdetailass(ars_id,ars_ardid,ars_detno,ars_asstype,ars_asscode,ars_assname,ars_type) "
						+ "select accountregisterdetailass_seq.nextval,ard_id,3,'项目','"
						+ data[6]
						+ "','"
						+ data[7]
						+ "','AccountRegister!Bank' from accountregisterdetail,category where ard_catecode=ca_code and REGEXP_LIKE(ca_asstype, '(#|^)Otp') and ard_arid="
						+ ar_id;
				baseDao.execute(sql1);
				baseDao.execute(sql2);
				baseDao.execute(sql3);
			}
			success = true;
		} else if ("FeePlease!HKSQ".equals(caller)) {// 还款申请
			Object[] data = baseDao.getFieldsDataByCondition("FeePlease left join department on fp_department=dp_name", new String[] {
					"fp_code", "fp_pleaseamount", "fp_department", "dp_code", "fp_remark", "fp_pleaseman", "fp_prjcode", "fp_prjname",
					"fp_sourcecode", "fp_cop" }, "fp_id=" + id);
			String insertSql = "insert into AccountRegister (ar_id,ar_recorddate,ar_date,ar_deposit,ar_type,"
					+ "ar_code,ar_sourceid,ar_source,ar_sourcetype,ar_statuscode,ar_status,ar_recordman,"
					+ "ar_accountcode,ar_accountname,ar_accountcurrency,ar_cateid,ar_departmentcode,ar_departmentname,"
					+ "ar_pleaseman,ar_memo,ar_prjcode,ar_prjname,ar_apamount,ar_cop) values("
					+ "?,sysdate,sysdate,?,?,?,?,?,?,'ENTERING',?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			String remark = data[5] + "还款（" + data[4] + "）";// 银行登记备注默认为申请人+还款+原单据备注，若原备注为空，则备注为申请人+报销
			if (!StringUtil.hasText(data[4])) {
				remark = data[5] + "还款";
			}
			baseDao.execute(insertSql,
					new Object[] { ar_id, thispayamount, "其它收款", code, id, data[0], "还款申请单", BaseUtil.getLocalMessage("ENTERING"),
							SystemSession.getUser().getEm_name(), paymentcode, payment, cate[1], cate[0], data[3], data[2], data[5],
							remark, data[6], data[7], oldamount, data[9] });
			baseDao.execute("update accountregister set ar_accountrate=(select nvl(cm_crrate,0) from currencysmonth where ar_accountcurrency=cm_crname and to_char(ar_date,'yyyymm')=cm_yearmonth) where ar_code='"
					+ code + "'");
			String adidstr = "";
			if (data[8] != null) {
				Object[] data2 = baseDao.getFieldsDataByCondition("FeePlease left join department on fp_department=dp_name", new String[] {
						"fp_code", "fp_pleaseamount", "fp_department", "dp_code", "fp_remark", "fp_pleaseman", "fp_prjcode", "fp_prjname",
						"fp_v1", "fp_vendcode", "fp_vendname", "fp_pleasemancode", "fp_id", "fp_catecode" }, "fp_code='" + data[8]
						+ "' and fp_kind='借款申请单'");
				String insertDetSql = "insert into AccountRegisterDetail (ard_id,ard_arid,ard_detno,ard_credit,ard_catecode,"
						+ "ard_nowbalance) values (ACCOUNTREGISTERDETAIL_SEQ.NEXTVAL,?,1,?,?,?)";
				adidstr += data2[12].toString();
				if (data2[13] != null) {
					baseDao.execute(insertDetSql, new Object[] { ar_id, thispayamount, data2[13], data2[1] });
					baseDao.execute("update AccountRegisterDetail set ard_catedesc=(select ca_description from category where ca_code=ard_catecode) where ard_arid="
							+ ar_id + " and nvl(ard_catecode,' ')<>' '");
					baseDao.execute("update accountregister set ar_currencytype=(select max(ca_currencytype) from category where ca_code in (select ard_catecode from accountregisterdetail where ar_id=ard_arid and nvl(ard_catecode,' ')<>' ')) where ar_id="
							+ ar_id);
					adidstr += id;
					if (!adidstr.equals("")) {
						adidstr = adidstr.substring(1);
					}
					SqlRowList rs1 = baseDao.queryForRowSet("select * from accountregister where ar_id=? and nvl(ar_currencytype,0)<>0",
							ar_id);
					if (rs1.next()) {
						baseDao.execute("update accountregisterdetail set ard_currency=(select fp_v13 from feeplease where fp_id in ("
								+ adidstr + ")) where ard_arid=" + ar_id);
						baseDao.execute("update accountregisterdetail set ard_doubledebit=ard_debit where ard_arid=" + ar_id
								+ " and nvl(ard_currency,' ')<>' '");
						baseDao.execute("update accountregisterdetail set ard_rate=(select nvl(cm_crrate,0) from currencysmonth where cm_yearmonth=to_char(sysdate,'yyyymm') and ard_currency=cm_crname) where ard_arid="
								+ ar_id + " and nvl(ard_currency,' ')<>' '");
						baseDao.execute("update accountregisterdetail set ard_debit=round(ard_doubledebit*ard_rate,2) where ard_arid="
								+ ar_id + " and nvl(ard_currency,' ')<>' '");
					}
					// 如果ca_asstype为部门或者员工则插入accountregisterdetailass表
					String sql1 = "insert into accountregisterdetailass(ars_id,ars_ardid,ars_detno,ars_asstype,ars_asscode,ars_assname,ars_type) "
							+ "select accountregisterdetailass_seq.nextval,ard_id,1,'员工','"
							+ data2[11]
							+ "','"
							+ data2[5]
							+ "','AccountRegister!Bank' from accountregisterdetail,category where ard_catecode=ca_code and (ca_asstype like '%Empl%') and ard_arid="
							+ ar_id;
					String sql2 = "insert into accountregisterdetailass(ars_id,ars_ardid,ars_detno,ars_asstype,ars_asscode,ars_assname,ars_type) "
							+ "select accountregisterdetailass_seq.nextval,ard_id,2,'其它应收往来','"
							+ data2[9]
							+ "','"
							+ data2[10]
							+ "','AccountRegister!Bank' from accountregisterdetail,category where ard_catecode=ca_code and (ca_asstype like '%Otc%') and ard_arid="
							+ ar_id;
					String sql3 = "insert into accountregisterdetailass(ars_id,ars_ardid,ars_detno,ars_asstype,ars_asscode,ars_assname,ars_type) "
							+ "select accountregisterdetailass_seq.nextval,ard_id,3,'项目','"
							+ data[6]
							+ "','"
							+ data[7]
							+ "','AccountRegister!Bank' from accountregisterdetail,category where ard_catecode=ca_code and REGEXP_LIKE(ca_asstype, '(#|^)Otp') and ard_arid="
							+ ar_id;
					baseDao.execute(sql1);
					baseDao.execute(sql2);
					baseDao.execute(sql3);
				}
			}
			baseDao.execute("update accountregisterdetail set ard_catedesc=(select ca_description from category where ard_catecode=ca_code) where ard_arid="
					+ ar_id + " and nvl(ard_catecode,' ')<>' '");
			success = true;
		} else if ("FeePlease!ZWSQ".equals(caller)) {// 总务报销单
			Object[] data = baseDao.getFieldsDataByCondition("FeePlease left join department on fp_department=dp_name", new String[] {
					"fp_code", "fp_pleaseamount", "fp_department", "dp_code", "fp_remark", "fp_pleaseman", "fp_prjcode", "fp_prjname",
					"fp_v9", "fp_v3", "fp_cop" }, "fp_id=" + id);
			String insertSql = "insert into AccountRegister (ar_id,ar_recorddate,ar_date,ar_payment,ar_type,"
					+ "ar_code,ar_sourceid,ar_source,ar_sourcetype,ar_statuscode,ar_status,ar_recordman,"
					+ "ar_accountcode,ar_accountname,ar_accountcurrency,ar_cateid,ar_departmentcode,ar_departmentname,"
					+ "ar_pleaseman,ar_memo,ar_prjcode,ar_prjname,ar_apamount,ar_cop) values("
					+ "?,sysdate,sysdate,?,?,?,?,?,?,'ENTERING',?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			String remark = data[5].toString();
			if (StringUtil.hasText(data[8])) {
				remark = remark + ";" + data[8];
			}
			if (StringUtil.hasText(data[9])) {
				remark = remark + ";" + data[9];
			}
			baseDao.execute(insertSql,
					new Object[] { ar_id, thispayamount, "其它付款", code, id, data[0], "总务申请单", BaseUtil.getLocalMessage("ENTERING"),
							SystemSession.getUser().getEm_name(), paymentcode, payment, cate[1], cate[0], data[3], data[2], data[5],
							remark, data[6], data[7], oldamount, data[10] });
			baseDao.execute("update accountregister set ar_accountrate=(select nvl(cm_crrate,0) from currencysmonth where ar_accountcurrency=cm_crname and to_char(ar_date,'yyyymm')=cm_yearmonth) where ar_code='"
					+ code + "'");
			baseDao.execute("update accountregisterdetail set ard_catedesc=(select ca_description from category where ard_catecode=ca_code) where ard_arid="
					+ ar_id + " and nvl(ard_catecode,' ')<>' '");
			success = true;
		} else {
			BaseUtil.showError("此类型不能转银行登记");
		}
		if (success) {
			baseDao.updateByCondition("FeePlease", "fp_n1=fp_n1+" + oldamount, "fp_id=" + id);
			if ("FeePlease!FYBX".equals(caller)) {
				baseDao.execute("update FeePlease set fp_v7='已支付' where nvl(fp_pleaseamount,0)=nvl(fp_n1,0)+nvl(fp_n6,0) and fp_id=" + id);
				baseDao.execute("update FeePlease set fp_v7='部分支付' where nvl(fp_pleaseamount,0)>nvl(fp_n1,0)+nvl(fp_n6,0) and fp_id=" + id);
				baseDao.execute("update FeePlease set fp_v7='未支付' where nvl(fp_n1,0)+nvl(fp_n6,0)=0 and fp_id=" + id);
			} else {
				baseDao.execute("update FeePlease set fp_v7=case when fp_pleaseamount=fp_n1 then '已支付' else '部分支付' end where fp_id=" + id);
			}
			// 费用报销,差旅费转银行登记时间记录
			if ("FeePlease!FYBX".equals(caller) || "FeePlease!CLFBX".equals(caller)) {
				baseDao.execute("update FeePlease set fp_paydate=sysdate where fp_id=" + id);
			}
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "转银行登记,本次金额为:" + thispayamount, "转入成功", caller
					+ "|fp_id=" + id));
			accountRegisterBankService.updateErrorString(ar_id);
			return "转入成功,银行登记单号:<a href=\"javascript:openUrl('jsps/fa/gs/accountRegister.jsp?formCondition=ar_idIS" + ar_id
					+ "&gridCondition=ard_aridIS" + ar_id + "&whoami=AccountRegister!Bank')\">" + code + "</a>&nbsp;";
		}
		return "";
	}

	@Override
	public int turnFYBX2(int fp_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("FeePlease", "fp_statuscode", "fp_id=" + fp_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError("审核后才能转银行登记");
		}
		int id = baseDao.getSeqId("FeePlease_seq");
		String code = baseDao.sGetMaxNumber("FeePlease!FYBX", 2);
		String sql = "insert into FeePlease (fp_id,fp_code,fp_status,fp_statuscode,fp_kind,fp_recorddate,fp_recordman,fp_pleaseman,"
				+ "fp_department,fp_item,fp_v7,fp_sourcecode,fp_sourcekind,fp_remark,fp_n2) select " + id + ",'" + code
				+ "','在录入','ENTERING','费用报销单',sysdate,'" + SystemSession.getUser().getEm_name()
				+ "',fp_pleaseman,fp_department,fp_item,'未支付',fp_code,'业务招待费申请单',fp_remark,0 from FeePlease where fp_id=" + fp_id;
		baseDao.execute(sql);
		String detSql = "insert into FeePleaseDetail(fpd_id,fpd_fpid,fpd_detno,fpd_d1,fpd_n2,fpd_n1) select FeePleaseDetail_seq.nextval,"
				+ id + ",fpd_detno,fpd_d1,fpd_total,fpd_n1" + " from FeePleaseDetail where fpd_fpid=" + fp_id;
		baseDao.execute(detSql);
		baseDao.updateByCondition("FeePlease", "fp_v11='已转费用申请'", "fp_id=" + fp_id);
		return id;
	}

	@Override
	public void updateFactdays(String data) {
		Map<Object, Object> formdata = BaseUtil.parseFormStoreToMap(data);
		int ISAllUpdate = Integer.parseInt(formdata.get("allupdate").toString());
		StringBuffer sb = new StringBuffer();
		Object factdays = formdata.get("factdays");
		if (factdays != null && !"".equals(factdays.toString()) && !"null".equals(factdays.toString())) {
			sb.append("fpd_n6=").append(factdays);
		}
		String updateSql = "update FeePleaseDetail set " + sb.toString();
		Object fp_id = formdata.get("fpd_fpid");
		if (ISAllUpdate == 1) {
			updateSql = updateSql + " WHERE fpd_fpid =" + fp_id;
			SqlRowList rs = baseDao.queryForRowSet("select fpd_detno,fpd_n6 from FeePleaseDetail where fpd_fpid=? order by fpd_detno",
					fp_id);
			while (rs.next()) {
				// 记录操作
				if (factdays != null && !"".equals(factdays.toString()) && !"null".equals(factdays.toString())) {
					baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "更新实际天数", "行" + rs.getInt(1) + ":"
							+ rs.getObject(2) + "=>" + factdays, "FeePlease!CCSQ|fp_id=" + fp_id));
				}
			}
		} else {
			updateSql = updateSql + " WHERE fpd_id=" + formdata.get("fpd_id");
			SqlRowList rs = baseDao.queryForRowSet("select fpd_detno,fpd_n6,fpd_fpid from FeePleaseDetail where fpd_id=?",
					formdata.get("fpd_id"));
			if (rs.next()) {
				// 记录操作
				if (factdays != null && !"".equals(factdays.toString()) && !"null".equals(factdays.toString())) {
					baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "更新实际天数", "行" + rs.getInt(1) + ":"
							+ rs.getObject(2) + "=>" + factdays, "FeePlease!CCSQ|fp_id=" + rs.getInt(3)));
				}
			}
		}
		baseDao.execute(updateSql);
	}

	private void comitLogic_feeCategory(int fp_id) {
		StringBuffer sb = new StringBuffer();
		// 差旅费报销单,费用报销单：提交之前判断费用科目是否正确,如果正确则更新fpd_catecode字段
		int isRight = baseDao.getCount("select count(1) from feeplease where fp_pleaseamount=0 and fp_id=" + fp_id);
		if (isRight != 0) {
			BaseUtil.showError("申请金额为0，不允许提交！");
		}
		Object dept = baseDao.getFieldDataByCondition("feeplease", "fp_department", "fp_id=" + fp_id);
		SqlRowList rs1 = baseDao.queryForRowSet("select fpd_detno,fpd_d1,fpd_id from FeePleaseDetail where fpd_fpid=?", fp_id);
		while (rs1.next()) {
			SqlRowList rs = baseDao.queryForRowSet(
					"select fcs_departmentname from FeeCategorySet where fcs_departmentname=? and fcs_itemname=?", dept,
					rs1.getObject("fpd_d1"));
			if (rs.next()) {

			} else {
				sb.append("第" + rs1.getObject("fpd_detno") + "行部门[" + dept + "]费用用途[" + rs1.getObject("fpd_d1") + "]在费用申请科目没有设置，不能提交");
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}
		baseDao.execute("update FeePleaseDetail set fpd_catecode=(select max(fcs_catecode) from FeeCategorySet where fcs_departmentname='"
				+ dept + "' and fcs_itemname=fpd_d1) where fpd_fpid=" + fp_id);
	}

	private void comitLogic_feelimit(int fp_id, String caller) {
		// 费用报销单,差旅费报销单:报销日期所在的月份是否小于当前额度表的最大月份，小于不能提交
		int month = baseDao.getCount("select max(FL_YEARMONTH) from feelimit");
		int yd = baseDao.getCount("select to_char(fp_recorddate,'yyyymm') from feeplease where fp_id=" + fp_id);
		if (yd < month) {
			BaseUtil.showError("报销日期所在的月份小于当前额度表的最大月份,不能提交!");
		}
		// 计算明细中的所有费用
		String sql1 = "update FeePleaseDetail set fpd_total=fpd_n2 where fpd_total=0 and fpd_fpid=" + fp_id;
		baseDao.execute(sql1);
		String sql = "update FeePleaseDetail set fpd_n1=fpd_total where (nvl(fpd_n1,0)=0 or nvl(fpd_n1,0)<>fpd_total)  and fpd_fpid="
				+ fp_id;
		baseDao.execute(sql);
		baseDao.updateByCondition("FeePlease",
				"fp_pleaseamount=" + baseDao.getFieldDataByCondition("feepleasedetail", "sum(fpd_n1)", "fpd_fpid=" + fp_id), "fp_id="
						+ fp_id);
		baseDao.updateByCondition("FeePlease", "fp_v7='未支付'", "fp_v7 is null and fp_id=" + fp_id);
		if ("FeePlease!FYBX".equals(caller)) {
			baseDao.execute("update FeePlease set fp_v7='已支付' where nvl(fp_pleaseamount,0)=nvl(fp_n1,0)+nvl(fp_n6,0) and fp_kind='费用报销单' and fp_id="
					+ fp_id);
			baseDao.execute("update FeePlease set fp_v7='部分支付' where nvl(fp_pleaseamount,0)>nvl(fp_n1,0)+nvl(fp_n6,0) and fp_kind='费用报销单' and fp_id="
					+ fp_id);
			baseDao.execute("update FeePlease set fp_v7='未支付' where nvl(fp_n1,0)+nvl(fp_n6,0)=0 and fp_kind='费用报销单' and fp_id=" + fp_id);
		}
		// 费用报销单:是否超额度
		// ps:费用报销和差旅费报销的是否超额度是不同的!
		Object[] feePleasedata = baseDao.getFieldsDataByCondition("feeplease", new String[] { "fp_class", "fp_department",
				"fp_pleaseamount" }, "fp_id=" + fp_id);
		Object type = feePleasedata[0];
		String errorString = null;
		Object[] data = baseDao.getFieldsDataByCondition("feeplease", new String[] { "fp_pleaseman", "fp_recorddate", "fp_department",
				"fp_class" }, "fp_id=" + fp_id);
		if (data != null && data[3] != null) {
			if (!"其他".equals(data[3])) {
				Object[] limit = baseDao.getFieldsDataByCondition(
						// 抓取对应人员当月的对应类型额度
						"FeeLimitDetail left join FeeLimit on fl_id=fld_flid",
						new String[] { "nvl(fld_amount,0)", "nvl(fld_actamount,0)" }, "fl_yearmonth=" + yd + " and fld_emname='" + data[0]
								+ "' and fld_class='" + type + "' and fl_statuscode='AUDITED'");
				if (limit != null) {// 如果有设置额度则检查是否超额度
					Object count = feePleasedata[2];
					if (Double.parseDouble(limit[0] + "") < Double.parseDouble(limit[1] + "") + Double.parseDouble(count + "")) {
						errorString = "额度类型<" + type + ">申请金额已超本月额度！余额为："
								+ (Double.parseDouble(limit[0] + "") - Double.parseDouble(limit[1] + ""));
					}
				} else {
					errorString = "额度表没有相应记录，不予提交!";
				}
				if (errorString != null) {
					BaseUtil.showError(errorString);
				}
			}
		}
		// 费用报销单:添加都相应额度记录
		Object count1 = baseDao.getFieldDataByCondition("feepleasedetail", "sum(fpd_total)", "fpd_fpid=" + fp_id);
		Object id = baseDao.getFieldDataByCondition("FeeLimitDetail left join FeeLimit on fl_id=fld_flid", "fld_id",
				"nvl(fld_amount,0)-nvl(fld_actamount,0)-" + count1 + ">=0 and fl_yearmonth=" + yd + " and fld_emname='" + data[0]
						+ "' and fld_class='" + type + "' and fl_statuscode='AUDITED'");
		if (id != null) {
			baseDao.execute("update FeeLimitDetail set fld_actamount=nvl(fld_actamount,0)+" + count1 + " where fld_id=" + id);
		}
	}

	private void resCommitLogic(int fp_id) {
		// 反提交后添加都相应额度记录
		Object type = baseDao.getFieldDataByCondition("feeplease", "fp_class", "fp_id=" + fp_id);
		Object[] data = baseDao.getFieldsDataByCondition("feeplease", new String[] { "fp_pleaseman", "fp_billdate", "fp_department" },
				"fp_id=" + fp_id);
		int yd = Integer.parseInt(data[1].toString().substring(0, 7).replaceAll("-", ""));
		int fyd = baseDao.getCount("select max(FL_YEARMONTH) from feelimit");
		boolean isNextMonth = false;
		if (fyd > yd) {// 查看是否反提交的时候,已经是下一个月 了
			isNextMonth = true;
		}
		Object id = baseDao.getFieldDataByCondition("FeeLimitDetail left join FeeLimit on fl_id=fld_flid", "fld_id", "fl_yearmonth=" + yd
				+ " and fld_emname='" + data[0] + "' and fld_class='" + type + "' and fl_statuscode='AUDITED'");
		Object count = baseDao.getFieldDataByCondition("feepleasedetail", "sum(fpd_total)", "fpd_fpid=" + fp_id);
		if (id != null) {// 修改实际发生额度
			baseDao.execute("update FeeLimitDetail set fld_actamount=fld_actamount-" + count + " where fld_id=" + id);
		}
		// 如果反提交时,已经是另一个月了,则把额度添加到新的记录中
		if (isNextMonth) {
			Object fid = baseDao.getFieldDataByCondition("FeeLimitDetail left join FeeLimit on fl_id=fld_flid", "fld_id", "fl_yearmonth="
					+ fyd + " and fld_emname='" + data[0] + "' and fld_class='" + type + "' and fl_statuscode='AUDITED'");
			String sql = "update FeeLimitDetail set fld_amount=fld_amount+" + count + " where fld_id=" + fid;
			baseDao.execute(sql);
		}
	}

	@Override
	public List<JSONTree> getJsonTrees(int parentid) {
		List<JSONTree> tree = new ArrayList<JSONTree>();
		List<ContractType> list = contractTypeDao.getContractTypeByParentId(parentid);
		for (ContractType navigation : list) {
			tree.add(new JSONTree(navigation));
		}
		return tree;
	}

	@Override
	public Object getContractTypeNum(int id, String table) {
		String number = "";
		Object[] objs = baseDao.getFieldsDataByCondition("ContractType", "ct_pcode,ct_code,ct_wcodeset,ct_length,ct_name", "ct_id=" + id);
		int wcodeset = Integer.parseInt(objs[2].toString());
		int length = Integer.parseInt(objs[3].toString());
		if (length == 0) {
			BaseUtil.showError(objs[4].toString() + "流水码长度位0,请重新设置！");
		}
		Date dd = Calendar.getInstance().getTime();
		String yearmonth = new SimpleDateFormat("YYYYMM").format(dd);
		String year = new SimpleDateFormat("YYYY").format(dd);
		if (wcodeset == 1) {// 年流水
			table += year;
			number += year;
		} else if (wcodeset == 2) {// 年月流水
			table += yearmonth;
			number += yearmonth;
		}
		Object[] feeP = baseDao.getFieldsDataByCondition("FeePlease", "fp_code,fp_n1", "fp_n2=" + length + " and fp_v12='" + table
				+ "' order by fp_n1 desc");
		if (feeP != null) {
			String fp_n1 = String.valueOf(Integer.parseInt(feeP[1].toString()) + 1);// 000
			if (length > fp_n1.length()) {
				for (int i = 0; i < length - fp_n1.length(); i++) {
					number += "0";
				}
			} else if (length < fp_n1.length()) {
				BaseUtil.showError("当前流水码已达到最大值，请重新设置流水码长度！");
			}
			number += fp_n1;
		} else {
			if (length > 1) {
				for (int i = 0; i < length - 1; i++) {
					number += "0";
				}
			}
			number += 1;
		}
		return number;
	}

	@Override
	public Object getContractTypeNumByKind(String k1, String k2, String k3, String k4) {
		SqlRowList rs = baseDao.queryForRowSet("select ct_id,ct_code,ct_leaf,ct_length from ContractType where ct_name=?", k1);
		int length = 0;
		if (rs.next()) {
			String num = rs.getGeneralString("ct_code");
			if ("F".equals(rs.getString("ct_leaf"))) {
				SqlRowList rd = baseDao.queryForRowSet("select ct_id,ct_code,ct_leaf,ct_length from ContractType where ct_subof="
						+ rs.getInt("ct_id") + " and ct_name='" + ((k2 == null || "".equals(k2)) ? '无' : k2) + "'");
				if (rd.next()) {
					num += rd.getGeneralString("ct_code");
					if ("F".equals(rd.getString("ct_leaf"))) {
						SqlRowList rd1 = baseDao.queryForRowSet("select ct_id,ct_code,ct_leaf,ct_length from ContractType where ct_subof="
								+ rd.getInt("ct_id") + " and ct_name='" + ((k3 == null || "".equals(k3)) ? '无' : k3) + "'");
						if (rd1.next()) {
							num += rd1.getGeneralString("ct_code");
							if ("F".equals(rd1.getString("ct_leaf"))) {
								SqlRowList rd2 = baseDao
										.queryForRowSet("select ct_id,ct_code,ct_leaf,ct_length from ContractType where ct_subof="
												+ rd1.getInt("ct_id") + " and ct_name='" + ((k4 == null || "".equals(k4)) ? '无' : k4) + "'");
								if (rd2.next()) {
									num += rd2.getGeneralString("ct_code");
									num += getContractTypeNum(rd2.getInt("ct_id"), num);
									length = rd2.getInt("ct_length");
								}
							} else {
								num += getContractTypeNum(rd1.getInt("ct_id"), num);
								length = rd1.getInt("ct_length");
							}
						}
					} else {
						num += getContractTypeNum(rd.getInt("ct_id"), num);
						length = rd.getInt("ct_length");
					}
				}
			} else {
				num += getContractTypeNum(rs.getInt("ct_id"), num);
				length = rs.getInt("ct_length");
			}
			return num + ";" + length;
		}
		return null;

	}

	@Override
	public String vastTurnFYBX(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		if (BaseUtil.groupMap(maps, "CT_CALLER").keySet().size() == 1 && BaseUtil.groupMap(maps, "CT_DEPARTMENTCODE").keySet().size() == 1
				&& BaseUtil.groupMap(maps, "CT_DEPARTMENTNAME").keySet().size() == 1) {
			Object ct_caller = maps.get(0).get("CT_CALLER");
			Object ct_sourcekind = maps.get(0).get("CT_SOURCEKIND");
			Object ct_departmentname = maps.get(0).get("CT_DEPARTMENTNAME");
			Object ct_emname = maps.get(0).get("CT_EMNAME");
			int id = baseDao.getSeqId("FEEPLEASE_SEQ");
			String code = baseDao.sGetMaxNumber("FEEPLEASE", 2);
			Employee employee = SystemSession.getUser();
			double fp_pleaseamount = 0/* ,fp_n1 = 0 */;
			for (int i = 0; i < maps.size(); i++) {
				fp_pleaseamount += Double.parseDouble(maps.get(i).get("CT_ISTURN").toString());
				// fp_n1+=Double.parseDouble(maps.get(i).get("CT_TURNAMOUNT").toString());
				String sql = "insert into feepleasedetail (fpd_id,fpd_fpid,fpd_code,fpd_detno,fpd_n2,fpd_total,fpd_n1,fpd_class,fpd_d9) values ("
						+ baseDao.getSeqId("FEEPLEASEDETAIL_SEQ")
						+ ","
						+ id
						+ ",'"
						+ maps.get(i).get("CT_CODE")
						+ "',"
						+ (i + 1)
						+ ","
						+ maps.get(i).get("CT_ISTURN")
						+ ","
						+ maps.get(i).get("CT_ISTURN")
						+ ","
						+ maps.get(i).get("CT_ISTURN")
						+ ",'费用报销单','" + ct_caller + "')";
				baseDao.execute(sql);
				baseDao.updateByCondition("CUSTOMTABLE", "CT_TURNAMOUNT=NVL(CT_TURNAMOUNT,0)+" + maps.get(i).get("CT_ISTURN"), "CT_CODE='"
						+ maps.get(i).get("CT_CODE") + "' and CT_CALLER='" + maps.get(i).get("CT_CALLER") + "'");
			}
			String sqlstr = "insert into feeplease (fp_id,fp_code,fp_kind,fp_statuscode,fp_status,fp_recordman,fp_recorddate,fp_printstatuscode,fp_printstatus,fp_pleaseman,fp_department,fp_pleaseamount,fp_sourcekind,fp_remark,fp_sourcecaller) "
					+ "values ("
					+ id
					+ ",'"
					+ code
					+ "','费用报销单','ENTERING','"
					+ BaseUtil.getLocalMessage("ENTERING")
					+ "','"
					+ employee.getEm_name()
					+ "',sysdate,'UNPRINT','"
					+ BaseUtil.getLocalMessage("UNPRINT")
					+ "','"
					+ ct_emname
					+ "','"
					+ ct_departmentname + "'," + fp_pleaseamount + ",'" + ct_sourcekind + "','OA单据批量转费用报销单','" + ct_caller + "')";
			baseDao.execute(sqlstr);

			return "转费用报销单成功,报销单号:<a href=\"javascript:openUrl('jsps/oa/fee/feePleaseFYBX.jsp?whoami=FeePlease!FYBX&formCondition=fp_idIS"
					+ id + "&gridCondition=fpd_fpidIS" + id + "')\">" + code + "</a>&nbsp;";
		} else {
			return "请统一单据类型、报销部门和报销人";
		}
	}

	@Override
	public String sealTurnFYBX(int fp_id, String caller, double thispayamount) {
		Object status = baseDao.getFieldDataByCondition("FeePlease", "fp_statuscode", "fp_id=" + fp_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError("审核后才能转费用报销");
		}

		int id = baseDao.getSeqId("FeePlease_seq");
		String code = baseDao.sGetMaxNumber("FeePlease!FYBX", 2);
		String sql = "insert into FeePlease (fp_id,fp_code,fp_status,fp_statuscode,fp_kind,fp_recorddate,fp_recordman,fp_pleaseman,"
				+ "fp_department,fp_v7,fp_sourcecaller,fp_sourcecode,fp_sourcekind,fp_pleaseamount,fp_remark,fp_n2) select " + id + ",'"
				+ code + "','在录入','ENTERING','费用报销单',sysdate,'" + SystemSession.getUser().getEm_name()
				+ "',fp_pleaseman,fp_department,'未支付','" + caller + "',fp_code,fp_kind," + thispayamount
				+ ",'公章用印申请转费用报销',0 from FeePlease where fp_id=" + fp_id;
		baseDao.execute(sql);
		String detSql = "insert into FeePleaseDetail(fpd_id,fpd_fpid,fpd_detno,fpd_code,fpd_class,fpd_n2,fpd_total,fpd_n1,fpd_d9) select "
				+ baseDao.getSeqId("FEEPLEASEDETAIL_SEQ") + "," + id + "," + 1 + ",fp_code,'费用报销单'," + thispayamount + "," + thispayamount
				+ "," + thispayamount + ",'" + caller + "' from FeePlease where fp_id=" + fp_id;
		baseDao.execute(detSql);
		baseDao.updateByCondition("FeePlease", "fp_n4=nvl(fp_n4,0)+" + thispayamount, "fp_id=" + fp_id);
		return "转费用报销单成功,报销单号:<a href=\"javascript:openUrl('jsps/oa/fee/feePleaseFYBX.jsp?whoami=FeePlease!FYBX&formCondition=fp_idIS" + id
				+ "&gridCondition=fpd_fpidIS" + id + "')\">" + code + "</a>&nbsp;";
	}

	@Override
	public JSONObject getFeeAccount(String emname) {
		JSONObject obj = new JSONObject();
		SqlRowList sl = baseDao
				.queryForRowSet("select  fp_v6,fp_v8,fp_v9 from feeplease where nvl(fp_kind,' ') in ('差旅费报销单','费用报销单') and fp_pleaseman='"
						+ emname + "' order by fp_id desc");
		if (sl.next()) {
			obj.put("fp_v6", sl.getObject(1));
			obj.put("fp_v8", sl.getObject(2));
			obj.put("fp_v9", sl.getObject(3));
		}
		return obj;
	}

	// 提交前判断是否超过系统设置的可延期天数
	private void commitNeedCheck(String caller, Object id) {
		if (baseDao.isDBSetting(caller, "commitNeedCheck")) {
			String days = baseDao.getDBSetting(caller, "SetDelayDays");
			Object time = null;
			if (days != null && Integer.parseInt(days) > 0) {
				if ("FeePlease!CCSQ".equals(caller)) {
					time = baseDao.getFieldDataByCondition("FeePlease left join FeePleaseDetail on fp_id=fpd_fpid",
							"to_char(min(fpd_date1),'yyyy-mm-dd')", "fp_id=" + id);
				} else if ("FeePlease!CCSQ!new".equals(caller)) {
					time = baseDao.getFieldDataByCondition("FeePlease ", "to_char(fp_preenddate,'yyyy-mm-dd')", "fp_id=" + id);
				}
				boolean bool = baseDao.checkIf("dual", "DAY_COUNT(to_date('" + time.toString() + "','yyyy-mm-dd'),sysdate)-1>" + days);
				if (bool)
					BaseUtil.showError("系统设置的延期提交天数为" + days + "天，已超过不允许提交！");
			}
		}
	}

	@Override
	public List<JSONTree> getJSONTreeBySearch(String search, Employee employee) {
		List<JSONTree> tree = new ArrayList<JSONTree>();
		Set<ContractType> list = contractTypeDao.getContractTypeBySearch(search);
		for (ContractType s : list) {
			JSONTree ct = new JSONTree();
			if (s.getCt_subof() == 0) {
				ct = recursionFn(list, s);
				tree.add(ct);
			}
		}
		return tree;
	}

	private JSONTree recursionFn(Collection<ContractType> list, ContractType s) {
		JSONTree jt = new JSONTree();
		jt.setId(String.valueOf(s.getCt_id()));
		jt.setParentId(String.valueOf(s.getCt_subof()));
		jt.setText(s.getCt_name());
		jt.setQtip(s.getCt_code());

		if (s.getCt_leaf().equals("F")) {
			if (s.getCt_subof() == 0) {
				jt.setCls("x-tree-cls-root");
			} else {
				jt.setCls("x-tree-cls-parent");
			}
			jt.setAllowDrag(false);
			jt.setLeaf(false);
			List<ContractType> childList = getChildList(list, s);
			Iterator<ContractType> it = childList.iterator();
			List<JSONTree> children = new ArrayList<JSONTree>();
			JSONTree ct = new JSONTree();
			while (it.hasNext()) {
				ContractType n = (ContractType) it.next();
				ct = recursionFn(list, n);
				children.add(ct);
			}
			jt.setChildren(children);
		} else {
			jt.setCls("x-tree-cls-node");
			jt.setAllowDrag(true);
			jt.setLeaf(true);
			jt.setChildren(new ArrayList<JSONTree>());
		}
		return jt;
	}

	private List<ContractType> getChildList(Collection<ContractType> list, ContractType s) {
		List<ContractType> li = new ArrayList<ContractType>();
		Iterator<ContractType> it = list.iterator();
		while (it.hasNext()) {
			ContractType n = (ContractType) it.next();
			// 父id等于id时 有子节点 添加该条数据
			if ((n.getCt_subof()) == (s.getCt_id())) {
				li.add(n);
			}
		}
		return li;
	}

	@Override
	public void saveOutAddress(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		if (baseDao.checkIf("mobile_outaddress", "md_address='" + store.get("MD_ADDRESS") + "'")) {
			baseDao.execute("update mobile_outaddress set md_company='" + store.get("MD_COMPANY") + "' where md_address='"
					+ store.get("MD_ADDRESS") + "'");
		} else {
			baseDao.execute("Insert into mobile_outaddress (MD_ID,MD_COMPANY,MD_ADDRESS,MD_VISITTIME,MD_VISITCOUNT) "
					+ "values (mobile_outaddress_SEQ.nextval,'" + store.get("MD_COMPANY") + "','" + store.get("MD_ADDRESS")
					+ "',sysdate,0)");
		}
	}

	/**
	 * @author wsy
	 */
	@Override
	public void checkTime(Map<Object, Object> formStore) {
		Timestamp starttime = Timestamp.valueOf(formStore.get("fp_prestartdate").toString());
		Timestamp endtime = Timestamp.valueOf(formStore.get("fp_preenddate").toString());
		if (starttime.after(endtime)) {
			BaseUtil.showError("时间输入有误，请检查后重新输入");
		}
	}

	@Override
	public String turnBillAP(int fp_id, String paymentcode, String payment, double thispayamount, String caller) {
		SqlRowList rs = baseDao.queryForRowSet("select * from FeePlease where fp_id=?", fp_id);
		if (rs.next()) {
			if (!"AUDITED".equals(rs.getGeneralString("fp_statuscode"))) {
				BaseUtil.showError("审核后才能转应付票据!");
			}
			if (paymentcode != null && !"".equals(paymentcode)) {
				String error = baseDao.getJdbcTemplate().queryForObject(
						"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
						String.class, paymentcode);
				if (error != null) {
					BaseUtil.showError("填写科目编号不存在，或者状态不等于已审核，或者不是末级科目，不允许转应付票据！");
				}
				error = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code NOT IN (SELECT COLUMN_VALUE FROM TABLE(parseString(getconfig('CheckAccount!GS','bapCatecode'), chr(10))))",
								String.class, paymentcode);
				if (error != null) {
					BaseUtil.showError("付款科目有误，请填写应付票据科目！");
				}
				baseDao.execute("update FeePlease set fp_v11=?,fp_v10=? where fp_id=?", paymentcode, payment, fp_id);
			} else {
				BaseUtil.showError("请填写付款科目！");
			}
			if (thispayamount == 0) {
				BaseUtil.showError("已经全部转应付票据!");
			}
			JSONObject j = feePleaseDao.turnBillAP(fp_id, thispayamount, caller);
			if (j != null) {
				baseDao.logger.turn("转应付票据" + thispayamount, caller, "fp_id", fp_id);
				return "转入成功,应付票据:<a href=\"javascript:openUrl('jsps/fa/gs/billAP.jsp?formCondition=bap_idIS" + j.get("bap_id") + "')\">"
						+ j.get("bap_code") + "</a>&nbsp;";
			}
		}
		return null;
	}

	@Override
	public String turnBillARChange(int fp_id, String paymentcode, String payment, double thispayamount, String caller) {
		SqlRowList rs = baseDao.queryForRowSet("select * from FeePlease where fp_id=?", fp_id);
		if (rs.next()) {
			if (!"AUDITED".equals(rs.getGeneralString("fp_statuscode"))) {
				BaseUtil.showError("审核后才能转应收票据异动!");
			}
			if (paymentcode != null && !"".equals(paymentcode)) {
				String error = baseDao.getJdbcTemplate().queryForObject(
						"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
						String.class, paymentcode);
				if (error != null) {
					BaseUtil.showError("填写科目编号不存在，或者状态不等于已审核，或者不是末级科目，不允许转应收票据异动！");
				}
				error = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code NOT IN (SELECT COLUMN_VALUE FROM TABLE(parseString(getconfig('CheckAccount!GS','barCatecode'), chr(10))))",
								String.class, paymentcode);
				if (error != null) {
					BaseUtil.showError("付款科目有误，请填写应收票据科目！");
				}
				baseDao.execute("update FeePlease set fp_v11=?,fp_v10=? where fp_id=?", paymentcode, payment, fp_id);
			} else {
				BaseUtil.showError("请填写付款科目！");
			}
			if (thispayamount == 0) {
				BaseUtil.showError("已经全部转应付票据!");
			}
			JSONObject j = feePleaseDao.turnBillARChange(fp_id, thispayamount, caller);
			if (j != null) {
				baseDao.logger.turn("转应收票据异动" + thispayamount, caller, "fp_id", fp_id);
				return "转入成功,应收票据异动:<a href=\"javascript:openUrl('jsps/fa/gs/billARChange.jsp?formCondition=brc_idIS" + j.get("brc_id")
						+ "&gridCondition=brd_brcidIS" + j.get("brc_id") + "')\">" + j.get("brc_code") + "</a>&nbsp;";
			}
		}
		return null;
	}

	@Override
	public String getFromSob(String condition) {
		String sob = SpObserver.getSp();
		// 切换默认账套进行查询
		SpObserver.putSp(BaseUtil.getXmlSetting("defaultSob"));
		Object fromSob = baseDao.getFieldDataByCondition("MASTER", "ma_user", condition);
		// 切换回原来的账套
		SpObserver.putSp(sob);
		if (fromSob == null) {
			return null;
		} else {
			return String.valueOf(fromSob);
		}
	}
}
