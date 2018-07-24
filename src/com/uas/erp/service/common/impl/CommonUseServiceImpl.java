package com.uas.erp.service.common.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.uas.erp.core.PasswordEncryUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Configs;
import com.uas.erp.model.Employee;
import com.uas.erp.model.EmpsJobs;
import com.uas.erp.model.commonuse.CommonUseItem;
import com.uas.erp.service.common.CommonUseService;

@Service
public class CommonUseServiceImpl implements CommonUseService {

	@Autowired
	private BaseDao baseDao;
	
	@Override
	public void importAll(Employee employee, JSONArray data) {
		List<String> sqls = new ArrayList<String>();
		int emid = employee.getEm_id();
		sqls.add("delete from COMMONUSE2 where cu_emid = " + emid);
		for(int i = 0; i < data.size(); i++) {
			JSONObject jsonObject = data.getJSONObject(i);
			String cuid = jsonObject.containsKey("cuid")?jsonObject.getString("cuid"):null;
			String cutext = jsonObject.containsKey("text")?jsonObject.getString("text"):null;
			int cugroup = jsonObject.containsKey("group")?(jsonObject.getBoolean("group") ? -1 : 0):0;
			int cugroupid = jsonObject.containsKey("groupid")?jsonObject.getInteger("groupid"):0;
			int cuindex = i;
			int itemid = jsonObject.getInteger("id");
			String parentid = jsonObject.containsKey("parentId")?(jsonObject.getString("parentId") != null ? ("'"+jsonObject.getString("parentId")+"'") : "null"):"null";
			int cuexpanded = jsonObject.containsKey("expanded")?(jsonObject.getBoolean("expanded") ? -1 : 0):0;
			String cuurl = jsonObject.containsKey("url")?(jsonObject.getString("url") != null ? ("'"+jsonObject.getString("url").replace("'", "''")+"'") : "null"):"null";
			String cuaddurl = jsonObject.containsKey("addurl")?(jsonObject.getString("addurl") != null ? ("'"+jsonObject.getString("addurl").replace("'", "''")+"'") : "null") : "null";
			JSONArray items = jsonObject.containsKey("items")?jsonObject.getJSONArray("items"):new JSONArray();
			sqls.add("insert into COMMONUSE2(cu_id,cu_text,cu_group,cu_groupid,cu_index,cu_itemid,cu_expanded,cu_emid,cu_url,cu_addurl,cu_parentid)"
					+ " values ("+((cuid==null || cuid.trim().length()==0)?"COMMONUSE2_SEQ.NEXTVAL":cuid)+",'"+cutext+"',"+cugroup+","+cugroupid+","+cuindex+","+itemid+","+cuexpanded+","
					+emid+","+cuurl+","+cuaddurl+","+parentid+")");
			for(int j = 0; j < items.size(); j++) {
				JSONObject itemobject = items.getJSONObject(j);
				String itemcuid = itemobject.containsKey("cuid")?itemobject.getString("cuid"):null;
				String itemcutext = itemobject.containsKey("text")?itemobject.getString("text"):null;
				int itemcugroup = 0;
				int itemcugroupid = itemobject.containsKey("groupid")?itemobject.getInteger("groupid"):0;
				int itemcuindex = j;
				int itemitemid = itemobject.getInteger("id");
				String itemparentid = itemobject.containsKey("parentId")?(itemobject.getString("parentId") != null ? ("'"+itemobject.getString("parentId")+"'") : "null"):"null";
				int itemcuexpanded = 0;
				String itemcuurl = itemobject.containsKey("url")?(itemobject.getString("url") != null ? ("'"+itemobject.getString("url").replace("'", "''")+"'") : "null"):"null";
				String itemcuaddurl = itemobject.containsKey("addurl")?(itemobject.getString("addurl") != null ? ("'"+itemobject.getString("addurl").replace("'", "''")+"'") : "null"):"null";
				sqls.add("insert into COMMONUSE2(cu_id,cu_text,cu_group,cu_groupid,cu_index,cu_itemid,cu_expanded,cu_emid,cu_url,cu_addurl,cu_parentid)"
						+ " values ("+((itemcuid==null || itemcuid.trim().length()==0)?"COMMONUSE2_SEQ.NEXTVAL":itemcuid)+",'"+itemcutext+"',"+itemcugroup+","+itemcugroupid+","+itemcuindex+","+
						itemitemid+","+itemcuexpanded+","+emid+","+itemcuurl+","+itemcuaddurl+","+itemparentid+")");
			}
		}
		baseDao.execute(sqls);
	}
	
