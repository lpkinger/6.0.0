package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.BOMSetChangeService;

@Service
public class BOMSetChangeServiceImpl implements BOMSetChangeService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private BomsetServiceImpl bomsetServiceImpl;
	@Override
	public void saveBOMSetChange(String formStore,String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BOMSetChange", "bc_code='" + store.get("bc_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}		
		//判断同一个配套编号不允许同时存在两张未审核单据
		bool = baseDao.checkByCondition("BOMSetChange", "bc_bscode='" + store.get("bc_bscode") + "' and nvl(bc_statuscode,' ')<>'AUDITED' and bc_code<>'"+store.get("bc_code")+"'");
		if(!bool){
			BaseUtil.showError("配套表编号已存在一张未审核的变更单");
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller,new Object[]{store});
		//保存BOMSetChange
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BOMSetChange", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//保存BOMSetChangeDetail
		List<String> gridSql = SqlUtil.getInsertSqlbyList(gstore, "BOMSetChangeDetail", "bcd_id");
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "bc_id", store.get("bc_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller,new Object[]{store,gstore});
	}

	@Override
	public void updateById(String formStore,String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("BOMSetChange", "bc_statuscode", "bc_id=" + store.get("bc_id"));
		StateAssert.updateOnlyEntering(status);
		//判断同一个配套编号不允许同时存在两张未审核单据
		boolean bool = baseDao.checkByCondition("BOMSetChange", "bc_bscode='" + store.get("bc_bscode") + "' and nvl(bc_statuscode,' ')<>'AUDITED' and bc_code<>'"+store.get("bc_code")+"'");
		if(!bool){
			BaseUtil.showError("配套表编号已存在一张未审核的变更单");
		}
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller,new Object[]{store,gstore});
		//修改BomSet
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BOMSetChange", "bc_id");
		baseDao.execute(formSql);
		//修改BomSetDetail
		List<String> gridSql = SqlUtil.getInsertOrUpdateSqlbyGridStore(gstore, "BomSetChangeDetail", "bcd_id");
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "bc_id", store.get("bc_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store,gstore});
	}

	@Override
	public void deleteBOMSetChange(int bc_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("BOMSetChange", "bc_statuscode", "bc_id=" + bc_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{bc_id});
		//删除BOMSetChange
		baseDao.deleteById("BOMSetChange", "bc_id", bc_id);
		//删除BOMSetChangeDetail
		baseDao.deleteById("BOMSetChangedetail", "bcd_bcid", bc_id);
		//记录操作
		baseDao.logger.delete(caller, "bc_id", bc_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{bc_id});
	}

	@Override
	@Transactional
	public void auditBOMSetChange(int bc_id, String caller) {		
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("BOMSetChange", "bc_statuscode", "bc_id=" + bc_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{bc_id});		
		//执行审核操作
		baseDao.audit("BOMSetChange", "bc_id=" + bc_id, "bc_status", "bc_statuscode","bc_auditdate","bc_auditman");
		String sql = "";
		//执行变更回写数据至表bomsetdetail 中
		SqlRowList rs = baseDao.queryForRowSet("select * from bomsetchange left join bomsetchangedetail on bcd_bcid=bc_id left join bomset on bs_code=bc_bscode where bc_id="+bc_id+" and nvl(bcd_didstatuscode,' ') not in ('EXECUTED','FAIL')");
		while(rs.next()){
			Object ob = null;
			if("ADD".equals(rs.getString("bcd_type"))){//新增
				//新增的是否已经存在
				Object [] obs = baseDao.getFieldsDataByCondition("BOMSet left join BOMSetDetail on bsd_bsid=bs_id", new String[]{"bsd_id","bsd_usestatus"}, "bs_code='"+rs.getString("bc_bscode")+"' and bsd_prodcode='"+rs.getString("bcd_prodcode")+"' and bsd_mothercode='"+rs.getString("bcd_mothercode")+"'");
			    if(obs != null){
			    	if(obs[1] != null && !obs[1].equals("") && obs[1].toString().equals("DISABLE")){//禁用的重新启用
			    		sql = "update bomsetdetail set bsd_usestatus='',bsd_validdate=sysdate,bsd_remark=bsd_remark||'重新启用,单号:"+rs.getString("bc_code")+"' where bsd_id=" +Integer.valueOf(obs[0].toString());				 
				    	baseDao.execute(sql);
			    		sql = "update bomsetchangedetail set bcd_didstatuscode='EXECUTED',bcd_didstatus='已执行' ,bcd_diddate=sysdate where bcd_id=" + rs.getInt("bcd_id");
				    	baseDao.execute(sql);
				    	//判断是否存在嵌套
						bomsetServiceImpl.checkNest(rs.getString("bcd_prodcode"), 0);
			    	}else{
				    	sql = "update bomsetchangedetail set bcd_didstatuscode='FAIL',bcd_didstatus='执行失败'  where bcd_id=" + rs.getInt("bcd_id");
						baseDao.execute(sql);
			    	}
			    }else{			    	
					sql = "insert into bomsetdetail (bsd_id,bsd_bsid,bsd_prodcode,bsd_mothercode,bsd_detno,bsd_remark,bsd_code) values(BOMSETDETAIL_SEQ.nextval,"+rs.getInt("bs_id")+",'"+rs.getString("bcd_prodcode")+"','"+rs.getString("bcd_mothercode")+"', nvl((select max(bsd_detno) from bomsetdetail where bsd_bsid="+rs.getString("bs_id")+"),0)+1, '增加,单号:'||'"+rs.getString("bc_code")+"','"+rs.getString("bc_bscode")+"')" ;				 
			    	baseDao.execute(sql);
			    	//判断是否存在嵌套
					bomsetServiceImpl.checkNest(rs.getString("bcd_prodcode"), 0);
			    	sql = "update bomsetchangedetail set bcd_didstatuscode='EXECUTED',bcd_didstatus='已执行',bcd_diddate=sysdate where bcd_id=" + rs.getInt("bcd_id");
			    	baseDao.execute(sql);
			    }			   
			}else if("DISABLE".equals(rs.getString("bcd_type"))){//禁用				
				ob = baseDao.getFieldDataByCondition("bomset left join bomsetdetail on bsd_bsid=bs_id", "bsd_id", "bs_code='"+rs.getInt("bc_bscode")+"' and nvl(bs_statuscode,' ')='AUDITED' and nvl(bsd_usestatus,' ')<>'DISABLE' and bsd_detno="+rs.getString("bcd_bsdetno")+" and bsd_prodcode='"+rs.getString("bcd_prodcode")+"'");
			    if(ob != null){//执行
			    	sql = "update bomsetdetail set bsd_usestatus='DISABLE',bsd_disabledate=sysdate,bsd_remark=bsd_remark||'禁用,单号:"+rs.getString("bc_code")+"' where bsd_id=" +Integer.valueOf(ob.toString());				 
			    	baseDao.execute(sql);
			    	sql = "update bomsetchangedetail set bcd_didstatuscode='EXECUTED',bcd_didstatus='已执行',bcd_diddate=sysdate where bcd_id=" + rs.getInt("bcd_id");
			    	baseDao.execute(sql);
			    }else{//执行失败
			    	sql = "update bomsetchangedetail set bcd_didstatuscode='FAIL',bcd_didstatus='执行失败' where bcd_id=" + rs.getInt("bcd_id");
					baseDao.execute(sql);
			    }
		    }	
		}		
		//记录操作
		baseDao.logger.audit(caller, "bc_id", bc_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller,new Object[]{bc_id});
	}

	@Override
	public void submitBOMSetChange(int bc_id, String caller) {		
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("BOMSetChange", "bc_statuscode", "bc_id=" + bc_id);
		StateAssert.submitOnlyEntering(status); 	
		//更新操作名称
		UpdateChangeTypeCode(bc_id,caller);
		//判断配套表编号是否存在
		Object bs_id = baseDao.getFieldDataByCondition("bomsetchange left join bomset on bc_bscode=bs_code", "bs_id", "NVL(bs_statuscode,' ' )= 'AUDITED' and bc_id="+bc_id);
		if(bs_id == null){
			BaseUtil.showError("配套表编号不存在或未审核");
		}
		SqlRowList rs = baseDao.queryForRowSet("select bcd_detno from bomsetchangedetail left join product on pr_code=bcd_prodcode where bcd_bcid="+bc_id+" and pr_statuscode IS null");
		if (rs.next()) {
			BaseUtil.showError("序号:"+rs.getString("bcd_detno")+"物料编号不存在");
		}
		rs = baseDao.queryForRowSet("select bcd_detno from bomsetchangedetail left join product on pr_code=bcd_mothercode where bcd_bcid="+bc_id+" and pr_statuscode IS null");
		if (rs.next()) {
			BaseUtil.showError("序号:"+rs.getString("bcd_detno")+"母件编号不存在");
		}
		rs = baseDao.queryForRowSet("select bc_id from bomsetchange left join product on pr_code=bc_mothercode where bc_id="+bc_id+" and pr_statuscode IS null");
		if (rs.next()) {
			BaseUtil.showError("母件编号不存在");
		}
		rs = baseDao.queryForRowSet("select bcd_detno from bomsetchangedetail where bcd_bcid="+bc_id+" and bcd_prodcode=bcd_mothercode");
		if (rs.next()) {
			BaseUtil.showError("序号:"+rs.getString("bcd_detno")+"物料编号和母件编号相同");
		}
		rs = baseDao.queryForRowSet("select wmsys.wm_concat(a.bcd_detno) dt  from bomsetchangedetail a"
                                   +" where exists(select 1 from bomsetchangedetail b where a.bcd_bcid = b.bcd_bcid"
                                   +" and b.bcd_prodcode = a.bcd_prodcode and b.bcd_mothercode=a.bcd_mothercode and a.bcd_detno <> b.bcd_detno)"
                                   +" and a.bcd_bcid="+bc_id);
		if (rs.next()) {
			if(rs.getString("dt") != null){
			   BaseUtil.showError("序号:"+rs.getString("dt")+",【不同明细行】物料编号和母件编号重复");
			}
		}
		//操作类型
		rs = baseDao.queryForRowSet("select count(1)c,wm_concat(bcd_detno)bcd_detno from bomsetchangedetail where bcd_bcid='" + bc_id + "' and NVL(bcd_type,' ')=' '");
		if (rs.next()) {
			if (rs.getInt("c")>0) {
				BaseUtil.showError("序号[" + rs.getString("bcd_detno") + "]必须填写操作类型!");
			} 
		}  
		//禁用必填配套表需要
		rs = baseDao.queryForRowSet("select count(1) c ,wm_concat(bcd_detno) bcd_detno from bomsetchangedetail where bcd_bcid="+bc_id +" and bcd_type='DISABLE' and bcd_bsdetno is null");
		if (rs.next()) {
			if (rs.getInt("c")>0) {
				BaseUtil.showError("序号[" + rs.getString("bcd_detno") + "]禁用的明细必须选择配套表序号!");
			} 
		}  		
		//查看禁用的是否已经禁用
		rs = baseDao.queryForRowSet("select count(1)c,wm_concat(bcd_detno) bcd_detno from bomsetchangedetail where bcd_bcid="+bc_id+" and bcd_type='DISABLE' and bcd_bsdetno not in(select bsd_detno from bomsetdetail where bsd_bsid="+bs_id+" and nvl(bsd_usestatus,' ')<>'DISABLE') and rownum<10 ");
		if (rs.next()) {
			if (rs.getInt("c")>0) {
				BaseUtil.showError("序号[" + rs.getString("bcd_detno") + "]禁用的明细在配套表中不存在或者已经禁用!");
			} 
		}  				
		//判断新增是否已经存在
		rs = baseDao.queryForRowSet("select count(1)c,wm_concat(bcd_detno) bcd_detno from bomsetchangedetail where bcd_bcid="+bc_id+" and bcd_type='ADD' and exists(select 1 from bomsetdetail where bsd_bsid="+bs_id+" and bsd_prodcode=bcd_prodcode and bsd_mothercode=bcd_mothercode and nvl(bsd_usestatus,' ')<>'DISABLE') and rownum<10");
		if (rs.next()) {
			if (rs.getInt("c")>0) {
				BaseUtil.showError("序号[" + rs.getString("bcd_detno") + "]新增的明细在配套表中已经存在!");
			} 
		}  
		//禁用判断 序号+物料编号+母件编号是否一致
		rs = baseDao.queryForRowSet("select count(1)c,wm_concat(bcd_detno) bcd_detno from bomsetchangedetail where bcd_bcid="+bc_id+" and bcd_type='DISABLE' and exists (select 1 from bomsetdetail where bsd_bsid="+bs_id+" and nvl(bsd_usestatus,' ')<>'DISABLE' and bsd_detno=bcd_bsdetno and (bsd_prodcode<>bcd_prodcode OR bsd_mothercode<>bcd_mothercode)) and rownum<10 ");
		if (rs.next()) {
			if (rs.getInt("c")>0) {
				BaseUtil.showError("序号[" + rs.getString("bcd_detno") + "]禁用,配套表序号+物料编号+母件编号与配置表中不一致!");
			} 
		}  	
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller,new Object[]{bc_id});
		//执行提交操作
		baseDao.submit("BOMSetChange", "bc_id="+bc_id, "bc_status", "bc_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "bc_id", bc_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit("BOMSetChange",new Object[]{bc_id});
	}

	@Override
	public void resSubmitBOMSetChange(int bc_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("BOMSetChange", "bc_statuscode", "bc_id=" + bc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit("BOMSetChange", new Object[]{bc_id});
		//执行反提交操作
		baseDao.resOperate("BOMSetChange", "bc_id="+bc_id, "bc_status", "bc_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "bc_id", bc_id);
		handlerService.afterResSubmit("BOMSetChange",new Object[]{bc_id});
	}
	
	private void UpdateChangeTypeCode(int bc_id, String caller){
		baseDao.execute("update bomsetchangedetail set bcd_type=replace(bcd_type,'增加','ADD') where bcd_bcid='" + bc_id + "' and bcd_type='增加'");
		baseDao.execute("update bomsetchangedetail set bcd_type=replace(bcd_type,'禁用','DISABLE') where bcd_bcid='" + bc_id + "' and bcd_type='禁用'");
	}

}
