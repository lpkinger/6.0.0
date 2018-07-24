package com.uas.erp.service.ma.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.ma.PeriodsService;

@Service("periodsService")
public class PeriodsServiceImpl implements PeriodsService {
	@Autowired
	private BaseDao baseDao;

	@Override
	public void addPeriods(Integer param) {
		String year = param.toString().substring(0, 4);
		String month = param.toString().substring(4);
		int count = baseDao.getCountByCondition("PeriodsDetail", "pd_detno>="+ param);
		if(count <= 0){
			baseDao.execute("declare v_beginyear varchar2(10); v_beginmonth varchar2(10); v_yearmonth varchar2(30); v_i number; begin v_i := 0; v_beginyear := '"
					+ year
					+ "'; v_beginmonth := '"
					+ month
					+ "'; while(v_i < 10) loop v_yearmonth := v_beginyear || v_beginmonth; insert into PeriodsDetail(pd_code,pd_detno,pd_startdate,pd_enddate,pd_status,pd_year) select pe_code,to_char(to_date(v_yearmonth,'yyyymm'),'yyyymm'),to_date(v_yearmonth,'yyyymm'),last_day(to_date(v_yearmonth,'yyyymm')),0,v_beginyear from periods; if v_beginmonth = '12' then v_beginmonth := '01'; v_beginyear := to_number(v_beginyear) + 1; v_i := v_i + 1; else  v_beginmonth := to_number(v_beginmonth) + 1; end if; end loop; COMMIT; end;");
		} else {
			BaseUtil.showError("当前账期"+param+"已存在");
		}
	}

	@Override
	public boolean per_chk(String type, Integer month, String start, String end) {
		// TODO
		return true;
	}
	
	@Override
	public int getCurrentYearmonth() {
		SqlRowList rs = baseDao.queryForRowSet("select max(pd_detno) from periodsdetail where pd_code='MONTH-P'");
		if (rs.next()) {
			return rs.getInt(1);
		}
		return 0;
	}
}
