package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.drools.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.Assert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.scm.CreditChangeService;

@Service("creditChangeService")
public class CreditChangeServiceImpl implements CreditChangeService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private EnterpriseService enterpriseService;

	@Override
	public void auditCreditChange(int cc_id) {
		// 只能对状态为[已提交]的订单进行审核操作!
		SqlRowList rs = baseDao.queryForRowSet(
				"select cc_statuscode,cc_newcredit,cc_custcode,cc_newtempcredit,cc_newtempcreditdate from CreditChange where cc_id=?",
				cc_id);
		if (rs.next()) {
			Object custcode = rs.getObject("cc_custcode");
			if (StringUtil.hasText(custcode)) {
				Assert.isEquals("common.audit_onlyCommited", "COMMITED", rs.getObject("cc_statuscode"));
				if (rs.getGeneralDouble("cc_newtempcredit") != 0 && StringUtil.hasText(rs.getObject("cc_newtempcreditdate"))) {
					baseDao.execute("UPDATE customercredit SET cuc_tempcredit=? where cuc_custcode=?",
							rs.getGeneralDouble("cc_newtempcredit"), custcode);
					baseDao.execute("update customercredit set cuc_tempcreditdate=to_date('"
							+ rs.getObject("cc_newtempcreditdate").toString().substring(0, 10) + "','yyyy-mm-dd') where cuc_custcode='"
							+ custcode + "'");
				}
				baseDao.execute("UPDATE customercredit SET cuc_credit=? where cuc_custcode=?", rs.getGeneralDouble("cc_newcredit"),
						custcode);
				baseDao.execute("UPDATE customer SET cu_credit=? where cu_code=?", rs.getGeneralDouble("cc_newcredit"), custcode);
			} else {
				BaseUtil.showError("请选择客户！");
			}
			// 执行审核操作
			baseDao.audit("CreditChange", "cc_id=" + cc_id, "cc_status", "cc_statuscode");
			// 记录操作
			baseDao.logger.audit("CreditChange", "cc_id", cc_id);
			// 执行审核后的其它逻辑
			handlerService.handler("CreditChange", "audit", "after", new Object[] { cc_id });
		}
	}

	@Override
	public void auditVendCreditChange(int vc_id) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("VendCreditChange", new String[] { "vc_statuscode", "nvl(vc_newcredit,0)",
				"vc_vendcode" }, "vc_id=" + vc_id);
		Assert.isEquals("common.audit_onlyCommited", "COMMITED", status[0]);
		// 执行审核前的其它逻辑
		handlerService.handler("VendCreditChange", "audit", "before", new Object[] { vc_id });
		// 更新客户资料
		if (status[2] != null) {
			baseDao.execute("UPDATE vendorcredit SET vec_credit=? where vec_vendcode=?", status[1], status[2]);
			baseDao.execute("UPDATE vendor SET ve_credit=? where ve_code=?", status[1], status[2]);
		}
		// 执行审核操作
		baseDao.updateByCondition("VendCreditChange", "vc_statuscode='AUDITED',vc_status='" + BaseUtil.getLocalMessage("AUDITED") + "'",
				"vc_id=" + vc_id);
		// 记录操作
		baseDao.logger.audit("VendCreditChange", "vc_id", vc_id);
		// 执行审核后的其它逻辑
		handlerService.handler("VendCreditChange", "audit", "after", new Object[] { vc_id });
	}

	@Override
	public void auditCustomerCredit(int cuc_id) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("CustomerCredit", new String[] { "cuc_statuscode", "cuc_custcode" }, "cuc_id="
				+ cuc_id);
		Assert.isEquals("common.audit_onlyCommited", "COMMITED", status[0]);
		// 执行审核前的其它逻辑
		handlerService.handler("CustomerCredit", "audit", "before", new Object[] { cuc_id });
		// 执行审核操作
		baseDao.updateByCondition("CustomerCredit", "cuc_statuscode='AUDITED',cuc_status='" + BaseUtil.getLocalMessage("AUDITED") + "'",
				"cuc_id=" + cuc_id);
		baseDao.execute("update customer set cu_enablecredit='是' where cu_code in (select cuc_custcode from customercredit where cuc_id="
				+ cuc_id + "  and nvl(cuc_custcode,' ') <>' ')");
		List<String> allMasters = new ArrayList<String>();
		Master master = SystemSession.getUser().getCurrentMaster();
		allMasters.add(master.getMa_user());
		Master parentMaster = null;
		final List<String> Sqls = new ArrayList<String>();
		if (master != null && !StringUtils.isEmpty(master.getMa_soncode())) {// 父级账套
			parentMaster = master;
		} else if (null != master.getMa_pid() && master.getMa_pid() > 0) {
			parentMaster = enterpriseService.getMasterByID(master.getMa_pid());
		}
		if (null != parentMaster && !StringUtils.isEmpty(parentMaster.getMa_soncode())) {
			allMasters.addAll(Arrays.asList(parentMaster.getMa_soncode().split(",")));
			for (String son : allMasters) {
				Sqls.add("UPDATE " + son
						+ ".customer set cu_enablecredit='是' where cu_code in (select cuc_custcode from customercredit where cuc_id="
						+ cuc_id + "  and nvl(cuc_custcode,' ') <>' ') and nvl(cu_enablecredit,'否')='否'");
			}
			baseDao.execute(Sqls);
		}
		// 记录操作
		baseDao.logger.audit("CustomerCredit", "cuc_id", cuc_id);
		// 执行审核后的其它逻辑
		handlerService.handler("CustomerCredit", "audit", "after", new Object[] { cuc_id });
	}

	@Override
	public void saveCreditChange(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		boolean bool = baseDao.checkIf("CreditChange", "cc_code = '" + store.get("cc_code") + "'");
		if (bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		handlerService.handler(caller, "save", "before", new Object[] { store });
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "CreditChange"));
		updateCustCredit(store.get("cc_custcode"));
		baseDao.logger.save(caller, "cc_id", store.get("cc_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });

	}

	@Override
	public void updateCreditChangeById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		boolean bool = baseDao.checkIf("CreditChange", "cc_code = '" + store.get("cc_code") + "' and cc_id<>" + store.get("cc_id"));
		if (bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		handlerService.handler(caller, "update", "before", new Object[] { store });
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "CreditChange", "cc_id"));
		updateCustCredit(store.get("cc_custcode"));
		baseDao.logger.update(caller, "cc_id", store.get("cc_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "update", "after", new Object[] { store });
	}

	void updateCustCredit(Object cucode) {
		Double B = 0.0;// 取出这个客户的应收-预收的余额折算为本位币
		Double C = 0.0;// 未过账发货单的金额(变量C)
		Double D = 0.0;// 未结案的通知单未转出货单部分(变量D)
		Double F = 0.0;// 已提交审核未结案的订单未转出的订单本币金额(变量F)
		if ("group".equals(baseDao.getDBSetting("creditMethod"))) {
			List<String> allMasters = new ArrayList<String>();
			Master master = SystemSession.getUser().getCurrentMaster();
			allMasters.add(master.getMa_user());
			Master parentMaster = null;
			if (master != null && !StringUtils.isEmpty(master.getMa_soncode())) {// 父级账套
				parentMaster = master;
			} else if (null != master.getMa_pid() && master.getMa_pid() > 0) {
				parentMaster = enterpriseService.getMasterByID(master.getMa_pid());
			}
			if (null != parentMaster && !StringUtils.isEmpty(parentMaster.getMa_soncode())) {
				allMasters.addAll(Arrays.asList(parentMaster.getMa_soncode().split(",")));
				for (String son : allMasters) {
					B = B
							+ baseDao.getJdbcTemplate().queryForObject(
									"select nvl(round(sum(nvl((nvl(ca_amount,0)-nvl(ca_prepayamount,0))*nvl(cr_rate,0),0)),0),0) from "
											+ son + ".custar," + son + ".currencys where ca_currency=cr_name and ca_custcode=?",
									new Object[] { cucode }, Double.class);
					C = C
							+ baseDao
									.getJdbcTemplate()
									.queryForObject(
											"select nvl(round(sum(nvl(pd_outqty*pd_sendprice*pi_rate,0)),0),0) from "
													+ son
													+ ".prodinout,"
													+ son
													+ ".prodiodetail,"
													+ son
													+ ".currencys where pi_id=pd_piid and pi_currency=cr_name and pi_statuscode<>'POSTED' and pi_class='出货单' and pi_cardcode=?",
											new Object[] { cucode }, Double.class);

					D = D
							+ baseDao
									.getJdbcTemplate()
									.queryForObject(
											"select nvl(round(nvl(sum((snd_outqty-nvl(snd_yqty,0))*snd_sendprice*sn_rate),0),0),0) from "
													+ son
													+ ".sendnotify,"
													+ son
													+ ".sendnotifydetail,"
													+ son
													+ ".currencys where sn_id=snd_snid and sn_currency=cr_name and sn_custcode=? and sn_statuscode in ('ENTERING','COMMITED','AUDITED') and nvl(snd_statuscode,' ')<>'FINISH' and nvl(snd_statuscode,' ')<>'FREEZE'",
											new Object[] { cucode }, Double.class);
					F = F
							+ baseDao
									.getJdbcTemplate()
									.queryForObject(
											"select nvl(round(nvl(sum((sd_qty-nvl(sd_yqty,0))*sd_price*sa_rate),0),0),0) from "
													+ son
													+ ".sale,"
													+ son
													+ ".saledetail,"
													+ son
													+ ".currencys where sa_id=sd_said and nvl(sa_statuscode,' ') in ('AUDITED','COMMITED') and nvl(sd_statuscode,' ')<>'FINISH' and nvl(sd_statuscode,' ')<>'FREEZE' and sa_currency=cr_name and sa_custcode=?",
											new Object[] { cucode }, Double.class);
				}
				if (baseDao.isDBSetting("creditNoSale")) {
					baseDao.execute("update " + parentMaster.getMa_user() + ".CreditChange set cc_usedcredit=" + (B + C + D)
							+ " where cc_custcode='" + cucode + "'");
				} else {
					baseDao.execute("update " + parentMaster.getMa_user() + ".CreditChange set cc_usedcredit=" + (B + C + D + F)
							+ " where cc_custcode='" + cucode + "'");
				}
			}
		} else {
			B = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select nvl(round(sum(nvl((nvl(ca_amount,0)-nvl(ca_prepayamount,0))*nvl(cr_rate,0),0)),0),0) from custar,currencys where ca_currency=cr_name and ca_custcode=?",
							new Object[] { cucode }, Double.class);
			C = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select nvl(round(sum(nvl(pd_outqty*pd_sendprice*pi_rate,0)),0),0) from prodinout,prodiodetail,currencys where pi_id=pd_piid and pi_currency=cr_name and pi_statuscode<>'POSTED' and pi_class='出货单' and pi_cardcode=?",
							new Object[] { cucode }, Double.class);
			D = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select nvl(round(nvl(sum((snd_outqty-nvl(snd_yqty,0))*snd_sendprice*sn_rate),0),0),0) from sendnotify,sendnotifydetail,currencys where sn_id=snd_snid and sn_currency=cr_name and sn_custcode=? and sn_statuscode in ('ENTERING','COMMITED','AUDITED') and nvl(snd_statuscode,' ')<>'FINISH' and nvl(snd_statuscode,' ')<>'FREEZE'",
							new Object[] { cucode }, Double.class);
			F = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select nvl(round(nvl(sum((sd_qty-nvl(sd_yqty,0))*sd_price*sa_rate),0),0),0) from sale,saledetail,currencys where sa_id=sd_said and nvl(sa_statuscode,' ') in ('AUDITED','COMMITED') and nvl(sd_statuscode,' ')<>'FINISH' and nvl(sd_statuscode,' ')<>'FREEZE' and sa_currency=cr_name and sa_custcode=?",
							new Object[] { cucode }, Double.class);
			if (baseDao.isDBSetting("creditNoSale")) {
				baseDao.execute("update CreditChange set cc_usedcredit=" + (B + C + D) + " where cc_custcode='" + cucode + "'");
			} else {
				baseDao.execute("update CreditChange set cc_usedcredit=" + (B + C + D + F) + " where cc_custcode='" + cucode + "'");
			}
		}
	}
}
