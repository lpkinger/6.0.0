package com.uas.erp.service.plm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.Key;
import com.uas.erp.service.plm.ProjectFinishReportService;

@Service
public class ProjectFinishReportServiceImpl implements ProjectFinishReportService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private VoucherDao voucherDao;
	@Autowired
	private TransferRepository transferRepository;

	@Override
	public void saveProjectFinishReport(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store });
		// 执行保存操作
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "ProjectFinishReport"));
		// 记录操作
		baseDao.logger.save(caller, "pfr_id", store.get("pfr_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void updateProjectFinishReport(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store });
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "ProjectFinishReport", "pfr_id"));
		// 记录操作
		baseDao.logger.update(caller, "pfr_id", store.get("pfr_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void deleteProjectFinishReport(int id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { id });
		// 删除
		baseDao.deleteById("ProjectFinishReport", "pfr_id", id);
		// 记录操作
		baseDao.logger.delete(caller, "pfr_id", id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { id });
	}

	@Override
	public void auditProjectFinishReport(int id, String caller) {
		Object[] status = baseDao.getFieldsDataByCondition("ProjectFinishReport", new String[] { "pfr_statuscode", "pfr_close",
				"pfr_closereason" }, "pfr_id=" + id);
		StateAssert.auditOnlyCommited(status[0]);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { id });
		int bill = baseDao.getCountByCondition("ARBILL", "ab_sourceid=" + id + " and AB_SOURCETYPE='项目验收结案'");
		if (bill == 0) {
			Key key = transferRepository.transfer("ProjectFinishReport!ToARBill", id);
			// 转入明细
			transferRepository.transferDetail("ProjectFinishReport!ToARBill", id, key);
			baseDao.execute("update arbilldetail set abd_code=(select ab_code from arbill where abd_abid=ab_id) where abd_abid="
					+ key.getId() + " and not exists (select 1 from arbill where abd_code=ab_code)");
			baseDao.execute("update arbilldetail set abd_aramount=ROUND(abd_thisvoprice*abd_qty,2) WHERE abd_abid=" + key.getId());
			baseDao.execute("update arbilldetail set abd_noaramount=ROUND(abd_thisvoprice*abd_qty/(1+abd_taxrate/100),2) WHERE abd_abid="
					+ key.getId());
			baseDao.execute("update arbilldetail set abd_taxamount=NVL(abd_aramount,0)-NVL(abd_noaramount,0) WHERE abd_abid=" + key.getId());
			// 更新ARBill主表的金额
			baseDao.execute("update arbill set ab_aramount=round((select sum(abd_aramount) from arbilldetail where abd_abid=" + key.getId()
					+ "),2) where ab_id=" + key.getId());
			baseDao.execute("update arbill set ab_taxamount=(select sum(round(((abd_thisvoprice*abd_qty*abd_taxrate/100)/(1+abd_taxrate/100)),2)) from arbilldetail where abd_abid="
					+ key.getId() + ")+ab_differ where ab_id=" + key.getId());
		}
		// 执行审核操作
		baseDao.audit("ProjectFinishReport", "pfr_id=" + id, "pfr_status", "pfr_statuscode", "pfr_auditdate", "pfr_auditman");
		baseDao.execute("update project set PRJ_STATUSCODE='FINISH', PRJ_STATUS='已结案',PRJ_CLOSE='" + status[1] + "',PRJ_CLOSEREASON='"
				+ status[2] + "' WHERE PRJ_CODE=(SELECT pfr_prjcode from ProjectFinishReport where pfr_id=" + id
				+ " and nvl(pfr_prjcode,' ')<>' ')");
		// 记录操作
		baseDao.logger.audit(caller, "pfr_id", id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { id });
	}

	@Override
	public void resAuditProjectFinishReport(int id, String caller) {
		// 执行反审核操作
		baseDao.resAudit("ProjectFinishReport", "pfr_id=" + id, "pfr_status", "pfr_statuscode", "pfr_auditdate", "pfr_auditman");
		baseDao.execute("update project set PRJ_STATUSCODE='AUDITED', PRJ_STATUS='已审核' WHERE PRJ_CODE=(SELECT pfr_prjcode from ProjectFinishReport where pfr_id="
				+ id + " and nvl(pfr_prjcode,' ')<>' ')");
		// 记录操作
		baseDao.logger.resAudit(caller, "pfr_id", id);
	}

	@Override
	public void submitProjectFinishReport(int id, String caller) {
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { id });
		// 执行提交操作
		baseDao.submit("ProjectFinishReport", "pfr_id=" + id, "pfr_status", "pfr_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pfr_id", id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { id });
	}

	@Override
	public void resSubmitProjectFinishReport(int id, String caller) {
		// 执行反提交操作
		baseDao.resOperate("ProjectFinishReport", "pfr_id=" + id, "pfr_status", "pfr_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pfr_id", id);
	}

	/**
	 * 转资本化
	 * 
	 * @author mad
	 */
	@Override
	public String turnCapitalization(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		if (maps.size() > 0) {
			int vo_id = baseDao.getSeqId("VOUCHER_SEQ");
			String code = baseDao.sGetMaxNumber("Voucher", 2);
			Object prjcode = maps.get(0).get("vds_asscode");
			int nowym = voucherDao.getNowPddetno("Month-A");// 当前期间
			Map<String, Object> period = voucherDao.getJustPeriods("Month-A");
			int detno = 0;
			Object number = voucherDao.getVoucherNumber(String.valueOf(nowym), null, null);
			boolean bool = baseDao
					.execute(
							"INSERT INTO VOUCHER(vo_id, vo_statuscode, vo_explanation, vo_code, vo_recorddate, vo_date, vo_number,"
									+ "vo_yearmonth, vo_recordman, vo_status, vo_printstatus, vo_emid) VALUES (?,'ENTERING',?,?,sysdate,?,?,?,?,?,?,?)",
							new Object[] { vo_id, prjcode + "转资本化", code, period.get("PD_ENDDATE"), number, nowym,
									SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("ENTERING"),
									BaseUtil.getLocalMessage("UNPRINT"), SystemSession.getUser().getEm_id() });
			if (bool) {
				for (Map<Object, Object> m : maps) {
					int vdid = Integer.parseInt(m.get("vd_id").toString());
					int vd_id = baseDao.getSeqId("VOUCHERDETAIL_SEQ");
					baseDao.execute("INSERT INTO VoucherDetail(vd_id, vd_void, vd_detno, vd_yearmonth, vd_explanation, vd_catecode, vd_currency,"
							+ "vd_rate, vd_credit)"
							+ "select "
							+ vd_id
							+ ","
							+ vo_id
							+ ","
							+ (detno++)
							+ ","
							+ nowym
							+ ",'月份'||vo_yearmonth||'凭证号'||vo_number,vd_catecode,vd_currency,vd_rate,vd_debit"
							+ " from voucher, voucherdetail where vd_void=vo_id and vd_id=" + vdid);
					baseDao.execute("INSERT INTO voucherdetailass(VDS_ID,VDS_VDID,VDS_DETNO,VDS_ASSTYPE,VDS_ASSID,VDS_ASSCODE,VDS_ASSNAME,VDS_TYPE) "
							+ "select VoucherDetailAss_SEQ.NEXTVAL,"
							+ vd_id
							+ ",VDS_DETNO,VDS_ASSTYPE,VDS_ASSID,VDS_ASSCODE,VDS_ASSNAME,VDS_TYPE"
							+ " FROM voucherdetailass WHERE VDS_VDID=" + vdid);
				}
				return "转入成功,凭证号:" + "<a href=\"javascript:openUrl('jsps/fa/ars/voucher.jsp?formCondition=vo_idIS" + vo_id
						+ "&gridCondition=vd_voidIS" + vo_id + "')\">" + code + "</a>&nbsp;<hr>";
			}
		}
		return null;
	}
}
