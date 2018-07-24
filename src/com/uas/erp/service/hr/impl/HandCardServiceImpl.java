package com.uas.erp.service.hr.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.hr.HandCardService;

@Service
public class HandCardServiceImpl implements HandCardService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveHandCard(String formStore, String gridStore,
			String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		 List<Map<Object, Object>> gStore=BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[] {store,gStore});
		String formSql=SqlUtil.getInsertSqlByFormStore(store, "HandCard", new String[]{}, new Object[] {});
		
		for(Map<Object, Object> s:gStore){
			s.put("hcd_id",baseDao.getSeqId("HandCardDetail_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gStore, "HandCardDetail");
		baseDao.execute(formSql);
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "hc_id", store.get("hc_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store,gStore});
	}

	@Override
	public void updateHandCardById(String formStore, String gridStore,
			String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] {store,gstore});
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "HandCard", "hc_id");
				
		baseDao.execute(formSql);
		
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "HandCardDetail", "hcd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("hcd_id") == null || s.get("hcd_id").equals("")
					|| s.get("hcd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("HandCardDetail_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "HandCardDetail",
						new String[] { "hcd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "hc_id", store.get("hc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] {store,gstore});
	}

	@Override
	public void deleteHandCard(int hc_id, String  caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[] {hc_id});
		// 删除purchase
		baseDao.deleteById("HandCard", "hc_id", hc_id);
		// 删除purchaseDetail
		baseDao.deleteById("HandCardDetail", "hcd_hcid", hc_id);
		// 记录操作
		baseDao.logger.delete(caller, "hc_id", hc_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[] {hc_id});

	}

	@Override
	public void auditHandCard(int hc_id, String  caller) {
		// 只能对状态为[已提交]的订单进行审核操作!

		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] {hc_id});
//		String getDateSql = "SELECT TO_CHAR(hc_startdate,'yyyy-mm-dd') AS STARTDATE, TO_CHAR(hc_enddate,'yyyy-mm-dd') AS ENDDATE FROM HandCard WHERE hc_id="+hc_id;
		String getDate[] = baseDao.getStringFieldsDataByCondition("HandCard", new String[]{"TO_CHAR(hc_startdate,'yyyy-mm-dd')","TO_CHAR(hc_enddate,'yyyy-mm-dd')","hc_cardtime"}, "hc_id="+hc_id);
		
		String timeStrSql = "SELECT TO_CHAR(TO_DATE('"+getDate[0]+"','yyyy-mm-dd') + ROWNUM - 1,'yyyy-mm-dd') AS DATES " +
				"FROM ALL_OBJECTS " +
				"WHERE TO_DATE('"+getDate[0]+"','yyyy-mm-dd') + ROWNUM -1 <= TO_DATE('"+getDate[1]+"','yyyy-mm-dd')";
		SqlRowList rs = baseDao.queryForRowSet(timeStrSql);
		String sql = "";
		if(rs.hasNext()){
			while(rs.next()){
				sql = "INSERT INTO CARDLOG (cl_id, cl_cardcode, cl_emid, cl_time, cl_status, cl_emcode) " +
						" SELECT CardLog_SEQ.Nextval, '', hcd_emid, to_date('"+rs.getGeneralString(1)+" "+getDate[2]+"','yyyy-mm-dd hh24:mi') , 'HAND', hcd_emcode FROM HandCardDetail WHERE hcd_hcid="+hc_id;
				baseDao.execute(sql);
			}
		}	
		// 执行审核操作
		baseDao.audit("HandCard", "hc_id=" + hc_id, "hc_status", "hc_statuscode", "hc_auditdate", "hc_auditman");	
		// 记录操作
		baseDao.logger.audit(caller, "hc_id", hc_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] {hc_id});
	}

	@Override
	public void insertEmployee(int hcid, String deptcode, String caller) {
		// TODO Auto-generated method stub
		
		String sql = "INSERT INTO HandCardDetail (hcd_id, hcd_hcid, hcd_emid, hcd_emcode, hcd_emname, hcd_detno) " +
				"SELECT HandCardDetail_SEQ.Nextval, "+hcid+" ,em_id ,em_code ,em_name , rownum+NVL((SELECT MAX(hcd_detno) FROM HandCardDetail WHERE hcd_hcid="+hcid+"),0) " +
						"FROM employee WHERE em_depart = (SELECT dp_name FROM department WHERE dp_code ='"+deptcode+"')";
		
		baseDao.execute(sql);
		
		
	}



}
