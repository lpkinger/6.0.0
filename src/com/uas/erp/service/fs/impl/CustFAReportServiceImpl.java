package com.uas.erp.service.fs.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.fs.CustFAReportService;

@Service
public class CustFAReportServiceImpl implements CustFAReportService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCustFAReport(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		Object fatype = store.get("cr_fatype");
		if (StringUtil.hasText(fatype) && grid.size() > 0) {
			StringBuffer sb = new StringBuffer();
			for (Map<Object, Object> s : grid) {
				if (StringUtil.hasText(s.get("crd_fsname"))) {
					if (!fatype.equals(s.get("crd_fsname"))) {
						sb.append(s.get("crd_detno") + "；");
					}
				}
			}
			if (sb.length() > 0) {
				BaseUtil.showError("明细行报表名称与主表报表类型不一致！行号：" + sb.toString());
			}
		}
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "CUSTFAREPORT"));
		baseDao.execute(SqlUtil.getInsertSqlbyList(grid, "CUSTFAREPORTDETAIL", "crd_id"));
		Object crid = store.get("cr_id");
		baseDao.execute("update CUSTFAREPORTDETAIL set (CRD_CUNAME, CRD_YEARMONTH)=(SELECT CR_CUNAME,CR_YEARMONTH FROM CUSTFAREPORT WHERE crd_crid=cr_id) where crd_crid="
				+ crid);
		baseDao.execute("update CUSTFAREPORTDETAIL set CRD_FSCODE='A01' WHERE trim(CRD_FSNAME)='资产负债表' and crd_crid=" + crid);
		baseDao.execute("update CUSTFAREPORTDETAIL set CRD_FSCODE='P01' WHERE trim(CRD_FSNAME)='损益表' and crd_crid=" + crid);
		baseDao.execute("update CUSTFAREPORTDETAIL set CRD_FSCODE='C01' WHERE trim(CRD_FSNAME)='现金流量表' and crd_crid=" + crid);
		baseDao.procedure("SP_COUNTFAITEMS_CUST", new Object[] { store.get("cr_yearmonth"), store.get("cr_cuname") });
		baseDao.procedure("SP_COUNTCREDITTARGETSITEMS", new Object[] { store.get("cr_yearmonth"), store.get("cr_cuname") });
		baseDao.logger.save(caller, "cr_id", crid);
	}

	@Override
	public void updateCustFAReport(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		Object fatype = store.get("cr_fatype");
		if (StringUtil.hasText(fatype) && grid.size() > 0) {
			StringBuffer sb = new StringBuffer();
			for (Map<Object, Object> s : grid) {
				if (StringUtil.hasText(s.get("crd_fsname"))) {
					if (!fatype.equals(s.get("crd_fsname"))) {
						sb.append(s.get("crd_detno") + "；");
					}
				}
			}
			if (sb.length() > 0) {
				BaseUtil.showError("明细行报表名称与主表报表类型不一致！行号：" + sb.toString());
			}
		}
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "CUSTFAREPORT", "cr_id"));
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(grid, "CUSTFAREPORTDETAIL", "crd_id"));
		Object crid = store.get("cr_id");
		baseDao.execute("update CUSTFAREPORTDETAIL set (CRD_CUNAME, CRD_YEARMONTH)=(SELECT CR_CUNAME,CR_YEARMONTH FROM CUSTFAREPORT WHERE crd_crid=cr_id) where crd_crid="
				+ crid);
		baseDao.execute("update CUSTFAREPORTDETAIL set CRD_FSCODE='A01' WHERE trim(CRD_FSNAME)='资产负债表' and crd_crid=" + crid);
		baseDao.execute("update CUSTFAREPORTDETAIL set CRD_FSCODE='P01' WHERE trim(CRD_FSNAME)='损益表' and crd_crid=" + crid);
		baseDao.execute("update CUSTFAREPORTDETAIL set CRD_FSCODE='C01' WHERE trim(CRD_FSNAME)='现金流量表' and crd_crid=" + crid);
		baseDao.procedure("SP_COUNTFAITEMS_CUST", new Object[] { store.get("cr_yearmonth"), store.get("cr_cuname") });
		baseDao.procedure("SP_COUNTCREDITTARGETSITEMS", new Object[] { store.get("cr_yearmonth"), store.get("cr_cuname") });
		baseDao.logger.save(caller, "cr_id", crid);
	}

	@Override
	public void deleteCustFAReport(int id, String caller) {
		baseDao.execute("delete from CUSTFAREPORT where CR_ID=" + id);
		baseDao.execute("delete from CUSTFAREPORTDETAIL where CRD_CRID=" + id);
		baseDao.execute("delete from faitems where (fi_cuname,fi_year)=(select cr_cuname,cr_yearmonth from CUSTFAREPORT where cr_id=" + id
				+ ")");
		baseDao.execute("delete from credittargetsitems where (cti_cuname,cti_year)=(select cr_cuname,cr_yearmonth from CUSTFAREPORT where cr_id="
				+ id + ")");
		baseDao.logger.delete(caller, "cr_id", id);
	}

	@Override
	public void count(int cr_id, String caller) {
		SqlRowList rs = baseDao.queryForRowSet(
				"select cr_yearmonth, cr_cuname from CustFAReport where cr_id=? and nvl(cr_yearmonth,0)<>0 and nvl(cr_cuname,' ')<>' '",
				cr_id);
		if (rs.next()) {
			int yearmonth = rs.getGeneralInt("cr_yearmonth");
			String custname = rs.getGeneralString("cr_cuname");
			baseDao.procedure("SP_COUNTFAITEMS_CUST", new Object[] { yearmonth, custname });
			baseDao.procedure("SP_COUNTCREDITTARGETSITEMS", new Object[] { yearmonth, custname });
			baseDao.logger.others("财务项目计算", "计算成功", caller, "cr_id", cr_id);
		}
	}
}
