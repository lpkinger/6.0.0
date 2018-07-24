package com.uas.erp.service.scm.impl;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.AcceptNotifyDao;
import com.uas.erp.service.scm.AvgCostCountService;

@Service("avgCostCountService")
public class AvgCostCountServiceImpl implements AvgCostCountService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private AcceptNotifyDao acceptNotifyDao;

	@Override
	public void countAvgCost(Integer param) {
		String res = null;
		res = baseDao.callProcedure("SP_CountAvgCost", new Object[] { param, SystemSession.getUser().getEm_name() });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
	}

	@Override
	@Transactional
	public JSONObject turnCostChange(Integer param) {
		int count = baseDao.getCount("select count(1) from BATCHMONTH where BAM_YEARMONTH=" + param);
		if (count == 0) {
			BaseUtil.showError("请先进行加权平均成本计算！");
		}
		count = baseDao.getCount("select count(1) from BATCHMONTH left join warehouse on bam_whcode=wh_code where BAM_YEARMONTH=" + param
				+ " and round(nvl(BAM_AVGPRICE,0),10)<>round(nvl(BAM_PRICE,0),10) and nvl(wh_nocost,0)=0");
		if (count > 0) {
			String pi_inoutno = baseDao.sGetMaxNumber("ProdInOut!CostChange", 2);
			int pi_id = baseDao.getSeqId("PRODINOUT_SEQ");
			baseDao.execute("insert into PRODINOUT (pi_id,pi_inoutno,pi_date,pi_departmentcode,pi_departmentname,pi_emcode,pi_emname,pi_status,pi_invostatus,"
					+ "pi_recordman,pi_recorddate,pi_class,pi_sourcetype,pi_invostatuscode,pi_statuscode) values ("
					+ pi_id
					+ ", '"
					+ pi_inoutno
					+ "',sysdate,'"
					+ SystemSession.getUser().getEm_departmentcode()
					+ "','"
					+ SystemSession.getUser().getEm_depart()
					+ "','"
					+ SystemSession.getUser().getEm_code()
					+ "','"
					+ SystemSession.getUser().getEm_name()
					+ "','"
					+ BaseUtil.getLocalMessage("UNPOST")
					+ "','"
					+ BaseUtil.getLocalMessage("AUDITED")
					+ "','"
					+ SystemSession.getUser().getEm_name()
					+ "', sysdate, '成本调整单','加权平均调整','AUDITED','UNPOST')");
			baseDao.execute("update prodinout set pi_date=(select pd_enddate from PeriodsDetail where pd_code='MONTH-P' and pd_detno="
					+ param + ") where pi_id=" + pi_id);
			baseDao.execute("insert into ProdIODetail (pd_id,pd_piid,pd_inoutno,pd_piclass,pd_pdno,pd_prodcode,pd_batchcode,pd_batchid,pd_orderqty,"
					+ "pd_orderprice,pd_price,pd_whcode,pd_whname,pd_prodid,pd_accountstatus,pd_status,pd_accountstatuscode) "
					+ "select ProdIODetail_seq.nextval,"
					+ pi_id
					+ ", '"
					+ pi_inoutno
					+ "','成本调整单',rownum,BAM_PRODCODE,BAM_BATCHCODE,BAM_BATCHID,case when to_char(bam_time,'yyyymm')<"
					+ param
					+ " then nvl(BAM_BEGINQTY,0) else nvl(BAM_NOWINQTY,0) end,nvl(BAM_PRICE,0),"
					+ "nvl(BAM_AVGPRICE,0),bam_whcode,wh_description,BAM_PRODID,'未核算',0,'UNACCOUNT' "
					+ "from BATCHMONTH left join warehouse on bam_whcode=wh_code where BAM_YEARMONTH="
					+ param
					+ " and round(nvl(BAM_AVGPRICE,0),10)<>round(nvl(BAM_PRICE,0),10) and nvl(wh_nocost,0)=0");
			baseDao.execute(
					"update prodiodetail set pd_total=round((nvl(pd_price,0)-nvl(pd_orderprice,0))*nvl(pd_orderqty,0),2) where pd_piclass ='成本调整单' and pd_piid=?",
					pi_id);
			baseDao.execute("update prodinout set (pi_whcode,pi_whname)=(select pd_whcode,pd_whname from "
					+ "(select pd_whcode,pd_whname from prodiodetail where pd_piid=? order by pd_pdno) where rownum=1 ) where pi_id=?",
					pi_id, pi_id);
			// 存储过程
			String res1 = baseDao.callProcedure("SP_PRODUCTCOSTADJUST", new Object[] { "成本调整单", pi_inoutno, "" });
			if (res1 != null && !res1.trim().equals("")) {
				BaseUtil.showError(res1);
			}
			baseDao.updateByCondition("ProdInOut", "pi_statuscode='POSTED',pi_status='" + BaseUtil.getLocalMessage("POSTED")
					+ "',pi_inoutman='" + SystemSession.getUser().getEm_name() + "',pi_date1=sysdate,pi_sendstatus='待上传'", "pi_id=" + pi_id);
			baseDao.updateByCondition("ProdIODetail", "pd_status=99", "pd_piid=" + pi_id);
			JSONObject j = new JSONObject();
			j.put("pi_id", pi_id);
			j.put("pi_inoutno", pi_inoutno);
			return j;
		}
		return null;
	}
}
