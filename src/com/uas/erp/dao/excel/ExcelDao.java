package com.uas.erp.dao.excel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.mockrunner.jdbc.SQLUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.excel.ExcelCell;
import com.uas.erp.model.excel.SheetTabIdOrderInfo;

@Repository("ExcelDao")
public class ExcelDao extends BaseDao{
	
	
	public void updateRowsAddSpanYY(Integer sheetid_tpl, Integer rowSpan, Integer row, Integer col, int maxcol,
			String sheetcelltable_tpl) {
		String sql = "update "+sheetcelltable_tpl+" a " + 
    			" set a.cellrow=a.cellrow+"+rowSpan+ 
    			" where exists(select 1 from (select cellid from "+sheetcelltable_tpl+" where cellsheetid = "+sheetid_tpl+" and cellrow>="+row+" and (cellcol between "+col+" and "+maxcol+") order by cellrow desc) b " + 
    			" where b.cellid = a.cellid)";
		execute(sql);
	}

	public void updateColsAddSpanXX(Integer sheetid_tpl, Integer colSpan, Integer row, int maxrow, Integer col,
			String sheetcelltable_tpl) {
		String sql = "update "+sheetcelltable_tpl+" a " + 
				" set a.cellcol=a.cellcol+"+colSpan+ 
				" where exists(select 1 from (select cellid from "+sheetcelltable_tpl+" where cellsheetid = "+sheetid_tpl+" and cellcol>="+col+" and (cellrow between "+row+" and "+maxrow+") order by cellcol desc) b " + 
				" where b.cellid = a.cellid)";
		execute(sql);
	}

	public void insertColumns(Integer sheetid_tpl, Integer col, Integer colSpan, String sheetcelltable_tpl) {
		String sql = "update "+sheetcelltable_tpl+" a " + 
				" set a.cellcol=a.cellcol+"+colSpan+ 
				" where exists(select 1 from (select cellid from "+sheetcelltable_tpl+" where cellsheetid = "+sheetid_tpl+" and cellcol>="+col+"  order by cellcol desc) b " + 
				" where b.cellid = a.cellid)";
		execute(sql);
	}

	public void insertRows(Integer sheetid_tpl, Integer row, Integer rowSpan, String sheetcelltable_tpl) {
		String sql = "update "+sheetcelltable_tpl+" a " + 
				" set a.cellrow=a.cellrow+"+rowSpan+ 
				" where exists(select 1 from (select cellid from "+sheetcelltable_tpl+" where cellsheetid = "+sheetid_tpl+" and cellrow>="+row+"  order by cellrow desc) b " + 
				" where b.cellid = a.cellid)";
		execute(sql);
	}

	public void deleteCellsXXYY(Integer sheetid_tpl, Integer minrow, Integer maxrow, Integer mincol, Integer maxcol,
			String sheetcelltable_tpl) {
		deleteByCondition(sheetcelltable_tpl, 
		"cellsheetid="+sheetid_tpl+" and (cellrow between "+minrow+" and "+maxrow+") "
		+ " and (cellcol between "+mincol+" and "+maxcol+")");
	}

	public void updateRowsWithSpanYY(Integer sheetid_tpl, int rowSpan, Integer maxrow, Integer mincol, Integer maxcol,
			String sheetcelltable_tpl) {
		String sql = "update "+sheetcelltable_tpl+" a " + 
				" set a.cellrow=a.cellrow-"+rowSpan+ 
				" where exists(select 1 from (select cellid from "+sheetcelltable_tpl+" where cellsheetid = "+sheetid_tpl+" and cellrow>"+maxrow+" and (cellcol between "+mincol+" and "+maxcol+") order by cellrow desc) b " + 
				" where b.cellid = a.cellid)";
		execute(sql);
	}

	public void updateColsWithSpanXX(Integer sheetid_tpl, int colSpan, Integer minrow, Integer maxrow, Integer maxcol,
			String sheetcelltable_tpl) {
		String sql = "update "+sheetcelltable_tpl+" a " + 
				" set a.cellcol=a.cellcol-"+colSpan+ 
				" where exists(select 1 from (select cellid from "+sheetcelltable_tpl+" where cellsheetid = "+sheetid_tpl+" and cellcol>"+maxcol+" and (cellrow between "+minrow+" and "+maxrow+") order by cellcol desc) b " + 
				" where b.cellid = a.cellid)";
		execute(sql);
	
	}

	public void updateColsWithSpan(Integer sheetid_tpl, int totalSpan, Integer maxcol, String sheetcelltable_tpl) {
		String sql = 
				"update "+sheetcelltable_tpl+" a " + 
				" set a.cellcol=a.cellcol-"+totalSpan+ 
				" where exists(select 1 from (select cellid from "+sheetcelltable_tpl+" where cellsheetid = "+sheetid_tpl+" and cellcol>="+maxcol+"  order by cellcol desc) b " + 
				" where b.cellid = a.cellid)";
		execute(sql);
	}

