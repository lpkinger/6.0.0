package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.MakeScrapService;

@Service
public class MakeScrapServiceImpl implements MakeScrapService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveMakeScrap(String formStore, String gridStore, String caller) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("MakeScrap", "ms_code='" + store.get("ms_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		checkFreezeMonth(store.get("ms_date"));
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 保存Dispatch
		String formSql = SqlUtil.getInsertSqlByMap(store, "MakeScrap");
		baseDao.execute(formSql);
		// //保存DispatchDetail
		Object[] md_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			md_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				md_id[i] = baseDao.getSeqId("MakeScrapdetail_SEQ");
			}
		} else {
			md_id[0] = baseDao.getSeqId("MakeScrapdetail_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "MakeScrapdetail", "md_id", md_id);
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "ms_id", store.get("ms_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });

	}

	@Override
	public void updateMakeScrapById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("MakeScrap", "ms_statuscode", "ms_id=" + store.get("ms_id"));
		StateAssert.updateOnlyEntering(status);
		checkFreezeMonth(store.get("ms_date"));
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 修改Dispatch
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "MakeScrap", "ms_id");
		baseDao.execute(formSql);
		// 修改DispatchDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "MakeScrapdetail", "md_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("md_id") == null || s.get("md_id").equals("") || s.get("md_id").equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("MakeScrapdetail_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "MakeScrapdetail", new String[] { "md_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "ms_id", store.get("ms_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });

	}

	@Override
	public void deleteMakeScrap(int ms_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("MakeScrap", "ms_statuscode", "ms_id=" + ms_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { ms_id });
		// 删除主表
		baseDao.deleteById("MakeScrap", "ms_id", ms_id);
		// 删除明细表
		baseDao.deleteById("MakeScrapdetail", "md_msid", ms_id);
		// 记录操作
		baseDao.logger.delete(caller, "ms_id", ms_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { ms_id });

	}

	@Override
	public void auditMakeScrap(int ms_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("MakeScrap", new String[] { "ms_statuscode", "ms_indate","ms_date" }, "ms_id=" + ms_id);
		StateAssert.auditOnlyCommited(status[0]);
		checkFreezeMonth(status[2]);
		// 账期和工单状态检查
		ScrapCheckAll(ms_id, caller);
		ScrapCheck_scrapqty(ms_id);
		copcheck(ms_id);
		checkMakeMaterial(ms_id);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { ms_id });
		baseDao.updateByCondition("MakeScrapdetail", "md_status=99", "md_msid=" + ms_id);
		List<Object[]> objects = baseDao.getFieldsDatasByCondition("MakeScrapdetail left join make on ma_code=md_mmcode ", new String[] {
				"ma_id", "md_mmdetno", "md_qty", "md_prodcode" }, " md_msid=" + ms_id);
		for (Object[] object : objects) {
			baseDao.updateByCondition("Makematerial ", "mm_scrapqty =nvl(mm_scrapqty,0)+" + object[2], "mm_detno=" + object[1]
					+ " and mm_maid=" + object[0]);
			baseDao.updateByCondition("Makematerial ", "mm_repscrapqty =nvl(mm_repscrapqty,0)+" + object[2], "mm_detno=" + object[1]
					+ " and mm_maid=" + object[0] + " and mm_prodcode<>'" + object[3] + "'");
			baseDao.updateByCondition("Makematerialreplace ", "mp_scrapqty =nvl(mp_scrapqty,0)+" + object[2], " mp_maid=" + object[0]
					+ " and mp_mmdetno=" + object[1] + " and mp_prodcode='" + object[3] + "'");
		}
		// 执行审核操作
		baseDao.audit("MakeScrap", "ms_id=" + ms_id, "ms_status", "ms_statuscode", "ms_auditdate", "ms_auditman");
		// List<Object> objects =
		// baseDao.getFieldDatasByCondition("MakeScrapdetail", "md_code",
		// condition)

		// 记录操作
		baseDao.logger.audit(caller, "ms_id", ms_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { ms_id });

	}

	@Override
	public void resAuditMakeScrap(int ms_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("MakeScrap", new String[] { "ms_statuscode", "ms_indate","ms_date" }, "ms_id=" + ms_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		checkFreezeMonth(status[2]);
		baseDao.execute("update MakeMaterial set mm_turnaddqty=(select sum(nvl(pd_outqty,0)) from prodiodetail,prodinout "
				+ "where pd_piid=pi_id  and pi_statuscode<>'DELETE' and pd_status=0 and "
				+ "pd_ordercode=mm_code and pd_orderdetno=mm_detno  and pd_piclass in ('生产补料单', '委外补料单')) where mm_code in (select md_mmcode from MakeScrapDetail where md_msid="
				+ ms_id + ")");
		SqlRowList rs = baseDao
				.queryForRowSet("select md_detno from MakeScrapDetail left join makematerial on mm_code=md_mmcode and md_mmdetno=mm_detno left join make on mm_maid=ma_id where md_msid='"
						+ ms_id
						+ "' and mm_addqty+NVL(mm_turnaddqty,0)>nvl(mm_scrapqty,0)+nvl(mm_returnmqty,0)-md_qty-nvl(mm_balance,0) and nvl(mm_scrapqty,0)+nvl(mm_returnmqty,0)-md_qty-nvl(mm_balance,0)>0 ");
		if (rs.next()) {
			BaseUtil.showError("序号【" + rs.getString("md_detno") + "】报废数已生成补料单，不能反审核报废单");
		}
		// 账期和工单状态检查
		ScrapCheckAll(ms_id, caller);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { ms_id });
		baseDao.updateByCondition("MakeScrapdetail", "md_status=0", "md_msid=" + ms_id);
		List<Object[]> objects = baseDao.getFieldsDatasByCondition("MakeScrapdetail left join make on ma_code=md_mmcode", new String[] {
				"ma_id", "md_mmdetno", "md_qty", "md_prodcode" }, " md_msid=" + ms_id);
		for (Object[] object : objects) {
			baseDao.updateByCondition("Makematerial ", "mm_scrapqty =nvl(mm_scrapqty,0)-" + object[2], "mm_detno=" + object[1]
					+ " and mm_maid=" + object[0]);
			baseDao.updateByCondition("Makematerial ", "mm_repscrapqty =nvl(mm_repscrapqty,0)-" + object[2], "mm_detno=" + object[1]
					+ " and mm_maid=" + object[0] + " and mm_prodcode<>'" + object[3] + "'");
			baseDao.updateByCondition("Makematerialreplace ", "mp_scrapqty =nvl(mp_scrapqty,0)-" + object[2], " mp_maid=" + object[0]
					+ " and mp_mmdetno=" + object[1] + " and mp_prodcode='" + object[3] + "'");
		}
		// 执行反审核操作
		baseDao.updateByCondition("MakeScrap", "ms_statuscode='ENTERING',ms_status='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"ms_id=" + ms_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ms_id", ms_id);
	}

	@Override
	public void submitMakeScrap(int ms_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("MakeScrap", new String[] { "ms_statuscode", "ms_indate","ms_date" }, "ms_id=" + ms_id);
		StateAssert.submitOnlyEntering(status[0]);
		checkFreezeMonth(status[2]);
		// 账期和工单状态检查
		ScrapCheckAll(ms_id, caller);
		ScrapCheck_scrapqty(ms_id);
		copcheck(ms_id);
		checkMakeMaterial(ms_id);
		String sql = null;
		Object enddate = baseDao.getFieldDataByCondition("PeriodsDetail", "to_char(pd_enddate,'yyyymmdd')",
				"pd_code='MONTH-P' and pd_detno=to_char(to_date('" + status[1] + "','yyyy-mm-dd hh24:mi:ss'), 'yyyymm')");
		if (enddate != null && !enddate.equals("")) {
			sql = "merge into makescrapdetail using(select * from (select pd_prodcode,pd_price,ROW_NUMBER() OVER (partition by pd_prodcode order by pi_id desc) AS rn "
					+ "from prodiodetail left join prodinout on  pi_id=pd_piid where pd_prodcode in (select md_prodcode from makescrapdetail where md_msid="
					+ ms_id
					+ ") "
					+ "and pd_status=99 and nvl(pd_price,0) > 0 and nvl(pd_inqty,0) > 0  and to_char(pi_date,'yyyymmdd')<='"
					+ enddate.toString()
					+ "' ) where rn<2 ) "
					+ "src on (md_prodcode=src.pd_prodcode)  when matched then update set md_price=src.pd_price where md_msid=" + ms_id;
			baseDao.execute(sql);
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { ms_id });
		// 执行提交操作
		baseDao.submit("MakeScrap", "ms_id=" + ms_id, "ms_status", "ms_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "ms_id", ms_id);
		baseDao.execute("merge into makescrapdetail using (select pd_prodcode,pd_ordercode,avg(pd_price) as price from prodiodetail where pd_ordercode in (select md_mmcode from makescrapdetail where md_msid="
				+ ms_id
				+ ") and pd_status=99 and pd_piclass in ('生产领料单','委外领料单') group by pd_prodcode,pd_ordercode)A on (md_msid="+ms_id+" and md_mmcode=pd_ordercode and md_prodcode=pd_prodcode) when matched then update set md_price=NVL(price,0) where md_msid="
				+ ms_id + " and NVL(md_price,0)=0");
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { ms_id });
	}

	@Override
	public void resSubmitMakeScrap(int ms_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MakeScrap", "ms_statuscode", "ms_id=" + ms_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { ms_id });
		// 执行反提交操作
		baseDao.updateByCondition("MakeScrap", "ms_statuscode='ENTERING',ms_status='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"ms_id=" + ms_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ms_id", ms_id);
		handlerService.afterResSubmit(caller, new Object[] { ms_id });
	}

	@Override
	public String[] printMakeScrap(int ms_id, String caller, String reportName, String condition) {
		// 只能打印审核后的单据!
		/*
		 * Object status = baseDao.getFieldDataByCondition("MakeScrap",
		 * "ms_statuscode", "ms_id=" + ms_id); if (!status.equals("AUDITED") &&
		 * !status.equals("PARTRECEIVED") && !status.equals("RECEIVED") &&
		 * !status.equals("NULLIFIED")) {
		 * BaseUtil.showError(BaseUtil.getLocalMessage("common.print_onlyAudit",
		 * language)); }
		 */
		copcheck(ms_id);
		checkMakeMaterial(ms_id);
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, new Object[] { ms_id });
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 修改打印状态
		baseDao.updateByCondition("MakeScrap", "ms_printstatuscode='PRINTED',ms_printstatus='" + BaseUtil.getLocalMessage("PRINTED") + "'",
				"ms_id=" + ms_id);
		// 记录打印次数
		baseDao.updateByCondition("MakeScrap", "ms_count=nvl(ms_count,0)+1", "ms_id=" + ms_id);
		// 记录操作
		baseDao.logger.print(caller, "ms_id", ms_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, new Object[] { ms_id });
		return keys;

	}

	public void ScrapCheckAll(int ms_id, String caller) {
		// 判断工单状态是否已审核
		SqlRowList rs = baseDao.queryForRowSet("select md_detno,md_mmcode,ma_status from makescrapdetail,make  where md_msid=" + ms_id
				+ " and md_mmcode=ma_code and ma_statuscode<>'AUDITED' ");
		if (rs.next()) {
			BaseUtil.showError("制造单" + rs.getString("md_mmcode") + "状态[" + rs.getString("ma_status") + "],只能操作已审核状态的制造单");
		}
	}

	/**
	 * 判断报废数是否大于结存可报废数
	 * 
	 * @param msid
	 */
	public void ScrapCheck_scrapqty(Integer ms_id) {
		String sql = "";
		String err = "";
		sql = "UPDATE MakeMaterial set mm_allscrapqty=nvl(mm_scrapqty,0)+nvl((select sum(md_qty) from MakeScrapDetail,MakeScrap where ms_id=md_msid and (ms_statuscode='COMMITED' or ms_id='"
				+ ms_id
				+ "') and mm_code=md_mmcode and md_mmdetno=mm_detno),0) "
				+ " WHERE (mm_code,mm_detno) in (select md_mmcode as mm_code,md_mmdetno as mm_detno from MakeScrapDetail where md_msid='"
				+ ms_id + "')";
		baseDao.execute(sql);

		if(baseDao.isDBSetting("usingMakeCraft")){
			sql = "select md_detno,md_mmcode,md_prodcode from MakeScrapDetail left join makematerial on md_mmcode=mm_code and md_mmdetno=mm_detno left join make on mm_maid=ma_id where md_msid="
					+ ms_id + "  and round(nvl(mm_allscrapqty,0),4)>round(nvl(mm_havegetqty,0)+nvl(mm_stepinqty,0)-nvl(mm_clashqty,0) -NVL(mm_scrapqty,0),4) ";
		}else{
			sql = "select md_detno,md_mmcode,md_prodcode from MakeScrapDetail left join makematerial on md_mmcode=mm_code and md_mmdetno=mm_detno left join make on mm_maid=ma_id where md_msid="
					+ ms_id ;
			if(baseDao.isDBSetting("Make!Base", "allowChangeAfterCom")){
				sql = sql+ "  and round(nvl(mm_allscrapqty,0),4) > round(nvl(mm_havegetqty,0)-nvl(mm_backqty,0),4) ";
			}else{
				sql += "  and round(nvl(mm_allscrapqty,0),4)>round(nvl(mm_havegetqty,0)-mm_oneuseqty*ma_madeqty,4) ";
			}
		}
		SqlRowList rs = baseDao.queryForRowSet(sql);
		while (rs.next()) {
			err = err + "," + rs.getString("md_detno");
		}
		if (!err.equals("")) {
			BaseUtil.showError("序号:" + err.substring(1) + "报废数大于结存数");
		}
		sql = "SELECT count(1) detno ,wm_concat(md_detno) as detnostr FROM MakeScrapDetail WHERE  md_msid='" + ms_id + "' and md_qty<=0  ";
		rs = baseDao.queryForRowSet(sql);
		if (rs.next()) {
			if (rs.getInt("detno") > 0) {
				BaseUtil.showError("序号：" + rs.getString("detnostr") + "报废数小于或等于0，不能提交");
			}
		}
	}

	private void copcheck(int ms_id) {
		if (baseDao.isDBSetting("CopCheck")) {
			// 生产报废单：明细行制造单所属公司与当前单所属公司必须一致，可在提交、打印、审核、过账等操作前配置
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(md_detno) from MakeScrapDetail left join MakeScrap on md_msid=ms_id left join Make on md_mmcode=ma_code where ms_id=? and nvl(ma_cop,' ')<>nvl(ms_cop,' ') ",
							String.class, ms_id);
			if (dets != null) {
				BaseUtil.showError("明细行制造单所属公司与当前单所属公司不一致，不允许进行当前操作!行号：" + dets);
			}
		}
	}

	// 验证从表中的工单的序号是否有效
	private void checkMakeMaterial(int ms_id) {
		String err = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(md_detno) from MakeScrapdetail where md_msid=? and not exists (select 1 from make left join makematerial on ma_id=mm_maid where md_mmcode=ma_code and md_mmdetno=mm_detno)",
						String.class, ms_id);
		if (err != null) {
			BaseUtil.showError("序号:" + err.toString() + " 无对应工单和序号，不允许进行当前操作");
		}
	}

	void checkFreezeMonth(Object pidate) {
		boolean bool = baseDao.checkIf("PeriodsDetail", "pd_code='MONTH-P' and pd_status=99 and pd_detno=to_char(to_date('" + pidate
				+ "','yyyy-mm-dd hh24:mi:ss'), 'yyyymm')");
		if (bool) {
			BaseUtil.showError("单据日期所属期间已结账，不允许进行当前操作!");
		}
		String freeze = baseDao.getDBSetting("freezeMonth");
		if (freeze != null && !freeze.equals("")) {
			if (Integer.parseInt(freeze) == DateUtil.getYearmonth(pidate.toString())) {
				BaseUtil.showError("单据日期所属期间已冻结，不允许进行当前操作!");
			}
		}
	}

}
