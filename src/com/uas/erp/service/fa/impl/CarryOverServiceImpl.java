package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.fa.CarryOverService;

@Service
public class CarryOverServiceImpl implements CarryOverService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private VoucherDao voucherDao;

	@Override
	public String makeMonthComplete(Boolean account) {
		Employee employee = SystemSession.getUser();
		String language = SystemSession.getLang();
		int month = voucherDao.getNowPddetno("MONTH-A");
		String explanation = "本月完工结转";
		// 查找有没有本月完工结转生成的凭证
		Object[] vo = baseDao.getFieldsDataByCondition("Voucher", "vo_id,vo_code", "vo_yearmonth=" + month + " AND (vo_explanation like '%"
				+ explanation + "%' or vo_source like '%" + explanation + "%')");
		if (vo != null && vo[1] != null) {
			BaseUtil.showError("本月完工已经结转,凭证号:" + "<a href=\"javascript:openUrl('jsps/fa/ars/voucher.jsp?formCondition=vo_idIS" + vo[0]
					+ "&gridCondition=vd_voidIS" + vo[0] + "')\">" + vo[1] + "</a>,不能再次制作!");
		}
		// 生产成本直接人工科目
		String manCate = baseDao.getDBSetting("MakeComplete", "manMakeCatecode");
		// 生产成本制造费用
		String makeCate = baseDao.getDBSetting("MakeFeeClose", "makeToCatecode");
		// 生产成本原材料
		String materialCate = baseDao.getDBSetting("MakeComplete", "materialCatecode");
		if (makeCate == null) {
			BaseUtil.showError("生产成本制造费用科目未设置！");
		}
		if (materialCate == null) {
			BaseUtil.showError("生产成本原材料科目未设置！");
		}
		if (manCate == null) {
			BaseUtil.appendError("生产成本直接人工科目未设置！请根据实际情况确定是否需要设置生产成本直接人工科目！");
		}
		Double manCost = baseDao.getSummaryByField("manufactfee", "mf_laboramount", "mf_yearmonth=" + month);
		Double makeCost = baseDao.getSummaryByField("manufactfee", "mf_amount", "mf_yearmonth=" + month);
		int voId = baseDao.getSeqId("voucher_seq");
		String voCode = baseDao.sGetMaxNumber("Voucher", 2);
		List<String> sqls = new ArrayList<String>();
		int vdDetno = 1;
		sqls.add(addVoucherSql(employee, language, month, voId, voCode, explanation));
		if (!StringUtils.isEmpty(manCate)) {
			sqls.add(addDetailSql(employee, language, month, voId, vdDetno++, manCate, explanation, 0, manCost));
		}
		sqls.add(addDetailSql(employee, language, month, voId, vdDetno++, makeCate, explanation, 0, makeCost));
		sqls.add(addDetailSql(employee, language, month, voId, vdDetno++, materialCate, explanation, manCost + makeCost, 0));
		baseDao.execute(sqls);
		voucherDao.validVoucher(voId);
		String error = baseDao.getJdbcTemplate().queryForObject("select vo_errstring from Voucher Where vo_id=?", String.class, voId);
		String codeStr = "<a href=\"javascript:openUrl('jsps/fa/ars/voucher.jsp?formCondition=vo_idIS" + voId + "&gridCondition=vd_voidIS"
				+ voId + "')\">" + voCode + "</a><br>";
		if ((error != null && error.trim().length() > 0)) {
			return "产生的凭证有问题，请打开凭证查看!<br>" + codeStr;
		} else {
			baseDao.updateByCondition("Voucher", "vo_statuscode='COMMITED',vo_status='" + BaseUtil.getLocalMessage("COMMITED") + "'",
					"vo_id=" + voId);
			if (account) {
				baseDao.updateByCondition("Voucher", "vo_status='" + BaseUtil.getLocalMessage("AUDITED")
						+ "',vo_statuscode='AUDITED',vo_checkby='" + employee.getEm_name() + "'", "vo_id=" + voId);
				baseDao.callProcedure("SP_WriteVoucher", new Object[] { month });
				return "已成功产生研发费用结转凭证并记账!<br>" + codeStr;
			} else {
				return "本月完工结转凭证生成成功,凭证号:" + codeStr;
			}
		}
	}

	@Override
	public String researchCost(Boolean account) {
		Employee employee = SystemSession.getUser();
		String language = SystemSession.getLang();
		int month = voucherDao.getNowPddetno("MONTH-A");
		String explanation = "研发费用结转";
		// 有未记账凭证
		boolean notAccount = baseDao.checkIf("voucher", "vo_yearmonth=" + month + " and vo_statuscode<>'ACCOUNT'");
		if (notAccount)
			BaseUtil.showError("本月有未记账凭证.");
		// 查找有没有研发费用结转生成的凭证
		Object[] vo = baseDao.getFieldsDataByCondition("Voucher", "vo_id,vo_code", "vo_yearmonth=" + month + " AND (vo_explanation like '%"
				+ explanation + "%' or vo_source like '%" + explanation + "%')");
		if (vo != null && vo[1] != null) {
			BaseUtil.showError("本月研发费用已经结转,凭证号:" + "<a href=\"javascript:openUrl('jsps/fa/ars/voucher.jsp?formCondition=vo_idIS" + vo[0]
					+ "&gridCondition=vd_voidIS" + vo[0] + "')\">" + vo[1] + "</a>,不能再次制作!");
		}
		// 研发支出转入科目
		String toCate = baseDao.getDBSetting("ResearchFee", "researchToCatecode");
		// 研发费用科目
		String feeCate = baseDao.getDBSetting("ResearchFee", "researchFeeCatecode");
		if (toCate == null || feeCate == null)
			BaseUtil.showError("结转科目未设置.");
		int count = baseDao.getJdbcTemplate().queryForObject(
				"SELECT count(1) FROM CategorySET,Category WHERE cs_catecode=ca_code AND abs(ca_isleaf)=1 AND ca_code like '" + feeCate
						+ "%' AND cs_debit+cs_credit<>0", Integer.class);
		if (count > 0) {
			int id = baseDao.getSeqId("Voucher_seq");
			int detno = 1;
			double total = 0.0;
			double debit = 0.0;
			double credit = 0.0;
			List<String> sqls = new ArrayList<String>();
			// 多辅助核算
			SqlRowList mAss = baseDao
					.queryForRowSet(
							"select vd_catecode,ass,round(sum(debit),2) debit from (select vd_catecode,str_concat(vds_asstype || '#' || vds_asscode || '#' || replace(vds_assname,',','@@')) ass,max(vd_debit)-max(vd_credit) debit from (select vd_catecode,vds_vdid,vds_asstype,vds_asscode,vds_assname,vd_debit,vd_credit from voucherdetailass left join voucherdetail on vds_vdid=vd_id where vd_catecode in ("
									+ "SELECT cs_catecode FROM CategorySET,Category WHERE cs_catecode=ca_code AND abs(ca_isleaf)=1 AND ca_code like '"
									+ feeCate
									+ "%' AND cs_debit+cs_credit<>0 and instr(ca_asstype,'#')>0) and vd_void in(select vo_id from voucher where vo_yearmonth=?) order by vds_asstype,vds_asscode,vds_assname) group by vd_catecode,vds_vdid) group by vd_catecode,ass",
							month);
			while (mAss.next()) {
				debit = mAss.getDouble("debit");
				total += debit;
				int vd_id = baseDao.getSeqId("VoucherDetail_seq");
				sqls.add(addDetailSql(employee, language, month, id, vd_id, detno++, mAss.getString("vd_catecode"), explanation, 0, debit));
				String[] assGroup = mAss.getGeneralString("ass").split(",");
				int assDetno = 1;
				for (String assStr : assGroup) {
					String[] ass = assStr.split("#");
					if (ass.length == 3)
						sqls.add(addAssSql(vd_id, assDetno++, ass[0], ass[1], ass[2]));
				}
			}
			// 单辅助核算
			SqlRowList sAss = baseDao
					.queryForRowSet("SELECT ca_catecode,CategoryAss.ca_asstype ca_asstype,ca_asscode,CategoryAss.ca_assname ca_assname,ca_debit,ca_credit FROM CategoryAss where (ca_catecode,nvl(ca_currency,'RMB')) in ("
							+ "SELECT cs_catecode,nvl(ca_currency,'RMB') FROM CategorySET,Category WHERE cs_catecode=ca_code AND abs(ca_isleaf)=1 AND ca_code like '"
							+ feeCate
							+ "%' and nvl(ca_asstype,' ')<>' ' and instr(ca_asstype,'#')=0 AND cs_debit+cs_credit<>0) AND ca_debit+ca_credit<>0");
			while (sAss.next()) {
				debit = sAss.getDouble("ca_debit");
				credit = sAss.getDouble("ca_credit");
				total += debit - credit;
				if (debit != 0) {
					credit = debit;
					debit = 0;
				} else if (credit != 0) {
					credit = -1 * credit;
					debit = 0;
				}
				int vd_id = baseDao.getSeqId("VoucherDetail_seq");
				sqls.add(addDetailSql(employee, language, month, id, vd_id, detno++, sAss.getString("ca_catecode"), explanation, debit,
						credit));
				sqls.add(addAssSql(vd_id, 1, sAss.getString("ca_asstype"), sAss.getString("ca_asscode"), sAss.getString("ca_assname")));
			}
			// 无辅助核算
			SqlRowList nAss = baseDao
					.queryForRowSet("select cs_catecode,cs_debit,cs_credit from categoryset,category where cs_catecode=ca_code and abs(ca_isleaf)=1 and ca_code like '"
							+ feeCate + "%' and nvl(ca_asstype,' ')=' ' AND cs_debit+cs_credit<>0");
			while (nAss.next()) {
				debit = nAss.getDouble("cs_debit");
				credit = nAss.getDouble("cs_credit");
				total += debit - credit;
				if (debit != 0) {
					credit = debit;
					debit = 0;
				} else if (credit != 0) {
					credit = -1 * credit;
					debit = 0;
				}
				int vd_id = baseDao.getSeqId("VoucherDetail_seq");
				sqls.add(addDetailSql(employee, language, month, id, vd_id, detno++, nAss.getString("cs_catecode"), explanation, debit,
						credit));
			}
			// 研发支出转入
			sqls.add(addDetailSql(employee, language, month, id, detno++, toCate, explanation, total, 0));
			// 主记录
			String code = baseDao.sGetMaxNumber("Voucher", 2);
			sqls.add(addVoucherSql(employee, language, month, id, code, explanation));
			baseDao.execute(sqls);
			voucherDao.validVoucher(id);
			String error = baseDao.getJdbcTemplate().queryForObject("select vo_errstring from Voucher Where vo_id=?", String.class, id);
			String codeStr = "<a href=\"javascript:openUrl('jsps/fa/ars/voucher.jsp?formCondition=vo_idIS" + id
					+ "&gridCondition=vd_voidIS" + id + "')\">" + code + "</a><br>";
			if ((error != null && error.trim().length() > 0)) {
				return "产生的凭证有问题，请打开凭证查看!<br>" + codeStr;
			} else {
				baseDao.updateByCondition("Voucher", "vo_statuscode='COMMITED',vo_status='" + BaseUtil.getLocalMessage("COMMITED") + "'",
						"vo_id=" + id);
				if (account) {
					baseDao.updateByCondition("Voucher", "vo_status='" + BaseUtil.getLocalMessage("AUDITED")
							+ "',vo_statuscode='AUDITED',vo_checkby='" + employee.getEm_name() + "'", "vo_id=" + id);
					baseDao.callProcedure("SP_WriteVoucher", new Object[] { month });
					return "已成功产生研发费用结转凭证并记账!<br>" + codeStr;
				} else {
					return "研发费用结转凭证生成成功,凭证号:" + codeStr;
				}
			}
		} else {
			return "本月没有需要结转的费用!";
		}
	}

	private String addVoucherSql(Employee employee, String language, int yearmonth, int vo_id, String vo_code, String explanation) {
		Map<String, Object> periods = voucherDao.getPeriodsDate("MONTH-A", yearmonth);
		String lead = StringUtil.valueOf(periods.get("vo_lead"));
		String vo_number = voucherDao.getVoucherNumber(String.valueOf(yearmonth), lead, null);
		return "INSERT INTO Voucher(vo_id,vo_code,vo_yearmonth,vo_lead,vo_number,vo_emid,vo_recordman,vo_status,"
				+ "vo_statuscode,vo_recorddate,vo_explanation,vo_currencytype,vo_printstatus,vo_date)" + " VALUES (" + vo_id + ",'"
				+ vo_code + "'," + yearmonth + ",'" + (lead == null ? "" : lead) + "'," + vo_number + "," + employee.getEm_id() + ",'"
				+ employee.getEm_name() + "','" + BaseUtil.getLocalMessage("ENTERING", language) + "','ENTERING',sysdate,'" + explanation
				+ "',0,'未打印'," + periods.get("pd_enddate") + ")";
	}

	private String addDetailSql(Employee employee, String language, int yearmonth, int voId, int num, String cate, String explanation,
			double debit, double credit) {
		return "INSERT INTO VoucherDetail(vd_id,vd_void,vd_detno,vd_catecode,vd_explanation,vd_debit,"
				+ "vd_credit,vd_currency,vd_yearmonth) VALUES (VoucherDetail_seq.nextval," + voId + "," + num + ",'" + cate + "','"
				+ explanation + "'," + debit + "," + credit + ",'RMB'," + yearmonth + ")";
	}

	private String addDetailSql(Employee employee, String language, int yearmonth, int voId, int vdId, int num, String cate,
			String explanation, double debit, double credit) {
		return "INSERT INTO VoucherDetail(vd_id,vd_void,vd_detno,vd_catecode,vd_explanation,vd_debit,"
				+ "vd_credit,vd_currency,vd_yearmonth) VALUES (" + vdId + "," + voId + "," + num + ",'" + cate + "','" + explanation + "',"
				+ debit + "," + credit + ",'RMB'," + yearmonth + ")";
	}

	private String addAssSql(int vd_id, int num, String type, String code, String name) {
		return "INSERT INTO VoucherDetailAss(vds_id,vds_vdid,vds_detno,vds_asstype,vds_asscode,vds_assname,vds_type) VALUES("
				+ "VoucherDetailAss_SEQ.nextval," + vd_id + "," + num + ",'" + type + "','" + code + "',replace('" + name
				+ "','@@',','),'Voucher')";
	}

}