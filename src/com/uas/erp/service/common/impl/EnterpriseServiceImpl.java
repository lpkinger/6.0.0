package com.uas.erp.service.common.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.EnterpriseDao;
import com.uas.erp.model.Enterprise;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EnterpriseService;

import oracle.net.aso.i;

@Service("enterpriseService")
public class EnterpriseServiceImpl implements EnterpriseService {
	@Autowired
	private EnterpriseDao enterpriseDao;
	@Autowired
	private BaseDao baseDao;

	@Override
	public boolean checkEnterpriseName(String name) {
		// 根据en_Name到数据库查看
		// 如果结果集为空，则en_Name未被注册，可以使用，返回true
		// 反之，返回false
		if (enterpriseDao.getEnterpriseByName(name) == null) {
			return true;
		}
		return false;
	}

	/**
	 * 保存企业注册信息
	 * 
	 * @param enterprise
	 *            企业信息
	 */
	@Override
	public void saveEnterprise(Enterprise enterprise) {
		enterpriseDao.saveEnterprise(enterprise);
	}

	/**
	 * 判断以企业账号登录的用户的信息
	 * 
	 * @param username
	 *            en_uu
	 * @param password
	 *            密码
	 */
	@Override
	public String loginWithEn(String username, String password) {
		Enterprise enterprise = enterpriseDao.getEnterpriseByEnUU(Integer.parseInt(username));
		if (enterprise == null) {
			return "企业账号不存在!";
		} else if (!password.equals(enterprise.getEn_password())) {
			return "密码错误!";
		}
		return null;
	}

	@Override
	public Enterprise getEnterpriseById(int id) {
		return enterpriseDao.getEnterpriseById(id);
	}

	@Override
	public List<Master> getMasters() {
		return enterpriseDao.getMasters();
	}

	@Override
	public Master getMasterByName(String dbname) {
		return enterpriseDao.getMasterByName(dbname);
	}
	
	/* 获取未 */
	@Override
	public List<Master> getAbleMasters() {
		String sob = SpObserver.getSp();
		SpObserver.putSp(BaseUtil.getXmlSetting("defaultSob"));
		List<Master> masters = enterpriseDao.getAbleMaster();
		SpObserver.putSp(sob);
		return masters;
	}

	@Override
	public List<Master> getAbleMastersByEmMasters(String emMasters,Boolean isOwnerMaster) {
		if (StringUtil.hasText(emMasters) && (isOwnerMaster!=null && isOwnerMaster)) {
			String sob = SpObserver.getSp();
			SpObserver.putSp(BaseUtil.getXmlSetting("defaultSob"));
			List<Master> masters = baseDao.query("select * from master where Ma_User in ('"+emMasters.replaceAll(",","','")+"') and ma_enable=1 order by ma_id", Master.class);
			SpObserver.putSp(sob);
			if (masters.size()<1) 
			  return getAbleMasters();
			return masters;
		}
		return getAbleMasters();
	}
	
	@Override
	public List<Object> getMasterNames() {
		return baseDao.getFieldDatasByCondition("Master", "ma_name", "ma_type<>2");
	}

	@Override
	public boolean checkMasterNamePwd(String name, String pwd) {
		return baseDao.getCountByCondition("Master", "ma_name='" + name + "' AND ms_pwd='" + pwd + "'") == 1;
	}

	@Override
	public String getMasterByUU(Integer uu) {
		String sob = SpObserver.getSp();
		SpObserver.putSp(BaseUtil.getXmlSetting("defaultSob"));
		String master = baseDao.getJdbcTemplate().queryForObject("select ma_name from master where ma_uu=?", String.class, uu);
		SpObserver.putSp(sob);
		return master;
	}
	
	@Override
	public String getMasterByUU(String uu) {
		String sob = SpObserver.getSp();
		SpObserver.putSp(BaseUtil.getXmlSetting("defaultSob"));
		Object master = baseDao.getFieldDataByCondition("master", "ma_name", "ma_uu='"+uu+"'");
		SpObserver.putSp(sob);
		if(master!=null){
			return (String)master;
		}else{
			return null;
		}
	}
	

	@Override
	public Master getMasterByManage(long manageId) {
		String sob = SpObserver.getSp();
		SpObserver.putSp(BaseUtil.getXmlSetting("defaultSob"));
		try {
			return baseDao.getJdbcTemplate().queryForObject("select * from master where ma_manageid=?", Master.class, manageId);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} finally {
			SpObserver.putSp(sob);
		}
	}

	@Override
	public Master getMasterByID(int id) {
		List<Master> masters = getMasters();
		if (masters != null) {
			for (Master m : masters) {
				if (m.getMa_id() == id) {
					return m;
				}
			}
		}
		return null;
	}

	@Override
	public Enterprise getEnterprise() {
		return enterpriseDao.getEnterprise();
	}

	@Override
	public List<Map<String, Object>> getOutMasters() {
		List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
		boolean bool = baseDao.checkTableName("MASTEROUT");
		if (bool) {
			SqlRowList sl = baseDao.queryForRowSet("select  * from masterout order by mo_id asc");
			while (sl.next())
				maps.add(sl.getCurrentMap());
		}
		return maps;
	}

	@Override
	public Master getMasterByDomain(String domain) {
		String sob = SpObserver.getSp();
		SpObserver.putSp(BaseUtil.getXmlSetting("defaultSob"));
		Master master = enterpriseDao.getMasterByDomain(domain);
		SpObserver.putSp(sob);
		return master;
	}

	@Override
	@CacheEvict(value = "master", allEntries = true)
	public void clearMasterCache() {

	}

	/**
	 * 判断组织岗位是否关联，不关联返回1
	 */
	@Override
	public boolean checkJobOrgRelation() {
		return baseDao.isDBSetting("Job", "JobOrgNoRelation");
	}

	@Override
	public Object getDefaultMasterName() {
		// TODO Auto-generated method stub
		return BaseUtil.getXmlSetting("defaultSob");
	}

	@Override
	public Object getDefaultMasterFun() {
		// TODO Auto-generated method stub
		String sob = SpObserver.getSp();
		SpObserver.putSp(BaseUtil.getXmlSetting("defaultSob"));
		Object en = baseDao.getFieldDataByCondition("master", "ma_function", "ma_name='" + BaseUtil.getXmlSetting("defaultSob") + "'");
		SpObserver.putSp(sob);
		return en;
		/*
		 * return
		 * baseDao.getFieldDataByCondition(BaseUtil.getXmlSetting("defaultSob"
		 * )+".master", "ma_function",
		 * "ma_name='"+BaseUtil.getXmlSetting("defaultSob")+"'");
		 */
	}

	@Override
	public Object getDefaultEnterpriseName() {
		// TODO Auto-generated method stub
		String sob = SpObserver.getSp();
		SpObserver.putSp(BaseUtil.getXmlSetting("defaultSob"));
		Object en = baseDao.getFieldDataByCondition("ENTERPRISE", "EN_NAME", "1=1");
		SpObserver.putSp(sob);
		return en;
		/*
		 * return
		 * baseDao.getFieldDataByCondition(BaseUtil.getXmlSetting("defaultSob"
		 * )+".ENTERPRISE", "EN_NAME","1=1");
		 */
	}



}
