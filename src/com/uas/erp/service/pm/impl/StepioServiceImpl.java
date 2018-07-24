package com.uas.erp.service.pm.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.MakeScrapService;
import com.uas.erp.service.pm.StepioService;

@Service
public class StepioServiceImpl implements StepioService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private	MakeScrapService makeScrapService;
	
	@Override
	public void saveStepio(String formStore, String caller,String param) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Stepio", "si_code='" + store.get("si_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Stepio", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		if(store.get("st_Class")!=null && "工序报废".equals(store.get("st_Class"))){
			baseDao.execute("update stepio set st_qty=(select sum(sd_qty) from stepiodetail where sd_siid=si_id) where si_id="+store.get("si_id"));
		}
		if("Stepio!CraftScrap".equals(caller)){
			for (Map<Object, Object> map : grid) {
				map.put("sd_id", baseDao.getSeqId("STEPIODETAIL_SEQ"));
			}
			List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "StepIOdetail");			
			baseDao.execute(gridSql);
		}
		try{
			//记录操作
			baseDao.logger.save(caller, "si_id", store.get("si_id"));
		} catch (Exception e) {
			e.printStackTrace();
		} 
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void updateStepioById(String formStore, String  caller,String param,String param2) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param);
		//只能修改[在录入]的采购单资料!
		 Object[] ob=baseDao.getFieldsDataByCondition("stepio",new String[] {"si_statuscode","si_code","st_class","si_makecode","st_outno","si_indate"},
				 "si_id="+store.get("si_id"));		
		StateAssert.updateOnlyEntering(ob[0]);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Stepio", "si_id");	
		baseDao.execute(formSql);
		
		if(store.get("st_Class")!=null && "工序报废".equals(store.get("st_Class"))){
			baseDao.execute("update stepio set st_qty=(select sum(sd_qty) from stepiodetail where sd_siid=si_id) where si_id="+store.get("si_id"));
		}
		if("Stepio!CraftScrap".equals(caller)){
			List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(grid, "StepIOdetail", "sd_id");
			for (Map<Object, Object> map : grid) {
				if (NumberUtil.isEmpty(map.get("sd_id"))) {
					map.put("sd_id", baseDao.getSeqId("STEPIODETAIL_SEQ"));
					gridSql.add(SqlUtil.getInsertSqlByMap(map,  "StepIOdetail"));
				}
			} 
			baseDao.execute(gridSql);
		}
		
		//记录操作
		baseDao.logger.update(caller, "si_id", store.get("si_id"));
		//更新上次采购价格、供应商
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
	}

	@Override
	public void deleteStepio(int si_id, String  caller) {
		Object status = baseDao.getFieldDataByCondition("Stepio", "si_statuscode", "si_id=" + si_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{si_id});
		//删除
		baseDao.execute("delete makeclash where (mc_code,mc_class) in (select si_code,st_class from stepio where si_id="+si_id+")");
		baseDao.deleteById("Stepio", "si_id", si_id);
		baseDao.deleteById("StepIODetail", "sd_siid",si_id);         
		//记录操作
		baseDao.logger.delete(caller, "si_id", si_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{si_id});
	}


	@Override
	public void submitStepio(int si_id, String  caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object[] ob = baseDao.getFieldsDataByCondition("Stepio",new String[]{ "si_statuscode","si_code","st_class"}, "si_id=" + si_id);
		StateAssert.submitOnlyEntering(ob[0]);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller,new Object[]{si_id});
		int count1=baseDao.getCount("select count(1) from makeclash where  mc_code='"+ob[1]+"' and mc_class = '"+ob[2]+"'");
		if(count1 == 0 && !"工序退制".equals(ob[2])){
			if("工序报废".equals(ob[2].toString())){
				int count2=baseDao.getCount("select count(1) from stepio,makecraft where si_makecode=mc_makecode and st_outno=mc_detno and si_id=" +si_id+ "  and si_prodcode=mc_prodcode ");
				if (count2>0) {
					BaseUtil.showError("请先设置冲减项目");
				}
			}else{
				BaseUtil.showError("请先设置冲减项目");
			}						
		}
		if("Stepio!CraftScrap".equals(caller)){
			int count=baseDao.getCount("select  count(1) from stepio where si_id="+si_id+" and nvl(si_qty,0)=(select sum(nvl(sd_qty,0)) from Stepiodetail where sd_siid=si_id)");
			if(count == 0 ){
				BaseUtil.showError("报废单明细数量和与主表报废数量不等，不能提交！");
			}
		}
		// 调用提交校验存储过程 
		String res = baseDao.callProcedure("sp_CommiteStepIO_Commit",new Object[] { ob[1].toString(), ob[2].toString() });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
		
		baseDao.submit("Stepio", "si_id=" + si_id, "si_status", "si_statuscode");		
		//记录操作
		baseDao.logger.submit(caller, "si_id", si_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller,new Object[]{si_id});
	}

	@Override
	public void resSubmitStepio(int si_id, String  caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Stepio", "si_statuscode", "si_id=" + si_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller,  new Object[]{si_id});
		baseDao.resOperate("Stepio", "si_id=" + si_id, "si_status", "si_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "si_id", si_id);
		handlerService.afterResSubmit(caller,  new Object[]{si_id});
	}

	@Override
	public Map<String, Object> getClashInfo(String caller, String con) {
		Map<String, Object> map = new HashMap<String, Object>();
		int saveclash=0,setclash=0;
		double mconmake=0,mcremain=0,clashqty=0;
		Object[] ob=baseDao.getFieldsDataByCondition("stepio",new String[] {"si_statuscode","si_code","st_class","si_makecode","st_outno","si_prodcode","case when nvl(SI_STATUSCODE,' ')='POSTED' then 0 else si_qty end as si_qty","si_indate"},con);
		Object onmake= baseDao.getFieldDataByCondition("makeCraft", "mc_onmake", "mc_makecode='"+(ob[3]==null?"":ob[3])+"' and  mc_detno='" + (ob[4]==null?0:ob[4]) + "'");
		Object reamin=baseDao.getFieldDataByCondition("makematerial left join makecraft on mc_makecode=mm_code and mc_code=mm_mdcode",
				"min( floor((nvl(mm_havegetqty,0)+nvl(mm_stepinqty,0)-nvl(mm_clashqty,0)) /mm_oneuseqty)) ", 
				"mm_code='"+(ob[3]==null?"":ob[3])+"' and mc_detno="+(ob[4]==null?0:ob[4])+" and mm_qty>0 and mm_oneuseqty>0 and mm_supplytype='PUSH'"); 
		Object clash=baseDao.getFieldDataByCondition("makeclash", "mc_clashqty", "mc_class='"+ob[2]+"' and mc_code='"+ob[1]+"' and  mc_clashclass='套料'") ;
		mconmake= onmake==null? 0 : Double.parseDouble(onmake.toString());
		mcremain= reamin==null? 0 : Double.parseDouble(reamin.toString());
		clashqty= clash==null?   0 : Double.parseDouble(clash.toString());
		
		//报废情况
		if("Stepio!CraftScrap".equals(caller)){
			Object mccode = baseDao.getFieldDataByCondition("makecraft", "mc_code", "mc_makecode='"+ob[3] +"' and mc_detno='" +ob[4]+ "'");
			Object scrapqty=null;
			Object orderqty=null;
			if(baseDao.checkIf("makematerial", "mm_mdcode='"+ob[4]+"' and mm_prodcode='"+ob[5]+"'")){
				Object[] ob1=baseDao.getFieldsDataByCondition("makematerial",new String []{"nvl(sum(mm_scrapqty),0)+"+Double.parseDouble(ob[6].toString())+" as scrapqty","sum(mm_qty) as orderqty"},  "mm_mdcode='"+mccode +"' and mm_prodcode='"+ob[5]+"'");			
				scrapqty=ob1[0];
				orderqty=ob1[1];
			}else{
				Object[] ob1=baseDao.getFieldsDataByCondition("makecraft",new String []{"nvl(sum(mc_scrapqty),0)+"+Double.parseDouble(ob[6].toString())+" as scrapqty","nvl(max(mc_qty),0) as orderqty"},  "mc_code='"+mccode +"'");			
				scrapqty=ob1[0];
				orderqty=ob1[1];
			}
			Object tfinishqty=baseDao.getFieldDataByCondition("prodiodetail left join prodinout on pd_piid=pi_id", "nvl(sum(pd_inqty),0)", "pd_jobcode='"+mccode+"' and to_char(pi_date,'yyyy-mm-dd')='"+ob[7].toString()+"'");
		    String lossrateText = "订单数量：" + orderqty.toString() + ",累积报废:" + scrapqty.toString() + ",报废率:" + (100 * NumberUtil.formatDouble(Double.parseDouble(scrapqty.toString()) / Double.parseDouble(orderqty.toString()) , 4))  + "%     当天完工数:" + tfinishqty ;
		    if ( Double.parseDouble(tfinishqty.toString())>0 ){
		    	lossrateText +=" 报废率:"+(100 * NumberUtil.formatDouble(Double.parseDouble(ob[6].toString())  / Double.parseDouble(tfinishqty.toString()), 4)) + "%";
		    } 	   
			map.put("lossrateText",lossrateText);
			if(!baseDao.checkIf("makecraft", "mc_code='"+mccode+"' and mc_detno='"+ob[4]+"' and mc_prodcode='"+ob[5]+"'")){
				map.put("notclash","Y");
			}
		}
		
		int count=baseDao.getCount("select count(1) from makeclash where mc_code='"+ob[1]+"' and mc_class='"+ob[2]+"'");
		if(count==0){
			saveclash=1;
			setclash=0;
			clashqty=mcremain;
		}else{
			saveclash=0;
			setclash=1;
		}
		if(!"ENTERING".equals(ob[0])){
			saveclash=0;
			setclash=0;
		}
		map.put("saveclash",saveclash );
		map.put("setclash",setclash );
		map.put("mconmake",mconmake );
		map.put("mcremain",mcremain );
		map.put("clashqty",clashqty );
		return map;
	}

	@Override
	public void setclash(int id, String caller) {
		Object[] ob=baseDao.getFieldsDataByCondition("stepio",new String[] {"si_statuscode","si_code","st_class","si_makecode","st_outno"},"si_id="+id);
		if("ENTERING".equals(ob[0])){
			baseDao.execute("delete makeclash where mc_code='"+ob[1]+"' and mc_class='"+ob[2]+"'");
		}
	}

	@Override
	public void saveclash(String caller, String data, int id, int clashqty) {
		Object[] ob=baseDao.getFieldsDataByCondition("stepio",new String []{"si_statuscode","si_code","st_class","si_makecode","st_outno"}, "si_id="+id);
		if(data!=null && !"null".equals(data)){
			baseDao.execute("delete makeclash where mc_code='"+ob[1]+"' and mc_class='"+ob[2]+"'");  
			List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
			for (Map<Object, Object> map : maps) {
				int count=baseDao.getCount("Select count(1) from stepIO where si_qty-nvl(st_clashqty,0)>=" +(map.get("st_inqty")==null?0:map.get("st_inqty"))+ " and "
						+ "si_code='" +map.get("si_code") + "' and si_status='已过账'");
				if(count>0){
					baseDao.execute("insert into makeclash(mc_id,mc_code,mc_class,mc_clashclass,mc_clashcode,mc_clashqty,mc_prodcode) "
							+ "values(makeclash_seq.nextval,'"+ob[1]+"','"+ob[2]+"','"+map.get("st_class")+"','"+map.get("si_code")+"','"+map.get("st_inqty")+"','"+map.get("si_prodcode")+"')");
				}    
			}
		}
		if(clashqty>0){
			baseDao.execute("insert into makeclash(mc_id,mc_code,mc_class,mc_clashclass,mc_clashcode,mc_clashqty,mc_prodcode) "
					+ "values(makeclash_seq.nextval,'" + ob[1] + "','"+ob[2]+"','套料','"+ob[1]+"',"+clashqty+",'')");
		}		
	}

	@Override
	public void postStepIO(int id, String caller) {
		Object[] ob=baseDao.getFieldsDataByCondition("stepio",new String []{"si_statuscode","si_code","st_class"}, "si_id="+id);
		if(!"COMMITED".equals(ob[0])){
			BaseUtil.showError("已提交才能过账");
		}		
		// 过账前的其它逻辑
		handlerService.beforePost(caller,  id);
		// 调用过账存储过程 
		String res = baseDao.callProcedure("sp_CommiteStepIO",new Object[] { ob[1].toString(), ob[2].toString() });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
		baseDao.updateByCondition("stepio", "si_statuscode='POSTED',Si_status='" + BaseUtil.getLocalMessage("POSTED")
				+ "',st_postman='" + SystemSession.getUser().getEm_name() + "',st_postdate=sysdate", "si_id=" + id);
		// 记录操作
		baseDao.logger.post(caller, "si_id", id);
		// 执行过账后的其它逻辑
		handlerService.afterPost(caller, id);
		
	}

	@Override
	public void resPostStepIO(int id, String caller) {
		Object[] ob=baseDao.getFieldsDataByCondition("stepio",new String[] {"si_statuscode","si_code","st_class","si_makecode","st_outno"},"si_id="+id);
		if("POSETD".equals(ob[0])){
			BaseUtil.showError("单据未过账，不能反过账");
		}
		// 反过账前的其它逻辑
		handlerService.beforeResPost(caller, id);
		// 调用反过账存储过程 
		String res = baseDao.callProcedure("sp_UnCommiteStepIO",new Object[] { ob[1].toString(), ob[2].toString() });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
		baseDao.updateByCondition("stepio", "si_statuscode='ENTERING',si_status='" + BaseUtil.getLocalMessage("ENTERING")
				+ "',st_postman=null,st_postdate=null", "si_id=" +  id);		
		// 记录操作
		baseDao.logger.resPost(caller, "si_id", id);
	}
	@Transactional
	@Override
	public void batchSumbitStepio(String datas, String caller) {
		List<Map<Object,Object>> list = BaseUtil.parseGridStoreToMaps(datas);
		for(Map<Object,Object> map : list){
			Object si_id = map.get("si_id");
			submitStepio(Integer.parseInt(si_id.toString()),caller);
		}
	}
	@Override
	public void batchPostStepio(String datas, String caller) {
		List<Map<Object,Object>> list = BaseUtil.parseGridStoreToMaps(datas);
		for(Map<Object,Object> map : list){
			Object si_id = map.get("si_id");
			postStepIO(Integer.parseInt(si_id.toString()),caller);
		}
	}

}
