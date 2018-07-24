package com.uas.erp.service.fa.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.fa.AssetsCardService;

@Service("assetsCardService")
public class AssetsCardServiceImpl implements AssetsCardService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private VoucherDao voucherDao;

	@Override
	public void saveAssetsCard(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Map<String, Object> map = voucherDao.getJustPeriods("MONTH-F");
		Object acid = store.get("ac_id");
		SimpleDateFormat sdf = new SimpleDateFormat(Constant.YMD);
		try {
			Date date = sdf.parse(store.get("ac_date").toString());
			Date date1 = sdf.parse(map.get("PD_STARTDATE").toString());
			Date date2 = sdf.parse(map.get("PD_ENDDATE").toString());
			if (date1.compareTo(date) > 0 || date2.compareTo(date) < 0) {
				BaseUtil.showError("卡片入账日期只能是当前固定资产账期!");
			}
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("AssetsCard", "ac_code='" + store.get("ac_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		bool = baseDao.checkByCondition("AssetsCard", "ac_ascode='" + store.get("ac_ascode") + "'");
		if (!bool) {
			BaseUtil.showError("固定资产编号不能重复！");
		}
		checkCloseMonth(store.get("ac_date"));
		double oldvalue = Double.parseDouble((store.get("ac_oldvalue")).toString()); // 原值
		double useyear = Double.parseDouble((store.get("ac_useyears")).toString()); // 使用年限
		double crate = Double.parseDouble((store.get("ac_crate")).toString()); // 残值率
		double totaldepreciation = Double.parseDouble((store.get("ac_totaldepreciation")).toString()); // 累计折旧
		double ac_cvalue = 0; // 残值
		double ac_monthtotal = 0;// 月折旧额
		double ac_monthrate = 0; // 月折旧率

		ac_cvalue = oldvalue * crate;
		ac_monthtotal = (oldvalue - ac_cvalue) / (useyear * 12);
		ac_monthrate = oldvalue == 0 ? 1 : ac_monthtotal / oldvalue;

		store.put("ac_cvalue", NumberUtil.formatDouble(ac_cvalue, 2));
		store.put("ac_monthtotal", NumberUtil.formatDouble(ac_monthtotal, 2));
		store.put("ac_monthrate", ac_monthrate);
		store.put("ac_netvalue", NumberUtil.formatDouble(oldvalue - totaldepreciation, 2));

		if (store.get("ac_vendcode") == null || store.get("ac_vendcode").toString().trim().length() == 0) {
			store.put("ac_vendname", "");
			store.put("ac_vendid", 0);
		}
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "AssetsCard", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.execute(
				"update AssetsCard set ac_useyears=round(nvl(ac_usemonth,0)/12,8) where ac_id=? and nvl(ac_useyears,0)=0 and nvl(ac_usemonth,0)<>0",
				acid);
		// 记录操作
		baseDao.logger.save(caller, "ac_id", acid);
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	void checkVoucher(Object id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(ac_vouchercode) from AssetsCard where ac_id=? and nvl(ac_vouchercode,' ') <>' ' and ac_vouchercode<>'UNNEED'",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("已有凭证，不允许进行当前操作!凭证编号：" + dets);
		}
	}

	void checkCloseMonth(Object acdate) {
		int nowym = voucherDao.getNowPddetno("MONTH-F");// 当前期间
		int count = baseDao.getCount("select count(1) from dual where to_char(to_date('" + acdate
				+ "','yyyy-mm-dd hh24:mi:ss'), 'yyyymm') <" + nowym);
		if (count > 0) {
			BaseUtil.showError("单据日期所属期间已结账，不允许进行当前操作!");
		}
	}

	void checkCate(Object ac_id) {
		SqlRowList rs = baseDao.queryForRowSet("select ac_accatecode,ac_ascatecode from AssetsCard where ac_id=?", new Object[] { ac_id });
		if (rs.next()) {
			String error = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
					String.class, rs.getObject("ac_accatecode"));
			if (error != null) {
				BaseUtil.showError("填写的固定资产科目不存在，或者状态不等于已审核，或者不是末级科目！");
			}
			error = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
					String.class, rs.getObject("ac_ascatecode"));
			if (error != null) {
				BaseUtil.showError("填写的累计折旧科目不存在，或者状态不等于已审核，或者不是末级科目！");
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code NOT IN (SELECT COLUMN_VALUE FROM TABLE(parseString(getconfig('MonthAccount!AS','fixCatecode'), chr(10))))",
							String.class, rs.getObject("ac_accatecode"));
			if (error != null) {
				BaseUtil.showError("固定资产科目编号不是参数设置中的科目！");
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code NOT IN (SELECT COLUMN_VALUE FROM TABLE(parseString(getconfig('MonthAccount!AS','deCatecode'), chr(10))))",
							String.class, rs.getObject("ac_ascatecode"));
			if (error != null) {
				BaseUtil.showError("累计折旧科目编号不是参数设置中的科目！");
			}
		}
	}

	@Override
	public void updateAssetsCardById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object acid = store.get("ac_id");
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("AssetsCard", "ac_statuscode", "ac_id=" + acid);
		StateAssert.updateOnlyEntering(status);
		// 当前编号的记录已经存在,不能更新!
		boolean bool = baseDao.checkByCondition("AssetsCard", "ac_code='" + store.get("ac_code") + "' and ac_id<>" + acid);
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		bool = baseDao.checkByCondition("AssetsCard", "ac_ascode='" + store.get("ac_ascode") + "' and ac_id<>" + acid);
		if (!bool) {
			BaseUtil.showError("固定资产编号不能重复！");
		}
		checkVoucher(acid);
		Map<String, Object> map = voucherDao.getJustPeriods("MONTH-F");
		SimpleDateFormat sdf = new SimpleDateFormat(Constant.YMD);
		try {
			Date date = sdf.parse(store.get("ac_date").toString());
			Date date1 = sdf.parse(map.get("PD_STARTDATE").toString());
			Date date2 = sdf.parse(map.get("PD_ENDDATE").toString());
			if (date1.compareTo(date) > 0 || date2.compareTo(date) < 0) {
				BaseUtil.showError("卡片入账日期只能是当前固定资产账期!");
			}
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		checkCloseMonth(store.get("ac_date"));
		double oldvalue = Double.parseDouble((store.get("ac_oldvalue")).toString()); // 原值
		double useyear = Double.parseDouble((store.get("ac_useyears")).toString()); // 使用年限
		double crate = Double.parseDouble((store.get("ac_crate")).toString()); // 残值率
		double totaldepreciation = Double.parseDouble((store.get("ac_totaldepreciation")).toString()); // 累计折旧
		double ac_cvalue = 0; // 残值
		double ac_monthtotal = 0;// 月折旧额
		double ac_monthrate = 0; // 月折旧率

		ac_cvalue = oldvalue * crate;
		ac_monthtotal = (oldvalue - ac_cvalue) / (useyear * 12);
		ac_monthrate = oldvalue == 0 ? 1 : ac_monthtotal / oldvalue;

		store.put("ac_cvalue", NumberUtil.formatDouble(ac_cvalue, 2));
		store.put("ac_monthtotal", NumberUtil.formatDouble(ac_monthtotal, 2));
		store.put("ac_monthrate", ac_monthrate);
		store.put("ac_netvalue", NumberUtil.formatDouble(oldvalue - totaldepreciation, 2));
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改
		if (store.get("ac_vendcode") == null || store.get("ac_vendcode").toString().trim().length() == 0) {
			store.put("ac_vendname", "");
			store.put("ac_vendid", 0);
		}
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "AssetsCard", "ac_id");
		baseDao.execute(formSql);
		baseDao.execute(
				"update AssetsCard set ac_useyears=round(nvl(ac_usemonth,0)/12,8) where ac_id=? and nvl(ac_useyears,0)=0 and nvl(ac_usemonth,0)<>0",
				acid);
		// 记录操作
		baseDao.logger.update(caller, "ac_id", acid);
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void deleteAssetsCard(int ac_id, String caller) {
		// 只能删除在录入的采购单!
		Object[] status = baseDao.getFieldsDataByCondition("AssetsCard", new String[] { "ac_statuscode", "ac_date" }, "ac_id=" + ac_id);
		StateAssert.delOnlyEntering(status[0]);
		checkCloseMonth(status[1]);
		checkVoucher(ac_id);
		baseDao.delCheck("AssetsCard", ac_id);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, ac_id);
		// 删除
		baseDao.deleteById("AssetsCard", "ac_id", ac_id);
		// 记录操作
		baseDao.logger.delete(caller, "ac_id", ac_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ac_id);
	}

	@Override
	public void auditAssetsCard(int ac_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("AssetsCard", new String[] { "ac_statuscode", "ac_date" }, "ac_id=" + ac_id);
		StateAssert.auditOnlyCommited(status[0]);
		checkCloseMonth(status[1]);
		checkVoucher(ac_id);
		checkCate(ac_id);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ac_id);
		baseDao.execute(
				"update AssetsCard set ac_useyears=round(nvl(ac_usemonth,0)/12,8) where ac_id=? and nvl(ac_useyears,0)=0 and nvl(ac_usemonth,0)<>0",
				ac_id);
		// 执行审核操作
		baseDao.updateByCondition("AssetsCard", "ac_statuscode='AUDITED',ac_status='" + BaseUtil.getLocalMessage("AUDITED")
				+ "',ac_auditer='" + SystemSession.getUser().getEm_name() + "',ac_auditdate=sysdate", "ac_id=" + ac_id);
		// 记录操作
		baseDao.logger.audit(caller, "ac_id", ac_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ac_id);
	}

	@Override
	public void resAuditAssetsCard(int ac_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("AssetsCard", new String[] { "ac_statuscode", "ac_date" }, "ac_id=" + ac_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		checkCloseMonth(status[1]);
		checkVoucher(ac_id);
		Object ac_code = baseDao.getFieldDataByCondition("AssetsCard", "ac_code", "ac_id=" + ac_id);
		Object declass = baseDao.getFieldDataByCondition("AssetsDepreciationDetail", "dd_class", "dd_accode='" + ac_code + "'");
		if (declass != null) {
			BaseUtil.showError("已做过" + declass + "的卡片不能反审核！");
		}
		baseDao.resAuditCheck("AssetsCard", ac_id);
		handlerService.beforeResAudit(caller, ac_id);
		// 执行反审核操作
		baseDao.updateByCondition("AssetsCard", "ac_statuscode='ENTERING',ac_status='" + BaseUtil.getLocalMessage("ENTERING")
				+ "',ac_auditer='',ac_auditdate=null", "ac_id=" + ac_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ac_id", ac_id);
		handlerService.afterResAudit(caller, ac_id);
	}

	@Override
	public void submitAssetsCard(int ac_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("AssetsCard", new String[] { "ac_statuscode", "ac_date" }, "ac_id=" + ac_id);
		StateAssert.submitOnlyEntering(status[0]);
		checkCloseMonth(status[1]);
		checkVoucher(ac_id);
		checkCate(ac_id);
		baseDao.execute(
				"update AssetsCard set ac_useyears=round(nvl(ac_usemonth,0)/12,8) where ac_id=? and nvl(ac_useyears,0)=0 and nvl(ac_usemonth,0)<>0",
				ac_id);
		baseDao.execute("update AssetsCard set ac_cvalue=round(nvl(ac_oldvalue,0)*nvl(ac_crate,0),2) where ac_id=?", ac_id);
		baseDao.execute(
				"update AssetsCard set ac_monthtotal=round((nvl(ac_oldvalue,0)-nvl(ac_cvalue,0))/nvl(ac_usemonth,0),2) where ac_id=? and nvl(ac_usemonth,0)<>0",
				ac_id);
		baseDao.execute(
				"update AssetsCard set ac_monthrate=round(nvl((nvl(ac_oldvalue,0)-nvl(ac_cvalue,0))/nvl(ac_usemonth,0),0)/nvl(ac_oldvalue,0),15) where ac_id=? and nvl(ac_oldvalue,0)<>0",
				ac_id);
		baseDao.execute("update AssetsCard set ac_netvalue=round(nvl(ac_oldvalue,0)-nvl(ac_totaldepreciation,0),2) where ac_id=?", ac_id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ac_id);
		// 执行提交操作
		baseDao.updateByCondition("AssetsCard", "ac_statuscode='COMMITED',ac_status='" + BaseUtil.getLocalMessage("COMMITED") + "'",
				"ac_id=" + ac_id);
		// 记录操作
		baseDao.logger.submit(caller, "ac_id", ac_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ac_id);
	}

	@Override
	public void resSubmitAssetsCard(int ac_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("AssetsCard", new String[] { "ac_statuscode", "ac_date" }, "ac_id=" + ac_id);
		StateAssert.resSubmitOnlyCommited(status[0]);
		checkVoucher(ac_id);
		handlerService.beforeResSubmit(caller, ac_id);
		// 执行反提交操作
		baseDao.updateByCondition("AssetsCard", "ac_statuscode='ENTERING',ac_status='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"ac_id=" + ac_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ac_id", ac_id);
		handlerService.afterResSubmit(caller, ac_id);
	}

	@Override
	public JSONObject copyAssetsCard(int id, String accode, int kindid, String caller) {
		Map<String, Object> dif = new HashMap<String, Object>();
		Employee employee = SystemSession.getUser();
		JSONObject ac = getAssetsCardCodeNum(caller, kindid);
		int acnum = baseDao.getFieldValue("AssetsCard", "nvl(max(ac_number),0)+1", "nvl(ac_number,0)<>0", Integer.class);
		if (ac != null && StringUtil.hasText(ac.getString("code"))) {
			accode = ac.getString("code");
			acnum = ac.getInt("number");
		}
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("AssetsCard", "ac_code='" + accode + "'");
		if (!bool) {
			BaseUtil.showError("卡片编号[" + accode + "]已存在，请修改卡片编号！");
		}
		// Copy 凭证
		int nId = baseDao.getSeqId("ASSETSCARD_SEQ");
		dif.put("ac_id", nId);
		dif.put("ac_date", "sysdate");
		dif.put("ac_kindid", kindid);
		dif.put("ac_code", "'" + accode + "'");
		dif.put("ac_emid", employee.getEm_id());
		dif.put("ac_recorder", "'" + employee.getEm_name() + "'");
		dif.put("ac_status", "'" + BaseUtil.getLocalMessage("ENTERING") + "'");
		dif.put("ac_statuscode", "'ENTERING'");
		dif.put("ac_indate", "sysdate");
		dif.put("ac_ifinit", "null");
		dif.put("ac_vouchercode", "null");
		dif.put("ac_number", acnum);
		dif.put("ac_totaldepreciation", "0");
		dif.put("ac_initstatus", "卡片复制");
		dif.put("ac_monthback", "0");
		dif.put("ac_ymonth", "0");
		baseDao.copyRecord("AssetsCard", "AssetsCard", "ac_id=" + id, dif);
		baseDao.execute("update AssetsCard set ac_kind=(select ak_name from AssetsKind where ac_kindid=ak_id) where ac_id=" + nId);
		// Copy 凭证明细
		JSONObject obj = new JSONObject();
		obj.put("ac_id", nId);
		return obj;
	}

	@Override
	public void updateusestatus(int ac_id, String usestatus) {
		baseDao.execute("update AssetsCard set ac_usestatus='" + usestatus + "' where ac_id=" + ac_id);
		baseDao.logger.others("更新使用状况", "msg.updateSuccess", "AssetsCard", "ac_id", ac_id);
	}

	@Override
	public JSONObject getAssetsCardCodeNum(String caller, Object kind) {
		JSONObject obj = new JSONObject();
		String number = "";
		String code = "";
		Object[] objs = baseDao.getFieldsDataByCondition("assetskind", new String[] { "nvl(ak_maxnumber,0)", "ak_leadcode",
				"nvl(ak_length,0)" }, "ak_id=" + kind);
		if (objs != null && objs[1] != null) {
			String leadcode = objs[1].toString();
			int ret = Integer.parseInt(objs[0].toString());
			int length = Integer.parseInt(objs[2].toString());
			if (baseDao.isDBSetting(caller, "autoCode")) {
				if (length == 0) {
					number = baseDao.getJdbcTemplate().queryForObject(
							"select nvl(min(b), 0) from (select tab.a, '" + leadcode
									+ "'||rownum b from (SELECT ac_code a FROM AssetsCard WHERE ac_kindid=? and ac_code like '" + leadcode
									+ "%' order by ac_code) tab) where a>b", String.class, kind);
					if ("0".equals(number)) {
						number = String.valueOf(ret + 1);
						code = leadcode + number;
						baseDao.updateByCondition("assetskind", "ak_maxnumber=" + (ret + 1), "ak_id=" + kind);
					} else {
						code = number;
					}
				} else {
					number = baseDao.getJdbcTemplate().queryForObject(
							"select nvl(min(b), 0) from (select tab.a, '" + leadcode + "'||LPAD(rownum, " + length
									+ ", '0') b from (SELECT ac_code a FROM AssetsCard WHERE ac_kindid=? and ac_code like '" + leadcode
									+ "%' order by ac_code) tab) where a>b", String.class, kind);
					if ("0".equals(number)) {
						number = String.valueOf(ret + 1);
						length -= String.valueOf(number).length();
						String no = "";
						for (int i = 0; i < length; i++) {
							no += "0";
						}
						code = leadcode + no + number;
						baseDao.updateByCondition("assetskind", "ak_maxnumber=" + (ret + 1), "ak_id=" + kind);
					} else {
						code = number;
					}
				}
				obj.put("number", 0);
				obj.put("code", code);
				return obj;
			} else {
				ret++;
				baseDao.updateByCondition("assetskind", "ak_maxnumber=" + ret, "ak_id=" + kind);
				number += String.valueOf(ret);
				code = leadcode + number;
				obj.put("number", 0);
				obj.put("code", code);
				return obj;
			}
		} else {
			if (!baseDao.isDBSetting(caller, "autoCode")) {
				code = baseDao.sGetMaxNumber(caller, 2);
				SqlRowList rs = baseDao.queryForRowSet("select mn_number FROM maxnumbers where mn_tablename='" + caller + "'");
				if (rs.next()) {
					number = rs.getGeneralString("mn_number");
					baseDao.updateByCondition("maxnumbers", "mn_number=nvl(mn_number,0) + " + 1, "mn_tablename='" + caller + "'");
				}
				obj.put("number", number);
				obj.put("code", code);
				return obj;
			} else {
				int ret = baseDao.getFieldValue("AssetsCard", "max(ac_number)", "nvl(ac_number,0)<>0", Integer.class);
				number = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select MIN(t.num) FROM (select nvl(ac_number,0)+1 num from AssetsCard) t WHERE t.num not in(select nvl(ac_number,0) from AssetsCard)",
								String.class);
				SqlRowList rs = baseDao.queryForRowSet("select mn_leadcode, mn_maxreturn FROM maxnumbers where mn_tablename='" + caller
						+ "'");
				code = number;
				if (rs.next() && rs.getObject(1) != null && !"".equals(rs.getObject(1))) {
					int length = rs.getGeneralInt("mn_maxreturn");
					length = length >= 20 ? 10 : length;
					length -= String.valueOf(number).length();
					String no = "";
					if (length > 0) {
						for (int i = 0; i < length; i++) {
							no += "0";
						}
						code = (rs.getObject("mn_leadcode") == null ? "" : rs.getObject("mn_leadcode")) + no + number;
					} else {
						code = (rs.getObject("mn_leadcode") == null ? "" : rs.getObject("mn_leadcode")) + number;
					}
				}
				baseDao.updateByCondition("maxnumbers", "mn_number=" + (ret + 1), "mn_tablename='" + caller + "'");
				obj.put("number", number);
				obj.put("code", code);
				return obj;
			}
		}
	}
}
