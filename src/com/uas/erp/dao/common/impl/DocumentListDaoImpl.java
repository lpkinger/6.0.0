package com.uas.erp.dao.common.impl;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.DocumentListDao;
import com.uas.erp.model.DocumentList;
import com.uas.erp.model.Employee;
import com.uas.erp.model.EmpsJobs;
import com.uas.erp.model.JSONTree;
@Repository
public class DocumentListDaoImpl extends BaseDao implements DocumentListDao {
	@Override
	public List<JSONTree> getDocumentListByCondition(int parentId,String condition,Employee employee,String language) {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		sb.append("select * from documentList where  dl_kind=-1 AND dl_statuscode='AUDITED' and dl_parentid=?");
		if (condition != null && !"".equals(condition)) {
			sb.append(" AND ");
			sb.append(condition);
		}
		if(employee!=null){
			if (!"admin".equals(employee.getEm_type())) {
				sb.append(" and (dl_id in (select dp_dclid from documentjobpower where dp_role in(");
				StringBuffer jobIds = new StringBuffer(String.valueOf(employee.getEm_defaulthsid()));
				if(employee.getEmpsJobs() != null) {
					for (EmpsJobs empsJob : employee.getEmpsJobs()) {
						jobIds.append(",").append(empsJob.getJob_id());
					}
				}
				sb.append(jobIds).append("))");
				sb.append(" or dl_id in (select dp_dclid from documentpersonpower where dp_role=");
				sb.append(employee.getEm_id()).append(") ");
				sb.append(" or dl_id in (select dp_dclid from documentorgpower  left join HRORGEMPLOYEES on dp_role=OM_ORID where OM_EMID=");
				sb.append(employee.getEm_id()).append(")) ");
			}
		}

		if(parentId==-1){
			sb.append(" order by dl_detno desc"); //如果是项目文档，按序号降序排列
		}else{
			sb.append(" order by dl_detno");
		}
		
		try {
			List<DocumentList> documentlist = getJdbcTemplate("DocumentList").query(sb.toString(),
					new BeanPropertyRowMapper<DocumentList>(DocumentList.class), parentId);// ,
			List<JSONTree> tree = new ArrayList<JSONTree>();
			for (DocumentList doclist : documentlist) {
				tree.add(new JSONTree(doclist));
			}
			return tree;
		} catch (Exception exception) {
			exception.printStackTrace();
			return new ArrayList<JSONTree>();
		}
	}

	@Override
	public List<DocumentList> getDocumentsByCondition(int parentId,String condition, Employee employee, String language) {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		sb.append("select * from documentList where ");
		if (condition != null && !"".equals(condition)) {
			sb.append(condition);
		}
		condition=parentId==5?"1=1":"dl_parentid="+parentId;
		sb.append(" and "+condition);
		/*if (!"admin".equals(employee.getEm_type())) {
			sb.append(" and (dl_id in (select dp_dclid from documentjobpower where dp_role in(");
			StringBuffer jobIds = new StringBuffer(String.valueOf(employee.getEm_defaulthsid()));
			if(employee.getEmpsJobs() != null) {
				for (EmpsJobs empsJob : employee.getEmpsJobs()) {
					jobIds.append(",").append(empsJob.getJob_id());
				}
			}
			sb.append(jobIds).append("))");
			sb.append(" or dl_id in (select dp_dclid from documentpersonpower where dp_role=");
			sb.append(employee.getEm_id()).append(") ");
			sb.append(" or dl_id in (select dp_dclid from documentorgpower  left join HRORGEMPLOYEES on dp_role=OM_ORID where OM_EMID=");
			sb.append(employee.getEm_id()).append(")) ");
		}*/
		sb.append(" order by dl_kind ,dl_createtime desc");
		try {
			List<DocumentList> documentlist = getJdbcTemplate().query(sb.toString(),new BeanPropertyRowMapper<DocumentList>(DocumentList.class));
			return documentlist;
		} catch (EmptyResultDataAccessException exception) {
			return new ArrayList<DocumentList>();
		}
	}

}
