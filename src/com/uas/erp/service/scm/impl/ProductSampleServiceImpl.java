package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.hsqldb.lib.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.ProductSampleService;

@Service
public class ProductSampleServiceImpl implements ProductSampleService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProductSample(String formStore, String gridStore, String caller) {	
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.handler(caller, "save", "before", new Object[]{store, grid});		
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ProductSample", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
	    //保存ProductSampledetail
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "ProductSampledetail", "pd_id");
		baseDao.execute(gridSql);
		baseDao.execute("update productSampledetail set pd_totalmon=pd_price*pd_num where pd_psid="+store.get("ps_id"));
		baseDao.logger.save(caller, "ps_id", store.get("ps_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store, grid});
	}

	@Override
	public void updateProductSampleById(String formStore, String gridStore, String caller) {	
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("ProductSample", "ps_statuscode", "ps_id=" + store.get("ps_id"));
		StateAssert.updateOnlyEntering(status);
		handlerService.handler(caller, "save", "before", new Object[]{store, gstore});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProductSample", "ps_id");
		baseDao.execute(formSql);
		//修改Detail
		List<String> gridSql = SqlUtil.getInsertOrUpdateSql(gstore, "ProductSampledetail", "pd_id");
		baseDao.execute(gridSql);
		baseDao.execute("update productSampledetail set pd_totalmon=pd_price*pd_num where pd_psid="+store.get("ps_id"));
		//baseDao.execute("update productSample set ps_appman=(select sa_appman from sampleapply where sa_code=ps_sacode) where ps_id="+store.get("ps_id"));
		//记录操作
		baseDao.logger.update(caller, "ps_id", store.get("ps_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store, gstore});
	}

	@Override
	public void deleteProductSample(int ps_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("ProductSample", "ps_statuscode", "ps_id=" + ps_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{ps_id});
		Object[] sa = baseDao.getFieldsDataByCondition("ProductSample",new String[]{"ps_oricode","ps_oridetno"}, "ps_id=" + ps_id);
		if(sa != null && sa[0] != null && !"".equals(sa[0].toString())){
			Object said = baseDao.getFieldDataByCondition("Sampleapply", "sa_id", "sa_code='" + sa[0] + "'");
			baseDao.execute("update Sampleapply set sa_isturn=0 where sa_id="+said);
			baseDao.execute("update SampleapplyDetail set sd_turnprostatus=null where sd_said="+said+" and sd_detno="+sa[1]);
		}
		//删除purchase
		baseDao.deleteById("ProductSample", "ps_id", ps_id);
		//删除purchaseDetail
		baseDao.deleteById("ProductSampledetail", "pd_psid", ps_id);
		//记录操作
		baseDao.logger.delete(caller, "ps_id", ps_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{ps_id});
	}

	@Override
	public void auditProductSample(int ps_id, String caller) {		
		Object status = baseDao.getFieldDataByCondition("ProductSample", "ps_statuscode", "ps_id=" + ps_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[]{ps_id});
		//执行审核操作
		baseDao.audit("ProductSample", "ps_id=" + ps_id, "ps_status", "ps_statuscode", "ps_auditdate", "ps_auditor");
		baseDao.execute("update ProductSample set ps_sendstatus='待上传' where ps_id="+ps_id);
		//记录操作
		baseDao.logger.audit(caller, "ps_id", ps_id);
		//执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[]{ps_id});
	}

	@Override
	public void resAuditProductSample(int ps_id, String caller) {	
		Object [] status = baseDao.getFieldsDataByCondition("ProductSample", "ps_statuscode,ps_sendstatus", "ps_id=" + ps_id);		
		StateAssert.resAuditOnlyAudit(status[0]);
		if(status[1]!=null&&status[1].equals("已上传")){
			BaseUtil.showError("已经上传到平台,不允许反审核！");
		}
		handlerService.handler(caller, "resAudit", "before", new Object[]{ps_id});
		//执行反审核操作
		baseDao.resOperate("ProductSample", "ps_id=" + ps_id, "ps_status", "ps_statuscode");
		handlerService.handler(caller, "resAudit", "after", new Object[]{ps_id});
		//记录操作
		baseDao.logger.resAudit(caller, "ps_id", ps_id);
	}

	@Override
	public void submitProductSample(int ps_id, String caller) {	
		//只能对状态为[在录入]的单据进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ProductSample", "ps_statuscode", "ps_id=" + ps_id);
		StateAssert.submitOnlyEntering(status);
		int count=baseDao.getCount("select count(*) from productSampledetail where nvl(pd_vendoruu,' ')=' ' and  pd_psid="+ps_id);
		if(count>0){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.submit_vendoruu"));
		}
		//收费的样品明细单价币别不能为空
		Object isfree = baseDao.getFieldDataByCondition("ProductSample", "ps_isfree", "ps_id=" + ps_id);
		if(isfree!=null&&isfree.equals("是")){
			int checkcurrency=baseDao.getCount("select count(*) from productSampledetail where (nvl(pd_currency,' ')=' ' or nvl(pd_price,0)=0) and pd_psid="+ps_id);
		    if(checkcurrency>0){
		    	BaseUtil.showError("收费的样品明细的币别与单价不能为空!");
		    }
		}
		limitNumber(caller,ps_id);
		//执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[]{ps_id});
		//执行提交操作
		baseDao.submit("ProductSample", "ps_id=" + ps_id, "ps_status", "ps_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "ps_id", ps_id);
		//执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[]{ps_id});
	}

	@Override
	public void resSubmitProductSample(int ps_id, String caller) {
		//只能对状态为[已提交]的单据进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ProductSample", "ps_statuscode", "ps_id=" + ps_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[]{ps_id});
		//执行反提交操作
		baseDao.resOperate("ProductSample", "ps_id=" + ps_id, "ps_status", "ps_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "ps_id", ps_id);
		handlerService.handler(caller, "resCommit", "after", new Object[]{ps_id});
	}
	//如果为收费,则明细数额不能超限额
	private void limitNumber(String caller,int ps_id){
		String limitNumber = baseDao.getDBSetting(caller, "limitNumber");
		if("是".equals(baseDao.getFieldDataByCondition("ProductSample", "ps_isfree", "ps_id="+ps_id)+"")&&!StringUtil.isEmpty(limitNumber)){
			List<Object[]> data=baseDao.getFieldsDatasByCondition("productSampleDetail", new String[]{"pd_detno","pd_num"}, "pd_psid="+ps_id);
			double limit=Double.parseDouble(limitNumber);
			for(Object[] o:data){
				if(limit<Double.parseDouble(o[1].toString())){
					BaseUtil.showError("第"+o[0]+"行,数量超限额了!限额为:"+limit);
				}
			}
		}
	}
	/**
	 * 作废
	 */
	@Override
	public void nullifyProductSample(int id, String caller) {
		// 作废
		baseDao.updateByCondition("ProductSample", "ps_status='" + BaseUtil.getLocalMessage("NULLIFIED") + "', ps_statuscode='NULLIFIED'",
				"ps_id=" + id);
		// 记录操作
		baseDao.logger.others(BaseUtil.getLocalMessage("msg.nullify"), BaseUtil.getLocalMessage("msg.nullifySuccess"), caller, "ps_id",
				id);
	}
}
