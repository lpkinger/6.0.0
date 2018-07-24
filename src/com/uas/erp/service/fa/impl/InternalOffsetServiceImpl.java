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
import com.uas.erp.service.common.ExcelService;
import com.uas.erp.service.fa.InternalOffsetService;

@Service("internalOffsetService")
public class InternalOffsetServiceImpl implements InternalOffsetService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private ExcelService excelService;

	@Override
	public List<Map<String, Object>> getInternalOffsets(int yearmonth) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		SqlRowList rs = baseDao
				.queryForRowSet("select ss_mastercode,ss_mastername from SubsidiarySet where nvl(ss_enable,0)=1 and nvl(ss_mastercode,' ') <> ' ' order by ss_detno");
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			String code = rs.getGeneralString("ss_mastercode");
			map.put("ss_mastercode", code);
			map.put("ss_mastername", rs.getGeneralString("ss_mastername"));
			map.put("io_id",
					baseDao.getFieldDataByCondition("InternalOffset", "io_id", "io_yearmonth = " + yearmonth + " and io_mastercode = '"
							+ code + "'"));
			result.add(map);
		}
		return result;
	}

	@Override
	public Map<String, Object> getInternalOffset(String fields, Integer yearmonth, String mastercode) {
		Map<String, Object> data = new HashMap<String, Object>();
		Object[] report = baseDao.getFieldsDataByCondition("InternalOffset", fields, "io_yearmonth = " + yearmonth
				+ " and io_mastercode = '" + mastercode + "'");
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
	public String autoCatchInternalOffset(int yearmonth, String currency) {
		Object ym = baseDao.getFieldDataByCondition("PeriodsDetail", "min(PD_DETNO)", "pd_code='MONTH-A' and pd_status=0");
		if (yearmonth < Integer.parseInt(ym.toString())) {
			BaseUtil.showError("总账会计期间[" + yearmonth + "]已结账，无需获取数据！");
		}
		String res = baseDao.callProcedure("FA_CATCHINTERNALOFFSET", new Object[] { yearmonth, currency });
		if (res != null && res.length() > 0) {
			return res;
		}
		return null;
	}

	@Override
	public void updateInternalOffset(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		Object status = baseDao.getFieldDataByCondition("InternalOffset", "io_statuscode", "io_id=" + store.get("io_id"));
		if ("AUDITED".equals(status)) {
			BaseUtil.showError("已结账的总账期间数据不允许修改！");
		}
		Object io_id = store.get("io_id");
		Object zsrate = store.get("io_zsrate");
		Object oldzsrate = baseDao.getFieldDataByCondition("InternalOffset", "nvl(io_zsrate,0)", "io_id=" + io_id);
		if (StringUtil.hasText(store.get("io_id"))) {
			baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "InternalOffset", "io_id"));
		} else {
			int id = baseDao.getSeqId("InternalOffset_SEQ");
			store.put("io_id", id);
			for (Map<Object, Object> map : gstore) {
				map.put("iod_ioid", id);
			}
			baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "InternalOffsetDet", "iod_id"));
		}
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "InternalOffsetDet", "iod_id"));
		if (zsrate != null && oldzsrate != null) {
			if (Double.parseDouble(oldzsrate.toString()) != Double.parseDouble(zsrate.toString())) {
				baseDao.execute("update InternalOffsetDET set iod_ZSRATE=" + zsrate + " WHERE iod_ioid=" + io_id);
				baseDao.execute("update InternalOffsetDET set iod_BBAMOUNT=ROUND(NVL(iod_AMOUNT,0)*iod_ZSRATE,2) WHERE iod_ioid=" + io_id
						+ " and nvl(iod_ZSRATE,0)<>0");
			}
		}
		baseDao.execute("update InternalOffsetDET set (iod_mastercode,iod_mastername,iod_currency,iod_yearmonth)=(select io_mastercode,io_mastername,io_currency,io_yearmonth from InternalOffset where io_id=iod_ioid)"
				+ " where iod_ioid=" + io_id);
		// 记录操作
		baseDao.logger.update("InternalOffset", "io_id", io_id);
	}

	/**
	 * 准备数据，用于生成excel
	 */
	public HSSFWorkbook exportMultitabExcel(String yearmonth) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		Object[] mastername = baseDao.getFieldsDataByCondition("SubsidiarySet", "wm_concat(ss_mastername),wm_concat(ss_mastercode)",
				"nvl(ss_enable,0)=1 and nvl(ss_mastercode,' ') <> ' ' order by ss_detno");
		String dgCaller = "InternalOffset";
		String sheetNames = String.valueOf(mastername[0]);
		String codes = "'" + String.valueOf(mastername[1]).replaceAll(",", "','") + "'";
		String ioId = String.valueOf(baseDao.getFieldDataByCondition("InternalOffset", "wm_concat(io_id)", "io_yearmonth='" + yearmonth
				+ "' and io_mastercode in (" + codes + ")"));
		if (ioId == null || "null".equals(ioId)) {
			return null;
		}
		String[] ids = ioId.split(",");
		Object[] fieldAndCaption = baseDao.getFieldsDataByCondition("detailgrid",
				"wm_concat(dg_field),wm_concat(dg_caption),wm_concat(dg_type)", "dg_caller='" + dgCaller
						+ "' and dg_width>0 order by dg_sequence");
		String fields = String.valueOf(fieldAndCaption[0]);
		String gridTitle = String.valueOf(fieldAndCaption[1]);
		String gridType = String.valueOf(fieldAndCaption[2]);
		Map<String, Object> grid = new HashMap<String, Object>();
		for (String id : ids) {
			String sql = "select " + fields + " from internaloffsetdet where iod_ioid=" + id + " order by iod_detno";
			List<Map<String, Object>> list = baseDao.queryForList(sql);
			Object obj = baseDao.getFieldDataByCondition("InternalOffset", "io_mastername", "io_yearmonth='" + yearmonth + "' and io_id="
					+ id);
			grid.put(String.valueOf(obj), list);
		}
		workbook = excelService.saveTabPanelAsExcel(sheetNames, grid, gridTitle, gridType);
		return workbook;
	}

	/**
	 * 校验是否存在相应的ids
	 * 
	 * @param yearmonth
	 * @return
	 */
	public boolean valid(String yearmonth) {
		Object[] mastername = baseDao.getFieldsDataByCondition("SubsidiarySet", "wm_concat(ss_mastername),wm_concat(ss_mastercode)",
				"nvl(ss_enable,0)=1 and nvl(ss_mastercode,' ') <> ' ' order by ss_detno");
		String codes = "'" + String.valueOf(mastername[1]).replaceAll(",", "','") + "'";
		String ioId = String.valueOf(baseDao.getFieldDataByCondition("InternalOffset", "wm_concat(io_id)", "io_yearmonth='" + yearmonth
				+ "' and io_mastercode in (" + codes + ")"));
		if (ioId == null || "null".equals(ioId)) {
			return false;
		}
		return true;
	}

}
