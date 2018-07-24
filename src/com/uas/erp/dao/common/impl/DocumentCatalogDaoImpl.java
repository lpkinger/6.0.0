package com.uas.erp.dao.common.impl;

import java.io.File;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.lob.OracleLobHandler;
import org.springframework.jdbc.support.nativejdbc.CommonsDbcpNativeJdbcExtractor;
import org.springframework.stereotype.Repository;

import com.uas.erp.core.ContextUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.DocumentCatalogDao;
import com.uas.erp.dao.common.HrJobDao;
import com.uas.erp.model.DocumentCatalog;
import com.uas.erp.model.Employee;

@Repository("documentcatalogDao")
public class DocumentCatalogDaoImpl extends BaseDao implements DocumentCatalogDao {

	// @Autowired
	// private HrJobDao hrJoDao;

	@SuppressWarnings("unchecked")
	@Override
	public List<DocumentCatalog> getDocumentCatalogs() {
		return (List<DocumentCatalog>) getAll("DocumentCatalog", DocumentCatalog.class);
	}

	@Override
	public List<DocumentCatalog> getDocumentCatalogsByParentId(int parentId) {
		try {
			List<DocumentCatalog> dcs = getJdbcTemplate().query("select * from documentcatalog where dc_parentid=? order by dc_Id ",
					new BeanPropertyRowMapper<DocumentCatalog>(DocumentCatalog.class), parentId);
			return dcs;
		} catch (EmptyResultDataAccessException exception) {
			return null;
		}
	}

	@Override
	public List<DocumentCatalog> getDocumentCatalogsBySearch(String search) {
		try {
			StringBuffer sb = new StringBuffer();
			String[] names = null;
			String where = "";
			if (search.contains("&&")) {
				names = search.split("&&");
				for (String name : names) {
					sb.append(" Dc_displayname LIKE '%" + name + "%' AND ");
				}
				where = sb.substring(0, sb.lastIndexOf("AND"));
			} else if (search.contains("##")) {
				names = search.split("##");
				for (String name : names) {
					sb.append(" Dc_displayname LIKE '%" + name + "%' OR ");
				}
				where = sb.substring(0, sb.lastIndexOf("OR"));
			} else {
				where = " Dc_displayname LIKE '%" + search + "%'";
			}
			List<DocumentCatalog> dcs = getJdbcTemplate().query("SELECT * FROM documentcatalog WHERE " + where,
					new BeanPropertyRowMapper<DocumentCatalog>(DocumentCatalog.class));
			List<DocumentCatalog> list = dcs;
			for (int i = 0; i < dcs.size(); i++) {
				DocumentCatalog dc = dcs.get(i);
				while (dc.getDc_ParentId() != 0) {
					// 把它的父节点也找出来
					dc = getJdbcTemplate().queryForObject("select * from documentcatalog where Dc_Id=?",
							new BeanPropertyRowMapper<DocumentCatalog>(DocumentCatalog.class), dc.getDc_ParentId());
					boolean bool = true;
					for (DocumentCatalog ss : list) {// 父节点可能已经在之前加进来了
						if (ss.getDc_Id() == dc.getDc_Id()) {
							bool = false;
							break;
						}
					}
					if (bool)
						list.add(dc);
				}
			}
			return list;
		} catch (EmptyResultDataAccessException exception) {
			return null;
		}
	}

	// @Override
	// public List<DocumentCatalog> getDocumentCatalogsById(int id) {
	// List<DocumentCatalog> list = new ArrayList<DocumentCatalog>();
	// @SuppressWarnings({ "unchecked", "rawtypes" })
	// DocumentCatalog dc = getJdbcTemplate().queryForObject(
	// "select * from documentcatalog where dc_id=? ", new
	// BeanPropertyRowMapper(DocumentCatalog.class),id);
	// list.add(dc);
	// if (dc.getDc_ParentId() != 0) {
	// getDocumentCatalogsById(dc.getDc_ParentId());
	// }
	// return list;
	// }

