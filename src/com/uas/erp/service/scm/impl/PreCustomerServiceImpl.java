package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.PreCustomerDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.scm.PreCustomerService;

@Service("preCustomereService")
public class PreCustomerServiceImpl implements PreCustomerService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private PreCustomerDao preCustomerDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	@Transactional
	public void savePreCustomer(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object cuid = store.get("cu_id");
		Object cucode = store.get("cu_code");
		checkCustCode(cuid, cucode);
		String nameerror = checkName(cucode, store.get("cu_name"), "PreCustomer", cuid);
		String shortnameerror = checkShortName(cucode, store.get("cu_shortname"), "PreCustomer", cuid);
		if (store.get("cu_arcode") == null || store.get("cu_arcode").equals("")) {
			store.put("cu_arcode", store.get("cu_code"));
			store.put("cu_arname", store.get("cu_name"));
		}
		if (store.get("cu_shcustcode") == null || store.get("cu_shcustcode").equals("")) {
			store.put("cu_shcustcode", store.get("cu_code"));
			store.put("cu_shcustname", store.get("cu_name"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave("PreCustomer", new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PreCustomer", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		Object arname = baseDao.getFieldDataByCondition("Customer", "nvl(cu_name,' ')", "cu_code='" + store.get("cu_arcode") + "'");
		baseDao.execute("update PreCustomer set cu_arname='" + StringUtil.nvl(arname, " ") + "' where cu_code<>cu_arcode and cu_id=" + cuid);
		baseDao.execute("update PreCustomer set cu_arname=cu_name where cu_code=cu_arcode and cu_id=" + cuid);
		Object shname = baseDao.getFieldDataByCondition("Customer", "nvl(cu_name,' ')", "cu_code='" + store.get("cu_shcustcode") + "'");
		baseDao.execute("update PreCustomer set cu_shcustname='" + StringUtil.nvl(shname, " ")
				+ "' where cu_code<>cu_shcustcode and cu_id=" + cuid);
		baseDao.execute("update PreCustomer set cu_shcustname=cu_name where cu_code=cu_shcustcode and cu_id=" + cuid);
		baseDao.logger.save("PreCustomer", "cu_id", store.get("cu_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave("PreCustomer", new Object[] { store });
		defaultCurrencyRate("PreCustomer", cuid);
		if (nameerror != null) {
			BaseUtil.appendError(nameerror);
		}
		if (shortnameerror != null) {
			BaseUtil.appendError(shortnameerror);
		}
	}

	public void checkarshcode(Object cu_id, Object arcode, Object shcode, Object code) {
		String dets = null;
		if (!arcode.equals(code)) {
			dets = baseDao.getJdbcTemplate()
					.queryForObject("select WM_CONCAT(cu_code) from Customer where cu_code=?", String.class, arcode);
			if (dets == null) {
				BaseUtil.showError("应收客户不存在!应收客户号：" + arcode);
			}
		}
		if (!shcode.equals(code)) {
			dets = baseDao.getJdbcTemplate()
					.queryForObject("select WM_CONCAT(cu_code) from Customer where cu_code=?", String.class, shcode);
			if (dets == null) {
				BaseUtil.showError("收货客户不存在!收货客户号：" + shcode);
			}
		}
	}

	private void checkCustCode(Object cu_id, Object cucode) {
		// 判断客户编号在客户资料中是否存在重复
		String dets = baseDao.getJdbcTemplate().queryForObject("select WM_CONCAT(cu_code) from Customer where cu_code=?", String.class,
				cucode);
		if (dets != null) {
			BaseUtil.showError("客户编号在客户资料表中已存在!客户编号：" + dets);
		}
		// 判断客户编号在新客户引进资料中是否存在重复
		dets = baseDao.getJdbcTemplate().queryForObject("select WM_CONCAT(cu_code) from PreCustomer where cu_code=? and cu_id<>?",
				String.class, cucode, cu_id);
		if (dets != null) {
			BaseUtil.showError("客户编号在客户引进申请中已存在!客户编号：" + dets);
		}
	}

	/**
	 * 客户全称重复
	 * 
	 * @param vCode
	 *            客户号
	 * @param name
	 *            客户全称
	 * @param language
	 *            语言
	 * @throws RuntimeException
	 */
	private String checkName(Object vCode, Object name, String caller, Object vid) {
		Object precode = baseDao.getFieldDataByCondition("PreCustomer", "cu_code", "cu_id <> " + vid + " AND cu_name='" + name + "'");
		Object code = baseDao.getFieldDataByCondition("Customer", "cu_code", "cu_code <> '" + vCode + "' AND cu_name='" + name + "'");
		if (precode != null) {
			if (baseDao.isDBSetting("Customer!Base", "allowNameRepeat"))
				return "客户名称在客户引进申请中已存在，申请单号：" + precode;
			else
				BaseUtil.showError("客户名称在客户引进申请中已存在，申请单号：" + precode);
		}
		if (code != null) {
			if (baseDao.isDBSetting("Customer!Base", "allowNameRepeat"))
				return "客户名称在客户资料中已存在，客户编号：" + code;
			else
				BaseUtil.showError("客户名称在客户资料中已存在，客户编号：" + code);
		}
		return null;
	}

	/**
	 * 客户简称重复
	 * 
	 * @param vCode
	 *            客户号
	 * @param name
	 *            客户简称
	 * @param language
	 *            语言
	 * @throws RuntimeException
	 */
	private String checkShortName(Object vCode, Object name, String caller, Object vid) {
		Object precode = baseDao.getFieldDataByCondition("PreCustomer", "cu_code", "cu_id <> " + vid + " AND cu_shortname='" + name + "'");
		Object code = baseDao.getFieldDataByCondition("Customer", "cu_code", "cu_code <> '" + vCode + "' AND cu_shortname='" + name + "'");
		if (precode != null) {
			if (baseDao.isDBSetting("Customer!Base", "allowShortNameRepeat"))
				return "客户简称在客户引进申请中已存在，申请单号：" + precode;
			else
				BaseUtil.showError("客户简称在新客户引进申请中已存在，申请单号：" + precode);
		}
		if (code != null) {
			if (baseDao.isDBSetting("Customer!Base", "allowShortNameRepeat"))
				return "客户简称在新客户资料中已存在，客户编号：" + code;
			else
				BaseUtil.showError("客户简称在新客户资料中已存在，客户编号：" + code);
		}
		return null;
	}

	@Override
	public void deletePreCustomer(int cu_id) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("PreCustomer", "cu_auditstatuscode", "cu_id=" + cu_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler("PreCustomer", "delete", "before", new Object[] { cu_id });
		// 删除
		baseDao.deleteById("PreCustomer", "cu_id", cu_id);
		// 记录操作
		baseDao.logger.save("PreCustomer", "cu_id", cu_id);
		// 执行删除后的其它逻辑
		handlerService.handler("PreCustomer", "delete", "after", new Object[] { cu_id });
	}

	@Override
	public void updatePreCustomerById(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的采购单资料!
		Object status = baseDao.getFieldDataByCondition("PreCustomer", "cu_auditstatuscode", "cu_id=" + store.get("cu_id"));
		StateAssert.updateOnlyEntering(status);
		Object cuid = store.get("cu_id");
		checkCustCode(cuid, store.get("cu_code"));
		if (store.get("cu_shcustcode") == null || store.get("cu_shcustcode").equals("")) {
			store.put("cu_shcustcode", store.get("cu_code"));
			store.put("cu_shcustname", store.get("cu_name"));
		}
		if (store.get("cu_arcode") == null || store.get("cu_arcode").equals("")) {
			store.put("cu_arcode", store.get("cu_code"));
			store.put("cu_arname", store.get("cu_name"));
		}
		String nameerror = checkName(store.get("cu_code"), store.get("cu_name"), "PreCustomer", store.get("cu_id"));
		String shortnameerror = checkShortName(store.get("cu_code"), store.get("cu_shortname"), "PreCustomer", store.get("cu_id"));
		checkarshcode(cuid, store.get("cu_arcode"), store.get("cu_shcustcode"), store.get("cu_code"));
		// 执行修改前的其它逻辑
		handlerService.handler("PreCustomer", "save", "before", new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PreCustomer", "cu_id");
		baseDao.execute(formSql);
		Object arname = baseDao.getFieldDataByCondition("Customer", "nvl(cu_name,' ')", "cu_code='" + store.get("cu_arcode") + "'");
		baseDao.execute("update PreCustomer set cu_arname='" + StringUtil.nvl(arname, " ") + "' where cu_code<>cu_arcode and cu_id=" + cuid);
		baseDao.execute("update PreCustomer set cu_arname=cu_name where cu_code=cu_arcode and cu_id=" + cuid);
		Object shname = baseDao.getFieldDataByCondition("Customer", "nvl(cu_name,' ')", "cu_code='" + store.get("cu_shcustcode") + "'");
		baseDao.execute("update PreCustomer set cu_shcustname='" + StringUtil.nvl(shname, " ")
				+ "' where cu_code<>cu_shcustcode and cu_id=" + cuid);
		baseDao.execute("update PreCustomer set cu_shcustname=cu_name where cu_code=cu_shcustcode and cu_id=" + cuid);
		// 记录操作
		baseDao.logger.update("PreCustomer", "cu_id", cuid);
		// 执行修改后的其它逻辑
		handlerService.handler("PreCustomer", "save", "after", new Object[] { store });
		defaultCurrencyRate("PreCustomer", cuid);
		if (nameerror != null) {
			BaseUtil.appendError(nameerror);
		}
		if (shortnameerror != null) {
			BaseUtil.appendError(shortnameerror);
		}
	}

	@Override
	public void printPreCustomer(int cu_id) {
		// 执行打印前的其它逻辑
		handlerService.beforePrint("PreCustomer", cu_id);
		// 执行打印操作
		// TODO
		// 记录操作
		baseDao.logger.print("PreCustomer", "cu_id", cu_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint("PreCustomer", cu_id);
	}

	@Override
	public void auditPreCustomer(int cu_id) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("PreCustomer", new String[] { "cu_auditstatuscode", "cu_code", "cu_shcustcode",
				"cu_arcode", "cu_name", "cu_shortname" }, "cu_id=" + cu_id);
		StateAssert.auditOnlyCommited(status[0]);
		String nameerror = checkName(status[1], status[4], "PreCustomer", cu_id);
		String shortnameerror = checkShortName(status[1], status[5], "PreCustomer", cu_id);
		checkarshcode(cu_id, status[3], status[2], status[1]);
		defaultCurrencyRate("PreCustomer", cu_id);
		allowZeroTax("PreCustomer", cu_id);
		// 执行审核前的其它逻辑
		handlerService.handler("PreCustomer", "audit", "before", new Object[] { cu_id });
		// 执行审核操作
		baseDao.audit("PreCustomer", "cu_id=" + cu_id, "cu_auditstatus", "cu_auditstatuscode", "cu_auditdate", "cu_auditman");
		// 转正式客户
		if (!baseDao.isDBSetting("PreCustomer", "noAutoCust")) {
			int cuid = turnCustomer(cu_id);
			Employee employee = SystemSession.getUser();
			if (baseDao.isDBSetting("PreCustomer", "autoSync")) {
				Object custatus = baseDao.getFieldDataByCondition("Customer", "cu_auditstatuscode", "cu_id=" + cuid);
				if (custatus != null && "AUDITED".equals(custatus)) {
					Master master = employee.getCurrentMaster();
					if (master != null && master.getMa_soncode() != null) {// 资料中心
						String res = null;
						res = baseDao.callProcedure("SYS_POST", new Object[] { "Customer!Post", SpObserver.getSp(), master.getMa_soncode(),
								String.valueOf(cu_id), employee.getEm_name(), employee.getEm_id() });
						if (res != null) {
							BaseUtil.appendError(res);
						}
					}
				}
			}
		}
		// 记录操作
		baseDao.logger.audit("PreCustomer", "cu_id", cu_id);
		// 执行审核后的其它逻辑
		handlerService.handler("PreCustomer", "audit", "after", new Object[] { cu_id });
		if (nameerror != null) {
			BaseUtil.appendError(nameerror);
		}
		if (shortnameerror != null) {
			BaseUtil.appendError(shortnameerror);
		}
	}

	@Override
	public void resAuditPreCustomer(int cu_id) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("PreCustomer", "cu_auditstatuscode", "cu_id=" + cu_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("PreCustomer", "cu_id=" + cu_id, "cu_auditstatus", "cu_auditstatuscode");
		// 记录操作
		baseDao.logger.resAudit("PreCustomer", "cu_id", cu_id);
	}

	@Override
	public void submitPreCustomer(int cu_id) {
		if (baseDao.isDBSetting("Customer!Base", "creditControl")) {
			baseDao.execute("update precustomer set cu_enablecredit=(select pa_creditcontrol from payments where pa_code=cu_paymentscode and pa_class='收款方式') where cu_id="
					+ cu_id + " and nvl(cu_paymentscode,' ')<>' '");
		}
		// 只能对状态为[在录入]的订单进行提交操作!
		Object[] obj = baseDao.getFieldsDataByCondition("PreCustomer", new String[] { "cu_auditstatuscode", "cu_code", "cu_shcustcode",
				"cu_arcode", "cu_name", "cu_shortname", "cu_enablecredit", "cu_paymentscode" }, "cu_id=" + cu_id);
		StateAssert.submitOnlyEntering(obj[0]);
		String nameerror = checkName(obj[1], obj[4], "PreCustomer", cu_id);
		String shortnameerror = checkShortName(obj[1], obj[5], "PreCustomer", cu_id);
		checkarshcode(cu_id, obj[3], obj[2], obj[1]);
		// 判断客户资料中的收款方式的额度是否管控与收款方式表中设置的是否管控是否一致，不一致则提示
		if (obj[6] != null) {
			if (baseDao.checkIf("Payments", "pa_creditcontrol is not null and pa_code='" + obj[7] + "' and pa_creditcontrol!='" + obj[6] + "'and pa_class='收款方式''"
					+ "'")) {
				BaseUtil.showError("该客户的收款方式对应的是否额度管控与收款方式基础设置中对应的是否额度管控不一致，请修改后再提交");
			}
		}
		/*// 对于集团版去资料中心查找相应的编号是否存在
		if (BaseUtil.isGroup()) {
			String dataSop = BaseUtil.getXmlSetting("dataSob");
			if (dataSop != null) {
				if (baseDao.checkIf(dataSop + ".Customer", "cu_code='" + obj[1] + "'")) {
					BaseUtil.showError("改客户编号已经存在于资料中心!不能重复转入!");
				}
			}
		}*/
		if (!obj[1].equals(obj[2])) {
			int count = baseDao.getCountByCondition("Customer", "cu_code='" + obj[2] + "'");
			if (count == 0) {
				BaseUtil.showError("收货客户在不存在，不允许提交!客户编号：" + obj[2]);
			}
		}
		if (!obj[1].equals(obj[3])) {
			int count = baseDao.getCountByCondition("Customer", "cu_code='" + obj[3] + "'");
			if (count == 0) {
				BaseUtil.showError("应收客户在不存在，不允许提交!客户编号：" + obj[3]);
			}
		}
		defaultCurrencyRate("PreCustomer", cu_id);
		allowZeroTax("PreCustomer", cu_id);
		// 执行提交前的其它逻辑
		handlerService.handler("PreCustomer", "commit", "before", new Object[] { cu_id });
		// 执行提交操作
		baseDao.submit("PreCustomer", "cu_id=" + cu_id, "cu_auditstatus", "cu_auditstatuscode");
		// 记录操作
		baseDao.logger.submit("PreCustomer", "cu_id", cu_id);
		// 执行提交后的其它逻辑
		handlerService.handler("PreCustomer", "commit", "after", new Object[] { cu_id });
		if (nameerror != null) {
			BaseUtil.appendError(nameerror);
		}
		if (shortnameerror != null) {
			BaseUtil.appendError(shortnameerror);
		}
	}

	@Override
	public void resSubmitPreCustomer(int cu_id) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("PreCustomer", "cu_auditstatuscode", "cu_id=" + cu_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler("PreCustomer", "resCommit", "before", new Object[] { cu_id });
		// 执行反提交操作
		baseDao.resOperate("PreCustomer", "cu_id=" + cu_id, "cu_auditstatus", "cu_auditstatuscode");
		// 记录操作
		baseDao.logger.resSubmit("PreCustomer", "cu_id", cu_id);
		handlerService.handler("PreCustomer", "resCommit", "after", new Object[] { cu_id });
	}

	@Override
	public int turnCustomer(int cu_id) {
		int cuid = 0;
		// 判断该客户申请单是否已经转入过客户
		Object code = baseDao.getFieldDataByCondition("Customer", "cu_code", "cu_sourceid=" + cu_id);
		if (StringUtil.hasText(code)) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.precustomer.haveturn") + code);
		} else {
			// 转客户
			cuid = preCustomerDao.turnCustomer(cu_id);
			baseDao.execute("update CustomerDistr set cd_custcode=(select cu_code from customer where cd_cuid=cu_id) where cd_cuid=" + cuid);
		}
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "转客户操作", "转入成功", "PreCustomer|cu_id=" + cu_id));
		return cuid;
	}

	// 本位币允许税率为0
	private void allowZeroTax(String caller, Object cu_id) {
		if (!baseDao.isDBSetting("Sale", "allowZeroTax")) {
			String currency = baseDao.getDBSetting("defaultCurrency");
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select WM_CONCAT(cu_currency) from PreCustomer where nvl(cu_taxrate,0)=0 and cu_currency='" + currency
							+ "' and cu_id=?", String.class, cu_id);
			if (dets != null) {
				BaseUtil.showError("本位币税率为0，不允许进行当前操作!币别：" + dets);
			}
		}
	}

	// 税率强制等于币别表的默认税率
	private void defaultCurrencyRate(String caller, Object cu_id) {
		if (baseDao.isDBSetting("Customer!Base", "defaultCurrencyRate")) {
			baseDao.execute("update Precustomer set cu_taxrate=(select nvl(cr_taxrate,0) from currencys where cu_currency=cr_name and cr_statuscode='CANUSE')"
					+ " where cu_id=" + cu_id);
		}
	}
}
