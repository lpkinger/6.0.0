package com.uas.erp.service.ma.impl;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.ma.ExcelFxService;
@Service
public class ExcelFxServiceImpl implements ExcelFxService{
	@Autowired
	private BaseDao baseDao;
	@Override
	public void save(String formStore, String  caller) {
		// TODO Auto-generated method stub
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//校验 条件语句是否成立
		String efSql=store.get("ef_sql").toString();
	    String args=store.get("ef_args").toString();
	    String []arr=args.split(",");
	    String TestSql=efSql;
	    if(arr[0]!=null&&!arr[0].equals("")){
	    for(int i=0;i<arr.length;i++){
	    	String fieldtype=arr[i].split(";")[1];
	    	String field=arr[i].split(";")[0];
	    	if(fieldtype.equals("numberfield")){
	    		TestSql=TestSql.replaceAll(field,"'0'");
	    	}else if(fieldtype.equals("datefield")){
	    		TestSql=TestSql.replaceAll(field, "sysdate");
	    	}else {
	    		TestSql=TestSql.replaceAll(field, "null");
	    	}
	    }
	    }
	    TestSql=TestSql.replaceAll("BASECONDITION", "").replaceAll("beginmonthy","'1'").replaceAll("thismonthy", "'1'");
	    TestSql=TestSql.replaceAll("@@", "'");
		try{
		  baseDao.queryForRowSet(TestSql);
		}catch(Exception e){
		 	BaseUtil.showError(efSql+"</br>SQL语句设置不正确,请重新设置!");
		 	return;
		}
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ExcelFx", new String[]{}, new Object[]{});
		baseDao.execute(formSql);	
		try{
			//记录操作
			baseDao.logger.save(caller, "ef_id", store.get("ef_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void delete(int id, String  caller) {
		// TODO Auto-generated method stub
		baseDao.deleteById("ExcelFx", "ef_id", id);
		//记录操作
		baseDao.logger.delete(caller, "ef_id", id);
	}

	@Override
	public void update(String formStore, String  caller) {
		// TODO Auto-generated method stub
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ExcelFx", "ef_id");		
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "ef_id", store.get("ef_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
