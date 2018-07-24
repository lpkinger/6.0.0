package com.uas.erp.dao.common.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.OracleLobHandler;
import org.springframework.jdbc.support.nativejdbc.CommonsDbcpNativeJdbcExtractor;
import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.MailDao;
import com.uas.erp.model.CalcDay;
import com.uas.erp.model.Mail;

@Repository
public class MailDaoImpl extends BaseDao implements MailDao{

	@Override
	public void saveNewReceMail(List<Mail> mails) {
		for(final Mail mail:mails){
			try {
				  final OracleLobHandler lobHandler = new OracleLobHandler();
				  CommonsDbcpNativeJdbcExtractor extractor = new CommonsDbcpNativeJdbcExtractor();
				  lobHandler.setNativeJdbcExtractor(extractor);
				  String sql = "INSERT INTO Mail(ma_uid,ma_from,ma_to,ma_subject,ma_senddate,ma_attach,ma_status,ma_context)"
					        + " VALUES(?,?,?,?,?,?,?,?)";
				  getJdbcTemplate().execute(sql, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
						@Override
						protected void setValues(PreparedStatement ps, LobCreator lob) throws SQLException,
								DataAccessException {
							ps.setString(1, mail.getMa_uid());
							ps.setString(2, mail.getMa_from());
							ps.setString(3, mail.getMa_receaddr());
							ps.setString(4, mail.getMa_subject());
							ps.setString(5, mail.getMa_senddate());
							ps.setString(6, mail.getMa_attach());
							ps.setInt(7, 1);//未读
							lob.setClobAsString(ps, 8, mail.getMa_context());
						}
				         	
				  });  
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public List<Mail> getUnReadMail(String email, int page, int pageSize) {
		List<Mail> mails = new ArrayList<Mail>();
		Mail mail = null;
		String sql = "SELECT ma_id,ma_from,ma_subject,ma_senddate";
		sql = sql + " FROM(" + sql + ",row_number()over(order by ma_id desc) rn FROM mail WHERE ma_to='" 
				+ email + "' AND ma_status=1) WHERE rn between " 
				+ ((page-1)*pageSize+1) + " and " + page*pageSize;
		SqlRowList rs = queryForRowSet(sql);//ma_status=1表示未读
		while(rs.next()){
			mail = new Mail();
			mail.setMa_id(rs.getInt("ma_id"));
			mail.setMa_from(rs.getString("ma_from"));
			String date = rs.getString("ma_senddate");
			mail.setMa_senddate(date);
			mail.setMa_subject(rs.getString("ma_subject"));
			date = date.substring(0, 10);
			try {
				mail.setGroup(CalcDay.getCalcDay(new SimpleDateFormat("yyyy-MM-dd").parse(date)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			mails.add(mail);
		}
		return mails;
	}

	@Override
	public List<Mail> getHaveReadMail(String email, int page, int pageSize) {
		List<Mail> mails = new ArrayList<Mail>();
		Mail mail = null;
		String sql = "SELECT ma_id,ma_from,ma_subject,ma_senddate";
		sql = sql + " FROM(" + sql + ",row_number()over(order by ma_id desc) rn FROM mail WHERE ma_to='" 
				+ email + "' AND ma_status=2) WHERE rn between " 
				+ ((page-1)*pageSize+1) + " and " + page*pageSize;
		SqlRowList rs = queryForRowSet(sql);//ma_status=2表示已读
		while(rs.next()){
			mail = new Mail();
			mail.setMa_id(rs.getInt("ma_id"));
			mail.setMa_from(rs.getString("ma_from"));
			String date = rs.getString("ma_senddate");
			mail.setMa_senddate(date);
			mail.setMa_subject(rs.getString("ma_subject"));
			date = date.substring(0, 10);
			try {
				mail.setGroup(CalcDay.getCalcDay(new SimpleDateFormat("yyyy-MM-dd").parse(date)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			mails.add(mail);
		}
		return mails;
	}

	@Override
	public List<Mail> getAllReceMail(String email, int page, int pageSize) {
		List<Mail> mails = new ArrayList<Mail>();
		Mail mail = null;
		String sql = "SELECT ma_id,ma_from,ma_subject,ma_senddate";
		sql = sql + " FROM(" + sql + ",row_number()over(order by ma_id desc) rn FROM mail WHERE ma_to='" 
				+ email + "' AND ma_status=1 OR ma_status=2) WHERE rn between " 
				+ ((page-1)*pageSize+1) + " and " + page*pageSize;
		SqlRowList rs = queryForRowSet(sql);//ma_status=1或2表示查看所有接收到的邮件
		while(rs.next()){
			mail = new Mail();
			mail.setMa_id(rs.getInt("ma_id"));
			mail.setMa_from(rs.getString("ma_from"));
			String date = rs.getString("ma_senddate");
			mail.setMa_senddate(date);
			mail.setMa_subject(rs.getString("ma_subject"));
			date = date.substring(0, 10);
			try {
				mail.setGroup(CalcDay.getCalcDay(new SimpleDateFormat("yyyy-MM-dd").parse(date)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			mails.add(mail);
		}
		return mails;
	}

	@Override
	public List<Mail> getDeletedReceMail(String email, int page, int pageSize) {
		List<Mail> mails = new ArrayList<Mail>();
		Mail mail = null;
		String sql = "SELECT ma_id,ma_from,ma_subject,ma_senddate";
		sql = sql + " FROM(" + sql + ",row_number()over(order by ma_id desc) rn FROM mail WHERE ma_to='" 
				+ email + "' AND ma_status=3) WHERE rn between " 
				+ ((page-1)*pageSize+1) + " and " + page*pageSize;
		SqlRowList rs = queryForRowSet(sql);//ma_status=3表示回收站邮件(收件)
		while(rs.next()){
			mail = new Mail();
			mail.setMa_id(rs.getInt("ma_id"));
			mail.setMa_from(rs.getString("ma_from"));
			String date = rs.getString("ma_senddate");
			mail.setMa_senddate(date);
			mail.setMa_subject(rs.getString("ma_subject"));
			date = date.substring(0, 10);
			try {
				mail.setGroup(CalcDay.getCalcDay(new SimpleDateFormat("yyyy-MM-dd").parse(date)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			mails.add(mail);
		}
		return mails;
	}

	@Override
	public List<Mail> getDeletedPostMail(String email, int page, int pageSize) {
		List<Mail> mails = new ArrayList<Mail>();
		Mail mail = null;
		String sql = "SELECT ma_id,ma_to,ma_subject,ma_senddate";
		sql = sql + " FROM(" + sql + ",row_number()over(order by ma_id desc) rn FROM mail WHERE ma_from='" 
				+ email + "' AND ma_status=3) WHERE rn between " 
				+ ((page-1)*pageSize+1) + " and " + page*pageSize;
		SqlRowList rs = queryForRowSet(sql);//ma_status=3表示回收站邮件(发件)
		while(rs.next()){
			mail = new Mail();
			mail.setMa_id(rs.getInt("ma_id"));
			mail.setMa_receaddr(rs.getString("ma_to"));
			String date = rs.getString("ma_senddate");
			mail.setMa_senddate(date);
			mail.setMa_subject(rs.getString("ma_subject"));
			date = date.substring(0, 10);
			try {
				mail.setGroup(CalcDay.getCalcDay(new SimpleDateFormat("yyyy-MM-dd").parse(date)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			mails.add(mail);
		}
		return mails;
	}
	
	@Override
	public List<Mail> getPostedMail(String email, int page, int pageSize) {
		List<Mail> mails = new ArrayList<Mail>();
		Mail mail = null;
		String sql = "SELECT ma_id,ma_to,ma_subject,ma_senddate";
		sql = sql + " FROM(" + sql + ",row_number()over(order by ma_id desc) rn FROM mail WHERE ma_from='" 
				+ email + "' AND ma_status=4) WHERE rn between " 
				+ ((page-1)*pageSize+1) + " and " + page*pageSize;
		SqlRowList rs = queryForRowSet(sql);//ma_status=4表示已发送
		while(rs.next()){
			mail = new Mail();
			mail.setMa_id(rs.getInt("ma_id"));
			mail.setMa_receaddr(rs.getString("ma_to"));
			String date = rs.getString("ma_senddate");
			mail.setMa_senddate(date);
			mail.setMa_subject(rs.getString("ma_subject"));
			date = date.substring(0, 10);
			try {
				mail.setGroup(CalcDay.getCalcDay(new SimpleDateFormat("yyyy-MM-dd").parse(date)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			mails.add(mail);
		}
		return mails;
	}

	@Override
	public List<Mail> getDraftMail(String email, int page, int pageSize) {
		List<Mail> mails = new ArrayList<Mail>();
		Mail mail = null;
		String sql = "SELECT ma_id,ma_to,ma_subject,ma_senddate";
		sql = sql + " FROM(" + sql + ",row_number()over(order by ma_id desc) rn FROM mail WHERE ma_from='" 
				+ email + "' AND ma_status=5) WHERE rn between " 
				+ ((page-1)*pageSize+1) + " and " + page*pageSize;
		SqlRowList rs = queryForRowSet(sql);//ma_status=5表示草稿
		while(rs.next()){
			mail = new Mail();
			mail.setMa_id(rs.getInt("ma_id"));
			mail.setMa_receaddr(rs.getString("ma_to"));
			String date = rs.getString("ma_senddate");
			mail.setMa_senddate(date);
			mail.setMa_subject(rs.getString("ma_subject"));
			date = date.substring(0, 10);
			try {
				mail.setGroup(CalcDay.getCalcDay(new SimpleDateFormat("yyyy-MM-dd").parse(date)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			mails.add(mail);
		}
		return mails;
	}

	@Override
	public Mail getMailDetail(int id) {
		Mail mail = null;
		Connection conn=null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn=getConnection();
			ps = conn.prepareStatement("SELECT ma_id,ma_attach,ma_context,ma_status,ma_from,ma_to,ma_subject,ma_senddate FROM mail WHERE ma_id=" 
					+ id);
			rs = ps.executeQuery();
			if(rs.next()){
				mail = new Mail();
				mail.setMa_id(rs.getInt("ma_id"));
				mail.setMa_from(rs.getString("ma_from"));
				mail.setMa_receaddr(rs.getString("ma_to"));
				mail.setMa_senddate(rs.getString("ma_senddate"));
				mail.setMa_subject(rs.getString("ma_subject"));
				mail.setMa_attach(rs.getString("ma_attach"));
				mail.setMa_status(rs.getInt("ma_status"));
				final OracleLobHandler lobHandler = new OracleLobHandler();
				mail.setMa_context(lobHandler.getClobAsString(rs, "ma_context"));
			}
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				ps.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(mail.getMa_status() == 1){
			updateStatus(id, 2);
		}
		return mail;
	}

	@Override
	public void updateStatus(int id, int status) {
		execute("UPDATE mail SET ma_status=" + status + " WHERE ma_id=" + id);
	}
	
}
