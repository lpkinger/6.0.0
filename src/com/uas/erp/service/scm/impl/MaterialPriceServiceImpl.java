package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.HashSet;
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

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.MaterialPriceService;

@Service("materialPriceService")
public class MaterialPriceServiceImpl implements MaterialPriceService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveMaterialPrice(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		String currency = baseDao.getDBSetting("sys", "defaultCurrency");
		Object code = baseDao.sGetMaxNumber("PurchasePrice", 2);
		store.put("pp_code", code);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("PurchasePrice", "pp_code='" + store.get("pp_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PurchasePrice", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存Detail
		List<String> gridSql = SqlUtil.getInsertOrUpdateSql(gstore, "PurchasePriceDetail", "ppd_id");
		baseDao.execute(gridSql);
		Object pp_id = store.get("pp_id");
		useVendDefaultCurrency(caller, pp_id);
		defaultTax(caller, pp_id);
		defaultEndDate(caller, pp_id);
		getPurcRatePrice(pp_id);
		if("MaterialPrice".equals(caller)){
			SqlRowList rs = baseDao.queryForRowSet("select ppd_id from purchasepricedetail where ppd_currency is null and ppd_ppid="+store.get("pp_id"));
			while(rs.next()){
				baseDao.updateByCondition("PurchasePriceDetail", "ppd_currency='"+currency+"'", "ppd_id="+rs.getInt("ppd_id"));
			}
		}
		baseDao.execute("update PurchasePricedetail set ppd_code=(select pp_code from PurchasePrice where ppd_ppid=pp_id) where ppd_ppid="
				+ store.get("pp_id") + " and not exists (select 1 from PurchasePrice where ppd_code=pp_code)");
		baseDao.logger.save(caller, "pp_id", pp_id);
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
	}

	@Override
	public void deleteMaterialPrice(int pp_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("PurchasePrice", "pp_statuscode", "pp_id=" + pp_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { pp_id });
		// 删除MaterialPrice
		baseDao.deleteById("PurchasePrice", "pp_id", pp_id);
		// 删除MaterialPriceDetail
		baseDao.deleteById("PurchasePricedetail", "ppd_ppid", pp_id);
		// 记录操作
		baseDao.logger.delete(caller, "pp_id", pp_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { pp_id });
	}

	@Override
	public void updateMaterialPriceById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		String currency = baseDao.getDBSetting("sys", "defaultCurrency");
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("PurchasePrice", "pp_statuscode", "pp_id=" + store.get("pp_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PurchasePrice", "pp_id");
		baseDao.execute(formSql);
		// 修改Detail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "PurchasePriceDetail", "ppd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("ppd_id") == null || s.get("ppd_id").equals("") || s.get("ppd_id").equals("0")
					|| Integer.parseInt(s.get("ppd_id").toString()) == 0) {// 新添加的数据，id不存在
				String sql = SqlUtil.getInsertSql(s, "PurchasePriceDetail", "ppd_id");
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		Object pp_id = store.get("pp_id");
		useVendDefaultCurrency(caller, pp_id);
		defaultTax(caller, pp_id);
		defaultEndDate(caller, pp_id);
		getPurcRatePrice(pp_id);
		if("MaterialPrice".equals(caller)){
			SqlRowList rs = baseDao.queryForRowSet("select ppd_id from purchasepricedetail where ppd_currency is null and ppd_ppid="+store.get("pp_id"));
			while(rs.next()){
				baseDao.updateByCondition("PurchasePriceDetail", "ppd_currency='"+currency+"'", "ppd_id="+rs.getInt("ppd_id"));
			}
		}
		baseDao.execute("update PurchasePricedetail set ppd_code=(select pp_code from PurchasePrice where ppd_ppid=pp_id) where ppd_ppid="
				+ store.get("pp_id") + " and not exists (select 1 from PurchasePrice where ppd_code=pp_code)");
		// 记录操作
		baseDao.logger.update(caller, "pp_id", pp_id);
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
	}

	@Override
	public void printMaterialPrice(int pp_id, String caller) {
		// 执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[] { pp_id });
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "pp_id", pp_id);
		// 执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[] { pp_id });
	}

	@Override
	public void auditMaterialPrice(int pp_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("PurchasePrice", "pp_statuscode", "pp_id=" + pp_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { pp_id });
		baseDao.execute("update PurchasePricedetail set ppd_code=(select pp_code from PurchasePrice where ppd_ppid=pp_id) where ppd_ppid="
				+ pp_id + " and not exists (select 1 from PurchasePrice where ppd_code=pp_code)");
		//取最近审核的物料核价单的认定状态
		baseDao.execute("update PurchasePricedetail d set PPD_APPSTATUS=(select PPD_APPSTATUS from (select b.ppd_id,a.PPD_APPSTATUS from PurchasePricedetail b left join PurchasePricedetail a on  b.ppd_prodcode=a.ppd_prodcode and b.ppd_vendcode=a.ppd_vendcode "
						+ "left join PurchasePrice on pp_id=a.ppd_ppid   where b.ppd_ppid="+ pp_id
					+ " and pp_statuscode='AUDITED' AND a.ppd_ppid<>"+ pp_id
					+ " order by pp_auditdate desc) c where rownum<2 and d.ppd_id=c.ppd_id) WHERE nvl(PPD_APPSTATUS,' ')=' ' and PPD_PPID=" + pp_id);
		/*dyl 问题反馈2016120274
		 * 没有历史核价单，取同供应商同物料最新的已审核的物料认定单若认定结果为合格的的更新认定状态为合格
		 */
		SqlRowList rs = baseDao
				.queryForRowSet("select ppd_id,ppd_prodcode,ppd_vendcode from PurchasePricedetail  where ppd_ppid="+pp_id+" and nvl(PPD_APPSTATUS,' ')=' '");
		while (rs.next()) {
			SqlRowList rs1 = baseDao.queryForRowSet("select pa_finalresult from (select "
					+ "pa_finalresult from ProductApproval where PA_PRODCODE='"+rs.getString("ppd_prodcode")+"' and pa_providecode='"+rs.getString("ppd_vendcode")+"' "
					+ "and pa_statuscode='AUDITED' order by pa_auditdate desc) where rownum<2");
			if (rs1.next()&&"合格".equals(rs1.getString("pa_finalresult"))) {
				baseDao.updateByCondition("PurchasePricedetail","PPD_APPSTATUS='合格'","ppd_id="+rs.getInt("ppd_id"));
			}
		}
		// 执行审核操作
		baseDao.audit("PurchasePrice", "pp_id=" + pp_id, "pp_status", "pp_statuscode", "pp_auditdate", "pp_auditman");
		baseDao.updateByCondition("PurchasePriceDetail", "ppd_statuscode='VALID',ppd_status='" + BaseUtil.getLocalMessage("VALID") + "'",
				"ppd_ppid=" + pp_id);
		List<Object[]> list = baseDao.getFieldsDatasByCondition("PurchasePrice left join PurchasePriceDetail on pp_id=ppd_ppid",
				new String[] { "ppd_vendcode", "ppd_prodcode", "ppd_currency", "pp_kind" }, "ppd_ppid=" + pp_id
				+ " and ppd_statuscode = 'VALID'");// 供应商、料号、币别、定价类型
		if (!list.isEmpty()) {
			for (Object[] objs : list) {
				List<Object[]> spds = baseDao.getFieldsDatasByCondition(
						"PurchasePrice left join PurchasePriceDetail on pp_id=ppd_ppid", new String[] { "ppd_id", "pp_code", "pp_id",
						"ppd_detno" }, "ppd_vendcode='" + objs[0] + "' AND ppd_statuscode='VALID'" + " AND ppd_prodcode='"
								+ objs[1] + "' AND ppd_currency='" + objs[2] + "'" + " and ppd_ppid <> " + pp_id + " AND pp_kind='"
								+ objs[3] + "'");
				for (Object[] spd : spds) {
					baseDao.updateByCondition("PurchasePriceDetail",
							"ppd_unvaliddate=sysdate,ppd_statuscode='UNVALID',ppd_status='" + BaseUtil.getLocalMessage("UNVALID") + "'", "ppd_id=" + spd[0]);
				}
			}
		}
		// 更新物料供应商以及价格
		baseDao.execute("update product set (pr_vendcode,pr_purcprice)=(select max(ppd_vendcode),max(ppd_price * (1 - NVL(ppd_rate, 0) / (100 + NVL(ppd_rate, 0))) * cr_rate) from PurchasePricedetail,currencys where ppd_ppid="
				+ pp_id
				+ " and ppd_prodcode=pr_code and ppd_currency=cr_name) where pr_code in (select ppd_prodcode from PurchasePricedetail where ppd_ppid="
				+ pp_id + ")");
		/**
		 * @author wsy
		 * 单据编号：2017030162
		 * 物料核价单审核时更新字段'最近异动日期'为当前系统时间
		 */
		baseDao.execute("update PurchasePriceDetail set PPD_RECENTCHANGEDATE=sysdate,ppd_fromdate=sysdate where ppd_ppid="+pp_id);
		baseDao.execute("update PurchasePrice set pp_fromdate=sysdate where pp_id="+pp_id);
		// maz 物料核价单审核时，若来源pp_source为空，更新细明物料上次询价日期 17-09-26
		Object pp_source = baseDao.getFieldDataByCondition("PurchasePrice", "pp_source", "pp_id="+pp_id);
		if(pp_source==null || "".equals(pp_source)){
			baseDao.execute("update product set pr_lastinquirydate=(select pp_indate from PurchasePrice where pp_id="+pp_id+") where pr_code in (select ppd_prodcode from PurchasePriceDetail  where ppd_ppid="+pp_id+")");
		}
		// 记录操作
		baseDao.logger.audit(caller, "pp_id", pp_id);
		// 执行比例分配表的异动更新
		if (baseDao.isDBSetting("vendorRate")) {
			try {
				baseDao.callProcedure("SP_SetProdVendorRate", new Object[] { "异动更新" });
			} catch (Exception e) {
			}
		}
		// 默认更新物料资料为0或者为空的最小包装数、最小订购量、采购周期
		if (baseDao.isDBSetting("PurchasePrice", "UpdateProduct")) {
			String sqlstr = "update product set (pr_leadtime,pr_zxdhl,pr_zxbzs)=(select  max(NVL(ppd_purctime,0)),MAX(NVL(ppd_minqty,0)),max(NVL(ppd_zxbzs,0)) from PurchasePricedetail where ppd_prodcode=pr_code and ppd_ppid="
					+ pp_id
					+ ") where pr_code in (select ppd_prodcode from PurchasePricedetail where ppd_ppid="
					+ pp_id
					+ ") and  nvl(pr_leadtime,0)=0 and nvl(pr_zxdhl,0)=0 and nvl(pr_zxbzs,0)=0";
			baseDao.execute(sqlstr);
		}
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { pp_id });

	}

	@Override
	public void resAuditMaterialPrice(int pp_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("PurchasePrice", "pp_statuscode", "pp_id=" + pp_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("PurchasePrice", "pp_id=" + pp_id, "pp_status", "pp_statuscode", "pp_auditdate", "pp_auditman");
		baseDao.updateByCondition("PurchasePriceDetail", "ppd_unvaliddate=sysdate,ppd_statuscode='UNVALID',ppd_status='" + BaseUtil.getLocalMessage("UNVALID")
				+ "'", "ppd_ppid=" + pp_id);
		baseDao.logger.resAudit(caller, "pp_id", pp_id);
	}

	@Override
	public void submitMaterialPrice(int pp_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("PurchasePrice", "pp_statuscode", "pp_id=" + pp_id);
		StateAssert.submitOnlyEntering(status);
		String currency = baseDao.getDBSetting("sys", "defaultCurrency");
		/**
		 *  maz 如果勾选了“同物料，同币别，同供应商的价格不自动失效”逻辑后 不限制同一张核价单明细同物料同供应商同币别同分段数只能有一条数据  2017080530
		 */
		if (!baseDao.isDBSetting("PurchasePrice", "noAutoPurcPrice")) {
			SqlRowList rscount = baseDao.queryForRowSet("select WMSYS.WM_CONCAT(ppd_detno),ppd_material,ppd_weight,ppd_vendcode,ppd_currency,nvl(ppd_lapqty,0) ppd_lapqty from PurchasePriceDetail where ppd_ppid="+pp_id+" group by ppd_material,ppd_weight,ppd_vendcode,ppd_currency,nvl(ppd_lapqty,0) having count(1)>1");
			if(rscount.next()){
				BaseUtil.showError("同一张模材核价单明细同材质同比例同供应商同币别同分段数只能有一条数据,材质:"+rscount.getString("ppd_material")+",比重:"+rscount.getString("ppd_weight")+",供应商编号:"+rscount.getString("ppd_vendcode")+",币别编号:"+rscount.getString("ppd_currency")+",分段数量:"+rscount.getDouble("ppd_lapqty")+",行号:"+rscount.getString("WMSYS.WM_CONCAT(ppd_detno)")+"");
			}
		}
		baseDao.execute("update PurchasePricedetail set ppd_vendcode=upper(ltrim(rtrim(ppd_vendcode))),ppd_prodcode=upper(ltrim(rtrim(ppd_prodcode))) where ppd_ppid="
				+ pp_id);
		baseDao.execute("update PurchasePricedetail set ppd_code=(select pp_code from PurchasePrice where ppd_ppid=pp_id) where ppd_ppid="
				+ pp_id + " and not exists (select 1 from PurchasePrice where ppd_code=pp_code)");
		// 明细供应商不存在!
		SqlRowList rs = baseDao
				.queryForRowSet(
						"SELECT WMSYS.WM_CONCAT('明细行号:'||ppd_detno||'供应商:'||ppd_vendcode) FROM ( SELECT A.*, ROWNUM RN FROM (SELECT ppd_detno,ppd_vendcode FROM PurchasePriceDetail WHERE ppd_ppid=? AND ppd_vendcode not in (SELECT ve_code FROM Vendor)) A WHERE ROWNUM <= 10 ) WHERE RN >= 1",
						pp_id);
		if (rs.next() && rs.getString(1) != null) {
			BaseUtil.showError(BaseUtil.getLocalMessage("vendor_not_exist") + "<br>" + rs.getString(1));
		}
		// 只能选择已审核的供应商!
		rs = baseDao
				.queryForRowSet(
						"SELECT ve_id,ve_code FROM Vendor WHERE ve_code in(SELECT ppd_vendcode FROM PurchasePriceDetail WHERE ppd_ppid=?) AND nvl(ve_auditstatuscode,' ')<>'AUDITED'",
						pp_id);
		if (rs.hasNext()) {
			StringBuffer sb = new StringBuffer();
			while (rs.next()) {
				sb.append("<a href=\"javascript:openUrl('jsps/scm/purchase/vendor.jsp?formCondition=ve_idIS");
				sb.append(rs.getInt(1));
				sb.append("')\">");
				sb.append(rs.getString(2));
				sb.append("</a>&nbsp;<br>");
			}
			if (sb.length() > 0) {
				BaseUtil.showError(BaseUtil.getLocalMessage("vendor_onlyAudited") + sb.toString());
			}
		}
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(ppd_detno) from PurchasePriceDetail where ppd_ppid=? and nvl(ppd_price,0)=0", String.class, pp_id);
		if (dets != null) {
			BaseUtil.showError("明细行单价为0，不能进行当前操作!行号：" + dets);
		}
		useVendDefaultCurrency(caller, pp_id);
		defaultTax(caller, pp_id);
		defaultEndDate(caller, pp_id);
		allowZeroTax(caller, pp_id);
		maxDateInterval(caller, pp_id);
		List<Object[]> datas = baseDao.getFieldsDatasByCondition("PurchasePriceDetail", new String[] { "ppd_prodcode", "ppd_vendcode",
				"ppd_currency" }, "ppd_ppid=" + pp_id);
		Set<String> sets = new HashSet<String>();
		String allField;
		StringBuffer sb = new StringBuffer();
		for (Object[] data : datas) {
			allField = null;
			for (Object single : data) {
				allField = String.valueOf(single) + "#" + allField;
			}
			sets.add(allField);
		}
		for (String data : sets) {
			String fields[] = data.split("#");
			int count = baseDao.getCountByCondition("PurchasePriceDetail", " ppd_prodcode='" + fields[2] + "' and ppd_vendcode='"
					+ fields[1] + "' and ppd_currency='" + fields[0] + "'" + " and ppd_lapqty>0");
			if (count > 0) {
				int i = baseDao.getCountByCondition("PurchasePriceDetail", " ppd_prodcode='" + fields[2] + "' and ppd_vendcode='"
						+ fields[1] + "' and ppd_currency='" + fields[0] + "'" + " and nvl(ppd_lapqty,0)=0 and ppd_ppid=" + pp_id);
				if (i == 0) {
					sb.append("物料编号[" + fields[2] + "]供应商[" + fields[1] + "]币别[" + fields[0] + "],没有分段数为0的价格,请重新填写数据！");
				}
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError(BaseUtil.getLocalMessage(sb.toString()));
		}
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { pp_id });
		int passcount = 0;
		String sql = "select count(case when nvl(ppd_price,0)>nvl(pr_purcprice,0) then 1 else null end) as passcount from PurchasePriceDetail left join Product on pr_code=ppd_prodcode where ppd_ppid="
				+ pp_id;
		// 更新
		SqlRowList sl = baseDao.queryForRowSet(sql);
		if (sl.next()) {
			passcount = sl.getInt(1);
		}
		// 执行提交操作
		baseDao.submit("PurchasePrice", "pp_id=" + pp_id, "pp_status", "pp_statuscode");
		baseDao.updateByCondition("PurchasePrice", "pp_passcount=" + passcount, "pp_id=" + pp_id);
		baseDao.logger.submit(caller, "pp_id", pp_id);
		if("MaterialPrice".equals(caller)){
			SqlRowList rs1 = baseDao.queryForRowSet("select ppd_id from purchasepricedetail where ppd_currency is null and ppd_ppid="+pp_id);
			while(rs1.next()){
				baseDao.updateByCondition("PurchasePriceDetail", "ppd_currency='"+currency+"'", "ppd_id="+rs1.getInt("ppd_id"));
			}
		}
		if (baseDao.isDBSetting(caller, "autoProductVendor")) {
			baseDao.execute("insert into productvendor(pv_id,pv_vendid,PV_DETNO,pv_prodid,pv_vendcode,PV_VENDNAME,pv_prodcode)"
					+ " select productvendor_seq.nextval,ve_id,1,pr_id,ppd_vendcode,ppd_vendname,ppd_prodcode "
					+ " from (select distinct  ve_id,1,pr_id,ppd_vendcode,ppd_vendname,ppd_prodcode "
					+ " from PurchasePricedetail left join vendor on ppd_vendcode=ve_code left join Product on ppd_prodcode=pr_code where ppd_ppid="
					+ pp_id
					+ " and nvl(ppd_vendcode,' ')<>' ' and not exists (select 1 from productvendor where ppd_vendcode=pv_vendcode and ppd_prodcode=pv_prodcode))");
		}
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { pp_id });
	}

	@Override
	public void resSubmitMaterialPrice(int pp_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("PurchasePrice", "pp_statuscode", "pp_id=" + pp_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { pp_id });
		// 执行反提交操作
		baseDao.resOperate("PurchasePrice", "pp_id=" + pp_id, "pp_status", "pp_statuscode");
		baseDao.logger.resSubmit(caller, "pp_id", pp_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { pp_id });
	}

	@Override
	public boolean ImportExcel(int id, Workbook wbs, String substring, String caller) {
		int sheetnum = wbs.getNumberOfSheets();
		StringBuffer sb = new StringBuffer();
		int detno = 1;
		Object textValue = "";
		List<String> sqls = new ArrayList<String>();
		SqlRowList sl = baseDao.queryForRowSet("select max(ppd_detno) from PurchasePriceDetail where ppd_ppid=" + id);
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
				sb.append("insert into PurchasePriceDetail(ppd_id,ppd_detno,ppd_prodcode,ppd_fromdate,ppd_todate,ppd_price,ppd_currency,ppd_rate,ppd_vendcode,ppd_vendname,ppd_lapqty,ppd_minqty,ppd_maxlimit,ppd_purctime,ppd_buyercode,ppd_buyer,ppd_remark,ppd_ppid) Values( ");
				// 取前15列
				sb.append(baseDao.getSeqId("PurchasePriceDETAIL_SEQ") + "," + detno + ",");
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
					if (j == 0) {
						if (textValue == "") {
							BaseUtil.showError("提示第" + (i + 1) + "行 没有物料编号");
						} else {
							sb.append("'" + textValue + "',");
						}
					} else if (j == 1 || j == 2) {
						sb.append(textValue + ",");
					} else
						sb.append("'" + textValue + "',");

				}
				sb.append(id + ")");
				sqls.add(sb.toString());
				detno++;
			}
		}
		baseDao.execute(sqls);
		return true;
	}


	// 税率默认
	private void defaultTax(String caller, Object pp_id) {
		String defaultTax = baseDao.getDBSetting("PurchasePrice", "defaultTax");
		if (defaultTax != null) {
			// 税率强制等于币别表的默认税率
			if ("1".equals(defaultTax)) {
				baseDao.execute("update PurchasePriceDetail set ppd_rate=(select nvl(cr_taxrate,0) from currencys where ppd_currency=cr_name and cr_statuscode='CANUSE')"
						+ " where ppd_ppid=" + pp_id);
			}
			// 税率强制等于供应商资料的默认税率
			if ("2".equals(defaultTax)) {
				baseDao.execute("update PurchasePriceDetail set ppd_rate=(select nvl(ve_taxrate,0) from vendor where ppd_vendcode=ve_code and ve_auditstatuscode='AUDITED')"
						+ " where ppd_ppid=" + pp_id);
			}
		}
	}

	// 有效截止日期默认等于有效开始日期+设置有效天数
	private void defaultEndDate(String caller, Object pp_id) {
		String effectiveDays = baseDao.getDBSetting(caller, "effectiveDays");
		if (effectiveDays != null && !"0".equals(effectiveDays)) {
			baseDao.execute("update PurchasePrice set pp_todate=pp_fromdate+" + effectiveDays + " where pp_id=" + pp_id);
			baseDao.execute("update PurchasePriceDetail set ppd_todate=ppd_fromdate+" + effectiveDays + " where ppd_ppid=" + pp_id);
		}
	}

	// 币别强制等于供应商资料的默认币别
	private void useVendDefaultCurrency(String caller, Object pp_id) {
		if (baseDao.isDBSetting("PurchasePrice", "useVendDefaultCurrency")) {
			baseDao.execute("update PurchasePriceDetail set ppd_currency=(select ve_currency from vendor where ppd_vendcode=ve_code and ve_auditstatuscode='AUDITED')"
					+ " where ppd_ppid=" + pp_id);
		}
	}

	// 本位币允许税率为0
	private void allowZeroTax(String caller, Object pp_id) {
		if (!baseDao.isDBSetting("Purchase", "allowZeroTax")) {
			String currency = baseDao.getDBSetting("defaultCurrency");
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select WM_CONCAT(ppd_detno) from PurchasePriceDetail where nvl(ppd_rate,0)=0 and ppd_currency='" + currency
							+ "' and ppd_ppid=?", String.class, pp_id);
			if (dets != null) {
				BaseUtil.showError("本位币税率为0，不允许提交!行号：" + dets);
			}
		}
	}

	// 间隔天数大于所设置天数，不允许提交
	private void maxDateInterval(String caller, Object pp_id) {
		String maxDateInterval = baseDao.getDBSetting("PurchasePrice", "maxDateInterval");
		if (maxDateInterval != null) {
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select WM_CONCAT(ppd_detno) from PurchasePriceDetail where ppd_todate-ppd_fromdate>? and ppd_ppid=?", String.class,
					maxDateInterval, pp_id);
			if (dets != null) {
				BaseUtil.showError("有效起止日期间隔大于" + maxDateInterval + "天，不允许提交!行号：" + dets);
			}
		}
	}


	/**
	 * 根据采购单位价格计算下单价格
	 */
	private void getPurcRatePrice(Object id) {
		if (baseDao.isDBSetting("PurchasePrice", "purcRatePrice")) {
			SqlRowList rs = baseDao
					.queryForRowSet(
							"SELECT ppd_id,pr_purcrate FROM PurchasePrice LEFT JOIN PurchasePriceDetail on "
									+ "pp_id=ppd_ppid left join Product on ppd_prodcode=pr_code WHERE pp_id=? and nvl(pr_purcrate,0)<>0 and nvl(ppd_purcprice,0)<>0 and nvl(ppd_price,0)=0",
							id);
			while (rs.next()) {
				baseDao.execute("update PurchasePriceDetail set ppd_price=round(nvl(ppd_purcprice,0)*" + rs.getGeneralDouble("pr_purcrate")
						+ ",8) where ppd_id=" + rs.getInt("ppd_id"));
			}
		}
	}
}
