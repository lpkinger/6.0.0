package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sun.org.apache.bcel.internal.generic.Select;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.service.scm.ProductLossSetService;

@Service
public class ProductLossSetServiceImpl implements ProductLossSetService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProductLossSet(String formStore, String param,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> prodgrid = BaseUtil.parseGridStoreToMaps(param);
		handlerService.handler(caller, "save", "before", new Object[]{store});		
		List<String> sqls = new ArrayList<String>();
		for (Map<Object, Object> prodmap : prodgrid) {
			int prodid = baseDao.getSeqId("ProductLossSetDet_SEQ");
			prodmap.put("psd_id", prodid);
		}
		sqls.add(SqlUtil.getInsertSqlByMap(store, "ProductLossSet"));
		// 保存从表
		sqls.addAll(SqlUtil.getInsertSqlbyGridStore(prodgrid, "ProductLossSetDet"));
		baseDao.execute(sqls);	
		baseDao.logger.save(caller, "ps_id", store.get("ps_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void updateProductLossSetById(String formStore, String param, String caller) {		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> prodgrid = BaseUtil.parseGridStoreToMaps(param);
		List<String> sqls = new ArrayList<String>();
		handlerService.handler(caller, "save", "before", new Object[]{store});
		sqls.add(SqlUtil.getUpdateSqlByFormStore(store, "ProductLossSet", "ps_id"));
		if (prodgrid.size() > 0) {
			for (Map<Object, Object> s : prodgrid) {
				if (s.get("psd_id") == null || s.get("psd_id").equals("") || s.get("psd_id").equals("0")
						|| Integer.parseInt(s.get("psd_id").toString()) <= 0) {
					int id = baseDao.getSeqId("ProductLossSetDet_SEQ");
					s.put("psd_id", id);
					String sql = SqlUtil.getInsertSqlByMap(s, "ProductLossSetDet", new String[] { "psd_id" }, new Object[] { id });
					sqls.add(sql);
				}
			}
			sqls.addAll(SqlUtil.getInsertOrUpdateSql(prodgrid, "ProductLossSetDet", "psd_id"));
		}
		baseDao.execute(sqls);
		//记录操作
		baseDao.logger.update(caller, "ps_id", store.get("ps_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void deleteProductLossSet(int ps_id, String caller) {		
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{ps_id});
		//删除主表内容
		baseDao.deleteById("ProductLossSet", "ps_id", ps_id);
		baseDao.logger.delete(caller, "ps_id", ps_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{ps_id});
	}

	@Override
	public String auditProductLossSet(int ps_id, String caller) {
		//只能对已提交进行审核操作
		Object status = baseDao.getFieldDataByCondition("ProductLossSet", "ps_statuscode", "ps_id=" + ps_id);
		StateAssert.auditOnlyCommited(status);
		Employee employee = SystemSession.getUser();
		handlerService.beforeAudit("ProductLossSet", ps_id);
		//执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[]{ps_id});
		List<Object[]> os = baseDao.getFieldsDatasByCondition("ProductLossSetDet", new String[]{"psd_plcode","psd_rate","psd_qty","psd_testrate","psd_testqty","psd_osrate","psd_osqty","psd_lapqty","psd_id"}, "psd_psid="+ps_id+" order by psd_detno");
		for(Object[] o : os){
			SqlRowList rs = baseDao.queryForRowSet("select * from ProductLoss where pl_code='"+o[0]+"' and pl_lapqty='"+o[7]+"'");
			if(!rs.hasNext()){
				baseDao.execute("Insert into productloss (PL_ID,PL_CODE,PL_RATE,PL_QTY,PL_TESTRATE,PL_TESTQTY,PL_OSRATE,PL_OSQTY,PL_LAPQTY,PL_INDATE,PL_RECORDMAN) select ProductLoss_SEQ.nextval,psd_plcode,psd_rate,psd_qty,psd_testrate,psd_testqty,psd_osrate,psd_osqty,psd_lapqty,sysdate,'"+employee.getEm_name()+"' from ProductLossSetDet where psd_id="+o[8]);
			}else if(rs.next()){
				baseDao.execute("update ProductLoss a set (pl_rate,pl_qty,pl_testrate,pl_testqty,pl_osrate,pl_osqty,pl_lapqty)=(select psd_rate,psd_qty,psd_testrate,psd_testqty,psd_osrate,psd_osqty,psd_lapqty from ProductLossSetDet where psd_plcode=a.pl_code and psd_id="+o[8]+") where pl_code='"+o[0]+"' and pl_lapqty='"+o[7]+"'");
			}
			baseDao.execute("update product set pr_lossrate='"+o[1]+"',pr_testlossrate='"+o[3]+"',pr_exportlossrate='"+o[5]+"' where pr_code in (select pr_code from product where pr_code like '"+o[0]+"%' and pr_code not in (select pr_code from product A left join productloss on pr_code like pl_code||'%' where pl_code like '"+o[0]+"%' and  pl_code<>'"+o[0]+"'))");
		}
		//执行审核操作
		baseDao.audit("ProductLossSet", "ps_id=" + ps_id, "ps_status", "ps_statuscode", "ps_auditdate", "ps_auditer");
		//记录操作
		baseDao.logger.audit(caller, "ps_id", ps_id);
		//执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[]{ps_id});
		return "物料损耗设置成功!";
	}
	@Override
	public void submitProductLossSet(int ps_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ProductLossSet", "ps_statuscode", "ps_id=" + ps_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { ps_id });
		SqlRowList rs = baseDao.queryForRowSet("select psd_plcode,psd_lapqty from (select psd_plcode,psd_lapqty,count(1)a from ProductLossSetDet where psd_psid="+ps_id+" group by psd_plcode,psd_lapqty) where a>1");
		String log = "";
		while(rs.next()){
			Object detno = baseDao.getFieldDataByCondition("ProductLossSetDet", "wm_concat(psd_detno)detno", "psd_plcode='"+rs.getObject("psd_plcode")+"' and psd_lapqty="+rs.getObject("psd_lapqty")+"");
			log = log + detno +";" ;
		}
		if(log!=""){
			log = log.substring(0,log.length() - 1);
			BaseUtil.showError("明细表存在料号前缀和分段数相同的行，请确认后再提交，序号（"+log+"）");
		}
		// 执行提交操作
		baseDao.submit("ProductLossSet", "ps_id=" + ps_id, "ps_status", "ps_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "ps_id", ps_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { ps_id });
	}
	@Override
	public void resSubmitProductLossSet(int ps_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ProductLossSet", "ps_statuscode", "ps_id=" + ps_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { ps_id });
		// 执行反提交操作
		baseDao.resOperate("ProductLossSet", "ps_id=" + ps_id, "ps_status", "ps_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "ps_id", ps_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { ps_id });
	}
	@Override
	public void resAuditProductLossSet(int ps_id, String caller) {
		// 只能对状态为[已审核]的表单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ProductLossSet", "ps_statuscode", "ps_id=" + ps_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("ProductLossSet", "ps_id=" + ps_id, "ps_status", "ps_statuscode", "ps_auditdate", "ps_auditor");
		baseDao.resOperate("ProductLossSet", "ps_id=" + ps_id, "ps_status", "ps_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "ps_id", ps_id);
	}
	
}
