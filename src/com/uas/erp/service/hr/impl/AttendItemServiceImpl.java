package com.uas.erp.service.hr.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.hr.AttendItemService;

@Service
public class AttendItemServiceImpl implements AttendItemService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveAttendItem(String formStore, String caller) {		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);		
		handlerService.beforeSave(caller, new Object[]{store});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "AttendItem", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "ai_id", store.get("ai_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller, new Object[]{store});

	}

	@Override
	public void updateAttendItemById(String formStore, String caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.handler("AttendItem", "save", "before", new Object[]{formStore});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "AttendItem", "ai_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "ai_id", store.get("ai_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});

	}

	@Override
	public void deleteAttendItem(int ai_id, String  caller) {
		handlerService.beforeDel(caller, new Object[]{ai_id});	
		//删除
		baseDao.deleteById("AttendItem", "ai_id", ai_id);
		//记录操作
		baseDao.logger.delete(caller, "ai_id", ai_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{ai_id});		
	}

	@Override
	public void attendDataCom(String emcode, String startdate, String enddate,
			String  caller) {
		String res = baseDao.callProcedure("SP_GETATTENDDATA", new Object[]{startdate,enddate,emcode});
		if(res.equals("OK")){
			String r = baseDao.callProcedure("SP_CACATTENDDATA", new Object[]{startdate,enddate,emcode});
			if(res.equals("OK")){
				
			}else{
				BaseUtil.showError(r);
			}
		}else{
			BaseUtil.showError(res);
		}
	}
	
	@Override
	public void cardLogImp(String cardcode, String startdate, String enddate,String yearmonth,
			String  caller) {
//		String card = new String()["malata_card1","malata_card2"];
		List<String> dbconnect = new ArrayList<String>();
		dbconnect.add("malata_card1");
//		dbconnect.add("malata_card2");
		String sob = SpObserver.getSp();
		for(String card : dbconnect){
			
			SpObserver.putSp(card);
			
			if (!card.equals(SpObserver.getSp())) {
				BaseUtil.showError("无法连接到万利达打卡数据库.");
			}
			String error = checkExist(cardcode,startdate,enddate,yearmonth);
			if (error != null) {
				SpObserver.putSp(sob);
				BaseUtil.showError(error);
			}
			boolean b = !cardcode.equals("");
			String condition = "";
			String deCond = "";
			if(b){
				condition += " card = '"+cardcode.trim()+"'" + " and rq >= '"+startdate.substring(8, 10)+"' and rq <= '"+enddate.substring(8, 10)+"'";
				deCond += " and cl_cardcode ='"+cardcode.trim()+"'";
			} else {
				condition += " rq >= '"+startdate.substring(8, 10)+"' and rq <= '"+enddate.substring(8, 10)+"'";
			}
			
			String sql = "select * from kq"+yearmonth.substring(2)+"source where  " + condition;
			SqlRowList r = baseDao.queryForRowSet(sql);
			SpObserver.putSp(sob);
			baseDao.execute("delete from cardlog where cl_status<>'HAND' and cl_time>=to_date('"+startdate+" 00:00:00','yyyy-mm-dd hh24:mi:ss') and cl_time<=to_date('"+enddate+" 23:59:59','yyyy-mm-dd hh24:mi:ss')"+ deCond);
			while(r.next()){
				baseDao.execute("insert into cardlog (cl_id,cl_cardcode,cl_time) values(cardlog_seq.nextval,'"+r.getString("card")+"',to_date('"+yearmonth+r.getString("rq")+" "+r.getString("sktime")+"','yyyymmdd hh24:mi'))");
			}
			
			baseDao.execute("update cardlog set cl_cardcode=substr(cl_cardcode,3,10) where cl_cardcode like '00%'");
			baseDao.execute("update cardlog set cl_cardcode=substr(cl_cardcode,2,10) where cl_cardcode like '0%'");
			baseDao.execute("UPDATE CARDLOG SET (cl_emid,cl_emcode)=(select em_id,em_code from employee where em_cardcode=cl_cardcode)");
//			UPDATE CARDLOG SET (cl_emid,cl_emcode)=(select em_id,em_code from employee where em_cardcode=cl_cardcode)
			
			
		}

		
	}
	/**
	 * 检查HK帐套是否已经存在关联的单据
	 */
	private String checkExist(String cardcode,String startdate,String enddate,String yearmonth) {
//		String condition = (cardcode!=null&&!cardcode.equals(""))?"card='"+cardcode+"'":"1=1" +
//				" and rq >= '"+startdate.substring(8, 10)+"' and rq <= '"+enddate.substring(8, 10)+"'";
		boolean b = !cardcode.equals("");
		String condition = "";
		if(b){
			condition += " card = '"+cardcode.trim()+"'" + " and rq >= '"+startdate.substring(8, 10)+"' and rq <= '"+enddate.substring(8, 10)+"'";
		} else {
			condition += " rq >= '"+startdate.substring(8, 10)+"' and rq <= '"+enddate.substring(8, 10)+"'";
		}
		
		Object existCode = baseDao.getFieldDataByCondition("kq"+yearmonth.substring(2)+"source", "card",
				condition);

		if (existCode == null) {
			return "打卡数据库中没有筛选数据";
		}
		return null;
	}

}
