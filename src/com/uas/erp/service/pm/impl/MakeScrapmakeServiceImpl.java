package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.MakeScrapmakeService;

@Service
public class MakeScrapmakeServiceImpl implements MakeScrapmakeService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Autowired
	private PmHandler PmHandler;


	@Override
	public void saveMakeScrapmake(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("MakeScrap", "ms_code='" + store.get("ms_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//保存Dispatch
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "MakeScrap", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		////保存DispatchDetail
		Object[] md_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			md_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				md_id[i] = baseDao.getSeqId("MakeScrapdetail_SEQ");
			}
		} else {
			md_id[0] = baseDao.getSeqId("MakeScrapdetail_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "MakeScrapdetail", "md_id", md_id);
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "ms_id", store.get("ms_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void updateMakeScrapmakeById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("MakeScrap", "ms_statuscode", "ms_id=" + store.get("ms_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,new Object[]{store,gstore});
		//修改Dispatch
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "MakeScrap", "ms_id");
		baseDao.execute(formSql);
		//修改DispatchDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "MakeScrapdetail", "md_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("md_id") == null || s.get("md_id").equals("") || s.get("md_id").equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("MakeScrapdetail_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "MakeScrapdetail", new String[]{"md_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "ms_id", store.get("ms_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[]{store,gstore});
	}

	@Override
	public void deleteMakeScrapmake(int ms_id, String caller) {
		//只能删除在录入的单据!
				Object status = baseDao.getFieldDataByCondition("MakeScrap", "ms_statuscode", "ms_id=" + ms_id);
				StateAssert.delOnlyEntering(status);
				//执行删除前的其它逻辑
				handlerService.beforeDel(caller,new Object[]{ms_id});
				//删除Dispatch
				baseDao.deleteById("MakeScrap", "ms_id", ms_id);
				//删除DispatchDetail
				baseDao.deleteById("MakeScrapdetail", "md_msid", ms_id);
				//记录操作
				baseDao.logger.delete(caller, "ms_id", ms_id);
				//执行删除后的其它逻辑
				handlerService.afterDel(caller,new Object[]{ms_id});
	}

	@Override
	public void auditMakeScrapmake(int ms_id, String caller) {

		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("MakeScrap", "ms_statuscode", "ms_id=" + ms_id);
		StateAssert.auditOnlyCommited(status);
		ScrapCheck_scrapqty(ms_id);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{ms_id});  
		baseDao.updateByCondition("MakeScrapdetail", "md_status=99", "md_msid="+ms_id);
		List<Object[]> objects = baseDao.getFieldsDatasByCondition("MakeScrapdetail left join make on ma_code=md_mmcode ", new String[]{"ma_id","md_mmdetno","md_qty"}, " md_msid="+ms_id);
		for(Object[] object: objects){
			baseDao.updateByCondition("Makematerial ","mm_scrapqty =nvl(mm_scrapqty,0)+"+object[2], "mm_detno="+object[1]+" and mm_maid="+object[0]);
		}
		//执行审核操作
		baseDao.audit("MakeScrap", "ms_id=" + ms_id, "ms_status", "ms_statuscode", "ms_auditdate", "ms_auditman");
		//List<Object> objects = baseDao.getFieldDatasByCondition("MakeScrapdetail", "md_code", condition)
		
		//记录操作
		baseDao.logger.audit(caller, "ms_id", ms_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{ms_id});
	}

	@Override
	public void resAuditMakeScrapmake(int ms_id, String caller) {

		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("MakeScrap", "ms_statuscode", "ms_id=" + ms_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, new Object[]{ms_id});
		baseDao.updateByCondition("MakeScrapdetail", "md_status=0", "md_msid="+ms_id);
		List<Object[]> objects = baseDao.getFieldsDatasByCondition("MakeScrapdetail left join make on ma_code=md_mmcode", new String[]{"ma_id","md_mmdetno","md_qty"}, " md_msid="+ms_id);
		for(Object[] object: objects){ 
			baseDao.updateByCondition("Makematerial ","mm_scrapqty =nvl(mm_scrapqty,0)-"+object[2], "mm_detno="+object[1]+" and mm_maid="+object[0]);
		} 
		//执行反审核操作
		baseDao.resAudit("MakeScrap", "ms_id=" + ms_id, "ms_status", "ms_statuscode","ms_auditman","ms_auditdate");
		//记录操作
		baseDao.logger.resAudit(caller, "ms_id", ms_id);

	}

	@Override
	public void submitMakeScrapmake(int ms_id, String caller) {
		
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MakeScrap", "ms_statuscode", "ms_id=" + ms_id);
		StateAssert.submitOnlyEntering(status);
		ScrapCheck_scrapqty(ms_id);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller,new Object[]{ms_id});		//执行提交操作
		baseDao.submit("MakeScrap", "ms_id=" + ms_id, "ms_status", "ms_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "ms_id", ms_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller,new Object[]{ms_id});

	}

	@Override
	public void resSubmitMakeScrapmake(int ms_id, String caller) {
		
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MakeScrap", "ms_statuscode", "ms_id=" + ms_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller,new Object[]{ms_id});
		//执行反提交操作
		baseDao.resOperate("MakeScrap", "ms_id=" + ms_id, "ms_status", "ms_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "ms_id", ms_id);
		handlerService.afterResSubmit(caller,new Object[]{ms_id});

	}
	/**
	 * 判断报废数是否大于结存可报废数
	 * 
	 * @param msid
	 */
	public void ScrapCheck_scrapqty(Integer ms_id) {
		String sql = "";
		String err = "";
		sql = "UPDATE MakeMaterial set mm_allscrapqty=nvl(mm_scrapqty,0)+nvl((select sum(md_qty) from MakeScrapDetail,MakeScrap where ms_id=md_msid and (ms_statuscode='COMMITED' or ms_id='"
				+ ms_id
				+ "') and mm_code=md_mmcode and md_mmdetno=mm_detno),0) "
				+ " WHERE (mm_code,mm_detno) in (select md_mmcode as mm_code,md_mmdetno as mm_detno from MakeScrapDetail where md_msid='"
				+ ms_id + "')";
		baseDao.execute(sql);
		sql = "select md_detno,md_mmcode,md_prodcode from MakeScrapDetail left join makematerial on md_mmcode=mm_code and md_mmdetno=mm_detno left join make on mm_maid=ma_id where md_msid="
				+ ms_id ;
		//@zjh start
		if(baseDao.isDBSetting("Make", "allowChangeAfterCom")){
			sql = sql+ "  and round(nvl(mm_allscrapqty,0),4) > round(nvl(mm_havegetqty,0)-nvl(mm_backqty,0),4) ";
		}else{
			sql += "  and round(nvl(mm_allscrapqty,0),4)>round(nvl(mm_havegetqty,0)-mm_oneuseqty*ma_madeqty,4) ";
		}
		//@zjh  end
		SqlRowList rs = baseDao.queryForRowSet(sql);
		while (rs.next()) {
			err = err + "," + rs.getString("md_detno");
		}
		if (!err.equals("")) {
			BaseUtil.showError("序号:" + err.substring(1) + "报废数大于结存数");
		}
		sql = "SELECT count(1) detno ,wm_concat(md_detno) as detnostr FROM MakeScrapDetail WHERE  md_msid='"+ ms_id+ "' and md_qty<=0  ";
		rs = baseDao.queryForRowSet(sql);
		if (rs.next()) {
			if (rs.getInt("detno") > 0) {
				BaseUtil.showError("序号：" + rs.getString("detnostr") + "报废数小于或等于0，不能提交");
			} 
		}
	}
	

}
