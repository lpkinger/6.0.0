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
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.KnowledgeKind;
import com.uas.erp.model.KnowledgeModule;
import com.uas.erp.service.oa.KnowledgeService;
@Service
public class KnowledgeServiceImpl implements KnowledgeService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	public void saveKnowledge(String formStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Knowledge", "kl_code='" + store.get("kl_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Knowledge", new String[]{}, new Object[]{});
		// 发布之后往人员表中插入数据 便于个人知识的处理  不然 不好处理个人
		Object personid=store.get("kl_scanpersonid");
		Object maincode=store.get("kl_code");
		List<String>sqls=new ArrayList<String>();
		sqls.add(formSql);
		if(personid!=null&&!personid.equals("")){
			String []arr=personid.toString().split("#");
			for(int i=0;i<arr.length;i++){
				int kp_id=baseDao.getSeqId("KNOWLEDGEPERSON_SEQ");
				String insertSql="insert into knowledgeperson (kp_id,kp_personid,kp_klcode)values("+kp_id+","+Integer.parseInt(arr[i])+",'"+maincode+"')";
				sqls.add(insertSql);
			}
		}
//		SqlRowSet rs=baseDao.getJdbcTemplate().queryForRowSet("select * from knowledge"); 
//		while(rs.next()){
//			String person=rs.getString("kl_scanpersonid");
//			 String  kl_code=rs.getString("kl_code");
//			
//			 String []arr=person.toString().split("#");
//				for(int i=0;i<arr.length;i++){
//					int kp_id=baseDao.getSeqId("KNOWLEDGEPERSON_SEQ");
//					String insertSql="insert into knowledgeperson (kp_id,kp_personid,kp_klcode)values("+kp_id+","+Integer.parseInt(arr[i])+","+kl_code+")";
//					sqls.add(insertSql);
//				}
//			 
//		}
		baseDao.execute(sqls);
		try{
			//记录操作
			baseDao.logger.save(caller, "kl_id", store.get("kl_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}
	@Override
	public void deleteKnowledge(int kl_id, String  caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{kl_id});
		//删除Knowledge
		baseDao.deleteById("Knowledge", "kl_id", kl_id);		
		//记录操作
		baseDao.logger.delete(caller, "kl_id", kl_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{kl_id});
	}

	@Override
	public void updateKnowledge(String formStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Knowledge", "kl_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "kl_id", store.get("kl_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[]{store});
	}


	@Override
	public void auditKnowledge(int kl_id, String  caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Knowledge", "kl_statuscode", "kl_id=" + kl_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{kl_id});
		//执行审核操作
		baseDao.audit("Knowledge", "kl_id=" + kl_id, "kl_status", "kl_statuscode", "kl_auditdate", "kl_auditman");
		//记录操作
		baseDao.logger.audit(caller, "kl_id", kl_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{kl_id});
	}
	@Override
	public void resAuditKnowledge(int kl_id, String  caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Knowledge", "kl_statuscode", "kl_id=" + kl_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("Knowledge", "kl_id=" + kl_id, "kl_status", "kl_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "kl_id", kl_id);
	}
	@Override
	public void submitKnowledge(int kl_id, String  caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Knowledge", "kl_statuscode", "kl_id=" + kl_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{kl_id});
		//执行提交操作
		baseDao.submit("Knowledge", "kl_id=" + kl_id, "kl_status", "kl_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "kl_id", kl_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{kl_id});
	}
	@Override
	public void resSubmitKnowledge(int kl_id, String  caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Knowledge", "kl_statuscode", "kl_id=" + kl_id);
		StateAssert.resSubmitOnlyCommited(status);
		//执行反提交操作
		baseDao.resOperate("Knowledge", "kl_id=" + kl_id, "kl_status", "kl_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "kl_id", kl_id);
	}
	@Override
	public void recommendKnowledge(String data, String  caller) {
		try{
			List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
			Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupMap(store, "kl_code");
			Object code = null;
			List<String>sqls=new ArrayList<String>();
			for(Object m:map.keySet()){
				List<Map<Object, Object>> maps=map.get(m);
				code=maps.get(0).get("kl_code");
				sqls.add("update knowledge set kl_recommendtimes=kl_recommendtimes"+1+" where kl_code='"+code+"'");
			}
		  baseDao.execute(sqls);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	@Override
	public void VastDeleteKnowledgeModule(String data, String caller) {
		List<String >sqls=new ArrayList<String>();
		try{
			List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
			for(int i=0;i<store.size();i++){
				sqls.add("Delete from KnowledgeModule  where km_id="+store.get(i).get("km_id"));
				
			}
		  baseDao.execute(sqls);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<JSONTree> getJSONModule(String caller) {
		List<JSONTree> tree=new ArrayList<JSONTree>();  
		List<KnowledgeModule> modules=baseDao.getJdbcTemplate().query("SELECT * FROM KnowledgeModule", new BeanPropertyRowMapper(KnowledgeModule.class));
		if(modules.size()>0){
			for(KnowledgeModule module:modules){
			JSONTree jt = new JSONTree(module);
		  List<KnowledgeKind> kinds=baseDao.getJdbcTemplate().query("SELECT * FROM KnowledgeKind where kk_kmid="+module.getKm_id(), new BeanPropertyRowMapper(KnowledgeKind.class));
		    List<JSONTree> tmtree=new ArrayList<JSONTree>();  
		  for(KnowledgeKind kind:kinds){
			  JSONTree ct = new JSONTree(kind);  
			  tmtree.add(ct);
		   }
		   jt.setChildren(tmtree);
		   tree.add(jt);
		}
		}
		return tree;
	  }
	@Override
	public void saveKnowledgeComment(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "KnowledgeComment", new String[]{}, new Object[]{});
		//更新知识中的分数 及评论的次数
	    Object id= store.get("kc_klid");
	    Object point=store.get("kc_point");
	    baseDao.updateByCondition("Knowledge", "kl_point=kl_point+"+point+",kl_commenttimes=kl_commenttimes+"+1, "kl_id="+id);
	    baseDao.execute(formSql);
		
	}
	@Override
	public void saveKnowledgeRecommend(String formStore,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[]{store});
		//更新知识中的分数 及评论的次数
	    Object id= store.get("kc_klid");
	    Object code=store.get("kc_klcode");
	    Object personid=store.get("kc_personid");
	    List<String>sqls=new ArrayList<String>();
	    baseDao.updateByCondition("Knowledge", "kl_recommonedtimes=kl_recommonedtimes+"+1, "kl_id="+id);
	    if(personid!=null&&!personid.equals("")){
			String []arr=personid.toString().split("#");
			for(int i=0;i<arr.length;i++){
				boolean bool = baseDao.checkByCondition("Knowledgeperson", "kp_klcode='" +code +"' AND  kp_personid="+Integer.parseInt(arr[i]));
				if(bool){
				int kp_id=baseDao.getSeqId("KNOWLEDGEPERSON_SEQ");
				String insertSql="insert into knowledgeperson (kp_id,kp_personid,kp_klcode,kp_status)values("+kp_id+","+Integer.parseInt(arr[i])+","+code+","+1+")";
				sqls.add(insertSql);
				}
			}
		}
	    baseDao.execute(sqls);
	}

}

