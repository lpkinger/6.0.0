package com.uas.erp.dao.common.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.OracleLobHandler;
import org.springframework.jdbc.support.nativejdbc.CommonsDbcpNativeJdbcExtractor;
import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.FeedbackDao;
import com.uas.erp.model.Employee;


@Repository
public class FeedbackDaoImpl extends BaseDao implements FeedbackDao {
	static final String TURNBUGLIST = "SELECT * from feedback WHERE fb_id=?" ;
	/*static final String TURNBUGLISTDETAIL = "SELECT sa_code,sa_custid,sa_custcode,sa_sellerid,sa_seller,sa_currency,sa_rate,sa_cop" + 
			",sa_paymentsid,sa_toplace,sa_pocode,sa_plandelivery,sa_custname,sa_payments,sa_kind,sa_transport,sa_salemethod"+
			",sa_shcustcode,sa_shcustname,sa_apcustcode,sa_apcustname FROM sale WHERE sa_id=?";*/
	static final String INSERTBUGLIST = "insert into checklist(cl_id,cl_code,cl_recorder,cl_recorddate,cl_description,cl_pmman,cl_pmmancode,cl_recorderid,cl_custname,cl_custuu,cl_statuscode,cl_status)values(?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String INSERTBUGLISTDETAIL = "insert into checklistdetail(cld_id,cld_clid,cld_detno,cld_name,cld_needlevel,cld_newtestmanid,cld_newtestman,cld_statuscode,cld_sourceid,cld_sourcecode)values(?,?,?,?,?,?,?,?,?,?)";
	@Override
	public void replyCommetnt(final int id, final String comment, String sendname,Employee employee) {
		try {
			  final OracleLobHandler lobHandler = new OracleLobHandler();
			  CommonsDbcpNativeJdbcExtractor extractor = new CommonsDbcpNativeJdbcExtractor();
			  lobHandler.setNativeJdbcExtractor(extractor);
			  String sql = "update Feedback set fb_uasdetail=?,fb_sendstatus='待上传',fb_email='"+employee.getEm_name()+"' where fb_id=?";				       
			  getJdbcTemplate().execute(sql, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
					@Override
					protected void setValues(PreparedStatement ps, LobCreator lob) throws SQLException,
							DataAccessException {
						lob.setClobAsString(ps,1,comment);//String转化成Clob						
						ps.setInt(2,id);
					}
			  });
			  Object pr_code = getFieldDataByCondition("feedback", "fb_code","fb_id="+id);
			  if(pr_code!= null){
				  updateByCondition("ProjectTask", "handstatus='已结束'", "sourcecode='"+pr_code+"'");
			  }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int turnBuglist(int id, String language, Employee employee) {
		try {		
			SqlRowList rs = queryForRowSet(TURNBUGLIST, new Object[]{id});
			int snid = 0;
			if(rs.next()){
				snid = getSeqId("CHECKLIST_SEQ");
				String code ="BL_"+ sGetMaxNumber("CheckList", 2);
				
				String sourcecode = rs.getString(1);
				boolean bool = execute(INSERTBUGLIST, new Object[]{snid,code,employee.getEm_name(),Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)),rs.getObject("fb_detail"),employee.getEm_name(),employee.getEm_code(),
						employee.getEm_id(),rs.getObject("fb_enname"),rs.getObject("fb_enid"),"ENTERING","在录入"});
				if(bool){
					rs = queryForRowSet(TURNBUGLIST, new Object[]{id});
					int count = 1;
					if(rs.next()){						
							int sndid = getSeqId("CHECKLISTDETAIL_SEQ");
						bool=execute(INSERTBUGLISTDETAIL, new Object[]{sndid,snid,count++,rs.getString("fb_module")+rs.getString("fb_ywcode"),rs.getString("fb_urgent"),employee.getEm_id(),employee.getEm_name(),"PENDING",id,sourcecode});						
					}
				}
			}
			return snid;
		} catch (Exception e){
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
			return 0;
		}
	}

}
