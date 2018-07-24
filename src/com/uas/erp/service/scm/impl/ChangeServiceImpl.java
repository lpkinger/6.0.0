package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.sql.ast.statement.SQLIfStatement.Else;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.Assert;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.ChangeService;
@Service
public class ChangeServiceImpl implements ChangeService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveChange(String formStore, String gridstore,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gStore = BaseUtil.parseGridStoreToMaps(gridstore);
		handlerService.handler(caller, "save", "before", new Object[]{store, gStore});		
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "changestatus", new String[]{}, new Object[]{});
		baseDao.execute(formSql);	
		//保存Purchasetypedetail
		for(Map<Object, Object> map:gStore){
			map.put("csd_id", baseDao.getSeqId("changestatusdetail_seq"));
		}
		List<String> gridSql=SqlUtil.getInsertSqlbyGridStore(gStore, "changestatusdetail");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "cs_id", store.get("cs_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store, gStore});
	}

	@Override
	public void updateChangeById(String formStore, String gridstore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridstore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("changestatus", "cs_statuscode", "cs_id=" + store.get("cs_id"));
		Assert.isEquals("common.update_onlyEntering", "ENTERING", status);
		handlerService.handler(caller, "save", "before", new Object[]{store, gstore});
		//失效变更单更新前先判断是否变更类型，变更类型则清楚从表所有数据
		if("Change!PLXG".equals(caller)){
			Object cs_class = baseDao.getFieldDataByCondition("changestatus", "cs_class", "cs_id="+store.get("cs_id"));
			if(!cs_class.equals(store.get("cs_class"))){
				String sql = "delete changestatusdetail where csd_csid="+store.get("cs_id");
				baseDao.execute(sql);
			}
		}
		//修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "changestatus", "cs_id");
		baseDao.execute(formSql);
		//修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridstore, "changestatusdetail", "csd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("csd_id") == null || s.get("csd_id").equals("") || s.get("csd_id").toString().equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("changestatusdetail_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "changestatusdetail", new String[]{"csd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "cs_id", store.get("cs_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store, gstore});
	}

	@Override
	public void deleteChange(int cs_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("changestatus", "cs_statuscode", "cs_id=" + cs_id);
		Assert.isEquals("common.delete_onlyEntering", "ENTERING", status);
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{cs_id});
		//删除purchase
		baseDao.deleteById("changestatus", "cs_id", cs_id);
		//删除purchaseDetail
		baseDao.deleteById("changestatusdetail", "csd_csid", cs_id);
		//记录操作
		baseDao.logger.delete(caller, "cs_id", cs_id);
	}

	@Override
	public void auditChange(int cs_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("changestatus", "cs_statuscode", "cs_id=" + cs_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, cs_id);
		checkProduct(cs_id,caller);
		//执行审核操作
		baseDao.audit("changestatus", "cs_id=" + cs_id, "cs_status", "cs_statuscode", "cs_auditdate", "cs_auditman");
		List<String> updatesql=new ArrayList<String>();
		//价格&认定失效申请单审核成功后自动更新核价单状态
		if("Change!PLXG".equals(caller)){
			Object cs_class = baseDao.getFieldDataByCondition("changestatus", "cs_class", "cs_id="+cs_id);
			List<Object[]> objs = baseDao.getFieldsDatasByCondition("changestatusdetail",new String[] { "csd_ordercode","csd_orderdetno","csd_todate","csd_newdate" }, "csd_csid="+cs_id);
			for (Object[] obj : objs) {
				Object pp_id = baseDao.getFieldDataByCondition("PurchasePrice", "pp_id", "pp_code='"+obj[0]+"'");
				if("转无效".equals(cs_class)){
					baseDao.updateByCondition("PurchasePriceDetail", "ppd_unvaliddate=sysdate,ppd_status='无效',ppd_statuscode='UNVALID'", "ppd_id=(select ppd_id from purchaseprice left join purchasepricedetail on pp_id=ppd_ppid where pp_code='"+obj[0]+"' and ppd_detno="+obj[1]+")");
					baseDao.logger.others("失效申请单转无效", "转无效成功！序号：" + obj[1], "PurchasePrice", "pp_id", pp_id);
				} else if("转有效".equals(cs_class)){
					baseDao.updateByCondition("PurchasePriceDetail", "ppd_status='有效',ppd_statuscode='VALID'", "ppd_id=(select ppd_id from purchaseprice left join purchasepricedetail on pp_id=ppd_ppid where pp_code='"+obj[0]+"' and ppd_detno="+obj[1]+")");
					baseDao.logger.others("失效申请单转有效", "转有效成功！序号：" + obj[1], "PurchasePrice", "pp_id", pp_id);
				} else if("转未认定".equals(cs_class)){
					baseDao.updateByCondition("PurchasePriceDetail", "ppd_appstatus='未认定'", "ppd_id=(select ppd_id from purchaseprice left join purchasepricedetail on pp_id=ppd_ppid where pp_code='"+obj[0]+"' and ppd_detno="+obj[1]+")");
					baseDao.logger.others("失效申请单转未认定", "转未认定成功！序号：" + obj[1], "PurchasePrice", "pp_id", pp_id);
				} else if("转合格".equals(cs_class)){
					baseDao.updateByCondition("PurchasePriceDetail", "ppd_appstatus='合格'", "ppd_id=(select ppd_id from purchaseprice left join purchasepricedetail on pp_id=ppd_ppid where pp_code='"+obj[0]+"' and ppd_detno="+obj[1]+")");
					baseDao.logger.others("失效申请单转合格", "转合格成功！序号：" + obj[1], "PurchasePrice", "pp_id", pp_id);
				}
				if(obj[2]!=null&&obj[3]!=null){
					String olddate = obj[2].toString().substring(0,10);
					String newdate = obj[3].toString().substring(0,10);
					if(!olddate.equals(newdate)&&newdate!=null){
						baseDao.updateByCondition("PurchasePriceDetail", "ppd_todate=to_date('"+newdate+"','yyyy-mm-dd')", "ppd_id=(select ppd_id from purchaseprice left join purchasepricedetail on pp_id=ppd_ppid where pp_code='"+obj[0]+"' and ppd_detno="+obj[1]+")");
					}
				}
			}
		}
		if ("Change!WLJY".equals(caller)) {
			String sql="update product set pr_status='已禁用',pr_statuscode='DISABLE' where pr_code in (select csd_prodcode from changestatusdetail where csd_csid="+cs_id+")";
			updatesql.add(sql);
		}else if("Change!WLFJY".equals(caller)){
			String sql="update product set pr_status='已审核',pr_statuscode='AUDITED' where pr_code in (select csd_prodcode from changestatusdetail where csd_csid="+cs_id+")";
			updatesql.add(sql);
		}else if("Change!CGDJ".equals(caller)){
			String sqla="update purchasepricedetail set ppd_status='有效',ppd_statuscode='VALID' where ppd_id in(select csd_orderid from changestatusdetail where csd_csid="+cs_id+" and nvl(csd_d6,' ')='转有效')";
			String sqlb="update purchasepricedetail set ppd_unvaliddate=sysdate,ppd_status='无效',ppd_statuscode='UNVALID' where ppd_id in(select csd_orderid from changestatusdetail where csd_csid="+cs_id+" and nvl(csd_d6,' ')='无效')";
			updatesql.add(sqla);
			updatesql.add(sqlb);
		}else if("Change!PLXG".equals(caller)){
			Object type = baseDao.getFieldDataByCondition("changestatus", "cs_class", "cs_id=" + cs_id);
		    String str="update changestatusdetail set csd_orderid=(select max(ppd_id) from purchaseprice left join purchasepricedetail on pp_id=ppd_ppid where pp_code=csd_ordercode and ppd_detno=csd_orderdetno) where csd_csid="+cs_id;
			baseDao.execute(str);
			if("转未认定".equals(type+"")){
				String sql="update purchasepricedetail set ppd_appstatus='未认定' where ppd_id in(select csd_orderid from changestatusdetail where csd_csid="+cs_id+")";
				updatesql.add(sql);
			}
			if("转无效".equals(type+"")){
				String sql="update purchasepricedetail set ppd_unvaliddate=sysdate,ppd_status='无效',ppd_statuscode='UNVALID',ppd_remark=to_char(sysdate,'yyyy-mm-dd HH24:mi:ss')||'采购价格批量修改状态' where ppd_id in(select csd_orderid from changestatusdetail where csd_csid="+cs_id+")";
				updatesql.add(sql);
			}
		}
		baseDao.execute(updatesql);
		// 执行比例分配表的异动更新
		if (baseDao.isDBSetting("vendorRate")) {
			try {
				baseDao.callProcedure("SP_SetProdVendorRate", new Object[] { "异动更新" });
			} catch (Exception e) {
			}
		}
		//记录操作
		baseDao.logger.audit(caller, "cs_id", cs_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, cs_id);
	}

	@Override
	public void resAuditChange(int cs_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("changestatus", "cs_statuscode", "cs_id=" + cs_id);
		Assert.isEquals("common.resAudit_onlyAudit", "AUDITED", status);
		//执行反审核操作
		baseDao.resOperate("changestatus", "cs_id=" + cs_id, "cs_status", "cs_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "cs_id", cs_id);
	}

	@Override
	public void submitChange(int cs_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("changestatus", "cs_statuscode", "cs_id=" + cs_id);
		Assert.isEquals("common.submit_onlyEntering", "ENTERING", status);
		//执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[]{cs_id});
		checkProduct(cs_id,caller);
		//执行提交操作
		baseDao.submit("changestatus", "cs_id=" + cs_id, "cs_status", "cs_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "cs_id", cs_id);
		//执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[]{cs_id});
	}

	@Override
	public void resSubmitChange(int cs_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("changestatus", "cs_statuscode", "cs_id=" + cs_id);
		Assert.isEquals("common.resSubmit_onlyCommited", "COMMITED", status);
		handlerService.handler(caller, "resCommit", "before", new Object[]{cs_id});
		//执行反提交操作
		baseDao.resOperate("changestatus", "cs_id=" + cs_id, "cs_status", "cs_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "cs_id", cs_id);
		handlerService.handler(caller, "resCommit", "after", new Object[]{cs_id});
	}
	private void checkProduct(int id,String caller){
		//价格认定&失效单检查限制
		if("Change!PLXG".equals(caller)){
			Object cs_class = baseDao.getFieldDataByCondition("changestatus", "cs_class", "cs_id="+id);
			List<Object[]> objs = baseDao.getFieldsDatasByCondition("changestatusdetail",new String[] { "csd_ordercode","csd_orderdetno","csd_detno" }, "csd_csid="+id);
			for (Object[] obj : objs) {
				if("转无效".equals(cs_class)){
					String sql = "select * from purchaseprice left join purchasepricedetail on pp_id=ppd_ppid where pp_code='"+obj[0]+"' and ppd_detno="+obj[1]+" and ppd_status='无效'";
					SqlRowList rs = baseDao.queryForRowSet(sql);
					if(rs.next()){
						BaseUtil.showError("明细行第"+obj[2]+"行定价状态为无效，不允许提交！");
					}
				} else if("转有效".equals(cs_class)){
					String sql = "select * from purchaseprice left join purchasepricedetail on pp_id=ppd_ppid where pp_code='"+obj[0]+"' and ppd_detno="+obj[1]+" and ppd_status='有效'";
					SqlRowList rs = baseDao.queryForRowSet(sql);
					if(rs.next()){
						BaseUtil.showError("明细行第"+obj[2]+"行定价状态为有效，不允许提交！");
					}
				} else if("转未认定".equals(cs_class)){
					String sql = "select * from purchaseprice left join purchasepricedetail on pp_id=ppd_ppid where pp_code='"+obj[0]+"' and ppd_detno="+obj[1]+" and ppd_appstatus='未认定'";
					SqlRowList rs = baseDao.queryForRowSet(sql);
					if(rs.next()){
						BaseUtil.showError("明细行第"+obj[2]+"行认定状态为未认定，不允许提交！");
					}
				} else if("转合格".equals(cs_class)){
					String sql = "select * from purchaseprice left join purchasepricedetail on pp_id=ppd_ppid where pp_code='"+obj[0]+"' and ppd_detno="+obj[1]+" and ppd_appstatus='合格'";
					SqlRowList rs = baseDao.queryForRowSet(sql);
					if(rs.next()){
						BaseUtil.showError("明细行第"+obj[2]+"行认定状态为合格，不允许提交！");
					}
				}
			}
		}
		if("Change!WLJY".equals(caller)){//物料禁用，检查是否存在于已审核BOM
			List<Object[]> objs = baseDao.getFieldsDatasByCondition("changestatusdetail",new String[] { "csd_prodcode","csd_detno" }, "csd_csid="+id);
			StringBuffer sb=new StringBuffer("必须先禁用BOM或者禁用子件才允许禁用物料：").append("<hr>"); 
			for (Object[] obj : objs) {
				String SQLStr = "select count(1) num, wm_concat(bo_id) bo_id from bom,bomdetail where bo_id=bd_bomid and bd_soncode='" 
								+ obj[0].toString()	+ "' and bo_statuscode='AUDITED' and NVL(bd_usestatus,' ')<>'DISABLE' ";
				SqlRowList rs;
				rs = baseDao.queryForRowSet(SQLStr);
				if (rs.next()) {
					if (rs.getInt("num") > 0) {
						sb.append("行号："+obj[1].toString()+"，相关BOM:"+rs.getString("bo_id")).append("<hr>");
					}
				}
			}
			if(sb.length()>26){
				BaseUtil.showError(sb.substring(0,sb.length()-1));
			}
		}
	}
}
