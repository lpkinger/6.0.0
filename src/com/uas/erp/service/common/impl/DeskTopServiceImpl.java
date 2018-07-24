package com.uas.erp.service.common.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.model.DeskTop;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.Employee;
import com.uas.erp.model.GridFields;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.DeskTopService;

@Service
public class DeskTopServiceImpl implements DeskTopService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private DetailGridDao detailGridDao;
	
	static final String GET_PROCESS_UNDO="SELECT * FROM (SELECT PROCESS_UNDO_VIEW.*,EM_IMID,ROW_NUMBER() OVER(ORDER BY JP_ID,JP_LAUNCHTIME) RN FROM PROCESS_UNDO_VIEW LEFT JOIN EMPLOYEE ON EM_CODE=JP_LAUNCHERID WHERE (JP_NODEDEALMAN=? AND JP_STATUS='待审批') OR (JP_LAUNCHERID=? AND JP_STATUS='未通过' )   ORDER BY JP_ID,JP_LAUNCHTIME) WHERE RN<=? ORDER BY CASE TYPECODE  WHEN 'process' THEN 1 WHEN 'transferprocess' THEN 2 WHEN 'procand' THEN 3 END";
	static final String GET_PROCESS_UNDO_GROUP=" SELECT * FROM (SELECT JPROCESSVIEW.*,EM_IMID, ROW_NUMBER() OVER(ORDER BY JP_ID,JP_LAUNCHTIME) RN FROM JPROCESSVIEW LEFT JOIN EMPLOYEE ON EM_CODE=JP_LAUNCHERID WHERE JP_NODEDEALMAN=? AND JP_STATUS='待审批' OR (JP_LAUNCHERID=? AND JP_STATUS='未通过' )   ORDER BY JP_ID,JP_LAUNCHTIME) WHERE RN<=?  ORDER BY CASE TYPECODE  WHEN 'process' THEN 1 WHEN 'transferprocess' THEN 2 WHEN 'procand' THEN 3 END";
	static final String GET_PROCESS_TOLUANCH="SELECT  * FROM (SELECT PAGELINK,CODE,TITLE,ROWNUM RN FROM table(GET_REMINDDATA(?))) WHERE RN<=?";
	static final String GET_PROCESS_ALREADYDO="SELECT * FROM (SELECT JN_NAME,JN_INFORECEIVER||JN_OPERATEDDESCRIPTION JN_OPERATEDDESCRIPTION,JN_NODEDESCRIPTION,JN_DEALMANID,JN_DEALMANNAME,JN_DEALTIME,JN_DEALRESULT,JP_CODEVALUE,JP_NODEID,JP_KEYVALUE,JP_NAME,JP_LAUNCHERID,ROW_NUMBER() OVER(ORDER BY JN_DEALTIME DESC) RN,JP_LAUNCHERNAME,EM_IMID  FROM JNODE LEFT JOIN JPROCESS ON JN_PROCESSINSTANCEID=JP_PROCESSINSTANCEID AND JN_NAME=JP_NODENAME LEFT JOIN EMPLOYEE ON EM_CODE=JPROCESS.JP_LAUNCHERID WHERE JP_CODEVALUE IS NOT NULL AND JN_DEALMANID=? ORDER BY JN_DEALTIME DESC)WHERE RN<=?";
	static final String GET_PROCESS_ALREADYLAUNCH="SELECT *  FROM (SELECT PROCESS_LAUNCH_VIEW.*,ROW_NUMBER() OVER(ORDER BY JP_LAUNCHTIME DESC) RN FROM PROCESS_LAUNCH_VIEW WHERE JP_LAUNCHERID=? ORDER BY JP_LAUNCHTIME DESC) WHERE  RN<=?";
	//static final String GET_PROCESS_ALREADYCOMMUNICATE="SELECT * FROM ()"
	static final String GET_NOTE_INFORM="SELECT * FROM (SELECT NO_ID,NO_TITLE,NO_APPROVER,NO_APPTIME,ROW_NUMBER() OVER(ORDER BY STATUS DESC,NO_APPTIME DESC) RN,STATUS FROM NOTE LEFT JOIN READSTATUS ON NO_ID=MAINID AND SOURCEKIND='note' AND MAN=? WHERE NO_INFOTYPE='TZ' AND (NO_APPROVER=? OR nvl(NO_ISPUBLIC,0)<>0 OR (NO_ISPUBLIC=0 AND (NO_ID IN (SELECT NO_ID FROM EMPSNOTES WHERE EMP_ID =?)))) ORDER BY STATUS DESC,NO_APPTIME DESC) WHERE RN>? AND RN<=?";
	static final String GET_NOTE_NOTICE="SELECT * FROM (SELECT NO_ID,NO_TITLE,NO_APPROVER,NO_APPTIME,ROW_NUMBER() OVER(ORDER BY STATUS DESC,NO_APPTIME DESC) RN,STATUS FROM NOTE LEFT JOIN READSTATUS ON NO_ID=MAINID AND SOURCEKIND='note' AND MAN=? WHERE NO_INFOTYPE='GG' AND (NO_APPROVER=? OR nvl(NO_ISPUBLIC,0)<>0 OR (NO_ISPUBLIC=0 AND (NO_ID IN (SELECT NO_ID FROM EMPSNOTES WHERE EMP_ID =?)))) ORDER BY STATUS DESC,NO_APPTIME DESC) WHERE RN>? AND RN<=?";
	static final String GET_NEWS="SELECT * FROM (SELECT NE_THEME,NE_RELEASER,NE_RELEASEDATE,NE_ID,ROW_NUMBER() OVER(ORDER BY STATUS DESC,NE_RELEASEDATE DESC) RN,STATUS FROM NEWS LEFT JOIN READSTATUS ON NE_ID=MAINID AND SOURCEKIND='new' AND MAN=? ORDER BY STATUS DESC,NE_RELEASEDATE DESC) WHERE RN>? AND RN<=? ";
	static final String GET_SUBS="SELECT * FROM  (SELECT ID_,NUM_ID_,INSTANCE_ID_,CREATEDATE_,TITLE_,SON_TITLE_,STATUS_,EMP_ID_,null SONTITLE_,SUMDATA_,ROW_NUMBER() OVER(ORDER BY CREATEDATE_ DESC) RN FROM (SELECT SUBS_MAN_INSTANCE.ID_,SUBS_MAN_INSTANCE.NUM_ID_,SUBS_MAN_INSTANCE.INSTANCE_ID_,SUBS_MAN_INSTANCE.CREATEDATE_,SUBS_MAN_INSTANCE.TITLE_,SUBS_MAN_INSTANCE.SON_TITLE_,SUBS_MAN_INSTANCE.STATUS_,SUBS_MAN_INSTANCE.EMP_ID_,SUBS_MAN_INSTANCE.ISPUSH_,WMSYS.wm_concat(SUBS_DATA.SONTITLE_||SUBS_DATA.DATA_) SUMDATA_ FROM subs_man_instance left join SUBS_DATA on SUBS_DATA.num_id=subs_man_instance.NUM_ID_ and  SUBS_DATA.INSTANCE_ID_=subs_man_instance.id_ and  SUBS_DATA.MAININSTANCE_ID_=subs_man_instance.INSTANCE_ID_ and type_='sum' WHERE EMP_ID_=? GROUP BY SUBS_MAN_INSTANCE.ID_,SUBS_MAN_INSTANCE.NUM_ID_,SUBS_MAN_INSTANCE.INSTANCE_ID_,SUBS_MAN_INSTANCE.CREATEDATE_,SUBS_MAN_INSTANCE.TITLE_,SUBS_MAN_INSTANCE.SON_TITLE_,SUBS_MAN_INSTANCE.STATUS_,SUBS_MAN_INSTANCE.EMP_ID_,SUBS_MAN_INSTANCE.ISPUSH_)";	
	static final String GET_CUSTBIRTH="SELECT * FROM (SELECT CU_ID,CU_CODE,CU_NAME,CT_ID,CT_NAME,BIRTHDAY,DAYS,ROW_NUMBER() OVER(ORDER BY DAYS ASC) RN FROM (select CUSTOMER.*,CONTACT.*,TO_DATE(CASE WHEN TO_CHAR(CT_BIRTHDAY,'mmdd')<TO_CHAR(SYSDATE,'mmdd') THEN TO_CHAR(ADD_MONTHS(SYSDATE, +12),'yyyy') ELSE TO_CHAR(SYSDATE,'yyyy')END||TO_CHAR(CT_BIRTHDAY,'mmdd'),'yyyymmdd') as BIRTHDAY,CEIL(TO_DATE(CASE WHEN TO_CHAR(CT_BIRTHDAY,'mmdd')<TO_CHAR(SYSDATE,'mmdd') THEN TO_CHAR(ADD_MONTHS(SYSDATE, +12),'yyyy') ELSE TO_CHAR(SYSDATE,'yyyy')END||TO_CHAR(CT_BIRTHDAY,'mmdd'),'yyyymmdd')-SYSDATE)as DAYS FROM CONTACT LEFT JOIN CUSTOMER LEFT JOIN CUSTOMERDISTR ON CU_ID=CD_CUID on CT_CUCODE=CU_CODE AND CT_BIRTHDAY IS NOT NULL WHERE CD_SELLERCODE=?) ";
	static final String GET_FEEDBACK="SELECT * FROM (SELECT FEEDBACK_VIEW.*,ROW_NUMBER() OVER(ORDER BY FB_DATE DESC) RN FROM FEEDBACK_VIEW  ";
	static final String GET_KPIBILL="SELECT * FROM (SELECT KPIBILL.*,KPIDESIGN.KD_STARTKIND,ROW_NUMBER() OVER(ORDER BY KB_ENDDATE DESC) RN FROM KPIBILL LEFT JOIN KPIDESIGN ON KB_KDCODE=KD_CODE where  KB_MANID=?  ";
	static final String GET_NEWSPAGENUMBER = "SELECT * FROM (SELECT NE_ID,ROW_NUMBER() OVER(ORDER BY NE_RELEASEDATE DESC) RN FROM NEWS ORDER BY NE_RELEASEDATE DESC) WHERE NE_ID = ?";
	
	//新流程视图
	static final String GET_FLOW_PENDING = "SELECT distinct FI_CODEVALUE,FI_HANDLERCODE,FI_ID,FI_FDSHORTNAME,FI_NODEID,FI_KEYVALUE,FI_HANDLER,FI_TIME,FI_NODENAME,FI_STARTTIME,FI_STARTMAN," + 
			"FI_STATUS,FI_STARTMANCODE,FI_KEYFIELD,FI_CALLER,FIR_MANCODE,FD_NAME,FI_TITLE FROM (SELECT FLOW_PENDING_VIEW.*,ROW_NUMBER() OVER(ORDER BY FI_TIME DESC) RN FROM FLOW_PENDING_VIEW WHERE (FIR_MANCODE=? AND FI_STATUS='using')) WHERE RN<=? ORDER BY FI_TIME DESC";
	static final String GET_FLOW_PROCESSED = "SELECT * FROM (SELECT FLOW_PROCESSED_VIEW.*,ROW_NUMBER() OVER(ORDER BY FI_TIME DESC) RN FROM FLOW_PROCESSED_VIEW WHERE FIR_MANCODE=?) WHERE RN<=? ORDER BY FI_TIME DESC";
	static final String GET_FLOW_CREATED = "SELECT * FROM (SELECT FLOW_CREATED_VIEW.*,ROW_NUMBER() OVER(ORDER BY FI_TIME DESC) RN FROM FLOW_CREATED_VIEW WHERE (FI_STARTMANCODE=?)) WHERE RN<=? ORDER BY FI_TIME DESC";
	static final String GET_FLOW_DATACENTER = "SELECT distinct FI_CODEVALUE,FI_HANDLERCODE,MASTERNAME,FI_ID,FI_FDSHORTNAME,FI_NODEID,FI_KEYVALUE,FI_HANDLER,FI_TIME,FI_NODENAME,FI_STARTTIME,FI_STARTMAN," + 
			"FI_STATUS,FI_STARTMANCODE,FI_KEYFIELD,FI_CALLER,FIR_MANCODE,FD_NAME,FI_TITLE FROM (SELECT FLOW_DATACENTER_VIEW.*,ROW_NUMBER() OVER(ORDER BY FI_TIME DESC) RN FROM FLOW_DATACENTER_VIEW WHERE (FIR_MANCODE=? AND FI_STATUS='using')) WHERE RN<=? ORDER BY FI_TIME DESC";
	
	@Override
	@Cacheable(value = "bench", key = "#employee.em_master + '@' + #employee.em_id + 'getWorkBench'",unless="#result==null")
	public List<DeskTop> getOwner(Employee employee) {
		List<DeskTop> deskList=null;
		try {
			deskList= baseDao.getJdbcTemplate().query(
					"select xtype_,detno_,count_ from DeskTop where emid_ = ? order by detno_ asc",
					new BeanPropertyRowMapper<DeskTop>(DeskTop.class), employee.getEm_id());
		} catch (EmptyResultDataAccessException e) {
			deskList=setDefault(employee);
		} catch (Exception e) {
			return null;
		}
		if(deskList.size()<1) deskList=setDefault(employee);
		return deskList;
	}
	
	private List<DeskTop> setDefault(Employee employee){
    	String []defualts={"flowportal","commonuseportal","taskportal","infoportal","subsportal"};
    	int [] count={10,15,10,10,10};
    	List<DeskTop> deskList=new ArrayList<DeskTop>();
    	DeskTop desk=null;
    	for(int i=0;i<defualts.length;i++){
    		desk=new DeskTop(defualts[i],i+1,count[i],employee.getEm_id());
    		deskList.add(desk);    		
    	}
    	
    	try {
    		String sql = "select  xtype_||'#'||portid_ xtype_,nvl(detno_,1)+5,count_ from WorkBenchSet where nvl(remove_,1) = 0 order by detno_";
    		deskList.addAll(baseDao.getJdbcTemplate().query(sql,new BeanPropertyRowMapper<DeskTop>(DeskTop.class)));
		} catch (Exception e) {
			// TODO: handle exception
		}
    	
    	baseDao.save(deskList);
        return deskList;
    }
	
	@Override
	public Map<String, Object> getData(String caller, String condition, String orderby, Integer count) {
		Employee employee=SystemSession.getUser();
		Map<String,Object> map=new HashMap<String,Object>();
		List<DetailGrid> details=detailGridDao.getDetailGridsByCaller(caller,employee.getEm_master());
		int c_=0;String data="[]";
		if(details.size()>0){
		    c_=baseDao.getCountByCondition(details.get(0).getDg_table(), condition);
			if(c_>0){
				data=baseDao.getGridDataBySql(details,SqlUtil.getQuerySqlByDetailGrid(details, null, condition+" "+orderby, employee, 1, count));
			}
		}
		map.put("count", c_);
		map.put("data", data);
		return map;
	}
	@Override
	@CacheEvict(value = "bench", allEntries = true)
	public String setTotalCount(int count, String type) {
		// TODO Auto-generated method stub
		Employee Employee=SystemSession.getUser();
		baseDao.updateByCondition("DeskTop", "count_="+count, "emid_="+Employee.getEm_id()+" AND xtype_='"+type+"'");
		return "success";
	}
	@Override
	public Map<String, Object> getProcess_UnDo(int count) {
		// TODO Auto-generated method stub
		Employee em=SystemSession.getUser();
		Map<String,Object> map=new HashMap<String,Object>();
		Master master=em.getCurrentMaster();
		if(master!=null &&  master.getMa_soncode()!=null){
			//需要汇集数据
		}else {
		   map.put("data", baseDao.queryForList(GET_PROCESS_UNDO, new Object[]{em.getEm_code(),em.getEm_code(),count}));
		   map.put("totalCount", baseDao.getCountByCondition("PROCESS_UNDO_VIEW", "(JP_NODEDEALMAN='"+em.getEm_code()+"' AND JP_STATUS='待审批') OR (JP_LAUNCHERID='"+em.getEm_code()+"' AND JP_STATUS='未通过' ) "));			
		}		
		return map;
	}
	@Override
	public Map<String, Object> getDeskProcess(int count, String type, Model model,Integer isMobile,Employee em) {
		// TODO Auto-generated method stub
		Map<String,Object> map=new HashMap<String,Object>();
		Master master=em.getCurrentMaster();
		if("toDo".equals(type)){
			//待办
			if(master!=null &&  master.getMa_soncode()!=null && !master.getMa_soncode().equals(master.getMa_user())&&isMobile==null){
				//需要汇集数据
			map.put("data", baseDao.queryForList(GET_PROCESS_UNDO_GROUP,new Object[]{em.getEm_code(),em.getEm_code(),count}));
			}else {
			   map.put("data", baseDao.queryForList(GET_PROCESS_UNDO, new Object[]{em.getEm_code(),em.getEm_code(),count}));
			   map.put("totalCount", baseDao.getCountByCondition("PROCESS_UNDO_VIEW", "(JP_NODEDEALMAN='"+em.getEm_code()+"' AND JP_STATUS='待审批') OR (JP_LAUNCHERID='"+em.getEm_code()+"' AND JP_STATUS='未通过' ) "));			
			}	
		}else if("toLaunch".equals(type)){
			map.put("data", baseDao.queryForList(GET_PROCESS_TOLUANCH,new Object[]{em.getEm_code(),count}));
		}else if("alreadyDo".equals(type)){
			map.put("data", baseDao.queryForList(GET_PROCESS_ALREADYDO,new Object[]{em.getEm_code(),count}));
		}else if("alreadyLaunch".equals(type)){
			map.put("data",baseDao.queryForList(GET_PROCESS_ALREADYLAUNCH,new Object[]{em.getEm_code(),count}));
		}else {
			//已沟通
			
		}
		return map;
	}
	@Override
	@CacheEvict(value = "bench", allEntries = true)
	public String setDetno(String nodes) {
		// TODO Auto-generated method stub
		List<Map<Object, Object>> lists=BaseUtil.parseGridStoreToMaps(nodes);
		Employee em=SystemSession.getUser();
		List<String>sqls=new ArrayList<String>();
		for(Map<Object,Object> map:lists){
			sqls.add("update desktop set detno_="+map.get("detno_")+" where emid_="+em.getEm_id()+" and xtype_='"+map.get("xtype_")+"'");
		}
		baseDao.execute(sqls);
		return "success";
	}
	@Override
	public Map<String, Object> getNote_Inform(int start,int end) {
		Employee em=SystemSession.getUser();
		Map<String,Object> map=new HashMap<String,Object>();
		Master master=em.getCurrentMaster();
		if(master!=null &&  master.getMa_soncode()!=null){
		   map.put("data", baseDao.queryForList(GET_NOTE_INFORM, new Object[]{em.getEm_id(),em.getEm_name(),em.getEm_id(),start,end}));
		}else {
		   map.put("data", baseDao.queryForList(GET_NOTE_INFORM, new Object[]{em.getEm_id(),em.getEm_name(),em.getEm_id(),start,end}));		   
		}		
		return map;
	}
	@Override
	public Map<String, Object> getNote_Notice(int start,int end) {
		Employee em=SystemSession.getUser();
		Map<String,Object> map=new HashMap<String,Object>();
		Master master=em.getCurrentMaster();
		if(master!=null &&  master.getMa_soncode()!=null){
		   map.put("data", baseDao.queryForList(GET_NOTE_NOTICE, new Object[]{em.getEm_id(),em.getEm_name(),em.getEm_id(),start,end}));
		}else {
		   map.put("data", baseDao.queryForList(GET_NOTE_NOTICE, new Object[]{em.getEm_id(),em.getEm_name(),em.getEm_id(),start,end}));		  
		}		
		return map;
	}
	@Override
	public Map<String, Object> getNews(int start,int end) {
		Employee em=SystemSession.getUser();
		Map<String,Object> map=new HashMap<String,Object>();
		Master master=em.getCurrentMaster();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if(master!=null &&  master.getMa_soncode()!=null){
		   list = baseDao.queryForList(GET_NEWS, new Object[]{em.getEm_id(),start,end});
		   for(int i = 0 ; i < list.size(); i++){
			   SqlRowList rs = baseDao.queryForRowSet(GET_NEWSPAGENUMBER,list.get(i).get("NE_ID"));
			   if(rs.next()){
				   int page = (int) Math.ceil(rs.getDouble("RN")/10);
				   list.get(i).put("PAGE", page);
			   }
		   }
		   map.put("data", list);
		}else {
		   list = baseDao.queryForList(GET_NEWS, new Object[]{em.getEm_id(),start,end});
		   for(int i = 0 ; i < list.size(); i++){
			   SqlRowList rs = baseDao.queryForRowSet(GET_NEWSPAGENUMBER,list.get(i).get("NE_ID"));
			   if(rs.next()){
				   int page = (int) Math.ceil(rs.getDouble("RN")/10);
				   list.get(i).put("PAGE", page);
			   }
		   }
		   map.put("data", list);		  
		}		
		return map;
	}
	
	@Override
	public Map<String, Object> getSubs(int count,String condition) {
		Employee em=SystemSession.getUser();
		Map<String,Object> map=new HashMap<String,Object>();
		Master master=em.getCurrentMaster();
		if(master!=null &&  master.getMa_soncode()!=null){
			   map.put("data", baseDao.queryForList(GET_SUBS+condition+") WHERE RN<=?" , new Object[]{em.getEm_id(),count}));
			}else {
			   map.put("data", baseDao.queryForList(GET_SUBS+condition+") WHERE RN<=?", new Object[]{em.getEm_id(),count}));		  
			}		
		return map;
	}
	
	@Override
	public Map<String, Object> getCustBirth(int count,String condition) {
		Employee em=SystemSession.getUser();
		Map<String,Object> map=new HashMap<String,Object>();
		Master master=em.getCurrentMaster();
		if(master!=null &&  master.getMa_soncode()!=null){
		   map.put("data", baseDao.queryForList(GET_CUSTBIRTH+condition+") WHERE RN<=?" , new Object[]{em.getEm_code(),count}));
		}else {
		   map.put("data", baseDao.queryForList(GET_CUSTBIRTH+condition+") WHERE RN<=?", new Object[]{em.getEm_code(),count}));		  
		}		
		return map;
	}
	@Override
	@CacheEvict(value = "bench", allEntries = true)
	public void setDeskTop(String param) {
		Employee em=SystemSession.getUser();
		List<Map<Object, Object>> map = BaseUtil.parseGridStoreToMaps(param);
		for(Map<Object, Object> m:map){
			m.put("emid_", em.getEm_id());
		}
		List<String> addsqls = SqlUtil.getInsertSqlbyGridStore(map, "desktop");
		baseDao.execute("delete desktop where emid_="+em.getEm_id());
		baseDao.execute(addsqls);
	}
	
	@Override
	public Map<String, Object> getFeedback(int count, String condition) {
		Employee em=SystemSession.getUser();
		Map<String,Object> map=new HashMap<String,Object>();
		Master master=em.getCurrentMaster();
		if(master!=null &&  master.getMa_soncode()!=null){
		   map.put("data", baseDao.queryForList(GET_FEEDBACK+condition+") WHERE RN<=?" , new Object[]{em.getEm_name(),count}));
		}else {
		   map.put("data", baseDao.queryForList(GET_FEEDBACK+condition+") WHERE RN<=?", new Object[]{em.getEm_name(),count}));		  
		}		
		return map;
	}
	
	@Override
	public Map<String, Object> getKpibill(int count, String condition) {
		Employee em=SystemSession.getUser();
		Map<String,Object> map=new HashMap<String,Object>();
		Master master=em.getCurrentMaster();
		if(master!=null &&  master.getMa_soncode()!=null){
		   map.put("data", baseDao.queryForList(GET_KPIBILL+condition+") WHERE RN<=?" , new Object[]{em.getEm_id(),count}));
		}else {
		   map.put("data", baseDao.queryForList(GET_KPIBILL+condition+") WHERE RN<=?", new Object[]{em.getEm_id(),count}));		  
		}		
		return map;
	}

	@Override
	public Map<String, Object> getBench(Employee employee, String portid) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		String master = employee != null ? employee.getEm_master() : SpObserver.getSp();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		Object [] bench = baseDao.getFieldsDataByCondition("WorkBenchSet", new String[]{"text_","activetab_","condition_"}, "portid_ = '"+portid+"'");
		if (bench!=null) {
			result.put("title", bench[0]);
			result.put("activeTab", bench[1]);
			result.put("condition", bench[2]);
		}
		SqlRowList rs = baseDao.queryForRowSet("select caller_,desc_,condition_ from WorkBenchSetDetail where mainportid_  = ? "
				+ "order by detno_", portid);
		
		while (rs.next()) {
			Map<String, Object> data = new HashMap<String, Object>();
			String caller = rs.getGeneralString("caller_");
			List<DetailGrid> detailGrids = detailGridDao.getDetailGridsByCaller(caller, master);
			
			if (detailGrids != null && detailGrids.size() > 0) {
				List<GridFields> fields = new ArrayList<GridFields>();
				List<Map<String, Object>> columns = new ArrayList<Map<String, Object>>();// grid的列信息columns
				
				for (DetailGrid grid : detailGrids) {
					fields.add(new GridFields(grid));
					columns.add(getGridColumns(grid));
				}
				
				data.put("fields", fields);
				data.put("columns", columns);
				
			}
			data.put("title", rs.getGeneralString("desc_"));
			data.put("condition", rs.getGeneralString("condition_"));
			data.put("caller", caller);
			datas.add(data);
		}
		result.put("datas", datas);
		return result;
	}
	
	private Map<String, Object> getGridColumns(DetailGrid grid){
		Map<String, Object> column = new HashMap<String, Object>();
		float width = grid.getDg_width();
		if(width>0&&width<10){
			column.put("flex", width);
		}else{
			column.put("width", width);
		}
		
		column.put("renderer", grid.getDg_renderer());
		column.put("cls", "x-grid-header-simple");
		String dataIndex = grid.getDg_field();
		String header = null;
		String language = SystemSession.getLang();
		if (dataIndex.contains(" ")) {// column有取别名
			String[] strs = dataIndex.split(" ");
			dataIndex = strs[strs.length - 1];
		}
		column.put("dataIndex", dataIndex);
		if (language.equals("en_US")) {
			header = grid.getDg_captionen();
		} else if (language.equals("zh_TW")) {
			header = grid.getDg_captionfan();
		} else {
			header = grid.getDg_caption();
		}
		column.put("header", header);
		column.put("text", header);
		String type = grid.getDg_type();
		if (type.equals("numbercolumn")) {
			column.put("align" , "right");
			column.put("format", "0,000");
			column.put("xtype","numbercolumn");
		} else if (type.equals("floatcolumn")) {
			column.put("align" , "right");
			column.put("xtype","numbercolumn");
			column.put("format", "0,000.00");
		} else if (type.matches("^floatcolumn\\d{1}$")) {
			column.put("align" , "right");
			column.put("xtype","numbercolumn");
			String format = "0,000.";
			int length = Integer.parseInt(type.replace("floatcolumn", ""));
			for (int i = 0; i < length; i++) {
				format += "0";
			}
			column.put("format", format);
		} else if (type.equals("texttrigger")) {
			column.put("xtype","textareatrigger");
		} else if (type.contains("datecolumn")) {
			column.put("xtype","datecolumn");
			column.put("format",  "Y-m-d");
		} else if (type.contains("datetimecolumn")) {
			column.put("xtype","datecolumn");
			column.put("format", "Y-m-d H:i:s");
		} else if (type.contains("treecolumn")) {
			column.put("xtype","treecolumn");
		} else if (type.equals("yncolumn")) {
			column.put("xtype","yncolumn");
			column.put("readOnly", true);
		} else if (type.equals("ynnvcolumn")) {
			column.put("xtype","ynnvcolumn");
		} else if (type.equals("combo")) {
			column.put("xtype","combocolumn");
		}
		if (width == 0) {
			column.put("hidden", true);
		}
		return column;
	}
	
	@Override
	public List<Map<String, Object>> getData(String caller, String condition, int pageSize) {
		Employee employee = SystemSession.getUser();
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		List<DetailGrid> detailGrids=detailGridDao.getDetailGridsByCaller(caller,employee.getEm_master());
		if (detailGrids != null && detailGrids.size() > 0) {
			data = baseDao.getDetailGridData(detailGrids, condition, employee, 0, pageSize);
		}
		return data;
	}

	@Override
	@Cacheable(value = "bench", key = "#employee.em_master + '@' + 'getBenchSet'", unless="#result==null")
	public List<Map<String, Object>> getBenchSet(Employee employee) {
		List<Map<String, Object>> deskList= new ArrayList<Map<String,Object>>();
		try {
			SqlRowList rs = baseDao.queryForRowSet("select  xtype_||'#'||portid_ xtype_,remove_,count_,text_ from WorkBenchSet order by detno_");
	    	while (rs.next()) {
	    		Map<String, Object> desk = new HashMap<String, Object>();
	    		desk.put("remove", rs.getGeneralInt("remove_")==1);
	    		desk.put("count", rs.getGeneralString("count_"));
	    		desk.put("id", rs.getGeneralString("xtype_"));
	    		desk.put("text", rs.getGeneralString("text_"));
	    		deskList.add(desk);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	
		return deskList;
	}
	
	@Override
	public List<Map<String, Object>> getReports(Employee employee,String code) {
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		SqlRowList rs = new SqlRowList();
		boolean isModelQuery = false;//模块查询排序 根据模块名优先显示
		Object obj = null;
		if(!code.equals("")){
			obj = baseDao.getFieldDataByCondition("bench", "bc_desc", "bc_code = '"+code+"'");
			if(String.valueOf(obj)!="null"){
				isModelQuery = true;
			}
		}
		if(isModelQuery){
			if(employee.getEm_type().toLowerCase().equals("admin")){
				rs = baseDao.queryForRowSet("select distinct v1.*,commonreports.cr_using as using from (select b.* from (SELECT DISTINCT s.sn_id as id,s.SN_DISPLAYNAME as name,s.sn_url as url,s.sn_caller,ps.SN_DISPLAYNAME as type FROM sysnavigation s left join sysnavigation ps on s.sn_parentid = ps.sn_id "+
						" WHERE s.sn_isleaf ='T' AND s.sn_addurl is null AND s.sn_using = '1' AND s.sn_url like '%print.jsp%'  AND ps.SN_DISPLAYNAME<>' ' ) b) v1 "+
						" left join  "+
						" (SELECT * FROM sysnavigation a START WITH sn_displayname = '"+obj+"' CONNECT BY sn_parentid = PRIOR sn_ID) v2 on  "+
						" v1.sn_caller = v2.sn_caller "+
						" left join commonreports on cr_snid = v1.id and cr_emcode = '"+employee.getEm_code()+"' "+
						" where v2.sn_caller is not null and v2.sn_using = '1' "+
						" order by type desc");
			}else{
				rs = baseDao.queryForRowSet("select distinct v1.*,commonreports.cr_using as using from (select b.* from (SELECT DISTINCT s.sn_id as id,s.SN_DISPLAYNAME as name,s.sn_url as url,s.sn_caller,ps.SN_DISPLAYNAME as type FROM sysnavigation s  "+
						" left join sysnavigation ps on s.sn_parentid = ps.sn_id   "+
						" WHERE s.sn_isleaf ='T' AND s.sn_using=1 AND s.sn_addurl is null AND s.sn_url like '%print.jsp%'  AND ps.SN_DISPLAYNAME<>' ' AND((s.sn_caller IN (SELECT DISTINCT pp_caller FROM positionpower WHERE (pp_alllist=1   "+
						" OR pp_selflist =1 OR pp_see =1) and pp_joid IN (select em_defaulthsid jobid from employee where em_id='"+employee.getEm_id()+"' union all select job_id jobid from empsjobs where emp_id='"+employee.getEm_id()+"'))) or s.sn_caller IN   "+
						" (SELECT DISTINCT pp_caller FROM personalpower WHERE (pp_alllist=1 OR pp_selflist =1  OR pp_see =1) AND pp_emid ='"+employee.getEm_id()+"' ))) b) v1  "+
						" left join   "+
						" (SELECT * FROM sysnavigation a START WITH sn_displayname = '"+obj+"' CONNECT BY sn_parentid = PRIOR sn_ID) v2 on  "+
						" v1.sn_caller = v2.sn_caller "+
						" left join commonreports on cr_snid = v1.id and cr_emcode = '"+employee.getEm_code()+"' "+
						" where v2.sn_caller is not null and v2.sn_using = '1' order by type desc");
			}		
		}else{
			if(employee.getEm_type().toLowerCase().equals("admin")){
				rs = baseDao.queryForRowSet("SELECT x.*,cr_using as using FROM (SELECT DISTINCT s.sn_id as id,s.SN_DISPLAYNAME as name,s.sn_url as url,s.sn_caller,ps.SN_DISPLAYNAME as type "+
						" FROM sysnavigation s left join sysnavigation ps on s.sn_parentid = ps.sn_id WHERE s.sn_isleaf ='T' AND s.sn_using=1 "+ 
						" AND s.sn_url like '%print.jsp%' AND ps.SN_DISPLAYNAME<>' ' "+
						" UNION "+
						" SELECT DISTINCT s.sn_id as id,s.SN_DISPLAYNAME as name,s.sn_url as url,s.sn_caller,ps.SN_DISPLAYNAME as type "+
						" FROM sysnavigation s left join sysnavigation ps on s.sn_parentid = ps.sn_id "+
						" WHERE s.sn_isleaf ='T' AND s.sn_using=1 AND s.sn_addurl is null AND ps.SN_DISPLAYNAME<>' ' AND s.sn_caller in (SELECT fo_caller FROM FORM where fo_pagetype in ('report','print'))) x "+
						" left join commonreports on cr_snid = x.id and cr_emcode = '"+employee.getEm_code()+"' "+
						" order by type desc ");
			}else{
				rs = baseDao.queryForRowSet("SELECT x.*,cr_using as using FROM (SELECT DISTINCT s.sn_id as id,s.SN_DISPLAYNAME as name,s.sn_url as url,s.sn_caller,ps.SN_DISPLAYNAME as type "+
						" FROM sysnavigation s left join sysnavigation ps on s.sn_parentid = ps.sn_id WHERE s.sn_isleaf ='T' AND s.sn_using=1 AND s.sn_addurl is null "+
						" AND s.sn_url like '%print.jsp%'  AND ps.SN_DISPLAYNAME<>' ' AND ps.SN_DISPLAYNAME<>' ' AND((s.sn_caller IN (SELECT DISTINCT pp_caller FROM positionpower WHERE (pp_alllist=1 "+
						" OR pp_selflist =1 OR pp_see =1) and pp_joid IN (select em_defaulthsid jobid from employee where em_id='"+employee.getEm_id()+"' union all select job_id jobid from empsjobs where emp_id='"+employee.getEm_id()+"'))) or s.sn_caller IN "+
						" (SELECT DISTINCT pp_caller FROM personalpower WHERE (pp_alllist=1 OR pp_selflist =1  OR pp_see =1) AND pp_emid ='"+employee.getEm_id()+"')) "+
						" UNION "+
						" SELECT DISTINCT s.sn_id as id,s.SN_DISPLAYNAME as name,s.sn_url as url,s.sn_caller,ps.SN_DISPLAYNAME as type "+
						" FROM sysnavigation s left join sysnavigation ps on s.sn_parentid = ps.sn_id "+
						" WHERE s.sn_isleaf ='T' AND s.sn_using=1 AND s.sn_addurl is null AND ps.SN_DISPLAYNAME<>' ' AND s.sn_caller in (SELECT fo_caller FROM FORM where fo_pagetype in ('report','print')) "+
						" AND((s.sn_caller IN (SELECT DISTINCT pp_caller FROM positionpower WHERE (pp_alllist=1 "+
						" OR pp_selflist =1 OR pp_see =1) and pp_joid IN (select em_defaulthsid jobid from employee where em_id='"+employee.getEm_id()+"' union all select job_id jobid from empsjobs where emp_id='"+employee.getEm_id()+"'))) or s.sn_caller IN "+
						" (SELECT DISTINCT pp_caller FROM personalpower WHERE (pp_alllist=1 OR pp_selflist =1  OR pp_see =1) AND pp_emid ='"+employee.getEm_id()+"'))) x "+
						" left join commonreports on cr_snid = x.id and cr_emcode = '"+employee.getEm_code()+"' "+
						" order by type desc");
			}		
		}
		if (rs.next()) {
			datas = rs.getResultList();
		}
		return datas;
	}
	
	@Override
	public List<Map<String, Object>> getQuerys(Employee employee,String code) {
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		SqlRowList rs = new SqlRowList();
		boolean isModelQuery = false;//模块查询排序 根据模块名优先显示
		Object obj = null;
		if(!code.equals("")){
			obj = baseDao.getFieldDataByCondition("bench", "bc_desc", "bc_code = '"+code+"'");
			if(String.valueOf(obj)!="null"){
				isModelQuery = true;
			}
		}
		if(isModelQuery){
			if(employee.getEm_type().toLowerCase().equals("admin")){
				rs = baseDao.queryForRowSet("select distinct v1.*,commonreports.cr_using as using from (select b.* from (SELECT DISTINCT s.sn_id as id,s.SN_DISPLAYNAME as name,s.sn_url as url,s.sn_caller,ps.SN_DISPLAYNAME as type FROM sysnavigation s left join sysnavigation ps on s.sn_parentid = ps.sn_id "+
						" WHERE s.sn_isleaf ='T' AND s.sn_addurl is null AND s.sn_using = '1' AND (s.sn_url like '%query%' or s.sn_url like '%search%')  AND ps.SN_DISPLAYNAME<>' ' ) b) v1 "+
						" left join  "+
						" (SELECT * FROM sysnavigation a START WITH sn_displayname = '"+obj+"' CONNECT BY sn_parentid = PRIOR sn_ID) v2 on  "+
						" v1.sn_caller = v2.sn_caller "+
						" left join commonreports on cr_snid = v1.id and cr_emcode = '"+employee.getEm_code()+"' "+
						" where v2.sn_caller is not null and v2.sn_using = '1' "+
						" order by type desc");
			}else{
				rs = baseDao.queryForRowSet("select distinct v1.*,commonreports.cr_using as using from (select b.* from (SELECT DISTINCT s.sn_id as id,s.SN_DISPLAYNAME as name,s.sn_url as url,s.sn_caller,ps.SN_DISPLAYNAME as type FROM sysnavigation s  "+
						" left join sysnavigation ps on s.sn_parentid = ps.sn_id   "+
						" WHERE s.sn_isleaf ='T' AND s.sn_using=1 AND s.sn_addurl is null AND (s.sn_url like '%query%' or s.sn_url like '%search%')  AND ps.SN_DISPLAYNAME<>' ' AND((s.sn_caller IN (SELECT DISTINCT pp_caller FROM positionpower WHERE (pp_alllist=1   "+
						" OR pp_selflist =1 OR pp_see =1) and pp_joid IN (select em_defaulthsid jobid from employee where em_id='"+employee.getEm_id()+"' union all select job_id jobid from empsjobs where emp_id='"+employee.getEm_id()+"'))) or s.sn_caller IN   "+
						" (SELECT DISTINCT pp_caller FROM personalpower WHERE (pp_alllist=1 OR pp_selflist =1  OR pp_see =1) AND pp_emid ='"+employee.getEm_id()+"' ))) b) v1  "+
						" left join   "+
						" (SELECT * FROM sysnavigation a START WITH sn_displayname = '"+obj+"' CONNECT BY sn_parentid = PRIOR sn_ID) v2 on  "+
						" v1.sn_caller = v2.sn_caller "+
						" left join commonreports on cr_snid = v1.id and cr_emcode = '"+employee.getEm_code()+"' "+
						" where v2.sn_caller is not null and v2.sn_using = '1' order by type desc");
			}	
		}else{
			if(employee.getEm_type().toLowerCase().equals("admin")){
				rs = baseDao.queryForRowSet("SELECT x.*,cr_using as using FROM (SELECT DISTINCT s.sn_id as id,s.SN_DISPLAYNAME as name,s.sn_url as url,s.sn_caller,ps.SN_DISPLAYNAME as type "+
						" FROM sysnavigation s left join sysnavigation ps on s.sn_parentid = ps.sn_id WHERE s.sn_isleaf ='T' AND s.sn_using=1 "+ 
						" AND (s.sn_url like '%query%' or s.sn_url like '%search%') AND ps.SN_DISPLAYNAME<>' ' "+
						" UNION "+
						" SELECT DISTINCT s.sn_id as id,s.SN_DISPLAYNAME as name,s.sn_url as url,s.sn_caller,ps.SN_DISPLAYNAME as type "+
						" FROM sysnavigation s left join sysnavigation ps on s.sn_parentid = ps.sn_id "+
						" WHERE s.sn_isleaf ='T' AND s.sn_using=1 AND s.sn_addurl is null AND ps.SN_DISPLAYNAME<>' ' AND s.sn_caller in (SELECT fo_caller FROM FORM where fo_pagetype in ('query'))) x "+
						" left join commonreports on cr_snid = x.id and cr_emcode = '"+employee.getEm_code()+"' "+
						" order by type desc ");
			}else{
				rs = baseDao.queryForRowSet("SELECT x.*,cr_using as using FROM (SELECT DISTINCT s.sn_id as id,s.SN_DISPLAYNAME as name,s.sn_url as url,s.sn_caller,ps.SN_DISPLAYNAME as type "+
						" FROM sysnavigation s left join sysnavigation ps on s.sn_parentid = ps.sn_id WHERE s.sn_isleaf ='T' AND s.sn_using=1 AND s.sn_addurl is null "+
						" AND (s.sn_url like '%query%' or s.sn_url like '%search%')  AND ps.SN_DISPLAYNAME<>' ' AND ps.SN_DISPLAYNAME<>' ' AND((s.sn_caller IN (SELECT DISTINCT pp_caller FROM positionpower WHERE (pp_alllist=1 "+
						" OR pp_selflist =1 OR pp_see =1) and pp_joid IN (select em_defaulthsid jobid from employee where em_id='"+employee.getEm_id()+"' union all select job_id jobid from empsjobs where emp_id='"+employee.getEm_id()+"'))) or s.sn_caller IN "+
						" (SELECT DISTINCT pp_caller FROM personalpower WHERE (pp_alllist=1 OR pp_selflist =1  OR pp_see =1) AND pp_emid ='"+employee.getEm_id()+"')) "+
						" UNION "+
						" SELECT DISTINCT s.sn_id as id,s.SN_DISPLAYNAME as name,s.sn_url as url,s.sn_caller,ps.SN_DISPLAYNAME as type "+
						" FROM sysnavigation s left join sysnavigation ps on s.sn_parentid = ps.sn_id "+
						" WHERE s.sn_isleaf ='T' AND s.sn_using=1 AND s.sn_addurl is null AND ps.SN_DISPLAYNAME<>' ' AND s.sn_caller in (SELECT fo_caller FROM FORM where fo_pagetype in ('query')) "+
						" AND((s.sn_caller IN (SELECT DISTINCT pp_caller FROM positionpower WHERE (pp_alllist=1 "+
						" OR pp_selflist =1 OR pp_see =1) and pp_joid IN (select em_defaulthsid jobid from employee where em_id='"+employee.getEm_id()+"' union all select job_id jobid from empsjobs where emp_id='"+employee.getEm_id()+"'))) or s.sn_caller IN "+
						" (SELECT DISTINCT pp_caller FROM personalpower WHERE (pp_alllist=1 OR pp_selflist =1  OR pp_see =1) AND pp_emid ='"+employee.getEm_id()+"'))) x "+
						" left join commonreports on cr_snid = x.id and cr_emcode = '"+employee.getEm_code()+"' "+
						" order by type desc");
			}	
		}
		if (rs.next()) {
			datas = rs.getResultList();
		}
		return datas;
	}
	
	@Override
	public Map<String, Object> getDeskFlow(int count, String type, Model model, Integer isMobile, Employee em) {
		Map<String,Object> map=new HashMap<String,Object>();
		Master master=em.getCurrentMaster();
		if("pending".equals(type)){
			//待办
			if(master!=null &&  master.getMa_soncode()!=null && !master.getMa_soncode().equals(master.getMa_user())&&isMobile==null){
				//需要汇集数据
				map.put("data", baseDao.queryForList(GET_FLOW_DATACENTER,new Object[]{em.getEm_code(),count}));
			}else {
			   map.put("data", baseDao.queryForList(GET_FLOW_PENDING, new Object[]{em.getEm_code(),count}));
			   map.put("totalCount", baseDao.getCountByCondition("FLOW_PENDING_VIEW", "(FIR_MANCODE='"+em.getEm_code()+"' AND FI_status='end')"));			
			}	
		}else if("processed".equals(type)){
			map.put("data", baseDao.queryForList(GET_FLOW_PROCESSED,new Object[]{em.getEm_code(),count}));
		}else if("created".equals(type)){
			map.put("data", baseDao.queryForList(GET_FLOW_CREATED,new Object[]{em.getEm_code(),count}));
		}
		return map;
	}
	
	public List<Map<String, Object>> getMyStore(Employee employee){
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		SqlRowList rs = new SqlRowList();
		rs = baseDao.queryForRowSet("SELECT DISTINCT s.sn_id as id,s.SN_DISPLAYNAME as name,s.sn_url as url,s.sn_caller,ps.SN_DISPLAYNAME as type,'使用' as using FROM sysnavigation s "+
			" left join sysnavigation ps on s.sn_parentid = ps.sn_id   "+
			" WHERE s.sn_isleaf ='T' AND s.sn_using=1 AND s.sn_addurl is null AND s.sn_id in (select cr_snid from commonreports WHERE CR_EMCODE = '"+employee.getEm_code()+"')");
		if (rs.next()) {
			datas = rs.getResultList();
		}
		return datas;
	}
	
	public void changeReports(int sn_id,Employee employee,String type){
		String em_code = employee.getEm_code();
		if("1".equals(type)){
			String instSql = "INSERT INTO COMMONREPORTS(CR_ID,CR_SNID,CR_USING,CR_EMCODE) VALUES (COMMONREPORTS_SEQ.nextval,"
					   + "'"+sn_id+"',"
					   + "'使用',"
					   + "'"+em_code+"')";
			baseDao.execute(instSql);
		}else{
			String delSql = "delete commonreports where cr_emcode = '"+em_code+"' and cr_snid = "+sn_id;
			baseDao.execute(delSql);
		}
	}
}
