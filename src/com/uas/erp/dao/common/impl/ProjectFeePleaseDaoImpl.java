package com.uas.erp.dao.common.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ProjectFeePleaseDao;
import com.uas.erp.model.Employee;

@Repository
public class ProjectFeePleaseDaoImpl extends BaseDao implements ProjectFeePleaseDao{
	static final String ProjectFeePlease = "SELECT * FROM ProjectFeePlease WHERE pf_id=?";
	static final String InsertProjectFeeClaim = "INSERT INTO ProjectFeeClaim(pc_id,pc_code,pc_claimman,pc_department,pc_pleasecode," +
			"pc_pleaseamount,pc_prjid,pc_prjname,pc_status,pc_statuscode,pc_recordman,pc_recorddate)" +
			" VALUES (?,?,?,?,?,?,?,?,?,?,?,sysdate)";
	static final String ProjectFeePleaseDetail = "SELECT * FROM ProjectFeePleaseDetail WHERE pfd_pfid=?";
	static final String InsertProjectFeeClaimDetail = "INSERT INTO ProjectFeeClaimDetail(pcd_id,pcd_pcid,pcd_detno," +
			"pcd_subjectcode,pcd_subjectname,pcd_subjecttype,pcd_pleasegrade,pcd_currency,pcd_pleaseamount)" +
			" VALUES (PROJECTFEECLAIMDETAIL_SEQ.nextval,?,?,?,?,?,?,?,?)";
	@Override
	@Transactional
	public int turnProjectFeeClaim(int id) {
		try {
			SqlRowList rs = queryForRowSet(ProjectFeePlease, new Object[]{id});
			int pcid = 0;
			if(rs.next()){
				pcid = getSeqId("PROJECTFEECLAIM_SEQ");
				String code = sGetMaxNumber("ProjectFeeClaim", 2);
				Employee employee = SystemSession.getUser();
				boolean bool = execute(InsertProjectFeeClaim, new Object[]{pcid,code,rs.getObject("pf_organiger"),rs.getObject("pf_organigerdep"),
						rs.getString("pf_code"),rs.getObject("pf_amount"),rs.getObject("pf_prjid"),rs.getObject("pf_prjname"),
						BaseUtil.getLocalMessage("ENTERING"),"ENTERING",employee.getEm_name()});
				if(bool){
					rs = queryForRowSet(ProjectFeePleaseDetail, new Object[]{id});
					int count = 1;
					while(rs.next()){
						execute(InsertProjectFeeClaimDetail, new Object[]{pcid,count++,rs.getObject("pfd_subjectcode"),rs.getObject("pfd_subjectname"),
								rs.getObject("pfd_subjecttype"),rs.getObject("pfd_grade"),rs.getObject("pfd_currency"),rs.getObject("pfd_amount")});
					}
					execute("update ProjectFeeClaim set pc_claimamount = (select sum(pcd_claimamount) from ProjectFeeClaimDetail where pcd_pcid=" 
							+ pcid + ") where pc_id=" + pcid);
				}
			}
			return pcid;
		} catch (Exception e){
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
			return 0;
		}
	}
}