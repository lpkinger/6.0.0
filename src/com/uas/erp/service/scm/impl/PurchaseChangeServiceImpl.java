package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.PurchaseChangeDao;
import com.uas.erp.dao.common.PurchaseDao;
import com.uas.erp.service.oa.SendMailService;
import com.uas.erp.service.scm.PurchaseChangeService;

@Service("purchaseChangeService")
public class PurchaseChangeServiceImpl implements PurchaseChangeService {

	private final static Logger logger = Logger.getLogger(PurchaseChangeServiceImpl.class);

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private PurchaseChangeDao purchaseChangeDao;
	@Autowired
	private PurchaseDao purchaseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private SendMailService sendMailService;

	@Override
	public void savePurchaseChange(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("PurchaseChange", "pc_code='" + store.get("pc_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT('采购单号：'||pu_code||'状态：'||pu_status) from Purchase where nvl(pu_statuscode,' ')<>'AUDITED' and pu_code=?",
				String.class, store.get("pc_purccode"));
		if (dets != null) {
			BaseUtil.showError("需要变更的采购单状态不等于已审核，不允许保存!" + dets);
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, grid });
		// 保存PurchaseChange
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PurchaseChange", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存PurchaseChangeDetail
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "PurchaseChangeDetail", "pcd_id");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "pc_id", store.get("pc_id"));
		baseDao.execute("update PurchaseChangeDetail set PCD_NEWPURCQTY=round(NVL(PCD_NEWQTY,0)*NVL(PCD_OLDPURCQTY,0)/NVL(PCD_OLDQTY,0),4) where pcd_pcid="
				+ store.get("pc_id")
				+ " and NVL(PCD_NEWPURCQTY,0)=0 AND NVL(PCD_NEWQTY,0)<>0 AND NVL(PCD_OLDQTY,0)<>0 AND NVL(PCD_OLDPURCQTY,0)<>0 and exists (select 1 from product where nvl(pcd_newprodcode,pcd_prodcode)=pr_code and nvl(pr_purcunit,pr_unit)<>pr_unit)");
		baseDao.execute("update PurchaseChangeDetail set PCD_NEWPURCQTY=NVL(PCD_NEWQTY,0) where pcd_pcid="
				+ store.get("pc_id")
				+ " and NVL(PCD_NEWQTY,0)<>0 and exists (select 1 from product where nvl(pcd_newprodcode,pcd_prodcode)=pr_code and nvl(pr_purcunit,pr_unit)=pr_unit)");
		baseDao.execute("UPDATE PURCHASECHANGEDETAIL SET PCD_NEWQTY=ROUND(NVL(PCD_OLDQTY,0)*NVL(PCD_NEWPURCQTY,0)/NVL(PCD_OLDPURCQTY,0),4) WHERE PCD_PCID="
				+ store.get("pc_id")
				+ " AND NVL(PCD_NEWQTY,0)=0 AND NVL(PCD_NEWPURCQTY,0)<>0 AND NVL(PCD_OLDQTY,0)<>0 AND NVL(PCD_OLDPURCQTY,0)<>0 and exists (select 1 from product where nvl(pcd_newprodcode,pcd_prodcode)=pr_code and nvl(pr_purcunit,pr_unit)<>pr_unit)");
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, grid });
	}

	@Override
	public void deletePurchaseChange(int pc_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("PurchaseChange", "pc_statuscode", "pc_id=" + pc_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { pc_id });
		// 删除PurchaseChange
		baseDao.deleteById("PurchaseChange", "pc_id", pc_id);
		// 删除PurchaseChangeDetail
		baseDao.deleteById("PurchaseChangedetail", "pcd_pcid", pc_id);
		// 记录操作
		baseDao.logger.delete(caller, "pc_id", pc_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { pc_id });
	}

	@Override
	public void updatePurchaseChangeById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("PurchaseChange", "pc_statuscode", "pc_id=" + store.get("pc_id"));
		StateAssert.updateOnlyEntering(status);
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT('采购单号：'||pu_code||'状态：'||pu_status) from Purchase where nvl(pu_statuscode,' ')<>'AUDITED' and pu_code=?",
				String.class, store.get("pc_purccode"));
		if (dets != null) {
			BaseUtil.showError("需要变更的采购单状态不等于已审核，不允许更新!" + dets);
		}
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		// 修改PurchaseChange
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PurchaseChange", "pc_id");
		baseDao.execute(formSql);
		// 修改PurchaseChangeDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "PurchaseChangeDetail", "pcd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("pcd_id") == null || s.get("pcd_id").equals("") || s.get("pcd_id").equals("0")
					|| Integer.parseInt(s.get("pcd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("PURCHASECHANGEDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "PurchaseChangeDetail", new String[] { "pcd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		baseDao.execute("update PurchaseChangeDetail set PCD_NEWPURCQTY=round(NVL(PCD_NEWQTY,0)*NVL(PCD_OLDPURCQTY,0)/NVL(PCD_OLDQTY,0),4) where pcd_pcid="
				+ store.get("pc_id")
				+ " and NVL(PCD_NEWPURCQTY,0)=0 AND NVL(PCD_NEWQTY,0)<>0 AND NVL(PCD_OLDQTY,0)<>0 AND NVL(PCD_OLDPURCQTY,0)<>0 and exists (select 1 from product where nvl(pcd_newprodcode,pcd_prodcode)=pr_code and nvl(pr_purcunit,pr_unit)<>pr_unit)");
		baseDao.execute("update PurchaseChangeDetail set PCD_NEWPURCQTY=NVL(PCD_NEWQTY,0) where pcd_pcid="
				+ store.get("pc_id")
				+ " AND NVL(PCD_NEWQTY,0)<>0 and exists (select 1 from product where nvl(pcd_newprodcode,pcd_prodcode)=pr_code and nvl(pr_purcunit,pr_unit)=pr_unit)");
		baseDao.execute("UPDATE PURCHASECHANGEDETAIL SET PCD_NEWQTY=ROUND(NVL(PCD_OLDQTY,0)*NVL(PCD_NEWPURCQTY,0)/NVL(PCD_OLDPURCQTY,0),4) WHERE PCD_PCID="
				+ store.get("pc_id")
				+ " AND NVL(PCD_NEWQTY,0)=0 AND NVL(PCD_NEWPURCQTY,0)<>0 AND NVL(PCD_OLDQTY,0)<>0 AND NVL(PCD_OLDPURCQTY,0)<>0 and exists (select 1 from product where nvl(pcd_newprodcode,pcd_prodcode)=pr_code and nvl(pr_purcunit,pr_unit)<>pr_unit)");
		// 记录操作
		baseDao.logger.update(caller, "pc_id", store.get("pc_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
	}

	@Override
	public String[] printPurchaseChange(int pc_id, String caller, String reportName, String condition) {
		// 执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[] { pc_id });
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 记录操作
		baseDao.logger.print(caller, "pc_id", pc_id);
		// 记录打印次数
		baseDao.updateByCondition("PurchaseChange", "pc_count=nvl(pc_count,0)+1", "pc_id=" + pc_id);
		// 执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[] { pc_id });
		return keys;
	}

	@Override
	public void auditPurchaseChange(int pc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("PurchaseChange", new String[] { "pc_statuscode", "pc_purccode" }, "pc_id="
				+ pc_id);
		StateAssert.auditOnlyCommited(status[0]);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(pcd_detno) from PurchaseChangeDetail where pcd_pcid=? and trunc(pcd_olddelivery) <> trunc(pcd_newdelivery) and trunc(pcd_newdelivery)<trunc(sysdate)",
						String.class, pc_id);
		if (dets != null) {
			BaseUtil.showError("新交货日期小于系统当前日期，不允许审核！行号：" + dets);
		}
		SqlRowList rs = baseDao.queryForRowSet("select pc_id,pc_purccode from PurchaseChange where pc_id=" + pc_id
				+ " and pc_newvendcode<>pc_vendcode ");
		if (rs.next()) {
			boolean bool = baseDao.checkIf("PurchaseNotify", "pn_ordercode='" + rs.getString("pc_purccode")
					+ "' and pn_statuscode<>'CANCELED'");
			if (bool) {
				BaseUtil.showError("采购单已投放了相应的进料需求不允许变更供应商!");
			}
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(pc_purccode) from PurchaseChange left join PurchaseChangedetail on pc_id=pcd_pcid where nvl(pc_statuscode,' ') in ('COMMITED','ENTERING') and pc_id<>?"
								+ " and (pc_purccode,pcd_pddetno) in (select pc_purccode,pcd_pddetno from  PurchaseChange left join PurchaseChangedetail on pc_id=pcd_pcid where pc_id=?)",
						String.class, pc_id, pc_id);
		if (dets != null) {
			BaseUtil.showError("采购单号+采购行号存在未审核的采购变更单，不允许审核!变更单号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(pcd_detno) from PurchaseChange left join PurchaseChangedetail on pc_id=pcd_pcid left join PurchaseDetail on pc_purccode=pd_code and pcd_pddetno=pd_detno where pc_id=? and nvl(pcd_newqty,0)<>nvl(pcd_oldqty,0) and (nvl(pcd_newqty,0)<nvl(pd_acceptqty,0) or nvl(pcd_newqty,0)<nvl(pd_yqty,0))",
						String.class, pc_id);
		if (dets != null) {
			BaseUtil.showError("新数量小于采购单明细已验收数量或者已转数量，不允许审核!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(pcd_detno) from PurchaseChange left join PurchaseChangedetail on pc_id=pcd_pcid left join PurchaseDetail on pc_purccode=pd_code and pcd_pddetno=pd_detno where pc_id=? and nvl(pcd_newqty,0)<>nvl(pcd_oldqty,0) and nvl(pcd_newqty,0)<nvl(pd_frozenqty,0)",
						String.class, pc_id);
		if (dets != null) {
			BaseUtil.showError("新数量小于采购单明细已冻结数量，不允许审核!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(distinct '采购单号：'||pc_purccode||'序号：'||pcd_pddetno) from PurchaseChangeDetail left join PurchaseChange on pcd_pcid=pc_id where (pc_purccode, pcd_pddetno) in (select vcd_pucode, vcd_pudetno from VerifyApplyChangeDetail left join VerifyApplyChange on vcd_vcid=vc_id where vc_statuscode<>'AUDITED') and pcd_pcid=?",
						String.class, pc_id);
		if (dets != null) {
			BaseUtil.showError("存在收料变更单未审核，不允许进行当前操作！" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(pcd_detno) from purchasechangedetail left join PurchaseChange on pcd_pcid=pc_id left join Purchase on pc_purccode=pu_code left join PurchaseDetail on pc_purccode=pd_code and pcd_pddetno=pd_detno left join PurchaseKind on pu_kind = pk_name where pcd_pcid=? and nvl(pcd_newprice,0) = 0 and pk_allownullprice=0 and pd_mark<>'备品'",
						String.class, pc_id);
		if (dets != null) {
			BaseUtil.showError("当前采购单的合同类型不允许0单价，新单价为0，不允许审核!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('采购单号：'||pc_purccode||'状态：'||pu_status) from PurchaseChange left join Purchase on pc_purccode=pu_code where nvl(pu_statuscode,' ')<>'AUDITED' and pc_id=?",
						String.class, pc_id);
		if (dets != null) {
			BaseUtil.showError("需要变更的采购单状态不等于已审核，不允许审核!" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('采购单号：'||pc_purccode||'，行号：'||pcd_pddetno) from purchasechangedetail left join purchasechange on pcd_pcid=pc_id where pc_id=? and nvl(pcd_prodcode,' ')<>nvl(pcd_newprodcode,' ') and exists (select 1 from PurchaseNotify where pc_purccode=pn_ordercode and pcd_pddetno=pn_orderdetno and pn_status||pn_sendstatus<>'已取消已上传')",
						String.class, pc_id);
		if (dets != null) {
			BaseUtil.showError("供应商送货通知已使用，不允许变更料号!：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('采购单号：'||pc_purccode||'，行号：'||pcd_pddetno) from purchasechangedetail left join purchasechange on pcd_pcid=pc_id where pc_id=? and nvl(pcd_prodcode,' ')<>nvl(pcd_newprodcode,' ') and exists (select 1 from VerifyApplyDetail where pc_purccode=vad_pucode and pcd_pddetno=vad_pudetno)",
						String.class, pc_id);
		if (dets != null) {
			BaseUtil.showError("收料单已使用，不允许变更料号!：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('采购单号：'||pc_purccode||'，行号：'||pcd_pddetno) from purchasechangedetail left join purchasechange on pcd_pcid=pc_id where pc_id=? and nvl(pcd_prodcode,' ')<>nvl(pcd_newprodcode,' ') and exists (select 1 from AcceptNotifyDetail where pc_purccode=and_ordercode and pcd_pddetno=and_orderdetno)",
						String.class, pc_id);
		if (dets != null) {
			BaseUtil.showError("收料通知单已使用，不允许变更料号!：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('采购单号：'||pc_purccode||'，行号：'||pcd_pddetno) from purchasechangedetail left join purchasechange on pcd_pcid=pc_id where pc_id=? and nvl(pcd_prodcode,' ')<>nvl(pcd_newprodcode,' ') and exists (select 1 from ProdIODetail where pc_purccode=pd_ordercode and pcd_pddetno=pd_orderdetno and pd_piclass in ('采购验收单','采购验退单','不良品入库单','不良品出库单'))",
						String.class, pc_id);
		if (dets != null) {
			BaseUtil.showError("出入库单已使用，不允许变更料号!：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('采购单号：'||pc_purccode||'，行号：'||pcd_pddetno) from purchasechangedetail left join purchasechange on pcd_pcid=pc_id where pc_id=? and nvl(pcd_prodcode,' ')<>nvl(pcd_newprodcode,' ') and exists (select 1 from QUA_VerifyApplyDetail where pc_purccode=ve_ordercode and pcd_pddetno=ve_orderdetno and ve_class ='采购检验单')",
						String.class, pc_id);
		if (dets != null) {
			BaseUtil.showError("采购检验单已使用，不允许变更料号!：" + dets);
		}
		if (baseDao.isDBSetting("PurchaseChange", "noAllowChange")) {
			// 已收料、已入库的情况下，不允许变更单价、税率
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select WM_CONCAT('采购单号：'||pc_purccode||'，行号：'||pcd_pddetno) from purchasechangedetail left join purchasechange on pcd_pcid=pc_id where pc_id=? and nvl(pcd_oldprice,0)<>nvl(pcd_newprice,0) and exists (select 1 from purchasedetail where pc_purccode=pd_code and pcd_pddetno=pd_detno and nvl(pd_yqty,0)>0)",
							String.class, pc_id);
			if (dets != null) {
				BaseUtil.showError("采购单已收料、已入库的情况下，不允许变更单价!：" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select WM_CONCAT('采购单号：'||pc_purccode||'，行号：'||pcd_pddetno) from purchasechangedetail left join purchasechange on pcd_pcid=pc_id where pc_id=? and nvl(pcd_taxrate,0)<>nvl(pcd_newtaxrate,0) and exists (select 1 from purchasedetail where pc_purccode=pd_code and pcd_pddetno=pd_detno and nvl(pd_yqty,0)>0)",
							String.class, pc_id);
			if (dets != null) {
				BaseUtil.showError("采购单已收料、已入库的情况下，不允许变更税率!：" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select WM_CONCAT('采购单号：'||pc_purccode||'，行号：'||pcd_pddetno) from purchasechangedetail left join purchasechange on pcd_pcid=pc_id where pc_id=? and nvl(pc_currency,0)<>nvl(pc_newcurrency,0) and exists (select 1 from purchasedetail where pc_purccode=pd_code and pcd_pddetno=pd_detno and nvl(pd_yqty,0)>0)",
							String.class, pc_id);
			if (dets != null) {
				BaseUtil.showError("采购单已收料、已入库的情况下，不允许变更币别!：" + dets);
			}
		}
		/**
		 * 限制如果存在已投放的进料需求 不允许变更供应商
		 * */
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(pc_purccode) from PurchaseChange,PurchaseNotify where pc_id=? and pn_ordercode=pc_purccode and pc_newvendcode<>pc_vendcode and pn_statuscode<>'CANCELED'",
						String.class, pc_id);
		if (dets != null) {
			BaseUtil.showError("采购单已投放了相应的进料需求不允许变更供应商，不允许审核!");
		}
		int countnum = baseDao.getCount("select count(*) from purchasechange where pc_id=" + pc_id + " and nvl(pc_needvendcheck,0)<>0");
		if (countnum > 0) {
			baseDao.execute("update purchasechange set pc_status='待确认',pc_statuscode='TO_CONFIRM',pc_auditman='"
					+ SystemSession.getUser().getEm_name() + "',pc_auditdate=sysdate,pc_agreed=null where pc_id=" + pc_id);
			baseDao.logger.audit(caller, "pc_id", pc_id);
		} else {
			// 限制如果存在已投放的进料需求 不允许变更供应商
			/**
			 * 更新已发送货通知数
			 * */
			purchaseDao.updatePurcYNotifyQTY(0,
					"select pd_id from purchasedetail where pd_code in (select pc_purccode from PurchaseChange where pc_id=" + pc_id + ")");
			rs = baseDao
					.queryForRowSet("select pcd_detno,pd_yqty,pd_turnqty,pd_frozenqty from PurchaseChange,PurchaseChangeDetail,purchasedetail where pcd_pcid="
							+ pc_id
							+ " and pc_id=pcd_pcid and pc_purccode=pd_code and pcd_pddetno=pd_detno and nvl(pd_yqty,0)+NVL(pd_turnqty,0)+nvl(pd_frozenqty,0)>nvl(pcd_newqty,0) and nvl(pcd_newqty,0)<>pd_qty ");
			if (rs.next()) {
				BaseUtil.showError("采购单序号[" + rs.getString("pcd_detno") + "]变更后的数量小于已收料数[" + rs.getString("pd_yqty") + "]+已投放送货通知数["
						+ rs.getString("pd_turnqty") + "]+已冻结数量[" + rs.getString("pd_frozenqty") + "]!");
			}
			// 执行审核前的其它逻辑
			handlerService.beforeAudit(caller, pc_id);
			// 信息自动反馈到采购单
			// try {
			// baseDao.procedure("scm_purc_change_audit", new Object[] { pc_id,
			// SystemSession.getUser().getEm_name() });
			// } catch (Exception ex) {
			// BaseUtil.showError(ex.getCause().getMessage());
			// }
			String pu_code = purchaseChangeDao.turnPurchase(pc_id);
			purchaseChangeDao.updatePurchaseStatus(pu_code);
			baseDao.audit("PurchaseChange", "pc_id=" + pc_id, "pc_status", "pc_statuscode", "pc_auditdate", "pc_auditman");

			// 记录操作
			baseDao.logger.audit(caller, "pc_id", pc_id);
			// 无需供应商确认，即已直接同意
			baseDao.execute("update purchasechange set pc_agreed=-1 where pc_id=?", pc_id);
			// 执行审核后的其它逻辑
			handlerService.afterAudit(caller, pc_id);
			int pu_id = baseDao.getFieldValue("Purchase", "nvl(pu_id,0)", "pu_code='" + pu_code + "'", Integer.class);
			// 是否配置审核自动发邮件
			// 过滤掉未维护UU号，并且未验证的供应商
			int count = baseDao
					.getCount("select count(*) from vendor where nvl(ve_uu,' ')<>' ' and nvl(ve_b2benable,0)=1 and ve_code=(select pu_vendcode from purchase where pu_id="
							+ pu_id + ")");
			// 是否配置审核自动发邮件
			if (baseDao.isDBSetting("Purchase", "AuditedAutoEmail") && count == 1) {
				purchase_audit_sendMail(pu_id);
			}
			BaseUtil.appendError("信息已自动反馈到采购单&nbsp;&nbsp;"
					+ "<a href=\"javascript:openUrl('jsps/scm/purchase/purchase.jsp?formCondition=pu_codeIS" + pu_code
					+ "&gridCondition=pd_codeIS" + pu_code + "')\">点击查看</a>&nbsp;");
		}
		baseDao.execute("update purchasechange set pc_sendstatus='待上传' where pc_id=?", pc_id);
	}

	public void purchase_audit_sendMail(Integer id) {
		Object email = baseDao.getFieldDataByCondition("Purchase left join Vendor on pu_vendcode=ve_code", "ve_email", "pu_id=" + id);
		if (email == null || "".equals(email.toString().trim()) || "null".equals(email.toString().trim())) {
			return;
			// BaseUtil.showError("供应商邮箱为空，无法发送邮件!");
		}
		// 标题和内容一致
		String encop = baseDao.getFieldDataByCondition("enterprise", "en_name", "1=1").toString();
		Object[] objs = baseDao.getFieldsDataByCondition("purchase", new String[] { "pu_code", "pu_vendname" }, "pu_id=" + id);
		String title = "采购订单有变更，请查看采购订单，订单编号：" + objs[0];
		String contextdetail = "<P class=MsoNormal style='MARGIN: 0cm 0cm 0pt'><SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'>"
				+ objs[1]
				+ "，您好！：<SPAN lang=EN-US><?xml:namespace prefix = 'o' ns = 'urn:schemas-microsoft-com:office:office' /><o:p></o:p></SPAN></SPAN></P>"
				+ "<P class=MsoNormal style='MARGIN: 0cm 0cm 0pt'><SPAN lang=EN-US style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'><SPAN style='mso-spacerun: yes'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				+ " </SPAN></SPAN><SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'>您有一张来自于<SPAN style='COLOR: blue'>公司名称（<SPAN lang=EN-US>"
				+ encop
				+ "</SPAN>）</SPAN>的变更后订单<SPAN lang=EN-US>(</SPAN>订单编号：<SPAN lang=EN-US style='COLOR: blue'>"
				+ objs[0]
				+ ")</SPAN>"
				+ "<SPAN lang=EN-US>,</SPAN>及时登入优软商务平台查取您的订单<SPAN lang=EN-US>!<o:p></o:p></SPAN></SPAN></P><P class=MsoNormal style='MARGIN: 0cm 0cm 0pt'><SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'>登入平台的地址：<SPAN lang=EN-US><A href='http://www.ubtob.com/'><FONT color=#0000ff>www.ubtob.com</FONT></A>"
				+ "<o:p></o:p></SPAN></SPAN></P><P class=MsoNormal style='MARGIN: 0cm 0cm 0pt'><SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'>如在使用平台过程中，遇到任何操作问题，请及时与深圳市优软科技有限公司客服人员（谭小姐）联系，联系电话：<SPAN lang=EN-US>0755-26996828<o:p></o:p></SPAN></SPAN></P>"
				+ "<SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑; mso-font-kerning: 1.0pt; mso-ansi-language: EN-US; mso-fareast-language: ZH-CN; mso-bidi-language: AR-SA'>致敬！</SPAN>";
		sendMailService.sendSysMail(title, contextdetail, email.toString());
	}

	public void onChangeAgreed(String pc_code) {
		Integer pc_id = baseDao.queryForObject("select pc_id from purchasechange where pc_code=?", Integer.class, pc_code);
		if (pc_id == null) {
			logger.error("平台回复变更单，无法找到变更单：" + pc_code);
		} else {
			// 信息自动反馈到采购单
			// baseDao.procedure("scm_purc_change_audit", new Object[] { pc_id,
			// SystemSession.getUser().getEm_name() });
			String pu_code = purchaseChangeDao.turnPurchase(pc_id);
			purchaseChangeDao.updatePurchaseStatus(pu_code);
			baseDao.audit("PurchaseChange", "pc_code='" + pc_code + "'", "pc_status", "pc_statuscode", "pc_auditdate", "pc_auditman");
		}
	}

	@Override
	public void resAuditPurchaseChange(int pc_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("PurchaseChange", "pc_statuscode", "pc_id=" + pc_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("PurchaseChange", "pc_id=" + pc_id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "pc_id", pc_id);
	}

	@Override
	public void submitPurchaseChange(int pc_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("PurchaseChange", "pc_statuscode", "pc_id=" + pc_id);
		StateAssert.submitOnlyEntering(status);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(pcd_detno) from PurchaseChangeDetail where pcd_pcid=? and trunc(pcd_olddelivery) <> trunc(pcd_newdelivery) and trunc(pcd_newdelivery)<trunc(sysdate)",
						String.class, pc_id);
		if (dets != null) {
			BaseUtil.showError("新交货日期小于系统当前日期，不允许提交！行号：" + dets);
		}
		//判断明细行是否存在多行采购序号相同，相同则限制提交
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject("select WM_CONCAT('('||detno||')') from (select WM_CONCAT(pcd_detno) detno from PurchaseChangeDetail "
						+ "where pcd_pcid=? group by pcd_pddetno having count(*)>1 order by min(pcd_detno))",String.class,pc_id);
		if(dets !=null) {
			BaseUtil.showError("存在相同采购序号的行，不能提交，行号"+dets);
		}
		// 判断是否需要供应商确认
		int countnum = baseDao
				.getCount("select count(*) from purchasechange left join purchase on pc_purccode=pu_code left join vendor on pu_vendcode=ve_code where  nvl(pc_needvendcheck,0)<>0 and (nvl(ve_uu,' ')=' ' or nvl(ve_b2benable,0)=0) and pc_id="
						+ pc_id);
		if (countnum > 0) {
			BaseUtil.showError("供应商未开通或者未检测通过，不需要供应商确认!");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(pc_code) from PurchaseChange left join PurchaseChangedetail on pc_id=pcd_pcid where nvl(pc_statuscode,' ') in ('COMMITED','ENTERING','TO_CONFIRM') and pc_id<>?"
								+ " and (pc_purccode,pcd_pddetno) in (select pc_purccode,pcd_pddetno from  PurchaseChange left join PurchaseChangedetail on pc_id=pcd_pcid where pc_id=?)",
						String.class, pc_id, pc_id);
		if (dets != null) {
			String[] pc_code = dets.split(",");
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < pc_code.length; i++) {
				Object pcid = baseDao.getFieldDataByCondition("PurchaseChange", "pc_id", "pc_code='" + pc_code[i] + "'");
				sb.append("<a href=\"javascript:openUrl('jsps/scm/purchase/purchaseChange.jsp?formCondition=pc_idIS" + pcid
						+ "&gridCondition=pcd_pcidIS" + pcid + "')\">" + pc_code[i] + "</a>&nbsp;");
			}
			BaseUtil.showError("采购单号+采购行号存在未审核或待确认的采购变更单，不允许提交!变更单号：" + sb);
		}

		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(pc_code) from purchasechangedetail left join purchasechange on pcd_pcid=pc_id LEFT JOIN PURCHASEDETAIL ON PD_CODE=PC_PURCCODE AND PD_DETNO=PCD_PDDETNO "
								+ "	LEFT JOIN APPLICATIONDETAIL ON NVL(PD_SOURCEDETAIL,0)=AD_ID WHERE pc_id<>? and nvl(pcd_oldqty,0)<>nvl(pcd_newqty,0) and nvl(pc_statuscode,' ') in ('COMMITED','ENTERING','TO_CONFIRM') "
								+ "	and	NVL(PD_SOURCEDETAIL,0)>0 and NVL(PD_SOURCEDETAIL,0) IN (SELECT AD_ID FROM PURCHASECHANGEDETAIL LEFT JOIN PURCHASECHANGE ON PCD_PCID=PC_ID "
								+ "	LEFT JOIN PURCHASEDETAIL ON PD_CODE=PC_PURCCODE AND PD_DETNO=PCD_PDDETNO LEFT JOIN APPLICATIONDETAIL ON NVL(PD_SOURCEDETAIL,0)=AD_ID WHERE  NVL(PD_SOURCEDETAIL,0)>0 "
								+ " AND PC_ID=? and nvl(pcd_oldqty,0)<>nvl(pcd_newqty,0))", String.class, pc_id, pc_id);
		if (dets != null) {
			String[] pc_code = dets.split(",");
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < pc_code.length; i++) {
				Object pcid = baseDao.getFieldDataByCondition("PurchaseChange", "pc_id", "pc_code='" + pc_code[i] + "'");
				sb.append("<a href=\"javascript:openUrl('jsps/scm/purchase/purchaseChange.jsp?formCondition=pc_idIS" + pcid
						+ "&gridCondition=pcd_pcidIS" + pcid + "')\">" + pc_code[i] + "</a>&nbsp;");
			}
			BaseUtil.showError("采购单号+行号存在请购来源相同的未审核或待确认的变更数量的采购变更单，不能提交！变更单号：" + sb);
		}

		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(pcd_detno) from PurchaseChange left join PurchaseChangedetail on pc_id=pcd_pcid left join PurchaseDetail on pc_purccode=pd_code and pcd_pddetno=pd_detno where pc_id=? and nvl(pcd_newqty,0)<>nvl(pcd_oldqty,0) and (nvl(pcd_newqty,0)<nvl(pd_acceptqty,0) or nvl(pcd_newqty,0)<nvl(pd_yqty,0))",
						String.class, pc_id);
		if (dets != null) {
			BaseUtil.showError("新数量小于采购单明细已验收数量或者已转数量，不允许提交!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(pcd_detno) from PurchaseChange left join PurchaseChangedetail on pc_id=pcd_pcid left join PurchaseDetail on pc_purccode=pd_code and pcd_pddetno=pd_detno where pc_id=? and nvl(pcd_newqty,0)<>nvl(pcd_oldqty,0) and nvl(pcd_newqty,0)<nvl(pd_frozenqty,0)",
						String.class, pc_id);
		if (dets != null) {
			BaseUtil.showError("新数量小于采购单明细已冻结数量，不允许提交!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(distinct '采购单号：'||pc_purccode||'，行号：'||pcd_pddetno) from PurchaseChangeDetail left join PurchaseChange on pcd_pcid=pc_id where (pc_purccode, pcd_pddetno) in (select vcd_pucode, vcd_pudetno from VerifyApplyChangeDetail left join VerifyApplyChange on vcd_vcid=vc_id where vc_statuscode<>'AUDITED') and pcd_pcid=?",
						String.class, pc_id);
		if (dets != null) {
			BaseUtil.showError("存在收料变更单未审核，不允许进行当前操作！" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('采购单号：'||pc_purccode||'，状态：'||pu_status) from PurchaseChange left join Purchase on pc_purccode=pu_code where nvl(pu_statuscode,' ')<>'AUDITED' and pc_id=?",
						String.class, pc_id);
		if (dets != null) {
			BaseUtil.showError("需要变更的采购单状态不等于已审核，不允许提交!" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(pcd_detno) from purchasechangedetail left join PurchaseChange on pcd_pcid=pc_id left join Purchase on pc_purccode=pu_code left join PurchaseDetail on pc_purccode=pd_code and pcd_pddetno=pd_detno left join PurchaseKind on pu_kind = pk_name where pcd_pcid=? and nvl(pcd_newprice,0) = 0 and pk_allownullprice=0 and pd_mark<>'备品'",
						String.class, pc_id);
		if (dets != null) {
			BaseUtil.showError("当前采购单的合同类型不允许0单价，新单价为0，不允许提交!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('采购单号：'||pc_purccode||'，行号：'||pcd_pddetno) from purchasechangedetail left join purchasechange on pcd_pcid=pc_id where pc_id=? and nvl(pcd_prodcode,' ')<>nvl(pcd_newprodcode,' ') and exists (select 1 from PurchaseNotify where pc_purccode=pn_ordercode and pcd_pddetno=pn_orderdetno  and pn_status||pn_sendstatus<>'已取消已上传')",
						String.class, pc_id);
		if (dets != null) {
			BaseUtil.showError("供应商送货通知已使用，不允许变更料号!：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('采购单号：'||pc_purccode||'，行号：'||pcd_pddetno) from purchasechangedetail left join purchasechange on pcd_pcid=pc_id where pc_id=? and nvl(pcd_prodcode,' ')<>nvl(pcd_newprodcode,' ') and exists (select 1 from VerifyApplyDetail where pc_purccode=vad_pucode and pcd_pddetno=vad_pudetno)",
						String.class, pc_id);
		if (dets != null) {
			BaseUtil.showError("收料单已使用，不允许变更料号!：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('采购单号：'||pc_purccode||'，行号：'||pcd_pddetno) from purchasechangedetail left join purchasechange on pcd_pcid=pc_id where pc_id=? and nvl(pcd_prodcode,' ')<>nvl(pcd_newprodcode,' ') and exists (select 1 from AcceptNotifyDetail where pc_purccode=and_ordercode and pcd_pddetno=and_orderdetno)",
						String.class, pc_id);
		if (dets != null) {
			BaseUtil.showError("收料通知单已使用，不允许变更料号!：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('采购单号：'||pc_purccode||'，行号：'||pcd_pddetno) from purchasechangedetail left join purchasechange on pcd_pcid=pc_id where pc_id=? and nvl(pcd_prodcode,' ')<>nvl(pcd_newprodcode,' ') and exists (select 1 from ProdIODetail where pc_purccode=pd_ordercode and pcd_pddetno=pd_orderdetno and pd_piclass in ('采购验收单','采购验退单','不良品入库单','不良品出库单'))",
						String.class, pc_id);
		if (dets != null) {
			BaseUtil.showError("出入库单已使用，不允许变更料号!：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('采购单号：'||pc_purccode||'，行号：'||pcd_pddetno) from purchasechangedetail left join purchasechange on pcd_pcid=pc_id where pc_id=? and nvl(pcd_prodcode,' ')<>nvl(pcd_newprodcode,' ') and exists (select 1 from QUA_VerifyApplyDetail where pc_purccode=ve_ordercode and pcd_pddetno=ve_orderdetno and ve_class ='采购检验单')",
						String.class, pc_id);
		if (dets != null) {
			BaseUtil.showError("采购检验单已使用，不允许变更料号!：" + dets);
		}
		if (baseDao.isDBSetting("PurchaseChange", "noAllowChange")) {
			// 已收料、已入库的情况下，不允许变更单价、税率
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select WM_CONCAT('采购单号：'||pc_purccode||'，行号：'||pcd_pddetno) from purchasechangedetail left join purchasechange on pcd_pcid=pc_id where pc_id=? and nvl(pcd_oldprice,0)<>nvl(pcd_newprice,0) and exists (select 1 from purchasedetail where pc_purccode=pd_code and pcd_pddetno=pd_detno and nvl(pd_yqty,0)>0)",
							String.class, pc_id);
			if (dets != null) {
				BaseUtil.showError("采购单已收料、已入库的情况下，不允许变更单价!：" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select WM_CONCAT('采购单号：'||pc_purccode||'，行号：'||pcd_pddetno) from purchasechangedetail left join purchasechange on pcd_pcid=pc_id where pc_id=? and nvl(pcd_taxrate,0)<>nvl(pcd_newtaxrate,0) and exists (select 1 from purchasedetail where pc_purccode=pd_code and pcd_pddetno=pd_detno and nvl(pd_yqty,0)>0)",
							String.class, pc_id);
			if (dets != null) {
				BaseUtil.showError("采购单已收料、已入库的情况下，不允许变更税率!：" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select WM_CONCAT('采购单号：'||pc_purccode||'，行号：'||pcd_pddetno) from purchasechangedetail left join purchasechange on pcd_pcid=pc_id where pc_id=? and nvl(pc_currency,0)<>nvl(pc_newcurrency,0) and exists (select 1 from purchasedetail where pc_purccode=pd_code and pcd_pddetno=pd_detno and nvl(pd_yqty,0)>0)",
							String.class, pc_id);
			if (dets != null) {
				BaseUtil.showError("采购单已收料、已入库的情况下，不允许变更币别!：" + dets);
			}
		}
		/**
		 * 限制如果存在已投放的进料需求 不允许变更供应商
		 * */
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(pc_purccode) from PurchaseChange,PurchaseNotify where pc_id=? and pn_ordercode=pc_purccode and pc_newvendcode<>pc_vendcode and pn_statuscode<>'CANCELED'",
						String.class, pc_id);
		if (dets != null) {
			BaseUtil.showError("采购单已投放了相应的进料需求不允许变更供应商，不允许提交!");
		}
		/**
		 * 更新已发送货通知数
		 * */
		purchaseDao.updatePurcYNotifyQTY(0,
				"select pd_id from purchasedetail where pd_code in (select pc_purccode from PurchaseChange where pc_id=" + pc_id + ")");
		SqlRowList rs = baseDao
				.queryForRowSet("select pcd_detno,pd_yqty,pd_turnqty,pd_frozenqty from PurchaseChange,PurchaseChangeDetail,purchasedetail where pcd_pcid="
						+ pc_id
						+ " and pc_id=pcd_pcid and pc_purccode=pd_code and pcd_pddetno=pd_detno and nvl(pd_yqty,0)+NVL(pd_turnqty,0)+nvl(pd_frozenqty,0)>nvl(pcd_newqty,0) and nvl(pcd_newqty,0)<>pd_qty ");
		if (rs.next()) {
			BaseUtil.showError("变更单序号[" + rs.getString("pcd_detno") + "]变更后的数量小于已收料数[" + rs.getString("pd_yqty") + "]+已投放送货通知数["
					+ rs.getString("pd_turnqty") + "]+已冻结数量[" + rs.getString("pd_frozenqty") + "]!");
		}
		// 只能选择已审核的物料!
		List<Object[]> codes = baseDao.getFieldsDatasByCondition("PurchaseChangeDetail", new String[] { "pcd_newprodcode", "pcd_newqty",
				"pcd_pddetno" }, "pcd_pcid=" + pc_id);
		for (Object[] c : codes) {
			status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_code='" + c[0] + "'");
			if (!status.equals("AUDITED")) {
				BaseUtil.showError(BaseUtil.getLocalMessage("product_onlyAudited")
						+ "<a href=\"javascript:openUrl('jsps/scm/product/product.jsp?formCondition=pr_codeIS" + c[0] + "')\">" + c[0]
						+ "</a>&nbsp;");
			}
		}
		checkApplicationqty(pc_id);
		allowZeroTax(caller, pc_id);
		String remark = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat('行'||pcd_detno||':'||err_info||' ') from (select pcd_detno,"
						+ " case when pcd_newprodcode<>pcd_prodcode then '物料.' else '' end ||"
						+ " case when pcd_newprice<>pcd_oldprice then '单价.' else '' end ||"
						+ " case when pcd_newqty<>pcd_oldqty then '数量.' else '' end ||"
						+ " case when pcd_newdelivery<>pcd_olddelivery then '交货日期.' else '' end ||"
						+ " case when pcd_newtaxrate<>pcd_taxrate then '税率.' else '' end err_info "
						+ " from PurchaseChangeDetail  where pcd_pcid=?) where err_info is not null", String.class, pc_id);
		if (remark != null) {
			baseDao.execute("update PurchaseChange set pc_description=? where pc_id=?", remark, pc_id);
		}
		/**
		 * @author wsy 反馈编号：2017040323
		 */
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject("select  wm_concat(pcd_pddetno) from purchasechangedetail left join purchasechange on pcd_pcid=pc_id "
						+ " left join purchasedetail on pd_code=pc_purccode and pd_detno=pcd_pddetno "
						+ " left join applicationdetail on ad_id=pd_sourcedetail left join application on ad_apid=ap_id "
						+ " where pc_id= ?  and nvl(pc_application,0)=1 and nvl(pcd_oldqty,0)<>nvl(pcd_newqty,0) "
						+ " and  (nvl(ap_status,' ')='已结案' or nvl(ad_status,' ')='已结案')",
						String.class, pc_id);
		if (dets != null) {
			BaseUtil.showError("变更明细行:" + dets + " 有关联的请购明细已结案，不允许变更请购单已转数！");
		}
		/*Object pc_application = baseDao.getFieldDataByCondition("PurchaseChange", "pc_application", "pc_id=" + pc_id);
		if ("1".equals(pc_application.toString())) {
			SqlRowList rst = baseDao.queryForRowSet("select * from PurchaseChangeDetail where pcd_pcid=" + pc_id);
			if (rst.next()) {
				if (!rst.getString("pcd_oldqty").equals(rst.getString("pcd_newqty"))) {
					Object pc_purcid = baseDao.getFieldDataByCondition("PurchaseChange ", "pc_purcid", "pc_id=" + pc_id);
					SqlRowList sqlRow = baseDao.queryForRowSet("select * from PurchaseDetail where pd_puid=?", pc_purcid);
					while (sqlRow.next()) {
						Object ap_status = baseDao.getFieldDataByCondition("Application", "ap_status",
								"ap_code='" + sqlRow.getString("pd_sourcecode") + "'");
						if ("已结案".equals(ap_status)) {
							BaseUtil.showError("变更明细行:" + rst.getString("pcd_detno") + " 有关联的请购明细已结案，不允许变更请购单已转数！");
						}
					}
				}
			}
		}*/
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { pc_id });
		// 执行提交操作
		baseDao.submit("PurchaseChange", "pc_id=" + pc_id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pc_id", pc_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { pc_id });
	}

	@Override
	public void resSubmitPurchaseChange(int pc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("PurchaseChange", "pc_statuscode", "pc_id=" + pc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { pc_id });
		// 执行反提交操作
		baseDao.resOperate("PurchaseChange", "pc_id=" + pc_id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pc_id", pc_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { pc_id });
	}

	@Override
	public void needCheck(Integer changeId) {
		baseDao.execute("update purchasechange set pc_needvendcheck=-1 where pc_id=?", changeId);
	}

	// 本位币允许税率为0
	private void allowZeroTax(String caller, Object pc_id) {
		if (!baseDao.isDBSetting("Purchase", "allowZeroTax")) {
			String currency = baseDao.getDBSetting("defaultCurrency");
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select WM_CONCAT(pcd_detno) from PurchaseChangeDetail left join PurchaseChange on pcd_pcid=pc_id where nvl(pcd_newtaxrate,0)=0 and nvl(pcd_newprice,0)<>0 and pc_newcurrency='"
									+ currency + "' and pcd_pcid=?", String.class, pc_id);
			if (dets != null) {
				BaseUtil.showError("本位币税率为0，不允许提交!行号：" + dets);
			}
		}
	}

	private void checkApplicationqty(Object pc_id) {
		if (!baseDao.isDBSetting("PurchaseChange", "allowADqty")) {
			// 判断数量是否超出请购数
			SqlRowList rs = baseDao.queryForRowSet("SELECT * FROM purchasechangedetail left join PurchaseChange "
					+ "on pc_id=pcd_pcid left join PurchaseDetail on pc_purccode=pd_code and pcd_pddetno=pd_detno " + "WHERE pcd_pcid=?",
					new Object[] { pc_id });
			Object ad_id = null;
			Object qty = 0;
			Object aq = 0;
			while (rs.next()) {
				ad_id = baseDao.getFieldDataByCondition("PurchaseDetail", "pd_sourcedetail", "pd_code='" + rs.getString("pc_purccode")
						+ "' AND pd_detno=" + rs.getObject("pcd_pddetno"));
				if (ad_id != null && Integer.parseInt(ad_id.toString()) > 0) {
					qty = baseDao.getFieldDataByCondition("PurchaseDetail", "sum(pd_qty+nvl(pd_cancelqty,0))", "pd_sourcedetail=" + ad_id
							+ " AND (pd_code <> '" + rs.getString("pc_purccode") + "' or (pd_code='" + rs.getString("pc_purccode")
							+ "' and pd_detno<>" + rs.getObject("pcd_pddetno") + "))");
					qty = qty == null ? 0 : qty;
					aq = baseDao.getFieldDataByCondition("ApplicationDetail", "ad_qty", "ad_id=" + ad_id);
					aq = aq == null ? 0 : aq;
					if (Double.parseDouble(aq.toString()) != 0) {
						if (Double.parseDouble(aq.toString()) < Double.parseDouble(qty.toString()) + rs.getGeneralDouble("pcd_newqty") && !baseDao.isDBSetting("Purchase","AllowOut")) {
							BaseUtil.showError("新数量超出原请购数,超出数量:"
									+ (Double.parseDouble(qty.toString()) + rs.getGeneralDouble("pcd_newqty") - Double.parseDouble(aq
											.toString())));
						}
					}
				}
			}
		}
	}
}
