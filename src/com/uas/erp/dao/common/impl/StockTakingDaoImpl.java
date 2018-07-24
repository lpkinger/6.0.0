package com.uas.erp.dao.common.impl;

import java.sql.Timestamp;

import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.StockTakingDao;
import com.uas.erp.model.Employee;

@Repository
public class StockTakingDaoImpl extends BaseDao implements StockTakingDao{
	static final String INSERTSTOCKTAKING = "insert into ProdInOut(pi_id,pi_inoutno,pi_class,pi_date,pi_recorddate,pi_whcode,pi_whname"+
			",pi_status,pi_statuscode,pi_invostatus,pi_invostatuscode,pi_remark,pi_recordman,pi_sourcecode)"+
			" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String INSERTSTOCKTAKINGDETAIL = "insert into ProdIODetail(pd_id, pd_piid, pd_orderid, pd_ordercode, pd_orderdetno, pd_inoutno,pd_piclass, pd_pdno,"+
			"pd_prodcode,pd_batchcode,pd_outqty,pd_price,pd_remark,pd_prodid, pd_batchid,pd_whcode,pd_whname,pd_whid,pd_total,pd_accountstatuscode,pd_accountstatus, pd_stdid)" +
			" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String INSERTSTOCKTAKINGDETAILIN = "insert into ProdIODetail(pd_id, pd_piid, pd_orderid, pd_ordercode, pd_orderdetno, pd_inoutno,pd_piclass, pd_pdno,"+
			"pd_prodcode,pd_batchcode,pd_inqty,pd_price,pd_remark,pd_prodid, pd_batchid,pd_whcode,pd_whname,pd_whid,pd_total,pd_accountstatuscode,pd_accountstatus, pd_stdid)" +
			" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	@Override
	public Object[] turnProdIO(String piclass, String whcode, String stcode, String caller) {
		Timestamp time = Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS));
		Employee employee = SystemSession.getUser();
		String code = sGetMaxNumber(caller, 2);
		int id = getSeqId("PRODINOUT_SEQ");
		Object whname = getFieldDataByCondition("Warehouse", "wh_description", "wh_code='" + whcode + "'");
		execute(INSERTSTOCKTAKING, new Object[] {id, code, piclass, time, time, whcode, whname,
				BaseUtil.getLocalMessage("UNPOST"), "UNPOST", 
				BaseUtil.getLocalMessage("ENTERING"), "ENTERING", "盘点数据,盘点单号:" + stcode,
				employee.getEm_name(), stcode});
		return new Object[] { code, id };
	}
	@Override
	public void turnProdIODetail(int stdid, int detno, String code, Object id, String piclass) {
		Object[] objs = getFieldsDataByCondition("StockTakingDetail left join StockTaking on std_stid=st_id", new String[] { "std_code", "std_detno", "std_prodcode",
				"std_batchcode", "abs(std_batchqty-std_actqty)", "std_price", "std_remark", "st_whcode"}, "std_id=" + stdid);
		Object price = objs[5] == null ? 0 : objs[5];
		double qty = Double.parseDouble(objs[4].toString());
		Object prid = getFieldDataByCondition("Product", "pr_id", "pr_code='" + objs[2] + "'");
		Object batchid = getFieldDataByCondition("Batch", "ba_id", "ba_code='" + objs[3] + "'");
		Object[] wh = getFieldsDataByCondition("Warehouse", new String[]{"wh_id","wh_description"}, "wh_code='" + objs[7] + "'");
		if("盘盈调整单".equals(piclass)){
			execute(INSERTSTOCKTAKINGDETAILIN, new Object[] {getSeqId("PRODIODETAIL_SEQ"), id, stdid, objs[0], objs[1], 
					code, piclass, detno, objs[2], "", qty, price, objs[6], prid, 0, objs[7], wh[1], wh[0], 
					qty*Float.parseFloat(price.toString()),"UNACCOUNT",BaseUtil.getLocalMessage("UNACCOUNT"), stdid});
			execute("update prodiodetail set (pd_prodmadedate,pd_replydate)=(select trunc(std_prodmadedate),trunc(std_validtime) from StockTakingDetail where pd_orderid=std_id) where pd_piid="+id);
		} else {
			execute(INSERTSTOCKTAKINGDETAIL, new Object[] {getSeqId("PRODIODETAIL_SEQ"), id, stdid, objs[0], objs[1], 
					code, piclass, detno, objs[2], objs[3], qty, price, objs[6], prid, batchid, objs[7], wh[1], wh[0], 
					qty*Float.parseFloat(price.toString()),"UNACCOUNT",BaseUtil.getLocalMessage("UNACCOUNT"), stdid});
		}
	}
}
