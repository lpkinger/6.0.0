package com.uas.erp.service.common.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.CommonUse;
import com.uas.erp.model.Employee;
import com.uas.erp.model.ShortCut;
import com.uas.erp.model.WorkBench;
import com.uas.erp.service.common.WorkBenchService;

@Service
public class WorkBenchServiceImpl implements WorkBenchService {
	@Autowired
	private BaseDao baseDao;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	/*@Cacheable(value = "bench", key = "#employee.em_master + '@' + #employee.em_id + 'getWorkBench'")*/
	public List<WorkBench> getWorkBench(Employee employee) {
		try {
			List<WorkBench> benchs = baseDao.getJdbcTemplate().query(
					"select * from workbench where wb_emid=? and wb_isuse=1 order by wb_detno",
					new BeanPropertyRowMapper(WorkBench.class), employee.getEm_id());
			if (benchs == null || benchs.size() == 0) {
				boolean bool = baseDao.checkByCondition("workbench", "wb_emid=" + employee.getEm_id());
				if (bool) {
					setDefaultBench(employee);
				}
			}
			return benchs;
		} catch (EmptyResultDataAccessException e) {
			// 如果没有设置，就添加默认设置到数据库
			setDefaultBench(employee);
			List<WorkBench> benchs = baseDao.getJdbcTemplate().query(
					"select * from workbench where wb_emid=? and wb_isuse=1 order by wb_detno",
					new BeanPropertyRowMapper(WorkBench.class), employee.getEm_id());
			return benchs;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
/*	@CacheEvict(value = "bench", allEntries = true)*/
	public void setWorkBench(Employee employee, String data) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		List<String> sqls = new ArrayList<String>();
		for (Map<Object, Object> d : store) {
			sqls.add("update workbench set wb_detno=" + d.get("wb_detno") + ",wb_isuse="
					+ (Boolean.parseBoolean(d.get("wb_isuse").toString()) ? 1 : 0) + ",wb_width="
					+ Double.parseDouble(d.get("wb_width").toString()) / 100 + ",wb_height="
					+ Double.parseDouble(d.get("wb_height").toString()) / 100 + " where wb_emid=" + employee.getEm_id()
					+ " AND wb_name='" + d.get("wb_name") + "'");
		}
		baseDao.execute(sqls);
	}

	public void setDefaultBench(Employee employee) {
		WorkBench bench = new WorkBench("bench_task", employee.getEm_id(), 1, 1, (float) 2 / 3, (float) 2 / 3);
		baseDao.save(bench);
		bench = new WorkBench("bench_link", employee.getEm_id(), 1, 2, (float) 1 / 3, (float) 2 / 3);
		baseDao.save(bench);
		bench = new WorkBench("bench_notify", employee.getEm_id(), 1, 3, (float) 1 / 3, (float) 1 / 3);
		baseDao.save(bench);
		bench = new WorkBench("bench_news", employee.getEm_id(), 1, 4, (float) 1 / 3, (float) 1 / 3);
		baseDao.save(bench);
		bench = new WorkBench("bench_subscription", employee.getEm_id(), 1, 5, (float) 1 / 3, (float) 1 / 3);
		baseDao.save(bench);
		bench = new WorkBench("bench_schedule", employee.getEm_id(), 0, 6, (float) 1 / 3, (float) 1 / 3);
		baseDao.save(bench);
		bench = new WorkBench("bench_note", employee.getEm_id(), 0, 7, (float) 1 / 3, (float) 1 / 3);
		baseDao.save(bench);
		bench = new WorkBench("bench_mytask", employee.getEm_id(), 0, 8, (float) 1 / 3, (float) 1 / 3);
		baseDao.save(bench);
		bench = new WorkBench("bench_plan", employee.getEm_id(), 0, 9, (float) 1 / 3, (float) 1 / 3);
		baseDao.save(bench);
		bench = new WorkBench("bench_email", employee.getEm_id(), 0, 10, (float) 1 / 3, (float) 1 / 3);
		baseDao.save(bench);
		bench = new WorkBench("bench_knowledge", employee.getEm_id(), 0, 11, (float) 1 / 3, (float) 1 / 3);
		baseDao.save(bench);
		bench = new WorkBench("bench_meeting", employee.getEm_id(), 0, 12, (float) 1 / 3, (float) 1 / 3);
		baseDao.save(bench);
		bench = new WorkBench("bench_flow", employee.getEm_id(), 0, 12, (float) 1 / 3, (float) 1 / 3);
		baseDao.save(bench);
		bench = new WorkBench("bench_overflow", employee.getEm_id(), 0, 12, (float) 1 / 3, (float) 1 / 3);
		baseDao.save(bench);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@Cacheable(value = "short", key = "#employee.em_master + '@' + #employee.em_id + 'getShortCut'")
	public List<ShortCut> getShortCut(Employee employee) {
		try {
			List<ShortCut> shorts = baseDao.getJdbcTemplate().query(
					"select * from shortcut where sc_emid=? and sc_isuse=1 order by sc_detno",
					new BeanPropertyRowMapper(ShortCut.class), employee.getEm_id());
			if (shorts.size() == 0) {
				setDefaultShort(employee);
			}
			return shorts;
		} catch (EmptyResultDataAccessException e) {
			// 如果没有设置，就添加默认设置到数据库
			setDefaultShort(employee);
			List<ShortCut> shorts = baseDao.getJdbcTemplate().query(
					"select * from shortcut where sc_emid=? and sc_isuse=1 order by sc_detno",
					new BeanPropertyRowMapper(ShortCut.class), employee.getEm_id());
			return shorts;
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@CacheEvict(value = "short", allEntries = true)
	public void setShortCut(Employee employee, String data) {
		baseDao.updateByCondition("ShortCut", "sc_isuse=0", "sc_emid=" + employee.getEm_id());
		Iterator<String> keys = JSONObject.fromObject(data).keys();
		while (keys.hasNext()) {
			baseDao.updateByCondition("ShortCut", "sc_isuse=1", "sc_emid=" + employee.getEm_id() + " AND sc_name='"
					+ keys.next() + "'");
		}
	}

	public void setDefaultShort(Employee employee) {
		int emid = employee.getEm_id();
		ShortCut shortCut = new ShortCut(emid, "short_setting", 1, 1);
		baseDao.save(shortCut);
		shortCut = new ShortCut(emid, "short_news", 1, 2);
		baseDao.save(shortCut);
		shortCut = new ShortCut(emid, "short_address", 1, 3);
		baseDao.save(shortCut);
		shortCut = new ShortCut(emid, "short_calendar", 1, 4);
		baseDao.save(shortCut);
		shortCut = new ShortCut(emid, "short_email", 1, 5);
		baseDao.save(shortCut);
		shortCut = new ShortCut(emid, "short_bbs", 1, 6);
		baseDao.save(shortCut);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<CommonUse> getCommonUses(Employee employee,Integer count) {
		count=count!=null?count:20;
		try {
			return baseDao.getJdbcTemplate().query(
					"select * from (select * from CommonUse where cu_emid=? and cu_snid is not null order by cu_lock desc,cu_count desc) where rownum<=?",
					new BeanPropertyRowMapper(CommonUse.class), employee.getEm_id(),count);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public void setCommonUse(Integer id,String url,String addUrl,Employee employee,String caller) {
		String queryurl=url.replaceAll("'", "''");
		Object cuId = baseDao.getFieldDataByCondition("CommonUse", "cu_id", "CU_URL='" + queryurl + "' AND cu_emid="
				+ employee.getEm_id());
		if (cuId == null) {
			try{
				String displayName= baseDao.getFieldValue("SysNavigation", "sn_displayname", "sn_url='" +queryurl+"'", String.class);
				if (displayName!=null) {
					CommonUse commonUse = new CommonUse(displayName, id, employee.getEm_id(), url, addUrl,caller);
					baseDao.save(commonUse);
				}
			}catch (Exception e){
               e.printStackTrace();
			}
		} else {
			String modifyStr = " cu_count=cu_count+1 ";
			modifyStr += ",cu_snid="+id;
			modifyStr += ",cu_caller='"+caller+"'";
			if(addUrl != null && addUrl.trim().length() > 0) {
				modifyStr += ",cu_addUrl='"+addUrl+"' ";
			}
			baseDao.updateByCondition("CommonUse", modifyStr, "cu_id=" + cuId);
		}
	}

	@Override
	public void deleteCommonUse(int cuid) {
		baseDao.deleteById("CommonUse", "cu_id", cuid);
	}

	@Override
	public void updateCommonUse(Employee employee, int cuid, int type) {
		Integer count = 0;
		if (type == 1) {
			count = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select nvl(min(cu_count),0) from commonuse where cu_emid=? and cu_count>(select cu_count from commonuse where cu_id=?)",
							Integer.class, employee.getEm_id(), cuid);
		} else {
			count = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select nvl(max(cu_count),0) from commonuse where cu_emid=? and cu_count<(select cu_count from commonuse where cu_id=?)",
							Integer.class, employee.getEm_id(), cuid);
		}
		if (count > 0) {
			baseDao.updateByCondition("commonuse", "cu_count=" + (count + type), "cu_id=" + cuid);
		} else {
			baseDao.updateByCondition("commonuse", "cu_count=cu_count" + (type == 1 ? "+1" : "-1"), "cu_id=" + cuid);
		}
	}

	@Override
	public Map<String, Object> getDatas(String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void lockCommonUse(Employee employee, int cuid, int type) {
		if (type == 1) {
			baseDao.updateByCondition("commonuse", "cu_lock=1", "cu_id=" + cuid);
		} else {
			baseDao.updateByCondition("commonuse", "cu_lock=0", "cu_id=" + cuid);
		}
	}
}
