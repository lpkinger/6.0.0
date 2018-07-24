package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.InquiryDao;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.pm.InquiryMouldService;

@Service("inquiryMouldService")
public class InquiryMouldServiceImpl implements InquiryMouldService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private InquiryDao inquiryDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveInquiryMould(String formStore, String gridStore, String gridStore2, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> grid2 = BaseUtil.parseGridStoreToMaps(gridStore2);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("InquiryMould", "in_code='" + store.get("in_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存PriceMould
		baseDao.execute(SqlUtil.getInsertSqlByFormStore(store, "InquiryMould", new String[] {}, new Object[] {}));
		// 保存PriceMouldDet
		baseDao.execute(SqlUtil.getInsertSqlbyList(grid, "InquiryMouldDet", "idd_id"));
		// 保存PriceMouldDetail
		baseDao.execute(SqlUtil.getInsertSqlbyList(grid2, "InquiryMouldDetail", "ind_id"));
		// 记录操作
		baseDao.logger.save(caller, "in_id", store.get("in_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void deleteInquiryMould(int in_id, String caller) {
		// 只能删除在录入的采购单!
		Object status = baseDao.getFieldDataByCondition("InquiryMould", "in_statuscode", "in_id=" + in_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { in_id }); // 删除InquiryMould
		baseDao.deleteById("InquiryMould", "in_id", in_id);
		// 删除InquiryMouldDet
		baseDao.deleteById("InquiryMoulddet", "idd_inid", in_id);
		// 删除InquiryMouldDetail
		baseDao.deleteById("InquiryMoulddetail", "ind_inid", in_id);
		// 记录操作
		baseDao.logger.delete(caller, "in_id", in_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { in_id });
	}

	@Override
	public void updateInquiryMouldById(String formStore, String gridStore, String gridStore2, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> gstore2 = BaseUtil.parseGridStoreToMaps(gridStore2);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("InquiryMould", "in_statuscode", "in_id=" + store.get("in_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 修改PriceMould
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "InquiryMould", "in_id"));
		// 修改PriceMouldDet
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "InquiryMouldDet", "idd_id"));
		// 修改PriceMouldDetail
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore2, "InquiryMouldDetail", "ind_id"));
		// 记录操作
		baseDao.logger.update(caller, "pd_id", store.get("pd_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void printInquiryMould(int in_id, String caller) {
		// 只能打印审核后的采购单!
		Object status = baseDao.getFieldDataByCondition("InquiryMould", "in_statuscode", "in_id=" + in_id);
		if (!status.equals("AUDITED") && !status.equals("PARTRECEIVED") && !status.equals("RECEIVED") && !status.equals("NULLIFIED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.InquiryMould.print_onlyAudit"));
		}
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, new Object[] { in_id }); // 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "in_id", in_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, new Object[] { in_id });
	}

	@Override
	public void auditInquiryMould(int in_id, String caller) {
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { in_id });
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(in_vendcode) from InquiryMould left join vendor on in_vendcode=ve_code where in_id=" + in_id
						+ " and nvl(in_vendcode,' ')<>' ' and nvl(ve_uu,' ')=' '", String.class);
		if (dets != null) {
			BaseUtil.showError("供应商[" + dets + "]UU号为空，不允许审核！");
		}
		// 执行审核操作
		baseDao.audit("InquiryMould", "in_id=" + in_id, "in_status", "in_statuscode", "in_auditdate", "in_auditor");
		// 记录操作
		baseDao.logger.audit(caller, "in_id", in_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { in_id });
	}

	@Override
	public void resAuditInquiryMould(int in_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("InquiryMould", new String[] { "in_statuscode", "in_sendstatus" }, "in_id="
				+ in_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		// 判断该单据上是否上传到B2B，已上传，则不允许反审核
		StateAssert.onSendingLimit(status[1]);
		StateAssert.onSendedLimit(status[1]);
		// 判断该询价单是否已经转入过核价单
		Object code = baseDao.getFieldDataByCondition("InquiryMould", "in_code", "in_id=" + in_id);
		code = baseDao.getFieldDataByCondition("PurchasePrice", "pp_code", "pp_source='" + code + "'");
		if (code != null && !code.equals("")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.InquiryMould.resAudit_haveturn")
					+ "<a href=\"javascript:openUrl('jsps/scm/purchase/purchasePrice.jsp?formCondition=pp_codeIS" + code
					+ "&gridCondition=ppd_ppcodeIS" + code + "')\">" + code + "</a>&nbsp;");
		} else {
			// 执行反审核操作
			baseDao.resAudit("InquiryMould", "in_id=" + in_id, "in_status", "in_statuscode", "in_auditdate", "in_auditor");
			// 记录操作
			baseDao.logger.resAudit(caller, "in_id", in_id);
		}
	}

	@Override
	public void submitInquiryMould(int in_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("InquiryMould", "in_statuscode", "in_id=" + in_id);
		StateAssert.submitOnlyEntering(status);
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(in_vendcode) from InquiryMould left join vendor on in_vendcode=ve_code where in_id=" + in_id
						+ " and nvl(in_vendcode,' ')<>' ' and nvl(ve_uu,' ')=' '", String.class);
		if (dets != null) {
			BaseUtil.showError("供应商[" + dets + "]UU号为空，不允许提交！");
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { in_id }); // 执行提交操作
		baseDao.submit("InquiryMould", "in_id=" + in_id, "in_status", "in_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "in_id", in_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { in_id });
	}

	@Override
	public void resSubmitInquiryMould(int in_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("InquiryMould", "in_statuscode", "in_id=" + in_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { in_id }); // 执行反提交操作
		baseDao.updateByCondition("InquiryMould", "in_statuscode='ENTERING',in_status='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"in_id=" + in_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "in_id", in_id);
		handlerService.afterResSubmit(caller, new Object[] { in_id });
	}

	/**
	 * 转物料核价单
	 */
	@Override
	public int turnPurcPrice(int in_id, String caller) {
		int id = 0;
		// 判断该询价单是否已经转入过核价单
		baseDao.execute("update InquiryMoulddetail set id_isagreed=-1 where ind_inid=" + in_id);
		Object code = baseDao.getFieldDataByCondition("InquiryMould", "in_code", "in_id=" + in_id);
		Object[] pp = baseDao.getFieldsDataByCondition("PurchasePrice", "pp_id,pp_code", "pp_source='" + code + "'");
		if (pp != null && pp[0] != null) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.InquiryMould.haveturn")
					+ "<a href=\"javascript:openUrl('jsps/scm/purchase/purchasePrice.jsp?formCondition=pp_idIS" + pp[0]
					+ "&gridCondition=ppd_ppidIS" + pp[0] + "')\">" + pp[1] + "</a>&nbsp;");
		} else {
			int countnum = baseDao.getCount("select count(*) from InquiryMoulddetail where id_isagreed=-1 and ind_inid=" + in_id);
			if (countnum > 0) {
				id = inquiryDao.turnPurcPrice(in_id, "采购");
				// 记录操作
				baseDao.logger.turn("msg.turnPurcPrice", caller, "in_id", in_id);
			} else {
				BaseUtil.showErrorOnSuccess(BaseUtil.getLocalMessage("scm.purchase.InquiryMould.nohaveturn"));
			}
		}
		baseDao.updateByCondition("InquiryMould", "in_checkstatus='已批准',in_checkstatuscode='APPROVED'", "in_id=" + in_id);
		return id;
	}

	public void nullifybeforeCheck(int in_id) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select pd_code,pd_statuscode,pd_status,in_vendcode,pd_vend1,pd_vend2,pd_vend3,pd_vend4,pd_vend5,pd_id from InquiryMould,PriceMould where in_id=? and in_sourceid=pd_id and in_sourcetype='模具报价单'"
								+ " and pd_statuscode<>'ENTERING'", in_id);
		if (rs.next()) {
			BaseUtil.showError("来源报价单[" + rs.getObject("pd_code") + "]状态为[" + rs.getObject("pd_status") + "]，不允许作废！");
		}
	}

	@Override
	public void nullifyInquiryMould(int in_id, String caller, String reason) {
		nullifybeforeCheck(in_id);
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select pd_code,pd_statuscode,pd_status,in_vendcode,pd_vend1,pd_vend2,pd_vend3,pd_vend4,pd_vend5,pd_id from InquiryMould,PriceMould where in_id=? and in_sourceid=pd_id and in_sourcetype='模具报价单'",
						in_id);
		if (rs.next()) {
			// 询价单作废的时候将模具报价单信息返回
			if (StringUtil.hasText(rs.getObject("in_vendcode"))) {
				String invend = rs.getGeneralString("in_vendcode");
				int pd_id = rs.getGeneralInt("pd_id");
				// 报价单供应商1
				if (invend.equals(rs.getGeneralString("pd_vend1"))) {
					baseDao.execute("update PriceMouldDet set pdd_price1=0 where pdd_pdid=" + pd_id);
					baseDao.execute("update PriceMouldDetail set pmd_prodprice1=0 where pmd_pdid=" + pd_id);
					baseDao.execute("update PriceMould set pd_mouldprice1=0, pd_mmprice1=0 where pd_id=" + pd_id);
				}
				// 报价单供应商2
				if (invend.equals(rs.getGeneralString("pd_vend2"))) {
					baseDao.execute("update PriceMouldDet set pdd_price2=0 where pdd_pdid=" + pd_id);
					baseDao.execute("update PriceMouldDetail set pmd_prodprice2=0 where pmd_pdid=" + pd_id);
					baseDao.execute("update PriceMould set pd_mouldprice2=0, pd_mmprice2=0 where pd_id=" + pd_id);
				}
				// 报价单供应商3
				if (invend.equals(rs.getGeneralString("pd_vend3"))) {
					baseDao.execute("update PriceMouldDet set pdd_price3=0 where pdd_pdid=" + pd_id);
					baseDao.execute("update PriceMouldDetail set pmd_prodprice3=0 where pmd_pdid=" + pd_id);
					baseDao.execute("update PriceMould set pd_mouldprice3=0, pd_mmprice3=0 where pd_id=" + pd_id);
				}
				// 报价单供应商4
				if (invend.equals(rs.getGeneralString("pd_vend4"))) {
					baseDao.execute("update PriceMouldDet set pdd_price4=0 where pdd_pdid=" + pd_id);
					baseDao.execute("update PriceMouldDetail set pmd_prodprice4=0 where pmd_pdid=" + pd_id);
					baseDao.execute("update PriceMould set pd_mouldprice4=0, pd_mmprice4=0 where pd_id=" + pd_id);
				}
				// 报价单供应商5
				if (invend.equals(rs.getGeneralString("pd_vend5"))) {
					baseDao.execute("update PriceMouldDet set pdd_price5=0 where pdd_pdid=" + pd_id);
					baseDao.execute("update PriceMouldDetail set pmd_prodprice5=0 where pmd_pdid=" + pd_id);
					baseDao.execute("update PriceMould set pd_mouldprice5=0, pd_mmprice5=0 where pd_id=" + pd_id);
				}
			}
		}
		// 作废
		baseDao.updateByCondition("InquiryMould", "in_statuscode='NULLIFIED', in_status='" + BaseUtil.getLocalMessage("NULLIFIED")
				+ "', in_reason='" + reason + "'", "in_id=" + in_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.nullify"), BaseUtil
				.getLocalMessage("msg.nullifySuccess"), caller + "|in_id=" + in_id));

	}

	@Override
	public void returnPriceMould(int in_id, int idd_id, int ind_id) {
		// 将模具询价单单价返回到模具报价单
		SqlRowList rs = baseDao.queryForRowSet(
				"select * from InquiryMould where in_id=? and nvl(in_sourceid,0)<>0 and nvl(in_sourcetype,' ')='模具报价单' and nvl(in_status,' ')<>'已作废'",
				new Object[] { in_id });
		if (rs.next()) {
			Object vend = rs.getObject("in_vendcode");
			int pdid = rs.getGeneralInt("in_sourceid");
			SqlRowList pd = baseDao.queryForRowSet("select * from PriceMould where pd_id=? and pd_vend1=?", new Object[] { pdid, vend });
			if (pd.next()) {
				baseDao.execute("merge into PriceMouldDetail a using ( select ind_pmdid,ind_price from InquiryMouldDetail where ind_inid="
						+ in_id + " and ind_id= " + ind_id
						+ " ) b on (b.ind_pmdid=a.pmd_id  ) when matched then update set a.pmd_prodprice1 = nvl(b.ind_price,0)");
			}
			pd = baseDao.queryForRowSet("select * from PriceMould where pd_id=? and pd_vend2=?", new Object[] { pdid, vend });
			if (pd.next()) {
				baseDao.execute("merge into PriceMouldDetail a using ( select ind_pmdid,ind_price from InquiryMouldDetail where ind_inid="
						+ in_id + " and ind_id= " + ind_id
						+ " ) b on (b.ind_pmdid=a.pmd_id  ) when matched then update set a.pmd_prodprice2 = nvl(b.ind_price,0)");
			}
			pd = baseDao.queryForRowSet("select * from PriceMould where pd_id=? and pd_vend3=?", new Object[] { pdid, vend });
			if (pd.next()) {
				baseDao.execute("merge into PriceMouldDetail a using ( select ind_pmdid,ind_price from InquiryMouldDetail where ind_inid="
						+ in_id + " and ind_id= " + ind_id
						+ " ) b on (b.ind_pmdid=a.pmd_id  ) when matched then update set a.pmd_prodprice3 = nvl(b.ind_price,0)");
			}
			pd = baseDao.queryForRowSet("select * from PriceMould where pd_id=? and pd_vend4=?", new Object[] { pdid, vend });
			if (pd.next()) {
				baseDao.execute("merge into PriceMouldDetail a using ( select ind_pmdid,ind_price from InquiryMouldDetail where ind_inid="
						+ in_id + " and ind_id= " + ind_id
						+ " ) b on (b.ind_pmdid=a.pmd_id  ) when matched then update set a.pmd_prodprice4 = nvl(b.ind_price,0)");
			}
			pd = baseDao.queryForRowSet("select * from PriceMould where pd_id=? and pd_vend5=?", new Object[] { pdid, vend });
			if (pd.next()) {
				baseDao.execute("merge into PriceMouldDetail a using ( select ind_pmdid,ind_price from InquiryMouldDetail where ind_inid="
						+ in_id + " and ind_id= " + ind_id
						+ " ) b on (b.ind_pmdid=a.pmd_id  ) when matched then update set a.pmd_prodprice5 = nvl(b.ind_price,0)");
			}
		}
	}

	@Override
	public void returnPriceMouldDet(int in_id, int idd_id) {
		// 将模具询价单单价返回到模具报价单
		SqlRowList rs = baseDao.queryForRowSet(
				"select * from InquiryMould where in_id=? and nvl(in_sourceid,0)<>0 and nvl(in_sourcetype,' ')='模具报价单' and nvl(in_status,' ')<>'已作废'",
				new Object[] { in_id });
		if (rs.next()) {
			Object vend = rs.getObject("in_vendcode");
			int pdid = rs.getGeneralInt("in_sourceid");
			SqlRowList pd = baseDao.queryForRowSet("select * from PriceMould where pd_id=? and pd_vend1=?", new Object[] { pdid, vend });
			if (pd.next()) {
				baseDao.execute("merge into pricemoulddet a using ( select idd_pddid,idd_price from inquirymoulddet where idd_inid="
						+ in_id + " and idd_id= " + idd_id
						+ " ) b on (b.idd_pddid=a.pdd_id  ) when matched then update set a.pdd_price1 = nvl(b.idd_price,0)");
			}
			pd = baseDao.queryForRowSet("select * from PriceMould where pd_id=? and pd_vend2=?", new Object[] { pdid, vend });
			if (pd.next()) {
				baseDao.execute("merge into pricemoulddet a using ( select idd_pddid,idd_price from inquirymoulddet where idd_inid="
						+ in_id + " and idd_id= " + idd_id
						+ " ) b on (b.idd_pddid=a.pdd_id  ) when matched then update set a.pdd_price2 = nvl(b.idd_price,0)");
			}
			pd = baseDao.queryForRowSet("select * from PriceMould where pd_id=? and pd_vend3=?", new Object[] { pdid, vend });
			if (pd.next()) {
				baseDao.execute("merge into pricemoulddet a using ( select idd_pddid,idd_price from inquirymoulddet where idd_inid="
						+ in_id + " and idd_id= " + idd_id
						+ " ) b on (b.idd_pddid=a.pdd_id  ) when matched then update set a.pdd_price3 = nvl(b.idd_price,0)");
			}
			pd = baseDao.queryForRowSet("select * from PriceMould where pd_id=? and pd_vend4=?", new Object[] { pdid, vend });
			if (pd.next()) {
				baseDao.execute("merge into pricemoulddet a using ( select idd_pddid,idd_price from inquirymoulddet where idd_inid="
						+ in_id + " and idd_id= " + idd_id
						+ " ) b on (b.idd_pddid=a.pdd_id  ) when matched then update set a.pdd_price4 = nvl(b.idd_price,0)");
			}
			pd = baseDao.queryForRowSet("select * from PriceMould where pd_id=? and pd_vend5=?", new Object[] { pdid, vend });
			if (pd.next()) {
				baseDao.execute("merge into pricemoulddet a using ( select idd_pddid,idd_price from inquirymoulddet where idd_inid="
						+ in_id + " and idd_id= " + idd_id
						+ " ) b on (b.idd_pddid=a.pdd_id  ) when matched then update set a.pdd_price5 = nvl(b.idd_price,0)");
			}
		}
	}
}
