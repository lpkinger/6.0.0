package com.uas.erp.dao.common.impl;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ProjectTaskChangeDao;

public class ProjectTaskChangeDaoImpl   extends BaseDao implements ProjectTaskChangeDao{
	static final String TURNPURC = "SELECT ptc_startdate,ptc_enddate,ptc_name,ptc_needattach,ptc_milestone,ptc_oldmilestone,ptc_oldenddate,ptc_oldtaskid from ProjectTaskChange"+
                           " where ptc_id=?";
	static final String UPDATEPURC = "update purchase set pu_rate=?,pu_currency=?,pu_vendname=?,pu_vendcode=?,pu_vendid=?,pu_paymentsid=?,pu_payments=?" +
			",pu_delivery=?,pu_recordman=?,pu_indate=? where pu_id=?";
	static final String TURNPURCDETAIL = "SELECT pcd_newtaxrate,pcd_newdelivery,pcd_newprice,pcd_newbeipin,pcd_newqty,pcd_newprodid,pcd_newprodcode,pcd_pddetno" + 
			",pcd_newqty,pcd_newprodname FROM purchasechangedetail WHERE pcd_pcid=?";
	static final String UPDATEPURCDETAIL = "UPDATE purchasedetail SET pd_rate=?,pd_delivery=?,pd_price=?,pd_beipin=?,pd_qualityqty=?,pd_prodcode=?" +
			",pd_qty=? WHERE pd_puid=? AND pd_detno=?";
	@Override
	public int turnProjectTask(int id) {
		SqlRowList rs = queryForRowSet(TURNPURC, new Object[]{id});
		if(rs.next()){
			if(rs.getString("ptc_enddate") != null && (!rs.getString("ptc_enddate").equals(rs.getString("ptc_oldenddate"))))
				BaseUtil.showError("该变更涉及到历程碑的变更需先变更里程碑!");
		}
		return 0;
	}
}