	@Override
	public List<CommonUseItem> getList(Employee employee) {
		int emid = employee.getEm_id();
		List<CommonUseItem> data = new ArrayList<CommonUseItem>();
		data = baseDao.getJdbcTemplate().query("select * from COMMONUSE2 where cu_emid=?", new BeanPropertyRowMapper<CommonUseItem>(CommonUseItem.class), emid);
		// 如果未曾设置过常用功能项则从之前的常用模块表中填充数据
		if(data.size() == 0 ) {
			data = baseDao.getJdbcTemplate().query("select rownum cu_index,t.* from(select cu_description cu_text, 0 cu_group, '-1' cu_groupid, cu_snid cu_itemid,0 cu_expanded, cu_emid, cu_url, cu_addurl from COMMONUSE where cu_emid=? and cu_snid<>0 order by cu_lock desc,cu_count desc) t where rownum<=?",
					new BeanPropertyRowMapper<CommonUseItem>(CommonUseItem.class), emid, 20);
		}
		return data;
	}

	@Override
	public void add(Employee employee, boolean group, String groupid, String itemid, String items, int index) {
		// TODO Auto-generated method stub
	}

	@Override
	public void modify(Employee employee, String groupid, String text, int index) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void remove(Employee employee, String id) {
		// TODO Auto-generated method stub
	}

	@Override
	@Transactional
	public Map<String, List<String>> synchronous(Employee employee, String[] sobs) {
		Map<String, List<String>> masters = new HashMap<String, List<String>>();
		List<String> failureMasters = new ArrayList<String>();
		List<String> successMasters = new ArrayList<String>();
		String currentMaster = SpObserver.getSp();
		int currentEmid = employee.getEm_id();
		String currentEmcode = employee.getEm_code();
		for(int i = 0; i < sobs.length; i++) {
			String targetMaster = sobs[i];
			if(!targetMaster.toLowerCase().equals(currentMaster.toLowerCase())) {
				// 获得子账套的employee
				Employee targetEmployee = getEmployeeByTelOrCode(targetMaster, currentEmcode);
				
				if(targetEmployee == null) {
					failureMasters.add(targetMaster);
					continue;
				}	
							
				successMasters.add(targetMaster);
				// 删除原来的配置
				baseDao.execute("delete from " + targetMaster + ".COMMONUSE2 where cu_emid = "+targetEmployee.getEm_id()+"");
				// 同步非组别数据(不包括从配置文件添加的菜单节点)
				baseDao.execute("insert into "+targetMaster+".COMMONUSE2(cu_id,cu_text,cu_group,cu_groupid,cu_index,cu_itemid,cu_parentid,cu_expanded,cu_emid,cu_url,cu_addurl) "
						+ "select "+targetMaster+".COMMONUSE2_SEQ.NEXTVAL cu_id,t.* from(select cu_text,cu_group,cu_groupid,cu_index,sn_id cu_itemid,sn_parentid cu_parentid, cu_expanded,"+targetEmployee.getEm_id()+" cu_emid,cu_url,cu_addurl "
						+ "from "+currentMaster+".COMMONUSE2 left join "+targetMaster+".SYSNAVIGATION on cu_url=sn_url where (sn_using=1) and cu_emid ="+currentEmid+" and cu_group=0 and (cu_parentid not in('sys','json') or cu_parentid is null)) t");
				// 同步组别数据
				baseDao.execute("insert into "+targetMaster+".COMMONUSE2(cu_id,cu_text,cu_group,cu_groupid,cu_index,cu_itemid,cu_parentid,cu_expanded,cu_emid,cu_url,cu_addurl) "
						+ "select "+targetMaster+".COMMONUSE2_SEQ.NEXTVAL cu_id,t.* from(select cu_text,cu_group,cu_groupid,cu_index,cu_itemid,cu_parentid,cu_expanded,"+targetEmployee.getEm_id()+" cu_emid,cu_url,cu_addurl "
						+ "from "+currentMaster+".COMMONUSE2 where cu_emid ="+currentEmid+" and cu_group=-1) t");
				// 删除子账套不存在的
				baseDao.execute("delete from "+targetMaster+".COMMONUSE2 where cu_itemid is null and cu_group=0 and cu_emid="+targetEmployee.getEm_id());
				// 删除父节点不启用的数据
				SqlRowList rs = baseDao.queryForRowSet("select cu_itemid from "+targetMaster+".commonuse2 where cu_group=0");
				StringBuffer sb = new StringBuffer();
				while(rs.hasNext()) {
					rs.next();
					int itemID = rs.getInt("cu_itemid");
					int count = baseDao.getCount("select count(*) from (select * from "+targetMaster+".SYSNAVIGATION start with sn_id="+itemID+" connect by prior sn_parentid=sn_id) where SN_USING=0");
					if(count > 0) {
						sb.append(itemID+",");
					}
				}
				if(sb.length() > 0) {
					sb.deleteCharAt(sb.length()-1);
					baseDao.execute("delete from "+targetMaster+".COMMONUSE2 where cu_itemid in ("+sb+")");
				}
				if(targetEmployee.getEm_type().equals("admin")) {
					// 同步从系统维护管理配置文件添加的菜单节点
					baseDao.execute("insert into "+targetMaster+".COMMONUSE2(cu_id,cu_text,cu_group,cu_groupid,cu_index,cu_itemid,cu_parentid,cu_expanded,cu_emid,cu_url,cu_addurl) "
							+ "select "+targetMaster+".COMMONUSE2_SEQ.NEXTVAL cu_id,t.* from(select cu_text,cu_group,cu_groupid,cu_index,cu_itemid,cu_parentid, cu_expanded,"+targetEmployee.getEm_id()+" cu_emid,cu_url,cu_addurl "
							+ "from "+currentMaster+".COMMONUSE2 where cu_emid ="+currentEmid+" and cu_group=0 and cu_parentid='sys') t");
				}else {
					// 删除子账套无权限的
					StringBuffer jobIds = new StringBuffer(String.valueOf(targetEmployee.getEm_defaulthsid()));
					if (targetEmployee.getEmpsJobs() != null) {
						for (EmpsJobs empsJob : targetEmployee.getEmpsJobs()) {
							jobIds.append(",").append(empsJob.getJob_id());
						}
					}
					baseDao.execute("delete from "+targetMaster+".COMMONUSE2 where cu_itemid not in ("
							+ "select sn_id from "+targetMaster+".SYSNAVIGATION where case when sn_isleaf='T' then nvl(sn_url,' ') else 'T' end <> ' ' "
							+ "and (case when sn_isleaf='F' then to_char(sn_id) else sn_caller end in(select distinct pp_caller from "+targetMaster+".positionpower where (pp_alllist=1 or pp_selflist=1 or pp_jobemployee=1 or pp_see=1) and pp_joid in("+jobIds+")) "
							+ "or case when sn_isleaf='F' then to_char(sn_id) else sn_caller end in(select distinct pp_caller from "+targetMaster+".personalpower where (pp_alllist=1 or pp_selflist=1 or pp_jobemployee=1 or pp_see=1) and pp_emid="+targetEmployee.getEm_id()+") "
							+ ")"
							+ ") and cu_group=0 and cu_emid="+targetEmployee.getEm_id());
				}
				// 同步非组别数据(从配置文件添加的菜单节点，不包括系统维护管理配置节点)
				baseDao.execute("insert into "+targetMaster+".COMMONUSE2(cu_id,cu_text,cu_group,cu_groupid,cu_index,cu_itemid,cu_parentid,cu_expanded,cu_emid,cu_url,cu_addurl) "
						+ "select "+targetMaster+".COMMONUSE2_SEQ.NEXTVAL cu_id,t.* from(select cu_text,cu_group,cu_groupid,cu_index,cu_itemid,cu_parentid, cu_expanded,"+targetEmployee.getEm_id()+" cu_emid,cu_url,cu_addurl "
						+ "from "+currentMaster+".COMMONUSE2 where cu_emid ="+currentEmid+" and cu_group=0 and cu_parentid='json') t");
				// 删除重复数据（步骤5使用url左连接可能产生重复数据）
				baseDao.execute("delete from "+targetMaster+".COMMONUSE2 a where a.rowid != (select max(b.rowid) from "+targetMaster+".COMMONUSE2 b where a.cu_url = b.cu_url and a.cu_emid = b.cu_emid and a.cu_groupid = b.cu_groupid )");
				
			}
		}
		masters.put("successMasters", successMasters);
		masters.put("failureMasters", failureMasters);
		return masters;
	}
	
