package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.PurchaseDao;
import com.uas.erp.dao.common.SaleDao;
import com.uas.erp.service.scm.VerifyApplyChangeService;

@Service("verifyApplyChangeService")
public class VerifyApplyChangeServiceImpl implements VerifyApplyChangeService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private SaleDao saleDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private PurchaseDao purchaseDao;

	@Override
	public void saveVerifyApplyChange(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("VerifyApplyChange", "vc_code='" + store.get("vc_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存VerifyApplyChange
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "VerifyApplyChange", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存VerifyApplyChangeDetail
		for (Map<Object, Object> m : grid) {
			m.put("vcd_id", baseDao.getSeqId("VERIFYAPPLYCHANGEDETAIL_SEQ"));
			m.put("vcd_code", store.get("vc_code"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "VerifyApplyChangeDetail");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "vc_id", store.get("vc_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void deleteVerifyApplyChange(String caller, int vc_id) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("VerifyApplyChange", "vc_statuscode", "vc_id=" + vc_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, vc_id);
		// 删除VerifyApplyChange
		baseDao.deleteById("VerifyApplyChange", "vc_id", vc_id);
		// 删除VerifyApplyChangeDetail
		baseDao.deleteById("VerifyApplyChangedetail", "vcd_vcid", vc_id);
		// 记录操作
		baseDao.logger.delete(caller, "vc_id", vc_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, vc_id);
	}

	@Override
	public void updateVerifyApplyChangeById(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("VerifyApplyChange", "vc_statuscode", "vc_id=" + store.get("vc_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 修改VerifyApplyChange
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "VerifyApplyChange", "vc_id");
		baseDao.execute(formSql);
		double tQty = 0;
		double qty = 0;
		double aq = 0;
		double returnqty = 0;
		Object pucode = null;
		Object pudetno = 0;
		Object[] vads = null;
		StringBuffer sb = new StringBuffer();
		// 修改VerifyApplyChangeDetail
		for (Map<Object, Object> s : gstore) {
			vads = baseDao.getFieldsDataByCondition("VerifyApply left join VerifyApplyDetail on va_id=vad_vaid", new String[] { "vad_id",
					"va_class" }, "va_code='" + store.get("vcd_vacode") + "' and vad_detno=" + store.get("vcd_vadetno"));
			if (vads != null && "采购收料单".equals(vads[1])) {
				pucode = s.get("vcd_pucode");
				pudetno = s.get("vcd_pudetno");
				tQty = Double.parseDouble(String.valueOf(s.get("vcd_newqty")));
				qty = baseDao.getFieldValue("VerifyApplyDetail", "nvl(sum(vad_qty),0)", "vad_pucode='" + pucode + "' and vad_pudetno="
						+ pudetno + " AND vad_id <>" + vads[0], Double.class);
				returnqty = baseDao.getFieldValue("ProdIODetail", "nvl(sum(pd_outqty),0)", "pd_ordercode='" + pucode
						+ "' and pd_orderdetno=" + pudetno + " AND pd_piclass in ('采购验退单','不良品出库单') and pd_status>0", Double.class);
				aq = baseDao.getFieldValue("PurchaseDetail", "nvl(pd_qty,0)", "pd_code='" + pucode + "' and pd_detno=" + pudetno,
						Double.class);
				if (aq + returnqty < qty + tQty) {
					sb.append("采购单号：").append(pucode).append("序号：").append(pudetno).append("超出数量：").append((qty + tQty - aq))
							.append("<br>");
				}
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError("新数量超出采购单数量！" + sb.toString());
		}
		List<String> gridSql = SqlUtil.getInsertOrUpdateSql(gstore, "VerifyApplyChangeDetail", "vcd_id");
		baseDao.execute(gridSql);
		baseDao.execute("update VerifyApplyChangeDetail set vcd_code='" + store.get("vc_code") + "' where vcd_vcid=" + store.get("vc_id"));
		// 记录操作
		baseDao.logger.update(caller, "vc_id", store.get("vc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void auditVerifyApplyChange(int vc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("VerifyApplyChange", "vc_statuscode", "vc_id=" + vc_id);
		StateAssert.auditOnlyCommited(status);
		check(vc_id);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, vc_id);
		// 信息自动反馈到收料单
		baseDao.execute("update VerifyApplyDetail set vad_qty=(select vcd_newqty from VerifyApplyChangeDetail where vcd_vadid=vad_id and vcd_vcid="
				+ vc_id + ") where vad_id in (select vcd_vadid from VerifyApplyChangeDetail where vcd_vcid=" + vc_id + ")");
		baseDao.execute(
				"update AcceptNotifyDetail set and_yqty=NVL(and_yqty,0)+NVL((select nvl(vcd_newqty,0)-nvl(vcd_oldqty,0) from VerifyApplyChangeDetail left join VerifyApplyDetail on vcd_vadid=vad_id where nvl(vad_andid,0)>0 and vad_andid=and_id and vcd_vcid=? and nvl(vcd_qctype,' ')='采购收料单'),0) where and_id in (select vad_andid from VerifyApplyChangeDetail left join VerifyApplyDetail on vcd_vadid=vad_id where vcd_vcid=? and nvl(vcd_qctype,' ')='采购收料单')",
				vc_id, vc_id);
		baseDao.execute(
				"update PurchaseDetail set pd_yqty=NVL(pd_yqty,0)+NVL((select NVL(vcd_newqty,0)-NVL(vcd_oldqty,0) from VerifyApplyChangeDetail where vcd_pucode=pd_code and vcd_pudetno=pd_detno and vcd_vcid=? and nvl(vcd_qctype,' ')='采购收料单'),0) where (pd_code,pd_detno) in (select vcd_pucode, vcd_pudetno from VerifyApplyChangeDetail where vcd_vcid=? and nvl(vcd_qctype,' ')='采购收料单')",
				vc_id, vc_id);
		baseDao.execute(
				"update make set ma_haveqty=NVL(ma_haveqty,0)+(select NVL(vcd_newqty,0)-NVL(vcd_oldqty,0) from VerifyApplyChangeDetail where vcd_pucode=ma_code and vcd_vcid=? and nvl(vcd_qctype,' ')='委外收料单') where ma_code in (select vcd_pucode from VerifyApplyChangeDetail where vcd_vcid=? and nvl(vcd_qctype,' ')='委外收料单')",
				vc_id, vc_id);
		// 执行审核操作
		baseDao.audit("VerifyApplyChange", "vc_id=" + vc_id, "vc_status", "vc_statuscode", "vc_auditdate", "vc_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "vc_id", vc_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, vc_id);
	}

	private void check(Object vc_id) {
		baseDao.execute("update VerifyApplyChangeDetail set vcd_qctype=(select va_class from VerifyApply where vcd_vacode=va_code) where vcd_vcid="
				+ vc_id + " and nvl(vcd_qctype,' ')=' '");
		double tQty = 0;
		double qty = 0;
		double aq = 0;
		double returnqty = 0;
		Object pucode = null;
		Object pudetno = 0;
		Object aqS = null;
		StringBuffer sb = new StringBuffer();
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select * from VerifyApplyChangeDetail,VerifyApply,VerifyApplyDetail where vcd_vacode=va_code and vcd_vadetno=vad_detno and va_id=vad_vaid and vcd_vcid=?",
						vc_id);
		while (rs.next()) {
			if ("采购收料单".equals(rs.getObject("va_class"))) {
				pucode = rs.getObject("vcd_pucode");
				pudetno = rs.getObject("vcd_pudetno");
				tQty = rs.getGeneralDouble("vcd_newqty");
				qty = baseDao.getFieldValue("VerifyApplyDetail", "nvl(sum(vad_qty),0)", "vad_pucode='" + pucode + "' and vad_pudetno="
						+ pudetno + " AND vad_id <>" + rs.getObject("vad_id"), Double.class);
				returnqty = baseDao.getFieldValue("ProdIODetail", "nvl(sum(pd_outqty),0)", "pd_ordercode='" + pucode
						+ "' and pd_orderdetno=" + pudetno + " AND pd_piclass in ('采购验退单','不良品出库单') and pd_status>0", Double.class);
				aqS = baseDao.getFieldValue("PurchaseDetail", "nvl(pd_qty,0)", "pd_code='" + pucode + "' and pd_detno=" + pudetno,
						String.class);
				if(aqS==null){
					BaseUtil.showError("采购单号："+pucode+" 序号："+pudetno+" 不存在");
				}else{
					aq = Double.parseDouble(aqS.toString());
				}
				if (aq + returnqty < qty + tQty) {
					sb.append("采购单号：").append(pucode).append("序号：").append(pudetno).append("超出数量：").append((qty + tQty - aq))
							.append("<br>");
				}
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError("新数量超出采购单数量！" + sb.toString());
		}
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(vcd_detno) from VerifyApplyChangeDetail left join VerifyApplyDetail on vcd_vadid=vad_id where nvl(ve_code,' ')<>' ' and vcd_vcid=?",
						String.class, vc_id);
		if (dets != null) {
			BaseUtil.showError("存在已转检验单的收料单，不允许进行当前操作！行号" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(vcd_detno) from VerifyApplyChangeDetail WHERE  vcd_vcid=? and not exists (SELECT vad_code, vad_detno from VerifyApplyDetail where vad_code=vcd_vacode and vad_detno=vcd_vadetno) ",
						String.class, vc_id);
		if (dets != null) {
			BaseUtil.showError("明细行收料单不存在，不允许进行当前操作！行号" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(vcd_detno) from VerifyApplyChangeDetail WHERE vcd_vcid=? and vcd_qctype='采购收料单' and (vcd_pucode, vcd_pudetno) IN (SELECT pc_purccode, pcd_pddetno from PurchaseChangeDetail left join PurchaseChange on pcd_pcid=pc_id where pc_statuscode<>'AUDITED')",
						String.class, vc_id);
		if (dets != null) {
			BaseUtil.showError("明细行采购单存在未审核的采购变更单，不允许进行当前操作！行号" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(vcd_detno) from VerifyApplyChangeDetail left join PurchaseDetail on pd_code=vcd_pucode and pd_detno=vcd_pudetno WHERE vcd_vcid=? and vcd_qctype='采购收料单' and nvl(pd_yqty,0)+(nvl(vcd_newqty,0)-nvl(vcd_oldqty,0)) > nvl(pd_qty,0)",
						String.class, vc_id);
		if (dets != null) {
			BaseUtil.showError("明细行新数量填写超过采购单数量（已转数+加上本次差异数>采购数量），不允许进行当前操作！行号" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(vcd_detno) from VerifyApplyChangeDetail left join Make on ma_code=vcd_pucode WHERE vcd_vcid=? and vcd_qctype='委外收料单' and nvl(ma_haveqty,0)+(nvl(vcd_newqty,0)-nvl(vcd_oldqty,0)) > nvl(ma_qty,0)",
						String.class, vc_id);
		if (dets != null) {
			BaseUtil.showError("明细行新数量填写超过委外单数量（已转数+加上本次差异数>委外数量），不允许进行当前操作！行号" + dets);
		}
	}

	@Override
	public void submitVerifyApplyChange(String caller, int vc_id) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("VerifyApplyChange", "vc_statuscode", "vc_id=" + vc_id);
		StateAssert.submitOnlyEntering(status);
		// 同一收料单只能存在一张已提交未审核的变更单
		List<Object[]> va = baseDao.getFieldsDatasByCondition("VerifyApplyChangeDetail", new String[] { "vcd_vacode", "vcd_vadetno" },
				"vcd_vcid=" + vc_id);
		for (Object[] c : va) {
			int count = baseDao.getCountByCondition("VerifyApplyChangeDetail left join VerifyApplyChange on vcd_vcid=vc_id", "vcd_vacode='"
					+ c[0] + "' and vc_statuscode = 'COMMITED' and vcd_vadetno=" + c[1]);
			if (count > 1) {
				BaseUtil.showError("收料号单[" + c[0] + "],序号[" + c[1] + "]只能存在一张已提交未审核的变更单");
			}
		}
		check(vc_id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, vc_id);
		// 执行提交操作
		baseDao.submit("VerifyApplyChange", "vc_id=" + vc_id, "vc_status", "vc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "vc_id", vc_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, vc_id);
	}

	@Override
	public void resSubmitVerifyApplyChange(String caller, int vc_id) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("VerifyApplyChange", "vc_statuscode", "vc_id=" + vc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, vc_id);
		// 执行反提交操作
		baseDao.resOperate("VerifyApplyChange", "vc_id=" + vc_id, "vc_status", "vc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "vc_id", vc_id);
		handlerService.afterResSubmit(caller, vc_id);
	}
}
