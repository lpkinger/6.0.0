package com.uas.erp.service.scm.impl;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.PurcVendorRateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PurcVendorRateServiceImpl implements PurcVendorRateService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void savePurcVendorRate(String formStore, String gridStore,String   caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller,new Object[] {store});
		String formSql=SqlUtil.getInsertSqlByFormStore(store, "PurcVendorRate", new String[]{}, new Object[] {});
		baseDao.execute(formSql);	
		for(Map<Object, Object> m:gstore){
			m.put("pvd_id", baseDao.getSeqId("PURCVENDORRATEDET_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gstore, "PurcVendorRateDet");
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "pvr_id", store.get("pvr_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store,gstore});
	}

	@Override
	public void updatePurcVendorRateById(String formStore, String gridStore,String   caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] {store});
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PurcVendorRate", "pvr_id");		
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "PurcVendorRateDet", "pvd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("pvd_id") == null || s.get("pvd_id").equals("") || s.get("pvd_id").toString().equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("PURCVENDORRATEDET_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "PurcVendorRateDet", new String[]{"pvd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "pvr_id", store.get("pvr_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] {store,gstore});
	}

	@Override
	public void deletePurcVendorRate(int pvr_id, String   caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] {pvr_id});
		baseDao.deleteById("PurcVendorRate", "pvr_id", pvr_id);
		baseDao.deleteById("PurcVendorRateDet", "pvd_pvrid", pvr_id);
		// 记录操作
		baseDao.logger.delete(caller, "pvr_id", pvr_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] {pvr_id});

	}

	@Override
	public void auditPurcVendorRate(int pvr_id, String   caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("PurcVendorRate","pvr_statuscode", "pvr_id=" + pvr_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] {pvr_id });
		baseDao.execute("delete productvendorrate where pv_prodcode in(select distinct pvd_prodcode from PurcVendorRateDet where pvd_pvrid="+pvr_id+")");
		List<Object[]> list=baseDao.getFieldsDatasByCondition("PurcVendorRateDet", new String[] {"pvd_prodcode","pvd_vendcode","pvd_rate"}, "pvd_pvrid="+pvr_id);
		List<String> sqls = new ArrayList<String>();
		if (list.size() > 0) {
			for (Object[] o : list) {
				String sql="insert into productvendorrate(pv_id ,pv_prodcode,pv_vendcode,pv_setrate) values( productvendorrate_SEQ.nextval,'"+o[0]+"','"+o[1]+"',"+o[2]+")";
				sqls.add(sql);
			}
		}
		baseDao.execute(sqls);
		// 执行审核操作
		baseDao.audit("PurcVendorRate", "pvr_id=" + pvr_id, "pvr_status", "pvr_statuscode" ,"pvr_auditdate", "pvr_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "pvr_id", pvr_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] {pvr_id});
	}

	@Override
	public void resAuditPurcVendorRate(int pvr_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("PurcVendorRate","pvr_statuscode", "pvr_id=" + pvr_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("PurcVendorRate", "pvr_id=" + pvr_id, "pvr_status", "pvr_statuscode", "pvr_auditdate", "pvr_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "pvr_id", pvr_id);
		handlerService.afterResAudit(caller, new Object[] {pvr_id });
	}

	@Override
	public void submitPurcVendorRate(int pvr_id, String   caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("PurcVendorRate","pvr_statuscode", "pvr_id=" + pvr_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑	
		handlerService.beforeSubmit(caller, new Object[] {pvr_id});
		//判断供应商分配的比例之和是为100%
		StringBuffer sb=new StringBuffer("物料供应商的分配比例之和不为100，物料编号：");
		StringBuffer error=new StringBuffer();
		StringBuffer error1=new StringBuffer("明细行不能有“物料编号”+“供应商编号”都相同的两行或者多:");
		List<Object> obs = baseDao.getFieldDatasByCondition("PurcVendorRateDet", "pvd_prodcode", "pvd_pvrid="+pvr_id+" group by pvd_prodcode having sum(pvd_rate) <> 100");
		for(Object codes:obs){
			sb.append("<hr>").append(codes);
		}
		if(sb.length()>23){
			BaseUtil.showError(sb.toString());
		}
		//验证是否所有供应商都在价格库有有效定价
		List<Object[]> list=baseDao.getFieldsDatasByCondition("PurcVendorRateDet", new String[] {"pvd_prodcode","pvd_vendcode"}, "pvd_pvrid="+pvr_id);
		if (list.size() > 0) {
			for (Object[] o : list) {
				int count=baseDao.getCount("select count(1) from PurchasePriceDetail left join PurchasePrice on ppd_ppid=pp_id  where ppd_vendcode='"+o[1]+"' "
						+ "and ppd_prodcode='"+o[0]+"'  and to_char(ppd_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and "
								+ "to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') and pp_statuscode='AUDITED' AND ppd_statuscode='VALID'");	
				if(count==0){
					error.append("根据 物料编号:[" + o[0] + "],供应商号:[" + o[1] + "] 在物料核价单未找到对应单价，或单价为空值、0等!<BR/>");
				}
			}
		}
		if(error.length()>0){
			BaseUtil.showError(error.toString());
		}
		//明细行不能有“物料编号”+“供应商编号”都相同的两行或者多
		SqlRowList rs=baseDao.queryForRowSet("select count(*)  count,pvd_prodcode,pvd_vendcode,wm_concat(PVD_DETNO) detnos from (select * from PurcVendorRatedet where "
				+ "PVD_PVRID="+pvr_id+"  order by PVD_DETNO) group by PVD_PRODCODE,PVD_VENDCODE");
		while(rs.next()){
			if(rs.getInt("count")>1){
				error1.append("<hr>").append("[物料编号："+rs.getString("pvd_prodcode")+"]+[供应商编号："+rs.getString("pvd_vendcode")+"] 行号："+rs.getString("detnos"));
			}
		}
		if(error1.length()>30){
			BaseUtil.showError(error1.toString());
		}
		// 执行提交操作
		baseDao.submit("PurcVendorRate", "pvr_id=" + pvr_id, "pvr_status", "pvr_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pvr_id", pvr_id);;
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] {pvr_id});
	}

	@Override
	public void resSubmitPurcVendorRate(int pvr_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		handlerService.beforeResSubmit(caller, new Object[] {pvr_id});
		Object status = baseDao.getFieldDataByCondition("PurcVendorRate","pvr_statuscode", "pvr_id=" + pvr_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		baseDao.resOperate("PurcVendorRate", "pvr_id=" + pvr_id, "pvr_status", "pvr_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pvr_id", pvr_id);
		handlerService.afterResSubmit(caller, new Object[] {pvr_id});
	}
}
