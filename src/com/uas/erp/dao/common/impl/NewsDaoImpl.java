package com.uas.erp.dao.common.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.OracleLobHandler;
import org.springframework.jdbc.support.nativejdbc.CommonsDbcpNativeJdbcExtractor;
import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.NewsDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.model.News;
import com.uas.erp.model.NewsComment;

@SuppressWarnings("deprecation")
@Repository
public class NewsDaoImpl extends BaseDao implements NewsDao {
	//
	final static String GET_NEWS = "SELECT ne_releaser,ne_theme,ne_releasedate,ne_type,ne_code,ne_content,ne_browsenumber,ne_feel,ne_istop,ne_attachs FROM news WHERE ne_id=?";
	final static String NEWS_PREV = "select * from news where ne_id=(select c.p from (select ne_id,lag(ne_id,1,0)  over (order by ne_id) as p from news) c where c.ne_id=?)";
	final static String NEWS_NEXT = "select * from news where ne_id=(select c.p from (select ne_id,lead(ne_id,1,0)  over (order by ne_id) as p from news) c where c.ne_id=?)";

	@Override
	public void saveNews(final News news, final Employee employee,final String attachs) {
		try {
			final OracleLobHandler lobHandler = new OracleLobHandler();
			CommonsDbcpNativeJdbcExtractor extractor = new CommonsDbcpNativeJdbcExtractor();
			lobHandler.setNativeJdbcExtractor(extractor);
			final List<String> Sqls = new ArrayList<String>();
			//String isGroup = BaseUtil.getXmlSetting("group");
			final Master master = employee.getCurrentMaster();
/*			if ("true".equals(isGroup)) {
				String masoncode = master.getMa_soncode();
				if (master != null && master.getMa_type() != 3 && masoncode != null) {
					for (String sob : masoncode.split(",")) {
						Sqls.add("INSERT INTO "
								+ sob
								+ ".News(ne_releaser,ne_theme,ne_type,ne_code,ne_content,ne_browsenumber,ne_id,ne_istop,ne_attachs)"
								+ " VALUES(?,?,?,?,?,?,?,?,?)");
					}
				}
			} else*/
				Sqls.add("INSERT INTO News(ne_releaser,ne_theme,ne_type,ne_code,ne_content,ne_browsenumber,ne_id,ne_istop,ne_attachs)"
						+ " VALUES(?,?,?,?,?,?,?,?,?)");
			for (int i = 0; i < Sqls.size(); i++) {
				getJdbcTemplate().execute(Sqls.get(i), new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
					@Override
					protected void setValues(PreparedStatement ps, LobCreator lob) throws SQLException,
							DataAccessException {
						ps.setString(1, news.getNe_releaser());
						if (Sqls.size() > 1) {
							ps.setString(2, news.getNe_theme() + "-" + master.getMa_function());
						} else
							ps.setString(2, news.getNe_theme());

						// java.sql.Date date=new
						// java.sql.Date(BaseUtil.parseStringToDate(news.getNe_releasedate(),
						// "yyyy-MM-dd HH:mm:ss").getTime());
						ps.setString(3, news.getNe_type());
						ps.setString(4, news.getNe_code());
						lob.setClobAsString(ps, 5, news.getNe_content());// String转化成Clob
						ps.setInt(6, 0);
						ps.setInt(7, news.getNe_id());
						ps.setInt(8, news.getNe_istop());
						ps.setString(9, attachs);
					}

				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized News getNews(final int ne_id) {
		try {
			return getJdbcTemplate().execute(new CallableStatementCreator() {
				@Override
				public CallableStatement createCallableStatement(Connection conn) throws SQLException {
					CallableStatement cs = conn.prepareCall(GET_NEWS);
					cs.setInt(1, ne_id);
					return cs;
				}
			}, new CallableStatementCallback<News>() {
				@Override
				public News doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
					cs.execute();
					ResultSet rs = cs.getResultSet();
					News news = null;
					if (rs.next()) {
						news = new News();
						news.setNe_browsenumber(rs.getInt("ne_browsenumber") + 1);
						news.setNe_code(rs.getString("ne_code"));
						final OracleLobHandler lobHandler = new OracleLobHandler();
						news.setNe_content(lobHandler.getClobAsString(rs, "ne_content"));
						news.setNe_id(ne_id);
						news.setNe_releasedate(rs.getTimestamp("ne_releasedate"));
						news.setNe_releaser(rs.getString("ne_releaser"));
						news.setNe_theme(rs.getString("ne_theme"));
						news.setNe_type(rs.getString("ne_type"));
						news.setNe_feel(rs.getString("ne_feel"));
						news.setNe_istop(rs.getInt("ne_istop"));
						String attachs = rs.getString("ne_attachs");
						String condition = "(";
						List<String> attachwithnames = new ArrayList<String>();
						if (StringUtil.hasText(attachs) && attachs.contains(";")) {
							for (int i = 0; i <= attachs.split(";").length - 1; i++) {
								condition += "'" + attachs.split(";")[i] + "',";
							}
							condition = condition.substring(0, condition.length() - 1) + ")";
							SqlRowList sl = queryForRowSet("select fp_id ,fp_name from filepath where fp_id in " + condition);
							while (sl.next()) {
								attachwithnames.add(sl.getInt("fp_id") + "#" + sl.getString("fp_name"));
							}
						}
						news.setNe_attachs(attachwithnames);
						updateByCondition("News", "ne_browsenumber=ne_browsenumber+1", "ne_id=" + ne_id);// 浏览次数++
						List<NewsComment> comments = getJdbcTemplate().query(
								"Select * from  NewsComment where nc_neid=? order by nc_id asc",
								new BeanPropertyRowMapper<NewsComment>(NewsComment.class), ne_id);
						news.setComments(comments);
						News prevnews = null;
						News nextnews = null;
						try {
							prevnews = getJdbcTemplate().queryForObject(NEWS_PREV, new BeanPropertyRowMapper<News>(News.class), ne_id);
						} catch (EmptyResultDataAccessException e) {

						} catch (Exception e) {

						}
						try {
							nextnews = getJdbcTemplate().queryForObject(NEWS_NEXT, new BeanPropertyRowMapper<News>(News.class), ne_id);
						} catch (EmptyResultDataAccessException e) {

						} catch (Exception e) {

						}
						news.setPrevNews(prevnews);
						news.setNextNews(nextnews);
					}
					return news;
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public List<News> getNews(int page, int pageSize) {
		try {
			return getJdbcTemplate().query("select * from (select ne_id,ne_releaser,ne_theme,ne_releasedate,substr(regexp_replace(replace(ne_content,chr(10),''), '<\\/?.+?>'), 1, 50) ne_content,rownum r from (select ne_id,ne_releaser,ne_theme,ne_releasedate,ne_content from news order by ne_releasedate desc) T where rownum < ?) where r > ?",
					new BeanPropertyRowMapper<News>(News.class), page*pageSize + 1, (page - 1) * pageSize);
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
