package com.uas.erp.service.fa.impl;

import java.math.BigDecimal;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.fa.DebitContractRegisterService;

@Service("DebitContractRegisterService")
public class DebitContractRegisterServiceImpl implements
		DebitContractRegisterService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveDebitContractRegister(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 合同编号相同的限制保存
		boolean bool = baseDao.checkByCondition("DebitContractRegister",
				"dcr_contractno='" + store.get("dcr_contractno") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"DebitContractRegister", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		Double dcr_loanamount = Double.parseDouble(store.get("dcr_loanamount")
				.toString());
		Double dcr_loanrate = Double.parseDouble(store.get("dcr_loanrate")
				.toString());
		String dcr_startdate = store.get("dcr_startdate").toString();
		int dcr_months = Integer.parseInt(store.get("dcr_months").toString());
		String dcr_payinterestway = store.get("dcr_payinterestway").toString();
		Object detno = baseDao.getFieldDataByCondition(
				"DebitContractRegisterDet", "max(dcrd_detno)", "dcrd_dcrid="
						+ store.get("dcr_id"));
		detno = detno == null ? 0 : detno;
		if (dcr_payinterestway.equals("等额本金")) {
			List<Object[]> objs = principal(dcr_startdate, dcr_loanamount,
					dcr_loanrate, dcr_months);
			int i = 0;
			for (Object[] os : objs) {
				i++;
				int id1 = baseDao.getSeqId("DEBITCONTRACTREGISTERDET_SEQ");
				String sql = "insert into DebitContractRegisterDet (dcrd_id,dcrd_dcrid,dcrd_detno,dcrd_plandate,dcrd_planprincipal,dcrd_planinterest) Values('"
						+ id1
						+ "','"
						+ store.get("dcr_id")
						+ "','"
						+ (Integer.parseInt(detno.toString()) + i)
						+ "',to_date('"
						+ os[0]
						+ "','yyyy-mm-dd'),"
						+ os[2]
						+ "," + os[3] + ")";
				baseDao.execute(sql);
			}
		} else if (dcr_payinterestway.equals("等额本息")) {
			List<Object[]> objs = interest(dcr_startdate, dcr_loanamount,
					dcr_loanrate, dcr_months);
			int i = 0;
			for (Object[] os : objs) {
				i++;
				int id1 = baseDao.getSeqId("DEBITCONTRACTREGISTERDET_SEQ");
				String sql = "insert into DebitContractRegisterDet (dcrd_id,dcrd_dcrid,dcrd_detno,dcrd_plandate,dcrd_planprincipal,dcrd_planinterest) Values('"
						+ id1
						+ "','"
						+ store.get("dcr_id")
						+ "','"
						+ (Integer.parseInt(detno.toString()) + i)
						+ "',to_date('"
						+ os[0]
						+ "','yyyy-mm-dd'),"
						+ os[2]
						+ "," + os[3] + ")";
				baseDao.execute(sql);
			}
		} else if (dcr_payinterestway.equals("利随本清")) {
			int i = 0;
			i++;
			int id1 = baseDao.getSeqId("DEBITCONTRACTREGISTERDET_SEQ");
			String sql = "insert into DebitContractRegisterDet (dcrd_id,dcrd_dcrid,dcrd_detno,dcrd_plandate,dcrd_planprincipal,dcrd_planinterest) Values('"
					+ id1
					+ "','"
					+ store.get("dcr_id")
					+ "','"
					+ (Integer.parseInt(detno.toString()) + i)
					+ "',to_date('"
					+ store.get("dcr_deadline")
					+ "','yyyy-mm-dd'),"
					+ store.get("dcr_loanamount")
					+ ","
					+ store.get("dcr_interest") + ")";
			baseDao.execute(sql);
		} else if (dcr_payinterestway.equals("不付息")) {
			int i = 0;
			i++;
			int id1 = baseDao.getSeqId("DEBITCONTRACTREGISTERDET_SEQ");
			String sql = "insert into DebitContractRegisterDet (dcrd_id,dcrd_dcrid,dcrd_detno,dcrd_plandate,dcrd_planprincipal,dcrd_planinterest) Values('"
					+ id1
					+ "','"
					+ store.get("dcr_id")
					+ "','"
					+ (Integer.parseInt(detno.toString()) + i)
					+ "',to_date('"
					+ store.get("dcr_deadline")
					+ "','yyyy-mm-dd'),"
					+ store.get("dcr_loanamount") + "," + 0 + ")";
			baseDao.execute(sql);
		}

		// 记录操作
		baseDao.logger.save(caller, "dcr_id", store.get("dcr_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteDebitContractRegister(int dcr_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition(
				"DebitContractRegister", "dcr_statuscode", "dcr_id=" + dcr_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { dcr_id });
		// 删除DebitContractRegister
		baseDao.deleteById("DebitContractRegister", "dcr_id", dcr_id);
		// 记录操作
		baseDao.logger.delete(caller, "dcr_id", dcr_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, dcr_id);
	}

	@Override
	public void updateDebitContractRegisterById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition(
				"DebitContractRegister", "dcr_statuscode",
				"dcr_id=" + store.get("dcr_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.update_onlyEntering"));
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 更新操作
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"DebitContractRegister", "dcr_id");
		baseDao.execute(formSql);
		Double dcr_loanamount = Double.parseDouble(store.get("dcr_loanamount")
				.toString());
		Double dcr_loanrate = Double.parseDouble(store.get("dcr_loanrate")
				.toString());
		String dcr_startdate = store.get("dcr_startdate").toString();
		int dcr_months = Integer.parseInt(store.get("dcr_months").toString());
		String dcr_payinterestway = store.get("dcr_payinterestway").toString();
		String sqls = "delete from DebitContractRegisterDet where dcrd_dcrid="
				+ store.get("dcr_id");
		baseDao.execute(sqls);
		Object detno = baseDao.getFieldDataByCondition(
				"DebitContractRegisterDet", "max(dcrd_detno)", "dcrd_dcrid="
						+ store.get("dcr_id"));
		detno = detno == null ? 0 : detno;
		if (dcr_payinterestway.equals("等额本金")) {
			List<Object[]> objs = principal(dcr_startdate, dcr_loanamount,
					dcr_loanrate, dcr_months);
			int i = 0;
			for (Object[] os : objs) {
				i++;
				int id1 = baseDao.getSeqId("DEBITCONTRACTREGISTERDET_SEQ");
				String sql = "insert into DebitContractRegisterDet (dcrd_id,dcrd_dcrid,dcrd_detno,dcrd_plandate,dcrd_planprincipal,dcrd_planinterest) Values('"
						+ id1
						+ "','"
						+ store.get("dcr_id")
						+ "','"
						+ (Integer.parseInt(detno.toString()) + i)
						+ "',to_date('"
						+ os[0]
						+ "','yyyy-mm-dd'),"
						+ os[2]
						+ "," + os[3] + ")";
				baseDao.execute(sql);
			}
		} else if (dcr_payinterestway.equals("等额本息")) {
			List<Object[]> objs = interest(dcr_startdate, dcr_loanamount,
					dcr_loanrate, dcr_months);
			int i = 0;
			for (Object[] os : objs) {
				i++;
				int id1 = baseDao.getSeqId("DEBITCONTRACTREGISTERDET_SEQ");
				String sql = "insert into DebitContractRegisterDet (dcrd_id,dcrd_dcrid,dcrd_detno,dcrd_plandate,dcrd_planprincipal,dcrd_planinterest) Values('"
						+ id1
						+ "','"
						+ store.get("dcr_id")
						+ "','"
						+ (Integer.parseInt(detno.toString()) + i)
						+ "',to_date('"
						+ os[0]
						+ "','yyyy-mm-dd'),"
						+ os[2]
						+ "," + os[3] + ")";
				baseDao.execute(sql);
			}
		} else if (dcr_payinterestway.equals("利随本清")) {
			int i = 0;
			i++;
			int id1 = baseDao.getSeqId("DEBITCONTRACTREGISTERDET_SEQ");
			String sql = "insert into DebitContractRegisterDet (dcrd_id,dcrd_dcrid,dcrd_detno,dcrd_plandate,dcrd_planprincipal,dcrd_planinterest) Values('"
					+ id1
					+ "','"
					+ store.get("dcr_id")
					+ "','"
					+ (Integer.parseInt(detno.toString()) + i)
					+ "',to_date('"
					+ store.get("dcr_deadline")
					+ "','yyyy-mm-dd'),"
					+ store.get("dcr_loanamount")
					+ ","
					+ store.get("dcr_interest") + ")";
			baseDao.execute(sql);
		} else if (dcr_payinterestway.equals("不付息")) {
			int i = 0;
			i++;
			int id1 = baseDao.getSeqId("DEBITCONTRACTREGISTERDET_SEQ");
			String sql = "insert into DebitContractRegisterDet (dcrd_id,dcrd_dcrid,dcrd_detno,dcrd_plandate,dcrd_planprincipal,dcrd_planinterest) Values('"
					+ id1
					+ "','"
					+ store.get("dcr_id")
					+ "','"
					+ (Integer.parseInt(detno.toString()) + i)
					+ "',to_date('"
					+ store.get("dcr_deadline")
					+ "','yyyy-mm-dd'),"
					+ store.get("dcr_loanamount") + "," + 0 + ")";
			baseDao.execute(sql);
		}
		// 记录操作
		baseDao.logger.update(caller, "dcr_id", store.get("dcr_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void submitDebitContractRegister(int dcr_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition(
				"DebitContractRegister", "dcr_statuscode", "dcr_id=" + dcr_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.submit_onlyEntering"));
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, dcr_id);
		// 执行提交操作
		baseDao.updateByCondition(
				caller,
				"dcr_statuscode='COMMITED',dcr_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "dcr_id="
						+ dcr_id);
		// 记录操作
		baseDao.logger.submit(caller, "dcr_id", dcr_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, dcr_id);
	}

	@Override
	public void resSubmitDebitContractRegister(int dcr_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition(
				"DebitContractRegister", "dcr_statuscode", "dcr_id=" + dcr_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resSubmit_onlyCommited"));
		}
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, dcr_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"DebitContractRegister",
				"dcr_statuscode='ENTERING',dcr_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "dcr_id="
						+ dcr_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "dcr_id", dcr_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, dcr_id);
	}

	@Override
	public void auditDebitContractRegister(int dcr_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition(
				"DebitContractRegister", "dcr_statuscode", "dcr_id=" + dcr_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, dcr_id);
		// 执行审核操作
		baseDao.updateByCondition(
				caller,
				"dcr_statuscode='AUDITED',dcr_status='"
						+ BaseUtil.getLocalMessage("AUDITED") + "'", "dcr_id="
						+ dcr_id);
		// 记录操作
		baseDao.logger.audit(caller, "dcr_id", dcr_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, dcr_id);
	}

	@Override
	public void resAuditDebitContractRegister(int dcr_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition(
				"DebitContractRegister", "dcr_statuscode", "dcr_id=" + dcr_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resAudit_onlyAudit"));
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, dcr_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				caller,
				"dcr_statuscode='ENTERING',dcr_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "dcr_id="
						+ dcr_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "dcr_id", dcr_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, dcr_id);
	}

	/**
	 * 等额本金还款法【利息少，但前期还的多】
	 * 
	 * @param totalMoeny
	 *            贷款总额
	 * @param rate
	 *            贷款商业利率
	 * @param year
	 *            贷款年限
	 */
	public List<Object[]> principal(String begindate, double totalMoney,
			double rate, int year) {
		List<Object[]> list = new ArrayList<Object[]>();
		int totalMonth = year;
		// 每月本金
		double monthPri = totalMoney / totalMonth;
		// 获取月利率
		double monRate = resMonthRate(rate);
		BigDecimal b = new BigDecimal(monRate);
		monRate = b.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
		Object[] os = null;
		for (int i = 1; i <= totalMonth; i++) {
			os = new Object[4];
			begindate = GetSysDate("yyyy-MM-dd", begindate, 0, 1, 0);
			double monthinterest = (totalMoney - monthPri * (i - 1)) * monRate;
			double monthRes = monthPri + (totalMoney - monthPri * (i - 1))
					* monRate;
			BigDecimal b1 = new BigDecimal(monthRes);
			monthRes = b1.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			// 开始还款日期
			os[0] = begindate;
			// 每月还款
			os[1] = String.valueOf(monthRes);
			// 每月还本金
			os[2] = String.valueOf(monthPri);
			// 每月利息
			os[3] = String.valueOf(monthinterest);
			list.add(os);
		}
		return list;
	}

	/**
	 * 等额本息还款【利息多】
	 * 
	 * @param totalMoeny
	 *            贷款总额
	 * @param rate
	 *            贷款商业利率
	 * @param year
	 *            贷款年限
	 */
	public List<Object[]> interest(String begindate, Double dcr_loanamount,
			double rate, int year) {
		List<Object[]> list = new ArrayList<Object[]>();
		int totalMonth = year;
		// 每月本金
		double monthPri = dcr_loanamount / totalMonth;
		// 获取月利率
		double monRate = resMonthRate(rate);
		BigDecimal b = new BigDecimal(monRate);
		monRate = b.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
		Object[] os = null;
		for (int i = 1; i <= totalMonth; i++) {
			os = new Object[4];
			begindate = GetSysDate("yyyy-MM-dd", begindate, 0, 1, 0);
			double monInterest = dcr_loanamount * monRate
					* Math.pow((1 + monRate), year)
					/ (Math.pow((1 + monRate), year) - 1) - monthPri;
			double monthRes = monthPri + monInterest;
			BigDecimal b1 = new BigDecimal(monthRes);
			monthRes = b1.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			BigDecimal b2 = new BigDecimal(monInterest);
			monInterest = b2.setScale(2, BigDecimal.ROUND_HALF_UP)
					.doubleValue();
			// 开始还款日期
			os[0] = begindate;
			// 每月还款
			os[1] = String.valueOf(monthRes);
			// 每月还本金
			os[2] = String.valueOf(monthPri);
			// 每月利息
			os[3] = String.valueOf(monInterest);
			list.add(os);
		}
		return list;
	}

	/**
	 * 转换为月利率
	 * 
	 * @param rate
	 * @return
	 */
	public static double resMonthRate(double rate) {
		return rate / 12 * 0.01;
	}

	@SuppressWarnings("static-access")
	static String GetSysDate(String format, String StrDate, int year,
			int month, int day) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sFmt = new SimpleDateFormat(format);
		cal.setTime(sFmt.parse((StrDate), new ParsePosition(0)));

		if (day != 0) {
			cal.add(cal.DATE, day);
		}
		if (month != 0) {
			cal.add(cal.MONTH, month);
		}
		if (year != 0) {
			cal.add(cal.YEAR, year);

		}
		return sFmt.format(cal.getTime());
	}
}
