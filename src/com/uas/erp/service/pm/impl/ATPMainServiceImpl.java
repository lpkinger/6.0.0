package com.uas.erp.service.pm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.ATPMainService;

@Service("atpMainService")
public class ATPMainServiceImpl implements ATPMainService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void saveATPMain(String formStore, String gridStore,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store, gridStore});
		//保存BillOut
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ATPMain", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//保存BillOutDetail
		for(Map<Object, Object> m:grid){
			m.put("ad_id", baseDao.getSeqId("ATPDETAIL_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "ATPDetail");
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "am_id=",store.get("am_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store, gridStore});
	}
	@Override
	public void deleteATPMain(int am_id,String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("ATPMain", "am_statuscode", "am_id=" + am_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{am_id}); 
		baseDao.deleteById("ATPMain", "am_id", am_id); 
		baseDao.deleteById("atpdetail", "ad_amid", am_id);
		baseDao.deleteById("ATPData", "ad_atpid", am_id);
		//记录操作
		baseDao.logger.delete(caller, "am_id", am_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{am_id}); 
	}
	
	@Override
	public void updateATPMainById(String formStore, String gridStore,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("ATPMain", "am_statuscode", "am_id=" + store.get("am_id"));
		StateAssert.updateOnlyEntering(status);
		
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store, gstore});
		//修改BillOut
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ATPMain", "am_id");
		baseDao.execute(formSql);
		//修改BillOutDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ATPDetail", "ad_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("ad_id") == null || s.get("ad_id").equals("") || s.get("ad_id").equals("0") ||
					Integer.parseInt(s.get("ad_id").toString()) == 0){//新添加的数据，id不存在
				s.put("ad_id", baseDao.getSeqId("ATPDETAIL_SEQ"));
				String sql = SqlUtil.getInsertSqlByMap(s, "ATPDetail");
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "am_id", store.get("am_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store, gstore});
	}
	@Override
	public void printATPMain(int am_id,String caller) {
		//只能打印审核后的单据!
		Object status = baseDao.getFieldDataByCondition("ATPMain", "am_statuscode", "am_id=" + am_id);
		if(!status.equals("AUDITED") && !status.equals("PARTRECEIVED") 
				&& !status.equals("RECEIVED") && !status.equals("NULLIFIED")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.print_onlyAudit"));
		}
		//执行打印前的其它逻辑
		handlerService.beforePrint(caller, new Object[]{am_id});
		//执行打印操作
		//记录操作
		baseDao.logger.print(caller, "am_id", am_id);
		//执行打印后的其它逻辑
		handlerService.afterPrint(caller, new Object[]{am_id});
	}
	@Override
	public void auditATPMain(int am_id,String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ATPMain", "am_statuscode", "am_id=" + am_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{am_id});
		//执行审核操作
		baseDao.audit("ATPMain", "am_id=" + am_id, "am_status", "am_statuscode", "am_auditdate", "am_auditman");
		//记录操作
		baseDao.logger.audit(caller, "am_id", am_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{am_id});
	}
	@Override
	public void resAuditATPMain(int am_id,String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ATPMain", "am_statuscode", "am_id=" + am_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("ATPMain", "am_id=" + am_id, "am_status", "am_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "am_id", am_id);
	}
	@Override
	public void submitATPMain(int am_id,String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ATPMain", "am_statuscode", "am_id=" + am_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller,new Object[]{am_id});
		//执行提交操作
		baseDao.submit("ATPMain", "am_id=" + am_id, "am_status", "am_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "am_id", am_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller,new Object[]{am_id});
	}
	@Override
	public void resSubmitATPMain(int am_id,String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ATPMain", "am_statuscode", "am_id=" + am_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller,new Object[]{am_id});
		//执行反提交操作
		baseDao.resOperate("ATPMain", "am_id=" + am_id, "am_status", "am_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "am_id", am_id);
		handlerService.afterResSubmit(caller,new Object[]{am_id});
	}
	@Override
	public void executeOperation(int am_id,String caller) {
		// TODO Auto-generated method stub 
		String	res = baseDao.callProcedure("MM_RUNATP",
				new Object[] { am_id,SystemSession.getUser().getEm_name()});
		if (res != null && !res.trim().equals("") && !"运算成功".equals(res.toUpperCase())) {
			BaseUtil.showErrorOnSuccess(res);
		}
	}
	@Override
	public int runATPFromOther(String fromcode,String fromwhere,String caller) {
		int atpid=0;
		String SQLStr=""; 
		String Atpcode=""; 
		Object findid=baseDao.getFieldDataByCondition("atpmain,atpdetail", "am_id", "am_fromcode='"+fromcode+"' and am_fromwhere='"+fromwhere+"' and am_id=ad_amid and ad_prodcode<>' ' ");
		if (findid!=null && Integer.parseInt(findid.toString())>0){
			if (fromwhere.equals("SALE")){
				//检查是否有变更的数据行
				SqlRowList sl=baseDao.queryForRowSet("select sd_detno from saledetail left join atpmain on am_fromcode=sd_code and am_fromwhere='"+fromwhere+"' left join atpdetail on ad_amid=am_id and ad_detno=sd_detno where sd_code='"+fromcode+"' and (ad_detno is null or sd_qty<>ad_qty or trunc(NVL(sd_pmcdate,sd_delivery))<>trunc(ad_delivery) or ad_prodcode <>sd_prodcode) ");
				if(sl.next()){ 
					try{
						this.deleteATPMain(Integer.parseInt(findid.toString()),caller); 
					} catch (Exception e) { 
					} 
				}else{ 
					return  Integer.parseInt(findid.toString());
				}
			}else if (fromwhere.equals("PRESALE")){
				//检查是否有变更的数据行
				SqlRowList sl=baseDao.queryForRowSet("select ps_code from presale left join atpmain on am_fromcode=ps_code and am_fromwhere='"+fromwhere+"' left join atpdetail on ad_amid=am_id  where ps_code='"+fromcode+"' and (ad_detno is null or ps_qty<>ad_qty or trunc(ps_startdate)<>trunc(ad_delivery) or ad_prodcode <>ps_prodcode) ");
				if(sl.next()){  
					try{
						this.deleteATPMain(Integer.parseInt(findid.toString()),caller); 
					} catch (Exception e) { 
					} 
				}else{
					return  Integer.parseInt(findid.toString());
				}
			}  
		} 
		if (fromwhere.equals("SALE")){
			SQLStr="select sd_detno,sd_prodcode,sd_qty,NVL(sd_pmcdate,sd_delivery) sd_pmcdate from saledetail,sale where sa_code='"+fromcode+"' and sd_said=sa_id";
		}else if(fromwhere.equals("PRESALE")){
			SQLStr="select 1,ps_prodcode,ps_qty,ps_startdate  from presale where ps_code='"+fromcode+"' "; 
		}else{
			return 0;
		}
		SqlRowList sl=baseDao.queryForRowSet(SQLStr);
		if(sl.next()){ 
			Atpcode =  baseDao.sGetMaxNumber("ATPMain", 2); 
			baseDao.execute("insert into atpmain(am_id,am_code,am_fromcode,am_fromwhere,am_title,am_type,am_indate,am_inman,am_statuscode,am_status) "
					+"values(ATPMain_SEQ.nextval,'"+Atpcode+"','"+fromcode+"','"+fromwhere+"','"+fromcode+"','订单优先',sysdate,'"+SystemSession.getUser().getEm_name()+"','ENTERING','"+BaseUtil.getLocalMessage("ENTERING")+"')");
			findid=baseDao.getFieldDataByCondition("atpmain,atpdetail", "am_id", "am_code='"+Atpcode+"' ");
			if (findid!=null && Integer.parseInt(findid.toString())>0){
				atpid=Integer.parseInt(findid.toString());
			}
			if (fromwhere.equals("SALE")){
				baseDao.execute("insert into atpdetail(ad_id,ad_amid,ad_detno,ad_prodcode,ad_qty,ad_delivery,ad_specdescription,ad_salecode,ad_saledetno,ad_sdid,ad_forecastcode,ad_forecastdetno,ad_bonded)"
						+"select ATPDetail_SEQ.nextval,"+atpid+",sd_detno,sd_prodcode,sd_qty,NVL(sd_pmcdate,sd_delivery),sd_specdescription,sd_code,sd_detno,sd_id,sd_forecastcode,sd_forecastdetno,NVL(sd_bonded,0)  from saledetail,sale where sa_code='"+fromcode+"' and sd_said=sa_id"); 
			}else if(fromwhere.equals("PRESALE")){
				baseDao.execute("insert into atpdetail(ad_id,ad_amid,ad_detno,ad_prodcode,ad_qty,ad_delivery)"
						+"select ATPDetail_SEQ.nextval,"+atpid+",1,ps_prodcode,ps_qty,ps_startdate  from presale where ps_code='"+fromcode+"' "); 
			}
		}
		if (atpid>0){
			executeOperation(atpid,caller); 
		}
		return atpid; 
	}
	@Override
	public void loadSale(String caller, String data, int am_id) {
		List<Map<Object, Object>> lists = BaseUtil.parseGridStoreToMaps(data);
		Map<Object, Object> map = null;
		List<String> sqls = new ArrayList<String>();		
		Object maxdetno = baseDao.getFieldDataByCondition("ATPDetail", "nvl(max(ad_detno),0)", "ad_amid=" + am_id);
		Object[] fieldsdata = baseDao.getFieldsDataByCondition("ATPMain", new String[]{"am_code"}, "am_id=" + am_id);
		int detno = Integer.parseInt(maxdetno.toString()) + 1;
		for (int i = 0; i < lists.size(); i++) {
			map = lists.get(i);			
			sqls.add("insert into atpdetail(ad_id,ad_amid,ad_detno,ad_prodcode,ad_qty,ad_delivery,ad_specdescription,ad_salecode,ad_saledetno,ad_sdid,ad_code,ad_forecastcode,ad_forecastdetno) " +
					"select ATPDetail_SEQ.nextval,"+am_id+","+detno+",sd_prodcode,sd_qty,NVL(sd_pmcdate,sd_delivery),sd_specdescription,sa_code,sd_detno,sd_id,'"+fieldsdata[0]+"',sd_forecastcode,sd_forecastdetno from saledetail,sale where sa_code='"+map.get("sa_code")+"' and sd_said=sa_id and sd_detno="+map.get("sd_detno"));
			detno++;
		}
		baseDao.execute(sqls); 
	}
	@Override
	public void loadAllSale(String caller, int am_id, String condition) {	 
		Object []obj1 = baseDao.getFieldsDataByCondition("form","fo_detailtable,fo_detailcondition", " fo_caller='"+caller+"'");
		if (obj1==null){
			return;
		}
		String BaseCondition = obj1[1].toString();
		BaseCondition =  BaseCondition + " AND " + condition;
		SqlRowList sl = baseDao
				.queryForRowSet("select sa_code,sa_date,sd_prodcode,sd_qty-nvl(sd_sendqty,0) qty,sd_detno from "+obj1[0].toString()+" where "
						+ BaseCondition);
		List<String> sqls = new ArrayList<String>();
		// 取时间
		Object maxdetno = baseDao.getFieldDataByCondition("ATPDetail", "nvl(max(ad_detno),0)", "ad_amid=" + am_id);
		Object[] fieldsdata = baseDao.getFieldsDataByCondition("ATPMain", new String[]{"am_code"}, "am_id=" + am_id);
		int detno = Integer.parseInt(maxdetno.toString()) + 1;
		while (sl.next()) {		
			sqls.add("insert into atpdetail(ad_id,ad_amid,ad_detno,ad_prodcode,ad_qty,ad_delivery,ad_specdescription,ad_salecode,ad_saledetno,ad_sdid,ad_code,ad_forecastcode,ad_forecastdetno) " +
					"select ATPDetail_SEQ.nextval,"+am_id+","+detno+",sd_prodcode,sd_qty,NVL(sd_pmcdate,sd_delivery),sd_specdescription,sa_code,sd_detno,sd_id,'"+fieldsdata[0]+"',sd_forecastcode,sd_forecastdetno from saledetail,sale where sa_code='"+sl.getString("sa_code")+"' and sd_said=sa_id and sd_detno="+sl.getString("sd_detno"));
			detno++;
		}
		baseDao.execute(sqls);
	}

}
