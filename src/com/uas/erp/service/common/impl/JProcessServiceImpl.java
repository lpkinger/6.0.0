package com.uas.erp.service.common.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.dao.common.JProcessDao;
import com.uas.erp.dao.common.JProcessSetDao;
import com.uas.erp.dao.common.ProcessDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.JNodeEfficiency;
import com.uas.erp.model.JProCand;
import com.uas.erp.model.JProcess;
import com.uas.erp.model.JProcessSet;
import com.uas.erp.model.JnodeRelation;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.JProcessService;
@Service
public class JProcessServiceImpl implements JProcessService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private JProcessDao jprocessDao;
	@Autowired
	private  ProcessDao processDao;
	@Autowired
	private JProcessSetDao processSetDao;
	@Autowired
	private EmployeeDao employeeDao;

	@Override
	public List<JProcess> getJProcessList(int page, int pageSize) {
		return jprocessDao.getAllJProcess(page, pageSize);
	}

	@Override
	public List<JProcess> getReviewedJProcessList(int page, int pageSize) {

		return jprocessDao.getAllReviewedJProcess(page, pageSize);
	}

	@Override
	public List<JNodeEfficiency> getJNodeEfficiencysList(int page, int pageSize) {
		List<JProcess> jps = jprocessDao.getAllReviewedJProcess(page, pageSize);
		//List<String> processInstanceIds = new LinkedList<String>();
		List<JNodeEfficiency> jes = new LinkedList<JNodeEfficiency>();

		for(JProcess jp:jps){
			long wholeMinutes = jprocessDao.getDurationOfInProcessInstance(jp.getJp_form());
			int sum = jprocessDao.getSumOfNode(jp.getJp_form());
			Map<String,Integer> taskInfo = getDuedateOfJNodeInProcessInstance(jp.getJp_form());
			int standard = taskInfo.get(jp.getJp_nodeName());
			long timeout = 0 ;
			if(standard>0){
				timeout = jp.getJp_stayMinutes()-standard*60;
			}
			JNodeEfficiency  je= new JNodeEfficiency(jp.getJp_name(),jp.getJp_form(),jp.getJp_nodeName(),jp.getJp_nodeDealMan(),
					jp.getJp_stayMinutes(),wholeMinutes/sum,standard*60,timeout);
			jes.add(je);
		}
		return jes;
	}

	@Override
	public int getJProcessCount() {
		return jprocessDao.getAllJProcessCount();
	}
	@Override
	public void delete(String ids) {
		String[] s = ids.split(",");
		for (String id : s) {
			jprocessDao.delete(Integer.parseInt(id));			
		}		

	}
	@Override
	public List<JProcess> search(String condition, int page, int pageSize) {
		return jprocessDao.search(condition, page, pageSize);
	}
	@Override
	public List<JNodeEfficiency> searchJNodeEfficiency(String condition,int page, int pageSize) {
		List<JProcess> jps = jprocessDao.search(condition, page, pageSize);
		List<JNodeEfficiency> jes = new LinkedList<JNodeEfficiency>();

		for(JProcess jp:jps){
			long wholeMinutes = jprocessDao.getDurationOfInProcessInstance(jp.getJp_form());
			int sum = jprocessDao.getSumOfNode(jp.getJp_form());
			Map<String,Integer> taskInfo = getDuedateOfJNodeInProcessInstance(jp.getJp_form());
			int standard = taskInfo.get(jp.getJp_nodeName());
			long timeout = 0 ;
			if(standard>0){// standard = 0  则无期限,永不超时.
				timeout = jp.getJp_stayMinutes()- standard*60;
			}
			JNodeEfficiency  je= new JNodeEfficiency(jp.getJp_name(),jp.getJp_form(),jp.getJp_nodeName(),jp.getJp_nodeDealMan(),
					jp.getJp_stayMinutes(),wholeMinutes/sum,standard*60,timeout);
			jes.add(je);
		}
		return jes;
	}
	@Override
	public int searchCount(String condition) {
		return jprocessDao.searchCount(condition);
	}
	/**
	 * 设置节点处理人 只能抓待审批的
	 *
	 */
	@Override
	public Map<String,Object> getJprocessNode(String caller, int keyValue,String type) {
		Map<String,Object> map=new HashMap<String,Object>();
		//历史和当前
		Object flowcaller=baseDao.getFieldDataByCondition("Form","fo_flowcaller", "fo_caller='"+caller+"'");
		if(flowcaller==null) flowcaller=caller;
		String condition="jp_keyvalue="+keyValue+" and jp_caller='"+flowcaller+"'";
		if(type!=null && type.equals("current")) {
			condition+=" and jp_status<>'已结束' ";
		}else if(type!=null && type.equals("currentunhand")){
			condition+=" and jp_status='待审批' ";
		}
		Object[] data = baseDao.getFieldsDataByCondition("JProcess", "jp_nodeid,jp_jdid,jp_processInstanceId,jp_nodename,jp_processdefid",  condition+ " order by JP_LAUNCHTIME desc");		
		if(data != null){
			map.put("node", Integer.parseInt(data[0].toString()));
			map.put("jd", baseDao.getFieldDataByCondition("Jprocessdeploy", "jd_id", "jd_caller='"+flowcaller+"'"));
			map.put("instanceId",data[2]);
			map.put("nodename", data[3]);
			map.put("type", "JPROCESS");
			map.put("processDefId", data[4]);
			return map;
		} else {
			//没有指定到 指定人			
			data=baseDao.getFieldsDataByCondition("JProcand", "jp_nodeid,jp_processInstanceId,jp_nodename,jp_processdefid", condition+
					" and jp_flag=1 order by jp_id desc");			 
			if(data!=null){
				map.put("node",Integer.parseInt(data[0].toString()));
				map.put("jd",baseDao.getFieldDataByCondition("Jprocessdeploy", "jd_id", "jd_caller='"+flowcaller+"'"));
				map.put("instanceId",data[1]);
				map.put("nodename", data[2]);
				map.put("processDefId", data[3]);
				map.put("type", "JPROCAND");
			}else {
				map.put("node",-1);
				map.put("jd",-1);
				map.put("instanceId",-1);
				map.put("nodename", "");
				map.put("processDefId", -1);
				map.put("type", "JPROCESS");
			}

			return map ;
		}
	}
	//获取当前所有待处理的节点
	public List<Map<String,Object>> getAllNodesUnDeal(String caller, int keyValue) {
		List<Map<String,Object>> maps=new ArrayList<Map<String,Object>>();
		//历史和当前
		Object flowcaller=baseDao.getFieldDataByCondition("Form","fo_flowcaller", "fo_caller='"+caller+"'");
		String condition="jp_keyvalue="+keyValue+" and jp_caller='"+flowcaller+"' and jp_status='待审批'";	
		maps=baseDao.getJdbcTemplate().queryForList("select jp_nodeid as node,jp_jdid as jd,jp_processInstanceId as instanceId,jp_nodename as nodename,jp_processdefid as processDefId,'JPROCESS' as type from jprocess where  "+condition);
		//没有指定到 指定人			
		SqlRowList sl=baseDao.queryForRowSet("select distinct jp_nodename as nodename,jp_nodeid as node,jp_processInstanceId as instanceId,jp_processdefid  as processDefId,'JPROCAND'  as type from jprocand where "+ condition+" and jp_flag=1 ");
		while(sl.next()){
			maps.add(sl.getCurrentMap());
		}
		return maps;
	}
	@Override
	public Map<String, Integer> getDuedateOfJNodeInProcessInstance(String processInstanceId) {
		String processDefId = processDao.getProcessDefIdByProcessInstanceId(processInstanceId);
		return jprocessDao.getDuedateOfJNode(processDefId);
	}

	@Override
	public List<JNodeEfficiency> getTimeoutNodeList(int page, int pageSize) {
		List<JNodeEfficiency> jes = getJNodeEfficiencysList(page,pageSize);
		List<JNodeEfficiency> jes2 = new LinkedList<JNodeEfficiency>();
		for(JNodeEfficiency je:jes){
			if(je.getJe_nodeTimeout()>0){
				jes2.add(je);
			}
		}
		return jes2;
	}

	@Override
	public List<JNodeEfficiency> searchTimeoutJNode(String condition,int page, int pageSize) {
		List<JNodeEfficiency> jes = searchJNodeEfficiency(condition,page, pageSize);
		List<JNodeEfficiency> jes2 = new LinkedList<JNodeEfficiency>();
		for(JNodeEfficiency je:jes){
			if(je.getJe_nodeTimeout()>0){
				jes2.add(je);
			}
		}
		return jes2;

	}

	@Override
	public List<Map<String,Object>> SetCurrentJnodes(String caller, int keyValue) {
		// TODO Auto-generated method stub
		Object FlowCaller=baseDao.getFieldDataByCondition("Form","fo_flowcaller","fo_caller='"+caller+"'");		
		List<Map<String,Object>> lists=new ArrayList<Map<String,Object>>();
		Object definitionId=null;
		boolean bool=true;
		//获取condition信息
		if(FlowCaller!=null){             
			JProcessSet processset=processSetDao.getCallerInfo(FlowCaller.toString());
			Map<String ,Object> current=getJprocessNode(caller, keyValue, "current");
			definitionId=current.get("processDefId");
			if(!(current!=null && !current.get("instanceId").equals("-1") && definitionId !=null && !definitionId.equals("-1"))){
				//dangqian
				definitionId=baseDao.getFieldDataByCondition("JprocessDeploy", "jd_processdefinitionid", "jd_caller='"+FlowCaller+"'");	
			}		   
			bool=baseDao.checkIf("JNodePerson", "jp_caller='"+FlowCaller+"' and jp_keyvalue="+keyValue+" and jp_processdefid='"+definitionId+"'");
			if(!bool) {
				List<JnodeRelation> relations=jprocessDao.getJnodeRelationsByDefId(String.valueOf(definitionId));				
				if(relations.size()>0){
					for(JnodeRelation startrelation: relations){
						if(startrelation.getJr_type().equals("start")){
							addItems(lists,relations,startrelation,processset,keyValue);
						}	
					}									
				}				
			}
		}
		if(!bool){
			List<String> sqls=new ArrayList<String>();
			for(Map<String,Object> map:lists){
				map.put("JP_ID",baseDao.getSeqId("JNODEPERSON_SEQ"));
				map.put("JP_CALLER", FlowCaller);
				map.put("JP_KEYVALUE", keyValue);
				map.put("JP_PROCESSDEFID",definitionId);
				sqls.add(SqlUtil.getInsertSqlByMap(map, "JNodePerson"));
			}
			baseDao.execute(sqls);
			return lists;
		}
		//返回需要设置的节点
		SqlRowList sl=baseDao.queryForRowSet("select JP_ID,JP_CALLER,JP_KEYVALUE,JP_NODENAME,JP_NODEDEALMAN,JP_PROCESSDEFID,JP_NODEDEALMANNAME,JP_NEWNODEDEALMAN,JP_NEWNODEDEALMANNAME,JP_CANEXTRA,JP_EXTRAMAN,JP_EXTRAMANNAME from jnodeperson where jp_caller='"+FlowCaller+"' and jp_keyvalue="+keyValue+" and jp_processdefid='"+definitionId+"' order by jp_id");
		while(sl.next()){
			lists.add(sl.getCurrentMap());
		} 
		return lists;
	}
	private void addItems(List<Map<String,Object>> lists,List<JnodeRelation> relations,JnodeRelation parentRelation,JProcessSet processset,int keyValue)	{
		Map<String,Object> map=null; 
		String torelation=parentRelation.getJr_to();
		String typerelation=parentRelation.getJr_type();
		if(torelation!=null){
			if(typerelation.equals("decision")){
				String conditions=parentRelation.getJr_condition();
				String arr[]=conditions.split(",");
				String toarr[]=torelation.split(",");
				for(int j=0;j<arr.length;j++){
					if(conditionreturn(arr[j],processset,keyValue,parentRelation.getJr_processdefid())){
						JnodeRelation re=FindItem(relations,toarr[j]);
						if(re!=null){
							if(re.getJr_type().equals("task")){
								map=new HashMap<String,Object>();
								List<String> mans=getActualNodeDealMan(re,processset,keyValue);
								map.put("JP_NODENAME", re.getJr_name());
								map.put("JP_NODEDEALMAN",(mans!=null && mans.get(0)!=null)?mans.get(0):re.getJr_nodedealman());
								map.put("JP_NODEDEALMANNAME",(mans!=null && mans.get(1)!=null)?mans.get(1):re.getJr_nodedealmanname());
								map.put("JP_CANEXTRA", re.getJr_canextra());
								lists.add(map);
							}
							if(!(re.getJr_to().startsWith("end") && re.getJr_to().indexOf(",")<0)){
								addItems(lists,relations,re,processset,keyValue);
							}
						}
					} 
				}
			}else if(typerelation.equals("fork")){
				String []toarr=torelation.split(",");
				int index=0;
				for(String str:toarr){
					JnodeRelation re=FindItem(relations,str);
					if(re!=null){
						if(re.getJr_type().equals("task")){
							map=new HashMap<String,Object>();
							List<String> mans=getActualNodeDealMan(re,processset,keyValue);
							map.put("JP_NODENAME", re.getJr_name());
							map.put("JP_NODEDEALMAN",(mans!=null && mans.get(0)!=null)?mans.get(0):re.getJr_nodedealman());
							map.put("JP_NODEDEALMANNAME",(mans!=null && mans.get(1)!=null)?mans.get(1):re.getJr_nodedealmanname());
							map.put("JP_CANEXTRA", re.getJr_canextra());
							lists.add(map);
						}
						index++;
						if(index==toarr.length && !(re.getJr_to().startsWith("end") && re.getJr_to().indexOf(",")<0)){
							addItems(lists,relations,re,processset,keyValue);
						}
					}	
				}								
			}else if(typerelation.equals("join")){
				//多条汇聚  重复
				boolean bool=true;
				for(Map<String,Object> m:lists){
					if(m.get("JP_NODENAME").equals(torelation)){
						bool=false;
						break;
					}
				}
				if(bool){
					JnodeRelation re=FindItem(relations,torelation);
					if(re!=null){
						if(re.getJr_type().equals("task")){
							map=new HashMap<String,Object>();
							List<String> mans=getActualNodeDealMan(re,processset,keyValue);
							map.put("JP_NODENAME", re.getJr_name());
							map.put("JP_NODEDEALMAN",(mans!=null && mans.get(0)!=null)?mans.get(0):re.getJr_nodedealman());
							map.put("JP_NODEDEALMANNAME",(mans!=null && mans.get(1)!=null)?mans.get(1):re.getJr_nodedealmanname());
							map.put("JP_CANEXTRA", re.getJr_canextra());
							lists.add(map);
						}
						if(!(re.getJr_to().startsWith("end") && re.getJr_to().indexOf(",")<0)){
							addItems(lists,relations,re,processset,keyValue);
						}
					}
				}

			}else if(typerelation.equals("sql")){
				JnodeRelation re=FindItem(relations, torelation);
				//通过parent
				//String condition=parentRelation.getJr_condition();
				//String sql=parentRelation.getJr_nodedealman();
				//String nodedealman="",nodedealmanname="";
				//String queryobject=condition.split("\\|")[1];
				//String queryfield=queryobject.substring(queryobject.lastIndexOf("{")+1, queryobject.lastIndexOf("}"));
				//Object fielddata=baseDao.getFieldDataByCondition(processset.getJs_table(),queryfield,processset.getJs_formKeyName()+"="+keyValue);			
				/*SqlRowList sl1=baseDao.queryForRowSet("select wmsys.wm_concat(em_code) as code,wmsys.wm_concat(em_name) as name from employee where em_code in ("+sql.split("=:")[0]+" ='"+fielddata+"') order by em_code ");

				if(sl1.next()){
					nodedealman=sl1.getString(1);
					nodedealmanname=sl1.getString(2);
				}*/
				if(re!=null){
					if(re.getJr_type().equals("task")){
						map=new HashMap<String,Object>();
						List<String> mans=getActualNodeDealMan(re,processset,keyValue);
						map.put("JP_NODENAME", re.getJr_name());
						map.put("JP_NODEDEALMAN",(mans!=null && mans.get(0)!=null)?mans.get(0):re.getJr_nodedealman());
						map.put("JP_NODEDEALMANNAME",(mans!=null && mans.get(1)!=null)?mans.get(1):re.getJr_nodedealmanname());
						map.put("JP_CANEXTRA", re.getJr_canextra());
						lists.add(map);
					}
					if(!(re.getJr_to().startsWith("end") && re.getJr_to().indexOf(",")<0)){
						addItems(lists,relations,re,processset,keyValue);
					}
				}

			}else{
				JnodeRelation re=FindItem(relations,torelation);
				if(re!=null){
					if(re.getJr_type().equals("task")){
						map=new HashMap<String,Object>();
						List<String> mans=getActualNodeDealMan(re,processset,keyValue);
						map.put("JP_NODENAME", re.getJr_name());
						map.put("JP_NODEDEALMAN",(mans!=null && mans.get(0)!=null)?mans.get(0):re.getJr_nodedealman());
						map.put("JP_NODEDEALMANNAME",(mans!=null && mans.get(1)!=null)?mans.get(1):re.getJr_nodedealmanname());
						map.put("JP_CANEXTRA", re.getJr_canextra());
						lists.add(map);
					}
					if(!(re.getJr_to().startsWith("end") && re.getJr_to().indexOf(",")<0)){
						addItems(lists,relations,re,processset,keyValue);
					}
				}
			}
		}
	}
	public boolean conditionreturn(String condition,JProcessSet processset,int keyValue,String processDefid){
		String conditionFields=processset.getJs_decisionCondition();
		String VariablesFields=processset.getJs_decisionVariables();
		condition=condition.substring(condition.indexOf("#{")+2, condition.lastIndexOf("}"));
		/*if(conditionFields!=null){
			String []conditionarr=conditionFields.split("#");
			String []variablesarr=VariablesFields.split("#");
			for(int i=0;i<conditionarr.length;i++){
				condition=condition.replaceAll(variablesarr[i], conditionarr[i]);
			}
		}
		condition=condition.trim();
		condition=condition.replaceAll("==", "=").replaceAll("&&"," and ").replaceAll("\"","'");
		condition=condition.replaceAll("\\|\\|", " or ").replaceAll("!=", "<>");
		if(condition.indexOf(".contains")>0){
			condition=condition.replaceAll(".contains"," like ").replaceAll("\\('", "'%").replaceAll("'\\)", "%'"); 
		}
		String querySql="select case when "+condition +" then 1 else 0 end  from "+processset.getJs_table() +" where "+processset.getJs_formKeyName()+"="+keyValue;

		SqlRowList sl=baseDao.queryForRowSet(querySql);
		if(sl.next()){
			return sl.getInt(1)==1;
		}*/
		condition=getExpression(condition);
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("js");
		if(conditionFields!=null){
			String []conditionarr=conditionFields.split("#");
			String []variablesarr=VariablesFields.split("#");
			Object []data=baseDao.getFieldsDataByCondition(processset.getJs_table(), conditionarr, processset.getJs_formKeyName()+"="+keyValue+" "+(processset.getJs_groupby()!=null?processset.getJs_groupby():""));
			if(data!=null){
				for(int i=0;i<variablesarr.length;i++){
					engine.put(variablesarr[i],data[i]);
				}

			}
			List<Object[]> objs=baseDao.getFieldsDatasByCondition("Jnoderelation",new String[]{"Jr_Nodedealman","Jr_condition"},  "jr_type='sql' and jr_processdefid='"+processDefid+"'");
			for(Object[] obj:objs){
				if(obj!=null && obj.length>0 && obj[0]!=null && obj[1]!=null){
					String queryobject=obj[1].toString().split("\\|")[1];
					String queryfield=queryobject.substring(queryobject.lastIndexOf("{")+1, queryobject.lastIndexOf("}"));
					/* 解决流程使用sql时参数后面还有右括号 */
					SqlRowList sl1=baseDao.queryForRowSet(obj[0].toString().replace(":" + queryfield, "'" + String.valueOf(engine.get(queryfield)) + "'"));
					if(sl1.next()){
						for(Map<String,Object> map2:sl1.getResultList()){
							Iterator<?> it=map2.entrySet().iterator();
							while(it.hasNext()){
								Map.Entry e=(Map.Entry)it.next();
								engine.put(String.valueOf(e.getKey()).toLowerCase(),e.getValue());	
							}
						}
					}
			}
		}
	}
		boolean result=false;
		try {
			result= Boolean.valueOf(engine.eval(condition).toString());
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private String getExpression(String condition){
		condition=condition.replace(" ","");
		Pattern p =Pattern.compile("[&]{2}|[\\|]{2}");  
		Matcher m = p.matcher(condition);  
		String []b=p.split(condition);
		StringBuffer sb=new StringBuffer();		 
		if(b.length > 0)  
		{  
			int count = 0;
			String exp="";
			while(count < b.length)  
			{  
				exp=b[count];
				if(b[count].contains(".contains")){	    		
					exp=exp.replace(".contains", ".indexOf");
					if(exp.matches("^[\\(]{0,}!.*")){
						exp=exp.replaceFirst("\\)",")<0").replaceFirst("\\!", "");
					}else
						exp=exp.replaceFirst("\\)", ")>=0");	
				}
				sb.append(exp);
				if(m.find())  
				{  
					sb.append(" "+m.group()+" ");
				}  
				count++;  
			}  
		}  
		return sb.toString();
	}
	private JnodeRelation FindItem(List<JnodeRelation> relations,String nodename){
		for(JnodeRelation relation:relations){
			if(relation.getJr_name().equals(nodename)) return relation;
		}
		return null;
	}
    @SuppressWarnings("unused")
	private List<String> getActualNodeDealMan(JnodeRelation relation,JProcessSet processset,int keyValue){
    	List<String> list=new ArrayList<String>();
    	StringBuffer codes=new StringBuffer();
    	StringBuffer names=new StringBuffer();
    	String jobs=baseDao.getFieldValue("JTASK", "JT_JOBS","jt_processdefid='"+relation.getJr_processdefid()+"' and jt_name='"+relation.getJr_name()+"'",String.class);
    	if(jobs!=null && !jobs.equals("") && !jobs.equals("null")){
        	List<Employee> emps=employeeDao.getEmployeesByJobs(jobs.split(","));
        	if(emps!=null){
        		for(Employee em:emps){
        			codes.append(em.getEm_code()+",");
        			names.append(em.getEm_name()+",");
        		}
        	}
        }
    	String sqlAssignee=baseDao.getFieldValue("JTASK", "JT_ASSIGNEE","jt_processdefid='"+relation.getJr_processdefid()+"' and jt_name='"+relation.getJr_name()+"'",String.class);
    	if(sqlAssignee!=null && !sqlAssignee.equals("") && !sqlAssignee.equals("null")&& (sqlAssignee.startsWith("$")||sqlAssignee.startsWith("@")||sqlAssignee.startsWith("#"))){
    		List<Map<String, Object>> sqlVarList=new ArrayList<Map<String, Object>>();						    		
    		if (processset.getJs_decisionCondition()!=null && !processset.getJs_decisionCondition().equals("") && !processset.getJs_decisionCondition().equals("null"))
			{String[] varList= processset.getJs_decisionCondition().split("#");						
			for (String var : varList) {
				Object fielddata=baseDao.getFieldDataByCondition(processset.getJs_table(),var,processset.getJs_formKeyName()+"="+keyValue);	
				Map<String, Object> map1 = new HashMap<String, Object>();	
				map1.put(var,fielddata);
				sqlVarList.add(map1);
			}}
			List<Object[]> objs=baseDao.getFieldsDatasByCondition("Jnoderelation",new String[]{"Jr_Nodedealman","Jr_condition"},  "jr_type='sql' and jr_processdefid='"+relation.getJr_processdefid()+"'");
			String conditions = processset.getJs_decisionCondition();
			String[] cons = null;
			String[] vars = null;
			if(conditions!=null){
				cons = conditions.split("#");
				vars = processset.getJs_decisionVariables().split("#");
			}
			
			for(Object[] obj:objs){
				if(obj!=null && obj.length>0 && obj[0]!=null && obj[1]!=null){
					String queryobject=obj[1].toString().split("\\|")[1];
					String queryfield=queryobject.substring(queryobject.lastIndexOf("{")+1, queryobject.lastIndexOf("}"));
					
					String query = null;
					if(queryfield!=null){
						for(int i=0;i<vars.length;i++){
							String var = vars[i];
							if(queryfield.equals(var)){
								query = cons[i];
								break;
							}
						}
					}
					Object fielddata=baseDao.getFieldDataByCondition(processset.getJs_table(),query,processset.getJs_formKeyName()+"="+keyValue);
					/* 解决流程使用sql时参数后面还有右括号 */
					SqlRowList sl1=baseDao.queryForRowSet(obj[0].toString().replace(":" + queryfield, "'" + String.valueOf(fielddata) + "'"));
				if(sl1.next()){
					for(Map<String,Object> map2:sl1.getResultList()){
						Map<String, Object> map1 = new HashMap<String, Object>();	
						Iterator<?> it=map2.entrySet().iterator();
						while(it.hasNext()){
							Map.Entry e=(Map.Entry)it.next();
							map1.put(String.valueOf(e.getKey()).toLowerCase(),e.getValue());
						}					
						sqlVarList.add(map1);
					}					
				}
			}
		}
    		for (Map<String, Object> sqlvar : sqlVarList) {			
    			Iterator<?> it=sqlvar.entrySet().iterator();
    			while(it.hasNext()){
    			Map.Entry e=(Map.Entry)it.next();
    			if((sqlAssignee.startsWith("$")&&sqlAssignee.substring(1).equals(e.getKey()))||(sqlAssignee.startsWith("#")&&sqlAssignee.substring(2,sqlAssignee.length()-1).equals(e.getKey()))){   //code 				    			
    					Employee emp=employeeDao.getEmployeeByEmCode(String.valueOf(e.getValue()));
    					codes.append(String.valueOf(e.getValue())+",");
            			names.append(emp.getEm_name()+",");   				    
    			}else if(sqlAssignee.startsWith("@")&&sqlAssignee.substring(1).equals(e.getKey())){  //code or name  				    			
					Employee emp=employeeDao.getEmployeeByConditon(" nvl(em_class, ' ')<>'离职' and (em_code='"+e.getValue()+"' or em_name='"+e.getValue()+"')");
					codes.append(emp.getEm_code()+",");
        			names.append(emp.getEm_name()+",");   				    
    			}
    		  }
    		}	
        }
        if(codes!=null && codes.length()!=0){
        	String emcode=codes.toString();
        	String emname=names.toString();
        	list.add(emcode.substring(0, emcode.length()-1));
        	list.add(emname.substring(0, emname.length()-1));
        	return list;
        }else return null;
    }
	@Override
	public void updateJnodePerson(String param,String caller,Integer id,Employee employee) {
		// TODO Auto-generated method stub
		id=id==null?0:id;
		List<Map<String,Object>> currentmaps=getAllNodesUnDeal(caller,id);
		List<Map<Object,Object>> maps=BaseUtil.parseGridStoreToMaps(param);
		for(Map<Object,Object> m:maps){
			for(Map<String,Object> currentmap:currentmaps){
				if(m.get("JP_NODENAME").equals(currentmap.get("nodename"))){
					//
					Object extra=m.get("JP_EXTRAMAN");
					Object newdeal=m.get("JP_NEWNODEDEALMAN");
					newdeal=extra!=null && !extra.equals("") && !extra.equals("null")?extra:newdeal;
					String nodeId=currentmap.get("node").toString();
					if( newdeal!=null && !newdeal.equals("") && !newdeal.equals("null")){
						if("JPROCESS".equals(currentmap.get("type"))){
							processDao.updateAssigneeOfJprocess(nodeId,newdeal.toString());
						}else {
							List<JProCand> jcs =processDao.getJProCands(nodeId);   	   				 
							if(newdeal.toString().contains(",")){
								String []arr=newdeal.toString().split(",");
								String codeid="";
								for (String str:arr){
									codeid+="'"+str+"',";
								}
								codeid="("+codeid.substring(0, codeid.length()-1)+")";
								baseDao.updateByCondition("JProCand", "jp_flag=0,jp_status='已结束',jp_type='takeover'","jp_nodeid='"+nodeId+"' and jp_candidate not in "+codeid);
							}else{
								Master master = employee.getCurrentMaster();
								processDao.saveJProcessFromJProCand(jcs.get(0), newdeal.toString(), master);
								processDao.updateFlagOfJProCands(jcs.get(0));
							}
						}
					}
				} 
			}

		}	
		List<String>sqls=SqlUtil.getUpdateSqlbyGridStore(maps, "JnodePerson", "JP_ID");
		baseDao.execute(sqls);

	}
}
