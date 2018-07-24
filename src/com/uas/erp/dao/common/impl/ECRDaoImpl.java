package com.uas.erp.dao.common.impl;

import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ECRDao;
import com.uas.erp.model.Employee;

@Repository
public class ECRDaoImpl extends BaseDao implements ECRDao{
	static final String TURNECN = "SELECT ecr_code,ecr_upman,ecr_updepart,ecr_cuname,ecr_aimdate,ecr_reason" + 
			",ecr_kind,ecr_changekind,FIN_Code,ecr_effectrange,ecr_priority,ecr_custaudit,ecr_oldproddeal FROM ecr WHERE ecr_id=?";
	static final String INSERTECN = "INSERT INTO ecn(ecn_id,ecn_code,ecn_ecrcode,ecn_checkstatus,ecn_indate,ecn_upman,ecn_updepartment,ecn_custname,ecn_diddate,ecn_changereason,ecn_recordman" +
			"ecn_type,ecn_changekind,FIN_Code,ecn_effectrange,ecn_level,ecn_custaudit,ecn_oldproddeal) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String TURNECNDETAIL = "SELECT ecrd_bomid,ecrd_type,ecrd_didstatus,ecrd_diddate,ecrd_mothercode FROM ecrdetail" + 
			" WHERE ecrd_id=?";
	static final String INSERTECNDETAIL = "INSERT INTO ecndetail(ed_id,ed_ecnid,ed_boid,ed_didtype,ed_didstatus,ed_diddate,ed_mothercode VALUES (?,?,?,?,?,?,?)";
	

	@Override
	@Transactional
	public int turnECN(int id, String language, Employee employee) {
		try {
			SqlRowList rs = queryForRowSet(TURNECN, new Object[]{id});
			int ecnid = 0;
			if(rs.next()){
				ecnid = getSeqId("ECN_SEQ");
				String code = sGetMaxNumber("ECN", 2);
				oracle.sql.TIMESTAMP time = (oracle.sql.TIMESTAMP)rs.getObject(5);
				Object date = null;
				if(time != null){
					try {
						date = time.dateValue();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				String sourcecode = rs.getString(3);
				boolean bool = execute(INSERTECN, new Object[]{ecnid,code,BaseUtil.getLocalMessage("ENTERING", language),"ENTERING",id,sourcecode,date,rs.getObject(3),
						rs.getObject(4),rs.getObject(5),rs.getObject(6),rs.getObject(7),rs.getObject(8),rs.getObject(9),rs.getObject(10),rs.getObject(11),rs.getObject(12),
						rs.getObject(13),rs.getObject(14),rs.getObject(15),rs.getObject(16),rs.getObject(17),rs.getObject(18),
						employee.getEm_id(),employee.getEm_name(), Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS))});
				if(bool){
					rs = queryForRowSet(TURNECNDETAIL, new Object[]{id});
					int count = 1;
					while(rs.next()){
						int edid = getSeqId("ECNEDETAIL_SEQ");
						execute(INSERTECNDETAIL, new Object[]{edid,ecnid,code,count++,rs.getObject(1),rs.getObject(2),
								rs.getObject(3),rs.getObject(4),rs.getObject(5),rs.getObject(6),rs.getObject(7),id,sourcecode});
					}
				}
			}
			return ecnid;
		} catch (Exception e){
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
			return 0;
		}
	}
	
	
}
