package com.uas.erp.service.drp.impl;

import java.sql.Timestamp;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;

import com.uas.erp.model.Employee;
import com.uas.erp.service.drp.RepairAccountService;

/**
 * Created by IntelliJ IDEA.
 * User: USOFTPC30
 * Date: 13-5-22
 * Time: 下午12:00
 * To change this template use File | Settings | File Templates.
 */

@Service
public class RepairAccountServiceImpl implements RepairAccountService {

    @Autowired
    private BaseDao baseDao;

    @Override
    public void makeBill(String ra_id,  String caller) {
        String status = (String) baseDao.getFieldDataByCondition("REPAIRACCOUNT", "RA_STATUS", "RA_ID=" + ra_id);
        if ("已开票".equals(status)) {
            BaseUtil.showError("该单据已开票！");
            return;
        }
        Employee employee=SystemSession.getUser();
        SqlRowList repairAccount = baseDao.queryForRowSet("SELECT * FROM REPAIRACCOUNT WHERE RA_ID=" + ra_id);
        SqlRowList repairAccountDetail = baseDao.queryForRowSet("SELECT * FROM REPAIRACCOUNTDETAIL WHERE RAD_RAID=" + ra_id);

        int abid = baseDao.getSeqId("ARBILL_SEQ");
		String code = baseDao.sGetMaxNumber("ARBill", 2);

         Map<String, Object> ra = repairAccount.getResultList().get(0);
         baseDao.execute("INSERT INTO arbill(ab_id,ab_code,ab_auditstatus,ab_auditstatuscode" +
                    ",ab_recorderid,ab_indate,ab_class,ab_aramount,ab_printstatuscode,ab_printstatus,ab_paystatuscode,ab_paystatus,ab_statuscode,ab_status,ab_recorder,ab_date"+
                    ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{abid,code,BaseUtil.getLocalMessage("ENTERING"),"ENTERING",
				employee.getEm_id(), Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), "应收发票", ra.get("RA_SUMREPAIRFEE")
				, "UNPRINT",BaseUtil.getLocalMessage("UNPRINT"),"UNCOLLECT",BaseUtil.getLocalMessage("UNCOLLECT"),"UNPOST",BaseUtil.getLocalMessage("UNPOST"),
				employee.getEm_name(),Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS))});

        if (repairAccountDetail != null && repairAccountDetail.getResultList() != null) {
            int i = 1;
            for (Map<String, Object> detail: repairAccountDetail.getResultList()) {
                int abdid = baseDao.getSeqId("APBILLDETAIL_SEQ");
                baseDao.execute("INSERT INTO ARBILLDETAIL(abd_id,abd_abid,abd_detno,abd_pdinoutno,abd_prodcode,abd_qty,abd_price,abd_description,abd_statuscode,abd_status," +
                        "abd_code,abd_date,abd_sourcetype,abd_sourcekind,abd_sourcedetailid) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{abdid, abid, i++, ra.get("RA_RWCODE"), detail.get("RA_PRCODE"), detail.get("RAD_QTY"), detail.get("RAD_PRICE"),
                        detail.get("RAD_REMARK"), "ENTERING", 0, code, Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), "售后结算单", "RepairAccount", detail.get("RAD_ID")});
            }
        }

        baseDao.updateByCondition("REPAIRACCOUNT", "RA_STATUS='已开票'", "ra_id=" + ra_id);
    }
}
