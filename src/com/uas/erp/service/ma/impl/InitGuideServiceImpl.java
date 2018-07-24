package com.uas.erp.service.ma.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.ma.InitGuideService;

/**
 * 初始化向导
 * 
 * @author yingp
 * 
 */
@Service
public class InitGuideServiceImpl implements InitGuideService {

	@Autowired
	private BaseDao baseDao;

	static final String[] Base_Conf_Tables = { "BaseDataSet", "Datalist",
			"DatalistDetail", "DbfindsetUI", "Dbfindset", "DbfindsetDetail", "DbfindsetGrid", "DatalistCombo",
			"DetailGrid", "DocumentHandler", "DocumentSetup", "Form", "FormDetail", "GridButton", "Initialize",
			"InitDetail", "LogicDesc", "LogicSetup", "PostStyle", "PostStyleDetail", "PostStyleStep", "RelativeSearch",
			"RelativeSearchForm", "RelativeSearchGrid", "Status", "Sysnavigation", "SysSpecialPower",
			"ST_PositionPower", "UUListener" };
	static final String[] Base_Data_Tables = { "AssKind", "Currencys", "CurrencysMonth", "MaxNumbers", "Periods",
			"PeriodsDetail", "Setting" };

	@Override
	public String checkBaseTables() {
		StringBuffer sb = new StringBuffer();
		for (String table : Base_Conf_Tables) {
			boolean hasRows = baseDao.checkIf(table, "1=1");
			if (!hasRows) {
				if (sb.length() > 0)
					sb.append(",");
				sb.append(table);
			}
		}
		for (String table : Base_Data_Tables) {
			boolean hasRows = baseDao.checkIf(table, "1=1");
			if (!hasRows) {
				if (sb.length() > 0)
					sb.append(",");
				sb.append(table);
			}
		}
		return sb.toString();
	}

	@Override
	public String checkBefore(String tables) {
		String[] tbs = tables.split(",");
		StringBuffer sb = new StringBuffer();
		for (String table : tbs) {
			boolean hasRows = baseDao.checkIf(table, "1=1");
			if (!hasRows) {
				if (sb.length() > 0)
					sb.append(",");
				sb.append(table);
			}
		}
		return sb.toString();
	}

	@Override
	public String checkTab(String table) {
		boolean hasRows = baseDao.checkIf(table, "1=1");
		if(hasRows)
			return "success";
		return null;
	}

	@Override
	public void repairTabs(String tbs) {
		String[] tables = tbs.split(",");
		// 在没有数据库链接的情况下，只有切帐套取数据，再插入
		String sob = SpObserver.getSp();
		SpObserver.putSp("uaserp");
		List<String> sqls = new ArrayList<String>();
		for(String t : tables) {
			SqlRowList rs = baseDao.queryForRowSet("select * from " + t);
			while(rs.next()) {
				sqls.add(SqlUtil.getInsertSqlByMap(rs.getCurrentMap(), t));
			}
		}
		SpObserver.putSp(sob);
		baseDao.execute(sqls);
	}

	@Override
	public int getCount(String table, String condition) {
		condition = condition == null || condition.length() == 0 ? "1=1" : condition;
		return baseDao.getCountByCondition(table, condition);
	}

}
