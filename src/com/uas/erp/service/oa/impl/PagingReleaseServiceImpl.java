package com.uas.erp.service.oa.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.listener.UserOnlineListener;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.model.PagingRelease;
import com.uas.erp.model.UserSession;
import com.uas.erp.service.oa.PagingReleaseService;

@Service
public class PagingReleaseServiceImpl implements PagingReleaseService {

	@Autowired
	private BaseDao baseDao;

	@Override
	public void save(String formStore, Employee employee) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String[] rece = BaseUtil.parseStr2Array(store.get("prd_recipient").toString(), ";");
		String[] receId = BaseUtil.parseStr2Array(store.get("prd_recipientid").toString(), ";");
		String[] mobile = new String[receId.length];
		if (store.get("prd_mobile") == null) {
			for (int i = 0; i < receId.length; i++) {
				mobile[i] = baseDao.getFieldValue("Employee", "em_mobile", "em_id=" + receId[i], String.class);
			}
		} else {
			mobile = BaseUtil.parseStr2Array(store.get("prd_mobile").toString(), ";");
		}
		store.remove("prd_recipient");
		store.remove("prd_recipientid");
		store.remove("prd_mobile");
		if (store.get("pr_id") == null) {
			store.put("pr_id", baseDao.getSeqId("PAGINGRELEASE_SEQ"));
		}
		store.put("pr_releaser", employee.getEm_name());
		store.put("pr_releaserid", employee.getEm_id());
		if (store.get("pr_date") == null) {
			store.put("pr_date", DateUtil.currentDateString(Constant.YMD_HMS));
		}
		store.put("pr_status", -1);
		String sql = SqlUtil.getInsertSqlByFormStore(store, "PagingRelease", new String[] {}, new String[] {});
		baseDao.execute(sql);
		List<Map<Object, Object>> grid = new ArrayList<Map<Object, Object>>();
		Map<Object, Object> m = null;
		for (int i = 0; i < rece.length; i++) {
			m = new HashMap<Object, Object>();
			m.put("prd_recipient", rece[i]);
			m.put("prd_recipientid", receId[i]);
			m.put("prd_mobile", mobile[i]);
			m.put("prd_id", baseDao.getSeqId("PAGINGRELEASEDETAIL_SEQ"));
			m.put("prd_prid", store.get("pr_id"));
			m.put("prd_status", -1);
			grid.add(m);
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(grid, "PagingReleaseDetail"));
		
