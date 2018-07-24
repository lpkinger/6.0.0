package com.uas.erp.dao.common.impl;

import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.MakeNoticeDao;

@Repository
public class MakeNoticeDaoImpl extends BaseDao implements MakeNoticeDao{
	static final String TURNMAKE = "SELECT mn_code,mn_mrpid,mn_prid,mn_prodcode,mn_kind,mn_qty,mn_planbegindate,mn_planenddate,mn_ordercode,mn_orderdetno" + 
			" FROM makenotice WHERE mn_id=?";
	static final String INSERTMAKE = "INSERT INTO make(ma_id,ma_code,ma_source,ma_mpsid,ma_prodid,ma_prodcode,ma_type" +
			",ma_qty,ma_planbegindate,ma_planenddate,ma_salecode,ma_saledetno,ma_statuscode,ma_status,ma_tasktype" +
			") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	@Override
	public int turnMake(int id, String caller) {
		SqlRowList rs = queryForRowSet(TURNMAKE, new Object[]{id});
		int veid=0;
		if(rs.next()){
			veid = getSeqId("MAKE_SEQ");
			String code = sGetMaxNumber("Make", 2);			
			execute(INSERTMAKE, new Object[]{veid,code,rs.getObject(1),rs.getObject(2),rs.getObject(3),
					rs.getObject(4),rs.getObject(5),rs.getObject(6),rs.getObject(7),rs.getObject(8),
					rs.getObject(9),rs.getObject(10),"ENTERING",BaseUtil.getLocalMessage("ENTERING"),
					"制造单"});			
		}
		return veid;
	}
	
}
