package com.uas.erp.service.plm.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DataListComboDao;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.model.DataListCombo;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.GridColumns;
import com.uas.erp.model.GridFields;
import com.uas.erp.model.GridPanel;
import com.uas.erp.service.plm.AnalysegridService;

@Service
public class AnalysegridServiceImpl implements AnalysegridService {
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private DetailGridDao detailGridDao;
	@Autowired
	private DataListComboDao dataListComboDao;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public JSONArray getData(String condition) {
		String findSql = "select ra_resourcecode,ra_resourcename,count(ra_id),count(CASE  WHEN  ra_taskpercentdone='100' THEN  'had_complete' END)count  from resourceassignment "
				+ condition + " group by ra_resourcename,ra_resourcecode";
		SqlRowSet rs = baseDao.getJdbcTemplate().queryForRowSet(findSql);
		JSONArray dataarray = new JSONArray();
		while (rs.next()) {
			JSONObject js = new JSONObject();
			js.put("code", rs.getString("ra_resourcecode"));
			js.put("name", rs.getString("ra_resourcename"));
			js.put("count", rs.getInt(3));
			js.put("finishcount", rs.getInt(4));
			js.put("percentdone", NumberUtil.subFloat((float) (100 * (rs.getInt(4) + 0.0) / rs.getInt(3)), 2));
			js.put("rank", "");
			dataarray.add(js);
		}
		// dataarray=BaseUtil.sortJsonArray(dataarray, "percentdone");
		return dataarray;
	}

	@Override
	public GridPanel getGridPanel(String caller) {
		GridPanel panel = new GridPanel();
		List<DetailGrid> detailGrids = detailGridDao.getDetailGridsByCaller(caller, SpObserver.getSp());
		if (detailGrids != null && detailGrids.size() > 0) {
			List<GridFields> fields = new ArrayList<GridFields>();
			List<DataListCombo> combos = dataListComboDao.getComboxsByCaller(caller, SpObserver.getSp());
			List<GridColumns> columns = new ArrayList<GridColumns>();
			for (DetailGrid grid : detailGrids) {
				// 从数据库表detailgrid的数据，通过自定义的构造器，转化为extjs识别的fields格式，详情可见GridFields的构造函数
				fields.add(new GridFields(grid));
				columns.add(new GridColumns(grid, combos));
			}
			panel.setGridFields(fields);
			panel.setGridColumns(columns);
		}
		return panel;
	}

	@Override
	public JSONArray getLogData(String condition, String startdate, String enddate) throws Exception {
		JSONArray dataarray = new JSONArray();
		String findrecord = "Select wr_recorderemid,wr_recorder from workrecord where " + condition
				+ " group by wr_recorderemid,wr_recorder";
		SqlRowSet rs = baseDao.getJdbcTemplate().queryForRowSet(findrecord);
		Map<String, Long> map = getWeekends(startdate, enddate);
		long days = map.get("days");
		long weekends = map.get("weekends");
		Map<String, Long> Dvalue = getDvalue(startdate, enddate);
		Calendar c = Calendar.getInstance();
		long holicount = Dvalue.get("holiday") + 1;
		long addcount = Dvalue.get("add") + 1;
		long count = days - weekends - holicount + addcount;
		List<Long> addlist = getAddDate(startdate, enddate);
		count = count > 0 ? count : 0;
		while (rs.next()) {
			JSONObject js = new JSONObject();
			int realcount = 0;
			int ind = 0;
			int emid = rs.getInt(1);
			String findlog = "Select wr_recorddate from workrecord where wr_recorderemid= " + emid
					+ " and (wr_recorddate  >=to_date('" + startdate + "','yyyy-MM-dd') and  wr_recorddate<to_date('"
					+ enddate + "','yyyy-MM-dd')) group by wr_recorddate";
			SqlRowSet rs1 = baseDao.getJdbcTemplate().queryForRowSet(findlog);
			js.put("id", emid);
			js.put("name", rs.getString("wr_recorder"));
			while (rs1.next()) {
				long time = format.parse(rs1.getString(1)).getTime();
				c.setTimeInMillis(time);
				ind = c.get(Calendar.DAY_OF_WEEK);
				if (addlist.contains(time) || (ind > 1 && ind < 7)) {
					realcount++;
				}
			}
			// 取 实际需要提交的总数
			// 周末有加班情况需要加上 有休假的需减去
			js.put("realcount", realcount);
			js.put("count", count);
			js.put("holiday", holicount + weekends - addcount);
			js.put("addcount", addcount);
			if (count > 0) {
				js.put("percentdone", NumberUtil.subFloat((float) (100 * (realcount + 0.0) / count), 2));
			} else
				js.put("percentdone", 100);
			js.put("startdate", startdate);
			js.put("enddate", enddate);
			js.put("rank", "");
			dataarray.add(js);
		}
		return dataarray;
	}

