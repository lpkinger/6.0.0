package com.uas.erp.dao.common.impl;

import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.MODYSReportDao;

@Repository
public class MODYSReportDaoImpl extends BaseDao implements MODYSReportDao {
	static final String YSReport = "SELECT * FROM MOD_YSREPORT WHERE mo_id=?";
	static final String YSReportDetail = "SELECT * FROM MOD_YSBGDETAIL WHERE yd_moid=?";
	static final String MJProject = "INSERT INTO MOD_MJPROTECT(ws_id, ws_code, ws_pdate, ws_status, ws_statuscode,ws_sourcecode,ws_stf,ws_recorder,ws_indate)"
			+ " values (?,?,sysdate,?,'ENTERING',?,?,?,sysdate)";
	static final String MJProjectDetail = "INSERT INTO MOD_MJPROTECTDetail (wd_id, wd_wsid, wd_code,wd_detno,wd_mjcode, wd_kzhtcode,"
			+ "wd_prodcode, wd_mjvendcode,wd_mjvendname,wd_kzdate,wd_ysdate,wd_mjysid,wd_kzhtdetno,wd_pstyle) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	@Override
	public int turnMJProject(int id) {
		SqlRowList rs = queryForRowSet(YSReport, id);
		int ws_id = 0;
		if (rs.next()) {
			ws_id = getSeqId("MOD_MJPROTECT_SEQ");
			String code = sGetMaxNumber("MJProject!Mould", 2);
			Object vendcode = "";
			Object vendname = rs.getObject("mo_gys");
			if (vendname != null && !"".equals(vendname.toString())) {
				vendcode = getFieldDataByCondition("Vendor", "ve_code", "ve_name='" + vendname + "' and ve_auditstatuscode='AUDITED'");
			}
			boolean bool = execute(MJProject, new Object[] { ws_id, code, BaseUtil.getLocalMessage("ENTERING"), rs.getObject("mo_code"),
					vendname , SystemSession.getUser().getEm_name() });
			if (bool) {
				rs = queryForRowSet(YSReportDetail, id);
				int count = 1;
				while (rs.next()) {
					Object pstype = getFieldDataByCondition("PRODUCTSET", "ps_type", "ps_code= '" + rs.getObject("yd_mjcode")+"'");
					execute(MJProjectDetail,
							new Object[] { getSeqId("MOD_MJPROTECTDETAIL_SEQ"), ws_id, code, count++, rs.getObject("yd_mjcode"),
									rs.getObject("yd_mjhtcod"), rs.getObject("yd_prodcode"), vendcode, vendname, rs.getObject("yd_kzdate"),
									rs.getObject("yd_ysdate"), rs.getObject("yd_id"), rs.getObject("yd_mjhtdetno"), pstype });
				}
			}
		}
		execute("update MOD_MJPROTECTDetail set wd_price=(select pmd_price from PURMOULDDETAIL where wd_kzhtcode=pmd_code and wd_kzhtdetno=pmd_detno) where wd_wsid="
				+ ws_id);
		return ws_id;
	}
}
