package com.uas.erp.dao.common.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ProjectDocPowerDao;
import com.uas.erp.model.Employee;

@Repository
public class ProjectDocPowerDaoImpl implements ProjectDocPowerDao {

	@Autowired
	private BaseDao baseDao;
	
	@Override
	public void powerForManage(Object id, Integer _noc) {
		_noc = _noc ==null?0:_noc;
		Employee employee = SystemSession.getUser();
		String [] ems = baseDao.getDBSettingArray("ProjectRequest", "fileOperator");
		boolean fileOperator = false;
		if (ems!=null) {
			for (String em : ems) {
				if (em!=null&&em.equals(employee.getEm_code())) {
					fileOperator = true;
					break;
				}
			}
		}
		
		Object assigntocode = baseDao.getFieldDataByCondition("Project,ProjectDoc", "prj_assigntocode", "prj_id = pd_prjid and pd_id = "+id);
		if ("admin".equals(employee.getEm_type())||_noc==1||(baseDao.isDBSetting("ProjectRequest", 
				"DefaultDocPower")&&assigntocode!=null&&assigntocode!=null&&assigntocode.equals(employee.getEm_code()))||fileOperator) {
			return ;
		}else {
			Object manage = baseDao.getFieldDataByCondition("ProjectDocPower", "pp_manage", "pp_docid="+id+" and pp_emid="+employee.getEm_id());
			if (manage!=null&&"1".equals(manage.toString())) {
				return ;
			}else {
				BaseUtil.showError("你没有管理此文件夹的权限！");
			}
		}
	}

	@Override
	public String powerForScan(Object id, Integer _noc) {
		_noc = _noc ==null?0:_noc;
		Employee employee = SystemSession.getUser();
		String [] ems = baseDao.getDBSettingArray("ProjectRequest", "fileOperator");
		boolean fileOperator = false;
		if (ems!=null) {
			for (String em : ems) {
				if (em!=null&&em.equals(employee.getEm_code())) {
					fileOperator = true;
					break;
				}
			}
		}
		Object assigntocode = baseDao.getFieldDataByCondition("Project,ProjectDoc", "prj_assigntocode", "prj_id = pd_prjid and pd_id = "+id);
		if ("admin".equals(employee.getEm_type())||_noc==1||(baseDao.isDBSetting("ProjectRequest", "DefaultDocPower")
				&&assigntocode!=null&&assigntocode.equals(employee.getEm_code()))||fileOperator) {
			return null;
		}else {
			Object scan = baseDao.getFieldDataByCondition("ProjectDocPower", "pp_scan", "pp_docid="+id+" and pp_emid="+employee.getEm_id());
			if (scan!=null&&"1".equals(scan.toString())) {
				return null;
			}else {
				return "你没有浏览此文件夹文件的权限！";
			}
		}
	}
	
	@Override
	public void powerForRead(Object id, Integer _noc) {
		_noc = _noc ==null?0:_noc;
		Employee employee = SystemSession.getUser();
		String [] ems = baseDao.getDBSettingArray("ProjectRequest", "fileOperator");
		boolean fileOperator = false;
		if (ems!=null) {
			for (String em : ems) {
				if (em!=null&&em.equals(employee.getEm_code())) {
					fileOperator = true;
					break;
				}
			}
		}
		Object assigntocode = baseDao.getFieldDataByCondition("Project,ProjectDoc", "prj_assigntocode", "prj_id = pd_prjid and pd_id = "+id);
		if ("admin".equals(employee.getEm_type())||_noc==1||(baseDao.isDBSetting("ProjectRequest", "DefaultDocPower")
				&&assigntocode!=null&&assigntocode.equals(employee.getEm_code()))||fileOperator) {
			return ;
		}else {
			Object read = baseDao.getFieldDataByCondition("ProjectDocPower", "pp_read", "pp_docid="+id+" and pp_emid="+employee.getEm_id());
			if (read!=null&&"1".equals(read.toString())) {
				return ;
			}else {
				BaseUtil.showError("你没有阅读此文件的权限！");
			}
		}
	}

