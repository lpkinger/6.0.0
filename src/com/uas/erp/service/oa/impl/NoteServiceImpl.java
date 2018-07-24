package com.uas.erp.service.oa.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.JSONUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.NoteDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.oa.NoteService;

@Service
public class NoteServiceImpl implements NoteService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private NoteDao noteDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveNote(String formStore, String  caller) {
		/*
		 * 反馈编号：2017070930 ， 3、不需要把 '\' 替换成'%' ， 把后台解析Json格式的JSONUtil.toMap()改成BaseUtil.parseFormStoreToMap()解决html格式问题
		*/
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String html = "<div class=\"note-default\">" +store.get("no_content").toString()+ "</div>";
		store.remove("no_content");
		/*List<String> sqls = new ArrayList<String>();
		if (BaseUtil.isGroup()) {
			Master master = SystemSession.getUser().getCurrentMaster();
			String masoncode = master.getMa_soncode();
			if (master != null && master.getMa_type() != 3 && masoncode != null) {
				String title = String.valueOf(store.get("no_title"));
				store.put("no_title", title + "_" + master.getMa_function());
				for (String sob : masoncode.split(",")) {
					store.put("no_id", baseDao.getSeqId(sob + ".NOTE_SEQ"));
					sqls.add(SqlUtil.getInsertSqlByMap(store, sob + ".note"));
				}
				// 如果是集团中心需要插入到集团中心 其他虚拟集团不用
				if (master.getMa_name().equals(BaseUtil.getXmlSetting("defaultSob"))) {
					sqls.add(SqlUtil.getInsertSqlByFormStore(store, "note", new String[] { "no_title" },
							new Object[] { title }));
				}
				baseDao.execute(sqls);
			} else
				baseDao.execute(SqlUtil.getInsertSqlByMap(store, "note"));
		} else*/
			baseDao.execute(SqlUtil.getInsertSqlByMap(store, "note"));
		
		baseDao.saveClob("Note", "no_content", html,
				"no_id=" + store.get("no_id"));
		
