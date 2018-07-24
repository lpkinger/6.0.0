package com.uas.erp.dao.common.impl;

import java.sql.Timestamp;

import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.SampleDao;
import com.uas.erp.model.Employee;

@Repository
public class SampleDaoImpl extends BaseDao implements SampleDao {
	
	static final String TURNSENDSAMPLE = "select cps_id,cps_code,cps_custuu,cps_custcode,cps_custname,cps_prodcode,cps_detail,cps_spec,cps_unit,cps_qty,cps_isfree,cps_status,cps_remark from CuProductSample where cps_id=?";
	static final String INSERTSENDSAMPLE = "insert into SendSample(ss_id,ss_code,ss_recorder,ss_recorddate,ss_prodcode,ss_detail,ss_spec,ss_unit,ss_qty,ss_remark,ss_status,ss_statuscode,ss_isfree,ss_cpsid,ss_cpscode,ss_custcode,ss_custname,ss_custuu)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	

	@Override
	public int turnSendSample(int id) {
		try {	
			Employee employee = SystemSession.getUser();
			SqlRowList rs = queryForRowSet(TURNSENDSAMPLE, new Object[]{id});
			int snid = 0;
			if(rs.next()){
				snid = getSeqId("SENDSAMPLE_SEQ");
				String code = sGetMaxNumber("SendSample", 2);				
				String sourcecode = rs.getString(1);
				execute(INSERTSENDSAMPLE, new Object[]{snid,code,employee.getEm_name(),Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)),rs.getObject("cps_prodcode"),rs.getObject("cps_detail"),rs.getObject("cps_spec"),rs.getObject("cps_unit"),rs.getObject("cps_qty"),rs.getObject("cps_remark"),"在录入","ENTERING",id,sourcecode,rs.getObject("cps_custcode"),rs.getObject("cps_custname"),rs.getObject("cps_custuu"),
						employee.getEm_id(),rs.getObject("fb_enname"),rs.getObject("fb_enid"),"ENTERING","在录入"});		
			}
			return snid;
		} catch (Exception e){
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
			return 0;
		}
	}

}
