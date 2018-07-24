package com.uas.erp.service.pm.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.bind.Status;
import com.uas.erp.core.support.Assert;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.MakeDao;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.common.ProcessService;
import com.uas.erp.service.pm.MakeMouldService;

@Service
public class MakeMouldImpl implements MakeMouldService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private MakeDao makeDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private ProcessService processService;
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveMakeMould(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		String code = store.get("ma_code").toString();
		baseDao.asserts.nonExistCode("Make", "ma_code", code);
		for (Map<Object, Object> s : gstore) {
			// 判断可拆数不能小于拆件入库数
			if (((s.get("mm_gqty")) != null) && ((s.get("mm_yqty")) != null)) {
				if (Double.valueOf(String.valueOf(s.get("mm_gqty"))) < Double.valueOf(String.valueOf(s.get("mm_yqty"))))
					BaseUtil.showError("可拆件数量" + s.get("mm_gqty") + "不能小于 拆件入库数(" + s.get("mm_yqty") + ")");
			}
		}
		// 初始化完工状态
		store.put("ma_checkstatuscode", "UNAPPROVED");
		store.put("ma_checkstatus", BaseUtil.getLocalMessage("UNAPPROVED"));
		store.put("ma_finishstatuscode", "UNCOMPLET");
		store.put("ma_finishstatus", BaseUtil.getLocalMessage("UNCOMPLET"));
		store.put("ma_printstatuscode", "UNPRINT");
		store.put("ma_printstatus", BaseUtil.getLocalMessage("UNPRINT"));
		store.put("ma_turnstatuscode", "UNGET");
		store.put("ma_turnstatus", BaseUtil.getLocalMessage("UNGET"));
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, store, gstore);
		// 保存
		Object[] objs = baseDao.getFieldsDataByCondition("Product", new String[] { "pr_whcode", "pr_detail" },
				"pr_code='" + store.get("ma_prodcode") + "'");
		store.put("ma_whcode", objs[0]);
		String formSql = SqlUtil.getInsertSqlByMap(store, "Make");
		baseDao.execute(formSql);
		baseDao.execute("update make set (ma_prodname,ma_prodspec) =(select pr_detail,pr_spec from product where pr_code=ma_prodcode) where ma_id="
				+ store.get("ma_id"));
		baseDao.execute("update make set (ma_paymentscode,ma_payments,ma_vendname)=(select ve_paymentcode, ve_payment, ve_name from vendor where ve_code=ma_vendcode) where ma_id="
				+ store.get("ma_id") + " and nvl(ma_paymentscode,' ')=' '");
		// 更新汇率
		baseDao.execute("update make set ma_rate=(select cm_crrate from currencysmonth where ma_currency=cm_crname and "
				+ "cm_yearmonth=to_char(ma_date,'yyyymm')) where ma_id=? and nvl(ma_rate,0)=0 ", store.get("ma_id"));
		useDefaultTax(caller, store.get("ma_id"));
		// 保存MakeMaterial
		for (Map<Object, Object> s : gstore) {
			s.put("mm_code", code);
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyList(gstore, "MakeMaterial", "mm_id");
		baseDao.execute(gridSql);
		baseDao.execute("update MakeMaterial set mm_code=(select ma_code from make where mm_maid=ma_id) where mm_maid="
				+ store.get("ma_id") + " and not exists (select 1 from make where mm_code=ma_code)");
		baseDao.execute("update make set ma_total=round(nvl(ma_price,0)*nvl(ma_qty,0),2) where ma_id=" + store.get("ma_id"));
		baseDao.execute("update make set ma_totalupper=L2U(nvl(ma_total,0)) WHERE ma_id=" + store.get("ma_id"));
		// 记录操作
		baseDao.logger.save(caller, "ma_id", store.get("ma_id"));
		makeDao.setBalance(store.get("ma_id").toString(), "");
		makeDao.saveDefault(store.get("ma_id").toString(), "");
		// 产品编号不能为外购、客供和虚拟的物料
		CustOrPurs(caller, Integer.valueOf(store.get("ma_id").toString()));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, store, gstore);
	}

	@Override
	public void deleteMakeMould(int ma_id, String caller) {
		// 只能删除在录入的单据!
		SqlRowList status = baseDao.queryForRowSet("select ma_statuscode,ma_checkstatuscode from make where ma_id=?", ma_id);
		if (status.next()) {
			if (!status.getString("ma_statuscode").equals("ENTERING")
					&& (status.getString("ma_checkstatuscode").equals("APPROVE") || status.getString("ma_checkstatuscode").equals(
							"COMMITED"))) {
				BaseUtil.showError("只能删除在录入且批准状态不等于已批准或已提交的工单");
			}
		}
		//更新销售单明细中的sd_tomakeqty
		SqlRowList field = baseDao.queryForRowSet("select ma_salecode,ma_saledetno,ma_qty,ma_prodcode from make where ma_id="+ma_id);
		if(field.next()){
			String ma_salecode = field.getString("ma_salecode");
			String ma_saledetno = field.getString("ma_saledetno");
			double ma_qty = field.getDouble("ma_qty");
			if(ma_salecode!=null&&ma_saledetno!=null){
				baseDao.execute(" update SALEDETAIL set sd_tomakeqty=sd_tomakeqty-? where sd_code=? and sd_detno=? and sd_prodcode=?",ma_qty,ma_salecode,ma_saledetno,field.getString("ma_prodcode"));
				baseDao.execute(" update SALEFORECASTDETAIL set sd_tomakeqty=sd_tomakeqty-? where sd_code=? and sd_detno=? and sd_prodcode=?",ma_qty,ma_salecode,ma_saledetno,field.getString("ma_prodcode"));
			}
		}
		if(baseDao.isDBSetting("usingMakeCraft")){//启用工厂模式  有对应的作业单 制造单不能删除
			int count=baseDao.getCount("Select count(1) from makecraft left join make on mc_makecode=ma_code where ma_id="+ma_id);
			if(count>0){
				BaseUtil.showError("当前工单存在作业单，不能删除");
			}
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, ma_id);
		// 删除Make
		/* makeDao.deleteMake(ma_id); */
		baseDao.deleteById("Make", "ma_id", ma_id);
		// 删除替代物料
		baseDao.deleteByCondition("MakeMaterialReplace", "mp_mmid in (select mm_id from makematerial where mm_maid='" + ma_id + "')");
		// 删除MakeMaterial
		baseDao.deleteById("MakeMaterial", "mm_maid", ma_id);
		// 删除MakeCraftPieceWork 制造单工序记录表
		baseDao.deleteByCondition("MakeCraftPieceWork", "mcp_maid=" + ma_id);
		// 记录操作
		baseDao.logger.delete(caller, "ma_id", ma_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ma_id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateMakeMould(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		boolean mbo = baseDao.isDBSetting(caller, "checkWorkCenter");
		if (mbo) {
			int ma_id = Integer.parseInt(store.get("ma_id").toString());
			checkWorkCenter(ma_id);
		}
		// 当前编号的记录已经存在,不能修改!
		String code = store.get("ma_code").toString();
		baseDao.asserts.isFalse("Make", "ma_code='" + code + "' AND ma_id<>" + store.get("ma_id"), "common.save_codeHasExist");
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("Make", "ma_statuscode", "ma_id=" + store.get("ma_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, store, gstore);
		for (Map<Object, Object> s : gstore) {
			// 判断是否配置了mm_yqty和mm_gqty这两个字段
			if (s.get("mm_gqty") != null && s.get("mm_yqty") != null && !"".equals(s.get("mm_gqty")) && !"".equals(s.get("mm_yqty"))) {
				// 判断可拆数不能小于拆件入库数
				if (Double.valueOf((s.get("mm_gqty").toString())) < Double.valueOf((s.get("mm_yqty").toString())))
					BaseUtil.showError("可拆件数量" + s.get("mm_gqty") + "不能小于 拆件入库数(" + s.get("mm_yqty") + ")");
			}
		}
		// 避免保存了完工数
		if (store.containsKey("ma_madeqty")) {
			store.remove("ma_madeqty");
		}
		// 记录更新日期和更新人
		if (store.containsKey("ma_updateman")) {
			store.remove("ma_updateman");
		}
		if (store.containsKey("ma_updatedate")) {
			store.remove("ma_updatedate");
		}

		store.put("ma_checkstatuscode", "UNAPPROVED");
		store.put("ma_checkstatus", BaseUtil.getLocalMessage("UNAPPROVED"));
		store.put("ma_finishstatuscode", "UNCOMPLET");
		store.put("ma_finishstatus", BaseUtil.getLocalMessage("UNCOMPLET"));
		store.put("ma_printstatuscode", "UNPRINT");
		store.put("ma_printstatus", BaseUtil.getLocalMessage("UNPRINT"));
		store.put("ma_updateman", SystemSession.getUser().getEm_name());
		store.put("ma_updatedate", DateUtil.currentDateString("yyyy-MM-dd HH:mm:ss"));
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Make", "ma_id");
		baseDao.execute(formSql);
		baseDao.execute("update make set (ma_paymentscode,ma_payments,ma_vendname)=(select ve_paymentcode, ve_payment, ve_name from vendor where ve_code=ma_vendcode ) where ma_id="
				+ store.get("ma_id") + " and nvl(ma_paymentscode,' ')=' '");
		// 更新汇率
		baseDao.execute("update make set ma_rate=(select cm_crrate from currencysmonth where ma_currency=cm_crname and "
				+ "cm_yearmonth=to_char(ma_date,'yyyymm')) where ma_id=? and nvl(ma_rate,0)=0 ", store.get("ma_id"));
		// 更新ma_prodname
		baseDao.execute("update make set (ma_prodname,ma_prodspec) =(select pr_detail,pr_spec from product where pr_code=ma_prodcode) where ma_id="
				+ store.get("ma_id"));
		useDefaultTax(caller, store.get("ma_id"));
		// 修改MakeMaterial
		Object whcode = null;
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "MakeMaterial", "mm_id");
		Object detno = baseDao.getFieldDataByCondition("MakeMaterial", "max(mm_detno)+1", "mm_maid=" + store.get("ma_id"));
		int n = detno != null ? Integer.parseInt(String.valueOf(detno)) : 1;
		for (Map<Object, Object> s : gstore) {
			if (s.get("mm_id") == null || s.get("mm_id").equals("") || s.get("mm_id").equals("0")
					|| Integer.parseInt(s.get("mm_id").toString()) == 0) {// 新添加的数据，id不存在
				s.put("mm_detno", n);
				s.put("mm_code", code);
				s.put("mm_id", baseDao.getSeqId("MAKEMATERIAL_SEQ"));
				// 更新明细的发料仓
				whcode = s.get("mm_whcode");
				if (whcode == null || "".equals(whcode)) {
					// 发料仓为空 设置
					whcode = baseDao.getFieldDataByCondition("Product", "pr_whcode", "pr_code='" + s.get("mm_prodcode") + "'");
					if (whcode != null) {
						s.remove("mm_whcode");
						s.put("mm_whcode", whcode);
					}
				}
				gridSql.add(SqlUtil.getInsertSqlByMap(s, "MakeMaterial"));
				n++;
			}
		}
		baseDao.execute(gridSql);
		// 避免更新了工单号用料表的工单号未更新
		baseDao.execute("update makematerial set mm_code='" + store.get("ma_code") + "' where mm_maid=" + store.get("ma_id")
				+ " and nvl(mm_code,' ')<>'" + store.get("ma_code") + "'");
		baseDao.execute("update make set ma_total=round(nvl(ma_price,0)*nvl(ma_qty,0),2) where ma_id=" + store.get("ma_id"));
		baseDao.execute("update make set ma_totalupper=L2U(nvl(ma_total,0)) WHERE ma_id=" + store.get("ma_id"));
		/**
		 * @Tip 防止更新了工单号，未更新替代料中的工单号
		 * @author XiaoST 2016年8月18日 上午11:39:42
		 */
		baseDao.execute("update MakeMaterialReplace set mp_mmcode='" + store.get("ma_code") + "' where mp_maid=" + store.get("ma_id")
				+ " and nvl(mp_mmcode,' ')<>'" + store.get("ma_code") + "'");
		makeDao.setBalance(store.get("ma_id").toString(), "");
		makeDao.saveDefault(store.get("ma_id").toString(), "");
		// 记录操作
		baseDao.logger.update(caller, "ma_id", store.get("ma_id"));
		// 产品编号不能为外购、客供和虚拟的物料
		CustOrPurs(caller, Integer.valueOf(store.get("ma_id").toString()));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, store, gstore);
	}

	@Override
	public void auditMakeMould(int ma_id, String caller) {
		baseDao.execute("update make set ma_total=round(nvl(ma_price,0)*nvl(ma_qty,0),2) where ma_id=" + ma_id);
		baseDao.execute("update make set ma_totalupper=L2U(nvl(ma_total,0)) WHERE ma_id=" + ma_id);
		// 只能对状态为[在录入]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Make", "ma_statuscode", "ma_id=" + ma_id);
		Assert.isEquals("common.statuswrong", "ENTERING", status);
		useDefaultTax(caller, ma_id);
		allowZeroTax(caller, ma_id);
		checkAll(caller, ma_id);
		//@add 20170112 新增限制，主料中的替代料维护数不允许大于主料用量
		SqlRowList rs0 = baseDao.queryForRowSet("select wm_concat(mm_detno) detno, count(1) cn from makematerial left join product on MM_PRODCODE =pr_code where mm_maid=? and NVL(pr_putouttoint,0)=0 and nvl(mm_canuserepqty,0)>mm_qty and rownum<30",ma_id);
		if(rs0.next() && rs0.getInt("cn")>0){
			BaseUtil.showError("替代料可维护数不允许大于制单需求数，序号："+rs0.getString("detno"));
	    }
		// 根据配置参数
		Object str;
		if ("Make!Base".equals(caller)) {// 制造单
			boolean mbo = baseDao.isDBSetting(caller, "checkProductMake");
			if (mbo) {
				str = baseDao.getFieldDataByCondition("make left join product on pr_code=ma_prodcode", "pr_code", "ma_id=" + ma_id
						+ " and pr_manutype<>'MAKE'");
				if (str != null) {
					BaseUtil.showError("制造单产品编号必须是生产类型为制造的物料");
				}
			}
			// 检查工作中心的是否计件字段的值
			mbo = baseDao.isDBSetting(caller, "checkWorkCenter");
			if (mbo) {
				checkWorkCenter(ma_id);
			}
			// 安嵘生成制造单工序
			Object ma_code = baseDao.getFieldDataByCondition("Make", "ma_code", "ma_id = " + ma_id);
			try {
				baseDao.callProcedure("MM_MAKECRAFTPIECEWORK",
						new String[] { ma_code.toString(), SystemSession.getUser().getEm_name()});
			} catch (Exception e) {
				
			}
		} else if ("Make".equals(caller)) {// 委外单
			boolean osbo = baseDao.isDBSetting(caller, "checkProductOSMake");
			if (osbo) {
				str = baseDao.getFieldDataByCondition("make left join product on pr_code=ma_prodcode", "pr_code", "ma_id=" + ma_id
						+ " and pr_manutype<>'OSMAKE'");
				if (str != null) {
					BaseUtil.showError("委外单产品编号必须是生产类型为委外的物料");
				}
			}
		}
		//更新销售明细单中的sd_tomakeqty
		updateSd_tomakeqty(ma_id);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { ma_id, }); // 执行审核操作
		baseDao.audit("Make", "ma_id=" + ma_id, "ma_status", "ma_statuscode", "ma_auditdate", "ma_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "ma_id", ma_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ma_id);
		if(baseDao.isDBSetting("usingMakeCraft")){//启用工厂模式  制造单审核后审核对应的车间作业单状更新为已审核 			
			baseDao.updateByCondition("makecraft", "mc_status='已审核'，mc_statuscode='AUDITED'", "nvl(mc_tasktype,' ')='车间作业单' and MC_MAKECODE=(SELECT MA_CODE FROM MAKE WHERE MA_ID="+ma_id+")");
		}
	}

	@Override
	public void resAuditMakeMould(int ma_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] objs = baseDao.getFieldsDataByCondition("Make", new String[] { "ma_statuscode", "ma_madeqty", "ma_checkstatuscode" },
				"ma_id=" + ma_id);
		if (!"AUDITED".equals(objs[0])) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.make.makebase.resAudit_onlyAudit"));
		} else if (objs[2] != null && (objs[2].toString().equals("COMMITED") || objs[2].toString().equals("APPROVE"))) {
			BaseUtil.showError("只能反审核未批准的工单");
		} else if (Integer.parseInt(objs[1].toString()) > 0) {
			BaseUtil.showError("已产生完工数，不能反审核");
		}
		String haveGet = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WMSYS.WM_CONCAT(mm_detno) from makematerial where mm_maid=? and (mm_code,mm_detno) in (select pd_ordercode,pd_orderdetno from prodiodetail where pd_piclass in ('生产领料单','委外领料单','生产退料单','委外退料单','生产补料单','委外补料单'))",
						String.class, ma_id);
		if (haveGet != null) {
			BaseUtil.showError("当前工单的明细行" + haveGet + "发生过领退料操作，无法反审核!");
		}
		// 判断是否存在已提交，待执行的制造ECN
		haveGet = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(distinct mc_code) from MakeMaterialChange left join MakeMaterialChangeDet on md_mcid=mc_id left join make on ma_code=md_makecode where ma_id=? and mc_statuscode='COMMITED' and nvl(md_didstatus,'待执行')='待执行'",
						String.class, ma_id);
		if (haveGet != null) {
			BaseUtil.showError("当前工单存在已提交待执行的制造ECN，无法反审核!");
		}
		if(baseDao.isDBSetting("usingMakeCraft")){//启用工厂模式  有对应的作业单 制造单不能反审核
			int count=baseDao.getCount("Select count(1) from makecraft left join make on mc_makecode=ma_code where ma_id="+ma_id);
			if(count>0){
				BaseUtil.showError("当前工单存在作业单，不能反审核");
			}
		}
		// 执行反审核操作
		baseDao.resOperate("Make", "ma_id=" + ma_id, "ma_status", "ma_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "ma_id", ma_id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void submitMakeMould(int ma_id, String caller) {
		boolean mbo = baseDao.isDBSetting(caller, "checkWorkCenter");
		if (mbo) {
			checkWorkCenter(ma_id);
		}
		baseDao.execute("update make set ma_total=round(nvl(ma_price,0)*nvl(ma_qty,0),2) where ma_id=" + ma_id);
		baseDao.execute("update make set ma_totalupper=L2U(nvl(ma_total,0)) WHERE ma_id=" + ma_id);
		// 只能批准状态必须是未批准或空
		Object[] status = baseDao.getFieldsDataByCondition("Make", "ma_checkstatuscode,ma_statuscode", "ma_id=" + ma_id);
		Assert.isEquals("common.statuswrong", Status.UNAPPROVED.code(), status[0]);
		// 审核状态必须是已审核
		Assert.isEquals("common.statuswrong", Status.AUDITED.code(), status[1]);
		makeDao.saveDefault("" + ma_id, "");
		checkAll(caller, ma_id);// 工单有效性检测
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { ma_id }); // 执行提交操作
		baseDao.updateByCondition("Make", "ma_checkstatuscode='COMMITED',ma_checkstatus='" + BaseUtil.getLocalMessage("COMMITED")
				+ "',ma_checkdate=" + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + ",ma_checkman='"
				+ SystemSession.getUser().getEm_name() + "'", "ma_id=" + ma_id);
		// 完工状态
		baseDao.updateByCondition("Make",
				"ma_finishstatuscode='UNCOMPLET',ma_finishstatus='" + BaseUtil.getLocalMessage("UNCOMPLET") + "'", "ma_id=" + ma_id
						+ " AND ma_madeqty<ma_qty");
		baseDao.updateByCondition("Make",
				"ma_finishstatuscode='COMPLETED',ma_finishstatus='" + BaseUtil.getLocalMessage("COMPLETED") + "'", "ma_id=" + ma_id
						+ " AND ma_madeqty>=ma_qty");
		// 记录操作
		baseDao.logger.submit(caller, "ma_id", ma_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ma_id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void resSubmitMakeMould(int ma_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Make", "ma_checkstatuscode", "ma_id=" + ma_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, ma_id); // 执行反提交操作
		baseDao.updateByCondition("Make",
				"ma_checkstatuscode='UNAPPROVED',ma_checkstatus='" + BaseUtil.getLocalMessage("UNAPPROVED") + "'", "ma_id=" + ma_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ma_id", ma_id);
		handlerService.afterResSubmit(caller, ma_id);
	}

	@Override
	public void approveMakeMould(int ma_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Make", "ma_checkstatuscode", "ma_id=" + ma_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("pm.make.makebase.approve_onlycommited"));
		}
		// 工单类型设置单价是否允许为0，限制单据单价能否为零
		SqlRowList sl0 = baseDao.queryForRowSet("select mk_price , ma_price from make  left join makekind on ma_kind=mk_name where ma_id="
				+ ma_id + " and nvl(mk_price,0)=0 and nvl(ma_price,0)=0");
		if (sl0.next()) {
			BaseUtil.showError("工单类型设置单价不允许为0，必须填写单价！");
		}
		Object[] obj = baseDao.getFieldsDataByCondition("Make left join makekind on ma_kind=mk_name ", new String[] { "ma_vendcode",
				"ma_tasktype", "nvl(mk_price,0)", "nvl(ma_price,0)" }, "ma_id=" + ma_id);
		if (obj != null) {
			if ("0".equals(obj[2])) {
				if ("0".equals(obj[3])) {
					BaseUtil.showError("工单类型设置单价不允许为0，必须填写单价！");
				}
			}
			if (obj[1].equals("OS") && (obj[0] == null || obj[0].equals(""))) {
				BaseUtil.showError("委外商编号未填写，不能批准！");
			}
		}

		checkAll(caller, ma_id);
		// 执行批准前的其它逻辑
		handlerService.handler(caller, "approve", "before", new Object[] { ma_id });
		// 执行批准操作
		baseDao.updateByCondition("Make",
				"ma_checkstatuscode='APPROVE',ma_sendstatus='待上传',ma_checkstatus='" + BaseUtil.getLocalMessage("APPROVE")
						+ "',ma_checkdate=" + DateUtil.parseDateToOracleString("yyyy-MM-dd HH:mm:ss", new Date()) + ",ma_checkman='"
						+ SystemSession.getUser().getEm_name() + "'", "ma_id=" + ma_id);
		// 记录操作
		baseDao.logger.approve(caller, "ma_id", ma_id);
		// 清除 批准流程
		String flowcaller = processService.getFlowCaller(caller);
		if (flowcaller != null) {
			processService.deletePInstance(ma_id, caller, "approve");
		}
		// 委外加工单：提交时去价格库找是否有限购量的设置，如果有，并且本次数量加上历史的委外总数超过限购数量，则提交后无效核价单的价格
		SqlRowList rs = baseDao
				.queryForRowSet(
						"SELECT ma_id,ma_vendcode,ma_currency,ma_price,ma_prodcode,ma_qty,ma_taxrate,ma_code FROM make WHERE ma_id=? and ma_tasktype='OS'",
						new Object[] { ma_id });
		if (rs.next()) {
			Object[] maxlimit = baseDao.getFieldsDataByCondition("purchasepricedetail left join purchaseprice on ppd_ppid=pp_id",
					new String[] { "ppd_maxlimit", "ppd_id" },
					"ppd_vendcode='" + rs.getObject("ma_vendcode") + "' and ppd_prodcode='" + rs.getObject("ma_prodcode")
							+ "' and ppd_currency='" + rs.getObject("ma_currency") + "' and ppd_price='" + rs.getObject("ma_price")
							+ "' and ppd_rate='" + rs.getObject("ma_taxrate") + "' and ppd_statuscode='VALID' and pp_kind='委外'");
			if (maxlimit != null && maxlimit[0] != null && Double.parseDouble(maxlimit[0].toString()) > 0) {
				double pursumqty = baseDao.getSummaryByField("make", "case when ma_statuscode='FINISH' then ma_madeqty else ma_qty end",
						"ma_vendcode='" + rs.getObject("ma_vendcode") + "' and ma_currency='" + rs.getObject("ma_currency")
								+ "' and ma_prodcode='" + rs.getObject("ma_prodcode") + "' and ma_price='" + rs.getObject("ma_price")
								+ "' and ma_taxrate=" + rs.getObject("ma_taxrate")
								+ " and ma_checkdate>=(select NVL(ppd_fromdate,sysdate) from purchasepricedetail where ppd_id="
								+ maxlimit[1] + ")");
				if (Double.parseDouble(maxlimit[0].toString()) <= pursumqty) {
					baseDao.execute("update purchasepricedetail set ppd_status='无效',ppd_statuscode='UNVALID',ppd_remark=to_char(sysdate,'yyyy-mm-dd HH24:mi:ss')||'超委外限量'||'委外单号"
							+ rs.getObject("ma_code") + "' where ppd_id='" + maxlimit[1] + "'");
				}
			}
		}
		// 执行批准后的其它逻辑
		handlerService.handler(caller, "approve", "after", new Object[] { ma_id });
	}

	@Override
	public void resApproveMakeMould(int ma_id, String caller) {
		// 判断该单据上是否上传到B2B，已上传，则不允许反审核，需要变更的话，走变更单流程
		String sendStatus = baseDao.getFieldValue("make", "ma_sendstatus", "ma_id=" + ma_id, String.class);
		StateAssert.onSendingLimit(sendStatus);
		StateAssert.onSendedLimit(sendStatus);
		Object status = baseDao.getFieldDataByCondition("Make", "ma_checkstatuscode", "ma_id=" + ma_id);
		if (!status.equals("APPROVE")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.statuswrong"));
		}
		Object ob2 = baseDao.getFieldDataByCondition("make", "ma_code", "ma_id=" + ma_id + " and ma_statuscode='FINISH'");
		if (ob2 != null && ob2 != " ") {
			BaseUtil.showError("单据状态为已结案，不允许反批准!");
		}
		String haveGet = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WMSYS.WM_CONCAT(mm_detno) from makematerial where mm_maid=? and (mm_code,mm_detno) in (select pd_ordercode,pd_orderdetno from prodiodetail where pd_piclass in ('生产领料单','委外领料单','生产退料单','委外退料单','生产补料单','委外补料单'))",
						String.class, ma_id);
		if (haveGet != null) {
			BaseUtil.showError("当前工单的明细行" + haveGet + "发生过领退料操作，无法反批准!");
		}
		// 如果已产生生产日报则不允许反批准
		int haveDispatch = baseDao
				.getCount("select count(did_makecode) as haveDispatch from dispatchdetail where did_makecode=(select ma_code from make where ma_id="
						+ ma_id + ")");
		if (haveDispatch > 0) {
			BaseUtil.showError("当前制造单已产生生产日报，无法反批准!");
		}
		// 执行反批准操作
		baseDao.updateByCondition("Make",
				"ma_checkstatuscode='UNAPPROVED',ma_checkstatus='" + BaseUtil.getLocalMessage("UNAPPROVED") + "'", "ma_id=" + ma_id);
		// 记录操作
		baseDao.logger.resApprove(caller, "ma_id", ma_id);
		handlerService.handler(caller, "resApprove", "after", new Object[] { ma_id, SystemSession.getUser().getEm_name() });
	}

	@Override
	public void endMakeMould(int ma_id, String caller) {
		// 只能对状态为[已审核]的订单进行结案操作!
		Object[] status = baseDao.getFieldsDataByCondition("Make", new String[] { "ma_statuscode", "ma_tasktype" }, "ma_id=" + ma_id);
		if (!status[0].equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.statuswrong"));
		}
		// 执行结案前的其它逻辑
		handlerService.handler(caller, "end", "before", new Object[] { ma_id });
		// 执行结案操作
		baseDao.updateByCondition("Make", "ma_statuscode='FINISH', ma_sendstatus='待上传', ma_status='" + BaseUtil.getLocalMessage("FINISH")
				+ "',ma_actenddate=" + DateUtil.parseDateToOracleString(null, new Date()), "ma_id=" + ma_id);
		updateSd_tomakeqty(ma_id);
		// 记录操作
		if (status[1].equals("OS")) {
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.end"), BaseUtil
					.getLocalMessage("msg.endSuccess"), "Make|ma_id=" + ma_id));
		} else if (status[1].equals("MAKE")) {
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.end"), BaseUtil
					.getLocalMessage("msg.endSuccess"), "Make!Base|ma_id=" + ma_id));
		}
		// 执行结案后的其它逻辑
		handlerService.handler(caller, "end", "after", new Object[] { ma_id });
	}

	@Override
	public void resEndMakeMould(int ma_id, String caller) {
		// 只能对状态为[已结案]的订单进行反结案操作!
		Object status = baseDao.getFieldDataByCondition("Make", "ma_statuscode", "ma_id=" + ma_id);
		if (!status.equals("FINISH")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resEnd_onlyEnd"));
		}
		// 执行反结案操作
		baseDao.updateByCondition("Make", "ma_statuscode='ENTERING', ma_status='" + BaseUtil.getLocalMessage("ENTERING")
				+ "',ma_actenddate=null", "ma_id=" + ma_id);
		updateSd_tomakeqty(ma_id);
		// 记录操作
		baseDao.logger.resEnd(caller, "ma_id", ma_id);
	}

	public void setMakeMaterial(String code, String caller) {
		// 执行mrp运算前的其它逻辑
		handlerService.handler("Make", "SetMaterial", "before", new Object[] { code });
		// 执行内置判断逻辑
		SqlRowList sl0 = baseDao
				.queryForRowSet("select count(1) c, WM_CONCAT(mm_detno) detno from makematerial where mm_code='"
						+ code
						+ "' and (mm_code,mm_detno) in (select pd_ordercode,pd_orderdetno from prodiodetail where pd_piclass in ('生产领料单','委外领料单','生产退料单','委外退料单','生产补料单','委外补料单') )");
		if (sl0.next()) {
			if (sl0.getInt("c") > 0) {
				BaseUtil.showError("当前工单的明细行" + sl0.getString("detno") + "发生过领退料操作，不能计算用料!");
			}
		}
		// 执行运算存储过程
		String str = baseDao.callProcedure("MM_MakeMaterialM", new String[] { code, SystemSession.getUser().getEm_name() });
		if (str != null && !str.trim().equals("")) {
			// mrp投放算料失败处理
			Object mdid = baseDao.getFieldDataByCondition("make", "nvl(max(ma_sourceid),0)", "ma_code='" + code + "'");
			if (Integer.parseInt(mdid.toString()) > 0) {
				baseDao.updateByCondition("mrpdata", "md_statuscode='THROWED',md_status='已投放',md_tomacode='" + code
						+ "',md_throwremark='工单：" + code + "算料失败'", "md_id=" + mdid);
			}
			// 提示错误信息
			BaseUtil.showError("工单：" + code + str);

		} else {
			//计算用料完成后，按委外商定义的物料损耗率更新工单需求数
			if(baseDao.isDBSetting(caller, "osProductlossRate")){
				setMM_balance(code);
			}
			SqlRowList sl = baseDao
					.queryForRowSet("select ma_id,case when ma_tasktype='OS' then 'Make' else 'Make!Base' end caller from make where ma_code='"
							+ code + "' ");
			if (sl.next()) {
				// 记录操作
				baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "计算用料", "算料成功", "Make|ma_id="
						+ sl.getString("ma_id")));
				baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "计算用料", "算料成功", "Make!Base|ma_id="
						+ sl.getString("ma_id")));
			}
		}
	}

	public void makeMaterialCheck(String code, String caller) {
		// 执行运算存储过程
		String str = baseDao.callProcedure("MM_MakeMaterialCheck", new String[] { code });
		if (str != null && !str.trim().equals("")) {
			// 提示错误信息
			BaseUtil.showError("工单：" + code + str);
		}
	}

	@Override
	public void saveMakeSubMaterial(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object mm_id = store.get("mm_id");
		Object mp_detno = store.get("mp_detno");
		int detno = 1;
		int repdetno = 0;
		int mm_detno = 0;
		Object ma_id = baseDao.getFieldDataByCondition("makematerial", "mm_maid", "mm_id=" + mm_id);
		mm_detno = Integer.parseInt(baseDao.getFieldDataByCondition("makematerial", "mm_detno", "mm_id=" + mm_id).toString());
		makeDao.setThisQty(Integer.parseInt(store.get("mm_id").toString()), 0, "");
		handlerService.beforeSave(caller, new Object[] { store });
		boolean bool = baseDao.checkByCondition("product", "pr_code='" + store.get("mp_prodcode") + "'");
		if (bool) {
			BaseUtil.showError("料号不存在!");
			return;
		}
		boolean ifnew ;//标识是新增替代料还是更新替代料数据
		Object oldprcode = "",oldcanuseqty="";  //旧替代料号//原替代数
		/*
		 * Object ob1 = baseDao.getFieldDataByCondition("product", "pr_code",
		 * "pr_code='"
		 * +store.get("mp_prodcode")+"' and pr_supplytype ='VIRTUAL'"); if(ob1
		 * != null){
		 * BaseUtil.showError("物料："+store.get("mp_prodcode")+"为虚拟件，不允许保存"); }
		 */
		if (mp_detno != null && !mp_detno.equals("") && Integer.parseInt(mp_detno.toString()) > 0) {
			Object[] obj1 = baseDao.getFieldsDataByCondition(
					"makematerialreplace left join makematerial on mp_mmid=mm_id left join make on mm_maid=ma_id",
					"ma_code,mm_detno,mm_prodcode,mp_prodcode,ma_id,mp_canuseqty", "mp_mmid=" + mm_id + " and mp_detno=" + mp_detno);
			if (obj1!=null && obj1[3] != null) {
				int count = baseDao.getCountByCondition("prodinout,prodiodetail", "pi_id=pd_piid and pd_ordercode='" + obj1[0]
						+ "' and pd_orderdetno=" + obj1[1] + " and pd_prodcode='" + obj1[3] + "'");
				oldprcode = obj1[3];
				oldcanuseqty = obj1[5];
				if (count > 0) {
					if (!store.get("mp_prodcode").equals(obj1[3])) {// 判断料号是否发生了改变
						BaseUtil.showError("该替代料已经存在出入库单据，不能改料号!");
						return;
					}
				}
			}
		}
		bool = baseDao.checkByCondition("makematerial", "mm_id=" + mm_id + " and mm_prodcode='" + store.get("mp_prodcode") + "'");
		if (!bool) {
			BaseUtil.showError("替代料和主料编号相同，不能维护!");
			return;
		}
		if (mp_detno != null && !mp_detno.equals("")) {
			repdetno = Integer.parseInt(mp_detno.toString());
		}
		bool = baseDao.checkByCondition("makematerialreplace", "mp_mmid=" + mm_id + " and mp_prodcode='" + store.get("mp_prodcode")
				+ "' and mp_detno<>" + repdetno);
		if (!bool) {
			BaseUtil.showError("替代料不能重复，不能维护!");
			return;
		}
		//2016-12-16 修改可替代数量取值:替代已领数-替代补料数+替代不良退料+替代已转数 ,nvl(mp_haverepqty,0)-NVL(mp_addqty,0)+NVL(mp_returnmqty,0)+nvl(mp_repqty,0) 
		SqlRowList sl0 = baseDao
				.queryForRowSet("select max(mm_qty-(NVL(mm_havegetqty,0)-NVL(mm_addqty,0)-NVL(mm_haverepqty,0))-NVL(mm_totaluseqty,0)+NVL(mm_repqty,0)) as canrepqty,sum(case when mp_detno='"
						+ repdetno
						+ "' then"
						+ " 0 else nvl(mp_canuseqty,0) end) as allqty,sum((case when mp_detno='"
						+ repdetno
						+ "' then nvl(mp_haverepqty,0)-NVL(mp_addqty,0)+NVL(mp_returnmqty,0)+nvl(mp_repqty,0) "
						+ "else 0 end)) as getqty from makematerial left join makematerialreplace on mp_mmid=mm_id where mm_id='"
						+ mm_id
						+ "' ");
		if (sl0.next()) {
			if (sl0.getFloat("allqty") + Float.parseFloat(store.get("mp_canuseqty").toString()) > sl0.getFloat("canrepqty")) {
				BaseUtil.showError("可替代数量不能大于工单需求数-主料已领数-主料已转数!");
				return;
			}
		}
		if (mp_detno != null && !mp_detno.equals("") && Integer.parseInt(mp_detno.toString()) != 0) {
			if (Float.parseFloat(store.get("mp_canuseqty").toString()) < sl0.getFloat("getqty")) {
				BaseUtil.showError("可替代数量不能小于此替代料已领料数+已转领料数!");
				return;
			}
			// 说明是更新
			detno = Integer.parseInt(mp_detno.toString());
			baseDao.updateByCondition(
					"MakeMaterialReplace",
					"mp_prodcode='" + store.get("mp_prodcode") + "',mp_canuseqty='" + store.get("mp_canuseqty") + "',mp_warehouseid='"
							+ store.get("mp_warehouseid") + "',mp_whcode='" + store.get("mp_whcode") + "',mp_remark='"
							+ store.get("mp_remark") + "'", "mp_mmid=" + mm_id + " AND mp_detno=" + mp_detno);
			// 更新主料的替代料物料串
			makeDao.setMaterialRepcode("", mm_id.toString());
			ifnew = false;
		} else {
			boolean bool2 = baseDao.checkByCondition("MakeMaterialReplace", "mp_prodcode='" + store.get("mp_prodcode") + "' AND mp_mmid="
					+ mm_id);
			if (!bool2) {
				BaseUtil.showError("替代料已存在!");
				return;
			}
			SqlRowList sl = baseDao.queryForRowSet("select max(mp_detno) from MakeMaterialReplace where mp_mmid=?", mm_id);
			if (sl.next()) {
				if (sl.getInt(1) != -1) {
					detno = sl.getInt(1) + 1;
				}
			}
			String insertSql = "insert into MakeMaterialReplace (mp_mmid,mp_detno,mp_prodcode,mp_canuseqty,mp_warehouseid,mp_whcode,mp_remark) values('"
					+ mm_id
					+ "','"
					+ detno
					+ "','"
					+ store.get("mp_prodcode")
					+ "','"
					+ store.get("mp_canuseqty")
					+ "','"
					+ store.get("mp_warehouseid") + "','" + store.get("mp_whcode") + "','" + store.get("mp_remark") + "')";
			baseDao.execute(insertSql);
			// 更新主料的替代料物料串
			makeDao.setMaterialRepcode("", mm_id.toString());
			ifnew = true;
		}
		// 自动更新语句
		List<String> updateSqls = new ArrayList<String>();
		updateSqls
				.add("update  MakeMaterial set mm_canuserepqty=(select sum(nvl(mp_canuseqty,0)) from MakeMaterialReplace where mp_mmid=mm_id) where  mm_id="
						+ mm_id);
		updateSqls.add("update MakeMaterial set mm_ifrep=1 where  mm_id=" + mm_id);
		updateSqls
				.add("update  MakeMaterialreplace set mp_rate=1,(mp_mmdetno,mp_mmcode,mp_maid)=(select mm_detno,mm_code,mm_maid from makematerial where mm_id='"
						+ mm_id + "') where  mp_mmid=" + mm_id);
		updateSqls
				.add("update makematerialreplace set mp_repqty=(select sum(pd_outqty-pd_inqty) from prodiodetail,prodinout where pd_piid=pi_id  and pi_statuscode<>'DELETE' and pd_ordercode=mp_mmcode and pd_orderdetno=mp_mmdetno and pd_piclass in (select ds_name  FROM   documentsetup"
						+ " WHERE  ds_name<>'' and nvl(ds_ismakemminout,0)<>0)) where mp_mmid=" + mm_id + " and mp_detno=" + detno);
		baseDao.execute(updateSqls);
		
		if(ifnew){
			// 新增替代料的详细操作记录在工单日志中；
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "第" + mm_detno + "行,新增替代料" + store.get("mp_prodcode"),
					"成功", "Make|ma_id=" + ma_id));
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "第" + mm_detno + "行,新增替代料" + store.get("mp_prodcode"),
					"成功", "Make!Base|ma_id=" + ma_id));
		}else{
			//修改替代料的详细操作记录在工单日志中；
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "用料表第" + mm_detno + "行,修改替代料第"+mp_detno+"行成功",
					"原料号:" +oldprcode+",可替代数："+oldcanuseqty+",新料号:"+store.get("mp_prodcode")+",新可替代数:"+store.get("mp_canuseqty"), "Make|ma_id=" + ma_id));
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "用料表第" + mm_detno + "行,修改替代料第"+mp_detno+"行成功",
					"原料号:" +oldprcode+",可替代数："+oldcanuseqty+",新料号:"+store.get("mp_prodcode")+",新可替代数:"+store.get("mp_canuseqty"), "Make!Base|ma_id=" + ma_id));
		}
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteMakeSubMaterial(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object mp_mmid = store.get("mm_id");
		Object mp_detno = store.get("mp_detno");
		handlerService.beforeDel(caller, new Object[] { mp_mmid });
		makeDao.setThisQty(Integer.parseInt(mp_mmid.toString()), null, null); // 更新已转领料数
		SqlRowList sl = baseDao
				.queryForRowSet("select * from makematerialreplace where NVL(mp_repqty,0)+NVL(mp_haverepqty,0)>0 and mp_mmid='" + mp_mmid
						+ "' AND  mp_detno='" + mp_detno + "'");
		if (sl.next()) {
			BaseUtil.showError("领料数+已转领料数大于0，不能删除!");
			return;
		}
		Object[] obj1 = baseDao.getFieldsDataByCondition(
				"makematerialreplace left join makematerial on mp_mmid=mm_id left join make on mm_maid=ma_id",
				"ma_id,mm_detno,mm_prodcode,mp_prodcode", "mp_mmid=" + Integer.valueOf(mp_mmid.toString()) + " and mp_detno=" + mp_detno);
		baseDao.execute("delete  makematerialreplace where mp_mmid='" + mp_mmid + "' and mp_detno='" + mp_detno + "'");
		baseDao.execute("update  MakeMaterial set mm_ifrep=0 where mm_id not in (select mp_mmid from MakeMaterialReplace where mp_mmid=mm_id   ) and mm_id='"
				+ mp_mmid + "'");
		baseDao.execute("update MakeMaterial set mm_canuserepqty=(select NVL(sum(mp_canuseqty),0) from MakeMaterialReplace where mp_mmid=mm_id and mp_canuseqty>0) where  mm_id="
				+ mp_mmid);
		makeDao.setMaterialRepcode("", mp_mmid.toString());
		// 删除替代料的详细操作记录在工单日志中；
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "第" + obj1[1].toString() + "行,删除替代料"
				+ store.get("mp_prodcode"), "成功", "Make|ma_id=" + obj1[0]));
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "第" + obj1[1].toString() + "行,删除替代料"
				+ store.get("mp_prodcode"), "成功", "Make!Base|ma_id=" + obj1[0]));
		handlerService.afterDel(caller, new Object[] { mp_mmid });
	}

	@Override
	public Object saveModifyMaterial(String formStore, String caller) {
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(formStore);
		Object id = map.get("mm_id");
		String Oldprcode = "";
		Double Oldmmqty = null;
		Double Oldoneuseqty = null;
		Double oldmmgqty = null;
		String logstr = "",mcaller = "Make!Base";
		if (id == null || String.valueOf(id).equals("")) {
			id = 0;
		}
		handlerService.beforeSave(caller, new Object[] { map });
		Object maid = map.get("mm_maid");
		// 限制制单的状态为“已结案,已冻结”，不能修改用料
		Object[] obs = baseDao.getFieldsDataByCondition("make", new String[]{"ma_statuscode","ma_tasktype"}, "ma_id='" + maid.toString() + "'");
		if(obs!=null && !"".equals(obs)){
			if ("FINISH".equals(obs[0]) || "FREEZE".equals(obs[0])) {
				BaseUtil.showError("工单状态为已结案或者已冻结，不允许修改用料");
			}
			if("OS".equals(obs[1])){ //委外
				mcaller = "Make";
			}
		}else{
			BaseUtil.showError("工单不存在或已删除");
		}
		//启用参数完工后允许继续发料及更改用料，则可以增加用料，制造和委外分别采用两个参数
		int havefinish = baseDao.getCount("select count(1) from make where ma_id=" + maid + " and ma_madeqty>0 and ma_madeqty>=ma_qty");
		if (havefinish > 0 && Integer.valueOf(String.valueOf(id)) == 0 && !baseDao.isDBSetting(mcaller, "allowChangeAfterCom")) {
			BaseUtil.showError("已完工的工单不能添加用料");
		}
		/*
		 * Object ob1 = baseDao.getFieldDataByCondition("product", "pr_code",
		 * "pr_code='"+map.get("mm_prodcode")+"' and pr_supplytype ='VIRTUAL'");
		 * if(ob1 != null){
		 * BaseUtil.showError("物料："+map.get("mm_prodcode")+"为虚拟件，不允许保存"); }
		 */
		/*
		 * sl = baseDao.queryForRowSet(
		 * "select mp_mmid ,mp_mmdetno from MakeMaterialreplace where mp_maid='"
		 * + maid + "' and mp_prodcode='" +
		 * map.get("mm_prodcode").toString() + "'"); if (sl.next()) {
		 * BaseUtil.showError("此物料是序号[" + sl.getObject("mp_mmdetno") +
		 * "]的替代料，不能维护"); }
		 */
		int detno = 1;
		Object ma_code=null;
		if (Integer.valueOf(String.valueOf(id)) != 0) {
			// 刷新工单数量 限制数量不能小于 已领数+已转数
			refreshqty(Integer.parseInt(maid.toString()), caller, Integer.valueOf(String.valueOf(id)));
			// 判断可拆数不能小于拆件入库数
			if (map.get("mm_gqty") != null && !("").equals(map.get("mm_gqty"))) {
				Object obj = baseDao.getFieldDataByCondition("MakeMaterial", "mm_yqty", "mm_id=" + id + " AND mm_yqty>" + map.get("mm_gqty")
						+ " and mm_gqty>0");
				if (obj != null) {
					BaseUtil.showError("可拆件数量" + map.get("mm_gqty") + "不能小于 拆件入库数(" + obj.toString() + ")");
				}
			}
			//判断主料和替代料不能相同
			boolean bool = baseDao.checkByCondition("makematerial", "mm_id=" + id + " and (instr(mm_repprodcode,'" + map.get("mm_prodcode") + ",')>0 or instr(mm_repprodcode,'," + map.get("mm_prodcode") + "')>0 or case when instr(mm_repprodcode,',')>0 then 0 else instr(mm_repprodcode,'"+map.get("mm_prodcode")+"') end >0)");
			if (!bool) {
				BaseUtil.showError("主料和替代料编号相同，不能维护!");
			}
			
			//修改 20161216 已领数更新为：已领数+退料数-补料数
			Object[] objs = baseDao.getFieldsDataByCondition(
					"MakeMaterial",
					new String[] { "nvl(mm_havegetqty,0)+nvl(mm_returnmqty,0)-nvl(mm_addqty,0)", "nvl(mm_totaluseqty,0)" },
					"mm_id=" + id + " AND nvl(mm_totaluseqty,0)+nvl(mm_havegetqty,0)>0 AND (nvl(mm_havegetqty,0)+nvl(mm_returnmqty,0)-nvl(mm_addqty,0)+nvl(mm_totaluseqty,0))>"
							+ map.get("mm_qty"));
			if (objs != null) {
				BaseUtil.showError("修改的数量" + map.get("mm_qty") + "不能小于 已领数(" + objs[0].toString() + ")+已转数(" + objs[1].toString() + ")");
			}
			
			detno = Integer.parseInt(map.get("mm_detno").toString());
			SqlRowList sl = baseDao.queryForRowSet("select mm_prodcode from MakeMaterial where mm_id=" + id
					+ " AND nvl(mm_totaluseqty,0)+nvl(mm_havegetqty,0)>0");
			if (sl.next()) {
				if (!sl.getObject("mm_prodcode").toString().equals(map.get("mm_prodcode").toString())) {
					BaseUtil.showError("已经转领料或已经领料的用料不能变更料号");
				}
			}
			SqlRowList rsm = baseDao.queryForRowSet("select NVL(max(mm_topbomid),0)mm_topbomid,nvl(max(mm_stepcode),'')mm_stepcode from MakeMaterial where mm_id=" + id);
			if(rsm.next()){
				sl = baseDao.queryForRowSet("select mm_detno,NVL(mm_topbomid,0) mm_topbomid from MakeMaterial where mm_maid='" + maid
						+ "' and mm_prodcode='" + map.get("mm_prodcode").toString() + "' and mm_id<>" + id + " and nvl(mm_stepcode,' ')=nvl(nvl("+map.get("mm_stepcode")+",'"+rsm.getGeneralString("mm_stepcode")+"'),' ')");
				if (sl.next()) {
					if (rsm.getInt("mm_topbomid") == 0) {
						BaseUtil.showError("此物料已出现在序号[" + sl.getObject("mm_detno") + "]，不能维护");
					} else if (rsm.getInt("mm_topbomid")> 0 && sl.getInt("mm_topbomid") == rsm.getInt("mm_topbomid")) {
						BaseUtil.showError("此物料已出现在序号[" + sl.getObject("mm_detno") + "]，不能维护");
					}
				}
			}
			sl = baseDao
					.queryForRowSet("select NVL(mm_oneuseqty,0) mm_oneuseqty,NVL(mm_qty,0) mm_qty,mm_prodcode,mm_updatetype,mm_code,mm_gqty from MakeMaterial where mm_id="
							+ id);
			if (sl.next()) {
				Oldprcode = sl.getString("mm_prodcode");
				Oldoneuseqty = sl.getDouble("mm_oneuseqty");
				Oldmmqty = sl.getDouble("mm_qty");
				oldmmgqty = sl.getDouble("mm_gqty");
				if(map.get("mm_updatetype") != null){//20161228 前台是否允许工单外退料，禁用物料才允许修改成允许工单外退料
					if("D".equals(sl.getString("mm_updatetype")) && "R".equals(map.get("mm_updatetype"))){
						map.put("mm_updatedate",DateUtil.currentDateString(null));
					}else{
						map.remove("mm_updatetype");
						map.put("mm_updatedate",DateUtil.currentDateString(null));
					}
				} 
				ma_code=sl.getString("mm_code");
			}
			baseDao.execute(SqlUtil.getUpdateSqlByFormStore(map, "MakeMaterial", "mm_id"));
			//baseDao.execute("update makematerial set mm_updatetype='UPDATE',mm_updatedate=sysdate where mm_id=" + String.valueOf(id));
		} else {
			// 新增
			SqlRowList sl = baseDao.queryForRowSet("select mm_detno from MakeMaterial where mm_maid=" + maid + " AND mm_prodcode='"
					+ map.get("mm_prodcode").toString() + "' and nvl(mm_stepcode,' ')=nvl("+map.get("mm_stepcode")+",' ')");
			if (sl.next()) {
				BaseUtil.showError("此物料已经存在，序号：" + sl.getString("mm_detno") + "，不能重复增加");
			}
			ma_code = baseDao.getFieldDataByCondition("Make", "ma_code", "ma_id=" + maid);
			id = baseDao.getSeqId("MAKEMATERIAL_SEQ");
			sl = baseDao.queryForRowSet("select max(mm_detno) from makematerial where mm_maid=?", maid);
			if (sl.next()) {
				detno = sl.getInt(1) + 1;
			}
			map.remove("mm_id");
			map.put("mm_id", id);
			map.put("mm_code", ma_code);
			map.put("mm_detno", detno);
			map.put("mm_havegetqty", 0);
			map.put("mm_updatedate", DateUtil.currentDateString(null));
			if(map.get("mm_updatetype") != null)
			    map.remove("mm_updatetype");
			baseDao.execute(SqlUtil.getInsertSqlByMap(map, "MakeMaterial"));
			//baseDao.execute("update makematerial set mm_updatetype='ADD',mm_updatedate=sysdate where mm_id=" + String.valueOf(id));
		}
		if(baseDao.isDBSetting("usingMakeCraft")){//启用工厂模式  重新计算对应作业单 数量
			if(ma_code!=null && !"".equals(ma_code)){
				//更新领料最小套数
				baseDao.execute("UPDATE  makecraft set mc_setqty=nvl((select min( floor(0.1+((nvl(mm_havegetqty,0)+nvl(mm_stepinqty,0)) / mm_oneuseqty)))"
						+ "	from makematerial where mm_code=mc_makecode  and mm_oneuseqty>0  and mm_mdcode=mc_code  and nvl(mm_supplytype,'')='PUSH'  ) ,0) "
						+ "where mc_makecode='"+ma_code+"'");
				baseDao.execute("update makecraft set  mc_whinqty= nvl((select min(FLOOR(0.01+nvl(mm_havegetqty,0)/mm_oneuseqty)) from makematerial  "
						+ "where mm_code='"+ma_code+"' and mm_oneuseqty>0 and mm_mdcode=mc_code and mm_supplytype='PUSH' ),0)"
						+ " where mc_makecode='"+ma_code+"'");
				//更新入站数
	            baseDao.execute("UPDATE  makecraft set mc_inqty=nvl(mc_setqty,0)+nvl(mc_jumpinqty,0) where mc_makecode='"+ma_code+"'");
	            baseDao.execute("UPDATE  MakeCraft SET mc_onmake = nvl(mc_inqty, 0) - nvl(mc_outqty, 0)  where mc_makecode='"+ma_code+"'");    
	            baseDao.execute("UPDATE MAKECRAFT SET MC_STATUS='已完工',MC_STATUSCODE='COMPLETED' WHERE NVL(MC_QTY,0)-NVL(MC_OUTQTY,0)+NVL(MC_SCRAPQTY,0)+NVL(MC_NGOUTQTY,0)<=0 AND MC_STATUS IN ('已审核','已完工') AND MC_MAKECODE='"+ma_code+"'");  
	            baseDao.execute("UPDATE MAKECRAFT SET MC_STATUS='已审核',MC_STATUSCODE='AUDITED' WHERE NVL(MC_QTY,0)-NVL(MC_OUTQTY,0)+NVL(MC_SCRAPQTY,0)+NVL(MC_NGOUTQTY,0)>0 AND MC_STATUS IN ('已审核','已完工') AND mc_makecode='"+ma_code+"'");  
			}
		}
		makeDao.setBalance("", id.toString());
		makeDao.saveDefault("", id.toString());
		handlerService.afterSave(caller, map, "");
		logstr = "序号:" + detno + " ";
		if (Integer.valueOf(String.valueOf(id)) != 0) {
			logstr = "原料号:" + Oldprcode + "用量:" + Oldoneuseqty + "需求:" + Oldmmqty+"可拆数:"+oldmmgqty;
		}
		logstr += " 新料号:" + map.get("mm_prodcode") + "新用量:" + map.get("mm_oneuseqty") + "新需求:" + map.get("mm_qty")+"新可拆数:"+map.get("mm_gqty");
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "修改用料" + logstr, "第" + detno + "行,修改成功", "Make|ma_id="
				+ maid));
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "修改用料" + logstr, "第" + detno + "行,修改成功",
				"Make!Base|ma_id=" + maid));
		makeDao.updateMakeGetStatus(maid.toString());
		return id;
	}

	@Override
	public void deleteModifyMaterial(int id, String caller) {
		// 限制制单的状态为“已结案,已冻结”，不能修改用料
		Object obs = null;
		obs = baseDao.getFieldDataByCondition("make left join makematerial on ma_id=mm_maid", "ma_id", "mm_id=" + id);
		if (obs != null && obs != " ") {
			obs = baseDao.getFieldDataByCondition("make", "ma_statuscode", "ma_id='" + Integer.valueOf(obs.toString()) + "'");
			if (obs.toString().equals("FINISH") || obs.toString().equals("FREEZE")) {
				BaseUtil.showError("工单状态为已结案或者已冻结，不允许删除");
			}
		}
		String haveGet = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WMSYS.WM_CONCAT(mm_detno) from makematerial where mm_id=? and (mm_code,mm_detno) in (select pd_ordercode,pd_orderdetno from prodiodetail where pd_piclass in ('生产领料单','委外领料单','生产退料单','委外退料单','生产补料单','委外补料单','拆件入库单'))",
						String.class, id);
		if (haveGet != null) {
			BaseUtil.showError("当前明细行发生过领退料操作或者拆件入库操作，不能删除!");
		}
		String Oldprcode = "", logstr = "";
		Double Oldmmqty = null;
		Double Oldoneuseqty = null;
		int mmdetno = 0;
		Object maid = baseDao.getFieldDataByCondition("MakeMaterial", "mm_maid", "mm_id=" + id);
		SqlRowList sl = baseDao
				.queryForRowSet("select mm_detno, NVL(mm_oneuseqty,0) mm_oneuseqty,NVL(mm_qty,0) mm_qty,mm_prodcode from MakeMaterial where mm_id="
						+ id + " ");
		if (sl.next()) {
			Oldprcode = sl.getString("mm_prodcode");
			Oldoneuseqty = sl.getDouble("mm_oneuseqty");
			Oldmmqty = sl.getDouble("mm_qty");
			mmdetno = sl.getInt("mm_detno");
			logstr = " 料号:" + Oldprcode + "用量:" + Oldoneuseqty.toString() + "需求:" + Oldmmqty.toString();
		}
		baseDao.deleteById("MakeMaterial", "mm_id", id);
		//更新领料状态
		makeDao.updateMakeGetStatus(maid.toString());
		// 删除关联的替代料
		baseDao.execute("delete from makematerialreplace where mp_mmid=" + id);
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "删除用料" + logstr, "第" + mmdetno + "行,删除成功", "Make|ma_id="
				+ maid));
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "删除用料" + logstr, "第" + mmdetno + "行,删除成功",
				"Make!Base|ma_id=" + maid));
	}

	@Override
	public String[] printMakeMould(int ma_id, String caller, String reportName, String condition) {
		// 已审核才允许打印
		if (baseDao.isDBSetting(caller, "printNeedAudit")) {
			String status = baseDao.getFieldValue("Make", "ma_statuscode", "ma_id=" + ma_id, String.class);
			StateAssert.printOnlyAudited(status);
		}
		// 已批准才允许打印
		if (baseDao.isDBSetting(caller, "printNeedApprove")) {
			String status = baseDao.getFieldValue("Make", "ma_checkstatuscode", "ma_id=" + ma_id, String.class);
			Assert.isEquals("common.print_onlyApprove", Status.APPROVE.code(), status);
		}
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, new Object[] { ma_id });
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 修改打印状态
		baseDao.updateByCondition("Make", "ma_printstatuscode='PRINTED',ma_printstatus='" + BaseUtil.getLocalMessage("PRINTED") + "'",
				"ma_id=" + ma_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.print"), BaseUtil
				.getLocalMessage("msg.printSuccess"), caller + "|ma_id=" + ma_id));
		// 记录打印次数
		baseDao.updateByCondition("Make", "ma_count=nvl(ma_count,0)+1", "ma_id=" + ma_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, new Object[] { ma_id });
		return keys;
	}

	/**
	 * 成套发料时，计算本次应领数
	 */
	@Override
	public void calThisQty(String ids, String caller) {
		// 成套发料提示
		String[] arr = ids.split(",");
		for (String str : arr) {
			if (str.equals("0")) {
				BaseUtil.showError("所选工单存在未批准或未审核的,不能发料!");
			}
		}
		boolean bool = baseDao.checkIf("make", "(nvl(ma_checkstatuscode,' ')<>'APPROVE' or ma_statuscode<>'AUDITED') and ma_id in (" + ids
				+ ")");
		if (bool) {
			BaseUtil.showError("所选工单存在未批准或未审核的,不能发料!");
		}
		
		bool = baseDao.isDBSetting(caller, "autoUpdateRepQty");  //根据库存按先进先出原则自动更新替代料的可替代数
		if(bool){
			SqlRowList rs = baseDao.queryForRowSet(" select * from ( select  mm_code,mm_detno,mm_id,mm_prodcode,mm_whcode,iifrep,mm_qty,ba_date,remain,"
					             + " (select sum(pd_outqty) from prodiodetail where pd_status=0 and pd_prodcode=mm_prodcode and pd_whcode=mm_whcode) unpostqty,"
					             + " rank() over(partition by mm_code,mm_id order by case when remain>mm_qty then ba_date else sysdate+300 end,remain) rm "
					             + " from ( "
					             + " select mm_code,mm_id,mm_prodcode,mm_whcode,iifrep,min(ba_date)ba_date,sum(ba_remain)remain,max(mm_qty)mm_qty,max(mm_detno)mm_detno from( "
					             + " select mm_code,mm_detno,mm_id,0 iifrep,mm_prodcode,mm_whcode,mm_qty  "
					             + " from makematerial where mm_maid in ("+ids+") and mm_ifrep<>0 and mm_canuseqty=0 "
					             + " union(select mp_mmcode mm_code,mp_mmdetno mm_detno,mp_mmid mm_id,-1 ifrep,mp_prodcode mm_prodcode,mp_whcode mm_whcode, mm_qty "
					             + " from makematerial left join makematerialreplace on mp_maid=mm_maid and mp_mmdetno=mm_detno where mp_maid in ("+ids+")) "
					             + " ) left join batch on ba_prodcode=mm_prodcode and ba_whcode=mm_whcode and ba_remain>0 "
					             + " group by  mm_code,mm_id,mm_prodcode,mm_whcode,iifrep)A) where RM=1 "
					             + " and remain-NVL(unpostqty,0)>mm_qty and iifrep<>0 and mm_detno>0 "
					             + " and not exists(select 1 from prodiodetail where pd_ordercode=mm_code and pd_orderdetno=mm_detno) ");
			while(rs.next()){
				int mm_id = rs.getGeneralInt("mm_id");
				baseDao.execute("update makematerialreplace set mp_canuseqty=? where mp_mmid=? and mp_prodcode=?",rs.getObject("mm_qty"),mm_id,rs.getGeneralString("mm_prodcode"));
				baseDao.execute("update makematerial set mm_canuseqty=? where mm_id=?",rs.getObject("mm_qty"),mm_id);
			}
		}
		makeDao.setThisQty(null, null, ids);
		//增加存储过程计算可用库存
		String str = baseDao.callProcedure("MM_SETASSIGNQTY", new String[] {ids});
		if (str != null && !str.trim().equals("")) {
			// 提示错误信息
			BaseUtil.showError("计算可用库存失败");
		}
	}

	static final String MAKE_CLOSE = "SELECT ma_code,mm_detno,mm_prodcode FROM makematerial inner join make on mm_maid=ma_id WHERE ma_id=? and ma_qty>0 "
			+ " and(nvl(mm_havegetqty,0)-nvl(mm_scrapqty,0)-mm_oneuseqty*nvl(ma_madeqty,0)>=1 OR mm_gqty>NVL(mm_yqty,0))";
	static final String MAKE_CLOSE_OSBalance = "SELECT ma_code,mm_detno,mm_prodcode FROM makematerial inner join make on mm_maid=ma_id WHERE ma_id=? and ma_qty>0 "
			+ " and(nvl(mm_havegetqty,0)-nvl(mm_scrapqty,0)-NVL(mm_balance,0)-mm_oneuseqty*nvl(ma_madeqty,0)>=1 OR mm_gqty>NVL(mm_yqty,0))";
	static final String MAKE_CLOSE_SPLIT = "SELECT ma_code,mm_detno,mm_prodcode FROM makematerial inner join make on mm_maid=ma_id  WHERE ma_id=? and ma_qty>0 "
			+ " and mm_gqty>0 and mm_gqty>NVL(mm_yqty,0) ";
	static final String MAKE_CLOSE_PullBalance = "SELECT ma_code,mm_detno,mm_prodcode FROM makematerial inner join make on mm_maid=ma_id left join product on mm_prodcode=pr_code WHERE ma_id=? and ma_qty>0 "
			+ " and(nvl(mm_havegetqty,0)-nvl(mm_scrapqty,0)-(case when pr_supplytype='PULL' then NVL(mm_balance,0) else 0 end)-mm_oneuseqty*nvl(ma_madeqty,0)>=1 OR mm_gqty>NVL(mm_yqty,0))";

	/**
	 * 制造单、委外单批量结案
	 */
	@Override
	public void vastCloseMake(String data, String caller) {
		List<Map<Object, Object>> list = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer s = new StringBuffer();
		SqlRowList rs = null;
		StringBuffer sb = null;
		Object id = null;
		Object code = null;
		Object ma_bzremark = null;
		Boolean OSbalanceNotReturn = baseDao.isDBSetting("Make", "OSbalanceNotReturn");// 根据Setting表配置是否不需要退委外的备损数
		String Sqlstr = "";
		Boolean PullbalanceNotReturn = baseDao.isDBSetting("Make!Base", "PullbalanceNotReturn");// 根据Setting表配置是否不需要退拉式物料的备损数
		Boolean balanceNotReturn = baseDao.isDBSetting("Make!Base", "balanceNotReturn");// 根据Setting表配置是否不需要退备损数
		for (Map<Object, Object> m : list) {
			id = m.get("ma_id");
			if (m.containsKey("ma_bzremark") && !StringUtil.hasText(m.get("ma_bzremark"))) {
				BaseUtil.showError("结案原因没有填写,结案失败!");
			}
			ma_bzremark = m.get("ma_bzremark");
			// 判断可拆数是否达成
			Sqlstr = MAKE_CLOSE_SPLIT;
			SqlRowList rs0 = baseDao.queryForRowSet(Sqlstr, id);
			if (rs0.hasNext()) {
				s.append("<a href=\"javascript:openUrl('jsps/pm/make/makeBase.jsp?formCondition=ma_idIS" + id + "&gridCondition=mm_maidIS"
						+ id + "&whoami=" + caller + "')\">" + rs0.getString("ma_code") + "</a>&nbsp;");
				s.append("<br>");
				while (rs0.next()) {
					s.append("序号：" + rs0.getString("mm_detno"));
					s.append("<br>");
				}
				s.append("有剩余未拆件入库的物料!");
				s.append("<hr>");
				continue;
			}
			// 以下判断结存数是否为0
			Object[] objm = baseDao.getFieldsDataByCondition("make left join makekind on (ma_kind=mk_name or ma_kind=mk_code)",
					"ma_tasktype,mk_type", "ma_id=" + id);
			if (objm != null) {
				if (objm[1] != null && objm[1].equals("D")) {// 拆件工单
					Sqlstr = "";// 拆件工单不判断结存
				} else if ("OS".equals(objm[0]) && OSbalanceNotReturn) {// 不需要退备损的委外单
					Sqlstr = MAKE_CLOSE_OSBalance;
				} else if ("MAKE".equals(objm[0]) && balanceNotReturn) {// 不需要退备损的标准工单
					Sqlstr = MAKE_CLOSE_OSBalance;
				} else if ("MAKE".equals(objm[0]) && PullbalanceNotReturn) {// 不需要退拉式损耗的标准工单
					Sqlstr = MAKE_CLOSE_PullBalance;
				} else {
					Sqlstr = MAKE_CLOSE;// 需要退备损的标准工单或委外单
				}
			}
			caller = "OS".equals(objm[0]) ? "Make" : "Make!Base";
			if (caller.equals("Make!Base")) {
				code = baseDao.getFieldDataByCondition("Make", "ma_code", "ma_id=" + id);
				// 有无未过账或未审核的关联单据：完工入库单、生产领料单、生产补料单、生产退料单、拆件完工入库单
				rs = baseDao
						.queryForRowSet("select count(0) cn, wm_concat( '制造单号：'||pd_ordercode||'关联单号：'||pi_inoutno||'单据类型：'||pi_class) data　from prodinout left join prodiodetail on pi_id=pd_piid where pd_ordercode='"
								+ code.toString()
								+ "'"
								+ "   and pi_statuscode<>'POSTED'"
								+ " and pi_class in('完工入库单','生产领料单','生产补料单','生产退料单','拆件入库单') and rownum<30");
				if (rs.next()) {
					if (rs.getInt("cn") > 0) {
						BaseUtil.showError("存在还未过账或者未审核的关联单据!" + rs.getString("data"));
					}
				}
				// 有无未审核的生产报废单
				rs = baseDao
						.queryForRowSet("select count(0) cn ,wm_concat('制造单号：'|| md_mmcode||'报废单号：'||ms_code) data from MakeScrapdetail left join MakeScrap on md_msid=ms_id  where md_mmcode='"
								+ code + "' and ms_statuscode<>'AUDITED'");
				if (rs.next()) {
					if (rs.getInt("cn") > 0) {
						BaseUtil.showError("制造单存在未审核的生产报废单!" + rs.getString("data"));
					}
				}
			}
			if (!Sqlstr.equals("")) {
				rs = baseDao.queryForRowSet(Sqlstr, id);
			}
			if (Sqlstr != null && rs != null && rs.hasNext()) {
				sb = new StringBuffer();
				while (rs.next()) {
					if (sb.length() == 0) {
						sb.append("工单号:");
						sb.append("<a href=\"javascript:openUrl('jsps/pm/make/makeBase.jsp?formCondition=ma_idIS" + id
								+ "&gridCondition=mm_maidIS" + id + "&whoami=" + caller + "')\">" + rs.getString("ma_code") + "</a>&nbsp;");
						sb.append("<br>");
					}
					sb.append("序号:");
					sb.append(rs.getInt("mm_detno"));
					sb.append("&nbsp;&nbsp;");
					sb.append("料号:");
					sb.append(rs.getString("mm_prodcode"));
					sb.append("<br>");
				}
				if (sb.length() > 0) {
					if (objm[1] != null && objm[1].equals("D")) {
						sb.append("有物料拆件未入库!");
					} else {
						sb.append("有结余物料未退仓!");
					}
					s.append(sb);
					s.append("<hr>");
					continue;
				}
			} else {
				// 修改结案状态
				baseDao.updateByCondition("Make",
						"ma_statuscode='FINISH', ma_endstatus='待上传', ma_status='" + BaseUtil.getLocalMessage("FINISH") + "',ma_actenddate="
								+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()), "ma_id=" + id);
				updateSd_tomakeqty(Integer.parseInt(id.toString()));
				// ma_bzremark 结案原因
				if (StringUtil.hasText(m.get("ma_bzremark"))) {
					baseDao.updateByCondition("Make", "ma_bzremark='" + ma_bzremark + "'", "ma_id=" + id);
				}
				if(baseDao.isDBSetting("usingMakeCraft")){
					SqlRowList rd = baseDao.queryForRowSet("select mm_mdcode from MakeMaterial where mm_maid="+id+" group by mm_mdcode");
					if(rd.next()){
						baseDao.execute("update MakeCraft set mc_status='" + BaseUtil.getLocalMessage("FINISH") + "',mc_statuscode='FINISH' where mc_code='"+rd.getObject("mm_mdcode")+"'");
					}
				}
				// 记录操作
				baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.end"), BaseUtil
						.getLocalMessage("msg.endSuccess"), "Make|ma_id=" + id));
				baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.end"), BaseUtil
						.getLocalMessage("msg.endSuccess"), "Make!Base|ma_id=" + id));
			}
		}
		if (s.length() > 0) {
			BaseUtil.showError("AFTERSUCCESS" + s.toString());
		}
	}

	/**
	 * 制造单、委外单批量强制结案
	 */
	@Override
	public void vastEnforceEndMake(String data, String caller) {
		List<Map<Object, Object>> list = BaseUtil.parseGridStoreToMaps(data);
		int id = 0;
		StringBuffer str = new StringBuffer();
		for (Map<Object, Object> m : list) {
			id = Integer.parseInt(m.get("ma_id").toString());
			try{
			   enforceEndMake(id, "Make!Base", null);
			}catch(Exception e){
				if(e.getMessage().contains("工单")){
					str.append(e.getMessage().split(",")[0]).append(";</br>");
				}else {
					BaseUtil.showError(e.getMessage());
				}
			}
		}
		if(str.length()>0){
			str.append("存在未过账的出入库单据，请点击界面上制造单号链接，查看出入库明细");
			BaseUtil.showError(str.toString());
		}
	}

	@Override
	public void vastResStart(String data, String caller) {
		List<Map<Object, Object>> list = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> m : list) {
			String tasktype = baseDao.getFieldDataByCondition("make", "ma_tasktype", "ma_id=" + m.get("ma_id")).toString();
			if (tasktype != null) {
				caller = "OS".equals(tasktype) ? "Make" : "Make!Base";
			}
			baseDao.updateByCondition("Make", "ma_statuscode='AUDITED',ma_status='" + BaseUtil.getLocalMessage("AUDITED") + "',ma_bzremark=''", "ma_id="
					+ m.get("ma_id"));
			// 有领料的工单，重启后，状态为已审核
			/*
			 * baseDao.updateByCondition("Make",
			 * "ma_statuscode='AUDITED',ma_status='" +
			 * BaseUtil.getLocalMessage("AUDITED") + "'",
			 * 
			 * "ma_id=" + m.get("ma_id") +
			 * " and (select sum(mm_havegetqty) from MakeMaterial where mm_maid=ma_id)>0"
			 * );
			 */
			// 记录操作
			baseDao.logger.others("冻结重启", "冻结重启成功", caller, "ma_id", m.get("ma_id"));
		}
	}
	
	@Override
	public void vastFreeze(String data, String caller) {
		List<Map<Object, Object>> list = BaseUtil.parseGridStoreToMaps(data);
		Object ma_bzremark = null;
		for (Map<Object, Object> m : list) {
			String tasktype = baseDao.getFieldDataByCondition("make", "ma_tasktype", "ma_id=" + m.get("ma_id")).toString();
			if (tasktype != null) {
				caller = "OS".equals(tasktype) ? "Make" : "Make!Base";
			}
			if (m.containsKey("ma_bzremark") && !StringUtil.hasText(m.get("ma_bzremark"))) {
				BaseUtil.showError("冻结备注没有填写,冻结失败!");
			}
			ma_bzremark = m.get("ma_bzremark");
			baseDao.updateByCondition("Make", "ma_statuscode='FREEZE',ma_status='" + BaseUtil.getLocalMessage("FREEZE") + "'", "ma_id="
					+ m.get("ma_id"));
			// ma_bzremark 冻结备注
			if (StringUtil.hasText(ma_bzremark)) {
				baseDao.updateByCondition("Make", "ma_bzremark='" + ma_bzremark + "'", "ma_id=" + m.get("ma_id"));
			}
			baseDao.logger.others("冻结操作", "冻结成功", caller, "ma_id", m.get("ma_id"));
		}
	}

	/**
	 * 成套退料时，更新工单用料在线结存数量
	 */
	@Override
	public void calOnlineQty(String ids, String caller) {
		// 计算在制
		makeDao.setMMOnlineQTY(ids, null);
		// 计算已转退料数和已转报废数
		makeDao.setBackQty(ids, 0);
		// 本次退料或报废数需扣减已转报废和已转退料数
		baseDao.updateByCondition("makematerial", "mm_thisqty=mm_onlineqty-nvl(mm_backqty,0)-NVL(mm_turnscrapqty,0)-NVL(mm_stepinqty,0)", " mm_maid in (" + ids
				+ ")");
		baseDao.updateByCondition("MakeMaterialreplace", "mp_thisqty=(select mm_thisqty from MakeMaterial where mm_id=mp_mmid)",
				"mp_maid in (" + ids + ")");
	}

	@Override
	public void vastFinishResStart(String data, String caller) {
		StringBuffer s = new StringBuffer();
		StringBuffer sb = null;
		List<Map<Object, Object>> list = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> m : list) {
			String tasktype = baseDao.getFieldDataByCondition("make", "ma_tasktype", "ma_id=" + m.get("ma_id")).toString();
			if (tasktype != null) {
				caller = "OS".equals(tasktype) ? "Make" : "Make!Base";
			}
			SqlRowList rs = baseDao.queryForRowSet("select ma_code from make where ma_id=" + m.get("ma_id")
					+ " and ma_coststatuscode='FINISH'");
			if (rs.hasNext()) {
				sb = new StringBuffer();
				while (rs.next()) {
					if (sb.length() == 0) {
						sb.append("工单号:");
						sb.append(rs.getString("ma_code"));
						sb.append("<br>");
					}
				}
				if (sb.length() > 0) {
					sb.append("已经财务结案，不允许反结案!");
					s.append(sb);
					s.append("<hr>");
				}
			} else {
				baseDao.updateByCondition("Make",
						"ma_actenddate=null,ma_endstatus='待上传',ma_bzremark='',ma_statuscode='AUDITED',ma_status='" + BaseUtil.getLocalMessage("AUDITED")
								+ "'", "ma_id=" + m.get("ma_id"));
				updateSd_tomakeqty(Integer.parseInt(m.get("ma_id").toString()));
				// 记录操作
				baseDao.logger.resEnd(caller, "ma_id", m.get("ma_id"));
			}
		}
		if (s.length() > 0) {
			BaseUtil.showError("AFTERSUCCESS" + s.toString());
		}
	}

	@Override
	public void setLSThisqty(String caller, String maid, Integer setqty, String wipwhcode) {
		makeDao.setLSThisqty(caller, maid, setqty, wipwhcode);
	}

	@Override
	public void calAddQty(String ids, String caller) {
		makeDao.setAddQty(ids);
		//增加存储过程计算可用库存
		String str = baseDao.callProcedure("MM_SETASSIGNQTY", new String[] {ids});
		if (str != null && !str.trim().equals("")) {
			// 提示错误信息
			BaseUtil.showError("计算可用库存失败");
		}
	}

	@Override
	public void refreshqty(Integer maid, String caller, Integer mmid) {
		makeDao.refreshTurnQty(maid, mmid);
		makeDao.setBackQty(maid.toString(), mmid);
	}

	@Override
	public List<Object> checkmfcode(String mf_code, String caller) {
		mf_code = "'" + mf_code.replace(",", "','") + "'";
		String prodSql = "select pd_flowcode from prodiodetail where pd_flowcode in (" + mf_code + ")";
		SqlRowList sqlRowList = baseDao.queryForRowSet(prodSql);
		List<Object> data = new ArrayList<Object>();
		if (sqlRowList.hasNext()) {
			while (sqlRowList.next()) {
				data.add(sqlRowList.getObject(1));
			}
		} else {
			sqlRowList = baseDao.queryForRowSet("select si_flowcode from stepio where in (" + mf_code + ")");
			while (sqlRowList.next()) {
				data.add(sqlRowList.getObject(1));
			}
		}
		return data;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void splitMake(String formdata, String data, String caller) {
		Map<Object, Object> madata = BaseUtil.parseFormStoreToMap(formdata);
		int id = Integer.parseInt(madata.get("ma_id").toString());
		Object planbegindate = madata.get("ma_planbegindate");
		Object planenddate = madata.get("ma_planenddate");
		Object teamcode = madata.get("ma_teamcode");
		Object[] datas = baseDao.getFieldsDataByCondition("make", new String[] { "ma_qty", "ma_code", "ma_tasktype", "nvl(ma_price,0)" },
				"ma_id=" + id);
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		int splitsumqty = 0;
		int allmaqty = Integer.parseInt(datas[0].toString());
		Object[] probj = baseDao.getFieldsDataByCondition("product", new String[] { "NVL(pr_leadtime,0)", "NVL(pr_plzl,0)",
				"NVL(pr_gdtqq,0)"}, "pr_code in (select ma_prodcode from make where ma_id=" + id + " and ma_prodcode<>' ')");
		Double price = Double.parseDouble(datas[3].toString());
		String maketype = datas[2].toString();
		for (Map<Object, Object> map : maps) {
			if (DateUtil.compare(DateUtil.getCurrentDate(), map.get("ma_planbegindate").toString()) > 0) {
				BaseUtil.showError("明细行中计划开工日期必须大于等于今天");
			}
			/*
			 * if (maketype.equals("MAKE")) { Object ob =
			 * baseDao.getFieldDataByCondition("CUSTOMTABLE", "CT_CODE",
			 * "CT_VARCHAR50_1='" + map.get("ma_teamcode").toString() + "'"); if
			 * (ob == null) { BaseUtil.showError("明细行中线别无效，不允许保存"); } }
			 */
			splitsumqty += Integer.parseInt(map.get("ma_qty").toString());
		}
		// 刷新工单数量 限制数量不能小于 已领数+已转数
		refreshqty(id, caller, 0);
		int remainmaqty = allmaqty - splitsumqty;
		// 分拆后剩余的原始工单 明细中不能存在 用料数小于已领料数 -报废数+已转领料数
		SqlRowList sl = baseDao.queryForRowSet("Select * from Makematerial where  mm_maid='" + id
				+ "' and round(mm_havegetqty-nvl(mm_scrapqty,0),5)+NVL(mm_totaluseqty,0)>round(mm_qty*(" + remainmaqty + "*1.0/" + allmaqty
				+ "),5) and mm_qty>0 and mm_oneuseqty>0");
		if (sl.next()) {
			BaseUtil.showError("原始工单 序号:" + sl.getInt("mm_detno") + ",物料:" + sl.getString("mm_prodcode") + "领料数+已转领料数大于分拆后的需求数，不能分拆!");
		}
		// 判断是否有未执行的变更单 zhongyl 20131223
		sl = baseDao
				.queryForRowSet("Select * from makechangedetail,makechange,make where md_mcid=mc_id and md_makecode=ma_code and mc_statuscode='COMMITED' and  ma_id='"
						+ id + "' ");
		if (sl.next()) {
			BaseUtil.showError("原始工单存在未执行的生产变更单，不能分拆!");
		}
		// 判断是否有未执行的制造ECN ZHONGYL 20140606
		sl = baseDao
				.queryForRowSet("Select count(1) num, wm_concat(mc_code) mc_code from makematerialchange,makematerialchangeDet,make where md_mcid=mc_id and Md_Makecode=ma_code and mc_statuscode<>'AUDITED' and ma_id='"
						+ id + "' and NVL(md_didstatus,' ')<>'已取消'");
		if (sl.next()) {
			if (sl.getInt("num") > 0) {
				BaseUtil.showError("原始工单存在未执行的制造ECN单:" + sl.getString("mc_code") + "，不能分拆!");
			}
		}
		// 更新 原始工单的日期
		if (planbegindate != null && planenddate != null) {
			// 完工日期小于开工日期，根据提前期默认完工日期
			if (DateUtil.compare(planbegindate.toString(), planenddate.toString()) > 0) {
				Date enddate1 = null;
				enddate1 = getFinishDate(planbegindate.toString(), remainmaqty, Integer.parseInt(probj[0].toString()),
						Integer.parseInt(probj[1].toString()));
				planenddate = enddate1;
			}
			if (teamcode != null)
				baseDao.updateByCondition("make", "ma_planbegindate=to_date('" + planbegindate.toString()
						+ "','yyyy-MM-dd'),ma_planenddate=to_date('" + planenddate + "','yyyy-MM-dd'),ma_teamcode='" + teamcode.toString()
						+ "'", "ma_id=" + id);
			else
				baseDao.updateByCondition("make", "ma_planbegindate=to_date('" + planbegindate.toString()
						+ "','yyyy-MM-dd'),ma_planenddate=to_date('" + planenddate + "','yyyy-MM-dd')", "ma_id=" + id);
			// 更新ma_requireddate
			baseDao.updateByCondition("make", "ma_requiredate=ma_planenddate+" + probj[2], "ma_id=" + id);
		}
		// 工单拆分
		SqlRowList sl2 = baseDao.queryForRowSet("select ma_code from make where ma_version='" + datas[1]
				+ "' and ma_code like '%-%' order by ma_id desc");
		int thisdetno = 0;
		if (sl2.next()) {
			thisdetno = Integer.parseInt(sl2.getString("ma_code").split("-")[1]) + 1;
		}
		thisdetno = thisdetno == 0 ? 1 : thisdetno;
		Map<String, Object> basemake = new HashMap<String, Object>();
		SqlRowList sl3 = baseDao.queryForRowSet("select * from make where ma_id=" + id);
		if (sl3.next()) {
			basemake = sl3.getCurrentMap();
		}
		// 先锁定记录，防止多次分拆
		baseDao.execute("update make set ma_version=ma_version where ma_id=" + id);
		SqlRowList sl0 = baseDao.queryForRowSet("select * from make where ma_id=" + id + " and ma_qty=" + allmaqty);
		if (!sl0.hasNext()) {
			BaseUtil.showError("原始工单数量已变，不能分拆!");
		}
		int maid = 0;
		String macode = "";
		int maqty = 0;
		//获取参数设置，工单拆分或者工单变更：原标准需求物料按照标准损耗计算新旧工单需求数
		boolean standardqty = false;
		if("OS".equals(maketype)){
			 standardqty = baseDao.isDBSetting("Make","usingSPrLossRate");
		}else{
			 standardqty = baseDao.isDBSetting("Make!Base","usingSPrLossRate");
		}
		for (Map<Object, Object> map : maps) {
			// 判断
			maid = baseDao.getSeqId("MAKE_SEQ");
			boolean bool = baseDao.checkIf("make", "ma_code='" + datas[1].toString() + "-" + thisdetno + "'");
			while (bool) {
				thisdetno++;
				bool = baseDao.checkIf("make", "ma_code='" + datas[1].toString() + "-" + thisdetno + "'");
			}
			maqty = Integer.parseInt(map.get("ma_qty").toString());
			if (map.get("ma_planenddate") == null
					|| DateUtil.compare(map.get("ma_planbegindate").toString(), map.get("ma_planenddate").toString()) > 0) {
				Date enddate = null;
				enddate = getFinishDate(map.get("ma_planbegindate").toString(), Integer.parseInt(map.get("ma_qty").toString()),
						Integer.parseInt(probj[0].toString()), Integer.parseInt(probj[1].toString()));
				map.remove("ma_planenddate");
				map.put("ma_planenddate", DateUtil.parseDateToString(enddate, Constant.YMD_HMS));
			}
			basemake.remove("ma_code");
			basemake.remove("ma_qty");
			basemake.remove("ma_id");
			basemake.remove("ma_teamcode");
			basemake.remove("ma_planbegindate");
			basemake.remove("ma_planenddate");
			basemake.remove("ma_actbegindate");
			basemake.remove("ma_version");
			basemake.remove("ma_madeqty");
			basemake.remove("ma_haveqty");
			basemake.remove("ma_recorder");
			basemake.remove("ma_recorddate");
			basemake.remove("ma_toquaqty");
			macode = datas[1].toString() + "-" + thisdetno;
			basemake.put("ma_code", macode);
			basemake.put("ma_qty", maqty);
			if ("OS".equals(maketype)) {
				basemake.remove("ma_total");
				basemake.put("ma_total", price * maqty);
			}
			basemake.put("ma_id", maid);
			basemake.put("ma_madeqty", 0);
			basemake.put("ma_haveqty", 0);
			basemake.put("ma_recorder", SystemSession.getUser().getEm_name());
			basemake.put("ma_recorddate", DateUtil.parseDateToString(new Date(), Constant.YMD_HMS));
			basemake.put("ma_teamcode", map.get("ma_teamcode"));
			basemake.put("ma_planbegindate", map.get("ma_planbegindate"));
			basemake.put("ma_planenddate", map.get("ma_planenddate"));
			basemake.put("ma_version", datas[1].toString());
			baseDao.execute(SqlUtil.getInsertSqlByMap(basemake, "make"));
			thisdetno++;
			//add 拆分将字段 MM_UPDATETYPE 值赋值至新工单
			String insertmaterial = "insert into Makematerial(mm_id,mm_maid,mm_code,mm_detno,mm_prodcode,mm_level,mm_bomid,mm_topbomid,"
					+ "mm_mothercode,mm_bddetno,mm_oneuseqty,mm_lostqty,mm_qty,mm_canuserepqty,mm_ifrep,mm_havegetqty,mm_totaluseqty,mm_builddate,mm_remark,mm_topmothercode,"
					+ "mm_returnedqty,mm_mrpclosed,mm_bdid,mm_supplytype,mm_wccode,mm_mdcode,mm_balance,mm_materialstatus,mm_processcode,mm_whcode,mm_updatetype,mm_repprodcode)"
					+ "select  Makematerial_SEQ.nextval,'"
					+ maid
					+ "','"
					+ macode
					+ "',mm_detno,mm_prodcode,mm_level,mm_bomid,mm_topbomid,"
					+ "mm_mothercode,mm_bddetno,mm_oneuseqty,mm_lostqty,round(mm_qty*1.00*" + maqty +"/"
					+ allmaqty
					+" +0.4999*power(0.1,nvl(pr_precision,0)),nvl(pr_precision,0))"
					+ ",round(mm_canuserepqty*("
					+ maqty
					+ "* 1.0/"
					+ allmaqty
					+ ")+ 0.4999*power(0.1,nvl(pr_precision,0)),nvl(pr_precision,0)),mm_ifrep,0,0,sysdate,mm_remark,mm_topmothercode,"
					+ "0,mm_mrpclosed,mm_bdid,mm_supplytype,mm_wccode,mm_mdcode,round(mm_qty*("
					+ maqty
					+ "* 1.0/"
					+ allmaqty
					+ ") + 0.4999*power(0.1,nvl(pr_precision,0)),nvl(pr_precision,0))-mm_oneuseqty*"
					+ maqty
					+ ",mm_materialstatus,mm_processcode,mm_whcode,mm_updatetype,mm_repprodcode "
					+ "from makematerial left join product on mm_prodcode=pr_code where mm_maid='"
					+ id + "'";
			baseDao.execute(insertmaterial);
			if(standardqty){
				baseDao.execute(" merge into makematerial using product on ( mm_prodcode=pr_code)"
								+" when matched then update set mm_qty=round(mm_oneuseqty*?*(1+(CASE WHEN ?='OS' AND NVL(pr_exportlossrate,0)>0 THEN NVL(pr_exportlossrate, 0) "
								+" ELSE NVL(pr_lossrate, 0) END )* 0.01)+0.4999*power(0.1,nvl(pr_precision,0)),nvl(pr_precision,0)),"
								+" mm_balance=round(mm_oneuseqty*?*(1+ (CASE WHEN ?='OS' AND NVL(pr_exportlossrate,0)>0 THEN NVL(pr_exportlossrate, 0) "
								+" ELSE NVL(pr_lossrate, 0) END )* 0.01)+0.4999*power(0.1,nvl(pr_precision,0)),nvl(pr_precision,0))-mm_oneuseqty*? where mm_maid=? and mm_qty>=mm_oneuseqty*?",maqty,maketype,maqty,maketype,maqty,maid,maqty);			
			}
			// 插入替代料
			baseDao.execute("insert into MakematerialReplace(mp_mmid,mp_maid,mp_detno,mp_prodcode,mp_rate,mp_canuseqty,mp_whcode,mp_remark,mp_mmcode,mp_mmdetno,mp_warehouseid)"
					+ "select mm_id,"
					+ maid
					+ ",mp_detno,mp_prodcode,mp_rate,round((mp_canuseqty-NVL(mp_haverepqty,0))*("
					+ maqty
					* 1.0
					+ "/"
					+ allmaqty
					+ ") + 0.4999*power(0.1,nvl(pr_precision,0)),nvl(pr_precision,0)),mp_whcode,mp_remark,'"
					+ macode
					+ "',mm_detno ,mp_warehouseid "
					+ "from makematerial,makematerialreplace ,product where mp_mmcode ='"
					+ datas[1].toString()
					+ "' and mm_code='"
					+ macode
					+ "' and mm_detno=mp_mmdetno and mm_prodcode=pr_code");
			baseDao.execute("update MakeMaterial set mm_canuserepqty=(select sum(NVL(mp_canuseqty,0)) from MakeMaterialReplace where mp_mmid=mm_id and mp_canuseqty>0) where  mm_maid="
					+ maid);
			baseDao.execute("update makematerial set mm_balance=0 where mm_maid='" + maid + "' and (mm_balance<0 or mm_oneuseqty<0) ");
			makeDao.changeMakeGetStatus(maid);
			//拆分工单后新旧工单都记录操作日志，便于后期查询工单来源   2018030129
			//新工单日志记录
			baseDao.logger.others("工单拆分", "来源于工单："+datas[1].toString(), "Make!Base", "ma_id", maid);
			//旧工单日志记录
			baseDao.logger.others("工单拆分", "拆成"+macode+"工单，数量："+maqty, "Make!Base", "ma_id", id);
		}
		// 更新原始工单数量
		if (allmaqty > remainmaqty) {
			String SQLStr = "merge into makematerial using  product on (mm_maid='"+ id +"' and  mm_prodcode=pr_code) when matched then "
					+ "update set mm_qty=round(mm_qty*1.00*"+ remainmaqty + " /" + allmaqty +"+0.4999*power(0.1,nvl(pr_precision,0)),nvl(pr_precision,0)) "
					+",mm_balance=round(mm_qty*("+ remainmaqty + "* 1.0/" + allmaqty + ")+ 0.4999*power(0.1,nvl(pr_precision,0)),nvl(pr_precision,0))-mm_oneuseqty*" + remainmaqty;
			baseDao.execute(SQLStr);
			/*baseDao.execute("update makematerial set mm_qty=round(mm_qty*1.00*" + sl.getDouble("md_newqty")
					+ "/"+sl.getDouble("md_qty")+" +0.4999*power(0.1,"+probj[3]+"),"+probj[3]+") end),mm_balance=ceil(mm_qty*("
					+ remainmaqty + "* 1.0/" + allmaqty + "))-mm_oneuseqty*" + remainmaqty + " where mm_maid='" + id + "'");*/
			if (!"OS".equals(maketype)) {
				baseDao.execute("update make set ma_qty=" + remainmaqty + " where ma_id='" + id + "'");
			} else {
				baseDao.execute("update make set ma_total=" + remainmaqty * price + ",ma_qty=" + remainmaqty + " where ma_id='" + id + "'");
				baseDao.execute("update make set ma_totalupper=L2U(nvl(ma_total,0)) WHERE ma_id=" + id);
			}
			if(standardqty){
					baseDao.execute(" merge into makematerial using product on (mm_prodcode=pr_code)"
							+" when matched then update set mm_qty=round(mm_oneuseqty*?*(1+(CASE WHEN ?='OS' AND NVL(pr_exportlossrate,0)>0 THEN NVL(pr_exportlossrate, 0) "
							+" ELSE NVL(pr_lossrate, 0) END )* 0.01)+0.4999*power(0.1,nvl(pr_precision,0)),nvl(pr_precision,0)),"
							+" mm_balance=round(mm_oneuseqty*?*(1+ (CASE WHEN ?='OS' AND NVL(pr_exportlossrate,0)>0 THEN NVL(pr_exportlossrate, 0) "
							+" ELSE NVL(pr_lossrate, 0) END )* 0.01)+0.4999*power(0.1,nvl(pr_precision,0)),nvl(pr_precision,0))-mm_oneuseqty*? where mm_maid=? and mm_qty>=mm_oneuseqty*? ",remainmaqty,maketype,remainmaqty,maketype,remainmaqty,id,remainmaqty);			
			}
			makeDao.setBalance(String.valueOf(id), "");
			baseDao.execute("merge into MakematerialReplace using (select mm_id,mm_qty,pr_precision from makematerial left join product on mm_prodcode=pr_code where mm_maid='"
					+ id
					+ "' ) on (mm_id=mp_mmid) when matched then update set mp_canuseqty=mp_canuseqty-round((mp_canuseqty-(NVL(mp_haverepqty,0)+NVL(mp_returnmqty,0)-NVL(mp_addqty,0)))*"
					+ splitsumqty + "*1.0/" + allmaqty + "+ 0.4999*power(0.1,nvl(pr_precision,0)),nvl(pr_precision,0)) where mp_mmid in (select mm_id from makematerial where mm_maid='" + id
					+ "')");
			baseDao.execute("update MakeMaterial set mm_canuserepqty=(select sum(nvl(mp_canuseqty,0)) from MakeMaterialReplace where mp_mmid=mm_id) where  mm_maid="
					+ id);
			baseDao.execute("update makematerial set mm_canuserepqty=mm_qty where mm_maid='" + id + "' and mm_canuserepqty>mm_qty");
			makeDao.changeMakeGetStatus(id);
		}
	}

	@Override
	public void enforceEndMake(int ma_id, String caller, String remark) {
		// 只能对状态为[已审核]的工单进行结案操作!
		Object[] status = baseDao.getFieldsDataByCondition("Make", "ma_statuscode,ma_code,ma_tasktype", "ma_id=" + ma_id);
		if (!status[0].equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.end_onlyAudited"));
		}
		caller = "Make!Base";
		caller = status[2].equals("OS") ? "Make" : caller;
		// 存在未过账的出入库单不能强制结案 zhongyl 20141029
		SqlRowList sl0 = baseDao
				.queryForRowSet("select count(1) as c,wm_concat(pi_class||pi_inoutno) as code from (select distinct pi_inoutno,pi_class from prodinout,prodiodetail where pi_id=pd_piid and pd_ordercode='"
						+ status[1] + "' and pd_status=0 and pi_class in('生产领料单','委外领料单','生产退料单','委外退料单','生产补料单','委外补料单') )A");
		if (sl0.next()) {
			if (sl0.getInt("c") > 0) {
				BaseUtil.showError("工单:"+status[1]+",存在未过账的出入库单不能强制结案，单号:" + sl0.getString("code") + "!");
				return;
			}
		}
		if(!StringUtil.hasText(remark)){
			remark = "强制结案";
		}
		// 结案
		baseDao.updateByCondition("Make", "ma_statuscode='FINISH',ma_status='" + BaseUtil.getLocalMessage("FINISH")
				+ "',ma_actenddate = sysdate, ma_bzremark='"+remark+"',ma_endstatus='待上传'", "ma_id=" + ma_id);
		updateSd_tomakeqty(ma_id);
		// 记录操作
		baseDao.logger.others("强制结案", "强制结案成功", caller, "ma_id", ma_id);
	}

	@Override
	public void updateOSVendor(Integer id, String vendcode, String currency, String taxrate, String price, String paymc, String paym,
			String ma_servicer, String remark, String apvendcode, String caller) {
		Object[] ma = baseDao.getFieldsDataByCondition("Make", new String[] { "ma_code", "nvl(ma_price,0)" }, "ma_id=" + id);
		if (ma != null) {
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') from prodiodetail where pd_ordercode=? and pd_piclass in ('委外验收单','委外验退单')",
							String.class, ma[0]);
			if (dets != null && NumberUtil.formatDouble(ma[1].toString(), 6) != NumberUtil.formatDouble(price, 6)) {
				BaseUtil.showError("已存在出入库单，不允许变更单价！" + dets);
			}
		}
		if (!ma_servicer.equals("1") && !ma_servicer.equals("-1")) {
			ma_servicer = "0";
		}
		Object vendname = baseDao.getFieldDataByCondition("Vendor", "ve_name", "ve_code='" + vendcode + "'");
		baseDao.updateByCondition("Make", "ma_vendcode='" + vendcode + "', ma_vendname='" + vendname + "', ma_currency='" + currency
				+ "',ma_taxrate= " + taxrate + ", ma_price=round(" + price + ",8), ma_total=round(" + price
				+ "*ma_qty,2), ma_paymentscode='" + paymc + "', ma_payments='" + paym + "',ma_servicer='" + ma_servicer + "'", "ma_id ="
				+ id);
		// 更新汇率
		baseDao.execute("update make set ma_rate=(select cm_crrate from currencysmonth where ma_currency=cm_crname and "
				+ "cm_yearmonth=to_char(ma_date,'yyyymm')) where ma_id=?", id);
		int argCount = baseDao.getCountByCondition("user_tab_columns",
				"table_name='MAKE' and column_name in ('MA_APVENDCODE','MA_APVENDNAME')");
		if (argCount == 2) {
			if (StringUtil.hasText(apvendcode)) {
				baseDao.execute("update make set MA_APVENDCODE='" + apvendcode + "' where ma_id=" + id);
				baseDao.execute("update make set MA_APVENDNAME=(select ve_name from vendor where ve_code=MA_APVENDCODE) where ma_id=" + id
						+ " and nvl(MA_APVENDCODE,' ')<>' '");
			} else {
				baseDao.execute("update make set (MA_APVENDCODE,MA_APVENDNAME)=(select ve_apvendcode,ve_apvendname from vendor where ve_code=ma_vendcode) where ma_id="
						+ id);
			}
		}
		if (remark != null && !"".equals(remark)) {
			baseDao.updateByCondition("Make", "ma_remark='" + remark + "'", "ma_id =" + id);
		}
		baseDao.execute("update make set ma_pricetype='手工录入单价' where ma_id=" + id);
		//更改委外商时，使用按委外商定义的物料损耗率
		if(baseDao.isDBSetting(caller, "osProductlossRate")){
			setMM_balance(ma[0]);
		}
		// 记录操作
		baseDao.logger.others("更新委外信息", "msg.updateSuccess", caller, "ma_id", id);

	}

	@Override
	public void vastupdatemakecoststatus(String data, String caller) {
		List<Map<Object, Object>> list = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> map : list) {
			baseDao.updateByCondition("make", "ma_coststatus='已结案',ma_coststatuscode='FINISH'", "ma_id=" + map.get("ma_id"));
		}
	}

	@Override
	public void vastcostrestartMake(String data, String caller) {
		List<Map<Object, Object>> list = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> map : list) {
			baseDao.updateByCondition("make", "ma_coststatus='已审核',ma_coststatuscode='AUDITED'", "ma_id=" + map.get("ma_id"));
			updateSd_tomakeqty(Integer.parseInt(map.get("ma_id").toString()));
		}
	}

	@Override
	public void updateRemark(int id, String remark, String caller) {
		if (remark != null && !"".equals(remark)) {
			baseDao.updateByCondition("Make", "ma_remark='" + remark + "'", "ma_id=" + id);
		}
		// 记录操作
		baseDao.logger.others("更新备注", "msg.updateSuccess", caller, "ma_id", id);
	}

	@Override
	public void updateTeamcode(int id, String value, String caller) {
		baseDao.updateByCondition("Make", "ma_teamcode='" + value + "'", "ma_id=" + id);
		// 记录操作
		baseDao.logger.others("更新线别", "msg.updateSuccess", caller, "ma_id", id);
	}

	@Override
	public void updateMaterialWH(int id, String whcode, String caller) {
		baseDao.updateByCondition("MakeMaterial", "mm_whcode='" + whcode + "'", "mm_maid=" + id);
		//更新替代料仓库
		baseDao.updateByCondition("MAKEMATERIALREPLACE","MP_WHCODE='"+whcode+"',MP_WAREHOUSEID=(SELECT WH_ID FROM WAREHOUSE WHERE WH_CODE='"+whcode+"')","MP_MAID="+id);
		// 记录操作
		baseDao.logger.others("更新用料表仓库", "msg.updateSuccess", caller, "ma_id", id);
	}

	@Override
	public void turnOSMake(String caller, Integer maid, String kind) {
		String haveGet = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WMSYS.WM_CONCAT(mm_detno) from makematerial where mm_maid=? and (mm_code,mm_detno) in (select pd_ordercode,pd_orderdetno from prodiodetail where pd_piclass in ('生产领料单','委外领料单','生产退料单','委外退料单','生产补料单','委外补料单'))",
						String.class, maid);
		if (haveGet != null) {
			BaseUtil.showError("当前工单的明细行" + haveGet + "发生过领退料操作，不能转委外单!");
		}
		Object ob = baseDao.getFieldDataByCondition("make", "ma_code", "ma_id=" + maid + " and ma_checkstatuscode='COMMITED'");
		if (ob != null && ob != " ") {
			BaseUtil.showError("单据状态为已提交，请反提交之后再转委外单");
		}
		Object ob2 = baseDao.getFieldDataByCondition("make", "ma_code", "ma_id=" + maid + " and ma_statuscode='FINISH'");
		if (ob2 != null && ob2 != " ") {
			BaseUtil.showError("单据状态为已结案，不允许转委外单!");
		}
		// 制造单转委外单，根据委外单的逻辑配置，是否委外单的产品生产类型为委外
		boolean osbo = baseDao.isDBSetting("Make", "checkProductOSMake");
		if (osbo) {
			ob = baseDao.getFieldDataByCondition("make left join product on pr_code=ma_prodcode", "pr_code", "ma_id=" + maid
					+ " and pr_manutype<>'OSMAKE'");
			if (ob != null) {
				BaseUtil.showError("系统启用了'委外单的产品所属的生产类型必须是委外'的限制");
			}
		}
		baseDao.updateByCondition("Make", "ma_tasktype='OS',ma_checkstatuscode='UNAPPROVED',ma_checkstatus='未批准',ma_kind='" + kind + "'",
				"ma_id=" + maid);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "转委外单", "成功", "Make|ma_id=" + maid));
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "转委外单", "成功", "Make!Base|ma_id=" + maid));
	}

	@Override
	public void turnOSToMake(String caller, Integer maid, String kind) {
		String haveGet = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WMSYS.WM_CONCAT(mm_detno) from makematerial where mm_maid=? and (mm_code,mm_detno) in (select pd_ordercode,pd_orderdetno from prodiodetail where pd_piclass in ('生产领料单','委外领料单','生产退料单','委外退料单','生产补料单','委外补料单'))",
						String.class, maid);
		if (haveGet != null) {
			BaseUtil.showError("当前工单的明细行" + haveGet + "发生过领退料操作，不能转制造单!");
		}
		Object ob = baseDao.getFieldDataByCondition("make", "ma_code", "ma_id=" + maid
				+ " and ma_checkstatuscode in('COMMITED','APPROVE') ");
		if (ob != null && ob != " ") {
			BaseUtil.showError("审批状态为已提交或已批准，不允许转制造单");
		}
		Object ob2 = baseDao.getFieldDataByCondition("make", "ma_code", "ma_id=" + maid + " and ma_statuscode='FINISH'");
		if (ob2 != null && ob2 != " ") {
			BaseUtil.showError("单据状态为已结案，不允许转制造单!");
		}
		// 制造单转委外单，根据委外单的逻辑配置，是否委外单的产品生产类型为委外
		boolean osbo = baseDao.isDBSetting("Make!Base", "checkProductMake");
		if (osbo) {
			ob = baseDao.getFieldDataByCondition("make left join product on pr_code=ma_prodcode", "pr_code", "ma_id=" + maid
					+ " and pr_manutype<>'MAKE'");
			if (ob != null) {
				BaseUtil.showError("系统启用了'制造单的产品所属的生产类型必须是制造的限制'");
			}
		}
		baseDao.updateByCondition("Make", "ma_tasktype='MAKE',ma_checkstatuscode='UNAPPROVED',ma_checkstatus='未批准',ma_kind='" + kind + "'",
				"ma_id=" + maid);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "转制造单", "成功", "Make|ma_id=" + maid));
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "转制造单", "成功", "Make!Base|ma_id=" + maid));
	}

	@Override
	public String getCodeString(String caller, String table, int type, String conKind) {
		if (table == null || table.equals("")) {
			table = (String) baseDao.getFieldDataByCondition("form", "fo_table", "fo_caller='" + caller + "'");
		}
		String oldCode = baseDao.sGetMaxNumber(table.split(" ")[0], type);
		// 如果MakeKind中有设置对应的前缀码 用新前缀码+原单号
		Object newLCode = baseDao.getFieldDataByCondition("MAKEKIND", "mk_excode", "mk_name='" + conKind + "'");
		if (newLCode != null) {
			if (!newLCode.toString().equals("")) {
				// 修改前缀
				oldCode = newLCode.toString() + oldCode;
			}
		}
		return oldCode;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void setMain(Integer mmid, Integer detno, String caller) {
		int maid = 0;
		SqlRowList sl = baseDao
				.queryForRowSet("select mm_maid,mm_detno,mm_prodcode,mm_totaluseqty,mm_haverepqty,mm_repqty,mp_prodcode,mm_havegetqty,mm_whcode,nvl(mp_whcode,mm_whcode) mp_whcode from MakeMaterial left join makematerialreplace on mp_mmid=mm_id where mm_id="
						+ mmid + " and mp_detno=" + detno);
		if (sl.next()) {
			maid = sl.getInt("mm_maid");
			if (sl.getDouble("mm_havegetqty") > 0 || sl.getDouble("mm_haverepqty") > 0) {
				BaseUtil.showError("已发生领料的明细行不能变更主替关系");
			}
			if (sl.getDouble("mm_repqty") > 0 || sl.getDouble("mm_totaluseqty") > 0) {
				BaseUtil.showError("已转领料数大于0的明细行不能变更主替关系");
			}
			baseDao.updateByCondition("makematerial", "mm_prodcode='" + sl.getString("mp_prodcode") + "'", "mm_id=" + mmid);
			baseDao.updateByCondition("makematerialreplace",
					"mp_prodcode='" + sl.getString("mm_prodcode") + "',mp_whcode='" + sl.getString("mm_whcode") + "'", "mp_mmid=" + mmid
							+ " and mp_detno=" + detno);
			baseDao.updateByCondition("makematerial",
					"mm_repprodcode=(select wm_concat(mp_prodcode) from makematerialreplace where mp_mmid=mm_id)", "mm_id=" + mmid);

			// 记录操作
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "第" + sl.getString("mm_detno") + "替代料设为主料," + "原主料:"
					+ sl.getString("mp_prodcode"), "成功", "Make|ma_id=" + maid));
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "第" + sl.getString("mm_detno") + "替代料设为主料," + "原主料:"
					+ sl.getString("mp_prodcode"), "成功", "Make!Base|ma_id=" + maid));
		}
	}

	// 税率强制等于币别表的默认税率
	private void useDefaultTax(String caller, Object ma_id) {
		if (baseDao.isDBSetting(caller, "useDefaultTax")) {
			baseDao.execute("update make set ma_taxrate=(select cr_taxrate from currencys where ma_currency=cr_name and cr_statuscode='CANUSE')"
					+ " where ma_id=" + ma_id);
		}
	}

	// 本位币允许税率为0
	private void allowZeroTax(String caller, Object ma_id) {
		if (!baseDao.isDBSetting("Make", "allowZeroTax")) {
			String currency = baseDao.getDBSetting("defaultCurrency");
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select WM_CONCAT(ma_code) from make where nvl(ma_taxrate,0)=0 and ma_currency='" + currency + "' and ma_id=?",
					String.class, ma_id);
			if (dets != null) {
				BaseUtil.showError("本位币税率为0，不允许提交!行号：" + dets);
			}
		}
	}

	// 有效性检测
	private void checkAll(String caller, int ma_id) {
		SqlRowList rs0;
		SqlRowList sl = baseDao.queryForRowSet("select * from make left join makekind on (ma_kind=mk_name or ma_kind=mk_code) where ma_id=" + ma_id);
		if (sl.next()) {
			if (sl.getString("ma_sourcekind") != null && !sl.getString("ma_sourcekind").equals("")) {
				if (sl.getString("ma_sourcekind").toUpperCase().equals("SALE")) {
					rs0 = baseDao.queryForRowSet("select sa_code from sale,saledetail where sa_code='" + sl.getString("ma_salecode")
							+ "' and sd_said=sa_id and sd_detno=" + sl.getInt("ma_saledetno"));
					if (!rs0.hasNext()) {
						BaseUtil.showError("销售单号录入错误！");
					}
				} else if (sl.getString("ma_sourcekind").toUpperCase().equals("SALEFORECAST")) {
					rs0 = baseDao.queryForRowSet("select sf_code from saleforecast,saleforecastdetail where sf_code='"
							+ sl.getString("ma_salecode") + "' and sd_sfid=sf_id and sd_detno=" + sl.getInt("ma_saledetno"));
					if (!rs0.hasNext()) {
						BaseUtil.showError("销售预测号录入错误！");
					}
				}
			}
			if (sl.getString("ma_cop") != null && !sl.getString("ma_cop").equals("")) {
				if (sl.getString("ma_salecode") != null && !sl.getString("ma_salecode").equals("")) {
					if (baseDao.isDBSetting("CopCheck")) {
						if (sl.getString("ma_sourcekind") != null && !sl.getString("ma_sourcekind").equals("")) {
							if (sl.getString("ma_sourcekind").toUpperCase().equals("SALE")) {
								rs0 = baseDao.queryForRowSet("select sa_cop from sale where sa_code='" + sl.getString("ma_salecode")
										+ "' and sa_cop<>'" + sl.getString("ma_cop") + "'");
								if (rs0.next()) {
									BaseUtil.showError("订单所属公司是:" + rs0.getString("sa_cop") + "与制造单所属公司不一致！");
								}
							}
							if (sl.getString("ma_sourcekind").toUpperCase().equals("SALEFORECAST")) {
								rs0 = baseDao.queryForRowSet("select sf_cop from saleforecast where sf_code='"
										+ sl.getString("ma_salecode") + "' and sf_cop<>'" + sl.getString("ma_cop") + "'");
								if (!rs0.hasNext()) {
									BaseUtil.showError("预测所属公司是:" + rs0.getString("sf_cop") + "与制造单所属公司不一致！");
								}
							}
						}
					}
				}
			}
			
			rs0 = baseDao.queryForRowSet("select ma_code from make  where ma_id=" + ma_id + " and trunc(ma_planbegindate)<trunc(sysdate) and ma_checkstatuscode<>'COMMITED'");
			if (rs0.next()) {
				BaseUtil.showError("计划开工日期不能早于今天！");
			}
			rs0 = baseDao.queryForRowSet("select count(1) as c,wm_concat(mm_detno) as detno from makematerial where mm_maid=" + ma_id
					+ " and NVL(mm_prodcode,' ')<>'空' and NVL(mm_materialstatus,' ')=' ' and ( NVL(mm_whcode,' ')=' ')");
			if (rs0.next()) {
				if (rs0.getInt("c") > 0) {
					BaseUtil.showError("明细行序号:" + rs0.getString("detno") + "没有定义发料仓库!");
					return;
				}
			}
			rs0 = baseDao
					.queryForRowSet("select count(1) as c,wm_concat(mm_detno) as detno from makematerial left join product on mm_prodcode=pr_code where mm_maid="
							+ ma_id
							+ " and NVL(mm_prodcode,' ')<>'空' and NVL(mm_materialstatus,' ')=' ' and ( NVL(pr_statuscode,' ')<>'AUDITED')");
			if (rs0.next()) {
				if (rs0.getInt("c") > 0) {
					BaseUtil.showError("明细行序号:" + rs0.getString("detno") + "不是已审核的物料!");
					return;
				}
			}
			// 主料和替代料重复不能重复
			rs0 = baseDao.queryForRowSet("select mm_detno from makematerial,MakeMaterialreplace where mm_id=mp_mmid and mm_maid=" + ma_id
					+ " AND mp_prodcode=mm_prodcode");
			if (rs0.next()) {
				BaseUtil.showError("序号：" + rs0.getString("mm_detno") + "主料和替代料重复，不能提交");
			}
			// BOM、子件不能重复 ,【手工维护的重复】+【手工维护与用料计算重复】+【用料计算重复】判断
			rs0 = baseDao
					.queryForRowSet("select wm_concat(distinct A.mm_prodcode) mm_prodcode, count(1) cn from makematerial A ,makematerial B where A.mm_maid="
							+ ma_id
							+ " and A.mm_maid=B.mm_maid and A.mm_prodcode=B.mm_prodcode and nvl(A.mm_stepcode,' ')=NVL(B.mm_stepcode,' ') and A.mm_id<>B.mm_id and "
							+ "(nvl(A.mm_topbomid,0)=nvl(B.mm_topbomid,0) OR (nvl(A.mm_topbomid,0)<>nvl(B.mm_topbomid,0) and "
							+ "(nvl(A.mm_topbomid,0)=0 OR nvl(B.mm_topbomid,0)=0)))");
			if (rs0.next()) {
				if (rs0.getInt("cn") > 0) {
					BaseUtil.showError("物料:" + rs0.getString("mm_prodcode") + "重复，不能提交");
				}
			}
			// 产品编号必须已审核
			rs0 = baseDao.queryForRowSet("select pr_status from make left join product on pr_code=ma_prodcode where ma_id=" + ma_id
					+ " and NVL(pr_statuscode,' ')<>'AUDITED' ");
			if (rs0.next()) {
				BaseUtil.showError("产品料号必须是已审核的物料");
			}
			//@add 20170620 问题反馈2017060644，产品编号对应的BOM与主表BOM编号中的不一致
			rs0 = baseDao.queryForRowSet("select ma_code from make left join bom on ma_prodcode=bo_mothercode where ma_id=? and ma_bomid>0 and bo_id>0 and ma_bomid<>bo_id",ma_id);
			if(rs0.next()){
				BaseUtil.showError("BOM编号与产品料号实际BOMID不一致！");
			}
			rs0 = baseDao.queryForRowSet("select ma_code from make  where ma_id=" + ma_id
					+ " and ma_tasktype<>'OS' and NVL(ma_wccode,' ') not in (select wc_code from workcenter) ");
			if (rs0.next()) {
				BaseUtil.showError("工作中心必须正确填写!");
				return;
			}
			// 明细行不能为空
			rs0 = baseDao.queryForRowSet("select count(1)c from makematerial where mm_maid=" + ma_id + " ");
			if (rs0.next() && rs0.getInt("c") == 0) {
				BaseUtil.showError("至少要有一行明细用料");
			}
			/*
			 * rs0 = baseDao.queryForRowSet(
			 * "select count(0) cn, wmsys.wm_concat('序号：'||mm_detno||',物料编号：'||mm_prodcode)  detno  from makematerial left join product on pr_code=mm_prodcode where mm_maid="
			 * +ma_id+" and pr_supplytype ='VIRTUAL' and mm_level is null");
			 * if(rs0.next()){ if(rs0.getInt("cn") > 0 )
			 * BaseUtil.showError(rs0.getString("detno")+",为虚拟件"); }
			 */
			// 产品编号不能为虚拟特征件
			rs0 = baseDao.queryForRowSet("select ma_code from make left join product on pr_code=ma_prodcode where ma_id=" + ma_id
					+ " and pr_specvalue='NOTSPECIFIC'");
			if (rs0.next()) {
				BaseUtil.showError("产品料号不能为[虚拟特征件]物料");
			}
			// 用料表物料编号不能为虚拟特征件
			rs0 = baseDao
					.queryForRowSet("select count(1) as c,wm_concat(mm_detno) as detno from makematerial left join product on mm_prodcode=pr_code where mm_maid="
							+ ma_id + "  and pr_specvalue='NOTSPECIFIC'");
			if (rs0.next()) {
				if (rs0.getInt("c") > 0) {
					BaseUtil.showError("明细行序号:" + rs0.getString("detno") + "料号不能为[虚拟特征件]物料!");
					return;
				}
			}
			//标准工单必须存在一行明细的需求数大于等于 单位用量*工单数
			if(sl.getObject("mk_type") != null && sl.getString("mk_type").equals("S")){
				rs0 = baseDao.queryForRowSet("select count(1) cn from make left join makematerial on mm_maid=ma_id where ma_id=? and nvl(mm_qty,0)>=ma_qty*nvl(mm_oneuseqty,0)",ma_id);
				if(rs0.next() && rs0.getInt("cn")==0){
					BaseUtil.showError("标准工单用料表明细行至少有一行的需求数大于等于单位用量*工单数");
			    }
			}
			//@add 20180308 用料表中虚拟件必须存在已审核的BOM或者虚拟件对应的BOM必须是已审核状态
			if (baseDao.isDBSetting(caller, "virtualCodeHasBom")) {
				rs0 = baseDao.queryForRowSet("select wm_concat(mm_detno)detno,count(1) cn from makematerial left join product on pr_code=mm_prodcode left join bom on bo_mothercode=pr_code where mm_maid=? and pr_supplytype ='VIRTUAL' and (nvl(bo_id,0)=0 or (bo_statuscode<>'AUDITED' AND bo_level<>'外购件BOM')) and rownum<20",ma_id);
				if(rs0.next() && rs0.getInt("cn")>0){
					BaseUtil.showError("明细行序号："+rs0.getString("detno")+"是虚拟件没有建立已审核的BOM");
			    }
			}else{
				rs0 = baseDao.queryForRowSet("select wm_concat(mm_detno)detno,count(1) cn from makematerial left join product on pr_code=mm_prodcode left join bom on bo_mothercode=pr_code where mm_maid=? and pr_supplytype ='VIRTUAL' and nvl(bo_id,0)>0 and bo_statuscode<>'AUDITED' AND bo_level<>'外购件BOM' and rownum<20",ma_id);
				if(rs0.next() && rs0.getInt("cn")>0){
					BaseUtil.showError("明细行序号："+rs0.getString("detno")+"是虚拟件，存在状态不是已审核的BOM");
			    }
			}
		}
		// 产品编号不能为外购、客供和虚拟件的物料
		CustOrPurs(caller, ma_id);
		// 工单类型符合BOM等级有效性检测
		Make_audit_bolevel(ma_id);
	}

	/**
	 * 判断工单类型是否符合BOM等级定义的要求
	 * 
	 * @author ZHONGYL
	 * */
	private void Make_audit_bolevel(Integer ma_id) {
		String SQLStr = "";
		SqlRowList rs, rs0;
		SQLStr = "select ma_code,ma_qty,ma_kind,ma_prodcode,ma_bomid,bo_id,bo_level,bl_id  from make left join bom on ma_bomid=bo_id left join bomlevel on bo_level=bl_code  where ma_id='"
				+ ma_id + "' and ma_kind<>' ' ";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("bo_id") > 0 && rs.getInt("bl_id") > 0) {
				// 判断是否禁用的
				rs0 = baseDao.queryForRowSet("select count(1) num from Maketypedetail  where md_blid='" + rs.getInt("bl_id")
						+ "' and NVL(md_useable,0)=0 and md_prodtype='" + rs.getString("ma_kind") + "'");
				if (rs0.next()) {
					if (rs0.getInt("num") > 0) {
						BaseUtil.showError("BOM等级：" + rs.getString("bo_level") + "不允许下达类型【" + rs.getString("ma_kind") + "】的工单");
					}
				}
				// 判断是否在设置允许范围内
				rs0 = baseDao.queryForRowSet("select count(1) num,sum((case when md_prodtype='" + rs.getString("ma_kind")
						+ "' then 1 else 0 end)) as alownum from Maketypedetail  where md_blid='" + rs.getInt("bl_id")
						+ "' and NVL(md_useable,0)<>0 ");
				if (rs0.next()) {
					if (rs0.getInt("num") > 0 && rs0.getInt("alownum") == 0) {
						BaseUtil.showError("类型【" + rs.getString("ma_kind") + "】的工单 不在等级：" + rs.getString("bo_level") + "的BOM允许下达范围内");
					}
				}
				// 判断是否超出数量限制
				rs0 = baseDao.queryForRowSet("select count(1) num from Maketypedetail where md_blid='" + rs.getInt("bl_id")
						+ "' and NVL(md_useable,0)<>0 and md_prodtype='" + rs.getString("ma_kind") + "' and md_maxnum>0 and md_maxnum<"
						+ rs.getDouble("ma_qty"));
				if (rs0.next()) {
					if (rs0.getInt("num") > 0) {
						BaseUtil.showError("工单数量超过BOM等级：" + rs.getString("bo_level") + "允许下达范围内");
					}
				}
			}
		}
	}

	private void CustOrPurs(String caller, Integer ma_id) {
		SqlRowList rs0;
		// 产品编号不能为外购或客供的物料
		if (baseDao.isDBSetting(caller, "unAllowedCustOrPurc")) {
			rs0 = baseDao.queryForRowSet("select ma_code from make left join product on pr_code=ma_prodcode where ma_id=" + ma_id
					+ " and pr_manutype in('PURCHASE','CUSTOFFER')");
			if (rs0.next()) {
				BaseUtil.showError("产品料号不能为[外购或者客供]物料");
			}
		}
		// 产品编号不能为虚拟件
		if (baseDao.isDBSetting(caller, "unAllowedVirtual")) {
			rs0 = baseDao.queryForRowSet("select ma_code from make left join product on pr_code=ma_prodcode where ma_id=" + ma_id
					+ " and pr_supplytype='VIRTUAL'");
			if (rs0.next()) {
				BaseUtil.showError("产品料号不能为虚拟件");
			}
		}
	}

	@Override
	public void updateCraftById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Make", "ma_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "ma_id", store.get("ma_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public Map<String, String> statistics(String param) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(param);
		StringBuffer condition = new StringBuffer("");
		condition.append("mc_wccode=\'" + store.get("mc_wccode") + "\' and mc_actbegindate between ("
				+ DateUtil.parseDateToOracleString(Constant.YMD, store.get("mc_actbegindate_from").toString()) + ") and ("
				+ DateUtil.parseDateToOracleString(Constant.YMD, store.get("mc_actbegindate_to").toString()) + ")");
		if (!(store.get("mc_linecode") == null || store.get("mc_linecode").equals(""))) {
			condition.append(" and mc_linecode=\'" + store.get("mc_linecode") + "\'");
		}
		if (!(store.get("mc_prodcode") == null || store.get("mc_prodcode").equals(""))) {
			condition.append(" and mc_prodcode=\'" + store.get("mc_prodcode") + "\'");
		}
		Map<String, String> map = new HashMap<String, String>();
		map = makeDao.statistics(condition.toString());
		return map;
	}

	/*
	 * 根据开工日期和提前期、提前期批量推算完工日期
	 */
	private Date getFinishDate(String begindate, int maqty, int leadtime, int plzl) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(DateUtil.parse(begindate.toString(), Constant.YMD));
		int thisleadtime = 0;
		if (leadtime > 0 || plzl > 0) {
			thisleadtime = leadtime;
			if (plzl > 1) {
				thisleadtime += Math.ceil(Double.parseDouble(Integer.toString(maqty)) / Double.parseDouble(Integer.toString(plzl)));
			}
		}
		ca.add(Calendar.DATE, thisleadtime);
		Date enddate = ca.getTime();
		if (enddate.compareTo(new Date()) < 0) {
			enddate = new Date();
		}
		return enddate;
	}

	@Override
	public void updateMaStyle(int id, String value, String caller) {
		// 判断单据是否已经批准或已提交，不允许更新
		Object ob = baseDao.getFieldDataByCondition("Make", "ma_checkstatus", "ma_id=" + id
				+ " and ma_checkstatuscode in('APPROVE','COMMITED')");
		if (ob != null) {// 已批准,已提交
			BaseUtil.showError("单据" + ob.toString() + "不允许更新流程类型");
		}
		baseDao.updateByCondition("Make", "ma_style='" + value + "'", "ma_id=" + id);
		// 记录操作
		baseDao.logger.others("更新流程类型", "msg.updateSuccess", caller, "ma_id", id);
	}

	@Override
	public void updateShiPAddress(Integer id, String address, String caller) {
		if (address != null && !"".equals(address)) {
			baseDao.updateByCondition("Make", "ma_shipaddresscode='" + address + "'", "ma_id =" + id);
			// 记录操作
			baseDao.logger.others("更新委外交货地点", "msg.updateSuccess", caller, "ma_id", id);
		} else {
			BaseUtil.showError("交货地点不能为空");
		}
	}

	@Override
	public void openMrp(int id, String caller) {
		// ma_statuscode<>'FINISH'才允许操作.
		Object ob = baseDao.getFieldDataByCondition("make left join makeMaterial on ma_id=mm_maid", "ma_statuscode", "mm_id=" + id);
		if (ob == null) {
			BaseUtil.showError("工单用料表该明细行不存在!");
		} else if (ob.toString().equals("FINISH")) {
			BaseUtil.showError("工单已经结案，不允许操作!");
		}
		SqlRowList rs = baseDao.queryForRowSet("select mm_maid ,mm_detno from makeMaterial where mm_id=" + id + " and mm_mrpclosed<>0");
		if (rs.next()) {
			String sql = "update makeMaterial set mm_mrpclosed='0' where mm_id=" + id;
			baseDao.execute(sql);
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.openMrp"), BaseUtil
					.getLocalMessage("msg.openMrpSuccess") + "序号" + rs.getString("mm_detno"), caller + "|ma_id=" + rs.getString("mm_maid")));
		} else {
			BaseUtil.showError("该明细行已打开MRP!");
		}
	}

	@Override
	public void closeMrp(int id, String caller) {
		// ma_statuscode<>'FINISH'才允许操作.
		Object ob = baseDao.getFieldDataByCondition("make left join makeMaterial on ma_id=mm_maid", "ma_statuscode", "mm_id=" + id);
		if (ob == null) {
			BaseUtil.showError("工单用料表该明细行不存在!");
		} else if (ob.toString().equals("FINISH")) {
			BaseUtil.showError("工单已经结案，不允许操作!");
		}
		SqlRowList rs = baseDao.queryForRowSet("select mm_maid ,mm_detno from makeMaterial where mm_id=" + id
				+ " and NVL(mm_mrpclosed,0)=0");
		if (rs.next()) {
			String sql = "update makeMaterial set mm_mrpclosed='-1' where mm_id=" + id;
			baseDao.execute(sql);
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.closeMrp"), BaseUtil
					.getLocalMessage("msg.closeMrpSuccess") + "序号" + rs.getString("mm_detno"), caller + "|ma_id=" + rs.getString("mm_maid")));
		} else {
			BaseUtil.showError("该明细行已关闭MRP!");
		}
	}

	@Override
	public List<Map<String, Object>> getPastBom(Long ma_id, String caller) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select mm_id,mm_maid,mm_detno,mm_prodcode,pr_detail,pr_spec,mm_oneuseqty,mm_qty from makematerial left join product on pr_code=mm_prodcode where mm_maid=? and mm_materialstatus='JUMP' and nvl(mm_bomid,0)>0",
						ma_id);
		if (rs.next()) {
			return rs.getResultList();
		} else {
			BaseUtil.showError("制造用料中不存在跳层BOM");
		}
		return null;
	}

	@Override
	@Transactional
	public void disableBomPast(Long mm_id, String caller) {
		SqlRowList rs = baseDao.queryForRowSet(
				"select mm_bomid,mm_id,mm_detno,mm_maid,mm_prodcode,ma_statuscode from make left join makematerial on mm_maid=ma_id "
						+ " where mm_id=? and nvl(mm_bomid,0)>0 and mm_materialstatus='JUMP'", mm_id);
		if (rs.next()) {
			if ("FINISH".equals(rs.getString("ma_statuscode"))) {
				BaseUtil.showError("工单已经结案，不允许操作!");
			}
			// 判断子件的已转+已领数量
			checkTurnQty(rs.getInt("mm_bomid"), rs.getInt("mm_detno"), rs.getInt("mm_maid"));
			confirmdisableBomPast(rs.getInt("mm_maid"), rs.getInt("mm_bomid"));
			// 更新跳层，记录日志
			baseDao.execute("update makematerial set mm_materialstatus='' where mm_id=?", mm_id);
			baseDao.logger.others("工单取消跳层", "序号：" + rs.getInt("mm_detno") + ",物料：" + rs.getString("mm_prodcode") + "取消跳层", caller, "ma_id",
					rs.getInt("mm_maid"));
		} else {
			BaseUtil.showError("该用料不是跳层BOM");
		}
	}

	private void checkTurnQty(int bo_id, int detno, int mm_maid) {
		// 加循环判断已领数+已转数大于0
		SqlRowList rs0 = baseDao.queryForRowSet("select count(1)cn,wm_concat(mm_detno)detno from makematerial where mm_maid=" + mm_maid
				+ " and mm_topbomid=" + bo_id + " and nvl(mm_havegetqty,0)+NVL(mm_totaluseqty,0)>0 and rownum<20");
		if (rs0.next() && rs0.getInt("cn") > 0) {
			BaseUtil.showError("物料展开子件的已领数+已转数大于0，限制提交!用料表序号[" + rs0.getString("detno") + "]");
		}
		rs0 = baseDao.queryForRowSet("select mm_bomid from makematerial where mm_maid=" + mm_maid + " and mm_topbomid=" + bo_id
				+ " and nvl(mm_bomid,0)>0");
		while (rs0.next()) {
			checkTurnQty(rs0.getInt("mm_bomid"), detno, mm_maid);
		}
	}

	private void confirmdisableBomPast(int ma_id, int bo_id) {
		baseDao.execute("update makematerial set mm_oneuseqty=0 ,mm_qty=0,mm_remark=mm_remark||'取消BOM跳层'" + " where mm_maid='" + ma_id
				+ "' and mm_topbomid='" + bo_id + "'");
		SqlRowList rs0 = baseDao.queryForRowSet("select mm_bomid from makematerial where mm_maid=" + ma_id + " and mm_topbomid=" + bo_id
				+ " and nvl(mm_bomid,0)>0 and mm_materialstatus='JUMP'");
		while (rs0.next()) {
			confirmdisableBomPast(ma_id, rs0.getInt("mm_bomid"));
		}
	}

	private void checkWorkCenter(int ma_id) {
		Object str = baseDao.getFieldDataByCondition("make left join workcenter on ma_wccode=wc_code", "wc_capatype", "ma_id=" + ma_id);
		if (str != null) {
			if (str.equals("-1")) {
				str = baseDao.getFieldDataByCondition("make", "ma_craftcode", "ma_id=" + ma_id);
				if (str == null) {
					BaseUtil.showError("工作中心【是否计件】选项为是时必须选择工艺路线编号");
				}
			}
		}
	}
	private void updateSd_tomakeqty(int ma_id){
		SqlRowList field = baseDao.queryForRowSet("select ma_prodcode,ma_salecode,ma_saledetno from make where ma_id="+ma_id);
		if(field.next()){
			String ma_prodcode = field.getString("ma_prodcode");
			String ma_salecode = field.getString("ma_salecode");
			String ma_saledetno = field.getString("ma_saledetno");
			baseDao.execute(" update SALEDETAIL set sd_tomakeqty=(select sum(case when ma_statuscode='FINISH' then nvl(ma_madeqty,0) else ma_qty end) qty"
	          +" from make ma where ma_prodcode=? and ma_salecode=? and ma_saledetno=? ) where sd_code=? and sd_detno=?",ma_prodcode,ma_salecode,ma_saledetno,ma_salecode,ma_saledetno);
			baseDao.execute(" update SALEFORECASTDETAIL set sd_tomakeqty=(select sum(case when ma_statuscode='FINISH' then nvl(ma_madeqty,0) else ma_qty end) qty"
			          +" from make ma where ma_prodcode=? and ma_salecode=? and ma_saledetno=? ) where sd_code=? and sd_detno=?",ma_prodcode,ma_salecode,ma_saledetno,ma_salecode,ma_saledetno);
		}
	}
	
	/**
	 * 批量核销
	 */
	@Override
	public String vastWriteoff(String data, String caller) {
		List<Map<Object, Object>> list = BaseUtil.parseGridStoreToMaps(data);
		Object id = null;
		Object bi_writeoffreason = null;
		for (Map<Object, Object> m : list) {
			id = m.get("bi_id");
			bi_writeoffreason = m.get("bi_writeoffreason");
			if (m.containsKey("bi_writeoffreason") && !StringUtil.hasText(m.get("bi_writeoffreason"))) {
				BaseUtil.showError("核销原因没有填写,核销失败!");
			}
			// 核销原因
			if (StringUtil.hasText(m.get("bi_writeoffreason"))) {
				baseDao.updateByCondition("barcodeio", "bi_writeoffreason='" + bi_writeoffreason + "', bi_ifwriteoff = -1", "bi_id=" + id);
			}
			// 记录操作
			baseDao.logger.others("特殊出库批量核销", "成功", "barcodeio", "bi_id=",id);
		}
		return "核销成功";
	}

	/**
	 * 制造单批量批准
	 */
	@Override
	public void vastApproveMake(String data, String caller) {List<Map<Object, Object>> list = BaseUtil.parseGridStoreToMaps(data);
	int id = 0;
	String code;
	StringBuffer str = new StringBuffer();
	for (Map<Object, Object> m : list) {
		id = Integer.parseInt(m.get("ma_id").toString());	
		code = m.get("ma_code").toString();
		SqlRowList rs = baseDao.queryForRowSet("select ma_checkstatuscode,ma_statuscode,ma_tasktype from make where ma_id=?",id);
		if(rs.next()){
			if(("APPROVE").equals(rs.getString("ma_checkstatuscode"))){
				BaseUtil.showError("只能批准未批准的制造单,制造单号"+code);
			}else if(!("MAKE").equals(rs.getString("ma_tasktype"))){
				BaseUtil.showError("只能批量批准制造单,单号"+code);
			}else if (!("AUDITED").equals(rs.getString("ma_statuscode"))) {
				BaseUtil.showError("只能批量批准已审核的制造单,制造单号"+code);
			}
			str.append(id+",");
		}else{
			BaseUtil.showError("制造单号["+code+"]不存在或已删除");
		}
	}
	String str1 = str.substring(0,str.length() - 1);
	baseDao.updateByCondition("Make", "ma_checkstatuscode='APPROVE',ma_checkstatus='" + BaseUtil.getLocalMessage("APPROVE")+"'","ma_id in ("+str1+")");
	String[] ids = str1.split(",");
	for (String s : ids) {
		baseDao.logger.others("批准成功", "批量批准成功", "Make!Base", "ma_id",s);
	}}
	
	/**
	 * 制造单结案==》生成退料单
	 */
	@Override
	public String createReturnMake(String data, String caller) {
		Timestamp time = Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS));
		StringBuffer sb = new StringBuffer();
		StringBuffer condition = new StringBuffer();
		String INSERT_PRODIO = "INSERT INTO ProdInOut(pi_id, pi_inoutno, pi_date, pi_class, pi_invostatus, pi_invostatuscode,"
				+ "pi_recordman, pi_recorddate, pi_status, pi_statuscode,pi_updatedate,pi_updateman,pi_printstatuscode,pi_printstatus)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		String INSERT_DETAIL = "INSERT INTO ProdIODetail(pd_id, pd_piid, pd_inoutno, pd_piclass, pd_pdno, pd_status,pd_auditstatus,pd_prodcode, pd_inqty,"
				+ "pd_ordercode, pd_orderdetno, pd_plancode, pd_wccode, pd_orderid, pd_prodid, pd_whcode) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		List<Map<Object,Object>> list = BaseUtil.parseGridStoreToMaps(data);
		List<Integer> ma_ids = new ArrayList<Integer>();
		for(Map<Object,Object> map : list){
			int id = (int) map.get("ma_id");
			ma_ids.add(id);
		}
		String ids = StringUtils.join(ma_ids.toArray(), ",");
		//计算可退料数
		calOnlineQty(ids, caller);
		//制造单逐个转退料单
		StringBuffer sql_pull=new StringBuffer();
		sql_pull.append("select ma_code,(nvl(mm_thisqty,0)+nvl(mp_thisqty,0)) mm_thisqty,mm_detno,mm_whcode,pr_id,mm_prodcode from make left join makematerial on ma_id=mm_maid left join product on mm_prodcode = pr_code left join makematerialreplace on mp_mmid =mm_id where ma_checkstatuscode = 'APPROVE'");
		if(baseDao.isDBSetting("Make!Base", "PullbalanceNotReturn")){
			sql_pull.append(" and pr_supplytype <> 'PULL'");
			condition.append(" pr_supplytype <> 'PULL' and ");
		}
		if((baseDao.isDBSetting("Make!Base", "balanceNotReturn"))&&(baseDao.isDBSetting("Make!Base", "autoEndBalanceNotReturn"))){
			sb.append("所有物料都不用生成退料!");
			return sb.toString();
		}
		int pi_id =0;
		int pd_pdno =0;
		String pi_inoutno = "";
		Object mm_thisqty = baseDao.getFieldDataByCondition("make left join makematerial on ma_id= mm_maid left join product on pr_code= mm_prodcode left join makematerialreplace on mp_mmid =mm_id", "sum(nvl(mm_thisqty,0)+nvl(mp_thisqty,0)) mm_thisqty", condition +"ma_id in ("+ids+") and ma_checkstatuscode = 'APPROVE' group by ma_code");
		SqlRowList queryForRowSet = baseDao.queryForRowSet("SELECT SUM(NVL(mm_thisqty,0)+NVL(mp_thisqty,0)) mm_thisqty FROM make LEFT JOIN makematerial ON ma_id= mm_maid "
						+"LEFT JOIN product ON pr_code= mm_prodcode LEFT JOIN makematerialreplace ON mp_mmid =mm_id WHERE ma_id IN ("+ids+") "
						+ "AND ma_checkstatuscode = 'APPROVE'GROUP BY ma_code");
		if(queryForRowSet.next()){
			if(queryForRowSet.getInt("mm_thisqty")==0){
				return "勾选的制造单不需要退料!";
			}else{
				pi_id = baseDao.getSeqId("PRODINOUT_SEQ");
				pi_inoutno = baseDao.sGetMaxNumber("prodinout", 2);
				baseDao.execute(INSERT_PRODIO,new Object[] { pi_id, pi_inoutno, time, "生产退料单", BaseUtil.getLocalMessage("ENTERING"), "ENTERING",
						SystemSession.getUser().getEm_name(), time,BaseUtil.getLocalMessage("UNPOST"), "UNPOST", time,
						SystemSession.getUser().getEm_name(), "UNPRINT", BaseUtil.getLocalMessage("UNPRINT") });
				for(Map<Object,Object> map : list){
					SqlRowList rowSet = baseDao.queryForRowSet(sql_pull.append(" and ma_id="+map.get("ma_id")).append(" AND (NVL(mm_thisqty,0)>0 or nvl(mp_thisqty,0) >0)").toString());
					while(rowSet.next()){
						pd_pdno = pd_pdno+1;
						int pd_id = baseDao.getSeqId("PRODIODETAIL_SEQ");
						baseDao.execute(INSERT_DETAIL,new Object[] { pd_id, pi_id, pi_inoutno, "生产退料单", pd_pdno, 0, "ENTERING", rowSet.getString("mm_prodcode"), rowSet.getInt("mm_thisqty"),
								rowSet.getString("ma_code"), rowSet.getString("mm_detno"), null, null, null, rowSet.getInt("pr_id"), rowSet.getString("mm_whcode") });
					}
				}
			}
		}else{
			return "制造单未批准，勾选的制造单不需要退料!";
		}
		return "转入成功，退料单号:<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + pi_id
				+ "&gridCondition=pd_piidIS" + pi_id + "&whoami=ProdInOut!Make!Return')\">" + pi_inoutno + "</a>&nbsp";
		
	}
	
	//更新备损数和需求数
	private void setMM_balance(Object code){
		//获取工单数
		Object ma_vendcode = baseDao.getFieldDataByCondition("make", "ma_vendcode", "ma_code='"+code+"'");
		SqlRowList list = baseDao.queryForRowSet("select mm_prodcode,nvl(pr_precision,0) pr_precision,nvl(ma_qty,0) ma_qty,nvl(mm_oneuseqty,0) mm_oneuseqty ,nvl((select vpl_oslossrate from vendprodloss where vpl_vendcode=? and mm_prodcode =vpl_code ),0) mm_oslossrate"+
						" from make left join makematerial on  ma_id = mm_maid left join product on pr_code =mm_prodcode where ma_code=?",ma_vendcode,code);
		while(list.next()){
			float oslossrate = list.getFloat("mm_oslossrate");
			String prodcode = list.getString("mm_prodcode");
			int maqty = list.getInt("ma_qty");
			int pr_precision = list.getInt("pr_precision");
			baseDao.execute("update makematerial set mm_qty = round((?*mm_oneuseqty*(100+?)/100)+0.4999*power(0.1,?), ?) where mm_prodcode=? and mm_code=?",maqty,oslossrate,pr_precision,pr_precision,prodcode,code);
			baseDao.execute("update makematerial set mm_balance=mm_qty-?*mm_oneuseqty where mm_prodcode=? and mm_code=?",maqty,prodcode,code);
		}
	}
}