		if(store.get("no_ispublic")!=null && store.get("no_ispublic").equals("0") && !store.get("no_recipientid").equals("")){
			String sql1="insert into EMPSNOTES(emp_id,no_id)select em_id,"+store.get("no_id")+" from(";
			String sql=baseDao.getJdbcTemplate().queryForObject("select KPI.getQuerymanSql('" + store.get("no_recipientid") + "') from dual", String.class);
				baseDao.execute(sql1+sql+")");
		}
		sendPaging(store.get("no_id"),store.get("no_title"),store.get("no_infotype"),(store.get("no_ispublic")!=null && !store.get("no_ispublic").equals("0")));
	}

	@Override
	public void updateNote(String formStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[]{store});	
		String html = "<div class=\"note-default\">" +store.get("no_content").toString()+ "</div>";
		store.remove("no_content");
		//保存
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "NOTE","no_id");
		baseDao.execute(formSql);
		baseDao.saveClob("Note", "no_content", html,
				"no_id=" + store.get("no_id"));
		baseDao.deleteById("EMPSNOTES", "no_id", Integer.parseInt(String.valueOf(store.get("no_id"))));
		if(store.get("no_ispublic")!=null && store.get("no_ispublic").equals("0") && !store.get("no_recipientid").equals("")){
			String sql1="insert into EMPSNOTES(emp_id,no_id)select em_id,"+store.get("no_id")+" from(";
			String sql=baseDao.getJdbcTemplate().queryForObject("select KPI.getQuerymanSql('" + store.get("no_recipientid") + "') from dual", String.class);
				baseDao.execute(sql1+sql+")");
		}		
		try{
			//记录操作
			baseDao.logger.update(caller, "no_id", store.get("no_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterUpdate(caller, new Object[]{store});	
		sendPaging(store.get("no_id"),store.get("no_title"),store.get("no_infotype"),(store.get("no_ispublic")!=null && !store.get("no_ispublic").equals("0")));
	}

	@Override
	public void deleteNote(int no_id, String  caller) {
		handlerService.beforeDel(caller,new Object[]{no_id});
		baseDao.deleteById("Note", "no_id", no_id);
		baseDao.deleteById("EMPSNOTES", "no_id", no_id);
		// 记录操作
		baseDao.logger.delete(caller, "no_id", no_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel("Note", new Object[] { no_id});

	}

	@Override
	public Map<String, Object> getNote(int id, String  caller) {
		boolean bool=baseDao.checkIf("readstatus","sourcekind='note' and mainid="+id+" and man="+SystemSession.getUser().getEm_id());
		if(!bool)baseDao.logger.read("note",id);
		return noteDao.getNote(id );
	}

	@Override
	public String saveReadStatus() {
			/*sysdate会计算分钟数，而选择日期时默认的是12点，如果开始日期和结束日期是一天则会出现查找为空的情况*/	
			int manid=SystemSession.getUser().getEm_id();
			Object ob[]=baseDao.getFieldsDataByCondition("note", new String[]{"no_isrepeat","no_id"}, "NO_INFOTYPE='Major' and sysdate>=NO_BEGINTIME and to_date(to_char(sysdate,'yyyy-mm-dd'),'yyyy-mm-dd')<=NO_ENDTIME");	
			/*不存在的时候取值为空*/
			if(ob[0]!=null){
				int mainid=Integer.parseInt(ob[1].toString());
				boolean bool=baseDao.checkIf("readstatus", "man="+manid+" and mainid="+mainid);
				if(bool){
					if(ob[0].toString().equals("-1")){
						return "readed_but_repeate";
					}else{
						return "readed";
					}		
				}else{
					baseDao.execute("insert into readstatus (status,man,mainid,sourcekind) values(0,'"+manid+"','"+mainid+"','note')");
					return "unread";
				}
			}
			return null;
	}

    private void sendPaging(Object id,Object title,Object type,boolean ispublic){
    	Employee employee=SystemSession.getUser();
    	StringBuffer sb=new StringBuffer();
    	sb.append(employee.getEm_name()+"发布了");
    	sb.append("<a href=\"javascript:parent.openUrl(''jsps/oa/info/NoteR.jsp?whoami=Note&formCondition=no_idIS"+id+"&_noc=1'')\">"+title+"</a>");
    	sb.append("GG".equals(type)?"公告!":"通知!");
    	String tab=ispublic?" employee ":"  EMPSNOTES left join employee on emp_id=em_id ";   	
    	List<String> sqls = new ArrayList<String>();
    	Object[] ids=baseDao.getFieldsDataByCondition("ICQHISTORY", "IH_ID,IH_PRID", "IH_FROM='note' and IH_CALLER='Note' and IH_KEYVALUE="+id);
		int pr_id=(ids!=null && ids[0]!=null && ids[1]!=null)?Integer.parseInt(ids[1].toString()):baseDao.getSeqId("PAGINGRELEASE_SEQ");
		int IH_ID=(ids!=null && ids[0]!=null && ids[1]!=null)?Integer.parseInt(ids[0].toString()):baseDao.getSeqId("ICQHISTORY_SEQ");    	
			
		sqls.add("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context,pr_from,pr_title,pr_codevalue,pr_keyvalue,pr_caller,pr_attach)select " + pr_id + ",'"+ employee.getEm_name() + "'," + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + ",'"+ employee.getEm_id() + "','" + sb.toString() + "','note','通知公告',"+id+","+id+",'Note' ,no_attachs from note where no_id='"+id+"' and "
				+ "not exists (select 1 from pagingrelease where pr_id="+pr_id+" and pr_from='note' and pr_keyvalue="+id+")");

		sqls.add("insert into pagingreleasedetail(prd_id,prd_prid,PRD_RECIPIENTID,PRD_RECIPIENT) select pagingreleasedetail_seq.nextval,"+pr_id+",em_id,em_name from (select distinct em_id,em_name from "+tab+" where nvl(em_class,' ')<>'离职' and em_id<>"+employee.getEm_id()+" and ("+pr_id+",em_id,em_name) not in (select prd_prid,PRD_RECIPIENTID,PRD_RECIPIENT from pagingreleasedetail where prd_prid="+pr_id+"))");
			
		sqls.add("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
				+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE "
				+ " where pr_id="+pr_id+" and not exists (select 1 from ICQHISTORY where IH_PRID="+pr_id+" and ih_id="+IH_ID+" AND IH_FROM='note' and IH_KEYVALUE="+id+")");							
		sqls.add("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
					+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+pr_id+"and ("+IH_ID+",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail where ihd_ihid="+IH_ID+")");   	    	
		baseDao.execute(sqls);
    }
}
