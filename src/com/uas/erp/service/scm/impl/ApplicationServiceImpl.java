package com.uas.erp.service.scm.impl;

import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.b2c.service.common.GetGoodsReserveService;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlMap;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ApplicationDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.pm.MakeBaseService;
import com.uas.erp.service.scm.ApplicationService;

@Service("applicationService")
public class ApplicationServiceImpl implements ApplicationService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private ApplicationDao applicationDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private GetGoodsReserveService getGoodsReserveService;
	@Autowired
	private MakeBaseService makeBaseService;
	static final String INSERTMAKE="INSERT INTO Make(ma_statuscode,ma_status,ma_date,ma_id,ma_code,ma_tasktype,ma_kind,ma_typeset,ma_adid,ma_planbegindate,ma_planenddate,ma_prodid,ma_prodcode,ma_prodname,ma_prodspec,ma_rate,ma_qty,ma_recorder,ma_recorderid,ma_recorddate,ma_checkstatuscode,ma_checkstatus,ma_finishstatuscode,ma_finishstatus,ma_printstatuscode,ma_printstatus,ma_turnstatuscode,ma_turnstatus) VALUES  "
			+ "(?,?,sysdate,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,'UNAPPROVED','未批准','UNCOMPLET','未完工','UNPRINT','未打印','UNGET','未领料')";
	@Override
	public void saveApplication(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		String code = store.get("ap_code").toString();
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Application", "ap_code='" + code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, grid });
		store.put("ap_printstatuscode", "UNPRINT");
		store.put("ap_printstatus", BaseUtil.getLocalMessage("UNPRINT"));
		// 保存Application
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Application", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存ApplicationDetail
		for (Map<Object, Object> m : grid) {
			m.put("ad_id", baseDao.getSeqId("APPLICATIONDETAIL_SEQ"));
			m.put("ad_status", BaseUtil.getLocalMessage("ENTERING"));
			m.put("ad_code", code);
			m.remove("ad_yqty");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "ApplicationDetail");
		baseDao.execute(gridSql);
		String error = checkqty(store.get("ap_id"));
		if(baseDao.isDBSetting(caller,"getAdPrice")){
			getProdPrice(Integer.parseInt(store.get("ap_id").toString()));
		}
		baseDao.updateByCondition("Applicationdetail","ad_total=nvl(ad_price,0)*nvl(ad_qty,0)", "ad_apid="+store.get("ap_id"));
		baseDao.updateByCondition("Application a", "ap_total=(select sum(nvl(ad_total,0)) from applicationdetail where ad_apid=a.ap_id)", "ap_id="+store.get("ap_id"));
		// 记录操作
		baseDao.logger.save(caller, "ap_id", store.get("ap_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, grid });
		BaseUtil.showErrorOnSuccess(error);
	}

	private String checkqty(Object apid) {
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(ad_detno) from applicationdetail where ad_apid =" + apid
						+ " and (nvl(ad_qty,0)<nvl(ad_minpack,0) or nvl(ad_qty,0)<nvl(ad_minorder,0))", String.class);
		if (dets != null) {
			return "请购单的数量小于最小订购量或者小于最小包装量!行号：" + dets;
		}
		return null;
	}

	@Override
	public void deleteApplication(int ap_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("Application", "ap_statuscode", "ap_id=" + ap_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { ap_id });
		// 删除Application
		baseDao.deleteById("Application", "ap_id", ap_id);
		// 删除ApplicationDetail
		baseDao.deleteById("applicationdetail", "ad_apid", ap_id);
		// 记录操作
		baseDao.logger.delete(caller, "ap_id", ap_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { ap_id });
	}

	@Override
	public void updateApplicationById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		String code = store.get("ap_code").toString();
		// 只能修改[在录入]的资料!
		/*Object status = baseDao.getFieldDataByCondition("Application", "ap_statuscode", "ap_id=" + store.get("ap_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}*/
		// 增加限制:如果原物料存在ad_mrpcode和ad_mdid,则不允许该行进行更新
		for (Map<Object, Object> s : gstore) {
			s.remove("ad_yqty");
			boolean bool = s.get("ad_id") == null || s.get("ad_id").equals("") || s.get("ad_id").equals("0")
					|| Integer.parseInt(s.get("ad_id").toString()) == 0;
			if (!bool) {// 新添加的数据，id不存在
				SqlRowList rs = baseDao.queryForRowSet("select count(1) c from applicationdetail where ad_id=? and ad_prodcode<>? and nvl(ad_mrpcode,' ')<>' ' and nvl(ad_mdid,0)>0",s.get("ad_id"),s.get("ad_prodcode"));
				if(rs.next()){
					int c = rs.getInt("c");
					if(c>0){
						BaseUtil.showError("来源MRP的明细物料不允许变更，行号"+s.get("ad_detno")+"!");
					}
				}
			}
		}
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		// 修改Application
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Application", "ap_id");
		baseDao.execute(formSql);
		// 修改ApplicationDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ApplicationDetail", "ad_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("ad_id") == null || s.get("ad_id").equals("") || s.get("ad_id").equals("0")
					|| Integer.parseInt(s.get("ad_id").toString()) == 0) {// 新添加的数据，id不存在
				s.put("ad_status", BaseUtil.getLocalMessage("ENTERING"));
				s.put("ad_code", code);
				s.put("ad_id", baseDao.getSeqId("APPLICATIONDETAIL_SEQ"));
				String sql = SqlUtil.getInsertSqlByMap(s, "ApplicationDetail");
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		String error = checkqty(store.get("ap_id"));
		if(baseDao.isDBSetting(caller,"getAdPrice")){
			getProdPrice(Integer.parseInt(store.get("ap_id").toString()));
		}
		baseDao.updateByCondition("Applicationdetail","ad_total=nvl(ad_price,0)*nvl(ad_qty,0)", "ad_apid="+store.get("ap_id"));
		baseDao.updateByCondition("Application a", "ap_total=(select sum(nvl(ad_total,0)) from applicationdetail where ad_apid=a.ap_id)", "ap_id="+store.get("ap_id"));
		// 记录操作
		baseDao.logger.update(caller, "ap_id", store.get("ap_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
		BaseUtil.showErrorOnSuccess(error);
	}

	@Override
	public String[] printApplication(int ap_id, String caller, String reportName, String condition) {
		// 执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[] { ap_id });
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 修改打印状态
		baseDao.print("Application", "ap_id=" + ap_id, "ap_printstatus", "ap_printstatuscode");
		// 记录操作
		baseDao.logger.print(caller, "ap_id", ap_id);
		// 执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[] { ap_id });
		return keys;
	}

	@Override
	public void auditApplication(int ap_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Application", "ap_statuscode", "ap_id=" + ap_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.audit_onlyCommited"));
		}
		/**@author wuyx 
		 * 反馈编号：2018050183
		 * 越疆 允许请购类型是不允许未认可物料的时候，物料认可状态是【确认中】也能提交审核
		 * */
		if(baseDao.isDBSetting("Application","allowConfirming")){
			SqlRowList rs = baseDao
					.queryForRowSet("select  wm_concat(ad_prodcode) prcode,count(1) num from application left join purchasekind on pk_name=ap_kind left join applicationdetail on ap_id=ad_apid  left join product on ad_prodcode=pr_code where ap_id="
							+ ap_id + " and NVL(pk_allownoappstatus,0)=0 and NVL(pr_material,' ') not in ('已认可','无须认可','确认中') ");
			if (rs.next()) {
				if (rs.getInt("num") > 0) {
					BaseUtil.showError("未认可物料:" + rs.getString("prcode") + ",只有已认可、无须认可和确认中状态的物料才允许下达请购");
				}
			}
		}else{
			SqlRowList rs = baseDao
					.queryForRowSet("select  wm_concat(ad_prodcode) prcode,count(1) num from application left join purchasekind on pk_name=ap_kind left join applicationdetail on ap_id=ad_apid  left join product on ad_prodcode=pr_code where ap_id="
							+ ap_id + " and NVL(pk_allownoappstatus,0)=0 and NVL(pr_material,' ') not in ('已认可','无须认可') ");
			if (rs.next()) {
				if (rs.getInt("num") > 0) {
					BaseUtil.showError("未认可物料:" + rs.getString("prcode") + ",只有已认可和无须认可状态的物料才允许下达请购");
				}
			}
		}
		String det = baseDao.getJdbcTemplate().queryForObject("select wm_concat(ad_detno) from applicationdetail left join product on ad_prodcode=pr_code where round(ad_qty,nvl(pr_precision,0))<>ad_qty and ad_apid="+ap_id, String.class);
		if(det != null){
			BaseUtil.showError("当前物料请购数量不符合物料精度，请修改！序号："+det);
		}
		/**
		 * @author wsy
		 * 最小包装量
		 */
		Object pk_isminpack = baseDao.getFieldDataByCondition("PurchaseKind left join application on pk_name=ap_kind", "nvl(pk_isminpack,0)", "ap_id="+ap_id);
		pk_isminpack = pk_isminpack==null?"0":pk_isminpack;
		if("-1".equals(pk_isminpack.toString())){
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ad_detno) from applicationdetail left join Application on ad_apid=ap_id where ap_id=? and nvl(ad_minpack,0)<>0 and nvl(ad_qty,0)<>0 and mod(nvl(ad_qty,0),nvl(ad_minpack,0))>0",
							String.class, ap_id);
			if (dets != null) {
				BaseUtil.showError("请购数量不是最小包装量的倍数，不允许进行当前操作!行号：" + dets);
			}
		}
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { ap_id });
		// 执行审核操作
		baseDao.audit("Application", "ap_id=" + ap_id, "ap_status", "ap_statuscode", "ap_auditdate", "ap_auditman");
		baseDao.audit("ApplicationDetail", "ad_apid=" + ap_id, "ad_status", "ad_statuscode");
		// 记录操作
		baseDao.logger.audit(caller, "ap_id", ap_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { ap_id });
		// 审核之后自动从标准器件库获取uuid对应的库存
		getGoodsReserve(ap_id);
	}

	@Override
	public void resAuditApplication(int ap_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object objs = baseDao.getFieldDataByCondition("Application", "ap_statuscode", "ap_id=" + ap_id);
		if (!objs.toString().equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
		}
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(ad_detno) from Applicationdetail where ad_apid=? and nvl(ad_yqty,0)>0 ", String.class, ap_id);
		if (dets != null) {
			BaseUtil.showError("已转采购单，不允许反审核!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(ad_detno) from ApplicationDetail where nvl(ad_statuscode, ' ') in ('FINISH','FREEZE','NULLIFIED') and ad_apid=?",
						String.class, ap_id);
		if (dets != null) {
			BaseUtil.showError("明细行已结案、已冻结、已作废，不允许反审核!行号：" + dets);
		}
		handlerService.handler(caller, "resAudit", "before", new Object[] { ap_id });
		// 执行反审核操作
		baseDao.resAudit("Application", "ap_id=" + ap_id, "ap_status", "ap_statuscode", "ap_auditdate", "ap_auditman");
		baseDao.resOperate("ApplicationDetail", "ad_apid=" + ap_id, "ad_status", "ad_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "ap_id", ap_id);
		handlerService.handler(caller, "resAudit", "after", new Object[] { ap_id });
	}

	@Override
	public void submitApplication(int ap_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Application", "ap_statuscode", "ap_id=" + ap_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.submit_onlyEntering"));
		}
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(ad_detno) from ApplicationDetail where trunc(ad_delivery)<trunc(sysdate) and ad_apid=?", String.class,
				ap_id);
		if (dets != null) {
			BaseUtil.showError("单据需求日期小于当前日期，不允许提交!行号：" + dets);
		}
		dets = baseDao.getJdbcTemplate().queryForObject("select wm_concat(ad_detno) from applicationdetail left join product on ad_prodcode=pr_code where round(ad_qty,nvl(pr_precision,0))<>ad_qty and ad_apid="+ap_id, String.class);
		if(dets != null){
			BaseUtil.showError("当前物料请购数量不符合物料精度，请修改！序号："+dets);
		}
		baseDao.execute("update Application set ap_vendcode=ltrim(rtrim(ap_vendcode)) where ap_id=" + ap_id);
		baseDao.execute("update ApplicationDetail set ad_prodcode=ltrim(rtrim(ad_prodcode)) where ad_apid=" + ap_id);
		// 供应商是否存在
		SqlRowList rs = baseDao.queryForRowSet(
				"SELECT ap_vendcode FROM Application WHERE ap_id=? AND ap_vendcode not in (SELECT ve_code FROM Vendor)", ap_id);
		if (rs.next() && rs.getString("ap_vendcode") != null) {
			BaseUtil.showError(BaseUtil.getLocalMessage("vendor_not_exist") + "<br>" + rs.getString("ap_vendcode"));
		}
		/*
		 * dets = baseDao.getJdbcTemplate().queryForObject(
		 * "select wmsys.wm_concat(ad_detno) from applicationdetail left join product on ad_prodcode=pr_code  where ad_apid ="
		 * + ap_id + " and pr_supplytype='VIRTUAL' ", String.class); if(dets !=
		 * null) { BaseUtil.showError("序号" + dets+ "物料为虚拟件，不能下达请购！"); return; }
		 */
		// 只能选择已审核的供应商!
		Object code = baseDao.getFieldDataByCondition("Application", "ap_vendcode", "ap_id=" + ap_id);
		status = baseDao.getFieldDataByCondition("Vendor", "ve_auditstatuscode", "ve_code='" + code + "'");
		if (status != null && !status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("vendor_onlyAudited")
					+ "<a href=\"javascript:openUrl('jsps/scm/purchase/vendor.jsp?formCondition=ve_codeIS" + code + "')\">" + code
					+ "</a>&nbsp;");
		}
		// 判断明细行物料是否有未审核的，有未审核不让提交
		String selectSQL = "select ad_detno,ad_prodcode,pr_status,pr_code from ApplicationDetail left join product on pr_code=ad_prodcode where ad_apid="
				+ ap_id + " and NVL(pr_statuscode,' ')<>'AUDITED' ";
		rs = baseDao.queryForRowSet(selectSQL);
		int detno = 0;
		if (rs.next()) {
			detno = rs.getInt("ad_detno");
			if (rs.getObject("pr_code") != null) {
				BaseUtil.showError("序号" + String.valueOf(detno) + "物料未审核，不能提交！");
				return;
			} else {
				BaseUtil.showError("序号" + String.valueOf(detno) + "物料不存在，不能提交！");
				return;
			}
		}
		/**@author wuyx
		 * 反馈编号：2018050183
		 * 越疆 允许请购类型是不允许未认可物料的时候，物料认可状态是【确认中】也能提交审核
		 * */
		if(baseDao.isDBSetting("Application","allowConfirming")){
			 rs = baseDao
					.queryForRowSet("select  wm_concat(ad_prodcode) prcode,count(1) num from application left join purchasekind on pk_name=ap_kind left join applicationdetail on ap_id=ad_apid  left join product on ad_prodcode=pr_code where ap_id="
							+ ap_id + " and NVL(pk_allownoappstatus,0)=0 and NVL(pr_material,' ') not in ('已认可','无须认可','确认中') ");
			if (rs.next()) {
				if (rs.getInt("num") > 0) {
					BaseUtil.showError("未认可物料:" + rs.getString("prcode") + ",只有已认可、无须认可和确认中状态的物料才允许下达请购");
				}
			}
		}else{
			 rs = baseDao
					.queryForRowSet("select  wm_concat(ad_prodcode) prcode,count(1) num from application left join purchasekind on pk_name=ap_kind left join applicationdetail on ap_id=ad_apid  left join product on ad_prodcode=pr_code where ap_id="
							+ ap_id + " and NVL(pk_allownoappstatus,0)=0 and NVL(pr_material,' ') not in ('已认可','无须认可') ");
			if (rs.next()) {
				if (rs.getInt("num") > 0) {
					BaseUtil.showError("未认可物料:" + rs.getString("prcode") + ",当前请购类型只允许下达已认可和无须认可状态的物料！");
				}
			}
		}
		/**
		 * @author wsy
		 * 最小包装量
		 */
		Object pk_isminpack = baseDao.getFieldDataByCondition("PurchaseKind left join application on pk_name=ap_kind", "nvl(pk_isminpack,0)", "ap_id="+ap_id);
		pk_isminpack = pk_isminpack==null?"0":pk_isminpack;
		if("-1".equals(pk_isminpack.toString())){
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ad_detno) from applicationdetail left join Application on ad_apid=ap_id where ap_id=? and nvl(ad_minpack,0)<>0 and nvl(ad_qty,0)<>0 and mod(nvl(ad_qty,0),nvl(ad_minpack,0))>0",
							String.class, ap_id);
			if (dets != null) {
				BaseUtil.showError("请购数量不是最小包装量的倍数，不允许进行当前操作!行号：" + dets);
			}
		}
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { ap_id });
		// 执行提交操作
		baseDao.submit("Application", "ap_id=" + ap_id, "ap_status", "ap_statuscode");
		baseDao.submit("ApplicationDetail", "ad_apid=" + ap_id, "ad_status", "ad_statuscode");
		if(baseDao.isDBSetting(caller,"getAdPrice")){
			getProdPrice(ap_id);
		}
		baseDao.updateByCondition("Applicationdetail","ad_total=nvl(ad_price,0)*nvl(ad_qty,0)", "ad_apid="+ap_id);
		baseDao.updateByCondition("Application a", "ap_total=(select sum(nvl(ad_total,0)) from applicationdetail where ad_apid=a.ap_id)", "ap_id="+ap_id);
		// 记录操作
		baseDao.logger.submit(caller, "ap_id", ap_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { ap_id });
	}

	@Override
	public void resSubmitApplication(int ap_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Application", "ap_statuscode", "ap_id=" + ap_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resSubmit_onlyCommited"));
		}
		handlerService.handler(caller, "resCommit", "before", new Object[] { ap_id });
		// 执行反提交操作
		baseDao.resOperate("Application", "ap_id=" + ap_id, "ap_status", "ap_statuscode");
		baseDao.resOperate("ApplicationDetail", "ad_apid=" + ap_id, "ad_status", "ad_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "ap_id", ap_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { ap_id });
	}

	@Override
	@Transactional
	public int turnPurchase(int ap_id, String caller) {
		int puid = 0;
		// 判断该请购单是否已经转入过采购单
		Object code = baseDao.getFieldDataByCondition("application", "ap_code", "ap_id=" + ap_id);
		code = baseDao.getFieldDataByCondition("purchase", "pu_code", "pu_sourcecode='" + code + "'");
		if (!StringUtil.hasText(code)) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.application.haveturn")
					+ "<a href=\"javascript:openUrl('jsps/scm/purchase/purchase.jsp?formCondition=pu_codeIS" + code
					+ "&gridCondition=pd_codeIS" + code + "')\">" + code + "</a>&nbsp;");
		} else {
			// 转采购
			puid = applicationDao.turnPurchase(ap_id);
			// 修改请购单状态
			baseDao.updateByCondition("Application", "ap_statuscode='TURNPURC',ap_status='" + BaseUtil.getLocalMessage("TURNPURC") + "'",
					"ap_id=" + ap_id);
			baseDao.updateByCondition("ApplicationDetail", "ad_statuscode='TURNPURC',ad_status='" + BaseUtil.getLocalMessage("TURNPURC")
					+ "',ad_yqty=ad_qty", "ad_apid=" + ap_id);
			// 记录操作
			baseDao.logger.turn("msg.turnPurchase", "Application", "ap_id", ap_id);
		}
		return puid;
	}

	@Override
	public void getVendor(int[] id) {
		applicationDao.getVendor(id);
	}
	/**
	 * 取MC的供应商、币别按最低最新有效价格
	 */
	@Override
	public void getMCVendor(int[] id) {
		String GETVENDORBYDATE = "SELECT ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid,ppd_price,ppd_rate FROM (SELECT ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid,ppd_price,ppd_rate FROM PurchasePriceDetail,Currencys,PurchasePrice WHERE "
				+ "ppd_currency=cr_name and pp_id=ppd_ppid and pp_statuscode='AUDITED' and ppd_statuscode='VALID' and pp_kind='模材' and ppd_material=(SELECT ad_material FROM applicationdetail where ad_id=?) and ppd_lapqty<=(select ad_qty from applicationdetail where ad_id=?) and nvl(ppd_todate, nvl(pp_todate, sysdate)) + 1>sysdate order by pp_indate desc,ppd_id desc) where rownum<2";
		for(int idx : id){
			SqlRowList rs = baseDao.queryForRowSet(GETVENDORBYDATE,idx,idx);
			if(rs.next()){
				baseDao.execute("UPDATE ApplicationDetail SET ad_ifvendrate=0,ad_vendor=?,ad_vendname=?,ad_currency=?,ad_vendid=?,ad_barcode=?,ad_purcprice=?,ad_rate=? WHERE ad_id=?",new Object[] { rs.getObject(1), rs.getObject(2), rs.getObject(3), rs.getObject(4) ,"", rs.getObject(5),rs.getObject(6), idx });
			}
		}
	}
	/**
	 * 请购单批量抛转
	 */
	@Override
	public String[] postApplication(int[] id, int ma_id_t) {
		// 同一服务器，不同数据库账号间抛数据
		String from = baseDao.getFieldDataByCondition("master", "ma_name", "ma_id=" + SystemSession.getUser().getEm_maid()).toString();
		String to = baseDao.getFieldDataByCondition("master", "ma_name", "ma_id=" + ma_id_t).toString();
		return applicationDao.postApplication(id, from, to);
		// 不同服务器间数据抛转
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean ImportExcel(int id, Workbook wbs, String substring) {
		int sheetnum = wbs.getNumberOfSheets();
		StringBuffer sb = new StringBuffer();
		int detno = 1;
		Object textValue = "";
		List<String> sqls = new ArrayList<String>();
		SqlRowList sl = baseDao.queryForRowSet("select max(ad_detno) from ApplicationDetail where ad_apid=" + id);
		if (sl.next()) {
			if (sl.getObject(1) != null) {
				detno = sl.getInt(1) + 1;
			}
		}
		if (sheetnum > 0) {
			HSSFSheet sheet = (HSSFSheet) wbs.getSheetAt(0);
			// 再遍历行 从第2行开始
			for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
				HSSFRow row = sheet.getRow(i);
				sb.setLength(0);
				sb.append("insert into ApplicationDetail(ad_id,ad_detno,ad_prodcode,ad_qty,ad_delivery,ad_apid) Values( ");
				// 取前5列
				sb.append(baseDao.getSeqId("ApplicationDetail_SEQ") + "," + detno + ",");
				for (int j = 0; j < row.getLastCellNum(); j++) {
					textValue = "";
					HSSFCell cell = row.getCell(j);
					if (cell != null) {
						switch (cell.getCellType()) {
						case HSSFCell.CELL_TYPE_NUMERIC: {
							if (HSSFDateUtil.isCellDateFormatted(cell)) {
								textValue = DateUtil.parseDateToOracleString(Constant.YMD, cell.getDateCellValue());
							} else {
								textValue = cell.getNumericCellValue();
							}
							break;
						}
						case HSSFCell.CELL_TYPE_STRING:
							textValue = cell.getStringCellValue();
							break;
						case HSSFCell.CELL_TYPE_BOOLEAN:
							textValue = cell.getBooleanCellValue();
							break;
						case HSSFCell.CELL_TYPE_FORMULA:
							textValue = cell.getCellFormula() + "";
							break;
						case HSSFCell.CELL_TYPE_BLANK:
							textValue = "";
							break;
						case HSSFCell.CELL_TYPE_ERROR:
							textValue = "";
							break;
						default:
							textValue = "";
							break;
						}
					}
					if (j == 1) {
						// 分配人的情况 最好是按编号找
						if (textValue == "") {
							BaseUtil.showError("提示第" + (i + 1) + "行 没有物料编号");
						} else {
							sb.append("'" + textValue + "',");
						}

					} else if (j == 4) {
						if (textValue.toString().indexOf(".") > 0) {
							// 存在。0 则去掉
							sb.append("'" + textValue.toString().substring(0, textValue.toString().indexOf(".")) + "',");
						} else if (textValue.equals("")) {
							sb.append("null,");
						} else {
							sb.append("'" + textValue + "',");
						}
					} else if (j == 5) {
						sb.append(textValue + ",");
					}

				}
				sb.append(id + ")");
				sqls.add(sb.toString());
				detno++;
			}
		}
		baseDao.execute(sqls);
		return true;
	}

	@Override
	public void applicationdataupdate(int id, String caller) {
		String sqlstrb = "update ApplicationDetail PDD set PDD.ad_b=nvl((select sum(PDS.pd_qty-nvl(PDS.pd_acceptqty,0)) from Purchase,PurchaseDetail PDS where pu_id=pd_puid and PDS.pd_prodcode=PDD.ad_prodcode and nvl(pu_statuscode,' ')<>'FINISH'  and nvl(pu_statuscode,' ')<>'ENTERING' and nvl(PDS.pd_mrpstatuscode,' ')<>'FINISH'),0) where ad_apid="
				+ id;
		String sqlstrc = "update ApplicationDetail set ad_c=NVL((select sum(pw_onhand) from productwh where pw_prodcode=ad_prodcode),0) where ad_apid="
				+ id;
		String sqlstrd = "update ApplicationDetail  PDD set PDD.ad_d=NVL((select sum(ad_qty-nvl(ad_yqty,0)) from application,applicationdetail where ap_id=ad_apid and ad_prodcode=PDD.ad_prodcode and nvl(ap_statuscode,' ')<>'FINISH' and nvl(ap_statuscode,' ')<>'ENTERING' and nvl(ad_statuscode,' ')<>'FINISH' and nvl(ad_mrpstatuscode,' ')<>'FINISH'),0) where ad_apid="
				+ id;
		// String
		// sqlstre="update saleforecastdetail set sd_e=NVL((select sum(pw_onhand) from productwh,warehouse where pw_whcode=wh_code and pw_prodcode=sd_prodcode and nvl(wh_type,' ')='不良品仓'),0) where sd_sfid="+id;
		String sqlstrf = "update ApplicationDetail set ad_f=NVL((select round(sum(pd_outqty)/3,2) from prodinout,prodiodetail where pi_id=prodiodetail.pd_piid and prodiodetail.pd_prodcode=ApplicationDetail.ad_prodcode and pi_class<>'拨出单' and pi_statuscode='POSTED' and dateadd('M',3,pi_date)>=sysdate ),0) where ad_apid="
				+ id;
		// String
		// sqlstrg="update saleforecastdetail set sd_g=NVL((select sum(sd_qty-nvl(sd_sendqty,0)) from sale,saledetail where sale.sa_id=saledetail.sd_said and saledetail.sd_prodcode=saleforecastdetail.sd_prodcode and nvl(sa_statuscode,' ')<>'FINISH' and nvl(sa_statuscode,' ')<>'ENTERING' and nvl(saledetail.sd_statuscode,' ')<>'FINISH'),0) where sd_sfid="+id;
		List<String> sqls = new ArrayList<String>();
		sqls.add(sqlstrb);
		sqls.add(sqlstrc);
		sqls.add(sqlstrd);
		// sqls.add(sqlstre);
		sqls.add(sqlstrf);
		// sqls.add(sqlstrg);
		baseDao.execute(sqls);
	}

	private void getGoodsReserve(int ap_id) {
		// 请购单明细行有匹配的UUID的物料，逐一调用方法获取平台库存信息，
		// 调用之前判断B2C$GoodsOnhand的go_synctime距离现在是否超过了1小时。一小时内同步过的不再同步
		SqlRowList rs = baseDao.queryForRowSet("select distinct pr_uuid from applicationdetail left join application on ap_id=ad_apid"
				+ " left join product on pr_code=ad_prodcode where ap_id=" + ap_id + " and nvl(pr_uuid,' ')<>' '"
				+ " and pr_uuid not in (select go_uuid from B2C$GoodsOnhand where ROUND(TO_NUMBER(sysdate-go_synctime) * 24)<1)");
		if (rs.next()) {
			StringBuffer strs = new StringBuffer();
			for (Map<String, Object> map : rs.getResultList()) {
				strs.append(map.get("pr_uuid") + ",");
			}
			String uuids = strs.substring(0, strs.length() - 1);
			if (StringUtil.hasText(uuids)) {
				getGoodsReserveService.getGoodsOnhand(uuids);
				getGoodsReserveService.getGoodsBatch(uuids);
			}
		}
	}
	/**
	 * 保存、更新、提交自动取最新采购单单价
	 * @param ap_id
	 */
	private void getProdPrice(int ap_id){
		List<Object[]>objs = baseDao.getFieldsDatasByCondition("applicationdetail", new String[]{"ad_prodcode","ad_id"}, "ad_apid="+ap_id);
		for(Object[] obj:objs){
			baseDao.execute("update applicationdetail set ad_price=(select nvl(price,0) from "
					+ "(select round(nvl(pd_price,0)*nvl(pu_rate,0),8) price from purchasedetail left join purchase on pd_puid=pu_id "
					+ " where pu_auditdate is not null and pu_statuscode='AUDITED' and pd_prodcode='"+obj[0]+"' order by pu_auditdate desc,pd_id desc) "
					+ " WHERE rownum<2) where ad_id="+obj[1]+" and nvl(ad_price,0)=0 and EXISTS (select 1 from purchasedetail left join purchase on pd_puid=pu_id "
					+ " where pu_statuscode='AUDITED' and pd_prodcode='"+obj[0]+"')");
		}
	}

	@Override
	public void updateQty(String data) {
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(data);
		Object ap_id = baseDao.getFieldDataByCondition("applicationdetail", "ad_apid", "ad_id="+map.get("ad_id")+"");
		double ad_qty = map.get("ad_qty")==null?0:Double.parseDouble(map.get("ad_qty").toString());
		double ad_yqty = map.get("ad_yqty")==null?0:Double.parseDouble(map.get("ad_yqty").toString());
		if(ad_qty<ad_yqty){
			BaseUtil.showError("变更数量："+ad_qty+"不能小于已转数量："+ad_yqty+"");
		}
		baseDao.execute("update applicationdetail set ad_qty="+map.get("ad_qty")+" where ad_id="+map.get("ad_id")+"");
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "更新明细数量", "行"+map.get("ad_detno")+" : "+map.get("ad_oldqty")+"=>"+map.get("ad_qty")+"", map.get("caller")+"|ap_id="+ap_id+""));
	
	}
	/**
	 * 英唐集团  请购单抛转  maz 18-05-25
	 */
	@Override
	public String postApplication(String caller,String data,String to){
		List<Map<Object, Object>> gridStore = BaseUtil.parseGridStoreToMaps(data);
		Employee employee = SystemSession.getUser();
		Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(gridStore, new Object[] { "ad_apid" });
		Set<Object> mapSet = groups.keySet();
		List<Map<Object, Object>> items;
		for (Object s : mapSet) {
			items = groups.get(s);
			Object ap_id = baseDao.getFieldDataByCondition("dual", ""+to+".application_seq.nextval", "1=1");
			Object ap_code = baseDao.sGetMaxNumber(""+to+".application", 2);
			int detno = 1;
			baseDao.execute("Insert into "+to+".application (AP_ID,AP_CODE,AP_SOURCEID,AP_SOURCE,AP_REFCODE,AP_DATE,AP_DELIVERY,AP_COSTCENTER,AP_VENDID,AP_VENDCODE,AP_TOTAL,AP_PAYMENTS,AP_CURRENCY,AP_RATE,AP_PURPOSE,AP_REMARK,AP_STATUSCODE,AP_STATUS,AP_RECORDERID,AP_RECORDDATE,AP_DEPARTMENT,AP_PLEAMANID,AP_PLEAMANNAME,AP_COP,AP_OLDSTATUSCODE,AP_OLDSTATUS,AP_AUDITMAN,AP_AUDITDATE,AP_DATASOURCE,AP_TEL,AP_ACCEPTDEPARTMENT,FIN_CODE,AP_DEPARTCODE,AP_VENDNAME,AP_KIND,AP_DEPARTNAME,AP_CONTACT,AP_RECORDER,AP_TYPE,AP_TURNSTATUSCODE,AP_TURNSTATUS,AP_PRINTSTATUSCODE,AP_PRINTSTATUS,AP_BUYERCODE,AP_BUYERNAME,AP_PARENTORNAME,AP_BONDED,AP_SYNC,AP_PRJCODE,AP_PRJNAME,AP_MRPREMARK,AP_XIJ,AP_REASON) "
					+ "select "+ap_id+",'"+ap_code+"',AP_SOURCEID,AP_SOURCE,AP_REFCODE,AP_DATE,AP_DELIVERY,AP_COSTCENTER,AP_VENDID,AP_VENDCODE,AP_TOTAL,AP_PAYMENTS,AP_CURRENCY,AP_RATE,AP_PURPOSE,AP_REMARK,'ENTERING','在录入','"+employee.getEm_id()+"',sysdate,AP_DEPARTMENT,AP_PLEAMANID,AP_PLEAMANNAME,AP_COP,AP_OLDSTATUSCODE,AP_OLDSTATUS,null,null,AP_DATASOURCE,AP_TEL,AP_ACCEPTDEPARTMENT,FIN_CODE,AP_DEPARTCODE,AP_VENDNAME,AP_KIND,AP_DEPARTNAME,AP_CONTACT,'"+employee.getEm_code()+"',AP_TYPE,AP_TURNSTATUSCODE,AP_TURNSTATUS,AP_PRINTSTATUSCODE,AP_PRINTSTATUS,AP_BUYERCODE,AP_BUYERNAME,AP_PARENTORNAME,AP_BONDED,AP_SYNC,AP_PRJCODE,AP_PRJNAME,AP_MRPREMARK,AP_XIJ,AP_REASON from application where ap_id="+items.get(0).get("ad_apid"));
			for(Map<Object,Object>map:items){
				baseDao.execute("Insert into "+to+".applicationdetail (AD_DETNO,AD_PRODID,AD_PRODCODE,AD_QTY,AD_PRICE,AD_TOTAL,AD_DELIVERY,AD_WAREHOUSEID,AD_BARCODE,AD_VENDID,AD_SOURCE,AD_SOURCEID,AD_REMARK,AD_STATUSCODE,AD_STATUS,AD_JYPUDATE,AD_YT,AD_VENDOR,AD_SOURCECODE,AD_VENDNAME,AD_MRPCODE,AD_MDID,AD_YQTY,AD_ID,AD_APID,AD_CURRENCY,AD_TQTY,AD_PHRASE,AD_MINPACK,AD_MINORDER,AD_MRPQTY,AD_USE,AD_LEADTIME,AD_CODE,AD_BONDED,AD_IFREP,AD_MRPSTATUSCODE,AD_MRPSTATUS,AD_PLANCODE,AD_PLANDETNO,AD_B,AD_C,AD_D,AD_F,AD_IFVENDRATE,AD_FACTORY,AD_PUQTY_USER,AD_CUSTCODE,AD_CUSTNAME,AD_SELLERCODE,AD_SELLER,AD_PRJCODE,AD_PRJNAME,AD_SACODE,AD_SADETNO,AD_PURCPRICE,AD_RATE,AD_TOPMOTHERCODE,AD_PPDID,AD_REASON,AD_OLDQTY) "
						+ "select "+detno+++",AD_PRODID,AD_PRODCODE,"+map.get("ad_tqty")+",AD_PRICE,AD_TOTAL,AD_DELIVERY,AD_WAREHOUSEID,AD_BARCODE,AD_VENDID,AD_SOURCE,AD_SOURCEID,AD_REMARK,AD_STATUSCODE,AD_STATUS,AD_JYPUDATE,AD_YT,AD_VENDOR,AD_SOURCECODE,AD_VENDNAME,AD_MRPCODE,AD_MDID,AD_YQTY,"+to+".applicationdetail_seq.nextval,"+ap_id+",AD_CURRENCY,AD_TQTY,AD_PHRASE,AD_MINPACK,AD_MINORDER,AD_MRPQTY,AD_USE,AD_LEADTIME,AD_CODE,AD_BONDED,AD_IFREP,AD_MRPSTATUSCODE,AD_MRPSTATUS,AD_PLANCODE,AD_PLANDETNO,AD_B,AD_C,AD_D,AD_F,AD_IFVENDRATE,AD_FACTORY,AD_PUQTY_USER,AD_CUSTCODE,AD_CUSTNAME,AD_SELLERCODE,AD_SELLER,AD_PRJCODE,AD_PRJNAME,AD_SACODE,AD_SADETNO,AD_PURCPRICE,AD_RATE,AD_TOPMOTHERCODE,AD_PPDID,AD_REASON,AD_OLDQTY from applicationdetail where ad_id="+map.get("ad_id"));
				//插入原帐套的日志给新帐套，并且新增一条新的日志
				baseDao.execute("Insert into "+to+".messagelog (ML_ID,ML_DATE,ML_MAN,ML_CONTENT,ML_RESULT,ML_SEARCH,CODE) select "+to+".messagelog_seq.nextval,ML_DATE,ML_MAN,ML_CONTENT,ML_RESULT,ML_SEARCH,CODE from messagelog where ml_search like '%Application|ap_id="+map.get("ad_apid")+"%'");
				baseDao.execute("Insert into "+to+".messagelog (ML_ID,ML_DATE,ML_MAN,ML_CONTENT,ML_RESULT,ML_SEARCH,CODE) select "+to+".messagelog_seq.nextval,sysdate,'"+employee.getEm_name()+"','抛转操作','从"+SpObserver.getSp()+"抛转到"+to+"','Application|ap_id="+ap_id+"','"+ap_code+"' from dual");
				//更新原帐套的内容
				baseDao.execute("update applicationdetail set ad_qty=ad_qty-"+map.get("ad_tqty")+" where ad_id="+map.get("ad_id"));
				baseDao.execute("Insert into messagelog (ML_ID,ML_DATE,ML_MAN,ML_CONTENT,ML_RESULT,ML_SEARCH,CODE) select messagelog_seq.nextval,sysdate,'"+employee.getEm_name()+"','抛转操作','从"+SpObserver.getSp()+"抛转到"+to+"','Application|ap_id="+map.get("ad_apid")+"','"+ap_code+"' from dual");
			}
		}
		return "OK";
	}
	@Override
	public Object ApplicationTurnMake(String caller,String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String adidstr = "";
		int maId = 0;
		String log = null;
		StringBuffer sb =  new StringBuffer();
		String NewCode =null;//工单编号
		String makelCode ="";//工单前缀
		SqlRowList rs = new SqlRowList();
		Object maKind  = maps.get(0).get("ma_kind");
		String mkMakind = null;
		String mkBatchtype = null;
		//判断工单类型是否存在、已启用 mk_isuse
		if(StringUtil.hasText(maKind)){
			rs = baseDao.queryForRowSet("select mk_code,mk_makind,mk_excode,mk_batchtype from makekind where nvl(mk_isuse,0) = -1  and mk_name = '"+maKind+"'");
			if(rs.next()){
				makelCode = rs.getGeneralString("mk_excode");
				mkMakind = rs.getGeneralString("mk_makind");
				mkBatchtype = rs.getGeneralString("mk_batchtype");
			}else{
				BaseUtil.showError("工单类型不存在或未启用！");
			}
		}else{
			BaseUtil.showError("工单类型为空，不允许转单！");
		}
		//获取所有请购单明细id
		String errorlLog ="";
		for (Map<Object, Object> map : maps) {
			adidstr += "," + map.get("ad_id").toString();
			//判断是否超请购数
			rs = baseDao.queryForRowSet("select ad_code,ad_detno , ad_prodcode ,ad_qty-ad_yqty restQty,ABS(ad_qty-ad_yqty-("+map.get("ad_tqty")+")) countQty from applicationdetail "
					+ "WHERE ad_id = "+map.get("ad_id")+"  and (ad_qty-ad_yqty-("+map.get("ad_tqty")+")) < 0" );
			if(rs.next()){
				if(rs.getObject("ad_detno")!=null){
					errorlLog+="请购单号："+rs.getGeneralString("ad_code")+"，行："+rs.getGeneralString("ad_detno")+",物料编号： "+rs.getGeneralString("ad_prodcode")
					+"，本次数量填写超出可转数量:"+rs.getGeneralDouble("restQty")+",超出："+rs.getGeneralDouble("countQty")+"<hr>";
				}
			}
		}
			adidstr = adidstr.substring(1);
		if(errorlLog!=""){
			BaseUtil.showError(errorlLog);
		}
		
		//提示[物料：XXX不满足条件，不能转工单]
		rs = baseDao.queryForRowSet("select wm_concat(ad_prodcode) prodcode "
				+ " from  application,applicationdetail,product  "
				+ " where ap_id=ad_apid and pr_code = ad_prodcode and (nvl(pr_manutype,' ') not in('MAKE','OSMAKE') or nvl(pr_dhzc,' ')<>'MPS') and ad_id in ("+adidstr+")" );
		if(rs.next()){
			if(rs.getObject("prodcode")!=null){
				BaseUtil.showError("物料编号： "+rs.getGeneralString("prodcode")+"制造类型或计划类型不满足条件，不能转工单!");
			}
		}
		//检查是否有已审核的BOM 没有时提示【物料：XXX没有BOM资料或BOM资料状态不为已审核，不能转工单】
		rs = baseDao.queryForRowSet("select wm_concat(ad_prodcode) prodcode "
				+ " from applicationdetail left join  application on ap_id = ad_apid left join bom on bo_mothercode = ad_prodcode "
				+ " where nvl(BO_STATUSCODE, ' ')<>'AUDITED' and ad_id in ("+adidstr+")");
		if(rs.next()){
			if(rs.getObject("prodcode")!=null){
				BaseUtil.showError("物料编号："+rs.getGeneralString("prodcode")+" 没有BOM资料或BOM资料状态不为已审核，不能转工单！");
			}
		}
		//检查工单类型和物料类型是否匹配  mk_code
		rs = baseDao.queryForRowSet("select wm_concat(ad_prodcode) prodcode from  application,applicationdetail,product where ap_id=ad_apid and pr_code = ad_prodcode and nvl(pr_manutype,' ') <> '"+mkMakind+"' and ad_id in ("+adidstr+")");
		if(rs.next()){
			if(rs.getObject("prodcode")!=null){
			BaseUtil.showError("物料编号："+rs.getGeneralString("prodcode")+" 物料制造类型与转入工单制造类型不一致，不能转工单！");
			}
		}
		int adId = 0;
		Double adTqty = 0.0;
		String ma_vendcode = "";
		String ma_planbegindate = null;
		String ma_planenddate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Calendar calendar = Calendar.getInstance();
		boolean bool = false;
		Double rate = 0.0;
		String ma_statuscode ="ENTERING";
		String ma_status = "在录入";
		String ma_tasktype ="MAKE";
		if(mkMakind.equals("OSMAKE")){
			ma_tasktype = "OS";
			ma_statuscode ="AUDITED";
			ma_status = "已审核";
		}
		for (Map<Object, Object> map : maps) {
			bool = false;
			rate = 0.0;
			adId = Integer.valueOf(map.get("ad_id").toString());
			calendar = Calendar.getInstance();
			rs = baseDao.queryForRowSet("select ad_prodcode,pr_id,pr_detail,pr_spec,ap_currency from  application,applicationdetail,product  "
					+ " where ap_id=ad_apid and pr_code = ad_prodcode and ad_id = "+adId );
			maId = baseDao.getSeqId("MAKE_SEQ");
			NewCode = getNewcode(makelCode.toString(),"Make!Base");
			adTqty = Double.valueOf(map.get("ad_tqty").toString());
			ma_planbegindate = map.get("ad_delivery").toString().substring(0, 10)+" 00:00:00";
			
			if(!StringUtil.hasText(map.get("ap_reason"))){
				Object endDays = 2;
				if(baseDao.checkIf("configs", "caller ='Application!ToMake!Deal' and code ='defaultEndDays'")){
					endDays = baseDao.getFieldDataByCondition("configs", "nvl(data,2)", "caller ='Application!ToMake!Deal' and code ='defaultEndDays'");
				}
				if(endDays!=null){
					try{
						int endDay = Integer.valueOf(endDays.toString());
						if(endDay>=0){
							calendar.setTime(sdf.parse(ma_planbegindate, new ParsePosition(0)));
							calendar.add(Calendar.DATE, Integer.valueOf(endDays.toString()));
							ma_planenddate = sdf.format(calendar.getTime());
						}else{
							BaseUtil.showError("默认工单完工周期设置有误，请重新设置！");
						}
					}catch(Exception e){
						e.printStackTrace();
						BaseUtil.showError("默认工单完工周期设置有误，请重新设置！");
					}
				}else{
					BaseUtil.showError("请维护默认工单完工周期，或填写明细完工周期！");
				}
			}else{
				ma_planenddate = map.get("ap_reason").toString().substring(0, 10)+" 00:00:00";
			}
			if(rs.next()){
				if(rs.getObject("ap_currency")!=null){
					rate = baseDao.queryForObject("select cm_crrate from currencysmonth where cm_crname = '"+rs.getString("ap_currency")+"' and "
							+ "cm_yearmonth=to_char(sysdate,'yyyymm')", Double.class);
				}
			bool = baseDao.execute(INSERTMAKE,new Object[]{
					ma_statuscode,ma_status,maId,NewCode,ma_tasktype,maKind,mkBatchtype,adId,
					Timestamp.valueOf(ma_planbegindate),Timestamp.valueOf(ma_planenddate),
					rs.getGeneralInt("pr_id"),rs.getGeneralString("ad_prodcode"),rs.getGeneralString("pr_detail"),rs.getGeneralString("pr_spec"),
					rate,adTqty,SystemSession.getUser().getEm_name(), SystemSession.getUser().getEm_id()});
			}
			if(bool){
				if(mkMakind.equals("OSMAKE")){//解决sqlmap越界问题
					if(StringUtil.hasText(map.get("ma_vendcode"))){
						ma_vendcode = map.get("ma_vendcode").toString();
					}else if (StringUtil.hasText(map.get("ad_vendor"))){
						ma_vendcode = map.get("ad_vendor").toString();
					}
					rs = baseDao.queryForRowSet("select ve_code,ve_name,ve_payment,ve_paymentcode,ppd_currency,ppd_rate,ppd_price from  Vendor left join PurchasePriceDetail on ve_code=ppd_vendcode  "
							+ " where ve_code = '"+ma_vendcode +"'");
					if(rs.next()){
						baseDao.execute("update make set ma_vendcode = '"+ma_vendcode
								+ "',ma_vendname='"+rs.getGeneralString("ve_name")
								+ "',ma_payments='"+rs.getGeneralString("ve_payment")
								+ "',ma_paymentscode='"+rs.getGeneralString("ve_paymentcode")
								+ "',ma_currency='"+rs.getGeneralString("ppd_currency")
								+ "',ma_taxrate='"+rs.getGeneralString("ppd_rate")
								+ "',ma_price='"+rs.getGeneralString("ppd_price")
								+ "' where ma_id = "+maId);
					}
				}
				baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "请购转工单", 
						"行"+map.get("ad_detno")+" : ap_id: "+adId+"=> 工单号："+NewCode+"，id: "+maId, "Make!Base|ma_id="+maId));
				// 计算用料表生成明细 Make!Base
				makeBaseService.setMakeMaterial(NewCode,"Make!Base");
				//更新
				updateApplicationTurnMakeStatus(adId);
				baseDao.execute("update applicationdetail set ad_yqty = ad_yqty+"+adTqty+" where ad_id =  "+adId);
				log = "转入成功,工单号:" + "<a href=\"javascript:openUrl('jsps/pm/make/makeBase.jsp?whoami=Make!Base&formCondition=ma_idIS" + maId
						+ "&gridCondition=mm_maidIS" + maId + "')\">" + NewCode + "</a>&nbsp;";
				sb.append(log).append("<hr>");
			}
		}
		return sb;
	}
	/**
	 * 修改请购单转工单状态
	 */
	public void updateApplicationTurnMakeStatus(Object adId) {
		Object apid = baseDao.getFieldDataByCondition("ApplicationDetail", "ad_apid", "ad_id=" + adId);
		int count = baseDao.getCountByCondition("ApplicationDetail", "ad_apid=" + apid);
		int yCount = baseDao.getCountByCondition("ApplicationDetail", "ad_apid=" + apid + " AND ad_yqty>=ad_qty AND NVL(ad_yqty,0)>0");
		int nCount = baseDao.getCountByCondition("ApplicationDetail", "ad_apid=" + apid + " AND NVL(ad_yqty,0)=0");
		String status = "PART2MA2";
		if (nCount == count) {
			status = "";
		} else if (yCount == count) {
			status = "TURNMA2";
		}
		baseDao.execute("UPDATE Application set ap_turnstatuscode=?,ap_turnstatus=? where ap_id=?", status, BaseUtil.getLocalMessage(status), apid);
	}

	@Override
	public void getVendorByCaller(int[] id, String caller) {
		String GETOSMAKEVENDOR = "SELECT ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid,ppd_price,ppd_rate FROM (SELECT ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid,ppd_price,ppd_rate FROM PurchasePriceDetail,Currencys,PurchasePrice WHERE "
				+ "ppd_currency=cr_name and pp_id=ppd_ppid and pp_statuscode='AUDITED' and ppd_statuscode='VALID' and pp_kind='委外' and ppd_prodcode=(SELECT ad_prodcode FROM applicationdetail where ad_id=?) and ppd_lapqty<=(select ad_qty from applicationdetail where ad_id=?) and nvl(ppd_todate, nvl(pp_todate, sysdate)) + 1>sysdate order by  ppd_netprice asc,pp_indate desc,ppd_id desc) where rownum<2";
		SqlRowList rs = new SqlRowList();
		for(int idx : id){
			if(caller.equals("Application!ToMake!Deal")){
				rs = baseDao.queryForRowSet(GETOSMAKEVENDOR,idx,idx);
			}else{
				rs = baseDao.queryForRowSet(GETOSMAKEVENDOR,idx,idx);
			}
			if(rs.next()){
				baseDao.execute("UPDATE ApplicationDetail SET ad_ifvendrate=0,ad_vendor=?,ad_vendname=?,ad_currency=?,ad_vendid=?,ad_barcode=?,ad_purcprice=?,ad_rate=? WHERE ad_id=?",new Object[] { rs.getObject(1), rs.getObject(2), rs.getObject(3), rs.getObject(4) ,"", rs.getObject(5),rs.getObject(6), idx });
			}
		}		
	}
	private String getNewcode(String str,String caller){
		Object[] form = baseDao.getFieldsDataByCondition("form", "fo_table,fo_codefield", "fo_caller = '"+caller+"'");
		String newCode = str + baseDao.sGetMaxNumber(caller, 2);
		if(baseDao.checkIf(form[0].toString(), form[1].toString()+"='"+newCode+"'")){
			newCode = getNewcode( str, caller);
		}
		return newCode;
	}
}