	@Override
	public String powerForUpload(Object id, Integer _noc) {
		_noc = _noc ==null?0:_noc;
		Employee employee = SystemSession.getUser();
		String [] ems = baseDao.getDBSettingArray("ProjectRequest", "fileOperator");
		boolean fileOperator = false;
		if (ems!=null) {
			for (String em : ems) {
				if (em!=null&&em.equals(employee.getEm_code())) {
					fileOperator = true;
					break;
				}
			}
		}
		Object [] taskid = baseDao.getFieldsDataByCondition("ProjectDoc", "pd_taskid,pd_parentid", "pd_id="+id);
		boolean bool = true;
		String result = null;
		if (taskid!=null&&taskid[0]!=null) {
			bool = baseDao.checkIf("ResourceAssignMent", "ra_taskid = "+taskid[0]+" and ra_emid = "+employee.getEm_id());
		}
		
		Object assigntocode = baseDao.getFieldDataByCondition("Project,ProjectDoc", "prj_assigntocode", "prj_id = pd_prjid and pd_id = "+id);
		if ("admin".equals(employee.getEm_type())||_noc==1||(baseDao.isDBSetting("ProjectRequest", "DefaultDocPower")
				&&assigntocode!=null&&assigntocode.equals(employee.getEm_code()))||fileOperator) {
			if (!bool) {
				result = "此文件关联任务，你不是任务负责人，不具备上传此文件附件的权限！";
			}
		}else {
			Object upload = baseDao.getFieldDataByCondition("ProjectDocPower", "pp_upload", "pp_docid="+(taskid[1]==null?-1:taskid[1])+" and pp_emid="+employee.getEm_id());
			if (upload!=null&&"1".equals(upload.toString())) {
				if (!bool) {
					result = "此文件关联任务，你不是任务负责人，不具备上传此文件附件的权限！";
				}
			}else {
				result = "你没有上传此文件的权限！";
			}
		}
		return result;
	}

	@Override
	public void powerForDown(Object id, Integer _noc) {
		_noc = _noc ==null?0:_noc;
		Employee employee = SystemSession.getUser();
		String [] ems = baseDao.getDBSettingArray("ProjectRequest", "fileOperator");
		boolean fileOperator = false;
		if (ems!=null) {
			for (String em : ems) {
				if (em!=null&&em.equals(employee.getEm_code())) {
					fileOperator = true;
					break;
				}
			}
		}
		Object assigntocode = baseDao.getFieldDataByCondition("Project,ProjectDoc", "prj_assigntocode", "prj_id = pd_prjid and pd_id = "+id);
		if ("admin".equals(employee.getEm_type())||_noc==1||(baseDao.isDBSetting("ProjectRequest", "DefaultDocPower")
				&&assigntocode!=null&&assigntocode.equals(employee.getEm_code()))||fileOperator) {
			return ;
		}else {
			Object down = baseDao.getFieldDataByCondition("ProjectDocPower", "pp_down", "pp_docid="+id+" and pp_emid="+employee.getEm_id());
			if (down!=null&&"1".equals(down.toString())) {
				return ;
			}else {
				BaseUtil.showError("你没有下载此文件的权限！");
			}
		}
	}
	