		//保存到历史消息表
		int IH_ID=baseDao.getSeqId("ICQHISTORY_SEQ");
		baseDao.execute("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
				+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
				+ " where pr_id="+store.get("pr_id"));
		baseDao.execute("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
				+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+store.get("pr_id"));
	}

	@Override
	public void updateStatus(int id, int status, String master) {
		String updatetable = "ICQHISTORYdetail";
		if (master != null && !master.equals(""))
			updatetable = master + ".ICQHISTORYdetail";
		baseDao.updateByCondition(updatetable, "ihd_readstatus=-1,ihd_readtime="+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date()), "ihd_id=" + id);
	}

	@Override
	public List<UserSession> getOnlineEmployeeByOrg(int orgid) {
		Set<UserSession> users = UserOnlineListener.getOnLineList();
		if (users != null) {
			List<UserSession> us = new ArrayList<UserSession>();
			for (UserSession u : users) {
				if (u.getEm_orgid() == orgid) {
					us.add(u);
				}
			}
			return us;
		}
		return null;
	}

	@Override
	public String getPaging(Employee employee) {
		String masters = employee.getEm_masters();
		String querySql = "select * from (select pr_releaser,pr_releaserid,pr_date,pr_context,prd_id,pr_id,pr_istop from PagingRelease left join PagingReleaseDetail on pr_id=prd_prid where prd_recipientid=? AND prd_status=-1 AND pr_date<sysdate order by pr_date asc) where rownum<=5";
		List<String> sqls = new ArrayList<String>();
		Object[] args = null;
/*		if (BaseUtil.isGroup() && masters != null && masters.length() > 0) {
			StringBuffer sb = new StringBuffer();
			sb.append("select * from (");
			String ms[] = masters.split(",");
			args = new Object[ms.length];
			for (int i = 0; i < ms.length; i++) {
				if (i > 0) {
					sb.append(" UNION ALL ");
				}
				sb.append("select '" + ms[i] + "'  CURRENTMASTER,pr_releaser,pr_releaserid,pr_date,pr_context,prd_id,pr_id,pr_istop from "
						+ ms[i] + ".PagingRelease left join " + ms[i]
						+ ".PagingReleaseDetail on pr_id=prd_prid where prd_recipientid=? AND prd_status=-1 AND pr_date<sysdate ");
				args[i] = employee.getEm_id();
			}
			sb.append(" order by pr_date asc) where rownum<=5");
			querySql = sb.toString();
		} else {
			args = new Object[] { employee.getEm_id() };
		}*/
		args = new Object[] { employee.getEm_id() };
		SqlRowList rs = baseDao.queryForRowSet(querySql, args);
		JSONArray arr = new JSONArray();
		JSONObject d;
		while (rs.next()) {
			d = new JSONObject();
			d.put("pr_releaser", rs.getObject("pr_releaser"));
			d.put("pr_releaserid", rs.getObject("pr_releaserid"));
			d.put("pr_date", rs.getGeneralTimestamp("pr_date"));
			d.put("pr_context", rs.getObject("pr_context"));
			d.put("prd_id", rs.getObject("prd_id"));
			d.put("pr_id", rs.getObject("pr_id"));
			d.put("pr_istop", rs.getObject("pr_istop"));
/*			if ("true".equals(isGroup) && masters != null && masters.length() > 0) {
				d.put("currentMaster", rs.getObject("CURRENTMASTER"));
				sqls.add("UPDATE " + rs.getObject("CURRENTMASTER") + ".PagingReleaseDetail SET PRD_STATUS=-2 WHERE PRD_ID="
						+ rs.getObject("prd_id"));
			} else {
				sqls.add("UPDATE PagingReleaseDetail SET PRD_STATUS=-2 where prd_id=" + rs.getObject("prd_id"));
			}*/
			sqls.add("UPDATE PagingReleaseDetail SET PRD_STATUS=-2 where prd_id=" + rs.getObject("prd_id"));
			arr.add(d);
		}
		baseDao.execute(sqls);
		return arr.toString();
	}

	@Transactional
	public String turnToHoitory() {
		// 超过30天的未阅寻呼直接转移到寻呼历史表
		/*baseDao.execute("update pagingreleasedetail set prd_status=0 where exists (select 1 from pagingrelease where pr_date < sysdate - 30 and pr_id=prd_prid) and prd_status=-1");
		baseDao.execute("INSERT INTO ICQHistoryDetail(ihd_id,ihd_ihid,ihd_receive,ihd_receiveid,ihd_mobile) select PRD_ID,PRD_PRID,PRD_RECIPIENT,PRD_RECIPIENTID,PRD_MOBILE from PagingReleaseDetail where prd_status<>-1");
		baseDao.deleteByCondition("PagingReleaseDetail", "prd_status<>-1");
		baseDao.execute("INSERT INTO ICQHistory(ih_id,ih_attach,ih_call,ih_callid,ih_date,ih_context,ih_from,ih_codevalue,ih_caller,ih_title) select pr_id,pr_attach,pr_releaser,pr_releaserid,pr_date,pr_context,pr_from,pr_codevalue,pr_caller,pr_title from PagingRelease where (SELECT count(1) from PagingReleaseDetail where prd_prid=pr_id and prd_status=-1)=0");
		baseDao.deleteByCondition("PagingRelease", "(SELECT count(1) from PagingReleaseDetail where prd_prid=pr_id and prd_status=-1)=0");*/
		return "信息已转移!";
	}

	@Override
	public String confirmNotifyJprocess(int id, String source) {
		baseDao.updateByCondition("pagingreleasedetail", "prd_readstatus = -1", "prd_id='" + id + "'");
		baseDao.updateByCondition("ICQHistoryDetail", "ihd_readstatus = -1,ihd_readtime="+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date()), "ihd_id='" + id + "'");
		return "修改状态成功!";
	}

	@Override
	public List<Map<String, Object>> getUsersIsOnline() {
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		SqlRowList sl = baseDao
				.queryForRowSet("select em_id,em_code,em_name,em_sex,em_position,em_defaultorname,em_depart,0 isonline from employee where nvl(em_class,' ')<>'离职 ' order by em_id");
		Set<UserSession> users = UserOnlineListener.getOnLineList();
		Set<UserSession> us = new HashSet<UserSession>();
		us.addAll(users);
		while (sl.next()) {
			map = sl.getCurrentMap();
			for (UserSession u : us) {
				if (sl.getString(2).equals(u.getEm_code())) {
					map.put("ISONLINE", 1);
					us.remove(u);
					break;
				}
			}
			lists.add(map);
		}
		return lists;
	}

	@Override
	public void pagingRelease(String mans, String msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pagingRelease(String mans, String title, String context) {
		if (mans != null) {
			String sql = baseDao.getJdbcTemplate().queryForObject("select KPI.getQuerymanSql('" + mans + "') from dual", String.class);
			SqlRowList sl = baseDao.queryForRowSet(sql);
			int prId = baseDao.getSeqId("PAGINGRELEASE_SEQ");
			Employee releaser = SystemSession.getUser();
			List<String> sqls = new ArrayList<String>();
			baseDao.save(new PagingRelease(prId, title, context, releaser));
			while (sl.next()) {
				sqls.add("INSERT INTO PAGINGRELEASEDETAIL(PRD_PRID,PRD_RECIPIENTID,PRD_RECIPIENT) VALUES(" + prId + "," + sl.getObject(1)
						+ ",'" + sl.getObject(2) + "')");
			}
			baseDao.execute(sqls);
			//保存到历史消息表
			int IH_ID=baseDao.getSeqId("ICQHISTORY_SEQ");
			baseDao.execute("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
					+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
					+ " where pr_id="+prId);
			baseDao.execute("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
					+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+prId);
		}

	}

	@Override
	public void pagingRelease(String mans, String msg, String caller, String keyValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void paging(String mans, String title, String context,String type) {
		if (mans != null) {
			String sql = baseDao.getJdbcTemplate().queryForObject("select KPI.getQuerymanSql('" + mans + "') from dual", String.class);
			SqlRowList sl = baseDao.queryForRowSet(sql);
			int prId = baseDao.getSeqId("PAGINGRELEASE_SEQ");
			Employee releaser = SystemSession.getUser();
			List<String> sqls = new ArrayList<String>();
			baseDao.save(new PagingRelease(prId, title, context, releaser,type));
			while (sl.next()) {
				sqls.add("INSERT INTO PAGINGRELEASEDETAIL(PRD_PRID,PRD_RECIPIENTID,PRD_RECIPIENT) VALUES(" + prId + "," + sl.getObject(1)
						+ ",'" + sl.getObject(2) + "')");
			}
			baseDao.execute(sqls);
			//保存到历史消息表
			int IH_ID=baseDao.getSeqId("ICQHISTORY_SEQ");
			baseDao.execute("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
					+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
					+ " where pr_id="+prId);
			baseDao.execute("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
					+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+prId);
		}
	}

	@Override
	public String getPagingById(Integer id) {
		SqlRowList sl = baseDao
				.queryForRowSet(
						"select pr_id,pr_releaser,pr_releaserid,pr_date,pr_context,prd_id from Pagingrelease left join pagingreleasedetail on pr_id=prd_prid where prd_id=?",
						new Object[] { id });
		if (sl.next())
			return BaseUtil.parseMap2Str(sl.getCurrentMap());
		else
			return null;
	}

	@Override
	public void B2BMsg(String caller, String ids, String type) {
		try{
			//判断该操作是否设置了消息模板  
			   Object mmid=baseDao.getFieldDataByCondition("MESSAGEMODEL left join MESSAGEROLE on mm_id=mr_mmid", "distinct mm_id", "MR_ISUSED=-1 AND MM_ISUSED=-1 and mm_caller='"+caller+"' and MM_OPERATE='b2b' AND MM_ACTION='"+type+"'");
				//调用生成消息的存储过程
				if (mmid != null) {
					baseDao.callProcedure("SP_CREATEINFO",new Object[] { mmid,"ADMIN", ids,DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) });
				}			
        } catch(Exception e) {         
            System.out.println("Got a Exception：" + e.getMessage());
            e.printStackTrace();
        }		
	}
}