package com.uas.erp.dao.common.impl;

import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.WorkovertimeDao;
@Repository
public class WorkovertimeDaoImpl extends BaseDao implements WorkovertimeDao {

	@Override
	public void syncToSqlServer(int id, String caller) {
		String hk = "skypine_DB";
		SqlRowList rs = queryForRowSet(
				"select * from Workovertime  where wo_id=?",
				id);
		if (rs.next()) {
			SqlRowList rd = queryForRowSet(
					"select * from Workovertimedetail  where wod_woid=? order by wod_detno",
					id);
			if (rd.hasNext()) {
				String sob = SpObserver.getSp();
				SpObserver.putSp(hk);
				if(!hk.equals(SpObserver.getSp())) {
					BaseUtil.showError("无法连接到东宝系统,同步失败.");
				}			
				String[] sqls = new String[2 * rd.getResultList().size() + 2];
				int i = 0;				
				while (rd.next()) {					
					sqls[i++] = "insert into AddWorkTimeReq(YYMMDD, EmpNo, BeginTime, EndTime, IsWeekEnd, IsOverAddWork,Times, WhyAddWork, IsRestAddWork,CreateMan, CreateDate)values(CONVERT(datetime,'"
							+ rd.getGeneralTimestamp("wod_startdate")
							+ "',20),'"							
							+ rd.getGeneralString("wod_empcode")
							+ "','"
							+ rd.getGeneralString("wod_jias1")
							+ "',"
							+ rd.getGeneralDouble("wod_jiax1")
							+ ",0"							
							+ ",0,"
							+ rd.getGeneralDouble("wod_count")
							+ ",'"
							+ rd.getGeneralString("wo_remark")
							+ "',0,'"
							+ SystemSession.getUser().getEm_name()
							+ "',sysdate)";					
				}				
				getJdbcTemplate().batchUpdate(sqls);				
				SpObserver.putSp(sob);
				execute("update Workovertime set wo_sync='已同步' where wo_id=?", id);
			}
		}
	}

}
