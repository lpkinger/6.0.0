package com.uas.erp.service.as.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sun.org.apache.xml.internal.utils.ObjectPool;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.as.MaterielReturnService;

@Service
public class MaterielReturnServiceImpl implements MaterielReturnService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveMaterielReturn(String formStore, String param,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[]{store});		
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "AS_MAKEReturn", new String[]{}, new Object[]{});
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		List<String> sqls = new ArrayList<String>();
		for(Map<Object, Object> s:gstore){
			if(s.get("amrd_id") == null || s.get("amrd_id").equals("") || s.get("amrd_id").equals("0")){//新添加的数据，id不存在
				int amrd_id = baseDao.getSeqId("AS_MAKERETURNDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "AS_MAKERETURNDETAIL", new String[]{"amrd_id"}, new Object[]{amrd_id});
				sqls.add(sql);
			}
		}
		baseDao.execute(sqls);
		baseDao.execute(formSql);	
		baseDao.logger.save(caller, "amr_id", store.get("amr_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void updateMaterielReturnById(String formStore,String param,String caller) {		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[]{store});
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "AS_MAKEReturn", "amr_id");
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(param, "AS_MAKERETURNDETAIL", "amrd_id");
		String amodid = null;
		for(Map<Object, Object> s:gstore){
			amodid = s.get("amrd_amodid").toString();
			if(s.get("amrd_id") == null || s.get("amrd_id").equals("") || s.get("amrd_id").equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("AS_MAKERETURNDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "AS_MAKERETURNDETAIL", new String[]{"amrd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
			Object outqty = baseDao.getFieldDataByCondition("AS_MAKEOUTDETAIL", "amod_outqty", "amod_id="+s.get("amrd_amodid"));
			Object returnqty = baseDao.getFieldDataByCondition("AS_MAKERETURNDETAIL", "sum(amrd_nowrtqty)", "amrd_amocode='"+s.get("amrd_amocode")+"' and amrd_id<>"+s.get("amrd_id")+" and amrd_amodid="+amodid);
			int sum1=Integer.parseInt(returnqty==null?"0":returnqty.toString());
			int sum2=Integer.parseInt(s.get("amrd_nowrtqty")==null?"0":s.get("amrd_nowrtqty").toString());
			int count1=Integer.parseInt(outqty==null?"0":outqty.toString());
			int total=sum1+sum2;
			if(count1<total){
				BaseUtil.showError("该归还单归还总数量大于出库数量，不允许更新");
			}else{
				baseDao.updateByCondition("AS_MAKEOUTDETAIL", "amod_tqty="+total+"", "amod_id="+amodid);
			}
		}
		baseDao.execute(gridSql);
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "amr_id", store.get("amr_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void deleteMaterielReturn(int amr_id, String caller) {		
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{amr_id});
		//回写出库单已转数
		String sql="select amrd_id,amrd_amodid,amrd_amocode,amrd_nowrtqty from as_makereturndetail where amrd_amrid="+amr_id;
		SqlRowList rs1 = baseDao.queryForRowSet(sql);
		while(rs1.next()){
			Object amo_id = baseDao.getFieldDataByCondition("AS_MAKEOUT",
					"amo_id", "amo_code='" + rs1.getString("amrd_amocode")+"'");
			baseDao.execute("update As_makeoutdetail set amod_tqty=nvl(amod_tqty,0)-"+rs1.getInt("amrd_nowrtqty")+" where amod_amoid="+amo_id+" and amod_id="+rs1.getInt("amrd_amodid"));
		}
		//删除主表内容
		baseDao.deleteById("AS_MAKEReturn", "amr_id", amr_id);
		baseDao.deleteById("As_makereturndetail", "amrd_amrid", amr_id );
		baseDao.logger.delete(caller, "amr_id", amr_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{amr_id});
	}

	@Override
	public void auditMaterielReturn(int amr_id, String caller) {
		//只能对已提交进行审核操作
		Object ob=baseDao.getFieldDataByCondition("AS_MAKEReturn", "amr_statuscode",  "amr_id=" + amr_id);
		StateAssert.auditOnlyCommited(ob);
		//执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[]{amr_id});
		String sql="select amrd_id,amrd_amodid,amrd_amocode,amrd_nowrtqty from as_makereturndetail where amrd_amrid="+amr_id;
		SqlRowList rs1 = baseDao.queryForRowSet(sql);
		while(rs1.next()){
			Object amo_id = baseDao.getFieldDataByCondition("AS_MAKEOUT",
					"amo_id", "amo_code='" + rs1.getString("amrd_amocode")+"'");
			baseDao.execute("update As_makeoutdetail set amod_rtqty=nvl(amod_rtqty,0)+"+rs1.getInt("amrd_nowrtqty")+" where amod_amoid="+amo_id+" and amod_id="+rs1.getInt("amrd_amodid"));
			baseDao.execute("update As_makereturndetail set amrd_rtqty=nvl(amrd_rtqty,0)+"+rs1.getInt("amrd_nowrtqty")+" where amrd_id="+rs1.getInt("amrd_id"));
		}
		//执行审核操作,待写
		baseDao.audit("AS_MAKEReturn", "amr_id=" + amr_id, "amr_status", "amr_statuscode", "amr_auditdate", "amr_auditor");
		//记录操作
		baseDao.logger.audit(caller, "amr_id", amr_id);
		//执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[]{amr_id});
	}
	@Override
	public void submitMaterielReturn(int amr_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("AS_MAKEReturn", "amr_statuscode", "amr_id=" + amr_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { amr_id });
		// 执行提交操作
		baseDao.submit("AS_MAKEReturn", "amr_id=" + amr_id, "amr_status", "amr_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "amr_id", amr_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { amr_id });
	}
	@Override
	public void resSubmitMaterielReturn(int amr_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("AS_MAKEReturn", "amr_statuscode", "amr_id=" + amr_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		baseDao.resOperate("AS_MAKEReturn", "amr_id=" + amr_id, "amr_status", "amr_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "amr_id", amr_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { amr_id });
	}
	@Override
	public void resAuditMaterielReturn(int amr_id, String caller) {
		// 只能对状态为[已审核]的表单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("AS_MAKEReturn", "amr_statuscode", "amr_id=" + amr_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		handlerService.handler(caller, "resCommit", "before", new Object[] { amr_id });
		String sql="select amrd_id,amrd_amodid,amrd_amocode,amrd_nowrtqty from as_makereturndetail where amrd_amrid="+amr_id;
		SqlRowList rs1 = baseDao.queryForRowSet(sql);
		while(rs1.next()){
			Object amo_id = baseDao.getFieldDataByCondition("AS_MAKEOUT",
					"amo_id", "amo_code='" + rs1.getString("amrd_amocode")+"'");
			baseDao.execute("update As_makeoutdetail set amod_rtqty=nvl(amod_rtqty,0)-"+rs1.getInt("amrd_nowrtqty")+" where amod_amoid="+amo_id+" and amod_id="+rs1.getInt("amrd_amodid"));
			baseDao.execute("update As_makereturndetail set amrd_rtqty=nvl(amrd_rtqty,0)-"+rs1.getInt("amrd_nowrtqty")+" where amrd_id="+rs1.getInt("amrd_id"));
		}
		baseDao.resAudit("AS_MAKEReturn", "amr_id=" + amr_id, "amr_status", "amr_statuscode", "amr_auditdate", "amr_auditor");
		baseDao.resOperate("AS_MAKEReturn", "amr_id=" + amr_id, "amr_status", "amr_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "amr_id", amr_id);
	}
}
