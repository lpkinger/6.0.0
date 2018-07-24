package com.uas.erp.service.pm.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DataListComboDao;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.dao.common.HrJobDao;
import com.uas.erp.model.DataListCombo;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.GridColumns;
import com.uas.erp.model.GridFields;
import com.uas.erp.model.GridPanel;
import com.uas.erp.model.LimitFields;
import com.uas.erp.service.pm.CompareBomService;

@Service
public class CompareBomSeviceImpl implements CompareBomService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private DetailGridDao detailGridDao;

	@Autowired
	private DataListComboDao dataListComboDao;

	@Autowired
	private HrJobDao hrJobDao;

	@Override
	public GridPanel getGridData(String condition, boolean bd_single, boolean bd_difbom, String caller) {
		Integer id = SystemSession.getUser().getEm_id();
		int i;
		String sql = "delete from bomdouble where c8='" + id + "'";
		String[] arr = condition.split(",");
		StringBuffer cond = new StringBuffer();
		baseDao.execute(sql);
		sql = " insert into bomdouble(c1,c8,bd_id) values('子件编号/母件编号','" + id + "'," + baseDao.getSeqId("bomdouble_SEQ") + ")";
		baseDao.execute(sql);
		SqlRowList sqlRowList;
		StringBuffer codes = new StringBuffer();
		Object bomObject, proObject = null;
		for (i = 0; i < arr.length; i++) {
			bomObject = baseDao.getFieldDataByCondition(" bom", "bo_id", " bo_mothercode='" + arr[i] + "'");
			proObject = baseDao.getFieldDataByCondition(" product", "pr_specdescription", " pr_code='" + arr[i] + "'");
			if (proObject != null) {
				bomObject = baseDao
						.getFieldDataByCondition(" bom,product", "bo_id", " bo_mothercode=pr_refno and pr_code='" + arr[i] + "'");
			}
			if (bomObject != null) {
				if (proObject == null) {
					baseDao.callProcedure("mm_SetProdBomStruct", new Object[] { Integer.valueOf(bomObject.toString()), null });
				} else {
					baseDao.callProcedure("mm_SetProdBomStruct",
							new Object[] { Integer.valueOf(bomObject.toString()), proObject.toString() });
					sql = "Select bs_soncode from Bomstruct left join product on pr_code=bs_soncode  where bs_topbomid='"
							+ bomObject.toString() + "' and bs_topmothercode in ('" + condition.replace(",", "','")
							+ "') and pr_specvalue='NOTSPECIFIC'";
					sqlRowList = baseDao.queryForRowSet(sql);
					while (sqlRowList.next()) {
						codes.append(sqlRowList.getString("bs_soncode") + ",");
					}
					if (codes.length() != 0) {
						BaseUtil.showErrorOnSuccess(codes.toString().subSequence(0, codes.length() - 1) + "对应的特征没有定义实体料号!");
					}
				}
			}
			sql = "update bomdouble set c" + Integer.valueOf(i + 2) + "='" + arr[i] + "' where c1='子件编号/母件编号' ";
			baseDao.execute(sql);
			if (bd_single) {
				sql = "insert into bomdouble(bd_id,c1,c8) select bomdouble_SEQ.nextval,bs_soncode,'" + SystemSession.getUser().getEm_id()
						+ "' from( select distinct bs_soncode,'" + SystemSession.getUser().getEm_id()
						+ "' from bomstruct where bs_topmothercode='" + arr[i]
						+ "' and bs_level='..2' and bs_soncode not in (select c1 from bomdouble where c8='"
						+ SystemSession.getUser().getEm_id() + "'))A";
			} else {
				sql = "insert into bomdouble(bd_id,c1,c8) select bomdouble_SEQ.nextval,bs_soncode,'" + SystemSession.getUser().getEm_id()
						+ "' from (select distinct bs_soncode,'" + SystemSession.getUser().getEm_id()
						+ "' from bomstruct where bs_topmothercode='" + arr[i]
						+ "' and bs_soncode not in (select c1 from bomdouble where c8='" + SystemSession.getUser().getEm_id() + "'))A";
			}
			baseDao.execute(sql);
			if (bd_single) {
				sql = "select bs_soncode ,sum(bs_baseqty) as bs_baseqty  from bomstruct where bs_topmothercode='" + arr[i]
						+ "' and bs_level='..2' group by bs_soncode  ";
			} else {
				sql = "select bs_soncode ,sum(bs_baseqty) as bs_baseqty  from bomstruct where bs_topmothercode='" + arr[i]
						+ "' group by bs_soncode ";
			}
			sqlRowList = baseDao.queryForRowSet(sql);
			while (sqlRowList.next()) {
				baseDao.execute("update bomdouble set c" + Integer.valueOf(i + 2) + "='" + sqlRowList.getObject("bs_baseqty").toString()
						+ "' where c8='" + SystemSession.getUser().getEm_id() + "' and c1='" + sqlRowList.getString("bs_soncode") + "'");
			}
		}

		// grid 获得数据
		if (bd_difbom) {
			codes.setLength(0);
			cond.append("c8='"+ SystemSession.getUser().getEm_id() + "' and not(");
			for (i = 1; i < arr.length; i++) {
				if (i == 1) {
					cond.append("NVL(c" + Integer.valueOf(i + 1) + ",-1)=NVL(c" + Integer.valueOf(i + 2) + ",-1)");
				} else {
					cond.append(" and NVL(c" + Integer.valueOf(i + 1) + ",-1)=NVL(c" + Integer.valueOf(i + 2) + ",-1)");
				}
			}
			cond.append(") ");
		} else {
			cond.append("c8='"+ SystemSession.getUser().getEm_id() + "'");
		}
		cond.append(" order by bd_id");
		GridPanel gridPanel = new GridPanel();
		List<DetailGrid> detailGrids = detailGridDao.getDetailGridsByCaller(caller, SystemSession.getUser().getEm_master());
		if (detailGrids != null && detailGrids.size() > 0) {
			List<DataListCombo> combos = dataListComboDao.getComboxsByCaller(caller, SystemSession.getUser().getEm_master());
			List<GridFields> fields = new ArrayList<GridFields>();// grid
																	// store的字段fields
			List<GridColumns> columns = new ArrayList<GridColumns>();// grid的列信息columns
			List<LimitFields> limits = new ArrayList<LimitFields>();
			if (!"admin".equals(SystemSession.getUser().getEm_type())) {
				limits = hrJobDao.getLimitFieldsByType(caller, null, 0, SystemSession.getUser().getEm_defaulthsid(), SystemSession
						.getUser().getEm_master());
			}
			gridPanel.setLimits(limits);// 权限控制字段
			for (DetailGrid grid : detailGrids) {
				// 从数据库表detailgrid的数据，通过自定义的构造器，转化为extjs识别的fields格式，详情可见GridFields的构造函数
				fields.add(new GridFields(grid));
				columns.add(new GridColumns(grid, combos));
			}
			gridPanel.setGridColumns(columns);
			gridPanel.setGridFields(fields);
			if (!condition.equals("")) {
				gridPanel.setDataString(baseDao.getDataStringByDetailGrid(detailGrids,cond.toString(),1,10000));
			}
		}
		return gridPanel;
	}
}
