package com.uas.erp.service.hr.impl;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DataListComboDao;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.hr.EmpWorkDateSetService;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmpWorkDateSetServiceImpl implements EmpWorkDateSetService {

    @Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	
	@Autowired
	private EmployeeDao employeeDao;
	
	@Autowired
	private DetailGridDao detailGridDao;
	
	@Autowired
	private DataListComboDao dataListComboDao;
	
    @Override
    public void saveEmpWorkDateSet(String formStore, String gridStore, String caller) throws ParseException {
        Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
        String fromdate = store.get("ew_date_from").toString();
		String todate = store.get("ew_date_to").toString();
		String emps=store.get("ew_emids").toString();
		String [] emp= emps.split(";");
		List<Object> objdate = new ArrayList<Object>();
		List<String> sqls = new ArrayList<String>();
		try {
			objdate = DateUtil.findDates(fromdate, todate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		StringBuffer sb=new StringBuffer("以下日期已排班员工如下：");
		sb.append("<hr>");
		String emps1=emps.replace(";", ",");
		for (Object od : objdate) {
			for(int i=0;i<7;i++){
				if("1".equals(store.get("ew_w"+i).toString())&& DateUtil.getWeekDay1(od.toString())==i){
					Object msg = baseDao.getFieldDataByCondition("Employee", "WMSYS.wm_concat(em_code)", "em_id in (select ew_emid from EmpWorkDate WHERE to_char(ew_date, 'yyyy-mm-dd')='"+od.toString()+"' and ew_emid in ("+emps1+"))");
					if(msg!=null){
						sb.append(od+":"+msg).append("<hr>");
					}
					for(int j=0;j<emp.length;j++){
						int id = baseDao.getSeqId("EMPWORKDATE_SEQ");
						String sql="insert into EmpWorkDate(ew_id ,ew_date,ew_emid,ew_wdcode) values( "+id+",to_date('"+od.toString()+"','yyyy-mm-dd'),"+Integer.parseInt(emp[j])+",'"+store.get("ew_wdcode").toString()+"')";
						sqls.add(sql);
					}
				}
			}
		}
		if(sb.length()==16){
			baseDao.execute(sqls);
			baseDao.execute("update empworkdate set ew_emcode=(select em_code from employee where ew_emid=em_id)");	
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name()+"("+SystemSession.getUser().getEm_code()+")"
					, "排班操作", "排班成功。班次："+store.get("ew_wdcode").toString()+",时间："+fromdate+" ~ "+todate+" ", "empWorkDateSet|wd_code="+store.get("ew_wdcode").toString()+"|fromdate="+fromdate+"|todate="+todate));
		}else{
			sb.append("请修调整后继续操作");
			BaseUtil.showError(sb.toString());
		}
	}
    @Override
    public List<JSONTree> getWdTreeAndEmployees(String caller) {
    	List<JSONTree> treeList = new ArrayList<JSONTree>();
		SqlRowList rs=baseDao.queryForRowSet("select DISTINCT em_wdcode,wd_name,wd_id from employee left join workdate on em_wdcode=wd_code where wd_name is not null and em_wdcode is not null");
		while(rs.next()){
			List<Employee> employees=employeeDao.getEmployeesByConditon("nvl(em_workattendance,0)<>0 and em_wdcode='"+rs.getString("em_wdcode")+"'");
			JSONTree jsonTree =new JSONTree();
			jsonTree.setId(rs.getInt("wd_id"));
			jsonTree.setText(rs.getString("wd_name"));
			jsonTree.setData(employees);
			jsonTree.setAllowDrag(true);
			jsonTree.setLeaf(false);
			jsonTree.setCls("x-tree-parent");
			treeList.add(jsonTree);
		}
		return treeList;
	}

	@Override
	public List<JSONObject> search(String likestring) {
		List<JSONObject> js=new ArrayList<JSONObject>();
	    SqlRowList orsl=baseDao.queryForRowSet("select  or_name,or_id from Hrorg where nvl(or_statuscode,' ')<>'DISABLE' and or_name like '%"+likestring+"%' ");
	    while(orsl.next()){
	    	JSONObject  ob=new JSONObject();
	    	ob.put("text", "<font color=\"#D52B2B\">[组织]</font>"+orsl.getString(1));
	    	Object[] ob1=baseDao.getFieldsDataByCondition("HRORGEMPLOYEES left join employee on om_emid=em_id",new String[]{"lob_concat(em_name)","lob_concat(em_id)"}, "om_orid="+orsl.getInt(2));   			
	    	ob.put("value",ob1[1]==null?"":ob1[1].toString());
	    	ob.put("value1",ob1[0]==null?"":ob1[0].toString());
	    	js.add(ob);
	    }
	    SqlRowList jobsl=baseDao.queryForRowSet("select  jo_name,jo_id from job where nvl(jo_statuscode,' ')<>'DISABLE' and jo_name like '%"+likestring+"%' ");
	    while(jobsl.next()){
	    	JSONObject  ob=new JSONObject();
	    	ob.put("text", "<font color=\"#C4C43C\">[岗位]</font>"+jobsl.getString(1));
	    	Object[] ob1=baseDao.getFieldsDataByCondition("employee",new String[]{"nvl(lob_concat(em_name),'')","nvl(lob_concat(em_id),'')"}, "em_defaulthsid="+jobsl.getInt(2));   
	    	ob.put("value",ob1[1]==null?"":ob1[1].toString());
	    	ob.put("value1",ob1[0]==null?"":ob1[0].toString());
	    	js.add(ob);
	    }
	    SqlRowList employeesl=baseDao.queryForRowSet("select  em_name,em_id from employee where nvl(em_class,' ')<>'离职' and em_name like '%"+likestring+"%' ");
	     while(employeesl.next()){
	    	 JSONObject  ob=new JSONObject();
	    	ob.put("text", employeesl.getString("em_name"));
	    	ob.put("value",employeesl.getInt("em_id")+"");
	    	ob.put("value1", employeesl.getString("em_name"));
	    	js.add(ob);
	    }
	    return js;
	}

	@Override
	public List<Map<String, Object>> getDatas(String condition) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		String sql="select to_char(ew_date,'dd') d,em_code,em_name,em_depart,ew_wdcode from Empworkdate left join employee on ew_emid=em_id  where "+condition+" order by em_id,to_char(ew_date,'dd')";
		SqlRowList rs=baseDao.queryForRowSet(sql);
		int c=rs.size();
		String emcode="";
		int index = 0;
		Map<String, Object> item=null;
		while(rs.next()){
			index++;
			if(!emcode.equals(rs.getString("em_code"))){
				if(index!=1){
					store.add(item);
				};
				item = new HashMap<String, Object>();
				emcode=rs.getString("em_code");
				item.put("em_depart",rs.getString("em_depart"));
				item.put("em_code",rs.getString("em_code"));
				item.put("em_name",rs.getString("em_name"));
				String field="";
				for(int i=1 ;i<32;i++){
					if(i<10){
						field="0"+i;						
					}else if(i<32){
						field=i+"";
					}
					item.put(field,"");
				}
				item.put(rs.getString("d"),rs.getString("ew_wdcode"));
				if(index==c){
					store.add(item);
				}
			}else{
				item.put(rs.getString("d"),rs.getString("ew_wdcode"));
				if(index==c){
					store.add(item);
				}
			}

		}
		return store;
	}

	@Override
	public String deleteEmpworkdate(String caller, String data) {
		List<Map<Object,Object>> maps=BaseUtil.parseGridStoreToMaps(data);
		String ids = CollectionUtil.pluckSqlString(maps, "ew_id");
		baseDao.execute("delete Empworkdate where ew_id in("+ids+")");		
		return "处理成功";
	
	}

}
