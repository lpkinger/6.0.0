package com.uas.mobile.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.PathUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.common.FilePathService;
import com.uas.erp.service.oa.WorkDailyService;
import com.uas.erp.service.oa.WorkMonthlyService;
import com.uas.erp.service.oa.WorkWeeklyService;
import com.uas.mobile.service.AddrBookService;
import com.uas.erp.dao.common.EmployeeDao;

@Service("addrBookService")
public class AddrBookServiceImpl implements AddrBookService {
	@Autowired
	private FilePathService filePathService;
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private WorkDailyService workDailyService;
	@Autowired
	private WorkWeeklyService workWeeklyService;
	@Autowired
	private WorkMonthlyService workMonthlyService;
	
	@Override
	public List<Map<String, Object>> getRootHrorg() {
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		List<Object[]> objs = baseDao.getFieldsDatasByCondition("hrorg",
				new String[] { "or_id", "or_code", "or_name", "or_subof",
						"or_isleaf" }, "or_subof=0 order by or_detno,or_id");
		for (Object[] obj : objs) {
			map = new HashMap<String, Object>();
			map.put("or_id", obj[0]);
			map.put("or_code", obj[1]);
			map.put("or_name", obj[2]);
			map.put("or_subof", obj[3]);
			map.put("or_isleaf", obj[4]);
			lists.add(map);
		}
		return lists;
	}

	@Override
	public List<Map<String, Object>> getLeafHrorg(int or_id) {
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		List<Object[]> objs = baseDao.getFieldsDatasByCondition("hrorg",
				new String[] { "or_id", "or_code", "or_name", "or_subof",
						"or_isleaf" }, "or_subof=" + or_id+" order by or_detno,or_id");
		for (Object[] obj : objs) {
			map = new HashMap<String, Object>();
			map.put("or_id", obj[0]);
			map.put("or_code", obj[1]);
			map.put("or_name", obj[2]);
			map.put("or_subof", obj[3]);
			map.put("or_isleaf", obj[4]);
			lists.add(map);
		}
		return lists;

	}

	/*@Override
	public List<Map<String, Object>> getAllHrorg(String lastdate) {
		String condition = lastdate;
		if (lastdate.equals("") || lastdate == "" || lastdate == null) {
			condition = "1=1 and nvl(or_status,' ')<>'已禁用'";
		} else {
			condition = "or_lastdate>to_date('"
					+ lastdate
					+ "','yyyy-MM-dd hh24:mi:ss') and nvl(or_status,' ')<>'已禁用'";
		}
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		List<Object[]> objs = baseDao.getFieldsDatasByCondition("hrorg_mobile",
				new String[] { "or_id", "or_code", "or_name", "or_subof",
						"or_isleaf", "company", "whichsys", "flag",
						"or_lastdate", "or_headmancode", "or_headmanname",
						"or_remark" }, condition);
		for (Object[] obj : objs) {
			map = new HashMap<String, Object>();
			map.put("or_id", obj[0]);
			map.put("or_code", obj[1]);
			map.put("or_name", obj[2]);
			map.put("or_subof", obj[3]);
			map.put("or_isleaf", obj[4]);
			map.put("company", obj[5]);
			map.put("whichsys", obj[6]);
			map.put("flag", obj[7]);
			map.put("or_lastdate", obj[8]);
			map.put("or_headmancode", obj[9]);
			map.put("or_headmanname", obj[10]);
			map.put("or_remark", obj[11]);
			lists.add(map);
		}
		return lists;
	}*/

	/*@Override
	public List<Map<String, Object>> getAllEmps(String lastdate) {
		String sql = "";
		if (lastdate.equals("") || lastdate == "" || lastdate == null) {
			sql = "select em_id,em_code,em_name,em_position,wm_concat(jo_name) as em_jobs,em_defaultorname,em_defaultorid,em_depart,em_tel,em_mobile,em_email,em_uu,em_imageid,company,whichsys,flag,em_lastdate,em_imid from employee_mobile left join job left join empsjobs on job_id=jo_id on em_id=emp_id  where nvl(em_class,' ')<>'离职'  group by em_id,em_code,em_name,em_position,em_defaultorname,em_defaultorid,em_depart,em_tel,em_mobile,em_email,em_uu,em_imageid,company,whichsys,flag,em_lastdate,em_imid";
		} else {
			sql = "select em_id,em_code,em_name,em_position,wm_concat(jo_name) as em_jobs,em_defaultorname,em_defaultorid,em_depart,em_tel,em_mobile,em_email,em_uu,em_imageid,company,whichsys,flag,em_lastdate,em_imid from employee_mobile left join job left join empsjobs on job_id=jo_id on em_id=emp_id  where nvl(em_class,' ')<>'离职' and  em_lastdate>to_date('"
					+ lastdate
					+ "','yyyy-MM-dd hh24:mi:ss') group by em_id,em_code,em_name,em_position,em_defaultorname,em_defaultorid,em_depart,em_tel,em_mobile,em_email,em_uu,em_imageid,company,whichsys,flag,em_lastdate,em_imid";
		}
		return baseDao.getJdbcTemplate().queryForList(sql);

	}*/

	@Override
	public List<Map<String, Object>> getEmployeesByOrId(int or_id, int start,
			int end) {
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		List<Object[]> objs = baseDao.getFieldsDatasByCondition(
				"(select A.*,rownum rn "
						+ "from(select em_id,em_code,em_name from employee "
						+ "where nvl(em_class,' ')<>'离职' and em_defaultorid="
						+ or_id + ") A " + "where rownum<=" + end + ")",
				new String[] { "em_id", "em_code", "em_name" }, "rn>=" + start);
		for (Object[] obj : objs) {
			map = new HashMap<String, Object>();
			map.put("em_id", obj[0]);
			map.put("em_code", obj[1]);
			map.put("em_name", obj[2]);

			lists.add(map);
		}
		return lists;

	}

	@Override
	public String getSobName(String master) {
		Object localSobName = baseDao.getFieldDataByCondition("enterprise",
				"en_shortname", "en_whichsystem='" + master + "'");
		return localSobName == null ? "企业架构" : String.valueOf(localSobName);
	}

	@Override
	public List<Object> getJobs(int em_id) {
		List<Object> jobs = baseDao.getFieldDatasByCondition(
				"job left join empsjobs on job_id=jo_id", "jo_name", "emp_id="
						+ em_id);
		return jobs;
	}

	@Override
	public List<Map<String, Object>> queryEmployeeByName(String em_name) {
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		List<Object[]> objs = baseDao.getFieldsDatasByCondition("employee",
				new String[] { "em_id", "em_code", "em_name", "em_depart",
						"em_defaultorname", "em_position", "em_imid" },
				"nvl(em_class,' ')<>'离职' and em_name like '%" + em_name + "%'");
		for (Object[] obj : objs) {
			map = new HashMap<String, Object>();
			map.put("em_id", obj[0]);
			map.put("em_code", obj[1]);
			map.put("em_name", obj[2]);
			map.put("em_department", obj[3]);
			map.put("em_organization", obj[4]);
			map.put("em_position", obj[5]);
			map.put("em_imid", obj[6]);
			lists.add(map);
		}
		return lists;
	}

	@Override
	public void updateEmployeePic(int em_id, int fp_id, String fp_path) {
		baseDao.updateByCondition("employee", "EM_IMAGEID=" + fp_id
				+ ",EM_PHOTOURL='" + fp_path + "'", "em_id=" + em_id);
	}

	@Override
	public Object[] getEmployeePic(String em_code) {
		Object[] objs = baseDao.getFieldsDataByCondition("employee",
				new String[] { "em_name", "EM_PHOTOURL" }, "em_code='"
						+ em_code + "'");
		return objs;
	}

