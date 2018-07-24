package com.uas.erp.service.oa.impl;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.DocumentRoom;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.oa.DocumentRoomService;
@Service
public class DocumentRoomServiceImpl implements DocumentRoomService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	public void saveDocumentRoom(String formStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("DocumentRoom", "dr_id='" + store.get("dr_id") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller,new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "DocumentRoom", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "dr_id", store.get("dr_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		//执行保存后的其它逻辑
		handlerService.afterSave(caller,new Object[]{store});
	}
	@Override
	public void deleteDocumentRoom(int dr_id, String  caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[]{dr_id});
		//删除Knowledge
		baseDao.deleteById("DocumentRoom", "dr_id", dr_id);		
		//记录操作
		baseDao.logger.delete(caller, "dr_id", dr_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[]{dr_id});
	}

	@Override
	public void updateDocumentRoom(String formStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "DocumentRoom", "dr_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "dr_id", store.get("dr_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<JSONTree> getJSONTree(String caller) {
		List<JSONTree> tree=new ArrayList<JSONTree>();  
		List<DocumentRoom> rooms=baseDao.getJdbcTemplate().query("SELECT * FROM DocumentRoom", new BeanPropertyRowMapper(DocumentRoom.class));
		JSONTree root = new JSONTree();
		root.setId(0);
		root.setParentId(-1);
		root.setDeleteable(false);
		root.setText("档案室");
		root.setQtip("档案室");
		root.setLeaf(false);
		root.setAllowDrag(true);
		root.setExpanded(false);
		root.setCls("x-tree-cls-root");
		if(rooms.size()>0){
			List<JSONTree> jtree=new ArrayList<JSONTree>();  
			for(DocumentRoom room:rooms){
			  JSONTree ct = new JSONTree(room);  
			  jtree.add(ct);
		   }
		   root.setChildren(jtree);
		}
		tree.add(root);
		return tree;
	  }
	@Override
	public void addDept(int drid, String dept, int deptid) {
		int id = baseDao.getSeqId("DOCUMENTROOMDEPT_SEQ");
		String sql = "insert into DocumentRoomDept(drd_id,drd_dept_id,drd_dept,drd_drid) " +
				"values(" + id + "," + deptid + ",'" +dept + "'," + drid + ")"; 
		baseDao.execute(sql);		
	}

}