	public void updateRowsWithSpan(Integer sheetid_tpl, int totalSpan, Integer maxrow, String sheetcelltable_tpl) {
		String sql = "update "+sheetcelltable_tpl+" a " + 
				" set a.cellrow=a.cellrow-"+totalSpan+ 
				" where exists(select 1 from (select cellid from "+sheetcelltable_tpl+" where cellsheetid = "+sheetid_tpl+" and cellrow>="+maxrow+"  order by cellrow desc) b " + 
				" where b.cellid = a.cellid)";
		execute(sql);
	}

	public List<ExcelCell> loadAllCellsByTabAndCalCells(Integer startCellId, Integer size, String sheetcelltable,
			Integer sheetid, List<Integer> otherSheetIds) {
 	    String ids = StringUtils.join(otherSheetIds.toArray(), ",");  
		String sql = "select * from (select * from "+sheetcelltable+" where ( "
				    +"cellsheetid="+sheetid+" or (cellcal=1 and cellsheetid in ("+ids+")) "
				    +") and cellid > "+startCellId+" order by cellid asc ) where rownum<"+size;
		
		return query(sql, ExcelCell.class);
	}

	public List<ExcelCell> loadCornerCalCellsOnDemand(Integer startCellId, Integer sheetid, Integer size,
			String sheetcelltable, List<Integer> tabIds) {
 	    String ids = StringUtils.join(tabIds.toArray(), ",");  
		String sql = "select * from ( select * from "+sheetcelltable+" where cellid>"+startCellId+
 	                 " and ( (cellsheetid="+sheetid+" and(cellrow=0 or cellcol=0))  or (cellcal=1 and cellsheetid in ("+ids+"))) "+
 	                 " order by cellid asc ) where rownum<"+size;
		return query(sql, ExcelCell.class);
	}

	public List<SheetTabIdOrderInfo> findSheetTabIdOrderInfoByDocument(Integer sheetfileid) {
		String sql = "select sheetid tab_id,sheetorder tab_order from ExcelSheet "+
	                 "where sheetfileid="+sheetfileid+"  order by sheetorder asc";
		return query(sql, SheetTabIdOrderInfo.class);
	}

	public List<ExcelCell> loadNotCalCellsOfRawDataOnDemand(Integer startCellId, Integer getsheetid, Integer size,
			String getsheetcelltable) {
		String sql = "select * from ( SELECT * FROM "+getsheetcelltable+ 
				     " WHERE (cellrawdata is not null and cellrawdata != '') AND cellid > "+startCellId+" and cellsheetid="+getsheetid + 
				     " AND (cellcal=0 OR cellcal is null) order by cellid asc ) where rownum<"+size;
		return query(sql,ExcelCell.class);
	}

	public List<ExcelCell> loadCellsOfRawDataOnDemand(Integer startCellId, Integer sheetId, Integer size,
			String getsheetcelltable, String ids) {
		String sql = "select * from ( SELECT * FROM "+getsheetcelltable + 
				     " WHERE (CELLRAWDATA is not null and CELLRAWDATA != '') AND cellid > "+startCellId + 
				     " AND ( CELLSHEETID="+sheetId+" OR ( CELLCAL=1 AND CELLSHEETID IN ("+ids+"))) " + 
				     " order by cellid asc ) where ROWNUM<"+size;
		return query(sql,ExcelCell.class);
	}

	public List<ExcelCell> loadNotCalCellsOnDemand(Integer startCellId, Integer getsheetid, Integer size,
			String getsheetcelltable) {
		String sql = "select * from ( SELECT * FROM "+ getsheetcelltable+ 
				     " WHERE cellid > "+startCellId+" and cellsheetid="+getsheetid + 
				     " AND (CELLCAL=0 OR CELLCAL is null) " + 
				     " order by cellid asc ) where rownum<"+size;
		return query(sql,ExcelCell.class);
	}

	public List<ExcelCell> loadCellsOnDemand(Integer startCellId, Integer sheetId, Integer size,
			String getsheetcelltable, String ids) {
		String sql = " select * from ( SELECT * FROM "+getsheetcelltable + 
				     " WHERE cellid >"+startCellId + 
				     " AND (cellsheetid="+sheetId+" OR (CELLCAL=1 AND cellsheetid IN("+ids+"))) " + 
				     " order by cellid asc ) where rownum<"+size;
		return query(sql,ExcelCell.class);
	}

	public void copySheetCellFromSheet(Integer oldsheetid, Integer newsheetid, String oldsheetcelltable,
			String newsheetcelltable) {
		String sql = " insert into "+newsheetcelltable+" (cellid,cellsheetid,cellrow,cellcol,cellcontent,cellcal,cellrawdata) " + 
				     " (select excelcell_seq.nextval,"+newsheetid+",cellrow,cellcol,cellcontent,cellcal,cellrawdata " + 
				     " from "+oldsheetcelltable + 
				     " where cellsheetid= "+oldsheetid+" )";
		execute(sql);
	}

}
