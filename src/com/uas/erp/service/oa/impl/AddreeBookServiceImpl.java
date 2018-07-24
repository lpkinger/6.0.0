package com.uas.erp.service.oa.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.AddressBookGroup;
import com.uas.erp.model.HROrg;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.oa.AddressBookService;
@Service
public class AddreeBookServiceImpl implements AddressBookService{
   @Autowired
   private BaseDao baseDao;
   @Autowired
   private HandlerService handlerService;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<JSONTree> getJSONGroup(String caller,int emid) {
	 List<AddressBookGroup> groups=	baseDao.getJdbcTemplate().query("select * from AddressBookGroup where ag_emid="+emid+" order by ag_id asc",new BeanPropertyRowMapper(AddressBookGroup.class));
	 List<JSONTree> tree=new ArrayList<JSONTree>(); 
	  JSONTree j=new JSONTree();
	  SqlRowList s= baseDao.queryForRowSet("select count(*) from AddressBook where ab_recorderid="+emid);
	  if(s.next()){
		  j.setText("全部联系人("+s.getInt(1)+")");
		  j.setParentId(-1);
		  j.setId(0);
	  }
	  tree.add(j);
	 if(groups.size()>0){
		 for(AddressBookGroup group:groups){
		    SqlRowList sl= baseDao.queryForRowSet("select count(*) from AddressBook where ab_groupid="+group.getAg_id());
		     if(sl.next()){
		    	 String str=group.getAg_remark();
		    	 if(str!=null&&str.length()>8){
		    		str= str.substring(0, 8);
		    	 }
		    	 group.setAg_remark(str);
		    	 group.setAg_name(group.getAg_name()+"("+sl.getInt(1)+")");
		    	 
		     }
			 JSONTree  jt=new JSONTree(group);			  
			 tree.add(jt);
		 } 		 
	 }
     return tree;
	}
	@Override
	public void saveAddressBookGroup(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller,new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "AddressBookGroup", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "ag_id", store.get("ag_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller,new Object[]{store});
	}
	@Override
	public void updateAddressBookGroup(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[]{store});	
		//保存
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "AddressBookGroup","ag_id");
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.update(caller, "ag_id", store.get("ag_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterUpdate(caller, new Object[]{store});	
	}
	@Override
	public void deleteAddressBookGroup(int id, String caller) {
		handlerService.beforeDel(caller,new Object[]{id});
		//删除AddressBookGroup
		baseDao.deleteById("AddressBookGroup", "ag_id", id);		
		//记录操作
		baseDao.logger.delete(caller, "ag_id", id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[]{id});		
	}
	@Override
	public void removeToOtherGroup(int id, String data, String caller) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		for(Map<Object, Object> s:store){
	       baseDao.updateByCondition("AddressBook","ab_groupid="+id, "ab_id="+Integer.parseInt( s.get("ab_id").toString()));
		}
	}
	@Override
	public void saveAddressPerson(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller,new Object[]{store});	
		store.remove("msg");
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "AddressBook", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "ab_id", store.get("ab_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller,new Object[]{store});	
	}
	@Override
	public void updateAddressPerson(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		store.remove("msg");		
		handlerService.beforeUpdate(caller, new Object[]{store});	
		//保存
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "AddressBook","ab_id");
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.update(caller, "ab_id", store.get("ab_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterUpdate(caller, new Object[]{store});		
	}
	@Override
	public void deleteAddressPerson(String data, String caller) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		for(Map<Object, Object> s:store){
	       baseDao.deleteById("AddressBook", "ab_id", Integer.parseInt( s.get("ab_id").toString())); 
		}
		
	}
	@Override
	public void sharedToOther(String formStore, String type, String data,String caller) {
		//可能传回来的是   组 也可能传回来的是个人
		Map<Object, Object> form=BaseUtil.parseFormStoreToMap(formStore); 
		List<String> sqls=new ArrayList<String>();
		String []personid=form.get("sharedid").toString().split(";");
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
			for(int i=0;i<personid.length;i++){
				if(type.equals("group")){
				for(Map<Object, Object> s:store){			
			      int as_id=baseDao.getSeqId("AddressBookShare_SEQ");
			      StringBuffer sb1 = new StringBuffer("INSERT into AddressBookShare (as_id,as_groupid,as_sharedemid,as_recorderid,as_recordername,as_RECORDDATE) Values (");
			    	sb1.append("'"+as_id+"',");
			    	sb1.append( "'"+s.get("ab_groupid")+"',");
			    	sb1.append("'"+Integer.parseInt(personid[i])+"',");
			    	sb1.append("'"+SystemSession.getUser().getEm_id()+"',");
			    	sb1.append("'"+SystemSession.getUser().getEm_name()+"',");
			    	sb1.append("to_date('"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"','yyyy-MM-dd'))");
                   sqls.add(sb1.toString());
				}
				}else {
					List<Map<Object, Object>> datas = BaseUtil.parseGridStoreToMaps(data);
					for(Map<Object, Object> da:datas){
						  int as_id=baseDao.getSeqId("AddressBookShare_SEQ");
					      StringBuffer sb1 = new StringBuffer("INSERT into AddressBookShare (as_id,as_groupid,as_sharedemid,as_recorderid,as_recordername,as_RECORDDATE) Values (");
					    	sb1.append("'"+as_id+"',");
					    	sb1.append( "'"+da.get("ab_groupid")+"',");
					    	sb1.append("'"+Integer.parseInt(personid[i])+"',");
					    	sb1.append("'"+SystemSession.getUser().getEm_id()+"',");
					    	sb1.append("'"+SystemSession.getUser().getEm_name()+"',");
					    	sb1.append("to_date('"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"','yyyy-MM-dd'))");
		                   sqls.add(sb1.toString());
				   } 
				}
			 }				
		baseDao.execute(sqls);
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<JSONTree> getEmployee(String caller) {
		List<JSONTree> tree=new ArrayList<JSONTree>(); 	
		JSONTree j=new JSONTree();
		  SqlRowList s= baseDao.queryForRowSet("select count(*) from Employee");
		  if(s.next()){
			  j.setText("全部联系人("+s.getInt(1)+")");
			  j.setParentId(-1);
			  j.setId(0);
		  }
		  tree.add(j);
		 List<HROrg> orgs=	baseDao.getJdbcTemplate().query("select * from HrOrg  ",new BeanPropertyRowMapper(HROrg.class));
		 for(HROrg org:orgs){
			 SqlRowList sl= baseDao.queryForRowSet("select count(*) from Employee where em_defaultorid="+org.getOr_id());
			 while(sl.next()){
				org.setOr_name(org.getOr_name()+"("+sl.getInt(1)+")");
				JSONTree  it=new JSONTree(org,0);
				tree.add(it);
			 }			 
		 }
	   return tree;
	}

}
