package com.uas.erp.service.oa.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.oa.SalaryBillService;

@Service
public class SalaryBillServiceImpl implements SalaryBillService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private VoucherDao voucherDao;

	@Override
	public void auditSalaryBill(int sb_id) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("SalaryBill",
				"sb_statuscode", "sb_id=" + sb_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.audit_onlyCommited"));
		}
		// 执行审核操作
		baseDao.audit("SalaryBill", "sb_id=" + sb_id, "sb_status",
				"sb_statuscode", "sb_auditdate", "sb_auditer");
		// 执行审核前的其它逻辑
		handlerService.handler("SalaryBill", "audit", "before",
				new Object[] { sb_id });
		// 记录操作
		baseDao.logger.audit("SalaryBill", "sb_id", sb_id);
		// 执行审核后的其它逻辑
		handlerService.handler("SalaryBill", "audit", "after",
				new Object[] { sb_id });
	}

	@Override
	public void resAuditSalaryBill(int sb_id) {
		// 执行反审核前的其它逻辑
		handlerService.handler("SalaryBill", "resAudit", "before",
				new Object[] { sb_id });
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("SalaryBill",
				new String[]{"sb_statuscode","sb_vouchercode"}, "sb_id=" + sb_id);
		if (!status[0].equals("AUDITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resAudit_onlyAudit"));
		}
		if(status[1] != null && !"".equals(status[1])){
			BaseUtil.showError("已生成计提研发工资的凭证，不允许反审核！");
		}
		// 执行反审核操作
		baseDao.resAudit("SalaryBill", "sb_id=" + sb_id, "sb_status",
				"sb_statuscode", "sb_auditdate", "sb_auditer");

		// 记录操作
		baseDao.logger.resAudit("SalaryBill", "sb_id", sb_id);
		// 执行反审核后的其它逻辑
		handlerService.handler("SalaryBill", "resAudit", "after",
				new Object[] { sb_id });

	}

	@Override
	public int createVoucher(int sb_id) {
		int vo_id = 0;
		Object yearmonth = baseDao.getFieldDataByCondition("SalaryBill", "sb_yearmonth", "sb_id=" + sb_id);
		if(yearmonth != null && !"".equals(yearmonth) && !"0".equals(yearmonth)){
			boolean haveturn = baseDao.checkByCondition("Voucher", "vo_yearmonth=" + yearmonth + " and vo_explanation like '%计提研发工资'");
			if (!haveturn) {
				BaseUtil.showError("计提研发工资的凭证已经存在！");
			}
		} else {
			BaseUtil.showError("请先选择期间！");
		}
		vo_id = baseDao.getSeqId("VOUCHER_SEQ");
		int vdid = baseDao.getSeqId("VOUCHERDETAIL_SEQ");
		Employee employee = SystemSession.getUser();
		String code = baseDao.sGetMaxNumber("Voucher", 2);
		String vonumber = voucherDao.getVoucherNumber(String.valueOf(yearmonth), null, null);
		baseDao.execute("INSERT INTO Voucher(vo_id, vo_code, vo_date, vo_emid, vo_recordman, vo_status, vo_statuscode,"
				+ "vo_printstatus,vo_number, vo_yearmonth, vo_refno, vo_recorddate, vo_explanation,vo_remark)"
				+ " select " + vo_id + ",'" + code + "',sb_recorddate,'"+employee.getEm_id()+"','"+employee.getEm_name()+"','在录入','ENTERING',"
				+ "'"+BaseUtil.getLocalMessage("UNPRINT")+"',"+vonumber+",sb_yearmonth,sb_code,sysdate,sb_yearmonth||'计提研发工资','项目工资单转入' "
				+ " from SalaryBill where sb_id=" + sb_id);
		baseDao.execute("INSERT INTO VoucherDetail(vd_id, vd_code, vd_void, vd_detno, vd_yearmonth, vd_explanation, vd_catecode,vd_debit,vd_catename)"
				+ " select " + vdid + ",'" + code + "',"+vo_id+",1,sb_yearmonth,sb_yearmonth||'计提研发工资',sb_outcatecode,0,sb_outcatedesc"
				+ " from SalaryBill where sb_id=" + sb_id);
		baseDao.execute("update VoucherDetail set vd_debit=nvl((select sum(nvl(sbd_standardsalary,0)) from SALARYBILLDETAIL where sbd_sbid="+sb_id+"),0)*(-1) where vd_id=" + vdid);
		baseDao.execute("INSERT INTO VoucherDetailAss(VDS_ID, VDS_VDID, VDS_DETNO, VDS_ASSTYPE, VDS_ASSID, VDS_ASSCODE, VDS_ASSNAME,VDS_TYPE)"
				+ " select VoucherDetailAss_SEQ.nextval," + vdid + ",1,'部门',0,sb_departmentcode,sb_departmentname,'Voucher'"
				+ " from SalaryBill where sb_id=" + sb_id);
		SqlRowList rs = baseDao.queryForRowSet("select * from SALARYBILLDETAIL left join Project on sbd_prjcode=prj_code where sbd_sbid=?", sb_id);
		int detno = 2;
		int vd_id = 0;
		while (rs.next()) {
			vd_id = baseDao.getSeqId("VOUCHERDETAIL_SEQ");
			baseDao.execute("INSERT INTO VoucherDetail(vd_id, vd_code, vd_void, vd_detno, vd_yearmonth, vd_explanation, vd_catecode,vd_debit,vd_catename)"
					+ " select " + vd_id + ",'" + code + "',"+vo_id+","+(detno++)+",sb_yearmonth,sb_yearmonth||'计提研发工资',sb_incatecode,"+rs.getGeneralDouble("sbd_standardsalary")+",sb_incatedesc"
					+ " from SalaryBill where sb_id=" + sb_id);
			baseDao.execute("INSERT INTO VoucherDetailAss(VDS_ID, VDS_VDID, VDS_DETNO, VDS_ASSTYPE, VDS_ASSID, VDS_TYPE) values (VoucherDetailAss_SEQ.nextval," + vd_id + ",1,'项目',0,'Voucher')");
			if(!"".equals(rs.getGeneralString("sbd_prjcode"))){
				baseDao.execute("update VoucherDetailAss set VDS_ASSCODE='"+rs.getGeneralString("sbd_prjcode")+"', VDS_ASSNAME='"+rs.getGeneralString("prj_name")+"' where VDS_VDID="+ vd_id + " and VDS_DETNO=1");
			}
			baseDao.execute("INSERT INTO VoucherDetailAss(VDS_ID, VDS_VDID, VDS_DETNO, VDS_ASSTYPE, VDS_ASSID, VDS_TYPE) values(VoucherDetailAss_SEQ.nextval," + vd_id + ",2,'部门',0,'Voucher')");
			if(!"".equals(rs.getGeneralString("sbd_departmentcode"))){
				baseDao.execute("update VoucherDetailAss set VDS_ASSCODE='"+rs.getGeneralString("sbd_departmentcode")+"', VDS_ASSNAME='"+rs.getGeneralString("sbd_departmentname")+"' where VDS_VDID="+ vd_id + " and VDS_DETNO=2");
			}
		}
		// 修改收料通知单状态
		baseDao.updateByCondition("SalaryBill", "sb_vouchercode='"+code+"'", "sb_id=" + sb_id);
		return vo_id;
	}

}
