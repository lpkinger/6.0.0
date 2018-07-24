package com.uas.erp.service.hr.impl;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.hr.EmpWorkDateChangeService;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: USOFTPC30
 * Date: 13-6-17
 * Time: 上午9:26
 * To change this template use File | Settings | File Templates.
 */
@Service
public class EmpWorkDateChangeServiceImpl implements EmpWorkDateChangeService {

    @Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

    @Override
    public void saveEmpWorkDateChange(String formStore, String gridStore, String  caller) {
        Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
        handlerService.beforeSave(caller, new Object[]{store});
        // edc_emnames,edc_emids是clob字段，特殊处理
     	String edc_emnames = store.get("edc_emnames").toString();
     	store.remove("edc_emnames");
     	String edc_emids = store.get("edc_emids").toString();
     	store.remove("edc_emids");
        String formSql = SqlUtil.getInsertSqlByFormStore(store, "empWorkDateChange", new String[]{}, new Object[]{});
        baseDao.execute(formSql);
        baseDao.saveClob("empWorkDateChange", "edc_emnames", edc_emnames,"edc_id=" + store.get("edc_id"));
		baseDao.saveClob("empWorkDateChange", "edc_emids", edc_emids,"edc_id=" + store.get("edc_id"));
        try{
            //记录操作
            baseDao.logger.save(caller, "edc_id", store.get("edc_id"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //执行保存后的其它逻辑
        handlerService.afterSave(caller, new Object[]{store});
    }

    @Override
    public void updateEmpWorkDateChangeById(String formStore, String gridStore, String  caller) {
        Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeUpdate(caller, new Object[]{store});
		// edc_emnames,edc_emids是clob字段，特殊处理
		String edc_emnames = store.get("edc_emnames").toString();
     	store.remove("edc_emnames");
     	String edc_emids = store.get("edc_emids").toString();
     	store.remove("edc_emids");
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "empWorkDateChange", "edc_id");
		baseDao.execute(formSql);
		baseDao.saveClob("empWorkDateChange", "edc_emnames", edc_emnames,"edc_id=" + store.get("edc_id"));
		baseDao.saveClob("empWorkDateChange", "edc_emids", edc_emids,"edc_id=" + store.get("edc_id"));	       
		//记录操作
		baseDao.logger.update(caller, "edc_id", store.get("edc_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
    }

    @Override
    public void deleteEmpWorkDateChange(int edc_id, String  caller) {
        //执行删除前的其它逻辑
    	handlerService.beforeDel(caller, new Object[]{edc_id});
		//删除empWorkDateChange
		baseDao.deleteById("empWorkDateChange", "edc_id", edc_id);
		//删除empWorkDateChangeDetail
		baseDao.deleteById("empWorkDateChangeDetail", "edcd_edcid", edc_id);
		//记录操作
		baseDao.logger.delete(caller, "edc_id", edc_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{edc_id});
    }

	@Override
	public void submitEmpWorkDateChange(int  id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("empWorkDateChange","edc_statuscode", "edc_id=" +  id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { id });
		// 执行提交操作
		baseDao.submit("empWorkDateChange", "edc_id=" +  id, "edc_status","edc_statuscode");
		Object emids = baseDao.getFieldDataByCondition("empWorkDateChange","edc_emids", "edc_id=" +  id);
		insertAllEmps(emids,  id);
		// 记录操作
		baseDao.logger.submit(caller, "edc_id", id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] {  id });
	}

	@Override
	public void resSubmitEmpWorkDateChange(int  id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("empWorkDateChange","edc_statuscode", "edc_id=" +  id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[]{ id});
		// 执行反提交操作
		baseDao.resOperate("empWorkDateChange", "edc_id=" + id, "edc_status","edc_statuscode");
		baseDao.deleteById("empWorkDateChangeDetail", "edcd_edcid",  id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "edc_id",  id);
		handlerService.handler(caller, "resCommit", "after", new Object[]{ id});
	}

	@Override
	public void auditEmpWorkDateChange(int  id, String caller) throws ParseException {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("empWorkDateChange","edc_statuscode", "edc_id=" +  id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] {  id });
		// 执行审核操作
		baseDao.audit("empWorkDateChange", "edc_id=" + id, "edc_status","edc_statuscode","edc_auditdate", "edc_auditer");
		SqlRowList r=baseDao.queryForRowSet("select edc_wdcode, TO_DATE(TO_CHAR(EDC_BEGINDATE,'yyyy-mm-dd'),'yyyy-mm-dd') begin ,TO_DATE(TO_CHAR(EDC_ENDDATE,'yyyy-mm-dd'),'yyyy-mm-dd') end from empworkdatechange where edc_id="+id);
		while(r.next()){
			SqlRowList rs=baseDao.queryForRowSet("select edcd_emid from EMPWORKDATECHANGEDETAIL left join EMPWORKDATECHANGE on edcd_edcid=edc_id where edc_id="+id);
			List<String> sqls = new ArrayList<String>();
			while(rs.next()){
				String sql ="update EMPWORKDATE a set ew_wdcode=(select b.edc_wdcode from empWorkDateChange b where edc_id="+id+")  where ew_emid="+rs.getInt("edcd_emid")+" and exists (select 1 from empWorkDateChange b where edc_id="+id+" and a.ew_date between edc_begindate and edc_enddate)";
				sqls.add(sql );
			}
			baseDao.execute(sqls);
		}			
		// 记录操作
		baseDao.logger.audit(caller, "edc_id",  id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] {id });	
	}

	@Override
	public void resAuditEmpWorkDateChange(int  id, String caller) {
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, new Object[] {id});
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("empWorkDateChange","edc_statuscode", "edc_id=" +  id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("empWorkDateChange", "edc_id=" +  id, "edc_status","edc_statuscode","edc_auditdate", "edc_auditer");
		baseDao.deleteById("empWorkDateChangeDetail", "edcd_edcid",  id);
		// 记录操作
		baseDao.logger.resAudit(caller, "edc_id",  id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, new Object[] { id});		
	}
	/**
	 * 把员工插入到明细中
	 */
	private void insertAllEmps(Object emids,Object scid){
		int detno = 1;
		List<String> sqls = new ArrayList<String>();
		for (String emid : emids.toString().split(";")) {
			String sqldetail = "insert into empWorkDateChangeDetail(edcd_detno,edcd_edcid,edcd_emid,edcd_emcode,edcd_emname) select "+detno++ +","+scid+",em_id,em_code, em_name from employee where em_id="+emid;				
			sqls.add(sqldetail);
		}
		baseDao.execute(sqls);
	}

	@Override
	public List<JSONObject> getEmployees(String condition) {
    	List<JSONObject> treeList = new ArrayList<JSONObject>();
		SqlRowList rs=baseDao.queryForRowSet("select DISTINCT em_id,em_name from employee left join empworkdate on em_id=ew_emid where "+condition);
		 while(rs.next()){
	    	JSONObject  ob=new JSONObject();
	    	ob.put("text", rs.getString("em_name"));
	    	ob.put("value",rs.getInt("em_id")+"");
	    	ob.put("value1", rs.getString("em_name"));
	    	treeList.add(ob);
	    }
		return treeList;	
	}
}