	private Map<String, Long> getWeekends(String startdate, String enddate) {
		Map<String, Long> map = new HashMap<String, Long>();
		Calendar start = new GregorianCalendar(Integer.parseInt(startdate.split("-")[0]), Integer.parseInt(startdate
				.split("-")[1]), Integer.parseInt(startdate.split("-")[2]), 0, 0);
		Calendar end = new GregorianCalendar(Integer.parseInt(enddate.split("-")[0]), Integer.parseInt(enddate
				.split("-")[1]), Integer.parseInt(enddate.split("-")[2]), 0, 0);
		long day = 86400000;
		long mod = (end.getTimeInMillis() - start.getTimeInMillis()) / day;
		Calendar c = Calendar.getInstance();
		Date s;
		try {
			s = format.parse(startdate);
			c.setTime(s);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int n = c.get(Calendar.DAY_OF_WEEK);
		long week = (mod / 7);
		week = week * 2;
		if (n == 1) {
			week++;
		}
		if (n != 1) {
			n = n - 1;
		}
		map.put("days", mod);

		long yushu = mod % 7;
		if (n + yushu > 7)
			week = week + 2;
		if (n + yushu == 7)
			week++;
		map.put("weekends", week);
		return map;
	}

	private Map<String, Long> getDvalue(String startdate, String enddate) throws Exception {
		String newstartdate = startdate + "T00:00:00";
		String newenddate = enddate + "T24:00:00";
		String findSql = "Select ca_calendarid,ca_startdate,ca_enddate from calendar where ((ca_startdate>='"
				+ newstartdate + "' and ca_startdate<='" + newenddate + "')or(ca_enddate>='" + newstartdate
				+ "' and ca_enddate<='" + newenddate + "')) and ca_calendarid<>2";
		SqlRowSet rs = baseDao.getJdbcTemplate().queryForRowSet(findSql);
		Map<String, Long> map = new HashMap<String, Long>();
		Date Startdate = format.parse(startdate);
		Date Enddate = format.parse(enddate);
		Long holicount = (long) 0;
		Long addcount = (long) 0;
		while (rs.next()) {
			int calendarid = rs.getInt("ca_calendarid");
			String b = rs.getString("ca_startdate").split("T")[0];
			String e = rs.getString("ca_enddate").split("T")[0];
			Date begin = format.parse(b);
			Date end = format.parse(e);
			if (begin.compareTo(Startdate) >= 0 && end.compareTo(Enddate) <= 0) {
				if (calendarid == 3) {
					// 加班情况
					addcount += getWeekends(b, e).get("weekends");
				} else {
					// 休假
					holicount += getWeekends(b, e).get("days") - getWeekends(b, e).get("weekends");
				}
			} else if (begin.compareTo(Startdate) >= 0 && end.compareTo(Enddate) >= 0) {
				if (calendarid == 3) {
					// 加班情况
					addcount += getWeekends(b, enddate).get("weekends");
				} else {
					// 休假
					holicount += getWeekends(b, enddate).get("days") - getWeekends(b, e).get("weekends");
				}
			} else if (begin.compareTo(Startdate) <= 0 && end.compareTo(Enddate) >= 0) {
				if (calendarid == 3) {
					// 加班情况
					addcount += getWeekends(startdate, enddate).get("weekends");
				} else {
					// 休假
					holicount += getWeekends(startdate, enddate).get("days") - getWeekends(b, e).get("weekends");
				}
			} else if (begin.compareTo(Startdate) <= 0 && end.compareTo(Enddate) <= 0) {
				if (calendarid == 3) {
					// 加班情况
					addcount += getWeekends(startdate, e).get("weekends");
				} else {
					// 休假
					holicount += getWeekends(startdate, e).get("days") - getWeekends(startdate, e).get("weekends");
				}
			}
		}
		map.put("holiday", holicount);
		map.put("add", addcount);
		return map;
	}

	@Override
	public JSONArray getEmData(String startdate, String enddate, int emid, String wr_recorder) throws Exception {
		String findlog = "Select wr_recorddate from workrecord where wr_recorderemid= " + emid
				+ " and (wr_recorddate  >=to_date('" + startdate + "','yyyy-MM-dd') and  wr_recorddate<to_date('"
				+ enddate + "','yyyy-MM-dd')) group by wr_recorddate";
		SqlRowSet rs = baseDao.getJdbcTemplate().queryForRowSet(findlog);
		String recorder = employeeDao.getEmployeeByEmId(emid).getEm_name();
		Calendar c = Calendar.getInstance();
		List<Long> biglist = new ArrayList<Long>();
		for (long begin = format.parse(startdate).getTime(); begin < format.parse(enddate).getTime(); begin += 86400000) {
			c.setTimeInMillis(begin);
			if (c.get(Calendar.DAY_OF_WEEK) != 1 && c.get(Calendar.DAY_OF_WEEK) != 7) {
				biglist.add(begin);
			}
		}
		List<Long> smalllist = new ArrayList<Long>();
		while (rs.next()) {
			smalllist.add(format.parse(rs.getString(1)).getTime());
		}
		// 取加班和休假日的时间
		List<Long> addlist = getAddDate(startdate, enddate);
		List<Long> removelist = getRemoveDate(startdate, enddate);
		biglist.addAll(addlist);
		biglist.removeAll(smalllist);
		biglist.removeAll(removelist);
		// 拿到最终的结果 包含 未加班的日期
		JSONArray json = new JSONArray();
		List<List<Long>> Transaction = getTransaction(emid, startdate, enddate);
		List<Long> evectionlist = Transaction.get(0);
		List<Long> holilist = Transaction.get(1);
		for (int i = 0; i < biglist.size(); i++) {
			JSONObject js = new JSONObject();
			js.put("name", recorder);
			js.put("date", format.format(new Date(biglist.get(i))));
			if (evectionlist.contains(biglist.get(i))) {
				js.put("type", "出差");
			} else if (holilist.contains(biglist.get(i))) {
				js.put("type", "请假");
			} else
				js.put("type", "未提交");
			json.add(js);
		}
		return json;
	}

	private List<Long> getAddDate(String startdate, String enddate) throws Exception {
		String newstartdate = startdate + "T00:00:00";
		String newenddate = enddate + "T24:00:00";
		String findSql = "Select * from calendar where ((ca_startdate>='" + newstartdate + "' and ca_startdate<='"
				+ newenddate + "')or(ca_enddate>='" + newstartdate + "' and ca_enddate<='" + newenddate
				+ "') or(ca_startdate<='" + newstartdate + "' and ca_enddate>='" + newenddate
				+ "'))and ca_calendarid=3";
		SqlRowSet rs = baseDao.getJdbcTemplate().queryForRowSet(findSql);
		List<Long> addlist = new ArrayList<Long>();
		Date Startdate = format.parse(startdate);
		Date Enddate = format.parse(enddate);
		while (rs.next()) {
			String b = rs.getString("ca_startdate").split("T")[0];
			String e = rs.getString("ca_enddate").split("T")[0];
			Date begin = format.parse(b);
			Date end = format.parse(e);
			if (begin.compareTo(Startdate) >= 0 && end.compareTo(Enddate) <= 0) {

				addlist.addAll(FinddateTime(b, e, 0));

				// 休假
				// removelist.addAll(FinddateTime(b,e,1));
			} else if (begin.compareTo(Startdate) >= 0 && end.compareTo(Enddate) >= 0) {
				// 加班情况
				addlist.addAll(FinddateTime(b, enddate, 0));
				// 休假
				// removelist.addAll(FinddateTime(b,enddate,1));
			} else if (begin.compareTo(Startdate) <= 0 && end.compareTo(Enddate) >= 0) {
				// 加班情况
				addlist.addAll(FinddateTime(startdate, enddate, 0));
			} else if (begin.compareTo(Startdate) <= 0 && end.compareTo(Enddate) <= 0) {
				addlist.addAll(FinddateTime(startdate, e, 0));
			}
		}
		return addlist;
	}

	private List<Long> getRemoveDate(String startdate, String enddate) throws Exception {
		String newstartdate = startdate + "T00:00:00";
		String newenddate = enddate + "T24:00:00";
		String findSql = "Select * from calendar where ((ca_startdate>='" + newstartdate + "' and ca_startdate<='"
				+ newenddate + "')or(ca_enddate>='" + newstartdate + "' and ca_enddate<='" + newenddate
				+ "') or(ca_startdate<='" + newstartdate + "' and ca_enddate>='" + newenddate
				+ "'))and ca_calendarid=1";
		SqlRowSet rs = baseDao.getJdbcTemplate().queryForRowSet(findSql);
		List<Long> removelist = new ArrayList<Long>();
		Date Startdate = format.parse(startdate);
		Date Enddate = format.parse(enddate);
		while (rs.next()) {
			String b = rs.getString("ca_startdate").split("T")[0];
			String e = rs.getString("ca_enddate").split("T")[0];
			Date begin = format.parse(b);
			Date end = format.parse(e);
			if (begin.compareTo(Startdate) >= 0 && end.compareTo(Enddate) <= 0) {
				removelist.addAll(FinddateTime(b, e, 1));
			} else if (begin.compareTo(Startdate) >= 0 && end.compareTo(Enddate) >= 0) {
				removelist.addAll(FinddateTime(b, enddate, 1));
			} else if (begin.compareTo(Startdate) <= 0 && end.compareTo(Enddate) >= 0) {
				removelist.addAll(FinddateTime(b, enddate, 1));
			} else if (begin.compareTo(Startdate) <= 0 && end.compareTo(Enddate) <= 0) {
				removelist.addAll(FinddateTime(b, enddate, 1));
			}
		}
		return removelist;
	}

	private List<Long> FinddateTime(String startdate, String enddate, int type) throws ParseException {
		List<Long> lists = new ArrayList<Long>();
		Calendar c = Calendar.getInstance();
		for (long begin = format.parse(startdate).getTime(); begin <= format.parse(enddate).getTime(); begin += 86400000) {
			if (type == 1) {
				// 要取到加班时间 不然不好搞
				c.setTimeInMillis(begin);
				if (c.get(Calendar.DAY_OF_WEEK) != 1 && c.get(Calendar.DAY_OF_WEEK) != 7) {
					lists.add(begin);
				}
			} else
				lists.add(begin);
		}
		return lists;
	}

	private List<List<Long>> getTransaction(int emid, String startdate, String enddate) throws Exception {
		String findSql = "Select tt_startdate,tt_enddate,tt_type from TeammemberTran where ((tt_startdate>=to_date('"
				+ startdate + "','yyyy-MM-dd') and tt_startdate<=to_date('" + enddate
				+ "','yyyy-MM-dd'))or(tt_enddate>=to_date('" + startdate + "','yyyy-MM-dd') and tt_enddate<=to_date('"
				+ enddate + "','yyyy-MM-dd'))or(tt_startdate<=to_date('" + startdate
				+ "','yyyy-MM-dd') and tt_enddate>=to_date('" + enddate + "','yyyy-MM-dd')))  and tt_employeeid ="
				+ emid;
		List<Long> evectionlist = new ArrayList<Long>();
		List<Long> holylist = new ArrayList<Long>();
		Date Startdate = format.parse(startdate);
		Date Enddate = format.parse(enddate);
		SqlRowSet rs = baseDao.getJdbcTemplate().queryForRowSet(findSql);
		List<List<Long>> data = new ArrayList<List<Long>>();
		while (rs.next()) {
			String b = rs.getString("tt_startdate");
			String e = rs.getString("tt_enddate");
			int type = rs.getInt("tt_type");
			Date begin = format.parse(b);
			Date end = format.parse(e);
			if (begin.compareTo(Startdate) >= 0 && end.compareTo(Enddate) <= 0) {
				if (type == 1) {
					// 出差
					evectionlist.addAll(FinddateTime(b, e, 0));
				} else {
					holylist.addAll(FinddateTime(b, e, 0));
				}
			} else if (begin.compareTo(Startdate) >= 0 && end.compareTo(Enddate) >= 0) {
				if (type == 1) {
					// 出差
					evectionlist.addAll(FinddateTime(b, enddate, 0));
				} else {
					// 休假
					holylist.addAll(FinddateTime(b, enddate, 0));
				}
			} else if (begin.compareTo(Startdate) <= 0 && end.compareTo(Enddate) >= 0) {
				if (type == 1) {
					// 加班情况
					evectionlist.addAll(FinddateTime(startdate, enddate, 0));
				} else {
					// 休假
					holylist.addAll(FinddateTime(startdate, enddate, 0));
				}
			} else if (begin.compareTo(Startdate) <= 0 && end.compareTo(Enddate) <= 0) {
				if (type == 1) {
					evectionlist.addAll(FinddateTime(startdate, e, 0));
				} else {
					// 休假
					holylist.addAll(FinddateTime(startdate, e, 0));
				}
			}

		}
		data.add(evectionlist);
		data.add(holylist);
		return data;
	}

	@Override
	public JSONArray getTestData(String condition, String startdate, String enddate) throws Exception {
		JSONArray dataarray = new JSONArray();
		SqlRowList sl = baseDao
				.queryForRowSet("Select cld_exhibitor,cld_exhibitorid  from checkListDetail left join checkList on cld_clid=cl_id where "
						+ condition + " and cl_statuscode='AUDITED' group by cld_exhibitor,cld_exhibitorid ");
		while (sl.next()) {
			int handedbug = 0, testingbug = 0, pengingbug = 0, bugs = 0, overtestingbug = 0, over3testingbug = 0, emid = sl
					.getInt(2);
			String emname = sl.getString(1);
			String find = "Select cld_statuscode,cld_newhanddate from checkListDetail left join checkList on cld_clid=cl_id where cld_exhibitorid= "
					+ emid
					+ " and (cld_exhibitdate  >=to_date('"
					+ startdate
					+ "','yyyy-MM-dd') and  cld_exhibitdate<to_date('"
					+ getnextDate(enddate)
					+ "','yyyy-MM-dd')) and cl_statuscode='AUDITED'";
			SqlRowList sl2 = baseDao.queryForRowSet(find);
			while (sl2.next()) {
				bugs++;
				String statuscode = sl2.getString("cld_statuscode");
				if (statuscode.equals("HANDED")) {
					handedbug++;
				} else if (statuscode.equals("PENDING")) {
					pengingbug++;
				} else if (statuscode.equals("TESTING")) {
					Object date = sl2.getObject("cld_newhanddate");
					if (date != null) {
						Date date1 = DateUtil.parseStringToDate(date.toString(), Constant.YMD_HMS);
						int overdays = overdays(date1);
						if (overdays > 0) {
							overtestingbug++;
							if (overdays > 3) {
								over3testingbug++;
							}
						}
					}

					testingbug++;
				}
			}
			JSONObject js = new JSONObject();
			js.put("handedbug", handedbug);
			js.put("unhandedbug", bugs - handedbug);
			js.put("testingbug", testingbug);
			js.put("pendingbug", pengingbug);
			js.put("bugs", bugs);
			js.put("exhibitor", emname);
			js.put("overtestingbug", overtestingbug);
			js.put("over3testingbug", over3testingbug);
			js.put("exhibitorid", emid);
			dataarray.add(js);
		}
		return dataarray;
	}

	@Override
	public JSONArray getHandData(String condition, String startdate, String enddate) throws Exception {
		JSONArray dataarray = new JSONArray();
		SqlRowList sl = baseDao
				.queryForRowSet("Select cld_newhandman,cld_newhandmanid  from checkListDetail left join checkList on cld_clid=cl_id where "
						+ condition + " and cl_statuscode='AUDITED' group by cld_newhandman,cld_newhandmanid ");
		while (sl.next()) {
			int handedbug = 0, testingbug = 0, pengingbug = 0, bugs = 0, overunhandedbug = 0, over7unhandedbug = 0, emid = sl
					.getInt(2);
			String emname = sl.getString(1);
			String find = "Select cld_statuscode,cld_handenddate from checkListDetail left join checkList on cld_clid=cl_id where cld_newhandmanid= "
					+ emid
					+ " and (cld_exhibitdate  >=to_date('"
					+ startdate
					+ "','yyyy-MM-dd') and  cld_exhibitdate<to_date('"
					+ getnextDate(enddate)
					+ "','yyyy-MM-dd')) and cl_statuscode='AUDITED'";
			SqlRowList sl2 = baseDao.queryForRowSet(find);
			while (sl2.next()) {
				bugs++;
				String statuscode = sl2.getString("cld_statuscode");
				Date handenddate = sl2.getDate("cld_handenddate");
				if(handenddate != null) {
					int overdays = overdays(handenddate);
					if (statuscode.equals("HANDED")) {
						handedbug++;
					} else {
						if (overdays > 0) {
							overunhandedbug++;
							if (overdays > 6) {
								over7unhandedbug++;
							}
						}
						if (statuscode.equals("PENDING")) {
							pengingbug++;
						} else if (statuscode.equals("TESTING")) {
							testingbug++;
						}
					}
				}
			}
			JSONObject js = new JSONObject();
			js.put("handedbug", handedbug);
			js.put("unhandedbug", bugs - handedbug);
			js.put("testingbug", testingbug);
			js.put("pendingbug", pengingbug);
			js.put("bugs", bugs);
			js.put("hander", emname);
			js.put("overunhandedbug", overunhandedbug);
			js.put("over7unhandedbug", over7unhandedbug);
			js.put("handerid", emid);
			dataarray.add(js);
		}
		return dataarray;
	}

	@Override
	public JSONArray getSingleTestData(int emid, String startdate, String enddate) {
		JSONArray arr = new JSONArray();
		SqlRowList sl = baseDao
				.queryForRowSet("select cld_name,cld_exhibitdate,cld_exhibitor from checklistdetail where cld_exhibitorid="
						+ emid
						+ " and cld_exhibitdate  >=to_date('"
						+ startdate
						+ "','yyyy-MM-dd') and  cld_exhibitdate < to_date('"
						+ getnextDate(enddate)
						+ "','yyyy-MM-dd') and cld_statuscode='TESTING'");
		JSONObject json = null;
		while (sl.next()) {
			json = new JSONObject();
			json.put("bugname", sl.getString(1));
			json.put("emname", sl.getString(3));
			json.put("date", sl.getString(2));
			arr.add(json);
		}
		return arr;
	}

	@Override
	public JSONArray getSingleHandData(int emid, String startdate, String enddate, int emid2) {
		JSONArray arr = new JSONArray();
		SqlRowList sl = baseDao
				.queryForRowSet("select cld_name,cld_newhandman,cld_newhanddate from checklistdetail where cld_newhandmanid="
						+ emid
						+ " and cld_exhibitdate  >=to_date('"
						+ startdate
						+ "','yyyy-MM-dd') and  cld_exhibitdate<to_date('"
						+ getnextDate(enddate)
						+ "','yyyy-MM-dd') and cld_statuscode='PENDING'");
		JSONObject json = null;
		while (sl.next()) {
			json = new JSONObject();
			json.put("bugname", sl.getString(1));
			json.put("date", sl.getString(3));
			json.put("emname", sl.getString(2));
			arr.add(json);
		}
		return arr;
	}

	public String getnextDate(String date) {
		Calendar calendar = Calendar.getInstance();
		try {
			if (!date.equals("")) {
				calendar.setTimeInMillis(format.parse(date).getTime());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		calendar.add(Calendar.DATE, 1); // 下一天
		Date date1 = calendar.getTime();
		return format.format(date1);
	}

	public int overdays(Date date) {
		return (int) ((new Date().getTime() - date.getTime()) / (1000 * 60 * 60 * 24));
	}
}
