package com.uas.erp.service.pm.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.MakeDao;
import com.uas.erp.dao.common.PurchaseDao;
import com.uas.erp.service.pm.WCPlanService;

@Service
public class WCPlanServiceImpl implements WCPlanService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private PurchaseDao purchaseDao;
	@Autowired
	private MakeDao makeDao;
	@Autowired
	private HandlerService handlerService;

	public void saveWCPlan(String formStore, String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("WCPlan", "wc_code='" + store.get("wc_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "WCPlan", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "wc_id", store.get("wc_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteWCPlan(int wc_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("WCPlan", "wc_statuscode", "wc_id=" + wc_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { wc_id });
		// 删除
		baseDao.deleteById("WCPlan", "wc_id", wc_id);
		// 记录操作
		baseDao.logger.delete(caller, "wc_id", wc_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { wc_id });
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void updateWCPlan(String formStore, String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gridstores = BaseUtil.parseGridStoreToMaps(param);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("WCPlan", "wc_statuscode", "wc_id=" + store.get("wc_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		List<String> sqls = new ArrayList<String>();
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "WCPlan", "wc_id");
		String getSql = "";
		int detno = 0;
		Object wc_id = store.get("wc_id");
		sqls.add(formSql);
		SqlRowList sl = baseDao.queryForRowSet("select max(wd_detno) from WCPLANDETAIL where wd_wcid=" + wc_id);
		if (sl.next()) {
			detno = sl.getInt(1);
		}
		for (Map<Object, Object> map : gridstores) {
			Object keyvalue = map.get("wd_id");
			if (StringUtil.hasText(keyvalue) && !keyvalue.equals("0")) {
				// 更新 按日期拆开
				Object makecode = map.get("wd_makecode");
				for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
					String key = (String) iter.next();
					if (key.contains("-")) {
						String datetime = DateUtil.parseDateToOracleString(Constant.YMD, key);
						Object wd_id = baseDao.getFieldDataByCondition("WCPLANDETAIL", "wd_id", "wd_wcid=" + wc_id + " AND  wd_makecode='"
								+ makecode + "' AND wd_date=" + datetime);
						if (wd_id != null) {
							getSql = "update WCPlanDetail  set wd_planqty='" + map.get(key) + "' where  wd_id=" + wd_id;
						} else {
							detno++;
							getSql = "insert into WCPlanDetail (wd_id,wd_code,wd_detno,wd_makecode,wd_planqty,wd_date,wd_wcid) values("
									+ baseDao.getSeqId("WCPLANDETAIL_SEQ") + ",'WC_" + baseDao.sGetMaxNumber("WCPLANDETAIL", 2) + "',"
									+ detno + ",'" + makecode + "','" + map.get(key) + "'," + datetime + "," + wc_id + ")";
						}
						sqls.add(getSql);
					}
				}
			} else {
				sqls.add(SqlUtil.getInsertSql(map, "WCPlanDetail", "wd_id"));
			}
		}
		baseDao.execute(sqls);

		SqlRowList rs = baseDao
				.queryForRowSet("select count(0)c ,wm_concat(wd_makecode) macode from wcplandetail left join make on wd_makecode=ma_code where wd_wcid="
						+ wc_id + " and (ma_code is null or ma_statuscode<>'AUDITED')");
		if (rs.getInt("c") > 0) {
			BaseUtil.showError("不是已审核工单不能排程，单号:" + rs.getString("macode"));
		}
		// 记录操作
		baseDao.logger.save(caller, "wc_id", store.get("wc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void auditWCPlan(int wc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("WCPlan", "wc_statuscode", "wc_id=" + wc_id);
		StateAssert.auditOnlyCommited(status);
		SqlRowList rs = baseDao
				.queryForRowSet("select count(0)c ,wm_concat(wd_makecode) macode from wcplandetail left join make on wd_makecode=ma_code where wd_wcid="
						+ wc_id + " and (ma_code is null or ma_statuscode<>'AUDITED')");
		if (rs.getInt("c") > 0) {
			BaseUtil.showError("不是已审核工单不能排程，单号:" + rs.getString("macode"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { wc_id });
		// 执行审核操作
		baseDao.audit("WCPlan", "wc_id=" + wc_id, "wc_status", "wc_statuscode", "wc_auditdate", "wc_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "wc_id", wc_id);
		// 执行审核后的其它逻辑
		WCPlanClash("", wc_id);
		handlerService.afterAudit(caller, new Object[] { wc_id });
	}

	@Override
	public void resAuditWCPlan(int wc_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("WCPlan", "wc_statuscode", "wc_id=" + wc_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("WCPlan", "wc_id=" + wc_id, "wc_status", "wc_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "wc_id", wc_id);
	}

	@Override
	public void submitWCPlan(int wc_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("WCPlan", "wc_statuscode", "wc_id=" + wc_id);
		StateAssert.submitOnlyEntering(status);
		SqlRowList rs = baseDao
				.queryForRowSet("select count(0)c ,wm_concat(wd_makecode) macode from wcplandetail left join make on wd_makecode=ma_code where wd_wcid="
						+ wc_id + " and (ma_code is null or ma_statuscode<>'AUDITED') and rownum<10");
		if (rs.getInt("c") > 0) {
			BaseUtil.showError("不是已审核工单不能排程，单号:" + rs.getString("macode"));
		}
		rs = baseDao
				.queryForRowSet("select count(0)c ,wm_concat(wd_makecode) macode from wcplandetail left join wcplan on wc_id=wd_wcid left join make on wd_makecode=ma_code where wd_wcid="
						+ wc_id + " and (wc_factory<>' ' and wc_factory<>wc_factory) and rownum<10");
		if (rs.getInt("c") > 0) {
			BaseUtil.showError("工单的[所属工厂]与本排程运算的工厂不一致，单号:" + rs.getString("macode"));
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { wc_id });
		// 执行提交操作
		baseDao.submit("WCPlan", "wc_id=" + wc_id, "wc_status", "wc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "wc_id", wc_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { wc_id });
	}

	@Override
	public void resSubmitWCPlan(int wc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("WCPlan", "wc_statuscode", "wc_id=" + wc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { wc_id });
		// 执行反提交操作
		baseDao.resOperate("WCPlan", "wc_id=" + wc_id, "wc_status", "wc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "wc_id", wc_id);
		handlerService.afterResSubmit(caller, new Object[] { wc_id });
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean ImportExcel(int id, Workbook wbs, String substring, String caller) {
		int sheetnum = wbs.getNumberOfSheets();
		StringBuffer sb = new StringBuffer();
		String fields = "";
		StringBuffer sbfield = new StringBuffer();
		int detno = 1;
		Object textValue = "";
		int startnum = 0;
		int endnum = 0;
		List<String> sqls = new ArrayList<String>();
		Object wccode = baseDao.getFieldDataByCondition("WCPlan", "wc_code", "wc_id=" + id);
		if (sheetnum > 0) {
			HSSFSheet sheet = (HSSFSheet) wbs.getSheetAt(0);
			// 取第一列的信息 格式可能不确定 需要
			HSSFRow firstrow = sheet.getRow(0);
			// 取到需要插入的字段
			for (int n = 0; n < firstrow.getLastCellNum(); n++) {
				HSSFCell cell = firstrow.getCell(n);
				String value = cell.getStringCellValue();
				if (value.contains("date")) {
					if (sbfield.indexOf("wd_date") == -1) {
						sbfield.append("wd_date,");
						startnum = n;
					} else
						endnum = n;

				} else
					sbfield.append(value.split(";")[1] + ",");
			}
			fields = "wd_code,wd_id,wd_wcid,wd_detno," + sbfield.toString() + "wd_planqty";
			// 再遍历行 从第3行开始
			if (endnum < startnum && endnum == 0)
				endnum = 1;
			for (int i = 2; i < sheet.getLastRowNum() + 1; i++) {
				HSSFRow row = sheet.getRow(i);
				if (row != null) {
					for (int k = startnum; k < endnum + 1; k++) {
						HSSFCell datecell = row.getCell(k);
						if (datecell != null) {
							Object value = datecell.toString();
							switch (datecell.getCellType()) {
							case HSSFCell.CELL_TYPE_NUMERIC:
								if (HSSFDateUtil.isCellDateFormatted(datecell)) {
									value = DateUtil.parseDateToString(datecell.getDateCellValue(), null);
								} else {
									value = datecell.getNumericCellValue();
								}
								break;
							case HSSFCell.CELL_TYPE_STRING:
								value = datecell.getStringCellValue();
								break;
							case HSSFCell.CELL_TYPE_BOOLEAN:
								value = datecell.getBooleanCellValue();
								break;
							case HSSFCell.CELL_TYPE_FORMULA:
								value = datecell.getCellFormula() + "";
								break;
							case HSSFCell.CELL_TYPE_BLANK:
								value = "";
								break;
							case HSSFCell.CELL_TYPE_ERROR:
								value = "";
								break;
							default:
								value = "";
								break;
							}
							if (value != null && !value.equals("")) {
								sb.setLength(0);
								sb.append("insert into wcplandetail(");
								sb.append(fields + ") Values ('" + wccode + "',");
								sb.append(baseDao.getSeqId("WCPLANDETAIL_SEQ") + ",");
								sb.append(id + ",");
								sb.append(detno + ",");
								for (int j = 0; j < row.getLastCellNum(); j++) {
									HSSFCell cell = row.getCell(j);
									if (j < startnum || j > endnum) {
										switch (cell.getCellType()) {
										case HSSFCell.CELL_TYPE_NUMERIC:
											textValue = cell.getNumericCellValue();
											break;
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
										if (textValue.equals("")) {
											sb.append("null,");
										} else {
											sb.append("'" + textValue + "',");
										}
									} else if (j == k) {
										sb.append(DateUtil.parseDateToOracleString(Constant.YMD, sheet.getRow(1).getCell(k)
												.getDateCellValue())
												+ ",");
									}
								}
								// 排产数
								sb.append(value + ")");
								sqls.add(sb.toString());
								detno++;
							}
						}
					}
				}
			}
		}
		baseDao.execute(sqls);
		// 更新资料
		String updateSql = "update wcplandetail set  (wd_prodcode,wd_salecode,wd_saledetno)=(select ma_prodcode,ma_salecode,ma_saledetno from make where ma_code=wd_makecode ) where wd_makecode is not null and  wd_wcid="
				+ id;
		baseDao.execute(updateSql);
		return true;
	}

	@Override
	public void deleteAllDetails(int id, String caller) {
		/** 删除所有明细 */
		String DeleteSql = "delete from wcplandetail where wd_wcid=" + id;
		baseDao.execute(DeleteSql);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void loadMake(String caller, String data, int wc_id) {
		// deleteAllDetails(wc_id,language,employee);
		List<Map<Object, Object>> lists = BaseUtil.parseGridStoreToMaps(data);
		Map<Object, Object> map = null;
		Map<Object, Object> modelmap = null;
		List<String> sqls = new ArrayList<String>();
		// 取时间
		Object maxdetno = baseDao.getFieldDataByCondition("WCPlanDetail", "nvl(max(wd_detno),0)", "wd_wcid=" + wc_id);
		Object[] fieldsdata = baseDao.getFieldsDataByCondition("WCPlan", "wc_code,wc_fromdate,wc_todate", "wc_id=" + wc_id);
		int detno = Integer.parseInt(maxdetno.toString()) + 1;
		Date date = null, date1 = null, date2 = null;
		SimpleDateFormat sdf = new SimpleDateFormat(Constant.YMD);
		for (int i = 0; i < lists.size(); i++) {
			map = lists.get(i);
			modelmap = new HashMap<Object, Object>();
			modelmap.put("wd_id", baseDao.getSeqId("WCPLANDETAIL_SEQ"));
			modelmap.put("wd_code", fieldsdata[0]);
			modelmap.put("wd_detno", detno);
			modelmap.put("wd_wcid", wc_id);
			modelmap.put("wd_makecode", map.get("ma_code"));
			modelmap.put("wd_salecode", map.get("ma_salecode"));
			modelmap.put("wd_planqty", map.get("ma_qty-nvl(ma_madeqty,0)"));
			try {
				date = sdf.parse(map.get("ma_planbegindate").toString());
				date1 = sdf.parse(fieldsdata[1].toString());
				date2 = sdf.parse(fieldsdata[2].toString());
				if (date.compareTo(date1) < 0) {
					modelmap.put("wd_date", DateUtil.parseDateToString(date1, Constant.YMD));
				} else if (date.compareTo(date2) > 0) {
					modelmap.put("wd_date", DateUtil.parseDateToString(date2, Constant.YMD));
				} else
					modelmap.put("wd_date", map.get("ma_planbegindate"));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			sqls.add(SqlUtil.getInsertSqlByMap(modelmap, "WCPlanDetail"));
			detno++;
		}
		baseDao.execute(sqls);
		baseDao.execute("merge into WCPlanDetail using (select ma_code,ma_qty-nvl(ma_madeqty,0) as qty,ma_prodcode from make )src on(wd_makecode=src.ma_code) when matched then update set wd_planqty=src.qty,wd_prodcode=ma_prodcode where wd_wcid="
				+ wc_id + " ");
	}

	@Override
	public void loadAllMakeByCondition(String caller, int wc_id, String condition) {
		Object[] obj1 = baseDao.getFieldsDataByCondition("form", "fo_detailtable,fo_detailcondition", " fo_caller='WCPlanSource'");
		if (obj1 == null) {
			return;
		}
		String BaseCondition = obj1[1].toString();
		BaseCondition = BaseCondition + " AND " + condition;
		SqlRowList sl = baseDao.queryForRowSet("select ma_planbegindate,ma_code,ma_salecode,ma_qty-nvl(ma_madeqty,0) remain from "
				+ obj1[0].toString() + " where " + BaseCondition);
		Map<Object, Object> modelmap = null;
		List<String> sqls = new ArrayList<String>();
		// 取时间
		Object maxdetno = baseDao.getFieldDataByCondition("WCPlanDetail", "nvl(max(wd_detno),0)", "wd_wcid=" + wc_id);
		Object[] fieldsdata = baseDao.getFieldsDataByCondition("WCPlan", "wc_code,wc_fromdate,wc_todate", "wc_id=" + wc_id);
		int detno = Integer.parseInt(maxdetno.toString()) + 1;
		Date date = null, date1 = null, date2 = null;
		SimpleDateFormat sdf = new SimpleDateFormat(Constant.YMD);
		while (sl.next()) {
			modelmap = new HashMap<Object, Object>();
			modelmap.put("wd_id", baseDao.getSeqId("WCPLANDETAIL_SEQ"));
			modelmap.put("wd_code", fieldsdata[0]);
			modelmap.put("wd_detno", detno);
			modelmap.put("wd_wcid", wc_id);
			modelmap.put("wd_makecode", sl.getObject("ma_code"));
			modelmap.put("wd_salecode", sl.getObject("ma_salecode"));
			modelmap.put("wd_planqty", sl.getObject("remain"));
			try {
				date = sdf.parse(sl.getObject("ma_planbegindate").toString());
				date1 = sdf.parse(fieldsdata[1].toString());
				date2 = sdf.parse(fieldsdata[2].toString());
				if (date.compareTo(date1) < 0) {
					modelmap.put("wd_date", DateUtil.parseDateToString(date1, Constant.YMD));
				} else if (date.compareTo(date2) > 0) {
					modelmap.put("wd_date", DateUtil.parseDateToString(date2, Constant.YMD));
				} else
					modelmap.put("wd_date", sl.getObject("ma_planbegindate"));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			sqls.add(SqlUtil.getInsertSqlByMap(modelmap, "WCPlanDetail"));
			detno++;
		}
		baseDao.execute(sqls);
		baseDao.execute("merge into WCPlanDetail using (select ma_code,ma_qty-nvl(ma_madeqty,0) as qty,ma_prodcode from make )src on(wd_makecode=src.ma_code) when matched then update set wd_planqty=src.qty,wd_prodcode=ma_prodcode where wd_wcid="
				+ wc_id + " ");
	}

	/*
	 * 冲减排程数量大于工单未完工数量 ,参数二选一
	 */
	public void WCPlanClash(String wc_code, int wc_id) {

		float clashqty = 0;
		int wcid = 0;
		String SQLStr = "";
		wcid = wc_id;
		if (wcid == 0) {
			wcid = Integer.parseInt(baseDao.getFieldDataByCondition("WCPlan", "wc_id", "wc_code='" + wc_code + "'").toString());
		}
		if (wcid == 0) {
			return;
		}
		// 第一次装载的记录标示来源数量
		SQLStr = "UPDATE wcplandetail set wd_sourceqty=wd_planqty where wd_wcid='" + wcid
				+ "' and wd_planqty+nvl(wd_clashqty,0)<>nvl(wd_sourceqty,0) ";
		baseDao.execute(SQLStr);
		SQLStr = "select ma_code,planqty,ma_qty,nvl(ma_madeqty,0) as ma_madeqty from (select wd_makecode,sum(wd_planqty) as planqty from wcplandetail where wd_wcid="
				+ wcid
				+ " and wd_planqty>0 group by wd_makecode)A left join make on ma_code=wd_makecode "
				+ " where ma_qty-NVL(ma_madeqty,0)< planqty  ";
		SqlRowList rs = baseDao.queryForRowSet(SQLStr);
		while (rs.next()) {
			clashqty = rs.getFloat("planqty") - (rs.getFloat("ma_qty") - rs.getFloat("ma_madeqty"));
			if (clashqty > 0) {
				SQLStr = "select wd_id,wd_planqty from wcplandetail where wd_wcid='" + wcid + "' and wd_makecode='"
						+ rs.getString("ma_code") + "' and wd_planqty>0 order by wd_date asc";
				SqlRowList rs2 = baseDao.queryForRowSet(SQLStr);
				while (rs2.next() && clashqty > 0) {
					if (rs2.getFloat("wd_planqty") > clashqty) {
						SQLStr = "UPDATE wcplandetail set wd_planqty=wd_planqty-" + clashqty + ",wd_clashqty=NVL(wd_clashqty,0)+"
								+ clashqty + " where wd_id=" + rs2.getInt("wd_id");
						clashqty = 0;

					} else {
						SQLStr = "UPDATE wcplandetail set wd_planqty=0 ,wd_clashqty=NVL(wd_clashqty,0)+" + rs2.getFloat("wd_planqty")
								+ " where wd_id=" + rs2.getInt("wd_id");
						clashqty = clashqty - rs2.getFloat("wd_planqty");

					}
					baseDao.execute(SQLStr);
				}
			}

		}
	}

	@Override
	public String RunLackMaterial(String code, String caller) {
		WCPlanClash(code, 0);
		// 执行运算存储过程
		String str = baseDao.callProcedure("MM_MaterialLackForWCPlan", new Object[] { code, SystemSession.getUser().getEm_name() });
		return str;
	}

	@Override
	public void RunLackWip(String code, String caller) {
		WCPlanClash(code, 0);
		String str = baseDao.callProcedure("MM_WCPLANFORPULLSEND", new Object[] { code, SystemSession.getUser().getEm_name() });
		if (str != null && !str.trim().equals("")) {
			// 提示错误信息
			BaseUtil.showError(str);
		}
	}

	@Override
	public JSONObject getDateRange(String condition) {
		JSONObject obj = new JSONObject();
		if (!condition.equals("")) {
			Object[] datas = baseDao.getFieldsDataByCondition("WCPlan", "wc_fromdate,wc_todate", "wc_id=" + condition.split("=")[1]);
			obj.put("startdate", datas[0]);
			obj.put("enddate", datas[1]);
		}
		return obj;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void throwPurchaseNotify(String caller, String data, String condition) {
		String statuscondition = "";
		String sql = "", checksql = "";
		String errstr = "";
		statuscondition = " and (mln_statuscode='UNTHROW' or NVL(mln_statuscode,' ')=' ') and pd_qty-NVL(pd_yqty,0)-mln_qty>=0 and mln_changeqty>0 ";
		if (data != null) {
			StringBuffer sb = new StringBuffer();
			List<Map<Object, Object>> NeedStore = BaseUtil.parseGridStoreToMaps(data);
			String ids = CollectionUtil.pluckSqlString(NeedStore, "pd_id");
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(distinct '采购单号：'||pd_code||'序号：'||pd_detno) from purchasedetail where pd_id in ("
									+ ids
									+ ") and exists (select 1 from PurchaseChangeDetail left join PurchaseChange on pcd_pcid=pc_id where pc_purccode=pd_code and pcd_pddetno=pd_detno and pc_statuscode not in ('AUDITED','CONFIRMED') and nvl(pcd_oldqty,0)<>nvl(pcd_newqty,0))",
							String.class);
			if (dets != null) {
				BaseUtil.showError("存在采购变更单未审核，不允许投放！" + dets);
			}
			sb.append("(");
			for (int i = 0; i < NeedStore.size(); i++) {
				sb.append(NeedStore.get(i).get("mln_id") + ",");
			}
			String range = sb.toString().substring(0, sb.toString().length() - 1) + ")";
			String idcondition = " mln_id in " + range;
			//扣减特采未过账数据-nvl(v_tcqty,0)
			checksql = "select * from (Select max(pd_code)pd_code,max(pd_detno)pd_detno,sum(mln_changeqty) qty,max(pd_qty-NVL(pd_yqty,0)-NVL(v_pd_turnqty,0)-nvl(v_tcqty,0)) remainqty from  materiallackfornotify left join purchasedetail on mln_pdid=pd_id left join scm_purchaseturnqty_view on pd_id=v_pd_id where "
					+ idcondition + statuscondition + " group by mln_pdid) where qty>remainqty ";
			SqlRowList rs0 = baseDao.queryForRowSet(checksql);
			while (rs0.next()) {
				errstr += "PO:" + rs0.getString("pd_code") + "序号:" + rs0.getString("pd_detno") + "投放数量大于未通知数:" + rs0.getString("remainqty");
			}
			if (!errstr.equals("")) {
				BaseUtil.showErrorOnSuccess(errstr);
				return;
			}
			sql = "Select materiallackfornotify.*,pr_code,pr_id,pu_vendcode,pu_vendname,pd_qty-NVL(pd_yqty,0)-NVL(v_pd_turnqty,0)-nvl(v_tcqty,0) remainqty,pu_statuscode from materiallackfornotify left join product on pr_code=mln_prodcode left join purchasedetail on mln_pdid=pd_id left join purchase on pu_id=pd_puid left join scm_purchaseturnqty_view on pd_id=v_pd_id  where "
					+ idcondition + statuscondition;
		} else {
			checksql = "select * from (Select max(pd_code)pd_code,max(pd_detno)pd_detno,sum(mln_changeqty) qty,max(pd_qty-NVL(pd_yqty,0)-NVL(v_pd_turnqty,0)-nvl(v_tcqty,0)) remainqty from materiallackfornotify left join product on pr_code=mln_prodcode left join purchasedetail on mln_pdid=pd_id left join scm_purchaseturnqty_view on pd_id=v_pd_id left join purchase on pu_id=pd_puid where "
					+ condition + statuscondition + " group by mln_pdid) where qty>remainqty ";
			SqlRowList rs0 = baseDao.queryForRowSet(checksql);
			while (rs0.next()) {
				errstr += "PO:" + rs0.getString("pd_code") + "序号:" + rs0.getString("pd_detno") + "投放数量大于未通知数:" + rs0.getString("remainqty");
			}
			if (!errstr.equals("")) {
				BaseUtil.showErrorOnSuccess(errstr);
				return;
			}
			sql = "Select materiallackfornotify.*,pr_code,pr_id,pu_vendcode,pu_vendname,pd_qty-NVL(pd_yqty,0)-NVL(v_pd_turnqty,0)-nvl(v_tcqty,0) remainqty,pu_statuscode from  materiallackfornotify left join product on pr_code=mln_prodcode left join purchasedetail on mln_pdid=pd_id left join scm_purchaseturnqty_view on pd_id=v_pd_id left join purchase on pu_id=pd_puid  where "
					+ condition + statuscondition;
		}
		// 判断是否有已经超数量投放
		// 以下开始投放
		errstr = "";
		SqlRowList rs = baseDao.queryForRowSet(sql);
		while (rs.next()) {
			if (rs.getString("pu_statuscode").equals("AUDITED")) {
				Map<Object, Object> map = new HashMap<Object, Object>();
				map.put("pn_id", baseDao.getSeqId("PURCHASENOTIFY_SEQ"));
				map.put("pn_mrpcode", rs.getObject("mln_plancode"));
				map.put("pn_mdid", rs.getObject("mln_id"));
				map.put("pn_ordercode", rs.getObject("mln_ordercode"));
				map.put("pn_orderdetno", rs.getObject("mln_orderdetno"));
				map.put("pn_vendcode", rs.getObject("pu_vendcode"));
				map.put("pn_vendname", rs.getObject("pu_vendname"));
				map.put("pn_prodcode", rs.getObject("mln_prodcode"));
				map.put("pn_qty", rs.getObject("mln_changeqty"));
				map.put("pn_delivery", rs.getObject("mln_newdelivery"));
				map.put("pn_prodid", rs.getObject("pr_id"));
				map.put("pn_pdid", rs.getObject("mln_pdid"));
				map.put("pn_status", "未确认");
				map.put("pn_statuscode", "UNCONFIRM");
				map.put("pn_indate", DateUtil.parseDateToString(new Date(), Constant.YMD_HMS));
				map.put("pn_inman", SystemSession.getUser().getEm_name());
				map.put("pn_thisqty", 0);
				map.put("pn_endqty", 0);
				map.put("pn_thisbpqty", 0);
				baseDao.execute(SqlUtil.getInsertSqlByMap(map, "PURCHASENOTIFY"));
				baseDao.updateByCondition("materiallackfornotify", " mln_statuscode='THROWED',mln_status='已投放'",
						"mln_id=" + rs.getString("mln_id"));
			} else {
				errstr += "PO:" + rs.getString("pd_code") + ";";
			}
		}
		if (!errstr.equals("")) {
			BaseUtil.showErrorOnSuccess("其中:" + errstr + "不是已审核状态不能投放");
			return;
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public String ThrowWipNeed(String caller, String data, String condition) {
		String statuscondition = "";
		String sql = "";
		StringBuffer sb = new StringBuffer();
		StringBuffer returnsb = new StringBuffer();
		JSONObject j = null;
		String bccode = null;
		Object bcpiid = 0;
		String bcpiclass = "拨出单", bccaller = "ProdInOut!AppropriationOut";
		String outwhcode = "", inwhcode = "";
		int bcdetno = 0;
		statuscondition = " and (mlp_statuscode='UNTHROW' or NVL(mlp_statuscode,' ')=' ') and mlp_changeqty>0 ";
		if (data != null) {
			List<Map<Object, Object>> NeedStore = BaseUtil.parseGridStoreToMaps(data);
			sb.append("(");
			for (int i = 0; i < NeedStore.size(); i++) {
				sb.append(NeedStore.get(i).get("mlp_id") + ",");
			}
			String range = sb.toString().substring(0, sb.toString().length() - 1) + ")";
			String idcondition = " mlp_id in " + range;
			sql = "Select * from materiallackforpull  where " + idcondition + statuscondition + " order by mlp_outwhcode,mlp_inwhcode";
		}
		// 以下开始投放
		SqlRowList rs = baseDao.queryForRowSet(sql);
		while (rs.next()) {
			if (!outwhcode.equals(rs.getString("mlp_outwhcode")) || !inwhcode.equals(rs.getString("mlp_inwhcode"))) {
				outwhcode = rs.getString("mlp_outwhcode");
				inwhcode = rs.getString("mlp_inwhcode");
				j = makeDao.newProdIO(outwhcode, bcpiclass, bccaller,null);
				bccode = j.getString("pi_inoutno");
				bcpiid = j.get("pi_id");
				returnsb.append("成功产生，拨出单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
						+ bcpiid + "&gridCondition=pd_piidIS" + bcpiid + "&whoami=" + bccaller + "')\">" + bccode + "</a>&nbsp;<br>");
				bcdetno = 1;
			}
			if (bccode != null) {
				String newdetailstr = "INSERT INTO ProdIODetail(pd_id, pd_piid, pd_inoutno, pd_piclass, pd_pdno, pd_status,pd_auditstatus,pd_prodcode, pd_outqty,"
						+ "pd_prodid,pd_whcode,pd_whname,pd_inwhcode,pd_inwhname,pd_remark) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				baseDao.execute(
						newdetailstr,
						new Object[] { baseDao.getSeqId("PRODIODETAIL_SEQ"), bcpiid, bccode, bcpiclass, bcdetno++, 0, "ENTERING",
								rs.getString("mlp_prodcode"), rs.getDouble("mlp_changeqty"), rs.getInt("mlp_prodid"), outwhcode,
								rs.getString("mlp_outwhname"), inwhcode, rs.getString("mlp_inwhname"),
								"总欠料数:" + rs.getInt("mlp_wiplacksum") });
				baseDao.execute("update materiallackforpull set mlp_status='已投放',mlp_statuscode='THROWED' where mlp_id="
						+ rs.getString("mlp_id"));
			}

		}
		return returnsb.toString();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void loadSale(String caller, String data, int wc_id) {

		List<Map<Object, Object>> lists = BaseUtil.parseGridStoreToMaps(data);
		Map<Object, Object> map = null;
		Map<Object, Object> modelmap = null;
		List<String> sqls = new ArrayList<String>();
		// 取时间
		Object maxdetno = baseDao.getFieldDataByCondition("WCPlanDetail", "nvl(max(wd_detno),0)", "wd_wcid=" + wc_id);
		Object[] fieldsdata = baseDao.getFieldsDataByCondition("WCPlan", "wc_code,wc_fromdate,wc_todate", "wc_id=" + wc_id);
		int detno = Integer.parseInt(maxdetno.toString()) + 1;
		for (int i = 0; i < lists.size(); i++) {
			map = lists.get(i);
			modelmap = new HashMap<Object, Object>();
			modelmap.put("wd_id", baseDao.getSeqId("WCPLANDETAIL_SEQ"));
			modelmap.put("wd_code", fieldsdata[0]);
			modelmap.put("wd_detno", detno);
			modelmap.put("wd_wcid", wc_id);
			modelmap.put("wd_salecode", map.get("sa_code"));
			modelmap.put("wd_saledetno", map.get("sd_detno"));
			modelmap.put("wd_date", map.get("sd_pmcdate"));
			modelmap.put("wd_prodcode", map.get("sd_prodcode"));
			modelmap.put("wd_orderkind", "SALE");
			sqls.add(SqlUtil.getInsertSqlByMap(modelmap, "WCPlanDetail"));
			detno++;
		}
		baseDao.execute(sqls);
		baseDao.execute("update WCPlanDetail set wd_planqty=(select max(sd_qty-NVL(sd_sendqty,0)) from saledetail where sd_code=wd_salecode and sd_detno=wd_saledetno ) where wd_wcid="
				+ wc_id + " and wd_salecode<>' ' and wd_saledetno>0 and NVL(wd_makecode,' ')=' ' and NVL(wd_planqty,0)=0");
	}

	@Override
	public void loadAllSaleByCondition(String caller, int wc_id, String condition) {
		Object[] obj1 = baseDao.getFieldsDataByCondition("form", "fo_detailtable,fo_detailcondition", " fo_caller='WCPlanSaleSource'");
		if (obj1 == null) {
			return;
		}
		String BaseCondition = obj1[1].toString();
		BaseCondition = BaseCondition + " AND " + condition;
		SqlRowList sl = baseDao.queryForRowSet("select sa_code,sa_date,sd_prodcode,sd_qty-nvl(sd_sendqty,0) qty,sd_detno from "
				+ obj1[0].toString() + " where " + BaseCondition);
		Map<Object, Object> modelmap = null;
		List<String> sqls = new ArrayList<String>();
		// 取时间
		Object maxdetno = baseDao.getFieldDataByCondition("WCPlanDetail", "nvl(max(wd_detno),0)", "wd_wcid=" + wc_id);
		Object[] fieldsdata = baseDao.getFieldsDataByCondition("WCPlan", "wc_code,wc_fromdate,wc_todate", "wc_id=" + wc_id);
		int detno = Integer.parseInt(maxdetno.toString()) + 1;
		while (sl.next()) {
			modelmap = new HashMap<Object, Object>();
			modelmap.put("wd_id", baseDao.getSeqId("WCPLANDETAIL_SEQ"));
			modelmap.put("wd_code", fieldsdata[0]);
			modelmap.put("wd_detno", detno);
			modelmap.put("wd_wcid", wc_id);
			modelmap.put("wd_salecode", sl.getObject("sa_code"));
			modelmap.put("wd_saledetno", sl.getObject("sd_detno"));
			modelmap.put("wd_date", sl.getObject("sd_pmcdate"));
			modelmap.put("wd_prodcode", sl.getObject("sd_prodcode"));
			modelmap.put("wd_planqty", sl.getObject("qty"));
			modelmap.put("wd_orderkind", "SALE");
			sqls.add(SqlUtil.getInsertSqlByMap(modelmap, "WCPlanDetail"));
			detno++;
		}
		baseDao.execute(sqls);
		baseDao.execute("update WCPlanDetail set wd_planqty=(select max(sd_qty-NVL(sd_sendqty,0)) from saledetail where sd_code=wd_salecode and sd_detno=wd_saledetno ) where wd_wcid="
				+ wc_id + " and wd_salecode<>' ' and wd_saledetno>0 and NVL(wd_makecode,' ')=' ' and NVL(wd_planqty,0)=0");
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void loadSaleForecast(String caller, String data, int wc_id) {

		List<Map<Object, Object>> lists = BaseUtil.parseGridStoreToMaps(data);
		Map<Object, Object> map = null;
		Map<Object, Object> modelmap = null;
		List<String> sqls = new ArrayList<String>();
		// 取时间
		Object maxdetno = baseDao.getFieldDataByCondition("WCPlanDetail", "nvl(max(wd_detno),0)", "wd_wcid=" + wc_id);
		Object[] fieldsdata = baseDao.getFieldsDataByCondition("WCPlan", "wc_code,wc_fromdate,wc_todate", "wc_id=" + wc_id);
		int detno = Integer.parseInt(maxdetno.toString()) + 1;
		for (int i = 0; i < lists.size(); i++) {
			map = lists.get(i);
			modelmap = new HashMap<Object, Object>();
			modelmap.put("wd_id", baseDao.getSeqId("WCPLANDETAIL_SEQ"));
			modelmap.put("wd_code", fieldsdata[0]);
			modelmap.put("wd_detno", detno);
			modelmap.put("wd_wcid", wc_id);
			modelmap.put("wd_salecode", map.get("sf_code"));
			modelmap.put("wd_saledetno", map.get("sd_detno"));
			modelmap.put("wd_date", map.get("sd_needdate"));
			modelmap.put("wd_prodcode", map.get("sd_prodcode"));
			modelmap.put("wd_orderkind", "FORECAST");
			sqls.add(SqlUtil.getInsertSqlByMap(modelmap, "WCPlanDetail"));
			detno++;
		}
		baseDao.execute(sqls);
		baseDao.execute("update WCPlanDetail set wd_planqty=(select max(sd_qty) from saleforecastdetail where sd_code=wd_salecode and sd_detno=wd_saledetno ) where wd_wcid="
				+ wc_id + " and wd_salecode<>' ' and wd_saledetno>0 and NVL(wd_makecode,' ')=' ' and NVL(wd_planqty,0)=0");
	}

	@Override
	public void loadAllSaleForecastByCondition(String caller, int wc_id, String condition) {
		Object[] obj1 = baseDao.getFieldsDataByCondition("form", "fo_detailtable,fo_detailcondition", " fo_caller='WCPlanSaleForecastSource'");
		if (obj1 == null) {
			return;
		}
		String BaseCondition = obj1[1].toString();
		BaseCondition = BaseCondition + " AND " + condition;
		SqlRowList sl = baseDao.queryForRowSet("select sf_code,sd_needdate,sd_prodcode,sd_qty qty,sd_detno from "
				+ obj1[0].toString() + " where " + BaseCondition);
		Map<Object, Object> modelmap = null;
		List<String> sqls = new ArrayList<String>();
		// 取时间
		Object maxdetno = baseDao.getFieldDataByCondition("WCPlanDetail", "nvl(max(wd_detno),0)", "wd_wcid=" + wc_id);
		Object[] fieldsdata = baseDao.getFieldsDataByCondition("WCPlan", "wc_code,wc_fromdate,wc_todate", "wc_id=" + wc_id);
		int detno = Integer.parseInt(maxdetno.toString()) + 1;
		while (sl.next()) {
			modelmap = new HashMap<Object, Object>();
			modelmap.put("wd_id", baseDao.getSeqId("WCPLANDETAIL_SEQ"));
			modelmap.put("wd_code", fieldsdata[0]);
			modelmap.put("wd_detno", detno);
			modelmap.put("wd_wcid", wc_id);
			modelmap.put("wd_salecode", sl.getObject("sa_code"));
			modelmap.put("wd_saledetno", sl.getObject("sd_detno"));
			modelmap.put("wd_date", sl.getObject("sd_needdate"));
			modelmap.put("wd_prodcode", sl.getObject("sd_prodcode"));
			modelmap.put("wd_planqty", sl.getObject("qty"));
			modelmap.put("wd_orderkind", "FORECAST");
			sqls.add(SqlUtil.getInsertSqlByMap(modelmap, "WCPlanDetail"));
			detno++;
		}
		baseDao.execute(sqls);
	}

}
