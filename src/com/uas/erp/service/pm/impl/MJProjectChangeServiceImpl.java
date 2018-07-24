package com.uas.erp.service.pm.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.MJProjectChangeService;

@Service("MJProjectChangeService")
public class MJProjectChangeServiceImpl implements MJProjectChangeService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void auditMJProjectChange(int wsc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("MOD_MJPROJECTCHANGE", new String[] { "wsc_statuscode", "wsc_wscode",
				"ws_newstf" }, "wsc_id=" + wsc_id);
		StateAssert.auditOnlyCommited(status[0]);
		if (StringUtil.hasText(status[2])) {

		} else {
			BaseUtil.showError("请填写新受托方(乙方)信息！");
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { wsc_id });
		// 执行审核操作
		baseDao.audit("MOD_MJPROJECTCHANGE", "wsc_id=" + wsc_id, "wsc_status", "wsc_statuscode", "wsc_auditdate", "wsc_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "wsc_id", wsc_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { wsc_id });
		if (StringUtil.hasText(status[1])) {
			Object wsid = baseDao.getFieldDataByCondition("MOD_MJPROTECT", "ws_id", "ws_code='" + status[1] + "'");
			if (wsid != null) {
				Object newstf = status[2];
				if (StringUtil.hasText(newstf)) {
					baseDao.execute("update MOD_MJPROTECT set ws_statuscode='FINISH', ws_status='" + BaseUtil.getLocalMessage("FINISH")
							+ "',ws_mouldstatus='已移模' where ws_id=" + wsid);
					int ws_id = baseDao.getSeqId("MOD_MJPROTECT_SEQ");
					String code = baseDao.sGetMaxNumber("MJProject!Mould", 2);
					baseDao.execute("insert into MOD_MJPROTECT(ws_id,ws_code,ws_wtf,ws_stf,ws_status,ws_promise,ws_dd,ws_ddate,ws_wcf,ws_cdate,ws_recorder,ws_indate,ws_departmentcode,ws_departmentname,ws_cop,ws_attach,ws_statuscode) "
							+ "select "
							+ ws_id
							+ ",'"
							+ code
							+ "',ws_wtf,'"
							+ newstf
							+ "','"
							+ BaseUtil.getLocalMessage("ENTERING")
							+ "',ws_promise,ws_dd,ws_ddate,ws_wcf,ws_cdate,'"
							+ SystemSession.getUser().getEm_name()
							+ "',sysdate,ws_departmentcode,ws_departmentname,ws_cop,ws_attach,'ENTERING' FROM MOD_MJPROTECT WHERE WS_ID="
							+ wsid);
					baseDao.execute("insert into MOD_MJPROTECTDetail(wd_id,wd_wsid,wd_code,wd_detno,wd_mjcode,wd_kzhtcode,wd_pstyle,"
							+ "wd_prodcode,wd_mjvendcode,wd_mjvendname,wd_kzdate,wd_ysdate,wd_zs,wd_cy,wd_qs,wd_image,wd_price,wd_weight,"
							+ "wd_size,wd_kzhtdetno,wd_mjysid) select MOD_MJPROTECTDETAIL_SEQ.nextval, " + ws_id + ", '" + code
							+ "', wd_detno,wd_mjcode,wd_kzhtcode,wd_pstyle,"
							+ "wd_prodcode,wd_mjvendcode,wd_mjvendname,wd_kzdate,wd_ysdate,wd_zs,wd_cy,wd_qs,wd_image,wd_price,wd_weight,"
							+ "wd_size,wd_kzhtdetno,wd_mjysid from MOD_MJPROTECTDetail where wd_wsid=" + wsid);
					BaseUtil.appendError("新模具委托保管书&nbsp;&nbsp;"
							+ "<a href=\"javascript:openUrl('jsps/pm/mould/MJProject.jsp?formCondition=ws_idIS" + ws_id
							+ "&gridCondition=wd_wsidIS" + ws_id + "')\">点击查看</a>&nbsp;");
				} else {
					BaseUtil.showError("请填写新受托方(乙方)信息！");
				}
			} else {
				BaseUtil.showError(status[1] + "模具委托保管书不存在！");
			}
		}
	}

	@Override
	public void auditMJProject(int ws_id, String caller) {
		baseDao.execute("update MOD_MJPROTECTDetail set wd_code=(select ws_code from MOD_MJPROTECT where wd_wsid=ws_id) where wd_wsid="
				+ ws_id + " and not exists (select 1 from MOD_MJPROTECT where wd_code=ws_code)");
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("MOD_MJPROTECT", new String[] { "ws_statuscode", "ws_code", "ws_sourcecode" },
				"ws_id=" + ws_id);
		StateAssert.auditOnlyCommited(status[0]);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { ws_id });
		// 执行审核操作
		baseDao.audit("MOD_MJPROTECT", "ws_id=" + ws_id, "ws_status", "ws_statuscode");
		baseDao.execute("update MOD_MJPROTECT set ws_mouldstatus='使用中' where ws_id=" + ws_id);
		baseDao.execute("update MOD_YSREPORT set MO_WSCODE='" + status[1] + "' where mo_code='" + status[2] + "'");
		// 记录操作
		baseDao.logger.audit(caller, "ws_id", ws_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { ws_id });
	}

	@Override
	public void resAuditMJProject(int ws_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("MOD_MJPROTECT", new String[] { "ws_statuscode", "ws_code", "ws_sourcecode" },
				"ws_id=" + ws_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { ws_id });
		// 执行审核操作
		baseDao.resOperate("MOD_MJPROTECT", "ws_id=" + ws_id, "ws_status", "ws_statuscode");
		baseDao.execute("update MOD_YSREPORT set MO_WSCODE=null where mo_code='" + status[2] + "'");
		// 记录操作
		baseDao.logger.resAudit(caller, "ws_id", ws_id);
		// 执行审核后的其它逻辑
		handlerService.afterResAudit(caller, new Object[] { ws_id });
	}
}
