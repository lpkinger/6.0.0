package com.uas.erp.service.plm.impl;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Key;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.plm.CheckBaseService;

@Service
public class CheckBaseServiceImpl implements CheckBaseService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private TransferRepository transferRepository;
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void saveCheckBase(String formStore) {
		Map<Object,Object> map=BaseUtil.parseFormStoreToMap(formStore);
		Object result=map.get("cbd_result");
		StringBuffer sb=new StringBuffer();
		List<String>sqls=new ArrayList<String>();
		Employee employee=SystemSession.getUser();
		String language=SystemSession.getLang();
		String code = baseDao.sGetMaxNumber("CHECKLISTBASEdetail", 2);
		int ch_cldid=0; //测试历史关联BUG单的ID
		int cbd_cldid=0; //测试单关联BUG单的ID
		baseDao.updateByCondition("CHECKHISTORY", "CH_DETNO=CH_DETNO+1", "CH_CBDID='"+map.get("cbd_id")+"'");
		int id = baseDao.getSeqId("CHECKHISTORY_SEQ");
		String gridSqls = "";
		String ch_cbdcode = "";
		String ch_cldstatus = "";
		if(map.get("cbd_testman")!=null && map.get("cbd_testman").equals(map.get("cbd_handman"))){ //测试人和处理人不能为同一人
			BaseUtil.showError("测试人和处理人不能是同一人");
		}
		if(result.equals("NG")){
			int num = baseDao.getCountByCondition("CheckListDetail", "cld_status='待处理' and cld_cbdid="+map.get("cbd_id")); //查询现在这个测试单是否有待处理的单据，一次只能有一个待处理的，为了能够多次测试，在有待处理的单据时，再次提交测试成功的，会自动把状态改为已处理
			if(num!=0){
				BaseUtil.showError("当前测试项已经存在待处理的BUG--"+map.get("cbd_name")+"，不能提交BUG");
			}
			if (baseDao.isDBSetting("CheckListBaseDetail", "turnCheckNeedCommit")) {    //测试结果NG时自动转BUG
				//生成Bug单
				//是否有转单配置
				String dets = baseDao.
						getJdbcTemplate().
						queryForObject("select wm_concat(td_tofield) from transferdetail left join transfers on td_trid = tr_id where tr_caller = 'CheckListBaseDetail!ToCheck'",String.class);
				//当前日期
				String date=DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date());
				Object cl_id=baseDao.getFieldDataByCondition("CheckList ", "cl_id","cl_cbid="+map.get("cbd_cbid"));
				int cld_id=baseDao.getSeqId("CHECKLISTDETAIL_SEQ");
				ch_cldid=cld_id;
				cbd_cldid=cld_id;
				int detno = 1;
				if(cl_id!=null){//已经生成bug单主记录，取序号
					SqlRowList sl=baseDao.queryForRowSet("select max(cld_detno) from checkListDetail where cld_clid="+cl_id);				
					if(sl.next()){
						detno=sl.getInt(1)==-1?0:sl.getInt(1);
						detno = detno+1;
					}	
				}else{//没有生成bug单主记录，插入主记录
					cl_id=baseDao.getSeqId("CheckList_SEQ");
					Object prjid=map.get("cb_prjid")!=null && !map.get("cb_prjid").equals("")?map.get("cb_prjid"):0,prjcode=map.get("cb_prjcode")!=null && !map.get("cb_prjcode").equals("")?map.get("cb_prjcode"):0;
					sb.append("insert into CheckList (cl_id,cl_code,cl_status,cl_statuscode,cl_recorder,cl_recorderid,cl_recorddate,cl_prjplanid,cl_prjplancode,cl_prjplanname,cl_pmman,cl_pmmancode,cl_cbid,cl_description)  values("+cl_id+",'BL_"+baseDao.sGetMaxNumber("CheckList",2)+"','"+BaseUtil.getLocalMessage("AUDITED", language)+"','AUDITED','"+employee.getEm_name()+"',"+employee.getEm_id()+","+date+",");
					sb.append(prjid+",'"+prjcode+"','"+map.get("cb_prjname")+"','"+employee.getEm_name()+"','"+employee.getEm_code()+"',"+map.get("cbd_cbid")+",'"+map.get("cb_remark")+"')");
					sqls.add(sb.toString());
					sb.setLength(0);
				}
				if (dets!=null) {
					//有转单配置使用转单配置
					Key key = transferRepository.transfer("CheckListBaseDetail!ToCheck", map.get("cbd_id"));
					cld_id = key.getId();
					ch_cldid=cld_id;
					cbd_cldid=cld_id;
					sb.append("update CheckListDetail set cld_clid="+cl_id+",cld_detno='"+detno+"',cld_testresult=0,cld_newtestman='"+employee.getEm_name()+"',cld_newtestdate="+date+",cld_newtestmanid="+employee.getEm_id()+",cld_exhibitor='"+employee.getEm_name()+"',cld_exhibitdate="+date+",cld_exhibitorid='"+employee.getEm_id()+"',cld_needlevel='"+map.get("cbd_problemgrade")+"',cld_problemrate='"+map.get("cbd_problemrate")+"',cld_testdescription='"+String.valueOf(map.get("cbd_testdescription")).replaceAll("'", "''")+"',cld_isconfirm='0',cld_newhandman='"+map.get("cbd_handman")+"',cld_newhandmanid='"+(map.get("cbd_handmanid")==null?0:map.get("cbd_handmanid"))+"' where cld_id = "+cld_id);
					sqls.add(sb.toString());
				}else{
					//没有转单配置直接插入
					sb.append("insert into CheckListDetail (cld_id,cld_clid,cld_cbdid,cld_detno,cld_testresult,cld_testdescription,cld_newtestman,cld_newtestdate,cld_newtestmanid,cld_newhandman,cld_newhandmanid,cld_name,cld_exhibitor,cld_exhibitdate,cld_exhibitorid,cld_sourcecode,cld_sourceid,cld_testman2,cld_needlevel,cld_problemrate,cld_isconfirm)values(");
					sb.append(cld_id+","+cl_id+","+map.get("cbd_id")+",'"+detno+"',0,'"+String.valueOf(map.get("cbd_testdescription")).replaceAll("'", "''")+"','"+employee.getEm_name()+"',"+date+","+employee.getEm_id()+",'"+map.get("cbd_handman")+"','"+map.get("cbd_handmanid")+"','"+map.get("cbd_name")+"','"+employee.getEm_name()+"',"+date+",'"+employee.getEm_id()+"','"+map.get("cbd_code")+"',"+map.get("cbd_id")+",'"+map.get("cbd_testman2")+"','"+map.get("cbd_problemgrade")+"','"+map.get("cbd_problemrate")+"','0')");
					sqls.add(sb.toString());
				}	
				//下面是记录BUG单明细行内容，并在提交的时候向处理人发送BUG测试不通过的信息
				int ch_id=baseDao.getSeqId("CHECKTABLE_SEQ");
				baseDao.execute("insert into checktable(ch_cldid,ch_description,ch_recorddate,ch_id,ch_type,ch_recorder,ch_detno) values("+cld_id+",'"+String.valueOf(map.get("cbd_testdescription")).replaceAll("'", "''")+"',sysdate,"+ch_id+",'Test','"+employee.getEm_name()+"',"+ch_id+")");
				int pr_id=baseDao.getSeqId("PAGINGRELEASE_SEQ");
				int prd_id=baseDao.getSeqId("PAGINGRELEASEDETAIL_SEQ");
				sb.setLength(0); 
				/*sb.append("任务提醒&nbsp;&nbsp;&nbsp;&nbsp;["+DateUtil.parseDateToString(DateUtil.parseStringToDate(null, "yyyy-MM-dd HH:mm:ss"), "MM-dd HH:mm")+"]</br>");
				sb.append("你有处理的BUG测试未通过!该单据名称为:&nbsp;&nbsp;");
				sb.append("<a href=\"javascript:openGridUrl(" + cld_id
						+ ",''cld_id'',''ch_cldid'',''jsps/plm/test/check.jsp'',''Check单''" + ")\" style=\"font-size:14px; color:blue;\">" + map.get("cbd_name") + "</a></br>");*/
				sb.append("("+employee.getEm_name()+")提交了新的BUG给您！");			
				sqls.add("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context,pr_from)values('"+pr_id+"','"+employee.getEm_name()+"',"+DateUtil.parseDateToOracleString(Constant.YMD, new Date())+",'"+employee.getEm_id()+"','"+sb.toString()+"','task')");
				sqls.add("insert into pagingreleasedetail(prd_id,prd_prid,PRD_RECIPIENTID,PRD_RECIPIENT) values('"+prd_id+"','"+pr_id+"','"+map.get("cbd_handmanid")+"','"+map.get("cbd_handman")+"')");
				//保存到历史消息表
				int IH_ID=baseDao.getSeqId("ICQHISTORY_SEQ");
				sqls.add("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
						+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
						+ " where pr_id="+pr_id);
				sqls.add("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
						+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+pr_id+"and ("+IH_ID+",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");
				map.remove("cbd_status");
				map.remove("cbd_statuscode");
				map.put("cbd_statuscode","PENDING");
				map.put("cbd_status",BaseUtil.getLocalMessage("PENDING", language));
				ch_cbdcode = map.get("cbd_name").toString();
				ch_cldstatus = map.get("cbd_status").toString();
			}else{
				ch_cldstatus = "待处理";
			}
			baseDao.execute("update CheckListDetail set cld_enclosure=(select cbd_enclosure from CheckListBaseDetail where cbd_id='"+map.get("cbd_id")+"') where cld_cbdid='"+map.get("cbd_id")+"'");
			baseDao.updateByCondition("CHECKLISTBASEdetail", "cbd_status='"+BaseUtil.getLocalMessage("PENDING", language)+"',cbd_statuscode='PENDING',cbd_result='"+map.get("cbd_result")+"',cbd_problemgrade='"+map.get("cbd_problemgrade")+"',cbd_problemrate='"+map.get("cbd_problemrate")+"',cbd_testdescription='"+String.valueOf(map.get("cbd_testdescription")).replaceAll("'", "''")+"',cbd_testman='"+map.get("cbd_testman")+"',cbd_name='"+map.get("cbd_name")+"',cbd_code='"+code+"',cbd_handman='"+map.get("cbd_handman")+"',cbd_handmanid='"+map.get("cbd_handmanid")+"',cbd_cldid='"+cbd_cldid+"'", "cbd_id="+map.get("cbd_id"));
		}else {
			map.remove("cbd_status");
			map.remove("cbd_statuscode");
			map.put("cbd_statuscode","HANDED");
			map.put("cbd_status",BaseUtil.getLocalMessage("HANDED", language));
			baseDao.updateByCondition("CheckListDetail", "cld_status='已处理'", "cld_cbdid='"+map.get("cbd_id")+"'");
			baseDao.updateByCondition("CHECKHISTORY", "ch_cbdstatus='已处理'", "ch_cbdstatus<>'null' and ch_cbdid='"+map.get("cbd_id")+"'");
			baseDao.updateByCondition("CHECKLISTBASEdetail", "cbd_status='"+BaseUtil.getLocalMessage("HANDED", language)+"',cbd_statuscode='HANDED',cbd_result='"+map.get("cbd_result")+"',cbd_problemgrade='"+map.get("cbd_problemgrade")+"',cbd_problemrate='"+map.get("cbd_problemrate")+"',cbd_testdescription='"+String.valueOf(map.get("cbd_testdescription")).replaceAll("'", "''")+"',cbd_testman='"+map.get("cbd_testman")+"',cbd_name='"+map.get("cbd_name")+"',cbd_code='"+code+"',cbd_handman='"+map.get("cbd_handman")+"',cbd_handmanid='"+map.get("cbd_handmanid")+"',cbd_cldid='"+cbd_cldid+"'", "cbd_id="+map.get("cbd_id"));
		}
		gridSqls = "Insert into CHECKHISTORY (CH_ID,CH_CBDID,CH_DETNO,CH_RESULT,CH_TESTMAN,CH_TESTDATE,CH_TESTDESCRIPTION,CH_CBDSTATUS,CH_CLDID,CH_CBDCODE)"
				+ " values ("+id+","+map.get("cbd_id")+",1,'"+map.get("cbd_result")+"','"+employee.getEm_name()+"',"+DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date())+",'"+String.valueOf(map.get("cbd_testdescription")).replaceAll("'", "''")+"','"+ch_cldstatus+"','"+ch_cldid+"','"+ch_cbdcode+"')";
		map.remove("cb_prjid");
		map.remove("cb_prjcode");
		map.remove("cb_prjname");
		map.remove("cb_remark");
		baseDao.execute(sqls);
		baseDao.execute(gridSqls);  	
		baseDao.logMessage(new MessageLog(employee.getEm_name(), "提交操作", "提交成功!", "CheckListBaseDetail|cbd_id="+map.get("cbd_id")));
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void resSubmitCheckBase(int id) {
		String language=SystemSession.getLang();
		Employee employee =SystemSession.getUser();
		//更新状态为待测试
		baseDao.updateByCondition("CheckListBaseDetail","cbd_statuscode='TESTING',cbd_status='"+BaseUtil.getLocalMessage("TESTING", language)+"',cbd_result=null","cbd_id="+id);
		baseDao.deleteByCondition("CheckListDetail","cld_cbdid="+id);
		baseDao.logMessage(new MessageLog(employee.getEm_name(), "反提交操作", "反提交成功!", "CheckListBaseDetail|cbd_id="+id));
	}
	public  String nextDate(){
		Calendar calendar = Calendar.getInstance();	
		calendar.setTimeInMillis(new Date().getTime());
		calendar.add(Calendar.DATE, 1);    //下一天
		calendar.setTimeInMillis(calendar.getTime().getTime());
		return DateUtil.parseDateToOracleString(Constant.YMD_HMS, calendar.getTime());
	}
	/**
	 *  新增生成BUG按钮
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void checkBaseToBug(String formStore) {
	Map<Object,Object> map=BaseUtil.parseFormStoreToMap(formStore);
	StringBuffer sb=new StringBuffer();
	List<String>sqls=new ArrayList<String>();
	Employee employee=SystemSession.getUser();
	String language=SystemSession.getLang();
	Object cbd_testdescription = baseDao.getFieldDataByCondition("CheckListBaseDetail", "cbd_testdescription", "cbd_id='"+map.get("cbd_id")+"'");
	int ch_cldid=0; //测试历史关联BUG单的ID
	int cbd_cldid=0; //测试单关联BUG单的ID
	//生成Bug单
	//是否有转单配置
	String dets = baseDao.
			getJdbcTemplate().
			queryForObject("select wm_concat(td_tofield) from transferdetail left join transfers on td_trid = tr_id where tr_caller = 'CheckListBaseDetail!ToCheck'",String.class);
	//当前日期
	String date=DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date());
	Object cl_id=baseDao.getFieldDataByCondition("CheckList ", "cl_id","cl_cbid="+map.get("cbd_cbid"));
	int cld_id=baseDao.getSeqId("CHECKLISTDETAIL_SEQ");
	ch_cldid = cld_id;
	cbd_cldid = cld_id;
	int detno = 1;
	if(cl_id!=null){//已经生成bug单主记录，取序号
		SqlRowList sl=baseDao.queryForRowSet("select max(cld_detno) from checkListDetail where cld_clid="+cl_id);				
		if(sl.next()){
			detno=sl.getInt(1)==-1?0:sl.getInt(1);
			detno = detno+1;
		}	
	}else{//没有生成bug单主记录，插入主记录
		cl_id=baseDao.getSeqId("CheckList_SEQ");
		Object prjid=map.get("cb_prjid")!=null && !map.get("cb_prjid").equals("")?map.get("cb_prjid"):0,prjcode=map.get("cb_prjcode")!=null && !map.get("cb_prjcode").equals("")?map.get("cb_prjcode"):0;
		sb.append("insert into CheckList (cl_id,cl_code,cl_status,cl_statuscode,cl_recorder,cl_recorderid,cl_recorddate,cl_prjplanid,cl_prjplancode,cl_prjplanname,cl_pmman,cl_pmmancode,cl_cbid,cl_description)  values("+cl_id+",'BL_"+baseDao.sGetMaxNumber("CheckList",2)+"','"+BaseUtil.getLocalMessage("AUDITED", language)+"','AUDITED','"+employee.getEm_name()+"',"+employee.getEm_id()+","+date+",");
		sb.append(prjid+",'"+prjcode+"','"+map.get("cb_prjname")+"','"+employee.getEm_name()+"','"+employee.getEm_code()+"',"+map.get("cbd_cbid")+",'"+map.get("cb_remark")+"')");
		sqls.add(sb.toString());
		sb.setLength(0);
	}
	if (dets!=null) {
		//有转单配置使用转单配置
		Key key = transferRepository.transfer("CheckListBaseDetail!ToCheck", map.get("cbd_id"));
		cld_id = key.getId();
		ch_cldid = cld_id;
		cbd_cldid = cld_id;
		sb.append("update CheckListDetail set cld_clid="+cl_id+",cld_detno='"+detno+"',cld_testresult=0,cld_newtestman='"+employee.getEm_name()+"',cld_newtestdate="+date+",cld_newtestmanid="+employee.getEm_id()+",cld_exhibitor='"+employee.getEm_name()+"',cld_exhibitdate="+date+",cld_exhibitorid='"+employee.getEm_id()+"',cld_needlevel='"+map.get("cbd_problemgrade")+"',cld_problemrate='"+map.get("cbd_problemrate")+"',cld_testdescription='"+cbd_testdescription+"',cld_isconfirm='0',cld_newhandman='"+map.get("cbd_handman")+"',cld_newhandmanid='"+(map.get("cbd_handmanid")==null?0:map.get("cbd_handmanid"))+"' where cld_id = "+cld_id);
		sqls.add(sb.toString());
	}else{
		//没有转单配置直接插入
		sb.append("insert into CheckListDetail (cld_id,cld_clid,cld_cbdid,cld_detno,cld_testresult,cld_testdescription,cld_newtestman,cld_newtestdate,cld_newtestmanid,cld_newhandman,cld_newhandmanid,cld_name,cld_exhibitor,cld_exhibitdate,cld_exhibitorid,cld_sourcecode,cld_sourceid,cld_testman2,cld_needlevel,cld_problemrate,cld_isconfirm)values(");
		sb.append(cld_id+","+cl_id+","+map.get("cbd_id")+",'"+detno+"',0,'"+cbd_testdescription+"','"+employee.getEm_name()+"',"+date+","+employee.getEm_id()+",'"+map.get("cbd_handman")+"','"+map.get("cbd_handmanid")+"','"+map.get("cbd_name")+"','"+employee.getEm_name()+"',"+date+",'"+employee.getEm_id()+"','"+map.get("cbd_code")+"',"+map.get("cbd_id")+",'"+map.get("cbd_testman2")+"','"+map.get("cbd_problemgrade")+"','"+map.get("cbd_problemrate")+"','0')");
		sqls.add(sb.toString());
	}	
	//下面是记录BUG单明细行内容，并在提交的时候向处理人发送BUG测试不通过的信息
	int ch_id=baseDao.getSeqId("CHECKTABLE_SEQ");
	baseDao.execute("insert into checktable(ch_cldid,ch_description,ch_recorddate,ch_id,ch_type,ch_recorder,ch_detno) values("+cld_id+",'"+cbd_testdescription+"',sysdate,"+ch_id+",'Test','"+employee.getEm_name()+"',"+ch_id+")");
	int pr_id=baseDao.getSeqId("PAGINGRELEASE_SEQ");
	int prd_id=baseDao.getSeqId("PAGINGRELEASEDETAIL_SEQ");
	sb.setLength(0); 
	/*sb.append("任务提醒&nbsp;&nbsp;&nbsp;&nbsp;["+DateUtil.parseDateToString(DateUtil.parseStringToDate(null, "yyyy-MM-dd HH:mm:ss"), "MM-dd HH:mm")+"]</br>");
	sb.append("你有处理的BUG测试未通过!该单据名称为:&nbsp;&nbsp;");
	sb.append("<a href=\"javascript:openGridUrl(" + cld_id
			+ ",''cld_id'',''ch_cldid'',''jsps/plm/test/check.jsp'',''Check单''" + ")\" style=\"font-size:14px; color:blue;\">" + map.get("cbd_name") + "</a></br>");*/
	sb.append("("+employee.getEm_name()+")提交了新的BUG给您！");
	sqls.add("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context,pr_from)values('"+pr_id+"','"+employee.getEm_name()+"',"+DateUtil.parseDateToOracleString(Constant.YMD, new Date())+",'"+employee.getEm_id()+"','"+sb.toString()+"','task')");
	sqls.add("insert into pagingreleasedetail(prd_id,prd_prid,PRD_RECIPIENTID,PRD_RECIPIENT) values('"+prd_id+"','"+pr_id+"','"+map.get("cbd_handmanid")+"','"+map.get("cbd_handman")+"')");
	//保存到历史消息表
	int IH_ID=baseDao.getSeqId("ICQHISTORY_SEQ");
	sqls.add("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
			+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
			+ " where pr_id="+pr_id);
	sqls.add("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
			+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+pr_id+"and ("+IH_ID+",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");
	map.remove("cbd_status");
	map.remove("cbd_statuscode");
	map.put("cbd_statuscode","PENDING");
	map.put("cbd_status",BaseUtil.getLocalMessage("PENDING", language));
	baseDao.updateByCondition("CHECKHISTORY", "CH_CBDCODE='"+map.get("cbd_name")+"',CH_CBDSTATUS='待处理',CH_CLDID='"+ch_cldid+"'", "CH_CBDID='"+map.get("cbd_id")+"' and ch_detno='1'");
	baseDao.updateByCondition("CHECKLISTBASEdetail", "cbd_result='"+map.get("cbd_result")+"',cbd_cldid='"+cbd_cldid+"'", "cbd_id="+map.get("cbd_id"));
	baseDao.execute("update CheckListDetail set cld_enclosure=(select cbd_enclosure from CheckListBaseDetail where cbd_id='"+map.get("cbd_id")+"') where cld_cbdid='"+map.get("cbd_id")+"'");
	baseDao.execute(sqls);
	}
}
