package com.uas.erp.dao.common.impl;

import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.SynergyDao;
import com.uas.erp.model.Synergy;

@Repository
public class SynergyDaoImpl extends BaseDao implements SynergyDao {

	@Override
	public void delete(int sy_id) {
		try {
			getJdbcTemplate().execute(
					"delete from synergy where sy_id = " + sy_id);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	@Override
//	public List<Agenda> getArrangeList(int em_id, int page, int pageSize) {
////		List<Agenda> ags = new ArrayList<Agenda>();
////		Agenda ag = null;
////		String sql = "SELECT ag_id,ag_atid,ag_arrange,ag_arrange_id,ag_content,ag_end,ag_executor,ag_executor_id,ag_ismessage,ag_issecrecy," +
////				"ag_isweek,ag_predict,ag_start,ag_title,ag_type ";
////		sql = sql
////				+ " FROM("
////				+ sql
////				+ ",row_number()over(order by ag_id desc) rn FROM agenda WHERE ag_arrange_id = " + em_id + " ) WHERE rn between "
////				+ ((page - 1) * pageSize + 1) + " and " + page * pageSize;
////		SqlRowSet rs = getJdbcTemplate().queryForRowSet(sql);// ma_status=1表示未读
////		while (rs.next()) {
////			ag = new Agenda();
////			ag.setAg_id(rs.getInt("ag_id"));
////			ag.setAg_atid(rs.getInt("ag_atid"));
////			ag.setAg_arrange(rs.getString("ag_arrange"));
////			ag.setAg_arrange_id(rs.getInt("ag_arrange_id"));
////			ag.setAg_content(rs.getString("ag_content"));
////			ag.setAg_end(rs.getDate("ag_end"));
////			ag.setAg_executor(rs.getString("ag_executor"));
////			ag.setAg_executor_id(rs.getString("ag_executor_id"));
////			ag.setAg_ismessage(rs.getInt("ag_ismessage"));
////			ag.setAg_issecrecy(rs.getInt("ag_issecrecy"));
////			ag.setAg_isweek(rs.getInt("ag_isweek"));
////			ag.setAg_predict(rs.getDate("ag_predict"));
////			ag.setAg_start(rs.getDate("ag_start"));
////			ag.setAg_title(rs.getString("ag_title"));
////			ag.setAg_type(rs.getString("ag_type"));
////			ags.add(ag);
////		}
////		return ags;
//		String sql = "ag_id,ag_atid,ag_arrange,ag_arrange_id,ag_content,ag_end,ag_executor,ag_executor_id,ag_ismessage,ag_issecrecy," +
//				"ag_isweek,ag_predict,ag_start,ag_title,ag_type";
//		try {
////			return getJdbcTemplate()
////					.query("select ? from (select * ,row_number()over(order by ag_id desc) rn FROM agenda WHERE ag_arrange_id = ?) WHERE rn between ? and ?",
////							new BeanPropertyRowMapper(Agenda.class),
////							new Object[] {sql, sql, em_id, (page - 1) * pageSize + 1, page * pageSize });
//			return getJdbcTemplate("Agenda").query("select * from agenda where ag_arrange_id = ?", new BeanPropertyRowMapper(Agenda.class),em_id);
//		} catch (EmptyResultDataAccessException e) {
//			return null;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
// 
//	}
//

	@Override
	public List<Synergy> getByCondition(String condition, int page, int pageSize) {
		try {
			return getJdbcTemplate().query("select * from synergy where "+condition, new BeanPropertyRowMapper<Synergy>(Synergy.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int getSearchCount(String condition) {
		return getCountByCondition("synergy", condition);
	}
	@Override
	public Synergy getSynergyById(int id) {
		try{
			return getJdbcTemplate().queryForObject("select * from synergy where sy_id=?",
					new BeanPropertyRowMapper<Synergy>(Synergy.class), id);			
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	@Override
	public List<Synergy> getList(int page, int pageSize) {
		try {
			return getJdbcTemplate("Synergy").
					query("select * from synergy ", 
							new BeanPropertyRowMapper<Synergy>(Synergy.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public int getListCount() {
		return getCountByTable("synergy");
	}

}
