package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ApplicationDao;
import com.uas.erp.service.scm.MCApplicationService;

@Service("mcapplicationService")
public class MCApplicationServiceImpl implements MCApplicationService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private ApplicationDao applicationDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private GetGoodsReserveService getGoodsReserveService;

	@Override
	public void saveMCApplication(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);System.out.println(store.get("ap_id"));
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		String code = store.get("ap_code").toString();
		String ap_type = store.get("ap_type").toString();
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Application", "ap_code='" + code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, grid });
		store.put("ap_code", code);
		store.put("ap_printstatuscode", "UNPRINT");
		store.put("ap_printstatus", BaseUtil.getLocalMessage("UNPRINT"));
		// 保存ApplicationDetail
		for (Map<Object, Object> m : grid) {
			//保存、更新时需要根据价格表【类型=模材】抓取比重，计算总重量
			if("模材".equals(ap_type)){
				String sql = "select ppd_weight from (select nvl(ppd_weight,0) ppd_weight from purchasepricedetail left join PurchasePrice on ppd_ppid=pp_id where pp_kind='模材' and to_char(ppd_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID' and ppd_material='"+m.get("ad_material")+"' order by ppd_price) where rownum<2";
				SqlRowList rs = baseDao.queryForRowSet(sql);
				while(rs.next()){
					if(m.get("ad_sg")==null || "0".equals(m.get("ad_sg").toString())){
						m.put("ad_sg", rs.getDouble("ppd_weight"));
					}
				}
			}
			//保存更新时，分开判断名称、规格（不为空时）是否已经在目前的物料库中存在，如果有存在则提示
			if("一次性请购".equals(ap_type)){
				int count = baseDao.getCountByCondition("product", "pr_detail='"+m.get("ad_materialname")+"' and pr_spec='"+m.get("ad_spec")+"'");
				if(count>0){
					BaseUtil.showError("物料名称："+m.get("ad_materialname")+"，规格："+m.get("ad_spec")+"在物料资料中已经存在！");
				}
			}
			m.put("ad_id", baseDao.getSeqId("APPLICATIONDETAIL_SEQ"));
			m.put("ad_status", BaseUtil.getLocalMessage("ENTERING"));
			m.put("ad_code", code);
			m.remove("ad_yqty");
		}
		// 保存Application
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Application", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "ApplicationDetail");
		baseDao.execute(gridSql);
		if("模材".equals(ap_type)){
			baseDao.execute("update Applicationdetail set ad_qty=round(nvl(ad_mjqty,0)*nvl(ad_length,0)*nvl(ad_width,0)*nvl(ad_height,0)*nvl(ad_sg,0)/1000000,2) where ad_apid="+store.get("ap_id")+"");
			baseDao.execute("update Applicationdetail set ad_qty=round(nvl(ad_mjqty,0)*nvl(ad_height,0)*3.1415926*((nvl(ad_dia,0)/2)*(nvl(ad_dia,0)/2))*nvl(ad_sg,0)/1000000,2) where ad_apid="+store.get("ap_id")+" and nvl(ad_dia,0)>0");
		}
		//String error = checkqty(store.get("ap_id"));
		/*if(baseDao.isDBSetting(caller,"getAdPrice")){
			getProdPrice(Integer.parseInt(store.get("ap_id").toString()));
		}*/
		// 记录操作
		baseDao.logger.save(caller, "ap_id", store.get("ap_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, grid });
		//BaseUtil.showErrorOnSuccess(error);
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
	public void deleteMCApplication(int ap_id, String caller) {
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
	public void updateMCApplicationById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		String code = store.get("ap_code").toString();
		String ap_type = store.get("ap_type").toString();
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		// 修改Application
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Application", "ap_id");
		baseDao.execute(formSql);
		// 修改ApplicationDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ApplicationDetail", "ad_id");
		for (Map<Object, Object> s : gstore) {
			if("模材".equals(ap_type)){
				String sq = "select ppd_weight from (select nvl(ppd_weight,0) ppd_weight from purchasepricedetail left join PurchasePrice on ppd_ppid=pp_id where pp_kind='模材' and to_char(ppd_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID' and ppd_material='"+s.get("ad_material")+"' order by ppd_price) where rownum<2";
				SqlRowList rs = baseDao.queryForRowSet(sq);
				while(rs.next()){
					if(s.get("ad_sg")==null || "0".equals(s.get("ad_sg").toString())){
						s.put("ad_sg", rs.getDouble("ppd_weight"));
					}
				}
			}
			//保存更新时，分开判断名称、规格（不为空时）是否已经在目前的物料库中存在，如果有存在则提示
			if("一次性请购".equals(ap_type)){
				if(s.get("ad_prodname")!=null && !"".equals(s.get("ad_prodname")) && s.get("ad_spec")!=null && !"".equals(s.get("ad_spec"))){
					int count = baseDao.getCountByCondition("product", "pr_detail='"+s.get("ad_prodname")+"' and pr_spec='"+s.get("ad_spec")+"'");
					if(count>0){
						BaseUtil.showError("物料名称："+s.get("ad_prodname")+"，规格："+s.get("ad_spec")+"在物料资料中已经存在！");
					}
				}
			}
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
		if("模材".equals(ap_type)){
			baseDao.execute("update Applicationdetail set ad_qty=round(nvl(ad_mjqty,0)*nvl(ad_length,0)*nvl(ad_width,0)*nvl(ad_height,0)*nvl(ad_sg,0)/1000000,2) where ad_apid="+store.get("ap_id")+"");
			baseDao.execute("update Applicationdetail set ad_qty=round(nvl(ad_mjqty,0)*nvl(ad_height,0)*3.1415926*((nvl(ad_dia,0)/2)*(nvl(ad_dia,0)/2))*nvl(ad_sg,0)/1000000,2) where ad_apid="+store.get("ap_id")+" and nvl(ad_dia,0)>0");
		}
		// 记录操作
		baseDao.logger.update(caller, "ap_id", store.get("ap_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
		BaseUtil.showErrorOnSuccess(error);
	}

	@Override
	public String[] printMCApplication(int ap_id, String caller, String reportName, String condition) {
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
	public void auditMCApplication(int ap_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Application", "ap_statuscode", "ap_id=" + ap_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.audit_onlyCommited"));
		}
		/*SqlRowList rs = baseDao
				.queryForRowSet("select  wm_concat(ad_prodcode) prcode,count(1) num from application left join purchasekind on pk_name=ap_kind left join applicationdetail on ap_id=ad_apid  left join product on ad_prodcode=pr_code where ap_id="
						+ ap_id + " and NVL(pk_allownoappstatus,0)=0 and NVL(pr_material,' ') not in ('已认可','无须认可') ");
		if (rs.next()) {
			if (rs.getInt("num") > 0) {
				BaseUtil.showError("未认可物料:" + rs.getString("prcode") + ",只有已认可和无须认可状态的物料才允许下达请购");
			}
		}*/
		generatePro(caller,ap_id);
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
		//getGoodsReserve(ap_id);
	}

	@Override
	public void resAuditMCApplication(int ap_id, String caller) {
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
	public void submitMCApplication(int ap_id, String caller) {
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
		baseDao.execute("update Application set ap_vendcode=ltrim(rtrim(ap_vendcode)) where ap_id=" + ap_id);
		baseDao.execute("update ApplicationDetail set ad_prodcode=ltrim(rtrim(ad_prodcode)) where ad_apid=" + ap_id);
		// 供应商是否存在
		SqlRowList rs = baseDao.queryForRowSet(
				"SELECT ap_vendcode FROM Application WHERE ap_id=? AND ap_vendcode not in (SELECT ve_code FROM Vendor)", ap_id);
		if (rs.next() && rs.getString("ap_vendcode") != null) {
			BaseUtil.showError(BaseUtil.getLocalMessage("vendor_not_exist") + "<br>" + rs.getString("ap_vendcode"));
		}
		// 只能选择已审核的供应商!
		Object code = baseDao.getFieldDataByCondition("Application", "ap_vendcode", "ap_id=" + ap_id);
		status = baseDao.getFieldDataByCondition("Vendor", "ve_auditstatuscode", "ve_code='" + code + "'");
		if (status != null && !status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("vendor_onlyAudited")
					+ "<a href=\"javascript:openUrl('jsps/scm/purchase/vendor.jsp?formCondition=ve_codeIS" + code + "')\">" + code
					+ "</a>&nbsp;");
		}
		generatePro(caller,ap_id);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { ap_id });
		// 执行提交操作
		baseDao.submit("Application", "ap_id=" + ap_id, "ap_status", "ap_statuscode");
		baseDao.submit("ApplicationDetail", "ad_apid=" + ap_id, "ad_status", "ad_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "ap_id", ap_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { ap_id });
	}

	@Override
	public void resSubmitMCApplication(int ap_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Application", "ap_statuscode", "ap_id=" + ap_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resSubmit_onlyCommited"));
		}
		handlerService.handler(caller, "resCommit", "before", new Object[] { ap_id });
		baseDao.execute("delete product where pr_code in (select ad_prodcode from applicationdetail where ad_apid="+ap_id+") and pr_codetype='一次性'");
		baseDao.execute("update applicationdetail set ad_prodcode=null where ad_apid="+ap_id);
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
					+ " where pu_statuscode='AUDITED' and pd_prodcode='"+obj[0]+"' order by pu_auditdate desc,pd_id desc) "
					+ " WHERE rownum<2) where ad_id="+obj[1]+" and nvl(ad_price,0)=0 and EXISTS (select 1 from purchasedetail left join purchase on pd_puid=pu_id "
					+ " where pu_statuscode='AUDITED' and pd_prodcode='"+obj[0]+"')");
		}
		baseDao.updateByCondition("Applicationdetail","ad_total=nvl(ad_price,0)*nvl(ad_qty,0)", "ad_apid="+ap_id);
		baseDao.updateByCondition("Application a", "ap_total=(select sum(nvl(ad_total,0)) from applicationdetail where ad_apid=a.ap_id)", "ap_id="+ap_id);
	}
	/**
	 * 物料为空  提交审核生成一次性料
	 */
	private void generatePro(String caller,int ap_id){
		//物料是否为空
		List<String> list = new ArrayList<String>();
		SqlRowList rs = baseDao.queryForRowSet("select * from applicationdetail where ad_apid="+ap_id);
		Object ap_type = baseDao.getFieldDataByCondition("Application", "ap_type","ap_id="+ap_id);
		while(rs.next()){
			int pr_precision = 0;
			if(StringUtil.hasText(rs.getObject("ad_qty")) && rs.getObject("ad_qty").toString().indexOf(".") != -1){
				pr_precision = rs.getObject("ad_qty").toString().length() - rs.getObject("ad_qty").toString().indexOf(".") - 1;
			}
			if(rs.getString("ad_prodcode")==null || "".equals(rs.getString("ad_prodcode").toString())){
				Object ad_prodcode = baseDao.getFieldDataByCondition("applicationdetail", "max(ad_prodcode)", "nvl(ad_materialname,' ')='"+(rs.getString("ad_materialname")==null?" ":rs.getString("ad_materialname"))+"' and nvl(ad_spec,' ')='"+(rs.getString("ad_spec")==null?" ":rs.getString("ad_spec"))+"' and ad_id<>"+rs.getInt("ad_id")+" and nvl(ad_prodcode,' ')<>' '");
				if(ad_prodcode!=null && !"".equals(ad_prodcode)){
					baseDao.execute("Update applicationdetail set ad_prodcode='"+ad_prodcode+"' where ad_id="+rs.getInt("ad_id")+"");
					baseDao.execute("update product set pr_precision="+pr_precision+" where pr_code='"+ad_prodcode+"'");
				}else {
					int pr_id = baseDao.getSeqId("PRODUCT_SEQ");
					String pr_code = baseDao.sGetMaxNumber("PRODUCT", 2);
					String sql = "";
					if(StringUtil.hasText(ap_type) && "一次性请购".equals(ap_type)){
						sql = "insert into product(pr_id,pr_code, pr_detail, pr_spec, pr_unit, pr_serial, pr_recordman, pr_docdate, pr_status,pr_statuscode,pr_manutype, pr_supplytype,pr_purcrate,pr_buyercode,pr_buyername,pr_purcunit,pr_codetype,pr_precision) "
								+ "values("+pr_id+",'"+pr_code+"','"+rs.getString("ad_materialname")+"','"+rs.getString("ad_spec")+"','"+rs.getString("ad_unit")+"','模材','"+SystemSession.getUser().getEm_name()+"',sysdate,'已审核','AUDITED','外购','推式',1,'"+SystemSession.getUser().getEm_name()+"','"+SystemSession.getUser().getEm_code()+"','"+rs.getString("ad_unit")+"','一次性','3')";
					}else if(StringUtil.hasText(ap_type) && "模材".equals(ap_type)){
						sql = "insert into product(pr_id,pr_code, pr_detail, pr_spec, pr_unit, pr_serial, pr_recordman, pr_docdate, pr_status,pr_statuscode,pr_manutype, pr_supplytype,pr_purcrate,pr_buyercode,pr_buyername,pr_purcunit,pr_codetype,pr_precision) "
								+ "values("+pr_id+",'"+pr_code+"','"+rs.getString("ad_materialname")+"','"+rs.getString("ad_spec")+"','"+rs.getString("ad_unit")+"','模材','"+SystemSession.getUser().getEm_name()+"',sysdate,'已审核','AUDITED','外购','推式',1,'"+SystemSession.getUser().getEm_name()+"','"+SystemSession.getUser().getEm_code()+"','KG','一次性','2')";
					}
					baseDao.execute("Update applicationdetail set ad_prodcode='"+pr_code+"' where ad_id="+rs.getInt("ad_id")+"");
					list.add(sql);
					handlerService.handler(caller, "turn", "after", new Object[] { pr_id });
				}
			}
		}
		baseDao.execute(list);
	}
}
