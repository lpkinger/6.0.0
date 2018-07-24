package com.uas.erp.service.pm.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.BomSetService;

@Service
public class BomsetServiceImpl implements BomSetService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void saveBomSet(String formStore,String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BomSet", "bs_code='" + store.get("bs_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		bool  = baseDao.checkByCondition("BomSet", "bs_mothercode='"+store.get("bs_mothercode")+"'");
		if(!bool){
			BaseUtil.showError("该母件的配套表已经存在,不能新增！");
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave("BomSet",new Object[]{store});
		//保存BomSet
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BomSet", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		////保存BomSetDetail
		Object[] bsd_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			bsd_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				bsd_id[i] = baseDao.getSeqId("BomSetDETAIL_SEQ");
			}
		} else {
			bsd_id[0] = baseDao.getSeqId("BomSetDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "BomSetDetail", "bsd_id", bsd_id);
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "bs_id", store.get("bs_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave("BomSet",new Object[]{store,gstore});

	}

	@Override
	public void updateBomSetById(String formStore,String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("BomSet", "bs_statuscode", "bs_id=" + store.get("bs_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeSave("BomSet",new Object[]{store,gstore});
		//修改BomSet
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BomSet", "bs_id");
		baseDao.execute(formSql);
		//修改BomSetDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "BomSetDetail", "bsd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("bsd_id") == null || s.get("bsd_id").equals("") || s.get("bsd_id").equals("0") ||
					Integer.parseInt(s.get("bsd_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("BomSetDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "BomSetDetail", new String[]{"bsd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "bs_id", store.get("bs_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave("BomSet", new Object[]{store,gstore});

	}

	@Override
	public void deleteBomSet(int bs_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("BomSet", "bs_statuscode", "bs_id=" + bs_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel("BomSet", new Object[]{bs_id});
		//删除BomSet
		baseDao.deleteById("BomSet", "bs_id", bs_id);
		//删除BomSetDetail
		baseDao.deleteById("BomSetdetail", "bsd_bsid", bs_id);
		//记录操作
		baseDao.logger.delete(caller, "bs_id", bs_id);
		//执行删除后的其它逻辑
		handlerService.afterDel("BomSet", new Object[]{bs_id});

	}

	@Override
	public void auditBomSet(int bs_id, String caller) {		
		//只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("BomSet", new String[]{"bs_statuscode","bs_mothercode"}, "bs_id=" + bs_id);
		StateAssert.auditOnlyCommited(status[0]);
		checkNest(status[1].toString(),0);
		//执行审核前的其它逻辑
		handlerService.beforeAudit("BomSet", new Object[]{bs_id});
		//执行审核操作
		baseDao.audit("BomSet", "bs_id=" + bs_id, "bs_status", "bs_statuscode","bs_date","bs_recordman");
		//记录操作
		baseDao.logger.audit(caller, "bs_id", bs_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit("BomSet",new Object[]{bs_id});

	}

	@Override
	public void resAuditBomSet(int bs_id, String caller) {	
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("BomSet", "bs_statuscode", "bs_id=" + bs_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.updateByCondition("BomSet", "bs_statuscode='ENTERING',bs_status='" + 
				BaseUtil.getLocalMessage("ENTERING") + "',bs_date=" + 
				DateUtil.parseDateToOracleString(null, new Date()) + ",bs_recordman='" + 
				SystemSession.getUser().getEm_name() + "'", "bs_id=" + bs_id);
		//记录操作
		baseDao.logger.resAudit(caller, "bs_id", bs_id);

	}

	@Override
	public void submitBomSet(int bs_id, String caller) {		
		//只能对状态为[在录入]的订单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("BomSet", new String[]{"bs_statuscode","bs_code"}, "bs_id=" + bs_id);
		StateAssert.submitOnlyEntering(status[0]); 
		SqlRowList rs = baseDao.queryForRowSet("select bsd_detno from bomsetdetail left join product on pr_code=bsd_prodcode where bsd_bsid="+bs_id+" and pr_statuscode IS null");
		if (rs.next()) {
			BaseUtil.showError("序号:"+rs.getString("bsd_detno")+"物料编号不存在");
		}
		rs = baseDao.queryForRowSet("select bsd_detno from bomsetdetail left join product on pr_code=bsd_mothercode where bsd_bsid="+bs_id+" and pr_statuscode IS null");
		if (rs.next()) {
			BaseUtil.showError("序号:"+rs.getString("bsd_detno")+"母件编号不存在");
		}
		rs = baseDao.queryForRowSet("select bs_id from bomset  left join product on pr_code=bs_mothercode where bs_id="+bs_id+" and pr_statuscode IS null");
		if (rs.next()) {
			BaseUtil.showError("母件编号不存在");
		}
		rs = baseDao.queryForRowSet("select bsd_detno from bomsetdetail where bsd_bsid="+bs_id+" and bsd_prodcode=bsd_mothercode");
		if (rs.next()) {
			BaseUtil.showError("序号:"+rs.getString("bsd_detno")+"物料编号和母件编号相同");
		}
		rs = baseDao.queryForRowSet("select wmsys.wm_concat( a.bsd_detno) dt  from bomsetdetail a"
                                   +" where exists ( select 1 from bomsetdetail b where a.bsd_bsid = b.bsd_bsid"
                                   +" and b.bsd_prodcode = a.bsd_prodcode and b.bsd_mothercode=a.bsd_mothercode and a.bsd_detno <> b.bsd_detno)"
                                   +" and a.bsd_bsid="+bs_id);
		if (rs.next()) {
			if(rs.getString("dt") != null){
			   BaseUtil.showError("序号:"+rs.getString("dt")+",【不同明细行】物料编号和母件编号重复");
			}
		}
		Object ob = baseDao.getFieldDataByCondition("bomset", "bs_mothercode", "bs_id="+bs_id);
		if(ob!= null){
			//判断是否存在层级嵌套
			checkNest(ob.toString(),0);
		}
		//更新明细的bsd_code=bs_code
		baseDao.execute("update bomsetdetail set bsd_code='"+status[1]+"' where bsd_bsid="+bs_id);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit("BomSet",new Object[]{bs_id});
		//执行提交操作
		baseDao.updateByCondition("BomSet", "bs_statuscode='COMMITED',bs_status='" + 
				BaseUtil.getLocalMessage("COMMITED") + "',bs_date=" + 
				DateUtil.parseDateToOracleString(null, new Date()) + ",bs_recordman='" + 
				SystemSession.getUser().getEm_name() + "'", "bs_id=" + bs_id);
		//记录操作
		baseDao.logger.submit(caller, "bs_id", bs_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit("BomSet",new Object[]{bs_id});
	}

	@Override
	public void resSubmitBomSet(int bs_id, String caller) {		
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("BomSet", "bs_statuscode", "bs_id=" + bs_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit("BomSet", new Object[]{bs_id});
		//执行反提交操作
		baseDao.updateByCondition("BomSet", "bs_statuscode='ENTERING',bs_status='" + 
				BaseUtil.getLocalMessage("ENTERING") + "',bs_date=" + 
				DateUtil.parseDateToOracleString(null, new Date()) + ",bs_recordman='" + 
				SystemSession.getUser().getEm_name() + "'", "bs_id=" + bs_id);
		//记录操作
		baseDao.logger.resSubmit(caller, "bs_id", bs_id);
		handlerService.afterResSubmit("BomSet",new Object[]{bs_id});
	}
	
	public void checkNest(String mothercode,int level){		
		if(level > 15){	//判断是否存在嵌套
			BaseUtil.showError("BOM配套表存在层级嵌套,不允许提交");
		}else{
			level ++;
			SqlRowList rs = baseDao.queryForRowSet("select nvl(bsd_prodcode,bs_mothercode) mothercode ,bsd_bsid from bomset left join bomsetdetail on bsd_bsid=bs_id where bsd_mothercode='"+mothercode+"' and nvl(bsd_usestatus,' ')<>'DISABLE'");
			while(rs.next()){
		    	checkNest(rs.getString("mothercode"),level);
		    }	
		}		
	}
}
