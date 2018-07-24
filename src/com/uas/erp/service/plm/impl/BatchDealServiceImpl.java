package com.uas.erp.service.plm.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.service.plm.BatchDealService;

@Service
public class BatchDealServiceImpl implements BatchDealService {
	
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	// 试产制造单批量结案
	@Override
	public String makeDeal(String data) {
		
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer message = new StringBuffer();
		String ids = GetMakeID(data);
		SqlRowList sqlRowList = baseDao.queryForRowSet("SELECT * FROM makematerial,make,product WHERE ma_id=mm_maid and pr_code=mm_prodcode  and ma_qty>0  and ma_kind not like '%返修%' and ma_id in (" +ids+
				") and mm_oneuseqty<>0 and NVL(mm_havegetqty,0)-NVL(mm_scrapqty,0)-mm_oneuseqty*NVL(ma_madeqty,0)>=1 and " +
				"(mm_oneuseqty<0 or (( ma_tasktype<>'OS' and floor((NVL(mm_havegetqty,0)-NVL(mm_scrapqty,0))/mm_oneuseqty)>ceil(NVL(ma_madeqty,0)) ) " +
				"or (ma_tasktype='OS' and NVL(mm_havegetqty,0)-NVL(mm_addqty,0)-NVL(mm_balance,0)-NVL(mm_scrapqty,0)>NVL(ma_madeqty,0)*mm_oneuseqty) ))");
		if(sqlRowList.next()){
			message.append("工单号为:"+sqlRowList.getString("ma_code")+",序号为:"+sqlRowList.getInt("mm_detno")+",料号为:"+sqlRowList.getString("mm_prodcode")+"有结存物料需要退料!");
		}
		if(message.length()==0){
			for(Map<Object, Object> s:store){
				baseDao.updateByCondition("make", "ma_statuscode='FINISH',ma_status='"+BaseUtil.getLocalMessage("FINISH")+"'", 
						" ma_id="+Integer.valueOf(String.valueOf(s.get("ma_id"))));
			}
			message.append("批量结案成功！");
		} else{
			BaseUtil.showError(message.toString());
		}
		return message.toString();
	}
	
	public String GetMakeID(String data){
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer ids = new StringBuffer();
		for(Map<Object, Object> s:store){
			ids.append(String.valueOf(s.get("ma_id")));
			ids.append(",");
		}
		return ids.substring(0,(ids.length()-1)).toString();
	}

