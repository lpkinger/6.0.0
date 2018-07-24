package com.uas.mobile.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.mobile.model.CardLog;
import com.uas.mobile.model.Enterprise;
import com.uas.mobile.model.WorkDate;
import com.uas.mobile.service.UserService;

@Service("mobileUserService")
public class UserServiceImpl extends BaseDao implements UserService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private EnterpriseService enterpriseService;

	/**
	 * 获取所有账套的登陆过移动客户端的用户
	 */
	@Override
	public List<Map<String, Object>> getLoginedEmployees() {
		List<Master> masters = enterpriseService.getMasters();
		String usersob = SystemSession.getUser().getEm_master();
		String sql = "";
		sql = "select em_code,em_name,ma_user,ma_function,em_mologintime from "
				+ "(select em_code,em_name,ma_user,ma_function,em_mologintime ,row_number()"
				+ " over(partition by em_code order by em_mologintime desc) rn"
				+ "  from  (";
		Master master = null;
		for (int i = 0; i < masters.size(); i++) {
			master = masters.get(i);
			if(BaseUtil.getXmlSetting("defaultSob").equals("uas_master")){
				if(!master.getMa_user().equals(usersob)) continue;
			}
			sql += " select em_code, em_name, em_mologintime,'"
					+ master.getMa_user()
					+ "' "
					+ "as ma_user, '"
					+ master.getMa_function()
					+ "' as  ma_function "
					+ "from "
					+ master.getMa_user()
					+ ".employee where em_mologintime"
					+ ">to_date('2000-01-02 00:00:00', 'yyyy-mm-dd HH24:mi:ss') ";
			if (i < masters.size() - 1 && !BaseUtil.getXmlSetting("defaultSob").equals("uas_master"))
				sql += " union ";
		}
		sql += ") ) where rn=1 order by em_mologintime desc ";
		List<Map<String, Object>> resultList = baseDao.getJdbcTemplate()
				.queryForList(sql);
		return resultList;
	}

	@Override
	public WorkDate getWorkDates(String en_code) {
		WorkDate workDate = null;
		String sql = "select wd_id,wd_code,wd_ondutyone,wd_offdutyone,wd_latitude,wd_longitude"
				+ ",wd_distanceforallow from  "
				+ "workdate w,employee e where w.WD_ID=e.EM_WDDEFAULTID and e.em_code=?";
		try {
			workDate = baseDao.getJdbcTemplate().queryForObject(sql,
					new BeanPropertyRowMapper<WorkDate>(WorkDate.class),
					en_code);
		} catch (Exception e) {
		}
		if (workDate == null) {
			return null;
		}
		return workDate;
	}

	@Override
	public void saveCardLogs(CardLog cardLog) {
		// TODO Auto-generated method stub
		cardLog.setCl_id(baseDao.getSeqId("cardLog_sq"));
		String sql = "insert into cardlog (cl_id,cl_cardcode,cl_emid,"
				+ "cl_time,cl_status,cl_emcode,cl_latitude,cl_longitude,"
				+ "cl_isproxy,cl_imageforproxy ) values (?,?,?,?,?,?,?,?,?,?)";
		baseDao.getJdbcTemplate()
				.update(sql,
						new Object[] { cardLog.getCl_id(),
								cardLog.getCl_cardcode(), cardLog.getCl_emid(),
								cardLog.getCl_time(), cardLog.getCl_status(),
								cardLog.getCl_emcode(),
								cardLog.getCl_latitude(),
								cardLog.getCl_longitude(),
								cardLog.getCl_isproxy(),
								cardLog.getCl_imageforproxy() },
						new int[] { java.sql.Types.INTEGER,
								java.sql.Types.VARCHAR, java.sql.Types.INTEGER,
								java.sql.Types.DATE, java.sql.Types.VARCHAR,
								java.sql.Types.VARCHAR, java.sql.Types.VARCHAR,
								java.sql.Types.VARCHAR, java.sql.Types.INTEGER,
								java.sql.Types.VARCHAR, });

	}

	@Override
	public List<CardLog> selectCardLogsForEnCode(String en_code, int startid,
			int endid) {
		// TODO Auto-generated method stub
		List<CardLog> cardLogs = new ArrayList<CardLog>();
		String sql = "select * from (" + "select A.*,rownum rn "
				+ "from(select * from cardlog where cl_emcode='" + en_code
				+ "') A " + "where rownum<=" + endid + ") " + "where rn>="
				+ startid;
		System.out.println(sql);
		try {
			cardLogs = baseDao.getJdbcTemplate().query(sql,
					new BeanPropertyRowMapper<CardLog>(CardLog.class));
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}

		return cardLogs;
	}

	@Override
	public Enterprise getEnterprise(Integer en_id) {
		// TODO Auto-generated method stub
		Enterprise enterprise = null;
		String sql = "select * from enterprise where en_id=" + en_id;
		enterprise = baseDao.getJdbcTemplate().queryForObject(sql,
				new BeanPropertyRowMapper<Enterprise>(Enterprise.class));
		if (enterprise != null) {
			return enterprise;
		}
		return null;
	}

}
