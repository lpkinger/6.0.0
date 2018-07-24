package com.uas.erp.service.hr.impl;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.EmpTransferCheckSetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EmpTransferCheckSetServiceImpl implements EmpTransferCheckSetService {

	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void save(String formStore, String gridStore,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gStore=BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller,new Object[] {store,gStore});
		for(Map<Object, Object> s:gStore){
			Object link = s.get("link");
			s.put("id",baseDao.getSeqId("EmpTransferCheckSet_SEQ"));
			s.put("caller", store.get("caller"));
			s.put("field", store.get("field"));
			s.put("tablename", store.get("tablename"));
			s.put("type", store.get("type"));
			if(link==null||"".equals(link.toString())){
				String caller_rel = s.get("caller_rel").toString();
				Object[] obj = baseDao.getFieldsDataByCondition("datalist", new String[]{"DL_LOCKPAGE","DL_KEYFIELD","DL_PFFIELD"}, "dl_caller='"+caller_rel+"'");
				if(obj[0].toString().contains("?")){
					link = obj[0]+"&formCondition="+obj[1]+"IS" + "@KEYVALUE"
							+ "&gridCondition="+(obj[2]==null?"null":obj[2]) +"IS" + "@KEYVALUE"+ "&whoami="+caller_rel ;
				}else{
					link = obj[0]+"?formCondition="+obj[1]+"IS" + "@KEYVALUE"
							+ "&gridCondition="+(obj[2]==null?"null":obj[2]) +"IS" + "@KEYVALUE"+ "&whoami="+caller_rel ;
				}
				s.put("link",link);
			}
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gStore, "EmpTransferCheckSet");
		baseDao.execute(gridSql);
		baseDao.execute("update  emptransfercheckset set KEYFIELD_REL=(select a.column_name  from user_cons_columns a,"
				+ " user_constraints b where a.constraint_name = b.constraint_name and b.constraint_type = 'P' and "
				+ "a.table_name =Upper(table_rel)) where caller='"+store.get("caller")+"' and KEYFIELD_REL is null");
		try {
			// 记录操作
			baseDao.logger.save(caller, "caller", store.get("caller"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store,gStore});
	}

	@Override
	public void updateEmpTransferCheckSetById(String formStore, String gridStore,String   caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] {store,gstore});
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "EmpTransferCheckSet", "id");
		for (Map<Object, Object> s : gstore) {
			Object link = s.get("link");
			if(link==null||"".equals(link.toString())){
				String caller_rel = s.get("caller_rel").toString();
				Object[] obj = baseDao.getFieldsDataByCondition("datalist", new String[]{"DL_LOCKPAGE","DL_KEYFIELD","DL_PFFIELD"}, "dl_caller='"+caller_rel+"'");
				if(obj!=null&&obj[0]!=null&&obj[1]!=null){
					if(obj[0].toString().contains("?")){
						link = obj[0]+"&formCondition="+obj[1]+"IS" + "@KEYVALUE"
								+ "&gridCondition="+(obj[2]==null?"null":obj[2]) +"IS" + "@KEYVALUE"+ "&whoami="+caller_rel ;
					}else{
						link = obj[0]+"?formCondition="+obj[1]+"IS" + "@KEYVALUE"
								+ "&gridCondition="+(obj[2]==null?"null":obj[2]) +"IS" + "@KEYVALUE"+ "&whoami="+caller_rel ;
					}
					s.put("link",link);
				}
			}
			if (s.get("id") == null || s.get("id").equals("")
					|| s.get("id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("EmpTransferCheckSet_SEQ");
				/*s.put("caller", store.get("caller"));
				s.put("field", store.get("field"));
				s.put("tablename", store.get("tablename"));
				s.put("type", store.get("type"));*/
				String sql = SqlUtil.getInsertSqlByMap(s, "EmpTransferCheckSet",
						new String[] { "id" }, new Object[] { id });
				gridSql.add(sql);
			}
			String sql = "update emptransfercheckset set link='"+link+"' where id="+s.get("id")+" ";
			gridSql.add(sql);
		}
		baseDao.execute(gridSql);
		baseDao.execute("update  emptransfercheckset set KEYFIELD_REL=(select a.column_name  from user_cons_columns a,"
				+ " user_constraints b where a.constraint_name = b.constraint_name and b.constraint_type = 'P' and "
				+ "a.table_name =Upper(table_rel)) where caller='"+store.get("caller")+"' and KEYFIELD_REL is null");
		// 记录操作
		baseDao.logger.update(caller, "caller", store.get("caller"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] {store,gstore});
	}

	@Override
	public void deleteEmpTransferCheckSet(int id, String   caller) {
		}
}
