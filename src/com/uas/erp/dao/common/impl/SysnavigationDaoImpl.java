package com.uas.erp.dao.common.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.SysnavigationDao;
import com.uas.erp.model.Bench;
import com.uas.erp.model.CheckBoxTree;
import com.uas.erp.model.Employee;
import com.uas.erp.model.EmpsJobs;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.SysNavigation;

@Repository("sysnavigationDao")
public class SysnavigationDaoImpl extends BaseDao implements SysnavigationDao {
	
	@SuppressWarnings("unchecked")
	@Override
	public List<SysNavigation> getSysNavigations() {
		return (List<SysNavigation>) getAll("SysNavigation", SysNavigation.class);
	}

	@Override
	@Cacheable(value = "tree", key = "#employee.em_master + '#' + #parentId + '#' + #employee.em_id + #condition + #_noc + 'getJSONTreeByParentId'")
	public List<JSONTree> getJSONTreeByParentId(int parentId, String condition, Employee employee, Integer _noc) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from sysnavigation where ");
		sb.append("sn_parentid=? and sn_using=1 and case when sn_isleaf='T' then nvl(sn_url,' ') else 'T' end <> ' '");
		if (!"admin".equals(employee.getEm_type()) && _noc != 1) {
			sb.append(" and (case when sn_isleaf='F' then to_char(sn_id) else sn_caller end in(select distinct pp_caller from positionpower where nvl(pp_alllist,0)+nvl(pp_selflist,0)+nvl(pp_jobemployee,0)+nvl(pp_see,0)>0 and pp_joid in(");
			StringBuffer jobIds = new StringBuffer(String.valueOf(employee.getEm_defaulthsid()));
			if (employee.getEmpsJobs() != null) {
				for (EmpsJobs empsJob : employee.getEmpsJobs()) {
					jobIds.append(",").append(empsJob.getJob_id());
				}
			}
			sb.append(jobIds).append("))");
			sb.append(" or case when sn_isleaf='F' then to_char(sn_id) else sn_caller end in(select distinct pp_caller from personalpower where nvl(pp_alllist,0)+nvl(pp_selflist,0)+nvl(pp_jobemployee,0)+nvl(pp_see,0)>0 and pp_emid=");
			sb.append(employee.getEm_id()).append("))");
		}
		if (condition != null && !"".equals(condition)) {
			sb.append(" AND ");
			sb.append(condition);
		}
		sb.append(" order by sn_detno");
		List<JSONTree> tree = new ArrayList<JSONTree>();
		try {
			boolean bool = checkIf("Bench", "nvl(bc_used,0) = -1");
			if (bool&&(parentId==0||parentId==-999)) {
				List<JSONTree> benchTree = getBenchTree(parentId, employee, _noc);
				if (benchTree!=null&&benchTree.size()>0) {
					tree.addAll(benchTree);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if (parentId!=-999) {
			try {
				List<SysNavigation> sns = getJdbcTemplate().query(sb.toString(), new BeanPropertyRowMapper<SysNavigation>(SysNavigation.class),
						parentId);// ,
				
				for (SysNavigation navigation : sns) {
					tree.add(new JSONTree(navigation));
				}
			} catch (EmptyResultDataAccessException exception) {
				return new ArrayList<JSONTree>();
			}
		}
		return tree;
	}

	@Override
	@Cacheable(value = "tree", key = "#employee.em_master + '#' + #parentId + #condition + #language + 'getSysNavigationsByParentId'")
	public List<SysNavigation> getSysNavigationsByParentId(int parentId, String condition, Employee employee) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from sysnavigation where ");
		sb.append("sn_parentid=?");
		if (condition != null && !"".equals(condition)) {
			sb.append(" AND ");
			sb.append(condition);
		}
		sb.append(" order by sn_detno");
		try {
			List<SysNavigation> sns = getJdbcTemplate().query(sb.toString(), new BeanPropertyRowMapper<SysNavigation>(SysNavigation.class),
					parentId);// ,
			return sns;
		} catch (EmptyResultDataAccessException exception) {
			return new ArrayList<SysNavigation>();
		}
	}

	@Override
	public Set<SysNavigation> getSysNavigationsBySearch(String search, Boolean isPower) {
		try {
			StringBuffer sb = new StringBuffer();
			String[] names = null;
			String where = "";
			Employee employee = SystemSession.getUser();
			if (isPower) {
				where = "sn_limit=1 and case when sn_isleaf='T' then nvl(sn_url,' ') else ' ' end <> ' '";
			} else {
				where = "sn_using=1 and case when sn_isleaf='T' then nvl(sn_url,' ') else ' ' end <> ' '";
			}
			
			if (!"admin".equals(employee.getEm_type())) {
				if (isPower) {
					where += " and (case when sn_isleaf='T' then nvl(sn_caller,' ') else 'T' end = ' ' or case when sn_isleaf='F' then to_char(sn_id) else sn_caller end in(select distinct pp_caller from positionpower where nvl(pp_alllist,0)+nvl(pp_selflist,0)+nvl(pp_jobemployee,0)+nvl(pp_see,0)>0 and pp_joid in(";
				} else {
					where += " and (case when sn_isleaf='F' then to_char(sn_id) else sn_caller end in(select distinct pp_caller from positionpower where nvl(pp_alllist,0)+nvl(pp_selflist,0)+nvl(pp_jobemployee,0)+nvl(pp_see,0)>0 and pp_joid in(";
				}				
				StringBuffer jobIds = new StringBuffer(String.valueOf(employee.getEm_defaulthsid()));
				if (employee.getEmpsJobs() != null) {
					for (EmpsJobs empsJob : employee.getEmpsJobs()) {
						jobIds.append(",").append(empsJob.getJob_id());
					}
				}
				where += jobIds.toString();
				where += "))";
				// where += employee.getEm_defaulthsid() + ")";
				where += " or case when sn_isleaf='F' then to_char(sn_id) else sn_caller end in(select distinct pp_caller from personalpower where nvl(pp_alllist,0)+nvl(pp_selflist,0)+nvl(pp_jobemployee,0)+nvl(pp_see,0)>0 and pp_emid=";
				where += employee.getEm_id() + "))";
			}
			if (search.contains("&&")) {
				names = search.split("&&");
				for (String name : names) {
					sb.append(" AND sn_displayname LIKE '%" + name + "%' ");
				}
				where += sb.toString();
			} else if (search.contains("##")) {
				names = search.split("##");
				for (String name : names) {
					sb.append(" sn_displayname LIKE '%" + name + "%' OR ");
				}
				where += " and (" + sb.substring(0, sb.lastIndexOf("OR")) + ")";
			} else {
				where += " and sn_displayname LIKE '%" + search + "%'";
			}
		
			List<SysNavigation> sns = getJdbcTemplate().query("SELECT * FROM sysnavigation WHERE " + where,
					new BeanPropertyRowMapper<SysNavigation>(SysNavigation.class));
			Set<SysNavigation> set = new HashSet<SysNavigation>();
			for (SysNavigation sn : sns) {
				set.addAll(getNavigations(sn, null));
			}
			return set;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private Set<SysNavigation> getNavigations(SysNavigation navigation, Set<SysNavigation> set) {
		if (set == null) {
			set = new HashSet<SysNavigation>();
		}
		if (navigation.getSn_ParentId() == 0) {
			set.add(navigation);
			return set;
		}
		try {
			set.add(navigation);
			SysNavigation sn = getJdbcTemplate().queryForObject("select * from sysnavigation where sn_id=?",
					new BeanPropertyRowMapper<SysNavigation>(SysNavigation.class), navigation.getSn_ParentId());
			return getNavigations(sn, set);
		} catch (EmptyResultDataAccessException e) {
			// 非父节点， 但是却找不到父节点
			return new HashSet<SysNavigation>();
		}
	}

	@Override
	public List<JSONTree> getAllNavigation(int parentId, String condition) {
		String sql = "select * from UAS_sysnavigation where sn_show=1 and  SN_PARENTID=? order by sn_detno";
		try {
			List<SysNavigation> sns = getJdbcTemplate().query(sql, new BeanPropertyRowMapper<SysNavigation>(SysNavigation.class), parentId);
			List<JSONTree> tree = new ArrayList<JSONTree>();
			for (SysNavigation navigation : sns) {
				JSONTree treenode = new JSONTree();
				treenode.setId(navigation.getSn_Id());
				treenode.setParentId(navigation.getSn_ParentId());
				treenode.setDetno(navigation.getSn_detno());
				treenode.setCaller(navigation.getSn_caller());
				treenode.setText(navigation.getSn_DisplayName());
				treenode.setUrl(navigation.getSn_Url());
				treenode.setNum(navigation.getSn_num());
				treenode.setSvnversion(navigation.getSn_svnversion());
				treenode.setUpdateflag(navigation.getSn_updateflag());
				if (navigation.getSn_isleaf().equals("F")) {
					treenode.setLeaf(false);
					treenode.setAllowDrag(false);
					if (navigation.getSn_ParentId() == 0) {
						treenode.setCls("x-tree-cls-root");
					} else {
						treenode.setCls("x-tree-cls-parent");
					}
				} else {
					treenode.setLeaf(true);
					treenode.setAllowDrag(true);
					treenode.setCls("x-tree-cls-node");
					treenode.setIconCls(navigation.getSn_icon());
				}
				tree.add(treenode);
			}
			return tree;
		} catch (EmptyResultDataAccessException exception) {
			return new ArrayList<JSONTree>();
		}
	}

	@Override
	public Set<SysNavigation> getNavigationTreeBySearch(String search) {
		try {
			StringBuffer sb = new StringBuffer();
			String[] names = null;
			String where = "";
			if (search.contains("&&")) {
				names = search.split("&&");
				for (String name : names) {
					sb.append(" AND SN_STANDARDDESC LIKE '%" + name + "%' ");
				}
				where += sb.toString();
			} else if (search.contains("##")) {
				names = search.split("##");
				for (String name : names) {
					sb.append(" SN_STANDARDDESC LIKE '%" + name + "%' OR ");
				}
				where += " and (" + sb.substring(0, sb.lastIndexOf("OR")) + ")";
			} else {
				where += " and SN_STANDARDDESC LIKE '%" + search + "%'";
			}
			List<SysNavigation> sns = getJdbcTemplate().query("SELECT * FROM sysnavigation WHERE sn_show=1" + where,
					new BeanPropertyRowMapper<SysNavigation>(SysNavigation.class));
			Set<SysNavigation> set = new HashSet<SysNavigation>();
			for (SysNavigation sn : sns) {
				set.addAll(getNavigations(sn, null));
			}
			return set;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	@Override
	public List<CheckBoxTree> getCheckTreeByParentId(int parentId) {
		String sql="select * from sysnavigation where sn_parentid=? and sn_using=1 and case when sn_isleaf='T' "
				+ "then nvl(sn_url,' ') else 'T' end <> ' '  order by sn_detno";
		try {
			List<SysNavigation> sns = getJdbcTemplate().query(sql, new BeanPropertyRowMapper<SysNavigation>(SysNavigation.class),parentId);
			List<CheckBoxTree> tree = new ArrayList<CheckBoxTree>();
			for (SysNavigation navigation : sns) {
				tree.add(new CheckBoxTree(navigation));
			}
			return tree;
		} catch (EmptyResultDataAccessException exception) {
			return new ArrayList<CheckBoxTree>();
		}
	}
	
	private List<JSONTree> getBenchTree(int parentId, Employee employee, Integer _noc) {
		
		List<JSONTree> tree = new ArrayList<JSONTree>();
		if (parentId==0) {
			JSONTree root = new JSONTree();
			root.setCls("x-tree-cls-root");
			root.setParentId(0);
			root.setLeaf(false);
			root.setText("工作台");
			root.setQtip("工作台");
			root.setId(-999);
			root.setDetno(0);
			tree.add(root);
		}else if(parentId==-999){
			StringBuffer sb = new StringBuffer();
			sb.append("select * from bench where nvl(bc_used,0) = -1 ");
			if (!"admin".equals(employee.getEm_type()) && _noc != 1) {
				sb.append(" and EXISTS (select 1 from BENCHSCENE inner join BENCHBUSINESS on BS_BBCODE = BB_CODE where BS_BCCODE = BC_CODE "
						+ "and nvl(bs_enable,0) <> 0 and bb_statuscode <> 'DISABLE' and ((nvl(BS_CALLER,' ') <> ' ' and BS_CALLER in (select distinct pp_caller from ("
						+ "select pp_caller from positionpower where nvl(pp_alllist,0)+nvl(pp_selflist,0)+nvl(pp_jobemployee,0)+nvl(pp_see,0)>0 and pp_joid in(");
				StringBuffer jobIds = new StringBuffer(String.valueOf(employee.getEm_defaulthsid()));
				if (employee.getEmpsJobs() != null) {
					for (EmpsJobs empsJob : employee.getEmpsJobs()) {
						jobIds.append(",").append(empsJob.getJob_id());
					}
				}
				sb.append(jobIds).append(")");
				sb.append(" union select pp_caller from personalpower where nvl(pp_alllist,0)+nvl(pp_selflist,0)+nvl(pp_jobemployee,0)+nvl(pp_see,0)>0 and pp_emid=");
				sb.append(employee.getEm_id()).append("))) or (nvl(bs_caller,' ') = ' ')))");
			}
			
			sb.append(" order by bc_detno");
	
			try {
				List<Bench> benchs = getJdbcTemplate().query(sb.toString(), new BeanPropertyRowMapper<Bench>(Bench.class));
				
				for (Bench bench : benchs) {
					tree.add(new JSONTree(bench));
				}
			} catch (EmptyResultDataAccessException e) {
				return new ArrayList<JSONTree>();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return tree;
	}

}
