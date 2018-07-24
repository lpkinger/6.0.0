package com.uas.erp.service.pm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.AppMouldDao;
import com.uas.erp.dao.common.PriceMouldDao;
import com.uas.erp.service.pm.PriceMouldService;

@Service("priceMouldService")
public class PriceMouldServiceImpl implements PriceMouldService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private PriceMouldDao priceMouldDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private AppMouldDao appMouldDao;

	@Override
	public void savePriceMould(String formStore, String gridStore, String gridStore2, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> grid2 = BaseUtil.parseGridStoreToMaps(gridStore2);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("PriceMould", "pd_code='" + store.get("pd_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存PriceMould
		baseDao.execute(SqlUtil.getInsertSqlByFormStore(store, "PriceMould", new String[] {}, new Object[] {}));
		// 保存PriceMouldDet
		baseDao.execute(SqlUtil.getInsertSqlbyList(grid, "PriceMouldDet", "pdd_id"));
		// 保存PriceMouldDetail
		baseDao.execute(SqlUtil.getInsertSqlbyList(grid2, "PriceMouldDetail", "pmd_id"));
		getdetaiprice(store.get("pd_id"));
		// 记录操作
		baseDao.logger.save(caller, "pd_id", store.get("pd_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void deletePriceMould(int pd_id, String caller) {
		// 只能删除在录入的单据!
		Object[] status = baseDao.getFieldsDataByCondition("PriceMould", new String[] { "pd_statuscode", "pd_appmouldcode" }, "pd_id="
				+ pd_id);
		StateAssert.delOnlyEntering(status[0]);
		String dets = baseDao.queryForObject("select WM_CONCAT(in_code) from InquiryMould where in_sourcetype='模具报价单' and in_sourceid="
				+ pd_id, String.class);
		if (dets != null) {
			BaseUtil.showError("该模具报价单存在模具询价单，不允许删除！询价单号：" + dets);
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { pd_id });
		SqlRowList rs = baseDao.queryForRowSet("select pdd_adid from PriceMouldDet where pdd_pdid=? and nvl(pdd_adid,0)<>0",
				new Object[] { pd_id });
		// 删除PriceMould
		baseDao.deleteById("PriceMould", "pd_id", pd_id);
		// 删除PriceMouldDetail
		baseDao.deleteById("PriceMoulddetail", "pmd_pdid", pd_id);
		// 删除PriceMouldDet
		baseDao.deleteById("PriceMouldDet", "pdd_pdid", pd_id);
		// 删除之后还原开模申请单状态
		while (rs.next()) {
			int ad_id = rs.getGeneralInt("pdd_adid");
			baseDao.execute("update AppMouldDetail set ad_statuscode=null, ad_status=null where ad_id=" + ad_id);
			appMouldDao.checkAdQty(ad_id);
		}
		int i = baseDao.getCountByCondition("PriceMould", "pd_sourcecode='" + status[1] + "' and pd_sourcetype='模具修改申请单'");
		if (i == 0) {
			baseDao.updateByCondition("MOD_ALTER", "al_turnpricecode=null,al_turnprice=null", "al_code='" + status[1] + "'");
		}
		// 记录操作
		baseDao.logger.delete(caller, "pd_id", pd_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { pd_id });
	}

	private void getdetaiprice(Object pd_id) {
		baseDao.execute("update PRICEMOULDDETAIL set pmd_code=(select pd_code from PRICEMOULD where pmd_pdid=pd_id) where pmd_pdid="
				+ pd_id + " and not exists (select 1 from PRICEMOULD where pmd_code=pd_code)");
		baseDao.execute("update PRICEMOULDDET set pdd_code=(select pd_code from PRICEMOULD where pdd_pdid=pd_id) where pdd_pdid=" + pd_id
				+ " and not exists (select 1 from PRICEMOULD where pdd_code=pd_code)");
		baseDao.execute("update PRICEMOULD set PD_MMPRICE1=nvl((select sum(nvl(PMD_PRODPRICE1,0)) from PRICEMOULDDETAIL where pmd_pdid=pd_id),0) where pd_id="
				+ pd_id);
		baseDao.execute("update PRICEMOULD set PD_MMPRICE2=nvl((select sum(nvl(PMD_PRODPRICE2,0)) from PRICEMOULDDETAIL where pmd_pdid=pd_id),0) where pd_id="
				+ pd_id);
		baseDao.execute("update PRICEMOULD set PD_MMPRICE3=nvl((select sum(nvl(PMD_PRODPRICE3,0)) from PRICEMOULDDETAIL where pmd_pdid=pd_id),0) where pd_id="
				+ pd_id);
		baseDao.execute("update PRICEMOULD set PD_MMPRICE4=nvl((select sum(nvl(PMD_PRODPRICE4,0)) from PRICEMOULDDETAIL where pmd_pdid=pd_id),0) where pd_id="
				+ pd_id);
		baseDao.execute("update PRICEMOULD set PD_MMPRICE5=nvl((select sum(nvl(PMD_PRODPRICE5,0)) from PRICEMOULDDETAIL where pmd_pdid=pd_id),0) where pd_id="
				+ pd_id);
		baseDao.execute("update PRICEMOULD set pd_mouldprice1=nvl((select sum(nvl(PDD_PRICE1,0)) from PRICEMOULDDET where pdd_pdid=pd_id),0) where pd_id="
				+ pd_id);
		baseDao.execute("update PRICEMOULD set pd_mouldprice2=nvl((select sum(nvl(PDD_PRICE2,0)) from PRICEMOULDDET where pdd_pdid=pd_id),0) where pd_id="
				+ pd_id);
		baseDao.execute("update PRICEMOULD set pd_mouldprice3=nvl((select sum(nvl(PDD_PRICE3,0)) from PRICEMOULDDET where pdd_pdid=pd_id),0) where pd_id="
				+ pd_id);
		baseDao.execute("update PRICEMOULD set pd_mouldprice4=nvl((select sum(nvl(PDD_PRICE4,0)) from PRICEMOULDDET where pdd_pdid=pd_id),0) where pd_id="
				+ pd_id);
		baseDao.execute("update PRICEMOULD set pd_mouldprice5=nvl((select sum(nvl(PDD_PRICE5,0)) from PRICEMOULDDET where pdd_pdid=pd_id),0) where pd_id="
				+ pd_id);
	}

	@Override
	public void updatePriceMouldById(String formStore, String gridStore, String gridStore2, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> gstore2 = BaseUtil.parseGridStoreToMaps(gridStore2);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("PriceMould", "pd_statuscode", "pd_id=" + store.get("pd_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		SqlRowList rs = baseDao.queryForRowSet("SELECT * FROM PriceMould where pd_id=" + store.get("pd_id"));
		StringBuffer sb = new StringBuffer();
		String dets = null;
		if (rs.next()) {
			Object newvend1 = store.get("pd_vend1");
			Object newvend2 = store.get("pd_vend2");
			Object newvend3 = store.get("pd_vend3");
			Object newvend4 = store.get("pd_vend4");
			Object newvend5 = store.get("pd_vend5");
			if (StringUtil.hasText(newvend1) && !newvend1.equals(rs.getObject("pd_vend1"))) {
				dets = baseDao.queryForObject("select WM_CONCAT(in_code) from InquiryMould where in_sourcetype='模具报价单' and in_source='"
						+ store.get("pd_code") + "' and in_vendcode='" + rs.getObject("pd_vend1") + "'", String.class);
				if (dets != null) {
					sb.append("该模具报价单供应商[" + rs.getGeneralString("pd_vend1") + "]已转入模具询价单，不允许修改！询价单号：" + dets).append("<hr>");
				}
			}
			if (StringUtil.hasText(newvend2) && !newvend2.equals(rs.getObject("pd_vend2"))) {
				dets = baseDao.queryForObject("select WM_CONCAT(in_code) from InquiryMould where in_sourcetype='模具报价单' and in_source='"
						+ store.get("pd_code") + "' and in_vendcode='" + rs.getObject("pd_vend2") + "'", String.class);
				if (dets != null) {
					sb.append("该模具报价单供应商[" + rs.getGeneralString("pd_vend2") + "]已转入模具询价单，不允许修改！询价单号：" + dets).append("<hr>");
				}
			}
			if (StringUtil.hasText(newvend3) && !newvend3.equals(rs.getObject("pd_vend3"))) {
				dets = baseDao.queryForObject("select WM_CONCAT(in_code) from InquiryMould where in_sourcetype='模具报价单' and in_source='"
						+ store.get("pd_code") + "' and in_vendcode='" + rs.getObject("pd_vend3") + "'", String.class);
				if (dets != null) {
					sb.append("该模具报价单供应商[" + rs.getGeneralString("pd_vend3") + "]已转入模具询价单，不允许修改！询价单号：" + dets).append("<hr>");
				}
			}
			if (StringUtil.hasText(newvend4) && !newvend4.equals(rs.getObject("pd_vend4"))) {
				dets = baseDao.queryForObject("select WM_CONCAT(in_code) from InquiryMould where in_sourcetype='模具报价单' and in_source='"
						+ store.get("pd_code") + "' and in_vendcode='" + rs.getObject("pd_vend4") + "'", String.class);
				if (dets != null) {
					sb.append("该模具报价单供应商[" + rs.getGeneralString("pd_vend4") + "]已转入模具询价单，不允许修改！询价单号：" + dets).append("<hr>");
				}
			}
			if (StringUtil.hasText(newvend5) && !newvend5.equals(rs.getObject("pd_vend5"))) {
				dets = baseDao.queryForObject("select WM_CONCAT(in_code) from InquiryMould where in_sourcetype='模具报价单' and in_source='"
						+ store.get("pd_code") + "' and in_vendcode='" + rs.getObject("pd_vend5") + "'", String.class);
				if (dets != null) {
					sb.append("该模具报价单供应商[" + rs.getGeneralString("pd_vend5") + "]已转入模具询价单，不允许修改！询价单号：" + dets).append("<hr>");
				}
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}
		// 修改PriceMould
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "PriceMould", "pd_id"));
		// 修改PriceMouldDet
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "PriceMouldDet", "pdd_id"));
		// 修改PriceMouldDetail
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore2, "PriceMouldDetail", "pmd_id"));
		getdetaiprice(store.get("pd_id"));
		// 记录操作
		baseDao.logger.update(caller, "pd_id", store.get("pd_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void printPriceMould(int pd_id, String caller) {
		// 只能打印审核后的单据!
		Object status = baseDao.getFieldDataByCondition("PriceMould", "pd_statuscode", "pd_id=" + pd_id);
		StateAssert.printOnlyAudited(status);
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, new Object[] { pd_id });
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "pd_id", pd_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, new Object[] { pd_id });
	}

	@Override
	public void auditPriceMould(int pd_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("PriceMould", "pd_statuscode", "pd_id=" + pd_id);
		StateAssert.auditOnlyCommited(status);
		SqlRowList rs = baseDao.queryForRowSet("SELECT * FROM PriceMould where pd_id=" + pd_id);
		if (rs.next()) {
			String vendcode = rs.getGeneralString("pd_vendcode");
			if (!"".equals(vendcode)) {
				if (!vendcode.equals(rs.getGeneralString("pd_vend1")) && !vendcode.equals(rs.getGeneralString("pd_vend2"))
						&& !vendcode.equals(rs.getGeneralString("pd_vend3")) && !vendcode.equals(rs.getGeneralString("pd_vend4"))
						&& !vendcode.equals(rs.getGeneralString("pd_vend5"))) {
					BaseUtil.showError("必须在五个供应商中选定!");
				}
			} else {
				BaseUtil.showError("请先选定采购供应商!");
			}
			SqlRowList pd = baseDao.queryForRowSet(
					"select in_id,in_vendcode from InquiryMould where nvl(in_sourceid,0)=? and nvl(in_sourcetype,' ')='模具报价单'",
					new Object[] { pd_id });
			while (pd.next()) {
				if (vendcode.equals(pd.getGeneralString("in_vendcode"))) {
					baseDao.execute("update InquiryMould set in_adoptstatus='已采纳',in_checksendstatus='待上传' where in_id="
							+ pd.getGeneralInt("in_id"));
				} else {
					baseDao.execute("update InquiryMould set in_adoptstatus='未采纳',in_checksendstatus='待上传' where in_id="
							+ pd.getGeneralInt("in_id"));
				}
			}
			String dets = baseDao.queryForObject("select WM_CONCAT(in_code) from InquiryMould where in_sourcetype='模具报价单' and in_source='"
					+ rs.getObject("pd_code") + "' and in_statuscode='NULLIFIED' and in_adoptstatus='已采纳'", String.class);
			if (dets != null) {
				BaseUtil.showError("该模具报价单采纳的询价单[" + dets + "]已作废，不允许审核！");
			}
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { pd_id });
		// 执行审核操作
		baseDao.audit("PriceMould", "pd_id=" + pd_id, "pd_status", "pd_statuscode", "pd_auditdate", "pd_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "pd_id", pd_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { pd_id });
	}

	@Override
	public void resAuditPriceMould(int pd_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("PriceMould", "pd_statuscode", "pd_id=" + pd_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("PriceMould", "pd_id=" + pd_id, "pd_status", "pd_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "pd_id", pd_id);
	}

	@Override
	public void submitPriceMould(int pd_id, String caller) {
		getdetaiprice(pd_id);
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("PriceMould", "pd_statuscode", "pd_id=" + pd_id);
		StateAssert.submitOnlyEntering(status);
		SqlRowList rs = baseDao.queryForRowSet("SELECT * FROM PriceMould where pd_id=" + pd_id);
		StringBuffer sb = new StringBuffer();
		if (rs.next()) {
			String dets = null;
			Object[] in = null;
			Object vend1 = rs.getObject("pd_vend1");
			Object vend2 = rs.getObject("pd_vend2");
			Object vend3 = rs.getObject("pd_vend3");
			Object vend4 = rs.getObject("pd_vend4");
			Object vend5 = rs.getObject("pd_vend5");

			if (StringUtil.hasText(vend1)) {
				in = baseDao.getFieldsDataByCondition("InquiryMould", new String[] { "in_code", "in_statuscode" }, "in_sourceid=" + pd_id
						+ " and in_sourcetype='模具报价单' and in_vendcode='" + vend1 + "'");
				dets = baseDao.queryForObject("select WM_CONCAT(pdd_detno) from PriceMouldDet where pdd_pdid=? and nvl(pdd_price1,0)=0",
						String.class, pd_id);
				if (dets != null) {
					if (in != null) {
						if (!"NULLIFIED".equals(in[1])) {
							sb.append("供应商1[" + vend1 + "]存在有效的询价单[" + in[0] + "]，明细一开模价1不能为空！行号" + dets).append("<hr>");
						} else {
							sb.append("供应商1[" + vend1 + "]，明细一开模价1不能为空！行号" + dets).append("<hr>");
						}
					} else {
						sb.append("供应商1[" + vend1 + "]，明细一开模价1不能为空！行号" + dets).append("<hr>");
					}

				}
				dets = baseDao.queryForObject(
						"select WM_CONCAT(pmd_detno) from PriceMouldDetail where pmd_pdid=? and nvl(pmd_prodprice1,0)=0", String.class,
						pd_id);
				if (dets != null) {
					if (in != null) {
						if (!"NULLIFIED".equals(in[1])) {
							sb.append("供应商1[" + vend1 + "]存在有效的询价单[" + in[0] + "]，明细二材料价1不能为空！行号" + dets).append("<hr>");
						} else {
							sb.append("供应商1[" + vend1 + "]，明细二材料价1不能为空！行号" + dets).append("<hr>");
						}
					} else {
						sb.append("供应商1[" + vend1 + "]，明细二材料价1不能为空！行号" + dets).append("<hr>");
					}
				}
			} else if (!StringUtil.hasText(vend1)) {
				dets = baseDao.queryForObject("select WM_CONCAT(pdd_detno) from PriceMouldDet where pdd_pdid=? and nvl(pdd_price1,0)<>0",
						String.class, pd_id);
				if (dets != null) {
					sb.append("供应商1为空，明细一开模价1不能填写！行号" + dets).append("<hr>");
				}
				dets = baseDao.queryForObject(
						"select WM_CONCAT(pmd_detno) from PriceMouldDetail where pmd_pdid=? and nvl(pmd_prodprice1,0)<>0", String.class,
						pd_id);
				if (dets != null) {
					sb.append("供应商1为空，明细二材料价1不能填写！行号" + dets).append("<hr>");
				}
			}
			if (StringUtil.hasText(vend2)) {
				in = baseDao.getFieldsDataByCondition("InquiryMould", new String[] { "in_code", "in_statuscode" }, "in_sourceid=" + pd_id
						+ " and in_sourcetype='模具报价单' and in_vendcode='" + vend2 + "'");
				dets = baseDao.queryForObject("select WM_CONCAT(pdd_detno) from PriceMouldDet where pdd_pdid=? and nvl(pdd_price2,0)=0",
						String.class, pd_id);
				if (dets != null) {
					if (in != null) {
						if (!"NULLIFIED".equals(in[1])) {
							sb.append("供应商2[" + vend2 + "]存在有效的询价单[" + in[0] + "]，明细一开模价2不能为空！行号" + dets).append("<hr>");
						} else {
							sb.append("供应商2[" + vend2 + "]，明细一开模价2不能为空！行号" + dets).append("<hr>");
						}
					} else {
						sb.append("供应商2[" + vend2 + "]，明细一开模价2不能为空！行号" + dets).append("<hr>");
					}
				}
				dets = baseDao.queryForObject(
						"select WM_CONCAT(pmd_detno) from PriceMouldDetail where pmd_pdid=? and nvl(pmd_prodprice2,0)=0", String.class,
						pd_id);
				if (dets != null) {
					if (in != null) {
						if (!"NULLIFIED".equals(in[1])) {
							sb.append("供应商2[" + vend2 + "]存在有效的询价单[" + in[0] + "]，明细二材料价2不能为空！行号" + dets).append("<hr>");
						} else {
							sb.append("供应商2[" + vend2 + "]，明细二材料价2不能为空！行号" + dets).append("<hr>");
						}
					} else {
						sb.append("供应商2[" + vend2 + "]，明细二材料价2不能为空！行号" + dets).append("<hr>");
					}
				}
			} else if (!StringUtil.hasText(vend2)) {
				dets = baseDao.queryForObject("select WM_CONCAT(pdd_detno) from PriceMouldDet where pdd_pdid=? and nvl(pdd_price2,0)<>0",
						String.class, pd_id);
				if (dets != null) {
					sb.append("供应商2为空，明细一开模价2不能填写！行号" + dets).append("<hr>");
				}
				dets = baseDao.queryForObject(
						"select WM_CONCAT(pmd_detno) from PriceMouldDetail where pmd_pdid=? and nvl(pmd_prodprice2,0)<>0", String.class,
						pd_id);
				if (dets != null) {
					sb.append("供应商2为空，明细二材料价2不能填写！行号" + dets).append("<hr>");
				}
			}
			if (StringUtil.hasText(vend3)) {
				in = baseDao.getFieldsDataByCondition("InquiryMould", new String[] { "in_code", "in_statuscode" }, "in_sourceid=" + pd_id
						+ " and in_sourcetype='模具报价单' and in_vendcode='" + vend3 + "'");
				dets = baseDao.queryForObject("select WM_CONCAT(pdd_detno) from PriceMouldDet where pdd_pdid=? and nvl(pdd_price3,0)=0",
						String.class, pd_id);
				if (dets != null) {
					if (in != null) {
						if (!"NULLIFIED".equals(in[1])) {
							sb.append("供应商3[" + vend3 + "]存在有效的询价单[" + in[0] + "]，明细一开模价3不能为空！行号" + dets).append("<hr>");
						} else {
							sb.append("供应商3[" + vend3 + "]，明细一开模价3不能为空！行号" + dets).append("<hr>");
						}
					} else {
						sb.append("供应商3[" + vend3 + "]，明细一开模价3不能为空！行号" + dets).append("<hr>");
					}
				}
				dets = baseDao.queryForObject(
						"select WM_CONCAT(pmd_detno) from PriceMouldDetail where pmd_pdid=? and nvl(pmd_prodprice3,0)=0", String.class,
						pd_id);
				if (dets != null) {
					if (in != null) {
						if (!"NULLIFIED".equals(in[1])) {
							sb.append("供应商3[" + vend3 + "]存在有效的询价单[" + in[0] + "]，明细二材料价3不能为空！行号" + dets).append("<hr>");
						} else {
							sb.append("供应商3[" + vend3 + "]，明细二材料价3不能为空！行号" + dets).append("<hr>");
						}
					} else {
						sb.append("供应商3[" + vend3 + "]，明细二材料价3不能为空！行号" + dets).append("<hr>");
					}
				}
			} else if (!StringUtil.hasText(vend3)) {
				dets = baseDao.queryForObject("select WM_CONCAT(pdd_detno) from PriceMouldDet where pdd_pdid=? and nvl(pdd_price3,0)<>0",
						String.class, pd_id);
				if (dets != null) {
					sb.append("供应商3为空，明细一开模价3不能填写！行号" + dets).append("<hr>");
				}
				dets = baseDao.queryForObject(
						"select WM_CONCAT(pmd_detno) from PriceMouldDetail where pmd_pdid=? and nvl(pmd_prodprice3,0)<>0", String.class,
						pd_id);
				if (dets != null) {
					sb.append("供应商3为空，明细二材料价3不能填写！行号" + dets).append("<hr>");
				}
			}
			if (StringUtil.hasText(vend4)) {
				in = baseDao.getFieldsDataByCondition("InquiryMould", new String[] { "in_code", "in_statuscode" }, "in_sourceid=" + pd_id
						+ " and in_sourcetype='模具报价单' and in_vendcode='" + vend4 + "'");
				dets = baseDao.queryForObject("select WM_CONCAT(pdd_detno) from PriceMouldDet where pdd_pdid=? and nvl(pdd_price4,0)=0",
						String.class, pd_id);
				if (dets != null) {
					if (in != null) {
						if (!"NULLIFIED".equals(in[1])) {
							sb.append("供应商4[" + vend4 + "]存在有效的询价单[" + in[0] + "]，明细一开模价4不能为空！行号" + dets).append("<hr>");
						} else {
							sb.append("供应商4[" + vend4 + "]，明细一开模价4不能为空！行号" + dets).append("<hr>");
						}
					} else {
						sb.append("供应商4[" + vend4 + "]，明细一开模价4不能为空！行号" + dets).append("<hr>");
					}
				}
				dets = baseDao.queryForObject(
						"select WM_CONCAT(pmd_detno) from PriceMouldDetail where pmd_pdid=? and nvl(pmd_prodprice4,0)=0", String.class,
						pd_id);
				if (dets != null) {
					if (in != null) {
						if (!"NULLIFIED".equals(in[1])) {
							sb.append("供应商4[" + vend4 + "]存在有效的询价单[" + in[0] + "]，明细二材料价4不能为空！行号" + dets).append("<hr>");
						} else {
							sb.append("供应商4[" + vend4 + "]，明细二材料价4不能为空！行号" + dets).append("<hr>");
						}
					} else {
						sb.append("供应商4[" + vend4 + "]，明细二材料价4不能为空！行号" + dets).append("<hr>");
					}
				}
			} else if (!StringUtil.hasText(vend4)) {
				dets = baseDao.queryForObject("select WM_CONCAT(pdd_detno) from PriceMouldDet where pdd_pdid=? and nvl(pdd_price4,0)<>0",
						String.class, pd_id);
				if (dets != null) {
					sb.append("供应商4为空，明细一开模价4不能填写！行号" + dets).append("<hr>");
				}
				dets = baseDao.queryForObject(
						"select WM_CONCAT(pmd_detno) from PriceMouldDetail where pmd_pdid=? and nvl(pmd_prodprice4,0)<>0", String.class,
						pd_id);
				if (dets != null) {
					sb.append("供应商4为空，明细二材料价4不能填写！行号" + dets).append("<hr>");
				}
			}
			if (StringUtil.hasText(vend5)) {
				in = baseDao.getFieldsDataByCondition("InquiryMould", new String[] { "in_code", "in_statuscode" }, "in_sourceid=" + pd_id
						+ " and in_sourcetype='模具报价单' and in_vendcode='" + vend5 + "'");
				dets = baseDao.queryForObject("select WM_CONCAT(pdd_detno) from PriceMouldDet where pdd_pdid=? and nvl(pdd_price5,0)=0",
						String.class, pd_id);
				if (dets != null) {
					if (in != null) {
						if (!"NULLIFIED".equals(in[1])) {
							sb.append("供应商5[" + vend5 + "]存在有效的询价单[" + in[0] + "]，明细一开模价5不能为空！行号" + dets).append("<hr>");
						} else {
							sb.append("供应商5[" + vend5 + "]，明细一开模价5不能为空！行号" + dets).append("<hr>");
						}
					} else {
						sb.append("供应商5[" + vend5 + "]，明细一开模价5不能为空！行号" + dets).append("<hr>");
					}
				}
				dets = baseDao.queryForObject(
						"select WM_CONCAT(pmd_detno) from PriceMouldDetail where pmd_pdid=? and nvl(pmd_prodprice5,0)=0", String.class,
						pd_id);
				if (dets != null) {
					if (in != null) {
						if (!"NULLIFIED".equals(in[1])) {
							sb.append("供应商5[" + vend5 + "]存在有效的询价单[" + in[0] + "]，明细二材料价5不能为空！行号" + dets).append("<hr>");
						} else {
							sb.append("供应商5[" + vend5 + "]，明细二材料价5不能为空！行号" + dets).append("<hr>");
						}
					} else {
						sb.append("供应商5[" + vend5 + "]，明细二材料价5不能为空！行号" + dets).append("<hr>");
					}
				}
			} else if (!StringUtil.hasText(rs.getObject("pd_vend5"))) {
				dets = baseDao.queryForObject("select WM_CONCAT(pdd_detno) from PriceMouldDet where pdd_pdid=? and nvl(pdd_price5,0)<>0",
						String.class, pd_id);
				if (dets != null) {
					sb.append("供应商5为空，明细一开模价5不能填写！行号" + dets).append("<hr>");
				}
				dets = baseDao.queryForObject(
						"select WM_CONCAT(pmd_detno) from PriceMouldDetail where pmd_pdid=? and nvl(pmd_prodprice5,0)<>0", String.class,
						pd_id);
				if (dets != null) {
					sb.append("供应商5为空，明细二材料价5不能填写！行号" + dets).append("<hr>");
				}
			}
			if (sb.length() > 0) {
				BaseUtil.showError(sb.toString());
			}
			String vendcode = rs.getGeneralString("pd_vendcode");
			if (!"".equals(vendcode)) {
				if (!vendcode.equals(rs.getGeneralString("pd_vend1")) && !vendcode.equals(rs.getGeneralString("pd_vend2"))
						&& !vendcode.equals(rs.getGeneralString("pd_vend3")) && !vendcode.equals(rs.getGeneralString("pd_vend4"))
						&& !vendcode.equals(rs.getGeneralString("pd_vend5"))) {
					BaseUtil.showError("必须在五个供应商中选定!");
				}
			} else {
				BaseUtil.showError("请先选定采购供应商!");
			}
			//20170824   反馈编号2017080589,去掉该限制
			/*dets = baseDao.queryForObject(
					"select WM_CONCAT(in_code) from InquiryMould where in_sourcetype='模具报价单' and in_source='" + rs.getObject("pd_code")
							+ "' and in_statuscode='NULLIFIED' and in_adoptstatus='已采纳'", String.class);
			if (dets != null) {
				BaseUtil.showError("该模具报价单采纳的询价单[" + dets + "]已作废，不允许提交！");
			}*/
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { pd_id });
		// 执行提交操作
		baseDao.submit("PriceMould", "pd_id=" + pd_id, "pd_status", "pd_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pd_id", pd_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { pd_id });
	}

	@Override
	public void resSubmitPriceMould(int pd_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("PriceMould", "pd_statuscode", "pd_id=" + pd_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { pd_id });
		// 执行反提交操作
		baseDao.resOperate("PriceMould", "pd_id=" + pd_id, "pd_status", "pd_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pd_id", pd_id);
		handlerService.afterResSubmit(caller, new Object[] { pd_id });
	}

	@Override
	public List<Map<String, Object>> turnInquiry(int pd_id, String caller) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		StringBuffer sb = new StringBuffer();
		String dets = baseDao.queryForObject("select WM_CONCAT(pd_vend1) from PriceMould left join vendor on pd_vend1=ve_code where pd_id="
				+ pd_id + " and nvl(pd_vend1,' ')<>' ' and nvl(ve_uu,' ')=' '", String.class);
		if (dets != null) {
			sb.append("供应商" + dets + "，不存在优软云账号，请安排供应商注册优软云账号！").append("<hr>");
		}
		dets = baseDao.queryForObject("select WM_CONCAT(pd_vend2) from PriceMould left join vendor on pd_vend2=ve_code where pd_id="
				+ pd_id + " and nvl(pd_vend2,' ')<>' ' and nvl(ve_uu,' ')=' '", String.class);
		if (dets != null) {
			sb.append("供应商" + dets + "，不存在优软云账号，请安排供应商注册优软云账号！").append("<hr>");
		}
		dets = baseDao.queryForObject("select WM_CONCAT(pd_vend3) from PriceMould left join vendor on pd_vend3=ve_code where pd_id="
				+ pd_id + " and nvl(pd_vend3,' ')<>' ' and nvl(ve_uu,' ')=' '", String.class);
		if (dets != null) {
			sb.append("供应商" + dets + "，不存在优软云账号，请安排供应商注册优软云账号！").append("<hr>");
		}
		dets = baseDao.queryForObject("select WM_CONCAT(pd_vend4) from PriceMould left join vendor on pd_vend4=ve_code where pd_id="
				+ pd_id + " and nvl(pd_vend4,' ')<>' ' and nvl(ve_uu,' ')=' '", String.class);
		if (dets != null) {
			sb.append("供应商" + dets + "，不存在优软云账号，请安排供应商注册优软云账号！").append("<hr>");
		}
		dets = baseDao.queryForObject("select WM_CONCAT(pd_vend5) from PriceMould left join vendor on pd_vend5=ve_code where pd_id="
				+ pd_id + " and nvl(pd_vend5,' ')<>' ' and nvl(ve_uu,' ')=' '", String.class);
		if (dets != null) {
			sb.append("供应商" + dets + "，不存在优软云账号，请安排供应商注册优软云账号！").append("<hr>");
		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}
		SqlRowList rs = baseDao
				.queryForRowSet("SELECT * FROM PriceMould where pd_id="
						+ pd_id
						+ "and nvl(pd_vend1,' ')=' ' and nvl(pd_vend2,' ')=' ' and nvl(pd_vend3,' ')=' ' and nvl(pd_vend4,' ')=' ' and nvl(pd_vend5,' ')=' '");
		if (rs.next()) {
			BaseUtil.showError("五个供应商不能都为空！");
		}
		int count = baseDao.getCount("select count(1) from PriceMouldDet where pdd_pdid=" + pd_id);
		if (count == 0) {
			BaseUtil.showError("模具明细不能为空！");
		}
		count = baseDao.getCount("select count(1) from PriceMouldDetail where pmd_pdid=" + pd_id);
		if (count == 0) {
			BaseUtil.showError("物料明细不能为空！");
		}
		
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject("select WM_CONCAT(pdd_detno) from PriceMouldDet left join PriceMould on pdd_pdid=pd_id where pd_code in (select in_source from InquiryMould) and (nvl(pdd_price1,0)<>0 or nvl(pdd_price2,0)<>0 or nvl(pdd_price3,0)<>0 or nvl(pdd_price4,0)<>0 or nvl(pdd_price5,0)<>0) and pdd_pdid="+pd_id+" ", String.class);
		if (dets != null) {
			sb.append("该报价单模具明细行存在已有价格的明细行（"+dets+"），请先将对应询价单作废后再重新转询价单！").append("<hr>");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject("select WM_CONCAT(pmd_detno) from PriceMouldDetail left join PriceMould on pmd_pdid=pd_id where pd_code in (select in_source from InquiryMould) and (nvl(pmd_prodprice1,0)<>0 or nvl(pmd_prodprice2,0)<>0 or nvl(pmd_prodprice3,0)<>0 or nvl(pmd_prodprice4,0)<>0 or nvl(pmd_prodprice5,0)<>0 ) and pmd_pdid="+pd_id+" ", String.class);
		if (dets != null) {
			sb.append("该报价单物料明细行存在已有价格的明细行（"+dets+"），请先将对应询价单作废后再重新转询价单！").append("<hr>");
		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(pdd_detno) from PriceMouldDet where pdd_pdid="
								+ pd_id
								+ " and (nvl(pdd_price1,0)<>0 or nvl(pdd_price2,0)<>0 or nvl(pdd_price3,0)<>0 or nvl(pdd_price4,0)<>0 or nvl(pdd_price5,0)<>0 )",
						String.class);
		if (dets != null) {
			sb.append("该报价单模具模具明细存在已有价格的明细行，请清除价格后重新转询价单！行号：" + dets).append("<hr>");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(pmd_detno) from PriceMouldDetail where pmd_pdid="
								+ pd_id
								+ " and (nvl(pmd_prodprice1,0)<>0 or nvl(pmd_prodprice2,0)<>0 or nvl(pmd_prodprice3,0)<>0 or nvl(pmd_prodprice4,0)<>0 or nvl(pmd_prodprice5,0)<>0 )",
						String.class);
		if (dets != null) {
			sb.append("该报价单模具物料明细存在已有价格的明细行，请清除价格后重新转询价单！行号：" + dets).append("<hr>");
		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}
		rs = baseDao.queryForRowSet("SELECT * FROM PriceMould where pd_id=" + pd_id);
		if (rs.next()) {
			String vendcode = rs.getGeneralString("pd_vendcode");
			if (!"".equals(vendcode)) {
				if (!vendcode.equals(rs.getGeneralString("pd_vend1")) && !vendcode.equals(rs.getGeneralString("pd_vend2"))
						&& !vendcode.equals(rs.getGeneralString("pd_vend3")) && !vendcode.equals(rs.getGeneralString("pd_vend4"))
						&& !vendcode.equals(rs.getGeneralString("pd_vend5"))) {
					BaseUtil.showError("必须在五个供应商中选定!");
				}
			}
			JSONObject j = null;
			if (!"".equals(rs.getGeneralString("pd_vend1"))) {
				dets = baseDao
						.queryForObject("select WM_CONCAT(in_code) from InquiryMould where in_sourcetype='模具报价单' and in_sourceid=" + pd_id
								+ " and in_statuscode<>'NULLIFIED' and in_vendcode='" + rs.getGeneralString("pd_vend1") + "'", String.class);
				Map<String, Object> map = new HashMap<String, Object>();
				if (dets != null) {
					map.put("errMsg", "该模具报价单供应商[" + rs.getGeneralString("pd_vend1") + "]已转入模具询价单！询价单号：" + dets);
				} else {
					j = priceMouldDao.turnInquiry(pd_id, rs.getString("pd_vend1"), caller);
					if (j != null) {
						baseDao.execute(
								"update InquiryDetail set id_price=(select nvl(pmd_prodprice1,0) from PRICEMOULDDetail where id_prodcode=pmd_prodcode and pmd_pdid=?) where id_inid=?",
								pd_id, j.getInt("in_id"));
						map.put("id", j.getInt("in_id"));
						map.put("code", j.getString("in_code"));
					}
				}
				list.add(map);
			}
			if (!"".equals(rs.getGeneralString("pd_vend2"))) {
				dets = baseDao
						.queryForObject("select WM_CONCAT(in_code) from InquiryMould where in_sourcetype='模具报价单' and in_sourceid=" + pd_id
								+ " and in_statuscode<>'NULLIFIED' and in_vendcode='" + rs.getGeneralString("pd_vend2") + "'", String.class);
				Map<String, Object> map = new HashMap<String, Object>();
				if (dets != null) {
					map.put("errMsg", "该模具报价单供应商[" + rs.getGeneralString("pd_vend2") + "]已转入模具询价单！询价单号：" + dets);
				} else {
					j = priceMouldDao.turnInquiry(pd_id, rs.getString("pd_vend2"), caller);
					if (j != null) {
						baseDao.execute(
								"update InquiryDetail set id_price=(select nvl(pmd_prodprice2,0) from PRICEMOULDDetail where id_prodcode=pmd_prodcode and pmd_pdid=?) where id_inid=?",
								pd_id, j.getInt("in_id"));
						map.put("id", j.getInt("in_id"));
						map.put("code", j.getString("in_code"));
					}
				}
				list.add(map);
			}
			if (!"".equals(rs.getGeneralString("pd_vend3"))) {
				dets = baseDao
						.queryForObject("select WM_CONCAT(in_code) from InquiryMould where in_sourcetype='模具报价单' and in_sourceid=" + pd_id
								+ " and in_statuscode<>'NULLIFIED' and in_vendcode='" + rs.getGeneralString("pd_vend3") + "'", String.class);
				Map<String, Object> map = new HashMap<String, Object>();
				if (dets != null) {
					map.put("errMsg", "该模具报价单供应商[" + rs.getGeneralString("pd_vend3") + "]已转入模具询价单！询价单号：" + dets);
				} else {
					j = priceMouldDao.turnInquiry(pd_id, rs.getString("pd_vend3"), caller);
					if (j != null) {
						baseDao.execute(
								"update InquiryDetail set id_price=(select nvl(pmd_prodprice3,0) from PRICEMOULDDetail where id_prodcode=pmd_prodcode and pmd_pdid=?) where id_inid=?",
								pd_id, j.getInt("in_id"));
						map.put("id", j.getInt("in_id"));
						map.put("code", j.getString("in_code"));
					}
				}
				list.add(map);
			}
			if (!"".equals(rs.getGeneralString("pd_vend4"))) {
				dets = baseDao
						.queryForObject("select WM_CONCAT(in_code) from InquiryMould where in_sourcetype='模具报价单' and in_sourceid=" + pd_id
								+ " and in_statuscode<>'NULLIFIED' and in_vendcode='" + rs.getGeneralString("pd_vend4") + "'", String.class);
				Map<String, Object> map = new HashMap<String, Object>();
				if (dets != null) {
					map.put("errMsg", "该模具报价单供应商[" + rs.getGeneralString("pd_vend4") + "]已转入模具询价单！询价单号：" + dets);
				} else {
					j = priceMouldDao.turnInquiry(pd_id, rs.getString("pd_vend4"), caller);
					if (j != null) {
						baseDao.execute(
								"update InquiryDetail set id_price=(select nvl(pmd_prodprice4,0) from PRICEMOULDDetail where id_prodcode=pmd_prodcode and pmd_pdid=?) where id_inid=?",
								pd_id, j.getInt("in_id"));
						map.put("id", j.getInt("in_id"));
						map.put("code", j.getString("in_code"));
					}
				}
				list.add(map);
			}
			if (!"".equals(rs.getGeneralString("pd_vend5"))) {
				dets = baseDao
						.queryForObject("select WM_CONCAT(in_code) from InquiryMould where in_sourcetype='模具报价单' and in_sourceid=" + pd_id
								+ " and in_statuscode<>'NULLIFIED' and in_vendcode='" + rs.getGeneralString("pd_vend5") + "'", String.class);
				Map<String, Object> map = new HashMap<String, Object>();
				if (dets != null) {
					map.put("errMsg", "该模具报价单供应商[" + rs.getGeneralString("pd_vend5") + "]已转入模具询价单！询价单号：" + dets);
				} else {
					j = priceMouldDao.turnInquiry(pd_id, rs.getString("pd_vend5"), caller);
					if (j != null) {
						baseDao.execute(
								"update InquiryDetail set id_price=(select nvl(pmd_prodprice5,0) from PRICEMOULDDetail where id_prodcode=pmd_prodcode and pmd_pdid=?) where id_inid=?",
								pd_id, j.getInt("in_id"));
						map.put("id", j.getInt("in_id"));
						map.put("code", j.getString("in_code"));
					}
				}
				list.add(map);
			}
		}
		return list;
	}

	@Override
	public int turnPurMould(int pd_id, String caller) {
		int pmid = 0;
		String pricecolumn = null;
		String returncolumn = null;
		SqlRowList rs = baseDao.queryForRowSet("SELECT * FROM PriceMould where pd_id=" + pd_id);
		// 判断该模具报价单是否已经转入过模具采购单
		Object code = baseDao.getFieldDataByCondition("PriceMould", "pd_code", "pd_id=" + pd_id);
		code = baseDao.getFieldDataByCondition("PURMOULD", "pm_code", "pm_source='" + code + "'");
		if (code != null && !code.equals("")) {
			BaseUtil.showError("该模具报价单已经转入过模具采购单!模具采购单单号:" + code);
		} else {
			if (rs.next()) {
				String vendcode = rs.getGeneralString("pd_vendcode");
				if ("".equals(vendcode)) {
					BaseUtil.showError("请先选定供应商!");
				} else {
					if (vendcode.equals(rs.getGeneralString("pd_vend1"))) {
						pricecolumn = "pdd_price1";
						returncolumn = "pd_returnqty1";
					} else if (vendcode.equals(rs.getGeneralString("pd_vend2"))) {
						pricecolumn = "pdd_price2";
						returncolumn = "pd_returnqty2";
					} else if (vendcode.equals(rs.getGeneralString("pd_vend3"))) {
						pricecolumn = "pdd_price3";
						returncolumn = "pd_returnqty3";
					} else if (vendcode.equals(rs.getGeneralString("pd_vend4"))) {
						pricecolumn = "pdd_price4";
						returncolumn = "pd_returnqty4";
					} else if (vendcode.equals(rs.getGeneralString("pd_vend5"))) {
						pricecolumn = "pdd_price5";
						returncolumn = "pd_returnqty5";
					} else {
						BaseUtil.showError("必须在五个供应商中选定!");
					}
					if (pricecolumn.length() > 0) {
						pmid = priceMouldDao.turnPurMould(pd_id, pricecolumn, returncolumn, caller);
						baseDao.execute("update PURMOULDDETAIL set pmd_total=ROUND(nvl(pmd_price,0)*nvl(pmd_qty,0),2) where pmd_pmid="
								+ pmid);
						baseDao.execute("update PURMOULD set pm_taxtotal=(select sum(pmd_total) from PURMOULDDETAIL where PURMOULDDETAIL.pmd_pmid = PURMOULD.pm_id) where pm_id="
								+ pmid);
					}
				}
			}
		}
		// 修改报价单状态
		baseDao.updateByCondition("PriceMould", "pd_statuscode='TURNPURC',pd_status='" + BaseUtil.getLocalMessage("TURNPURC") + "'",
				"pd_id=" + pd_id);
		// 记录操作
		baseDao.logger.turn(BaseUtil.getLocalMessage("msg.turnPurchase"), caller, "pd_id", pd_id);
		return pmid;
	}
}
