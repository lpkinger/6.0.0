package com.uas.api.serve.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.api.serve.service.ServeCommonService;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;

@Service
public class ServeCommonServiceImpl  extends ServeCommon implements ServeCommonService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Override
	public List<Map<String, Object>> getDefaultServices(String basePath, String kind) {
		
		List<Map<String, Object>> serves = new ArrayList<Map<String,Object>>();
		SqlRowList rs = null;
		Employee employee = SystemSession.getUser();
		String sql = "SELECT sv_id,sv_name,sv_moblogo,sv_platlogo FROM SERVICEDEFAULT left join SERVICE on SDF_SVID = SV_ID "
				+ "LEFT JOIN SERVICETYPE ON SV_STID = ST_ID WHERE NVL(SV_ENABLE,0)<>0 and ST_KIND = ? AND SDF_UU = ? ORDER BY SDF_DETNO";
		rs = baseDao.queryForRowSet(sql, kind, employee.getEm_uu());
		if (!rs.hasNext()) {
			sql = "SELECT sv_id,sv_name,sv_moblogo,sv_platlogo FROM SERVICE LEFT JOIN SERVICETYPE ON SV_STID = ST_ID "
					+ "WHERE NVL(SV_ENABLE,0)<>0 and ST_KIND = ? and nvl(sv_type,'common') = 'default' ORDER BY SV_DETNO";
			rs = baseDao.queryForRowSet(sql, kind);
		}
		
		while(rs.next()){
			Map<String, Object> serve = new HashMap<String, Object>();
			serve.put("sv_id", rs.getGeneralInt("sv_id"));
			serve.put("sv_name", rs.getString("sv_name"));
			serve.put("sv_logourl", getLogoUrl(basePath, rs.getString("sv_moblogo")));
			serves.add(serve);
		}
		return serves;
	}
	
	@Override
	public void setDefaultServices(String kind,String ids) {
		String stids = baseDao.queryForObject("SELECT WMSYS.WM_CONCAT(ST_ID) FROM SERVICETYPE WHERE ST_KIND = ?", String.class, kind);
		if (stids!=null) {
			String [] IDS = stids.split(",");
			Employee employee = SystemSession.getUser();
			List<Map<Object, Object>> list = new ArrayList<Map<Object,Object>>();
			for (int i= 0; i<IDS.length; i++) {
				Map<Object, Object> map = new HashMap<Object, Object>();
				int id = baseDao.getSeqId("SERVICEDEFAULT_SEQ");
				map.put("sdf_id", id);
				map.put("sdf_svid", IDS[i]);
				map.put("sdf_uu", employee.getEm_uu());
				map.put("sdf_detno", i+1);
				list.add(map);
			}
			baseDao.deleteByCondition("SERVICEDEFAULT", "sdf_uu = ?", employee.getEm_uu());
			baseDao.execute(SqlUtil.getInsertSqlbyGridStore(list, "SERVICEDEFAULT"));
		}
	}

	@Override
	public List<Map<String, Object>> getProcesses(Long serve_id, Integer id) {
		List<Map<String, Object>> process = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		String caller = baseDao.queryForObject("select sv_caller from Service where sv_id = ?", String.class, serve_id);
		if (!StringUtil.hasText(caller)) {
			BaseUtil.showError("未绑定配置，请联系管理员！");
		}else{
			String submittime = null;
			SqlRowList rs = baseDao.queryForRowSet("select jt_neccessaryfield,JN_DEALTIME,to_char(jp_launchtime,'yyyy-MM-dd Hh24:mm:ss') jp_launchtime from jtask left join "
				+ "(select JP_NODENAME,JN_DEALTIME,jp_launchtime from JPROCESS left join Jnode on JP_FORM = JN_PROCESSINSTANCEID  AND JP_NODENAME = jn_name "
				+ "where jp_caller = ? and jp_keyvalue = ? and jp_status='已审批') on jp_nodename = jt_name "
				+ "where jt_processdefid=(select JD_PROCESSDEFINITIONID from jprocessdeploy where jd_caller = ?) order by jt_id",caller,id,caller);
			while(rs.next()){
				Map<String, Object> map = new HashMap<String, Object>();
				if (submittime==null) {
					submittime = rs.getString("jp_launchtime");
				}
				map.put("status", rs.getString("jt_neccessaryfield"));
				map.put("time", rs.getString("JN_DEALTIME"));
				list.add(map);
			}
			if (submittime==null) {
				submittime = baseDao.queryForObject("select to_char(jp_launchtime,'yyyy-MM-dd Hh24:mm:ss') jp_launchtime from "
						+ "JPROCAND where jp_caller = ? and JP_KEYVALUE = ? group by jp_launchtime", String.class, caller, id);
			}
			if (submittime==null) {
				submittime = baseDao.queryForObject("select to_char(jp_launchtime,'yyyy-MM-dd Hh24:mm:ss') jp_launchtime from "
						+ "JPROCESS where jp_caller = ? and JP_KEYVALUE = ? group by jp_launchtime", String.class, caller, id);
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("status", "提交申请");
			map.put("time", submittime);
			process.add(map);
			process.addAll(list);
		}
		return process;
	}

}
