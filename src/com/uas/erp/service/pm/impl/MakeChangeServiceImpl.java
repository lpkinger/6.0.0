package com.uas.erp.service.pm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.MakeDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.MakeChangeService;


@Service("makeChangeService")
public class MakeChangeServiceImpl implements MakeChangeService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private MakeDao makeDao; 
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveMakeChange(String caller,String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("MakeChange", "mc_code='" + store.get("mc_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store,gstore});		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "MakeChange", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		////保存Detail
	    List<String> gridSql = SqlUtil.getInsertSqlbyList(gstore, "MakeChangeDetail","md_id");
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "mc_id", store.get("mc_id"));
		} catch (Exception e) {
			e.printStackTrace();
		} 
		//更新原保税=case when ma_bonded<>0 then '是' else '否' end,
		baseDao.execute("update MakeChangeDetail set md_oldbonded=(select case when ma_bonded<>0 then '是' else '否' end from make where ma_code=md_makecode) where md_mcid="+store.get("mc_id"));
		//更新新保税，如果为空则默认等于原保税属性。
		baseDao.execute(" update MakeChangeDetail set md_newbonded=(md_oldbonded) where md_mcid="+store.get("mc_id")+" and nvl(md_newbonded,' ')=' '");
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store,gstore});
	}
	
	@Override
	public void deleteMakeChange(String caller,int mc_id) {
		//只能删除在录入的单据
		Object status = baseDao.getFieldDataByCondition("MakeChange", "mc_statuscode", "mc_id=" + mc_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{mc_id});		//删除
		baseDao.deleteById("MakeChange", "mc_id", mc_id);
		//删除Detail
		baseDao.deleteById("MakeChangedetail", "md_mcid", mc_id);
		//记录操作
		baseDao.logger.delete(caller, "mc_id", mc_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{mc_id});
	}
	
	@Override
	public void updateMakeChangeById(String caller,String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的采购单资料!
		Object status = baseDao.getFieldDataByCondition("makechange", "mc_statuscode", "mc_id=" + store.get("mc_id"));
		StateAssert.updateOnlyEntering(status);
		//更新采购计划下达数\本次下达数\状态
		//purchaseDao.updatePurchasePlan(Integer.parseInt((String)store.get("pu_id")));
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store,gstore});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "MakeChange", "mc_id");
		baseDao.execute(formSql);
		//修改Detail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "MakeChangeDetail", "md_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("md_id") == null || s.get("md_id").equals("") || s.get("md_id").equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("MAKECHANGEDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "MakeChangeDetail", new String[]{"md_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "mc_id", store.get("mc_id"));
		//更新上次采购价格、供应商
		//purchaseDao.updatePrePurchase((String)store.get("pu_code"), (String)store.get("pu_date"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store,gstore});
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void auditMakeChange(int mc_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("MakeChange", "mc_statuscode", "mc_id=" + mc_id);
		StateAssert.auditOnlyCommited(status);
		checkAll(mc_id, caller);//检测是否允许变更
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{mc_id}); 
		//变更业务处理  by zyl 2013-7-2
		SqlRowList rs = baseDao.queryForRowSet("select * from makechangedetail where md_mcid="+mc_id);
		String SQLStr="";
		List<String> sqls = new ArrayList<String>();
		while (rs.next()) {
			//变更委外工单的上传状态
			try{
				
				/*SQLStr="update make set ma_sendstatus='待上传' where ma_code='" +rs.getString("md_makecode") + "' ";
				baseDao.execute(SQLStr);*/
			}catch(Exception ex){
				
			}
			SQLStr="update make set ma_qty=" + rs.getDouble("md_newqty")+ " where ma_code='" +rs.getString("md_makecode") + "' ";
			baseDao.execute(SQLStr);
			
			//@add 20170205 更新工单完工状态
			SQLStr="UPDATE Make SET ma_finishstatuscode='COMPLETED',ma_finishstatus='已完工' WHERE ma_code=? AND ma_qty <= NVL(ma_madeqty,0)";
			baseDao.execute(SQLStr,rs.getString("md_makecode"));
			SQLStr = "update make set ma_finishstatuscode='PARTFI',ma_finishstatus='" + BaseUtil.getLocalMessage("PARTFI") + "' where ma_code=? and ma_madeqty>0 and ma_madeqty<ma_qty";
			baseDao.execute(SQLStr,rs.getString("md_makecode"));
			//更新销售明细单中的sd_tomakeqty
			SqlRowList field = baseDao.queryForRowSet("select ma_prodcode,ma_salecode,ma_saledetno from make where ma_code='"+rs.getString("md_makecode")+"'");
			if(field.next()){
				String ma_prodcode = field.getString("ma_prodcode");
				String ma_salecode = field.getString("ma_salecode");
				String ma_saledetno = field.getString("ma_saledetno");
				baseDao.execute(" update SALEDETAIL set sd_tomakeqty=(select sum(case when ma_statuscode='FINISH' then nvl(ma_madeqty,0) else ma_qty end) qty"
		          +" from make ma where ma_prodcode=? and ma_salecode=? and ma_saledetno=? ) where sd_code=? and sd_detno=?",ma_prodcode,ma_salecode,ma_saledetno,ma_salecode,ma_saledetno);
				baseDao.execute(" update SALEFORECASTDETAIL set sd_tomakeqty=(select sum(case when ma_statuscode='FINISH' then nvl(ma_madeqty,0) else ma_qty end) qty"
				          +" from make ma where ma_prodcode=? and ma_salecode=? and ma_saledetno=? ) where sd_code=? and sd_detno=?",ma_prodcode,ma_salecode,ma_saledetno,ma_salecode,ma_saledetno);
			}
			SQLStr="update make set ma_total=ma_qty*NVL(ma_price,0) where ma_code='" +rs.getString("md_makecode") + "' ";
			baseDao.execute(SQLStr);
		
			SQLStr = "merge into makematerial using  product on (mm_code='"+ rs.getString("md_makecode") +"' and  mm_prodcode=pr_code) when matched then update set mm_qty=round(mm_qty*1.00*"+ rs.getDouble("md_newqty") + " /" + rs.getDouble("md_qty")+"+0.4999*power(0.1,nvl(pr_precision,0)),nvl(pr_precision,0)) ";
			baseDao.execute(SQLStr);
		    SQLStr ="update makematerial set mm_qty=NVL(mm_havegetqty,0)-NVL(mm_addqty,0)+NVL(mm_totaluseqty,0) where mm_code='" + rs.getString("md_makecode") + "'and NVL(mm_havegetqty,0)-NVL(mm_addqty,0)+NVL(mm_totaluseqty,0)>mm_qty "
		    		+ "and exists (select 1 from product where mm_prodcode = pr_code and nvl(pr_putouttoint, 0) = 0)";
		    baseDao.execute(SQLStr);
		    SQLStr ="update makematerial set mm_qty=NVL(mm_havegetqty,0)-NVL(mm_addqty,0)+NVL(mm_totaluseqty,0) where mm_code='" + rs.getString("md_makecode") + "'and abs(NVL(mm_havegetqty,0))-NVL(mm_addqty,0)+NVL(mm_totaluseqty,0)>abs(mm_qty) "
		    		+ "and exists (select 1 from product where mm_prodcode = pr_code and nvl(pr_putouttoint, 0) <> 0)";
		    baseDao.execute(SQLStr);
		    
		    SQLStr = "update MakeMaterial set mm_balance=mm_qty-mm_oneuseqty*" + rs.getDouble("md_newqty") + " where mm_code='" + rs.getString("md_makecode")  + "'";
		    baseDao.execute(SQLStr);
		    SQLStr = "update MakeMaterial set mm_balance=0 where mm_code='" + rs.getString("md_makecode")  + "' and mm_balance<0 ";
		    baseDao.execute(SQLStr); 
		    double diffqty=rs.getDouble("md_qty")-rs.getDouble("md_newqty");
		    if (diffqty>0){ 
		    	baseDao.execute("merge into MakematerialReplace using(select mm_id,mm_qty from makematerial where mm_code='" + rs.getString("md_makecode")  + "' ) on (mm_id=mp_mmid) when matched then update set mp_canuseqty=mp_canuseqty-ceil(round((mp_canuseqty-(NVL(mp_haverepqty,0)+NVL(mp_returnmqty,0)-NVL(mp_addqty,0)))*"+diffqty+"*1.0/"+rs.getDouble("md_qty")+",7)) where mp_mmid in (select mm_id from makematerial where mm_code='" + rs.getString("md_makecode")  + "')");
				baseDao.execute("update MakeMaterial set mm_canuserepqty=(select sum(nvl(mp_canuseqty,0)) from MakeMaterialReplace where mp_mmid=mm_id) where  mm_code='" + rs.getString("md_makecode")  + "'");
				baseDao.execute("update makematerial set mm_canuserepqty=mm_qty where mm_code='" + rs.getString("md_makecode")  + "' and mm_canuserepqty>mm_qty");
			}
			
			if(rs.getFloat("md_newprice")>=0){
				SQLStr = "update make set ma_price=" +  rs.getDouble("md_newprice")+ " where ma_code='" +  rs.getString("md_makecode") + "' ";
				baseDao.execute(SQLStr);
				SQLStr = "update make set ma_total=round(ma_price*ma_qty,2) where ma_code='" +  rs.getString("md_makecode") + "' ";
				baseDao.execute(SQLStr);                 
			}
			if(rs.getDate("md_newplanbegindate")!=rs.getDate("md_planbegindate")){
				 SQLStr = "update make set ma_planbegindate=to_date('" + rs.getDate("md_newplanbegindate").toString().substring(0,10) + "','yyyy-MM-dd') where ma_code='" + rs.getString("md_makecode") + "' ";
				 baseDao.execute(SQLStr);        
			}
			if(rs.getDate("md_newplanenddate")!=rs.getDate("md_planenddate")){
				 SQLStr = "update make set ma_planenddate=to_date('" + rs.getDate("md_newplanenddate").toString().substring(0,10) + "','yyyy-MM-dd') where ma_code='" + rs.getString("md_makecode") + "' ";
				 baseDao.execute(SQLStr);
				 //更新ma_requiredate 
				 baseDao.execute("update make set ma_requiredate=ma_planenddate+(select nvl(pr_gdtqq,0) from product where pr_code=ma_prodcode) where ma_code='" + rs.getString("md_makecode") + "'");
			}
			if(rs.getObject("md_newwccode")!=null && !rs.getString("md_newwccode").equals("") && !rs.getString("md_newwccode").equals(rs.getString("md_wccode"))){
				 SQLStr = "update make set ma_wccode='"+rs.getString("md_newwccode")+"',ma_wcname='"+rs.getString("md_newwccode")+"' where ma_code='" + rs.getString("md_makecode") + "' ";
				 baseDao.execute(SQLStr);        
			}
			//变更付款方式和付款方式名称
			if(rs.getObject("md_newpaymentscode")!=null && !rs.getString("md_newpaymentscode").equals("") && !rs.getString("md_newpaymentscode").equals(rs.getString("md_paymentscode"))){
				 SQLStr = "update make set ma_paymentscode='"+rs.getString("md_newpaymentscode")+"',ma_payments='"+(rs.getString("md_newpayments")==null?"":rs.getString("md_newpayments"))+"' where ma_code='" + rs.getString("md_makecode") + "' ";
				 baseDao.execute(SQLStr);        
			}
			//变更币别
			if(rs.getObject("md_newcurrency")!=null && !rs.getString("md_newcurrency").equals("") && !rs.getString("md_newcurrency").equals(rs.getString("md_currency"))){
				 SQLStr = "update make set ma_currency='"+rs.getString("md_newcurrency")+"',ma_rate=(select cm_crrate from currencysmonth where cm_crname='"+rs.getString("md_newcurrency")+"' and "
						 		+ "cm_yearmonth=to_char(ma_date,'yyyymm')) where ma_code='" + rs.getString("md_makecode") + "' ";
				 baseDao.execute(SQLStr);        
			}
			int argCount = baseDao.getCountByCondition("user_tab_columns",
					"table_name='MAKECHANGEDETAIL' and column_name in ('MD_TAXRATE','MD_NEWTAXRATE')");
			if (argCount == 2) {
				baseDao.execute("update MAKE set MA_TAXRATE="+rs.getString("md_newtaxrate") + " where ma_code='"+rs.getString("md_makecode")+"' and ma_code in (select md_makecode from  MAKECHANGEDETAIL where md_id=" +rs.getInt("md_id")+ " and nvl(md_newtaxrate, 0)>=0 and md_newtaxrate<>md_taxrate)");
			}
			//如果新保税属性不为空并且不等于原保税属性,更新make.ma_bonded=case when md_newbonded='是' then -1 else 0 end 。
			if(rs.getObject("md_newbonded")!=null && !rs.getString("md_newbonded").equals("") && !rs.getString("md_newbonded").equals(rs.getString("md_oldbonded"))){
			   baseDao.execute("update make set ma_bonded='"+rs.getString("md_newbonded")+"' where ma_code='"+rs.getString("md_makecode")+"'");
			}
			//////////
			sqls.add("update make set ma_recentchangetime=sysdate where ma_code='"+rs.getString("md_makecode")+"'");			

			if(baseDao.isDBSetting("usingMakeCraft")){
				SQLStr = "merge into makecraft using  product on (mc_makecode='"+ rs.getString("md_makecode") +"' and  mc_prodcode=pr_code) when matched then update set mc_qty=round(mc_qty*1.00*"+ rs.getDouble("md_newqty") + " /" + rs.getDouble("md_qty")+"+0.4999*power(0.1,nvl(pr_precision,0)),nvl(pr_precision,0)) ";
				baseDao.execute(SQLStr);}
		}	
		baseDao.execute("update makechange set mc_sendstatus='待上传' where mc_id="+mc_id+" and nvl(mc_tasktype,' ')='委外加工变更单'");		
		//执行审核操作
		baseDao.audit("MakeChange", "mc_id=" + mc_id, "mc_status", "mc_statuscode", "mc_auditdate", "mc_auditman");
		/**
		 * 反馈编号：2017020686
		 * @author wsy
		 * 委外变更单审核后更新委外加工单最近变更日期
		 */
		baseDao.execute(sqls);
		String ids = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(ma_id) from makechangedetail left join make on ma_code=md_makecode where md_mcid=?", String.class, mc_id);
		if(ids != null){
			makeDao.updateMakeGetStatus(ids);
		}
		//记录操作
		baseDao.logger.audit(caller, "mc_id", mc_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{mc_id});
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void resAuditMakeChange(String caller,int mc_id) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("MakeChange", "mc_statuscode", "mc_id=" + mc_id);
		StateAssert.resAuditOnlyAudit(status);
		//变更业务处理  by zyl 2013-7-2
		SqlRowList rs = baseDao.queryForRowSet("select md_id ,md_detno,md_makecode,md_newqty,md_newplanbegindate,md_newplanenddate,md_newprice,md_wccode,md_newwccode from makechangedetail where md_mcid="+mc_id);
		String SQLStr="";
		while (rs.next()) {
			SQLStr="update make set ma_qty=" + rs.getDouble("md_qty")+ " where ma_code='" +rs.getString("md_makecode") + "' ";
			baseDao.execute(SQLStr);
			SQLStr = "update makematerial set mm_qty=(case when round(mm_qty,1)=round(mm_qty,0) then round(mm_qty*1.00*" + rs.getDouble("md_qty") + " /" + rs.getDouble("md_newqty") + ",0) else round(mm_qty*1.00*" + rs.getDouble("md_qty") + " / " + rs.getDouble("md_newqty") + ",5) end) where mm_code='" + rs.getString("md_makecode") + "' ";
			baseDao.execute(SQLStr);
			SQLStr = "update MakeMaterial set mm_balance=mm_qty-mm_oneuseqty*" + rs.getDouble("md_newqty") + " where mm_code='" + rs.getString("md_makecode")  + "'";
		    baseDao.execute(SQLStr);
		    SQLStr = "update MakeMaterial set mm_balance=0 where mm_code='" + rs.getString("md_makecode")  + "' and mm_balance<0 ";
		    baseDao.execute(SQLStr);
			if(rs.getFloat("md_newprice")>=0){
				SQLStr = "update make set ma_price=" +  rs.getDouble("md_oldprice")+ " where ma_code='" +  rs.getString("md_makecode") + "' ";
				baseDao.execute(SQLStr);
				SQLStr = "update make set ma_total=round(ma_price*ma_qty,2) where ma_code='" +  rs.getString("md_makecode") + "' ";
				baseDao.execute(SQLStr);                 
			}
			if(rs.getDate("md_newplanbegindate")!=rs.getDate("md_planbegindate")){
				 SQLStr = "update make set ma_planbegindate=to_date('" + rs.getDate("md_planbegindate").toString().substring(0,10) + "','yyyy-MM-dd') where ma_code='" + rs.getString("md_makecode") + "' ";
				 baseDao.execute(SQLStr);        
			}
			if(rs.getDate("md_newplanenddate")!=rs.getDate("ma_planenddate")){
				 SQLStr = "update make set ma_planenddate=to_date('" + rs.getDate("md_planenddate").toString().substring(0,10) + "','yyyy-MM-dd') where ma_code='" + rs.getString("md_makecode") + "' ";
				 baseDao.execute(SQLStr);        
			}
			if(rs.getObject("md_newwccode")!=null && !rs.getString("md_newwccode").equals("") && !rs.getString("md_wccode").equals(rs.getString("md_newwccode"))){
				 SQLStr = "update make set ma_wccode='"+rs.getString("md_wccode")+"',ma_wcname='"+rs.getString("md_wccode")+"' where ma_code='" + rs.getString("md_makecode") + "' ";
				 baseDao.execute(SQLStr);        
			}
			 
		} 
		//执行反审核操作
		baseDao.updateByCondition("MakeChange", "mc_statuscode='ENTERING',mc_status='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "mc_id=" + mc_id);
		//记录操作
		baseDao.logger.resAudit(caller, "mc_id", mc_id);
	}
	
	@Override
	public void submitMakeChange(String caller, int mc_id) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MakeChange", "mc_statuscode", "mc_id=" + mc_id);
		StateAssert.submitOnlyEntering(status);
		//相同工单不能录入多行变更
		SqlRowList rs = baseDao.queryForRowSet("select * from (select md_makecode,count(1) n  from makechangedetail where md_mcid="+mc_id+" group by md_makecode) where n>1");
		while (rs.next()) {
			BaseUtil.showError("工单:"+rs.getString("md_makecode")+"重复录入");
		}
		rs = baseDao.queryForRowSet("select mc_code,md_makecode from makechange,makechangedetail where md_mcid=mc_id and mc_statuscode in ('ENTERING','COMMITED') and md_makecode in (select md_makecode from makechangedetail where md_mcid="+mc_id+") and md_mcid<>"+mc_id);
		while (rs.next()) {
			BaseUtil.showError("工单:"+rs.getString("md_makecode")+"存在另一张未审核的生产变更单:"+rs.getString("mc_code"));
		}
		checkAll(mc_id, caller);//检测是否允许变更
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{mc_id});		
		SaveOldmakedata(mc_id);
		//执行提交操作
		baseDao.submit("MakeChange",  "mc_id=" + mc_id, "mc_status", "mc_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "mc_id", mc_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{mc_id});
	}
	
	@Override
	public void resSubmitMakeChange(String caller,int mc_id) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MakeChange", "mc_statuscode", "mc_id=" + mc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[]{mc_id});		//执行反提交操作
		baseDao.updateByCondition("MakeChange", "mc_statuscode='ENTERING',mc_status='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "mc_id=" + mc_id);
		//记录操作
		baseDao.logger.resSubmit(caller, "mc_id", mc_id);
		handlerService.afterResSubmit(caller, new Object[]{mc_id});
	}
	 
	public void SaveOldmakedata(int mc_id) {
		String SQLStr="";
		SQLStr= "update makeChangedetail set md_qty=(select max(ma_qty) from make where ma_code=md_makecode) where md_mcid=" + mc_id ;
		baseDao.execute(SQLStr);
		SQLStr= "update makeChangedetail set md_oldprice=(select max(ma_price) from make where ma_code=md_makecode) where md_mcid=" + mc_id ;
		baseDao.execute(SQLStr);
		SQLStr= "update makeChangedetail set md_planbegindate=(select max(ma_planbegindate) from make where ma_code=md_makecode) where md_mcid=" + mc_id ;
		baseDao.execute(SQLStr);
		SQLStr= "update makeChangedetail set md_planenddate=(select max(ma_planenddate) from make where ma_code=md_makecode) where md_mcid=" + mc_id ;
		baseDao.execute(SQLStr);
		SQLStr= "update makeChangedetail set md_newplanenddate=(select max(ma_planenddate) from make where ma_code=md_makecode) where md_mcid=" + mc_id +" and md_newplanenddate is null ";
		baseDao.execute(SQLStr);
		SQLStr= "update makeChangedetail set md_newplanbegindate=(select max(ma_planbegindate) from make where ma_code=md_makecode) where md_mcid=" + mc_id +" and md_newplanbegindate is null ";
		baseDao.execute(SQLStr);
		SQLStr= "update makeChangedetail set md_newqty=(select max(ma_qty) from make where ma_code=md_makecode) where md_mcid=" + mc_id  + "  and nvl(md_newqty,0)=0 ";
		baseDao.execute(SQLStr);
		SQLStr= "update makeChangedetail set md_newprice=to_char((select max(ma_price) from make where ma_code=md_makecode),'fm999990.99999999') where md_mcid=" + mc_id +" and nvl(md_newprice,0)<0 ";
		baseDao.execute(SQLStr);
	}
	private void checkAll(Integer mc_id, String caller){		
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(md_makecode) from makeChangedetail left join make on ma_code=md_makecode where md_mcid=? and nvl(md_newqty,0)<nvl(ma_madeqty,0)",
						String.class, mc_id);
		if (dets != null) {
			BaseUtil.showError("工单更新后的数量小于已完工的数量，不能进行更新操作！制造单号：" + dets);
		}
		SqlRowList rs = baseDao.queryForRowSet(
				"Select * from makeChangedetail left join make on ma_code=md_makecode left join makekind on mk_name=ma_kind where md_mcid=?", mc_id);
		float newqty = 0;
		float oldqty = 0;
		while (rs.next()) {
			if (rs.getInt("ma_id")>0){ 
				makeDao.refreshTurnQty(rs.getInt("ma_id"),0); 
			} 
			newqty = Float.parseFloat(rs.getString("md_newqty").toString());
			oldqty = Float.parseFloat(rs.getString("md_oldqty").toString());
			if (rs.getString("ma_statuscode") == "FINISH") {
				BaseUtil.showError("工单[" + rs.getString("md_makecode") + "]已结案");
			}
			if (newqty > 0 && newqty < oldqty && "S".equals(rs.getString("mk_type"))) {//@update 20170207 标准类型工单才限制
				SqlRowList rs1 = baseDao.queryForRowSet("Select count(1)n,wm_concat(mm_detno) mm_detno from makematerial left join product on MM_PRODCODE = pr_code where mm_maid=?"
						+ " and NVL(pr_putouttoint, 0) = 0"
						+ " and ((nvl(mm_havegetqty,0)-nvl(mm_scrapqty,0)+NVL(mm_totaluseqty,0)-1>=mm_qty*"
						+ newqty + "*1.00/" + oldqty
						+ " and round(mm_qty,0)=round(mm_qty,3)) or (nvl(mm_havegetqty,0)-nvl(mm_scrapqty,0)+NVL(mm_totaluseqty,0)>mm_qty*"
						+ newqty + "*1.00/" + oldqty + " and round(mm_qty,0)<>round(mm_qty,3))) "
								+ "and nvl(mm_oneuseqty,0)*"+rs.getDouble("ma_qty")+"-0.01<=mm_qty",rs.getInt("ma_id"));
				if (rs1.next()) {
					if (rs1.getInt("n")>0){
						BaseUtil.showError("工单[" + rs.getString("md_makecode") + "],序号[" + rs1.getString("mm_detno")
			 					+ "]已领数量+已转领料数大于变更后的需求数, 不能变更!");
					} 
				}
				//针对用料表数量小于单位用量*工单数
				rs1 = baseDao.queryForRowSet("Select count(1)n,wm_concat(mm_detno) mm_detno from makematerial left join product on MM_PRODCODE = pr_code where mm_maid=?"
						+ " and NVL(pr_putouttoint, 0) = 0"
						+" and nvl(mm_oneuseqty,0)*"+rs.getDouble("ma_qty")+"-0.01>mm_qty and nvl(mm_havegetqty,0)-NVL(mm_scrapqty,0)+NVL(mm_totaluseqty,0)>nvl(mm_oneuseqty,0)*"+newqty,rs.getInt("ma_id"));
				if (rs1.next()) {
					if (rs1.getInt("n")>0){
						BaseUtil.showError("工单[" + rs.getString("md_makecode") + "],序号[" + rs1.getString("mm_detno")
								+ "]已领数量+已转领料数大于变更后的需求数, 不能变更!");
					} 
				}
			}
		}
		if("MakeChange!OSChange".equals(caller)){
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(md_makecode) from makeChangedetail where md_mcid=? and nvl(md_newprice,0)<>nvl(md_oldprice,0) and exists (select pd_ordercode from prodiodetail where pd_ordercode=md_makecode and pd_piclass='委外验收单')",
							String.class, mc_id);
			if (dets != null) {
				BaseUtil.showError("委外单已转入委外验收单，不能进行单价变更！委外单号：" + dets);
			}
		}else if("MakeChange!Change".equals(caller)){
			/**
			 * @tips 新增 限制 制造单变更单的新数量不允许小于已转检验单数量
			 * @data  2016年10月9日 上午11:22:42
			 */
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(md_makecode) from makeChangedetail left join make on ma_code=md_makecode where md_mcid=? and md_newqty<nvl(ma_toquaqty,0)",
							String.class, mc_id);
			if (dets != null) {
				BaseUtil.showError("制造单新数量不允许小于已转检验单数量！制造单号：" + dets);
			}
		}
		
	}
	
}
