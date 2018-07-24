package com.uas.erp.dao.common.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DataListDao;
import com.uas.erp.model.DataList;
import com.uas.erp.model.DataListDetail;
import com.uas.erp.model.Employee;
import com.uas.erp.model.EmpsJobs;

/**
 * 根据caller拿到对应的datalist
 * 
 * @author yingp
 */

@Repository("dataListDao")
public class DataListDaoImpl extends BaseDao implements DataListDao {

	@Cacheable(value = "datalist", key = "#caller + #sob + 'getDataList'",unless="#result==null")
	public DataList getDataList(String caller, String sob) {
		try {
			DataList dataList = getJdbcTemplate().queryForObject("select *  from datalist where dl_caller=?", new BeanPropertyRowMapper<DataList>(DataList.class), caller);
			List<DataListDetail> dataListDetails = getJdbcTemplate(dataList.getDl_tablename()).query("select * from datalistdetail where dld_dlid=? order by dld_detno", new BeanPropertyRowMapper<DataListDetail>(DataListDetail.class), dataList.getDl_id());
			dataList.setDataListDetails(dataListDetails);
			return dataList;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	@Cacheable(value = "datalistEm", key = "#caller + #employee.em_master +#employee.em_code+ 'getDataListByEm'",unless="#result==null")
	public DataList getDataListByEm(String caller, Employee employee) {
		try {
			DataList dataList = getJdbcTemplate().queryForObject("select *  from datalist where dl_caller=?", new BeanPropertyRowMapper<DataList>(DataList.class), caller);
			List<DataListDetail> dataListDetails = getJdbcTemplate(dataList.getDl_tablename()).query("select * from datalistdetail  left join (select * from datalistdetailemps  where dde_dlid=? and dde_emid=?) on dld_dlid=dde_dlid and datalistdetail.dld_field=dde_field where dld_dlid=? and (dde_emid=?  or dde_emid is null) order by dde_detno", new BeanPropertyRowMapper<DataListDetail>(DataListDetail.class), dataList.getDl_id(), employee.getEm_id(), dataList.getDl_id(), employee.getEm_id());
			dataList.setDataListDetails(dataListDetails);
			dataList.setPersonality(true);
			String orderby = getFieldValue("(select DDE_FIELD,dde_orderby from datalistdetailemps where  dde_emid=" + employee.getEm_id() + " and dde_dlid=" + dataList.getDl_id() + " and dde_orderby is not null order by dde_priority asc)", "wmsys.wm_concat(DDE_FIELD||' '||dde_orderby)", "1=1", String.class);
			if (orderby != null)
				dataList.setDl_orderby(" order by " + orderby);
			return dataList;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	@Cacheable(value = "empsrelativesettings", key = "#caller + #kind + #emid + 'getRelativesettings'",unless="#result==null")
	public String getRelativesettings(String caller, String kind, int emid) {
		SqlRowList sl = queryForRowSet("select es_field||es_conditionstr  from  empsrelativesettings  where es_emid=" + emid + "  and es_pagecaller='" + caller + "' and nvl(es_kind,' ')='" + kind + "'");
		String conditionstr = "";
		while (sl.next()) {
			conditionstr += sl.getString(1) + " and ";
		}
		if ("".equals(conditionstr))
			return null;
		else
			return conditionstr.substring(0, conditionstr.length() - 4);
	}

	/**
	 * {getDataStringByDataList()}的新方法 支持jboss
	 * 
	 * @param dataList
	 *            配置
	 * @param condition
	 *            条件语句
	 * @param employee
	 *            当前操作人
	 * @param page
	 *            当前页
	 * @param pageSize
	 *            每页显示条数
	 * @param _f
	 *            使用快速取数模式
	 * @param _alia
	 *            使用短别名简化模式
	 * @param orderby
	 *            自定义排序
	 */
	public List<Map<String, Object>> getDataListData(DataList dataList, String condition, Employee employee, int page, int pageSize, Integer _f, boolean _alia, String orderby,boolean jobemployee) {
		String con = dataList.getDl_condition();
		condition = (con == null || "".equals(con)) ? condition : ("(" + con + ")" + ((condition == null || "".equals(condition)) ? "" : " AND (" + condition + ")"));
		String sql = null;
		String sort = dataList.getDl_orderby();
		if (StringUtils.hasText(orderby))
			sort = orderby;
		boolean isFast = _f != null && _f == Constant.YES;
		/**
		 * dataList.getFasterSql换为dataList.getSearchSql
		 * 
		 * @date 2016-6-20 16:39:14
		 */

		sql = dataList.getSearchSql(condition, sort, employee, page, pageSize);
		
		if(jobemployee){
			sql = getSqlWithJobEmployee(null) + sql;
		}
		
		SqlRowList rs = queryForRowSet(sql);
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		int index = 0;
		while (rs.next()) {
			index = 0;
			Map<String, Object> map = new HashMap<String, Object>();
			for (DataListDetail detail : dataList.getDataListDetails()) {
				String field = detail.getDld_field();
				if (field.contains(" ")) {// column有取别名
					String[] strs = field.split(" ");
					field = strs[strs.length - 1];
				}
				Object value = rs.getObject(field);
				value = value == null || value.equals("null") ? "" : SqlRowList.parseValue(value);
				if (_alia) {
					if (detail.getDld_width() != 0 || detail.getDld_flex() != 0) {
						map.put(String.valueOf((char) (48 + ++index)), value);
					}
				} else
					map.put(field, value);
			}
			if (isFast)
				map.remove("RN");
			datas.add(map);
		}
		return datas;
	}

	public String getDataStringByDataList(DataList dataList, String condition, Employee employee, int page, int pageSize) {
		List<Map<String, Object>> maps = getDataListData(dataList, condition, employee, page, pageSize, null, false, null,false);
		return BaseUtil.parseGridStore2Str(maps);
	}

	@Override
	public List<Map<String, Object>> getSummaryData(DataList dataList, String condition,boolean jobemployee) {
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		String sql = dataList.getSummarySql(condition);
		if (!StringUtils.hasText(sql))
			return lists;
		
		if(jobemployee){
			sql = getSqlWithJobEmployee(null) + sql;
		}
		
		SqlRowList rs = queryForRowSet(sql);
		Map<String, Object> map = null;
		int index = 0;
		if (rs.next()) {
			for (DataListDetail detail : dataList.getDataListDetails()) {
				if (StringUtils.hasText(detail.getDld_summarytype())) {
					index++;
					map = new HashMap<String, Object>();
					map.put("field", detail.getDld_field());
					map.put("value", rs.getObject(index));
					map.put("type", detail.getDld_summarytype());
					lists.add(map);
				}
			}
		}
		return lists;
	}

	@Override
	public String getSqlWithJobEmployee(Employee employee) {
		if(employee==null){
			employee = SystemSession.getUser();
		}
		
		Integer jobId = employee.getEm_defaulthsid();
		
		String jobIds = null;
		if(jobId!=null){
			jobIds = String.valueOf(jobId);
		}
		for (EmpsJobs empsJob : employee.getEmpsJobs()) {
			if(empsJob.getJob_id()!=null){
				jobIds += "," + empsJob.getJob_id();
			}
		}
		String sql = "with " + Constant.TEMP_TABLE_NAME + " as (select hj_joid,hj_em_id,hj_em_code,hj_em_name from  hrjobemployees where nvl(hj_joid,0) in ("+jobIds+") union all select " + employee.getEm_defaulthsid()+ "," + employee.getEm_id() + ",'"+employee.getEm_code()+"','"+employee.getEm_name()+"' from dual) ";
		return sql;
	}

}
