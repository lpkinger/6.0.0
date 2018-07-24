package com.uas.erp.service.fs.impl;

import org.drools.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Master;

@Service("FsHandler")
public class FsHandler {

	@Autowired
	private BaseDao baseDao;

	/**
	 * 保理客户基础资料：审核之后自动同步到子帐套
	 * 
	 * @author madan 2017-10-31 09:31:51
	 * 
	 * @param id
	 */
	public void customerInfor_audit_sync(Integer id) {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master != null && !StringUtils.isEmpty(master.getMa_soncode())) {// 父级账套
			String[] syncSon = baseDao.getDBSettingArray("Customer!Infor", "syncSon");
			if (StringUtil.hasText(syncSon)) {
				String masters = CollectionUtil.toString(syncSon);
				String res = baseDao.callProcedure("SYS_POST",
						new Object[] { "Customer!Infor!Post", SpObserver.getSp(), masters, String.valueOf(id),
								SystemSession.getUser().getEm_name(), SystemSession.getUser().getEm_id() });
				if (StringUtil.hasText(res) && !"ok".equals(res.toLowerCase())) {
					BaseUtil.showError(res);
				}

				// 同步系统客户资料
				Object custname = baseDao.getFieldDataByCondition("CustomerInfor", "cu_name", "cu_id = " + id);
				Integer custid = baseDao.getFieldValue("Customer", "cu_id", "cu_name = '" + custname
						+ "' and cu_auditstatuscode = 'AUDITED'", Integer.class);
				res = baseDao.callProcedure("SYS_POST", new Object[] { "Customer!Post", SpObserver.getSp(), masters,
						String.valueOf(custid), SystemSession.getUser().getEm_name(), SystemSession.getUser().getEm_id() });
				if (StringUtil.hasText(res) && !"ok".equals(res.toLowerCase())) {
					BaseUtil.showError(res);
				}
			}
		}
	}

	/**
	 * 基础合同：审核之后更新审核人，审核日期
	 * 
	 * @author madan 2017-10-31 13:53:54
	 * 
	 * @param id
	 */
	public void fssale_audit_updateAuditman(Integer id) {
		baseDao.execute("update FsSale set auditman='" + SystemSession.getUser().getEm_name() + "',auditdate=sysdate where id=" + id);
	}

	/**
	 * 基础合同：反审核之后更新审核人，审核日期
	 * 
	 * @author madan 2017-10-31 13:56:28
	 * 
	 * @param id
	 */
	public void fssale_resaudit_updateAuditman(Integer id) {
		baseDao.execute("update FsSale set auditman=null,auditdate=null where id=" + id);
	}

	/**
	 * 基础合同：审核之后自动同步到子帐套
	 * 
	 * @author madan 2017-10-31 09:17:28
	 * 
	 * @param id
	 */
	public void fssale_audit_sync(Integer id) {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master != null && !StringUtils.isEmpty(master.getMa_soncode())) {// 父级账套
			String[] syncSon = baseDao.getDBSettingArray("Customer!Infor", "syncSon");
			if (StringUtil.hasText(syncSon)) {
				String masters = CollectionUtil.toString(syncSon);
				String res = baseDao.callProcedure("SYS_POST",
						new Object[] { "FsSale!Post", SpObserver.getSp(), masters, String.valueOf(id),
								SystemSession.getUser().getEm_name(), SystemSession.getUser().getEm_id() });
				if (res != null) {
					BaseUtil.showError(res);
				}
				baseDao.execute("update FsSale set sa_sync='已同步' where id=" + id);
			}
		}
	}

	/**
	 * 核心企业额度申请单：审核之后自动同步到子帐套
	 * 
	 * @author madan 2017-10-31 09:31:14
	 * 
	 * @param id
	 */
	public void customerQuotaApply_audit_sync(Integer id) {
		Object cop = baseDao.getFieldDataByCondition("CustomerQuotaApply", "ca_cop", "ca_id=" + id);
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master != null && !StringUtils.isEmpty(master.getMa_soncode())) {// 父级账套
			String masters = null;
			if (cop != null && !"$ALL".equals(cop) && !"全部".equals(cop)) {
				masters = cop.toString();
			} else {
				String[] syncSon = baseDao.getDBSettingArray("Customer!Infor", "syncSon");
				if (StringUtil.hasText(syncSon)) {
					masters = CollectionUtil.toString(syncSon);
				}
			}
			if (masters != null) {
				String res = baseDao.callProcedure("SYS_POST", new Object[] { "CustomerQuotaApply!Post", SpObserver.getSp(), masters,
						String.valueOf(id), SystemSession.getUser().getEm_name(), SystemSession.getUser().getEm_id() });
				if (res != null) {
					BaseUtil.showError(res);
				}
				baseDao.execute("update CustomerQuotaApply set ca_sync='已同步' where ca_id=" + id);
			}
		}
	}

	/**
	 * 额度申请单：审核之后自动同步到子帐套
	 * 
	 * @author madan 2017-10-31 09:17:28
	 * 
	 * @param id
	 */
	public void customerQuota_audit_sync(Integer id) {
		Object cop = baseDao.getFieldDataByCondition("CustomerQuota", "cq_cop", "cq_id=" + id);
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master != null && !StringUtils.isEmpty(master.getMa_soncode())) {// 父级账套
			String masters = null;
			if (cop != null && !"$ALL".equals(cop) && !"全部".equals(cop)) {
				masters = cop.toString();
			} else {
				String[] syncSon = baseDao.getDBSettingArray("Customer!Infor", "syncSon");
				if (StringUtil.hasText(syncSon)) {
					masters = CollectionUtil.toString(syncSon);
				}
			}
			if (masters != null) {
				String res = baseDao.callProcedure("SYS_POST",
						new Object[] { "CustomerQuota!Post", SpObserver.getSp(), masters, String.valueOf(id),
								SystemSession.getUser().getEm_name(), SystemSession.getUser().getEm_id() });
				if (res != null) {
					BaseUtil.showError(res);
				}
				baseDao.execute("update CustomerQuota set cq_sync='已同步' where cq_id=" + id);
			}
		}
	}

	/**
	 * 审查结果报：审核之后自动同步到子帐套
	 * 
	 * @author liujw 2017-12-25 09:04:54
	 * 
	 * @param id
	 */
	public void fs_report_audit_sync(Integer id) {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master != null && !StringUtils.isEmpty(master.getMa_soncode())) {// 父级账套
			String[] syncSon = baseDao.getDBSettingArray("Customer!Infor", "syncSon");
			if (StringUtil.hasText(syncSon)) {
				String masters = CollectionUtil.toString(syncSon);
				String res = baseDao.callProcedure("SYS_POST",
						new Object[] { "FsResultsReport!Post", SpObserver.getSp(), masters, String.valueOf(id),
								SystemSession.getUser().getEm_name(), SystemSession.getUser().getEm_id() });
				if (res != null) {
					BaseUtil.showError(res);
				}
				baseDao.execute("update Fs_Report set re_sync='已同步' where re_id=" + id);
			}
		}
	}

	/**
	 * 买方客户信息：审核之后自动同步到子帐套
	 * 
	 * @author madan 2017-11-27 14:05:10
	 * 
	 * @param id
	 */
	public void mfcustinfo_audit_sync(Integer id) {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master != null && !StringUtils.isEmpty(master.getMa_soncode())) {// 父级账套
			String[] syncSon = baseDao.getDBSettingArray("Customer!Infor", "syncSon");
			if (StringUtil.hasText(syncSon)) {
				String masters = CollectionUtil.toString(syncSon);
				String res = baseDao.callProcedure("SYS_POST",
						new Object[] { "FSMFCustInfo!Post", SpObserver.getSp(), masters, String.valueOf(id),
								SystemSession.getUser().getEm_name(), SystemSession.getUser().getEm_id() });
				if (res != null) {
					BaseUtil.showError(res);
				}
			}
		}
	}

	/**
	 * 客户个人信息：审核之后自动同步到子帐套
	 * 
	 * @author madan 2018-02-01 09:43:16
	 * 
	 * @param id
	 */
	public void custPersonInfo_audit_sync(Integer id) {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master != null && !StringUtils.isEmpty(master.getMa_soncode())) {// 父级账套
			String[] syncSon = baseDao.getDBSettingArray("Customer!Infor", "syncSon");
			if (StringUtil.hasText(syncSon)) {
				String masters = CollectionUtil.toString(syncSon);
				String res = baseDao.callProcedure("SYS_POST",
						new Object[] { "CustPersonInfo!Post", SpObserver.getSp(), masters, String.valueOf(id),
								SystemSession.getUser().getEm_name(), SystemSession.getUser().getEm_id() });
				if (res != null) {
					BaseUtil.showError(res);
				}
			}
		}
	}

	/**
	 * 企业信用评级：审核之后自动同步到子帐套
	 * 
	 * @author madan 2018-02-01 09:43:16
	 * 
	 * @param id
	 */
	public void custCreditRatingApply_audit_sync(Integer id) {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master != null && !StringUtils.isEmpty(master.getMa_soncode())) {// 父级账套
			String[] syncSon = baseDao.getDBSettingArray("Customer!Infor", "syncSon");
			if (StringUtil.hasText(syncSon)) {
				String masters = CollectionUtil.toString(syncSon);
				String res = baseDao.callProcedure("SYS_POST", new Object[] { "CustCreditRatingApply!Post", SpObserver.getSp(), masters,
						String.valueOf(id), SystemSession.getUser().getEm_name(), SystemSession.getUser().getEm_id() });
				if (res != null) {
					BaseUtil.showError(res);
				}
			}
		}
	}
}