	@Override
	public void salevastClose(String data) {
		try {
			List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
			for(Map<Object, Object> s:store){
				baseDao.updateByCondition("Sale", "sa_status='"+BaseUtil.getLocalMessage("FINISH")+"',sa_statuscode='FINISH'", "sa_id="+Integer.valueOf(String.valueOf(s.get("sa_id"))));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 批量确认BUG
	 */
	@Override
	public String batchTestBug(String caller,String data){
		List<Map<Object, Object>> Store = BaseUtil.parseGridStoreToMaps(data);
		for(Map<Object,Object >store : Store){
			if("".equals(store.get("cld_testresult")) || store.get("cld_testdescription")==null || "".equals(store.get("cld_testdescription"))){
				BaseUtil.showError("请选择测试结果、填写明细行的测试描述再进行测试回复");
			}
			Object[] checkList = baseDao.getFieldsDataByCondition("CheckListDetail left join checkList on cld_clid=cl_id left join checklistbasedetail on cld_cbdid=cbd_id", new String[] { "cld_status", "cld_statuscode", "cl_prjplanname","cl_prjplanid", "cld_newhandmanid", "cld_handdescription","cld_name","cld_cbdid","cld_newtestmanid","cld_newtestman"}, "cld_id="+store.get("cld_id"));
			store.put("cld_status", checkList[0]);
			store.put("cld_statuscode", checkList[1]);
			store.put("cl_prjplanname", checkList[2]);
			store.put("cl_prjplanid", checkList[3]);
			store.put("cld_newhandmanid", checkList[4]);
			store.put("cld_handresult", "-1");
			store.put("cld_name", checkList[6]);
			store.put("cld_cbdid", checkList[7]);
			store.put("cld_newtestmanid", checkList[8]);
			store.put("cld_newtestman", checkList[9]);
			List<String> sqls = new ArrayList<String>();
			String language = SystemSession.getLang();
			handlerService.handler("Check", "save", "before", new Object[] { store });
			store.remove("cld_status");
			store.remove("cld_statuscode");
			store.remove("cl_prjplanname");
			store.remove("cl_prjplanid");
			// 插入语句
			int ch_id = baseDao.getSeqId("CHECKTABLE_SEQ");
			String description = null;
			String type = null;
			String statuscode = null;
			String status = null;
			StringBuffer sb = new StringBuffer();
			Employee employee = SystemSession.getUser();
			if (Integer.parseInt(store.get("cld_newtestmanid").toString()) == employee.getEm_id()) {
				type = "Test";
				description = "cld_testdescription";
				if (store.get("cld_testresult").equals("-1")) {
					statuscode = "HANDED";
					store.put("cld_realenddate", DateUtil.parseDateToString(new Date(), Constant.YMD_HMS));
					baseDao.updateByCondition("CheckListBaseDetail", "cbd_status='"+BaseUtil.getLocalMessage("HANDED", language)+"',cbd_statuscode='HANDED',cbd_result='"+store.get("cld_testdescription")+"'", "cbd_name='"+store.get("cld_name")+"' and cbd_id="+store.get("cld_cbdid")+"");
					baseDao.updateByCondition("CHECKHISTORY", "ch_cbdstatus='"+BaseUtil.getLocalMessage("HANDED", language)+"'", "ch_cbdcode='"+store.get("cld_name")+"' and ch_cldid="+store.get("cld_id")+"");
				} else if(store.get("cld_testresult").equals("0")){
					statuscode = "PENDING";
					// 插入
				} else if(store.get("cld_testresult").equals("finish")){
					statuscode = "FINISH";
				}
				status = BaseUtil.getLocalMessage(statuscode);
				int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
				int prd_id = baseDao.getSeqId("PAGINGRELEASEDETAIL_SEQ");
				sb.append("("+employee.getEm_name()+")已回复了你处理的BUG！");
				sqls.add("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context)values('" + pr_id + "','"
						+ employee.getEm_name() + "'," + DateUtil.parseDateToOracleString(Constant.YMD, new Date()) + ",'"
						+ employee.getEm_id() + "','" + sb.toString() + "')");
				sqls.add("insert into pagingreleasedetail(prd_id,prd_prid,PRD_RECIPIENTID,PRD_RECIPIENT) values('" + prd_id + "','" + pr_id
						+ "','" + store.get("cld_newhandmanid") + "','" + store.get("cld_newhandman") + "')");
				int IH_ID=baseDao.getSeqId("ICQHISTORY_SEQ");
				sqls.add("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
						+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
						+ " where pr_id="+pr_id);
				sqls.add("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
						+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+pr_id+"and ("+IH_ID+",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");
			}
			store.put("cld_status", status);
			store.put("cld_statuscode", statuscode);
			String formSql = SqlUtil.getUpdateSqlByFormStore(store, "CheckListDetail", "cld_id");
			String insertSql = "insert into checktable(ch_id,ch_cldid,ch_recorder,ch_recorddate,ch_description,ch_type,ch_detno) values('"
					+ ch_id + "','" + store.get("cld_id") + "','" + employee.getEm_name() + "',"
					+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + ",'" + store.get(description) + "','" + type + "','"
					+ ch_id + "')";
			sqls.add(insertSql);
			sqls.add(formSql);
			baseDao.execute(sqls);
			baseDao.updateByCondition("CheckListDetail", "cld_newtestdate=sysdate", "cld_id="+store.get("cld_id"));
			baseDao.logger.update("Check", "cld_id", store.get("cld_id"));
			handlerService.handler("Check", "save", "after", new Object[] { store });
		}
		StringBuffer sp = new StringBuffer();
		sp.append("批量处理成功");
		return sp.toString();
	}
}
