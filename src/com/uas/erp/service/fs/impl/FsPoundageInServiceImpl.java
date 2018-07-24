package com.uas.erp.service.fs.impl;

import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.fa.AccountRegisterBankService;
import com.uas.erp.service.fs.FsPoundageInService;

@Service
public class FsPoundageInServiceImpl implements FsPoundageInService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private AccountRegisterBankService accountRegisterBankService;

	@Override
	public void saveFsPoundageIn(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[] { store });
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "FsPoundageIn"));
		baseDao.execute("update FsPoundageIn set pi_handamount=round(nvl(pi_saamount,0)*nvl(pi_handrate,0)/100,2) where pi_id="
				+ store.get("pi_id"));
		baseDao.logger.save(caller, "pi_id", store.get("pi_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void updateFsPoundageIn(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[] { store });
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "FsPoundageIn", "pi_id"));
		baseDao.execute("update FsPoundageIn set pi_handamount=round(nvl(pi_saamount,0)*nvl(pi_handrate,0)/100,2) where pi_id="
				+ store.get("pi_id"));
		// 记录操作
		baseDao.logger.update(caller, "pi_id", store.get("pi_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void deleteFsPoundageIn(int pi_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { pi_id });
		// 删除主表内容
		baseDao.deleteById("FsPoundageIn", "pi_id", pi_id);
		baseDao.logger.delete(caller, "pi_id", pi_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { pi_id });
	}

	@Override
	public void submitFsPoundageIn(int pi_id, String caller) {
		baseDao.execute("update FsPoundageIn set pi_handamount=round(nvl(pi_saamount,0)*nvl(pi_handrate,0)/100,2) where pi_id=" + pi_id);
		// 只能对状态为[在录入]的表单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("FsPoundageIn", new String[] { "pi_statuscode", "nvl(pi_thisamount,0)",
				"nvl(pi_handamount,0)" }, "pi_id=" + pi_id);
		StateAssert.submitOnlyEntering(status[0]);
		double thisamount = Double.parseDouble(status[1].toString());
		double handamount = Double.parseDouble(status[2].toString());
		// 逾期利息
		if (thisamount > 0 && handamount > 0 && thisamount < handamount) {
			BaseUtil.showError("本次支付手续费必须大于等于应付手续费！");
		}
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { pi_id });
		// 执行提交操作
		baseDao.submit("FsPoundageIn", "pi_id=" + pi_id, "pi_status", "pi_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pi_id", pi_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { pi_id });
	}

	@Override
	public void resSubmitFsPoundageIn(int pi_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("FsPoundageIn", "pi_statuscode", "pi_id=" + pi_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { pi_id });
		// 执行反提交操作
		baseDao.resOperate("FsPoundageIn", "pi_id=" + pi_id, "pi_status", "pi_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pi_id", pi_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { pi_id });
	}

	@Override
	@Transactional
	public void auditFsPoundageIn(int pi_id, String caller) {
		// 只能对已提交进行审核操作
		Object status = baseDao.getFieldDataByCondition("FsPoundageIn", "pi_statuscode", "pi_id=" + pi_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { pi_id });
		// 审核之后转银行登记
		turnBankRegister(caller, pi_id);
		baseDao.audit("FsPoundageIn", "pi_id=" + pi_id, "pi_status", "pi_statuscode", "pi_auditdate", "pi_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "pi_id", pi_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { pi_id });
	}

	@Override
	public void resAuditFsPoundageIn(int pi_id, String caller) {
		// 只能对状态为[已审核]的单据进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("FsPoundageIn", new String[] { "pi_statuscode", "pi_code" }, "pi_id=" + pi_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		baseDao.resAuditCheck("FsPoundageIn", pi_id);
		// 已存在银行登记
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(ar_code) from AccountRegister where ar_sourcetype='手续费入账单' and ar_sourceid=?", String.class, pi_id);
		if (dets != null) {
			BaseUtil.showError("已存在银行登记[" + dets + "]，不允许反审核！");
		}
		handlerService.beforeResAudit(caller, new Object[] { pi_id });
		// 执行反审核操作
		baseDao.resAudit("FsPoundageIn", "pi_id=" + pi_id, "pi_status", "pi_statuscode", "pi_auditman", "pi_auditdate");
		// 记录操作
		baseDao.logger.resAudit(caller, "pi_id", pi_id);
		handlerService.afterResAudit(caller, new Object[] { pi_id });
	}

	/**
	 * 转银行登记
	 */
	@Override
	public JSONObject turnBankRegister(String caller, int pi_id) {
		JSONObject j = null;
		SqlRowList rs = baseDao.queryForRowSet("select * from FsPoundageIn where pi_id=? ", pi_id);
		if (rs.next()) {
			if (StringUtil.hasText(rs.getObject("pi_catecode"))) {
				String pi_catecode = rs.getGeneralString("pi_catecode");
				String error = baseDao.getJdbcTemplate().queryForObject(
						"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
						String.class, pi_catecode);
				if (error != null) {
					BaseUtil.showError("填写科目编号不存在，或者状态不等于已审核，或者不是末级科目，不允许转银行登记！");
				}
				error = baseDao.getJdbcTemplate().queryForObject(
						"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(CA_ISCASHBANK,0)=0", String.class,
						pi_catecode);
				if (error != null) {
					BaseUtil.showError("付款科目有误，请填写银行现金科目！");
				}
			} else {
				BaseUtil.showError("请填写银行科目!");
			}
			int ar_id = baseDao.getSeqId("ACCOUNTREGISTER_SEQ");
			String code = baseDao.sGetMaxNumber("AccountRegister", 2);
			baseDao.execute("insert into AccountRegister (ar_id,ar_code,ar_date,ar_recorddate,"
					+ "ar_payment,ar_type,ar_sourceid,ar_source,ar_sourcetype,ar_statuscode,ar_status,ar_recordman,"
					+ "ar_accountcode,ar_accountname,ar_accountcurrency,ar_cateid,ar_memo) select "
					+ ar_id
					+ ", '"
					+ code
					+ "',pi_date,sysdate,pi_thisamount,'其它收款',pi_id,pi_code,'手续费入账单','ENTERING','"
					+ BaseUtil.getLocalMessage("ENTERING")
					+ "',pi_recorder,pi_catecode,pi_catename,ca_currency,ca_id,pi_custname||'手续费收入' from fspoundagein,category where pi_catecode=ca_code and pi_id="
					+ pi_id);
			baseDao.execute("update accountregister set ar_accountrate=nvl((select cm_crrate from currencysmonth where cm_crname=ar_accountcurrency and cm_yearmonth=to_char(ar_date,'yyyymm')),1) where ar_id="
					+ ar_id);
			j = new JSONObject();
			j.put("ar_id", ar_id);
			j.put("ar_code", code);
			accountRegisterBankService.updateErrorString(ar_id);
			baseDao.logger.turn("转银行登记" + code, caller, "pi_id", pi_id);
		}
		return j;
	}
}
