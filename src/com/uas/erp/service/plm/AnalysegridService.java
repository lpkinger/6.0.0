package com.uas.erp.service.plm;

import net.sf.json.JSONArray;
import com.uas.erp.model.GridPanel;

public interface AnalysegridService {
	JSONArray getData(String condition);

	GridPanel getGridPanel(String caller);

	JSONArray getLogData(String condition, String startdate, String enddate) throws Exception;

	JSONArray getEmData(String startdate, String enddate, int emid, String wr_recorder) throws Exception;

	JSONArray getTestData(String condition, String startdate, String enddate) throws Exception;

	JSONArray getHandData(String condition, String startdate, String enddate) throws Exception;

	JSONArray getSingleTestData(int emid, String startdate, String enddate);

	JSONArray getSingleHandData(int emid, String startdate, String enddate, int emid2);
}
