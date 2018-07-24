package com.uas.erp.service.b2b.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.service.b2b.ProductSampleDownService;

@Service("productSampleDownService")
public class ProductSampleDownServiceImpl implements ProductSampleDownService {
	@Autowired
	private BaseDao baseDao;

	@Override
	public int turnCustSendSample(int id) {
		// TODO Auto-generated method stub
		int ssid = 0;
		// 判断该问题反馈是否已转过BUG
		Object code = baseDao.getFieldDataByCondition("CustSendSample", "ss_id", "ss_sourceid=" + id);
		if (code != null && !code.equals("")) {
			BaseUtil.showError("不能重复转送样单！");
		} else {
			ssid = turnSample(id);
			if (ssid != 0) {
				Object sscode = baseDao.getFieldDataByCondition("CustSendSample", "ss_code", "ss_id=" + ssid);
				baseDao.execute("update ProductSampleDown set ps_samplestatus = '已送样' ,ps_samplecode = " + sscode.toString()
						+ " where ps_id=" + id);
			}

		}
		return ssid;
	}

	// snid,code,employee.getEm_name(),Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)),rs.getObject("cps_prodcode"),rs.getObject("cps_detail"),rs.getObject("cps_spec"),rs.getObject("cps_unit"),rs.getObject("cps_qty"),rs.getObject("cps_remark"),"在录入","ENTERING",id,sourcecode,rs.getObject("cps_custcode"),rs.getObject("cps_custname"),rs.getObject("cps_custuu"),
	// employee.getEm_id(),rs.getObject("fb_enname"),rs.getObject("fb_enid"),"ENTERING","在录入"

	static final String TURNSENDSAMPLE = "select ps_id,ps_code,ps_custuu,ps_custcode,ps_custname,ps_custprodcode,ps_custproddetail,ps_custspec,"
			+ "ps_custunit,ps_qty,ps_isfree,ps_contact,ps_contactuu,ps_envrequire,ps_scope,ps_price,ps_currency,ps_rate,ps_qty,ps_total,ps_delivery from ProductSampleDown where ps_id=?";
	static final String INSERTSENDSAMPLE = "insert into CustSendSample(ss_code,ss_pscode,ss_custprodcode,ss_custproddetail,ss_custspec,"
			+ "ss_custunit,ss_sendnum,ss_isfree,ss_custcode,ss_custname,ss_custuu,ss_contact,ss_contactuu,ss_sampleprice,"
			+ "ss_rate,ss_recorder,ss_recorddate,ss_id,ss_sourceid)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?,?)";

	private int turnSample(int id) {
		try {
			Employee employee = SystemSession.getUser();
			SqlRowList rs = baseDao.queryForRowSet(TURNSENDSAMPLE, new Object[] { id });
			int ssid = 0;
			if (rs.next()) {
				ssid = baseDao.getSeqId("CustSENDSAMPLE_SEQ");
				String code = baseDao.sGetMaxNumber("CustSendSample", 2);
				Object cu_rate = baseDao.getFieldDataByCondition("Currencys", "cr_taxrate", "cr_name='RMB'");
				double rate = cu_rate == null ? 0.17 : Double.parseDouble(cu_rate.toString())/100;
				baseDao.execute(
						INSERTSENDSAMPLE,
						new Object[] { code, rs.getObject("ps_code"), rs.getObject("ps_custprodcode"), rs.getObject("ps_custproddetail"),
								rs.getObject("ps_custspec"), rs.getObject("ps_custunit"), rs.getObject("ps_qty"),
								rs.getObject("ps_isfree"), rs.getObject("ps_custcode"), rs.getObject("ps_custname"),
								rs.getObject("ps_custuu"), rs.getObject("ps_contact"), rs.getObject("ps_contactuu"),
								rs.getObject("ps_price"), rs.getObject("ps_rate"), employee.getEm_name(), ssid, id });
				baseDao.execute("update custsendsample set ss_status='在录入',ss_statuscode='ENTERING',ss_currency=(select ps_currency from productSampledown where ps_id=ss_sourceid) where ss_id="
						+ ssid);
				baseDao.execute("update custsendsample set ss_currency='RMB',ss_rate="+rate+"*100 where nvl(ss_currency,' ')=' ' and ss_id=" + ssid);
				baseDao.execute(
						"update custsendsample sd1 set (ss_prodid,ss_prodcode)=(select max(pc_prodid),max(pc_prodcode) from productcustomer,custsendsample sd2 where  pc_custcode=ss_custcode and pc_custprodcode=sd2.ss_custprodcode and sd1.ss_id=sd2.ss_id) where sd1.ss_id = ?",
						ssid);
				baseDao.execute("update PRODUCTSAMPLEDOWN set PS_SAMPLECODE =" + code + "where ps_id=" + ssid);
			}
			return ssid;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
			BaseUtil.showError("数据异常,转入失败");
			return 0;
		}
	}
}
