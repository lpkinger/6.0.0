package com.uas.mobile.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.DataList;
import com.uas.mobile.model.Stats;
import com.uas.mobile.service.StatsService;
@Service
public class StatsServiceImpl implements StatsService {
	@Autowired
	private BaseDao baseDao;
	@Override
	public Map<String,List<Stats>> getStats() {
		// TODO Auto-generated method stub
		Map<String,List<Stats>> set=new HashMap<String, List<Stats>>();
		List<Stats> stats=baseDao.getJdbcTemplate().query("select * from Stats order by st_id desc", new BeanPropertyRowMapper<Stats>(Stats.class));
		List<Stats> list = null;
		for (Stats st: stats) {
			String key = st.getSt_group();
			if (StringUtil.hasText(key) && set.containsKey(key)) {
				list = set.get(key);
			} else {
				list = new ArrayList<Stats>();
			}
			list.add(st);
			set.put(key, list);
		}
		return set;
	}
	@Override
	public Stats getStats(int id,String config) {
		// TODO Auto-generated method stub
		Stats stats=baseDao.getJdbcTemplate().queryForObject("select * from stats  where st_id="+id, new BeanPropertyRowMapper<Stats>(Stats.class));
		String sql=stats.getQuerySql(config);
		stats.setDatas(baseDao.getJdbcTemplate().queryForList(sql));
		return stats;
	}
	
	

}