	private Employee getEmployeeByTelOrCode(String sob, String code) {
		int count = baseDao.getCount("select count(*) from "+sob+".employee where (nvl(em_mobile,' ')='"+code+"' or lower(em_code)=lower('"+code+"')) and nvl(em_class, ' ')<>'离职'");
		if(count>1){
			return null;
		}
		String sql = "select * from "+sob+".Employee where (em_mobile=? or lower(em_code)=lower(?)) and nvl(em_class, ' ')<>'离职'";
		try {
			Employee employee = baseDao.getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<Employee>(Employee.class), code,code);
			//密码解密
			employee = PasswordEncryUtil.decryptEmployeePassword(employee);
			try {
				employee.setJoborgnorelation(isDBSetting(sob, "Job","JobOrgNoRelation")?1:0);}
				catch (Exception e) {
				}
			try {
				List<EmpsJobs> empsJobs = baseDao.getJdbcTemplate().query("select * from "+sob+".EmpsJobs where emp_id=?",
						new BeanPropertyRowMapper<EmpsJobs>(EmpsJobs.class), employee.getEm_id());
				employee.setEmpsJobs(empsJobs);
			} catch (EmptyResultDataAccessException e) {

			} catch (Exception e) {

			}
			return employee;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private boolean isDBSetting(String sob, String caller, String code) {
		return is(sob, caller, code);
	}
	
	private boolean is(String sob, String caller, String code) {
		Configs configs = getConfigs(sob, caller, code);
		if (configs != null) {
			String data = configs.getData();
			if ("YN".equals(configs.getData_type())) {
				return String.valueOf(Constant.YES).equals(data);
			}
			return data == null ? false : true;
		}
		return false;
	}
	
	private Configs getConfigs(String sob, String caller, String code) {
		try {
			return baseDao.getJdbcTemplate().queryForObject("select * from "+sob+".configs where caller=? and code=?",
					new BeanPropertyRowMapper<Configs>(Configs.class), caller, code);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
}