	public String getPathById(int id) {
		DocumentCatalog dc = getJdbcTemplate().queryForObject("select * from documentcatalog where dc_id=? ",
				new BeanPropertyRowMapper<DocumentCatalog>(DocumentCatalog.class), id);
		if (dc.getDc_ParentId() == 0) {
			return dc.getDc_DisplayName();
		} else {
			return getPathById(dc.getDc_ParentId()) + File.separator + dc.getDc_DisplayName();
		}
	}

	@Override
	public void insertDocumentCatalog(DocumentCatalog dc, Employee employee) {
		try {
			int id = getSeqId("DOCUMENTCA_SEQ");
			final OracleLobHandler lobHandler = new OracleLobHandler();
			CommonsDbcpNativeJdbcExtractor extractor = new CommonsDbcpNativeJdbcExtractor();
			lobHandler.setNativeJdbcExtractor(extractor);
			String sql = "INSERT INTO documentcatalog(dc_id,dc_displayname,dc_parentid,dc_url,dc_tabtitle,dc_isfile,"
					+ "dc_deleteable,dc_updatetime,dc_filesize,dc_version,dc_creator,dc_creator_id) " + "values("
					+ id
					+ ",'"
					+ dc.getDc_DisplayName()
					+ "',"
					+ dc.getDc_ParentId()
					+ ",'"
					+ dc.getDc_Url()
					+ "','"
					+ dc.getDc_DisplayName()
					+ "','"
					+ dc.getDc_isfile()
					+ "','"
					+ dc.getDc_deleteable()
					+ "','"
					+ dc.getDc_updatetime()
					+ "','"
					+ dc.getDc_filesize()
					+ "','"
					+ dc.getDc_version()
					+ "','"
					+ dc.getDc_creator()
					+ "','"
					+ dc.getDc_creator_id()
					+ "')";
			getJdbcTemplate().execute(sql);
			String sql2 = "INSERT INTO documentpower(dcp_id,dcp_powername,dcp_parentid,dcp_isleaf)" + " values(" + id + ",'"
					+ dc.getDc_DisplayName() + "'," + dc.getDc_ParentId() + ",'" + dc.getDc_isfile() + "')";
			getJdbcTemplate().execute(sql2);
			int dpp_id = getSeqId("DOCUMENTPOSITIONPOWER_SEQ");
			HrJobDao hrJoDao = (HrJobDao) ContextUtil.getBean("hrJoDao");
			String sql3 = "INSERT INTO documentpositionpower(dpp_id,dpp_add,dpp_delete,dpp_update,"
					+ "dpp_download,dpp_upload,dpp_joid,dpp_dcpid)" + " values(" + dpp_id + "," + 1 + "," + 1 + "," + 1 + "," + 1 + "," + 1
					+ "," + hrJoDao.getJoIdByEmId(employee.getEm_id()) + "," + id + ")";
			getJdbcTemplate().execute(sql3);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public DocumentCatalog getDocumentCatalogById(int id) {
		DocumentCatalog dc = getJdbcTemplate().queryForObject("select * from documentcatalog where Dc_Id=?",
				new BeanPropertyRowMapper<DocumentCatalog>(DocumentCatalog.class), id);
		return dc;
	}

	@Override
	public void deleteByVersion(int dcl_number, int dc_ParentId) {
		String sql = "delete from documentcatalog where dc_parentid = " + dc_ParentId + " and dc_version like '" + dcl_number + ".%'";
		getJdbcTemplate().execute(sql);
	}
	// @Override
	// public List<DocumentCatalog> getFileListById(int id) {
	// List<DocumentCatalog> list = new ArrayList<DocumentCatalog>();
	// try{
	// @SuppressWarnings({ "unchecked", "rawtypes" })
	// List<DocumentCatalog> dcs = getJdbcTemplate().query(
	// "select * from documentcatalog where dc_parentid=? order by dc_Id ", new
	// BeanPropertyRowMapper(DocumentCatalog.class),id);
	// for (DocumentCatalog dc : dcs) {
	// if (dc.getDc_isfile() == "F") {
	// getFileListById(dc.getDc_Id());
	// } else {
	// list.add(dc);
	// }
	//
	// }
	// return list;
	// } catch(EmptyResultDataAccessException exception){
	// return null;
	// }
	// }

}
