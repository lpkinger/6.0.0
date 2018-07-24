package com.uas.mobile.dao.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.mobile.dao.AllProcessDao;
import com.uas.mobile.model.AllProcess;

/**
 * 待办事宜Dao实现类
 * @author suntg
 * @date 2014年9月9日 15:01:53
 */
@Repository("allProcessDao")
public class AllProcessDaoImpl extends BaseDao implements AllProcessDao{
	@Autowired
	private EnterpriseService enterpriseService;

	/**
	 * 获取当前处理人的待办事宜记录
	 * @param employeeCode 处理人编号
	 * @return 处理人员工编号为employeeCode的待办事宜记录
	 */
	@Override
	public List<AllProcess> getAllProcessByDealPersonCode(String employeeCode, String currentMaster) {
		String sql = "";
		String sonCodeStr = getMasterSonCode(currentMaster);
		if("datacenter".equals(currentMaster.toLowerCase()) ||  StringUtils.hasText(sonCodeStr)) {
			//有子账套的集团账套要将子账套的待办事宜联起来处理
			sql = "select * from ( ";
			String[] sonCode = sonCodeStr.split(",");
			for(int i=0; i<sonCode.length; i ++) {
				if(i == 0) {
					sql += "select '" + sonCode[i] + "' as master, ALLPROCESS_UNDO_VIEW.* from "
							+ sonCode[i] + ".ALLPROCESS_UNDO_VIEW";
				} else {
					sql += " union select '" + sonCode[i] + "' as master, ALLPROCESS_UNDO_VIEW.* from "
							+ sonCode[i] + ".ALLPROCESS_UNDO_VIEW";
				}
			}
			sql += " ) where (nvl(status,' ')<>'未通过'  and dealpersoncode='" + employeeCode + "') or "
					+ "(nvl(status,' ')='未通过'  and recorderid='" + employeeCode + "') "
					+ "order by datetime desc";
		} else {
			sql = "select '" + currentMaster + "' as master, ALLPROCESS_UNDO_VIEW.* from "
					+ currentMaster + ".ALLPROCESS_UNDO_VIEW where "
					+ "(nvl(status,' ')<>'未通过'  and dealpersoncode='" + employeeCode + "') or "
					+ "(nvl(status,' ')='未通过'  and recorderid='" + employeeCode + "') order by datetime desc";
		}
		sql = "select * from ( " + sql + " ) where rownum<51";//只取前50条
		return getJdbcTemplate().query(sql, new BeanPropertyRowMapper<AllProcess>(AllProcess.class));
	}

	/**
	 * 获取当前处理人的待办事宜中Id大于当前ID的记录
	 * @param employeeCode 处理人编号
	 * @param time 输入的待办事宜的time
	 * @return 处理人员工编号为employeeCode的id大于id的待办事宜记录
	 */
	@Override
	public List<AllProcess> getAllProcessSinceTime(String employeeCode, long time, String currentMaster) {
		String sql = "";
		String sonCodeStr = getMasterSonCode(currentMaster);
		String datetime = new Timestamp(time).toString();
		String datetimeToTimestamp = "to_timestamp('" + datetime + "','yyyy-MM-dd HH24:MI:ss.ff')";
		if("datacenter".equals(currentMaster.toLowerCase()) || StringUtils.hasText(sonCodeStr)) {
			// 有子账套的集团账套要将子账套的待办事宜联起来处理
			sql = "select * from ( ";
			String[] sonCode = sonCodeStr.split(",");
			for(int i=0; i<sonCode.length; i ++) {
				if(i == 0) {
					sql += "select '" + sonCode[i] + "' as master, ALLPROCESS_UNDO_VIEW.* from "
							+ sonCode[i] + ".ALLPROCESS_UNDO_VIEW";
				} else {
					sql += " union select '" + sonCode[i] + "' as master, ALLPROCESS_UNDO_VIEW.* from "
							+ sonCode[i] + ".ALLPROCESS_UNDO_VIEW";
				}
			}
			sql += " )  where (nvl(status,' ')<>'未通过'  and dealpersoncode='" + employeeCode + "') or "
					+ "(nvl(status,' ')='未通过'  and recorderid='" + employeeCode + "') and datetime > "
					+ datetimeToTimestamp + " order by datetime desc";
		} else {
			sql = "select '" + currentMaster + "' as master, ALLPROCESS_UNDO_VIEW.* from "
					+ currentMaster + ".ALLPROCESS_UNDO_VIEW where "
					+ "(nvl(status,' ')<>'未通过'  and dealpersoncode='" + employeeCode + "') or "
					+ "(nvl(status,' ')='未通过'  and recorderid='" + employeeCode + "')"
					+ " and datetime > " + datetimeToTimestamp + " order by datetime desc";
		}
		sql = "select * from ( " + sql + " ) where rownum<51";//只取前50条
		return getJdbcTemplate().query(sql,new BeanPropertyRowMapper<AllProcess>(AllProcess.class));
	}

