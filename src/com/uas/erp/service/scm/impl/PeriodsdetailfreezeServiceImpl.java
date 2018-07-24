package com.uas.erp.service.scm.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.service.fa.CheckAccountService;
import com.uas.erp.service.scm.PeriodsdetailfreezeService;

@Service
public class PeriodsdetailfreezeServiceImpl implements PeriodsdetailfreezeService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private CheckAccountService checkAccountService;
	@Autowired
	private VoucherDao voucherDao;

	@CacheEvict(value = "configs", allEntries = true)
	public void Periodsdetailfreeze(String pd_detno, String caller) {
		if (Integer.parseInt(pd_detno) < Integer.parseInt(voucherDao.getJustPeriods("MONTH-P").get("PD_DETNO").toString())) {
			BaseUtil.showError("当前期间小于库存结账期间，不能冻结！");
		}
		int count = baseDao.getCountByCondition("CONFIGS", "code='freezeMonth' and data='" + pd_detno + "'");
		if (count > 0) {
			BaseUtil.showError("当前期间已经冻结，不能重复冻结！");
		}
		count = baseDao.getCountByCondition("CONFIGS", "code='freezeMonth'");
		if (count > 0) {
			baseDao.updateByCondition("CONFIGS", "data='" + pd_detno + "'", "code='freezeMonth'");
		} else {
			String sql = "insert into CONFIGS(data,code,title,data_type,caller,multi,id,help) values(?,'freezeMonth','库存冻结月份',"
					+ "'NUMBER', 'sys', 0, 10000, '单据日期小于等于当前冻结月份，则不允许保存、更新、提交、过账、反过账')";
			baseDao.execute(sql, new Object[] { pd_detno });
			baseDao.execute("update CONFIGS set id=(select max(id)+1 from CONFIGS) where code='freezeMonth'");
		}
		baseDao.logger.others("库存冻结[" + pd_detno + "]", "库存冻结成功", caller, "id", pd_detno);
	}

	@Override
	@CacheEvict(value = "configs", allEntries = true)
	public void Periodsdetailcancelfreeze(String caller) {
		String pd_detno = getFreezeDetno();
		if (pd_detno==null) {
			BaseUtil.showError("当前没有冻结期间！");
		}
		baseDao.updateByCondition("CONFIGS", "data=''", "code='freezeMonth'");
		baseDao.logger.others("库存反冻结[" + pd_detno + "]", "库存反冻结成功", caller, "id", pd_detno);
	}

	@Override
	public String getFreezeDetno() {
		Object pd_detno= baseDao.getFieldDataByCondition("configs ", "data","code='freezeMonth' and caller='sys'");
		if (pd_detno==null) return null;
		return pd_detno.toString();
	}
}