	@Override
	public List<Map<String, Object>> getFilePowers(Integer docid, Integer prjid) {
		List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		//获取团队内成员信息
		SqlRowList rs = baseDao.queryForRowSet("select a.*,b.* from (select * from teammember where TM_PRJID=?)a "
				+ "left join (select * from projectdocpower left join employee on em_id=pp_emid where pp_docid=? and pp_emid<>0 )b on a.tm_employeeid =b.pp_emid",prjid,docid);
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("pp_id", rs.getGeneralInt("pp_id"));
			map.put("pp_emid",rs.getGeneralInt("tm_employeeid"));
			map.put("tm_employeename",rs.getString("tm_employeename"));
			map.put("tm_functional", rs.getString("tm_functional")==null?" ":rs.getString("tm_functional"));
			map.put("pp_manage", rs.getGeneralInt("pp_manage")==1);
			map.put("pp_scan", rs.getGeneralInt("pp_scan")==1);
			map.put("pp_upload", rs.getGeneralInt("pp_upload")==1);
			map.put("pp_down", rs.getGeneralInt("pp_down")==1);
			map.put("pp_read", rs.getGeneralInt("pp_read")==1);
			data.add(map);
		}
		//获取团队外成员信息
		SqlRowList row = baseDao.queryForRowSet("select * from projectdocpower left join employee on em_id=pp_emid where pp_docid=? and pp_emid<>0 "
				+ "and not exists (select 1 from teammember where tm_prjid=? and tm_employeeid=pp_emid)",docid,prjid);
		while (row.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("pp_id", row.getGeneralInt("pp_id"));
			map.put("pp_emid",row.getGeneralInt("pp_emid"));
			map.put("tm_employeename",row.getString("em_name"));
			map.put("tm_functional", "团队外成员");
			map.put("pp_manage", row.getGeneralInt("pp_manage")==1);
			map.put("pp_scan", row.getGeneralInt("pp_scan")==1);
			map.put("pp_upload", row.getGeneralInt("pp_upload")==1);
			map.put("pp_down", row.getGeneralInt("pp_down")==1);
			map.put("pp_read", row.getGeneralInt("pp_read")==1);
			data.add(map);
		}
		
