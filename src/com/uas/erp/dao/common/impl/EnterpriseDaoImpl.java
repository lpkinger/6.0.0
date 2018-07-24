package com.uas.erp.dao.common.impl;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.EnterpriseDao;
import com.uas.erp.model.Enterprise;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.Master;

@Repository("enterpriseDao")
public class EnterpriseDaoImpl extends BaseDao implements EnterpriseDao {

	@Override
	public Enterprise getEnterpriseByName(String name) {
		String sql = "select * from Enterprise where en_Name=?";
		return getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<Enterprise>(Enterprise.class), new Object[] { name });
	}

	@Override
	public void saveEnterprise(Enterprise enterprise) {
		super.saveAndReturnKey(enterprise);
	}

	@Override
	public Enterprise getEnterpriseByEnUU(int en_uu) {
		String sql = "select * from Enterprise where en_uu=?";
		return getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<Enterprise>(Enterprise.class), new Object[] { en_uu });
	}

	@Override
	public Enterprise getEnterpriseById(int id) {
		try {
			String sql = "select * from Enterprise where en_Id=?";
			return getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<Enterprise>(Enterprise.class), new Object[] { id });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	@Cacheable(value = "master")
	public List<Master> getMasters() {
		String sob = SpObserver.getSp();
		SpObserver.putSp(BaseUtil.getXmlSetting("defaultSob"));
		try {
			return getJdbcTemplate().query("SELECT * FROM master order by ma_id", new BeanPropertyRowMapper<Master>(Master.class));
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			SpObserver.putSp(sob);
		}
	}

	@SuppressWarnings("finally")
	@Override
	public Master getMasterByName(String dbname) {
		String sob = SpObserver.getSp();
		SpObserver.putSp(BaseUtil.getXmlSetting("defaultSob"));
		Master master = null;
		try {
			master = getJdbcTemplate().queryForObject("SELECT * FROM master where ma_user='" + dbname + "'",
					new BeanPropertyRowMapper<Master>(Master.class));
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SpObserver.putSp(sob);
			return master;
		}
	}

	@Override
	public List<JSONTree> getMastersTree(Integer pid) {
		return parseMasters(this.getMasters(pid));
	}

	private List<JSONTree> parseMasters(List<Master> masters) {
		if (masters == null)
			return null;
		List<JSONTree> tree = new ArrayList<JSONTree>();
		for (Master master : masters) {
			JSONTree node = new JSONTree(master);
			if (!node.isLeaf()) {
				node.setChildren(parseMasters(master.getChildren()));
			}
			tree.add(node);
		}
		return tree;
	}

	private List<Master> getMasters(Integer pid) {
		try {
			List<Master> masters = getJdbcTemplate().query("SELECT * FROM master where ma_pid=? and ma_enable=1 order by ma_id",
					new BeanPropertyRowMapper<Master>(Master.class), pid);
			for (Master master : masters) {
				if (master.getMa_type() == 0 || master.getMa_type() == 2) {
					master.setChildren(getMasters(master.getMa_id()));
				}
			}
			return masters;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<Master> getAbleMaster() {
		try {
			return getJdbcTemplate().query("SELECT * FROM master where ma_enable=1 order by ma_id",
					new BeanPropertyRowMapper<Master>(Master.class));
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Enterprise getEnterprise() {
		try {
			String sql = "select * from Enterprise ";
			return getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<Enterprise>(Enterprise.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public Master getMasterByDomain(String domain) {
		try {
			return getJdbcTemplate().queryForObject("select * from Master where ma_domain=?",
					new BeanPropertyRowMapper<Master>(Master.class), domain);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public Blob getLogo() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT en_logo FROM enterprise");
			rs = ps.executeQuery();
			if (rs.next()) {
				Blob logo = rs.getBlob("en_logo");
				return logo;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ps != null)
				try {
					ps.close();
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return null;
	}

}
