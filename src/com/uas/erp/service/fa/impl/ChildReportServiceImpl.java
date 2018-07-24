package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.service.common.ExcelService;
import com.uas.erp.service.fa.ChildReportService;

@Service("childReportService")
public class ChildReportServiceImpl implements ChildReportService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private ExcelService excelService;
	@Autowired
	private VoucherDao voucherDao;

	@Override
	public List<Map<String, Object>> getChildReports(int yearmonth, String fatype, String kind) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if ("子报表".equals(kind)) {
			SqlRowList rs = baseDao
					.queryForRowSet("select ss_mastercode,ss_mastername from SubsidiarySet where nvl(ss_enable,0)=1 and nvl(ss_mastercode,' ') <> ' ' order by ss_detno");
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				String code = rs.getGeneralString("ss_mastercode");
				map.put("ss_mastercode", code);
				map.put("ss_mastername", rs.getGeneralString("ss_mastername"));
				map.put("cr_id",
						baseDao.getFieldDataByCondition("ChildReport", "cr_id", "cr_yearmonth = " + yearmonth + " and cr_fatype = '"
								+ fatype + "' and cr_mastercode = '" + code + "' and nvl(cr_kind,'子报表') = '" + kind + "'"));
				result.add(map);
			}
		} else if ("集团报表".equals(kind)) {
			SqlRowList rs = baseDao
					.queryForRowSet("select ss_mastercode,ss_mastername,ss_name from CONSOLIDATEDCOP_VIEW order by ss_detno");
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				String code = rs.getGeneralString("ss_mastercode");
				map.put("ss_mastercode", code);
				map.put("ss_mastername", rs.getGeneralString("ss_mastername"));
				map.put("cr_id",
						baseDao.getFieldDataByCondition("ChildReport", "cr_id", "cr_yearmonth = " + yearmonth + " and cr_fatype = '"
								+ fatype + "' and cr_mastercode = '" + code + "' and nvl(cr_kind,'子报表') = '" + kind + "'"));
				result.add(map);
			}
		}
		return result;
	}

	@Override
	public Map<String, Object> getChildReport(String fields, Integer yearmonth, String mastercode, String fatype, String kind) {
		Map<String, Object> data = new HashMap<String, Object>();
		Object[] report = baseDao.getFieldsDataByCondition("ChildReport", fields, "cr_yearmonth = " + yearmonth + " and cr_fatype = '"
				+ fatype + "' and cr_mastercode = '" + mastercode + "' and nvl(cr_kind,'子报表') = '" + kind + "'");
		String[] strs = fields.split(",");
		for (int i = 0; i < strs.length; i++) {
			if (report != null) {
				data.put(strs[i], report[i]);
			} else {
				data.put(strs[i], null);
			}
		}
		return data;
	}

	@Override
	public String autoCatchReport(int yearmonth, String currency, String fatype, String kind) {
		Object ym = baseDao.getFieldDataByCondition("PeriodsDetail", "min(PD_DETNO)", "pd_code='MONTH-A' and pd_status=0");
		if (yearmonth < Integer.parseInt(ym.toString())) {
			BaseUtil.showError("总账会计期间[" + yearmonth + "]已结账，无需获取数据！");
		}
		if ("子报表".equals(kind)) {
			String res = baseDao.callProcedure("FA_CATCHCHILDREPORT", new Object[] { yearmonth, currency });
			if (res != null && res.length() > 0) {
				return res;
			}
		} else if ("集团报表".equals(kind)) {
			String res = baseDao.callProcedure("FA_CATCHCONSOLIDATED", new Object[] { yearmonth, currency });
			if (res != null && res.length() > 0) {
				return res;
			}
		}
		return null;
	}

	@Override
	public void updateChildReport(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		Object status = baseDao.getFieldDataByCondition("ChildReport", "cr_statuscode", "cr_id=" + store.get("cr_id"));
		if ("AUDITED".equals(status)) {
			BaseUtil.showError("已结账的总账期间数据不允许修改！");
		}
		Object cr_id = store.get("cr_id");
		Object zsrate = store.get("cr_zsrate");
		Object fatype = store.get("cr_fatype");
		Object oldzsrate = baseDao.getFieldDataByCondition("ChildReport", "nvl(cr_zsrate,0)", "cr_id=" + cr_id);
		if (StringUtil.hasText(store.get("cr_id"))) {
			baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "ChildReport", "cr_id"));
		} else {
			int id = baseDao.getSeqId("CHILDREPORT_SEQ");
			store.put("cr_id", id);
			for (Map<Object, Object> map : gstore) {
				map.put("crd_crid", id);
			}
			baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "ChildReportDet", "crd_id"));
		}
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "ChildReportDet", "crd_id"));
		if (zsrate != null && oldzsrate != null) {
			if (Double.parseDouble(oldzsrate.toString()) != Double.parseDouble(zsrate.toString())) {
				baseDao.execute("update CHILDREPORTDET set CRD_ZSRATE=" + zsrate + " WHERE crd_crid=" + cr_id);
				if ("资产负债表".equals(fatype)) {
					baseDao.execute("UPDATE CHILDREPORTDET SET CRD_BBAMOUNT2=ROUND(NVL(CRD_AMOUNT2,0)*CRD_ZSRATE,2),CRD_RIGHTBBAMOUNT2=ROUND(NVL(CRD_RIGHTAMOUNT2,0)*CRD_ZSRATE,2) WHERE CRD_CRID="
							+ cr_id);
				} else if ("利润表".equals(fatype)) {
					baseDao.execute("UPDATE CHILDREPORTDET SET CRD_BBAMOUNT1=ROUND(NVL(CRD_AMOUNT1,0)*CRD_ZSRATE,2) WHERE CRD_CRID="
							+ cr_id);
					SqlRowList rs = baseDao
							.queryForRowSet("SELECT cr_mastercode,cr_yearmonth,to_char(ADD_MONTHS(round(to_date(cr_yearmonth,'yyyymm'),'MM'),-1),'yyyymm') preym FROM CHILDREPORT WHERE cr_id="
									+ cr_id + " and cr_fatype='利润表' and cr_kind='子报表'");
					if (rs.next()) {
						int count = baseDao.getCount("SELECT COUNT(1) from CHILDREPORTDET WHERE CRD_MASTERCODE='"
								+ rs.getObject("cr_mastercode") + "' AND CRD_YEARMONTH=" + rs.getObject("preym")
								+ " AND CRD_FATYPE='利润表' AND CRD_KIND='子报表'");
						if (count > 0) {
							baseDao.execute("UPDATE CHILDREPORTDET A SET CRD_BBAMOUNT2=ROUND(NVL((SELECT CRD_BBAMOUNT2 FROM CHILDREPORTDET B WHERE B.CRD_YEARMONTH="
									+ rs.getObject("preym")
									+ " AND A.CRD_FATYPE=A.CRD_FATYPE AND A.CRD_KIND=B.CRD_KIND AND B.CRD_MASTERCODE=A.CRD_MASTERCODE AND A.CRD_STANDARD=B.CRD_STANDARD),0)+NVL(CRD_BBAMOUNT1,0),2) WHERE CRD_CRID="
									+ cr_id);
						}
					}
					baseDao.execute("UPDATE CHILDREPORTDET SET CRD_BBAMOUNT2=ROUND(NVL(CRD_AMOUNT2,0)*CRD_ZSRATE,2) WHERE CRD_CRID="
							+ cr_id);
				}
			}
		}
		baseDao.execute("update CHILDREPORTDET set (crd_mastercode,crd_mastername,crd_currency,crd_yearmonth,crd_kind)=(select cr_mastercode,cr_mastername,cr_currency,cr_yearmonth,cr_kind from ChildReport where cr_id=crd_crid)"
				+ " where crd_crid=" + cr_id);
		baseDao.procedure("FA_UPDATECHILDREPORT", new Object[] { cr_id });
		// 记录操作
		baseDao.logger.update("ChildReport", "cr_id", cr_id);
	}

	@Override
	public String countConsolidated(int yearmonth, String currency, String fatype) {
		Object ym = baseDao.getFieldDataByCondition("PeriodsDetail", "min(PD_DETNO)", "pd_code='MONTH-A' and pd_status=0");
		if (yearmonth < Integer.parseInt(ym.toString())) {
			BaseUtil.showError("总账会计期间[" + yearmonth + "]已结账，无需获取数据！");
		}
		String res = baseDao.callProcedure("FA_COUNTCONSOLIDATED", new Object[] { yearmonth, currency });
		if (res != null && res.length() > 0) {
			return res;
		}
		return null;
	}

	/**
	 * 数据准备，用于生成导出的excel
	 */
	public HSSFWorkbook exportMultitabExcel(String yearmonth, String fatype, String kind) {
		HSSFWorkbook workbook = null;
		Object mastername = null;
		String dgCaller = null;
		if ("子报表".equals(kind)) {
			mastername = baseDao.getFieldDataByCondition("SubsidiarySet", "wm_concat(ss_mastername)",
					"nvl(ss_enable,0)=1 and nvl(ss_mastercode,' ') <> ' ' order by ss_detno");
			dgCaller = "ChildReport";
		} else if ("集团报表".equals(kind)) {
			List<Map<String, Object>> shrnameList = baseDao
					.queryForList("select wm_concat(shr_name) shr_name from (select distinct shr_code,shr_name,ss_level FROM SHAREHOLDERSRATESET,SUBSIDIARYSET where shr_pid=ss_id and nvl(ss_enable,0)=1 order by ss_level desc)");
			if (shrnameList != null && !shrnameList.isEmpty()) {
				mastername = shrnameList.get(0).get("SHR_NAME");
			}
			dgCaller = "Consolidated";
		}
		String sheetNames = String.valueOf(mastername);
		String names = "'" + sheetNames.replaceAll(",", "','") + "'";
		String crId = String.valueOf(baseDao.getFieldDataByCondition("childreport", "wm_concat(cr_id)", "cr_yearmonth='" + yearmonth
				+ "' and cr_fatype='" + fatype + "' and nvl(cr_kind,'" + kind + "')='" + kind + "' and cr_mastername in (" + names + ")"));
		if (crId == null || "null".equals(crId)) {
			return null;
		}
		String[] ids = crId.split(",");
		Object[] fieldAndCaption = baseDao.getFieldsDataByCondition("detailgrid",
				"wm_concat(dg_field),wm_concat(dg_caption),wm_concat(dg_type)", "dg_caller='" + dgCaller
						+ "' and dg_width>0 order by dg_sequence");
		String fields = String.valueOf(fieldAndCaption[0]);
		String gridTitle = String.valueOf(fieldAndCaption[1]);
		String gridType = String.valueOf(fieldAndCaption[2]);
		Map<String, Object> grid = new HashMap<String, Object>();
		for (String id : ids) {
			String sql = "select " + fields + " from childreportdet where crd_fatype='" + fatype + "' and crd_crid=" + id
					+ " order by crd_detno";
			List<Map<String, Object>> list = baseDao.queryForList(sql);
			Object obj = baseDao.getFieldDataByCondition("childreport", "cr_mastername", "cr_yearmonth='" + yearmonth + "' and cr_fatype='"
					+ fatype + "' and nvl(cr_kind,'" + kind + "')='" + kind + "' and cr_id=" + id);
			grid.put(String.valueOf(obj), list);
		}
		workbook = excelService.saveTabPanelAsExcel(sheetNames, grid, gridTitle, gridType);
		return workbook;
	}

	/**
	 * 验证数据库中是否存在相应的id
	 * 
	 * @param yearmonth
	 * @param fatype
	 * @param kind
	 * @return
	 */
	public boolean valid(String yearmonth, String fatype, String kind) {
		Object mastername = null;
		String dgCaller = null;
		if ("子报表".equals(kind)) {
			mastername = baseDao.getFieldDataByCondition("SubsidiarySet", "wm_concat(ss_mastername)",
					"nvl(ss_enable,0)=1 and nvl(ss_mastercode,' ') <> ' ' order by ss_detno");
			dgCaller = "ChildReport";
		} else if ("集团报表".equals(kind)) {
			List<Map<String, Object>> shrnameList = baseDao
					.queryForList("select wm_concat(shr_name) shr_name from (select distinct shr_code,shr_name,ss_level FROM SHAREHOLDERSRATESET,SUBSIDIARYSET where shr_pid=ss_id and nvl(ss_enable,0)=1 order by ss_level desc)");
			if (shrnameList != null && !shrnameList.isEmpty()) {
				mastername = shrnameList.get(0).get("SHR_NAME");
			}
			dgCaller = "Consolidated";
		}
		String sheetNames = String.valueOf(mastername);
		String names = "'" + sheetNames.replaceAll(",", "','") + "'";
		String crId = String.valueOf(baseDao.getFieldDataByCondition("childreport", "wm_concat(cr_id)", "cr_yearmonth='" + yearmonth
				+ "' and cr_fatype='" + fatype + "' and nvl(cr_kind,'" + kind + "')='" + kind + "' and cr_mastername in (" + names + ")"));
		if (crId == null || "null".equals(crId)) {
			return false;
		}
		return true;
	}

	@Override
	public void saveReportYearBegin(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ReportYearBegin",
				"yb_year='" + store.get("yb_year") + "' and yb_id<>" + store.get("yb_id") + " and yb_kind='资产负债表'");
		if (!bool) {
			BaseUtil.showError("已存在" + store.get("yb_year") + "资产负债表的年初数据！");
		}
		bool = baseDao.checkByCondition("ReportYearBegin", "yb_id<>" + store.get("yb_id") + " and yb_kind='利润表'");
		if (!bool) {
			BaseUtil.showError("已存在" + store.get("yb_year") + "利润表的本年累计数！");
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { formStore, gridStore });
		// 保存APCheck
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "ReportYearBegin"));
		baseDao.execute(SqlUtil.getInsertSqlbyList(grid, "ReportYearBeginDet", "ybd_id"));
		baseDao.execute("update ReportYearBeginDet set ybd_year=(select yb_year from ReportYearBegin where yb_id=ybd_ybid) where ybd_ybid="
				+ store.get("yb_id"));
		baseDao.logger.save(caller, "yb_id", store.get("yb_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { formStore, gridStore });
	}

	@Override
	public void updateReportYearBegin(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ReportYearBegin",
				"yb_year='" + store.get("yb_year") + "' and yb_id<>" + store.get("yb_id") + " and yb_kind='资产负债表'");
		if (!bool) {
			BaseUtil.showError("已存在" + store.get("yb_year") + "资产负债表的年初数据！");
		}
		bool = baseDao.checkByCondition("ReportYearBegin", "yb_id<>" + store.get("yb_id") + " and yb_kind='利润表'");
		if (!bool) {
			BaseUtil.showError("已存在" + store.get("yb_year") + "利润表的本年累计数！");
		}
		int now = voucherDao.getNowPddetno("MONTH-A");
		if ("利润表".equals(store.get("yb_kind")) && now != Integer.parseInt(store.get("yb_year").toString())) {
			BaseUtil.showError("只能导入当前总账期间" + now + "利润表的本年累计数！");
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, store, gstore);
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "ReportYearBegin", "yb_id"));
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "ReportYearBeginDet", "ybd_id"));
		baseDao.execute("update ReportYearBeginDet set ybd_year=(select yb_year from ReportYearBegin where yb_id=ybd_ybid) where ybd_ybid="
				+ store.get("yb_id"));
		// 记录操作
		baseDao.logger.update(caller, "yb_id", store.get("yb_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, store, gstore);
	}

}