		return data;
	}

	@Override
	public void saveFilePowers(Boolean appyforChilds,List<Map<Object, Object>> store) {
		List<Map<Object, Object>> list = new ArrayList<Map<Object,Object>>();
		List<String> sqls =new ArrayList<String>();
		try {
			for (Map<Object, Object> map : store) {
				
				//是否应用到子级目录
				if (appyforChilds) {
					SqlRowList rs = baseDao.queryForRowSet("select * from (select pd_id,pd_kind from ProjectDoc start with pd_parentid=? "
							+ "connect by prior pd_id =  pd_parentid) left join (select * from ProjectDocPower where pp_emid = ?) "
							+ "on pd_id = pp_docid where pd_kind =-1",map.get("pp_docid"),map.get("pp_emid"));
					while (rs.next()) {
							Map<Object, Object> m = new HashMap<Object, Object>();
							m.put("pp_id", rs.getGeneralInt("pp_id"));
							m.put("pp_emid", map.get("pp_emid"));
							m.put("pp_docid", rs.getGeneralInt("pd_id"));
							m.put("pp_manage", map.get("pp_manage"));
							m.put("pp_scan", map.get("pp_scan"));
							m.put("pp_read", map.get("pp_read"));
							m.put("pp_upload", map.get("pp_upload"));
							m.put("pp_down", map.get("pp_down"));
							list.add(m);
					}
				}
				
			}
			sqls.addAll(SqlUtil.getInsertOrUpdateSql(list, "ProjectDocPower", "pp_id"));
			sqls.addAll(SqlUtil.getInsertOrUpdateSql(store, "ProjectDocPower", "pp_id"));
			sqls.add("delete ProjectDocPower where pp_manage=0 and pp_scan=0 and pp_upload=0 and pp_read=0 and pp_down=0");
			baseDao.execute(sqls);
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("保存失败，错误："+e.getMessage());
		}
	}

	@Override
	public List<Object[]>  getFileList(Boolean superPower,String condition, boolean canRead) {
		List<Object[]> datas = new ArrayList<Object[]>();
		if (!superPower) {
			List<Object> set = new ArrayList<Object>();
			Employee employee = SystemSession.getUser();
			if (!canRead) {
				condition += " and pp_scan = 1 and pp_emid = "+employee.getEm_id();
			}
			
			List<Object> pps =  baseDao.getFieldDatasByCondition("projectdoc left join ProjectDocPower on pd_id = pp_docid", "pd_id", condition);
			for (Object pp : pps) {
				String table = "(select * from ProjectDoc start with pd_id="+pp+" connect by pd_id = prior pd_parentid) "
						+ "left join (select pp_docid,pp_manage from ProjectDocPower where pp_emid = "+employee.getEm_id()+") on pd_id = pp_docid";
				List<Object[]> treeList = baseDao.getFieldsDatasByCondition(table, new String[]{"pd_id","pd_kind","pd_prjid","pd_parentid",
						"pd_name","pd_remark","pd_virtualpath","pd_detno","pd_code","pp_manage"}, "1=1 order by pd_detno");
				for (Object[] tree : treeList) {
					if (set.contains(tree[0])) {
						continue;
					}
					set.add(tree[0]);
					datas.add(tree);
				}
			}
		}else {
			datas = baseDao.getFieldsDatasByCondition("projectdoc", new String[]{"pd_id","pd_kind","pd_prjid","pd_parentid","pd_name","pd_remark","pd_virtualpath","pd_detno","pd_code"}, "pd_kind=-1 and " + condition + " order by pd_detno");
		}
		return datas;
	}
	
	public void powerForAddRoot(Object prjid,Integer _noc){
		_noc = _noc ==null?0:_noc;
		Employee employee = SystemSession.getUser();
		String [] ems = baseDao.getDBSettingArray("ProjectRequest", "fileOperator");
		boolean fileOperator = false;
		if (ems!=null) {
			for (String em : ems) {
				if (em!=null&&em.equals(employee.getEm_code())) {
					fileOperator = true;
					break;
				}
			}
		}
		Object assigntocode = baseDao.getFieldDataByCondition("Project", "prj_assigntocode", "prj_id = "+prjid);
		if ("admin".equals(employee.getEm_type())||_noc==1||(baseDao.isDBSetting("ProjectRequest", "DefaultDocPower")
				&&assigntocode!=null&&assigntocode.equals(employee.getEm_code()))||fileOperator) {
			return ;
		}else {
			BaseUtil.showError("你不是管理员或未配置为项目负责人，没有新增一级目录的权限！");
		}
		
	}

	@Override
	public List<Object[]> getFileList(Boolean superPower, String formCondition,
			String condition, String search) {
		List<Object[]> datas = new ArrayList<Object[]>();
		
		if (superPower) {
			//分页
			datas = baseDao.getFieldsDatasByCondition("(select a.*,rownum rn from (select * from projectdoc "
					+ "left join documentlist on dl_prjdocid=pd_id where pd_kind=0 and pd_name like '%"+search+"%' and "+formCondition+" order by pd_detno) a)", 
					new String[]{"pd_id","pd_kind","pd_prjid","pd_parentid","pd_name","pd_remark","pd_virtualpath","pd_detno","pd_code","pd_filepath","dl_version","dl_createtime"}, condition);
		}else {
			Employee employee = SystemSession.getUser();
			//分页
			datas = baseDao.getFieldsDatasByCondition("(select a.*,rownum rn from (select * from projectdoc "
					+ "left join documentlist on dl_prjdocid=pd_id inner join ProjectDocPower on pd_parentid = pp_docid where pd_kind=0 and pd_name like "
					+ "'%"+search+"%' and "+formCondition+" and pp_scan=1 and pp_emid="+employee.getEm_id()+" order by pd_detno) a)", new String[]{"pd_id","pd_kind","pd_prjid","pd_parentid",
					"pd_name","pd_remark","pd_virtualpath","pd_detno","pd_code","pd_filepath","dl_version","dl_createtime"}, condition);
		}
		
		return datas;
	}

	@Override
	public Object getCountFile(Boolean superPower, String formCondition,
			String search) {
		Object total = 0;
		if (superPower) {
			total = baseDao.getFieldDataByCondition("projectdoc", "count(1)", "pd_kind=0 and "+formCondition+" and pd_name like '%"+search+"%'");
		}else {
			Employee employee = SystemSession.getUser();
			total = baseDao.getFieldDataByCondition("projectdoc inner join ProjectDocPower on pd_parentid=pp_docid", "count(1)", "pd_kind=0 and "+formCondition+" and pd_name like '%"+search+"%' and pp_scan=1 and pp_emid="+employee.getEm_id());
		}
		
		return total;
	}

}