	/**
	 * 获取当前处理人的最新一条待办事宜记录
	 * @param employeeCode 处理人编号
	 * @return 处理人员工编号为employeeCode的最新一条（id最大的一条）记录
	 */
	@Override
	public AllProcess getLastAllProcess(String employeeCode, String currentMaster) {
		String sql = "select * from allprocess_undo_view where id=(select max(id) from allprocess_undo_view where "
				+ "(nvl(status,' ')<>'未通过'  and dealpersoncode='" + employeeCode + "') or "
				+ "(nvl(status,' ')='未通过'  and recorderid='" + employeeCode + "') )";
		return getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<AllProcess>(AllProcess.class));
	}

	/**
	 * 获取当前处理人的最后一条待办事宜的时间
	 * @param employeeCode 处理人编号
	 * @return 处理人员工编号为employeeCode的待办事宜的最新的时间
	 */
	@Override
	public long getLastTime(String employeeCode, String currentMaster) {
		String sql = "";
		String sonCodeStr = getMasterSonCode(currentMaster);
		if("datacenter".equals(currentMaster.toLowerCase()) || (getMasterType(currentMaster) != 3 && StringUtils.hasText(sonCodeStr))) {
			//有子账套的集团账套要将子账套的待办事宜联起来处理
			sql = "select max(datetime) from ( ";
			String[] sonCode = sonCodeStr.split(",");
			for(int i=0; i<sonCode.length; i ++) {
				if(i == 0) {
					sql += "select '" + sonCode[i] + "' as master, ALLPROCESS_UNDO_VIEW.* from "
							+ sonCode[i] + ".ALLPROCESS_UNDO_VIEW";
				} else {
					sql += " union select '" + sonCode[i] + "' as master, ALLPROCESS_UNDO_VIEW.* from "
							+ sonCode[i] + ".ALLPROCESS_UNDO_VIEW";
				}
			}
			sql += " ) where (nvl(status,' ')<>'未通过'  and dealpersoncode='" + employeeCode + "') or "
					+ "(nvl(status,' ')='未通过'  and recorderid='" + employeeCode + "')";
		} else {//非集团账套
			sql = "select max(datetime) from " + currentMaster + ".ALLPROCESS_UNDO_VIEW where "
				+ "(nvl(status,' ')<>'未通过'  and dealpersoncode='" + employeeCode + "') or "
				+ "(nvl(status,' ')='未通过'  and recorderid='" + employeeCode + "')";
		}
		long result = 0;
		try {
			result =  getJdbcTemplate().queryForObject(sql, Date.class).getTime();
		} catch(IncorrectResultSizeDataAccessException e) {
			result = 1;
		} catch(NullPointerException e) {
			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取当前处理人的待办事宜记录数
	 */
	@SuppressWarnings("deprecation")
	@Override
	public int getAllProcessCount(String employeeCode, String currentMaster) {
		String sql = "";
		String sonCodeStr = getMasterSonCode(currentMaster);
		if("datacenter".equals(currentMaster.toLowerCase()) || (getMasterType(currentMaster) != 3 && StringUtils.hasText(sonCodeStr))) {
			//有子账套的集团账套要将子账套的待办事宜联起来处理
			sql = "select count(*) from ( ";
			String[] sonCode = sonCodeStr.split(",");
			for(int i=0; i<sonCode.length; i ++) {
				if(i == 0) {
					sql += "select '" + sonCode[i] + "' as master, ALLPROCESS_UNDO_VIEW.* from "
							+ sonCode[i] + ".ALLPROCESS_UNDO_VIEW";
				} else {
					sql += " union select '" + sonCode[i] + "' as master, ALLPROCESS_UNDO_VIEW.* from "
							+ sonCode[i] + ".ALLPROCESS_UNDO_VIEW";
				}
			}
			sql += " ) where (nvl(status,' ')<>'未通过'  and dealpersoncode='" + employeeCode + "') or "
					+ "(nvl(status,' ')='未通过'  and recorderid='" + employeeCode + "')";
		} else {//非集团账套
			sql = "select count(*) from " + currentMaster + ".ALLPROCESS_UNDO_VIEW where "
					+ "(nvl(status,' ')<>'未通过'  and dealpersoncode='" + employeeCode + "') or "
					+ "(nvl(status,' ')='未通过'  and recorderid='" + employeeCode + "')";
		}
		return getJdbcTemplate().queryForInt(sql);
	}

	/**
	 * 获取当前账套的类型
	 */
	@SuppressWarnings("deprecation")
	@Override
	public int getMasterType(String currentMaster) {
		String sql = "select ma_type from " + getDefaultSob() + ".master where ma_name='" + currentMaster + "'";
		int result = 3;
		try {
			result = getJdbcTemplate().queryForInt(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取当前账套的子账套
	 */
	@Override
	public String getMasterSonCode(String currentMaster) {
		String sql = "select ma_soncode from " + getDefaultSob() + ".master where ma_name='" + currentMaster + "'";
		return getJdbcTemplate().queryForObject(sql, String.class);
	}
	
	/**
	 * 获取当前的默认账套，一般也就是集团账套
	 * @return
	 */
	public String getDefaultSob() {
		return BaseUtil.getXmlSetting("defaultSob");
	}

	@Override
	public long getLastTimeByMaster(String currentMaster) {
		long result = 0;
		String sql =  "select max(datetime) from " + currentMaster + ".ALLPROCESS_UNDO_VIEW ";
		try {
			Date lastDate =  getJdbcTemplate().queryForObject(sql, Date.class);
			if( lastDate != null) {
				result = lastDate.getTime();
			}
		} catch(IncorrectResultSizeDataAccessException e) {
			result = 0;
		} catch(NullPointerException e) {
			result = 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public List<AllProcess> getAllProcessSinceTimeByMaster(long time, String currentMaster) {
		String sql = "";
		String datetime = new Timestamp(time).toString();
		String datetimeToTimestamp = "to_timestamp('" + datetime + "','yyyy-MM-dd HH24:MI:ss.ff')";
		sql = "select '" + currentMaster + "' as master, ALLPROCESS_UNDO_VIEW.* from "
				+ currentMaster + ".ALLPROCESS_UNDO_VIEW where typecode<>'dingyue' and "
				+ " datetime > " + datetimeToTimestamp + " order by datetime desc";
		return getJdbcTemplate().query(sql,new BeanPropertyRowMapper<AllProcess>(AllProcess.class));
	}
	
	@Override
	public List<AllProcess> getAllProcessSinceTimeByMaster2(long time, String currentMaster) {
		//增加取订阅的条件typecode='dingyue'
		String sql = "";
		String datetime = new Timestamp(time).toString();
		String datetimeToTimestamp = "to_timestamp('" + datetime + "','yyyy-MM-dd HH24:MI:ss.ff')";
		sql = "select '" + currentMaster + "' as master, ALLPROCESS_UNDO_VIEW.* from "
				+ currentMaster + ".ALLPROCESS_UNDO_VIEW where typecode='dingyue' and "
				+ " datetime >= " + datetimeToTimestamp + " order by datetime desc";
		return getJdbcTemplate().query(sql,new BeanPropertyRowMapper<AllProcess>(AllProcess.class));
	}
}