	@Override
	public Map<String, Object> Commentsback_mobile(String formStore, String caller,MultipartFile img1,MultipartFile img2,MultipartFile img3) {		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Map<String, Object> modelMap=new HashMap<String,Object>();
		//String code = (String) store.get("cb_code");
		String result="";
		String cb_code=baseDao.sGetMaxNumber("Commentsback_mobile", 2);
		int id = baseDao.getSeqId("COMMENTSBACK_MOBILE_SEQ");
		store.put("cb_code", cb_code);
		store.put("cb_id",id);
		modelMap.put("cb_code", cb_code);
		modelMap.put("cb_id", id);
		/*int count = baseDao
				.getCount("select count(*) from Commentsback_mobile where cb_code='"
						+ code + "'");*/
		/*if (count > 0) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_sameCode"));
			return;
		}*/
		if(!("".equals(img1))&&img1!=null){
			try {				
				String filename = img1.getOriginalFilename();
				long size = img1.getSize();
				if (size > 104857600) {
					result="{error: '文件过大'}";
					modelMap.put("result", result);
				}
				String path = getFilePath(filename, store.get("em_code").toString());
				File file = new File(path);
				BufferedOutputStream bos = null;
				BufferedInputStream bis = null;
				try {

					bos = new BufferedOutputStream(new FileOutputStream(file));
					bis = new BufferedInputStream(img1.getInputStream());
					int c;
					while ((c = bis.read()) != -1) {
						bos.write(c);
						bos.flush();
					}
					bos.close();			
				} catch (Exception e) {
					e.printStackTrace();
				}
				//Employee employee=(Employee) store.get("em_code");
				Employee employee = employeeDao.getEmployeeByEmcode(store.get("em_code").toString());
				Object loadid = filePathService.saveFilePath(path, (int) size, filename,employee);
				result= "{success: true, filepath: " + loadid + ",size: " + size + ",path:\"" + path + "\"}";
				//modelMap.put("result", result);
				store.put("cb_attch", loadid);
			} catch (Exception e) {
				e.printStackTrace();
				 result="{error: '文件过大,上传失败'}";
				modelMap.put("result", result);
			}
		}
		if(!("".equals(img2))&&img2!=null){
			try {
				String filename = img2.getOriginalFilename();
				long size = img2.getSize();
				if (size > 104857600) {
					result="{error: '文件过大'}";
					modelMap.put("result", result);
				}
				String path = getFilePath(filename, store.get("em_code").toString());
				File file = new File(path);
				BufferedOutputStream bos = null;
				BufferedInputStream bis = null;
				try {

					bos = new BufferedOutputStream(new FileOutputStream(file));
					bis = new BufferedInputStream(img2.getInputStream());
					int c;
					while ((c = bis.read()) != -1) {
						bos.write(c);
						bos.flush();
					}
					bos.close();			
				} catch (Exception e) {
					e.printStackTrace();
				}
				//Employee employee=(Employee) store.get("em_code");
				Employee employee = employeeDao.getEmployeeByEmcode(store.get("em_code").toString());
				Object loadid = filePathService.saveFilePath(path, (int) size, filename,employee);
				result= "{success: true, filepath: " + loadid + ",size: " + size + ",path:\"" + path + "\"}";
				//modelMap.put("result", store.get("result")+result);
				store.put("cb_attch", store.get("cb_attch")+";"+loadid);
			} catch (Exception e) {
				e.printStackTrace();
				 result="{error: '文件过大,上传失败'}";
				 modelMap.put("result", result);
			}
		}
		if(!("".equals(img3))&&img3!=null){
			try {
				String filename = img3.getOriginalFilename();
				long size = img3.getSize();
				if (size > 104857600) {
					result="{error: '文件过大'}";
					modelMap.put("result", result);
				}
				String path = getFilePath(filename, store.get("em_code").toString());
				File file = new File(path);
				BufferedOutputStream bos = null;
				BufferedInputStream bis = null;
				try {
					bos = new BufferedOutputStream(new FileOutputStream(file));
					bis = new BufferedInputStream(img3.getInputStream());
					int c;
					while ((c = bis.read()) != -1) {
						bos.write(c);
						bos.flush();
					}
					bos.close();			
				} catch (Exception e) {
					e.printStackTrace();
				}
				//Employee employee=(Employee) store.get("em_code");
				Employee employee = employeeDao.getEmployeeByEmcode(store.get("em_code").toString());
				Object loadid = filePathService.saveFilePath(path, (int) size, filename,employee);
				result= "{success: true, filepath: " + loadid + ",size: " + size + ",path:\"" + path + "\"}";
				//modelMap.put("result", store.get("result")+result);
				store.put("cb_attch", store.get("cb_attch")+";"+loadid);
			} catch (Exception e) {
				e.printStackTrace();
				 result="{error: '文件过大,上传失败'}";
				 modelMap.put("result", result);
			}
		}		
		store.remove("em_code");		
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"Commentsback_mobile", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// updateEmployeeHoliday(language,employee);
		try {
			// 记录操作
			baseDao.logger.save(caller, "cb_id", store.get("cb_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return modelMap;
	}
	private String getFilePath(String fileName, String em_code) {
		String uuid = UUID.randomUUID().toString().replaceAll("\\-", "");
		String suffix = fileName.indexOf(".") != -1 ? fileName.substring(
				fileName.lastIndexOf("."), fileName.length()) : "";
		String path = PathUtil.getFilePath() + "postattach";
		File file = new File(path);
		if (!file.isDirectory()) {
			file.mkdir();
			path = path + File.separator + em_code;
			new File(path).mkdir();
		} else {
			path = path + File.separator + em_code;
			file = new File(path);
			if (!file.isDirectory()) {
				file.mkdir();
			}
		}
		return path + File.separator + uuid + suffix;
	}
	// 公开、定向通知公告
	@Override
	public Map<String, Object> getAllNoteCount(String master, String emid) {
		String TZ = "0";
		String GG = "0";
		String TZ_read = "0";
		String GG_read = "0";
		String TZ_public = "0";
		String GG_public = "0";
		String TZ_public_read = "0";
		String GG_public_read = "0";
		String news = "0";
		String newsRead = "0";
		String process = "0";
		String getprocess = "0";
		String task = "0";
		// 取定向通知、公告条数
		TZ = baseDao.getFieldDataByCondition(
				"note",
				"count(*)",
				"NO_INFOTYPE in('TZ') and no_ispublic=0 and NO_RECIPIENTID like '%"
						+ emid + "%'").toString();
		// System.out.println("TZ="+TZ);
		GG = baseDao.getFieldDataByCondition(
				"note",
				"count(*)",
				"NO_INFOTYPE in('GG') and no_ispublic=0 and NO_RECIPIENTID like '%"
						+ emid + "%'").toString();
		// System.out.println("GG="+GG);
		// 取已读定向通知、公告条数
		TZ_read = baseDao.getFieldDataByCondition(
				"note , READSTATUS",
				"count(*)",
				"no_id=mainid and NO_INFOTYPE in('TZ') and man='" + emid
						+ "' and no_ispublic=0").toString();
		// System.out.println("TZ_read="+TZ_read);
		GG_read = baseDao.getFieldDataByCondition(
				"note , READSTATUS",
				"count(*)",
				"no_id=mainid and NO_INFOTYPE in('GG') and man='" + emid
						+ "' and no_ispublic=0").toString();
		// System.out.println("GG_read="+GG_read);

		// 取公开通知、公告条数
		TZ_public = baseDao.getFieldDataByCondition("note", "count(*)",
				"NO_INFOTYPE in('TZ') and (no_ispublic=-1 or no_ispublic=1) ")
				.toString();
		// System.out.println("TZ_public="+TZ_public);
		GG_public = baseDao.getFieldDataByCondition("note", "count(*)",
				"NO_INFOTYPE in('GG') and (no_ispublic=-1 or no_ispublic=1) ")
				.toString();
		// System.out.println("GG_public="+GG_public);
		// 取已读公开通、公告条数
		TZ_public_read = baseDao.getFieldDataByCondition(
				"note , READSTATUS",
				"count(*)",
				"no_id=mainid and NO_INFOTYPE in('TZ') and  man='" + emid
						+ "' and (no_ispublic=-1 or no_ispublic=1)").toString();
		// System.out.println("TZ_public_read="+TZ_public_read);
		GG_public_read = baseDao.getFieldDataByCondition(
				"note , READSTATUS",
				"count(*)",
				"no_id=mainid and NO_INFOTYPE in('GG') and  man='" + emid
						+ "' and (no_ispublic=-1 or no_ispublic=1) ")
				.toString();
		// System.out.println("GG_public_read="+GG_public_read);

		// 取新闻条数
		news = baseDao.getFieldDataByCondition("news", "count(*)",
				"rownum<10000000").toString();
		// System.out.println("news="+news);
		// 取新闻条数
		newsRead = baseDao.getFieldDataByCondition("news , readstatus",
				"count(*)",
				"ne_id=mainid and man='" + emid + "' and SOURCEKIND='new'")
				.toString();
		// System.out.println("newsRead="+newsRead);
		// 取待办任务、审批流条数
		String emcode = baseDao.getFieldDataByCondition("employee", "em_code",
				"em_id=" + emid).toString();
		process = baseDao.getFieldDataByCondition("ALLPROCESS_UNDO_VIEW",
				"count(*)", " DEALPERSONCODE='" + emcode + "' and type='待办流程'")
				.toString();
		getprocess = baseDao.getFieldDataByCondition("ALLPROCESS_UNDO_VIEW",
				"count(*)", "DEALPERSONCODE='" + emcode + "' and type='待接管流程'")
				.toString();
		task = baseDao.getFieldDataByCondition("ALLPROCESS_UNDO_VIEW",
				"count(*)", "DEALPERSONCODE='" + emcode + "' and type='任务信息'")
				.toString();
		/*
		 * System.out.println("process="+process);
		 * System.out.println("getprocess="+getprocess);
		 * System.out.println("task="+task);
		 */
		// 计算通知未读条数
		int TZcount = Integer.parseInt(TZ_public)
				+ Integer.parseInt(TZ)
				- (Integer.parseInt(TZ_read) + Integer.parseInt(TZ_public_read));
		int GGcount = Integer.parseInt(GG_public)
				+ Integer.parseInt(GG)
				- (Integer.parseInt(GG_read) + Integer.parseInt(GG_public_read));
		int newscount = Integer.parseInt(news) - Integer.parseInt(newsRead);
		Map<String, Object> mapData = new HashMap<String, Object>();
		mapData.put("未读通知", TZcount);
		mapData.put("未读公告", GGcount);
		mapData.put("未读新闻", newscount);
		mapData.put("待办流程",
				Integer.parseInt(process) + Integer.parseInt(getprocess));
		mapData.put("待办任务", Integer.parseInt(task));
		return mapData;
	}

	@Override
	public String update_hrorgmobile(String orid, int kind) {
		// TODO Auto-generated method stub
		String updateSQL = "";
		if (kind == 1) {
			updateSQL = "update hrorg_mobile set or_remark='1' where or_id in ("
					+ orid + ")";
		} else {
			updateSQL = "update hrorg_mobile set or_remark=null where or_id in ("
					+ orid + ")";
		}

		System.out.println("sql=" + updateSQL);
		try {
			baseDao.execute(updateSQL);
			return orid;
		} catch (Exception e) {
			return "F";
		}
	}


	@Override
	public List<Map<String, Object>> addWorkReport(String caller,
			String formStore) {
		// TODO Auto-generated method stub
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String emcode = null;
		String wc = null;
		//根据录入人和日报日期判断一个人一天只能录入一张工作日报单
		if("WorkDaily".equals(caller)){
			emcode = (String) store.get("wd_empcode");
			if(store.get("wd_date")==null||"".equals(store.get("wd_date"))){
				store.put("wd_date", DateUtil.currentDateString(null));
			}
			String wd_date=store.get("wd_date").toString();
			wc=(String) baseDao.getFieldDataByCondition("WorkDaily","wd_code","wd_empcode='"+emcode+"' and trunc(wd_date)=trunc(to_date('"+wd_date+"','yyyy-mm-dd'))");
			if(wc!=null&&!"".equals(wc)){
				BaseUtil.showError("你在"+wd_date+"已经录入了一张工作日报，该工作日报的编号为:"+wc);
			}
			//获取限制补写日报的参数设置
			workDailyService.workDailyLimit(wd_date);
		}else if("WorkWeekly".equals(caller)){
			emcode = (String) store.get("ww_empcode");
			String ww_starttime=store.get("ww_starttime").toString();
			wc=(String) baseDao.getFieldDataByCondition("WorkWeekly","ww_code","ww_empcode='"+emcode+"' and trunc(ww_starttime)=trunc(to_date('"+ww_starttime+"','yyyy-mm-dd'))");
			if(wc!=null&&!"".equals(wc)){
				BaseUtil.showError("你在第"+store.get("ww_week").toString()+"周已经录入了一张工作周报，该工作周报的编号为:"+wc);
			}
			//获取限制补写周报的参数设置
			workWeeklyService.workWeeklyLimit(DateUtil.parseDateToOracleString(Constant.YMD, ww_starttime));
		}else if("WorkMonthly".equals(caller)){
			emcode = (String) store.get("wm_empcode");
			String wm_starttime=store.get("wm_starttime").toString();
			wc=(String) baseDao.getFieldDataByCondition("WorkMonthly","wm_code","wm_empcode='"+emcode+"' and trunc(wm_starttime)=trunc(to_date('"+wm_starttime+"','yyyy-mm-dd'))");
			if(wc!=null&&!"".equals(wc)){
				BaseUtil.showError("你在"+store.get("wm_month").toString()+"月已经录入了一张工作月报，该工作月报的编号为:"+wc);
			}
			//获取限制补写月报的参数设置
			workMonthlyService.workWeeklyLimit(store.get("wm_month").toString());
		}
		String[] fields = {"fo_seq","fo_keyfield","fo_table","fo_codefield","fo_statusfield","fo_statuscodefield"};
		Object[] formData = baseDao.getFieldsDataByCondition("form",  fields, "fo_caller='"+caller+"'");
		if(formData==null){
			BaseUtil.showError("该页面没有配置Form设置，请进行配置");
		}
		Object id = baseDao.getSeqId(formData[0].toString());
		/*
		 * baseDao.getFieldDataByCondition("dual", "WorkDaily_seq.nextval",
		 * "1=1")
		 */
		System.out.println("id=" + id);
		// store.put("wd_id",id.toString().substring(1,
		// id.toString().length()-1));
		store.put(formData[1].toString(), id);
		String table = formData[2].toString();
		Object code = baseDao.sGetMaxNumber(table != null ? table.split(" ")[0]
				: caller, 2);
		store.put(formData[3].toString(), code);
		store.put(formData[4].toString(), "在录入");
		store.put(formData[5].toString(), "ENTERING");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(System.currentTimeMillis());
		if("WorkDaily".equals(caller)){
			//加入录入日期
			store.put("wd_entrydate", format.format(date));
		}else if("WorkWeekly".equals(caller)){
			//加入录入日期
			store.put("ww_date", format.format(date));
		}else if("WorkMonthly".equals(caller)){
			//加入录入日期
			store.put("wm_date", format.format(date));
		}
		// 根据员工编号取出部门岗位组织
		Object dept = baseDao.getFieldDataByCondition("employee", "em_depart",
				"em_code='" + emcode + "'");
		Object position = baseDao.getFieldDataByCondition("employee",
				"EM_POSITION", "em_code='" + emcode + "'");
		Object orname = baseDao.getFieldDataByCondition("employee",
				"EM_DEFAULTORNAME", "em_code='" + emcode + "'");
		Object emname = baseDao.getFieldDataByCondition("employee", "em_name",
				"em_code='" + emcode + "'");
		Object emid = baseDao.getFieldDataByCondition("employee", "em_id",
				"em_code='" + emcode + "'");
		if("WorkDaily".equals(caller)){
			store.put("wd_depart", dept);
			store.put("wd_hrorg", orname);
			store.put("wd_joname", position);
			store.put("wd_emp", emname);
			store.put("wd_empid", emid);
		}else if("WorkWeekly".equals(caller)){
			store.put("ww_depart", dept);
			store.put("ww_hrorg", orname);
			store.put("ww_joname", position);
			store.put("ww_emp", emname);
			store.put("ww_empid", emid);
		}else if("WorkMonthly".equals(caller)){
			store.put("wm_depart", dept);
			store.put("wm_hrorg", orname);
			store.put("wm_joname", position);
			store.put("wm_emp", emname);
			store.put("wm_empid", emid);
		}

		String formSql = SqlUtil.getInsertSqlByFormStore(store, formData[2].toString(),
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 抓取工作内容
		try {
			if("WorkDaily".equals(caller)){
				baseDao.callProcedure("SP_CATCHWORKCONTENT", new Object[] { id });
			}else if("WorkWeekly".equals(caller)){	
				baseDao.callProcedure("SP_CATCHWORKCONTENTWEEKLY", new Object[] { id });
			}else if("WorkMonthly".equals(caller)){
				baseDao.callProcedure("SP_CATCHWORKCONTENTMONTHLY", new Object[] { id });
			}
		} catch (Exception ex) {

		}

		// 触发审批流
		// 执行提交操作
		baseDao.submit(formData[2].toString(), formData[1].toString() + "=" + id, formData[4].toString(), formData[5].toString());
		// 触发审批流
		handlerService.afterSubmit(caller, Integer.valueOf(id.toString()));
		// updateEmployeeHoliday(language,employee);
		try {
			// 记录操作
			baseDao.logger.save(caller, formData[1].toString(), id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 获取保存数据
		String sql = "select * from "+formData[2].toString()+" where "+formData[1].toString()+"=" + id + "";
		return baseDao.getJdbcTemplate().queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> formConfig(String caller) {
		// TODO Auto-generated method stub
		String fromdetail = "select fd_detno,fd_caption,fd_field,fd_type,fd_readonly from formdetail where fd_mobileused=-1 and fd_type<>'H' and fd_foid in(select fo_id from form where fo_caller='"
				+ caller + "') order by fd_detno";
		System.out.println(fromdetail);
		return baseDao.getJdbcTemplate().queryForList(fromdetail);
	}

	@Override
	public List<Map<String, Object>> gridConfig(String caller) {
		// TODO Auto-generated method stub
		String griddetail = "select dg_sequence,dg_caption,dg_field,dg_type,dg_logictype from detailgrid where dg_mobileused=-1 and dg_width>0 and dg_caller='"
				+ caller + "' order by dg_sequence";
		return baseDao.getJdbcTemplate().queryForList(griddetail);
	}

	@Override
	public void addMobileMac(String emcode, String macAddress) {
		// TODO Auto-generated method stub

		// 判断mac地址是否已经绑定了其他用户
		String sqlmac = "select count(1) from employee where em_macaddress='"
				+ macAddress + "' and em_code<>'" + emcode + "' ";
		int count = baseDao.getCount(sqlmac);
		if (count > 0) {
			BaseUtil.showError("该设备已被他人绑定!");
		}
		// 再查询MAC地址在人员资料是否存在,不存在插入，存在就进行对比，相等
		Object mac = baseDao.getFieldDataByCondition("employee",
				"em_macaddress", "em_code='" + emcode + "'");
		String sqlUpdate = "update employee set em_macaddress='" + macAddress
				+ "' where em_code='" + emcode + "'";
		if (null == mac) {
			baseDao.execute(sqlUpdate);
		} else {
			// MAC地址存在，对比是否一致，不一致不让打卡，并给出提示
			Object macexist = baseDao.getFieldDataByCondition("employee",
					"em_macaddress", "em_code='" + emcode + "'");
			if (!macexist.toString().equals(macAddress)) {
				int changeCount = baseDao.getCount("select count(*) from mobile_macchange where mm_emcode='"+emcode+"' and mm_status = '已提交'"); 
				if(changeCount > 0){
					BaseUtil.showError("正处于申请变更绑定阶段");
				}else{
					BaseUtil.showError("该设备不是考勤打卡常用设备,是否需要更换?");
				}
			}
		}
	}

	@Override
	public List<Map<String, Object>> queryMobileMac(String emcode,
			String macaddress) {
		// TODO Auto-generated method stub
		// 判断mac地址是否已经绑定了其他用户
		String sqlmac = "select count(1) from employee where em_macaddress='"
				+ macaddress + "' and em_code<>'" + emcode + "' ";
		int count = baseDao.getCount(sqlmac);
		if (count > 0) {
			BaseUtil.showError("该设备已被他人绑定!");
		}
		// 查询设备是否处于变更当中
		String sqlmac1 = "select count(1) from mobile_macchange where mm_macaddress='"
				+ macaddress + "' and mm_status='已提交' ";
		int count1 = baseDao.getCount(sqlmac1);
		if (count1 > 0) {
			BaseUtil.showError("该设备正处于申请变更绑定阶段，不能重复申请!");
		}
		String sql = "select nvl(em_macaddress,'0') macaddress from employee where em_code='"
				+ emcode + "'";
		return baseDao.getJdbcTemplate().queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getWorkDaily(String emcode, int pageIndex) {
		// TODO Auto-generated method stub
		int pageSize = 10;
		int start = ((pageIndex - 1) * pageSize + 1);
		int end = pageIndex * pageSize;
		String sql = "select * from(select a.*,rownum rn from(select wd_id,wd_emp,to_char(wd_date,'yyyy-MM-dd') wd_date,wd_comment,wd_depart,wd_joname,wd_plan,wd_experience,wd_context,case when wd_status<>'已审核' then '待审批' else '已审批' end status,wd_status,wd_unfinishedtask,to_char(wd_entrydate,'yyyy-MM-dd') wd_entrydate from WORKDAILY where wd_empcode='"
				+ emcode
				+ "' order by wd_date desc ) a where rownum<="
				+ end
				+ ") where rn>=" + start + " ";
		return baseDao.getJdbcTemplate().queryForList(sql);
	}
	@Override
	public List<Map<String, Object>> getWorkReports(String emcode,int pageIndex,String caller){
		int pageSize = 10;
		int start = ((pageIndex - 1) * pageSize + 1);
		int end = pageIndex * pageSize;
		String sql = null;
		if("WorkDaily".equals(caller)){
			sql = "select * from(select a.*,rownum rn from(select wd_id,wd_emp,to_char(wd_date,'yyyy-MM-dd') wd_date,to_char(wd_date,'day') wd_weekDays,wd_comment,wd_depart,wd_joname,wd_plan,wd_experience,wd_context,case when wd_status<>'已审核' then '待审批' else '已审批' end status,wd_status,wd_unfinishedtask,to_char(wd_entrydate,'yyyy-MM-dd') wd_entrydate from WORKDAILY where wd_empcode='"
					+ emcode
					+ "' order by wd_date desc,wd_id desc ) a where rownum<="
					+ end
					+ ") where rn>=" + start + " ";
		}else if("WorkWeekly".equals(caller)){
			sql = "select * from(select a.*,rownum rn from(select ww_id,ww_emp,to_char(ww_date,'yyyy-MM-dd') ww_date,ww_comment,ww_depart,ww_joname,ww_plan,ww_experience,ww_context,case when ww_status<>'已审核' then '待审批' else '已审批' end status,ww_status,ww_unfinishedtask,ww_week,to_char(ww_starttime,'yyyy-MM-dd') ww_starttime,to_char(ww_endtime,'yyyy-MM-dd') ww_endtime from WORKWEEKLY where ww_empcode='"
					+ emcode
					+ "' order by ww_starttime desc,ww_date desc ) a where rownum<="
					+ end
					+ ") where rn>=" + start + " ";
		}else if("WorkMonthly".equals(caller)){
			sql = "select * from(select a.*,rownum rn from(select wm_id,wm_emp,to_char(wm_date,'yyyy-MM-dd') wm_date,wm_comment,wm_depart,wm_joname,wm_plan,wm_experience,wm_context,case when wm_status<>'已审核' then '待审批' else '已审批' end status,wm_status,wm_unfinishedtask,wm_month,to_char(wm_starttime,'yyyy-MM-dd') wm_starttime,to_char(wm_endtime,'yyyy-MM-dd') wm_endtime from WORKMONTHLY where wm_empcode='"
					+ emcode
					+ "' order by wm_starttime desc,wm_date desc ) a where rownum<="
					+ end
					+ ") where rn>=" + start + " ";
		}
		if(sql!=null){			
			return baseDao.getJdbcTemplate().queryForList(sql);
		}
		BaseUtil.showError("caller值错误，请设置正确的周报或者月报的caller值");
		return null;
	}

	@Override
	public void addSignCard(String caller, String formStore) {
		// TODO Auto-generated method stub
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object id = baseDao.getFieldDataByCondition("dual",
				"mobile_signcard_seq.nextval", "1=1");
		store.put("ms_id",
				id.toString().substring(1, id.toString().length() - 1));
		String table = "mobile_signcard";
		Object code = baseDao.sGetMaxNumber(table != null ? table.split(" ")[0]
				: caller, 2);
		store.put("ms_code", code);
		store.put("ms_status", "在录入");
		store.put("ms_statuscode", "ENTERING");

		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"mobile_signcard", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 触发审批流
		// 执行提交操作
		baseDao.submit(
				"mobile_signcard",
				"ms_id="
						+ id.toString()
								.substring(1, id.toString().length() - 1),
				"ms_status", "ms_statuscode");
		// 触发审批流
		handlerService.afterSubmit(
				caller,
				Integer.valueOf(id.toString().substring(1,
						id.toString().length() - 1)));
		// updateEmployeeHoliday(language,employee);
		try {
			// 记录操作
			baseDao.logger.save(caller, "ms_id",
					id.toString().substring(1, id.toString().length() - 1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void configUpdate(String caller, String formStore, String gridStore) {
		// TODO Auto-generated method stub
		if (formStore != null && formStore != "" && !formStore.equals("")) {
			String[] arr = formStore.split(",");
			String sql = "";
			// StringBuffer sb1 = new
			// StringBuffer("UPDATE MOBILEFORMDETAIL SET ");
			for (int i = 0; i < arr.length; i++) {
				// sb1.append(arr[i]+"=-1,");
				sql = "UPDATE MOBILEFORMDETAIL SET MFD_ISDEFAULT=-1"
						+ " where mfd_caller='" + caller + "' and mfd_field='"
						+ arr[i] + "'";
				// System.out.println("sql="+sql);
				try {
					baseDao.execute(sql);
				} catch (Exception ex) {
					BaseUtil.showError("明细表字段修改错误");
				}
			}
		}
		if (gridStore != null && gridStore != "" && !gridStore.equals("")) {
			String sqldetail = "";
			String[] arrdetail = gridStore.split(",");
			for (int i = 0; i < arrdetail.length; i++) {
				// sb1.append(arr[i]+"=-1,");
				sqldetail = "UPDATE MOBILEDETAILGRID SET MDG_ISDEFAULT=-1"
						+ " where MDG_CALLER='" + caller + "' and MDG_FIELD='"
						+ arrdetail[i] + "'";
				// System.out.println("sql="+sql);
				try {
					baseDao.execute(sqldetail);
				} catch (Exception ex) {
					BaseUtil.showError("明细表字段修改错误");
				}
			}
		}

	}

	@Override
	public void commonUpdate(String caller, String formStore, String gridStore,
			int keyid) {
		// TODO Auto-generated method stub
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		String table = "";
		String keyfield = "";
		String detailtable = "";
		String detailkeyfield = "";
		String detno = "";
		String mainkey = "";
		String detailseq = "";
		int detailidvalue = 0;
		String status = "";
		String statuscode = "";
		if (caller.equals("Ask4Leave")) {
			table = "Vacation";
			keyfield = "va_id";
			status = "va_status";
			statuscode = "va_statuscode";
		} else if (caller.equals("SpeAttendance")) {
			table = "SpeAttendance";
			keyfield = "sa_id";
			status = "sa_status";
			statuscode = "sa_statuscode";
		} else if (caller.equals("Workovertime")) {
			table = "Workovertime";
			keyfield = "wo_id";
			detailtable = "Workovertimedet";
			detailkeyfield = "wod_id";
			detno = "wod_detno";
			mainkey = "wod_woid";
			detailseq = "WORKOVERTIMEDET_SEQ";
			status = "wo_status";
			statuscode = "wo_statuscode";
		} else if (caller.equals("FeePlease!CCSQ")) {
			table = "FeePlease";
			keyfield = "fp_id";
			detailtable = "FeePleaseDetail";
			detailkeyfield = "fpd_id";
			detno = "fpd_detno";
			mainkey = "fpd_fpid";
			detailseq = "FEEPLEASEDETAIL_SEQ";
			status = "fp_status";
			statuscode = "fp_statuscode";
		}
		
		if("".equals(table)){
			Map<String,String> map = getFormConfig(caller);
			table = map.get("table");
			if ("CUSTOMTABLE".equals(table.toUpperCase())) {
				store.remove("keyvalue");
			}
			keyfield = map.get("keyfield");
			status = map.get("statusfield");
			statuscode = map.get("statuscodefield");
			detailtable = map.get("detailtable");
			detailkeyfield = map.get("detailkeyfield");
			detno = map.get("detaildetno");
			mainkey = map.get("detailmainkeyfield");
			detailseq = map.get("detailseq");			
		}
		
		// 给主记录store设置id
		// store.put(keyfield, keyid);
		if ("Ask4Leave".equals(caller)) {
			store.put("va_emcode",
					baseDao.getFieldDataByCondition(
							"vacation", "va_emcode",
							keyfield + "=" + keyid));
		}
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		
		String formSql = SqlUtil
				.getUpdateSqlByFormStore(store, table, keyfield);
		baseDao.execute(formSql);

		/*
		 * for (int i = 0; i < gstore.size(); i++) { //
		 * gstore.get(i).put("mpd_id", //
		 * baseDao.getSeqId("MOBILE_OUTPLANDETAIL_SEQ")); //
		 * 根据主表ID找出明细表ID,put到gstore里 Object detailid =
		 * baseDao.getFieldDataByCondition(detailtable, detailkeyfield, mainkey
		 * + "=" + keyid + " and " + detno + "=" + gstore.get(i).get(detno) +
		 * ""); if (null != detailid) { gstore.get(i).put(detailkeyfield,
		 * detailid); gstore.get(i).put(mainkey, keyid); } else { detailidvalue
		 * = baseDao.getSeqId(detailseq); gstore.get(i).put(detailkeyfield,
		 * detailidvalue); }
		 * 
		 * }
		 */

		if (gridStore != "" && !"".equals(gridStore) && gridStore != null&&gstore.size()>0) {
			List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
					detailtable, detailkeyfield);
			int i = 0;
			for (Map<Object, Object> s : gstore) {
				if (s.get(detailkeyfield) == null
						|| s.get(detailkeyfield).equals("")
						|| s.get(detailkeyfield).equals("0")
						|| Integer.parseInt(s.get(detailkeyfield).toString()) == 0) {// 新添加的数据，id不存在
					int id = baseDao.getSeqId(detailseq);
					// 获取当前记录明细序号的最大值
					try {
						Object detnovalue = baseDao.getFieldDataByCondition(
								detailtable, "max(" + detno + ")", mainkey
										+ "=" + keyid);
						if (null == detnovalue) {
							detnovalue = 1 + (i++);
						} else {
							detnovalue = Integer.valueOf(detnovalue.toString()) + 1 + (i++);
						}
						s.put(detno, detnovalue);

						if ("Workovertime".equals(caller)) {
							s.put("wod_empcode",
									baseDao.getFieldDataByCondition(
											"Workovertime", "wo_emcode",
											keyfield + "=" + keyid));
							s.put("wod_empname", baseDao
									.getFieldDataByCondition("Workovertime",
											"wo_recorder", keyfield + "="
													+ keyid));

						}
					} catch (Exception ex) {

					}

					String sql = SqlUtil.getInsertSqlByMap(s, detailtable,
							new String[] { detailkeyfield },
							new Object[] { id });
					gridSql.add(sql);
				}
			}
			baseDao.execute(gridSql);
		}

		//记录操作
		baseDao.logger.update(caller, keyfield, store.get(keyfield));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, keyid);
		// 执行提交操作
		baseDao.submit(table, keyfield + "=" + keyid, status, statuscode);
		// 记录操作
		baseDao.logger.submit(caller, keyfield, keyid);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { keyid });

	}

	@Override
	public void commondelete(String caller, int id) {
		// TODO Auto-generated method stub
		String table = "";
		String keyfield = "";
		String detailtable = "";
		String detailkeyfield = "";
		String status = "";
		if (caller.equals("Ask4Leave")) {
			table = "Vacation";
			keyfield = "va_id";
			status = "va_status";
		} else if (caller.equals("SpeAttendance")) {
			table = "SpeAttendance";
			keyfield = "sa_id";
			status = "sa_status";
		} else if (caller.equals("Workovertime")) {
			table = "Workovertime";
			keyfield = "wo_id";
			detailtable = "Workovertimedet";
			detailkeyfield = "wod_woid";
			status = "wo_status";
		} else if (caller.equals("FeePlease!CCSQ")) {
			table = "FeePlease";
			keyfield = "fp_id";
			detailtable = "FeePleaseDetail";
			detailkeyfield = "fpd_fpid";
			status = "fp_status";
		}
		if (caller.equals("WorkDaily")) {
			table = "WorkDaily";
			keyfield = "wd_id";
			status = "wd_status";
		}else if (caller.equals("WorkWeekly")) {
			table = "WorkWeekly";
			keyfield = "ww_id";
			status = "ww_status";
		}else if (caller.equals("WorkMonthly")) {
			table = "WorkMonthly";
			keyfield = "wm_id";
			status = "wm_status";
		}else if(("DeviceChange!Use").equals(caller)){
			table = "DeviceChange";
			keyfield = "dc_id";
			status = "dc_status";
		}else if(("DeviceChange!Scrap").equals(caller)){
			table = "DeviceChange";
			keyfield = "dc_id";
			status = "dc_status";
		}else if(("DeviceChange!Maintain").equals(caller)){
			table = "DeviceChange";
			keyfield = "dc_id";
			status = "dc_status";
		}
		
		if("".equals(table)){
			Map<String,String> map = getFormConfig(caller);
			table = map.get("table");
			keyfield = map.get("keyfield");
			status = map.get("statusfield");
		}
		
		Object mainstatus = baseDao.getFieldDataByCondition(table, status,
				keyfield + "=" + String.valueOf(id));
		String sql = "";
		String detailsql = "";
		if (null != mainstatus) {
			if (!mainstatus.equals("已审核")) {
				// 执行删除前的其它逻辑
				handlerService.handler(caller, "delete", "before",
						new Object[] { id });

				sql = "delete from " + table + " where " + keyfield + "=" + id
						+ "";

				// System.out.println("sql=" + sql);
				if (caller.equals("Workovertime")
						|| caller.equals("FeePlease!CCSQ")) {
					detailsql = "delete from " + detailtable + " where "
							+ detailkeyfield + "=" + id + "";
					// System.out.println("detailsql="+detailsql);
					baseDao.execute(detailsql);
				}
				baseDao.execute(sql);
				// 记录操作
				baseDao.logger.delete(caller, keyfield, id);
				// 执行删除后的其它逻辑
				handlerService.handler(caller, "delete", "after",
						new Object[] { id });

			} else {
				BaseUtil.showError("不能删除已审核的单据");
			}
		}
	}

	@Override
	public List<Map<String, Object>> mobileoutplan(String emcode) {
		// TODO Auto-generated method stub
		String sql = "select * from(select distinct mpd_id,mp_id,mpd_mpid,mpd_distance,mpd_company,mpd_address,to_char(mpd_arrivedate,'yyyy-MM-dd hh24:mi:ss') mpd_arrivedate,MD_LONGITUDE,MD_LATITUDE,mpd_actdate,mpd_outdate,to_char(mpd_recorddate,'yyyy-MM-dd hh24:mi:ss') mpd_recorddate,mpd_location,mpd_status,mpd_remark,mpd_kind from  MOBILE_OUTPLAN left join MOBILE_OUTPLANdetail on mp_id=mpd_mpid  and MP_RECORDERCODE='"
				+ emcode
				+ "' and  to_char(mpd_arrivedate,'yyyy-MM-dd')=to_char(sysdate,'yyyy-MM-dd') left join mobile_outaddress on mpd_company=md_company) where mpd_company is not null and nvl(mpd_status,' ')<>'签退' order by mpd_id";
		return baseDao.queryForList(sql);

	}

	@Override
	public void addAutoSign(String caller, String formStore, int mpd_id) {
		// TODO Auto-generated method stub
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int id = baseDao.getSeqId("mobile_outsign_seq");
		store.put("mo_id", id);
		String table = "mobile_outsign";
		Object code = baseDao.sGetMaxNumber(table != null ? table.split(" ")[0]
				: caller, 2);
		store.put("mo_code", code);
		store.put("mo_status", "在录入");
		store.put("mo_statuscode", "ENTERING");
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存主表
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"mobile_outsign", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 触发审批流
		// 执行提交操作
		baseDao.submit("mobile_outsign", "mo_id=" + id, "mo_status",
				"mo_statuscode");
		/*
		 * // 触发审批流 handlerService.afterSubmit(caller, id);
		 */
		// 查询对应的外勤计划，是否签到，没有签到则更新实际到达时间
		Object acttime = baseDao.getFieldDataByCondition(
				"mobile_outplandetail", "mpd_actdate", "mpd_id=" + mpd_id);
		String updatesql = "";
		if (null == acttime) {
			updatesql = "update mobile_outplandetail set mpd_actdate=sysdate where mpd_id="
					+ mpd_id + "";
		} else {
			updatesql = "update mobile_outplandetail set mpd_outdate=sysdate where mpd_id="
					+ mpd_id + "";
		}
		baseDao.execute(updatesql);

		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public String yesornoplan(String emcode) {
		// TODO Auto-generated method stub
		String sql = "select * from mobile_outplan,mobile_outplandetail where mp_id=mpd_mpid and MP_RECORDERCODE='"
				+ emcode
				+ "' and to_char(mp_visittime,'yyyy-MM-dd')=to_char(sysdate,'yyyy-MM-dd') and mpd_actdate is null";
		int count = baseDao.getCount(sql);
		if (count > 0) {
			return "1";
		} else {
			return "0";
		}

	}

	@Override
	public Map<String, Object> getFormAndGridDetail(String caller,
			String condition, int id) {
		// TODO Auto-generated method stub

		Map<String, Object> detailData = new HashMap<String, Object>();
		// 取formdetail数据
		List<Object[]> formDatas = null;
		List<Object[]> gridDatas = null;
		List<Map<String, Object>> foData = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> detailGridData = new ArrayList<Map<String, Object>>();

		// 存放逻辑类型
		List<Map<String, Object>> logicData = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> gridLogicData = new ArrayList<Map<String, Object>>();

		String table = "";
		String keyfield = "";
		String detailtable = "";
		String detailkeyfield = "";
		String detailkey = "";
		String status = "";

		if (caller.equals("Ask4Leave")) {
			table = "Vacation";
			keyfield = "va_id";
			status = "va_status";
		} else if (caller.equals("SpeAttendance")) {
			table = "SpeAttendance";
			keyfield = "sa_id";
			status = "sa_status";
		} else if (caller.equals("Workovertime")) {
			table = "Workovertime";
			keyfield = "wo_id";
			detailtable = "Workovertimedet";
			detailkeyfield = "wod_woid";
			status = "wo_status";
			detailkey = "wod_id";

		} else if (caller.equals("FeePlease!CCSQ")) {
			table = "FeePlease";
			keyfield = "fp_id";
			detailtable = "FeePleaseDetail";
			detailkeyfield = "fpd_fpid";
			status = "fp_status";
			detailkey = "fpd_id";
		}

		if("".equals(table)){
			Map<String,String> map = getFormConfig(caller);
			table = map.get("table");
			keyfield = map.get("keyfield");
			status = map.get("statusfield");
			detailtable = map.get("detailtable");
			detailkey = map.get("detailkeyfield");	
			detailkeyfield = map.get("detailmainkeyfield");	
		}
		
		if (condition != null) {
			try {
				formDatas = baseDao
						.getFieldsDatasByCondition(
								"(mobileformdetail left join form on fo_caller=mfd_caller left join formdetail on fd_foid=fo_id and upper(fd_field)=upper(mfd_field))",
								new String[] { "FD_DETNO", "FD_CAPTION",
										"FD_FIELD", "FD_TYPE", "FD_ALLOWBLANK",
										"FD_GROUP", "MFD_ISDEFAULT",
										"fd_dbfind", "fd_id", "fd_logictype",
										"FD_FIELDLENGTH","mfd_caption","fd_readonly" }, "mfd_caller='"
										+ caller + "' and " + condition
										+ " order by fd_group,fd_detno");
				gridDatas = baseDao
						.getFieldsDatasByCondition(
								"(mobiledetailgrid left join detailgrid on dg_caller=mdg_caller and upper(dg_field)=upper(mdg_field))",
								new String[] { "DG_SEQUENCE", "DG_CAPTION",
										"DG_FIELD", "DG_TYPE", "DG_LOGICTYPE",
										"mdg_isdefault", "dg_id",
										"DG_MAXLENGTH","mdg_caption" ,"dg_dbbutton"}, "mdg_caller='"
										+ caller + "' and " + condition
										+ " order by dg_sequence");
			} catch (Exception e) {
				e.printStackTrace();
				BaseUtil.showError("参数错误");
			}
			if (formDatas != null) {
				Map<String, Object> fdData = null;
				Object value = "";
				for (Object[] data : formDatas) {
					fdData = new HashMap<String, Object>();
					fdData.put("fd_detno", data[0]);
					fdData.put("fd_caption", data[1]);
					fdData.put("fd_field", data[2]);
					// 增加字段长度
					if (data[10] == "null" || null == data[10]) {
						data[10] = 0;
					}
					;
					fdData.put("fd_maxlength", data[10]);

					String changeType = null;
					if (data[3] != null) {
						changeType = changeFieldType("fieldtype",
								data[3].toString(), "form");
					}

					if (data[7] != null) {
						if (!"F".equals(data[7].toString())) {
							changeType = changeFieldType("dbfind",
									data[7].toString(), "form");
							fdData.put("fd_type", changeType);
						} else {
							fdData.put("fd_type", data[3] == null ? ""
									: changeType);
						}
					} else {
						fdData.put("fd_type", data[3] == null ? "" : changeType);
					}
					// 给主表字段增加值
					if (fdData.get("fd_type").equals("D")) {
						data[2] = "to_char(" + data[2]
								+ ",'yyyy-MM-dd hh24:mi:ss') " + data[2];
					}
					if (null != data[2]) {
						value = baseDao.getFieldDataByCondition(table,
								data[2].toString(), keyfield + "=" + id);
					}

					if (null == value) {
						value = "";
					}
					;
					// System.out.println(data[2]+"="+value);
					fdData.put("fd_value", value);

					if (data[9] != null && !"".equals(data[9])) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("logicType", data[9].toString().toLowerCase());
						map.put("type", changeType);
						logicData.add(map);
					}

					fdData.put("fd_readonly", "T".equals(data[12])?"T":"F");
					fdData.put("fd_group", data[5]);
					fdData.put("mfd_isdefault", data[6]);
					fdData.put("fd_id", data[8]);
					fdData.put("mfd_caption", data[11]);
					foData.add(fdData);
				}
			}

			// form逻辑类型转换
			if (logicData.size() > 0) {
				for (Map<String, Object> map : logicData) {
					String logicType = map.get("logicType").toString();
					String type = map.get("type").toString();
					for (Map<String, Object> formMap : foData) {
						if(StringUtil.hasText(formMap.get("fd_field"))){
							if (logicType.equals(formMap.get("fd_field").toString()
									.toLowerCase())) {
								formMap.remove("fd_type");
								formMap.put("fd_type", type);
								Object value = "";
								Object field = formMap.get("fd_field");
								if (formMap.get("fd_type").equals("D")) {
									field = "to_char(" + field
											+ ",'yyyy-MM-dd hh24:mi:ss') " + field;
								}
								value = baseDao.getFieldDataByCondition(table,
										field.toString(), keyfield + "=" + id);
								if (null == value) {
									value = "";
								}
								;
								// System.out.println(data[2]+"="+value);
								formMap.put("fd_value", value);
							}							
						}

					}
				}
			}

			if (gridDatas != null&&gridDatas.size()>0) {
				Map<String, Object> gdData = null;
				// 先查找对应的名字记录，有多少行
				String sql = "";
				int count = 0;
				if (detailtable != "" && !detailtable.equals("")) {
					sql = "select count(1) from " + detailtable + " where "
							+ detailkeyfield + "=" + id + "";
					// System.out.println("sql="+sql);
					count = baseDao.getCount(sql);
					// System.out.println("count="+count);
				}

				// System.exit(0);
				for (Object[] data : gridDatas) {
					Object detailvalue = "";
					if (count > 0) {
						Object detailid = baseDao.getFieldDatasByCondition(
								detailtable, detailkey, detailkeyfield + "="
										+ id + " order by " + detailkey
										+ " asc");
						// System.out.println("detailid="+detailid.toString());
						String[] arr = detailid.toString()
								.substring(1, detailid.toString().length() - 1)
								.split(",");
						for (int m = 0; m < count; m++) {
							gdData = new HashMap<String, Object>();
							gdData.put("dg_sequence", data[0]);
							gdData.put("dg_caption", data[1]);
							gdData.put("dg_field", data[2]);
							gdData.put("mdg_caption", data[8]);
							if (data[7] == "null" || null == data[7]) {
								data[7] = 0;
							}
							;
							gdData.put("dg_maxlength", data[7]);

							String changeType = null;
							if (data[3] != null) {
								changeType = changeFieldType(null,
										data[3].toString(), "grid");
								if(data[9]!=null&&!"".equals(data[9])){
									if(!"0".equals(data[9].toString())){
										changeType = "DF";
									}
								}
							}

							gdData.put("dg_type", data[3] == null ? ""
									: changeType);

							if (data[4] != null && !"".equals(data[4])) {
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("logicType", data[4].toString()
										.toLowerCase());
								map.put("type", changeType);
								gridLogicData.add(map);
							}

							gdData.put("dg_logictype", data[4]);

							gdData.put("mdg_isdefault", data[5]);
							gdData.put("gd_id", data[6]);

							gdData.put("dg_group", m + 1);
							String a = data[2].toString();
							if (gdData.get("dg_type").equals("D")) {
								data[2] = "to_char(" + data[2]
										+ ",'yyyy-MM-dd hh24:mi:ss') "
										+ data[2] + "";
							}
							detailvalue = baseDao.getFieldDataByCondition(
									detailtable, data[2].toString(), detailkey
											+ "=" + arr[m]);
							data[2] = a;
							if (null == detailvalue) {
								detailvalue = "";
							}
							;
							gdData.put("dg_value", detailvalue);
							detailGridData.add(gdData);
						}
					} else {
						gdData = new HashMap<String, Object>();
						gdData.put("dg_sequence", data[0]);
						gdData.put("dg_caption", data[1]);
						gdData.put("dg_field", data[2]);
						if (data[7] == "null" || null == data[7]) {
							data[7] = 0;
						}
						;
						gdData.put("dg_maxlength", data[7]);
						String changeType = null;
						if (data[3] != null) {
							changeType = changeFieldType(null,
									data[3].toString(), "grid");
							if(data[9]!=null&&!"".equals(data[9])){
								if(!"0".equals(data[9].toString())){
									changeType = "DF";
								}
							}
						}

						gdData.put("dg_type", data[3] == null ? "" : changeType);

						if (data[4] != null && !"".equals(data[4])) {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("logicType", data[4].toString()
									.toLowerCase());
							map.put("type", changeType);
							gridLogicData.add(map);
						}

						gdData.put("dg_logictype", data[4]);

						gdData.put("mdg_isdefault", data[5]);
						gdData.put("gd_id", data[6]);

						gdData.put("dg_value", "");
						gdData.put("dg_group", 0);
						gdData.put("mdg_caption", data[8]);
						detailGridData.add(gdData);
					}

				}
			}

			// grid逻辑类型转换
			/*
			 * if(gridLogicData.size()>0){ for(Map<String,Object>
			 * map:logicData){ String logicType =
			 * map.get("logicType").toString();
			 * System.out.println("logicType="+logicType);
			 * 
			 * String type = map.get("type").toString(); for(Map<String,Object>
			 * gridMap:detailGridData){
			 * System.out.println("fd_field="+gridMap.get("fd_field"));
			 * if(logicType
			 * .equals(gridMap.get("fd_field").toString().toLowerCase())){
			 * gridMap.remove("fd_type"); gridMap.put("fd_type", type); } } } }
			 */
		}
		detailData.put("formdetail", foData);
		detailData.put("gridetail", detailGridData);

		return detailData;

	}

	// 从UAS自定义类型到移动端所用类型
	private String changeFieldType(String field, String type, String formOrGrid) {
		String changeType = type;
		if ("form".equals(formOrGrid)) {
			if ("fieldtype".equals(field)) {

				if ("S".equals(type)) {
					changeType = "SS";
				} else if ("IN".equals(type)) {
					changeType = "N";
				} else if ("SN".equals(type)) {
					changeType = "N";
				} else if ("DT".equals(type)) {
					changeType = "D";
				} else if ("TF".equals(type)) {
					changeType = "D";
				} else if ("T".equals(type)) {
					changeType = "MS";
				} else if ("Html".equals(type)) {
					changeType = "MS";
				} else if ("CF".equals(type)) {
					changeType = "SS";
				} else if ("CDHM".equals(type)) {
					changeType = "D";
				} else if ("MT".equals(type)) {
					changeType = "SF";
				}else if ("DHMC".equals(type)) {
					changeType = "D";
				}
			} else if ("dbfind".equals(field)) {
				if ("form".equals(formOrGrid)) {
					if ("T".equals(type)) {
						changeType = "SF";
					} else if ("AT".equals(type)) {
						changeType = "MF";
					} else if ("M".equals(type)) {
						changeType = "MF";
					}
				}
			}
		} else if ("grid".equals(formOrGrid)) {
			if ("text".equals(type)) {
				changeType = "S";
			} else if ("numbercolumn".equals(type)) {
				changeType = "N";
			} else if ("floatcolumn".equals(type)) {
				changeType = "N";
			} else if ("floatcolumn4".equals(type)) {
				changeType = "N";
			} else if ("floatcolumn6".equals(type)) {
				changeType = "N";
			} else if ("combo".equals(type)) {
				changeType = "C";
			} else if ("yncolumn".equals(type)) {
				changeType = "C";
			} else if ("tfcolumn".equals(type)) {
				changeType = "C";
			} else if ("datecolumn".equals(type)) {
				changeType = "D";
			} else if ("datetimecolumn".equals(type)) {
				changeType = "D";
			} else if ("datetimecolumn2".equals(type)) {
				changeType = "D";
			} else if ("texttrigger".equals(type)) {
				changeType = "MS";
			} else if ("textareafield".equals(type)) {
				changeType = "MS";
			}else if ("checkcolumn-1".equals(type)){
				changeType = "YN";
			}else if("checkcolumn".equals(type)){
				changeType = "B";
			}
		}
		return changeType;
	}

	@Override
	public void addOutSet(String caller, String formStore) {
		// TODO Auto-generated method stub
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 外勤设置，原则上表里面只能有一条数
		int count = baseDao.getCount("select count(1) from mobile_outset");
		if (count > 0) {
			Object moid = baseDao.getFieldDataByCondition("mobile_outset",
					"mo_id", "rownum=1");
			store.put("mo_id", moid);
			String formSql = SqlUtil.getUpdateSqlByFormStore(store,
					"mobile_outset", "mo_id");
			baseDao.execute(formSql);
		} else {
			// 保存主表
			int id = baseDao.getSeqId("mobile_outset_seq");
			store.put("mo_id", id);
			String formSql = SqlUtil.getInsertSqlByFormStore(store,
					"mobile_outset", new String[] {}, new Object[] {});
			baseDao.execute(formSql);
		}
	}

	@Override
	public List<Map<String, Object>> getOutSet() {
		// TODO Auto-generated method stub
		try {
			String sql = "select MO_DISTANCE,MO_TIME,MO_AUTOSIGN,MO_NEEDPROCESS from mobile_outset where rownum=1";

			return baseDao.queryForList(sql);
		} catch (Exception ex) {
			return null;
		}

	}

	@Override
	public void commonres(String caller, int id) {
		String table = "";
		String keyfield = "";
		String detailtable = "";
		String detailkeyfield = "";
		String status = "";
		String statuscode = "";
		if (caller.equals("Ask4Leave")) {
			table = "Vacation";
			keyfield = "va_id";
			status = "va_status";
			statuscode = "va_statuscode";
		} else if (caller.equals("SpeAttendance")) {
			table = "SpeAttendance";
			keyfield = "sa_id";
			status = "sa_status";
			statuscode = "sa_statuscode";
		} else if (caller.equals("Workovertime")) {
			table = "Workovertime";
			keyfield = "wo_id";
			detailtable = "Workovertimedet";
			detailkeyfield = "wod_woid";
			status = "wo_status";
			statuscode = "wo_statuscode";
		} else if (caller.equals("FeePlease!CCSQ")) {
			table = "FeePlease";
			keyfield = "fp_id";
			detailtable = "FeePleaseDetail";
			detailkeyfield = "fpd_fpid";
			status = "fp_status";
			statuscode = "fp_statuscode";
		}
		if (caller.equals("WorkDaily")) {
			table = "WorkDaily";
			keyfield = "wd_id";
			status = "wd_status";
			statuscode = "wd_statuscode";
		}else if (caller.equals("WorkWeekly")) {
			table = "WorkWeekly";
			keyfield = "ww_id";
			status = "ww_status";
			statuscode = "ww_statuscode";
		}else if (caller.equals("WorkMonthly")) {
			table = "WorkMonthly";
			keyfield = "wm_id";
			status = "wm_status";
			statuscode = "wm_statuscode";
		}else if(("DeviceChange!Use").equals(caller)){
			table = "DeviceChange";
			keyfield = "dc_id";
			status = "dc_status";
			statuscode = "dc_statuscode";
		}else if(("DeviceChange!Scrap").equals(caller)){
			table = "DeviceChange";
			keyfield = "dc_id";
			status = "dc_status";
			statuscode = "dc_statuscode";
		}else if(("DeviceChange!Maintain").equals(caller)){
			table = "DeviceChange";
			keyfield = "dc_id";
			status = "dc_status";
			statuscode = "dc_statuscode";
		}
		
		if("".equals(table)){
			Map<String,String> map = getFormConfig(caller);
			table = map.get("table");
			keyfield = map.get("keyfield");
			status = map.get("statusfield");
			statuscode = map.get("statuscodefield");
		}
		
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object thisstatus = baseDao.getFieldDataByCondition(table, statuscode,
				"" + keyfield + "=" + id);
		if(thisstatus==null){
			BaseUtil.showError("该单已不存在");
		}
		StateAssert.resSubmitOnlyCommited(thisstatus);
		handlerService.handler(caller, "resCommit", "before",
				new Object[] { id });
		// 执行反提交操作
		baseDao.resOperate(table, "" + keyfield + "=" + id, status, statuscode);
		// 记录操作
		baseDao.logger.resSubmit(caller, keyfield, id);
		handlerService.handler(caller, "resCommit", "after",
				new Object[] { id });
	}

	@Override
	public List<Map<String, Object>> getsingledata(int id) {
		try {
			String sql = "select  wd_id,wd_date,WD_COMMENT,WD_CONTEXT,WD_PLAN,WD_EXPERIENCE,WD_DEPART,WD_JOCODE,WD_JONAME,WD_HRORG,WD_COMMENT,wd_unfinishedtask,wd_entrydate,wd_code from WORKDAILY where wd_id="
					+ id + "";

			return baseDao.queryForList(sql);
		} catch (Exception ex) {
			return null;
		}
	}

	@Override
	public List<Map<String, Object>> getsingleWorkReports(String caller ,int id) {
		try {
			String sql = null;
			if("WorkDaily".equals(caller)){
				sql = "select  wd_id,to_char(wd_date,'yyyy-MM-dd') wd_date,to_char(wd_date,'day') wd_weekDays,case when wd_status<>'已审核' then '待审批' else '已审批' end status,wd_status,WD_COMMENT,WD_CONTEXT,WD_PLAN,WD_EXPERIENCE,WD_DEPART,WD_JOCODE,WD_JONAME,WD_HRORG,WD_COMMENT,wd_unfinishedtask,to_char(wd_entrydate,'yyyy-MM-dd') wd_entrydate,wd_code from WORKDAILY where wd_id="
						+ id + "";
			}else if("WorkWeekly".equals(caller)){				
				sql = "select  ww_id,to_char(ww_date,'yyyy-MM-dd') ww_date,case when ww_status<>'已审核' then '待审批' else '已审批' end status,ww_status,WW_COMMENT,WW_CONTEXT,WW_PLAN,WW_EXPERIENCE,WW_DEPART,WW_JOCODE,WW_JONAME,WW_HRORG,WW_COMMENT,ww_unfinishedtask,ww_week,to_char(ww_starttime,'yyyy-MM-dd') ww_starttime,to_char(ww_endtime,'yyyy-MM-dd') ww_endtime,ww_code from WORKWEEKLY where ww_id="
						+ id + "";
			}else if("WorkMonthly".equals(caller)){
				sql = "select  wm_id,to_char(wm_date,'yyyy-MM-dd') wm_date,case when wm_status<>'已审核' then '待审批' else '已审批' end status,wm_status,WM_COMMENT,WM_CONTEXT,WM_PLAN,WM_EXPERIENCE,WM_DEPART,WM_JOCODE,WM_JONAME,WM_HRORG,WM_COMMENT,wm_unfinishedtask,wm_month,to_char(wm_starttime,'yyyy-MM-dd') wm_starttime,to_char(wm_endtime,'yyyy-MM-dd') wm_endtime,wm_code from WORKMONTHLY where wm_id="
						+ id + "";
			}
			if(sql!=null){				
				return baseDao.queryForList(sql);
			}
			return null;
		} catch (Exception ex) {
			return null;
		}
	}
	
	@Override
	public void mobileplanUpdate(int id) {
		// TODO Auto-generated method stub
		String updateSQL = "update mobile_outplandetail set mpd_status='签退' where mpd_id="
				+ id + "";
		baseDao.execute(updateSQL);
	}
	
	public Map<String,String> getFormConfig(String caller){
		Map<String,String> map = new HashMap<String,String>();
		
		Object[] basicFields = baseDao.getFieldsDataByCondition("form", new String[]{"fo_keyfield","fo_codefield","fo_statusfield","fo_statuscodefield","fo_seq","fo_table",
				"fo_detailtable","fo_detailseq","fo_detailkeyfield","fo_detailmainkeyfield","FO_DETAILDETNOFIELD"}, "fo_caller='" + caller + "'");
		
		if(basicFields!=null){		
			for(int i=0;i<6;i++){
				if(basicFields[i]==null||"".equals(basicFields[i])){
					BaseUtil.showError("请检查相应表单form配置");
				}
			}
			map.put("keyfield", basicFields[0].toString());
			map.put("codefield", basicFields[1].toString());
			map.put("statusfield", basicFields[2].toString());
			map.put("statuscodefield", basicFields[3].toString());
			map.put("seq", basicFields[4].toString());
			
			String table = basicFields[5].toString();
			if(!"ExtraWork$".equals(caller)){
				if(table.toUpperCase().indexOf("LEFT JOIN")>-1){
					table = table.substring(0,table.toUpperCase().indexOf("LEFT JOIN"));
				}else if(table.toUpperCase().indexOf("RIGHT JOIN")>-1){
					table = table.substring(0,table.toUpperCase().indexOf("RIGHT JOIN"));
				}				
			}
			
			map.put("table", table);
			if(basicFields[6]!=null&&!"".equals(basicFields[6])){ //有明细表
				for(int i=6;i<basicFields.length;i++){
					if(basicFields[i]==null||"".equals(basicFields[i])){
						BaseUtil.showError("请检查相应表单form配置");
					}
				}
				
				String detailtable = basicFields[6].toString();
				if(detailtable.toUpperCase().indexOf("LEFT JOIN")>-1){
					detailtable = detailtable.substring(0,detailtable.toUpperCase().indexOf("LEFT JOIN"));
				}else if(detailtable.toUpperCase().indexOf("RIGHT JOIN")>-1){
					detailtable = detailtable.substring(0,detailtable.toUpperCase().indexOf("RIGHT JOIN"));
				}
				
				map.put("detailtable",detailtable);
				map.put("detailseq", basicFields[7].toString());
				map.put("detailkeyfield", basicFields[8].toString());
				map.put("detailmainkeyfield", basicFields[9].toString());
				map.put("detaildetno", (basicFields[10]==null?"":basicFields[10].toString()));
			}
			
		}else{
			BaseUtil.showError("请检查相应表单form配置");
		}
		return map;
	}


	@Override
	public Map<String, Object> getWorkReportInit(String emcode, String caller) {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date(System.currentTimeMillis());
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			if("WorkDaily".equals(caller)){
				map.put("wd_date", format.format(date));
				//获取当前是周几
				String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
				int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		        map.put("wd_weekDays", weekDays[w]);
		        w = w-1;
		        if(w<0){
		        	w=6;
		        }
		        map.put("wd_weekDaysOld", weekDays[w]);
				calendar.add(Calendar.DATE, -1);
				Date dateOld = calendar.getTime();
				map.put("wd_dateOld", format.format(dateOld));
				//获取工作计划
				Object wd_plan = baseDao.getFieldDataByCondition("WorkDaily", "wd_plan", "wd_empcode='"+emcode+"' and wd_statuscode<>'ENTERING' and trunc(wd_date)+1=trunc(to_date('"+map.get("wd_date")+"','yyyy-mm-dd'))");
				if(wd_plan!=null){
					map.put("wd_plan", wd_plan);
				}
				Object wd_planOld = baseDao.getFieldDataByCondition("WorkDaily", "wd_plan", "wd_empcode='"+emcode+"' and wd_statuscode<>'ENTERING' and trunc(wd_date)+1=trunc(to_date('"+map.get("wd_dateOld")+"','yyyy-mm-dd'))");
				if(wd_planOld!=null){
					map.put("wd_planOld", wd_planOld);
				}
			}else if("WorkWeekly".equals(caller)){
				//获取本周的周次以及本周一和周日
				if(calendar.get(Calendar.DAY_OF_WEEK)==1){
					calendar.add(Calendar.DATE, -1);
				}
				int week = calendar.get(Calendar.WEEK_OF_YEAR);
		        String weekString = null;
		        if(week<10){
		        	weekString = "0"+week;
		        }else{
		        	weekString = ""+week;
		        }
				map.put("ww_week",weekString);
				calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		        Date date_start = calendar.getTime();
				map.put("ww_starttime", format.format(date_start));
				calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
				calendar.add(Calendar.DATE, 1);
				Date date_end = calendar.getTime();
				map.put("ww_endtime", format.format(date_end));
				//获取上周的周次以及上周一和周日
				calendar.add(Calendar.DATE, -8);
				int weekOld = calendar.get(Calendar.WEEK_OF_YEAR);
		        String weekStringOld = null;
		        if(weekOld<10){
		        	weekStringOld = "0"+weekOld;
		        }else{
		        	weekStringOld = ""+weekOld;
		        }
				map.put("ww_weekOld",weekStringOld);
				calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		        Date date_startOld = calendar.getTime();
				map.put("ww_starttimeOld", format.format(date_startOld));
				calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
				calendar.add(Calendar.DATE, 1);
				Date date_endOld = calendar.getTime();
				map.put("ww_endtimeOld", format.format(date_endOld));
				//获取工作计划
				Object wd_plan = baseDao.getFieldDataByCondition("WorkWeekly", "ww_plan", "ww_empcode='"+emcode+"' and ww_statuscode<>'ENTERING' and trunc(ww_endtime)+1=trunc(to_date('"+map.get("ww_starttime")+"','yyyy-mm-dd'))");
				if(wd_plan!=null){
					map.put("ww_plan", wd_plan);
				}
				Object wd_planOld = baseDao.getFieldDataByCondition("WorkWeekly", "ww_plan", "ww_empcode='"+emcode+"' and ww_statuscode<>'ENTERING' and trunc(ww_endtime)+1=trunc(to_date('"+map.get("ww_starttimeOld")+"','yyyy-mm-dd'))");
				if(wd_planOld!=null){
					map.put("ww_planOld", wd_planOld);
				}
			}else if("WorkMonthly".equals(caller)){
				//获取本月的月份以及本月一号和最后一天
				int month = calendar.get(Calendar.MONTH)+1;	
				String monthString = null;
				if(month<10){
					monthString = "0"+month;
				}else{
					monthString = ""+month;
				}
				map.put("wm_month",monthString);
				calendar.set(Calendar.DAY_OF_MONTH, 1);
		        Date date_start = calendar.getTime();
				map.put("wm_starttime", format.format(date_start));
				calendar.add(Calendar.MONTH, 1);
				calendar.add(Calendar.DATE, -1);
				Date date_end = calendar.getTime();
				map.put("wm_endtime", format.format(date_end));
				//获取上月的月份以及上月一号和最后一天
				calendar.add(Calendar.MONTH, -1);
				int monthOld = calendar.get(Calendar.MONTH)+1;	
				String monthStringOld = null;
				if(monthOld<10){
					monthStringOld = "0"+monthOld;
				}else{
					monthStringOld = ""+monthOld;
				}
				map.put("wm_monthOld",monthStringOld);
				calendar.set(Calendar.DAY_OF_MONTH, 1);
		        Date date_startOld = calendar.getTime();
				map.put("wm_starttimeOld", format.format(date_startOld));
				calendar.add(Calendar.MONTH, 1);
				calendar.add(Calendar.DATE, -1);
				Date date_endOld = calendar.getTime();
				map.put("wm_endtimeOld", format.format(date_endOld));
				//获取工作计划
				Object wd_plan = baseDao.getFieldDataByCondition("WorkMonthly", "wm_plan", "wm_empcode='"+emcode+"' and wm_statuscode<>'ENTERING' and trunc(wm_endtime)+1=trunc(to_date('"+map.get("wm_starttime")+"','yyyy-mm-dd'))");
				if(wd_plan!=null){
					map.put("wm_plan", wd_plan);
				}
				Object wd_planOld = baseDao.getFieldDataByCondition("WorkMonthly", "wm_plan", "wm_empcode='"+emcode+"' and wm_statuscode<>'ENTERING' and trunc(wm_endtime)+1=trunc(to_date('"+map.get("wm_starttimeOld")+"','yyyy-mm-dd'))");
				if(wd_planOld!=null){
					map.put("wm_planOld", wd_planOld);
				}
			}
			return map;
		}catch (Exception ex) {
			return null;
		}
	}

	@Override
	public List<Map<String, Object>> getTodayData(String emcode, String caller) {
		try {
			String sql = null;
			if("WorkDaily".equals(caller)){
				sql = "select wd_id,wd_emp,to_char(wd_date,'yyyy-MM-dd') wd_date,wd_comment,wd_depart,wd_joname,wd_plan,wd_experience,wd_context,wd_status,wd_unfinishedtask,to_char(wd_entrydate,'yyyy-MM-dd') wd_entrydate from WORKDAILY where wd_empcode='"+emcode+"' and trunc(wd_date)=trunc(sysdate)";
			}else if("WorkWeekly".equals(caller)){		
				sql = "select ww_id,to_char(ww_date,'yyyy-MM-dd') ww_date,WW_COMMENT,WW_CONTEXT,ww_status,WW_PLAN,WW_EXPERIENCE,WW_DEPART,WW_JOCODE,WW_JONAME,WW_HRORG,WW_COMMENT,ww_unfinishedtask,ww_week,to_char(ww_starttime,'yyyy-MM-dd') ww_starttime,to_char(ww_endtime,'yyyy-MM-dd') ww_endtime from WORKWEEKLY where ww_empcode='"+emcode+"' and trunc(ww_starttime)=trunc(sysdate, 'd')+1";
			}else if("WorkMonthly".equals(caller)){
				sql = "select wm_id,to_char(wm_date,'yyyy-MM-dd') wm_date,wm_COMMENT,wm_CONTEXT,wm_status,wm_PLAN,wm_EXPERIENCE,wm_DEPART,wm_JOCODE,wm_JONAME,wm_HRORG,wm_COMMENT,wm_unfinishedtask,wm_month,to_char(wm_starttime,'yyyy-MM-dd') wm_starttime,to_char(wm_endtime,'yyyy-MM-dd') wm_endtime from WORKMONTHLY where wm_empcode='"+emcode+"' and trunc(wm_starttime)=trunc(sysdate, 'mm')";
			}
			if(sql!=null){				
				return baseDao.queryForList(sql);
			}
			return null;
		} catch (Exception ex) {
			return null;
		}
	}

	@Override
	public List<Map<String, Object>> getYesterdayData(String emcode,
			String caller) {
		try {
			String sql = null;
			if("WorkDaily".equals(caller)){
				sql = "select wd_id,wd_emp,to_char(wd_date,'yyyy-MM-dd') wd_date,wd_comment,wd_depart,wd_joname,wd_plan,wd_experience,wd_context,wd_status,wd_unfinishedtask,to_char(wd_entrydate,'yyyy-MM-dd') wd_entrydate from WORKDAILY where wd_empcode='"+emcode+"' and trunc(wd_date)=trunc(sysdate)-1";
			}else if("WorkWeekly".equals(caller)){		
				sql = "select ww_id,to_char(ww_date,'yyyy-MM-dd') ww_date,WW_COMMENT,WW_CONTEXT,ww_status,WW_PLAN,WW_EXPERIENCE,WW_DEPART,WW_JOCODE,WW_JONAME,WW_HRORG,WW_COMMENT,ww_unfinishedtask,ww_week,to_char(ww_starttime,'yyyy-MM-dd') ww_starttime,to_char(ww_endtime,'yyyy-MM-dd') ww_endtime from WORKWEEKLY where ww_empcode='"+emcode+"' and trunc(ww_starttime)=trunc(sysdate, 'd')-6";
			}else if("WorkMonthly".equals(caller)){
				sql = "select wm_id,to_char(wm_date,'yyyy-MM-dd') wm_date,wm_COMMENT,wm_CONTEXT,wm_status,wm_PLAN,wm_EXPERIENCE,wm_DEPART,wm_JOCODE,wm_JONAME,wm_HRORG,wm_COMMENT,wm_unfinishedtask,wm_month,to_char(wm_starttime,'yyyy-MM-dd') wm_starttime,to_char(wm_endtime,'yyyy-MM-dd') wm_endtime from WORKMONTHLY where wm_empcode='"+emcode+"' and trunc(wm_starttime)=trunc(add_months(sysdate,-1),'mm')";
			}
			if(sql!=null){				
				return baseDao.queryForList(sql);
			}
			return null;
		} catch (Exception ex) {
			return null;
		}
	}
	
	@Override
	public List<Map<String, Object>> getOuterEmps(String lastdate,String departcode) {
		return getEmps(lastdate,departcode);
	}

	@Override
	public List<Map<String, Object>> getOuterHrorg(String lastdate,
			Integer orid) {
		String condition = lastdate;
		if (!StringUtil.hasText(lastdate)) {
			condition = "1=1 and nvl(or_status,' ')<>'已禁用' ";
		} else {
			condition = "or_lastdate>to_date('"
					+ lastdate
					+ "','yyyy-MM-dd hh24:mi:ss')  and nvl(or_status,' ')<>'已禁用'";
		}
		//依据序号,ID进行排序
		condition =  (condition==null? "1=1" : condition) + "order by or_detno,or_id";
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		List<Object[]> objs = baseDao.getFieldsDatasByCondition("(select * from HRORG_MOBILE a start with a.or_id="+orid+" connect by prior a.OR_SUBOF=a.or_id)",
				new String[] { "or_id", "or_code", "or_name", "or_subof",
						"or_isleaf", "company", "whichsys", "flag",
						"or_lastdate", "or_headmancode", "or_headmanname",
						"or_remark" }, condition);
		for (Object[] obj : objs) {
			map = new HashMap<String, Object>();
			map.put("or_id", obj[0]);
			map.put("or_code", obj[1]);
			map.put("or_name", obj[2]);
			map.put("or_subof", obj[3]);
			map.put("or_isleaf", obj[4]);
			map.put("company", obj[5]);
			map.put("whichsys", obj[6]);
			map.put("flag", obj[7]);
			map.put("or_lastdate", obj[8]);
			map.put("or_headmancode", obj[9]);
			map.put("or_headmanname", obj[10]);
			map.put("or_remark", obj[11]);
			lists.add(map);
		}
		return lists;
	}
	
	private List<Map<String, Object>> getEmps(String lastdate,String departcode){
		String sql = "";
		String con=departcode==null?"":"and em_departmentcode='"+departcode+"'";
		if (!StringUtil.hasText(lastdate)) {
			sql = "select em_id,em_code,em_name,em_position,wm_concat(jo_name) as em_jobs,em_defaultorname,em_defaultorid,em_depart,em_tel,em_mobile,em_email,em_uu,em_imageid,company,whichsys,flag,em_lastdate,em_imid from employee_mobile left join job left join empsjobs on job_id=jo_id on em_id=emp_id  where nvl(em_class,' ')<>'离职' "+con+" group by em_id,em_code,em_name,em_position,em_defaultorname,em_defaultorid,em_depart,em_tel,em_mobile,em_email,em_uu,em_imageid,company,whichsys,flag,em_lastdate,em_imid";
		} else {
			sql = "select em_id,em_code,em_name,em_position,wm_concat(jo_name) as em_jobs,em_defaultorname,em_defaultorid,em_depart,em_tel,em_mobile,em_email,em_uu,em_imageid,company,whichsys,flag,em_lastdate,em_imid from employee_mobile left join job left join empsjobs on job_id=jo_id on em_id=emp_id  where nvl(em_class,' ')<>'离职' "+con+" and  em_lastdate>to_date('"
					+ lastdate
					+ "','yyyy-MM-dd hh24:mi:ss') group by em_id,em_code,em_name,em_position,em_defaultorname,em_defaultorid,em_depart,em_tel,em_mobile,em_email,em_uu,em_imageid,company,whichsys,flag,em_lastdate,em_imid";
		}
		return baseDao.getJdbcTemplate().queryForList(sql);
	}
	
	private List<Map<String,Object>> getHrorg(String lastdate,String departcode){
		String condition = lastdate;
		String con=departcode==null?"":"and or_departmentcode like '"+departcode+"%'";
		if (!StringUtil.hasText(lastdate)) {
			condition = "1=1 and nvl(or_status,' ')<>'已禁用' "+con;
		} else {
			condition = "or_lastdate>to_date('"
					+ lastdate
					+ "','yyyy-MM-dd hh24:mi:ss') "+con+" and nvl(or_status,' ')<>'已禁用'";
		}
		//依据序号,ID进行排序
		condition =  (condition==null? "1=1" : condition) + "order by or_detno,or_id";
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		List<Object[]> objs = baseDao.getFieldsDatasByCondition("hrorg_mobile",
				new String[] { "or_id", "or_code", "or_name", "or_subof",
						"or_isleaf", "company", "whichsys", "flag",
						"or_lastdate", "or_headmancode", "or_headmanname",
						"or_remark" }, condition);
		for (Object[] obj : objs) {
			map = new HashMap<String, Object>();
			map.put("or_id", obj[0]);
			map.put("or_code", obj[1]);
			map.put("or_name", obj[2]);
			map.put("or_subof", obj[3]);
			map.put("or_isleaf", obj[4]);
			map.put("company", obj[5]);
			map.put("whichsys", obj[6]);
			map.put("flag", obj[7]);
			map.put("or_lastdate", obj[8]);
			map.put("or_headmancode", obj[9]);
			map.put("or_headmanname", obj[10]);
			map.put("or_remark", obj[11]);
			lists.add(map);
		}
		return lists;
	}

	@Override
	public List<Map<String, Object>> getAllHrorg(String lastdate) {
		return getHrorg(lastdate, null);
	}

	@Override
	public List<Map<String, Object>> getAllEmps(String lastdate) {
		return getEmps(lastdate, null);
	}

}
